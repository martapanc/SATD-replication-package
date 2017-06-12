diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index 585a66b80..d2e29faa9 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,947 +1,948 @@
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
 import org.apache.jmeter.samplers.SampleMonitor;
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
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     private final Controller threadGroupLoopController;
 
     private final HashTree testTree;
 
     private final TestCompiler compiler;
 
     private final JMeterThreadMonitor monitor;
 
     private final JMeterVariables threadVars;
 
     // Note: this is only used to implement TestIterationListener#testIterationStart
     // Since this is a frequent event, it makes sense to create the list once rather than scanning each time
     // The memory used will be released when the thread finishes
     private final Collection<TestIterationListener> testIterationStartListeners;
 
     private final Collection<SampleMonitor> sampleMonitors;
 
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
         threadGroupLoopController = (Controller) testTree.getArray()[0];
         SearchByClass<TestIterationListener> threadListenerSearcher = new SearchByClass<>(TestIterationListener.class); // TL - IS
         test.traverse(threadListenerSearcher);
         testIterationStartListeners = threadListenerSearcher.getSearchResults();
         SearchByClass<SampleMonitor> sampleMonitorSearcher = new SearchByClass<>(SampleMonitor.class);
         test.traverse(sampleMonitorSearcher);
         sampleMonitors = sampleMonitorSearcher.getSearchResults();
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
 
     @Override
     public void run() {
         // threadContext is not thread-safe, so keep within thread
         JMeterContext threadContext = JMeterContextService.getContext();
         LoopIterationListener iterationListener = null;
 
         try {
             iterationListener = initRun(threadContext);
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
                         }
+                        
+                        triggerEndOfLoopOnParentControllers(sam, threadContext);
+                        sam = null;
+                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
+                        threadContext.setRestartNextLoop(false);
+                    }
+                    else {
+                        sam = threadGroupLoopController.next();
                     }
-                    
-                    triggerEndOfLoopOnParentControllers(sam, threadContext);
-                    threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
-                    threadContext.setRestartNextLoop(false);
-                    sam = null;
                 }
                 
-                if (sam == null && threadGroupLoopController.isDone()) {
+                if (threadGroupLoopController.isDone()) {
                     running = false;
                     log.info("Thread is done: " + threadName);
                 }
-                else {
-                    sam = threadGroupLoopController.next();
-                }
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
         TransactionSampler transactionSampler = null;
         if(sam instanceof TransactionSampler) {
             transactionSampler = (TransactionSampler) sam;
         }
 
         Sampler realSampler = findRealSampler(sam);
         if(realSampler == null) {
             throw new IllegalStateException("Got null subSampler calling findRealSampler for:"+sam.getName()+", sam:"+sam);
         }
         // Find parent controllers of current sampler
         FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(realSampler);
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
      * Find the Real sampler (Not TransactionSampler) that really generated an error
      * The Sampler provided is not always the "real" one, it can be a TransactionSampler, 
      * if there are some other controllers (SimpleController or other implementations) between this TransactionSampler and the real sampler, 
      * triggerEndOfLoop will not be called for those controllers leaving them in "ugly" state.
      * the following method will try to find the sampler that really generate an error
      * @param sampler
      * @return {@link Sampler}
      */
     private Sampler findRealSampler(Sampler sampler) {
         Sampler realSampler = sampler;
         while(realSampler instanceof TransactionSampler) {
             realSampler = ((TransactionSampler) realSampler).getSubSampler();
         }
         return realSampler;
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
      * Broadcast the result to the sample listeners
      */
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
         for(SampleMonitor monitor : sampleMonitors) {
             monitor.sampleStarting(sampler);
         }
         SampleResult result = null;
         try {
             result = sampler.sample(null); // TODO: remove this useless Entry parameter
         } finally {
             for(SampleMonitor monitor : sampleMonitors) {
                 monitor.sampleEnded(sampler);
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
         
         threadGroupLoopController.initialize();
         IterationListener iterationListener = new IterationListener();
         threadGroupLoopController.addIterationListener(iterationListener);
 
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
             threadGroupLoopController.removeIterationListener(iterationListener);
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
                     SampleResult[] children = parent.getSubResults();
                     boolean childError = false;
                     for (SampleResult childSampleResult : children) {
                         processAssertion(childSampleResult, assertion);
                         if (!childSampleResult.isSuccessful()) {
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
 
     private void runPostProcessors(List<PostProcessor> extractors) {
         for (PostProcessor ex : extractors) {
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void runPreProcessors(List<PreProcessor> preProcessors) {
         for (PreProcessor ex : preProcessors) {
             if (log.isDebugEnabled()) {
                 log.debug("Running preprocessor: " + ((AbstractTestElement) ex).getName());
             }
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
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
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
                 ((TestElement) listener).recoverRunningVersion();
             } else {
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index c51336b0f..1de683a5d 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,517 +1,518 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 ]>
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 3.0 =================== -->
 
 <h1>Version 3.0</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since version 3.0, Groovy-2.4.6 is bundled with JMeter (lib folder), ensure you remove old version or referenced versions through properties <code>search_paths</code> or <code>user.classpath</code></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.idle_time</code> property value is true, meaning CSV/XML result files will contain an additional column containing idle time between samplers, see <bugzilla>57182</bugzilla></li>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 3.0, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 3.0, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. See <bugzilla>58687</bugzilla></li>
     <li>Property <code>jmeterthread.startearlier</code> has been removed. See <bugzilla>58726</bugzilla></li>   
     <li>Property <code>jmeterengine.startlistenerslater</code> has been removed. See <bugzilla>58728</bugzilla></li>   
     <li>Property <code>jmeterthread.reversePostProcessors</code> has been removed. See <bugzilla>58728</bugzilla></li>  
     <li>MongoDB elements (MongoDB Source Config, MongoDB Script) have been deprecated and will be removed in next version of jmeter. They do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. JMeter team advises not to use them anymore. See <bugzilla>58772</bugzilla></li>
     <li>Summariser listener now outputs a formated duration in HH:mm:ss (Hour:Minute:Second), it previously outputed seconds. See <bugzilla>58776</bugzilla></li>
     <li>WebService(SOAP) Request and HTML Parameter Mask which were deprecated in 2.13 version, have now been removed following our <a href="./usermanual/best-practices.html#deprecation">deprecation strategy</a>.
     Classes and properties which were only used by those elements have been dropped:
     <ul>
         <li><code>org.apache.jmeter.protocol.http.util.DOMPool</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLException</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLHelper</code></li>
         <li>Property <code>soap.document_cache</code></li>
         <li>JAR soap-2.3.1 has been also removed</li>
     </ul>
     </li>
     <li>org.apache.jmeter.protocol.http.visualizers.RequestViewHTTP.getQueryMap signature has changed, if you use it ensure you update your code. See <bugzilla>58845</bugzilla></li>
     <li><code>__jexl</code> function has been deprecated and will be removed in next version. See <bugzilla>58903</bugzilla></li>
     <li>JMS Subscriber will consider sample in error if number of received messages is not equals to expected number of messages. It previously considerer sample OK if only 1 message was received. See <bugzilla>58980</bugzilla></li>
     <li>Since version 3.0, HTTP(S) Test Script recorder uses default port 8888 as configured when using Recording Template. See <bugzilla>59006</bugzilla></li>
     <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>
     <li>Since version 3.0, the support for reading old Avalon format JTL (result) files has been removed, see <bugzilla>59064</bugzilla></li>
     <li>Since version 3.0, default property's value <code>http.java.sampler.retries</code> has been switched to 0 (no retry by default) to align it on HttpClient4's behaviour. 
     Note also that its meaning has changed, before 3.0, <code>http.java.sampler.retries=1</code> meant No Retry, since 3.0 it is more litteral, <code>http.java.sampler.retries=1</code> means 1 retry.
     See <bugzilla>59103</bugzilla></li>
     <li>Since 3.0, the following deprecated classes have been dropped
     <ul>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLContentHandler</li>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLErrorHandler</li>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLParser</li>
     </ul>
     </li>
     <li><code>httpsampler.await_termination_timeout</code> has been replaced by <code>httpsampler.parallel_download_thread_keepalive_inseconds</code> which is now the keep alive time for the parallel download threads (in seconds).</li>
     <li>For Thread Group Test Element, the property "Action to be taken after a Sample Error" value has been switched from "Continue" to "Start Next thread loop". See <bugzilla>59152</bugzilla></li>
     <li>The following jars have been removed:
     <ul>
         <li>excalibur-datasource-2.1.jar (see <bugzilla>59156</bugzilla>)</li>
         <li>excalibur-instrument-1.0.jar (see <bugzilla>58786</bugzilla>)</li>
         <li>excalibur-pool-api-2.1.jar (see <bugzilla>58786</bugzilla>)</li>
         <li>excalibur-pool-impl-2.1.jar (see <bugzilla>58786</bugzilla>)</li>
         <li>excalibur-pool-instrumented-2.1.jar (see <bugzilla>58786</bugzilla>)</li>
         <li>htmllexer-2.1.jar (see <bugzilla>59037</bugzilla>)</li>
         <li>htmlparser-2.1.jar (see <bugzilla>59037</bugzilla>)</li>
         <li>soap-2.3.1.jar</li>
         <li>xmlpull-1.1.3.1.jar (see <bugzilla>58679</bugzilla>)</li>
         <li>xpp3_min-1.1.4c.jar (see <bugzilla>58679</bugzilla>)</li>
         <li>jdom-1.1.3.jar (see <bugzilla>59156</bugzilla>)</li>
     </ul>
     </li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
     <li><bug>58843</bug>Improve the usable space in the HTTP sampler GUI. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58852</bug>Use less memory for <code>PUT</code> requests. The uploaded data will no longer be stored in the Sampler.
         This is the same behaviour as with <code>POST</code> requests.</li>
     <li><bug>58860</bug>HTTP Request : Add automatic variable generation in HTTP parameters table by right click. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58923</bug>normalize URIs when downloading embedded resources.</li>
     <li><bug>59005</bug>HTTP Sampler : Added WebDAV verb (SEARCH).</li>
     <li><bug>59006</bug>Change Default proxy recording port to 8888 to align it with Recording Template. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>58099</bug>Performance : Lazily initialize HttpClient SSL Context to avoid its initialization even for HTTP only scenarios</li>
     <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59023</bug>HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59028</bug>Use SystemDefaultDnsResolver singleton. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59036</bug>FormCharSetFinder : Use JSoup instead of deprecated HTMLParser</li>
     <li><bug>59034</bug>Parallel downloads connection management is not realistic. Contributed by Benoit Wiart (benoit dot wiart at gmail.com) and Philippe Mouawad</li>
     <li><bug>59060</bug>HTTP Request GUI : Move File Upload to a new Tab to have more space for parameters and prevent incoherent configuration. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59103</bug>HTTP Request Java Implementation: Change default "http.java.sampler.retries" to align it on HttpClient behaviour and make it meaningful</li>
     <li><bug>59083</bug>HTTP Request : Make Method field editable so that additional methods (Webdav) can be added easily</li>
     <li><bug>59118</bug>Add comment in recorded think time by proxy recorder. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>59116</bug>Add the possibility to setup a prefix to sampler name recorded by proxy. Partly based on a patch by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>59129</bug>HTTP Request : Simplify GUI with simple/advanced Tabs</li>
     <li><bug>59033</bug>Parallel Download : Rework Parser classes hierarchy to allow pluging parsers for different mime types</li>
     <li><bug>52073</bug>Embedded Resources Parallel download : Improve performances by avoiding shutdown of ThreadPoolExecutor at each sample. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>58786</bug>JDBC Sampler : Replace Excalibur DataSource by more up to date library commons-dbcp2</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58989</bug>Record controller gui : add a button to clear all the recorded samples. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 <li><bug>58776</bug>Summariser should display a more readable duration</li>
 <li><bug>58791</bug>Deprecate listeners:Distribution Graph (alpha) and Spline Visualizer</li>
 <li><bug>58849</bug>View Results Tree : Add a search panel to the request http view to be able to search in the parameters table. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58857</bug>View Results Tree : the request view http does not allow to resize the parameters table first column. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58955</bug>Request view http does not correctly display http parameters in multipart/form-data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>55597</bug>View Results Tree: Add a search feature to search in recorded samplers</li>
 <li><bug>59102</bug>View Results Tree: Better default value for "view.results.tree.max_size"</li>
 <li><bug>59099</bug>Backend listener : Add the possibility to consider samplersList as a Regular Expression. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
   <li><bug>58698</bug>Correct parsing of auth-files in HTTP Authorization Manager.</li>
   <li><bug>58756</bug>CookieManager : Cookie Policy select box content must depend on Cookie implementation.</li>
   <li><bug>56358</bug>Cookie manager supports cross port cookies and RFC6265. Thanks to Oleg Kalnichevski (olegk at apache.org)</li>
   <li><bug>58773</bug>TestCacheManager : Add tests for CacheManager that use HttpClient 4</li>
   <li><bug>58742</bug>CompareAssertion : Reset data in TableEditor when switching between different CompareAssertions in gui.
       Based on a patch by Vincent Herilier (vherilier at gmail.com)</li>
   <li><bug>58848</bug>Argument Panel : when adding an argument (add button or from clipboard) scroll the table to the new line. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>58865</bug>Allow empty default value in the Regular Expression Extractor. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>59156</bug>XMLAssertion : drop jdom dependency by using XMLReader</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
     <li><bug>58903</bug>Provide __jexl3 function that uses commons-jexl3 and deprecated __jexl (1.1) function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>58736</bug>Add Sample Timeout support</li>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>SVN Revision ID</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58726</bug>Remove the <code>jmeterthread.startearlier</code> parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58728</bug>Drop old behavioural properties</li>
 <li><bug>57319</bug>Upgrade to HttpClient 4.5.2. With the big help from Oleg Kalnichevski (olegk at apache.org) and Gary Gregory (ggregory at apache.org).</li>
 <li><bug>58772</bug>Deprecate MongoDB related elements</li>
 <li><bug>58782</bug>ThreadGroup : Improve ergonomy</li>
 <li><bug>58165</bug>Show the time elapsed since the start of the load test in GUI mode. Partly based on a contribution from Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><bug>58784</bug>Make JMeterUtils#runSafe sync/async awt invocation configurable and change the visualizers to use the async version.</li>
 <li><bug>58790</bug>Issue in CheckDirty and its relation to ActionRouter</li>
 <li><bug>58814</bug>JVM don't recognize option MaxLiveObjectEvacuationRatio; remove from comments</li>
 <li><bug>58810</bug>Config Element Counter (and others): Check Boxes Toggle Area Too Big</li>
 <li><bug>56554</bug>JSR223 Test Element : Generate compilation cache key automatically. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58911</bug>Header Manager : it should be possible to copy/paste between Header Managers. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58864</bug>Arguments Panel : when moving parameter with up / down, ensure that the selection remains visible. Based on a contribution by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58924</bug>Dashboard / report : It should be possible to export the generated graph as image (PNG). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58884</bug>JMeter report generator : need better error message. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58957</bug>Report/Dashboard: HTML Exporter does not create parent directories for output directory. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
 <li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 <li><bug>57182</bug>Settings defaults : Switch "jmeter.save.saveservice.idle_time" to true (after 2.13)</li>
 <li><bug>58987</bug>Report/Dashboard: Improve error reporting.</li>
 <li><bug>58870</bug>TableEditor: minimum size is too small. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59037</bug>Drop HtmlParserHTMLParser and dependencies on htmlparser and htmllexer</li>
 <li><bug>58933</bug>JSyntaxTextArea : Ability to set font.  Contributed by Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li><bug>58793</bug>Create developers page explaining how to build and contribute</li>
 <li><bug>59046</bug>JMeter Gui Replace controller should keep the name and the selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59038</bug>Deprecate HTTPClient 3.1 related elements</li>
 <li><bug>59094</bug>Drop support of old JMX file format</li>
 <li><bug>59082</bug>Remove the "TestCompiler.useStaticSet" parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59093</bug>Option parsing error message can be 'lost'</li>
 <li><bug>58715</bug>Feature request: Bundle groovy-all with JMeter</li>
 <li><bug>59095</bug>Remove UserParameterXMLParser that was deprecated 8 years ago. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58426</bug>Improve display of JMeter on high resolution devices (HiDPI) (part 1 of enhancement)</li>
 <li><bug>59105</bug>TableEditor : Add ability to paste rows from clipboard and delete multiple selection. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59152</bug>Thread Group: Change "Action to be taken after a Sample Error" value from "Continue" to "Start Next thread loop". Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to httpclient, httpmime 4.5.2 (from 4.2.6)</li>
 <li>Updated to tika-core and tika-parsers 1.12 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7.1 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.7.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.3 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.8 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li>Updated to commons-collections-3.2.2 (from 3.2.1)</li>
 <li>Updated to commons-net 3.4 (from 3.3)</li>
 <li>Updated to slf4j 1.7.13 (from 1.7.12)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58729</bug>Cleanup extras folder for maintainability</li>
 <li><bug>57110</bug>Fixed spelling+grammar, formatting, removed commented out code etc. Contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Correct instructions on running jmeter in help.txt. Contributed by Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li><bug>58704</bug>Non regression testing : Ant task batchtest fails if tests and run in a non en_EN locale and use a JMX file that uses a Csv DataSet</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58949</bug>Cleanup of ldap code. Based on a patch by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58967</bug>Use junit categories to exclude tests that need a gui. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59003</bug>ClutilTestCase testSingleArg8 and testSingleArg9 are identical</li>
 <li><bug>59064</bug>Remove OldSaveService which supported very old Avalon format JTL (result) files</li>
 <li><bug>59165</bug>RSyntaxTextArea not compatible with headless testing</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
     <li><bug>57804</bug>HTTP Request doesn't reuse cached SSL context when using Client Certificates in HTTPS (only fixed for HttpClient4 implementation)</li>
     <li><bug>58800</bug>proxy.pause default value , fix documentation</li>
     <li><bug>58844</bug>Buttons enable / disable is broken in the arguments panel. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58861</bug>When clicking on up, down or detail while in a cell of the argument panel, newly added content is lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>57935</bug>SSL SNI extension not supported by HttpClient 4.2.6</li>
     <li><bug>59044</bug>Http Sampler : It should not be possible to select the multipart encoding if the method is not POST. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59008</bug>Http Sampler: Infinite recursion SampleResult on frame depth limit reached</li>
     <li><bug>59069</bug>CookieManager : Selected Cookie Policy is always reset to default when saving or switching to another TestElement (nightly build 25th feb 2016)</li>
     <li><bug>58881</bug>HTTP Request : HTTPHC4Impl shows exception when server uses "deflate" compression</li>
     <li><bug>58583</bug>HTTP client fails to close connection if server misbehaves by not sending "connection: close", violating HTTP RFC 2616 / RFC 7230</li>
     <li><bug>58950</bug>NoHttpResponseException when Pause between samplers exceeds keepalive sent by server</li>
     <li><bug>59085</bug>Http file panel : data lost on browse cancellation. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>56141</bug>Application does not behave correctly when using HTTP Recorder. With the help of Dan (java.junkee at yahoo.com)</li>
     <li><bug>59079</bug>"httpsampler.max_redirects" property is not enforced when "Redirect Automatically" is used</li>
     <li><bug>58811</bug>When pasting arguments between http samplers the column "Encode" and "Include Equals" are lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59146</bug>MeasuringConnectionManager is not Thread Safe (nightly before 3.0)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
     <li><bug>59051</bug>JDBC Request : Connection is closed by pool if it exceeds the configured lifetime (affects nightly build as of 23 fev 2016).</li>
     <li><bug>59075</bug>JMS Publisher: NumberFormatException is thrown is priority or expiration fields are empty</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59067</bug>JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop". Contributed by Benoit Wiart(benoit dot wiart at gmail.com)</li>
     <li><bug>59076</bug>Test should fail if a module controller cannot find its replacement subtree</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug>SampleResultConverter should note that it cannot record non-TEXT data</li>
 <li><bug>58845</bug>Request http view doesn't display all the parameters. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58413</bug>ViewResultsTree : Request HTTP Renderer does not show correctly parameters that contain ampersand (&amp;). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59172</bug>SampleResult SaveConfig does not allow some fields to be disabled</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 <li><bug>58912</bug>Response assertion gui : Deleting more than 1 selected row deletes only one row. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
     <li><bug>58781</bug>Command line option "-?" shows Unknown option</li>
     <li><bug>58795</bug>NPE may occur in GuiPackage#getTestElementCheckSum with some 3rd party plugins</li>
     <li><bug>58913</bug>When closing jmeter should not interpret cancel as "destroy my test plan". Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58952</bug>Report/Dashboard: Generation of aggregated series in graphs does not work. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>58931</bug>New Report/Dashboard : Getting font errors under Firefox and Chrome (not Safari)</li>
     <li><bug>58932</bug>Report / Dashboard: Document clearly and log what report are not generated when saveservice options are not correct. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>59055</bug>JMeter report generator : When generation is not launched from jmeter/bin folder report-template is not found</li>
     <li><bug>58986</bug>Report/Dashboard reuses the same output directory</li>
     <li><bug>59096</bug>Search Feature : Case insensitive search is not really case insensitive</li>
+    <li><bug>59133</bug>Regression in custom Thread Group in nightly build before 3.0 (6 march 2016). Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 <li>Oleg Kalnichevski (olegk at apache.org)</li>
 <li>Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Florent Sabbe (f dot sabbe at ubik-ingenierie.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Harrison Termotto (harrison dot termotto at stonybrook.edu</li>
 <li>Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li>Gary Gregory (ggregory at apache.org)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (SHIFT + up/down) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
