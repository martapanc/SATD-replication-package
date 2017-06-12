/247/report.java
Satd-method: protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
********************************************
********************************************
/247/Between/Bug 28502  2526e684_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            // Save cache information
+            final CacheManager cacheManager = getCacheManager();
+            if (cacheManager != null){
+                cacheManager.saveDetails(httpMethod, res);
+            }
+

Lines added: 6. Lines removed: 0. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 39827  e5a3dda2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            setPostHeaders(httpMethod);
+            int content_len = setPostHeaders(httpMethod);
-            sendPostData(httpMethod);
+            sendPostData(httpMethod,content_len);

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 41416  f0592c5a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 44852  7179b7cc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        log.debug("Start : sample" + urlStr);
-        log.debug("method" + method);
+        log.debug("Start : sample " + urlStr);
-        res.setMonitor(isMonitor());
+        res.setMonitor(false);
-        res.setHTTPMethod(method);
+        res.setHTTPMethod(HTTPConstants.POST);
-            res.setQueryString(getQueryString());
-            sendPostData(httpMethod,content_len);
-
+            res.setQueryString(sendPostData(httpMethod,content_len));
+            res.setRequestHeaders(getConnectionHeaders(httpMethod));
-            if (httpMethod != null) {
-                httpMethod.releaseConnection();
-            }
+            httpMethod.releaseConnection();
-            if (httpMethod != null) {
-                httpMethod.releaseConnection();
-            }
+            httpMethod.releaseConnection();

Lines added: 7. Lines removed: 13. Tot = 20
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 48451  056429ba_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 48542  9c8cf8cf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(TRANSFER_ENCODING);
+                org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(HEADER_CONTENT_ENCODING);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 52115  1ccef3c2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 55161  697113b0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 57193: ee0c987f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 59038  fd8938f0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 60423  0bf26f41_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        res.setMonitor(false);

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 60564  81c34baf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Bug 60727  2651c6ff_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(
-    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
/247/Between/Use bytes, 6eb8a97a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
sample(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getResponseContentLength
* sampleEnd
* setEncodingAndType
* setRedirectLocation
* getResponseHeader
* setContentType
* write
* sampleStart
* read
* setQueryString
* setResponseCode
* setResponseHeaders
* setURL
* getURL
* setSuccessful
* setResponseMessage
* closeQuietly
* getStatusText
* getURI
* releaseConnection
* setHTTPMethod
* executeMethod
* isRedirect
* latencyEnd
* close
* setSampleLabel
* debug
* getResponseBodyAsStream
* getValue
* setResponseData
* setMonitor
* equals
* toByteArray
* toString
********************************************
********************************************
