64/report.java
Satd-method: public void execute() throws BuildException {
********************************************
********************************************
64/After/microoptimizati 5f20b9914_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            for (int i = 0; i < children.size(); i++) {
+            final int size = children.size();
+            for (int i = 0; i < size; i++) {

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
Method found in diff:	public String getParent() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setClasspath(Path s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getProperty() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAbsolutePath() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private int replace() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setFork(boolean s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String replaceString(String inpString, String escapeChars,

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int executeJava() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void exit(int exitCode) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void scanDir(File srcDir, File destDir, String[] files) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setArgs(String s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPath() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setClassname(String s) throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void clearArgs() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDirectory() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
