diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
index ab80ebb51..8ab9e2c26 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
@@ -1,85 +1,86 @@
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
 
 
 import java.io.IOException;
 import java.net.URL;
 
 import org.apache.commons.httpclient.HttpClient;
-import org.apache.commons.httpclient.methods.PostMethod;
+import org.apache.commons.httpclient.HttpMethod;
+import org.apache.commons.httpclient.HttpMethodBase;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.samplers.Interruptible;
 
 /**
  * A sampler which understands all the parts necessary to read statistics about
  * HTTP requests, including cookies and authentication.
  * This sampler uses HttpClient 3.1.
  *
  */
 public class HTTPSampler2 extends HTTPSamplerBase implements Interruptible {
 
     private static final long serialVersionUID = 240L;
 
     private final transient HTTPHC3Impl hc;
     
     public HTTPSampler2(){
         hc = new HTTPHC3Impl(this);
     }
 
     public boolean interrupt() {
         return hc.interrupt();
     }
 
     @Override
     protected HTTPSampleResult sample(URL u, String method,
             boolean areFollowingRedirect, int depth) {
         return hc.sample(u, method, areFollowingRedirect, depth);
     }
 
     // Methods needed by subclasses to get access to the implementation
-    protected HttpClient setupConnection(URL url, PostMethod httpMethod, HTTPSampleResult res) 
+    protected HttpClient setupConnection(URL url, HttpMethodBase httpMethod, HTTPSampleResult res) 
         throws IOException {
         return hc.setupConnection(url, httpMethod, res);
     }
 
-    protected void saveConnectionCookies(PostMethod httpMethod, URL url,
+    protected void saveConnectionCookies(HttpMethod httpMethod, URL url,
             CookieManager cookieManager) {
         hc.saveConnectionCookies(httpMethod, url, cookieManager);
    }
 
-    protected String getResponseHeaders(PostMethod httpMethod) {
+    protected String getResponseHeaders(HttpMethod httpMethod) {
         return hc.getResponseHeaders(httpMethod);
     }
 
-    protected String getConnectionHeaders(PostMethod httpMethod) {
+    protected String getConnectionHeaders(HttpMethod httpMethod) {
         return hc.getConnectionHeaders(httpMethod);
     }
 
     protected void setSavedClient(HttpClient savedClient) {
         hc.savedClient = savedClient;
     }
 
     /**
      * {@inheritDoc}
      * This implementation forwards to the implementation class.
      */
     @Override
     protected void notifySSLContextWasReset() {
         hc.notifySSLContextWasReset();
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 96586d535..2903cc6b8 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,257 +1,258 @@
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
 <li>Bug 52310 - variable in IPSource failed HTTP request if "Concurrent Pool Size" is enabled</li>
+<li>Bug 52371 - API Incompatibility - Methods in HTTPSampler2 now require PostMethod instead of HttpMethod[Base]. Reverted to original types.</li>
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
 <li>Bug 52296 - TransactionController + Children ThrouputController or InterleaveController leads to ERROR sampleEnd called twice java.lang.Throwable: Invalid call sequence when TPC does not run sample</li>
 <li>Bug 52330 - With next-Loop-On-Error after error samples are not executed in next loop</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52357 - View results in Table does not allow for multiple result samples</li>
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
 <li>Bug 52352 - Proxy : Support IPv6 URLs capture</li>
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
 <li>Bug 52340 - Allow remote sampling mode to be changed at run-time</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 52128 - Add JDBC pre- and post-processor</li>
 <li>Bug 52183 - SyncTimer could be improved (performance+reliability)</li>
 <li>Bug 52317 - Counter : Add option to reset counter on each Thread Group iteration</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
 <li>__char() function now supports octal values</li>
 <li>New function __machineIP returning IP address</li>
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
 <li>Added DiskStore remote sample sender: like Hold, but saves samples to disk until end of test.</li>
 <li>Bug 52333 - Reduce overhead in calculating SampleResult#nanoTimeOffset</li>
 <li>Bug 52346 - Shutdown detects if there are any non-daemon threads left which prevent JVM exit.</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>fixes to build.xml: support scripts; localise re-usable property names</li>
 <li>Bug 51923 - Counter function bug or documentation issue ? (fixed docs)</li>
 <li>Update velocity.jar to 1.7 (from 1.6.2)</li>
 <li>Update js.jar to 1.7R3 (from 1.6R5)</li>
 <li>Bug 51954 - Generated documents include &lt;/br&gt; entries which cause extra blank lines </li>
 <li>Bug 52075 - JMeterProperty.clone() currently returns Object; it should return JMeterProperty</li>
 </ul>
 
 </section> 
 </body> 
 </document>
