diff --git a/src/components/org/apache/jmeter/assertions/BSFAssertion.java b/src/components/org/apache/jmeter/assertions/BSFAssertion.java
index 9831393ee..9067cfa22 100644
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BSFAssertion extends BSFTestElement implements Cloneable, Assertion, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFAssertion.class);
 
-    private static final long serialVersionUID = 234L;
+    private static final long serialVersionUID = 235L;
 
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
             log.warn("Problem in BSF script "+e);
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
diff --git a/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java b/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
index 3d8bef1a2..8a60fa3a9 100644
--- a/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
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
 
 package org.apache.jmeter.assertions;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * An Assertion which understands BeanShell
  *
  */
 public class BeanShellAssertion extends BeanShellTestElement implements Assertion {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellAssertion.class);
 
-    private static final long serialVersionUID = 3;
+    private static final long serialVersionUID = 4;
 
     public static final String FILENAME = "BeanShellAssertion.filename"; //$NON-NLS-1$
 
     public static final String SCRIPT = "BeanShellAssertion.query"; //$NON-NLS-1$
 
     public static final String PARAMETERS = "BeanShellAssertion.parameters"; //$NON-NLS-1$
 
     public static final String RESET_INTERPRETER = "BeanShellAssertion.resetInterpreter"; //$NON-NLS-1$
 
     // can be specified in jmeter.properties
     public static final String INIT_FILE = "beanshell.assertion.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
     @Override
     public String getScript() {
         return getPropertyAsString(SCRIPT);
     }
 
     @Override
     public String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     @Override
     public String getParameters() {
         return getPropertyAsString(PARAMETERS);
     }
 
     @Override
     public boolean isResetInterpreter() {
         return getPropertyAsBoolean(RESET_INTERPRETER);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
 
         final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
         if (bshInterpreter == null) {
             result.setFailure(true);
             result.setError(true);
             result.setFailureMessage("BeanShell Interpreter not found");
             return result;
         }
         try {
 
             // Add SamplerData for consistency with BeanShell Sampler
             bshInterpreter.set("SampleResult", response); //$NON-NLS-1$
             bshInterpreter.set("Response", response); //$NON-NLS-1$
             bshInterpreter.set("ResponseData", response.getResponseData());//$NON-NLS-1$
             bshInterpreter.set("ResponseCode", response.getResponseCode());//$NON-NLS-1$
             bshInterpreter.set("ResponseMessage", response.getResponseMessage());//$NON-NLS-1$
             bshInterpreter.set("ResponseHeaders", response.getResponseHeaders());//$NON-NLS-1$
             bshInterpreter.set("RequestHeaders", response.getRequestHeaders());//$NON-NLS-1$
             bshInterpreter.set("SampleLabel", response.getSampleLabel());//$NON-NLS-1$
             bshInterpreter.set("SamplerData", response.getSamplerData());//$NON-NLS-1$
             bshInterpreter.set("Successful", response.isSuccessful());//$NON-NLS-1$
 
             // The following are used to set the Result details on return from
             // the script:
             bshInterpreter.set("FailureMessage", "");//$NON-NLS-1$ //$NON-NLS-2$
             bshInterpreter.set("Failure", false);//$NON-NLS-1$
 
             processFileOrScript(bshInterpreter);
 
             result.setFailureMessage(bshInterpreter.get("FailureMessage").toString());//$NON-NLS-1$
             result.setFailure(Boolean.parseBoolean(bshInterpreter.get("Failure") //$NON-NLS-1$
                     .toString()));
             result.setError(false);
         }
         catch (NoClassDefFoundError ex) { // NOSONAR explicitely trap this error to make tests work better 
             log.error("BeanShell Jar missing? " + ex.toString());
             result.setError(true);
             result.setFailureMessage("BeanShell Jar missing? " + ex.toString());
             response.setStopThread(true); // No point continuing
         } catch (Exception ex) // Mainly for bsh.EvalError
         {
             result.setError(true);
             result.setFailureMessage(ex.toString());
             log.warn(ex.toString());
         }
 
         return result;
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
index 2cb82c77d..6a9d66280 100644
--- a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
@@ -1,371 +1,355 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.io.StringWriter;
 import java.nio.charset.StandardCharsets;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.tidy.Node;
 import org.w3c.tidy.Tidy;
 
 /**
  * Assertion to validate the response of a Sample with Tidy.
  */
 public class HTMLAssertion extends AbstractTestElement implements Serializable, Assertion {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTMLAssertion.class);
 
     public static final String DEFAULT_DOCTYPE = "omit"; //$NON-NLS-1$
 
     public static final String DOCTYPE_KEY = "html_assertion_doctype"; //$NON-NLS-1$
 
     public static final String ERRORS_ONLY_KEY = "html_assertion_errorsonly"; //$NON-NLS-1$
 
     public static final String ERROR_THRESHOLD_KEY = "html_assertion_error_threshold"; //$NON-NLS-1$
 
     public static final String WARNING_THRESHOLD_KEY = "html_assertion_warning_threshold"; //$NON-NLS-1$
 
     public static final String FORMAT_KEY = "html_assertion_format"; //$NON-NLS-1$
 
     public static final String FILENAME_KEY = "html_assertion_filename"; //$NON-NLS-1$
 
     /**
      * 
      */
     public HTMLAssertion() {
         log.debug("HTMLAssertion(): called");
     }
 
     /**
      * Returns the result of the Assertion. If so an AssertionResult containing
      * a FailureMessage will be returned. Otherwise the returned AssertionResult
      * will reflect the success of the Sample.
      */
     @Override
     public AssertionResult getResult(SampleResult inResponse) {
         log.debug("HTMLAssertions.getResult() called");
 
         // no error as default
         AssertionResult result = new AssertionResult(getName());
 
         if (inResponse.getResponseData().length == 0) {
             return result.setResultForNull();
         }
 
         result.setFailure(false);
 
         // create parser
         Tidy tidy = null;
         try {
-            if (log.isDebugEnabled()){
-                log.debug("HTMLAssertions.getResult(): Setup tidy ...");
-                log.debug("doctype: " + getDoctype());
-                log.debug("errors only: " + isErrorsOnly());
-                log.debug("error threshold: " + getErrorThreshold());
-                log.debug("warning threshold: " + getWarningThreshold());
-                log.debug("html mode: " + isHTML());
-                log.debug("xhtml mode: " + isXHTML());
-                log.debug("xml mode: " + isXML());
+            if (log.isDebugEnabled()) {
+                log.debug(
+                        "Setting up tidy... doctype: {}, errors only: {}, error threshold: {}, warning threshold: {}, html mode: {}, xhtml mode: {}, xml mode: {}.",
+                        getDoctype(), isErrorsOnly(), getErrorThreshold(), getWarningThreshold(), isHTML(), isXHTML(),
+                        isXML());
             }
             tidy = new Tidy();
             tidy.setInputEncoding(StandardCharsets.UTF_8.name());
             tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
             tidy.setQuiet(false);
             tidy.setShowWarnings(true);
             tidy.setOnlyErrors(isErrorsOnly());
             tidy.setDocType(getDoctype());
             if (isXHTML()) {
                 tidy.setXHTML(true);
             } else if (isXML()) {
                 tidy.setXmlTags(true);
             }
             tidy.setErrfile(getFilename());
 
             if (log.isDebugEnabled()) {
-                log.debug("err file: " + getFilename());
-                log.debug("getParser : tidy parser created - " + tidy);
-                log.debug("HTMLAssertions.getResult(): Tidy instance created!");
+                log.debug("Tidy instance created... err file: {}, tidy parser: {}", getFilename(), tidy);
             }
 
         } catch (Exception e) {
             log.error("Unable to instantiate tidy parser", e);
             result.setFailure(true);
             result.setFailureMessage("Unable to instantiate tidy parser");
             // return with an error
             return result;
         }
 
         /*
          * Run tidy.
          */
         try {
             log.debug("HTMLAssertions.getResult(): start parsing with tidy ...");
 
             StringWriter errbuf = new StringWriter();
             tidy.setErrout(new PrintWriter(errbuf));
             ByteArrayOutputStream os = new ByteArrayOutputStream();
-            log.debug("Start : parse");
+            log.debug("Parsing with tidy starting...");
             Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
-            if (log.isDebugEnabled()) {
-                log.debug("node : " + node);
-                log.debug("End   : parse");
-                log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
-                log.debug("Output: " + os.toString());
-            }
+            log.debug("Parsing with tidy done! node: {}, output: {}", node, os);
 
             // write output to file
             writeOutput(errbuf.toString());
 
             // evaluate result
             if ((tidy.getParseErrors() > getErrorThreshold())
                     || (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
-                if (log.isDebugEnabled()) {
-                    log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
-                    log.debug(errbuf.toString());
-                }
+                log.debug("Errors/warnings detected while parsing with tidy: {}", errbuf);
                 result.setFailure(true);
                 result.setFailureMessage(MessageFormat.format("Tidy Parser errors:   " + tidy.getParseErrors()
                         + " (allowed " + getErrorThreshold() + ") " + "Tidy Parser warnings: "
                         + tidy.getParseWarnings() + " (allowed " + getWarningThreshold() + ")", new Object[0]));
                 // return with an error
 
             } else if ((tidy.getParseErrors() > 0) || (tidy.getParseWarnings() > 0)) {
                 // return with no error
                 log.debug("HTMLAssertions.getResult(): there were errors/warnings but threshold to high");
                 result.setFailure(false);
             } else {
                 // return with no error
                 log.debug("HTMLAssertions.getResult(): no errors/warnings detected:");
                 result.setFailure(false);
             }
 
         } catch (Exception e) {
             // return with an error
             log.warn("Cannot parse result content", e);
             result.setFailure(true);
             result.setFailureMessage(e.getMessage());
         }
         return result;
     }
 
     /**
      * Writes the output of tidy to file.
      * 
      * @param inOutput The String to write to file
      */
     private void writeOutput(String inOutput) {
         String lFilename = getFilename();
 
         // check if filename defined
         if ((lFilename != null) && (!"".equals(lFilename.trim()))) {
             
             try (FileWriter lOutputWriter = new FileWriter(lFilename, false)){
                 // write to file
                 lOutputWriter.write(inOutput);
-                if (log.isDebugEnabled()) {
-                    log.debug("writeOutput() -> output successfully written to file " + lFilename);
-                }
+                log.debug("writeOutput() -> output successfully written to file: {}", lFilename);
             } catch (IOException ex) {
-                log.warn("writeOutput() -> could not write output to file " + lFilename, ex);
+                log.warn("writeOutput() -> could not write output to file: {}", lFilename, ex);
             }
         }
     }
 
     /**
      * Gets the doctype
      * 
      * @return the document type
      */
     public String getDoctype() {
         return getPropertyAsString(DOCTYPE_KEY);
     }
 
     /**
      * Check if errors will be reported only
      * 
      * @return boolean - report errors only?
      */
     public boolean isErrorsOnly() {
         return getPropertyAsBoolean(ERRORS_ONLY_KEY);
     }
 
     /**
      * Gets the threshold setting for errors
      * 
      * @return long error threshold
      */
     public long getErrorThreshold() {
         return getPropertyAsLong(ERROR_THRESHOLD_KEY);
     }
 
     /**
      * Gets the threshold setting for warnings
      * 
      * @return long warning threshold
      */
     public long getWarningThreshold() {
         return getPropertyAsLong(WARNING_THRESHOLD_KEY);
     }
 
     /**
      * Sets the doctype setting
      * 
      * @param inDoctype
      *            The doctype to be set. If <code>doctype</code> is
      *            <code>null</code> or a blank string, {@link HTMLAssertion#DEFAULT_DOCTYPE} will be
      *            used
      */
     public void setDoctype(String inDoctype) {
         if ((inDoctype == null) || (inDoctype.trim().isEmpty())) {
             setProperty(new StringProperty(DOCTYPE_KEY, DEFAULT_DOCTYPE));
         } else {
             setProperty(new StringProperty(DOCTYPE_KEY, inDoctype));
         }
     }
 
     /**
      * Sets if errors should be tracked only
      * 
      * @param inErrorsOnly Flag whether only errors should be tracked
      */
     public void setErrorsOnly(boolean inErrorsOnly) {
         setProperty(new BooleanProperty(ERRORS_ONLY_KEY, inErrorsOnly));
     }
 
     /**
      * Sets the threshold on error level
      * 
      * @param inErrorThreshold
      *            The max number of parse errors which are to be tolerated
      * @throws IllegalArgumentException
      *             if <code>inErrorThreshold</code> is less or equals zero
      */
     public void setErrorThreshold(long inErrorThreshold) {
         if (inErrorThreshold < 0L) {
             throw new IllegalArgumentException(JMeterUtils.getResString("argument_must_not_be_negative")); //$NON-NLS-1$
         }
         if (inErrorThreshold == Long.MAX_VALUE) {
             setProperty(new LongProperty(ERROR_THRESHOLD_KEY, 0));
         } else {
             setProperty(new LongProperty(ERROR_THRESHOLD_KEY, inErrorThreshold));
         }
     }
 
     /**
      * Sets the threshold on warning level
      * 
      * @param inWarningThreshold
      *            The max number of warnings which are to be tolerated
      * @throws IllegalArgumentException
      *             if <code>inWarningThreshold</code> is less or equal zero
      */
     public void setWarningThreshold(long inWarningThreshold) {
         if (inWarningThreshold < 0L) {
             throw new IllegalArgumentException(JMeterUtils.getResString("argument_must_not_be_negative")); //$NON-NLS-1$
         }
         if (inWarningThreshold == Long.MAX_VALUE) {
             setProperty(new LongProperty(WARNING_THRESHOLD_KEY, 0));
         } else {
             setProperty(new LongProperty(WARNING_THRESHOLD_KEY, inWarningThreshold));
         }
     }
 
     /**
      * Enables html validation mode
      */
     public void setHTML() {
         setProperty(new LongProperty(FORMAT_KEY, 0));
     }
 
     /**
      * Check if html validation mode is set
      * 
      * @return boolean
      */
     public boolean isHTML() {
         return getPropertyAsLong(FORMAT_KEY) == 0;
     }
 
     /**
      * Enables xhtml validation mode
      */
     public void setXHTML() {
         setProperty(new LongProperty(FORMAT_KEY, 1));
     }
 
     /**
      * Check if xhtml validation mode is set
      * 
      * @return boolean
      */
     public boolean isXHTML() {
         return getPropertyAsLong(FORMAT_KEY) == 1;
     }
 
     /**
      * Enables xml validation mode
      */
     public void setXML() {
         setProperty(new LongProperty(FORMAT_KEY, 2));
     }
 
     /**
      * Check if xml validation mode is set
      * 
      * @return boolean
      */
     public boolean isXML() {
         return getPropertyAsLong(FORMAT_KEY) == 2;
     }
 
     /**
      * Sets the name of the file where tidy writes the output to
      * 
      * @return name of file
      */
     public String getFilename() {
         return getPropertyAsString(FILENAME_KEY);
     }
 
     /**
      * Sets the name of the tidy output file
      * 
      * @param inName The name of the file tidy will put its output to
      */
     public void setFilename(String inName) {
         setProperty(FILENAME_KEY, inName);
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/JSR223Assertion.java b/src/components/org/apache/jmeter/assertions/JSR223Assertion.java
index d5227b640..17f123c8a 100644
--- a/src/components/org/apache/jmeter/assertions/JSR223Assertion.java
+++ b/src/components/org/apache/jmeter/assertions/JSR223Assertion.java
@@ -1,61 +1,61 @@
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
 
 import java.io.IOException;
 
 import javax.script.Bindings;
 import javax.script.ScriptEngine;
 import javax.script.ScriptException;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JSR223Assertion extends JSR223TestElement implements Cloneable, Assertion, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223Assertion.class);
 
-    private static final long serialVersionUID = 234L;
+    private static final long serialVersionUID = 235L;
 
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
         try {
             ScriptEngine scriptEngine = getScriptEngine();
             Bindings bindings = scriptEngine.createBindings();
             bindings.put("SampleResult", response);
             bindings.put("AssertionResult", result);
             processFileOrScript(scriptEngine, bindings);
             result.setError(false);
         } catch (IOException | ScriptException e) {
-            log.error("Problem in JSR223 script "+getName(), e);
+            log.error("Problem in JSR223 script: {}", getName(), e);
             result.setError(true);
             result.setFailureMessage(e.toString());
         }
         return result;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java b/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
index 7c955f55f..6f733ed2b 100644
--- a/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
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
 
 /**
  * MD5HexAssertion class creates an MD5 checksum from the response <br/>
  * and matches it with the MD5 hex provided.
  * The assertion will fail when the expected hex is different from the <br/>
  * one calculated from the response OR when the expected hex is left empty.
  * 
  */
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class MD5HexAssertion extends AbstractTestElement implements Serializable, Assertion {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MD5HexAssertion.class);
 
     /** Key for storing assertion-information in the jmx-file. */
     private static final String MD5HEX_KEY = "MD5HexAssertion.size";
 
     /*
      * @param response @return
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
 
         AssertionResult result = new AssertionResult(getName());
         result.setFailure(false);
         byte[] resultData = response.getResponseData();
 
         if (resultData.length == 0) {
             result.setError(false);
             result.setFailure(true);
             result.setFailureMessage("Response was null");
             return result;
         }
 
         // no point in checking if we don't have anything to compare against
         if (getAllowedMD5Hex().isEmpty()) {
             result.setError(false);
             result.setFailure(true);
             result.setFailureMessage("MD5Hex to test against is empty");
             return result;
         }
 
         String md5Result = baMD5Hex(resultData);
 
         if (!md5Result.equalsIgnoreCase(getAllowedMD5Hex())) {
             result.setFailure(true);
 
             Object[] arguments = { md5Result, getAllowedMD5Hex() };
             String message = MessageFormat.format(JMeterUtils.getResString("md5hex_assertion_failure"), arguments); // $NON-NLS-1$
             result.setFailureMessage(message);
 
         }
 
         return result;
     }
 
     public void setAllowedMD5Hex(String hex) {
         setProperty(new StringProperty(MD5HexAssertion.MD5HEX_KEY, hex));
     }
 
     public String getAllowedMD5Hex() {
         return getPropertyAsString(MD5HexAssertion.MD5HEX_KEY);
     }
 
     // package protected so can be accessed by test class
     static String baMD5Hex(byte[] ba) {
         byte[] md5Result = {};
 
         try {
             MessageDigest md = MessageDigest.getInstance("MD5");
             md5Result = md.digest(ba);
         } catch (NoSuchAlgorithmException e) {
-            log.error("", e);
+            log.error("Message digestion failed.", e);
         }
         return JOrphanUtils.baToHexString(md5Result);
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index 4a2b6c078..4cc32c2e9 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,548 +1,539 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.Document;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Test element to handle Response Assertions, @see AssertionGui
  * see org.apache.jmeter.assertions.ResponseAssertionTest for unit tests
  */
 public class ResponseAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ResponseAssertion.class);
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
     private static final String TEST_FIELD = "Assertion.test_field";  // $NON-NLS-1$
 
     // Values for TEST_FIELD
     // N.B. we cannot change the text value as it is in test plans
     private static final String SAMPLE_URL = "Assertion.sample_label"; // $NON-NLS-1$
 
     private static final String RESPONSE_DATA = "Assertion.response_data"; // $NON-NLS-1$
 
     private static final String RESPONSE_DATA_AS_DOCUMENT = "Assertion.response_data_as_document"; // $NON-NLS-1$
 
     private static final String RESPONSE_CODE = "Assertion.response_code"; // $NON-NLS-1$
 
     private static final String RESPONSE_MESSAGE = "Assertion.response_message"; // $NON-NLS-1$
 
     private static final String RESPONSE_HEADERS = "Assertion.response_headers"; // $NON-NLS-1$
 
     private static final String ASSUME_SUCCESS = "Assertion.assume_success"; // $NON-NLS-1$
 
     private static final String TEST_STRINGS = "Asserion.test_strings"; // $NON-NLS-1$
 
     private static final String TEST_TYPE = "Assertion.test_type"; // $NON-NLS-1$
 
     /**
      * Mask values for TEST_TYPE 
      * they are mutually exclusive
      */
     private static final int MATCH = 1; // 1 << 0; // NOSONAR We want this comment
 
     private static final int CONTAINS = 1 << 1;
 
     private static final int NOT = 1 << 2;
 
     private static final int EQUALS = 1 << 3;
 
     private static final int SUBSTRING = 1 << 4;
 
     private static final int OR = 1 << 5;
 
     // Mask should contain all types (but not NOT nor OR)
     private static final int TYPE_MASK = CONTAINS | EQUALS | MATCH | SUBSTRING;
 
     private static final int  EQUALS_SECTION_DIFF_LEN
             = JMeterUtils.getPropDefault("assertion.equals_section_diff_len", 100);
 
     /** Signifies truncated text in diff display. */
     private static final String EQUALS_DIFF_TRUNC = "...";
 
     private static final String RECEIVED_STR = "****** received  : ";
     private static final String COMPARISON_STR = "****** comparison: ";
     private static final String DIFF_DELTA_START
             = JMeterUtils.getPropDefault("assertion.equals_diff_delta_start", "[[[");
     private static final String DIFF_DELTA_END
             = JMeterUtils.getPropDefault("assertion.equals_diff_delta_end", "]]]");
 
     public ResponseAssertion() {
         setProperty(new CollectionProperty(TEST_STRINGS, new ArrayList<String>()));
     }
 
     @Override
     public void clear() {
         super.clear();
         setProperty(new CollectionProperty(TEST_STRINGS, new ArrayList<String>()));
     }
 
     private void setTestField(String testField) {
         setProperty(TEST_FIELD, testField);
     }
 
     public void setTestFieldURL(){
         setTestField(SAMPLE_URL);
     }
 
     public void setTestFieldResponseCode(){
         setTestField(RESPONSE_CODE);
     }
 
     public void setTestFieldResponseData(){
         setTestField(RESPONSE_DATA);
     }
 
     public void setTestFieldResponseDataAsDocument(){
         setTestField(RESPONSE_DATA_AS_DOCUMENT);
     }
 
     public void setTestFieldResponseMessage(){
         setTestField(RESPONSE_MESSAGE);
     }
 
     public void setTestFieldResponseHeaders(){
         setTestField(RESPONSE_HEADERS);
     }
 
     public boolean isTestFieldURL(){
         return SAMPLE_URL.equals(getTestField());
     }
 
     public boolean isTestFieldResponseCode(){
         return RESPONSE_CODE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseData(){
         return RESPONSE_DATA.equals(getTestField());
     }
 
     public boolean isTestFieldResponseDataAsDocument() {
         return RESPONSE_DATA_AS_DOCUMENT.equals(getTestField());
     }
 
     public boolean isTestFieldResponseMessage(){
         return RESPONSE_MESSAGE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseHeaders(){
         return RESPONSE_HEADERS.equals(getTestField());
     }
 
     private void setTestType(int testType) {
         setProperty(new IntegerProperty(TEST_TYPE, testType));
     }
 
     private void setTestTypeMasked(int testType) {
         int value = getTestType() & ~(TYPE_MASK) | testType;
         setProperty(new IntegerProperty(TEST_TYPE, value));
     }
 
     public void addTestString(String testString) {
         getTestStrings().addProperty(new StringProperty(String.valueOf(testString.hashCode()), testString));
     }
 
     public void clearTestStrings() {
         getTestStrings().clear();
     }
 
     @Override
     public AssertionResult getResult(SampleResult response) {
         return evaluateResponse(response);
     }
 
     public String getTestField() {
         return getPropertyAsString(TEST_FIELD);
     }
 
     public int getTestType() {
         JMeterProperty type = getProperty(TEST_TYPE);
         if (type instanceof NullProperty) {
             return CONTAINS;
         }
         return type.getIntValue();
     }
 
     public CollectionProperty getTestStrings() {
         return (CollectionProperty) getProperty(TEST_STRINGS);
     }
 
     public boolean isEqualsType() {
         return (getTestType() & EQUALS) != 0;
     }
 
     public boolean isSubstringType() {
         return (getTestType() & SUBSTRING) != 0;
     }
 
     public boolean isContainsType() {
         return (getTestType() & CONTAINS) != 0;
     }
 
     public boolean isMatchType() {
         return (getTestType() & MATCH) != 0;
     }
 
     public boolean isNotType() {
         return (getTestType() & NOT) != 0;
     }
     
     public boolean isOrType() {
         return (getTestType() & OR) != 0;
     }
 
     public void setToContainsType() {
         setTestTypeMasked(CONTAINS);
     }
 
     public void setToMatchType() {
         setTestTypeMasked(MATCH);
     }
 
     public void setToEqualsType() {
         setTestTypeMasked(EQUALS);
     }
 
     public void setToSubstringType() {
         setTestTypeMasked(SUBSTRING);
     }
 
     public void setToNotType() {
         setTestType(getTestType() | NOT);
     }
 
     public void unsetNotType() {
         setTestType(getTestType() & ~NOT);
     }
     
     public void setToOrType() {
         setTestType(getTestType() | OR);
     }
 
     public void unsetOrType() {
         setTestType(getTestType() & ~OR);
     }
 
     public boolean getAssumeSuccess() {
         return getPropertyAsBoolean(ASSUME_SUCCESS, false);
     }
 
     public void setAssumeSuccess(boolean b) {
         setProperty(ASSUME_SUCCESS, b);
     }
 
     /**
      * Make sure the response satisfies the specified assertion requirements.
      *
      * @param response
      *            an instance of SampleResult
      * @return an instance of AssertionResult
      */
     private AssertionResult evaluateResponse(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
 
         if (getAssumeSuccess()) {
             response.setSuccessful(true);// Allow testing of failure codes
         }
 
         String toCheck; // The string to check (Url or data)
         // What are we testing against?
         if (isScopeVariable()){
             toCheck = getThreadContext().getVariables().get(getVariableName());
         } else if (isTestFieldResponseData()) {
             toCheck = response.getResponseDataAsString(); // (bug25052)
         } else if (isTestFieldResponseDataAsDocument()) {
             toCheck = Document.getTextFromDocument(response.getResponseData()); 
         } else if (isTestFieldResponseCode()) {
             toCheck = response.getResponseCode();
         } else if (isTestFieldResponseMessage()) {
             toCheck = response.getResponseMessage();
         } else if (isTestFieldResponseHeaders()) {
             toCheck = response.getResponseHeaders();
         } else { // Assume it is the URL
             toCheck = "";
             final URL url = response.getURL();
             if (url != null){
                 toCheck = url.toString();
             }
         }
 
         result.setFailure(false);
         result.setError(false); 
         boolean notTest = (NOT & getTestType()) > 0;
         boolean orTest = (OR & getTestType()) > 0;
         boolean contains = isContainsType(); // do it once outside loop
         boolean equals = isEqualsType();
         boolean substring = isSubstringType();
         boolean matches = isMatchType();
-        boolean debugEnabled = log.isDebugEnabled();
-        if (debugEnabled){
-            log.debug("Type:" + (contains?"Contains" : "Match") + (notTest? "(not)" : ""));
-            log.debug("Type:" + (contains?"Contains" : "Match") + (orTest? "(or)" : ""));
-        }
+
+        log.debug("Test Type Info: contains={}, notTest={}, orTest={}", contains, notTest, orTest);
 
         if (StringUtils.isEmpty(toCheck)) {
             if (notTest) { // Not should always succeed against an empty result
                 return result;
             }
-            if (debugEnabled){
-                log.debug("Not checking empty response field in: "+response.getSampleLabel());
+            if(log.isDebugEnabled()) {
+                log.debug("Not checking empty response field in: {}", response.getSampleLabel());
             }
             return result.setResultForNull();
         }
 
         boolean pass = true;
         boolean hasTrue = false;
         ArrayList<String> allCheckMessage = new ArrayList<>();
         try {
             // Get the Matcher for this thread
             Perl5Matcher localMatcher = JMeterUtils.getMatcher();
             for (JMeterProperty jMeterProperty : getTestStrings()) {
                 String stringPattern = jMeterProperty.getStringValue();
                 Pattern pattern = null;
                 if (contains || matches) {
                     pattern = JMeterUtils.getPatternCache().getPattern(stringPattern, Perl5Compiler.READ_ONLY_MASK);
                 }
                 boolean found;
                 if (contains) {
                     found = localMatcher.contains(toCheck, pattern);
                 } else if (equals) {
                     found = toCheck.equals(stringPattern);
                 } else if (substring) {
                     found = toCheck.contains(stringPattern);
                 } else {
                     found = localMatcher.matches(toCheck, pattern);
                 }
                 pass = notTest ? !found : found;
                 if (orTest) {
                     if (!pass) {
-                        if (debugEnabled) {
-                            log.debug("Failed: "+stringPattern);
-                        }
+                        log.debug("Failed: {}", stringPattern);
                         allCheckMessage.add(getFailText(stringPattern,toCheck));
                     } else {
                         hasTrue=true;
                         break;
                     }
                 } else {
                     if (!pass) {
-                        if (debugEnabled){
-                            log.debug("Failed: "+stringPattern);
-                        }
+                        log.debug("Failed: {}", stringPattern);
                         result.setFailure(true);
                         result.setFailureMessage(getFailText(stringPattern,toCheck));
                         break;
                     }
-                    if (debugEnabled){
-                        log.debug("Passed: "+stringPattern);
-                    }
+                    log.debug("Passed: {}", stringPattern);
                 }
             }
             if (orTest && !hasTrue){
                 StringBuilder errorMsg = new StringBuilder();
                 for(String tmp : allCheckMessage){
                     errorMsg.append(tmp).append('\t');
                 }
                 result.setFailure(true);
                 result.setFailureMessage(errorMsg.toString());   
             }
         } catch (MalformedCachePatternException e) {
             result.setError(true);
             result.setFailure(false);
             result.setFailureMessage("Bad test configuration " + e);
         }
         return result;
     }
 
     /**
      * Generate the failure reason from the TestType
      *
      * @param stringPattern
      * @return the message for the assertion report
      */
     private String getFailText(String stringPattern, String toCheck) {
 
         StringBuilder sb = new StringBuilder(200);
         sb.append("Test failed: ");
 
         if (isScopeVariable()){
             sb.append("variable(").append(getVariableName()).append(')');
         } else if (isTestFieldResponseData()) {
             sb.append("text");
         } else if (isTestFieldResponseCode()) {
             sb.append("code");
         } else if (isTestFieldResponseMessage()) {
             sb.append("message");
         } else if (isTestFieldResponseHeaders()) {
             sb.append("headers");
         } else if (isTestFieldResponseDataAsDocument()) {
             sb.append("document");
         } else // Assume it is the URL
         {
             sb.append("URL");
         }
 
         switch (getTestType()) {
         case CONTAINS:
         case SUBSTRING:
             sb.append(" expected to contain ");
             break;
         case NOT | CONTAINS:
         case NOT | SUBSTRING:
             sb.append(" expected not to contain ");
             break;
         case MATCH:
             sb.append(" expected to match ");
             break;
         case NOT | MATCH:
             sb.append(" expected not to match ");
             break;
         case EQUALS:
             sb.append(" expected to equal ");
             break;
         case NOT | EQUALS:
             sb.append(" expected not to equal ");
             break;
         default:// should never happen...
             sb.append(" expected something using ");
         }
 
         sb.append("/");
 
         if (isEqualsType()){
             sb.append(equalsComparisonText(toCheck, stringPattern));
         } else {
             sb.append(stringPattern);
         }
 
         sb.append("/");
         return sb.toString();
     }
 
 
     private static String trunc(final boolean right, final String str)
     {
         if (str.length() <= EQUALS_SECTION_DIFF_LEN) {
             return str;
         } else if (right) {
             return str.substring(0, EQUALS_SECTION_DIFF_LEN) + EQUALS_DIFF_TRUNC;
         } else {
             return EQUALS_DIFF_TRUNC + str.substring(str.length() - EQUALS_SECTION_DIFF_LEN, str.length());
         }
     }
 
     /**
      *   Returns some helpful logging text to determine where equality between two strings
      * is broken, with one pointer working from the front of the strings and another working
      * backwards from the end.
      *
      * @param received      String received from sampler.
      * @param comparison    String specified for "equals" response assertion.
      * @return  Two lines of text separated by newlines, and then forward and backward pointers
      *      denoting first position of difference.
      */
     private static StringBuilder equalsComparisonText(final String received, final String comparison)
     {
         final int recLength = received.length();
         final int compLength = comparison.length();
         final int minLength = Math.min(recLength, compLength);
 
         final StringBuilder text = new StringBuilder(Math.max(recLength, compLength) * 2);
         int firstDiff;
         for (firstDiff = 0; firstDiff < minLength; firstDiff++) {
             if (received.charAt(firstDiff) != comparison.charAt(firstDiff)){
                 break;
             }
         }
         final String            startingEqSeq;
         if (firstDiff == 0) {
             startingEqSeq = "";
         } else {
             startingEqSeq = trunc(false, received.substring(0, firstDiff));
         }
 
         int lastRecDiff = recLength - 1;
         int lastCompDiff = compLength - 1;
 
         while ((lastRecDiff > firstDiff) && (lastCompDiff > firstDiff)
                 && received.charAt(lastRecDiff) == comparison.charAt(lastCompDiff))
         {
             lastRecDiff--;
             lastCompDiff--;
         }
         String compDeltaSeq;
         String endingEqSeq = trunc(true, received.substring(lastRecDiff + 1, recLength));
         String                  recDeltaSeq;
         if (endingEqSeq.length() == 0) {
             recDeltaSeq = trunc(true, received.substring(firstDiff, recLength));
             compDeltaSeq = trunc(true, comparison.substring(firstDiff, compLength));
         }
         else {
             recDeltaSeq = trunc(true, received.substring(firstDiff, lastRecDiff + 1));
             compDeltaSeq = trunc(true, comparison.substring(firstDiff, lastCompDiff + 1));
         }
         final StringBuilder pad = new StringBuilder(Math.abs(recDeltaSeq.length() - compDeltaSeq.length()));
         for (int i = 0; i < pad.capacity(); i++){
             pad.append(' ');
         }
         
         if (recDeltaSeq.length() > compDeltaSeq.length()){
             compDeltaSeq += pad.toString();
         } else {
             recDeltaSeq += pad.toString();
         }
 
         text.append("\n\n");
         text.append(RECEIVED_STR);
         text.append(startingEqSeq);
         text.append(DIFF_DELTA_START);
         text.append(recDeltaSeq);
         text.append(DIFF_DELTA_END);
         text.append(endingEqSeq);
         text.append("\n\n");
         text.append(COMPARISON_STR);
         text.append(startingEqSeq);
         text.append(DIFF_DELTA_START);
         text.append(compDeltaSeq);
         text.append(DIFF_DELTA_END);
         text.append(endingEqSeq);
         text.append("\n\n");
         return text;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java b/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
index b6e2a504c..e70afd777 100644
--- a/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/SMIMEAssertion.java
@@ -1,375 +1,375 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.math.BigInteger;
 import java.security.GeneralSecurityException;
 import java.security.Security;
 import java.security.cert.CertificateException;
 import java.security.cert.CertificateFactory;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import javax.mail.MessagingException;
 import javax.mail.Session;
 import javax.mail.internet.MimeMessage;
 import javax.mail.internet.MimeMultipart;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.samplers.SampleResult;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
 import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
 import org.bouncycastle.asn1.x500.RDN;
 import org.bouncycastle.asn1.x500.X500Name;
 import org.bouncycastle.asn1.x500.style.BCStyle;
 import org.bouncycastle.asn1.x500.style.IETFUtils;
 import org.bouncycastle.asn1.x509.Extension;
 import org.bouncycastle.asn1.x509.GeneralName;
 import org.bouncycastle.asn1.x509.GeneralNames;
 import org.bouncycastle.cert.X509CertificateHolder;
 import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
 import org.bouncycastle.cms.CMSException;
 import org.bouncycastle.cms.SignerInformation;
 import org.bouncycastle.cms.SignerInformationStore;
 import org.bouncycastle.cms.SignerInformationVerifier;
 import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
 import org.bouncycastle.jce.provider.BouncyCastleProvider;
 import org.bouncycastle.mail.smime.SMIMEException;
 import org.bouncycastle.mail.smime.SMIMESignedParser;
 import org.bouncycastle.operator.OperatorCreationException;
 import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
 import org.bouncycastle.util.Store;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Helper class which isolates the BouncyCastle code.
  */
 class SMIMEAssertion {
 
     // Use the name of the test element, otherwise cannot enable/disable debug from the GUI
-    private static final Logger log = LoggingManager.getLoggerForShortName(SMIMEAssertionTestElement.class.getName());
+    private static final Logger log = LoggerFactory.getLogger(SMIMEAssertionTestElement.class);
 
     SMIMEAssertion() {
         super();
     }
 
     public static AssertionResult getResult(SMIMEAssertionTestElement testElement, SampleResult response, String name) {
         checkForBouncycastle();
         AssertionResult res = new AssertionResult(name);
         try {
             MimeMessage msg;
             final int msgPos = testElement.getSpecificMessagePositionAsInt();
             if (msgPos < 0){ // means counting from end
                 SampleResult[] subResults = response.getSubResults();
                 final int pos = subResults.length + msgPos;
-                if (log.isDebugEnabled()) {
-                    log.debug("Getting message number: "+pos+" of "+subResults.length);
-                }
+                log.debug("Getting message number: {} of {}", pos, subResults.length);
                 msg = getMessageFromResponse(response,pos);
             } else {
-                if (log.isDebugEnabled()) {
-                    log.debug("Getting message number: "+msgPos);
-                }
+                log.debug("Getting message number: {}", msgPos);
                 msg = getMessageFromResponse(response, msgPos);
             }
             
             SMIMESignedParser s = null;
-            if (log.isDebugEnabled()) {
-                log.debug("Content-type: "+msg.getContentType());
+            if(log.isDebugEnabled()) {
+                log.debug("Content-type: {}", msg.getContentType());
             }
             if (msg.isMimeType("multipart/signed")) { // $NON-NLS-1$
                 MimeMultipart multipart = (MimeMultipart) msg.getContent();
                 s = new SMIMESignedParser(new BcDigestCalculatorProvider(), multipart);
             } else if (msg.isMimeType("application/pkcs7-mime") // $NON-NLS-1$
                     || msg.isMimeType("application/x-pkcs7-mime")) { // $NON-NLS-1$
                 s = new SMIMESignedParser(new BcDigestCalculatorProvider(), msg);
             }
 
             if (null != s) {
                 log.debug("Found signature");
 
                 if (testElement.isNotSigned()) {
                     res.setFailure(true);
                     res.setFailureMessage("Mime message is signed");
                 } else if (testElement.isVerifySignature() || !testElement.isSignerNoCheck()) {
                     res = verifySignature(testElement, s, name);
                 }
 
             } else {
                 log.debug("Did not find signature");
                 if (!testElement.isNotSigned()) {
                     res.setFailure(true);
                     res.setFailureMessage("Mime message is not signed");
                 }
             }
 
         } catch (MessagingException e) {
             String msg = "Cannot parse mime msg: " + e.getMessage();
             log.warn(msg, e);
             res.setFailure(true);
             res.setFailureMessage(msg);
         } catch (CMSException e) {
             res.setFailure(true);
             res.setFailureMessage("Error reading the signature: "
                     + e.getMessage());
         } catch (SMIMEException e) {
             res.setFailure(true);
             res.setFailureMessage("Cannot extract signed body part from signature: "
                     + e.getMessage());
         } catch (IOException e) { // should never happen
-            log.error("Cannot read mime message content: " + e.getMessage(), e);
+            log.error("Cannot read mime message content: {}", e.getMessage(), e);
             res.setError(true);
             res.setFailureMessage(e.getMessage());
         }
 
         return res;
     }
 
     private static AssertionResult verifySignature(SMIMEAssertionTestElement testElement, SMIMESignedParser s, String name)
             throws CMSException {
         AssertionResult res = new AssertionResult(name);
 
         try {
             Store certs = s.getCertificates();
             SignerInformationStore signers = s.getSignerInfos();
             Iterator<?> signerIt = signers.getSigners().iterator();
 
             if (signerIt.hasNext()) {
 
                 SignerInformation signer = (SignerInformation) signerIt.next();
                 Iterator<?> certIt = certs.getMatches(signer.getSID()).iterator();
 
                 if (certIt.hasNext()) {
                     // the signer certificate
                     X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
 
                     if (testElement.isVerifySignature()) {
 
                         SignerInformationVerifier verifier = null;
                         try {
                             verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC")
                                     .build(cert);
                         } catch (OperatorCreationException e) {
-                            log.error("Can't create a provider", e);
+                            log.error("Can't create a provider.", e);
                         }
                         if (verifier == null || !signer.verify(verifier)) {
                             res.setFailure(true);
                             res.setFailureMessage("Signature is invalid");
                         }
                     }
 
                     if (testElement.isSignerCheckConstraints()) {
                         StringBuilder failureMessage = new StringBuilder();
 
                         String serial = testElement.getSignerSerial();
                         if (!JOrphanUtils.isBlank(serial)) {
                             BigInteger serialNbr = readSerialNumber(serial);
                             if (!serialNbr.equals(cert.getSerialNumber())) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Serial number ")
                                         .append(serialNbr)
                                         .append(" does not match serial from signer certificate: ")
                                         .append(cert.getSerialNumber()).append("\n");
                             }
                         }
 
                         String email = testElement.getSignerEmail();
                         if (!JOrphanUtils.isBlank(email)) {
                             List<String> emailFromCert = getEmailFromCert(cert);
                             if (!emailFromCert.contains(email)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Email address \"")
                                         .append(email)
                                         .append("\" not present in signer certificate\n");
                             }
 
                         }
 
                         String subject = testElement.getSignerDn();
                         if (subject.length() > 0) {
                             final X500Name certPrincipal = cert.getSubject();
-                            log.debug("DN from cert: " + certPrincipal.toString());
+                            log.debug("DN from cert: {}", certPrincipal);
                             X500Name principal = new X500Name(subject);
-                            log.debug("DN from assertion: " + principal.toString());
+                            log.debug("DN from assertion: {}", principal);
                             if (!principal.equals(certPrincipal)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Distinguished name of signer certificate does not match \"")
                                         .append(subject).append("\"\n");
                             }
                         }
 
                         String issuer = testElement.getIssuerDn();
                         if (issuer.length() > 0) {
                             final X500Name issuerX500Name = cert.getIssuer();
-                            log.debug("IssuerDN from cert: " + issuerX500Name.toString());
+                            log.debug("IssuerDN from cert: {}", issuerX500Name);
                             X500Name principal = new X500Name(issuer);
-                            log.debug("IssuerDN from assertion: " + principal);
+                            log.debug("IssuerDN from assertion: {}", principal);
                             if (!principal.equals(issuerX500Name)) {
                                 res.setFailure(true);
                                 failureMessage
                                         .append("Issuer distinguished name of signer certificate does not match \"")
                                         .append(subject).append("\"\n");
                             }
                         }
 
                         if (failureMessage.length() > 0) {
                             res.setFailureMessage(failureMessage.toString());
                         }
                     }
 
                     if (testElement.isSignerCheckByFile()) {
                         CertificateFactory cf = CertificateFactory
                                 .getInstance("X.509");
                         X509CertificateHolder certFromFile;
                         InputStream inStream = null;
                         try {
                             inStream = new BufferedInputStream(new FileInputStream(testElement.getSignerCertFile()));
                             certFromFile = new JcaX509CertificateHolder((X509Certificate) cf.generateCertificate(inStream));
                         } finally {
                             IOUtils.closeQuietly(inStream);
                         }
 
                         if (!certFromFile.equals(cert)) {
                             res.setFailure(true);
                             res.setFailureMessage("Signer certificate does not match certificate "
                                             + testElement.getSignerCertFile());
                         }
                     }
 
                 } else {
                     res.setFailure(true);
                     res.setFailureMessage("No signer certificate found in signature");
                 }
 
             }
 
             // TODO support multiple signers
             if (signerIt.hasNext()) {
                 log.warn("SMIME message contains multiple signers! Checking multiple signers is not supported.");
             }
 
         } catch (GeneralSecurityException e) {
             log.error(e.getMessage(), e);
             res.setError(true);
             res.setFailureMessage(e.getMessage());
         } catch (FileNotFoundException e) {
             res.setFailure(true);
             res.setFailureMessage("certificate file not found: " + e.getMessage());
         }
 
         return res;
     }
 
     /**
      * extracts a MIME message from the SampleResult
      */
     private static MimeMessage getMessageFromResponse(SampleResult response,
             int messageNumber) throws MessagingException {
         SampleResult[] subResults = response.getSubResults();
 
         if (messageNumber >= subResults.length || messageNumber < 0) {
             throw new MessagingException("Message number not present in results: "+messageNumber);
         }
 
         final SampleResult sampleResult = subResults[messageNumber];
-        if (log.isDebugEnabled()) {
-            log.debug("Bytes: "+sampleResult.getBytesAsLong()+" CT: "+sampleResult.getContentType());
+        if(log.isDebugEnabled()) {
+            log.debug("Bytes: {}, Content Type: {}", sampleResult.getBytesAsLong(), sampleResult.getContentType());
         }
         byte[] data = sampleResult.getResponseData();
         Session session = Session.getDefaultInstance(new Properties());
         MimeMessage msg = new MimeMessage(session, new ByteArrayInputStream(data));
 
-        log.debug("msg.getSize() = " + msg.getSize());
+        if(log.isDebugEnabled()) {
+            log.debug("msg.getSize() = {}", msg.getSize());
+        }
         return msg;
     }
 
     /**
      * Convert the value of <code>serialString</code> into a BigInteger. Strings
      * starting with 0x or 0X are parsed as hex numbers, otherwise as decimal
      * number.
      * 
      * @param serialString
      *            the String representation of the serial Number
      * @return the BitInteger representation of the serial Number
      */
     private static BigInteger readSerialNumber(String serialString) {
         if (serialString.startsWith("0x") || serialString.startsWith("0X")) { // $NON-NLS-1$  // $NON-NLS-2$
             return new BigInteger(serialString.substring(2), 16);
         } 
         return new BigInteger(serialString);
     }
 
     /**
      * Extract email addresses from a certificate
      * 
      * @param cert the X509 certificate holder
      * @return a List of all email addresses found
      * @throws CertificateException
      */
     private static List<String> getEmailFromCert(X509CertificateHolder cert)
             throws CertificateException {
         List<String> res = new ArrayList<>();
 
         X500Name subject = cert.getSubject();
         for (RDN emails : subject.getRDNs(BCStyle.EmailAddress)) {
             for (AttributeTypeAndValue emailAttr: emails.getTypesAndValues()) {
-                log.debug("Add email from RDN: " + IETFUtils.valueToString(emailAttr.getValue()));
+                if (log.isDebugEnabled()) {
+                    log.debug("Add email from RDN: {}", IETFUtils.valueToString(emailAttr.getValue()));
+                }
                 res.add(IETFUtils.valueToString(emailAttr.getValue()));
             }
         }
 
         Extension subjectAlternativeNames = cert
                 .getExtension(Extension.subjectAlternativeName);
         if (subjectAlternativeNames != null) {
             for (GeneralName name : GeneralNames.getInstance(
                     subjectAlternativeNames.getParsedValue()).getNames()) {
                 if (name.getTagNo() == GeneralName.rfc822Name) {
                     String email = IETFUtils.valueToString(name.getName());
-                    log.debug("Add email from subjectAlternativeName: " + email);
+                    log.debug("Add email from subjectAlternativeName: {}", email);
                     res.add(email);
                 }
             }
         }
 
         return res;
     }
 
     /**
      * Check if the Bouncycastle jce provider is installed and dynamically load
      * it, if needed;
      */
     private static void checkForBouncycastle() {
         if (null == Security.getProvider("BC")) { // $NON-NLS-1$
             Security.addProvider(new BouncyCastleProvider());
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/XMLAssertion.java b/src/components/org/apache/jmeter/assertions/XMLAssertion.java
index 7ada8ac58..00954f521 100644
--- a/src/components/org/apache/jmeter/assertions/XMLAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XMLAssertion.java
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.io.StringReader;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.ThreadListener;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.XMLReaderFactory;
 
 /**
  * Checks if the result is a well-formed XML content using {@link XMLReader}
  * 
  */
 public class XMLAssertion extends AbstractTestElement implements Serializable, Assertion, ThreadListener {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XMLAssertion.class);
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
     // one builder for all requests in a thread
     private static final ThreadLocal<XMLReader> XML_READER = new ThreadLocal<XMLReader>() {
         @Override
         protected XMLReader initialValue() {
             try {
                 return XMLReaderFactory.createXMLReader();
             } catch (SAXException e) {
-                LOG.error("Error initializing XMLReader in XMLAssertion", e); 
+                log.error("Error initializing XMLReader in XMLAssertion", e); 
                 return null;
             }
         }
     };
 
     /**
      * Returns the result of the Assertion. Here it checks whether the Sample
      * took to long to be considered successful. If so an AssertionResult
      * containing a FailureMessage will be returned. Otherwise the returned
      * AssertionResult will reflect the success of the Sample.
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         // no error as default
         AssertionResult result = new AssertionResult(getName());
         String resultData = response.getResponseDataAsString();
         if (resultData.length() == 0) {
             return result.setResultForNull();
         }
         result.setFailure(false);
         XMLReader builder = XML_READER.get();
         if(builder != null) {
             try {
                 builder.parse(new InputSource(new StringReader(resultData)));
             } catch (SAXException | IOException e) {
                 result.setError(true);
                 result.setFailureMessage(e.getMessage());
             }
         } else {
             result.setError(true);
             result.setFailureMessage("Cannot initialize XMLReader in element:"+getName()+", check jmeter.log file");
         }
 
         return result;
     }
 
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
         XML_READER.set(null);
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
index cd97e4ba0..c097096ab 100644
--- a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
@@ -1,204 +1,201 @@
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
 package org.apache.jmeter.assertions;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.io.StringReader;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 // See Bug 34383
 
 /**
  * XMLSchemaAssertion.java Validate response against an XML Schema author
  * <a href="mailto:d.maung@mdl.com">Dave Maung</a>
  * 
  */
 public class XMLSchemaAssertion extends AbstractTestElement implements Serializable, Assertion {
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
     public static final String FILE_NAME_IS_REQUIRED = "FileName is required";
 
     public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
 
     public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
 
     public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XMLSchemaAssertion.class);
 
     public static final String XSD_FILENAME_KEY = "xmlschema_assertion_filename";
 
     /**
      * getResult
      * 
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
         // Note: initialised with error = failure = false
 
         String resultData = response.getResponseDataAsString();
         if (resultData.length() == 0) {
             return result.setResultForNull();
         }
 
         String xsdFileName = getXsdFileName();
-        if (log.isDebugEnabled()) {
-            log.debug("xmlString: " + resultData);
-            log.debug("xsdFileName: " + xsdFileName);
-        }
+        log.debug("xmlString: {}, xsdFileName: {}", resultData, xsdFileName);
         if (xsdFileName == null || xsdFileName.length() == 0) {
             result.setResultForFailure(FILE_NAME_IS_REQUIRED);
         } else {
             setSchemaResult(result, resultData, xsdFileName);
         }
         return result;
     }
 
     public void setXsdFileName(String xmlSchemaFileName) throws IllegalArgumentException {
         setProperty(XSD_FILENAME_KEY, xmlSchemaFileName);
     }
 
     public String getXsdFileName() {
         return getPropertyAsString(XSD_FILENAME_KEY);
     }
 
     /**
      * set Schema result
      * 
      * @param result
      * @param xmlStr
      * @param xsdFileName
      */
     private void setSchemaResult(AssertionResult result, String xmlStr, String xsdFileName) {
         try {
             DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
             parserFactory.setValidating(true);
             parserFactory.setNamespaceAware(true);
             parserFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
             parserFactory.setAttribute(JAXP_SCHEMA_SOURCE, xsdFileName);
 
             // create a parser:
             DocumentBuilder parser = parserFactory.newDocumentBuilder();
             parser.setErrorHandler(new SAXErrorHandler(result));
             parser.parse(new InputSource(new StringReader(xmlStr)));
             // if everything went fine then xml schema validation is valid
         } catch (SAXParseException e) {
 
             // Only set message if error not yet flagged
             if (!result.isError() && !result.isFailure()) {
                 result.setError(true);
                 result.setFailureMessage(errorDetails(e));
             }
 
         } catch (SAXException e) {
 
             log.warn(e.toString());
             result.setResultForFailure(e.getMessage());
 
         } catch (IOException e) {
 
             log.warn("IO error", e);
             result.setResultForFailure(e.getMessage());
 
         } catch (ParserConfigurationException e) {
 
             log.warn("Problem with Parser Config", e);
             result.setResultForFailure(e.getMessage());
 
         }
 
     }
 
     // Helper method to construct SAX error details
     private static String errorDetails(SAXParseException spe) {
         StringBuilder str = new StringBuilder(80);
         int i;
         i = spe.getLineNumber();
         if (i != -1) {
             str.append("line=");
             str.append(i);
             str.append(" col=");
             str.append(spe.getColumnNumber());
             str.append(" ");
         }
         str.append(spe.getLocalizedMessage());
         return str.toString();
     }
 
     /**
      * SAXErrorHandler class
      */
     private static class SAXErrorHandler implements ErrorHandler {
         private final AssertionResult result;
 
         public SAXErrorHandler(AssertionResult result) {
             this.result = result;
         }
 
         /*
          * Can be caused by: - failure to read XSD file - xml does not match XSD
          */
         @Override
         public void error(SAXParseException exception) throws SAXParseException {
 
             String msg = "error: " + errorDetails(exception);
             log.debug(msg);
             result.setFailureMessage(msg);
             result.setError(true);
             throw exception;
         }
 
         /*
          * Can be caused by: - premature end of file - non-whitespace content
          * after trailer
          */
         @Override
         public void fatalError(SAXParseException exception) throws SAXParseException {
             String msg = "fatal: " + errorDetails(exception);
             log.debug(msg);
             result.setFailureMessage(msg);
             result.setError(true);
             throw exception;
         }
 
         /*
          * Not clear what can cause this ? conflicting versions perhaps
          */
         @Override
         public void warning(SAXParseException exception) throws SAXParseException {
             String msg = "warning: " + errorDetails(exception);
             log.debug(msg);
             result.setFailureMessage(msg);
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/XPathAssertion.java b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
index a7607070e..a0c548fcf 100644
--- a/src/components/org/apache/jmeter/assertions/XPathAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
@@ -1,275 +1,272 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.Serializable;
 import java.nio.charset.StandardCharsets;
 
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.TidyException;
 import org.apache.jmeter.util.XPathUtil;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 /**
  * Checks if the result is a well-formed XML content and whether it matches an
  * XPath
  *
  */
 public class XPathAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathAssertion.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     //+ JMX file attributes
     private static final String XPATH_KEY         = "XPath.xpath"; // $NON-NLS-1$
     private static final String WHITESPACE_KEY    = "XPath.whitespace"; // $NON-NLS-1$
     private static final String VALIDATE_KEY      = "XPath.validate"; // $NON-NLS-1$
     private static final String TOLERANT_KEY      = "XPath.tolerant"; // $NON-NLS-1$
     private static final String NEGATE_KEY        = "XPath.negate"; // $NON-NLS-1$
     private static final String NAMESPACE_KEY     = "XPath.namespace"; // $NON-NLS-1$
     private static final String QUIET_KEY         = "XPath.quiet"; // $NON-NLS-1$
     private static final String REPORT_ERRORS_KEY = "XPath.report_errors"; // $NON-NLS-1$
     private static final String SHOW_WARNINGS_KEY = "XPath.show_warnings"; // $NON-NLS-1$
     private static final String DOWNLOAD_DTDS     = "XPath.download_dtds"; // $NON-NLS-1$
     //- JMX file attributes
 
     public static final String DEFAULT_XPATH = "/";
 
     /**
      * Returns the result of the Assertion. Checks if the result is well-formed
      * XML, and that the XPath expression is matched (or not, as the case may
      * be)
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         // no error as default
         AssertionResult result = new AssertionResult(getName());
         result.setFailure(false);
         result.setFailureMessage("");
 
         byte[] responseData = null;
         Document doc = null;
 
         try {
             if (isScopeVariable()){
                 String inputString=getThreadContext().getVariables().get(getVariableName());
                 if (!StringUtils.isEmpty(inputString)) {
                     responseData = inputString.getBytes(StandardCharsets.UTF_8);
                 } 
             } else {
                 responseData = response.getResponseData();
             }
             
             if (responseData == null || responseData.length == 0) {
                 return result.setResultForNull();
             }
-    
-            if (log.isDebugEnabled()) {
-                log.debug(new StringBuilder("Validation is set to ").append(isValidating()).toString());
-                log.debug(new StringBuilder("Whitespace is set to ").append(isWhitespace()).toString());
-                log.debug(new StringBuilder("Tolerant is set to ").append(isTolerant()).toString());
+
+            if(log.isDebugEnabled()) {
+                log.debug("Validation is set to {}, Whitespace is set to {}, Tolerant is set to {}", isValidating(),
+                    isWhitespace(), isTolerant());
             }
-    
-    
             boolean isXML = JOrphanUtils.isXML(responseData);
 
             doc = XPathUtil.makeDocument(new ByteArrayInputStream(responseData), isValidating(),
                     isWhitespace(), isNamespace(), isTolerant(), isQuiet(), showWarnings() , reportErrors(), isXML
                     , isDownloadDTDs());
         } catch (SAXException e) {
-            log.debug("Caught sax exception: " + e);
+            log.debug("Caught sax exception.", e);
             result.setError(true);
             result.setFailureMessage(new StringBuilder("SAXException: ").append(e.getMessage()).toString());
             return result;
         } catch (IOException e) {
-            log.warn("Cannot parse result content", e);
+            log.warn("Cannot parse result content.", e);
             result.setError(true);
             result.setFailureMessage(new StringBuilder("IOException: ").append(e.getMessage()).toString());
             return result;
         } catch (ParserConfigurationException e) {
-            log.warn("Cannot parse result content", e);
+            log.warn("Cannot parse result content.", e);
             result.setError(true);
             result.setFailureMessage(new StringBuilder("ParserConfigurationException: ").append(e.getMessage())
                     .toString());
             return result;
         } catch (TidyException e) {
             result.setError(true);
             result.setFailureMessage(e.getMessage());
             return result;
         }
 
         if (doc == null || doc.getDocumentElement() == null) {
             result.setError(true);
             result.setFailureMessage("Document is null, probably not parsable");
             return result;
         }
         XPathUtil.computeAssertionResult(result, doc, getXPathString(), isNegated());
         return result;
     }
 
     /**
      * Get The XPath String that will be used in matching the document
      *
      * @return String xpath String
      */
     public String getXPathString() {
         return getPropertyAsString(XPATH_KEY, DEFAULT_XPATH);
     }
 
     /**
      * Set the XPath String this will be used as an xpath
      *
      * @param xpath
      *            String
      */
     public void setXPathString(String xpath) {
         setProperty(new StringProperty(XPATH_KEY, xpath));
     }
 
     /**
      * Set whether to ignore element whitespace
      *
      * @param whitespace Flag whether whitespace elements should be ignored
      */
     public void setWhitespace(boolean whitespace) {
         setProperty(new BooleanProperty(WHITESPACE_KEY, whitespace));
     }
 
     /**
      * Set use validation
      *
      * @param validate Flag whether validation should be used
      */
     public void setValidating(boolean validate) {
         setProperty(new BooleanProperty(VALIDATE_KEY, validate));
     }
 
     /**
      * Set whether this is namespace aware
      *
      * @param namespace Flag whether namespace should be used
      */
     public void setNamespace(boolean namespace) {
         setProperty(new BooleanProperty(NAMESPACE_KEY, namespace));
     }
 
     /**
      * Set tolerant mode if required
      *
      * @param tolerant
      *            true/false
      */
     public void setTolerant(boolean tolerant) {
         setProperty(new BooleanProperty(TOLERANT_KEY, tolerant));
     }
 
     public void setNegated(boolean negate) {
         setProperty(new BooleanProperty(NEGATE_KEY, negate));
     }
 
     /**
      * Is this whitespace ignored.
      *
      * @return boolean
      */
     public boolean isWhitespace() {
         return getPropertyAsBoolean(WHITESPACE_KEY, false);
     }
 
     /**
      * Is this validating
      *
      * @return boolean
      */
     public boolean isValidating() {
         return getPropertyAsBoolean(VALIDATE_KEY, false);
     }
 
     /**
      * Is this namespace aware?
      *
      * @return boolean
      */
     public boolean isNamespace() {
         return getPropertyAsBoolean(NAMESPACE_KEY, false);
     }
 
     /**
      * Is this using tolerant mode?
      *
      * @return boolean
      */
     public boolean isTolerant() {
         return getPropertyAsBoolean(TOLERANT_KEY, false);
     }
 
     /**
      * Negate the XPath test, that is return true if something is not found.
      *
      * @return boolean negated
      */
     public boolean isNegated() {
         return getPropertyAsBoolean(NEGATE_KEY, false);
     }
 
     public void setReportErrors(boolean val) {
         setProperty(REPORT_ERRORS_KEY, val, false);
     }
 
     public boolean reportErrors() {
         return getPropertyAsBoolean(REPORT_ERRORS_KEY, false);
     }
 
     public void setShowWarnings(boolean val) {
         setProperty(SHOW_WARNINGS_KEY, val, false);
     }
 
     public boolean showWarnings() {
         return getPropertyAsBoolean(SHOW_WARNINGS_KEY, false);
     }
 
     public void setQuiet(boolean val) {
         setProperty(QUIET_KEY, val, true);
     }
 
     public boolean isQuiet() {
         return getPropertyAsBoolean(QUIET_KEY, true);
     }
 
     public void setDownloadDTDs(boolean val) {
         setProperty(DOWNLOAD_DTDS, val, false);
     }
 
     public boolean isDownloadDTDs() {
         return getPropertyAsBoolean(DOWNLOAD_DTDS, false);
     }
 
 }
diff --git a/src/components/org/apache/jmeter/assertions/gui/HTMLAssertionGui.java b/src/components/org/apache/jmeter/assertions/gui/HTMLAssertionGui.java
index 3ea7962cf..b35b496eb 100644
--- a/src/components/org/apache/jmeter/assertions/gui/HTMLAssertionGui.java
+++ b/src/components/org/apache/jmeter/assertions/gui/HTMLAssertionGui.java
@@ -1,317 +1,317 @@
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
 
 package org.apache.jmeter.assertions.gui;
 
 import java.awt.BorderLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.ButtonGroup;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JTextField;
 
 import org.apache.jmeter.assertions.HTMLAssertion;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * GUI for HTMLAssertion
  */
 public class HTMLAssertionGui extends AbstractAssertionGui implements KeyListener, ActionListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTMLAssertionGui.class);
 
-    private static final long serialVersionUID = 1L;
+    private static final long serialVersionUID = 2L;
 
     // Names for the fields
     private static final String WARNING_THRESHOLD_FIELD = "warningThresholdField"; // $NON-NLS-1$
 
     private static final String ERROR_THRESHOLD_FIELD = "errorThresholdField"; // $NON-NLS-1$
 
     // instance attributes
     private JTextField errorThresholdField = null;
 
     private JTextField warningThresholdField = null;
 
     private JCheckBox errorsOnly = null;
 
     private JComboBox<String> docTypeBox = null;
 
     private JRadioButton htmlRadioButton = null;
 
     private JRadioButton xhtmlRadioButton = null;
 
     private JRadioButton xmlRadioButton = null;
 
     private FilePanel filePanel = null;
 
     /**
      * The constructor.
      */
     public HTMLAssertionGui() {
         init();
     }
 
     /**
      * Returns the label to be shown within the JTree-Component.
      */
     @Override
     public String getLabelResource() {
         return "html_assertion_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
         HTMLAssertion el = new HTMLAssertion();
         modifyTestElement(el);
         return el;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement inElement) {
 
         log.debug("HTMLAssertionGui.modifyTestElement() called");
 
         configureTestElement(inElement);
 
         String errorThresholdString = errorThresholdField.getText();
         long errorThreshold = 0;
 
         try {
             errorThreshold = Long.parseLong(errorThresholdString);
         } catch (NumberFormatException e) {
             errorThreshold = 0;
         }
         ((HTMLAssertion) inElement).setErrorThreshold(errorThreshold);
 
         String warningThresholdString = warningThresholdField.getText();
         long warningThreshold = 0;
         try {
             warningThreshold = Long.parseLong(warningThresholdString);
         } catch (NumberFormatException e) {
             warningThreshold = 0;
         }
         ((HTMLAssertion) inElement).setWarningThreshold(warningThreshold);
 
         String docTypeString = docTypeBox.getSelectedItem().toString();
         ((HTMLAssertion) inElement).setDoctype(docTypeString);
 
         boolean trackErrorsOnly = errorsOnly.isSelected();
         ((HTMLAssertion) inElement).setErrorsOnly(trackErrorsOnly);
 
         if (htmlRadioButton.isSelected()) {
             ((HTMLAssertion) inElement).setHTML();
         } else if (xhtmlRadioButton.isSelected()) {
             ((HTMLAssertion) inElement).setXHTML();
         } else {
             ((HTMLAssertion) inElement).setXML();
         }
         ((HTMLAssertion) inElement).setFilename(filePanel.getFilename());
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      * {@inheritDoc}
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         docTypeBox.setSelectedIndex(0);
         htmlRadioButton.setSelected(true);
         xhtmlRadioButton.setSelected(false);
         xmlRadioButton.setSelected(false);
         errorThresholdField.setText("0"); //$NON-NLS-1$
         warningThresholdField.setText("0"); //$NON-NLS-1$
         filePanel.setFilename(""); //$NON-NLS-1$
         errorsOnly.setSelected(false);
     }
 
     /**
      * Configures the associated test element.
      * {@inheritDoc}
      */
     @Override
     public void configure(TestElement inElement) {
         super.configure(inElement);
         HTMLAssertion lAssertion = (HTMLAssertion) inElement;
         errorThresholdField.setText(String.valueOf(lAssertion.getErrorThreshold()));
         warningThresholdField.setText(String.valueOf(lAssertion.getWarningThreshold()));
         errorsOnly.setSelected(lAssertion.isErrorsOnly());
         docTypeBox.setSelectedItem(lAssertion.getDoctype());
         if (lAssertion.isHTML()) {
             htmlRadioButton.setSelected(true);
         } else if (lAssertion.isXHTML()) {
             xhtmlRadioButton.setSelected(true);
         } else {
             xmlRadioButton.setSelected(true);
         }
         if (lAssertion.isErrorsOnly()) {
             warningThresholdField.setEnabled(false);
             warningThresholdField.setEditable(false);
         }
         else {
             warningThresholdField.setEnabled(true);
             warningThresholdField.setEditable(true);
         }
         filePanel.setFilename(lAssertion.getFilename());
     }
 
     /**
      * Inits the GUI.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
 
         setLayout(new BorderLayout(0, 10));
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new JPanel(new BorderLayout());
 
         // USER_INPUT
         VerticalPanel assertionPanel = new VerticalPanel();
         assertionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tidy Settings"));
 
         // doctype
         HorizontalPanel docTypePanel = new HorizontalPanel();
         docTypeBox = new JComboBox<>(new String[] { "omit", "auto", "strict", "loose" });
         // docTypePanel.add(new
         // JLabel(JMeterUtils.getResString("duration_assertion_label"))); //$NON-NLS-1$
         docTypePanel.add(new JLabel("Doctype:"));
         docTypePanel.add(docTypeBox);
         assertionPanel.add(docTypePanel);
 
         // format (HTML, XHTML, XML)
         VerticalPanel formatPanel = new VerticalPanel();
         formatPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Format"));
         htmlRadioButton = new JRadioButton("HTML", true); //$NON-NLS-1$
         xhtmlRadioButton = new JRadioButton("XHTML", false); //$NON-NLS-1$
         xmlRadioButton = new JRadioButton("XML", false); //$NON-NLS-1$
         ButtonGroup buttonGroup = new ButtonGroup();
         buttonGroup.add(htmlRadioButton);
         buttonGroup.add(xhtmlRadioButton);
         buttonGroup.add(xmlRadioButton);
         formatPanel.add(htmlRadioButton);
         formatPanel.add(xhtmlRadioButton);
         formatPanel.add(xmlRadioButton);
         assertionPanel.add(formatPanel);
 
         // errors only
         errorsOnly = new JCheckBox("Errors only", false);
         errorsOnly.addActionListener(this);
         assertionPanel.add(errorsOnly);
 
         // thresholds
         HorizontalPanel thresholdPanel = new HorizontalPanel();
         thresholdPanel.add(new JLabel("Error threshold:"));
         errorThresholdField = new JTextField("0", 5); // $NON-NLS-1$
         errorThresholdField.setName(ERROR_THRESHOLD_FIELD);
         errorThresholdField.addKeyListener(this);
         thresholdPanel.add(errorThresholdField);
         thresholdPanel.add(new JLabel("Warning threshold:"));
         warningThresholdField = new JTextField("0", 5); // $NON-NLS-1$
         warningThresholdField.setName(WARNING_THRESHOLD_FIELD);
         warningThresholdField.addKeyListener(this);
         thresholdPanel.add(warningThresholdField);
         assertionPanel.add(thresholdPanel);
 
         // file panel
         filePanel = new FilePanel(JMeterUtils.getResString("html_assertion_file"), ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
         assertionPanel.add(filePanel);
 
         mainPanel.add(assertionPanel, BorderLayout.NORTH);
         add(mainPanel, BorderLayout.CENTER);
     }
 
     /**
      * This method is called from errors-only checkbox
      *
      * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         if (errorsOnly.isSelected()) {
             warningThresholdField.setEnabled(false);
             warningThresholdField.setEditable(false);
         } else {
             warningThresholdField.setEnabled(true);
             warningThresholdField.setEditable(true);
         }
     }
 
     @Override
     public void keyPressed(KeyEvent e) {
         // NOOP
     }
 
     @Override
     public void keyReleased(KeyEvent e) {
         String fieldName = e.getComponent().getName();
 
         if (fieldName.equals(WARNING_THRESHOLD_FIELD)) {
             validateInteger(warningThresholdField);
         }
 
         if (fieldName.equals(ERROR_THRESHOLD_FIELD)) {
             validateInteger(errorThresholdField);
         }
     }
 
     private void validateInteger(JTextField field){
         try {
             Integer.parseInt(field.getText());
         } catch (NumberFormatException nfe) {
             int length = field.getText().length();
             if (length > 0) {
                 JOptionPane.showMessageDialog(this, "Only digits allowed", "Invalid data",
                         JOptionPane.WARNING_MESSAGE);
                 // Drop the last character:
                 field.setText(field.getText().substring(0, length-1));
             }
         }
 
     }
     @Override
     public void keyTyped(KeyEvent e) {
         // NOOP
     }
 
 }
diff --git a/src/components/org/apache/jmeter/assertions/gui/XMLSchemaAssertionGUI.java b/src/components/org/apache/jmeter/assertions/gui/XMLSchemaAssertionGUI.java
index b1f8aa930..afccc8236 100644
--- a/src/components/org/apache/jmeter/assertions/gui/XMLSchemaAssertionGUI.java
+++ b/src/components/org/apache/jmeter/assertions/gui/XMLSchemaAssertionGUI.java
@@ -1,137 +1,139 @@
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
 
 package org.apache.jmeter.assertions.gui;
 
 
 import java.awt.BorderLayout;
+
 import javax.swing.BorderFactory;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
+
 import org.apache.jmeter.assertions.XMLSchemaAssertion;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * XMLSchemaAssertionGUI.java
  *
  */
 public class XMLSchemaAssertionGUI extends AbstractAssertionGui {
     // class attributes
-     private static final Logger log = LoggingManager.getLoggerForClass();
+     private static final Logger log = LoggerFactory.getLogger(XMLSchemaAssertionGUI.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private JTextField xmlSchema;
 
     /**
      * The constructor.
      */
     public XMLSchemaAssertionGUI() {
         init();
     }
 
     /**
      * Returns the label to be shown within the JTree-Component.
      */
     @Override
     public String getLabelResource() {
         return "xmlschema_assertion_title"; //$NON-NLS-1$
     }
 
     /**
      * create Test Element
      */
     @Override
     public TestElement createTestElement() {
         log.debug("XMLSchemaAssertionGui.createTestElement() called");
         XMLSchemaAssertion el = new XMLSchemaAssertion();
         modifyTestElement(el);
         return el;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement inElement) {
 
         log.debug("XMLSchemaAssertionGui.modifyTestElement() called");
         configureTestElement(inElement);
         ((XMLSchemaAssertion) inElement).setXsdFileName(xmlSchema.getText());
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         xmlSchema.setText(""); //$NON-NLS-1$
     }
 
     /**
      * Configures the GUI from the associated test element.
      *
      * @param el -
      *            the test element (should be XMLSchemaAssertion)
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         XMLSchemaAssertion assertion = (XMLSchemaAssertion) el;
         xmlSchema.setText(assertion.getXsdFileName());
     }
 
     /**
      * Inits the GUI.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout(0, 10));
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new JPanel(new BorderLayout());
 
         // USER_INPUT
         VerticalPanel assertionPanel = new VerticalPanel();
         assertionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "XML Schema"));
 
         // doctype
         HorizontalPanel xmlSchemaPanel = new HorizontalPanel();
 
         xmlSchemaPanel.add(new JLabel(JMeterUtils.getResString("xmlschema_assertion_label"))); //$NON-NLS-1$
 
         xmlSchema = new JTextField(26);
         xmlSchemaPanel.add(xmlSchema);
 
         assertionPanel.add(xmlSchemaPanel);
 
         mainPanel.add(assertionPanel, BorderLayout.NORTH);
         add(mainPanel, BorderLayout.CENTER);
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java b/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java
index b6acf98ab..bd126c6e5 100644
--- a/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java
+++ b/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java
@@ -1,214 +1,214 @@
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
 
 package org.apache.jmeter.assertions.gui;
 
 import java.awt.BorderLayout;
 
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.XPathUtil;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 
 /**
  * Gui component for representing a xpath expression
  *
  */
 public class XPathPanel extends JPanel {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathPanel.class);
 
     private JCheckBox negated;
 
     private JSyntaxTextArea xpath;
 
     private JButton checkXPath;
 
     /**
      * 
      */
     public XPathPanel() {
         super();
         init();
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
 
         Box hbox = Box.createHorizontalBox();
 
         hbox.add(Box.createHorizontalGlue());
         hbox.add(getNegatedCheckBox());
         hbox.add(Box.createHorizontalGlue());
         hbox.add(getCheckXPathButton());
         hbox.add(Box.createHorizontalGlue());
 
         add(JTextScrollPane.getInstance(getXPathField()), BorderLayout.CENTER);
         add(hbox, BorderLayout.SOUTH);
 
         setDefaultValues();
     }
 
     /**
      * Set default values on this component
      */
     public void setDefaultValues() {
         setXPath("/"); //$NON-NLS-1$
         setNegated(false);
     }
 
     /**
      * Get the XPath String
      * 
      * @return String
      */
     public String getXPath() {
         return this.xpath.getText();
     }
 
     /**
      * Set the string that will be used in the xpath evaluation
      * 
      * @param xpath The string representing the xpath expression
      */
     public void setXPath(String xpath) {
         this.xpath.setInitialText(xpath);
     }
 
     /**
      * Does this negate the xpath results
      * 
      * @return boolean
      */
     public boolean isNegated() {
         return this.negated.isSelected();
     }
 
     /**
      * Set this to true, if you want success when the xpath does not match.
      * 
      * @param negated Flag whether xpath match should be negated
      */
     public void setNegated(boolean negated) {
         this.negated.setSelected(negated);
     }
 
     /**
      * Negated chechbox
      * 
      * @return JCheckBox
      */
     public JCheckBox getNegatedCheckBox() {
         if (negated == null) {
             negated = new JCheckBox(JMeterUtils.getResString("xpath_assertion_negate"), false); //$NON-NLS-1$
         }
 
         return negated;
     }
 
     /**
      * Check XPath button
      * 
      * @return JButton
      */
     public JButton getCheckXPathButton() {
         if (checkXPath == null) {
             checkXPath = new JButton(JMeterUtils.getResString("xpath_assertion_button")); //$NON-NLS-1$
             checkXPath.addActionListener(e -> validXPath(xpath.getText(), true));
         }
         return checkXPath;
     }
 
     /**
      * Returns the current {@link JSyntaxTextArea} for the xpath expression, or
      * creates a new one, if none is found.
      * 
      * @return {@link JSyntaxTextArea} for the xpath expression
      */
     public JSyntaxTextArea getXPathField() {
         if (xpath == null) {
             xpath = JSyntaxTextArea.getInstance(20, 80);
             xpath.setLanguage("xpath"); //$NON-NLS-1$
         }
         return xpath;
     }
 
     /**
      * @return Returns the showNegate.
      */
     public boolean isShowNegated() {
         return this.getNegatedCheckBox().isVisible();
     }
 
     /**
      * @param showNegate
      *            The showNegate to set.
      */
     public void setShowNegated(boolean showNegate) {
         getNegatedCheckBox().setVisible(showNegate);
     }
 
     /**
      * Test whether an XPath is valid. It seems the Xalan has no easy way to
      * check, so this creates a dummy test document, then tries to evaluate the xpath against it.
      * 
      * @param xpathString
      *            XPath String to validate
      * @param showDialog
      *            weather to show a dialog
      * @return returns true if valid, valse otherwise.
      */
     public static boolean validXPath(String xpathString, boolean showDialog) {
         String ret = null;
         boolean success = true;
         Document testDoc = null;
         try {
             testDoc = XPathUtil.makeDocumentBuilder(false, false, false, false).newDocument();
             Element el = testDoc.createElement("root"); //$NON-NLS-1$
             testDoc.appendChild(el);
             XPathUtil.validateXPath(testDoc, xpathString);
         } catch (IllegalArgumentException | ParserConfigurationException | TransformerException e) {
             log.warn(e.getLocalizedMessage(), e);
             success = false;
             ret = e.getLocalizedMessage();
         }
         if (showDialog) {
             JOptionPane.showMessageDialog(null, 
                     success ? JMeterUtils.getResString("xpath_assertion_valid") : ret, //$NON-NLS-1$
                     success ? JMeterUtils.getResString("xpath_assertion_valid") : //$NON-NLS-1$
                         JMeterUtils.getResString("xpath_assertion_failed"), //$NON-NLS-1$
                         success ? JOptionPane.INFORMATION_MESSAGE //$NON-NLS-1$
                                 : JOptionPane.ERROR_MESSAGE);
         }
         return success;
 
     }
 }
