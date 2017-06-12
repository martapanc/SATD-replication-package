diff --git a/src/components/org/apache/jmeter/extractor/XPathExtractor.java b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
index 1616fd3dc..633ff4f41 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,342 +1,383 @@
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
+import org.apache.jmeter.testelement.property.IntegerProperty;
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
 
-//@see org.apache.jmeter.extractor.TestXPathExtractor for unit tests
 
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
- */
- /* This file is inspired by RegexExtractor.
- * author <a href="mailto:hpaluch@gitus.cz">Henryk Paluch</a>
- *            of <a href="http://www.gitus.com">Gitus a.s.</a>
- *
- * See Bugzilla: 37183
+  see org.apache.jmeter.extractor.TestXPathExtractor for unit tests
  */
 public class XPathExtractor extends AbstractScopedTestElement implements
         PostProcessor, Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
+    
+    private static final int DEFAULT_VALUE = -1;
+    public static final String DEFAULT_VALUE_AS_STRING = Integer.toString(DEFAULT_VALUE);
 
-    private static final String MATCH_NR = "matchNr"; // $NON-NLS-1$
+    private static final String REF_MATCH_NR    = "matchNr"; // $NON-NLS-1$
 
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
+    private static final String MATCH_NUMBER    = "XPathExtractor.matchNumber"; // $NON-NLS-1$
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
-        final String matchNR = concat(refName,MATCH_NR);
+        final String matchNR = concat(refName,REF_MATCH_NR);
         int prevCount=0; // number of previous matches
         try {
             prevCount=Integer.parseInt(vars.get(matchNR));
         } catch (NumberFormatException e) {
             // ignored
         }
         vars.put(matchNR, "0"); // In case parse fails // $NON-NLS-1$
         vars.remove(concat(refName,"1")); // In case parse fails // $NON-NLS-1$
 
+        int matchNumber = getMatchNumber();
         List<String> matches = new ArrayList<>();
         try{
             if (isScopeVariable()){
                 String inputString=vars.get(getVariableName());
                 if(inputString != null) {
                     if(inputString.length()>0) {
                         Document d =  parseResponse(inputString);
-                        getValuesForXPath(d,getXPathQuery(),matches);
+                        getValuesForXPath(d,getXPathQuery(), matches, matchNumber);
                     }
                 } else {
                     log.warn("No variable '"+getVariableName()+"' found to process by XPathExtractor '"+getName()+"', skipping processing");
                 }
             } else {
                 List<SampleResult> samples = getSampleList(previousResult);
                 for (SampleResult res : samples) {
                     Document d = parseResponse(res.getResponseDataAsString());
-                    getValuesForXPath(d,getXPathQuery(),matches);
+                    getValuesForXPath(d,getXPathQuery(), matches, matchNumber);
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
+     * @param matchNumber int Match Number
      *
      * @throws TransformerException
      */
-    private void getValuesForXPath(Document d,String query, List<String> matchStrings)
+    private void getValuesForXPath(Document d,String query, List<String> matchStrings, int matchNumber)
         throws TransformerException {
-        XPathUtil.putValuesForXPathInList(d, query, matchStrings, getFragment());
+        XPathUtil.putValuesForXPathInList(d, query, matchStrings, getFragment(), matchNumber);
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
+    
+    /**
+     * Set which Match to use. This can be any positive number, indicating the
+     * exact match to use, or <code>0</code>, which is interpreted as meaning random.
+     *
+     * @param matchNumber The number of the match to be used
+     */
+    public void setMatchNumber(int matchNumber) {
+        setProperty(new IntegerProperty(MATCH_NUMBER, matchNumber));
+    }
+
+    /**
+     * Set which Match to use. This can be any positive number, indicating the
+     * exact match to use, or <code>0</code>, which is interpreted as meaning random.
+     *
+     * @param matchNumber The number of the match to be used
+     */
+    public void setMatchNumber(String matchNumber) {
+        setProperty(MATCH_NUMBER, matchNumber);
+    }
+
+    /**
+     * Return which Match to use. This can be any positive number, indicating the
+     * exact match to use, or <code>0</code>, which is interpreted as meaning random.
+     *
+     * @return matchNumber The number of the match to be used
+     */
+    public int getMatchNumber() {
+        return getPropertyAsInt(MATCH_NUMBER, DEFAULT_VALUE);
+    }
+
+    /**
+     * Return which Match to use. This can be any positive number, indicating the
+     * exact match to use, or <code>0</code>, which is interpreted as meaning random.
+     *
+     * @return matchNumber The number of the match to be used
+     */
+    public String getMatchNumberAsString() {
+        return getPropertyAsString(MATCH_NUMBER, DEFAULT_VALUE_AS_STRING);
+    }
 }
diff --git a/src/components/org/apache/jmeter/extractor/gui/XPathExtractorGui.java b/src/components/org/apache/jmeter/extractor/gui/XPathExtractorGui.java
index 72a232972..2426dd3d2 100644
--- a/src/components/org/apache/jmeter/extractor/gui/XPathExtractorGui.java
+++ b/src/components/org/apache/jmeter/extractor/gui/XPathExtractorGui.java
@@ -1,174 +1,179 @@
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
 package org.apache.jmeter.extractor.gui;
 
 import java.awt.BorderLayout;
 import java.awt.GridBagConstraints;
 import java.awt.GridBagLayout;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JCheckBox;
 import javax.swing.JComponent;
 import javax.swing.JPanel;
 
 import org.apache.jmeter.assertions.gui.XMLConfPanel;
 import org.apache.jmeter.extractor.XPathExtractor;
 import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
 /**
  * GUI for XPathExtractor class.
  */
- /* This file is inspired by RegexExtractor.
- * See Bugzilla: 37183
- */
 public class XPathExtractorGui extends AbstractPostProcessorGui {
 
     private static final long serialVersionUID = 240L;
 
     private final JLabeledTextField defaultField =
         new JLabeledTextField(JMeterUtils.getResString("default_value_field"));//$NON-NLS-1$
 
     private final JLabeledTextField xpathQueryField =
         new JLabeledTextField(JMeterUtils.getResString("xpath_extractor_query"));//$NON-NLS-1$
 
+    private final JLabeledTextField matchNumberField =
+            new JLabeledTextField(JMeterUtils.getResString("match_num_field"));//$NON-NLS-1$
+
     private final JLabeledTextField refNameField =
         new JLabeledTextField(JMeterUtils.getResString("ref_name_field"));//$NON-NLS-1$
 
     // Should we return fragment as text, rather than text of fragment?
     private final JCheckBox getFragment =
         new JCheckBox(JMeterUtils.getResString("xpath_extractor_fragment"));//$NON-NLS-1$
 
     private final XMLConfPanel xml = new XMLConfPanel();
 
     @Override
     public String getLabelResource() {
         return "xpath_extractor_title"; //$NON-NLS-1$
     }
 
     public XPathExtractorGui(){
         super();
         init();
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         XPathExtractor xpe = (XPathExtractor) el;
         showScopeSettings(xpe,true);
         xpathQueryField.setText(xpe.getXPathQuery());
         defaultField.setText(xpe.getDefaultValue());
         refNameField.setText(xpe.getRefName());
+        matchNumberField.setText(xpe.getMatchNumberAsString());
         getFragment.setSelected(xpe.getFragment());
         xml.configure(xpe);
     }
 
 
     @Override
     public TestElement createTestElement() {
         XPathExtractor extractor = new XPathExtractor();
         modifyTestElement(extractor);
         return extractor;
     }
 
     @Override
     public void modifyTestElement(TestElement extractor) {
         super.configureTestElement(extractor);
         if ( extractor instanceof XPathExtractor){
             XPathExtractor xpath = (XPathExtractor)extractor;
             saveScopeSettings(xpath);
             xpath.setDefaultValue(defaultField.getText());
             xpath.setRefName(refNameField.getText());
+            xpath.setMatchNumber(matchNumberField.getText());
             xpath.setXPathQuery(xpathQueryField.getText());
             xpath.setFragment(getFragment.isSelected());
             xml.modifyTestElement(xpath);
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         xpathQueryField.setText(""); // $NON-NLS-1$
         defaultField.setText(""); // $NON-NLS-1$
         refNameField.setText(""); // $NON-NLS-1$
+        matchNumberField.setText(XPathExtractor.DEFAULT_VALUE_AS_STRING); // $NON-NLS-1$
         xml.setDefaultValues();
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
         setBorder(makeBorder());
 
         Box box = Box.createVerticalBox();
         box.add(makeTitlePanel());
         box.add(createScopePanel(true, true, true));
         xml.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("xpath_assertion_option"))); //$NON-NLS-1$
         box.add(xml);
         box.add(getFragment);
         box.add(makeParameterPanel());
         add(box, BorderLayout.NORTH);
     }
 
 
     private JPanel makeParameterPanel() {
         JPanel panel = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         initConstraints(gbc);
         addField(panel, refNameField, gbc);
         resetContraints(gbc);
         addField(panel, xpathQueryField, gbc);
         resetContraints(gbc);
+        addField(panel, matchNumberField, gbc);
+        resetContraints(gbc);
         gbc.weighty = 1;
         addField(panel, defaultField, gbc);
         return panel;
     }
 
     private void addField(JPanel panel, JLabeledTextField field, GridBagConstraints gbc) {
         List<JComponent> item = field.getComponentList();
         panel.add(item.get(0), gbc.clone());
         gbc.gridx++;
         gbc.weightx = 1;
         gbc.fill=GridBagConstraints.HORIZONTAL;
         panel.add(item.get(1), gbc.clone());
     }
 
     private void resetContraints(GridBagConstraints gbc) {
         gbc.gridx = 0;
         gbc.gridy++;
         gbc.weightx = 0;
         gbc.fill=GridBagConstraints.NONE;
     }
 
     private void initConstraints(GridBagConstraints gbc) {
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.NONE;
         gbc.gridheight = 1;
         gbc.gridwidth = 1;
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.weightx = 0;
         gbc.weighty = 0;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/XPathUtil.java b/src/core/org/apache/jmeter/util/XPathUtil.java
index 17ee8374b..92f46f968 100644
--- a/src/core/org/apache/jmeter/util/XPathUtil.java
+++ b/src/core/org/apache/jmeter/util/XPathUtil.java
@@ -1,453 +1,477 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintWriter;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.nio.charset.StandardCharsets;
 import java.util.List;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.OutputKeys;
 import javax.xml.transform.Source;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.sax.SAXSource;
 import javax.xml.transform.stream.StreamResult;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.xml.utils.PrefixResolver;
 import org.apache.xpath.XPathAPI;
 import org.apache.xpath.objects.XObject;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 /**
  * This class provides a few utility methods for dealing with XML/XPath.
  */
 public class XPathUtil {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private XPathUtil() {
         super();
     }
 
     //@GuardedBy("this")
     private static DocumentBuilderFactory documentBuilderFactory;
 
     /**
      * Returns a suitable document builder factory.
      * Caches the factory in case the next caller wants the same options.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      *
      * @return javax.xml.parsers.DocumentBuilderFactory
      */
     private static synchronized DocumentBuilderFactory makeDocumentBuilderFactory(boolean validate, boolean whitespace,
             boolean namespace) {
         if (XPathUtil.documentBuilderFactory == null || documentBuilderFactory.isValidating() != validate
                 || documentBuilderFactory.isNamespaceAware() != namespace
                 || documentBuilderFactory.isIgnoringElementContentWhitespace() != whitespace) {
             // configure the document builder factory
             documentBuilderFactory = DocumentBuilderFactory.newInstance();
             documentBuilderFactory.setValidating(validate);
             documentBuilderFactory.setNamespaceAware(namespace);
             documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
         }
         return XPathUtil.documentBuilderFactory;
     }
 
     /**
      * Create a DocumentBuilder using the makeDocumentFactory func.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      * @param downloadDTDs if true, parser should attempt to resolve external entities
      * @return document builder
      * @throws ParserConfigurationException if {@link DocumentBuilder} can not be created for the wanted configuration
      */
     public static DocumentBuilder makeDocumentBuilder(boolean validate, boolean whitespace, boolean namespace, boolean downloadDTDs)
             throws ParserConfigurationException {
         DocumentBuilder builder = makeDocumentBuilderFactory(validate, whitespace, namespace).newDocumentBuilder();
         builder.setErrorHandler(new MyErrorHandler(validate, false));
         if (!downloadDTDs){
             EntityResolver er = new EntityResolver(){
                 @Override
                 public InputSource resolveEntity(String publicId, String systemId)
                         throws SAXException, IOException {
                     return new InputSource(new ByteArrayInputStream(new byte[]{}));
                 }
             };
             builder.setEntityResolver(er);
         }
         return builder;
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @return document
      * @throws ParserConfigurationException when no {@link DocumentBuilder} can be constructed for the wanted configuration
      * @throws SAXException if parsing fails
      * @throws IOException if an I/O error occurs while parsing
      * @throws TidyException if a ParseError is detected and <code>report_errors</code> is <code>true</code>
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         return makeDocument(stream, validate, whitespace, namespace,
                 tolerant, quiet, showWarnings, report_errors, isXml, downloadDTDs, null);
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @param tidyOut OutputStream for Tidy pretty-printing
      * @return document
      * @throws ParserConfigurationException if {@link DocumentBuilder} can not be created for the wanted configuration
      * @throws SAXException if parsing fails
      * @throws IOException if I/O error occurs while parsing
      * @throws TidyException if a ParseError is detected and <code>report_errors</code> is <code>true</code>
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs, 
             OutputStream tidyOut)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         Document doc;
         if (tolerant) {
             doc = tidyDoc(stream, quiet, showWarnings, report_errors, isXml, tidyOut);
         } else {
             doc = makeDocumentBuilder(validate, whitespace, namespace, downloadDTDs).parse(stream);
         }
         return doc;
     }
 
     /**
      * Create a document using Tidy
      *
      * @param stream - input
      * @param quiet - set Tidy quiet?
      * @param showWarnings - show Tidy warnings?
      * @param report_errors - log errors and throw TidyException?
      * @param isXML - treat document as XML?
      * @param out OutputStream, null if no output required
      * @return the document
      *
      * @throws TidyException if a ParseError is detected and report_errors is true
      */
     private static Document tidyDoc(InputStream stream, boolean quiet, boolean showWarnings, boolean report_errors,
             boolean isXML, OutputStream out) throws TidyException {
         StringWriter sw = new StringWriter();
         Tidy tidy = makeTidyParser(quiet, showWarnings, isXML, sw);
         Document doc = tidy.parseDOM(stream, out);
         doc.normalize();
         if (tidy.getParseErrors() > 0) {
             if (report_errors) {
                 log.error("TidyException: " + sw.toString());
                 throw new TidyException(tidy.getParseErrors(),tidy.getParseWarnings());
             }
             log.warn("Tidy errors: " + sw.toString());
         }
         return doc;
     }
 
     /**
      * Create a Tidy parser with the specified settings.
      *
      * @param quiet - set the Tidy quiet flag?
      * @param showWarnings - show Tidy warnings?
      * @param isXml - treat the content as XML?
      * @param stringWriter - if non-null, use this for Tidy errorOutput
      * @return the Tidy parser
      */
     public static Tidy makeTidyParser(boolean quiet, boolean showWarnings, boolean isXml, StringWriter stringWriter) {
         Tidy tidy = new Tidy();
         tidy.setInputEncoding(StandardCharsets.UTF_8.name());
         tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
         tidy.setQuiet(quiet);
         tidy.setShowWarnings(showWarnings);
         tidy.setMakeClean(true);
         tidy.setXmlTags(isXml);
         if (stringWriter != null) {
             tidy.setErrout(new PrintWriter(stringWriter));
         }
         return tidy;
     }
 
     static class MyErrorHandler implements ErrorHandler {
         private final boolean val;
         private final boolean tol;
 
         private final String type;
 
         MyErrorHandler(boolean validate, boolean tolerate) {
             val = validate;
             tol = tolerate;
             type = "Val=" + val + " Tol=" + tol;
         }
 
         @Override
         public void warning(SAXParseException ex) throws SAXException {
             log.info("Type=" + type + " " + ex);
             if (val && !tol){
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void error(SAXParseException ex) throws SAXException {
             log.warn("Type=" + type + " " + ex);
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void fatalError(SAXParseException ex) throws SAXException {
             log.error("Type=" + type + " " + ex);
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
     }
     
     /**
      * Return value for node
      * @param node Node
      * @return String
      */
     private static String getValueForNode(Node node) {
         StringWriter sw = new StringWriter();
         try {
             Transformer t = TransformerFactory.newInstance().newTransformer();
             t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             t.transform(new DOMSource(node), new StreamResult(sw));
         } catch (TransformerException e) {
             sw.write(e.getMessageAndLocation());
         }
         return sw.toString();
     }
 
     /**
      * Extract NodeList using expression
      * @param document {@link Document}
      * @param xPathExpression XPath expression
      * @return {@link NodeList}
      * @throws TransformerException when the internally used xpath engine fails
      */
     public static NodeList selectNodeList(Document document, String xPathExpression) throws TransformerException {
         XObject xObject = XPathAPI.eval(document, xPathExpression, getPrefixResolver(document));
         return xObject.nodelist();
     }
 
     /**
      * Put in matchStrings results of evaluation
      * @param document XML document
      * @param xPathQuery XPath Query
      * @param matchStrings List of strings that will be filled
      * @param fragment return fragment
      * @throws TransformerException when the internally used xpath engine fails
      */
     public static void putValuesForXPathInList(Document document, 
             String xPathQuery,
             List<String> matchStrings, boolean fragment) throws TransformerException {
+        putValuesForXPathInList(document, xPathQuery, matchStrings, fragment, -1);
+    }
+    
+    
+    /**
+     * Put in matchStrings results of evaluation
+     * @param document XML document
+     * @param xPathQuery XPath Query
+     * @param matchStrings List of strings that will be filled
+     * @param fragment return fragment
+     * @param matchNumber match number
+     * @throws TransformerException when the internally used xpath engine fails
+     */
+    public static void putValuesForXPathInList(Document document, 
+            String xPathQuery,
+            List<String> matchStrings, boolean fragment, 
+            int matchNumber) throws TransformerException {
         String val = null;
         XObject xObject = XPathAPI.eval(document, xPathQuery, getPrefixResolver(document));
         final int objectType = xObject.getType();
         if (objectType == XObject.CLASS_NODESET) {
             NodeList matches = xObject.nodelist();
             int length = matches.getLength();
+            int indexToMatch = matchNumber;
+            if(matchNumber == 0 && length>0) {
+                indexToMatch = JMeterUtils.getRandomInt(length)+1;
+            } 
+            boolean storeAllValues = matchNumber < 0;
             for (int i = 0 ; i < length; i++) {
                 Node match = matches.item(i);
                 if ( match instanceof Element){
                     if (fragment){
                         val = getValueForNode(match);
                     } else {
                         // elements have empty nodeValue, but we are usually interested in their content
                         final Node firstChild = match.getFirstChild();
                         if (firstChild != null) {
                             val = firstChild.getNodeValue();
                         } else {
                             val = match.getNodeValue(); // TODO is this correct?
                         }
                     }
                 } else {
                    val = match.getNodeValue();
                 }
-                matchStrings.add(val);
+                if(storeAllValues || indexToMatch == (i+1)) {
+                    matchStrings.add(val);
+                }
             }
         } else if (objectType == XObject.CLASS_NULL
                 || objectType == XObject.CLASS_UNKNOWN
                 || objectType == XObject.CLASS_UNRESOLVEDVARIABLE) {
             log.warn("Unexpected object type: "+xObject.getTypeString()+" returned for: "+xPathQuery);
         } else {
             val = xObject.toString();
             matchStrings.add(val);
       }
     }
 
     /**
      * 
      * @param document XML Document
      * @return {@link PrefixResolver}
      */
     private static PrefixResolver getPrefixResolver(Document document) {
         PropertiesBasedPrefixResolver propertiesBasedPrefixResolver =
                 new PropertiesBasedPrefixResolver(document.getDocumentElement());
         return propertiesBasedPrefixResolver;
     }
 
     /**
      * Validate xpathString is a valid XPath expression
      * @param document XML Document
      * @param xpathString XPATH String
      * @throws TransformerException if expression fails to evaluate
      */
     public static void validateXPath(Document document, String xpathString) throws TransformerException {
         if (XPathAPI.eval(document, xpathString, getPrefixResolver(document)) == null) {
             // We really should never get here
             // because eval will throw an exception
             // if xpath is invalid, but whatever, better
             // safe
             throw new IllegalArgumentException("xpath eval of '" + xpathString + "' was null");
         }
     }
 
     /**
      * Fills result
      * @param result {@link AssertionResult}
      * @param doc XML Document
      * @param xPathExpression XPath expression
      * @param isNegated flag whether a non-match should be considered a success
      */
     public static void computeAssertionResult(AssertionResult result,
             Document doc, 
             String xPathExpression,
             boolean isNegated) {
         try {
             XObject xObject = XPathAPI.eval(doc, xPathExpression, getPrefixResolver(doc));
             switch (xObject.getType()) {
                 case XObject.CLASS_NODESET:
                     NodeList nodeList = xObject.nodelist();
                     if (nodeList == null || nodeList.getLength() == 0) {
                         if (log.isDebugEnabled()) {
                             log.debug(new StringBuilder("nodeList null no match  ").append(xPathExpression).toString());
                         }
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                         return;
                     }
                     if (log.isDebugEnabled()) {
                         log.debug("nodeList length " + nodeList.getLength());
                         if (!isNegated) {
                             for (int i = 0; i < nodeList.getLength(); i++){
                                 log.debug(new StringBuilder("nodeList[").append(i).append("] ").append(nodeList.item(i)).toString());
                             }
                         }
                     }
                     result.setFailure(isNegated);
                     if (isNegated) {
                         result.setFailureMessage("Specified XPath was found... Turn off negate if this is not desired");
                     }
                     return;
                 case XObject.CLASS_BOOLEAN:
                     if (!xObject.bool()){
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                     }
                     return;
                 default:
                     result.setFailure(true);
                     result.setFailureMessage("Cannot understand: " + xPathExpression);
                     return;
             }
         } catch (TransformerException e) {
             result.setError(true);
             result.setFailureMessage(
                     new StringBuilder("TransformerException: ")
                     .append(e.getMessage())
                     .append(" for:")
                     .append(xPathExpression)
                     .toString());
         }
     }
     
     /**
      * Formats XML
      * @param xml string to format
      * @return String formatted XML
      */
     public static String formatXml(String xml){
         try {
             Transformer serializer= TransformerFactory.newInstance().newTransformer();
             serializer.setOutputProperty(OutputKeys.INDENT, "yes");
             serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
             Source xmlSource = new SAXSource(new InputSource(new StringReader(xml)));
             StringWriter stringWriter = new StringWriter();
             StreamResult res = new StreamResult(stringWriter);
             serializer.transform(xmlSource, res);
             return stringWriter.toString();
         } catch (Exception e) {
             return xml;
         }
     }
 
 }
diff --git a/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java b/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
index eba41be17..1f7c28cf5 100644
--- a/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
+++ b/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
@@ -1,226 +1,271 @@
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
 
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 import java.io.UnsupportedEncodingException;
+
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
+import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 public class TestXPathExtractor {
         private XPathExtractor extractor;
 
         private SampleResult result;
 
         private String data;
         
         private JMeterVariables vars;
 
 
         private JMeterContext jmctx;
 
         private static final String VAL_NAME = "value";
         private static final String VAL_NAME_NR = "value_matchNr";
         
         @Before
         public void setUp() throws UnsupportedEncodingException {
             jmctx = JMeterContextService.getContext();
             extractor = new XPathExtractor();
             extractor.setThreadContext(jmctx);// This would be done by the run command
             extractor.setRefName(VAL_NAME);
             extractor.setDefaultValue("Default");
             result = new SampleResult();
             data = "<book><preface title='Intro'>zero</preface><page>one</page><page>two</page><empty></empty><a><b></b></a></book>";
             result.setResponseData(data.getBytes("UTF-8"));
             vars = new JMeterVariables();
             jmctx.setVariables(vars);
             jmctx.setPreviousResult(result);
         }
 
         @Test
         public void testAttributeExtraction() throws Exception {
             extractor.setXPathQuery("/book/preface/@title");
             extractor.process();
             assertEquals("Intro", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("Intro", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
 
             extractor.setXPathQuery("/book/preface[@title]");
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
 
             extractor.setXPathQuery("/book/preface[@title='Intro']");
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
 
             extractor.setXPathQuery("/book/preface[@title='xyz']");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
             assertNull(vars.get(VAL_NAME+"_1"));
         }
         
         @Test
         public void testVariableExtraction() throws Exception {
             extractor.setXPathQuery("/book/preface");
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
             
             extractor.setXPathQuery("/book/page");
             extractor.process();
             assertEquals("one", vars.get(VAL_NAME));
             assertEquals("2", vars.get(VAL_NAME_NR));
             assertEquals("one", vars.get(VAL_NAME+"_1"));
             assertEquals("two", vars.get(VAL_NAME+"_2"));
             assertNull(vars.get(VAL_NAME+"_3"));
             
+            // Test match 1
+            extractor.setXPathQuery("/book/page");
+            extractor.setMatchNumber(1);
+            extractor.process();
+            assertEquals("one", vars.get(VAL_NAME));
+            assertEquals("1", vars.get(VAL_NAME_NR));
+            assertEquals("one", vars.get(VAL_NAME+"_1"));
+            assertNull(vars.get(VAL_NAME+"_2"));
+            assertNull(vars.get(VAL_NAME+"_3"));
+            
+            // Test match Random
+            extractor.setXPathQuery("/book/page");
+            extractor.setMatchNumber(0);
+            extractor.process();
+            assertEquals("1", vars.get(VAL_NAME_NR));
+            Assert.assertTrue(StringUtils.isNoneEmpty(vars.get(VAL_NAME)));
+            Assert.assertTrue(StringUtils.isNoneEmpty(vars.get(VAL_NAME+"_1")));
+            assertNull(vars.get(VAL_NAME+"_2"));
+            assertNull(vars.get(VAL_NAME+"_3"));
+            
+            // Put back default value
+            extractor.setMatchNumber(-1);
+            
             extractor.setXPathQuery("/book/page[2]");
             extractor.process();
             assertEquals("two", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("two", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
             assertNull(vars.get(VAL_NAME+"_3"));
 
             extractor.setXPathQuery("/book/index");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
             assertNull(vars.get(VAL_NAME+"_1"));
 
             // Has child, but child is empty
             extractor.setXPathQuery("/book/a");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertNull(vars.get(VAL_NAME+"_1"));
 
             // Has no child
             extractor.setXPathQuery("/book/empty");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertNull(vars.get(VAL_NAME+"_1"));
 
             // No text
             extractor.setXPathQuery("//a");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
 
+            // No text all matches
+            extractor.setXPathQuery("//a");
+            extractor.process();
+            extractor.setMatchNumber(-1);
+            assertEquals("Default", vars.get(VAL_NAME));
+
+            // No text match second
+            extractor.setXPathQuery("//a");
+            extractor.process();
+            extractor.setMatchNumber(2);
+            assertEquals("Default", vars.get(VAL_NAME));
+
+            // No text match random
+            extractor.setXPathQuery("//a");
+            extractor.process();
+            extractor.setMatchNumber(0);
+            assertEquals("Default", vars.get(VAL_NAME));
+
+            extractor.setMatchNumber(-1);
             // Test fragment
             extractor.setXPathQuery("/book/page[2]");
             extractor.setFragment(true);
             extractor.process();
             assertEquals("<page>two</page>", vars.get(VAL_NAME));
             // Now get its text
             extractor.setXPathQuery("/book/page[2]/text()");
             extractor.process();
             assertEquals("two", vars.get(VAL_NAME));
 
             // No text, but using fragment mode
             extractor.setXPathQuery("//a");
             extractor.process();
             assertEquals("<a><b/></a>", vars.get(VAL_NAME));
         }
 
         @Test
         public void testScope(){
             extractor.setXPathQuery("/book/preface");
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));            
 
             extractor.setScopeChildren(); // There aren't any
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
             assertNull(vars.get(VAL_NAME+"_1"));
 
             extractor.setScopeAll(); // same as Parent
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));            
 
             // Try to get data from subresult
             result.sampleStart(); // Needed for addSubResult()
             result.sampleEnd();
             SampleResult subResult = new SampleResult();
             subResult.sampleStart();
             subResult.setResponseData(result.getResponseData());
             subResult.sampleEnd();
             result.addSubResult(subResult);
             
             
             // Get data from both
             extractor.setScopeAll();
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("2", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertEquals("zero", vars.get(VAL_NAME+"_2"));
             assertNull(vars.get(VAL_NAME+"_3"));
 
             // get data from child
             extractor.setScopeChildren();
             extractor.process();
             assertEquals("zero", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("zero", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
             
         }
 
         @Test
         public void testInvalidXpath() throws Exception {
             extractor.setXPathQuery("<");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
         }
 
         @Test
         public void testInvalidDocument() throws Exception {
             result.setResponseData("<z>", null);
             extractor.setXPathQuery("<");
             extractor.process();
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
         }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index f3e2dc149..e98c15407 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,307 +1,308 @@
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
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
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
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60590</bug>BackendListener : Add Influxdb BackendListenerClient implementation to JMeter. Partly based on <pr>246</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60591</bug>BackendListener : Add a time boxed sampling. Based on a <pr>237</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
+    <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
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
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.8 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to jodd 3.8.1 (from 3.8.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.22 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Converted the old pdf tutorials to xml.</li>
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
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
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
index f148f37ad..75ade087a 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -4608,2261 +4608,2268 @@ See the file <code>BeanShellListeners.bshrc</code> for example definitions.
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
           <li><code>parameter values group number</code> - group number of regular expression for parameter values, would be <code>2</code> in our example</li>
         </ul>
       </li>
     </ol>
     </example>
     
     <p>See also the <complink name="Regular Expression Extractor"/> element, which is used to extract parametes names and values</p>
 </component>
 <links>
         <link href="../demos/RegEx-User-Parameters.jmx">Test Plan showing how to use RegEx User Parameters</link>
 </links>
 
 <component name="Sample Timeout" index="&sect-num;.7.11" anchor="interrupt" width="316" height="138" screenshot="sample_timeout.png">
 <description>
 <note>BETA CODE - the test element may be moved or replaced in a future release</note>
 <p>This Pre-Processor schedules a timer task to interrupt a sample if it takes too long to complete.
 The timeout is ignored if it is zero or negative.
 For this to work, the sampler must implement Interruptible. 
 The following samplers are known to do so:<br></br>
 AJP, BeanShell, FTP, HTTP, Soap, AccessLog, MailReader, JMS Subscriber, TCPSampler, TestAction, JavaSampler
 </p>
 <p>
 The test element is intended for use where individual timeouts such as Connection Timeout or Response Timeout are insufficient,
 or where the Sampler does not support timeouts.
 The timeout should be set sufficiently long so that it is not triggered in normal tests, but short enough that it interrupts samples
 that are stuck.
 </p>
 <p>
 [By default, JMeter uses a Callable to interrupt the sampler.
 This executes in the same thread as the timer, so if the interrupt takes a long while, 
 it may delay the processing of subsequent timeouts.
 This is not expected to be a problem, but if necessary the property <code>InterruptTimer.useRunnable</code>
 can be set to <code>true</code> to use a separate Runnable thread instead of the Callable.]
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Sample Timeout" required="Yes">If the sample takes longer to complete, it will be interrupted.</property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.8 Post-Processors" anchor="postprocessors">
     <description>
     <p>
         As the name suggests, Post-Processors are applied after samplers. Note that they are
         applied to <b>all</b> the samplers in the same scope, so to ensure that a post-processor
         is applied only to a particular sampler, add it as a child of the sampler.
     </p>
     <note>
     Note: Unless documented otherwise, Post-Processors are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of JSR223 and BeanShell post-processors, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </note>
     <p>
     Post-Processors are run before Assertions, so they do not have access to any Assertion Results, nor will
     the sample status reflect the results of any Assertions. If you require access to Assertion Results, try
     using a Listener instead. Also note that the variable <code>JMeterThread.last_sample_ok</code> is set to "<code>true</code>" or "<code>false</code>"
     after all Assertions have been run.
     </p>
     </description>
 <component name="Regular Expression Extractor" index="&sect-num;.8.1"  width="1127" height="277" screenshot="regex_extractor.png">
 <description><p>Allows the user to extract values from a server response using a Perl-type regular expression.  As a post-processor,
 this element will execute after each Sample request in its scope, applying the regular expression, extracting the requested values,
 generate the template string, and store the result into the given variable name.</p></description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         Matching is applied to all qualifying samples in turn.
         For example if there is a main sample and 3 sub-samples, each of which contains a single match for the regex,
         (i.e. 4 matches in total).
         For match number = <code>3</code>, Sub-samples only, the extractor will match the 3<sup>rd</sup> sub-sample.
         For match number = <code>3</code>, Main sample and sub-samples, the extractor will match the 2<sup>nd</sup> sub-sample (1<sup>st</sup> match is main sample).
         For match number = <code>0</code> or negative, all qualifying samples will be processed.
         For match number > <code>0</code>, matching will stop as soon as enough matches have been found.
         </property>
         <property name="Field to check" required="Yes">
         The following fields can be checked:
         <ul>
         <li><code>Body</code> - the body of the response, e.g. the content of a web-page (excluding headers)</li>
         <li><code>Body (unescaped)</code> - the body of the response, with all Html escape codes replaced.
         Note that Html escapes are processed without regard to context, so some incorrect substitutions
         may be made.
         <note>Note that this option highly impacts performances, so use it only when absolutely necessary and be aware of its impacts</note>
         </li>
         <li><code>Body as a Document</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).
         <note>Note that the Body as a Document option can impact performances, so ensure it is OK for your test</note>
         </li>
         <li><code>Request Headers</code> - may not be present for non-HTTP samples</li>
         <li><code>Response Headers</code> - may not be present for non-HTTP samples</li>
         <li><code>URL</code></li>
         <li><code>Response Code</code> - e.g. <code>200</code></li>
         <li><code>Response Message</code> - e.g. <code>OK</code></li>
         </ul>
         Headers can be useful for HTTP samples; it may not be present for other sample types.
         </property>
         <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.  Also note that each group is stored as <code>[refname]_g#</code>, where <code>[refname]</code> is the string you entered as the reference name, and <code>#</code> is the group number, where group <code>0</code> is the entire match, group <code>1</code> is the match from the first set of parentheses, etc.</property>
         <property name="Regular Expression" required="Yes">The regular expression used to parse the response data. 
         This must contain at least one set of parentheses "<code>()</code>" to capture a portion of the string, unless using the group <code>$0$</code>.
         Do not enclose the expression in <code>/ /</code> - unless of course you want to match these characters as well.
         </property>
         <property name="Template" required="Yes">The template used to create a string from the matches found.  This is an arbitrary string
         with special elements to refer to groups within the regular expression.  The syntax to refer to a group is: '<code>$1$</code>' to refer to
         group <code>1</code>, '<code>$2$</code>' to refer to group <code>2</code>, etc. <code>$0$</code> refers to whatever the entire expression matches.</property>
-        <property name="Match No." required="Yes">Indicates which match to use.  The regular expression may match multiple times.  
+        <property name="Match No. (0 for Random)" required="Yes">Indicates which match to use.  The regular expression may match multiple times.  
             <ul>
                 <li>Use a value of zero to indicate JMeter should choose a match at random.</li>
                 <li>A positive number N means to select the n<sup>th</sup> match.</li>
                 <li> Negative numbers are used in conjunction with the <complink name="ForEach Controller"/> - see below.</li>
             </ul>
         </property>
         <property name="Default Value" required="No, but recommended">
         If the regular expression does not match, then the reference variable will be set to the default value.
         This is particularly useful for debugging tests. If no default is provided, then it is difficult to tell
         whether the regular expression did not match, or the RE element was not processed or maybe the wrong variable
         is being used.
         <p>
         However, if you have several test elements that set the same variable, 
         you may wish to leave the variable unchanged if the expression does not match.
         In this case, remove the default value once debugging is complete.
         </p> 
         </property>
         <property name="Use empty default value" required="No">
         If the checkbox is checked and <code>Default Value</code> is empty, then JMeter will set the variable to empty string instead of not setting it.
         Thus when you will for example use <code>${var}</code> (if <code>Reference Name</code> is var) in your Test Plan, if the extracted value is not found then 
         <code>${var}</code> will be equal to empty string instead of containing <code>${var}</code> which may be useful if extracted value is optional.
         </property>
 </properties>
 <p>
     If the match number is set to a non-negative number, and a match occurs, the variables are set as follows:
 </p>
     <ul>
         <li><code>refName</code> - the value of the template</li>
         <li><code>refName_g<em>n</em></code>, where <code>n</code>=<code>0</code>,<code>1</code>,<code>2</code> - the groups for the match</li>
         <li><code>refName_g</code> - the number of groups in the Regex (excluding <code>0</code>)</li>
     </ul>
 <p>
     If no match occurs, then the <code>refName</code> variable is set to the default (unless this is absent). 
     Also, the following variables are removed:
 </p>
     <ul>
         <li><code>refName_g0</code></li>
         <li><code>refName_g1</code></li>
         <li><code>refName_g</code></li>
     </ul>
 <p>
     If the match number is set to a negative number, then all the possible matches in the sampler data are processed.
     The variables are set as follows:
 </p>
     <ul>
         <li><code>refName_matchNr</code> - the number of matches found; could be <code>0</code></li>
         <li><code>refName_<em>n</em></code>, where <code>n</code> = <code>1</code>, <code>2</code>, <code>3</code> etc. - the strings as generated by the template</li>
         <li><code>refName_<em>n</em>_g<em>m</em></code>, where <code>m</code>=<code>0</code>, <code>1</code>, <code>2</code> - the groups for match <code>n</code></li>
         <li><code>refName</code> - always set to the default value</li>
         <li><code>refName_g<em>n</em></code> - not set</li>
     </ul>
 <p>
     Note that the <code>refName</code> variable is always set to the default value in this case, 
     and the associated group variables are not set.
 </p>
     <p>See also <complink name="Response Assertion"/> for some examples of how to specify modifiers,
     and <a href="regular_expressions.html"> for further information on JMeter regular expressions.</a></p>
 </component>
 
 <component name="CSS/JQuery Extractor" index="&sect-num;.8.2"  width="826" height="276" screenshot="css_extractor_attr.png">
 <description><p>Allows the user to extract values from a server response using a CSS/JQuery selector like syntax.  As a post-processor,
 this element will execute after each Sample request in its scope, applying the CSS/JQuery expression, extracting the requested nodes,
 extracting the node as text or attribute value and store the result into the given variable name.</p></description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         Matching is applied to all qualifying samples in turn.
         For example if there is a main sample and 3 sub-samples, each of which contains a single match for the regex,
         (i.e. 4 matches in total).
         For match number = <code>3</code>, Sub-samples only, the extractor will match the 3<sup>rd</sup> sub-sample.
         For match number = <code>3</code>, Main sample and sub-samples, the extractor will match the 2<sup>nd</sup> sub-sample (1<sup>st</sup> match is main sample).
         For match number = <code>0</code> or negative, all qualifying samples will be processed.
         For match number > <code>0</code>, matching will stop as soon as enough matches have been found.
         </property>
         <property name="CSS/JQuery extractor Implementation" required="False">
         2 Implementations for CSS/JQuery based syntax are supported:
         <ul>
                 <li><a href="http://jsoup.org/" >JSoup</a></li>
                 <li><a href="http://jodd.org/doc/lagarto/index.html" >Jodd-Lagarto (CSSelly)</a></li>
         </ul>
         If selector is set to empty, default implementation(JSoup) will be used.
         </property>
         <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.</property>
         <property name="CSS/JQuery expression" required="Yes">The CSS/JQuery selector used to select nodes from the response data. 
         Selector, selectors combination and pseudo-selectors are supported, examples:
         <ul>
             <li><code>E[foo]</code> - an <code>E</code> element with a "<code>foo</code>" attribute</li>
             <li><code>ancestor child</code> - child elements that descend from ancestor, e.g. <code>.body p</code> finds <code>p</code> elements anywhere under a block with class "<code>body</code>"</li>
             <li><code>:lt(n)</code> - find elements whose sibling index (i.e. its position in the DOM tree relative to its parent) is less than <code>n</code>; e.g. <code>td:lt(3)</code></li>
             <li><code>:contains(text)</code> - find elements that contain the given <code>text</code>. The search is case-insensitive; e.g. <code>p:contains(jsoup)</code></li>
             <li>&hellip;</li>
         </ul>
         For more details on syntax, see:
             <ul>
                 <li><a href="http://jsoup.org/cookbook/extracting-data/selector-syntax" >JSoup</a></li>
                 <li><a href="http://jodd.org/doc/csselly/" >Jodd-Lagarto (CSSelly)</a></li>
             </ul>
         </property>
         <property name="Attribute" required="false">
             Name of attribute (as per HTML syntax) to extract from nodes that matched the selector. If empty, then the combined text of this element and all its children will be returned.<br/>
             This is the equivalent <a href="http://jsoup.org/apidocs/org/jsoup/nodes/Node.html#attr%28java.lang.String%29">Element#attr(name)</a> function for JSoup if an atttribute is set.<br/>
             <figure width="826" height="275" image="css_extractor_attr.png">CSS Extractor with attribute value set</figure><br/>
             If empty this is the equivalent of <a href="http://jsoup.org/apidocs/org/jsoup/nodes/Element.html#text%28%29">Element#text()</a> function for JSoup if not value is set for attribute.
             <figure width="825" height="275" image="css_extractor_noattr.png">CSS Extractor with no attribute set</figure>
         </property>
-        <property name="Match No." required="Yes">Indicates which match to use.  The CSS/JQuery selector may match multiple times.  
+        <property name="Match No. (0 for Random)" required="Yes">Indicates which match to use.  The CSS/JQuery selector may match multiple times.  
             <ul>
                 <li>Use a value of zero to indicate JMeter should choose a match at random.</li>
                 <li>A positive number <code>N</code> means to select the n<sup>th</sup> match.</li>
                 <li> Negative numbers are used in conjunction with the <complink name="ForEach Controller"/> - see below.</li>
             </ul>
         </property>
         <property name="Default Value" required="No, but recommended">
         If the expression does not match, then the reference variable will be set to the default value.
         This is particularly useful for debugging tests. If no default is provided, then it is difficult to tell
         whether the expression did not match, or the CSS/JQuery element was not processed or maybe the wrong variable
         is being used.
         <p>
         However, if you have several test elements that set the same variable, 
         you may wish to leave the variable unchanged if the expression does not match.
         In this case, remove the default value once debugging is complete.
         </p> 
         </property>
         <property name="Use empty default value" required="No">
         If the checkbox is checked and <code>Default Value</code> is empty, then JMeter will set the variable to empty string instead of not setting it.
         Thus when you will for example use <code>${var}</code> (if <code>Reference Name</code> is var) in your Test Plan, if the extracted value is not found then 
         <code>${var}</code> will be equal to empty string instead of containing <code>${var}</code> which may be useful if extracted value is optional.
         </property>
 </properties>
 <p>
     If the match number is set to a non-negative number, and a match occurs, the variables are set as follows:
 </p>
     <ul>
         <li><code>refName</code> - the value of the template</li>
     </ul>
 <p>
     If no match occurs, then the <code>refName</code> variable is set to the default (unless this is absent). 
 </p>
 <p>
     If the match number is set to a negative number, then all the possible matches in the sampler data are processed.
     The variables are set as follows:
 </p>
     <ul>
         <li><code>refName_matchNr</code> - the number of matches found; could be <code>0</code></li>
         <li><code>refName_n</code>, where <code>n</code> = <code>1</code>, <code>2</code>, <code>3</code>, etc. - the strings as generated by the template</li>
         <li><code>refName</code> - always set to the default value</li>
     </ul>
 <p>
     Note that the refName variable is always set to the default value in this case.
 </p>
 </component>
 
 <component name="XPath Extractor" index="&sect-num;.8.3"  width="729" height="317" screenshot="xpath_extractor.png">
     <description>This test element allows the user to extract value(s) from 
         structured response - XML or (X)HTML - using XPath
         query language.
    </description>
    <properties>
        <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
        <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - only applies to the main sample</li>
         <li><code>Sub-samples only</code> - only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         XPath matching is applied to all qualifying samples in turn, and all the matching results will be returned.
        </property>
        <property name="Use Tidy (tolerant parser)" required="Yes">If checked use Tidy to parse HTML response into XHTML.
        <ul>
            <li>"<code>Use Tidy</code>" should be checked on for HTML response. Such response is converted to valid XHTML (XML compatible HTML) using Tidy</li>
            <li>"<code>Use Tidy</code>" should be unchecked for both XHTML or XML response (for example RSS)</li>
        </ul>
        </property>
 <property name="Quiet"  required="If Tidy is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"  required="If Tidy is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"  required="If Tidy is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces" required="If Tidy is not selected">
         If checked, then the XML parser will use namespace resolution.(see note below on NAMESPACES)
         Note that currently only namespaces declared on the root element will be recognised.
         A later version of JMeter may support user-definition of additional workspace names.
         Meanwhile, a work-round is to replace: 
         <source>//mynamespace:tagname</source>
         by
         <source>//*[local-name()='tagname' and namespace-uri()='uri-for-namespace']</source>
         where "<code>uri-for-namespace</code>" is the uri for the "<code>mynamespace</code>" namespace.
         (not applicable if Tidy is selected)
         
 </property>
     <property name="Validate XML"   required="If Tidy is not selected">Check the document against its schema.</property>
     <property name="Ignore Whitespace"  required="If Tidy is not selected">Ignore Element Whitespace.</property>
     <property name="Fetch External DTDs"  required="If Tidy is not selected">If selected, external DTDs are fetched.</property>
     <property name="Return entire XPath fragment instead of text content?" required="Yes">
     If selected, the fragment will be returned rather than the text content.<br></br>
     For example <code>//title</code> would return "<code>&lt;title&gt;Apache JMeter&lt;/title&gt;</code>" rather than "<code>Apache JMeter</code>".<br></br>
     In this case, <code>//title/text()</code> would return "<code>Apache JMeter</code>".
     </property>
     <property name="Reference Name" required="Yes">The name of the JMeter variable in which to store the result.</property>
     <property name="XPath Query" required="Yes">Element query in XPath language. Can return more than one match.</property>
+    <property name="Match No. (0 for Random)" required="No">If the XPath Path query leads to many results, you can choose which one(s) to extract as Variables:
+    <ul>
+        <li><code>0</code> : means random</li>
+        <li><code>-1</code> means extract all results (default value), they will be named as <code><em>&lt;variable name&gt;</em>_N</code> (where <code>N</code> goes from 1 to Number of results)</li>
+        <li><code>X</code> : means extract the X<sup>th</sup> result. If this X<sup>th</sup> is greater than number of matches, then nothing is returned. Default value will be used</li>
+    </ul>
+    </property>
     <property name="Default Value" required="">Default value returned when no match found. 
     It is also returned if the node has no value and the fragment option is not selected.</property>
    </properties>
    <p>To allow for use in a <complink name="ForEach Controller"/>, the following variables are set on return:</p>
    <ul>
    <li><code>refName</code> - set to first (or only) match; if no match, then set to default</li>
    <li><code>refName_matchNr</code> - set to number of matches (may be <code>0</code>)</li>
    <li><code>refName_n</code> - <code>n</code>=<code>1</code>, <code>2</code>, <code>3</code>, etc. Set to the 1<sup>st</sup>, 2<sup>nd</sup> 3<sup>rd</sup> match etc. 
    </li>
    </ul>
    <note>Note: The next <code>refName_n</code> variable is set to <code>null</code> - e.g. if there are 2 matches, then <code>refName_3</code> is set to <code>null</code>,
    and if there are no matches, then <code>refName_1</code> is set to <code>null</code>.
    </note>
    <p>XPath is query language targeted primarily for XSLT transformations. However it is useful as generic query language for structured data too. See 
        <a href="http://www.topxml.com/xsl/xpathref.asp">XPath Reference</a> or <a href="http://www.w3.org/TR/xpath">XPath specification</a> for more information. Here are few examples:
    </p>
   <dl> 
    <dt><code>/html/head/title</code></dt>
      <dd>extracts title element from HTML response</dd>
    <dt><code>/book/page[2]</code></dt>
      <dd>extracts 2<sup>nd</sup> page from a book</dd>
    <dt><code>/book/page</code></dt>
      <dd>extracts all pages from a book</dd>
      <dt><code>//form[@name='countryForm']//select[@name='country']/option[text()='Czech Republic'])/@value</code></dt>
     <dd>extracts value attribute of option element that match text '<code>Czech Republic</code>'
         inside of select element with name attribute  '<code>country</code>' inside of
         form with name attribute '<code>countryForm</code>'</dd>
  </dl>
  <note>When "<code>Use Tidy</code>" is checked on - resulting XML document may slightly differ from original HTML response:
      <ul>
          <li>All elements and attribute names are converted to lowercase</li>
          <li>Tidy attempts to correct improperly nested elements. For example - original (incorrect) <code>ul/font/li</code> becomes correct <code>ul/li/font</code></li>
      </ul>
      See <a href="http://jtidy.sf.net">Tidy homepage</a> for more information.
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
 
 <component name="Result Status Action Handler" index="&sect-num;.8.4"  width="613" height="133" screenshot="resultstatusactionhandler.png">
    <description>This test element allows the user to stop the thread or the whole test if the relevant sampler failed.
    </description>
    <properties>
    <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
    <property name="Action to be taken after a Sampler error">
    Determines what happens if a sampler error occurs, either because the sample itself failed or an assertion failed.
    The possible choices are:
    <ul>
    <li><code>Continue</code> - ignore the error and continue with the test</li>
    <li><code>Start next thread loop</code> - does not execute samplers following the sampler in error for the current iteration and restarts the loop on next iteration</li>
    <li><code>Stop Thread</code> - current thread exits</li>
    <li><code>Stop Test</code> - the entire test is stopped at the end of any current samples.</li>
    <li><code>Stop Test Now</code> - the entire test is stopped abruptly. Any current samplers are interrupted if possible.</li>
    </ul>
    </property>
    </properties>
 </component>
 
 <component name="BeanShell PostProcessor"  index="&sect-num;.8.5"  width="847" height="633" screenshot="beanshell_postprocessor.png">
 <description>
 <p>
 The BeanShell PreProcessor allows arbitrary code to be applied after taking a sample.
 </p>
 <p>BeanShell Post-Processor no longer ignores samples with zero-length result data</p>
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
 <p>The following BeanShell variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult</li>
 <li><code>data</code> - (byte [])- gives access to the current sample data</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.postprocessor.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 <component name="BSF PostProcessor (DEPRECATED)" index="&sect-num;.8.6"  width="844" height="633" screenshot="bsf_postprocessor.png">
 <description>
 <p>
 The BSF PostProcessor allows BSF script code to be applied after taking a sample.
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
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
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
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 PostProcessor" index="&sect-num;.8.7">
 <description>
 <p>
 The JSR223 PostProcessor allows JSR223 script code to be applied after taking a sample.
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
 <p>
 Before invoking the script, some variables are set up.
 Note that these are JSR223 variables - i.e. they can be used directly in the script.
 </p>
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
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the current sampler</li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JDBC PostProcessor" index="&sect-num;.8.8">
 <description>
 <p>
 The JDBC PostProcessor enables you to run some SQL statement just after a sample has run.
 This can be useful if your JDBC Sample changes some data and you want to reset state to what it was before the JDBC sample run.
 </p>
 </description>
 <links>
         <link href="../demos/JDBC-Pre-Post-Processor.jmx">Test Plan using JDBC Pre/Post Processor</link>
 </links>
 <p>
 In the linked test plan, "<code>JDBC PostProcessor</code>" JDBC PostProcessor calls a stored procedure to delete from Database the Price Cut-Off that was created by PreProcessor.
 </p>
 <figure width="818" height="399" image="jdbc-post-processor.png">JDBC PostProcessor</figure>
 </component>
 
 <component name="JSON Extractor" index="&sect-num;.8.9" anchor="JSON_Path_PostProcessor">
 <description>
 <p>
 The JSON PostProcessor enables you extract data from JSON responses using JSON-PATH syntax. This post processor is very similar to Regular expression extractor.
 It must be placed as a child of HTTP Sampler or any other sampler that has responses.
 It will allow you to extract in a very easy way text content, see <a href="http://goessner.net/articles/JsonPath/" >JSON Path syntax</a>.
 
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Variable Names" required="Yes">Semi-colon separated names of variables that will contain the results of JSON-PATH expressions (must match number of JSON-PATH expressions)</property>
     <property name="JSON Path Expressions" required="Yes">Semi-colon separated JSON-PATH expressions (must match number of variables)</property>
     <property name="Default Values" required="No">Semi-colon separated default values if JSON-PATH expressions do not return any result(must match number of variables)</property>
-    <property name="Match Numbers" required="No">If the JSON Path query leads to many results, you can choose which one(s) to extract as Variables:
+    <property name="Match No. (0 for Random)" required="No">If the JSON Path query leads to many results, you can choose which one(s) to extract as Variables:
     <ul>
         <li><code>0</code> : means random (Default Value)</li>
         <li><code>-1</code> means extract all results, they will be named as <code><em>&lt;variable name&gt;</em>_N</code> (where <code>N</code> goes from 1 to Number of results)</li>
         <li><code>X</code> : means extract the X<sup>th</sup> result. If this X<sup>th</sup> is greater than number of matches, then nothing is returned. Default value will be used</li>
     </ul>
     </property>
     <property name="Compute concatenation var" required="No">If many results are found, plugin will concatenate them using ‘<code>,</code>’ separator and store it in a var named <code><em>&lt;variable name&gt;</em>_ALL</code></property>
 </properties>
 <figure width="855" height="276" image="json-post-processor.png">JSON PostProcessor</figure>
 </component>
 
 </section>
 
 <section name="&sect-num;.9 Miscellaneous Features" anchor="Miscellaneous_Features">
 <description>
         <br></br>
 </description>
 <component name="Test Plan" index="&sect-num;.9.1"  width="560" height="457" screenshot="testplan.png">
 <description>
 <p>
 The Test Plan is where the overall settings for a test are specified.
 </p>
 <p>
 Static variables can be defined for values that are repeated throughout a test, such as server names.
 For example the variable <code>SERVER</code> could be defined as <code>www.example.com</code>, and the rest of the test plan
 could refer to it as <code>${SERVER}</code>. This simplifies changing the name later.
 </p>
 <p>
 If the same variable name is reused on one of more
 <complink name="User Defined Variables"/> Configuration elements,
 the value is set to the last definition in the test plan (reading from top to bottom).
 Such variables should be used for items that may change between test runs, 
 but which remain the same during a test run.
 </p>
 <p>
 <note>Note that the Test Plan cannot refer to variables it defines.</note> 
 If you need to construct other variables from the Test Plan variables, 
 use a <complink name="User Defined Variables"/> test element.
 </p>
 <p>
 Selecting Functional Testing instructs JMeter to save the  additional sample information
 - Response Data and Sampler Data - to all result files.
 This increases the resources needed to run a test, and may adversely impact JMeter performance.
 If more data is required for a particular sampler only, then add a Listener to it, and configure the fields as required.
 <note>The option does not affect CSV result files, which cannot currently store such information.</note>
 </p>
 <p>Also, an option exists here to instruct JMeter to run the <complink name="Thread Group"/> serially rather than in parallel.</p>
 <p>Run tearDown Thread Groups after shutdown of main threads: 
 if selected, the tearDown groups (if any) will be run after graceful shutdown of the main threads.
 The tearDown threads won't be run if the test is forcibly stopped.
 </p>
 <p>
 Test plan now provides an easy way to add classpath setting to a specific test plan. 
 The feature is additive, meaning that you can add jar files or directories,
 but removing an entry requires restarting JMeter.
 <note>Note that this cannot be used to add JMeter GUI plugins, because they are processed earlier.</note>
 However it can be useful for utility jars such as JDBC drivers. The jars are only added to
 the search path for the JMeter loader, not for the system class loader.
 </p>
 <p>
 JMeter properties also provides an entry for loading additional classpaths.
 In <code>jmeter.properties</code>, edit "<code>user.classpath</code>" or "<code>plugin_dependency_paths</code>" to include additional libraries.
 See <a href="get-started.html#classpath">JMeter's Classpath</a> and
 <a href="get-started.html#configuring_jmeter">Configuring JMeter</a> for details.
 </p>
 </description>
 </component>
 
 <component name="Thread Group" index="&sect-num;.9.2"  width="706" height="407" screenshot="threadgroup.png">
 <description>
 <p>A Thread Group defines a pool of users that will execute a particular test case against your server.  In the Thread Group GUI, you can control the number of users simulated (num of threads), the ramp up time (how long it takes to start all the threads), the number of times to perform the test, and optionally, a start and stop time for the test.</p>
 <p>
 See also <complink name="tearDown Thread Group"/> and <complink name="setUp Thread Group"/>.
 </p>
 <p>
 When using the scheduler, JMeter runs the thread group until either the number of loops is reached or the duration/end-time is reached - whichever occurs first.
 Note that the condition is only checked between samples; when the end condition is reached, that thread will stop.
 JMeter does not interrupt samplers which are waiting for a response, so the end time may be delayed arbitrarily.
 </p>
 </description>
 <p>
 Since JMeter 3.0, you can run a selection of Thread Group by selecting them and right clicking. A popup menu will appear:
 <figure width="461" height="818" image="threadgroup-popup-menu.png">Popup menu to start a selection of Thread Groups</figure>
 
 <br/>Notice you have 3 options to run the selection of Thread Groups:
 <ul>
 <li>Start : Start the selected thread groups only</li>
 <li>Start no pauses : Start the selected thread groups only but without running the timers</li>
 <li>Validate : Start the selected thread groups only using validation mode. Per default this runs the Thread Group in validation mode (see below)</li>
 </ul> 
 
 <b>Validation Mode:</b><br></br>
 This mode enables rapid validation of a Thread Group by running it with 1 thread, 1 iteration, no timers and no <code>Startup delay</code> set to 0.
 Behaviour can be modified with some properties by setting in user.properties:
 <ul>
 <li><code>testplan_validation.nb_threads_per_thread_group</code> : Number of threads to use to validate a Thread Group, by default 1</li>
 <li><code>testplan_validation.ignore_timers</code> : Ignore timers when validating the thread group of plan, by default 1</li>
 <li><code>testplan_validation.number_iterations</code> : Number of iterations to use to validate a Thread Group</li>
 <li><code>testplan_validation.tpc_force_100_pct</code> : Wether to force Throughput Controller in percentage mode to run as if percentage was 100%. Defaults to false</li>
 </ul> 
 </p>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Action to be taken after a Sampler error">
         Determines what happens if a sampler error occurs, either because the sample itself failed or an assertion failed.
         The possible choices are:
         <ul>
         <li><code>Continue</code> - ignore the error and continue with the test</li>
         <li><code>Start Next Loop</code> - ignore the error, start next loop and continue with the test</li>
         <li><code>Stop Thread</code> - current thread exits</li>
         <li><code>Stop Test</code> - the entire test is stopped at the end of any current samples.</li>
         <li><code>Stop Test Now</code> - the entire test is stopped abruptly. Any current samplers are interrupted if possible.</li>
         </ul>
         </property>
         <property name="Number of Threads" required="Yes">Number of users to simulate.</property>
         <property name="Ramp-up Period" required="Yes">How long JMeter should take to get all the threads started.  If there are 10 threads and a ramp-up time of 100 seconds, then each thread will begin 10 seconds after the previous thread started, for a total time of 100 seconds to get the test fully up to speed.</property>
         <property name="Loop Count" required="Yes, unless forever is selected">Number of times to perform the test case.  Alternatively, "<code>forever</code>" can be selected causing the test to run until manually stopped.</property>
         <property name="Delay Thread creation until needed" required="Yes">
         If selected, threads are created only when the appropriate proportion of the ramp-up time has elapsed.
         This is most appropriate for tests with a ramp-up time that is significantly longer than the time to execute a single thread.
         I.e. where earlier threads finish before later ones start. 
         <br></br>
         If not selected, all threads are created when the test starts (they then pause for the appropriate proportion of the ramp-up time).
         This is the original default, and is appropriate for tests where threads are active throughout most of the test.
         </property>
         <property name="Scheduler" required="Yes">If selected, enables the scheduler</property>
         <property name="Start Time" required="No">If the scheduler checkbox is selected, one can choose an absolute start time.  When you start your test, JMeter will wait until the specified start time to begin testing.
             Note: the <code>Startup Delay</code> field over-rides this - see below.
             </property>
         <property name="End Time" required="No">If the scheduler checkbox is selected, one can choose an absolute end time.  When you start your test, JMeter will wait until the specified start time to begin testing, and it will stop at the specified end time.
             Note: the <code>Duration</code> field over-rides this - see below.
             </property>
         <property name="Duration (seconds)" required="No">
             If the scheduler checkbox is selected, one can choose a relative end time. 
             JMeter will use this to calculate the End Time, and ignore the <code>End Time</code> value.
         </property>
         <property name="Startup delay (seconds)" required="No">
             If the scheduler checkbox is selected, one can choose a relative startup delay.
             JMeter will use this to calculate the Start Time, and ignore the <code>Start Time</code> value.
         </property>
 </properties>
 </component>
 
 <component name="WorkBench" index="&sect-num;.9.3"  width="384" height="103" screenshot="workbench.png">
 <description>
 <p>The WorkBench simply provides a place to temporarily store test elements while not in use, for copy/paste purposes, or any other purpose you desire. 
 When you save your test plan, WorkBench items are not saved with it by default unless you check "<code>Save Workbench</code>" option.
 Your WorkBench can be saved independently, if you like (right-click on <code>WorkBench</code> and choose <code>Save</code>).</p>
 <p>Certain test elements are only available on the WorkBench:</p>
 <ul>
 <li><complink name="HTTP(S) Test Script Recorder"/></li>
 <li><complink name="HTTP Mirror Server"/></li>
 <li><complink name="Property Display"/></li>
 </ul>
 <properties>
          <property name="Save WorkBench" required="No">
                 Allow to save the WorkBench's elements into the JMX file.
         </property>
 </properties>
 </description>
 </component>
 
 <component name="SSL Manager" index="&sect-num;.9.4" screenshot="">
 <p>
   The SSL Manager is a way to select a client certificate so that you can test
   applications that use Public Key Infrastructure (PKI).
   It is only needed if you have not set up the appropriate System properties.
 </p>
 
 <b>Choosing a Client Certificate</b>
 <p>
   You may either use a Java Key Store (JKS) format key store, or a Public Key
   Certificate Standard #12 (PKCS12) file for your client certificates.  There
   is a feature of the JSSE libraries that require you to have at least a six character
   password on your key (at least for the keytool utility that comes with your
   JDK).
 </p>
 <p>
   To select the client certificate, choose <menuchoice><guimenuitem>Options</guimenuitem>
   <guimenuitem>SSL Manager</guimenuitem></menuchoice> from the menu bar.
   You will be presented with a file finder that looks for PKCS12 files by default.
   Your PKCS12 file must have the extension '<code>.p12</code>' for SSL Manager to recognize it
   as a PKCS12 file.  Any other file will be treated like an average JKS key store.
   If JSSE is correctly installed, you will be prompted for the password.  The text
   box does not hide the characters you type at this point -- so make sure no one is
   looking over your shoulder.  The current implementation assumes that the password
   for the keystore is also the password for the private key of the client you want
   to authenticate as.
 </p>
 <p>Or you can set the appropriate System properties - see the <code>system.properties</code> file.</p>
 <p>
   The next time you run your test, the SSL Manager will examine your key store to
   see if it has at least one key available to it.  If there is only one key, SSL
   Manager will select it for you.  If there is more than one key, it currently selects the first key.
   There is currently no way to select other entries in the keystore, so the desired key must be the first.
 </p>
 <b>Things to Look Out For</b>
 <p>
   You must have your Certificate Authority (CA) certificate installed properly
   if it is not signed by one of the five CA certificates that ships with your
   JDK.  One method to install it is to import your CA certificate into a JKS
   file, and name the JKS file "<code>jssecacerts</code>".  Place the file in your JRE's
   <code>lib/security</code> folder.  This file will be read before the "<code>cacerts</code>" file in
   the same directory.  Keep in mind that as long as the "<code>jssecacerts</code>" file
   exists, the certificates installed in "<code>cacerts</code>" will not be used.  This may
   cause problems for you.  If you don't mind importing your CA certificate into
   the "<code>cacerts</code>" file, then you can authenticate against all of the CA certificates
   installed.
 </p>
 </component>
 
 <component name="HTTP(S) Test Script Recorder" was="HTTP Proxy Server" index="&sect-num;.9.5"  width="1052" height="694" screenshot="proxy_control.png">
 <description><p>The HTTP(S) Test Script Recorder allows JMeter to intercept and record your actions while you browse your web application
 with your normal browser.  JMeter will create test sample objects and store them
 directly into your test plan as you go (so you can view samples interactively while you make them).<br/>
 Ensure you read this <a href="https://wiki.apache.org/jmeter/TestRecording210">wiki page</a> to setup correctly JMeter.
 </p>
 
 <p>To use the recorder, <i>add</i> the HTTP(S) Test Script Recorder element to the workbench.
 Select the WorkBench element in the tree, and right-click on this element to get the
 Add menu 
 (<menuchoice>
   <guimenuitem>Add</guimenuitem>
   <guimenuitem>Non-Test Elements</guimenuitem>
   <guimenuitem>HTTP(S) Test Script Recorder</guimenuitem>
 </menuchoice>
 ).</p>
 <p>
 The recorder is implemented as an HTTP(S) proxy server.
 You need to set up your browser use the proxy for all HTTP and HTTPS requests.
 <note>Do not use JMeter as the proxy for any other request types - FTP, etc. - as JMeter cannot handle them.</note>
 </p>
 <p>
 Ideally use private browsing mode when recording the session.
 This should ensure that the browser starts with no stored cookies, and prevents certain changes from being saved.
 For example, Firefox does not allow certificate overrides to be saved permanently.
 </p>
 <h4>HTTPS recording and certificates</h4>
 <p>
 HTTPS connections use certificates to authenticate the connection between the browser and the web server.
 When connecting via HTTPS, the server presents the certificate to the browser.
 To authenticate the certificate, the browser checks that the server certificate is signed
 by a Certificate Authority (CA) that is linked to one of its in-built root CAs.
 <note>Browsers also check that the certificate is for the correct host or domain, and that it is valid and not expired.</note>
 If any of the browser checks fail, it will prompt the user who can then decide whether to allow the connection to proceed.  
 </p>
 <p>
 JMeter needs to use its own certificate to enable it to intercept the HTTPS connection from
 the browser. Effectively JMeter has to pretend to be the target server.
 </p>
 <p>
 JMeter will generate its own certificate(s).
 These are generated with a validity period defined by the property <code>proxy.cert.validity</code>, default 7 days, and random passwords.
 If JMeter detects that it is running under Java 8 or later, it will generate certificates for each target server as necessary (dynamic mode)
 unless the following property is defined: <code>proxy.cert.dynamic_keys=false</code>.
 When using dynamic mode, the certificate will be for the correct host name, and will be signed by a JMeter-generated CA certificate.
 By default, this CA certificate won't be trusted by the browser, however it can be installed as a trusted certificate.
 Once this is done, the generated server certificates will be accepted by the browser.
 This has the advantage that even embedded HTTPS resources can be intercepted, and there is no need to override the browser checks for each new server.
 <note>Browsers don't prompt for embedded resources. So with earlier versions, embedded resources would only be downloaded for servers that were already 'known' to the browser</note>
 </p>
 <p>Unless a keystore is provided (and you define the property <code>proxy.cert.alias</code>),
 JMeter needs to use the keytool application to create the keystore entries. 
 JMeter includes code to check that keytool is available by looking in various standard places.
 If JMeter is unable to find the keytool application, it will report an error.
 If necessary, the system property <code>keytool.directory</code> can be used to tell JMeter where to find keytool.
 This should be defined in the file <code>system.properties</code>.
 </p>
 <p>
 The JMeter certificates are generated (if necessary) when the <code>Start</code> button is pressed.
 <note>Certificate generation can take some while, during which time the GUI will be unresponsive.</note>
 The cursor is changed to an hour-glass whilst this is happening.
 When certificate generation is complete, the GUI will display a pop-up dialogue containing the details of the certificate for the root CA.
 This certificate needs to be installed by the browser in order for it to accept the host certificates generated by JMeter; see <a href="#install_cert">below</a> for details.
 </p>
 <p>
 If necessary, you can force JMeter to regenerate the keystore (and the exported certificates - <code>ApacheJMeterTemporaryRootCA[.usr|.crt]</code>) by deleting the keystore file <code>proxyserver.jks</code> from the JMeter directory.
 </p>
 <p>
 This certificate is not one of the certificates that browsers normally trust, and will not be for the
 correct host.<br/>
 As a consequence: 
 </p>
 <ul>
 <li>The browser should display a dialogue asking if you want to accept the certificate or not. For example:
 <source>
 1) The server's name "<code>www.example.com</code>" does not match the certificate's name
    "<code>JMeter Proxy (DO NOT TRUST)</code>". Somebody may be trying to eavesdrop on you.
 2) The certificate for "<code>JMeter Proxy (DO NOT TRUST)</code>" is signed by the unknown Certificate Authority
    "<code>JMeter Proxy (DO NOT TRUST)</code>". It is not possible to verify that this is a valid certificate.
 </source>
 You will need to accept the certificate in order to allow the JMeter Proxy to intercept the SSL traffic in order to
 record it.
 However, do not accept this certificate permanently; it should only be accepted temporarily.
 Browsers only prompt this dialogue for the certificate of the main url, not for the resources loaded in the page, such as images, css or javascript files hosted on a secured external CDN. 
 If you have such resources (gmail has for example), you'll have to first browse manually to these other domains in order to accept JMeter's certificate for them. 
 Check in <code>jmeter.log</code> for secure domains that you need to register certificate for.
 </li>
 <li>If the browser has already registered a validated certificate for this domain, the browser will detect JMeter as a security breach and will refuse to load the page. If so, you have to remove the trusted certificate from your browser's keystore.
 </li>
 </ul>
 <p>
 Versions of JMeter from 2.10 onwards still support this method, and will continue to do so if you define the following property:
 <code>proxy.cert.alias</code>
 The following properties can be used to change the certificate that is used:
 </p>
 <ul>
 <li><code>proxy.cert.directory</code> - the directory in which to find the certificate (default = JMeter <code>bin/</code>)</li>
 <li><code>proxy.cert.file</code> - name of the keystore file (default "<code>proxyserver.jks</code>")</li>
 <li><code>proxy.cert.keystorepass</code> - keystore password (default "<code>password</code>") [Ignored if using JMeter certificate]</li>
 <li><code>proxy.cert.keypassword</code> - certificate key password (default "<code>password</code>") [Ignored if using JMeter certificate]</li>
 <li><code>proxy.cert.type</code> - the certificate type (default "<code>JKS</code>") [Ignored if using JMeter certificate]</li>
 <li><code>proxy.cert.factory</code> - the factory (default "<code>SunX509</code>") [Ignored if using JMeter certificate]</li>
 <li><code>proxy.cert.alias</code> - the alias for the key to be used. If this is defined, JMeter does not attempt to generate its own certificate(s).</li>
 <li><code>proxy.ssl.protocol</code> - the protocol to be used (default "<code>SSLv3</code>")</li>
 </ul>
 <note>
 If your browser currently uses a proxy (e.g. a company intranet may route all external requests via a proxy),
 then you need to <a href="get-started.html#proxy_server">tell JMeter to use that proxy</a> before starting JMeter, 
 using the <a href="get-started.html#options">command-line options</a> <code>-H</code> and <code>-P</code>.
 This setting will also be needed when running the generated test plan.
 </note>
 <a name="install_cert"/>
 <h4>Installing the JMeter CA certificate for HTTPS recording</h4>
 <p>
 As mentioned above, when run under Java 8, JMeter can generate certificates for each server.
 For this to work smoothly, the root CA signing certificate used by JMeter needs to be trusted by the browser.
 The first time that the recorder is started, it will generate the certificates if necessary. 
 The root CA certificate is exported into a file with the name <code>ApacheJMeterTemporaryRootCA</code> in the current launch directory.
 When the certificates have been set up, JMeter will show a dialog with the current certificate details.
 At this point, the certificate can be imported into the browser, as per the instructions below.
 </p>
 <p>
 Note that once the root CA certificate has been installed as a trusted CA, the browser will trust any certificates signed by it.
 Until such time as the certificate expires or the certificate is removed from the browser, it will not warn the user that the certificate is being relied upon.
 So anyone that can get hold of the keystore and password can use the certificate to generate certificates which will be accepted
 by any browsers that trust the JMeter root CA certificate.
 For this reason, the password for the keystore and private keys are randomly generated and a short validity period used.
 The passwords are stored in the local preferences area.
 Please ensure that only trusted users have access to the host with the keystore.
 </p>
 <note>
 The popup that displays once you start the Recorder is an informational popup:
 <figure width="1024" height="749" image="recorder_popup_info.png">Recorder Install Certificate Popup</figure>
 Just click ok and proceed further.
 </note>
 <h5>Installing the certificate in Firefox</h5>
 <p>
 Choose the following options:
 </p>
 <ul>
 <li><code>Tools / Options</code></li>
 <li><code>Advanced / Certificates</code></li>
 <li><code>View Certificates</code></li>
 <li><code>Authorities</code></li>
 <li><code>Import &hellip;</code></li>
 <li>Browse to the JMeter launch directory, and click on the file <code>ApacheJMeterTemporaryRootCA.crt</code>, press <code>Open</code></li>
 <li>Click <code>View</code> and check that the certificate details agree with the ones displayed by the JMeter Test Script Recorder</li>
 <li>If OK, select "<code>Trust this CA to identify web sites</code>", and press <code>OK</code></li>
 <li>Close dialogs by pressing <code>OK</code> as necessary</li>
 </ul>
 <h5>Installing the certificate in Chrome or Internet Explorer</h5>
 <p>
 Both Chrome and Internet Explorer use the same trust store for certificates.
 </p>
 <ul>
 <li>Browse to the JMeter launch directory, and click on the file <code>ApacheJMeterTemporaryRootCA.crt</code>, and open it</li>
 <li>Click on the "<code>Details</code>" tab and check that the certificate details agree with the ones displayed by the JMeter Test Script Recorder</li>
 <li>If OK, go back to the "<code>General</code>" tab, and click on "<code>Install Certificate &hellip;</code>" and follow the Wizard prompts</li>
 </ul>
 <h5>Installing the certificate in Opera</h5>
 <ul>
 <li><code>Tools / Preferences / Advanced / Security</code></li>
 <li><code>Manage Certificates &hellip;</code></li>
 <li>Select "<code>Intermediate</code>" tab, click "<code>Import &hellip;</code>"</li>
 <li>Browse to the JMeter launch directory, and click on the file <code>ApacheJMeterTemporaryRootCA.usr</code>, and open it</li>
 <li></li>
 </ul>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Port" required="Yes">The port that the HTTP(S) Test Script Recorder listens to.  <code>8888</code> is the default, but you can change it.</property>
         <property name="HTTPS Domains" required="No">List of domain (or host) names for HTTPS. Use this to pre-generate certificates for all servers you wish to record.
         <br/>
         For example, <code>*.example.com,*.subdomain.example.com</code>
         <br/>
         Note that wildcard domains only apply to one level,
         i.e. <code>abc.subdomain.example.com</code> matches <code>*.subdomain.example.com</code> but not <code>*.example.com</code>
         </property>
         <property name="Target Controller" required="Yes">The controller where the proxy will store the generated samples. By default, it will look for a Recording Controller and store them there wherever it is.</property>
         <property name="Grouping" required="Yes">Whether to group samplers for requests from a single "click" (requests received without significant time separation), and how to represent that grouping in the recording:
            <ul>
              <li><code>Do not group samplers</code> - store all recorded samplers sequentially, without any grouping.</li>
              <li><code>Add separators between groups</code> - add a controller named "<code>--------------</code>" to create a visual separation between the groups. Otherwise the samplers are all stored sequentially.</li>
              <li><code>Put each group in a new controller</code> - create a new <complink name="Simple Controller"/> for each group, and store all samplers for that group in it.</li>
              <li><code>Store 1<sup>st</sup> sampler of each group only</code> - only the first request in each group will be recorded. The "<code>Follow Redirects</code>" and "<code>Retrieve All Embedded Resources &hellip;</code>" flags will be turned on in those samplers.</li>
              <li><code>Put each group in a new transaction controller</code> - create a new <complink name="Transaction Controller"/> for each group, and store all samplers for that group in it.</li>
            </ul>
            The property <code>proxy.pause</code> determines the minimum gap that JMeter needs between requests
            to treat them as separate "clicks". The default is <code>5000</code> (milliseconds) i.e. 5 seconds.
            If you are using grouping, please ensure that you leave the required gap between clicks.
         </property>
         <!-- TODO:property name="Group Separation Interval">Inactivity time between two requests needed to consider them in two separate groups.</property-->
         <property name="Capture HTTP Headers" required="Yes">Should headers be added to the plan?
         If specified, a Header Manager will be added to each HTTP Sampler.
         The Proxy server always removes Cookie and Authorization headers from the generated Header Managers.
         By default it also removes <code>If-Modified-Since</code> and <code>If-None-Match</code> headers.
         These are used to determine if the browser cache items are up to date;
         when recording one normally wants to download all the content.
         To change which additional headers are removed, define the JMeter property <code>proxy.headers.remove</code>
         as a comma-separated list of headers.
         </property>
         <property name="Add Assertions" required="Yes">Add a blank assertion to each sampler?</property>
         <property name="Regex Matching" required="Yes">Use Regex Matching when replacing variables? If checked replacement will use word boundaries, i.e. it will only replace word matching values of variable, not part of a word. A word boundary follows Perl5 definition and is equivalent to <code>\b</code>. More information below in the paragraph about "<code>User Defined Variable replacement</code>".</property>
         <property name="Type" required="Yes">Which type of sampler to generate (the HTTPClient default or Java)</property>
         <property name="Redirect Automatically" required="Yes">Set Redirect Automatically in the generated samplers?</property>
         <property name="Follow Redirects" required="Yes">Set Follow Redirects in the generated samplers?<br/>
         <note>Note: see "Recording and redirects" section below for important information.</note>
         </property>
         <property name="Use Keep-Alive" required="Yes">Set Use Keep-Alive in the generated samplers?</property>
         <property name="Retrieve all Embedded Resources" required="Yes">Set Retrieve all Embedded Resources in the generated samplers?</property>
         <property name="Content Type filter" required="No">
         Filter the requests based on the <code>content-type</code> - e.g. "<code>text/html [;charset=utf-8 ]</code>".
         The fields are regular expressions which are checked to see if they are contained in the <code>content-type</code>.
         [Does not have to match the entire field].
         The include filter is checked first, then the exclude filter.
         Samples which are filtered out will not be stored.
         <note>Note: this filtering is applied to the content type of the response</note>
         </property>
         <property name="Patterns to Include" required="No">Regular expressions that are matched against the full URL that is sampled.  Allows filtering of requests that are recorded.  All requests pass through, but only
         those that meet the requirements of the <code>Include</code>/<code>Exclude</code> fields are <em>recorded</em>.  If both <code>Include</code> and <code>Exclude</code> are
         left empty, then everything is recorded (which can result in dozens of samples recorded for each page, as images, stylesheets,
         etc. are recorded).  <note>If there is at least one entry in the <code>Include</code> field, then only requests that match one or more <code>Include</code> patterns are
         recorded</note>.</property>
         <property name="Patterns to Exclude" required="No">Regular expressions that are matched against the URL that is sampled.
         <note>Any requests that match one or more <code>Exclude</code> pattern are <em>not</em> recorded</note>.</property>
         <property name="Notify Child Listeners of filtered samplers" required="No">Notify Child Listeners of filtered samplers
         <note>Any response that match one or more <code>Exclude</code> pattern is <em>not</em> delivered to Child Listeners (View Results Tree)</note>.</property>
         <property name="Start Button" required="N/A">Start the proxy server.  JMeter writes the following message to the console once the proxy server has started up and is ready to take requests: "<code>Proxy up and running!</code>".</property>
         <property name="Stop Button" required="N/A">Stop the proxy server.</property>
         <property name="Restart Button" required="N/A">Stops and restarts the proxy server.  This is
   useful when you change/add/delete an include/exclude filter expression.</property>
 </properties>
 
 <h4>Recording and redirects</h4>
 <p>
 During recording, the browser will follow a redirect response and generate an additional request.
 The Proxy will record both the original request and the redirected request
 (subject to whatever exclusions are configured).
 The generated samples have "<code>Follow Redirects</code>" selected by default, because that is generally better.
 <note>Redirects may depend on the original request, so repeating the originally recorded sample may not always work.</note>
 </p>
 <p>
 Now if JMeter is set to follow the redirect during replay, it will issue the original request, 
 and then replay the redirect request that was recorded.
 To avoid this duplicate replay, JMeter tries to detect when a sample is the result of a previous
 redirect. If the current response is a redirect, JMeter will save the redirect URL.
 When the next request is received, it is compared with the saved redirect URL and if there is a match,
 JMeter will disable the generated sample. It also adds comments to the redirect chain.
 This assumes that all the requests in a redirect chain will follow each other without any intervening requests.
 To disable the redirect detection, set the property <code>proxy.redirect.disabling=false</code>
 </p>
 
 <h4>Includes and Excludes</h4>
 <p>The <b>include and exclude patterns</b> are treated as regular expressions (using Jakarta ORO).
 They will be matched against the host name, port (actual or implied), path and query (if any) of each browser request.
 If the URL you are browsing is <br></br> 
 "<code>http://localhost/jmeter/index.html?username=xxxx</code>",<br></br> 
 then the regular expression will be tested against the string:<br></br> 
 "<code>localhost:80/jmeter/index.html?username=xxxx</code>".<br></br>   
 Thus, if you want to include all <code>.html</code> files, your regular expression might look like: <br></br> 
 "<code>.*\.html(\?.*)?</code>" - or "<code>.*\.html</code> 
 if you know that there is no query string or you only want html pages without query strings.
 </p>
 <p>
 If there are any include patterns, then the URL <b>must match at least one</b> of the patterns
 , otherwise it will not be recorded.
 If there are any exclude patterns, then the URL <b>must not match any</b> of the patterns
 , otherwise it will not be recorded.
 Using a combination of includes and excludes,
 you should be able to record what you are interested in and skip what you are not.
 </p>
 
 <note>
 N.B. the string that is matched by the regular expression must be the same as the <b>whole</b> host+path string.<br></br>Thus "<code>\.html</code>" will <b>not</b> match <code>localhost:80/index.html</code>
 </note>
 
 <h4>Capturing binary POST data</h4>
 <p>
 JMeter is able to capture binary POST data.
 To configure which <code>content-types</code> are treated as binary, update the JMeter property <code>proxy.binary.types</code>.
 The default settings are as follows:
 </p>
 <source>
 # These content-types will be handled by saving the request in a file:
 proxy.binary.types=application/x-amf,application/x-java-serialized-object
 # The files will be saved in this directory:
 proxy.binary.directory=user.dir
 # The files will be created with this file filesuffix:
 proxy.binary.filesuffix=.binary
 </source>
 
 <h4>Adding timers</h4>
 <p>It is also possible to have the proxy add timers to the recorded script. To
 do this, create a timer directly within the HTTP(S) Test Script Recorder component.
 The proxy will place a copy of this timer into each sample it records, or into
 the first sample of each group if you're using grouping. This copy will then be
 scanned for occurrences of variable <code>${T}</code> in its properties, and any such
 occurrences will be replaced by the time gap from the previous sampler
 recorded (in milliseconds).</p>
 
 <p>When you are ready to begin, hit &quot;<code>start</code>&quot;.</p>
 <note>You will need to edit the proxy settings of your browser to point at the
 appropriate server and port, where the server is the machine JMeter is running on, and
 the port # is from the Proxy Control Panel shown above.</note>
 
 <h4>Where Do Samples Get Recorded?</h4>
 <p>JMeter places the recorded samples in the Target Controller you choose. If you choose the default option
 "<code>Use Recording Controller</code>", they will be stored in the first Recording Controller found in the test object tree (so be
 sure to add a Recording Controller before you start recording).</p>
 
 <p>
 If the Proxy does not seem to record any samples, this could be because the browser is not actually using the proxy.
 To check if this is the case, try stopping the proxy.
 If the browser still downloads pages, then it was not sending requests via the proxy.
 Double-check the browser options. 
 If you are trying to record from a server running on the same host,
 then check that the browser is not set to "<code>Bypass proxy server for local addresses</code>"
 (this example is from IE7, but there will be similar options for other browsers).
 If JMeter does not record browser URLs such as <code>http://localhost/</code> or <code>http://127.0.0.1/</code>,
 try using the non-loopback hostname or IP address, e.g. <code>http://myhost/</code> or <code>http://192.168.0.2/</code>.
 </p>
 
 <h4>Handling of HTTP Request Defaults</h4>
 <p>If the HTTP(S) Test Script Recorder finds enabled <complink name="HTTP Request Defaults"/> directly within the
 controller where samples are being stored, or directly within any of its parent controllers, the recorded samples
 will have empty fields for the default values you specified. You may further control this behaviour by placing an
 HTTP Request Defaults element directly within the HTTP(S) Test Script Recorder, whose non-blank values will override
 those in the other HTTP Request Defaults. See <a href="best-practices.html#proxy_server"> Best
 Practices with the HTTP(S) Test Script Recorder</a> for more info.</p>
 
 <h4>User Defined Variable replacement</h4>
 <p>Similarly, if the HTTP(S) Test Script Recorder finds <complink name="User Defined Variables"/> (UDV) directly within the
 controller where samples are being stored, or directly within any of its parent controllers, the recorded samples
 will have any occurrences of the values of those variables replaced by the corresponding variable. Again, you can
 place User Defined Variables directly within the HTTP(S) Test Script Recorder to override the values to be replaced. See
 <a href="best-practices.html#proxy_server"> Best Practices with the Test Script Recorder</a> for more info.</p>
 
 <note>Please note that matching is case-sensitive.</note>
 
 <p>Replacement by Variables: by default, the Proxy server looks for all occurrences of UDV values. 
 If you define the variable <code>WEB</code> with the value <code>www</code>, for example,
 the string <code>www</code> will be replaced by <code>${WEB}</code> wherever it is found.
 To avoid this happening everywhere, set the "<code>Regex Matching</code>" check-box.
 This tells the proxy server to treat values as Regexes (using the perl5 compatible regex matchers provided by ORO).</p>
 
 <p>If "<code>Regex Matching</code>" is selected every variable will be compiled into a perl compatible regex enclosed in
 <code>\b(</code> and <code>)\b</code>. That way each match will start and end at a word boundary.</p>
 
 <note>Note that the boundary characters are not part of the matching group, e.g. <code>n.*</code> to match <code>name</code> out
 of <code>You can call me 'name'</code>.</note>
 
 <p>If you don't want your regex to be enclosed with those boundary matchers, you have to enclose your
 regex within parens, e.g <code>('.*?')</code> to match <code>'name'</code> out of <code>You can call me 'name'</code>.</p>
 
 <note>
 The variables will be checked in random order. So ensure, that the potential matches don't overlap.
 Overlapping matchers would be <code>.*</code> (which matches anything) and <code>www</code> (which
 matches <code>www</code> only). Non-overlapping matchers would be <code>a+</code> (matches a sequence
 of <code>a</code>'s) and <code>b+</code> (matches a sequence of <code>b</code>'s).
 </note>
 
 <p>If you want to match a whole string only, enclose it in <code>(^</code> and <code>$)</code>, e.g. <code>(^thus$)</code>.
 The parens are necessary, since the normally added boundary characters will prevent <code>^</code> and
 <code>$</code> to match.</p>
 
 <p>If you want to match <code>/images</code> at the start of a string only, use the value <code>(^/images)</code>.
 Jakarta ORO also supports zero-width look-ahead, so one can match <code>/images/&hellip;</code>
 but retain the trailing <code>/</code> in the output by using <code>(^/images(?=/))</code>.</p>
 
 <note>
 Note that the current version of Jakara ORO does not support look-behind - i.e. <code>(?&lt;=&hellip;)</code> or <code>(?&lt;!&hellip;)</code>.
 </note>
 
 <p>Look out for overlapping matchers. For example the value <code>.*</code> as a regex in a variable named
 <code>regex</code> will partly match a previous replaced variable, which will result in something like
 <code>${{regex}</code>, which is most probably not the desired result.</p>
 
 <p>If there are any problems interpreting any variables as patterns, these are reported in <code>jmeter.log</code>,
 so be sure to check this if UDVs are not working as expected.</p>
 
 <p>When you are done recording your test samples, stop the proxy server (hit the &quot;<code>stop</code>&quot; button).  Remember to reset
 your browser's proxy settings.  Now, you may want to sort and re-order the test script, add timers, listeners, a
 cookie manager, etc.</p>
 
 <h4>How can I record the server's responses too?</h4>
 <p>Just place a <complink name="View Results Tree"/> listener as a child of the HTTP(S) Test Script Recorder and the responses will be displayed. 
 You can also add a <complink name="Save Responses to a file"/> Post-Processor which will save the responses to files.
 </p>
 
 <h4>Associating requests with responses</h4>
 <p>
 If you define the property <code>proxy.number.requests=true</code> 
 JMeter will add a number to each sampler and each response.
 Note that there may be more responses than samplers if excludes or includes have been used.
 Responses that have been excluded will have labels enclosed in <code>[</code> and <code>],</code> for example <code>[23 /favicon.ico]</code> 
 </p>
 <h4>Cookie Manager</h4>
 <p>
 If the server you are testing against uses cookies, remember to add an <complink name="HTTP Cookie Manager"/> to the test plan
 when you have finished recording it.
 During recording, the browser handles any cookies, but JMeter needs a Cookie Manager
 to do the cookie handling during a test run.
 The JMeter Proxy server passes on all cookies sent by the browser during recording, but does not save them to the test
 plan because they are likely to change between runs.
 </p>
 <h4>Authorization Manager</h4>
 <p>
 The HTTP(S) Test Script Recorder grabs "<code>Authentication</code>" header, tries to compute the Auth Policy. If Authorization Manager was added to target
 controller manually, HTTP(S) Test Script Recorder will find it and add authorization (matching ones will be removed). Otherwise
 Authorization Manager will be added to target controller with authorization object.
 You may have to fix automatically computed values after recording.
 
 </p>
 <h4>Uploading files</h4>
 <p>
 Some browsers (e.g. Firefox and Opera) don't include the full name of a file when uploading files.
 This can cause the JMeter proxy server to fail.
 One solution is to ensure that any files to be uploaded are in the JMeter working directory,
 either by copying the files there or by starting JMeter in the directory containing the files.
 </p>
 <h4>Recording HTTP Based Non Textual Protocols not natively available in JMeter</h4>
 <p>
 You may have to record an HTTP protocol that is not handled by default by JMeter (Custom Binary Protocol, Adobe Flex, Microsoft Silverlight, &hellip; ).
 Although JMeter does not provide a native proxy implementation to record these protocols, you have the ability to
 record these protocols by implementing a custom <code>SamplerCreator</code>. This Sampler Creator will translate the binary format into a <code>HTTPSamplerBase</code> subclass
 that can be added to the JMeter Test Case.
 For more details see "Extending JMeter".
 </p>
 </component>
 
 <component name="HTTP Mirror Server" index="&sect-num;.9.6"  width="794" height="157" screenshot="mirrorserver.png">
 <description>
 <p>
 The HTTP Mirror Server is a very simple HTTP server - it simply mirrors the data sent to it.
 This is useful for checking the content of HTTP requests.
 </p>
 <p>
 It uses default port <code>8081</code>.
 </p>
 </description>
 <properties>
         <property name="Port" required="Yes">Port on which Mirror server listens, defaults to <code>8081</code>.</property>
         <property name="Max Number of threads" required="No">If set to a value &gt; <code>0</code>, number of threads serving requests will be limited to the configured number, if set to a value &le; <code>0</code> 
         a new thread will be created to serve each incoming request. Defaults to <code>0</code></property>
         <property name="Max Queue size" required="No">Size of queue used for holding tasks before they are executed by Thread Pool, when Thread pool is exceeded, incoming requests will
         be held in this queue and discarded when this queue is full. This parameter is only used if Max Number of Threads is greater than <code>0</code>. Defaults to <code>25</code></property>
 </properties>
 <note>
 Note that you can get more control over the responses by adding an HTTP Header Manager with the following name/value pairs:
 </note>
 <properties>
         <property name="X-Sleep" required="No">Time to sleep in ms before sending response</property>
         <property name="X-SetCookie" required="No">Cookies to be set on response</property>
         <property name="X-ResponseStatus" required="No">Response status, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">HTTP Status responses</a>, example 200 OK, 500 Internal Server Error, &hellip;.</property>
         <property name="X-ResponseLength" required="No">Size of response, this trims the response to the requested size if that is less than the total size</property>
         <property name="X-SetHeaders" required="No">Pipe separated list of headers, example:<br/> 
         <code>headerA=valueA|headerB=valueB</code> would set <code>headerA</code> to <code>valueA</code> and <code>headerB</code> to <code>valueB</code>.
         </property>
 </properties>
 <p>
 You can also use the following query parameters:
 </p>
 <properties>
     <property name="redirect" required="No">Generates a 302 (Temporary Redirect) with the provided location.
     e.g. <code>?redirect=/path</code>
     </property>
     <property name="status" required="No">Overrides the default status return. e.g. <code>?status=404 Not Found</code></property>
     <property name="v" required="No">Verbose flag, writes some details to standard output. e.g. first line and redirect location if specified</property>
 </properties>
 </component>
 
 <component name="Property Display" index="&sect-num;.9.7"  width="804" height="508" screenshot="property_display.png">
 <description>
 <p>
 The Property Display shows the values of System or JMeter properties.
 Values can be changed by entering new text in the Value column.
 It is available only on the WorkBench.
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
 </properties>
 </component>
 
 <component name="Debug Sampler" index="&sect-num;.9.8"  width="431" height="172" screenshot="debug_sampler.png">
 <description>
 <p>
 The Debug Sampler generates a sample containing the values of all JMeter variables and/or properties.
 </p>
 <p>
 The values can be seen in the <complink name="View Results Tree"/> Listener Response Data pane.
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="JMeter Properties" required="Yes">Include JMeter properties?</property>
         <property name="JMeter Variables" required="Yes">Include JMeter variables?</property>
         <property name="System Properties" required="Yes">Include System properties?</property>
 </properties>
 </component>
 
 <component name="Debug PostProcessor" index="&sect-num;.9.8"  width="344" height="193" screenshot="debug_postprocessor.png">
 <description>
 <p>
 The Debug PostProcessor creates a subSample with the details of the previous Sampler properties, 
 JMeter variables, properties and/or System Properties.
 </p>
 <p>
 The values can be seen in the <complink name="View Results Tree"/> Listener Response Data pane.
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="JMeter Properties" required="Yes">Whether to show JMeter properties (default <code>false</code>).</property>
         <property name="JMeter Variables" required="Yes">Whether to show JMeter variables (default <code>false</code>).</property>
         <property name="Sampler Properties" required="Yes">Whether to show Sampler properties (default <code>true</code>).</property>
         <property name="System Properties" required="Yes">Whether to show System properties (default <code>false</code>).</property>
 </properties>
 </component>
 
 <component name="Test Fragment" index="&sect-num;.9.9"  width="236" height="94" screenshot="test_fragment.png">
 <description>
 <p>
 The Test Fragment is used in conjunction with the <complink name="Include Controller"/> and <complink name="Module Controller"/>.
 </p>
 </description>
 <properties>
         <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
 </properties>
 <note>
 When using Test Fragment with <complink name="Module Controller"/>, ensure you disable the Test Fragment to avoid the execution of Test Fragment itself.
 This is done by default since JMeter 2.13.
 </note>
 </component>
 
 <component name="setUp Thread Group" index="&sect-num;.9.10" width="678" height="377" screenshot="setup_thread_group.png">
 <description>
     <p>
     A special type of ThreadGroup that can be utilized to perform Pre-Test Actions.  The behavior of these threads
     is exactly like a normal <complink name="Thread Group"/> element.  The difference is that these type of threads 
     execute before the test proceeds to the executing of regular Thread Groups.
     </p>
 </description>
 </component>
 
 <component name="tearDown Thread Group" index="&sect-num;.9.11" width="677" height="379" screenshot="teardown_thread_group.png">
 <description>
     <p>
     A special type of ThreadGroup that can be utilized to perform Post-Test Actions.  The behavior of these threads
     is exactly like a normal <complink name="Thread Group"/> element.  The difference is that these type of threads 
     execute after the test has finished executing its regular Thread Groups.
     </p>
 </description>
 <note>
 Note that by default it won't run if Test is gracefully shutdown, if you want to make it run in this case, 
 ensure you check option "<code>Run tearDown Thread Groups after shutdown of main threads</code>" on Test Plan element.
 If Test Plan is stopped, tearDown will not run even if option is checked.
 </note>
 <figure width="1130" height="486" image="tear_down_on_shutdown.png">Figure 1 - Run tearDown Thread Groups after shutdown of main threads</figure>
 </component>
 
 <a href="#">^</a>
 
 </section>
 <!-- 
 <section name="&sect-num;.10 Reports" anchor="Reports">
 <description>
         <br></br>
 </description>
 
 <component name="Report Plan" index="&sect-num;.10.1" screenshot="">
 <description><p></p></description>
 </component>
 
 <component name="Report Table" index="&sect-num;.10.2" screenshot="">
 <description><p></p></description>
 </component>
 
 <component name="HTML Report Writer" index="&sect-num;.10.3" screenshot="">
 <description><p></p></description>
 </component>
 
 <component name="Report Page" index="&sect-num;.10.4" screenshot="">
 <description><p></p></description>
 </component>
 
 <component name="Line Graph" index="&sect-num;.10.5" screenshot="">
 <description><p></p></description>
 </component>
 
 <component name="Bar Chart" index="&sect-num;.10.6" screenshot="">
 <description><p></p></description>
 </component>
 
 <a href="#">^</a>
 
 </section>
  -->
 </body>
 </document>
 
