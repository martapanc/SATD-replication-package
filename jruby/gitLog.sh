#!/bin/sh

satd=$1

#inputFilename="jrubyComments.txt"
#out="jruby-comments-output.java"
#echo "" > $out

#while IFS= read -r satd 
#do
    
 #   echo "***"$satd
  #  echo "*_*_*" >> $out
   # echo $satd >> $out

    gitlog="$(git log -S"${satd}" --oneline)"
    gitlogNum="$(git log -S"${satd}" --oneline | wc -l)"

    #git log -S"${satd}" --oneline

    if [ $gitlogNum -ge 2 ]
        then
            git log -S"${satd}" --oneline #>> $out
            while read comm
            do
                sha=${comm:0:9}
                #printf  "$sha " >> $out
                git diff-tree -S"${satd}" -r $sha --name-only #--no-commit-id
                filepath="$(git diff-tree -S"${satd}" -r $sha --name-only --no-commit-id)"
            	
#                while read file
 #               do
#                    printf $sha" "$file"\n" >> $out
 #               done <<< "$filepath"

                git diff $sha^ $sha | grep "${satd}" #>> $out
            	#printf "$filepath " >> $out
            	#while read file
            	#do
            	#	echo "Git log -- filepath"
            	#	git log -S"${satd}" --oneline -- $filepath
            	#done <<< "$filepath"
            
            done <<< "$gitlog"
            

        else 
            echo "# shas = " $gitlogNum #>> $out
    fi

#done < "$inputFilename"

printf \\a
