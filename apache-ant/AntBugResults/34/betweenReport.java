34/report.java
Satd-method: public void execute() throws BuildException {
********************************************
********************************************
34/Between/-breakiterator  44564adf2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        boolean javadoc5 = javadoc4 &&
+            !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4);
-            if (breakiterator && doclet == null) {
+            if (breakiterator && (doclet == null || javadoc5)) {

Lines added: 3. Lines removed: 1. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	public String getHref() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getPackagelistLoc() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/Add a new attri a9831bdca_diff.java
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
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	public String getHref() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getText() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Path getPath() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getPackagelistLoc() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private String replace(String str, char fromChar, String toString) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Enumeration getParams() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/Add attributes  f1a903928_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        doDocFilesSubDirs(toExecute); // docfilessubdir attribute

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	public String getHref() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getText() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Path getPath() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getPackagelistLoc() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Enumeration getParams() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setPath(Path path) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setValue(String value) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/javadoc fails i 5f81fd801_diff.java
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
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    private String replace(String str, char fromChar, String toString) {
-    private String replace(String str, char fromChar, String toString) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/microoptimizati 5f20b9914_diff.java
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
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	public String getHref() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {
-            for (int i = 0; i < packages.size(); i++) {
+            final int size = packages.size();
+            for (int i = 0; i < size; i++) {

Lines added: 2. Lines removed: 1. Tot = 3
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getPackagelistLoc() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
34/Between/post-process ge 7bc745a28_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            postProcessGeneratedJavadocs();

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getHref
* getText
* addExisting
* setAntRun
* getDirectoryScanner
* getCommandline
* concatSystemBootClasspath
* isJavaVersion
* deleteOnExit
* concatSystemClasspath
* getJdkExecutable
* getName
* logFlush
* getFile
* hasMoreElements
* describeCommand
* createTempFile
* setWorkingDirectory
* getIncludedFiles
* setExecutable
* getFileURL
* setCommandline
* shouldResolveLink
* getDir
* readLine
* getValue
* clone
* getPackages
* append
* isLinkOffline
* getPath
* getParameter
* getPackagelistLoc
* toExternalForm
* nextToken
* hasMoreTokens
* elements
* replace
* getTitle
* indexOf
* getParams
* delete
* substring
* resolveFile
* createArgument
* getAbsolutePath
* setPath
* setValue
* nextElement
—————————
Method found in diff:	public String getHref() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getText() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void logFlush() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setExecutable(String executable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean shouldResolveLink() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPackages() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLinkOffline() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Path getPath() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getParameter() throws BuildException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public File getPackagelistLoc() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTitle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setPath(Path path) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setValue(String value) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
