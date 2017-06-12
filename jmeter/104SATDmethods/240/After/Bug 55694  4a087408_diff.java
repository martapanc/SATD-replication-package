diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index 841a7703a..b10524ff3 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,547 +1,548 @@
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
 
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.Document;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 // @see org.apache.jmeter.assertions.ResponseAssertionTest for unit tests
 
 /**
  * Test element to handle Response Assertions, @see AssertionGui
  */
 public class ResponseAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
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
 
     /*
      * Mask values for TEST_TYPE TODO: remove either MATCH or CONTAINS - they
      * are mutually exckusive
      */
     private static final int MATCH = 1 << 0;
 
     private static final int CONTAINS = 1 << 1;
 
     private static final int NOT = 1 << 2;
 
     private static final int EQUALS = 1 << 3;
 
     private static final int SUBSTRING = 1 << 4;
 
     // Mask should contain all types (but not NOT)
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
         AssertionResult result;
 
         // None of the other Assertions check the response status, so remove
         // this check
         // for the time being, at least...
         // if (!response.isSuccessful())
         // {
         // result = new AssertionResult();
         // result.setError(true);
         // byte [] ba = response.getResponseData();
         // result.setFailureMessage(
         // ba == null ? "Unknown Error (responseData is empty)" : new String(ba)
         // );
         // return result;
         // }
 
         result = evaluateResponse(response);
         return result;
     }
 
     /***************************************************************************
      * !ToDoo (Method description)
      *
      * @return !ToDo (Return description)
      **************************************************************************/
     public String getTestField() {
         return getPropertyAsString(TEST_FIELD);
     }
 
     /***************************************************************************
      * !ToDoo (Method description)
      *
      * @return !ToDo (Return description)
      **************************************************************************/
     public int getTestType() {
         JMeterProperty type = getProperty(TEST_TYPE);
         if (type instanceof NullProperty) {
             return CONTAINS;
         }
         return type.getIntValue();
     }
 
     /***************************************************************************
      * !ToDoo (Method description)
      *
      * @return !ToDo (Return description)
      **************************************************************************/
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
         setTestType((getTestType() | NOT));
     }
 
     public void unsetNotType() {
         setTestType(getTestType() & ~NOT);
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
         String toCheck = ""; // The string to check (Url or data)
 
         if (getAssumeSuccess()) {
             response.setSuccessful(true);// Allow testing of failure codes
         }
 
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
         boolean contains = isContainsType(); // do it once outside loop
         boolean equals = isEqualsType();
         boolean substring = isSubstringType();
         boolean matches = isMatchType();
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Type:" + (contains?"Contains":"Match") + (notTest? "(not)": ""));
         }
 
-        if (toCheck.length() == 0) {
+        if (StringUtils.isEmpty(toCheck)) {
             if (notTest) { // Not should always succeed against an empty result
                 return result;
             }
             if (debugEnabled){
                 log.debug("Not checking empty response field in: "+response.getSampleLabel());
             }
             return result.setResultForNull();
         }
 
         boolean pass = true;
         try {
             // Get the Matcher for this thread
             Perl5Matcher localMatcher = JMeterUtils.getMatcher();
             PropertyIterator iter = getTestStrings().iterator();
             while (iter.hasNext()) {
                 String stringPattern = iter.next().getStringValue();
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
                     found = toCheck.indexOf(stringPattern) != -1;
                 } else {
                     found = localMatcher.matches(toCheck, pattern);
                 }
                 pass = notTest ? !found : found;
                 if (!pass) {
                     if (debugEnabled){log.debug("Failed: "+stringPattern);}
                     result.setFailure(true);
                     result.setFailureMessage(getFailText(stringPattern,toCheck));
                     break;
                 }
                 if (debugEnabled){log.debug("Passed: "+stringPattern);}
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
     // TODO strings should be resources
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
         int                     firstDiff;
         int                     lastRecDiff = -1;
         int                     lastCompDiff = -1;
         final int               recLength = received.length();
         final int               compLength = comparison.length();
         final int               minLength = Math.min(recLength, compLength);
         final String            startingEqSeq;
         String                  recDeltaSeq = "";
         String                  compDeltaSeq = "";
         String                  endingEqSeq = "";
 
         final StringBuilder text = new StringBuilder(Math.max(recLength, compLength) * 2);
         for (firstDiff = 0; firstDiff < minLength; firstDiff++) {
             if (received.charAt(firstDiff) != comparison.charAt(firstDiff)){
                 break;
             }
         }
         if (firstDiff == 0) {
             startingEqSeq = "";
         } else {
             startingEqSeq = trunc(false, received.substring(0, firstDiff));
         }
 
         lastRecDiff = recLength - 1;
         lastCompDiff = compLength - 1;
 
         while ((lastRecDiff > firstDiff) && (lastCompDiff > firstDiff)
                 && received.charAt(lastRecDiff) == comparison.charAt(lastCompDiff))
         {
             lastRecDiff--;
             lastCompDiff--;
         }
         endingEqSeq = trunc(true, received.substring(lastRecDiff + 1, recLength));
         if (endingEqSeq.length() == 0)
         {
             recDeltaSeq = trunc(true, received.substring(firstDiff, recLength));
             compDeltaSeq = trunc(true, comparison.substring(firstDiff, compLength));
         }
         else
         {
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
diff --git a/src/components/org/apache/jmeter/assertions/XPathAssertion.java b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
index e82c3de65..2ba1c9ae4 100644
--- a/src/components/org/apache/jmeter/assertions/XPathAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
@@ -1,270 +1,274 @@
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
 
+import org.apache.commons.lang3.StringUtils;
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
-                responseData = getThreadContext().getVariables().get(getVariableName()).getBytes("UTF-8");
+                String inputString=getThreadContext().getVariables().get(getVariableName());
+                if(!StringUtils.isEmpty(inputString)) {
+                    responseData = inputString.getBytes("UTF-8");
+                } 
             } else {
                 responseData = response.getResponseData();
             }
             
-            if (responseData.length == 0) {
+            if (responseData == null || responseData.length == 0) {
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
      * @param whitespace
      */
     public void setWhitespace(boolean whitespace) {
         setProperty(new BooleanProperty(WHITESPACE_KEY, whitespace));
     }
 
     /**
      * Set use validation
      *
      * @param validate
      */
     public void setValidating(boolean validate) {
         setProperty(new BooleanProperty(VALIDATE_KEY, validate));
     }
 
     /**
      * Set whether this is namespace aware
      *
      * @param namespace
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
diff --git a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
index a246d26fd..35e980bf9 100644
--- a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
@@ -1,293 +1,302 @@
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
+import java.util.Collections;
 import java.util.List;
 
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * 
  */
 public class HtmlExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
     public static final String EXTRACTOR_JSOUP = "JSOUP"; //$NON-NLS-1$
 
     public static final String EXTRACTOR_JODD = "JODD"; //$NON-NLS-1$
 
     public static String[] getImplementations(){
         return new String[]{EXTRACTOR_JSOUP,EXTRACTOR_JODD};
     }
 
     public static final String DEFAULT_EXTRACTOR = ""; // $NON-NLS-1$
 
     /**
      * 
      */
     private static final long serialVersionUID = 3978073849365558131L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String EXPRESSION = "HtmlExtractor.expr"; // $NON-NLS-1$
 
     private static final String ATTRIBUTE = "HtmlExtractor.attribute"; // $NON-NLS-1$
 
     private static final String REFNAME = "HtmlExtractor.refname"; // $NON-NLS-1$
 
     private static final String MATCH_NUMBER = "HtmlExtractor.match_number"; // $NON-NLS-1$
 
     private static final String DEFAULT = "HtmlExtractor.default"; // $NON-NLS-1$
 
     private static final String EXTRACTOR_IMPL = "HtmlExtractor.extractor_impl"; // $NON-NLS-1$
 
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
     
     private static final String UNDERSCORE = "_";  // $NON-NLS-1$
     
     private Extractor extractor;
 
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
         log.debug("HtmlExtractor processing result");
 
         // Fetch some variables
         JMeterVariables vars = context.getVariables();
         
         String refName = getRefName();
         String expression = getExpression();
         String attribute = getAttribute();
         int matchNumber = getMatchNumber();
         final String defaultValue = getDefaultValue();
         
         if (defaultValue.length() > 0){// Only replace default if it is provided
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
                 } catch (NumberFormatException e1) {
                     log.warn("Could not parse "+prevString+" "+e1);
                 }
             }
             int matchCount=0;// Number of refName_n variable sets to keep
             try {
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
                             final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                             vars.put(refName_n, match);
                         }
                     }
                 }
                 // Remove any left-over variables
                 for (int i = matchCount + 1; i <= prevCount; i++) {
                     final String refName_n = new StringBuilder(refName).append(UNDERSCORE).append(i).toString();
                     vars.remove(refName_n);
                 }
             } catch (RuntimeException e) {
                 log.warn("Error while generating result");
             }
 
         } catch (RuntimeException e) {
             log.warn("Error while generating result");
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
         List<String> result = new ArrayList<String>();
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
-            getExtractorImpl().extract(expression, attribute, matchNumber, inputString, result, found, "-1");
+            if(!StringUtils.isEmpty(inputString)) {
+                getExtractorImpl().extract(expression, attribute, matchNumber, inputString, result, found, "-1");
+            } else {
+                if(inputString==null) {
+                    log.warn("No variable '"+getVariableName()+"' found to process by Css/JQuery Extractor '"+getName()+"', skipping processing");
+                }
+                return Collections.emptyList();
+            } 
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
     public static final Extractor getExtractorImpl(String impl) {
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
     
 
     public void setExtractor(String attribute) {
         setProperty(EXTRACTOR_IMPL, attribute);
     }
 
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
      * exact match to use, or 0, which is interpreted as meaning random.
      *
      * @param matchNumber
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
      * @param defaultValue
      */
     public void setDefaultValue(String defaultValue) {
         setProperty(DEFAULT, defaultValue);
     }
 
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index e74b6fcc5..e8f78a14e 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,472 +1,472 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcher;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 // @see org.apache.jmeter.extractor.TestRegexExtractor for unit tests
 
 public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
 
     private static final String TEMPLATE = "RegexExtractor.template"; // $NON-NLS-1$
 
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
 
     private static final String UNDERSCORE = "_";  // $NON-NLS-1$
 
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
         if (defaultValue.length() > 0){// Only replace default if it is provided
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
                 } catch (NumberFormatException e1) {
                     log.warn("Could not parse "+prevString+" "+e1);
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
             log.error("Error in pattern: " + regex);
         } finally {
             JMeterUtils.clearMatcherMemory(matcher, pattern);
         }
     }
 
     private String getInputString(SampleResult result) {
         String inputString = useUrl() ? result.getUrlAsString() // Bug 39707
                 : useHeaders() ? result.getResponseHeaders()
                 : useCode() ? result.getResponseCode() // Bug 43451
                 : useMessage() ? result.getResponseMessage() // Bug 43451
                 : useUnescapedBody() ? StringEscapeUtils.unescapeHtml4(result.getResponseDataAsString())
                 : useBodyAsDocument() ? Document.getTextFromDocument(result.getResponseData())
                 : result.getResponseDataAsString() // Bug 36898
                 ;
        if (log.isDebugEnabled()) {
            log.debug("Input = " + inputString);
        }
        return inputString;
     }
 
     private List<MatchResult> processMatches(Pattern pattern, String regex, SampleResult result, int matchNumber, JMeterVariables vars) {
         if (log.isDebugEnabled()) {
             log.debug("Regex = " + regex);
         }
 
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         List<MatchResult> matches = new ArrayList<MatchResult>();
         int found = 0;
 
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
             if(inputString == null) {
-                log.warn("No variable '"+getVariableName()+"' found to process by RegexExtractor "+getName()+", skipping processing");
+                log.warn("No variable '"+getVariableName()+"' found to process by RegexExtractor '"+getName()+"', skipping processing");
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
             } catch (NumberFormatException e) {
                 log.warn("Could not parse "+prevString+" "+e);
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
             if (log.isDebugEnabled()) {
                 log.debug("RegexExtractor: Template piece " + obj + " (" + obj.getClass().getSimpleName() + ")");
             }
             if (obj instanceof Integer) {
                 result.append(match.group(((Integer) obj).intValue()));
             } else {
                 result.append(obj);
             }
         }
         if (log.isDebugEnabled()) {
             log.debug("Regex Extractor result = " + result.toString());
         }
         return result.toString();
     }
 
     private void initTemplate() {
         if (template != null) {
             return;
         }
         // Contains Strings and Integers
         List<Object> combined = new ArrayList<Object>();
         String rawTemplate = getTemplate();
         PatternMatcher matcher = JMeterUtils.getMatcher();
         Pattern templatePattern = JMeterUtils.getPatternCache().getPattern("\\$(\\d+)\\$"  // $NON-NLS-1$
                 , Perl5Compiler.READ_ONLY_MASK
                 & Perl5Compiler.SINGLELINE_MASK);
         if (log.isDebugEnabled()) {
             log.debug("Pattern = " + templatePattern.getPattern());
             log.debug("template = " + rawTemplate);
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
         if (log.isDebugEnabled()){
             log.debug("Template item count: "+combined.size());
             for(Object o : combined){
                 log.debug(o.getClass().getSimpleName()+" '"+o.toString()+"'");
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
 
     public void setRegex(String regex) {
         setProperty(REGEX, regex);
     }
 
     public String getRegex() {
         return getPropertyAsString(REGEX);
     }
 
     public void setRefName(String refName) {
         setProperty(REFNAME, refName);
     }
 
     public String getRefName() {
         return getPropertyAsString(REFNAME);
     }
 
     /**
      * Set which Match to use. This can be any positive number, indicating the
      * exact match to use, or 0, which is interpreted as meaning random.
      *
      * @param matchNumber
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
      * @param defaultValue
      */
     public void setDefaultValue(String defaultValue) {
         setProperty(DEFAULT, defaultValue);
     }
 
     public String getDefaultValue() {
         return getPropertyAsString(DEFAULT);
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
index 9aace6989..a767dcb3c 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,336 +1,342 @@
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
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.TidyException;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 //@see org.apache.jmeter.extractor.TestXPathExtractor for unit tests
 
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
  */
  /* This file is inspired by RegexExtractor.
  * author <a href="mailto:hpaluch@gitus.cz">Henryk Paluch</a>
  *            of <a href="http://www.gitus.com">Gitus a.s.</a>
  *
  * See Bugzilla: 37183
  */
 public class XPathExtractor extends AbstractScopedTestElement implements
         PostProcessor, Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String MATCH_NR = "matchNr"; // $NON-NLS-1$
 
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
         final String matchNR = concat(refName,MATCH_NR);
         int prevCount=0; // number of previous matches
         try {
             prevCount=Integer.parseInt(vars.get(matchNR));
         } catch (NumberFormatException e) {
             // ignored
         }
         vars.put(matchNR, "0"); // In case parse fails // $NON-NLS-1$
         vars.remove(concat(refName,"1")); // In case parse fails // $NON-NLS-1$
 
         List<String> matches = new ArrayList<String>();
         try{
             if (isScopeVariable()){
                 String inputString=vars.get(getVariableName());
-                Document d =  parseResponse(inputString);
-                getValuesForXPath(d,getXPathQuery(),matches);
+                if(inputString != null) {
+                    if(inputString.length()>0) {
+                        Document d =  parseResponse(inputString);
+                        getValuesForXPath(d,getXPathQuery(),matches);
+                    }
+                } else {
+                    log.warn("No variable '"+getVariableName()+"' found to process by XPathExtractor '"+getName()+"', skipping processing");
+                }
             } else {
                 List<SampleResult> samples = getSampleList(previousResult);
                 for (SampleResult res : samples) {
                     Document d = parseResponse(res.getResponseDataAsString());
                     getValuesForXPath(d,getXPathQuery(),matches);
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
             final String errorMessage = "IOException on ("+getXPathQuery()+")";
             log.error(errorMessage,e);
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
             log.warn("SAXException while processing ("+getXPathQuery()+") "+e.getLocalizedMessage());
             addAssertionFailure(previousResult, e, false); // Should this also fail the sample?
         } catch (TransformerException e) {// Can happen for incorrect XPath expression
             log.warn("TransformerException while processing ("+getXPathQuery()+") "+e.getLocalizedMessage());
             addAssertionFailure(previousResult, e, false);
         } catch (TidyException e) {
             // Will already have been logged by XPathUtil
             addAssertionFailure(previousResult, e, true); // fail the sample
         }
     }
 
     private void addAssertionFailure(final SampleResult previousResult,
             final Throwable thrown, final boolean setFailed) {
         AssertionResult ass = new AssertionResult(thrown.getClass().getSimpleName()); // $NON-NLS-1$
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
       throws UnsupportedEncodingException, IOException, ParserConfigurationException,SAXException,TidyException
     {
       //TODO: validate contentType for reasonable types?
 
       // NOTE: responseData encoding is server specific
       //       Therefore we do byte -> unicode -> byte conversion
       //       to ensure UTF-8 encoding as required by XPathUtil
       // convert unicode String -> UTF-8 bytes
       byte[] utf8data = unicodeData.getBytes("UTF-8"); // $NON-NLS-1$
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
      *
      * @throws TransformerException
      */
     private void getValuesForXPath(Document d,String query, List<String> matchStrings)
         throws TransformerException {
         XPathUtil.putValuesForXPathInList(d, query, matchStrings, getFragment());
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
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b76666962..00d15e053 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,222 +1,222 @@
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
 
 
 <!--  =================== 2.11 =================== -->
 
 <h1>Version 2.11</h1>
 
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
 
 <ch_category>Core Improvements</ch_category>
 
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
 
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
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
 <li></li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>55717</bugzilla> - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient31</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55685</bugzilla> - OS Sampler: timeout option don't save and restore correctly value and don't init correctly timeout</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
-<li><bugzilla>55694</bugzilla> - java.lang.NullPointerException if Apply to is set to a missing JMeter variable</li>
+<li><bugzilla>55694</bugzilla> - Assertions and Extractors : Avoid NullPointerException when scope is variable and variable is missing</li>
 <li><bugzilla>55721</bugzilla> - HTTP Cache Manager - no-store directive is wrongly interpreted</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>55739</bugzilla> - Remote Test : Total threads in GUI mode shows invalid total number of threads</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Proxy</h3>
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
 <li><bugzilla>55610</bugzilla> - View Results Tree : Add an XPath Tester</li>
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
 <li><bugzilla>55693</bugzilla> - Add a "Save as Test Fragment" option</li>
 <li><bugzilla>55753</bugzilla> - Improve FilePanel behaviour to start from the value set in Filename field if any. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55756</bugzilla> - HTTP Mirror Server : Add ability to set Headers</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above.<br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Firstname Name (email at gmail.com)</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
