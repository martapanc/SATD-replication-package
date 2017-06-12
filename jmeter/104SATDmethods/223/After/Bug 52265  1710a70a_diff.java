diff --git a/src/components/org/apache/jmeter/control/ThroughputController.java b/src/components/org/apache/jmeter/control/ThroughputController.java
index 6cacc035a..1eeca897a 100644
--- a/src/components/org/apache/jmeter/control/ThroughputController.java
+++ b/src/components/org/apache/jmeter/control/ThroughputController.java
@@ -1,278 +1,283 @@
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
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.FloatProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This class represents a controller that can control the number of times that
  * it is executed, either by the total number of times the user wants the
  * controller executed (BYNUMBER) or by the percentage of time it is called
  * (BYPERCENT)
  *
  * The current implementation executes the first N samples (BYNUMBER)
  * or the last N% of samples (BYPERCENT).
  */
 public class ThroughputController extends GenericController implements Serializable, LoopIterationListener,
         TestStateListener {
 
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
     public static final int BYNUMBER = 0;
 
     public static final int BYPERCENT = 1;
 
     private static final String STYLE = "ThroughputController.style";// $NON-NLS-1$
 
     private static final String PERTHREAD = "ThroughputController.perThread";// $NON-NLS-1$
 
     private static final String MAXTHROUGHPUT = "ThroughputController.maxThroughput";// $NON-NLS-1$
 
     private static final String PERCENTTHROUGHPUT = "ThroughputController.percentThroughput";// $NON-NLS-1$
 
     private static class MutableInteger{
         private int integer;
         MutableInteger(int value){
             integer=value;
         }
         int incr(){
             return ++integer;
         }
         public int intValue() {
             return integer;
         }
     }
 
     // These items are shared between threads in a group by the clone() method
     // They are initialised by testStarted() so don't need to be serialised
     private transient MutableInteger globalNumExecutions;
 
     private transient MutableInteger globalIteration;
-    // FIXME Sync on byte is wrong, stupid of me
-    private Byte counterLock = new Byte("0"); // ensure counts are updated correctly
-    // Need to use something that is serializable, so Object is no use
-    // TODO does it need to be serializable? If not, we can use transient Object
+
+    private transient Object counterLock = new Object(); // ensure counts are updated correctly
 
     /**
      * Number of iterations on which we've chosen to deliver samplers.
      */
     private int numExecutions = 0;
 
     /**
      * Index of the current iteration. 0-based.
      */
     private int iteration = -1;
 
     /**
      * Whether to deliver samplers on this iteration.
      */
     private boolean runThisTime;
 
     public ThroughputController() {
         setStyle(BYNUMBER);
         setPerThread(true);
         setMaxThroughput(1);
         setPercentThroughput(100);
         runThisTime = false;
     }
 
     public void setStyle(int style) {
         setProperty(new IntegerProperty(STYLE, style));
     }
 
     public int getStyle() {
         return getPropertyAsInt(STYLE);
     }
 
     public void setPerThread(boolean perThread) {
         setProperty(new BooleanProperty(PERTHREAD, perThread));
     }
 
     public boolean isPerThread() {
         return getPropertyAsBoolean(PERTHREAD);
     }
 
     public void setMaxThroughput(int maxThroughput) {
         setProperty(new IntegerProperty(MAXTHROUGHPUT, maxThroughput));
     }
 
     public void setMaxThroughput(String maxThroughput) {
         setProperty(new StringProperty(MAXTHROUGHPUT, maxThroughput));
     }
 
     public String getMaxThroughput() {
         return getPropertyAsString(MAXTHROUGHPUT);
     }
 
     protected int getMaxThroughputAsInt() {
         JMeterProperty prop = getProperty(MAXTHROUGHPUT);
         int retVal = 1;
         if (prop instanceof IntegerProperty) {
             retVal = ((IntegerProperty) prop).getIntValue();
         } else {
             try {
                 retVal = Integer.parseInt(prop.getStringValue());
             } catch (NumberFormatException e) {
             	log.warn("Error parsing "+prop.getStringValue(),e);
             }
         }
         return retVal;
     }
 
     public void setPercentThroughput(float percentThroughput) {
         setProperty(new FloatProperty(PERCENTTHROUGHPUT, percentThroughput));
     }
 
     public void setPercentThroughput(String percentThroughput) {
         setProperty(new StringProperty(PERCENTTHROUGHPUT, percentThroughput));
     }
 
     public String getPercentThroughput() {
         return getPropertyAsString(PERCENTTHROUGHPUT);
     }
 
     protected float getPercentThroughputAsFloat() {
         JMeterProperty prop = getProperty(PERCENTTHROUGHPUT);
         float retVal = 100;
         if (prop instanceof FloatProperty) {
             retVal = ((FloatProperty) prop).getFloatValue();
         } else {
             try {
                 retVal = Float.parseFloat(prop.getStringValue());
             } catch (NumberFormatException e) {
             	log.warn("Error parsing "+prop.getStringValue(),e);
             }
         }
         return retVal;
     }
 
     private int getExecutions() {
         if (!isPerThread()) {
             synchronized (counterLock) {
                 return globalNumExecutions.intValue();
             }
         }
         return numExecutions;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next() {
         if (runThisTime) {
             return super.next();
         }
         return null;
     }
 
     /**
      * Decide whether to return any samplers on this iteration.
      */
     private boolean decide(int executions, int iterations) {
         if (getStyle() == BYNUMBER) {
             return executions < getMaxThroughputAsInt();
         }
         return (100.0 * executions + 50.0) / (iterations + 1) < getPercentThroughputAsFloat();
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#isDone()
      */
     @Override
     public boolean isDone() {
         if (subControllersAndSamplers.size() == 0) {
             return true;
         } else if (getStyle() == BYNUMBER && getExecutions() >= getMaxThroughputAsInt()
                 && current >= getSubControllers().size()) {
             return true;
         } else {
             return false;
         }
     }
 
     @Override
     public Object clone() {
         ThroughputController clone = (ThroughputController) super.clone();
         clone.numExecutions = numExecutions;
         clone.iteration = iteration;
         clone.runThisTime = false;
         // Ensure global counters and lock are shared across threads in the group
         clone.globalIteration = globalIteration;
         clone.globalNumExecutions = globalNumExecutions;
         clone.counterLock = counterLock;
         return clone;
     }
 
     @Override
     public void iterationStart(LoopIterationEvent iterEvent) {
         if (!isPerThread()) {
             synchronized (counterLock) {
                 globalIteration.incr();
                 runThisTime = decide(globalNumExecutions.intValue(), globalIteration.intValue());
                 if (runThisTime) {
                     globalNumExecutions.incr();
                 }
             }
         } else {
             iteration++;
             runThisTime = decide(numExecutions, iteration);
             if (runThisTime) {
                 numExecutions++;
             }
         }
     }
 
     @Override
     public void testStarted() {
         synchronized (counterLock) {
             globalNumExecutions = new MutableInteger(0);
             globalIteration = new MutableInteger(-1);
         }
     }
 
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     @Override
     public void testEnded() {
     	// NOOP
     }
 
     @Override
     public void testEnded(String host) {
     	// NOOP
     }
+    
+    @Override
+    protected Object readResolve(){
+        super.readResolve();
+        counterLock = new Object();
+        return this;
+    }
 
 }
diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index d366c859f..c93250805 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,432 +1,444 @@
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
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.threads.TestCompiler;
 import org.apache.jmeter.threads.TestCompilerHelper;
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
 public class GenericController extends AbstractTestElement implements Controller, Serializable, TestCompilerHelper {
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient LinkedList<LoopIterationListener> iterationListeners =
         new LinkedList<LoopIterationListener>();
 
     // Only create the map if it is required
-    private transient final ConcurrentMap<TestElement, Object> children = 
+    private transient ConcurrentMap<TestElement, Object> children = 
             TestCompiler.IS_USE_STATIC_SET ? null : new ConcurrentHashMap<TestElement, Object>();
 
     private static final Object DUMMY = new Object();
 
     // May be replaced by RandomOrderController
     protected transient List<TestElement> subControllersAndSamplers =
         new ArrayList<TestElement>();
 
     /**
      * Index of current sub controller or sampler
      */
     protected transient int current;
 
     /**
      * TODO document this
      */
     private transient int iterCount;
     
     /**
      * Controller has ended
      */
     private transient boolean done;
     
     /**
      * First sampler or sub-controller
      */
     private transient boolean first;
 
     /**
      * Creates a Generic Controller
      */
     public GenericController() {
     }
 
     @Override
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
     @Override
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
             // NOOP
         }
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#isDone()
      */
     @Override
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
             throw new NextIsNullException("StackOverflowError detected", soe);
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
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         reInitialize();
     }
 
     /**
      * Called to re-initialize a index of controller's elements (Bug 50032)
      * 
      */
     protected void reInitializeSubController() {
         boolean wasFlagSet = getThreadContext().setIsReinitializingSubControllers();
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
             // NOOP
         } finally {
             if (wasFlagSet) {
                 getThreadContext().unsetIsReinitializingSubControllers();
             }
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
 
     @Override
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
     @Override
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
         LoopIterationEvent event = new LoopIterationEvent(this, getIterCount());
         for (LoopIterationListener item : iterationListeners) {
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
+    
+    protected Object readResolve(){
+        iterationListeners =
+                new LinkedList<LoopIterationListener>();
+        children = 
+                TestCompiler.IS_USE_STATIC_SET ? null : new ConcurrentHashMap<TestElement, Object>();
+        
+        subControllersAndSamplers =
+                new ArrayList<TestElement>();
+
+        return this;
+    }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index cf795479a..55ea593bd 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,305 +1,307 @@
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
 import org.apache.jmeter.threads.JMeterContextService;
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
     
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
     private static final String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
     
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Only used in parent Mode
      */
     private transient TransactionSampler transactionSampler;
     
     /**
      * Only used in NON parent Mode
      */
     private transient ListenerNotifier lnf;
 
     /**
      * Only used in NON parent Mode
      */
     private transient SampleResult res;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int calls;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      * Only used in NON parent Mode
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      * Only used in NON parent Mode
      */
     private transient long prevEndTime;
 
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
-    private Object readResolve(){
+    @Override
+    protected Object readResolve(){
+        super.readResolve();
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
                 res.sampleEnd();
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
                 notifyListeners();
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
      */
     @Override
     public void triggerEndOfLoop() {
         if(!isParent()) {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
                 res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 notifyListeners();
             }
         } else {
             transactionSampler.setTransactionDone();
             // This transaction is done
             transactionSampler = null;
         }
         super.triggerEndOfLoop();
     }
 
     /**
      * Create additional SampleEvent in NON Parent Mode
      */
     protected void notifyListeners() {
         // TODO could these be done earlier (or just once?)
         JMeterContext threadContext = getThreadContext();
         JMeterVariables threadVars = threadContext.getVariables();
         SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
         if (pack == null) {
             // If child of TransactionController is a ThroughputController and TPC does
             // not sample its children, then we will have this
             // TODO Should this be at warn level ?
             log.warn("Could not fetch SamplePackage");
         } else {
             SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
             // We must set res to null now, before sending the event for the transaction,
             // so that we can ignore that event in our sampleOccured method
             res = null;
             // bug 50032 
             if (!getThreadContext().isReinitializingSubControllers()) {
                 lnf.notifyListeners(event, pack.getSampleListeners());
             }
         }
     }
 
     @Override
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
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
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
