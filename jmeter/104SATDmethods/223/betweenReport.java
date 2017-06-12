/223/report.java
Satd-method: public Sampler next()
********************************************
********************************************
/223/Between/Bug 41913  cf1c0dc6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        // Check if transaction is done
+        if(transactionSampler != null && transactionSampler.isTransactionDone()) {
+            log.debug("End of transaction");
+            // This transaction is done
+            transactionSampler = null;
+            return null;
+        }
+        
+        // Check if it is the start of a new transaction
-			log_debug("+++++++++++++++++++++++++++++");
-			calls = 0;
-            noFailingSamples = 0;
-			res = new SampleResult();
-            res.setSampleLabel(getName());
-            // Assume success
-            res.setSuccessful(true);
-			res.sampleStart();
+		    log.debug("Start of transaction");
+		    transactionSampler = new TransactionSampler(this, getName());
-        Sampler returnValue = super.next();
-        
-		if (returnValue == null) // Must be the end of the controller
-		{
-			log_debug("-----------------------------" + calls);
-			if (res == null) {
-				log_debug("already called");
-			} else {
-				res.sampleEnd();
-                res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
-                if(res.isSuccessful()) {
-                    res.setResponseCodeOK();
-                }
-
-				// TODO could these be done earlier (or just once?)
-                JMeterContext threadContext = getThreadContext();
-                JMeterVariables threadVars = threadContext.getVariables();
-
-				SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
-				if (pack == null) {
-					log.warn("Could not fetch SamplePackage");
-				} else {
-                    SampleEvent event = new SampleEvent(res, getName());
-                    // We must set res to null now, before sending the event for the transaction,
-                    // so that we can ignore that event in our sampleOccured method 
-                    res = null;
-					lnf.notifyListeners(event, pack.getSampleListeners());
-				}
-			}
-		}
-        else {
-            // We have sampled one of our children
-            calls++;            
+        // Sample the children of the transaction
+		Sampler subSampler = super.next();
+        transactionSampler.setSubSampler(subSampler);
+        // If we do not get any sub samplers, the transaction is done
+        if (subSampler == null) {
+            transactionSampler.setTransactionDone();
-
-		return returnValue;
+        return transactionSampler;

Lines added: 18. Lines removed: 43. Tot = 61
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
next(
-        Sampler returnValue = super.next();
+		Sampler subSampler = super.next();

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
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
