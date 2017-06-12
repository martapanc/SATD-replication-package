#!/bin/sh

#Script for automatic retrieval of Bug data related to SATD

inputFilename="JRuby-satd-list.csv"

while IFS= read -r line 
do
	IFS=';' read -r -a array <<< "$line"

  #if [ ${array[0]} -eq 75 ] #un-comment this if to test script only for one SATD, by specifying the SATD id
  #then

    #Read input file line by line: each line is structured as "[0]id,[1]comment,[2]file_path,[3]introd_sha,[4]fixing_sha"
    #Each line is split into array, with ';' as separator
        id="${array[0]}"
        sha1="${array[3]:0:8}"
        sha2="${array[4]:0:8}"
        filepath="${array[2]}"
        comment="${array[1]:1:300}"
        commentCut="${array[1]:10:70}"
        echo $id $comment
        echo $sha1 $sha2


    #Save Change File (Diff) of Satd-introduction and -fixing commits
        output1=JRubyBugResults/$id/Satd-Introd-Diff.java
        git diff -U1000 $sha1^ $sha1 -- $filepath > $output1
     
        output2=JRubyBugResults/$id/Satd-Fix-Diff.java
        git diff -U1000 $sha2^ $sha2 -- $filepath > $output2
        
        
  #fi
  
done < "$inputFilename"

printf \\a  #sound notification
