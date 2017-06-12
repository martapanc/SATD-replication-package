/141/report.java
Satd-method: protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
********************************************
********************************************
/141/Between/Bug 44301  255f2d509_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            res.setSuccessful(false);
+            setParentSampleSuccess(res, false);
-                                res.setSuccessful(false);
+                                setParentSampleSuccess(res, false);
-                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                            setParentSampleSuccess(res, res.isSuccessful() && binRes.isSuccessful());
-                    res.setSuccessful(false);
+                    setParentSampleSuccess(res, false);
-                            res.setSuccessful(res.isSuccessful() && binRes.getResult().isSuccessful());
+                            setParentSampleSuccess(res, res.isSuccessful() && binRes.getResult().isSuccessful());

Lines added: 5. Lines removed: 5. Tot = 10
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 49374  9de8dfd38_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
+                    urls = parser.getEmbeddedResourceURLs(responseData, res.getURL(), res.getDataEncodingWithDefault());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51861  4b9cb415a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51876  30860c40e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51876  3dd627dcf_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51876  524e51555_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51876  6572ccd24_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 51919  279de7c33_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();
-            
+            final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<Callable<AsynSamplerResultHolder>>();
+
-                            liste.add(new ASyncSample(url, GET, false, frameDepth + 1, this));
+                            liste.add(new ASyncSample(url, GET, false, frameDepth + 1, getCookieManager(), this));
-            
-                        new LinkedBlockingQueue<Runnable>());
+                        new LinkedBlockingQueue<Runnable>(),
+                        new ThreadFactory() {
+                            public Thread newThread(final Runnable r) {
+                                Thread t = new CleanerThread(new Runnable() {
+                                    public void run() {
+                                        try {
+                                            r.run();
+                                        } finally {
+                                            ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
+                                        }
+                                    }
+                                });
+                                return t;
+                            }
+                        });
-                    final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
+                    final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(liste);
-
+                    CookieManager cookieManager = getCookieManager();
-                    for (Future<HTTPSampleResult> future : retExec) {
-                        HTTPSampleResult binRes;
+                    for (Future<AsynSamplerResultHolder> future : retExec) {
+                        AsynSamplerResultHolder binRes;
-                            res.addSubResult(binRes);
-                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                            if(cookieManager != null) {
+                                CollectionProperty cookies = binRes.getCookies();
+                                PropertyIterator iter = cookies.iterator();
+                                while (iter.hasNext()) {
+                                    Cookie cookie = (Cookie) iter.next().getObjectValue();
+                                    cookieManager.add(cookie) ;
+                                }
+                            }
+                            res.addSubResult(binRes.getResult());
+                            res.setSuccessful(res.isSuccessful() && binRes.getResult().isSuccessful());

Lines added: 32. Lines removed: 11. Tot = 43
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 52137  42a20fb88_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 52221  ef3452255_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 52310  3d11fe9b9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 52409  59553bf42_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            res.addSubResult(errorResult(e, res));
+            res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
-                                res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
+                                res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
-                    res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
+                    res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));

Lines added: 3. Lines removed: 3. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 53042  ed3fd9629_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 53145  de6a0a763_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 53765  472da1514_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 54129  e1c5c20a4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 54778  ee7db54f9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                            setParentSampleSuccess(res, res.isSuccessful() && binRes.isSuccessful());
+                            setParentSampleSuccess(res, res.isSuccessful() && (binRes != null ? binRes.isSuccessful() : true));
-                            setParentSampleSuccess(res, res.isSuccessful() && binRes.getResult().isSuccessful());
+                            setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 55023  e554711a8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 56772  74d599b35_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    urls = parser.getEmbeddedResourceURLs(responseData, res.getURL(), res.getDataEncodingWithDefault());
+                    String userAgent = getUserAgent(res);
+                    urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());

Lines added: 2. Lines removed: 1. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/Between/Bug 57107  60ee4df22_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* getName
* isSuccessful
* getParser
* getMatcher
* get
* addSubResult
* awaitTermination
* shutdownNow
* add
* currentThread
* addRawSubResult
* invokeAll
* length
* getMessage
* hasNext
* matches
* parseInt
* getURL
* warn
* getResponseData
* setSuccessful
* setThreadName
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
* shutdown
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
