#!/bin/sh

inputFilename="hibernate-satd-w-fix-2fp.java"

while IFS= read -r line 
do
	IFS=';' read -r -a array <<< "$line"

  #if [ ${array[0]} -eq 75 ] #un-comment this if to test script only for one SATD, by specifying the SATD id
  #then

    #Read input file line by line: each line is structured as "[0]id,[1]file_path,[2]comment,[3]introd_sha,[4]fixing_sha"
    #Each line is split into array, with ',' as separator
        id="${array[0]}"
        sha1="${array[3]:0:8}"
        sha2="${array[5]:0:8}"
        fp1="${array[2]}"
        fp2="${array[4]}"
        comment="${array[1]:1:300}"
        commentCut="${array[1]:10:70}"
        echo $comment
        echo $sha1 $sha2

        #mkdir -p HibBugResults/$id

    #Save Change File (Diff) of Satd-introduction and -fixing commits
        output1=HibBugResults/$id/Satd-Introd-Diff.java
        git diff -U1000 $sha1^ $sha1 -- $fp1 > $output1
     
        output2=HibBugResults/$id/Satd-Fix-Diff.java
        git diff -U1000 $sha2^ $sha2 -- $fp2 > $output2

  #fi
  
done < "$inputFilename"

printf \\a  #sound notification
