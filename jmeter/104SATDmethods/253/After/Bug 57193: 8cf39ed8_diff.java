diff --git a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
index 7bd88d939..8d79281ea 100644
--- a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
@@ -1,376 +1,385 @@
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
 import java.text.MessageFormat;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.w3c.tidy.Node;
 import org.w3c.tidy.Tidy;
 
 /**
  * Assertion to validate the response of a Sample with Tidy.
  */
 public class HTMLAssertion extends AbstractTestElement implements Serializable, Assertion {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
             if (log.isDebugEnabled()){
                 log.debug("HTMLAssertions.getResult(): Setup tidy ...");
                 log.debug("doctype: " + getDoctype());
                 log.debug("errors only: " + isErrorsOnly());
                 log.debug("error threshold: " + getErrorThreshold());
                 log.debug("warning threshold: " + getWarningThreshold());
                 log.debug("html mode: " + isHTML());
                 log.debug("xhtml mode: " + isXHTML());
                 log.debug("xml mode: " + isXML());
             }
             tidy = new Tidy();
             tidy.setInputEncoding("UTF8");
             tidy.setOutputEncoding("UTF8");
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
                 log.debug("err file: " + getFilename());
                 log.debug("getParser : tidy parser created - " + tidy);
                 log.debug("HTMLAssertions.getResult(): Tidy instance created!");
             }
 
         } catch (Exception e) {//TODO replace with proper Exception
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
             // Node node = tidy.parseDOM(new
             // ByteArrayInputStream(response.getResponseData()), null);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             log.debug("Start : parse");
             Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
             if (log.isDebugEnabled()) {
                 log.debug("node : " + node);
                 log.debug("End   : parse");
                 log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
                 log.debug("Output: " + os.toString());
             }
 
             // write output to file
             writeOutput(errbuf.toString());
 
             // evaluate result
             if ((tidy.getParseErrors() > getErrorThreshold())
                     || (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
                 if (log.isDebugEnabled()) {
                     log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
                     log.debug(errbuf.toString());
                 }
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
 
         } catch (Exception e) {//TODO replace with proper Exception
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
      * @param inOutput
      */
     private void writeOutput(String inOutput) {
         String lFilename = getFilename();
 
         // check if filename defined
         if ((lFilename != null) && (!"".equals(lFilename.trim()))) {
             FileWriter lOutputWriter = null;
             try {
 
                 // open file
                 lOutputWriter = new FileWriter(lFilename, false);
 
                 // write to file
                 lOutputWriter.write(inOutput);
 
                 // flush
                 lOutputWriter.flush();
 
                 if (log.isDebugEnabled()) {
                     log.debug("writeOutput() -> output successfully written to file " + lFilename);
                 }
 
             } catch (IOException ex) {
                 log.warn("writeOutput() -> could not write output to file " + lFilename, ex);
             } finally {
                 // close file
                 IOUtils.closeQuietly(lOutputWriter);
             }
         }
     }
 
     /**
      * Gets the doctype
      * 
      * @return the documemt type
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
+     *            The doctype to be set. If <code>doctype</code> is
+     *            <code>null</code> or a blank string, {@link HTMLAssertion#DEFAULT_DOCTYPE} will be
+     *            used
      */
     public void setDoctype(String inDoctype) {
         if ((inDoctype == null) || (inDoctype.trim().equals(""))) {
             setProperty(new StringProperty(DOCTYPE_KEY, DEFAULT_DOCTYPE));
         } else {
             setProperty(new StringProperty(DOCTYPE_KEY, inDoctype));
         }
     }
 
     /**
-     * Sets if errors shoud be tracked only
+     * Sets if errors should be tracked only
      * 
-     * @param inErrorsOnly
+     * @param inErrorsOnly Flag whether only errors should be tracked
      */
     public void setErrorsOnly(boolean inErrorsOnly) {
         setProperty(new BooleanProperty(ERRORS_ONLY_KEY, inErrorsOnly));
     }
 
     /**
      * Sets the threshold on error level
      * 
      * @param inErrorThreshold
+     *            The max number of parse errors which are to be tolerated
+     * @throws IllegalArgumentException
+     *             if <code>inErrorThreshold</code> is less or equals zero
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
+     *            The max number of warnings which are to be tolerated
+     * @throws IllegalArgumentException
+     *             if <code>inWarningThreshold</code> is less or equal zero
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
-     * @param inName
+     * @param inName The name of the file tidy will put its output to
      */
     public void setFilename(String inName) {
         setProperty(FILENAME_KEY, inName);
     }
 }
diff --git a/src/components/org/apache/jmeter/assertions/SizeAssertion.java b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
index 45d30431c..d9442b4c4 100644
--- a/src/components/org/apache/jmeter/assertions/SizeAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
@@ -1,241 +1,263 @@
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
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.util.JMeterUtils;
 
 //@see org.apache.jmeter.assertions.SizeAssertionTest for unit tests
 
 /**
  * Checks if the results of a Sample matches a particular size.
  * 
  */
 public class SizeAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
 
     private static final long serialVersionUID = 241L;
 
     // * Static int to signify the type of logical comparitor to assert
     public static final int EQUAL = 1;
 
     public static final int NOTEQUAL = 2;
 
     public static final int GREATERTHAN = 3;
 
     public static final int LESSTHAN = 4;
 
     public static final int GREATERTHANEQUAL = 5;
 
     public static final int LESSTHANEQUAL = 6;
 
     /** Key for storing assertion-informations in the jmx-file. */
     private static final String SIZE_KEY = "SizeAssertion.size"; // $NON-NLS-1$
 
     private static final String OPERATOR_KEY = "SizeAssertion.operator"; // $NON-NLS-1$
     
     private static final String TEST_FIELD = "Assertion.test_field";  // $NON-NLS-1$
 
     private static final String RESPONSE_NETWORK_SIZE = "SizeAssertion.response_network_size"; // $NON-NLS-1$
 
     private static final String RESPONSE_HEADERS = "SizeAssertion.response_headers"; // $NON-NLS-1$
 
     private static final String RESPONSE_BODY = "SizeAssertion.response_data"; // $NON-NLS-1$
 
     private static final String RESPONSE_CODE = "SizeAssertion.response_code"; // $NON-NLS-1$
 
     private static final String RESPONSE_MESSAGE = "SizeAssertion.response_message"; // $NON-NLS-1$
 
     /**
      * Returns the result of the Assertion. 
      * Here it checks the Sample responseData length.
      */
     @Override
     public AssertionResult getResult(SampleResult response) {
         AssertionResult result = new AssertionResult(getName());
         result.setFailure(false);
         long resultSize=0;
         if (isScopeVariable()){
             String variableName = getVariableName();
             String value = getThreadContext().getVariables().get(variableName);
             try {
                 resultSize = Integer.parseInt(value);
             } catch (NumberFormatException e) {
                 result.setFailure(true);
                 result.setFailureMessage("Error parsing variable name: "+variableName+" value: "+value);
                 return result;
             }
         } else if (isTestFieldResponseHeaders()) {
             resultSize = response.getHeadersSize();
         }  else if (isTestFieldResponseBody()) {
             resultSize = response.getBodySize();
         } else if (isTestFieldResponseCode()) {
             resultSize = response.getResponseCode().length();
         } else if (isTestFieldResponseMessage()) {
             resultSize = response.getResponseMessage().length();
         } else {
             resultSize = response.getBytes();
         }
         // is the Sample the correct size?
         final String msg = compareSize(resultSize);
         if (msg.length() > 0) {
             result.setFailure(true);
             Object[] arguments = { Long.valueOf(resultSize), msg, Long.valueOf(getAllowedSize()) };
             String message = MessageFormat.format(JMeterUtils.getResString("size_assertion_failure"), arguments); //$NON-NLS-1$
             result.setFailureMessage(message);
         }
         return result;
     }
 
     /**
      * Returns the size in bytes to be asserted.
+     * @return The allowed size
      */
     public String getAllowedSize() {
         return getPropertyAsString(SIZE_KEY);
     }
 
-    /***************************************************************************
-     * set the Operator
-     **************************************************************************/
+    /**
+     Set the operator used for the assertion. Has to be one of
+     <dl>
+     * <dt>EQUAL</dt><dd>1</dd>
+     * <dt>NOTEQUAL</dt><dd>2</dd>
+     * <dt>GREATERTHAN</dt><dd>3</dd>
+     * <dt>LESSTHAN</dt><dd>4</dd>
+     * <dt>GREATERTHANEQUAL</dt><dd>5</dd>
+     * <dt>LESSTHANEQUAL</dt><dd>6</dd>
+     * </dl>
+     * @param operator The operator to be used in the assertion
+     */
     public void setCompOper(int operator) {
         setProperty(new IntegerProperty(OPERATOR_KEY, operator));
 
     }
 
     /**
-     * Returns the operator to be asserted. EQUAL = 1, NOTEQUAL = 2 GREATERTHAN =
-     * 3,LESSTHAN = 4,GREATERTHANEQUAL = 5,LESSTHANEQUAL = 6
+     * Returns the operator to be asserted. 
+     * <dl>
+     * <dt>EQUAL</dt><dd>1</dd>
+     * <dt>NOTEQUAL</dt><dd>2</dd>
+     * <dt>GREATERTHAN</dt><dd>3</dd>
+     * <dt>LESSTHAN</dt><dd>4</dd>
+     * <dt>GREATERTHANEQUAL</dt><dd>5</dd>
+     * <dt>LESSTHANEQUAL</dt><dd>6</dd>
+     * </dl>
+     * @return The operator used for the assertion
      */
 
     public int getCompOper() {
         return getPropertyAsInt(OPERATOR_KEY);
     }
 
     /**
      * Set the size that shall be asserted.
      * 
      * @param size a number of bytes. 
      */
     public void setAllowedSize(String size) {
             setProperty(SIZE_KEY, size);
     }
 
+    /**
+     * Set the size that should be used in the assertion
+     * @param size The number of bytes
+     */
     public void setAllowedSize(long size) {
         setProperty(SIZE_KEY, Long.toString(size));
     }
 
     /**
      * Compares the the size of a return result to the set allowed size using a
      * logical comparator set in setLogicalComparator().
      * 
      * Possible values are: equal, not equal, greater than, less than, greater
-     * than eqaul, less than equal, .
+     * than equal, less than equal.
      * 
      */
     private String compareSize(long resultSize) {
         String comparatorErrorMessage;
         long allowedSize = Long.parseLong(getAllowedSize());
         boolean result = false;
         int comp = getCompOper();
         switch (comp) {
         case EQUAL:
             result = (resultSize == allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_equal"); //$NON-NLS-1$
             break;
         case NOTEQUAL:
             result = (resultSize != allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_notequal"); //$NON-NLS-1$
             break;
         case GREATERTHAN:
             result = (resultSize > allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greater"); //$NON-NLS-1$
             break;
         case LESSTHAN:
             result = (resultSize < allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_less"); //$NON-NLS-1$
             break;
         case GREATERTHANEQUAL:
             result = (resultSize >= allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greaterequal"); //$NON-NLS-1$
             break;
         case LESSTHANEQUAL:
             result = (resultSize <= allowedSize);
             comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_lessequal"); //$NON-NLS-1$
             break;
         default:
             result = false;
             comparatorErrorMessage = "ERROR - invalid condition";
             break;
         }
         return result ? "" : comparatorErrorMessage;
     }
     
     private void setTestField(String testField) {
         setProperty(TEST_FIELD, testField);
     }
 
     public void setTestFieldNetworkSize(){
         setTestField(RESPONSE_NETWORK_SIZE);
     }
     
     public void setTestFieldResponseHeaders(){
         setTestField(RESPONSE_HEADERS);
     }
     
     public void setTestFieldResponseBody(){
         setTestField(RESPONSE_BODY);
     }
     
     public void setTestFieldResponseCode(){
         setTestField(RESPONSE_CODE);
     }
     
     public void setTestFieldResponseMessage(){
         setTestField(RESPONSE_MESSAGE);
     }
 
     public String getTestField() {
         return getPropertyAsString(TEST_FIELD);
     }
 
     public boolean isTestFieldNetworkSize(){
         return RESPONSE_NETWORK_SIZE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseHeaders(){
         return RESPONSE_HEADERS.equals(getTestField());
     }
     
     public boolean isTestFieldResponseBody(){
         return RESPONSE_BODY.equals(getTestField());
     }
 
     public boolean isTestFieldResponseCode(){
         return RESPONSE_CODE.equals(getTestField());
     }
 
     public boolean isTestFieldResponseMessage(){
         return RESPONSE_MESSAGE.equals(getTestField());
     }
 
 }
diff --git a/src/components/org/apache/jmeter/assertions/XPathAssertion.java b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
index 2ba1c9ae4..c06400eaa 100644
--- a/src/components/org/apache/jmeter/assertions/XPathAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
@@ -1,274 +1,274 @@
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
 
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.TidyException;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 /**
  * Checks if the result is a well-formed XML content and whether it matches an
  * XPath
  *
  */
 public class XPathAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
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
                 if(!StringUtils.isEmpty(inputString)) {
                     responseData = inputString.getBytes("UTF-8");
                 } 
             } else {
                 responseData = response.getResponseData();
             }
             
             if (responseData == null || responseData.length == 0) {
                 return result.setResultForNull();
             }
     
             if (log.isDebugEnabled()) {
                 log.debug(new StringBuilder("Validation is set to ").append(isValidating()).toString());
                 log.debug(new StringBuilder("Whitespace is set to ").append(isWhitespace()).toString());
                 log.debug(new StringBuilder("Tolerant is set to ").append(isTolerant()).toString());
             }
     
     
             boolean isXML = JOrphanUtils.isXML(responseData);
 
             doc = XPathUtil.makeDocument(new ByteArrayInputStream(responseData), isValidating(),
                     isWhitespace(), isNamespace(), isTolerant(), isQuiet(), showWarnings() , reportErrors(), isXML
                     , isDownloadDTDs());
         } catch (SAXException e) {
             log.debug("Caught sax exception: " + e);
             result.setError(true);
             result.setFailureMessage(new StringBuilder("SAXException: ").append(e.getMessage()).toString());
             return result;
         } catch (IOException e) {
             log.warn("Cannot parse result content", e);
             result.setError(true);
             result.setFailureMessage(new StringBuilder("IOException: ").append(e.getMessage()).toString());
             return result;
         } catch (ParserConfigurationException e) {
             log.warn("Cannot parse result content", e);
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
-     * @param whitespace
+     * @param whitespace Flag whether whitespace elements should be ignored
      */
     public void setWhitespace(boolean whitespace) {
         setProperty(new BooleanProperty(WHITESPACE_KEY, whitespace));
     }
 
     /**
      * Set use validation
      *
-     * @param validate
+     * @param validate Flag whether validation should be used
      */
     public void setValidating(boolean validate) {
         setProperty(new BooleanProperty(VALIDATE_KEY, validate));
     }
 
     /**
      * Set whether this is namespace aware
      *
-     * @param namespace
+     * @param namespace Flag whether namespace should be used
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
      * Is this whitepsace ignored.
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
diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index 4751b4815..721c0071a 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,431 +1,431 @@
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
  * <code>while (running &amp;&amp; (sampler = controller.next()) != null)</code>
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
         first = true; // TODO should this use setFirst()?        
         initializeSubControllers();
     }
 
     /**
      * (re)Initializes sub controllers
      * See Bug 50032
      */
     protected void initializeSubControllers() {
         for (TestElement te : subControllersAndSamplers) {
             if(te instanceof GenericController) {
                 ((Controller) te).initialize();
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
-     * @param b
+     * @param b The flag, whether first is reseted
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
         Sampler sampler = controller.next();
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
      * @deprecated replaced by GeneriController#initializeSubControllers
      */
     protected void reInitializeSubController() {
         initializeSubControllers();
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
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
index a28e6b86a..0ecd843f3 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
@@ -1,241 +1,281 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.tree.DefaultTreeModel;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.TestPlanGui;
 import org.apache.jmeter.control.gui.WorkBenchGui;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 
 public class JMeterTreeModel extends DefaultTreeModel {
 
     private static final long serialVersionUID = 240L;
 
     public JMeterTreeModel(TestElement tp, TestElement wb) {
         super(new JMeterTreeNode(wb, null));
         initTree(tp,wb);
     }
 
     public JMeterTreeModel() {
         this(new TestPlanGui().createTestElement(),new WorkBenchGui().createTestElement());
 //        super(new JMeterTreeNode(new WorkBenchGui().createTestElement(), null));
 //        TestElement tp = new TestPlanGui().createTestElement();
 //        initTree(tp);
     }
 
     /**
      * Hack to allow TreeModel to be used in non-GUI and headless mode.
      *
      * @deprecated - only for use by JMeter class!
      * @param o - dummy
      */
     @Deprecated
     public JMeterTreeModel(Object o) {
         this(new TestPlan(),new WorkBench());
 //      super(new JMeterTreeNode(new WorkBench(), null));
 //      TestElement tp = new TestPlan();
 //      initTree(tp, new WorkBench());
     }
 
     /**
      * Returns a list of tree nodes that hold objects of the given class type.
      * If none are found, an empty list is returned.
+     * @param type The type of nodes, which are to be collected
+     * @return a list of tree nodes of the given <code>type</code>, or an empty list
      */
     public List<JMeterTreeNode> getNodesOfType(Class<?> type) {
         List<JMeterTreeNode> nodeList = new LinkedList<JMeterTreeNode>();
         traverseAndFind(type, (JMeterTreeNode) this.getRoot(), nodeList);
         return nodeList;
     }
 
     /**
      * Get the node for a given TestElement object.
+     * @param userObject The object to be found in this tree
+     * @return the node corresponding to the <code>userObject</code>
      */
     public JMeterTreeNode getNodeOf(TestElement userObject) {
         return traverseAndFind(userObject, (JMeterTreeNode) getRoot());
     }
 
     /**
      * Adds the sub tree at the given node. Returns a boolean indicating whether
      * the added sub tree was a full test plan.
+     * 
+     * @param subTree
+     *            The {@link HashTree} which is to be inserted into
+     *            <code>current</code>
+     * @param current
+     *            The node in which the <code>subTree</code> is to be inserted.
+     *            Will be overridden, when an instance of {@link TestPlan} or
+     *            {@link WorkBench} is found in the subtree.
+     * @return newly created sub tree now found at <code>current</code>
+     * @throws IllegalUserActionException
+     *             when <code>current</code> is not an instance of
+     *             {@link AbstractConfigGui} and no instance of {@link TestPlan}
+     *             or {@link WorkBench} could be found in the
+     *             <code>subTree</code>
      */
     public HashTree addSubTree(HashTree subTree, JMeterTreeNode current) throws IllegalUserActionException {
         Iterator<Object> iter = subTree.list().iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
             if (item instanceof TestPlan) {
                 TestPlan tp = (TestPlan) item;
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(0);
                 final TestPlan userObject = (TestPlan) current.getUserObject();
                 userObject.addTestElement(item);
                 userObject.setName(item.getName());
                 userObject.setFunctionalMode(tp.isFunctionalMode());
                 userObject.setSerialized(tp.isSerialized());
                 addSubTree(subTree.getTree(item), current);
             } else if (item instanceof WorkBench) {
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(1);
                 final TestElement testElement = ((TestElement) current.getUserObject());
                 testElement.addTestElement(item);
                 testElement.setName(item.getName());
                 addSubTree(subTree.getTree(item), current);
             } else {
                 addSubTree(subTree.getTree(item), addComponent(item, current));
             }
         }
         return getCurrentSubTree(current);
     }
 
+    /**
+     * Add a {@link TestElement} to a {@link JMeterTreeNode}
+     * @param component The {@link TestElement} to be used as data for the newly created note
+     * @param node The {@link JMeterTreeNode} into which the newly created node is to be inserted
+     * @return new {@link JMeterTreeNode} for the given <code>component</code>
+     * @throws IllegalUserActionException
+     *             when the user object for the <code>node</code> is not an instance
+     *             of {@link AbstractConfigGui}
+     */
     public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
         if (node.getUserObject() instanceof AbstractConfigGui) {
             throw new IllegalUserActionException("This node cannot hold sub-elements");
         }
 
         GuiPackage guiPackage = GuiPackage.getInstance();
         if (guiPackage != null) {
             // The node can be added in non GUI mode at startup
             guiPackage.updateCurrentNode();
             JMeterGUIComponent guicomp = guiPackage.getGui(component);
             guicomp.configure(component);
             guicomp.modifyTestElement(component);
             guiPackage.getCurrentGui(); // put the gui object back
                                         // to the way it was.
         }
         JMeterTreeNode newNode = new JMeterTreeNode(component, this);
 
         // This check the state of the TestElement and if returns false it
         // disable the loaded node
         try {
             newNode.setEnabled(component.isEnabled());
-        } catch (Exception e) { // TODO - can this eever happen?
+        } catch (Exception e) { // TODO - can this ever happen?
             newNode.setEnabled(true);
         }
 
         this.insertNodeInto(newNode, node, node.getChildCount());
         return newNode;
     }
 
     public void removeNodeFromParent(JMeterTreeNode node) {
         if (!(node.getUserObject() instanceof TestPlan) && !(node.getUserObject() instanceof WorkBench)) {
             super.removeNodeFromParent(node);
         }
     }
 
     private void traverseAndFind(Class<?> type, JMeterTreeNode node, List<JMeterTreeNode> nodeList) {
         if (type.isInstance(node.getUserObject())) {
             nodeList.add(node);
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             traverseAndFind(type, child, nodeList);
         }
     }
 
     private JMeterTreeNode traverseAndFind(TestElement userObject, JMeterTreeNode node) {
         if (userObject == node.getUserObject()) {
             return node;
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             JMeterTreeNode result = traverseAndFind(userObject, child);
             if (result != null) {
                 return result;
             }
         }
         return null;
     }
 
+    /**
+     * Get the current sub tree for a {@link JMeterTreeNode}
+     * @param node The {@link JMeterTreeNode} from which the sub tree is to be taken 
+     * @return newly copied sub tree
+     */
     public HashTree getCurrentSubTree(JMeterTreeNode node) {
         ListedHashTree hashTree = new ListedHashTree(node);
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             hashTree.add(node, getCurrentSubTree(child));
         }
         return hashTree;
     }
 
+    /**
+     * Get the {@link TestPlan} from the root of this tree
+     * @return The {@link TestPlan} found at the root of this tree
+     */
     public HashTree getTestPlan() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(0));
     }
 
+    /**
+     * Get the {@link WorkBench} from the root of this tree
+     * @return The {@link WorkBench} found at the root of this tree
+     */
     public HashTree getWorkBench() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(1));
     }
 
     /**
      * Clear the test plan, and use default node for test plan and workbench.
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan()}
      */
     public void clearTestPlan() {
         TestElement tp = new TestPlanGui().createTestElement();
         clearTestPlan(tp);
     }
 
     /**
      * Clear the test plan, and use specified node for test plan and default node for workbench
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan(TestElement)}
      *
      * @param testPlan the node to use as the testplan top node
      */
     public void clearTestPlan(TestElement testPlan) {
         // Remove the workbench and testplan nodes
         int children = getChildCount(getRoot());
         while (children > 0) {
             JMeterTreeNode child = (JMeterTreeNode)getChild(getRoot(), 0);
             super.removeNodeFromParent(child);
             children = getChildCount(getRoot());
         }
         // Init the tree
         initTree(testPlan,new WorkBenchGui().createTestElement()); // Assumes this is only called from GUI mode
     }
 
     /**
      * Initialize the model with nodes for testplan and workbench.
      *
      * @param tp the element to use as testplan
      * @param wb the element to use as workbench
      */
     private void initTree(TestElement tp, TestElement wb) {
         // Insert the test plan node
         insertNodeInto(new JMeterTreeNode(tp, this), (JMeterTreeNode) getRoot(), 0);
         // Insert the workbench node
         insertNodeInto(new JMeterTreeNode(wb, this), (JMeterTreeNode) getRoot(), 1);
         // Let others know that the tree content has changed.
         // This should not be necessary, but without it, nodes are not shown when the user
         // uses the Close menu item
         nodeStructureChanged((JMeterTreeNode)getRoot());
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
index 89c14ba18..cd6f9c9bd 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
@@ -1,202 +1,203 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.awt.Image;
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.List;
 
 import javax.swing.ImageIcon;
 import javax.swing.JPopupMenu;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.TreeNode;
 
 import org.apache.jmeter.gui.GUIFactory;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterTreeNode extends DefaultMutableTreeNode implements NamedTreeNode {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int TEST_PLAN_LEVEL = 1;
 
     // See Bug 54648
     private transient JMeterTreeModel treeModel;
 
     private boolean markedBySearch;
 
     public JMeterTreeNode() {// Allow serializable test to work
         // TODO: is the serializable test necessary now that JMeterTreeNode is
         // no longer a GUI component?
         this(null, null);
     }
 
     public JMeterTreeNode(TestElement userObj, JMeterTreeModel treeModel) {
         super(userObj);
         this.treeModel = treeModel;
     }
 
     public boolean isEnabled() {
         return getTestElement().isEnabled();
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setEnabled(enabled);
         treeModel.nodeChanged(this);
     }
     
     /**
      * Return nodes to level 2
      * @return {@link List} of {@link JMeterTreeNode}s
      */
     public List<JMeterTreeNode> getPathToThreadGroup() {
         List<JMeterTreeNode> nodes = new ArrayList<JMeterTreeNode>();
         if(treeModel != null) {
             TreeNode[] nodesToRoot = treeModel.getPathToRoot(this);
             for (TreeNode node : nodesToRoot) {
                 JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) node;
                 int level = jMeterTreeNode.getLevel();
                 if(level<TEST_PLAN_LEVEL) {
                     continue;
                 } else {
                     nodes.add(jMeterTreeNode);
                 }
             }
         }
         return nodes;
     }
     
     /**
      * Tag Node as result of a search
+     * @param tagged The flag to be used for tagging
      */
     public void setMarkedBySearch(boolean tagged) {
         this.markedBySearch = tagged;
         treeModel.nodeChanged(this);
     }
     
     /**
      * Node is markedBySearch by a search
      * @return true if marked by search
      */
     public boolean isMarkedBySearch() {
         return this.markedBySearch;
     }
 
     public ImageIcon getIcon() {
         return getIcon(true);
     }
 
     public ImageIcon getIcon(boolean enabled) {
         TestElement testElement = getTestElement();
         try {
             if (testElement instanceof TestBean) {
                 Class<?> testClass = testElement.getClass();
                 try {
                     Image img = Introspector.getBeanInfo(testClass).getIcon(BeanInfo.ICON_COLOR_16x16);
                     // If icon has not been defined, then use GUI_CLASS property
                     if (img == null) {
                         Object clazz = Introspector.getBeanInfo(testClass).getBeanDescriptor()
                                 .getValue(TestElement.GUI_CLASS);
                         if (clazz == null) {
                             log.warn("getIcon(): Can't obtain GUI class from " + testClass.getName());
                             return null;
                         }
                         return GUIFactory.getIcon(Class.forName((String) clazz), enabled);
                     }
                     return new ImageIcon(img);
                 } catch (IntrospectionException e1) {
                     log.error("Can't obtain icon for class "+testElement, e1);
                     throw new org.apache.jorphan.util.JMeterError(e1);
                 }
             }
             return GUIFactory.getIcon(Class.forName(testElement.getPropertyAsString(TestElement.GUI_CLASS)),
                         enabled);
         } catch (ClassNotFoundException e) {
             log.warn("Can't get icon for class " + testElement, e);
             return null;
         }
     }
 
     public Collection<String> getMenuCategories() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).getMenuCategories();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public JPopupMenu createPopupMenu() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).createPopupMenu();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public TestElement getTestElement() {
         return (TestElement) getUserObject();
     }
 
     public String getStaticLabel() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getStaticLabel();
     }
 
     public String getDocAnchor() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getDocAnchor();
     }
 
     /** {@inheritDoc} */
     @Override
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
     @Override
     public void nameChanged() {
         if (treeModel != null) { // may be null during startup
             treeModel.nodeChanged(this);
         }
     }
 
     // Override in order to provide type safety
     @Override
     @SuppressWarnings("unchecked")
     public Enumeration<JMeterTreeNode> children() {
         return super.children();
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/FileListPanel.java b/src/core/org/apache/jmeter/gui/util/FileListPanel.java
index ca7fcffee..f7e5ab0d0 100644
--- a/src/core/org/apache/jmeter/gui/util/FileListPanel.java
+++ b/src/core/org/apache/jmeter/gui/util/FileListPanel.java
@@ -1,219 +1,221 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.File;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterFileFilter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.reflect.Functor;
 
 public class FileListPanel extends JPanel implements ActionListener {
 
     private static final long serialVersionUID = 1L;
 
     private JTable files = null;
 
     private transient ObjectTableModel tableModel = null;
 
     private static final String ACTION_BROWSE = "browse"; // $NON-NLS-1$
 
     private static final String LABEL_LIBRARY = "library"; // $NON-NLS-1$
 
     private JButton browse = new JButton(JMeterUtils.getResString(ACTION_BROWSE));
 
     private JButton clear = new JButton(JMeterUtils.getResString("clear")); // $NON-NLS-1$
 
     private JButton delete = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
 
     private List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private String title;
 
     private String filetype;
 
     /**
      * Constructor for the FilePanel object.
      */
     public FileListPanel() {
         title = ""; // $NON-NLS-1$
         init();
     }
 
     public FileListPanel(String title) {
         this.title = title;
         init();
     }
 
     public FileListPanel(String title, String filetype) {
         this.title = title;
         this.filetype = filetype;
         init();
     }
 
     /**
      * Constructor for the FilePanel object.
+     * @param l The changelistener for this panel
+     * @param title The title of this panel
      */
     public FileListPanel(ChangeListener l, String title) {
         this.title = title;
         init();
         listeners.add(l);
     }
 
     public void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         this.setLayout(new BorderLayout(0, 5));
         setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
         JLabel jtitle = new JLabel(title);
 
         HorizontalPanel buttons = new HorizontalPanel();
         buttons.add(jtitle);
         buttons.add(browse);
         buttons.add(delete);
         buttons.add(clear);
         add(buttons,BorderLayout.NORTH);
 
         this.initializeTableModel();
         files = new JTable(tableModel);
         files.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         files.revalidate();
 
         JScrollPane scrollpane = new JScrollPane(files);
         scrollpane.setPreferredSize(new Dimension(100,80));
         add(scrollpane,BorderLayout.CENTER);
 
         browse.setActionCommand(ACTION_BROWSE); // $NON-NLS-1$
         browse.addActionListener(this);
         clear.addActionListener(this);
         delete.addActionListener(this);
         //this.setPreferredSize(new Dimension(400,150));
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
-     * @param enable
+     * @param enable Flag whether FilePanel should be enabled
      */
     public void enableFile(boolean enable) {
         browse.setEnabled(enable);
         files.setEnabled(false);
     }
 
     /**
      * Add a single file to the table
-     * @param f
+     * @param f The name of the file to be added
      */
     public void addFilename(String f) {
         tableModel.addRow(f);
     }
 
     /**
      * clear the files from the table
      */
     public void clearFiles() {
         tableModel.clearData();
     }
 
     public void setFiles(String[] files) {
         this.clearFiles();
         for (String file : files) {
             addFilename(file);
         }
     }
 
     public String[] getFiles() {
         String[] _files = new String[tableModel.getRowCount()];
         for (int idx=0; idx < _files.length; idx++) {
             _files[idx] = (String)tableModel.getValueAt(idx,0);
         }
         return _files;
     }
 
     protected void deleteFile() {
         // If a table cell is being edited, we must cancel the editing before
         // deleting the row
 
         int rowSelected = files.getSelectedRow();
         if (rowSelected >= 0) {
             tableModel.removeRow(rowSelected);
             tableModel.fireTableDataChanged();
 
         }
     }
 
     private void fireFileChanged() {
         for (ChangeListener cl : listeners) {
             cl.stateChanged(new ChangeEvent(this));
         }
     }
 
     protected void initializeTableModel() {
         tableModel = new ObjectTableModel(new String[] { JMeterUtils.getResString(LABEL_LIBRARY) },
                 new Functor[0] , new Functor[0] , // i.e. bypass the Functors
                 new Class[] { String.class });
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getSource() == clear) {
             this.clearFiles();
         } else if (e.getActionCommand().equals(ACTION_BROWSE)) {
             JFileChooser chooser = new JFileChooser();
             String start = System.getProperty("user.dir", ""); // $NON-NLS-1$ // $NON-NLS-2$
             chooser.setCurrentDirectory(new File(start));
             chooser.setFileFilter(new JMeterFileFilter(new String[] { filetype }));
             chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
             chooser.setMultiSelectionEnabled(true);
             chooser.showOpenDialog(GuiPackage.getInstance().getMainFrame());
             File[] cfiles = chooser.getSelectedFiles();
             if (cfiles != null) {
                 for (int idx=0; idx < cfiles.length; idx++) {
                     this.addFilename(cfiles[idx].getPath());
                 }
                 fireFileChanged();
             }
         } else if (e.getSource() == delete) {
             this.deleteFile();
         } else {
             fireFileChanged();
         }
     }
 }
