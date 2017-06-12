diff --git a/src/components/org/apache/jmeter/assertions/BSFAssertion.java b/src/components/org/apache/jmeter/assertions/BSFAssertion.java
index 9067cfa22..8dc44e8ef 100644
--- a/src/components/org/apache/jmeter/assertions/BSFAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/BSFAssertion.java
@@ -1,62 +1,62 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.assertions;
 
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BSFTestElement;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BSFAssertion extends BSFTestElement implements Cloneable, Assertion, TestBean
 {
     private static final Logger log = LoggerFactory.getLogger(BSFAssertion.class);
 
     private static final long serialVersionUID = 235L;
 
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
         BSFManager mgr =null;
         try {
             mgr = getManager();
             mgr.declareBean("SampleResult", response, SampleResult.class);
             mgr.declareBean("AssertionResult", result, AssertionResult.class);
             processFileOrScript(mgr);
             result.setError(false);
         } catch (BSFException e) {
-            log.warn("Problem in BSF script "+e);
+            log.warn("Problem in BSF script {}",e.toString());
             result.setFailure(true);
             result.setError(true);
             result.setFailureMessage(e.toString());
         } finally {
             if(mgr != null) {
                 mgr.terminate();
             }
         }
         return result;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java b/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
index a8b46f4a9..451aa6479 100644
--- a/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
+++ b/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
@@ -1,57 +1,57 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.modifiers;
 
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BSFTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BSFPreProcessor extends BSFTestElement implements Cloneable, PreProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFPreProcessor.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     @Override
     public void process(){
         BSFManager mgr =null;
         try {
             mgr = getManager();
             if (mgr == null) { 
                 return; 
             }
             processFileOrScript(mgr);
         } catch (BSFException e) {
-            log.warn("Problem in BSF script "+e);
+            log.warn("Problem in BSF script. {}", e.toString());
         } finally {
             if (mgr != null) {
                 mgr.terminate();
             }
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java b/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java
index 1cccade6a..80e9d9825 100644
--- a/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java
+++ b/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java
@@ -1,69 +1,69 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.modifiers;
 
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BeanShellPreProcessor extends BeanShellTestElement
     implements Cloneable, PreProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellPreProcessor.class);
 
-    private static final long serialVersionUID = 4;
+    private static final long serialVersionUID = 5;
 
     // can be specified in jmeter.properties
     private static final String INIT_FILE = "beanshell.preprocessor.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
     @Override
     public void process(){
         final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
         if (bshInterpreter == null) {
             log.error("BeanShell not found");
             return;
         }
         JMeterContext jmctx = JMeterContextService.getContext();
         Sampler sam = jmctx.getCurrentSampler();
         try {
             // Add variables for access to context and variables
             bshInterpreter.set("sampler", sam);//$NON-NLS-1$
             processFileOrScript(bshInterpreter);
         } catch (JMeterException e) {
-            log.warn("Problem in BeanShell script "+e);
+            log.warn("Problem in BeanShell script. {}", e.toString());
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/CounterConfig.java b/src/components/org/apache/jmeter/modifiers/CounterConfig.java
index 1b6a9469d..936f96184 100644
--- a/src/components/org/apache/jmeter/modifiers/CounterConfig.java
+++ b/src/components/org/apache/jmeter/modifiers/CounterConfig.java
@@ -1,241 +1,241 @@
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
 
 package org.apache.jmeter.modifiers;
 
 import java.io.Serializable;
 import java.text.DecimalFormat;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Provides a counter per-thread(user) or per-thread group.
  */
 public class CounterConfig extends AbstractTestElement
     implements Serializable, LoopIterationListener, NoThreadClone {
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
     private static final String START = "CounterConfig.start"; // $NON-NLS-1$
 
     private static final String END = "CounterConfig.end"; // $NON-NLS-1$
 
     private static final String INCREMENT = "CounterConfig.incr"; // $NON-NLS-1$
 
     private static final String FORMAT = "CounterConfig.format"; // $NON-NLS-1$
 
     private static final String PER_USER = "CounterConfig.per_user"; // $NON-NLS-1$
 
     private static final String VAR_NAME = "CounterConfig.name"; // $NON-NLS-1$
 
     private static final String RESET_ON_THREAD_GROUP_ITERATION = "CounterConfig.reset_on_tg_iteration"; // $NON-NLS-1$
 
     private static final boolean RESET_ON_THREAD_GROUP_ITERATION_DEFAULT = false;
 
     // This class is not cloned per thread, so this is shared
     //@GuardedBy("this")
     private long globalCounter = Long.MIN_VALUE;
 
     // Used for per-thread/user numbers
     private transient ThreadLocal<Long> perTheadNumber;
 
     // Used for per-thread/user storage of increment in Thread Group Main loop
     private transient ThreadLocal<Long> perTheadLastIterationNumber;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CounterConfig.class);
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         perTheadNumber = new ThreadLocal<Long>() {
             @Override
             protected Long initialValue() {
                 return Long.valueOf(getStart());
             }
         };
         perTheadLastIterationNumber = new ThreadLocal<Long>() {
             @Override
             protected Long initialValue() {
                 return Long.valueOf(1);
             }
         };
     }
 
 
     public CounterConfig() {
         super();
         init();
     }
 
     private Object readResolve(){
         init();
         return this;
     }
     /**
      * @see LoopIterationListener#iterationStart(LoopIterationEvent)
      */
     @Override
     public void iterationStart(LoopIterationEvent event) {
         // Cannot use getThreadContext() as not cloned per thread
         JMeterVariables variables = JMeterContextService.getContext().getVariables();
         long start = getStart();
         long end = getEnd();
         long increment = getIncrement();
         if (!isPerUser()) {
             synchronized (this) {
                 if (globalCounter == Long.MIN_VALUE || globalCounter > end) {
                     globalCounter = start;
                 }
                 variables.put(getVarName(), formatNumber(globalCounter));
                 globalCounter += increment;
             }
         } else {
             long current = perTheadNumber.get().longValue();
             if(isResetOnThreadGroupIteration()) {
                 int iteration = variables.getIteration();
                 Long lastIterationNumber = perTheadLastIterationNumber.get();
                 if(iteration != lastIterationNumber.longValue()) {
                     // reset
                     current = getStart();
                 }
                 perTheadLastIterationNumber.set(Long.valueOf(iteration));
             }
             variables.put(getVarName(), formatNumber(current));
             current += increment;
             if (current > end) {
                 current = start;
             }
             perTheadNumber.set(Long.valueOf(current));
         }
     }
 
     // Use format to create number; if it fails, use the default
     private String formatNumber(long value){
         String format = getFormat();
         if (format != null && format.length() > 0) {
             try {
                 DecimalFormat myFormatter = new DecimalFormat(format);
                 return myFormatter.format(value);
             } catch (IllegalArgumentException ignored) {
-                log.warn("Error formating "+value + " at format:"+format+", using default");
+                log.warn("Error formating {} at format {}, using default", value, format);
             }
         }
         return Long.toString(value);
     }
 
     public void setStart(long start) {
         setProperty(new LongProperty(START, start));
     }
 
     public void setStart(String start) {
         setProperty(START, start);
     }
 
     public long getStart() {
         return getPropertyAsLong(START);
     }
 
     public String getStartAsString() {
         return getPropertyAsString(START);
     }
 
     public void setEnd(long end) {
         setProperty(new LongProperty(END, end));
     }
 
     public void setEnd(String end) {
         setProperty(END, end);
     }
 
     /**
      * @param value boolean indicating if counter must be reset on Thread Group Iteration
      */
     public void setResetOnThreadGroupIteration(boolean value) {
         setProperty(RESET_ON_THREAD_GROUP_ITERATION, value, RESET_ON_THREAD_GROUP_ITERATION_DEFAULT);
     }
 
     /**
      * @return true if counter must be reset on Thread Group Iteration
      */
     public boolean isResetOnThreadGroupIteration() {
         return getPropertyAsBoolean(RESET_ON_THREAD_GROUP_ITERATION, RESET_ON_THREAD_GROUP_ITERATION_DEFAULT);
     }
 
     /**
      *
      * @return counter upper limit (default Long.MAX_VALUE)
      */
     public long getEnd() {
        long propertyAsLong = getPropertyAsLong(END);
        if (propertyAsLong == 0 && "".equals(getProperty(END).getStringValue())) {
           propertyAsLong = Long.MAX_VALUE;
        }
        return propertyAsLong;
     }
 
     public String getEndAsString(){
         return getPropertyAsString(END);
     }
 
     public void setIncrement(long inc) {
         setProperty(new LongProperty(INCREMENT, inc));
     }
 
     public void setIncrement(String incr) {
         setProperty(INCREMENT, incr);
     }
 
     public long getIncrement() {
         return getPropertyAsLong(INCREMENT);
     }
 
     public String getIncrementAsString() {
         return getPropertyAsString(INCREMENT);
     }
 
     public void setIsPerUser(boolean isPer) {
         setProperty(new BooleanProperty(PER_USER, isPer));
     }
 
     public boolean isPerUser() {
         return getPropertyAsBoolean(PER_USER);
     }
 
     public void setVarName(String name) {
         setProperty(VAR_NAME, name);
     }
 
     public String getVarName() {
         return getPropertyAsString(VAR_NAME);
     }
 
     public void setFormat(String format) {
         setProperty(FORMAT, format);
     }
 
     public String getFormat() {
         return getPropertyAsString(FORMAT);
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/JSR223PreProcessor.java b/src/components/org/apache/jmeter/modifiers/JSR223PreProcessor.java
index f52348a8f..9f3f0db60 100644
--- a/src/components/org/apache/jmeter/modifiers/JSR223PreProcessor.java
+++ b/src/components/org/apache/jmeter/modifiers/JSR223PreProcessor.java
@@ -1,52 +1,52 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.modifiers;
 
 import java.io.IOException;
 
 import javax.script.ScriptEngine;
 import javax.script.ScriptException;
 
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JSR223PreProcessor extends JSR223TestElement implements Cloneable, PreProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223PreProcessor.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     @Override
     public void process() {
         try {
             ScriptEngine scriptEngine = getScriptEngine();
             processFileOrScript(scriptEngine, null);
         } catch (ScriptException | IOException e) {
-            log.error("Problem in JSR223 script "+getName(), e);
+            log.error("Problem in JSR223 script, {}", getName(), e);
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/SampleTimeout.java b/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
index 0788cb330..d20d5667b 100644
--- a/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
+++ b/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
@@ -1,193 +1,193 @@
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
 
 package org.apache.jmeter.modifiers;
 
 import java.io.Serializable;
 import java.util.concurrent.Callable;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ScheduledExecutorService;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleMonitor;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * 
  * Sample timeout implementation using Executor threads
  * @since 3.0
  */
 public class SampleTimeout extends AbstractTestElement implements Serializable, ThreadListener, SampleMonitor {
 
-    private static final long serialVersionUID = 1L;
+    private static final long serialVersionUID = 2L;
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleTimeout.class);
 
     private static final String TIMEOUT = "InterruptTimer.timeout"; //$NON-NLS-1$
 
     private ScheduledFuture<?> future;
     
     private final transient ScheduledExecutorService execService;
     
     private final boolean debug;
 
     private static class TPOOLHolder {
         private TPOOLHolder() {
             // NOOP
         }
         static final ScheduledExecutorService EXEC_SERVICE =
                 Executors.newScheduledThreadPool(1,
                         (Runnable r) -> {
                             Thread t = Executors.defaultThreadFactory().newThread(r);
                             t.setDaemon(true); // also ensures that Executor thread is daemon
                             return t;
                         });
     }
 
     private static ScheduledExecutorService getExecutorService() {
         return TPOOLHolder.EXEC_SERVICE;
     }
 
     /**
      * No-arg constructor.
      */
     public SampleTimeout() {
-        debug = LOG.isDebugEnabled();
+        debug = log.isDebugEnabled();
         execService = getExecutorService();
         if (debug) {
-            LOG.debug(whoAmI("InterruptTimer()", this));
+            log.debug(whoAmI("InterruptTimer()", this));
         }
     }
 
     /**
      * Set the timeout for this timer.
      * @param timeout The timeout for this timer
      */
     public void setTimeout(String timeout) {
         setProperty(TIMEOUT, timeout);
     }
 
     /**
      * Get the timeout value for display.
      *
      * @return the timeout value for display.
      */
     public String getTimeout() {
         return getPropertyAsString(TIMEOUT);
     }
 
     @Override
     public void sampleStarting(Sampler sampler) {
         if (debug) {
-            LOG.debug(whoAmI("sampleStarting()", this));
+            log.debug(whoAmI("sampleStarting()", this));
         }
         createTask(sampler);
     }
 
     @Override
     public void sampleEnded(final Sampler sampler) {
         if (debug) {
-            LOG.debug(whoAmI("sampleEnded()", this));
+            log.debug(whoAmI("sampleEnded()", this));
         }
         cancelTask();
     }
 
     private void createTask(final Sampler samp) {
         long timeout = getPropertyAsLong(TIMEOUT); // refetch each time so it can be a variable
         if (timeout <= 0) {
             return;
         }
         if (!(samp instanceof Interruptible)) { // may be applied to a whole test 
             return; // Cannot time out in this case
         }
         final Interruptible sampler = (Interruptible) samp;
 
         Callable<Object> call = () -> {
             long start = System.nanoTime();
             boolean interrupted = sampler.interrupt();
             String elapsed = Double.toString((double)(System.nanoTime()-start)/ 1000000000)+" secs";
             if (interrupted) {
-                LOG.warn("Call Done interrupting " + getInfo(samp) + " took " + elapsed);
+                log.warn("Call Done interrupting {} took {}", getInfo(samp), elapsed);
             } else {
                 if (debug) {
-                    LOG.debug("Call Didn't interrupt: " + getInfo(samp) + " took " + elapsed);
+                    log.debug("Call Didn't interrupt: {} took {}", getInfo(samp), elapsed);
                 }
             }
             return null;
         };
         // schedule the interrupt to occur and save for possible cancellation 
         future = execService.schedule(call, timeout, TimeUnit.MILLISECONDS);
         if (debug) {
-            LOG.debug("Scheduled timer: @" + System.identityHashCode(future) + " " + getInfo(samp));
+            log.debug("Scheduled timer: @{} {}", System.identityHashCode(future), getInfo(samp));
         }
     }
 
     @Override
     public void threadStarted() {
         if (debug) {
-            LOG.debug(whoAmI("threadStarted()", this));
+            log.debug(whoAmI("threadStarted()", this));
         }
      }
 
     @Override
     public void threadFinished() {
         if (debug) {
-            LOG.debug(whoAmI("threadFinished()", this));
+            log.debug(whoAmI("threadFinished()", this));
         }
         cancelTask(); // cancel future if any
      }
 
     /**
      * Provide a description of this class.
      *
      * @return the description of this class.
      */
     @Override
     public String toString() {
         return JMeterUtils.getResString("sample_timeout_memo"); //$NON-NLS-1$
     }
 
     private String whoAmI(String id, TestElement o) {
         return id + " @" + System.identityHashCode(o)+ " '"+ o.getName() + "' " + (debug ?  Thread.currentThread().getName() : "");         
     }
 
     private String getInfo(TestElement o) {
         return whoAmI(o.getClass().getSimpleName(), o); 
     }
 
     private void cancelTask() {
         if (future != null) {
             if (!future.isDone()) {
                 boolean cancelled = future.cancel(false);
                 if (debug) {
-                    LOG.debug("Cancelled timer: @" + System.identityHashCode(future) + " with result " + cancelled);
+                    log.debug("Cancelled timer: @{}  with result {}", System.identityHashCode(future), cancelled);
                 }
             }
             future = null;
         }        
     }
 
 }
diff --git a/src/components/org/apache/jmeter/modifiers/UserParameters.java b/src/components/org/apache/jmeter/modifiers/UserParameters.java
index ced44e4ec..57afab1a1 100644
--- a/src/components/org/apache/jmeter/modifiers/UserParameters.java
+++ b/src/components/org/apache/jmeter/modifiers/UserParameters.java
@@ -1,192 +1,192 @@
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
 
 package org.apache.jmeter.modifiers;
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.LinkedList;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class UserParameters extends AbstractTestElement implements Serializable, PreProcessor, LoopIterationListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(UserParameters.class);
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
     public static final String NAMES = "UserParameters.names";// $NON-NLS-1$
 
     public static final String THREAD_VALUES = "UserParameters.thread_values";// $NON-NLS-1$
 
     public static final String PER_ITERATION = "UserParameters.per_iteration";// $NON-NLS-1$
 
     /**
      * Although the lock appears to be an instance lock, in fact the lock is
      * shared between all threads see the clone() method below
      *
      * The lock ensures that all the variables are processed together, which is
      * important for functions such as __CSVRead and _StringFromFile.
      * But it has a performance drawback.
      */
     private transient Object lock = new Object();
 
     private Object readResolve(){ // Lock object must exist
         lock = new Object();
         return this;
     }
 
     public CollectionProperty getNames() {
         return (CollectionProperty) getProperty(NAMES);
     }
 
     public CollectionProperty getThreadLists() {
         return (CollectionProperty) getProperty(THREAD_VALUES);
     }
 
     /**
      * The list of names of the variables to hold values. This list must come in
      * the same order as the sub lists that are given to
      * {@link #setThreadLists(Collection)}.
      * 
      * @param list
      *            The ordered list of names
      */
     public void setNames(Collection<?> list) {
         setProperty(new CollectionProperty(NAMES, list));
     }
 
     /**
      * The list of names of the variables to hold values. This list must come in
      * the same order as the sub lists that are given to
      * {@link #setThreadLists(CollectionProperty)}.
      * 
      * @param list
      *            The ordered list of names
      */
     public void setNames(CollectionProperty list) {
         setProperty(list);
     }
 
     /**
      * The thread list is a list of lists. Each list within the parent list is a
      * collection of values for a simulated user. As many different sets of
      * values can be supplied in this fashion to cause JMeter to set different
      * values to variables for different test threads.
      * 
      * @param threadLists
      *            The list of lists of values for each user thread
      */
     public void setThreadLists(Collection<?> threadLists) {
         setProperty(new CollectionProperty(THREAD_VALUES, threadLists));
     }
 
     /**
      * The thread list is a list of lists. Each list within the parent list is a
      * collection of values for a simulated user. As many different sets of
      * values can be supplied in this fashion to cause JMeter to set different
      * values to variables for different test threads.
      * 
      * @param threadLists
      *            The list of lists of values for each user thread
      */
     public void setThreadLists(CollectionProperty threadLists) {
         setProperty(threadLists);
     }
 
     private CollectionProperty getValues() {
         CollectionProperty threadValues = (CollectionProperty) getProperty(THREAD_VALUES);
         if (threadValues.size() > 0) {
             return (CollectionProperty) threadValues.get(getThreadContext().getThreadNum() % threadValues.size());
         }
         return new CollectionProperty("noname", new LinkedList<>());
     }
 
     public boolean isPerIteration() {
         return getPropertyAsBoolean(PER_ITERATION);
     }
 
     public void setPerIteration(boolean perIter) {
         setProperty(new BooleanProperty(PER_ITERATION, perIter));
     }
 
     @Override
     public void process() {
         if (log.isDebugEnabled()) {
-            log.debug(Thread.currentThread().getName() + " process " + isPerIteration());//$NON-NLS-1$
+            log.debug("{} process {}", Thread.currentThread().getName(), isPerIteration());//$NON-NLS-1$
         }
         if (!isPerIteration()) {
             setValues();
         }
     }
 
     @SuppressWarnings("SynchronizeOnNonFinalField")
     private void setValues() {
         synchronized (lock) {
             if (log.isDebugEnabled()) {
-                log.debug(Thread.currentThread().getName() + " Running up named: " + getName());//$NON-NLS-1$
+                log.debug("{} Running up named: {}", Thread.currentThread().getName(), getName());//$NON-NLS-1$
             }
             PropertyIterator namesIter = getNames().iterator();
             PropertyIterator valueIter = getValues().iterator();
             JMeterVariables jmvars = getThreadContext().getVariables();
             while (namesIter.hasNext() && valueIter.hasNext()) {
                 String name = namesIter.next().getStringValue();
                 String value = valueIter.next().getStringValue();
                 if (log.isDebugEnabled()) {
-                    log.debug(Thread.currentThread().getName() + " saving variable: " + name + "=" + value);//$NON-NLS-1$
+                    log.debug("{} saving variable: {}={}", Thread.currentThread().getName(), name, value);//$NON-NLS-1$
                 }
                 jmvars.put(name, value);
             }
         }
     }
 
     /**
      * @see LoopIterationListener#iterationStart(LoopIterationEvent)
      */
     @Override
     public void iterationStart(LoopIterationEvent event) {
         if (log.isDebugEnabled()) {
-            log.debug(Thread.currentThread().getName() + " iteration start " + isPerIteration());//$NON-NLS-1$
+            log.debug("{} iteration start {}", Thread.currentThread().getName(), isPerIteration());//$NON-NLS-1$
         }
         if (isPerIteration()) {
             setValues();
         }
     }
 
     /**
      * A new instance is created for each thread group, and the
      * clone() method is then called to create copies for each thread in a
      * thread group. This means that the lock object is common to all instances
      *
      * @see java.lang.Object#clone()
      */
     @Override
     public Object clone() {
         UserParameters up = (UserParameters) super.clone();
         up.lock = lock; // ensure that clones share the same lock object
         return up;
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/gui/UserParametersGui.java b/src/components/org/apache/jmeter/modifiers/gui/UserParametersGui.java
index ea0738d9c..3a1ff6618 100644
--- a/src/components/org/apache/jmeter/modifiers/gui/UserParametersGui.java
+++ b/src/components/org/apache/jmeter/modifiers/gui/UserParametersGui.java
@@ -1,426 +1,426 @@
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
 
 package org.apache.jmeter.modifiers.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FontMetrics;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.DefaultTableCellRenderer;
 import javax.swing.table.JTableHeader;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.modifiers.UserParameters;
 import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class UserParametersGui extends AbstractPreProcessorGui {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(UserParametersGui.class);
 
     private static final String NAME_COL_RESOURCE = "name"; // $NON-NLS-1$
     private static final String USER_COL_RESOURCE = "user"; // $NON-NLS-1$
     private static final String UNDERSCORE = "_"; // $NON-NLS-1$
 
     private JTable paramTable;
 
     private PowerTableModel tableModel;
 
     private int numUserColumns = 1;
 
     private JButton addParameterButton;
     private JButton addUserButton;
     private JButton deleteRowButton;
     private JButton deleteColumnButton;
     private JButton moveRowUpButton;
     private JButton moveRowDownButton;
 
     private JCheckBox perIterationCheck;
 
     private JPanel paramPanel;
 
     public UserParametersGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "user_parameters_title"; // $NON-NLS-1$
     }
 
     @Override
     public void configure(TestElement el) {
         initTableModel();
         paramTable.setModel(tableModel);
         UserParameters params = (UserParameters) el;
         CollectionProperty names = params.getNames();
         CollectionProperty threadValues = params.getThreadLists();
         tableModel.setColumnData(0, (List<?>) names.getObjectValue());
         PropertyIterator iter = threadValues.iterator();
         if (iter.hasNext()) {
             tableModel.setColumnData(1, (List<?>) iter.next().getObjectValue());
         }
         int count = 2;
         while (iter.hasNext()) {
             String colName = getUserColName(count);
             tableModel.addNewColumn(colName, String.class);
             tableModel.setColumnData(count, (List<?>) iter.next().getObjectValue());
             count++;
         }
         setColumnWidths();
         perIterationCheck.setSelected(params.isPerIteration());
         super.configure(el);
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
         UserParameters params = new UserParameters();
         modifyTestElement(params);
         return params;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement params) {
         GuiUtils.stopTableEditing(paramTable);
         UserParameters userParams = (UserParameters) params;
         userParams.setNames(new CollectionProperty(UserParameters.NAMES, tableModel.getColumnData(NAME_COL_RESOURCE)));
         CollectionProperty threadLists = new CollectionProperty(UserParameters.THREAD_VALUES, new ArrayList<>());
         log.debug("making threadlists from gui");
         for (int col = 1; col < tableModel.getColumnCount(); col++) {
             threadLists.addItem(tableModel.getColumnData(getUserColName(col)));
             if (log.isDebugEnabled()) {
-                log.debug("Adding column to threadlist: " + tableModel.getColumnData(getUserColName(col)));
-                log.debug("Threadlists now = " + threadLists);
+                log.debug("Adding column to threadlist: {}", tableModel.getColumnData(getUserColName(col)));
+                log.debug("Threadlists now = {}", threadLists);
             }
         }
         if (log.isDebugEnabled()) {
-            log.debug("In the end, threadlists = " + threadLists);
+            log.debug("In the end, threadlists = {}", threadLists);
         }
         userParams.setThreadLists(threadLists);
         userParams.setPerIteration(perIterationCheck.isSelected());
         super.configureTestElement(params);
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         initTableModel();
         paramTable.setModel(tableModel);
         HeaderAsPropertyRenderer defaultRenderer = new HeaderAsPropertyRenderer(){
             private static final long serialVersionUID = 240L;
 
             @Override
             protected String getText(Object value, int row, int column) {
                 if (column >= 1){ // Don't process the NAME column
                     String val = value.toString();
                     if (val.startsWith(USER_COL_RESOURCE+UNDERSCORE)){
                         return JMeterUtils.getResString(USER_COL_RESOURCE)+val.substring(val.indexOf(UNDERSCORE));
                     }
                 }
                 return super.getText(value, row, column);
             }
         };
         paramTable.getTableHeader().setDefaultRenderer(defaultRenderer);
         perIterationCheck.setSelected(false);
     }
 
     private String getUserColName(int user){
         return USER_COL_RESOURCE+UNDERSCORE+user;
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setBorder(makeBorder());
         setLayout(new BorderLayout());
         JPanel vertPanel = new VerticalPanel();
         vertPanel.add(makeTitlePanel());
 
         perIterationCheck = new JCheckBox(JMeterUtils.getResString("update_per_iter"), true); // $NON-NLS-1$
         Box perIterationPanel = Box.createHorizontalBox();
         perIterationPanel.add(perIterationCheck);
         perIterationPanel.add(Box.createHorizontalGlue());
         vertPanel.add(perIterationPanel);
         add(vertPanel, BorderLayout.NORTH);
 
         add(makeParameterPanel(), BorderLayout.CENTER);
     }
 
     private JPanel makeParameterPanel() {
         JLabel tableLabel = new JLabel(JMeterUtils.getResString("user_parameters_table")); // $NON-NLS-1$
         initTableModel();
         paramTable = new JTable(tableModel);
         paramTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         paramTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         JMeterUtils.applyHiDPI(paramTable);
 
         paramPanel = new JPanel(new BorderLayout());
         paramPanel.add(tableLabel, BorderLayout.NORTH);
         JScrollPane scroll = new JScrollPane(paramTable);
         scroll.setPreferredSize(scroll.getMinimumSize());
         paramPanel.add(scroll, BorderLayout.CENTER);
         paramPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
         return paramPanel;
     }
 
     protected void initTableModel() {
         tableModel = new PowerTableModel(new String[] { NAME_COL_RESOURCE, // $NON-NLS-1$
                 getUserColName(numUserColumns) }, new Class[] { String.class, String.class });
     }
 
     private JPanel makeButtonPanel() {
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new GridLayout(2, 2));
         addParameterButton = new JButton(JMeterUtils.getResString("add_parameter")); // $NON-NLS-1$
         addUserButton = new JButton(JMeterUtils.getResString("add_user")); // $NON-NLS-1$
         deleteRowButton = new JButton(JMeterUtils.getResString("delete_parameter")); // $NON-NLS-1$
         deleteColumnButton = new JButton(JMeterUtils.getResString("delete_user")); // $NON-NLS-1$
         moveRowUpButton = new JButton(JMeterUtils.getResString("up")); // $NON-NLS-1$
         moveRowDownButton = new JButton(JMeterUtils.getResString("down")); // $NON-NLS-1$
         buttonPanel.add(addParameterButton);
         buttonPanel.add(deleteRowButton);
         buttonPanel.add(moveRowUpButton);
         buttonPanel.add(addUserButton);
         buttonPanel.add(deleteColumnButton);
         buttonPanel.add(moveRowDownButton);
         addParameterButton.addActionListener(new AddParamAction());
         addUserButton.addActionListener(new AddUserAction());
         deleteRowButton.addActionListener(new DeleteRowAction());
         deleteColumnButton.addActionListener(new DeleteColumnAction());
         moveRowUpButton.addActionListener(new MoveRowUpAction());
         moveRowDownButton.addActionListener(new MoveRowDownAction());
         return buttonPanel;
     }
 
     /**
      * Set Column size
      */
     private void setColumnWidths() {
         int margin = 10;
         int minwidth = 150;
 
         JTableHeader tableHeader = paramTable.getTableHeader();
         FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
 
         for (int i = 0; i < tableModel.getColumnCount(); i++) {
             int headerWidth = headerFontMetrics.stringWidth(paramTable.getColumnName(i));
             int maxWidth = getMaximalRequiredColumnWidth(i, headerWidth);
 
             paramTable.getColumnModel().getColumn(i).setPreferredWidth(Math.max(maxWidth + margin, minwidth));
         }
     }
 
     /**
      * Compute max width between width of the largest column at columnIndex and headerWidth
      * @param columnIndex Column index
      * @param headerWidth Header width based on Font
      */
     private int getMaximalRequiredColumnWidth(int columnIndex, int headerWidth) {
         int maxWidth = headerWidth;
 
         TableColumn column = paramTable.getColumnModel().getColumn(columnIndex);
 
         TableCellRenderer cellRenderer = column.getCellRenderer();
 
         if(cellRenderer == null) {
             cellRenderer = new DefaultTableCellRenderer();
         }
 
         for(int row = 0; row < paramTable.getModel().getRowCount(); row++) {
             Component rendererComponent = cellRenderer.getTableCellRendererComponent(paramTable,
                 paramTable.getModel().getValueAt(row, columnIndex),
                 false,
                 false,
                 row,
                 columnIndex);
 
             double valueWidth = rendererComponent.getPreferredSize().getWidth();
 
             maxWidth = (int) Math.max(maxWidth, valueWidth);
         }
 
         return maxWidth;
     }
 
     private class AddParamAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.stopTableEditing(paramTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             // Enable DELETE (which may already be enabled, but it won't hurt)
             deleteRowButton.setEnabled(true);
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             paramTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         }
     }
 
     private class AddUserAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
 
             GuiUtils.stopTableEditing(paramTable);
 
             tableModel.addNewColumn(getUserColName(tableModel.getColumnCount()), String.class);
             tableModel.fireTableDataChanged();
 
             setColumnWidths();
             // Enable DELETE (which may already be enabled, but it won't hurt)
             deleteColumnButton.setEnabled(true);
 
             // Highlight (select) the appropriate row.
             int colToSelect = tableModel.getColumnCount() - 1;
             paramTable.setColumnSelectionInterval(colToSelect, colToSelect);
         }
     }
 
     private class DeleteRowAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.cancelEditing(paramTable);
 
             int[] rowsSelected = paramTable.getSelectedRows();
             if (rowsSelected.length > 0) {
                 for (int i = rowsSelected.length - 1; i >= 0; i--) {
                     tableModel.removeRow(rowsSelected[i]);
                 }
                 tableModel.fireTableDataChanged();
 
                 // Disable DELETE if there are no rows in the table to delete.
                 if (tableModel.getRowCount() == 0) {
                     deleteRowButton.setEnabled(false);
                 }
             }
         }
     }
 
     private class DeleteColumnAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.cancelEditing(paramTable);
 
             int colSelected = paramTable.getSelectedColumn();
             if (colSelected == 0 || colSelected == 1) {
                 JOptionPane.showMessageDialog(null,
                         JMeterUtils.getResString("column_delete_disallowed"), // $NON-NLS-1$
                         "Error",
                         JOptionPane.ERROR_MESSAGE);
                 return;
             }
             if (colSelected >= 0) {
                 tableModel.removeColumn(colSelected);
                 tableModel.fireTableDataChanged();
 
                 // Disable DELETE if there are no rows in the table to delete.
                 if (tableModel.getColumnCount() == 0) {
                     deleteColumnButton.setEnabled(false);
                 }
 
                 // Table still contains one or more rows, so highlight (select)
                 // the appropriate one.
                 else {
 
                     if (colSelected >= tableModel.getColumnCount()) {
                         colSelected = colSelected - 1;
                     }
 
                     paramTable.setColumnSelectionInterval(colSelected, colSelected);
                 }
                 setColumnWidths();
             }
         }
     }
 
     private class MoveRowUpAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             int[] rowsSelected = paramTable.getSelectedRows();
             GuiUtils.stopTableEditing(paramTable);
 
             if (rowsSelected.length > 0 && rowsSelected[0] > 0) {
                 for (int rowSelected : rowsSelected) {
                     tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected - 1);
                 }
 
                 for (int rowSelected : rowsSelected) {
                     paramTable.addRowSelectionInterval(rowSelected - 1, rowSelected - 1);
                 }
             }
         }
     }
 
     private class MoveRowDownAction implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             int[] rowsSelected = paramTable.getSelectedRows();
             GuiUtils.stopTableEditing(paramTable);
 
             if (rowsSelected.length > 0 && rowsSelected[rowsSelected.length - 1] < paramTable.getRowCount() - 1) {
                 for (int i = rowsSelected.length - 1; i >= 0; i--) {
                     int rowSelected = rowsSelected[i];
                     tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected + 1);
                 }
                 for (int rowSelected : rowsSelected) {
                     paramTable.addRowSelectionInterval(rowSelected + 1, rowSelected + 1);
                 }
             }
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/reporters/MailerModel.java b/src/components/org/apache/jmeter/reporters/MailerModel.java
index f3c0f41e0..6737df16e 100644
--- a/src/components/org/apache/jmeter/reporters/MailerModel.java
+++ b/src/components/org/apache/jmeter/reporters/MailerModel.java
@@ -1,515 +1,515 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import javax.mail.Authenticator;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.PasswordAuthentication;
 import javax.mail.Session;
 import javax.mail.Transport;
 import javax.mail.internet.AddressException;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeMessage;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The model for a MailerVisualizer.
  *
  */
 public class MailerModel extends AbstractTestElement implements Serializable {
     public enum MailAuthType {
         SSL,
         TLS,
         NONE
     }
 
-    private static final long serialVersionUID = 270L;
+    private static final long serialVersionUID = 271L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MailerModel.class);
 
     private static final String MAIL_SMTP_HOST = "mail.smtp.host"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_PORT = "mail.smtp.port"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_AUTH = "mail.smtp.auth"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class"; //$NON-NLS-1$
 
     private static final String MAIL_SMTP_STARTTLS = "mail.smtp.starttls.enable"; //$NON-NLS-1$
 
     private long failureCount = 0;
 
     private long successCount = 0;
 
     private boolean failureMsgSent = false;
 
     private boolean siteDown = false;
 
     private boolean successMsgSent = false;
 
     private static final String FROM_KEY = "MailerModel.fromAddress"; //$NON-NLS-1$
 
     private static final String TO_KEY = "MailerModel.addressie"; //$NON-NLS-1$
 
     private static final String HOST_KEY = "MailerModel.smtpHost"; //$NON-NLS-1$
 
     private static final String PORT_KEY = "MailerModel.smtpPort"; //$NON-NLS-1$
 
     private static final String SUCCESS_SUBJECT = "MailerModel.successSubject"; //$NON-NLS-1$
 
     private static final String FAILURE_SUBJECT = "MailerModel.failureSubject"; //$NON-NLS-1$
 
     private static final String FAILURE_LIMIT_KEY = "MailerModel.failureLimit"; //$NON-NLS-1$
 
     private static final String SUCCESS_LIMIT_KEY = "MailerModel.successLimit"; //$NON-NLS-1$
 
     private static final String LOGIN = "MailerModel.login"; //$NON-NLS-1$
 
     private static final String PASSWORD = "MailerModel.password"; //$NON-NLS-1$ NOSONAR no hardcoded password
 
     private static final String MAIL_AUTH_TYPE = "MailerModel.authType"; //$NON-NLS-1$
 
     private static final String DEFAULT_LIMIT = "2"; //$NON-NLS-1$
 
     private static final String DEFAULT_SMTP_PORT = "25";
 
     private static final String DEFAULT_PASSWORD_VALUE = ""; //$NON-NLS-1$ NOSONAR no hardcoded password
 
     private static final String DEFAULT_MAIL_AUTH_TYPE_VALUE = MailAuthType.NONE.toString(); //$NON-NLS-1$
 
     private static final String DEFAULT_LOGIN_VALUE = ""; //$NON-NLS-1$
 
     /** The listener for changes. */
     private transient ChangeListener changeListener;
 
     /**
      * Constructs a MailerModel.
      */
     public MailerModel() {
         super();
 
         setProperty(SUCCESS_LIMIT_KEY, JMeterUtils.getPropDefault("mailer.successlimit", DEFAULT_LIMIT)); //$NON-NLS-1$
         setProperty(FAILURE_LIMIT_KEY, JMeterUtils.getPropDefault("mailer.failurelimit", DEFAULT_LIMIT)); //$NON-NLS-1$
     }
 
     public void addChangeListener(ChangeListener list) {
         changeListener = list;
     }
 
     /** {@inheritDoc} */
     @Override
     public Object clone() {
         MailerModel m = (MailerModel) super.clone();
         m.changeListener = changeListener;
         return m;
     }
 
     public void notifyChangeListeners() {
         if (changeListener != null) {
             changeListener.stateChanged(new ChangeEvent(this));
         }
     }
 
     /**
      * Gets a List of String-objects. Each String is one mail-address of the
      * addresses-String set by <code>setToAddress(str)</code>. The addresses
      * must be seperated by commas. Only String-objects containing a "@" are
      * added to the returned List.
      *
      * @return a List of String-objects wherein each String represents a
      *         mail-address.
      */
     public List<String> getAddressList() {
         String addressees = getToAddress();
         List<String> addressList = new ArrayList<>();
 
         if (addressees != null) {
 
             StringTokenizer next = new StringTokenizer(addressees, ","); //$NON-NLS-1$
 
             while (next.hasMoreTokens()) {
                 String theToken = next.nextToken().trim();
 
                 if (theToken.indexOf('@') > 0) { //NOSONAR $NON-NLS-1$ 
                     addressList.add(theToken);
                 } else {
-                    log.warn("Ignored unexpected e-mail address: "+theToken);
+                    log.warn("Ignored unexpected e-mail address: {}", theToken);
                 }
             }
         }
 
         return addressList;
     }
 
     /**
      * Adds a SampleResult for display in the Visualizer.
      *
      * @param sample
      *            the SampleResult encapsulating informations about the last
      *            sample.
      */
     public void add(SampleResult sample) {
         add(sample, false);
     }
 
     /**
      * Adds a SampleResult. If SampleResult represents a change concerning the
      * failure/success of the sampling a message might be sent to the addressies
      * according to the settings of <code>successCount</code> and
      * <code>failureCount</code>.
      *
      * @param sample
      *            the SampleResult encapsulating information about the last
      *            sample.
      * @param sendMails whether or not to send e-mails
      */
     public synchronized void add(SampleResult sample, boolean sendMails) {
 
         // -1 is the code for a failed sample.
         //
         if (!sample.isSuccessful()) {
             failureCount++;
             successCount = 0;
         } else {
             successCount++;
         }
 
         if (sendMails && (failureCount > getFailureLimit()) && !siteDown && !failureMsgSent) {
             // Send the mail ...
             List<String> addressList = getAddressList();
 
             if (!addressList.isEmpty()) {
                 try {
                     sendMail(getFromAddress(), addressList, getFailureSubject(), "URL Failed: "
                             + sample.getSampleLabel(), getSmtpHost(),
                             getSmtpPort(), getLogin(), getPassword(),
                             getMailAuthType(), false);
                 } catch (Exception e) {
                     log.error("Problem sending mail: "+e);
                 }
                 siteDown = true;
                 failureMsgSent = true;
                 successCount = 0;
                 successMsgSent = false;
             }
         }
 
         if (sendMails && siteDown && (sample.getTime() != -1) && !successMsgSent && successCount > getSuccessLimit()) {
             List<String> addressList = getAddressList();
             try {
                 sendMail(getFromAddress(), addressList, getSuccessSubject(), "URL Restarted: "
                         + sample.getSampleLabel(), getSmtpHost(),
                         getSmtpPort(), getLogin(), getPassword(),
                         getMailAuthType(), false);
             } catch (Exception e) {
                 log.error("Problem sending mail", e);
             }
             siteDown = false;
             successMsgSent = true;
             failureCount = 0;
             failureMsgSent = false;
         }
 
         if (successMsgSent && failureMsgSent) {
             clear();
         }
         notifyChangeListeners();
     }
 
 
 
     /**
      * Resets the state of this object to its default. But: This method does not
      * reset any mail-specific attributes (like sender, mail-subject...) since
      * they are independent of the sampling.
      */
     @Override
     public synchronized void clear() {
         failureCount = 0;
         successCount = 0;
         siteDown = false;
         successMsgSent = false;
         failureMsgSent = false;
         notifyChangeListeners();
     }
 
     /**
      * Returns a String-representation of this object. Returns always
      * "E-Mail-Notification". Might be enhanced in future versions to return
      * some kind of String-representation of the mail-parameters (like sender,
      * addressies, smtpHost...).
      *
      * @return A String-representation of this object.
      */
     @Override
     public String toString() {
         return "E-Mail Notification";
     }
 
     /**
      * Sends a mail with the given parameters using SMTP.
      *
      * @param from
      *            the sender of the mail as shown in the mail-client.
      * @param vEmails
      *            all receivers of the mail. The receivers are seperated by
      *            commas.
      * @param subject
      *            the subject of the mail.
      * @param attText
      *            the message-body.
      * @param smtpHost
      *            the smtp-server used to send the mail.
      * @throws MessagingException
      *             if the building of the message fails
      * @throws AddressException
      *             if any of the addresses is wrong
      */
     public void sendMail(String from, List<String> vEmails, String subject, String attText, String smtpHost)
             throws MessagingException {
         sendMail(from, vEmails, subject, attText, smtpHost, DEFAULT_SMTP_PORT, null, null, null, false);
     }
 
     /**
      * Sends a mail with the given parameters using SMTP.
      *
      * @param from
      *            the sender of the mail as shown in the mail-client.
      * @param vEmails
      *            all receivers of the mail. The receivers are seperated by
      *            commas.
      * @param subject
      *            the subject of the mail.
      * @param attText
      *            the message-body.
      * @param smtpHost
      *            the smtp-server used to send the mail.
      * @param smtpPort the smtp-server port used to send the mail.
      * @param user the login used to authenticate
      * @param password the password used to authenticate
      * @param mailAuthType {@link MailAuthType} Security policy
      * @param debug Flag whether debug messages for the mail session should be generated
      * @throws AddressException If mail address is wrong
      * @throws MessagingException If building MimeMessage fails
      */
     public void sendMail(String from, List<String> vEmails, String subject,
             String attText, String smtpHost,
             String smtpPort,
             final String user,
             final String password,
             MailAuthType mailAuthType,
             boolean debug)
             throws MessagingException{
 
         InternetAddress[] address = new InternetAddress[vEmails.size()];
 
         for (int k = 0; k < vEmails.size(); k++) {
             address[k] = new InternetAddress(vEmails.get(k));
         }
 
         // create some properties and get the default Session
         Properties props = new Properties();
 
         props.put(MAIL_SMTP_HOST, smtpHost);
         props.put(MAIL_SMTP_PORT, smtpPort); // property values are strings
         Authenticator authenticator = null;
         if(mailAuthType != MailAuthType.NONE) {
             props.put(MAIL_SMTP_AUTH, "true");
             switch (mailAuthType) {
                 case SSL:
                     props.put(MAIL_SMTP_SOCKETFACTORY_CLASS,
                             "javax.net.ssl.SSLSocketFactory");
                     break;
                 case TLS:
                     props.put(MAIL_SMTP_STARTTLS,
                             "true");
                     break;
 
                 default:
                     break;
                 }
         }
 
         if(!StringUtils.isEmpty(user)) {
             authenticator =
                     new javax.mail.Authenticator() {
                         @Override
                         protected PasswordAuthentication getPasswordAuthentication() {
                             return new PasswordAuthentication(user,password);
                         }
                     };
         }
         Session session = Session.getInstance(props, authenticator);
         session.setDebug(debug);
 
         // create a message
         Message msg = new MimeMessage(session);
 
         msg.setFrom(new InternetAddress(from));
         msg.setRecipients(Message.RecipientType.TO, address);
         msg.setSubject(subject);
         msg.setText(attText);
         Transport.send(msg);
     }
 
     /**
      * Send a Test Mail to check configuration
      * @throws AddressException If mail address is wrong
      * @throws MessagingException If building MimeMessage fails
      */
     public synchronized void sendTestMail() throws MessagingException {
         String to = getToAddress();
         String from = getFromAddress();
         String subject = "Testing mail-addresses";
         String smtpHost = getSmtpHost();
         String attText = "JMeter-Testmail" + "\n" + "To:  " + to + "\n" + "From: " + from + "\n" + "Via:  " + smtpHost
                 + "\n" + "Fail Subject:  " + getFailureSubject() + "\n" + "Success Subject:  " + getSuccessSubject();
 
         log.info(attText);
 
         sendMail(from, getAddressList(), subject, attText, smtpHost,
                 getSmtpPort(),
                 getLogin(),
                 getPassword(),
                 getMailAuthType(),
                 true);
         log.info("Test mail sent successfully!!");
     }
 
     // ////////////////////////////////////////////////////////////
     //
     // setter/getter - JavaDoc-Comments not needed...
     //
     // ////////////////////////////////////////////////////////////
 
     public void setToAddress(String str) {
         setProperty(TO_KEY, str);
     }
 
     public void setFromAddress(String str) {
         setProperty(FROM_KEY, str);
     }
 
     public void setSmtpHost(String str) {
         setProperty(HOST_KEY, str);
     }
 
     public void setSmtpPort(String value) {
         if(StringUtils.isEmpty(value)) {
             value = DEFAULT_SMTP_PORT;
         }
         setProperty(PORT_KEY, value, DEFAULT_SMTP_PORT);
     }
 
     public void setLogin(String login) {
         setProperty(LOGIN, login, DEFAULT_LOGIN_VALUE);
     }
 
     public void setPassword(String password) {
         setProperty(PASSWORD, password, DEFAULT_PASSWORD_VALUE);
     }
 
     public void setMailAuthType(String value) {
         setProperty(MAIL_AUTH_TYPE, value, DEFAULT_MAIL_AUTH_TYPE_VALUE);
     }
 
     public void setFailureSubject(String str) {
         setProperty(FAILURE_SUBJECT, str);
     }
 
     public void setSuccessSubject(String str) {
         setProperty(SUCCESS_SUBJECT, str);
     }
 
     public void setSuccessLimit(String limit) {
         setProperty(SUCCESS_LIMIT_KEY, limit);
     }
 
     public void setFailureLimit(String limit) {
         setProperty(FAILURE_LIMIT_KEY, limit);
     }
 
     public String getToAddress() {
         return getPropertyAsString(TO_KEY);
     }
 
     public String getFromAddress() {
         return getPropertyAsString(FROM_KEY);
     }
 
     public String getSmtpHost() {
         return getPropertyAsString(HOST_KEY);
     }
 
     public String getSmtpPort() {
         return getPropertyAsString(PORT_KEY, DEFAULT_SMTP_PORT);
     }
 
     public String getFailureSubject() {
         return getPropertyAsString(FAILURE_SUBJECT);
     }
 
     public String getSuccessSubject() {
         return getPropertyAsString(SUCCESS_SUBJECT);
     }
 
     public long getSuccessLimit() {
         return getPropertyAsLong(SUCCESS_LIMIT_KEY);
     }
 
     public long getSuccessCount() {
         return successCount;
     }
 
     public long getFailureLimit() {
         return getPropertyAsLong(FAILURE_LIMIT_KEY);
     }
 
     public long getFailureCount() {
         return this.failureCount;
     }
 
     public String getLogin() {
         return getPropertyAsString(LOGIN, DEFAULT_LOGIN_VALUE);
     }
 
     public String getPassword() {
         return getPropertyAsString(PASSWORD, DEFAULT_PASSWORD_VALUE);
     }
 
     public MailAuthType getMailAuthType() {
         String authType = getPropertyAsString(MAIL_AUTH_TYPE, DEFAULT_MAIL_AUTH_TYPE_VALUE);
         return MailAuthType.valueOf(authType);
     }
 }
diff --git a/src/components/org/apache/jmeter/sampler/TestAction.java b/src/components/org/apache/jmeter/sampler/TestAction.java
index ec7f9d2fd..b9b3faa53 100644
--- a/src/components/org/apache/jmeter/sampler/TestAction.java
+++ b/src/components/org/apache/jmeter/sampler/TestAction.java
@@ -1,171 +1,171 @@
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
 package org.apache.jmeter.sampler;
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Dummy Sampler used to pause or stop a thread or the test;
  * intended for use in Conditional Controllers.
  *
  */
 public class TestAction extends AbstractSampler implements Interruptible {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestAction.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));
 
     // Actions
     public static final int STOP = 0;
     public static final int PAUSE = 1;
     public static final int STOP_NOW = 2;
     public static final int RESTART_NEXT_LOOP = 3;
 
     // Action targets
     public static final int THREAD = 0;
     public static final int TEST = 2;
 
     // Identifiers
     private static final String TARGET = "ActionProcessor.target"; //$NON-NLS-1$
     private static final String ACTION = "ActionProcessor.action"; //$NON-NLS-1$
     private static final String DURATION = "ActionProcessor.duration"; //$NON-NLS-1$
 
     private transient volatile Thread pauseThread;
 
     public TestAction() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult sample(Entry e) {
         JMeterContext context = JMeterContextService.getContext();
 
         int target = getTarget();
         int action = getAction();
         if (action == PAUSE) {
             pause(getDurationAsString());
         } else if (action == STOP || action == STOP_NOW || action == RESTART_NEXT_LOOP) {
             if (target == THREAD) {
                 if(action == STOP || action == STOP_NOW) {
                     log.info("Stopping current thread");
                     context.getThread().stop();
                 } else {
                     log.info("Restarting next loop");
                     context.setRestartNextLoop(true);
                 }
             } else if (target == TEST) {
                 if (action == STOP_NOW) {
                     log.info("Stopping all threads now");
                     context.getEngine().stopTest();
                 } else {
                     log.info("Stopping all threads");
                     context.getEngine().askThreadsToStop();
                 }
             }
         }
 
         return null; // This means no sample is saved
     }
 
     private void pause(String timeInMillis) {
         long millis;
         try {
             millis=Long.parseLong(timeInMillis);
         } catch (NumberFormatException e){
-            log.warn("Could not create number from "+timeInMillis);
+            log.warn("Could not create number from {}", timeInMillis);
             millis=0;
         }
         try {
             pauseThread = Thread.currentThread();
             if(millis>0) {
                 TimeUnit.MILLISECONDS.sleep(millis);
             } else if(millis<0) {
                 throw new IllegalArgumentException("Configured sleep is negative:"+millis);
             } // else == 0 we do nothing
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
         } finally {
             pauseThread = null;
         }
     }
 
     public void setTarget(int target) {
         setProperty(new IntegerProperty(TARGET, target));
     }
 
     public int getTarget() {
         return getPropertyAsInt(TARGET);
     }
 
     public void setAction(int action) {
         setProperty(new IntegerProperty(ACTION, action));
     }
 
     public int getAction() {
         return getPropertyAsInt(ACTION);
     }
 
     public void setDuration(String duration) {
         setProperty(new StringProperty(DURATION, duration));
     }
 
     public String getDurationAsString() {
         return getPropertyAsString(DURATION);
     }
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 
     @Override
     public boolean interrupt() {
         Thread thrd = pauseThread; // take copy so cannot get NPE
         if (thrd!= null) {
             thrd.interrupt();
             return true;
         }
         return false;
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/BSFTimer.java b/src/components/org/apache/jmeter/timers/BSFTimer.java
index a38a4c7f5..dabca91db 100644
--- a/src/components/org/apache/jmeter/timers/BSFTimer.java
+++ b/src/components/org/apache/jmeter/timers/BSFTimer.java
@@ -1,60 +1,60 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.timers;
 
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BSFTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BSFTimer extends BSFTestElement implements Cloneable, Timer, TestBean {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFTimer.class);
 
-    private static final long serialVersionUID = 4;
+    private static final long serialVersionUID = 5;
 
     /** {@inheritDoc} */
     @Override
     public long delay() {
         long delay = 0;
         BSFManager mgr = null;
         try {
             mgr = getManager();
             Object o = evalFileOrScript(mgr);
             if (o == null) {
                 log.warn("Script did not return a value");
                 return 0;
             }
             delay = Long.parseLong(o.toString());
         } catch (NumberFormatException | BSFException e) {
-            log.warn("Problem in BSF script "+e);
+            log.warn("Problem in BSF script. {}", e.toString());
         } finally {
             if(mgr != null) {
                 mgr.terminate();
             }
         }
         return delay;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/BeanShellTimer.java b/src/components/org/apache/jmeter/timers/BeanShellTimer.java
index 0355adbbb..a69505056 100644
--- a/src/components/org/apache/jmeter/timers/BeanShellTimer.java
+++ b/src/components/org/apache/jmeter/timers/BeanShellTimer.java
@@ -1,72 +1,72 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.timers;
 
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BeanShellTimer extends BeanShellTestElement implements Cloneable, Timer, TestBean {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellTimer.class);
 
-    private static final long serialVersionUID = 4;
+    private static final long serialVersionUID = 5;
 
     // can be specified in jmeter.properties
     private static final String INIT_FILE = "beanshell.timer.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public long delay() {
         String ret="0";
         final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
         if (bshInterpreter == null) {
             log.error("BeanShell not found");
             return 0;
         }
         try {
             Object o = processFileOrScript(bshInterpreter);
             if (o != null) { 
                 ret=o.toString(); 
             }
         } catch (JMeterException e) {
-            log.warn("Problem in BeanShell script "+e);
+            log.warn("Problem in BeanShell script. {}", e.toString());
         }
         try {
             return Long.decode(ret).longValue();
         } catch (NumberFormatException e){
             log.warn(e.getLocalizedMessage());
             return 0;
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/ConstantThroughputTimer.java b/src/components/org/apache/jmeter/timers/ConstantThroughputTimer.java
index 41045d9ec..1b5708958 100644
--- a/src/components/org/apache/jmeter/timers/ConstantThroughputTimer.java
+++ b/src/components/org/apache/jmeter/timers/ConstantThroughputTimer.java
@@ -1,332 +1,331 @@
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
 
 package org.apache.jmeter.timers;
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.util.ResourceBundle;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class implements a constant throughput timer. A Constant Throughtput
  * Timer paces the samplers under its influence so that the total number of
  * samples per unit of time approaches a given constant as much as possible.
  *
  * There are two different ways of pacing the requests:
  * - delay each thread according to when it last ran
  * - delay each thread according to when any thread last ran
  */
 public class ConstantThroughputTimer extends AbstractTestElement implements Timer, TestStateListener, TestBean {
-    private static final long serialVersionUID = 3;
+    private static final long serialVersionUID = 4;
 
     private static class ThroughputInfo{
         final Object MUTEX = new Object();
         long lastScheduledTime = 0;
     }
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ConstantThroughputTimer.class);
 
     private static final double MILLISEC_PER_MIN = 60000.0;
 
     /**
      * This enum defines the calculation modes used by the ConstantThroughputTimer.
      */
     public enum Mode {
         ThisThreadOnly("calcMode.1"), // NOSONAR Keep naming for compatibility
         AllActiveThreads("calcMode.2"), // NOSONAR Keep naming for compatibility
         AllActiveThreadsInCurrentThreadGroup("calcMode.3"), // NOSONAR Keep naming for compatibility
         AllActiveThreads_Shared("calcMode.4"), // NOSONAR Keep naming for compatibility
         AllActiveThreadsInCurrentThreadGroup_Shared("calcMode.5"), // NOSONAR Keep naming for compatibility
         ;
 
         private final String propertyName; // The property name to be used to look up the display string
 
         Mode(String name) {
             this.propertyName = name;
         }
 
         @Override
         public String toString() {
             return propertyName;
         }
     }
 
     /**
      * Target time for the start of the next request. The delay provided by the
      * timer will be calculated so that the next request happens at this time.
      */
     private long previousTime = 0;
 
     private Mode mode = Mode.ThisThreadOnly;
 
     /**
      * Desired throughput, in samples per minute.
      */
     private double throughput;
 
     //For calculating throughput across all threads
     private static final ThroughputInfo allThreadsInfo = new ThroughputInfo();
 
     //For holding the ThrougputInfo objects for all ThreadGroups. Keyed by AbstractThreadGroup objects
     private static final ConcurrentMap<AbstractThreadGroup, ThroughputInfo> threadGroupsInfoMap =
             new ConcurrentHashMap<>();
 
 
     /**
      * Constructor for a non-configured ConstantThroughputTimer.
      */
     public ConstantThroughputTimer() {
     }
 
     /**
      * Sets the desired throughput.
      *
      * @param throughput
      *            Desired sampling rate, in samples per minute.
      */
     public void setThroughput(double throughput) {
         this.throughput = throughput;
     }
 
     /**
      * Gets the configured desired throughput.
      *
      * @return the rate at which samples should occur, in samples per minute.
      */
     public double getThroughput() {
         return throughput;
     }
 
     public int getCalcMode() {
         return mode.ordinal();
     }
 
     public void setCalcMode(int mode) {
         this.mode = Mode.values()[mode];
     }
 
     /**
      * Retrieve the delay to use during test execution.
      *
      * @see org.apache.jmeter.timers.Timer#delay()
      */
     @Override
     public long delay() {
         long currentTime = System.currentTimeMillis();
 
         /*
          * If previous time is zero, then target will be in the past.
          * This is what we want, so first sample is run without a delay.
         */
         long currentTarget = previousTime  + calculateDelay();
         if (currentTime > currentTarget) {
             // We're behind schedule -- try to catch up:
             previousTime = currentTime; // assume the sample will run immediately
             return 0;
         }
         previousTime = currentTarget; // assume the sample will run as soon as the delay has expired
         return currentTarget - currentTime;
     }
 
     /**
      * Calculate the target time by adding the result of private method
      * <code>calculateDelay()</code> to the given <code>currentTime</code>
      * 
      * @param currentTime
      *            time in ms
      * @return new Target time
      */
     // TODO - is this used? (apart from test code)
     protected long calculateCurrentTarget(long currentTime) {
         return currentTime + calculateDelay();
     }
 
     // Calculate the delay based on the mode
     private long calculateDelay() {
         long delay;
         // N.B. we fetch the throughput each time, as it may vary during a test
         double msPerRequest = MILLISEC_PER_MIN / getThroughput();
         switch (mode) {
         case AllActiveThreads: // Total number of threads
             delay = Math.round(JMeterContextService.getNumberOfThreads() * msPerRequest);
             break;
 
         case AllActiveThreadsInCurrentThreadGroup: // Active threads in this group
             delay = Math.round(JMeterContextService.getContext().getThreadGroup().getNumberOfThreads() * msPerRequest);
             break;
 
         case AllActiveThreads_Shared: // All threads - alternate calculation
             delay = calculateSharedDelay(allThreadsInfo,Math.round(msPerRequest));
             break;
 
         case AllActiveThreadsInCurrentThreadGroup_Shared: //All threads in this group - alternate calculation
             final org.apache.jmeter.threads.AbstractThreadGroup group =
                 JMeterContextService.getContext().getThreadGroup();
             ThroughputInfo groupInfo = threadGroupsInfoMap.get(group);
             if (groupInfo == null) {
                 groupInfo = new ThroughputInfo();
                 ThroughputInfo previous = threadGroupsInfoMap.putIfAbsent(group, groupInfo);
                 if (previous != null) { // We did not replace the entry
                     groupInfo = previous; // so use the existing one
                 }
             }
             delay = calculateSharedDelay(groupInfo,Math.round(msPerRequest));
             break;
 
         case ThisThreadOnly:
         default: // e.g. 0
             delay = Math.round(msPerRequest); // i.e. * 1
             break;
         }
         return delay;
     }
 
     private long calculateSharedDelay(ThroughputInfo info, long milliSecPerRequest) {
         final long now = System.currentTimeMillis();
         final long calculatedDelay;
 
         //Synchronize on the info object's MUTEX to ensure
         //Multiple threads don't update the scheduled time simultaneously
         synchronized (info.MUTEX) {
             final long nextRequestTime = info.lastScheduledTime + milliSecPerRequest;
             info.lastScheduledTime = Math.max(now, nextRequestTime);
             calculatedDelay = info.lastScheduledTime - now;
         }
 
         return Math.max(calculatedDelay, 0);
     }
 
     private void reset() {
         synchronized (allThreadsInfo.MUTEX) {
             allThreadsInfo.lastScheduledTime = 0;
         }
         threadGroupsInfoMap.clear();
         // no need to sync as one per instance
         previousTime = 0;
     }
 
     /**
      * Provide a description of this timer class.
      *
      * TODO: Is this ever used? I can't remember where. Remove if it isn't --
      * TODO: or obtain text from bean's displayName or shortDescription.
      *
      * @return the description of this timer class.
      */
     @Override
     public String toString() {
         return JMeterUtils.getResString("constant_throughput_timer_memo"); //$NON-NLS-1$
     }
 
     /**
      * Get the timer ready to compute delays for a new test.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void testStarted()
     {
         log.debug("Test started - reset throughput calculation.");
         reset();
     }
 
     /**
      * Override the setProperty method in order to convert
      * the original String calcMode property.
      * This used the locale-dependent display value, so caused
      * problems when the language was changed.
      * Note that the calcMode StringProperty is replaced with an IntegerProperty
      * so the conversion only needs to happen once.
      */
     @Override
     public void setProperty(JMeterProperty property) {
         if (property instanceof StringProperty) {
             final String pn = property.getName();
             if (pn.equals("calcMode")) {
                 final Object objectValue = property.getObjectValue();
                 try {
                     final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
                     final ResourceBundle rb = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE);
                     for(Enum<Mode> e : Mode.values()) {
                         final String propName = e.toString();
                         if (objectValue.equals(rb.getObject(propName))) {
                             final int tmpMode = e.ordinal();
-                            if (log.isDebugEnabled()) {
-                                log.debug("Converted " + pn + "=" + objectValue + " to mode=" + tmpMode  + " using Locale: " + rb.getLocale());
-                            }
+                            log.debug("Converted {}={} to mode={} using Locale: {}", pn, objectValue, tmpMode,
+                                    rb.getLocale());
                             super.setProperty(pn, tmpMode);
                             return;
                         }
                     }
-                    log.warn("Could not convert " + pn + "=" + objectValue + " using Locale: " + rb.getLocale());
+                    log.warn("Could not convert {}={} using Locale: {}", pn, objectValue, rb.getLocale());
                 } catch (IntrospectionException e) {
                     log.error("Could not find BeanInfo", e);
                 }
             }
         }
         super.setProperty(property);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         //NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         //NOOP
     }
     
     // For access from test code
     Mode getMode() {
         return mode;
     }
     
     // For access from test code
     void setMode(Mode newMode) {
         mode = newMode;
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/JSR223Timer.java b/src/components/org/apache/jmeter/timers/JSR223Timer.java
index 78b686465..46c9c2300 100644
--- a/src/components/org/apache/jmeter/timers/JSR223Timer.java
+++ b/src/components/org/apache/jmeter/timers/JSR223Timer.java
@@ -1,58 +1,58 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.timers;
 
 import java.io.IOException;
 
 import javax.script.ScriptEngine;
 import javax.script.ScriptException;
 
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JSR223Timer extends JSR223TestElement implements Cloneable, Timer, TestBean {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223Timer.class);
 
-    private static final long serialVersionUID = 4;
+    private static final long serialVersionUID = 5;
 
     /** {@inheritDoc} */
     @Override
     public long delay() {
         long delay = 0;
         try {
             ScriptEngine scriptEngine = getScriptEngine();
             Object o = processFileOrScript(scriptEngine, null);
             if (o == null) {
                 log.warn("Script did not return a value");
                 return 0;
             }
             delay = Long.parseLong(o.toString());
         } catch (NumberFormatException | IOException | ScriptException e) {
-            log.error("Problem in JSR223 script "+getName(), e);
+            log.error("Problem in JSR223 script, {}", getName(), e);
         }
         return delay;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/SyncTimer.java b/src/components/org/apache/jmeter/timers/SyncTimer.java
index a7e529df0..b1ea62f24 100644
--- a/src/components/org/apache/jmeter/timers/SyncTimer.java
+++ b/src/components/org/apache/jmeter/timers/SyncTimer.java
@@ -1,278 +1,278 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.timers;
 
 import java.io.Serializable;
 import java.util.concurrent.BrokenBarrierException;
 import java.util.concurrent.CyclicBarrier;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The purpose of the SyncTimer is to block threads until X number of threads
  * have been blocked, and then they are all released at once. A SyncTimer can
  * thus create large instant loads at various points of the test plan.
  *
  */
 public class SyncTimer extends AbstractTestElement implements Timer, Serializable, TestBean, TestStateListener, ThreadListener {
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SyncTimer.class);
 
     /**
      * Wrapper to {@link CyclicBarrier} to allow lazy init of CyclicBarrier when SyncTimer is configured with 0
      */
     private static class BarrierWrapper implements Cloneable {
 
         private CyclicBarrier barrier;
 
         /**
          *
          */
         public BarrierWrapper() {
             this.barrier = null;
         }
 
         /**
          * @param parties Number of parties
          */
         public BarrierWrapper(int parties) {
             this.barrier = new CyclicBarrier(parties);
         }
 
         /**
          * Synchronized is required to ensure CyclicBarrier is initialized only once per Thread Group
          * @param parties Number of parties
          */
         public synchronized void setup(int parties) {
             if(this.barrier== null) {
                 this.barrier = new CyclicBarrier(parties);
             }
         }
 
 
         /**
          * Wait until all threads called await on this timer
          * 
          * @return The arrival index of the current thread
          * @throws InterruptedException
          *             when interrupted while waiting, or the interrupted status
          *             is set on entering this method
          * @throws BrokenBarrierException
          *             if the barrier is reset while waiting or broken on
          *             entering or while waiting
          * @see java.util.concurrent.CyclicBarrier#await()
          */
         public int await() throws InterruptedException, BrokenBarrierException{
             return barrier.await();
         }
         
         /**
          * Wait until all threads called await on this timer
          * 
          * @param timeout
          *            The timeout in <code>timeUnit</code> units
          * @param timeUnit
          *            The time unit for the <code>timeout</code>
          * @return The arrival index of the current thread
          * @throws InterruptedException
          *             when interrupted while waiting, or the interrupted status
          *             is set on entering this method
          * @throws BrokenBarrierException
          *             if the barrier is reset while waiting or broken on
          *             entering or while waiting
          * @throws TimeoutException
          *             if the specified time elapses
          * @see java.util.concurrent.CyclicBarrier#await()
          */
         public int await(long timeout, TimeUnit timeUnit) throws InterruptedException, BrokenBarrierException, TimeoutException {
             return barrier.await(timeout, timeUnit);
         }
 
         /**
          * @see java.util.concurrent.CyclicBarrier#reset()
          */
         public void reset() {
             barrier.reset();
         }
 
         /**
          * @see java.lang.Object#clone()
          */
         @Override
         protected Object clone()  {
             BarrierWrapper barrierWrapper=  null;
             try {
                 barrierWrapper = (BarrierWrapper) super.clone();
                 barrierWrapper.barrier = this.barrier;
             } catch (CloneNotSupportedException e) {
                 //Cannot happen
             }
             return barrierWrapper;
         }
     }
 
-    private static final long serialVersionUID = 2;
+    private static final long serialVersionUID = 3;
 
     private transient BarrierWrapper barrier;
 
     private int groupSize;
     
     private long timeoutInMs;
 
     // Ensure transient object is created by the server
     private Object readResolve(){
         createBarrier();
         return this;
     }
 
     /**
      * @return Returns the numThreads.
      */
     public int getGroupSize() {
         return groupSize;
     }
 
     /**
      * @param numThreads
      *            The numThreads to set.
      */
     public void setGroupSize(int numThreads) {
         this.groupSize = numThreads;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public long delay() {
         if(getGroupSize()>=0) {
             int arrival = 0;
             try {
                 if(timeoutInMs==0) {
                     arrival = this.barrier.await();                    
                 } else if(timeoutInMs > 0){
                     arrival = this.barrier.await(timeoutInMs, TimeUnit.MILLISECONDS);
                 } else {
                     throw new IllegalArgumentException("Negative value for timeout:"+timeoutInMs+" in Synchronizing Timer "+getName());
                 }
             } catch (InterruptedException | BrokenBarrierException e) {
                 return 0;
             } catch (TimeoutException e) {
-                LOGGER.warn("SyncTimer "+ getName() + " timeouted waiting for users after:"+getTimeoutInMs()+"ms");
+                log.warn("SyncTimer {} timeouted waiting for users after: {}ms", getName(), getTimeoutInMs());
                 return 0;
             } finally {
                 if(arrival == 0) {
                     barrier.reset();
                 }
             }
         }
         return 0;
     }
 
     /**
      * We have to control the cloning process because we need some cross-thread
      * communication if our synctimers are to be able to determine when to block
      * and when to release.
      */
     @Override
     public Object clone() {
         SyncTimer newTimer = (SyncTimer) super.clone();
         newTimer.barrier = barrier;
         return newTimer;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         this.testEnded(null);
     }
 
     /**
      * Reset timerCounter
      */
     @Override
     public void testEnded(String host) {
         createBarrier();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
         testStarted(null);
     }
 
     /**
      * Reset timerCounter
      */
     @Override
     public void testStarted(String host) {
         createBarrier();
     }
 
     /**
      *
      */
     private void createBarrier() {
         if(getGroupSize() == 0) {
             // Lazy init
             this.barrier = new BarrierWrapper();
         } else {
             this.barrier = new BarrierWrapper(getGroupSize());
         }
     }
 
     @Override
     public void threadStarted() {
         if(getGroupSize() == 0) {
             int numThreadsInGroup = JMeterContextService.getContext().getThreadGroup().getNumThreads();
             // Unique Barrier creation ensured by synchronized setup
             this.barrier.setup(numThreadsInGroup);
         }
     }
 
     @Override
     public void threadFinished() {
         // NOOP
     }
 
     /**
      * @return the timeoutInMs
      */
     public long getTimeoutInMs() {
         return timeoutInMs;
     }
 
     /**
      * @param timeoutInMs the timeoutInMs to set
      */
     public void setTimeoutInMs(long timeoutInMs) {
         this.timeoutInMs = timeoutInMs;
     }
 }
