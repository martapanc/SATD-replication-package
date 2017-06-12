/58/report.java
Satd-method: public class ResultSaver
********************************************
********************************************
/58/After/Bug 60564  11d942f4a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ResultSaver.class);
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
-        if(log.isDebugEnabled()) {
-            log.debug("Saving " + s.getSampleLabel() + " in " + fileName);
+        if (log.isDebugEnabled()) {
+            log.debug("Saving {} in {}", s.getSampleLabel(), fileName);
-        } catch (FileNotFoundException e1) {
-            log.error("Error creating sample file for " + s.getSampleLabel(), e1);
-        } catch (IOException e1) {
-            log.error("Error saving sample " + s.getSampleLabel(), e1);
+        } catch (FileNotFoundException e) {
+            log.error("Error creating sample file for {}", s.getSampleLabel(), e);
+        } catch (IOException e) {
+            log.error("Error saving sample {}", s.getSampleLabel(), e);

Lines added: 8. Lines removed: 8. Tot = 16
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
+    private static final Logger log = LoggerFactory.getLogger(ResultSaver.class);

Lines added containing method: 2. Lines removed containing method: 1. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* println
* write
* close
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	public void close() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void clear() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/58/After/Bug 60859  285abc026_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
ResultSaver
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
-public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener {
+public class ResultSaver extends AbstractTestElement implements NoThreadClone, Serializable, SampleListener, TestStateListener {
+++ b/test/src/org/apache/jmeter/reporters/TestResultSaver.java
+ * Test for {@link ResultSaver}
+public class TestResultSaver extends JMeterTestCase {
+    private ResultSaver resultSaver;
+        resultSaver = new ResultSaver();
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.setProperty(ResultSaver.ERRORS_ONLY, "true");
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.setProperty(ResultSaver.ERRORS_ONLY, "true");
+        resultSaver.setProperty(ResultSaver.FILENAME, "test");

Lines added containing method: 17. Lines removed containing method: 2. Tot = 19
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* error
* substring
* getSubResults
* getSampleLabel
* println
* write
* close
* indexOf
* currentThread
* debug
* clear
* getLoggerForClass
* getResponseData
* getResult
* getContentType
—————————
Method found in diff:	-    public void clear() {
-    public void clear() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
