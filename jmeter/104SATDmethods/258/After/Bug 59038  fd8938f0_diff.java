diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
index 3ca8b2b48..2f6b60e8b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
@@ -1,213 +1,215 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 import java.util.Date;
 
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.cookie.CookieSpec;
 import org.apache.commons.httpclient.cookie.MalformedCookieException;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTPClient 3.1 implementation
+ * @deprecated since 3.0, will be removed in next version
  */
+@Deprecated
 public class HC3CookieHandler implements CookieHandler {
    private static final Logger log = LoggingManager.getLoggerForClass();
 
    static final String DEFAULT_POLICY_NAME = "compatibility";
    public static final String[] AVAILABLE_POLICIES = new String[] {
        DEFAULT_POLICY_NAME,
        "default",
        "rfc2109",
        "rfc2965",
        "ignorecookies",
        "netscape"
    };
 
     private final transient CookieSpec cookieSpec;
 
     /**
      * @param policy
      *            cookie policy to which to conform (see
      *            {@link CookiePolicy#getCookieSpec(String)}
      */
     public HC3CookieHandler(String policy) {
         super();
         this.cookieSpec = CookiePolicy.getCookieSpec(policy);
     }
 
     /**
      * Create an HttpClient cookie from a JMeter cookie
      */
     private org.apache.commons.httpclient.Cookie makeCookie(Cookie jmc){
         long exp = jmc.getExpiresMillis();
         org.apache.commons.httpclient.Cookie ret=
             new org.apache.commons.httpclient.Cookie(
                 jmc.getDomain(),
                 jmc.getName(),
                 jmc.getValue(),
                 jmc.getPath(),
                 exp > 0 ? new Date(exp) : null, // use null for no expiry
                 jmc.getSecure()
                );
         ret.setPathAttributeSpecified(jmc.isPathSpecified());
         ret.setDomainAttributeSpecified(jmc.isDomainSpecified());
         ret.setVersion(jmc.getVersion());
         return ret;
     }
     /**
      * Get array of valid HttpClient cookies for the URL
      *
      * @param cookiesCP cookies to consider
      * @param url the target URL
      * @param allowVariableCookie flag whether to allow jmeter variables in cookie values
      * @return array of HttpClient cookies
      *
      */
     org.apache.commons.httpclient.Cookie[] getCookiesForUrl(
             CollectionProperty cookiesCP,
             URL url, 
             boolean allowVariableCookie){
         org.apache.commons.httpclient.Cookie[] cookies =
             new org.apache.commons.httpclient.Cookie[cookiesCP.size()];
         int i = 0;
         for (JMeterProperty jMeterProperty : cookiesCP) {
             Cookie jmcookie = (Cookie) jMeterProperty.getObjectValue();
             // Set to running version, to allow function evaluation for the cookie values (bug 28715)
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(true);
             }
             cookies[i++] = makeCookie(jmcookie);
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(false);
             }
         }
         String host = url.getHost();
         String protocol = url.getProtocol();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean secure = HTTPSamplerBase.isSecure(protocol);
         return cookieSpec.match(host, port, path, secure, cookies);
     }
     
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      *
      * @param url
      *            URL of the request to which the returned header will be added.
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     @Override
     public String getCookieHeaderForURL(
             CollectionProperty cookiesCP,
             URL url,
             boolean allowVariableCookie) {
         org.apache.commons.httpclient.Cookie[] c = 
                 getCookiesForUrl(cookiesCP, url, allowVariableCookie);
         int count = c.length;
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Found "+count+" cookies for "+url.toExternalForm());
         }
         if (count <=0){
             return null;
         }
         String hdr=cookieSpec.formatCookieHeader(c).getValue();
         if (debugEnabled){
             log.debug("Cookie: "+hdr);
         }
         return hdr;
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void addCookieFromHeader(CookieManager cookieManager,
             boolean checkCookies,String cookieHeader, URL url){
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled) {
             log.debug("Received Cookie: " + cookieHeader + " From: " + url.toExternalForm());
         }
         String protocol = url.getProtocol();
         String host = url.getHost();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean isSecure=HTTPSamplerBase.isSecure(protocol);
         org.apache.commons.httpclient.Cookie[] cookies= null;
         try {
             cookies = cookieSpec.parse(host, port, path, isSecure, cookieHeader);
         } catch (MalformedCookieException | IllegalArgumentException e) {
             log.warn(cookieHeader+e.getLocalizedMessage());
         }
         if (cookies == null) {
             return;
         }
         for(org.apache.commons.httpclient.Cookie cookie : cookies){
             try {
                 if (checkCookies) {
                     cookieSpec.validate(host, port, path, isSecure, cookie);
                 }
                 Date expiryDate = cookie.getExpiryDate();
                 long exp = 0;
                 if (expiryDate!= null) {
                     exp=expiryDate.getTime();
                 }
                 Cookie newCookie = new Cookie(
                         cookie.getName(),
                         cookie.getValue(),
                         cookie.getDomain(),
                         cookie.getPath(),
                         cookie.getSecure(),
                         exp / 1000,
                         cookie.isPathAttributeSpecified(),
                         cookie.isDomainAttributeSpecified()
                         );
 
                 // Store session cookies as well as unexpired ones
                 if (exp == 0 || exp >= System.currentTimeMillis()) {
                     newCookie.setVersion(cookie.getVersion());
                     cookieManager.add(newCookie); // Has its own debug log; removes matching cookies
                 } else {
                     cookieManager.removeMatchingCookies(newCookie);
                     if (debugEnabled){
                         log.debug("Dropping expired Cookie: "+newCookie.toString());
                     }
                 }
             } catch (MalformedCookieException e) { // This means the cookie was wrong for the URL
                 log.warn("Not storing invalid cookie: <"+cookieHeader+"> for URL "+url+" ("+e.getLocalizedMessage()+")");
             } catch (IllegalArgumentException e) {
                 log.warn(cookieHeader+e.getLocalizedMessage());
             }
         }
 
     }
 
     @Override
     public String getDefaultPolicy() {
         return DEFAULT_POLICY_NAME; 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index c736762d0..6a909d6a3 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -1,1091 +1,1093 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
+ * @deprecated since 3.0, will be removed in next version
  */
+@Deprecated
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 1); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient3.retrycount", 0);
 
     private static final String HTTP_AUTHENTICATION_PREEMPTIVE = "http.authentication.preemptive"; // $NON-NLS-1$
 
     private static final boolean canSetPreEmptive; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients = 
         new ThreadLocal<Map<HostConfiguration, HttpClient>>(){
         @Override
         protected Map<HostConfiguration, HttpClient> initialValue() {
             return new HashMap<>();
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
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + urlStr);
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
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
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation); // in case sanitising fails
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
             // httpMethod cannot be null here, otherwise 
             // it would have been caught in the previous catch block
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
index 6031312ba..5526a2e8b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
@@ -1,87 +1,88 @@
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
- *
+ * @deprecated since 3.0, will be removed in next version
  */
+@Deprecated
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
         hc.notifyFirstSampleAfterLoopRestart();
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
index 4e5c77363..bead9c0b9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
@@ -1,373 +1,375 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Commons HTTPClient based soap sampler
+ * @deprecated since 3.0, will be removed in next version
  */
+@Deprecated
 public class SoapSampler extends HTTPSampler2 implements Interruptible { // Implemented by parent class
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
                     InputStream in = null;
                     try{
                         in = new BufferedInputStream(new FileInputStream(xmlFile));
                         IOUtils.copy(in, out);
                         out.flush();
                     } finally {
                         IOUtils.closeQuietly(in);
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
         res.setMonitor(false);
 
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/LoopbackHttpClientSocketFactory.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/LoopbackHttpClientSocketFactory.java
index c3adc5cd9..f80bdb8ee 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/LoopbackHttpClientSocketFactory.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/LoopbackHttpClientSocketFactory.java
@@ -1,99 +1,100 @@
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
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.net.Socket;
 import java.net.URL;
 import java.net.URLConnection;
 import java.net.URLStreamHandler;
 import java.net.URLStreamHandlerFactory;
 import java.net.UnknownHostException;
 
 import org.apache.commons.httpclient.ConnectTimeoutException;
 import org.apache.commons.httpclient.params.HttpConnectionParams;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
 
 /**
  * Commons HttpClient protocol factory to generate Loopback HTTP sockets
+  * @deprecated since 3.0, will be removed in next version
  */
-
+@Deprecated
 public class LoopbackHttpClientSocketFactory implements ProtocolSocketFactory {
 
     public LoopbackHttpClientSocketFactory() {
         super();
     }
 
     @Override
     public Socket createSocket(String host, int port, InetAddress clientHost,
             int clientPort) throws IOException, UnknownHostException {
         return new LoopbackHTTPSocket(host,port,clientHost,clientPort);
     }
 
     @Override
     public Socket createSocket(String host, int port) throws IOException,
             UnknownHostException {
         return new LoopbackHTTPSocket(host,port);
     }
 
     @Override
     public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
             HttpConnectionParams params)
     throws IOException, UnknownHostException, ConnectTimeoutException {
         int timeout = params.getConnectionTimeout();
         if (timeout == 0) {
             return new LoopbackHTTPSocket(host,port,localAddress,localPort);
         } else {
             return new LoopbackHTTPSocket(host,port,localAddress,localPort, timeout);
         }
     }
 
     /**
      * Convenience method to set up the necessary HttpClient protocol and URL handler.
      *
      * Only works for HttpClient, because it's not possible (or at least very difficult)
      * to provide a different socket factory for the HttpURLConnection class.
      */
     public static void setup(){
         final String LOOPBACK = "loopback"; // $NON-NLS-1$
 
         // This ensures tha HttpClient knows about the protocol
         Protocol.registerProtocol(LOOPBACK, new Protocol(LOOPBACK,new LoopbackHttpClientSocketFactory(),1));
 
         // Now allow the URL handling to work.
         URLStreamHandlerFactory ushf = new URLStreamHandlerFactory(){
             @Override
             public URLStreamHandler createURLStreamHandler(String protocol) {
                 if (protocol.equalsIgnoreCase(LOOPBACK)){
                     return new URLStreamHandler(){
                         @Override
                         protected URLConnection openConnection(URL u) throws IOException {
                             return null;// not needed for HttpClient
                         }
                     };
                 }
                 return null;
             }
         };
 
         java.net.URL.setURLStreamHandlerFactory(ushf);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/SlowHttpClientSocketFactory.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/SlowHttpClientSocketFactory.java
index 8581687e4..648886de1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/SlowHttpClientSocketFactory.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/SlowHttpClientSocketFactory.java
@@ -1,71 +1,72 @@
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
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.net.Socket;
 import java.net.UnknownHostException;
 
 import org.apache.commons.httpclient.ConnectTimeoutException;
 import org.apache.commons.httpclient.params.HttpConnectionParams;
 import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
 import org.apache.jmeter.util.SlowSocket;
 
 /**
  * Commons HttpClient protocol factory to generate "slow" sockets for emulating dial-up modems
+ * @deprecated since 3.0, will be removed in next version
  */
-
+@Deprecated
 public class SlowHttpClientSocketFactory implements ProtocolSocketFactory {
 
     private final int CPS; // Characters per second to emulate
 
     /**
      *
      * @param cps - characters per second
      */
     public SlowHttpClientSocketFactory(final int cps) {
         super();
         CPS = cps;
     }
 
     @Override
     public Socket createSocket(String host, int port, InetAddress clientHost,
             int clientPort) throws IOException, UnknownHostException {
         return new SlowSocket(CPS,host,port,clientHost,clientPort);
     }
 
     @Override
     public Socket createSocket(String host, int port) throws IOException,
             UnknownHostException {
         return new SlowSocket(CPS,host,port);
     }
 
     @Override
     public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
             HttpConnectionParams params)
     throws IOException, UnknownHostException, ConnectTimeoutException {
         int timeout = params.getConnectionTimeout();
         if (timeout == 0) {
             return new SlowSocket(CPS,host,port,localAddress,localPort);
         } else {
             return new SlowSocket(CPS,host,port,localAddress,localPort, timeout);
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index fd513db20..e56be635a 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,461 +1,462 @@
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
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 ]>
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
 
 
 <!--  =================== 3.0 =================== -->
 
 <h1>Version 3.0</h1>
 
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
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.idle_time</code> property value is true, meaning CSV/XML result files will contain an additional column containing idle time between samplers, see <bugzilla>57182</bugzilla></li>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 3.0, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 3.0, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. See <bugzilla>58687</bugzilla></li>
     <li>Property <code>jmeterthread.startearlier</code> has been removed. See <bugzilla>58726</bugzilla></li>   
     <li>Property <code>jmeterengine.startlistenerslater</code> has been removed. See <bugzilla>58728</bugzilla></li>   
     <li>Property <code>jmeterthread.reversePostProcessors</code> has been removed. See <bugzilla>58728</bugzilla></li>  
     <li>MongoDB elements (MongoDB Source Config, MongoDB Script) have been deprecated and will be removed in next version of jmeter. They do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. JMeter team advises not to use them anymore. See <bugzilla>58772</bugzilla></li>
     <li>Summariser listener now outputs a formated duration in HH:mm:ss (Hour:Minute:Second), it previously outputed seconds. See <bugzilla>58776</bugzilla></li>
     <li>WebService(SOAP) Request and HTML Parameter Mask which were deprecated in 2.13 version, have now been removed following our <a href="./usermanual/best-practices.html#deprecation">deprecation strategy</a>.
     Classes and properties which were only used by those elements have been dropped:
     <ul>
         <li><code>org.apache.jmeter.protocol.http.util.DOMPool</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLException</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLHelper</code></li>
         <li>Property <code>soap.document_cache</code></li>
     </ul>
     </li>
     <li>org.apache.jmeter.protocol.http.visualizers.RequestViewHTTP.getQueryMap signature has changed, if you use it ensure you update your code. See <bugzilla>58845</bugzilla></li>
     <li><code>__jexl</code> function has been deprecated and will be removed in next version. See <bugzilla>58903</bugzilla></li>
     <li>JMS Subscriber will consider sample in error if number of received messages is not equals to expected number of messages. It previously considerer sample OK if only 1 message was received. See <bugzilla>58980</bugzilla></li>
     <li>Since version 3.0, HTTP(S) Test Script recorder uses default port 8888 as configured when using Recording Template. See <bugzilla>59006</bugzilla></li>
     <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>
     <li>Since version 3.0, the support for reading old Avalon format JTL (result) files has been removed, see <bugzilla>59064</bugzilla></li>     
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
     <li><bug>58811</bug>When pasting arguments between http samplers the column "Encode" and "Include Equals" are lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58843</bug>Improve the usable space in the HTTP sampler GUI. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58852</bug>Use less memory for <code>PUT</code> requests. The uploaded data will no longer be stored in the Sampler.
         This is the same behaviour as with <code>POST</code> requests.</li>
     <li><bug>58860</bug>HTTP Request : Add automatic variable generation in HTTP parameters table by right click. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58923</bug>normalize URIs when downloading embedded resources.</li>
     <li><bug>59005</bug>HTTP Sampler : Added WebDAV verb (SEARCH).</li>
     <li><bug>59006</bug>Change Default proxy recording port to 8888 to align it with Recording Template. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>58099</bug>Performance : Lazily initialize HttpClient SSL Context to avoid its initialization even for HTTP only scenarios</li>
     <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59023</bug>HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59028</bug>Use SystemDefaultDnsResolver singleton. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59036</bug>FormCharSetFinder : Use JSoup instead of deprecated HTMLParser</li>
     <li><bug>59034</bug>Parallel downloads connection management is not realistic. Contributed by Benoit Wiart (benoit dot wiart at gmail.com) and Philippe Mouawad</li>
     <li><bug>59060</bug>HTTP Request GUI : Move File Upload to a new Tab to have more space for parameters and prevent incoherent configuration. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>58786</bug>JDBC Sampler : Replace Excalibur DataSource by more up to date library commons-dbcp2</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58989</bug>Record controller gui : add a button to clear all the recorded samples. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 <li><bug>58776</bug>Summariser should display a more readable duration</li>
 <li><bug>58791</bug>Deprecate listeners:Distribution Graph (alpha) and Spline Visualizer</li>
 <li><bug>58849</bug>View Results Tree : Add a search panel to the request http view to be able to search in the parameters table. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58857</bug>View Results Tree : the request view http does not allow to resize the parameters table first column. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58955</bug>Request view http does not correctly display http parameters in multipart/form-data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
   <li><bug>58698</bug>Correct parsing of auth-files in HTTP Authorization Manager.</li>
   <li><bug>58756</bug>CookieManager : Cookie Policy select box content must depend on Cookie implementation.</li>
   <li><bug>56358</bug>Cookie manager supports cross port cookies and RFC6265. Thanks to Oleg Kalnichevski (olegk at apache.org)</li>
   <li><bug>58773</bug>TestCacheManager : Add tests for CacheManager that use HttpClient 4</li>
   <li><bug>58742</bug>CompareAssertion : Reset data in TableEditor when switching between different CompareAssertions in gui.
       Based on a patch by Vincent Herilier (vherilier at gmail.com)</li>
   <li><bug>58848</bug>Argument Panel : when adding an argument (add button or from clipboard) scroll the table to the new line. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>58865</bug>Allow empty default value in the Regular Expression Extractor. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
     <li><bug>58903</bug>Provide __jexl3 function that uses commons-jexl3 and deprecated __jexl (1.1) function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>58736</bug>Add Sample Timeout support</li>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>SVN Revision ID</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58726</bug>Remove the <code>jmeterthread.startearlier</code> parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58728</bug>Drop old behavioural properties</li>
 <li><bug>57319</bug>Upgrade to HttpClient 4.5.2. With the big help from Oleg Kalnichevski (olegk at apache.org) and Gary Gregory (ggregory at apache.org).</li>
 <li><bug>58772</bug>Deprecate MongoDB related elements</li>
 <li><bug>58782</bug>ThreadGroup : Improve ergonomy</li>
 <li><bug>58165</bug>Show the time elapsed since the start of the load test in GUI mode. Partly based on a contribution from Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><bug>58784</bug>Make JMeterUtils#runSafe sync/async awt invocation configurable and change the visualizers to use the async version.</li>
 <li><bug>58790</bug>Issue in CheckDirty and its relation to ActionRouter</li>
 <li><bug>58814</bug>JVM don't recognize option MaxLiveObjectEvacuationRatio; remove from comments</li>
 <li><bug>58810</bug>Config Element Counter (and others): Check Boxes Toggle Area Too Big</li>
 <li><bug>56554</bug>JSR223 Test Element : Generate compilation cache key automatically. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58911</bug>Header Manager : it should be possible to copy/paste between Header Managers. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58864</bug>Arguments Panel : when moving parameter with up / down, ensure that the selection remains visible. Based on a contribution by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58924</bug>Dashboard / report : It should be possible to export the generated graph as image (PNG). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58884</bug>JMeter report generator : need better error message. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58957</bug>Report/Dashboard: HTML Exporter does not create parent directories for output directory. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
 <li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 <li><bug>57182</bug>Settings defaults : Switch "jmeter.save.saveservice.idle_time" to true (after 2.13)</li>
 <li><bug>58987</bug>Report/Dashboard: Improve error reporting.</li>
 <li><bug>58870</bug>TableEditor: minimum size is too small. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59037</bug>Drop HtmlParserHTMLParser and dependencies on htmlparser and htmllexer</li>
 <li><bug>58933</bug>JSyntaxTextArea : Ability to set font.  Contributed by Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li><bug>58793</bug>Create developers page explaining how to build and contribute</li>
 <li><bug>59046</bug>JMeter Gui Replace controller should keep the name and the selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
+<li><bug>59038</bug>Deprecate HTTPClient 3.1 related elements</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to httpclient, httpmime 4.5.2 (from 4.2.6)</li>
 <li>Updated to tika-core and tika-parsers 1.12 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7.1 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.7.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.3 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.8 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li>Updated to commons-collections-3.2.2 (from 3.2.1)</li>
 <li>Updated to commons-net 3.4 (from 3.3)</li>
 <li>Updated to slf4j 1.7.13 (from 1.7.12)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58729</bug>Cleanup extras folder for maintainability</li>
 <li><bug>57110</bug>Fixed spelling+grammar, formatting, removed commented out code etc. Contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Correct instructions on running jmeter in help.txt. Contributed by Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li><bug>58704</bug>Non regression testing : Ant task batchtest fails if tests and run in a non en_EN locale and use a JMX file that uses a Csv DataSet</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58949</bug>Cleanup of ldap code. Based on a patch by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58967</bug>Use junit categories to exclude tests that need a gui. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59003</bug>ClutilTestCase testSingleArg8 and testSingleArg9 are identical</li>
 <li><bug>59064</bug>Remove OldSaveService which supported very old Avalon format JTL (result) files</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
     <li><bug>57804</bug>HTTP Request doesn't reuse cached SSL context when using Client Certificates in HTTPS (only fixed for HttpClient4 implementation)</li>
     <li><bug>58800</bug>proxy.pause default value , fix documentation</li>
     <li><bug>58844</bug>Buttons enable / disable is broken in the arguments panel. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58861</bug>When clicking on up, down or detail while in a cell of the argument panel, newly added content is lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>57935</bug>SSL SNI extension not supported by HttpClient 4.2.6</li>
     <li><bug>59044</bug>Http Sampler : It should not be possible to select the multipart encoding if the method is not POST. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59008</bug>Http Sampler: Infinite recursion SampleResult on frame depth limit reached</li>
     <li><bug>59069</bug>CookieManager : Selected Cookie Policy is always reset to default when saving or switching to another TestElement (nightly build 25th feb 2016)</li>
     <li><bug>58881</bug>HTTP Request : HTTPHC4Impl shows exception when server uses "deflate" compression</li>
     <li><bug>58583</bug>HTTP client fails to close connection if server misbehaves by not sending "connection: close", violating HTTP RFC 2616 / RFC 7230</li>
     <li><bug>58950</bug>NoHttpResponseException when Pause between samplers exceeds keepalive sent by server</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
     <li><bug>59051</bug>JDBC Request : Connection is closed by pool if it exceeds the configured lifetime (affects nightly build as of 23 fev 2016).</li>
     <li><bug>59075</bug>JMS Publisher: NumberFormatException is thrown is priority or expiration fields are empty</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59067</bug>JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop". Contributed by Benoit Wiart(benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug>SampleResultConverter should note that it cannot record non-TEXT data</li>
 <li><bug>58845</bug>Request http view doesn't display all the parameters. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58413</bug>ViewResultsTree : Request HTTP Renderer does not show correctly parameters that contain ampersand (&amp;). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 <li><bug>58912</bug>Response assertion gui : Deleting more than 1 selected row deletes only one row. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
     <li><bug>58781</bug>Command line option "-?" shows Unknown option</li>
     <li><bug>58795</bug>NPE may occur in GuiPackage#getTestElementCheckSum with some 3rd party plugins</li>
     <li><bug>58913</bug>When closing jmeter should not interpret cancel as "destroy my test plan". Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58952</bug>Report/Dashboard: Generation of aggregated series in graphs does not work. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>58931</bug>New Report/Dashboard : Getting font errors under Firefox and Chrome (not Safari)</li>
     <li><bug>58932</bug>Report / Dashboard: Document clearly and log what report are not generated when saveservice options are not correct. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>59055</bug>JMeter report generator : When generation is not launched from jmeter/bin folder report-template is not found</li>
     <li><bug>58986</bug>Report/Dashboard reuses the same output directory</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 <li>Oleg Kalnichevski (olegk at apache.org)</li>
 <li>Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Florent Sabbe (f dot sabbe at ubik-ingenierie.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Harrison Termotto (harrison dot termotto at stonybrook.edu</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li>Gary Gregory (ggregory at apache.org)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
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
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
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
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (SHIFT + up/down) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index d59f6437e..01204e650 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1,1226 +1,1226 @@
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
 <!ENTITY sect-num '19'>
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY le       "&#x02264;" >
 <!ENTITY ge       "&#x02265;" >
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
 <section name="&sect-num; Introduction" anchor="introduction">
 <description>
 <p>
 
 </p>
  <note>
  Several test elements use JMeter properties to control their behaviour.
  These properties are normally resolved when the class is loaded.
  This generally occurs before the test plan starts, so it's not possible to change the settings by using the <code>__setProperty()</code> function.
 </note>
 <p>
 </p>
 </description>
 </section>
  
 <section name="&sect-num;.1 Samplers" anchor="samplers">
 <description>
     <p>
     Samplers perform the actual work of JMeter.
     Each sampler (except <complink name="Test Action" />) generates one or more sample results.
     The sample results have various attributes (success/fail, elapsed time, data size etc.) and can be viewed in the various listeners.
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
 Latency is set to the time it takes to login.
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server Name or IP" required="Yes">Domain name or IP address of the FTP server.</property>
         <property name="Port" required="No">Port to use. If this is <code>&gt;0</code>, then this specific port is used,
            otherwise JMeter uses the default FTP port.</property>
         <property name="Remote File:" required="Yes">File to retrieve or name of destination file to upload.</property>
         <property name="Local File:" required="Yes, if uploading (*)">File to upload, or destination for downloads (defaults to remote file name).</property>
         <property name="Local File Contents:" required="Yes, if uploading (*)">Provides the contents for the upload, overrides the Local File property.</property>
         <property name="get(RETR) / put(STOR)" required="Yes">Whether to retrieve or upload a file.</property>
         <property name="Use Binary mode?" required="Yes">Check this to use Binary mode (default ASCII)</property>
         <property name="Save File in Response?" required="Yes, if downloading">
         Whether to store contents of retrieved file in response data.
         If the mode is ASCII, then the contents will be visible in the <complink name="View Results Tree"/>.
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
         The default parser is <code>org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser</code>.
         This can be changed by using the property "<code>htmlparser.className</code>" - see <code>jmeter.properties</code> for details.
         </p>
         <p>If you are going to send multiple requests to the same web server, consider
         using an <complink name="HTTP Request Defaults"/>
         Configuration Element so you do not have to enter the same information for each
         HTTP Request.</p>
 
         <p>Or, instead of manually adding HTTP Requests, you may want to use
         JMeter's <complink name="HTTP(S) Test Script Recorder"/> to create
         them.  This can save you time if you have a lot of HTTP requests or requests with many
         parameters.</p>
 
         <p><b>There are two different test elements used to define the samplers:</b></p>
         <dl>
         <dt>AJP/1.3 Sampler</dt><dd>uses the Tomcat mod_jk protocol (allows testing of Tomcat in AJP mode without needing Apache httpd)
         The AJP Sampler does not support multiple file upload; only the first file will be used.
         </dd>
         <dt>HTTP Request</dt><dd>this has an implementation drop-down box, which selects the HTTP protocol implementation to be used:
           <dl>
             <dt><code>Java</code></dt><dd>uses the HTTP implementation provided by the JVM.
             This has some limitations in comparison with the HttpClient implementations - see below.</dd>
-            <dt><code>HTTPClient3.1</code></dt><dd>uses Apache Commons HttpClient 3.1.
-            This is no longer being developed, and support for this may be dropped in a future JMeter release.</dd>
+            <dt><code>HTTPClient3.1</code></dt><dd>(DEPRECATED SINCE 3.0) uses Apache Commons HttpClient 3.1.
+            This is no longer being developed, and support for this will be dropped in a future JMeter release.</dd>
             <dt><code>HTTPClient4</code></dt><dd>uses Apache HttpComponents HttpClient 4.x.</dd>
             <dt>Blank Value</dt><dd>does not set implementation on HTTP Samplers, so relies on HTTP Request Defaults if present or on <code>jmeter.httpsampler</code> property defined in <code>jmeter.properties</code></dd>
           </dl>
         </dd>
         </dl>
          <p>The Java HTTP implementation has some limitations:</p>
          <ul>
          <li>There is no control over how connections are re-used.
          When a connection is released by JMeter, it may or may not be re-used by the same thread.</li>
          <li>The API is best suited to single-threaded usage - various settings
          are defined via system properties, and therefore apply to all connections.</li>
          <li>There is a bug in the handling of HTTPS via a Proxy (the <code>CONNECT</code> is not handled correctly).
          See Java bugs 6226610 and 6208335.
          </li>
          <li>It does not support virtual hosts.</li>
          <li>It does not support the following methods: <code>COPY</code>, <code>LOCK</code>, <code>MKCOL</code>, <code>MOVE</code>,
              <code>PATCH</code>, <code>PROPFIND</code>, <code>PROPPATCH</code>, <code>UNLOCK</code>, <code>REPORT</code>,
              <code>MKCALENDAR</code>, <code>SEARCH</code>.</li>
          <li>It does not support client based certificate testing with Keystore Config.</li>
          </ul>
          <note>Note: the <code>FILE</code> protocol is intended for testing purposes only.
          It is handled by the same code regardless of which HTTP Sampler is used.</note>
         <p>If the request requires server or proxy login authorization (i.e. where a browser would create a pop-up dialog box),
          you will also have to add an <complink name="HTTP Authorization Manager"/> Configuration Element.
          For normal logins (i.e. where the user enters login information in a form), you will need to work out what the form submit button does,
          and create an HTTP request with the appropriate method (usually <code>POST</code>) 
          and the appropriate parameters from the form definition.
          If the page uses HTTP, you can use the JMeter Proxy to capture the login sequence.
         </p>
         <p>
         A separate SSL context is used for each thread.
         If you want to use a single SSL context (not the standard behaviour of browsers), set the JMeter property:</p>
 <source>
 https.sessioncontext.shared=true
 </source>
         By default, the SSL context is retained for the duration of the test.
         The SSL session can be optionally reset for each test iteration.
         To enable this, set the JMeter property:
 <source>
 https.use.cached.ssl.context=false
 </source>
         <note>
          Note: this does not apply to the Java HTTP implementation.
         </note>
         JMeter defaults to the SSL protocol level TLS.
         If the server needs a different level, e.g. <code>SSLv3</code>, change the JMeter property, for example:
 <source>
 https.default.protocol=SSLv3
 </source>
         <p>
         JMeter also allows one to enable additional protocols, by changing the property <code>https.socket.protocols</code>.
         </p>
         <p>If the request uses cookies, then you will also need an
         <complink name="HTTP Cookie Manager"/>.  You can
         add either of these elements to the Thread Group or the HTTP Request. If you have
         more than one HTTP Request that needs authorizations or cookies, then add the
         elements to the Thread Group. That way, all HTTP Request controllers will share the
         same Authorization Manager and Cookie Manager elements.</p>
 
         <p>If the request uses a technique called "URL Rewriting" to maintain sessions,
         then see section
         <a href="build-adv-web-test-plan.html#session_url_rewriting">6.1 Handling User Sessions With URL Rewriting</a>
         for additional configuration steps.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server" required="Yes, unless provided by HTTP Request Defaults">
             Domain name or IP address of the web server, e.g. <code>www.example.com</code>. [Do not include the <code>http://</code> prefix.]
             Note: If the "<code>Host</code>" header is defined in a Header Manager, then this will be used
             as the virtual host name.
         </property>
         <property name="Port" required="No">Port the web server is listening to. Default: <code>80</code></property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.
         Note that this applies to each wait for a response. If the server response is sent in several chunks, the overall
         elapsed time may be longer than the timeout.
         <p>A <complink name="Duration Assertion"/> can be used to detect responses that take too long to complete.</p>
         </property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the <code>http://</code> prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server. (N.B. this is stored unencrypted in the test plan)</property>
-        <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient3.1</code>, <code>HttpClient4</code>.
+        <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient3.1 (DEPRECATED SINCE 3.0)</code>, <code>HttpClient4</code>.
         If not specified (and not defined by HTTP Request Defaults), the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the HttpClient4 implementation is used.</property>
         <property name="Protocol" required="No"><code>HTTP</code>, <code>HTTPS</code> or <code>FILE</code>. Default: <code>HTTP</code></property>
         <property name="Method" required="Yes"><code>GET</code>, <code>POST</code>, <code>HEAD</code>, <code>TRACE</code>,
           <code>OPTIONS</code>, <code>PUT</code>, <code>DELETE</code>, <code>PATCH</code> (not supported for
           <code>JAVA</code> implementation). With <code>HttpClient4</code>, the following methods related to WebDav are
           also allowed: <code>COPY</code>, <code>LOCK</code>, <code>MKCOL</code>, <code>MOVE</code>,
           <code>PROPFIND</code>, <code>PROPPATCH</code>, <code>UNLOCK</code>, <code>REPORT</code>, <code>MKCALENDAR</code>,
           <code>SEARCH</code>.</property>
         <property name="Content Encoding" required="No">
         Content encoding to be used (for <code>POST</code>, <code>PUT</code>, <code>PATCH</code> and <code>FILE</code>).
         This is the character encoding to be used, and is not related to the Content-Encoding HTTP header.
         </property>
         <property name="Redirect Automatically" required="No">
         Sets the underlying http protocol handler to automatically follow redirects,
         so they are not seen by JMeter, and thus will not appear as samples.
         Should only be used for <code>GET</code> and <code>HEAD</code> requests.
         The HttpClient sampler will reject attempts to use it for <code>POST</code> or <code>PUT</code>.
         <note>Warning: see below for information on cookie and header handling.</note>
         </property>
         <property name="Follow Redirects" required="No">
         This only has any effect if "<code>Redirect Automatically</code>" is not enabled.
         If set, the JMeter sampler will check if the response is a redirect and follow it if so.
         The initial redirect and further responses will appear as additional samples.
         The URL and data fields of the parent sample will be taken from the final (non-redirected)
         sample, but the parent byte count and elapsed time include all samples.
         The latency is taken from the initial response.
         Note that the HttpClient sampler may log the following message:
         <source>"Redirect requested but followRedirects is disabled"</source>
         This can be ignored.
         <br/>
         JMeter will collapse paths of the form '<code>/../segment</code>' in
         both absolute and relative redirect URLs. For example <code>http://host/one/../two</code> will be collapsed into <code>http://host/two</code>.
         If necessary, this behaviour can be suppressed by setting the JMeter property
         <code>httpsampler.redirect.removeslashdotdot=false</code>
         </property>
         <property name="Use KeepAlive" required="No">JMeter sets the Connection: <code>keep-alive</code> header. This does not work properly with the default HTTP implementation, as connection re-use is not under user-control.
                   It does work with the Apache HttpComponents HttpClient implementations.</property>
         <property name="Use multipart/form-data for HTTP POST" required="No">
         Use a <code>multipart/form-data</code> or <code>application/x-www-form-urlencoded</code> post request
         </property>
         <property name="Browser-compatible headers" required="No">
         When using <code>multipart/form-data</code>, this suppresses the <code>Content-Type</code> and
         <code>Content-Transfer-Encoding</code> headers; only the <code>Content-Disposition</code> header is sent.
         </property>
         <property name="Path" required="Yes">The path to resource (for example, <code>/servlets/myServlet</code>). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.
 <note>
 As a special case, if the path starts with "<code>http://</code>" or "<code>https://</code>" then this is used as the full URL.
 </note>
 In this case, the server, port and protocol fields are ignored; parameters are also ignored for <code>GET</code> and <code>DELETE</code> methods.
 Also please note that the path is not encoded - apart from replacing spaces with <code>%20</code> -
 so unsafe characters may need to be encoded to avoid errors such as <code>URISyntaxException</code>.
 </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <code>name</code> and
         <code>value</code>, the options to encode the parameter, and an option to include or exclude an equals sign (some applications
         don't expect an equals when the value is the empty string).  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (i.e. if you chose <code>GET</code> or <code>DELETE</code>, the query string will be
         appended to the URL, if <code>POST</code> or <code>PUT</code>, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.
         <b>See below for some further information on parameter handling.</b>
         <p>
         Additionally, you can specify whether each parameter should be URL encoded.  If you are not sure what this
         means, it is probably best to select it.  If your values contain characters such as the following then encoding is usually required.:
         </p>
         <ul>
             <li>ASCII Control Chars</li>
             <li>Non-ASCII characters</li>
             <li>Reserved characters:URLs use some characters for special use in defining their syntax. When these characters are not used in their special role inside a URL, they need to be encoded, example : '<code>$</code>', '<code>&amp;</code>', '<code>+</code>', '<code>,</code>' , '<code>/</code>', '<code>:</code>', '<code>;</code>', '<code>=</code>', '<code>?</code>', '<code>@</code>'</li>
             <li>Unsafe characters: Some characters present the possibility of being misunderstood within URLs for various reasons. These characters should also always be encoded, example : '<code> </code>', '<code>&lt;</code>', '<code>&gt;</code>', '<code>#</code>', '<code>%</code>', &hellip;</li>
         </ul>
         </property>
         <property name="File Path:" required="No">Name of the file to send.  If left blank, JMeter
         does not send a file, if filled in, JMeter automatically sends the request as
         a multipart form request.
         <p>
         If it is a <code>POST</code> or <code>PUT</code> or <code>PATCH</code> request and there is a single file whose 'Parameter name' attribute (below) is omitted, 
         then the file is sent as the entire body
         of the request, i.e. no wrappers are added. This allows arbitrary bodies to be sent. This functionality is present for <code>POST</code> requests, 
         and also for <code>PUT</code> requests.
         <b>See below for some further information on parameter handling.</b>
         </p>
         </property>
         <property name="Parameter name:" required="No">Value of the "<code>name</code>" web request parameter.</property>
         <property name="MIME Type" required="No">MIME type (for example, <code>text/plain</code>).
         If it is a <code>POST</code> or <code>PUT</code> or <code>PATCH</code> request and either the '<code>name</code>' attribute (below) are omitted or the request body is
         constructed from parameter values only, then the value of this field is used as the value of the
         <code>content-type</code> request header.
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
         So if you only want to download embedded resources from <code>http://example.com/</code>, use the expression:
         <code>http://example\.com/.*</code>
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
         <li>Select <i>Device IPv4</i> to select the IPv4 address of the device name (like <code>eth0</code>, <code>lo</code>, <code>em0</code>, etc.)</li>
         <li>Select <i>Device IPv6</i> to select the IPv6 address of the device name (like <code>eth0</code>, <code>lo</code>, <code>em0</code>, etc.)</li>
         </ul>
         </property>
         <property name="Source address field" required="No">
         <i>[Only for HTTP Request with HTTPClient implementation]</i> <br></br>
         This property is used to enable IP Spoofing.
         It override the default local IP address for this sample.
         The JMeter host must have multiple IP addresses (i.e. IP aliases, network interfaces, devices).
         The value can be a host name, IP address, or a network interface device such as "<code>eth0</code>" or "<code>lo</code>" or "<code>wlan0</code>".<br></br>
         If the property <code>httpclient.localaddress</code> is defined, that is used for all HttpClient requests.
         </property>
 </properties>
 <note>
 When using Automatic Redirection, cookies are only sent for the initial URL.
 This can cause unexpected behaviour for web-sites that redirect to a local server.
 E.g. if <code>www.example.com</code> redirects to <code>www.example.co.uk</code>.
 In this case the server will probably return cookies for both URLs, but JMeter will only see the cookies for the last
 host, i.e. <code>www.example.co.uk</code>. If the next request in the test plan uses <code>www.example.com</code>,
 rather than <code>www.example.co.uk</code>, it will not get the correct cookies.
 Likewise, Headers are sent for the initial request, and won't be sent for the redirect.
 This is generally only a problem for manually created test plans,
 as a test plan created using a recorder would continue from the redirected URL.
 </note>
 <p>
 <b>Parameter Handling:</b><br></br>
 For the <code>POST</code> and <code>PUT</code> method, if there is no file to send, and the name(s) of the parameter(s) are omitted,
 then the body is created by concatenating all the value(s) of the parameters.
 Note that the values are concatenated without adding any end-of-line characters.
 These can be added by using the <code>__char()</code> function in the value fields.
 This allows arbitrary bodies to be sent.
 The values are encoded if the encoding flag is set.
 See also the MIME Type above how you can control the <code>content-type</code> request header that is sent.
 <br></br>
 For other methods, if the name of the parameter is missing,
 then the parameter is ignored. This allows the use of optional parameters defined by variables.
 </p>
 <br/>
 <p>You have the option to switch to Post Body when a request has only unnamed parameters
 (or no parameters at all).
 This option is useful in the following cases (amongst others):</p>
 <ul>
 <li>GWT RPC HTTP Request</li>
 <li>JSON REST HTTP Request</li>
 <li>XML REST HTTP Request</li>
 <li>SOAP HTTP Request</li>
 </ul>
 <note>
 Note that once you leave the Tree node, you cannot switch back to the parameter tab unless you clear the Post Body tab of data.
 </note>
 <p>
 In Post Body mode, each line will be sent with <code>CRLF</code> appended, apart from the last line.
 To send a <code>CRLF</code> after the last line of data, just ensure that there is an empty line following it.
 (This cannot be seen, except by noting whether the cursor can be placed on the subsequent line.)
 </p>
 <figure width="902" height="421" image="http-request-raw-single-parameter.png">Figure 1 - HTTP Request with one unnamed parameter</figure>
 <figure width="908" height="212" image="http-request-confirm-raw-body.png">Figure 2 - Confirm dialog to switch</figure>
 <figure width="905" height="423" image="http-request-raw-body.png">Figure 3 - HTTP Request using Body Data</figure>
 
 <p>
 <b>Method Handling:</b><br></br>
 The <code>POST</code>, <code>PUT</code> and <code>PATCH</code> request methods work similarly, except that the <code>PUT</code> and <code>PATCH</code> methods do not support multipart requests
 or file upload.
 The <code>PUT</code> and <code>PATCH</code> method body must be provided as one of the following:</p>
 <ul>
 <li>define the body as a file with empty Parameter name field; in which case the MIME Type is used as the Content-Type</li>
 <li>define the body as parameter value(s) with no name</li>
 <li>use the Post Body tab</li>
 </ul>
 <p>
 If you define any parameters with a name in either the sampler or HTTP
 defaults then nothing is sent.
 <code>PUT</code> and <code>PATCH</code> require a Content-Type.
 If not using a file, attach a Header Manager to the sampler and define the Content-Type there.
 The <code>GET</code> and <code>DELETE</code> request methods work similarly to each other.
 </p>
 <p>JMeter scan responses from embedded resources. It uses the property <code>HTTPResponse.parsers</code>, which is a list of parser ids,
  e.g. <code>htmlParser</code> and <code>wmlParser</code>. For each id found, JMeter checks two further properties:</p>
  <ul>
  <li><code>id.types</code> - a list of content types</li>
  <li><code>id.className</code> - the parser to be used to extract the embedded resources</li>
  </ul>
  <p>See <code>jmeter.properties</code> file for the details of the settings.
  If the <code>HTTPResponse.parser</code> property is not set, JMeter reverts to the previous behaviour,
  i.e. only <code>text/html</code> responses will be scanned</p>
 <b>Emulating slow connections:</b><br></br>
 <code>HttpClient31</code>, <code>HttpClient4</code> and <code>Java</code> Sampler support emulation of slow connections; see the following entries in <code>jmeter.properties</code>:
 <source>
 # Define characters per second &gt; 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 </source>
 <p><b>Response size calculation</b><br></br>
 Optional properties to allow change the method to get response size:<br></br></p>
 <ul><li>Gets the real network size in bytes for the body response
 <source>sampleresult.getbytes.body_real_size=true</source></li>
 <li>Add HTTP headers to full response size
 <source>sampleresult.getbytes.headers_size=true</source></li></ul>
 
 <note>
 The <code>Java</code> and <code>HttpClient3</code> implementations do not include transport overhead such as
 chunk headers in the response body size.<br></br>
 The <code>HttpClient4</code> implementation does include the overhead in the response body size,
 so the value may be greater than the number of bytes in the response content.
 </note>
 
 <note>When those two properties are set  <code>false</code>, JMeter returns only data response size (uncompressed if request uses gzip/deflate mode).</note>
 <p>
 <b>Retry handling</b><br></br>
 For HttpClient4 and Commons HttpClient 3.1 samplers, the retry count has been set to <code>0</code>, meaning not retry is attempted. 
 Note that the Java implementation appears to retry 1 time.
 The retry count can be overridden by setting the relevant JMeter property, for example:
 </p>
 <source>
 httpclient4.retrycount=3
 httpclient3.retrycount=3
 </source>
 <p>
 <b>Note: Certificates does not conform to algorithm constraints</b><br></br>
 You may encounter the following error: <code>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</code>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like <code>md2WithRSAEncryption</code>) or with a SSL certificate with a size lower than 1024 bits.
 </p><p>
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 </p><p>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 </p><p>
 This property is in this file:</p>
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 <p>See  <bugzilla>56357</bugzilla> for details.
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
 then the following variables will be set up:</p>
 <source>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </source>
 <p>
 If the Select statement returns zero rows, then the <code>A_#</code> and <code>C_#</code> variables would be set to <code>0</code>, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves six rows and a second select returns only three rows,
 the additional variables for rows four, five and six will be removed.
 </p>
 <note>The latency time is set from the time it took to acquire a connection.</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">
         Name of the JMeter variable that the connection pool is bound to.
         This must agree with the '<code>Variable Name</code>' field of a <complink name="JDBC Connection Configuration"/>.
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
             When "<code>Prepared Select Statement</code>", "<code>Prepared Update Statement</code>" or "<code>Callable Statement</code>" types are selected, a Statement Cache per connection is used by JDBC Request. 
             It stores by default up to 100 PreparedStatements per connection, this can impact your database (Open Cursors).
             </note>
         </property>
         <property name="SQL Query" required="Yes">
         SQL query.
         <note>Do not enter a trailing semi-colon.</note>
         There is generally no need to use <code>{</code> and <code>}</code> to enclose Callable statements;
         however they may be used if the database uses a non-standard syntax.
         [The JDBC driver automatically converts the statement if necessary when it is enclosed in <code>{}</code>].
         For example:
         <ul>
         <li><code>select * from t_customers where id=23</code></li>
         <li><code>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null,?, ?, null, null, null)</code>
         <ul>
         <li>Parameter values: <code>tablename</code>,<code>filename</code></li>
         <li>Parameter types:  <code>VARCHAR</code>,<code>VARCHAR</code></li>
         </ul>
         </li>
         The second example assumes you are using Apache Derby.
         </ul>
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use <code>]NULL[</code> to indicate a <code>NULL</code> parameter.
         (If required, the null string can be changed by defining the property "<code>jdbcsampler.nullmarker</code>".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <source>"Dbl-Quote: "" and Comma: ,"</source>
         <note>There must be as many values as there are placeholders in the statement even if your parameters are <code>OUT</code> ones, be sure to set a value even if the value will not be used (for example in a CallableStatement).</note>
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. <code>INTEGER</code>, <code>DATE</code>, <code>VARCHAR</code>, <code>DOUBLE</code>) or integer values of Constants when for example you use custom database types proposed by driver (<code>-10</code> for <code>OracleTypes.CURSOR</code> for example).<br/>
         These are defined as fields in the class <code>java.sql.Types</code>, see for example:<br/>
         <a href="http://docs.oracle.com/javase/7/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.<br/>
         [Note: JMeter will use whatever types are defined by the runtime JVM,
         so if you are running on a different JVM, be sure to check the appropriate document]<br/>
         <b>If the callable statement has <code>INOUT</code> or <code>OUT</code> parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "<code>INTEGER</code>", use "<code>INOUT INTEGER</code>".</b> <br/>
         If not specified, "<code>IN</code>" is assumed, i.e. "<code>DATE</code>" is the same as "<code>IN DATE</code>".
         <br></br>
         If the type is not one of the fields found in <code>java.sql.Types</code>, JMeter also
         accepts the corresponding integer number, e.g. since <code>OracleTypes.CURSOR == -10</code>, you can use "<code>INOUT -10</code>".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements, Prepared Select Statements or CallableStatement. 
         Note that when used with CallableStatement, list of variables must be in the same sequence as the <code>OUT</code> parameters returned by the call.
         If there are less variable names than <code>OUT</code> parameters only as many results shall be stored in the thread-context variables as variable names were supplied.
         If more variable names than <code>OUT</code> parameters exist, the additional variables will be ignored</property>
         <property name="Result Variable Name" required="No">
         If specified, this will create an Object variable containing a list of row maps.
         Each map contains the column name as the key and the column data as the value. Usage:<br></br>
         <source>columnValue = vars.getObject("resultObject").get(0).get("Column Name");</source>
         </property>
         <property name="Handle ResultSet" required="No">Defines how ResultSet returned from callable statements be handled:
             <ul>
                 <li>Store As String (default) - All variables on Variable Names list are stored as strings, will not iterate through a <code>ResultSet</code> when present on the list.</li>
                 <li>Store As Object - Variables of <code>ResultSet</code> type on Variables Names list will be stored as Object and can be accessed in subsequent tests/scripts and iterated, will not iterate through the <code>ResultSet</code> </li>
                 <li>Count Records - Variables of <code>ResultSet</code> types will be iterated through showing the count of records as result. Variables will be stored as Strings.</li>
             </ul>
         </property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Versions of JMeter use UTF-8 as the character encoding. Previously the platform default was used.</note>
 <note>Ensure Variable Name is unique across Test Plan.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="563" height="347" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (<code>JavaTest</code> and <code>SleepTest</code>) are provided.
 </p>
 <p>
 The <code>JavaTest</code> example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>If the method <code>teardownTest</code> is not overriden by a subclass of <code>AbstractJavaSamplerClient</code>, its <code>teardownTest</code> method will not be called.
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
 
     <p>The following parameters apply to the <code>SleepTest</code> and <code>JavaTest</code> implementations:</p>
 
     <properties>
         <property name="Sleep_time" required="Yes">How long to sleep for (ms)</property>
         <property name="Sleep_mask" required="Yes">How much "randomness" to add:<br></br>
             The sleep time is calculated as follows:
             <source>totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)</source>
         </property>
     </properties>
 
     <p>The following parameters apply additionally to the <code>JavaTest</code> implementation:</p>
 
     <properties>
         <property name="Label" required="No">The label to use. If provided, overrides <code>Name</code></property>
         <property name="ResponseCode" required="No">If provided, sets the SampleResult ResponseCode.</property>
         <property name="ResponseMessage" required="No">If provided, sets the SampleResult ResponseMessage.</property>
         <property name="Status" required="No">If provided, sets the SampleResult Status. If this equals "<code>OK</code>" (ignoring case) then the status is set to success, otherwise the sample is marked as failed.</property>
         <property name="SamplerData" required="No">If provided, sets the SampleResult SamplerData.</property>
         <property name="ResultData" required="No">If provided, sets the SampleResult ResultData.</property>
     </properties>
 </component>
 
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="426" height="276" screenshot="soap_sampler.png">
 <note>
 See <a href="build-ws-test-plan.html">Building a WebService Test Plan</a> for up to date way of test SOAP and REST Webservices
 </note>
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content.
 To change the "<code>Content-type</code>" from the default of "<code>text/xml</code>", use a <complink name="HTTP Header Manager" />.
 Note that the sampler will use all the headers from the <complink name="HTTP Header Manager"/>.
 If a SOAP action is specified, that will override any <code>SOAPaction</code> in the <complink name="HTTP Header Manager"/>.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>The sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child <complink name="HTTP Header Manager"/>
 with <code>Content-Length</code> set to the actual length of the file.<br/>
 Or set <code>Content-Length</code> to <code>-1</code> to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the <complink name="HTTP Header Manager"/>)</property>
         <property name="Use KeepAlive" required="No">If set, sends <code>Connection: keep-alive</code>, else sends <code>Connection: close</code></property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7" width="621" height="462" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(<code>Add</code>, <code>Modify</code>, <code>Delete</code> and <code>Search</code>) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol>
     <li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li>
   </ol>
 
   <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
   <ol>
     <li>Add Test
       <ol>
         <li>Inbuilt test:
           <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p>
         </li>
         <li>User defined test:
           <p>This will add the entry in the LDAP Server. User has to enter all the
           attributes in the table.The entries are collected from the table to add. The
           execution time is calculated. The created entry will not be deleted after the
           test.</p>
         </li>
       </ol>
     </li>
     <li>Modify Test
       <ol>
         <li>Inbuilt test:
           <p>This will create a pre-defined entry first, then will modify the
           created entry in the LDAP Server.And calculate the execution time. After
           execution
           of the test, the created entry will be deleted from the LDAP Server.</p>
         </li>
         <li>User defined test:
           <p>This will modify the entry in the LDAP Server. User has to enter all the
           attributes in the table. The entries are collected from the table to modify.
           The execution time is calculated. The entry will not be deleted from the LDAP
           Server.</p>
         </li>
       </ol>
     </li>
     <li>Search Test
       <ol>
         <li>Inbuilt test:
           <p>This will create the entry first, then will search if the attributes
           are available. It calculates the execution time of the search query. At the
           end of  the execution,created entry will be deleted from the LDAP Server.</p>
         </li>
         <li>User defined test:
           <p>This will search the user defined entry(Search filter) in the Search
           base (again, defined by the user). The entries should be available in the LDAP
           Server. The execution time is  calculated.</p>
         </li>
       </ol>
     </li>
     <li>Delete Test
       <ol>
         <li>Inbuilt test:
           <p>This will create a pre-defined entry first, then it will be deleted
           from the LDAP Server. The execution time is calculated.</p>
         </li>
         <li>User defined test:
           <p>This will delete the user-defined entry in the LDAP Server. The entries
           should be available in the LDAP Server. The execution time is calculated.</p>
         </li>
       </ol>
     </li>
   </ol>
   <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
       JMeter assumes the LDAP server is listening on the default port (<code>389</code>).</property>
     <property name="Port" required="Yes">Port to connect to (default is <code>389</code>).</property>
     <property name="root DN" required="Yes">Base DN to use for ldap operations</property>
     <property name="Username" required="Usually">LDAP server username.</property>
     <property name="Password" required="Usually">LDAP server password. (N.B. this is stored unencrypted in the test plan)</property>
     <property name="Entry DN" required="Yes, if User Defined Test and Add Test or Modify Test is selected">the name of the context to create or Modify; may not be empty.
      <note>You have to set the right attributes of the object yourself. So if you want to add <code>cn=apache,ou=test</code>
       you have to add in the table <code>name</code> and <code>value</code> to <code>cn</code> and <code>apache</code>.
      </note>
     </property>
     <property name="Delete" required="Yes, if User Defined Test and Delete Test is selected">the name of the context to Delete; may not be empty</property>
     <property name="Search base" required="Yes, if User Defined Test and Search Test is selected">the name of the context or object to search</property>
     <property name="Search filter" required="Yes, if User Defined Test and Search Test is selected"> the filter expression to use for the search; may not be null</property>
     <property name="add test" required="Yes, if User Defined Test and add Test is selected">Use these <code>name</code>, <code>value</code> pairs for creation of the new object in the given context</property>
     <property name="modify test" required="Yes, if User Defined Test and Modify Test is selected">Use these <code>name</code>, <code>value</code> pairs for modification of the given context object</property>
   </properties>
 
   <links>
     <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
     <complink name="LDAP Request Defaults"/>
   </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8" width="619" height="371" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP requests to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <dl>
       <dt><b>Thread bind</b></dt>
       <dd>
         <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
         For starting this session a thread bind is used, which is equal to the LDAP "<code>bind</code>" operation.
         The user is requested to give a <code>username</code> (Distinguished name) and <code>password</code>,
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
             If it is incorrect, the sampler will return an error and revert to an anonymous bind. (N.B. this is stored unencrypted in the test plan)</property>
         </properties>
       </dd>
       <dt><b>Thread unbind</b></dt>
       <dd>
         <p>This is simply the operation to end a session.
         It is equal to the LDAP "<code>unbind</code>" operation.</p>
         <properties>
           <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         </properties>
       </dd>
       <dt><b>Single bind/unbind</b></dt>
       <dd>
         <p> This is a combination of the LDAP "<code>bind</code>" and "<code>unbind</code>" operations.
         It can be used for an authentication request/password check for any user. It will open an new session, just to
         check the validity of the user/password combination, and end the session again.</p>
         <properties>
           <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
           <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
           <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind.
             If it is incorrect, the sampler will return an error. (N.B. this is stored unencrypted in the test plan)</property>
         </properties>
       </dd>
       <dt><b>Rename entry</b></dt>
       <dd>
        <p>This is the LDAP "<code>moddn</code>" operation. It can be used to rename an entry, but
        also for moving an entry or a complete subtree to a different place in
        the LDAP tree.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move,
            relative to the given DN in the thread bind operation.</property>
          <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move,
            relative to the given DN in the thread bind operation.</property>
        </properties>
      </dd>
      <dt><b>Add test</b></dt>
      <dd>
        <p>This is the ldap "<code>add</code>" operation. It can be used to add any kind of
        object to the LDAP server.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
          <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
            If you need to add a multiple value attribute, you need to add the same attribute with their respective
            values several times to the list.</property>
        </properties>
      </dd>
      <dt><b>Delete test</b></dt>
      <dd>
        <p> This is the LDAP "<code>delete</code>" operation, it can be used to delete an
        object from the LDAP tree</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
        </properties>
      </dd>
      <dt><b>Search test</b></dt>
      <dd>
        <p>This is the LDAP "<code>search</code>" operation, and will be used for defining searches.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Search base" required="No">Distinguished name of the subtree you want your
            search to look in, relative to the given DN in the thread bind operation.</property>
          <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
          <property name="Scope" required="No">Use <code>0</code> for baseobject-, <code>1</code> for onelevel- and <code>2</code> for a subtree search. (Default=<code>0</code>)</property>
          <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=<code>0</code>, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode <code>4</code></property>
          <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in milliseconds) that the server can spend on your search. Take care, this does not say anything about the responsetime. (default is <code>0</code>, which means no limit)</property>
          <property name="Attributes" required="No">Specify the attributes you want to have returned, separated by a semicolon. An empty field will return all attributes</property>
          <property name="Return object" required="No">Whether the object will be returned (<code>true</code>) or not (<code>false</code>). Default=<code>false</code></property>
          <property name="Dereference aliases" required="No">If <code>true</code>, it will dereference aliases, if <code>false</code>, it will not follow them (default=<code>false</code>)</property>
          <property name="Parse the search results?" required="No">If <code>true</code>, the search results will be added to the response data. If <code>false</code>, a marker - whether results where found or not - will be added to the response data.</property>
        </properties>
      </dd>
      <dt><b>Modification test</b></dt>
      <dd>
        <p>This is the LDAP "<code>modify</code>" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
            to the given DN in the thread bind operation</property>
          <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any
            valid LDAP operationCode (<code>add</code>, <code>delete</code>/<code>remove</code> or <code>replace</code>).
            If you don't specify a value with a <code>delete</code> operation,
            all values of the given attribute will be deleted. If you do specify a value in a <code>delete</code> operation, only
            the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
        </properties>
      </dd>
      <dt><b>Compare</b></dt>
      <dd>
        <p>This is the LDAP "<code>compare</code>" operation. It can be used to compare the value
        of a given attribute with some already known value. In reality this is mostly
        used to check whether a given person is a member of some group. In such a case
        you can compare the DN of the user as a given value, with the values in the
        attribute "<code>member</code>" of an object of the type <code>groupOfNames</code>.
        If the compare operation fails, this test fails with errorcode <code>49</code>.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry DN" required="Yes">The current distinguished name of the object of
            which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
          <property name="Compare filter" required="Yes">In the form "<code>attribute=value</code>"</property>
        </properties>
      </dd>
    </dl>
    <links>
      <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
      <complink name="LDAP Extended Request Defaults"/>
    </links>
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="613" height="318" screenshot="accesslogsampler.png">
 <center><h2>(Beta Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means every image, css file, javascript file, html file, &hellip;
 The current implementation is complete, but some features have not been enabled. 
 There is a filter for the access log parser, but I haven't figured out how to link to the pre-processor. 
 Once I do, changes to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <source>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</source>
 <note>The current implementation of the parser only looks at the text within the quotes that contains one of the HTTP protocol methods (<code>GET</code>, <code>PUT</code>, <code>POST</code>, <code>DELETE</code>, &hellip;).
 Everything else is stripped out and ignored. For example, the response code is completely
 ignored by the parser. </note>
 <p>For the future, it might be nice to filter out entries that
 do not have a response code of <code>200</code>. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement:</p>
 <ul>
 <li><code>org.apache.jmeter.protocol.http.util.accesslog.LogParser</code></li>
 <li><code>org.apache.jmeter.protocol.http.util.accesslog.Generator</code></li>
 </ul>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer <code>1</code>, telling it to parse one entry. After that,
 <code>HTTPSampler.sample()</code> is called to make the request.</p>
 <source>
 samp = (HTTPSampler) GENERATOR.generateRequest();
 samp.setDomain(this.getDomain());
 samp.setPort(this.getPort());
 samp.setImageParser(this.isImageParser());
 PARSER.parse(1);
 res = samp.sample();
 res.setSampleLabel(samp.toString());
 </source>
 The required methods in <code>LogParser</code> are:
 <ul>
 <li><code>setGenerator(Generator)</code></li>
 <li><code>parse(int)</code></li> 
 </ul>
 <p>
 Classes implementing <code>Generator</code> interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 <code>StandardGenerator</code> and <code>TCLogParser</code>.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Protocol" required="No (defaults to http">Scheme</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The <code>TCLogParser</code> processes the access log independently for each thread.
 The <code>SharedTCLogParser</code> and <code>OrderPreservingLogParser</code> share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The <code>SessionFilter</code> is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The <code>LogFilter</code> is intended to allow access log entries to be filtered by filename and regex,
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
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> interface methods.
 These must be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 <p>
 The BeanShell sampler also supports the <code>Interruptible</code> interface.
 The <code>interrupt()</code> method can be defined in the script or the init file.
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
     <dl>
         <dt><code>Parameters</code></dt><dd>string containing the parameters as a single variable</dd>
         <dt><code>bsh.args</code></dt><dd>String array containing parameters, split on white-space</dd>
     </dl></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not <code>null</code>) is stored as the sampler result.</property>
 </properties>
 <note>
 N.B. Each Sampler instance has its own BeanShell interpreter,
 and Samplers are only called from a single thread
 </note><p>
 If the property "<code>beanshell.sampler.init</code>" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: <code>BeanShellSampler.bshrc</code>.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g.<code>props.get("START.HMS"); props.put("PROP1","1234");</code>
 <br/>
 BeanShell does not currently support Java 5 syntax such as generics and the enhanced for loop.
 </note>
         <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
         <p>The contents of the Parameters field is put into the variable "<code>Parameters</code>".
             The string is also split into separate tokens using a single space as the separator, and the resulting list
             is stored in the String array <code>bsh.args</code>.</p>
         <p>The full list of BeanShell variables that is set up is as follows:</p>
         <ul>
         <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a></li>
         <li><code>Label</code> - the Sampler label</li>
         <li><code>FileName</code> - the file name, if any</li>
         <li><code>Parameters</code> - text from the Parameters field</li>
         <li><code>bsh.args</code> - the parameters, split as described above</li>
         <li><code>SampleResult</code> - pointer to the current SampleResult</li>
             <li><code>ResponseCode</code> defaults to <code>200</code></li>
             <li><code>ResponseMessage</code> defaults to "<code>OK</code>"</li>
             <li><code>IsSuccess</code> defaults to <code>true</code></li>
             <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
             <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
                <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.remove("VAR3");
 vars.putObject("OBJ1",new Object());</source></li>
             <li><code>props</code> - JMeterProperties (class <code>java.util.Properties</code>) - e.g.
                 <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
         </ul>
         <p>When the script completes, control is returned to the Sampler, and it copies the contents
             of the following script variables into the corresponding variables in the <a href="../api/org/apache/jmeter/samplers/SampleResult.html"><code>SampleResult</code></a>:</p>
             <ul>
             <li><code>ResponseCode</code> - for example <code>200</code></li>
             <li><code>ResponseMessage</code> - for example "<code>OK</code>"</li>
             <li><code>IsSuccess</code> - <code>true</code> or <code>false</code></li>
             </ul>
             <p>The SampleResult ResponseData is set from the return value of the script.
             If the script returns null, it can set the response directly, by using the method 
             <code>SampleResult.setResponseData(data)</code>, where data is either a String or a byte array.
             The data type defaults to "<code>text</code>", but can be set to binary by using the method
             <code>SampleResult.setDataType(SampleResult.BINARY)</code>.
             </p>
             <p>The <code>SampleResult</code> variable gives the script full access to all the fields and
                 methods in the <code>SampleResult</code>. For example, the script has access to the methods
                 <code>setStopThread(boolean)</code> and <code>setStopTest(boolean)</code>.
 
                 Here is a simple (not very useful!) example script:</p>
 
 <source>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or
 SampleResult.setResponseData("My data");
 return null;
 </source>
 <p>Another example:<br></br> ensure that the property <code>beanshell.sampler.init=BeanShellSampler.bshrc</code> is defined in <code>jmeter.properties</code>. 
 The following script will show the values of all the variables in the <code>ResponseData</code> field:
 </p>
 <source>
 return getVariables();
 </source>
 <p>
 For details on the methods available for the various classes (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html"><code>JMeterVariables</code></a>, <a href="../api/org/apache/jmeter/samplers/SampleResult.html"><code>SampleResult</code></a> etc.) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find.
 </p>
 </component>
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="848" height="590" screenshot="bsfsampler.png">
     <description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
         See the <a href="http://commons.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
         website for details of the languages supported.
         You may need to download the appropriate jars for the language; they should be put in the JMeter <code>lib</code> directory.
         </p>
         <note>
         The BSF API has been largely superseded by JSR-223, which is included in Java 6 onwards.
         Most scripting languages now include support for JSR-223; please use the JSR223 Sampler instead.
         The BSF Sampler should only be needed for supporting legacy languages/test scripts.
         </note>
         <p>By default, JMeter supports the following languages:</p>
         <ul>
         <li>javascript</li>
         <li>jexl</li>
         <li>xslt</li>
         </ul>
         <note>Unlike the BeanShell sampler, the interpreter is not saved between invocations.</note>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
       <note>N.B. Not all the languages in the drop-down list are supported by default.
         The following are supported: jexl, javascript, xslt.
         Others may be available if the appropriate jar is installed in the JMeter lib directory.
       </note>
     </property>
     <property name="Script File" required="No">Name of a file to be used as a BSF script, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
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
 e.g.<code>props.get("START.HMS"); props.put("PROP1","1234");</code> 
 </note>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a></li>
 <li><code>Label</code> - the Sampler label</li>
 <li><code>FileName</code> - the file name, if any</li>
 <li><code>Parameters</code> - text from the Parameters field</li>
 <li><code>args</code> - the parameters, split as described above</li>
 <li><code>SampleResult</code> - pointer to the current <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a></li>
 <li><code>sampler</code> - <a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a> - pointer to current Sampler</li>
 <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
 <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
   <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.remove("VAR3");
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - JMeterProperties  (class <code>java.util.Properties</code>) - e.g. 
   <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>
 The <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> ResponseData is set from the return value of the script.
 If the script returns <code>null</code>, it can set the response directly, by using the method 
 <code>SampleResult.setResponseData(data)</code>, where data is either a String or a byte array.
 The data type defaults to "<code>text</code>", but can be set to binary by using the method
 <code>SampleResult.setDataType(SampleResult.BINARY)</code>.
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 <code>setStopThread(boolean)</code> and <code>setStopTest(boolean)</code>.
 </p>
 <p>
 Unlike the BeanShell Sampler, the BSF Sampler does not set the <code>ResponseCode</code>, <code>ResponseMessage</code> and sample status via script variables.
 Currently the only way to changes these is via the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> methods:
 </p>
 <ul>
 <li><code>SampleResult.setSuccessful(true/false)</code></li>
 <li><code>SampleResult.setResponseCode("code")</code></li>
@@ -2888,2225 +2888,2225 @@ Match[1][0]=JMeter - Apache JMeter&lt;/title&gt;
 Match[1][1]=JMeter
 Match[2][0]=JMeter" title="JMeter" border="0"/&gt;&lt;/a&gt;
 Match[2][1]=JMeter
 Match[3][0]=JMeterCommitters"&gt;Contributors&lt;/a&gt;
 Match[3][1]=JMeterCommitters
 &hellip; and so on &hellip;
 </source>
 <br/>
 The first number in <code>[]</code> is the match number; the second number is the group. 
 Group <code>[0]</code> is whatever matched the whole RE.
 Group <code>[1]</code> is whatever matched the 1<sup>st</sup> group, i.e. <code>(JMeter\w*)</code> in this case.
 See Figure 9b (below).
 <br/></td></tr>
 <tr><td><code>Text</code></td>
 <td>
 The default <i>Text view</i> shows all of the text contained in the response. 
 Note that this will only work if the response <code>content-type</code> is considered to be text.
 If the <code>content-type</code> begins with any of the following, it is considered as binary,
 otherwise it is considered to be text.
 <source>
 image/
 audio/
 video/
 </source>
 <br/></td></tr>
 <tr><td><code>XML</code></td>
 <td>The <i>XML view</i> will show response in tree style. 
 Any DTD nodes or Prolog nodes will not show up in tree; however, response may contain those nodes.
 <br/></td></tr>
 <tr><td><code>XPath Tester</code></td>
 <td>The <i>XPath Tester</i> only works for text responses. It shows the plain text in the upper panel.
 The "<code>Test</code>" button allows the user to apply the XPath query to the upper panel and the results
 will be displayed in the lower panel.<br/>
 </td></tr>
 </table>
 <p><code>Scroll automatically?</code> option permit to have last node display in tree selection</p>
 <p>
 With <code>Search</code> option, most of the views also allow the displayed data to be searched; the result of the search will be high-lighted
 in the display above. For example the Control panel screenshot below shows one result of searching for "<code>Java</code>".
 Note that the search operates on the visible text, so you may get different results when searching
 the Text and HTML views.
 <br/>Note: The regular expression uses the Java engine (not ORO engine like the Regular Expression Extractor or Regexp Tester view).
 </p>
 <p>
 If there is no <code>content-type</code> provided, then the content
 will not be displayed in the any of the Response Data panels.
 You can use <complink name="Save Responses to a file"/> to save the data in this case.
 Note that the response data will still be available in the sample result,
 so can still be accessed using Post-Processors.
 </p>
 <p>If the response data is larger than 200K, then it won't be displayed.
 To change this limit, set the JMeter property <code>view.results.tree.max_size</code>.
 You can also use save the entire response to a file using
 <complink name="Save Responses to a file"/>.
 </p>
 <p>
 Additional renderers can be created.
 The class must implement the interface <code>org.apache.jmeter.visualizers.ResultRenderer</code>
 and/or extend the abstract class <code>org.apache.jmeter.visualizers.SamplerResultTab</code>, and the
 compiled code must be available to JMeter (e.g. by adding it to the <code>lib/ext</code> directory).
 </p>
 </description>
 <p>
     The Control Panel (above) shows an example of an HTML display.<br/>
     Figure 9 (below) shows an example of an XML display.<br/>
     Figure 9a (below) shows an example of an Regexp tester display.<br/>
     Figure 9b (below) shows an example of an Document display.<br/>
 </p>
     <div align="center">
 <figure width="873" height="653" image="view_results_tree_xml.png">Figure 9 Sample XML display</figure>
 <figure width="858" height="643" image="view_results_tree_regex.png">Figure 9a Sample Regexp Test display</figure>
 <figure width="961" height="623" image="view_results_tree_document.png">Figure 9b Sample Document (here PDF) display</figure>
 </div>
 </component>
 
 <component name="Aggregate Report" index="&sect-num;.3.7"  width="1140" height="266" screenshot="aggregate_report.png">
 <description>The aggregate report creates a table row for each differently named request in your
 test.  For each request, it totals the response information and provides request count, min, max,
 average, error rate, approximate throughput (request/second) and Kilobytes per second throughput.
 Once the test is done, the throughput is the actual through for the duration of the entire test.
 <p>
 The throughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler names correctly to get the best results from
 the Aggregate Report.
 </p>
 <p>
 Calculation of the <a href="glossary.html#Median">Median</a> and 90% Line (90<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>) values requires additional memory.
 JMeter now combines samples with the same elapsed time, so far less memory is used.
 However, for samples that take more than a few seconds, the probability is that fewer samples will have identical times,
 in which case more memory will be needed.
 Note you can use this listener afterwards to reload a CSV or XML results file which is the recommended way to avoid performance impacts.
 See the <complink name="Summary Report"/> for a similar Listener that does not store individual samples and so needs constant memory.
 </p>
 <note>
 Starting with JMeter 2.12, you can configure the 3 percentile values you want to compute, this can be done by setting properties:
 <ul>
     <li><code>aggregate_rpt_pct1</code>: defaults to 90<sup>th</sup> <a href="glossary.html#Percentile">percentile</a></li>
     <li><code>aggregate_rpt_pct2</code>: defaults to 95<sup>th</sup> <a href="glossary.html#Percentile">percentile</a></li>
     <li><code>aggregate_rpt_pct3</code>: defaults to 99<sup>th</sup> <a href="glossary.html#Percentile">percentile</a></li>
 </ul>
 </note>
 <ul>
 <li><code>Label</code> - The label of the sample.
 If "<code>Include group name in label?</code>" is selected, then the name of the thread group is added as a prefix.
 This allows identical labels from different thread groups to be collated separately if required.
 </li>
 <li><code># Samples</code> - The number of samples with the same label</li>
 <li><code>Average</code> - The average time of a set of results</li>
 <li><code>Median</code> - The <a href="glossary.html#Median">median</a> is the time in the middle of a set of results.
 50% of the samples took no more than this time; the remainder took at least as long.</li>
 <li><code>90% Line</code> - 90% of the samples took no more than this time.
 The remaining samples took at least as long as this. (90<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>)</li>
 <li><code>95% Line</code> - 95% of the samples took no more than this time.
 The remaining samples took at least as long as this. (95<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>)</li>
 <li><code>99% Line</code> - 99% of the samples took no more than this time.
 The remaining samples took at least as long as this. (99<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>)</li>
 <li><code>Min</code> - The shortest time for the samples with the same label</li>
 <li>Max - The longest time for the samples with the same label</li>
 <li><code>Error %</code> - Percent of requests with errors</li>
 <li><code>Throughput</code> - the <a href="glossary.html#Throughput">Throughput</a> is measured in requests per second/minute/hour.
 The time unit is chosen so that the displayed rate is at least 1.0.
 When the throughput is saved to a CSV file, it is expressed in requests/second,
 i.e. 30.0 requests/minute is saved as 0.5.
 </li>
 <li><code>Kb/sec</code> - The throughput measured in Kilobytes per second</li>
 </ul>
 <p>Times are in milliseconds.</p>
 </description>
 <div align="center">
 <p>
     The figure below shows an example of selecting the "<code>Include group name</code>" checkbox.
 </p>
 <figure width="1140" height="276" image="aggregate_report_grouped.png">Sample "<code>Include group name</code>" display</figure>
 </div>
 </component>
 
 <component name="View Results in Table" index="&sect-num;.3.8"  width="966" height="683" screenshot="table_results.png">
 <description>This visualizer creates a row for every sample result.  
 Like the <complink name="View Results Tree"/>, this visualizer uses a lot of memory.
 <p>
 By default, it only displays the main (parent) samples; it does not display the sub-samples (child samples).
 JMeter has a "<code>Child Samples?</code>" check-box.
 If this is selected, then the sub-samples are displayed instead of the main samples.  
 </p>
 </description>
 </component>
 
 <component name="Simple Data Writer" index="&sect-num;.3.9"  width="741" height="141" screenshot="simpledatawriter.png">
 <description>This listener can record results to a file
 but not to the UI.  It is meant to provide an efficient means of
 recording data by eliminating GUI overhead.
 When running in non-GUI mode, the <code>-l</code> flag can be used to create a data file.
 The fields to save are defined by JMeter properties.
 See the <code>jmeter.properties</code> file for details.
 </description>
 </component>
 
 <component name="Monitor Results" index="&sect-num;.3.10"  width="762" height="757" screenshot="monitor_screencap.png">
 <description>
 <p>Monitor Results is a new Visualizer for displaying server
 status. It is designed for Tomcat 5, but any servlet container
 can port the status servlet and use this monitor. There are two primary
 tabs for the monitor. The first is the "<code>Health</code>" tab, which will show the
 status of one or more servers. The second tab labled "<code>Performance</code>" shows
 the performance for one server for the last 1000 samples. The equations
 used for the load calculation is included in the Visualizer.</p>
 <p>Currently, the primary limitation of the monitor is system memory. A
 quick benchmark of memory usage indicates a buffer of 1000 data points for
 100 servers would take roughly 10Mb of RAM. On a 1.4Ghz centrino
 laptop with 1Gb of ram, the monitor should be able to handle several
 hundred servers.</p>
 <p>As a general rule, monitoring production systems should take care to
 set an appropriate interval. Intervals shorter than 5 seconds are too
 aggressive and have a potential of impacting the server. With a buffer of
 1000 data points at 5 second intervals, the monitor would check the server
 status 12 times a minute or 720 times a hour. This means the buffer shows
 the performance history of each machine for the last hour.</p>
 <note>
 The monitor requires Tomcat 5 or above. 
 Use a browser to check that you can access the Tomcat status servlet OK.
 </note>
 <p>
 For a detailed description of how to use the monitor, please refer to
 <a href="build-monitor-test-plan.html">Building a Monitor Test Plan</a>
 </p>
 </description>
 </component>
 
 <component name="Distribution Graph (DEPRECATED)" index="&sect-num;.3.11"  width="819" height="626" screenshot="distribution_graph.png">
 <description>
 <note>
 Distribution Graph MUST NOT BE USED during load test as it consumes a lot of resources (memory and CPU). Use it only for either functional testing or 
 during Test Plan debugging and Validation.
 </note>
 
 <p>The distribution graph will display a bar for every unique response time. Since the
 granularity of <code>System.currentTimeMillis()</code> is 10 milliseconds, the 90% threshold should be
 within the width of the graph. The graph will draw two threshold lines: 50% and 90%.
 What this means is 50% of the response times finished between 0 and the line. The same
 is true of 90% line. Several tests with Tomcat were performed using 30 threads for 600K
 requests. The graph was able to display the distribution without any problems and both
 the 50% and 90% line were within the width of the graph. A performant application will
 generally produce results that clump together. A poorly written application that has
 memory leaks may result in wild fluctuations. In those situations, the threshold lines
 may be beyond the width of the graph. The recommended solution to this specific problem
 is fix the webapp so it performs well. If your test plan produces distribution graphs
 with no apparent clumping or pattern, it may indicate a memory leak. The only way to
 know for sure is to use a profiling tool.</p>
 </description>
 </component>
 
 <component name="Aggregate Graph" index="&sect-num;.3.12"  width="1132" height="872" screenshot="aggregate_graph.png">
 <description>The aggregate graph is similar to the aggregate report. The primary
 difference is the aggregate graph provides an easy way to generate bar graphs and save
 the graph as a PNG file.</description>
 <div align="center">
 <p>
     The figure below shows an example of settings to draw this graph.
 </p>
 <figure width="1147" height="420" image="aggregate_graph_settings.png">Aggregate graph settings</figure>
 </div>
 <note>Please note: All this parameters <em>aren't</em> saved in JMeter jmx script.</note>
 <properties>
         <property name="Column settings" required="Yes">
         <ul>
         <li><code>Columns to display:</code> Choose the column(s) to display in graph.</li>
         <li><code>Rectangles color:</code> Click on right color rectangle open a popup dialog to choose a custom color for column.</li>
         <li><code>Foreground color</code> Allow to change the value text color.</li>
         <li><code>Value font:</code> Allow to define font settings for the text.</li>
         <li><code>Draw outlines bar?</code> To draw or not the border line on bar chart</li>
         <li><code>Show number grouping?</code> Show or not the number grouping in Y Axis labels.</li>
         <li><code>Value labels vertical?</code> Change orientation for value label. (Default is horizontal)</li>
         <li><code>Column label selection:</code> Filter by result label. A regular expression can be used, example: <code>.*Transaction.*</code>
         <br></br>Before display the graph, click on <code>Apply filter</code> button to refresh internal data.</li>
         </ul>
         </property>
         <property name="Title" required="No">Define the graph's title on the head of chart. Empty value is the default value : "<code>Aggregate Graph</code>". 
         The button <code>Synchronize with name</code> define the title with the label of the listener. And define font settings for graph title</property>
         <property name="Graph size" required="No">Compute the graph size by  the width and height depending of the current JMeter's window size.
         Use <code>Width</code> and <code>Height</code> fields to define a custom size. The unit is pixel. </property>
         <property name="X Axis settings" required="No">Define the max length of X Axis label (in pixel).</property>
         <property name="Y Axis settings" required="No">Define a custom maximum value for Y Axis.</property>
         <property name="Legend" required="Yes">Define the placement and font settings for chart legend</property>
 </properties>
 </component>
 
 <component name="Response Time Graph" index="&sect-num;.3.13"  width="921" height="616" screenshot="response_time_graph.png">
 <description>
 The Response Time Graph draws a line chart showing the evolution of response time during the test, for each labelled request. 
 If many samples exist for the same timestamp, the mean value is displayed.
 </description>
 <div align="center">
 <p>
     The figure below shows an example of settings to draw this graph.
 </p>
 <figure width="919" height="481" image="response_time_graph_settings.png">Response time graph settings</figure>
 </div>
 <note>Please note: All this parameters are saved in JMeter <code>.jmx</code> file.</note>
 <properties>
         <property name="Interval (ms)" required="Yes">The time in milli-seconds for X axis interval. Samples are grouped according to this value.
         Before display the graph, click on <code>Apply interval</code> button to refresh internal data.</property>
         <property name="Sampler label selection" required="No">Filter by result label. A regular expression can be used, ex. <code>.*Transaction.*</code>. 
         Before display the graph, click on <code>Apply filter</code> button to refresh internal data.</property>
         <property name="Title" required="No">Define the graph's title on the head of chart. Empty value is the default value : "<code>Response Time Graph</code>". 
         The button <code>Synchronize with name</code> define the title with the label of the listener. And define font settings for graph title</property>
         <property name="Line settings" required="Yes">Define the width of the line. Define the type of each value point. Choose <code>none</code> to have a line without mark</property>
         <property name="Graph size" required="No">Compute the graph size by  the width and height depending of the current JMeter's window size.
         Use <code>Width</code> and <code>Height</code> fields to define a custom size. The unit is pixel. </property>
         <property name="X Axis settings" required="No">Customize the date format of  X axis label.
         The syntax is the Java <a href="http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat API</a>.</property>
         <property name="Y Axis settings" required="No">Define a custom maximum value for Y Axis in milli-seconds. Define the increment for the scale (in ms) Show or not the number grouping in Y Axis labels.</property>
         <property name="Legend" required="Yes">Define the placement and font settings for chart legend</property>
 </properties>
 </component>
 
 <component name="Mailer Visualizer" index="&sect-num;.3.14"  width="860" height="403" screenshot="mailervisualizer.png">
 <description><p>The mailer visualizer can be set up to send email if a test run receives too many
 failed responses from the server.</p></description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="From" required="Yes">Email address to send messages from.</property>
         <property name="Addressee(s)" required="Yes">Email address to send messages to, comma-separated.</property>
         <property name="Success Subject" required="No">Email subject line for success messages.</property>
         <property name="Success Limit" required="Yes">Once this number of successful responses is exceeded
         <strong>after previously reaching the failure limit</strong>, a success email
         is sent.  The mailer will thus only send out messages in a sequence of failed-succeeded-failed-succeeded, etc.</property>
         <property name="Failure Subject" required="No">Email subject line for fail messages.</property>
         <property name="Failure Limit" required="Yes">Once this number of failed responses is exceeded, a failure
         email is sent - i.e. set the count to <code>0</code> to send an e-mail on the first failure.</property>
 
         <property name="Host" required="No">IP address or host name of SMTP server (email redirector)
         server.</property>
         <property name="Port" required="No">Port of SMTP server (defaults to <code>25</code>).</property>
         <property name="Login" required="No">Login used to authenticate.</property>
         <property name="Password" required="No">Password used to authenticate.</property>
         <property name="Connection security" required="No">Type of encryption for SMTP authentication (SSL, TLS or none).</property>
 
         <property name="Test Mail" required="No">Press this button to send a test mail</property>
         <property name="Failures" required="No">A field that keeps a running total of number
         of failures so far received.</property>
 </properties>
 </component>
 
 <component name="BeanShell Listener"  index="&sect-num;.3.15"  width="844" height="633" screenshot="beanshell_listener.png">
 <description>
 <p>
 The BeanShell Listener allows the use of BeanShell for processing samples for saving etc.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable Label</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <dl>
         <dt><code>Parameters</code></dt><dd>string containing the parameters as a single variable</dd>
         <dt><code>bsh.args</code></dt><dd>String array containing parameters, split on white-space</dd>
     </dl></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <code>java.util.Properties</code>) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>sampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a></li>
 <li><code>sampleEvent</code> (<a href="../api/org/apache/jmeter/samplers/SampleEvent.html">SampleEvent</a>) gives access to the current sample event</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.listener.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 <component name="Summary Report" index="&sect-num;.3.16"  width="926" height="324" screenshot="summary_report.png">
 <description>The summary report creates a table row for each differently named request in your
 test. This is similar to the <complink name="Aggregate Report"/> , except that it uses less memory.
 <p>
 The throughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler labels correctly to get the best results from
 the Report.
 </p>
 <ul>
 <li><code>Label</code> - The label of the sample.
 If "<code>Include group name in label?</code>" is selected, then the name of the thread group is added as a prefix.
 This allows identical labels from different thread groups to be collated separately if required.
 </li>
 <li><code># Samples</code> - The number of samples with the same label</li>
 <li><code>Average</code> - The average elapsed time of a set of results</li>
 <li><code>Min</code> - The lowest elapsed time for the samples with the same label</li>
 <li><code>Max</code> - The longest elapsed time for the samples with the same label</li>
 <li><code>Std. Dev.</code> - the <a href="glossary.html#StandardDeviation">Standard Deviation</a> of the sample elapsed time</li>
 <li><code>Error %</code> - Percent of requests with errors</li>
 <li><code>Throughput</code> - the <a href="glossary.html#Throughput">Throughput</a> is measured in requests per second/minute/hour.
 The time unit is chosen so that the displayed rate is at least <code>1.0</code>.
 When the throughput is saved to a CSV file, it is expressed in requests/second,
 i.e. 30.0 requests/minute is saved as <code>0.5</code>.
 </li>
 <li><code>Kb/sec</code> - The throughput measured in Kilobytes per second</li>
 <li><code>Avg. Bytes</code> - average size of the sample response in bytes.</li>
 </ul>
 <p>Times are in milliseconds.</p>
 </description>
 <div align="center">
 <p>
     The figure below shows an example of selecting the "<code>Include group name</code>" checkbox.
 </p>
 <figure width="923" height="325" image="summary_report_grouped.png">Sample "<code>Include group name</code>" display</figure>
 </div>
 </component>
 
 <component name="Save Responses to a file" index="&sect-num;.3.17"  width="488" height="251" screenshot="savetofile.png">
     <description>
         <p>
         This test element can be placed anywhere in the test plan.
         For each sample in its scope, it will create a file of the response Data.
         The primary use for this is in creating functional tests, but it can also
         be useful where the response is too large to be displayed in the 
         <complink name="View Results Tree"/> Listener.
         The file name is created from the specified prefix, plus a number (unless this is disabled, see below).
         The file extension is created from the document type, if known.
         If not known, the file extension is set to '<code>unknown</code>'.
         If numbering is disabled, and adding a suffix is disabled, then the file prefix is
         taken as the entire file name. This allows a fixed file name to be generated if required.
         The generated file name is stored in the sample response, and can be saved
         in the test log output file if required.
         </p>
         <p>
         The current sample is saved first, followed by any sub-samples (child samples).
         If a variable name is provided, then the names of the files are saved in the order
         that the sub-samples appear. See below. 
         </p>
     </description>
  <properties>
  <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
  <property name="Filename Prefix" required="Yes">Prefix for the generated file names; this can include a directory name.
         Relative paths are resolved relative to the current working directory (which defaults to the <code>bin/</code> directory).
         JMeter also supports paths relative to the directory containing the current test plan (JMX file).
         If the path name begins with "<code>~/</code>" (or whatever is in the <code>jmeter.save.saveservice.base_prefix</code> JMeter property),
         then the path is assumed to be relative to the JMX file location. 
  </property>
  <property name="Variable Name" required="No">
  Name of a variable in which to save the generated file name (so it can be used later in the test plan).
  If there are sub-samples then a numeric suffix is added to the variable name.
  E.g. if the variable name is <code>FILENAME</code>, then the parent sample file name is saved in the variable <code>FILENAME</code>, 
  and the filenames for the child samplers are saved in <code>FILENAME1</code>, <code>FILENAME2</code> etc.
  </property>
  <property name="Save Failed Responses only" required="Yes">If selected, then only failed responses are saved</property>
  <property name="Save Successful Responses only" required="Yes">If selected, then only successful responses are saved</property>
  <property name="Don't add number to prefix" required="Yes">If selected, then no number is added to the prefix. If you select this option, make sure that the prefix is unique or the file may be overwritten.</property>
  <property name="Don't add suffix" required="Yes">If selected, then no suffix is added. If you select this option, make sure that the prefix is unique or the file may be overwritten.</property>
  <property name="Minimum Length of sequence number" required="No">If "<code>Don't add number to prefix</code>" is not checked, then numbers added to prefix will be padded by <code>0</code> so that prefix is has size of this value. Defaults to <code>0</code>.</property>
  </properties>
 </component>
 
 <component name="BSF Listener" index="&sect-num;.3.18"  width="847" height="634" screenshot="bsf_listener.png">
 <description>
 <p>
 The BSF Listener allows BSF script code to be applied to sample results.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <dl>
         <dt><code>Parameters</code></dt><dd>string containing the parameters as a single variable</dd>
         <dt><code>args</code></dt><dd>String array containing parameters, split on white-space</dd>
     </dl></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <dl>
 <dt><code>log</code></dt><dd>(<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</dd>
 <dt><code>Label</code></dt><dd>the String Label</dd>
 <dt><code>FileName</code></dt><dd>the script file name (if any)</dd>
 <dt><code>Parameters</code></dt><dd>the parameters (as a String)</dd>
 <dt><code>args</code></dt><dd>the parameters as a String array (split on whitespace)</dd>
 <dt><code>ctx</code></dt><dd>(<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</dd>
 <dt><code>vars</code></dt><dd>(<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></dd>
 <dt><code>props</code></dt><dd>(JMeterProperties - class <code>java.util.Properties</code>) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></dd>
 <dt><code>sampleResult</code>, <code>prev</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the SampleResult</dd>
 <dt><code>sampleEvent</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/SampleEvent.html">SampleEvent</a>) - gives access to the SampleEvent</dd>
 <dt><code>sampler</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the last sampler</dd>
 <dt><code>OUT</code></dt><dd><code>System.out</code> - e.g. <code>OUT.println("message")</code></dd>
 </dl>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Listener" index="&sect-num;.3.18.1">
 <description>
 <p>
 The JSR223 Listener allows JSR223 script code to be applied to sample results.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <dl>
         <dt><code>Parameters</code></dt><dd>string containing the parameters as a single variable</dd>
         <dt><code>args</code></dt><dd>String array containing parameters, split on white-space</dd>
     </dl></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code>Compilable</code> interface (Groovy is one of these, java, beanshell and javascript are not)</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are JSR223 variables - i.e. they can be used directly in the script.
 </p>
 <dl>
 <dt><code>log</code></dt><dd>(<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</dd>
 <dt><code>Label</code></dt><dd>the String Label</dd>
 <dt><code>FileName</code></dt><dd>the script file name (if any)</dd>
 <dt><code>Parameters</code></dt><dd>the parameters (as a String)</dd>
 <dt><code>args</code></dt><dd>the parameters as a String array (split on whitespace)</dd>
 <dt><code>ctx</code></dt><dd>(<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</dd>
 <dt><code>vars</code></dt><dd>(<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");</source></dd>
 <dt><code>props</code></dt><dd>(JMeterProperties - class <code>java.util.Properties</code>) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></dd>
 <dt><code>sampleResult</code>, <code>prev</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the SampleResult</dd>
 <dt><code>sampleEvent</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/SampleEvent.html">SampleEvent</a>) - gives access to the SampleEvent</dd>
 <dt><code>sampler</code></dt><dd>(<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>)- gives access to the last sampler</dd>
 <dt><code>OUT</code></dt><dd><code>System.out</code> - e.g. <code>OUT.println("message")</code></dd>
 </dl>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Generate Summary Results" index="&sect-num;.3.19"  width="358" height="131" screenshot="summary.png">
     <description>This test element can be placed anywhere in the test plan.
 Generates a summary of the test run so far to the log file and/or 
 standard output. Both running and differential totals are shown.
 Output is generated every <code>n</code> seconds (default 30 seconds) on the appropriate
 time boundary, so that multiple test runs on the same time will be synchronised.
 See <code>jmeter.properties</code> file for the summariser configuration items:
 <source>
 # Define the following property to automatically start a summariser with that name
 # (applies to non-GUI mode only)
 #summariser.name=summary
 #
 # interval between summaries (in seconds) default 3 minutes
 #summariser.interval=30
 #
 # Write messages to log file
 #summariser.log=true
 #
 # Write messages to System.out
 #summariser.out=true
 </source>
 This element is mainly intended for batch (non-GUI) runs.
 The output looks like the following:
 <source>
 label +     16 in 0:00:12 =    1.3/s Avg:  1608 Min:  1163 Max:  2009 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label +     82 in 0:00:30 =    2.7/s Avg:  1518 Min:  1003 Max:  2020 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =     98 in 0:00:42 =    2.3/s Avg:  1533 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     85 in 0:00:30 =    2.8/s Avg:  1505 Min:  1008 Max:  2005 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    183 in 0:01:13 =    2.5/s Avg:  1520 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     79 in 0:00:30 =    2.7/s Avg:  1578 Min:  1089 Max:  2012 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    262 in 0:01:43 =    2.6/s Avg:  1538 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     80 in 0:00:30 =    2.7/s Avg:  1531 Min:  1013 Max:  2014 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    342 in 0:02:12 =    2.6/s Avg:  1536 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     83 in 0:00:31 =    2.7/s Avg:  1512 Min:  1003 Max:  1982 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    425 in 0:02:43 =    2.6/s Avg:  1531 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     83 in 0:00:29 =    2.8/s Avg:  1487 Min:  1023 Max:  2013 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    508 in 0:03:12 =    2.6/s Avg:  1524 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     78 in 0:00:30 =    2.6/s Avg:  1594 Min:  1013 Max:  2016 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    586 in 0:03:43 =    2.6/s Avg:  1533 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     80 in 0:00:30 =    2.7/s Avg:  1516 Min:  1013 Max:  2005 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    666 in 0:04:12 =    2.6/s Avg:  1531 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     86 in 0:00:30 =    2.9/s Avg:  1449 Min:  1004 Max:  2017 Err:     0 (0.00%) Active: 5 Started: 5 Finished: 0
 label =    752 in 0:04:43 =    2.7/s Avg:  1522 Min:  1003 Max:  2020 Err:     0 (0.00%)
 label +     65 in 0:00:24 =    2.7/s Avg:  1579 Min:  1007 Max:  2003 Err:     0 (0.00%) Active: 0 Started: 5 Finished: 5
 label =    817 in 0:05:07 =    2.7/s Avg:  1526 Min:  1003 Max:  2020 Err:     0 (0.00%)
 </source>
 The "<code>label</code>" is the name of the element.
 The <code>"+"</code> means that the line is a delta line, i.e. shows the changes since the last output.<br/>
 The <code>"="</code> means that the line is a totals line, i.e. it shows the running total.<br/>
 Entries in the jmeter log file also include time-stamps.
 The example "<code>817 in 0:05:07 =    2.7/s</code>" means that there were 817 samples recorded in 5 minutes and 7 seconds,
 and that works out at 2.7 samples per second.<br/>
 The <code>Avg</code> (Average), <code>Min</code> (Minimum) and <code>Max</code> (Maximum) times are in milliseconds.<br/>
 "<code>Err</code>" means number of errors (also shown as percentage).<br/>
 The last two lines will appear at the end of a test.
 They will not be synchronised to the appropriate time boundary.
 Note that the initial and final deltas may be for less than the interval (in the example above this is 30 seconds).
 The first delta will generally be lower, as JMeter synchronizes to the interval boundary.
 The last delta will be lower, as the test will generally not finish on an exact interval boundary.
 <p>
 The label is used to group sample results together. 
 So if you have multiple Thread Groups and want to summarize across them all, then use the same label
  - or add the summariser to the Test Plan (so all thread groups are in scope).
 Different summary groupings can be implemented
 by using suitable labels and adding the summarisers to appropriate parts of the test plan.
 </p>
 <note>
 In Non-GUI mode by default a Generate Summary Results listener named "<code>summariser</code>" is configured, if you have already added one to your Test Plan, ensure you name it differently 
 otherwise results will be accumulated under this label (summary) leading to wrong results (sum of total samples + samples located under the Parent of Generate Summary Results listener).<br/>
 This is not a bug but a design choice allowing to summarize across thread groups.
 </note>
     </description>
  <properties>
  <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.
  It appears as the "<code>label</code>" in the output. Details for all elements with the same label will be added together.
  </property>
  </properties>
 </component>
 
 <component name="Comparison Assertion Visualizer" index="&sect-num;.3.20"  width="777" height="266" screenshot="comparison_assertion_visualizer.png">
 <description>
 The Comparison Assertion Visualizer shows the results of any <complink name="Compare Assertion"/> elements.
 </description>
  <properties>
  <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.
  </property>
  </properties>
 </component>
 
 <component name="Backend Listener" index="&sect-num;.3.21"  width="902" height="341" screenshot="backend_listener.png">
 <description>
 The backend listener is an Asynchronous listener that enables you to plug custom implementations of <a href="../api/org/apache/jmeter/visualizers/backend/BackendListenerClient.html">BackendListenerClient</a>.
 By default, a Graphite implementation is provided.
 </description>
  <properties>
  <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
  <property name="Backend Listener implementation" required="Yes">Class of the <code>BackendListenerClient</code> implementation.</property>
  <property name="Async Queue size" required="Yes">Size of the queue that holds the SampleResults while they are processed asynchronously.</property>
  <property name="Parameters" required="Yes">Parameters of the <code>BackendListenerClient</code> implementation.</property>
  </properties>
  
  
      <p>The following parameters apply to the <code>GraphiteBackendListenerClient</code> implementation:</p>
 
     <properties>
         <property name="graphiteMetricsSender" required="Yes"><code>org.apache.jmeter.visualizers.backend.graphite.TextGraphiteMetricsSender</code> or <code>org.apache.jmeter.visualizers.backend.graphite.PickleGraphiteMetricsSender</code></property>
         <property name="graphiteHost" required="Yes">Graphite or InfluxDB (with Graphite plugin enabled) server host</property>
         <property name="graphitePort" required="Yes">Graphite or InfluxDB (with Graphite plugin enabled) server port, defaults to <code>2003</code>. Note <code>PickleGraphiteMetricsSender</code> (port <code>2004</code>) can only talk to Graphite server.</property>
         <property name="rootMetricsPrefix" required="Yes">Prefix of metrics sent to backend. Defaults to "<code>jmeter</code>."</property>
         <property name="summaryOnly" required="Yes">Only send a summary with no detail. Defaults to <code>true</code>.</property>
         <property name="samplersList" required="Yes">Semicolon separated list of samplers for which you want to report metrics to backend.</property>
         <property name="percentiles" required="Yes">The percentiles you want to send to backend. List must be semicolon separated.</property>
     </properties>
     <p>Read <a href="realtime-results.html" >this</a> for more details.</p>
     <figure width="1265" height="581" image="grafana_dashboard.png">Grafana dashboard</figure>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.4 Configuration Elements" anchor="config_elements">
 <description>
     <br></br>
     Configuration elements can be used to set up defaults and variables for later use by samplers.
     Note that these elements are processed at the start of the scope in which they are found, 
     i.e. before any samplers in the same scope.
     <br></br>
 </description>
 
 <component name="CSV Data Set Config" index="&sect-num;.4.1"  width="433" height="281" screenshot="csvdatasetconfig.png">
 <description>
     <p>
     CSV Data Set Config is used to read lines from a file, and split them into variables.
     It is easier to use than the <code>__CSVRead()</code> and <code>_StringFromFile()</code> functions.
     It is well suited to handling large numbers of variables, and is also useful for testing with
     "random" and unique values.</p>
     <p>Generating unique random values at run-time is expensive in terms of CPU and memory, so just create the data
     in advance of the test. If necessary, the "random" data from the file can be used in conjunction with
     a run-time parameter to create different sets of values from each run - e.g. using concatenation - which is
     much cheaper than generating everything at run-time.
     </p>
     <p>
     JMeter allows values to be quoted; this allows the value to contain a delimiter.
     If "<code>allow quoted data</code>" is enabled, a value may be enclosed in double-quotes.
     These are removed. To include double-quotes within a quoted field, use two double-quotes.
     For example:
     </p>
 <source>
 1,"2,3","4""5" =>
 1
 2,3
 4"5
 </source>
     <p>
     JMeter supports CSV files which have a header line defining the column names.
     To enable this, leave the "<code>Variable Names</code>" field empty. The correct delimiter must be provided.
     </p>
     <p>
     JMeter supports CSV files with quoted data that includes new-lines.
     </p>
     <p>
     By default, the file is only opened once, and each thread will use a different line from the file.
     However the order in which lines are passed to threads depends on the order in which they execute,
     which may vary between iterations.
     Lines are read at the start of each test iteration.
     The file name and mode are resolved in the first iteration.
     </p>
     <p>
     See the description of the Share mode below for additional options.
     If you want each thread to have its own set of values, then you will need to create a set of files,
     one for each thread. For example <code>test1.csv</code>, <code>test2.csv</code>, &hellip;, <code>test<em>n</em>.csv</code>. Use the filename 
     <code>test${__threadNum}.csv</code> and set the "<code>Sharing mode</code>" to "<code>Current thread</code>".
     </p>
     <note>CSV Dataset variables are defined at the start of each test iteration.
     As this is after configuration processing is completed,
     they cannot be used for some configuration items - such as JDBC Config - 
     that process their contents at configuration time (see <bugzilla>40394</bugzilla>)
     However the variables do work in the HTTP Auth Manager, as the <code>username</code> etc. are processed at run-time.
     </note>
     <p>
     As a special case, the string "<code>\t</code>" (without quotes) in the delimiter field is treated as a Tab.
     </p>
     <p>
     When the end of file (<code>EOF</code>) is reached, and the recycle option is <code>true</code>, reading starts again with the first line of the file.
     </p>
     <p>
     If the recycle option is <code>false</code>, and stopThread is <code>false</code>, then all the variables are set to <code>&lt;EOF&gt;</code> when the end of file is reached.
     This value can be changed by setting the JMeter property <code>csvdataset.eofstring</code>.
     </p>
     <p>
     If the Recycle option is <code>false</code>, and Stop Thread is <code>true</code>, then reaching <code>EOF</code> will cause the thread to be stopped.
     </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Filename" required="Yes">Name of the file to be read. 
   <b>Relative file names are resolved with respect to the path of the active test plan.</b>
   <b>For distributed testing, the CSV file must be stored on the server host system in the correct relative directory to where the jmeter server is started.</b>
   Absolute file names are also supported, but note that they are unlikely to work in remote mode, 
   unless the remote server has the same directory structure.
   If the same physical file is referenced in two different ways - e.g. <code>csvdata.txt</code> and <code>./csvdata.txt</code> -
   then these are treated as different files.
   If the OS does not distinguish between upper and lower case, <code>csvData.TXT</code> would also be opened separately. 
   </property>
   <property name="File Encoding" required="No">The encoding to be used to read the file, if not the platform default.</property>
   <property name="Variable Names" required="Yes">List of variable names (comma-delimited).
   JMeter supports CSV header lines:
   if the variable name field empty, then the first line of the file is read and interpreted as the list of column names.
   The names must be separated by the delimiter character. They can be quoted using double-quotes.
   </property>
   <property name="Delimiter" required="Yes">Delimiter to be used to split the records in the file.
   If there are fewer values on the line than there are variables the remaining variables are not updated -
   so they will retain their previous value (if any).</property>
   <property name="Allow quoted data?" required="Yes">Should the CSV file allow values to be quoted?
   If enabled, then values can be enclosed in <code>"</code> - double-quote - allowing values to contain a delimiter.
   </property>
   <property name="Recycle on EOF?" required="Yes">Should the file be re-read from the beginning on reaching <code>EOF</code>? (default is <code>true</code>)</property>
   <property name="Stop thread on EOF?" required="Yes">Should the thread be stopped on <code>EOF</code>, if Recycle is false? (default is <code>false</code>)</property>
   <property name="Sharing mode" required="Yes">
   <ul>
   <li><code>All threads</code> - (the default) the file is shared between all the threads.</li>
   <li><code>Current thread group</code> - each file is opened once for each thread group in which the element appears</li>
   <li><code>Current thread</code> - each file is opened separately for each thread</li>
   <li><code>Identifier</code> - all threads sharing the same identifier share the same file.
   So for example if you have 4 thread groups, you could use a common id for two or more of the groups
   to share the file between them.
   Or you could use the thread number to share the file between the same thread numbers in different thread groups.
   </li>
   </ul>
   </property>
 </properties>
 </component>
 
 <component name="FTP Request Defaults" index="&sect-num;.4.2"  width="520" height="202" screenshot="ftp-config/ftp-request-defaults.png">
 <description></description>
 </component>
 
 <component name="DNS Cache Manager" index="&sect-num;.4.3"  width="712" height="387" screenshot="dns-cache-manager.png">
     <note>DNS Cache Manager is designed for using in the root of Thread Group or Test Plan. Do not place it as child element of particular HTTP Sampler
     </note>
     <note>DNS Cache Manager working only with the HTTP request using HTTPClient4 implementation.</note>
     <description><p>The DNS Cache Manager element allows to test applications, which have several servers behind load balancers (CDN, etc.), 
     when user receives content from different IP's. By default JMeter uses JVM DNS cache. That's why
     only one server from the cluster receives load. DNS Cache Manager resolves name for each thread separately each iteration and
     saves results of resolving to its internal DNS Cache, which independent from both JVM and OS DNS caches.
     </p>
     </description>
     <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
     <property name="Clear cache each Iteration" required="No">If selected, DNS cache of every  Thread is cleared each time new iteration is started.</property>
     <property name="Use system DNS resolver" required="N/A">System DNS resolver will be used. For correct work edit
        <code>$JAVA_HOME/jre/lib/security/java.security</code> and add <code>networkaddress.cache.ttl=0</code> 
     </property>
     <property name="Use custom DNS resolver" required="N/A">Custom DNS resolver(from dnsjava library) will be used.</property>
     <property name="Hostname or IP address" required="No">List of DNS servers to use. If empty, network configuration DNS will used.</property>
     <property name="Add Button" required="N/A">Add an entry to the DNS servers table.</property>
     <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
     </properties>
 </component>
 
 <component name="HTTP Authorization Manager" index="&sect-num;.4.4"  width="538" height="340" screenshot="http-config/http-auth-manager.png">
 <note>If there is more than one Authorization Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.</note>
 
 <description>
 <p>The Authorization Manager lets you specify one or more user logins for web pages that are
 restricted using server authentication.  You see this type of authentication when you use
 your browser to access a restricted page, and your browser displays a login dialog box.  JMeter
 transmits the login information when it encounters this type of page.</p>
 <p>
 The Authorization headers may not be shown in the Tree View Listener "<code>Request</code>" tab.
 The Java implementation does pre-emptive authentication, but it does not
 return the Authorization header when JMeter fetches the headers.
 The Commons HttpClient (3.1) implementation defaults to pre-emptive and the header will be shown.
 The HttpComponents (HC 4.5.X) implementation does not do pre-emptive auth 
 (it is supported by the library but JMeter does not enable it)
 </p>
 <p>
 The HttpClient3.1 implementation defaults to pre-emptive authentication
 if the setting has not been defined. To disable this, set the values as below, in which case
 authentication will only be performed in response to a challenge.
 </p>
 <p>
 In the file <code>jmeter.properties</code> set <code>httpclient.parameters.file=httpclient.parameters</code>
 and in <code>httpclient.parameters</code> set <code>http.authentication.preemptive$Boolean=false</code>
 </p>
 <note>
 Note: the above settings only apply to the HttpClient sampler.
 </note>
 <note>
 When looking for a match against a URL, JMeter checks each entry in turn, and stops when it finds the first match.
 Thus the most specific URLs should appear first in the list, followed by less specific ones.
 Duplicate URLs will be ignored.
 If you want to use different usernames/passwords for different threads, you can use variables.
 These can be set up using a <complink name="CSV Data Set Config"/> Element (for example).
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
         <property name="Clear auth on each iteration?" required="Yes">Used by Kerberos authentication. If checked, authentication will be done on each iteration of Main Thread Group loop even if it has already been done in a previous one.
         This is usually useful if each main thread group iteration represents behaviour of one Virtual User.
         </property>
   <property name="Base URL" required="Yes">A partial or complete URL that matches one or more HTTP Request URLs.  As an example,
 say you specify a Base URL of "<code>http://localhost/restricted/</code>" with a <code>Username</code> of "<code>jmeter</code>" and
 a <code>Password</code> of "<code>jmeter</code>".  If you send an HTTP request to the URL
 "<code>http://localhost/restricted/ant/myPage.html</code>", the Authorization Manager sends the login
 information for the user named, "<code>jmeter</code>".</property>
   <property name="Username" required="Yes">The username to authorize.</property>
   <property name="Password" required="Yes">The password for the user. (N.B. this is stored unencrypted in the test plan)</property>
   <property name="Domain" required="No">The domain to use for NTLM.</property>
   <property name="Realm" required="No">The realm to use for NTLM.</property>
   <property name="Mechanism" required="No">Type of authentication to perform. JMeter can perform different types of authentications based on used Http Samplers:
 <dl>
 <dt>Java</dt><dd><code>BASIC</code></dd>
 <dt>HttpClient 3.1</dt><dd><code>BASIC</code> and <code>DIGEST</code></dd>
 <dt>HttpClient 4</dt><dd><code>BASIC</code>, <code>DIGEST</code> and <code>Kerberos</code></dd>
 </dl>
 </property>
 </properties>
 <note>
 The Realm only applies to the HttpClient sampler.
 </note>
 <br></br>
 <b>Kerberos Configuration:</b>
 <p>To configure Kerberos you need to setup at least two JVM system properties:</p>
 <ul>
     <li><code>-Djava.security.krb5.conf=krb5.conf</code></li>
     <li><code>-Djava.security.auth.login.config=jaas.conf</code></li>
 </ul>
 <p>
 You can also configure those two properties in the file <code>bin/system.properties</code>.
 Look at the two sample configuration files (<code>krb5.conf</code> and <code>jaas.conf</code>) located in the jmeter <code>bin</code> folder
 for references to more documentation, and tweak them to match your Kerberos configuration.
 </p>
 <p>
 When generating a SPN for Kerberos SPNEGO authentication IE and Firefox will omit the port number
 from the url. Chrome has an option (<code>--enable-auth-negotiate-port</code>) to include the port
 number if it differs from the standard ones (<code>80</code> and <code>443</code>). That behavior
 can be emulated by setting the following jmeter property as below.
 </p>
 <p>
 In <code>jmeter.properties</code> or <code>user.properties</code>, set:
 </p>
 <ul>
 <li><code>kerberos.spnego.strip_port=false</code></li>
 </ul>
 <br></br>
 <b>Controls:</b>
 <ul>
   <li><code>Add</code> Button - Add an entry to the authorization table.</li>
   <li><code>Delete</code> Button - Delete the currently selected table entry.</li>
   <li><code>Load</code> Button - Load a previously saved authorization table and add the entries to the existing
 authorization table entries.</li>
   <li><code>Save As</code> Button - Save the current authorization table to a file.</li>
 </ul>
 
 <note>When you save the Test Plan, JMeter automatically saves all of the authorization
 table entries - including any passwords, which are not encrypted.</note>
 
 <example title="Authorization Example" anchor="authorization_example">
 
 <p><a href="../demos/AuthManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan on a local server that sends three HTTP requests, two requiring a login and the
 other is open to everyone.  See figure 10 to see the makeup of our Test Plan.  On our server, we have a restricted
 directory named, "<code>secret</code>", which contains two files, "<code>index.html</code>" and "<code>index2.html</code>".  We created a login id named, "<code>kevin</code>",
 which has a password of "<code>spot</code>".  So, in our Authorization Manager, we created an entry for the restricted directory and
 a username and password (see figure 11).  The two HTTP requests named "<code>SecretPage1</code>" and "<code>SecretPage2</code>" make requests
 to "<code>/secret/index.html</code>" and "<code>/secret/index2.html</code>".  The other HTTP request, named "<code>NoSecretPage</code>" makes a request to
 "<code>/index.html</code>".</p>
 
 <figure width="289" height="201" image="http-config/auth-manager-example1a.gif">Figure 10 - Test Plan</figure>
 <figure width="641" height="329" image="http-config/auth-manager-example1b.png">Figure 11 - Authorization Manager Control Panel</figure>
 
 <p>When we run the Test Plan, JMeter looks in the Authorization table for the URL it is requesting.  If the Base URL matches
 the URL, then JMeter passes this information along with the request.</p>
 
 <note>You can download the Test Plan, but since it is built as a test for our local server, you will not
 be able to run it.  However, you can use it as a reference in constructing your own Test Plan.</note>
 </example>
 
 </component>
 
 <component name="HTTP Cache Manager" index="&sect-num;.4.5"  width="511" height="196" screenshot="http-config/http-cache-manager.png">
 <description>
 <p>
 The HTTP Cache Manager is used to add caching functionality to HTTP requests within its scope to simulate browser cache feature.
 Each Virtual User thread has its own Cache. By default, Cache Manager will store up to 5000 items in cache per Virtual User thread, using LRU algorithm. 
 Use property "<code>maxSize</code>" to modify this value. Note that the more you increase this value the more HTTP Cache Manager will consume memory, so be sure to adapt the <code>-Xmx</code> jvm option accordingly.
 </p>
 <p>
 If a sample is successful (i.e. has response code <code>2xx</code>) then the <code>Last-Modified</code> and <code>Etag</code> (and <code>Expired</code> if relevant) values are saved for the URL.
 Before executing the next sample, the sampler checks to see if there is an entry in the cache, 
 and if so, the <code>If-Last-Modified</code> and <code>If-None-Match</code> conditional headers are set for the request.
 </p>
 <p>
 Additionally, if the "<code>Use Cache-Control/Expires header</code>" option is selected, then the <code>Cache-Control</code>/<code>Expires</code> value is checked against the current time.
 If the request is a <code>GET</code> request, and the timestamp is in the future, then the sampler returns immediately,
 without requesting the URL from the remote server. This is intended to emulate browser behaviour.
 Note that if <code>Cache-Control</code> header is "<code>no-cache</code>", the response will be stored in cache as pre-expired,
 so will generate a conditional <code>GET</code> request.
 If <code>Cache-Control</code> has any other value, 
 the "<code>max-age</code>" expiry option is processed to compute entry lifetime, if missing then expire header will be used, if also missing entry will be cached 
 as specified in <a href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a> section 13.2.4. using <code>Last-Modified</code> time and response Date.
 </p>
 <note>
 If the requested document has not changed since it was cached, then the response body will be empty.
 Likewise if the <code>Expires</code> date is in the future.
 This may cause problems for Assertions.
 </note>
 <note>Responses with a <code>Vary</code> header will not be cached.</note>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear cache each iteration" required="Yes">
   If selected, then the cache is cleared at the start of the thread.
   </property>
   <property name="Use Cache Control/Expires header when processing GET requests" required="Yes">See description above.</property>
   <property name="Max Number of elements in cache" required="Yes">See description above.</property>
 </properties>
 </component>
 
 <component name="HTTP Cookie Manager" index="&sect-num;.4.6"  width="513" height="383" screenshot="http-config/http-cookie-manager.png">
 
 <note>If there is more than one Cookie Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.
 Also, a cookie stored in one cookie manager is not available to any other manager,
 so use multiple Cookie Managers with care.</note>
 
 <description><p>The Cookie Manager element has two functions:<br></br>
 First, it stores and sends cookies just like a web browser. If you have an HTTP Request and
 the response contains a cookie, the Cookie Manager automatically stores that cookie and will
 use it for all future requests to that particular web site.  Each JMeter thread has its own
 "cookie storage area".  So, if you are testing a web site that uses a cookie for storing
 session information, each JMeter thread will have its own session.
 Note that such cookies do not appear on the Cookie Manager display, but they can be seen using
 the <complink name="View Results Tree"/> Listener.
 </p>
 <p>
 JMeter checks that received cookies are valid for the URL.
 This means that cross-domain cookies are not stored.
 If you have bugged behaviour or want Cross-Domain cookies to be used, define the JMeter property "<code>CookieManager.check.cookies=false</code>".
 </p>
 <p>
 Received Cookies can be stored as JMeter thread variables.
 To save cookies as variables, define the property "<code>CookieManager.save.cookies=true</code>".
 Also, cookies names are prefixed with "<code>COOKIE_</code>" before they are stored (this avoids accidental corruption of local variables)
 To revert to the original behaviour, define the property "<code>CookieManager.name.prefix= </code>" (one or more spaces).
 If enabled, the value of a cookie with the name <code>TEST</code> can be referred to as <code>${COOKIE_TEST}</code>.
 </p>
 <p>Second, you can manually add a cookie to the Cookie Manager.  However, if you do this,
 the cookie will be shared by all JMeter threads.</p>
 <p>Note that such Cookies are created with an Expiration time far in the future</p>
 <p>
 Cookies with <code>null</code> values are ignored by default.
 This can be changed by setting the JMeter property: <code>CookieManager.delete_null_cookies=false</code>.
 Note that this also applies to manually defined cookies - any such cookies will be removed from the display when it is updated.
 Note also that the cookie name must be unique - if a second cookie is defined with the same name, it will replace the first.
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear Cookies each Iteration" required="Yes">If selected, all server-defined cookies are cleared each time the main Thread Group loop is executed.
   Any cookie defined in the GUI are not cleared.</property>
   <property name="Cookie Policy" required="Yes">The cookie policy that will be used to manage the cookies. 
   "<code>standard</code>" is the default since 3.0, and should work in most cases. 
   See <a href="https://hc.apache.org/httpcomponents-client-ga/tutorial/html/statemgmt.html#d5e515">Cookie specifications</a> and 
   <a href="http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/cookie/CookieSpec.html">CookieSpec implementations</a>
   [Note: "<code>ignoreCookies</code>" is equivalent to omitting the CookieManager.]
   
-  If you still use HC3CookieHandler, default Cookie Policy is "<code>compatibility</code>", and should work in most cases.
+  If you still use HC3CookieHandler (which is DEPRECATED as of 3.0 version, so migration is highly advised), default Cookie Policy is "<code>compatibility</code>", and should work in most cases.
   See <a href="http://hc.apache.org/httpclient-3.x/cookies.html">http://hc.apache.org/httpclient-3.x/cookies.html</a> and 
   <a href="http://hc.apache.org/httpclient-3.x/apidocs/org/apache/commons/httpclient/cookie/CookiePolicy.html">http://hc.apache.org/httpclient-3.x/apidocs/org/apache/commons/httpclient/cookie/CookiePolicy.html</a>
     </property>
  <property name="Implementation" required="Yes"><code>HC4CookieHandler</code> (HttpClient 4.5.X API) or <code>HC3CookieHandler (deprecated)</code> (HttpClient 3 API). 
   Default is <code>HC4CookieHandler</code> since 3.0.
   <br></br>
   <i>[Note: If you have a website to test with IPv6 address, choose <code>HC4CookieHandler</code> (IPv6 compliant)]</i></property>
   <property name="User-Defined Cookies" required="No (discouraged, unless you know what you're doing)">This
   gives you the opportunity to use hardcoded cookies that will be used by all threads during the test execution.
   <br></br>
   The "<code>domain</code>" is the hostname of the server (without <code>http://</code>); the port is currently ignored.
   </property>
   <property name="Add Button" required="N/A">Add an entry to the cookie table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved cookie table and add the entries to the existing
 cookie table entries.</property>
   <property name="Save As Button" required="N/A">
   Save the current cookie table to a file (does not save any cookies extracted from HTTP Responses).
   </property>
 </properties>
 
 </component>
 
 <component name="HTTP Request Defaults" index="&sect-num;.4.7" width="853" height="463" 
          screenshot="http-config/http-request-defaults.png">
 <description><p>This element lets you set default values that your HTTP Request controllers use.  For example, if you are
 creating a Test Plan with 25 HTTP Request controllers and all of the requests are being sent to the same server,
 you could add a single HTTP Request Defaults element with the "<code>Server Name or IP</code>" field filled in.  Then, when
 you add the 25 HTTP Request controllers, leave the "<code>Server Name or IP</code>" field empty.  The controllers will inherit
 this field value from the HTTP Request Defaults element.</p>
 <note>
 All port values are treated equally; a sampler that does not specify a port will use the HTTP Request Defaults port, if one is provided.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Server" required="No">Domain name or IP address of the web server. e.g. <code>www.example.com</code>. [Do not include the <code>http://</code> prefix.</property>
         <property name="Port" required="No">Port the web server is listening to.</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
-        <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient3.1</code>, <code>HttpClient4</code>. 
+        <property name="Implementation" required="No"><code>Java</code>, <code>HttpClient3.1 (DEPRECATED SINCE 3.0)</code>, <code>HttpClient4</code>. 
         If not specified the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the <code>Java</code> implementation is used.</property>
         <property name="Protocol" required="No"><code>HTTP</code> or <code>HTTPS</code>.</property>
         <property name="Content encoding" required="No">The encoding to be used for the request.</property>
         <property name="Path" required="No">The path to resource (for example, <code>/servlets/myServlet</code>). If the
         resource requires query string parameters, add them below in the "<code>Send Parameters With the Request</code>" section.
         Note that the path is the default for the full path, not a prefix to be applied to paths
         specified on the HTTP Request screens.
         </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>.  The query string will be generated in the correct fashion, depending on
         the choice of "<code>Method</code>" you made (i.e. if you chose <code>GET</code>, the query string will be
         appended to the URL, if <code>POST</code>, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the <code>http://</code> prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server. (N.B. this is stored unencrypted in the test plan)</property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from <code>http://example.com/</code>, use the expression:
         <code>http://example\.com/.*</code>
         </property>
 </properties>
 <note>
 Note: radio buttons only have two states - on or off.
 This makes it impossible to override settings consistently
 - does off mean off, or does it mean use the current default?
 JMeter uses the latter (otherwise defaults would not work at all).
 So if the button is off, then a later element can set it on,
 but if the button is on, a later element cannot set it off.
 </note>
 </component>
 
 <component name="HTTP Header Manager" index="&sect-num;.4.8"  width="767" height="239" screenshot="http-config/http-header-manager.png">
 <description>
 <p>The Header Manager lets you add or override HTTP request headers.</p>
 <p>
 <b>JMeter now supports multiple Header Managers</b>. The header entries are merged to form the list for the sampler.
 If an entry to be merged matches an existing header name, it replaces the previous entry,
 unless the entry value is empty, in which case any existing entry is removed.
 This allows one to set up a default set of headers, and apply adjustments to particular samplers. 
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Name (Header)" required="No (You should have at least one, however)">Name of the request header.
         Two common request headers you may want to experiment with
 are "<code>User-Agent</code>" and "<code>Referer</code>".</property>
   <property name="Value" required="No (You should have at least one, however)">Request header value.</property>
   <property name="Add Button" required="N/A">Add an entry to the header table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved header table and add the entries to the existing
 header table entries.</property>
   <property name="Save As Button" required="N/A">Save the current header table to a file.</property>
 </properties>
 
 <example title="Header Manager example" anchor="header_manager_example">
 
 <p><a href="../demos/HeaderManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan
 that tells JMeter to override the default "<code>User-Agent</code>" request header and use a particular Internet Explorer agent string
 instead. (see figures 12 and 13).</p>
 
 <figure width="203" height="141" image="http-config/header-manager-example1a.gif">Figure 12 - Test Plan</figure>
 <figure image="http-config/header-manager-example1b.png">Figure 13 - Header Manager Control Panel</figure>
 </example>
 
 </component>
 
 <component name="Java Request Defaults" index="&sect-num;.4.9"  width="454" height="283" screenshot="java_defaults.png">
 <description><p>The Java Request Defaults component lets you set default values for Java testing.  See the <complink name="Java Request" />.</p>
 </description>
 
 </component>
 
 <component name="JDBC Connection Configuration" index="&sect-num;.4.10" 
                  width="474" height="458" screenshot="jdbc-config/jdbc-conn-config.png">
     <description>Creates a database connection (used by <complink name="JDBC Request"/>Sampler)
      from the supplied JDBC Connection settings. The connection may be optionally pooled between threads.
      Otherwise each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      The used pool is DBCP, see <a href="https://commons.apache.org/proper/commons-dbcp/configuration.html" >BasicDataSource Configuration Parameters</a>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">The name of the variable the connection is tied to.  
         Multiple connections can be used, each tied to a different variable, allowing JDBC Samplers
         to select the appropriate connection.
         <note>Each name must be different. If there are two configuration elements using the same name,
         only one will be saved. JMeter logs a message if a duplicate name is detected.</note>
         </property>
         <property name="Max Number of Connections" required="Yes">
         Maximum number of connections allowed in the pool.
         In most cases, <b>set this to zero (0)</b>.
         This means that each thread will get its own pool with a single connection in it, i.e.
         the connections are not shared between threads.
         <br />
         If you really want to use shared pooling (why?), then set the max count to the same as the number of threads
         to ensure threads don't wait on each other.
         </property>
         <property name="Max Wait (ms)" required="Yes">Pool throws an error if the timeout period is exceeded in the 
         process of trying to retrieve a connection, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getMaxWaitMillis--" >BasicDataSource.html#getMaxWaitMillis</a></property>
         <property name="Time Between Eviction Runs (ms)" required="Yes">The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run. (Defaults to "<code>60000</code>", 1 minute).
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTimeBetweenEvictionRunsMillis--" >BasicDataSource.html#getTimeBetweenEvictionRunsMillis</a></property>
         <property name="Auto Commit" required="Yes">Turn auto commit on or off for the connections.</property>
         <property name="Test While Idle" required="Yes">Test idle connections of the pool, see <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getTestWhileIdle--">BasicDataSource.html#getTestWhileIdle</a>. 
         Validation Query will be used to test it.</property>
         <property name="Soft Min Evictable Idle Time(ms)" required="Yes">Minimum amount of time a connection may sit idle in the pool before it is eligible for eviction by the idle object evictor, with the extra condition that at least <code>minIdle</code> connections remain in the pool.
         See <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/BasicDataSource.html#getSoftMinEvictableIdleTimeMillis--">BasicDataSource.html#getSoftMinEvictableIdleTimeMillis</a>.
         Defaults to 5000 (5 seconds)
         </property>
         <property name="Validation Query" required="Yes">A simple query used to determine if the database is still responding.</property>
         <property name="Database URL" required="Yes">JDBC Connection string for the database.</property>
         <property name="JDBC Driver class" required="Yes">Fully qualified name of driver class. (Must be in
         JMeter's classpath - easiest to copy <code>.jar</code> file into JMeter's <code>/lib</code> directory).</property>
         <property name="Username" required="No">Name of user to connect as.</property>
         <property name="Password" required="No">Password to connect with. (N.B. this is stored unencrypted in the test plan)</property>
     </properties>
 <p>Different databases and JDBC drivers require different JDBC settings. 
 The Database URL and JDBC Driver class are defined by the provider of the JDBC implementation.</p>
 <p>Some possible settings are shown below. Please check the exact details in the JDBC driver documentation.</p>
 
 <p>
 If JMeter reports <code>No suitable driver</code>, then this could mean either:
 </p>
 <ul>
 <li>The driver class was not found. In this case, there will be a log message such as <code>DataSourceElement: Could not load driver: {classname} java.lang.ClassNotFoundException: {classname}</code></li>
 <li>The driver class was found, but the class does not support the connection string. This could be because of a syntax error in the connection string, or because the wrong classname was used.</li>
 </ul>
 If the database server is not running or is not accessible, then JMeter will report a <code>java.net.ConnectException</code>.
 <table>
 <tr><th>Database</th><th>Driver class</th><th>Database URL</th></tr>
 <tr><td>MySQL</td><td><code>com.mysql.jdbc.Driver</code></td><td><code>jdbc:mysql://host[:port]/dbname</code></td></tr>
 <tr><td>PostgreSQL</td><td><code>org.postgresql.Driver</code></td><td><code>jdbc:postgresql:{dbname}</code></td></tr>
 <tr><td>Oracle</td><td><code>oracle.jdbc.OracleDriver</code></td><td><code>jdbc:oracle:thin:@//host:port/service</code>
 OR<br/><code>jdbc:oracle:thin:@(description=(address=(host={mc-name})(protocol=tcp)(port={port-no}))(connect_data=(sid={sid})))</code></td></tr>
 <tr><td>Ingres (2006)</td><td><code>ingres.jdbc.IngresDriver</code></td><td><code>jdbc:ingres://host:port/db[;attr=value]</code></td></tr>
 <tr><td>SQL Server (MS JDBC driver)</td><td><code>com.microsoft.sqlserver.jdbc.SQLServerDriver</code></td><td><code>jdbc:sqlserver://host:port;DatabaseName=dbname</code></td></tr>
 <tr><td>Apache Derby</td><td><code>org.apache.derby.jdbc.ClientDriver</code></td><td><code>jdbc:derby://server[:port]/databaseName[;URLAttributes=value[;&hellip;]]</code></td></tr>
 </table>
 <note>The above may not be correct - please check the relevant JDBC driver documentation.</note>
 </component>
 
 
 <component name="Keystore Configuration" index="&sect-num;.4.11"  width="441" height="189" screenshot="keystore_config.png">
 <description><p>The Keystore Config Element lets you configure how Keystore will be loaded and which keys it will use.
 This component is typically used in HTTPS scenarios where you don't want to take into account keystore initialization into account in response time.</p>
 <p>To use this element, you need to setup first a Java Key Store with the client certificates you want to test, to do that:
 </p>
 <ol>
 <li>Create your certificates either with Java <code>keytool</code> utility or through your PKI</li>
 <li>If created by PKI, import your keys in Java Key Store by converting them to a format acceptable by JKS</li>
 <li>Then reference the keystore file through the two JVM properties (or add them in <code>system.properties</code>):
     <ul>
         <li><code>-Djavax.net.ssl.keyStore=path_to_keystore</code></li>
         <li><code>-Djavax.net.ssl.keyStorePassword=password_of_keystore</code></li>
     </ul>
 </li>
 </ol>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Preload" required="Yes">Wether or not to preload Keystore. Setting it to <code>true</code> is usually the best option.</property>
   <property name="Variable name holding certificate alias" required="False">Variable name that will contain the alias to use for authentication by client certificate. Variable value will be filled from CSV Data Set for example. In the screenshot, "<code>certificat_ssl</code>" will also be a variable in CSV Data Set.</property>
   <property name="Alias Start Index" required="Yes">The index of the first key to use in Keystore, 0-based.</property>
   <property name="Alias End Index" required="Yes">The index of the last key to use in Keystore, 0-based. When using "<code>Variable name holding certificate alias</code>" ensure it is large enough so that all keys are loaded at startup.</property>
 </properties>
 <note>
 To make JMeter use more than one certificate you need to ensure that:
 <ul>
 <li><code>https.use.cached.ssl.context=false</code> is set in <code>jmeter.properties</code> or <code>user.properties</code></li>
-<li>You use either HTTPClient 3.1 or 4 implementations for HTTP Request</li>
+<li>You use either HTTPClient 4 (ADVISED) or HTTPClient 3.1 (DEPRECATED SINCE 3.0) implementations for HTTP Request</li>
 </ul>
 </note>
 </component>
 
 <component name="Login Config Element" index="&sect-num;.4.12"  width="352" height="112" screenshot="login-config.png">
 <description><p>The Login Config Element lets you add or override username and password settings in samplers that use username and password as part of their setup.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Username" required="No">The default username to use.</property>
   <property name="Password" required="No">The default password to use. (N.B. this is stored unencrypted in the test plan)</property>
 </properties>
 
 </component>
 
 <component name="LDAP Request Defaults" index="&sect-num;.4.13"  width="689" height="232" screenshot="ldap_defaults.png">
 <description><p>The LDAP Request Defaults component lets you set default values for LDAP testing.  See the <complink name="LDAP Request"/>.</p>
 </description>
 
 </component>
 
 <component name="LDAP Extended Request Defaults" index="&sect-num;.4.14"  width="686" height="184" screenshot="ldapext_defaults.png">
 <description><p>The LDAP Extended Request Defaults component lets you set default values for extended LDAP testing.  See the <complink name="LDAP Extended Request"/>.</p>
 </description>
 
 </component>
 
 <component name="TCP Sampler Config" index="&sect-num;.4.15"  width="826" height="450" screenshot="tcpsamplerconfig.png">
 <description>
         <p>
     The TCP Sampler Config provides default data for the TCP Sampler
     </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property <code>tcp.handler</code>, failing that <code>TCPClientImpl</code>.</property>
   <property name="ServerName or IP" required="">Name or IP of TCP server</property>
   <property name="Port Number" required="">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Close connection" required="Yes">If selected, the connection will be closed after running the sampler.</property>  
   <property name="SO_LINGER" required="No">Enable/disable <code>SO_LINGER</code> with the specified linger time in seconds when a socket is created. If you set "<code>SO_LINGER</code>" value as <code>0</code>, you may prevent large numbers of sockets sitting around with a <code>TIME_WAIT</code> status.</property>
   <property name="End of line(EOL) byte value" required="No">Byte value for end of line, set this to a value outside the range <code>-128</code> to <code>+127</code> to skip eol checking. You may set this in <code>jmeter.properties</code> file as well with the <code>tcp.eolByte</code> property. If you set this in TCP Sampler Config and in <code>jmeter.properties</code> file at the same time, the setting value in the TCP Sampler Config will be used.</property>
   <property name="Connect Timeout" required="No">Connect Timeout (milliseconds, 0 disables).</property>
   <property name="Response Timeout" required="No">Response Timeout (milliseconds, 0 disables).</property>
   <property name="Set Nodelay" required="">Should the nodelay property be set?</property>
   <property name="Text to Send" required="">Text to be sent</property>
 </properties>
 </component>
 
 <component name="User Defined Variables" index="&sect-num;.4.16"  width="741" height="266" screenshot="user_defined_variables.png">
 <description><p>The User Defined Variables element lets you define an <b>initial set of variables</b>, just as in the <complink name="Test Plan" />.
 <note>
 Note that all the UDV elements in a test plan - no matter where they are - are processed at the start.
 </note>
 So you cannot reference variables which are defined as part of a test run, e.g. in a Post-Processor.
 </p>
 <p>
 <b>
 UDVs should not be used with functions that generate different results each time they are called.
 Only the result of the first function call will be saved in the variable. 
 </b>
 However, UDVs can be used with functions such as <code>__P()</code>, for example:
 </p>
 <source>
 HOST      ${__P(host,localhost)} 
 </source>
 <p>
 which would define the variable "<code>HOST</code>" to have the value of the JMeter property "<code>host</code>", defaulting to "<code>localhost</code>" if not defined.
 </p>
 <p>
 For defining variables during a test run, see <complink name="User Parameters"/>.
 UDVs are processed in the order they appear in the Plan, from top to bottom.
 </p>
 <p>
 For simplicity, it is suggested that UDVs are placed only at the start of a Thread Group
 (or perhaps under the Test Plan itself).
 </p>
 <p>
 Once the Test Plan and all UDVs have been processed, the resulting set of variables is
 copied to each thread to provide the initial set of variables.
 </p>
 <p>
 If a runtime element such as a User Parameters Pre-Processor or Regular Expression Extractor defines a variable
 with the same name as one of the UDV variables, then this will replace the initial value, and all other test
 elements in the thread will see the updated value.
 </p>
 </description>
 <note>
 If you have more than one Thread Group, make sure you use different names for different values, as UDVs are shared between Thread Groups.
 Also, the variables are not available for use until after the element has been processed, 
 so you cannot reference variables that are defined in the same element.
 You can reference variables defined in earlier UDVs or on the Test Plan. 
 </note>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="User Defined Variables" required="">Variable name/value pairs. The string under the "<code>Name</code>"
       column is what you'll need to place inside the brackets in <code>${&hellip;}</code> constructs to use the variables later on. The
       whole <code>${&hellip;}</code> will then be replaced by the string in the "<code>Value</code>" column.</property>
 </properties>
 </component>
 
 <component name="Random Variable" index="&sect-num;.4.17"  width="495" height="286" screenshot="random_variable.png">
 <description>
 <p>
 The Random Variable Config Element is used to generate random numeric strings and store them in variable for use later.
 It's simpler than using <complink name="User Defined Variables"/> together with the <code>__Random()</code> function.
 </p>
 <p>
 The output variable is constructed by using the random number generator,
 and then the resulting number is formatted using the format string.
 The number is calculated using the formula <code>minimum+Random.nextInt(maximum-minimum+1)</code>.
 <code>Random.nextInt()</code> requires a positive integer.
 This means that <code>maximum-minimum</code> - i.e. the range - must be less than <code>2147483647</code>,
 however the <code>minimum</code> and <code>maximum</code> values can be any <code>long</code> values so long as the range is OK.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
   <property name="Variable Name" required="Yes">The name of the variable in which to store the random string.</property>
   <property name="Format String" required="No">The <code>java.text.DecimalFormat</code> format string to be used. 
   For example "<code>000</code>" which will generate numbers with at least 3 digits, 
   or "<code>USER_000</code>" which will generate output of the form <code>USER_nnn</code>. 
   If not specified, the default is to generate the number using <code>Long.toString()</code></property>
   <property name="Minimum Value" required="Yes">The minimum value (<code>long</code>) of the generated random number.</property>
   <property name="Maximum Value" required="Yes">The maximum value (<code>long</code>) of the generated random number.</property>
   <property name="Random Seed" required="No">The seed for the random number generator. Default is the current time in milliseconds. 
   If you use the same seed value with Per Thread set to <code>true</code>, you will get the same value for each Thread as per 
   <a href="http://docs.oracle.com/javase/7/docs/api/java/util/Random.html" >Random</a> class.
   </property>
   <property name="Per Thread(User)?" required="Yes">If <code>False</code>, the generator is shared between all threads in the thread group.
   If <code>True</code>, then each thread has its own random generator.</property>
 </properties>
 
 </component>
 
 <component name="Counter" index="&sect-num;.4.18"  width="404" height="262" screenshot="counter.png">
 <description><p>Allows the user to create a counter that can be referenced anywhere
 in the Thread Group.  The counter config lets the user configure a starting point, a maximum,
 and the increment.  The counter will loop from the start to the max, and then start over
 with the start, continuing on like that until the test is ended.  </p>
 <p>The counter uses a long to store the value, so the range is from <code>-2^63</code> to <code>2^63-1</code>.</p>
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Start" required="Yes">The starting number for the counter.  The counter will equal this
         number during the first iteration.</property>
         <property name="Increment" required="Yes">How much to increment the counter by after each
         iteration.</property>
         <property name="Maximum" required="No">If the counter exceeds the maximum, then it is reset to the <code>Start</code> value.
         Default is <code>Long.MAX_VALUE</code>
         </property>
         <property name="Format" required="No">Optional format, e.g. <code>000</code> will format as <code>001</code>, <code>002</code>, etc. 
         This is passed to <code>DecimalFormat</code>, so any valid formats can be used.
         If there is a problem interpreting the format, then it is ignored.
     [The default format is generated using <code>Long.toString()</code>]
         </property>
         <property name="Reference Name" required="Yes">This controls how you refer to this value in other elements.  Syntax is
         as in <a href="functions.html">user-defined values</a>: <code>$(reference_name}</code>.</property>
         <property name="Track Counter Independently for each User" required="No">In other words, is this a global counter, or does each user get their
         own counter?  If unchecked, the counter is global (i.e., user #1 will get value "<code>1</code>", and user #2 will get value "<code>2</code>" on
         the first iteration).  If checked, each user has an independent counter.</property>
         <property name="Reset counter on each Thread Group Iteration" required="No">This option is only available when counter is tracked per User, if checked, 
         counter will be reset to <code>Start</code> value on each Thread Group iteration. This can be useful when Counter is inside a Loop Controller.</property>
 </properties>
 </component>
 
 <component name="Simple Config Element" index="&sect-num;.4.19"  width="393" height="245" screenshot="simple_config_element.png">
 <description><p>The Simple Config Element lets you add or override arbitrary values in samplers.  You can choose the name of the value
 and the value itself.  Although some adventurous users might find a use for this element, it's here primarily for developers as a basic
 GUI that they can use while developing new JMeter components.</p>
 </description>
 
 <properties>
         <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree. </property>
   <property name="Parameter Name" required="Yes">The name of each parameter.  These values are internal to JMeter's workings and
   are not generally documented.  Only those familiar with the code will know these values.</property>
   <property name="Parameter Value" required="Yes">The value to apply to that parameter.</property>
 </properties>
 
 </component>
 
 
 <component name="MongoDB Source Config (DEPRECATED)" index="&sect-num;.4.20" 
                  width="1233" height="618" screenshot="mongodb-source-config.png">
     <description>Creates a MongoDB connection (used by <complink name="MongoDB Script"/>Sampler)
      from the supplied Connection settings. Each thread gets its own connection.
      The connection configuration name is used by the JDBC Sampler to select the appropriate
      connection.
      <p>
      You can then access <code>com.mongodb.DB</code> object in Beanshell or JSR223 Test Elements through the element <a href="../api/org/apache/jmeter/protocol/mongodb/config/MongoDBHolder.html">MongoDBHolder</a> 
      using this code</p>
      
     <source>
 import com.mongodb.DB;
 import org.apache.jmeter.protocol.mongodb.config.MongoDBHolder;
 DB db = MongoDBHolder.getDBFromSource("value of property MongoDB Source",
             "value of property Database Name");
 &hellip;
     </source>
     </description>
     <properties>
         <property name="Name" required="No">Descriptive name for the connection configuration that is shown in the tree.</property>
         <property name="Server Address List" required="Yes">Mongo DB Servers</property>
         <property name="MongoDB Source" required="Yes">The name of the variable the connection is tied to.  
         <note>Each name must be different. If there are two configuration elements using the same name, only one will be saved.</note>
         </property>
         
         <property name="Keep Trying" required="No">
             If <code>true</code>, the driver will keep trying to connect to the same server in case that the socket cannot be established.<br/>
             There is maximum amount of time to keep retrying, which is 15s by default.<br/>This can be useful to avoid some exceptions being thrown when a server is down temporarily by blocking the operations.
             <br/>It also can be useful to smooth the transition to a new master (so that a new master is elected within the retry time).<br/>
             <note>Note that when using this flag
               <ul>
                 <li>for a replica set, the driver will trying to connect to the old master for that time, instead of failing over to the new one right away </li>
                 <li>this does not prevent exception from being thrown in read/write operations on the socket, which must be handled by application.</li>
               </ul>
               Even if this flag is false, the driver already has mechanisms to automatically recreate broken connections and retry the read operations.
             </note>
             Default is <code>false</code>.
         </property>
         <property name="Maximum connections per host" required="No"></property>
         <property name="Connection timeout" required="No">
             The connection timeout in milliseconds.<br/>It is used solely when establishing a new connection <code>Socket.connect(java.net.SocketAddress, int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Maximum retry time" required="No">
             The maximum amount of time in milliseconds to spend retrying to open connection to the same server.<br/>Default is <code>0</code>, which means to use the default 15s if <code>autoConnectRetry</code> is on.
         </property>
         <property name="Maximum wait time" required="No">
             The maximum wait time in milliseconds that a thread may wait for a connection to become available.<br/>Default is <code>120,000</code>.
         </property>
         <property name="Socket timeout" required="No">
             The socket timeout in milliseconds It is used for I/O socket read and write operations <code>Socket.setSoTimeout(int)</code><br/>Default is <code>0</code> and means no timeout.
         </property>
         <property name="Socket keep alive" required="No">
             This flag controls the socket keep alive feature that keeps a connection alive through firewalls <code>Socket.setKeepAlive(boolean)</code><br/>
             Default is <code>false</code>.
         </property>
         <property name="ThreadsAllowedToBlockForConnectionMultiplier" required="No">
         This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that may be waiting for a connection to become available from the pool.<br/>
         All further threads will get an exception right away.<br/>
         For example if <code>connectionsPerHost</code> is <code>10</code> and <code>threadsAllowedToBlockForConnectionMultiplier</code> is <code>5</code>, then up to 50 threads can wait for a connection.<br/>
         Default is <code>5</code>.
         </property>
         <property name="Write Concern : Safe" required="No">
             If <code>true</code> the driver will use a <code>WriteConcern</code> of <code>WriteConcern.SAFE</code> for all operations.<br/>
             If <code>w</code>, <code>wtimeout</code>, <code>fsync</code> or <code>j</code> are specified, this setting is ignored.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Fsync" required="No">
             The <code>fsync</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for Journal" required="No">
             The <code>j</code> value of the global <code>WriteConcern</code>.<br/>
             Default is <code>false</code>.
         </property>
         <property name="Write Concern : Wait for servers" required="No">
             The <code>w</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Wait timeout" required="No">
             The <code>wtimeout</code> value of the global <code>WriteConcern</code>.<br/>Default is <code>0</code>.
         </property>
         <property name="Write Concern : Continue on error" required="No">
             If batch inserts should continue after the first error
         </property>
     </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.5 Assertions" anchor="assertions">
 <description>
     <p>
     Assertions are used to perform additional checks on samplers, and are processed after <b>every sampler</b>
     in the same scope.
     To ensure that an Assertion is applied only to a particular sampler, add it as a child of the sampler.
     </p>
     <note>
     Note: Unless documented otherwise, Assertions are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of BSF and BeanShell Assertions, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </note>
     <p>
     Assertions can be applied to either the main sample, the sub-samples or both. 
     The default is to apply the assertion to the main sample only.
     If the Assertion supports this option, then there will be an entry on the GUI which looks like the following:
     </p>
     <figure width="658" height="54" image="assertion/assertionscope.png">Assertion Scope</figure>
     or the following
     <figure width="841" height="55" image="assertion/assertionscopevar.png">Assertion Scope</figure>
     <p>
     If a sub-sampler fails and the main sample is successful,
     then the main sample will be set to failed status and an Assertion Result will be added.
     If the JMeter variable option is used, it is assumed to relate to the main sample, and
     any failure will be applied to the main sample only.
     </p>
     <note>
     The variable <code>JMeterThread.last_sample_ok</code> is updated to
     "<code>true</code>" or "<code>false</code>" after all assertions for a sampler have been run.
      </note>
 </description>
 <component name="Response Assertion" index="&sect-num;.5.1" anchor="basic_assertion"  width="921" height="423" screenshot="assertion/assertion.png">
 
 <description><p>The response assertion control panel lets you add pattern strings to be compared against various
     fields of the response.
     The pattern strings are:
     </p>
     <ul>
     <li><code>Contains</code>, <code>Matches</code>: Perl5-style regular expressions</li>
     <li><code>Equals</code>, <code>Substring</code>: plain text, case-sensitive</li>
     </ul>
     <p>
     A summary of the pattern matching characters can be found at <a href="http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html">ORO Perl5 regular expressions.</a>
     </p>
     <p>You can also choose whether the strings will be expected
 to <b>match</b> the entire response, or if the response is only expected to <b>contain</b> the
 pattern. You can attach multiple assertions to any controller for additional flexibility.</p>
 <p>Note that the pattern string should not include the enclosing delimiters, 
     i.e. use <code>Price: \d+</code> not <code>/Price: \d+/</code>.
     </p>
     <p>
     By default, the pattern is in multi-line mode, which means that the "<code>.</code>" meta-character does not match newline.
     In multi-line mode, "<code>^</code>" and "<code>$</code>" match the start or end of any line anywhere within the string 
     - not just the start and end of the entire string. Note that <code>\s</code> does match new-line.
     Case is also significant. To override these settings, one can use the <i>extended regular expression</i> syntax.
     For example:
 </p>
 <dl>
 <dt><code>(?i)</code></dt><dd>ignore case</dd>
 <dt><code>(?s)</code></dt><dd>treat target as single line, i.e. "<code>.</code>" matches new-line</dd>
 <dt><code>(?is)</code></dt><dd>both the above</dd>
 </dl>
 These can be used anywhere within the expression and remain in effect until overridden. E.g.
 <dl>
 <dt><code>(?i)apple(?-i) Pie</code></dt><dd>matches "<code>ApPLe Pie</code>", but not "<code>ApPLe pIe</code>"</dd>
 <dt><code>(?s)Apple.+?Pie</code></dt><dd>matches <code>Apple</code> followed by <code>Pie</code>, which may be on a subsequent line.</dd>
 <dt><code>Apple(?s).+?Pie</code></dt><dd>same as above, but it's probably clearer to use the <code>(?s)</code> at the start.</dd>
 </dl>
 
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
         <property name="Response Field to Test" required="Yes">Instructs JMeter which field of the Response to test.
         <ul>
         <li><code>Text Response</code> - the response text from the server, i.e. the body, excluding any HTTP headers.</li>
         <li><code>Document (text)</code> - the extract text from various type of documents via Apache Tika (see <complink name="View Results Tree"/> Document view section).</li>
         <li><code>URL sampled</code></li>
         <li><code>Response Code</code> - e.g. <code>200</code></li>
         <li><code>Response Message</code> - e.g. <code>OK</code></li>
         <li><code>Response Headers</code>, including Set-Cookie headers (if any)</li>
         </ul>
                 </property>
         <property name="Ignore status" required="Yes">Instructs JMeter to set the status to success initially. 
                 <p>
                 The overall success of the sample is determined by combining the result of the
                 assertion with the existing Response status.
                 When the <code>Ignore Status</code> checkbox is selected, the Response status is forced
                 to successful before evaluating the Assertion.
                 </p>
                 HTTP Responses with statuses in the <code>4xx</code> and <code>5xx</code> ranges are normally
                 regarded as unsuccessful. 
                 The "<code>Ignore status</code>" checkbox can be used to set the status successful before performing further checks.
                 Note that this will have the effect of clearing any previous assertion failures,
                 so make sure that this is only set on the first assertion.
         </property>
         <property name="Pattern Matching Rules" required="Yes">Indicates how the text being tested
         is checked against the pattern.
         <ul>
         <li><code>Contains</code> - true if the text contains the regular expression pattern</li>
         <li><code>Matches</code> - true if the whole text matches the regular expression pattern</li>
         <li><code>Equals</code> - true if the whole text equals the pattern string (case-sensitive)</li>
         <li><code>Substring</code> - true if the text contains the pattern string (case-sensitive)</li>
         </ul>
         <code>Equals</code> and <code>Substring</code> patterns are plain strings, not regular expressions.
         <code>NOT</code> may also be selected to invert the result of the check.</property>
         <property name="Patterns to Test" required="Yes">A list of patterns to
         be tested.  
         Each pattern is tested separately. 
         If a pattern fails, then further patterns are not checked.
         There is no difference between setting up
         one Assertion with multiple patterns and setting up multiple Assertions with one
         pattern each (assuming the other options are the same).
         <note>However, when the <code>Ignore Status</code> checkbox is selected, this has the effect of cancelling any
         previous assertion failures - so make sure that the <code>Ignore Status</code> checkbox is only used on
         the first Assertion.</note>
         </property>
 </properties>
 <p>
     The pattern is a Perl5-style regular expression, but without the enclosing brackets.
 </p>
 <example title="Assertion Examples" anchor="assertion_examples">
 <center>
 <figure image="assertion/example1a.png" width="242" height="123">Figure 14 - Test Plan</figure>
 <figure image="assertion/example1b.png" width="920" height="451">Figure 15 - Assertion Control Panel with Pattern</figure>
 <figure image="assertion/example1c-pass.png" width="801" height="230">Figure 16 - Assertion Listener Results (Pass)</figure>
 <figure image="assertion/example1c-fail.png" width="800" height="233">Figure 17 - Assertion Listener Results (Fail)</figure>
 </center>
 </example>
 
 
 </component>
 
 <component name="Duration Assertion" index="&sect-num;.5.2"  width="606" height="187" screenshot="duration_assertion.png">
 <description><p>The Duration Assertion tests that each response was received within a given amount
 of time.  Any response that takes longer than the given number of milliseconds (specified by the
 user) is marked as a failed response.</p></description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Duration in Milliseconds" required="Yes">The maximum number of milliseconds
         each response is allowed before being marked as failed.</property>
 </properties>
 </component>
 
 <component name="Size Assertion" index="&sect-num;.5.3"  width="732" height="358" screenshot="size_assertion.png">
 <description><p>The Size Assertion tests that each response contains the right number of bytes in it.  You can specify that
 the size be equal to, greater than, less than, or not equal to a given number of bytes.</p>
 <note>An empty response is treated as being 0 bytes rather than reported as an error.</note>
 </description>
 
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Apply to:" required="Yes">
         This is for use with samplers that can generate sub-samples, 
         e.g. HTTP Sampler with embedded resources, Mail Reader or samples generated by the Transaction Controller.
         <ul>
         <li><code>Main sample only</code> - assertion only applies to the main sample</li>
         <li><code>Sub-samples only</code> - assertion only applies to the sub-samples</li>
         <li><code>Main sample and sub-samples</code> - assertion applies to both.</li>
         <li><code>JMeter Variable</code> - assertion is to be applied to the contents of the named variable</li>
         </ul>
         </property>
         <property name="Size in bytes" required="Yes">The number of bytes to use in testing the size of the response (or value of the JMeter variable).</property>
         <property name="Type of Comparison" required="Yes">Whether to test that the response is equal to, greater than, less than,
         or not equal to, the number of bytes specified.</property>
 
 </properties>
 </component>
 
 <component name="XML Assertion" index="&sect-num;.5.4"  width="470" height="85" screenshot="xml_assertion.png">
 <description><p>The XML Assertion tests that the response data consists of a formally correct XML document.  It does not
 validate the XML based on a DTD or schema or do any further validation.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 
 </properties>
 </component>
 
 <component name="BeanShell Assertion" index="&sect-num;.5.5"  width="849" height="633" screenshot="beanshell_assertion.png">
 <description><p>The BeanShell Assertion allows the user to perform assertion checking using a BeanShell script.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p><p>
 Note that a different Interpreter is used for each independent occurrence of the assertion
 in each thread in a test script, but the same Interpreter is used for subsequent invocations.
 This means that variables persist across calls to the assertion.
 </p>
 <p>
 All Assertions are called from the same thread as the sampler.
 </p>
 <p>
 If the property "<code>beanshell.assertion.init</code>" is defined, it is passed to the Interpreter
 as the name of a sourced file. This can be used to define common methods and variables.
 There is a sample init file in the <code>bin</code> directory: <code>BeanShellAssertion.bshrc</code>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run. This overrides the script.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>There's a <a href="../demos/BeanShellAssertion.bsh">sample script</a> you can try.</p>
 <p>
 Before invoking the script, some variables are set up in the BeanShell interpreter.
 These are strings unless otherwise noted:
 </p>
 <ul>
   <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a> Object. (e.g.) <code>log.warn("Message"[,Throwable])</code></li>
   <li><code>SampleResult</code> - the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> Object; read-write</li>
   <li><code>Response</code> - the response Object; read-write</li>
   <li><code>Failure</code> - boolean; read-write; used to set the Assertion status</li>
   <li><code>FailureMessage</code> - String; read-write; used to set the Assertion message</li>
   <li><code>ResponseData</code> - the response body (byte [])</li>
   <li><code>ResponseCode</code> - e.g. <code>200</code></li>
   <li><code>ResponseMessage</code> - e.g. <code>OK</code></li>
   <li><code>ResponseHeaders</code> - contains the HTTP headers</li>
   <li><code>RequestHeaders</code> - contains the HTTP headers sent to the server</li>
   <li><code>SampleLabel</code></li>
   <li><code>SamplerData</code> - data that was sent to the server</li>
   <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
   <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
     <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.putObject("OBJ1",new Object());</source></li>
   <li><code>props</code> - JMeterProperties (class <code>java.util.Properties</code>) - e.g.
     <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 </ul>
 <p>The following methods of the Response object may be useful:</p>
 <ul>
     <li><code>setStopThread(boolean)</code></li>
     <li><code>setStopTest(boolean)</code></li>
     <li><code>String getSampleLabel()</code></li>
     <li><code>setSampleLabel(String)</code></li>
 </ul>
 </component>
 
 <component name="MD5Hex Assertion" index="&sect-num;.5.6" width="411" height="149" screenshot="assertion/MD5HexAssertion.png">
 <description><p>The MD5Hex Assertion allows the user to check the MD5 hash of the response data.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="MD5 sum" required="Yes">32 hex digits representing the MD5 hash (case not significant)</property>
 
 </properties>
 </component>
 
 <component name="HTML Assertion" index="&sect-num;.5.7"  width="464" height="384" screenshot="assertion/HTMLAssertion.png">
 <description><p>The HTML Assertion allows the user to check the HTML syntax of the response data using JTidy.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="doctype" required="Yes"><code>omit</code>, <code>auto</code>, <code>strict</code> or <code>loose</code></property>
 <property name="Format" required="Yes"><code>HTML</code>, <code>XHTML</code> or <code>XML</code></property>
 <property name="Errors only" required="Yes">Only take note of errors?</property>
 <property name="Error threshold" required="Yes">Number of errors allowed before classing the response as failed</property>
 <property name="Warning threshold" required="Yes">Number of warnings allowed before classing the response as failed</property>
 <property name="Filename" required="No">Name of file to which report is written</property>
 
 </properties>
 </component>
 <component name="XPath Assertion" index="&sect-num;.5.8"  width="800" height="317" screenshot="xpath_assertion.png">
 <description><p>The XPath Assertion tests a document for well formedness, has the option
 of validating against a DTD, or putting the document through JTidy and testing for an
 XPath.  If that XPath exists, the Assertion is true.  Using "<code>/</code>" will match any well-formed
 document, and is the default XPath Expression. 
 The assertion also supports boolean expressions, such as "<code>count(//*error)=2</code>".
 See <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a> for more information
 on XPath.
 </p>
 Some sample expressions:
 <ul>
 <li><code>//title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> anywhere in the response</li>
 <li><code>/title[text()='Text to match']</code> - matches <code>&lt;text&gt;Text to match&lt;/text&gt;</code> at root level in the response</li>
 </ul>
 </description>
 
 <properties>
 <property name="Name"        required="No">Descriptive name for this element that is shown in the tree.</property>
 <property name="Use Tidy (tolerant parser)"    required="Yes">Use Tidy, i.e. be tolerant of XML/HTML errors</property>
 <property name="Quiet"    required="If Tidy is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"    required="If Tidy is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"    required="If Tidy is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces"    required="If Tidy is not selected">Should namespaces be honoured?</property>
 <property name="Validate XML"    required="If Tidy is not selected">Check the document against its schema.</property>
 <property name="Ignore Whitespace"  required="If Tidy is not selected">Ignore Element Whitespace.</property>
 <property name="Fetch External DTDs"  required="If Tidy is not selected">If selected, external DTDs are fetched.</property>
 <property name="XPath Assertion"    required="Yes">XPath to match in the document.</property>
 <property name="True if nothing matches"    required="No">True if a XPath expression is not matched</property>
 </properties>
 <note>
 The non-tolerant parser can be quite slow, as it may need to download the DTD etc.
 </note>
 <note>
 As a work-round for namespace limitations of the Xalan XPath parser implementation on which JMeter is based,
 you can provide a Properties file which contains mappings for the namespace prefixes: 
 <source>
 prefix1=Full Namespace 1
 prefix2=Full Namespace 2
 &hellip;
 </source>
 
 You reference this file in <code>jmeter.properties</code> file using the property:
     <source>xpath.namespace.config</source>
 </note>
 </component>
 <component name="XML Schema Assertion" index="&sect-num;.5.9"  width="472" height="132" screenshot="assertion/XMLSchemaAssertion.png">
 <description><p>The XML Schema Assertion allows the user to validate a response against an XML Schema.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="File Name" required="Yes">Specify XML Schema File Name</property>
 </properties>
 </component>
 
 <component name="BSF Assertion" index="&sect-num;.5.10"  width="847" height="634" screenshot="bsf_assertion.png">
 <description>
 <p>
 The BSF Assertion allows BSF script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the <code>BSFEngine.exec()</code> method, which does not return a value.
 </p>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <code>java.util.Properties</code>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="JSR223 Assertion" index="&sect-num;.5.11">
 <description>
 <p>
 The JSR223 Assertion allows JSR223 script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The JSR223 language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Script compilation caching" required="No">Unique String across Test Plan that JMeter will use to cache result of Script compilation if language used supports <code>Compilable</code> interface (Groovy is one of these, java, beanshell and javascript are not)</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>Label</code> - the String Label</li>
 <li><code>Filename</code> - the script file name (if any)</li>
 <li><code>Parameters</code> - the parameters (as a String)</li>
 <li><code>args</code> - the parameters as a String array (split on whitespace)</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables:
 <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 vars.getObject("OBJ2");
 </source></li>
 <li><code>props</code> - (JMeterProperties - class <code>java.util.Properties</code>) - e.g.
 <source>
 props.get("START.HMS");
 props.put("PROP1","1234");
 </source></li>
 <li><code>SampleResult</code>, <code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous SampleResult (if any)</li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - gives access to the current sampler</li>
 <li><code>OUT</code> - <code>System.out</code> - e.g. <code>OUT.println("message")</code></li>
 <li><code>AssertionResult</code> - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>.
 If an error is detected, the script should use <code>AssertionResult.setFailureMessage("message")</code> and <code>AssertionResult.setFailure(true)</code>.
 </p>
 <p>For further details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Compare Assertion" index="&sect-num;.5.12"  width="292" height="296" screenshot="assertion/compare.png">
 <description>
 <note>
 Compare Assertion <b>must not be used</b> during load test as it consumes a lot of resources (memory and CPU). Use it only for either functional testing or 
 during Test Plan debugging and Validation.
 </note>
 
 The Compare Assertion can be used to compare sample results within its scope.
 Either the contents or the elapsed time can be compared, and the contents can be filtered before comparison.
 The assertion comparisons can be seen in the <complink name="Comparison Assertion Visualizer"/>.
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Compare Content" required="Yes">Whether or not to compare the content (response data)</property>
     <property name="Compare Time" required="Yes">If the value is &ge;0, then check if the response time difference is no greater than the value.
     I.e. if the value is <code>0</code>, then the response times must be exactly equal.</property>
     <property name="Comparison Filters" required="No">Filters can be used to remove strings from the content comparison.
     For example, if the page has a time-stamp, it might be matched with: "<code>Time: \d\d:\d\d:\d\d</code>" and replaced with a dummy fixed time "<code>Time: HH:MM:SS</code>".
     </property>
 </properties>
 </component>
 
 <component name="SMIME Assertion" index="&sect-num;.5.13"  width="471" height="428" screenshot="assertion/smime.png">
 <description>
 The SMIME Assertion can be used to evaluate the sample results from the Mail Reader Sampler.
 This assertion verifies if the body of a mime message is signed or not. The signature can also be verified against a specific signer certificate.
 As this is a functionality that is not necessarily needed by most users, additional jars need to be downloaded and added to <code>JMETER_HOME/lib</code>:<br></br> 
 <ul>
 <li><code>bcmail-xxx.jar</code> (BouncyCastle SMIME/CMS)</li>
 <li><code>bcprov-xxx.jar</code> (BouncyCastle Provider)</li>
 </ul>
 These need to be <a href="http://www.bouncycastle.org/latest_releases.html">downloaded from BouncyCastle.</a>
 <p>
 If using the <complink name="Mail Reader Sampler">Mail Reader Sampler</complink>, 
 please ensure that you select "<code>Store the message using MIME (raw)</code>" otherwise the Assertion won't be able to process the message correctly.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Verify Signature" required="Yes">If selected, the assertion will verify if it is a valid signature according to the parameters defined in the <code>Signer Certificate</code> box.</property>
     <property name="Message not signed" required="Yes">Whether or not to expect a signature in the message</property>
     <property name="Signer Cerificate" required="Yes">"<code>No Check</code>" means that it will not perform signature verification. "<code>Check values</code>" is used to verify the signature against the inputs provided. And "<code>Certificate file</code>" will perform the verification against a specific certificate file.</property>
     <property name="Message Position" required="Yes">
     The Mail sampler can retrieve multiple messages in a single sample.
     Use this field to specify which message will be checked.
     Messages are numbered from <code>0</code>, so <code>0</code> means the first message.
     Negative numbers count from the LAST message; <code>-1</code> means LAST, <code>-2</code> means penultimate etc.
     </property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.6 Timers" anchor="timers">
 <description>
     <br></br>
     <note>
     Note that timers are processed <b>before</b> each sampler in the scope in which they are found;
     if there are several timers in the same scope, <b>all</b> the timers will be processed <b>before
     each</b> sampler.
     <br></br>
     Timers are only processed in conjunction with a sampler.
     A timer which is not in the same scope as a sampler will not be processed at all.
     <br></br>
     To apply a timer to a single sampler, add the timer as a child element of the sampler.
     The timer will be applied before the sampler is executed.
     To apply a timer after a sampler, either add it to the next sampler, or add it as the
     child of a <complink name="Test Action"/> Sampler.
     </note>
 </description>
 <component name="Constant Timer" index="&sect-num;.6.1" anchor="constant" width="372" height="100" screenshot="timers/constant_timer.png">
 <description>
 <p>If you want to have each thread pause for the same amount of time between
 requests, use this timer.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Thread Delay" required="Yes">Number of milliseconds to pause.</property>
 </properties>
 </component>
 
 <component name="Gaussian Random Timer" index="&sect-num;.6.2" width="372" height="156" screenshot="timers/gauss_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals occurring near a particular value.  
 The total delay is the sum of the Gaussian distributed value (with mean <code>0.0</code> and standard deviation <code>1.0</code>) times
 the deviation value you specify, and the offset value.
 Another way to explain it, in Gaussian Random Timer, the variation around constant offset has a gaussian curve distribution. 
 
 </p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Deviation" required="Yes">Deviation in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Uniform Random Timer" index="&sect-num;.6.3" width="372" height="157" screenshot="timers/uniform_random_timer.png">
 
 <description><p>This timer pauses each thread request for a random amount of time, with
 each time interval having the same probability of occurring. The total delay
 is the sum of the random value and the offset value.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Random Delay Maximum" required="Yes">Maximum random number of milliseconds to
 pause.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Constant Throughput Timer" index="&sect-num;.6.4" width="636" height="146" screenshot="timers/constant_throughput_timer.png">
 
 <description><p>This timer introduces variable pauses, calculated to keep the total throughput (in terms of samples per minute) as close as possible to a give figure. Of course the throughput will be lower if the server is not capable of handling it, or if other timers or time-consuming test elements prevent it.</p>
 <p>
 N.B. although the Timer is called the Constant Throughput timer, the throughput value does not need to be constant.
 It can be defined in terms of a variable or function call, and the value can be changed during a test.
 The value can be changed in various ways:
 </p>
 <ul>
 <li>using a counter variable</li>
 <li>using a JavaScript or BeanShell function to provide a changing value</li>
 <li>using the remote BeanShell server to change a JMeter property</li>
 </ul>
 <p>See <a href="best-practices.html">Best Practices</a> for further details.
 <note>
 Note that the throughput value should not be changed too often during a test
 - it will take a while for the new value to take effect.
 </note>
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Target Throughput" required="Yes">Throughput we want the timer to try to generate.</property>
   <property name="Calculate Throughput based on" required="Yes">
    <ul>
     <li><code>this thread only</code> - each thread will try to maintain the target throughput. The overall throughput will be proportional to the number of active threads.</li>
     <li><code>all active threads in current thread group</code> - the target throughput is divided amongst all the active threads in the group. 
     Each thread will delay as needed, based on when it last ran.</li>
     <li><code>all active threads</code> - the target throughput is divided amongst all the active threads in all Thread Groups.
     Each thread will delay as needed, based on when it last ran.
     In this case, each other Thread Group will need a Constant Throughput timer with the same settings.</li>
     <li><code>all active threads in current thread group (shared)</code> - as above, but each thread is delayed based on when any thread in the group last ran.</li>
     <li><code>all active threads (shared)</code> - as above; each thread is delayed based on when any thread last ran.</li>
    </ul>
   </property>
   <p>The shared and non-shared algorithms both aim to generate the desired throughput, and will produce similar results.
   The shared algorithm should generate a more accurate overall transaction rate.
   The non-shared algorithm should generate a more even spread of transactions across threads.</p>
 </properties>
 </component>
 
 <component name="Synchronizing Timer" index="&sect-num;.6.5" width="415" height="125" screenshot="timers/sync_timer.png">
 
 <description>
 <p>
 The purpose of the SyncTimer is to block threads until X number of threads have been blocked, and
 then they are all released at once.  A SyncTimer can thus create large instant loads at various
 points of the test plan.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Number of Simultaneous Users to Group by" required="Yes">Number of threads to release at once. Setting it to <code>0</code> is equivalent to setting it to Number of threads in Thread Group.</property>
   <property name="Timeout in milliseconds" required="No">If set to <code>0</code>, Timer will wait for the number of threads to reach the value in "<code>Number of Simultaneous Users to Group</code>". If superior to <code>0</code>, then timer will wait at max "<code>Timeout in milliseconds</code>" for the number of Threads. If after the timeout interval the number of users waiting is not reached, timer will stop waiting. Defaults to <code>0</code></property>
 </properties>
 <note>
 If timeout in milliseconds is set to <code>0</code> and number of threads never reaches "<code>Number of Simultaneous Users to Group by</code>" then Test will pause infinitely.
 Only a forced stop will stop it. Setting Timeout in milliseconds is an option to consider in this case.
 </note>
 <note>
 Synchronizing timer blocks only within one JVM, so if using Distributed testing ensure you never set "<code>Number of Simultaneous Users to Group by</code>" to a value superior to the number of users
 of its containing Thread group considering 1 injector only.
 </note>
 
 </component>
 
 <component name="BeanShell Timer" index="&sect-num;.6.6"  width="846" height="636" screenshot="timers/beanshell_timer.png">
 <description>
 <p>
 The BeanShell Timer can be used to generate a delay.
 </p>
 <p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> methods.
 These should be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.
     The name is stored in the script variable <code>Label</code></property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>bsh.args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code>
      The return value is used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The BeanShell script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
 vars.putObject("OBJ1",new Object());
 </source></li>
 <li><code>props</code> - (JMeterProperties - class java.util.Properties) - e.g. <code>props.get("START.HMS");</code> <code>props.put("PROP1","1234");</code></li>
 <li><code>prev</code> - (<a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a>) - gives access to the previous <code>SampleResult</code> (if any)</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <code>beanshell.timer.init</code> is defined, this is used to load an initialisation file, which can be used to define methods etc. for use in the BeanShell script.</p>
 </component>
 
 
 <component name="BSF Timer" index="&sect-num;.6.7"  width="844" height="636" screenshot="timers/bsf_timer.png">
 <description>
 <p>
 The BSF Timer can be used to generate a delay using a BSF scripting language.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="ScriptLanguage" required="Yes">
         The scripting language to be used.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li><code>Parameters</code> - string containing the parameters as a single variable</li>
         <li><code>args</code> - String array containing parameters, split on white-space</li>
     </ul>
     </property>
     <property name="Script file" required="No">
     A file containing the script to run, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property
      The return value is converted to a long integer and used as the number of milliseconds to wait.
      </property>
     <property name="Script" required="Yes (unless script file is provided)">
         The script. The return value is used as the number of milliseconds to wait.
     </property>
 </properties>
 <p>Before invoking the script, some variables are set up in the script interpreter:</p>
 <ul>
 <li><code>log</code> - (<a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a>) - can be used to write to the log file</li>
 <li><code>ctx</code> - (<a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a>) - gives access to the context</li>
 <li><code>vars</code> - (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>) - gives read/write access to variables: <source>
 vars.get(key);
 vars.put(key,val);
