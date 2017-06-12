diff --git a/src/components/org/apache/jmeter/assertions/BSFAssertion.java b/src/components/org/apache/jmeter/assertions/BSFAssertion.java
index 8dc44e8ef..0145a4a8a 100644
--- a/src/components/org/apache/jmeter/assertions/BSFAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/BSFAssertion.java
@@ -1,62 +1,64 @@
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
-            log.warn("Problem in BSF script {}",e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BSF script {}", e.toString());
+            }
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
index 8a60fa3a9..9d092c91f 100644
--- a/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
@@ -1,126 +1,128 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * An Assertion which understands BeanShell
  *
  */
 public class BeanShellAssertion extends BeanShellTestElement implements Assertion {
     private static final Logger log = LoggerFactory.getLogger(BeanShellAssertion.class);
 
     private static final long serialVersionUID = 4;
 
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
-            log.warn(ex.toString());
+            if (log.isWarnEnabled()) {
+                log.warn(ex.toString());
+            }
         }
 
         return result;
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
index c097096ab..f05ab37ea 100644
--- a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
@@ -1,201 +1,202 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
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
 
     private static final long serialVersionUID = 234L;
 
     public static final String FILE_NAME_IS_REQUIRED = "FileName is required";
 
     public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
 
     public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
 
     public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
 
     private static final Logger log = LoggerFactory.getLogger(XMLSchemaAssertion.class);
 
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
         log.debug("xmlString: {}, xsdFileName: {}", resultData, xsdFileName);
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
-
-            log.warn(e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn(e.toString());
+            }
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
diff --git a/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java b/src/components/org/apache/jmeter/assertions/gui/XPathPanel.java
index bd126c6e5..fa1031d4e 100644
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 
 /**
  * Gui component for representing a xpath expression
  *
  */
 public class XPathPanel extends JPanel {
     private static final long serialVersionUID = 241L;
 
     private static final Logger log = LoggerFactory.getLogger(XPathPanel.class);
 
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
-            log.warn(e.getLocalizedMessage(), e);
+            log.warn("Exception while validating XPath.", e);
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
diff --git a/src/components/org/apache/jmeter/config/RandomVariableConfig.java b/src/components/org/apache/jmeter/config/RandomVariableConfig.java
index 9573a8e2b..b6ebf3e50 100644
--- a/src/components/org/apache/jmeter/config/RandomVariableConfig.java
+++ b/src/components/org/apache/jmeter/config/RandomVariableConfig.java
@@ -1,244 +1,244 @@
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
 
 package org.apache.jmeter.config;
 
 import java.text.DecimalFormat;
 import java.util.Random;
 
 import org.apache.commons.lang3.math.NumberUtils;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.NoConfigMerge;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class RandomVariableConfig extends ConfigTestElement
     implements TestBean, LoopIterationListener, NoThreadClone, NoConfigMerge
 {
     private static final Logger log = LoggerFactory.getLogger(RandomVariableConfig.class);
 
     private static final long serialVersionUID = 234L;
 
     /*
      *  N.B. this class is shared between threads (NoThreadClone) so all access to variables
      *  needs to be protected by a lock (either sync. or volatile) to ensure safe publication.
      */
 
     private String minimumValue;
 
     private String maximumValue;
 
     private String variableName;
 
     private String outputFormat;
 
     private String randomSeed;
 
     private boolean perThread;
 
     private int range;
     
     private long minimum;
 
     // This class is not cloned per thread, so this is shared
     private Random globalRandom = null;
 
     // Used for per-thread/user numbers
     // Cannot be static, as random numbers are not to be shared between instances
     private transient ThreadLocal<Random> perThreadRandom = initThreadLocal();
 
     private ThreadLocal<Random> initThreadLocal() {
         return new ThreadLocal<Random>() {
                 @Override
                 protected Random initialValue() {
                     init();
                     return new Random(getRandomSeedAsLong());
                 }};
     }
 
     private Object readResolve(){
         perThreadRandom = initThreadLocal();
         return this;
     }
 
     /*
      * nextInt(n) returns values in the range [0,n),
      * so n must be set to max-min+1
      */
     private void init(){
         final String minAsString = getMinimumValue();
         minimum = NumberUtils.toLong(minAsString);
         final String maxAsString = getMaximumValue();
         long maximum = NumberUtils.toLong(maxAsString);
         long rangeL=maximum-minimum+1; // This can overflow
         if (minimum >= maximum){
             log.error("maximum({}) must be > minimum({})", maxAsString, minAsString);
             range=0;// This is used as an error indicator
             return;
         }
         if (rangeL > Integer.MAX_VALUE || rangeL <= 0){// check for overflow too
             log.warn("maximum({}) - minimum({}) must be <= {}", maxAsString, minAsString, Integer.MAX_VALUE);
             rangeL=Integer.MAX_VALUE;
         }
         range = (int)rangeL;
     }
 
     /** {@inheritDoc} */
     @Override
     public void iterationStart(LoopIterationEvent iterEvent) {
         Random randGen;
         if (getPerThread()){
             randGen = perThreadRandom.get();
         } else {
             synchronized(this){
                 if (globalRandom == null){
                     init();
                     globalRandom = new Random(getRandomSeedAsLong());
                 }
                 randGen=globalRandom;
             }
         }
         if (range <=0){
             return;
         }
        long nextRand = minimum + randGen.nextInt(range);
        // Cannot use getThreadContext() as we are not cloned per thread
        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        variables.put(getVariableName(), formatNumber(nextRand));
     }
 
     // Use format to create number; if it fails, use the default
     private String formatNumber(long value){
         String format = getOutputFormat();
         if (format != null && format.length() > 0) {
             try {
                 DecimalFormat myFormatter = new DecimalFormat(format);
                 return myFormatter.format(value);
             } catch (IllegalArgumentException ignored) {
                 log.warn("Exception formatting value: {} at format: {}, using default", value, format);
             }
         }
         return Long.toString(value);
     }
 
     /**
      * @return the minValue
      */
     public synchronized String getMinimumValue() {
         return minimumValue;
     }
 
     /**
      * @param minValue the minValue to set
      */
     public synchronized void setMinimumValue(String minValue) {
         this.minimumValue = minValue;
     }
 
     /**
      * @return the maxvalue
      */
     public synchronized String getMaximumValue() {
         return maximumValue;
     }
 
     /**
      * @param maxvalue the maxvalue to set
      */
     public synchronized void setMaximumValue(String maxvalue) {
         this.maximumValue = maxvalue;
     }
 
     /**
      * @return the variableName
      */
     public synchronized String getVariableName() {
         return variableName;
     }
 
     /**
      * @param variableName the variableName to set
      */
     public synchronized void setVariableName(String variableName) {
         this.variableName = variableName;
     }
 
     /**
      * @return the randomSeed
      */
     public synchronized String getRandomSeed() {
         return randomSeed;
     }
 
     /**
      * @return the randomSeed as a long
      */
     private synchronized long getRandomSeedAsLong() {
         long seed;
         if (randomSeed.length()==0){
             seed = System.currentTimeMillis();
         }  else {
             try {
                 seed = Long.parseLong(randomSeed);
             } catch (NumberFormatException e) {
                 seed = System.currentTimeMillis();
-                log.warn("Cannot parse seed: {}. {}", randomSeed, e.getLocalizedMessage());
+                log.warn("Cannot parse random seed: '{}'", randomSeed);
             }
         }
         return seed;
     }
 
     /**
      * @param randomSeed the randomSeed to set
      */
     public synchronized void setRandomSeed(String randomSeed) {
         this.randomSeed = randomSeed;
     }
 
     /**
      * @return the perThread
      */
     public synchronized boolean getPerThread() {
         return perThread;
     }
 
     /**
      * @param perThread the perThread to set
      */
     public synchronized void setPerThread(boolean perThread) {
         this.perThread = perThread;
     }
     /**
      * @return the outputFormat
      */
     public synchronized String getOutputFormat() {
         return outputFormat;
     }
     /**
      * @param outputFormat the outputFormat to set
      */
     public synchronized void setOutputFormat(String outputFormat) {
         this.outputFormat = outputFormat;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/control/CriticalSectionController.java b/src/components/org/apache/jmeter/control/CriticalSectionController.java
index 8aa62bbef..afb57b4f8 100644
--- a/src/components/org/apache/jmeter/control/CriticalSectionController.java
+++ b/src/components/org/apache/jmeter/control/CriticalSectionController.java
@@ -1,198 +1,202 @@
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
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * This is a Critical Section Controller; it will execute the set of statements
  * (samplers/controllers, etc) under named lock.
  * <p>
  * In a programming world - this is equivalent of :
  * 
  * <pre>
  * try {
  *          named_lock.lock();
  *          statements ....
  * } finally {
  *          named_lock.unlock();
  * }
  * </pre>
  * 
  * In JMeter you may have :
  * 
  * <pre>
  * Thread-Group (set to loop a number of times or indefinitely,
  *    ... Samplers ... (e.g. Counter )
  *    ... Other Controllers ....
  *    ... CriticalSectionController ( lock name like "foobar" )
  *       ... statements to perform when lock acquired
  *       ...
  *    ... Other Controllers /Samplers }
  * </pre>
  * 
  * @since 2.12
  */
 public class CriticalSectionController extends GenericController implements
         ThreadListener, TestStateListener {
 
     private static final long serialVersionUID = 1L;
 
     private static final Logger log = LoggerFactory.getLogger(CriticalSectionController.class);
 
     private static final String LOCK_NAME = "CriticalSectionController.lockName"; //$NON-NLS-1$
 
     private static final ConcurrentHashMap<String, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();
 
     private transient volatile ReentrantLock currentLock;
 
     /**
      * constructor
      */
     public CriticalSectionController() {
         super();
     }
 
     /**
      * constructor
      * @param name The name of this controller
      */
     public CriticalSectionController(String name) {
         super();
         this.setName(name);
     }
 
     /**
      * Condition Accessor - this is gonna be any string value
      * @param name The name of the lock for this controller
      */
     public void setLockName(String name) {
         setProperty(new StringProperty(LOCK_NAME, name));
     }
 
     /**
      * If lock exists returns it, otherwise creates one, puts it in LOCK_MAP 
      * then returns it
      * 
      * @return {@link ReentrantLock}
      */
     private ReentrantLock getOrCreateLock() {
         String lockName = getLockName();
         ReentrantLock lock = LOCK_MAP.get(lockName);
         ReentrantLock prev;
         if (lock != null) {
             return lock;
         }
         lock = new ReentrantLock();
         prev = LOCK_MAP.putIfAbsent(lockName, lock);
         return prev == null ? lock : prev;
     }
 
     /**
      * @return String lock name
      */
     public String getLockName() {
         return getPropertyAsString(LOCK_NAME);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next() {
         if (StringUtils.isEmpty(getLockName())) {
-            log.warn("Empty lock name in Critical Section Controller: {}", getName());
+            if (log.isWarnEnabled()) {
+                log.warn("Empty lock name in Critical Section Controller: {}", getName());
+            }
             return super.next();
         }
         if (isFirst()) {
             // Take the lock for first child element
             long startTime = System.currentTimeMillis();
             if (this.currentLock == null) {
                 this.currentLock = getOrCreateLock();
             }
             this.currentLock.lock();
             long endTime = System.currentTimeMillis();
             if (log.isDebugEnabled()) {
                 log.debug("Thread ('{}') acquired lock: '{}' in Critical Section Controller {}  in: {} ms",
                         Thread.currentThread(), getLockName(), getName(), endTime - startTime);
             }
         }
         return super.next();
     }
 
     /**
      * Called after execution of last child of the controller We release lock
      * 
      * @see org.apache.jmeter.control.GenericController#reInitialize()
      */
     @Override
     protected void reInitialize() {
         if (this.currentLock != null) {
             if (currentLock.isHeldByCurrentThread()) {
                 this.currentLock.unlock();
             }
             this.currentLock = null;
         }
         super.reInitialize();
     }
 
     @Override
     public void threadStarted() {
         this.currentLock = null;
     }
 
     @Override
     public void threadFinished() {
         if (this.currentLock != null
                 && this.currentLock.isHeldByCurrentThread()) {
-            log.warn("Lock '{}' not released in: {}, releasing in threadFinished", getLockName(), getName());
+            if (log.isWarnEnabled()) {
+                log.warn("Lock '{}' not released in: {}, releasing in threadFinished", getLockName(), getName());
+            }
             this.currentLock.unlock();
         }
         this.currentLock = null;
     }
 
     @Override
     public void testStarted() {
         // NOOP
     }
 
     @Override
     public void testStarted(String host) {
         // NOOP
     }
 
     @Override
     public void testEnded() {
         LOCK_MAP.clear();
     }
 
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 }
diff --git a/src/components/org/apache/jmeter/control/IncludeController.java b/src/components/org/apache/jmeter/control/IncludeController.java
index ee7e37723..72371d19e 100644
--- a/src/components/org/apache/jmeter/control/IncludeController.java
+++ b/src/components/org/apache/jmeter/control/IncludeController.java
@@ -1,205 +1,207 @@
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
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.LinkedList;
 
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class IncludeController extends GenericController implements ReplaceableController {
     private static final Logger log = LoggerFactory.getLogger(IncludeController.class);
 
     private static final long serialVersionUID = 241L;
 
     private static final String INCLUDE_PATH = "IncludeController.includepath"; //$NON-NLS-1$
 
     private static  final String PREFIX =
         JMeterUtils.getPropDefault(
                 "includecontroller.prefix", //$NON-NLS-1$
                 ""); //$NON-NLS-1$
 
     private HashTree subtree = null;
     private TestElement sub = null;
 
     /**
      * No-arg constructor
      *
      * @see java.lang.Object#Object()
      */
     public IncludeController() {
         super();
     }
 
     @Override
     public Object clone() {
         // TODO - fix so that this is only called once per test, instead of at every clone
         // Perhaps save previous filename, and only load if it has changed?
         this.resolveReplacementSubTree(null);
         IncludeController clone = (IncludeController) super.clone();
         clone.setIncludePath(this.getIncludePath());
         if (this.subtree != null) {
             if (this.subtree.size() == 1) {
                 for (Object o : this.subtree.keySet()) {
                     this.sub = (TestElement) o;
                 }
             }
             clone.subtree = (HashTree)this.subtree.clone();
             clone.sub = this.sub==null ? null : (TestElement) this.sub.clone();
         }
         return clone;
     }
 
     /**
      * In the event an user wants to include an external JMX test plan
      * the GUI would call this.
      * @param jmxfile The path to the JMX test plan to include
      */
     public void setIncludePath(String jmxfile) {
         this.setProperty(INCLUDE_PATH,jmxfile);
     }
 
     /**
      * return the JMX file path.
      * @return the JMX file path
      */
     public String getIncludePath() {
         return this.getPropertyAsString(INCLUDE_PATH);
     }
 
     /**
      * The way ReplaceableController works is clone is called first,
      * followed by replace(HashTree) and finally getReplacement().
      */
     @Override
     public HashTree getReplacementSubTree() {
         return subtree;
     }
 
     public TestElement getReplacementElement() {
         return sub;
     }
 
     @Override
     public void resolveReplacementSubTree(JMeterTreeNode context) {
         this.subtree = this.loadIncludedElements();
     }
 
     /**
      * load the included elements using SaveService
      *
      * @return tree with loaded elements
      */
     protected HashTree loadIncludedElements() {
         // only try to load the JMX test plan if there is one
         final String includePath = getIncludePath();
         HashTree tree = null;
         if (includePath != null && includePath.length() > 0) {
             String fileName=PREFIX+includePath;
             try {
                 File file = new File(fileName.trim());
                 final String absolutePath = file.getAbsolutePath();
                 log.info("loadIncludedElements -- try to load included module: {}", absolutePath);
                 if(!file.exists() && !file.isAbsolute()){
                     log.info("loadIncludedElements -failed for: {}", absolutePath);
                     file = new File(FileServer.getFileServer().getBaseDir(), includePath);
-                    log.info("loadIncludedElements -Attempting to read it from: {}", file.getAbsolutePath());
+                    if (log.isInfoEnabled()) {
+                        log.info("loadIncludedElements -Attempting to read it from: {}", file.getAbsolutePath());
+                    }
                     if(!file.canRead() || !file.isFile()){
                         log.error("Include Controller '{}' can't load '{}' - see log for details", this.getName(),
                                 fileName);
                         throw new IOException("loadIncludedElements -failed for: " + absolutePath +
                                 " and " + file.getAbsolutePath());
                     }
                 }
                 
                 tree = SaveService.loadTree(file);
                 // filter the tree for a TestFragment.
                 tree = getProperBranch(tree);
                 removeDisabledItems(tree);
                 return tree;
             } catch (NoClassDefFoundError ex) // Allow for missing optional jars
             {
                 String msg = "Including file \""+ fileName 
                             + "\" failed for Include Controller \""+ this.getName()
                             +"\", missing jar file";
                 log.warn(msg, ex);
                 JMeterUtils.reportErrorToUser(msg+" - see log for details");
             } catch (FileNotFoundException ex) {
                 String msg = "File \""+ fileName 
                         + "\" not found for Include Controller \""+ this.getName()+"\"";
                 JMeterUtils.reportErrorToUser(msg+" - see log for details");
                 log.warn(msg, ex);
             } catch (Exception ex) {
                 String msg = "Including file \"" + fileName 
                             + "\" failed for Include Controller \"" + this.getName()
                             +"\", unexpected error";
                 JMeterUtils.reportErrorToUser(msg+" - see log for details");
                 log.warn(msg, ex);
             }
         }
         return tree;
     }
 
     /**
      * Extract from tree (included test plan) all Test Elements located in a Test Fragment
      * @param tree HashTree included Test Plan
      * @return HashTree Subset within Test Fragment or Empty HashTree
      */
     private HashTree getProperBranch(HashTree tree) {
         for (Object o : new LinkedList<>(tree.list())) {
             TestElement item = (TestElement) o;
 
             //if we found a TestPlan, then we are on our way to the TestFragment
             if (item instanceof TestPlan)
             {
                 return getProperBranch(tree.getTree(item));
             }
 
             if (item instanceof TestFragmentController)
             {
                 return tree.getTree(item);
             }
         }
         log.warn("No Test Fragment was found in included Test Plan, returning empty HashTree");
         return new HashTree();
     }
 
 
     private void removeDisabledItems(HashTree tree) {
         for (Object o : new LinkedList<>(tree.list())) {
             TestElement item = (TestElement) o;
             if (!item.isEnabled()) {
                 tree.remove(item);
             } else {
                 removeDisabledItems(tree.getTree(item));// Recursive call
             }
         }
     }
 
 }
diff --git a/src/components/org/apache/jmeter/control/ThroughputController.java b/src/components/org/apache/jmeter/control/ThroughputController.java
index a4d7400dd..3ee56aaa6 100644
--- a/src/components/org/apache/jmeter/control/ThroughputController.java
+++ b/src/components/org/apache/jmeter/control/ThroughputController.java
@@ -1,284 +1,286 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
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
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggerFactory.getLogger(ThroughputController.class);
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
 
     private transient Object counterLock = new Object(); // ensure counts are updated correctly
 
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
+            String valueString = prop.getStringValue();
             try {
-                retVal = Integer.parseInt(prop.getStringValue());
+                retVal = Integer.parseInt(valueString);
             } catch (NumberFormatException e) {
-                log.warn("Error parsing {}", prop.getStringValue(), e);
+                log.warn("Error parsing '{}'", valueString, e);
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
+            String valueString = prop.getStringValue();
             try {
-                retVal = Float.parseFloat(prop.getStringValue());
+                retVal = Float.parseFloat(valueString);
             } catch (NumberFormatException e) {
-                log.warn("Error parsing {}", prop.getStringValue(),e);
+                log.warn("Error parsing '{}'", valueString, e);
             }
         }
         return retVal;
     }
 
     @SuppressWarnings("SynchronizeOnNonFinalField")
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
         if (subControllersAndSamplers.isEmpty()) {
             return true;
         } else {
             return getStyle() == BYNUMBER && getExecutions() >= getMaxThroughputAsInt()
                 && current >= getSubControllers().size();
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
     @SuppressWarnings("SynchronizeOnNonFinalField")
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
     @SuppressWarnings("SynchronizeOnNonFinalField")
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
 
     @Override
     protected Object readResolve(){
         super.readResolve();
         counterLock = new Object();
         return this;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java b/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java
index e9efda78c..63ba0262a 100644
--- a/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java
@@ -1,54 +1,56 @@
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
 
 package org.apache.jmeter.extractor;
 
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BSFTestElement;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BSFPostProcessor extends BSFTestElement implements Cloneable, PostProcessor, TestBean
 {
     private static final Logger log = LoggerFactory.getLogger(BSFPostProcessor.class);
 
     private static final long serialVersionUID = 233L;
 
     @Override
     public void process(){
         BSFManager mgr =null;
         try {
             mgr = getManager();
             processFileOrScript(mgr);
         } catch (BSFException e) {
-            log.warn("Problem in BSF script {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BSF script: {}", e.toString());
+            }
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
diff --git a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
index d2e78774e..a4d73293c 100644
--- a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
@@ -1,74 +1,76 @@
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
 
 package org.apache.jmeter.extractor;
 
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
 import org.apache.jorphan.util.JMeterException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BeanShellPostProcessor extends BeanShellTestElement
     implements Cloneable, PostProcessor, TestBean
 {
     private static final Logger log = LoggerFactory.getLogger(BeanShellPostProcessor.class);
 
     private static final long serialVersionUID = 5;
     
     // can be specified in jmeter.properties
     private static final String INIT_FILE = "beanshell.postprocessor.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
     @Override
     public void process() {
         JMeterContext jmctx = JMeterContextService.getContext();
 
         SampleResult prev = jmctx.getPreviousResult();
         if (prev == null) {
             return; // TODO - should we skip processing here?
         }
         final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
         if (bshInterpreter == null) {
             log.error("BeanShell not found");
             return;
         }
 
         try {
             // Add variables for access to context and variables
             bshInterpreter.set("data", prev.getResponseData());//$NON-NLS-1$
             processFileOrScript(bshInterpreter);
         } catch (JMeterException e) {
-            log.warn("Problem in BeanShell script: {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BeanShell script: {}", e.toString());
+            }
         }
     }
      
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
index 36466fd36..099416943 100644
--- a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
@@ -1,332 +1,338 @@
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
 
 package org.apache.jmeter.extractor;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * 
  */
 public class HtmlExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
     private static final long serialVersionUID = 1L;
 
     public static final String EXTRACTOR_JSOUP = "JSOUP"; //$NON-NLS-1$
 
     public static final String EXTRACTOR_JODD = "JODD"; //$NON-NLS-1$
 
     public static final String DEFAULT_EXTRACTOR = ""; // $NON-NLS-1$
 
     private static final Logger log = LoggerFactory.getLogger(HtmlExtractor.class);
 
     private static final String EXPRESSION = "HtmlExtractor.expr"; // $NON-NLS-1$
 
     private static final String ATTRIBUTE = "HtmlExtractor.attribute"; // $NON-NLS-1$
 
     private static final String REFNAME = "HtmlExtractor.refname"; // $NON-NLS-1$
 
     private static final String MATCH_NUMBER = "HtmlExtractor.match_number"; // $NON-NLS-1$
 
     private static final String DEFAULT = "HtmlExtractor.default"; // $NON-NLS-1$
 
     private static final String EXTRACTOR_IMPL = "HtmlExtractor.extractor_impl"; // $NON-NLS-1$
 
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
     
     private static final String UNDERSCORE = "_";  // $NON-NLS-1$
     
     private static final String DEFAULT_EMPTY_VALUE = "HtmlExtractor.default_empty_value"; // $NON-NLS-1$
 
     private Extractor extractor;
     
     /**
      * Get the possible extractor implementations
      * @return Array containing the names of the possible extractors.
      */
     public static String[] getImplementations(){
         return new String[]{EXTRACTOR_JSOUP,EXTRACTOR_JODD};
     }
 
 
     /**
      * Parses the response data using CSS/JQuery expressions and saving the results
      * into variables for use later in the test.
      *
      * @see org.apache.jmeter.processor.PostProcessor#process()
      */
     @Override
     public void process() {
         JMeterContext context = getThreadContext();
         SampleResult previousResult = context.getPreviousResult();
         if (previousResult == null) {
             return;
         }
         if(log.isDebugEnabled()) {
             log.debug("HtmlExtractor {}: processing result", getName());
         }
         // Fetch some variables
         JMeterVariables vars = context.getVariables();
         
         String refName = getRefName();
         String expression = getExpression();
         String attribute = getAttribute();
         int matchNumber = getMatchNumber();
         final String defaultValue = getDefaultValue();
         
         if (defaultValue.length() > 0  || isEmptyDefaultValue()){// Only replace default if it is provided or empty default value is explicitly requested
             vars.put(refName, defaultValue);
         }
         
         try {            
             List<String> matches = 
                     extractMatchingStrings(vars, expression, attribute, matchNumber, previousResult);
             int prevCount = 0;
             String prevString = vars.get(refName + REF_MATCH_NR);
             if (prevString != null) {
                 vars.remove(refName + REF_MATCH_NR);// ensure old value is not left defined
                 try {
                     prevCount = Integer.parseInt(prevString);
                 } catch (NumberFormatException nfe) {
-                    log.warn("{}: Could not parse number '{}'.", getName(), prevString);
+                    if (log.isWarnEnabled()) {
+                        log.warn("{}: Could not parse number: '{}'.", getName(), prevString);
+                    }
                 }
             }
             int matchCount=0;// Number of refName_n variable sets to keep
             String match;
             if (matchNumber >= 0) {// Original match behaviour
                 match = getCorrectMatch(matches, matchNumber);
                 if (match != null) {
                     vars.put(refName, match);
                 } 
             } else // < 0 means we save all the matches
             {
                 matchCount = matches.size();
                 vars.put(refName + REF_MATCH_NR, Integer.toString(matchCount));// Save the count
                 for (int i = 1; i <= matchCount; i++) {
                     match = getCorrectMatch(matches, i);
                     if (match != null) {
                         final String refNameN = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                         vars.put(refNameN, match);
                     }
                 }
             }
             // Remove any left-over variables
             for (int i = matchCount + 1; i <= prevCount; i++) {
                 final String refNameN = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                 vars.remove(refNameN);
             }
         } catch (RuntimeException e) {
-            log.warn("{}: Error while generating result. {}", getName(), e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("{}: Error while generating result. {}", getName(), e.toString());
+            }
         }
 
     }
 
     /**
      * Grab the appropriate result from the list.
      *
      * @param matches
      *            list of matches
      * @param entry
      *            the entry number in the list
      * @return MatchResult
      */
     private String getCorrectMatch(List<String> matches, int entry) {
         int matchSize = matches.size();
 
         if (matchSize <= 0 || entry > matchSize){
             return null;
         }
 
         if (entry == 0) // Random match
         {
             return matches.get(JMeterUtils.getRandomInt(matchSize));
         }
 
         return matches.get(entry - 1);
     }
 
     private List<String> extractMatchingStrings(JMeterVariables vars,
             String expression, String attribute, int matchNumber,
             SampleResult previousResult) {
         int found = 0;
         List<String> result = new ArrayList<>();
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
             if(!StringUtils.isEmpty(inputString)) {
                 getExtractorImpl().extract(expression, attribute, matchNumber, inputString, result, found, "-1");
             } else {
                 if(inputString==null) {
-                    log.warn("No variable '{}' found to process by CSS/JQuery Extractor '{}', skipping processing",
-                            getVariableName(), getName());
+                    if (log.isWarnEnabled()) {
+                        log.warn("No variable '{}' found to process by CSS/JQuery Extractor '{}', skipping processing",
+                                getVariableName(), getName());
+                    }
                 }
                 return Collections.emptyList();
             } 
         } else {
             List<SampleResult> sampleList = getSampleList(previousResult);
             int i=0;
             for (SampleResult sr : sampleList) {
                 String inputString = sr.getResponseDataAsString();
                 found = getExtractorImpl().extract(expression, attribute, matchNumber, inputString, result, found,
                         i>0 ? null : Integer.toString(i));
                 i++;
                 if (matchNumber > 0 && found == matchNumber){// no need to process further
                     break;
                 }
             }
         }
         return result;
     }
     
     /**
      * @param impl Extractor implementation
      * @return Extractor
      */
     public static Extractor getExtractorImpl(String impl) {
         boolean useDefaultExtractor = DEFAULT_EXTRACTOR.equals(impl);
         if (useDefaultExtractor || EXTRACTOR_JSOUP.equals(impl)) {
             return new JSoupExtractor();
         } else if (EXTRACTOR_JODD.equals(impl)) {
             return new JoddExtractor();
         } else {
             throw new IllegalArgumentException("Extractor implementation:"+ impl+" is unknown");
         }
     }
     
     /**
      * 
      * @return Extractor
      */
     private Extractor getExtractorImpl() {
         if (extractor == null) {
             extractor = getExtractorImpl(getExtractor());
         }
         return extractor;
     }
     
 
     /**
      * Set the extractor. Has to be one of the list that can be obtained by
      * {@link HtmlExtractor#getImplementations()}
      * 
      * @param attribute
      *            The name of the extractor to be used
      */
     public void setExtractor(String attribute) {
         setProperty(EXTRACTOR_IMPL, attribute);
     }
 
     /**
      * Get the name of the currently configured extractor
      * @return The name of the extractor currently used
      */
     public String getExtractor() {
         return getPropertyAsString(EXTRACTOR_IMPL); // $NON-NLS-1$
     }
 
     
     public void setAttribute(String attribute) {
         setProperty(ATTRIBUTE, attribute);
     }
 
     public String getAttribute() {
         return getPropertyAsString(ATTRIBUTE, ""); // $NON-NLS-1$
     }
 
     public void setExpression(String regex) {
         setProperty(EXPRESSION, regex);
     }
 
     public String getExpression() {
         return getPropertyAsString(EXPRESSION);
     }
 
     public void setRefName(String refName) {
         setProperty(REFNAME, refName);
     }
 
     public String getRefName() {
         return getPropertyAsString(REFNAME);
     }
 
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning random.
      *
      * @param matchNumber The number of the match to be used
      */
     public void setMatchNumber(int matchNumber) {
         setProperty(new IntegerProperty(MATCH_NUMBER, matchNumber));
     }
 
     public void setMatchNumber(String matchNumber) {
         setProperty(MATCH_NUMBER, matchNumber);
     }
 
     public int getMatchNumber() {
         return getPropertyAsInt(MATCH_NUMBER);
     }
 
     public String getMatchNumberAsString() {
         return getPropertyAsString(MATCH_NUMBER);
     }
 
     /**
      * Sets the value of the variable if no matches are found
      *
      * @param defaultValue The default value for the variable
      */
     public void setDefaultValue(String defaultValue) {
         setProperty(DEFAULT, defaultValue);
     }
 
     /**
      * @param defaultEmptyValue boolean set value to "" if not found
      */
     public void setDefaultEmptyValue(boolean defaultEmptyValue) {
         setProperty(DEFAULT_EMPTY_VALUE, defaultEmptyValue);
     }
     
     /**
      * Get the default value for the variable if no matches are found
      * @return The default value for the variable
      */
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
     }
     
     /**
      * @return boolean set value to "" if not found
      */
     public boolean isEmptyDefaultValue() {
         return getPropertyAsBoolean(DEFAULT_EMPTY_VALUE);
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index 876641d47..523e772d5 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,520 +1,522 @@
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
 
 package org.apache.jmeter.extractor;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.Document;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcher;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 // @see org.apache.jmeter.extractor.TestRegexExtractor for unit tests
 
 public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
     private static final long serialVersionUID = 242L;
 
     private static final Logger log = LoggerFactory.getLogger(RegexExtractor.class);
 
     // What to match against. N.B. do not change the string value or test plans will break!
     private static final String MATCH_AGAINST = "RegexExtractor.useHeaders"; // $NON-NLS-1$
     /*
      * Permissible values:
      *  true - match against headers
      *  false or absent - match against body (this was the original default)
      *  URL - match against URL
      *  These are passed to the setUseField() method
      *
      *  Do not change these values!
     */
     public static final String USE_HDRS = "true"; // $NON-NLS-1$
     public static final String USE_REQUEST_HDRS = "request_headers"; // $NON-NLS-1$
     public static final String USE_BODY = "false"; // $NON-NLS-1$
     public static final String USE_BODY_UNESCAPED = "unescaped"; // $NON-NLS-1$
     public static final String USE_BODY_AS_DOCUMENT = "as_document"; // $NON-NLS-1$
     public static final String USE_URL = "URL"; // $NON-NLS-1$
     public static final String USE_CODE = "code"; // $NON-NLS-1$
     public static final String USE_MESSAGE = "message"; // $NON-NLS-1$
 
 
     private static final String REGEX = "RegexExtractor.regex"; // $NON-NLS-1$
 
     private static final String REFNAME = "RegexExtractor.refname"; // $NON-NLS-1$
 
     private static final String MATCH_NUMBER = "RegexExtractor.match_number"; // $NON-NLS-1$
 
     private static final String DEFAULT = "RegexExtractor.default"; // $NON-NLS-1$
     
     private static final String DEFAULT_EMPTY_VALUE = "RegexExtractor.default_empty_value"; // $NON-NLS-1$
 
     private static final String TEMPLATE = "RegexExtractor.template"; // $NON-NLS-1$
 
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
 
     private static final String UNDERSCORE = "_";  // $NON-NLS-1$
 
     private static final boolean DEFAULT_VALUE_FOR_DEFAULT_EMPTY_VALUE = false;
 
     private transient List<Object> template;
 
     /**
      * Parses the response data using regular expressions and saving the results
      * into variables for use later in the test.
      *
      * @see org.apache.jmeter.processor.PostProcessor#process()
      */
     @Override
     public void process() {
         initTemplate();
         JMeterContext context = getThreadContext();
         SampleResult previousResult = context.getPreviousResult();
         if (previousResult == null) {
             return;
         }
         log.debug("RegexExtractor processing result");
 
         // Fetch some variables
         JMeterVariables vars = context.getVariables();
         String refName = getRefName();
         int matchNumber = getMatchNumber();
 
         final String defaultValue = getDefaultValue();
         if (defaultValue.length() > 0 || isEmptyDefaultValue()) {// Only replace default if it is provided or empty default value is explicitly requested
             vars.put(refName, defaultValue);
         }
         
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         String regex = getRegex();
         Pattern pattern = null;
         try {
             pattern = JMeterUtils.getPatternCache().getPattern(regex, Perl5Compiler.READ_ONLY_MASK);
             List<MatchResult> matches = processMatches(pattern, regex, previousResult, matchNumber, vars);
             int prevCount = 0;
             String prevString = vars.get(refName + REF_MATCH_NR);
             if (prevString != null) {
                 vars.remove(refName + REF_MATCH_NR);// ensure old value is not left defined
                 try {
                     prevCount = Integer.parseInt(prevString);
                 } catch (NumberFormatException nfe) {
-                    log.warn("Could not parse number: '{}', message: '{}'", prevString, nfe.toString());
+                    log.warn("Could not parse number: '{}'", prevString);
                 }
             }
             int matchCount=0;// Number of refName_n variable sets to keep
             try {
                 MatchResult match;
                 if (matchNumber >= 0) {// Original match behaviour
                     match = getCorrectMatch(matches, matchNumber);
                     if (match != null) {
                         vars.put(refName, generateResult(match));
                         saveGroups(vars, refName, match);
                     } else {
                         // refname has already been set to the default (if present)
                         removeGroups(vars, refName);
                     }
                 } else // < 0 means we save all the matches
                 {
                     removeGroups(vars, refName); // remove any single matches
                     matchCount = matches.size();
                     vars.put(refName + REF_MATCH_NR, Integer.toString(matchCount));// Save the count
                     for (int i = 1; i <= matchCount; i++) {
                         match = getCorrectMatch(matches, i);
                         if (match != null) {
                             final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                             vars.put(refName_n, generateResult(match));
                             saveGroups(vars, refName_n, match);
                         }
                     }
                 }
                 // Remove any left-over variables
                 for (int i = matchCount + 1; i <= prevCount; i++) {
                     final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                     vars.remove(refName_n);
                     removeGroups(vars, refName_n);
                 }
             } catch (RuntimeException e) {
                 log.warn("Error while generating result");
             }
         } catch (MalformedCachePatternException e) {
             log.error("Error in pattern: '{}'", regex);
         } finally {
             JMeterUtils.clearMatcherMemory(matcher, pattern);
         }
     }
 
     private String getInputString(SampleResult result) {
         String inputString = useUrl() ? result.getUrlAsString() // Bug 39707
                 : useHeaders() ? result.getResponseHeaders()
                 : useRequestHeaders() ? result.getRequestHeaders()
                 : useCode() ? result.getResponseCode() // Bug 43451
                 : useMessage() ? result.getResponseMessage() // Bug 43451
                 : useUnescapedBody() ? StringEscapeUtils.unescapeHtml4(result.getResponseDataAsString())
                 : useBodyAsDocument() ? Document.getTextFromDocument(result.getResponseData())
                 : result.getResponseDataAsString() // Bug 36898
                 ;
        log.debug("Input = '{}'", inputString);
        return inputString;
     }
 
     private List<MatchResult> processMatches(Pattern pattern, String regex, SampleResult result, int matchNumber, JMeterVariables vars) {
         log.debug("Regex = '{}'", regex);
 
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         List<MatchResult> matches = new ArrayList<>();
         int found = 0;
 
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
             if(inputString == null) {
-                log.warn("No variable '{}' found to process by RegexExtractor '{}', skipping processing",
-                        getVariableName(), getName());
+                if (log.isWarnEnabled()) {
+                    log.warn("No variable '{}' found to process by RegexExtractor '{}', skipping processing",
+                            getVariableName(), getName());
+                }
                 return Collections.emptyList();
             }
             matchStrings(matchNumber, matcher, pattern, matches, found,
                     inputString);
         } else {
             List<SampleResult> sampleList = getSampleList(result);
             for (SampleResult sr : sampleList) {
                 String inputString = getInputString(sr);
                 found = matchStrings(matchNumber, matcher, pattern, matches, found,
                         inputString);
                 if (matchNumber > 0 && found == matchNumber){// no need to process further
                     break;
                 }
             }
         }
         return matches;
     }
 
     private int matchStrings(int matchNumber, Perl5Matcher matcher,
             Pattern pattern, List<MatchResult> matches, int found,
             String inputString) {
         PatternMatcherInput input = new PatternMatcherInput(inputString);
         while (matchNumber <=0 || found != matchNumber) {
             if (matcher.contains(input, pattern)) {
                 log.debug("RegexExtractor: Match found!");
                 matches.add(matcher.getMatch());
                 found++;
             } else {
                 break;
             }
         }
         return found;
     }
 
     /**
      * Creates the variables:<br/>
      * basename_gn, where n=0...# of groups<br/>
      * basename_g = number of groups (apart from g0)
      */
     private void saveGroups(JMeterVariables vars, String basename, MatchResult match) {
         StringBuilder buf = new StringBuilder();
         buf.append(basename);
         buf.append("_g"); // $NON-NLS-1$
         int pfxlen=buf.length();
         String prevString=vars.get(buf.toString());
         int previous=0;
         if (prevString!=null){
             try {
                 previous=Integer.parseInt(prevString);
             } catch (NumberFormatException nfe) {
-                log.warn("Could not parse number: '{}', message:'{}'", prevString, nfe.toString());
+                log.warn("Could not parse number: '{}'.", prevString);
             }
         }
         //Note: match.groups() includes group 0
         final int groups = match.groups();
         for (int x = 0; x < groups; x++) {
             buf.append(x);
             vars.put(buf.toString(), match.group(x));
             buf.setLength(pfxlen);
         }
         vars.put(buf.toString(), Integer.toString(groups-1));
         for (int i = groups; i <= previous; i++){
             buf.append(i);
             vars.remove(buf.toString());// remove the remaining _gn vars
             buf.setLength(pfxlen);
         }
     }
 
     /**
      * Removes the variables:<br/>
      * basename_gn, where n=0...# of groups<br/>
      * basename_g = number of groups (apart from g0)
      */
     private void removeGroups(JMeterVariables vars, String basename) {
         StringBuilder buf = new StringBuilder();
         buf.append(basename);
         buf.append("_g"); // $NON-NLS-1$
         int pfxlen=buf.length();
         // How many groups are there?
         int groups;
         try {
             groups=Integer.parseInt(vars.get(buf.toString()));
         } catch (NumberFormatException e) {
             groups=0;
         }
         vars.remove(buf.toString());// Remove the group count
         for (int i = 0; i <= groups; i++) {
             buf.append(i);
             vars.remove(buf.toString());// remove the g0,g1...gn vars
             buf.setLength(pfxlen);
         }
     }
 
     private String generateResult(MatchResult match) {
         StringBuilder result = new StringBuilder();
         for (Object obj : template) {
             if(log.isDebugEnabled()) {
                 log.debug("RegexExtractor: Template piece {} ({})", obj, obj.getClass());
             }
             if (obj instanceof Integer) {
                 result.append(match.group(((Integer) obj).intValue()));
             } else {
                 result.append(obj);
             }
         }
         log.debug("Regex Extractor result = '{}'", result);
         return result.toString();
     }
 
     private void initTemplate() {
         if (template != null) {
             return;
         }
         // Contains Strings and Integers
         List<Object> combined = new ArrayList<>();
         String rawTemplate = getTemplate();
         PatternMatcher matcher = JMeterUtils.getMatcher();
         Pattern templatePattern = JMeterUtils.getPatternCache().getPattern("\\$(\\d+)\\$"  // $NON-NLS-1$
                 , Perl5Compiler.READ_ONLY_MASK
                 & Perl5Compiler.SINGLELINE_MASK);
         if (log.isDebugEnabled()) {
             log.debug("Pattern = '{}', template = '{}'", templatePattern.getPattern(), rawTemplate);
         }
         int beginOffset = 0;
         MatchResult currentResult;
         PatternMatcherInput pinput = new PatternMatcherInput(rawTemplate);
         while(matcher.contains(pinput, templatePattern)) {
             currentResult = matcher.getMatch();
             final int beginMatch = currentResult.beginOffset(0);
             if (beginMatch > beginOffset) { // string is not empty
                 combined.add(rawTemplate.substring(beginOffset, beginMatch));
             }
             combined.add(Integer.valueOf(currentResult.group(1)));// add match as Integer
             beginOffset = currentResult.endOffset(0);
         }
 
         if (beginOffset < rawTemplate.length()) { // trailing string is not empty
             combined.add(rawTemplate.substring(beginOffset, rawTemplate.length()));
         }
         if (log.isDebugEnabled()) {
             log.debug("Template item count: {}", combined.size());
             int i = 0;
             for (Object o : combined) {
                 log.debug("Template item-{}: {} '{}'", i++, o.getClass(), o);
             }
         }
         template = combined;
     }
 
     /**
      * Grab the appropriate result from the list.
      *
      * @param matches
      *            list of matches
      * @param entry
      *            the entry number in the list
      * @return MatchResult
      */
     private MatchResult getCorrectMatch(List<MatchResult> matches, int entry) {
         int matchSize = matches.size();
 
         if (matchSize <= 0 || entry > matchSize){
             return null;
         }
 
         if (entry == 0) // Random match
         {
             return matches.get(JMeterUtils.getRandomInt(matchSize));
         }
 
         return matches.get(entry - 1);
     }
 
     /**
      * Set the regex to be used
      * @param regex The string representation of the regex
      */
     public void setRegex(String regex) {
         setProperty(REGEX, regex);
     }
 
     /**
      * Get the regex which is to be used
      * @return string representing the regex
      */
     public String getRegex() {
         return getPropertyAsString(REGEX);
     }
 
     /**
      * Set the prefix name of the variable to be used to store the regex matches
      * @param refName prefix of the variables to be used
      */
     public void setRefName(String refName) {
         setProperty(REFNAME, refName);
     }
 
     /**
      * Get the prefix name of the variable to be used to store the regex matches
      * @return The prefix of the variables to be used
      */
     public String getRefName() {
         return getPropertyAsString(REFNAME);
     }
 
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning
      * random.
      *
      * @param matchNumber
      *            The number of the match to be used, or <code>0</code> if a
      *            random match should be used.
      */
     public void setMatchNumber(int matchNumber) {
         setProperty(new IntegerProperty(MATCH_NUMBER, matchNumber));
     }
 
     public void setMatchNumber(String matchNumber) {
         setProperty(MATCH_NUMBER, matchNumber);
     }
 
     public int getMatchNumber() {
         return getPropertyAsInt(MATCH_NUMBER);
     }
 
     public String getMatchNumberAsString() {
         return getPropertyAsString(MATCH_NUMBER);
     }
 
     /**
      * Sets the value of the variable if no matches are found
      *
      * @param defaultValue The default value for the variable
      */
     public void setDefaultValue(String defaultValue) {
         setProperty(DEFAULT, defaultValue);
     }
     
     /**
      * Set default value to "" value when if it's empty
      *
      * @param defaultEmptyValue The default value for the variable
      */
     public void setDefaultEmptyValue(boolean defaultEmptyValue) {
         setProperty(DEFAULT_EMPTY_VALUE, defaultEmptyValue, DEFAULT_VALUE_FOR_DEFAULT_EMPTY_VALUE);
     }
 
     /**
      * Get the default value for the variable, which should be used, if no
      * matches are found
      * 
      * @return The default value for the variable
      */
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
     }
     
     /**
      * Do we set default value to "" value when if it's empty
      * @return true if we should set default value to "" if variable cannot be extracted
      */
     public boolean isEmptyDefaultValue() {
         return getPropertyAsBoolean(DEFAULT_EMPTY_VALUE, DEFAULT_VALUE_FOR_DEFAULT_EMPTY_VALUE);
     }
 
     public void setTemplate(String template) {
         setProperty(TEMPLATE, template);
     }
 
     public String getTemplate() {
         return getPropertyAsString(TEMPLATE);
     }
 
     public boolean useHeaders() {
         return USE_HDRS.equalsIgnoreCase( getPropertyAsString(MATCH_AGAINST));
     }
 
     public boolean useRequestHeaders() {
         return USE_REQUEST_HDRS.equalsIgnoreCase(getPropertyAsString(MATCH_AGAINST));
     }
 
     // Allow for property not yet being set (probably only applies to Test cases)
     public boolean useBody() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return prop.length()==0 || USE_BODY.equalsIgnoreCase(prop);// $NON-NLS-1$
     }
 
     public boolean useUnescapedBody() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_BODY_UNESCAPED.equalsIgnoreCase(prop);// $NON-NLS-1$
     }
 
     public boolean useBodyAsDocument() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_BODY_AS_DOCUMENT.equalsIgnoreCase(prop);// $NON-NLS-1$
     }
 
     public boolean useUrl() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_URL.equalsIgnoreCase(prop);
     }
 
     public boolean useCode() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_CODE.equalsIgnoreCase(prop);
     }
 
     public boolean useMessage() {
         String prop = getPropertyAsString(MATCH_AGAINST);
         return USE_MESSAGE.equalsIgnoreCase(prop);
     }
 
     public void setUseField(String actionCommand) {
         setProperty(MATCH_AGAINST,actionCommand);
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/XPathExtractor.java b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
index 5f6241995..eff4fa088 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,383 +1,389 @@
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
 package org.apache.jmeter.extractor;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.Serializable;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.TidyException;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 
 /**
  * Extracts text from (X)HTML response using XPath query language
  * Example XPath queries:
  * <dl>
  * <dt>/html/head/title</dt>
  *     <dd>extracts Title from HTML response</dd>
  * <dt>//form[@name='countryForm']//select[@name='country']/option[text()='Czech Republic'])/@value
  *     <dd>extracts value attribute of option element that match text 'Czech Republic'
  *                 inside of select element with name attribute  'country' inside of
  *                 form with name attribute 'countryForm'</dd>
  * <dt>//head</dt>
  *     <dd>extracts the XML fragment for head node.</dd>
  * <dt>//head/text()</dt>
  *     <dd>extracts the text content for head node.</dd>
  * </dl>
   see org.apache.jmeter.extractor.TestXPathExtractor for unit tests
  */
 public class XPathExtractor extends AbstractScopedTestElement implements
         PostProcessor, Serializable {
     private static final Logger log = LoggerFactory.getLogger(XPathExtractor.class);
 
     private static final long serialVersionUID = 242L;
     
     private static final int DEFAULT_VALUE = -1;
     public static final String DEFAULT_VALUE_AS_STRING = Integer.toString(DEFAULT_VALUE);
 
     private static final String REF_MATCH_NR    = "matchNr"; // $NON-NLS-1$
 
     //+ JMX file attributes
     private static final String XPATH_QUERY     = "XPathExtractor.xpathQuery"; // $NON-NLS-1$
     private static final String REFNAME         = "XPathExtractor.refname"; // $NON-NLS-1$
     private static final String DEFAULT         = "XPathExtractor.default"; // $NON-NLS-1$
     private static final String TOLERANT        = "XPathExtractor.tolerant"; // $NON-NLS-1$
     private static final String NAMESPACE       = "XPathExtractor.namespace"; // $NON-NLS-1$
     private static final String QUIET           = "XPathExtractor.quiet"; // $NON-NLS-1$
     private static final String REPORT_ERRORS   = "XPathExtractor.report_errors"; // $NON-NLS-1$
     private static final String SHOW_WARNINGS   = "XPathExtractor.show_warnings"; // $NON-NLS-1$
     private static final String DOWNLOAD_DTDS   = "XPathExtractor.download_dtds"; // $NON-NLS-1$
     private static final String WHITESPACE      = "XPathExtractor.whitespace"; // $NON-NLS-1$
     private static final String VALIDATE        = "XPathExtractor.validate"; // $NON-NLS-1$
     private static final String FRAGMENT        = "XPathExtractor.fragment"; // $NON-NLS-1$
     private static final String MATCH_NUMBER    = "XPathExtractor.matchNumber"; // $NON-NLS-1$
     //- JMX file attributes
 
 
     private String concat(String s1,String s2){
         return new StringBuilder(s1).append("_").append(s2).toString(); // $NON-NLS-1$
     }
 
     private String concat(String s1, int i){
         return new StringBuilder(s1).append("_").append(i).toString(); // $NON-NLS-1$
     }
 
     /**
      * Do the job - extract value from (X)HTML response using XPath Query.
      * Return value as variable defined by REFNAME. Returns DEFAULT value
      * if not found.
      */
     @Override
     public void process() {
         JMeterContext context = getThreadContext();
         final SampleResult previousResult = context.getPreviousResult();
         if (previousResult == null){
             return;
         }
         JMeterVariables vars = context.getVariables();
         String refName = getRefName();
         vars.put(refName, getDefaultValue());
         final String matchNR = concat(refName,REF_MATCH_NR);
         int prevCount=0; // number of previous matches
         try {
             prevCount=Integer.parseInt(vars.get(matchNR));
         } catch (NumberFormatException e) {
             // ignored
         }
         vars.put(matchNR, "0"); // In case parse fails // $NON-NLS-1$
         vars.remove(concat(refName,"1")); // In case parse fails // $NON-NLS-1$
 
         int matchNumber = getMatchNumber();
         List<String> matches = new ArrayList<>();
         try{
             if (isScopeVariable()){
                 String inputString=vars.get(getVariableName());
                 if(inputString != null) {
                     if(inputString.length()>0) {
                         Document d =  parseResponse(inputString);
                         getValuesForXPath(d,getXPathQuery(), matches, matchNumber);
                     }
                 } else {
-                    log.warn("No variable '{}' found to process by XPathExtractor '{}', skipping processing",
-                            getVariableName(), getName());
+                    if (log.isWarnEnabled()) {
+                        log.warn("No variable '{}' found to process by XPathExtractor '{}', skipping processing",
+                                getVariableName(), getName());
+                    }
                 }
             } else {
                 List<SampleResult> samples = getSampleList(previousResult);
                 for (SampleResult res : samples) {
                     Document d = parseResponse(res.getResponseDataAsString());
                     getValuesForXPath(d,getXPathQuery(), matches, matchNumber);
                 }
             }
             final int matchCount = matches.size();
             vars.put(matchNR, String.valueOf(matchCount));
             if (matchCount > 0){
                 String value = matches.get(0);
                 if (value != null) {
                     vars.put(refName, value);
                 }
                 for(int i=0; i < matchCount; i++){
                     value = matches.get(i);
                     if (value != null) {
                         vars.put(concat(refName,i+1),matches.get(i));
                     }
                 }
             }
             vars.remove(concat(refName,matchCount+1)); // Just in case
             // Clear any other remaining variables
             for(int i=matchCount+2; i <= prevCount; i++) {
                 vars.remove(concat(refName,i));
             }
         }catch(IOException e){// e.g. DTD not reachable
             log.error("IOException on ({})", getXPathQuery(), e);
             AssertionResult ass = new AssertionResult(getName());
             ass.setError(true);
             ass.setFailureMessage(new StringBuilder("IOException: ").append(e.getLocalizedMessage()).toString());
             previousResult.addAssertionResult(ass);
             previousResult.setSuccessful(false);
         } catch (ParserConfigurationException e) {// Should not happen
             final String errrorMessage = "ParserConfigurationException while processing ("+getXPathQuery()+")";
             log.error(errrorMessage,e);
             throw new JMeterError(errrorMessage,e);
         } catch (SAXException e) {// Can happen for bad input document
-            log.warn("SAXException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
+            if (log.isWarnEnabled()) {
+                log.warn("SAXException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
+            }
             addAssertionFailure(previousResult, e, false); // Should this also fail the sample?
         } catch (TransformerException e) {// Can happen for incorrect XPath expression
-            log.warn("TransformerException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
+            if (log.isWarnEnabled()) {
+                log.warn("TransformerException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
+            }
             addAssertionFailure(previousResult, e, false);
         } catch (TidyException e) {
             // Will already have been logged by XPathUtil
             addAssertionFailure(previousResult, e, true); // fail the sample
         }
     }
 
     private void addAssertionFailure(final SampleResult previousResult,
             final Throwable thrown, final boolean setFailed) {
         AssertionResult ass = new AssertionResult(getName()); // $NON-NLS-1$
         ass.setFailure(true);
         ass.setFailureMessage(thrown.getLocalizedMessage()+"\nSee log file for further details.");
         previousResult.addAssertionResult(ass);
         if (setFailed){
             previousResult.setSuccessful(false);
         }
     }
 
     /*============= object properties ================*/
     public void setXPathQuery(String val){
         setProperty(XPATH_QUERY,val);
     }
 
     public String getXPathQuery(){
         return getPropertyAsString(XPATH_QUERY);
     }
 
     public void setRefName(String refName) {
         setProperty(REFNAME, refName);
     }
 
     public String getRefName() {
         return getPropertyAsString(REFNAME);
     }
 
     public void setDefaultValue(String val) {
         setProperty(DEFAULT, val);
     }
 
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
     }
 
     public void setTolerant(boolean val) {
         setProperty(new BooleanProperty(TOLERANT, val));
     }
 
     public boolean isTolerant() {
         return getPropertyAsBoolean(TOLERANT);
     }
 
     public void setNameSpace(boolean val) {
         setProperty(new BooleanProperty(NAMESPACE, val));
     }
 
     public boolean useNameSpace() {
         return getPropertyAsBoolean(NAMESPACE);
     }
 
     public void setReportErrors(boolean val) {
             setProperty(REPORT_ERRORS, val, false);
     }
 
     public boolean reportErrors() {
         return getPropertyAsBoolean(REPORT_ERRORS, false);
     }
 
     public void setShowWarnings(boolean val) {
         setProperty(SHOW_WARNINGS, val, false);
     }
 
     public boolean showWarnings() {
         return getPropertyAsBoolean(SHOW_WARNINGS, false);
     }
 
     public void setQuiet(boolean val) {
         setProperty(QUIET, val, true);
     }
 
     public boolean isQuiet() {
         return getPropertyAsBoolean(QUIET, true);
     }
 
     /**
      * Should we return fragment as text, rather than text of fragment?
      * @return true if we should return fragment rather than text
      */
     public boolean getFragment() {
         return getPropertyAsBoolean(FRAGMENT, false);
     }
 
     /**
      * Should we return fragment as text, rather than text of fragment?
      * @param selected true to return fragment.
      */
     public void setFragment(boolean selected) {
         setProperty(FRAGMENT, selected, false);
     }
 
     /*================= internal business =================*/
     /**
      * Converts (X)HTML response to DOM object Tree.
      * This version cares of charset of response.
      * @param unicodeData
      * @return the parsed document
      *
      */
     private Document parseResponse(String unicodeData)
       throws IOException, ParserConfigurationException,SAXException,TidyException
     {
       //TODO: validate contentType for reasonable types?
 
       // NOTE: responseData encoding is server specific
       //       Therefore we do byte -> unicode -> byte conversion
       //       to ensure UTF-8 encoding as required by XPathUtil
       // convert unicode String -> UTF-8 bytes
       byte[] utf8data = unicodeData.getBytes(StandardCharsets.UTF_8);
       ByteArrayInputStream in = new ByteArrayInputStream(utf8data);
       boolean isXML = JOrphanUtils.isXML(utf8data);
       // this method assumes UTF-8 input data
       return XPathUtil.makeDocument(in,false,false,useNameSpace(),isTolerant(),isQuiet(),showWarnings(),reportErrors()
               ,isXML, isDownloadDTDs());
     }
 
     /**
      * Extract value from Document d by XPath query.
      * @param d the document
      * @param query the query to execute
      * @param matchStrings list of matched strings (may include nulls)
      * @param matchNumber int Match Number
      *
      * @throws TransformerException
      */
     private void getValuesForXPath(Document d,String query, List<String> matchStrings, int matchNumber)
         throws TransformerException {
         XPathUtil.putValuesForXPathInList(d, query, matchStrings, getFragment(), matchNumber);
     }
 
     public void setWhitespace(boolean selected) {
         setProperty(WHITESPACE, selected, false);
     }
 
     public boolean isWhitespace() {
         return getPropertyAsBoolean(WHITESPACE, false);
     }
 
     public void setValidating(boolean selected) {
         setProperty(VALIDATE, selected);
     }
 
     public boolean isValidating() {
         return getPropertyAsBoolean(VALIDATE, false);
     }
 
     public void setDownloadDTDs(boolean selected) {
         setProperty(DOWNLOAD_DTDS, selected, false);
     }
 
     public boolean isDownloadDTDs() {
         return getPropertyAsBoolean(DOWNLOAD_DTDS, false);
     }
     
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning random.
      *
      * @param matchNumber The number of the match to be used
      */
     public void setMatchNumber(int matchNumber) {
         setProperty(new IntegerProperty(MATCH_NUMBER, matchNumber));
     }
 
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning random.
      *
      * @param matchNumber The number of the match to be used
      */
     public void setMatchNumber(String matchNumber) {
         setProperty(MATCH_NUMBER, matchNumber);
     }
 
     /**
      * Return which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning random.
      *
      * @return matchNumber The number of the match to be used
      */
     public int getMatchNumber() {
         return getPropertyAsInt(MATCH_NUMBER, DEFAULT_VALUE);
     }
 
     /**
      * Return which Match to use. This can be any positive number, indicating the
      * exact match to use, or <code>0</code>, which is interpreted as meaning random.
      *
      * @return matchNumber The number of the match to be used
      */
     public String getMatchNumberAsString() {
         return getPropertyAsString(MATCH_NUMBER, DEFAULT_VALUE_AS_STRING);
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java b/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
index 451aa6479..d3b9ab554 100644
--- a/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
+++ b/src/components/org/apache/jmeter/modifiers/BSFPreProcessor.java
@@ -1,57 +1,59 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BSFPreProcessor extends BSFTestElement implements Cloneable, PreProcessor, TestBean
 {
     private static final Logger log = LoggerFactory.getLogger(BSFPreProcessor.class);
 
     private static final long serialVersionUID = 233L;
 
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
-            log.warn("Problem in BSF script. {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BSF script. {}", e.toString());
+            }
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
index 80e9d9825..605defb93 100644
--- a/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java
+++ b/src/components/org/apache/jmeter/modifiers/BeanShellPreProcessor.java
@@ -1,69 +1,71 @@
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
 import org.apache.jorphan.util.JMeterException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BeanShellPreProcessor extends BeanShellTestElement
     implements Cloneable, PreProcessor, TestBean
 {
     private static final Logger log = LoggerFactory.getLogger(BeanShellPreProcessor.class);
 
     private static final long serialVersionUID = 5;
 
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
-            log.warn("Problem in BeanShell script. {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BeanShell script. {}", e.toString());
+            }
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/modifiers/SampleTimeout.java b/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
index d20d5667b..7c3b94345 100644
--- a/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
+++ b/src/components/org/apache/jmeter/modifiers/SampleTimeout.java
@@ -1,193 +1,192 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * 
  * Sample timeout implementation using Executor threads
  * @since 3.0
  */
 public class SampleTimeout extends AbstractTestElement implements Serializable, ThreadListener, SampleMonitor {
 
     private static final long serialVersionUID = 2L;
 
     private static final Logger log = LoggerFactory.getLogger(SampleTimeout.class);
 
     private static final String TIMEOUT = "InterruptTimer.timeout"; //$NON-NLS-1$
 
     private ScheduledFuture<?> future;
     
     private final transient ScheduledExecutorService execService;
-    
-    private final boolean debug;
 
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
-        debug = log.isDebugEnabled();
         execService = getExecutorService();
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug(whoAmI("InterruptTimer()", this));
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
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug(whoAmI("sampleStarting()", this));
         }
         createTask(sampler);
     }
 
     @Override
     public void sampleEnded(final Sampler sampler) {
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug(whoAmI("sampleEnded()", this));
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
-                log.warn("Call Done interrupting {} took {}", getInfo(samp), elapsed);
+                if (log.isWarnEnabled()) {
+                    log.warn("Call Done interrupting {} took {}", getInfo(samp), elapsed);
+                }
             } else {
-                if (debug) {
+                if (log.isDebugEnabled()) {
                     log.debug("Call Didn't interrupt: {} took {}", getInfo(samp), elapsed);
                 }
             }
             return null;
         };
         // schedule the interrupt to occur and save for possible cancellation 
         future = execService.schedule(call, timeout, TimeUnit.MILLISECONDS);
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug("Scheduled timer: @{} {}", System.identityHashCode(future), getInfo(samp));
         }
     }
 
     @Override
     public void threadStarted() {
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug(whoAmI("threadStarted()", this));
         }
      }
 
     @Override
     public void threadFinished() {
-        if (debug) {
+        if (log.isDebugEnabled()) {
             log.debug(whoAmI("threadFinished()", this));
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
-        return id + " @" + System.identityHashCode(o)+ " '"+ o.getName() + "' " + (debug ?  Thread.currentThread().getName() : "");         
+        return id + " @" + System.identityHashCode(o)+ " '"+ o.getName() + "' " + (log.isDebugEnabled() ?  Thread.currentThread().getName() : "");         
     }
 
     private String getInfo(TestElement o) {
         return whoAmI(o.getClass().getSimpleName(), o); 
     }
 
     private void cancelTask() {
         if (future != null) {
             if (!future.isDone()) {
                 boolean cancelled = future.cancel(false);
-                if (debug) {
+                if (log.isDebugEnabled()) {
                     log.debug("Cancelled timer: @{}  with result {}", System.identityHashCode(future), cancelled);
                 }
             }
             future = null;
         }        
     }
 
 }
diff --git a/src/components/org/apache/jmeter/sampler/TestAction.java b/src/components/org/apache/jmeter/sampler/TestAction.java
index b9b3faa53..d1b9e2f65 100644
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Dummy Sampler used to pause or stop a thread or the test;
  * intended for use in Conditional Controllers.
  *
  */
 public class TestAction extends AbstractSampler implements Interruptible {
 
     private static final Logger log = LoggerFactory.getLogger(TestAction.class);
 
     private static final long serialVersionUID = 241L;
 
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
-            log.warn("Could not create number from {}", timeInMillis);
+            log.warn("Could not parse number: '{}'", timeInMillis);
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
index dabca91db..1495ee690 100644
--- a/src/components/org/apache/jmeter/timers/BSFTimer.java
+++ b/src/components/org/apache/jmeter/timers/BSFTimer.java
@@ -1,60 +1,62 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BSFTimer extends BSFTestElement implements Cloneable, Timer, TestBean {
     private static final Logger log = LoggerFactory.getLogger(BSFTimer.class);
 
     private static final long serialVersionUID = 5;
 
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
-            log.warn("Problem in BSF script. {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BSF script. {}", e.toString());
+            }
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
index a69505056..f8a94c608 100644
--- a/src/components/org/apache/jmeter/timers/BeanShellTimer.java
+++ b/src/components/org/apache/jmeter/timers/BeanShellTimer.java
@@ -1,72 +1,74 @@
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
 import org.apache.jorphan.util.JMeterException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class BeanShellTimer extends BeanShellTestElement implements Cloneable, Timer, TestBean {
     private static final Logger log = LoggerFactory.getLogger(BeanShellTimer.class);
 
     private static final long serialVersionUID = 5;
 
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
-            log.warn("Problem in BeanShell script. {}", e.toString());
+            if (log.isWarnEnabled()) {
+                log.warn("Problem in BeanShell script. {}", e.toString());
+            }
         }
         try {
             return Long.decode(ret).longValue();
         } catch (NumberFormatException e){
-            log.warn(e.getLocalizedMessage());
+            log.warn("Number format exception while decoding number: '{}'", ret);
             return 0;
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/timers/SyncTimer.java b/src/components/org/apache/jmeter/timers/SyncTimer.java
index b1ea62f24..d80ba2f7a 100644
--- a/src/components/org/apache/jmeter/timers/SyncTimer.java
+++ b/src/components/org/apache/jmeter/timers/SyncTimer.java
@@ -1,278 +1,280 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * The purpose of the SyncTimer is to block threads until X number of threads
  * have been blocked, and then they are all released at once. A SyncTimer can
  * thus create large instant loads at various points of the test plan.
  *
  */
 public class SyncTimer extends AbstractTestElement implements Timer, Serializable, TestBean, TestStateListener, ThreadListener {
     private static final Logger log = LoggerFactory.getLogger(SyncTimer.class);
 
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
 
     private static final long serialVersionUID = 3;
 
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
-                log.warn("SyncTimer {} timeouted waiting for users after: {}ms", getName(), getTimeoutInMs());
+                if (log.isWarnEnabled()) {
+                    log.warn("SyncTimer {} timeouted waiting for users after: {}ms", getName(), getTimeoutInMs());
+                }
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
