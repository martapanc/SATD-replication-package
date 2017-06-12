diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index cf12d8bd3..7eb174fc0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1069,1003 +1069,1015 @@ public abstract class HTTPSamplerBase extends AbstractSampler
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
 
             String metaData; // records the existence of an equal sign
             String name;
             String value;
             int length = arg.length();
             int endOfNameIndex = arg.indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = arg.substring(0, endOfNameIndex);
                 value = arg.substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name = arg;
                 value = "";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name + " Value: " + value + " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if (!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 } else {
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
             if (HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
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
             if (res != null) {
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
      * @param pRes
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(final HTTPSampleResult pRes, final HTTPSampleResult container, final int frameDepth) {
         HTTPSampleResult res = pRes;
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0) {  // Bug 39205
                 final LinkExtractorParser parser = getParser(res);
                 if (parser != null) {
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (LinkExtractorParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
         HTTPSampleResult lContainer = container;
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (lContainer == null) {
                 lContainer = new HTTPSampleResult(res);
                 lContainer.addRawSubResult(res);
             }
             res = lContainer;
 
             // Get the URL matcher
             String re = getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length() > 0) {
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) { // NOSONAR
                     log.warn("Ignoring embedded URL match string: " + e.getMessage());
                 }
             }
 
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> list = new ArrayList<>();
 
             int maxConcurrentDownloads = CONCURRENT_POOL_SIZE; // init with default value
             boolean isConcurrentDwn = isConcurrentDwn();
             if (isConcurrentDwn) {
                 try {
                     maxConcurrentDownloads = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
 
                 // if the user choose a number of parallel downloads of 1
                 // no need to use another thread, do the sample on the current thread
                 if (maxConcurrentDownloads == 1) {
                     log.warn("Number of parallel downloads set to 1, (sampler name=" + getName()+")");
                     isConcurrentDwn = false;
                 }
             }
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         try {
                             url = escapeIllegalURLCharacters(url);
                         } catch (Exception e) { // NOSONAR
                             res.addSubResult(errorResult(new Exception(url.toString() + " is not a correct URI", e), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(url.toString(), pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         try {
                             url = url.toURI().normalize().toURL();
                         } catch (MalformedURLException | URISyntaxException e) {
                             res.addSubResult(errorResult(new Exception(url.toString() + " URI can not be normalized", e), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
 
                         if (isConcurrentDwn) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             list.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes == null || binRes.isSuccessful()));
                         }
                     }
                 } catch (ClassCastException e) { // NOSONAR
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI", e), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                 }
             }
 
             // IF for download concurrent embedded resources
             if (isConcurrentDwn && !list.isEmpty()) {
 
                 ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
 
                 try {
                     // sample all resources
                     final List<Future<AsynSamplerResultHolder>> retExec =
                             resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
                         // this call will not block as the futures return by invokeAllAndAwaitTermination
                         //   are either done or cancelled
                         AsynSamplerResultHolder binRes = future.get();
                         if (cookieManager != null) {
                             CollectionProperty cookies = binRes.getCookies();
                             for (JMeterProperty jMeterProperty : cookies) {
                                 Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
                                 cookieManager.add(cookie);
                             }
                         }
                         res.addSubResult(binRes.getResult());
                         setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                     }
                 } catch (InterruptedException ie) {
                     log.warn("Interrupted fetching embedded resources", ie); // $NON-NLS-1$
                     Thread.currentThread().interrupt();
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 }
             }
         }
         return res;
     }
 
     /**
      * Gets parser from {@link HTTPSampleResult#getMediaType()}.
      * Returns null if no parser defined for it
      * @param res {@link HTTPSampleResult}
      * @return {@link LinkExtractorParser}
      * @throws LinkExtractorParseException
      */
     private LinkExtractorParser getParser(HTTPSampleResult res)
             throws LinkExtractorParseException {
         String parserClassName =
                 PARSERS_FOR_CONTENT_TYPE.get(res.getMediaType());
         if (!StringUtils.isEmpty(parserClassName)) {
             return BaseParser.getParser(parserClassName);
         }
         return null;
     }
     
     /**
      * @param url URL to escape
      * @return escaped url
      */
     private URL escapeIllegalURLCharacters(java.net.URL url) {
         if (url == null || "file".equals(url.getProtocol())) {
             return url;
         }
         try {
             return ConversionUtils.sanitizeUrl(url).toURL();
         } catch (Exception e1) { // NOSONAR
             log.error("Error escaping URL:'" + url + "', message:" + e1.getMessage());
             return url;
         }
     }
 
     /**
      * Extract User-Agent header value
      * @param sampleResult HTTPSampleResult
      * @return User Agent part
      */
     private String getUserAgent(HTTPSampleResult sampleResult) {
         String res = sampleResult.getRequestHeaders();
         int index = res.indexOf(USER_AGENT);
         if (index >= 0) {
             // see HTTPHC3Impl#getConnectionHeaders
             // see HTTPHC4Impl#getConnectionHeaders
             // see HTTPJavaImpl#getConnectionHeaders
             //': ' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
             final String userAgentPrefix = USER_AGENT+": ";
             String userAgentHdr = res.substring(
                     index+userAgentPrefix.length(),
                     res.indexOf('\n',// '\n' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
                             index+userAgentPrefix.length()+1));
             return userAgentHdr.trim();
         } else {
             if (log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:" + res);
             }
             return null;
         }
     }
 
     /**
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
         if (!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
             if (!initialValue) {
                 StringBuilder detailedMessage = new StringBuilder(80);
                 detailedMessage.append("Embedded resource download error:"); //$NON-NLS-1$
                 for (SampleResult subResult : res.getSubResults()) {
                     HTTPSampleResult httpSampleResult = (HTTPSampleResult) subResult;
                     if (!httpSampleResult.isSuccessful()) {
                         detailedMessage.append(httpSampleResult.getURL())
                                 .append(" code:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseCode())
                                 .append(" message:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseMessage())
                                 .append(", "); //$NON-NLS-1$
                     }
                 }
                 res.setResponseMessage(detailedMessage.toString()); //$NON-NLS-1$
             }
         }
     }
 
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         if (isConcurrentDwn()) {
             ResourcesDownloader.getInstance().shrink();
         }
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
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
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
         totalRes.addRawSubResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             String location = lastRes.getRedirectLocation();
             if (log.isDebugEnabled()) {
                 log.debug("Initial location: " + location);
             }
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             location = encodeSpaces(location);
             if (log.isDebugEnabled()) {
                 log.debug("Location after /. and space transforms: " + location);
             }
             // Change all but HEAD into GET (Bug 55450)
             String method = lastRes.getHTTPMethod();
             method = computeMethodForRedirect(method);
 
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 HTTPSampleResult tempRes = sample(url, method, true, frameDepth);
                 if (tempRes != null) {
                     lastRes = tempRes;
                 } else {
                     // Last url was in cache so tempRes is null
                     break;
                 }
             } catch (MalformedURLException | URISyntaxException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (SampleResult sub : subs) {
                     totalRes.addSubResult(sub);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if (!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeded maximum number of redirects: " + MAX_REDIRECTS), new HTTPSampleResult(lastRes));
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
      * See <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC2616#section-10.3</a>
      * JMeter conforms currently to HttpClient 4.5.2 supported RFC
      * TODO Update when migrating to HttpClient 5.X using response code
      * @param initialMethod the initial HTTP Method
      * @return the new HTTP Method as per RFC
      */
     private String computeMethodForRedirect(String initialMethod) {
         if (!HTTPConstants.HEAD.equalsIgnoreCase(initialMethod)) {
             return HTTPConstants.GET;
         }
         return initialMethod;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param pAreFollowingRedirect flag whether we are getting a redirect target
      * @param frameDepth Depth of this target in the frame structure. Used only to prevent infinite recursion.
      * @param pRes sample result to process
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(final boolean pAreFollowingRedirect, final int frameDepth, final HTTPSampleResult pRes) {
         boolean wasRedirected = false;
         boolean areFollowingRedirect = pAreFollowingRedirect;
         HTTPSampleResult res = pRes;
         if (!areFollowingRedirect && res.isRedirect()) {
             if(log.isDebugEnabled()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
             }
 
             if (getFollowRedirects()) {
                 res = followRedirects(res, frameDepth);
                 areFollowingRedirect = true;
                 wasRedirected = true;
             }
         }
         
         if (res.isSuccessful() && SampleResult.TEXT.equals(res.getDataType()) && isImageParser() ) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 HTTPSampleResult errSubResult = new HTTPSampleResult(res);
                 errSubResult.removeSubResults();
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), errSubResult));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 // otherwise, use null so the container is created if necessary unless
                 // the flag is false, in which case revert to broken 2.1 behaviour
                 // Bug 51939 -  https://bz.apache.org/bugzilla/show_bug.cgi?id=51939
                 if (!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (
                             areFollowingRedirect ? res.getParent() : SEPARATE_CONTAINER ? null : res);
                     res = downloadPageResources(res, container, frameDepth);
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @param code status code to check
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code) {
         return code >= 200 && code <= 399;
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuilder newValue = new StringBuilder();
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
         if (value.getHTTPFileArgCount() > 0) {
             setProperty(new TestElementProperty(FILE_ARGS, value));
         } else {
             removeProperty(FILE_ARGS); // no point saving an empty list
         }
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
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
         final HTTPFileArgs fileArgs = getHTTPFileArgs();
         return fileArgs == null ? new HTTPFileArg[] {} : fileArgs.asArray();
     }
 
     public int getHTTPFileCount() {
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
         HTTPFileArgs fileArgs = new HTTPFileArgs();
         // Weed out the empty files
         if (files.length > 0) {
             for (HTTPFileArg file : files) {
                 if (file.isNotEmpty()) {
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray() {
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol) {
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url) {
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         // NOOP to provide based empty impl and avoid breaking existing implementations
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the original response.
      * <p>
      * Closes the inputStream
      *
      * @param sampleResult sample to store information about the response into
      * @param in input stream from which to read the response
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException if reading the result fails
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, long length) throws IOException {
         
         OutputStream w = null;
         try { // NOSONAR No try with resource as performance is critical here
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize = 32;// Enough for MD5
 
             MessageDigest md = null;
             boolean knownResponseLength = length > 0;// may also happen if long value > int.max
             if (useMD5()) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
                 }
             } else {
                 if (!knownResponseLength) {
                     bufferSize = 4 * 1024;
                 } else {
                     bufferSize = (int) Math.min(MAX_BUFFER_SIZE, length);
                 }
             }
             
             
             int bytesReadInBuffer = 0;
             long totalBytes = 0;
             boolean first = true;
             boolean storeInBOS = true;
             while ((bytesReadInBuffer = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
                     if(md == null) {
                         if(!knownResponseLength) {
                             w = new org.apache.commons.io.output.ByteArrayOutputStream(bufferSize);
                         }
                         else {
                             w = new DirectAccessByteArrayOutputStream(bufferSize);
                         }
                     }
                 }
                 
                 if (md == null) {
                     if(storeInBOS) {
                         if(totalBytes+bytesReadInBuffer<=MAX_BYTES_TO_STORE_PER_REQUEST) {
                             w.write(readBuffer, 0, bytesReadInBuffer);
                         } else {
                             w.write(readBuffer, 0, (int)(MAX_BYTES_TO_STORE_PER_REQUEST-totalBytes));
                             storeInBOS = false;
                         }
                     }
                 } else {
                     md.update(readBuffer, 0, bytesReadInBuffer);
                 }
                 totalBytes += bytesReadInBuffer;
             }
             
             if (first) { // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
                 return new byte[0];
             }
             
             if (md == null) {
                 return toByteArray(w);
             } else {
                 byte[] md5Result = md.digest();
                 sampleResult.setBytes(totalBytes);
                 return JOrphanUtils.baToHexBytes(md5Result);                
             }
             
         } finally {
             IOUtils.closeQuietly(in);
             IOUtils.closeQuietly(w);
         }
     }
 
     /**
      * Optimized method to get byte array from {@link OutputStream}
      * @param w {@link OutputStream}
      * @return byte array
      */
     private byte[] toByteArray(OutputStream w) {
         if(w instanceof DirectAccessByteArrayOutputStream) {
             return ((DirectAccessByteArrayOutputStream) w).toByteArray();
         }
         
         if(w instanceof org.apache.commons.io.output.ByteArrayOutputStream) {
             return ((org.apache.commons.io.output.ByteArrayOutputStream) w).toByteArray();
         }
         
         log.warn("Unknown stream type " + w.getClass());
         
         return null;
     }
 
     /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
      * <ul>
      *   <li>FILE_NAME</li>
      *   <li>FILE_FIELD</li>
      *   <li>MIMETYPE</li>
      * </ul>
      * These were stored in their own individual properties.
      * <p>
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      * <p>
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      * <p>
      * Versions after 2.3.4 dispense with the original set of 3 properties.
      * Test plans that use them are converted to use a single HTTPFileArgs list.
      *
      * @see HTTPSamplerBaseConverter
      */
     void mergeFileProperties() {
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg oldStyleFile = new HTTPFileArg(fileName, paramName, mimeType);
 
         HTTPFileArgs fileArgs = getHTTPFileArgs();
 
         HTTPFileArgs allFileArgs = new HTTPFileArgs();
         if (oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if (fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (HTTPFileArg infile : infiles) {
                     allFileArgs.addHTTPFileArg(infile);
                 }
             }
         } else {
             if (fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
                 allFileArgs = fileArgs;
             }
         }
         // Updated the property lists
         setHTTPFileArgs(allFileArgs);
         removeProperty(FILE_FIELD);
         removeProperty(FILE_NAME);
         removeProperty(MIMETYPE);
     }
 
     /**
      * set IP source to use - does not apply to Java HTTP implementation currently
      *
      * @param value IP source to use
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      *
      * @return IP source to use
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE, "");
     }
 
     /**
      * set IP/address source type to use
      *
      * @param value type of the IP/address source
      */
     public void setIpSourceType(int value) {
         setProperty(IP_SOURCE_TYPE, value, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * get IP/address source type to use
      *
      * @return address source type
      */
     public int getIpSourceType() {
         return getPropertyAsInt(IP_SOURCE_TYPE, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * Return if used a concurrent thread pool to get embedded resources.
      *
      * @return true if used
      */
     public boolean isConcurrentDwn() {
         return getPropertyAsBoolean(CONCURRENT_DWN, false);
     }
 
     public void setConcurrentDwn(boolean concurrentDwn) {
         setProperty(CONCURRENT_DWN, concurrentDwn, false);
     }
 
     /**
      * Get the pool size for concurrent thread pool to get embedded resources.
      *
      * @return the pool size
      */
     public String getConcurrentPool() {
         return getPropertyAsString(CONCURRENT_POOL, CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
 
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         private final URL url;
         private final String method;
         private final boolean areFollowingRedirect;
         private final int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base) {
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
                 this.sampler.setCacheManagerProperty(cacheManager.createCacheManagerProxy());
             }
 
             if (cookieManager != null) {
                 CookieManager clonedCookieManager = (CookieManager) cookieManager.clone();
                 this.sampler.setCookieManagerProperty(clonedCookieManager);
             }
             this.sampler.setMD5(this.sampler.useMD5() || IGNORE_EMBEDDED_RESOURCES_DATA);
             this.jmeterContextOfParentThread = JMeterContextService.getContext();
         }
 
         @Override
         public AsynSamplerResultHolder call() {
             JMeterContextService.replaceContext(jmeterContextOfParentThread);
             HTTPSampleResult httpSampleResult = sampler.sample(url, method, areFollowingRedirect, depth);
             if (sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
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
 
     /**
      * Replace by replaceBy in path and body (arguments) properties
      */
     @Override
     public int replace(String regex, String replaceBy, boolean caseSensitive) throws Exception {
         int totalReplaced = 0;
         for (JMeterProperty jMeterProperty : getArguments()) {
             HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
             String value = arg.getValue();
             if(!StringUtils.isEmpty(value)) {
                 Object[] result = JOrphanUtils.replaceAllWithRegex(value, regex, replaceBy, caseSensitive);
                 // check if there is anything to replace
                 int nbReplaced = ((Integer)result[1]).intValue();
                 if (nbReplaced>0) {
                     String replacedText = (String) result[0];
                     arg.setValue(replacedText);
                     totalReplaced += nbReplaced;
                 }
             }
         }
         String value = getPath();
         if(!StringUtils.isEmpty(value)) {
             Object[] result = JOrphanUtils.replaceAllWithRegex(value, regex, replaceBy, caseSensitive);
             // check if there is anything to replace
             int nbReplaced = ((Integer)result[1]).intValue();
             if (nbReplaced>0) {
                 totalReplaced += nbReplaced;
                 String replacedText = (String) result[0];
                 setPath(replacedText);
                 totalReplaced += nbReplaced;
             }
         }
+
+        if(!StringUtils.isEmpty(getDomain())) {
+            Object[] result = JOrphanUtils.replaceAllWithRegex(getDomain(), regex, replaceBy, caseSensitive);            
+            // check if there is anything to replace
+            int nbReplaced = ((Integer)result[1]).intValue();
+            if (nbReplaced>0) {
+                totalReplaced += nbReplaced;
+                String replacedText = (String) result[0];
+                setDomain(replacedText);
+                totalReplaced += nbReplaced;
+            }
+        }
         return totalReplaced;
     }
 }
