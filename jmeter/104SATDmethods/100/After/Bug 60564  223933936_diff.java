diff --git a/src/core/org/apache/jmeter/util/BeanShellInterpreter.java b/src/core/org/apache/jmeter/util/BeanShellInterpreter.java
index 816b07180..1d8239f17 100644
--- a/src/core/org/apache/jmeter/util/BeanShellInterpreter.java
+++ b/src/core/org/apache/jmeter/util/BeanShellInterpreter.java
@@ -1,216 +1,216 @@
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
 
 package org.apache.jmeter.util;
 
 import java.io.File;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * BeanShell setup function - encapsulates all the access to the BeanShell
  * Interpreter in a single class.
  *
  * The class uses dynamic class loading to access BeanShell, which means that
  * all the source files can be built without needing access to the bsh jar.
  *
  * If the beanshell jar is not present at run-time, an error will be logged
  *
  */
 
 public class BeanShellInterpreter {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellInterpreter.class);
 
     private static final Method bshGet;
 
     private static final Method bshSet;
 
     private static final Method bshEval;
 
     private static final Method bshSource;
 
     private static final Class<?> bshClass;
 
     private static final String BSH_INTERPRETER = "bsh.Interpreter"; //$NON-NLS-1$
 
     static {
         // Temporary copies, so can set the final ones
         Method get = null;
         Method eval = null;
         Method set = null;
         Method source = null;
         Class<?> clazz = null;
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         try {
             clazz = loader.loadClass(BSH_INTERPRETER);
             Class<String> string = String.class;
             Class<Object> object = Object.class;
 
             get = clazz.getMethod("get", //$NON-NLS-1$
                     new Class[] { string });
             eval = clazz.getMethod("eval", //$NON-NLS-1$
                     new Class[] { string });
             set = clazz.getMethod("set", //$NON-NLS-1$
                     new Class[] { string, object });
             source = clazz.getMethod("source", //$NON-NLS-1$
                     new Class[] { string });
         } catch (ClassNotFoundException|SecurityException | NoSuchMethodException e) {
             log.error("Beanshell Interpreter not found", e);
         } finally {
             bshEval = eval;
             bshGet = get;
             bshSet = set;
             bshSource = source;
             bshClass = clazz;
         }
     }
 
     // This class is not serialised
     private Object bshInstance = null; // The interpreter instance for this class
 
     private final String initFile; // Script file to initialize the Interpreter with
 
     private final Logger logger; // Logger to use during initialization and script run
 
     public BeanShellInterpreter() throws ClassNotFoundException {
         this(null, null);
     }
 
     /**
      *
      * @param init initialisation file
      * @param _log logger to pass to interpreter
      * @throws ClassNotFoundException when beanshell can not be instantiated
      */
     public BeanShellInterpreter(String init, Logger _log)  throws ClassNotFoundException {
         initFile = init;
         logger = _log;
         init();
     }
 
     // Called from ctor, so must be private (or final, but it does not seem useful elsewhere)
     private void init() throws ClassNotFoundException {
         if (bshClass == null) {
             throw new ClassNotFoundException(BSH_INTERPRETER);
         }
         try {
             bshInstance = bshClass.newInstance();
         } catch (InstantiationException | IllegalAccessException e) {
             log.error("Can't instantiate BeanShell", e);
             throw new ClassNotFoundException("Can't instantiate BeanShell", e);
         } 
          if (logger != null) {// Do this before starting the script
             try {
                 set("log", logger);//$NON-NLS-1$
             } catch (JMeterException e) {
                 log.warn("Can't set logger variable", e);
             }
         }
         if (initFile != null && initFile.length() > 0) {
             String fileToUse=initFile;
             // Check file so we can distinguish file error from script error
             File in = new File(fileToUse);
             if (!in.exists()){// Cannot find the file locally, so try the bin directory
                 fileToUse=JMeterUtils.getJMeterHome()
                         +File.separator+"bin" // $NON-NLS-1$
                         +File.separator+initFile;
                 in = new File(fileToUse);
                 if (!in.exists()) {
                     log.warn("Cannot find init file: "+initFile);
                 }
             }
             if (!in.canRead()) {
                 log.warn("Cannot read init file: "+fileToUse);
             }
             try {
                 source(fileToUse);
             } catch (JMeterException e) {
                 log.warn("Cannot source init file: "+fileToUse,e);
             }
         }
     }
 
     /**
      * Resets the BeanShell interpreter.
      *
      * @throws ClassNotFoundException if interpreter cannot be instantiated
      */
     public void reset() throws ClassNotFoundException {
        init();
     }
 
     private Object bshInvoke(Method m, Object[] o, boolean shouldLog) throws JMeterException {
         Object r = null;
         final String errorString = "Error invoking bsh method: ";
         try {
             r = m.invoke(bshInstance, o);
         } catch (IllegalArgumentException | IllegalAccessException e) { // Programming error
             final String message = errorString + m.getName();
             log.error(message);
             throw new JMeterError(message, e);
         } catch (InvocationTargetException e) { // Can occur at run-time
             // could be caused by the bsh Exceptions:
             // EvalError, ParseException or TargetError
             String message = errorString + m.getName();
             Throwable cause = e.getCause();
             if (cause != null) {
                 message += "\t" + cause.getLocalizedMessage();
             }
 
             if (shouldLog) {
                 log.error(message);
             }
             throw new JMeterException(message, e);
         }
         return r;
     }
 
     public Object eval(String s) throws JMeterException {
         return bshInvoke(bshEval, new Object[] { s }, true);
     }
 
     public Object evalNoLog(String s) throws JMeterException {
         return bshInvoke(bshEval, new Object[] { s }, false);
     }
 
     public Object set(String s, Object o) throws JMeterException {
         return bshInvoke(bshSet, new Object[] { s, o }, true);
     }
 
     public Object set(String s, boolean b) throws JMeterException {
         return bshInvoke(bshSet, new Object[] { s, Boolean.valueOf(b) }, true);
     }
 
     public Object source(String s) throws JMeterException {
         return bshInvoke(bshSource, new Object[] { s }, true);
     }
 
     public Object get(String s) throws JMeterException {
         return bshInvoke(bshGet, new Object[] { s }, true);
     }
 
     // For use by Unit Tests
     public static boolean isInterpreterPresent(){
         return bshClass != null;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/BeanShellTestElement.java b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
index 3fb0a89ba..8a9513aae 100644
--- a/src/core/org/apache/jmeter/util/BeanShellTestElement.java
+++ b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
@@ -1,281 +1,281 @@
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
 
 package org.apache.jmeter.util;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public abstract class BeanShellTestElement extends AbstractTestElement
     implements Serializable, Cloneable, ThreadListener, TestStateListener
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellTestElement.class);
 
     private static final long serialVersionUID = 4;
 
     //++ For TestBean implementations only
     private String parameters; // passed to file or script
 
     private String filename; // file to source (overrides script)
 
     private String script; // script (if file not provided)
 
     private boolean resetInterpreter = false;
     //-- For TestBean implementations only
 
 
     private transient BeanShellInterpreter bshInterpreter = null;
 
     private transient boolean hasInitFile = false;
 
     public BeanShellTestElement() {
         super();
         init();
     }
 
     protected abstract String getInitFileProperty();
 
     /**
      * Get the interpreter and set up standard script variables.
      * <p>
      * Sets the following script variables:
      * <ul>
      * <li>ctx</li>
      * <li>Label</li>
      * <li>prev</li>
      * <li>props</li>
      * <li>vars</li>
      * </ul>
      * @return the interpreter
      */
     protected BeanShellInterpreter getBeanShellInterpreter() {
         if (isResetInterpreter()) {
             try {
                 bshInterpreter.reset();
             } catch (ClassNotFoundException e) {
                 log.error("Cannot reset BeanShell: "+e.toString());
             }
         }
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         try {
             bshInterpreter.set("ctx", jmctx);//$NON-NLS-1$
             bshInterpreter.set("Label", getName()); //$NON-NLS-1$
             bshInterpreter.set("prev", jmctx.getPreviousResult());//$NON-NLS-1$
             bshInterpreter.set("props", JMeterUtils.getJMeterProperties());
             bshInterpreter.set("vars", vars);//$NON-NLS-1$
         } catch (JMeterException e) {
             log.warn("Problem setting one or more BeanShell variables "+e);
         }
         return bshInterpreter;
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         parameters=""; // ensure variables are not null
         filename="";
         script="";
         try {
             String initFileName = JMeterUtils.getProperty(getInitFileProperty());
             hasInitFile = initFileName != null;
             bshInterpreter = new BeanShellInterpreter(initFileName, log);
         } catch (ClassNotFoundException e) {
             log.error("Cannot find BeanShell: "+e.toString());
         }
     }
 
     protected Object readResolve() {
         init();
         return this;
     }
 
     @Override
     public Object clone() {
         BeanShellTestElement o = (BeanShellTestElement) super.clone();
         o.init();
        return o;
     }
 
     /**
      * Process the file or script from the test element.
      * <p>
      * Sets the following script variables:
      * <ul>
      * <li>FileName</li>
      * <li>Parameters</li>
      * <li>bsh.args</li>
      * </ul>
      * @param bsh the interpreter, not {@code null}
      * @return the result of the script, may be {@code null}
      * 
      * @throws JMeterException when working with the bsh fails
      */
     protected Object processFileOrScript(BeanShellInterpreter bsh) throws JMeterException{
         String fileName = getFilename();
         String params = getParameters();
 
         bsh.set("FileName", fileName);//$NON-NLS-1$
         // Set params as a single line
         bsh.set("Parameters", params); // $NON-NLS-1$
         // and set as an array
         bsh.set("bsh.args",//$NON-NLS-1$
                 JOrphanUtils.split(params, " "));//$NON-NLS-1$
 
         if (fileName.length() == 0) {
             return bsh.eval(getScript());
         }
         return bsh.source(fileName);
     }
 
     /**
      * Return the script (TestBean version).
      * Must be overridden for subclasses that don't implement TestBean
      * otherwise the clone() method won't work.
      *
      * @return the script to execute
      */
     public String getScript(){
         return script;
     }
 
     /**
      * Set the script (TestBean version).
      * Must be overridden for subclasses that don't implement TestBean
      * otherwise the clone() method won't work.
      *
      * @param s the script to execute (may be blank)
      */
     public void setScript(String s){
         script=s;
     }
 
     @Override
     public void threadStarted() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("threadStarted()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     @Override
     public void threadFinished() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("threadFinished()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testEnded() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("testEnded()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testEnded(String host) {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.eval((new StringBuilder("testEnded(\"")) // $NON-NLS-1$
                     .append(host)
                     .append("\")") // $NON-NLS-1$
                     .toString()); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testStarted() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("testStarted()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     @Override
     public void testStarted(String host) {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.eval((new StringBuilder("testStarted(\"")) // $NON-NLS-1$
                     .append(host)
                     .append("\")") // $NON-NLS-1$
                     .toString()); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     // Overridden by non-TestBean implementations to return the property value instead
     public String getParameters() {
         return parameters;
     }
 
     public void setParameters(String s) {
         parameters = s;
     }
 
     // Overridden by non-TestBean implementations to return the property value instead
     public String getFilename() {
         return filename;
     }
 
     public void setFilename(String s) {
         filename = s;
     }
 
     public boolean isResetInterpreter() {
         return resetInterpreter;
     }
 
     public void setResetInterpreter(boolean b) {
         resetInterpreter = b;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/BeanShell.java b/src/functions/org/apache/jmeter/functions/BeanShell.java
index 6f26ba5da..aa26f1255 100644
--- a/src/functions/org/apache/jmeter/functions/BeanShell.java
+++ b/src/functions/org/apache/jmeter/functions/BeanShell.java
@@ -1,153 +1,153 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * A function which understands BeanShell
  * @since 1.X
  */
 public class BeanShell extends AbstractFunction {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShell.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__BeanShell"; //$NON-NLS-1$
 
     public static final String INIT_FILE = "beanshell.function.init"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("bsh_function_expression"));// $NON-NLS1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
 
     private BeanShellInterpreter bshInterpreter = null;
 
     public BeanShell() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         if (bshInterpreter == null) // did we find BeanShell?
         {
             throw new InvalidVariableException("BeanShell not found");
         }
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         String script = ((CompoundVariable) values[0]).execute();
         String varName = ""; //$NON-NLS-1$
         if (values.length > 1) {
             varName = ((CompoundVariable) values[1]).execute().trim();
         }
 
         String resultStr = ""; //$NON-NLS-1$
         try {
 
             // Pass in some variables
             if (currentSampler != null) {
                 bshInterpreter.set("Sampler", currentSampler); //$NON-NLS-1$
             }
 
             if (previousResult != null) {
                 bshInterpreter.set("SampleResult", previousResult); //$NON-NLS-1$
             }
 
             // Allow access to context and variables directly
             bshInterpreter.set("ctx", jmctx); //$NON-NLS-1$
             bshInterpreter.set("vars", vars); //$NON-NLS-1$
             bshInterpreter.set("props", JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
             bshInterpreter.set("threadName", Thread.currentThread().getName()); //$NON-NLS-1$
 
             // Execute the script
             Object bshOut = bshInterpreter.eval(script);
             if (bshOut != null) {
                 resultStr = bshOut.toString();
             }
             if (vars != null && varName.length() > 0) {// vars will be null on TestPlan
                 vars.put(varName, resultStr);
             }
         } catch (Exception ex) // Mainly for bsh.EvalError
         {
             log.warn("Error running BSH script", ex);
         }
         if(log.isDebugEnabled()) {
             log.debug("__Beanshell("+script+","+varName+")=" + resultStr);
         }
         return resultStr;
 
     }
 
     /*
      * Helper method for use by scripts
      *
      */
     public void log_info(String s) {
         log.info(s);
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
 
         checkParameterCount(parameters, 1, 2);
 
         values = parameters.toArray();
 
         try {
             bshInterpreter = new BeanShellInterpreter(JMeterUtils.getProperty(INIT_FILE), log);
         } catch (ClassNotFoundException e) {
             throw new InvalidVariableException("BeanShell not found", e);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/CSVRead.java b/src/functions/org/apache/jmeter/functions/CSVRead.java
index 4f94c5251..8de9f9bf2 100644
--- a/src/functions/org/apache/jmeter/functions/CSVRead.java
+++ b/src/functions/org/apache/jmeter/functions/CSVRead.java
@@ -1,162 +1,162 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The function represented by this class allows data to be read from CSV files.
  * Syntax is similar to StringFromFile function. The function allows the test to
  * line-thru the data in the CSV file - one line per each test. E.g. inserting
  * the following in the test scripts :
  *
  * ${_CSVRead(c:/BOF/abcd.csv,0)} // read (first) line of 'c:/BOF/abcd.csv' ,
  * return the 1st column ( represented by the '0'),
  * ${_CSVRead(c:/BOF/abcd.csv,1)} // read (first) line of 'c:/BOF/abcd.csv' ,
  * return the 2nd column ( represented by the '1'),
  * ${_CSVRead(c:/BOF/abcd.csv,next())} // Go to next line of 'c:/BOF/abcd.csv'
  *
  * NOTE: A single instance of each different file is opened and used for all
  * threads.
  *
  * To open the same file twice, use the alias function: __CSVRead(abc.csv,*ONE);
  * __CSVRead(abc.csv,*TWO);
  *
  * __CSVRead(*ONE,1); etc
  * @since 1.9
  */
 public class CSVRead extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CSVRead.class);
 
     private static final String KEY = "__CSVRead"; // Function name //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<>();
 
     private Object[] values; // Parameter list
 
     static {
         desc.add(JMeterUtils.getResString("csvread_file_file_name")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("column_number")); //$NON-NLS-1$
     }
 
     public CSVRead() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String myValue = ""; //$NON-NLS-1$
 
         String fileName = ((org.apache.jmeter.engine.util.CompoundVariable) values[0]).execute();
         String columnOrNext = ((org.apache.jmeter.engine.util.CompoundVariable) values[1]).execute();
 
         if (log.isDebugEnabled()) {
             log.debug("execute (" + fileName + " , " + columnOrNext + ")   ");
         }
 
         // Process __CSVRead(filename,*ALIAS)
         if (columnOrNext.startsWith("*")) { //$NON-NLS-1$
             FileWrapper.open(fileName, columnOrNext);
             /*
              * All done, so return
              */
             return ""; //$NON-NLS-1$
         }
 
         // if argument is 'next' - go to the next line
         if (columnOrNext.equals("next()") || columnOrNext.equals("next")) { //$NON-NLS-1$ //$NON-NLS-2$
             FileWrapper.endRow(fileName);
 
             /*
              * All done now ,so return the empty string - this allows the caller
              * to append __CSVRead(file,next) to the last instance of
              * __CSVRead(file,col)
              *
              * N.B. It is important not to read any further lines at this point,
              * otherwise the wrong line can be retrieved when using multiple
              * threads.
              */
             return ""; //$NON-NLS-1$
         }
 
         try {
             int columnIndex = Integer.parseInt(columnOrNext); // what column
                                                                 // is wanted?
             myValue = FileWrapper.getColumn(fileName, columnIndex);
         } catch (NumberFormatException e) {
             log.warn(Thread.currentThread().getName() + " - can't parse column number: " + columnOrNext + " "
                     + e.toString());
         } catch (IndexOutOfBoundsException e) {
             log.warn(Thread.currentThread().getName() + " - invalid column number: " + columnOrNext + " at row "
                     + FileWrapper.getCurrentRow(fileName) + " " + e.toString());
         }
 
         if (log.isDebugEnabled()) {
             log.debug("execute value: " + myValue);
         }
 
         return myValue;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         log.debug("setParameter - Collection.size=" + parameters.size());
 
         values = parameters.toArray();
 
         if (log.isDebugEnabled()) {
             for (int i = 0; i < parameters.size(); i++) {
                 log.debug("i:" + ((CompoundVariable) values[i]).execute());
             }
         }
 
         checkParameterCount(parameters, 2);
 
         /*
          * Need to reset the containers for repeated runs; about the only way
          * for functions to detect that a run is starting seems to be the
          * setParameters() call.
          */
         FileWrapper.clearAll();// TODO only clear the relevant entry - if possible...
 
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/CharFunction.java b/src/functions/org/apache/jmeter/functions/CharFunction.java
index e535caac2..f04ef5679 100644
--- a/src/functions/org/apache/jmeter/functions/CharFunction.java
+++ b/src/functions/org/apache/jmeter/functions/CharFunction.java
@@ -1,91 +1,91 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Function to generate chars from a list of decimal or hex values
  * @since 2.3.3
  */
 public class CharFunction extends AbstractFunction {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CharFunction.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__char"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("char_value")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public CharFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         StringBuilder sb = new StringBuilder(values.length);
         for (Object val : values) {
             String numberString = ((CompoundVariable) val).execute().trim();
             try {
                 long value = Long.decode(numberString).longValue();
                 char ch = (char) value;
                 sb.append(ch);
             } catch (NumberFormatException e) {
                 log.warn("Could not parse " + numberString + " : " + e);
             }
         }
         return sb.toString();
 
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkMinParameterCount(parameters, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/EscapeOroRegexpChars.java b/src/functions/org/apache/jmeter/functions/EscapeOroRegexpChars.java
index 166cbebd4..c19d5aed4 100644
--- a/src/functions/org/apache/jmeter/functions/EscapeOroRegexpChars.java
+++ b/src/functions/org/apache/jmeter/functions/EscapeOroRegexpChars.java
@@ -1,113 +1,113 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.apache.oro.text.regex.Perl5Compiler;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Escape ORO meta characters
  * @since 2.9
  */
 public class EscapeOroRegexpChars extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(EscapeOroRegexpChars.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__escapeOroRegexpChars"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("value_to_quote_meta")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private CompoundVariable[] values;
 
     private static final int MAX_PARAM_COUNT = 2;
 
     private static final int MIN_PARAM_COUNT = 1;
     
     private static final int PARAM_NAME = 2;
 
     /**
      * No-arg constructor.
      */
     public EscapeOroRegexpChars() {
         super();
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String valueToEscape = values[0].execute();       
         
         String varName = "";//$NON-NLS-1$
         if (values.length >= PARAM_NAME) {
             varName = values[PARAM_NAME - 1].execute().trim();
         }
 
         String escapedValue = Perl5Compiler.quotemeta(valueToEscape);
          
         if (varName.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null) {// Can be null if called from Config item testEnded() method
                 vars.put(varName, escapedValue);
             }
         }
 
         if (log.isDebugEnabled()) {
             String tn = Thread.currentThread().getName();
             log.debug(tn + " name:" //$NON-NLS-1$
                     + varName + " value:" + escapedValue);//$NON-NLS-1$
         }
 
         return escapedValue;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
         values = parameters.toArray(new CompoundVariable[parameters.size()]);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/EvalVarFunction.java b/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
index 48b9c6d22..f658a95e0 100644
--- a/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
+++ b/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
@@ -1,96 +1,96 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Function to evaluate a string which may contain variable or function references.
  *
  * Parameter: string to be evaluated
  *
  * Returns: the evaluated value
  * @since 2.3.1
  */
 public class EvalVarFunction extends AbstractFunction {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(EvalVarFunction.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__evalVar"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
     private static final int MAX_PARAMETER_COUNT = 1;
 
     static {
         desc.add(JMeterUtils.getResString("evalvar_name_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public EvalVarFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String variableName = ((CompoundVariable) values[0]).execute();
         final JMeterVariables vars = getVariables();
         if (vars == null){
             log.error("Variables have not yet been defined");
             return "**ERROR - see log file**";
         }
         String variableValue = vars.get(variableName);
         CompoundVariable cv = new CompoundVariable(variableValue);
         return cv.execute();
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/FileRowColContainer.java b/src/functions/org/apache/jmeter/functions/FileRowColContainer.java
index 5a3f70630..8e89df49d 100644
--- a/src/functions/org/apache/jmeter/functions/FileRowColContainer.java
+++ b/src/functions/org/apache/jmeter/functions/FileRowColContainer.java
@@ -1,188 +1,188 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.BufferedReader;
 import java.io.FileNotFoundException;
 import java.io.FileReader;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * File data container for CSV (and similar delimited) files Data is accessible
  * via row and column number
  *
  */
 public class FileRowColContainer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FileRowColContainer.class);
 
     private final List<List<String>> fileData; // Lines in the file, split into columns
 
     private final String fileName; // name of the file
 
     public static final String DELIMITER
         = JMeterUtils.getPropDefault("csvread.delimiter",  // $NON-NLS-1$
                 ","); // $NON-NLS-1$
 
     /** Keeping track of which row is next to be read. */
     private int nextRow;
 
     /** Delimiter for this file */
     private final String delimiter;
 
     public FileRowColContainer(String file, String delim) throws IOException, FileNotFoundException {
         log.debug("FRCC(" + file + "," + delim + ")");
         fileName = file;
         delimiter = delim;
         nextRow = 0;
         fileData = new ArrayList<>();
         load();
     }
 
     public FileRowColContainer(String file) throws IOException, FileNotFoundException {
         log.debug("FRCC(" + file + ")[" + DELIMITER + "]");
         fileName = file;
         delimiter = DELIMITER;
         nextRow = 0;
         fileData = new ArrayList<>();
         load();
     }
 
     private void load() throws IOException, FileNotFoundException {
 
         BufferedReader myBread = null;
         try {
             FileReader fis = new FileReader(fileName);
             myBread = new BufferedReader(fis);
             String line = myBread.readLine();
             /*
              * N.B. Stop reading the file if we get a blank line: This allows
              * for trailing comments in the file
              */
             while (line != null && line.length() > 0) {
                 fileData.add(splitLine(line, delimiter));
                 line = myBread.readLine();
             }
         } catch (IOException e) {
             fileData.clear();
             log.warn(e.toString());
             throw e;
         } finally {
             if (myBread != null) {
                 myBread.close();
             }
         }
     }
 
     /**
      * Get the string for the column from the current row
      *
      * @param row
      *            row number (from 0)
      * @param col
      *            column number (from 0)
      * @return the string (empty if out of bounds)
      * @throws IndexOutOfBoundsException
      *             if the column number is out of bounds
      */
     public String getColumn(int row, int col) throws IndexOutOfBoundsException {
         String colData;
         colData = fileData.get(row).get(col);
         log.debug(fileName + "(" + row + "," + col + "): " + colData);
         return colData;
     }
 
     /**
      * Returns the next row to the caller, and updates it, allowing for wrap
      * round
      *
      * @return the first free (unread) row
      *
      */
     public int nextRow() {
         int row = nextRow;
         nextRow++;
         if (nextRow >= fileData.size())// 0-based
         {
             nextRow = 0;
         }
         log.debug("Row: " + row);
         return row;
     }
 
     /**
      * Splits the line according to the specified delimiter
      *
      * @return a List of Strings containing one element for each value in
      *         the line
      */
     private static List<String> splitLine(String theLine, String delim) {
         List<String> result = new ArrayList<>();
         StringTokenizer tokener = new StringTokenizer(theLine, delim, true);
         /*
          * the beginning of the line is a "delimiter" so that ,a,b,c returns ""
          * "a" "b" "c"
          */
         boolean lastWasDelim = true;
         while (tokener.hasMoreTokens()) {
             String token = tokener.nextToken();
             if (token.equals(delim)) {
                 if (lastWasDelim) {
                     // two delimiters in a row; add an empty String
                     result.add("");
                 }
                 lastWasDelim = true;
             } else {
                 lastWasDelim = false;
                 result.add(token);
             }
         }
         if (lastWasDelim) // Catch the trailing delimiter
         {
             result.add(""); // $NON-NLS-1$
         }
         return result;
     }
 
     /**
      * @return the file name for this class
      */
     public String getFileName() {
         return fileName;
     }
 
     /**
      * @return Returns the delimiter.
      */
     final String getDelimiter() {
         return delimiter;
     }
 
     // Added to support external testing
     public int getSize(){
         return fileData.size();
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/FileToString.java b/src/functions/org/apache/jmeter/functions/FileToString.java
index 4e25139ac..fbea2964b 100644
--- a/src/functions/org/apache/jmeter/functions/FileToString.java
+++ b/src/functions/org/apache/jmeter/functions/FileToString.java
@@ -1,143 +1,143 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopThreadException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * FileToString Function to read a complete file into a String.
  *
  * Parameters:
  * - file name
  * - file encoding (optional)
  * - variable name (optional)
  *
  * Returns:
  * - the whole text from a file
  * - or **ERR** if an error occurs
  * - value is also optionally saved in the variable for later re-use.
  * @since 2.4
  */
 public class FileToString extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FileToString.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__FileToString";//$NON-NLS-1$
 
     static final String ERR_IND = "**ERR**";//$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("string_from_file_file_name"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("string_from_file_encoding"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));//$NON-NLS-1$
     }
 
     private static final int MIN_PARAM_COUNT = 1;
 
     private static final int MAX_PARAM_COUNT = 3;
 
     private static final int ENCODING = 2;
 
     private static final int PARAM_NAME = 3;
 
     private Object[] values;
 
     public FileToString() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String fileName = ((CompoundVariable) values[0]).execute();
 
         String encoding = null;//means platform default
         if (values.length >= ENCODING) {
             encoding = ((CompoundVariable) values[ENCODING - 1]).execute().trim();
             if (encoding.length() <= 0) { // empty encoding, return to platorm default
                 encoding = null;
             }
         }
 
         String myName = "";//$NON-NLS-1$
         if (values.length >= PARAM_NAME) {
             myName = ((CompoundVariable) values[PARAM_NAME - 1]).execute().trim();
         }
 
         String myValue = ERR_IND;
 
         try {
             myValue = FileUtils.readFileToString(new File(fileName), encoding);
         } catch (IOException e) {
             log.warn("Could not read file: "+fileName+" "+e.getMessage(), e);
             throw new JMeterStopThreadException("End of sequence", e);
         }
 
         if (myName.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null) {// Can be null if called from Config item testEnded() method
                 vars.put(myName, myValue);
             }
         }
 
         if (log.isDebugEnabled()) {
             String tn = Thread.currentThread().getName();
             log.debug(tn + " name:" //$NON-NLS-1$
                     + myName + " value:" + myValue);//$NON-NLS-1$
         }
 
         return myValue;
     }
 
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/FileWrapper.java b/src/functions/org/apache/jmeter/functions/FileWrapper.java
index 4bff0593d..203d606a1 100644
--- a/src/functions/org/apache/jmeter/functions/FileWrapper.java
+++ b/src/functions/org/apache/jmeter/functions/FileWrapper.java
@@ -1,208 +1,208 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class wraps the FileRowColContainer for use across multiple threads.
  *
  * It does this by maintaining a list of open files, keyed by file name (or
  * alias, if used). A list of open files is also maintained for each thread,
  * together with the current line number.
  *
  */
 public final class FileWrapper {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FileWrapper.class);
 
     private static final int NO_LINE = -1;
 
     private static volatile String defaultFile = ""; // for omitted file names //$NON-NLS-1$
 
     /*
      * This Map serves two purposes:
      * - maps file names to  containers
      * - ensures only one container per file across all threads
      */
     private static final Map<String, FileRowColContainer> fileContainers = new HashMap<>();
 
     /* The cache of file packs - used to improve thread access */
     private static final ThreadLocal<Map<String, FileWrapper>> filePacks = 
         new ThreadLocal<Map<String, FileWrapper>>() {
         @Override
         protected Map<String, FileWrapper> initialValue() {
             return new HashMap<>();
         }
     };
 
     private final FileRowColContainer container;
 
     private int currentRow;
 
     /*
      * Only needed locally
      */
     private FileWrapper(FileRowColContainer fdc) {
         super();
         container = fdc;
         currentRow = -1;
     }
 
     private static String checkDefault(String file) {
         if (file.length() == 0) {
             if (fileContainers.size() == 1 && defaultFile.length() > 0) {
                 log.warn("Using default: " + defaultFile);
                 file = defaultFile;
             } else {
                 log.error("Cannot determine default file name");
             }
         }
         return file;
     }
 
     /*
      * called by CSVRead(file,alias)
      */
     public static synchronized void open(String file, String alias) {
         log.info("Opening " + file + " as " + alias);
         file = checkDefault(file);
         if (alias.length() == 0) {
             log.error("Alias cannot be empty");
             return;
         }
         Map<String, FileWrapper> m = filePacks.get();
         if (m.get(alias) == null) {
             FileRowColContainer frcc;
             try {
                 frcc = getFile(file, alias);
                 log.info("Stored " + file + " as " + alias);
                 m.put(alias, new FileWrapper(frcc));
             } catch (IOException e) {
                 // Already logged
             }
         }
     }
 
     private static FileRowColContainer getFile(String file, String alias) throws FileNotFoundException, IOException {
         FileRowColContainer frcc;
         if ((frcc = fileContainers.get(alias)) == null) {
             frcc = new FileRowColContainer(file);
             fileContainers.put(alias, frcc);
             log.info("Saved " + file + " as " + alias + " delimiter=<" + frcc.getDelimiter() + ">");
             if (defaultFile.length() == 0) {
                 defaultFile = file;// Save in case needed later
             }
         }
         return frcc;
     }
 
     /*
      * Called by CSVRead(x,next) - sets the row to nil so the next row will be
      * picked up the next time round
      *
      */
     public static void endRow(String file) {
         file = checkDefault(file);
         Map<String, FileWrapper> my = filePacks.get();
         FileWrapper fw = my.get(file);
         if (fw == null) {
             log.warn("endRow(): no entry for " + file);
         } else {
             fw.endRow();
         }
     }
 
     private void endRow() {
         if (currentRow == NO_LINE) {
             log.warn("endRow() called twice in succession");
         }
         currentRow = NO_LINE;
     }
 
     public static String getColumn(String file, int col) {
         Map<String, FileWrapper> my = filePacks.get();
         FileWrapper fw = my.get(file);
         if (fw == null) // First call
         {
             if (file.startsWith("*")) { //$NON-NLS-1$
                 log.warn("Cannot perform initial open using alias " + file);
             } else {
                 file = checkDefault(file);
                 log.info("Attaching " + file);
                 open(file, file);
                 fw = my.get(file);
             }
             // TODO improve the error handling
             if (fw == null) {
                 return "";  //$NON-NLS-1$
             }
         }
         return fw.getColumn(col);
     }
 
     private String getColumn(int col) {
         if (currentRow == NO_LINE) {
             currentRow = container.nextRow();
 
         }
         return container.getColumn(currentRow, col);
     }
 
     /**
      * Gets the current row number (mainly for error reporting)
      *
      * @param file
      *            name of the file for which the row number is asked
      * @return the current row number for this thread, or <code>-1</code> if
      *         <code>file</code> was not opened yet
      */
     public static int getCurrentRow(String file) {
 
         Map<String, FileWrapper> my = filePacks.get();
         FileWrapper fw = my.get(file);
         if (fw == null) // Not yet open
         {
             return -1;
         }
         return fw.currentRow;
     }
 
     /**
      *
      */
     public static void clearAll() {
         log.debug("clearAll()");
         Map<String, FileWrapper> my = filePacks.get();
         for (Iterator<Map.Entry<String, FileWrapper>>  i = my.entrySet().iterator(); i.hasNext();) {
             Map.Entry<String, FileWrapper> fw = i.next();
             log.info("Removing " + fw.toString());
             i.remove();
         }
         fileContainers.clear();
         defaultFile = ""; //$NON-NLS-1$
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/Groovy.java b/src/functions/org/apache/jmeter/functions/Groovy.java
index 874ccc97c..81ed7ad26 100644
--- a/src/functions/org/apache/jmeter/functions/Groovy.java
+++ b/src/functions/org/apache/jmeter/functions/Groovy.java
@@ -1,187 +1,187 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Properties;
 
 import javax.script.Bindings;
 import javax.script.ScriptEngine;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * __groovy function 
  * Provides a Groovy interpreter
  * @since 3.1
  */
 public class Groovy extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Groovy.class);
 
     private static final String GROOVY_ENGINE_NAME = "groovy";
     
     private static final List<String> DESCRIPTION = new LinkedList<>();
 
     private static final String KEY = "__groovy"; //$NON-NLS-1$
 
     public static final String INIT_FILE = "groovy.utilities"; //$NON-NLS-1$
 
     static {
         DESCRIPTION.add(JMeterUtils.getResString("groovy_function_expression"));// $NON-NLS1$
         DESCRIPTION.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
     private ScriptEngine scriptEngine;
 
 
     public Groovy() {
     }
     
     /**
      * Populate variables to be passed to scripts
      * @param bindings Bindings
      */
     protected void populateBindings(Bindings bindings) {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         Bindings bindings = scriptEngine.createBindings();
         populateBindings(bindings);
 
 
         String script = ((CompoundVariable) values[0]).execute();
         String varName = ""; //$NON-NLS-1$
         if (values.length > 1) {
             varName = ((CompoundVariable) values[1]).execute().trim();
         }
 
         String resultStr = ""; //$NON-NLS-1$
         try {
 
             // Pass in some variables
             if (currentSampler != null) {
                 bindings.put("sampler", currentSampler); // $NON-NLS-1$ 
             }
 
             if (previousResult != null) {
                 bindings.put("prev", previousResult); //$NON-NLS-1$
             }
             bindings.put("log", log); // $NON-NLS-1$ (this name is fixed)
             // Add variables for access to context and variables
             bindings.put("threadName", Thread.currentThread().getName());
             JMeterContext jmctx = JMeterContextService.getContext();
             bindings.put("ctx", jmctx); // $NON-NLS-1$ (this name is fixed)
             JMeterVariables vars = jmctx.getVariables();
             bindings.put("vars", vars); // $NON-NLS-1$ (this name is fixed)
             Properties props = JMeterUtils.getJMeterProperties();
             bindings.put("props", props); // $NON-NLS-1$ (this name is fixed)
             // For use in debugging:
             bindings.put("OUT", System.out); // $NON-NLS-1$ (this name is fixed)
 
             
             // Execute the script
             Object out = scriptEngine.eval(script, bindings);
             if (out != null) {
                 resultStr = out.toString();
             }
             
             if (varName.length() > 0) {// vars will be null on TestPlan
                 if(vars != null) {
                     vars.put(varName, resultStr);
                 }
             }
         } catch (Exception ex) // Mainly for bsh.EvalError
         {
             log.warn("Error running groovy script", ex);
         }
         if(log.isDebugEnabled()) {
             log.debug("__groovy("+script+","+varName+")=" + resultStr);
         }
         return resultStr;
 
     }
 
     /*
      * Helper method for use by scripts
      *
      */
     public void log_info(String s) {
         log.info(s);
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1, 2);
         values = parameters.toArray();
         scriptEngine = JSR223TestElement.getInstance().getEngineByName(GROOVY_ENGINE_NAME); //$NON-NLS-N$
 
         String fileName = JMeterUtils.getProperty(INIT_FILE);
         if(!StringUtils.isEmpty(fileName)) {
             File file = new File(fileName);
             if(!(file.exists() && file.canRead())) {
                 // File maybe relative to JMeter home
                 File oldFile = file;
                 file = new File(JMeterUtils.getJMeterHome(), fileName);
                 if(!(file.exists() && file.canRead())) {
                     throw new InvalidVariableException("Cannot read file, neither from:"+oldFile.getAbsolutePath()+
                             ", nor from:"+file.getAbsolutePath()+", check property '"+INIT_FILE+"'");
                 }
             }
             try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr)) {
                 Bindings bindings = scriptEngine.createBindings();
                 bindings.put("log", log);
                 scriptEngine.eval(reader, bindings);
             } catch(Exception ex) {
                 throw new InvalidVariableException("Failed loading script:"+file.getAbsolutePath(), ex);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return DESCRIPTION;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/JavaScript.java b/src/functions/org/apache/jmeter/functions/JavaScript.java
index 61625af64..3608afee1 100644
--- a/src/functions/org/apache/jmeter/functions/JavaScript.java
+++ b/src/functions/org/apache/jmeter/functions/JavaScript.java
@@ -1,218 +1,218 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.script.Bindings;
 import javax.script.ScriptContext;
 import javax.script.ScriptEngine;
 import javax.script.ScriptEngineManager;
 import javax.script.SimpleScriptContext;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.RhinoException;
 import org.mozilla.javascript.Scriptable;
 
 /**
  * javaScript function implementation that executes a piece of JavaScript (not Java!) code and returns its value
  * @since 1.9
  */
 public class JavaScript extends AbstractFunction {
     private static final String NASHORN_ENGINE_NAME = "nashorn"; //$NON-NLS-1$
 
     private static final String USE_RHINO_ENGINE_PROPERTY = "javascript.use_rhino"; //$NON-NLS-1$
 
     /**
      * Initialization On Demand Holder pattern
      */
     private static class LazyHolder {
         public static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
     }
  
     private static final boolean USE_RHINO_ENGINE = 
             JMeterUtils.getPropDefault(USE_RHINO_ENGINE_PROPERTY, false) || 
             (getInstance().getEngineByName(JavaScript.NASHORN_ENGINE_NAME) == null);
 
     /**
      * @return ScriptEngineManager singleton
      */
     private static ScriptEngineManager getInstance() {
             return LazyHolder.INSTANCE;
     }
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__javaScript"; //$NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JavaScript.class);
 
     static {
         desc.add(JMeterUtils.getResString("javascript_expression"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public JavaScript() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         String script = ((CompoundVariable) values[0]).execute();
         // Allow variable to be omitted
         String varName = values.length < 2 ? null : ((CompoundVariable) values[1]).execute().trim();
         String resultStr = "";
 
         if(USE_RHINO_ENGINE) {
             resultStr = executeWithRhino(previousResult, currentSampler, jmctx,
                 vars, script, varName);
         } else {
             resultStr = executeWithNashorn(previousResult, currentSampler, jmctx,
                     vars, script, varName);
         }
 
         return resultStr;
 
     }
 
     /**
      * 
      * @param previousResult {@link SampleResult}
      * @param currentSampler {@link Sampler}
      * @param jmctx {@link JMeterContext}
      * @param vars {@link JMeterVariables}
      * @param script Javascript code
      * @param varName variable name
      * @return result as String
      * @throws InvalidVariableException
      */
     private String executeWithNashorn(SampleResult previousResult,
             Sampler currentSampler, JMeterContext jmctx, JMeterVariables vars,
             String script, String varName)
             throws InvalidVariableException {
         String resultStr = null;
         try {
             ScriptContext newContext = new SimpleScriptContext();
             ScriptEngine engine = getInstance().getEngineByName(JavaScript.NASHORN_ENGINE_NAME);
             Bindings bindings = engine.createBindings();
 
             // Set up some objects for the script to play with
             bindings.put("log", log); //$NON-NLS-1$
             bindings.put("ctx", jmctx); //$NON-NLS-1$
             bindings.put("vars", vars); //$NON-NLS-1$
             bindings.put("props", JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
 
             bindings.put("threadName", Thread.currentThread().getName()); //$NON-NLS-1$
             bindings.put("sampler", currentSampler); //$NON-NLS-1$
             bindings.put("sampleResult", previousResult); //$NON-NLS-1$
             newContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
             Object result = engine.eval(script, newContext);
             resultStr = result.toString();
             if (varName != null && vars != null) {// vars can be null if run from TestPlan
                 vars.put(varName, resultStr);
             }
         } catch (Exception e) {
             log.error("Error processing Javascript: [" + script + "]\n", e);
             throw new InvalidVariableException("Error processing Javascript: [" + script + "]", e);
         } 
         return resultStr;
     }
     
     /**
      * @param previousResult {@link SampleResult}
      * @param currentSampler {@link Sampler}
      * @param jmctx {@link JMeterContext}
      * @param vars {@link JMeterVariables}
      * @param script Javascript code
      * @param varName variable name
      * @return result as String
      * @throws InvalidVariableException
      */
     private String executeWithRhino(SampleResult previousResult,
             Sampler currentSampler, JMeterContext jmctx, JMeterVariables vars,
             String script, String varName)
             throws InvalidVariableException {
         Context cx = Context.enter();
         String resultStr = null;
         try {
 
             Scriptable scope = cx.initStandardObjects(null);
 
             // Set up some objects for the script to play with
             scope.put("log", scope, log); //$NON-NLS-1$
             scope.put("ctx", scope, jmctx); //$NON-NLS-1$
             scope.put("vars", scope, vars); //$NON-NLS-1$
             scope.put("props", scope, JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
             // Previously mis-spelt as theadName
             scope.put("threadName", scope, Thread.currentThread().getName()); //$NON-NLS-1$
             scope.put("sampler", scope, currentSampler); //$NON-NLS-1$
             scope.put("sampleResult", scope, previousResult); //$NON-NLS-1$
 
             Object result = cx.evaluateString(scope, script, "<cmd>", 1, null); //$NON-NLS-1$
 
             resultStr = Context.toString(result);
             if (varName != null && vars != null) {// vars can be null if run from TestPlan
                 vars.put(varName, resultStr);
             }
         } catch (RhinoException e) {
             log.error("Error processing Javascript: [" + script + "]\n", e);
             throw new InvalidVariableException("Error processing Javascript: [" + script + "]", e);
         } finally {
             Context.exit();
         }
         return resultStr;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1, 2);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/Jexl2Function.java b/src/functions/org/apache/jmeter/functions/Jexl2Function.java
index b47042cf8..8cce0a42c 100644
--- a/src/functions/org/apache/jmeter/functions/Jexl2Function.java
+++ b/src/functions/org/apache/jmeter/functions/Jexl2Function.java
@@ -1,162 +1,162 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.jexl2.Expression;
 import org.apache.commons.jexl2.JexlContext;
 import org.apache.commons.jexl2.JexlEngine;
 import org.apache.commons.jexl2.MapContext;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * A function which understands Commons JEXL2
  * @since 2.6
  */
 // For unit tests, see TestJexlFunction
 public class Jexl2Function extends AbstractFunction implements ThreadListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Jexl2Function.class);
 
     private static final String KEY = "__jexl2"; //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final ThreadLocal<JexlEngine> threadLocalJexl = new ThreadLocal<>();
 
     static
     {
         desc.add(JMeterUtils.getResString("jexl_expression")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException
     {
         String str = ""; //$NON-NLS-1$
 
         CompoundVariable var = (CompoundVariable) values[0];
         String exp = var.execute();
 
         String varName = ""; //$NON-NLS-1$
         if (values.length > 1) {
             varName = ((CompoundVariable) values[1]).execute().trim();
         }
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         try
         {
             JexlContext jc = new MapContext();
             jc.set("log", log); //$NON-NLS-1$
             jc.set("ctx", jmctx); //$NON-NLS-1$
             jc.set("vars", vars); //$NON-NLS-1$
             jc.set("props", JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
             // Previously mis-spelt as theadName
             jc.set("threadName", Thread.currentThread().getName()); //$NON-NLS-1$
             jc.set("sampler", currentSampler); //$NON-NLS-1$ (may be null)
             jc.set("sampleResult", previousResult); //$NON-NLS-1$ (may be null)
             jc.set("OUT", System.out);//$NON-NLS-1$
 
             // Now evaluate the script, getting the result
             Expression e = getJexlEngine().createExpression( exp );
             Object o = e.evaluate(jc);
             if (o != null)
             {
                 str = o.toString();
             }
             if (vars != null && varName.length() > 0) {// vars will be null on TestPlan
                 vars.put(varName, str);
             }
         } catch (Exception e)
         {
             log.error("An error occurred while evaluating the expression \""
                     + exp + "\"\n",e);
         }
         return str;
     }
 
     /**
      * Get JexlEngine from ThreadLocal
      * @return JexlEngine
      */
     private static JexlEngine getJexlEngine() {
         JexlEngine engine = threadLocalJexl.get();
         if(engine == null) {
             engine = new JexlEngine();
             engine.setCache(512);
             engine.setLenient(false);
             engine.setSilent(false);
             threadLocalJexl.set(engine);
         }
         return engine;
     }
     
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc()
     {
         return desc;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey()
     {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters)
             throws InvalidVariableException
     {
         checkParameterCount(parameters, 1, 2);
         values = parameters.toArray();
     }
 
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
         JexlEngine engine = threadLocalJexl.get();
         if(engine != null) {
             engine.clearCache();
             threadLocalJexl.remove();
         }
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/Jexl3Function.java b/src/functions/org/apache/jmeter/functions/Jexl3Function.java
index 098a64859..e5b8b7310 100644
--- a/src/functions/org/apache/jmeter/functions/Jexl3Function.java
+++ b/src/functions/org/apache/jmeter/functions/Jexl3Function.java
@@ -1,196 +1,196 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.jexl3.JexlArithmetic;
 import org.apache.commons.jexl3.JexlBuilder;
 import org.apache.commons.jexl3.JexlContext;
 import org.apache.commons.jexl3.JexlEngine;
 import org.apache.commons.jexl3.JexlExpression;
 import org.apache.commons.jexl3.MapContext;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * A function which understands Commons JEXL3
  * @since 3.0
  */
 // For unit tests, see TestJexlFunction
 public class Jexl3Function extends AbstractFunction implements ThreadListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Jexl3Function.class);
 
     private static final String KEY = "__jexl3"; //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final ThreadLocal<JexlEngine> threadLocalJexl = new ThreadLocal<>();
 
     static
     {
         desc.add(JMeterUtils.getResString("jexl_expression")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException
     {
         String str = ""; //$NON-NLS-1$
 
         CompoundVariable var = (CompoundVariable) values[0];
         String exp = var.execute();
 
         String varName = ""; //$NON-NLS-1$
         if (values.length > 1) {
             varName = ((CompoundVariable) values[1]).execute().trim();
         }
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         try
         {
             JexlContext jc = new MapContext();
             jc.set("log", log); //$NON-NLS-1$
             jc.set("ctx", jmctx); //$NON-NLS-1$
             jc.set("vars", vars); //$NON-NLS-1$
             jc.set("props", JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
             // Previously mis-spelt as theadName
             jc.set("threadName", Thread.currentThread().getName()); //$NON-NLS-1$
             jc.set("sampler", currentSampler); //$NON-NLS-1$ (may be null)
             jc.set("sampleResult", previousResult); //$NON-NLS-1$ (may be null)
             jc.set("OUT", System.out);//$NON-NLS-1$
 
             // Now evaluate the script, getting the result
             JexlExpression e = getJexlEngine().createExpression( exp );
             Object o = e.evaluate(jc);
             if (o != null)
             {
                 str = o.toString();
             }
             if (vars != null && varName.length() > 0) {// vars will be null on TestPlan
                 vars.put(varName, str);
             }
         } catch (Exception e)
         {
             log.error("An error occurred while evaluating the expression \""
                     + exp + "\"\n",e);
         }
         return str;
     }
     
     /**
      * FIXME Remove when upgrading to commons-jexl3-3.1
      *
      */
     private static class JMeterArithmetic extends JexlArithmetic {
         public JMeterArithmetic(boolean astrict) {
             super(astrict);
         }
 
         /**
          * A workaround to create an operator overload.
          *  the 'size' method is discovered through introspection as
          * an overload of the 'size' operator; this creates an entry in a cache for
          * that arithmetic class avoiding to re-discover the operator overloads
          * (Uberspect) on each execution. So, no, this method is not called; it is just
          * meant as a workaround of the bug.
          * @see <a href="https://issues.apache.org/jira/browse/JEXL-186">JEXL-186</a>
          * @param jma an improbable parameter class
          * @return 1
          */
         @SuppressWarnings("unused")
         public int size(JMeterArithmetic jma) {
             return 1;
         }
     }
     /**
      * Get JexlEngine from ThreadLocal
      * @return JexlEngine
      */
     private static JexlEngine getJexlEngine() {
         JexlEngine engine = threadLocalJexl.get();
         if(engine == null) {
             engine = new JexlBuilder()
                     .cache(512)
                     .silent(true)
                     .strict(true)
                     // debug is true by default an impact negatively performances
                     // by a factory of 10
                     // Use JexlInfo if necessary
                     .debug(false)
                     // see https://issues.apache.org/jira/browse/JEXL-186
                     .arithmetic(new JMeterArithmetic(true))
                     .create();
             threadLocalJexl.set(engine);
         }
         return engine;
     }
     
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc()
     {
         return desc;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey()
     {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters)
             throws InvalidVariableException
     {
         checkParameterCount(parameters, 1, 2);
         values = parameters.toArray();
     }
 
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
         JexlEngine engine = threadLocalJexl.get();
         if(engine != null) {
             engine.clearCache();
             threadLocalJexl.remove();
         }
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/RandomFromMultipleVars.java b/src/functions/org/apache/jmeter/functions/RandomFromMultipleVars.java
index 26ed5399b..34a9f2f91 100644
--- a/src/functions/org/apache/jmeter/functions/RandomFromMultipleVars.java
+++ b/src/functions/org/apache/jmeter/functions/RandomFromMultipleVars.java
@@ -1,153 +1,153 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ThreadLocalRandom;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * Provides a RandomFromMultiResult function which returns a random element from a multi valued extracted variable.
  * Those kind of variable are extracted by:
  * - Regular Expression extractor
  * - JSON extractor
  * - CSS/JQuery extractor
  * - XPath Extractor
  * 
  * @since 3.1
  */
 public class RandomFromMultipleVars extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RandomFromMultipleVars.class);
 
     private static final List<String> desc = new LinkedList<>();
     private static final String KEY = "__RandomFromMultipleVars"; //$NON-NLS-1$
     private static final String SEPARATOR = "\\|"; //$NON-NLS-1$
     static {
         desc.add(JMeterUtils.getResString("random_multi_result_source_variable")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("random_multi_result_target_variable")); //$NON-NLS-1$
     }
 
     private CompoundVariable variablesNamesSplitBySeparator;
     private CompoundVariable varName;
 
     /**
      * No-arg constructor.
      */
     public RandomFromMultipleVars() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String variablesNamesSplitBySeparatorValue = variablesNamesSplitBySeparator.execute().trim();
         JMeterVariables vars = getVariables();
         String outputValue = "";
         String separator = "";
         if (vars != null) { // vars will be null on TestPlan
             List<String> results = new ArrayList<>();
             String[] variables = variablesNamesSplitBySeparatorValue.split(SEPARATOR);
             for (String varName : variables) {
                 if(!StringUtils.isEmpty(varName)) {
                     extractVariableValuesToList(varName, vars, results);
                 }
             }
 
             if(results.size() > 0) {
                 int randomIndex = ThreadLocalRandom.current().nextInt(0, results.size());
                 outputValue = results.get(randomIndex);                
             } else {
                 if(log.isDebugEnabled()) {
                     log.debug("RandomFromMultiResult didn't find <var>_matchNr in variables :'"+variablesNamesSplitBySeparatorValue
                             +"' using separator:'"+separator+"', will return empty value");
                 }
             }
 
             if (varName != null) {
                 final String varTrim = varName.execute().trim();
                 if (varTrim.length() > 0){ 
                     vars.put(varTrim, outputValue);
                 }
             }
         }    
         return outputValue;
 
     }
 
     /**
      * @param variableName String
      * @param vars {@link JMeterVariables}
      * @param results {@link List} where results are stored
      * @throws NumberFormatException
      */
     private void extractVariableValuesToList(String variableName,
             JMeterVariables vars, List<String> results)
             throws NumberFormatException {
         String matchNumberAsStr = vars.get(variableName+"_matchNr");
         int matchNumber = 0;
         if(!StringUtils.isEmpty(matchNumberAsStr)) {
             matchNumber = Integer.parseInt(matchNumberAsStr);
         }
         if(matchNumber > 0) {
             for (int i = 1; i <= matchNumber; i++) {
                 results.add(vars.get(variableName+"_"+i));
             }
         } else {
             String value = vars.get(variableName);
             if(!StringUtils.isEmpty(value)) {
                 results.add(value);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1, 2);
         Object[] values = parameters.toArray();
         variablesNamesSplitBySeparator = (CompoundVariable) values[0];
         if (values.length>1){
             varName = (CompoundVariable) values[1];
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/RandomString.java b/src/functions/org/apache/jmeter/functions/RandomString.java
index aff290691..0397f4084 100644
--- a/src/functions/org/apache/jmeter/functions/RandomString.java
+++ b/src/functions/org/apache/jmeter/functions/RandomString.java
@@ -1,131 +1,131 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.lang3.RandomStringUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Provides a RandomString function which returns a random String of length (first argument) 
  * using characters (second argument)
  * @since 2.6
  */
 public class RandomString extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RandomString.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__RandomString"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("random_string_length")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("random_string_chars_to_use")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private CompoundVariable[] values;
 
     private static final int MAX_PARAM_COUNT = 3;
 
     private static final int MIN_PARAM_COUNT = 1;
     
     private static final int CHARS = 2;
 
     private static final int PARAM_NAME = 3;
 
     /**
      * No-arg constructor.
      */
     public RandomString() {
         super();
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         int length = Integer.parseInt(values[0].execute());
 
         String charsToUse = null;//means no restriction
         if (values.length >= CHARS) {
             charsToUse = (values[CHARS - 1]).execute().trim();
             if (charsToUse.length() <= 0) { // empty chars, return to null
                 charsToUse = null;
             }
         }
 
         String myName = "";//$NON-NLS-1$
         if (values.length >= PARAM_NAME) {
             myName = (values[PARAM_NAME - 1]).execute().trim();
         }
 
         String myValue = null;
         if(StringUtils.isEmpty(charsToUse)) {
             myValue = RandomStringUtils.random(length);
         } else {
             myValue = RandomStringUtils.random(length, charsToUse);
         }
  
         if (myName.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null) {// Can be null if called from Config item testEnded() method
                 vars.put(myName, myValue);
             }
         }
 
         if (log.isDebugEnabled()) {
             String tn = Thread.currentThread().getName();
             log.debug(tn + " name:" //$NON-NLS-1$
                     + myName + " value:" + myValue);//$NON-NLS-1$
         }
 
         return myValue;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
         values = parameters.toArray(new CompoundVariable[parameters.size()]);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/RegexFunction.java b/src/functions/org/apache/jmeter/functions/RegexFunction.java
index 8fc7b0ea4..55dfdc5c1 100644
--- a/src/functions/org/apache/jmeter/functions/RegexFunction.java
+++ b/src/functions/org/apache/jmeter/functions/RegexFunction.java
@@ -1,286 +1,286 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ThreadLocalRandom;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcher;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Util;
 /**
  * Implements regular expression parsing of sample results and variables
  * @since 1.X
  */
 
 // @see TestRegexFunction for unit tests
 
 public class RegexFunction extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RegexFunction.class);
 
     public static final String ALL = "ALL"; //$NON-NLS-1$
 
     public static final String RAND = "RAND"; //$NON-NLS-1$
 
     public static final String KEY = "__regexFunction"; //$NON-NLS-1$
 
     private Object[] values;// Parameters are stored here
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String TEMPLATE_PATTERN = "\\$(\\d+)\\$";  //$NON-NLS-1$
     /** initialised to the regex \$(\d+)\$ */
     private final Pattern templatePattern;
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 2;
 
     private static final int MAX_PARAMETER_COUNT = 7;
     static {
         desc.add(JMeterUtils.getResString("regexfunc_param_1"));// regex //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("regexfunc_param_2"));// template //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("regexfunc_param_3"));// which match //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("regexfunc_param_4"));// between text //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("regexfunc_param_5"));// default text //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); // output variable name //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("regexfunc_param_7"));// input variable //$NON-NLS-1$
     }
 
     public RegexFunction() {
         templatePattern = JMeterUtils.getPatternCache().getPattern(TEMPLATE_PATTERN,
                 Perl5Compiler.READ_ONLY_MASK);
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String valueIndex = ""; //$NON-NLS-1$
         String defaultValue = ""; //$NON-NLS-1$
         String between = ""; //$NON-NLS-1$ 
         String name = ""; //$NON-NLS-1$
         String inputVariable = ""; //$NON-NLS-1$
         Pattern searchPattern;
         Object[] tmplt;
         try {
             searchPattern = JMeterUtils.getPatternCache().getPattern(((CompoundVariable) values[0]).execute(),
                     Perl5Compiler.READ_ONLY_MASK);
             tmplt = generateTemplate(((CompoundVariable) values[1]).execute());
 
             if (values.length > 2) {
                 valueIndex = ((CompoundVariable) values[2]).execute();
             }
             if (valueIndex.length() == 0) {
                 valueIndex = "1"; //$NON-NLS-1$
             }
 
             if (values.length > 3) {
                 between = ((CompoundVariable) values[3]).execute();
             }
 
             if (values.length > 4) {
                 String dv = ((CompoundVariable) values[4]).execute();
                 if (dv.length() != 0) {
                     defaultValue = dv;
                 }
             }
 
             if (values.length > 5) {
                 name = ((CompoundVariable) values[5]).execute();
             }
 
             if (values.length > 6) {
                 inputVariable = ((CompoundVariable) values[6]).execute();
             }
         } catch (MalformedCachePatternException e) {
             log.error("Malformed cache pattern:"+values[0], e);
             throw new InvalidVariableException("Malformed cache pattern:"+values[0], e);
         }
 
         // Relatively expensive operation, so do it once
         JMeterVariables vars = getVariables();
 
         if (vars == null){// Can happen if called during test closedown
             return defaultValue;
         }
 
         if (name.length() > 0) {
             vars.put(name, defaultValue);
         }
 
         String textToMatch=null;
 
         if (inputVariable.length() > 0){
             textToMatch=vars.get(inputVariable);
         } else if (previousResult != null){
             textToMatch = previousResult.getResponseDataAsString();
         }
 
         if (textToMatch == null || textToMatch.length() == 0) {
             return defaultValue;
         }
 
         List<MatchResult> collectAllMatches = new ArrayList<>();
         try {
             PatternMatcher matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = new PatternMatcherInput(textToMatch);
             while (matcher.contains(input, searchPattern)) {
                 MatchResult match = matcher.getMatch();
                 collectAllMatches.add(match);
             }
         } finally {
             if (name.length() > 0){
                 vars.put(name + "_matchNr", Integer.toString(collectAllMatches.size())); //$NON-NLS-1$
             }
         }
 
         if (collectAllMatches.size() == 0) {
             return defaultValue;
         }
 
         if (valueIndex.equals(ALL)) {
             StringBuilder value = new StringBuilder();
             Iterator<MatchResult> it = collectAllMatches.iterator();
             boolean first = true;
             while (it.hasNext()) {
                 if (!first) {
                     value.append(between);
                 } else {
                     first = false;
                 }
                 value.append(generateResult(it.next(), name, tmplt, vars));
             }
             return value.toString();
         } else if (valueIndex.equals(RAND)) {
             MatchResult result = collectAllMatches.get(ThreadLocalRandom.current().nextInt(collectAllMatches.size()));
             return generateResult(result, name, tmplt, vars);
         } else {
             try {
                 int index = Integer.parseInt(valueIndex) - 1;
                 MatchResult result = collectAllMatches.get(index);
                 return generateResult(result, name, tmplt, vars);
             } catch (NumberFormatException e) {
                 float ratio = Float.parseFloat(valueIndex);
                 MatchResult result = collectAllMatches
                         .get((int) (collectAllMatches.size() * ratio + .5) - 1);
                 return generateResult(result, name, tmplt, vars);
             } catch (IndexOutOfBoundsException e) {
                 return defaultValue;
             }
         }
 
     }
 
     private void saveGroups(MatchResult result, String namep, JMeterVariables vars) {
         if (result != null) {
             for (int x = 0; x < result.groups(); x++) {
                 vars.put(namep + "_g" + x, result.group(x)); //$NON-NLS-1$
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
     private String generateResult(MatchResult match, String namep, Object[] template, JMeterVariables vars) {
         saveGroups(match, namep, vars);
         StringBuilder result = new StringBuilder();
         for (Object t : template) {
             if (t instanceof String) {
                 result.append(t);
             } else {
                 result.append(match.group(((Integer) t).intValue()));
             }
         }
         if (namep.length() > 0){
             vars.put(namep, result.toString());
         }
         return result.toString();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
         values = parameters.toArray();
     }
 
     private Object[] generateTemplate(String rawTemplate) {
         List<String> pieces = new ArrayList<>();
         // String or Integer
         List<Object> combined = new LinkedList<>();
         PatternMatcher matcher = JMeterUtils.getMatcher();
         Util.split(pieces, matcher, templatePattern, rawTemplate);
         PatternMatcherInput input = new PatternMatcherInput(rawTemplate);
         boolean startsWith = isFirstElementGroup(rawTemplate);
         if (startsWith) {
             pieces.remove(0);// Remove initial empty entry
         }
         Iterator<String> iter = pieces.iterator();
         while (iter.hasNext()) {
             boolean matchExists = matcher.contains(input, templatePattern);
             if (startsWith) {
                 if (matchExists) {
                     combined.add(Integer.valueOf(matcher.getMatch().group(1)));
                 }
                 combined.add(iter.next());
             } else {
                 combined.add(iter.next());
                 if (matchExists) {
                     combined.add(Integer.valueOf(matcher.getMatch().group(1)));
                 }
             }
         }
         if (matcher.contains(input, templatePattern)) {
             combined.add(Integer.valueOf(matcher.getMatch().group(1)));
         }
         return combined.toArray();
     }
 
     private boolean isFirstElementGroup(String rawData) {
         Pattern pattern = JMeterUtils.getPatternCache().getPattern("^\\$\\d+\\$",  //$NON-NLS-1$
                 Perl5Compiler.READ_ONLY_MASK);
         return JMeterUtils.getMatcher().contains(rawData, pattern);
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/SplitFunction.java b/src/functions/org/apache/jmeter/functions/SplitFunction.java
index aafd1f519..71b987293 100644
--- a/src/functions/org/apache/jmeter/functions/SplitFunction.java
+++ b/src/functions/org/apache/jmeter/functions/SplitFunction.java
@@ -1,129 +1,129 @@
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
 
 package org.apache.jmeter.functions;
 
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 // @see org.apache.jmeter.functions.PackageTest for unit tests
 
 /**
  * Function to split a string into variables
  * <p>
  * Parameters:
  * <ul>
  * <li>String to split</li>
  * <li>Variable name prefix</li>
  * <li>String to split on (optional, default is comma)</li>
  * </ul>
  * <p>
  * Returns: the input string
  * </p>
  * Also sets the variables:
  * <ul>
  * <li>VARNAME - the input string</li>
  * <li>VARNAME_n - number of fields found</li>
  * <li>VARNAME_1..n - fields</li>
  * </ul>
  * @since 2.0.2
  */
 public class SplitFunction extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SplitFunction.class);
 
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__split";// $NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 2;
 
     private static final int MAX_PARAMETER_COUNT = 3;
     static {
         desc.add(JMeterUtils.getResString("split_function_string"));   //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_param"));     //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("split_function_separator"));//$NON-NLS-1$
     }
 
     private Object[] values;
 
     public SplitFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         JMeterVariables vars = getVariables();
 
         String stringToSplit = ((CompoundVariable) values[0]).execute();
         String varNamePrefix = ((CompoundVariable) values[1]).execute().trim();
         String splitString = ",";
 
         if (values.length > 2) { // Split string provided
             splitString = ((CompoundVariable) values[2]).execute();
         }
         if (log.isDebugEnabled()){
             log.debug("Split "+stringToSplit+ " using "+ splitString+ " into "+varNamePrefix);
         }
         String[] parts = JOrphanUtils.split(stringToSplit, splitString, "?");// $NON-NLS-1$
 
         vars.put(varNamePrefix, stringToSplit);
         vars.put(varNamePrefix + "_n", Integer.toString(parts.length));// $NON-NLS-1$ 
         for (int i = 1; i <= parts.length; i++) {
             if (log.isDebugEnabled()){
                 log.debug(parts[i-1]);
             }
             vars.put(varNamePrefix + "_" + i, parts[i - 1]);// $NON-NLS-1$
         }
         vars.remove(varNamePrefix + "_" + (parts.length+1));
         return stringToSplit;
 
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/StringFromFile.java b/src/functions/org/apache/jmeter/functions/StringFromFile.java
index 1d7a7c5bc..9d68eb299 100644
--- a/src/functions/org/apache/jmeter/functions/StringFromFile.java
+++ b/src/functions/org/apache/jmeter/functions/StringFromFile.java
@@ -1,339 +1,339 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.BufferedReader;
 import java.io.FileReader;
 import java.io.IOException;
 import java.text.DecimalFormat;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JMeterStopThreadException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * StringFromFile Function to read a String from a text file.
  *
  * Parameters:
  * - file name
  * - variable name (optional - defaults to StringFromFile_)
  *
  * Returns:
  * - the next line from the file
  * - or **ERR** if an error occurs
  * - value is also saved in the variable for later re-use.
  *
  * Ensure that different variable names are used for each call to the function
  *
  *
  * Notes:
  * <ul>
  * <li>JMeter instantiates a single copy of each function for every reference in the test plan</li>
  * <li>Function instances are shared between threads.</li>
  * <li>Each StringFromFile instance reads the file independently. The output variable can be used to save the
  * value for later use in the same thread.</li>
  * <li>The file name is resolved at file (re-)open time; the file is initially opened on first execution (which could be any thread)</li>
  * <li>the output variable name is resolved every time the function is invoked</li>
  * </ul>
  * Because function instances are shared, it does not make sense to use the thread number as part of the file name.
  * @since 1.9
  */
 public class StringFromFile extends AbstractFunction implements TestStateListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StringFromFile.class);
 
     // Only modified by static block so no need to synchronize subsequent read-only access
     private static final List<String> desc = new LinkedList<>();
 
     private static final String KEY = "__StringFromFile";//$NON-NLS-1$
 
     static final String ERR_IND = "**ERR**";//$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("string_from_file_file_name"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("string_from_file_seq_start"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("string_from_file_seq_final"));//$NON-NLS-1$
     }
 
     private static final int MIN_PARAM_COUNT = 1;
 
     private static final int PARAM_NAME = 2;
 
     private static final int PARAM_START = 3;
 
     private static final int PARAM_END = 4;
 
     private static final int MAX_PARAM_COUNT = 4;
 
     private static final int COUNT_UNUSED = -2;
 
     // @GuardedBy("this")
     private Object[] values;
 
     // @GuardedBy("this")
     private BufferedReader myBread = null; // Buffered reader
 
     // @GuardedBy("this")
     private boolean firstTime = false; // should we try to open the file?
 
     // @GuardedBy("this")
     private String fileName; // needed for error messages
 
     // @GuardedBy("this")
     private int myStart = COUNT_UNUSED;
 
     // @GuardedBy("this")
     private int myCurrent = COUNT_UNUSED;
 
     // @GuardedBy("this")
     private int myEnd = COUNT_UNUSED;
 
     public StringFromFile() {
         if (log.isDebugEnabled()) {
             log.debug("++++++++ Construct " + this);
         }
     }
 
     /**
      * Close file and log
      */
     private synchronized void closeFile() {
         if (myBread == null) {
             return;
         }
         String tn = Thread.currentThread().getName();
         log.info(tn + " closing file " + fileName);//$NON-NLS-1$
         try {
             myBread.close();
         } catch (IOException e) {
             log.error("closeFile() error: " + e.toString(), e);//$NON-NLS-1$
         }
     }
     
     private synchronized void openFile() {
         String tn = Thread.currentThread().getName();
         fileName = ((CompoundVariable) values[0]).execute();
 
         String start = "";
         if (values.length >= PARAM_START) {
             start = ((CompoundVariable) values[PARAM_START - 1]).execute();
             try {
                 // Low chances to be non numeric, we parse
                 myStart = Integer.parseInt(start);
             } catch(NumberFormatException e) {
                 myStart = COUNT_UNUSED;// Don't process invalid numbers
                 log.warn("Exception parsing "+start + " as int, value will not be considered as Start Number sequence");
             }
         }
         // Have we used myCurrent yet?
         // Set to 1 if start number is missing (to allow for end without start)
         if (myCurrent == COUNT_UNUSED) {
             myCurrent = myStart == COUNT_UNUSED ? 1 : myStart;
         }
 
         if (values.length >= PARAM_END) {
             String tmp = ((CompoundVariable) values[PARAM_END - 1]).execute();
             try {
                 // Low chances to be non numeric, we parse
                 myEnd = Integer.parseInt(tmp);
             } catch(NumberFormatException e) {
                 myEnd = COUNT_UNUSED;// Don't process invalid numbers (including "")
                 log.warn("Exception parsing "+tmp + " as int, value will not be considered as End Number sequence");
             }
         }
 
         if (values.length >= PARAM_START) {
             log.info(tn + " Start = " + myStart + " Current = " + myCurrent + " End = " + myEnd);//$NON-NLS-1$
             if (myEnd != COUNT_UNUSED) {
                 if (myCurrent > myEnd) {
                     log.info(tn + " No more files to process, " + myCurrent + " > " + myEnd);//$NON-NLS-1$
                     myBread = null;
                     return;
                 }
             }
             /*
              * DecimalFormat adds the number to the end of the format if there
              * are no formatting characters, so we need a way to prevent this
              * from messing up the file name.
              *
              */
             if (myStart != COUNT_UNUSED) // Only try to format if there is a
                                             // number
             {
                 log.info(tn + " using format " + fileName);
                 try {
                     DecimalFormat myFormatter = new DecimalFormat(fileName);
                     fileName = myFormatter.format(myCurrent);
                 } catch (NumberFormatException e) {
                     log.warn("Bad file name format ", e);
                 }
             }
             myCurrent++;// for next time
         }
 
         log.info(tn + " opening file " + fileName);//$NON-NLS-1$
         try {
             myBread = new BufferedReader(new FileReader(fileName));
         } catch (Exception e) {
             log.error("openFile() error: " + e.toString());//$NON-NLS-1$
             myBread = null;
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String myValue = ERR_IND;
         String myName = "StringFromFile_";//$NON-NLS-1$
         if (values.length >= PARAM_NAME) {
             myName = ((CompoundVariable) values[PARAM_NAME - 1]).execute().trim();
         }
 
         /*
          * To avoid re-opening the file repeatedly after an error, only try to
          * open it in the first execute() call (It may be re=opened at EOF, but
          * that will cause at most one failure.)
          */
         if (firstTime) {
             openFile();
             firstTime = false;
         }
 
         if (null != myBread) { // Did we open the file?
             try {
                 String line = myBread.readLine();
                 if (line == null) { // EOF, re-open file
                     String tn = Thread.currentThread().getName();
                     log.info(tn + " EOF on  file " + fileName);//$NON-NLS-1$
                     closeFile();
                     openFile();
                     if (myBread != null) {
                         line = myBread.readLine();
                     } else {
                         line = ERR_IND;
                         if (myEnd != COUNT_UNUSED) {// Are we processing a file
                                                     // sequence?
                             log.info(tn + " Detected end of sequence.");
                             throw new JMeterStopThreadException("End of sequence");
                         }
                     }
                 }
                 myValue = line;
             } catch (IOException e) {
                 String tn = Thread.currentThread().getName();
                 log.error(tn + " error reading file " + e.toString());//$NON-NLS-1$
             }
         } else { // File was not opened successfully
             if (myEnd != COUNT_UNUSED) {// Are we processing a file sequence?
                 String tn = Thread.currentThread().getName();
                 log.info(tn + " Detected end of sequence.");
                 throw new JMeterStopThreadException("End of sequence");
             }
         }
 
         if (myName.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null) {// Can be null if called from Config item testEnded() method
                 vars.put(myName, myValue);
             }
         }
 
         if (log.isDebugEnabled()) {
             String tn = Thread.currentThread().getName();
             log.debug(tn + " name:" //$NON-NLS-1$
                     + myName + " value:" + myValue);//$NON-NLS-1$
         }
 
         return myValue;
 
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
 
         log.debug(this + "::StringFromFile.setParameters()");//$NON-NLS-1$
         checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
         values = parameters.toArray();
 
         StringBuilder sb = new StringBuilder(40);
         sb.append("setParameters(");//$NON-NLS-1$
         for (int i = 0; i < values.length; i++) {
             if (i > 0) {
                 sb.append(',');
             }
             sb.append(((CompoundVariable) values[i]).getRawParameters());
         }
         sb.append(')');//$NON-NLS-1$
         log.info(sb.toString());
 
         // N.B. setParameters is called before the test proper is started,
         // and thus variables are not interpreted at this point
         // So defer the file open until later to allow variable file names to be
         // used.
         firstTime = true;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted() {
         //
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted(String host) {
         //
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded() {
         this.testEnded(""); //$NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded(String host) {
         closeFile();
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/XPath.java b/src/functions/org/apache/jmeter/functions/XPath.java
index b882225b9..4f49ca1b3 100644
--- a/src/functions/org/apache/jmeter/functions/XPath.java
+++ b/src/functions/org/apache/jmeter/functions/XPath.java
@@ -1,126 +1,126 @@
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
 
 package org.apache.jmeter.functions;
 
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 // @see org.apache.jmeter.functions.PackageTest for unit tests
 
 /**
  * The function represented by this class allows data to be read from XML files.
  * Syntax is similar to the CVSRead function. The function allows the test to
  * line-thru the nodes in the XML file - one node per each test. E.g. inserting
  * the following in the test scripts :
  *
  * ${_XPath(c:/BOF/abcd.xml,/xpath/)} // match the (first) node
  * ${_XPath(c:/BOF/abcd.xml,/xpath/)} // Go to next match of '/xpath/' expression
  *
  * NOTE: A single instance of each different file/expression combination
  * is opened and used for all threads.
  * @since 2.0.3
  */
 public class XPath extends AbstractFunction {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPath.class);
 
     // static {
     // LoggingManager.setPriority("DEBUG","jmeter");
     // LoggingManager.setTarget(new java.io.PrintWriter(System.out));
     // }
     private static final String KEY = "__XPath"; // Function name //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<>();
 
     private Object[] values; // Parameter list
 
     static {
         desc.add(JMeterUtils.getResString("xpath_file_file_name")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("xpath_expression")); //$NON-NLS-1$
     }
 
     public XPath() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String myValue = ""; //$NON-NLS-1$
 
         String fileName = ((CompoundVariable) values[0]).execute();
         String xpathString = ((CompoundVariable) values[1]).execute();
 
         if (log.isDebugEnabled()){
             log.debug("execute (" + fileName + " " + xpathString + ")   ");
         }
 
         myValue = XPathWrapper.getXPathString(fileName, xpathString);
 
         if (log.isDebugEnabled()){
             log.debug("execute value: " + myValue);
         }
 
         return myValue;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return KEY;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         log.debug("setParameter - Collection.size=" + parameters.size());
 
         values = parameters.toArray();
 
         if (log.isDebugEnabled()) {
             for (int i = 0; i < parameters.size(); i++) {
                 log.debug("i:" + ((CompoundVariable) values[i]).execute());
             }
         }
 
         checkParameterCount(parameters, 2);
 
         /*
          * Need to reset the containers for repeated runs; about the only way
          * for functions to detect that a run is starting seems to be the
          * setParameters() call.
          */
         XPathWrapper.clearAll();// TODO only clear the relevant entry - if possible...
 
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/XPathFileContainer.java b/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
index a665a240f..de179c94c 100644
--- a/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
+++ b/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
@@ -1,124 +1,124 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.BufferedInputStream;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jmeter.util.XPathUtil;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 
 //@see org.apache.jmeter.functions.PackageTest for unit tests
 
 /**
  * File data container for XML files Data is accessible via XPath
  *
  */
 public class XPathFileContainer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathFileContainer.class);
 
     private final NodeList nodeList;
 
     private final String fileName; // name of the file
 
     /** Keeping track of which row is next to be read. */
     private int nextRow;// probably does not need to be synch (always accessed through ThreadLocal?)
     int getNextRow(){// give access to Test code
         return nextRow;
     }
 
     public XPathFileContainer(String file, String xpath) throws FileNotFoundException, IOException,
             ParserConfigurationException, SAXException, TransformerException {
         if(log.isDebugEnabled()) {
             log.debug("XPath(" + file + ") xpath " + xpath);
         }
         fileName = file;
         nextRow = 0;
         nodeList=load(xpath);
     }
 
     private NodeList load(String xpath) throws IOException, FileNotFoundException, ParserConfigurationException, SAXException,
             TransformerException {
         InputStream fis = null;
         NodeList nl = null;
         try {
             DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
 
             fis = new BufferedInputStream(new FileInputStream(fileName));
             nl = XPathUtil.selectNodeList(builder.parse(fis), xpath);
             if(log.isDebugEnabled()) {
                 log.debug("found " + nl.getLength());
             }
         } catch (TransformerException | SAXException
                 | ParserConfigurationException | IOException e) {
             log.warn(e.toString());
             throw e;
         } finally {
             JOrphanUtils.closeQuietly(fis);
         }
         return nl;
     }
 
     public String getXPathString(int num) {
         return nodeList.item(num).getNodeValue();
     }
 
     /**
      * Returns the next row to the caller, and updates it, allowing for wrap
      * round
      *
      * @return the first free (unread) row
      *
      */
     public int nextRow() {
         int row = nextRow;
         nextRow++;
         if (nextRow >= size())// 0-based
         {
             nextRow = 0;
         }
         log.debug(new StringBuilder("Row: ").append(row).toString());
         return row;
     }
 
     public int size() {
         return (nodeList == null) ? -1 : nodeList.getLength();
     }
 
     /**
      * @return the file name for this class
      */
     public String getFileName() {
         return fileName;
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/XPathWrapper.java b/src/functions/org/apache/jmeter/functions/XPathWrapper.java
index 37f9ff108..1071db429 100644
--- a/src/functions/org/apache/jmeter/functions/XPathWrapper.java
+++ b/src/functions/org/apache/jmeter/functions/XPathWrapper.java
@@ -1,127 +1,127 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.xml.sax.SAXException;
 
 /**
  * This class wraps the XPathFileContainer for use across multiple threads.
  *
  * It maintains a list of nodelist containers, one for each file/xpath combination
  *
  */
 final class XPathWrapper {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathWrapper.class);
 
     /*
      * This Map serves two purposes:
      * <ul>
      *   <li>maps names to  containers</li>
      *   <li>ensures only one container per file across all threads</li>
      * </ul>
      * The key is the concatenation of the file name and the XPath string
      */
     //@GuardedBy("fileContainers")
     private static final Map<String, XPathFileContainer> fileContainers =
             new HashMap<>();
 
     /* The cache of file packs - for faster local access */
     private static final ThreadLocal<Map<String, XPathFileContainer>> filePacks =
         new ThreadLocal<Map<String, XPathFileContainer>>() {
         @Override
         protected Map<String, XPathFileContainer> initialValue() {
             return new HashMap<>();
         }
     };
 
     private XPathWrapper() {// Prevent separate instantiation
         super();
     }
 
     private static XPathFileContainer open(String file, String xpathString) {
         String tname = Thread.currentThread().getName();
         log.info(tname+": Opening " + file);
         XPathFileContainer frcc=null;
         try {
             frcc = new XPathFileContainer(file, xpathString);
         } catch (TransformerException | SAXException
                 | ParserConfigurationException | IOException e) {
             log.warn(e.getLocalizedMessage());
         }
         return frcc;
     }
 
     /**
      * Not thread-safe - must be called from a synchronized method.
      *
      * @param file name of the file
      * @param xpathString xpath to look up in file
      * @return the next row from the file container
      */
     public static String getXPathString(String file, String xpathString) {
         Map<String, XPathFileContainer> my = filePacks.get();
         String key = file+xpathString;
         XPathFileContainer xpfc = my.get(key);
         if (xpfc == null) // We don't have a local copy
         {
             synchronized(fileContainers){
                 xpfc = fileContainers.get(key);
                 if (xpfc == null) { // There's no global copy either
                     xpfc=open(file, xpathString);
                 }
                 if (xpfc != null) {
                     fileContainers.put(key, xpfc);// save the global copy
                 }
             }
             // TODO improve the error handling
             if (xpfc == null) {
                 log.error("XPathFileContainer is null!");
                 return ""; //$NON-NLS-1$
             }
             my.put(key,xpfc); // save our local copy
         }
         if (xpfc.size()==0){
             log.warn("XPathFileContainer has no nodes: "+file+" "+xpathString);
             return ""; //$NON-NLS-1$
         }
         int currentRow = xpfc.nextRow();
         log.debug("getting match number " + currentRow);
         return xpfc.getXPathString(currentRow);
     }
 
     public static void clearAll() {
         log.debug("clearAll()");
         filePacks.get().clear();
         String tname = Thread.currentThread().getName();
         log.info(tname+": clearing container");
         synchronized (fileContainers) {
             fileContainers.clear();
         }
     }
 }
