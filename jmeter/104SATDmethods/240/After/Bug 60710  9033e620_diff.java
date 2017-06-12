diff --git a/src/components/org/apache/jmeter/extractor/XPathExtractor.java b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
index 9fcbc1839..5f6241995 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,383 +1,383 @@
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
                     log.warn("No variable '{}' found to process by XPathExtractor '{}', skipping processing",
                             getVariableName(), getName());
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
             log.warn("SAXException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
             addAssertionFailure(previousResult, e, false); // Should this also fail the sample?
         } catch (TransformerException e) {// Can happen for incorrect XPath expression
             log.warn("TransformerException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
             addAssertionFailure(previousResult, e, false);
         } catch (TidyException e) {
             // Will already have been logged by XPathUtil
             addAssertionFailure(previousResult, e, true); // fail the sample
         }
     }
 
     private void addAssertionFailure(final SampleResult previousResult,
             final Throwable thrown, final boolean setFailed) {
-        AssertionResult ass = new AssertionResult(thrown.getClass().getSimpleName()); // $NON-NLS-1$
+        AssertionResult ass = new AssertionResult(getName()); // $NON-NLS-1$
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
diff --git a/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java b/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
index 1f7c28cf5..25a1962fb 100644
--- a/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
+++ b/test/src/org/apache/jmeter/extractor/TestXPathExtractor.java
@@ -1,271 +1,295 @@
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
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.junit.Assert;
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
             
             // Test match 1
             extractor.setXPathQuery("/book/page");
             extractor.setMatchNumber(1);
             extractor.process();
             assertEquals("one", vars.get(VAL_NAME));
             assertEquals("1", vars.get(VAL_NAME_NR));
             assertEquals("one", vars.get(VAL_NAME+"_1"));
             assertNull(vars.get(VAL_NAME+"_2"));
             assertNull(vars.get(VAL_NAME+"_3"));
             
             // Test match Random
             extractor.setXPathQuery("/book/page");
             extractor.setMatchNumber(0);
             extractor.process();
             assertEquals("1", vars.get(VAL_NAME_NR));
             Assert.assertTrue(StringUtils.isNoneEmpty(vars.get(VAL_NAME)));
             Assert.assertTrue(StringUtils.isNoneEmpty(vars.get(VAL_NAME+"_1")));
             assertNull(vars.get(VAL_NAME+"_2"));
             assertNull(vars.get(VAL_NAME+"_3"));
             
             // Put back default value
             extractor.setMatchNumber(-1);
             
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
 
             // No text all matches
             extractor.setXPathQuery("//a");
             extractor.process();
             extractor.setMatchNumber(-1);
             assertEquals("Default", vars.get(VAL_NAME));
 
             // No text match second
             extractor.setXPathQuery("//a");
             extractor.process();
             extractor.setMatchNumber(2);
             assertEquals("Default", vars.get(VAL_NAME));
 
             // No text match random
             extractor.setXPathQuery("//a");
             extractor.process();
             extractor.setMatchNumber(0);
             assertEquals("Default", vars.get(VAL_NAME));
 
             extractor.setMatchNumber(-1);
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
+            assertEquals(1, result.getAssertionResults().length);
+            assertEquals(extractor.getName(), result.getAssertionResults()[0].getName());
+            org.junit.Assert.assertTrue(result.getAssertionResults()[0].
+                    getFailureMessage().contains("A location path was expected, but the following token was encountered"));
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
         }
 
         @Test
+        public void testNonXmlDocument() throws Exception {
+            result.setResponseData("Error:exception occured", null);
+            extractor.setXPathQuery("//test");
+            extractor.process();
+            assertEquals(1, result.getAssertionResults().length);
+            assertEquals(extractor.getName(), result.getAssertionResults()[0].getName());
+            org.junit.Assert.assertTrue(result.getAssertionResults()[0].
+                    getFailureMessage().contains("Content is not allowed in prolog"));
+            assertEquals("Default", vars.get(VAL_NAME));
+            assertEquals("0", vars.get(VAL_NAME_NR));
+        }
+        @Test
         public void testInvalidDocument() throws Exception {
             result.setResponseData("<z>", null);
-            extractor.setXPathQuery("<");
+            extractor.setXPathQuery("//test");
             extractor.process();
+            
+            assertEquals(1, result.getAssertionResults().length);
+            assertEquals(extractor.getName(), result.getAssertionResults()[0].getName());
+            System.out.println(result.getAssertionResults()[0].
+                    getFailureMessage());
+            org.junit.Assert.assertTrue(result.getAssertionResults()[0].
+                    getFailureMessage().contains("XML document structures must start and end within the same entity"));
+
             assertEquals("Default", vars.get(VAL_NAME));
             assertEquals("0", vars.get(VAL_NAME_NR));
         }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 4e869608e..1d2ecb9f9 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,361 +1,362 @@
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
     <li>JMeter now sets through <code>-Djava.security.egd=file:/dev/urandom</code> the algorithm for secure random</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
     <li>JMeter now uses by default Oracle Nashorn engine instead of Mozilla Rhino for better performances. This should not have an impact unless
     you use some advanced features. You can revert back to Rhino by settings property <code>javascript.use_rhino=true</code>. 
     You can read this <a href="https://wiki.openjdk.java.net/display/Nashorn/Rhino+Migration+Guide">migration guide</a> for more details on Nashorn. See <bugzilla>60672</bugzilla></li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60661</bug>Deprecate SOAP/XML-RPC Request</li>
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
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
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
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
+    <li><bug>60710</bug>XPath Extractor : When content on which assertion applies is not XML, in View Results Tree the extractor is marked in Red and named SAXParseException. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
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
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
     <li><bug>59995</bug>Allow user to change font size with 2 new menu items and use <code>jmeter.hidpi.scale.factor</code> for scaling fonts. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60654</bug>Validation Feature : Be able to ignore BackendListener. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60646</bug>Workbench : Save it by default</li>
     <li><bug>60684</bug>Thread Group: Validate ended prematurely by Scheduler with 0 or very short duration. Contributed by Andrew Burton (andrewburtonatwh at gmail.com).</li>
     <li><bug>60589</bug>Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules. Contributed by Woonsan Ko (woonsan at apache.org)</li>
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
     <li><bug>60621</bug>The "report-template" folder is missing from ApacheJMeter_config-3.1.jar in maven central</li>
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
