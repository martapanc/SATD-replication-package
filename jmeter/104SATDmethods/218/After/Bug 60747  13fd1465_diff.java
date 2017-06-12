diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index 4cc32c2e9..b60e01c4d 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,539 +1,552 @@
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
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Test element to handle Response Assertions, @see AssertionGui
  * see org.apache.jmeter.assertions.ResponseAssertionTest for unit tests
  */
 public class ResponseAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
     private static final Logger log = LoggerFactory.getLogger(ResponseAssertion.class);
 
     private static final long serialVersionUID = 242L;
 
     private static final String TEST_FIELD = "Assertion.test_field";  // $NON-NLS-1$
 
     // Values for TEST_FIELD
     // N.B. we cannot change the text value as it is in test plans
     private static final String SAMPLE_URL = "Assertion.sample_label"; // $NON-NLS-1$
 
     private static final String RESPONSE_DATA = "Assertion.response_data"; // $NON-NLS-1$
 
     private static final String RESPONSE_DATA_AS_DOCUMENT = "Assertion.response_data_as_document"; // $NON-NLS-1$
 
     private static final String RESPONSE_CODE = "Assertion.response_code"; // $NON-NLS-1$
 
     private static final String RESPONSE_MESSAGE = "Assertion.response_message"; // $NON-NLS-1$
 
     private static final String RESPONSE_HEADERS = "Assertion.response_headers"; // $NON-NLS-1$
+    
+    private static final String REQUEST_HEADERS = "Assertion.request_headers"; // $NON-NLS-1$
 
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
+    
+    public void setTestFieldRequestHeaders() {
+        setTestField(REQUEST_HEADERS);
+    }
 
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
 
+    public boolean isTestFieldRequestHeaders(){
+        return REQUEST_HEADERS.equals(getTestField());
+    }
+    
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
+        } else if (isTestFieldRequestHeaders()) {
+            toCheck = response.getRequestHeaders();
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
 
         log.debug("Test Type Info: contains={}, notTest={}, orTest={}", contains, notTest, orTest);
 
         if (StringUtils.isEmpty(toCheck)) {
             if (notTest) { // Not should always succeed against an empty result
                 return result;
             }
             if(log.isDebugEnabled()) {
                 log.debug("Not checking empty response field in: {}", response.getSampleLabel());
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
                         log.debug("Failed: {}", stringPattern);
                         allCheckMessage.add(getFailText(stringPattern,toCheck));
                     } else {
                         hasTrue=true;
                         break;
                     }
                 } else {
                     if (!pass) {
                         log.debug("Failed: {}", stringPattern);
                         result.setFailure(true);
                         result.setFailureMessage(getFailText(stringPattern,toCheck));
                         break;
                     }
                     log.debug("Passed: {}", stringPattern);
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
+        } else if (isTestFieldRequestHeaders()) {
+            sb.append("request headers");
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
-
 }
diff --git a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
index 5762117eb..d2341edc9 100644
--- a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
+++ b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
@@ -1,521 +1,565 @@
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
 import java.awt.Dimension;
+import java.awt.GridBagConstraints;
+import java.awt.GridBagLayout;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.ButtonGroup;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
+import javax.swing.JToggleButton;
 import javax.swing.ListSelectionModel;
 
 import org.apache.jmeter.assertions.ResponseAssertion;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.gui.util.TextAreaCellRenderer;
 import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 
 /**
  * GUI interface for a {@link ResponseAssertion}.
  *
  */
 public class AssertionGui extends AbstractAssertionGui {
     private static final long serialVersionUID = 240L;
 
     /** The name of the table column in the list of patterns. */
     private static final String COL_RESOURCE_NAME = "assertion_patterns_to_test"; //$NON-NLS-1$
 
     /** Radio button indicating that the text response should be tested. */
     private JRadioButton responseStringButton;
 
     /** Radio button indicating that the text of a document should be tested. */
     private JRadioButton responseAsDocumentButton;
 
     /** Radio button indicating that the URL should be tested. */
     private JRadioButton urlButton;
 
     /** Radio button indicating that the responseMessage should be tested. */
     private JRadioButton responseMessageButton;
 
     /** Radio button indicating that the responseCode should be tested. */
     private JRadioButton responseCodeButton;
 
     /** Radio button indicating that the headers should be tested. */
     private JRadioButton responseHeadersButton;
+    
+    /** Radio button indicating that the request headers should be tested. */
+    private JRadioButton requestHeadersButton;
 
     /**
      * Checkbox to indicate whether the response should be forced successful
      * before testing. This is intended for use when checking the status code or
      * status message.
      */
     private JCheckBox assumeSuccess;
 
     /**
      * Radio button indicating to test if the field contains one of the
      * patterns.
      */
     private JRadioButton containsBox;
 
     /**
      * Radio button indicating to test if the field matches one of the patterns.
      */
     private JRadioButton matchesBox;
 
     /**
      * Radio button indicating if the field equals the string.
      */
     private JRadioButton equalsBox;
 
     /**
      * Radio button indicating if the field contains the string.
      */
     private JRadioButton substringBox;
 
     /**
      * Checkbox indicating to test that the field does NOT contain/match the
      * patterns.
      */
     private JCheckBox notBox;
     
     /**
      * Add new OR checkbox.
      */
     private JCheckBox orBox;
 
     /** A table of patterns to test against. */
     private JTable stringTable;
 
     /** Button to delete a pattern. */
     private JButton deletePattern;
 
     /** Table model for the pattern table. */
     private PowerTableModel tableModel;
 
     /**
      * Create a new AssertionGui panel.
      */
     public AssertionGui() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "assertion_title"; // $NON-NLS-1$
     }
 
     /* Implements JMeterGUIComponent.createTestElement() */
     @Override
     public TestElement createTestElement() {
         ResponseAssertion el = new ResponseAssertion();
         modifyTestElement(el);
         return el;
     }
 
     /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
     @Override
     public void modifyTestElement(TestElement el) {
         GuiUtils.stopTableEditing(stringTable);
         configureTestElement(el);
         if (el instanceof ResponseAssertion) {
             ResponseAssertion ra = (ResponseAssertion) el;
 
             saveScopeSettings(ra);
 
             ra.clearTestStrings();
             String[] testStrings = tableModel.getData().getColumn(COL_RESOURCE_NAME);
             for (String testString : testStrings) {
                 ra.addTestString(testString);
             }
 
             if (responseStringButton.isSelected()) {
                 ra.setTestFieldResponseData();
             } else if (responseAsDocumentButton.isSelected()) {
                 ra.setTestFieldResponseDataAsDocument();
             } else if (responseCodeButton.isSelected()) {
                 ra.setTestFieldResponseCode();
             } else if (responseMessageButton.isSelected()) {
                 ra.setTestFieldResponseMessage();
+            } else if (requestHeadersButton.isSelected()) {
+                ra.setTestFieldRequestHeaders();
             } else if (responseHeadersButton.isSelected()) {
                 ra.setTestFieldResponseHeaders();
             } else { // Assume URL
                 ra.setTestFieldURL();
             }
 
             ra.setAssumeSuccess(assumeSuccess.isSelected());
 
             if (containsBox.isSelected()) {
                 ra.setToContainsType();
             } else if (equalsBox.isSelected()) {
                 ra.setToEqualsType();
             } else if (substringBox.isSelected()) {
                 ra.setToSubstringType();
             } else {
                 ra.setToMatchType();
             }
 
             if (notBox.isSelected()) {
                 ra.setToNotType();
             } else {
                 ra.unsetNotType();
             }
 
             if (orBox.isSelected()) {
                 ra.setToOrType();
             } else {
                 ra.unsetOrType();
             }
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         GuiUtils.stopTableEditing(stringTable);
         tableModel.clearData();
 
         responseStringButton.setSelected(true);
         urlButton.setSelected(false);
         responseCodeButton.setSelected(false);
         responseMessageButton.setSelected(false);
+        requestHeadersButton.setSelected(false);
         responseHeadersButton.setSelected(false);
         assumeSuccess.setSelected(false);
 
         substringBox.setSelected(true);
         notBox.setSelected(false);
         orBox.setSelected(false);
     }
 
     /**
      * A newly created component can be initialized with the contents of a Test
      * Element object by calling this method. The component is responsible for
      * querying the Test Element object for the relevant information to display
      * in its GUI.
      *
      * @param el
      *            the TestElement to configure
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         ResponseAssertion model = (ResponseAssertion) el;
 
         showScopeSettings(model, true);
 
         if (model.isContainsType()) {
             containsBox.setSelected(true);
         } else if (model.isEqualsType()) {
             equalsBox.setSelected(true);
         } else if (model.isSubstringType()) {
             substringBox.setSelected(true);
         } else {
             matchesBox.setSelected(true);
         }
 
         notBox.setSelected(model.isNotType());
         orBox.setSelected(model.isOrType());
 
         if (model.isTestFieldResponseData()) {
             responseStringButton.setSelected(true);
         } else if (model.isTestFieldResponseDataAsDocument()) {
             responseAsDocumentButton.setSelected(true);
         } else if (model.isTestFieldResponseCode()) {
             responseCodeButton.setSelected(true);
         } else if (model.isTestFieldResponseMessage()) {
             responseMessageButton.setSelected(true);
+        } else if (model.isTestFieldRequestHeaders()) {
+            requestHeadersButton.setSelected(true);
         } else if (model.isTestFieldResponseHeaders()) {
             responseHeadersButton.setSelected(true);
         } else // Assume it is the URL
         {
             urlButton.setSelected(true);
         }
 
         assumeSuccess.setSelected(model.getAssumeSuccess());
 
         tableModel.clearData();
         for (JMeterProperty jMeterProperty : model.getTestStrings()) {
             tableModel.addRow(new Object[] { jMeterProperty.getStringValue() });
         }
 
         if (model.getTestStrings().size() == 0) {
             deletePattern.setEnabled(false);
         } else {
             deletePattern.setEnabled(true);
         }
 
         tableModel.fireTableDataChanged();
     }
 
     /**
      * Initialize the GUI components and layout.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
         Box box = Box.createVerticalBox();
         setBorder(makeBorder());
 
         box.add(makeTitlePanel());
         box.add(createScopePanel(true));
         box.add(createFieldPanel());
         box.add(createTypePanel());
         add(box, BorderLayout.NORTH);
         add(createStringPanel(), BorderLayout.CENTER);
     }
 
     /**
      * Create a panel allowing the user to choose which response field should be
      * tested.
      *
      * @return a new panel for selecting the response field
      */
     private JPanel createFieldPanel() {
-        JPanel panel = new JPanel();
-        panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("assertion_resp_field"))); //$NON-NLS-1$
-
         responseStringButton = new JRadioButton(JMeterUtils.getResString("assertion_text_resp")); //$NON-NLS-1$
         responseAsDocumentButton = new JRadioButton(JMeterUtils.getResString("assertion_text_document")); //$NON-NLS-1$
         urlButton = new JRadioButton(JMeterUtils.getResString("assertion_url_samp")); //$NON-NLS-1$
         responseCodeButton = new JRadioButton(JMeterUtils.getResString("assertion_code_resp")); //$NON-NLS-1$
         responseMessageButton = new JRadioButton(JMeterUtils.getResString("assertion_message_resp")); //$NON-NLS-1$
         responseHeadersButton = new JRadioButton(JMeterUtils.getResString("assertion_headers")); //$NON-NLS-1$
+        requestHeadersButton = new JRadioButton(JMeterUtils.getResString("assertion_req_headers")); //$NON-NLS-1$
 
         ButtonGroup group = new ButtonGroup();
         group.add(responseStringButton);
         group.add(responseAsDocumentButton);
         group.add(urlButton);
         group.add(responseCodeButton);
         group.add(responseMessageButton);
+        group.add(requestHeadersButton);
         group.add(responseHeadersButton);
-
-        panel.add(responseStringButton);
-        panel.add(responseAsDocumentButton);
-        panel.add(urlButton);
-        panel.add(responseCodeButton);
-        panel.add(responseMessageButton);
-        panel.add(responseHeadersButton);
-
+        
         responseStringButton.setSelected(true);
 
         assumeSuccess = new JCheckBox(JMeterUtils.getResString("assertion_assume_success")); //$NON-NLS-1$
-        panel.add(assumeSuccess);
 
+        GridBagLayout gridBagLayout = new GridBagLayout();
+        GridBagConstraints gbc = new GridBagConstraints();
+        initConstraints(gbc);
+
+        JPanel panel = new JPanel(gridBagLayout);
+        panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("assertion_resp_field"))); //$NON-NLS-1$
+
+        addField(panel, responseStringButton, gbc);
+        addField(panel, responseCodeButton, gbc);
+        addField(panel, responseMessageButton, gbc);
+        addField(panel, responseHeadersButton, gbc);
+
+        resetContraints(gbc);
+        addField(panel, requestHeadersButton, gbc);
+        addField(panel, urlButton, gbc);
+        addField(panel, responseAsDocumentButton, gbc);
+        addField(panel, assumeSuccess, gbc);
         return panel;
     }
+    
+    private void addField(JPanel panel, JToggleButton button, GridBagConstraints gbc) {
+        panel.add(button, gbc.clone());
+        gbc.gridx++;
+        gbc.fill=GridBagConstraints.HORIZONTAL;
+    }
+    
+    // Next line
+    private void resetContraints(GridBagConstraints gbc) {
+        gbc.gridx = 0;
+        gbc.gridy++;
+        gbc.fill=GridBagConstraints.NONE;
+    }
+
+    private void initConstraints(GridBagConstraints gbc) {
+        gbc.anchor = GridBagConstraints.NORTHWEST;
+        gbc.fill = GridBagConstraints.NONE;
+        gbc.gridheight = 1;
+        gbc.gridwidth = 1;
+        gbc.gridx = 0;
+        gbc.gridy = 0;
+        gbc.weightx = 1;
+        gbc.weighty = 1;
+    }
+
 
     /**
      * Create a panel allowing the user to choose what type of test should be
      * performed.
      *
      * @return a new panel for selecting the type of assertion test
      */
     private JPanel createTypePanel() {
         JPanel panel = new JPanel();
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("assertion_pattern_match_rules"))); //$NON-NLS-1$
 
         ButtonGroup group = new ButtonGroup();
 
         containsBox = new JRadioButton(JMeterUtils.getResString("assertion_contains")); //$NON-NLS-1$
         group.add(containsBox);
         containsBox.setSelected(true);
         panel.add(containsBox);
 
         matchesBox = new JRadioButton(JMeterUtils.getResString("assertion_matches")); //$NON-NLS-1$
         group.add(matchesBox);
         panel.add(matchesBox);
 
         equalsBox = new JRadioButton(JMeterUtils.getResString("assertion_equals")); //$NON-NLS-1$
         group.add(equalsBox);
         panel.add(equalsBox);
 
         substringBox = new JRadioButton(JMeterUtils.getResString("assertion_substring")); //$NON-NLS-1$
         group.add(substringBox);
         panel.add(substringBox);
 
         notBox = new JCheckBox(JMeterUtils.getResString("assertion_not")); //$NON-NLS-1$
         panel.add(notBox);
 
         orBox = new JCheckBox(JMeterUtils.getResString("assertion_or")); //$NON-NLS-1$
         panel.add(orBox);
 
         return panel;
     }
 
     /**
      * Create a panel allowing the user to supply a list of string patterns to
      * test against.
      *
      * @return a new panel for adding string patterns
      */
     private JPanel createStringPanel() {
         tableModel = new PowerTableModel(new String[] { COL_RESOURCE_NAME }, new Class[] { String.class });
         stringTable = new JTable(tableModel);
         stringTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         stringTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         JMeterUtils.applyHiDPI(stringTable);
 
 
         TextAreaCellRenderer renderer = new TextAreaCellRenderer();
         stringTable.setRowHeight(renderer.getPreferredHeight());
         stringTable.setDefaultRenderer(String.class, renderer);
         stringTable.setDefaultEditor(String.class, new TextAreaTableCellEditor());
         stringTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         JPanel panel = new JPanel();
         panel.setLayout(new BorderLayout());
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("assertion_patterns_to_test"))); //$NON-NLS-1$
 
         panel.add(new JScrollPane(stringTable), BorderLayout.CENTER);
         panel.add(createButtonPanel(), BorderLayout.SOUTH);
 
         return panel;
     }
 
     /**
      * Create a panel with buttons to add and delete string patterns.
      *
      * @return the new panel with add and delete buttons
      */
     private JPanel createButtonPanel() {
         JButton addPattern = new JButton(JMeterUtils.getResString("add")); //$NON-NLS-1$
         addPattern.addActionListener(new AddPatternListener());
         
         JButton addFromClipboardPattern = new JButton(JMeterUtils.getResString("add_from_clipboard")); //$NON-NLS-1$
         addFromClipboardPattern.addActionListener(new AddFromClipboardListener());
 
         deletePattern = new JButton(JMeterUtils.getResString("delete")); //$NON-NLS-1$
         deletePattern.addActionListener(new ClearPatternsListener());
         deletePattern.setEnabled(false);
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addPattern);
         buttonPanel.add(addFromClipboardPattern);
         buttonPanel.add(deletePattern);
         return buttonPanel;
     }
 
     /**
      * An ActionListener for deleting a pattern.
      *
      */
     private class ClearPatternsListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.cancelEditing(stringTable);
             
             int[] rowsSelected = stringTable.getSelectedRows();
             stringTable.clearSelection();
             if (rowsSelected.length > 0) {
                 for (int i = rowsSelected.length - 1; i >= 0; i--) {
                     tableModel.removeRow(rowsSelected[i]);
                 }
                 tableModel.fireTableDataChanged();
             } else {
                 if(tableModel.getRowCount()>0) {
                     tableModel.removeRow(0);
                     tableModel.fireTableDataChanged();
                 }
             }
 
             if (stringTable.getModel().getRowCount() == 0) {
                 deletePattern.setEnabled(false);
             }
         }
     }
 
     /**
      * An ActionListener for adding a pattern.
      */
     private class AddPatternListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.stopTableEditing(stringTable);
             tableModel.addNewRow();
             checkButtonsStatus();
             tableModel.fireTableDataChanged();
         }
     }
     
     /**
      * An ActionListener for pasting from clipboard
      */
     private class AddFromClipboardListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             deletePattern.setEnabled(true);
             GuiUtils.stopTableEditing(stringTable);
             int rowCount = stringTable.getRowCount();
             try {
                 String clipboardContent = GuiUtils.getPastedText();
                 if(clipboardContent == null) {
                     return;
                 }
                 String[] clipboardLines = clipboardContent.split("\n");
                 for (String clipboardLine : clipboardLines) {
                     tableModel.addRow(new Object[] { clipboardLine.trim() });
                 }
                 if (stringTable.getRowCount() > rowCount) {
                     checkButtonsStatus();
 
                     // Highlight (select) and scroll to the appropriate rows.
                     int rowToSelect = tableModel.getRowCount() - 1;
                     stringTable.setRowSelectionInterval(rowCount, rowToSelect);
                     stringTable.scrollRectToVisible(stringTable.getCellRect(rowCount, 0, true));
                 }
             } catch (IOException ioe) {
                 JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),
                         "Could not add data from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                         JOptionPane.ERROR_MESSAGE);
             } catch (UnsupportedFlavorException ufe) {
                 JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),
                         "Could not add retrieve " + DataFlavor.stringFlavor.getHumanPresentableName()
                                 + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             }
             tableModel.fireTableDataChanged();
         }
     }
     
     protected void checkButtonsStatus() {
         // Disable DELETE if there are no rows in the table to delete.
         if (tableModel.getRowCount() == 0) {
             deletePattern.setEnabled(false);
         } else {
             deletePattern.setEnabled(true);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index cc704ac2f..245977bc3 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1122 +1,1123 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 # Warning: JMeterUtils.getResString() replaces space with '_'
 # and converts keys to lowercase before lookup
 # => All keys in this file must also be lower case or they won't match
 #
 
 # Please add new entries in alphabetical order
 
 about=About Apache JMeter
 active_threads_tooltip=Running threads
 add=Add
 add_host=Add static host
 add_as_child=Add as Child
 add_from_clipboard=Add from Clipboard
 add_from_suggested_excludes=Add suggested Excludes
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 add_think_times=Add Think Times to children
 aggregate_graph=Statistical Graphs
 aggregate_graph_choose_color=Choose color
 aggregate_graph_choose_foreground_color=Foreground color
 aggregate_graph_color_bar=Color\:
 aggregate_graph_column=Column\:
 aggregate_graph_column_selection=Column label selection\:
 aggregate_graph_column_settings=Column settings
 aggregate_graph_columns_to_display=Columns to display\:
 aggregate_graph_dimension=Graph size
 aggregate_graph_display=Display Graph
 aggregate_graph_draw_outlines=Draw outlines bar?
 aggregate_graph_dynamic_size=Dynamic graph size
 aggregate_graph_font=Font\:
 aggregate_graph_height=Height\:
 aggregate_graph_increment_scale=Increment scale\:
 aggregate_graph_legend=Legend
 aggregate_graph_legend.placement.bottom=Bottom
 aggregate_graph_legend.placement.left=Left
 aggregate_graph_legend.placement.right=Right
 aggregate_graph_legend.placement.top=Top
 aggregate_graph_legend_placement=Placement\:
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label\:
 aggregate_graph_ms=Milliseconds
 aggregate_graph_no_values_to_graph=No values to graph
 aggregate_graph_number_grouping=Show number grouping?
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_size=Size\:
 aggregate_graph_style=Style\:
 aggregate_graph_sync_with_name=Synchronize with name
 aggregate_graph_tab_graph=Graph
 aggregate_graph_tab_settings=Settings
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_title_group=Title
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Graph title\:
 aggregate_graph_value_font=Value font\:
 aggregate_graph_value_labels_vertical=Value labels vertical?
 aggregate_graph_width=Width\:
 aggregate_graph_xaxis_group=X Axis
 aggregate_graph_yaxis_group=Y Axis (milli-seconds)
 aggregate_graph_yaxis_max_value=Scale maximum value\:
 aggregate_report=Aggregate Report
 aggregate_report_xx_pct1_line={0}% Line
 aggregate_report_xx_pct2_line={0}% Line
 aggregate_report_xx_pct3_line={0}% Line
 aggregate_report_90=90%
 aggregate_report_bandwidth=Received KB/sec
 aggregate_report_sent_bytes_per_sec=Sent KB/sec
 aggregate_report_count=# Samples
 aggregate_report_error=Error
 aggregate_report_error%=Error %
 aggregate_report_max=Max
 aggregate_report_median=Median
 aggregate_report_min=Min
 aggregate_report_rate=Throughput
 aggregate_report_stddev=Std. Dev.
 aggregate_report_total_label=TOTAL
 ajp_sampler_title=AJP/1.3 Sampler
 als_message=Note\: The Access Log Parser is generic in design and allows you to plugin
 als_message2=your own parser. To do so, implement the LogParser, add the jar to the
 als_message3=/lib directory and enter the class in the sampler.
 analyze=Analyze Data File...
 anchor_modifier_title=HTML Link Parser
 appearance=Look and Feel
 apply_naming=Apply Naming Policy
 argument_must_not_be_negative=The Argument must not be negative\!
 arguments_panel_title=Command parameters
 ask_existing_file=The file {0} already exist, what you want to do?
 assertion_assume_success=Ignore Status
 assertion_body_resp=Response Body
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_network_size=Full Response
 assertion_not=Not
 assertion_or=Or
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_regex_empty_default_value=Use empty default value
-assertion_resp_field=Response Field to Test
+assertion_req_headers=Request Headers
+assertion_resp_field=Field to Test
 assertion_resp_size_field=Response Size Field to Test
 assertion_substring=Substring
 assertion_text_document=Document (text)
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attribute_field=Attribute\:
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_clear_per_iter=Clear auth on each iteration?
 auth_manager_options=Options
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 backend_listener=Backend Listener
 backend_listener_classname=Backend Listener implementation
 backend_listener_paramtable=Parameters
 backend_listener_queue_size=Async Queue size
 bind=Thread Bind
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, props, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 bsh_script_reset_interpreter=Reset bsh.Interpreter before each call
 bsh_script_variables=The following variables are defined for the script\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_manager_size=Max Number of elements in cache
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_from_template=There are test items that have not been saved.  Do you wish to save before creating a test plan from selected template?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 change_parent=Change Controller
 char_value=Unicode character number (decimal or 0xhex)
 check_return_code_title=Check Return Code
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_each_iteration=Clear cache each iteration
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 clipboard_node_read_error=An error occurred while copying node
 close=Close
 closeconnection=Close connection
 collapse_tooltip=Click to open / collapse
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 command_config_box_title=Command to Execute
 command_config_std_streams_title=Standard streams (files)
 command_field_title=Command:
 compare=Compare
 comparefilt=Compare filter
 comparison_differ_content=Responses differ in content
 comparison_differ_time=Responses differ in response time by more than 
 comparison_invalid_node=Invalid Node 
 comparison_regex_string=Regex String
 comparison_regex_substitution=Substitution
 comparison_response_time=Response Time: 
 comparison_unit=\ ms
 comparison_visualizer_title=Comparison Assertion Visualizer
 concat_result=Append result to the existing files
 config_element=Config Element
 config_save_settings=Configure
 confirm=Confirm
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_implementation_choose=Implementation:
 cookie_manager_policy=Cookie Policy:
 cookie_manager_title=HTTP Cookie Manager
 cookie_options=Options
 cookies_stored=User-Defined Cookies
 copy=Copy
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 counter_reset_per_tg_iteration=Reset counter on each Thread Group Iteration
 countlim=Size limit
 critical_section_controller_label=Lock name
 critical_section_controller_title=Critical Section Controller
 cssjquery_attribute=Attribute\:
 cssjquery_empty_default_value=Use empty default value
 cssjquery_tester_error=An error occured evaluating expression:{0}, error:{1}
 cssjquery_impl=CSS/JQuery implementation\:
 cssjquery_render_no_text=Data response result isn't text.
 cssjquery_tester_button_test=Test
 cssjquery_tester_field=Selector\:
 cssjquery_tester_title=CSS/JQuery Tester
 csvread_file_file_name=CSV file to get values from | *alias
 cut=Cut
 cut_paste_function=Copy and paste function string
 database_conn_pool_max_usage=Max Usage For Each Connection\:
 database_conn_pool_props=Database Connection Pool
 database_conn_pool_size=Number of Connections in Pool\:
 database_conn_pool_title=JDBC Database Connection Pool Defaults
 database_driver_class=Driver Class\:
 database_login_title=JDBC Database Login Defaults
 database_sql_query_string=SQL Query String\:
 database_sql_query_title=JDBC SQL Query Defaults
 database_testing_title=JDBC Request
 database_url=JDBC URL\:
 database_url_jdbc_props=Database URL and JDBC Driver
 ddn=DN
 de=German
 debug_off=Disable debug
 debug_on=Enable debug
 default_parameters=Default Parameters
 default_value_field=Default Value\:
 delay=Startup delay (seconds)
 delayed_start=Delay Thread creation until needed
 delete=Delete
 delete_parameter=Delete Variable
 delete_host=Delete static host
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 description=Description
 detail=Detail
 directory_field_title=Working directory:
 disable=Disable
 dn=DN
 dns_cache_manager_title=DNS Cache Manager
 dns_hostname_or_ip=Hostname or IP address
 dns_host=Host
 dns_hosts=Static Host Table
 dns_servers=DNS Servers
 domain=Domain
 done=Done
 dont_start=Don't start
 down=Down
 duplicate=Duplicate
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 duration_tooltip=Elapsed time of current running Test
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode=URL Encode
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 environment_panel_title=Environment Variables
 eolbyte=End of line(EOL) byte value: 
 error_indicator_tooltip=Show the number of errors in log, click to open Log Viewer panel
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 error_title=Error
 es=Spanish
 escape_html_string=String to escape
 eval_name_param=Text containing variable and function references
 evalvar_name_param=Name of variable
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 find_target_element=Find target element
 expected_return_code_title=Expected Return Code: 
 expiration=Expiration
 expression_field=CSS/JQuery expression\:
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write results to file / Read from file
 file_visualizer_submit_data=Include Submitted Data
 file_visualizer_title=File Reporter
 file_visualizer_verbose=Verbose Output
 filename=File Name
 follow_redirects=Follow Redirects
 follow_redirects_auto=Redirect Automatically
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Bold
 fontstyle.italic=Italic
 fontstyle.normal=Normal
 foreach_controller_title=ForEach Controller
 foreach_end_index=End index for loop (inclusive)
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_start_index=Start index for loop (exclusive)
 foreach_use_separator=Add "_" before number ?
 format=Number format
 fr=French
 ftp_binary_mode=Use Binary mode ?
 ftp_get=get(RETR)
 ftp_local_file=Local File:
 ftp_local_file_contents=Local File Contents:
 ftp_put=put(STOR)
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result (required)
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode (i.e. save Response Data and Sampler Data)
 functional_mode_explanation=Selecting Functional Test Mode may adversely affect performance.
 gaussian_timer_delay=Constant Delay Offset (in milliseconds)\:
 gaussian_timer_memo=Adds a random delay with a gaussian distribution
 gaussian_timer_range=Deviation (in milliseconds)\:
 gaussian_timer_title=Gaussian Random Timer
 generate=Generate
 generator=Name of Generator class
 generator_cnf_msg=Could not find the generator class. Please make sure you place your jar file in the /lib directory.
 generator_illegal_msg=Could not access the generator class due to IllegalAccessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 graph_apply_filter=Apply filter
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_pointshape_circle=Circle
 graph_pointshape_diamond=Diamond
 graph_pointshape_none=None
 graph_pointshape_square=Square
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms):
 graph_resp_time_interval_reload=Apply interval
 graph_resp_time_not_enough_data=Unable to graph, not enough data
 graph_resp_time_series_selection=Sampler label selection:
 graph_resp_time_settings_line=Line settings
 graph_resp_time_settings_pane=Graph settings
 graph_resp_time_shape_label=Shape point:
 graph_resp_time_stroke_width=Stroke width:
 graph_resp_time_title=Response Time Graph
 graph_resp_time_title_label=Graph title:
 graph_resp_time_xaxis_time_format=Time format (SimpleDateFormat):
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 groovy_function_expression=Expression to evaluate
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_in_transaction_controllers=Put each group in a new transaction controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 heap_dump=Create a heap dump
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_extractor_title=CSS/JQuery Extractor
 html_extractor_type=CSS/JQuery Extractor Implementation
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_max_pool_size=Max number of Threads:
 httpmirror_max_queue_size=Max queue size:
 httpmirror_settings=Settings
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_expression=Interpret Condition as Variable Expression?
 if_controller_label=Condition (default Javascript)
 if_controller_performance=<html>TIP 1\: For performance it is advised to use JMeter functions\: <ul><li>__jexl3</li><li>or __groovy</li></ul> for example\:<br/><b>${__jexl3("${VAR}" == "23")}</b><br/> and check "Interpret Condition as Variable Expression".<br/>TIP 2\: To test success of last Sampler you can use <b>${JMeterThread.last_sample_ok}</b></html>
 if_controller_title=If Controller
 ignore_subcontrollers=Ignore sub-controller blocks
 include_controller=Include Controller
 include_equals=Include Equals?
 include_path=Include Test Plan
 increment=Increment
 infinite=Forever
 initial_context_factory=Initial Context Factory
 insert_after=Insert After
 insert_before=Insert Before
 insert_parent=Insert Parent
 interleave_control_title=Interleave Controller
 interleave_accross_threads=Interleave accross threads
 intsum_param_1=First int to add.
 intsum_param_2=Second int to add - further ints can be summed by adding further arguments.
 invalid_data=Invalid data
 invalid_mail=Error occurred sending the e-mail
 invalid_mail_address=One or more invalid e-mail addresses detected
 invalid_mail_server=Problem contacting the e-mail server (see JMeter log file)
 invalid_variables=Invalid variables
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 ja=Japanese
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 java_request_warning=<html>Classname not found in classpath, ensure you add the required jar and restart. <br/> If you modify "Classname" before you may lose the parameters of the original test plan.<html>
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
 jms_auth_required=Required
 jms_bytes_message=Bytes Message
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_id=Client ID
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Message source
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_correlation_title=Use alternate fields for message correlation
 jms_dest_setup=Setup
 jms_dest_setup_dynamic=Each sample
 jms_dest_setup_static=At startup
 jms_durable_subscription_id=Durable Subscription ID
 jms_error_reconnect_on_codes=Reconnect on error codes (regex)
 jms_error_pause_between=Pause between errors (ms)
 jms_expiration=Expiration (ms)
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_map_message=Map Message
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_priority=Priority (0-9)
 jms_properties=JMS Properties
 jms_properties_name=Name
 jms_properties_title=JMS Properties
 jms_properties_type=Class of value
 jms_properties_value=Value
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Path of folder containing random files suffixed with .dat for bytes messages, .txt or .obj for text and Object messages
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_selector=JMS Selector
 jms_send_queue=JNDI name Request queue
 jms_separator=Separator
 jms_stop_between_samples=Stop between samples?
 jms_store_response=Store Response
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_area=Text Message or Object Message serialized to XML by XStream
 jms_text_message=Text Message
 jms_timeout=Timeout (ms)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File from folder specified below
 jms_use_req_msgid_as_correlid=Use Request Message Id
 jms_use_res_msgid_as_correlid=Use Response Message Id
 jms_use_text=Textarea
 jms_user=User
 jndi_config_title=JNDI Configuration
 jndi_lookup_name=Remote Interface
 jndi_lookup_title=JNDI Lookup Configuration
 jndi_method_button_invoke=Invoke
 jndi_method_button_reflect=Reflect
 jndi_method_home_name=Home Method Name
 jndi_method_home_parms=Home Method Parameters
 jndi_method_name=Method Configuration
 jndi_method_remote_interface_list=Remote Interfaces
 jndi_method_remote_name=Remote Method Name
 jndi_method_remote_parms=Remote Method Parameters
 jndi_method_title=Remote Method Configuration
 jndi_testing_title=JNDI Request
 jndi_url_jndi_props=JNDI Properties
 jsonpath_renderer=JSON Path Tester
 jsonpath_tester_title=JSON Path Tester
 jsonpath_tester_field=JSON Path Expression
 jsonpath_tester_button_test=Test
 jsonpath_render_no_text=No Text
 json_post_processor_title=JSON Extractor
 jsonpp_variable_names=Variable names
 jsonpp_json_path_expressions=JSON Path expressions
 jsonpp_default_values=Default Values
 jsonpp_match_numbers=Match No. (0 for Random)
 jsonpp_compute_concat=Compute concatenation var (suffix _ALL)
 jsonpp_error_number_arguments_mismatch_error=Mismatch between number of variables, json expressions and default values
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_create_instance_per_sample=Create a new instance per sample
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_junit4=Search for JUnit 4 annotations (instead of JUnit 3)
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
 action_check_message=A Test is currently running, stop or shutdown test to execute this command
 action_check_title=Test Running
 ldap_argument_list=LDAPArgument List
 ldap_connto=Connection timeout (in milliseconds)
 ldap_parse_results=Parse the search results ?
 ldap_sample_title=LDAP Request Defaults
 ldap_search_baseobject=Perform baseobject search
 ldap_search_onelevel=Perform onelevel search
 ldap_search_subtree=Perform subtree search
 ldap_secure=Use Secure LDAP Protocol ?
 ldap_testing_title=LDAP Request
 ldapext_sample_title=LDAP Extended Request Defaults
 ldapext_testing_title=LDAP Extended Request
 library=Library
 load=Load
 log_errors_only=Errors
 log_file=Location of log File
 log_function_comment=Additional comment (optional)
 log_function_level=Log level (default INFO) or OUT or ERR
 log_function_string=String to be logged
 log_function_string_ret=String to be logged (and returned)
 log_function_throwable=Throwable text (optional)
 log_only=Log/Display Only:
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAccessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 log_success_only=Successes
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 longsum_param_1=First long to add
 longsum_param_2=Second long to add - further longs can be summed by adding further arguments.
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_header_only=Fetch headers only
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_addressees=Addressee(s): 
 mailer_attributes_panel=Mailing attributes
 mailer_connection_security=Connection security: 
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_failure_limit=Failure Limit: 
 mailer_failure_subject=Failure Subject: 
 mailer_failures=Failures: 
 mailer_from=From: 
 mailer_host=Host: 
 mailer_login=Login: 
 mailer_msg_title_error=Error
 mailer_msg_title_information=Information
 mailer_password=Password: 
 mailer_port=Port: 
 mailer_string=E-Mail Notification
 mailer_success_limit=Success Limit: 
 mailer_success_subject=Success Subject: 
 mailer_test_mail=Test Mail
 mailer_title_message=Message
 mailer_title_settings=Mailer settings
 mailer_title_smtpserver=SMTP server
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\: 
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 mechanism=Mechanism
 menu_assertions=Assertions
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_fragments=Test Fragment
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logger_panel=Log Viewer
 menu_logger_level=Log Level
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_search=Search
 menu_search_reset=Reset Search
 menu_tables=Table
 menu_threads=Threads (Users)
 menu_timer=Timer
 menu_toolbar=Toolbar
 menu_zoom_in=Zoom In
 menu_zoom_out=Zoom Out
 metadata=MetaData
 method=Method\:
 mimetype=Mimetype
 minimum_param=The minimum value allowed for a range of values
 minute=minute
 modddn=Old entry name
 modification_controller_title=Modification Controller
 modification_manager_title=Modification Manager
 modify_test=Modify Test
 modtest=Modification test
 module_controller_module_to_run=Module To Run 
 module_controller_title=Module Controller
 module_controller_warning=Could not find module: 
 name=Name\:
 new=New
 newdn=New distinguished name
 next=Next
 no=Norwegian
 notify_child_listeners_fr=Notify Child Listeners of filtered samplers
 number_of_threads=Number of Threads (users)\:
 obsolete_test_element=This test element is obsolete
 once_only_controller_title=Once Only Controller
 opcode=opCode
 open=Open...
 option=Options
 optional_tasks=Optional Tasks
 paramtable=Send Parameters With the Request\:
 password=Password
 paste=Paste
 paste_insert=Paste As Insert
 path=Path\:
 path_extension_choice=Path Extension (use ";" as separator)
 path_extension_dont_use_equals=Do not use equals in path extension (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Do not use questionmark in path extension (Intershop Enfinity compatibility)
 patterns_to_exclude=URL Patterns to Exclude
 patterns_to_include=URL Patterns to Include
 pkcs12_desc=PKCS 12 Key (*.p12)
 pl=Polish
 poisson_timer_delay=Constant Delay Offset (in milliseconds)\:
 poisson_timer_memo=Adds a random delay with a poisson distribution
 poisson_timer_range=Lambda (in milliseconds)\:
 poisson_timer_title=Poisson Random Timer
 port=Port\:
 post_as_parameters=Parameters
 post_body=Body Data
 post_body_raw=Body Data
 post_files_upload=Files Upload
 post_thread_group_title=tearDown Thread Group
 previous=Previous
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Undefined
 property_value_param=Value of property
 property_visualiser_title=Property Display
 protocol=Protocol [http]\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_cl_wrong_target_cl=Target Controller is configured to "Use Recording Controller" but no such controller exists, \nensure you add a Recording Controller as child of Thread Group node to start recording correctly
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create script recorder - port in use. Choose another port.
 proxy_daemon_error=Could not create script recorder - see log for details
 proxy_daemon_error_from_clipboard=from clipboard
 proxy_daemon_error_not_retrieve=Could not add retrieve
 proxy_daemon_error_read_args=Could not add read arguments from clipboard\:
 proxy_daemon_msg_check_details=Please check the details below when installing the certificate in the browser
 proxy_daemon_msg_created_in_bin=created in JMeter bin directory
 proxy_daemon_msg_install_as_in_doc=You can install it following instructions in Component Reference documentation (see Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Root CA certificate\:
 proxy_domains=HTTPS Domains \:
 proxy_domains_dynamic_mode_tooltip=List of domain names for HTTPS url, ex. jmeter.apache.org or wildcard domain like *.apache.org. Use comma as separator. 
 proxy_domains_dynamic_mode_tooltip_java6=To activate this field, use a Java 7+ runtime environment
 proxy_general_settings=Global Settings
 proxy_headers=Capture HTTP Headers
 proxy_prefix_http_sampler_name=Prefix\:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_settings_port_error_digits=Only digits allowed
 proxy_settings_port_error_invalid_data=Invalid data
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP(S) Test Script Recorder
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 random_multi_result_source_variable=Source Variable(s) (use | as separator)
 random_multi_result_target_variable=Target Variable
 random_string_chars_to_use=Chars to use for random string generation
 random_string_length=Random string length
 realm=Realm
 record_controller_clear_samples=Clear all the recorded samples
 record_controller_title=Recording Controller
 redo=Redo
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_params_names_field=Parameter names regexp group number
 regex_params_ref_name_field=Regular Expression Reference Name
 regex_params_title=RegEx User Parameters
 regex_params_values_field=Parameter values regex group number
 regex_source=Field to check
 regex_src_body=Body
 regex_src_body_as_document=Body as a Document
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Response Headers
 regex_src_hdrs_req=Request Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 regexp_render_no_text=Data response result isn't text.
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_tester_title=RegExp Tester
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_shut=Remote Shutdown
 remote_shut_all=Remote Shutdown All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 remove_confirm_msg=Are you sure you want remove the selected element(s)?
 remove_confirm_title=Confirm remove?
 rename=Rename entry
 replace_file=Replace existing file
 report=Report
 report_bar_chart=Bar Chart
 report_bar_graph_url=URL
 report_base_directory=Base Directory
 report_chart_caption=Chart Caption
 report_chart_x_axis=X Axis
 report_chart_x_axis_label=Label for X Axis
 report_chart_y_axis=Y Axis
 report_chart_y_axis_label=Label for Y Axis
 report_line_graph=Line Graph
 report_line_graph_urls=Include URLs
 report_output_directory=Output Directory for Report
 report_page=Report Page
 report_page_element=Page Element
 report_page_footer=Page Footer
 report_page_header=Page Header
 report_page_index=Create Page Index
 report_page_intro=Page Introduction
 report_page_style_url=Stylesheet url
 report_page_title=Page Title
 report_pie_chart=Pie Chart
 report_plan=Report Plan
 report_select=Select
 report_summary=Report Summary
 report_table=Report Table
 report_writer=Report Writer
 report_writer_html=HTML Report Writer
 reportgenerator_top5_error_count=#Errors
 reportgenerator_top5_error_label=Error
 reportgenerator_top5_label=Sample
 reportgenerator_top5_sample_count=#Samples
 reportgenerator_top5_total=Total
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Label
 reportgenerator_summary_apdex_satisfied=T (Toleration threshold)  
 reportgenerator_summary_apdex_tolerated=F (Frustration threshold)
 reportgenerator_summary_errors_count=Number of errors
 reportgenerator_summary_errors_rate_all=% in all samples
 reportgenerator_summary_errors_rate_error=% in errors
 reportgenerator_summary_errors_type=Type of error
 reportgenerator_summary_statistics_count=#Samples
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=Error %
 reportgenerator_summary_statistics_kbytes=Received
 reportgenerator_summary_statistics_sent_kbytes=Sent
 reportgenerator_summary_statistics_label=Label
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Average
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%dth pct
 reportgenerator_summary_statistics_throughput=Throughput
 reportgenerator_summary_total=Total
 request_data=Request Data
 reset=Reset
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 response_time_distribution_satisfied_label=Requests having \\nresponse time <= {0}ms
 response_time_distribution_tolerated_label= Requests having \\nresponse time > {0}ms and <= {1}ms
 response_time_distribution_untolerated_label=Requests having \\nresponse time > {0}ms
 response_time_distribution_failed_label=Requests in error
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_addtimestamp=Add timestamp
 resultsaver_errors=Save Failed Responses only
 resultsaver_numberpadlen=Minimum Length of sequence number
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 return_code_config_box_title=Return Code Configuration
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 run_threadgroup=Start
 run_threadgroup_no_timers=Start no pauses
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sample_scope=Apply to:
 sample_scope_all=Main sample and sub-samples
 sample_scope_children=Sub-samples only
 sample_scope_parent=Main sample only
 sample_scope_variable=JMeter Variable
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_start_next_loop=Start Next Thread Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 sample_timeout_memo=Interrupt the sampler if it times out
 sample_timeout_timeout=Sample timeout (in milliseconds)\:
 sample_timeout_title=Sample Timeout
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_as_test_fragment=Save as Test Fragment
 save_as_test_fragment_error=One of the selected nodes cannot be put inside a Test Fragment
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save received byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_idletime=Save Idle Time
 save_label=Save Label
 save_latency=Save Latency
 save_connecttime=Save Connect Time
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_sentbytes=Save sent byte count
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 save_workbench=Save WorkBench
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search=Search
 search_base=Search base
 search_expand=Search & Expand
 search_filter=Search Filter
 search_replace_all=Replace All
 search_test=Search Test
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_tree_matches={0} node(s) match the search
 search_text_replace=Replace by
 search_text_title_not_found=Not found
 search_tree_title=Search Tree
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path
 send_file_mime_label=MIME Type
 send_file_param_name_label=Parameter Name
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 setup_thread_group_title=setUp Thread Group
 should_save=You should save your test plan before running it.  \nIf you are using supporting data files (ie, for CSV Data Set or _StringFromFile), \nthen it is particularly important to first save your test script. \nDo you want to save your test plan first?
 shutdown=Shutdown
 simple_config_element=Simple Config Element
 simple_data_writer_title=Simple Data Writer
 size_assertion_comparator_error_equal=been equal to
 size_assertion_comparator_error_greater=been greater than
 size_assertion_comparator_error_greaterequal=been greater or equal to
 size_assertion_comparator_error_less=been less than
 size_assertion_comparator_error_lessequal=been less than or equal to
 size_assertion_comparator_error_notequal=not been equal to
 size_assertion_comparator_label=Type of Comparison
 size_assertion_failure=The result was the wrong size\: It was {0} bytes, but should have {1} {2} bytes.
 size_assertion_input_error=Please enter a valid positive integer.
 size_assertion_label=Size in bytes\:
 size_assertion_size_test=Size to Assert
 size_assertion_title=Size Assertion
 smime_assertion_issuer_dn=Issuer distinguished name
 smime_assertion_message_position=Execute assertion on message at position
 smime_assertion_not_signed=Message not signed
 smime_assertion_signature=Signature
 smime_assertion_signer=Signer certificate
 smime_assertion_signer_by_file=Certificate file
 smime_assertion_signer_constraints=Check values
 smime_assertion_signer_dn=Signer distinguished name
 smime_assertion_signer_email=Signer email address
 smime_assertion_signer_no_check=No check
 smime_assertion_signer_serial=Serial Number
 smime_assertion_title=SMIME Assertion
 smime_assertion_verify_signature=Verify signature
 smtp_additional_settings=Additional Settings
 smtp_attach_file=Attach file(s):
 smtp_attach_file_tooltip=Separate multiple files with ";"
 smtp_auth_settings=Auth settings
 smtp_bcc=Address To BCC:
 smtp_cc=Address To CC:
 smtp_default_port=(Defaults: SMTP:25, SSL:465, StartTLS:587)
 smtp_eml=Send .eml:
 smtp_enabledebug=Enable debug logging?
 smtp_enforcestarttls=Enforce StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in "/tmp/", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_header_add=Add Header
 smtp_header_name=Header Name
 smtp_header_remove=Remove
 smtp_header_value=Header Value
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server=Server:
 smtp_server_connection_timeout=Connection timeout:
 smtp_server_port=Port:
 smtp_server_settings=Server settings
 smtp_server_timeout=Read timeout:
 smtp_server_timeouts_settings=Timeouts (milliseconds)
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_timestamp=Include timestamp in subject
 smtp_to=Address To:
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_file_invalid=Filename references a missing or unreadable file\:
 soap_sampler_title=SOAP/XML-RPC Request (DEPRECATED)
 soap_send_action=Send SOAPAction: 
 solinger=SO_LINGER:
 split_function_separator=String to split on. Default is , (comma).
 split_function_string=String to split
 ssl_alias_prompt=Please type your preferred alias
 ssl_alias_select=Select your alias for the test
 ssl_alias_title=Client Alias
 ssl_error_title=Key Store Problem
 ssl_pass_prompt=Please type your password
 ssl_pass_title=KeyStore Password
 ssl_port=SSL Port
 sslmanager=SSL Manager
 start=Start
 start_no_timers=Start no pauses
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads. You can see number of active threads in the upper right corner of GUI. Please be patient. 
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_host=Host
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 system_sampler_stderr=Standard error (stderr):
 system_sampler_stdin=Standard input (stdin):
 system_sampler_stdout=Standard output (stdout):
 system_sampler_title=OS Process Sampler
 table_visualizer_bytes=Bytes
 table_visualizer_latency=Latency
 table_visualizer_connect=Connect Time(ms)
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_sent_bytes=Sent Bytes
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 3ea955049..a7c7d5a22 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1117 +1,1118 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 #Stored by I18NEdit, may be edited!
 about=A propos de JMeter
 action_check_message=Un test est en cours, arr\u00EAtez le avant d''utiliser cette commande
 action_check_title=Test en cours
 active_threads_tooltip=Unit\u00E9s actives
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_from_clipboard=Ajouter depuis Presse-papier
 add_from_suggested_excludes=Ajouter exclusions propos\u00E9es
 add_host=Ajouter un h\u00F4te statique
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_think_times=Ajouter Temps de pause
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 addtest=Ajout
 aggregate_graph=Graphique des statistiques
 aggregate_graph_choose_color=Choisir couleur
 aggregate_graph_choose_foreground_color=Couleur valeur
 aggregate_graph_color_bar=Couleur \:
 aggregate_graph_column=Colonne
 aggregate_graph_column_selection=S\u00E9lection de colonnes par libell\u00E9 \:
 aggregate_graph_column_settings=Param\u00E8tres colonne
 aggregate_graph_columns_to_display=Colonnes \u00E0 afficher \:
 aggregate_graph_dimension=Taille graphique
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_draw_outlines=Bordure de barre ?
 aggregate_graph_dynamic_size=Taille de graphique dynamique
 aggregate_graph_font=Police \:
 aggregate_graph_height=Hauteur \:
 aggregate_graph_increment_scale=Intervalle \u00E9chelle \:
 aggregate_graph_legend=L\u00E9gende
 aggregate_graph_legend.placement.bottom=Bas
 aggregate_graph_legend.placement.left=Gauche
 aggregate_graph_legend.placement.right=Droite
 aggregate_graph_legend.placement.top=Haut
 aggregate_graph_legend_placement=Position \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_no_values_to_graph=Pas de valeurs pour le graphique
 aggregate_graph_number_grouping=S\u00E9parateur de milliers ?
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_size=Taille \:
 aggregate_graph_style=Style \:
 aggregate_graph_sync_with_name=Synchroniser avec nom
 aggregate_graph_tab_graph=Graphique
 aggregate_graph_tab_settings=Param\u00E8tres
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_title_group=Titre
 aggregate_graph_use_group_name=Ajouter le nom du groupe aux libell\u00E9s
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_value_font=Police de la valeur \:
 aggregate_graph_value_labels_vertical=Libell\u00E9 de valeurs vertical ?
 aggregate_graph_width=Largeur \:
 aggregate_graph_xaxis_group=Abscisses
 aggregate_graph_yaxis_group=Ordonn\u00E9es (milli-secondes)
 aggregate_graph_yaxis_max_value=Echelle maximum \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_bandwidth=Ko/sec re\u00E7us
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
 aggregate_report_sent_bytes_per_sec=KB/sec \u00E9mis
 aggregate_report_stddev=Ecart type
 aggregate_report_total_label=TOTAL
 aggregate_report_xx_pct1_line={0}% centile
 aggregate_report_xx_pct2_line={0}% centile
 aggregate_report_xx_pct3_line={0}% centile
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 apply_naming=Appliquer Convention Nommage
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 arguments_panel_title=Param\u00E8tres de commande
 ask_existing_file=Le fichier {0} existe d\u00E9j\u00e0, que voulez-vous faire?
 assertion_assume_success=Ignorer le statut
 assertion_body_resp=Corps de r\u00E9ponse
 assertion_code_resp=Code de r\u00E9ponse
 assertion_contains=Contient (exp. r\u00E9guli\u00E8re)
 assertion_equals=Est \u00E9gale \u00E0 (texte brut)
 assertion_headers=Ent\u00EAtes de r\u00E9ponse
 assertion_matches=Correspond \u00E0 (exp. r\u00E9guli\u00E8re)
 assertion_message_resp=Message de r\u00E9ponse
 assertion_network_size=R\u00E9ponse compl\u00E8te
 assertion_not=Inverser
 assertion_or=Ou
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_regex_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
-assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
+assertion_req_headers=Ent\u00EAtes de requ\u00EAte
+assertion_resp_field=Section \u00E0 tester
 assertion_resp_size_field=Taille \u00E0 v\u00E9rifier sur
 assertion_substring=Contient (texte brut)
 assertion_text_document=Document (texte)
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9cepteur d'assertions
 attribute=Attribut \:
 attribute_field=Attribut \:
 attrs=Attributs
 auth_base_url=URL de base
 auth_manager_clear_per_iter=R\u00E9authentifier \u00E0 chaque it\u00E9ration ?
 auth_manager_options=Options
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 backend_listener=R\u00E9cepteur asynchrone
 backend_listener_classname=Impl\u00E9mentation du r\u00E9cepteur asynchrone
 backend_listener_paramtable=Param\u00E8tres
 backend_listener_queue_size=Taille de la queue
 bind=Connexion de l'unit\u00E9
 bouncy_castle_unavailable_message=Les jars de bouncycastle sont indisponibles, ajoutez les au classpath.
 browse=Parcourir...
 bsf_sampler_title=Echantillon BSF
 bsf_script=Script \u00E0 lancer (variables\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Fichier script \u00E0 lancer \:
 bsf_script_language=Langage de script \:
 bsf_script_parameters=Param\u00E8tres \u00E0 passer au script/fichier \:
 bsh_assertion_script=Script (IO\: Failure[Message], Response. IN\: Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData])
 bsh_assertion_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nEn lecture/\u00E9criture \: Failure, FailureMessage, SampleResult, vars, props, log.\nEn lecture seule \: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Assertion BeanShell
 bsh_function_expression=Expression \u00E0 \u00E9valuer
 bsh_sampler_title=Echantillon BeanShell
 bsh_script=Script (voir ci-dessous pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_size=Nombre maximum d'\u00E9l\u00E9ments dans le cache
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_from_template=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de charger le mod\u00E8le ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 change_parent=Changer le contr\u00F4leur
 char_value=Caract\u00E8re num\u00E9rique Unicode (d\u00E9cimal or 0xhex)
 check_return_code_title=V\u00E9rifier le code retour
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_each_iteration=Vider le cache \u00E0 chaque it\u00E9ration ?
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 clipboard_node_read_error=Une erreur est survenue lors de la copie du noeud
 close=Fermer
 closeconnection=Fermer la connexion
 collapse_tooltip=Cliquer pour ouvrir / r\u00E9duire
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 command_config_box_title=Commande \u00E0 ex\u00E9cuter
 command_config_std_streams_title=Flux standard (fichiers)
 command_field_title=Commande \:
 compare=Comparaison
 comparefilt=Filtre de comparaison
 comparison_differ_content=Le contenu des r\u00E9ponses est diff\u00E9rent.
 comparison_differ_time=La diff\u00E9rence du temps de r\u00E9ponse diff\u00E8re de plus de 
 comparison_invalid_node=Noeud invalide 
 comparison_regex_string=Expression r\u00E9guli\u00E8re
 comparison_regex_substitution=Substitution
 comparison_response_time=Temps de r\u00E9ponse \: 
 comparison_unit=ms
 comparison_visualizer_title=R\u00E9cepteur d'assertions de comparaison
 concat_result=Ajouter les r\u00E9sultats au fichier existant
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 confirm=Confirmer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage contenu \:
 controller=Contr\u00F4leur
 cookie_implementation_choose=Impl\u00E9mentation \:
 cookie_manager_policy=Politique des cookies \:
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookie_options=Options
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 counter_reset_per_tg_iteration=R\u00E9initialiser le compteur \u00E0 chaque it\u00E9ration du groupe d'unit\u00E9s
 countlim=Limiter le nombre d'\u00E9l\u00E9ments retourn\u00E9s \u00E0
 critical_section_controller_label=Nom du verrou
 critical_section_controller_title=Contr\u00F4leur Section critique
 cssjquery_attribute=Attribut
 cssjquery_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 cssjquery_impl=Impl\u00E9mentation CSS/JQuery\:
 cssjquery_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 cssjquery_tester_button_test=Tester
 cssjquery_tester_error=Une erreur s''est produite lors de l''\u00E9valuation de l''expression\:{0}, erreur\:{1}
 cssjquery_tester_field=S\u00E9lecteur\:
 cssjquery_tester_title=Testeur CSS/JQuery
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_conn_pool_max_usage=Utilisation max pour chaque connexion\:
 database_conn_pool_props=Pool de connexions \u221A\u2020 la base de donn\u221A\u00A9es
 database_conn_pool_size=Nombre de Connexions dans le Pool\:
 database_conn_pool_title=Valeurs par d\u00E9faut du Pool de connexions JDBC
 database_driver_class=Classe du Driver\:
 database_login_title=Valeurs par d\u00E9faut de la base de donn\u221A\u00A9es JDBC
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 database_testing_title=Requ\u221A\u2122te JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=URL et driver JDBC de la base de donn\u221A\u00A9es
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delayed_start=Cr\u00E9er les unit\u00E9s seulement quand n\u00E9cessaire
 delete=Supprimer
 delete_host=Supprimer l''h\u00F4te statique
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 description=Description
 detail=D\u00E9tail
 directory_field_title=R\u00E9pertoire d'ex\u00E9cution \:
 disable=D\u00E9sactiver
 dn=Racine DN \:
 dns_cache_manager_title=Gestionnaire de cache DNS
 dns_hostname_or_ip=Nom de machine ou adresse IP
 dns_host=H\u00F4te
 dns_hosts=Table d''h\u00F4te statique
 dns_servers=Serveurs DNS
 domain=Domaine \:
 done=Fait
 dont_start=Ne pas ex\u00E9cuter le test
 down=Descendre
 duplicate=Dupliquer
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 duration_tooltip=Temps pass\u00E9 depuis le d\u00E9but du test en cours
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode=URL Encoder
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
 entrydn=Entr\u00E9e DN
 environment_panel_title=Variables d'environnement
 eolbyte=Valeur byte de l'indicateur de fin de ligne (EOL)\: 
 error_indicator_tooltip=Affiche le nombre d'erreurs dans le journal(log), cliquer pour afficher la console.
 error_loading_help=Erreur au chargement de la page d'aide
 error_occurred=Une erreur est survenue
 error_title=Erreur
 es=Espagnol
 escape_html_string=Cha\u00EEne d'\u00E9chappement
 eval_name_param=Variable contenant du texte et r\u00E9f\u00E9rences de fonctions
 evalvar_name_param=Nom de variable
 example_data=Exemple de donn\u00E9e
 example_title=Echantillon exemple
 exit=Quitter
 expected_return_code_title=Code retour attendu \: 
 expiration=Expiration
 expression_field=Expression CSS/JQuery \:
 field_name=Nom du champ
 file=Fichier
 file_already_in_use=Ce fichier est d\u00E9j\u00E0 utilis\u00E9
 file_visualizer_append=Concat\u00E9ner au fichier de donn\u00E9es existant
 file_visualizer_auto_flush=Vider automatiquement apr\u00E8s chaque echantillon de donn\u00E9es
 file_visualizer_browse=Parcourir...
 file_visualizer_close=Fermer
 file_visualizer_file_options=Options de fichier
 file_visualizer_filename=Nom du fichier \: 
 file_visualizer_flush=Vider
 file_visualizer_missing_filename=Aucun fichier de sortie sp\u00E9cifi\u00E9.
 file_visualizer_open=Ouvrir...
 file_visualizer_output_file=\u00C9crire les r\u00E9sultats dans un fichier ou lire les r\u00E9sultats depuis un fichier CSV / JTL
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 find_target_element=Trouver l'\u00E9l\u00E9ment cible
 follow_redirects=Suivre les redirect.
 follow_redirects_auto=Rediriger automat.
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Gras
 fontstyle.italic=Italique
 fontstyle.normal=Normal
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_end_index=Indice de fin de la boucle (inclus)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
 foreach_start_index=Indice de d\u00E9but de la boucle(exclus)
 foreach_use_separator=Ajouter un soulign\u00E9 "_" avant le nombre ?
 format=Format du nombre \:
 fr=Fran\u00E7ais
 ftp_binary_mode=Utiliser le mode binaire ?
 ftp_get=R\u00E9cup\u00E9rer (get)
 ftp_local_file=Fichier local \:
 ftp_local_file_contents=Contenus fichier local \:
 ftp_put=D\u00E9poser (put)
 ftp_remote_file=Fichier distant \:
 ftp_sample_title=Param\u00E8tres FTP par d\u00E9faut
 ftp_save_response_data=Enregistrer le fichier dans la r\u00E9ponse ?
 ftp_testing_title=Requ\u00EAte FTP
 function_dialog_menu_item=Assistant de fonctions
 function_helper_title=Assistant de fonctions
 function_name_param=Nom de la fonction. Utilis\u00E9 pour stocker les valeurs \u00E0 utiliser ailleurs dans la plan de test
 function_name_paropt=Nom de variable dans laquelle le r\u00E9sultat sera stock\u00E9 (optionnel)
 function_params=Param\u00E8tres de la fonction
 functional_mode=Mode de test fonctionnel
 functional_mode_explanation=S\u00E9lectionner le mode de test fonctionnel uniquement si vous avez besoin\nd'enregistrer les donn\u00E9es re\u00E7ues du serveur dans un fichier \u00E0 chaque requ\u00EAte. \n\nS\u00E9lectionner cette option affecte consid\u00E9rablement les performances.
 gaussian_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 gaussian (en millisecondes) \:
 gaussian_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution gaussienne
 gaussian_timer_range=D\u00E9viation (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 graph_apply_filter=Appliquer le filtre
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_pointshape_circle=Cercle
 graph_pointshape_diamond=Diamant
 graph_pointshape_none=Aucun
 graph_pointshape_square=Carr\u00E9
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms) \:
 graph_resp_time_interval_reload=Appliquer l'interval
 graph_resp_time_not_enough_data=Impossible de dessiner le graphique, pas assez de donn\u00E9es
 graph_resp_time_series_selection=S\u00E9lection des \u00E9chantillons par libell\u00E9 \:
 graph_resp_time_settings_line=Param\u00E9tres de la courbe
 graph_resp_time_settings_pane=Param\u00E9tres du graphique
 graph_resp_time_shape_label=Forme de la jonction \:
 graph_resp_time_stroke_width=Largeur de ligne \:
 graph_resp_time_title=Graphique \u00E9volution temps de r\u00E9ponses
 graph_resp_time_title_label=Titre du graphique \:  
 graph_resp_time_xaxis_time_format=Formatage heure (SimpleDateFormat) \:
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_ms=ms
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 groovy_function_expression=Expression \u00E0 \u00E9valuer
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_in_transaction_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur de transaction
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 heap_dump=Cr\u00E9er une image disque de la m\u00E9moire (heap dump)
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_extractor_title=Extracteur CSS/JQuery
 html_extractor_type=Impl\u00E9mentation de l'extracteur CSS/JQuery
 http_implementation=Impl\u00E9mentation \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
 httpmirror_max_pool_size=Taille maximum du pool d'unit\u00E9s \:
 httpmirror_max_queue_size=Taille maximum de la file d'attente \:
 httpmirror_settings=Param\u00E8tres
 httpmirror_title=Serveur HTTP miroir
 id_prefix=Pr\u00E9fixe d'ID
 id_suffix=Suffixe d'ID
 if_controller_evaluate_all=Evaluer pour tous les fils ?
 if_controller_expression=Interpr\u00E9ter la condition comme une expression
 if_controller_label=Condition (d\u00E9faut Javascript) \:
 if_controller_performance=<html>Astuce 1 \: Pour la performance, il est conseill\u00E9 d''utiliser les fonctions JMeter\: <ul><li> __ jexl3</li><li>ou __groovy</li></ul> par exemple\: <br/><b> $ {__ jexl3("${VAR}" == "23")}</b><br/>et cocher la case "Interpr\u00E9ter la condition en tant qu'expression variable". <br/>Astuce 2 \: Pour tester le succ\u00E8s du dernier Sampler, vous pouvez utiliser <b> $ { JMeterThread.last_sample_ok} </b></html>
 if_controller_title=Contr\u00F4leur Si (If)
 ignore_subcontrollers=Ignorer les sous-blocs de contr\u00F4leurs
 include_controller=Contr\u00F4leur Inclusion
 include_equals=Inclure \u00E9gal ?
 include_path=Plan de test \u00E0 inclure
 increment=Incr\u00E9ment \:
 infinite=Infini
 initial_context_factory=Fabrique de contexte initiale
 insert_after=Ins\u00E9rer apr\u00E8s
 insert_before=Ins\u00E9rer avant
 insert_parent=Ins\u00E9rer en tant que parent
 interleave_accross_threads=Alterne en prenant en compte toutes les unit\u00E9s
 interleave_control_title=Contr\u00F4leur Interleave
 intsum_param_1=Premier entier \u00E0 ajouter
 intsum_param_2=Deuxi\u00E8me entier \u00E0 ajouter - les entier(s) suivants peuvent \u00EAtre ajout\u00E9(s) avec les arguments suivants.
 invalid_data=Donn\u00E9e invalide
 invalid_mail=Une erreur est survenue lors de l'envoi de l'email
 invalid_mail_address=Une ou plusieurs adresse(s) invalide(s) ont \u00E9t\u00E9 d\u00E9tect\u00E9e(s)
 invalid_mail_server=Le serveur de mail est inconnu (voir le fichier de journalisation JMeter)
 invalid_variables=Variables invalides
 iteration_counter_arg_1=TRUE, pour que chaque utilisateur ait son propre compteur, FALSE pour un compteur global
 iterator_num=Nombre d'it\u00E9rations \:
 ja=Japonais
 jar_file=Fichiers .jar
 java_request=Requ\u00EAte Java
 java_request_defaults=Requ\u00EAte Java par d\u00E9faut
 java_request_warning=<html>Classe introuvable dans le classpath, veuillez ajouter le jar la contenant et red\u00E9marrer.<br/>Ne modifiez pas "Nom de classe" avant sinon vous perdrez les param\u00E8tres.</html>
 javascript_expression=Expression JavaScript \u00E0 \u00E9valuer
 jexl_expression=Expression JEXL \u00E0 \u00E9valuer
 jms_auth_required=Obligatoire
 jms_bytes_message=Message binaire
 jms_client_caption=Le client r\u00E9cepteur utilise MessageConsumer.receive () pour \u00E9couter les messages.
 jms_client_caption2=MessageListener utilise l'interface onMessage(Message) pour \u00E9couter les nouveaux messages.
 jms_client_id=ID du Client
 jms_client_type=Client
 jms_communication_style=Type de communication \: 
 jms_concrete_connection_factory=Fabrique de connexion 
 jms_config=Source du message \:
 jms_config_title=Configuration JMS
 jms_connection_factory=Fabrique de connexion
 jms_correlation_title=Champs alternatifs pour la correspondance de message
 jms_dest_setup=Evaluer
 jms_dest_setup_dynamic=A chaque \u00E9chantillon
 jms_dest_setup_static=Au d\u00E9marrage
 jms_durable_subscription_id=ID d'abonnement durable
 jms_error_pause_between=Temporisation entre erreurs (ms)
 jms_error_reconnect_on_codes=Se reconnecter pour les codes d'erreurs (regex)
 jms_expiration=Expiration (ms)
 jms_file=Fichier
 jms_initial_context_factory=Fabrique de connexion initiale
 jms_itertions=Nombre d'\u00E9chantillons \u00E0 agr\u00E9ger
 jms_jndi_defaults_title=Configuration JNDI par d\u00E9faut
 jms_jndi_props=Propri\u00E9t\u00E9s JNDI
 jms_map_message=Message Map
 jms_message_title=Propri\u00E9t\u00E9s du message
 jms_message_type=Type de message \: 
 jms_msg_content=Contenu
 jms_object_message=Message Object
 jms_point_to_point=Requ\u00EAte JMS Point-\u00E0-point
 jms_priority=Priorit\u00E9 (0-9)
 jms_properties=Propri\u00E9t\u00E9s JMS
 jms_properties_name=Nom
 jms_properties_title=Propri\u00E9t\u00E9s JMS
 jms_properties_type=Classe de la Valeur
 jms_properties_value=Valeur
 jms_props=Propri\u00E9t\u00E9s JMS
 jms_provider_url=URL du fournisseur
 jms_publisher=Requ\u00EAte JMS Publication
 jms_pwd=Mot de passe
 jms_queue=File
 jms_queue_connection_factory=Fabrique QueueConnection
 jms_queueing=Ressources JMS
 jms_random_file=Dossier contenant des fichiers al\u00E9atoires (suffix\u00E9s par .dat pour un message binaire, .txt ou .obj pour un message texte ou un objet)
 jms_receive_queue=Nom JNDI de la file d'attente Receive 
 jms_request=Requ\u00EAte seule
 jms_requestreply=Requ\u00EAte R\u00E9ponse
 jms_sample_title=Requ\u00EAte JMS par d\u00E9faut
 jms_selector=S\u00E9lecteur JMS
 jms_send_queue=Nom JNDI de la file d'attente Request
 jms_separator=S\u00E9parateur
 jms_stop_between_samples=Arr\u00EAter entre les \u00E9chantillons ?
 jms_store_response=Stocker la r\u00E9ponse
 jms_subscriber_on_message=Utiliser MessageListener.onMessage()
 jms_subscriber_receive=Utiliser MessageConsumer.receive()
 jms_subscriber_title=Requ\u00EAte JMS Abonnement
 jms_testing_title=Messagerie Request
 jms_text_area=Message texte ou Message Objet s\u00E9rialis\u00E9 en XML par XStream
 jms_text_message=Message texte
 jms_timeout=D\u00E9lai (ms)
 jms_topic=Destination
 jms_use_auth=Utiliser l'authentification ?
 jms_use_file=Depuis un fichier
 jms_use_non_persistent_delivery=Utiliser un mode de livraison non persistant ?
 jms_use_properties_file=Utiliser le fichier jndi.properties
 jms_use_random_file=Fichier al\u00E9atoire
 jms_use_req_msgid_as_correlid=Utiliser l'ID du message Request
 jms_use_res_msgid_as_correlid=Utiliser l'ID du message Response
 jms_use_text=Zone de texte (ci-dessous)
 jms_user=Utilisateur
 jndi_config_title=Configuration JNDI
 jndi_lookup_name=Interface remote
 jndi_lookup_title=Configuration Lookup JNDI 
 jndi_method_button_invoke=Invoquer
 jndi_method_button_reflect=R\u00E9flection
 jndi_method_home_name=Nom de la m\u00E9thode home
 jndi_method_home_parms=Param\u00E8tres de la m\u00E9thode home
 jndi_method_name=Configuration m\u00E9thode
 jndi_method_remote_interface_list=Interfaces remote
 jndi_method_remote_name=Nom m\u00E9thodes remote
 jndi_method_remote_parms=Param\u00E8tres m\u00E9thode remote
 jndi_method_title=Configuration m\u00E9thode remote
 jndi_testing_title=Requ\u00EAte JNDI
 jndi_url_jndi_props=Propri\u00E9t\u00E9s JNDI
 json_post_processor_title=Extracteur JSON
 jsonpath_render_no_text=Pas de Texte
 jsonpath_renderer=Testeur JSON Path
 jsonpath_tester_button_test=Tester
 jsonpath_tester_field=Expression JSON Path
 jsonpath_tester_title=Testeur JSON Path
 jsonpp_compute_concat=Calculer la variable de concat\u00E9nation (suffix _ALL)
 jsonpp_default_values=Valeur par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire)
 jsonpp_variable_names=Noms des variables
 junit_append_error=Concat\u00E9ner les erreurs d'assertion
 junit_append_exception=Concat\u00E9ner les exceptions d'ex\u00E9cution
 junit_constructor_error=Impossible de cr\u00E9er une instance de la classe
 junit_constructor_string=Libell\u00E9 de cha\u00EEne Constructeur
 junit_create_instance_per_sample=Cr\u00E9er une nouvelle instance pour chaque \u00E9chantillon
 junit_do_setup_teardown=Ne pas appeler setUp et tearDown
 junit_error_code=Code d'erreur
 junit_error_default_msg=Une erreur inattendue est survenue
 junit_error_msg=Message d'erreur
 junit_failure_code=Code d'\u00E9chec
 junit_failure_default_msg=Test \u00E9chou\u00E9
 junit_failure_msg=Message d'\u00E9chec
 junit_junit4=Rechercher les annotations JUnit 4 (au lieu de JUnit 3)
 junit_pkg_filter=Filtre de paquets
 junit_request=Requ\u00EAte JUnit
 junit_request_defaults=Requ\u00EAte par d\u00E9faut JUnit
 junit_success_code=Code de succ\u00E8s
 junit_success_default_msg=Test r\u00E9ussi
 junit_success_msg=Message de succ\u00E8s
 junit_test_config=Param\u00E8tres Test JUnit
 junit_test_method=M\u00E9thode de test
 ldap_argument_list=Liste d'arguments LDAP
 ldap_connto=D\u00E9lai d'attente de connexion (millisecondes)
 ldap_parse_results=Examiner les r\u00E9sultats de recherche ?
 ldap_sample_title=Requ\u00EAte LDAP par d\u00E9faut
 ldap_search_baseobject=Effectuer une recherche 'baseobject'
 ldap_search_onelevel=Effectuer une recherche 'onelevel'
 ldap_search_subtree=Effectuer une recherche 'subtree'
 ldap_secure=Utiliser le protocole LDAP s\u00E9curis\u00E9 (ldaps) ?
 ldap_testing_title=Requ\u00EAte LDAP
 ldapext_sample_title=Requ\u00EAte LDAP \u00E9tendue par d\u00E9faut
 ldapext_testing_title=Requ\u00EAte LDAP \u00E9tendue
 library=Librairie
 load=Charger
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
 log_function_throwable=Texte de l'exception Throwable (optionnel)
 log_only=Uniquement \:
 log_parser=Nom de la classe de parseur des journaux (log)
 log_parser_cnf_msg=N'a pas p\u00FB trouver cette classe. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 log_parser_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classe \u00E0 cause d'une exception IllegalAccessException.
 log_parser_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur de log. Assurez-vous que le parseur impl\u00E9mente l'interface LogParser.
 log_sampler=Echantillon Journaux d'acc\u00E8s Tomcat
 log_success_only=Succ\u00E8s
 logic_controller_title=Contr\u00F4leur Simple
 login_config=Configuration Identification
 login_config_element=Configuration Identification 
 longsum_param_1=Premier long \u221A\u2020 ajouter
 longsum_param_2=Second long \u221A\u2020 ajouter - les autres longs pourront \u221A\u2122tre cumul\u221A\u00A9s en ajoutant d'autres arguments.
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_header_only=R\u00E9cup\u00E9rer seulement les ent\u00EAtes
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_port=Port (optionnel) \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Protocole (ex. pop3, imaps) \:
 mail_reader_storemime=Stocker le message en utilisant MIME (brut)
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_addressees=Destinataire(s) \: 
 mailer_attributes_panel=Attributs de courrier
 mailer_connection_security=S\u00E9curit\u00E9 connexion \: 
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_failure_limit=Limite d'\u00E9chec \: 
 mailer_failure_subject=Sujet Echec \: 
 mailer_failures=Nombre d'\u00E9checs \: 
 mailer_from=Exp\u00E9diteur \: 
 mailer_host=Serveur \: 
 mailer_login=Identifiant \: 
 mailer_msg_title_error=Erreur
 mailer_msg_title_information=Information
 mailer_password=Mot de passe \: 
 mailer_port=Port \: 
 mailer_string=Notification d'email
 mailer_success_limit=Limite de succ\u00E8s \: 
 mailer_success_subject=Sujet Succ\u00E8s \: 
 mailer_test_mail=Tester email
 mailer_title_message=Message
 mailer_title_settings=Param\u00E8tres
 mailer_title_smtpserver=Serveur SMTP
 mailer_visualizer_title=R\u00E9cepteur Notification Email
 match_num_field=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire) \: 
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 mechanism=M\u00E9canisme
 menu_assertions=Assertions
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logger_panel=Afficher la console
 menu_logger_level=Niveau de log
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_search=Rechercher
 menu_search_reset=Effacer la recherche
 menu_tables=Table
 menu_threads=Moteurs d'utilisateurs
 menu_timer=Compteurs de temps
 menu_toolbar=Barre d'outils
 menu_zoom_in=Agrandir
 menu_zoom_out=R\u00E9duire
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
 minute=minute
 modddn=Ancienne valeur
 modification_controller_title=Contr\u00F4leur Modification
 modification_manager_title=Gestionnaire Modification
 modify_test=Modification
 modtest=Modification
 module_controller_module_to_run=Module \u00E0 ex\u00E9cuter \:
 module_controller_title=Contr\u00F4leur Module
 module_controller_warning=Ne peut pas trouver le module \:
 name=Nom \:
 new=Nouveau
 newdn=Nouveau DN
 next=Suivant
 no=Norv\u00E9gien
 notify_child_listeners_fr=Notifier les r\u00E9cepteurs fils des \u00E9chantillons filtr\u00E9s
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 opcode=Code d'op\u00E9ration
 open=Ouvrir...
 option=Options
 optional_tasks=T\u00E2ches optionnelles
 paramtable=Envoyer les param\u00E8tres avec la requ\u00EAte \:
 password=Mot de passe \:
 paste=Coller
 paste_insert=Coller ins\u00E9rer
 path=Chemin \:
 path_extension_choice=Extension de chemin (utiliser ";" comme separateur)
 path_extension_dont_use_equals=Ne pas utiliser \u00E9gale dans l'extension de chemin (Compatibilit\u00E9 Intershop Enfinity)
 path_extension_dont_use_questionmark=Ne pas utiliser le point d'interrogation dans l'extension du chemin (Compatiblit\u00E9 Intershop Enfinity)
 patterns_to_exclude=URL \: motifs \u00E0 exclure
 patterns_to_include=URL \: motifs \u00E0 inclure
 pkcs12_desc=Clef PKCS 12 (*.p12)
 pl=Polonais
 poisson_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 sur la loi de poisson (en millisecondes) \:
 poisson_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution de type Poisson
 poisson_timer_range=D\u00E9viation (en millisecondes) \:
 poisson_timer_title=Compteur de temps al\u00E9atoire selon la loi de Poisson 
 port=Port \:
 post_as_parameters=Param\u00E8tres
 post_body=Corps de la requ\u00EAte
 post_body_raw=Donn\u00E9es de la requ\u00EAte
 post_files_upload=T\u00E9l\u00E9chargement de fichiers
 post_thread_group_title=Groupe d'unit\u00E9s de fin
 previous=Pr\u00E9c\u00E9dent
 property_as_field_label={0}\:
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole [http] \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 provider_url=Provider URL
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_cl_wrong_target_cl=Le contr\u00F4leur cible est configur\u00E9 en mode "Utiliser un contr\u00F4leur enregistreur" \nmais aucun contr\u00F4leur de ce type n'existe, assurez vous de l'ajouter comme fils \nde Groupe d'unit\u00E9s afin de pouvoir d\u00E9marrer l'enregisteur
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_daemon_error_from_clipboard=depuis le presse-papier
 proxy_daemon_error_not_retrieve=Impossible d'ajouter
 proxy_daemon_error_read_args=Impossible de lire les arguments depuis le presse-papiers \:
 proxy_daemon_msg_check_details=Svp, v\u00E9rifier les d\u00E9tails ci-dessous lors de l'installation du certificat dans le navigateur
 proxy_daemon_msg_created_in_bin=cr\u00E9\u00E9 dans le r\u00E9pertoire bin de JMeter
 proxy_daemon_msg_install_as_in_doc=Vous pouvez l'installer en suivant les instructions de la documentation Component Reference (voir Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Certificat AC ra\u00E7ine \:
 proxy_domains=Domaines HTTPS \:
 proxy_domains_dynamic_mode_tooltip=Liste de noms de domaine pour les url HTTPS, ex. jmeter.apache.org ou les domaines wildcard comme *.apache.org. Utiliser la virgule comme s\u00E9parateur. 
 proxy_domains_dynamic_mode_tooltip_java6=Pour activer ce champ, utiliser un environnement d'ex\u00E9cution Java 7+
 proxy_general_settings=Param\u00E8tres g\u00E9n\u00E9raux
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_prefix_http_sampler_name=Pr\u00E9fixe \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_settings_port_error_digits=Seuls les chiffres sont autoris\u00E9s.
 proxy_settings_port_error_invalid_data=Donn\u00E9es invalides
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Enregistreur script de test HTTP(S)
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_multi_result_source_variable=Variable(s) source (separateur |)
 random_multi_result_target_variable=Variable cible
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_string_chars_to_use=Caract\u00E8res \u00E0 utiliser pour la g\u00E9n\u00E9ration de la cha\u00EEne al\u00E9atoire
 random_string_length=Longueur de cha\u00EEne al\u00E9atoire
 realm=Univers (realm)
 record_controller_clear_samples=Supprimer tous les \u00E9chantillons
 record_controller_title=Contr\u00F4leur Enregistreur
 redo=R\u00E9tablir
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_params_names_field=Num\u00E9ro du groupe de la Regex pour les noms des param\u00E8tres
 regex_params_ref_name_field=Nom de la r\u00E9f\u00E9rence de la Regex
 regex_params_title=Param\u00E8tres utilisateurs bas\u00E9s sur RegEx
 regex_params_values_field=Num\u00E9ro du groupe de la Regex pour les valeurs des param\u00E8tres
 regex_source=Port\u00E9e
 regex_src_body=Corps
 regex_src_body_as_document=Corps en tant que Document
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes (R\u00E9ponse)
 regex_src_hdrs_req=Ent\u00EAtes (Requ\u00EAte)
 regex_src_url=URL
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 regexfunc_param_7=Variable en entr\u221A\u00A9e contenant le texte \u221A\u2020 parser ([\u221A\u00A9chantillon pr\u221A\u00A9c\u221A\u00A9dent])
 regexp_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 regexp_tester_button_test=Tester
 regexp_tester_field=Expression r\u00E9guli\u00E8re \:
 regexp_tester_title=Testeur de RegExp
 remote_error_init=Erreur lors de l'initialisation du serveur distant
 remote_error_starting=Erreur lors du d\u221A\u00A9marrage du serveur distant
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_shut=Extinction \u00E0 distance
 remote_shut_all=Extinction \u00E0 distance de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 remove_confirm_msg=Etes-vous s\u00FBr de vouloir supprimer ce(s) \u00E9l\u00E9ment(s) ?
 remove_confirm_title=Confirmer la suppression ?
 rename=Renommer une entr\u00E9e
 replace_file=Remplacer le fichier existant	
 report=Rapport
 report_bar_chart=Graphique \u221A\u2020 barres
 report_bar_graph_url=URL
 report_base_directory=R\u221A\u00A9pertoire de Base
 report_chart_caption=L\u221A\u00A9gende du graph
 report_chart_x_axis=Axe X
 report_chart_x_axis_label=Libell\u221A\u00A9 de l'Axe X
 report_chart_y_axis=Axe Y
 report_chart_y_axis_label=Libell\u221A\u00A9 de l'Axe Y
 report_line_graph=Graphique Lin\u221A\u00A9aire
 report_line_graph_urls=Inclure les URLs
 report_output_directory=R\u221A\u00A9pertoire de sortie du rapport
 report_page=Page de Rapport
 report_page_element=Page Element
 report_page_footer=Pied de page
 report_page_header=Ent\u221A\u2122te de Page
 report_page_index=Cr\u221A\u00A9er la Page d'Index
 report_page_intro=Page d'Introduction
 report_page_style_url=Url de la feuille de style
 report_page_title=Titre de la Page
 report_pie_chart=Camembert
 report_plan=Plan du rapport
 report_select=Selectionner
 report_summary=Rapport r\u221A\u00A9sum\u221A\u00A9
 report_table=Table du Rapport
 report_writer=R\u221A\u00A9dacteur du Rapport
 report_writer_html=R\u221A\u00A9dacteur de rapport HTML
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Libell\u00E9
 reportgenerator_summary_apdex_satisfied=T (Seuil de tol\u00E9rance)
 reportgenerator_summary_apdex_tolerated=F (Seuil de frustration)
 reportgenerator_summary_errors_count=Nombre d'erreurs
 reportgenerator_summary_errors_rate_all=% de tous les \u00E9chantillons
 reportgenerator_summary_errors_rate_error=% des erreurs
 reportgenerator_summary_errors_type=Type d'erreur
 reportgenerator_summary_statistics_count=\#Echantillons
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=% Erreur
 reportgenerator_summary_statistics_kbytes=Re\u00E7ues
 reportgenerator_summary_statistics_label=Libell\u00E9
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Temps moyen
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile
 reportgenerator_summary_statistics_sent_kbytes=Envoy\u00E9s
 reportgenerator_summary_statistics_throughput=D\u00E9bit
 reportgenerator_summary_total=Total
 reportgenerator_top5_error_count=\#Erreurs
 reportgenerator_top5_error_label=Erreur
 reportgenerator_top5_label=Echantillon
 reportgenerator_top5_sample_count=\#Echantillons
 reportgenerator_top5_total=Total
 request_data=Donn\u00E9e requ\u00EAte
 reset=R\u00E9initialiser
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 response_time_distribution_failed_label=Requ\u00EAtes en erreur
 response_time_distribution_satisfied_label=Requ\u00EAtes \\ntemps de r\u00E9ponse <\= {0}ms
 response_time_distribution_tolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms et <\= {1}ms
 response_time_distribution_untolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_addtimestamp=Ajouter un timestamp
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_numberpadlen=Taille minimale du num\u00E9ro de s\u00E9quence
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 retobj=Retourner les objets
 return_code_config_box_title=Configuration du code retour
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 run_threadgroup=Lancer
 run_threadgroup_no_timers=Lancer sans pauses
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sample_result_save_configuration=Sauvegarder la configuration de la sauvegarde des \u00E9chantillons
 sample_scope=Appliquer sur
 sample_scope_all=L'\u00E9chantillon et ses ressources li\u00E9es
 sample_scope_children=Les ressources li\u00E9es
 sample_scope_parent=L'\u00E9chantillon
 sample_scope_variable=Une variable \:
 sample_timeout_memo=Interrompre l'\u00E9chantillon si le d\u00E9lai est d\u00E9pass\u00E9
 sample_timeout_timeout=D\u00E9lai d'attente avant interruption (en millisecondes) \: 
 sample_timeout_title=Compteur Interruption
 sampler_label=Libell\u00E9
 sampler_on_error_action=Action \u00E0 suivre apr\u00E8s une erreur d'\u00E9chantillon
 sampler_on_error_continue=Continuer
 sampler_on_error_start_next_loop=D\u00E9marrer it\u00E9ration suivante
 sampler_on_error_stop_test=Arr\u00EAter le test
 sampler_on_error_stop_test_now=Arr\u00EAter le test imm\u00E9diatement
 sampler_on_error_stop_thread=Arr\u00EAter l'unit\u00E9
 save=Enregistrer le plan de test
 save?=Enregistrer ?
 save_all_as=Enregistrer le plan de test sous...
 save_as=Enregistrer la s\u00E9lection sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_as_test_fragment=Enregistrer comme Fragment de Test
 save_as_test_fragment_error=Au moins un \u00E9l\u00E9ment ne peut pas \u00EAtre plac\u00E9 sous un Fragment de Test
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets re\u00E7us
 save_code=Code de r\u00E9ponse HTTP
 save_connecttime=Temps \u00E9tablissement connexion
 save_datatype=Type de donn\u00E9es
 save_encoding=Encodage
 save_fieldnames=Libell\u00E9 des colonnes (CSV)
 save_filename=Nom de fichier de r\u00E9ponse
 save_graphics=Enregistrer le graphique
 save_hostname=Nom d'h\u00F4te
 save_idletime=Temps d'inactivit\u00E9
 save_label=Libell\u00E9
 save_latency=Latence
 save_message=Message de r\u00E9ponse
 save_overwrite_existing_file=Le fichier s\u00E9lectionn\u00E9 existe d\u00E9j\u00E0, voulez-vous l'\u00E9craser ?
 save_requestheaders=Ent\u00EAtes de requ\u00EAte (XML)
 save_responsedata=Donn\u00E9es de r\u00E9ponse (XML)
 save_responseheaders=Ent\u00EAtes de r\u00E9ponse (XML)
 save_samplecount=Nombre d'\u00E9chantillon et d'erreur
 save_samplerdata=Donn\u00E9es d'\u00E9chantillon (XML)
 save_sentbytes=Nombre d'octets envoy\u00E9s
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 save_workbench=Sauvegarder le plan de travail
 sbind=Simple connexion/d\u00E9connexion
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 scope=Port\u00E9e
 search=Rechercher
 search_base=Base de recherche
 search_expand=Rechercher & D\u00E9plier
 search_filter=Filtre de recherche
 search_replace_all=Tout remplacer
 search_test=Recherche
 search_text_button_close=Fermer
 search_text_button_find=Rechercher
 search_text_button_next=Suivant
 search_text_chkbox_case=Consid\u00E9rer la casse
 search_text_chkbox_regexp=Exp. reguli\u00E8re
 search_text_field=Rechercher \:
 search_tree_matches={0} correspondance(s)
 search_text_replace=Remplacer par
 search_text_title_not_found=Pas trouv\u00E9
 search_tree_title=Rechercher dans l'arbre
 searchbase=Base de recherche
 searchfilter=Filtre de recherche
 searchtest=Recherche
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier
 send_file_mime_label=Type MIME
 send_file_param_name_label=Nom du param\u00E8tre
 server=Nom ou IP du serveur \:
 servername=Nom du serveur \:
 session_argument_name=Nom des arguments de la session
 setup_thread_group_title=Groupe d'unit\u00E9s de d\u00E9but
 should_save=Vous devez enregistrer le plan de test avant de le lancer.  \nSi vous utilisez des fichiers de donn\u00E9es (i.e. Source de donn\u00E9es CSV ou la fonction _StringFromFile), \nalors c'est particuli\u00E8rement important d'enregistrer d'abord votre script de test. \nVoulez-vous enregistrer maintenant votre plan de test ?
 shutdown=Eteindre
 simple_config_element=Configuration Simple
 simple_data_writer_title=Enregistreur de donn\u00E9es
 size_assertion_comparator_error_equal=est \u00E9gale \u00E0
 size_assertion_comparator_error_greater=est plus grand que
 size_assertion_comparator_error_greaterequal=est plus grand ou \u00E9gale \u00E0
 size_assertion_comparator_error_less=est inf\u00E9rieur \u00E0
 size_assertion_comparator_error_lessequal=est inf\u00E9rieur ou \u00E9gale \u00E0
 size_assertion_comparator_error_notequal=n'est pas \u00E9gale \u00E0
 size_assertion_comparator_label=Type de comparaison
 size_assertion_failure=Le r\u00E9sultat n''a pas la bonne taille \: il \u00E9tait de {0} octet(s), mais aurait d\u00FB \u00EAtre de {1} {2} octet(s).
 size_assertion_input_error=Entrer un entier positif valide svp.
 size_assertion_label=Taille en octets \:
 size_assertion_size_test=Taille \u00E0 v\u00E9rifier
 size_assertion_title=Assertion Taille
 smime_assertion_issuer_dn=Nom unique de l'\u00E9metteur \: 
 smime_assertion_message_position=V\u00E9rifier l'assertion sur le message \u00E0 partir de la position
 smime_assertion_not_signed=Message non sign\u00E9
 smime_assertion_signature=Signature
 smime_assertion_signer=Certificat signataire
 smime_assertion_signer_by_file=Fichier du certificat \: 
 smime_assertion_signer_constraints=V\u00E9rifier les valeurs \:
 smime_assertion_signer_dn=Nom unique du signataire \: 
 smime_assertion_signer_email=Adresse courriel du signataire \: 
 smime_assertion_signer_no_check=Pas de v\u00E9rification
 smime_assertion_signer_serial=Num\u00E9ro de s\u00E9rie \: 
 smime_assertion_title=Assertion SMIME
 smime_assertion_verify_signature=V\u00E9rifier la signature
 smtp_additional_settings=Param\u00E8tres suppl\u00E9mentaires
 smtp_attach_file=Fichier(s) attach\u00E9(s) \:
 smtp_attach_file_tooltip=S\u00E9parer les fichiers par le point-virgule ";"
 smtp_auth_settings=Param\u00E8tres d'authentification
 smtp_bcc=Adresse en copie cach\u00E9e (Bcc) \:
 smtp_cc=Adresse en copie (CC) \:
 smtp_default_port=(D\u00E9fauts \: SMTP \: 25, SSL \: 465, StartTLS \: 587)
 smtp_eml=Envoyer un message .eml \:
 smtp_enabledebug=Activer les traces de d\u00E9bogage ?
 smtp_enforcestarttls=Forcer le StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Force</b> le serveur a utiliser StartTLS.<br />Si il n'est pas s\u00E9lectionn\u00E9 et que le serveur SMTP ne supporte pas StartTLS, <br />une connexion SMTP normale sera utilis\u00E9e \u00E0 la place. <br /><i>Merci de noter</i> que la case \u00E0 cocher cr\u00E9\u00E9e un fichier dans /tmp/, <br />donc cela peut poser des probl\u00E8mes sous Windows.</html>
 smtp_from=Adresse exp\u00E9diteur (From) \:
 smtp_header_add=Ajouter une ent\u00EAte
 smtp_header_name=Nom d'ent\u00EAte
 smtp_header_remove=Supprimer
 smtp_header_value=Valeur d'ent\u00EAte
 smtp_mail_settings=Param\u00E8tres du courriel
 smtp_message=Message \:
 smtp_message_settings=Param\u00E8tres du message
 smtp_messagesize=Calculer la taille du message
 smtp_password=Mot de passe \:
 smtp_plainbody=Envoyer le message en texte (i.e. sans multipart/mixed)
 smtp_replyto=Adresse de r\u00E9ponse (Reply-To) \:
 smtp_sampler_title=Requ\u00EAte SMTP
 smtp_security_settings=Param\u00E8tres de s\u00E9curit\u00E9
 smtp_server=Serveur \:
 smtp_server_connection_timeout=D\u00E9lai d'attente de connexion \:
 smtp_server_port=Port \:
 smtp_server_settings=Param\u00E8tres du serveur
 smtp_server_timeout=D\u00E9lai d'attente de r\u00E9ponse \:
 smtp_server_timeouts_settings=D\u00E9lais d'attente (milli-secondes)
 smtp_subject=Sujet \:
 smtp_suppresssubj=Supprimer l'ent\u00EAte Sujet (Subject)
 smtp_timestamp=Ajouter un horodatage dans le sujet
 smtp_to=Adresse destinataire (To) \:
 smtp_trustall=Faire confiance \u00E0 tous les certificats
 smtp_trustall_tooltip=<html><b>Forcer</b> JMeter \u00E0 faire confiance \u00E0 tous les certificats, quelque soit l'autorit\u00E9 de certification du certificat.</html>
 smtp_truststore=Coffre de cl\u00E9s local \:
 smtp_truststore_tooltip=<html>Le chemin du coffre de confiance.<br />Les chemins relatifs sont d\u00E9termin\u00E9s \u00E0 partir du r\u00E9pertoire courant.<br />En cas d'\u00E9chec, c'est le r\u00E9pertoire contenant le script JMX qui est utilis\u00E9.</html>
 smtp_useauth=Utiliser l'authentification
 smtp_usenone=Pas de fonctionnalit\u00E9 de s\u00E9curit\u00E9
 smtp_username=Identifiant \:
 smtp_usessl=Utiliser SSL
 smtp_usestarttls=Utiliser StartTLS
 smtp_usetruststore=Utiliser le coffre de confiance local
 smtp_usetruststore_tooltip=<html>Autoriser JMeter \u00E0 utiliser le coffre de confiance local.</html>
 soap_action=Action Soap
 soap_data_title=Donn\u00E9es Soap/XML-RPC
 soap_sampler_file_invalid=Le nom de fichier r\u00E9f\u00E9rence un fichier absent ou sans droits de lecture\:
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC (DEPRECATED)
 soap_send_action=Envoyer l'action SOAP \:
 solinger=SO_LINGER\:
 split_function_separator=S\u00E9parateur utilis\u00E9 pour scinder le texte. Par d\u00E9faut , (virgule) est utilis\u00E9.
 split_function_string=Texte \u00E0 scinder
 ssl_alias_prompt=Veuillez entrer votre alias pr\u00E9f\u00E9r\u00E9
 ssl_alias_select=S\u00E9lectionner votre alias pour le test
 ssl_alias_title=Alias du client
 ssl_error_title=Probl\u00E8me de KeyStore
 ssl_pass_prompt=Entrer votre mot de passe
 ssl_pass_title=Mot de passe KeyStore
 ssl_port=Port SSL
 sslmanager=Gestionnaire SSL
 start=Lancer
 start_no_timers=Lancer sans pauses
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=Arr\u00EAt de toutes les unit\u00E9s de tests en cours. Le nombre d'unit\u00E9s actives est visible dans le coin haut droit de l'interface. Soyez patient, merci. 
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_host=H\u00F4te
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_encoding=Encodage du fichier (optionnel)
 string_from_file_file_name=Entrer le chemin (absolu ou relatif) du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Aller vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur Aller \u00E0
 system_sampler_stderr=Erreur standard (stderr) \:
 system_sampler_stdin=Entr\u00E9e standard (stdin) \:
 system_sampler_stdout=Sortie standard (stdout) \:
 system_sampler_title=Appel de processus syst\u00E8me
 table_visualizer_bytes=Octets
 table_visualizer_connect=\u00C9tabl. Conn.(ms)
 table_visualizer_latency=Latence
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
 table_visualizer_sent_bytes=Octets envoy\u00E9s
 table_visualizer_start_time=Heure d\u00E9but
 table_visualizer_status=Statut
 table_visualizer_success=Succ\u00E8s
 table_visualizer_thread_name=Nom d'unit\u00E9
 table_visualizer_warning=Alerte
 target_server=Serveur cible
 tcp_classname=Nom de classe TCPClient \:
 tcp_config_title=Param\u00E8tres TCP par d\u00E9faut
 tcp_nodelay=D\u00E9finir aucun d\u00E9lai (NoDelay)
 tcp_port=Num\u00E9ro de port \:
diff --git a/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java b/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
index 714d90732..ca771b99d 100644
--- a/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
+++ b/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
@@ -1,313 +1,330 @@
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
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.atomic.AtomicInteger;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.junit.Before;
 import org.junit.Test;
 
 public class ResponseAssertionTest {
 
     public ResponseAssertionTest() {
     }
 
     private ResponseAssertion assertion;
     private SampleResult sample;
     private AssertionResult result;
     
     @Before
     public void setUp() throws MalformedURLException {
         JMeterContext jmctx = JMeterContextService.getContext();
         assertion = new ResponseAssertion();
         assertion.setThreadContext(jmctx);
         sample = new SampleResult();
         JMeterVariables vars = new JMeterVariables();
         jmctx.setVariables(vars);
         jmctx.setPreviousResult(sample);
         sample.setResponseData("response Data\nline 2\n\nEOF", null);
         sample.setURL(new URL("http://localhost/Sampler/Data/"));
         sample.setResponseCode("401");
         sample.setResponseHeaders("X-Header: abcd");
+        sample.setRequestHeaders("X-reqHeader: cdef");
     }
 
     @Test
     public void testResponseAssertionEquals() throws Exception{
         assertion.unsetNotType();
         assertion.setToEqualsType();
         assertion.setTestFieldURL();
         assertion.addTestString("Sampler Label");
         assertion.addTestString("Sampler labelx");      
         result = assertion.getResult(sample);
         assertFailed();
 
         assertion.setToNotType();
         assertion.clearTestStrings();
         assertion.addTestString("Sampler LabeL");
         assertion.addTestString("Sampler Labelx");      
         result = assertion.getResult(sample);
         assertPassed();
     }
     
     @Test
-    public void testResponseAssertionHeaders() throws Exception{
+    public void testResponseAssertionResponseHeaders() throws Exception{
         assertion.unsetNotType();
         assertion.setToEqualsType();
         assertion.setTestFieldResponseHeaders();
         assertion.addTestString("X-Header: abcd");
         assertion.addTestString("X-Header: abcdx");
         result = assertion.getResult(sample);
         assertFailed();
 
         assertion.clearTestStrings();
         assertion.addTestString("X-Header: abcd");
         result = assertion.getResult(sample);
         assertPassed();
     }
     
     @Test
+    public void testResponseAssertionRequestHeaders() throws Exception{
+        assertion.unsetNotType();
+        assertion.setToEqualsType();
+        assertion.setTestFieldRequestHeaders();
+        assertion.addTestString("X-reqHeader: cdef");
+        assertion.addTestString("X-reqHeader: cdefx");
+        result = assertion.getResult(sample);
+        assertFailed();
+
+        assertion.clearTestStrings();
+        assertion.addTestString("X-reqHeader: cdef");
+        result = assertion.getResult(sample);
+        assertPassed();
+    }
+    
+    @Test
     public void testResponseAssertionContains() throws Exception{
         assertion.unsetNotType();
         assertion.setToContainsType();
         assertion.setTestFieldURL();
         assertion.addTestString("Sampler");
         assertion.addTestString("Label");
         assertion.addTestString(" x");
         
         result = assertion.getResult(sample);
         assertFailed();
         
         assertion.setToNotType();
         
         result = assertion.getResult(sample);
         assertFailed();
 
         assertion.clearTestStrings();
         assertion.addTestString("r l");
         result = assertion.getResult(sample);
         assertPassed();
 
         assertion.unsetNotType();
         assertion.setTestFieldResponseData();
         
         assertion.clearTestStrings();
         assertion.addTestString("line 2");
         result = assertion.getResult(sample);
         assertPassed();
         
         assertion.clearTestStrings();
         assertion.addTestString("line 2");
         assertion.addTestString("NOTINSAMPLEDATA");
         result = assertion.getResult(sample);
         assertFailed();
         
         assertion.clearTestStrings();
         assertion.setToOrType();
         assertion.addTestString("line 2");
         assertion.addTestString("NOTINSAMPLEDATA");
         result = assertion.getResult(sample);
         assertPassed();
         assertion.unsetOrType();
         
         assertion.clearTestStrings();
         assertion.setToOrType();
         assertion.addTestString("NOTINSAMPLEDATA");
         assertion.addTestString("line 2");
         result = assertion.getResult(sample);
         assertPassed();
         assertion.unsetOrType();
         
         assertion.clearTestStrings();
         assertion.setToOrType();
         assertion.addTestString("NOTINSAMPLEDATA");
         assertion.addTestString("NOTINSAMPLEDATA2");
         result = assertion.getResult(sample);
         assertFailed();
         assertion.unsetOrType();
         
         assertion.clearTestStrings();
         assertion.setToOrType();
         assertion.setToNotType();
         assertion.addTestString("line 2");
         assertion.addTestString("NOTINSAMPLEDATA2");
         result = assertion.getResult(sample);
         assertPassed();
         assertion.unsetOrType();
         assertion.unsetNotType();
 
         
         assertion.clearTestStrings();
         assertion.setToNotType();
         assertion.addTestString("NOTINSAMPLEDATA");
         result = assertion.getResult(sample);
         assertPassed();
         assertion.unsetNotType();
         
         
         assertion.clearTestStrings();
         assertion.addTestString("(?s)line \\d+.*EOF");
         result = assertion.getResult(sample);
         assertPassed();
 
         assertion.setTestFieldResponseCode();
         
         assertion.clearTestStrings();
         assertion.addTestString("401");
         result = assertion.getResult(sample);
         assertPassed();
 
     }
 
     // Bug 46831 - check can match dollars
     @Test
     public void testResponseAssertionContainsDollar() throws Exception {
         sample.setResponseData("value=\"${ID}\" Group$ctl00$drpEmails", null);
         assertion.unsetNotType();
         assertion.setToContainsType();
         assertion.setTestFieldResponseData();
         assertion.addTestString("value=\"\\${ID}\" Group\\$ctl00\\$drpEmails");
         
         result = assertion.getResult(sample);
         assertPassed();        
     }
     
     @Test
     public void testResponseAssertionSubstring() throws Exception{
         assertion.unsetNotType();
         assertion.setToSubstringType();
         assertion.setTestFieldURL();
         assertion.addTestString("Sampler");
         assertion.addTestString("Label");
         assertion.addTestString("+(");
         
         result = assertion.getResult(sample);
         assertFailed();
         
         assertion.setToNotType();
         
         result = assertion.getResult(sample);
         assertFailed();
 
         assertion.clearTestStrings();
         assertion.addTestString("r l");
         result = assertion.getResult(sample);
         assertPassed();
 
         assertion.unsetNotType();
         assertion.setTestFieldResponseData();
         
         assertion.clearTestStrings();
         assertion.addTestString("line 2");
         result = assertion.getResult(sample);
         assertPassed();
 
         assertion.clearTestStrings();
         assertion.addTestString("line 2\n\nEOF");
         result = assertion.getResult(sample);
         assertPassed();
 
         assertion.setTestFieldResponseCode();
         
         assertion.clearTestStrings();
         assertion.addTestString("401");
         result = assertion.getResult(sample);
         assertPassed();
 
     }
 
 //TODO - need a lot more tests
     
     private void assertPassed() throws Exception{
         assertNull(result.getFailureMessage(),result.getFailureMessage());
         assertFalse("Not expecting error: "+result.getFailureMessage(),result.isError());
         assertFalse("Not expecting error",result.isError());
         assertFalse("Not expecting failure",result.isFailure());        
     }
     
     private void assertFailed() throws Exception{
         assertNotNull(result.getFailureMessage());
         assertFalse("Should not be: Response was null","Response was null".equals(result.getFailureMessage()));
         assertFalse("Not expecting error: "+result.getFailureMessage(),result.isError());
         assertTrue("Expecting failure",result.isFailure());     
         
     }
     private AtomicInteger failed;
 
     @Test
     public void testThreadSafety() throws Exception {
         Thread[] threads = new Thread[100];
         CountDownLatch latch = new CountDownLatch(threads.length);
         for (int i = 0; i < threads.length; i++) {
             threads[i] = new TestThread(latch);
         }
         failed = new AtomicInteger(0);
         for (Thread thread : threads) {
             thread.start();
         }
         latch.await();
         assertEquals(failed.get(), 0);
     }
 
     class TestThread extends Thread {
         static final String TEST_STRING = "DAbale arroz a la zorra el abad.";
 
         // Used to be 'dábale', but caused trouble on Gump. Reasons
         // unknown.
         static final String TEST_PATTERN = ".*A.*\\.";
 
         private CountDownLatch latch;
 
         public TestThread(CountDownLatch latch) {
             this.latch = latch;
         }
 
         @Override
         public void run() {
             try {
                 ResponseAssertion assertion = new ResponseAssertion();
                 assertion.setTestFieldResponseData();
                 assertion.setToContainsType();
                 assertion.addTestString(TEST_PATTERN);
                 SampleResult response = new SampleResult();
                 response.setResponseData(TEST_STRING, null);
                 for (int i = 0; i < 100; i++) {
                     AssertionResult result;
                     result = assertion.getResult(response);
                     if (result.isFailure() || result.isError()) {
                         failed.incrementAndGet();
                     }
                 }
             } finally {
                 latch.countDown();
             }
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 6dd79f92b..b904216a8 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,388 +1,389 @@
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
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
 ]>
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 JMeter now requires Java 8. Ensure you use most up to date version.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>PDF Documentations have been migrated to HTML user manual</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>JMeter logging has been migrated to SLF4J and Log4j2, this involves changes in the way configuration is done. JMeter now relies on standard
     <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">log4j2 configuration</a> in file <code>log4j2.xml</code>
     </li>
     <li>The following jars have been removed after migration from Logkit to SLF4J (see <bugzilla>60589</bugzilla>):
         <ul>
             <li>ApacheJMeter_slf4j_logkit.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>commons-logging-1.2.jar</li>
             <li>excalibur-logger-1.1.jar</li>
             <li>logkit-2.0.jar</li>
         </ul>
     </li>
     <li>The <code>commons-httpclient-3.1.jar</code> has been removed after drop of HC3.1 support(see <bugzilla>60727</bugzilla>)</li>
     <li>JMeter now sets through <code>-Djava.security.egd=file:/dev/urandom</code> the algorithm for secure random</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
     <li>JMeter now uses by default Oracle Nashorn engine instead of Mozilla Rhino for better performances. This should not have an impact unless
     you use some advanced features. You can revert back to Rhino by settings property <code>javascript.use_rhino=true</code>. 
     You can read this <a href="https://wiki.openjdk.java.net/display/Nashorn/Rhino+Migration+Guide">migration guide</a> for more details on Nashorn. See <bugzilla>60672</bugzilla></li>
     <li><bug>60729</bug>The Random Variable Config Element now allows minimum==maximum. Previous versions logged an error when minimum==maximum and did not set the configured variable.</li>
     <li><bug>60730</bug>The JSON PostProcessor now sets the <code>_ALL</code> variable (assuming <code>Compute concatenation var</code> was checked)
     even if the JSON path matches only once. Previous versions did not set the <code>_ALL</code> variable in this case.</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li>SOAP/XML-RPC Request has been removed as part of <bugzilla>60727</bugzilla>. Use HTTP Request element as a replacement. See <a href="./build-ws-test-plan.html" >Building a WebService Test Plan</a></li>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li>Drop unused methods <code>org.apache.jmeter.protocol.http.control.HeaderManager#getSOAPHeader</code>
     and <code>org.apache.jmeter.protocol.http.control.HeaderManager#setSOAPHeader(Object)</code>
     </li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
     <li><bug>57242</bug>HTTP Authorization is not pre-emptively set with HttpClient4</li>
     <li><bug>60727</bug>Drop commons-httpclient-3.1 and related elements. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 	<li><bug>60740</bug>Support variable for all JMS messages (bytes, object, ...) and sources (file, folder), based on <pr>241</pr>. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><pr>259</pr>Refactored and reformatted SmtpSampler. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>60672</bug>JavaScript function / IfController : use Nashorn engine by default</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60590</bug>BackendListener : Add Influxdb BackendListenerClient implementation to JMeter. Partly based on <pr>246</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60591</bug>BackendListener : Add a time boxed sampling. Based on a <pr>237</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60678</bug>View Results Tree : Text renderer, search should not popup "Text Not Found"</li>
     <li><bug>60691</bug>View Results Tree : In Renderers (XPath, JSON Path Tester, RegExp Tester and CSS/JQuery Tester) lower panel is sometimes not visible as upper panel is too big and cannot be resized</li>
     <li><bug>60687</bug>Keep GUI responsive when many events are processed by View Results Tree, Summary Table and Log Panel.</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
     <li><bug>60710</bug>XPath Extractor : When content on which assertion applies is not XML, in View Results Tree the extractor is marked in Red and named SAXParseException. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60712</bug>Response Assertion : Improve Renderer of Patterns</li>
     <li><bug>59174</bug>Add a table with static hosts to the DNS Cache Manager. This enables better virtual hosts testing with HttpClient4.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li>Improve translation "save_as" in French. Based on a <pr>252</pr> by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60637</bug>Improve Statistics table design <figure image="dashboard/report_statistics.png" ></figure></li>
 </ul>
 
 <h3>General</h3>
 <ul>
 	<li><bug>58164</bug>Check if output file exist on all listener before start the loadtest</li>	
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60711</bug>Improve Delete button behaviour for Assertions / Header Manager / User Parameters GUIs</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
     <li><bug>59995</bug>Allow user to change font size with 2 new menu items and use <code>jmeter.hidpi.scale.factor</code> for scaling fonts. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60654</bug>Validation Feature : Be able to ignore BackendListener. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60646</bug>Workbench : Save it by default</li>
     <li><bug>60684</bug>Thread Group: Validate ended prematurely by Scheduler with 0 or very short duration. Contributed by Andrew Burton (andrewburtonatwh at gmail.com).</li>
     <li><bug>60589</bug>Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60565</bug>Migrate LogKit to SLF4J - Optimize logging statements. e.g, message format args, throwable args, unnecessary if-enabled-logging in simple ones, etc. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60564</bug>Migrate LogKit to SLF4J - Replace logkit loggers with slf4j ones and keep the current logkit binding solution for backward compatibility with plugins. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60664</bug>Add a UI menu to set log level. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><pr>276</pr>Added some translations for polish locale. Contributed by Bartosz Siewniak (barteksiewniak at gmail.com)</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.8 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to httpclient 4.5.3 (from 4.5.2)</li>
     <li>Updated to jodd 3.8.1 (from 3.8.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.22 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Updated to rsyntaxtextarea-2.6.1 (from 2.6.0)</li>
     <li>Updated to commons-net-3.6 (from 3.5)</li>
     <li>Converted the old pdf tutorials to xml.</li>
     <li><pr>255</pr>Utilised Java 8 (and 7) features to tidy up code. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
     <li><bug>60575</bug>HTTP GET Requests could have a content-type header without a body.</li>
     <li><bug>60682</bug>HTTP Request : Get method may fail on redirect due to Content-Length header being set</li>
     <li><bug>60643</bug>HTTP(S) Test Script Recorder doesn't correctly handle restart or start after stop. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60652</bug>PUT might leak file descriptors.</li>
     <li><bug>60689</bug><code>httpclient4.validate_after_inactivity</code> has no impact leading to usage of potentially stale/closed connections</li>
     <li><bug>60690</bug>Default values for "httpclient4.validate_after_inactivity" and "httpclient4.time_to_live" which are equal to each other makes validation useless</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
     <li><bug>55652</bug>JavaSampler silently resets classname if class can not be found</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60648</bug>GraphiteBackendListener can lose some metrics at end of test if test is very short</li>
     <li><bug>60650</bug>AbstractBackendListenerClient does not reset UserMetric between runs</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
     <li><bug>60607</bug>DNS Cache Manager configuration is ignored</li>
     <li><bug>60729</bug>The Random Variable Config Element should allow minimum==maximum</li>
     <li><bug>60730</bug>The JSON PostProcessor should set the <code>_ALL</code> variable even if the JSON path matches only once.</li>
+    <li><bug>60747</bug>Response Assertion : Add Request Headers to <code>Field to Test</code></li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60726</bug>Report / Dashboard : Top 5 errors by samplers must not take into account the series filtering</li>
 </ul>
     
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
     <li><bug>60621</bug>The "report-template" folder is missing from ApacheJMeter_config-3.1.jar in maven central</li>
     <li><bug>60744</bug>GUI elements are not cleaned up when reused during load of Test Plan which can lead them to be partially initialized with a previous state for a new Test Element</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
 <li>Andrew Burton (andrewburtonatwh at gmail.com)</li>
 <li>Woonsan Ko (woonsan at apache.org)</li>
 <li>Bartosz Siewniak (barteksiewniak at gmail.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 <li>Tuukka Mustonen (tuukka.mustonen at gmail.com) who gave us a lot of useful feedback which helped resolve <bugzilla>60689</bugzilla> and <bugzilla>60690</bugzilla></li>
 <li>Amar Darisa (amar.darisa at gmail.com) who helped us with his feedback on <bugzilla>60682</bugzilla></li>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
 
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads,
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a>
 The fix is to use JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later (but be aware that Java 9 is not certified yet for JMeter).
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a>
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
 
 </section>
 </body>
 </document>
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index c4678e4a5..e62ea3d59 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -3440,2081 +3440,2083 @@ By default, a Graphite implementation is provided.
         <property name="graphiteMetricsSender" required="Yes"><code>org.apache.jmeter.visualizers.backend.graphite.TextGraphiteMetricsSender</code> or <code>org.apache.jmeter.visualizers.backend.graphite.PickleGraphiteMetricsSender</code></property>
         <property name="graphiteHost" required="Yes">Graphite or InfluxDB (with Graphite plugin enabled) server host</property>
         <property name="graphitePort" required="Yes">Graphite or InfluxDB (with Graphite plugin enabled) server port, defaults to <code>2003</code>. Note <code>PickleGraphiteMetricsSender</code> (port <code>2004</code>) can only talk to Graphite server.</property>
         <property name="rootMetricsPrefix" required="Yes">Prefix of metrics sent to backend. Defaults to "<code>jmeter</code>."
         Note that JMeter does not add a separator between the root prefix and the samplerName which is why the trailing dot is currently needed.</property>
         <property name="summaryOnly" required="Yes">Only send a summary with no detail. Defaults to <code>true</code>.</property>
         <property name="samplersList" required="Yes">Defines the names (labels) of sample results to be sent to the back end. 
         If <code>useRegexpForSamplersList=false</code> this is a list of semi-colon separated names.
         If <code>useRegexpForSamplersList=true</code> this is a regular expression which will be matched against the names.</property>
         <property name="useRegexpForSamplersList" required="Yes">Consider samplersList as a regular expression to select the samplers for which you want to report metrics to backend. Defaults to <code>false</code>.</property>
         <property name="percentiles" required="Yes">The percentiles you want to send to the backend.
         A percentile may contain a fractional part, for example <code>12.5</code>.
         (The separator is always ".")
         List must be semicolon separated. Generally 3 or 4 values should be sufficient.</property>
     </properties>
     <p>See also <a href="realtime-results.html" >Real-time results</a> for more details.</p>
     <figure width="1265" height="581" image="grafana_dashboard.png">Grafana dashboard</figure>
     
     
     <p>Since JMeter 3.2, a new implementation (in Alpha state) has been added that allows writing directly in InfluxDB with a custom schema, it is called <code>InfluxdbBackendListenerClient</code> 
       The following parameters apply to the <a href="../api/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.html">InfluxdbBackendListenerClient</a> implementation:</p>
 
     <properties>
         <property name="influxdbMetricsSender" required="Yes"><code>org.apache.jmeter.visualizers.backend.influxdb.HttpMetricsSender</code></property>
         <property name="influxdbUrl" required="Yes">Influx URL (example : http://influxHost:8086/write?db=jmeter)</property>
         <property name="application" required="Yes">Name of tested application</property>
         <property name="measurement" required="Yes">Measurement as per <a href="https://docs.influxdata.com/influxdb/v1.1/write_protocols/line_protocol_reference/">Influx Line Protocol Reference</a>. Defaults to "<code>jmeter</code>."</property>
         <property name="summaryOnly" required="Yes">Only send a summary with no detail. Defaults to <code>true</code>.</property>
         <property name="samplersRegex" required="Yes">Regular expression which will be matched against the names of samples and sent to the back end.</property>
         <property name="testTitle" required="Yes">Test name. Defaults to <code>Test name</code>.</property>
         <property name="percentiles" required="Yes">The percentiles you want to send to the backend.
         A percentile may contain a fractional part, for example <code>12.5</code>.
         (The separator is always ".")
         List must be semicolon separated. Generally 3 or 4 values should be sufficient.</property>
     </properties>
     <p>See also <a href="realtime-results.html" >Real-time results</a> for more details.</p>     
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.4 Configuration Elements" anchor="config_elements">
 <description>
     <br></br>
     Configuration elements can be used to set up defaults and variables for later use by samplers.
     Note that these elements are processed at the start of the scope in which they are found, 
     i.e. before any samplers in the same scope.
     <br></br>
 </description>
 
 <component name="CSV Data Set Config" index="&sect-num;.4.1"  width="433" height="281" screenshot="csvdatasetconfig.png">
 <description>
     <p>
     CSV Data Set Config is used to read lines from a file, and split them into variables.
     It is easier to use than the <code>__CSVRead()</code> and <code>_StringFromFile()</code> functions.
     It is well suited to handling large numbers of variables, and is also useful for testing with
     "random" and unique values.</p>
     <p>Generating unique random values at run-time is expensive in terms of CPU and memory, so just create the data
     in advance of the test. If necessary, the "random" data from the file can be used in conjunction with
     a run-time parameter to create different sets of values from each run - e.g. using concatenation - which is
     much cheaper than generating everything at run-time.
     </p>
     <p>
     JMeter allows values to be quoted; this allows the value to contain a delimiter.
     If "<code>allow quoted data</code>" is enabled, a value may be enclosed in double-quotes.
     These are removed. To include double-quotes within a quoted field, use two double-quotes.
     For example:
     </p>
 <source>
 1,"2,3","4""5" =>
 1
 2,3
 4"5
 </source>
     <p>
     JMeter supports CSV files which have a header line defining the column names.
     To enable this, leave the "<code>Variable Names</code>" field empty. The correct delimiter must be provided.
     </p>
     <p>
     JMeter supports CSV files with quoted data that includes new-lines.
     </p>
     <p>
     By default, the file is only opened once, and each thread will use a different line from the file.
     However the order in which lines are passed to threads depends on the order in which they execute,
     which may vary between iterations.
     Lines are read at the start of each test iteration.
     The file name and mode are resolved in the first iteration.
     </p>
     <p>
     See the description of the Share mode below for additional options.
     If you want each thread to have its own set of values, then you will need to create a set of files,
     one for each thread. For example <code>test1.csv</code>, <code>test2.csv</code>, &hellip;, <code>test<em>n</em>.csv</code>. Use the filename 
     <code>test${__threadNum}.csv</code> and set the "<code>Sharing mode</code>" to "<code>Current thread</code>".
     </p>
     <note>CSV Dataset variables are defined at the start of each test iteration.
     As this is after configuration processing is completed,
     they cannot be used for some configuration items - such as JDBC Config - 
     that process their contents at configuration time (see <bugzilla>40394</bugzilla>)
     However the variables do work in the HTTP Auth Manager, as the <code>username</code> etc. are processed at run-time.
     </note>
     <p>
     As a special case, the string "<code>\t</code>" (without quotes) in the delimiter field is treated as a Tab.
     </p>
     <p>
     When the end of file (<code>EOF</code>) is reached, and the recycle option is <code>true</code>, reading starts again with the first line of the file.
     </p>
     <p>
     If the recycle option is <code>false</code>, and stopThread is <code>false</code>, then all the variables are set to <code>&lt;EOF&gt;</code> when the end of file is reached.
     This value can be changed by setting the JMeter property <code>csvdataset.eofstring</code>.
     </p>
     <p>
     If the Recycle option is <code>false</code>, and Stop Thread is <code>true</code>, then reaching <code>EOF</code> will cause the thread to be stopped.
     </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Filename" required="Yes">Name of the file to be read. 
   <b>Relative file names are resolved with respect to the path of the active test plan.</b>
   <b>For distributed testing, the CSV file must be stored on the server host system in the correct relative directory to where the jmeter server is started.</b>
   Absolute file names are also supported, but note that they are unlikely to work in remote mode, 
   unless the remote server has the same directory structure.
   If the same physical file is referenced in two different ways - e.g. <code>csvdata.txt</code> and <code>./csvdata.txt</code> -
   then these are treated as different files.
   If the OS does not distinguish between upper and lower case, <code>csvData.TXT</code> would also be opened separately. 
   </property>
   <property name="File Encoding" required="No">The encoding to be used to read the file, if not the platform default.</property>
   <property name="Variable Names" required="Yes">List of variable names (comma-delimited).
   JMeter supports CSV header lines:
   if the variable name field empty, then the first line of the file is read and interpreted as the list of column names.
   The names must be separated by the delimiter character. They can be quoted using double-quotes.
   </property>
   <property name="Ignore first line (only used if Variable Names is not empty)" required="No">
   Ignore first line of CSV file, it will only be used used if Variable Names is not empty, 
   if Variable Names is empty the first line must contain the headers.
   </property>
   <property name="Delimiter" required="Yes">Delimiter to be used to split the records in the file.
   If there are fewer values on the line than there are variables the remaining variables are not updated -
   so they will retain their previous value (if any).</property>
   <property name="Allow quoted data?" required="Yes">Should the CSV file allow values to be quoted?
   If enabled, then values can be enclosed in <code>"</code> - double-quote - allowing values to contain a delimiter.
   </property>
   <property name="Recycle on EOF?" required="Yes">Should the file be re-read from the beginning on reaching <code>EOF</code>? (default is <code>true</code>)</property>
   <property name="Stop thread on EOF?" required="Yes">Should the thread be stopped on <code>EOF</code>, if Recycle is false? (default is <code>false</code>)</property>
   <property name="Sharing mode" required="Yes">
   <ul>
   <li><code>All threads</code> - (the default) the file is shared between all the threads.</li>
   <li><code>Current thread group</code> - each file is opened once for each thread group in which the element appears</li>
   <li><code>Current thread</code> - each file is opened separately for each thread</li>
   <li><code>Identifier</code> - all threads sharing the same identifier share the same file.
   So for example if you have 4 thread groups, you could use a common id for two or more of the groups
   to share the file between them.
   Or you could use the thread number to share the file between the same thread numbers in different thread groups.
   </li>
   </ul>
   </property>
 </properties>
 </component>
 
 <component name="FTP Request Defaults" index="&sect-num;.4.2"  width="520" height="202" screenshot="ftp-config/ftp-request-defaults.png">
 <description></description>
 </component>
 
 <component name="DNS Cache Manager" index="&sect-num;.4.3"  width="712" height="387" screenshot="dns-cache-manager.png">
     <note>DNS Cache Manager is designed for using in the root of Thread Group or Test Plan. Do not place it as child element of particular HTTP Sampler
     </note>
     <note>DNS Cache Manager works only with HTTP requests using HTTPClient4 implementation.</note>
     <description><p>The DNS Cache Manager element allows to test applications, which have several servers behind load balancers (CDN, etc.), 
     when user receives content from different IP's. By default JMeter uses JVM DNS cache. That's why
     only one server from the cluster receives load. DNS Cache Manager resolves names for each thread separately each iteration and
     saves results of resolving to its internal DNS Cache, which is independent from both JVM and OS DNS caches.
     </p>
     <p>
     A mapping for static hosts can be used to simulate something like <code>/etc/hosts</code> file.
     These entries will be preferred over the custom resolver. <code>Use custom DNS resolver</code> has to be enabled,
     if you want to use this mapping.
     </p>
     <example title="Usage of static host table" anchor="static_host_table">
     <p>Say, you have a test server, that you want to reach with a name, that is not (yet) set up in your dns servers.
     For our example, this would be <code>www.example.com</code> for the server name, which you want to reach at the
     ip of the server <code>a123.another.example.org</code>.
     </p>
     <p>You could change your workstation and add an entry to your <code>/etc/hosts</code> file - or the equivalent for
     your OS, or add an entry to the Static Host Table of the DNS Cache Manager.
     </p>
     <p>You would type <code>www.example.com</code> into the first column (<code>Host</code>) and
     <code>a123.another.example.org</code> into the second column (<code>Hostname or IP address</code>).
     As the name of the second column implies, you could even use the ip address of your test server there.
     </p>
     <p>The ip address for the test server will be looked up by using the custom dns resolver. When none is given, the
     system dns resolver will be used.
     </p>
     <p>Now you can use <code>www.example.com</code> in your HTTPClient4 samplers and the requests will be made against
     <code>a123.another.example.org</code> with all headers set to <code>www.example.com</code>.
     </p>
     </example>
     </description>
     <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
     <property name="Clear cache each Iteration" required="No">If selected, DNS cache of every  Thread is cleared each time new iteration is started.</property>
     <property name="Use system DNS resolver" required="N/A">System DNS resolver will be used. For correct work edit
        <code>$JAVA_HOME/jre/lib/security/java.security</code> and add <code>networkaddress.cache.ttl=0</code> 
     </property>
     <property name="Use custom DNS resolver" required="N/A">Custom DNS resolver (from dnsjava library) will be used.</property>
     <property name="Hostname or IP address" required="No">List of DNS servers to use. If empty, network configuration DNS will used.</property>
     <property name="Add Button" required="N/A">Add an entry to the DNS servers table.</property>
     <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
     <property name="Host and Hostname or IP address" required="No">Mapping of hostnames to a static host entry which will be resolved using the custom DNS resolver.</property>
     <property name="Add static host Button" required="N/A">Add an entry to the static hosts table.</property>
     <property name="Delete static host Button" required="N/A">Delete the currently selected static host in the table.</property>
     </properties>
 </component>
 
 <component name="HTTP Authorization Manager" index="&sect-num;.4.4"  width="538" height="340" screenshot="http-config/http-auth-manager.png">
 <note>If there is more than one Authorization Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.</note>
 
 <description>
 <p>The Authorization Manager lets you specify one or more user logins for web pages that are
 restricted using server authentication.  You see this type of authentication when you use
 your browser to access a restricted page, and your browser displays a login dialog box.  JMeter
 transmits the login information when it encounters this type of page.</p>
 <p>
 The Authorization headers may not be shown in the Tree View Listener "<code>Request</code>" tab.
 The Java implementation does pre-emptive authentication, but it does not
 return the Authorization header when JMeter fetches the headers.
 The HttpComponents (HC 4.5.X) implementation defaults to pre-emptive since 3.2 and the header will be shown.
 To disable this, set the values as below, in which case authentication will only be performed in response to a challenge.
 </p>
 <p>
 In the file <code>jmeter.properties</code> set <code>httpclient4.auth.preemptive=false</code>
 </p>
 <note>
 Note: the above settings only apply to the HttpClient sampler.
 </note>
 <note>
 When looking for a match against a URL, JMeter checks each entry in turn, and stops when it finds the first match.
 Thus the most specific URLs should appear first in the list, followed by less specific ones.
 Duplicate URLs will be ignored.
 If you want to use different usernames/passwords for different threads, you can use variables.
 These can be set up using a <complink name="CSV Data Set Config"/> Element (for example).
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
         <property name="Clear auth on each iteration?" required="Yes">Used by Kerberos authentication. If checked, authentication will be done on each iteration of Main Thread Group loop even if it has already been done in a previous one.
         This is usually useful if each main thread group iteration represents behaviour of one Virtual User.
         </property>
   <property name="Base URL" required="Yes">A partial or complete URL that matches one or more HTTP Request URLs.  As an example,
 say you specify a Base URL of "<code>http://localhost/restricted/</code>" with a <code>Username</code> of "<code>jmeter</code>" and
 a <code>Password</code> of "<code>jmeter</code>".  If you send an HTTP request to the URL
 "<code>http://localhost/restricted/ant/myPage.html</code>", the Authorization Manager sends the login
 information for the user named, "<code>jmeter</code>".</property>
   <property name="Username" required="Yes">The username to authorize.</property>
   <property name="Password" required="Yes">The password for the user. (N.B. this is stored unencrypted in the test plan)</property>
   <property name="Domain" required="No">The domain to use for NTLM.</property>
   <property name="Realm" required="No">The realm to use for NTLM.</property>
   <property name="Mechanism" required="No">Type of authentication to perform. JMeter can perform different types of authentications based on used Http Samplers:
 <dl>
 <dt>Java</dt><dd><code>BASIC</code></dd>
 <dt>HttpClient 4</dt><dd><code>BASIC</code>, <code>DIGEST</code> and <code>Kerberos</code></dd>
 </dl>
 </property>
 </properties>
 <note>
 The Realm only applies to the HttpClient sampler.
 </note>
 <br></br>
 <b>Kerberos Configuration:</b>
 <p>To configure Kerberos you need to setup at least two JVM system properties:</p>
 <ul>
     <li><code>-Djava.security.krb5.conf=krb5.conf</code></li>
     <li><code>-Djava.security.auth.login.config=jaas.conf</code></li>
 </ul>
 <p>
 You can also configure those two properties in the file <code>bin/system.properties</code>.
 Look at the two sample configuration files (<code>krb5.conf</code> and <code>jaas.conf</code>) located in the jmeter <code>bin</code> folder
 for references to more documentation, and tweak them to match your Kerberos configuration.
 </p>
 <p>
 When generating a SPN for Kerberos SPNEGO authentication IE and Firefox will omit the port number
 from the URL. Chrome has an option (<code>--enable-auth-negotiate-port</code>) to include the port
 number if it differs from the standard ones (<code>80</code> and <code>443</code>). That behavior
 can be emulated by setting the following jmeter property as below.
 </p>
 <p>
 In <code>jmeter.properties</code> or <code>user.properties</code>, set:
 </p>
 <ul>
 <li><code>kerberos.spnego.strip_port=false</code></li>
 </ul>
 <br></br>
 <b>Controls:</b>
 <ul>
   <li><code>Add</code> Button - Add an entry to the authorization table.</li>
   <li><code>Delete</code> Button - Delete the currently selected table entry.</li>
   <li><code>Load</code> Button - Load a previously saved authorization table and add the entries to the existing
 authorization table entries.</li>
   <li><code>Save As</code> Button - Save the current authorization table to a file.</li>
 </ul>
 
 <note>When you save the Test Plan, JMeter automatically saves all of the authorization
 table entries - including any passwords, which are not encrypted.</note>
 
 <example title="Authorization Example" anchor="authorization_example">
 
 <p><a href="../demos/AuthManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan on a local server that sends three HTTP requests, two requiring a login and the
 other is open to everyone.  See figure 10 to see the makeup of our Test Plan.  On our server, we have a restricted
 directory named, "<code>secret</code>", which contains two files, "<code>index.html</code>" and "<code>index2.html</code>".  We created a login id named, "<code>kevin</code>",
 which has a password of "<code>spot</code>".  So, in our Authorization Manager, we created an entry for the restricted directory and
 a username and password (see figure 11).  The two HTTP requests named "<code>SecretPage1</code>" and "<code>SecretPage2</code>" make requests
 to "<code>/secret/index.html</code>" and "<code>/secret/index2.html</code>".  The other HTTP request, named "<code>NoSecretPage</code>" makes a request to
 "<code>/index.html</code>".</p>
 
 <figure width="452" height="177" image="http-config/auth-manager-example1a.png">Figure 10 - Test Plan</figure>
 <figure width="641" height="329" image="http-config/auth-manager-example1b.png">Figure 11 - Authorization Manager Control Panel</figure>
 
 <p>When we run the Test Plan, JMeter looks in the Authorization table for the URL it is requesting.  If the Base URL matches
 the URL, then JMeter passes this information along with the request.</p>
 
 <note>You can download the Test Plan, but since it is built as a test for our local server, you will not
 be able to run it.  However, you can use it as a reference in constructing your own Test Plan.</note>
 </example>
 
 </component>
 
 <component name="HTTP Cache Manager" index="&sect-num;.4.5"  width="511" height="196" screenshot="http-config/http-cache-manager.png">
 <description>
 <p>
 The HTTP Cache Manager is used to add caching functionality to HTTP requests within its scope to simulate browser cache feature.
 Each Virtual User thread has its own Cache. By default, Cache Manager will store up to 5000 items in cache per Virtual User thread, using LRU algorithm. 
 Use property "<code>maxSize</code>" to modify this value. Note that the more you increase this value the more HTTP Cache Manager will consume memory, so be sure to adapt the <code>-Xmx</code> jvm option accordingly.
 </p>
 <p>
 If a sample is successful (i.e. has response code <code>2xx</code>) then the <code>Last-Modified</code> and <code>Etag</code> (and <code>Expired</code> if relevant) values are saved for the URL.
 Before executing the next sample, the sampler checks to see if there is an entry in the cache, 
 and if so, the <code>If-Last-Modified</code> and <code>If-None-Match</code> conditional headers are set for the request.
 </p>
 <p>
 Additionally, if the "<code>Use Cache-Control/Expires header</code>" option is selected, then the <code>Cache-Control</code>/<code>Expires</code> value is checked against the current time.
 If the request is a <code>GET</code> request, and the timestamp is in the future, then the sampler returns immediately,
 without requesting the URL from the remote server. This is intended to emulate browser behaviour.
 Note that if <code>Cache-Control</code> header is "<code>no-cache</code>", the response will be stored in cache as pre-expired,
 so will generate a conditional <code>GET</code> request.
 If <code>Cache-Control</code> has any other value, 
 the "<code>max-age</code>" expiry option is processed to compute entry lifetime, if missing then expire header will be used, if also missing entry will be cached 
 as specified in <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a> section 13.2.4. using <code>Last-Modified</code> time and response Date.
 </p>
 <note>
 If the requested document has not changed since it was cached, then the response body will be empty.
 Likewise if the <code>Expires</code> date is in the future.
 This may cause problems for Assertions.
 </note>
 <note>Responses with a <code>Vary</code> header will not be cached.</note>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear cache each iteration" required="Yes">
   If selected, then the cache is cleared at the start of the thread.
   </property>
   <property name="Use Cache Control/Expires header when processing GET requests" required="Yes">See description above.</property>
   <property name="Max Number of elements in cache" required="Yes">See description above.</property>
 </properties>
 </component>
 
 <component name="HTTP Cookie Manager" index="&sect-num;.4.6"  width="653" height="373" screenshot="http-config/http-cookie-manager.png">
 
 <note>If there is more than one Cookie Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.
 Also, a cookie stored in one cookie manager is not available to any other manager,
 so use multiple Cookie Managers with care.</note>
 
 <description><p>The Cookie Manager element has two functions:<br></br>
 First, it stores and sends cookies just like a web browser. If you have an HTTP Request and
 the response contains a cookie, the Cookie Manager automatically stores that cookie and will
 use it for all future requests to that particular web site.  Each JMeter thread has its own
 "cookie storage area".  So, if you are testing a web site that uses a cookie for storing
 session information, each JMeter thread will have its own session.
 Note that such cookies do not appear on the Cookie Manager display, but they can be seen using
 the <complink name="View Results Tree"/> Listener.
 </p>
 <p>
 JMeter checks that received cookies are valid for the URL.
 This means that cross-domain cookies are not stored.
 If you have bugged behaviour or want Cross-Domain cookies to be used, define the JMeter property "<code>CookieManager.check.cookies=false</code>".
 </p>
 <p>
 Received Cookies can be stored as JMeter thread variables.
 To save cookies as variables, define the property "<code>CookieManager.save.cookies=true</code>".
 Also, cookies names are prefixed with "<code>COOKIE_</code>" before they are stored (this avoids accidental corruption of local variables)
 To revert to the original behaviour, define the property "<code>CookieManager.name.prefix= </code>" (one or more spaces).
 If enabled, the value of a cookie with the name <code>TEST</code> can be referred to as <code>${COOKIE_TEST}</code>.
 </p>
 <p>Second, you can manually add a cookie to the Cookie Manager.  However, if you do this,
 the cookie will be shared by all JMeter threads.</p>
 <p>Note that such Cookies are created with an Expiration time far in the future</p>
 <p>
 Cookies with <code>null</code> values are ignored by default.
 This can be changed by setting the JMeter property: <code>CookieManager.delete_null_cookies=false</code>.
 Note that this also applies to manually defined cookies - any such cookies will be removed from the display when it is updated.
 Note also that the cookie name must be unique - if a second cookie is defined with the same name, it will replace the first.
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear Cookies each Iteration" required="Yes">If selected, all server-defined cookies are cleared each time the main Thread Group loop is executed.
   Any cookie defined in the GUI are not cleared.</property>
   <property name="Cookie Policy" required="Yes">The cookie policy that will be used to manage the cookies. 
   "<code>standard</code>" is the default since 3.0, and should work in most cases. 
   See <a href="https://hc.apache.org/httpcomponents-client-ga/tutorial/html/statemgmt.html#d5e515">Cookie specifications</a> and 
   <a href="http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/cookie/CookieSpec.html">CookieSpec implementations</a>
   [Note: "<code>ignoreCookies</code>" is equivalent to omitting the CookieManager.]
  </property>
  <property name="Implementation" required="Yes"><code>HC4CookieHandler</code> (HttpClient 4.5.X API). 
   Default is <code>HC4CookieHandler</code> since 3.0.
   <br></br>
   <i>[Note: If you have a website to test with IPv6 address, choose <code>HC4CookieHandler</code> (IPv6 compliant)]</i></property>
   <property name="User-Defined Cookies" required="No (discouraged, unless you know what you're doing)">This
   gives you the opportunity to use hardcoded cookies that will be used by all threads during the test execution.
   <br></br>
   The "<code>domain</code>" is the hostname of the server (without <code>http://</code>); the port is currently ignored.
   </property>
   <property name="Add Button" required="N/A">Add an entry to the cookie table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved cookie table and add the entries to the existing
 cookie table entries.</property>
   <property name="Save As Button" required="N/A">
   Save the current cookie table to a file (does not save any cookies extracted from HTTP Responses).
   </property>
 </properties>
 
 </component>
 
 <component name="HTTP Request Defaults" index="&sect-num;.4.7" width="879" height="469" 
          screenshot="http-config/http-request-defaults.png">
 <description><p>This element lets you set default values that your HTTP Request controllers use.  For example, if you are
 creating a Test Plan with 25 HTTP Request controllers and all of the requests are being sent to the same server,
 you could add a single HTTP Request Defaults element with the "<code>Server Name or IP</code>" field filled in.  Then, when
 you add the 25 HTTP Request controllers, leave the "<code>Server Name or IP</code>" field empty.  The controllers will inherit
 this field value from the HTTP Request Defaults element.</p>
 <note>
 All port values are treated equally; a sampler that does not specify a port will use the HTTP Request Defaults port, if one is provided.
 </note>
 </description>
 <figure width="881" height="256" image="http-config/http-request-defaults-advanced-tab.png">HTTP Request Advanced config fields</figure>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Server" required="No">Domain name or IP address of the web server. e.g. <code>www.example.com</code>. [Do not include the <code>http://</code> prefix.</property>
         <property name="Port" required="No">Port the web server is listening to.</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
         <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient4</code>. 
         If not specified the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the <code>Java</code> implementation is used.</property>
         <property name="Protocol" required="No"><code>HTTP</code> or <code>HTTPS</code>.</property>
         <property name="Content encoding" required="No">The encoding to be used for the request.</property>
         <property name="Path" required="No">The path to resource (for example, <code>/servlets/myServlet</code>). If the
         resource requires query string parameters, add them below in the "<code>Send Parameters With the Request</code>" section.
         Note that the path is the default for the full path, not a prefix to be applied to paths
         specified on the HTTP Request screens.
         </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>.  The query string will be generated in the correct fashion, depending on
         the choice of "<code>Method</code>" you made (i.e. if you chose <code>GET</code>, the query string will be
         appended to the URL, if <code>POST</code>, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the <code>http://</code> prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server. (N.B. this is stored unencrypted in the test plan)</property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from <code>http://example.com/</code>, use the expression:
         <code>http://example\.com/.*</code>
         </property>
 </properties>
 <note>
 Note: radio buttons only have two states - on or off.
 This makes it impossible to override settings consistently
 - does off mean off, or does it mean use the current default?
 JMeter uses the latter (otherwise defaults would not work at all).
 So if the button is off, then a later element can set it on,
 but if the button is on, a later element cannot set it off.
 </note>
 </component>
 
 <component name="HTTP Header Manager" index="&sect-num;.4.8"  width="767" height="239" screenshot="http-config/http-header-manager.png">
 <description>
 <p>The Header Manager lets you add or override HTTP request headers.</p>
 <p>
 <b>JMeter now supports multiple Header Managers</b>. The header entries are merged to form the list for the sampler.
 If an entry to be merged matches an existing header name, it replaces the previous entry,
 unless the entry value is empty, in which case any existing entry is removed.
 This allows one to set up a default set of headers, and apply adjustments to particular samplers. 
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Name (Header)" required="No (You should have at least one, however)">Name of the request header.
         Two common request headers you may want to experiment with
 are "<code>User-Agent</code>" and "<code>Referer</code>".</property>
   <property name="Value" required="No (You should have at least one, however)">Request header value.</property>
   <property name="Add Button" required="N/A">Add an entry to the header table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved header table and add the entries to the existing
 header table entries.</property>
   <property name="Save As Button" required="N/A">Save the current header table to a file.</property>
 </properties>
 
 <example title="Header Manager example" anchor="header_manager_example">
 
 <p><a href="../demos/HeaderManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan
 that tells JMeter to override the default "<code>User-Agent</code>" request header and use a particular Internet Explorer agent string
 instead. (see figures 12 and 13).</p>
 
 <figure width="247" height="121" image="http-config/header-manager-example1a.png">Figure 12 - Test Plan</figure>
 <figure image="http-config/header-manager-example1b.png">Figure 13 - Header Manager Control Panel</figure>
 </example>
 
 </component>
 
 <component name="Java Request Defaults" index="&sect-num;.4.9"  width="685" height="373" screenshot="java_defaults.png">
 <description><p>The Java Request Defaults component lets you set default values for Java testing.  See the <complink name="Java Request" />.</p>
 </description>
 
 </component>
 
 <component name="JDBC Connection Configuration" index="&sect-num;.4.10" 
                  width="474" height="458" screenshot="jdbc-config/jdbc-conn-config.png">
     <description>Creates a database connection (used by <complink name="JDBC Request"/>Sampler)
      from the supplied JDBC Connection settings. The connection may be optionally pooled between threads.
      Otherwise each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      The used pool is DBCP, see <a href="https://commons.apache.org/proper/commons-dbcp/configuration.html" >BasicDataSource Configuration Parameters</a>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">The name of the variable the connection is tied to.  
         Multiple connections can be used, each tied to a different variable, allowing JDBC Samplers
         to select the appropriate connection.
         <note>Each name must be different. If there are two configuration elements using the same name,
         only one will be saved. JMeter logs a message if a duplicate name is detected.</note>
         </property>
         <property name="Max Number of Connections" required="Yes">
         Maximum number of connections allowed in the pool.
         In most cases, <b>set this to zero (0)</b>.
         This means that each thread will get its own pool with a single connection in it, i.e.
         the connections are not shared between threads.
         <br />
         If you really want to use shared pooling (why?), then set the max count to the same as the number of threads
         to ensure threads don't wait on each other.
         </property>
         <property name="Max Wait (ms)" required="Yes">Pool throws an error if the timeout period is exceeded in the 
         process of trying to retrieve a connection, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getMaxWaitMillis--" >BasicDataSource.html#getMaxWaitMillis</a></property>
         <property name="Time Between Eviction Runs (ms)" required="Yes">The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run. (Defaults to "<code>60000</code>", 1 minute).
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTimeBetweenEvictionRunsMillis--" >BasicDataSource.html#getTimeBetweenEvictionRunsMillis</a></property>
         <property name="Auto Commit" required="Yes">Turn auto commit on or off for the connections.</property>
         <property name="Test While Idle" required="Yes">Test idle connections of the pool, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTestWhileIdle--">BasicDataSource.html#getTestWhileIdle</a>. 
         Validation Query will be used to test it.</property>
         <property name="Soft Min Evictable Idle Time(ms)" required="Yes">Minimum amount of time a connection may sit idle in the pool before it is eligible for eviction by the idle object evictor, with the extra condition that at least <code>minIdle</code> connections remain in the pool.
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getSoftMinEvictableIdleTimeMillis--">BasicDataSource.html#getSoftMinEvictableIdleTimeMillis</a>.
         Defaults to 5000 (5 seconds)
         </property>
         <property name="Validation Query" required="No">A simple query used to determine if the database is still responding.
         This defaults to the '<code>isValid()</code>' method of the jdbc driver, which is suitable for many databases.
         However some may require a different query; for example Oracle something like '<code>SELECT 1 FROM DUAL</code>' could be used.
         <note>Note this validation query is used on pool creation to validate it even if "<code>Test While Idle</code>" suggests query would only be used on idle connections.
         This is DBCP behaviour.</note>
         </property>
         <property name="Database URL" required="Yes">JDBC Connection string for the database.</property>
         <property name="JDBC Driver class" required="Yes">Fully qualified name of driver class. (Must be in
         JMeter's classpath - easiest to copy <code>.jar</code> file into JMeter's <code>/lib</code> directory).</property>
         <property name="Username" required="No">Name of user to connect as.</property>
         <property name="Password" required="No">Password to connect with. (N.B. this is stored unencrypted in the test plan)</property>
     </properties>
 <p>Different databases and JDBC drivers require different JDBC settings. 
 The Database URL and JDBC Driver class are defined by the provider of the JDBC implementation.</p>
 <p>Some possible settings are shown below. Please check the exact details in the JDBC driver documentation.</p>
 
 <p>
 If JMeter reports <code>No suitable driver</code>, then this could mean either:
 </p>
 <ul>
 <li>The driver class was not found. In this case, there will be a log message such as <code>DataSourceElement: Could not load driver: {classname} java.lang.ClassNotFoundException: {classname}</code></li>
 <li>The driver class was found, but the class does not support the connection string. This could be because of a syntax error in the connection string, or because the wrong classname was used.</li>
 </ul>
 <p>
 If the database server is not running or is not accessible, then JMeter will report a <code>java.net.ConnectException</code>.
 </p>
 <p>Some examples for databases and their parameters are given below.</p>
 <dl>
   <dt>MySQL</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>com.mysql.jdbc.Driver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:mysql://host[:port]/dbname</code></dd>
     </dl>
   </dd>
   <dt>PostgreSQL</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>org.postgresql.Driver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:postgresql:{dbname}</code></dd>
     </dl>
   </dd>
   <dt>Oracle</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>oracle.jdbc.OracleDriver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:oracle:thin:@//host:port/service</code> OR <code>jdbc:oracle:thin:@(description=(address=(host={mc-name})(protocol=tcp)(port={port-no}))(connect_data=(sid={sid})))</code></dd>
     </dl>
   </dd>
   <dt>Ingress (2006)</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>ingres.jdbc.IngresDriver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:ingres://host:port/db[;attr=value]</code></dd>
     </dl>
   </dd>
   <dt>Microsoft SQL Server (MS JDBC driver)</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>com.microsoft.sqlserver.jdbc.SQLServerDriver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:sqlserver://host:port;DatabaseName=dbname</code></dd>
     </dl>
   </dd>
   <dt>Apache Derby</dt>
   <dd>
     <dl>
       <dt>Driver class</dt>
       <dd><code>org.apache.derby.jdbc.ClientDriver</code></dd>
       <dt>Database URL</dt>
       <dd><code>jdbc:derby://server[:port]/databaseName[;URLAttributes=value[;&hellip;]]</code></dd>
     </dl>
   </dd>
 </dl>
 <note>The above may not be correct - please check the relevant JDBC driver documentation.</note>
 </component>
 
 
 <component name="Keystore Configuration" index="&sect-num;.4.11"  width="441" height="189" screenshot="keystore_config.png">
 <description><p>The Keystore Config Element lets you configure how Keystore will be loaded and which keys it will use.
 This component is typically used in HTTPS scenarios where you don't want to take into account keystore initialization into account in response time.</p>
 <p>To use this element, you need to setup first a Java Key Store with the client certificates you want to test, to do that:
 </p>
 <ol>
 <li>Create your certificates either with Java <code>keytool</code> utility or through your PKI</li>
 <li>If created by PKI, import your keys in Java Key Store by converting them to a format acceptable by JKS</li>
 <li>Then reference the keystore file through the two JVM properties (or add them in <code>system.properties</code>):
     <ul>
         <li><code>-Djavax.net.ssl.keyStore=path_to_keystore</code></li>
         <li><code>-Djavax.net.ssl.keyStorePassword=password_of_keystore</code></li>
     </ul>
 </li>
 </ol>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Preload" required="Yes">Wether or not to preload Keystore. Setting it to <code>true</code> is usually the best option.</property>
   <property name="Variable name holding certificate alias" required="False">Variable name that will contain the alias to use for authentication by client certificate. Variable value will be filled from CSV Data Set for example. In the screenshot, "<code>certificat_ssl</code>" will also be a variable in CSV Data Set.</property>
   <property name="Alias Start Index" required="Yes">The index of the first key to use in Keystore, 0-based.</property>
   <property name="Alias End Index" required="Yes">The index of the last key to use in Keystore, 0-based. When using "<code>Variable name holding certificate alias</code>" ensure it is large enough so that all keys are loaded at startup.</property>
 </properties>
 <note>
 To make JMeter use more than one certificate you need to ensure that:
 <ul>
 <li><code>https.use.cached.ssl.context=false</code> is set in <code>jmeter.properties</code> or <code>user.properties</code></li>
 <li>You use HTTPClient 4 implementation for HTTP Request</li>
 </ul>
 </note>
 </component>
 
 <component name="Login Config Element" index="&sect-num;.4.12"  width="459" height="126" screenshot="login-config.png">
 <description><p>The Login Config Element lets you add or override username and password settings in samplers that use username and password as part of their setup.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Username" required="No">The default username to use.</property>
   <property name="Password" required="No">The default password to use. (N.B. this is stored unencrypted in the test plan)</property>
 </properties>
 
 </component>
 
 <component name="LDAP Request Defaults" index="&sect-num;.4.13"  width="689" height="232" screenshot="ldap_defaults.png">
 <description><p>The LDAP Request Defaults component lets you set default values for LDAP testing.  See the <complink name="LDAP Request"/>.</p>
 </description>
 
 </component>
 
 <component name="LDAP Extended Request Defaults" index="&sect-num;.4.14"  width="686" height="184" screenshot="ldapext_defaults.png">
 <description><p>The LDAP Extended Request Defaults component lets you set default values for extended LDAP testing.  See the <complink name="LDAP Extended Request"/>.</p>
 </description>
 
 </component>
 
 <component name="TCP Sampler Config" index="&sect-num;.4.15"  width="826" height="450" screenshot="tcpsamplerconfig.png">
 <description>
         <p>
     The TCP Sampler Config provides default data for the TCP Sampler
     </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property <code>tcp.handler</code>, failing that <code>TCPClientImpl</code>.</property>
   <property name="ServerName or IP" required="">Name or IP of TCP server</property>
   <property name="Port Number" required="">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Close connection" required="Yes">If selected, the connection will be closed after running the sampler.</property>  
   <property name="SO_LINGER" required="No">Enable/disable <code>SO_LINGER</code> with the specified linger time in seconds when a socket is created. If you set "<code>SO_LINGER</code>" value as <code>0</code>, you may prevent large numbers of sockets sitting around with a <code>TIME_WAIT</code> status.</property>
   <property name="End of line(EOL) byte value" required="No">Byte value for end of line, set this to a value outside the range <code>-128</code> to <code>+127</code> to skip eol checking. You may set this in <code>jmeter.properties</code> file as well with the <code>tcp.eolByte</code> property. If you set this in TCP Sampler Config and in <code>jmeter.properties</code> file at the same time, the setting value in the TCP Sampler Config will be used.</property>
   <property name="Connect Timeout" required="No">Connect Timeout (milliseconds, 0 disables).</property>
   <property name="Response Timeout" required="No">Response Timeout (milliseconds, 0 disables).</property>
   <property name="Set Nodelay" required="">Should the nodelay property be set?</property>
   <property name="Text to Send" required="">Text to be sent</property>
 </properties>
 </component>
 
 <component name="User Defined Variables" index="&sect-num;.4.16"  width="741" height="266" screenshot="user_defined_variables.png">
 <description><p>The User Defined Variables element lets you define an <b>initial set of variables</b>, just as in the <complink name="Test Plan" />.
 <note>
 Note that all the UDV elements in a test plan - no matter where they are - are processed at the start.
 </note>
 So you cannot reference variables which are defined as part of a test run, e.g. in a Post-Processor.
 </p>
 <p>
 <b>
 UDVs should not be used with functions that generate different results each time they are called.
 Only the result of the first function call will be saved in the variable. 
 </b>
 However, UDVs can be used with functions such as <code>__P()</code>, for example:
 </p>
 <source>
 HOST      ${__P(host,localhost)} 
 </source>
 <p>
 which would define the variable "<code>HOST</code>" to have the value of the JMeter property "<code>host</code>", defaulting to "<code>localhost</code>" if not defined.
 </p>
 <p>
 For defining variables during a test run, see <complink name="User Parameters"/>.
 UDVs are processed in the order they appear in the Plan, from top to bottom.
 </p>
 <p>
 For simplicity, it is suggested that UDVs are placed only at the start of a Thread Group
 (or perhaps under the Test Plan itself).
 </p>
 <p>
 Once the Test Plan and all UDVs have been processed, the resulting set of variables is
 copied to each thread to provide the initial set of variables.
 </p>
 <p>
 If a runtime element such as a User Parameters Pre-Processor or Regular Expression Extractor defines a variable
 with the same name as one of the UDV variables, then this will replace the initial value, and all other test
 elements in the thread will see the updated value.
 </p>
 </description>
 <note>
 If you have more than one Thread Group, make sure you use different names for different values, as UDVs are shared between Thread Groups.
 Also, the variables are not available for use until after the element has been processed, 
 so you cannot reference variables that are defined in the same element.
 You can reference variables defined in earlier UDVs or on the Test Plan. 
 </note>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="User Defined Variables" required="">Variable name/value pairs. The string under the "<code>Name</code>"
       column is what you'll need to place inside the brackets in <code>${&hellip;}</code> constructs to use the variables later on. The
       whole <code>${&hellip;}</code> will then be replaced by the string in the "<code>Value</code>" column.</property>
 </properties>
 </component>
 
 <component name="Random Variable" index="&sect-num;.4.17"  width="495" height="286" screenshot="random_variable.png">
 <description>
 <p>
 The Random Variable Config Element is used to generate random numeric strings and store them in variable for use later.
 It's simpler than using <complink name="User Defined Variables"/> together with the <code>__Random()</code> function.
 </p>
 <p>
 The output variable is constructed by using the random number generator,
 and then the resulting number is formatted using the format string.
 The number is calculated using the formula <code>minimum+Random.nextInt(maximum-minimum+1)</code>.
 <code>Random.nextInt()</code> requires a positive integer.
 This means that <code>maximum-minimum</code> - i.e. the range - must be less than <code>2147483647</code>,
 however the <code>minimum</code> and <code>maximum</code> values can be any <code>long</code> values so long as the range is OK.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
   <property name="Variable Name" required="Yes">The name of the variable in which to store the random string.</property>
   <property name="Format String" required="No">The <code>java.text.DecimalFormat</code> format string to be used. 
   For example "<code>000</code>" which will generate numbers with at least 3 digits, 
   or "<code>USER_000</code>" which will generate output of the form <code>USER_nnn</code>. 
   If not specified, the default is to generate the number using <code>Long.toString()</code></property>
   <property name="Minimum Value" required="Yes">The minimum value (<code>long</code>) of the generated random number.</property>
   <property name="Maximum Value" required="Yes">The maximum value (<code>long</code>) of the generated random number.</property>
   <property name="Random Seed" required="No">The seed for the random number generator. Default is the current time in milliseconds. 
   If you use the same seed value with Per Thread set to <code>true</code>, you will get the same value for each Thread as per 
   <a href="http://docs.oracle.com/javase/8/docs/api/java/util/Random.html" >Random</a> class.
   </property>
   <property name="Per Thread(User)?" required="Yes">If <code>False</code>, the generator is shared between all threads in the thread group.
   If <code>True</code>, then each thread has its own random generator.</property>
 </properties>
 
 </component>
 
 <component name="Counter" index="&sect-num;.4.18"  width="404" height="262" screenshot="counter.png">
 <description><p>Allows the user to create a counter that can be referenced anywhere
 in the Thread Group.  The counter config lets the user configure a starting point, a maximum,
 and the increment.  The counter will loop from the start to the max, and then start over
 with the start, continuing on like that until the test is ended.  </p>
 <p>The counter uses a long to store the value, so the range is from <code>-2^63</code> to <code>2^63-1</code>.</p>
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Start" required="Yes">The starting number for the counter.  The counter will equal this
         number during the first iteration.</property>
         <property name="Increment" required="Yes">How much to increment the counter by after each
         iteration.</property>
         <property name="Maximum" required="No">If the counter exceeds the maximum, then it is reset to the <code>Start</code> value.
         Default is <code>Long.MAX_VALUE</code>
         </property>
         <property name="Format" required="No">Optional format, e.g. <code>000</code> will format as <code>001</code>, <code>002</code>, etc. 
         This is passed to <code>DecimalFormat</code>, so any valid formats can be used.
         If there is a problem interpreting the format, then it is ignored.
     [The default format is generated using <code>Long.toString()</code>]
         </property>
         <property name="Reference Name" required="Yes">This controls how you refer to this value in other elements.  Syntax is
         as in <a href="functions.html">user-defined values</a>: <code>$(reference_name}</code>.</property>
         <property name="Track Counter Independently for each User" required="No">In other words, is this a global counter, or does each user get their
         own counter?  If unchecked, the counter is global (i.e., user #1 will get value "<code>1</code>", and user #2 will get value "<code>2</code>" on
         the first iteration).  If checked, each user has an independent counter.</property>
         <property name="Reset counter on each Thread Group Iteration" required="No">This option is only available when counter is tracked per User, if checked, 
         counter will be reset to <code>Start</code> value on each Thread Group iteration. This can be useful when Counter is inside a Loop Controller.</property>
 </properties>
 </component>
 
 <component name="Simple Config Element" index="&sect-num;.4.19"  width="627" height="282" screenshot="simple_config_element.png">
 <description><p>The Simple Config Element lets you add or override arbitrary values in samplers.  You can choose the name of the value
 and the value itself.  Although some adventurous users might find a use for this element, it's here primarily for developers as a basic
 GUI that they can use while developing new JMeter components.</p>
 </description>
 
 <properties>
         <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree. </property>
   <property name="Parameter Name" required="Yes">The name of each parameter.  These values are internal to JMeter's workings and
   are not generally documented.  Only those familiar with the code will know these values.</property>
   <property name="Parameter Value" required="Yes">The value to apply to that parameter.</property>
 </properties>
 
 </component>
 
 
 <component name="MongoDB Source Config (DEPRECATED)" index="&sect-num;.4.20" 
                  width="1233" height="618" screenshot="mongodb-source-config.png">
     <description>Creates a MongoDB connection (used by <complink name="MongoDB Script"/>Sampler)
      from the supplied Connection settings. Each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      <p>
      You can then access <code>com.mongodb.DB</code> object in Beanshell or JSR223 Test Elements through the element <a href="../api/org/apache/jmeter/protocol/mongodb/config/MongoDBHolder.html">MongoDBHolder</a> 
      using this code</p>
      
     <source>
 import com.mongodb.DB;
 import org.apache.jmeter.protocol.mongodb.config.MongoDBHolder;
 DB db = MongoDBHolder.getDBFromSource("value of property MongoDB Source",
             "value of property Database Name");
 &hellip;
     </source>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Server Address List" required="Yes">Mongo DB Servers</property>
         <property name="MongoDB Source" required="Yes">The name of the variable the connection is tied to.  
         <note>Each name must be different. If there are two configuration elements using the same name, only one will be saved.</note>
         </property>
         
         <property name="Keep Trying" required="No">
             If <code>true</code>, the driver will keep trying to connect to the same server in case that the socket cannot be established.<br/>
             There is maximum amount of time to keep retrying, which is 15s by default.<br/>This can be useful to avoid some exceptions being thrown when a server is down temporarily by blocking the operations.
             <br/>It can also be useful to smooth the transition to a new master (so that a new master is elected within the retry time).<br/>
             <note>Note that when using this flag
               <ul>
                 <li>for a replica set, the driver will try to connect to the old master for that time, instead of failing over to the new one right away </li>
                 <li>this does not prevent exception from being thrown in read/write operations on the socket, which must be handled by application.</li>
               </ul>
               Even if this flag is false, the driver already has mechanisms to automatically recreate broken connections and retry the read operations.
             </note>
             Default is <code>false</code>.
         </property>
         <property name="Maximum connections per host" required="No"></property>
         <property name="Connection timeout" required="No">
             The connection timeout in milliseconds.<br/>It is used solely when establishing a new connection <code>Socket.connect(java.net.SocketAddress, int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Maximum retry time" required="No">
             The maximum amount of time in milliseconds to spend retrying to open connection to the same server.<br/>Default is <code>0</code>, which means to use the default 15s if <code>autoConnectRetry</code> is on.
         </property>
         <property name="Maximum wait time" required="No">
             The maximum wait time in milliseconds that a thread may wait for a connection to become available.<br/>Default is <code>120,000</code>.
         </property>
         <property name="Socket timeout" required="No">
             The socket timeout in milliseconds It is used for I/O socket read and write operations <code>Socket.setSoTimeout(int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Socket keep alive" required="No">
             This flag controls the socket keep alive feature that keeps a connection alive through firewalls <code>Socket.setKeepAlive(boolean)</code><br/>
             Default is <code>false</code>.
         </property>
         <property name="ThreadsAllowedToBlockForConnectionMultiplier" required="No">
         This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool.<br/>
         All further threads will get an exception right away.<br/>
         For example if <code>connectionsPerHost</code> is <code>10</code> and <code>threadsAllowedToBlockForConnectionMultiplier</code> is <code>5</code>, then up to 50 threads can wait for a connection.<br/>
         Default is <code>5</code>.
         </property>
         <property name="Write Concern : Safe" required="No">
             If <code>true</code> the driver will use a <code>WriteConcern</code> of <code>WriteConcern.SAFE</code> for all operations.<br/>
             If <code>w</code>, <code>wtimeout</code>, <code>fsync</code> or <code>j</code> are specified, this setting is ignored.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Fsync" required="No">
             The <code>fsync</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for Journal" required="No">
             The <code>j</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for servers" required="No">
             The <code>w</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Wait timeout" required="No">
             The <code>wtimeout</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Continue on error" required="No">
             If batch inserts should continue after the first error
         </property>
     </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.5 Assertions" anchor="assertions">
 <description>
     <p>
     Assertions are used to perform additional checks on samplers, and are processed after <b>every sampler</b>
     in the same scope.
     To ensure that an Assertion is applied only to a particular sampler, add it as a child of the sampler.
     </p>
     <note>
     Note: Unless documented otherwise, Assertions are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of JSR223 and BeanShell Assertions, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </note>
     <p>
     Assertions can be applied to either the main sample, the sub-samples or both. 
     The default is to apply the assertion to the main sample only.
     If the Assertion supports this option, then there will be an entry on the GUI which looks like the following:
     </p>
     <figure width="658" height="54" image="assertion/assertionscope.png">Assertion Scope</figure>
     or the following
     <figure width="841" height="55" image="assertion/assertionscopevar.png">Assertion Scope</figure>
     <p>
     If a sub-sampler fails and the main sample is successful,
     then the main sample will be set to failed status and an Assertion Result will be added.
     If the JMeter variable option is used, it is assumed to relate to the main sample, and
     any failure will be applied to the main sample only.
     </p>
     <note>
     The variable <code>JMeterThread.last_sample_ok</code> is updated to
     "<code>true</code>" or "<code>false</code>" after all assertions for a sampler have been run.
      </note>
 </description>
 <component name="Response Assertion" index="&sect-num;.5.1" anchor="basic_assertion"  width="921" height="423" screenshot="assertion/assertion.png">
 
 <description><p>The response assertion control panel lets you add pattern strings to be compared against various
-    fields of the response.
+    fields of the request or response.
     The pattern strings are:
     </p>
     <ul>
     <li><code>Contains</code>, <code>Matches</code>: Perl5-style regular expressions</li>
     <li><code>Equals</code>, <code>Substring</code>: plain text, case-sensitive</li>
     </ul>
     <p>
     A summary of the pattern matching characters can be found at <a href="http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html">ORO Perl5 regular expressions.</a>
     </p>
     <p>You can also choose whether the strings will be expected
 to <b>match</b> the entire response, or if the response is only expected to <b>contain</b> the
 pattern. You can attach multiple assertions to any controller for additional flexibility.</p>
 <p>Note that the pattern string should not include the enclosing delimiters, 
     i.e. use <code>Price: \d+</code> not <code>/Price: \d+/</code>.
     </p>
     <p>
     By default, the pattern is in multi-line mode, which means that the "<code>.</code>" meta-character does not match newline.
     In multi-line mode, "<code>^</code>" and "<code>$</code>" match the start or end of any line anywhere within the string 
     - not just the start and end of the entire string. Note that <code>\s</code> does match new-line.
     Case is also significant. To override these settings, one can use the <i>extended regular expression</i> syntax.
     For example:
 </p>
 <dl>
 <dt><code>(?i)</code></dt><dd>ignore case</dd>
 <dt><code>(?s)</code></dt><dd>treat target as single line, i.e. "<code>.</code>" matches new-line</dd>
 <dt><code>(?is)</code></dt><dd>both the above</dd>
 </dl>
 These can be used anywhere within the expression and remain in effect until overridden. E.g.
 <dl>
 <dt><code>(?i)apple(?-i) Pie</code></dt><dd>matches "<code>ApPLe Pie</code>", but not "<code>ApPLe pIe</code>"</dd>
 <dt><code>(?s)Apple.+?Pie</code></dt><dd>matches <code>Apple</code> followed by <code>Pie</code>, which may be on a subsequent line.</dd>
 <dt><code>Apple(?s).+?Pie</code></dt><dd>same as above, but it's probably clearer to use the <code>(?s)</code> at the start.</dd>
 </dl>
 
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
-        <property name="Response Field to Test" required="Yes">Instructs JMeter which field of the Response to test.
+        <property name="Field to Test" required="Yes">Instructs JMeter which field of the Request or Response to test.
         <ul>
         <li><code>Text Response</code> - the response text from the server, i.e. the body, excluding any HTTP headers.</li>
-        <li><code>Document (text)</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).</li>
-        <li><code>URL sampled</code></li>
         <li><code>Response Code</code> - e.g. <code>200</code></li>
         <li><code>Response Message</code> - e.g. <code>OK</code></li>
         <li><code>Response Headers</code>, including Set-Cookie headers (if any)</li>
+        <li><code>Request Headers</code></li>
+        <li><code>URL sampled</code></li>
+        <li><code>Document (text)</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).</li>
         </ul>
-                </property>
+        </property>
         <property name="Ignore status" required="Yes">Instructs JMeter to set the status to success initially. 
                 <p>
                 The overall success of the sample is determined by combining the result of the
                 assertion with the existing Response status.
                 When the <code>Ignore Status</code> checkbox is selected, the Response status is forced
                 to successful before evaluating the Assertion.
                 </p>
                 HTTP Responses with statuses in the <code>4xx</code> and <code>5xx</code> ranges are normally
                 regarded as unsuccessful. 
                 The "<code>Ignore status</code>" checkbox can be used to set the status successful before performing further checks.
                 Note that this will have the effect of clearing any previous assertion failures,
                 so make sure that this is only set on the first assertion.
         </property>
         <property name="Pattern Matching Rules" required="Yes">Indicates how the text being tested
         is checked against the pattern.
         <ul>
         <li><code>Contains</code> - true if the text contains the regular expression pattern</li>
         <li><code>Matches</code> - true if the whole text matches the regular expression pattern</li>
         <li><code>Equals</code> - true if the whole text equals the pattern string (case-sensitive)</li>
         <li><code>Substring</code> - true if the text contains the pattern string (case-sensitive)</li>
         </ul>
         <code>Equals</code> and <code>Substring</code> patterns are plain strings, not regular expressions.
-        <code>NOT</code> may also be selected to invert the result of the check.</property>
+        <code>NOT</code> may also be selected to invert the result of the check.
+        <code>OR</code> Apply each assertion in OR combination (if 1 pattern to test maches, Assertion will be ok) instead of AND (All patterns must match so that Assertion is OK).</property>
         <property name="Patterns to Test" required="Yes">A list of patterns to
         be tested.  
         Each pattern is tested separately. 
         If a pattern fails, then further patterns are not checked.
         There is no difference between setting up
         one Assertion with multiple patterns and setting up multiple Assertions with one
         pattern each (assuming the other options are the same).
         <note>However, when the <code>Ignore Status</code> checkbox is selected, this has the effect of cancelling any
         previous assertion failures - so make sure that the <code>Ignore Status</code> checkbox is only used on
         the first Assertion.</note>
         </property>
 </properties>
 <p>
     The pattern is a Perl5-style regular expression, but without the enclosing brackets.
 </p>
 <example title="Assertion Examples" anchor="assertion_examples">
 <center>
 <figure image="assertion/example1a.png" width="266" height="117">Figure 14 - Test Plan</figure>
 <figure image="assertion/example1b.png" width="920" height="451">Figure 15 - Assertion Control Panel with Pattern</figure>
 <figure image="assertion/example1c-pass.png" width="801" height="230">Figure 16 - Assertion Listener Results (Pass)</figure>
 <figure image="assertion/example1c-fail.png" width="800" height="233">Figure 17 - Assertion Listener Results (Fail)</figure>
 </center>
 </example>
 
 
 </component>
 
 <component name="Duration Assertion" index="&sect-num;.5.2"  width="606" height="187" screenshot="duration_assertion.png">
 <description><p>The Duration Assertion tests that each response was received within a given amount
 of time.  Any response that takes longer than the given number of milliseconds (specified by the
 user) is marked as a failed response.</p></description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Duration in Milliseconds" required="Yes">The maximum number of milliseconds
         each response is allowed before being marked as failed.</property>
 </properties>
 </component>
 
 <component name="Size Assertion" index="&sect-num;.5.3"  width="732" height="358" screenshot="size_assertion.png">
 <description><p>The Size Assertion tests that each response contains the right number of bytes in it.  You can specify that
 the size be equal to, greater than, less than, or not equal to a given number of bytes.</p>
 <note>An empty response is treated as being 0 bytes rather than reported as an error.</note>
 </description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
         <property name="Size in bytes" required="Yes">The number of bytes to use in testing the size of the response (or value of the JMeter variable).</property>
         <property name="Type of Comparison" required="Yes">Whether to test that the response is equal to, greater than, less than,
         or not equal to, the number of bytes specified.</property>
 
 </properties>
 </component>
 
 <component name="XML Assertion" index="&sect-num;.5.4"  width="470" height="85" screenshot="xml_assertion.png">
 <description><p>The XML Assertion tests that the response data consists of a formally correct XML document.  It does not
 validate the XML based on a DTD or schema or do any further validation.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 
 </properties>
 </component>
 
 <component name="BeanShell Assertion" index="&sect-num;.5.5"  width="849" height="633" screenshot="beanshell_assertion.png">
 <description><p>The BeanShell Assertion allows the user to perform assertion checking using a BeanShell script.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p><p>
 Note that a different Interpreter is used for each independent occurrence of the assertion
 in each thread in a test script, but the same Interpreter is used for subsequent invocations.
 This means that variables persist across calls to the assertion.
 </p>
 <p>
 All Assertions are called from the same thread as the sampler.
 </p>
 <p>
 If the property "<code>beanshell.assertion.init</code>" is defined, it is passed to the Interpreter
 as the name of a sourced file. This can be used to define common methods and variables.
 There is a sample init file in the <code>bin</code> directory: <code>BeanShellAssertion.bshrc</code>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run. This overrides the script.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>There's a <a href="../demos/BeanShellAssertion.bsh">sample script</a> you can try.</p>
 <p>
 Before invoking the script, some variables are set up in the BeanShell interpreter.
 These are strings unless otherwise noted:
 </p>
 <ul>
   <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a> Object. (e.g.) <code>log.warn("Message"[,Throwable])</code></li>
   <li><code>SampleResult</code> - the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> Object; read-write</li>
   <li><code>Response</code> - the response Object; read-write</li>
   <li><code>Failure</code> - boolean; read-write; used to set the Assertion status</li>
   <li><code>FailureMessage</code> - String; read-write; used to set the Assertion message</li>
   <li><code>ResponseData</code> - the response body (byte [])</li>
   <li><code>ResponseCode</code> - e.g. <code>200</code></li>
   <li><code>ResponseMessage</code> - e.g. <code>OK</code></li>
   <li><code>ResponseHeaders</code> - contains the HTTP headers</li>
   <li><code>RequestHeaders</code> - contains the HTTP headers sent to the server</li>
   <li><code>SampleLabel</code></li>
   <li><code>SamplerData</code> - data that was sent to the server</li>
   <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
   <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
     <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.putObject("OBJ1",new Object());</source></li>
   <li><code>props</code> - JMeterProperties (class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g.
     <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 </ul>
 <p>The following methods of the Response object may be useful:</p>
 <ul>
     <li><code>setStopThread(boolean)</code></li>
     <li><code>setStopTest(boolean)</code></li>
     <li><code>String getSampleLabel()</code></li>
     <li><code>setSampleLabel(String)</code></li>
 </ul>
 </component>
 
 <component name="MD5Hex Assertion" index="&sect-num;.5.6" width="398" height="130" screenshot="assertion/MD5HexAssertion.png">
 <description><p>The MD5Hex Assertion allows the user to check the MD5 hash of the response data.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="MD5 sum" required="Yes">32 hex digits representing the MD5 hash (case not significant)</property>
 
 </properties>
 </component>
 
 <component name="HTML Assertion" index="&sect-num;.5.7"  width="505" height="341" screenshot="assertion/HTMLAssertion.png">
 <description><p>The HTML Assertion allows the user to check the HTML syntax of the response data using JTidy.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="doctype" required="Yes"><code>omit</code>, <code>auto</code>, <code>strict</code> or <code>loose</code></property>
 <property name="Format" required="Yes"><code>HTML</code>, <code>XHTML</code> or <code>XML</code></property>
 <property name="Errors only" required="Yes">Only take note of errors?</property>
 <property name="Error threshold" required="Yes">Number of errors allowed before classing the response as failed</property>
 <property name="Warning threshold" required="Yes">Number of warnings allowed before classing the response as failed</property>
 <property name="Filename" required="No">Name of file to which report is written</property>
 
 </properties>
 </component>
 <component name="XPath Assertion" index="&sect-num;.5.8"  width="871" height="615" screenshot="xpath_assertion.png">
 <description><p>The XPath Assertion tests a document for well formedness, has the option
 of validating against a DTD, or putting the document through JTidy and testing for an
 XPath.  If that XPath exists, the Assertion is true.  Using "<code>/</code>" will match any well-formed
 document, and is the default XPath Expression. 
 The assertion also supports boolean expressions, such as "<code>count(//*error)=2</code>".
 See <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a> for more information
 on XPath.
 </p>
 Some sample expressions:
 <ul>
 <li><code>//title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> anywhere in the response</li>
 <li><code>/title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> at root level in the response</li>
 </ul>
 </description>
 
 <properties>
 <property name="Name"        required="No">Descriptive name for this element that is shown in the tree.</property>
 <property name="Use Tidy (tolerant parser)"    required="Yes">Use Tidy, i.e. be tolerant of XML/HTML errors</property>
 <property name="Quiet"    required="If Tidy is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"    required="If Tidy is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"    required="If Tidy is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces"    required="If Tidy is not selected">Should namespaces be honoured? (see note below on NAMESPACES)</property>
 <property name="Validate XML"    required="If Tidy is not selected">Check the document against its schema.</property>
 <property name="Ignore Whitespace"  required="If Tidy is not selected">Ignore Element Whitespace.</property>
 <property name="Fetch External DTDs"  required="If Tidy is not selected">If selected, external DTDs are fetched.</property>
 <property name="XPath Assertion"    required="Yes">XPath to match in the document.</property>
 <property name="True if nothing matches"    required="No">True if a XPath expression is not matched</property>
 </properties>
 <note>
 The non-tolerant parser can be quite slow, as it may need to download the DTD etc.
 </note>
 <note>
 <b>NAMESPACES</b>
 As a work-round for namespace limitations of the Xalan XPath parser implementation on which JMeter is based,
 you can provide a Properties file which contains mappings for the namespace prefixes: 
 <source>
 prefix1=Full Namespace 1
 prefix2=Full Namespace 2
 &hellip;
 </source>
 
 You reference this file in <code>jmeter.properties</code> file using the property:
     <source>xpath.namespace.config</source>
 </note>
 </component>
 <component name="XML Schema Assertion" index="&sect-num;.5.9"  width="472" height="132" screenshot="assertion/XMLSchemaAssertion.png">
 <description><p>The XML Schema Assertion allows the user to validate a response against an XML Schema.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="File Name" required="Yes">Specify XML Schema File Name</property>
 </properties>
 </component>
 
 <component name="BSF Assertion (DEPRECATED)" index="&sect-num;.5.10"  width="847" height="634" screenshot="bsf_assertion.png">
 <description>
 <p>
 The BSF Assertion allows BSF script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Assertion" index="&sect-num;.5.11">
 <description>
 <p>
 The JSR223 Assertion allows JSR223 script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code><a href="https://docs.oracle.com/javase/8/docs/api/javax/script/Compilable.html">Compilable</a></code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Compare Assertion" index="&sect-num;.5.12"  width="580" height="302" screenshot="assertion/compare.png">
 <description>
 <note>
 Compare Assertion <b>must not be used</b> during load test as it consumes a lot of resources (memory and CPU). Use it only for either functional testing or 
 during Test Plan debugging and Validation.
 </note>
 
 The Compare Assertion can be used to compare sample results within its scope.
 Either the contents or the elapsed time can be compared, and the contents can be filtered before comparison.
 The assertion comparisons can be seen in the <complink name="Comparison Assertion Visualizer"/>.
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Compare Content" required="Yes">Whether or not to compare the content (response data)</property>
     <property name="Compare Time" required="Yes">If the value is &ge;0, then check if the response time difference is no greater than the value.
     I.e. if the value is <code>0</code>, then the response times must be exactly equal.</property>
     <property name="Comparison Filters" required="No">Filters can be used to remove strings from the content comparison.
     For example, if the page has a time-stamp, it might be matched with: "<code>Time: \d\d:\d\d:\d\d</code>" and replaced with a dummy fixed time "<code>Time: HH:MM:SS</code>".
     </property>
 </properties>
 </component>
 
 <component name="SMIME Assertion" index="&sect-num;.5.13"  width="471" height="428" screenshot="assertion/smime.png">
 <description>
 The SMIME Assertion can be used to evaluate the sample results from the Mail Reader Sampler.
 This assertion verifies if the body of a mime message is signed or not. The signature can also be verified against a specific signer certificate.
 As this is a functionality that is not necessarily needed by most users, additional jars need to be downloaded and added to <code>JMETER_HOME/lib</code>:<br></br> 
 <ul>
 <li><code>bcmail-xxx.jar</code> (BouncyCastle SMIME/CMS)</li>
 <li><code>bcprov-xxx.jar</code> (BouncyCastle Provider)</li>
 </ul>
 These need to be <a href="http://www.bouncycastle.org/latest_releases.html">downloaded from BouncyCastle.</a>
 <p>
 If using the <complink name="Mail Reader Sampler">Mail Reader Sampler</complink>, 
 please ensure that you select "<code>Store the message using MIME (raw)</code>" otherwise the Assertion won't be able to process the message correctly.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Verify Signature" required="Yes">If selected, the assertion will verify if it is a valid signature according to the parameters defined in the <code>Signer Certificate</code> box.</property>
     <property name="Message not signed" required="Yes">Whether or not to expect a signature in the message</property>
     <property name="Signer Cerificate" required="Yes">"<code>No Check</code>" means that it will not perform signature verification. "<code>Check values</code>" is used to verify the signature against the inputs provided. And "<code>Certificate file</code>" will perform the verification against a specific certificate file.</property>
     <property name="Message Position" required="Yes">
     The Mail sampler can retrieve multiple messages in a single sample.
     Use this field to specify which message will be checked.
     Messages are numbered from <code>0</code>, so <code>0</code> means the first message.
     Negative numbers count from the LAST message; <code>-1</code> means LAST, <code>-2</code> means penultimate etc.
     </property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.6 Timers" anchor="timers">
 <description>
     <note>
     Since version 3.1, a new feature (in Beta mode as of JMeter 3.1 and subject to changes) has been implemented which provides the following feature.<br/>
     You can apply a multiplication factor on the sleep delays computed by Random timer by setting property <code>timer.factor=float number</code> where float number is a decimal positive number.<br/>
     JMeter will multiply this factor by the computed sleep delay. This feature can be used by:
     <ul> 
         <li><complink name="Gaussian Random Timer"/></li>
         <li><complink name="Poisson Random Timer"/></li>
         <li><complink name="Uniform Random Timer"/></li>
     </ul> 
     </note>
     <note>
     Note that timers are processed <b>before</b> each sampler in the scope in which they are found;
     if there are several timers in the same scope, <b>all</b> the timers will be processed <b>before
     each</b> sampler.
     <br></br>
     Timers are only processed in conjunction with a sampler.
     A timer which is not in the same scope as a sampler will not be processed at all.
     <br></br>
     To apply a timer to a single sampler, add the timer as a child element of the sampler.
     The timer will be applied before the sampler is executed.
     To apply a timer after a sampler, either add it to the next sampler, or add it as the
     child of a <complink name="Test Action"/> Sampler.
     </note>
 </description>
 <component name="Constant Timer" index="&sect-num;.6.1" anchor="constant" width="372" height="100" screenshot="timers/constant_timer.png">
 <description>
 <p>If you want to have each thread pause for the same amount of time between
 requests, use this timer.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Thread Delay" required="Yes">Number of milliseconds to pause.</property>
 </properties>
 </component>
 
 <component name="Gaussian Random Timer" index="&sect-num;.6.2" width="372" height="156" screenshot="timers/gauss_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals occurring near a particular value.  
 The total delay is the sum of the Gaussian distributed value (with mean <code>0.0</code> and standard deviation <code>1.0</code>) times
 the deviation value you specify, and the offset value.
 Another way to explain it, in Gaussian Random Timer, the variation around constant offset has a gaussian curve distribution. 
 
 </p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Deviation" required="Yes">Deviation in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Uniform Random Timer" index="&sect-num;.6.3" width="372" height="157" screenshot="timers/uniform_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with
 each time interval having the same probability of occurring. The total delay
 is the sum of the random value and the offset value.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Random Delay Maximum" required="Yes">Maximum random number of milliseconds to
 pause.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Constant Throughput Timer" index="&sect-num;.6.4" width="636" height="146" screenshot="timers/constant_throughput_timer.png">
 
 <description><p>This timer introduces variable pauses, calculated to keep the total throughput (in terms of samples per minute) as close as possible to a give figure. Of course the throughput will be lower if the server is not capable of handling it, or if other timers or time-consuming test elements prevent it.</p>
 <p>
 N.B. although the Timer is called the Constant Throughput timer, the throughput value does not need to be constant.
 It can be defined in terms of a variable or function call, and the value can be changed during a test.
 The value can be changed in various ways:
 </p>
 <ul>
 <li>using a counter variable</li>
 <li>using a JavaScript or BeanShell function to provide a changing value</li>
 <li>using the remote BeanShell server to change a JMeter property</li>
 </ul>
 <p>See <a href="best-practices.html">Best Practices</a> for further details.
 <note>
 Note that the throughput value should not be changed too often during a test
 - it will take a while for the new value to take effect.
 </note>
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Target Throughput" required="Yes">Throughput we want the timer to try to generate.</property>
   <property name="Calculate Throughput based on" required="Yes">
    <ul>
     <li><code>this thread only</code> - each thread will try to maintain the target throughput. The overall throughput will be proportional to the number of active threads.</li>
     <li><code>all active threads in current thread group</code> - the target throughput is divided amongst all the active threads in the group. 
     Each thread will delay as needed, based on when it last ran.</li>
     <li><code>all active threads</code> - the target throughput is divided amongst all the active threads in all Thread Groups.
     Each thread will delay as needed, based on when it last ran.
     In this case, each other Thread Group will need a Constant Throughput timer with the same settings.</li>
     <li><code>all active threads in current thread group (shared)</code> - as above, but each thread is delayed based on when any thread in the group last ran.</li>
     <li><code>all active threads (shared)</code> - as above; each thread is delayed based on when any thread last ran.</li>
    </ul>
   </property>
   <p>The shared and non-shared algorithms both aim to generate the desired throughput, and will produce similar results.<br/>
   The shared algorithm should generate a more accurate overall transaction rate.<br/>
   The non-shared algorithm should generate a more even spread of transactions across threads.</p>
 </properties>
 </component>
 
 <component name="Synchronizing Timer" index="&sect-num;.6.5" width="410" height="145" screenshot="timers/sync_timer.png">
 
 <description>
 <p>
 The purpose of the SyncTimer is to block threads until X number of threads have been blocked, and
 then they are all released at once.  A SyncTimer can thus create large instant loads at various
 points of the test plan.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Number of Simultaneous Users to Group by" required="Yes">Number of threads to release at once. Setting it to <code>0</code> is equivalent to setting it to Number of threads in Thread Group.</property>
   <property name="Timeout in milliseconds" required="No">If set to <code>0</code>, Timer will wait for the number of threads to reach the value in "<code>Number of Simultaneous Users to Group</code>". If superior to <code>0</code>, then timer will wait at max "<code>Timeout in milliseconds</code>" for the number of Threads. If after the timeout interval the number of users waiting is not reached, timer will stop waiting. Defaults to <code>0</code></property>
 </properties>
 <note>
 If timeout in milliseconds is set to <code>0</code> and number of threads never reaches "<code>Number of Simultaneous Users to Group by</code>" then Test will pause infinitely.
 Only a forced stop will stop it. Setting Timeout in milliseconds is an option to consider in this case.
 </note>
 <note>
 Synchronizing timer blocks only within one JVM, so if using Distributed testing ensure you never set "<code>Number of Simultaneous Users to Group by</code>" to a value superior to the number of users
 of its containing Thread group considering 1 injector only.
 </note>
 
 </component>
 
 <component name="BeanShell Timer" index="&sect-num;.6.6"  width="846" height="636" screenshot="timers/beanshell_timer.png">
 <description>
 <p>
 The BeanShell Timer can be used to generate a delay.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code>
      The return value is used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The BeanShell script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 </source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous <code>SampleResult</code> (if any)</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.timer.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 
 <component name="BSF Timer (DEPRECATED)" index="&sect-num;.6.7"  width="844" height="636" screenshot="timers/bsf_timer.png">
 <description>
 <p>
 The BSF Timer can be used to generate a delay using a BSF scripting language.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="ScriptLanguage" required="Yes">
         The scripting language to be used.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property
      The return value is converted to a long integer and used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the script interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)  - the current Sampler</li>
 <li><code>Label</code> - the name of the Timer</li>
 <li><code>FileName</code> - the file name (if any)</li>
 <li><code>OUT</code> - System.out</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Timer" index="&sect-num;.6.8">
 <description>
 <p>
 The JSR223 Timer can be used to generate a delay using a JSR223 scripting language,
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="ScriptLanguage" required="Yes">
         The scripting language to be used.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property
      The return value is converted to a long integer and used as the number of milliseconds to wait.
      </property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code><a href="https://docs.oracle.com/javase/8/docs/api/javax/script/Compilable.html">Compilable</a></code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the script interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - the current Sampler</li>
 <li><code>Label</code> - the name of the Timer</li>
 <li><code>FileName</code> - the file name (if any)</li>
 <li><code>OUT</code> - System.out</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Poisson Random Timer" index="&sect-num;.6.9" width="341" height="182" screenshot="timers/poisson_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals occurring near a particular value.  The total delay is the
 sum of the Poisson distributed value, and the offset value.</p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Lambda" required="Yes">Lambda value in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.7 Pre Processors" anchor="preprocessors">
     <description>
     <br></br>
         Preprocessors are used to modify the Samplers in their scope.
     <br></br>
     </description>
 <component name="HTML Link Parser" index="&sect-num;.7.1" width="373" height="79" screenshot="html_link_parser.png" anchor="html_link_parser">
 <description>
 <p>This modifier parses HTML response from the server and extracts
 links and forms.  A URL test sample that passes through this modifier will be examined to
 see if it &quot;matches&quot; any of the links or forms extracted
 from the immediately previous response.  It would then replace the values in the URL
 test sample with appropriate values from the matching link or form.  Perl-type regular
 expressions are used to find matches.</p>
 </description>
 <note>
 Matches are performed using <code>protocol</code>, <code>host</code>, <code>path</code> and <code>parameter names</code>.
 The target sampler cannot contain parameters that are not in the response links.
 </note>
 <note>
 If using distributed testing, ensure you switch mode (see <code>jmeter.properties</code>) so that it's not a stripping one, see <bugzilla>56376</bugzilla>
 </note>
 
 <example title="Spidering Example" anchor="spider_example">
 <p>Consider a simple example: let's say you wanted JMeter to &quot;spider&quot; through your site,
 hitting link after link parsed from the HTML returned from your server (this is not
 actually the most useful thing to do, but it serves as a good example).  You would create
 a <complink name="Simple Controller"/>, and add the &quot;HTML Link Parser&quot; to it.  Then, create an
 HTTP Request, and set the domain to &quot;<code>.*</code>&quot;, and the path likewise. This will
 cause your test sample to match with any link found on the returned pages.  If you wanted to
 restrict the spidering to a particular domain, then change the domain value
 to the one you want.  Then, only links to that domain will be followed.
 </p>
 </example>
 
 <example title="Poll Example" anchor="poll_example">
 <p>A more useful example: given a web polling application, you might have a page with
 several poll options as radio buttons for the user to select.  Let's say the values
 of the poll options are very dynamic - maybe user generated.  If you wanted JMeter to
 test the poll, you could either create test samples with hardcoded values chosen, or you
 could let the HTML Link Parser parse the form, and insert a random poll option into
 your URL test sample.  To do this, follow the above example, except, when configuring
 your Web Test controller's URL options, be sure to choose &quot;<code>POST</code>&quot; as the
 method.  Put in hard-coded values for the <code>domain</code>, <code>path</code>, and any additional form parameters.
 Then, for the actual radio button parameter, put in the name (let's say it's called &quot;<code>poll_choice</code>&quot;),
 and then &quot;<code>.*</code>&quot; for the value of that parameter.  When the modifier examines
 this URL test sample, it will find that it &quot;matches&quot; the poll form (and
 it shouldn't match any other form, given that you've specified all the other aspects of
 the URL test sample), and it will replace your form parameters with the matching
 parameters from the form.  Since the regular expression &quot;<code>.*</code>&quot; will match with
 anything, the modifier will probably have a list of radio buttons to choose from.  It
 will choose at random, and replace the value in your URL test sample.  Each time through
 the test, a new random value will be chosen.</p>
 
 <figure width="1250" height="493" image="modification.png">Figure 18 - Online Poll Example</figure>
 
 <note>One important thing to remember is that you must create a test sample immediately
 prior that will return an HTML page with the links and forms that are relevant to
 your dynamic test sample.</note>
 </example>
 
 </component>
 
 <component name="HTTP URL Re-writing Modifier" index="&sect-num;.7.2"  width="579" height="239" screenshot="url_rewriter.png">
 <description><p>This modifier works similarly to the HTML Link Parser, except it has a specific purpose for which
 it is easier to use than the HTML Link Parser, and more efficient.  For web applications that
 use URL Re-writing to store session ids instead of cookies, this element can be attached at the
 ThreadGroup level, much like the <complink name="HTTP Cookie Manager"/>.  Simply give it the name
 of the session id parameter, and it will find it on the page and add the argument to every
 request of that ThreadGroup.</p>
 <p>Alternatively, this modifier can be attached to select requests and it will modify only them.
 Clever users will even determine that this modifier can be used to grab values that elude the
 <complink name="HTML Link Parser"/>.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name given to this element in the test tree.</property>
         <property name="Session Argument Name" required="Yes">The name of the parameter to grab from
         previous response.  This modifier will find the parameter anywhere it exists on the page, and
         grab the value assigned to it, whether it's in an HREF or a form.</property>
         <property name="Path Extension" required="No">Some web apps rewrite URLs by appending
         a semi-colon plus the session id parameter.  Check this box if that is so.</property>
         <property name="Do not use equals in path extension" required="No">Some web apps rewrite URLs without using an &quot;<code>=</code>&quot; sign between the parameter name and value (such as Intershop Enfinity).</property>
         <property name="Do not use questionmark in path extension" required="No">Prevents the query string to end up in the path extension (such as Intershop Enfinity).</property>
         <property name="Cache Session Id?" required="Yes">
         Should the value of the session Id be saved for later use when the session Id is not present?
         </property>
         <property name="URL Encode" required="No">
         URL Encode value when writing parameter
         </property>
 </properties>
 
 <note>
 If using distributed testing, ensure you switch mode (see <code>jmeter.properties</code>) so that it's not a stripping one, see <bugzilla>56376</bugzilla>.
 </note>
 
 </component>
 
 <component name="User Parameters" index="&sect-num;.7.5"  width="703" height="303" screenshot="user_params.png">
 <description><p>Allows the user to specify values for User Variables specific to individual threads.</p>
 <p>User Variables can also be specified in the Test Plan but not specific to individual threads. This panel allows
 you to specify a series of values for any User Variable. For each thread, the variable will be assigned one of the values from the series
 in sequence. If there are more threads than values, the values get re-used. For example, this can be used to assign a distinct
 user id to be used by each thread. User variables can be referenced in any field of any jMeter Component.</p>
 
 <p>The variable is specified by clicking the <code>Add Variable</code> button in the bottom of the panel and filling in the Variable name in the '<code>Name:</code>' column.
 To add a new value to the series, click the '<code>Add User</code>' button and fill in the desired value in the newly added column.</p>
 
 <p>Values can be accessed in any test component in the same thread group, using the <a href="functions.html">function syntax</a>: <code>${variable}</code>.</p>
 <p>See also the <complink name="CSV Data Set Config"/> element, which is more suitable for large numbers of parameters</p>
 </description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Update Once Per Iteration" required="Yes">A flag to indicate whether the User Parameters element
         should update its variables only once per iteration.  if you embed functions into the UP, then you may need greater
         control over how often the values of the variables are updated.  Keep this box checked to ensure the values are
         updated each time through the UP's parent controller.  Uncheck the box, and the UP will update the parameters for 
         every sample request made within its <a href="test_plan.html#scoping_rules">scope</a>.</property>
         
 </properties>
 </component>
 
 <component name="BeanShell PreProcessor" index="&sect-num;.7.7"  width="845" height="633" screenshot="beanshell_preprocessor.png">
 <description>
 <p>
 The BeanShell PreProcessor allows arbitrary code to be applied before taking a sample.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script. The return value is ignored.</property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.preprocessor.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 <component name="BSF PreProcessor (DEPRECATED)" index="&sect-num;.7.8"  width="844" height="632" screenshot="bsf_preprocessor.png">
 <description>
 <p>
 The BSF PreProcessor allows BSF script code to be applied before taking a sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>The following BSF variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>FileName</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 PreProcessor" index="&sect-num;.7.8">
 <description>
 <p>
 The JSR223 PreProcessor allows JSR223 script code to be applied before taking a sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code><a href="https://docs.oracle.com/javase/8/docs/api/javax/script/Compilable.html">Compilable</a></code> interface (Groovy is one of these, java, beanshell and javascript are not)
     <note>See note in JSR223 Sampler Java System property if you're using Groovy without checking this option</note>
     </property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>The following JSR223 variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>FileName</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JDBC PreProcessor" index="&sect-num;.7.9">
 <description>
 <p>
 The JDBC PreProcessor enables you to run some SQL statement just before a sample runs.
 This can be useful if your JDBC Sample requires some data to be in DataBase and you cannot compute this in a setup Thread group.
 For details, see <complink name="JDBC Request"/>.
 </p>
 <p>
 See the following Test plan:
 </p>
 <links>
         <link href="../demos/JDBC-Pre-Post-Processor.jmx">Test Plan using JDBC Pre/Post Processor</link>
 </links>
 <p>
 In the linked test plan, "<code>Create Price Cut-Off</code>" JDBC PreProcessor calls a stored procedure to create a Price Cut-Off in Database,
 this one will be used by "<code>Calculate Price cut off</code>".
 </p>
 <figure width="818" height="394" image="jdbc-pre-processor.png">Create Price Cut-Off Preprocessor</figure>
 
 </description>
 </component>
 
 <component name="RegEx User Parameters" index="&sect-num;.7.10"  width="727" height="138" screenshot="regex_user_params.png">
     <description><p>Allows to specify dynamic values for HTTP parameters extracted from another HTTP Request using regular expressions.
             RegEx User Parameters are specific to individual threads.</p>
         <p>This component allows you to specify reference name of a regular expression that extracts names and values of HTTP request parameters. 
             Regular expression group numbers must be specified for parameter's name and also for parameter's value.
             Replacement will only occur for parameters in the Sampler that uses this RegEx User Parameters which name matches </p>
     </description>
     
     <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Regular Expression Reference Name" required="Yes">Name of a reference to a regular expression</property>
         <property name="Parameter names regexp group number" required="Yes">Group number of regular expression used to extract parameter names</property>
         <property name="Parameter values regex group number" required="Yes">Group number of regular expression used to extract parameter values</property>
     </properties>
     
     <example title="Regexp Example" anchor="regex_user_param_example">
     <p>Suppose we have a request which returns a form with 3 input parameters and we want to extract the value of 2 of them to inject them in next request</p>
     <ol>
       <li>Create Post Processor Regular Expression for first HTTP Request
         <ul>
           <li><code>refName</code> - set name of a regular expression Expression (<code>listParams</code>)</li>
           <li><code>regular expression</code> - expression that will extract input names and input values attributes
             <br/>
             Ex: <code>input name="([^"]+?)" value="([^"]+?)"</code></li>
           <li><code>template</code> - would be empty</li>
           <li><code>match nr</code> - <code>-1</code> (in order to iterate through all the possible matches)</li>
         </ul>
       </li>
       <li>Create Pre Processor RegEx User Parameters for second HTTP Request
         <ul>
           <li><code>refName</code> - set the same reference name of a regular expression, would be <code>listParams</code> in our example</li>
           <li><code>parameter names group number</code> - group number of regular expression for parameter names, would be <code>1</code> in our example</li>
