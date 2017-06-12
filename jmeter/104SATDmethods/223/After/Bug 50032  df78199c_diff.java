diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index 4160498af..c5078f470 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,378 +1,383 @@
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
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * <p>
  * This class is the basis for all the controllers.
  * It also implements SimpleController.
  * </p>
  * <p>
  * The main entry point is next(), which is called by by JMeterThread as follows:
  * </p>
  * <p>
  * <code>while (running && (sampler = controller.next()) != null)</code>
  * </p>
  */
 public class GenericController extends AbstractTestElement implements Controller, Serializable {
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient LinkedList<LoopIterationListener> iterationListeners =
         new LinkedList<LoopIterationListener>();
 
     // May be replaced by RandomOrderController
     protected transient List<TestElement> subControllersAndSamplers =
         new ArrayList<TestElement>();
 
     protected transient int current;
 
     private transient int iterCount;
 
     private transient boolean done, first;
 
     /**
      * Creates a Generic Controller
      */
     public GenericController() {
     }
 
     public void initialize() {
         resetCurrent();
         resetIterCount();
         done = false; // TODO should this use setDone()?
         first = true; // TODO should this use setFirst()?
         TestElement elem;
         for (int i = 0; i < subControllersAndSamplers.size(); i++) {
             elem = subControllersAndSamplers.get(i);
             if (elem instanceof Controller) {
                 ((Controller) elem).initialize();
             }
         }
     }
 
     /**
      * Resets the controller:
      * <ul>
      * <li>resetCurrent() (i.e. current=0)</li>
      * <li>increment iteration count</li>
      * <li>sets first=true</li>
      * <li>recoverRunningVersion() to set the controller back to the initial state</li>
      * </ul>
      *
      */
     protected void reInitialize() {
         resetCurrent();
         incrementIterCount();
         setFirst(true);
         recoverRunningVersion();
     }
 
     /**
      * <p>
      * Determines the next sampler to be processed.
      * </p>
      *
      * <p>
      * If isDone, returns null.
      * </p>
      *
      * <p>
      * Gets the list element using current pointer.
      * If this is null, calls {@link #nextIsNull()}.
      * </p>
      *
      * <p>
      * If the list element is a sampler, calls {@link #nextIsASampler(Sampler)},
      * otherwise calls {@link #nextIsAController(Controller)}
      * </p>
      *
      * <p>
      * If any of the called methods throws NextIsNullException, returns null,
      * otherwise the value obtained above is returned.
      * </p>
      *
      * @return the next sampler or null
      */
     public Sampler next() {
         fireIterEvents();
         if (log.isDebugEnabled()) {
             log.debug("Calling next on: " + this.getClass().getName());
         }
         if (isDone()) {
             return null;
         }
         Sampler returnValue = null;
         try {
             TestElement currentElement = getCurrentElement();
             setCurrentElement(currentElement);
             if (currentElement == null) {
                 // incrementCurrent();
                 returnValue = nextIsNull();
             } else {
                 if (currentElement instanceof Sampler) {
                     returnValue = nextIsASampler((Sampler) currentElement);
                 } else { // must be a controller
                     returnValue = nextIsAController((Controller) currentElement);
                 }
             }
         } catch (NextIsNullException e) {
         }
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#isDone()
      */
     public boolean isDone() {
         return done;
     }
 
     protected void setDone(boolean done) {
         this.done = done;
     }
 
     protected boolean isFirst() {
         return first;
     }
 
     public void setFirst(boolean b) {
         first = b;
     }
 
     /**
      * Called by next() if the element is a Controller,
      * and returns the next sampler from the controller.
      * If this is null, then updates the current pointer and makes recursive call to next().
      * @param controller
      * @return the next sampler
      * @throws NextIsNullException
      */
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         Sampler sampler = null;
         try {
             sampler = controller.next();
         } catch (StackOverflowError soe) {
             // See bug 50618  Catches a StackOverflowError when a condition returns 
             // always false (after at least one iteration with return true)
             log.warn("StackOverflowError detected"); // $NON-NLS-1$
             throw new NextIsNullException();
         }
         if (sampler == null) {
             currentReturnedNull(controller);
             sampler = next();
         }
         return sampler;
     }
 
     /**
      * Increment the current pointer and return the element.
      * Called by next() if the element is a sampler.
      * (May be overriden by sub-classes).
      *
      * @param element
      * @return input element
      * @throws NextIsNullException
      */
     protected Sampler nextIsASampler(Sampler element) throws NextIsNullException {
         incrementCurrent();
         return element;
     }
 
     /**
      * Called by next() when getCurrentElement() returns null.
      * Reinitialises the controller.
      *
      * @return null (always, for this class)
      * @throws NextIsNullException
      */
     protected Sampler nextIsNull() throws NextIsNullException {
         reInitialize();
         return null;
     }
 
     /**
      * Called to re-initialize a index of controller's elements (Bug 50032)
      * 
      */
     protected void reInitializeSubController() {
+        boolean wasFlagSet = getThreadContext().setIsReinitializingSubControllers();
         try {
             TestElement currentElement = getCurrentElement();
             if (currentElement != null) {
                 if (currentElement instanceof Sampler) {
                     nextIsASampler((Sampler) currentElement);
                 } else { // must be a controller
                     if (nextIsAController((Controller) currentElement) != null) {
                         reInitializeSubController();
                     }
                 }
             }
         } catch (NextIsNullException e) {
+        } finally {
+            if (wasFlagSet) {
+                getThreadContext().unsetIsReinitializingSubControllers();
+            }
         }
     }
     
     /**
      * If the controller is done, remove it from the list,
      * otherwise increment to next entry in list.
      *
      * @param c controller
      */
     protected void currentReturnedNull(Controller c) {
         if (c.isDone()) {
             removeCurrentElement();
         } else {
             incrementCurrent();
         }
     }
 
     /**
      * Gets the SubControllers attribute of the GenericController object
      *
      * @return the SubControllers value
      */
     protected List<TestElement> getSubControllers() {
         return subControllersAndSamplers;
     }
 
     private void addElement(TestElement child) {
         subControllersAndSamplers.add(child);
     }
 
     /**
      * Empty implementation - does nothing.
      *
      * @param currentElement
      * @throws NextIsNullException
      */
     protected void setCurrentElement(TestElement currentElement) throws NextIsNullException {
     }
 
     /**
      * <p>
      * Gets the element indicated by the <code>current</code> index, if one exists,
      * from the <code>subControllersAndSamplers</code> list.
      * </p>
      * <p>
      * If the <code>subControllersAndSamplers</code> list is empty,
      * then set done = true, and throw NextIsNullException.
      * </p>
      * @return the current element - or null if current index too large
      * @throws NextIsNullException if list is empty
      */
     protected TestElement getCurrentElement() throws NextIsNullException {
         if (current < subControllersAndSamplers.size()) {
             return subControllersAndSamplers.get(current);
         }
         if (subControllersAndSamplers.size() == 0) {
             setDone(true);
             throw new NextIsNullException();
         }
         return null;
     }
 
     protected void removeCurrentElement() {
         subControllersAndSamplers.remove(current);
     }
 
     /**
      * Increments the current pointer; called by currentReturnedNull to move the
      * controller on to its next child.
      */
     protected void incrementCurrent() {
         current++;
     }
 
     protected void resetCurrent() {
         current = 0;
     }
 
     @Override
     public void addTestElement(TestElement child) {
         if (child instanceof Controller || child instanceof Sampler) {
             addElement(child);
         }
     }
 
     public void addIterationListener(LoopIterationListener lis) {
         /*
          * A little hack - add each listener to the start of the list - this
          * ensures that the thread running the show is the first listener and
          * can modify certain values before other listeners are called.
          */
         iterationListeners.addFirst(lis);
     }
     
     /**
      * Remove listener
      */
     public void removeIterationListener(LoopIterationListener iterationListener) {
         for (Iterator<LoopIterationListener> iterator = iterationListeners.iterator(); iterator.hasNext();) {
             LoopIterationListener listener = iterator.next();
             if(listener == iterationListener)
             {
                 iterator.remove();
                 break; // can only match once
             }
         }
     }
 
     protected void fireIterEvents() {
         if (isFirst()) {
             fireIterationStart();
             first = false; // TODO - should this use setFirst() ?
         }
     }
 
     protected void fireIterationStart() {
         Iterator<LoopIterationListener> iter = iterationListeners.iterator();
         LoopIterationEvent event = new LoopIterationEvent(this, getIterCount());
         while (iter.hasNext()) {
             LoopIterationListener item = iter.next();
             item.iterationStart(event);
         }
     }
 
     protected int getIterCount() {
         return iterCount;
     }
 
     protected void incrementIterCount() {
         iterCount++;
     }
 
     protected void resetIterCount() {
         iterCount = 0;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index c568b790e..8628e855b 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,252 +1,255 @@
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
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient TransactionSampler transactionSampler;
 
     private transient ListenerNotifier lnf;
 
     private transient SampleResult res;
 
     private transient int calls;
 
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      */
     private transient long prevEndTime;
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
     private final static String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
 
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
 
         Sampler returnValue = super.next();
 
         if (returnValue == null) // Must be the end of the controller
         {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
                  res.sampleEnd();
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
 
                 // TODO could these be done earlier (or just once?)
                 JMeterContext threadContext = getThreadContext();
                 JMeterVariables threadVars = threadContext.getVariables();
 
                 SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
                 if (pack == null) {
                     log.warn("Could not fetch SamplePackage");
                 } else {
                     SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
                     // We must set res to null now, before sending the event for the transaction,
                     // so that we can ignore that event in our sampleOccured method
                     res = null;
-                    lnf.notifyListeners(event, pack.getSampleListeners());
+                    // bug 50032 
+                    if (!getThreadContext().isReinitializingSubControllers()) {
+                        lnf.notifyListeners(event, pack.getSampleListeners());
+                    }
                 }
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
 
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
diff --git a/src/core/org/apache/jmeter/threads/JMeterContext.java b/src/core/org/apache/jmeter/threads/JMeterContext.java
index 7d6e361e3..960eb5c94 100644
--- a/src/core/org/apache/jmeter/threads/JMeterContext.java
+++ b/src/core/org/apache/jmeter/threads/JMeterContext.java
@@ -1,172 +1,205 @@
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
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 
 /**
  * Holds context for a thread.
  * Generated by JMeterContextService.
  * 
  * The class is not thread-safe - it is only intended for use within a single thread.
  */
 public class JMeterContext {
     private JMeterVariables variables;
 
     private SampleResult previousResult;
 
     private Sampler currentSampler;
 
     private Sampler previousSampler;
 
     private boolean samplingStarted;
 
     private StandardJMeterEngine engine;
 
     private JMeterThread thread;
 
     private AbstractThreadGroup threadGroup;
 
     private int threadNum;
 
     private byte[] readBuffer = null;
 
+    private boolean isReinitSubControllers = false;
+
     JMeterContext() {
         clear0();
     }
 
     public void clear() {
         clear0();
     }
 
     private void clear0() {
         variables = null;
         previousResult = null;
         currentSampler = null;
         previousSampler = null;
         samplingStarted = false;
         threadNum = 0;
         readBuffer = null;
         thread = null;
+        isReinitSubControllers = false;
     }
 
     /**
      * Gives access to the JMeter variables for the current thread.
      * 
      * @return a pointer to the JMeter variables.
      */
     public JMeterVariables getVariables() {
         return variables;
     }
 
     /**
      * A temporary buffer that can be shared between samplers in a thread.
      * 
      * @return the shared read buffer
      */
     public byte[] getReadBuffer() {
         if (readBuffer == null) {
             readBuffer = new byte[8192];
         }
         return readBuffer;
     }
 
     public void setVariables(JMeterVariables vars) {
         this.variables = vars;
     }
 
     public SampleResult getPreviousResult() {
         return previousResult;
     }
 
     public void setPreviousResult(SampleResult result) {
         this.previousResult = result;
     }
 
     public Sampler getCurrentSampler() {
         return currentSampler;
     }
 
     public void setCurrentSampler(Sampler sampler) {
         this.previousSampler = currentSampler;
         this.currentSampler = sampler;
     }
 
     /**
      * Returns the previousSampler.
      *
      * @return Sampler
      */
     public Sampler getPreviousSampler() {
         return previousSampler;
     }
 
     /**
      * Returns the threadNum.
      *
      * @return int
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
 
     public JMeterThread getThread() {
         return this.thread;
     }
 
     public void setThread(JMeterThread thread) {
         this.thread = thread;
     }
 
     public AbstractThreadGroup getThreadGroup() {
         return this.threadGroup;
     }
 
     public void setThreadGroup(AbstractThreadGroup threadgrp) {
         this.threadGroup = threadgrp;
     }
 
     public StandardJMeterEngine getEngine() {
         return engine;
     }
 
     public void setEngine(StandardJMeterEngine engine) {
         this.engine = engine;
     }
 
     public boolean isSamplingStarted() {
         return samplingStarted;
     }
 
     public void setSamplingStarted(boolean b) {
         samplingStarted = b;
     }
+
+    /**
+     * Reset flag indicating listeners should not be notified since reinit of sub 
+     * controllers is being done. See bug 50032 
+     */
+    public void unsetIsReinitializingSubControllers() {
+        if (isReinitSubControllers) {
+            isReinitSubControllers = false;
+        }
+    }
+
+    /**
+     * Set flag indicating listeners should not be notified since reinit of sub 
+     * controllers is being done. See bug 50032 
+     * @return true if it is the first one to set
+     */
+    public boolean setIsReinitializingSubControllers() {
+        if (!isReinitSubControllers) {
+            isReinitSubControllers = true;
+            return true;
+        }
+        return false;
+    }
+
+    /**
+     * @return true if within reinit of Sub Controllers
+     */
+    public boolean isReinitializingSubControllers() {
+        return isReinitSubControllers;
+    }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 90eb0720a..6396f6cc0 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,169 +1,167 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h1>Version 2.5.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
-<p>If Controller generates additional samples for children Transaction Controller inside when condition is false.</p>
-
 <p>If Controller may make that JMeter starts a infinite running when the If condition is always
 false from the first iteration.</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 The HttpClient4 sampler as implemented in version 2.5 used a retry count of 3.
 As this can hide server errors, JMeter now sets the retry count to 0 to prevent any automatic retries.
 This can be overridden by setting the JMeter property <b>httpclient4.retrycount</b>.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Fix HttpClient 4 sampler so it reuses HttpClient instances and connections where possible.</li>
 <li>Bug 51750 - Retrieve all embedded resources doesn't follow IFRAME</li>
 <li>Change the default so the HttpClient 4 sampler does not retry</li>
 <li>Bug 51752 - HTTP Cache is broken when using "Retrieve all embedded resources" with concurrent pool</li>
 <li>Bug 39219 - HTTP Server: You can't stop it after File->Open</li>
 <li>Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 50424 - Web Methods drop down list box inconsistent</li>
 <li>Bug 43293 - Java Request fields not cleared when creating new sampler</li>
 <li>Bug 51830 - Webservice Soap Request triggers too many popups when Webservice WSDL URL is down</li>
 <li>WebService(SOAP) request - add a connect timeout to get the wsdl used to populate Web Methods when server doesn't response</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
-<li>If Controller - Fixed a regression introduce by bug 50032 (see bug 50618 too)</li>
+<li>If Controller - Fixed two regressions introduced by bug 50032 (see bug 50618 too)</li>
 <li>If Controller - Catches a StackOverflowError when a condition returns always false (after at least one iteration with return true) See bug 50618</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 48943 - Functions are invoked additional times when used in combination with a Config Element</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>WebService(SOAP) request - add I18N for some labels</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51831 - Cannot disable UDP server or change the maximum UDP port</li>
 <li>Bug 51821 - Add short-cut for Enabling / Disabling (sub)tree or branches in test plan.</li>
 <li>Bug 47921 - Variables not released for GC after JMeterThread exits.</li>
 <li>Bug 51839 - "... end of run" printed prematurely</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 42246 - Need for a 'auto-scroll' option in "View Results Tree" and "Assertion Results"</li>
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
 <li>Bug 51822 - (part 1) save 1 invocation of GuiPackage#getCurrentGui</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 </ul>
 
 </section> 
 </body> 
 </document>
