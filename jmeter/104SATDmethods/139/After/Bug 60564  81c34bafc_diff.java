diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
index f61c907f7..50ddaf261 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
@@ -1,384 +1,383 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.accesslog.Filter;
 import org.apache.jmeter.protocol.http.util.accesslog.LogParser;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestCloneable;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Description: <br>
  * <br>
  * AccessLogSampler is responsible for a couple of things:
  * <ul>
  * <li>creating instances of Generator
  * <li>creating instances of Parser
  * <li>triggering popup windows
  * <li>calling Generator.generateRequest()
  * <li>checking to make sure the classes are valid
  * <li>making sure a class can be instantiated
  * </ul>
  * The intent of this sampler is it uses the generator and parser to create a
  * HTTPSampler when it is needed. It does not contain logic about how to parse
  * the logs. It also doesn't care how Generator is implemented, as long as it
  * implements the interface. This means a person could simply implement a dummy
  * parser to generate random parameters and the generator consumes the results.
  * This wasn't the original intent of the sampler. I originaly wanted to write
  * this sampler, so that I can take production logs to simulate production
  * traffic in a test environment. Doing so is desirable to study odd or unusual
  * behavior. It's also good to compare a new system against an existing system
  * to get near apples-to-apples comparison. I've been asked if benchmarks are
  * really fair comparisons just about every single time, so this helps me
  * accomplish that task.
  * <p>
  * Some bugs only appear under production traffic, so it is useful to generate
  * traffic using production logs. This way, JMeter can record when problems
  * occur and provide a way to match the server logs.
  * <p>
- * Created on: Jun 26, 2003
  *
  */
 public class AccessLogSampler extends HTTPSampler implements TestBean,ThreadListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AccessLogSampler.class);
 
-    private static final long serialVersionUID = 232L; // Remember to change this when the class changes ...
+    private static final long serialVersionUID = 233L; // Remember to change this when the class changes ...
 
     public static final String DEFAULT_CLASS = "org.apache.jmeter.protocol.http.util.accesslog.TCLogParser"; // $NON-NLS-1$
 
     /* private members used by class */
     private transient LogParser parser = null;
 
     private String logFile;
     private String parserClassName;
     private String filterClassName;
 
     private transient Filter filter;
 
     private int count = 0;
 
     private boolean started = false;
 
     /**
      * Set the path where XML messages are stored for random selection.
      *
      * @param path path where to store XML messages
      */
     public void setLogFile(String path) {
         logFile = path;
     }
 
     /**
      * Get the path where XML messages are stored. this is the directory where
      * JMeter will randomly select a file.
      *
      * @return path where XML messages are stored
      */
     public String getLogFile() {
         return logFile;
     }
 
     /**
      * it's kinda obvious, but we state it anyways. Set the xml file with a
      * string path.
      *
      * @param classname -
      *            parser class name
      */
     public void setParserClassName(String classname) {
         parserClassName = classname;
     }
 
     /**
      * Get the file location of the xml file.
      *
      * @return String file path.
      */
     public String getParserClassName() {
         return parserClassName;
     }
 
     /**
      * sample gets a new HTTPSampler from the generator and calls it's sample()
      * method.
      *
      * @return newly generated and called sample
      */
     public SampleResult sampleWithParser() {
         initFilter();
         instantiateParser();
         SampleResult res = null;
         try {
 
             if (parser == null) {
                 throw new JMeterException("No Parser available");
             }
             // we call parse with 1 to get only one.
             // this also means if we change the implementation
             // to use 2, it would use every other entry and
             // so on. Not that it is really useful, but a
             // person could use it that way if they have a
             // huge gigabyte log file and they only want to
             // use a quarter of the entries.
             int thisCount = parser.parseAndConfigure(1, this);
             if (thisCount < 0) // Was there an error?
             {
                 return errorResult(new Error("Problem parsing the log file"), new HTTPSampleResult());
             }
             if (thisCount == 0) {
                 if (count == 0 || filter == null) {
                     log.info("Stopping current thread");
                     JMeterContextService.getContext().getThread().stop();
                 }
                 if (filter != null) {
                     filter.reset();
                 }
                 CookieManager cm = getCookieManager();
                 if (cm != null) {
                     cm.clear();
                 }
                 count = 0;
                 return errorResult(new Error("No entries found"), new HTTPSampleResult());
             }
             count = thisCount;
             res = sample();
             if(res != null) {
                 res.setSampleLabel(toString());
             }
         } catch (Exception e) {
             log.warn("Sampling failure", e);
             return errorResult(e, new HTTPSampleResult());
         }
         return res;
     }
 
     /**
      * sample(Entry e) simply calls sample().
      *
      * @param e -
      *            ignored
      * @return the new sample
      */
     @Override
     public SampleResult sample(Entry e) {
         return sampleWithParser();
     }
 
     /**
      * Method will instantiate the log parser based on the class in the text
      * field. This was done to make it easier for people to plugin their own log
      * parser and use different log parser.
      */
     public void instantiateParser() {
         if (parser == null) {
             try {
                 if (this.getParserClassName() != null && this.getParserClassName().length() > 0) {
                     if (this.getLogFile() != null && this.getLogFile().length() > 0) {
                         parser = (LogParser) Class.forName(getParserClassName()).newInstance();
                         parser.setSourceFile(this.getLogFile());
                         parser.setFilter(filter);
                     } else {
                         log.error("No log file specified");
                     }
                 }
             } catch (InstantiationException | ClassNotFoundException
                     | IllegalAccessException e) {
                 log.error("", e);
             }
         }
     }
 
     /**
      * @return Returns the filterClassName.
      */
     public String getFilterClassName() {
         return filterClassName;
     }
 
     /**
      * @param filterClassName
      *            The filterClassName to set.
      */
     public void setFilterClassName(String filterClassName) {
         this.filterClassName = filterClassName;
     }
 
     /**
      * @return Returns the domain.
      */
     @Override
     public String getDomain() { // N.B. Must be in this class for the TestBean code to work
         return super.getDomain();
     }
 
     /**
      * @param domain
      *            The domain to set.
      */
     @Override
     public void setDomain(String domain) { // N.B. Must be in this class for the TestBean code to work
         super.setDomain(domain);
     }
 
     /**
      * @return Returns the imageParsing.
      */
     public boolean isImageParsing() {
         return super.isImageParser();
     }
 
     /**
      * @param imageParsing
      *            The imageParsing to set.
      */
     public void setImageParsing(boolean imageParsing) {
         super.setImageParser(imageParsing);
     }
 
     /**
      * @return Returns the port.
      */
     public String getPortString() {
         return super.getPropertyAsString(HTTPSamplerBase.PORT);
     }
 
     /**
      * @param port
      *            The port to set.
      */
     public void setPortString(String port) {
         super.setProperty(HTTPSamplerBase.PORT, port);
     }
     
     /**
      * Sets the scheme, with default
      * @param value the protocol
      */
     @Override
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
     
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     @Override
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (StringUtils.isEmpty(protocol)) {
             return HTTPConstants.PROTOCOL_HTTP;
         }
         return protocol;
     }
 
     /**
      *
      */
     public AccessLogSampler() {
         super();
     }
 
     protected void initFilter() {
         if (filter == null && filterClassName != null && filterClassName.length() > 0) {
             try {
                 filter = (Filter) Class.forName(filterClassName).newInstance();
             } catch (Exception e) {
                 log.warn("Couldn't instantiate filter '" + filterClassName + "'", e);
             }
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         AccessLogSampler s = (AccessLogSampler) super.clone();
         if (started) {
             if (filterClassName != null && filterClassName.length() > 0) {
 
                 try {
                     if (TestCloneable.class.isAssignableFrom(Class.forName(filterClassName))) {
                         initFilter();
                         s.filter = (Filter) ((TestCloneable) filter).clone();
                     }
                     if (TestCloneable.class.isAssignableFrom(Class.forName(parserClassName)))
                     {
                         instantiateParser();
                         s.parser = (LogParser)((TestCloneable)parser).clone();
                         if (filter != null)
                         {
                             s.parser.setFilter(s.filter);
                         }
                     }
                 } catch (Exception e) {
                     log.warn("Could not clone cloneable filter", e);
                 }
             }
         }
         return s;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         if (parser != null) {
             parser.close();
         }
         filter = null;
         started = false;
         super.testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
         started = true;
         super.testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
         if(parser instanceof ThreadListener) {
             ((ThreadListener)parser).threadFinished();
         }
         if(filter instanceof ThreadListener) {
             ((ThreadListener)filter).threadFinished();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerBeanInfo.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerBeanInfo.java
index 98bba7aa4..898c05520 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerBeanInfo.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSamplerBeanInfo.java
@@ -1,104 +1,104 @@
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
 
 /*
  * Created on May 24, 2004
  *
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.beans.PropertyDescriptor;
 import java.io.IOException;
 import java.util.List;
 
 import org.apache.jmeter.protocol.http.util.accesslog.Filter;
 import org.apache.jmeter.protocol.http.util.accesslog.LogParser;
 import org.apache.jmeter.testbeans.BeanInfoSupport;
 import org.apache.jmeter.testbeans.gui.FileEditor;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.reflect.ClassFinder;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 public class AccessLogSamplerBeanInfo extends BeanInfoSupport {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AccessLogSamplerBeanInfo.class);
 
     public AccessLogSamplerBeanInfo() {
         super(AccessLogSampler.class);
         log.debug("Entered access log sampler bean info");
         try {
             createPropertyGroup("defaults",  // $NON-NLS-1$
                     new String[] { "protocol", "domain", "portString", "imageParsing" });// $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
 
             createPropertyGroup("plugins",  // $NON-NLS-1$
                     new String[] { "parserClassName", "filterClassName" }); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
 
             createPropertyGroup("accesslogfile",  // $NON-NLS-1$
                     new String[] { "logFile" }); // $NON-NLS-1$
 
             PropertyDescriptor p;
 
             p = property("parserClassName");
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, AccessLogSampler.DEFAULT_CLASS);
             p.setValue(NOT_OTHER, Boolean.TRUE);
             p.setValue(NOT_EXPRESSION, Boolean.TRUE);
             final List<String> logParserClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(), new Class[] { LogParser.class });
             if (log.isDebugEnabled()) {
                 log.debug("found parsers: " + logParserClasses);
             }
             p.setValue(TAGS, logParserClasses.toArray(new String[logParserClasses.size()]));
 
             p = property("filterClassName"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.FALSE);
             p.setValue(DEFAULT, ""); // $NON-NLS-1$
             p.setValue(NOT_EXPRESSION, Boolean.TRUE);
             List<String> classes = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(),
                     new Class[] { Filter.class }, false);
             p.setValue(TAGS, classes.toArray(new String[classes.size()]));
 
             p = property("logFile"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, "");
             p.setPropertyEditorClass(FileEditor.class);
 
             p = property("domain"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, "");
 
             p = property("protocol"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, "http"); // $NON-NLS-1$
             p.setValue(DEFAULT_NOT_SAVED, Boolean.TRUE);
 
             p = property("portString"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, ""); // $NON-NLS-1$
 
             p = property("imageParsing"); // $NON-NLS-1$
             p.setValue(NOT_UNDEFINED, Boolean.TRUE);
             p.setValue(DEFAULT, Boolean.FALSE);
             p.setValue(NOT_OTHER, Boolean.TRUE);
         } catch (IOException e) {
             log.warn("couldn't find classes and set up properties", e);
             throw new RuntimeException("Could not find classes with class finder", e);
         }
         log.debug("Got to end of access log samper bean info init");
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AjpSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AjpSampler.java
index 93a5e27be..6ce3cc7e6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AjpSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AjpSampler.java
@@ -1,522 +1,522 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.Socket;
 import java.net.URL;
 
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Selector for the AJP/1.3 protocol
  * (i.e. what Tomcat uses with mod_jk)
  * It allows you to test Tomcat in AJP mode without
  * actually having Apache installed and configured
  *
  */
 public class AjpSampler extends HTTPSamplerBase implements Interruptible {
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
-    private static final Logger log= LoggingManager.getLoggerForClass();
+    private static final Logger log= LoggerFactory.getLogger(AjpSampler.class);
 
     private static final char NEWLINE = '\n';
     private static final String COLON_SPACE = ": ";//$NON-NLS-1$
 
     /**
      *  Translates integer codes to request header names
      */
     private static final String[] HEADER_TRANS_ARRAY = {
         "accept",               //$NON-NLS-1$
         "accept-charset",       //$NON-NLS-1$
         "accept-encoding",      //$NON-NLS-1$
         "accept-language",      //$NON-NLS-1$
         "authorization",        //$NON-NLS-1$
         "connection",           //$NON-NLS-1$
         "content-type",         //$NON-NLS-1$
         "content-length",       //$NON-NLS-1$
         "cookie",               //$NON-NLS-1$
         "cookie2",              //$NON-NLS-1$
         "host",                 //$NON-NLS-1$
         "pragma",               //$NON-NLS-1$
         "referer",              //$NON-NLS-1$
         "user-agent"            //$NON-NLS-1$
     };
 
     /**
      * Base value for translated headers
      */
     static final int AJP_HEADER_BASE = 0xA000;
 
     static final int MAX_SEND_SIZE = 8*1024 - 4 - 4;
 
     private transient Socket channel = null;
     private transient Socket activeChannel = null;
     private int lastPort = -1;
     private String lastHost = null;
     private String localName = null;
     private String localAddress = null;
     private final byte [] inbuf = new byte[8*1024];
     private final byte [] outbuf = new byte[8*1024];
     private final transient ByteArrayOutputStream responseData = new ByteArrayOutputStream();
     private int inpos = 0;
     private int outpos = 0;
     private transient String stringBody = null;
     private transient InputStream body = null;
 
     public AjpSampler() {
     }
 
     @Override
     protected HTTPSampleResult sample(URL url,
                        String method,
                        boolean frd,
                        int fd) {
         HTTPSampleResult res = new HTTPSampleResult();
         res.setSampleLabel(url.toExternalForm());
         res.sampleStart();
         try {
             setupConnection(url, method, res);
             activeChannel = channel;
             execute(method, res);
             res.sampleEnd();
             res.setResponseData(responseData.toByteArray());
             return res;
         } catch(IOException iex) {
             res.sampleEnd();
             lastPort = -1; // force reopen on next sample
             channel = null;
             return errorResult(iex, res);
         } finally {
             activeChannel = null;
             JOrphanUtils.closeQuietly(body);
             body = null;
         }
     }
 
     @Override
     public void threadFinished() {
         if(channel != null) {
             try {
                 channel.close();
             } catch(IOException iex) {
             log.debug("Error closing channel",iex);
             }
         }
         channel = null;
         JOrphanUtils.closeQuietly(body);
         body = null;
         stringBody = null;
     }
 
     private void setupConnection(URL url,
                  String method,
                  HTTPSampleResult res) throws IOException {
 
         String host = url.getHost();
         int port = url.getPort();
         if(port <= 0 || port == url.getDefaultPort()) {
             port = 8009;
         }
         String scheme = url.getProtocol();
         if(channel == null || !host.equals(lastHost) || port != lastPort) {
             if(channel != null) {
             channel.close();
             }
             channel = new Socket(host, port);
             int timeout = JMeterUtils.getPropDefault("httpclient.timeout",0);//$NON-NLS-1$
             if(timeout > 0) {
                 channel.setSoTimeout(timeout);
             }
             localAddress = channel.getLocalAddress().getHostAddress();
             localName = channel.getLocalAddress().getHostName();
             lastHost = host;
             lastPort = port;
         }
         res.setURL(url);
         res.setHTTPMethod(method);
         outpos = 4;
         setByte((byte)2);
         if(method.equals(HTTPConstants.POST)) {
             setByte((byte)4);
         } else {
             setByte((byte)2);
         }
         if(JMeterUtils.getPropDefault("httpclient.version","1.1").equals("1.0")) {//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
             setString("HTTP/1.0");//$NON-NLS-1$
         } else {
             setString(HTTPConstants.HTTP_1_1);
         }
         setString(url.getPath());
         setString(localAddress);
         setString(localName);
         setString(host);
         setInt(url.getDefaultPort());
         setByte(HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(scheme) ? (byte)1 : (byte)0);
         setInt(getHeaderSize(method, url));
         String hdr = setConnectionHeaders(url, host, method);
         res.setRequestHeaders(hdr);
         res.setCookies(setConnectionCookies(url, getCookieManager()));
         String query = url.getQuery();
         if (query != null) {
             setByte((byte)0x05); // Marker for query string attribute
             setString(query);
         }
         setByte((byte)0xff); // More general attributes not supported
     }
 
     private int getHeaderSize(String method, URL url) {
         HeaderManager headers = getHeaderManager();
         CookieManager cookies = getCookieManager();
         AuthManager auth = getAuthManager();
         int hsz = 1; // Host always
         if(method.equals(HTTPConstants.POST)) {
             HTTPFileArg[] hfa = getHTTPFiles();
             if(hfa.length > 0) {
                 hsz += 3;
             } else {
                 hsz += 2;
             }
         }
         if(headers != null) {
             hsz += headers.size();
         }
         if(cookies != null) {
             hsz += cookies.getCookieCount();
         }
         if(auth != null) {
                 String authHeader = auth.getAuthHeaderForURL(url);
             if(authHeader != null) {
             ++hsz;
             }
         }
         return hsz;
     }
 
 
     private String setConnectionHeaders(URL url, String host, String method)
     throws IOException {
         HeaderManager headers = getHeaderManager();
         AuthManager auth = getAuthManager();
         StringBuilder hbuf = new StringBuilder();
         // Allow Headers to override Host setting
         hbuf.append("Host").append(COLON_SPACE).append(host).append(NEWLINE);//$NON-NLS-1$
         setInt(0xA00b); //Host
         setString(host);
         if(headers != null) {
             for (JMeterProperty jMeterProperty : headers.getHeaders()) {
                 Header header = (Header) jMeterProperty.getObjectValue();
                 String n = header.getName();
                 String v = header.getValue();
                 hbuf.append(n).append(COLON_SPACE).append(v).append(NEWLINE);
                 int hc = translateHeader(n);
                 if(hc > 0) {
                     setInt(hc+AJP_HEADER_BASE);
                 } else {
                     setString(n);
                 }
                 setString(v);
             }
         }
         if(method.equals(HTTPConstants.POST)) {
             int cl = -1;
             HTTPFileArg[] hfa = getHTTPFiles();
             if(hfa.length > 0) {
                 HTTPFileArg fa = hfa[0];
                 String fn = fa.getPath();
                 File input = new File(fn);
                 cl = (int)input.length();
                 if(body != null) {
                     JOrphanUtils.closeQuietly(body);
                     body = null;
                 }
                 body = new BufferedInputStream(new FileInputStream(input));
                 setString(HTTPConstants.HEADER_CONTENT_DISPOSITION);
                 setString("form-data; name=\""+encode(fa.getParamName())+
                       "\"; filename=\"" + encode(fn) +"\""); //$NON-NLS-1$ //$NON-NLS-2$
                 String mt = fa.getMimeType();
                 hbuf.append(HTTPConstants.HEADER_CONTENT_TYPE).append(COLON_SPACE).append(mt).append(NEWLINE);
                 setInt(0xA007); // content-type
                 setString(mt);
             } else {
                 hbuf.append(HTTPConstants.HEADER_CONTENT_TYPE).append(COLON_SPACE).append(HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED).append(NEWLINE);
                 setInt(0xA007); // content-type
                 setString(HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                 StringBuilder sb = new StringBuilder();
                 boolean first = true;
                 for (JMeterProperty arg : getArguments()) {
                     if (first) {
                         first = false;
                     } else {
                         sb.append('&');
                     }
                     sb.append(arg.getStringValue());
                 }
                 stringBody = sb.toString();
                 byte [] sbody = stringBody.getBytes(); // TODO - charset?
                 cl = sbody.length;
                 body = new ByteArrayInputStream(sbody);
             }
             hbuf.append(HTTPConstants.HEADER_CONTENT_LENGTH).append(COLON_SPACE).append(String.valueOf(cl)).append(NEWLINE);
             setInt(0xA008); // Content-length
             setString(String.valueOf(cl));
         }
         if(auth != null) {
             String authHeader = auth.getAuthHeaderForURL(url);
             if(authHeader != null) {
                 setInt(0xA005); // Authorization
                 setString(authHeader);
                 hbuf.append(HTTPConstants.HEADER_AUTHORIZATION).append(COLON_SPACE).append(authHeader).append(NEWLINE);
             }
         }
         return hbuf.toString();
     }
 
     private String encode(String value)  {
         StringBuilder newValue = new StringBuilder();
         char[] chars = value.toCharArray();
         for (char c : chars) {
             if (c == '\\')//$NON-NLS-1$
             {
                 newValue.append("\\\\");//$NON-NLS-1$
             } else {
                 newValue.append(c);
             }
         }
         return newValue.toString();
     }
 
     private String setConnectionCookies(URL url, CookieManager cookies) {
         String cookieHeader = null;
         if(cookies != null) {
             cookieHeader = cookies.getCookieHeaderForURL(url);
             for (JMeterProperty jMeterProperty : cookies.getCookies()) {
                 Cookie cookie = (Cookie)(jMeterProperty.getObjectValue());
                 setInt(0xA009); // Cookie
                 setString(cookie.getName()+"="+cookie.getValue());//$NON-NLS-1$
             }
         }
         return cookieHeader;
     }
 
     private int translateHeader(String n) {
         for(int i=0; i < HEADER_TRANS_ARRAY.length; i++) {
             if(HEADER_TRANS_ARRAY[i].equalsIgnoreCase(n)) {
                 return i+1;
             }
         }
         return -1;
     }
 
     private void setByte(byte b) {
         outbuf[outpos++] = b;
     }
 
     private void setInt(int n) {
         outbuf[outpos++] = (byte)((n >> 8)&0xff);
         outbuf[outpos++] = (byte) (n&0xff);
     }
 
     private void setString(String s) {
         if( s == null ) {
             setInt(0xFFFF);
         } else {
             int len = s.length();
             setInt(len);
             for(int i=0; i < len; i++) {
                 setByte((byte)s.charAt(i));
             }
             setByte((byte)0);
         }
     }
 
     private void send() throws IOException {
         OutputStream os = channel.getOutputStream();
         int len = outpos;
         outpos = 0;
         setInt(0x1234);
         setInt(len-4);
         os.write(outbuf, 0, len);
     }
 
     private void execute(String method, HTTPSampleResult res)
     throws IOException {
         send();
         if(method.equals(HTTPConstants.POST)) {
             res.setQueryString(stringBody);
             sendPostBody();
         }
         handshake(res);
     }
 
     private void handshake(HTTPSampleResult res) throws IOException {
         responseData.reset();
         int msg = getMessage();
         while(msg != 5) {
             if(msg == 3) {
             int len = getInt();
                 responseData.write(inbuf, inpos, len);
             } else if(msg == 4) {
                 parseHeaders(res);
             } else if(msg == 6) {
                 setNextBodyChunk();
                 send();
             }
             msg = getMessage();
         }
     }
 
 
     private void sendPostBody() throws IOException {
         setNextBodyChunk();
         send();
     }
 
     private void setNextBodyChunk() throws IOException {
         int nr = 0;
         if(body != null) {
             int len = body.available();
             if(len < 0) {
                 len = 0;
             } else if(len > MAX_SEND_SIZE) {
                 len = MAX_SEND_SIZE;
             }
             outpos = 4;
             if(len > 0) {
                 nr = body.read(outbuf, outpos+2, len);
             }
         } else {
             outpos = 4;
         }
         setInt(nr);
         outpos += nr;
     }
 
 
     private void parseHeaders(HTTPSampleResult res)
     throws IOException {
         int status = getInt();
         res.setResponseCode(Integer.toString(status));
         res.setSuccessful(200 <= status && status <= 399);
         String msg = getString();
         res.setResponseMessage(msg);
         int nh = getInt();
         StringBuilder sb = new StringBuilder();
         sb.append(HTTPConstants.HTTP_1_1 ).append(status).append(" ").append(msg).append(NEWLINE);//$NON-NLS-1$//$NON-NLS-2$
         for(int i=0; i < nh; i++) {
             String name;
             int thn = peekInt();
             if((thn & 0xff00) == AJP_HEADER_BASE) {
                 name = HEADER_TRANS_ARRAY[(thn&0xff)-1];
                 getInt(); // we need to use up the int now
             } else {
                 name = getString();
             }
             String value = getString();
             if(HTTPConstants.HEADER_CONTENT_TYPE.equalsIgnoreCase(name)) {
                 res.setContentType(value);
                 res.setEncodingAndType(value);
             } else if(HTTPConstants.HEADER_SET_COOKIE.equalsIgnoreCase(name)) {
                 CookieManager cookies = getCookieManager();
                 if(cookies != null) {
                     cookies.addCookieFromHeader(value, res.getURL());
                 }
             }
             sb.append(name).append(COLON_SPACE).append(value).append(NEWLINE);
         }
         res.setResponseHeaders(sb.toString());
     }
 
 
     private int getMessage() throws IOException {
         InputStream is = channel.getInputStream();
         inpos = 0;
         int nr = is.read(inbuf, inpos, 4);
         if(nr != 4) {
             channel.close();
             channel = null;
             throw new IOException("Connection Closed: "+nr);
         }
         getInt();
         int len = getInt();
         int toRead = len;
         int cpos = inpos;
         while(toRead > 0) {
             nr = is.read(inbuf, cpos, toRead);
             cpos += nr;
             toRead -= nr;
         }
         return getByte();
     }
 
     private byte getByte() {
         return inbuf[inpos++];
     }
 
     private int getInt() {
         int res = (inbuf[inpos++]<<8)&0xff00;
         res += inbuf[inpos++]&0xff;
         return res;
     }
 
     private int peekInt() {
         int res = (inbuf[inpos]<<8)&0xff00;
         res += inbuf[inpos+1]&0xff;
         return res;
     }
 
     private String getString() throws IOException {
         int len = getInt();
         String s = new String(inbuf, inpos, len, "iso-8859-1");//$NON-NLS-1$
         inpos+= len+1;
         return s;
     }
 
     @Override
     public boolean interrupt() {
         Socket chan = activeChannel;
         if (chan != null) {
             activeChannel = null;
             try {
                 chan.close();
             } catch (Exception e) {
                 // Ignored
             }
         }
         return chan != null;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index 69c559e8a..b75faaa0f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -1,1096 +1,1096 @@
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
 import org.apache.commons.httpclient.Header;
 import org.apache.commons.httpclient.HostConfiguration;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpConnectionManager;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.HttpMethodBase;
 import org.apache.commons.httpclient.HttpState;
 import org.apache.commons.httpclient.HttpVersion;
 import org.apache.commons.httpclient.NTCredentials;
 import org.apache.commons.httpclient.ProtocolException;
 import org.apache.commons.httpclient.SimpleHttpConnectionManager;
 import org.apache.commons.httpclient.auth.AuthScope;
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
 import org.apache.commons.httpclient.methods.FileRequestEntity;
 import org.apache.commons.httpclient.methods.GetMethod;
 import org.apache.commons.httpclient.methods.HeadMethod;
 import org.apache.commons.httpclient.methods.OptionsMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.PutMethod;
 import org.apache.commons.httpclient.methods.StringRequestEntity;
 import org.apache.commons.httpclient.methods.TraceMethod;
 import org.apache.commons.httpclient.methods.multipart.FilePart;
 import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
 import org.apache.commons.httpclient.methods.multipart.Part;
 import org.apache.commons.httpclient.methods.multipart.PartBase;
 import org.apache.commons.httpclient.methods.multipart.StringPart;
 import org.apache.commons.httpclient.params.DefaultHttpParams;
 import org.apache.commons.httpclient.params.HttpClientParams;
 import org.apache.commons.httpclient.params.HttpMethodParams;
 import org.apache.commons.httpclient.params.HttpParams;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.apache.commons.io.input.CountingInputStream;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.LoopbackHttpClientSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHttpClientSocketFactory;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
  * @deprecated since 3.0, will be removed in next version
  */
 @Deprecated
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPHC3Impl.class);
 
     /** retry count to be used (default 1); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient3.retrycount", 0);
 
     private static final String HTTP_AUTHENTICATION_PREEMPTIVE = "http.authentication.preemptive"; // $NON-NLS-1$
 
     private static final boolean CAN_SET_PREEMPTIVE; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients =
             ThreadLocal.withInitial(HashMap::new);
 
     // Needs to be accessible by HTTPSampler2
     volatile HttpClient savedClient;
 
     private volatile boolean resetSSLContext;
 
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             Protocol.registerProtocol(HTTPConstants.PROTOCOL_HTTP,
                     new Protocol(HTTPConstants.PROTOCOL_HTTP,new SlowHttpClientSocketFactory(CPS_HTTP),HTTPConstants.DEFAULT_HTTP_PORT));
         }
 
         // Now done in JsseSSLManager (which needs to register the protocol)
 //        cps =
 //            JMeterUtils.getPropDefault("httpclient.socket.https.cps", 0); // $NON-NLS-1$
 //
 //        if (cps > 0) {
 //            log.info("Setting up HTTPS SlowProtocol, cps="+cps);
 //            Protocol.registerProtocol(PROTOCOL_HTTPS,
 //                    new Protocol(PROTOCOL_HTTPS,new SlowHttpClientSocketFactory(cps),DEFAULT_HTTPS_PORT));
 //        }
 
         // Set default parameters as needed
         HttpParams params = DefaultHttpParams.getDefaultParams();
         params.setIntParameter("http.protocol.max-redirects", HTTPSamplerBase.MAX_REDIRECTS); //$NON-NLS-1$
         // Process Commons HttpClient parameters file
         String file=JMeterUtils.getProperty("httpclient.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, params);
         }
 
         // If the pre-emptive parameter is undefined, then we can set it as needed
         // otherwise we should do what the user requested.
         CAN_SET_PREEMPTIVE =  params.getParameter(HTTP_AUTHENTICATION_PREEMPTIVE) == null;
 
         // Handle old-style JMeter properties
         try {
             params.setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.parse("HTTP/"+HTTP_VERSION));
         } catch (ProtocolException e) {
             log.warn("Problem setting protocol version "+e.getLocalizedMessage());
         }
 
         if (SO_TIMEOUT >= 0){
             params.setIntParameter(HttpMethodParams.SO_TIMEOUT, SO_TIMEOUT);
         }
 
         // This must be done last, as must not be overridden
         params.setParameter(HttpMethodParams.COOKIE_POLICY,CookiePolicy.IGNORE_COOKIES);
         // We do our own cookie handling
 
         if (USE_LOOPBACK){
             LoopbackHttpClientSocketFactory.setup();
         }
     }
 
     protected HTTPHC3Impl(HTTPSamplerBase base) {
         super(base);
     }
 
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
 
         String urlStr = url.toString();
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HttpMethodBase httpMethod = null;
 
         HTTPSampleResult res = new HTTPSampleResult();
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         res.sampleStart(); // Count the retries as well in the time
         try {
             // May generate IllegalArgumentException
             if (method.equals(HTTPConstants.POST)) {
                 httpMethod = new PostMethod(urlStr);
             } else if (method.equals(HTTPConstants.GET)){
                 httpMethod = new GetMethod(urlStr);
             } else if (method.equals(HTTPConstants.PUT)){
                 httpMethod = new PutMethod(urlStr);
             } else if (method.equals(HTTPConstants.HEAD)){
                 httpMethod = new HeadMethod(urlStr);
             } else if (method.equals(HTTPConstants.TRACE)){
                 httpMethod = new TraceMethod(urlStr);
             } else if (method.equals(HTTPConstants.OPTIONS)){
                 httpMethod = new OptionsMethod(urlStr);
             } else if (method.equals(HTTPConstants.DELETE)){
                 httpMethod = new EntityEnclosingMethod(urlStr) {
                     @Override
                     public String getName() { // HC3.1 does not have the method
                         return HTTPConstants.DELETE;
                     }
                 };
             } else if (method.equals(HTTPConstants.PATCH)){
                 httpMethod = new EntityEnclosingMethod(urlStr) {
                     @Override
                     public String getName() { // HC3.1 does not have the method
                         return HTTPConstants.PATCH;
                     }
                 };
             } else {
                 throw new IllegalArgumentException("Unexpected method: '"+method+"'");
             }
 
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
                if (cacheManager.inCache(url)) {
                    return updateSampleResultForResourceInCache(res);
                }
             }
 
             // Set any default request headers
             setDefaultRequestHeaders(httpMethod);
 
             // Setup connection
             HttpClient client = setupConnection(url, httpMethod, res);
             savedClient = client;
 
             // Handle the various methods
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData((PostMethod)httpMethod);
                 res.setQueryString(postBody);
             } else if (method.equals(HTTPConstants.PUT) || method.equals(HTTPConstants.PATCH) 
                     || method.equals(HTTPConstants.DELETE)) {
                 String putBody = sendEntityData((EntityEnclosingMethod) httpMethod);
                 res.setQueryString(putBody);
             }
 
             int statusCode = client.executeMethod(httpMethod);
 
             // We've finished with the request, so we can add the LocalAddress to it for display
             final InetAddress localAddr = client.getHostConfiguration().getLocalAddress();
             if (localAddr != null) {
                 httpMethod.addRequestHeader(HEADER_LOCAL_ADDRESS, localAddr.toString());
             }
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             InputStream instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
                 instream = new CountingInputStream(instream);
                 try {
                     Header responseHeader = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_ENCODING);
                     if (responseHeader!= null && HTTPConstants.ENCODING_GZIP.equals(responseHeader.getValue())) {
                         InputStream tmpInput = new GZIPInputStream(instream); // tmp inputstream needs to have a good counting
                         res.setResponseData(readResponse(res, tmpInput, httpMethod.getResponseContentLength()));                        
                     } else {
                         res.setResponseData(readResponse(res, instream, httpMethod.getResponseContentLength()));
                     }
                 } finally {
                     JOrphanUtils.closeQuietly(instream);
                 }
             }
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setSampleLabel(httpMethod.getURI().toString());
             // Pick up Actual path (after redirects)
 
             res.setResponseCode(Integer.toString(statusCode));
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseMessage(httpMethod.getStatusText());
 
             String ct = null;
             Header h = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 final Header headerLocation = httpMethod.getResponseHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation); // in case sanitising fails
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             if (instream != null) {
                 res.setBodySize(((CountingInputStream) instream).getByteCount());
             }
             res.setHeadersSize(calculateHeadersSize(httpMethod));
             if (log.isDebugEnabled()) {
                 log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySizeAsLong()
                         + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
             }
             
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(new URL(httpMethod.getURI().toString()));
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpMethod, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IllegalArgumentException // e.g. some kinds of invalid URL
                 | IOException e) { 
             res.sampleEnd();
             // pick up headers if failed to execute the request
             // httpMethod can be null if method is unexpected
             if(httpMethod != null) {
                 res.setRequestHeaders(getConnectionHeaders(httpMethod));
             }
             errorResult(e, res);
             return res;
         } finally {
             savedClient = null;
             if (httpMethod != null) {
                 httpMethod.releaseConnection();
             }
         }
     }
     
     /**
      * Calculate response headers size
      * 
      * @return the size response headers (in bytes)
      */
     private static int calculateHeadersSize(HttpMethodBase httpMethod) {
         int headerSize = httpMethod.getStatusLine().toString().length()+2; // add a \r\n
         Header[] rh = httpMethod.getResponseHeaders();
         for (Header responseHeader : rh) {
             headerSize += responseHeader.toString().length(); // already include the \r\n
         }
         headerSize += 2; // last \r\n before response data
         return headerSize;
     }
 
     /**
      * Returns an <code>HttpConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param httpMethod
      *            GET/PUT/HEAD etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpClient setupConnection(URL u, HttpMethodBase httpMethod, HTTPSampleResult res) throws IOException {
 
         String urlStr = u.toString();
 
         org.apache.commons.httpclient.URI uri = new org.apache.commons.httpclient.URI(urlStr,false);
 
         String schema = uri.getScheme();
         if ((schema == null) || (schema.length()==0)) {
             schema = HTTPConstants.PROTOCOL_HTTP;
         }
 
         final boolean isHTTPS = HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(schema);
         if (isHTTPS){
             SSLManager.getInstance(); // ensure the manager is initialised
             // we don't currently need to do anything further, as this sets the default https protocol
         }
 
         Protocol protocol = Protocol.getProtocol(schema);
 
         String host = uri.getHost();
         int port = uri.getPort();
 
         /*
          *  We use the HostConfiguration as the key to retrieve the HttpClient,
          *  so need to ensure that any items used in its equals/hashcode methods are
          *  not changed after use, i.e.:
          *  host, port, protocol, localAddress, proxy
          *
         */
         HostConfiguration hc = new HostConfiguration();
         hc.setHost(host, port, protocol); // All needed to ensure re-usablility
 
         // Set up the local address if one exists
         final InetAddress inetAddr = getIpSourceAddress();
         if (inetAddr != null) {// Use special field ip source address (for pseudo 'ip spoofing')
             hc.setLocalAddress(inetAddr);
         } else {
             hc.setLocalAddress(localAddress); // null means use the default
         }
 
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         if (useDynamicProxy){
             hc.setProxy(proxyHost, proxyPort);
             useStaticProxy = false; // Dynamic proxy overrules static proxy
         } else if (useStaticProxy) {
             if (log.isDebugEnabled()){
                 log.debug("Setting proxy: "+PROXY_HOST+":"+PROXY_PORT);
             }
             hc.setProxy(PROXY_HOST, PROXY_PORT);
         }
 
         Map<HostConfiguration, HttpClient> map = httpClients.get();
         // N.B. HostConfiguration.equals() includes proxy settings in the compare.
         HttpClient httpClient = map.get(hc);
 
         if (httpClient != null && resetSSLContext && isHTTPS) {
             httpClient.getHttpConnectionManager().closeIdleConnections(-1000);
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if ( httpClient == null )
         {
             httpClient = new HttpClient(new SimpleHttpConnectionManager());
             httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
                     new DefaultHttpMethodRetryHandler(RETRY_COUNT, false));
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
             httpClient.setHostConfiguration(hc);
             map.put(hc, httpClient);
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // Set up any required Proxy credentials
         if (useDynamicProxy){
             String user = getProxyUser();
             if (user.length() > 0){
                 httpClient.getState().setProxyCredentials(
                         new AuthScope(proxyHost,proxyPort,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(user,getProxyPass(),LOCALHOST,PROXY_DOMAIN)
                     );
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         } else {
             if (useStaticProxy) {
                 if (PROXY_USER.length() > 0){
                     httpClient.getState().setProxyCredentials(
                         new AuthScope(PROXY_HOST,PROXY_PORT,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(PROXY_USER,PROXY_PASS,LOCALHOST,PROXY_DOMAIN)
                     );
                 }
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             httpMethod.getParams().setSoTimeout(rto);
         }
 
         int cto = getConnectTimeout();
         if (cto > 0){
             httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(cto);
         }
 
 
         // Allow HttpClient to handle the redirects:
         httpMethod.setFollowRedirects(getAutoRedirects());
 
         // a well-behaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             httpMethod.setRequestHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
         } else {
             httpMethod.setRequestHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
         }
 
         setConnectionHeaders(httpMethod, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(httpMethod, u, getCookieManager());
 
         setConnectionAuthorization(httpClient, u, getAuthManager());
 
         if (res != null) {
             res.setCookies(cookies);
         }
 
         return httpClient;
     }
 
     /**
      * Set any default request headers to include
      *
      * @param httpMethod the HttpMethod used for the request
      */
     protected void setDefaultRequestHeaders(HttpMethod httpMethod) {
         // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param method the method used to perform the request
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpMethod method) {
         StringBuilder headerBuf = new StringBuilder();
         org.apache.commons.httpclient.Header[] rh = method.getResponseHeaders();
         headerBuf.append(method.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (Header responseHeader : rh) {
             String key = responseHeader.getName();
             headerBuf.append(key);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(responseHeader.getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param method <code>HttpMethod</code> for the request
      * @param u <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     private String setConnectionCookie(HttpMethod method, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 method.setRequestHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Set all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                             jMeterProperty.getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // This helps with SoapSampler hack too
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             method.getParams().setVirtualHost(v);
                         } else {
                             method.addRequestHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(u, method);
         }
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     protected String getConnectionHeaders(HttpMethod method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getRequestHeaders();
         for (Header requestHeader : requestHeaders) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                 hdrs.append(requestHeader.getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeader.getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpMethod</code> passed in.
      *
      * @param client the HttpClient object
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the authorisations for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpClient client, URL u, AuthManager authManager) {
         HttpState state = client.getState();
         if (authManager != null) {
             HttpClientParams params = client.getParams();
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " >  D="+ username + " D="+domain+" R="+realm);
                     }
                     state.setCredentials(
                             new AuthScope(u.getHost(),u.getPort(),
                                     realm.length()==0 ? null : realm //"" is not the same as no realm
                                     ,AuthScope.ANY_SCHEME),
                             // NT Includes other types of Credentials
                             new NTCredentials(
                                     username,
                                     auth.getPass(),
                                     LOCALHOST,
                                     domain
                             ));
                     // We have credentials - should we set pre-emptive authentication?
                     if (CAN_SET_PREEMPTIVE){
                         log.debug("Setting Pre-emptive authentication");
                         params.setAuthenticationPreemptive(true);
                     }
             } else {
                 state.clearCredentials();
                 if (CAN_SET_PREEMPTIVE){
                     params.setAuthenticationPreemptive(false);
                 }
             }
         } else {
             state.clearCredentials();
         }
     }
 
 
     /*
      * Send POST data from <code>Entry</code> to the open connection.
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     private String sendPostData(PostMethod post) throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg[] files = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(isNullOrEmptyTrimmed(contentEncoding)) {
                 contentEncoding = null;
             }
 
             final boolean browserCompatible = getDoBrowserCompatibleMultipart();
             // We don't know how many entries will be skipped
             List<PartBase> partlist = new ArrayList<>();
             // Create the parts
             // Add any parameters
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)) {
                     continue;
                 }
                 StringPart part = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
                 if (browserCompatible) {
                     part.setTransferEncoding(null);
                     part.setContentType(null);
                 }
                 partlist.add(part);
             }
 
             // Add any files
             for (HTTPFileArg file : files) {
                 File inputFile = FileServer.getFileServer().getResolvedFile(file.getPath());
                 // We do not know the char set of the file to be uploaded, so we set it to null
                 ViewableFilePart filePart = new ViewableFilePart(file.getParamName(), inputFile, file.getMimeType(), null);
                 filePart.setCharSet(null); // We do not know what the char set of the file is
                 partlist.add(filePart);
             }
 
             // Set the multipart for the post
             int partNo = partlist.size();
             Part[] parts = partlist.toArray(new Part[partNo]);
             MultipartRequestEntity multiPart = new MultipartRequestEntity(parts, post.getParams());
             post.setRequestEntity(multiPart);
 
             // Set the content type
             String multiPartContentType = multiPart.getContentType();
             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, multiPartContentType);
 
             // If the Multipart is repeatable, we can send it first to
             // our own stream, without the actual file content, so we can return it
             if(multiPart.isRepeatable()) {
                 // For all the file multiparts, we must tell it to not include
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(true); // .sendMultipartWithoutFileContent(bos);
                     }
                 }
                 // Write the request to our own stream
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 multiPart.writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
 
                 // For all the file multiparts, we must revert the hiding of
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(false);
                     }
                 }
             }
             else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
         }
         else {
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(file.getPath()),null);
                 post.setRequestEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             }
             else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(isNullOrEmptyTrimmed(contentEncoding)) {
                     contentEncoding=null;                    
                 } else {
                     post.getParams().setContentCharset(contentEncoding);
                     haveContentEncoding = true;                    
                 }
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 if(getSendParameterValuesAsPostBody()) {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         String value;
                         if (haveContentEncoding) {
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setRequestEntity(requestEntity);
                 }
                 else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)) {
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if (!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             String urlContentEncoding = contentEncoding;
                             if (urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                 // Use the default encoding for urls
                                 urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                             }
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         post.addParameter(parameterName, parameterValue);
                     }
 
 /*
 //                    // Alternative implementation, to make sure that HTTPSampler and HTTPSampler2
 //                    // sends the same post body.
 //
 //                    // Only include the content char set in the content-type header if it is not
 //                    // an APPLICATION_X_WWW_FORM_URLENCODED content type
 //                    String contentCharSet = null;
 //                    if(!post.getRequestHeader(HEADER_CONTENT_TYPE).getValue().equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
 //                        contentCharSet = post.getRequestCharSet();
 //                    }
 //                    StringRequestEntity requestEntity = new StringRequestEntity(getQueryString(contentEncoding), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentCharSet);
 //                    post.setRequestEntity(requestEntity);
 */
                 }
 
                 // If the request entity is repeatable, we can send it first to
                 // our own stream, so we can return it
                 if(post.getRequestEntity().isRepeatable()) {
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     post.getRequestEntity().writeRequest(bos);
                     bos.flush();
                     // We get the posted bytes using the encoding used to create it
                     postedBody.append(new String(bos.toByteArray(),post.getRequestCharSet()));
                     bos.close();
                 }
                 else {
                     postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                 }
             }
         }
         // Set the content length
         post.setRequestHeader(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));
 
         return postedBody.toString();
     }
 
     /**
      * Set up the PUT/PATCH/DELETE data
      */
     private String sendEntityData(EntityEnclosingMethod put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type for a POST request
         Header contentTypeHeader = put.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
         HTTPFileArg[] files = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             File reservedFile = FileServer.getFileServer().getResolvedFile(files[0].getPath());
             FileRequestEntity fileRequestEntity = new FileRequestEntity(reservedFile,null);
             put.setRequestEntity(fileRequestEntity);
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             String contentEncoding = getContentEncoding();
             boolean haveContentEncoding = false;
             if(isNullOrEmptyTrimmed(contentEncoding)) {
                 contentEncoding = null;
             } else {
                 put.getParams().setContentCharset(contentEncoding);
                 haveContentEncoding = true;
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String value = null;
                 if (haveContentEncoding) {
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE).getValue();
             }
             StringRequestEntity requestEntity = new StringRequestEntity(putBodyContent.toString(), contentTypeValue, put.getRequestCharSet());
             put.setRequestEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getRequestEntity().isRepeatable()) {
                 putBody.append("<actual file content, not shown here>");
             }
             else {
                 putBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
             if(!hasContentTypeHeader) {
                 // Allow the mimetype of the file to control the content type
                 // This is not obvious in GUI if you are not uploading any files,
                 // but just sending the content of nameless parameters
                 // TODO: needs a multiple file upload scenerio
                 HTTPFileArg file = files.length > 0? files[0] : null;
                 if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                     put.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             // Set the content length
             put.setRequestHeader(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(put.getRequestEntity().getContentLength()));
         }
         return putBody.toString();
     }
 
     /**
      * Class extending FilePart, so that we can send placeholder text
      * instead of the actual file content
      */
     private static class ViewableFilePart extends FilePart {
         private boolean hideFileData;
 
         public ViewableFilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
             super(name, file, contentType, charset);
             this.hideFileData = false;
         }
 
         public void setHideFileData(boolean hideFileData) {
             this.hideFileData = hideFileData;
         }
 
         @Override
         protected void sendData(OutputStream out) throws IOException {
             // Check if we should send only placeholder text for the
             // file content, or the real file content
             if(hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             }
             else {
                 super.sendData(out);
             }
         }
     }
 
     /**
      * From the <code>HttpMethod</code>, store all the "set-cookie" key-pair
      * values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      */
     protected void saveConnectionCookies(HttpMethod method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdr = method.getResponseHeaders(HTTPConstants.HEADER_SET_COOKIE);
             for (Header responseHeader : hdr) {
                 cookieManager.addCookieFromHeader(responseHeader.getValue(), u);
             }
         }
     }
 
 
     @Override
     protected void threadFinished() {
         log.debug("Thread Finished");
 
         closeThreadLocalConnections();
     }
 
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index bbf550ce6..1dfd1ef87 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1151 +1,1151 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.InetAddress;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.security.PrivilegedActionException;
 import java.security.PrivilegedExceptionAction;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.TimeUnit;
 import java.util.regex.Pattern;
 
 import javax.security.auth.Subject;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.io.input.BoundedInputStream;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.Header;
 import org.apache.http.HttpConnection;
 import org.apache.http.HttpConnectionMetrics;
 import org.apache.http.HttpEntity;
 import org.apache.http.HttpException;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpRequestInterceptor;
 import org.apache.http.HttpResponse;
 import org.apache.http.HttpResponseInterceptor;
 import org.apache.http.NameValuePair;
 import org.apache.http.StatusLine;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.Credentials;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.client.AuthCache;
 import org.apache.http.client.ClientProtocolException;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.HttpRequestRetryHandler;
 import org.apache.http.client.config.CookieSpecs;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPatch;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.client.protocol.HttpClientContext;
 import org.apache.http.client.protocol.ResponseContentEncoding;
 import org.apache.http.conn.ConnectionKeepAliveStrategy;
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.PlainSocketFactory;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.ContentType;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
 import org.apache.http.entity.mime.FormBodyPartBuilder;
 import org.apache.http.entity.mime.MIME;
 import org.apache.http.entity.mime.MultipartEntityBuilder;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.auth.BasicScheme;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.BasicAuthCache;
 import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
 import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.impl.conn.SystemDefaultDnsResolver;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.message.BufferedHeader;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.params.SyncBasicHttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.HTTP;
 import org.apache.http.protocol.HttpContext;
 import org.apache.http.protocol.HttpCoreContext;
 import org.apache.http.util.CharArrayBuffer;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.AuthManager.Mechanism;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  *
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final int MAX_BODY_RETAIN_SIZE = JMeterUtils.getPropDefault("httpclient4.max_body_retain_size", 32 * 1024);
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPHC4Impl.class);
 
     /** retry count to be used (default 0); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient4.retrycount", 0);
 
     /** Idle timeout to be applied to connections if no Keep-Alive header is sent by the server (default 0 = disable) */
     private static final int IDLE_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.idletimeout", 0);
     
     private static final int VALIDITY_AFTER_INACTIVITY_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.validate_after_inactivity", 1700);
     
     private static final int TIME_TO_LIVE = JMeterUtils.getPropDefault("httpclient4.time_to_live", 2000);
 
     /** Preemptive Basic Auth */
     private static final boolean BASIC_AUTH_PREEMPTIVE = JMeterUtils.getPropDefault("httpclient4.auth.preemptive", true);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack for metrics related to HTTPCLIENT-1081, to be removed later
     
     private static final Pattern PORT_PATTERN = Pattern.compile("\\d+"); // only used in .matches(), no need for anchors
 
     private static final ConnectionKeepAliveStrategy IDLE_STRATEGY = new DefaultConnectionKeepAliveStrategy(){
         @Override
         public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
             long duration = super.getKeepAliveDuration(response, context);
             if (duration <= 0 && IDLE_TIMEOUT > 0) {// none found by the superclass
                 if(log.isDebugEnabled()) {
                     log.debug("Setting keepalive to " + IDLE_TIMEOUT);
                 }
                 return IDLE_TIMEOUT;
             } 
             return duration; // return the super-class value
         }
         
     };
 
     /**
      * Special interceptor made to keep metrics when connection is released for some method like HEAD
      * Otherwise calling directly ((HttpConnection) localContext.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
      * would throw org.apache.http.impl.conn.ConnectionShutdownException
      * See <a href="https://bz.apache.org/jira/browse/HTTPCLIENT-1081">HTTPCLIENT-1081</a>
      */
     private static final HttpResponseInterceptor METRICS_SAVER = (HttpResponse response, HttpContext context) -> {
         HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
         context.setAttribute(CONTEXT_METRICS, metrics);
     };
     private static final HttpRequestInterceptor METRICS_RESETTER = (HttpRequest request, HttpContext context) -> {
         HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
         metrics.reset();
     };
 
 
     /**
      * Headers to save
      */
     private static final String[] HEADERS_TO_SAVE = new String[]{
                     "content-length",
                     "content-encoding",
                     "content-md5"
             };
     
     /**
      * Custom implementation that backups headers related to Compressed responses 
      * that HC core {@link ResponseContentEncoding} removes after uncompressing
      * See Bug 59401
      */
     private static final HttpResponseInterceptor RESPONSE_CONTENT_ENCODING = new ResponseContentEncoding() {
         @Override
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             ArrayList<Header[]> headersToSave = null;
             
             final HttpEntity entity = response.getEntity();
             final HttpClientContext clientContext = HttpClientContext.adapt(context);
             final RequestConfig requestConfig = clientContext.getRequestConfig();
             // store the headers if necessary
             if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0) {
                 final Header ceheader = entity.getContentEncoding();
                 if (ceheader != null) {
                     headersToSave = new ArrayList<>(3);
                     for(String name : HEADERS_TO_SAVE) {
                         Header[] hdr = response.getHeaders(name); // empty if none
                         headersToSave.add(hdr);
                     }
                 }
             }
 
             // Now invoke original parent code
             super.process(response, clientContext);
             // Should this be in a finally ? 
             if(headersToSave != null) {
                 for (Header[] headers : headersToSave) {
                     for (Header headerToRestore : headers) {
                         if (response.containsHeader(headerToRestore.getName())) {
                             break;
                         }
                         response.addHeader(headerToRestore);
                     }
                 }
             }
         }
     };
     
     /**
      * 1 HttpClient instance per combination of (HttpClient,HttpClientKey)
      */
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY = 
         new InheritableThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<>();
         }
     };
 
     // Scheme used for slow HTTP sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
 
     private static final String USER_TOKEN = "__jmeter.USER_TOKEN__"; //$NON-NLS-1$
     
     static final String SAMPLER_RESULT_TOKEN = "__jmeter.SAMPLER_RESULT__"; //$NON-NLS-1$
     
     private static final String HTTPCLIENT_TOKEN = "__jmeter.HTTPCLIENT_TOKEN__";
 
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
 
         DEFAULT_HTTP_PARAMS = new SyncBasicHttpParams(); // Could we drop the Sync here?
         DEFAULT_HTTP_PARAMS.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
         DEFAULT_HTTP_PARAMS.setIntParameter(ClientPNames.MAX_REDIRECTS, HTTPSamplerBase.MAX_REDIRECTS);
         DefaultHttpClient.setDefaultHttpParams(DEFAULT_HTTP_PARAMS);
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         // Set up HTTP scheme override if necessary
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(HTTPConstants.PROTOCOL_HTTP, HTTPConstants.DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     private volatile boolean resetSSLContext;
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
     
     /**
      * Implementation that allows GET method to have a body
      */
     public static final class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
 
         public HttpGetWithEntity(final URI uri) {
             super();
             setURI(uri);
         }
 
         @Override
         public String getMethod() {
             return HTTPConstants.GET;
         }
     }
 
     public static final class HttpDelete extends HttpEntityEnclosingRequestBase {
 
         public HttpDelete(final URI uri) {
             super();
             setURI(uri);
         }
 
         @Override
         public String getMethod() {
             return HTTPConstants.DELETE;
         }
     }
     
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + url.toString());
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = createSampleResult(url, method);
 
         HttpClient httpClient = setupClient(url, res);
 
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(HTTPConstants.POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(HTTPConstants.GET)) {
                 // Some servers fail if Content-Length is equal to 0
                 // so to avoid this we use HttpGet when there is no body (Content-Length will not be set)
                 // otherwise we use HttpGetWithEntity
                 if ( !areFollowingRedirect 
                         && ((!hasArguments() && getSendFileAsPostBody()) 
                         || getSendParameterValuesAsPostBody()) ) {
                     httpRequest = new HttpGetWithEntity(uri);
                 } else {
                     httpRequest = new HttpGet(uri);
                 }
             } else if (method.equals(HTTPConstants.PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HTTPConstants.HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(HTTPConstants.TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(HTTPConstants.OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(HTTPConstants.DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(HTTPConstants.PATCH)) {
                 httpRequest = new HttpPatch(uri);
             } else if (HttpWebdav.isWebdavMethod(method)) {
                 httpRequest = new HttpWebdav(method, uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: '"+method+"'");
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             errorResult(e, res);
             return res;
         }
 
         HttpContext localContext = new BasicHttpContext();
         setupClientContextBeforeSample(localContext);
         
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method) && cacheManager.inCache(url)) {
             return updateSampleResultForResourceInCache(res);
         }
 
         try {
             currentRequest = httpRequest;
             handleMethod(method, res, httpRequest, localContext);
             // store the SampleResult in LocalContext to compute connect time
             localContext.setAttribute(SAMPLER_RESULT_TOKEN, res);
             // perform the sample
             HttpResponse httpResponse = 
                     executeRequest(httpClient, httpRequest, localContext, url);
 
             // Needs to be done after execute to pick up all the headers
             final HttpRequest request = (HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
             extractClientContextAfterSample(localContext);
             // We've finished with the request, so we can add the LocalAddress to it for display
             final InetAddress localAddr = (InetAddress) httpRequest.getParams().getParameter(ConnRoutePNames.LOCAL_ADDRESS);
             if (localAddr != null) {
                 request.addHeader(HEADER_LOCAL_ADDRESS, localAddr.toString());
             }
             res.setRequestHeaders(getConnectionHeaders(request));
 
             Header contentType = httpResponse.getLastHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 res.setResponseData(readResponse(res, entity.getContent(), entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse, localContext));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header in redirect for " + httpRequest.getRequestLine());
                 }
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation);
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             HttpConnectionMetrics  metrics = (HttpConnectionMetrics) localContext.getAttribute(CONTEXT_METRICS);
             long headerBytes = 
                 (long)res.getResponseHeaders().length()   // condensed length (without \r)
               + (long) httpResponse.getAllHeaders().length // Add \r for each header
               + 1L // Add \r for initial header
               + 2L; // final \r\n before data
             long totalBytes = metrics.getReceivedBytesCount();
             res.setHeadersSize((int)headerBytes);
             res.setBodySize(totalBytes - headerBytes);
             res.setSentBytes(metrics.getSentBytesCount());
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySizeAsLong()
                         + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                 URI redirectURI = req.getURI();
                 if (redirectURI.isAbsolute()){
                     res.setURL(redirectURI.toURL());
                 } else {
                     res.setURL(new URL(new URL(target.toURI()),redirectURI.toString()));
                 }
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpResponse, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpResponse, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
         } catch (IOException e) {
             log.debug("IOException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
            // pick up headers if failed to execute the request
             if (res.getRequestHeaders() != null) {
                 log.debug("Overwriting request old headers: " + res.getRequestHeaders());
             }
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST)));
             errorResult(e, res);
             return res;
         } catch (RuntimeException e) {
             log.debug("RuntimeException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
             errorResult(e, res);
             return res;
         } finally {
             currentRequest = null;
             JMeterContextService.getContext().getSamplerContext().remove(HTTPCLIENT_TOKEN);
         }
         return res;
     }
 
     /**
      * Store in JMeter Variables the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void extractClientContextAfterSample(HttpContext localContext) {
         Object userToken = localContext.getAttribute(HttpClientContext.USER_TOKEN);
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Extracted from HttpContext user token:"+userToken+", storing it as JMeter variable:"+USER_TOKEN);
             }
             // During recording JMeterContextService.getContext().getVariables() is null
             JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
             if (jMeterVariables != null) {
                 jMeterVariables.putObject(USER_TOKEN, userToken); 
             }
         }
     }
 
     /**
      * Configure the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void setupClientContextBeforeSample(HttpContext localContext) {
         Object userToken = null;
         // During recording JMeterContextService.getContext().getVariables() is null
         JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
         if(jMeterVariables != null) {
             userToken = jMeterVariables.getObject(USER_TOKEN);            
         }
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Found user token:"+userToken+" as JMeter variable:"+USER_TOKEN+", storing it in HttpContext");
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userToken);
         } else {
             // It would be better to create a ClientSessionManager that would compute this value
             // for now it can be Thread.currentThread().getName() but must be changed when we would change 
             // the Thread per User model
             String userId = Thread.currentThread().getName();
             if(log.isDebugEnabled()) {
                 log.debug("Storing in HttpContext the user token:"+userId);
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userId);
         }
     }
 
     /**
      * Calls {@link #sendPostData(HttpPost)} if method is <code>POST</code> and
      * {@link #sendEntityData(HttpEntityEnclosingRequestBase)} if method is
      * <code>PUT</code> or <code>PATCH</code>
      * <p>
      * Field HTTPSampleResult#queryString of result is modified in the 2 cases
      * 
      * @param method
      *            String HTTP method
      * @param result
      *            {@link HTTPSampleResult}
      * @param httpRequest
      *            {@link HttpRequestBase}
      * @param localContext
      *            {@link HttpContext}
      * @throws IOException
      *             when posting data fails due to I/O
      */
     protected void handleMethod(String method, HTTPSampleResult result,
             HttpRequestBase httpRequest, HttpContext localContext) throws IOException {
         // Handle the various methods
         if (httpRequest instanceof HttpPost) {
             String postBody = sendPostData((HttpPost)httpRequest);
             result.setQueryString(postBody);
         } else if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
             String entityBody = sendEntityData((HttpEntityEnclosingRequestBase) httpRequest);
             result.setQueryString(entityBody);
         }
     }
 
     /**
      * Create HTTPSampleResult filling url, method and SampleLabel.
      * Monitor field is computed calling isMonitor()
      * @param url URL
      * @param method HTTP Method
      * @return {@link HTTPSampleResult}
      */
     protected HTTPSampleResult createSampleResult(URL url, String method) {
         HTTPSampleResult res = new HTTPSampleResult();
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
         
         return res;
     }
 
     /**
      * Execute request either as is or under PrivilegedAction 
      * if a Subject is available for url
      * @param httpClient the {@link HttpClient} to be used to execute the httpRequest
      * @param httpRequest the {@link HttpRequest} to be executed
      * @param localContext th {@link HttpContext} to be used for execution
      * @param url the target url (will be used to look up a possible subject for the execution)
      * @return the result of the execution of the httpRequest
      * @throws IOException
      * @throws ClientProtocolException
      */
     private HttpResponse executeRequest(final HttpClient httpClient,
             final HttpRequestBase httpRequest, final HttpContext localContext, final URL url)
             throws IOException, ClientProtocolException {
         AuthManager authManager = getAuthManager();
         if (authManager != null) {
             Subject subject = authManager.getSubjectForUrl(url);
             if (subject != null) {
                 try {
                     return Subject.doAs(subject,
                             (PrivilegedExceptionAction<HttpResponse>) () ->
                                     httpClient.execute(httpRequest, localContext));
                 } catch (PrivilegedActionException e) {
                     log.error("Can't execute httpRequest with subject:" + subject, e);
                     throw new RuntimeException("Can't execute httpRequest with subject:" + subject, e);
                 }
             }
 
             if(BASIC_AUTH_PREEMPTIVE) {
                 Authorization authorization = authManager.getAuthForURL(url);
                 if(authorization != null && Mechanism.BASIC_DIGEST.equals(authorization.getMechanism())) {
                     HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                     // Create AuthCache instance
                     AuthCache authCache = new BasicAuthCache();
                     // Generate BASIC scheme object and 
                     // add it to the local auth cache
                     BasicScheme basicAuth = new BasicScheme();
                     authCache.put(target, basicAuth);
                     // Add AuthCache to the execution context
                     localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
                 }
             }
         }
         return httpClient.execute(httpRequest, localContext);
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final String target; // protocol://[user:pass@]host:[port]
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         /**
          * @param url URL Only protocol and url authority are used (protocol://[user:pass@]host:[port])
          * @param hasProxy has proxy
          * @param proxyHost proxy host
          * @param proxyPort proxy port
          * @param proxyUser proxy user
          * @param proxyPass proxy password
          */
         public HttpClientKey(URL url, boolean hasProxy, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             // N.B. need to separate protocol from authority otherwise http://server would match https://erver (<= sic, not typo error)
             // could use separate fields, but simpler to combine them
             this.target = url.getProtocol()+"://"+url.getAuthority();
             this.hasProxy = hasProxy;
             this.proxyHost = proxyHost;
             this.proxyPort = proxyPort;
             this.proxyUser = proxyUser;
             this.proxyPass = proxyPass;
             this.hashCode = getHash();
         }
         
         private int getHash() {
             int hash = 17;
             hash = hash*31 + (hasProxy ? 1 : 0);
             if (hasProxy) {
                 hash = hash*31 + getHash(proxyHost);
                 hash = hash*31 + proxyPort;
                 hash = hash*31 + getHash(proxyUser);
                 hash = hash*31 + getHash(proxyPass);
             }
             hash = hash*31 + target.hashCode();
             return hash;
         }
 
         // Allow for null strings
         private int getHash(String s) {
             return s == null ? 0 : s.hashCode(); 
         }
         
         @Override
         public boolean equals (Object obj){
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof HttpClientKey)) {
                 return false;
             }
             HttpClientKey other = (HttpClientKey) obj;
             if (this.hasProxy) { // otherwise proxy String fields may be null
                 return 
                 this.hasProxy == other.hasProxy &&
                 this.proxyPort == other.proxyPort &&
                 this.proxyHost.equals(other.proxyHost) &&
                 this.proxyUser.equals(other.proxyUser) &&
                 this.proxyPass.equals(other.proxyPass) &&
                 this.target.equals(other.target);
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.target.equals(other.target);
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
 
         // For debugging
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(target);
             if (hasProxy) {
                 sb.append(" via ");
                 sb.append(proxyUser);
                 sb.append('@');
                 sb.append(proxyHost);
                 sb.append(':');
                 sb.append(proxyPort);
             }
             return sb.toString();
         }
     }
 
     private HttpClient setupClient(URL url, SampleResult res) {
 
         Map<HttpClientKey, HttpClient> mapHttpClientPerHttpClientKey = HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY.get();
         
         final String host = url.getHost();
         String proxyHost = getProxyHost();
         int proxyPort = getProxyPortInt();
         String proxyPass = getProxyPass();
         String proxyUser = getProxyUser();
 
         // static proxy is the globally define proxy eg command line or properties
         boolean useStaticProxy = isStaticProxy(host);
         // dynamic proxy is the proxy defined for this sampler
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
         boolean useProxy = useStaticProxy || useDynamicProxy;
         
         // if both dynamic and static are used, the dynamic proxy has priority over static
         if(!useDynamicProxy) {
             proxyHost = PROXY_HOST;
             proxyPort = PROXY_PORT;
             proxyUser = PROXY_USER;
             proxyPass = PROXY_PASS;
         }
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, useProxy, proxyHost, proxyPort, proxyUser, proxyPass);
         
         HttpClient httpClient = null;
         boolean concurrentDwn = this.testElement.isConcurrentDwn();
         if(concurrentDwn) {
             httpClient = (HttpClient) JMeterContextService.getContext().getSamplerContext().get(HTTPCLIENT_TOKEN);
         }
         
         if (httpClient == null) {
             httpClient = mapHttpClientPerHttpClientKey.get(key);
         }
 
         if (httpClient != null && resetSSLContext && HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(url.getProtocol())) {
             ((AbstractHttpClient) httpClient).clearRequestInterceptors(); 
             ((AbstractHttpClient) httpClient).clearResponseInterceptors(); 
             httpClient.getConnectionManager().closeIdleConnections(1L, TimeUnit.MICROSECONDS);
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if (httpClient == null) { // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
 
             DnsResolver resolver = this.testElement.getDNSResolver();
             if (resolver == null) {
                 resolver = SystemDefaultDnsResolver.INSTANCE;
             }
             MeasuringConnectionManager connManager = new MeasuringConnectionManager(
                     createSchemeRegistry(), 
                     resolver, 
                     TIME_TO_LIVE,
                     VALIDITY_AFTER_INACTIVITY_TIMEOUT);
             
             // Modern browsers use more connections per host than the current httpclient default (2)
             // when using parallel download the httpclient and connection manager are shared by the downloads threads
             // to be realistic JMeter must set an higher value to DefaultMaxPerRoute
             if(concurrentDwn) {
                 try {
                     int maxConcurrentDownloads = Integer.parseInt(this.testElement.getConcurrentPool());
                     connManager.setDefaultMaxPerRoute(Math.max(maxConcurrentDownloads, connManager.getDefaultMaxPerRoute()));                
                 } catch (NumberFormatException nfe) {
                    // no need to log -> will be done by the sampler
                 }
             }
             
             httpClient = new DefaultHttpClient(connManager, clientParams) {
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false); // set retry count
                 }
             };
             
             if (IDLE_TIMEOUT > 0) {
                 ((AbstractHttpClient) httpClient).setKeepAliveStrategy(IDLE_STRATEGY );
             }
             // see https://issues.apache.org/jira/browse/HTTPCORE-397
             ((AbstractHttpClient) httpClient).setReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(RESPONSE_CONTENT_ENCODING);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             ((AbstractHttpClient) httpClient).addRequestInterceptor(METRICS_RESETTER); 
             
             // Override the default schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             // Set up proxy details
             if(useProxy) {
 
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 
                 if (proxyUser.length() > 0) {                   
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new NTCredentials(proxyUser, proxyPass, LOCALHOST, PROXY_DOMAIN));
                 }
             }
 
             // Bug 52126 - we do our own cookie handling
             clientParams.setParameter(ClientPNames.COOKIE_POLICY, CookieSpecs.IGNORE_COOKIES);
 
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
 
             mapHttpClientPerHttpClientKey.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
         }
 
         if(concurrentDwn) {
             JMeterContextService.getContext().getSamplerContext().put(HTTPCLIENT_TOKEN, httpClient);
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager(), key);
 
         return httpClient;
     }
 
     /**
      * Setup LazySchemeSocketFactory
      * @see "https://bz.apache.org/bugzilla/show_bug.cgi?id=58099"
      */
     private static SchemeRegistry createSchemeRegistry() {
         final SchemeRegistry registry = new SchemeRegistry();
         registry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory())); //$NON-NLS-1$
         registry.register(
                 new Scheme("https", 443, new LazySchemeSocketFactory())); //$NON-NLS-1$
         return registry;
     }
 
     /**
      * Setup following elements on httpRequest:
      * <ul>
      * <li>ConnRoutePNames.LOCAL_ADDRESS enabling IP-SPOOFING</li>
      * <li>Socket and connection timeout</li>
      * <li>Redirect handling</li>
      * <li>Keep Alive header or Connection Close</li>
      * <li>Calls setConnectionHeaders to setup headers</li>
      * <li>Calls setConnectionCookie to setup Cookie</li>
      * </ul>
      * 
      * @param url
      *            {@link URL} of the request
      * @param httpRequest
      *            http request for the request
      * @param res
      *            sample result to set cookies on
      * @throws IOException
      *             if hostname/ip to use could not be figured out
      */
     protected void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final InetAddress inetAddr = getIpSourceAddress();
     if (inetAddr != null) {// Use special field ip source address (for pseudo 'ip spoofing')
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, inetAddr);
     } else if (localAddress != null){
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
     } else { // reset in case was set previously
         requestParams.removeParameter(ConnRoutePNames.LOCAL_ADDRESS);
     }
 
     int rto = getResponseTimeout();
     if (rto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, rto);
     }
 
     int cto = getConnectTimeout();
     if (cto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, cto);
     }
 
     requestParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, getAutoRedirects());
     
     // a well-behaved browser is supposed to send 'Connection: close'
     // with the last request to an HTTP server. Instead, most browsers
     // leave it to the server to close the connection after their
     // timeout period. Leave it to the JMeter user to decide.
     if (getUseKeepAlive()) {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
     }
 
     setConnectionHeaders(httpRequest, url, getHeaderManager(), getCacheManager());
 
     String cookies = setConnectionCookie(httpRequest, url, getCookieManager());
 
     if (res != null) {
         res.setCookies(cookies);
     }
 
 }
 
     
     /**
      * Set any default request headers to include
      *
      * @param request the HttpRequest to be used
      */
     protected void setDefaultRequestHeaders(HttpRequest request) {
      // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param response
      *            containing the headers
      * @param localContext {@link HttpContext}
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response, HttpContext localContext) {
         Header[] rh = response.getAllHeaders();
 
         StringBuilder headerBuf = new StringBuilder(40 * (rh.length+1));
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (Header responseHeader : rh) {
             writeResponseHeader(headerBuf, responseHeader);
         }
         return headerBuf.toString();
     }
 
     /**
      * Write responseHeader to headerBuffer in an optimized way
      * @param headerBuffer {@link StringBuilder}
      * @param responseHeader {@link Header}
      */
     private void writeResponseHeader(StringBuilder headerBuffer, Header responseHeader) {
         if(responseHeader instanceof BufferedHeader) {
             CharArrayBuffer buffer = ((BufferedHeader)responseHeader).getBuffer();
             headerBuffer.append(buffer.buffer(), 0, buffer.length()).append('\n'); // $NON-NLS-1$
         }
         else {
             headerBuffer.append(responseHeader.getName())
             .append(": ") // $NON-NLS-1$
             .append(responseHeader.getValue())
             .append('\n'); // $NON-NLS-1$
         }
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param request <code>HttpRequest</code> for the request
      * @param url <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     protected String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
     
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param request
      *            <code>HttpRequest</code> which represents the request
      * @param url
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     protected void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                             jMeterProperty.getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = getPortFromHostHeader(v, url.getPort());
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             if (port != -1) {
                                 if (port == url.getDefaultPort()) {
                                     port = -1; // no need to specify the port if it is the default
                                 }
                             }
                             request.getParams().setParameter(ClientPNames.VIRTUAL_HOST, new HttpHost(v, port));
                         } else {
                             request.addHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(url, request);
         }
     }
 
     /**
      * Get port from the value of the Host header, or return the given
      * defaultValue
      *
      * @param hostHeaderValue
      *            value of the http Host header
      * @param defaultValue
      *            value to be used, when no port could be extracted from
      *            hostHeaderValue
      * @return integer representing the port for the host header
      */
     private int getPortFromHostHeader(String hostHeaderValue, int defaultValue) {
         String[] hostParts = hostHeaderValue.split(":");
         if (hostParts.length > 1) {
             String portString = hostParts[hostParts.length - 1];
             if (PORT_PATTERN.matcher(portString).matches()) {
                 return Integer.parseInt(portString);
             }
         }
         return defaultValue;
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         if(method != null) {
             // Get all the request headers
             StringBuilder hdrs = new StringBuilder(150);
             Header[] requestHeaders = method.getAllHeaders();
             for (Header requestHeader : requestHeaders) {
                 // Exclude the COOKIE header, since cookie is reported separately in the sample
                 if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                     writeResponseHeader(hdrs, requestHeader);
                 }
             }
     
             return hdrs.toString();
         }
         return ""; ////$NON-NLS-1$
     }
 
     /**
      * Setup credentials for url AuthScope but keeps Proxy AuthScope credentials
      * @param client HttpClient
      * @param url URL
      * @param authManager {@link AuthManager}
      * @param key key
      */
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager, HttpClientKey key) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             if(authManager.hasAuthForURL(url)) {
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHCAbstractImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHCAbstractImpl.java
index b826aeceb..6f2bee245 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHCAbstractImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHCAbstractImpl.java
@@ -1,168 +1,168 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Common parent class for HttpClient implementations.
  * 
  * Includes system property settings that are handled internally by the Java HTTP implementation,
  * but which need to be explicitly configured in HttpClient implementations. 
  */
 public abstract class HTTPHCAbstractImpl extends HTTPAbstractImpl {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPHCAbstractImpl.class);
 
     protected static final String PROXY_HOST = System.getProperty("http.proxyHost","");
 
     protected static final String NONPROXY_HOSTS = System.getProperty("http.nonProxyHosts","");
 
     protected static final int PROXY_PORT = Integer.parseInt(System.getProperty("http.proxyPort","0"));
 
     protected static final boolean PROXY_DEFINED = PROXY_HOST.length() > 0 && PROXY_PORT > 0;
 
     protected static final String PROXY_USER = JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_USER,"");
 
     protected static final String PROXY_PASS = JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_PASS,"");
 
     protected static final String PROXY_DOMAIN = JMeterUtils.getPropDefault("http.proxyDomain","");
 
     protected static final InetAddress localAddress;
 
     protected static final String LOCALHOST;
 
     protected static final Set<String> nonProxyHostFull = new HashSet<>();
 
     protected static final List<String> nonProxyHostSuffix = new ArrayList<>();
 
     protected static final int NON_PROXY_HOST_SUFFIX_SIZE;
 
     protected static final int CPS_HTTP = JMeterUtils.getPropDefault("httpclient.socket.http.cps", 0);
     
     protected static final int CPS_HTTPS = JMeterUtils.getPropDefault("httpclient.socket.https.cps", 0);
 
     protected static final boolean USE_LOOPBACK = JMeterUtils.getPropDefault("httpclient.loopback", false);
     
     protected static final String HTTP_VERSION = JMeterUtils.getPropDefault("httpclient.version", "1.1");
 
     // -1 means not defined
     protected static final int SO_TIMEOUT = JMeterUtils.getPropDefault("httpclient.timeout", -1);
     
     // Control reuse of cached SSL Context in subsequent iterations
     protected static final boolean USE_CACHED_SSL_CONTEXT = 
             JMeterUtils.getPropDefault("https.use.cached.ssl.context", true);//$NON-NLS-1$
 
     static {
         if(!StringUtils.isEmpty(JMeterUtils.getProperty("httpclient.timeout"))) { //$NON-NLS-1$
             log.warn("You're using property 'httpclient.timeout' that will soon be deprecated for HttpClient3.1, you should either set "
                     + "timeout in HTTP Request GUI, HTTP Request Defaults or set http.socket.timeout in httpclient.parameters");
         }
         if (NONPROXY_HOSTS.length() > 0){
             StringTokenizer s = new StringTokenizer(NONPROXY_HOSTS,"|");// $NON-NLS-1$
             while (s.hasMoreTokens()){
                 String t = s.nextToken();
                 if (t.indexOf('*') ==0){// e.g. *.apache.org // $NON-NLS-1$
                     nonProxyHostSuffix.add(t.substring(1));
                 } else {
                     nonProxyHostFull.add(t);// e.g. www.apache.org
                 }
             }
         }
         NON_PROXY_HOST_SUFFIX_SIZE=nonProxyHostSuffix.size();
 
         InetAddress inet=null;
         String localHostOrIP =
             JMeterUtils.getPropDefault("httpclient.localaddress",""); // $NON-NLS-1$
         if (localHostOrIP.length() > 0){
             try {
                 inet = InetAddress.getByName(localHostOrIP);
                 log.info("Using localAddress "+inet.getHostAddress());
             } catch (UnknownHostException e) {
                 log.warn(e.getLocalizedMessage());
             }
         } else {
             // Get hostname
             localHostOrIP = JMeterUtils.getLocalHostName();
         }
         localAddress = inet;
         LOCALHOST = localHostOrIP;
         log.info("Local host = "+LOCALHOST);
 
     }
 
     protected HTTPHCAbstractImpl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     protected static boolean isNonProxy(String host){
         return nonProxyHostFull.contains(host) || isPartialMatch(host);
     }
 
     protected static boolean isPartialMatch(String host) {
         for (int i=0;i<NON_PROXY_HOST_SUFFIX_SIZE;i++){
             if (host.endsWith(nonProxyHostSuffix.get(i))) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Is a dynamic proxy defined?
      *
      * @param proxyHost the host to check
      * @param proxyPort the port to check
      * @return {@code true} iff both ProxyPort and ProxyHost are defined.
      */
     protected boolean isDynamicProxy(String proxyHost, int proxyPort){
         return !JOrphanUtils.isBlank(proxyHost) && proxyPort > 0;        
     }
 
     /**
      * Is a static proxy defined?
      * 
      * @param host to check against non-proxy hosts
      * @return {@code true} iff a static proxy has been defined.
      */
     protected static boolean isStaticProxy(String host){
         return PROXY_DEFINED && !isNonProxy(host);
     }
     
     /**
      * @param value String value to test
      * @return true if value is null or empty trimmed
      */
     protected static boolean isNullOrEmptyTrimmed(String value) {
         return JOrphanUtils.isBlank(value);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
index 7b31db563..67e007ca9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPJavaImpl.java
@@ -1,651 +1,651 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.BindException;
 import java.net.HttpURLConnection;
 import java.net.InetSocketAddress;
 import java.net.Proxy;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.List;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.io.input.CountingInputStream;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * A sampler which understands all the parts necessary to read statistics about
  * HTTP requests, including cookies and authentication.
  *
  */
 public class HTTPJavaImpl extends HTTPAbstractImpl {
     private static final boolean OBEY_CONTENT_LENGTH =
         JMeterUtils.getPropDefault("httpsampler.obey_contentlength", false); // $NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPJavaImpl.class);
 
     private static final int MAX_CONN_RETRIES =
         JMeterUtils.getPropDefault("http.java.sampler.retries" // $NON-NLS-1$
                 ,0); // Maximum connection retries
 
     static {
         log.info("Maximum connection retries = "+MAX_CONN_RETRIES); // $NON-NLS-1$
         // Temporary copies, so can set the final ones
     }
 
     private static final byte[] NULL_BA = new byte[0];// can share these
 
     /** Handles writing of a post or put request */
     private transient PostWriter postOrPutWriter;
 
     private volatile HttpURLConnection savedConn;
 
     protected HTTPJavaImpl(HTTPSamplerBase base) {
         super(base);
     }
 
     /**
      * Set request headers in preparation to opening a connection.
      *
      * @param conn
      *            <code>URLConnection</code> to set headers on
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected void setPostHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PostWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     private void setPutHeaders(URLConnection conn) throws IOException {
         postOrPutWriter = new PutWriter();
         postOrPutWriter.setHeaders(conn, testElement);
     }
 
     /**
      * Send POST data from <code>Entry</code> to the open connection.
      * This also handles sending data for PUT requests
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected String sendPostData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     private String sendPutData(URLConnection connection) throws IOException {
         return postOrPutWriter.sendPostData(connection, testElement);
     }
 
     /**
      * Returns an <code>HttpURLConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param method
      *            GET, POST etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpURLConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpURLConnection setupConnection(URL u, String method, HTTPSampleResult res) throws IOException {
         SSLManager sslmgr = null;
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 sslmgr=SSLManager.getInstance(); // N.B. this needs to be done before opening the connection
             } catch (Exception e) {
                 log.warn("Problem creating the SSLManager: ", e);
             }
         }
 
         final HttpURLConnection conn;
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
         if (proxyHost.length() > 0 && proxyPort > 0){
             Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
             //TODO - how to define proxy authentication for a single connection?
             // It's not clear if this is possible
 //            String user = getProxyUser();
 //            if (user.length() > 0){
 //                Authenticator auth = new ProxyAuthenticator(user, getProxyPass());
 //            }
             conn = (HttpURLConnection) u.openConnection(proxy);
         } else {
             conn = (HttpURLConnection) u.openConnection();
         }
 
         // Update follow redirects setting just for this connection
         conn.setInstanceFollowRedirects(getAutoRedirects());
 
         int cto = getConnectTimeout();
         if (cto > 0){
             conn.setConnectTimeout(cto);
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             conn.setReadTimeout(rto);
         }
 
         if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
             try {
                 if (null != sslmgr){
                     sslmgr.setContext(conn); // N.B. must be done after opening connection
                 }
             } catch (Exception e) {
                 log.warn("Problem setting the SSLManager for the connection: ", e);
             }
         }
 
         // a well-behaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
         } else {
             conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
         }
 
         conn.setRequestMethod(method);
         setConnectionHeaders(conn, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(conn, u, getCookieManager());
 
         setConnectionAuthorization(conn, u, getAuthManager());
 
         if (method.equals(HTTPConstants.POST)) {
             setPostHeaders(conn);
         } else if (method.equals(HTTPConstants.PUT)) {
             setPutHeaders(conn);
         }
 
         if (res != null) {
             res.setRequestHeaders(getConnectionHeaders(conn));
             res.setCookies(cookies);
         }
 
         return conn;
     }
 
     /**
      * Reads the response from the URL connection.
      *
      * @param conn
      *            URL from which to read response
      * @param res
      *            {@link SampleResult} to read response into
      * @return response content
      * @exception IOException
      *                if an I/O exception occurs
      */
     protected byte[] readResponse(HttpURLConnection conn, SampleResult res) throws IOException {
         BufferedInputStream in;
 
         final long contentLength = conn.getContentLength();
         if ((contentLength == 0)
             && OBEY_CONTENT_LENGTH) {
             log.info("Content-Length: 0, not reading http-body");
             res.setResponseHeaders(getResponseHeaders(conn));
             res.latencyEnd();
             return NULL_BA;
         }
 
         // works OK even if ContentEncoding is null
         boolean gzipped = HTTPConstants.ENCODING_GZIP.equals(conn.getContentEncoding());
         InputStream instream = null;
         try {
             instream = new CountingInputStream(conn.getInputStream());
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(instream));
             } else {
                 in = new BufferedInputStream(instream);
             }
         } catch (IOException e) {
             if (! (e.getCause() instanceof FileNotFoundException))
             {
                 log.error("readResponse: "+e.toString());
                 Throwable cause = e.getCause();
                 if (cause != null){
                     log.error("Cause: "+cause);
                     if(cause instanceof Error) {
                         throw (Error)cause;
                     }
                 }
             }
             // Normal InputStream is not available
             InputStream errorStream = conn.getErrorStream();
             if (errorStream == null) {
                 log.info("Error Response Code: "+conn.getResponseCode()+", Server sent no Errorpage");
                 res.setResponseHeaders(getResponseHeaders(conn));
                 res.latencyEnd();
                 return NULL_BA;
             }
 
             log.info("Error Response Code: "+conn.getResponseCode());
 
             if (gzipped) {
                 in = new BufferedInputStream(new GZIPInputStream(errorStream));
             } else {
                 in = new BufferedInputStream(errorStream);
             }
         } catch (Exception e) {
             log.error("readResponse: "+e.toString());
             Throwable cause = e.getCause();
             if (cause != null){
                 log.error("Cause: "+cause);
                 if(cause instanceof Error) {
                     throw (Error)cause;
                 }
             }
             in = new BufferedInputStream(conn.getErrorStream());
         }
         // N.B. this closes 'in'
         byte[] responseData = readResponse(res, in, contentLength);
         if (instream != null) {
             res.setBodySize(((CountingInputStream) instream).getByteCount());
             instream.close();
         }
         return responseData;
     }
 
     /**
      * Gets the ResponseHeaders from the URLConnection
      *
      * @param conn
      *            connection from which the headers are read
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpURLConnection conn) {
         StringBuilder headerBuf = new StringBuilder();
         headerBuf.append(conn.getHeaderField(0));// Leave header as is
         // headerBuf.append(conn.getHeaderField(0).substring(0, 8));
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseCode());
         // headerBuf.append(" ");
         // headerBuf.append(conn.getResponseMessage());
         headerBuf.append("\n"); //$NON-NLS-1$
 
         String hfk;
         for (int i = 1; (hfk=conn.getHeaderFieldKey(i)) != null; i++) {
             headerBuf.append(hfk);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(conn.getHeaderField(i));
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private String setConnectionCookie(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required headers for that particular URL request and
      * sets them in the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpURLConnection conn, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Add all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     Header header = (Header) jMeterProperty.getObjectValue();
                     String n = header.getName();
                     String v = header.getValue();
                     conn.addRequestProperty(n, v);
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(conn, u);
         }
     }
 
     /**
      * Get all the headers for the <code>HttpURLConnection</code> passed in
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpURLConnection conn) {
         // Get all the request properties, which are the headers set on the connection
         StringBuilder hdrs = new StringBuilder(100);
         Map<String, List<String>> requestHeaders = conn.getRequestProperties();
         for(Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
             String headerKey=entry.getKey();
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(headerKey)) {
                 // value is a List of Strings
                 for (String value : entry.getValue()){
                     hdrs.append(headerKey);
                     hdrs.append(": "); // $NON-NLS-1$
                     hdrs.append(value);
                     hdrs.append("\n"); // $NON-NLS-1$
                 }
             }
         }
         return hdrs.toString();
     }
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpURLConnection</code> passed in.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the cookies for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpURLConnection conn, URL u, AuthManager authManager) {
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                 conn.setRequestProperty(HTTPConstants.HEADER_AUTHORIZATION, auth.toBasicHeader());
             }
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
         HttpURLConnection conn = null;
 
         String urlStr = url.toString();
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = new HTTPSampleResult();
 
         res.setSampleLabel(urlStr);
         res.setURL(url);
         res.setHTTPMethod(method);
 
         res.sampleStart(); // Count the retries as well in the time
 
         // Check cache for an entry with an Expires header in the future
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                return updateSampleResultForResourceInCache(res);
            }
         }
 
         try {
             // Sampling proper - establish the connection and read the response:
             // Repeatedly try to connect:
             int retry = -1;
             // Start with -1 so tries at least once, and retries at most MAX_CONN_RETRIES times
             for (; retry < MAX_CONN_RETRIES; retry++) {
                 try {
                     conn = setupConnection(url, method, res);
                     // Attempt the connection:
                     savedConn = conn;
                     conn.connect();
                     break;
                 } catch (BindException e) {
                     if (retry >= MAX_CONN_RETRIES) {
                         log.error("Can't connect after "+retry+" retries, "+e);
                         throw e;
                     }
                     log.debug("Bind exception, try again");
                     if (conn!=null) {
                         savedConn = null; // we don't want interrupt to try disconnection again
                         conn.disconnect();
                     }
                     setUseKeepAlive(false);
                 } catch (IOException e) {
                     log.debug("Connection failed, giving up");
                     throw e;
                 }
             }
             if (retry > MAX_CONN_RETRIES) {
                 // This should never happen, but...
                 throw new BindException();
             }
             // Nice, we've got a connection. Finish sending the request:
             if (method.equals(HTTPConstants.POST)) {
                 String postBody = sendPostData(conn);
                 res.setQueryString(postBody);
             } else if (method.equals(HTTPConstants.PUT)) {
                 String putBody = sendPutData(conn);
                 res.setQueryString(putBody);
             }
             // Request sent. Now get the response:
             byte[] responseData = readResponse(conn, res);
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setResponseData(responseData);
 
             int errorLevel = conn.getResponseCode();
             String respMsg = conn.getResponseMessage();
             String hdr=conn.getHeaderField(0);
             if (hdr == null) {
                 hdr="(null)";  // $NON-NLS-1$
             }
             if (errorLevel == -1){// Bug 38902 - sometimes -1 seems to be returned unnecessarily
                 if (respMsg != null) {// Bug 41902 - NPE
                     try {
                         errorLevel = Integer.parseInt(respMsg.substring(0, 3));
                         log.warn("ResponseCode==-1; parsed "+respMsg+ " as "+errorLevel);
                       } catch (NumberFormatException e) {
                         log.warn("ResponseCode==-1; could not parse "+respMsg+" hdr: "+hdr);
                       }
                 } else {
                     respMsg=hdr; // for result
                     log.warn("ResponseCode==-1 & null ResponseMessage. Header(0)= "+hdr);
                 }
             }
             if (errorLevel == -1) {
                 res.setResponseCode("(null)"); // $NON-NLS-1$
             } else {
                 res.setResponseCode(Integer.toString(errorLevel));
             }
             res.setSuccessful(isSuccessCode(errorLevel));
 
             if (respMsg == null) {// has been seen in a redirect
                 respMsg=hdr; // use header (if possible) if no message found
             }
             res.setResponseMessage(respMsg);
 
             String ct = conn.getContentType();
             if (ct != null){
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             String responseHeaders = getResponseHeaders(conn);
             res.setResponseHeaders(responseHeaders);
             if (res.isRedirect()) {
                 res.setRedirectLocation(conn.getHeaderField(HTTPConstants.HEADER_LOCATION));
             }
             
             // record headers size to allow HTTPSampleResult.getBytes() with different options
             res.setHeadersSize(responseHeaders.replaceAll("\n", "\r\n") // $NON-NLS-1$ $NON-NLS-2$
                     .length() + 2); // add 2 for a '\r\n' at end of headers (before data) 
             if (log.isDebugEnabled()) {
                 log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySizeAsLong()
                         + " Total=" + (res.getHeadersSize() + res.getBodySizeAsLong()));
             }
             
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(conn.getURL());
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(conn, url, getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(conn, res);
             }
 
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IOException e) {
             res.sampleEnd();
             savedConn = null; // we don't want interrupt to try disconnection again
             // We don't want to continue using this connection, even if KeepAlive is set
             if (conn != null) { // May not exist
                 conn.disconnect();
             }
             conn=null; // Don't process again
             return errorResult(e, res);
         } finally {
             // calling disconnect doesn't close the connection immediately,
             // but indicates we're through with it. The JVM should close
             // it when necessary.
             savedConn = null; // we don't want interrupt to try disconnection again
             disconnect(conn); // Disconnect unless using KeepAlive
         }
     }
 
     protected void disconnect(HttpURLConnection conn) {
         if (conn != null) {
             String connection = conn.getHeaderField(HTTPConstants.HEADER_CONNECTION);
             String protocol = conn.getHeaderField(0);
             if ((connection == null && (protocol == null || !protocol.startsWith(HTTPConstants.HTTP_1_1)))
                     || (connection != null && connection.equalsIgnoreCase(HTTPConstants.CONNECTION_CLOSE))) {
                 conn.disconnect();
             } // TODO ? perhaps note connection so it can be disconnected at end of test?
         }
     }
 
     /**
      * From the <code>HttpURLConnection</code>, store all the "set-cookie"
      * key-pair values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param conn
      *            <code>HttpUrlConnection</code> which represents the URL
      *            request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      */
     private void saveConnectionCookies(HttpURLConnection conn, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             for (int i = 1; conn.getHeaderFieldKey(i) != null; i++) {
                 if (conn.getHeaderFieldKey(i).equalsIgnoreCase(HTTPConstants.HEADER_SET_COOKIE)) {
                     cookieManager.addCookieFromHeader(conn.getHeaderField(i), u);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         HttpURLConnection conn = savedConn;
         if (conn != null) {
             savedConn = null;
             conn.disconnect();
         }
         return conn != null;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index e1d08ee89..27f60b1dc 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1102 +1,1102 @@
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.gui.Replaceable;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.BaseParser;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParseException;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParser;
 import org.apache.jmeter.protocol.http.sampler.ResourcesDownloader.AsynSamplerResultHolder;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.DirectAccessByteArrayOutputStream;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface,
         Replaceable {
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPSamplerBase.class);
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.control.DNSCacheManager",
                     "org.apache.jmeter.protocol.http.gui.DNSCachePanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"
             ));
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DNS_CACHE_MANAGER = "HTTPSampler.dns_cache_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     /** This is the encoding used for the content, i.e. the charset name, not the header "Content-Encoding" */
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = HTTPConstants.PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String IP_SOURCE_TYPE = "HTTPSampler.ipSourceType"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
 
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
 
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     public static final int CONCURRENT_POOL_SIZE = 6; // Default concurrent pool size for download embedded resources
 
     private static final String CONCURRENT_POOL_DEFAULT = Integer.toString(CONCURRENT_POOL_SIZE); // default for concurrent pool
 
     private static final String USER_AGENT = "User-Agent"; // $NON-NLS-1$
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
 
     private static final int MAX_BYTES_TO_STORE_PER_REQUEST =
             JMeterUtils.getPropDefault("httpsampler.max_bytes_to_store_per_request", 10 * 1024 *1024); // $NON-NLS-1$ // default value: 10MB
 
     private static final int MAX_BUFFER_SIZE = 
             JMeterUtils.getPropDefault("httpsampler.max_buffer_size", 65 * 1024); // $NON-NLS-1$
 
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES =
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     private static final boolean IGNORE_EMBEDDED_RESOURCES_DATA =
             JMeterUtils.getPropDefault("httpsampler.embedded_resources_use_md5", false); // $NON-NLS-1$ // default value: false
 
     public static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
 
     private static final List<String> METHODLIST;
     static {
         List<String> defaultMethods = new ArrayList<>(Arrays.asList(
             DEFAULT_METHOD, // i.e. GET
             HTTPConstants.POST,
             HTTPConstants.HEAD,
             HTTPConstants.PUT,
             HTTPConstants.OPTIONS,
             HTTPConstants.TRACE,
             HTTPConstants.DELETE,
             HTTPConstants.PATCH,
             HTTPConstants.PROPFIND,
             HTTPConstants.PROPPATCH,
             HTTPConstants.MKCOL,
             HTTPConstants.COPY,
             HTTPConstants.MOVE,
             HTTPConstants.LOCK,
             HTTPConstants.UNLOCK,
             HTTPConstants.REPORT,
             HTTPConstants.MKCALENDAR,
             HTTPConstants.SEARCH
         ));
         String userDefinedMethods = JMeterUtils.getPropDefault(
                 "httpsampler.user_defined_methods", "");
         if (StringUtils.isNotBlank(userDefinedMethods)) {
             defaultMethods.addAll(Arrays.asList(userDefinedMethods.split("\\s*,\\s*")));
         }
         METHODLIST = Collections.unmodifiableList(defaultMethods);
     }
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private static final String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     public static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw";
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 20); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> PARSERS_FOR_CONTENT_TYPE = new HashMap<>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS = // list of parsers
             JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
     
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT =
             JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
     
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER =
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
     static {
         String[] parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (final String parser : parsers) {
             String classname = JMeterUtils.getProperty(parser + ".className");//$NON-NLS-1$
             if (classname == null) {
                 log.error("Cannot find .className property for " + parser+", ensure you set property:'" + parser + ".className'");
                 continue;
             }
             String typeList = JMeterUtils.getProperty(parser + ".types");//$NON-NLS-1$
             if (typeList != null) {
                 String[] types = JOrphanUtils.split(typeList, " ", true);
                 for (final String type : types) {
                     log.info("Parser for " + type + " is " + classname);
                     PARSERS_FOR_CONTENT_TYPE.put(type, classname);
                 }
             } else {
                 log.warn("Cannot find .types property for " + parser
                         + ", as a consequence parser will not be used, to make it usable, define property:'"
                         + parser + ".types'");
             }
         }
     }
     
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     public enum SourceType {
         HOSTNAME("web_testing_source_ip_hostname"), //$NON-NLS-1$
         DEVICE("web_testing_source_ip_device"), //$NON-NLS-1$
         DEVICE_IPV4("web_testing_source_ip_device_ipv4"), //$NON-NLS-1$
         DEVICE_IPV6("web_testing_source_ip_device_ipv6"); //$NON-NLS-1$
 
         public final String propertyName;
         SourceType(String propertyName) {
             this.propertyName = propertyName;
         }
     }
 
     // Use for ComboBox Source Address Type. Preserve order (specially with localization)
     public static String[] getSourceTypeList() {
         final SourceType[] types = SourceType.values();
         final String[] displayStrings = new String[types.length];
         for(int i = 0; i < types.length; i++) {
             displayStrings[i] = JMeterUtils.getResString(types[i].propertyName);
         }
         return displayStrings;
     }
     /**
      * Determine if the file should be sent as the entire Content body,
      * i.e. without any additional wrapping.
      *
      * @return true if specified file is to be sent as the body,
      * i.e. there is a single file entry which has a non-empty path and
      * an empty Parameter name.
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
                 && (files[0].getPath().length() > 0)
                 && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that is the case,
      * it means that the parameter values should be sent as the entity body
      *
      * @return {@code true} if there are parameters and none of these have a
      *         name specified, or {@link HTTPSamplerBase#getPostBodyRaw()} returns
      *         {@code true}
      */
     public boolean getSendParameterValuesAsPostBody() {
         if (getPostBodyRaw()) {
             return true;
         } else {
             boolean hasArguments = false;
             for (JMeterProperty jMeterProperty : getArguments()) {
                 hasArguments = true;
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 if (arg.getName() != null && arg.getName().length() > 0) {
                     return false;
                 }
             }
             return hasArguments;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost() {
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         return HTTPConstants.POST.equals(getMethod())
                 && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()));
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; if the request is a GET or DELETE (and the path
      * does not start with http[s]://) it also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         boolean fullUrl = path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX);
         boolean getOrDelete = HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod());
         if (!fullUrl && getOrDelete) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     /**
      * Sets the value of the encoding to be used for the content.
      *
      * @param charsetName the name of the encoding to be used
      */
     public void setContentEncoding(String charsetName) {
         setProperty(CONTENT_ENCODING, charsetName);
     }
 
     /**
      *
      * @return the encoding of the content, i.e. its charset name
      */
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     /**
      * @return boolean 
      * @deprecated since 3.2 always returns false
      */
     @Deprecated
     public String getMonitor() {
         return "false";
     }
 
     /**
      * @return boolean
      * @deprecated since 3.2 always returns false
      */
     @Deprecated
     public boolean isMonitor() {
         return false;
     }
     
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
     public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
      *
      * @param name name of the argument
      * @param value value of the argument
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      *
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()) {
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if (nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         } else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded;
         if (nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             } catch (UnsupportedEncodingException e) { // NOSONAR 
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         } else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName())
                 && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else if (el instanceof DNSCacheManager) {
             setDNSResolver((DNSCacheManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren() {
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol, int port) {
         if (port == URL_UNSPECIFIED_PORT) {
             if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)) {
                 return HTTPConstants.DEFAULT_HTTP_PORT;
             } else if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String portAsString = getPropertyAsString(PORT);
         if(portAsString == null || portAsString.isEmpty()) {
             return UNSPECIFIED_PORT;
         }
         
         try {
             return Integer.parseInt(portAsString.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         boolean isDefaultHTTPPort = HTTPConstants.PROTOCOL_HTTP
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTP_PORT;
         boolean isDefaultHTTPSPort = HTTPConstants.PROTOCOL_HTTPS
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTPS_PORT;
         return port == UNSPECIFIED_PORT ||
                 isDefaultHTTPPort ||
                 isDefaultHTTPSPort;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: " + prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     // gets called from ctor, so has to be final
     public final void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(final HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         HeaderManager lValue = value;
         if (mgr != null) {
             lValue = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + lValue.getName() + "'");
                 for (int i = 0; i < lValue.getHeaders().size(); i++) {
                     log.debug("    " + lValue.getHeader(i).getName() + "=" + lValue.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, lValue));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCookieManagerProperty(CookieManager value) {
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCookieManagerProperty(value);
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCacheManagerProperty(CacheManager value) {
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCacheManagerProperty(value);
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public DNSCacheManager getDNSResolver() {
         return (DNSCacheManager) getProperty(DNS_CACHE_MANAGER).getObjectValue();
     }
 
     public void setDNSResolver(DNSCacheManager cacheManager) {
         DNSCacheManager mgr = getDNSResolver();
         if (mgr != null) {
             log.warn("Existing DNSCacheManager " + mgr.getName() + " superseded by " + cacheManager.getName());
         }
         setProperty(new TestElementProperty(DNS_CACHE_MANAGER, cacheManager));
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE, "");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel(res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text)); // NOSONAR Stacktrace will be used in the response
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": " + e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": " + e.getMessage());
         res.setSuccessful(false);
         return res;
     }
 
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException if url is malformed
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
                 || path.startsWith(HTTPS_PREFIX)) {
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain = null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")) { // $NON-NLS-1$
                 pathAndQuery.append('/'); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if (queryString.length() > 0) {
                 if (path.contains(QRY_PFX)) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if (isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(final String contentEncoding) {
         
         CollectionProperty arguments = getArguments().getArguments();
         // Optimisation : avoid building useless objects if empty arguments
         if(arguments.size() == 0) {
             return "";
         }
         String lContentEncoding = contentEncoding;
         // Check if the sampler has a specified content encoding
         if (JOrphanUtils.isBlank(lContentEncoding)) {
             // We use the encoding which should be used according to the HTTP spec, which is UTF-8
             lContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
         }
         
         StringBuilder buf = new StringBuilder(arguments.size() * 15);
         PropertyIterator iter = arguments.iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) { // NOSONAR
                 log.warn("Unexpected argument type: " + objectValue.getClass().getName() +" cannot be cast to HTTPArgument");
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(lContentEncoding));
             } catch(UnsupportedEncodingException e) { // NOSONAR
                 log.warn("Unable to encode parameter in encoding " + lContentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string;
      *            if non-null then it is used to decode the
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         final boolean isDebug = log.isDebugEnabled();
         for (String arg : args) {
             if (isDebug) {
                 log.debug("Arg: " + arg);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpClientDefaultParameters.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpClientDefaultParameters.java
index dfe156635..c605faae0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpClientDefaultParameters.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpClientDefaultParameters.java
@@ -1,161 +1,161 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Map;
 import java.util.Properties;
 
 import org.apache.jmeter.NewDriver;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /*
  * Utility class to set up default HttpClient parameters from a file.
  * 
  * Supports both Commons HttpClient and Apache HttpClient.
  * 
  */
 public class HttpClientDefaultParameters {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HttpClientDefaultParameters.class);
 
     // Non-instantiable
     private HttpClientDefaultParameters(){
     }
 
     // Helper class (callback) for applying parameter definitions
     private static abstract class GenericHttpParams {
         public abstract void setParameter(String name, Object value);
         public abstract void setVersion(String name, String value) throws Exception;
     }
 
     /**
      * Loads a property file and converts parameters as necessary.
      * 
      * @param file the file to load
      * @param params Commons HttpClient parameter instance
      * @deprecated HC3.1 will be dropped in upcoming version
      */
     @Deprecated
     public static void load(String file, 
             final org.apache.commons.httpclient.params.HttpParams params){
         load(file, 
                 new GenericHttpParams (){
                     @Override
                     public void setParameter(String name, Object value) {
                         params.setParameter(name, value);
                     }
                     @Override
                     public void setVersion(String name, String value) throws Exception {
                         params.setParameter(name,
                         org.apache.commons.httpclient.HttpVersion.parse("HTTP/"+value));
                     }            
                 }
             );
     }
 
     /**
      * Loads a property file and converts parameters as necessary.
      * 
      * @param file the file to load
      * @param params Apache HttpClient parameter instance
      */
     public static void load(String file, 
             final org.apache.http.params.HttpParams params){
         load(file, 
                 new GenericHttpParams (){
                     @Override
                     public void setParameter(String name, Object value) {
                         params.setParameter(name, value);
                     }
 
                     @Override
                     public void setVersion(String name, String value) {
                         String[] parts = value.split("\\.");
                         if (parts.length != 2){
                             throw new IllegalArgumentException("Version must have form m.n");
                         }
                         params.setParameter(name,
                                 new org.apache.http.HttpVersion(
                                         Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
                     }            
                 }
             );
     }
 
     private static void load(String file, GenericHttpParams params){
         log.info("Trying httpclient parameters from "+file);
         File f = new File(file);        
         if(! (f.exists() && f.canRead())) {
             f = new File(NewDriver.getJMeterDir() + File.separator
                     + "bin" + File.separator + file); // $NON-NLS-1$
             log.info(file + " httpclient parameters does not exist, trying "+f.getAbsolutePath());
             if(! (f.exists() && f.canRead())) {
                 log.error("Cannot read parameters file for HttpClient: "+ file);
                 return;
             }
         }
         log.info("Reading httpclient parameters from "+f.getAbsolutePath());
         InputStream is = null;
         Properties props = new Properties();
         try {
             is = new FileInputStream(f);
             props.load(is);
             for (Map.Entry<Object, Object> me : props.entrySet()){
                 String key = (String) me.getKey();
                 String value = (String)me.getValue();
                 int typeSep = key.indexOf('$'); // $NON-NLS-1$
                 try {
                     if (typeSep > 0){
                         String type = key.substring(typeSep+1);// get past separator
                         String name=key.substring(0,typeSep);
                         log.info("Defining "+name+ " as "+value+" ("+type+")");
                         if (type.equals("Integer")){
                             params.setParameter(name, Integer.valueOf(value));
                         } else if (type.equals("Long")){
                             params.setParameter(name, Long.valueOf(value));
                         } else if (type.equals("Boolean")){
                             params.setParameter(name, Boolean.valueOf(value));
                         } else if (type.equals("HttpVersion")){ // Commons HttpClient only
                             params.setVersion(name, value);
                         } else {
                             log.warn("Unexpected type: "+type+" for name "+name);
                         }
                     } else {
                             log.info("Defining "+key+ " as "+value);
                             params.setParameter(key, value);
                     }
                 } catch (Exception e) {
                     log.error("Error in property: "+key+"="+value+" "+e.toString());
                 }
             }
         } catch (IOException e) {
             log.error("Problem loading properties "+e.toString());
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/LazySchemeSocketFactory.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/LazySchemeSocketFactory.java
index e547eb444..522bf89e4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/LazySchemeSocketFactory.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/LazySchemeSocketFactory.java
@@ -1,123 +1,123 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.IOException;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.UnknownHostException;
 import java.security.GeneralSecurityException;
 
 import org.apache.http.conn.ConnectTimeoutException;
 import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
 import org.apache.http.conn.ssl.SSLInitializationException;
 import org.apache.http.conn.ssl.SSLSocketFactory;
 import org.apache.http.params.HttpParams;
 import org.apache.jmeter.protocol.http.util.HC4TrustAllSSLSocketFactory;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Lazy SchemeSocketFactory that lazily initializes HTTPS Socket Factory
  * @since 3.0
  */
 public final class LazySchemeSocketFactory implements SchemeLayeredSocketFactory{
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(LazySchemeSocketFactory.class);
 
     private static class AdapteeHolder { // IODH idiom
         private static final SchemeLayeredSocketFactory ADAPTEE = checkAndInit();  
 
         /**
          * @throws SSLInitializationException
          */
         private static SchemeLayeredSocketFactory checkAndInit() throws SSLInitializationException {
             LOG.info("Setting up HTTPS TrustAll Socket Factory");
             try {
                 return new HC4TrustAllSSLSocketFactory();
             } catch (GeneralSecurityException e) {
                 LOG.warn("Failed to initialise HTTPS HC4TrustAllSSLSocketFactory", e);
                 return SSLSocketFactory.getSocketFactory();
             }
         }
 
         static SchemeLayeredSocketFactory getINSTANCE() {
             return ADAPTEE;
         }
     }
     
     /**
      * 
      */
     public LazySchemeSocketFactory() {
         super();
     }
     
     /**
      * @param params {@link HttpParams}
      * @return the socket
      * @throws IOException
      * @see org.apache.http.conn.scheme.SchemeSocketFactory#createSocket(org.apache.http.params.HttpParams)
      */
     @Override
     public Socket createSocket(HttpParams params) throws IOException {
         return AdapteeHolder.getINSTANCE().createSocket(params);
     }
     
     /**
      * @param sock {@link Socket}
      * @param remoteAddress {@link InetSocketAddress}
      * @param localAddress {@link InetSocketAddress}
      * @param params {@link HttpParams}
      * @return the socket
      * @throws IOException
      * @throws UnknownHostException
      * @throws ConnectTimeoutException
      * @see org.apache.http.conn.scheme.SchemeSocketFactory#connectSocket(java.net.Socket, java.net.InetSocketAddress, java.net.InetSocketAddress, org.apache.http.params.HttpParams)
      */
     @Override
     public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress,
             InetSocketAddress localAddress, HttpParams params)
             throws IOException, UnknownHostException, ConnectTimeoutException {
         return AdapteeHolder.getINSTANCE().connectSocket(sock, remoteAddress, localAddress, params);
     }
     
     /**
      * @param sock {@link Socket}
      * @return true if the socket is secure
      * @throws IllegalArgumentException
      * @see org.apache.http.conn.scheme.SchemeSocketFactory#isSecure(java.net.Socket)
      */
     @Override
     public boolean isSecure(Socket sock) throws IllegalArgumentException {
         return AdapteeHolder.getINSTANCE().isSecure(sock);
     }
 
     /**
      * @param socket {@link Socket}
      * @param target {@link String}  
      * @param port int port of socket
      * @param params {@link HttpParams}
      * @return the socket
      */
     @Override
     public Socket createLayeredSocket(Socket socket, String target, int port,
             HttpParams params) throws IOException, UnknownHostException {
         return AdapteeHolder.getINSTANCE().createLayeredSocket(socket, target, port, params);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java
index fe67d2839..31ed2e8e9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java
@@ -1,240 +1,240 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CompletionService;
 import java.util.concurrent.ExecutorCompletionService;
 import java.util.concurrent.Future;
 import java.util.concurrent.SynchronousQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Manages the parallel http resources download.<br>
  * A shared thread pool is used by all the sample.<br>
  * A sampler will usually do the following
  * <pre> {@code 
  *   // list of AsynSamplerResultHolder to download
  *   List<Callable<AsynSamplerResultHolder>> list = ...
  *   
  *   // max parallel downloads
  *   int maxConcurrentDownloads = ...
  *   
  *   // get the singleton instance
  *   ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
  *   
  *   // schedule the downloads and wait for the completion
  *   List<Future<AsynSamplerResultHolder>> retExec = resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
  *   
  * }</pre>
  * 
  * the call to invokeAllAndAwaitTermination will block until the downloads complete or get interrupted<br>
  * the Future list only contains task that have been scheduled in the threadpool.<br>
  * The status of those futures are either done or cancelled<br>
  * <br>
  *  
  *  Future enhancements :
  *  <ul>
  *  <li>this implementation should be replaced with a NIO async download
  *   in order to reduce the number of threads needed</li>
  *  </ul>
  * @since 3.0
  */
 public class ResourcesDownloader {
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(ResourcesDownloader.class);
     
     /** this is the maximum time that excess idle threads will wait for new tasks before terminating */
     private static final long THREAD_KEEP_ALIVE_TIME = JMeterUtils.getPropDefault("httpsampler.parallel_download_thread_keepalive_inseconds", 60L);
     
     private static final int MIN_POOL_SIZE = 1;
     private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
     
     private static final ResourcesDownloader INSTANCE = new ResourcesDownloader();
     
     public static ResourcesDownloader getInstance() {
         return INSTANCE;
     }
     
     
     private ThreadPoolExecutor concurrentExecutor = null;
 
     private ResourcesDownloader() {
         init();
     }
     
     
     private void init() {
         LOG.info("Creating ResourcesDownloader with keepalive_inseconds:"+THREAD_KEEP_ALIVE_TIME);
         concurrentExecutor = new ThreadPoolExecutor(
                 MIN_POOL_SIZE, MAX_POOL_SIZE, THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                 new SynchronousQueue<>(),
                 r -> {
                     Thread t = new Thread(r);
                     t.setName("ResDownload-" + t.getName()); //$NON-NLS-1$
                     t.setDaemon(true);
                     return t;
                 }) {
 
         };
     }
     
     /**
      * this method will try to shrink the thread pool size as much as possible
      * it should be called at the end of a test
      */
     public void shrink() {
         if(concurrentExecutor.getPoolSize() > MIN_POOL_SIZE) {
             // drain the queue
             concurrentExecutor.purge();
             List<Runnable> drainList = new ArrayList<>();
             concurrentExecutor.getQueue().drainTo(drainList);
             if(!drainList.isEmpty()) {
                 LOG.warn("the pool executor workqueue is not empty size=" + drainList.size());
                 for (Runnable runnable : drainList) {
                     if(runnable instanceof Future<?>) {
                         Future<?> f = (Future<?>) runnable;
                         f.cancel(true);
                     }
                     else {
                         LOG.warn("Content of workqueue is not an instance of Future");
                     }
                 }
             }
             
             // this will force the release of the extra threads that are idle
             // the remaining extra threads will be released with the keepAliveTime of the thread
             concurrentExecutor.setMaximumPoolSize(MIN_POOL_SIZE);
             
             // do not immediately restore the MaximumPoolSize as it will block the release of the threads
         }
     }
     
     // probablyTheBestMethodNameInTheUniverseYeah!
     /**
      * This method will block until the downloads complete or it get interrupted
      * the Future list returned by this method only contains tasks that have been scheduled in the threadpool.<br>
      * The status of those futures are either done or cancelled
      * 
      * @param maxConcurrentDownloads max concurrent downloads
      * @param list list of resources to download
      * @return list tasks that have been scheduled
      * @throws InterruptedException when interrupted while waiting
      */
     public List<Future<AsynSamplerResultHolder>> invokeAllAndAwaitTermination(int maxConcurrentDownloads, List<Callable<AsynSamplerResultHolder>> list) throws InterruptedException {
         List<Future<AsynSamplerResultHolder>> submittedTasks = new ArrayList<>();
         
         // paranoid fast path
         if(list.isEmpty()) {
             return submittedTasks;
         }
         
         // restore MaximumPoolSize original value
         concurrentExecutor.setMaximumPoolSize(MAX_POOL_SIZE);
         
         if(LOG.isDebugEnabled()) {
             LOG.debug("PoolSize=" + concurrentExecutor.getPoolSize()+" LargestPoolSize=" + concurrentExecutor.getLargestPoolSize());
         }
         
         CompletionService<AsynSamplerResultHolder> completionService = new ExecutorCompletionService<>(concurrentExecutor);
         int remainingTasksToTake = list.size();
         
         try {
             // push the task in the threadpool until <maxConcurrentDownloads> is reached
             int i = 0;
             for (i = 0; i < Math.min(maxConcurrentDownloads, list.size()); i++) {
                 Callable<AsynSamplerResultHolder> task = list.get(i);
                 submittedTasks.add(completionService.submit(task));
             }
             
             // push the remaining tasks but ensure we use at most <maxConcurrentDownloads> threads
             // wait for a previous download to finish before submitting a new one
             for (; i < list.size(); i++) {
                 Callable<AsynSamplerResultHolder> task = list.get(i);
                 completionService.take();
                 remainingTasksToTake--;
                 submittedTasks.add(completionService.submit(task));
             }
             
             // all the resources downloads are in the thread pool queue
             // wait for the completion of all downloads
             while (remainingTasksToTake > 0) {
                 completionService.take();
                 remainingTasksToTake--;
             }
         }
         finally {
             //bug 51925 : Calling Stop on Test leaks executor threads when concurrent download of resources is on
             if(remainingTasksToTake > 0) {
                 if(LOG.isDebugEnabled()) {
                     LOG.debug("Interrupted while waiting for resource downloads : cancelling remaining tasks");
                 }
                 for (Future<AsynSamplerResultHolder> future : submittedTasks) {
                     if(!future.isDone()) {
                         future.cancel(true);
                     }
                 }
             }
         }
         
         return submittedTasks;
     }
     
     
     /**
      * Holder of AsynSampler result
      */
     public static class AsynSamplerResultHolder {
         private final HTTPSampleResult result;
         private final CollectionProperty cookies;
         
         /**
          * @param result {@link HTTPSampleResult} to hold
          * @param cookies cookies to hold
          */
         public AsynSamplerResultHolder(HTTPSampleResult result, CollectionProperty cookies) {
             super();
             this.result = result;
             this.cookies = cookies;
         }
         
         /**
          * @return the result
          */
         public HTTPSampleResult getResult() {
             return result;
         }
         
         /**
          * @return the cookies
          */
         public CollectionProperty getCookies() {
             return cookies;
         }
     }
     
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
index 5416ae06a..4c2ab4e65 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
@@ -1,371 +1,371 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.RequestEntity;
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Commons HTTPClient based soap sampler
  * @deprecated since 3.0, will be removed in next version
  */
 @Deprecated
 public class SoapSampler extends HTTPSampler2 implements Interruptible { // Implemented by parent class
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SoapSampler.class);
 
     private static final long serialVersionUID = 240L;
 
     public static final String XML_DATA = "HTTPSamper.xml_data"; //$NON-NLS-1$
 
     public static final String URL_DATA = "SoapSampler.URL_DATA"; //$NON-NLS-1$
 
     public static final String SOAP_ACTION = "SoapSampler.SOAP_ACTION"; //$NON-NLS-1$
 
     public static final String SEND_SOAP_ACTION = "SoapSampler.SEND_SOAP_ACTION"; //$NON-NLS-1$
 
     public static final String XML_DATA_FILE = "SoapSampler.xml_data_file"; //$NON-NLS-1$
 
     private static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
 
     private static final String SOAPACTION = "SOAPAction"; //$NON-NLS-1$
 
     private static final String ENCODING = "utf-8"; //$NON-NLS-1$ TODO should this be variable?
 
     private static final String DEFAULT_CONTENT_TYPE = "text/xml"; //$NON-NLS-1$
 
     public void setXmlData(String data) {
         setProperty(XML_DATA, data);
     }
 
     public String getXmlData() {
         return getPropertyAsString(XML_DATA);
     }
 
     /**
      * it's kinda obvious, but we state it anyways. Set the xml file with a
      * string path.
      *
      * @param filename path to the xml file
      */
     public void setXmlFile(String filename) {
         setProperty(XML_DATA_FILE, filename);
     }
 
     /**
      * Get the file location of the xml file.
      *
      * @return String file path.
      */
     public String getXmlFile() {
         return getPropertyAsString(XML_DATA_FILE);
     }
 
     public String getURLData() {
         return getPropertyAsString(URL_DATA);
     }
 
     public void setURLData(String url) {
         setProperty(URL_DATA, url);
     }
 
     public String getSOAPAction() {
         return getPropertyAsString(SOAP_ACTION);
     }
 
     public String getSOAPActionQuoted() {
         String action = getSOAPAction();
         StringBuilder sb = new StringBuilder(action.length()+2);
         sb.append(DOUBLE_QUOTE);
         sb.append(action);
         sb.append(DOUBLE_QUOTE);
         return sb.toString();
     }
 
     public void setSOAPAction(String action) {
         setProperty(SOAP_ACTION, action);
     }
 
     public boolean getSendSOAPAction() {
         return getPropertyAsBoolean(SEND_SOAP_ACTION);
     }
 
     public void setSendSOAPAction(boolean action) {
         setProperty(SEND_SOAP_ACTION, String.valueOf(action));
     }
 
     protected int setPostHeaders(PostMethod post) {
         int length=0;// Take length from file
         if (getHeaderManager() != null) {
             // headerManager was set, so let's set the connection
             // to use it.
             HeaderManager mngr = getHeaderManager();
             int headerSize = mngr.size();
             for (int idx = 0; idx < headerSize; idx++) {
                 Header hd = mngr.getHeader(idx);
                 if (HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(hd.getName())) {// Use this to override file length
                     length = Integer.parseInt(hd.getValue());
                     break;
                 }
                 // All the other headers are set up by HTTPSampler2.setupConnection()
             }
         } else {
             // otherwise we use "text/xml" as the default
             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE); //$NON-NLS-1$
         }
         if (getSendSOAPAction()) {
             post.setRequestHeader(SOAPACTION, getSOAPActionQuoted());
         }
         return length;
     }
 
     /**
      * Send POST data from <code>Entry</code> to the open connection.
      *
      * @param post POST request to send
      * @param length the length of the content
      */
     private String sendPostData(PostMethod post, final int length) {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         final String xmlFile = getXmlFile();
         if (xmlFile != null && xmlFile.length() > 0) {
             File xmlFileAsFile = new File(xmlFile);
             if(!(xmlFileAsFile.exists() && xmlFileAsFile.canRead())) {
                 throw new IllegalArgumentException(JMeterUtils.getResString("soap_sampler_file_invalid") // $NON-NLS-1$
                         + xmlFileAsFile.getAbsolutePath());
             }
             // We just add placeholder text for file content
             postedBody.append("Filename: ").append(xmlFile).append("\n");
             postedBody.append("<actual file content, not shown here>");
             post.setRequestEntity(new RequestEntity() {
                 @Override
                 public boolean isRepeatable() {
                     return true;
                 }
 
                 @Override
                 public void writeRequest(OutputStream out) throws IOException {
                     try (InputStream fileStream = new FileInputStream(xmlFile);
                             InputStream in = new BufferedInputStream(fileStream)) {
                         IOUtils.copy(in, out);
                         out.flush();
                     }
                 }
 
                 @Override
                 public long getContentLength() {
                     switch(length){
                         case -1:
                             return -1;
                         case 0: // No header provided
                             return new File(xmlFile).length();
                         default:
                             return length;
                         }
                 }
 
                 @Override
                 public String getContentType() {
                     // TODO do we need to add a charset for the file contents?
                     return DEFAULT_CONTENT_TYPE; // $NON-NLS-1$
                 }
             });
         } else {
             postedBody.append(getXmlData());
             post.setRequestEntity(new RequestEntity() {
                 @Override
                 public boolean isRepeatable() {
                     return true;
                 }
 
                 @Override
                 public void writeRequest(OutputStream out) throws IOException {
                     // charset must agree with content-type below
                     IOUtils.write(getXmlData(), out, ENCODING); // $NON-NLS-1$
                     out.flush();
                 }
 
                 @Override
                 public long getContentLength() {
                     try {
                         return getXmlData().getBytes(ENCODING).length; // so we don't generate chunked encoding
                     } catch (UnsupportedEncodingException e) {
                         log.warn(e.getLocalizedMessage());
                         return -1; // will use chunked encoding
                     }
                 }
 
                 @Override
                 public String getContentType() {
                     return DEFAULT_CONTENT_TYPE+"; charset="+ENCODING; // $NON-NLS-1$
                 }
             });
         }
         return postedBody.toString();
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
 
         String urlStr = url.toString();
 
         log.debug("Start : sample " + urlStr);
 
         PostMethod httpMethod;
         httpMethod = new PostMethod(urlStr);
 
         HTTPSampleResult res = new HTTPSampleResult();
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(HTTPConstants.POST);
         res.setURL(url);
         res.sampleStart(); // Count the retries as well in the time
         HttpClient client = null;
         InputStream instream = null;
         try {
             int content_len = setPostHeaders(httpMethod);
             client = setupConnection(url, httpMethod, res);
             setSavedClient(client);
 
             res.setQueryString(sendPostData(httpMethod,content_len));
             int statusCode = client.executeMethod(httpMethod);
             // Some headers are set by executeMethod()
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
 
                 org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_ENCODING);
                 if (responseHeader != null && HTTPConstants.ENCODING_GZIP.equals(responseHeader.getValue())) {
                     instream = new GZIPInputStream(instream);
                 }
 
                 //int contentLength = httpMethod.getResponseContentLength();Not visible ...
                 //TODO size ouststream according to actual content length
                 ByteArrayOutputStream outstream = new ByteArrayOutputStream(4 * 1024);
                 //contentLength > 0 ? contentLength : DEFAULT_INITIAL_BUFFER_SIZE);
                 byte[] buffer = new byte[4096];
                 int len;
                 boolean first = true;// first response
                 while ((len = instream.read(buffer)) > 0) {
                     if (first) { // save the latency
                         res.latencyEnd();
                         first = false;
                     }
                     outstream.write(buffer, 0, len);
                 }
 
                 res.setResponseData(outstream.toByteArray());
                 outstream.close();
 
             }
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setSampleLabel(httpMethod.getURI().toString());
             // Pick up Actual path (after redirects)
 
             res.setResponseCode(Integer.toString(statusCode));
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseMessage(httpMethod.getStatusText());
 
             // Set up the defaults (may be overridden below)
             res.setDataEncoding(ENCODING);
             res.setContentType(DEFAULT_CONTENT_TYPE);
             String ct = null;
             org.apache.commons.httpclient.Header h
                     = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 res.setRedirectLocation(httpMethod.getResponseHeader(HTTPConstants.HEADER_LOCATION).getValue());
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()) {
                 res.setURL(new URL(httpMethod.getURI().toString()));
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());
 
             // Save cache information
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null){
                 cacheManager.saveDetails(httpMethod, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             httpMethod.releaseConnection();
             return res;
         } catch (IllegalArgumentException | IOException e)// e.g. some kinds of invalid URL
         {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } finally {
             JOrphanUtils.closeQuietly(instream);
             setSavedClient(null);
             httpMethod.releaseConnection();
         }
     }
 
     @Override
     public URL getUrl() throws MalformedURLException {
         return new URL(getURLData());
     }
 }
