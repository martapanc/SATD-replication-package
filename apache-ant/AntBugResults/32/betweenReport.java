32/report.java
Satd-method: public void execute() throws BuildException {
********************************************
********************************************
32/Between/microoptimizati 5f20b9914_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        for (int i = 0; i < filesToDo.size(); i++) {
+        final int size = filesToDo.size();
+        for (int i = 0; i < size; i++) {

Lines added: 2. Lines removed: 1. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getParent
* setClasspath
* getProperty
* getIncludedFiles
* getAbsolutePath
* replace
* setFork
* replaceString
* executeJava
* setTaskName
* exit
* scanDir
* setArgs
* getPath
* setClassname
* getDirectoryScanner
* createTask
* clearArgs
* isDirectory
* append
—————————
Method found in diff:	public void setClasspath(Path classpath) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String replaceString(String inpString, String escapeChars,

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void scanDir(String[] files) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
32/Between/Next try for PR 6650efb10_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            jspFile = new File((String) filesToDo.elementAt(i));
+            String filename = (String) filesToDo.elementAt(i);
+            jspFile = new File(filename);
-            args[j + 2] =  sourceDirectory + File.separator 
-                + (String) filesToDo.elementAt(i);
-            arg = "";
+            args[j + 2] =  sourceDirectory + File.separator + filename;
+            helperTask.clearArgs();
-            for (int x = 0; x < 12; x++) {
-                arg += " " + args[x];
+            for (int x = 0; x < j + 3; x++) {
+                helperTask.createArg().setValue(args[x]);
-            System.out.println("arg = " + arg);
-            
-            helperTask.clearArgs();
-            helperTask.setArgs(arg);
-                log(files[i] + " failed to compile", Project.MSG_WARN);
+                log(filename + " failed to compile", Project.MSG_WARN);

Lines added: 7. Lines removed: 11. Tot = 18
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getParent
* setClasspath
* getProperty
* getIncludedFiles
* getAbsolutePath
* replace
* setFork
* replaceString
* executeJava
* setTaskName
* exit
* scanDir
* setArgs
* getPath
* setClassname
* getDirectoryScanner
* createTask
* clearArgs
* isDirectory
* append
—————————
Method found in diff:	public void setClasspath(Path classpath) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String replaceString(String inpString, String escapeChars,

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void scanDir(String files[]) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
32/Between/remove authors  c885f5683_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getParent
* setClasspath
* getProperty
* getIncludedFiles
* getAbsolutePath
* replace
* setFork
* replaceString
* executeJava
* setTaskName
* exit
* scanDir
* setArgs
* getPath
* setClassname
* getDirectoryScanner
* createTask
* clearArgs
* isDirectory
* append
—————————
Method found in diff:	public void setClasspath(Path classpath) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String replaceString(String inpString, String escapeChars,

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void scanDir(String files[]) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
