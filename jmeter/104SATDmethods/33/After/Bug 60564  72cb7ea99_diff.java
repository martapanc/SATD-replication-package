diff --git a/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java b/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
index f7cc5662d..dd5d232c8 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
@@ -1,610 +1,610 @@
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
 package org.apache.jmeter.protocol.mail.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 
 import javax.mail.Address;
 import javax.mail.BodyPart;
 import javax.mail.Flags;
 import javax.mail.Folder;
 import javax.mail.Header;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.Session;
 import javax.mail.Store;
 import javax.mail.internet.MimeMultipart;
 import javax.mail.internet.MimeUtility;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.protocol.smtp.sampler.gui.SecuritySettingsPanel;
 import org.apache.jmeter.protocol.smtp.sampler.protocol.LocalTrustStoreSSLSocketFactory;
 import org.apache.jmeter.protocol.smtp.sampler.protocol.TrustAllSSLSocketFactory;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Sampler that can read from POP3 and IMAP mail servers
  */
 public class MailReaderSampler extends AbstractSampler implements Interruptible {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MailReaderSampler.class);
 
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));
 
     //+ JMX attributes - do not change the values
     private static final String SERVER_TYPE = "host_type"; // $NON-NLS-1$
     private static final String SERVER = "host"; // $NON-NLS-1$
     private static final String PORT = "port"; // $NON-NLS-1$
     private static final String USERNAME = "username"; // $NON-NLS-1$
     private static final String PASSWORD = "password"; // $NON-NLS-1$ NOSONAR not a hardcoded password
     private static final String FOLDER = "folder"; // $NON-NLS-1$
     private static final String DELETE = "delete"; // $NON-NLS-1$
     private static final String NUM_MESSAGES = "num_messages"; // $NON-NLS-1$
     private static final String NEW_LINE = "\n"; // $NON-NLS-1$
     private static final String STORE_MIME_MESSAGE = "storeMimeMessage"; // $NON-NLS-1$
     private static final String HEADER_ONLY = "headerOnly"; // $NON-NLS-1$
     private static final boolean HEADER_ONLY_DEFAULT = false;
     //-
 
     private static final String RFC_822_DEFAULT_ENCODING = "iso-8859-1"; // RFC 822 uses ascii per default
 
     public static final String DEFAULT_PROTOCOL = "pop3";  // $NON-NLS-1$
 
     // Use the actual class so the name must be correct.
     private static final String TRUST_ALL_SOCKET_FACTORY = TrustAllSSLSocketFactory.class.getName();
 
     private static final String FALSE = "false";  // $NON-NLS-1$
 
     private static final String TRUE = "true";  // $NON-NLS-1$
 
     public boolean isUseLocalTrustStore() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_LOCAL_TRUSTSTORE);
     }
 
     public String getTrustStoreToUse() {
         return getPropertyAsString(SecuritySettingsPanel.TRUSTSTORE_TO_USE);
     }
 
 
     public boolean isUseSSL() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_SSL);
     }
 
 
     public boolean isUseStartTLS() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_STARTTLS);
     }
 
 
     public boolean isTrustAllCerts() {
         return getPropertyAsBoolean(SecuritySettingsPanel.SSL_TRUST_ALL_CERTS);
     }
 
 
     public boolean isEnforceStartTLS() {
         return getPropertyAsBoolean(SecuritySettingsPanel.ENFORCE_STARTTLS);
 
     }
 
     public static final int ALL_MESSAGES = -1; // special value
 
     private volatile boolean busy;
 
     public MailReaderSampler() {
         setServerType(DEFAULT_PROTOCOL);
         setFolder("INBOX");  // $NON-NLS-1$
         setNumMessages(ALL_MESSAGES);
         setDeleteMessages(false);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult sample(Entry e) {
         SampleResult parent = new SampleResult();
         boolean isOK = false; // Did sample succeed?
         final boolean deleteMessages = getDeleteMessages();
         final String serverProtocol = getServerType();
 
         parent.setSampleLabel(getName());
 
         String samplerString = toString();
         parent.setSamplerData(samplerString);
 
         /*
          * Perform the sampling
          */
         parent.sampleStart(); // Start timing
         try {
             // Create empty properties
             Properties props = new Properties();
 
             if (isUseStartTLS()) {
                 props.setProperty(mailProp(serverProtocol, "starttls.enable"), TRUE);  // $NON-NLS-1$
                 if (isEnforceStartTLS()){
                     // Requires JavaMail 1.4.2+
                     props.setProperty(mailProp(serverProtocol, "starttls.require"), TRUE);  // $NON-NLS-1$
                 }
             }
 
             if (isTrustAllCerts()) {
                 if (isUseSSL()) {
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 } else if (isUseStartTLS()) {
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 }
             } else if (isUseLocalTrustStore()){
                 File truststore = new File(getTrustStoreToUse());
                 log.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
                 if(!truststore.exists()){
                     log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
                     truststore = new File(FileServer.getFileServer().getBaseDir(), getTrustStoreToUse());
                     log.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
                     if(!truststore.exists()){
                         log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
                         throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
                     }
                 }
                 if (isUseSSL()) {
                     // Requires JavaMail 1.4.2+
                     props.put(mailProp(serverProtocol, "ssl.socketFactory"),   // $NON-NLS-1$ 
                             new LocalTrustStoreSSLSocketFactory(truststore));
                     props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 } else if (isUseStartTLS()) {
                     // Requires JavaMail 1.4.2+
                     props.put(mailProp(serverProtocol, "ssl.socketFactory"),  // $NON-NLS-1$
                             new LocalTrustStoreSSLSocketFactory(truststore));
                     props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 }
             }
 
             // Get session
             Session session = Session.getInstance(props, null);
 
             // Get the store
             Store store = session.getStore(serverProtocol);
             store.connect(getServer(), getPortAsInt(), getUserName(), getPassword());
 
             // Get folder
             Folder folder = store.getFolder(getFolder());
             if (deleteMessages) {
                 folder.open(Folder.READ_WRITE);
             } else {
                 folder.open(Folder.READ_ONLY);
             }
 
             final int messageTotal = folder.getMessageCount();
             int n = getNumMessages();
             if (n == ALL_MESSAGES || n > messageTotal) {
                 n = messageTotal;
             }
 
             // Get directory
             Message[] messages = folder.getMessages(1,n);
             StringBuilder pdata = new StringBuilder();
             pdata.append(messages.length);
             pdata.append(" messages found\n");
             parent.setResponseData(pdata.toString(),null);
             parent.setDataType(SampleResult.TEXT);
             parent.setContentType("text/plain"); // $NON-NLS-1$
 
             final boolean headerOnly = getHeaderOnly();
             busy = true;
             for (Message message : messages) {
                 StringBuilder cdata = new StringBuilder();
                 SampleResult child = new SampleResult();
                 child.sampleStart();
 
                 cdata.append("Message "); // $NON-NLS-1$
                 cdata.append(message.getMessageNumber());
                 child.setSampleLabel(cdata.toString());
                 child.setSamplerData(cdata.toString());
                 cdata.setLength(0);
 
                 final String contentType = message.getContentType();
                 child.setContentType(contentType);// Store the content-type
                 child.setDataEncoding(RFC_822_DEFAULT_ENCODING); // RFC 822 uses ascii per default
                 child.setEncodingAndType(contentType);// Parse the content-type
 
                 if (isStoreMimeMessage()) {
                     // Don't save headers - they are already in the raw message
                     ByteArrayOutputStream bout = new ByteArrayOutputStream();
                     message.writeTo(bout);
                     child.setResponseData(bout.toByteArray()); // Save raw message
                     child.setDataType(SampleResult.TEXT);
                 } else {
                     @SuppressWarnings("unchecked") // Javadoc for the API says this is OK
                     Enumeration<Header> hdrs = message.getAllHeaders();
                     while(hdrs.hasMoreElements()){
                         Header hdr = hdrs.nextElement();
                         String value = hdr.getValue();
                         try {
                             value = MimeUtility.decodeText(value);
                         } catch (UnsupportedEncodingException uce) {
                             // ignored
                         }
                         cdata.append(hdr.getName()).append(": ").append(value).append("\n");
                     }
                     child.setResponseHeaders(cdata.toString());
                     cdata.setLength(0);
                     if (!headerOnly) {
                         appendMessageData(child, message);
                     }
                 }
 
                 if (deleteMessages) {
                     message.setFlag(Flags.Flag.DELETED, true);
                 }
                 child.setResponseOK();
                 if (child.getEndTime()==0){// Avoid double-call if addSubResult was called.
                     child.sampleEnd();
                 }
                 parent.addSubResult(child);
             }
 
             // Close connection
             folder.close(true);
             store.close();
 
             parent.setResponseCodeOK();
             parent.setResponseMessageOK();
             isOK = true;
         } catch (NoClassDefFoundError | IOException ex) {
             log.debug("",ex);// No need to log normally, as we set the status
             parent.setResponseCode("500"); // $NON-NLS-1$
             parent.setResponseMessage(ex.toString());
         } catch (MessagingException ex) {
             log.debug("", ex);// No need to log normally, as we set the status
             parent.setResponseCode("500"); // $NON-NLS-1$
             parent.setResponseMessage(ex.toString() + "\n" + samplerString); // $NON-NLS-1$
         } finally {
             busy = false;
         }
 
         if (parent.getEndTime()==0){// not been set by any child samples
             parent.sampleEnd();
         }
         parent.setSuccessful(isOK);
         return parent;
     }
 
     private void appendMessageData(SampleResult child, Message message)
             throws MessagingException, IOException {
         StringBuilder cdata = new StringBuilder();
         cdata.append("Date: "); // $NON-NLS-1$
         cdata.append(message.getSentDate());// TODO - use a different format here?
         cdata.append(NEW_LINE);
 
         cdata.append("To: "); // $NON-NLS-1$
         Address[] recips = message.getAllRecipients(); // may be null
         for (int j = 0; recips != null && j < recips.length; j++) {
             cdata.append(recips[j].toString());
             if (j < recips.length - 1) {
                 cdata.append("; "); // $NON-NLS-1$
             }
         }
         cdata.append(NEW_LINE);
 
         cdata.append("From: "); // $NON-NLS-1$
         Address[] from = message.getFrom(); // may be null
         for (int j = 0; from != null && j < from.length; j++) {
             cdata.append(from[j].toString());
             if (j < from.length - 1) {
                 cdata.append("; "); // $NON-NLS-1$
             }
         }
         cdata.append(NEW_LINE);
 
         cdata.append("Subject: "); // $NON-NLS-1$
         cdata.append(message.getSubject());
         cdata.append(NEW_LINE);
 
         cdata.append(NEW_LINE);
         Object content = message.getContent();
         if (content instanceof MimeMultipart) {
             appendMultiPart(child, cdata, (MimeMultipart) content);
         } else if (content instanceof InputStream){
             child.setResponseData(IOUtils.toByteArray((InputStream) content));
         } else {
             cdata.append(content);
             child.setResponseData(cdata.toString(),child.getDataEncodingNoDefault());
         }
     }
 
     private void appendMultiPart(SampleResult child, StringBuilder cdata,
             MimeMultipart mmp) throws MessagingException, IOException {
         String preamble = mmp.getPreamble();
         if (preamble != null ){
             cdata.append(preamble);
         }
         child.setResponseData(cdata.toString(),child.getDataEncodingNoDefault());
         int count = mmp.getCount();
         for (int j=0; j<count;j++){
             BodyPart bodyPart = mmp.getBodyPart(j);
             final Object bodyPartContent = bodyPart.getContent();
             final String contentType = bodyPart.getContentType();
             SampleResult sr = new SampleResult();
             sr.setSampleLabel("Part: "+j);
             sr.setContentType(contentType);
             sr.setDataEncoding(RFC_822_DEFAULT_ENCODING);
             sr.setEncodingAndType(contentType);
             sr.sampleStart();
             if (bodyPartContent instanceof InputStream){
                 sr.setResponseData(IOUtils.toByteArray((InputStream) bodyPartContent));
             } else if (bodyPartContent instanceof MimeMultipart){
                 appendMultiPart(sr, cdata, (MimeMultipart) bodyPartContent);
             } else {
                 sr.setResponseData(bodyPartContent.toString(),sr.getDataEncodingNoDefault());
             }
             sr.setResponseOK();
             if (sr.getEndTime()==0){// not been set by any child samples
                 sr.sampleEnd();
             }
             child.addSubResult(sr);
         }
     }
 
     /**
      * Sets the type of protocol to use when talking with the remote mail
      * server. Either MailReaderSampler.TYPE_IMAP[S] or
      * MailReaderSampler.TYPE_POP3[S]. Default is MailReaderSampler.TYPE_POP3.
      *
      * @param serverType protocol to use
      */
     public void setServerType(String serverType) {
         setProperty(SERVER_TYPE, serverType);
     }
 
     /**
      * Returns the type of the protocol set to use when talking with the remote
      * server. Either MailReaderSampler.TYPE_IMAP[S] or
      * MailReaderSampler.TYPE_POP3[S].
      *
      * @return Server Type
      */
     public String getServerType() {
         return getPropertyAsString(SERVER_TYPE);
     }
 
     /**
      * @param server -
      *            The name or address of the remote server.
      */
     public void setServer(String server) {
         setProperty(SERVER, server);
     }
 
     /**
      * @return The name or address of the remote server.
      */
     public String getServer() {
         return getPropertyAsString(SERVER);
     }
 
     public String getPort() {
         return getPropertyAsString(PORT);
     }
 
     private int getPortAsInt() {
         return getPropertyAsInt(PORT, -1);
     }
 
     public void setPort(String port) {
         setProperty(PORT, port, "");
     }
 
     /**
      * @param username -
      *            The username of the mail account.
      */
     public void setUserName(String username) {
         setProperty(USERNAME, username);
     }
 
     /**
      * @return The username of the mail account.
      */
     public String getUserName() {
         return getPropertyAsString(USERNAME);
     }
 
     /**
      * @param password the password to use
      */
     public void setPassword(String password) {
         setProperty(PASSWORD, password);
     }
 
     /**
      * @return password
      */
     public String getPassword() {
         return getPropertyAsString(PASSWORD);
     }
 
     /**
      * @param folder -
      *            Name of the folder to read emails from. "INBOX" is the only
      *            acceptable value if the server type is POP3.
      */
     public void setFolder(String folder) {
         setProperty(FOLDER, folder);
     }
 
     /**
      * @return folder
      */
     public String getFolder() {
         return getPropertyAsString(FOLDER);
     }
 
     /**
      * @param numMessages -
      *            The number of messages to retrieve from the mail server. Set
      *            this value to -1 to retrieve all messages.
      */
     public void setNumMessages(int numMessages) {
         setProperty(new IntegerProperty(NUM_MESSAGES, numMessages));
     }
 
     /**
      * @param numMessages -
      *            The number of messages to retrieve from the mail server. Set
      *            this value to -1 to retrieve all messages.
      */
     public void setNumMessages(String numMessages) {
         setProperty(new StringProperty(NUM_MESSAGES, numMessages));
     }
 
     /**
      * @return The number of messages to retrieve from the mail server.
      *         -1 denotes get all messages.
      */
     public int getNumMessages() {
         return getPropertyAsInt(NUM_MESSAGES);
     }
 
     /**
      * @return The number of messages to retrieve from the mail server.
      *         -1 denotes get all messages.
      */
     public String getNumMessagesString() {
         return getPropertyAsString(NUM_MESSAGES);
     }
 
     /**
      * @param delete -
      *            Whether or not to delete the read messages from the folder.
      */
     public void setDeleteMessages(boolean delete) {
         setProperty(new BooleanProperty(DELETE, delete));
     }
 
     /**
      * @return Whether or not to delete the read messages from the folder.
      */
     public boolean getDeleteMessages() {
         return getPropertyAsBoolean(DELETE);
     }
 
     /**
      * @return Whether or not to store the retrieved message as MIME message in
      *         the sample result
      */
     public boolean isStoreMimeMessage() {
         return getPropertyAsBoolean(STORE_MIME_MESSAGE, false);
     }
 
     /**
      * @param storeMimeMessage
      *            Whether or not to store the retrieved message as MIME message in the
      *            sample result
      */
     public void setStoreMimeMessage(boolean storeMimeMessage) {
         setProperty(STORE_MIME_MESSAGE, storeMimeMessage, false);
     }
 
     @Override
     public String toString(){
         StringBuilder sb = new StringBuilder();
         sb.append(getServerType());
         sb.append("://");
         String name = getUserName();
         if (name.length() > 0){
             sb.append(name);
             sb.append("@");
         }
         sb.append(getServer());
         int port=getPortAsInt();
         if (port != -1){
             sb.append(":").append(port);
         }
         sb.append("/");
         sb.append(getFolder());
         sb.append("[");
         sb.append(getNumMessages());
         sb.append("]");
         return sb.toString();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean interrupt() {
         boolean wasbusy = busy;
         busy = false;
         return wasbusy;
     }
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 
     public boolean getHeaderOnly() {
         return getPropertyAsBoolean(HEADER_ONLY, HEADER_ONLY_DEFAULT);
     }
 
     public void setHeaderOnly(boolean selected) {
         setProperty(HEADER_ONLY, selected, HEADER_ONLY_DEFAULT);
     }
 
     /**
      * Build a property name of the form "mail.pop3s.starttls.require"
      *
      * @param protocol the protocol, i.e. "pop3s" in the example
      * @param propname the property name suffix, i.e. "starttls.require" in the example
      * @return the constructed name
      */
     private String mailProp(String protocol, String propname) {
         StringBuilder sb = new StringBuilder();
         sb.append("mail.").append(protocol).append(".");
         sb.append(propname);
         return sb.toString();
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
index fa7ab0f16..33872f2da 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
@@ -1,382 +1,382 @@
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
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.stream.Collectors;
 
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
 
 import org.apache.commons.io.IOUtils;
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
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
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SmtpSampler.class);
 
     //+JMX file attribute names - do not change any values!
     public static final String SERVER               = "SMTPSampler.server"; // $NON-NLS-1$
     public static final String SERVER_PORT          = "SMTPSampler.serverPort"; // $NON-NLS-1$
     public static final String SERVER_TIMEOUT       = "SMTPSampler.serverTimeout"; // $NON-NLS-1$
     public static final String SERVER_CONNECTION_TIMEOUT = "SMTPSampler.serverConnectionTimeout"; // $NON-NLS-1$
     public static final String USE_AUTH             = "SMTPSampler.useAuth"; // $NON-NLS-1$
     public static final String USERNAME             = "SMTPSampler.username"; // $NON-NLS-1$
     public static final String PASSWORD             = "SMTPSampler.password"; // $NON-NLS-1$ NOSONAR not a hardcoded password
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
      * @param e Standard-method-header from JMeter
      * @return Result of the sample
      * @see org.apache.jmeter.samplers.Sampler#sample(org.apache.jmeter.samplers.Entry)
      */
     @Override
     public SampleResult sample(Entry e) {
         SendMailCommand sendMailCmd;
         Message message;
         SampleResult result = createSampleResult();
 
         try {
             sendMailCmd = createSendMailCommandFromProperties();
             message = sendMailCmd.prepareMessage();
             result.setBytes(calculateMessageSize(message));
         } catch (Exception ex) {
             log.warn("Error while preparing message", ex);
             result.setResponseCode("500");
             result.setResponseMessage(ex.toString());
             return result;
         }
 
         // Set up the sample result details
         result.setDataType(SampleResult.TEXT);
         try {
             result.setRequestHeaders(getRequestHeaders(message));
             result.setSamplerData(getSamplerData(message));
         } catch (MessagingException | IOException ex) {
             result.setSamplerData("Error occurred trying to save request info: " + ex);
             log.warn("Error occurred trying to save request info", ex);
         }
 
         // Perform the sampling
         result.sampleStart();
         boolean isSuccessful = executeMessage(result, sendMailCmd, message);
         result.sampleEnd();
 
         try {
             result.setResponseData(processSampler(message));
         } catch (IOException | MessagingException ex) {
             log.warn("Failed to set result response data", ex);
         }
 
         result.setSuccessful(isSuccessful);
 
         return result;
     }
 
     private SampleResult createSampleResult() {
         SampleResult result = new SampleResult();
         result.setSampleLabel(getName());
         return result;
     }
 
     private boolean executeMessage(SampleResult result, SendMailCommand sendMailCmd, Message message) {
         boolean didSampleSucceed = false;
         try {
             sendMailCmd.execute(message);
             result.setResponseCodeOK();
             result.setResponseMessage(
                     "Message successfully sent!\n" + sendMailCmd.getServerResponse());
             didSampleSucceed = true;
         } catch (AuthenticationFailedException afex) {
             log.warn("", afex);
             result.setResponseCode("500");
             result.setResponseMessage(
                     "AuthenticationFailedException: authentication failed - wrong username / password!\n"
                             + afex);
         } catch (Exception ex) {
             log.warn("", ex);
             result.setResponseCode("500");
             result.setResponseMessage(ex.getMessage());
         }
         return didSampleSucceed;
     }
 
     private long calculateMessageSize(Message message) throws IOException, MessagingException {
         if (getPropertyAsBoolean(MESSAGE_SIZE_STATS)) {
             // calculate message size
             CounterOutputStream cs = new CounterOutputStream();
             message.writeTo(cs);
             return cs.getCount();
         } else {
             return -1L;
         }
     }
 
     private byte[] processSampler(Message message) throws IOException, MessagingException {
         // process the sampler result
         try (InputStream is = message.getInputStream()) {
             return IOUtils.toByteArray(is);
         }        
     }
 
     private List<File> getAttachmentFiles() {
         final String[] attachments = getPropertyAsString(ATTACH_FILE).split(FILENAME_SEPARATOR);
         return Arrays.stream(attachments) // NOSONAR No need to close
                 .map(this::attachmentToFile)
                 .collect(Collectors.toList());
     }
 
     private File attachmentToFile(String attachment) { // NOSONAR False positive saying not used
         File file = new File(attachment);
         if (!file.isAbsolute() && !file.exists()) {
             if(log.isDebugEnabled()) {
                 log.debug("loading file with relative path: " + attachment);
             }
             file = new File(FileServer.getFileServer().getBaseDir(), attachment);
             if(log.isDebugEnabled()) {
                 log.debug("file path set to: " + attachment);
             }
         }
         return file;
     }
 
     private String calculateSubject() {
         if (getPropertyAsBoolean(SUPPRESS_SUBJECT)) {
             return null;
         } else {
             String subject = getPropertyAsString(SUBJECT);
             if (getPropertyAsBoolean(INCLUDE_TIMESTAMP)) {
                 subject = subject
                         + " <<< current timestamp: "
                         + System.currentTimeMillis()
                         + " >>>";
             }
             return subject;
         }
     }
 
     private SendMailCommand createSendMailCommandFromProperties() throws AddressException {
         SendMailCommand sendMailCmd = new SendMailCommand();
         sendMailCmd.setSmtpServer(getPropertyAsString(SmtpSampler.SERVER));
         sendMailCmd.setSmtpPort(getPropertyAsString(SmtpSampler.SERVER_PORT));
         sendMailCmd.setConnectionTimeOut(getPropertyAsString(SmtpSampler.SERVER_CONNECTION_TIMEOUT));
         sendMailCmd.setTimeOut(getPropertyAsString(SmtpSampler.SERVER_TIMEOUT));
 
         sendMailCmd.setUseSSL(getPropertyAsBoolean(SecuritySettingsPanel.USE_SSL));
         sendMailCmd.setUseStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.USE_STARTTLS));
         sendMailCmd.setTrustAllCerts(getPropertyAsBoolean(SecuritySettingsPanel.SSL_TRUST_ALL_CERTS));
         sendMailCmd.setEnforceStartTLS(getPropertyAsBoolean(SecuritySettingsPanel.ENFORCE_STARTTLS));
 
         sendMailCmd.setUseAuthentication(getPropertyAsBoolean(USE_AUTH));
         sendMailCmd.setUsername(getPropertyAsString(USERNAME));
         sendMailCmd.setPassword(getPropertyAsString(PASSWORD));
 
         sendMailCmd.setUseLocalTrustStore(getPropertyAsBoolean(SecuritySettingsPanel.USE_LOCAL_TRUSTSTORE));
         sendMailCmd.setTrustStoreToUse(getPropertyAsString(SecuritySettingsPanel.TRUSTSTORE_TO_USE));
 
         sendMailCmd.setEmlMessage(getPropertyAsString(EML_MESSAGE_TO_SEND));
         sendMailCmd.setUseEmlMessage(getPropertyAsBoolean(USE_EML));
         if (!getPropertyAsBoolean(USE_EML)) {
             // if we are not sending an .eml file
             sendMailCmd.setMailBody(getPropertyAsString(MESSAGE));
             sendMailCmd.setPlainBody(getPropertyAsBoolean(PLAIN_BODY));
             getAttachmentFiles().forEach(sendMailCmd::addAttachment);
         }
 
         sendMailCmd.setEnableDebug(getPropertyAsBoolean(ENABLE_DEBUG));
 
         if (getPropertyAsString(MAIL_FROM).matches(".*@.*")) {
             sendMailCmd.setSender(getPropertyAsString(MAIL_FROM));
         }
 
         // Process address lists
         sendMailCmd.setReceiverTo(getPropAsAddresses(SmtpSampler.RECEIVER_TO));
         sendMailCmd.setReceiverCC(getPropAsAddresses(SmtpSampler.RECEIVER_CC));
         sendMailCmd.setReceiverBCC(getPropAsAddresses(SmtpSampler.RECEIVER_BCC));
         sendMailCmd.setReplyTo(getPropAsAddresses(SmtpSampler.MAIL_REPLYTO));
         sendMailCmd.setSubject(calculateSubject());
 
         // needed for measuring sending time
         sendMailCmd.setSynchronousMode(true);
 
         sendMailCmd.setHeaderFields((CollectionProperty) getProperty(SmtpSampler.HEADER_FIELDS));
 
         return sendMailCmd;
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
             String boundary = ct.getParameter("boundary");
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
         } else if (content instanceof BodyPart) {
             BodyPart bodyPart = (BodyPart) content;
             writeBodyPart(sb, bodyPart); // throws IOE, ME
         } else if (content instanceof String) {
             sb.append(content);
         } else {
             sb.append("Content has class: ");
             sb.append(content.getClass().getCanonicalName());
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
      *
      * @param propKey key of the property containing addresses separated by ";"
      * @return the list or null if the input was the empty string
      * @throws AddressException thrown if any address is an illegal format
      */
     private List<InternetAddress> getPropAsAddresses(String propKey) throws AddressException {
         final String propValue = getPropertyAsString(propKey).trim();
         if (!propValue.isEmpty()) { // we have at least one potential address
             List<InternetAddress> addresses = new ArrayList<>();
             for (String address : propValue.split(";")) {
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
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
index 343f6ed2c..af51f393e 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
@@ -1,857 +1,857 @@
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
 
 package org.apache.jmeter.protocol.smtp.sampler.protocol;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import javax.activation.DataHandler;
 import javax.activation.FileDataSource;
 import javax.mail.BodyPart;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.Multipart;
 import javax.mail.Session;
 import javax.mail.Transport;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeBodyPart;
 import javax.mail.internet.MimeMessage;
 import javax.mail.internet.MimeMultipart;
 import javax.net.ssl.SSLContext;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class performs all tasks necessary to send a message (build message,
  * prepare connection, send message). Provides getter-/setter-methods for an
  * SmtpSampler-object to configure transport and message settings. The
  * send-mail-command itself is started by the SmtpSampler-object.
  */
 public class SendMailCommand {
 
     // local vars
-    private static final Logger logger = LoggingManager.getLoggerForClass();
-
+    private static final Logger logger = LoggerFactory.getLogger(SendMailCommand.class);
+ 
     // Use the actual class so the name must be correct.
     private static final String TRUST_ALL_SOCKET_FACTORY = TrustAllSSLSocketFactory.class.getName();
 
     private boolean useSSL = false;
     private boolean useStartTLS = false;
     private boolean trustAllCerts = false;
     private boolean enforceStartTLS = false;
     private boolean sendEmlMessage = false;
     private boolean enableDebug;
     private String smtpServer;
     private String smtpPort;
     private String sender;
     private List<InternetAddress> replyTo;
     private String emlMessage;
     private List<InternetAddress> receiverTo;
     private List<InternetAddress> receiverCC;
     private List<InternetAddress> receiverBCC;
     private CollectionProperty headerFields;
     private String subject = "";
 
     private boolean useAuthentication = false;
     private String username;
     private String password;
 
     private boolean useLocalTrustStore;
     private String trustStoreToUse;
 
     private List<File> attachments;
 
     private String mailBody;
 
     private String timeOut; // Socket read timeout value in milliseconds. This timeout is implemented by java.net.Socket.
     private String connectionTimeOut; // Socket connection timeout value in milliseconds. This timeout is implemented by java.net.Socket.
 
     // case we are measuring real time of spedition
     private boolean synchronousMode;
 
     private Session session;
 
     private StringBuilder serverResponse = new StringBuilder(); // TODO this is not populated currently
 
     /** send plain body, i.e. not multipart/mixed */
     private boolean plainBody;
 
     /**
      * Standard-Constructor
      */
     public SendMailCommand() {
         headerFields = new CollectionProperty();
         attachments = new ArrayList<>();
     }
 
     /**
      * Prepares message prior to be sent via execute()-method, i.e. sets
      * properties such as protocol, authentication, etc.
      *
      * @return Message-object to be sent to execute()-method
      * @throws MessagingException
      *             when problems constructing or sending the mail occur
      * @throws IOException
      *             when the mail content can not be read or truststore problems
      *             are detected
      */
     public Message prepareMessage() throws MessagingException, IOException {
 
         Properties props = new Properties();
 
         String protocol = getProtocol();
 
         // set properties using JAF
         props.setProperty("mail." + protocol + ".host", smtpServer);
         props.setProperty("mail." + protocol + ".port", getPort());
         props.setProperty("mail." + protocol + ".auth", Boolean.toString(useAuthentication));
         
         // set timeout
         props.setProperty("mail." + protocol + ".timeout", getTimeout());
         props.setProperty("mail." + protocol + ".connectiontimeout", getConnectionTimeout());
 
         if (useStartTLS || useSSL) {
             try {
                 String allProtocols = StringUtils.join(
                     SSLContext.getDefault().getSupportedSSLParameters().getProtocols(), " ");
                 logger.info("Use ssl/tls protocols for mail: " + allProtocols);
                 props.setProperty("mail." + protocol + ".ssl.protocols", allProtocols);
             } catch (Exception e) {
                 logger.error("Problem setting ssl/tls protocols for mail", e);
             }
         }
 
         if (enableDebug) {
             props.setProperty("mail.debug","true");
         }
 
         if (useStartTLS) {
             props.setProperty("mail.smtp.starttls.enable", "true");
             if (enforceStartTLS){
                 // Requires JavaMail 1.4.2+
                 props.setProperty("mail.smtp.starttls.require", "true");
             }
         }
 
         if (trustAllCerts) {
             if (useSSL) {
                 props.setProperty("mail.smtps.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
                 props.setProperty("mail.smtps.ssl.socketFactory.fallback", "false");
             } else if (useStartTLS) {
                 props.setProperty("mail.smtp.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
                 props.setProperty("mail.smtp.ssl.socketFactory.fallback", "false");
             }
         } else if (useLocalTrustStore){
             File truststore = new File(trustStoreToUse);
             logger.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
             if(!truststore.exists()){
                 logger.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
                 truststore = new File(FileServer.getFileServer().getBaseDir(), trustStoreToUse);
                 logger.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
                 if(!truststore.exists()){
                     logger.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
                     throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
                 }
             }
             if (useSSL) {
                 // Requires JavaMail 1.4.2+
                 props.put("mail.smtps.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
                 props.put("mail.smtps.ssl.socketFactory.fallback", "false");
             } else if (useStartTLS) {
                 // Requires JavaMail 1.4.2+
                 props.put("mail.smtp.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
                 props.put("mail.smtp.ssl.socketFactory.fallback", "false");
             }
         }
 
         session = Session.getInstance(props, null);
         
         Message message;
 
         if (sendEmlMessage) {
             message = new MimeMessage(session, new BufferedInputStream(new FileInputStream(emlMessage)));
         } else {
             message = new MimeMessage(session);
             // handle body and attachments
             Multipart multipart = new MimeMultipart();
             final int attachmentCount = attachments.size();
             if (plainBody && 
                (attachmentCount == 0 ||  (mailBody.length() == 0 && attachmentCount == 1))) {
                 if (attachmentCount == 1) { // i.e. mailBody is empty
                     File first = attachments.get(0);
                     try (FileInputStream fis = new FileInputStream(first);
                             InputStream is = new BufferedInputStream(fis)){
                         message.setText(IOUtils.toString(is, Charset.defaultCharset()));
                     }
                 } else {
                     message.setText(mailBody);
                 }
             } else {
                 BodyPart body = new MimeBodyPart();
                 body.setText(mailBody);
                 multipart.addBodyPart(body);
                 for (File f : attachments) {
                     BodyPart attach = new MimeBodyPart();
                     attach.setFileName(f.getName());
                     attach.setDataHandler(new DataHandler(new FileDataSource(f.getAbsolutePath())));
                     multipart.addBodyPart(attach);
                 }
                 message.setContent(multipart);
             }
         }
 
         // set from field and subject
         if (null != sender) {
             message.setFrom(new InternetAddress(sender));
         }
 
         if (null != replyTo) {
             InternetAddress[] to = new InternetAddress[replyTo.size()];
             message.setReplyTo(replyTo.toArray(to));
         }
 
         if(null != subject) {
             message.setSubject(subject);
         }
         
         if (receiverTo != null) {
             InternetAddress[] to = new InternetAddress[receiverTo.size()];
             receiverTo.toArray(to);
             message.setRecipients(Message.RecipientType.TO, to);
         }
 
         if (receiverCC != null) {
             InternetAddress[] cc = new InternetAddress[receiverCC.size()];
             receiverCC.toArray(cc);
             message.setRecipients(Message.RecipientType.CC, cc);
         }
 
         if (receiverBCC != null) {
             InternetAddress[] bcc = new InternetAddress[receiverBCC.size()];
             receiverBCC.toArray(bcc);
             message.setRecipients(Message.RecipientType.BCC, bcc);
         }
 
         for (int i = 0; i < headerFields.size(); i++) {
             Argument argument = (Argument) headerFields.get(i).getObjectValue();
             message.setHeader(argument.getName(), argument.getValue());
         }
 
         message.saveChanges();
         return message;
     }
 
     /**
      * Sends message to mailserver, waiting for delivery if using synchronous
      * mode.
      *
      * @param message
      *            Message previously prepared by prepareMessage()
      * @throws MessagingException
      *             when problems sending the mail arise
      * @throws InterruptedException
      *             when interrupted while waiting for delivery in synchronous
      *             mode
      */
     public void execute(Message message) throws MessagingException, InterruptedException {
 
         Transport tr = null;
         try {
             tr = session.getTransport(getProtocol());
             SynchronousTransportListener listener = null;
 
             if (synchronousMode) {
                 listener = new SynchronousTransportListener();
                 tr.addTransportListener(listener);
             }
     
             if (useAuthentication) {
                 tr.connect(smtpServer, username, password);
             } else {
                 tr.connect();
             }
 
             tr.sendMessage(message, message.getAllRecipients());
 
             if (listener != null /*synchronousMode==true*/) {
                 listener.attend(); // listener cannot be null here
             }
         } finally {
             if(tr != null) {
                 try {
                     tr.close();
                 } catch (Exception e) {
                     // NOOP
                 }
             }
             logger.debug("transport closed");
         }
 
         logger.debug("message sent");
     }
 
     /**
      * Processes prepareMessage() and execute()
      *
      * @throws InterruptedException
      *             when interrupted while waiting for delivery in synchronous
      *             modus
      * @throws IOException
      *             when the mail content can not be read or truststore problems
      *             are detected
      * @throws MessagingException
      *             when problems constructing or sending the mail occur
      */
     public void execute() throws MessagingException, IOException, InterruptedException {
         execute(prepareMessage());
     }
 
     /**
      * Returns FQDN or IP of SMTP-server to be used to send message - standard
      * getter
      *
      * @return FQDN or IP of SMTP-server
      */
     public String getSmtpServer() {
         return smtpServer;
     }
 
     /**
      * Sets FQDN or IP of SMTP-server to be used to send message - to be called
      * by SmtpSampler-object
      *
      * @param smtpServer
      *            FQDN or IP of SMTP-server
      */
     public void setSmtpServer(String smtpServer) {
         this.smtpServer = smtpServer;
     }
 
     /**
      * Returns sender-address for current message - standard getter
      *
      * @return sender-address
      */
     public String getSender() {
         return sender;
     }
 
     /**
      * Sets the sender-address for the current message - to be called by
      * SmtpSampler-object
      *
      * @param sender
      *            Sender-address for current message
      */
     public void setSender(String sender) {
         this.sender = sender;
     }
 
     /**
      * Returns subject for current message - standard getter
      *
      * @return Subject of current message
      */
     public String getSubject() {
         return subject;
     }
 
     /**
      * Sets subject for current message - called by SmtpSampler-object
      *
      * @param subject
      *            Subject for message of current message - may be null
      */
     public void setSubject(String subject) {
         this.subject = subject;
     }
 
     /**
      * Returns username to authenticate at the mailserver - standard getter
      *
      * @return Username for mailserver
      */
     public String getUsername() {
         return username;
     }
 
     /**
      * Sets username to authenticate at the mailserver - to be called by
      * SmtpSampler-object
      *
      * @param username
      *            Username for mailserver
      */
     public void setUsername(String username) {
         this.username = username;
     }
 
     /**
      * Returns password to authenticate at the mailserver - standard getter
      *
      * @return Password for mailserver
      */
     public String getPassword() {
         return password;
     }
 
     /**
      * Sets password to authenticate at the mailserver - to be called by
      * SmtpSampler-object
      *
      * @param password
      *            Password for mailserver
      */
     public void setPassword(String password) {
         this.password = password;
     }
 
     /**
      * Sets receivers of current message ("to") - to be called by
      * SmtpSampler-object
      *
      * @param receiverTo
      *            List of receivers
      */
     public void setReceiverTo(List<InternetAddress> receiverTo) {
         this.receiverTo = receiverTo;
     }
 
     /**
      * Returns receivers of current message as {@link InternetAddress} ("cc") - standard
      * getter
      *
      * @return List of receivers
      */
     public List<InternetAddress> getReceiverCC() {
         return receiverCC;
     }
 
     /**
      * Sets receivers of current message ("cc") - to be called by
      * SmtpSampler-object
      *
      * @param receiverCC
      *            List of receivers
      */
     public void setReceiverCC(List<InternetAddress> receiverCC) {
         this.receiverCC = receiverCC;
     }
 
     /**
      * Returns receivers of current message as {@link InternetAddress} ("bcc") - standard
      * getter
      *
      * @return List of receivers
      */
     public List<InternetAddress> getReceiverBCC() {
         return receiverBCC;
     }
 
     /**
      * Sets receivers of current message ("bcc") - to be called by
      * SmtpSampler-object
      *
      * @param receiverBCC
      *            List of receivers
      */
     public void setReceiverBCC(List<InternetAddress> receiverBCC) {
         this.receiverBCC = receiverBCC;
     }
 
     /**
      * Returns if authentication is used to access the mailserver - standard
      * getter
      *
      * @return True if authentication is used to access mailserver
      */
     public boolean isUseAuthentication() {
         return useAuthentication;
     }
 
     /**
      * Sets if authentication should be used to access the mailserver - to be
      * called by SmtpSampler-object
      *
      * @param useAuthentication
      *            Should authentication be used to access mailserver?
      */
     public void setUseAuthentication(boolean useAuthentication) {
         this.useAuthentication = useAuthentication;
     }
 
     /**
      * Returns if SSL is used to send message - standard getter
      *
      * @return True if SSL is used to transmit message
      */
     public boolean getUseSSL() {
         return useSSL;
     }
 
     /**
      * Sets SSL to secure the delivery channel for the message - to be called by
      * SmtpSampler-object
      *
      * @param useSSL
      *            Should StartTLS be used to secure SMTP-connection?
      */
     public void setUseSSL(boolean useSSL) {
         this.useSSL = useSSL;
     }
 
     /**
      * Returns if StartTLS is used to transmit message - standard getter
      *
      * @return True if StartTLS is used to transmit message
      */
     public boolean getUseStartTLS() {
         return useStartTLS;
     }
 
     /**
      * Sets StartTLS to secure the delivery channel for the message - to be
      * called by SmtpSampler-object
      *
      * @param useStartTLS
      *            Should StartTLS be used to secure SMTP-connection?
      */
     public void setUseStartTLS(boolean useStartTLS) {
         this.useStartTLS = useStartTLS;
     }
 
     /**
      * Returns port to be used for SMTP-connection (standard 25 or 465) -
      * standard getter
      *
      * @return Port to be used for SMTP-connection
      */
     public String getSmtpPort() {
         return smtpPort;
     }
 
     /**
      * Sets port to be used for SMTP-connection (standard 25 or 465) - to be
      * called by SmtpSampler-object
      *
      * @param smtpPort
      *            Port to be used for SMTP-connection
      */
     public void setSmtpPort(String smtpPort) {
         this.smtpPort = smtpPort;
     }
 
     /**
      * Returns if sampler should trust all certificates - standard getter
      *
      * @return True if all Certificates are trusted
      */
     public boolean isTrustAllCerts() {
         return trustAllCerts;
     }
 
     /**
      * Determines if SMTP-sampler should trust all certificates, no matter what
      * CA - to be called by SmtpSampler-object
      *
      * @param trustAllCerts
      *            Should all certificates be trusted?
      */
     public void setTrustAllCerts(boolean trustAllCerts) {
         this.trustAllCerts = trustAllCerts;
     }
 
     /**
      * Instructs object to enforce StartTLS and not to fallback to plain
      * SMTP-connection - to be called by SmtpSampler-object
      *
      * @param enforceStartTLS
      *            Should StartTLS be enforced?
      */
     public void setEnforceStartTLS(boolean enforceStartTLS) {
         this.enforceStartTLS = enforceStartTLS;
     }
 
     /**
      * Returns if StartTLS is enforced to secure the connection, i.e. no
      * fallback is used (plain SMTP) - standard getter
      *
      * @return True if StartTLS is enforced
      */
     public boolean isEnforceStartTLS() {
         return enforceStartTLS;
     }
 
     /**
      * Returns headers for current message - standard getter
      *
      * @return CollectionProperty of headers for current message
      */
     public CollectionProperty getHeaders() {
         return headerFields;
     }
 
     /**
      * Sets headers for current message
      *
      * @param headerFields
      *            CollectionProperty of headers for current message
      */
     public void setHeaderFields(CollectionProperty headerFields) {
         this.headerFields = headerFields;
     }
 
     /**
      * Adds a header-part to current HashMap of headers - to be called by
      * SmtpSampler-object
      *
      * @param headerName
      *            Key for current header
      * @param headerValue
      *            Value for current header
      */
     public void addHeader(String headerName, String headerValue) {
         if (this.headerFields == null){
             this.headerFields = new CollectionProperty();
         }
         Argument argument = new Argument(headerName, headerValue);
         this.headerFields.addItem(argument);
     }
 
     /**
      * Deletes all current headers in HashMap
      */
     public void clearHeaders() {
         if (this.headerFields == null){
             this.headerFields = new CollectionProperty();
         }else{
             this.headerFields.clear();
         }
     }
 
     /**
      * Returns all attachment for current message - standard getter
      *
      * @return List of attachments for current message
      */
     public List<File> getAttachments() {
         return attachments;
     }
 
     /**
      * Adds attachments to current message
      *
      * @param attachments
      *            List of files to be added as attachments to current message
      */
     public void setAttachments(List<File> attachments) {
         this.attachments = attachments;
     }
 
     /**
      * Adds an attachment to current message - to be called by
      * SmtpSampler-object
      *
      * @param attachment
      *            File-object to be added as attachment to current message
      */
     public void addAttachment(File attachment) {
         this.attachments.add(attachment);
     }
 
     /**
      * Clear all attachments for current message
      */
     public void clearAttachments() {
         this.attachments.clear();
     }
 
     /**
      * Returns if synchronous-mode is used for current message (i.e. time for
      * delivery, ... is measured) - standard getter
      *
      * @return True if synchronous-mode is used
      */
     public boolean isSynchronousMode() {
         return synchronousMode;
     }
 
     /**
      * Sets the use of synchronous-mode (i.e. time for delivery, ... is
      * measured) - to be called by SmtpSampler-object
      *
      * @param synchronousMode
      *            Should synchronous-mode be used?
      */
     public void setSynchronousMode(boolean synchronousMode) {
         this.synchronousMode = synchronousMode;
     }
 
     /**
      * Returns which protocol should be used to transport message (smtps for
      * SSL-secured connections or smtp for plain SMTP / StartTLS)
      *
      * @return Protocol that is used to transport message
      */
     private String getProtocol() {
         return useSSL ? "smtps" : "smtp";
     }
 
     /**
      * Returns port to be used for SMTP-connection - returns the
      * default port for the protocol if no port has been supplied.
      *
      * @return Port to be used for SMTP-connection
      */
     private String getPort() {
         String port = smtpPort.trim();
         if (port.length() > 0) { // OK, it has been supplied
             return port;
         }
         if (useSSL){
             return "465";
         }
         if (useStartTLS) {
             return "587";
         }
         return "25";
     }
 
     /**
      * @param timeOut the timeOut to set
      */
     public void setTimeOut(String timeOut) {
         this.timeOut = timeOut;
     }
 
     /**
      * Returns timeout for the SMTP-connection - returns the
      * default timeout if no value has been supplied.
      *
      * @return Timeout to be set for SMTP-connection
      */
     public String getTimeout() {
         String timeout = timeOut.trim();
         if (timeout.length() > 0) { // OK, it has been supplied
             return timeout;
         }
         return "0"; // Default is infinite timeout (value 0).
     }
 
     /**
      * @param connectionTimeOut the connectionTimeOut to set
      */
     public void setConnectionTimeOut(String connectionTimeOut) {
         this.connectionTimeOut = connectionTimeOut;
     }
 
     /**
      * Returns connection timeout for the SMTP-connection - returns the
      * default connection timeout if no value has been supplied.
      *
      * @return Connection timeout to be set for SMTP-connection
      */
     public String getConnectionTimeout() {
         String connectionTimeout = connectionTimeOut.trim();
         if (connectionTimeout.length() > 0) { // OK, it has been supplied
             return connectionTimeout;
         }
         return "0"; // Default is infinite timeout (value 0).
     }
 
     /**
      * Assigns the object to use a local truststore for SSL / StartTLS - to be
      * called by SmtpSampler-object
      *
      * @param useLocalTrustStore
      *            Should a local truststore be used?
      */
     public void setUseLocalTrustStore(boolean useLocalTrustStore) {
         this.useLocalTrustStore = useLocalTrustStore;
     }
 
     /**
      * Sets the path to the local truststore to be used for SSL / StartTLS - to
      * be called by SmtpSampler-object
      *
      * @param trustStoreToUse
      *            Path to local truststore
      */
     public void setTrustStoreToUse(String trustStoreToUse) {
         this.trustStoreToUse = trustStoreToUse;
     }
 
     public void setUseEmlMessage(boolean sendEmlMessage) {
         this.sendEmlMessage = sendEmlMessage;
     }
 
     /**
      * Sets eml-message to be sent
      *
      * @param emlMessage
      *            path to eml-message
      */
     public void setEmlMessage(String emlMessage) {
         this.emlMessage = emlMessage;
     }
 
     /**
      * Set the mail body.
      *
      * @param body the body of the mail
      */
     public void setMailBody(String body){
         mailBody = body;
     }
     
     /**
      * Set whether to send a plain body (i.e. not multipart/mixed)
      *
      * @param plainBody <code>true</code> if sending a plain body (i.e. not multipart/mixed)
      */
     public void setPlainBody(boolean plainBody){
         this.plainBody = plainBody;
     }
 
     public String getServerResponse() {
         return this.serverResponse.toString();
     }
 
     public void setEnableDebug(boolean selected) {
         enableDebug = selected;
 
     }
 
     public void setReplyTo(List<InternetAddress> replyTo) {
         this.replyTo = replyTo;
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SynchronousTransportListener.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SynchronousTransportListener.java
index 80708887a..0602d34a7 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SynchronousTransportListener.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SynchronousTransportListener.java
@@ -1,100 +1,101 @@
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
 
 package org.apache.jmeter.protocol.smtp.sampler.protocol;
 
 import javax.mail.event.TransportAdapter;
 import javax.mail.event.TransportEvent;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger; // this comes out of logkit.jar and not
+
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class implements a listener for SMTP events and a monitor for all
  * threads sending mail. The main purpose is to synchronize the send action with
  * the end of communication with remote smtp server, so that sending time can be
  * measured.
  */
 public class SynchronousTransportListener extends TransportAdapter {
 
-    private static final Logger logger = LoggingManager.getLoggerForClass();
+    private static final Logger logger = LoggerFactory.getLogger(SynchronousTransportListener.class);
 
     private boolean finished = false;
 
     private final Object LOCK = new Object();
     
     /**
      * Creates a new instance of SynchronousTransportListener
      */
     public SynchronousTransportListener() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void messageDelivered(TransportEvent e) {
         logger.debug("Message delivered");
         finish();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void messageNotDelivered(TransportEvent e) {
         logger.debug("Message not delivered");
         finish();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void messagePartiallyDelivered(TransportEvent e) {
         logger.debug("Message partially delivered");
         finish();
     }
 
     /**
      * Synchronized-method
      * <p>
      * Waits until {@link #finish()} was called and thus the end of the mail
      * sending was signalled.
      *
      * @throws InterruptedException
      *             when interrupted while waiting with the lock
      */
     public void attend() throws InterruptedException {
         synchronized(LOCK) {
             while (!finished) {
                 LOCK.wait();            
             }
         }
     }
 
     /**
      * Synchronized-method
      */
     public void finish() {
         finished = true;
         synchronized(LOCK) {
             LOCK.notify();
         }
     }
 
 }
