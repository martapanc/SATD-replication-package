diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index 861b05b92..444ac7b1b 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,548 +1,546 @@
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
-import org.apache.jmeter.testelement.property.PropertyIterator;
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
      * are mutually exclusive
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
             log.debug("Type:" + (contains?"Contains" : "Match") + (notTest? "(not)" : ""));
         }
 
         if (StringUtils.isEmpty(toCheck)) {
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
-            PropertyIterator iter = getTestStrings().iterator();
-            while (iter.hasNext()) {
-                String stringPattern = iter.next().getStringValue();
+            for (JMeterProperty jMeterProperty : getTestStrings()) {
+                String stringPattern = jMeterProperty.getStringValue();
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
diff --git a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
index 56865dc92..626279fb3 100644
--- a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
+++ b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
@@ -1,435 +1,434 @@
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
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.ButtonGroup;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 
 import org.apache.jmeter.assertions.ResponseAssertion;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.gui.util.TextAreaCellRenderer;
 import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
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
         responseHeadersButton.setSelected(false);
         assumeSuccess.setSelected(false);
 
         substringBox.setSelected(true);
         notBox.setSelected(false);
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
 
         if (model.isTestFieldResponseData()) {
             responseStringButton.setSelected(true);
         } else if (model.isTestFieldResponseDataAsDocument()) {
             responseAsDocumentButton.setSelected(true);
         } else if (model.isTestFieldResponseCode()) {
             responseCodeButton.setSelected(true);
         } else if (model.isTestFieldResponseMessage()) {
             responseMessageButton.setSelected(true);
         } else if (model.isTestFieldResponseHeaders()) {
             responseHeadersButton.setSelected(true);
         } else // Assume it is the URL
         {
             urlButton.setSelected(true);
         }
 
         assumeSuccess.setSelected(model.getAssumeSuccess());
 
         tableModel.clearData();
-        PropertyIterator tests = model.getTestStrings().iterator();
-        while (tests.hasNext()) {
-            tableModel.addRow(new Object[] { tests.next().getStringValue() });
+        for (JMeterProperty jMeterProperty : model.getTestStrings()) {
+            tableModel.addRow(new Object[] { jMeterProperty.getStringValue() });
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
     private void init() {
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
         JPanel panel = new JPanel();
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("assertion_resp_field"))); //$NON-NLS-1$
 
         responseStringButton = new JRadioButton(JMeterUtils.getResString("assertion_text_resp")); //$NON-NLS-1$
         responseAsDocumentButton = new JRadioButton(JMeterUtils.getResString("assertion_text_document")); //$NON-NLS-1$
         urlButton = new JRadioButton(JMeterUtils.getResString("assertion_url_samp")); //$NON-NLS-1$
         responseCodeButton = new JRadioButton(JMeterUtils.getResString("assertion_code_resp")); //$NON-NLS-1$
         responseMessageButton = new JRadioButton(JMeterUtils.getResString("assertion_message_resp")); //$NON-NLS-1$
         responseHeadersButton = new JRadioButton(JMeterUtils.getResString("assertion_headers")); //$NON-NLS-1$
 
         ButtonGroup group = new ButtonGroup();
         group.add(responseStringButton);
         group.add(responseAsDocumentButton);
         group.add(urlButton);
         group.add(responseCodeButton);
         group.add(responseMessageButton);
         group.add(responseHeadersButton);
 
         panel.add(responseStringButton);
         panel.add(responseAsDocumentButton);
         panel.add(urlButton);
         panel.add(responseCodeButton);
         panel.add(responseMessageButton);
         panel.add(responseHeadersButton);
 
         responseStringButton.setSelected(true);
 
         assumeSuccess = new JCheckBox(JMeterUtils.getResString("assertion_assume_success")); //$NON-NLS-1$
         panel.add(assumeSuccess);
 
         return panel;
     }
 
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
 
         deletePattern = new JButton(JMeterUtils.getResString("delete")); //$NON-NLS-1$
         deletePattern.addActionListener(new ClearPatternsListener());
         deletePattern.setEnabled(false);
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addPattern);
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
             int index = stringTable.getSelectedRow();
             if (index > -1) {
                 stringTable.getCellEditor(index, stringTable.getSelectedColumn()).cancelCellEditing();
                 tableModel.removeRow(index);
                 tableModel.fireTableDataChanged();
             }
             if (stringTable.getModel().getRowCount() == 0) {
                 deletePattern.setEnabled(false);
             }
         }
     }
 
     /**
      * An ActionListener for adding a pattern.
      *
      */
     private class AddPatternListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             tableModel.addNewRow();
             deletePattern.setEnabled(true);
             tableModel.fireTableDataChanged();
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/config/Arguments.java b/src/core/org/apache/jmeter/config/Arguments.java
index a1e0b95e3..cbe826671 100644
--- a/src/core/org/apache/jmeter/config/Arguments.java
+++ b/src/core/org/apache/jmeter/config/Arguments.java
@@ -1,259 +1,261 @@
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
 
 package org.apache.jmeter.config;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.testelement.property.CollectionProperty;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 
 /**
  * A set of Argument objects.
  *
  */
-public class Arguments extends ConfigTestElement implements Serializable {
+public class Arguments extends ConfigTestElement implements Serializable, Iterable<JMeterProperty> {
     private static final long serialVersionUID = 240L;
 
     /** The name of the property used to store the arguments. */
     public static final String ARGUMENTS = "Arguments.arguments"; //$NON-NLS-1$
 
     /**
      * Create a new Arguments object with no arguments.
      */
     public Arguments() {
         setProperty(new CollectionProperty(ARGUMENTS, new ArrayList<Argument>()));
     }
 
     /**
      * Get the arguments.
      *
      * @return the arguments
      */
     public CollectionProperty getArguments() {
         return (CollectionProperty) getProperty(ARGUMENTS);
     }
 
     /**
      * Clear the arguments.
      */
     @Override
     public void clear() {
         super.clear();
         setProperty(new CollectionProperty(ARGUMENTS, new ArrayList<Argument>()));
     }
 
     /**
      * Set the list of arguments. Any existing arguments will be lost.
      *
      * @param arguments
      *            the new arguments
      */
     public void setArguments(List<Argument> arguments) {
         setProperty(new CollectionProperty(ARGUMENTS, arguments));
     }
 
     /**
      * Get the arguments as a Map. Each argument name is used as the key, and
      * its value as the value.
      *
      * @return a new Map with String keys and values containing the arguments
      */
     public Map<String, String> getArgumentsAsMap() {
         PropertyIterator iter = getArguments().iterator();
         Map<String, String> argMap = new LinkedHashMap<>();
         while (iter.hasNext()) {
             Argument arg = (Argument) iter.next().getObjectValue();
             // Because CollectionProperty.mergeIn will not prevent adding two
             // properties of the same name, we need to select the first value so
             // that this element's values prevail over defaults provided by
             // configuration
             // elements:
             if (!argMap.containsKey(arg.getName())) {
                 argMap.put(arg.getName(), arg.getValue());
             }
         }
         return argMap;
     }
 
     /**
      * Add a new argument with the given name and value.
      *
      * @param name
      *            the name of the argument
      * @param value
      *            the value of the argument
      */
     public void addArgument(String name, String value) {
         addArgument(new Argument(name, value, null));
     }
 
     /**
      * Add a new argument.
      *
      * @param arg
      *            the new argument
      */
     public void addArgument(Argument arg) {
         TestElementProperty newArg = new TestElementProperty(arg.getName(), arg);
         if (isRunningVersion()) {
             this.setTemporary(newArg);
         }
         getArguments().addItem(newArg);
     }
 
     /**
      * Add a new argument with the given name, value, and metadata.
      *
      * @param name
      *            the name of the argument
      * @param value
      *            the value of the argument
      * @param metadata
      *            the metadata for the argument
      */
     public void addArgument(String name, String value, String metadata) {
         addArgument(new Argument(name, value, metadata));
     }
 
     /**
      * Get a PropertyIterator of the arguments.
      *
      * @return an iteration of the arguments
      */
+    @Override
     public PropertyIterator iterator() {
         return getArguments().iterator();
     }
 
     /**
      * Create a string representation of the arguments.
      *
      * @return the string representation of the arguments
      */
     @Override
     public String toString() {
         StringBuilder str = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
         while (iter.hasNext()) {
             Argument arg = (Argument) iter.next().getObjectValue();
             final String metaData = arg.getMetaData();
             str.append(arg.getName());
             if (metaData == null) {
                 str.append("="); //$NON-NLS-1$
             } else {
                 str.append(metaData);
             }
             str.append(arg.getValue());
             if (iter.hasNext()) {
                 str.append("&"); //$NON-NLS-1$
             }
         }
         return str.toString();
     }
 
     /**
      * Remove the specified argument from the list.
      *
      * @param row
      *            the index of the argument to remove
      */
     public void removeArgument(int row) {
         if (row < getArguments().size()) {
             getArguments().remove(row);
         }
     }
 
     /**
      * Remove the specified argument from the list.
      *
      * @param arg
      *            the argument to remove
      */
     public void removeArgument(Argument arg) {
         PropertyIterator iter = getArguments().iterator();
         while (iter.hasNext()) {
             Argument item = (Argument) iter.next().getObjectValue();
             if (arg.equals(item)) {
                 iter.remove();
             }
         }
     }
 
     /**
      * Remove the argument with the specified name.
      *
      * @param argName
      *            the name of the argument to remove
      */
     public void removeArgument(String argName) {
         PropertyIterator iter = getArguments().iterator();
         while (iter.hasNext()) {
             Argument arg = (Argument) iter.next().getObjectValue();
             if (arg.getName().equals(argName)) {
                 iter.remove();
             }
         }
     }
 
     /**
      * Remove all arguments from the list.
      */
     public void removeAllArguments() {
         getArguments().clear();
     }
 
     /**
      * Add a new empty argument to the list. The new argument will have the
      * empty string as its name and value, and null metadata.
      */
     public void addEmptyArgument() {
         addArgument(new Argument("", "", null));
     }
 
     /**
      * Get the number of arguments in the list.
      *
      * @return the number of arguments
      */
     public int getArgumentCount() {
         return getArguments().size();
     }
 
     /**
      * Get a single argument.
      *
      * @param row
      *            the index of the argument to return.
      * @return the argument at the specified index, or null if no argument
      *         exists at that index.
      */
     public Argument getArgument(int row) {
         Argument argument = null;
 
         if (row < getArguments().size()) {
             argument = (Argument) getArguments().get(row).getObjectValue();
         }
 
         return argument;
     }
 }
diff --git a/src/core/org/apache/jmeter/engine/util/CompoundVariable.java b/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
index 1a53ab5fa..01c1cb343 100644
--- a/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
+++ b/src/core/org/apache/jmeter/engine/util/CompoundVariable.java
@@ -1,240 +1,241 @@
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
-import java.util.Iterator;
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
     private static final Map<String, Class<? extends Function>> functions = new HashMap<>();
 
     private boolean hasFunction, isDynamic;
 
     private String permanentResults;
 
     private LinkedList<Object> compiledComponents = new LinkedList<>();
 
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
+            
             List<String> classes = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(),
                     new Class[] { Function.class }, true, contain, notContain);
-            Iterator<String> iter = classes.iterator();
-            while (iter.hasNext()) {
-                Function tempFunc = (Function) Class.forName(iter.next()).newInstance();
+            for (String clazzName : classes) {
+                Function tempFunc = (Function) Class.forName(clazzName).newInstance();
                 String referenceKey = tempFunc.getReferenceKey();
                 if (referenceKey.length() > 0) { // ignore self
                     functions.put(referenceKey, tempFunc.getClass());
                     // Add alias for original StringFromFile name (had only one underscore)
                     if (referenceKey.equals("__StringFromFile")){//$NON-NLS-1$
                         functions.put("_StringFromFile", tempFunc.getClass());//$NON-NLS-1$
                     }
                 }
             }
+            
             final int functionCount = functions.size();
-            if (functionCount == 0){
+            if (functionCount == 0) {
                 log.warn("Did not find any functions");
             } else {
                 log.debug("Function count: "+functionCount);
             }
         } catch (Exception err) {
             log.error("", err);
         }
     }
 
     public CompoundVariable() {
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
         if (isDynamic || permanentResults == null) {
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
+        
         StringBuilder results = new StringBuilder();
         for (Object item : compiledComponents) {
             if (item instanceof Function) {
                 try {
                     results.append(((Function) item).execute(previousResult, currentSampler));
                 } catch (InvalidVariableException e) {
                     // TODO should level be more than debug ?
                     if(log.isDebugEnabled()) {
                         log.debug("Invalid variable:"+item, e);
                     }
                 }
             } else if (item instanceof SimpleVariable) {
                 results.append(((SimpleVariable) item).toString());
             } else {
                 results.append(item);
             }
         }
         if (!isDynamic) {
             permanentResults = results.toString();
         }
         return results.toString();
     }
 
     @SuppressWarnings("unchecked") // clone will produce correct type
     public CompoundVariable getFunction() {
         CompoundVariable func = new CompoundVariable();
         func.compiledComponents = (LinkedList<Object>) compiledComponents.clone();
         func.rawParameters = rawParameters;
         func.hasFunction = hasFunction;
         func.isDynamic = isDynamic;
         return func;
     }
 
     /** {@inheritDoc} */
     @Override
     public List<String> getArgumentDesc() {
         return new LinkedList<>();
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
         permanentResults = null; // To be calculated and cached on first execution
         isDynamic = false;
         for (Object item : compiledComponents) {
             if (item instanceof Function || item instanceof SimpleVariable) {
                 isDynamic = true;
                 break;
             }
         }
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
diff --git a/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java b/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
index 5f60592b9..e5817e0d4 100644
--- a/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
+++ b/src/core/org/apache/jmeter/gui/action/SearchTreeDialog.java
@@ -1,230 +1,228 @@
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
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.BorderLayout;
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashSet;
-import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.ActionMap;
 import javax.swing.BorderFactory;
 import javax.swing.BoxLayout;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import javax.swing.JRootPane;
 import javax.swing.JTree;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * FIXME Why is searchTF not getting focus correctly after having been setVisible(false) once
  */
 public class SearchTreeDialog extends JDialog implements ActionListener {
 
     private static final long serialVersionUID = -4436834972710248247L;
 
     private static final Logger logger = LoggingManager.getLoggerForClass();
 
     private JButton searchButton;
 
     private JLabeledTextField searchTF;
 
     private JCheckBox isRegexpCB;
 
     private JCheckBox isCaseSensitiveCB;
 
     private JButton cancelButton;
 
     /**
      * Store last search
      */
     private transient String lastSearch = null;
 
     private JButton searchAndExpandButton;
 
     public SearchTreeDialog() {
         super((JFrame) null, JMeterUtils.getResString("search_tree_title"), true); //$NON-NLS-1$
         init();
     }
 
     @Override
     protected JRootPane createRootPane() {
         JRootPane rootPane = new JRootPane();
         // Hide Window on ESC
         Action escapeAction = new AbstractAction("ESCAPE") {
 
             private static final long serialVersionUID = -6543764044868772971L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 setVisible(false);
             }
         };
         // Do search on Enter
         Action enterAction = new AbstractAction("ENTER") {
 
             private static final long serialVersionUID = -3661361497864527363L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 doSearch(actionEvent);
             }
         };
         ActionMap actionMap = rootPane.getActionMap();
         actionMap.put(escapeAction.getValue(Action.NAME), escapeAction);
         actionMap.put(enterAction.getValue(Action.NAME), enterAction);
         InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         inputMap.put(KeyStrokes.ESC, escapeAction.getValue(Action.NAME));
         inputMap.put(KeyStrokes.ENTER, enterAction.getValue(Action.NAME));
 
         return rootPane;
     }
 
     private void init() {
         this.getContentPane().setLayout(new BorderLayout(10,10));
 
         searchTF = new JLabeledTextField(JMeterUtils.getResString("search_text_field"), 20); //$NON-NLS-1$
         if(!StringUtils.isEmpty(lastSearch)) {
             searchTF.setText(lastSearch);
         }
         isRegexpCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), false); //$NON-NLS-1$
         isCaseSensitiveCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); //$NON-NLS-1$
         Font font = new Font("SansSerif", Font.PLAIN, 10); // reduce font
         isRegexpCB.setFont(font);
         isCaseSensitiveCB.setFont(font);
 
         JPanel searchCriterionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         searchCriterionPanel.add(isCaseSensitiveCB);
         searchCriterionPanel.add(isRegexpCB);
 
         JPanel searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(7, 3, 3, 3));
         searchPanel.add(searchTF, BorderLayout.NORTH);
         searchPanel.add(searchCriterionPanel, BorderLayout.CENTER);
         JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
 
         searchButton = new JButton(JMeterUtils.getResString("search")); //$NON-NLS-1$
         searchButton.addActionListener(this);
         searchAndExpandButton = new JButton(JMeterUtils.getResString("search_expand")); //$NON-NLS-1$
         searchAndExpandButton.addActionListener(this);
         cancelButton = new JButton(JMeterUtils.getResString("cancel")); //$NON-NLS-1$
         cancelButton.addActionListener(this);
         buttonsPanel.add(searchButton);
         buttonsPanel.add(searchAndExpandButton);
         buttonsPanel.add(cancelButton);
         searchPanel.add(buttonsPanel, BorderLayout.SOUTH);
         this.getContentPane().add(searchPanel);
         searchTF.requestFocusInWindow();
 
         this.pack();
         ComponentUtil.centerComponentInWindow(this);
     }
 
     /**
      * Do search
      * @param e {@link ActionEvent}
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getSource()==cancelButton) {
             searchTF.requestFocusInWindow();
             this.setVisible(false);
             return;
         }
         doSearch(e);
     }
 
     /**
      * @param e {@link ActionEvent}
      */
     private void doSearch(ActionEvent e) {
         boolean expand = e.getSource()==searchAndExpandButton;
         String wordToSearch = searchTF.getText();
         if (StringUtils.isEmpty(wordToSearch)) {
             return;
         } else {
             this.lastSearch = wordToSearch;
         }
 
         // reset previous result
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.SEARCH_RESET));
         // do search
         Searcher searcher = null;
         if (isRegexpCB.isSelected()) {
             searcher = new RegexpSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText());
         } else {
             searcher = new RawTextSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText());
         }
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeModel jMeterTreeModel = guiPackage.getTreeModel();
         Set<JMeterTreeNode> nodes = new HashSet<>();
         for (JMeterTreeNode jMeterTreeNode : jMeterTreeModel.getNodesOfType(Searchable.class)) {
             try {
                 if (jMeterTreeNode.getUserObject() instanceof Searchable){
                     Searchable searchable = (Searchable) jMeterTreeNode.getUserObject();
                     List<JMeterTreeNode> matchingNodes = jMeterTreeNode.getPathToThreadGroup();
                     List<String> searchableTokens = searchable.getSearchableTokens();
                     boolean result = searcher.search(searchableTokens);
                     if (result) {
                         nodes.addAll(matchingNodes);
                     }
                 }
             } catch (Exception ex) {
                 logger.error("Error occured searching for word:"+ wordToSearch, ex);
             }
         }
         GuiPackage guiInstance = GuiPackage.getInstance();
         JTree jTree = guiInstance.getMainFrame().getTree();
 
-        for (Iterator<JMeterTreeNode> iterator = nodes.iterator(); iterator.hasNext();) {
-            JMeterTreeNode jMeterTreeNode = iterator.next();
+        for (JMeterTreeNode jMeterTreeNode : nodes) {
             jMeterTreeNode.setMarkedBySearch(true);
             if (expand) {
                 jTree.expandPath(new TreePath(jMeterTreeNode.getPath()));
             }
         }
         GuiPackage.getInstance().getMainFrame().repaint();
         searchTF.requestFocusInWindow();
         this.setVisible(false);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/MultiPropertyConverter.java b/src/core/org/apache/jmeter/save/converters/MultiPropertyConverter.java
index 64f1b7b06..07b6dc9a8 100644
--- a/src/core/org/apache/jmeter/save/converters/MultiPropertyConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/MultiPropertyConverter.java
@@ -1,86 +1,84 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.MapProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
 
-import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
+import com.thoughtworks.xstream.mapper.Mapper;
 
 public class MultiPropertyConverter extends AbstractCollectionConverter {
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$";  //$NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not use types
         return CollectionProperty.class.equals(arg0) || MapProperty.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
         MultiProperty prop = (MultiProperty) arg0;
+        
         writer.addAttribute(ConversionHelp.ATT_NAME, ConversionHelp.encode(prop.getName()));
-        PropertyIterator iter = prop.iterator();
-        while (iter.hasNext()) {
-            writeItem(iter.next(), context, writer);
+        for (JMeterProperty jMeterProperty : prop) {
+            writeItem(jMeterProperty, context, writer);
         }
-
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         MultiProperty prop = (MultiProperty) createCollection(context.getRequiredType());
         prop.setName(ConversionHelp.decode(reader.getAttribute(ConversionHelp.ATT_NAME)));
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             JMeterProperty subProp = (JMeterProperty) readItem(reader, context, prop);
             if (subProp != null) { // could be null if it has been deleted via NameUpdater
                 prop.addProperty(subProp);
             }
             reader.moveUp();
         }
         return prop;
     }
 
     /**
      * @param arg0 the mapper
      */
     public MultiPropertyConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java b/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
index 49eb16a36..0461fb3d9 100644
--- a/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
+++ b/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
@@ -1,214 +1,210 @@
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
  */
 package org.apache.jmeter.testbeans;
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.LinkedList;
 
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testbeans.gui.TableEditor;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.Converter;
 import org.apache.log.Logger;
 
 /**
  * This is an experimental class. An attempt to address the complexity of
  * writing new JMeter components.
  * <p>
  * TestBean currently extends AbstractTestElement to support
  * backward-compatibility, but the property-value-map may later on be separated
  * from the test beans themselves. To ensure this will be doable with minimum
  * damage, all inherited methods are deprecated.
  *
  */
 public class TestBeanHelper {
     protected static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Prepare the bean for work by populating the bean's properties from the
      * property value map.
      * <p>
      *
      * @param el the TestElement to be prepared
      * @deprecated to limit it's usage in expectation of moving it elsewhere.
      */
     @Deprecated
     public static void prepare(TestElement el) {
         if (!(el instanceof TestBean)) {
             return;
         }
         try {
             BeanInfo beanInfo = Introspector.getBeanInfo(el.getClass());
             PropertyDescriptor[] descs = beanInfo.getPropertyDescriptors();
 
             if (log.isDebugEnabled()) {
                 log.debug("Preparing " + el.getClass());
             }
 
             for (PropertyDescriptor desc : descs) {
                 if (isDescriptorIgnored(desc)) {
                     if (log.isDebugEnabled()) {
                         log.debug("Ignoring property '" + desc.getName() + "' in " + el.getClass().getCanonicalName());
                     }
                     continue;
                 }
                 // Obtain a value of the appropriate type for this property.
                 JMeterProperty jprop = el.getProperty(desc.getName());
                 Class<?> type = desc.getPropertyType();
                 Object value = unwrapProperty(desc, jprop, type);
 
                 if (log.isDebugEnabled()) {
                     log.debug("Setting " + jprop.getName() + "=" + value);
                 }
 
                 // Set the bean's property to the value we just obtained:
                 if (value != null || !type.isPrimitive())
                 // We can't assign null to primitive types.
                 {
                     Method writeMethod = desc.getWriteMethod();
                     if (writeMethod!=null) {
                         invokeOrBailOut(el, writeMethod, new Object[] {value});
                     }
                 }
             }
         } catch (IntrospectionException e) {
             log.error("Couldn't set properties for " + el.getClass().getName(), e);
         } catch (UnsatisfiedLinkError ule) { // Can occur running headless on Jenkins
             log.error("Couldn't set properties for " + el.getClass().getName());
             throw ule;
         }
     }
 
     private static Object unwrapProperty(PropertyDescriptor desc, JMeterProperty jprop, Class<?> type) {
         Object value;
         if(jprop instanceof TestElementProperty)
         {
             TestElement te = ((TestElementProperty)jprop).getElement();
             if(te instanceof TestBean)
             {
                 prepare(te);
             }
             value = te;
         }
         else if(jprop instanceof MultiProperty)
         {
             value = unwrapCollection((MultiProperty)jprop,(String)desc.getValue(TableEditor.CLASSNAME));
         }
         // value was not provided, and this is allowed
         else if (jprop instanceof NullProperty &&
                 // use negative condition so missing (null) value is treated as FALSE
                 ! Boolean.TRUE.equals(desc.getValue(GenericTestBeanCustomizer.NOT_UNDEFINED))) {    
             value=null;
         } else {
             value = Converter.convert(jprop.getStringValue(), type);
         }
         return value;
     }
 
-    private static Object unwrapCollection(MultiProperty prop,String type)
+    private static Object unwrapCollection(MultiProperty prop, String type)
     {
         if(prop instanceof CollectionProperty)
         {
             Collection<Object> values = new LinkedList<>();
-            PropertyIterator iter = prop.iterator();
-            while(iter.hasNext())
-            {
-                try
-                {
-                    values.add(unwrapProperty(null,iter.next(),Class.forName(type)));
+            for (JMeterProperty jMeterProperty : prop) {
+                try {
+                    values.add(unwrapProperty(null, jMeterProperty, Class.forName(type)));
                 }
-                catch(Exception e)
-                {
+                catch(Exception e) {
                     log.error("Couldn't convert object: " + prop.getObjectValue() + " to " + type,e);
                 }
             }
             return values;
         }
         return null;
     }
 
     /**
      * Utility method that invokes a method and does the error handling around
      * the invocation.
      *
      * @param invokee
      *            the object on which the method should be invoked
      * @param method
      *            the method which should be invoked
      * @param params
      *            the parameters for the method
      * @return the result of the method invocation.
      */
     private static Object invokeOrBailOut(Object invokee, Method method, Object[] params) {
         try {
             return method.invoke(invokee, params);
         } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
             throw new Error(createMessage(invokee, method, params), e);
         }
     }
 
     private static String createMessage(Object invokee, Method method, Object[] params){
         StringBuilder sb = new StringBuilder();
         sb.append("This should never happen. Tried to invoke:\n");
         sb.append(invokee.getClass().getName());
         sb.append("#");
         sb.append(method.getName());
         sb.append("(");
         for(Object o : params) {
             if (o != null) {
                 sb.append(o.getClass().getSimpleName());
                 sb.append(' ');
             }
             sb.append(o);
             sb.append(' ');
         }
         sb.append(")");
         return sb.toString();
     }
 
     /**
      * Checks whether the descriptor should be ignored, i.e.
      * <ul>
      * <li>isHidden</li>
      * <li>isExpert and JMeter not using expert mode</li>
      * <li>no read method</li>
      * <li>no write method</li>
      * </ul>
      * @param descriptor the {@link PropertyDescriptor} to be checked
      * @return <code>true</code> if the descriptor should be ignored
      */
     public static boolean isDescriptorIgnored(PropertyDescriptor descriptor) {
         return descriptor.isHidden() 
             || (descriptor.isExpert() && !JMeterUtils.isExpertMode())
             || descriptor.getReadMethod() == null 
             || descriptor.getWriteMethod() == null;
     }
 }
diff --git a/src/core/org/apache/jmeter/testelement/property/MultiProperty.java b/src/core/org/apache/jmeter/testelement/property/MultiProperty.java
index 36a73d355..35afd471d 100644
--- a/src/core/org/apache/jmeter/testelement/property/MultiProperty.java
+++ b/src/core/org/apache/jmeter/testelement/property/MultiProperty.java
@@ -1,97 +1,98 @@
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
 
 package org.apache.jmeter.testelement.property;
 
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * For JMeterProperties that hold multiple properties within, provides a simple
  * interface for retrieving a property iterator for the sub values.
  *
  * @version $Revision$
  */
-public abstract class MultiProperty extends AbstractProperty {
+public abstract class MultiProperty extends AbstractProperty implements Iterable<JMeterProperty> {
     private static final long serialVersionUID = 240L;
 
     public MultiProperty() {
         super();
     }
 
     public MultiProperty(String name) {
         super(name);
     }
 
     /**
      * Get the property iterator to iterate through the sub-values of this
      * JMeterProperty.
      *
      * @return an iterator for the sub-values of this property
      */
+    @Override
     public abstract PropertyIterator iterator();
 
     /**
      * Add a property to the collection.
      *
      * @param prop the {@link JMeterProperty} to add
      */
     public abstract void addProperty(JMeterProperty prop);
 
     /**
      * Clear away all values in the property.
      */
     public abstract void clear();
 
     @Override
     public void setRunningVersion(boolean running) {
         super.setRunningVersion(running);
         PropertyIterator iter = iterator();
         while (iter.hasNext()) {
             iter.next().setRunningVersion(running);
         }
     }
 
     protected void recoverRunningVersionOfSubElements(TestElement owner) {
         PropertyIterator iter = iterator();
         while (iter.hasNext()) {
             JMeterProperty prop = iter.next();
             if (owner.isTemporary(prop)) {
                 iter.remove();
             } else {
                 prop.recoverRunningVersion(owner);
             }
         }
     }
 
     @Override
     public void mergeIn(JMeterProperty prop) {
         if (prop.getObjectValue() == getObjectValue()) {
             return;
         }
         log.debug("merging in " + prop.getClass());
         if (prop instanceof MultiProperty) {
             PropertyIterator iter = ((MultiProperty) prop).iterator();
             while (iter.hasNext()) {
                 JMeterProperty item = iter.next();
                 addProperty(item);
             }
         } else {
             addProperty(prop);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/testelement/property/PropertyIterator.java b/src/core/org/apache/jmeter/testelement/property/PropertyIterator.java
index d67b27dd9..afa07cd8e 100644
--- a/src/core/org/apache/jmeter/testelement/property/PropertyIterator.java
+++ b/src/core/org/apache/jmeter/testelement/property/PropertyIterator.java
@@ -1,27 +1,33 @@
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
 
 package org.apache.jmeter.testelement.property;
 
-public interface PropertyIterator {
+import java.util.Iterator;
+
+public interface PropertyIterator extends Iterator<JMeterProperty> {
+    
+    @Override
     boolean hasNext();
 
+    @Override
     JMeterProperty next();
 
+    @Override
     void remove();
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/AuthManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/AuthManager.java
index 7ca47cb75..7af10f74e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/AuthManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/AuthManager.java
@@ -1,538 +1,537 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.security.Principal;
 import java.util.ArrayList;
 import java.util.NoSuchElementException;
 import java.util.StringTokenizer;
 
 import javax.security.auth.Subject;
 
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.Credentials;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.params.AuthPolicy;
 import org.apache.http.impl.auth.SPNegoSchemeFactory;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 // For Unit tests, @see TestAuthManager
 
 /**
  * This class provides a way to provide Authorization in jmeter requests. The
  * format of the authorization file is: URL user pass where URL is an HTTP URL,
  * user a username to use and pass the appropriate password.
  *
  */
 public class AuthManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String CLEAR = "AuthManager.clearEachIteration";// $NON-NLS-1$
 
     private static final String AUTH_LIST = "AuthManager.auth_list"; //$NON-NLS-1$
 
     private static final String[] COLUMN_RESOURCE_NAMES = {
         "auth_base_url", //$NON-NLS-1$
         "username",      //$NON-NLS-1$
         "password",      //$NON-NLS-1$
         "domain",        //$NON-NLS-1$
         "realm",         //$NON-NLS-1$
         "mechanism",     //$NON-NLS-1$
         };
 
     // Column numbers - must agree with order above
     public static final int COL_URL = 0;
     public static final int COL_USERNAME = 1;
     public static final int COL_PASSWORD = 2;
     public static final int COL_DOMAIN = 3;
     public static final int COL_REALM = 4;
     public static final int COL_MECHANISM = 5;
 
     private static final int COLUMN_COUNT = COLUMN_RESOURCE_NAMES.length;
 
     private static final Credentials USE_JAAS_CREDENTIALS = new NullCredentials();
 
     private static final boolean DEFAULT_CLEAR_VALUE = false;
 
     /** Decides whether port should be omitted from SPN for kerberos spnego authentication */
     private static final boolean STRIP_PORT = JMeterUtils.getPropDefault("kerberos.spnego.strip_port", true);
 
     public enum Mechanism {
         BASIC_DIGEST, KERBEROS;
     }
 
     private static final class NullCredentials implements Credentials {
         @Override
         public String getPassword() {
             return null;
         }
 
         @Override
         public Principal getUserPrincipal() {
             return null;
         }
     }
     
     private KerberosManager kerberosManager = new KerberosManager();
 
     /**
      * Default Constructor.
      */
     public AuthManager() {
         setProperty(new CollectionProperty(AUTH_LIST, new ArrayList<>()));
     }
 
     /** {@inheritDoc} */
     @Override
     public void clear() {
         super.clear();
         kerberosManager.clearSubjects();
         setProperty(new CollectionProperty(AUTH_LIST, new ArrayList<>()));
     }
 
     /**
      * Update an authentication record.
      *
      * @param index
      *            index at which position the record should be set
      * @param url
      *            url for which the authentication record should be used
      * @param user
      *            name of the user
      * @param pass
      *            password of the user
      * @param domain
      *            domain of the user
      * @param realm
      *            realm of the site
      * @param mechanism
      *            authentication {@link Mechanism} to use
      */
     public void set(int index, String url, String user, String pass, String domain, String realm, Mechanism mechanism) {
         Authorization auth = new Authorization(url, user, pass, domain, realm, mechanism);
         if (index >= 0) {
             getAuthObjects().set(index, new TestElementProperty(auth.getName(), auth));
         } else {
             getAuthObjects().addItem(auth);
         }
     }
 
     public CollectionProperty getAuthObjects() {
         return (CollectionProperty) getProperty(AUTH_LIST);
     }
 
     public int getColumnCount() {
         return COLUMN_COUNT;
     }
 
     public String getColumnName(int column) {
         return COLUMN_RESOURCE_NAMES[column];
     }
 
     public Class<?> getColumnClass(int column) {
         return COLUMN_RESOURCE_NAMES[column].getClass();
     }
 
     public Authorization getAuthObjectAt(int row) {
         return (Authorization) getAuthObjects().get(row).getObjectValue();
     }
 
     public boolean isEditable() {
         return true;
     }
 
     /**
      * Return the record at index i
      *
      * @param i
      *            index of the record to get
      * @return authorization record at index <code>i</code>
      */
     public Authorization get(int i) {
         return (Authorization) getAuthObjects().get(i).getObjectValue();
     }
 
     public String getAuthHeaderForURL(URL url) {
         Authorization auth = getAuthForURL(url);
         if (auth == null) {
             return null;
         }
         return auth.toBasicHeader();
     }
 
     public Authorization getAuthForURL(URL url) {
         if (!isSupportedProtocol(url)) {
             return null;
         }
 
         // TODO: replace all this url2 mess with a proper method
         // "areEquivalent(url1, url2)" that
         // would also ignore case in protocol and host names, etc. -- use that
         // method in the CookieManager too.
 
         URL url2 = null;
 
         try {
             if (url.getPort() == -1) {
                 // Obtain another URL with an explicit port:
                 int port = url.getProtocol().equalsIgnoreCase("http") ? HTTPConstants.DEFAULT_HTTP_PORT : HTTPConstants.DEFAULT_HTTPS_PORT;
                 // only http and https are supported
                 url2 = new URL(url.getProtocol(), url.getHost(), port, url.getPath());
             } else if ((url.getPort() == HTTPConstants.DEFAULT_HTTP_PORT && url.getProtocol().equalsIgnoreCase("http"))
                     || (url.getPort() == HTTPConstants.DEFAULT_HTTPS_PORT && url.getProtocol().equalsIgnoreCase("https"))) {
                 url2 = new URL(url.getProtocol(), url.getHost(), url.getPath());
             }
         } catch (MalformedURLException e) {
             log.error("Internal error!", e); // this should never happen
             // anyway, we'll continue with url2 set to null.
         }
 
         String s1 = url.toString();
         String s2 = null;
         if (url2 != null) {
             s2 = url2.toString();
         }
 
-            log.debug("Target URL strings to match against: "+s1+" and "+s2);
+        log.debug("Target URL strings to match against: "+s1+" and "+s2);
         // TODO should really return most specific (i.e. longest) match.
-        for (PropertyIterator iter = getAuthObjects().iterator(); iter.hasNext();) {
-            Authorization auth = (Authorization) iter.next().getObjectValue();
+        for (JMeterProperty jMeterProperty : getAuthObjects()) {
+            Authorization auth = (Authorization) jMeterProperty.getObjectValue();
 
             String uRL = auth.getURL();
             log.debug("Checking match against auth'n entry: "+uRL);
             if (s1.startsWith(uRL) || s2 != null && s2.startsWith(uRL)) {
                 log.debug("Matched");
                 return auth;
             }
             log.debug("Did not match");
         }
         return null;
     }
 
     /**
      * Tests whether an authorization record is available for a given URL
      *
      * @param url
      *            {@link URL} for which an authorization record should be
      *            available
      * @return <code>true</code> if an authorization is setup for url,
      *         <code>false</code> otherwise
      */
     public boolean hasAuthForURL(URL url) {
         return getAuthForURL(url) != null;
     }
     
     /**
      * Get a {@link Subject} for a given URL, if available
      *
      * @param url
      *            {@link URL} for which the subject was asked
      * @return Subject if Auth Scheme uses Subject and an authorization is setup
      *         for <code>url</code>, <code>null</code> otherwise
      */
     public Subject getSubjectForUrl(URL url) {
         Authorization authorization = getAuthForURL(url);
         if (authorization != null && Mechanism.KERBEROS.equals(authorization.getMechanism())) {
             return kerberosManager.getSubjectForUser(
                     authorization.getUser(), authorization.getPass());
         }
         return null;
     }
 
     /** {@inheritDoc} */
     @Override
     public void addConfigElement(ConfigElement config) {
     }
 
     /**
      * Add newAuthorization if it does not already exist
      * @param newAuthorization authorization to be added
      */
     public void addAuth(Authorization newAuthorization) {
-        boolean alreadyExists=false;
-        PropertyIterator iter = getAuthObjects().iterator();
+        boolean alreadyExists = false;
         //iterate over authentication objects in manager
-        while (iter.hasNext()) {
-            Authorization authorization = (Authorization) iter.next().getObjectValue();
+        for (JMeterProperty jMeterProperty : getAuthObjects()) {
+            Authorization authorization = (Authorization) jMeterProperty.getObjectValue();
             if (authorization == null) {
                 continue;
             }
             if (match(authorization,newAuthorization)) {
                 if (log.isDebugEnabled()) {
                     log.debug("Found the same Authorization object:" + newAuthorization.toString());
                 }
                 //set true, if found the same one
                 alreadyExists=true;
                 break;
             }
         }
         if(!alreadyExists){
             // if there was no such auth object, add.
             getAuthObjects().addItem(newAuthorization);
         }
     }
 
     public void addAuth() {
         getAuthObjects().addItem(new Authorization());
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean expectsModification() {
         return false;
     }
 
     /**
      * Save the authentication data to a file.
      *
      * @param authFile
      *            path of the file to save the authentication data to
      * @throws IOException
      *             when writing to the file fails
      */
     public void save(String authFile) throws IOException {
         File file = new File(authFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir"),authFile);
         }
         PrintWriter writer = null;
         try {
             writer = new PrintWriter(new FileWriter(file));
             writer.println("# JMeter generated Authorization file");
             for (int i = 0; i < getAuthObjects().size(); i++) {
                 Authorization auth = (Authorization) getAuthObjects().get(i).getObjectValue();
                 writer.println(auth.toString());
             }
             writer.flush();
             writer.close();
         } finally {
             JOrphanUtils.closeQuietly(writer);
         }
     }
 
     /**
      * Add authentication data from a file.
      *
      * @param authFile
      *            path to the file to read the authentication data from
      * @throws IOException
      *             when reading the data fails
      */
     public void addFile(String authFile) throws IOException {
         File file = new File(authFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir") + File.separator + authFile);
         }
         if (!file.canRead()) {
             throw new IOException("The file you specified cannot be read.");
         }
 
         BufferedReader reader = null;
         boolean ok = true;
         try {
             reader = new BufferedReader(new FileReader(file));
             String line;
             while ((line = reader.readLine()) != null) {
                 try {
                     if (line.startsWith("#") || JOrphanUtils.isBlank(line)) { //$NON-NLS-1$
                         continue;
                     }
                     StringTokenizer st = new StringTokenizer(line, "\t"); //$NON-NLS-1$
                     String url = st.nextToken();
                     String user = st.nextToken();
                     String pass = st.nextToken();
                     String domain = "";
                     String realm = "";
                     if (st.hasMoreTokens()){// Allow for old format file without the extra columnns
                         domain = st.nextToken();
                         realm = st.nextToken();
                     }
                     Mechanism mechanism = Mechanism.BASIC_DIGEST;
                     if (st.hasMoreTokens()){// Allow for old format file without mechanism support
                         mechanism = Mechanism.valueOf(st.nextToken());
                     }
                     Authorization auth = new Authorization(url, user, pass, domain, realm, mechanism);
                     getAuthObjects().addItem(auth);
                 } catch (NoSuchElementException e) {
                     log.error("Error parsing auth line: '" + line + "'");
                     ok = false;
                 }
             }
         } finally {
             JOrphanUtils.closeQuietly(reader);
         }
         if (!ok){
             JMeterUtils.reportErrorToUser("One or more errors found when reading the Auth file - see the log file");
         }
     }
 
     /**
      * Remove an authentication record.
      *
      * @param index
      *            index of the authentication record to remove
      */
     public void remove(int index) {
         getAuthObjects().remove(index);
     }
 
     /**
      *
      * @return true if kerberos auth must be cleared on each mail loop iteration 
      */
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR, DEFAULT_CLEAR_VALUE);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(CLEAR, clear, DEFAULT_CLEAR_VALUE);
     }
 
     /**
      * Return the number of records.
      *
      * @return the number of records
      */
     public int getAuthCount() {
         return getAuthObjects().size();
     }
 
     // Needs to be package protected for Unit test
     static boolean isSupportedProtocol(URL url) {
         String protocol = url.getProtocol().toLowerCase(java.util.Locale.ENGLISH);
         return protocol.equals(HTTPConstants.PROTOCOL_HTTP) || protocol.equals(HTTPConstants.PROTOCOL_HTTPS);
     }    
 
     /**
      * Configure credentials and auth scheme on client if an authorization is 
      * available for url
      * @param client {@link HttpClient}
      * @param url URL to test 
      * @param credentialsProvider {@link CredentialsProvider}
      * @param localHost host running JMeter
      */
     public void setupCredentials(HttpClient client, URL url,
             CredentialsProvider credentialsProvider, String localHost) {
         Authorization auth = getAuthForURL(url);
         if (auth != null) {
             String username = auth.getUser();
             String realm = auth.getRealm();
             String domain = auth.getDomain();
             if (log.isDebugEnabled()){
                 log.debug(username + " > D="+domain+" R="+realm + " M="+auth.getMechanism());
             }
             if (Mechanism.KERBEROS.equals(auth.getMechanism())) {
                 ((AbstractHttpClient) client).getAuthSchemes().register(AuthPolicy.SPNEGO, new SPNegoSchemeFactory(isStripPort(url)));
                 credentialsProvider.setCredentials(new AuthScope(null, -1, null), USE_JAAS_CREDENTIALS);
             } else {
                 credentialsProvider.setCredentials(
                         new AuthScope(url.getHost(), url.getPort(), realm.length()==0 ? null : realm),
                         new NTCredentials(username, auth.getPass(), localHost, domain));
             }
         }
     }
 
     /**
      * IE and Firefox will always strip port from the url before constructing
      * the SPN. Chrome has an option (<code>--enable-auth-negotiate-port</code>)
      * to include the port if it differs from <code>80</code> or
      * <code>443</code>. That behavior can be changed by setting the jmeter
      * property <code>kerberos.spnego.strip_port</code>.
      *
      * @param url to be checked
      * @return <code>true</code> when port should omitted in SPN
      */
     private boolean isStripPort(URL url) {
         if (STRIP_PORT) {
             return true;
         }
         return (url.getPort() == HTTPConstants.DEFAULT_HTTP_PORT ||
                 url.getPort() == HTTPConstants.DEFAULT_HTTPS_PORT);
     }
 
     /**
      * Check if two authorization objects are equal ignoring username/password
      * @param a {@link Authorization}
      * @param b {@link Authorization}
      * @return true if a and b match
      */
     private boolean match(Authorization a, Authorization b){
         return
                 a.getURL().equals(b.getURL())&&
                 a.getDomain().equals(b.getDomain())&&
                 a.getRealm().equals(b.getRealm())&&
                 a.getMechanism().equals(b.getMechanism());
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted() {
         kerberosManager.clearSubjects();
     }
     
     /** {@inheritDoc} */
     @Override
     public void testEnded() {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded(String host) {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             kerberosManager.clearSubjects();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
index d937cc5d8..96460e2a4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
@@ -1,439 +1,439 @@
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
 
 // For unit tests @see TestCookieManager
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 
 import org.apache.http.client.params.CookiePolicy;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * This class provides an interface to the netscape cookies file to pass cookies
  * along with a request.
  *
  * Now uses Commons HttpClient parsing and matching code (since 2.1.2)
  *
  */
 public class CookieManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //++ JMX tag values
     private static final String CLEAR = "CookieManager.clearEachIteration";// $NON-NLS-1$
 
     private static final String COOKIES = "CookieManager.cookies";// $NON-NLS-1$
 
     private static final String POLICY = "CookieManager.policy"; //$NON-NLS-1$
     
     private static final String IMPLEMENTATION = "CookieManager.implementation"; //$NON-NLS-1$
     //-- JMX tag values
 
     private static final String TAB = "\t"; //$NON-NLS-1$
 
     // See bug 33796
     private static final boolean DELETE_NULL_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.delete_null_cookies", true);// $NON-NLS-1$
 
     // See bug 28715
     // Package protected for tests
     static final boolean ALLOW_VARIABLE_COOKIES
         = JMeterUtils.getPropDefault("CookieManager.allow_variable_cookies", true);// $NON-NLS-1$
 
     private static final String COOKIE_NAME_PREFIX =
         JMeterUtils.getPropDefault("CookieManager.name.prefix", "COOKIE_").trim();// $NON-NLS-1$ $NON-NLS-2$
 
     private static final boolean SAVE_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.save.cookies", false);// $NON-NLS-1$
 
     private static final boolean CHECK_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.check.cookies", true);// $NON-NLS-1$
 
     static {
         log.info("Settings:"
                 + " Delete null: " + DELETE_NULL_COOKIES
                 + " Check: " + CHECK_COOKIES
                 + " Allow variable: " + ALLOW_VARIABLE_COOKIES
                 + " Save: " + SAVE_COOKIES
                 + " Prefix: " + COOKIE_NAME_PREFIX
                 );
     }
     private transient CookieHandler cookieHandler;
 
     private transient CollectionProperty initialCookies;
 
     public static final String DEFAULT_POLICY = CookiePolicy.BROWSER_COMPATIBILITY;
     
     public static final String DEFAULT_IMPLEMENTATION = HC3CookieHandler.class.getName();
 
     public CookieManager() {
         clearCookies(); // Ensure that there is always a collection available
     }
 
     // ensure that the initial cookies are copied to the per-thread instances
     /** {@inheritDoc} */
     @Override
     public Object clone(){
         CookieManager clone = (CookieManager) super.clone();
         clone.initialCookies = initialCookies;
         clone.cookieHandler = cookieHandler;
         return clone;
     }
 
     public String getPolicy() {
         return getPropertyAsString(POLICY, DEFAULT_POLICY);
     }
 
     public void setCookiePolicy(String policy){
         setProperty(POLICY, policy, DEFAULT_POLICY);
     }
 
     public CollectionProperty getCookies() {
         return (CollectionProperty) getProperty(COOKIES);
     }
 
     public int getCookieCount() {// Used by GUI
         return getCookies().size();
     }
 
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR, clear));
     }
 
     public String getImplementation() {
         return getPropertyAsString(IMPLEMENTATION, DEFAULT_IMPLEMENTATION);
     }
 
     public void setImplementation(String implementation){
         setProperty(IMPLEMENTATION, implementation, DEFAULT_IMPLEMENTATION);
     }
 
     /**
      * Save the static cookie data to a file.
      * <p>
      * Cookies are only taken from the GUI - runtime cookies are not included.
      *
      * @param authFile
      *            name of the file to store the cookies into. If the name is
      *            relative, the system property <code>user.dir</code> will be
      *            prepended
      * @throws IOException
      *             when writing to that file fails
      */
     public void save(String authFile) throws IOException {
         File file = new File(authFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir") // $NON-NLS-1$
                     + File.separator + authFile);
         }
-        PrintWriter writer = new PrintWriter(new FileWriter(file)); // TODO Charset ?
-        writer.println("# JMeter generated Cookie file");// $NON-NLS-1$
-        PropertyIterator cookies = getCookies().iterator();
-        long now = System.currentTimeMillis();
-        while (cookies.hasNext()) {
-            Cookie cook = (Cookie) cookies.next().getObjectValue();
-            final long expiresMillis = cook.getExpiresMillis();
-            if (expiresMillis == 0 || expiresMillis > now) { // only save unexpired cookies
-                writer.println(cookieToString(cook));
+        try(PrintWriter writer = new PrintWriter(new FileWriter(file))) { // TODO Charset ?
+            writer.println("# JMeter generated Cookie file");// $NON-NLS-1$
+            long now = System.currentTimeMillis();
+            for (JMeterProperty jMeterProperty : getCookies()) {
+                Cookie cook = (Cookie) jMeterProperty.getObjectValue();
+                final long expiresMillis = cook.getExpiresMillis();
+                if (expiresMillis == 0 || expiresMillis > now) { // only save unexpired cookies
+                    writer.println(cookieToString(cook));
+                }
             }
+            writer.flush();
         }
-        writer.flush();
-        writer.close();
     }
 
     /**
      * Add cookie data from a file.
      *
      * @param cookieFile
      *            name of the file to read the cookies from. If the name is
      *            relative, the system property <code>user.dir</code> will be
      *            prepended
      * @throws IOException
      *             if reading the file fails
      */
     public void addFile(String cookieFile) throws IOException {
         File file = new File(cookieFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir") // $NON-NLS-1$
                     + File.separator + cookieFile);
         }
         BufferedReader reader = null;
         if (file.canRead()) {
             reader = new BufferedReader(new FileReader(file)); // TODO Charset ?
         } else {
             throw new IOException("The file you specified cannot be read.");
         }
 
         // N.B. this must agree with the save() and cookieToString() methods
         String line;
         try {
             final CollectionProperty cookies = getCookies();
             while ((line = reader.readLine()) != null) {
                 try {
                     if (line.startsWith("#") || JOrphanUtils.isBlank(line)) {//$NON-NLS-1$
                         continue;
                     }
                     String[] st = JOrphanUtils.split(line, TAB, false);
 
                     final int _domain = 0;
                     //final int _ignored = 1;
                     final int _path = 2;
                     final int _secure = 3;
                     final int _expires = 4;
                     final int _name = 5;
                     final int _value = 6;
                     final int _fields = 7;
                     if (st.length!=_fields) {
                         throw new IOException("Expected "+_fields+" fields, found "+st.length+" in "+line);
                     }
 
                     if (st[_path].length()==0) {
                         st[_path] = "/"; //$NON-NLS-1$
                     }
                     boolean secure = Boolean.parseBoolean(st[_secure]);
                     long expires = Long.parseLong(st[_expires]);
                     if (expires==Long.MAX_VALUE) {
                         expires=0;
                     }
                     //long max was used to represent a non-expiring cookie, but that caused problems
                     Cookie cookie = new Cookie(st[_name], st[_value], st[_domain], st[_path], secure, expires);
                     cookies.addItem(cookie);
                 } catch (NumberFormatException e) {
                     throw new IOException("Error parsing cookie line\n\t'" + line + "'\n\t" + e);
                 }
             }
         } finally {
             reader.close();
          }
     }
 
     private String cookieToString(Cookie c){
         StringBuilder sb=new StringBuilder(80);
         sb.append(c.getDomain());
         //flag - if all machines within a given domain can access the variable.
         //(from http://www.cookiecentral.com/faq/ 3.5)
         sb.append(TAB).append("TRUE");
         sb.append(TAB).append(c.getPath());
         sb.append(TAB).append(JOrphanUtils.booleanToSTRING(c.getSecure()));
         sb.append(TAB).append(c.getExpires());
         sb.append(TAB).append(c.getName());
         sb.append(TAB).append(c.getValue());
         return sb.toString();
     }
 
     /** {@inheritDoc} */
     @Override
     public void recoverRunningVersion() {
         // do nothing, the cookie manager has to accept changes.
     }
 
     /** {@inheritDoc} */
     @Override
     public void setRunningVersion(boolean running) {
         // do nothing, the cookie manager has to accept changes.
     }
 
     /**
      * Add a cookie.
      *
      * @param c cookie to be added
      */
     public void add(Cookie c) {
         String cv = c.getValue();
         String cn = c.getName();
         removeMatchingCookies(c); // Can't have two matching cookies
 
         if (DELETE_NULL_COOKIES && (null == cv || cv.length()==0)) {
             if (log.isDebugEnabled()) {
                 log.debug("Dropping cookie with null value " + c.toString());
             }
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Add cookie to store " + c.toString());
             }
             getCookies().addItem(c);
             if (SAVE_COOKIES)  {
                 JMeterContext context = getThreadContext();
                 if (context.isSamplingStarted()) {
                     context.getVariables().put(COOKIE_NAME_PREFIX+cn, cv);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void clear(){
         super.clear();
         clearCookies(); // ensure data is set up OK initially
     }
 
     /*
      * Remove all the cookies.
      */
     private void clearCookies() {
         log.debug("Clear all cookies from store");
         setProperty(new CollectionProperty(COOKIES, new ArrayList<>()));
     }
 
     /**
      * Remove a cookie.
      *
      * @param index index of the cookie to remove
      */
     public void remove(int index) {// TODO not used by GUI
         getCookies().remove(index);
     }
 
     /**
      * Return the cookie at index i.
      *
      * @param i index of the cookie to get
      * @return cookie at index <code>i</code>
      */
     public Cookie get(int i) {// Only used by GUI
         return (Cookie) getCookies().get(i).getObjectValue();
     }
 
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      *
      * @param url
      *            URL of the request to which the returned header will be added.
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     public String getCookieHeaderForURL(URL url) {
         return cookieHandler.getCookieHeaderForURL(getCookies(), url, ALLOW_VARIABLE_COOKIES);
     }
 
 
     public void addCookieFromHeader(String cookieHeader, URL url){
         cookieHandler.addCookieFromHeader(this, CHECK_COOKIES, cookieHeader, url);
     }
     /**
      * Check if cookies match, i.e. name, path and domain are equal.
      * <br/>
      * TODO - should we compare secure too?
      * @param a
      * @param b
      * @return true if cookies match
      */
     private boolean match(Cookie a, Cookie b){
         return
         a.getName().equals(b.getName())
         &&
         a.getPath().equals(b.getPath())
         &&
         a.getDomain().equals(b.getDomain());
     }
 
     void removeMatchingCookies(Cookie newCookie){
         // Scan for any matching cookies
         PropertyIterator iter = getCookies().iterator();
         while (iter.hasNext()) {
             Cookie cookie = (Cookie) iter.next().getObjectValue();
             if (cookie == null) {// TODO is this possible?
                 continue;
             }
             if (match(cookie,newCookie)) {
                 if (log.isDebugEnabled()) {
                     log.debug("New Cookie = " + newCookie.toString()
                               + " removing matching Cookie " + cookie.toString());
                 }
                 iter.remove();
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted() {
         initialCookies = getCookies();
         try {
             cookieHandler = (CookieHandler) ClassTools.construct(getImplementation(), getPolicy());
         } catch (JMeterException e) {
             log.error("Unable to load or invoke class: " + getImplementation(), e);
         }
         if (log.isDebugEnabled()){
             log.debug("Policy: "+getPolicy()+" Clear: "+getClearEachIteration());
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded() {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded(String host) {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             log.debug("Initialise cookies from pre-defined list");
             // No need to call clear
             setProperty(initialCookies.clone());
         }
     }
 
     /**
      * Package protected for tests
      * @return the cookieHandler
      */
     CookieHandler getCookieHandler() {
         return cookieHandler;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
index a5a4d5f5d..09fa34a77 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
@@ -1,241 +1,240 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with this
  * work for additional information regarding copyright ownership. The ASF
  * licenses this file to You under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations under
  * the License.
  */
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.impl.conn.SystemDefaultDnsResolver;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.xbill.DNS.ARecord;
 import org.xbill.DNS.Cache;
 import org.xbill.DNS.ExtendedResolver;
 import org.xbill.DNS.Lookup;
 import org.xbill.DNS.Record;
 import org.xbill.DNS.Resolver;
 import org.xbill.DNS.TextParseException;
 import org.xbill.DNS.Type;
 
 /**
  * This config element provides ability to have flexible control over DNS
  * caching function. Depending on option from @see
  * {@link org.apache.jmeter.protocol.http.gui.DNSCachePanel}, either system or
  * custom resolver can be used. Custom resolver uses dnsjava library, and gives
  * ability to bypass both OS and JVM cache. It allows to use paradigm
  * "1 virtual user - 1 DNS cache" in performance tests.
  *
  * @since 2.12
  */
 
 public class DNSCacheManager extends ConfigTestElement implements TestIterationListener, Serializable, DnsResolver {
     private static final long serialVersionUID = 2120L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient SystemDefaultDnsResolver systemDefaultDnsResolver = null;
 
     private Map<String, InetAddress[]> cache = null;
 
     private transient Resolver resolver = null;
 
     //++ JMX tag values
     public static final String CLEAR_CACHE_EACH_ITER = "DNSCacheManager.clearEachIteration"; // $NON-NLS-1$
 
     public static final String SERVERS = "DNSCacheManager.servers"; // $NON-NLS-1$
 
     public static final String IS_CUSTOM_RESOLVER = "DNSCacheManager.isCustomResolver"; // $NON-NLS-1$
     //-- JMX tag values
 
     public static final boolean DEFAULT_CLEAR_CACHE_EACH_ITER = false;
 
     public static final String DEFAULT_SERVERS = ""; // $NON-NLS-1$
 
     public static final boolean DEFAULT_IS_CUSTOM_RESOLVER = false;
 
     private final transient Cache lookupCache;
 
     // ensure that the initial DNSServers are copied to the per-thread instances
 
     public DNSCacheManager() {
         setProperty(new CollectionProperty(SERVERS, new ArrayList<String>()));
         //disabling cache
         lookupCache = new Cache();
         lookupCache.setMaxCache(0);
         lookupCache.setMaxEntries(0);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         DNSCacheManager clone = (DNSCacheManager) super.clone();
         clone.systemDefaultDnsResolver = new SystemDefaultDnsResolver();
         clone.cache = new LinkedHashMap<>();
         CollectionProperty dnsServers = getServers();
         try {
             String[] serverNames = new String[dnsServers.size()];
-            PropertyIterator dnsServIt = dnsServers.iterator();
-            int index=0;
-            while (dnsServIt.hasNext()) {
-                serverNames[index] = dnsServIt.next().getStringValue();
+            int index = 0;
+            for (JMeterProperty jMeterProperty : dnsServers) {
+                serverNames[index] = jMeterProperty.getStringValue();
                 index++;
             }
             clone.resolver = new ExtendedResolver(serverNames);
             log.debug("Using DNS Resolvers: "
                     + Arrays.asList(((ExtendedResolver) clone.resolver)
                             .getResolvers()));
             // resolvers will be chosen via round-robin
             ((ExtendedResolver) clone.resolver).setLoadBalance(true);
         } catch (UnknownHostException uhe) {
             log.warn("Failed to create Extended resolver: " + uhe.getMessage());
         }
         return clone;
     }
 
     /**
      *
      * Resolves address using system or custom DNS resolver
      */
     @Override
     public InetAddress[] resolve(String host) throws UnknownHostException {
         if (cache.containsKey(host)) {
             if (log.isDebugEnabled()) {
                 log.debug("Cache hit thr#" + JMeterContextService.getContext().getThreadNum() + ": " + host + "=>"
                         + Arrays.toString(cache.get(host)));
             }
             return cache.get(host);
         } else {
             InetAddress[] addresses = requestLookup(host);
             if (log.isDebugEnabled()) {
                 log.debug("Cache miss thr#" + JMeterContextService.getContext().getThreadNum() + ": " + host + "=>"
                         + Arrays.toString(addresses));
             }
             cache.put(host, addresses);
             return addresses;
         }
     }
 
     /**
      * Sends DNS request via system or custom DNS resolver
      */
     private InetAddress[] requestLookup(String host) throws UnknownHostException {
         InetAddress[] addresses = null;
         if (isCustomResolver() && ((ExtendedResolver) resolver).getResolvers().length > 0) {
             try {
                 Lookup lookup = new Lookup(host, Type.A);
                 lookup.setCache(lookupCache);
                 lookup.setResolver(resolver);
                 Record[] records = lookup.run();
                 if (records == null || records.length == 0) {
                     throw new UnknownHostException("Failed to resolve host name: " + host);
                 }
                 addresses = new InetAddress[records.length];
                 for (int i = 0; i < records.length; i++) {
                     addresses[i] = ((ARecord) records[i]).getAddress();
                 }
             } catch (TextParseException tpe) {
                 log.debug("Failed to create Lookup object: " + tpe);
             }
         } else {
             addresses = systemDefaultDnsResolver.resolve(host);
             if (log.isDebugEnabled()) {
                 log.debug("Cache miss: " + host + " Thread #" + JMeterContextService.getContext().getThreadNum()
                         + ", resolved with system resolver into " + Arrays.toString(addresses));
             }
         }
         return addresses;
     }
 
     /**
      * {@inheritDoc} Clean DNS cache if appropriate check-box was selected
      */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (isClearEachIteration()) {
             this.cache.clear();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clear() {
         super.clear();
         clearServers(); // ensure data is set up OK initially
     }
 
     /**
      * Remove all the servers.
      */
     private void clearServers() {
         log.debug("Clear all servers from store");
         setProperty(new CollectionProperty(SERVERS, new ArrayList<String>()));
     }
 
     public void addServer(String dnsServer) {
         getServers().addItem(dnsServer);
     }
 
     public CollectionProperty getServers() {
         return (CollectionProperty) getProperty(SERVERS);
     }
 
     /**
      * Clean DNS cache each iteration
      * 
      * @return boolean
      */
     public boolean isClearEachIteration() {
         return this.getPropertyAsBoolean(CLEAR_CACHE_EACH_ITER, DEFAULT_CLEAR_CACHE_EACH_ITER);
     }
 
     /**
      * Clean DNS cache each iteration
      *
      * @param clear
      *            flag whether DNS cache should be cleared on each iteration
      */
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR_CACHE_EACH_ITER, clear));
     }
 
     public boolean isCustomResolver() {
         return this.getPropertyAsBoolean(IS_CUSTOM_RESOLVER, DEFAULT_IS_CUSTOM_RESOLVER);
     }
 
     public void setCustomResolver(boolean isCustomResolver) {
         this.setProperty(IS_CUSTOM_RESOLVER, isCustomResolver);
     }
 
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
index edb0806f7..08d208561 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
@@ -1,200 +1,200 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 import java.util.Date;
 
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.cookie.CookieSpec;
 import org.apache.commons.httpclient.cookie.MalformedCookieException;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTPClient 3.1 implementation
  */
 public class HC3CookieHandler implements CookieHandler {
    private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final transient CookieSpec cookieSpec;
 
     /**
      * @param policy
      *            cookie policy to which to conform (see
      *            {@link CookiePolicy#getCookieSpec(String)}
      */
     public HC3CookieHandler(String policy) {
         super();
         this.cookieSpec = CookiePolicy.getCookieSpec(policy);
     }
 
     /**
      * Create an HttpClient cookie from a JMeter cookie
      */
     private org.apache.commons.httpclient.Cookie makeCookie(Cookie jmc){
         long exp = jmc.getExpiresMillis();
         org.apache.commons.httpclient.Cookie ret=
             new org.apache.commons.httpclient.Cookie(
                 jmc.getDomain(),
                 jmc.getName(),
                 jmc.getValue(),
                 jmc.getPath(),
                 exp > 0 ? new Date(exp) : null, // use null for no expiry
                 jmc.getSecure()
                );
         ret.setPathAttributeSpecified(jmc.isPathSpecified());
         ret.setDomainAttributeSpecified(jmc.isDomainSpecified());
         ret.setVersion(jmc.getVersion());
         return ret;
     }
     /**
      * Get array of valid HttpClient cookies for the URL
      *
      * @param cookiesCP cookies to consider
      * @param url the target URL
      * @param allowVariableCookie flag whether to allow jmeter variables in cookie values
      * @return array of HttpClient cookies
      *
      */
     org.apache.commons.httpclient.Cookie[] getCookiesForUrl(
             CollectionProperty cookiesCP,
             URL url, 
             boolean allowVariableCookie){
         org.apache.commons.httpclient.Cookie cookies[]=
             new org.apache.commons.httpclient.Cookie[cookiesCP.size()];
-        int i=0;
-        for (PropertyIterator iter = cookiesCP.iterator(); iter.hasNext();) {
-            Cookie jmcookie = (Cookie) iter.next().getObjectValue();
+        int i = 0;
+        for (JMeterProperty jMeterProperty : cookiesCP) {
+            Cookie jmcookie = (Cookie) jMeterProperty.getObjectValue();
             // Set to running version, to allow function evaluation for the cookie values (bug 28715)
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(true);
             }
             cookies[i++] = makeCookie(jmcookie);
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(false);
             }
         }
         String host = url.getHost();
         String protocol = url.getProtocol();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean secure = HTTPSamplerBase.isSecure(protocol);
         return cookieSpec.match(host, port, path, secure, cookies);
     }
     
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      *
      * @param url
      *            URL of the request to which the returned header will be added.
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     @Override
     public String getCookieHeaderForURL(
             CollectionProperty cookiesCP,
             URL url,
             boolean allowVariableCookie) {
         org.apache.commons.httpclient.Cookie[] c = 
                 getCookiesForUrl(cookiesCP, url, allowVariableCookie);
         int count = c.length;
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Found "+count+" cookies for "+url.toExternalForm());
         }
         if (count <=0){
             return null;
         }
         String hdr=cookieSpec.formatCookieHeader(c).getValue();
         if (debugEnabled){
             log.debug("Cookie: "+hdr);
         }
         return hdr;
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void addCookieFromHeader(CookieManager cookieManager,
             boolean checkCookies,String cookieHeader, URL url){
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled) {
             log.debug("Received Cookie: " + cookieHeader + " From: " + url.toExternalForm());
         }
         String protocol = url.getProtocol();
         String host = url.getHost();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean isSecure=HTTPSamplerBase.isSecure(protocol);
         org.apache.commons.httpclient.Cookie[] cookies= null;
         try {
             cookies = cookieSpec.parse(host, port, path, isSecure, cookieHeader);
         } catch (MalformedCookieException e) {
             log.warn(cookieHeader+e.getLocalizedMessage());
         } catch (IllegalArgumentException e) {
             log.warn(cookieHeader+e.getLocalizedMessage());
         }
         if (cookies == null) {
             return;
         }
         for(org.apache.commons.httpclient.Cookie cookie : cookies){
             try {
                 if (checkCookies) {
                     cookieSpec.validate(host, port, path, isSecure, cookie);
                 }
                 Date expiryDate = cookie.getExpiryDate();
                 long exp = 0;
                 if (expiryDate!= null) {
                     exp=expiryDate.getTime();
                 }
                 Cookie newCookie = new Cookie(
                         cookie.getName(),
                         cookie.getValue(),
                         cookie.getDomain(),
                         cookie.getPath(),
                         cookie.getSecure(),
                         exp / 1000,
                         cookie.isPathAttributeSpecified(),
                         cookie.isDomainAttributeSpecified()
                         );
 
                 // Store session cookies as well as unexpired ones
                 if (exp == 0 || exp >= System.currentTimeMillis()) {
                     newCookie.setVersion(cookie.getVersion());
                     cookieManager.add(newCookie); // Has its own debug log; removes matching cookies
                 } else {
                     cookieManager.removeMatchingCookies(newCookie);
                     if (debugEnabled){
                         log.debug("Dropping expired Cookie: "+newCookie.toString());
                     }
                 }
             } catch (MalformedCookieException e) { // This means the cookie was wrong for the URL
                 log.warn("Not storing invalid cookie: <"+cookieHeader+"> for URL "+url+" ("+e.getLocalizedMessage()+")");
             } catch (IllegalArgumentException e) {
                 log.warn(cookieHeader+e.getLocalizedMessage());
             }
         }
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
index dc0ca0bb9..f2dd869af 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC4CookieHandler.java
@@ -1,215 +1,215 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.apache.http.Header;
 import org.apache.http.client.params.CookiePolicy;
 import org.apache.http.cookie.CookieOrigin;
 import org.apache.http.cookie.CookieSpec;
 import org.apache.http.cookie.CookieSpecRegistry;
 import org.apache.http.cookie.MalformedCookieException;
 import org.apache.http.impl.cookie.BasicClientCookie;
 import org.apache.http.impl.cookie.BestMatchSpecFactory;
 import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
 import org.apache.http.impl.cookie.IgnoreSpecFactory;
 import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
 import org.apache.http.impl.cookie.RFC2109SpecFactory;
 import org.apache.http.impl.cookie.RFC2965SpecFactory;
 import org.apache.http.message.BasicHeader;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class HC4CookieHandler implements CookieHandler {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     private final transient CookieSpec cookieSpec;
     
     private static CookieSpecRegistry registry  = new CookieSpecRegistry();
 
     static {
         registry.register(CookiePolicy.BEST_MATCH, new BestMatchSpecFactory());
         registry.register(CookiePolicy.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory());
         registry.register(CookiePolicy.RFC_2109, new RFC2109SpecFactory());
         registry.register(CookiePolicy.RFC_2965, new RFC2965SpecFactory());
         registry.register(CookiePolicy.IGNORE_COOKIES, new IgnoreSpecFactory());
         registry.register(CookiePolicy.NETSCAPE, new NetscapeDraftSpecFactory());
     }
 
     public HC4CookieHandler(String policy) {
         super();
         if (policy.equals(org.apache.commons.httpclient.cookie.CookiePolicy.DEFAULT)) { // tweak diff HC3 vs HC4
             policy = CookiePolicy.BEST_MATCH;
         }
         this.cookieSpec = registry.getCookieSpec(policy);
     }
 
     @Override
     public void addCookieFromHeader(CookieManager cookieManager,
             boolean checkCookies, String cookieHeader, URL url) {
             boolean debugEnabled = log.isDebugEnabled();
             if (debugEnabled) {
                 log.debug("Received Cookie: " + cookieHeader + " From: " + url.toExternalForm());
             }
             String protocol = url.getProtocol();
             String host = url.getHost();
             int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
             String path = url.getPath();
             boolean isSecure=HTTPSamplerBase.isSecure(protocol);
 
             List<org.apache.http.cookie.Cookie> cookies = null;
             
             CookieOrigin cookieOrigin = new CookieOrigin(host, port, path, isSecure);
             BasicHeader basicHeader = new BasicHeader(HTTPConstants.HEADER_SET_COOKIE, cookieHeader);
 
             try {
                 cookies = cookieSpec.parse(basicHeader, cookieOrigin);
             } catch (MalformedCookieException e) {
                 log.error("Unable to add the cookie", e);
             }
             if (cookies == null) {
                 return;
             }
             for (org.apache.http.cookie.Cookie cookie : cookies) {
                 try {
                     if (checkCookies) {
                         cookieSpec.validate(cookie, cookieOrigin);
                     }
                     Date expiryDate = cookie.getExpiryDate();
                     long exp = 0;
                     if (expiryDate!= null) {
                         exp=expiryDate.getTime();
                     }
                     Cookie newCookie = new Cookie(
                             cookie.getName(),
                             cookie.getValue(),
                             cookie.getDomain(),
                             cookie.getPath(),
                             cookie.isSecure(),
                             exp / 1000
                             );
 
                     // Store session cookies as well as unexpired ones
                     if (exp == 0 || exp >= System.currentTimeMillis()) {
                         newCookie.setVersion(cookie.getVersion());
                         cookieManager.add(newCookie); // Has its own debug log; removes matching cookies
                     } else {
                         cookieManager.removeMatchingCookies(newCookie);
                         if (debugEnabled){
                             log.info("Dropping expired Cookie: "+newCookie.toString());
                         }
                     }
                 } catch (MalformedCookieException e) { // This means the cookie was wrong for the URL
                     log.warn("Not storing invalid cookie: <"+cookieHeader+"> for URL "+url+" ("+e.getLocalizedMessage()+")");
                 } catch (IllegalArgumentException e) {
                     log.warn(cookieHeader+e.getLocalizedMessage());
                 }
             }
     }
 
     @Override
     public String getCookieHeaderForURL(CollectionProperty cookiesCP, URL url,
             boolean allowVariableCookie) {
         List<org.apache.http.cookie.Cookie> c = 
                 getCookiesForUrl(cookiesCP, url, allowVariableCookie);
         
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Found "+c.size()+" cookies for "+url.toExternalForm());
         }
         if (c.size() <= 0) {
             return null;
         }
         List<Header> lstHdr = cookieSpec.formatCookies(c);
         
         StringBuilder sbHdr = new StringBuilder();
         for (Header header : lstHdr) {
             sbHdr.append(header.getValue());
         }
 
         return sbHdr.toString();
     }
 
     /**
      * Get array of valid HttpClient cookies for the URL
      *
      * @param cookiesCP property with all available cookies
      * @param url the target URL
      * @param allowVariableCookie flag whether cookies may contain jmeter variables
      * @return array of HttpClient cookies
      *
      */
     List<org.apache.http.cookie.Cookie> getCookiesForUrl(
             CollectionProperty cookiesCP, URL url, boolean allowVariableCookie) {
         List<org.apache.http.cookie.Cookie> cookies = new ArrayList<>();
 
-        for (PropertyIterator iter = cookiesCP.iterator(); iter.hasNext();) {
-            Cookie jmcookie = (Cookie) iter.next().getObjectValue();
+        for (JMeterProperty jMeterProperty : cookiesCP) {
+            Cookie jmcookie = (Cookie) jMeterProperty.getObjectValue();
             // Set to running version, to allow function evaluation for the cookie values (bug 28715)
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(true);
             }
             cookies.add(makeCookie(jmcookie));
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(false);
             }
         }
         String host = url.getHost();
         String protocol = url.getProtocol();
         int port = HTTPSamplerBase.getDefaultPort(protocol, url.getPort());
         String path = url.getPath();
         boolean secure = HTTPSamplerBase.isSecure(protocol);
 
         CookieOrigin cookieOrigin = new CookieOrigin(host, port, path, secure);
 
         List<org.apache.http.cookie.Cookie> cookiesValid = new ArrayList<>();
         for (org.apache.http.cookie.Cookie cookie : cookies) {
             if (cookieSpec.match(cookie, cookieOrigin)) {
                 cookiesValid.add(cookie);
             }
         }
 
         return cookiesValid;
     }
     
     /**
      * Create an HttpClient cookie from a JMeter cookie
      */
     private org.apache.http.cookie.Cookie makeCookie(Cookie jmc) {
         long exp = jmc.getExpiresMillis();
         BasicClientCookie ret = new BasicClientCookie(jmc.getName(),
                 jmc.getValue());
 
         ret.setDomain(jmc.getDomain());
         ret.setPath(jmc.getPath());
         ret.setExpiryDate(exp > 0 ? new Date(exp) : null); // use null for no expiry
         ret.setSecure(jmc.getSecure());
         ret.setVersion(jmc.getVersion());
         return ret;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
index e5b857220..367782f57 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
@@ -1,427 +1,426 @@
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
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.TableCellEditor;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieHandler;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HC3CookieHandler;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This is the GUI for Cookie Manager
  *
  * Allows the user to specify if she needs cookie services, and give parameters
  * for this service.
  *
  */
 public class CookiePanel extends AbstractConfigGui implements ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //++ Action command names
     private static final String ADD_COMMAND = "Add"; //$NON-NLS-1$
 
     private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
 
     private static final String LOAD_COMMAND = "Load"; //$NON-NLS-1$
 
     private static final String SAVE_COMMAND = "Save"; //$NON-NLS-1$
 
     private static final String HANDLER_COMMAND = "Handler"; // $NON-NLS-1$
     //--
 
     private JTable cookieTable;
 
     private PowerTableModel tableModel;
 
     private JCheckBox clearEachIteration;
 
     private JComboBox<String> selectHandlerPanel;
 
     private HashMap<String, String> handlerMap = new HashMap<>();
 
     private static final String[] COLUMN_RESOURCE_NAMES = {
         ("name"),   //$NON-NLS-1$
         ("value"),  //$NON-NLS-1$
         ("domain"), //$NON-NLS-1$
         ("path"),   //$NON-NLS-1$
         ("secure"), //$NON-NLS-1$
         // removed expiration because it's just an annoyance for static cookies
     };
 
     private static final Class<?>[] columnClasses = {
         String.class,
         String.class,
         String.class,
         String.class,
         Boolean.class, };
 
     private JButton addButton;
 
     private JButton deleteButton;
 
     private JButton loadButton;
 
     private JButton saveButton;
 
     /**
      * List of cookie policies.
      *
      * These are used both for the display, and for setting the policy
     */
     private final String[] policies = new String[] {
         "default",
         "compatibility",
         "rfc2109",
         "rfc2965",
         "ignorecookies",
         "netscape"
     };
 
     private JLabeledChoice policy;
 
     /**
      * Default constructor.
      */
     public CookiePanel() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "cookie_manager_title"; //$NON-NLS-1$
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
 
         if (action.equals(DELETE_COMMAND)) {
             if (tableModel.getRowCount() > 0) {
                 // If a table cell is being edited, we must cancel the editing
                 // before deleting the row.
                 if (cookieTable.isEditing()) {
                     TableCellEditor cellEditor = cookieTable.getCellEditor(cookieTable.getEditingRow(),
                             cookieTable.getEditingColumn());
                     cellEditor.cancelCellEditing();
                 }
 
                 int rowSelected = cookieTable.getSelectedRow();
 
                 if (rowSelected != -1) {
                     tableModel.removeRow(rowSelected);
                     tableModel.fireTableDataChanged();
 
                     // Disable the DELETE and SAVE buttons if no rows remaining
                     // after delete.
                     if (tableModel.getRowCount() == 0) {
                         deleteButton.setEnabled(false);
                         saveButton.setEnabled(false);
                     }
 
                     // Table still contains one or more rows, so highlight
                     // (select) the appropriate one.
                     else {
                         int rowToSelect = rowSelected;
 
                         if (rowSelected >= tableModel.getRowCount()) {
                             rowToSelect = rowSelected - 1;
                         }
 
                         cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                     }
                 }
             }
         } else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(cookieTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             // Enable the DELETE and SAVE buttons if they are currently
             // disabled.
             if (!deleteButton.isEnabled()) {
                 deleteButton.setEnabled(true);
             }
             if (!saveButton.isEnabled()) {
                 saveButton.setEnabled(true);
             }
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         } else if (action.equals(LOAD_COMMAND)) {
             try {
                 final String [] _txt={".txt"}; //$NON-NLS-1$
                 final JFileChooser chooser = FileDialoger.promptToOpenFile(_txt);
                 if (chooser != null) {
                     CookieManager manager = new CookieManager();
                     manager.addFile(chooser.getSelectedFile().getAbsolutePath());
                     for (int i = 0; i < manager.getCookieCount() ; i++){
                         addCookieToTable(manager.get(i));
                     }
                     tableModel.fireTableDataChanged();
 
                     if (tableModel.getRowCount() > 0) {
                         deleteButton.setEnabled(true);
                         saveButton.setEnabled(true);
                     }
                 }
             } catch (IOException ex) {
                 log.error("", ex);
             }
         } else if (action.equals(SAVE_COMMAND)) {
             try {
                 final JFileChooser chooser = FileDialoger.promptToSaveFile("cookies.txt"); //$NON-NLS-1$
                 if (chooser != null) {
                     ((CookieManager) createTestElement()).save(chooser.getSelectedFile().getAbsolutePath());
                 }
             } catch (IOException ex) {
                 JMeterUtils.reportErrorToUser(ex.getMessage(), "Error saving cookies");
             }
         }
     }
 
     private void addCookieToTable(Cookie cookie) {
         tableModel.addRow(new Object[] { cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
                 Boolean.valueOf(cookie.getSecure()) });
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement cm) {
         GuiUtils.stopTableEditing(cookieTable);
         cm.clear();
         configureTestElement(cm);
         if (cm instanceof CookieManager) {
             CookieManager cookieManager = (CookieManager) cm;
             for (int i = 0; i < tableModel.getRowCount(); i++) {
                 Cookie cookie = createCookie(tableModel.getRowData(i));
                 cookieManager.add(cookie);
             }
             cookieManager.setClearEachIteration(clearEachIteration.isSelected());
             cookieManager.setCookiePolicy(policy.getText());
             cookieManager.setImplementation(handlerMap.get(selectHandlerPanel.getSelectedItem()));
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         tableModel.clearData();
         clearEachIteration.setSelected(false);
         policy.setText(CookieManager.DEFAULT_POLICY);
         selectHandlerPanel.setSelectedItem(CookieManager.DEFAULT_IMPLEMENTATION
                 .substring(CookieManager.DEFAULT_IMPLEMENTATION.lastIndexOf('.') + 1));
         deleteButton.setEnabled(false);
         saveButton.setEnabled(false);
     }
 
     private Cookie createCookie(Object[] rowData) {
         Cookie cookie = new Cookie(
                 (String) rowData[0],
                 (String) rowData[1],
                 (String) rowData[2],
                 (String) rowData[3],
                 ((Boolean) rowData[4]).booleanValue(),
                 0); // Non-expiring
         return cookie;
     }
 
     private void populateTable(CookieManager manager) {
         tableModel.clearData();
-        PropertyIterator iter = manager.getCookies().iterator();
-        while (iter.hasNext()) {
-            addCookieToTable((Cookie) iter.next().getObjectValue());
+        for (JMeterProperty jMeterProperty : manager.getCookies()) {
+            addCookieToTable((Cookie) jMeterProperty.getObjectValue());
         }
     }
 
     @Override
     public TestElement createTestElement() {
         CookieManager cookieManager = new CookieManager();
         modifyTestElement(cookieManager);
         return cookieManager;
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
 
         CookieManager cookieManager = (CookieManager) el;
         populateTable(cookieManager);
         clearEachIteration.setSelected((cookieManager).getClearEachIteration());
         policy.setText(cookieManager.getPolicy());
         String fullImpl = cookieManager.getImplementation();
         selectHandlerPanel.setSelectedItem(fullImpl.substring(fullImpl.lastIndexOf('.') + 1));
     }
 
     /**
      * Shows the main cookie configuration panel.
      */
     private void init() {
         tableModel = new PowerTableModel(COLUMN_RESOURCE_NAMES, columnClasses);
         clearEachIteration = 
             new JCheckBox(JMeterUtils.getResString("clear_cookies_per_iter"), false); //$NON-NLS-1$
         policy = new JLabeledChoice(
                 JMeterUtils.getResString("cookie_manager_policy"), //$NON-NLS-1$
                 policies);
         policy.setText(CookieManager.DEFAULT_POLICY);
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         northPanel.add(makeTitlePanel());
         JPanel optionsPane = new JPanel();
         optionsPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("cookie_options"))); // $NON-NLS-1$
         optionsPane.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         optionsPane.add(clearEachIteration);
         JPanel policyTypePane = new JPanel();
         policyTypePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         policyTypePane.add(policy);
         policyTypePane.add(GuiUtils.createLabelCombo(
                 JMeterUtils.getResString("cookie_implementation_choose"), createComboHandler())); // $NON-NLS-1$
         optionsPane.add(policyTypePane);
         northPanel.add(optionsPane);
         add(northPanel, BorderLayout.NORTH);
         add(createCookieTablePanel(), BorderLayout.CENTER);
     }
 
     public JPanel createCookieTablePanel() {
         // create the JTable that holds one cookie per row
         cookieTable = new JTable(tableModel);
         cookieTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         cookieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         cookieTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         JPanel buttonPanel = createButtonPanel();
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("cookies_stored"))); //$NON-NLS-1$
 
         panel.add(new JScrollPane(cookieTable), BorderLayout.CENTER);
         panel.add(buttonPanel, BorderLayout.SOUTH);
         return panel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = (tableModel.getRowCount() == 0);
 
         addButton = createButton("add", 'A', ADD_COMMAND, true); //$NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); //$NON-NLS-1$
         loadButton = createButton("load", 'L', LOAD_COMMAND, true); //$NON-NLS-1$
         saveButton = createButton("save", 'S', SAVE_COMMAND, !tableEmpty); //$NON-NLS-1$
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton);
         buttonPanel.add(deleteButton);
         buttonPanel.add(loadButton);
         buttonPanel.add(saveButton);
         return buttonPanel;
     }
     
     /**
      * Create the drop-down list to changer render
      * @return List of all render (implement ResultsRender)
      */
     private JComboBox<String> createComboHandler() {
         ComboBoxModel<String> nodesModel = new DefaultComboBoxModel<>();
         // drop-down list for renderer
         selectHandlerPanel = new JComboBox<>(nodesModel);
         selectHandlerPanel.setActionCommand(HANDLER_COMMAND);
         selectHandlerPanel.addActionListener(this);
 
         // if no results render in jmeter.properties, load Standard (default)
         List<String> classesToAdd = Collections.<String>emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(CookieHandler.class);
         } catch (IOException e1) {
             // ignored
         }
         String tmpName = null;
         for (String clazz : classesToAdd) {
             String shortClazz = clazz.substring(clazz.lastIndexOf('.') + 1);
             if (HC3CookieHandler.class.getName().equals(clazz)) {
                 tmpName = shortClazz;
             }
             selectHandlerPanel.addItem(shortClazz);
             handlerMap.put(shortClazz, clazz);
         }
         nodesModel.setSelectedItem(tmpName); // preset to default impl
         return selectHandlerPanel;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/DNSCachePanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/DNSCachePanel.java
index ec2433cae..38331a31b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/DNSCachePanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/DNSCachePanel.java
@@ -1,329 +1,328 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with this
  * work for additional information regarding copyright ownership. The ASF
  * licenses this file to You under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations under
  * the License.
  */
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.ButtonGroup;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.TableCellEditor;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 
 /**
  * This gui part of @see
  * {@link org.apache.jmeter.protocol.http.control.DNSCacheManager}. Using
  * radiobuttons, user can switch between using system DNS resolver and custom
  * resolver. Custom resolver functionality is provided by dnsjava library.
  * "DNS servers" may contain one or more IP/Name of dns server for resolving
  * name DNS servers are chosen via round-robin. If table is empty - system
  * resolver is used.
  *
  * @since 2.12
  */
 public class DNSCachePanel extends AbstractConfigGui implements ActionListener {
 
     private static final long serialVersionUID = 2120L;
 
     public static final String OPTIONS = JMeterUtils.getResString("option");
 
     private static final String ADD_COMMAND = JMeterUtils.getResString("add"); // $NON-NLS-1$
 
     private static final String DELETE_COMMAND = JMeterUtils.getResString("delete"); // $NON-NLS-1$
 
     private static final String SYS_RES_COMMAND = JMeterUtils.getResString("use_system_dns_resolver"); // $NON-NLS-1$
 
     private static final String CUST_RES_COMMAND = JMeterUtils.getResString("use_custom_dns_resolver"); // $NON-NLS-1$
 
     private JTable dnsServersTable;
 
     private JPanel dnsServersPanel;
 
     private JPanel dnsServButPanel;
 
     private PowerTableModel dnsServersTableModel;
 
     private JRadioButton sysResButton;
 
     private JRadioButton custResButton;
 
     private JButton deleteButton;
 
     private JButton addButton;
 
     private ButtonGroup providerDNSradioGroup = new ButtonGroup();
 
     private static final String[] COLUMN_RESOURCE_NAMES = {
         (JMeterUtils.getResString("dns_hostname_or_ip")), //$NON-NLS-1$
     };
     private static final Class<?>[] columnClasses = {
         String.class };
 
     private JCheckBox clearEachIteration;
 
     /**
      * Default constructor.
      */
     public DNSCachePanel() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "dns_cache_manager_title";
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(org.apache.jmeter.testelement.TestElement)
      */
     @Override
     public void modifyTestElement(TestElement dnsRes) {
         GuiUtils.stopTableEditing(dnsServersTable);
         dnsRes.clear();
         configureTestElement(dnsRes);
         if (dnsRes instanceof DNSCacheManager) {
             DNSCacheManager dnsCacheManager = (DNSCacheManager) dnsRes;
             for (int i = 0; i < dnsServersTableModel.getRowCount(); i++) {
                 String server = (String) dnsServersTableModel.getRowData(i)[0];
                 dnsCacheManager.addServer(server);
             }
             dnsCacheManager.setClearEachIteration(clearEachIteration.isSelected());
             if (providerDNSradioGroup.isSelected(custResButton.getModel())) {
                 dnsCacheManager.setCustomResolver(true);
             } else {
                 dnsCacheManager.setCustomResolver(false);
             }
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         clearEachIteration.setSelected(DNSCacheManager.DEFAULT_CLEAR_CACHE_EACH_ITER);
         providerDNSradioGroup.setSelected(sysResButton.getModel(), true);
         dnsServersTableModel.clearData();
         deleteButton.setEnabled(false);
 
     }
 
     private void populateTable(DNSCacheManager resolver) {
         dnsServersTableModel.clearData();
-        PropertyIterator iter = resolver.getServers().iterator();
-        while (iter.hasNext()) {
-            addServerToTable((String) iter.next().getObjectValue());
+        for (JMeterProperty jMeterProperty : resolver.getServers()) {
+            addServerToTable((String) jMeterProperty.getObjectValue());
         }
     }
 
     @Override
     public TestElement createTestElement() {
         DNSCacheManager dnsCacheManager = new DNSCacheManager();
         modifyTestElement(dnsCacheManager);
         return dnsCacheManager;
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
 
         DNSCacheManager dnsCacheManager = (DNSCacheManager) el;
         populateTable(dnsCacheManager);
         clearEachIteration.setSelected(dnsCacheManager.isClearEachIteration());
         if (dnsCacheManager.isCustomResolver()) {
             providerDNSradioGroup.setSelected(custResButton.getModel(), true);
             deleteButton.setEnabled(dnsServersTable.getColumnCount() > 0);
             addButton.setEnabled(true);
         } else {
             providerDNSradioGroup.setSelected(sysResButton.getModel(), true);
         }
     }
 
     private void init() {
         dnsServersTableModel = new PowerTableModel(COLUMN_RESOURCE_NAMES, columnClasses);
 
         clearEachIteration = new JCheckBox(JMeterUtils.getResString("clear_cache_each_iteration"), true); //$NON-NLS-1$
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         northPanel.add(makeTitlePanel());
         JPanel optionsPane = new JPanel();
         optionsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), OPTIONS)); // $NON-NLS-1$
         optionsPane.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         optionsPane.add(clearEachIteration, BorderLayout.WEST);
         optionsPane.add(createChooseResPanel(), BorderLayout.SOUTH);
         northPanel.add(optionsPane);
         add(northPanel, BorderLayout.NORTH);
 
         dnsServersPanel = createDnsServersTablePanel();
         add(dnsServersPanel, BorderLayout.CENTER);
 
     }
 
     public JPanel createDnsServersTablePanel() {
         // create the JTable that holds header per row
         dnsServersTable = new JTable(dnsServersTableModel);
         dnsServersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         dnsServersTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("dns_servers"))); // $NON-NLS-1$
         JScrollPane dnsServScrollPane = new JScrollPane(dnsServersTable);
         panel.add(dnsServScrollPane, BorderLayout.CENTER);
         dnsServButPanel = createButtonPanel();
         panel.add(dnsServButPanel, BorderLayout.SOUTH);
         return panel;
     }
 
     private JPanel createChooseResPanel() {
         JPanel chooseResPanel = new JPanel(new BorderLayout(0, 5));
         sysResButton = new JRadioButton();
         sysResButton.setSelected(true);
         sysResButton.setText(SYS_RES_COMMAND);
         sysResButton.setToolTipText(SYS_RES_COMMAND);
         sysResButton.setEnabled(true);
         sysResButton.addActionListener(this);
 
         custResButton = new JRadioButton();
         custResButton.setSelected(false);
         custResButton.setText(CUST_RES_COMMAND);
         custResButton.setToolTipText(CUST_RES_COMMAND);
         custResButton.setEnabled(true);
         custResButton.addActionListener(this);
 
         providerDNSradioGroup.add(sysResButton);
         providerDNSradioGroup.add(custResButton);
 
         chooseResPanel.add(sysResButton, BorderLayout.WEST);
         chooseResPanel.add(custResButton, BorderLayout.CENTER);
         return chooseResPanel;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = (dnsServersTableModel.getRowCount() == 0);
 
         addButton = createButton("add", 'A', ADD_COMMAND, custResButton.isSelected()); // $NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); // $NON-NLS-1$
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton, BorderLayout.WEST);
         buttonPanel.add(deleteButton, BorderLayout.LINE_END);
         return buttonPanel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private void addServerToTable(String dnsServer) {
         dnsServersTableModel.addRow(new Object[] {
             dnsServer });
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
         dnsServersTable.setEnabled(custResButton.isSelected());
         Color greyColor = new Color(240, 240, 240);
         Color blueColor = new Color(184, 207, 229);
         dnsServersTable.setBackground(sysResButton.isSelected() ? greyColor : Color.WHITE);
         dnsServersTable.setSelectionBackground(sysResButton.isSelected() ? greyColor : blueColor);
         addButton.setEnabled(custResButton.isSelected());
         deleteButton.setEnabled(custResButton.isSelected());
         if (custResButton.isSelected() && (dnsServersTableModel.getRowCount() > 0)) {
             deleteButton.setEnabled(true);
             addButton.setEnabled(true);
         }
 
         if (action.equals(DELETE_COMMAND)) {
             if (dnsServersTableModel.getRowCount() > 0) {
                 // If a table cell is being edited, we must cancel the editing
                 // before deleting the row.
                 if (dnsServersTable.isEditing()) {
                     TableCellEditor cellEditor = dnsServersTable.getCellEditor(dnsServersTable.getEditingRow(),
                             dnsServersTable.getEditingColumn());
                     cellEditor.cancelCellEditing();
                 }
 
                 int rowSelected = dnsServersTable.getSelectedRow();
 
                 if (rowSelected != -1) {
                     dnsServersTableModel.removeRow(rowSelected);
                     dnsServersTableModel.fireTableDataChanged();
 
                     if (dnsServersTableModel.getRowCount() == 0) {
                         deleteButton.setEnabled(false);
                     }
 
                     else {
                         int rowToSelect = rowSelected;
 
                         if (rowSelected >= dnsServersTableModel.getRowCount()) {
                             rowToSelect = rowSelected - 1;
                         }
 
                         dnsServersTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                     }
                 }
             }
         } else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(dnsServersTable);
 
             dnsServersTableModel.addNewRow();
             dnsServersTableModel.fireTableDataChanged();
 
             if (!deleteButton.isEnabled()) {
                 deleteButton.setEnabled(true);
             }
 
             // Highlight (select) the appropriate row.
             int rowToSelect = dnsServersTableModel.getRowCount() - 1;
             dnsServersTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index 1a64b3f7b..fa52c9b79 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -325,1703 +325,1702 @@ public abstract class HTTPSamplerBase extends AbstractSampler
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
         
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Content body,
      * i.e. without any additional wrapping.
      *
      * @return true if specified file is to be sent as the body,
      * i.e. there is a single file entry which has a non-empty path and
      * an empty Parameter name.
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
             && (files[0].getPath().length() > 0)
             && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the entity body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if(getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 if(arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(HTTPConstants.POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0 ) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; if the request is a GET or DELETE (and the path
      * does not start with http[s]://) it also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         boolean fullUrl = path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX); 
         if (!fullUrl && (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod()))) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     /**
      * Sets the value of the encoding to be used for the content.
      * 
      * @param charsetName the name of the encoding to be used
      */
     public void setContentEncoding(String charsetName) {
         setProperty(CONTENT_ENCODING, charsetName);
     }
 
     /**
      * 
      * @return the encoding of the content, i.e. its charset name
      */
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
     }
 
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
    public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
      *
      * @param name name of the argument
      * @param value value of the argument
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      * 
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if(nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else if (el instanceof DNSCacheManager) {
             setDNSResolver((DNSCacheManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren(){
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)  ? HTTPConstants.DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS) ? HTTPConstants.DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         if (port == UNSPECIFIED_PORT ||
                 (HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT) ||
                 (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCookieManagerProperty(CookieManager value) {
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));        
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCookieManagerProperty(value);
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCacheManagerProperty(CacheManager value) {
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCacheManagerProperty(value);
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public DNSCacheManager getDNSResolver() {
         return (DNSCacheManager) getProperty(DNS_CACHE_MANAGER).getObjectValue();
     }
 
     public void setDNSResolver(DNSCacheManager cacheManager) {
         DNSCacheManager mgr = getDNSResolver();
         if (mgr != null) {
             log.warn("Existing DNSCacheManager " + mgr.getName() + " superseded by " + cacheManager.getName());
         }
         setProperty(new TestElementProperty(DNS_CACHE_MANAGER, cacheManager));
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE,"");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      * 
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel(res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER = 
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException if url is malformed
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain=null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")){ // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(String contentEncoding) {
          // Check if the sampler has a specified content encoding
          if(JOrphanUtils.isBlank(contentEncoding)) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(contentEncoding));
             }
             catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string; 
      *            if non-null then it is used to decode the 
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         final boolean isDebug = log.isDebugEnabled();
         for (String arg : args) {
             if (isDebug) {
                 log.debug("Arg: " + arg);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = arg.length();
             int endOfNameIndex = arg.indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = arg.substring(0, endOfNameIndex);
                 value = arg.substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name = arg;
                 value = "";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name + " Value: " + value + " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if (!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 } else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if(HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
      *            <code>Entry</code> to be sampled
      * @return results of the sampling
      */
     @Override
     public SampleResult sample(Entry e) {
         return sample();
     }
 
     /**
      * Perform a sample, and return the results
      *
      * @return results of the sampling
      */
     public SampleResult sample() {
         SampleResult res = null;
         try {
             res = sample(getUrl(), getMethod(), false, 0);
             if(res != null) {
                 res.setSampleLabel(getName());
             }
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param u
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param depth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling, can be null if u is in CacheManager
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      * 
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0){  // Bug 39205
                 String parserName = getParserClass(res);
                 if(parserName != null)
                 {
                     final HTMLParser parser =
                         parserName.length() > 0 ? // we have a name
                         HTMLParser.getParser(parserName)
                         :
                         HTMLParser.getParser(); // we don't; use the default parser
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re=getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
                 }
             }
             
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<>();
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc=escapeIllegalURLCharacters(encodeSpaces(urlstr));
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
                                 setParentSampleSuccess(res, false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         
                         if (isConcurrentDwn()) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             liste.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes != null ? binRes.isSuccessful() : true));
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                     continue;
                 }
             }
             // IF for download concurrent embedded resources
             if (isConcurrentDwn()) {
                 int poolSize = CONCURRENT_POOL_SIZE; // init with default value
                 try {
                     poolSize = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
                 final String parentThreadName = Thread.currentThread().getName();
                 // Thread pool Executor to get resources 
                 // use a LinkedBlockingQueue, note: max pool size doesn't effect
                 final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                         poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                         new LinkedBlockingQueue<Runnable>(),
                         new ThreadFactory() {
                             @Override
                             public Thread newThread(final Runnable r) {
                                 Thread t = new CleanerThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             r.run();
                                         } finally {
                                             ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
                                         }
                                     }
                                 });
                                 t.setName(parentThreadName+"-ResDownload-" + t.getName()); //$NON-NLS-1$
                                 t.setDaemon(true);
                                 return t;
                             }
                         });
 
                 boolean tasksCompleted = false;
                 try {
                     // sample all resources with threadpool
                     final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(liste);
                     // call normal shutdown (wait ending all tasks)
                     exec.shutdown();
                     // put a timeout if tasks couldn't terminate
                     exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
                         AsynSamplerResultHolder binRes;
                         try {
                             binRes = future.get(1, TimeUnit.MILLISECONDS);
                             if(cookieManager != null) {
                                 CollectionProperty cookies = binRes.getCookies();
-                                PropertyIterator iter = cookies.iterator();
-                                while (iter.hasNext()) {
-                                    Cookie cookie = (Cookie) iter.next().getObjectValue();
+                                for (JMeterProperty jMeterProperty : cookies) {
+                                    Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
                                     cookieManager.add(cookie) ;
                                 }
                             }
                             res.addSubResult(binRes.getResult());
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                         } catch (TimeoutException e) {
                             errorResult(e, res);
                         }
                     }
                     tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
                 } catch (InterruptedException ie) {
                     log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 } finally {
                     if (!tasksCompleted) {
                         exec.shutdownNow(); // kill any remaining tasks
                     }
                 }
             }
         }
         return res;
     }
     
     /**
      * @param url URL to escape
      * @return escaped url
      */
     private String escapeIllegalURLCharacters(String url) {
         if (url == null || url.toLowerCase().startsWith("file:")) {
             return url;
         }
         try {
             String escapedUrl = ConversionUtils.escapeIllegalURLCharacters(url);
             if (!escapedUrl.equals(url)) {
                 log.warn("Url '" + url + "' has been escaped to '" + escapedUrl
                         + "'. Please corect your webpage.");
             }
             return escapedUrl;
         } catch (Exception e1) {
             log.error("Error escaping URL:'"+url+"', message:"+e1.getMessage());
             return url;
         }
     }
 
     /**
      * Extract User-Agent header value
      * @param sampleResult HTTPSampleResult
      * @return User Agent part
      */
     private String getUserAgent(HTTPSampleResult sampleResult) {
         String res = sampleResult.getRequestHeaders();
         int index = res.indexOf(USER_AGENT);
         if(index >=0) {
             // see HTTPHC3Impl#getConnectionHeaders
             // see HTTPHC4Impl#getConnectionHeaders
             // see HTTPJavaImpl#getConnectionHeaders    
             //': ' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
             final String userAgentPrefix = USER_AGENT+": ";
             String userAgentHdr = res.substring(
                     index+userAgentPrefix.length(), 
                     res.indexOf('\n',// '\n' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
                             index+userAgentPrefix.length()+1));
             return userAgentHdr.trim();
         } else {
             if(log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:"+res);
             }
             return null;
         }
     }
 
     /**
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
         if(!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
             if(!initialValue) {
                 res.setResponseMessage("Embedded resource download error"); //$NON-NLS-1$
             }
         }
     }
 
     /*
      * @param res HTTPSampleResult to check
      * @return parser class name (may be "") or null if entry does not exist
      */
     private String getParserClass(HTTPSampleResult res) {
         final String ct = res.getMediaType();
         return parsersForType.get(ct);
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
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
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         return base;
     }
 
     /**
      * Iteratively download the redirect targets of a redirect response.
      * <p>
      * The returned result will contain one subsample for each request issued,
      * including the original one that was passed in. It will be an
      * HTTPSampleResult that should mostly look as if the final destination of
      * the redirect chain had been obtained in a single shot.
      *
      * @param res
      *            result of the initial request - must be a redirect response
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult followRedirects(HTTPSampleResult res, int frameDepth) {
         HTTPSampleResult totalRes = new HTTPSampleResult(res);
         totalRes.addRawSubResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             String location = lastRes.getRedirectLocation(); 
             if (log.isDebugEnabled()) {
                 log.debug("Initial location: " + location);
             }
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             location = encodeSpaces(location);
             if (log.isDebugEnabled()) {
                 log.debug("Location after /. and space transforms: " + location);
             }
             // Change all but HEAD into GET (Bug 55450)
             String method = lastRes.getHTTPMethod();
             if (!HTTPConstants.HEAD.equalsIgnoreCase(method)) {
                 method = HTTPConstants.GET;
             }
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 HTTPSampleResult tempRes = sample(url, method, true, frameDepth);
                 if(tempRes != null) {
                     lastRes = tempRes;
                 } else {
                     // Last url was in cache so tempRes is null
                     break;
                 }
             } catch (MalformedURLException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             } catch (URISyntaxException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (SampleResult sub : subs) {
                     totalRes.addSubResult(sub);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if(!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), new HTTPSampleResult(lastRes));
             totalRes.addSubResult(lastRes);
         }
 
         // Now populate the any totalRes fields that need to
         // come from lastRes:
         totalRes.setSampleLabel(totalRes.getSampleLabel() + "->" + lastRes.getSampleLabel());
         // The following three can be discussed: should they be from the
         // first request or from the final one? I chose to do it this way
         // because that's what browsers do: they show the final URL of the
         // redirect chain in the location field.
         totalRes.setURL(lastRes.getURL());
         totalRes.setHTTPMethod(lastRes.getHTTPMethod());
         totalRes.setQueryString(lastRes.getQueryString());
         totalRes.setRequestHeaders(lastRes.getRequestHeaders());
 
         totalRes.setResponseData(lastRes.getResponseData());
         totalRes.setResponseCode(lastRes.getResponseCode());
         totalRes.setSuccessful(lastRes.isSuccessful());
         totalRes.setResponseMessage(lastRes.getResponseMessage());
         totalRes.setDataType(lastRes.getDataType());
         totalRes.setResponseHeaders(lastRes.getResponseHeaders());
         totalRes.setContentType(lastRes.getContentType());
         totalRes.setDataEncoding(lastRes.getDataEncodingNoDefault());
         return totalRes;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect flag whether we are getting a redirect target
      * @param frameDepth Depth of this target in the frame structure. Used only to prevent infinite recursion.
      * @param res sample result to process
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect) {
             if (res.isRedirect()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
 
                 if (getFollowRedirects()) {
                     res = followRedirects(res, frameDepth);
                     areFollowingRedirect = true;
                     wasRedirected = true;
                 }
             }
         }
         if (isImageParser() && (SampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), new HTTPSampleResult(res)));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 // otherwise, use null so the container is created if necessary unless
                 // the flag is false, in which case revert to broken 2.1 behaviour 
                 // Bug 51939 -  https://bz.apache.org/bugzilla/show_bug.cgi?id=51939
                 if(!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (
                             areFollowingRedirect ? res.getParent() : SEPARATE_CONTAINER ? null : res);
                     res = downloadPageResources(res, container, frameDepth);
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @param code status code to check
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code){
         return (code >= 200 && code <= 399);
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuilder newValue = new StringBuilder();
         for (int i = 0; i < value.length(); i++) {
             char charAt = value.charAt(i);
             if (charAt == '\\') { // $NON-NLS-1$
                 newValue.append("\\\\"); // $NON-NLS-1$
             } else {
                 newValue.append(charAt);
             }
         }
         return newValue.toString();
     }
 
     /*
      * Method to set files list to be uploaded.
      *
      * @param value
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void setHTTPFileArgs(HTTPFileArgs value) {
         if (value.getHTTPFileArgCount() > 0){
             setProperty(new TestElementProperty(FILE_ARGS, value));
         } else {
             removeProperty(FILE_ARGS); // no point saving an empty list
         }
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
     }
 
     /**
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      *
      * If there are no valid file entries, then an empty list is returned.
      *
      * @return an array of file arguments (never null)
      */
     public HTTPFileArg[] getHTTPFiles() {
         final HTTPFileArgs fileArgs = getHTTPFileArgs();
         return fileArgs == null ? new HTTPFileArg[] {} : fileArgs.asArray();
     }
 
     public int getHTTPFileCount(){
         return getHTTPFiles().length;
     }
     /**
      * Saves the list of files.
      * The first file is saved in the Filename/field/mimetype properties.
      * Any additional files are saved in the FILE_ARGS array.
      *
      * @param files list of files to save
      */
     public void setHTTPFiles(HTTPFileArg[] files) {
         HTTPFileArgs fileArgs = new HTTPFileArgs();
         // Weed out the empty files
         if (files.length > 0) {
             for (HTTPFileArg file : files) {
                 if (file.isNotEmpty()) {
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray(){
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol){
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url){
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted(){
     }
 
     @Override
     public void threadFinished(){
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         // NOOP to provide based empty impl and avoid breaking existing implementations
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the original response.
      * <p>
      * Closes the inputStream 
      * 
      * @param sampleResult sample to store information about the response into
      * @param in input stream from which to read the response
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException if reading the result fails
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
         try {
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize=32;// Enough for MD5
     
             MessageDigest md=null;
             boolean asMD5 = useMD5();
             if (asMD5) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
                     asMD5=false;
                 }
             } else {
                 if (length <= 0) {// may also happen if long value > int.max
                     bufferSize = 4 * 1024;
                 } else {
                     bufferSize = length;
                 }
             }
             ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
             int bytesRead = 0;
             int totalBytes = 0;
             boolean first = true;
             while ((bytesRead = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
                 }
                 if (asMD5 && md != null) {
                     md.update(readBuffer, 0 , bytesRead);
                     totalBytes += bytesRead;
                 } else {
                     w.write(readBuffer, 0, bytesRead);
                 }
             }
             if (first){ // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
             }
             in.close();
             w.flush();
             if (asMD5 && md != null) {
                 byte[] md5Result = md.digest();
                 w.write(JOrphanUtils.baToHexBytes(md5Result)); 
                 sampleResult.setBytes(totalBytes);
             }
             w.close();
             return w.toByteArray();
         } finally {
             IOUtils.closeQuietly(in);
         }
     }
 
     /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
      * <ul>
      *   <li>FILE_NAME</li>
      *   <li>FILE_FIELD</li>
      *   <li>MIMETYPE</li>
      * </ul>
      * These were stored in their own individual properties.
      * <p>
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      * <p>
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      * <p>
      * Versions after 2.3.4 dispense with the original set of 3 properties.
      * Test plans that use them are converted to use a single HTTPFileArgs list.
      *
      * @see HTTPSamplerBaseConverter
      */
     void mergeFileProperties() {
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg oldStyleFile = new HTTPFileArg(fileName, paramName, mimeType);
 
         HTTPFileArgs fileArgs = getHTTPFileArgs();
 
         HTTPFileArgs allFileArgs = new HTTPFileArgs();
         if(oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if(fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (HTTPFileArg infile : infiles) {
                     allFileArgs.addHTTPFileArg(infile);
                 }
             }
         } else {
             if(fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
                 allFileArgs = fileArgs;
             }
         }
         // Updated the property lists
         setHTTPFileArgs(allFileArgs);
         removeProperty(FILE_FIELD);
         removeProperty(FILE_NAME);
         removeProperty(MIMETYPE);
     }
 
     /**
      * set IP source to use - does not apply to Java HTTP implementation currently
      *
      * @param value IP source to use
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      *
      * @return IP source to use
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE,"");
     }
  
     /**
      * set IP/address source type to use
      *
      * @param value type of the IP/address source
      */
     public void setIpSourceType(int value) {
         setProperty(IP_SOURCE_TYPE, value, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * get IP/address source type to use
      * 
      * @return address source type
      */
     public int getIpSourceType() {
         return getPropertyAsInt(IP_SOURCE_TYPE, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * Return if used a concurrent thread pool to get embedded resources.
      *
      * @return true if used
      */
     public boolean isConcurrentDwn() {
         return getPropertyAsBoolean(CONCURRENT_DWN, false);
     }
 
     public void setConcurrentDwn(boolean concurrentDwn) {
         setProperty(CONCURRENT_DWN, concurrentDwn, false);
     }
 
     /**
      * Get the pool size for concurrent thread pool to get embedded resources.
      *
      * @return the pool size
      */
     public String getConcurrentPool() {
         return getPropertyAsString(CONCURRENT_POOL,CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
     
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         final private URL url;
         final private String method;
         final private boolean areFollowingRedirect;
         final private int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base){
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
                 this.sampler.setCacheManagerProperty(cacheManager);
             }
             
             if(cookieManager != null) {
                 CookieManager clonedCookieManager = (CookieManager) cookieManager.clone();
                 this.sampler.setCookieManagerProperty(clonedCookieManager);
             } 
             this.jmeterContextOfParentThread = JMeterContextService.getContext();
         }
 
         @Override
         public AsynSamplerResultHolder call() {
             JMeterContextService.replaceContext(jmeterContextOfParentThread);
             ((CleanerThread) Thread.currentThread()).registerSamplerForEndNotification(sampler);
             HTTPSampleResult httpSampleResult = sampler.sample(url, method, areFollowingRedirect, depth);
             if(sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
         }
     }
     
     /**
      * Custom thread implementation that 
      *
      */
     private static class CleanerThread extends Thread {
         private final List<HTTPSamplerBase> samplersToNotify = new ArrayList<>();
         /**
          * @param runnable Runnable
          */
         public CleanerThread(Runnable runnable) {
            super(runnable);
         }
         
         /**
          * Notify of thread end
          */
         public void notifyThreadEnd() {
             for (HTTPSamplerBase samplerBase : samplersToNotify) {
                 samplerBase.threadFinished();
             }
             samplersToNotify.clear();
         }
 
         /**
          * Register sampler to be notify at end of thread
          * @param sampler {@link HTTPSamplerBase}
          */
         public void registerSamplerForEndNotification(HTTPSamplerBase sampler) {
             this.samplersToNotify.add(sampler);
         }
     }
     
     /**
      * Holder of AsynSampler result
      */
     private static class AsynSamplerResultHolder {
         private final HTTPSampleResult result;
         private final CollectionProperty cookies;
         /**
          * @param result {@link HTTPSampleResult} to hold
          * @param cookies cookies to hold
          */
         public AsynSamplerResultHolder(HTTPSampleResult result, CollectionProperty cookies) {
             super();
             this.result = result;
             this.cookies = cookies;
         }
         /**
          * @return the result
          */
         public HTTPSampleResult getResult() {
             return result;
         }
         /**
          * @return the cookies
          */
         public CollectionProperty getCookies() {
             return cookies;
         }
     }
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
index c5fce41ae..2627570de 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
@@ -1,268 +1,267 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.Serializable;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.testelement.property.BooleanProperty;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 //For unit tests, @see TestHTTPArgument
 
 /*
  *
  * Represents an Argument for HTTP requests.
  */
 public class HTTPArgument extends Argument implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String ALWAYS_ENCODE = "HTTPArgument.always_encode";
 
     private static final String USE_EQUALS = "HTTPArgument.use_equals";
 
     private static final EncoderCache cache = new EncoderCache(1000);
 
     /**
      * Constructor for the Argument object.
      * <p>
      * The value is assumed to be not encoded.
      *
      * @param name
      *            name of the paramter
      * @param value
      *            value of the parameter
      * @param metadata
      *            the separator to use between name and value
      */
     public HTTPArgument(String name, String value, String metadata) {
         this(name, value, false);
         this.setMetaData(metadata);
     }
 
     public void setUseEquals(boolean ue) {
         if (ue) {
             setMetaData("=");
         } else {
             setMetaData("");
         }
         setProperty(new BooleanProperty(USE_EQUALS, ue));
     }
 
     public boolean isUseEquals() {
         boolean eq = getPropertyAsBoolean(USE_EQUALS);
         if (getMetaData().equals("=") || (getValue() != null && getValue().length() > 0)) {
             setUseEquals(true);
             return true;
         }
         return eq;
 
     }
 
     public void setAlwaysEncoded(boolean ae) {
         setProperty(new BooleanProperty(ALWAYS_ENCODE, ae));
     }
 
     public boolean isAlwaysEncoded() {
         return getPropertyAsBoolean(ALWAYS_ENCODE);
     }
 
     /**
      * Constructor for the Argument object.
      * <p>
      * The value is assumed to be not encoded.
      *
      * @param name
      *            name of the parameter
      * @param value
      *            value of the parameter
      */
     public HTTPArgument(String name, String value) {
         this(name, value, false);
     }
 
     /**
      * @param name
      *            name of the parameter
      * @param value
      *            value of the parameter
      * @param alreadyEncoded
      *            <code>true</code> if the value is already encoded, in which
      *            case they are decoded before storage
      */
     public HTTPArgument(String name, String value, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance; alwaysEncoded is set to true.
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param alreadyEncoded true if the name and value is already encoded, in which case they are decoded before storage.
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, boolean alreadyEncoded, String contentEncoding) {
         setAlwaysEncoded(true);
         if (alreadyEncoded) {
             try {
                 // We assume the name is always encoded according to spec
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding name, calling URLDecoder.decode with '"+name+"' and contentEncoding:"+EncoderCache.URL_ARGUMENT_ENCODING);
                 }
                 name = URLDecoder.decode(name, EncoderCache.URL_ARGUMENT_ENCODING);
                 // The value is encoded in the specified encoding
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding value, calling URLDecoder.decode with '"+value+"' and contentEncoding:"+contentEncoding);
                 }
                 value = URLDecoder.decode(value, contentEncoding);
             } catch (UnsupportedEncodingException e) {
                 log.error(contentEncoding + " encoding not supported!");
                 throw new Error(e.toString(), e);
             }
         }
         setName(name);
         setValue(value);
         setMetaData("=");
     }
 
     /**
      * Construct a new HTTPArgument instance
      *
      * @param name
      *            the name of the parameter
      * @param value
      *            the value of the parameter
      * @param metaData
      *            the separator to use between name and value
      * @param alreadyEncoded
      *            true if the name and value is already encoded
      */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, metaData, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param metaData the separator to use between name and value
      * @param alreadyEncoded true if the name and value is already encoded
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded, String contentEncoding) {
         this(name, value, alreadyEncoded, contentEncoding);
         setMetaData(metaData);
     }
 
     public HTTPArgument(Argument arg) {
         this(arg.getName(), arg.getValue(), arg.getMetaData());
     }
 
     /**
      * Constructor for the Argument object
      */
     public HTTPArgument() {
     }
 
     /**
      * Sets the Name attribute of the Argument object.
      *
      * @param newName
      *            the new Name value
      */
     @Override
     public void setName(String newName) {
         if (newName == null || !newName.equals(getName())) {
             super.setName(newName);
         }
     }
 
     /**
      * Get the argument value encoded using UTF-8
      *
      * @return the argument value encoded in UTF-8
      */
     public String getEncodedValue() {
         // Encode according to the HTTP spec, i.e. UTF-8
         try {
             return getEncodedValue(EncoderCache.URL_ARGUMENT_ENCODING);
         } catch (UnsupportedEncodingException e) {
             // This can't happen (how should utf8 not be supported!?!),
             // so just throw an Error:
             throw new Error("Should not happen: " + e.toString());
         }
     }
 
     /**
      * Get the argument value encoded in the specified encoding
      *
      * @param contentEncoding the encoding to use when encoding the argument value
      * @return the argument value encoded in the specified encoding
      * @throws UnsupportedEncodingException of the encoding is not supported
      */
     public String getEncodedValue(String contentEncoding) throws UnsupportedEncodingException {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getValue(), contentEncoding);
         } else {
             return getValue();
         }
     }
 
     public String getEncodedName() {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getName());
         } else {
             return getName();
         }
 
     }
 
     /**
      * Converts all {@link Argument} entries in the collection to {@link HTTPArgument} entries.
      * 
      * @param args collection of {@link Argument} and/or {@link HTTPArgument} entries
      */
     public static void convertArgumentsToHTTP(Arguments args) {
         List<Argument> newArguments = new LinkedList<>();
-        PropertyIterator iter = args.getArguments().iterator();
-        while (iter.hasNext()) {
-            Argument arg = (Argument) iter.next().getObjectValue();
+        for (JMeterProperty jMeterProperty : args.getArguments()) {
+            Argument arg = (Argument) jMeterProperty.getObjectValue();
             if (!(arg instanceof HTTPArgument)) {
                 newArguments.add(new HTTPArgument(arg));
             } else {
                 newArguments.add(arg);
             }
         }
         args.removeAllArguments();
         args.setArguments(newArguments);
     }
 }
diff --git a/src/protocol/java/org/apache/jmeter/protocol/java/config/gui/JavaConfigGui.java b/src/protocol/java/org/apache/jmeter/protocol/java/config/gui/JavaConfigGui.java
index 6889efe32..3c2618cca 100644
--- a/src/protocol/java/org/apache/jmeter/protocol/java/config/gui/JavaConfigGui.java
+++ b/src/protocol/java/org/apache/jmeter/protocol/java/config/gui/JavaConfigGui.java
@@ -1,281 +1,280 @@
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
 
 package org.apache.jmeter.protocol.java.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 
 import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.protocol.java.config.JavaConfig;
 import org.apache.jmeter.protocol.java.sampler.JavaSampler;
 import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.log.Logger;
 
 /**
  * The <code>JavaConfigGui</code> class provides the user interface for the
  * {@link JavaConfig} object.
  *
  */
 public class JavaConfigGui extends AbstractConfigGui implements ActionListener {
     private static final long serialVersionUID = 240L;
 
     /** Logging */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** A combo box allowing the user to choose a test class. */
     private JComboBox<String> classnameCombo;
 
     /**
      * Indicates whether or not the name of this component should be displayed
      * as part of the GUI. If true, this is a standalone component. If false, it
      * is embedded in some other component.
      */
     private boolean displayName = true;
 
     /** A panel allowing the user to set arguments for this test. */
     private ArgumentsPanel argsPanel;
 
     /**
      * Create a new JavaConfigGui as a standalone component.
      */
     public JavaConfigGui() {
         this(true);
     }
 
     /**
      * Create a new JavaConfigGui as either a standalone or an embedded
      * component.
      *
      * @param displayNameField
      *            tells whether the component name should be displayed with the
      *            GUI. If true, this is a standalone component. If false, this
      *            component is embedded in some other component.
      */
     public JavaConfigGui(boolean displayNameField) {
         this.displayName = displayNameField;
         init();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getLabelResource() {
         return "java_request_defaults"; // $NON-NLS-1$
     }
 
     /**
      * Initialize the GUI components and layout.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(0, 5));
 
         if (displayName) {
             setBorder(makeBorder());
             add(makeTitlePanel(), BorderLayout.NORTH);
         }
 
         JPanel classnameRequestPanel = new JPanel(new BorderLayout(0, 5));
         classnameRequestPanel.add(createClassnamePanel(), BorderLayout.NORTH);
         classnameRequestPanel.add(createParameterPanel(), BorderLayout.CENTER);
 
         add(classnameRequestPanel, BorderLayout.CENTER);
     }
 
     /**
      * Create a panel with GUI components allowing the user to select a test
      * class.
      *
      * @return a panel containing the relevant components
      */
     private JPanel createClassnamePanel() {
         List<String> possibleClasses = new ArrayList<>();
 
         try {
             // Find all the classes which implement the JavaSamplerClient
             // interface.
             possibleClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(),
                     new Class[] { JavaSamplerClient.class });
 
             // Remove the JavaConfig class from the list since it only
             // implements the interface for error conditions.
 
             possibleClasses.remove(JavaSampler.class.getName() + "$ErrorSamplerClient");
         } catch (Exception e) {
             log.debug("Exception getting interfaces.", e);
         }
 
         JLabel label = new JLabel(JMeterUtils.getResString("protocol_java_classname")); // $NON-NLS-1$
 
         classnameCombo = new JComboBox<>(possibleClasses.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
         classnameCombo.addActionListener(this);
         classnameCombo.setEditable(false);
         label.setLabelFor(classnameCombo);
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.add(label);
         panel.add(classnameCombo);
 
         return panel;
     }
 
     /**
      * Handle action events for this component. This method currently handles
      * events for the classname combo box.
      *
      * @param evt
      *            the ActionEvent to be handled
      */
     @Override
     public void actionPerformed(ActionEvent evt) {
         if (evt.getSource() == classnameCombo) {
             String className = ((String) classnameCombo.getSelectedItem()).trim();
             try {
                 JavaSamplerClient client = (JavaSamplerClient) Class.forName(className, true,
                         Thread.currentThread().getContextClassLoader()).newInstance();
 
                 Arguments currArgs = new Arguments();
                 argsPanel.modifyTestElement(currArgs);
                 Map<String, String> currArgsMap = currArgs.getArgumentsAsMap();
 
                 Arguments newArgs = new Arguments();
                 Arguments testParams = null;
                 try {
                     testParams = client.getDefaultParameters();
                 } catch (AbstractMethodError e) {
                     log.warn("JavaSamplerClient doesn't implement "
                             + "getDefaultParameters.  Default parameters won't "
                             + "be shown.  Please update your client class: " + className);
                 }
 
                 if (testParams != null) {
-                    PropertyIterator i = testParams.getArguments().iterator();
-                    while (i.hasNext()) {
-                        Argument arg = (Argument) i.next().getObjectValue();
+                    for (JMeterProperty jMeterProperty : testParams.getArguments()) {
+                        Argument arg = (Argument) jMeterProperty.getObjectValue();
                         String name = arg.getName();
                         String value = arg.getValue();
 
                         // If a user has set parameters in one test, and then
                         // selects a different test which supports the same
                         // parameters, those parameters should have the same
                         // values that they did in the original test.
                         if (currArgsMap.containsKey(name)) {
                             String newVal = currArgsMap.get(name);
                             if (newVal != null && newVal.length() > 0) {
                                 value = newVal;
                             }
                         }
                         newArgs.addArgument(name, value);
                     }
                 }
 
                 argsPanel.configure(newArgs);
             } catch (Exception e) {
                 log.error("Error getting argument list for " + className, e);
             }
         }
     }
 
     /**
      * Create a panel containing components allowing the user to provide
      * arguments to be passed to the test class instance.
      *
      * @return a panel containing the relevant components
      */
     private JPanel createParameterPanel() {
         argsPanel = new ArgumentsPanel(JMeterUtils.getResString("paramtable")); // $NON-NLS-1$
         return argsPanel;
     }
 
     /** {@inheritDoc} */
     @Override
     public void configure(TestElement config) {
         super.configure(config);
 
         argsPanel.configure((Arguments) config.getProperty(JavaSampler.ARGUMENTS).getObjectValue());
 
         String className = config.getPropertyAsString(JavaSampler.CLASSNAME);
         if(checkContainsClassName(classnameCombo.getModel(), className)) {
             classnameCombo.setSelectedItem(className);
         } else {
             log.error("Error setting class:'"+className+"' in JavaSampler "+getName()+", check for a missing jar in your jmeter 'search_paths' and 'plugin_dependency_paths' properties");
         }
     }
 
     /**
      * Check combo contains className
      * @param model ComboBoxModel
      * @param className String class name
      * @return boolean
      */
     private static final boolean checkContainsClassName(ComboBoxModel<String> model, String className) {
         int size = model.getSize();
         Set<String> set = new HashSet<>(size);
         for (int i = 0; i < size; i++) {
             set.add(model.getElementAt(i));
         }
         return set.contains(className);
     }
 
     /** {@inheritDoc} */
     @Override
     public TestElement createTestElement() {
         JavaConfig config = new JavaConfig();
         modifyTestElement(config);
         return config;
     }
 
     /** {@inheritDoc} */
     @Override
     public void modifyTestElement(TestElement config) {
         configureTestElement(config);
         ((JavaConfig) config).setArguments((Arguments) argsPanel.createTestElement());
         ((JavaConfig) config).setClassname(String.valueOf(classnameCombo.getSelectedItem()));
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#clearGui()
      */
     @Override
     public void clearGui() {
         super.clearGui();
         this.displayName = true;
         argsPanel.clearGui();
         classnameCombo.setSelectedIndex(0);
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSProperties.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSProperties.java
index 5ae8c0910..7bca77af7 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSProperties.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSProperties.java
@@ -1,248 +1,247 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
+import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 
 /**
  * A set of JMSProperty objects.
  * @since 2.11
  */
 public class JMSProperties extends AbstractTestElement implements Serializable {
 
     /**
      * 
      */
     private static final long serialVersionUID = -2896138201054314563L;
     /** The name of the property used to store the JmsProperties. */
     public static final String JMS_PROPERTIES = "JMSProperties.properties"; //$NON-NLS-1$
 
     /**
      * Create a new JmsPropertys object with no JmsProperties
      */
     public JMSProperties() {
         setProperty(new CollectionProperty(JMS_PROPERTIES, new ArrayList<JMSProperty>()));
     }
 
     /**
      * Get the JmsPropertiess.
      *
      * @return the JmsProperties
      */
     public CollectionProperty getProperties() {
         return (CollectionProperty) getProperty(JMS_PROPERTIES);
     }
 
     /**
      * Clear the JmsProperties.
      */
     @Override
     public void clear() {
         super.clear();
         setProperty(new CollectionProperty(JMS_PROPERTIES, new ArrayList<JMSProperty>()));
     }
 
     /**
      * Set the list of JmsProperties. Any existing JmsProperties will be lost.
      *
      * @param jmsProperties
      *            the new JmsProperties
      */
     public void setProperties(List<JMSProperty> jmsProperties) {
         setProperty(new CollectionProperty(JMS_PROPERTIES, jmsProperties));
     }
 
     /**
      * Get the JmsProperties as a Map. Each JMSProperty name is used as the key, and
      * its value as the value.
      *
      * @return a new Map with String keys and values containing the JmsProperties
      */
     public Map<String, Object> getJmsPropertysAsMap() {
-        PropertyIterator iter = getProperties().iterator();
         Map<String, Object> argMap = new LinkedHashMap<>();
-        while (iter.hasNext()) {
-            JMSProperty arg = (JMSProperty) iter.next().getObjectValue();
+        for (JMeterProperty jMeterProperty : getProperties()) {
+            JMSProperty arg = (JMSProperty) jMeterProperty.getObjectValue();
             // Because CollectionProperty.mergeIn will not prevent adding two
             // properties of the same name, we need to select the first value so
             // that this element's values prevail over defaults provided by
-            // configuration
-            // elements:
+            // configuration elements:
             if (!argMap.containsKey(arg.getName())) {
                 argMap.put(arg.getName(), arg.getValueAsObject());
             }
         }
         return argMap;
     }
 
     /**
      * Add a new JMSProperty with the given name and value.
      *
      * @param name
      *            the name of the JMSProperty
      * @param value
      *            the value of the JMSProperty
      */
     public void addJmsProperty(String name, String value) {
         addJmsProperty(new JMSProperty(name, value));
     }
 
     /**
      * Add a new argument.
      *
      * @param arg
      *            the new argument
      */
     public void addJmsProperty(JMSProperty arg) {
         TestElementProperty newArg = new TestElementProperty(arg.getName(), arg);
         if (isRunningVersion()) {
             this.setTemporary(newArg);
         }
         getProperties().addItem(newArg);
     }
 
     /**
      * Add a new argument with the given name, value, and metadata.
      *
      * @param name
      *            the name of the argument
      * @param value
      *            the value of the argument
      * @param type
      *            the type for the argument
      */
     public void addJmsProperty(String name, String value, String type) {
         addJmsProperty(new JMSProperty(name, value, type));
     }
 
     /**
      * Get a PropertyIterator of the JmsProperties.
      *
      * @return an iteration of the JmsProperties
      */
     public PropertyIterator iterator() {
         return getProperties().iterator();
     }
 
     /**
      * Create a string representation of the JmsProperties.
      *
      * @return the string representation of the JmsProperties
      */
     @Override
     public String toString() {
         StringBuilder str = new StringBuilder();
         PropertyIterator iter = getProperties().iterator();
         while (iter.hasNext()) {
             JMSProperty arg = (JMSProperty) iter.next().getObjectValue();
             str.append(arg.toString());
             if (iter.hasNext()) {
                 str.append(","); //$NON-NLS-1$
             }
         }
         return str.toString();
     }
 
     /**
      * Remove the specified argument from the list.
      *
      * @param row
      *            the index of the argument to remove
      */
     public void removeJmsProperty(int row) {
         if (row < getProperties().size()) {
             getProperties().remove(row);
         }
     }
 
     /**
      * Remove the specified argument from the list.
      *
      * @param arg
      *            the argument to remove
      */
     public void removeJmsProperty(JMSProperty arg) {
         PropertyIterator iter = getProperties().iterator();
         while (iter.hasNext()) {
             JMSProperty item = (JMSProperty) iter.next().getObjectValue();
             if (arg.equals(item)) {
                 iter.remove();
             }
         }
     }
 
     /**
      * Remove the argument with the specified name.
      *
      * @param argName
      *            the name of the argument to remove
      */
     public void removeJmsProperty(String argName) {
         PropertyIterator iter = getProperties().iterator();
         while (iter.hasNext()) {
             JMSProperty arg = (JMSProperty) iter.next().getObjectValue();
             if (arg.getName().equals(argName)) {
                 iter.remove();
             }
         }
     }
 
     /**
      * Remove all JmsProperties from the list.
      */
     public void removeAllJmsPropertys() {
         getProperties().clear();
     }
 
     /**
      * Get the number of JmsProperties in the list.
      *
      * @return the number of JmsProperties
      */
     public int getJmsPropertyCount() {
         return getProperties().size();
     }
 
     /**
      * Get a single JMSProperty.
      *
      * @param row
      *            the index of the JMSProperty to return.
      * @return the JMSProperty at the specified index, or null if no JMSProperty
      *         exists at that index.
      */
     public JMSProperty getJmsProperty(int row) {
         JMSProperty argument = null;
 
         if (row < getProperties().size()) {
             argument = (JMSProperty) getProperties().get(row).getObjectValue();
         }
 
         return argument;
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 78d1fa930..c77f8df11 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,321 +1,322 @@
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
 
 
 <!--  =================== 2.14 =================== -->
 
 <h1>Version 2.14</h1>
 
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
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 2.14, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 2.14, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. see <bugzill>58687</bugzill></li>   
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>$Revision$</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.11 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.6.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.2 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.7 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
+<li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug> SampleResultConverter should note that it cannot record non-TEXT data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work.Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
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
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
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
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
