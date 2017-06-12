9/report.java
Satd-method: public void execute() throws BuildException {
********************************************
********************************************
9/After/A bug in SQLExe 9a672c032_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Allow ant to co 352396620_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Allow more cont f97926a10_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Allow Result Se 740ed5fbf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(
-            if (!statement.execute(sql)) {
+            ret = statement.execute(sql);

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/allow SQLWarnin eaffd9d71_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Allow subclasse 72e471f0b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            conn = getConnection();
-            if (!isValidRdbms(conn)) {
-                return;
-            }
-            try {
-                statement = conn.createStatement();
-                statement.setEscapeProcessing(escapeProcessing);
+            try {
-                            conn.commit();
+                            getConnection().commit();
-                    if (statement != null) {
-                        statement.close();
+                    if (getStatement() != null) {
+                        getStatement().close();
-                    if (conn != null) {
-                        conn.close();
+                    if (getConnection() != null) {
+                        getConnection().close();

Lines added: 6. Lines removed: 12. Tot = 18
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(
-            ret = statement.execute(sql);
+            ret = getStatement().execute(sql);

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Avoid calling g be45954b9_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/errorproperty a cfb2e0108_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                setErrorProperty();
+                setErrorProperty();

Lines added: 2. Lines removed: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Fix for SQLExec 2a1a857ad_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Fix performance f358c00ad_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/fix resultset u 543148ca1_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Log all stateme f82a8c58d_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out) 

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Minor updates b 0df2b1de3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    (Resource[]) nonFileResources.toArray(new Resource[0]);
+                    (Resource[]) nonFileResources.toArray(new Resource[nonFileResources.size()]);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private AddAsisRemove newInstance() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected synchronized Class loadClass(String classname, boolean resolve)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/most likely fix 2f46b6af9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                PrintStream out = System.out;
+                PrintStream out =
+                    new PrintStream(new KeepAliveOutputStream(System.out));

Lines added: 2. Lines removed: 1. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/new showWarning 1a8500873_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/option to prese 2270580b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            if (srcFile == null && sqlCommand.length() == 0
-                && filesets.isEmpty()) {
+            if (srcFile == null && sqlCommand.length() == 0 
+                && filesets.isEmpty()) { 
-                                             + "must be set!", getLocation());
+                                             + "must be set!", location);
-
+        
-                throw new BuildException("Source file does not exist!", getLocation());
+                throw new BuildException("Source file does not exist!", location);
-                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
-                File srcDir = fs.getDir(getProject());
-
+                DirectoryScanner ds = fs.getDirectoryScanner(project);
+                File srcDir = fs.getDir(project);
+                
-
+                
-
+            
-
+            
-                        log("Opening PrintStream to output file " + output,
+                        log("Opening PrintStream to output file " + output, 
-
+                    
-                    for (Enumeration e = transactions.elements();
+                    for (Enumeration e = transactions.elements(); 
-
+                       
-                }
+                } 
-                throw new BuildException(e, getLocation());
+                throw new BuildException(e, location);
-                throw new BuildException(e, getLocation());
+                throw new BuildException(e, location);
-
-            log(goodSql + " of " + totalSql +
+            
+            log(goodSql + " of " + totalSql + 

Lines added: 20. Lines removed: 20. Tot = 40
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-        private void runTransaction(PrintStream out)
-        private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
9/After/outputencoding  7dbdd6081_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                        if (outputEncoding != null) {
+                            out = new PrintStream(new BufferedOutputStream(os),
+                                                  false, outputEncoding);
+                        } else {
+                        }

Lines added: 5. Lines removed: 0. Tot = 5
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/Remove direct c 3396e7c32_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasMoreElements() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object nextElement()

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/rowcountpropert 4e4a35935_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/sql's onerror=' 02b3a7d13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                throw new BuildException(e, getLocation());
+                if (onError.equals("abort")) {
+                    throw new BuildException(e, getLocation());
+                }
-                throw new BuildException(e, getLocation());
+                if (onError.equals("abort")) {
+                    throw new BuildException(e, getLocation());
+                }

Lines added: 6. Lines removed: 2. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
9/After/work on PR 2645 e1f227ae3_diff.java
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
* setSrc
* rollback
* getIncludedFiles
* forName
* commit
* newInstance
* createStatement
* hasMoreElements
* runTransaction
* loadClass
* setAutoCommit
* elements
* getDirectoryScanner
* connect
* getDir
* nextElement
—————————
Method found in diff:	public void setSrc(File srcFile) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void runTransaction(PrintStream out)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Connection connect(String url, Properties info)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
