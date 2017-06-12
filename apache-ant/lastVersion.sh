#!/bin/sh

inputFilename="AntSATD.java"

while IFS= read -r line 
do
	IFS=',' read -r -a array <<< "$line"

  #if [ ${array[0]} -eq 75 ] #un-comment this if to test script only for one SATD, by specifying the SATD id
  #then

    #Read input file line by line: each line is structured as "[0]id,[1]file_path,[2]comment,[3]introd_sha,[4]fixing_sha"
    #Each line is split into array, with ',' as separator
        id="${array[0]}"
        sha1="${array[3]:0:9}"
        sha2="${array[4]:0:9}"
        filepath="${array[2]}"
        comment="${array[1]}"
        echo $comment
        echo $sha1 $sha2
       
        output=AntBugResults/$id/last_version.java
        git show $sha2:$filepath > $output

        #java -jar MethodFromCommentFinal.jar $firstVersionOutput "${comment}" >> $output

  #fi
  
done < "$inputFilename"

printf \\a  #sound notification
