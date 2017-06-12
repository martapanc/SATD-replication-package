58/report.java
Satd-method: public class ResultSaver
********************************************
********************************************
58/Between/Bug 36755  e861ae37d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			try {
-				if (pw != null)
-					pw.close();
-			} catch (IOException e) {
-			}
+            JOrphanUtils.closeQuietly(pw);

Lines added: 1. Lines removed: 5. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
58/Between/Bug 41944  289264650_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		SampleResult s = e.getResult();
+      processSample(e.getResult());
+   }
+
+   /**
+    * Recurse the whole (sub)result hierarchy.
+    *
+    * @param s Sample result
+    */
+   private void processSample(SampleResult s) {
-			saveSample(sr[i]);
+			processSample(sr[i]);

Lines added: 10. Lines removed: 2. Tot = 12
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
58/Between/Bug 43119  d81ad7e22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+    public static final String SKIP_AUTO_NUMBER = "FileSaver.skipautonumber"; // $NON-NLS-1$
+
-        String fileName = makeFileName(s.getContentType());
+        String fileName = makeFileName(s.getContentType(), getSkipAutoNumber());
-    private String makeFileName(String contentType) {
+    private String makeFileName(String contentType, boolean skipAutoNumber) {
-            int i = contentType.indexOf("/");
+            int i = contentType.indexOf("/"); // $NON-NLS-1$
-                int j = contentType.indexOf(";");
+                int j = contentType.indexOf(";"); // $NON-NLS-1$
-        return getFilename() + nextNumber() + "." + suffix;
+        if (skipAutoNumber) {
+            return getFilename() + "." + suffix; // $NON-NLS-1$
+        }
+        else {
+            return getFilename() + nextNumber() + "." + suffix; // $NON-NLS-1$
+        }
+    private boolean getSkipAutoNumber() {
+        return getPropertyAsBoolean(SKIP_AUTO_NUMBER);
+    }
+

Lines added: 16. Lines removed: 5. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
58/Between/Bug 44575  59671c56f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	private static synchronized long nextNumber() {
+    public static final String SUCCESS_ONLY = "FileSaver.successonly"; // $NON-NLS-1$
+
+    private static synchronized long nextNumber() {
-		// Should we save successful samples?
-		if (s.isSuccessful() && getErrorsOnly())
-			return;
+		// Should we save the sample?
+		if (s.isSuccessful()){
+		    if (getErrorsOnly()){
+		        return;
+		    }
+		} else {
+		    if (getSuccessOnly()){
+		        return;
+		    }
+		}
+    private boolean getSuccessOnly() {
+        return getPropertyAsBoolean(SUCCESS_ONLY);
+    }
+

Lines added: 17. Lines removed: 4. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
58/Between/Bug 49365  9cca78bc0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        StrBuilder sb = new StrBuilder(getFilename());
+        StrBuilder sb = new StrBuilder(FileServer.resolveBaseRelativeName(getFilename()));

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
58/Between/Bug 52214  3e16150b7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+    private static final Object LOCK = new Object();
+
-    //@GuardedBy("this")
+    //@GuardedBy("LOCK")
+    //@GuardedBy("LOCK")
+    private static String timeStamp;
+
+    private static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmm_"; // $NON-NLS-1$
+
+    //@GuardedBy("LOCK")
+    private static int numberPadLength;
+
+    //+ JMX property names; do not change
+
+    public static final String ADD_TIMESTAMP = "FileSaver.addTimstamp"; // $NON-NLS-1$
+
+    public static final String NUMBER_PAD_LENGTH = "FileSaver.numberPadLen"; // $NON-NLS-1$
+
+    //- JMX property names
+
-        super.clear();
-        synchronized(this){
+        synchronized(LOCK){
+            if (getAddTimeStamp()) {
+                DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
+                timeStamp = format.format(new Date());
+            } else {
+                timeStamp = "";
+            }
+            numberPadLength=getNumberPadLen();
+        super.clear();
+        sb.append(timeStamp); // may be the empty string
-            sb.append(nextNumber());
+            String number = Long.toString(nextNumber());
+            for(int i=number.length(); i < numberPadLength; i++) {
+                sb.append('0');
+            }
+            sb.append(number);
+    private boolean getAddTimeStamp() {
+        return getPropertyAsBoolean(ADD_TIMESTAMP);
+    }
+
+    private int getNumberPadLen() {
+        return getPropertyAsInt(NUMBER_PAD_LENGTH, 0);
+    }
+

Lines added: 42. Lines removed: 4. Tot = 46
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* write
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void clear() {
-        super.clear();
-        synchronized(this){
+        synchronized(LOCK){
+            if (getAddTimeStamp()) {
+                DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
+                timeStamp = format.format(new Date());
+            } else {
+                timeStamp = "";
+            }
+            numberPadLength=getNumberPadLen();
+        super.clear();

Lines added: 9. Lines removed: 2. Tot = 11
********************************************
********************************************
