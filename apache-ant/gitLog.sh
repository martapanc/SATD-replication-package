#!/bin/sh

satd=$1

inputFilename="ant-comments.txt"
out="output.txt"
echo "" > $out

#while IFS= read -r satd 
#do
    
    echo $satd

    gitlog="$(git log -S"${satd}" --oneline)"
    gitlogNum="$(git log -S"${satd}" --oneline | wc -l)"

    git log -S"${satd}" --oneline

    if [ $gitlogNum -ge 2 ]
        then
            printf "\n$satd," >> $out
            while read comm
            do
                sha=${comm:0:8}
                printf  "$sha," >> $out
                git diff-tree -S"${satd}" -r $sha --name-only 
                filepath="$(git diff-tree -S"${satd}" -r $sha --name-only --no-commit-id)"
            	git diff $sha^ $sha | grep "${satd}"
            	printf "$filepath " >> $out
            	#while read file
            	#do
            	#	echo "Git log -- filepath"
            	#	git log -S"${satd}" --oneline -- $filepath
            	#done <<< "$filepath"
            
            done <<< "$gitlog"
            

        else 
            echo "# shas = " $gitlogNum
    fi

#done < "$inputFilename"

printf \\a
