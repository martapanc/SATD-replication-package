diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 7208be111..75475b2ec 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,256 +1,302 @@
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
 
 package org.apache.jmeter.control;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.threads.JMeterContext;
+import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterThread;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.threads.ListenerNotifier;
 import org.apache.jmeter.threads.SamplePackage;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Transaction Controller to measure transaction times
  *
  * There are two different modes for the controller:
  * - generate additional total sample after nested samples (as in JMeter 2.2)
  * - generate parent sampler containing the nested samples
  *
  */
 public class TransactionController extends GenericController implements SampleListener, Controller, Serializable {
     private static final long serialVersionUID = 233L;
+    
+    private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
+    private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
+
+    private final static String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
+    
     private static final Logger log = LoggingManager.getLoggerForClass();
 
+    /**
+     * Only used in parent Mode
+     */
     private transient TransactionSampler transactionSampler;
-
+    
+    /**
+     * Only used in NON parent Mode
+     */
     private transient ListenerNotifier lnf;
 
+    /**
+     * Only used in NON parent Mode
+     */
     private transient SampleResult res;
-
+    
+    /**
+     * Only used in NON parent Mode
+     */
     private transient int calls;
-
+    
+    /**
+     * Only used in NON parent Mode
+     */
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
+     * Only used in NON parent Mode
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
+     * Only used in NON parent Mode
      */
     private transient long prevEndTime;
 
-    private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
-
-    private final static String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
-
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
     private Object readResolve(){
         lnf = new ListenerNotifier();
         return this;
     }
 
     public void setParent(boolean _parent){
         setProperty(new BooleanProperty(PARENT, _parent));
     }
 
     public boolean isParent(){
         return getPropertyAsBoolean(PARENT);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next(){
         if (isParent()){
             return next1();
         }
         return next2();
     }
 
 ///////////////// Transaction Controller - parent ////////////////
 
     private Sampler next1() {
         // Check if transaction is done
         if(transactionSampler != null && transactionSampler.isTransactionDone()) {
             if (log.isDebugEnabled()) {
                 log.debug("End of transaction " + getName());
             }
             // This transaction is done
             transactionSampler = null;
             return null;
         }
 
         // Check if it is the start of a new transaction
         if (isFirst()) // must be the start of the subtree
         {
             if (log.isDebugEnabled()) {
                 log.debug("Start of transaction " + getName());
             }
             transactionSampler = new TransactionSampler(this, getName());
         }
 
         // Sample the children of the transaction
         Sampler subSampler = super.next();
         transactionSampler.setSubSampler(subSampler);
         // If we do not get any sub samplers, the transaction is done
         if (subSampler == null) {
             transactionSampler.setTransactionDone();
         }
         return transactionSampler;
     }
 
     @Override
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         if (!isParent()) {
             return super.nextIsAController(controller);
         }
         Sampler returnValue;
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             // We need to call the super.next, instead of this.next, which is done in GenericController,
             // because if we call this.next(), it will return the TransactionSampler, and we do not want that.
             // We need to get the next real sampler or controller
             returnValue = super.next();
         } else {
             returnValue = sampler;
         }
         return returnValue;
     }
 
 ////////////////////// Transaction Controller - additional sample //////////////////////////////
 
     private Sampler next2() {
         if (isFirst()) // must be the start of the subtree
         {
             calls = 0;
             noFailingSamples = 0;
             res = new SampleResult();
             res.setSampleLabel(getName());
             // Assume success
             res.setSuccessful(true);
             res.sampleStart();
             prevEndTime = res.getStartTime();//???
             pauseTime = 0;
         }
         boolean isLast = current==super.subControllersAndSamplers.size();
         Sampler returnValue = super.next();
         if (returnValue == null && isLast) // Must be the end of the controller
         {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
-                 res.sampleEnd();
+                res.sampleEnd();
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
-
-                // TODO could these be done earlier (or just once?)
-                JMeterContext threadContext = getThreadContext();
-                JMeterVariables threadVars = threadContext.getVariables();
-
-                SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
-                if (pack == null) {
-                	// If child of TransactionController is a ThroughputController and TPC does
-                	// not sample its children, then we will have this
-                	// TODO Should this be at warn level ?
-                    log.warn("Could not fetch SamplePackage");
-                } else {
-                    SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
-                    // We must set res to null now, before sending the event for the transaction,
-                    // so that we can ignore that event in our sampleOccured method
-                    res = null;
-                    // bug 50032 
-                    if (!getThreadContext().isReinitializingSubControllers()) {
-                        lnf.notifyListeners(event, pack.getSampleListeners());
-                    }
-                }
+                notifyListeners();
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
 
+    /**
+     * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
+     */
+    @Override
+    public void triggerEndOfLoop() {
+        if(!isParent()) {
+            if (res != null) {
+                res.setIdleTime(pauseTime+res.getIdleTime());
+                res.sampleEnd();
+                res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
+                res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
+                notifyListeners();
+            }
+        } else {
+            transactionSampler.setTransactionDone();
+            // This transaction is done
+            transactionSampler = null;
+        }
+        super.triggerEndOfLoop();
+    }
+
+    /**
+     * Create additional SampleEvent in NON Parent Mode
+     */
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
+
     public void sampleOccurred(SampleEvent se) {
         if (!isParent()) {
             // Check if we are still sampling our children
             if(res != null && !se.isTransactionSampleEvent()) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytes() + sampleResult.getBytes());
                 if (!isIncludeTimers()) {// Accumulate waiting time for later
                     pauseTime += sampleResult.getEndTime() - sampleResult.getTime() - prevEndTime;
                     prevEndTime = sampleResult.getEndTime();
                 }
                 if(!sampleResult.isSuccessful()) {
                     res.setSuccessful(false);
                     noFailingSamples++;
                 }
                 res.setAllThreads(sampleResult.getAllThreads());
                 res.setGroupThreads(sampleResult.getGroupThreads());
                 res.setLatency(res.getLatency() + sampleResult.getLatency());
             }
         }
     }
 
     public void sampleStarted(SampleEvent e) {
     }
 
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * Whether to include timers and pre/post processor time in overall sample.
      * @param includeTimers
      */
     public void setIncludeTimers(boolean includeTimers) {
         setProperty(INCLUDE_TIMERS, includeTimers, true); // default true for compatibility
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, true);
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index e6173343e..ba314942b 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,890 +1,901 @@
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
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
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
 import org.apache.jmeter.testelement.TestListener;
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
 
     private final Controller controller;
 
     private final HashTree testTree;
 
     private final TestCompiler compiler;
 
     private final JMeterThreadMonitor monitor;
 
     private final JMeterVariables threadVars;
 
     private final Collection<TestListener> testListeners;
 
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
         compiler = new TestCompiler(testTree, threadVars);
         controller = (Controller) testTree.getArray()[0];
         SearchByClass<TestListener> threadListenerSearcher = new SearchByClass<TestListener>(TestListener.class);
         test.traverse(threadListenerSearcher);
         testListeners = threadListenerSearcher.getSearchResults();
         notifier = note;
         running = true;
     }
 
     public void setInitialContext(JMeterContext context) {
         threadVars.putAll(context.getVariables());
     }
 
     /**
      * Enable the scheduler for this JMeterThread.
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
      * Check the scheduled time is completed.
      *
      */
     private void stopScheduler() {
         long delay = System.currentTimeMillis() - endTime;
         if ((delay >= 0)) {
             running = false;
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = (startTime - System.currentTimeMillis());
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
                     Thread.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // Don't bother reporting stop test interruptions
                         log.warn("startScheduler delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
                     }
                     break;
                 }
             }
         }
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     /*
      * See below for reason for this change. Just in case this causes problems,
      * allow the change to be backed out
      */
     private static final boolean startEarlier =
         JMeterUtils.getPropDefault("jmeterthread.startearlier", true); // $NON-NLS-1$
 
     private static final boolean reversePostProcessors =
         JMeterUtils.getPropDefault("jmeterthread.reversePostProcessors",false); // $NON-NLS-1$
 
     static {
         if (startEarlier) {
             log.info("jmeterthread.startearlier=true (see jmeter.properties)");
         } else {
             log.info("jmeterthread.startearlier=false (see jmeter.properties)");
         }
         if (reversePostProcessors) {
             log.info("Running PostProcessors in reverse order");
         } else {
             log.info("Running PostProcessors in forward order");
         }
     }
 
     public void run() {
         // threadContext is not thread-safe, so keep within thread
         JMeterContext threadContext = JMeterContextService.getContext();
         LoopIterationListener iterationListener=null;
 
         try {
             iterationListener = initRun(threadContext);
             while (running) {
                 Sampler firstSampler = controller.next();
                 Sampler sam = firstSampler;
                 while (running && sam != null) {
                 	process_sampler(sam, null, threadContext);
                 	if(onErrorStartNextLoop || threadContext.isRestartNextLoop()) {
                 	    if(threadContext.isRestartNextLoop()) {
-                            triggerEndOfLoopOnParentControllers(sam);
+                            triggerEndOfLoopOnParentControllers(sam, threadContext);
                             sam = null;
                             threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                             threadContext.setRestartNextLoop(false);
                 	    } else {
                     		boolean lastSampleFailed = !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK));
                     		if(lastSampleFailed) {
     	                		if(log.isDebugEnabled()) {
     	                    		log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
     	                    	}
-    	                    	triggerEndOfLoopOnParentControllers(sam);
+    	                    	triggerEndOfLoopOnParentControllers(sam, threadContext);
     	                        sam = null;
     	                        threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                     		} else {
                     			sam = controller.next();
                     		}
                 	    }
                 	} 
                 	else {
                 		sam = controller.next();
                 	}
                 }
                 if (controller.isDone()) {
                     running = false;
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
                 monitor.threadFinished(this); // Tell the engine we are done
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
+     * @param threadContext 
      */
-    private void triggerEndOfLoopOnParentControllers(Sampler sam) {
+    private void triggerEndOfLoopOnParentControllers(Sampler sam, JMeterContext threadContext) {
         // Find parent controllers of current sampler
-        FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
+        FindTestElementsUpToRootTraverser pathToRootTraverser=null;
+        TransactionSampler transactionSampler = null;
+        if(sam instanceof TransactionSampler) {
+            transactionSampler = (TransactionSampler) sam;
+            pathToRootTraverser = new FindTestElementsUpToRootTraverser((transactionSampler).getTransactionController());
+        } else {
+            pathToRootTraverser = new FindTestElementsUpToRootTraverser(sam);
+        }
         testTree.traverse(pathToRootTraverser);
         List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
   	
         // Trigger end of loop condition on all parent controllers of current sampler
         for (Iterator<Controller> iterator = controllersToReinit
                 .iterator(); iterator.hasNext();) {
             Controller parentController =  iterator.next();
             if(parentController instanceof ThreadGroup) {
                 ThreadGroup tg = (ThreadGroup) parentController;
                 tg.startNextLoop();
             } else {
                 parentController.triggerEndOfLoop();
             }
         }
+        if(transactionSampler!=null) {
+            process_sampler(transactionSampler, null, threadContext);
+        }
     }
 
     /**
      * Process the current sampler, handling transaction samplers.
      *
      * @param current sampler
      * @param parent sampler
      * @param threadContext
      * @return SampleResult if a transaction was processed
      */
     @SuppressWarnings("deprecation") // OK to call TestBeanHelper.prepare()
     private SampleResult process_sampler(Sampler current, Sampler parent, JMeterContext threadContext) {
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
                     // Get the transaction sample result
                     transactionResult = transactionSampler.getTransactionResult();
                     transactionResult.setThreadName(threadName);
                     transactionResult.setGroupThreads(threadGroup.getNumberOfThreads());
                     transactionResult.setAllThreads(JMeterContextService.getNumberOfThreads());
 
                     // Check assertions for the transaction sample
                     checkAssertions(transactionPack.getAssertions(), transactionResult, threadContext);
                     // Notify listeners with the transaction sample result
                     if (!(parent instanceof TransactionSampler)){
                         notifyListeners(transactionPack.getSampleListeners(), transactionResult);
                     }
                     compiler.done(transactionPack);
                     // Transaction is done, we do not have a sampler to sample
                     current = null;
                 }
                 else {
                     Sampler prev = current;
                     // It is the sub sampler of the transaction that will be sampled
                     current = transactionSampler.getSubSampler();
                     if (current instanceof TransactionSampler){
                         SampleResult res = process_sampler(current, prev, threadContext);// recursive call
                         threadContext.setCurrentSampler(prev);
                         current=null;
                         if (res!=null){
                             transactionSampler.addSubSamplerResult(res);
                         }
                     }
                 }
             }
 
             // Check if we have a sampler to sample
             if(current != null) {
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
                 // might be more efficient than fetching the name elsewehere
                 sampler.setThreadName(threadName);
                 TestBeanHelper.prepare(sampler);
 
                 // Perform the actual sample
                 currentSampler = sampler;
                 SampleResult result = sampler.sample(null);
                 currentSampler = null;
                 // TODO: remove this useless Entry parameter
 
                 // If we got any results, then perform processing on the result
                 if (result != null) {
                     result.setGroupThreads(threadGroup.getNumberOfThreads());
                     result.setAllThreads(JMeterContextService.getNumberOfThreads());
                     result.setThreadName(threadName);
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
                 } else {
                     compiler.done(pack); // Finish up
                 }
             }
             if (scheduler) {
                 // checks the scheduler to stop the iteration
                 stopScheduler();
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
             ArrayList<SampleListener> onlySubSamplerListeners = new ArrayList<SampleListener>();
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
      * @return 
      *
      */
     private IterationListener initRun(JMeterContext threadContext) {
         threadContext.setVariables(threadVars);
         threadContext.setThreadNum(getThreadNum());
         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
         threadContext.setThread(this);
         threadContext.setThreadGroup(threadGroup);
         threadContext.setEngine(engine);
         testTree.traverse(compiler);
         // listeners = controller.getListeners();
         if (scheduler) {
             // set the scheduler to start
             startScheduler();
         }
         rampUpDelay(); // TODO - how to handle thread stopped here
         log.info("Thread started: " + Thread.currentThread().getName());
         /*
          * Setting SamplingStarted before the contollers are initialised allows
          * them to access the running values of functions and variables (however
          * it does not seem to help with the listeners)
          */
         if (startEarlier) {
             threadContext.setSamplingStarted(true);
         }
         controller.initialize();
         IterationListener iterationListener = new IterationListener();
         controller.addIterationListener(iterationListener);
         if (!startEarlier) {
             threadContext.setSamplingStarted(true);
         }
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
 
     private static class ThreadListenerTraverser implements HashTreeTraverser {
         private boolean isStart = false;
 
         private ThreadListenerTraverser(boolean start) {
             isStart = start;
         }
 
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
 
         public void subtractNode() {
         }
 
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
         ListIterator<PostProcessor> iter;
         if (reversePostProcessors) {// Original (rather odd) behaviour
             iter = extractors.listIterator(extractors.size());// start at the end
             while (iter.hasPrevious()) {
                 PostProcessor ex = iter.previous();
                 TestBeanHelper.prepare((TestElement) ex);
                 ex.process();
             }
         } else {
             for (PostProcessor ex : extractors) {
                 TestBeanHelper.prepare((TestElement) ex);
                 ex.process();
             }
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
                 Thread.sleep(sum);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
             }
         }
     }
 
     void notifyTestListeners() {
         threadVars.incIteration();
         for (TestListener listener : testListeners) {
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
 
     public void setInitialDelay(int delay) {
         initialDelay = delay;
     }
 
     /**
      * Initial delay if ramp-up period is active for this threadGroup.
      */
     private void rampUpDelay() {
         if (initialDelay > 0) {
             long start = System.currentTimeMillis();
             long end = start + initialDelay;
             long now=0;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     Thread.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // Don't bother reporting stop test interruptions
                         log.warn("RampUp delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+initialDelay);
                     }
                     break;
                 }
             }
         }
     }
 
     /**
      * Returns the threadNum.
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
         public void iterationStart(LoopIterationEvent iterEvent) {
             notifyTestListeners();
         }
     }
 
     /**
      * Save the engine instance for access to the stop methods
      *
      * @param engine
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
index 329dea894..5a5606c66 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,207 +1,208 @@
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
 <document>   
 <properties>     
 	<author email="dev AT jmeter.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.7 =================== -->
 
 <h1>Version 2.7</h1>
 
 <h2>New and Noteworthy</h2>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode (see Bug 50898). 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 The workaround is to use different names for IncludeControllers
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see Bug 52496).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 When doing replacement of User Defined Variables, Proxy will not substitute partial values anymore when "Regexp matching" is used. It will use Perl 5 word matching ("\b")
 </p>
 
 <p>
 In User Defined Variables, Test Plan, HTTP Sampler Arguments Table, Java Request Defaults, JMS Sampler and Publisher, LDAP Request Defaults and LDAP Extended Request Defaults, rows with
 empty Name and Value are no more saved.
 </p>
 
 <p>
 JMeter now expands the Test Plan tree to the testplan level and no further and selects the root of the tree. Furthermore default value of onload.expandtree is false.
 </p>
 
 <p>
 Graph Full Results Listener has been removed.
 </p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 52613 - Using Raw Post Body option, text gets encoded</li>
 <li>Bug 52781 - Content-Disposition header garbled even if browser compatible headers is checked (HC4) </li>
 <li>Bug 52796 - MonitorHandler fails to clear variables when starting a new parse</li>
 <li>Bug 52871 - Multiple Certificates not working with HTTP Client 4</li>
 <li>Bug 52885 - Proxy : Recording issues with HTTPS, cookies starting with secure are partly truncated</li>
 <li>Bug 52886 - Proxy : Recording issues with HTTPS when spoofing is on, secure cookies are not always changed</li>
 <li>Bug 52897 - HTTPSampler : Using PUT method with HTTPClient4 and empty Content Encoding and sending files leads to NullPointerException</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51737 - TCPSampler : Packet gets converted/corrupted</li>
 <li>Bug 52868 - BSF language list should be sorted</li>
 <li>Bug 52869 - JSR223 language list currently uses BSF list which is wrong</li>
 <li>Bug 52932 - JDBC Sampler : Sampler is not marked in error in an Exception which is not of class IOException, SQLException, IOException occurs</li>
 <li>Bug 52916 - JDBC Exception if there is an empty user defined variable</li>
 <li>Bug 52937 - Webservice Sampler : Clear Soap Documents Cache at end of Test </li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
+<li>Bug 52968 - Option Start Next Loop in Thread Group does not mark parent Transaction Sampler in error when an error occurs</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 43450 - Listeners/Savers assume SampleResult count is always 1; fixed Generate Summary Results</li>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 <li>Bug 52848 - NullPointer in "XPath Assertion"</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>Bug 52551 - Function Helper Dialog does not switch language correctly</li>
 <li>Bug 52552 - Help reference only works in English</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 52639 - JSplitPane divider for log panel should be hidden if log is not activated</li>
 <li>Bug 52672 - Change Controller action deletes all but one child samplers</li>
 <li>Bug 52694 - Deadlock in GUI related to non AWT Threads updating GUI</li>
 <li>Bug 52678 - Proxy : When doing replacement of UserDefinedVariables, partial values should not be substituted</li>
 <li>Bug 52728 - CSV Data Set Config element cannot coexist with BSF Sampler in same Thread Plan</li>
 <li>Bug 52762 - Problem with multiples certificates: first index not used until indexes are restarted</li>
 <li>Bug 52741 - TestBeanGUI default values do not work at second time or later</li>
 <li>Bug 52783 - oro.patterncache.size property never used due to early init</li>
 <li>Bug 52789 - Proxy with Regexp Matching can fail with NullPointerException in Value Replacement if value is null</li>
 <li>Bug 52645 - Recording with Proxy leads to OutOfMemory</li>
 <li>Bug 52679 - User Parameters columns narrow</li>
 <li>Bug 52843 - Sample headerSize and bodySize not being accumulated for subsamples</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 52775 - JMS Publisher : Add Non Persistent Delivery option</li>
 <li>Bug 52810 - Enable setting JMS Properties through JMS Publisher sampler</li>
 <li>Bug 52938 - Webservice Sampler : Add a jmeter property soap.document_cache to control size of Document Cache</li>
 <li>Bug 52939 - Webservice Sampler : Make MaintainSession configurable</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication</li>
 <li>Bug 52698 - Remove Graph Full Results Listener</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 45839 - Test Action : Allow premature exit from a loop</li>
 <li>Bug 52614 - MailerModel.sendMail has strange way to calculate debug setting</li>
 <li>Bug 52782 - Add a detail button on parameters table to show detail of a Row</li>
 <li>Bug 52674 - Proxy : Add a Sampler Creator to allow plugging HTTP based samplers using potentially non textual POST Body (AMF, Silverlight...) and customizing them for others</li>
 <li>Bug 52934 - GUI : Open Test plan with the tree expanded to the testplan level and no further and select the root of the tree</li>
 <li>Bug 52941 - Improvements of HTML report design generated by JMeter Ant task extra</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Upgraded to rhino 1.7R3 (was js-1.7R2.jar). 
 Note: the Maven coordinates for the jar were changed from rhino:js to org.mozilla:rhino.
 This does not affect JMeter directly, but might cause problems if using JMeter in a Maven project
 with other code that depends on an earlier version of the Rhino Javascript jar.
 </li>
 <li>Bug 52675 - Refactor Proxy and HttpRequestHdr to allow Sampler Creation by Proxy</li>
 <li>Bug 52680 - Mention version in which function was introduced</li>
 <li>Bug 52788 - HttpRequestHdr : Optimize code to avoid useless work</li>
 <li>JMeter Ant (ant-jmeter-1.1.1.jar) task was upgraded from 1.0.9 to 1.1.1</li>
 </ul>
 
 </section> 
 </body> 
 </document>
