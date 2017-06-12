diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index ead3b6b4a..59aa272cd 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,311 +1,322 @@
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
 import org.apache.jmeter.threads.JMeterContextService;
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
     
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
     private static final String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
     
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final boolean DEFAULT_VALUE_FOR_INCLUDE_TIMERS = true; // default true for compatibility
 
     /**
      * Only used in parent Mode
      */
     private transient TransactionSampler transactionSampler;
     
     /**
      * Only used in NON parent Mode
      */
     private transient ListenerNotifier lnf;
 
     /**
      * Only used in NON parent Mode
      */
     private transient SampleResult res;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int calls;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      * Only used in NON parent Mode
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      * Only used in NON parent Mode
      */
     private transient long prevEndTime;
 
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
     @Override
     protected Object readResolve(){
         super.readResolve();
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
         boolean isLast = current==super.subControllersAndSamplers.size();
         Sampler returnValue = super.next();
         if (returnValue == null && isLast) // Must be the end of the controller
         {
             if (res != null) {
                 // See BUG 55816
                 if (!isIncludeTimers()) {
                     long processingTimeOfLastChild = res.currentTimeInMillis() - prevEndTime;
                     pauseTime += processingTimeOfLastChild;
                 }
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
                 notifyListeners();
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
      */
     @Override
     public void triggerEndOfLoop() {
         if(!isParent()) {
             if (res != null) {
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
                 res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
                 res.setResponseMessage("Number of samples in transaction : " + calls + ", number of failing samples : " + noFailingSamples);
                 notifyListeners();
             }
         } else {
+            Sampler subSampler = transactionSampler.getSubSampler();
+            // See Bug 56811
+            // triggerEndOfLoop is called when error occurs to end Main Loop
+            // in this case normal workflow doesn't happen, so we need 
+            // to notify the childs of TransactionController and 
+            // update them with SubSamplerResult
+            if(subSampler instanceof TransactionSampler) {
+                TransactionSampler tc = (TransactionSampler) subSampler;
+                tc.getTransactionController().triggerEndOfLoop();
+                transactionSampler.addSubSamplerResult(tc.getTransactionResult());
+            }
             transactionSampler.setTransactionDone();
             // This transaction is done
             transactionSampler = null;
         }
         super.triggerEndOfLoop();
     }
 
     /**
      * Create additional SampleEvent in NON Parent Mode
      */
     protected void notifyListeners() {
         // TODO could these be done earlier (or just once?)
         JMeterContext threadContext = getThreadContext();
         JMeterVariables threadVars = threadContext.getVariables();
         SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
         if (pack == null) {
             // If child of TransactionController is a ThroughputController and TPC does
             // not sample its children, then we will have this
             // TODO Should this be at warn level ?
             log.warn("Could not fetch SamplePackage");
         } else {
             SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
             // We must set res to null now, before sending the event for the transaction,
             // so that we can ignore that event in our sampleOccured method
             res = null;
             lnf.notifyListeners(event, pack.getSampleListeners());
         }
     }
 
     @Override
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
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * Whether to include timers and pre/post processor time in overall sample.
      * @param includeTimers
      */
     public void setIncludeTimers(boolean includeTimers) {
         setProperty(INCLUDE_TIMERS, includeTimers, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 4bcd246ff..23e4e016b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,404 +1,405 @@
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
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.12 =================== -->
 
 <h1>Version 2.12</h1>
 
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
 
 <ch_title>Java 8 support</ch_title>
 <p>
 Now, JMeter 2.12 is compliant with Java 8.
 </p>
 
 <ch_category>New Elements</ch_category>
 <ch_title>Critical Section Controller</ch_title>
 <p>The Critical Section Controller allow to serialize the execution of a section in your tree. 
 Only one instance of the section will be executed at the same time during the test.</p>
 <figure width="683" height="240" image="changes/2.12/01_critical_section_controller.png"></figure>
 
 <ch_title>DNS Cache Manager</ch_title>
 <p>The new configuration element <b>DNS Cache Manager</b> allow to improve the testing of CDN (Content Delivery Network) 
 and/or DNS load balancing.</p>
 <figure width="573" height="359" image="changes/2.12/02_dns_cache_manager.png"></figure>
 
 <ch_category>Core Improvements</ch_category>
 
 <ch_title>Smarter Recording of Http Test Plans</ch_title>
 <p>Test Script Recorder has been improved in many ways</p>
 <ul>
     <li>Better matching of Variables in Requests, making Test Script Recorder variabilize your sampler during recording more versatile</li>
     <li>Ability to filter from View Results Tree the Samples that are excluded from recording, this lets you concentrate on recorded Samplers analysis and not bother with useless Sample Results</li>
     <li>Better defaults for recording, since this version Recorder will number created Samplers letting you find them much easily in View Results Tree. Grouping of Samplers under Transaction Controller will
     will be smarter making all requests emitted by a web page be children as new Transaction Controller</li>
 </ul>
 
 <ch_title>Better handling of embedded resources</ch_title>
 <p>When download embedded resources is checked, JMeter now uses User Agent header to download or not resources embedded within conditionnal comments as per <a href="http://msdn.microsoft.com/en-us/library/ms537512%28v=vs.85%29.aspx" target="_blank">About conditional comments</a>.</p>
 
 <ch_title>Ability to customize Cache Manager (Browser cache simulation) handling of cached resources</ch_title>
 <p>You can now configure the behaviour of JMeter when a resource is found in Cache, this can be controlled with <i>cache_manager.cached_resource_mode</i> property</p>
 <figure width="1024" height="314" image="changes/2.12/12_cache_resource_mode.png"></figure>
 
 
 <ch_title>JMS Publisher / JMS Point-to-Point</ch_title>
 <p> Add JMSPriority and JMSExpiration fields for these samplers.</p>
 <figure width="901" height="277" image="changes/2.12/04_jms_publisher.png"></figure>
 
 <figure width="900" height="294" image="changes/2.12/05_jms_point_to_point.png"></figure>
 
 <ch_title>Mail Reader Sampler</ch_title>
 <p>You can now specify the number of messages that want you retrieve (before all messages were retrieved). 
 In addition, you can fetch only the message header now.</p>
 <figure width="814" height="416" image="changes/2.12/03_mail_reader_sampler.png"></figure>
 
 <ch_title>SMTP Sampler</ch_title>
 <p>Adding the Connection timeout and the Read timeout to the <b>SMTP Sampler.</b></p>
 <figure width="796" height="192" image="changes/2.12/06_smtp_sampler.png"></figure>
 
 <ch_title>Synchronizing Timer </ch_title>
 <p>Adding a timeout to define the maximum time to waiting of the group of virtual users.</p>
 <figure width="546" height="144" image="changes/2.12/09_synchronizing_timer.png"></figure>
 
 <ch_category>GUI Improvements</ch_category>
 
 <ch_title>Undo/Redo support</ch_title>
 <p>Undo / Redo has been introduced and allows user to undo/redo changes made on Test Plan Tree. This feature (ALPHA MODE) is disabled by default, to enable it set property <b>undo.history.size=25</b> </p>
 <figure width="1024" height="56" image="changes/2.12/10_undo_redo.png"></figure>
 
 <ch_title>View Results Tree</ch_title>
 <p>Improve the ergonomics of View Results Tree by changing placement of Renderers and allowing custom ordering 
 (with the property <i>view.results.tree.renderers_order</i>).</p>
 <figure width="900" height="329" image="changes/2.12/07_view_results_tree.png"></figure>
 
 <ch_title>Response Time Graph</ch_title>
 <p>Adding the ability for the <b>Response Time Graph</b> listener to save/restore format its settings in/from the jmx file.</p>
 <figure width="997" height="574" image="changes/2.12/08_response_time_graph.png"></figure>
 
 <ch_title>Log Viewer</ch_title>
 <p>Starting with this version, jmeter logs can be viewed in GUI by clicking on Warning icon in the upper right corner. This will unfold the Log Viewer panel and show logs.</p>
 <figure width="1024" height="437" image="changes/2.12/11_log_viewer.png"></figure>
 
 
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
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
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
 
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 <li>Since JMeter 2.12, active threads in all thread groups and active threads in current thread group are saved by default to CSV or XML results, see <bugzilla>57025</bugzilla>. If you want to revert to previous behaviour, set property <b>jmeter.save.saveservice.thread_counts=true</b></li>
 <li>Since JMeter 2.12, Mail Reader Sampler will show 1 for number of samples instead of number of messages retrieved, see <bugzilla>56539</bugzilla></li>
 <li>Since JMeter 2.12, when using Cache Manager, if resource is found in cache no SampleResult will be created, in previous version a SampleResult with empty content and 204 return code was returned, see <bugzilla>54778</bugzilla>.
 You can choose between different ways to handle this case, see cache_manager.cached_resource_mode in jmeter.properties.</li>
 <li>Since JMeter 2.12, Log Viewer will no more clear logs when closed and will have logs available even if closed. See <bugzilla>56920</bugzilla>. Read <a href="./usermanual/hints_and_tips.html#debug_logging">Hints and Tips &gt; Enabling Debug logging</a>
 for details on configuring this component.</li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55998</bugzilla> - HTTP recording – Replacing port value by user defined variable does not work</li>
 <li><bugzilla>56178</bugzilla> - keytool error: Invalid escaped character in AVA: - some characters must be escaped</li>
 <li><bugzilla>56222</bugzilla> - NPE if jmeter.httpclient.strict_rfc2616=true and location is not absolute</li>
 <li><bugzilla>56263</bugzilla> - DefaultSamplerCreator should set BrowserCompatible Multipart true</li>
 <li><bugzilla>56231</bugzilla> - Move redirect location processing from HC3/HC4 samplers to HTTPSamplerBase#followRedirects()</li>
 <li><bugzilla>56207</bugzilla> - URLs get encoded on redirects in HC3.1 &amp; HC4 samplers</li>
 <li><bugzilla>56303</bugzilla> - The width of target controller's combo list should be set to the current panel size, not on label size of the controllers</li>
 <li><bugzilla>54778</bugzilla> - HTTP Sampler should not return 204 when resource is found in Cache, make it configurable with new property cache_manager.cached_resource_mode</li> 
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55977</bugzilla> - JDBC pool keepalive flooding</li>
 <li><bugzilla>55999</bugzilla> - Scroll bar on jms point-to-point sampler does not work when content exceeds display</li>
 <li><bugzilla>56198</bugzilla> - JMSSampler : NullPointerException is thrown when JNDI underlying implementation of JMS provider does not comply with Context.getEnvironment contract</li>
 <li><bugzilla>56428</bugzilla> - MailReaderSampler - should it use mail.pop3s.* properties?</li>
 <li><bugzilla>46932</bugzilla> - Alias given in select statement is not used as column header in response data for a JDBC request.Based on report and analysis of Nicola Ambrosetti</li>
 <li><bugzilla>56539</bugzilla> - Mail reader sampler: When Number of messages to retrieve is superior to 1, Number of samples should only show 1 not the number of messages retrieved</li>
 <li><bugzilla>56809</bugzilla> - JMSSampler closes InitialContext too early. Contributed by Bradford Hovinen (hovinen at gmail.com)</li>
 <li><bugzilla>56761</bugzilla> - JMeter tries to stop already stopped JMS connection and displays "The connection is closed"</li>
 <li><bugzilla>57068</bugzilla> - No error thrown when negative duration is entered in Test Action</li>
 <li><bugzilla>57078</bugzilla> - LagartoBasedHTMLParser fails to parse page that contains input with no type</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56243</bugzilla> - Foreach works incorrectly with indexes on subsequent iterations </li>
 <li><bugzilla>56276</bugzilla> - Loop controller becomes broken once loop count evaluates to zero </li>
 <li><bugzilla>56160</bugzilla> - StackOverflowError when using WhileController within IfController</li>
+<li><bugzilla>56811</bugzilla> - "Start Next Thread Loop" in Result Status Action Handler or on Thread Group and "Go to next Loop iteration" in Test Action behave incorrectly with TransactionController that has "Generate Parent Sampler" checked</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56706</bugzilla> - SampleResult#getResponseDataAsString() does not use encoding in response body impacting PostProcessors and ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57052</bugzilla> - ArithmeticException: / by zero when sampleCount is equal to 0</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56162</bugzilla> -  HTTP Cache Manager should not cache PUT/POST etc.</li>
 <li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 <li><bugzilla>41319</bugzilla> - URLRewritingModifier : Allow Parameter value to be url encoded</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>56111</bugzilla> - "comments" in german translation is not correct</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>56059</bugzilla> - Older TestBeans incompatible with 2.11 when using TextAreaEditor</li>
 <li><bugzilla>56080</bugzilla> - Conversion error com.thoughtworks.xstream.converters.ConversionException with Java 8 Early Access Build</li>
 <li><bugzilla>56182</bugzilla> - Can't trigger bsh script using bshclient.jar; socket is closed unexpectedly </li>
 <li><bugzilla>56360</bugzilla> - HashTree and ListedHashTree fail to compile with Java 8</li>
 <li><bugzilla>56419</bugzilla> - Jmeter silently fails to save results</li>
 <li><bugzilla>56662</bugzilla> - Save as xml in a listener is not remembered</li>
 <li><bugzilla>56367</bugzilla> - JMeter 2.11 on maven central triggers a not existing dependency rsyntaxtextarea 2.5.1, upgrade to 2.5.3</li>
 <li><bugzilla>56743</bugzilla> - Wrong mailing list archives on mail2.xml. Contributed by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56763</bugzilla> - Removing the Oracle icons, not used by JMeter (and missing license)</li>
 <li><bugzilla>54100</bugzilla> - Switching languages fails to preserve toolbar button states (enabled/disabled)</li>
 <li><bugzilla>54648</bugzilla> - JMeter GUI on OS X crashes when using CMD+C (keyboard shortcut or UI menu entry) on an element from the tree</li>
 <li><bugzilla>56962</bugzilla> - JMS GUIs should disable all fields affected by jndi.properties checkbox</li>
 <li><bugzilla>57061</bugzilla> - Save as Test Fragment fails to clone deeply selected node. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57075</bugzilla> - BeanInfoSupport.MULTILINE attribute is not processed</li>
 <li><bugzilla>57076</bugzilla> - BooleanPropertyEditor#getAsText() must return a value that is in getTags()</li>
 <li><bugzilla>57088</bugzilla> - NPE in ResultCollector.testEnded</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55959</bugzilla> - Improve error message when Test Script Recorder fails due to I/O problem</li>
 <li><bugzilla>52013</bugzilla> - Test Script Recorder's Child View Results Tree does not take into account Test Script Recorder excluded/included URLs. Based on report and analysis of James Liang</li>
 <li><bugzilla>56119</bugzilla> - File uploads fail every other attempt using timers. Enable idle timeouts for servers that don't send Keep-Alive headers.</li>
 <li><bugzilla>56272</bugzilla> - MirrorServer should support query parameters for status and redirects</li>
 <li><bugzilla>56772</bugzilla> - Handle IE Conditional comments when parsing embedded resources</li>
 <li><bugzilla>57026</bugzilla> - HTTP(S) Test Script Recorder : Better default settings. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57107</bugzilla> - Patch proposal: Add DAV verbs to HTTP Sampler. Contributed by Philippe Jung (apache at famille-jung.fr)</li>
 <li><bugzilla>56357</bugzilla> - Certificates does not conform to algorithm constraints: Adding a note to indicate how to remove of the Java installation these new security constraints</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>56033</bugzilla> - Add Connection timeout and Read timeout to SMTP Sampler</li>
 <li><bugzilla>56429</bugzilla> - MailReaderSampler - no need to fetch all Messages if not all wanted</li>
 <li><bugzilla>56427</bugzilla> - MailReaderSampler enhancement: read message header only</li>
 <li><bugzilla>56510</bugzilla> - JMS Publisher/Point to Point: Add JMSPriority and JMSExpiration</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56728</bugzilla> - New Critical Section Controller to serialize blocks of a Test. Based partly on a patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56228</bugzilla> - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering</li>
 <li><bugzilla>56349</bugzilla> - "summary" is a bad name for a Generate Summary Results component, documentation clarified</li>
 <li><bugzilla>56769</bugzilla> - Adds the ability for the Response Time Graph listener to save/restore format settings in/from the jmx file</li>
 <li><bugzilla>57025</bugzilla> - SaveService : Better defaults, save thread counts by default</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56691</bugzilla> - Synchronizing Timer : Add timeout on waiting</li>
 <li><bugzilla>56701</bugzilla> - HTTP Authorization Manager/ Kerberos Authentication: add port to SPN when server port is neither 80 nor 443. Based on patches from Dan Haughey (dan.haughey at swinton.co.uk) and Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56841</bugzilla> - New configuration element: DNS Cache Manager to improve the testing of CDN. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 <li><bugzilla>52061</bugzilla> - Allow access to Request Headers in Regex Extractor. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com), and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>56708</bugzilla> - __jexl2 doesn't scale with multiple CPU cores. Based on analysis and patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 <li><bugzilla>57114</bugzilla> - Performance : Functions that only have values as instance variable should not synchronize execute. Based on analysis by Ubik Load Pack support and Vladimir Sitnikov, patch contributed by Vladimir Sitnikov (sitnikov.vladimir at gmail.com)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>21695</bugzilla> - Unix jmeter start script assumes it is on PATH, not a link</li>
 <li><bugzilla>56292</bugzilla> - Add the check of the Java's version in startup files and disable some options when is Java v8 engine</li>
 <li><bugzilla>56298</bugzilla> - JSR223 language display does not show which engine will be used</li>
 <li><bugzilla>56455</bugzilla> - Batch files: drop support for non-NT Windows shell scripts</li>
 <li><bugzilla>56807</bugzilla> - Ability to force flush of ResultCollector file. Contributed by Andrey Pohilko (apc4 at ya.ru)</li>
 <li><bugzilla>56921</bugzilla> - Templates : Improve Recording template to ignore embedded resources case and URL parameters. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>42248</bugzilla> - Undo-redo support on Test Plan tree modification. Developed by Andrey Pohilko (apc4 at ya.ru) and contributed by BlazeMeter Ltd. Additional contribution by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>56920</bugzilla> - LogViewer : Make it receive all log events even when it is closed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>57083</bugzilla> - simplified the CachedResourceMode enum. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57082</bugzilla> - ComboStringEditor : Added hashCode to an inner class which overwrote equals. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57081</bugzilla> - Updating checkstyle to only check for tabs in java, xml, xsd, dtd, htm, html and txt files (not images!). Contributed by Graham Russell (graham at ham1.co.uk)</li>
 <li><bugzilla>56178</bugzilla> - Really replace backslashes in user name before generating proxy certificate. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 <li><bugzilla>57084</bugzilla> - Close socket after usage in BeahShellClient. Contributed by Graham Russel (graham at ham1.co.uk)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li><bugzilla>57117</bugzilla> - Increase the default cipher for HTTPS Test Script Recorder from SSLv3 to TLS</li>
 <li>Updated to commons-lang3 3.3.2 (from 3.1)</li>
 <li>Updated to commons-codec 1.9 (from 1.8)</li>
 <li>Updated to commons-logging 1.2 (from 1.1.3)</li>
 <li>Updated to tika 1.6 (from 1.4)</li>
 <li>Updated to xercesImpl 2.11.0 (from 2.9.1)</li>
 <li>Updated to xml-apis 1.4.01 (from 1.3.04)</li>
 <li>Updated to xstream 1.4.7 (from 1.4.4)</li>
 <li>Updated to jodd 3.6.1 (from 3.4.10)</li>
 <li>Updated to rsyntaxtextarea 2.5.3 (from 2.5.1)</li>
 <li>Updated xalan and serializer to 2.7.2 (from 2.7.1)</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li>James Liang (jliang at andera.com)</li>
 <li>Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Nicola Ambrosetti (ambrosetti.nicola at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li>Dan Haughey (dan.haughey at swinton.co.uk)</li>
 <li>Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li>Andrey Pohilko (apc4 at ya.ru)</li>
 <li>Bradford Hovinen (hovinen at gmail.com)</li>
 <li><a href="http://blazemeter.com">BlazeMeter Ltd.</a></li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Philippe Jung (apache at famille-jung.fr)</li>
 <li>Vladimir Sitnikov (sitnikov.vladimir at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Oliver LLoyd (email at oliverlloyd.com) for his help on <bugzilla>56119</bugzilla></li>
 <li>Vladimir Ryabtsev (greatvovan at gmail.com) for his help on <bugzilla>56243</bugzilla> and <bugzilla>56276</bugzilla></li>
 <li>Adrian Speteanu (asp.adieu at gmail.com) and Matt Kilbride (matt.kilbride at gmail.com) for their feedback and tests on <bugzilla>54648</bugzilla></li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
