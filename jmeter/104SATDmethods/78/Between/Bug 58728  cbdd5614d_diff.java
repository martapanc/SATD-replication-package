diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index 6db23befb..7e9650db1 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,966 +1,940 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
-import java.util.ListIterator;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.TransactionSampler;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.SearchByClass;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.jorphan.util.JMeterStopTestNowException;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.log.Logger;
 
 /**
  * The JMeter interface to the sampling process, allowing JMeter to see the
  * timing, add listeners for sampling events and to stop the sampling process.
  *
  */
 public class JMeterThread implements Runnable, Interruptible {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final boolean IMPLEMENTS_SAMPLE_STARTED = JMeterUtils.getPropDefault("temp.sampleStarted", true);
     static {
         log.info("IMPLEMENTS_SAMPLE_STARTED="+IMPLEMENTS_SAMPLE_STARTED);
     }
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     private final Controller controller;
 
     private final HashTree testTree;
 
     private final TestCompiler compiler;
 
     private final JMeterThreadMonitor monitor;
 
     private final JMeterVariables threadVars;
 
     // Note: this is only used to implement TestIterationListener#testIterationStart
     // Since this is a frequent event, it makes sense to create the list once rather than scanning each time
     // The memory used will be released when the thread finishes
     private final Collection<TestIterationListener> testIterationStartListeners;
 
     private final ListenerNotifier notifier;
 
     /*
      * The following variables are set by StandardJMeterEngine.
      * This is done before start() is called, so the values will be published to the thread safely
      * TODO - consider passing them to the constructor, so that they can be made final
      * (to avoid adding lots of parameters, perhaps have a parameter wrapper object.
      */
     private String threadName;
 
     private int initialDelay = 0;
 
     private int threadNum = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private boolean scheduler = false;
     // based on this scheduler is enabled or disabled
 
     // Gives access to parent thread threadGroup
     private AbstractThreadGroup threadGroup;
 
     private StandardJMeterEngine engine = null; // For access to stop methods.
 
     /*
      * The following variables may be set/read from multiple threads.
      */
     private volatile boolean running; // may be set from a different thread
 
     private volatile boolean onErrorStopTest;
 
     private volatile boolean onErrorStopTestNow;
 
     private volatile boolean onErrorStopThread;
 
     private volatile boolean onErrorStartNextLoop;
 
     private volatile Sampler currentSampler;
 
     private final ReentrantLock interruptLock = new ReentrantLock(); // ensure that interrupt cannot overlap with shutdown
 
     public JMeterThread(HashTree test, JMeterThreadMonitor monitor, ListenerNotifier note) {
         this.monitor = monitor;
         threadVars = new JMeterVariables();
         testTree = test;
         compiler = new TestCompiler(testTree);
         controller = (Controller) testTree.getArray()[0];
         SearchByClass<TestIterationListener> threadListenerSearcher = new SearchByClass<>(TestIterationListener.class); // TL - IS
         test.traverse(threadListenerSearcher);
         testIterationStartListeners = threadListenerSearcher.getSearchResults();
         notifier = note;
         running = true;
     }
 
     public void setInitialContext(JMeterContext context) {
         threadVars.putAll(context.getVariables());
     }
 
     /**
      * Enable the scheduler for this JMeterThread.
      *
      * @param sche
      *            flag whether the scheduler should be enabled
      */
     public void setScheduled(boolean sche) {
         this.scheduler = sche;
     }
 
     /**
      * Set the StartTime for this Thread.
      *
      * @param stime the StartTime value.
      */
     public void setStartTime(long stime) {
         startTime = stime;
     }
 
     /**
      * Get the start time value.
      *
      * @return the start time value.
      */
     public long getStartTime() {
         return startTime;
     }
 
     /**
      * Set the EndTime for this Thread.
      *
      * @param etime
      *            the EndTime value.
      */
     public void setEndTime(long etime) {
         endTime = etime;
     }
 
     /**
      * Get the end time value.
      *
      * @return the end time value.
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * Check if the scheduled time is completed.
      */
     private void stopSchedulerIfNeeded() {
         long now = System.currentTimeMillis();
         long delay = now - endTime;
         if ((delay >= 0)) {
             running = false;
             log.info("Stopping because end time detected by thread: " + threadName);
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = (startTime - System.currentTimeMillis());
         delayBy(delay, "startScheduler");
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
-    /*
-     * See below for reason for this change. Just in case this causes problems,
-     * allow the change to be backed out
-     */
-    private static final boolean reversePostProcessors =
-        JMeterUtils.getPropDefault("jmeterthread.reversePostProcessors", false); // $NON-NLS-1$
-
-    static {
-        if (reversePostProcessors) {
-            log.info("Running PostProcessors in reverse order");
-        } else {
-            log.info("Running PostProcessors in forward order");
-        }
-    }
-
     @Override
     public void run() {
         // threadContext is not thread-safe, so keep within thread
         JMeterContext threadContext = JMeterContextService.getContext();
         LoopIterationListener iterationListener = null;
 
         try {
             iterationListener = initRun(threadContext);
             Sampler sam = controller.next();
             while (running && sam != null) {
                 processSampler(sam, null, threadContext);
                 threadContext.cleanAfterSample();
                 
                 // restart of the next loop 
                 // - was request through threadContext
                 // - or the last sample failed AND the onErrorStartNextLoop option is enabled
                 if(threadContext.isRestartNextLoop()
                         || (onErrorStartNextLoop
                                 && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
                 {
                     
                     if(log.isDebugEnabled()) {
                         if(onErrorStartNextLoop
                                 && !threadContext.isRestartNextLoop()) {
                             log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                         }
                     }
                     
                     triggerEndOfLoopOnParentControllers(sam, threadContext);
                     threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                     threadContext.setRestartNextLoop(false);
                     sam = null;
                 }
                 
                 if (sam == null && controller.isDone()) {
                     running = false;
                     log.info("Thread is done: " + threadName);
                 }
                 else {
                     sam = controller.next();
                 }
             }
         }
         // Might be found by contoller.next()
         catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         }
         catch (JMeterStopTestNowException e) {
             log.info("Stopping Test Now: " + e.toString());
             stopTestNow();
         } catch (JMeterStopThreadException e) {
             log.info("Stop Thread seen: " + e.toString());
         } catch (Exception e) {
             log.error("Test failed!", e);
         } catch (ThreadDeath e) {
             throw e; // Must not ignore this one
         } catch (Error e) {// Make sure errors are output to the log file
             log.error("Test failed!", e);
         } finally {
             currentSampler = null; // prevent any further interrupts
             try {
                 interruptLock.lock();  // make sure current interrupt is finished, prevent another starting yet
                 threadContext.clear();
                 log.info("Thread finished: " + threadName);
                 threadFinished(iterationListener);
                 monitor.threadFinished(this); // Tell the monitor we are done
                 JMeterContextService.removeContext(); // Remove the ThreadLocal entry
             }
             finally {
                 interruptLock.unlock(); // Allow any pending interrupt to complete (OK because currentSampler == null)
             }
         }
     }
 
     /**
      * Trigger end of loop on parent controllers up to Thread Group
      * @param sam Sampler Base sampler
      * @param threadContext 
      */
     private void triggerEndOfLoopOnParentControllers(Sampler sam, JMeterContext threadContext) {
         // Find parent controllers of current sampler
         FindTestElementsUpToRootTraverser pathToRootTraverser = null;
         TransactionSampler transactionSampler = null;
         if(sam instanceof TransactionSampler) {
             transactionSampler = (TransactionSampler) sam;
             pathToRootTraverser = new FindTestElementsUpToRootTraverser(transactionSampler.getTransactionController());
         } else {
             pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
         }
         testTree.traverse(pathToRootTraverser);
         
         // Trigger end of loop condition on all parent controllers of current sampler
         List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
         for (Controller parentController : controllersToReinit) {
             if(parentController instanceof AbstractThreadGroup) {
                 AbstractThreadGroup tg = (AbstractThreadGroup) parentController;
                 tg.startNextLoop();
             } else {
                 parentController.triggerEndOfLoop();
             }
         }
         
         // bug 52968
         // When using Start Next Loop option combined to TransactionController.
         // if an error occurs in a Sample (child of TransactionController) 
         // then we still need to report the Transaction in error (and create the sample result)
         if(transactionSampler != null) {
             SamplePackage transactionPack = compiler.configureTransactionSampler(transactionSampler);
             doEndTransactionSampler(transactionSampler, null, transactionPack, threadContext);
         }
     }
 
     /**
      * Process the current sampler, handling transaction samplers.
      *
      * @param current sampler
      * @param parent sampler
      * @param threadContext
      * @return SampleResult if a transaction was processed
      */
     
     private SampleResult processSampler(Sampler current, Sampler parent, JMeterContext threadContext) {
         SampleResult transactionResult = null;
         try {
             // Check if we are running a transaction
             TransactionSampler transactionSampler = null;
             if(current instanceof TransactionSampler) {
                 transactionSampler = (TransactionSampler) current;
             }
             // Find the package for the transaction
             SamplePackage transactionPack = null;
             if(transactionSampler != null) {
                 transactionPack = compiler.configureTransactionSampler(transactionSampler);
 
                 // Check if the transaction is done
                 if(transactionSampler.isTransactionDone()) {
                     transactionResult = doEndTransactionSampler(transactionSampler, 
                             parent, 
                             transactionPack,
                             threadContext);
                     // Transaction is done, we do not have a sampler to sample
                     current = null;
                 }
                 else {
                     Sampler prev = current;
                     // It is the sub sampler of the transaction that will be sampled
                     current = transactionSampler.getSubSampler();
                     if (current instanceof TransactionSampler) {
                         SampleResult res = processSampler(current, prev, threadContext);// recursive call
                         threadContext.setCurrentSampler(prev);
                         current = null;
                         if (res != null) {
                             transactionSampler.addSubSamplerResult(res);
                         }
                     }
                 }
             }
 
             // Check if we have a sampler to sample
             if(current != null) {
                 executeSamplePackage(current, transactionSampler, transactionPack, threadContext);
             }
             
             if (scheduler) {
                 // checks the scheduler to stop the iteration
                 stopSchedulerIfNeeded();
             }
         } catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         } catch (JMeterStopThreadException e) {
             log.info("Stopping Thread: " + e.toString());
             stopThread();
         } catch (Exception e) {
             if (current != null) {
                 log.error("Error while processing sampler '"+current.getName()+"' :", e);
             } else {
                 log.error("", e);
             }
         }
         return transactionResult;
     }
 
     /*
      * Execute the sampler with its pre/post processors, timers, assertions
      * Brodcast the result to the sample listeners
      */
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private void executeSamplePackage(Sampler current,
             TransactionSampler transactionSampler,
             SamplePackage transactionPack,
             JMeterContext threadContext) {
         
         threadContext.setCurrentSampler(current);
         // Get the sampler ready to sample
         SamplePackage pack = compiler.configureSampler(current);
         runPreProcessors(pack.getPreProcessors());
 
         // Hack: save the package for any transaction controllers
         threadVars.putObject(PACKAGE_OBJECT, pack);
 
         delay(pack.getTimers());
         Sampler sampler = pack.getSampler();
         sampler.setThreadContext(threadContext);
         // TODO should this set the thread names for all the subsamples?
         // might be more efficient than fetching the name elsewhere
         sampler.setThreadName(threadName);
         TestBeanHelper.prepare(sampler);
 
         // Perform the actual sample
         currentSampler = sampler;
         if (IMPLEMENTS_SAMPLE_STARTED) {
             for(SampleListener listener : pack.getSampleListeners()) {
                 try {
                     TestBeanHelper.prepare((TestElement) listener);
                     listener.sampleStarted(null);
                 } catch (RuntimeException e) {
                     log.error("Detected problem in Listener: ", e);
                     log.info("Continuing to process further listeners");
                 }
             }                    
         }
         SampleResult result = sampler.sample(null); // TODO: remove this useless Entry parameter
         if (IMPLEMENTS_SAMPLE_STARTED) {
             for(SampleListener listener : pack.getSampleListeners()) {
                 // We don't need to prepare these again here
                 listener.sampleStopped(null);
             }                    
         }
         currentSampler = null;
 
         // If we got any results, then perform processing on the result
         if (result != null) {
             int nbActiveThreadsInThreadGroup = threadGroup.getNumberOfThreads();
             int nbTotalActiveThreads = JMeterContextService.getNumberOfThreads();
             result.setGroupThreads(nbActiveThreadsInThreadGroup);
             result.setAllThreads(nbTotalActiveThreads);
             result.setThreadName(threadName);
             SampleResult[] subResults = result.getSubResults();
             if(subResults != null) {
                 for (SampleResult subResult : subResults) {
                     subResult.setGroupThreads(nbActiveThreadsInThreadGroup);
                     subResult.setAllThreads(nbTotalActiveThreads);
                     subResult.setThreadName(threadName);
                 }
             }
             threadContext.setPreviousResult(result);
             runPostProcessors(pack.getPostProcessors());
             checkAssertions(pack.getAssertions(), result, threadContext);
             // Do not send subsamples to listeners which receive the transaction sample
             List<SampleListener> sampleListeners = getSampleListeners(pack, transactionPack, transactionSampler);
             notifyListeners(sampleListeners, result);
             compiler.done(pack);
             // Add the result as subsample of transaction if we are in a transaction
             if(transactionSampler != null) {
                 transactionSampler.addSubSamplerResult(result);
             }
 
             // Check if thread or test should be stopped
             if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)) {
                 stopThread();
             }
             if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)) {
                 stopTest();
             }
             if (result.isStopTestNow() || (!result.isSuccessful() && onErrorStopTestNow)) {
                 stopTestNow();
             }
             if(result.isStartNextThreadLoop()) {
                 threadContext.setRestartNextLoop(true);
             }
         } else {
             compiler.done(pack); // Finish up
         }
     }
 
     private SampleResult doEndTransactionSampler(
                             TransactionSampler transactionSampler, 
                             Sampler parent,
                             SamplePackage transactionPack,
                             JMeterContext threadContext) {
         SampleResult transactionResult;
         // Get the transaction sample result
         transactionResult = transactionSampler.getTransactionResult();
         transactionResult.setThreadName(threadName);
         transactionResult.setGroupThreads(threadGroup.getNumberOfThreads());
         transactionResult.setAllThreads(JMeterContextService.getNumberOfThreads());
 
         // Check assertions for the transaction sample
         checkAssertions(transactionPack.getAssertions(), transactionResult, threadContext);
         // Notify listeners with the transaction sample result
         if (!(parent instanceof TransactionSampler)) {
             notifyListeners(transactionPack.getSampleListeners(), transactionResult);
         }
         compiler.done(transactionPack);
         return transactionResult;
     }
 
     /**
      * Get the SampleListeners for the sampler. Listeners who receive transaction sample
      * will not be in this list.
      *
      * @param samplePack
      * @param transactionPack
      * @param transactionSampler
      * @return the listeners who should receive the sample result
      */
     private List<SampleListener> getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {
         List<SampleListener> sampleListeners = samplePack.getSampleListeners();
         // Do not send subsamples to listeners which receive the transaction sample
         if(transactionSampler != null) {
             List<SampleListener> onlySubSamplerListeners = new ArrayList<>();
             List<SampleListener> transListeners = transactionPack.getSampleListeners();
             for(SampleListener listener : sampleListeners) {
                 // Check if this instance is present in transaction listener list
                 boolean found = false;
                 for(SampleListener trans : transListeners) {
                     // Check for the same instance
                     if(trans == listener) {
                         found = true;
                         break;
                     }
                 }
                 if(!found) {
                     onlySubSamplerListeners.add(listener);
                 }
             }
             sampleListeners = onlySubSamplerListeners;
         }
         return sampleListeners;
     }
 
     /**
      * @param threadContext
      * @return the iteration listener 
      */
     private IterationListener initRun(JMeterContext threadContext) {
         threadContext.setVariables(threadVars);
         threadContext.setThreadNum(getThreadNum());
         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
         threadContext.setThread(this);
         threadContext.setThreadGroup(threadGroup);
         threadContext.setEngine(engine);
         testTree.traverse(compiler);
         if (scheduler) {
             // set the scheduler to start
             startScheduler();
         }
 
         rampUpDelay(); // TODO - how to handle thread stopped here
         log.info("Thread started: " + Thread.currentThread().getName());
         /*
          * Setting SamplingStarted before the controllers are initialised allows
          * them to access the running values of functions and variables (however
          * it does not seem to help with the listeners)
          */
         threadContext.setSamplingStarted(true);
         
         controller.initialize();
         IterationListener iterationListener = new IterationListener();
         controller.addIterationListener(iterationListener);
 
         threadStarted();
         return iterationListener;
     }
 
     private void threadStarted() {
         JMeterContextService.incrNumberOfThreads();
         threadGroup.incrNumberOfThreads();
         GuiPackage gp =GuiPackage.getInstance();
         if (gp != null) {// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         ThreadListenerTraverser startup = new ThreadListenerTraverser(true);
         testTree.traverse(startup); // call ThreadListener.threadStarted()
     }
 
     private void threadFinished(LoopIterationListener iterationListener) {
         ThreadListenerTraverser shut = new ThreadListenerTraverser(false);
         testTree.traverse(shut); // call ThreadListener.threadFinished()
         JMeterContextService.decrNumberOfThreads();
         threadGroup.decrNumberOfThreads();
         GuiPackage gp = GuiPackage.getInstance();
         if (gp != null){// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         if (iterationListener != null) { // probably not possible, but check anyway
             controller.removeIterationListener(iterationListener);
         }
     }
 
     // N.B. This is only called at the start and end of a thread, so there is not
     // necessary to cache the search results, thus saving memory
     private static class ThreadListenerTraverser implements HashTreeTraverser {
         private final boolean isStart;
 
         private ThreadListenerTraverser(boolean start) {
             isStart = start;
         }
 
         @Override
         public void addNode(Object node, HashTree subTree) {
             if (node instanceof ThreadListener) {
                 ThreadListener tl = (ThreadListener) node;
                 if (isStart) {
                     tl.threadStarted();
                 } else {
                     tl.threadFinished();
                 }
             }
         }
 
         @Override
         public void subtractNode() {
         }
 
         @Override
         public void processPath() {
         }
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void stop() { // Called by StandardJMeterEngine, TestAction and AccessLogSampler
         running = false;
         log.info("Stopping: " + threadName);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt(){
         try {
             interruptLock.lock();
             Sampler samp = currentSampler; // fetch once; must be done under lock
             if (samp instanceof Interruptible){ // (also protects against null)
                 log.warn("Interrupting: " + threadName + " sampler: " +samp.getName());
                 try {
                     boolean found = ((Interruptible)samp).interrupt();
                     if (!found) {
                         log.warn("No operation pending");
                     }
                     return found;
                 } catch (Exception e) {
                     log.warn("Caught Exception interrupting sampler: "+e.toString());
                 }
             } else if (samp != null){
                 log.warn("Sampler is not Interruptible: "+samp.getName());
             }
         } finally {
             interruptLock.unlock();            
         }
         return false;
     }
 
     private void stopTest() {
         running = false;
         log.info("Stop Test detected by thread: " + threadName);
         if (engine != null) {
             engine.askThreadsToStop();
         }
     }
 
     private void stopTestNow() {
         running = false;
         log.info("Stop Test Now detected by thread: " + threadName);
         if (engine != null) {
             engine.stopTest();
         }
     }
 
     private void stopThread() {
         running = false;
         log.info("Stop Thread detected by thread: " + threadName);
     }
 
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private void checkAssertions(List<Assertion> assertions, SampleResult parent, JMeterContext threadContext) {
         for (Assertion assertion : assertions) {
             TestBeanHelper.prepare((TestElement) assertion);
             if (assertion instanceof AbstractScopedAssertion){
                 AbstractScopedAssertion scopedAssertion = (AbstractScopedAssertion) assertion;
                 String scope = scopedAssertion.fetchScope();
                 if (scopedAssertion.isScopeParent(scope) || scopedAssertion.isScopeAll(scope) || scopedAssertion.isScopeVariable(scope)){
                     processAssertion(parent, assertion);
                 }
                 if (scopedAssertion.isScopeChildren(scope) || scopedAssertion.isScopeAll(scope)){
                     SampleResult children[] = parent.getSubResults();
                     boolean childError = false;
                     for (int i=0;i <children.length; i++){
                         processAssertion(children[i], assertion);
                         if (!children[i].isSuccessful()){
                             childError = true;
                         }
                     }
                     // If parent is OK, but child failed, add a message and flag the parent as failed
                     if (childError && parent.isSuccessful()) {
                         AssertionResult assertionResult = new AssertionResult(((AbstractTestElement)assertion).getName());
                         assertionResult.setResultForFailure("One or more sub-samples failed");
                         parent.addAssertionResult(assertionResult);
                         parent.setSuccessful(false);
                     }
                 }
             } else {
                 processAssertion(parent, assertion);
             }
         }
         threadContext.getVariables().put(LAST_SAMPLE_OK, Boolean.toString(parent.isSuccessful()));
     }
 
     private void processAssertion(SampleResult result, Assertion assertion) {
         AssertionResult assertionResult;
         try {
             assertionResult = assertion.getResult(result);
         } catch (ThreadDeath e) {
             throw e;
         } catch (Error e) {
             log.error("Error processing Assertion ",e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         } catch (Exception e) {
             log.error("Exception processing Assertion ",e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         }
         result.setSuccessful(result.isSuccessful() && !(assertionResult.isError() || assertionResult.isFailure()));
         result.addAssertionResult(assertionResult);
     }
 
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private void runPostProcessors(List<PostProcessor> extractors) {
-        ListIterator<PostProcessor> iter;
-        if (reversePostProcessors) {// Original (rather odd) behaviour
-            iter = extractors.listIterator(extractors.size());// start at the end
-            while (iter.hasPrevious()) {
-                PostProcessor ex = iter.previous();
-                TestBeanHelper.prepare((TestElement) ex);
-                ex.process();
-            }
-        } else {
-            for (PostProcessor ex : extractors) {
-                TestBeanHelper.prepare((TestElement) ex);
-                ex.process();
-            }
+        for (PostProcessor ex : extractors) {
+            TestBeanHelper.prepare((TestElement) ex);
+            ex.process();
         }
     }
 
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private void runPreProcessors(List<PreProcessor> preProcessors) {
         for (PreProcessor ex : preProcessors) {
             if (log.isDebugEnabled()) {
                 log.debug("Running preprocessor: " + ((AbstractTestElement) ex).getName());
             }
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private void delay(List<Timer> timers) {
         long sum = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
             sum += timer.delay();
         }
         if (sum > 0) {
             try {
                 TimeUnit.MILLISECONDS.sleep(sum);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
             }
         }
     }
 
     void notifyTestListeners() {
         threadVars.incIteration();
         for (TestIterationListener listener : testIterationStartListeners) {
             if (listener instanceof TestElement) {
                 listener.testIterationStart(new LoopIterationEvent(controller, threadVars.getIteration()));
                 ((TestElement) listener).recoverRunningVersion();
             } else {
                 listener.testIterationStart(new LoopIterationEvent(controller, threadVars.getIteration()));
             }
         }
     }
 
     private void notifyListeners(List<SampleListener> listeners, SampleResult result) {
         SampleEvent event = new SampleEvent(result, threadGroup.getName(), threadVars);
         notifier.notifyListeners(event, listeners);
 
     }
 
     /**
      * Set rampup delay for JMeterThread Thread
      * @param delay Rampup delay for JMeterThread
      */
     public void setInitialDelay(int delay) {
         initialDelay = delay;
     }
 
     /**
      * Initial delay if ramp-up period is active for this threadGroup.
      */
     private void rampUpDelay() {
         delayBy(initialDelay, "RampUp");
     }
 
     /**
      * Wait for delay with RAMPUP_GRANULARITY
      * @param delay delay in ms
      * @param type Delay type
      */
     protected final void delayBy(long delay, String type) {
         if (delay > 0) {
             long start = System.currentTimeMillis();
             long end = start + delay;
             long now=0;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // Don't bother reporting stop test interruptions
                         log.warn(type+" delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
                     }
                     break;
                 }
             }
         }
     }
 
     /**
      * Returns the threadNum.
      *
      * @return the threadNum
      */
     public int getThreadNum() {
         return threadNum;
     }
 
     /**
      * Sets the threadNum.
      *
      * @param threadNum
      *            the threadNum to set
      */
     public void setThreadNum(int threadNum) {
         this.threadNum = threadNum;
     }
 
     private class IterationListener implements LoopIterationListener {
         /**
          * {@inheritDoc}
          */
         @Override
         public void iterationStart(LoopIterationEvent iterEvent) {
             notifyTestListeners();
         }
     }
 
     /**
      * Save the engine instance for access to the stop methods
      *
      * @param engine the engine which is used
      */
     public void setEngine(StandardJMeterEngine engine) {
         this.engine = engine;
     }
 
     /**
      * Should Test stop on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTest(boolean b) {
         onErrorStopTest = b;
     }
 
     /**
      * Should Test stop abruptly on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTestNow(boolean b) {
         onErrorStopTestNow = b;
     }
 
     /**
      * Should Thread stop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopThread(boolean b) {
         onErrorStopThread = b;
     }
 
     /**
      * Should Thread start next loop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStartNextLoop(boolean b) {
         onErrorStartNextLoop = b;
     }
 
     public void setThreadGroup(AbstractThreadGroup group) {
         this.threadGroup = group;
     }
 
 }
