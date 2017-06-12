/224/report.java
Satd-method: public Sampler next()
********************************************
********************************************
/224/After/Bug 60564  5f0651b4_diff.java
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
