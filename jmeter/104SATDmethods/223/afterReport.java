/223/report.java
Satd-method: public Sampler next()
********************************************
********************************************
/223/After/Bug 41418  12b53ca4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 42778  858ce038_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            log.debug("End of transaction");
+        	if (log.isDebugEnabled()) {
+                log.debug("End of transaction " + getName());
+        	}
-		    log.debug("Start of transaction");
+        	if (log.isDebugEnabled()) {
+		        log.debug("Start of transaction " + getName());
+        	}

Lines added: 6. Lines removed: 2. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(
+		Sampler sampler = controller.next();
+			// because if we call this.next(), it will return the TransactionSampler, and we do not want that.
+			returnValue = super.next();

Lines added containing method: 3. Lines removed containing method: 0. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 47385  54e1cef5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 47909  34c29868_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 50032  df78199c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	public JMeterVariables getVariables() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 50134  0c9eab39_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 51876  6572ccd2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	public void setThreadName(String inthreadName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 51876  6d25bd5a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 52265  1710a70a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 52296  2c316251_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 52296  3143c47e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 52296  f27a8aac_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
********************************************
********************************************
/223/After/Bug 52968  03ea5d70_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	+    protected void notifyListeners() {
+    protected void notifyListeners() {
+        // TODO could these be done earlier (or just once?)
+        JMeterContext threadContext = getThreadContext();
+        JMeterVariables threadVars = threadContext.getVariables();
+        SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
+        if (pack == null) {
+            // If child of TransactionController is a ThroughputController and TPC does
+            // not sample its children, then we will have this
+            // TODO Should this be at warn level ?
+            log.warn("Could not fetch SamplePackage");
+        } else {
+            SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
+            // We must set res to null now, before sending the event for the transaction,
+            // so that we can ignore that event in our sampleOccured method
+            res = null;
+            // bug 50032 
+            if (!getThreadContext().isReinitializingSubControllers()) {
+                lnf.notifyListeners(event, pack.getSampleListeners());
+            }
+        }
+    }

Lines added: 21. Lines removed: 0. Tot = 21
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 53039  caaf9e66_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	public void sampleStart() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSampleLabel(String label) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void sampleEnd() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setResponseCode(String code) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSuccessful(boolean success) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setResponseMessage(String msg) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 55816  a6696aa5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 56160  aa77e7b8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(
-            sampler = controller.next();
+        Sampler sampler = controller.next();
+                while ((sampler = controller.next()) != null) {
+                Sampler sampler = controller.next();
+                sampler = controller.next();

Lines added containing method: 4. Lines removed containing method: 1. Tot = 5
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	public JMeterVariables getVariables() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void notifyListeners() {
-            // bug 50032 
-            if (!getThreadContext().isReinitializingSubControllers()) {
-                lnf.notifyListeners(event, pack.getSampleListeners());
-            }
+            lnf.notifyListeners(event, pack.getSampleListeners());

Lines added: 1. Lines removed: 4. Tot = 5
********************************************
********************************************
/223/After/Bug 56811  07d60f60_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 57193: 65bd9c28_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 58122  1058659d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 59067  6c9d00ae_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 60229  bac01a62_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	public void sampleStart() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSampleLabel(String label) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void sampleEnd() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setResponseCode(String code) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSuccessful(boolean success) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setResponseMessage(String msg) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/223/After/Bug 60564  5f0651b4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        if (log.isDebugEnabled()) {
-            log.debug("Calling next on: " + this.getClass().getName());
-        }
+        log.debug("Calling next on: {}", GenericController.class);

Lines added: 1. Lines removed: 3. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* sampleStart
* setSampleLabel
* getObject
* getVariables
* sampleEnd
* notifyListeners
* setResponseCode
* warn
* setSuccessful
* setResponseMessage
* setThreadName
* getContext
* getSampleListeners
—————————
Method found in diff:	protected void notifyListeners() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
