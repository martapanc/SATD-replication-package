/78/report.java
Satd-method: public void run()
********************************************
********************************************
/78/Between/Bug 30563  17c919ff8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                Sampler sam;
-                while (running && (sam = controller.next()) != null) {
-                    process_sampler(sam, null, threadContext);
+                Sampler firstSampler = controller.next();
+                Sampler sam = firstSampler;
+                while (running && sam != null) {
+                    if (onErrorStartNextLoop) { // if the threadGroup option is to start next loop when it fails
+
+                        if (sam.equals(firstSampler)) { // if it's the start of an iteration
+                            threadContext.getVariables().put(LAST_SAMPLE_OK, "true");
+                        }
+                        if (threadContext.getVariables().get(LAST_SAMPLE_OK) == "true") {
+                            process_sampler(sam, null, threadContext);
+                            sam = controller.next();
+                        } else {
+                            while (!sam.equals(firstSampler)) { // while the thread is NOT on the begining of the tree
+                                sam = controller.next();
+                            }
+                        }
+                    } else {
+                        process_sampler(sam, null, threadContext);
+                        sam = controller.next();
+                    }

Lines added: 20. Lines removed: 3. Tot = 23
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 34739  51329310a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {
+        threadGroup.decrNumberOfThreads();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public void setThreadName(String threadName)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum)

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 38391  0415393d9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 41140  be00b0cb0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 41913  cf1c0dc65_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-						SamplePackage pack = compiler.configureSampler(sam);
-
-						// Hack: save the package for any transaction
-						// controllers
-						threadContext.getVariables().putObject(PACKAGE_OBJECT, pack);
-
-						delay(pack.getTimers());
-						Sampler sampler = pack.getSampler();
-						sampler.setThreadContext(threadContext);
-						sampler.setThreadName(threadName);
-						TestBeanHelper.prepare(sampler);
-						SampleResult result = sampler.sample(null); 
-                        // TODO: remove this useless Entry parameter
-						if (result != null) {
-							result.setThreadName(threadName);
-							threadContext.setPreviousResult(result);
-							runPostProcessors(pack.getPostProcessors());
-							checkAssertions(pack.getAssertions(), result);
-							notifyListeners(pack.getSampleListeners(), result);
-							compiler.done(pack);
-							if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)) {
-								stopThread();
-							}
-							if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)) {
-								stopTest();
-							}
-						} else {
-                            compiler.done(pack); // Finish up
+                        // Check if we are running a transaction
+                        TransactionSampler transactionSampler = null;
+                        if(sam instanceof TransactionSampler) {
+                            transactionSampler = (TransactionSampler) sam;
+                        }
+                        // Find the package for the transaction
+                        SamplePackage transactionPack = null;
+                        if(transactionSampler != null) {
+                            transactionPack = compiler.configureTransactionSampler(transactionSampler);
+                            
+                            // Check if the transaction is done
+                            if(transactionSampler.isTransactionDone()) {
+                                // Get the transaction sample result
+                                SampleResult transactionResult = transactionSampler.getTransactionResult();
+
+                                // Check assertions for the transaction sample
+                                transactionResult.setThreadName(threadName);
+                                checkAssertions(transactionPack.getAssertions(), transactionResult);
+                                // Notify listeners with the transaction sample result
+                                notifyListeners(transactionPack.getSampleListeners(), transactionResult);
+                                compiler.done(transactionPack);
+                                // Transaction is done, we do not have a sampler to sample
+                                sam = null;
+                            }
+                            else {
+                                // It is the sub sampler of the transaction that will be sampled
+                                sam = transactionSampler.getSubSampler();
+                            }
+                        }
+                        
+                        // Check if we have a sampler to sample
+                        if(sam != null) {
+                            // Get the sampler ready to sample
+                            SamplePackage pack = compiler.configureSampler(sam);
+
+                            delay(pack.getTimers());
+                            Sampler sampler = pack.getSampler();
+                            sampler.setThreadContext(threadContext);
+                            sampler.setThreadName(threadName);
+                            TestBeanHelper.prepare(sampler);
+                        
+                            // Perform the actual sample
+                            SampleResult result = sampler.sample(null); 
+                            // TODO: remove this useless Entry parameter
+                        
+                            // If we got any results, then perform processing on the result
+                            if (result != null) {
+                                result.setThreadName(threadName);
+                                threadContext.setPreviousResult(result);
+                                runPostProcessors(pack.getPostProcessors());
+                                checkAssertions(pack.getAssertions(), result);
+                                // Do not send subsamples to listeners which receive the transaction sample
+                                List sampleListeners = getSampleListeners(pack, transactionPack, transactionSampler);
+                                notifyListeners(sampleListeners, result);
+                                compiler.done(pack);
+                                // Add the result as subsample of transaction if we are in a transaction
+                                if(transactionSampler != null) {
+                                    transactionSampler.addSubSamplerResult(result);
+                                }
+
+                                // Check if thread or test should be stopped
+                                if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)) {
+                                    stopThread();
+                                }
+                                if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)) {
+                                    stopTest();
+                                }
+                            } else {
+                                compiler.done(pack); // Finish up
+                            }
-

Lines added: 70. Lines removed: 29. Tot = 99
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    private List getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {
+    private List getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {
+        List sampleListeners = samplePack.getSampleListeners();
+        // Do not send subsamples to listeners which receive the transaction sample
+        if(transactionSampler != null) {
+            ArrayList onlySubSamplerListeners = new ArrayList();
+            List transListeners = transactionPack.getSampleListeners();
+            for(Iterator i = sampleListeners.iterator(); i.hasNext();) {
+                SampleListener listener = (SampleListener)i.next();
+                // Check if this instance is present in transaction listener list
+                boolean found = false;
+                for(Iterator j = transListeners.iterator(); j.hasNext();) {
+                    // Check for the same instance
+                    if(j.next() == listener) {
+                        found = true;
+                        break;
+                    }
+                }
+                if(!found) {
+                    onlySubSamplerListeners.add(listener);
+                }
+            }
+            sampleListeners = onlySubSamplerListeners;
+        }
+        return sampleListeners;
+    }

Lines added: 25. Lines removed: 0. Tot = 25
********************************************
********************************************
/78/Between/Bug 43430  9a3d4075a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                            	result.setGroupThreads(threadGroup.getNumberOfThreads());
+                            	result.setAllThreads(JMeterContextService.getNumberOfThreads());

Lines added: 2. Lines removed: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private List getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 45839  ec46abc7a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                	if(onErrorStartNextLoop) {
-                		boolean lastSampleFailed = !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK));
-                		if(lastSampleFailed) {
-	                		if(log.isDebugEnabled()) {
-	                    		log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
-	                    	}
-	                    	// Find parent controllers of current sampler
-	                        FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
-	                        testTree.traverse(pathToRootTraverser);
-	                        List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
-	
-	                        // Trigger end of loop condition on all parent controllers of current sampler
-	                        for (Iterator<Controller> iterator = controllersToReinit
-	                                .iterator(); iterator.hasNext();) {
-	                            Controller parentController =  iterator.next();
-	                            if(parentController instanceof ThreadGroup) {
-	                                ThreadGroup tg = (ThreadGroup) parentController;
-	                                tg.startNextLoop();
-	                            } else {
-	                                parentController.triggerEndOfLoop();
-	                            }
-	                        }
-	                        sam = null;
-	                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
-                		} else {
-                			sam = controller.next();
-                		}
+                	if(onErrorStartNextLoop || threadContext.isRestartNextLoop()) {
+                	    if(threadContext.isRestartNextLoop()) {
+                            triggerEndOfLoopOnParentControllers(sam);
+                            sam = null;
+                            threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                            threadContext.setRestartNextLoop(false);
+                	    } else {
+                    		boolean lastSampleFailed = !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK));
+                    		if(lastSampleFailed) {
+    	                		if(log.isDebugEnabled()) {
+    	                    		log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+    	                    	}
+    	                    	triggerEndOfLoopOnParentControllers(sam);
+    	                        sam = null;
+    	                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                    		} else {
+                    			sam = controller.next();
+                    		}
+                	    }

Lines added: 19. Lines removed: 27. Tot = 46
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 45903  f3bca638d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private List getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 47921  b56c8c975_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        LoopIterationListener iterationListener=null;
+
-            initRun(threadContext);
+            iterationListener = initRun(threadContext);
-            threadFinished();
+            threadFinished(iterationListener);

Lines added: 4. Lines removed: 2. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	-    private void threadFinished() {
-    private void threadFinished() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 48749  ca8e0c22b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 51880  708a7949f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 51880  dfdf1dbc8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 51888  77babfc75_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            currentSampler = null; // prevent any further interrupts
+            interruptLock.lock();  // make sure current interrupt is finished

Lines added: 2. Lines removed: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 52330  0a63e84b2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    if (onErrorStartNextLoop) { // if the threadGroup option is to start next loop when it fails
-                        // special case: TC as parent and last subsampler is not Ok
-                        if (!TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)) && sam instanceof TransactionSampler) {
-                            TransactionSampler ts = (TransactionSampler) sam;
-                            while (!ts.isTransactionDone()) { // go to last subsampler
-                                sam = controller.next();
-                                ts = (TransactionSampler) sam;
-                            }
-                            // process now for close transaction (not sampling)
-                            process_sampler(sam, null, threadContext);
-                        }
-                        
-                        // normal case: process sampler and get next
-                        if (TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK))) {
-                            process_sampler(sam, null, threadContext);
-                            sam = controller.next();
-                        } else {
-                            // Find parent controllers of current sampler
-                            FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
-                            testTree.traverse(pathToRootTraverser);
-                            List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
-
-                            // Trigger end of loop condition on all parent controllers of current sampler
-                            for (Iterator<Controller> iterator = controllersToReinit
-                                    .iterator(); iterator.hasNext();) {
-                                Controller parentController =  iterator.next();
-                                if(parentController instanceof ThreadGroup) {
-                                    ThreadGroup tg = (ThreadGroup) parentController;
-                                    tg.startNextLoop();
-                                } else {
-                                    parentController.triggerEndOfLoop();
-                                }
-                            }
-                            sam = null;
-                            threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
-                        }
-                    } else {
-                        process_sampler(sam, null, threadContext);
-                        sam = controller.next();
-                    }
+                	process_sampler(sam, null, threadContext);
+                	if(onErrorStartNextLoop) {
+                		boolean lastSampleFailed = !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK));
+                		if(lastSampleFailed) {
+	                		if(log.isDebugEnabled()) {
+	                    		log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+	                    	}
+	                    	// Find parent controllers of current sampler
+	                        FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
+	                        testTree.traverse(pathToRootTraverser);
+	                        List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
+	
+	                        // Trigger end of loop condition on all parent controllers of current sampler
+	                        for (Iterator<Controller> iterator = controllersToReinit
+	                                .iterator(); iterator.hasNext();) {
+	                            Controller parentController =  iterator.next();
+	                            if(parentController instanceof ThreadGroup) {
+	                                ThreadGroup tg = (ThreadGroup) parentController;
+	                                tg.startNextLoop();
+	                            } else {
+	                                parentController.triggerEndOfLoop();
+	                            }
+	                        }
+	                        sam = null;
+	                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                		} else {
+                			sam = controller.next();
+                		}
+                	} 
+                	else {
+                		sam = controller.next();
+                	}

Lines added: 32. Lines removed: 40. Tot = 72
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 52968  03ea5d70c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                            triggerEndOfLoopOnParentControllers(sam);
+                            triggerEndOfLoopOnParentControllers(sam, threadContext);
-    	                    	triggerEndOfLoopOnParentControllers(sam);
+    	                    	triggerEndOfLoopOnParentControllers(sam, threadContext);

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/bug 52968  373a03821_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 53418  95d97c944_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 54204  faa9e3ca0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 54267  e417a04bf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 54268  32f301947_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                	threadContext.cleanAfterSample();

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 57193: e6b1b0acc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 58726  6cb0db932_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 58728  cbdd5614d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 58736  fd62770a0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 59067  6c9d00ae1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 59133  28c3e8d2e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            Sampler sam = threadGroupLoopController.next();
-            while (running && sam != null) {
-                processSampler(sam, null, threadContext);
-                threadContext.cleanAfterSample();
-                
-                // restart of the next loop 
-                // - was request through threadContext
-                // - or the last sample failed AND the onErrorStartNextLoop option is enabled
-                if(threadContext.isRestartNextLoop()
-                        || (onErrorStartNextLoop
-                                && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
-                {
+            while (running) {
+                Sampler sam = threadGroupLoopController.next();
+                while (running && sam != null) {
+                    processSampler(sam, null, threadContext);
+                    threadContext.cleanAfterSample();
-                    if(log.isDebugEnabled()) {
-                        if(onErrorStartNextLoop
-                                && !threadContext.isRestartNextLoop()) {
-                            log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+                    // restart of the next loop 
+                    // - was requested through threadContext
+                    // - or the last sample failed AND the onErrorStartNextLoop option is enabled
+                    if(threadContext.isRestartNextLoop()
+                            || (onErrorStartNextLoop
+                                    && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
+                    {
+                        if(log.isDebugEnabled()) {
+                            if(onErrorStartNextLoop
+                                    && !threadContext.isRestartNextLoop()) {
+                                log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+                            }
+                        
+                        triggerEndOfLoopOnParentControllers(sam, threadContext);
+                        sam = null;
+                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                        threadContext.setRestartNextLoop(false);
+                    }
+                    else {
+                        sam = threadGroupLoopController.next();
-                    
-                    triggerEndOfLoopOnParentControllers(sam, threadContext);
-                    threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
-                    threadContext.setRestartNextLoop(false);
-                    sam = null;
-                if (sam == null && threadGroupLoopController.isDone()) {
+                if (threadGroupLoopController.isDone()) {
-                else {
-                    sam = threadGroupLoopController.next();
-                }

Lines added: 26. Lines removed: 25. Tot = 51
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 59882  5f87f3092_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 60018  01618c3e6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 60049  3fd2896a6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 60049  b32997c38_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Bug 60050  482e1edb1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            log.info("Stop Thread seen: " + e.toString());
+            log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Fix to Sta c64a5b2bc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                            // Last not ok. start get the begining of the tree
-                            sam = controller.next(); // need perfom a until loop for special case (tc as parent)
-                            while (sam != null && !sam.equals(firstSampler)) { // while the thread is NOT on the begining of the tree
-                                sam = controller.next();
+                            // Find parent controllers of current sampler
+                            FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
+                            testTree.traverse(pathToRootTraverser);
+                            List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
+
+                            // Trigger end of loop condition on all parent controllers of current sampler
+                            for (Iterator<Controller> iterator = controllersToReinit
+                                    .iterator(); iterator.hasNext();) {
+                                Controller parentController =  iterator.next();
+                                if(parentController instanceof ThreadGroup) {
+                                    ThreadGroup tg = (ThreadGroup) parentController;
+                                    tg.startNextLoop();
+                                } else {
+                                    parentController.triggerEndOfLoop();
+                                }
-                            // At this point: begining tree, thus Last must Ok
+                            sam = null;

Lines added: 16. Lines removed: 5. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Since add  65a69f812_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-
-                        if (sam.equals(firstSampler)) { // if it's the start of an iteration
-                            threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                        // special case: TC as parent and last subsampler is not Ok
+                        if (!TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)) && sam instanceof TransactionSampler) {
+                            TransactionSampler ts = (TransactionSampler) sam;
+                            while (!ts.isTransactionDone()) { // go to last subsampler
+                                sam = controller.next();
+                                ts = (TransactionSampler) sam;
+                            }
+                            // process now for close transaction (not sampling)
+                            process_sampler(sam, null, threadContext);
+                        
+                        // normal case: process sampler and get next
-                            while (!sam.equals(firstSampler)) { // while the thread is NOT on the begining of the tree
+                            // Last not ok. start get the begining of the tree
+                            sam = controller.next(); // need perfom a until loop for special case (tc as parent)
+                            while (sam != null && !sam.equals(firstSampler)) { // while the thread is NOT on the begining of the tree
+                            // At this point: begining tree, thus Last must Ok
+                            threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);

Lines added: 16. Lines removed: 4. Tot = 20
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/78/Between/Sonar : Fi 7d8faded6_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        if(log.isDebugEnabled()) {
-                            if(onErrorStartNextLoop
-                                    && !threadContext.isRestartNextLoop()) {
+                        if(log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
-                            }
-        catch (JMeterStopTestException e) {
-            log.info("Stopping Test: " + e.toString());
+        catch (JMeterStopTestException e) { // NOSONAR
+            log.info("Stopping Test: " + e.toString()); 
-        catch (JMeterStopTestNowException e) {
+        catch (JMeterStopTestNowException e) { // NOSONAR
-        } catch (JMeterStopThreadException e) {
+        } catch (JMeterStopThreadException e) { // NOSONAR
-        } catch (Exception e) {
+        } catch (Exception | JMeterError e) {
-        } catch (Error e) {// Make sure errors are output to the log file
-            log.error("Test failed!", e);

Lines added: 6. Lines removed: 11. Tot = 17
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getListeners
* next
* getTimers
* prepare
* getName
* getVariables
* setSamplingStarted
* getAssertions
* error
* getPostProcessors
* threadFinished
* addIterationListener
* isStopTest
* isSuccessful
* info
* setCurrentSampler
* currentThread
* setVariables
* getSampler
* clear
* configureSampler
* sample
* done
* isDone
* traverse
* setPreviousResult
* setThreadName
* setThreadNum
* initialize
* isStopThread
* putObject
* getContext
* getSampleListeners
—————————
Method found in diff:	private void threadFinished(LoopIterationListener iterationListener) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadNum(int threadNum) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
