diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index bf9e233bf..ce0dce2a2 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,254 +1,257 @@
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
 public class TransactionController extends GenericController implements SampleListener, Controller, Serializable {
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
 
-        if (returnValue == null) // Must be the end of the controller
+        if (returnValue == null && !(calls == 0)) // Must be the end of the controller
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
+                	// If child of TransactionController is a ThroughputController and TPC does
+                	// not sample its children, then we will have this
+                	// TODO Should this be at warn level ?
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
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 8164714a8..59db2dc1b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,245 +1,246 @@
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
 
 
 <!--  =================== 2.5.2 =================== -->
 
 <h1>Version 2.5.2</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode (see Bugs 40671, 41286, 44973, 50898). 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>The If Controller may cause an infinite loop if the condition is always false from the first iteration. 
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 JMeter versions since 2.1 failed to create a container sample when loading embedded resources.
 This has been corrected; can still revert to the Bug 51939 behaviour by setting the following property:
 <code>httpsampler.separate.container=false</code>
 </p>
 <p>
 Mirror server now uses default port 8081, was 8080 before 2.5.1.
 </p>
 <p>
 TCP Sampler handles SocketTimeoutException, SocketException and InterruptedIOException differently since 2.5.2, when
 these occurs, Sampler is marked as failed.
 </p>
 <p>
 Sample Sender implementations know resolve their configuration on Client side since 2.5.2.
 This behaviour can be changed with property sample_sender_client_configured (set it to false).
 </p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 51932 - CacheManager does not handle cache-control header with any attributes after max-age</li>
 <li>Bug 51918 - GZIP compressed traffic produces errors, when multiple connections allowed</li>
 <li>Bug 51939 - Should generate new parent sample if necessary when retrieving embedded resources</li>
 <li>Bug 51942 - Synchronisation issue on CacheManager when Concurrent Download is used</li>
 <li>Bug 51957 - Concurrent get can hang if a task does not complete</li>
 <li>Bug 51925 - Calling Stop on Test leaks executor threads when concurrent download of resources is on</li>
 <li>Bug 51980 - HtmlParserHTMLParser double-counts images used in links</li>
 <li>Bug 52064 - OutOfMemory Risk in CacheManager</li>
 <li>Bug 51919 - Random ConcurrentModificationException or NoSuchElementException in CookieManager#removeMatchingCookies when using Concurrent Download</li>
 <li>Bug 52126 - HttpClient4 does not clear cookies between iterations</li>
 <li>Bug 52129 - Reported Body Size is wrong when using HTTP Client 4 and Keep Alive connection</li>
 <li>Bug 52137 - Problems with HTTP Cache Manager</li>
 <li>Bug 52221 - Nullpointer Exception with use Retrieve Embedded Resource without HTTP Cache Manager</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51996 - JMS Initial Context leak newly created Context when Multiple Thread enter InitialContextFactory#lookupContext at the same time</li>
 <li>Bug 51691 - Authorization does not work for JMS Publisher and JMS Subscriber</li>
 <li>Bug 52036 - Durable Subscription fails with ActiveMQ due to missing clientId field</li>
 <li>Bug 52044 - JMS Subscriber used with many threads leads to javax.naming.NamingException: Something already bound with ActiveMQ</li>
 <li>Bug 52072 - LengthPrefixedBinaryTcpClientImpl may end a sample prematurely</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 51865 - Infinite loop inside thread group does not work properly if "Start next loop after a Sample error" option set</li>
 <li>Bug 51868 - A lot of exceptions in jmeter.log while using option "Start next loop" for thread</li>
 <li>Bug 51866 - Counter under loop doesn't work properly if "Start next loop on error" option set for thread group</li>
+<li>Bug 52296 - Getting ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>The CRLF example for the char function was wrong; CRLF=(0xD,0xA), not (0xC,0xA)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51937 - JMeter does not handle missing TestPlan entry well</li>
 <li>Bug 51988 - CSV Data Set Configuration does not resolve default delimiter for header parsing when variables field is empty</li>
 <li>Bug 52003 - View Results Tree "Scroll automatically" does not scroll properly in case nodes are expanded</li>
 <li>Bug 27112 - User Parameters should use scrollbars</li>
 <li>Bug 52029 - Command-line shutdown only gets sent to last engine that was started</li>
 <li>Bug 52093 - Toolbar ToolTips don't switch language</li>
 <li>Bug 51733 - SyncTimer is messed up if you a interrupt a test plan</li>
 <li>Bug 52118 - New toolbar : shutdown and stop buttons not disabled when no test is running</li>
 <li>Bug 52125 - StatCalculator.addAll(StatCalculator calc) joins incorrect if there are more samples with the same response time in one of the TreeMap</li>
 <li>Bug 52215 - Confusing synchronization in StatVisualizer, SummaryReport ,Summariser and issue in StatGraphVisualizer</li>
 <li>Bug 52216 - TableVisualizer : currentData field is badly synchronized</li>
 <li>Bug 52217 - ViewResultsFullVisualizer : Synchronization issues on root and treeModel</li>
 <li>Bug 43294 - XPath Extractor namespace problems</li>
 <li>Bug 52224 - TestBeanHelper does not support NOT_UNDEFINED == Boolean.FALSE</li>
 <li>Bug 52279 - Switching to another language loses icons in Tree and logs error Can't obtain GUI class from ...</li>
 <li>Bug 52280 - The menu item Options / Choose Language does not change all the displayed text to the new language</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 51981 - Better support for file: protocol in HTTP sampler</li>
 <li>Bug 52033 - Allowing multiple certificates (JKS)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 51419 - JMS Subscriber: ability to use Selectors</li>
 <li>Bug 52088 - JMS Sampler : Add a selector when REQUEST / RESPONSE is chosen</li>
 <li>Bug 52104 - TCP Sampler handles badly errors</li>
 <li>Bug 52087 - TCPClient interface does not allow for partial reads</li>
 <li>Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found</li>
 <li>Bug 40750 - TCPSampler : Behaviour when sockets are closed by remote host</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured</li>
 <li>Bug 52201 - Add option to TableVisualiser to display child samples instead of parent </li>
 <li>Bug 52214 - Save Responses to a file - improve naming algorithm</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 52128 - Add JDBC pre- and post-processor</li>
 <li>Bug 52183 - SyncTimer could be improved (performance+reliability)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
 <li>__char() function now supports octal values</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51892 - Default mirror port should be different from default proxy port</li>
 <li>Bug 51817 - Moving variables up and down in User Defined Variables control</li>
 <li>Bug 51876 - Functionality to search in Samplers TreeView</li>
 <li>Bug 52019 - Add menu option to Start a test ignoring Pause Timers</li>
 <li>Bug 52027 - Allow System or CrossPlatform LAF to be set from options menu</li>
 <li>Bug 52037 - Remember user-set LaF over restarts.</li>
 <li>Bug 51861 - Improve HTTP Request GUI to better show parameters without name (GWT RPC requests for example) (UNDER DEVELOPMENT)</li>
 <li>Bug 52040 - Add a toolbar in JMeter main window</li>
 <li>Bug 51816 - Comment Field in User Defined Variables control.</li>
 <li>Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber</li>
 <li>Bug 52103 - Add automatic scrolling option to table visualizer</li>
 <li>Bug 52097 - Save As should point to same folder that was used to open a file if MRU list is used</li>
 <li>Bug 52085 - Allow multiple selection in arguments panel</li>
 <li>Bug 52099 - Allow to set the transaction isolation in the JDBC Connection Configuration</li>
 <li>Bug 52116 - Allow to add (paste) entries from the clipboard to an arguments list</li>
 <li>Bug 51091 - New function returning the name of the current "Test Plan"</li>
 <li>Bug 52160 - Don't display TestBeanGui items which are flagged as hidden</li>
 <li>Bug 51886 - SampleSender configuration resolved partly on client and partly on server</li>
 <li>Bug 52161 - Enable plugins to add own translation rules in addition to upgrade.properties.
 Loads any additional properties found in META-INF/resources/org.apache.jmeter.nameupdater.properties files</li>
 <li>Bug 42538 - Add "duplicate node" in context menu</li>
 <li>Bug 46921 - Add Ability to Change Controller elements</li>
 <li>Bug 52240 - TestBeans should support Boolean, Integer and Long</li>
 <li>Bug 52241 - GenericTestBeanCustomizer assumes that the default value is the empty string</li>
 <li>Bug 52242 - FileEditor does not allow output to be saved in a File </li>
 <li>Bug 51093 - when loading a selection previously stored by "Save Selection As", show the file name in the blue window bar</li>
 <li>Bug 50086 - Password fields not Hidden in JMS Publisher, JMS Subscriber, Mail Reader sampler, SMTP sampler and Database Configuration</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>fixes to build.xml: support scripts; localise re-usable property names</li>
 <li>Bug 51923 - Counter function bug or documentation issue ? (fixed docs)</li>
 <li>Update velocity.jar to 1.7 (from 1.6.2)</li>
 <li>Bug 51954 - Generated documents include &lt;/br&gt; entries which cause extra blank lines </li>
 <li>Bug 52075 - JMeterProperty.clone() currently returns Object; it should return JMeterProperty</li>
 </ul>
 
 </section> 
 </body> 
 </document>
