#!/bin/sh

#Script for automatic retrieval of Bug data related to SATD

inputFilename="hib-2.java"
regex='HHH-[0-9][0-9][0-9][0-9]'

while IFS= read -r line 
do
	IFS=';' read -r -a array <<< "$line"

  #if [ ${array[0]} -eq 75 ] #un-comment this if to test script only for one SATD, by specifying the SATD id
  #then

    #Read input file line by line: each line is structured as "[0]id,[1]comment,[2]file_path,[3]introd_sha,[4]fixing_sha"
    #Each line is split into array, with ';' as separator
        id="${array[0]}"
        sha1="${array[3]:0:8}"
        sha2="${array[5]:0:8}"
        fp1="${array[2]}"
        fp2="${array[4]}"
        comment="${array[1]:3:300}"
        commentCut="${array[1]:10:70}"
        echo $comment
        echo $sha1 $sha2

        mkdir -p HibBugResults/$id
    #Create folder "Between" containing diff files of the Bug Reports 
    #that were found between SATD-introd and SATD-fix
        output1=HibBugResults/$id/bugsBetween.txt
        git log $sha1..$sha2 --oneline -- $fp1 | grep -i $regex > $output1
        git log $sha1..$sha2 --oneline -- $fp2 | grep -i $regex > $output1
        while IFS= read -r bug
        do
            bugSha="${bug:0:8}"
            bugId="${bug:10:10}"
            #echo $bugSha
            mkdir -p "HibBugResults/$id/Between"
            git diff -U1000 $bugSha^ $bugSha > "HibBugResults/$id/Between/${bugId} ${bugSha}_diff.java"
        done < "$output1"  
        echo " "
    #Create folder "After" containing diff files of the Bug Reports 
    #that were found after SATD-fix
        output2=HibBugResults/$id/bugsAfter.txt
        git log $sha2.. --oneline -- $fp2 | grep -i $regex > $output2
        while IFS= read -r bug
        do
            bugSha="${bug:0:8}"
            bugId="${bug:10:10}"
            #echo $bugSha
            mkdir -p HibBugResults/$id/After
            #no file path, in order to have complete diff for the commit
            git diff -U1000 $bugSha^ $bugSha > "HibBugResults/$id/After/${bugId} ${bugSha}_diff.java"
        done < "$output2" 
  
    #Search in git history between the first (SATD introduced) and second (SATD removed) sha in the Java class containing 
    #the SATD, and search for commits that refer to bugs - their message looks like "Bug 12345"
        between="$(git log $sha1..$sha2 --oneline -- $fp1 | grep -i $regex)"
        betweenNum="$(git log $sha1..$sha2 --oneline -- $fp1 | grep -i $regex | wc -l)"
    #Search between the second sha (SATD removed) until the most recent existing commit for the same Class
        after="$(git log $sha2.. --oneline -- $fp2 | grep -i $regex)"
        afterNum="$(git log $sha2.. --oneline -- $fp2 | grep -i $regex | wc -l)"
  
    #Print result to file  
        
        output=HibBugResults/$id/report.java #java is just to have colors in the Sublime editor :)
        echo "File path: $fp1" > $output
        echo "Comment: $comment" >> $output
        echo "Initial commit id: $sha1" >> $output
        echo "Final commit id: $sha2" >> $output
        echo "   Bugs between [$betweenNum]:" >> $output
        echo "$between" >> $output
        echo ""
        echo "   Bugs after [$afterNum]:" >> $output
        echo "$after" >> $output 
        echo "" >> $output
       
        firstVersionOutput=HibBugResults/$id/Satd-Introd-Diff.java
        git show $sha1:$fp1 > $firstVersionOutput

        java -jar MethodFromCommentFinal.jar $firstVersionOutput "${comment}" >> $output

        satdDiffOutput=HibBugResults/$id/Satd-Fix-Diff.java
        git show $sha2:$fp2 > $satdDiffOutput

  #fi
  
done < "$inputFilename"

printf \\a  #sound notification
