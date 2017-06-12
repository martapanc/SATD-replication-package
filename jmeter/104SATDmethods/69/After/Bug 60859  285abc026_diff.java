diff --git a/src/core/org/apache/jmeter/reporters/ResultSaver.java b/src/core/org/apache/jmeter/reporters/ResultSaver.java
index a4c17cbff..854d12f8e 100644
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
@@ -1,289 +1,300 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Serializable;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
+import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.AbstractTestElement;
+import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Save Result responseData to a set of files
  *
  *
  * This is mainly intended for validation tests
  *
  */
-public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener {
+public class ResultSaver extends AbstractTestElement implements NoThreadClone, Serializable, SampleListener, TestStateListener {
     private static final Logger log = LoggerFactory.getLogger(ResultSaver.class);
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
     private static final Object LOCK = new Object();
 
     private static final String TIMESTAMP_FORMAT = "yyyyMMdd-HHmm_"; // $NON-NLS-1$
 
-    // File name sequence number
-    //@GuardedBy("LOCK")
-    private static long sequenceNumber = 0;
-
-    //@GuardedBy("LOCK")
-    private static String timeStamp;
-
-    //@GuardedBy("LOCK")
-    private static int numberPadLength;
-
     //+ JMX property names; do not change
 
     public static final String FILENAME = "FileSaver.filename"; // $NON-NLS-1$
 
     public static final String VARIABLE_NAME = "FileSaver.variablename"; // $NON-NLS-1$
 
     public static final String ERRORS_ONLY = "FileSaver.errorsonly"; // $NON-NLS-1$
 
     public static final String SUCCESS_ONLY = "FileSaver.successonly"; // $NON-NLS-1$
 
     public static final String SKIP_AUTO_NUMBER = "FileSaver.skipautonumber"; // $NON-NLS-1$
 
     public static final String SKIP_SUFFIX = "FileSaver.skipsuffix"; // $NON-NLS-1$
 
     public static final String ADD_TIMESTAMP = "FileSaver.addTimstamp"; // $NON-NLS-1$
 
     public static final String NUMBER_PAD_LENGTH = "FileSaver.numberPadLen"; // $NON-NLS-1$
 
     //- JMX property names
 
+    // File name sequence number
+    //@GuardedBy("LOCK")
+    private long sequenceNumber = 0;
+
+    //@GuardedBy("LOCK")
+    private String timeStamp;
+
+    //@GuardedBy("LOCK")
+    private int numberPadLength;
+
     /**
      * Constructor is initially called once for each occurrence in the test plan
      * For GUI, several more instances are created Then clear is called at start
      * of test Called several times during test startup The name will not
      * necessarily have been set at this point.
      */
     public ResultSaver() {
         super();
     }
 
     /**
      * Constructor for use during startup (intended for non-GUI use) 
      * @param name of summariser
      */
     public ResultSaver(String name) {
         this();
         setName(name);
     }
 
     /**
      * @return next number accross all instances
      */
-    private static long nextNumber() {
+    private long nextNumber() {
         synchronized(LOCK) {
             return ++sequenceNumber;
         }
+    }    
+
+    @Override
+    public void testStarted() {
+        testStarted(""); //$NON-NLS-1$
     }
-    
-    /**
-     * This is called once for each occurrence in the test plan, before the
-     * start of the test. The super.clear() method clears the name (and all
-     * other properties), so it is called last.
-     */
+
     @Override
-    public void clear() {
+    public void testStarted(String host) {
         synchronized(LOCK){
             sequenceNumber = 0;
             if (getAddTimeStamp()) {
                 DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
                 timeStamp = format.format(new Date());
             } else {
                 timeStamp = "";
             }
             numberPadLength=getNumberPadLen();
         }
-        super.clear();
+    }
+
+    @Override
+    public void testEnded() {
+        testEnded(""); //$NON-NLS-1$
+    }
+
+    @Override
+    public void testEnded(String host) {
+        
     }
 
     /**
      * Saves the sample result (and any sub results) in files
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
       processSample(e.getResult(), new Counter());
    }
 
    /**
     * Recurse the whole (sub)result hierarchy.
     *
     * @param s Sample result
     * @param c sample counter
     */
    private void processSample(SampleResult s, Counter c) {
        saveSample(s, c.num++);
        SampleResult[] sampleResults = s.getSubResults();
        for (SampleResult sampleResult : sampleResults) {
            processSample(sampleResult, c);
        }
     }
 
     /**
      * @param s SampleResult to save
      * @param num number to append to variable (if >0)
      */
     private void saveSample(SampleResult s, int num) {
         // Should we save the sample?
         if (s.isSuccessful()){
             if (getErrorsOnly()){
                 return;
             }
         } else {
             if (getSuccessOnly()){
                 return;
             }
         }
 
         String fileName = makeFileName(s.getContentType(), getSkipAutoNumber(), getSkipSuffix());
         if (log.isDebugEnabled()) {
             log.debug("Saving {} in {}", s.getSampleLabel(), fileName);
         }
         s.setResultFileName(fileName);// Associate sample with file name
         String variable = getVariableName();
         if (variable.length()>0){
             if (num > 0) {
                 StringBuilder sb = new StringBuilder(variable);
                 sb.append(num);
                 variable=sb.toString();
             }
             JMeterContextService.getContext().getVariables().put(variable, fileName);
         }
         File out = new File(fileName);
         try (FileOutputStream fos = new FileOutputStream(out)){
             JOrphanUtils.write(s.getResponseData(), fos); // chunk the output if necessary
         } catch (FileNotFoundException e) {
             log.error("Error creating sample file for {}", s.getSampleLabel(), e);
         } catch (IOException e) {
             log.error("Error saving sample {}", s.getSampleLabel(), e);
         }
     }
 
     /**
      * @param contentType Content type
      * @param skipAutoNumber Skip auto number
      * @param skipSuffix Skip suffix
      * @return fileName composed of fixed prefix, a number, and a suffix derived
      *         from the contentType e.g. Content-Type:
      *         text/html;charset=ISO-8859-1
      */
-    private String makeFileName(String contentType, boolean skipAutoNumber, boolean skipSuffix) {
+    String makeFileName(String contentType, boolean skipAutoNumber, boolean skipSuffix) {
         StringBuilder sb = new StringBuilder(FileServer.resolveBaseRelativeName(getFilename()));
         sb.append(timeStamp); // may be the empty string
         if (!skipAutoNumber){
             String number = Long.toString(nextNumber());
             for(int i=number.length(); i < numberPadLength; i++) {
                 sb.append('0');
             }
             sb.append(number);
         }
         if (!skipSuffix){
             sb.append('.');
             if (contentType != null) {
                 int i = contentType.indexOf('/'); // $NON-NLS-1$
                 if (i != -1) {
                     int j = contentType.indexOf(';'); // $NON-NLS-1$
                     if (j != -1) {
                         sb.append(contentType.substring(i + 1, j));
                     } else {
                         sb.append(contentType.substring(i + 1));
                     }
                 } else {
                     sb.append("unknown");
                 }
             } else {
                 sb.append("unknown");
             }
         }
         return sb.toString();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStarted(SampleEvent e) {
         // not used
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void sampleStopped(SampleEvent e) {
         // not used
     }
 
     private String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     private String getVariableName() {
         return getPropertyAsString(VARIABLE_NAME,""); // $NON-NLS-1$
     }
 
     private boolean getErrorsOnly() {
         return getPropertyAsBoolean(ERRORS_ONLY);
     }
 
     private boolean getSkipAutoNumber() {
         return getPropertyAsBoolean(SKIP_AUTO_NUMBER);
     }
 
     private boolean getSkipSuffix() {
         return getPropertyAsBoolean(SKIP_SUFFIX);
     }
 
     private boolean getSuccessOnly() {
         return getPropertyAsBoolean(SUCCESS_ONLY);
     }
 
     private boolean getAddTimeStamp() {
         return getPropertyAsBoolean(ADD_TIMESTAMP);
     }
 
     private int getNumberPadLen() {
         return getPropertyAsInt(NUMBER_PAD_LENGTH, 0);
     }
 
     // Mutable int to keep track of sample count
     private static class Counter{
         int num;
     }
 }
diff --git a/test/src/org/apache/jmeter/reporters/TestResultSaver.java b/test/src/org/apache/jmeter/reporters/TestResultSaver.java
new file mode 100644
index 000000000..ca8d5356d
--- /dev/null
+++ b/test/src/org/apache/jmeter/reporters/TestResultSaver.java
@@ -0,0 +1,139 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ * 
+ */
+
+package org.apache.jmeter.reporters;
+
+import java.io.File;
+
+import org.apache.jmeter.junit.JMeterTestCase;
+import org.apache.jmeter.samplers.SampleEvent;
+import org.apache.jmeter.samplers.SampleResult;
+import org.apache.jmeter.threads.JMeterContext;
+import org.apache.jmeter.threads.JMeterContextService;
+import org.apache.jmeter.threads.JMeterVariables;
+import org.apache.jmeter.util.JMeterUtils;
+import org.junit.Assert;
+import org.junit.Before;
+import org.junit.Test;
+
+
+/**
+ * Test for {@link ResultSaver}
+ */
+public class TestResultSaver extends JMeterTestCase {
+    private ResultSaver resultSaver;
+    private SampleResult sampleResult;
+    private final String data = "response Data";
+    private final JMeterVariables vars = new JMeterVariables();
+
+    @Before
+    public void setUp() {
+        JMeterContext jmctx = JMeterContextService.getContext();
+        resultSaver = new ResultSaver();
+        resultSaver.setThreadContext(jmctx);
+        jmctx.setVariables(vars);
+        sampleResult = new SampleResult();
+        sampleResult.setResponseData(data, null);
+    }
+    
+    @Test
+    public void testSuccess() {
+        sampleResult.setSuccessful(true);
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.testStarted();
+        resultSaver.sampleOccurred(new SampleEvent(sampleResult, "JUnit-TG"));
+        String fileName = sampleResult.getResultFileName();
+        Assert.assertNotNull(fileName);
+        Assert.assertEquals("00001.unknown", fileName);
+        File file = new File(JMeterUtils.getJMeterHome(), fileName);
+        Assert.assertTrue(file.exists());
+        Assert.assertTrue(file.delete());
+    }
+    
+    @Test
+    public void testSuccessWithVariable() {
+        sampleResult.setSuccessful(true);
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.testStarted();
+        resultSaver.sampleOccurred(new SampleEvent(sampleResult, "JUnit-TG"));
+        String fileName = sampleResult.getResultFileName();
+        Assert.assertNotNull(fileName);
+        Assert.assertEquals("00001.unknown", fileName);
+        File file = new File(JMeterUtils.getJMeterHome(), fileName);
+        Assert.assertTrue(file.exists());
+        Assert.assertTrue(file.delete());
+        Assert.assertEquals("00001.unknown", vars.get("myVar"));
+    }
+    
+    @Test
+    public void testSuccessSaveErrorsOnly() {
+        sampleResult.setSuccessful(true);
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.setProperty(ResultSaver.ERRORS_ONLY, "true");
+        resultSaver.testStarted();
+        resultSaver.sampleOccurred(new SampleEvent(sampleResult, "JUnit-TG"));
+        String fileName = sampleResult.getResultFileName();
+        Assert.assertEquals("", fileName);
+        Assert.assertNull(vars.get("myVar"));
+    }
+    
+    @Test
+    public void testFailureSaveErrorsOnly() {
+        sampleResult.setSuccessful(true);
+        resultSaver.setProperty(ResultSaver.NUMBER_PAD_LENGTH, "5");
+        resultSaver.setProperty(ResultSaver.VARIABLE_NAME,"myVar");
+        resultSaver.setProperty(ResultSaver.ERRORS_ONLY, "true");
+        resultSaver.testStarted();
+        sampleResult.setSuccessful(false);
+        resultSaver.sampleOccurred(new SampleEvent(sampleResult, "JUnit-TG"));
+        String fileName = sampleResult.getResultFileName();
+        Assert.assertNotNull(fileName);
+        Assert.assertEquals("00001.unknown", fileName);
+        File file = new File(JMeterUtils.getJMeterHome(), fileName);
+        Assert.assertTrue(file.exists());
+        Assert.assertTrue(file.delete());
+        Assert.assertEquals("00001.unknown", vars.get("myVar"));
+    }
+    
+    @Test
+    public void testMakeFileName() {
+        resultSaver.setProperty(ResultSaver.FILENAME, "test");
+        resultSaver.testStarted();
+        Assert.assertEquals("test", resultSaver.makeFileName(null, true, true));
+        resultSaver.testStarted();
+        Assert.assertEquals("test", resultSaver.makeFileName("text/plain", true, true));
+        resultSaver.testStarted();
+        Assert.assertEquals("test", resultSaver.makeFileName("text/plain;charset=utf8", true, true));
+        
+        Assert.assertEquals("test1.plain", resultSaver.makeFileName("text/plain", false, false));
+        resultSaver.testStarted();
+        Assert.assertEquals("test.plain", resultSaver.makeFileName("text/plain", true, false));
+        resultSaver.testStarted();
+        Assert.assertEquals("test1", resultSaver.makeFileName("text/plain", false, true));
+        Assert.assertEquals("test2", resultSaver.makeFileName("text/plain", false, true));
+        
+        resultSaver.testStarted();
+        Assert.assertEquals("test.plain", resultSaver.makeFileName("text/plain;charset=UTF-8", true, false));
+        
+        resultSaver.testStarted();
+        Assert.assertEquals("test.unknown", resultSaver.makeFileName(null, true, false));
+
+    }
+}
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 66638d903..7e5b2cacd 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,512 +1,513 @@
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
 
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <ch_section>IMPORTANT CHANGES</ch_section>
 <p>
 JMeter now requires Java 8. Ensure you use the most up to date version.
 </p>
 <p>
 JMeter logging has been migrated to SLF4J and Log4j 2.
 This affects configuration and 3<sup>rd</sup> party plugins.
 </p>
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>JMeter now provides a new BackendListener implementation that interfaces InfluxDB through its HTTP API using Asynchronous HTTP requests.
 <figure width="813" height="407" image="changes/3.2/backend_influxdb.png"></figure>
 </li>
 <li>DNS Cache Manager now has a table to allow static host resolution.
 <figure width="803" height="561" image="changes/3.2/dns_cache_manager_static_hosts.png"></figure>
 </li>
 <li>JMS Publisher and Subscriber now allow reconnection on error with pause.
 <figure width="852" height="738" image="changes/3.2/jms_publisher_reconnect.png"></figure>
 <figure width="716" height="538" image="changes/3.2/jms_subscriber_reconnect_pause.png"></figure>
 </li>
 <li>Variables in JMS Publisher are now supported for all types of messages. Add the encoding type of the file to parse his content</li>
 <figure width="750" height="743" image="changes/3.2/jms_subscriber_content_encoding.png"></figure>
 <li>XPath Extractor now allows extraction randomly, by index or for all matches.
 <figure width="823" height="348" image="changes/3.2/xpath_extractor.png"></figure>
 </li>
 <li>Response Assertion now allows to work on Request Header, provides a "OR" combination and has a better cell renderer
 <figure width="1053" height="329" image="changes/3.2/response_assertion.png"></figure>
 </li>
 <li>HTTP HC4 Implementation now allows preemptive Basic Auth</li>
 <li>Embedded resources download in CSS has been improved to avoid useless repetitive parsing to find the resources</li>
 <li>An important work on code quality and code coverage with tests has been done since Sonar has been setup on the project.
 You can see Sonar report <a href="https://builds.apache.org/analysis/overview?id=12927" >here</a>.
 </li>
 </ul>
 
 <ch_title>UX improvements</ch_title>
 <ul>
 <li>When running a Test, GUI is now more responsive and less impacting on memory usage thanks to a limitation on the number of Sample Results 
 listeners hold and a rework of the way GUI is updated</li>
 <li>HTTP Request GUI has been simplified and provides more place for parameters and body.
 <figure width="848" height="475" image="changes/3.2/http_request.png"></figure>
 </li>
 <li>A <code>replace</code> feature has been added to Search feature to allow replacement in some elements.
 <figure width="459" height="196" image="changes/3.2/search_replace.png"></figure>
 </li>
 <li>View Results Tree now provides a more up to date Browser renderer which requires JavaFX.</li>
 <li>You can now add through a contextual menu think times, this will add think times between samplers and Transaction Controllers
  of selected node.
  <figure width="326" height="430" image="changes/3.2/menu_add_think_times.png"></figure>
  </li>
 <li>You can now apply a naming policy to children of a Transaction Controller. A default policy exists but you can implement your own 
     through <code><a href="./api/org/apache/jmeter/gui/action/TreeNodeNamingPolicy.html" >org.apache.jmeter.gui.action.TreeNodeNamingPolicy</a></code>
     and configuring property <code>naming_policy.impl</code>
 <figure width="327" height="518" image="changes/3.2/menu_apply_naming_policy.png"></figure>    
 </li>
 <li>Sorting per column has been added to View Results in Table, Summary Report, Aggregate Report and Aggregate Graph elements.
 <figure width="1065" height="369" image="changes/3.2/sorting.png"></figure>
 </li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>PDF Documentations have been migrated to HTML user manual</li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a Java 8 version to run.</li>
     <li>JMeter logging has been migrated to SLF4J and Log4j 2, this involves changes in the way configuration is done. JMeter now relies on standard
     <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">Log4j 2 configuration</a> in file <code>log4j2.xml</code>
     See <code>Logging changes</code> section below for further details.
     </li>
     <li>The following jars have been removed after migration from LogKit to SLF4J (see <bugzilla>60589</bugzilla>):
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
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> two protected static fields (<code>localhost</code> and <code>nonProxyHostSuffixSize</code>) have been renamed to (<code>LOCALHOST</code> and <code>NON_PROXY_HOST_SUFFIX_SIZE</code>) 
         to follow static fields naming convention</li>
     <li>JMeter now uses by default Oracle Nashorn engine instead of Mozilla Rhino for better performances. This should not have an impact unless
     you use some advanced features. You can revert back to Rhino by settings property <code>javascript.use_rhino=true</code>. 
     You can read this <a href="https://wiki.openjdk.java.net/display/Nashorn/Rhino+Migration+Guide">migration guide</a> for more details on Nashorn. See <bugzilla>60672</bugzilla></li>
     <li><bug>60729</bug>The Random Variable Config Element now allows minimum==maximum. Previous versions logged an error when minimum==maximum and did not set the configured variable.</li>
     <li><bug>60730</bug>The JSON PostProcessor now sets the <code>_ALL</code> variable (assuming <code>Compute concatenation var</code> was checked)
     even if the JSON path matches only once. Previous versions did not set the <code>_ALL</code> variable in this case.</li>
 </ul>
 
 <h3>Removed elements or functions</h3>
 <ul>
     <li>SOAP/XML-RPC Request has been removed as part of <bugzilla>60727</bugzilla>. Use HTTP Request element as a replacement. 
     See <a href="./build-ws-test-plan.html" >Building a WebService Test Plan</a></li>
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
 
 <h3>Logging changes</h3>
 <p>
     JMeter logging has been migrated to SLF4J and Log4j 2.
     This affects logging configuration and 3<sup>rd</sup> party plugins (if they use JMeter logging).
     The following sections describe what changes need to be made.
 </p>
 
 <h4>Setting the logging level and log file</h4>
 <p>
     The default logging level can be changed on the command-line using the <code>-L</code> parameter.
     Likewise the <code>-l</code> parameter can be used to change the name of the log file.
     However the <code>log_level</code> properties no longer work.
 </p>
 <p>
     The default logging levels and file name are defined in the <code>log4j2.xml</code> configuration file
     in the launch directory (usually <code>JMETER_HOME/bin</code>)
 </p>
 <p>
     <note>If you need to change the level programmatically from Groovy code or Beanshell, you need to do the following:
     <source>
     import org.apache.logging.log4j.core.config.Configurator;
     ...
     final String loggerName = te.getClass().getName(); // te being a JMeter class
     Configurator.setAllLevels(loggerName, Level.DEBUG); 
     </source>
     </note>
 </p>
 
 <h4>Changes to 3<sup>rd</sup> party plugin logging</h4>
 <p>
     <note>3rd party plugins should migrate their logging code from logkit to slf4j. This is fairly easy and can be done by replacing:
     <source>
         import org.apache.jorphan.logging.LoggingManager;
         import org.apache.log.Logger;
         ...
         private static final Logger log = LoggingManager.getLoggerForClass();
     </source>
     By:
     <source>
         import org.slf4j.Logger;
         import org.slf4j.LoggerFactory;
         ...
         private static final Logger log = LoggerFactory.getLogger(YourClassName.class);
     </source>
     </note>
 </p>
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
     <li><bug>57242</bug>HTTP Authorization is not pre-emptively set with HttpClient4</li>
     <li><bug>60727</bug>Drop commons-httpclient-3.1 and related elements. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60790</bug>HTTP(S) Test Script Recorder : Improve information on certificate expiration and have better UX for Start/Stop</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>60740</bug>Support variable for all JMS messages (bytes, object, &hellip;) and sources (file, folder), based on <pr>241</pr>. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><pr>259</pr> - Refactored and reformatted SmtpSampler. Contributed by Graham Russell (graham at ham1.co.uk)</li>
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
     <li><bug>60687</bug>Make GUI more responsive when it gets a lot of events.</li>
     <li><bug>60791</bug>View Results Tree: Trigger search on Enter key in Search Feature and display red background if no match</li>
     <li><bug>60822</bug>ResultCollector does not ensure unique file name entries in files HashMap</li>
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
     <li>Improve translation "<code>save_as</code>" in French. Based on a <pr>252</pr> by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60785</bug>Improvement of Japanese translation. Patch by Kimono (kimono.outfit.am at gmail.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60637</bug>Improve Statistics table design <figure image="dashboard/report_statistics.png" ></figure></li>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>58164</bug>Check if file already exists on ResultCollector listener before starting the loadtest</li>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60711</bug>Improve Delete button behaviour for Assertions / Header Manager / User Parameters GUIs / Exclude, Include in HTTP(S) Test Script Recorder</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
     <li><bug>59995</bug>Allow user to change font size with two new menu items and use <code>jmeter.hidpi.scale.factor</code> for scaling fonts. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60654</bug>Validation Feature : Be able to ignore BackendListener. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60646</bug>Workbench : Save it by default</li>
     <li><bug>60684</bug>Thread Group: Validate ended prematurely by Scheduler with 0 or very short duration. Contributed by Andrew Burton (andrewburtonatwh at gmail.com).</li>
     <li><bug>60589</bug>Migrate LogKit to SLF4J - Drop Avalon, LogKit and Excalibur with backward compatibility for 3<sup>rd</sup> party modules. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60565</bug>Migrate LogKit to SLF4J - Optimize logging statements. e.g, message format args, throwable args, unnecessary if-enabled-logging in simple ones, etc. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60564</bug>Migrate LogKit to SLF4J - Replace LogKit loggers with SLF4J ones and keep the current LogKit binding solution for backward compatibility with plugins. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60664</bug>Add a UI menu to set log level. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><pr>276</pr> - Added some translations for polish locale. Contributed by Bartosz Siewniak (barteksiewniak at gmail.com)</li>
     <li><bug>60792</bug>Create a new Help menu item to create a thread dump</li>
     <li><bug>60813</bug>JSR223 Test element : Take into account JMeterStopTestNowException, JMeterStopTestException and JMeterStopThreadException</li>
     <li><bug>60814</bug>Menu : Add <code>Open Recent</code> menu item to make recent files loading more obvious</li>
     <li><bug>60815</bug>Drop "Reset GUI" from menu</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.9 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to httpclient 4.5.3 (from 4.5.2)</li>
     <li>Updated to jodd 3.8.1 (from 3.7.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.24 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Updated to rsyntaxtextarea-2.6.1 (from 2.6.0)</li>
     <li>Updated to commons-net-3.6 (from 3.5)</li>
     <li>Converted the old pdf tutorials to xml.</li>
     <li><pr>255</pr> - Utilised Java 8 (and 7) features to tidy up code. Contributed by Graham Russell (graham at ham1.co.uk)</li>
     <li><bug>59435</bug>JMeterTestCase no longer supports JUnit3</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
     <li><bug>60575</bug>HTTP GET Requests could have a content-type header without a body.</li>
     <li><bug>60682</bug>HTTP Request : Get method may fail on redirect due to Content-Length header being set</li>
     <li><bug>60643</bug>HTTP(S) Test Script Recorder doesn't correctly handle restart or start after stop. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60652</bug>HTTP PUT Requests might leak file descriptors.</li>
     <li><bug>60689</bug><code>httpclient4.validate_after_inactivity</code> has no impact leading to usage of potentially stale/closed connections</li>
     <li><bug>60690</bug>Default values for "httpclient4.validate_after_inactivity" and "httpclient4.time_to_live" which are equal to each other makes validation useless</li>
     <li><bug>60758</bug>HTTP(s) Test Script Recorder : Number request may generate duplicate numbers. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>56939</bug>Parameters are not passed with OPTIONS HTTP Request</li>
     <li><bug>60778</bug>Http Java Impl does not show Authorization header in SampleResult even if it is sent</li>
     <li><bug>60837</bug>GET with body, PUT are not retried even if <code>httpclient4.retrycount</code> is higher than 0</li>
     <li><bug>60842</bug>Trim extracted URLs when loading embedded resources using the Lagarto based HTML Parser.</li>
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
     <li><bug>60759</bug>View Results Tree : Search feature does not search in URL. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
+    <li><bug>60859</bug>Save Responses to a file : 2 elements with different configuration will overlap</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr> - Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
     <li><bug>60607</bug>DNS Cache Manager configuration is ignored</li>
     <li><bug>60729</bug>The Random Variable Config Element should allow minimum==maximum</li>
     <li><bug>60730</bug>The JSON PostProcessor should set the <code>_ALL</code> variable even if the JSON path matches only once.</li>
     <li><bug>60747</bug>Response Assertion : Add Request Headers to <code>Field to Test</code></li>
     <li><bug>60763</bug>XMLAssertion should not leak errors to console</li>
     <li><bug>60797</bug>TestAction in pause mode can last beyond configured duration of test</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>60819</bug>Function __fileToString does not honor the documentation contract when file is not found</li>
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
     <li><bug>60775</bug>NamePanel ctor calls overrideable method</li>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of <code>TestHTTPMirrorThread#testSleep()</code>. Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
     <li><bug>60621</bug>The "<code>report-template</code>" folder is missing from <code>ApacheJMeter_config-3.1.jar</code> in maven central</li>
     <li><bug>60744</bug>GUI elements are not cleaned up when reused during load of Test Plan which can lead them to be partially initialized with a previous state for a new Test Element</li>
     <li><bug>60812</bug>JMeterThread does not honor contract of JMeterStopTestNowException</li>
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
 <li>Kimono (kimono.outfit.am at gmail.com)</li>
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
 <li>View Results Tree may freeze rendering large response particularly if this response has no spaces, see <bugzilla>60816</bugzilla>.
 This is due to an identified Java Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8172336">UI stuck when calling JEditorPane.setText() or JTextArea.setText() with long text without space</a>.
 </li>
 </ul>
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
