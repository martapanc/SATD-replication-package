File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
Comment: TODO - is this the correct default?
Initial commit id: c06370a0
Final commit id: 75e6b13e
   Bugs between [       8]:
db972810f Bug 48300 - Allow override of IP source address for HTTP HttpClient requests
1987e3fdb Bug 47461 - Update Cache Manager to handle Expires HTTP header
22ef64ab6 Bug 47321 -  HTTPSampler2 response timeout not honored
006b977a0 Bug 44521 - empty variables for a POST in the HTTP Request dont get ignored
2526e684a Bug 28502 - HTTP Resource Cache - initial implementation
0431342fc Bug 19128 - Added multiple file POST support to HTTP Samplers
4f047a40b Bug 44852 SOAP/ XML-RPC Request does not show Request details in View Results Tree - give access to method
6ccc5cf06 Implement Bug 41921 for HTTP Samplers
   Bugs after [       6]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
c199d56a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Bugzilla Id: 55023
e554711a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Fixed regression on 51380 introduced by fix Bugzilla Id: 55023
752cde47f Bug 52371 - API Incompatibility - Methods in HTTPSampler2 now require PostMethod instead of HttpMethod[Base]. Reverted to original types.
3ccce7695 Bug 51380 - Control reuse of cached SSL Context from iteration to iteration

Start block index: 253
End block index: 474
	private String sendPostData(PostMethod post) throws IOException {
        // Buffer to hold the post body, expect file content
        StringBuffer postedBody = new StringBuffer(1000);
        
        // Check if we should do a multipart/form-data or an
        // application/x-www-form-urlencoded post request
        if(getUseMultipartForPost()) {
            // If a content encoding is specified, we use that es the
            // encoding of any parameter values
            String contentEncoding = getContentEncoding();
            if(contentEncoding != null && contentEncoding.length() == 0) {
                contentEncoding = null;
            }
            
            // Check how many parts we need, one for each parameter and file
            int noParts = getArguments().getArgumentCount();
            if(hasUploadableFiles())
            {
                noParts++;
            }

            // Create the parts
            Part[] parts = new Part[noParts];
            int partNo = 0;
            // Add any parameters
            PropertyIterator args = getArguments().iterator();
            while (args.hasNext()) {
               HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
               String parameterName = arg.getName();
               if (parameterName.length()==0){
                   continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
               }
               parts[partNo++] = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
            }
            
            // Add any files
            if(hasUploadableFiles()) {
                File inputFile = new File(getFilename());
                // We do not know the char set of the file to be uploaded, so we set it to null
                ViewableFilePart filePart = new ViewableFilePart(getFileField(), inputFile, getMimetype(), null);
                filePart.setCharSet(null); // We do not know what the char set of the file is
                parts[partNo++] = filePart;
            }
            
            // Set the multipart for the post
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
                // We get the posted bytes as UTF-8, since java is using UTF-8
                postedBody.append(new String(bos.toByteArray() , "UTF-8")); // $NON-NLS-1$
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
            if(!hasArguments() && getSendFileAsPostBody()) {
                if(!hasContentTypeHeader) {
                    // Allow the mimetype of the file to control the content type
                    if(getMimetype() != null && getMimetype().length() > 0) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
                    }
                    else {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                    }
                }
                
                FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(getFilename()),null); 
                post.setRequestEntity(fileRequestEntity);
                
                // We just add placeholder text for file content
                postedBody.append("<actual file content, not shown here>");
            }
            else {
                // In a post request which is not multipart, we only support
                // parameters, no file upload is allowed

                // If a content encoding is specified, we set it as http parameter, so that
                // the post body will be encoded in the specified content encoding
                final String contentEncoding = getContentEncoding();
                boolean haveContentEncoding = false;
                if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                    post.getParams().setContentCharset(contentEncoding);
                    haveContentEncoding = true;
                }
                
                // If none of the arguments have a name specified, we
                // just send all the values as the post body
                if(getSendParameterValuesAsPostBody()) {
                    // Allow the mimetype of the file to control the content type
                    // This is not obvious in GUI if you are not uploading any files,
                    // but just sending the content of nameless parameters
                    if(!hasContentTypeHeader) {
                        if(getMimetype() != null && getMimetype().length() > 0) {
                            post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
                        }
                        else {
                        	 // TODO - is this the correct default?
                            post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                        }
                    }
                    
                    // Just append all the non-empty parameter values, and use that as the post body
                    StringBuffer postBody = new StringBuffer();
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
                    StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), post.getRequestCharSet());
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
                        if (parameterName.length()==0){
                            continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
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
                    // We get the posted bytes as UTF-8, since java is using UTF-8
                    postedBody.append(new String(bos.toByteArray() , "UTF-8")); // $NON-NLS-1$
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
