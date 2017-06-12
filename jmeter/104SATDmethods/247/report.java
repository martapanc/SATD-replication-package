File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
Comment: May be replaced later
Initial commit id: f8f97220
Final commit id: 2651c6ff
   Bugs between [      14]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
0bf26f41b Bug 60423 - Drop Monitor Results listener Part 1 Bugzilla Id: 60423
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
697113b02 Bug 55161 - Useless processing in SoapSampler.setPostHeaders Bugzilla Id: 55161
1ccef3c2d Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found
9c8cf8cfa Bug 48542 - SoapSampler uses wrong response header field to decide if response is gzip encoded
056429bae Bug 48451 - Error in: SoapSampler.setPostHeaders(PostMethod post) in the else branch
2526e684a Bug 28502 - HTTP Resource Cache - initial implementation
7179b7ccb Bug 44852 SOAP/ XML-RPC Request does not show Request details in View Results Tree
e5a3dda2e Bug 39827 - set correct content length; and allow override if necessary
6eb8a97ac Use bytes, not characters! See Bug 39827
f0592c5a9 Bug 41416 - dont use chunked encoding for XML provided in the text box
   Bugs after [       0]:


Start block index: 198
End block index: 314
    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {

        String urlStr = url.toString();

        log.debug("Start : sample" + urlStr);
        log.debug("method" + method);

        PostMethod httpMethod;
        httpMethod = new PostMethod(urlStr);

        HTTPSampleResult res = new HTTPSampleResult();
        res.setMonitor(isMonitor());

        res.setSampleLabel(urlStr); // May be replaced later
        res.setHTTPMethod(method);
        res.sampleStart(); // Count the retries as well in the time
        HttpClient client = null;
        InputStream instream = null;
        try {
            setPostHeaders(httpMethod);
            client = setupConnection(url, httpMethod, res);

            res.setQueryString(getQueryString());
            sendPostData(httpMethod);

            int statusCode = client.executeMethod(httpMethod);

            // Request sent. Now get the response:
            instream = httpMethod.getResponseBodyAsStream();

            if (instream != null) {// will be null for HEAD

                org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(TRANSFER_ENCODING);
                if (responseHeader != null && ENCODING_GZIP.equals(responseHeader.getValue())) {
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

            String ct = null;
            org.apache.commons.httpclient.Header h
                    = httpMethod.getResponseHeader(HEADER_CONTENT_TYPE);
            if (h != null)// Can be missing, e.g. on redirect
            {
                ct = h.getValue();
                res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                res.setEncodingAndType(ct);
            }

            res.setResponseHeaders(getResponseHeaders(httpMethod));
            if (res.isRedirect()) {
                res.setRedirectLocation(httpMethod.getResponseHeader(HEADER_LOCATION).getValue());
            }

            // If we redirected automatically, the URL may have changed
            if (getAutoRedirects()) {
                res.setURL(new URL(httpMethod.getURI().toString()));
            }

            // Store any cookies received in the cookie manager:
            saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());

            // Follow redirects and download page resources if appropriate:
            res = resultProcessing(areFollowingRedirect, frameDepth, res);

            log.debug("End : sample");
            if (httpMethod != null)
                httpMethod.releaseConnection();
            return res;
        } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
        {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } catch (IOException e) {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } finally {
            JOrphanUtils.closeQuietly(instream);
            if (httpMethod != null)
                httpMethod.releaseConnection();
        }
    }
