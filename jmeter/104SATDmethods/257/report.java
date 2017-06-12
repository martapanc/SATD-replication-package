File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
Comment: TODO - is this the correct default?
Initial commit id: 803af3bd
Final commit id: 2651c6ff
   Bugs between [      25]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
0bf26f41b Bug 60423 - Drop Monitor Results listener Part 1 Bugzilla Id: 60423
caaf9e666 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Bugzilla Id: 53039
528484883 Bug 59079 - "httpsampler.max_redirects" property is not enforced when "Redirect Automatically" is used Bugzilla Id: 59079
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
13de0f65d Bug 57956 - The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin Rollback and fix differently. Bugzilla Id: 57956
6318068ef Bug 57956 - The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin Bugzilla Id: 57956
795c1a3d1 Bug 57995 - Use FileServer in HTTP Request files
ee7db54f9 Bug 54778 - HTTP Sampler should not return 204 when resource is found in Cache Bugzilla Id: 54778
05cccf1b4 Bug 54778 - HTTP Sampler should not return 204 when resource is found in Cache Factor out common code Bugzilla Id: 54778
9c53b7a16 Bug 55717 - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient31 Add property to control redirect handling See: http://mail-archives.apache.org/mod_mbox/jmeter-dev/201312.mbox/%3CCAOGo0VaYNmSw9wEA_jx8qb3g1NTAOHsF360aWGyevAsGXJ7D6Q%40mail.gmail.com%3E Bugzilla Id: 55717
61c1eed7a Bug 55717 - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient31 Bugzilla Id: 55717
8075cd904 Bug 54482 - HC fails to follow redirects with non-encoded chars Oups take into account new exceptions Make error message more complete Bugzilla Id: 54482
d91a728ee Bug 54482 - HC fails to follow redirects with non-encoded chars Bugzilla Id: 54482
c199d56a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Bugzilla Id: 55023
78f927f9c Bug 55255 - Allow Body in HTTP DELETE method to support API that use it (like ElasticSearch) Bugzilla Id: 55255
fd31714f1 Bug 54482 - HC fails to follow redirects with non-encoded chars Apply fix to HTTPHC3Impl Factor out sanitize code in ConversionUtils Bugzilla Id: 54482
c8d0b33ac Bug 51882 - HTTPHC3Client uses a default retry count of 3, make it configurable
3ccce7695 Bug 51380 - Control reuse of cached SSL Context from iteration to iteration
9d9fc5b67 Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x) Simplify and improve last fix. Thanks sebb.
b3732e9fd Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x)
98a9ad03e Bug 50516 - "Host" header in HTTP Header Manager is not included in generated HTTP request
a75d1b6fe Change strategy to get response size (use CountingInputStream from Commons IO) View Results Tree - Add new size fields: response headers and response body (in bytes) - derived from Bug 43363 Size Assertion - Add response size scope (full, headers, body, code, message) - derived from Bug 43363
592bf6b72 Bug 50684 - Optionally disable Content-Type and Transfer-Encoding in Multipart POST
   Bugs after [       0]:


Start block index: 264
End block index: 489
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
            if(contentEncoding != null && contentEncoding.length() == 0) {
                contentEncoding = null;
            }

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
               partlist.add(new StringPart(arg.getName(), arg.getValue(), contentEncoding));
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
            post.setRequestHeader(HEADER_CONTENT_TYPE, multiPartContentType);

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
            Header contentTypeHeader = post.getRequestHeader(HEADER_CONTENT_TYPE);
            boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
            // If there are no arguments, we can send a file as the body of the request
            // TODO: needs a multiple file upload scenerio
            if(!hasArguments() && getSendFileAsPostBody()) {
                // If getSendFileAsPostBody returned true, it's sure that file is not null
                HTTPFileArg file = files[0];
                if(!hasContentTypeHeader) {
                    // Allow the mimetype of the file to control the content type
                    if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                    }
                    else {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
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
                if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                    post.getParams().setContentCharset(contentEncoding);
                    haveContentEncoding = true;
                } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                    contentEncoding=null;
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
                            post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                        }
                        else {
                             // TODO - is this the correct default?
                            post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
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
                    StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                    post.setRequestEntity(requestEntity);
                }
                else {
                    // It is a normal post request, with parameter names and values

                    // Set the content type
                    if(!hasContentTypeHeader) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
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
        post.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));

        return postedBody.toString();
    }
