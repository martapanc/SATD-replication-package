diff --git a/src/components/org/apache/jmeter/config/CSVDataSet.java b/src/components/org/apache/jmeter/config/CSVDataSet.java
index 4b72b12f4..96e745bfe 100644
--- a/src/components/org/apache/jmeter/config/CSVDataSet.java
+++ b/src/components/org/apache/jmeter/config/CSVDataSet.java
@@ -1,332 +1,332 @@
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
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.io.IOException;
 import java.util.ResourceBundle;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.NoConfigMerge;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
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
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CSVDataSet.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
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
 
     private boolean ignoreFirstLine = false;
 
     private Object readResolve(){
         recycle = true;
         return this;
     }
 
     /**
      * Override the setProperty method in order to convert
      * the original String shareMode property.
      * This used the locale-dependent display value, so caused
      * problems when the language was changed. 
      * If the "shareMode" value matches a resource value then it is converted
      * into the resource key.
      * To reduce the need to look up resources, we only attempt to
      * convert values with spaces in them, as these are almost certainly
      * not variables (and they are definitely not resource keys).
      */
     @Override
     public void setProperty(JMeterProperty property) {
         if (property instanceof StringProperty) {
             final String propName = property.getName();
             if ("shareMode".equals(propName)) { // The original name of the property
                 final String propValue = property.getStringValue();
                 if (propValue.contains(" ")){ // variables are unlikely to contain spaces, so most likely a translation
                     try {
                         final BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
                         final ResourceBundle rb = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE);
                         for(String resKey : CSVDataSetBeanInfo.getShareTags()) {
                             if (propValue.equals(rb.getString(resKey))) {
                                 if (log.isDebugEnabled()) {
-                                    log.debug("Converted " + propName + "=" + propValue + " to " + resKey  + " using Locale: " + rb.getLocale());
+                                    log.debug("Converted {}={} to {} using Locale: {}", propName, propValue, resKey, rb.getLocale());
                                 }
                                 ((StringProperty) property).setValue(resKey); // reset the value
                                 super.setProperty(property);
                                 return;                                        
                             }
                         }
                         // This could perhaps be a variable name
-                        log.warn("Could not translate " + propName + "=" + propValue + " using Locale: " + rb.getLocale());
+                        log.warn("Could not translate {}={} using Locale: {}", propName, propValue, rb.getLocale());
                     } catch (IntrospectionException e) {
                         log.error("Could not find BeanInfo; cannot translate shareMode entries", e);
                     }
                 }
             }
         }
         super.setProperty(property);        
     }
 
     @Override
     public void iterationStart(LoopIterationEvent iterEvent) {
         FileServer server = FileServer.getFileServer();
         final JMeterContext context = getThreadContext();
         String delim = getDelimiter();
         if ("\\t".equals(delim)) { // $NON-NLS-1$
             delim = "\t";// Make it easier to enter a Tab // $NON-NLS-1$
         } else if (delim.isEmpty()){
             log.warn("Empty delimiter converted to ','");
             delim=",";
         }
         if (vars == null) {
             String fileName = getFilename().trim();
             String mode = getShareMode();
             int modeInt = CSVDataSetBeanInfo.getShareModeAsInt(mode);
             switch(modeInt){
                 case CSVDataSetBeanInfo.SHARE_ALL:
                     alias = fileName;
                     break;
                 case CSVDataSetBeanInfo.SHARE_GROUP:
                     alias = fileName+"@"+System.identityHashCode(context.getThreadGroup());
                     break;
                 case CSVDataSetBeanInfo.SHARE_THREAD:
                     alias = fileName+"@"+System.identityHashCode(context.getThread());
                     break;
                 default:
                     alias = fileName+"@"+mode; // user-specified key
                     break;
             }
             final String names = getVariableNames();
             if (StringUtils.isEmpty(names)) {
                 String header = server.reserveFile(fileName, getFileEncoding(), alias, true);
                 try {
                     vars = CSVSaveService.csvSplitString(header, delim.charAt(0));
                     firstLineIsNames = true;
                 } catch (IOException e) {
                     throw new IllegalArgumentException("Could not split CSV header line from file:" + fileName,e);
                 }
             } else {
                 server.reserveFile(fileName, getFileEncoding(), alias, ignoreFirstLine);
                 vars = JOrphanUtils.split(names, ","); // $NON-NLS-1$
             }
             trimVarNames(vars);
         }
            
         // TODO: fetch this once as per vars above?
         JMeterVariables threadVars = context.getVariables();
         String[] lineValues = {};
         try {
             if (getQuotedData()) {
                 lineValues = server.getParsedLine(alias, recycle, 
                         firstLineIsNames || ignoreFirstLine, delim.charAt(0));
             } else {
                 String line = server.readLine(alias, recycle, 
                         firstLineIsNames || ignoreFirstLine);
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
                 throw new JMeterStopThreadException("End of file:"+ getFilename()+" detected for CSV DataSet:"
                         +getName()+" configured with stopThread:"+ getStopThread()+", recycle:" + getRecycle());
             }
             for (String var :vars) {
                 threadVars.put(var, EOFVALUE);
             }
         }
     }
 
     /**
      * trim content of array varNames
      * @param varsNames
      */
     private void trimVarNames(String[] varsNames) {
         for (int i = 0; i < varsNames.length; i++) {
             varsNames[i] = varsNames[i].trim();
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
 
     /**
      * @return the ignoreFirstLine
      */
     public boolean isIgnoreFirstLine() {
         return ignoreFirstLine;
     }
 
     /**
      * @param ignoreFirstLine the ignoreFirstLine to set
      */
     public void setIgnoreFirstLine(boolean ignoreFirstLine) {
         this.ignoreFirstLine = ignoreFirstLine;
     }
 }
diff --git a/src/components/org/apache/jmeter/config/KeystoreConfig.java b/src/components/org/apache/jmeter/config/KeystoreConfig.java
index d064e5537..e3cd99000 100644
--- a/src/components/org/apache/jmeter/config/KeystoreConfig.java
+++ b/src/components/org/apache/jmeter/config/KeystoreConfig.java
@@ -1,158 +1,160 @@
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
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Configure Keystore
  */
 public class KeystoreConfig extends ConfigTestElement implements TestBean, TestStateListener {
 
-    private static final long serialVersionUID = -5781402012242794890L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final long serialVersionUID = 1L;
+    private static final Logger log = LoggerFactory.getLogger(KeystoreConfig.class);
 
     private static final String KEY_STORE_START_INDEX = "https.keyStoreStartIndex"; // $NON-NLS-1$
     private static final String KEY_STORE_END_INDEX   = "https.keyStoreEndIndex"; // $NON-NLS-1$
 
     private String startIndex;
     private String endIndex;
     private String preload;
     private String clientCertAliasVarName;
     
     public KeystoreConfig() {
         super();
     }
 
     @Override
     public void testEnded() {
         testEnded(null);
     }
 
     @Override
     public void testEnded(String host) {
         log.info("Destroying Keystore");         
         SSLManager.getInstance().destroyKeystore();
     }
 
     @Override
     public void testStarted() {
         testStarted(null);
     }
 
     @Override
     public void testStarted(String host) {
         String reuseSSLContext = JMeterUtils.getProperty("https.use.cached.ssl.context");
         if(StringUtils.isEmpty(reuseSSLContext)||"true".equals(reuseSSLContext)) {
             log.warn("https.use.cached.ssl.context property must be set to false to ensure Multiple Certificates are used");
         }
         int startIndexAsInt = JMeterUtils.getPropDefault(KEY_STORE_START_INDEX, 0);
         int endIndexAsInt = JMeterUtils.getPropDefault(KEY_STORE_END_INDEX, 0);
         
         if(!StringUtils.isEmpty(this.startIndex)) {
             try {
                 startIndexAsInt = Integer.parseInt(this.startIndex);
             } catch(NumberFormatException e) {
-                log.warn("Failed parsing startIndex :'"+this.startIndex+"', will default to:'"+startIndexAsInt+"', error message:"+ e.getMessage(), e);
+                log.warn("Failed parsing startIndex: {}, will default to: {}, error message: {}", this.startIndex,
+                        startIndexAsInt, e, e);
             }
         } 
         
         if(!StringUtils.isEmpty(this.endIndex)) {
             try {
                 endIndexAsInt = Integer.parseInt(this.endIndex);
             } catch(NumberFormatException e) {
-                log.warn("Failed parsing endIndex :'"+this.endIndex+"', will default to:'"+endIndexAsInt+"', error message:"+ e.getMessage(), e);
+                log.warn("Failed parsing endIndex: {}, will default to: {}, error message: {}", this.endIndex,
+                        endIndexAsInt, e, e);
             }
         } 
         if(startIndexAsInt>endIndexAsInt) {
             throw new JMeterStopTestException("Keystore Config error : Alias start index must be lower than Alias end index");
         }
-        log.info("Configuring Keystore with (preload:"+preload+", startIndex:"+
-                startIndexAsInt+", endIndex:"+endIndexAsInt+
-                ", clientCertAliasVarName:'" + clientCertAliasVarName +"')");
+        log.info(
+                "Configuring Keystore with (preload: '{}', startIndex: {}, endIndex: {}, clientCertAliasVarName: '{}')",
+                preload, startIndexAsInt, endIndexAsInt, clientCertAliasVarName);
 
         SSLManager.getInstance().configureKeystore(Boolean.parseBoolean(preload),
                 startIndexAsInt, 
                 endIndexAsInt,
                 clientCertAliasVarName);
     }
 
     /**
      * @return the endIndex
      */
     public String getEndIndex() {
         return endIndex;
     }
 
     /**
      * @param endIndex the endIndex to set
      */
     public void setEndIndex(String endIndex) {
         this.endIndex = endIndex;
     }
 
     /**
      * @return the startIndex
      */
     public String getStartIndex() {
         return startIndex;
     }
 
     /**
      * @param startIndex the startIndex to set
      */
     public void setStartIndex(String startIndex) {
         this.startIndex = startIndex;
     }
 
     /**
      * @return the preload
      */
     public String getPreload() {
         return preload;
     }
 
     /**
      * @param preload the preload to set
      */
     public void setPreload(String preload) {
         this.preload = preload;
     }
 
     /**
      * @return the clientCertAliasVarName
      */
     public String getClientCertAliasVarName() {
         return clientCertAliasVarName;
     }
 
     /**
      * @param clientCertAliasVarName the clientCertAliasVarName to set
      */
     public void setClientCertAliasVarName(String clientCertAliasVarName) {
         this.clientCertAliasVarName = clientCertAliasVarName;
     }
 }
diff --git a/src/components/org/apache/jmeter/config/RandomVariableConfig.java b/src/components/org/apache/jmeter/config/RandomVariableConfig.java
index e2a469e7c..9573a8e2b 100644
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class RandomVariableConfig extends ConfigTestElement
     implements TestBean, LoopIterationListener, NoThreadClone, NoConfigMerge
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RandomVariableConfig.class);
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
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
-            log.error("maximum("+maxAsString+") must be > minimum"+minAsString+")");
+            log.error("maximum({}) must be > minimum({})", maxAsString, minAsString);
             range=0;// This is used as an error indicator
             return;
         }
         if (rangeL > Integer.MAX_VALUE || rangeL <= 0){// check for overflow too
-            log.warn("maximum("+maxAsString+") - minimum"+minAsString+") must be <="+Integer.MAX_VALUE);
+            log.warn("maximum({}) - minimum({}) must be <= {}", maxAsString, minAsString, Integer.MAX_VALUE);
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
-                log.warn("Exception formatting value:"+value + " at format:"+format+", using default");
+                log.warn("Exception formatting value: {} at format: {}, using default", value, format);
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
-                log.warn("Cannot parse seed "+e.getLocalizedMessage());
+                log.warn("Cannot parse seed: {}. {}", randomSeed, e.getLocalizedMessage());
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
index fb1a554d8..8aa62bbef 100644
--- a/src/components/org/apache/jmeter/control/CriticalSectionController.java
+++ b/src/components/org/apache/jmeter/control/CriticalSectionController.java
@@ -1,205 +1,198 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
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
 
-    /**
-     * 
-     */
-    private static final long serialVersionUID = 4362876132435968088L;
+    private static final long serialVersionUID = 1L;
 
-    private static final Logger logger = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CriticalSectionController.class);
 
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
-            logger.warn("Empty lock name in Critical Section Controller:"
-                    + getName());
+            log.warn("Empty lock name in Critical Section Controller: {}", getName());
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
-            if (logger.isDebugEnabled()) {
-                logger.debug(Thread.currentThread().getName()
-                        + " acquired lock:'" + getLockName()
-                        + "' in Critical Section Controller " + getName()
-                        + " in:" + (endTime - startTime) + " ms");
+            if (log.isDebugEnabled()) {
+                log.debug("Thread ('{}') acquired lock: '{}' in Critical Section Controller {}  in: {} ms",
+                        Thread.currentThread(), getLockName(), getName(), endTime - startTime);
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
-            logger.warn("Lock " + getLockName() + " not released in:"
-                    + getName() + ", releasing in threadFinished");
+            log.warn("Lock '{}' not released in: {}, releasing in threadFinished", getLockName(), getName());
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
index 1eea17a77..ee7e37723 100644
--- a/src/components/org/apache/jmeter/control/IncludeController.java
+++ b/src/components/org/apache/jmeter/control/IncludeController.java
@@ -1,206 +1,205 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class IncludeController extends GenericController implements ReplaceableController {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(IncludeController.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
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
-                log.info("loadIncludedElements -- try to load included module: "+absolutePath);
+                log.info("loadIncludedElements -- try to load included module: {}", absolutePath);
                 if(!file.exists() && !file.isAbsolute()){
-                    log.info("loadIncludedElements -failed for: "+absolutePath);
+                    log.info("loadIncludedElements -failed for: {}", absolutePath);
                     file = new File(FileServer.getFileServer().getBaseDir(), includePath);
-                    log.info("loadIncludedElements -Attempting to read it from: " + file.getAbsolutePath());
+                    log.info("loadIncludedElements -Attempting to read it from: {}", file.getAbsolutePath());
                     if(!file.canRead() || !file.isFile()){
-                        log.error("Include Controller \""
-                                + this.getName()+"\" can't load \"" 
-                                + fileName+"\" - see log for details");
+                        log.error("Include Controller '{}' can't load '{}' - see log for details", this.getName(),
+                                fileName);
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
index 1378f0c0c..a4d7400dd 100644
--- a/src/components/org/apache/jmeter/control/ThroughputController.java
+++ b/src/components/org/apache/jmeter/control/ThroughputController.java
@@ -1,284 +1,284 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
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
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ThroughputController.class);
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
             try {
                 retVal = Integer.parseInt(prop.getStringValue());
             } catch (NumberFormatException e) {
-                log.warn("Error parsing "+prop.getStringValue(),e);
+                log.warn("Error parsing {}", prop.getStringValue(), e);
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
             try {
                 retVal = Float.parseFloat(prop.getStringValue());
             } catch (NumberFormatException e) {
-                log.warn("Error parsing "+prop.getStringValue(),e);
+                log.warn("Error parsing {}", prop.getStringValue(),e);
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
index d782d36fe..e9efda78c 100644
--- a/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/BSFPostProcessor.java
@@ -1,54 +1,54 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BSFPostProcessor extends BSFTestElement implements Cloneable, PostProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFPostProcessor.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     @Override
     public void process(){
         BSFManager mgr =null;
         try {
             mgr = getManager();
             processFileOrScript(mgr);
         } catch (BSFException e) {
-            log.warn("Problem in BSF script "+e);
+            log.warn("Problem in BSF script {}", e.toString());
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
index 0dd7b1e87..d2e78774e 100644
--- a/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/BeanShellPostProcessor.java
@@ -1,74 +1,74 @@
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class BeanShellPostProcessor extends BeanShellTestElement
     implements Cloneable, PostProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellPostProcessor.class);
 
-    private static final long serialVersionUID = 4;
+    private static final long serialVersionUID = 5;
     
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
-            log.warn("Problem in BeanShell script "+e);
+            log.warn("Problem in BeanShell script: {}", e.toString());
         }
     }
      
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
index c4b102642..36466fd36 100644
--- a/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/HtmlExtractor.java
@@ -1,332 +1,332 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * 
  */
 public class HtmlExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
-    private static final long serialVersionUID = 3978073849365558131L;
+    private static final long serialVersionUID = 1L;
 
     public static final String EXTRACTOR_JSOUP = "JSOUP"; //$NON-NLS-1$
 
     public static final String EXTRACTOR_JODD = "JODD"; //$NON-NLS-1$
 
     public static final String DEFAULT_EXTRACTOR = ""; // $NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HtmlExtractor.class);
 
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
-            log.debug("HtmlExtractor "+getName()+":processing result");
+            log.debug("HtmlExtractor {}: processing result", getName());
         }
-
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
-                } catch (NumberFormatException e1) {
-                    log.warn(getName()+":Could not parse "+prevString+" "+e1);
+                } catch (NumberFormatException nfe) {
+                    log.warn("{}: Could not parse number '{}'.", getName(), prevString);
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
-            log.warn(getName()+":Error while generating result " + e);
+            log.warn("{}: Error while generating result. {}", getName(), e.toString());
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
-                    log.warn("No variable '"+getVariableName()+"' found to process by Css/JQuery Extractor '"+getName()+"', skipping processing");
+                    log.warn("No variable '{}' found to process by CSS/JQuery Extractor '{}', skipping processing",
+                            getVariableName(), getName());
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
diff --git a/src/components/org/apache/jmeter/extractor/JSR223PostProcessor.java b/src/components/org/apache/jmeter/extractor/JSR223PostProcessor.java
index 0d01eca44..c50d1c922 100644
--- a/src/components/org/apache/jmeter/extractor/JSR223PostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/JSR223PostProcessor.java
@@ -1,52 +1,52 @@
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
 
 import java.io.IOException;
 
 import javax.script.ScriptEngine;
 import javax.script.ScriptException;
 
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class JSR223PostProcessor extends JSR223TestElement implements Cloneable, PostProcessor, TestBean
 {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223PostProcessor.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     @Override
     public void process() {
         try {
             ScriptEngine scriptEngine = getScriptEngine();
             processFileOrScript(scriptEngine, null);
         } catch (ScriptException | IOException e) {
-            log.error("Problem in JSR223 script "+getName(), e);
+            log.error("Problem in JSR223 script, {}", getName(), e);
         }
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/RegexExtractor.java b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
index 74ef8ce92..876641d47 100644
--- a/src/components/org/apache/jmeter/extractor/RegexExtractor.java
+++ b/src/components/org/apache/jmeter/extractor/RegexExtractor.java
@@ -1,525 +1,520 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcher;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 // @see org.apache.jmeter.extractor.TestRegexExtractor for unit tests
 
 public class RegexExtractor extends AbstractScopedTestElement implements PostProcessor, Serializable {
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RegexExtractor.class);
 
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
-                } catch (NumberFormatException e1) {
-                    log.warn("Could not parse "+prevString+" "+e1);
+                } catch (NumberFormatException nfe) {
+                    log.warn("Could not parse number: '{}', message: '{}'", prevString, nfe.toString());
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
-            log.error("Error in pattern: " + regex);
+            log.error("Error in pattern: '{}'", regex);
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
-       if (log.isDebugEnabled()) {
-           log.debug("Input = " + inputString);
-       }
+       log.debug("Input = '{}'", inputString);
        return inputString;
     }
 
     private List<MatchResult> processMatches(Pattern pattern, String regex, SampleResult result, int matchNumber, JMeterVariables vars) {
-        if (log.isDebugEnabled()) {
-            log.debug("Regex = " + regex);
-        }
+        log.debug("Regex = '{}'", regex);
 
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         List<MatchResult> matches = new ArrayList<>();
         int found = 0;
 
         if (isScopeVariable()){
             String inputString=vars.get(getVariableName());
             if(inputString == null) {
-                log.warn("No variable '"+getVariableName()+"' found to process by RegexExtractor '"+getName()+"', skipping processing");
+                log.warn("No variable '{}' found to process by RegexExtractor '{}', skipping processing",
+                        getVariableName(), getName());
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
-            } catch (NumberFormatException e) {
-                log.warn("Could not parse "+prevString+" "+e);
+            } catch (NumberFormatException nfe) {
+                log.warn("Could not parse number: '{}', message:'{}'", prevString, nfe.toString());
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
-            if (log.isDebugEnabled()) {
-                log.debug("RegexExtractor: Template piece " + obj + " (" + obj.getClass().getSimpleName() + ")");
+            if(log.isDebugEnabled()) {
+                log.debug("RegexExtractor: Template piece {} ({})", obj, obj.getClass());
             }
             if (obj instanceof Integer) {
                 result.append(match.group(((Integer) obj).intValue()));
             } else {
                 result.append(obj);
             }
         }
-        if (log.isDebugEnabled()) {
-            log.debug("Regex Extractor result = " + result.toString());
-        }
+        log.debug("Regex Extractor result = '{}'", result);
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
-            log.debug("Pattern = " + templatePattern.getPattern());
-            log.debug("template = " + rawTemplate);
+            log.debug("Pattern = '{}', template = '{}'", templatePattern.getPattern(), rawTemplate);
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
-        if (log.isDebugEnabled()){
-            log.debug("Template item count: "+combined.size());
-            for(Object o : combined){
-                log.debug(o.getClass().getSimpleName()+" '"+o.toString()+"'");
+        if (log.isDebugEnabled()) {
+            log.debug("Template item count: {}", combined.size());
+            int i = 0;
+            for (Object o : combined) {
+                log.debug("Template item-{}: {} '{}'", i++, o.getClass(), o);
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
index 633ff4f41..9fcbc1839 100644
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
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
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathExtractor.class);
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
     
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
-                    log.warn("No variable '"+getVariableName()+"' found to process by XPathExtractor '"+getName()+"', skipping processing");
+                    log.warn("No variable '{}' found to process by XPathExtractor '{}', skipping processing",
+                            getVariableName(), getName());
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
-            final String errorMessage = "IOException on ("+getXPathQuery()+")";
-            log.error(errorMessage,e);
+            log.error("IOException on ({})", getXPathQuery(), e);
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
-            log.warn("SAXException while processing ("+getXPathQuery()+") "+e.getLocalizedMessage());
+            log.warn("SAXException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
             addAssertionFailure(previousResult, e, false); // Should this also fail the sample?
         } catch (TransformerException e) {// Can happen for incorrect XPath expression
-            log.warn("TransformerException while processing ("+getXPathQuery()+") "+e.getLocalizedMessage());
+            log.warn("TransformerException while processing ({}). {}", getXPathQuery(), e.getLocalizedMessage());
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
diff --git a/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONManager.java b/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONManager.java
index bdfdfa9ef..847d994d5 100644
--- a/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONManager.java
+++ b/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONManager.java
@@ -1,109 +1,108 @@
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
 
 package org.apache.jmeter.extractor.json.jsonpath;
 
 import java.text.ParseException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-
-import net.minidev.json.JSONArray;
-import net.minidev.json.JSONObject;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.jayway.jsonpath.Configuration;
 import com.jayway.jsonpath.JsonPath;
 import com.jayway.jsonpath.Option;
 import com.jayway.jsonpath.PathNotFoundException;
 
+import net.minidev.json.JSONArray;
+import net.minidev.json.JSONObject;
+
 /**
  * Handles the extractions
  * https://github.com/jayway/JsonPath/blob/master/json-path/src/test/java/com/jayway/jsonpath/ComplianceTest.java
  * @since 3.0
  */
 public class JSONManager {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSONManager.class);
     private static final Configuration DEFAULT_CONFIGURATION =
             Configuration.defaultConfiguration().addOptions(Option.ALWAYS_RETURN_LIST);
     /**
      * This Map can hardly grow above 10 elements as it is used within JSONPostProcessor to 
      * store the computed JsonPath for the set of JSON Path Expressions.
      * Usually there will be 1 to Maximum 10 elements
      */
     private Map<String, JsonPath> expressionToJsonPath = new HashMap<>(2);
 
     private JsonPath getJsonPath(String jsonPathExpression) {
         JsonPath jsonPath = expressionToJsonPath.get(jsonPathExpression);
         if (jsonPath == null) {
             jsonPath = JsonPath.compile(jsonPathExpression);
             expressionToJsonPath.put(jsonPathExpression, jsonPath);
         }
 
         return jsonPath;
     }
     
     public void reset() {
         expressionToJsonPath.clear();
     }
 
     /**
      * 
      * @param jsonString JSON String from which data is extracted
      * @param jsonPath JSON-PATH expression
      * @return List of JSON Strings of the extracted data
      * @throws ParseException when parsing fails
      */
     public List<Object> extractWithJsonPath(String jsonString, String jsonPath)
             throws ParseException {
         JsonPath jsonPathParser = getJsonPath(jsonPath);
         List<Object> extractedObjects;
         try {
             extractedObjects = jsonPathParser.read(jsonString,
                     DEFAULT_CONFIGURATION);
         } catch (PathNotFoundException e) {
-            if (log.isDebugEnabled()) {
-                log.debug("Could not find JSON Path " + jsonPath + " in ["
-                        + jsonString + "]: " + e.getLocalizedMessage());
+            if(log.isDebugEnabled()) {
+                log.debug("Could not find JSON Path {} in [{}]: {}", jsonPath, jsonString, e.getLocalizedMessage());
             }
             return Collections.emptyList();
         }
         List<Object> results = new ArrayList<>(extractedObjects.size());
         for (Object obj: extractedObjects) {
             results.add(stringifyJSONObject(obj));
         }
         return results;
     }
 
     @SuppressWarnings("unchecked")
     private String stringifyJSONObject(Object obj) {
         if (obj instanceof Map) {
             return new JSONObject((Map<String, ?>) obj).toJSONString();
         }
         if (obj instanceof JSONArray) {
             return ((JSONArray)obj).toJSONString();
         }
         return obj == null ? "" : obj.toString(); //$NON-NLS-1$
     }
 
 }
diff --git a/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONPostProcessor.java b/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONPostProcessor.java
index 2bff0e410..b33ab8513 100644
--- a/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONPostProcessor.java
+++ b/src/components/org/apache/jmeter/extractor/json/jsonpath/JSONPostProcessor.java
@@ -1,271 +1,268 @@
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
 
 package org.apache.jmeter.extractor.json.jsonpath;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.List;
 
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * JSON-PATH based extractor
  * @since 3.0
  */
 public class JSONPostProcessor extends AbstractScopedTestElement implements Serializable, PostProcessor, ThreadListener{
 
-    private static final long serialVersionUID = 1320798545214331506L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final long serialVersionUID = 1L;
+    private static final Logger log = LoggerFactory.getLogger(JSONPostProcessor.class);
 
     private static final String JSON_PATH_EXPRESSIONS = "JSONPostProcessor.jsonPathExprs"; // $NON-NLS-1$
     private static final String REFERENCE_NAMES = "JSONPostProcessor.referenceNames"; // $NON-NLS-1$
     private static final String DEFAULT_VALUES = "JSONPostProcessor.defaultValues"; // $NON-NLS-1$
     private static final String MATCH_NUMBERS = "JSONPostProcessor.match_numbers"; // $NON-NLS-1$
     private static final String COMPUTE_CONCATENATION = "JSONPostProcessor.compute_concat"; // $NON-NLS-1$
     private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
     private static final String ALL_SUFFIX = "_ALL"; // $NON-NLS-1$
 
     private static final String JSON_CONCATENATION_SEPARATOR = ","; //$NON-NLS-1$
     private static final String SEPARATOR = ";"; // $NON-NLS-1$
     public static final boolean COMPUTE_CONCATENATION_DEFAULT_VALUE = false;
     
     private static final ThreadLocal<JSONManager> localMatcher = new ThreadLocal<JSONManager>() {
         @Override
         protected JSONManager initialValue() {
             return new JSONManager();
         }
     };
 
     @Override
     public void process() {
         JMeterContext context = getThreadContext();
         JMeterVariables vars = context.getVariables();
         String jsonResponse;
         if (isScopeVariable()) {
             jsonResponse = vars.get(getVariableName());
             if (log.isDebugEnabled()) {
-                log.debug("JSON Extractor is using variable:" + getVariableName() + " which content is:" + jsonResponse);
+                log.debug("JSON Extractor is using variable: {}, which content is: {}", getVariableName(), jsonResponse);
             }
         } else {
             SampleResult previousResult = context.getPreviousResult();
             if (previousResult == null) {
                 return;
             }
             jsonResponse = previousResult.getResponseDataAsString();
             if (log.isDebugEnabled()) {
-                log.debug("JSON Extractor " + getName() + " working on Response:" + jsonResponse);
+                log.debug("JSON Extractor {} working on Response: {}", getName(), jsonResponse);
             }
         }
         String[] refNames = getRefNames().split(SEPARATOR);
         String[] jsonPathExpressions = getJsonPathExpressions().split(SEPARATOR);
         String[] defaultValues = getDefaultValues().split(SEPARATOR);
         int[] matchNumbers = getMatchNumbersAsInt(defaultValues.length);
 
         //jsonResponse = jsonResponse.replaceAll("'", "\""); // $NON-NLS-1$  $NON-NLS-2$
 
         if (refNames.length != jsonPathExpressions.length ||
                 refNames.length != defaultValues.length) {
             log.error("Number of JSON Path variables must match number of default values and json-path expressions, check you use separator ';' if you have many values"); // $NON-NLS-1$
             throw new IllegalArgumentException(JMeterUtils
                     .getResString("jsonpp_error_number_arguments_mismatch_error")); // $NON-NLS-1$
         }
 
         for (int i = 0; i < jsonPathExpressions.length; i++) {
             int matchNumber = matchNumbers[i];
             String currentRefName = refNames[i].trim();
             String currentJsonPath = jsonPathExpressions[i].trim();
             clearOldRefVars(vars, currentRefName);
             try {
                 if (jsonResponse.isEmpty()) {
                     vars.put(currentRefName, defaultValues[i]);
                 } else {
 
                     List<Object> extractedValues = localMatcher.get()
                             .extractWithJsonPath(jsonResponse, currentJsonPath);
                     // if no values extracted, default value added
                     if (extractedValues.isEmpty()) {
                         vars.put(currentRefName, defaultValues[i]);
                         vars.put(currentRefName + REF_MATCH_NR, "0"); //$NON-NLS-1$
                         if (matchNumber < 0 && getComputeConcatenation()) {
-                            log.debug("No value extracted, storing empty in:" //$NON-NLS-1$
-                                    + currentRefName + ALL_SUFFIX);
+                            log.debug("No value extracted, storing empty in: {}{}", currentRefName, ALL_SUFFIX);
                             vars.put(currentRefName + ALL_SUFFIX, "");
                         }
                     } else {
                         // if more than one value extracted, suffix with "_index"
                         if (extractedValues.size() > 1) {
                             if (matchNumber < 0) {
                                 // Extract all
                                 int index = 1;
                                 StringBuilder concat =
                                         new StringBuilder(getComputeConcatenation()
                                                 ? extractedValues.size() * 20
                                                 : 1);
                                 for (Object extractedObject : extractedValues) {
                                     String extractedString = stringify(extractedObject);
                                     vars.put(currentRefName + "_" + index,
                                             extractedString); //$NON-NLS-1$
                                     if (getComputeConcatenation()) {
                                         concat.append(extractedString)
                                                 .append(JSONPostProcessor.JSON_CONCATENATION_SEPARATOR);
                                     }
                                     index++;
                                 }
                                 if (getComputeConcatenation()) {
                                     concat.setLength(concat.length() - 1);
                                     vars.put(currentRefName + ALL_SUFFIX, concat.toString());
                                 }
                             } else if (matchNumber == 0) {
                                 // Random extraction
                                 int matchSize = extractedValues.size();
                                 int matchNr = JMeterUtils.getRandomInt(matchSize);
                                 placeObjectIntoVars(vars, currentRefName,
                                         extractedValues, matchNr);
                             } else {
                                 // extract at position
                                 if (matchNumber > extractedValues.size()) {
-                                    if (log.isDebugEnabled()) {
-                                        log.debug("matchNumber(" + matchNumber
-                                                + ") exceeds number of items found("
-                                                + extractedValues.size()
-                                                + "), default value will be used");
+                                    if(log.isDebugEnabled()) {
+                                        log.debug(
+                                            "matchNumber({}) exceeds number of items found({}), default value will be used",
+                                            matchNumber, extractedValues.size());
                                     }
                                     vars.put(currentRefName, defaultValues[i]);
                                 } else {
                                     placeObjectIntoVars(vars, currentRefName, extractedValues, matchNumber - 1);
                                 }
                             }
                         } else {
                             // else just one value extracted
                             String suffix = (matchNumber < 0) ? "_1" : "";
                             placeObjectIntoVars(vars, currentRefName + suffix, extractedValues, 0);
                             if (matchNumber < 0 && getComputeConcatenation()) {
                                 vars.put(currentRefName + ALL_SUFFIX, vars.get(currentRefName));
                             }
                         }
                         if (matchNumber != 0) {
                             vars.put(currentRefName + REF_MATCH_NR, Integer.toString(extractedValues.size()));
                         }
                     }
                 }
             } catch (Exception e) {
                 // if something wrong, default value added
                 if (log.isDebugEnabled()) {
-                    log.error("Error processing JSON content in "+ getName()+", message:"+e.getLocalizedMessage(),e);
+                    log.error("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage(), e);
                 } else {
-                    log.error("Error processing JSON content in "+ getName()+", message:"+e.getLocalizedMessage());
-                    
+                    log.error("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage());
                 }
                 vars.put(currentRefName, defaultValues[i]);
             }
         }
     }
 
     private void clearOldRefVars(JMeterVariables vars, String refName) {
         vars.remove(refName + REF_MATCH_NR);
         for (int i=1; vars.get(refName + "_" + i) != null; i++) {
             vars.remove(refName + "_" + i);
         }
     }
 
     private void placeObjectIntoVars(JMeterVariables vars, String currentRefName,
             List<Object> extractedValues, int matchNr) {
         vars.put(currentRefName,
                 stringify(extractedValues.get(matchNr)));
     }
 
     private String stringify(Object obj) {
         return obj == null ? "" : obj.toString(); //$NON-NLS-1$
     }
 
     public String getJsonPathExpressions() {
         return getPropertyAsString(JSON_PATH_EXPRESSIONS);
     }
 
     public void setJsonPathExpressions(String jsonPath) {
         setProperty(JSON_PATH_EXPRESSIONS, jsonPath);
     }
 
     public String getRefNames() {
         return getPropertyAsString(REFERENCE_NAMES);
     }
 
     public void setRefNames(String refName) {
         setProperty(REFERENCE_NAMES, refName);
     }
 
     public String getDefaultValues() {
         return getPropertyAsString(DEFAULT_VALUES);
     }
 
     public void setDefaultValues(String defaultValue) {
         setProperty(DEFAULT_VALUES, defaultValue, ""); // $NON-NLS-1$
     }
 
     public boolean getComputeConcatenation() {
         return getPropertyAsBoolean(COMPUTE_CONCATENATION, COMPUTE_CONCATENATION_DEFAULT_VALUE);
     }
 
     public void setComputeConcatenation(boolean computeConcatenation) {
         setProperty(COMPUTE_CONCATENATION, computeConcatenation, COMPUTE_CONCATENATION_DEFAULT_VALUE); 
     }
     
     @Override
     public void threadStarted() {
         // NOOP
     }
 
     @Override
     public void threadFinished() {
         localMatcher.get().reset();
     }
 
     public void setMatchNumbers(String matchNumber) {
         setProperty(MATCH_NUMBERS, matchNumber);
     }
 
     public String getMatchNumbers() {
         return getPropertyAsString(MATCH_NUMBERS);
     }
 
     public int[] getMatchNumbersAsInt(int arraySize) {
         
         String matchNumbersAsString = getMatchNumbers();
         int[] result = new int[arraySize];
         if (JOrphanUtils.isBlank(matchNumbersAsString)) {
             Arrays.fill(result, 0);
         } else {
             String[] matchNumbersAsStringArray = 
                     matchNumbersAsString.split(SEPARATOR);
             for (int i = 0; i < matchNumbersAsStringArray.length; i++) {
                 result[i] = Integer.parseInt(matchNumbersAsStringArray[i].trim());
             }
         }
         return result;
     }
 }
diff --git a/src/components/org/apache/jmeter/extractor/json/render/RenderAsJsonRenderer.java b/src/components/org/apache/jmeter/extractor/json/render/RenderAsJsonRenderer.java
index 573d4f6a9..24b24b06d 100644
--- a/src/components/org/apache/jmeter/extractor/json/render/RenderAsJsonRenderer.java
+++ b/src/components/org/apache/jmeter/extractor/json/render/RenderAsJsonRenderer.java
@@ -1,267 +1,267 @@
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
 
 package org.apache.jmeter.extractor.json.render;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.text.ParseException;
 import java.util.List;
 
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextArea;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.extractor.json.jsonpath.JSONManager;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.RenderAsJSON;
 import org.apache.jmeter.visualizers.ResultRenderer;
 import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 
 /**
  * Implement ResultsRender for JSON Path tester
  * @since 3.0
  */
 public class RenderAsJsonRenderer implements ResultRenderer, ActionListener {
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsJsonRenderer.class);
 
     private static final String TAB_SEPARATOR = "    "; //$NON-NLS-1$
     
     private static final String JSONPATH_TESTER_COMMAND = "jsonpath_tester"; // $NON-NLS-1$
 
     private JPanel jsonWithJSonPathPanel;
 
     private JTextArea jsonDataField;
 
     private JLabeledTextField jsonPathExpressionField;
 
     private JTextArea jsonPathResultField;
 
     private JTabbedPane rightSide;
 
     private SampleResult sampleResult;
 
     private JScrollPane jsonDataPane;
 
 
     /** {@inheritDoc} */
     @Override
     public void clearData() {
         this.jsonDataField.setText(""); // $NON-NLS-1$
         // don't set empty to keep json path
         this.jsonPathResultField.setText(""); // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void init() {
         // Create the panels for the json tab
         jsonWithJSonPathPanel = createJSonPathExtractorPanel();
     }
 
     /**
      * Display the response as text or as rendered HTML. Change the text on the
      * button appropriate to the current display.
      *
      * @param e the ActionEvent being processed
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();
         if ((sampleResult != null) && (JSONPATH_TESTER_COMMAND.equals(command))) {
             String response = jsonDataField.getText();
             executeAndShowXPathTester(response);
         }
     }
 
     /**
      * Launch json path engine to parse a input text
      * @param textToParse
      */
     private void executeAndShowXPathTester(String textToParse) {
         if (textToParse != null && textToParse.length() > 0
                 && this.jsonPathExpressionField.getText().length() > 0) {
             this.jsonPathResultField.setText(process(textToParse));
             this.jsonPathResultField.setCaretPosition(0); // go to first line
         }
     }
 
     private String process(String textToParse) {
         try {
             List<Object> matchStrings = extractWithJSonPath(textToParse, jsonPathExpressionField.getText());
             if (matchStrings.isEmpty()) {
                 return "NO MATCH"; //$NON-NLS-1$
             } else {
                 StringBuilder builder = new StringBuilder();
                 int i = 0;
                 for (Object obj : matchStrings) {
                     String objAsString =
                             obj != null ? obj.toString() : ""; //$NON-NLS-1$
                     builder.append("Result[").append(i++).append("]=").append(objAsString).append("\n"); //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                 }
 
                 return builder.toString();
             }
         } catch (Exception e) {
             return "Exception: " + e.getMessage(); //$NON-NLS-1$
         }
     }
     
     private List<Object> extractWithJSonPath(String textToParse, String expression) throws ParseException {
         JSONManager jsonManager = new JSONManager();
         return jsonManager.extractWithJsonPath(textToParse, expression);
     }
 
     /*================= internal business =================*/
 
     /** {@inheritDoc} */
     @Override
     public void renderResult(SampleResult sampleResult) {
         String response = ViewResultsFullVisualizer.getResponseAsString(sampleResult);
         try {
             jsonDataField.setText(response == null ? "" : RenderAsJSON.prettyJSON(response, TAB_SEPARATOR));  //$NON-NLS-1$
             jsonDataField.setCaretPosition(0);
         } catch (Exception e) {
-            LOGGER.error("Exception converting to XML: "+response+ ", message: "+e.getMessage(),e); //$NON-NLS-1$ $NON-NLS-2$
+            log.error("Exception converting to XML: {}, message: {}", response, e.getMessage(), e); //$NON-NLS-1$ $NON-NLS-2$
             jsonDataField.setText("Exception converting to XML: "+response+ ", message: "+e.getMessage()); //$NON-NLS-1$ $NON-NLS-2$
             jsonDataField.setCaretPosition(0);
         }
     }
 
 
     /** {@inheritDoc} */
     @Override
     public String toString() {
         return JMeterUtils.getResString("jsonpath_renderer"); // $NON-NLS-1$
     }
 
 
     /** {@inheritDoc} */
     @Override
     public void setupTabPane() {
          // Add json-path tester pane
         if (rightSide.indexOfTab(JMeterUtils.getResString("jsonpath_tester_title")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("jsonpath_tester_title"), jsonWithJSonPathPanel); // $NON-NLS-1$
         }
         clearData();
     }
 
     /**
      * @return JSON PATH Tester panel
      */
     private JPanel createJSonPathExtractorPanel() {
         
         jsonDataField = new JTextArea();
         jsonDataField.setEditable(false);
         jsonDataField.setLineWrap(true);
         jsonDataField.setWrapStyleWord(true);
 
         this.jsonDataPane = GuiUtils.makeScrollPane(jsonDataField);
         jsonDataPane.setPreferredSize(new Dimension(100, 200));
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
 
         JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 jsonDataPane, createJSonPathExtractorTasksPanel());
         mainSplit.setDividerLocation(0.6d);
         mainSplit.setOneTouchExpandable(true);
         panel.add(mainSplit, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * Create the JSON PATH task pane
      *
      * @return JSON PATH task pane
      */
     private JPanel createJSonPathExtractorTasksPanel() {
         JPanel jsonPathActionPanel = new JPanel();
         jsonPathActionPanel.setLayout(new BoxLayout(jsonPathActionPanel, BoxLayout.X_AXIS));
         Border margin = new EmptyBorder(5, 5, 0, 5);
         jsonPathActionPanel.setBorder(margin);
         jsonPathExpressionField = new JLabeledTextField(JMeterUtils.getResString("jsonpath_tester_field")); // $NON-NLS-1$
         jsonPathActionPanel.add(jsonPathExpressionField, BorderLayout.WEST);
 
         JButton xpathTester = new JButton(JMeterUtils.getResString("jsonpath_tester_button_test")); // $NON-NLS-1$
         xpathTester.setActionCommand(JSONPATH_TESTER_COMMAND);
         xpathTester.addActionListener(this);
         jsonPathActionPanel.add(xpathTester, BorderLayout.EAST);
 
         jsonPathResultField = new JTextArea();
         jsonPathResultField.setEditable(false);
         jsonPathResultField.setLineWrap(true);
         jsonPathResultField.setWrapStyleWord(true);
         jsonPathResultField.setMinimumSize(new Dimension(100, 150));
 
         JPanel xpathTasksPanel = new JPanel(new BorderLayout(0, 5));
         xpathTasksPanel.add(jsonPathActionPanel, BorderLayout.NORTH);
         xpathTasksPanel.add(GuiUtils.makeScrollPane(jsonPathResultField), BorderLayout.CENTER);
 
         return xpathTasksPanel;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setRightSide(JTabbedPane side) {
         rightSide = side;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setSamplerResult(Object userObject) {
         if (userObject instanceof SampleResult) {
             sampleResult = (SampleResult) userObject;
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setLastSelectedTab(int index) {
         // nothing to do
     }
 
     /** {@inheritDoc} */
     @Override
     public void renderImage(SampleResult sampleResult) {
         clearData();
         jsonDataField.setText(JMeterUtils.getResString("jsonpath_render_no_text")); // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void setBackgroundColor(Color backGround) {
         // NOOP
     }
 
 }
