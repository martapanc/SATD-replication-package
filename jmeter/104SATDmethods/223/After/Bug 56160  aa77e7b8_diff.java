diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index dbbe6e4c0..ba41984f8 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,451 +1,431 @@
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
     private transient ConcurrentMap<TestElement, Object> children = 
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
-        first = true; // TODO should this use setFirst()?
-        TestElement elem;
-        for (int i = 0; i < subControllersAndSamplers.size(); i++) {
-            elem = subControllersAndSamplers.get(i);
-            if (elem instanceof Controller) {
-                ((Controller) elem).initialize();
+        first = true; // TODO should this use setFirst()?        
+        initializeSubControllers();
+    }
+
+    /**
+     * (re)Initializes sub controllers
+     * See Bug 50032
+     */
+    protected void initializeSubControllers() {
+        for (TestElement te : subControllersAndSamplers) {
+            if(te instanceof GenericController) {
+                ((Controller) te).initialize();
             }
         }
     }
 
     /**
      * Resets the controller (called after execution of last child of controller):
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
 
     /**
      * @return true if it's the controller is returning the first of its children
      */
     protected boolean isFirst() {
         return first;
     }
 
     /**
      * If b is true, it means first is reset which means Controller has executed all its children 
      * @param b
      */
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
-        Sampler sampler = null;
-        try {
-            sampler = controller.next();
-        } catch (StackOverflowError soe) {
-            // See bug 50618  Catches a StackOverflowError when a condition returns 
-            // always false (after at least one iteration with return true)
-            log.warn("StackOverflowError detected"); // $NON-NLS-1$
-            throw new NextIsNullException("StackOverflowError detected", soe);
-        }
+        Sampler sampler = controller.next();
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
-     * 
+     * @deprecated replaced by GeneriController#initializeSubControllers
      */
     protected void reInitializeSubController() {
-        boolean wasFlagSet = getThreadContext().setIsReinitializingSubControllers();
-        try {
-            TestElement currentElement = getCurrentElement();
-            if (currentElement != null) {
-                if (currentElement instanceof Sampler) {
-                    nextIsASampler((Sampler) currentElement);
-                } else { // must be a controller
-                    if (nextIsAController((Controller) currentElement) != null) {
-                        reInitializeSubController();
-                    }
-                }
-            }
-        } catch (NextIsNullException e) {
-            // NOOP
-        } finally {
-            if (wasFlagSet) {
-                getThreadContext().unsetIsReinitializingSubControllers();
-            }
-        }
+        initializeSubControllers();
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
     
     protected Object readResolve(){
         iterationListeners =
                 new LinkedList<LoopIterationListener>();
         children = 
                 TestCompiler.IS_USE_STATIC_SET ? null : new ConcurrentHashMap<TestElement, Object>();
         
         subControllersAndSamplers =
                 new ArrayList<TestElement>();
 
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/IfController.java b/src/core/org/apache/jmeter/control/IfController.java
index e240b49a0..49cc446ed 100644
--- a/src/core/org/apache/jmeter/control/IfController.java
+++ b/src/core/org/apache/jmeter/control/IfController.java
@@ -1,209 +1,209 @@
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
 
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.Scriptable;
 
 /**
  *
  *
  * This is a Conditional Controller; it will execute the set of statements
  * (samplers/controllers, etc) while the 'condition' is true.
  * <p>
  * In a programming world - this is equivalant of :
  * <pre>
  * if (condition) {
  *          statements ....
  *          }
  * </pre>
  * In JMeter you may have :
  * <pre> 
  * Thread-Group (set to loop a number of times or indefinitely,
  *    ... Samplers ... (e.g. Counter )
  *    ... Other Controllers ....
  *    ... IfController ( condition set to something like - ${counter}<10)
  *       ... statements to perform if condition is true
  *       ...
  *    ... Other Controllers /Samplers }
  * </pre>
  */
 
 // for unit test code @see TestIfController
 
 public class IfController extends GenericController implements Serializable {
 
     private static final Logger logger = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String CONDITION = "IfController.condition"; //$NON-NLS-1$
 
     private static final String EVALUATE_ALL = "IfController.evaluateAll"; //$NON-NLS-1$
 
     private static final String USE_EXPRESSION = "IfController.useExpression"; //$NON-NLS-1$
 
     /**
      * constructor
      */
     public IfController() {
         super();
     }
 
     /**
      * constructor
      */
     public IfController(String condition) {
         super();
         this.setCondition(condition);
     }
 
     /**
      * Condition Accessor - this is gonna be like ${count}<10
      */
     public void setCondition(String condition) {
         setProperty(new StringProperty(CONDITION, condition));
     }
 
     /**
      * Condition Accessor - this is gonna be like ${count}<10
      */
     public String getCondition() {
         return getPropertyAsString(CONDITION);
     }
 
     /**
      * evaluate the condition clause log error if bad condition
      */
     private boolean evaluateCondition(String cond) {
         logger.debug("    getCondition() : [" + cond + "]");
 
         String resultStr = "";
         boolean result = false;
 
         // now evaluate the condition using JavaScript
         Context cx = Context.enter();
         try {
             Scriptable scope = cx.initStandardObjects(null);
             Object cxResultObject = cx.evaluateString(scope, cond
             /** * conditionString ** */
             , "<cmd>", 1, null);
             resultStr = Context.toString(cxResultObject);
 
             if (resultStr.equals("false")) { //$NON-NLS-1$
                 result = false;
             } else if (resultStr.equals("true")) { //$NON-NLS-1$
                 result = true;
             } else {
                 throw new Exception(" BAD CONDITION :: " + cond + " :: expected true or false");
             }
 
             logger.debug("    >> evaluate Condition -  [ " + cond + "] results is  [" + result + "]");
         } catch (Exception e) {
             logger.error(getName()+": error while processing "+ "[" + cond + "]\n", e);
         } finally {
             Context.exit();
         }
 
         return result;
     }
 
     private static boolean evaluateExpression(String cond) {
         return cond.equalsIgnoreCase("true"); // $NON-NLS-1$
     }
 
     /**
      * This is overriding the parent method. IsDone indicates whether the
      * termination condition is reached. I.e. if the condition evaluates to
      * False - then isDone() returns TRUE
      */
     @Override
     public boolean isDone() {
         // boolean result = true;
         // try {
         // result = !evaluateCondition();
         // } catch (Exception e) {
         // logger.error(e.getMessage(), e);
         // }
         // setDone(true);
         // return result;
         // setDone(false);
         return false;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next() {
         // We should only evalute the condition if it is the first
         // time ( first "iteration" ) we are called.
         // For subsequent calls, we are inside the IfControllerGroup,
         // so then we just pass the control to the next item inside the if control
         boolean result = true;
         if(isEvaluateAll() || isFirst()) {
             result = isUseExpression() ? 
                     evaluateExpression(getCondition())
                     :
                     evaluateCondition(getCondition());
         }
 
         if (result) {
             return super.next();
         }
         // If-test is false, need to re-initialize indexes
         try {
-            reInitializeSubController(); // Bug 50032 - reinitialize current index element for all sub controller
+            initializeSubControllers();
             return nextIsNull();
         } catch (NextIsNullException e1) {
             return null;
         }
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
-        reInitializeSubController();
+        super.initializeSubControllers();
         super.triggerEndOfLoop();
     }
 
     public boolean isEvaluateAll() {
         return getPropertyAsBoolean(EVALUATE_ALL,false);
     }
 
     public void setEvaluateAll(boolean b) {
         setProperty(EVALUATE_ALL,b);
     }
 
     public boolean isUseExpression() {
         return getPropertyAsBoolean(USE_EXPRESSION, false);
     }
 
     public void setUseExpression(boolean selected) {
         setProperty(USE_EXPRESSION, selected, false);
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index e54ce2ae9..ead3b6b4a 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,314 +1,311 @@
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
 
     private static final boolean DEFAULT_VALUE_FOR_INCLUDE_TIMERS = true; // default true for compatibility
 
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
 
     @Override
     protected Object readResolve(){
         super.readResolve();
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
                 // See BUG 55816
                 if (!isIncludeTimers()) {
                     long processingTimeOfLastChild = res.currentTimeInMillis() - prevEndTime;
                     pauseTime += processingTimeOfLastChild;
                 }
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
-            // bug 50032 
-            if (!getThreadContext().isReinitializingSubControllers()) {
-                lnf.notifyListeners(event, pack.getSampleListeners());
-            }
+            lnf.notifyListeners(event, pack.getSampleListeners());
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
         setProperty(INCLUDE_TIMERS, includeTimers, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/JMeterContext.java b/src/core/org/apache/jmeter/threads/JMeterContext.java
index d33588918..3ce577a97 100644
--- a/src/core/org/apache/jmeter/threads/JMeterContext.java
+++ b/src/core/org/apache/jmeter/threads/JMeterContext.java
@@ -1,232 +1,199 @@
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
 
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 
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
 
-    private boolean isReinitSubControllers = false;
-
     private boolean restartNextLoop = false;
 
     private ConcurrentHashMap<String, Object> samplerContext = new ConcurrentHashMap<String, Object>(5);
 
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
         thread = null;
-        isReinitSubControllers = false;
         samplerContext.clear();
     }
 
     /**
      * Gives access to the JMeter variables for the current thread.
      * 
      * @return a pointer to the JMeter variables.
      */
     public JMeterVariables getVariables() {
         return variables;
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
 
     /**
-     * Reset flag indicating listeners should not be notified since reinit of sub 
-     * controllers is being done. See bug 50032 
-     */
-    public void unsetIsReinitializingSubControllers() {
-        if (isReinitSubControllers) {
-            isReinitSubControllers = false;
-        }
-    }
-
-    /**
-     * Set flag indicating listeners should not be notified since reinit of sub 
-     * controllers is being done. See bug 50032 
-     * @return true if it is the first one to set
-     */
-    public boolean setIsReinitializingSubControllers() {
-        if (!isReinitSubControllers) {
-            isReinitSubControllers = true;
-            return true;
-        }
-        return false;
-    }
-
-    /**
-     * @return true if within reinit of Sub Controllers
-     */
-    public boolean isReinitializingSubControllers() {
-        return isReinitSubControllers;
-    }
-
-    /**
      * if set to true a restart of the loop will occurs
      * @param restartNextLoop
      */
     public void setRestartNextLoop(boolean restartNextLoop) {
         this.restartNextLoop = restartNextLoop;
     }
 
     /**
      * a restart of the loop was required ?
      * @return the restartNextLoop
      */
     public boolean isRestartNextLoop() {
         return restartNextLoop;
     }
 
     /**
      * Clean cached data after sample
      */
     public void cleanAfterSample() {
         if(previousResult != null) {
             previousResult.cleanAfterSample();
         }
         samplerContext.clear();
     }
 
     /**
      * Sampler context is cleaned up as soon as Post-Processor have ended
      * @return Context to use within PostProcessors to cache data
      */
     public Map<String, Object> getSamplerContext() {
         return samplerContext;
     }
 }
diff --git a/test/src/org/apache/jmeter/control/TestIfController.java b/test/src/org/apache/jmeter/control/TestIfController.java
index d5a1822b9..0ee93c156 100644
--- a/test/src/org/apache/jmeter/control/TestIfController.java
+++ b/test/src/org/apache/jmeter/control/TestIfController.java
@@ -1,176 +1,293 @@
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
 
+import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.junit.stubs.TestSampler;
+import org.apache.jmeter.modifiers.CounterConfig;
+import org.apache.jmeter.sampler.DebugSampler;
+import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
+import org.apache.jmeter.threads.JMeterContext;
+import org.apache.jmeter.threads.JMeterContextService;
+import org.apache.jmeter.threads.JMeterVariables;
 
 public class TestIfController extends JMeterTestCase {
         public TestIfController(String name) {
             super(name);
         }
+        
+        /**
+         * See Bug 56160
+         * @throws Exception
+         */
+        public void testStackOverflow() throws Exception {
+            LoopController controller = new LoopController();
+            controller.setLoops(1);
+            controller.setContinueForever(false);
+            
+            IfController ifCont = new IfController("true==false");
+            ifCont.setUseExpression(false);
+            ifCont.setEvaluateAll(false);
+            WhileController whileController = new WhileController();
+            whileController.setCondition("${__javaScript(\"true\" != \"false\")}");
+            whileController.addTestElement(new TestSampler("Sample1"));
+            
+
+            controller.addTestElement(ifCont);
+            ifCont.addTestElement(whileController);
+
+            Sampler sampler = null;
+            int counter = 0;
+            controller.initialize();
+            controller.setRunningVersion(true);
+            ifCont.setRunningVersion(true);
+            whileController.setRunningVersion(true);
+
+            try {
+                while ((sampler = controller.next()) != null) {
+                    sampler.sample(null);
+                    counter++;
+                }
+                assertEquals(0, counter);
+            } catch(StackOverflowError e) {
+                fail("Stackoverflow occured in testStackOverflow");
+            }
+        }
+        
+        /**
+         * See Bug 53768
+         * @throws Exception
+         */
+        public void testBug53768() throws Exception {
+            LoopController controller = new LoopController();
+            controller.setLoops(1);
+            controller.setContinueForever(false);
+            
+            Arguments arguments = new Arguments();
+            arguments.addArgument("VAR1", "0", "=");
+            
+            DebugSampler debugSampler1 = new DebugSampler();
+            debugSampler1.setName("VAR1 = ${VAR1}");
+            
+            IfController ifCont = new IfController("true==false");
+            ifCont.setUseExpression(false);
+            ifCont.setEvaluateAll(false);
+            
+            IfController ifCont2 = new IfController("true==true");
+            ifCont2.setUseExpression(false);
+            ifCont2.setEvaluateAll(false);
+            
+            CounterConfig counterConfig = new CounterConfig();
+            counterConfig.setStart(1);
+            counterConfig.setIncrement(1);
+            counterConfig.setVarName("VAR1");
+            
+            DebugSampler debugSampler2 = new DebugSampler();
+            debugSampler2.setName("VAR1 = ${VAR1}");
+
+            controller.addTestElement(arguments);
+            controller.addTestElement(debugSampler1);
+            controller.addTestElement(ifCont);
+            ifCont.addTestElement(ifCont2);
+            ifCont2.addTestElement(counterConfig);
+            controller.addTestElement(debugSampler2);
+            
+            
+
+            controller.initialize();
+            controller.setRunningVersion(true);
+            ifCont.setRunningVersion(true);
+            ifCont2.setRunningVersion(true);
+            counterConfig.setRunningVersion(true);
+            arguments.setRunningVersion(true);
+            debugSampler1.setRunningVersion(true);
+            debugSampler2.setRunningVersion(true);
+            ifCont2.addIterationListener(counterConfig);
+            JMeterVariables vars = new JMeterVariables();
+            JMeterContext jmctx = JMeterContextService.getContext();
+
+            jmctx.setVariables(vars);
+            vars.put("VAR1", "0");
+            try {
+
+                Sampler sampler = controller.next();
+                SampleResult sampleResult1 = sampler.sample(null);
+                assertEquals("0", vars.get("VAR1"));
+                sampler = controller.next();
+                SampleResult sampleResult2 = sampler.sample(null);
+                assertEquals("0", vars.get("VAR1"));
+                
+
+            } catch(StackOverflowError e) {
+                fail("Stackoverflow occured in testStackOverflow");
+            }
+        }
 
         public void testProcessing() throws Exception {
 
             GenericController controller = new GenericController();
 
             controller.addTestElement(new IfController("false==false"));
             controller.addTestElement(new IfController(" \"a\".equals(\"a\")"));
             controller.addTestElement(new IfController("2<100"));
 
             //TODO enable some proper tests!!
             
             /*
              * GenericController sub_1 = new GenericController();
              * sub_1.addTestElement(new IfController("3==3"));
              * controller.addTestElement(sub_1); controller.addTestElement(new
              * IfController("false==true"));
              */
 
             /*
              * GenericController controller = new GenericController();
              * GenericController sub_1 = new GenericController();
              * sub_1.addTestElement(new IfController("10<100"));
              * sub_1.addTestElement(new IfController("true==false"));
              * controller.addTestElement(sub_1); controller.addTestElement(new
              * IfController("false==false"));
              * 
              * IfController sub_2 = new IfController(); sub_2.setCondition( "10<10000");
              * GenericController sub_3 = new GenericController();
              * 
              * sub_2.addTestElement(new IfController( " \"a\".equals(\"a\")" ) );
              * sub_3.addTestElement(new IfController("2>100"));
              * sub_3.addTestElement(new IfController("false==true"));
              * sub_2.addTestElement(sub_3); sub_2.addTestElement(new
              * IfController("2==3")); controller.addTestElement(sub_2);
              */
 
             /*
              * IfController controller = new IfController("12==12");
              * controller.initialize();
              */
 //          TestElement sampler = null;
 //          while ((sampler = controller.next()) != null) {
 //              logger.debug("    ->>>  Gonna assertTrue :" + sampler.getClass().getName() + " Property is   ---->>>"
 //                      + sampler.getName());
 //          }
         }
    
         public void testProcessingTrue() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             ifCont.addTestElement(new TestSampler("Sample2"));
             TestSampler sample3 = new TestSampler("Sample3");            
             ifCont.addTestElement(sample3);
             controller.addTestElement(ifCont);
                         
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
+            controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6);
         }
         
         /**
          * Test false return on sample3 (sample4 doesn't execute)
          * @throws Exception
          */
         public void testEvaluateAllChildrenWithoutSubController() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             controller.addTestElement(ifCont);
             
             ifCont.addTestElement(new TestSampler("Sample2"));
             TestSampler sample3 = new TestSampler("Sample3");            
             ifCont.addTestElement(sample3);
             TestSampler sample4 = new TestSampler("Sample4");
             ifCont.addTestElement(sample4);
             
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
+            controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 if (sampler.getName().equals("Sample3")) {
                     ifCont.setCondition("true==false");
                 }
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6);
         }
         
         /**
          * test 2 loops with a sub generic controller (sample4 doesn't execute)
          * @throws Exception
          */
         public void testEvaluateAllChildrenWithSubController() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             controller.addTestElement(ifCont);
             ifCont.addTestElement(new TestSampler("Sample2"));
             
             GenericController genericCont = new GenericController();
             TestSampler sample3 = new TestSampler("Sample3");            
             genericCont.addTestElement(sample3);
             TestSampler sample4 = new TestSampler("Sample4");
             genericCont.addTestElement(sample4);
             ifCont.addTestElement(genericCont);
             
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
+            controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             genericCont.setRunningVersion(true);
 
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 if (sampler.getName().equals("Sample3")) {
                     ifCont.setCondition("true==false");
                 }
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6); 
         }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index c0c33afcf..256eb0ccb 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,381 +1,376 @@
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
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.12 =================== -->
 
 <h1>Version 2.12</h1>
 
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
 
 <ch_title>Java 8 support</ch_title>
 <p>
 Now, JMeter 2.12 is compliant with Java 8.
 </p>
 
 <ch_category>New Elements</ch_category>
 <ch_title>Critical Section Controller</ch_title>
 <p>The Critical Section Controller allow to serialize the execution of a section in your tree. 
 Only one instance of the section will be executed at the same time during the test.</p>
 <figure width="683" height="240" image="changes/2.12/01_critical_section_controller.png"></figure>
 
 <ch_title>DNS Cache Manager</ch_title>
 <p>The new configuration element <b>DNS Cache Manager</b> allow to improve the testing of CDN (Content Delivery Network) 
 and/or DNS load balancing.</p>
 <figure width="573" height="359" image="changes/2.12/02_dns_cache_manager.png"></figure>
 
 <ch_category>Core Improvements</ch_category>
 
 <ch_title>Smarter Recording of Http Test Plans</ch_title>
 <p>Test Script Recorder has been improved in many ways</p>
 <ul>
     <li>Better matching of Variables in Requests, making Test Script Recorder variabilize your sampler during recording more versatile</li>
     <li>Ability to filter from View Results Tree the Samples that are excluded from recording, this lets you concentrate on recorded Samplers analysis and not bother with useless Sample Results</li>
     <li>Better defaults for recording, since this version Recorder will number created Samplers letting you find them much easily in View Results Tree. Grouping of Samplers under Transaction Controller will
     will be smarter making all requests emitted by a web page be children as new Transaction Controller</li>
 </ul>
 
 <ch_title>Better handling of embedded resources</ch_title>
 <p>When download embedded resources is checked, JMeter now uses User Agent header to download or not resources embedded within conditionnal comments as per <a href="http://msdn.microsoft.com/en-us/library/ms537512%28v=vs.85%29.aspx" target="_blank">About conditional comments</a>.</p>
 
 <ch_title>Ability to customize Cache Manager (Browser cache simulation) handling of cached resources</ch_title>
 <p>You can now configure the behaviour of JMeter when a resource is found in Cache, this can be controlled with <i>cache_manager.cached_resource_mode</i> property</p>
 <figure width="1024" height="314" image="changes/2.12/12_cache_resource_mode.png"></figure>
 
 
 <ch_title>JMS Publisher / JMS Point-to-Point</ch_title>
 <p> Add JMSPriority and JMSExpiration fields for these samplers.</p>
 <figure width="901" height="277" image="changes/2.12/04_jms_publisher.png"></figure>
 
 <figure width="900" height="294" image="changes/2.12/05_jms_point_to_point.png"></figure>
 
 <ch_title>Mail Reader Sampler</ch_title>
 <p>You can now specify the number of messages that want you retrieve (before all messages were retrieved). 
 In addition, you can fetch only the message header now.</p>
 <figure width="814" height="416" image="changes/2.12/03_mail_reader_sampler.png"></figure>
 
 <ch_title>SMTP Sampler</ch_title>
 <p>Adding the Connection timeout and the Read timeout to the <b>SMTP Sampler.</b></p>
 <figure width="796" height="192" image="changes/2.12/06_smtp_sampler.png"></figure>
 
 <ch_title>Synchronizing Timer </ch_title>
 <p>Adding a timeout to define the maximum time to waiting of the group of virtual users.</p>
 <figure width="546" height="144" image="changes/2.12/09_synchronizing_timer.png"></figure>
 
 <ch_category>GUI Improvements</ch_category>
 
 <ch_title>Undo/Redo support</ch_title>
 <p>Undo / Redo has been introduced and allows user to undo/redo changes made on Test Plan Tree. This feature (ALPHA MODE) is disabled by default, to enable it set property <b>undo.history.size=25</b> </p>
 <figure width="1024" height="56" image="changes/2.12/10_undo_redo.png"></figure>
 
 <ch_title>View Results Tree</ch_title>
 <p>Improve the ergonomics of View Results Tree by changing placement of Renderers and allowing custom ordering 
 (with the property <i>view.results.tree.renderers_order</i>).</p>
 <figure width="900" height="329" image="changes/2.12/07_view_results_tree.png"></figure>
 
 <ch_title>Response Time Graph</ch_title>
 <p>Adding the ability for the <b>Response Time Graph</b> listener to save/restore format its settings in/from the jmx file.</p>
 <figure width="997" height="574" image="changes/2.12/08_response_time_graph.png"></figure>
 
 <ch_title>Log Viewer</ch_title>
 <p>Starting with this version, jmeter logs can be viewed in GUI by clicking on Warning icon in the upper right corner. This will unfold the Log Viewer panel and show logs.</p>
 <figure width="1024" height="437" image="changes/2.12/11_log_viewer.png"></figure>
 
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
-<li>Infinite Loop can happen within JMeter (with possible StackOverflow side effect) when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
-A workaround is to add a sampler at the same level as (or superior to) the If Controller.
-For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
-or a Debug Sampler with all fields set to False (to reduce the sample size).
-</li>
-
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
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 <li>Since JMeter 2.12, active threads in all thread groups and active threads in current thread group are saved by default to CSV or XML results, see <bugzilla>57025</bugzilla>. If you want to revert to previous behaviour, set property <b>jmeter.save.saveservice.thread_counts=true</b></li>
 <li>Since JMeter 2.12, Mail Reader Sampler will show 1 for number of samples instead of number of messages retrieved, see <bugzilla>56539</bugzilla></li>
 <li>Since JMeter 2.12, when using Cache Manager, if resource is found in cache no SampleResult will be created, in previous version a SampleResult with empty content and 204 return code was returned, see <bugzilla>54778</bugzilla>.
 You can choose between different ways to handle this case, see cache_manager.cached_resource_mode in jmeter.properties.</li>
 <li>Since JMeter 2.12, Log Viewer will no more clear logs when closed and will have logs available even if closed. See <bugzilla>56920</bugzilla>. Read <a href="./usermanual/hints_and_tips.html#debug_logging">Hints and Tips &gt; Enabling Debug logging</a>
 for details on configuring this component.</li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55998</bugzilla> - HTTP recording – Replacing port value by user defined variable does not work</li>
 <li><bugzilla>56178</bugzilla> - keytool error: Invalid escaped character in AVA: - some characters must be escaped</li>
 <li><bugzilla>56222</bugzilla> - NPE if jmeter.httpclient.strict_rfc2616=true and location is not absolute</li>
 <li><bugzilla>56263</bugzilla> - DefaultSamplerCreator should set BrowserCompatible Multipart true</li>
 <li><bugzilla>56231</bugzilla> - Move redirect location processing from HC3/HC4 samplers to HTTPSamplerBase#followRedirects()</li>
 <li><bugzilla>56207</bugzilla> - URLs get encoded on redirects in HC3.1 &amp; HC4 samplers</li>
 <li><bugzilla>56303</bugzilla> - The width of target controller's combo list should be set to the current panel size, not on label size of the controllers</li>
 <li><bugzilla>54778</bugzilla> - HTTP Sampler should not return 204 when resource is found in Cache, make it configurable with new property cache_manager.cached_resource_mode</li> 
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55977</bugzilla> - JDBC pool keepalive flooding</li>
 <li><bugzilla>55999</bugzilla> - Scroll bar on jms point-to-point sampler does not work when content exceeds display</li>
 <li><bugzilla>56198</bugzilla> - JMSSampler : NullPointerException is thrown when JNDI underlying implementation of JMS provider does not comply with Context.getEnvironment contract</li>
 <li><bugzilla>56428</bugzilla> - MailReaderSampler - should it use mail.pop3s.* properties?</li>
 <li><bugzilla>46932</bugzilla> - Alias given in select statement is not used as column header in response data for a JDBC request.Based on report and analysis of Nicola Ambrosetti</li>
 <li><bugzilla>56539</bugzilla> - Mail reader sampler: When Number of messages to retrieve is superior to 1, Number of samples should only show 1 not the number of messages retrieved</li>
 <li><bugzilla>56809</bugzilla> - JMSSampler closes InitialContext too early. Contributed by Bradford Hovinen (hovinen at gmail.com)</li>
 <li><bugzilla>56761</bugzilla> - JMeter tries to stop already stopped JMS connection and displays "The connection is closed"</li>
 <li><bugzilla>57068</bugzilla> - No error thrown when negative duration is entered in Test Action</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56243</bugzilla> - Foreach works incorrectly with indexes on subsequent iterations </li>
 <li><bugzilla>56276</bugzilla> - Loop controller becomes broken once loop count evaluates to zero </li>
+<li><bugzilla>56160</bugzilla> - StackOverflowError when using WhileController within IfController</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56706</bugzilla> - SampleResult#getResponseDataAsString() does not use encoding in response body impacting PostProcessors and ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57052</bugzilla> - ArithmeticException: / by zero when sampleCount is equal to 0</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56162</bugzilla> -  HTTP Cache Manager should not cache PUT/POST etc.</li>
 <li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 <li><bugzilla>41319</bugzilla> - URLRewritingModifier : Allow Parameter value to be url encoded</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>56111</bugzilla> - "comments" in german translation is not correct</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>56059</bugzilla> - Older TestBeans incompatible with 2.11 when using TextAreaEditor</li>
 <li><bugzilla>56080</bugzilla> - Conversion error com.thoughtworks.xstream.converters.ConversionException with Java 8 Early Access Build</li>
 <li><bugzilla>56182</bugzilla> - Can't trigger bsh script using bshclient.jar; socket is closed unexpectedly </li>
 <li><bugzilla>56360</bugzilla> - HashTree and ListedHashTree fail to compile with Java 8</li>
 <li><bugzilla>56419</bugzilla> - Jmeter silently fails to save results</li>
 <li><bugzilla>56662</bugzilla> - Save as xml in a listener is not remembered</li>
 <li><bugzilla>56367</bugzilla> - JMeter 2.11 on maven central triggers a not existing dependency rsyntaxtextarea 2.5.1, upgrade to 2.5.3</li>
 <li><bugzilla>56743</bugzilla> - Wrong mailing list archives on mail2.xml. Contributed by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56763</bugzilla> - Removing the Oracle icons, not used by JMeter (and missing license)</li>
 <li><bugzilla>54100</bugzilla> - Switching languages fails to preserve toolbar button states (enabled/disabled)</li>
 <li><bugzilla>54648</bugzilla> - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree</li>
 <li><bugzilla>56962</bugzilla> - JMS GUIs should disable all fields affected by jndi.properties checkbox</li>
 <li><bugzilla>57061</bugzilla> - Save as Test Fragment fails to clone deeply selected node. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57075</bugzilla> - BeanInfoSupport.MULTILINE attribute is not processed</li>
 <li><bugzilla>57076</bugzilla> - BooleanPropertyEditor#getAsText() must return a value that is in getTags()</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55959</bugzilla> - Improve error message when Test Script Recorder fails due to I/O problem</li>
 <li><bugzilla>52013</bugzilla> - Test Script Recorder's Child View Results Tree does not take into account Test Script Recorder excluded/included URLs. Based on report and analysis of James Liang</li>
 <li><bugzilla>56119</bugzilla> - File uploads fail every other attempt using timers. Enable idle timeouts for servers that don't send Keep-Alive headers.</li>
 <li><bugzilla>56272</bugzilla> - MirrorServer should support query parameters for status and redirects</li>
 <li><bugzilla>56772</bugzilla> - Handle IE Conditional comments when parsing embedded resources</li>
 <li><bugzilla>57026</bugzilla> - HTTP(S) Test Script Recorder : Better default settings. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>56033</bugzilla> - Add Connection timeout and Read timeout to SMTP Sampler</li>
 <li><bugzilla>56429</bugzilla> - MailReaderSampler - no need to fetch all Messages if not all wanted</li>
 <li><bugzilla>56427</bugzilla> - MailReaderSampler enhancement: read message header only</li>
 <li><bugzilla>56510</bugzilla> - JMS Publisher/Point to Point: Add JMSPriority and JMSExpiration</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56728</bugzilla> - New Critical Section Controller to serialize blocks of a Test. Based partly on a patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56228</bugzilla> - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering</li>
 <li><bugzilla>56349</bugzilla> - "summary" is a bad name for a Generate Summary Results component, documentation clarified</li>
 <li><bugzilla>56769</bugzilla> - Adds the ability for the Response Time Graph listener to save/restore format settings in/from the jmx file</li>
 <li><bugzilla>57025</bugzilla> - SaveService : Better defaults, save thread counts by default</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56691</bugzilla> - Synchronizing Timer : Add timeout on waiting</li>
 <li><bugzilla>56701</bugzilla> - HTTP Authorization Manager/ Kerberos Authentication: add port to SPN when server port is neither 80 nor 443. Based on patches from Dan Haughey (dan.haughey at swinton.co.uk) and Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56841</bugzilla> - New configuration element: DNS Cache Manager to improve the testing of CDN. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 <li><bugzilla>52061</bugzilla> - Allow access to Request Headers in Regex Extractor. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>56708</bugzilla> - __jexl2 doesn't scale with multiple CPU cores. Based on analysis and patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>21695</bugzilla> - Unix jmeter start script assumes it is on PATH, not a link</li>
 <li><bugzilla>56292</bugzilla> - Add the check of the Java's version in startup files and disable some options when is Java v8 engine</li>
 <li><bugzilla>56298</bugzilla> - JSR223 language display does not show which engine will be used</li>
 <li><bugzilla>56455</bugzilla> - Batch files: drop support for non-NT Windows shell scripts</li>
 <li><bugzilla>56807</bugzilla> - Ability to force flush of ResultCollector file. Contributed by Andrey Pohilko (apc4 at ya.ru)</li>
 <li><bugzilla>56921</bugzilla> - Templates : Improve Recording template to ignore embedded resources case and URL parameters. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>42248</bugzilla> - Undo-redo support on Test Plan tree modification. Developed by Andrey Pohilko (apc4 at ya.ru) and contributed by BlazeMeter Ltd. Additional contribution by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>56920</bugzilla> - LogViewer : Make it receive all log events even when it is closed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to commons-lang3 3.3.2 (from 3.1)</li>
 <li>Updated to commons-codec 1.9 (from 1.8)</li>
 <li>Updated to commons-logging 1.2 (from 1.1.3)</li>
 <li>Updated to tika 1.6 (from 1.4)</li>
 <li>Updated to xercesImpl 2.11.0 (from 2.9.1)</li>
 <li>Updated to xml-apis 1.4.01 (from 1.3.04)</li>
 <li>Updated to xstream 1.4.7 (from 1.4.4)</li>
 <li>Updated to jodd 3.6 (from 3.4.10)</li>
 <li>Updated to rsyntaxtextarea 2.5.3 (from 2.5.1)</li>
 <li>Updated xalan and serializer to 2.7.2 (from 2.7.1)</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li>James Liang (jliang at andera.com)</li>
 <li>Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Nicola Ambrosetti (ambrosetti.nicola at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li>Dan Haughey (dan.haughey at swinton.co.uk)</li>
 <li>Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li>Andrey Pohilko (apc4 at ya.ru)</li>
 <li>Bradford Hovinen (hovinen at gmail.com)</li>
 <li><a href="http://blazemeter.com">BlazeMeter Ltd.</a></li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Oliver LLoyd (email at oliverlloyd.com) for his help on <bugzilla>56119</bugzilla></li>
 <li>Vladimir Ryabtsev (greatvovan at gmail.com) for his help on <bugzilla>56243</bugzilla> and <bugzilla>56276</bugzilla></li>
 <li>Adrian Speteanu (asp.adieu at gmail.com) and Matt Kilbride (matt.kilbride at gmail.com) for their feedback and tests on <bugzilla>54648</bugzilla></li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
