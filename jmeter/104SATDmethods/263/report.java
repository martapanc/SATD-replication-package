File path: src/core/org/apache/jmeter/gui/action/Save.java
Comment: NOTUSED private String chosenFile
Initial commit id: 76159a5b
Final commit id: 9deff4ce
   Bugs between [       0]:

   Bugs after [      16]:
c6a82dfad Bug 60564 - Migrating LogKit to SLF4J - core/gui package (2/2) Contributed by Woonsan Ko This closes #272 Bugzilla Id: 60564
eba38b513 Bug 58699 : Workbench changes neither saved nor prompted for saving upon close #resolve #45 Bugzilla Id: 58699
75ab5a73c Bug 57913 - Automated backups of last saved JMX files Only backup file, if it exists (otherwise we get an exception on first save of a file).
46eb9d63c Bug 57913 - Automated backups of last saved JMX files Bugzilla Id: 57913
ceaf2b7cc Bug 57061 - Save as Test Fragment fails to clone deeply selected node Bugzilla Id: 57061
ea6580c13 Bug 55693 - Add a "Save as Test Fragment" option Bugzilla Id: 55693
493384439 Bug 55693 - Add a "Save as Test Fragment" option Bugzilla Id: 55693
93d0af854 Bug 42428 - Workbench not saved with Test Plan Bugzilla Id: 42428
7c6697267 Bug 52997 - Jmeter should not exit without saving Test Plan if saving before exit fails Fix crash with larger trees; only update saved copy of tree if we wrote the whole tree
7ccb69835 Bug 52997 - Jmeter should not exit without saving Test Plan if saving before exit fails
3a2f36ba6 Bug 52997 - Jmeter should not exit without saving Test Plan if saving before exit fails
3e87a3dfb Bug 52097 - Save As should point to same folder that was used to open a file if MRU list is used
45a133a6b Bug 43283 - Save action should add extension .jmx if not present
ecb301a26 Bug 42346 (patch 20143) - Objects that doesn´t save changes
e861ae37d Bug 36755 (patch 20073) - consistent closing of file streams
6f1c66ae7 Bug 36755 - Save XML test files with UTF-8 encoding

Start block index: 58
End block index: 58
	// NOTUSED private String chosenFile;
