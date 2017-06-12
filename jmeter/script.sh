#!/bin/sh

#Script for automatic retrieval of Bug data related to SATD

inputFilename="maldonados200.java"

while IFS= read -r line 
do
	IFS=',' read -r -a array <<< "$line"

    id="${array[0]}"
    filepath="${array[1]}"
    comment="${array[2]}"
    sha1="${array[3]:0:8}" #if shas have 9 chars, script returns "bad revision"... 
    sha2="${array[4]:0:8}"

    echo $comment

    between="$(git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]')"
    betweenNum="$(git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]' | wc -l)"

    output1=BugResults200/$id/bugsBetween.txt
	git log $sha1..$sha2 --oneline -- $filepath | grep -i 'Bug [0-9]' > $output1

	while IFS= read -r bug
    do
        bugSha="${bug:0:8}"
        bugId="${bug:10:10}"
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
        bugSha="${bug:0:8}"
        bugId="${bug:10:10}"
        echo $bugSha
        mkdir -p BugResults200/$id/After
        #no file path to have complete diff for the commit
        git diff -U1000 $bugSha^ $bugSha > "BugResults200/$id/After/${bugId} ${bugSha}_diff.java"
    done < "$output2" 

    after="$(git log $sha2.. --oneline -- $filepath | grep -i 'Bug [0-9]')"
    afterNum="$(git log $sha2.. --oneline -- $filepath | grep -i 'Bug [0-9]' | wc -l)"
    
done < "$inputFilename"

printf \\a
