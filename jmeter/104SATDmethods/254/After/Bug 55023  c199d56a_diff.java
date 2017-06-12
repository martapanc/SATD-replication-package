diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
index 0e89a60ff..29adbbe5b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
@@ -1,274 +1,274 @@
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
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Base class for HTTP implementations used by the HTTPSamplerProxy sampler.
  */
 public abstract class HTTPAbstractImpl implements Interruptible, HTTPConstantsInterface {
 
     protected final HTTPSamplerBase testElement;
 
     protected HTTPAbstractImpl(HTTPSamplerBase testElement){
         this.testElement = testElement;
     }
 
     protected abstract HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth);
 
     // Allows HTTPSamplerProxy to call threadFinished; subclasses can override if necessary
     protected void threadFinished() {
     }
 
-    // Allows HTTPSamplerProxy to call testIterationStart; subclasses can override if necessary
-    protected void testIterationStart(LoopIterationEvent event) {
+    // Allows HTTPSamplerProxy to call notifyFirstSampleAfterLoopRestart; subclasses can override if necessary
+    protected void notifyFirstSampleAfterLoopRestart() {
     }
 
     // Provide access to HTTPSamplerBase methods
     
     /**
      * Invokes {@link HTTPSamplerBase#errorResult(Throwable, HTTPSampleResult)}
      */
     protected HTTPSampleResult errorResult(Throwable t, HTTPSampleResult res) {
         return testElement.errorResult(t, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getArguments()}
      */
     protected Arguments getArguments() {
         return testElement.getArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAuthManager()}
      */
     protected AuthManager getAuthManager() {
         return testElement.getAuthManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAutoRedirects()}
      */
     protected boolean getAutoRedirects() {
         return testElement.getAutoRedirects();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCacheManager()}
      */
     protected CacheManager getCacheManager() {
         return testElement.getCacheManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getConnectTimeout()}
      */
     protected int getConnectTimeout() {
         return testElement.getConnectTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getContentEncoding()}
      * @return the encoding of the content, i.e. its charset name
      */
     protected String getContentEncoding() {
         return testElement.getContentEncoding();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCookieManager()}
      */
     protected CookieManager getCookieManager() {
         return testElement.getCookieManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHeaderManager()}
      */
     protected HeaderManager getHeaderManager() {
         return testElement.getHeaderManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHTTPFiles()}
      */
     protected HTTPFileArg[] getHTTPFiles() {
         return testElement.getHTTPFiles();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getIpSource()}
      */
     protected String getIpSource() {
         return testElement.getIpSource();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyHost()}
      */
     protected String getProxyHost() {
         return testElement.getProxyHost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPass()}
      */
     protected String getProxyPass() {
         return testElement.getProxyPass();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPortInt()}
      */
     protected int getProxyPortInt() {
         return testElement.getProxyPortInt();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyUser()}
      */
     protected String getProxyUser() {
         return testElement.getProxyUser();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getResponseTimeout()}
      */
     protected int getResponseTimeout() {
         return testElement.getResponseTimeout();
     }
 
     /**
      * Determine whether to send a file as the entire body of an
      * entity enclosing request such as POST, PUT or PATCH.
      * 
      * Invokes {@link HTTPSamplerBase#getSendFileAsPostBody()}
      */
     protected boolean getSendFileAsPostBody() {
         return testElement.getSendFileAsPostBody();
     }
 
     /**
      * Determine whether to send concatenated parameters as the entire body of an
      * entity enclosing request such as POST, PUT or PATCH.
      * 
      * Invokes {@link HTTPSamplerBase#getSendParameterValuesAsPostBody()}
      */
     protected boolean getSendParameterValuesAsPostBody() {
         return testElement.getSendParameterValuesAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseKeepAlive()}
      */
     protected boolean getUseKeepAlive() {
         return testElement.getUseKeepAlive();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseMultipartForPost()}
      */
     protected boolean getUseMultipartForPost() {
         return testElement.getUseMultipartForPost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getDoBrowserCompatibleMultipart()}
      */
     protected boolean getDoBrowserCompatibleMultipart() {
         return testElement.getDoBrowserCompatibleMultipart();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#hasArguments()}
      */
     protected boolean hasArguments() {
         return testElement.hasArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isMonitor()}
      */
     protected boolean isMonitor() {
         return testElement.isMonitor();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isSuccessCode(int)}
      */
     protected boolean isSuccessCode(int errorLevel) {
         return testElement.isSuccessCode(errorLevel);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, InputStream instream,
             int responseContentLength) throws IOException {
         return testElement.readResponse(res, instream, responseContentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, BufferedInputStream in,
             int contentLength) throws IOException {
         return testElement.readResponse(res, in, contentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#resultProcessing(boolean, int, HTTPSampleResult)}
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect,
             int frameDepth, HTTPSampleResult res) {
         return testElement.resultProcessing(areFollowingRedirect, frameDepth, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#setUseKeepAlive(boolean)}
      */
     protected void setUseKeepAlive(boolean b) {
         testElement.setUseKeepAlive(b);
     }
 
     /**
      * Called by testIterationStart if the SSL Context was reset.
      * 
      * This implementation does nothing.
      */
     protected void notifySSLContextWasReset() {
         // NOOP
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index ff2a7bf0f..70c9b91b1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -1,1154 +1,1152 @@
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
-import org.apache.commons.httpclient.methods.DeleteMethod;
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
-import org.apache.jmeter.engine.event.LoopIterationEvent;
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
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
  */
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 1); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient3.retrycount", 1);
 
     private static final String HTTP_AUTHENTICATION_PREEMPTIVE = "http.authentication.preemptive"; // $NON-NLS-1$
 
     private static final boolean canSetPreEmptive; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients = 
         new ThreadLocal<Map<HostConfiguration, HttpClient>>(){
         @Override
         protected Map<HostConfiguration, HttpClient> initialValue() {
             return new HashMap<HostConfiguration, HttpClient>();
         }
     };
 
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
 
         // Process Commons HttpClient parameters file
         String file=JMeterUtils.getProperty("httpclient.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, params);
         }
 
         // If the pre-emptive parameter is undefined, then we can set it as needed
         // otherwise we should do what the user requested.
         canSetPreEmptive =  params.getParameter(HTTP_AUTHENTICATION_PREEMPTIVE) == null;
 
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
 
         log.debug("Start : sample " + urlStr);
         log.debug("method " + method);
 
         HttpMethodBase httpMethod = null;
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         res.sampleStart(); // Count the retries as well in the time
         try {
             // May generate IllegalArgumentException
             if (method.equals(HTTPConstants.POST)) {
                 httpMethod = new PostMethod(urlStr);
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
             } else if (method.equals(HTTPConstants.GET)){
                 httpMethod = new GetMethod(urlStr);
             } else if (method.equals(HTTPConstants.PATCH)){
                 httpMethod = new EntityEnclosingMethod(urlStr) {
                     @Override
                     public String getName() { // HC3.1 does not have the method
                         return HTTPConstants.PATCH;
                     }
                 };
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
 
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
                if (cacheManager.inCache(url)) {
                    res.sampleEnd();
                    res.setResponseNoContent();
                    res.setSuccessful(true);
                    return res;
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
                         res.setResponseData(readResponse(res, tmpInput, (int) httpMethod.getResponseContentLength()));                        
                     } else {
                         res.setResponseData(readResponse(res, instream, (int) httpMethod.getResponseContentLength()));
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
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             if (instream != null) {
                 res.setBodySize(((CountingInputStream) instream).getCount());
             }
             res.setHeadersSize(calculateHeadersSize(httpMethod));
             if (log.isDebugEnabled()) {
                 log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
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
         } catch (IllegalArgumentException e) { // e.g. some kinds of invalid URL
             res.sampleEnd();
             // pick up headers if failed to execute the request
             // httpMethod can be null if method is unexpected
             if(httpMethod != null) {
                 res.setRequestHeaders(getConnectionHeaders(httpMethod));
             }
             errorResult(e, res);
             return res;
         } catch (IOException e) {
             res.sampleEnd();
             // pick up headers if failed to execute the request
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
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
         for (int i = 0; i < rh.length; i++) {
             headerSize += rh[i].toString().length(); // already include the \r\n
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
         if (localAddress != null){
             hc.setLocalAddress(localAddress);
         } else {
             final String ipSource = getIpSource();
             if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
                 InetAddress inetAddr = InetAddress.getByName(ipSource);
                 hc.setLocalAddress(inetAddr);
             }
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
                         new NTCredentials(user,getProxyPass(),localHost,PROXY_DOMAIN)
                     );
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         } else {
             if (useStaticProxy) {
                 if (PROXY_USER.length() > 0){
                     httpClient.getState().setProxyCredentials(
                         new AuthScope(PROXY_HOST,PROXY_PORT,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(PROXY_USER,PROXY_PASS,localHost,PROXY_DOMAIN)
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
         org.apache.commons.httpclient.Header rh[] = method.getResponseHeaders();
         headerBuf.append(method.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             String key = rh[i].getName();
             headerBuf.append(key);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
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
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
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
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
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
                                     localHost,
                                     domain
                             ));
                     // We have credentials - should we set pre-emptive authentication?
                     if (canSetPreEmptive){
                         log.debug("Setting Pre-emptive authentication");
                         params.setAuthenticationPreemptive(true);
                     }
             } else {
                 state.clearCredentials();
                 if (canSetPreEmptive){
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
         HTTPFileArg files[] = getHTTPFiles();
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
             ArrayList<PartBase> partlist = new ArrayList<PartBase>();
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)){
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
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 File inputFile = new File(file.getPath());
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
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
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
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)){
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if(!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             String urlContentEncoding = contentEncoding;
                             if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
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
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(files[0].getPath()),null);
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
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String value = null;
                 if (haveContentEncoding){
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
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 put.getRequestEntity().writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 putBody.append(new String(bos.toByteArray(),put.getRequestCharSet()));
                 bos.close();
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
             Header hdr[] = method.getResponseHeaders(HTTPConstants.HEADER_SET_COOKIE);
             for (int i = 0; i < hdr.length; i++) {
                 cookieManager.addCookieFromHeader(hdr[i].getValue(),u);
             }
         }
     }
 
 
     @Override
     protected void threadFinished() {
         log.debug("Thread Finished");
 
         closeThreadLocalConnections();
     }
 
     @Override
-    protected void testIterationStart(LoopIterationEvent event) {
-        log.debug("TtestIterationStart");
+    protected void notifyFirstSampleAfterLoopRestart() {
+        log.debug("notifyFirstSampleAfterLoopRestart");
         resetSSLContext = !USE_CACHED_SSL_CONTEXT;
     }
 
     /**
      * 
      */
     private void closeThreadLocalConnections() {
         // Does not need to be synchronised, as all access is from same thread
         Map<HostConfiguration, HttpClient> map = httpClients.get();
 
         if ( map != null ) {
             for (HttpClient cl : map.values())
             {
                 // Can cause NPE in HttpClient 3.1
                 //((SimpleHttpConnectionManager)cl.getHttpConnectionManager()).shutdown();// Closes the connection
                 // Revert to original method:
                 cl.getHttpConnectionManager().closeIdleConnections(-1000);// Closes the connection
             }
             map.clear();
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt() {
         HttpClient client = savedClient;
         if (client != null) {
             savedClient = null;
             // TODO - not sure this is the best method
             final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
             if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                 ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
             }
         }
         return client != null;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 444357c14..4fa56f07b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -186,1042 +186,1042 @@ public class HTTPHC4Impl extends HTTPHCAbstractImpl {
         } else {
             SLOW_HTTP = null;
         }
         
         // We always want to override the HTTPS scheme
         Scheme https = null;
         if (CPS_HTTPS > 0) {
             log.info("Setting up HTTPS SlowProtocol, cps="+CPS_HTTPS);
             try {
                 https = new Scheme(HTTPConstants.PROTOCOL_HTTPS, HTTPConstants.DEFAULT_HTTPS_PORT, new SlowHC4SSLSocketFactory(CPS_HTTPS));
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise SLOW_HTTPS scheme, cps="+CPS_HTTPS, e);
             }
         } else {
             log.info("Setting up HTTPS TrustAll scheme");
             try {
                 https = new Scheme(HTTPConstants.PROTOCOL_HTTPS, HTTPConstants.DEFAULT_HTTPS_PORT, new HC4TrustAllSSLSocketFactory());
             } catch (GeneralSecurityException e) {
                 log.warn("Failed to initialise HTTPS TrustAll scheme", e);
             }
         }
         HTTPS_SCHEME = https;
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     private volatile boolean resetSSLContext;
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
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
 
         HTTPSampleResult res = createSampleResult(url, method);
 
         HttpClient httpClient = setupClient(url);
         
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(HTTPConstants.POST)) {
                 httpRequest = new HttpPost(uri);
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
             } else if (method.equals(HTTPConstants.GET)) {
                 httpRequest = new HttpGet(uri);
             } else if (method.equals(HTTPConstants.PATCH)) {
                 httpRequest = new HttpPatch(uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             errorResult(e, res);
             return res;
         }
 
         HttpContext localContext = new BasicHttpContext();
         
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                res.sampleEnd();
                res.setResponseNoContent();
                res.setSuccessful(true);
                return res;
            }
         }
 
         try {
             currentRequest = httpRequest;
             handleMethod(method, res, httpRequest, localContext);
             HttpResponse httpResponse = httpClient.execute(httpRequest, localContext); // perform the sample
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST)));
 
             Header contentType = httpResponse.getLastHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 InputStream instream = entity.getContent();
                 res.setResponseData(readResponse(res, instream, (int) entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             HttpConnectionMetrics  metrics = (HttpConnectionMetrics) localContext.getAttribute(CONTEXT_METRICS);
             long headerBytes = 
                 res.getResponseHeaders().length()   // condensed length (without \r)
               + httpResponse.getAllHeaders().length // Add \r for each header
               + 1 // Add \r for initial header
               + 2; // final \r\n before data
             long totalBytes = metrics.getReceivedBytesCount();
             res.setHeadersSize((int) headerBytes);
             res.setBodySize((int)(totalBytes - headerBytes));
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
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
             res.sampleEnd();
            // pick up headers if failed to execute the request
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST)));
             errorResult(e, res);
             return res;
         } catch (RuntimeException e) {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } finally {
             currentRequest = null;
         }
         return res;
     }
 
     /**
      * Calls sendPostData if method is POST and sendEntityData if method is PUT or PATCH
      * Field HTTPSampleResult#queryString of result is modified in the 2 cases
      * @param method String HTTP method
      * @param result {@link HTTPSampleResult}
      * @param httpRequest {@link HttpRequestBase}
      * @param localContext {@link HttpContext}
      * @throws IOException
      */
     protected void handleMethod(String method, HTTPSampleResult result,
             HttpRequestBase httpRequest, HttpContext localContext) throws IOException {
         // Handle the various methods
         if (method.equals(HTTPConstants.POST)) {
             String postBody = sendPostData((HttpPost)httpRequest);
             result.setQueryString(postBody);
         } else if (method.equals(HTTPConstants.PUT) || method.equals(HTTPConstants.PATCH)
                 || method.equals(HTTPConstants.DELETE)) {
             String entityBody = sendEntityData(( HttpEntityEnclosingRequestBase)httpRequest);
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
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
         
         return res;
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
             // N.B. need to separate protocol from authority otherwise http://server would match https://erver
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
     }
 
     private HttpClient setupClient(URL url) {
 
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         
         final String host = url.getHost();
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, (useStaticProxy || useDynamicProxy), 
                 useDynamicProxy ? proxyHost : PROXY_HOST,
                 useDynamicProxy ? proxyPort : PROXY_PORT,
                 useDynamicProxy ? getProxyUser() : PROXY_USER,
                 useDynamicProxy ? getProxyPass() : PROXY_PASS);
         
         HttpClient httpClient = map.get(key);
 
         if (httpClient != null && resetSSLContext && HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(url.getProtocol())) {
             ((AbstractHttpClient) httpClient).clearRequestInterceptors(); 
             ((AbstractHttpClient) httpClient).clearResponseInterceptors(); 
             httpClient.getConnectionManager().shutdown();
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if (httpClient == null){ // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
             
             httpClient = new DefaultHttpClient(clientParams){
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false); // set retry count
                 }
             };
             ((AbstractHttpClient) httpClient).addResponseInterceptor(new ResponseContentEncoding());
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             ((AbstractHttpClient) httpClient).addRequestInterceptor(METRICS_RESETTER); 
             
             // Override the defualt schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             if (HTTPS_SCHEME != null){
                 schemeRegistry.register(HTTPS_SCHEME);
             }
 
             // Set up proxy details
             if (useDynamicProxy){
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 String proxyUser = getProxyUser();
                 
                 if (proxyUser.length() > 0) {                   
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new NTCredentials(proxyUser, getProxyPass(), localHost, PROXY_DOMAIN));
                 }
             } else if (useStaticProxy) {
                 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 if (PROXY_USER.length() > 0) {
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(PROXY_HOST, PROXY_PORT),
                             new NTCredentials(PROXY_USER, PROXY_PASS, localHost, PROXY_DOMAIN));
                 }
             }
 
             // Bug 52126 - we do our own cookie handling
             clientParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
 
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
 
             map.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager(), key);
 
         return httpClient;
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
      * @param url
      * @param httpRequest
      * @param res
      * @throws IOException
      */
     protected void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final String ipSource = getIpSource();
     if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
         InetAddress inetAddr = InetAddress.getByName(ipSource);
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
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response) {
         StringBuilder headerBuf = new StringBuilder();
         Header[] rh = response.getAllHeaders();
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             headerBuf.append(rh[i].getName());
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
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
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = url.getPort();
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
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getAllHeaders();
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
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
             Authorization auth = authManager.getAuthForURL(url);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " > D="+domain+" R="+realm);
                     }
                     credentialsProvider.setCredentials(
                             new AuthScope(url.getHost(), url.getPort(), realm.length()==0 ? null : realm),
                             new NTCredentials(username, auth.getPass(), localHost, domain));
             } else {
                 credentialsProvider.clear();
             }
         } else {
             Credentials credentials = null;
             AuthScope authScope = null;
             if(key.hasProxy && !StringUtils.isEmpty(key.proxyUser)) {
                 authScope = new AuthScope(key.proxyHost, key.proxyPort);
                 credentials = credentialsProvider.getCredentials(authScope);
             }
             credentialsProvider.clear(); 
             if(credentials != null) {
                 credentialsProvider.setCredentials(authScope, credentials);
             }
         }
     }
 
     // Helper class so we can generate request data without dumping entire file contents
     private static class ViewableFileBody extends FileBody {
         private boolean hideFileData;
         
         public ViewableFileBody(File file, String mimeType) {
             super(file, mimeType);
             hideFileData = false;
         }
 
         @Override
         public void writeTo(final OutputStream out) throws IOException {
             if (hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             } else {
                 super.writeTo(out);
             }
         }
     }
 
     // TODO needs cleaning up
     /**
      * 
      * @param post {@link HttpPost}
      * @return String posted body if computable
      * @throws IOException
      */
     protected String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg files[] = getHTTPFiles();
 
         final String contentEncoding = getContentEncodingOrNull();
         final boolean haveContentEncoding = contentEncoding != null;
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             Charset charset = null;
             if(haveContentEncoding) {
                 charset = Charset.forName(contentEncoding);
             }
 
             // Write the request to our own stream
             MultipartEntity multiPart = new MultipartEntity(
                     getDoBrowserCompatibleMultipart() ? HttpMultipartMode.BROWSER_COMPATIBLE : HttpMultipartMode.STRICT,
                             null, charset);
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                String parameterName = arg.getName();
                if (arg.isSkippable(parameterName)){
                    continue;
                }
                FormBodyPart formPart;
                StringBody stringBody = new StringBody(arg.getValue(), charset);
                formPart = new FormBodyPart(arg.getName(), stringBody);                   
                multiPart.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 fileBodies[i] = new ViewableFileBody(new File(file.getPath()), file.getMimeType());
                 multiPart.addPart(file.getParamName(),fileBodies[i]);
             }
 
             post.setEntity(multiPart);
 
             if (multiPart.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 multiPart.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
             } else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
 
 //            // Set the content type TODO - needed?
 //            String multiPartContentType = multiPart.getContentType().getValue();
 //            post.setHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
         } else { // not multipart
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),(ContentType) null);// TODO is null correct?
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 if(haveContentEncoding) {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
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
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                         if (haveContentEncoding) {
                             postBody.append(arg.getEncodedValue(contentEncoding));                    
                         } else {
                             postBody.append(arg.getEncodedValue());
                         }
                     }
                     // Let StringEntity perform the encoding
                     StringEntity requestEntity = new StringEntity(postBody.toString(), contentEncoding);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString());
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                     String urlContentEncoding = contentEncoding;
                     if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                         // Use the default encoding for urls
                         urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                     }
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)){
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if(!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         nvps.add(new BasicNameValuePair(parameterName, parameterValue));
                     }
                     UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, urlContentEncoding);
                     post.setEntity(entity);
                     if (entity.isRepeatable()){
                         ByteArrayOutputStream bos = new ByteArrayOutputStream();
                         post.getEntity().writeTo(bos);
                         bos.flush();
                         // We get the posted bytes using the encoding used to create it
                         if (contentEncoding != null) {
                             postedBody.append(new String(bos.toByteArray(), contentEncoding));
                         } else {
                             postedBody.append(new String(bos.toByteArray(), SampleResult.DEFAULT_HTTP_ENCODING));
                         }
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
             }
         }
         return postedBody.toString();
     }
 
     // TODO merge put and post methods as far as possible.
     // e.g. post checks for multipart form/files, and if not, invokes sendData(HttpEntityEnclosingRequestBase)
 
 
     /**
      * Creates the entity data to be sent.
      * <p>
      * If there is a file entry with a non-empty MIME type we use that to
      * set the request Content-Type header, otherwise we default to whatever
      * header is present from a Header Manager.
      * <p>
      * If the content charset {@link #getContentEncoding()} is null or empty 
      * we use the HC4 default provided by {@link HTTP#DEF_CONTENT_CHARSET} which is
      * ISO-8859-1.
      * 
      * @param entity to be processed, e.g. PUT or PATCH
      * @return the entity content, may be empty
      * @throws  UnsupportedEncodingException for invalid charset name
      * @throws IOException cannot really occur for ByteArrayOutputStream methods
      */
     protected String sendEntityData( HttpEntityEnclosingRequestBase entity) throws IOException {
         // Buffer to hold the entity body
         StringBuilder entityBody = new StringBuilder(1000);
         boolean hasEntityBody = false;
 
         final HTTPFileArg files[] = getHTTPFiles();
         // Allow the mimetype of the file to control the content type
         // This is not obvious in GUI if you are not uploading any files,
         // but just sending the content of nameless parameters
         final HTTPFileArg file = files.length > 0? files[0] : null;
         String contentTypeValue = null;
         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
             contentTypeValue = file.getMimeType();
             entity.setHeader(HEADER_CONTENT_TYPE, contentTypeValue); // we provide the MIME type here
         }
 
         // Check for local contentEncoding (charset) override; fall back to default for content body
         // we do this here rather so we can use the same charset to retrieve the data
         final String charset = getContentEncoding(HTTP.DEF_CONTENT_CHARSET.name());
 
         // Only create this if we are overriding whatever default there may be
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasEntityBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileEntity fileRequestEntity = new FileEntity(new File(files[0].getPath())); // no need for content-type here
             entity.setEntity(fileRequestEntity);
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the entity body
         else if(getSendParameterValuesAsPostBody()) {
             hasEntityBody = true;
 
             // Just append all the parameter values, and use that as the entity body
             StringBuilder entityBodyContent = new StringBuilder();
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                 if (charset!= null) {
                     entityBodyContent.append(arg.getEncodedValue(charset));                    
                 } else {
                     entityBodyContent.append(arg.getEncodedValue());
                 }
             }
             StringEntity requestEntity = new StringEntity(entityBodyContent.toString(), charset);
             entity.setEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasEntityBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             final HttpEntity entityEntry = entity.getEntity();
             if(entityEntry.isRepeatable()) {
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 entityEntry.writeTo(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 entityBody.append(new String(bos.toByteArray(), charset));
                 bos.close();
             }
             else { // this probably cannot happen
                 entityBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
         }
         return entityBody.toString(); // may be the empty string
     }
 
     /**
      * 
      * @return the value of {@link #getContentEncoding()}; forced to null if empty
      */
     private String getContentEncodingOrNull() {
         return getContentEncoding(null);
     }
 
     /**
      * @param dflt the default to be used
      * @return the value of {@link #getContentEncoding()}; default if null or empty
      */
     private String getContentEncoding(String dflt) {
         String ce = getContentEncoding();
         if (isNullOrEmptyTrimmed(ce)) {
             return dflt;
         } else {
             return ce;
         }
     }
 
     /**
      * If contentEncoding is not set by user, then Platform encoding will be used to convert to String
      * @param putParams {@link HttpParams}
      * @return String charset
      */
     protected String getCharsetWithDefault(HttpParams putParams) {
         String charset =(String) putParams.getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET);
         if(StringUtils.isEmpty(charset)) {
             charset = Charset.defaultCharset().name();
         }
         return charset;
     }
 
     private void saveConnectionCookies(HttpResponse method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdrs = method.getHeaders(HTTPConstants.HEADER_SET_COOKIE);
             for (Header hdr : hdrs) {
                 cookieManager.addCookieFromHeader(hdr.getValue(),u);
             }
         }
     }
 
     @Override
-    protected void testIterationStart(LoopIterationEvent event) {
-        log.debug("TtestIterationStart");
+    protected void notifyFirstSampleAfterLoopRestart() {
+        log.debug("notifyFirstSampleAfterLoopRestart");
         resetSSLContext = !USE_CACHED_SSL_CONTEXT;
     }
 
     @Override
     protected void threadFinished() {
         log.debug("Thread Finished");
         closeThreadLocalConnections();
     }
 
     /**
      * 
      */
     private void closeThreadLocalConnections() {
         // Does not need to be synchronised, as all access is from same thread
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         if ( map != null ) {
             for ( HttpClient cl : map.values() ) {
                 ((AbstractHttpClient) cl).clearRequestInterceptors(); 
                 ((AbstractHttpClient) cl).clearResponseInterceptors(); 
                 cl.getConnectionManager().shutdown();
             }
             map.clear();
         }
     }
 
     @Override
     public boolean interrupt() {
         HttpUriRequest request = currentRequest;
         if (request != null) {
             currentRequest = null; // don't try twice
             try {
                 request.abort();
             } catch (UnsupportedOperationException e) {
                 log.warn("Could not abort pending request", e);
             }
         }
         return request != null;
     }
     
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
index 12617d2cf..6031312ba 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
@@ -1,87 +1,87 @@
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
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.HttpMethodBase;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
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
 
     @Override
     public boolean interrupt() {
         return hc.interrupt();
     }
 
     @Override
     protected HTTPSampleResult sample(URL u, String method,
             boolean areFollowingRedirect, int depth) {
         return hc.sample(u, method, areFollowingRedirect, depth);
     }
 
     // Methods needed by subclasses to get access to the implementation
     protected HttpClient setupConnection(URL url, HttpMethodBase httpMethod, HTTPSampleResult res) 
         throws IOException {
         return hc.setupConnection(url, httpMethod, res);
     }
 
     protected void saveConnectionCookies(HttpMethod httpMethod, URL url,
             CookieManager cookieManager) {
         hc.saveConnectionCookies(httpMethod, url, cookieManager);
    }
 
     protected String getResponseHeaders(HttpMethod httpMethod) {
         return hc.getResponseHeaders(httpMethod);
     }
 
     protected String getConnectionHeaders(HttpMethod httpMethod) {
         return hc.getConnectionHeaders(httpMethod);
     }
 
     protected void setSavedClient(HttpClient savedClient) {
         hc.savedClient = savedClient;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase#testIterationStart(org.apache.jmeter.engine.event.LoopIterationEvent)
      */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
-        hc.testIterationStart(event);
+        hc.notifyFirstSampleAfterLoopRestart();
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
index a12a8ffe5..1af54b3e8 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerProxy.java
@@ -1,112 +1,102 @@
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
 
 import java.net.URL;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.samplers.Interruptible;
 
 /**
  * Proxy class that dispatches to the appropriate HTTP sampler.
  * <p>
  * This class is stored in the test plan, and holds all the configuration settings.
  * The actual implementation is created at run-time, and is passed a reference to this class
  * so it can get access to all the settings stored by HTTPSamplerProxy.
  */
 public final class HTTPSamplerProxy extends HTTPSamplerBase implements Interruptible {
 
     private static final long serialVersionUID = 1L;
 
     private transient HTTPAbstractImpl impl;
     
-    private transient Exception initException;
+    private transient volatile boolean notifyFirstSampleAfterLoopRestart;
 
     public HTTPSamplerProxy(){
         super();
     }
     
     /**
      * Convenience method used to initialise the implementation.
      * 
      * @param impl the implementation to use.
      */
     public HTTPSamplerProxy(String impl){
         super();
         setImplementation(impl);
     }
         
     /** {@inheritDoc} */
     @Override
     protected HTTPSampleResult sample(URL u, String method, boolean areFollowingRedirect, int depth) {
         // When Retrieve Embedded resources + Concurrent Pool is used
         // as the instance of Proxy is cloned, we end up with impl being null
         // testIterationStart will not be executed but it's not a problem for 51380 as it's download of resources
         // so SSL context is to be reused
         if (impl == null) { // Not called from multiple threads, so this is OK
             try {
-                if(initException != null) {
-                    return errorResult(initException, new HTTPSampleResult());
-                }
                 impl = HTTPSamplerFactory.getImplementation(getImplementation(), this);
             } catch (Exception ex) {
                 return errorResult(ex, new HTTPSampleResult());
             }
         }
+        // see https://issues.apache.org/bugzilla/show_bug.cgi?id=51380
+        if(notifyFirstSampleAfterLoopRestart) {
+            impl.notifyFirstSampleAfterLoopRestart();
+            notifyFirstSampleAfterLoopRestart = false;
+        }
         return impl.sample(u, method, areFollowingRedirect, depth);
     }
 
-    // N.B. It's not possible to forward threadStarted() to the implementation class.
+    // N.B. It's not po ssible to forward threadStarted() to the implementation class.
     // This is because Config items are not processed until later, and HTTPDefaults may define the implementation
 
     @Override
     public void threadFinished(){
         if (impl != null){
             impl.threadFinished(); // Forward to sampler
         }
     }
 
     @Override
     public boolean interrupt() {
         if (impl != null) {
             return impl.interrupt(); // Forward to sampler
         }
         return false;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase#testIterationStart(org.apache.jmeter.engine.event.LoopIterationEvent)
      */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
-        if (impl == null) { // Not called from multiple threads, so this is OK
-            try {
-                impl = HTTPSamplerFactory.getImplementation(getImplementation(), this);
-                initException=null;
-            } catch (Exception ex) {
-                initException = ex;
-            }
-        } 
-        if(impl != null) {
-            // see https://issues.apache.org/bugzilla/show_bug.cgi?id=51380
-            // TODO Would need a rename ?
-            impl.testIterationStart(event);
-        }
+        notifyFirstSampleAfterLoopRestart = true;
     }
 }
