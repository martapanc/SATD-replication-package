diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 20b5ff9d4..3e5e2bed3 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,309 +1,314 @@
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
+                // See BUG 55816
+                if (!isIncludeTimers()) {
+                    long processingTimeOfLastChild = res.currentTimeInMillis()-prevEndTime;
+                    pauseTime += processingTimeOfLastChild;
+                }
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
             // bug 50032 
             if (!getThreadContext().isReinitializingSubControllers()) {
                 lnf.notifyListeners(event, pack.getSampleListeners());
             }
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
index 6757eb403..80698fca0 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,223 +1,224 @@
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
 
 
 <!--  =================== 2.11 =================== -->
 
 <h1>Version 2.11</h1>
 
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
 
 <ch_category>Core Improvements</ch_category>
 
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
 
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
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
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 <li></li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55815</bugzilla> - Proxy#getDomainMatch does not handle wildcards correctly</li>
 <li><bugzilla>55717</bugzilla> - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient3.1</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55685</bugzilla> - OS Sampler: timeout option don't save and restore correctly value and don't init correctly timeout</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
+<li><bugzilla>55816</bugzilla> - Transaction Controller with "Include duration of timer..." unchecked does not ignore processing time of last child sampler</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>55694</bugzilla> - Assertions and Extractors : Avoid NullPointerException when scope is variable and variable is missing</li>
 <li><bugzilla>55721</bugzilla> - HTTP Cache Manager - no-store directive is wrongly interpreted</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>55739</bugzilla> - Remote Test : Total threads in GUI mode shows invalid total number of threads</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>55610</bugzilla> - View Results Tree : Add an XPath Tester</li>
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
 <li><bugzilla>55693</bugzilla> - Add a "Save as Test Fragment" option</li>
 <li><bugzilla>55753</bugzilla> - Improve FilePanel behaviour to start from the value set in Filename field if any. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55756</bugzilla> - HTTP Mirror Server : Add ability to set Headers</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above.<br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Firstname Name (email at gmail.com)</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
