diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index 73c502442..121f763c6 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,513 +1,548 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
-// @see org.apache.jmeter.assertions.ResponseAssertionTest for unit tests
-
 /**
  * Test element to handle Response Assertions, @see AssertionGui
+ * see org.apache.jmeter.assertions.ResponseAssertionTest for unit tests
  */
 public class ResponseAssertion extends AbstractScopedAssertion implements Serializable, Assertion {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
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
     private static final int MATCH = 1; // 1 << 0;
 
     private static final int CONTAINS = 1 << 1;
 
     private static final int NOT = 1 << 2;
 
     private static final int EQUALS = 1 << 3;
 
     private static final int SUBSTRING = 1 << 4;
 
-    // Mask should contain all types (but not NOT)
+    private static final int OR = 1 << 5;
+
+    // Mask should contain all types (but not NOT nor OR)
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
+    
+    public boolean isOrType() {
+        return (getTestType() & OR) != 0;
+    }
 
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
+    
+    public void setToOrType() {
+        setTestType((getTestType() | OR));
+    }
+
+    public void unsetOrType() {
+        setTestType(getTestType() & ~OR);
+    }
 
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
-        result.setError(false);
-
+        result.setError(false); 
         boolean notTest = (NOT & getTestType()) > 0;
+        boolean orTest = (OR & getTestType()) > 0;
         boolean contains = isContainsType(); // do it once outside loop
         boolean equals = isEqualsType();
         boolean substring = isSubstringType();
         boolean matches = isMatchType();
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Type:" + (contains?"Contains" : "Match") + (notTest? "(not)" : ""));
+            log.debug("Type:" + (contains?"Contains" : "Match") + (orTest? "(or)" : ""));
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
+        boolean hasTrue = false;
+        ArrayList<String> allCheckMessage = new ArrayList<>();
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
-                if (!pass) {
+                if (orTest) {
+                    if (!pass) {
+                        if (debugEnabled) {
+                            log.debug("Failed: "+stringPattern);
+                        }
+                        allCheckMessage.add(getFailText(stringPattern,toCheck));
+                    } else {
+                        hasTrue=true;
+                        break;
+                    }
+                } else {
+                    if (!pass) {
+                        if (debugEnabled){
+                            log.debug("Failed: "+stringPattern);
+                        }
+                        result.setFailure(true);
+                        result.setFailureMessage(getFailText(stringPattern,toCheck));
+                        break;
+                    }
                     if (debugEnabled){
-                        log.debug("Failed: "+stringPattern);
+                        log.debug("Passed: "+stringPattern);
                     }
-                    result.setFailure(true);
-                    result.setFailureMessage(getFailText(stringPattern,toCheck));
-                    break;
                 }
-                if (debugEnabled){
-                    log.debug("Passed: "+stringPattern);
+            }
+            if (orTest && !hasTrue){
+                StringBuilder errorMsg = new StringBuilder();
+                for(String tmp : allCheckMessage){
+                    errorMsg.append(tmp).append('\t');
                 }
+                result.setFailure(true);
+                result.setFailureMessage(errorMsg.toString());   
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
-
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
diff --git a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
index 85c8828b2..b3f2f632a 100644
--- a/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
+++ b/src/components/org/apache/jmeter/assertions/gui/AssertionGui.java
@@ -1,500 +1,516 @@
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
+    
+    /**
+	 * Add new OR checkbox.
+     */
+    private JCheckBox orBox;
 
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
+
+            if (orBox.isSelected()) {
+                ra.setToOrType();
+            } else {
+                ra.unsetOrType();
+            }
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
+        orBox.setSelected(false);
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
+        orBox.setSelected(model.isOrType());
 
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
 
+        orBox = new JCheckBox(JMeterUtils.getResString("assertion_or")); //$NON-NLS-1$
+        panel.add(orBox);
+
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
index b97361856..bc5492e47 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1346 +1,1347 @@
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
 add_as_child=Add as Child
 add_from_clipboard=Add from Clipboard
 add_from_suggested_excludes=Add suggested Excludes
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
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
 argument_must_not_be_negative=The Argument must not be negative\!
 arguments_panel_title=Command parameters
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
+assertion_or=Or
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_regex_empty_default_value=Use empty default value
 assertion_resp_field=Response Field to Test
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
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 description=Description
 detail=Detail
 directory_field_title=Working directory:
 disable=Disable
 distribution_graph_title=Distribution Graph (DEPRECATED)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 dns_cache_manager_title=DNS Cache Manager
 dns_hostname_or_ip=Hostname or IP address
 dns_servers=DNS Servers
 domain=Domain
 done=Done
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
 jsonpp_match_numbers=Match Numbers
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
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_fragments=Test Fragment
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logger_panel=Log Viewer 
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
 reportgenerator_summary_statistics_kbytes=Received KB/sec
 reportgenerator_summary_statistics_sent_kbytes=Sent KB/sec
 reportgenerator_summary_statistics_label=Label
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Average response time
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
 search_text_msg_not_found=Text not found
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
 soap_sampler_title=SOAP/XML-RPC Request
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
 target_server=Target Server
 tcp_classname=TCPClient classname\:
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 teardown_on_shutdown=Run tearDown Thread Groups after shutdown of main threads
 template_choose=Select Template
 template_create_from=Create
 template_field=Template\:
 template_load?=Load template ?
 template_menu=Templates...
 template_merge_from=Merge
 template_reload=Reload templates
 template_title=Templates
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_restart_next_loop=Go to next loop iteration
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_fragment_title=Test Fragment
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
 testplan_comments=Comments\:
 testt=Test
 textbox_cancel=Cancel
 textbox_close=Close
 textbox_save_close=Save & Close
 textbox_title_edit=Edit text
 textbox_title_view=View text
 textbox_tooltip_cell=Double click to view/edit
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 timeout_config_box_title=Timeout configuration
 timeout_title=Timeout (ms)
 toggle=Toggle
 toolbar_icon_set_not_found=The file description of toolbar icon set is not found. See logs.
 total_threads_tooltip=Total number of threads to run
 tr=Turkish
 transaction_controller_include_timers=Include duration of timer and pre-post processors in generated sample
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transform_into_variable=Replace values with variables
 unbind=Thread Unbind
 undo=Undo
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 up=Up
 update=Update
 update_per_iter=Update Once Per Iteration
 upload=File Upload
 upper_bound=Upper Bound
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocol\:
 url_config_title=HTTP Request Defaults
 url_full_config_title=UrlFull Sample
 url_multipart_config_title=HTTP Multipart Request Defaults
 urldecode_string=String with URL encoded chars to decode
 urlencode_string=String to encode in URL encoded chars
 use_custom_dns_resolver=Use custom DNS resolver
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
 use_recording_controller=Use Recording Controller
 use_system_dns_resolver=Use system DNS resolver
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 validate_threadgroup=Validate
 value=Value
 value_to_quote_meta=Value to escape from ORO Regexp meta chars
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_autoscroll=Scroll automatically?
 view_results_childsamples=Child samples?
 view_results_datatype=Data type ("text"|"bin"|""): 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_connect_time=Connect Time: 
 view_results_load_time=Load time: 
 view_results_render=Render:
 view_results_render_browser=Browser
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download resources)
 view_results_render_html_formatted=HTML Source Formatted
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_missing_tika=Missing tika-app.jar in classpath. Unable to convert to plain text this kind of document.\nDownload the tika-app-x.x.jar file from http://tika.apache.org/download.html\nAnd put the file in <JMeter>/lib directory.
 view_results_response_partial_message=Start of message:
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_sent_bytes=Sent bytes:
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
 view_results_size_in_bytes=Size in bytes: 
 view_results_tab_assertion=Assertion result
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_table_fields_key=Additional field
 view_results_table_fields_value=Value
 view_results_table_headers_key=Response header
 view_results_table_headers_value=Value
 view_results_table_request_headers_key=Request header
 view_results_table_request_headers_value=Value
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=Host
 view_results_table_request_http_method=Method
 view_results_table_request_http_nohttp=No HTTP Sample
 view_results_table_request_http_path=Path
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocol
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_cannot_convert_parameters_to_raw=Cannot convert parameters to Body Data \nbecause one of the parameters has a name
 web_cannot_switch_tab=You cannot switch because data cannot be converted\n to target Tab data, empty data to switch
 web_parameters_lost_message=Switching to Body Data will convert the parameters.\nParameter table will be cleared when you select\nanother node or save the test plan.\nOK to proceeed?
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 web_testing2_title=HTTP Request HTTPClient
 web_testing_basic=Basic
 web_testing_advanced=Advanced
 web_testing_concurrent_download=Parallel downloads. Number:
 web_testing_embedded_url_pattern=URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources
 web_testing_retrieve_title=Embedded Resources from HTML Files
 web_testing_source_ip=Source address
 web_testing_source_ip_device=Device
 web_testing_source_ip_device_ipv4=Device IPv4
 web_testing_source_ip_device_ipv6=Device IPv6
 web_testing_source_ip_hostname=IP/Hostname
 web_testing_title=HTTP Request
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 xml_assertion_title=XML Assertion
 xml_download_dtds=Fetch external DTDs
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Use Tidy (tolerant parser)
 xml_validate_button=Validate XML
 xml_whitespace_button=Ignore Whitespace
 xmlschema_assertion_label=File Name:
 xmlschema_assertion_title=XML Schema Assertion
 xpath_assertion_button=Validate
 xpath_assertion_check=Check XPath Expression
 xpath_assertion_error=Error with XPath
 xpath_assertion_failed=Invalid XPath Expression
 xpath_assertion_label=XPath
 xpath_assertion_negate=True if nothing matches
 xpath_assertion_option=XML Parsing Options
 xpath_assertion_test=XPath Assertion 
 xpath_assertion_tidy=Try and tidy up the input
 xpath_assertion_title=XPath Assertion
 xpath_assertion_valid=Valid XPath Expression
 xpath_assertion_validation=Validate the XML against the DTD
 xpath_assertion_whitespace=Ignore whitespace
 xpath_expression=XPath expression to match against
 xpath_extractor_fragment=Return entire XPath fragment instead of text content?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tester=XPath Tester
 xpath_tester_button_test=Test
 xpath_tester_field=XPath expression
 xpath_tester_fragment=Return entire XPath fragment instead of text content?
 xpath_tester_no_text=Data response result isn't text.
 xpath_tester_title=XPath Tester
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
-zh_tw=Chinese (Traditional)
+zh_tw=Chinese (Traditional)
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 7d0b1c96b..239e20a88 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1106 +1,1107 @@
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
 active_threads_tooltip=Unit\u00E9s actives
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_from_clipboard=Ajouter depuis Presse-papier
 add_from_suggested_excludes=Ajouter exclusions propos\u00E9es
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
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
 aggregate_report_bandwidth=Ko/sec re\u00e7us
 aggregate_report_sent_bytes_per_sec=KB/sec \u00E9mis
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
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
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 arguments_panel_title=Param\u00E8tres de commande
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
+assertion_or=Ou
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_regex_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
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
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 description=Description
 detail=D\u00E9tail
 directory_field_title=R\u00E9pertoire d'ex\u00E9cution \:
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (DEPRECATED)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=Racine DN \:
 dns_cache_manager_title=Gestionnaire de cache DNS
 dns_hostname_or_ip=Nom de machine ou adresse IP
 dns_servers=Serveurs DNS
 domain=Domaine \:
 done=Fait
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
 if_controller_title=Contr\u00F4leur Si (If)
 ignore_subcontrollers=Ignorer les sous-blocs de contr\u00F4leurs
 include_controller=Contr\u00F4leur Inclusion
 include_equals=Inclure \u00E9gale ?
 include_path=Plan de test \u00E0 inclure
 increment=Incr\u00E9ment \:
 infinite=Infini
 initial_context_factory=Fabrique de contexte initiale
 insert_after=Ins\u00E9rer apr\u00E8s
 insert_before=Ins\u00E9rer avant
 insert_parent=Ins\u00E9rer en tant que parent
 interleave_control_title=Contr\u00F4leur Interleave
 interleave_accross_threads=Alterne en prenant en compte toutes les unit\u00E9s
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
 jsonpp_default_values=Valeure par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=Nombre de correspondances
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
 action_check_message=Un test est en cours, arr\u00EAtez le avant d''utiliser cette commande
 action_check_title=Test en cours
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
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logger_panel=Afficher la console 
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
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_multi_result_source_variable=Variable(s) source (separateur |)
 random_multi_result_target_variable=Variable cible
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
 reportgenerator_top5_error_count=#Erreurs
 reportgenerator_top5_error_label=Erreur
 reportgenerator_top5_label=Echantillon
 reportgenerator_top5_sample_count=#Echantillons
 reportgenerator_top5_total=Total
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
 reportgenerator_summary_statistics_kbytes=Ko re\u00e7ues / sec
 reportgenerator_summary_statistics_sent_kbytes=Ko envoy\u00e9s / sec
 reportgenerator_summary_statistics_label=Libell\u00E9
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Temps moyen
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile
 reportgenerator_summary_statistics_throughput=D\u00E9bit
 reportgenerator_summary_total=Total
 request_data=Donn\u00E9e requ\u00EAte
 reset=R\u00E9initialiser
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 response_time_distribution_satisfied_label=Requ\u00EAtes \\ntemps de r\u00E9ponse <= {0}ms
 response_time_distribution_tolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms et <= {1}ms
 response_time_distribution_untolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms
 response_time_distribution_failed_label=Requ\u00EAtes en erreur
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
 save_as=Enregistrer sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_as_test_fragment=Enregistrer comme Fragment de Test
 save_as_test_fragment_error=Au moins un \u00E9l\u00E9ment ne peut pas \u00EAtre plac\u00E9 sous un Fragment de Test
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets re\u00e7us
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
 search_text_msg_not_found=Texte non trouv\u00E9
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
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
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
 tcp_request_data=Texte \u00E0 envoyer \:
 tcp_sample_title=Requ\u00EAte TCP
 tcp_timeout=Expiration (millisecondes) \:
 teardown_on_shutdown=Ex\u00E9cuter le Groupe d'unit\u00E9s de fin m\u00EAme apr\u00E8s un arr\u00EAt manuel des Groupes d'unit\u00E9s principaux
diff --git a/src/core/org/apache/jmeter/resources/messages_zh_CN.properties b/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
index 4e86695e9..07120e347 100644
--- a/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
+++ b/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
@@ -1,405 +1,405 @@
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
 
 about=\u5173\u4E8EApache JMeter
 add=\u6DFB\u52A0
 add_as_child=\u6DFB\u52A0\u5B50\u8282\u70B9
 add_parameter=\u6DFB\u52A0\u53D8\u91CF
 add_pattern=\u6DFB\u52A0\u6A21\u5F0F\uFF1A
 add_test=\u6DFB\u52A0\u6D4B\u8BD5
 add_user=\u6DFB\u52A0\u7528\u6237
 add_value=\u6DFB\u52A0\u6570\u503C
 aggregate_report=\u805A\u5408\u62A5\u544A
 aggregate_report_total_label=\u603B\u4F53
 als_message=\u6CE8\u610F\uFF1A\u8BBF\u95EE\u65E5\u5FD7\u89E3\u6790\u5668\uFF08Access Log Parser\uFF09\u662F\u901A\u7528\u7684\u5E76\u5141\u8BB8\u5B9A\u4E49\u63D2\u4EF6
 als_message2=\u81EA\u5B9A\u4E49\u7684\u89E3\u6790\u5668\u3002\u8981\u8FD9\u4E48\u505A\uFF0C\u5B9E\u73B0LogParser\uFF0C\u6DFB\u52A0jar\u5230
 als_message3=/lib\u76EE\u5F55\u5E76\u5728sampler\u4E2D\u8F93\u5165\u7C7B\u540D\u79F0\u3002
 analyze=\u5206\u6790\u6570\u636E\u6587\u4EF6...
 anchor_modifier_title=HTML\u94FE\u63A5\u89E3\u6790\u5668
 appearance=\u5916\u89C2
 argument_must_not_be_negative=\u53C2\u6570\u4E0D\u5141\u8BB8\u662F\u8D1F\u503C\uFF01
 assertion_code_resp=\u54CD\u5E94\u4EE3\u7801
 assertion_contains=\u5305\u62EC
 assertion_matches=\u5339\u914D
 assertion_message_resp=\u54CD\u5E94\u4FE1\u606F
 assertion_not=\u5426
+assertion_or=\u6216\u8005
 assertion_pattern_match_rules=\u6A21\u5F0F\u5339\u914D\u89C4\u5219
 assertion_patterns_to_test=\u8981\u6D4B\u8BD5\u7684\u6A21\u5F0F
 assertion_resp_field=\u8981\u6D4B\u8BD5\u7684\u54CD\u5E94\u5B57\u6BB5
 assertion_text_resp=\u54CD\u5E94\u6587\u672C
 assertion_textarea_label=\u65AD\u8A00\uFF1A
 assertion_title=\u54CD\u5E94\u65AD\u8A00
 assertion_url_samp=URL\u6837\u672C
 assertion_visualizer_title=\u65AD\u8A00\u7ED3\u679C
 auth_base_url=\u57FA\u7840URL
 auth_manager_title=HTTP\u6388\u6743\u7BA1\u7406\u5668
 auths_stored=\u5B58\u50A8\u5728\u6388\u6743\u7BA1\u7406\u5668\u4E2D\u7684\u6388\u6743
 browse=\u6D4F\u89C8...
 bsf_sampler_title=BSF \u53D6\u6837\u5668
 bsf_script=\u8981\u8FD0\u884C\u7684\u811A\u672C
 bsf_script_file=\u8981\u8FD0\u884C\u7684\u811A\u672C\u6587\u4EF6
 bsf_script_language=\u811A\u672C\u8BED\u8A00\uFF1A
 bsf_script_parameters=\u4F20\u9012\u7ED9\u811A\u672C/\u6587\u4EF6\u7684\u53C2\u6570\uFF1A
 bsh_assertion_title=BeanShell\u65AD\u8A00
 bsh_function_expression=\u8868\u8FBE\u5F0F\u6C42\u503C
 bsh_script_file=\u811A\u672C\u6587\u4EF6
 bsh_script_parameters=\u53C2\u6570\uFF08-> String Parameters \u548C String [ ]bash.args\uFF09
 busy_testing=\u6B63\u5728\u6D4B\u8BD5\uFF0C\u8BF7\u5728\u4FEE\u6539\u8BBE\u7F6E\u524D\u505C\u6B62\u6D4B\u8BD5
 cancel=\u53D6\u6D88
 cancel_exit_to_save=\u6D4B\u8BD5\u6761\u76EE\u672A\u5B58\u50A8\u3002\u4F60\u60F3\u5728\u9000\u51FA\u524D\u5B58\u50A8\u5417\uFF1F
 cancel_new_to_save=\u6D4B\u8BD5\u6761\u76EE\u672A\u5B58\u50A8\u3002\u4F60\u60F3\u5728\u6E05\u7A7A\u6D4B\u8BD5\u8BA1\u5212\u524D\u5B58\u50A8\u5417\uFF1F
 choose_function=\u9009\u62E9\u4E00\u4E2A\u529F\u80FD
 choose_language=\u9009\u62E9\u8BED\u8A00
 clear=\u6E05\u9664
 clear_all=\u6E05\u9664\u5168\u90E8
 clear_cookies_per_iter=\u6BCF\u6B21\u53CD\u590D\u6E05\u9664Cookies \uFF1F
 column_delete_disallowed=\u4E0D\u5141\u8BB8\u5220\u9664\u6B64\u5217
 column_number=CSV\u6587\u4EF6\u5217\u53F7| next| *alias
 constant_throughput_timer_memo=\u5728\u53D6\u6837\u95F4\u6DFB\u52A0\u5EF6\u8FDF\u6765\u83B7\u5F97\u56FA\u5B9A\u7684\u541E\u5410\u91CF
 constant_timer_delay=\u7EBF\u7A0B\u5EF6\u8FDF\uFF08\u6BEB\u79D2\uFF09\uFF1A
 constant_timer_memo=\u5728\u53D6\u6837\u95F4\u6DFB\u52A0\u56FA\u5B9A\u5EF6\u8FDF
 constant_timer_title=\u56FA\u5B9A\u5B9A\u65F6\u5668
 controller=\u63A7\u5236\u5668
 cookie_manager_title=HTTP Cookie \u7BA1\u7406\u5668
 cookies_stored=\u5B58\u50A8\u5728Cookie\u7BA1\u7406\u5668\u4E2D\u7684Cookie
 copy=\u590D\u5236
 counter_config_title=\u8BA1\u6570\u5668
 counter_per_user=\u4E0E\u6BCF\u7528\u6237\u72EC\u7ACB\u7684\u8DDF\u8E2A\u8BA1\u6570\u5668
 cut=\u526A\u5207
 cut_paste_function=\u62F7\u8D1D\u5E76\u7C98\u8D34\u51FD\u6570\u5B57\u7B26\u4E32
 database_sql_query_string=SQL\u67E5\u8BE2\u5B57\u7B26\u4E32\uFF1A
 database_sql_query_title=JDBC SQL \u67E5\u8BE2\u7F3A\u7701\u503C
 de=\u5FB7\u8BED
 default_parameters=\u7F3A\u7701\u53C2\u6570
 default_value_field=\u7F3A\u7701\u503C\uFF1A
 delay=\u542F\u52A8\u5EF6\u8FDF\uFF08\u79D2\uFF09
 delete=\u5220\u9664
 delete_parameter=\u5220\u9664\u53D8\u91CF
 delete_test=\u5220\u9664\u6D4B\u8BD5
 delete_user=\u5220\u9664\u7528\u6237
 disable=\u7981\u7528
 domain=\u57DF
 duration=\u6301\u7EED\u65F6\u95F4\uFF08\u79D2\uFF09
 duration_assertion_duration_test=\u65AD\u8A00\u6301\u7EED\u65F6\u95F4
 duration_assertion_failure=\u64CD\u4F5C\u6301\u7EED\u592A\u957F\u65F6\u95F4\uFF1A\u4ED6\u82B1\u8D39\u4E86{0}\u6BEB\u79D2\uFF0C\u4F46\u4E0D\u5E94\u8BE5\u8D85\u8FC7{1}\u6BEB\u79D2\u3002
 duration_assertion_input_error=\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u6B63\u6574\u6570\u3002
 duration_assertion_label=\u6301\u7EED\u65F6\u95F4\uFF08\u6BEB\u79D2\uFF09\uFF1A
 duration_assertion_title=\u65AD\u8A00\u6301\u7EED\u65F6\u95F4
 edit=\u7F16\u8F91
 email_results_title=\u7535\u5B50\u90AE\u4EF6\u7ED3\u679C
 en=\u82F1\u8BED
 enable=\u542F\u7528
 encode?=\u7F16\u7801\uFF1F
 encoded_value=URL\u7F16\u7801\u540E\u7684\u503C
 endtime=\u7ED3\u675F\u65F6\u95F4
 entry_dn=\u5165\u53E3DN
 error_loading_help=\u52A0\u8F7D\u5E2E\u52A9\u9875\u9762\u51FA\u9519
 error_occurred=\u53D1\u751F\u9519\u8BEF
 example_data=\u6837\u672C\u6570\u636E
 example_title=\u793A\u4F8B\u53D6\u6837\u5668
 exit=\u9000\u51FA
 expiration=\u8FC7\u671F
 field_name=\u5B57\u6BB5\u540D\u6210
 file=\u6587\u4EF6
 file_already_in_use=\u6587\u4EF6\u6B63\u5728\u4F7F\u7528
 file_visualizer_append=\u6DFB\u52A0\u5230\u5DF2\u7ECF\u5B58\u5728\u7684\u6570\u636E\u6587\u4EF6
 file_visualizer_auto_flush=\u5728\u6BCF\u6B21\u6570\u636E\u53D6\u6837\u540E\u81EA\u52A8\u66F4\u65B0
 file_visualizer_browse=\u6D4F\u89C8...
 file_visualizer_close=\u5173\u95ED
 file_visualizer_file_options=\u6587\u4EF6\u64CD\u4F5C
 file_visualizer_filename=\u6587\u4EF6\u540D
 file_visualizer_flush=\u66F4\u65B0
 file_visualizer_missing_filename=\u6CA1\u6709\u6307\u5B9A\u8F93\u51FA\u6587\u4EF6\u540D\u3002
 file_visualizer_open=\u6253\u5F00
 file_visualizer_output_file=\u6240\u6709\u6570\u636E\u5199\u5165\u4E00\u4E2A\u6587\u4EF6
 file_visualizer_submit_data=\u5305\u62EC\u88AB\u63D0\u4EA4\u7684\u6570\u636E
 file_visualizer_title=\u6587\u4EF6\u62A5\u544A\u5668
 file_visualizer_verbose=\u8BE6\u7EC6\u7684\u8F93\u51FA
 filename=\u6587\u4EF6\u540D\u79F0
 follow_redirects=\u8DDF\u968F\u91CD\u5B9A\u5411
 follow_redirects_auto=\u81ea\u52a8\u91cd\u5b9a\u5411
 foreach_controller_title=ForEach\u63A7\u5236\u5668
 foreach_input=\u8F93\u5165\u53D8\u91CF\u524D\u7F00
 foreach_output=\u8F93\u51FA\u53D8\u91CF\u540D\u79F0
 ftp_sample_title=FTP\u8BF7\u6C42\u7F3A\u7701\u503C
 ftp_testing_title=FTP\u8BF7\u6C42
 function_dialog_menu_item=\u51FD\u6570\u52A9\u624B\u5BF9\u8BDD\u6846
 function_helper_title=\u51FD\u6570\u52A9\u624B
 function_name_param=\u51FD\u6570\u540D\u79F0\u3002\u7528\u4E8E\u5B58\u50A8\u5728\u6D4B\u8BD5\u8BA1\u5212\u4E2D\u5176\u4ED6\u7684\u65B9\u5F0F\u4F7F\u7528\u7684\u503C\u3002
 function_params=\u51FD\u6570\u53C2\u6570
 functional_mode=\u51FD\u6570\u6D4B\u8BD5\u6A21\u5F0F
 functional_mode_explanation=\u53EA\u6709\u5F53\u4F60\u9700\u8981\u8BB0\u5F55\u6BCF\u4E2A\u8BF7\u6C42\u4ECE\u670D\u52A1\u5668\u53D6\u5F97\u7684\u6570\u636E\u5230\u6587\u4EF6\u65F6\n\u624D\u9700\u8981\u9009\u62E9\u51FD\u6570\u6D4B\u8BD5\u6A21\u5F0F\u3002\n\n\u9009\u62E9\u8FD9\u4E2A\u9009\u9879\u5F88\u5F71\u54CD\u6027\u80FD\u3002\n
 gaussian_timer_delay=\u56FA\u5B9A\u5EF6\u8FDF\u504F\u79FB\uFF08\u6BEB\u79D2\uFF09\uFF1A
 gaussian_timer_memo=\u6DFB\u52A0\u4E00\u4E2A\u968F\u673A\u7684\u9AD8\u65AF\u5206\u5E03\u5EF6\u8FDF
 gaussian_timer_range=\u504F\u5DEE\uFF08\u6BEB\u79D2\uFF09\uFF1A
 gaussian_timer_title=\u9AD8\u65AF\u968F\u673A\u5B9A\u65F6\u5668
 generate=\u751F\u6210
 generator=\u751F\u6210\u5668\u7C7B\u540D\u79F0
 generator_cnf_msg=\u4E0D\u80FD\u627E\u5230\u751F\u6210\u5668\u7C7B\u3002\u8BF7\u786E\u5B9A\u4F60\u5C06jar\u6587\u4EF6\u653E\u7F6E\u5728/lib\u76EE\u5F55\u4E2D\u3002
 generator_illegal_msg=\u7531\u4E8EIllegalAcessException\uFF0C\u4E0D\u80FD\u8BBF\u95EE\u751F\u6210\u5668\u7C7B\u3002
 generator_instantiate_msg=\u4E0D\u80FD\u521B\u5EFA\u751F\u6210\u5668\u89E3\u6790\u5668\u7684\u5B9E\u4F8B\u3002\u8BF7\u786E\u4FDD\u751F\u6210\u5668\u5B9E\u73B0\u4E86Generator\u63A5\u53E3\u3002
 graph_choose_graphs=\u8981\u663E\u793A\u7684\u56FE\u5F62
 graph_full_results_title=\u56FE\u5F62\u7ED3\u679C
 graph_results_average=\u5E73\u5747
 graph_results_data=\u6570\u636E
 graph_results_deviation=\u504F\u79BB
 graph_results_latest_sample=\u6700\u65B0\u6837\u672C
 graph_results_median=\u4E2D\u503C
 graph_results_no_samples=\u6837\u672C\u6570\u76EE
 graph_results_throughput=\u541E\u5410\u91CF
 graph_results_title=\u56FE\u5F62\u7ED3\u679C
 grouping_add_separators=\u5728\u7EC4\u95F4\u6DFB\u52A0\u5206\u9694
 grouping_in_controllers=\u6BCF\u4E2A\u7EC4\u653E\u5165\u4E00\u4E2A\u65B0\u7684\u63A7\u5236\u5668
 grouping_mode=\u5206\u7EC4\uFF1A
 grouping_no_groups=\u4E0D\u5BF9\u6837\u672C\u5206\u7EC4
 grouping_store_first_only=\u53EA\u5B58\u50A8\u6BCF\u4E2A\u7EC4\u7684\u7B2C\u4E00\u4E2A\u6837\u672C
 header_manager_title=HTTP\u4FE1\u606F\u5934\u7BA1\u7406\u5668
 headers_stored=\u4FE1\u606F\u5934\u5B58\u50A8\u5728\u4FE1\u606F\u5934\u7BA1\u7406\u5668\u4E2D
 help=\u5E2E\u52A9
 http_response_code=HTTP\u578B\u5E94\u4EE3\u7801
 http_url_rewriting_modifier_title=HTTP URL \u91CD\u5199\u4FEE\u9970\u7B26
 http_user_parameter_modifier=HTTP \u7528\u6237\u53C2\u6570\u4FEE\u9970\u7B26
 id_prefix=ID\u524D\u7F00
 id_suffix=ID\u540E\u7F00
 if_controller_label=\u6761\u4EF6
 if_controller_title=\u5982\u679C\uFF08If\uFF09\u63A7\u5236\u5668
 ignore_subcontrollers=\u5FFD\u7565\u8D44\u63A7\u5236\u5668\u5757
 include_equals=\u5305\u542B\u7B49\u4E8E\uFF1F
 increment=\u9012\u589E
 infinite=\u6C38\u8FDC
 insert_after=\u4E4B\u540E\u63D2\u5165
 insert_before=\u4E4B\u524D\u63D2\u5165
 insert_parent=\u63D2\u5165\u4E0A\u7EA7
 interleave_control_title=\u4EA4\u66FF\u63A7\u5236\u5668
 intsum_param_1=\u8981\u6DFB\u52A0\u7684\u7B2C\u4E00\u4E2A\u6574\u6570\u3002
 intsum_param_2=\u8981\u6DFB\u52A0\u7684\u7B2C\u4E8C\u4E2A\u6574\u6570\u2014\u2014\u66F4\u591A\u7684\u6574\u6570\u53EF\u4EE5\u901A\u8FC7\u6DFB\u52A0\u66F4\u591A\u7684\u53C2\u6570\u6765\u6C42\u548C\u3002
 invalid_data=\u65E0\u6548\u6570\u636E
 invalid_mail_server=\u90AE\u4EF6\u670D\u52A1\u5668\u4E0D\u53EF\u77E5\u3002
 iteration_counter_arg_1=TRUE\uFF0C\u6BCF\u4E2A\u7528\u6237\u6709\u81EA\u5DF1\u7684\u8BA1\u6570\u5668\uFF1BFALSE\uFF0C\u4F7F\u7528\u5168\u5C40\u8BA1\u6570\u5668
 iterator_num=\u5FAA\u73AF\u6B21\u6570
 java_request=Java\u8BF7\u6C42
 java_request_defaults=Java\u8BF7\u6C42\u9ED8\u8BA4\u503C
 jndi_config_title=JNDI\u914D\u7F6E
 jndi_lookup_name=\u8FDC\u7A0B\u63A5\u53E3
 jndi_lookup_title=JNDI\u67E5\u8BE2\u914D\u7F6E
 jndi_method_button_invoke=\u8C03\u7528
 jndi_method_button_reflect=\u53CD\u5C04
 jndi_method_home_name=\u672C\u5730\u65B9\u6CD5\u540D\u79F0
 jndi_method_home_parms=\u672C\u5730\u65B9\u6CD5\u53C2\u6570
 jndi_method_name=\u65B9\u6CD5\u914D\u7F6E
 jndi_method_remote_interface_list=\u8FDC\u7A0B\u63A5\u53E3
 jndi_method_remote_name=\u8FDC\u7A0B\u65B9\u6CD5\u540D\u79F0
 jndi_method_remote_parms=\u8FDC\u7A0B\u65B9\u6CD5\u53C2\u6570
 jndi_method_title=\u8FDC\u7A0B\u65B9\u6CD5\u914D\u7F6E
 jndi_testing_title=JNDI\u8BF7\u6C42
 jndi_url_jndi_props=JNDI\u5C5E\u6027
 ja=\u65E5\u8BED
 ldap_sample_title=LDAP\u8BF7\u6C42\u9ED8\u8BA4\u503C
 ldap_testing_title=LDAP\u8BF7\u6C42
 load=\u8F7D\u5165
 log_errors_only=\u4EC5\u65E5\u5FD7\u9519\u8BEF
 log_file=\u65E5\u5FD7\u6587\u4EF6\u4F4D\u7F6E
 log_parser=\u65E5\u5FD7\u89E3\u6790\u5668\u7C7B\u540D
 log_parser_cnf_msg=\u627E\u4E0D\u5230\u7C7B\u3002\u786E\u8BA4\u4F60\u5C06jar\u6587\u4EF6\u653E\u5728\u4E86/lib\u76EE\u5F55\u4E2D\u3002
 log_parser_illegal_msg=\u56E0\u4E3AIllegalAccessException\u4E0D\u80FD\u8BBF\u95EE\u7C7B\u3002
 log_parser_instantiate_msg=\u4E0D\u80FD\u521B\u5EFA\u65E5\u5FD7\u89E3\u6790\u5668\u5B9E\u4F8B\u3002\u786E\u8BA4\u89E3\u6790\u5668\u5B9E\u73B0\u4E86LogParser\u63A5\u53E3\u3002
 log_sampler=Tomcat\u8BBF\u95EE\u65E5\u5FD7\u53D6\u6837\u5668
 logic_controller_title=\u7B80\u5355\u63A7\u5236\u5668
 login_config=\u767B\u9646\u914D\u7F6E
 login_config_element=\u767B\u9646\u914D\u7F6E\u5143\u4EF6/\u7D20
 loop_controller_title=\u5FAA\u73AF\u63A7\u5236\u5668
 looping_control=\u5FAA\u73AF\u63A7\u5236
 lower_bound=\u8F83\u4F4E\u8303\u56F4
 mailer_attributes_panel=\u90AE\u4EF6\u5C5E\u6027
 mailer_error=\u4E0D\u80FD\u53D1\u9001\u90AE\u4EF6\u3002\u8BF7\u4FEE\u6B63\u9519\u8BEF\u3002
 mailer_visualizer_title=\u90AE\u4EF6\u89C2\u5BDF\u4EEA
 match_num_field=\u5339\u914D\u6570\u5B57\uFF080\u4EE3\u8868\u968F\u673A\uFF09\uFF1A
 max=\u6700\u5927\u503C
 maximum_param=\u4E00\u4E2A\u8303\u56F4\u5185\u5141\u8BB8\u7684\u6700\u5927\u503C
 md5hex_assertion_failure=MD5\u603B\u5408\u65AD\u8A00\u9519\u8BEF\uFF1A\u5F97\u5230\u4E86{0}\uFF0C\u4F46\u5E94\u8BE5\u662F{1}
 md5hex_assertion_md5hex_test=\u8981\u65AD\u8A00\u7684MD5Hex
 md5hex_assertion_title=MD5Hex\u65AD\u8A00
 menu_assertions=\u65AD\u8A00
 menu_close=\u5173\u95ED
 menu_config_element=\u914D\u7F6E\u5143\u4EF6
 menu_edit=\u7F16\u8F91
 menu_listener=\u76D1\u542C\u5668
 menu_logic_controller=\u903B\u8F91\u63A7\u5236\u5668
 menu_merge=\u5408\u5E76
 menu_modifiers=\u4FEE\u9970\u7B26
 menu_non_test_elements=\u975E\u6D4B\u8BD5\u5143\u4EF6
 menu_open=\u6253\u5F00
 menu_post_processors=\u540E\u7F6E\u5904\u7406\u5668
 menu_pre_processors=\u524D\u7F6E\u5904\u7406\u5668
 menu_response_based_modifiers=\u57FA\u4E8E\u76F8\u5E94\u7684\u4FEE\u9970\u7B26
 menu_timer=\u5B9A\u65F6\u5668
 metadata=\u539F\u6570\u636E
 method=\u65B9\u6CD5\uFF1A
 mimetype=MIME\u7C7B\u578B
 minimum_param=\u4E00\u4E2A\u8303\u56F4\u5185\u7684\u6700\u5C0F\u503C
 minute=\u5206\u949F
 modification_controller_title=\u4FEE\u6B63\u63A7\u5236\u5668
 modification_manager_title=\u4FEE\u6B63\u7BA1\u7406\u5668
 modify_test=\u4FEE\u6539\u6D4B\u8BD5
 module_controller_title=\u6A21\u5757\u63A7\u5236\u5668
 name=\u540D\u79F0\uFF1A
 new=\u65B0\u5EFA
 no=\u632A\u5A01\u8BED
 number_of_threads=\u7EBF\u7A0B\u6570\uFF1A
 once_only_controller_title=\u4EC5\u4E00\u6B21\u63A7\u5236\u5668
 open=\u6253\u5F00...
 option=\u9009\u9879
 optional_tasks=\u5176\u4ED6\u4EFB\u52A1
 paramtable=\u540C\u8BF7\u6C42\u4E00\u8D77\u53D1\u9001\u53C2\u6570\uFF1A
 password=\u5BC6\u7801
 paste=\u7C98\u8D34
 paste_insert=\u4F5C\u4E3A\u63D2\u5165\u7C98\u8D34
 path=\u8DEF\u5F84\uFF1A
 path_extension_choice=\u8DEF\u5F84\u6269\u5C55\uFF08\u4F7F\u7528";"\u4F5C\u5206\u9694\u7B26\uFF09
 patterns_to_exclude=\u6392\u9664\u6A21\u5F0F
 patterns_to_include=\u5305\u542B\u6A21\u5F0F
 port=\u7AEF\u53E3\uFF1A
 property_default_param=\u9ED8\u8BA4\u503C
 property_edit=\u7F16\u8F91
 property_editor.value_is_invalid_title=\u65E0\u6548\u8F93\u5165
 property_name_param=\u5C5E\u6027\u540D\u79F0
 property_undefined=\u672A\u5B9A\u4E49
 protocol=\u534F\u8BAE\uFF1A
 protocol_java_border=Java\u7C7B
 protocol_java_classname=\u7C7B\u540D\u79F0\uFF1A
 protocol_java_config_tile=\u914D\u7F6EJava\u6837\u672C
 protocol_java_test_title=Java\u6D4B\u8BD5
 proxy_assertions=\u6DFB\u52A0\u65AD\u8A00
 proxy_cl_error=\u5982\u679C\u6307\u5B9A\u4EE3\u7406\u670D\u52A1\u5668\uFF0C\u4E3B\u673A\u548C\u7AEF\u53E3\u5FC5\u987B\u6307\u5B9A
 proxy_headers=\u8BB0\u5F55HTTP\u4FE1\u606F\u5934
 proxy_separators=\u6DFB\u52A0\u5206\u9694\u7B26
 proxy_target=\u76EE\u6807\u63A7\u5236\u5668\uFF1A
 proxy_title=HTTP\u4EE3\u7406\u670D\u52A1\u5668
 random_control_title=\u968F\u673A\u63A7\u5236\u5668
 random_order_control_title=\u968F\u673A\u987A\u5E8F\u63A7\u5236\u5668
 record_controller_title=\u5F55\u5236\u63A7\u5236\u5668
 ref_name_field=\u5F15\u7528\u540D\u79F0\uFF1A
 regex_extractor_title=\u6B63\u5219\u8868\u8FBE\u5F0F\u63D0\u53D6\u5668
 regex_field=\u6B63\u5219\u8868\u8FBE\u5F0F\uFF1A
 regex_source=\u8981\u68C0\u67E5\u7684\u54CD\u5E94\u5B57\u6BB5
 regex_src_body=\u4E3B\u4F53
 regex_src_hdrs=\u4FE1\u606F\u5934
 regexfunc_param_1=\u7528\u4E8E\u4ECE\u524D\u4E00\u4E2A\u8BF7\u6C42\u641C\u7D22\u7ED3\u679C\u7684\u6B63\u5219\u8868\u8FBE\u5F0F
 remote_exit=\u8FDC\u7A0B\u9000\u51FA
 remote_exit_all=\u8FDC\u7A0B\u5168\u90E8\u9000\u51FA
 remote_start=\u8FDC\u7A0B\u542F\u52A8
 remote_start_all=\u8FDC\u7A0B\u5168\u90E8\u542F\u52A8
 remote_stop=\u8FDC\u7A0B\u505C\u6B62
 remote_stop_all=\u8FDC\u7A0B\u5168\u90E8\u505C\u6B62
 remove=\u5220\u9664
 report=\u62A5\u544A
 request_data=\u8BF7\u6C42\u6570\u636E
 restart=\u91CD\u542F
 resultsaver_prefix=\u6587\u4EF6\u540D\u79F0\u524D\u7F00\uFF1A
 resultsaver_title=\u4FDD\u5B58\u54CD\u5E94\u5230\u6587\u4EF6
 root=\u6839
 root_title=\u6839
 run=\u8FD0\u884C
 running_test=\u6B63\u5728\u8FD0\u884C\u7684\u6D4B\u8BD5
 sampler_on_error_action=\u5728\u53D6\u6837\u5668\u9519\u8BEF\u540E\u8981\u6267\u884C\u7684\u52A8\u4F5C
 sampler_on_error_continue=\u7EE7\u7EED
 sampler_on_error_stop_test=\u505C\u6B62\u6D4B\u8BD5
 sampler_on_error_stop_thread=\u505C\u6B62\u7EBF\u7A0B
 save=\u4FDD\u5B58\u6D4B\u8BD5\u8BA1\u5212
 save?=\u4FDD\u5B58\uFF1F
 save_all_as=\u4FDD\u5B58\u6D4B\u8BD5\u8BA1\u5212\u4E3A
 save_as=\u9009\u4E2D\u90E8\u5206\u4FDD\u5B58\u4E3A...
 scheduler=\u8C03\u5EA6\u5668
 scheduler_configuration=\u8C03\u5EA6\u5668\u914D\u7F6E
 search_filter=\u641C\u7D22\u8FC7\u6EE4\u5668
 search_test=\u641C\u7D22\u6D4B\u8BD5
 secure=\u5B89\u5168
 send_file=\u540C\u8BF7\u6C42\u4E00\u8D77\u53D1\u9001\u6587\u4EF6\uFF1A
 send_file_browse=\u6D4F\u89C8...
 send_file_filename_label=\u6587\u4EF6\u540D\u79F0\uFF1A
 send_file_mime_label=MIME\u7C7B\u578B\uFF1A
 send_file_param_name_label=\u53C2\u6570\u540D\u79F0\uFF1A
 server=\u670D\u52A1\u5668\u540D\u79F0\u6216IP\uFF1A
 servername=\u670D\u52A1\u5668\u540D\u79F0\uFF1A
 session_argument_name=\u4F1A\u8BDD\u53C2\u6570\u540D\u79F0\uFF1A
 shutdown=\u5173\u95ED
 simple_config_element=\u7B80\u5355\u914D\u7F6E\u5143\u4EF6
 size_assertion_comparator_label=\u6BD4\u8F83\u7C7B\u578B
 size_assertion_input_error=\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u6B63\u6574\u6570\u3002
 size_assertion_label=\u5B57\u8282\u5927\u5C0F\uFF1A
 soap_action=Soap\u52A8\u4F5C
 ssl_alias_prompt=\u8BF7\u8F93\u5165\u9996\u9009\u7684\u522B\u540D
 ssl_alias_select=\u4E3A\u6D4B\u8BD5\u9009\u62E9\u4F60\u7684\u522B\u540D
 ssl_alias_title=\u5BA2\u6237\u7AEF\u522B\u540D
 ssl_pass_prompt=\u8BF7\u8F93\u5165\u4F60\u7684\u5BC6\u7801
 ssl_port=SSL\u7AEF\u53E3
 sslmanager=SSL\u7BA1\u7406\u5668
 start=\u542F\u52A8
 starttime=\u542F\u52A8\u65F6\u95F4
 stop=\u505C\u6B62
 stopping_test=\u505C\u6B62\u5168\u90E8\u6D4B\u8BD5\u7EBF\u7A0B\u3002\u8BF7\u8010\u5FC3\u7B49\u5F85\u3002
 stopping_test_title=\u6B63\u5728\u505C\u6B62\u6D4B\u8BD5
 string_from_file_file_name=\u8F93\u5165\u6587\u4EF6\u7684\u5168\u8DEF\u5F84
 summariser_title=\u751F\u6210\u6982\u8981\u7ED3\u679C
 tcp_config_title=TCP\u53D6\u6837\u5668\u914D\u7F6E
 tcp_nodelay=\u8BBE\u7F6E\u65E0\u5EF6\u8FDF
 tcp_port=\u7AEF\u53E3\u53F7\uFF1A
 tcp_request_data=\u8981\u53D1\u9001\u7684\u6587\u672C
 tcp_sample_title=TCP\u53D6\u6837\u5668
 tcp_timeout=\u8D85\u65F6\uFF1A
 template_field=\u6A21\u677F\uFF1A
 test=\u6D4B\u8BD5
 test_configuration=\u6D4B\u8BD5\u914D\u7F6E
 test_plan=\u6D4B\u8BD5\u8BA1\u5212
 testplan.serialized=\u72EC\u7ACB\u8FD0\u884C\u6BCF\u4E2A\u7EBF\u7A0B\u7EC4\uFF08\u4F8B\u5982\u5728\u4E00\u4E2A\u7EC4\u8FD0\u884C\u7ED3\u675F\u540E\u542F\u52A8\u4E0B\u4E00\u4E2A\uFF09
 testplan_comments=\u6CE8\u91CA\uFF1A
 thread_delay_properties=\u7EBF\u7A0B\u5EF6\u8FDF\u5C5E\u6027
 thread_group_title=\u7EBF\u7A0B\u7EC4
 thread_properties=\u7EBF\u7A0B\u5C5E\u6027
 threadgroup=\u7EBF\u7A0B\u7EC4
 throughput_control_title=\u541E\u5410\u91CF\u63A7\u5236\u5668
 throughput_control_tplabel=\u541E\u5410\u91CF
 transaction_controller_title=\u4E8B\u52A1\u63A7\u5236\u5668
 update_per_iter=\u6BCF\u6B21\u8DCC\u4EE3\u66F4\u65B0\u4E00\u6B21
 upload=\u6587\u4EF6\u4E0A\u8F7D
 upper_bound=\u4E0A\u9650
 url_config_protocol=\u534F\u8BAE\uFF1A
 url_config_title=HTTP\u8BF7\u6C42\u9ED8\u8BA4\u503C
 use_recording_controller=\u4F7F\u7528\u5F55\u5236\u63A7\u5236\u5668
 user=\u7528\u6237
 user_defined_test=\u7528\u6237\u5B9A\u4E49\u7684\u6D4B\u8BD5
 user_defined_variables=\u7528\u6237\u5B9A\u4E49\u7684\u53D8\u91CF
 user_parameters_table=\u53C2\u6570
 user_parameters_title=\u7528\u6237\u53C2\u6570
 username=\u7528\u6237\u540D
 value=\u503C
 var_name=\u5F15\u7528\u540D\u79F0
 view_graph_tree_title=\u5BDF\u770B\u7ED3\u679C\u6811
 view_results_in_table=\u7528\u8868\u683C\u5BDF\u770B\u7ED3\u679C
 view_results_tab_request=\u8BF7\u6C42
 view_results_tab_response=\u54CD\u5E94\u6570\u636E
 view_results_tab_sampler=\u53D6\u6837\u5668\u7ED3\u679C
 view_results_title=\u5BDF\u770B\u7ED3\u679C
 view_results_tree_title=\u5BDF\u770B\u7ED3\u679C\u6811
 web_request=HTTP\u8BF7\u6C42
 web_server=Web\u670D\u52A1\u5668
 web_server_domain=\u670D\u52A1\u5668\u540D\u79F0\u6216IP\uFF1A
 web_server_port=\u7AEF\u53E3\u53F7\uFF1A
 web_testing_retrieve_images=\u4ECEHTML\u6587\u4EF6\u83B7\u53D6\u6240\u6709\u5185\u542B\u7684\u8D44\u6E90
 web_testing_title=HTTP\u8BF7\u6C42
 workbench_title=\u5DE5\u4F5C\u53F0
 xml_assertion_title=XML\u65AD\u8A00
-you_must_enter_a_valid_number=\u5FC5\u987B\u8F93\u5165\u6709\u6548\u7684\u6570\u5B57
-
+you_must_enter_a_valid_number=\u5FC5\u987B\u8F93\u5165\u6709\u6548\u7684\u6570\u5B57
\ No newline at end of file
diff --git a/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java b/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
index da98c3886..c436f2b74 100644
--- a/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
+++ b/test/src/org/apache/jmeter/assertions/ResponseAssertionTest.java
@@ -1,264 +1,295 @@
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
     public void testResponseAssertionHeaders() throws Exception{
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
-
+        
+        assertion.clearTestStrings();
+        assertion.addTestString("line 2");
+        assertion.addTestString("NOTINSAMPLEDATA");
+        result = assertion.getResult(sample);
+        assertFailed();
+        
+        assertion.clearTestStrings();
+        assertion.setToOrType();
+        assertion.addTestString("line 2");
+        assertion.addTestString("NOTINSAMPLEDATA");
+        result = assertion.getResult(sample);
+        assertPassed();
+        assertion.unsetOrType();
+        
+        assertion.clearTestStrings();
+        assertion.setToOrType();
+        assertion.addTestString("NOTINSAMPLEDATA");
+        assertion.addTestString("line 2");
+        result = assertion.getResult(sample);
+        assertPassed();
+        assertion.unsetOrType();
+        
+        
+        assertion.clearTestStrings();
+        assertion.setToNotType();
+        assertion.addTestString("NOTINSAMPLEDATA");
+        result = assertion.getResult(sample);
+        assertPassed();
+        assertion.unsetNotType();
+        
+        
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
index 261daaa35..34e0932bb 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,299 +1,301 @@
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
 Fill in some detail.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>Documentation review and improvements for easier startup</li>
 <li>New <a href="usermanual/properties_reference.html">properties reference</a> documentation section</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
+    <li><bug>60507</bug>Added 'Or' Function into ResponseAssertion. Based on a contributed from \u5ffb\u9686 (298015902 at qq.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize and Maxime Chassagneux</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to xxx-1.1 (from 0.2)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
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
 <li>Logan Mauzaize (https://github.com/loganmzz)</li>
 <li>Maxime Chassagneux (https://github.com/max3163)</li>
+<li>\u5ffb\u9686 (298015902 at qq.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
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
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
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
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry.
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
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
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later.
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
