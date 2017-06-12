diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 2b3cf3907..6daa093e1 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,252 +1,252 @@
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
-                    SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars);
+                    SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
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
-            if(res != null) {
-                SampleResult sampleResult = se.getResult();
+            if(res != null && !se.isTransactionSampleEvent()) {
+               	SampleResult sampleResult = se.getResult();
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
diff --git a/src/core/org/apache/jmeter/samplers/SampleEvent.java b/src/core/org/apache/jmeter/samplers/SampleEvent.java
index 9ee1b8359..bcdc2da6e 100644
--- a/src/core/org/apache/jmeter/samplers/SampleEvent.java
+++ b/src/core/org/apache/jmeter/samplers/SampleEvent.java
@@ -1,164 +1,182 @@
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
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
 /**
  * Packages information regarding the target of a sample event, such as the
  * result from that event and the thread group it ran in.
  */
 public class SampleEvent implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 232L;
 
     public static final String SAMPLE_VARIABLES = "sample_variables"; // $NON-NLS-1$
 
     public static final String HOSTNAME;
 
     // List of variable names to be saved in JTL files
     private static final String[] variableNames;
     // Number of variable names
     private static final int varCount;
 
     // The values. Entries be null, but there will be the correct number.
     private final String[] values;
 
     static {
         String hn="";
         try {
             hn = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException e) {
             log.error("Cannot obtain local host name "+e);
         }
         HOSTNAME=hn;
 
         String vars = JMeterUtils.getProperty(SAMPLE_VARIABLES);
            variableNames=vars != null ? vars.split(",") : new String[0];
            varCount=variableNames.length;
         if (varCount>0){
             log.info(varCount + " sample_variables have been declared: "+vars);
         }
     }
 
 
     private final SampleResult result;
 
     private final String threadGroup; // TODO appears to duplicate the threadName field in SampleResult
 
     private final String hostname;
 
+    private final boolean isTransactionSampleEvent;
 
     /*
      * Only for Unit tests
      */
     public SampleEvent() {
         this(null, null);
     }
 
     /**
      * Creates SampleEvent without saving any variables.
      *
      * Use by Proxy and StatisticalSampleSender.
      *
      * @param result SampleResult
      * @param threadGroup name
      */
     public SampleEvent(SampleResult result, String threadGroup) {
-        this.result = result;
-        this.threadGroup = threadGroup;
-        this.hostname = HOSTNAME;
-        values = new String[variableNames.length];
+        this(result, threadGroup, HOSTNAME, false);
     }
 
     /**
      * Contructor used for normal samples, saves variable values if any are defined.
      *
      * @param result
      * @param threadGroup name
      * @param jmvars Jmeter variables
      */
     public SampleEvent(SampleResult result, String threadGroup, JMeterVariables jmvars) {
-        this.result = result;
-        this.threadGroup = threadGroup;
-        this.hostname = HOSTNAME;
-        values = new String[variableNames.length];
-        saveVars(jmvars);
+        this(result, threadGroup, jmvars, false);
     }
 
     /**
      * Only intended for use when loading results from a file.
      *
      * @param result
      * @param threadGroup
      * @param hostname
      */
     public SampleEvent(SampleResult result, String threadGroup, String hostname) {
+       this(result, threadGroup, hostname, false);
+    }
+    
+    private SampleEvent(SampleResult result, String threadGroup, String hostname, boolean isTransactionSampleEvent) {
         this.result = result;
         this.threadGroup = threadGroup;
         this.hostname = hostname;
         values = new String[variableNames.length];
+        this.isTransactionSampleEvent = isTransactionSampleEvent;
+    }
+
+    /**
+     * @param result
+     * @param threadGroup
+     * @param jmvars
+     * @param isTransactionSampleEvent
+     */
+    public SampleEvent(SampleResult result, String threadGroup, JMeterVariables jmvars, boolean isTransactionSampleEvent) {
+        this(result, threadGroup, HOSTNAME, isTransactionSampleEvent);
+        saveVars(jmvars);
     }
 
     private void saveVars(JMeterVariables vars){
         for(int i = 0; i < variableNames.length; i++){
             values[i] = vars.get(variableNames[i]);
         }
     }
 
     /** Return the number of variables defined */
     public static int getVarCount(){
         return varCount;
     }
 
     /** Get the nth variable name (zero-based) */
     public static String getVarName(int i){
         return variableNames[i];
     }
 
     /** Get the nth variable value (zero-based) */
     public String getVarValue(int i){
         try {
             return values[i];
         } catch (ArrayIndexOutOfBoundsException e) {
             throw new JMeterError("Check the sample_variable settings!", e);
         }
     }
 
     public SampleResult getResult() {
         return result;
     }
 
     public String getThreadGroup() {
         return threadGroup;
     }
 
     public String getHostname() {
         return hostname;
     }
+
+    /**
+     * @return the isTransactionSampleEvent
+     */
+    public boolean isTransactionSampleEvent() {
+        return isTransactionSampleEvent;
+    }
+
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 961de9e22..282563fd5 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,162 +1,163 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
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
 
 <h1>Version 2.4.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <p>
 <ul>
 </ul>
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
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 50178 - HeaderManager added as child of Thread Group can create concatenated HeaderManager names and OutOfMemoryException</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 50173 - JDBCSampler discards ResultSet from a PreparedStatement</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 50032 - Last_Sample_Ok along with other controllers doesnt work correctly when the threadgroup has multiple loops</li>
 <li>Bug 50080 - Transaction controller incorrectly creates samples including timer duration</li>
+<li>Bug 50134 - TransactionController : Reports bad response time when it contains other TransactionControllers</li>
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
 <li>Bug 49734 - Null pointer exception on stop Threads command (Run>Stop)</li>
 <li>Bug 49666 - CSV Header read as data after EOF</li>
 <li>Bug 45703 - Synchronizing Timer</li>
 <li>Bug 50088 - fix getAvgPageBytes in SamplingStatCalculator so it returns what it should</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 49622 - Allow sending messages without a subject (SMTP Sampler)</li>
 <li>Bug 49603 - Allow accepting expired certificates on Mail Reader Sampler</li>
 <li>Bug 49775 - Allow sending messages without a body</li>
 <li>Bug 49862 - Improve SMTPSampler Request output.</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>View Results Tree - Add a dialog's text box on "Sampler result tab > Parsed" to display the long value with a double click on cell</li>
 <li>Bug 37156 - Formatted view of Request in Results Tree</li>
 <li>Bug 49365 - Allow result set to be written to file in a path relative to the loaded script</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 48015 - Proposal new icons for pre-processor, post-processor and assertion elements</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 49975 - New function returning the name of the current sampler</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 30563 - Thread Group should have a start next loop option on Sample Error</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Bug 50008 - Allow BatchSampleSender to be subclassed</li>
 </ul>
 
 </section> 
 </body> 
 </document>
