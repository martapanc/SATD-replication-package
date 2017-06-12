diff --git a/src/core/org/apache/jmeter/threads/AbstractThreadGroup.java b/src/core/org/apache/jmeter/threads/AbstractThreadGroup.java
index a1a73a867..8fff7eda6 100644
--- a/src/core/org/apache/jmeter/threads/AbstractThreadGroup.java
+++ b/src/core/org/apache/jmeter/threads/AbstractThreadGroup.java
@@ -1,261 +1,298 @@
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
 
 import java.io.Serializable;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.LoopController;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jorphan.collections.ListedHashTree;
 
 /**
  * ThreadGroup holds the settings for a JMeter thread group.
  * 
  * This class is intended to be ThreadSafe.
  */
 public abstract class AbstractThreadGroup extends AbstractTestElement 
     implements Serializable, Controller, JMeterThreadMonitor, TestCompilerHelper {
 
     private static final long serialVersionUID = 240L;
 
     // Only create the map if it is required
     private final transient ConcurrentMap<TestElement, Object> children = new ConcurrentHashMap<>();
 
     private static final Object DUMMY = new Object();
 
     /** Action to be taken when a Sampler error occurs */
     public static final String ON_SAMPLE_ERROR = "ThreadGroup.on_sample_error"; // int
 
     /** Continue, i.e. ignore sampler errors */
     public static final String ON_SAMPLE_ERROR_CONTINUE = "continue";
 
     /** Start next loop for current thread if sampler error occurs */
     public static final String ON_SAMPLE_ERROR_START_NEXT_LOOP = "startnextloop";
 
     /** Stop current thread if sampler error occurs */
     public static final String ON_SAMPLE_ERROR_STOPTHREAD = "stopthread";
 
     /** Stop test (all threads) if sampler error occurs, the entire test is stopped at the end of any current samples */
     public static final String ON_SAMPLE_ERROR_STOPTEST = "stoptest";
 
     /** Stop test NOW (all threads) if sampler error occurs, the entire test is stopped abruptly. Any current samplers are interrupted if possible. */
     public static final String ON_SAMPLE_ERROR_STOPTEST_NOW = "stoptestnow";
 
     /** Number of threads in the thread group */
     public static final String NUM_THREADS = "ThreadGroup.num_threads";
 
     public static final String MAIN_CONTROLLER = "ThreadGroup.main_controller";
 
     private final AtomicInteger numberOfThreads = new AtomicInteger(0); // Number of active threads in this group
 
     /** {@inheritDoc} */
     @Override
     public boolean isDone() {
         return getSamplerController().isDone();
     }
 
     /** {@inheritDoc} */
     @Override
     public Sampler next() {
         return getSamplerController().next();
     }
 
     /**
      * Get the sampler controller.
      *
      * @return the sampler controller.
      */
     public Controller getSamplerController() {
         return (Controller) getProperty(MAIN_CONTROLLER).getObjectValue();
     }
 
     /**
      * Set the sampler controller.
      *
      * @param c
      *            the sampler controller.
      */
     public void setSamplerController(LoopController c) {
         c.setContinueForever(false);
         setProperty(new TestElementProperty(MAIN_CONTROLLER, c));
     }
 
     /**
      * Add a test element.
      *
      * @param child
      *            the test element to add.
      */
     @Override
     public void addTestElement(TestElement child) {
         getSamplerController().addTestElement(child);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public final boolean addTestElementOnce(TestElement child){
         if (children.putIfAbsent(child, DUMMY) == null) {
             addTestElement(child);
             return true;
         }
         return false;
     }
 
     /** {@inheritDoc} */
     @Override
     public void addIterationListener(LoopIterationListener lis) {
         getSamplerController().addIterationListener(lis);
     }
     
     /** {@inheritDoc} */
     @Override
     public void removeIterationListener(LoopIterationListener iterationListener) {
         getSamplerController().removeIterationListener(iterationListener);
     }
 
     /** {@inheritDoc} */
     @Override
     public void initialize() {
         Controller c = getSamplerController();
         JMeterProperty property = c.getProperty(TestElement.NAME);
         property.setObjectValue(getName()); // Copy our name into that of the controller
         property.setRunningVersion(property.isRunningVersion());// otherwise name reverts
         c.initialize();
     }
 
     /**
      * Start next iteration after an error
      */
     public void startNextLoop() {
        ((LoopController) getSamplerController()).startNextLoop();
     }
     
     /**
      * NOOP
      */
     @Override
     public void triggerEndOfLoop() {
         // NOOP
     }
     
     /**
      * Set the total number of threads to start
      *
      * @param numThreads
      *            the number of threads.
      */
     public void setNumThreads(int numThreads) {
         setProperty(new IntegerProperty(NUM_THREADS, numThreads));
     }
 
     /**
      * Increment the number of active threads
      */
     void incrNumberOfThreads() {
         numberOfThreads.incrementAndGet();
     }
 
     /**
      * Decrement the number of active threads
      */
     void decrNumberOfThreads() {
         numberOfThreads.decrementAndGet();
     }
 
     /**
      * Get the number of active threads
      *
      * @return the number of active threads
      */
     public int getNumberOfThreads() {
         return numberOfThreads.get();
     }
     
     /**
      * Get the number of threads.
      *
      * @return the number of threads.
      */
     public int getNumThreads() {
         return this.getPropertyAsInt(AbstractThreadGroup.NUM_THREADS);
     }
 
     /**
      * Check if a sampler error should cause thread to start next loop.
      *
      * @return true if thread should start next loop
      */
     public boolean getOnErrorStartNextLoop() {
         return getPropertyAsString(AbstractThreadGroup.ON_SAMPLE_ERROR).equalsIgnoreCase(ON_SAMPLE_ERROR_START_NEXT_LOOP);
     }
 
     /**
      * Check if a sampler error should cause thread to stop.
      *
      * @return true if thread should stop
      */
     public boolean getOnErrorStopThread() {
         return getPropertyAsString(AbstractThreadGroup.ON_SAMPLE_ERROR).equalsIgnoreCase(ON_SAMPLE_ERROR_STOPTHREAD);
     }
 
     /**
      * Check if a sampler error should cause test to stop.
      *
      * @return true if test (all threads) should stop
      */
     public boolean getOnErrorStopTest() {
         return getPropertyAsString(AbstractThreadGroup.ON_SAMPLE_ERROR).equalsIgnoreCase(ON_SAMPLE_ERROR_STOPTEST);
     }
 
     /**
      * Check if a sampler error should cause test to stop now.
      *
      * @return true if test (all threads) should stop immediately
      */
     public boolean getOnErrorStopTestNow() {
         return getPropertyAsString(AbstractThreadGroup.ON_SAMPLE_ERROR).equalsIgnoreCase(ON_SAMPLE_ERROR_STOPTEST_NOW);
     }
 
+    /**
+     * Hard or graceful stop depending on now flag
+     * @param threadName String thread name
+     * @param now if true interrupt {@link Thread} 
+     * @return boolean true if stop succeeded
+     */
     public abstract boolean stopThread(String threadName, boolean now);
 
+    /**
+     * @return int number of active threads 
+     */
     public abstract int numberOfActiveThreads();
 
+    /**
+     * Start the {@link ThreadGroup}
+     * @param groupCount group number
+     * @param notifier {@link ListenerNotifier}
+     * @param threadGroupTree {@link ListedHashTree}
+     * @param engine {@link StandardJMeterEngine}
+     */
     public abstract void start(int groupCount, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine);
 
+    /**
+     * Add a new {@link JMeterThread} to this {@link ThreadGroup} for engine
+     * @param delay Delay in milliseconds
+     * @param engine {@link StandardJMeterEngine}
+     * @return {@link JMeterThread}
+     */
+    public abstract JMeterThread addNewThread(int delay, StandardJMeterEngine engine);
+
+    /**
+     * @return true if threads were correctly stopped
+     */
     public abstract boolean verifyThreadsStopped();
 
+    /**
+     * Wait for all Group Threads to stop after a graceful stop
+     */
     public abstract void waitThreadsStopped();
 
+    /**
+     * Ask threads to stop gracefully
+     */
     public abstract void tellThreadsToStop();
 
+    /**
+     * This immediately stop threads of Group by interrupting them
+     * It differs from {@link AbstractThreadGroup#tellThreadsToStop()} by being a hard stop
+     */
     public abstract void stop();
 }
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index afaad81c2..880b1d52b 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,980 +1,999 @@
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
+import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.collections.SearchByClass;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
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
 
     private static final float TIMER_FACTOR = JMeterUtils.getPropDefault("timer.factor", 1.0f);
 
     /**
      * 1 as float
      */
     private static final float ONE_AS_FLOAT = 1.0f;
 
     private static final boolean APPLY_TIMER_FACTOR = Float.compare(TIMER_FACTOR,ONE_AS_FLOAT) != 0;
 
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
         if (delay >= 0) {
             running = false;
             log.info("Stopping because end time detected by thread: " + threadName);
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = startTime - System.currentTimeMillis();
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
             while (running) {
                 Sampler sam = threadGroupLoopController.next();
                 while (running && sam != null) {
                     processSampler(sam, null, threadContext);
                     threadContext.cleanAfterSample();
                     
                     // restart of the next loop 
                     // - was requested through threadContext
                     // - or the last sample failed AND the onErrorStartNextLoop option is enabled
                     if(threadContext.isRestartNextLoop()
                             || (onErrorStartNextLoop
                                     && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
                     {
                         if(log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
                                 log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                         }
                         
                         triggerEndOfLoopOnParentControllers(sam, threadContext);
                         sam = null;
                         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                         threadContext.setRestartNextLoop(false);
                     }
                     else {
                         sam = threadGroupLoopController.next();
                     }
                 }
                 
                 if (threadGroupLoopController.isDone()) {
                     running = false;
                     log.info("Thread is done: " + threadName);
                 }
             }
         }
         // Might be found by contoller.next()
         catch (JMeterStopTestException e) { // NOSONAR
             log.info("Stopping Test: " + e.toString()); 
             stopTest();
         }
         catch (JMeterStopTestNowException e) { // NOSONAR
             log.info("Stopping Test Now: " + e.toString());
             stopTestNow();
         } catch (JMeterStopThreadException e) { // NOSONAR
             log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());
         } catch (Exception | JMeterError e) {
             log.error("Test failed!", e);
         } catch (ThreadDeath e) {
             throw e; // Must not ignore this one
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
         } catch (JMeterStopTestException e) { // NOSONAR
             log.info("Stopping Test: " + e.toString());
             stopTest();
         } catch (JMeterStopThreadException e) { // NOSONAR
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
         if(!sampleMonitors.isEmpty()) {
             for(SampleMonitor sampleMonitor : sampleMonitors) {
                 sampleMonitor.sampleStarting(sampler);
             }
         }
         SampleResult result = null;
         try {
             result = sampler.sample(null);
         } finally {
             if(!sampleMonitors.isEmpty()) {
                 for(SampleMonitor sampleMonitor : sampleMonitors) {
                     sampleMonitor.sampleEnded(sampler);
                 }
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
             // NOOP
         }
 
         @Override
         public void processPath() {
             // NOOP
         }
     }
 
     public String getThreadName() {
         return threadName;
     }
 
+    /**
+     * Set running flag to false which will interrupt JMeterThread on next flag test.
+     * This is a clean shutdown.
+     */
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
                 } catch (Exception e) { // NOSONAR
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
         } catch (JMeterError e) {
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
         long totalDelay = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
             long delay = timer.delay();
             if(APPLY_TIMER_FACTOR && 
                     timer.isModifiable()) {
                 if(log.isDebugEnabled()) {
                     log.debug("Applying TIMER_FACTOR:"
                             +TIMER_FACTOR + " on timer:"
                             +((TestElement)timer).getName()
                             + " for thread:"+getThreadName());
                 }
                 delay = Math.round(delay * TIMER_FACTOR);
             }
             totalDelay += delay;
         }
         if (totalDelay > 0) {
             try {
                 if(scheduler) {
                     // We reduce pause to ensure end of test is not delayed by a sleep ending after test scheduled end
                     // See Bug 60049
                     long now = System.currentTimeMillis();
                     if(now + totalDelay > endTime) {
                         totalDelay = endTime - now;
                     }
                 }
                 TimeUnit.MILLISECONDS.sleep(totalDelay);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
                 Thread.currentThread().interrupt();
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
             long now;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // NOSONAR Don't bother reporting stop test interruptions 
                         log.warn(type+" delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
                     }
                     Thread.currentThread().interrupt();
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
 
+    /**
+     * @return {@link ListedHashTree}
+     */
+    public ListedHashTree getTestTree() {
+        return (ListedHashTree) testTree;
+    }
+
+    /**
+     * @return {@link ListenerNotifier}
+     */
+    public ListenerNotifier getNotifier() {
+        return notifier;
+    }
+
 }
diff --git a/src/core/org/apache/jmeter/threads/ThreadGroup.java b/src/core/org/apache/jmeter/threads/ThreadGroup.java
index aa41c37f0..ac428739a 100644
--- a/src/core/org/apache/jmeter/threads/ThreadGroup.java
+++ b/src/core/org/apache/jmeter/threads/ThreadGroup.java
@@ -1,603 +1,672 @@
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
 
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.log.Logger;
 
 /**
  * ThreadGroup holds the settings for a JMeter thread group.
  * 
  * This class is intended to be ThreadSafe.
  */
 public class ThreadGroup extends AbstractThreadGroup {
-    private static final long serialVersionUID = 280L;
+    private static final long serialVersionUID = 281L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     private static final String DATE_FIELD_FORMAT = "yyyy/MM/dd HH:mm:ss"; //$NON-NLS-1$
 
     private static final long WAIT_TO_DIE = JMeterUtils.getPropDefault("jmeterengine.threadstop.wait", 5 * 1000); // 5 seconds
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     //+ JMX entries - do not change the string values
 
     /** Ramp-up time */
     public static final String RAMP_TIME = "ThreadGroup.ramp_time";
 
     /** Whether thread startup is delayed until required */
     public static final String DELAYED_START = "ThreadGroup.delayedStart";
 
     /** Whether scheduler is being used */
     public static final String SCHEDULER = "ThreadGroup.scheduler";
 
     /** Scheduler absolute start time */
     public static final String START_TIME = "ThreadGroup.start_time";
 
     /** Scheduler absolute end time */
     public static final String END_TIME = "ThreadGroup.end_time";
 
     /** Scheduler duration, overrides end time */
     public static final String DURATION = "ThreadGroup.duration";
 
     /** Scheduler start delay, overrides start time */
     public static final String DELAY = "ThreadGroup.delay";
 
     //- JMX entries
 
     private transient Thread threadStarter;
 
     // List of active threads
     private final Map<JMeterThread, Thread> allThreads = new ConcurrentHashMap<>();
+    
+    private final Object addThreadLock = new Object();
 
     /**
      * Is test (still) running?
      */
     private volatile boolean running = false;
 
     /**
+     * Thread Group number
+     */
+    private int groupNumber;
+
+    /**
      * Are we using delayed startup?
      */
     private boolean delayedStartup;
 
     /**
+     * Thread safe class
+     */
+    private ListenerNotifier notifier;
+
+    /**
+     * This property will be cloned
+     */
+    private ListedHashTree threadGroupTree;
+
+    /**
      * No-arg constructor.
      */
     public ThreadGroup() {
         super();
     }
 
     /**
      * Set whether scheduler is being used
      *
      * @param scheduler true is scheduler is to be used
      */
     public void setScheduler(boolean scheduler) {
         setProperty(new BooleanProperty(SCHEDULER, scheduler));
     }
 
     /**
      * Get whether scheduler is being used
      *
      * @return true if scheduler is being used
      */
     public boolean getScheduler() {
         return getPropertyAsBoolean(SCHEDULER);
     }
 
     /**
      * Set the absolute StartTime value.
      *
      * @param stime -
      *            the StartTime value.
      */
     public void setStartTime(long stime) {
         setProperty(new LongProperty(START_TIME, stime));
     }
 
     /**
      * Get the absolute start time value.
      *
      * @return the start time value.
      */
     public long getStartTime() {
         return getPropertyAsLong(START_TIME);
     }
 
     /**
      * Get the desired duration of the thread group test run
      *
      * @return the duration (in secs)
      */
     public long getDuration() {
         return getPropertyAsLong(DURATION);
     }
 
     /**
      * Set the desired duration of the thread group test run
      *
      * @param duration
      *            in seconds
      */
     public void setDuration(long duration) {
         setProperty(new LongProperty(DURATION, duration));
     }
 
     /**
      * Get the startup delay
      *
      * @return the delay (in secs)
      */
     public long getDelay() {
         return getPropertyAsLong(DELAY);
     }
 
     /**
      * Set the startup delay
      *
      * @param delay
      *            in seconds
      */
     public void setDelay(long delay) {
         setProperty(new LongProperty(DELAY, delay));
     }
 
     /**
      * Set the EndTime value.
      *
      * @param etime -
      *            the EndTime value.
      */
     public void setEndTime(long etime) {
         setProperty(new LongProperty(END_TIME, etime));
     }
 
     /**
      * Get the end time value.
      *
      * @return the end time value.
      */
     public long getEndTime() {
         return getPropertyAsLong(END_TIME);
     }
 
     /**
      * Set the ramp-up value.
      *
      * @param rampUp
      *            the ramp-up value.
      */
     public void setRampUp(int rampUp) {
         setProperty(new IntegerProperty(RAMP_TIME, rampUp));
     }
 
     /**
      * Get the ramp-up value.
      *
      * @return the ramp-up value.
      */
     public int getRampUp() {
         return getPropertyAsInt(ThreadGroup.RAMP_TIME);
     }
 
     private boolean isDelayedStartup() {
         return getPropertyAsBoolean(DELAYED_START);
     }
 
     /**
      * This will schedule the time for the JMeterThread.
      *
      * @param thread JMeterThread
+     * @param now in milliseconds
      */
     private void scheduleThread(JMeterThread thread, long now) {
 
         // if true the Scheduler is enabled
         if (getScheduler()) {
             // set the start time for the Thread
             if (getDelay() > 0) {// Duration is in seconds
                 thread.setStartTime(getDelay() * 1000 + now);
             } else {
                 long start = getStartTime();
                 if (start < now) {
                     start = now; // Force a sensible start time
                 }                
                 thread.setStartTime(start);
             }
 
             // set the endtime for the Thread
             if (getDuration() > 0) {// Duration is in seconds
                 thread.setEndTime(getDuration() * 1000 + (thread.getStartTime()));
             } else {
                 if( getEndTime() <= now ) {
                     SimpleDateFormat sdf = new SimpleDateFormat(DATE_FIELD_FORMAT);
                     throw new JMeterStopTestException("End Time ("
                             + sdf.format(new Date(getEndTime()))+") of Scheduler for Thread Group "+getName() 
                             + " is in the past, fix value of End Time field");
                 }
                 thread.setEndTime(getEndTime());
             }
 
             // Enables the scheduler
             thread.setScheduled(true);
         }
     }
 
     @Override
-    public void start(int groupCount, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
-        running = true;
+    public void start(int groupNum, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
+        this.running = true;
+        this.groupNumber = groupNum;
+        this.notifier = notifier;
+        this.threadGroupTree = threadGroupTree;
         int numThreads = getNumThreads();
         int rampUpPeriodInSeconds = getRampUp();
         float perThreadDelayInMillis = (float) (rampUpPeriodInSeconds * 1000) / (float) getNumThreads();
 
         delayedStartup = isDelayedStartup(); // Fetch once; needs to stay constant
-        log.info("Starting thread group number " + groupCount
+        log.info("Starting thread group number " + groupNumber
                 + " threads " + numThreads
                 + " ramp-up " + rampUpPeriodInSeconds
                 + " perThread " + perThreadDelayInMillis
                 + " delayedStart=" + delayedStartup);
         if (delayedStartup) {
-            threadStarter = new Thread(new ThreadStarter(groupCount, notifier, threadGroupTree, engine), getName()+"-ThreadStarter");
+            threadStarter = new Thread(new ThreadStarter(notifier, threadGroupTree, engine), getName()+"-ThreadStarter");
             threadStarter.setDaemon(true);
             threadStarter.start();
             // N.B. we don't wait for the thread to complete, as that would prevent parallel TGs
         } else {
             long now = System.currentTimeMillis(); // needs to be same time for all threads in the group
             final JMeterContext context = JMeterContextService.getContext();
-            for (int i = 0; running && i < numThreads; i++) {
-                JMeterThread jmThread = makeThread(groupCount, notifier, threadGroupTree, engine, i, context);
-                scheduleThread(jmThread, now); // set start and end time
-                jmThread.setInitialDelay((int)(i * perThreadDelayInMillis));
-                Thread newThread = new Thread(jmThread, jmThread.getThreadName());
-                registerStartedThread(jmThread, newThread);
-                newThread.start();
+            for (int threadNum = 0; running && threadNum < numThreads; threadNum++) {
+                startNewThread(notifier, threadGroupTree, engine, threadNum, context, now, (int)(threadNum * perThreadDelayInMillis));
             }
         }
-        log.info("Started thread group number "+groupCount);
+        log.info("Started thread group number "+groupNumber);
     }
 
     /**
+     * Start a new {@link JMeterThread} and registers it
+     * @param notifier {@link ListenerNotifier}
+     * @param threadGroupTree {@link ListedHashTree}
+     * @param engine {@link StandardJMeterEngine}
+     * @param threadNum Thread number
+     * @param context {@link JMeterContext}
+     * @param now Nom in milliseconds
+     * @param delay int delay in milliseconds
+     * @return {@link JMeterThread} newly created
+     */
+    private JMeterThread startNewThread(ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine,
+            int threadNum, final JMeterContext context, long now, int delay) {
+        JMeterThread jmThread = makeThread(notifier, threadGroupTree, engine, threadNum, context);
+        scheduleThread(jmThread, now); // set start and end time
+        jmThread.setInitialDelay(delay);
+        Thread newThread = new Thread(jmThread, jmThread.getThreadName());
+        registerStartedThread(jmThread, newThread);
+        newThread.start();
+        return jmThread;
+    }
+    /**
      * Register Thread when it starts
      * @param jMeterThread {@link JMeterThread}
      * @param newThread Thread
      */
     private void registerStartedThread(JMeterThread jMeterThread, Thread newThread) {
         allThreads.put(jMeterThread, newThread);
     }
 
-    private JMeterThread makeThread(int groupCount,
+    /**
+     * Create {@link JMeterThread} cloning threadGroupTree
+     * @param notifier {@link ListenerNotifier}
+     * @param threadGroupTree {@link ListedHashTree}
+     * @param engine {@link StandardJMeterEngine}
+     * @param threadNumber int thread number
+     * @param context {@link JMeterContext}
+     * @return {@link JMeterThread}
+     */
+    private JMeterThread makeThread(
             ListenerNotifier notifier, ListedHashTree threadGroupTree,
-            StandardJMeterEngine engine, int i, 
+            StandardJMeterEngine engine, int threadNumber, 
             JMeterContext context) { // N.B. Context needs to be fetched in the correct thread
         boolean onErrorStopTest = getOnErrorStopTest();
         boolean onErrorStopTestNow = getOnErrorStopTestNow();
         boolean onErrorStopThread = getOnErrorStopThread();
         boolean onErrorStartNextLoop = getOnErrorStartNextLoop();
         String groupName = getName();
         final JMeterThread jmeterThread = new JMeterThread(cloneTree(threadGroupTree), this, notifier);
-        jmeterThread.setThreadNum(i);
+        jmeterThread.setThreadNum(threadNumber);
         jmeterThread.setThreadGroup(this);
         jmeterThread.setInitialContext(context);
-        final String threadName = groupName + " " + (groupCount) + "-" + (i + 1);
+        final String threadName = groupName + " " + groupNumber + "-" + (threadNumber + 1);
         jmeterThread.setThreadName(threadName);
         jmeterThread.setEngine(engine);
         jmeterThread.setOnErrorStopTest(onErrorStopTest);
         jmeterThread.setOnErrorStopTestNow(onErrorStopTestNow);
         jmeterThread.setOnErrorStopThread(onErrorStopThread);
         jmeterThread.setOnErrorStartNextLoop(onErrorStartNextLoop);
         return jmeterThread;
     }
 
+    @Override
+    public JMeterThread addNewThread(int delay, StandardJMeterEngine engine) {
+        long now = System.currentTimeMillis();
+        JMeterContext context = JMeterContextService.getContext();
+        JMeterThread newJmThread;
+        int numThreads;
+        synchronized (addThreadLock) {
+            numThreads = getNumThreads();
+            setNumThreads(numThreads + 1);
+        }
+        newJmThread = startNewThread(notifier, threadGroupTree, engine, numThreads, context, now, delay);
+        JMeterContextService.addTotalThreads( 1 );
+        log.info("Started new thread in group " + groupNumber );
+        return newJmThread;
+    }
+
     /**
      * Stop thread called threadName:
      * <ol>
      *  <li>stop JMeter thread</li>
      *  <li>interrupt JMeter thread</li>
      *  <li>interrupt underlying thread</li>
      * </ol>
      * @param threadName String thread name
      * @param now boolean for stop
      * @return true if thread stopped
      */
     @Override
     public boolean stopThread(String threadName, boolean now) {
         for(Entry<JMeterThread, Thread> entry : allThreads.entrySet()) {
             JMeterThread thrd = entry.getKey();
             if (thrd.getThreadName().equals(threadName)) {
                 stopThread(thrd, entry.getValue(), now);
                 return true;
             }
         }
         return false;
     }
     
     /**
-     * @param thrd JMeterThread
-     * @param t Thread
-     * @param interrupt Interrup thread or not
+     * Hard Stop JMeterThread thrd and interrupt JVM Thread if interrupt is true
+     * @param jmeterThread {@link JMeterThread}
+     * @param jvmThread {@link Thread}
+     * @param interrupt Interrupt thread or not
      */
-    private void stopThread(JMeterThread thrd, Thread t, boolean interrupt) {
-        thrd.stop();
-        thrd.interrupt(); // interrupt sampler if possible
-        if (interrupt && t != null) { // Bug 49734
-            t.interrupt(); // also interrupt JVM thread
+    private void stopThread(JMeterThread jmeterThread, Thread jvmThread, boolean interrupt) {
+        jmeterThread.stop();
+        jmeterThread.interrupt(); // interrupt sampler if possible
+        if (interrupt && jvmThread != null) { // Bug 49734
+            jvmThread.interrupt(); // also interrupt JVM thread
         }
     }
 
     /**
      * Called by JMeterThread when it finishes
      */
     @Override
     public void threadFinished(JMeterThread thread) {
         log.debug("Ending thread " + thread.getThreadName());
         allThreads.remove(thread);
     }
 
     /**
      * For each thread, invoke:
      * <ul> 
      * <li>{@link JMeterThread#stop()} - set stop flag</li>
      * <li>{@link JMeterThread#interrupt()} - interrupt sampler</li>
      * <li>{@link Thread#interrupt()} - interrupt JVM thread</li>
      * </ul> 
      */
     @Override
     public void tellThreadsToStop() {
         running = false;
         if (delayedStartup) {
             try {
                 threadStarter.interrupt();
             } catch (Exception e) {
                 log.warn("Exception occured interrupting ThreadStarter", e);
             }
         }
         
         for (Entry<JMeterThread, Thread> entry : allThreads.entrySet()) {
             stopThread(entry.getKey(), entry.getValue(), true);
         }
     }
 
 
     /**
      * For each thread, invoke:
      * <ul> 
      * <li>{@link JMeterThread#stop()} - set stop flag</li>
      * </ul> 
      */
     @Override
     public void stop() {
         running = false;
         if (delayedStartup) {
             try {
                 threadStarter.interrupt();
             } catch (Exception e) {
                 log.warn("Exception occured interrupting ThreadStarter", e);
             }            
         }
         for (JMeterThread item : allThreads.keySet()) {
             item.stop();
         }
     }
 
     /**
      * @return number of active threads
      */
     @Override
     public int numberOfActiveThreads() {
         return allThreads.size();
     }
 
     /**
      * @return boolean true if all threads stopped
      */
     @Override
     public boolean verifyThreadsStopped() {
         boolean stoppedAll = true;
         if (delayedStartup) {
             stoppedAll = verifyThreadStopped(threadStarter);
         }
         for (Thread t : allThreads.values()) {
             stoppedAll = stoppedAll && verifyThreadStopped(t);
         }
         return stoppedAll;
     }
 
     /**
      * Verify thread stopped and return true if stopped successfully
      * @param thread Thread
      * @return boolean
      */
     private boolean verifyThreadStopped(Thread thread) {
         boolean stopped = true;
         if (thread != null && thread.isAlive()) {
             try {
                 thread.join(WAIT_TO_DIE);
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
             if (thread.isAlive()) {
                 stopped = false;
                 log.warn("Thread won't exit: " + thread.getName());
             }
         }
         return stopped;
     }
 
     /**
      * Wait for all Group Threads to stop
      */
     @Override
     public void waitThreadsStopped() {
         if (delayedStartup) {
             waitThreadStopped(threadStarter);
         }
         for (Thread t : allThreads.values()) {
             waitThreadStopped(t);
         }
     }
 
     /**
      * Wait for thread to stop
      * @param thread Thread
      */
     private void waitThreadStopped(Thread thread) {
         if (thread != null) {
             while (thread.isAlive()) {
                 try {
                     thread.join(WAIT_TO_DIE);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }
             }
         }
     }
 
+    /**
+     * @param tree {@link ListedHashTree}
+     * @return a clone of tree
+     */
     private ListedHashTree cloneTree(ListedHashTree tree) {
         TreeCloner cloner = new TreeCloner(true);
         tree.traverse(cloner);
         return cloner.getClonedTree();
     }
 
+    /**
+     * Pause ms milliseconds
+     * @param ms long milliseconds
+     */
     private void pause(long ms){
         try {
             TimeUnit.MILLISECONDS.sleep(ms);
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
         }
     }
 
     /**
      * Starts Threads using ramp up
      */
     class ThreadStarter implements Runnable {
 
-        private final int groupCount;
         private final ListenerNotifier notifier;
         private final ListedHashTree threadGroupTree;
         private final StandardJMeterEngine engine;
         private final JMeterContext context;
 
-        public ThreadStarter(int groupCount, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
+        public ThreadStarter(ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
             super();
-            this.groupCount = groupCount;
             this.notifier = notifier;
             this.threadGroupTree = threadGroupTree;
             this.engine = engine;
             // Store context from Root Thread to pass it to created threads
             this.context = JMeterContextService.getContext();
             
         }
         
 
         /**
          * Wait for delay with RAMPUP_GRANULARITY
          * @param delay delay in ms
          */
         private void delayBy(long delay) {
             if (delay > 0) {
                 long start = System.currentTimeMillis();
                 long end = start + delay;
                 long now;
                 long pause = RAMPUP_GRANULARITY; // maximum pause to use
                 while(running && (now = System.currentTimeMillis()) < end) {
                     long togo = end - now;
                     if (togo < pause) {
                         pause = togo;
                     }
                     pause(pause); // delay between checks
                 }
             }
         }
         
         @Override
         public void run() {
             try {
                 // Copy in ThreadStarter thread context from calling Thread
                 JMeterContextService.getContext().setVariables(this.context.getVariables());
                 long now = System.currentTimeMillis(); // needs to be constant for all threads
                 long endtime = 0;
                 final boolean usingScheduler = getScheduler();
                 if (usingScheduler) {
                     // set the start time for the Thread
                     if (getDelay() > 0) {// Duration is in seconds
                         delayBy(getDelay() * 1000);
                     } else {
                         long start = getStartTime();
                         if (start >= now) {
                             delayBy(start-now);
                         } 
                         // else start immediately
                     }
                     // set the endtime for the Thread
                     endtime = getDuration();
                     if (endtime > 0) {// Duration is in seconds, starting from when the threads start
                         endtime = endtime *1000 + System.currentTimeMillis();
                     } else {
                         if( getEndTime() <= now ) {
                             SimpleDateFormat sdf = new SimpleDateFormat(DATE_FIELD_FORMAT);
                             throw new JMeterStopTestException("End Time ("
                                     + sdf.format(new Date(getEndTime()))+") of Scheduler for Thread Group "+getName() 
                                     + " is in the past, fix value of End Time field");
                         }
                         endtime = getEndTime();
                     }
                 }
                 final int numThreads = getNumThreads();
                 final int perThreadDelayInMillis = Math.round((float) (getRampUp() * 1000) / (float) numThreads);
-                for (int i = 0; running && i < numThreads; i++) {
-                    if (i > 0) {
+                for (int threadNumber = 0; running && threadNumber < numThreads; threadNumber++) {
+                    if (threadNumber > 0) {
                         pause(perThreadDelayInMillis); // ramp-up delay (except first)
                     }
                     if (usingScheduler && System.currentTimeMillis() > endtime) {
                         break; // no point continuing beyond the end time
                     }
-                    JMeterThread jmThread = makeThread(groupCount, notifier, threadGroupTree, engine, i, context);
+                    JMeterThread jmThread = makeThread(notifier, threadGroupTree, engine, threadNumber, context);
                     jmThread.setInitialDelay(0);   // Already waited
                     if (usingScheduler) {
                         jmThread.setScheduled(true);
                         jmThread.setEndTime(endtime);
                     }
                     Thread newThread = new Thread(jmThread, jmThread.getThreadName());
                     newThread.setDaemon(false); // ThreadStarter is daemon, but we don't want sampler threads to be so too
                     registerStartedThread(jmThread, newThread);
                     newThread.start();
                 }
             } catch (Exception ex) {
                 log.error("An error occured scheduling delay start of threads for Thread Group:"+getName(), ex);
             }
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 011dd54a9..261daaa35 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,296 +1,299 @@
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
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 Fill in some detail.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>Documentation review and improvements for easier startup</li>
 <li>New <a href="usermanual/properties_reference.html">properties reference</a> documentation section</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
+    <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize and Maxime Chassagneux</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to xxx-1.1 (from 0.2)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
+<li>Logan Mauzaize (https://github.com/loganmzz)</li>
+<li>Maxime Chassagneux (https://github.com/max3163)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
 
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads,
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry.
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a>
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a>
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
 
 </section>
 </body>
 </document>
