diff --git a/src/protocol/ftp/org/apache/jmeter/protocol/ftp/sampler/FTPSampler.java b/src/protocol/ftp/org/apache/jmeter/protocol/ftp/sampler/FTPSampler.java
index fb0eef038..dde112435 100644
--- a/src/protocol/ftp/org/apache/jmeter/protocol/ftp/sampler/FTPSampler.java
+++ b/src/protocol/ftp/org/apache/jmeter/protocol/ftp/sampler/FTPSampler.java
@@ -1,324 +1,324 @@
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
 
 package org.apache.jmeter.protocol.ftp.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.io.output.NullOutputStream;
 import org.apache.commons.io.output.TeeOutputStream;
 import org.apache.commons.net.ftp.FTP;
 import org.apache.commons.net.ftp.FTPClient;
 import org.apache.commons.net.ftp.FTPReply;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * A sampler which understands FTP file requests.
  *
  */
 public class FTPSampler extends AbstractSampler implements Interruptible {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.ftp.config.gui.FtpConfigGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui"
             ));
     
     public static final String SERVER = "FTPSampler.server"; // $NON-NLS-1$
 
     public static final String PORT = "FTPSampler.port"; // $NON-NLS-1$
 
     // N.B. Originally there was only one filename, and only get(RETR) was supported
     // To maintain backwards compatibility, the property name needs to remain the same
     public static final String REMOTE_FILENAME = "FTPSampler.filename"; // $NON-NLS-1$
 
     public static final String LOCAL_FILENAME = "FTPSampler.localfilename"; // $NON-NLS-1$
 
     public static final String INPUT_DATA = "FTPSampler.inputdata"; // $NON-NLS-1$
 
     // Use binary mode file transfer?
     public static final String BINARY_MODE = "FTPSampler.binarymode"; // $NON-NLS-1$
 
     // Are we uploading?
     public static final String UPLOAD_FILE = "FTPSampler.upload"; // $NON-NLS-1$
 
     // Should the file data be saved in the response?
     public static final String SAVE_RESPONSE = "FTPSampler.saveresponse"; // $NON-NLS-1$
 
     private transient volatile FTPClient savedClient; // used for interrupting the sampler
 
     public FTPSampler() {
     }
 
     public String getUsername() {
         return getPropertyAsString(ConfigTestElement.USERNAME);
     }
 
     public String getPassword() {
         return getPropertyAsString(ConfigTestElement.PASSWORD);
     }
 
     public void setServer(String newServer) {
         this.setProperty(SERVER, newServer);
     }
 
     public String getServer() {
         return getPropertyAsString(SERVER);
     }
 
     public void setPort(String newPort) {
         this.setProperty(PORT, newPort, ""); // $NON-NLS-1$
     }
 
     public String getPort() {
         return getPropertyAsString(PORT, ""); // $NON-NLS-1$
     }
 
     public int getPortAsInt() {
         return getPropertyAsInt(PORT, 0);
     }
 
     public String getRemoteFilename() {
         return getPropertyAsString(REMOTE_FILENAME);
     }
 
     public String getLocalFilename() {
         return getPropertyAsString(LOCAL_FILENAME);
     }
 
     private String getLocalFileContents() {
         return getPropertyAsString(INPUT_DATA);
     }
 
     public boolean isBinaryMode(){
         return getPropertyAsBoolean(BINARY_MODE,false);
     }
 
     public boolean isSaveResponse(){
         return getPropertyAsBoolean(SAVE_RESPONSE,false);
     }
 
     public boolean isUpload(){
         return getPropertyAsBoolean(UPLOAD_FILE,false);
     }
 
 
     /**
      * Returns a formatted string label describing this sampler Example output:
      * ftp://ftp.nowhere.com/pub/README.txt
      *
      * @return a formatted string label describing this sampler
      */
     public String getLabel() {
         StringBuilder sb = new StringBuilder();
         sb.append("ftp://");// $NON-NLS-1$
         sb.append(getServer());
         String port = getPort();
         if (port.length() > 0){
             sb.append(':');
             sb.append(port);
         }
         sb.append("/");// $NON-NLS-1$
         sb.append(getRemoteFilename());
         sb.append(isBinaryMode() ? " (Binary) " : " (Ascii) ");// $NON-NLS-1$ $NON-NLS-2$
         sb.append(isUpload() ? " <- " : " -> "); // $NON-NLS-1$ $NON-NLS-2$
         sb.append(getLocalFilename());
         return sb.toString();
     }
 
     @Override
     public SampleResult sample(Entry e) {
         SampleResult res = new SampleResult();
         res.setSuccessful(false); // Assume failure
         String remote = getRemoteFilename();
         String local = getLocalFilename();
         boolean binaryTransfer = isBinaryMode();
         res.setSampleLabel(getName());
         final String label = getLabel();
         res.setSamplerData(label);
         try {
             res.setURL(new URL(label));
         } catch (MalformedURLException e1) {
             log.warn("Cannot set URL: "+e1.getLocalizedMessage());
         }
         InputStream input = null;
         OutputStream output = null;
 
         res.sampleStart();
         FTPClient ftp = new FTPClient();
         try {
             savedClient = ftp;
             final int port = getPortAsInt();
             if (port > 0){
                 ftp.connect(getServer(),port);
             } else {
                 ftp.connect(getServer());
             }
             res.latencyEnd();
             int reply = ftp.getReplyCode();
             if (FTPReply.isPositiveCompletion(reply))
             {
                 if (ftp.login( getUsername(), getPassword())){
                     if (binaryTransfer) {
                         ftp.setFileType(FTP.BINARY_FILE_TYPE);
                     }
                     ftp.enterLocalPassiveMode();// should probably come from the setup dialog
                     boolean ftpOK=false;
                     if (isUpload()) {
                         String contents=getLocalFileContents();
                         if (contents.length() > 0){
                             byte[] bytes = contents.getBytes(); // TODO - charset?
                             input = new ByteArrayInputStream(bytes);
-                            res.setBytes(bytes.length);
+                            res.setBytes((long)bytes.length);
                         } else {
                             File infile = new File(local);
-                            res.setBytes((int)infile.length());
+                            res.setBytes(infile.length());
                             input = new BufferedInputStream(new FileInputStream(infile));
                         }
                         ftpOK = ftp.storeFile(remote, input);
                     } else {
                         final boolean saveResponse = isSaveResponse();
                         ByteArrayOutputStream baos=null; // No need to close this
                         OutputStream target=null; // No need to close this
                         if (saveResponse){
                             baos  = new ByteArrayOutputStream();
                             target=baos;
                         }
                         if (local.length()>0){
                             output=new FileOutputStream(local);
                             if (target==null) {
                                 target=output;
                             } else {
                                 target = new TeeOutputStream(output,baos);
                             }
                         }
                         if (target == null){
                             target=new NullOutputStream();
                         }
                         input = ftp.retrieveFileStream(remote);
                         if (input == null){// Could not access file or other error
                             res.setResponseCode(Integer.toString(ftp.getReplyCode()));
                             res.setResponseMessage(ftp.getReplyString());
                         } else {
                             long bytes = IOUtils.copy(input,target);
                             ftpOK = bytes > 0;
                             if (saveResponse && baos != null){
                                 res.setResponseData(baos.toByteArray());
                                 if (!binaryTransfer) {
                                     res.setDataType(SampleResult.TEXT);
                                 }
                             } else {
-                                res.setBytes((int) bytes);
+                                res.setBytes(bytes);
                             }
                         }
                     }
 
                     if (ftpOK) {
                         res.setResponseCodeOK();
                         res.setResponseMessageOK();
                         res.setSuccessful(true);
                     } else {
                         res.setResponseCode(Integer.toString(ftp.getReplyCode()));
                         res.setResponseMessage(ftp.getReplyString());
                     }
                 } else {
                     res.setResponseCode(Integer.toString(ftp.getReplyCode()));
                     res.setResponseMessage(ftp.getReplyString());
                 }
             } else {
                 res.setResponseCode("501"); // TODO
                 res.setResponseMessage("Could not connect");
                 //res.setResponseCode(Integer.toString(ftp.getReplyCode()));
                 res.setResponseMessage(ftp.getReplyString());
             }
         } catch (IOException ex) {
             res.setResponseCode("000"); // TODO
             res.setResponseMessage(ex.toString());
         } finally {
             savedClient = null;
             if (ftp.isConnected()) {
                 try {
                     ftp.logout();
                 } catch (IOException ignored) {
                 }
                 try {
                     ftp.disconnect();
                 } catch (IOException ignored) {
                 }
             }
             IOUtils.closeQuietly(input);
             IOUtils.closeQuietly(output);
         }
 
         res.sampleEnd();
         return res;
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         FTPClient client = savedClient;
         if (client != null) {
             savedClient = null;
             try {
                 client.abort();
             } catch (IOException ignored) {
             }
             try {
                 client.disconnect();
             } catch (IOException ignored) {
             }
         }
         return client != null;
     }
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
index b2a726c64..da026349e 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
@@ -1,512 +1,512 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Enumeration;
 
 import javax.jms.BytesMessage;
 import javax.jms.JMSException;
 import javax.jms.MapMessage;
 import javax.jms.Message;
 import javax.jms.ObjectMessage;
 import javax.jms.TextMessage;
 import javax.naming.NamingException;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.protocol.jms.client.InitialContextFactory;
 import org.apache.jmeter.protocol.jms.client.ReceiveSubscriber;
 import org.apache.jmeter.protocol.jms.control.gui.JMSSubscriberGui;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This class implements the JMS Subscriber sampler.
  * It supports both receive and onMessage strategies via the ReceiveSubscriber class.
  * 
  */
 // TODO: do we need to implement any kind of connection pooling?
 // If so, which connections should be shared?
 // Should threads share connections to the same destination?
 // What about cross-thread sharing?
 
 // Note: originally the code did use the ClientPool to "share" subscribers, however since the
 // key was "this" and each sampler is unique - nothing was actually shared.
 
 public class SubscriberSampler extends BaseJMSSampler implements Interruptible, ThreadListener, TestStateListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Default wait (ms) for a message if timeouts are not enabled
     // This is the maximum time the sampler can be blocked.
     private static final long DEFAULT_WAIT = 500L;
 
     // No need to synch/ - only used by sampler
     // Note: not currently added to the ClientPool
     private transient ReceiveSubscriber SUBSCRIBER = null;
 
     private transient volatile boolean interrupted = false;
 
     private transient long timeout;
     
     private transient boolean useReceive;
 
     // This will be null if initialization succeeds.
     private transient Exception exceptionDuringInit;
 
     // If true, start/stop subscriber for each sample
     private transient boolean stopBetweenSamples;
 
     // Don't change the string, as it is used in JMX files
     private static final String CLIENT_CHOICE = "jms.client_choice"; // $NON-NLS-1$
     private static final String TIMEOUT = "jms.timeout"; // $NON-NLS-1$
     private static final String TIMEOUT_DEFAULT = ""; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID = "jms.durableSubscriptionId"; // $NON-NLS-1$
     private static final String CLIENT_ID = "jms.clientId"; // $NON-NLS-1$
     private static final String JMS_SELECTOR = "jms.selector"; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID_DEFAULT = "";
     private static final String CLIENT_ID_DEFAULT = ""; // $NON-NLS-1$
     private static final String JMS_SELECTOR_DEFAULT = ""; // $NON-NLS-1$
     private static final String STOP_BETWEEN = "jms.stop_between_samples"; // $NON-NLS-1$
     private static final String SEPARATOR = "jms.separator"; // $NON-NLS-1$
     private static final String SEPARATOR_DEFAULT = ""; // $NON-NLS-1$
 
     
     private transient boolean START_ON_SAMPLE = false;
 
     private transient String separator;
 
     public SubscriberSampler() {
         super();
     }
 
     /**
      * Create the OnMessageSubscriber client and set the sampler as the message
      * listener.
      * @throws JMSException 
      * @throws NamingException 
      *
      */
     private void initListenerClient() throws JMSException, NamingException {
         SUBSCRIBER = new ReceiveSubscriber(0, getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(),
                     getProviderUrl(), getConnectionFactory(), getDestination(), getDurableSubscriptionId(),
                     getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         setupSeparator();
         log.debug("SubscriberSampler.initListenerClient called");
     }
 
     /**
      * Create the ReceiveSubscriber client for the sampler.
      * @throws NamingException 
      * @throws JMSException 
      */
     private void initReceiveClient() throws NamingException, JMSException {
         SUBSCRIBER = new ReceiveSubscriber(getUseJNDIPropertiesAsBoolean(),
                 getJNDIInitialContextFactory(), getProviderUrl(), getConnectionFactory(), getDestination(),
                 getDurableSubscriptionId(), getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         setupSeparator();
         log.debug("SubscriberSampler.initReceiveClient called");
     }
 
     /**
      * sample method will check which client it should use and call the
      * appropriate client specific sample method.
      *
      * @return the appropriate sample result
      */
     // TODO - should we call start() and stop()?
     @Override
     public SampleResult sample() {
         // run threadStarted only if Destination setup on each sample
         if (!isDestinationStatic()) {
             threadStarted(true);
         }
         SampleResult result = new SampleResult();
         result.setDataType(SampleResult.TEXT);
         result.setSampleLabel(getName());
         result.sampleStart();
         if (exceptionDuringInit != null) {
             result.sampleEnd();
             result.setSuccessful(false);
             result.setResponseCode("000");
             result.setResponseMessage(exceptionDuringInit.toString());
             return result; 
         }
         if (stopBetweenSamples){ // If so, we need to start collection here
             try {
                 SUBSCRIBER.start();
             } catch (JMSException e) {
                 log.warn("Problem starting subscriber", e);
             }
         }
         StringBuilder buffer = new StringBuilder();
         StringBuilder propBuffer = new StringBuilder();
         
         int loop = getIterationCount();
         int read = 0;
         
         long until = 0L;
         long now = System.currentTimeMillis();
         if (timeout > 0) {
             until = timeout + now; 
         }
         while (!interrupted
                 && (until == 0 || now < until)
                 && read < loop) {
             Message msg;
             try {
                 msg = SUBSCRIBER.getMessage(calculateWait(until, now));
                 if (msg != null){
                     read++;
                     extractContent(buffer, propBuffer, msg, (read == loop));
                 }
             } catch (JMSException e) {
                 log.warn("Error "+e.toString());
             }
             now = System.currentTimeMillis();
         }
         result.sampleEnd();
         if (getReadResponseAsBoolean()) {
             result.setResponseData(buffer.toString().getBytes()); // TODO - charset?
         } else {
-            result.setBytes(buffer.toString().length());
+            result.setBytes((long)buffer.toString().length());
         }
         result.setResponseHeaders(propBuffer.toString());
         if (read == 0) {
             result.setResponseCode("404"); // Not found
             result.setSuccessful(false);
         } else if (read < loop) { // Not enough messages found
             result.setResponseCode("500"); // Server error
             result.setSuccessful(false);
         } else { 
             result.setResponseCodeOK();
             result.setSuccessful(true);
         }
         result.setResponseMessage(read + " message(s) received successfully of " + loop + " expected");
         result.setSamplerData(loop + " messages expected");
         result.setSampleCount(read);
         
         if (stopBetweenSamples){
             try {
                 SUBSCRIBER.stop();
             } catch (JMSException e) {
                 log.warn("Problem stopping subscriber", e);
             }
         }
         // run threadFinished only if Destination setup on each sample (stop Listen queue)
         if (!isDestinationStatic()) {
             threadFinished(true);
         }
         return result;
     }
 
     /**
      * Calculate the wait time, will never be more than DEFAULT_WAIT.
      * 
      * @param until target end time or 0 if timeouts not active
      * @param now current time
      * @return wait time
      */
     private long calculateWait(long until, long now) {
         if (until == 0) {
             return DEFAULT_WAIT; // Timeouts not active
         }
         long wait = until - now; // How much left
         return wait > DEFAULT_WAIT ? DEFAULT_WAIT : wait;
     }
 
     private void extractContent(StringBuilder buffer, StringBuilder propBuffer,
             Message msg, boolean isLast) {
         if (msg != null) {
             try {
                 if (msg instanceof TextMessage){
                     buffer.append(((TextMessage) msg).getText());
                 } else if (msg instanceof ObjectMessage){
                     ObjectMessage objectMessage = (ObjectMessage) msg;
                     if(objectMessage.getObject() != null) {
                         buffer.append(objectMessage.getObject().getClass());
                     } else {
                         buffer.append("object is null");
                     }
                 } else if (msg instanceof BytesMessage){
                     BytesMessage bytesMessage = (BytesMessage) msg;
                     buffer.append(bytesMessage.getBodyLength() + " bytes received in BytesMessage");
                 } else if (msg instanceof MapMessage){
                     MapMessage mapm = (MapMessage) msg;
                     @SuppressWarnings("unchecked") // MapNames are Strings
                     Enumeration<String> enumb = mapm.getMapNames();
                     while(enumb.hasMoreElements()){
                         String name = enumb.nextElement();
                         Object obj = mapm.getObject(name);
                         buffer.append(name);
                         buffer.append(",");
                         buffer.append(obj.getClass().getCanonicalName());
                         buffer.append(",");
                         buffer.append(obj);
                         buffer.append("\n");
                     }
                 }
                 Utils.messageProperties(propBuffer, msg);
                 if(!isLast && !StringUtils.isEmpty(separator)) {
                     propBuffer.append(separator);
                     buffer.append(separator);
                 }
             } catch (JMSException e) {
                 log.error(e.getMessage());
             }
         }
     }
 
     /**
      * Initialise the thread-local variables.
      * <br>
      * {@inheritDoc}
      */
     @Override
     public void threadStarted() {
         // Disabled thread start if listen on sample choice
         if (isDestinationStatic() || START_ON_SAMPLE) {
             timeout = getTimeoutAsLong();
             interrupted = false;
             exceptionDuringInit = null;
             useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
             stopBetweenSamples = isStopBetweenSamples();
             if (useReceive) {
                 try {
                     initReceiveClient();
                     if (!stopBetweenSamples){ // Don't start yet if stop between samples
                         SUBSCRIBER.start();
                     }
                 } catch (NamingException | JMSException e) {
                     exceptionDuringInit = e;
                 }
             } else {
                 try {
                     initListenerClient();
                     if (!stopBetweenSamples){ // Don't start yet if stop between samples
                         SUBSCRIBER.start();
                     }
                 } catch (JMSException | NamingException e) {
                     exceptionDuringInit = e;
                 }
             }
             if (exceptionDuringInit != null){
                 log.error("Could not initialise client",exceptionDuringInit);
             }
         }
     }
     
     public void threadStarted(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = true; // listen on sample 
         }
         threadStarted();
     }
 
     /**
      * Close subscriber.
      * <br>
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
         if (SUBSCRIBER != null){ // Can be null if init fails
             SUBSCRIBER.close();
         }
     }
     
     public void threadFinished(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = false; // listen on sample
         }
         threadFinished();
     }
 
     /**
      * Handle an interrupt of the test.
      */
     @Override
     public boolean interrupt() {
         boolean oldvalue = interrupted;
         interrupted = true;   // so we break the loops in SampleWithListener and SampleWithReceive
         return !oldvalue;
     }
 
     // ----------- get/set methods ------------------- //
     /**
      * Set the client choice. There are two options: ReceiveSusbscriber and
      * OnMessageSubscriber.
      *
      * @param choice
      *            the client to use. One of {@link JMSSubscriberGui#RECEIVE_RSC
      *            RECEIVE_RSC} or {@link JMSSubscriberGui#ON_MESSAGE_RSC
      *            ON_MESSAGE_RSC}
      */
     public void setClientChoice(String choice) {
         setProperty(CLIENT_CHOICE, choice);
     }
 
     /**
      * Return the client choice.
      *
      * @return the client choice, either {@link JMSSubscriberGui#RECEIVE_RSC
      *         RECEIVE_RSC} or {@link JMSSubscriberGui#ON_MESSAGE_RSC
      *         ON_MESSAGE_RSC}
      */
     public String getClientChoice() {
         String choice = getPropertyAsString(CLIENT_CHOICE);
         // Convert the old test plan entry (which is the language dependent string) to the resource name
         if (choice.equals(RECEIVE_STR)){
             choice = JMSSubscriberGui.RECEIVE_RSC;
         } else if (!choice.equals(JMSSubscriberGui.RECEIVE_RSC)){
             choice = JMSSubscriberGui.ON_MESSAGE_RSC;
         }
         return choice;
     }
 
     public String getTimeout(){
         return getPropertyAsString(TIMEOUT, TIMEOUT_DEFAULT);
     }
 
     public long getTimeoutAsLong(){
         return getPropertyAsLong(TIMEOUT, 0L);
     }
 
     public void setTimeout(String timeout){
         setProperty(TIMEOUT, timeout, TIMEOUT_DEFAULT);        
     }
     
     public String getDurableSubscriptionId(){
         return getPropertyAsString(DURABLE_SUBSCRIPTION_ID);
     }
     
     /**
      * @return JMS Client ID
      */
     public String getClientId() {
         return getPropertyAsString(CLIENT_ID, CLIENT_ID_DEFAULT);
     }
     
     /**
      * @return JMS selector
      */
     public String getJmsSelector() {
         return getPropertyAsString(JMS_SELECTOR, JMS_SELECTOR_DEFAULT);
     }
 
     public void setDurableSubscriptionId(String durableSubscriptionId){
         setProperty(DURABLE_SUBSCRIPTION_ID, durableSubscriptionId, DURABLE_SUBSCRIPTION_ID_DEFAULT);        
     }
 
     /**
      * @param clientId JMS CLient id
      */
     public void setClientID(String clientId) {
         setProperty(CLIENT_ID, clientId, CLIENT_ID_DEFAULT);
     }
    
     /**
      * @param jmsSelector JMS Selector
      */
     public void setJmsSelector(String jmsSelector) {
         setProperty(JMS_SELECTOR, jmsSelector, JMS_SELECTOR_DEFAULT);
     }
 
     /**
      * @return Separator for sampler results
      */
     public String getSeparator() {
         return getPropertyAsString(SEPARATOR, SEPARATOR_DEFAULT);
     }
     
     /**
      * Separator for sampler results
      *
      * @param text
      *            separator to use for sampler results
      */
     public void setSeparator(String text) {
         setProperty(SEPARATOR, text, SEPARATOR_DEFAULT);
     }
     
     // This was the old value that was checked for
     private static final String RECEIVE_STR = JMeterUtils.getResString(JMSSubscriberGui.RECEIVE_RSC); // $NON-NLS-1$
 
     public boolean isStopBetweenSamples() {
         return getPropertyAsBoolean(STOP_BETWEEN, false);
     }
 
     public void setStopBetweenSamples(boolean selected) {
         setProperty(STOP_BETWEEN, selected, false);                
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         InitialContextFactory.close();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
         testStarted("");
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         // NOOP
     }
 
     /**
      * 
      */
     private void setupSeparator() {
         separator = getSeparator();
         separator = separator.replace("\\t", "\t");
         separator = separator.replace("\\n", "\n");
         separator = separator.replace("\\r", "\r");
     }
 
     private Object readResolve(){
         setupSeparator();
         exceptionDuringInit=null;
         return this;
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
index 6559e0911..c24294438 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
@@ -1,393 +1,393 @@
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
 
 package org.apache.jmeter.protocol.smtp.sampler;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import javax.mail.AuthenticationFailedException;
 import javax.mail.BodyPart;
 import javax.mail.Header;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.Multipart;
 import javax.mail.Part;
 import javax.mail.internet.AddressException;
 import javax.mail.internet.ContentType;
 import javax.mail.internet.InternetAddress;
 
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.protocol.smtp.sampler.gui.SecuritySettingsPanel;
 import org.apache.jmeter.protocol.smtp.sampler.protocol.SendMailCommand;
 import org.apache.jmeter.protocol.smtp.sampler.tools.CounterOutputStream;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Sampler-Class for JMeter - builds, starts and interprets the results of the
  * sampler. Has to implement some standard-methods for JMeter in order to be
  * integrated in the framework. All getter/setter methods just deliver/set
  * values from/to the sampler, not from/to the message-object. Therefore, all
  * these methods are also present in class SendMailCommand.
  */
 public class SmtpSampler extends AbstractSampler {
 
     private static final long serialVersionUID = 1L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //+JMX file attribute names - do not change any values!
     public static final String SERVER               = "SMTPSampler.server"; // $NON-NLS-1$
 
 
     public static final String SERVER_PORT          = "SMTPSampler.serverPort"; // $NON-NLS-1$
     public static final String SERVER_TIMEOUT       = "SMTPSampler.serverTimeout"; // $NON-NLS-1$
     public static final String SERVER_CONNECTION_TIMEOUT = "SMTPSampler.serverConnectionTimeout"; // $NON-NLS-1$
     public static final String USE_AUTH             = "SMTPSampler.useAuth"; // $NON-NLS-1$
     public static final String USERNAME             = "SMTPSampler.username"; // $NON-NLS-1$
     public static final String PASSWORD             = "SMTPSampler.password"; // $NON-NLS-1$
     public static final String MAIL_FROM            = "SMTPSampler.mailFrom"; // $NON-NLS-1$
     public static final String MAIL_REPLYTO         = "SMTPSampler.replyTo"; // $NON-NLS-1$
     public static final String RECEIVER_TO          = "SMTPSampler.receiverTo"; // $NON-NLS-1$
     public static final String RECEIVER_CC          = "SMTPSampler.receiverCC"; // $NON-NLS-1$
     public static final String RECEIVER_BCC         = "SMTPSampler.receiverBCC"; // $NON-NLS-1$
 
     public static final String SUBJECT              = "SMTPSampler.subject"; // $NON-NLS-1$
     public static final String SUPPRESS_SUBJECT     = "SMTPSampler.suppressSubject"; // $NON-NLS-1$
     public static final String MESSAGE              = "SMTPSampler.message"; // $NON-NLS-1$
     public static final String PLAIN_BODY           = "SMTPSampler.plainBody"; // $NON-NLS-1$
     public static final String INCLUDE_TIMESTAMP    = "SMTPSampler.include_timestamp"; // $NON-NLS-1$
     public static final String ATTACH_FILE          = "SMTPSampler.attachFile"; // $NON-NLS-1$
     public static final String MESSAGE_SIZE_STATS   = "SMTPSampler.messageSizeStatistics"; // $NON-NLS-1$
     public static final String HEADER_FIELDS        = "SMTPSampler.headerFields"; // $NON-NLS-1$
 
     public static final String USE_EML              = "SMTPSampler.use_eml"; // $NON-NLS-1$
     public static final String EML_MESSAGE_TO_SEND  = "SMTPSampler.emlMessageToSend"; // $NON-NLS-1$
     public static final String ENABLE_DEBUG         = "SMTPSampler.enableDebug"; // $NON-NLS-1$
 
     // Used to separate attachment file names in JMX fields - do not change!
     public static final String FILENAME_SEPARATOR = ";";
     //-JMX file attribute names
 
 
     public SmtpSampler() {
     }
 
     /**
      * Performs the sample, and returns the result
      *
      * @param e
      *            Standard-method-header from JMeter
      * @return sampleresult Result of the sample
      * @see org.apache.jmeter.samplers.Sampler#sample(org.apache.jmeter.samplers.Entry)
      */
     @Override
     public SampleResult sample(Entry e) {
         Message message = null;
         SampleResult res = new SampleResult();
         res.setSampleLabel(getName());
         boolean isOK = false; // Did sample succeed?
         SendMailCommand instance = new SendMailCommand();
         instance.setSmtpServer(getPropertyAsString(SmtpSampler.SERVER));
         instance.setSmtpPort(getPropertyAsString(SmtpSampler.SERVER_PORT));
         instance.setConnectionTimeOut(getPropertyAsString(SmtpSampler.SERVER_CONNECTION_TIMEOUT));
         instance.setTimeOut(getPropertyAsString(SmtpSampler.SERVER_TIMEOUT));
 
         instance.setUseSSL(getPropertyAsBoolean(SecuritySettingsPanel.USE_SSL));
         instance.setUseStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.USE_STARTTLS));
         instance.setTrustAllCerts(getPropertyAsBoolean(SecuritySettingsPanel.SSL_TRUST_ALL_CERTS));
         instance.setEnforceStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.ENFORCE_STARTTLS));
 
         instance.setUseAuthentication(getPropertyAsBoolean(USE_AUTH));
         instance.setUsername(getPropertyAsString(USERNAME));
         instance.setPassword(getPropertyAsString(PASSWORD));
 
         instance.setUseLocalTrustStore(getPropertyAsBoolean(SecuritySettingsPanel.USE_LOCAL_TRUSTSTORE));
         instance.setTrustStoreToUse(getPropertyAsString(SecuritySettingsPanel.TRUSTSTORE_TO_USE));
         instance.setEmlMessage(getPropertyAsString(EML_MESSAGE_TO_SEND));
         instance.setUseEmlMessage(getPropertyAsBoolean(USE_EML));
 
         instance.setEnableDebug(getPropertyAsBoolean(ENABLE_DEBUG));
 
         if (getPropertyAsString(MAIL_FROM).matches(".*@.*")) {
             instance.setSender(getPropertyAsString(MAIL_FROM));
         }
 
         final String receiverTo = getPropertyAsString(SmtpSampler.RECEIVER_TO).trim();
         final String receiverCC = getPropertyAsString(SmtpSampler.RECEIVER_CC).trim();
         final String receiverBcc = getPropertyAsString(SmtpSampler.RECEIVER_BCC).trim();
         final String replyTo = getPropertyAsString(SmtpSampler.MAIL_REPLYTO).trim();
 
         try {
             // Process address lists
             instance.setReceiverTo(getPropNameAsAddresses(receiverTo));
             instance.setReceiverCC(getPropNameAsAddresses(receiverCC));
             instance.setReceiverBCC(getPropNameAsAddresses(receiverBcc));
             instance.setReplyTo(getPropNameAsAddresses(replyTo));
 
             if(getPropertyAsBoolean(SUPPRESS_SUBJECT)){
                 instance.setSubject(null);
             }else{
                 String subject = getPropertyAsString(SUBJECT);
                 if (getPropertyAsBoolean(INCLUDE_TIMESTAMP)){
                     StringBuilder sb = new StringBuilder(subject);
                     sb.append(" <<< current timestamp: ");
                     sb.append(new Date().getTime());
                     sb.append(" >>>");
                     subject = sb.toString();
                 }
                 instance.setSubject(subject);
             }
 
             if (!getPropertyAsBoolean(USE_EML)) { // part is only needed if we
                 // don't send an .eml-file
                 instance.setMailBody(getPropertyAsString(MESSAGE));
                 instance.setPlainBody(getPropertyAsBoolean(PLAIN_BODY));
                 final String filesToAttach = getPropertyAsString(ATTACH_FILE);
                 if (!filesToAttach.isEmpty()) {
                     String[] attachments = filesToAttach.split(FILENAME_SEPARATOR);
                     for (String attachment : attachments) {
                         File file = new File(attachment);
                         if(!file.isAbsolute() && !file.exists()){
                             log.debug("loading file with relative path: " +attachment);
                             file = new File(FileServer.getFileServer().getBaseDir(), attachment);
                             log.debug("file path set to: "+attachment);
                         }
                         instance.addAttachment(file);
                     }
                 }
 
             }
 
             // needed for measuring sending time
             instance.setSynchronousMode(true);
 
             instance.setHeaderFields((CollectionProperty)getProperty(SmtpSampler.HEADER_FIELDS));
             message = instance.prepareMessage();
 
             if (getPropertyAsBoolean(MESSAGE_SIZE_STATS)) {
                 // calculate message size
                 CounterOutputStream cs = new CounterOutputStream();
                 message.writeTo(cs);
                 res.setBytes(cs.getCount());
             } else {
-                res.setBytes(-1);
+                res.setBytes(-1L);
             }
 
         } catch (Exception ex) {
             log.warn("Error while preparing message", ex);
             res.setResponseCode("500");
             res.setResponseMessage(ex.toString());
             return res;
         }
 
         // Set up the sample result details
         res.setDataType(SampleResult.TEXT);
         try {
             res.setRequestHeaders(getRequestHeaders(message));
             res.setSamplerData(getSamplerData(message));
         } catch (MessagingException | IOException e1) {
             res.setSamplerData("Error occurred trying to save request info: "+e1);
             log.warn("Error occurred trying to save request info",e1);
         }
 
         // Perform the sampling
         res.sampleStart();
 
         try {
             instance.execute(message);
 
             res.setResponseCodeOK();
             /*
              * TODO if(instance.getSMTPStatusCode == 250)
              * res.setResponseMessage("Message successfully sent!"); else
              * res.setResponseMessage(instance.getSMTPStatusCodeIncludingMessage);
              */
             res.setResponseMessage("Message successfully sent!\n"
                     + instance.getServerResponse());
             isOK = true;
         }
         // username / password incorrect
         catch (AuthenticationFailedException afex) {
             log.warn("", afex);
             res.setResponseCode("500");
             res.setResponseMessage("AuthenticationFailedException: authentication failed - wrong username / password!\n"
                             + afex);
         // SSL not supported, startTLS not supported, other messagingException
         } catch (MessagingException mex) {
             log.warn("",mex);
             res.setResponseCode("500");
             if (mex.getMessage().matches(".*Could not connect to SMTP host.*465.*")
                     && mex.getCause().getMessage().matches(".*Connection timed out.*")) {
                 res.setResponseMessage("MessagingException: Probably, SSL is not supported by the SMTP-Server!\n"
                                 + mex);
             } else if (mex.getMessage().matches(".*StartTLS failed.*")) {
                 res.setResponseMessage("MessagingException: StartTLS not supported by server or initializing failed!\n"
                                 + mex);
             } else if (mex.getMessage().matches(".*send command to.*")
                     && mex.getCause().getMessage().matches(
                                     ".*unable to find valid certification path to requested target.*")) {
                 res.setResponseMessage("MessagingException: Server certificate not trusted - perhaps you have to restart JMeter!\n"
                                 + mex);
             } else {
                 res.setResponseMessage("Other MessagingException: " + mex.toString());
             }
         }  catch (Exception ex) {   // general exception
             log.warn("",ex);
             res.setResponseCode("500");
             if (null != ex.getMessage()
                     && ex.getMessage().matches("Failed to build truststore")) {
                 res.setResponseMessage("Failed to build truststore - did not try to send mail!");
             } else {
                 res.setResponseMessage("Other Exception: " + ex.toString());
             }
         }
 
         res.sampleEnd();
 
         try {
             // process the sampler result
             InputStream is = message.getInputStream();
             StringBuilder sb = new StringBuilder();
             byte[] buf = new byte[1024];
             int read = is.read(buf);
             while (read > 0) {
                 sb.append(new String(buf, 0, read));  // TODO - charset?
                 read = is.read(buf);
             }
             // TODO - charset?
             res.setResponseData(sb.toString().getBytes()); // TODO this should really be request data, but there is none
         } catch (IOException | MessagingException ex) {
             log.warn("",ex);
         }
 
         res.setSuccessful(isOK);
 
         return res;
     }
 
     private String getRequestHeaders(Message message) throws MessagingException {
         StringBuilder sb = new StringBuilder();
         @SuppressWarnings("unchecked") // getAllHeaders() is not yet genericised
         Enumeration<Header> headers = message.getAllHeaders(); // throws ME
         writeHeaders(headers, sb);
         return sb.toString();
     }
 
     private String getSamplerData(Message message) throws MessagingException, IOException {
         StringBuilder sb = new StringBuilder();
         Object content = message.getContent(); // throws ME
         if (content instanceof Multipart) {
             Multipart multipart = (Multipart) content;
             String contentType = multipart.getContentType();
             ContentType ct = new ContentType(contentType);
             String boundary=ct.getParameter("boundary");
             for (int i = 0; i < multipart.getCount(); i++) { // throws ME
                 sb.append("--");
                 sb.append(boundary);
                 sb.append("\n");
                 BodyPart bodyPart = multipart.getBodyPart(i); // throws ME
                 writeBodyPart(sb, bodyPart); // throws IOE, ME
             }
             sb.append("--");
             sb.append(boundary);
             sb.append("--");
             sb.append("\n");
         } else if(content instanceof BodyPart){
             BodyPart bodyPart = (BodyPart) content;
             writeBodyPart(sb, bodyPart); // throws IOE, ME
         } else if (content instanceof String){
             sb.append(content);
         } else {
             sb.append("Content has class: "+content.getClass().getCanonicalName());
         }
         return sb.toString();
     }
 
     private void writeHeaders(Enumeration<Header> headers, StringBuilder sb) {
         while (headers.hasMoreElements()) {
             Header header = headers.nextElement();
             sb.append(header.getName());
             sb.append(": ");
             sb.append(header.getValue());
             sb.append("\n");
         }
     }
 
     private void writeBodyPart(StringBuilder sb, BodyPart bodyPart)
             throws MessagingException, IOException {
         @SuppressWarnings("unchecked") // API not yet generic
         Enumeration<Header> allHeaders = bodyPart.getAllHeaders(); // throws ME
         writeHeaders(allHeaders, sb);
         String disposition = bodyPart.getDisposition(); // throws ME
         sb.append("\n");
         if (Part.ATTACHMENT.equals(disposition)) {
             sb.append("<attachment content not shown>");
         } else {
             sb.append(bodyPart.getContent()); // throws IOE, ME
         }
         sb.append("\n");
     }
 
     /**
      * Get the list of addresses or null.
      * Null is treated differently from an empty list.
      * @param propValue addresses separated by ";"
      * @return the list or null if the input was the empty string
      * @throws AddressException
      */
     private List<InternetAddress> getPropNameAsAddresses(String propValue) throws AddressException{
         if (propValue.length() > 0){ // we have at least one potential address
             List<InternetAddress> addresses = new ArrayList<>();
             for (String address : propValue.split(";")){
                 addresses.add(new InternetAddress(address.trim()));
             }
             return addresses;
         } else {
             return null;
         }
     }
     
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/tools/CounterOutputStream.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/tools/CounterOutputStream.java
index 62febe369..29930d3fa 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/tools/CounterOutputStream.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/tools/CounterOutputStream.java
@@ -1,64 +1,64 @@
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
 
 package org.apache.jmeter.protocol.smtp.sampler.tools;
 
 import java.io.OutputStream;
 
 /**
  * Utility-class to calculate message size.
  */
 public class CounterOutputStream extends OutputStream {
-    int count = 0;
+    private long count = 0;
 
     /**
      * {@inheritDoc}
      */
     @Override
 
     public void close() {}
     /**
      * {@inheritDoc}
      */
     @Override
     public void flush() {}
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void write(byte[] b, int off, int len) {
         count += len;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void write(int b) {
         count++;
     }
 
     /**
      * Returns message size
      * @return Message size
      */
-    public int getCount() {
+    public long getCount() {
         return count;
     }
 }
