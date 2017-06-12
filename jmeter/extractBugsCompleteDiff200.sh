#!/bin/sh

#Script for automatic retrieval of Bug data related to SATD

inputFilename="maldonados200.java"

while IFS= read -r line 
do
	IFS=',' read -r -a array <<< "$line"

  #if [ ${array[0]} -eq 33 ]
  #then
  #Read input file line by line: each line is structured as "[0]id,[1]file_path,[2]comment,[3]introd_sha,[4]fixing_sha"
  #Each line is split into array, with ',' as separator
    id="${array[0]}"
    sha1="${array[3]}"
    sha2="${array[4]}"
    filepath="${array[1]:0:8}"
    comment="${array[2]:0:8}"
    commentCut="${array[2]:10:70}"

    echo $comment $filepath $sha1 
    echo " "
    #echo $sha1
    #Search in git history between the first (SATD introduced) and second (SATD removed) sha in the Java class containing the SATD, 
  #and search for commits that refer to bugs - their message looks like "Bug 12345"
    between="$(git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]')"
    betweenNum="$(git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]' | wc -l)"
 
    #Collect shas of the bugs between the introd and the fix commit
    output1=BugResults200/$id/bugsBetween.txt
    $(git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]' > $output1)
    while IFS= read -r bug
    do
        bugSha=${bug:0:8}
        bugId=${bug:10:10}
        echo $bugSha
        mkdir -p "BugResults200/$id/Between"
        git diff -U1000 $bugSha^ $bugSha -- $filepath > "BugResults200/$id/Between/${bugId} ${bugSha}_diff.java"
    done < "$output1"  
    echo " "

    #Collect shas of the bugs after the fix commit
    output2=BugResults200/$id/bugsAfter.txt
    git log $sha2.. --oneline -- $filepath | grep -i 'Bug [0-9]' > $output2
    while IFS= read -r bug
    do
        bugSha=${bug:0:8}
        bugId=${bug:10:10}
        echo $bugSha
        mkdir -p BugResults200/$id/After
        #no file path to have complete diff for the commit
        git diff -U1000 $bugSha^ $bugSha > "BugResults200/$id/After/${bugId} ${bugSha}_diff.java"
    done < "$output2" 

    after="$(git log $sha2.. --oneline -- $filepath | grep -i 'Bug [0-9]')"
    afterNum="$(git log $sha2.. --oneline -- $filepath | grep -i 'Bug [0-9]' | wc -l)"

    IFS=$'\n' read -r -a aftArray <<< "$after"
    #echo $after
   #printf '%s\n' "${aftArray[@]}"


    #git diff -U1000 8cf39ed85^ 8cf39ed85 -- src/components/org/apache/jmeter/assertions/HTMLAssertion.java > HTMLAssertion_bug.java

    #firstVersionOutput=BugResultsCompleteDiff/$id/first_version.java
    #git show $sha1:$filepath > $firstVersionOutput

    #java -jar MethodFromComment2.jar $firstVersionOutput "${comment}" >> $output

  #fi

    
done < "$inputFilename"

printf \\a
