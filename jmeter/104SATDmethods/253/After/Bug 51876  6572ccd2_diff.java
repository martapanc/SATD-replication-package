diff --git a/src/components/org/apache/jmeter/config/CSVDataSet.java b/src/components/org/apache/jmeter/config/CSVDataSet.java
index b6fc34b7d..d0009dad0 100644
--- a/src/components/org/apache/jmeter/config/CSVDataSet.java
+++ b/src/components/org/apache/jmeter/config/CSVDataSet.java
@@ -1,259 +1,272 @@
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
 
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
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
 public class CSVDataSet extends ConfigTestElement implements TestBean, LoopIterationListener {
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
             String line = null;
             try {
                 line = server.readLine(alias, getRecycle(), firstLineIsNames);
             } catch (IOException e) { // treat the same as EOF
                 log.error(e.toString());
             }
             if (line!=null) {// i.e. not EOF
                 try {
                     String[] lineValues = getQuotedData() ?
                             CSVSaveService.csvSplitString(line, delim.charAt(0))
                             : JOrphanUtils.split(line, delim, false);
                             for (int a = 0; a < vars.length && a < lineValues.length; a++) {
                                 threadVars.put(vars[a], lineValues[a]);
                             }
                 } catch (IOException e) { // Should only happen for quoting errors
                    log.error("Unexpected error splitting '"+line+"' on '"+delim.charAt(0)+"'");
                 }
                 // TODO - report unused columns?
                 // TODO - provide option to set unused variables ?
             } else {
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
 
+    /** 
+     * {@inheritDoc}}
+     */
+    public boolean searchContent(String textToSearch) throws Exception {
+        if(super.searchContent(textToSearch)) {
+            return true;
+        }
+        String searchedTextLowerCase = textToSearch.toLowerCase();
+        if(testField(getPropertyAsString("variableNames"), searchedTextLowerCase)) {
+            return true;
+        }
+        return false;
+    }
 }
diff --git a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
index d145006b0..ab5346449 100644
--- a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
@@ -1,83 +1,68 @@
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
 
-import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.log.Logger;
 
 public class BeanShellPostProcessor extends BeanShellTestElement
-    implements Cloneable, PostProcessor, TestBean, Searchable
+    implements Cloneable, PostProcessor, TestBean
 {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 4;
 
     // can be specified in jmeter.properties
     private static final String INIT_FILE = "beanshell.postprocessor.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
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
             log.warn("Problem in BeanShell script "+e);
         }
     }
-     
-     /**
-      * {@inheritDoc}
-      */
-     public boolean searchContent(String textToSearch) throws Exception {
-         String searchedTextLowerCase = textToSearch.toLowerCase();
-         if(testField(getComment(), searchedTextLowerCase)) {
-             return true;
-         }
-         if(testField(getScript(), searchedTextLowerCase)) {
-             return true;
-         }
-         return false;
-     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index de723224b..7c31fd5e8 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,480 +1,476 @@
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
 
 import org.apache.commons.lang.StringEscapeUtils;
-import org.apache.jmeter.gui.Searchable;
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
 
-public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable, Searchable {
+public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
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
                 : useUnescapedBody() ? StringEscapeUtils.unescapeHtml(result.getResponseDataAsString())
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
 
     /**
      * {@inheritDoc}
      */
     public boolean searchContent(String textToSearch) throws Exception {
-        String searchedTextLowerCase = textToSearch.toLowerCase();
-        if(testField(getComment(), searchedTextLowerCase)) {
-            return true;
-        }
-        if(testField(getVariableName(), searchedTextLowerCase)) {
+        if(super.searchContent(textToSearch)) {
             return true;
         }
+        String searchedTextLowerCase = textToSearch.toLowerCase();
         if(testField(getRefName(), searchedTextLowerCase)) {
             return true;
         }
         if(testField(getDefaultValue(), searchedTextLowerCase)) {
             return true;
         }
         if(testField(getRegex(), searchedTextLowerCase)) {
             return true;
         }
         return false;
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/XPathExtractor.java b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
index c4db8bc4b..ea03c4385 100644
--- a/src/components/org/apache/jmeter/extractor/XPathExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/XPathExtractor.java
@@ -1,414 +1,410 @@
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
 import java.io.StringWriter;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.OutputKeys;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.stream.StreamResult;
 
 import org.apache.jmeter.assertions.AssertionResult;
-import org.apache.jmeter.gui.Searchable;
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
 import org.apache.xpath.XPathAPI;
 import org.apache.xpath.objects.XObject;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
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
-        PostProcessor, Serializable, Searchable {
+        PostProcessor, Serializable {
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
         String val = null;
         XObject xObject = XPathAPI.eval(d, query);
         final int objectType = xObject.getType();
         if (objectType == XObject.CLASS_NODESET) {
             NodeList matches = xObject.nodelist();
             int length = matches.getLength();
             for (int i = 0 ; i < length; i++) {
                 Node match = matches.item(i);
                 if ( match instanceof Element){
                     if (getFragment()){
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
                 matchStrings.add(val);
             }
         } else if (objectType == XObject.CLASS_NULL
                 || objectType == XObject.CLASS_UNKNOWN
                 || objectType == XObject.CLASS_UNRESOLVEDVARIABLE) {
             log.warn("Unexpected object type: "+xObject.getTypeString()+" returned for: "+getXPathQuery());
         } else {
             val = xObject.toString();
             matchStrings.add(val);
       }
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
 
     private String getValueForNode(Node node) {
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
      * {@inheritDoc}
      */
     public boolean searchContent(String textToSearch) throws Exception {
-        String searchedTextLowerCase = textToSearch.toLowerCase();
-        if(testField(getComment(), searchedTextLowerCase)) {
-            return true;
-        }
-        if(testField(getVariableName(), searchedTextLowerCase)) {
+        if(super.searchContent(textToSearch)) {
             return true;
         }
+        String searchedTextLowerCase = textToSearch.toLowerCase();
         if(testField(getRefName(), searchedTextLowerCase)) {
             return true;
         }
         if(testField(getDefaultValue(), searchedTextLowerCase)) {
             return true;
         }
         if(testField(getXPathQuery(), searchedTextLowerCase)) {
             return true;
         }
         return false;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 5aa68461f..bf9e233bf 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,269 +1,254 @@
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
 
-import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterThread;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.threads.ListenerNotifier;
 import org.apache.jmeter.threads.SamplePackage;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Transaction Controller to measure transaction times
  *
  * There are two different modes for the controller:
  * - generate additional total sample after nested samples (as in JMeter 2.2)
  * - generate parent sampler containing the nested samples
  *
  */
-public class TransactionController extends GenericController implements SampleListener, Controller, Serializable, Searchable {
+public class TransactionController extends GenericController implements SampleListener, Controller, Serializable {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient TransactionSampler transactionSampler;
 
     private transient ListenerNotifier lnf;
 
     private transient SampleResult res;
 
     private transient int calls;
 
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      */
     private transient long prevEndTime;
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
     private final static String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
 
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
     private Object readResolve(){
         lnf = new ListenerNotifier();
         return this;
     }
 
     public void setParent(boolean _parent){
         setProperty(new BooleanProperty(PARENT, _parent));
     }
 
     public boolean isParent(){
         return getPropertyAsBoolean(PARENT);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next(){
         if (isParent()){
             return next1();
         }
         return next2();
     }
 
 ///////////////// Transaction Controller - parent ////////////////
 
     private Sampler next1() {
         // Check if transaction is done
         if(transactionSampler != null && transactionSampler.isTransactionDone()) {
             if (log.isDebugEnabled()) {
                 log.debug("End of transaction " + getName());
             }
             // This transaction is done
             transactionSampler = null;
             return null;
         }
 
         // Check if it is the start of a new transaction
         if (isFirst()) // must be the start of the subtree
         {
             if (log.isDebugEnabled()) {
                 log.debug("Start of transaction " + getName());
             }
             transactionSampler = new TransactionSampler(this, getName());
         }
 
         // Sample the children of the transaction
         Sampler subSampler = super.next();
         transactionSampler.setSubSampler(subSampler);
         // If we do not get any sub samplers, the transaction is done
         if (subSampler == null) {
             transactionSampler.setTransactionDone();
         }
         return transactionSampler;
     }
 
     @Override
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         if (!isParent()) {
             return super.nextIsAController(controller);
         }
         Sampler returnValue;
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             // We need to call the super.next, instead of this.next, which is done in GenericController,
             // because if we call this.next(), it will return the TransactionSampler, and we do not want that.
             // We need to get the next real sampler or controller
             returnValue = super.next();
         } else {
             returnValue = sampler;
         }
         return returnValue;
     }
 
 ////////////////////// Transaction Controller - additional sample //////////////////////////////
 
     private Sampler next2() {
         if (isFirst()) // must be the start of the subtree
         {
             calls = 0;
             noFailingSamples = 0;
             res = new SampleResult();
             res.setSampleLabel(getName());
             // Assume success
             res.setSuccessful(true);
             res.sampleStart();
             prevEndTime = res.getStartTime();//???
             pauseTime = 0;
         }
 
         Sampler returnValue = super.next();
 
         if (returnValue == null) // Must be the end of the controller
         {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
                  res.sampleEnd();
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
 
                 // TODO could these be done earlier (or just once?)
                 JMeterContext threadContext = getThreadContext();
                 JMeterVariables threadVars = threadContext.getVariables();
 
                 SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
                 if (pack == null) {
                     log.warn("Could not fetch SamplePackage");
                 } else {
                     SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
                     // We must set res to null now, before sending the event for the transaction,
                     // so that we can ignore that event in our sampleOccured method
                     res = null;
                     // bug 50032 
                     if (!getThreadContext().isReinitializingSubControllers()) {
                         lnf.notifyListeners(event, pack.getSampleListeners());
                     }
                 }
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
 
     public void sampleOccurred(SampleEvent se) {
         if (!isParent()) {
             // Check if we are still sampling our children
             if(res != null && !se.isTransactionSampleEvent()) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytes() + sampleResult.getBytes());
                 if (!isIncludeTimers()) {// Accumulate waiting time for later
                     pauseTime += sampleResult.getEndTime() - sampleResult.getTime() - prevEndTime;
                     prevEndTime = sampleResult.getEndTime();
                 }
                 if(!sampleResult.isSuccessful()) {
                     res.setSuccessful(false);
                     noFailingSamples++;
                 }
                 res.setAllThreads(sampleResult.getAllThreads());
                 res.setGroupThreads(sampleResult.getGroupThreads());
                 res.setLatency(res.getLatency() + sampleResult.getLatency());
             }
         }
     }
 
     public void sampleStarted(SampleEvent e) {
     }
 
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * Whether to include timers and pre/post processor time in overall sample.
      * @param includeTimers
      */
     public void setIncludeTimers(boolean includeTimers) {
         setProperty(INCLUDE_TIMERS, includeTimers, true); // default true for compatibility
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, true);
     }
-
-    /**
-     * {@inheritDoc}
-     */
-    public boolean searchContent(String textToSearch) throws Exception {
-        String searchedTextLowerCase = textToSearch.toLowerCase();
-        if(testField(getComment(), searchedTextLowerCase)) {
-            return true;
-        }
-        if(testField(getName(), searchedTextLowerCase)) {
-            return true;
-        }
-        return false;
-    }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
index 422b345ca..20146cce5 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
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
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterTreeNode extends DefaultMutableTreeNode implements NamedTreeNode {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
-    private static final int TEST_PLAN_LEVEL = 2;
+    private static final int TEST_PLAN_LEVEL = 1;
 
     private final JMeterTreeModel treeModel;
 
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
         return ((AbstractTestElement) getTestElement()).getPropertyAsBoolean(TestElement.ENABLED);
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setProperty(new BooleanProperty(TestElement.ENABLED, enabled));
         treeModel.nodeChanged(this);
     }
     
     /**
      * Return nodes to level 2
      * @return {@link List}<JMeterTreeNode>
      */
     public List<JMeterTreeNode> getPathToThreadGroup() {
         List<JMeterTreeNode> nodes = new ArrayList<JMeterTreeNode>();
         if(treeModel != null) {
             TreeNode[] nodesToRoot = treeModel.getPathToRoot(this);
             for (int i = 0; i < nodesToRoot.length; i++) {
                 JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) nodesToRoot[i];
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
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
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
diff --git a/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
index a73a84383..e04597383 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractScopedTestElement.java
@@ -1,159 +1,173 @@
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
+    
+    /**
+     * {@inheritDoc}
+     */
+    public boolean searchContent(String textToSearch) throws Exception {
+        if(super.searchContent(textToSearch)) {
+            return true;
+        }
+        String searchedTextLowerCase = textToSearch.toLowerCase();
+        if(testField(getVariableName(), searchedTextLowerCase)) {
+            return true;
+        }
+        return false;
+    }
 }
diff --git a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
index 2e91040b6..2111bbcce 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
@@ -1,538 +1,553 @@
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
 import java.util.Collections;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.commons.lang.StringUtils;
+import org.apache.jmeter.gui.Searchable;
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
-public abstract class AbstractTestElement implements TestElement, Serializable {
+public abstract class AbstractTestElement implements TestElement, Serializable, Searchable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Map<String, JMeterProperty> propMap =
         Collections.synchronizedMap(new LinkedHashMap<String, JMeterProperty>());
 
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
                 clonedElement.setProperty((JMeterProperty) iter.next().clone());
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
 
     protected void addProperty(JMeterProperty property) {
         if (isRunningVersion()) {
             setTemporary(property);
         } else {
             clearTemporary(property);
         }
         JMeterProperty prop = getProperty(property.getName());
 
         if (prop instanceof NullProperty || (prop instanceof StringProperty && prop.getStringValue().equals(""))) {
             propMap.put(property.getName(), property);
         } else {
             prop.mergeIn(property);
         }
     }
 
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
 
     protected void mergeIn(TestElement element) {
         PropertyIterator iter = element.propertyIterator();
         while (iter.hasNext()) {
             JMeterProperty prop = iter.next();
             addProperty(prop);
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
+     * {@inheritDoc}
+     */
+    public boolean searchContent(String textToSearch) throws Exception {
+        String searchedTextLowerCase = textToSearch.toLowerCase();
+        if(testField(getComment(), searchedTextLowerCase)) {
+            return true;
+        }
+        if(testField(getName(), searchedTextLowerCase)) {
+            return true;
+        }
+        return false;
+    }
+    
+    /**
      * Returns true if searchedTextLowerCase is in value
      * @param value
      * @param searchedTextLowerCase
-     * @return true if searchedTextLowerCase is in value
+     * @return
      */
     protected boolean testField(String value, String searchedTextLowerCase) {
         if(!StringUtils.isEmpty(value)) {
             return value.toLowerCase().indexOf(searchedTextLowerCase)>=0;
         }
         return false;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/BeanShellTestElement.java b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
index cc1f2d021..a90bd69ca 100644
--- a/src/core/org/apache/jmeter/util/BeanShellTestElement.java
+++ b/src/core/org/apache/jmeter/util/BeanShellTestElement.java
@@ -1,282 +1,296 @@
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
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public abstract class BeanShellTestElement extends AbstractTestElement
     implements Serializable, Cloneable, ThreadListener, TestListener
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
 
     public void testIterationStart(LoopIterationEvent event) {
         // Not implemented
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
+    
+    /**
+     * {@inheritDoc}
+     */
+    public boolean searchContent(String textToSearch) throws Exception {
+        if(super.searchContent(textToSearch)) {
+            return true;
+        }
+        String searchedTextLowerCase = textToSearch.toLowerCase();
+        if(testField(getScript(), searchedTextLowerCase)) {
+            return true;
+        }
+        return false;
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index e3602cd02..be0a64482 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1725 +1,1724 @@
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
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
-import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
-    implements TestListener, ThreadListener, HTTPConstantsInterface, Searchable {
+    implements TestListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
     
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
     
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     private static final String CONCURRENT_POOL_DEFAULT = "4"; // default for concurrent pool (do not change)
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
     
     private static final long AWAIT_TERMINATION_TIMEOUT = 
         JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
     
     public static final int CONCURRENT_POOL_SIZE = 4; // Default concurrent pool size for download embedded resources
     
     
     public static final String DEFAULT_METHOD = GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
         DEFAULT_METHOD, // i.e. GET
         POST,
         HEAD,
         PUT,
         OPTIONS,
         TRACE,
         DELETE,
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private final static String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
 	// Control reuse of cached SSL Context in subsequent iterations
 	private static final boolean USE_CACHED_SSL_CONTEXT = 
 	        JMeterUtils.getPropDefault("https.use.cached.ssl.context", true);//$NON-NLS-1$
     
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
         
 		log.info("Reuse SSL session context on subsequent iterations: "
 				+ USE_CACHED_SSL_CONTEXT);
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Variables //////////////////////
 
     private boolean dynamicPath = false;// Set false if spaces are already encoded
 
 
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Post body,
      * i.e. without any additional wrapping
      *
      * @return true if specified file is to be sent as the body,
      * i.e. FileField is blank
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
      * as the post body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
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
         if(POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
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
      * Sets the PATH property; also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments if the request is a GET or DELETE.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         if (GET.equals(getMethod()) || DELETE.equals(getMethod())) {
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
         if (dynamicPath) {
             return encodeSpaces(p);
         }
         return p;
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
 
     public void setContentEncoding(String value) {
         setProperty(CONTENT_ENCODING, value);
     }
 
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
         if(contentEncoding != null) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(contentEncoding != null) {
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
                 protocol.equalsIgnoreCase(PROTOCOL_HTTP)  ? DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(PROTOCOL_HTTPS) ? DEFAULT_HTTPS_PORT :
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
                 (PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == DEFAULT_HTTP_PORT) ||
                 (PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == DEFAULT_HTTPS_PORT)) {
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
             if (PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return DEFAULT_HTTPS_PORT;
             }
             if (!PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return DEFAULT_HTTP_PORT;
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
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
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
      * Obtain a result that will help inform the user that an error has occured
      * during sampling, and how long it took to detect the error.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult
      * @return a sampling result useful to inform the user about the exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error: " + res.getSampleLabel());
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
 
     private static final String HTTP_PREFIX = PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
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
         if(GET.equals(getMethod()) || DELETE.equals(getMethod())) {
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
          if(contentEncoding == null || contentEncoding.trim().length() == 0) {
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
                 if(contentEncoding != null) {
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
             if(POST.equals(getMethod()) || PUT.equals(getMethod())) {
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
                     urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, res));
             res.setSuccessful(false);
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
             final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();
             
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
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                 res.setSuccessful(false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         
                         if (isConcurrentDwn()) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             liste.add(new ASyncSample(url, GET, false, frameDepth + 1));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                     res.setSuccessful(false);
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
                         new LinkedBlockingQueue<Runnable>());
 
                 boolean tasksCompleted = false;
                 try {
                     // sample all resources with threadpool
                     final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
                     // call normal shutdown (wait ending all tasks)
                     exec.shutdown();
                     // put a timeout if tasks couldn't terminate
                     exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
 
                     // add result to main sampleResult
                     for (Future<HTTPSampleResult> future : retExec) {
                         HTTPSampleResult binRes;
                         try {
                             binRes = future.get(1, TimeUnit.MILLISECONDS);
                             res.addSubResult(binRes);
                             res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
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
         dynamicPath = false;
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
         JMeterProperty pathP = getProperty(PATH);
         log.debug("path property is a " + pathP.getClass().getName());
         log.debug("path beginning value = " + pathP.getStringValue());
         if (pathP instanceof StringProperty && pathP.getStringValue().length() > 0) {
             log.debug("Encoding spaces in path");
             pathP.setObjectValue(encodeSpaces(pathP.getStringValue()));
             dynamicPath = false;
         } else {
             log.debug("setting dynamic path to true");
             dynamicPath = true;
         }
         log.debug("path ending value = " + pathP.getStringValue());
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
         base.dynamicPath = dynamicPath;
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
                 lastRes = sample(ConversionUtils.makeRelativeURL(lastRes.getURL(), location), GET, true, frameDepth);
             } catch (MalformedURLException e) {
                 lastRes = errorResult(e, lastRes);
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
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), lastRes);
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
         if (isImageParser() && (HTTPSampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), res));
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
         return METHODLIST.toArray(new String[0]);
     }
 
     public static boolean isSecure(String protocol){
         return PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
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
      * Closes the inputStream (unless there was an error)
      * 
      * @param sampleResult
      * @param in input stream
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
 
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
     public class ASyncSample implements Callable<HTTPSampleResult> {
         final private URL url;
         final private String method;
         final private boolean areFollowingRedirect;
         final private int depth;
 
         public ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth){
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
         }
 
         public HTTPSampleResult call() {
             return sample(url, method, areFollowingRedirect, depth);
         }
     }
 
     /**
      * We search in URL and arguments
      * TODO Can be enhanced
      * {@inheritDoc}
      */
     public boolean searchContent(String textToSearch) throws Exception {
+        if(super.searchContent(textToSearch)) {
+            return true;
+        }
         String searchedTextLowerCase = textToSearch.toLowerCase();
         if(testField(getUrl().toString(), searchedTextLowerCase)) {
             return true;
         }
         Arguments arguments = getArguments();
         if(arguments != null) {
             for (int i = 0; i < arguments.getArgumentCount(); i++) {
                 Argument argument = arguments.getArgument(i);
                 if(testField(argument.getName(), searchedTextLowerCase)) {
                     return true;
                 }
                 if(testField(argument.getValue(), searchedTextLowerCase)) {
                     return true;
                 }
             }
         }
-        if(testField(getComment(), searchedTextLowerCase)) {
-            return true;
-        }
         return false;
     }
  }
 
