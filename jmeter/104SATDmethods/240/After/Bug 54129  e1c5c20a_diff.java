diff --git a/src/components/org/apache/jmeter/config/CSVDataSet.java b/src/components/org/apache/jmeter/config/CSVDataSet.java
index 489bd3b8a..19b06e9bb 100644
--- a/src/components/org/apache/jmeter/config/CSVDataSet.java
+++ b/src/components/org/apache/jmeter/config/CSVDataSet.java
@@ -1,265 +1,254 @@
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
 
 import java.io.IOException;
-import java.util.List;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.NoConfigMerge;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Read lines from a file and split int variables.
  *
  * The iterationStart() method is used to set up each set of values.
  *
  * By default, the same file is shared between all threads
  * (and other thread groups, if they use the same file name).
  *
  * The shareMode can be set to:
  * <ul>
  * <li>All threads - default, as described above</li>
  * <li>Current thread group</li>
  * <li>Current thread</li>
  * <li>Identifier - all threads sharing the same identifier</li>
  * </ul>
  *
  * The class uses the FileServer alias mechanism to provide the different share modes.
  * For all threads, the file alias is set to the file name.
  * Otherwise, a suffix is appended to the filename to make it unique within the required context.
  * For current thread group, the thread group identityHashcode is used;
  * for individual threads, the thread hashcode is used as the suffix.
  * Or the user can provide their own suffix, in which case the file is shared between all
  * threads with the same suffix.
  *
  */
 public class CSVDataSet extends ConfigTestElement 
     implements TestBean, LoopIterationListener, NoConfigMerge {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 232L;
 
     private static final String EOFVALUE = // value to return at EOF
         JMeterUtils.getPropDefault("csvdataset.eofstring", "<EOF>"); //$NON-NLS-1$ //$NON-NLS-2$
 
     private transient String filename;
 
     private transient String fileEncoding;
 
     private transient String variableNames;
 
     private transient String delimiter;
 
     private transient boolean quoted;
 
     private transient boolean recycle = true;
 
     private transient boolean stopThread;
 
     private transient String[] vars;
 
     private transient String alias;
 
     private transient String shareMode;
     
     private boolean firstLineIsNames = false;
 
     private Object readResolve(){
         recycle = true;
         return this;
     }
 
     /**
      * {@inheritDoc}
      */
     public void iterationStart(LoopIterationEvent iterEvent) {
         FileServer server = FileServer.getFileServer();
         final JMeterContext context = getThreadContext();
         String delim = getDelimiter();
         if (delim.equals("\\t")) { // $NON-NLS-1$
             delim = "\t";// Make it easier to enter a Tab // $NON-NLS-1$
         } else if (delim.length()==0){
             log.warn("Empty delimiter converted to ','");
             delim=",";
         }
         if (vars == null) {
             String _fileName = getFilename();
             String mode = getShareMode();
             int modeInt = CSVDataSetBeanInfo.getShareModeAsInt(mode);
             switch(modeInt){
                 case CSVDataSetBeanInfo.SHARE_ALL:
                     alias = _fileName;
                     break;
                 case CSVDataSetBeanInfo.SHARE_GROUP:
                     alias = _fileName+"@"+System.identityHashCode(context.getThreadGroup());
                     break;
                 case CSVDataSetBeanInfo.SHARE_THREAD:
                     alias = _fileName+"@"+System.identityHashCode(context.getThread());
                     break;
                 default:
                     alias = _fileName+"@"+mode; // user-specified key
                     break;
             }
             final String names = getVariableNames();
             if (names == null || names.length()==0) {
                 String header = server.reserveFile(_fileName, getFileEncoding(), alias, true);
                 try {
                     vars = CSVSaveService.csvSplitString(header, delim.charAt(0));
                     firstLineIsNames = true;
                 } catch (IOException e) {
                     log.warn("Could not split CSV header line",e);
                 }
             } else {
                 server.reserveFile(_fileName, getFileEncoding(), alias);
                 vars = JOrphanUtils.split(names, ","); // $NON-NLS-1$
             }
         }
            
         // TODO: fetch this once as per vars above?
         JMeterVariables threadVars = context.getVariables();
         String[] lineValues = {};
         try {
             if (getQuotedData()) {
                 lineValues = server.getParsedLine(alias, recycle, firstLineIsNames, delim.charAt(0));
             } else {
                 String line = server.readLine(alias, recycle, firstLineIsNames);
                 lineValues = JOrphanUtils.split(line, delim, false);
             }
             for (int a = 0; a < vars.length && a < lineValues.length; a++) {
                 threadVars.put(vars[a], lineValues[a]);
             }
         } catch (IOException e) { // treat the same as EOF
             log.error(e.toString());
         }
         if (lineValues.length == 0) {// i.e. EOF
             if (getStopThread()) {
                 throw new JMeterStopThreadException("End of file detected");
             }
             for (int a = 0; a < vars.length ; a++) {
                 threadVars.put(vars[a], EOFVALUE);
             }
         }
     }
 
     /**
      * @return Returns the filename.
      */
     public String getFilename() {
         return filename;
     }
 
     /**
      * @param filename
      *            The filename to set.
      */
     public void setFilename(String filename) {
         this.filename = filename;
     }
 
     /**
      * @return Returns the file encoding.
      */
     public String getFileEncoding() {
         return fileEncoding;
     }
 
     /**
      * @param fileEncoding
      *            The fileEncoding to set.
      */
     public void setFileEncoding(String fileEncoding) {
         this.fileEncoding = fileEncoding;
     }
 
     /**
      * @return Returns the variableNames.
      */
     public String getVariableNames() {
         return variableNames;
     }
 
     /**
      * @param variableNames
      *            The variableNames to set.
      */
     public void setVariableNames(String variableNames) {
         this.variableNames = variableNames;
     }
 
     public String getDelimiter() {
         return delimiter;
     }
 
     public void setDelimiter(String delimiter) {
         this.delimiter = delimiter;
     }
 
     public boolean getQuotedData() {
         return quoted;
     }
 
     public void setQuotedData(boolean quoted) {
         this.quoted = quoted;
     }
 
     public boolean getRecycle() {
         return recycle;
     }
 
     public void setRecycle(boolean recycle) {
         this.recycle = recycle;
     }
 
     public boolean getStopThread() {
         return stopThread;
     }
 
     public void setStopThread(boolean value) {
         this.stopThread = value;
     }
 
     public String getShareMode() {
         return shareMode;
     }
 
     public void setShareMode(String value) {
         this.shareMode = value;
     }
-    
-    /** 
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getPropertyAsString("variableNames"));
-        return result;
-    }
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index 0a62add5d..f54780271 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,468 +1,456 @@
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
 import java.util.List;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.threads.JMeterContext;
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
 
 
         String regex = getRegex();
         try {
             List<MatchResult> matches = processMatches(regex, previousResult, matchNumber, vars);
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
             log.warn("Error in pattern: " + regex);
         }
     }
 
     private String getInputString(SampleResult result) {
         String inputString = useUrl() ? result.getUrlAsString() // Bug 39707
                 : useHeaders() ? result.getResponseHeaders()
                 : useCode() ? result.getResponseCode() // Bug 43451
                 : useMessage() ? result.getResponseMessage() // Bug 43451
                 : useUnescapedBody() ? StringEscapeUtils.unescapeHtml4(result.getResponseDataAsString())
                 : result.getResponseDataAsString() // Bug 36898
                 ;
        if (log.isDebugEnabled()) {
            log.debug("Input = " + inputString);
        }
        return inputString;
     }
 
     private List<MatchResult> processMatches(String regex, SampleResult result, int matchNumber, JMeterVariables vars) {
         if (log.isDebugEnabled()) {
             log.debug("Regex = " + regex);
         }
 
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         Pattern pattern = JMeterUtils.getPatternCache().getPattern(regex, Perl5Compiler.READ_ONLY_MASK);
         List<MatchResult> matches = new ArrayList<MatchResult>();
         int found = 0;
 
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
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
-    
-    /** 
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getRefName());
-        result.add(getDefaultValue());
-        result.add(getRegex());
-        return result;
-    }
 }
diff --git a/src/components/org/apache/jmeter/extractor/XPathExtractor.java b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
index 5cfe5b4e5..2af758457 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,347 +1,335 @@
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
                 Document d =  parseResponse(inputString);
                 getValuesForXPath(d,getXPathQuery(),matches);
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
      * @return
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
-    
-    /** 
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getRefName());
-        result.add(getDefaultValue());
-        result.add(getXPathQuery());
-        return result;
-    }
 }
diff --git a/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
index f8fd9052b..a73a84383 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
@@ -1,169 +1,159 @@
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
 
 package org.apache.jmeter.testelement;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * <p>
  * Super-class for TestElements that can be applied to main sample, sub-samples or both.
  * [Assertions use a different class because they use a different value for the {@link #getScopeName()} constant]
  * </p>
  *
  * <p>
  * Their corresponding GUI classes need to add the ScopePanel to the GUI
  * using the AbstractXXXGui methods:
  * <ul>
  * <li>createScopePanel()</li>
  * <li>saveScopeSettings()</li>
  * <li>showScopeSettings()</li>
  * </ul>
  * </p>
  */
 public abstract class AbstractScopedTestElement extends AbstractTestElement {
 
     private static final long serialVersionUID = 240L;
 
     //+ JMX attributes - do not change
     private static final String SCOPE = "Sample.scope"; // $NON-NLS-1$
     private static final String SCOPE_PARENT = "parent"; // $NON-NLS-1$
     private static final String SCOPE_CHILDREN = "children"; // $NON-NLS-1$
     private static final String SCOPE_ALL = "all"; // $NON-NLS-1$
     private static final String SCOPE_VARIABLE = "variable"; // $NON-NLS-1$
     private static final String SCOPE_VARIABLE_NAME = "Scope.variable"; // $NON-NLS-1$
     //- JMX
 
 
     protected String getScopeName() {
         return SCOPE;
     }
 
     /**
      * Get the scope setting
      * @return the scope, default parent
      */
     public String fetchScope() {
         return getPropertyAsString(getScopeName(), SCOPE_PARENT);
     }
 
     /**
      * Is the assertion to be applied to the main (parent) sample?
      *
      * @param scope
      * @return if the assertion is to be applied to the parent sample.
      */
     public boolean isScopeParent(String scope) {
         return scope.equals(SCOPE_PARENT);
     }
 
     /**
      * Is the assertion to be applied to the sub-samples (children)?
      *
      * @param scope
      * @return if the assertion is to be applied to the children.
      */
     public boolean isScopeChildren(String scope) {
         return scope.equals(SCOPE_CHILDREN);
     }
 
     /**
      * Is the assertion to be applied to the all samples?
      *
      * @param scope
      * @return if the assertion is to be applied to the all samples.
      */
     public boolean isScopeAll(String scope) {
         return scope.equals(SCOPE_ALL);
     }
 
     /**
      * Is the assertion to be applied to the all samples?
      *
      * @param scope
      * @return if the assertion is to be applied to the all samples.
      */
     public boolean isScopeVariable(String scope) {
         return scope.equals(SCOPE_VARIABLE);
     }
 
     /**
      * Is the assertion to be applied to the all samples?
      *
      * @return if the assertion is to be applied to the all samples.
      */
     protected boolean isScopeVariable() {
         return isScopeVariable(fetchScope());
     }
 
     public String getVariableName(){
         return getPropertyAsString(SCOPE_VARIABLE_NAME, "");
     }
 
     public void setScopeParent() {
         removeProperty(getScopeName());
     }
 
     public void setScopeChildren() {
         setProperty(getScopeName(), SCOPE_CHILDREN);
     }
 
     public void setScopeAll() {
         setProperty(getScopeName(), SCOPE_ALL);
     }
 
     public void setScopeVariable(String variableName) {
         setProperty(getScopeName(), SCOPE_VARIABLE);
         setProperty(SCOPE_VARIABLE_NAME, variableName);
     }
 
     /**
      * Generate a list of qualifying sample results,
      * depending on the scope.
      *
      * @param result current sample
      * @return list containing the current sample and/or its child samples
      */
     protected List<SampleResult> getSampleList(SampleResult result) {
         List<SampleResult> sampleList = new ArrayList<SampleResult>();
 
         String scope = fetchScope();
         if (isScopeParent(scope) || isScopeAll(scope)) {
             sampleList.add(result);
         }
         if (isScopeChildren(scope) || isScopeAll(scope)) {
             for (SampleResult subResult : result.getSubResults()) {
                 sampleList.add(subResult);
             }
         }
         return sampleList;
     }
-    
-    /** 
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getVariableName());
-        return result;
-    }
 }
diff --git a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
index 80aea0d31..2a179cb12 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
@@ -1,586 +1,590 @@
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
 
 package org.apache.jmeter.testelement;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.MapProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.PropertyIteratorImpl;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  */
 public abstract class AbstractTestElement implements TestElement, Serializable, Searchable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Map<String, JMeterProperty> propMap =
         Collections.synchronizedMap(new LinkedHashMap<String, JMeterProperty>());
 
     /**
      * Holds properties added when isRunningVersion is true
      */
     private transient Set<JMeterProperty> temporaryProperties;
 
     private transient boolean runningVersion = false;
 
     // Thread-specific variables saved here to save recalculation
     private transient JMeterContext threadContext = null;
 
     private transient String threadName = null;
 
     @Override
     public Object clone() {
         try {
             TestElement clonedElement = this.getClass().newInstance();
 
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 clonedElement.setProperty(iter.next().clone());
             }
             clonedElement.setRunningVersion(runningVersion);
             return clonedElement;
         } catch (InstantiationException e) {
             throw new AssertionError(e); // clone should never return null
         } catch (IllegalAccessException e) {
             throw new AssertionError(e); // clone should never return null
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void clear() {
         propMap.clear();
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Default implementation - does nothing
      */
     public void clearTestElementChildren(){
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     public void removeProperty(String key) {
         propMap.remove(key);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean equals(Object o) {
         if (o instanceof AbstractTestElement) {
             return ((AbstractTestElement) o).propMap.equals(propMap);
         } else {
             return false;
         }
     }
 
     // TODO temporary hack to avoid unnecessary bug reports for subclasses
 
     /**
      * {@inheritDoc}
      */
     @Override
     public int hashCode(){
         return System.identityHashCode(this);
     }
 
     /*
      * URGENT: TODO - sort out equals and hashCode() - at present equal
      * instances can/will have different hashcodes - problem is, when a proper
      * hashcode is used, tests stop working, e.g. listener data disappears when
      * switching views... This presumably means that instances currently
      * regarded as equal, aren't really equal.
      *
      * @see java.lang.Object#hashCode()
      */
     // This would be sensible, but does not work:
     // public int hashCode()
     // {
     // return propMap.hashCode();
     // }
 
     /**
      * {@inheritDoc}
      */
     public void addTestElement(TestElement el) {
         mergeIn(el);
     }
 
     public void setName(String name) {
         setProperty(TestElement.NAME, name);
     }
 
     public String getName() {
         return getPropertyAsString(TestElement.NAME);
     }
 
     public void setComment(String comment){
         setProperty(new StringProperty(TestElement.COMMENTS, comment));
     }
 
     public String getComment(){
         return getProperty(TestElement.COMMENTS).getStringValue();
     }
 
     /**
      * Get the named property. If it doesn't exist, a new NullProperty object is
      * created with the same name and returned.
      */
     public JMeterProperty getProperty(String key) {
         JMeterProperty prop = propMap.get(key);
         if (prop == null) {
             prop = new NullProperty(key);
         }
         return prop;
     }
 
     public void traverse(TestElementTraverser traverser) {
         PropertyIterator iter = propertyIterator();
         traverser.startTestElement(this);
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
         traverser.endTestElement(this);
     }
 
     protected void traverseProperty(TestElementTraverser traverser, JMeterProperty value) {
         traverser.startProperty(value);
         if (value instanceof TestElementProperty) {
             ((TestElement) value.getObjectValue()).traverse(traverser);
         } else if (value instanceof CollectionProperty) {
             traverseCollection((CollectionProperty) value, traverser);
         } else if (value instanceof MapProperty) {
             traverseMap((MapProperty) value, traverser);
         }
         traverser.endProperty(value);
     }
 
     protected void traverseMap(MapProperty map, TestElementTraverser traverser) {
         PropertyIterator iter = map.valueIterator();
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
     }
 
     protected void traverseCollection(CollectionProperty col, TestElementTraverser traverser) {
         PropertyIterator iter = col.iterator();
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
     }
 
     public int getPropertyAsInt(String key) {
         return getProperty(key).getIntValue();
     }
 
     public int getPropertyAsInt(String key, int defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
     }
 
     public boolean getPropertyAsBoolean(String key) {
         return getProperty(key).getBooleanValue();
     }
 
     public boolean getPropertyAsBoolean(String key, boolean defaultVal) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
     }
 
     public float getPropertyAsFloat(String key) {
         return getProperty(key).getFloatValue();
     }
 
     public long getPropertyAsLong(String key) {
         return getProperty(key).getLongValue();
     }
 
     public long getPropertyAsLong(String key, long defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
     }
 
     public double getPropertyAsDouble(String key) {
         return getProperty(key).getDoubleValue();
     }
 
     public String getPropertyAsString(String key) {
         return getProperty(key).getStringValue();
     }
 
     public String getPropertyAsString(String key, String defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
     }
 
     /**
      * Add property to test element
      * @param property {@link JMeterProperty} to add to current Test Element
      * @param clone clone property
      */
     protected void addProperty(JMeterProperty property, boolean clone) {
     	JMeterProperty propertyToPut = property;
     	if(clone) {
     		propertyToPut = property.clone();
     	}
         if (isRunningVersion()) {
         	setTemporary(propertyToPut);
         } else {
             clearTemporary(property);
         }
         JMeterProperty prop = getProperty(property.getName());
 
         if (prop instanceof NullProperty || (prop instanceof StringProperty && prop.getStringValue().equals(""))) {
         	propMap.put(property.getName(), propertyToPut);
         } else {
             prop.mergeIn(propertyToPut);
         }
     }
 
     /**
      * Add property to test element without cloning it
      * @param property {@link JMeterProperty}
      */
     protected void addProperty(JMeterProperty property) {
         addProperty(property, false);
     }
 
     /**
      * Remove property from temporaryProperties
      * @param property {@link JMeterProperty}
      */
     protected void clearTemporary(JMeterProperty property) {
         if (temporaryProperties != null) {
             temporaryProperties.remove(property);
         }
     }
 
     /**
      * Log the properties of the test element
      *
      * @see TestElement#setProperty(JMeterProperty)
      */
     protected void logProperties() {
         if (log.isDebugEnabled()) {
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 JMeterProperty prop = iter.next();
                 log.debug("Property " + prop.getName() + " is temp? " + isTemporary(prop) + " and is a "
                         + prop.getObjectValue());
             }
         }
     }
 
     public void setProperty(JMeterProperty property) {
         if (isRunningVersion()) {
             if (getProperty(property.getName()) instanceof NullProperty) {
                 addProperty(property);
             } else {
                 getProperty(property.getName()).setObjectValue(property.getObjectValue());
             }
         } else {
             propMap.put(property.getName(), property);
         }
     }
 
     public void setProperty(String name, String value) {
         setProperty(new StringProperty(name, value));
     }
 
     /**
      * Create a String property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     public void setProperty(String name, String value, String dflt) {
         if (dflt.equals(value)) {
             removeProperty(name);
         } else {
             setProperty(new StringProperty(name, value));
         }
     }
 
     public void setProperty(String name, boolean value) {
         setProperty(new BooleanProperty(name, value));
     }
 
     /**
      * Create a boolean property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     public void setProperty(String name, boolean value, boolean dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new BooleanProperty(name, value));
         }
     }
 
     public void setProperty(String name, int value) {
         setProperty(new IntegerProperty(name, value));
     }
 
     /**
      * Create a boolean property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     public void setProperty(String name, int value, int dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new IntegerProperty(name, value));
         }
     }
 
     public PropertyIterator propertyIterator() {
         return new PropertyIteratorImpl(propMap.values());
     }
 
     /**
      * Add to this the properties of element (by reference)
      * @param element {@link TestElement}
      */
     protected void mergeIn(TestElement element) {
         PropertyIterator iter = element.propertyIterator();
         while (iter.hasNext()) {
             JMeterProperty prop = iter.next();
             addProperty(prop, false);
         }
     }
 
     /**
      * Returns the runningVersion.
      */
     public boolean isRunningVersion() {
         return runningVersion;
     }
 
     /**
      * Sets the runningVersion.
      *
      * @param runningVersion
      *            the runningVersion to set
      */
     public void setRunningVersion(boolean runningVersion) {
         this.runningVersion = runningVersion;
         PropertyIterator iter = propertyIterator();
         while (iter.hasNext()) {
             iter.next().setRunningVersion(runningVersion);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void recoverRunningVersion() {
         Iterator<Map.Entry<String, JMeterProperty>>  iter = propMap.entrySet().iterator();
         while (iter.hasNext()) {
             Map.Entry<String, JMeterProperty> entry = iter.next();
             JMeterProperty prop = entry.getValue();
             if (isTemporary(prop)) {
                 iter.remove();
                 clearTemporary(prop);
             } else {
                 prop.recoverRunningVersion(this);
             }
         }
         emptyTemporary();
     }
 
     /**
      * Clears temporaryProperties
      */
     protected void emptyTemporary() {
         if (temporaryProperties != null) {
             temporaryProperties.clear();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public boolean isTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             return false;
         } else {
             return temporaryProperties.contains(property);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public void setTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             temporaryProperties = new LinkedHashSet<JMeterProperty>();
         }
         temporaryProperties.add(property);
         if (property instanceof MultiProperty) {
             PropertyIterator iter = ((MultiProperty) property).iterator();
             while (iter.hasNext()) {
                 setTemporary(iter.next());
             }
         }
     }
 
     /**
      * @return Returns the threadContext.
      */
     public JMeterContext getThreadContext() {
         if (threadContext == null) {
             /*
              * Only samplers have the thread context set up by JMeterThread at
              * present, so suppress the warning for now
              */
             // log.warn("ThreadContext was not set up - should only happen in
             // JUnit testing..."
             // ,new Throwable("Debug"));
             threadContext = JMeterContextService.getContext();
         }
         return threadContext;
     }
 
     /**
      * @param inthreadContext
      *            The threadContext to set.
      */
     public void setThreadContext(JMeterContext inthreadContext) {
         if (threadContext != null) {
             if (inthreadContext != threadContext) {
                 throw new RuntimeException("Attempting to reset the thread context");
             }
         }
         this.threadContext = inthreadContext;
     }
 
     /**
      * @return Returns the threadName.
      */
     public String getThreadName() {
         return threadName;
     }
 
     /**
      * @param inthreadName
      *            The threadName to set.
      */
     public void setThreadName(String inthreadName) {
         if (threadName != null) {
             if (!threadName.equals(inthreadName)) {
                 throw new RuntimeException("Attempting to reset the thread name");
             }
         }
         this.threadName = inthreadName;
     }
 
     public AbstractTestElement() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     // Default implementation
     public boolean canRemove() {
         return true;
     }
 
     /**
      * {@inheritDoc}
      */
     // Moved from JMeter class
     public boolean isEnabled() {
         return getProperty(TestElement.ENABLED) instanceof NullProperty || getPropertyAsBoolean(TestElement.ENABLED);
     }
     
     /** 
      * {@inheritDoc}}
      */
     public List<String> getSearchableTokens() throws Exception {
-        List<String> result = new ArrayList<String>(2);
-        result.add(getComment());
-        result.add(getName());
+        List<String> result = new ArrayList<String>(25);
+        PropertyIterator iterator = propertyIterator();
+        while(iterator.hasNext()) {
+            JMeterProperty jMeterProperty = iterator.next();    
+            result.add(jMeterProperty.getName());
+            result.add(jMeterProperty.getStringValue());
+        }
         return result;
     }
     
 	/**
 	 * Add to result the values of propertyNames
 	 * @param result List<String> values of propertyNames
 	 * @param propertyNames Set<String> properties to extract
 	 */
 	protected final void addPropertiesValues(List<String> result, Set<String> propertyNames) {
 		PropertyIterator iterator = propertyIterator();
 		while(iterator.hasNext()) {
 			JMeterProperty jMeterProperty = iterator.next();	
 			if(propertyNames.contains(jMeterProperty.getName())) {
 				result.add(jMeterProperty.getStringValue());
 			}
 		}
 	} 
 }
diff --git a/src/core/org/apache/jmeter/util/BeanShellTestElement.java b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
index 9afe25562..175450b29 100644
--- a/src/core/org/apache/jmeter/util/BeanShellTestElement.java
+++ b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
@@ -1,286 +1,276 @@
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
 
 package org.apache.jmeter.util;
 
 import java.io.Serializable;
 import java.util.List;
 
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public abstract class BeanShellTestElement extends AbstractTestElement
     implements Serializable, Cloneable, ThreadListener, TestStateListener
 {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 4;
 
     //++ For TestBean implementations only
     private String parameters; // passed to file or script
 
     private String filename; // file to source (overrides script)
 
     private String script; // script (if file not provided)
 
     private boolean resetInterpreter = false;
     //-- For TestBean implementations only
 
 
     private transient BeanShellInterpreter bshInterpreter = null;
 
     private transient boolean hasInitFile = false;
 
     public BeanShellTestElement() {
         super();
         init();
     }
 
     protected abstract String getInitFileProperty();
 
     /**
      * Get the interpreter and set up standard script variables.
      * <p>
      * Sets the following script variables:
      * <ul>
      * <li>ctx</li>
      * <li>Label</li>
      * <li>prev</li>
      * <li>props</li>
      * <li>vars</li>
      * </ul>
      * @return the interpreter
      */
     protected BeanShellInterpreter getBeanShellInterpreter() {
         if (isResetInterpreter()) {
             try {
                 bshInterpreter.reset();
             } catch (ClassNotFoundException e) {
                 log.error("Cannot reset BeanShell: "+e.toString());
             }
         }
 
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
 
         try {
             bshInterpreter.set("ctx", jmctx);//$NON-NLS-1$
             bshInterpreter.set("Label", getName()); //$NON-NLS-1$
             bshInterpreter.set("prev", jmctx.getPreviousResult());//$NON-NLS-1$
             bshInterpreter.set("props", JMeterUtils.getJMeterProperties());
             bshInterpreter.set("vars", vars);//$NON-NLS-1$
         } catch (JMeterException e) {
             log.warn("Problem setting one or more BeanShell variables "+e);
         }
         return bshInterpreter;
     }
 
     private void init() {
         parameters=""; // ensure variables are not null
         filename="";
         script="";
         try {
             String initFileName = JMeterUtils.getProperty(getInitFileProperty());
             hasInitFile = initFileName != null;
             bshInterpreter = new BeanShellInterpreter(initFileName, log);
         } catch (ClassNotFoundException e) {
             log.error("Cannot find BeanShell: "+e.toString());
         }
     }
 
     protected Object readResolve() {
         init();
         return this;
     }
 
     @Override
     public Object clone() {
         BeanShellTestElement o = (BeanShellTestElement) super.clone();
         o.init();
        return o;
     }
 
     /**
      * Process the file or script from the test element.
      * <p>
      * Sets the following script variables:
      * <ul>
      * <li>FileName</li>
      * <li>Parameters</li>
      * <li>bsh.args</li>
      * </ul>
      * @param bsh the interpreter, not {@code null}
      * @return the result of the script, may be {@code null}
      * 
      * @throws JMeterException
      */
     protected Object processFileOrScript(BeanShellInterpreter bsh) throws JMeterException{
         String fileName = getFilename();
         String params = getParameters();
 
         bsh.set("FileName", fileName);//$NON-NLS-1$
         // Set params as a single line
         bsh.set("Parameters", params); // $NON-NLS-1$
         // and set as an array
         bsh.set("bsh.args",//$NON-NLS-1$
                 JOrphanUtils.split(params, " "));//$NON-NLS-1$
 
         if (fileName.length() == 0) {
             return bsh.eval(getScript());
         }
         return bsh.source(fileName);
     }
 
     /**
      * Return the script (TestBean version).
      * Must be overridden for subclasses that don't implement TestBean
      * otherwise the clone() method won't work.
      *
      * @return the script to execute
      */
     public String getScript(){
         return script;
     }
 
     /**
      * Set the script (TestBean version).
      * Must be overridden for subclasses that don't implement TestBean
      * otherwise the clone() method won't work.
      *
      * @param s the script to execute (may be blank)
      */
     public void setScript(String s){
         script=s;
     }
 
     public void threadStarted() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("threadStarted()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     public void threadFinished() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("threadFinished()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     public void testEnded() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("testEnded()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     public void testEnded(String host) {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.eval((new StringBuilder("testEnded(")) // $NON-NLS-1$
                     .append(host)
                     .append(")") // $NON-NLS-1$
                     .toString()); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     public void testStarted() {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.evalNoLog("testStarted()"); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     public void testStarted(String host) {
         if (bshInterpreter == null || !hasInitFile) {
             return;
         }
         try {
             bshInterpreter.eval((new StringBuilder("testStarted(")) // $NON-NLS-1$
                     .append(host)
                     .append(")") // $NON-NLS-1$
                     .toString()); // $NON-NLS-1$
         } catch (JMeterException ignored) {
             log.debug(getClass().getName() + " : " + ignored.getLocalizedMessage()); // $NON-NLS-1$
         }
     }
 
     // Overridden by non-TestBean implementations to return the property value instead
     public String getParameters() {
         return parameters;
     }
 
     public void setParameters(String s) {
         parameters = s;
     }
 
     // Overridden by non-TestBean implementations to return the property value instead
     public String getFilename() {
         return filename;
     }
 
     public void setFilename(String s) {
         filename = s;
     }
 
     public boolean isResetInterpreter() {
         return resetInterpreter;
     }
 
     public void setResetInterpreter(boolean b) {
         resetInterpreter = b;
     }
-    
-    /** 
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getScript());
-        return result;
-    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index b523c9cc2..fb8ed01f7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -862,1029 +862,1009 @@ public abstract class HTTPSamplerBase extends AbstractSampler
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
      * @throws MalformedURLException
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
         for (int i = 0; i < args.length; i++) {
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
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
             res.setSampleLabel(getName());
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
      * @return results of the sampling
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
                     urls = parser.getEmbeddedResourceURLs(responseData, res.getURL(), res.getDataEncodingWithDefault());
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
                 // TODO needed here because currently done on sample completion in JMeterThread,
                 // but that only catches top-level samples.
                 res.setThreadName(Thread.currentThread().getName());
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
             final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<Callable<AsynSamplerResultHolder>>();
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc=encodeSpaces(urlstr);
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
                             setParentSampleSuccess(res, res.isSuccessful() && binRes.isSuccessful());
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
                 // Thread pool Executor to get resources 
                 // use a LinkedBlockingQueue, note: max pool size doesn't effect
                 final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                         poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                         new LinkedBlockingQueue<Runnable>(),
                         new ThreadFactory() {
                             public Thread newThread(final Runnable r) {
                                 Thread t = new CleanerThread(new Runnable() {
                                     public void run() {
                                         try {
                                             r.run();
                                         } finally {
                                             ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
                                         }
                                     }
                                 });
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
                                 PropertyIterator iter = cookies.iterator();
                                 while (iter.hasNext()) {
                                     Cookie cookie = (Cookie) iter.next().getObjectValue();
                                     cookieManager.add(cookie) ;
                                 }
                             }
                             res.addSubResult(binRes.getResult());
                             setParentSampleSuccess(res, res.isSuccessful() && binRes.getResult().isSuccessful());
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
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
 		if(!IGNORE_FAILED_EMBEDDED_RESOURCES) {
 			res.setSuccessful(initialValue);
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
     public void testEnded() {
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded(String host) {
         testEnded();
     }
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void testIterationStart(LoopIterationEvent event) {
 		if (!USE_CACHED_SSL_CONTEXT) {
 			JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
 			sslMgr.resetContext();
 			notifySSLContextWasReset();
 		}
 	}
 
 	/**
 	 * Called by testIterationStart if the SSL Context was reset.
 	 * 
 	 * This implementation does nothing.
 	 */
 	protected void notifySSLContextWasReset() {
 		// NOOP
 	}
 
     /**
      * {@inheritDoc}
      */
     public void testStarted() {
     }
 
     /**
      * {@inheritDoc}
      */
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
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             String location = lastRes.getRedirectLocation(); 
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             location = encodeSpaces(location);
             try {
                 lastRes = sample(ConversionUtils.makeRelativeURL(lastRes.getURL(), location), HTTPConstants.GET, true, frameDepth);
             } catch (MalformedURLException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (int i = 0; i < subs.length; i++) {
                     totalRes.addSubResult(subs[i]);
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
      * @param areFollowingRedirect
      * @param frameDepth
      * @param res
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
                 // Bug 51939 -  https://issues.apache.org/bugzilla/show_bug.cgi?id=51939
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
             for(int i=0; i < files.length; i++){
                 HTTPFileArg file = files[i];
                 if (file.isNotEmpty()){
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
     public void threadStarted(){
     }
 
     public void threadFinished(){
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      *
      * For the MD5 case, the result byte count is set to the size of the original response.
      * 
      * Closes the inputStream 
      * 
      * @param sampleResult
      * @param in input stream
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
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
      * - FILE_NAME
      * - FILE_FIELD
      * - MIMETYPE
      * These were stored in their own individual properties.
      *
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      *
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      *
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
                 for (int i = 0; i < infiles.length; i++){
                     allFileArgs.addHTTPFileArg(infiles[i]);
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
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE,"");
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
         private final List<HTTPSamplerBase> samplersToNotify = new ArrayList<HTTPSamplerBase>();
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
          * @param result
          * @param cookies
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
     
-    
-    /** 
-     * We search in URL and arguments
-     * {@inheritDoc}}
-     */
-    @Override
-    public List<String> getSearchableTokens() throws Exception {
-        List<String> result = super.getSearchableTokens();
-        result.add(getUrl().toExternalForm());
-        Arguments arguments = getArguments();
-        if(arguments != null) {
-            for (int i = 0; i < arguments.getArgumentCount(); i++) {
-                Argument argument = arguments.getArgument(i);
-                result.add(argument.getName());
-                result.add(argument.getValue());
-            }
-        }
-        return result;
-    }
-    
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
\ No newline at end of file
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
index af35fa86b..b344a6eb9 100644
--- a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/AbstractJDBCTestElement.java
@@ -1,626 +1,607 @@
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
 
 package org.apache.jmeter.protocol.jdbc;
 
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Field;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A base class for all JDBC test elements handling the basics of a SQL request.
  * 
  */
 public abstract class AbstractJDBCTestElement extends AbstractTestElement implements TestStateListener{
     private static final long serialVersionUID = 235L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String COMMA = ","; // $NON-NLS-1$
     private static final char COMMA_CHAR = ',';
 
     private static final String UNDERSCORE = "_"; // $NON-NLS-1$
 
     // String used to indicate a null value
     private static final String NULL_MARKER =
         JMeterUtils.getPropDefault("jdbcsampler.nullmarker","]NULL["); // $NON-NLS-1$
 
     private static final String INOUT = "INOUT"; // $NON-NLS-1$
 
     private static final String OUT = "OUT"; // $NON-NLS-1$
 
     // TODO - should the encoding be configurable?
     protected static final String ENCODING = "UTF-8"; // $NON-NLS-1$
 
     // key: name (lowercase) from java.sql.Types; entry: corresponding int value
     private static final Map<String, Integer> mapJdbcNameToInt;
     // read-only after class init
 
     static {
         // based on e291. Getting the Name of a JDBC Type from javaalmanac.com
         // http://javaalmanac.com/egs/java.sql/JdbcInt2Str.html
         mapJdbcNameToInt = new HashMap<String, Integer>();
 
         //Get all fields in java.sql.Types and store the corresponding int values
         Field[] fields = java.sql.Types.class.getFields();
         for (int i=0; i<fields.length; i++) {
             try {
                 String name = fields[i].getName();
                 Integer value = (Integer)fields[i].get(null);
                 mapJdbcNameToInt.put(name.toLowerCase(java.util.Locale.ENGLISH),value);
             } catch (IllegalAccessException e) {
                 throw new RuntimeException(e); // should not happen
             }
         }
     }
 
     // Query types (used to communicate with GUI)
     // N.B. These must not be changed, as they are used in the JMX files
     static final String SELECT   = "Select Statement"; // $NON-NLS-1$
     static final String UPDATE   = "Update Statement"; // $NON-NLS-1$
     static final String CALLABLE = "Callable Statement"; // $NON-NLS-1$
     static final String PREPARED_SELECT = "Prepared Select Statement"; // $NON-NLS-1$
     static final String PREPARED_UPDATE = "Prepared Update Statement"; // $NON-NLS-1$
     static final String COMMIT   = "Commit"; // $NON-NLS-1$
     static final String ROLLBACK = "Rollback"; // $NON-NLS-1$
     static final String AUTOCOMMIT_FALSE = "AutoCommit(false)"; // $NON-NLS-1$
     static final String AUTOCOMMIT_TRUE  = "AutoCommit(true)"; // $NON-NLS-1$
 
     private String query = ""; // $NON-NLS-1$
 
     private String dataSource = ""; // $NON-NLS-1$
 
     private String queryType = SELECT;
     private String queryArguments = ""; // $NON-NLS-1$
     private String queryArgumentsTypes = ""; // $NON-NLS-1$
     private String variableNames = ""; // $NON-NLS-1$
     private String resultVariable = "";
 
     /**
      *  Cache of PreparedStatements stored in a per-connection basis. Each entry of this
      *  cache is another Map mapping the statement string to the actual PreparedStatement.
      *  At one time a Connection is only held by one thread
      */
     private static final Map<Connection, Map<String, PreparedStatement>> perConnCache =
             new ConcurrentHashMap<Connection, Map<String, PreparedStatement>>();
 
     /**
      * Creates a JDBCSampler.
      */
     protected AbstractJDBCTestElement() {
     }
     
     /**
      * Execute the test element.
      * 
      * @param conn a {@link SampleResult} in case the test should sample; <code>null</code> if only execution is requested
      * @throws UnsupportedOperationException if the user provided incorrect query type 
      */
     protected byte[] execute(Connection conn) throws SQLException, UnsupportedEncodingException, IOException, UnsupportedOperationException {
         log.debug("executing jdbc");
         Statement stmt = null;
         
         try {
             // Based on query return value, get results
             String _queryType = getQueryType();
             if (SELECT.equals(_queryType)) {
                 stmt = conn.createStatement();
                 ResultSet rs = null;
                 try {
                     rs = stmt.executeQuery(getQuery());
                     return getStringFromResultSet(rs).getBytes(ENCODING);
                 } finally {
                     close(rs);
                 }
             } else if (CALLABLE.equals(_queryType)) {
                 CallableStatement cstmt = getCallableStatement(conn);
                 int out[]=setArguments(cstmt);
                 // A CallableStatement can return more than 1 ResultSets
                 // plus a number of update counts.
                 boolean hasResultSet = cstmt.execute();
                 String sb = resultSetsToString(cstmt,hasResultSet, out);
                 return sb.getBytes(ENCODING);
             } else if (UPDATE.equals(_queryType)) {
                 stmt = conn.createStatement();
                 stmt.executeUpdate(getQuery());
                 int updateCount = stmt.getUpdateCount();
                 String results = updateCount + " updates";
                 return results.getBytes(ENCODING);
             } else if (PREPARED_SELECT.equals(_queryType)) {
                 PreparedStatement pstmt = getPreparedStatement(conn);
                 setArguments(pstmt);
                 ResultSet rs = null;
                 try {
                     rs = pstmt.executeQuery();
                     return getStringFromResultSet(rs).getBytes(ENCODING);
                 } finally {
                     close(rs);
                 }
             } else if (PREPARED_UPDATE.equals(_queryType)) {
                 PreparedStatement pstmt = getPreparedStatement(conn);
                 setArguments(pstmt);
                 pstmt.executeUpdate();
                 String sb = resultSetsToString(pstmt,false,null);
                 return sb.getBytes(ENCODING);
             } else if (ROLLBACK.equals(_queryType)){
                 conn.rollback();
                 return ROLLBACK.getBytes(ENCODING);
             } else if (COMMIT.equals(_queryType)){
                 conn.commit();
                 return COMMIT.getBytes(ENCODING);
             } else if (AUTOCOMMIT_FALSE.equals(_queryType)){
                 conn.setAutoCommit(false);
                 return AUTOCOMMIT_FALSE.getBytes(ENCODING);
             } else if (AUTOCOMMIT_TRUE.equals(_queryType)){
                 conn.setAutoCommit(true);
                 return AUTOCOMMIT_TRUE.getBytes(ENCODING);
             } else { // User provided incorrect query type
                 throw new UnsupportedOperationException("Unexpected query type: "+_queryType);
             }
         } finally {
             close(stmt);
         }
     }
 
     private String resultSetsToString(PreparedStatement pstmt, boolean result, int[] out) throws SQLException, UnsupportedEncodingException {
         StringBuilder sb = new StringBuilder();
         int updateCount = 0;
         if (!result) {
             updateCount = pstmt.getUpdateCount();
         }
         do {
             if (result) {
                 ResultSet rs = null;
                 try {
                     rs = pstmt.getResultSet();
                     sb.append(getStringFromResultSet(rs)).append("\n"); // $NON-NLS-1$
                 } finally {
                     close(rs);
                 }
             } else {
                 sb.append(updateCount).append(" updates.\n");
             }
             result = pstmt.getMoreResults();
             if (!result) {
                 updateCount = pstmt.getUpdateCount();
             }
         } while (result || (updateCount != -1));
         if (out!=null && pstmt instanceof CallableStatement){
             ArrayList<Object> outputValues = new ArrayList<Object>();
             CallableStatement cs = (CallableStatement) pstmt;
             sb.append("Output variables by position:\n");
             for(int i=0; i < out.length; i++){
                 if (out[i]!=java.sql.Types.NULL){
                     Object o = cs.getObject(i+1);
                     outputValues.add(o);
                     sb.append("[");
                     sb.append(i+1);
                     sb.append("] ");
                     sb.append(o);
                     sb.append("\n");
                 }
             }
             String varnames[] = getVariableNames().split(COMMA);
             if(varnames.length > 0) {
         	JMeterVariables jmvars = getThreadContext().getVariables();
                 for(int i = 0; i < varnames.length && i < outputValues.size(); i++) {
                     String name = varnames[i].trim();
                     if (name.length()>0){ // Save the value in the variable if present
                         Object o = outputValues.get(i);
                         jmvars.put(name, o == null ? null : o.toString());
                     }
                 }
             }
         }
         return sb.toString();
     }
 
 
     private int[] setArguments(PreparedStatement pstmt) throws SQLException, IOException {
         if (getQueryArguments().trim().length()==0) {
             return new int[]{};
         }
         String[] arguments = CSVSaveService.csvSplitString(getQueryArguments(), COMMA_CHAR);
         String[] argumentsTypes = getQueryArgumentsTypes().split(COMMA);
         if (arguments.length != argumentsTypes.length) {
             throw new SQLException("number of arguments ("+arguments.length+") and number of types ("+argumentsTypes.length+") are not equal");
         }
         int[] outputs= new int[arguments.length];
         for (int i = 0; i < arguments.length; i++) {
             String argument = arguments[i];
             String argumentType = argumentsTypes[i];
             String[] arg = argumentType.split(" ");
             String inputOutput="";
             if (arg.length > 1) {
                 argumentType = arg[1];
                 inputOutput=arg[0];
             }
             int targetSqlType = getJdbcType(argumentType);
             try {
                 if (!OUT.equalsIgnoreCase(inputOutput)){
                     if (argument.equals(NULL_MARKER)){
                         pstmt.setNull(i+1, targetSqlType);
                     } else {
                         pstmt.setObject(i+1, argument, targetSqlType);
                     }
                 }
                 if (OUT.equalsIgnoreCase(inputOutput)||INOUT.equalsIgnoreCase(inputOutput)) {
                     CallableStatement cs = (CallableStatement) pstmt;
                     cs.registerOutParameter(i+1, targetSqlType);
                     outputs[i]=targetSqlType;
                 } else {
                     outputs[i]=java.sql.Types.NULL; // can't have an output parameter type null
                 }
             } catch (NullPointerException e) { // thrown by Derby JDBC (at least) if there are no "?" markers in statement
                 throw new SQLException("Could not set argument no: "+(i+1)+" - missing parameter marker?");
             }
         }
         return outputs;
     }
 
 
     private static int getJdbcType(String jdbcType) throws SQLException {
         Integer entry = mapJdbcNameToInt.get(jdbcType.toLowerCase(java.util.Locale.ENGLISH));
         if (entry == null) {
             try {
                 entry = Integer.decode(jdbcType);
             } catch (NumberFormatException e) {
                 throw new SQLException("Invalid data type: "+jdbcType);
             }
         }
         return (entry).intValue();
     }
 
 
     private CallableStatement getCallableStatement(Connection conn) throws SQLException {
         return (CallableStatement) getPreparedStatement(conn,true);
 
     }
     private PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
         return getPreparedStatement(conn,false);
     }
 
     private PreparedStatement getPreparedStatement(Connection conn, boolean callable) throws SQLException {
         Map<String, PreparedStatement> preparedStatementMap = perConnCache.get(conn);
         if (null == preparedStatementMap ) {
             preparedStatementMap = new ConcurrentHashMap<String, PreparedStatement>();
             // As a connection is held by only one thread, we cannot already have a 
             // preparedStatementMap put by another thread
             perConnCache.put(conn, preparedStatementMap);
         }
         PreparedStatement pstmt = preparedStatementMap.get(getQuery());
         if (null == pstmt) {
             if (callable) {
                 pstmt = conn.prepareCall(getQuery());
             } else {
                 pstmt = conn.prepareStatement(getQuery());
             }
             // PreparedStatementMap is associated to one connection so 
             //  2 threads cannot use the same PreparedStatement map at the same time
             preparedStatementMap.put(getQuery(), pstmt);
         }
         pstmt.clearParameters();
         return pstmt;
     }
 
     private static void closeAllStatements(Collection<PreparedStatement> collection) {
         for (PreparedStatement pstmt : collection) {
             close(pstmt);
         }
     }
 
     /**
      * Gets a Data object from a ResultSet.
      *
      * @param rs
      *            ResultSet passed in from a database query
      * @return a Data object
      * @throws java.sql.SQLException
      * @throws UnsupportedEncodingException
      */
     private String getStringFromResultSet(ResultSet rs) throws SQLException, UnsupportedEncodingException {
         ResultSetMetaData meta = rs.getMetaData();
 
         StringBuilder sb = new StringBuilder();
 
         int numColumns = meta.getColumnCount();
         for (int i = 1; i <= numColumns; i++) {
             sb.append(meta.getColumnName(i));
             if (i==numColumns){
                 sb.append('\n');
             } else {
                 sb.append('\t');
             }
         }
         
 
         JMeterVariables jmvars = getThreadContext().getVariables();
         String varnames[] = getVariableNames().split(COMMA);
         String resultVariable = getResultVariable().trim();
         List<Map<String, Object> > results = null;
         if(resultVariable.length() > 0) {
             results = new ArrayList<Map<String,Object> >();
             jmvars.putObject(resultVariable, results);
         }
         int j = 0;
         while (rs.next()) {
             Map<String, Object> row = null;
             j++;
             for (int i = 1; i <= numColumns; i++) {
                 Object o = rs.getObject(i);
                 if(results != null) {
                     if(row == null) {
                         row = new HashMap<String, Object>(numColumns);
                         results.add(row);
                     }
                     row.put(meta.getColumnName(i), o);
                 }
                 if (o instanceof byte[]) {
                     o = new String((byte[]) o, ENCODING);
                 }
                 sb.append(o);
                 if (i==numColumns){
                     sb.append('\n');
                 } else {
                     sb.append('\t');
                 }
                 if (i <= varnames.length) { // i starts at 1
                     String name = varnames[i - 1].trim();
                     if (name.length()>0){ // Save the value in the variable if present
                         jmvars.put(name+UNDERSCORE+j, o == null ? null : o.toString());
                     }
                 }
             }
         }
         // Remove any additional values from previous sample
         for(int i=0; i < varnames.length; i++){
             String name = varnames[i].trim();
             if (name.length()>0 && jmvars != null){
                 final String varCount = name+"_#"; // $NON-NLS-1$
                 // Get the previous count
                 String prevCount = jmvars.get(varCount);
                 if (prevCount != null){
                     int prev = Integer.parseInt(prevCount);
                     for (int n=j+1; n <= prev; n++ ){
                         jmvars.remove(name+UNDERSCORE+n);
                     }
                 }
                 jmvars.put(varCount, Integer.toString(j)); // save the current count
             }
         }
 
         return sb.toString();
     }
 
     public static void close(Connection c) {
         try {
             if (c != null) {
                 c.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing Connection", e);
         }
     }
 
     public static void close(Statement s) {
         try {
             if (s != null) {
                 s.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing Statement " + s.toString(), e);
         }
     }
 
     public static void close(ResultSet rs) {
         try {
             if (rs != null) {
                 rs.close();
             }
         } catch (SQLException e) {
             log.warn("Error closing ResultSet", e);
         }
     }
 
     public String getQuery() {
         return query;
     }
 
     @Override
     public String toString() {
         StringBuilder sb = new StringBuilder(80);
         sb.append("["); // $NON-NLS-1$
         sb.append(getQueryType());
         sb.append("] "); // $NON-NLS-1$
         sb.append(getQuery());
         sb.append("\n");
         sb.append(getQueryArguments());
         sb.append("\n");
         sb.append(getQueryArgumentsTypes());
         return sb.toString();
     }
 
     /**
      * @param query
      *            The query to set.
      */
     public void setQuery(String query) {
         this.query = query;
     }
 
     /**
      * @return Returns the dataSource.
      */
     public String getDataSource() {
         return dataSource;
     }
 
     /**
      * @param dataSource
      *            The dataSource to set.
      */
     public void setDataSource(String dataSource) {
         this.dataSource = dataSource;
     }
 
     /**
      * @return Returns the queryType.
      */
     public String getQueryType() {
         return queryType;
     }
 
     /**
      * @param queryType The queryType to set.
      */
     public void setQueryType(String queryType) {
         this.queryType = queryType;
     }
 
     public String getQueryArguments() {
         return queryArguments;
     }
 
     public void setQueryArguments(String queryArguments) {
         this.queryArguments = queryArguments;
     }
 
     public String getQueryArgumentsTypes() {
         return queryArgumentsTypes;
     }
 
     public void setQueryArgumentsTypes(String queryArgumentsType) {
         this.queryArgumentsTypes = queryArgumentsType;
     }
 
     /**
      * @return the variableNames
      */
     public String getVariableNames() {
         return variableNames;
     }
 
     /**
      * @param variableNames the variableNames to set
      */
     public void setVariableNames(String variableNames) {
         this.variableNames = variableNames;
     }
 
     /**
      * @return the resultVariable
      */
     public String getResultVariable() {
         return resultVariable ;
     }
 
     /**
      * @param resultVariable the variable name in which results will be stored
      */
     public void setResultVariable(String resultVariable) {
         this.resultVariable = resultVariable;
     }    
 
-    /** 
-     * {@inheritDoc}}
-	 */
-	@Override
-	public List<String> getSearchableTokens() throws Exception {
-		List<String> result = super.getSearchableTokens();
-		Set<String> properties = new HashSet<String>();
-		properties.addAll(Arrays.asList(new String[]{
-			"dataSource",
-			"query",
-			"queryArguments",
-			"queryArgumentsTypes",
-			"queryType",
-			"resultVariable",
-			"variableNames"
-		}));
-		addPropertiesValues(result, properties);
-        return result;
-	}
 
 	/** 
 	 * {@inheritDoc}
 	 * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
 	 */
 	public void testStarted() {
 		testStarted("");
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * @see org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
 	 */
 	public void testStarted(String host) {
 		cleanCache();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
 	 */
 	public void testEnded() {
 		testEnded("");
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * @see org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
 	 */
 	public void testEnded(String host) {
 		cleanCache();		
 	}
 	
 	/**
 	 * Clean cache of PreparedStatements
 	 */
 	private static final void cleanCache() {
 		for (Map<String, PreparedStatement> element : perConnCache.values()) {
 			closeAllStatements(element.values());
 		}
 		perConnCache.clear();
 	}
 
 }
\ No newline at end of file
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 7b38e22a8..fc22026e7 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,191 +1,192 @@
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
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.9 =================== -->
 
 <h1>Version 2.9</h1>
 
 <h2>New and Noteworthy</h2>
 
 <h3>Core Improvements:</h3>
 <h4>Webservice sampler now adds to request the headers that are set through Header Manager</h4>
 
 <h3>GUI and ergonomy Improvements:</h3>
 <h4>Allow copy from clipboard to HeaderPanel, headers are supposed to be separated by new line and have the following form name:value</h4>
 
 <h4>Proxy now has a button to add a set of default exclusions for URL patterns, this list can be configured through property : proxy.excludes.suggested</h4>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </p>
 
 <p>
 Changing language can break part of the configuration of the following elements (see <bugzilla>53679</bugzilla>):
 <ul>
     <li>CSV Data Set Config (sharing mode will be lost)</li>
     <li>Constant Throughput Timer (Calculate throughput based on will be lost)</li>
 </ul>
 </p>
 
 <p>
 Note that there is a bug in Java on some Linux systems that manifests
 itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation.
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>Webservice sampler now adds to request the headers that are set through Header Manager, these were previously ignored</p>
 
 <p>jdbcsampler.cachesize property has been removed, it previously limited the size of a per connection cache of Map &lt; String, PreparedStatement &gt; , it also limited the size of this
 map which held the PreparedStatement for SQL queries. This limitation provoked a bug <bugzilla>53995</bugzilla>. 
 It has been removed so now size of these 2 maps is not limited anymore. This change changes behaviour as starting from this version no PreparedStatement will be closed during the test.</p>
 
 <p>Starting with this version JSR223 Test Elements that have an invalid filename (not existing or unreadable) will make test fail instead of making the element silently work</p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Don't log spurious warning messages when using concurrent pool embedded downloads with Cache Manager or CookieManager</li>
 <li><bugzilla>54057</bugzilla>- Proxy option to set user and password at startup (-u and -a) not working with HTTPClient 4</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>53997</bugzilla> - LDAP Extended Request: Escape ampersand (&amp;), left angle bracket (&lt;) 
 and right angle bracket (&gt;) in search filter tag in XML response data</li>
 <li><bugzilla>53995</bugzilla> - AbstractJDBCTestElement shares PreparedStatement between multi-threads</li>
 <li><bugzilla>54119</bugzilla> - HTTP 307 response is not redirected</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54088</bugzilla> - The type video/f4m is text, not binary</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54058</bugzilla> - In HTTP Request Defaults, the value of field "Embedded URLs must match: is not saved if the check box "Retrieve All  Embedded Resources" is not checked.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53975</bugzilla> - Variables replacement doesn't work with option "Delay thread creation until needed"</li>
 <li><bugzilla>54055</bugzilla> - View Results tree: = signs are stripped from parameter values at HTTP tab</li>
+<li><bugzilla>54129</bugzilla> - Search Feature does not find text although existing in elements </li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54004</bugzilla> - Webservice Sampler : Allow adding headers to request with Header Manager</li>
 <li><bugzilla>54106</bugzilla> - JSR223TestElement should check for file existence when a filename is set instead of using Text Area content </li>
 <li><bugzilla>54107</bugzilla> - JSR223TestElement : Enable compilation and caching of Script Text</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
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
 <li><bugzilla>54005</bugzilla> - HTTP Mirror Server : Add special headers "X-" to control Response status and response content</li>
 <li><bugzilla>53875</bugzilla> - Include suggested defaults for URL filters on HTTP Proxy</li>
 <li><bugzilla>54031</bugzilla> - Add tooltip to running/total threads indicator </li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li><bugzilla>53956</bugzilla> - Add ability to paste (a list of values) from clipboard for Header Manager</li>
 <li>Updated to HttpComponents Client 4.2.2 (from 4.2.1)</li>
 <li><bugzilla>54110</bugzilla> - BSFTestElement and JSR223TestElement should use shared super-class for common fields</li>
 </ul>
 
 </section> 
 </body> 
 </document>
\ No newline at end of file
