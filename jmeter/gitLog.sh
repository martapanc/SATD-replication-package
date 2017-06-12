#!/bin/sh

satd=$1

echo $satd

gitlog="$(git log -S"${satd}" --oneline)"
gitlogNum="$(git log -S"${satd}" --oneline | wc -l)"

echo $gitlog

if [ $gitlogNum -ge 2 ]
    then
        while read comm
        do
            sha=${comm:0:8}
            echo ""
            echo  $sha
            git diff-tree -S"${satd}" -r $sha --name-only --no-commit-id
        	filepath="$(git diff $sha^ $sha | grep "${satd}")"
        	git diff $sha^ $sha | grep "${satd}"
        	
        	#while read file
        	#do
        	#	echo "Git log -- filepath"
        	#	git log -S"${satd}" --oneline -- $filepath
        	#done <<< "$filepath"
        
        done <<< "$gitlog"

    else 
        echo "# shas = " $gitlogNum
fi
