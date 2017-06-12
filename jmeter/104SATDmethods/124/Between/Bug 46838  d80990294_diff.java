diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index 46161dc24..9d8b592e7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -600,1012 +600,1015 @@ public abstract class HTTPSamplerBase extends AbstractSampler
 
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else {
             super.addTestElement(el);
         }
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
                 protocol.equalsIgnoreCase(PROTOCOL_HTTP)  ? DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(PROTOCOL_HTTPS) ? DEFAULT_HTTPS_PORT :
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
                 (PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == DEFAULT_HTTP_PORT) ||
                 (PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == DEFAULT_HTTPS_PORT)) {
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
             if (PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return DEFAULT_HTTPS_PORT;
             }
             if (!PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
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
             log.warn("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
             	log.debug("HeaderManager merged: " + value.getName());
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
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(new BooleanProperty(IMAGE_PARSER, parseImages));
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
      * Obtain a result that will help inform the user that an error has occured
      * during sampling, and how long it took to detect the error.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult
      * @return a sampling result useful to inform the user about the exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error");
         res.setDataType(HTTPSampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException
      */
     public URL getUrl() throws MalformedURLException {
         StringBuffer pathAndQuery = new StringBuffer(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         if (!path.startsWith("/")){ // $NON-NLS-1$
             pathAndQuery.append("/"); // $NON-NLS-1$
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(GET.equals(getMethod()) || DELETE.equals(getMethod())) {
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
             return new URL(getProtocol(), getDomain(), pathAndQuery.toString());
         }
         return new URL(getProtocol(), getDomain(), getPort(), pathAndQuery.toString());
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
          if(contentEncoding == null || contentEncoding.trim().length() == 0) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuffer buf = new StringBuffer();
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
      *            the query string
      * @param contentEncoding -
      *            the content encoding of the query string. The query string might
      *            actually be the post body of a http post request.
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         for (int i = 0; i < args.length; i++) {
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
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(contentEncoding != null) {
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
 
     public String toString() {
         try {
             StringBuffer stringBuffer = new StringBuffer();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if(POST.equals(getMethod()) || PUT.equals(getMethod())) {
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
             if (PROTOCOL_FILE.equalsIgnoreCase(getProtocol())){
                 res = fileSample(new URI(PROTOCOL_FILE,getPath(),null));
             } else {
                 res = sample(getUrl(), getMethod(), false, 0);
             }
             res.setSampleLabel(getName());
             return res;
         } catch (MalformedURLException e) {
             return errorResult(e, new HTTPSampleResult());
         } catch (IOException e) {
             return errorResult(e, new HTTPSampleResult());
         } catch (URISyntaxException e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     private HTTPSampleResult fileSample(URI uri) throws IOException {
 
         //String urlStr = uri.toString();
 
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
         res.setHTTPMethod(GET); // Dummy
         res.setURL(uri.toURL());
         res.setSampleLabel(uri.toString());
         FileInputStream fis = null;
         res.sampleStart();
         try {
             byte[] responseData;
             StringBuffer ctb=new StringBuffer("text/html"); // $NON-NLS-1$
             fis = new FileInputStream(getPath());
             String contentEncoding = getContentEncoding();
             if (contentEncoding.length() > 0) {
                 ctb.append("; charset="); // $NON-NLS-1$
                 ctb.append(contentEncoding);
             }
             responseData = IOUtils.toByteArray(fis);
             res.sampleEnd();
             res.setResponseData(responseData);
             res.setResponseCodeOK();
             res.setResponseMessageOK();
             res.setSuccessful(true);
             String ct = ctb.toString();
             res.setContentType(ct);
             res.setEncodingAndType(ct);
         } finally {
             IOUtils.closeQuietly(fis);
         }
 
         //res.setResponseHeaders("");
 
         return res;
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
      * @return results of the sampling
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      * <p>
      * If createContainerResult is true, the returned result will contain one
      * subsample for each request issued, including the original one that was
      * passed in. It will otherwise look exactly like that original one.
      * <p>
      * If createContainerResult is false, one subsample will be added to the
      * provided result for each requests issued.
      *
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator urls = null;
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
                     urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, res));
             res.setSuccessful(false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 res = new HTTPSampleResult(res);
             } else {
                 res = container;
             }
 
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
             while (urls.hasNext()) {
                 Object binURL = urls.next();
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc=encodeSpaces(urlstr);
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                 res.setSuccessful(false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                         res.addSubResult(binRes);
                         res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                     }
                 } catch (ClassCastException e) {
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                     res.setSuccessful(false);
                     continue;
                 }
             }
         }
         return res;
     }
 
     /*
      * @param res HTTPSampleResult to check
      * @return parser class name (may be "") or null if entry does not exist
      */
     private String getParserClass(HTTPSampleResult res) {
         final String ct = res.getMediaType();
         return (String)parsersForType.get(ct);
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestListener#testEnded()
      */
     public void testEnded() {
         dynamicPath = false;
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestListener#testEnded(java.lang.String)
      */
     public void testEnded(String host) {
         testEnded();
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestListener#testIterationStart(org.apache.jmeter.engine.event.LoopIterationEvent)
      */
     public void testIterationStart(LoopIterationEvent event) {
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestListener#testStarted()
      */
     public void testStarted() {
         JMeterProperty pathP = getProperty(PATH);
         log.debug("path property is a " + pathP.getClass().getName());
         log.debug("path beginning value = " + pathP.getStringValue());
         if (pathP instanceof StringProperty && pathP.getStringValue().length() > 0) {
             log.debug("Encoding spaces in path");
             pathP.setObjectValue(encodeSpaces(pathP.getStringValue()));
             dynamicPath = false;
         } else {
             log.debug("setting dynamic path to true");
             dynamicPath = true;
         }
         log.debug("path ending value = " + pathP.getStringValue());
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestListener#testStarted(java.lang.String)
      */
     public void testStarted(String host) {
         testStarted();
     }
 
     /*
      * (non-Javadoc)
      *
      * @see java.lang.Object#clone()
      */
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         base.dynamicPath = dynamicPath;
         base.fileList = null;
         return base;
     }
 
     /**
      * Iteratively download the redirect targets of a redirect response.
      * <p>
      * The returned result will contain one subsample for each request issued,
      * including the original one that was passed in. It will be an
      * HTTPSampleResult that should mostly look as if the final destination of
      * the redirect chain had been obtained in a single shot.
      *
      * @param res
      *            result of the initial request - must be a redirect response
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult followRedirects(HTTPSampleResult res, int frameDepth) {
         HTTPSampleResult totalRes = new HTTPSampleResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             String location = encodeSpaces(lastRes.getRedirectLocation());
             try {
                 lastRes = sample(new URL(lastRes.getURL(), location), GET, true, frameDepth);
             } catch (MalformedURLException e) {
                 lastRes = errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (int i = 0; i < subs.length; i++) {
                     totalRes.addSubResult(subs[i]);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if(!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), lastRes);
             totalRes.addSubResult(lastRes);
         }
 
         // Now populate the any totalRes fields that need to
         // come from lastRes:
         totalRes.setSampleLabel(totalRes.getSampleLabel() + "->" + lastRes.getSampleLabel());
         // The following three can be discussed: should they be from the
         // first request or from the final one? I chose to do it this way
         // because that's what browsers do: they show the final URL of the
         // redirect chain in the location field.
         totalRes.setURL(lastRes.getURL());
         totalRes.setHTTPMethod(lastRes.getHTTPMethod());
         totalRes.setQueryString(lastRes.getQueryString());
         totalRes.setRequestHeaders(lastRes.getRequestHeaders());
 
         totalRes.setResponseData(lastRes.getResponseData());
         totalRes.setResponseCode(lastRes.getResponseCode());
         totalRes.setSuccessful(lastRes.isSuccessful());
         totalRes.setResponseMessage(lastRes.getResponseMessage());
         totalRes.setDataType(lastRes.getDataType());
         totalRes.setResponseHeaders(lastRes.getResponseHeaders());
         totalRes.setContentType(lastRes.getContentType());
         totalRes.setDataEncoding(lastRes.getDataEncodingNoDefault());
         return totalRes;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect
      * @param frameDepth
      * @param res
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect) {
             if (res.isRedirect()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
 
                 if (getFollowRedirects()) {
                     res = followRedirects(res, frameDepth);
                     areFollowingRedirect = true;
                     wasRedirected = true;
                 }
             }
         }
         if (isImageParser() && (HTTPSampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), res));
             } else {
                 // If we followed redirects, we already have a container:
                 if(!areFollowingRedirect) {
                 HTTPSampleResult container = (HTTPSampleResult) (areFollowingRedirect ? res.getParent() : res);
 
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 if(!wasRedirected) {
                     res = downloadPageResources(res, container, frameDepth);
                 }
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code){
         return (code >= 200 && code <= 399);
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuffer newValue = new StringBuffer();
         for (int i = 0; i < value.length(); i++) {
             char charAt = value.charAt(i);
             if (charAt == '\\') { // $NON-NLS-1$
                 newValue.append("\\\\"); // $NON-NLS-1$
             } else {
                 newValue.append(charAt);
             }
         }
         return newValue.toString();
     }
 
     /*
      * Method to set files list to be uploaded.
      *
      * @param value
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void setHTTPFileArgs(HTTPFileArgs value) {
         setProperty(new TestElementProperty(FILE_ARGS, value));
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
     }
 
     /*
      * Method to clear the additional files list to be uploaded.
      *
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void clearHTTPFileArgs() {
         removeProperty(FILE_ARGS);
     }
 
     /**
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      *
      * If there are no valid file entries, then an empty list is returned.
      *
      * @return an array of file arguments (never null)
      */
     public HTTPFileArg[] getHTTPFiles() {
         if (fileList != null){
             return fileList;
         }
         HTTPFileArg[] outFiles;
         // Check for original data names
         // Use properties so variables and functions are not resolved too early
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg file = new HTTPFileArg(fileName, paramName, mimeType);
         if(file.isNotEmpty()) {
             // Now deal with any additional file arguments
             final HTTPFileArgs fileArgs = getHTTPFileArgs();
             if(fileArgs != null) {
                 outFiles = new HTTPFileArg[1+fileArgs.getHTTPFileArgCount()];
                 outFiles[0] = file; // first file
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (int i = 0; i < infiles.length; i++){
                     outFiles[i+1] = infiles[i];
                 }
             } else {
                 outFiles = new HTTPFileArg[]{file}; // just one file
             }
         } else {
             outFiles = new HTTPFileArg[]{}; // no files, empty array
         }
         fileList = outFiles; // update the list cache
         return fileList;
     }
 
     public int getHTTPFileCount(){
         return getHTTPFiles().length;
     }
     /**
      * Saves the list of files.
      * The first file is saved in the Filename/field/mimetype properties.
      * Any additional files are saved in the FILE_ARGS array.
      *
      * @param files list of files to save
      */
     public void setHTTPFiles(HTTPFileArg[] files) {
         clearHTTPFileArgs();
         fileList = null; // it will be regenerated by get
         // First weed out the empty files
         HTTPFileArg[] nonEmptyFile = {};
         int filesFound = 0;
         if (files.length > 0) {
             nonEmptyFile = new HTTPFileArg[files.length];
             for(int i=0; i < files.length; i++){
                 HTTPFileArg file = files[i];
                 if (file.isNotEmpty()){
                     nonEmptyFile[filesFound++] = file;
                 }
             }
         }
         // Any files left?
         if (filesFound > 0){
             HTTPFileArg file = nonEmptyFile[0];
             setFilenameProperty(file.getPath());
             setFileFieldProperty(file.getParamName());
             setMimetypeProperty(file.getMimeType());
             if (filesFound > 1){
                 HTTPFileArgs fileArgs = new HTTPFileArgs();
                 boolean empty=true;
                 for(int i=1; i < filesFound; i++){
                     final HTTPFileArg fileArg = nonEmptyFile[i];
                     if (fileArg.isNotEmpty()){
                         fileArgs.addHTTPFileArg(fileArg);
                         empty=false;
                     }
                 }
                 if (!empty){
                     setHTTPFileArgs(fileArgs);
                 }
             }
         } else {
             setFilenameProperty("");
             setFileFieldProperty("");
             setMimetypeProperty("");
         }
     }
 
     public static String[] getValidMethodsAsArray(){
         return (String[]) METHODLIST.toArray(new String[0]);
     }
 
     public static boolean isSecure(String protocol){
         return PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url){
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     public void threadStarted(){
     }
     public void threadFinished(){
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      *
      * For the MD5 case, the result byte count is set to the size of the original response.
      *
      * @param sampleResult
      * @param in input stream
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
 
         byte[] readBuffer = getThreadContext().getReadBuffer();
         int bufferSize=32;// Enough for MD5
 
         MessageDigest md=null;
         boolean asMD5 = useMD5();
         if (asMD5) {
             try {
                 md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
             } catch (NoSuchAlgorithmException e) {
                 log.error("Should not happen - could not find MD5 digest", e);
                 asMD5=false;
             }
         } else {
             if (length <= 0) {// may also happen if long value > int.max
                 bufferSize = 4 * 1024;
             } else {
                 bufferSize = length;
             }
         }
         ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
         int bytesRead = 0;
         int totalBytes = 0;
         boolean first = true;
         while ((bytesRead = in.read(readBuffer)) > -1) {
             if (first) {
                 sampleResult.latencyEnd();
                 first = false;
             }
             if (asMD5 && md != null) {
                 md.update(readBuffer, 0 , bytesRead);
                 totalBytes += bytesRead;
             } else {
                 w.write(readBuffer, 0, bytesRead);
             }
         }
+        if (first){ // Bug 46838 - if there was no data, still need to set latency
+            sampleResult.latencyEnd();
+        }
         in.close();
         w.flush();
         if (asMD5 && md != null) {
             byte[] md5Result = md.digest();
             w.write(JOrphanUtils.baToHexString(md5Result).getBytes());
             sampleResult.setBytes(totalBytes);
         }
         w.close();
         return w.toByteArray();
     }
 }
 
