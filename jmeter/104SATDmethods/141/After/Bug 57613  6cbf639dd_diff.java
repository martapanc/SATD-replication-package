diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index d657a3c25..6e0fb77c0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1241 +1,1243 @@
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
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.control.DNSCacheManager",
                     "org.apache.jmeter.protocol.http.gui.DNSCachePanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"}));
     
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
 
     private static final String CONCURRENT_POOL_DEFAULT = "4"; // default for concurrent pool (do not change)
     
     private static final String USER_AGENT = "User-Agent"; // $NON-NLS-1$
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
     
     private static final long AWAIT_TERMINATION_TIMEOUT = 
         JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
     
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES = 
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     public static final int CONCURRENT_POOL_SIZE = 4; // Default concurrent pool size for download embedded resources
 
     public static enum SourceType {
         HOSTNAME("web_testing_source_ip_hostname"), //$NON-NLS-1$
         DEVICE("web_testing_source_ip_device"), //$NON-NLS-1$
         DEVICE_IPV4("web_testing_source_ip_device_ipv4"), //$NON-NLS-1$
         DEVICE_IPV6("web_testing_source_ip_device_ipv6"); //$NON-NLS-1$
         
         public final String propertyName;
         SourceType(String propertyName) {
             this.propertyName = propertyName;
         }
     }
 
     private static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
     // Use for ComboBox Source Address Type. Preserve order (specially with localization)
     public static final String[] getSourceTypeList() {
         final SourceType[] types = SourceType.values();
         final String[] displayStrings = new String[types.length];
         for(int i = 0; i < types.length; i++) {
             displayStrings[i] = JMeterUtils.getResString(types[i].propertyName);
         }
         return displayStrings;
     }
 
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
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
-        HTTPConstants.UNLOCK
+        HTTPConstants.UNLOCK,
+        HTTPConstants.REPORT,
+        HTTPConstants.MKCALENDAR
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
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
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere 
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
         
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
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
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the entity body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if(getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 if(arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(HTTPConstants.POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
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
         if (protocol == null || protocol.length() == 0 ) {
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
         if (!fullUrl && (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod()))) {
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
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
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
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if(nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
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
     public void clearTestElementChildren(){
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
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)  ? HTTPConstants.DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS) ? HTTPConstants.DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
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
         if (port == UNSPECIFIED_PORT ||
                 (HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT) ||
                 (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
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
                 log.warn("Unexpected protocol: "+prot);
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
 
     public void setArguments(Arguments value) {
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
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
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
         return getPropertyAsString(EMBEDDED_URL_RE,"");
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
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER = 
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
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
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain=null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")){ // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
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
     public String getQueryString(String contentEncoding) {
          // Check if the sampler has a specified content encoding
          if(JOrphanUtils.isBlank(contentEncoding)) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
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
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
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
                 buf.append(item.getEncodedValue(contentEncoding));
             }
             catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
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
         for (int i = 0; i < args.length; i++) {
             if (isDebug) {
                 log.debug("Arg: " + args[i]);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name+ " Value: " + value+ " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if(HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
      *            <code>Entry</code> to be sampled
      * @return results of the sampling
      */
     @Override
     public SampleResult sample(Entry e) {
         return sample();
     }
 
     /**
      * Perform a sample, and return the results
      *
      * @return results of the sampling
      */
     public SampleResult sample() {
         SampleResult res = null;
         try {
             res = sample(getUrl(), getMethod(), false, 0);
             if(res != null) {
                 res.setSampleLabel(getName());
             }
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
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
      * @param u
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param depth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling, can be null if u is in CacheManager
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      * 
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0){  // Bug 39205
                 String parserName = getParserClass(res);
                 if(parserName != null)
                 {
                     final HTMLParser parser =
                         parserName.length() > 0 ? // we have a name
                         HTMLParser.getParser(parserName)
                         :
                         HTMLParser.getParser(); // we don't; use the default parser
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re=getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
                 }
             }
             
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<Callable<AsynSamplerResultHolder>>();
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java
index d13c0c983..586c890f3 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java
@@ -1,78 +1,80 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 
 /**
  * Constants used in HTTP, mainly header names.
  */
 
 public interface HTTPConstantsInterface {
 
     int DEFAULT_HTTPS_PORT = 443;
     String DEFAULT_HTTPS_PORT_STRING = "443"; // $NON-NLS-1$
     int    DEFAULT_HTTP_PORT = 80;
     String DEFAULT_HTTP_PORT_STRING = "80"; // $NON-NLS-1$
     String PROTOCOL_HTTP = "http"; // $NON-NLS-1$
     String PROTOCOL_HTTPS = "https"; // $NON-NLS-1$
     String HEAD = "HEAD"; // $NON-NLS-1$
     String POST = "POST"; // $NON-NLS-1$
     String PUT = "PUT"; // $NON-NLS-1$
     String GET = "GET"; // $NON-NLS-1$
     String OPTIONS = "OPTIONS"; // $NON-NLS-1$
     String TRACE = "TRACE"; // $NON-NLS-1$
     String DELETE = "DELETE"; // $NON-NLS-1$
     String PATCH = "PATCH"; // $NON-NLS-1$
     String PROPFIND = "PROPFIND"; // $NON-NLS-1$
     String PROPPATCH = "PROPPATCH"; // $NON-NLS-1$
     String MKCOL = "MKCOL"; // $NON-NLS-1$
     String COPY = "COPY"; // $NON-NLS-1$
     String MOVE = "MOVE"; // $NON-NLS-1$
     String LOCK = "LOCK"; // $NON-NLS-1$
     String UNLOCK = "UNLOCK"; // $NON-NLS-1$
     String CONNECT = "CONNECT"; // $NON-NLS-1$
+    String REPORT = "REPORT"; // $NON-NLS-1$
+    String MKCALENDAR = "MKCALENDAR"; // $NON-NLS-1$
     String HEADER_AUTHORIZATION = "Authorization"; // $NON-NLS-1$
     String HEADER_COOKIE = "Cookie"; // $NON-NLS-1$
     String HEADER_CONNECTION = "Connection"; // $NON-NLS-1$
     String CONNECTION_CLOSE = "close"; // $NON-NLS-1$
     String KEEP_ALIVE = "keep-alive"; // $NON-NLS-1$
     // e.g. "Transfer-Encoding: chunked", which is processed automatically by the underlying protocol
     String TRANSFER_ENCODING = "transfer-encoding"; // $NON-NLS-1$
     String HEADER_CONTENT_ENCODING = "content-encoding"; // $NON-NLS-1$
     String HTTP_1_1 = "HTTP/1.1"; // $NON-NLS-1$
     String HEADER_SET_COOKIE = "set-cookie"; // $NON-NLS-1$
     String ENCODING_GZIP = "gzip"; // $NON-NLS-1$
     String HEADER_CONTENT_DISPOSITION = "Content-Disposition"; // $NON-NLS-1$
     String HEADER_CONTENT_TYPE = "Content-Type"; // $NON-NLS-1$
     String HEADER_CONTENT_LENGTH = "Content-Length"; // $NON-NLS-1$
     String HEADER_HOST = "Host"; // $NON-NLS-1$
     String HEADER_LOCAL_ADDRESS = "X-LocalAddress"; // $NON-NLS-1$ pseudo-header for reporting Local Address
     String HEADER_LOCATION = "Location"; // $NON-NLS-1$
     String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"; // $NON-NLS-1$
     String MULTIPART_FORM_DATA = "multipart/form-data"; // $NON-NLS-1$
     // For handling caching
     String IF_NONE_MATCH = "If-None-Match"; // $NON-NLS-1$
     String IF_MODIFIED_SINCE = "If-Modified-Since"; // $NON-NLS-1$
     String ETAG = "Etag"; // $NON-NLS-1$
     String LAST_MODIFIED = "Last-Modified"; // $NON-NLS-1$
     String EXPIRES = "Expires"; // $NON-NLS-1$
     String CACHE_CONTROL = "Cache-Control";  //e.g. public, max-age=259200
     String DATE = "Date";  //e.g. Date Header of response 
 
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index c14eeda76..5fd6dbaa9 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,281 +1,283 @@
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
 
 
 <!--  =================== 2.13 =================== -->
 
 <h1>Version 2.13</h1>
 
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
 <li><bugzilla>48799</bugzilla> - Add time to establish connection to available sample metrics. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bugzilla>57500</bugzilla> - Introduce retry behavior for distributed testing. Implemented by Andrey Pokhilko and Dzimitry Kashlach and contributed by BlazeMeter Ltd.</li>
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
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(...) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477</bugzilla>). 
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
     <li>Since 2.13, Aggregate Graph, Summary Report and Aggregate Report now export percentages to %, before they exported the decimal value which differed from what was shown in GUI</li>
     <li>Third party plugins may be impacted by fix of <bugzilla>57586</bugzilla>, ensure that your subclass of HttpTestSampleGui implements ItemListener if you relied on parent class doing so.</li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bug>57385</bug>Getting empty thread name in xml result for HTTP requests with "Follow Redirects" set. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57579</bug>NullPointerException error is raised on main sample if "RETURN_NO_SAMPLE" is used (default) and "Use Cache-Control / Expires header..." is checked in HTTP Cache Manager</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bug>57447</bug>Use only the user listed DNS Servers, when "use custom DNS resolver" option is enabled.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>57262</bug>Aggregate Report, Aggregate Graph and Summary Report export : headers use keys instead of labels</li>
 <li><bug>57346</bug>Summariser : The + (difference) reports show wrong elapsed time and throughput</li>
 <li><bug>57449</bug>Distributed Testing: Stripped modes do not strip responses from SubResults (affects load tests that use Download of embedded resources). Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57562</bug>View Results Tree CSS/JQuery Tester : Nothing happens when there is an error in syntax and an exception occurs in jmeter.log</li>
 <li><bug>57514</bug>Aggregate Graph, Summary Report and Aggregate Report show wrong percentage reporting in saved file</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>57607</bug>Constant Throughput Timer : Wrong throughput computed in shared modes due to rounding error </li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57365</bug>Selected LAF is not correctly setup due to call of UIManager.setLookAndFeel too late. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57364</bug>Options &lt; Look And Feel does not update all windows LAF. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57394</bug>When constructing an instance with ClassTools#construct(String, int) the integer was ignored and the default constructor was used instead.</li>
 <li><bug>57440</bug>OutOfMemoryError after introduction of JSyntaxTextArea in LoggerPanel due to disableUndo not being taken into account.</li>
 <li><bug>57569</bug>FileServer.reserveFile - inconsistent behaviour when hasHeader is true</li>
 <li><bug>57555</bug>Cannot use JMeter 2.12 as a maven dependency. Contributed by Pascal Schumacher (pascal.schumacher at t-systems.com)</li>
 <li><bug>57608</bug>Fix start script compatibility with old Unix shells, e.g. on Solaris</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bug>25430</bug>HTTP(S) Test Script Recorder : Make it populate HTTP Authorisation Manager. Partly based on a patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bug>57381</bug>HTTP(S) Test Script Recorder should display an error if Target Controller references a Recording Controller and no Recording Controller exists. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57488</bug>Performance : Improve SSLContext reset for Two-way SSL Authentication</li>
 <li><bug>57565</bug>SamplerCreator : Add method to allow implementations to add children to created sampler</li>
 <li><bug>57606</bug>HTTPSamplerBase#errorResult changes the sample label on exception </li>
+<li><bug>57613</bug>HTTP Sampler : Added CalDAV verbs (REPORT, MKCALENDAR). Contributed by Richard Brigham (richard.brigham at teamaol.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57322</bug>JDBC Test elements: add ResultHandler to deal with ResultSets(cursors) returned by callable statements. Contributed by Yngvi &amp;THORN;&amp;oacute;r Sigurj&amp;oacute;nsson (blitzkopf at gmail.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bug>57561</bug>Module controller UI : Replace combobox by tree. Contributed by Maciej Franek (maciej.franek at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>55932</bug>Create a Async BackendListener to allow easy plug of new listener (Graphite, JDBC, Console,...)</li>
 <li><bug>57246</bug>BackendListener : Create a Graphite implementation</li>
 <li><bug>57217</bug>Aggregate graph and Aggregate report improvements (3 configurable percentiles, same data in both, factor out code). Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57537</bug>BackendListener : Allow implementations to drop samples</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>54453</bug>Performance enhancements : Replace Random by ThreadLocalRandom in __Random function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57518</bug>Icons for toolbar with several sizes</li>
 <li><bug>57605</bug>When there is an error loading Test Plan, SaveService.loadTree returns null leading to NPE in callers</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to jsoup-1.8.3.jar (from 1.7.3)</li>
 <li>Updated to tika-core and tika-parsers 1.7 (from 1.6)</li>
 <li><bug>57276</bug>RMIC no longer needed since Java 5</li>
 <li><bug>57310</bug>Replace System.getProperty("file.separator") with File.separator throughout (Also "path.separator" with File.pathSeparator)</li>
 <li><bug>57389</bug>Fix potential NPE in converters</li>
 <li><bug>57417</bug>Remove unused method isTemporary from NullProperty. This was a leftover from a refactoring done in 2003.</li>
 <li><bug>57418</bug>Remove unused constructor from Workbench</li>
 <li><bug>57419</bug>Remove unused interface ModelListener.</li>
 <li><bug>57466</bug>IncludeController : Remove an unneeded set creation. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li>Added property loggerpanel.usejsyntaxtext to disable the use of JSyntaxTextArea for the Console Logger (in case of memory or other issues)</li>
 <li><bug>57586</bug>HttpTestSampleGui: Remove interface ItemListener implementation</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Yngvi &amp;THORN;&amp;oacute;r Sigurj&amp;oacute;nsson (blitzkopf at gmail.com)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><a href="http://blazemeter.com">BlazeMeter Ltd.</a></li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li>Pascal Schumacher (pascal.schumacher at t-systems.com)</li>
 <li>Maciej Franek (maciej.franek at gmail.com)</li>
+<li>Richard Brigham (richard.brigham at teamaol.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Chaitanya Bhatt (bhatt.chaitanya at gmail.com) for his thorough testing of new BackendListener and Graphite Client implementation.</li>
 <li>Marcelo Jara (marcelojara at hotmail.com) for his clear report on <bugzilla>57607</bugzilla>.</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 872f184ff..dceb1934a 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1,1229 +1,1229 @@
 <?xml version="1.0"?>
 <!-- 
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
  -->
 <!DOCTYPE document
 [
 <!ENTITY sect-num '18'>
 ]>
 <document index="yes" index-level-2="yes" index-numbers="no" colbreak="&sect-num;.4"
   prev="boss.html" next="functions.html" id="$Id$">
 
 <properties>
   <title>User's Manual: Component Reference</title>
 </properties>
 
 <body>
 
 <!--
     Because this is an XML document, all tags must be properly closed, including ones
     which are passed unchanged into the HTML output, e.g. <br/>, not just <br>.
 
     Unfortunately Java does not currently allow for this - it outputs the trailing > -
     which messes up the Help display. 
     To avoid these artifacts, use the form <br></br>, which Java does seem to handle OK.
 
  -->
 <section name="&sect-num;.0 Introduction" anchor="introduction">
 <description>
 <p>
 
 </p>
  <note>
  Several test elements use JMeter properties to control their behaviour.
  These properties are normally resolved when the class is loaded.
  This generally occurs before the test plan starts, so it's not possible to change the settings by using the __setProperty() function.
 </note>
 <p>
 </p>
 </description>
 </section>
  
 <section name="&sect-num;.1 Samplers" anchor="samplers">
 <description>
     <p>
     Samplers perform the actual work of JMeter.
     Each sampler (except Test Action) generates one or more sample results.
     The sample results have various attributes (success/fail, elapsed time, data size etc) and can be viewed in the various listeners.
     </p>
 </description>
 <component name="FTP Request" index="&sect-num;.1.1" width="861" height="278" screenshot="ftptest/ftp-request.png">
 <description>
 This controller lets you send an FTP "retrieve file" or "upload file" request to an FTP server.
 If you are going to send multiple requests to the same FTP server, consider
 using a <complink name="FTP Request Defaults"/> Configuration
 Element so you do not have to enter the same information for each FTP Request Generative
 Controller. When downloading a file, it can be stored on disk (Local File) or in the Response Data, or both.
 <p>
 Latency is set to the time it takes to login (versions of JMeter after 2.3.1).
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server Name or IP" required="Yes">Domain name or IP address of the FTP server.</property>
         <property name="Port" required="No">Port to use. If this is  >0, then this specific port is used, otherwise JMeter uses the default FTP port.</property>
         <property name="Remote File:" required="Yes">File to retrieve or name of destination file to upload.</property>
         <property name="Local File:" required="Yes, if uploading (*)">File to upload, or destination for downloads (defaults to remote file name).</property>
         <property name="Local File Contents:" required="Yes, if uploading (*)">Provides the contents for the upload, overrides the Local File property.</property>
         <property name="get(RETR) / put(STOR)" required="Yes">Whether to retrieve or upload a file.</property>
         <property name="Use Binary mode ?" required="Yes">Check this to use Binary mode (default Ascii)</property>
         <property name="Save File in Response ?" required="Yes, if downloading">
         Whether to store contents of retrieved file in response data.
         If the mode is Ascii, then the contents will be visible in the Tree View Listener.
         </property>
         <property name="Username" required="Usually">FTP account username.</property>
         <property name="Password" required="Usually">FTP account password. N.B. This will be visible in the test plan.</property>
 </properties>
 <links>
         <link href="test_plan.html#assertions">Assertions</link>
         <complink name="FTP Request Defaults"/>
         <link href="build-ftp-test-plan.html">Building an FTP Test Plan</link>
 </links>
 
 </component>
 
 <component name="HTTP Request" index="&sect-num;.1.2" width="900" height="686" screenshot="http-request.png">
 
 <description>
         <p>This sampler lets you send an HTTP/HTTPS request to a web server.  It
         also lets you control whether or not JMeter parses HTML files for images and
         other embedded resources and sends HTTP requests to retrieve them.
         The following types of embedded resource are retrieved:</p>
         <ul>
         <li>images</li>
         <li>applets</li>
         <li>stylesheets</li>
         <li>external scripts</li>
         <li>frames, iframes</li>
         <li>background images (body, table, TD, TR)</li>
         <li>background sound</li>
         </ul>
         <p>
         The default parser is htmlparser.
         This can be changed by using the property "htmlparser.classname" - see jmeter.properties for details.
         </p>
         <p>If you are going to send multiple requests to the same web server, consider
         using an <complink name="HTTP Request Defaults"/>
         Configuration Element so you do not have to enter the same information for each
         HTTP Request.</p>
 
         <p>Or, instead of manually adding HTTP Requests, you may want to use
         JMeter's <complink name="HTTP(S) Test Script Recorder"/> to create
         them.  This can save you time if you have a lot of HTTP requests or requests with many
         parameters.</p>
 
         <p><b>There are two different test elements used to define the samplers:</b>
         <ul>
         <li>AJP/1.3 Sampler - uses the Tomcat mod_jk protocol (allows testing of Tomcat in AJP mode without needing Apache httpd)
         The AJP Sampler does not support multiple file upload; only the first file will be used.
         </li>
         <li>HTTP Request - this has an implementation drop-down box, which selects the HTTP protocol implementation to be used:</li>
         <ul>
         <li>Java - uses the HTTP implementation provided by the JVM. 
         This has some limitations in comparison with the HttpClient implementations - see below.</li>
         <li>HTTPClient3.1 - uses Apache Commons HttpClient 3.1. 
         This is no longer being developed, and support for this may be dropped in a future JMeter release.</li>
         <li>HTTPClient4 - uses Apache HttpComponents HttpClient 4.x.</li>
         <li>Blank Value - does not set implementation on HTTP Samplers, so relies on HTTP Request Defaults if present or on jmeter.httpsampler property defined in jmeter.properties</li>
         </ul>
         </ul>
          </p>
          <p>The Java HTTP implementation has some limitations:</p>
          <ul>
          <li>There is no control over how connections are re-used. 
          When a connection is released by JMeter, it may or may not be re-used by the same thread.</li>
          <li>The API is best suited to single-threaded usage - various settings 
          are defined via system properties, and therefore apply to all connections.</li>
          <li>There is a bug in the handling of HTTPS via a Proxy (the CONNECT is not handled correctly).
          See Java bugs 6226610 and 6208335.
          </li>
          <li>It does not support virtual hosts.</li>
-         <li>It does not support the following methods: COPY, LOCK, MKCOL, MOVE, PATCH, PROPFIND, PROPPATCH, UNLOCK.</li>
+         <li>It does not support the following methods: COPY, LOCK, MKCOL, MOVE, PATCH, PROPFIND, PROPPATCH, UNLOCK, REPORT, MKCALENDAR.</li>
          <li>It does not support client based certificate testing with Keystore Config.</li>
          </ul>
          <p>Note: the FILE protocol is intended for testing purposes only.
          It is handled by the same code regardless of which HTTP Sampler is used.</p>
         <p>If the request requires server or proxy login authorization (i.e. where a browser would create a pop-up dialog box),
          you will also have to add an <complink name="HTTP Authorization Manager"/> Configuration Element.
          For normal logins (i.e. where the user enters login information in a form), you will need to work out what the form submit button does,
          and create an HTTP request with the appropriate method (usually POST) 
          and the appropriate parameters from the form definition. 
          If the page uses HTTP, you can use the JMeter Proxy to capture the login sequence.
         </p>
         <p>
         In versions of JMeter up to 2.2, only a single SSL context was used for all threads and samplers.
         This did not generate the proper load for multiple users.
         A separate SSL context is now used for each thread.
         To revert to the original behaviour, set the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
         By default, the SSL context is retained for the duration of the test.
         In versions of JMeter from 2.5.1, the SSL session can be optionally reset for each test iteration.
         To enable this, set the JMeter property:
 <pre>
 https.use.cached.ssl.context=false
 </pre>
         Note: this does not apply to the Java HTTP implementation.
         </p>
         <p>
         JMeter defaults to the SSL protocol level TLS.
         If the server needs a different level, e.g. SSLv3, change the JMeter property, for example:
 <pre>
 https.default.protocol=SSLv3
 </pre> 
         </p>
         <p>
         JMeter also allows one to enable additional protocols, by changing the property <tt>https.socket.protocols</tt>.
         </p>
         <p>If the request uses cookies, then you will also need an
         <complink name="HTTP Cookie Manager"/>.  You can
         add either of these elements to the Thread Group or the HTTP Request.  If you have
         more than one HTTP Request that needs authorizations or cookies, then add the
         elements to the Thread Group.  That way, all HTTP Request controllers will share the
         same Authorization Manager and Cookie Manager elements.</p>
 
         <p>If the request uses a technique called "URL Rewriting" to maintain sessions,
         then see section
         <a href="build-adv-web-test-plan.html#session_url_rewriting">6.1 Handling User Sessions With URL Rewriting</a>
         for additional configuration steps.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server" required="Yes, unless provided by HTTP Request Defaults">
             Domain name or IP address of the web server. e.g. www.example.com. [Do not include the http:// prefix.]
             Note: in JMeter 2.5 (and later) if the "Host" header is defined in a Header Manager, then this will be used
             as the virtual host name.
         </property>
         <property name="Port" required="No">Port the web server is listening to. Default: 80</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response. 
         Note that this applies to each wait for a response. If the server response is sent in several chunks, the overall
         elapsed time may be longer than the timeout.
         A <complink name="Duration Assertion"/> can be used to detect responses that take too long to complete.
         </property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the http:// prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server. (N.B. this is stored unencrypted in the test plan)</property>
         <property name="Implementation" required="No">Java, HttpClient3.1, HttpClient4. 
         If not specified (and not defined by HTTP Request Defaults), the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the HttpClient4 implementation is used.</property>
         <property name="Protocol" required="No">HTTP, HTTPS or FILE. Default: HTTP</property>
         <property name="Method" required="Yes">GET, POST, HEAD, TRACE, OPTIONS, PUT, DELETE, PATCH (not supported for
         JAVA implementation). With HttpClient4, the following methods related to WebDav are also allowed: COPY, LOCK, MKCOL, MOVE,
-        PROPFIND, PROPPATCH, UNLOCK.</property>
+        PROPFIND, PROPPATCH, UNLOCK, REPORT, MKCALENDAR.</property>
         <property name="Content Encoding" required="No">
         Content encoding to be used (for POST, PUT, PATCH and FILE). 
         This the the character encoding to be used, and is not related to the Content-Encoding HTTP header.
         </property>
         <property name="Redirect Automatically" required="No">
         Sets the underlying http protocol handler to automatically follow redirects,
         so they are not seen by JMeter, and thus will not appear as samples.
         Should only be used for GET and HEAD requests.
         The HttpClient sampler will reject attempts to use it for POST or PUT.
         <b>Warning: see below for information on cookie and header handling.</b>
         </property>
         <property name="Follow Redirects" required="No">
         This only has any effect if "Redirect Automatically" is not enabled.
         If set, the JMeter sampler will check if the response is a redirect and follow it if so.
         The initial redirect and further responses will appear as additional samples.
         The URL and data fields of the parent sample will be taken from the final (non-redirected)
         sample, but the parent byte count and elapsed time include all samples.
         The latency is taken from the initial response (versions of JMeter after 2.3.4 - previously it was zero).
         Note that the HttpClient sampler may log the following message:<br/>
         "Redirect requested but followRedirects is disabled"<br/>
         This can be ignored.
         <br/>
         In versions after 2.3.4, JMeter will collapse paths of the form '/../segment' in
         both absolute and relative redirect URLs. For example http://host/one/../two => http://host/two.
         If necessary, this behaviour can be suppressed by setting the JMeter property
         <code>httpsampler.redirect.removeslashdotdot=false</code>
         </property>
         <property name="Use KeepAlive" required="No">JMeter sets the Connection: keep-alive header. This does not work properly with the default HTTP implementation, as connection re-use is not under user-control. 
                   It does work with the Apache HttpComponents HttpClient implementations.</property>
         <property name="Use multipart/form-data for HTTP POST" required="No">
         Use a multipart/form-data or application/x-www-form-urlencoded post request
         </property>
         <property name="Browser-compatible headers" required="No">
         When using multipart/form-data, this suppresses the Content-Type and 
         Content-Transfer-Encoding headers; only the Content-Disposition header is sent.
         </property>
         <property name="Path" required="Yes">The path to resource (for example, /servlets/myServlet). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.
 <b>
 As a special case, if the path starts with "http://" or "https://" then this is used as the full URL.
 </b>
 In this case, the server, port and protocol fields are ignored; parameters are also ignored for GET and DELETE methods.
 Also please note that the path is not encoded - apart from replacing spaces with %20 -
 so unsafe characters may need to be encoded to avoid errors such as URISyntaxException.
 </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>, the options to encode the parameter, and an option to include or exclude an equals sign (some applications
         don't expect an equals when the value is the empty string).  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET or DELETE, the query string will be
         appended to the URL, if POST or PUT, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.
         <b>See below for some further information on parameter handling.</b>
         <p>
         Additionally, you can specify whether each parameter should be URL encoded.  If you are not sure what this
         means, it is probably best to select it.  If your values contain characters such as the following then encoding is usually required.:
         <ul>
             <li>ASCII Control Chars</li>
             <li>Non-ASCII characters</li>
             <li>Reserved characters:URLs use some characters for special use in defining their syntax. When these characters are not used in their special role inside a URL, they need to be encoded, example : '$', '&amp;', '+', ',' , '/', ':', ';', '=', '?', '@'</li>
             <li>Unsafe characters: Some characters present the possibility of being misunderstood within URLs for various reasons. These characters should also always be encoded, example : ' ', '&lt;', '&gt;', '#', '%', ...</li>
         </ul>
         </p>
         </property>
         <property name="File Path:" required="No">Name of the file to send.  If left blank, JMeter
         does not send a file, if filled in, JMeter automatically sends the request as
         a multipart form request.
         <p>
         If it is a POST or PUT or PATCH request and there is a single file whose 'Parameter name' attribute (below) is omitted, 
         then the file is sent as the entire body
         of the request, i.e. no wrappers are added. This allows arbitrary bodies to be sent. This functionality is present for POST requests
         after version 2.2, and also for PUT requests after version 2.3.
         <b>See below for some further information on parameter handling.</b>
         </p>
         </property>
         <property name="Parameter name:" required="No">Value of the "name" web request parameter.</property>
         <property name="MIME Type" required="No">MIME type (for example, text/plain).
         If it is a POST or PUT or PATCH request and either the 'name' attribute (below) are omitted or the request body is
         constructed from parameter values only, then the value of this field is used as the value of the
         content-type request header.
         </property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         See below for more details.
         </property>
         <property name="Use as monitor" required="No">For use with the <complink name="Monitor Results"/> listener.</property>
        <property name="Save response as MD5 hash?" required="No">
        If this is selected, then the response is not stored in the sample result.
        Instead, the 32 character MD5 hash of the data is calculated and stored instead.
        This is intended for testing large amounts of data.
        </property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from http://example.com/, use the expression:
         http://example\.com/.*
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Source address type" required="No">
         <i>[Only for HTTP Request with HTTPClient implementation]</i> <br></br>
         To distinguish the source address value, select the type of these:
         <ul>
         <li>Select <i>IP/Hostname</i> to use a specific IP address or a (local) hostname</li>
         <li>Select <i>Device</i> to pick the first available address for that interface which
         this may be either IPv4 or IPv6</li>
         <li>Select <i>Device IPv4</i>To select the IPv4 address of the device name (like eth0, lo, em0, etc.)</li>
         <li>Select <i>Device IPv6</i>To select the IPv6 address of the device name (like eth0, lo, em0, etc.)</li>
         </ul>
         </property>
         <property name="Source address field" required="No">
         <i>[Only for HTTP Request with HTTPClient implementation]</i> <br></br>
         This property is used to enable IP Spoofing.
         It override the default local IP address for this sample.
         The JMeter host must have multiple IP addresses (i.e. IP aliases, network interfaces, devices).
         The value can be a host name, IP address, or a network interface device such as "eth0" or "lo" or "wlan0".<br></br>
         If the property <b>httpclient.localaddress</b> is defined, that is used for all HttpClient requests.
         </property>
 </properties>
 <p>
 <b>N.B.</b> when using Automatic Redirection, cookies are only sent for the initial URL.
 This can cause unexpected behaviour for web-sites that redirect to a local server.
 E.g. if www.example.com redirects to www.example.co.uk.
 In this case the server will probably return cookies for both URLs, but JMeter will only see the cookies for the last
 host, i.e. www.example.co.uk. If the next request in the test plan uses www.example.com, 
 rather than www.example.co.uk, it will not get the correct cookies.
 Likewise, Headers are sent for the initial request, and won't be sent for the redirect.
 This is generally only a problem for manually created test plans,
 as a test plan created using a recorder would continue from the redirected URL.
 </p>
 <p>
 <b>Parameter Handling:</b><br></br>
 For the POST and PUT method, if there is no file to send, and the name(s) of the parameter(s) are omitted,
 then the body is created by concatenating all the value(s) of the parameters.
 Note that the values are concatenated without adding any end-of-line characters.
 These can be added by using the __char() function in the value fields.
 This allows arbitrary bodies to be sent.
 The values are encoded if the encoding flag is set (versions of JMeter after 2.3).
 See also the MIME Type above how you can control the content-type request header that is sent.
 <br></br>
 For other methods, if the name of the parameter is missing,
 then the parameter is ignored. This allows the use of optional parameters defined by variables.
 (versions of JMeter after 2.3)
 </p>
 <br/>
 <p>Since JMeter 2.6, you have the option to switch to Post Body when a request has only unnamed parameters
 (or no parameters at all).
 This option is useful in the following cases (amongst others):
 <ul>
 <li>GWT RPC HTTP Request</li>
 <li>JSON REST HTTP Request</li>
 <li>XML REST HTTP Request</li>
 <li>SOAP HTTP Request</li>
 </ul>
 Note that once you leave the Tree node, you cannot switch back to the parameter tab unless you clear the Post Body tab of data.
 </p>
 <p>
 In Post Body mode, each line will be sent with CRLF appended, apart from the last line.
 To send a CRLF after the last line of data, just ensure that there is an empty line following it.
 (This cannot be seen, except by noting whether the cursor can be placed on the subsequent line.)
 </p>
 <figure width="902" height="421" image="http-request-raw-single-parameter.png">Figure 1 - HTTP Request with one unnamed parameter</figure>
 <figure width="908" height="212" image="http-request-confirm-raw-body.png">Figure 2 - Confirm dialog to switch</figure>
 <figure width="905" height="423" image="http-request-raw-body.png">Figure 3 - HTTP Request using Body Data</figure>
 
 <p>
 <b>Method Handling:</b><br></br>
 The POST, PUT and PATCH request methods work similarly, except that the PUT and PATCH methods do not support multipart requests
 or file upload.
 The PUT and PATCH method body must be provided as one of the following:
 <ul>
 <li>define the body as a file with empty Parameter name field; in which case the MIME Type is used as the Content-Type</li>
 <li>define the body as parameter value(s) with no name</li>
 <li>use the Post Body tab</li>
 </ul>
 If you define any parameters with a name in either the sampler or Http
 defaults then nothing is sent.
 PUT and PATCH require a Content-Type. 
 If not using a file, attach a Header Manager to the sampler and define the Content-Type there.
 The GET and DELETE request methods work similarly to each other.
 </p>
 <p>Upto and including JMeter 2.1.1, only responses with the content-type "text/html" were scanned for
 embedded resources. Other content-types were assumed to be something other than HTML.
 JMeter 2.1.2 introduces the a new property <b>HTTPResponse.parsers</b>, which is a list of parser ids,
  e.g. <b>htmlParser</b> and <b>wmlParser</b>. For each id found, JMeter checks two further properties:</p>
  <ul>
  <li>id.types - a list of content types</li>
  <li>id.className - the parser to be used to extract the embedded resources</li>
  </ul>
  <p>See jmeter.properties file for the details of the settings. 
  If the HTTPResponse.parser property is not set, JMeter reverts to the previous behaviour,
  i.e. only text/html responses will be scanned</p>
 <b>Emulating slow connections:</b><br></br>
 HttpClient31, HttpClient4 and Java Sampler support emulation of slow connections; see the following entries in jmeter.properties:
 <pre>
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 </pre>
 <p><b>Response size calculation</b><br></br>
 Optional properties to allow change the method to get response size:<br></br>
 <ul><li>Gets the real network size in bytes for the body response
 <pre>sampleresult.getbytes.body_real_size=true</pre></li>
 <li>Add HTTP headers to full response size
 <pre>sampleresult.getbytes.headers_size=true</pre></li></ul>
 
 <note>
 The Java and HttpClient3 inplementations do not include transport overhead such as
 chunk headers in the response body size.<br></br>
 The HttpClient4 implementation does include the overhead in the response body size,
 so the value may be greater than the number of bytes in the response content.
 </note>
 
 <note>Versions of JMeter before 2.5 returns only data response size (uncompressed if request uses gzip/defate mode).
 <br></br>To return to settings before version 2.5, set the two properties to false.</note>
 </p>
 <p>
 <b>Retry handling</b><br></br>
 In version 2.5 of JMeter, the HttpClient4 and Commons HttpClient 3.1 samplers used the default retry count, which was 3.
 In later versions, the retry count has been set to 1, which is what the Java implementation appears to do.
 The retry count can be overridden by setting the relevant JMeter property, for example:
 <pre>
 httpclient4.retrycount=3
 httpclient3.retrycount=3
 </pre>
 </p>
 <p>
 <b>Note: Certificates does not conform to algorithm constraints</b><br></br>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 </p><p>
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 </p><p>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 </p><p>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </p>
 <links>
         <link href="test_plan.html#assertions">Assertion</link>
         <link href="build-web-test-plan.html">Building a Web Test Plan</link>
         <link href="build-adv-web-test-plan.html">Building an Advanced Web Test Plan</link>
         <complink name="HTTP Authorization Manager"/>
         <complink name="HTTP Cookie Manager"/>
         <complink name="HTTP Header Manager"/>
         <complink name="HTML Link Parser"/>
         <complink name="HTTP(S) Test Script Recorder"/>
         <complink name="HTTP Request Defaults"/>
         <link href="build-adv-web-test-plan.html#session_url_rewriting">HTTP Requests and Session ID's: URL Rewriting</link>
 </links>
 
 </component>
 
 <component name="JDBC Request" index="&sect-num;.1.3"  width="466" height="334" screenshot="jdbctest/jdbc-request.png">
 
 <description><p>This sampler lets you send an JDBC Request (an SQL query) to a database.</p>
 <p>Before using this you need to set up a
 <complink name="JDBC Connection Configuration"/> Configuration element
 </p>
 <p>
 If the Variable Names list is provided, then for each row returned by a Select statement, the variables are set up
 with the value of the corresponding column (if a variable name is provided), and the count of rows is also set up.
 For example, if the Select statement returns 2 rows of 3 columns, and the variable list is <code>A,,C</code>,
 then the following variables will be set up:
 <pre>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </pre>
 If the Select statement returns zero rows, then the A_# and C_# variables would be set to 0, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves 6 rows and a second select returns only 3 rows,
 the additional variables for rows 4, 5 and 6 will be removed.
 </p>
 <p>
 <b>Note:</b> The latency time is set from the time it took to acquire a connection.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">
         Name of the JMeter variable that the connection pool is bound to.
         This must agree with the 'Variable Name' field of a JDBC Connection Configuration.
         </property>
         <property name="Query Type" required="Yes">Set this according to the statement type:
             <ul>
             <li>Select Statement</li>
             <li>Update Statement - use this for Inserts and Deletes as well</li>
             <li>Callable Statement</li>
             <li>Prepared Select Statement</li>
             <li>Prepared Update Statement - use this for Inserts and Deletes as well</li>
             <li>Commit</li>
             <li>Rollback</li>
             <li>Autocommit(false)</li>
             <li>Autocommit(true)</li>
             <li>Edit - this should be a variable reference that evaluates to one of the above</li>
             </ul>
             <note>
             When "Prepared Select Statement", "Prepared Update Statement" or "Callable Statement" types are selected, a Statement Cache per connection is used by JDBC Request. 
             It stores by default up to 100 PreparedStatements per connection, this can impact your database (Open Cursors). 
             </note>
         </property>
         <property name="SQL Query" required="Yes">
         SQL query.
         Do not enter a trailing semi-colon.
         There is generally no need to use { and } to enclose Callable statements;
         however they mey be used if the database uses a non-standard syntax.
         [The JDBC driver automatically converts the statement if necessary when it is enclosed in {}].
         For example:
         <ul>
         <li>select * from t_customers where id=23</li>
         <li>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null,?, ?, null, null, null)
         <ul>
         <li>Parameter values: tablename,filename</li>
         <li>Parameter types:  VARCHAR,VARCHAR</li>
         </ul>
         </li>
         The second example assumes you are using Apache Derby.
         </ul>
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use ]NULL[ to indicate a NULL parameter.
         (If required, the null string can be changed by defining the property "jdbcsampler.nullmarker".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <pre>"Dbl-Quote: "" and Comma: ,"</pre>
         <note>There must be as many values as there are placeholders in the statement even if your parameters are OUT ones, be sure to set a value even if the value will not be used (for example in a CallableStatement).</note>
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. INTEGER, DATE, VARCHAR, DOUBLE) or integer values of Constants when for example you use custom database types proposed by driver (-10 for OracleTypes.CURSOR for example).<br/>
         These are defined as fields in the class java.sql.Types, see for example:<br/>
         <a href="http://docs.oracle.com/javase/6/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.<br/>
         [Note: JMeter will use whatever types are defined by the runtime JVM, 
         so if you are running on a different JVM, be sure to check the appropriate document]<br/>
         <b>If the callable statement has INOUT or OUT parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "INTEGER", use "INOUT INTEGER".</b> <br/>
         If not specified, "IN" is assumed, i.e. "DATE" is the same as "IN DATE".
         <br></br>
         If the type is not one of the fields found in java.sql.Types, versions of JMeter after 2.3.2 also
         accept the corresponding integer number, e.g. since OracleTypes.CURSOR == -10, you can use "INOUT -10".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements, Prepared Select Statements or CallableStatement. 
         Note that when used with CallableStatement, list of variables must be in the same sequence as the OUT parameters returned by the call.
         If there are less variable names than OUT parameters only as many results shall be stored in the thread-context variables as variable names were supplied. 
         If more variable names than OUT parameters exist, the additional variables will be ignored</property>
         <property name="Result Variable Name" required="No">
         If specified, this will create an Object variable containing a list of row maps.
         Each map contains the column name as the key and the column data as the value. Usage:<br></br>
         <code>columnValue = vars.getObject("resultObject").get(0).get("Column Name");</code>
         </property>
         <property name="Handle ResultSet" required="No">Defines how ResultSet returned from callable statements be handled:
             <ul>
                 <li>Store As String (default) - All variables on Variable Names list are stored as strings, will not iterate through a ResultSets when present on the list.</li>
                 <li>Store As Object - Variables of ResultSet type on Variables Names list will be stored as Object and can be accessed in subsequent tests/scripts and iterated, will not iterate through the ResultSet </li>
                 <li>Count Records - Variables of ResultSet types will be iterated through showing the count of records as result. Variables will be stored as Strings.</li>
             </ul>
         </property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Versions of JMeter after 2.3.2 use UTF-8 as the character encoding. Previously the platform default was used.</note>
 <note>Ensure Variable Name is unique accross Test Plan.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="563" height="347" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <b><code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code></b> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (JavaTest and SleepTest) are provided.
 </p>
 <p>
 The JavaTest example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>Since JMeter 2.8, if the method teardownTest is not overriden by a subclass of AbstractJavaSamplerClient, its teardownTest method will not be called.
 This reduces JMeter memory requirements.
 This will not have any impact on existing Test plans.
 </note>
 <note>The Add/Delete buttons don't serve any purpose at present.</note>
 
     <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="Classname" required="Yes">The specific implementation of
         the JavaSamplerClient interface to be sampled.</property>
         <property name="Send Parameters with Request" required="No">A list of
         arguments that will be passed to the sampled class.  All arguments
         are sent as Strings. See below for specific settings.</property>
     </properties>
 
     <p>The following parameters apply to the <b>SleepTest</b> and <b>JavaTest</b> implementations:</p>
 
     <properties>
         <property name="Sleep_time" required="Yes">How long to sleep for (ms)</property>
         <property name="Sleep_mask" required="Yes">How much "randomness" to add:<br></br>
             The sleep time is calculated as follows:<br></br>
             totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)
         </property>
     </properties>
 
     <p>The following parameters apply additionaly to the <b>JavaTest</b> implementation:</p>
 
     <properties>
         <property name="Label" required="No">The label to use. If provided, overrides Name</property>
         <property name="ResponseCode" required="No">If provided, sets the SampleResult ResponseCode.</property>
         <property name="ResponseMessage" required="No">If provided, sets the SampleResult ResponseMessage.</property>
         <property name="Status" required="No">If provided, sets the SampleResult Status. If this equals "OK" (ignoring case) then the status is set to success, otherwise the sample is marked as failed.</property>
         <property name="SamplerData" required="No">If provided, sets the SampleResult SamplerData.</property>
         <property name="ResultData" required="No">If provided, sets the SampleResult ResultData.</property>
     </properties>
 </component>
 
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="426" height="276" screenshot="soap_sampler.png">
 
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content. 
 To change the "Content-type" from the default of "text/xml", use a HeaderManager. 
 Note that the sampler will use all the headers from the HeaderManager.
 If a SOAP action is specified, that will override any SOAPaction in the HeaderManager.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>For versions of JMeter later than 2.2, the sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child Header Manager
 with Content-Length set to the actual length of the file.<br/>
 Or set Content-Length to -1 to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the Header Manager)</property>
         <property name="Use KeepAlive" required="No">If set, sends Connection: keep-alive, else sends Connection: close</property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 
 </component>
 
 <component name="WebService(SOAP) Request (DEPRECATED)" useinstead="HTTP_Request" index="&sect-num;.1.6" width="943" height="648" screenshot="webservice_sampler.png">
 <description><p>This sampler has been tested with IIS Webservice running .NET 1.0 and .NET 1.1.
  It has been tested with SUN JWSDP, IBM webservices, Axis and gSoap toolkit for C/C++.
  The sampler uses Apache SOAP driver to serialize the message and set the header
  with the correct SOAPAction. Right now the sampler doesn't support automatic WSDL
  handling, since Apache SOAP currently does not provide support for it. Both IBM
  and SUN provide WSDL drivers. There are 3 options for the post data: text area,
  external file, or directory. If you want the sampler to randomly select a message,
  use the directory. Otherwise, use the text area or a file. The if either the
  file or path are set, it will not use the message in the text area. If you need
  to test a soap service that uses different encoding, use the file or path. If you
  paste the message in to text area, it will not retain the encoding and will result
  in errors. Save your message to a file with the proper encoding, and the sampler
  will read it as java.io.FileInputStream.</p>
  <p>An important note on the sampler is it will automatically use the proxy host
  and port passed to JMeter from command line, if those fields in the sampler are
  left blank. If a sampler has values in the proxy host and port text field, it
  will use the ones provided by the user. This behavior may not be what users
  expect.</p>
  <p>By default, the webservice sampler sets SOAPHTTPConnection.setMaintainSession
  (true). If you need to maintain the session, add a blank Header Manager. The
  sampler uses the Header Manager to store the SOAPHTTPConnection object, since
  the version of apache soap does not provide a easy way to get and set the cookies.</p>
  <p><b>Note:</b> If you are using CSVDataSet, do not check "Memory Cache". If memory
  cache is checked, it will not iterate to the next value. That means all the requests
  will use the first value.</p>
  <p>Make sure you use &amp;lt;soap:Envelope rather than &amp;lt;Envelope. For example:</p>
  <pre>
 &amp;lt;?xml version="1.0" encoding="utf-8"?>
 &amp;lt;soap:Envelope 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
 xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 &amp;lt;soap:Body>
 &amp;lt;foo xmlns="http://clients-xlmns"/>
 &amp;lt;/soap:Body>
 &amp;lt;/soap:Envelope>
 </pre>
 <note>The SOAP library that is used does not support SOAP 1.2, only SOAP 1.1. 
 Also the library does not provide access to the HTTP response code (e.g. 200) or message (e.g. OK). 
 To get round this, versions of JMeter after 2.3.2 check the returned message length.
 If this is zero, then the request is marked as failed.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="WSDL URL" required="No">The WSDL URL with the service description.
         Versions of JMeter after 2.3.1 support the file: protocol for local WSDL files.
         </property>
         <property name="Web Methods" required="No">Will be populated from the WSDL when the Load WSDL button is pressed.
         Select one of the methods and press the Configure button to populate the Protocol, Server, Port, Path and SOAPAction fields. 
         </property>
         <property name="Protocol" required="Yes">HTTP or HTTPS are acceptable protocol.</property>
         <property name="Server Name or IP" required="Yes">The hostname or IP address.</property>
         <property name="Port Number" required="Yes">Port Number.</property>
         <property name="Timeout" required="No">Connection timeout.</property>
         <property name="Path" required="Yes">Path for the webservice.</property>
         <property name="SOAPAction" required="Yes">The SOAPAction defined in the webservice description or WSDL.</property>
         <property name="Soap/XML-RPC Data" required="Yes">The Soap XML message</property>
         <property name="Soap file" required="No">File containing soap message</property>
         <property name="Message(s) Folder" required="No">Folder containing soap files. Files are choose randomly during test.</property>
         <property name="Memory cache" required="Yes">
         When using external files, setting this causes the file to be processed once and caches the result.
         This may use a lot of memory if there are many different large files.
         </property>
         <property name="Read SOAP Response" required="No">Read the SOAP reponse (consumes performance). Permit to have assertions or post-processors</property>
         <property name="Use HTTP Proxy" required="No">Check box if http proxy should be used</property>
         <property name="Server Name or IP" required="No">Proxy hostname</property>
         <property name="Port Number" required="No">Proxy host port</property>
         </properties>
 
 <note>
 Webservice Soap Sampler assumes that empty response means failure.
 </note>
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7" width="621" height="462" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(Add, Modify, Delete and Search) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol><li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li></ol>
 
     <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
     <ol>
       <li>Add Test</li>
       <ol><li>Inbuilt test :
         <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p></li>
           <li>User defined test :
             <p>This will add the entry in the LDAP Server. User has to enter all the
               attributes in the table.The entries are collected from the table to add. The
               execution time is calculated. The created entry will not be deleted after the
               test.</p></li></ol>
 
               <li>Modify Test</li>
               <ol><li>Inbuilt test :
                 <p>This will create a pre-defined entry first, then will modify the
                   created entry in the LDAP Server.And calculate the execution time. After
                   execution
                   of the test, the created entry will be deleted from the LDAP Server.</p></li>
                   <li>User defined test
                     <p>This will modify the entry in the LDAP Server. User has to enter all the
                       attributes in the table. The entries are collected from the table to modify.
                       The execution time is calculated. The entry will not be deleted from the LDAP
                       Server.</p></li></ol>
 
                       <li>Search Test</li>
                       <ol><li>Inbuilt test :
                         <p>This will create the entry first, then will search if the attributes
                           are available. It calculates the execution time of the search query. At the
                           end of  the execution,created entry will be deleted from the LDAP Server.</p></li>
                           <li>User defined test
                             <p>This will search the user defined entry(Search filter) in the Search
                               base (again, defined by the user). The entries should be available in the LDAP
                               Server. The execution time is  calculated.</p></li></ol>
 
                               <li>Delete Test</li>
                               <ol><li>Inbuilt test :
                                 <p>This will create a pre-defined entry first, then it will be deleted
                                   from the LDAP Server. The execution time is calculated.</p></li>
 
                                   <li>User defined test
                                     <p>This will delete the user-defined entry in the LDAP Server. The entries
                                       should be available in the LDAP Server. The execution time is calculated.</p></li></ol></ol>
                                       <properties>
                                         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
                                         <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
                                           JMeter assumes the LDAP server is listening on the default port(389).</property>
                                           <property name="Port" required="Yes">default port(389).</property>
                                           <property name="root DN" required="Yes">DN for the server to communicate</property>
                                           <property name="Username" required="Usually">LDAP server username.</property>
                                           <property name="Password" required="Usually">LDAP server password. (N.B. this is stored unencrypted in the test plan)</property>
                                           <property name="Entry DN" required="Yes">the name of the context to create or Modify; may not be empty Example: do you want to add cn=apache,ou=test
                                             you have to add in table name=cn, value=apache
                                           </property>
                                           <property name="Delete" required="Yes">the name of the context to Delete; may not be empty</property>
                                           <property name="Search base" required="Yes">the name of the context or object to search</property>
                                           <property name="Search filter" required="Yes"> the filter expression to use for the search; may not be null</property>
                                           <property name="add test" required="Yes"> this name, value pair to added in the given context object</property>
                                           <property name="modify test" required="Yes"> this name, value pair to add or modify in the given context object</property>
                                       </properties>
 
                                       <links>
                                         <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
                                         <complink name="LDAP Request Defaults"/>
                                       </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8" width="619" height="371" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP request to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <ol>
       <li><b>Thread bind</b></li>
       <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
        For starting this session a thread bind is used, which is equal to the LDAP "bind" operation.
        The user is requested to give a username (Distinguished name) and password, 
        which will be used to initiate a session.
        When no password, or the wrong password is specified, an anonymous session is started. Take care,
        omitting the password will not fail this test, a wrong password will. 
        (N.B. this is stored unencrypted in the test plan)</p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
      <property name="Servername" required="Yes">The name (or IP-address) of the LDAP server.</property>
      <property name="Port" required="No">The port number that the LDAP server is listening to. If this is omitted 
      JMeter assumes the LDAP server is listening on the default port(389).</property>
      <property name="DN" required="No">The distinguished name of the base object that will be used for any subsequent operation. 
      It can be used as a starting point for all operations. You cannot start any operation on a higher level than this DN!</property>
      <property name="Username" required="No">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error and revert to an anonymous bind. (N.B. this is stored unencrypted in the test plan)</property>
     </properties>
  <br />       
       <li><b>Thread unbind</b></li>
       <p>This is simply the operation to end a session. 
       It is equal to the LDAP "unbind" operation.</p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     </properties>
      
  <br />       
       <li><b>Single bind/unbind</b></li>
         <p> This is a combination of the LDAP "bind" and "unbind" operations.
         It can be used for an authentication request/password check for any user. It will open an new session, just to
         check the validity of the user/password combination, and end the session again.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error. (N.B. this is stored unencrypted in the test plan)</property>
      </properties>
 
  <br />       
       <li><b>Rename entry</b></li>
        <p>This is the LDAP "moddn" operation. It can be used to rename an entry, but 
        also for moving an entry or a complete subtree to a different place in 
        the LDAP tree.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      </properties>
        
  <br />       
         <li><b>Add test</b></li>
        <p>This is the ldap "add" operation. It can be used to add any kind of 
        object to the LDAP server.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
      <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
      If you need to add a multiple value attribute, you need to add the same attribute with their respective 
      values several times to the list.</property>
      </properties>
        
  <br />       
       <li><b>Delete test</b></li>
        <p> This is the LDAP "delete" operation, it can be used to delete an 
        object from the LDAP tree </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
       </properties>
        
  <br />       
       <li><b>Search test</b></li>
        <p>This is the LDAP "search" operation, and will be used for defining searches.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Search base" required="No">Distinguished name of the subtree you want your 
       search to look in, relative to the given DN in the thread bind operation.</property>
       <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
       <property name="Scope" required="No">Use 0 for baseobject-, 1 for onelevel- and 2 for a subtree search. (Default=0)</property>
       <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=0, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode 4</property>
       <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in miliseconds) that the server can spend on your search. Take care, this does not say anything about the responsetime. (default is 0, which means no limit)</property>
       <property name="Attributes" required="No">Specify the attributes you want to have returned, seperated by a semicolon. An empty field will return all attributes</property>
       <property name="Return object" required="No">Whether the object will be returned (true) or not (false). Default=false</property>
       <property name="Dereference aliases" required="No">If true, it will dereference aliases, if false, it will not follow them (default=false)</property>
      </properties>
 
  <br />       
       <li><b>Modification test</b></li>
        <p>This is the LDAP "modify" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
       to the given DN in the thread bind operation</property>
      <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any 
      valid LDAP operationCode (add, delete/remove or replace). If you don't specify a value with a delete operation,
      all values of the given attribute will be deleted. If you do specify a value in a delete operation, only 
      the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
      </properties>
        
  <br />       
       <li><b>Compare</b></li>
        <p>This is the LDAP "compare" operation. It can be used to compare the value 
        of a given attribute with some already known value. In reality this is mostly 
        used to check whether a given person is a member of some group. In such a case
         you can compare the DN of the user as a given value, with the values in the
          attribute "member" of an object of the type groupOfNames.
          If the compare operation fails, this test fails with errorcode 49.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">The current distinguished name of the object of 
       which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
      <property name="Compare filter" required="Yes">In the form "attribute=value"</property>
      </properties>
     </ol>
        
     <links>
       <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
       <complink name="LDAP Extended Request Defaults"/>
     </links>
 
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="613" height="318" screenshot="accesslogsampler.png">
 <center><h2>(Beta Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means every image, css file, javascript file, html file.... 
 The current implementation is complete, but some features have not been enabled. 
 There is a filter for the access log parser, but I haven't figured out how to link to the pre-processor. 
 Once I do, changes to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <p>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</p>
 <note>The current implementation of the parser only looks at the text within the quotes that contains one of the HTTP protocol methods (GET, PUT, POST, DELETE...).
 Everything else is stripped out and ignored. For example, the response code is completely
 ignored by the parser. </note>
 <p>For the future, it might be nice to filter out entries that
 do not have a response code of 200. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement:</p>
 <ul>
 <li>org.apache.jmeter.protocol.http.util.accesslog.LogParser</li>
 <li>org.apache.jmeter.protocol.http.util.accesslog.Generator</li>
 </ul>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer 1, telling it to parse one entry. After that,
 HTTPSampler.sample() is called to make the request.
 <code>
 <pre>
             samp = (HTTPSampler) GENERATOR.generateRequest();
             samp.setDomain(this.getDomain());
             samp.setPort(this.getPort());
             samp.setImageParser(this.isImageParser());
             PARSER.parse(1);
             res = samp.sample();
             res.setSampleLabel(samp.toString());
 </pre>
 </code>
 The required methods in LogParser are:
 <ul>
 <li>setGenerator(Generator)</li>
 <li>parse(int)</li> 
 </ul>
 Classes implementing Generator interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 StandardGenerator and TCLogParser.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The TCLogParser processes the access log independently for each thread.
 The SharedTCLogParser and OrderPreservingLogParser share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The SessionFilter is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The LogFilter is intended to allow access log entries to be filtered by filename and regex,
 as well as allowing for the replacement of file extensions. However, it is not currently possible
 to configure this via the GUI, so it cannot really be used.
 </p>
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="848" height="566" screenshot="beanshellsampler.png">
     <description><p>This sampler allows you to write a sampler using the BeanShell scripting language.
 </p><p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener interface methods.
 These must be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 <p>
 From JMeter version 2.5.1, the BeanShell sampler also supports the Interruptible interface.
 The interrupt() method can be defined in the script or the init file.
 </p>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.
     The name is stored in the script variable Label</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     This is intended for use with script files; for scripts defined in the GUI, you can use whatever
     variable and function references you need within the script itself.
     The parameters are stored in the following variables:
     <ul>
         <li>Parameters - string containing the parameters as a single variable</li>
         <li>bsh.args - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable FileName</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not null) is stored as the sampler result.</property>
 </properties>
 <p>
 N.B. Each Sampler instance has its own BeanShell interpeter,
 and Samplers are only called from a single thread
 </p><p>
 If the property "beanshell.sampler.init" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: BeanShellSampler.bshrc.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g. props.get("START.HMS"); props.put("PROP1","1234");
 <br/>
 BeanShell does not currently support Java 5 syntax such as generics and the enhanced for loop.
 </note>
         <p>Before invoking the script, some variables are set up in the BeanShell interpreter:
             </p>
                 <p>The contents of the Parameters field is put into the variable "Parameters".
             The string is also split into separate tokens using a single space as the separator, and the resulting list
             is stored in the String array bsh.args.</p>
             <p>The full list of BeanShell variables that is set up is as follows:</p>
         <ul>
         <li>log - the Logger</li>
         <li>Label - the Sampler label</li>
         <li>FileName - the file name, if any</li>
         <li>Parameters - text from the Parameters field</li>
         <li>bsh.args - the parameters, split as described above</li>
         <li>SampleResult - pointer to the current SampleResult</li>
             <li>ResponseCode = 200</li>
             <li>ResponseMessage = "OK"</li>
             <li>IsSuccess = true</li>
             <li>ctx - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
             <li>vars - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
             <li>props - JMeterProperties (class java.util.Properties)- e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
         </ul>
         <p>When the script completes, control is returned to the Sampler, and it copies the contents
             of the following script variables into the corresponding variables in the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>:</p>
             <ul>
             <li>ResponseCode - for example 200</li>
             <li>ResponseMessage - for example "OK"</li>
             <li>IsSuccess - true/false</li>
             </ul>
             <p>The SampleResult ResponseData is set from the return value of the script.
             Since version 2.1.2, if the script returns null, it can set the response directly, by using the method 
             SampleResult.setResponseData(data), where data is either a String or a byte array.
             The data type defaults to "text", but can be set to binary by using the method
             SampleResult.setDataType(SampleResult.BINARY).
             </p>
             <p>The SampleResult variable gives the script full access to all the fields and
                 methods in the SampleResult. For example, the script has access to the methods
                 setStopThread(boolean) and setStopTest(boolean).
 
                 Here is a simple (not very useful!) example script:</p>
 
 <pre>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or, since version 2.1.2
 SampleResult.setResponseData("My data");
 return null;
 </pre>
 <p>Another example:<br></br> ensure that the property <b>beanshell.sampler.init=BeanShellSampler.bshrc</b> is defined in jmeter.properties. 
 The following script will show the values of all the variables in the ResponseData field:
 </p>
 <pre>
 return getVariables();
 </pre>
 <p>
 For details on the methods available for the various classes (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>, <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> etc) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find ...
 </p>
 </component>
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="848" height="590" screenshot="bsfsampler.png">
     <description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
         See the <a href="http://commons.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
         website for details of the languages supported.
         You may need to download the appropriate jars for the language; they should be put in the JMeter <b>lib</b> directory.
         </p>
         <note>
         The BSF API has been largely superseded by JSR-223, which is included in Java 6 onwards.
         Most scripting languages now include support for JSR-223; please use the JSR223 Sampler instead.
         The BSF Sampler should only be needed for supporting legacy languages/test scripts.
         </note>
         <p>By default, JMeter supports the following languages:</p>
         <ul>
         <li>javascript</li>
         <li>jexl (JMeter version 2.3.2 and later)</li>
         <li>xslt</li>
         </ul>
         <note>Unlike the BeanShell sampler, the interpreter is not saved between invocations.</note>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
     N.B. Not all the languages in the drop-down list are supported by default.
     The following are supported: jexl, javascript, xslt.
     Others may be available if the appropriate jar is installed in the JMeter lib directory.
     </property>
     <property name="Script File" required="No">Name of a file to be used as a BSF script, if a relative file path is used, then it will be relative to directory referenced by "user.dir" System property</property>
     <property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
     <property name="Script" required="Yes (unless script file is provided)">Script to be passed to BSF language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g. props.get("START.HMS"); props.put("PROP1","1234");
 </note>
