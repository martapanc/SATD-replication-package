diff --git a/src/core/org/apache/jmeter/engine/util/CompoundVariable.java b/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
index b31498548..0eb1244a8 100644
--- a/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
+++ b/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
@@ -1,237 +1,241 @@
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
 
 package org.apache.jmeter.engine.util;
 
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.functions.Function;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.log.Logger;
 
 /**
  * CompoundFunction.
  *
  */
 public class CompoundVariable implements Function {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private String rawParameters;
 
     private static final FunctionParser functionParser = new FunctionParser();
 
     // Created during class init; not modified thereafter 
     private static final Map<String, Class<? extends Function>> functions =
         new HashMap<String, Class<? extends Function>>();
 
     private boolean hasFunction, isDynamic;
 
-    private String permanentResults = ""; // $NON-NLS-1$
+    private String permanentResults;
 
     private LinkedList<Object> compiledComponents = new LinkedList<Object>();
 
     static {
         try {
             final String contain = // Classnames must contain this string [.functions.]
                 JMeterUtils.getProperty("classfinder.functions.contain"); // $NON-NLS-1$
             final String notContain = // Classnames must not contain this string [.gui.]
                 JMeterUtils.getProperty("classfinder.functions.notContain"); // $NON-NLS-1$
             if (contain!=null){
                 log.info("Note: Function class names must contain the string: '"+contain+"'");
             }
             if (notContain!=null){
                 log.info("Note: Function class names must not contain the string: '"+notContain+"'");
             }
             List<String> classes = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(),
                     new Class[] { Function.class }, true, contain, notContain);
             Iterator<String> iter = classes.iterator();
             while (iter.hasNext()) {
                 Function tempFunc = (Function) Class.forName(iter.next()).newInstance();
                 String referenceKey = tempFunc.getReferenceKey();
                 if (referenceKey.length() > 0) { // ignore self
                     functions.put(referenceKey, tempFunc.getClass());
                     // Add alias for original StringFromFile name (had only one underscore)
                     if (referenceKey.equals("__StringFromFile")){//$NON-NLS-1$
                         functions.put("_StringFromFile", tempFunc.getClass());//$NON-NLS-1$
                     }
                 }
             }
             final int functionCount = functions.size();
             if (functionCount == 0){
                 log.warn("Did not find any functions");
             } else {
                 log.debug("Function count: "+functionCount);
             }
         } catch (Exception err) {
             log.error("", err);
         }
     }
 
     public CompoundVariable() {
-        super();
-        isDynamic = true;
         hasFunction = false;
     }
 
     public CompoundVariable(String parameters) {
         this();
         try {
             setParameters(parameters);
         } catch (InvalidVariableException e) {
             // TODO should level be more than debug ?
             if(log.isDebugEnabled()) {
                 log.debug("Invalid variable:"+ parameters, e);
             }
         }
     }
 
     public String execute() {
-        if (isDynamic) {
+        if (isDynamic || permanentResults == null) {
             JMeterContext context = JMeterContextService.getContext();
             SampleResult previousResult = context.getPreviousResult();
             Sampler currentSampler = context.getCurrentSampler();
             return execute(previousResult, currentSampler);
         }
         return permanentResults; // $NON-NLS-1$
     }
 
     /**
      * Allows the retrieval of the original String prior to it being compiled.
      *
      * @return String
      */
     public String getRawParameters() {
         return rawParameters;
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler) {
         if (compiledComponents == null || compiledComponents.size() == 0) {
             return ""; // $NON-NLS-1$
         }
-        boolean testDynamic = false;
         StringBuilder results = new StringBuilder();
         for (Object item : compiledComponents) {
             if (item instanceof Function) {
-                testDynamic = true;
                 try {
                     results.append(((Function) item).execute(previousResult, currentSampler));
                 } catch (InvalidVariableException e) {
                     // TODO should level be more than debug ?
                     if(log.isDebugEnabled()) {
                         log.debug("Invalid variable:"+item, e);
                     }
                 }
             } else if (item instanceof SimpleVariable) {
-                testDynamic = true;
                 results.append(((SimpleVariable) item).toString());
             } else {
                 results.append(item);
             }
         }
-        if (!testDynamic) {
-            isDynamic = false;
+        if (!isDynamic) {
             permanentResults = results.toString();
         }
         return results.toString();
     }
 
     @SuppressWarnings("unchecked") // clone will produce correct type
     public CompoundVariable getFunction() {
         CompoundVariable func = new CompoundVariable();
         func.compiledComponents = (LinkedList<Object>) compiledComponents.clone();
         func.rawParameters = rawParameters;
+        func.hasFunction = hasFunction;
+        func.isDynamic = isDynamic;
         return func;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return new LinkedList<String>();
     }
 
     public void clear() {
         // TODO should this also clear isDynamic, rawParameters, permanentResults?
         hasFunction = false;
         compiledComponents.clear();
     }
 
     public void setParameters(String parameters) throws InvalidVariableException {
         this.rawParameters = parameters;
         if (parameters == null || parameters.length() == 0) {
             return;
         }
 
         compiledComponents = functionParser.compileString(parameters);
         if (compiledComponents.size() > 1 || !(compiledComponents.get(0) instanceof String)) {
             hasFunction = true;
         }
+        permanentResults = null; // To be calculated and cached on first execution
+        isDynamic = false;
+        for (Object item : compiledComponents) {
+            if (item instanceof Function || item instanceof SimpleVariable) {
+                isDynamic = true;
+                break;
+            }
+        }
     }
 
     static Object getNamedFunction(String functionName) throws InvalidVariableException {
         if (functions.containsKey(functionName)) {
             try {
                 return ((Class<?>) functions.get(functionName)).newInstance();
             } catch (Exception e) {
                 log.error("", e); // $NON-NLS-1$
                 throw new InvalidVariableException(e);
             }
         }
         return new SimpleVariable(functionName);
     }
 
     // For use by FunctionHelper
     public static Class<? extends Function> getFunctionClass(String className) {
         return functions.get(className);
     }
 
     // For use by FunctionHelper
     public static String[] getFunctionNames() {
         return functions.keySet().toArray(new String[functions.size()]);
     }
 
     public boolean hasFunction() {
         return hasFunction;
     }
 
     // Dummy methods needed by Function interface
 
     /** {@inheritDoc} */
     @Override
     public String getReferenceKey() {
         return ""; // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
     }
 }
diff --git a/src/core/org/apache/jmeter/functions/AbstractFunction.java b/src/core/org/apache/jmeter/functions/AbstractFunction.java
index f2b54e9eb..4c95737d7 100644
--- a/src/core/org/apache/jmeter/functions/AbstractFunction.java
+++ b/src/core/org/apache/jmeter/functions/AbstractFunction.java
@@ -1,147 +1,144 @@
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
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 
 /**
  * Provides common methods for all functions
  */
 public abstract class AbstractFunction implements Function {
 
     /**
      * <p><b>
-     * N.B. setParameters() and execute() are called from different threads,
-     * so both must be synchronized unless there are no parameters to save
+     * N.B. execute() should be synchronized if function is operating with non-thread-safe
+     * objects (e.g. operates with files).
      * </b></p>
+     * JMeter ensures setParameters() happens-before execute(): setParameters is executed in main thread,
+     * and worker threads are started after that.
      * @see Function#execute(SampleResult, Sampler)
      */
     @Override
     abstract public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException;
 
     public String execute() throws InvalidVariableException {
         JMeterContext context = JMeterContextService.getContext();
         SampleResult previousResult = context.getPreviousResult();
         Sampler currentSampler = context.getCurrentSampler();
         return execute(previousResult, currentSampler);
     }
 
     /**
      *
-     * <p><b>
-     * N.B. setParameters() and execute() are called from different threads,
-     * so both must be synchronized unless there are no parameters to save
-     * </b></p>
-     *
      * @see Function#setParameters(Collection)
      * <br/>
      * Note: This is always called even if no parameters are provided
      * (versions of JMeter after 2.3.1)
      */
     @Override
     abstract public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException;
 
     /**
      * @see Function#getReferenceKey()
      */
     @Override
     abstract public String getReferenceKey();
 
     /**
      * Gives access to the JMeter variables for the current thread.
      * 
      * @return a pointer to the JMeter variables.
      */
     protected JMeterVariables getVariables() {
         return JMeterContextService.getContext().getVariables();
     }
 
     /**
      * Utility method to check parameter counts.
      *
      * @param parameters collection of parameters
      * @param min minimum number of parameters allowed
      * @param max maximum number of parameters allowed
      *
      * @throws InvalidVariableException if the number of parameters is incorrect
      */
     protected void checkParameterCount(Collection<CompoundVariable> parameters, int min, int max)
         throws InvalidVariableException
     {
         int num = parameters.size();
         if ((num > max) || (num < min)) {
             throw new InvalidVariableException(
                     getReferenceKey() +
                     " called with wrong number of parameters. Actual: "+num+
                     (
                         min==max ?
                         ". Expected: "+min+"."
                         : ". Expected: >= "+min+" and <= "+max
                     )
                     );
         }
     }
 
     /**
      * Utility method to check parameter counts.
      *
      * @param parameters collection of parameters
      * @param count number of parameters expected
      *
      * @throws InvalidVariableException if the number of parameters is incorrect
      */
     protected void checkParameterCount(Collection<CompoundVariable> parameters, int count)
         throws InvalidVariableException
     {
         int num = parameters.size();
         if (num != count) {
             throw new InvalidVariableException(
                     getReferenceKey() +
                     " called with wrong number of parameters. Actual: "+num+". Expected: "+count+"."
                    );
         }
     }
 
     /**
      * Utility method to check parameter counts.
      *
      * @param parameters collection of parameters
      * @param minimum number of parameters expected
      *
      * @throws InvalidVariableException if the number of parameters is incorrect
      */
     protected void checkMinParameterCount(Collection<CompoundVariable> parameters, int minimum)
         throws InvalidVariableException
     {
         int num = parameters.size();
         if (num < minimum) {
             throw new InvalidVariableException(
                     getReferenceKey() +
                     " called with wrong number of parameters. Actual: "+num+". Expected at least: "+minimum+"."
                    );
         }
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/AbstractHostIPName.java b/src/functions/org/apache/jmeter/functions/AbstractHostIPName.java
index 376bb9d78..f807ed3c9 100644
--- a/src/functions/org/apache/jmeter/functions/AbstractHostIPName.java
+++ b/src/functions/org/apache/jmeter/functions/AbstractHostIPName.java
@@ -1,84 +1,84 @@
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
 
 abstract class AbstractHostIPName extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     static {
         // desc.add("Use fully qualified host name: TRUE/FALSE (Default FALSE)");
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public AbstractHostIPName() {
     }
 
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         /*
          * boolean fullHostName = false; if (((CompoundFunction) values[0])
          * .execute() .toLowerCase() .equals("true")) { fullHostName = true; }
          */
 
         String value = compute();
 
         if (values.length >= 1){// we have a variable name
             JMeterVariables vars = getVariables();
             if (vars != null) {// May be null if function is used on TestPlan
                 String varName = ((CompoundVariable) values[0]).execute().trim();
                 if (varName.length() > 0) {
                     vars.put(varName, value);
                 }
             }
         }
         return value;
 
     }
 
     abstract protected String compute();
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 0, 1);
         values = parameters.toArray();
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return desc;
     }
 }
diff --git a/src/functions/org/apache/jmeter/functions/CSVRead.java b/src/functions/org/apache/jmeter/functions/CSVRead.java
index 0e19b49b7..62367fade 100644
--- a/src/functions/org/apache/jmeter/functions/CSVRead.java
+++ b/src/functions/org/apache/jmeter/functions/CSVRead.java
@@ -1,158 +1,162 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
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
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String KEY = "__CSVRead"; // Function name //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<String>();
 
     private Object[] values; // Parameter list
 
     static {
         desc.add(JMeterUtils.getResString("csvread_file_file_name")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("column_number")); //$NON-NLS-1$
     }
 
     public CSVRead() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String myValue = ""; //$NON-NLS-1$
 
         String fileName = ((org.apache.jmeter.engine.util.CompoundVariable) values[0]).execute();
         String columnOrNext = ((org.apache.jmeter.engine.util.CompoundVariable) values[1]).execute();
 
-        log.debug("execute (" + fileName + " , " + columnOrNext + ")   ");
+        if (log.isDebugEnabled()) {
+            log.debug("execute (" + fileName + " , " + columnOrNext + ")   ");
+        }
 
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
 
-        log.debug("execute value: " + myValue);
+        if (log.isDebugEnabled()) {
+            log.debug("execute value: " + myValue);
+        }
 
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
index a61adc8ec..813e04783 100644
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Function to generate chars from a list of decimal or hex values
  * @since 2.3.3
  */
 public class CharFunction extends AbstractFunction {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__char"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("char_value")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public CharFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         StringBuilder sb = new StringBuilder(values.length);
         for (int i=0; i < values.length; i++){
             String numberString = ((CompoundVariable) values[i]).execute().trim();
             try {
                 long value=Long.decode(numberString).longValue();
                 char ch = (char) value;
                 sb.append(ch);
             } catch (NumberFormatException e){
                 log.warn("Could not parse "+numberString+" : "+e);
             }
         }
         return sb.toString();
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/EscapeHtml.java b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
index 9b6c3466c..694c6d5de 100644
--- a/src/functions/org/apache/jmeter/functions/EscapeHtml.java
+++ b/src/functions/org/apache/jmeter/functions/EscapeHtml.java
@@ -1,93 +1,93 @@
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
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * <p>Function which escapes the characters in a <code>String</code> using HTML entities.</p>
  *
  * <p>
  * For example:
  * </p> 
  * <p><code>"bread" & "butter"</code></p>
  * becomes:
  * <p>
  * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
  * </p>
  *
  * <p>Supports all known HTML 4.0 entities.
  * Note that the commonly used apostrophe escape character (&amp;apos;)
  * is not a legal entity and so is not supported). </p>
  * 
  * @see StringEscapeUtils#escapeHtml4(String) (Commons Lang)
  * @since 2.3.3
  */
 public class EscapeHtml extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__escapeHtml"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("escape_html_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public EscapeHtml() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String rawString = ((CompoundVariable) values[0]).execute();
         return StringEscapeUtils.escapeHtml4(rawString);
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
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
index 73074c5f7..c6793b54f 100644
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 /**
  * Escape ORO meta characters
  * @since 2.9
  */
 public class EscapeOroRegexpChars extends AbstractFunction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
     public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/EvalFunction.java b/src/functions/org/apache/jmeter/functions/EvalFunction.java
index 451dc6140..5f88250b5 100644
--- a/src/functions/org/apache/jmeter/functions/EvalFunction.java
+++ b/src/functions/org/apache/jmeter/functions/EvalFunction.java
@@ -1,87 +1,87 @@
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
 
 // @see PackageTest for unit tests
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to evaluate a string which may contain variable or function references.
  *
  * Parameter: string to be evaluated
  *
  * Returns: the evaluated value
  * @since 2.3.1
  */
 public class EvalFunction extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__eval"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
     private static final int MAX_PARAMETER_COUNT = 1;
 
     static {
         desc.add(JMeterUtils.getResString("eval_name_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public EvalFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String parameter = ((CompoundVariable) values[0]).execute();
         CompoundVariable cv = new CompoundVariable(parameter);
         return cv.execute();
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/EvalVarFunction.java b/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
index fd3e55680..dfff314b2 100644
--- a/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
+++ b/src/functions/org/apache/jmeter/functions/EvalVarFunction.java
@@ -1,98 +1,98 @@
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
 
 // @see PackageTest for unit tests
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Function to evaluate a string which may contain variable or function references.
  *
  * Parameter: string to be evaluated
  *
  * Returns: the evaluated value
  * @since 2.3.1
  */
 public class EvalVarFunction extends AbstractFunction {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/FileToString.java b/src/functions/org/apache/jmeter/functions/FileToString.java
index 8fc5043a5..26493808c 100644
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.log.Logger;
 
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
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/IntSum.java b/src/functions/org/apache/jmeter/functions/IntSum.java
index 373f41778..7cc644784 100644
--- a/src/functions/org/apache/jmeter/functions/IntSum.java
+++ b/src/functions/org/apache/jmeter/functions/IntSum.java
@@ -1,106 +1,106 @@
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
 
 /**
  * Provides an intSum function that adds two or more integer values.
  *
  * @see LongSum
  * @since 1.8.1
  */
 public class IntSum extends AbstractFunction {
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__intSum"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("intsum_param_1")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("intsum_param_2")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     /**
      * No-arg constructor.
      */
     public IntSum() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         JMeterVariables vars = getVariables();
 
         int sum = 0;
         String varName = ((CompoundVariable) values[values.length - 1]).execute().trim(); // trim() see bug 55871
 
         for (int i = 0; i < values.length - 1; i++) {
             sum += Integer.parseInt(((CompoundVariable) values[i]).execute());
         }
 
         try {
             // Has chances to be a var
             sum += Integer.parseInt(varName);
             varName = null; // there is no variable name
         } catch(NumberFormatException ignored) {
             // varName keeps its value and sum has not taken 
             // into account non numeric or overflowing number
         }
 
         String totalString = Integer.toString(sum);
         if (vars != null && varName != null){// vars will be null on TestPlan
             vars.put(varName.trim(), totalString);
         }
 
         return totalString;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkMinParameterCount(parameters, 2);
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
diff --git a/src/functions/org/apache/jmeter/functions/IterationCounter.java b/src/functions/org/apache/jmeter/functions/IterationCounter.java
index 7fb4c6a21..b22f56a8d 100644
--- a/src/functions/org/apache/jmeter/functions/IterationCounter.java
+++ b/src/functions/org/apache/jmeter/functions/IterationCounter.java
@@ -1,121 +1,122 @@
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
 
 /**
  * Counter that can be referenced anywhere in the Thread Group. It can be configured per User (Thread Local)
  * or globally.
  * @since 1.X
  */
 public class IterationCounter extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__counter"; //$NON-NLS-1$
 
     private ThreadLocal<Integer> perThreadInt;
 
     private Object[] variables;
 
     private int globalCounter;//MAXINT = 2,147,483,647
 
     private void init(){
        synchronized(this){
            globalCounter=0;
        }
        perThreadInt = new ThreadLocal<Integer>(){
             @Override
             protected Integer initialValue() {
                 return Integer.valueOf(0);
             }
         };
     }
 
     static {
         desc.add(JMeterUtils.getResString("iteration_counter_arg_1")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     public IterationCounter() {
         init();
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
-        globalCounter++;
-
         JMeterVariables vars = getVariables();
 
         boolean perThread = Boolean.parseBoolean(((CompoundVariable) variables[0]).execute());
 
         String varName = ""; //$NON-NLS-1$
         if (variables.length >=2) {// Ensure variable has been provided
             varName = ((CompoundVariable) variables[1]).execute().trim();
         }
 
         String counterString = ""; //$NON-NLS-1$
 
         if (perThread) {
             int threadCounter;
             threadCounter = perThreadInt.get().intValue() + 1;
             perThreadInt.set(Integer.valueOf(threadCounter));
             counterString = String.valueOf(threadCounter);
         } else {
-            counterString = String.valueOf(globalCounter);
+            synchronized (this) {
+                globalCounter++;
+                counterString = String.valueOf(globalCounter);
+            }
         }
 
         // vars will be null on Test Plan
         if (vars != null && varName.length() > 0) {
             vars.put(varName, counterString);
         }
         return counterString;
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1, 2);
         variables = parameters.toArray();
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
diff --git a/src/functions/org/apache/jmeter/functions/JavaScript.java b/src/functions/org/apache/jmeter/functions/JavaScript.java
index 9ef3d193b..a6563456c 100644
--- a/src/functions/org/apache/jmeter/functions/JavaScript.java
+++ b/src/functions/org/apache/jmeter/functions/JavaScript.java
@@ -1,125 +1,125 @@
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
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.RhinoException;
 import org.mozilla.javascript.Scriptable;
 
 /**
  * javaScript function implementation that executes a piece of JavaScript (not Java!) code and returns its value
  * @since 1.9
  */
 public class JavaScript extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__javaScript"; //$NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     static {
         desc.add(JMeterUtils.getResString("javascript_expression"));//$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public JavaScript() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         String script = ((CompoundVariable) values[0]).execute();
         // Allow variable to be omitted
         String varName = values.length < 2 ? null : ((CompoundVariable) values[1]).execute().trim();
         String resultStr = "";
 
         Context cx = Context.enter();
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
index 6b4aad015..dcbfd6c59 100644
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A function which understands Commons JEXL2
  * @since 2.6
  */
 // For unit tests, see TestJexlFunction
 public class Jexl2Function extends AbstractFunction implements ThreadListener {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String KEY = "__jexl2"; //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final ThreadLocal<JexlEngine> threadLocalJexl = new ThreadLocal<JexlEngine>();
 
     static
     {
         desc.add(JMeterUtils.getResString("jexl_expression")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters)
+    public void setParameters(Collection<CompoundVariable> parameters)
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
diff --git a/src/functions/org/apache/jmeter/functions/JexlFunction.java b/src/functions/org/apache/jmeter/functions/JexlFunction.java
index 2227c4f14..f403a9f20 100644
--- a/src/functions/org/apache/jmeter/functions/JexlFunction.java
+++ b/src/functions/org/apache/jmeter/functions/JexlFunction.java
@@ -1,133 +1,133 @@
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
 import java.util.Map;
 
 import org.apache.commons.jexl.JexlContext;
 import org.apache.commons.jexl.JexlHelper;
 import org.apache.commons.jexl.Script;
 import org.apache.commons.jexl.ScriptFactory;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A function which understands Commons JEXL
  * @since 2.2
  */
 // For unit tests, see TestJexlFunction
 public class JexlFunction extends AbstractFunction {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String KEY = "__jexl"; //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<String>();
 
     static
     {
         desc.add(JMeterUtils.getResString("jexl_expression")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt"));// $NON-NLS1$
     }
 
     private Object[] values;
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
             Script script = ScriptFactory.createScript(exp);
             JexlContext jc = JexlHelper.createContext();
             @SuppressWarnings("unchecked")
             final Map<String, Object> jexlVars = jc.getVars();
             jexlVars.put("log", log); //$NON-NLS-1$
             jexlVars.put("ctx", jmctx); //$NON-NLS-1$
             jexlVars.put("vars", vars); //$NON-NLS-1$
             jexlVars.put("props", JMeterUtils.getJMeterProperties()); //$NON-NLS-1$
             // Previously mis-spelt as theadName
             jexlVars.put("threadName", Thread.currentThread().getName()); //$NON-NLS-1$
             jexlVars.put("sampler", currentSampler); //$NON-NLS-1$ (may be null)
             jexlVars.put("sampleResult", previousResult); //$NON-NLS-1$ (may be null)
             jexlVars.put("OUT", System.out);//$NON-NLS-1$
 
             // Now evaluate the script, getting the result
             Object o = script.execute(jc);
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters)
+    public void setParameters(Collection<CompoundVariable> parameters)
             throws InvalidVariableException
     {
         checkParameterCount(parameters, 1, 2);
         values = parameters.toArray();
     }
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/LogFunction.java b/src/functions/org/apache/jmeter/functions/LogFunction.java
index 570e5a087..066af6cea 100644
--- a/src/functions/org/apache/jmeter/functions/LogFunction.java
+++ b/src/functions/org/apache/jmeter/functions/LogFunction.java
@@ -1,182 +1,183 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.log.Priority;
 
 /**
  * <p>
  * Function to log a message.
  * </p>
  *
  * <p>
  * Parameters:
  * <ul>
  * <li>string value</li>
  * <li>log level (optional; defaults to INFO; or DEBUG if unrecognised; or can use OUT or ERR)</li>
  * <li>throwable message (optional)</li>
  * <li>comment (optional)</li>
  * </ul>
  * </p>
  * Returns: - the input string
  * @since 2.2
  */
 public class LogFunction extends AbstractFunction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__log"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
 
     private static final int MAX_PARAMETER_COUNT = 4;
     static {
         desc.add(JMeterUtils.getResString("log_function_string_ret"));    //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("log_function_level"));     //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("log_function_throwable")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("log_function_comment"));   //$NON-NLS-1$
     }
 
     private static final String DEFAULT_PRIORITY = "INFO"; //$NON-NLS-1$
 
     private static final String DEFAULT_SEPARATOR = " : "; //$NON-NLS-1$
 
     private Object[] values;
 
     public LogFunction() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
+        // The method is synchronized to avoid interference of messages from multiple threads
         String stringToLog = ((CompoundVariable) values[0]).execute();
 
         String priorityString;
         if (values.length > 1) { // We have a default
             priorityString = ((CompoundVariable) values[1]).execute();
             if (priorityString.length() == 0) {
                 priorityString = DEFAULT_PRIORITY;
             }
         } else {
             priorityString = DEFAULT_PRIORITY;
         }
 
         Throwable t = null;
         if (values.length > 2) { // Throwable wanted
             String value = ((CompoundVariable) values[2]).execute();
             if (value.length() > 0) {
                 t = new Throwable(value);
             }
         }
 
         String comment = "";
         if (values.length > 3) { // Comment wanted
             comment = ((CompoundVariable) values[3]).execute();
         }
 
         logDetails(log, stringToLog, priorityString, t, comment);
 
         return stringToLog;
 
     }
 
     // Common output function
     private static void printDetails(java.io.PrintStream ps, String s, Throwable t, String c) {
         String tn = Thread.currentThread().getName();
 
         StringBuilder sb = new StringBuilder(80);
         sb.append("Log: ");
         sb.append(tn);
         if (c.length()>0){
             sb.append(" ");
             sb.append(c);
         } else {
             sb.append(DEFAULT_SEPARATOR);
         }
         sb.append(s);
         if (t != null) {
             sb.append(" ");
             ps.print(sb.toString());
             t.printStackTrace(ps);
         } else {
             ps.println(sb.toString());
         }
     }
 
     // Routine to perform the output (also used by __logn() function)
     static void logDetails(Logger l, String s, String prio, Throwable t, String c) {
         if (prio.equalsIgnoreCase("OUT")) //$NON-NLS-1
         {
             printDetails(System.out, s, t, c);
         } else if (prio.equalsIgnoreCase("ERR")) //$NON-NLS-1
         {
             printDetails(System.err, s, t, c);
         } else {
             // N.B. if the string is not recognised, DEBUG is assumed
             Priority p = Priority.getPriorityForName(prio);
             if (log.isPriorityEnabled(p)) {// Thread method is potentially expensive
                 String tn = Thread.currentThread().getName();
                 StringBuilder sb = new StringBuilder(40);
                 sb.append(tn);
                 if (c.length()>0){
                     sb.append(" ");
                     sb.append(c);
                 } else {
                     sb.append(DEFAULT_SEPARATOR);
                 }
                 sb.append(s);
                 log.log(p, sb.toString(), t);
             }
         }
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/LogFunction2.java b/src/functions/org/apache/jmeter/functions/LogFunction2.java
index f8eddde51..534571d5b 100644
--- a/src/functions/org/apache/jmeter/functions/LogFunction2.java
+++ b/src/functions/org/apache/jmeter/functions/LogFunction2.java
@@ -1,118 +1,118 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * <p>
  * Function to log a message.
  * </p>
  *
  * <p>
  * Parameters:
  * <ul>
  * <li>string value</li>
  * <li>log level (optional; defaults to INFO; or DEBUG if unrecognised; or can use OUT or ERR)</li>
  * <li>throwable message (optional)</li>
  * </ul>
  * </p>
  * Returns: - Empty String (so can be used where return value would be a nuisance)
  * @since 2.2
  */
 public class LogFunction2 extends AbstractFunction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__logn"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
 
     private static final int MAX_PARAMETER_COUNT = 3;
     static {
         desc.add(JMeterUtils.getResString("log_function_string"));    //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("log_function_level"));     //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("log_function_throwable")); //$NON-NLS-1$
     }
 
     private static final String DEFAULT_PRIORITY = "INFO"; //$NON-NLS-1$
 
     private Object[] values;
 
     public LogFunction2() {
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String stringToLog = ((CompoundVariable) values[0]).execute();
 
         String priorityString;
         if (values.length > 1) { // We have a default
             priorityString = ((CompoundVariable) values[1]).execute();
             if (priorityString.length() == 0) {
                 priorityString = DEFAULT_PRIORITY;
             }
         } else {
             priorityString = DEFAULT_PRIORITY;
         }
 
         Throwable t = null;
         if (values.length > 2) { // Throwable wanted
             t = new Throwable(((CompoundVariable) values[2]).execute());
         }
 
         LogFunction.logDetails(log, stringToLog, priorityString, t, "");
 
         return "";
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/LongSum.java b/src/functions/org/apache/jmeter/functions/LongSum.java
index 978fd1f17..f68cdc4ad 100644
--- a/src/functions/org/apache/jmeter/functions/LongSum.java
+++ b/src/functions/org/apache/jmeter/functions/LongSum.java
@@ -1,106 +1,106 @@
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
 
 /**
  * Provides a longSum function that adds two or more long values.
  * @see IntSum
  * @since 2.3.2
  */
 public class LongSum extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__longSum"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("longsum_param_1")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("longsum_param_2")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     /**
      * No-arg constructor.
      */
     public LongSum() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         JMeterVariables vars = getVariables();
 
         long sum = 0;
         String varName = ((CompoundVariable) values[values.length - 1]).execute().trim();
 
         for (int i = 0; i < values.length - 1; i++) {
             sum += Long.parseLong(((CompoundVariable) values[i]).execute());
         }
 
         try {
             // Has chances to be a var
             sum += Long.parseLong(varName);
             varName = null; // there is no variable name
         } catch(NumberFormatException ignored) {
             // varName keeps its value and sum has not taken 
             // into account non numeric or overflowing number
         }
 
         String totalString = Long.toString(sum);
         if (vars != null && varName != null && varName.length() > 0){// vars will be null on TestPlan
             vars.put(varName, totalString);
         }
 
         return totalString;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkMinParameterCount(parameters, 2);
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
diff --git a/src/functions/org/apache/jmeter/functions/Property.java b/src/functions/org/apache/jmeter/functions/Property.java
index fda85a4a3..b99359340 100644
--- a/src/functions/org/apache/jmeter/functions/Property.java
+++ b/src/functions/org/apache/jmeter/functions/Property.java
@@ -1,108 +1,108 @@
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
 
 /**
  * Function to get a JMeter property, and optionally store it
  *
  * Parameters:
  *  - property name
  *  - variable name (optional)
  *  - default value (optional)
  *
  * Returns:
  * - the property value, but if not found:
  * - the default value, but if not defined:
  * - the property name itself
  * @since 2.0
  */
 public class Property extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__property"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
     private static final int MAX_PARAMETER_COUNT = 3;
 
     static {
         desc.add(JMeterUtils.getResString("property_name_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("property_default_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public Property() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String propertyName = ((CompoundVariable) values[0]).execute();
         String propertyDefault = propertyName;
         if (values.length > 2) { // We have a 3rd parameter
             propertyDefault = ((CompoundVariable) values[2]).execute();
         }
         String propertyValue = JMeterUtils.getPropDefault(propertyName, propertyDefault);
         if (values.length > 1) {
             String variableName = ((CompoundVariable) values[1]).execute();
             if (variableName.length() > 0) {// Allow for empty name
                 final JMeterVariables variables = getVariables();
                 if (variables != null) {
                     variables.put(variableName, propertyValue);
                 }
             }
         }
         return propertyValue;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/Property2.java b/src/functions/org/apache/jmeter/functions/Property2.java
index 408b5ca42..eab945b40 100644
--- a/src/functions/org/apache/jmeter/functions/Property2.java
+++ b/src/functions/org/apache/jmeter/functions/Property2.java
@@ -1,106 +1,106 @@
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
 
 /**
  * Function to get a JMeter property, or a default. Does not offer the option to
  * store the value, as it is just as easy to refetch it. This is a
  * specialisation of the __property() function to make it simpler to use for
  * ThreadGroup GUI etc. The name is also shorter.
  *
  * Parameters: - property name - default value (optional; defaults to "1")
  *
  * Usage:
  *
  * Define the property in jmeter.properties, or on the command-line: java ...
  * -Jpropname=value
  *
  * Retrieve the value in the appropriate GUI by using the string:
  * ${__P(propname)} $(__P(propname,default)}
  *
  * Returns: - the property value, but if not found - the default value, but if
  * not present - "1" (suitable for use in ThreadGroup GUI)
  * @since 2.0
  */
 public class Property2 extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__P"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
 
     private static final int MAX_PARAMETER_COUNT = 2;
     static {
         desc.add(JMeterUtils.getResString("property_name_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("property_default_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public Property2() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String propertyName = ((CompoundVariable) values[0]).execute();
 
         String propertyDefault = "1"; //$NON-NLS-1$
         if (values.length > 1) { // We have a default
             propertyDefault = ((CompoundVariable) values[1]).execute();
         }
 
         String propertyValue = JMeterUtils.getPropDefault(propertyName, propertyDefault);
 
         return propertyValue;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/Random.java b/src/functions/org/apache/jmeter/functions/Random.java
index 3e09f8b79..67b1cc1f3 100644
--- a/src/functions/org/apache/jmeter/functions/Random.java
+++ b/src/functions/org/apache/jmeter/functions/Random.java
@@ -1,109 +1,109 @@
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
 
 /**
  * Provides a Random function which returns a random long integer between a min
  * (first argument) and a max (second argument).
  * @since 1.9
  */
 public class Random extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__Random"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("minimum_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("maximum_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private CompoundVariable varName, minimum, maximum;
 
     /**
      * No-arg constructor.
      */
     public Random() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
 
         long min = Long.parseLong(minimum.execute().trim());
         long max = Long.parseLong(maximum.execute().trim());
 
         long rand = min + (long) (Math.random() * (max - min + 1));
 
         String randString = Long.toString(rand);
 
         if (varName != null) {
             JMeterVariables vars = getVariables();
             final String varTrim = varName.execute().trim();
             if (vars != null && varTrim.length() > 0){// vars will be null on TestPlan
                 vars.put(varTrim, randString);
             }
         }
 
         return randString;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 2, 3);
         Object[] values = parameters.toArray();
 
         minimum = (CompoundVariable) values[0];
         maximum = (CompoundVariable) values[1];
         if (values.length>2){
             varName = (CompoundVariable) values[2];
         } else {
             varName = null;
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
index 37da53557..1c8bdc6d6 100644
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Provides a RandomString function which returns a random String of length (first argument) 
  * using characters (second argument)
  * @since 2.6
  */
 public class RandomString extends AbstractFunction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
index f077ca686..79ff6fe98 100644
--- a/src/functions/org/apache/jmeter/functions/RegexFunction.java
+++ b/src/functions/org/apache/jmeter/functions/RegexFunction.java
@@ -1,288 +1,290 @@
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
 import java.util.Random;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
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
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String ALL = "ALL"; //$NON-NLS-1$
 
     public static final String RAND = "RAND"; //$NON-NLS-1$
 
     public static final String KEY = "__regexFunction"; //$NON-NLS-1$
 
     private Object[] values;// Parameters are stored here
 
+    // Using the same Random across threads might result in pool performance
+    // It might make sense to use ThreadLocalRandom or ThreadLocal<Random>
     private static final Random rand = new Random();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
 
         List<MatchResult> collectAllMatches = new ArrayList<MatchResult>();
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
             MatchResult result = collectAllMatches.get(rand.nextInt(collectAllMatches.size()));
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
         for (int a = 0; a < template.length; a++) {
             if (template[a] instanceof String) {
                 result.append(template[a]);
             } else {
                 result.append(match.group(((Integer) template[a]).intValue()));
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
         values = parameters.toArray();
     }
 
     private Object[] generateTemplate(String rawTemplate) {
         List<String> pieces = new ArrayList<String>();
         // String or Integer
         List<Object> combined = new LinkedList<Object>();
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
diff --git a/src/functions/org/apache/jmeter/functions/SamplerName.java b/src/functions/org/apache/jmeter/functions/SamplerName.java
index 34663bc6d..76cb32690 100644
--- a/src/functions/org/apache/jmeter/functions/SamplerName.java
+++ b/src/functions/org/apache/jmeter/functions/SamplerName.java
@@ -1,89 +1,89 @@
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
 
 /**
  * Function to return the name of the current sampler.
  * @since 2.5
  */
 public class SamplerName extends AbstractFunction {
 
     private static final String KEY = "__samplerName"; //$NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<String>();
 
     static {
         // desc.add("Use fully qualified host name: TRUE/FALSE (Default FALSE)");
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     // TODO Should this method be synchronized ? all other function execute are
     /** {@inheritDoc} */
     @Override
     public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         // return JMeterContextService.getContext().getCurrentSampler().getName();
         String name = "";
         if (currentSampler != null) { // will be null if function is used on TestPlan
             name = currentSampler.getName();
         }
         if (values.length > 0){
             JMeterVariables vars = getVariables();
             if (vars != null) {// May be null if function is used on TestPlan
                 String varName = ((CompoundVariable) values[0]).execute().trim();
                 if (varName.length() > 0) {
                     vars.put(varName, name);
                 }
             }
         }
         return name;
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters)
+    public void setParameters(Collection<CompoundVariable> parameters)
             throws InvalidVariableException {
         checkParameterCount(parameters, 0, 1);
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
diff --git a/src/functions/org/apache/jmeter/functions/SetProperty.java b/src/functions/org/apache/jmeter/functions/SetProperty.java
index 27c59361d..a31fb3d1a 100644
--- a/src/functions/org/apache/jmeter/functions/SetProperty.java
+++ b/src/functions/org/apache/jmeter/functions/SetProperty.java
@@ -1,105 +1,105 @@
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
 
 /**
  * Function to set a JMeter property
  *
  * Parameters: - property name - value
  *
  * Usage:
  *
  * Set the property value in the appropriate GUI by using the string:
  * ${__setProperty(propname,propvalue[,returnvalue?])}
  *
  * Returns: nothing or original value if the 3rd parameter is true
  * @since 2.1
  */
 public class SetProperty extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__setProperty"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 2;
 
     private static final int MAX_PARAMETER_COUNT = 3;
     static {
         desc.add(JMeterUtils.getResString("property_name_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("property_value_param")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("property_returnvalue_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public SetProperty() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String propertyName = ((CompoundVariable) values[0]).execute();
 
         String propertyValue = ((CompoundVariable) values[1]).execute();
 
         boolean returnValue = false;// should we return original value?
         if (values.length > 2) {
             returnValue = ((CompoundVariable) values[2]).execute().equalsIgnoreCase("true"); //$NON-NLS-1$
         }
 
         if (returnValue) { // Only obtain and cast the return if needed
             return (String) JMeterUtils.setProperty(propertyName, propertyValue);
         } else {
             JMeterUtils.setProperty(propertyName, propertyValue);
             return "";
         }
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/SplitFunction.java b/src/functions/org/apache/jmeter/functions/SplitFunction.java
index f79ecfa17..86dd17935 100644
--- a/src/functions/org/apache/jmeter/functions/SplitFunction.java
+++ b/src/functions/org/apache/jmeter/functions/SplitFunction.java
@@ -1,130 +1,130 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
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
  * </p>
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
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final List<String> desc = new LinkedList<String>();
 
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
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
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
         String parts[] = JOrphanUtils.split(stringToSplit, splitString, "?");// $NON-NLS-1$
 
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
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/src/functions/org/apache/jmeter/functions/TestPlanName.java b/src/functions/org/apache/jmeter/functions/TestPlanName.java
index 208437f2a..4958c6c07 100644
--- a/src/functions/org/apache/jmeter/functions/TestPlanName.java
+++ b/src/functions/org/apache/jmeter/functions/TestPlanName.java
@@ -1,71 +1,71 @@
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
 import org.apache.jmeter.services.FileServer;
 
 /**
  * Returns Test Plan name
  * @since 2.6
  */
 public class TestPlanName extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__TestPlanName"; //$NON-NLS-1$
 
     /**
      * No-arg constructor.
      */
     public TestPlanName() {
         super();
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         return FileServer.getFileServer().getScriptName();
     }
 
     /** {@inheritDoc} */
     @Override
     public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 0);
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
diff --git a/src/functions/org/apache/jmeter/functions/TimeFunction.java b/src/functions/org/apache/jmeter/functions/TimeFunction.java
index 8e551136c..8179f057e 100644
--- a/src/functions/org/apache/jmeter/functions/TimeFunction.java
+++ b/src/functions/org/apache/jmeter/functions/TimeFunction.java
@@ -1,136 +1,137 @@
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
 
 import java.text.SimpleDateFormat;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 
 // See org.apache.jmeter.functions.TestTimeFunction for unit tests
 
 /**
  * __time() function - returns the current time in milliseconds
  * @since 2.2
  */
 public class TimeFunction extends AbstractFunction {
 
     private static final String KEY = "__time"; // $NON-NLS-1$
 
     private static final List<String> desc = new LinkedList<String>();
 
     // Only modified in class init
     private static final Map<String, String> aliases = new HashMap<String, String>();
 
     static {
         desc.add(JMeterUtils.getResString("time_format")); //$NON-NLS-1$
         desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
         aliases.put("YMD", //$NON-NLS-1$
                 JMeterUtils.getPropDefault("time.YMD", //$NON-NLS-1$
                         "yyyyMMdd")); //$NON-NLS-1$
         aliases.put("HMS", //$NON-NLS-1$
                 JMeterUtils.getPropDefault("time.HMS", //$NON-NLS-1$
                         "HHmmss")); //$NON-NLS-1$
         aliases.put("YMDHMS", //$NON-NLS-1$
                 JMeterUtils.getPropDefault("time.YMDHMS", //$NON-NLS-1$
                         "yyyyMMdd-HHmmss")); //$NON-NLS-1$
         aliases.put("USER1", //$NON-NLS-1$
                 JMeterUtils.getPropDefault("time.USER1","")); //$NON-NLS-1$
         aliases.put("USER2", //$NON-NLS-1$
                 JMeterUtils.getPropDefault("time.USER2","")); //$NON-NLS-1$
     }
 
     // Ensure that these are set, even if no paramters are provided
     private String format   = ""; //$NON-NLS-1$
     private String variable = ""; //$NON-NLS-1$
 
     public TimeFunction(){
         super();
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
+    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
         String datetime;
         if (format.length() == 0){// Default to milliseconds
             datetime = Long.toString(System.currentTimeMillis());
         } else {
             // Resolve any aliases
             String fmt = aliases.get(format);
             if (fmt == null) {
                 fmt = format;// Not found
             }
+            // TODO: avoid regexp parsing in loop
             if (fmt.matches("/\\d+")) { // divisor is a positive number
                 long div = Long.parseLong(fmt.substring(1)); // should never case NFE
                 datetime = Long.toString((System.currentTimeMillis() / div));
             } else {
                 SimpleDateFormat df = new SimpleDateFormat(fmt);// Not synchronised, so can't be shared
                 datetime = df.format(new Date());
             }
         }
 
         if (variable.length() > 0) {
             JMeterVariables vars = getVariables();
             if (vars != null){// vars will be null on TestPlan
                 vars.put(variable, datetime);
             }
         }
         return datetime;
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
 
         checkParameterCount(parameters, 0, 2);
 
         Object []values = parameters.toArray();
         int count = values.length;
 
         if (count > 0) {
             format = ((CompoundVariable) values[0]).execute();
         }
 
         if (count > 1) {
             variable = ((CompoundVariable)values[1]).execute().trim();
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
diff --git a/src/functions/org/apache/jmeter/functions/UnEscape.java b/src/functions/org/apache/jmeter/functions/UnEscape.java
index d8fcc6de0..ac148a9d4 100644
--- a/src/functions/org/apache/jmeter/functions/UnEscape.java
+++ b/src/functions/org/apache/jmeter/functions/UnEscape.java
@@ -1,83 +1,83 @@
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
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to unescape any Java literals found in the String.
  * For example, it will turn a sequence of '\' and 'n' into a newline character,
  * unless the '\' is preceded by another '\'.
  * 
  * @see StringEscapeUtils#unescapeJava(String)
  * @since 2.3.3
  */
 public class UnEscape extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__unescape"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("unescape_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UnEscape() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String rawString = ((CompoundVariable) values[0]).execute();
         return StringEscapeUtils.unescapeJava(rawString);
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
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
diff --git a/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java b/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
index 0a33dc4ea..3449a2069 100644
--- a/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
+++ b/src/functions/org/apache/jmeter/functions/UnEscapeHtml.java
@@ -1,89 +1,89 @@
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
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to unescape a string containing entity escapes
  * to a string containing the actual Unicode characters corresponding to the escapes. 
  * Supports HTML 4.0 entities.
  * <p>
  * For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;" will become "&lt;Fran&ccedil;ais&gt;"
  * </p>
  * <p>
  * If an entity is unrecognized, it is left alone, and inserted verbatim into the result string.
  * e.g. "&amp;gt;&amp;zzzz;x" will become "&gt;&amp;zzzz;x".
  * </p>
  * @see org.apache.commons.lang3.StringEscapeUtils#unescapeHtml4(String)
  * @since 2.3.3
  */
 public class UnEscapeHtml extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__unescapeHtml"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("unescape_html_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UnEscapeHtml() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
 
         String escapedString = ((CompoundVariable) values[0]).execute();
         return StringEscapeUtils.unescapeHtml4(escapedString);
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
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
diff --git a/src/functions/org/apache/jmeter/functions/UrlDecode.java b/src/functions/org/apache/jmeter/functions/UrlDecode.java
index 513595500..9478a4105 100644
--- a/src/functions/org/apache/jmeter/functions/UrlDecode.java
+++ b/src/functions/org/apache/jmeter/functions/UrlDecode.java
@@ -1,87 +1,87 @@
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
 
 
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to decode a application/x-www-form-urlencoded string.
  * 
  * @since 2.10
  */
 public class UrlDecode extends AbstractFunction {
 
     private static final String CHARSET_ENCODING = "UTF-8"; //$NON-NLS-1$
     
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__urldecode"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("urldecode_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UrlDecode() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String decodeString = ""; //$NON-NLS-1$
         try {
             String rawString = ((CompoundVariable) values[0]).execute();
             decodeString = URLDecoder.decode(rawString, CHARSET_ENCODING);
         } catch (UnsupportedEncodingException uee) {
             return null;
         }
         return decodeString;
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
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
diff --git a/src/functions/org/apache/jmeter/functions/UrlEncode.java b/src/functions/org/apache/jmeter/functions/UrlEncode.java
index 07d2a024b..0e3e2da71 100644
--- a/src/functions/org/apache/jmeter/functions/UrlEncode.java
+++ b/src/functions/org/apache/jmeter/functions/UrlEncode.java
@@ -1,87 +1,87 @@
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
 
 
 import java.io.UnsupportedEncodingException;
 import java.net.URLEncoder;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.engine.util.CompoundVariable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Function to encode a string to a application/x-www-form-urlencoded string.
  * 
  * @since 2.10
  */
 public class UrlEncode extends AbstractFunction {
 
     private static final String CHARSET_ENCODING = "UTF-8"; //$NON-NLS-1$
     
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__urlencode"; //$NON-NLS-1$
 
     static {
         desc.add(JMeterUtils.getResString("urlencode_string")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public UrlEncode() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String decodeString = ""; //$NON-NLS-1$
         try {
             String encodedString = ((CompoundVariable) values[0]).execute();
             decodeString = URLEncoder.encode(encodedString, CHARSET_ENCODING);
         } catch (UnsupportedEncodingException uee) {
             return null;
         }
         return decodeString;
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
         checkParameterCount(parameters, 1);
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
diff --git a/src/functions/org/apache/jmeter/functions/Variable.java b/src/functions/org/apache/jmeter/functions/Variable.java
index 8fa4d7f87..8d27d48a8 100644
--- a/src/functions/org/apache/jmeter/functions/Variable.java
+++ b/src/functions/org/apache/jmeter/functions/Variable.java
@@ -1,90 +1,90 @@
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
 
 /**
  * Function to get a JMeter Variable
  *
  * Parameters:
  * - variable name
  *
  * Returns:
  * - the variable value, but if not found
  * - the variable name itself
  * @since 2.3RC3
  */
 public class Variable extends AbstractFunction {
 
     private static final List<String> desc = new LinkedList<String>();
 
     private static final String KEY = "__V"; //$NON-NLS-1$
 
     // Number of parameters expected - used to reject invalid calls
     private static final int MIN_PARAMETER_COUNT = 1;
     private static final int MAX_PARAMETER_COUNT = 1;
 
     static {
         desc.add(JMeterUtils.getResString("variable_name_param")); //$NON-NLS-1$
     }
 
     private Object[] values;
 
     public Variable() {
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
+    public String execute(SampleResult previousResult, Sampler currentSampler)
             throws InvalidVariableException {
         String variableName = ((CompoundVariable) values[0]).execute();
         String variableValue = getVariables().get(variableName);
         return variableValue == null? variableName : variableValue;
 
     }
 
     /** {@inheritDoc} */
     @Override
-    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
+    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index c5d459c73..07bcb07ba 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,402 +1,404 @@
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
 <li><bugzilla>57078</bugzilla> - LagartoBasedHTMLParser fails to parse page that contains input with no type</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56243</bugzilla> - Foreach works incorrectly with indexes on subsequent iterations </li>
 <li><bugzilla>56276</bugzilla> - Loop controller becomes broken once loop count evaluates to zero </li>
 <li><bugzilla>56160</bugzilla> - StackOverflowError when using WhileController within IfController</li>
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
 <li><bugzilla>57088</bugzilla> - NPE in ResultCollector.testEnded</li>
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
 <li><bugzilla>57107</bugzilla> - Patch proposal: Add DAV verbs to HTTP Sampler. Contributed by Philippe Jung (apache at famille-jung.fr)</li>
 <li><bugzilla>56357</bugzilla> - Certificates does not conform to algorithm constraints: Adding a note to indicate how to remove of the Java installation these new security constraints</li>
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
+<li><bugzilla>57114</bugzilla> - Performance : Functions that only have values as instance variable should not synchronize execute. Based on analysis by Ubik Load Pack support and Vladimir Sitnikov, patch contributed by Vladimir Sitnikov (sitnikov.vladimir at gmail.com)</li>
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
 <li><bugzilla>57083</bugzilla> - simplified the CachedResourceMode enum. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57082</bugzilla> - ComboStringEditor : Added hashCode to an inner class which overwrote equals. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57081</bugzilla> - Updating checkstyle to only check for tabs in java, xml, xsd, dtd, htm, html and txt files (not images!). Contributed by Graham Russell (graham at ham1.co.uk)</li>
 <li><bugzilla>56178</bugzilla> - Really replace backslashes in user name before generating proxy certificate. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57084</bugzilla> - Close socket after usage in BeahShellClient. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li><bugzilla>57117</bugzilla> - Increase the default cipher for HTTPS Test Script Recorder from SSLv3 to TLS</li>
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
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Philippe Jung (apache at famille-jung.fr)</li>
+<li>Vladimir Sitnikov (sitnikov.vladimir at gmail.com)</li>
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
