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
        echo $id $comment
        echo $sha1 $sha2

        firstVersionOutput=HibBugResults/$id/Satd-Introd-Diff.java
        git show $sha1:$fp1 > $firstVersionOutput

    
            satdDiffOutput=HibBugResults/$id/Satd-Fix-Diff.java
            git show $sha2:$fp2 > $satdDiffOutput
        

  #fi
  
done < "$inputFilename"

printf \\a  #sound notification
