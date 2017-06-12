diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 0a29c3a45..b4f0a6bf2 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,214 +1,215 @@
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
 
     private static final String PARENT = "TransactionController.parent";// $NON-NLS-1$
 
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
         }
 
         Sampler returnValue = super.next();
 
         if (returnValue == null) // Must be the end of the controller
         {
             if (res != null) {
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
                     SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars);
                     // We must set res to null now, before sending the event for the transaction,
                     // so that we can ignore that event in our sampleOccured method
                     res = null;
                     lnf.notifyListeners(event, pack.getSampleListeners());
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
             if(res != null) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytes() + sampleResult.getBytes());
                 if(!sampleResult.isSuccessful()) {
                     res.setSuccessful(false);
                     noFailingSamples++;
                 }
                 res.setAllThreads(sampleResult.getAllThreads());
                 res.setGroupThreads(sampleResult.getGroupThreads());
+                res.setLatency(res.getLatency() + sampleResult.getLatency());
             }
         }
     }
 
     public void sampleStarted(SampleEvent e) {
     }
 
     public void sampleStopped(SampleEvent e) {
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index cd6eb23fc..eb5bccc66 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,189 +1,190 @@
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
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h1>Version 2.4</h1>
 
 <h2>Summary of main changes</h2>
 
 <p>
 </p>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 The XPath Assertion and XPath Extractor elements no longer fetch external DTDs by default; this can be changed in the GUI.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 47445 -  Using Proxy with https-spoofing secure cookies need to be unsecured</li>
 <li>Bug 47442 -  Missing replacement of https by http for certain conditions using https-spoofing</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 47420 - LDAP extended request not closing connections during add request</li>
 <li>Bug 47870 - JMSSubscriber fails due to NPE</li>
 <li>Bug 47899 -  NullPointerExceptions in ReceiveSubscriber constructor</li>
 <li>Bug 48144 - NPE in OnMessageSubscriber</li>
 <li>Bug 47992 - JMS Point-to-Point Request - Response option doesn't work</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 47385 - TransactionController should set AllThreads and GroupThreads</li>
 <li>Bug 47940 - Module controller incorrectly creates the replacement Sub Tree</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
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
 <li>Bug 47646 -  NullPointerException in the "Random Variable" element</li>
 <li>Disallow adding any child elements to JDBC Configuration</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 47622 - enable recording of HTTPS sessions</li>
 <li>Allow Proxy Server to be specified on HTTP Sampler GUI and HTTP Config GUI</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>JUnit sampler now supports JUnit4 tests (using annotations)</li>
 <li>Bug 47900 - Allow JMS SubscriberSampler to be interrupted</li>
 <li>Added JSR223 Sampler</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
+<li>Bug 47909 - TransactionController should sum the latency</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 47398 - SampleEvents are sent twice over RMI in distributed testing and non gui mode</li>
 <li>Added DataStrippingSample sender - supports "Stripped" and "StrippedBatch" modes.</li>
 <li>Added Comparison Assertion Visualizer</li>
 <li>Bug 47907 - Improvements (enhancements and I18N) Comparison Assertion and Comparison Visualizer</li>
 <li>Bug 36726 - add search function to Tree View Listener</li>
 <li>Bug 47869 - Ability to cleanup fields of SampleResult</li>
 <li>Bug 47952 - Added JSR223 Listener</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 47338 - XPath Extractor forces retrieval of document DTD</li>
 <li>Added Comparison Assertion</li>
 <li>Bug 47952 - Added JSR223 PreProcessor and PostProcessor</li>
 <li>Added JSR223 Assertion</li>
 <li>Added BSF Timer and JSR223 Timer</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 47565 - [Function] FileToString</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>Bug 47938 -  Adding some French translations for new elements</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 47223 - Slow Aggregate Report Performance (StatCalculator)</li>
 <li>Bug 47980 - hostname resolves to 127.0.0.1 - specifiying IP not possible</li>
 <li>Bug 47943 - DisabledComponentRemover is not used in Start class</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Add TestBean Table Editor support</li>
 <li>Remove external libraries from SVN as far as possible; add download Ant target</li>
 <li>Updated various jar files:
 <ul>
 <li>BeanShell - 2.0b4 => 2.0b5</li>
 <li>Commons Codec - 1.3 => 1.4</li>
 <li>Commons-Collections - 3.2 => 3.2.1</li>
 <li>JUnit - 3.8.2 => 4.7</li>
 <li>Logkit - 1.2 => 2.0</li>
 <li>Xalan Serializer = 2.7.1 (previously erroneously shown as 2.9.1)</li>
 <li>Xerces xml-apis = 1.3.04 (previously erroneously shown as 2.9.1)</li>
 <li>Some jar files were renamed.</li>
 </ul>
 </li>
 </ul>
 
 </section> 
 </body> 
 </document>
