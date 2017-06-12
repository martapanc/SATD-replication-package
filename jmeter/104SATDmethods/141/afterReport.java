/141/report.java
Satd-method: protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
********************************************
********************************************
/141/After/[Bug 52073 a7705d5d5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                
-                        String urlStrEnc=escapeIllegalURLCharacters(encodeSpaces(urlstr));
+                        String urlStrEnc = escapeIllegalURLCharacters(encodeSpaces(urlstr));
+
-                final String parentThreadName = Thread.currentThread().getName();
-                // Thread pool Executor to get resources 
-                // use a LinkedBlockingQueue, note: max pool size doesn't effect
-                final ThreadPoolExecutor exec = new ThreadPoolExecutor(
-                        maxConcurrentDownloads, maxConcurrentDownloads, KEEPALIVETIME, TimeUnit.SECONDS,
-                        new LinkedBlockingQueue<Runnable>(),
-                        new ThreadFactory() {
-                            @Override
-                            public Thread newThread(final Runnable r) {
-                                Thread t = new CleanerThread(new Runnable() {
-                                    @Override
-                                    public void run() {
-                                        try {
-                                            r.run();
-                                        } finally {
-                                            ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
-                                        }
-                                    }
-                                });
-                                t.setName(parentThreadName+"-ResDownload-" + t.getName()); //$NON-NLS-1$
-                                t.setDaemon(true);
-                                return t;
-                            }
-                        });
+                ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
-                boolean tasksCompleted = false;
-                    // sample all resources with threadpool
-                    final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(list);
-                    // call normal shutdown (wait ending all tasks)
-                    exec.shutdown();
-                    // put a timeout if tasks couldn't terminate
-                    exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
+                    // sample all resources
+                    final List<Future<AsynSamplerResultHolder>> retExec = resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
-                        AsynSamplerResultHolder binRes;
-                        try {
-                            binRes = future.get(1, TimeUnit.MILLISECONDS);
-                            if(cookieManager != null) {
-                                CollectionProperty cookies = binRes.getCookies();
-                                for (JMeterProperty jMeterProperty : cookies) {
-                                    Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
-                                    cookieManager.add(cookie);
-                                }
+                        // this call will not block as the futures return by invokeAllAndAwaitTermination 
+                        //   are either done or cancelled
+                        AsynSamplerResultHolder binRes = future.get();
+                        if(cookieManager != null) {
+                            CollectionProperty cookies = binRes.getCookies();
+                            for (JMeterProperty jMeterProperty : cookies) {
+                                Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
+                                cookieManager.add(cookie);
-                            res.addSubResult(binRes.getResult());
-                            setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
-                        } catch (TimeoutException e) {
-                            errorResult(e, res);
+                        res.addSubResult(binRes.getResult());
+                        setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
-                    tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
-                } finally {
-                    if (!tasksCompleted) {
-                        exec.shutdownNow(); // kill any remaining tasks
-                    }

Lines added: 15. Lines removed: 51. Tot = 66
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res) 

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    public void shutdown() {
+    public void shutdown() {
+        this.log.debug("Connection manager is shutting down");
+        try {
+            this.pool.shutdown();
+        } catch (final IOException ex) {
+            this.log.debug("I/O exception shutting down connection manager", ex);
+        }
+        this.log.debug("Connection manager shut down");
+    }

Lines added: 9. Lines removed: 0. Tot = 9
********************************************
********************************************
/141/After/Bug 53039  caaf9e666_diff.java
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
Method found in diff:	public Sampler next(){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSuccessful() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addSubResult(SampleResult subResult) {
-        setBytes(getBytes() + subResult.getBytes());
+        setBytes(getBytesAsLong() + subResult.getBytesAsLong());
-        setBodySize(getBodySize() + subResult.getBodySize());
+        setBodySize(getBodySizeAsLong() + subResult.getBodySizeAsLong());

Lines added: 2. Lines removed: 2. Tot = 4
—————————
Method found in diff:	public void add(final SampleResult res) {
-                            res.getBytes(),
+                            res.getBytesAsLong(),

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public void addRawSubResult(SampleResult subResult){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public URL getURL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSuccessful(boolean success) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 53039  f4f92dac0_diff.java
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 54525  297622a69_diff.java
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 54525  7266caa52_diff.java
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 56939  9eaec178d_diff.java
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 57193: 591c1512b_diff.java
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
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean equals(Object o) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 57193: ee0c987ff_diff.java
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
Method found in diff:	public URL next() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static final HTMLParser getParser() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Cookie get(int i) {// Only used by GUI

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean add(String s){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasNext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public URL getURL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString(){

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 57577  1e1fceeb6_diff.java
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
/141/After/Bug 57577  89d0fa45b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
-    <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources should only compute size or hash by default. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
+    <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>

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
/141/After/Bug 57579  28c1ce150_diff.java
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
/141/After/Bug 57606  74f9d98ee_diff.java
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
/141/After/Bug 57613  6cbf639dd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPConstantsInterface.java
+    String REPORT = "REPORT"; // $NON-NLS-1$
+    String MKCALENDAR = "MKCALENDAR"; // $NON-NLS-1$
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
+<li><bug>57613</bug>HTTP Sampler : Added CalDAV verbs (REPORT, MKCALENDAR). Contributed by Richard Brigham (richard.brigham at teamaol.com)</li>
+<li>Richard Brigham (richard.brigham at teamaol.com)</li>
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
-         <li>It does not support the following methods: COPY, LOCK, MKCOL, MOVE, PATCH, PROPFIND, PROPPATCH, UNLOCK.</li>
+         <li>It does not support the following methods: COPY, LOCK, MKCOL, MOVE, PATCH, PROPFIND, PROPPATCH, UNLOCK, REPORT, MKCALENDAR.</li>
-        PROPFIND, PROPPATCH, UNLOCK.</property>
+        PROPFIND, PROPPATCH, UNLOCK, REPORT, MKCALENDAR.</property>

Lines added: 9. Lines removed: 5. Tot = 14
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
/141/After/Bug 57696  302012293_diff.java
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
/141/After/Bug 57696  74c6ad8b0_diff.java
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
/141/After/Bug 57696  b94669a7e_diff.java
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
/141/After/Bug 58137  bd765acb3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        String urlStrEnc=encodeSpaces(urlstr);
+                        String urlStrEnc=escapeIllegalURLCharacters(encodeSpaces(urlstr));

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
/141/After/Bug 58137: 195fe4c25_diff.java
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
/141/After/Bug 58137: 3f62343c9_diff.java
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
/141/After/Bug 58705  3b7e03d0f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                                PropertyIterator iter = cookies.iterator();
-                                while (iter.hasNext()) {
-                                    Cookie cookie = (Cookie) iter.next().getObjectValue();
+                                for (JMeterProperty jMeterProperty : cookies) {
+                                    Cookie cookie = (Cookie) jMeterProperty.getObjectValue();

Lines added: 2. Lines removed: 3. Tot = 5
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
Method found in diff:	public Authorization get(int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void add(Cookie c) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 59008  374063362_diff.java
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
/141/After/Bug 59023  2bc066acb_diff.java
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
/141/After/Bug 59023  fc21f0dd2_diff.java
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
/141/After/Bug 59033  b93b3328d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                String parserName = getParserClass(res);
-                if(parserName != null)
-                {
-                    final HTMLParser parser =
-                        parserName.length() > 0 ? // we have a name
-                        HTMLParser.getParser(parserName)
-                        :
-                        HTMLParser.getParser(); // we don't; use the default parser
+                final LinkExtractorParser parser = getParser(res);
+                if(parser != null) {
-        } catch (HTMLParseException e) {
+        } catch (LinkExtractorParseException e) {

Lines added: 3. Lines removed: 9. Tot = 12
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
Method found in diff:	+    public static LinkExtractorParser getParser(String parserClassName) 
+    public static LinkExtractorParser getParser(String parserClassName) 

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 59034  f9cbd6162_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            String re=getEmbeddedUrlRE();
+            String re = getEmbeddedUrlRE();
-            final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<>();
+            final List<Callable<AsynSamplerResultHolder>> list = new ArrayList<>();
+            int maxConcurrentDownloads = CONCURRENT_POOL_SIZE; // init with default value
+            boolean isConcurrentDwn = isConcurrentDwn();
+            if(isConcurrentDwn) {
+                
+                try {
+                    maxConcurrentDownloads = Integer.parseInt(getConcurrentPool());
+                } catch (NumberFormatException nfe) {
+                    log.warn("Concurrent download resources selected, "// $NON-NLS-1$
+                            + "but pool size value is bad. Use default value");// $NON-NLS-1$
+                }
+                
+                // if the user choose a number of parallel downloads of 1
+                // no need to use another thread, do the sample on the current thread
+                if(maxConcurrentDownloads == 1) {
+                    log.warn("Number of parallel downloads set to 1, (sampler name="+getName()+")");
+                    isConcurrentDwn = false;
+                }
+            }
+            
-                        if (isConcurrentDwn()) {
+                        if (isConcurrentDwn) {
-                            liste.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
+                            list.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
+            
-            if (isConcurrentDwn()) {
-                int poolSize = CONCURRENT_POOL_SIZE; // init with default value
-                try {
-                    poolSize = Integer.parseInt(getConcurrentPool());
-                } catch (NumberFormatException nfe) {
-                    log.warn("Concurrent download resources selected, "// $NON-NLS-1$
-                            + "but pool size value is bad. Use default value");// $NON-NLS-1$
-                }
+            if (isConcurrentDwn && !list.isEmpty()) {
+
-                        poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
+                        maxConcurrentDownloads, maxConcurrentDownloads, KEEPALIVETIME, TimeUnit.SECONDS,
-                    final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(liste);
+                    final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(list);
-                                    cookieManager.add(cookie) ;
+                                    cookieManager.add(cookie);
-                    log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
+                    log.warn("Interrupted fetching embedded resources", ie); // $NON-NLS-1$

Lines added: 30. Lines removed: 16. Tot = 46
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
/141/After/Bug 59249  a5d656bd5_diff.java
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
/141/After/Bug 59382  09cce647f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
+    <li>Number of redirects followed by JMeter is now 20, it was previously 5. This can be changed with property <code>httpsampler.max_redirects</code>. See <bugzilla>59382</bugzilla></li>
+    <li><bug>59382</bug>More realistic default value for <code>httpsampler.max_redirects</code></li>

Lines added: 3. Lines removed: 1. Tot = 4
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
/141/After/Bug 59882  5f87f3092_diff.java
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
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String inthreadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean equals(Object o) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 60084  7ffb94bb3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        String urlstr = url.toString();
-                        String urlStrEnc = escapeIllegalURLCharacters(encodeSpaces(urlstr));
-                        if (!urlstr.equals(urlStrEnc)) {// There were some spaces in the URL
-                            try {
-                                url = new URL(urlStrEnc);
-                            } catch (MalformedURLException e) {
-                                res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
-                                setParentSampleSuccess(res, false);
-                                continue;
-                            }
+                        try {
+                            url = escapeIllegalURLCharacters(url);
+                        } catch (Exception e) {
+                            res.addSubResult(errorResult(new Exception(url.toString() + " is not a correct URI"), new HTTPSampleResult(res)));
+                            setParentSampleSuccess(res, false);
+                            continue;
-                        if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
+                        if (pattern != null && localMatcher != null && !localMatcher.matches(url.toString(), pattern)) {
-                            res.addSubResult(errorResult(new Exception(urlStrEnc + " URI can not be normalized", e), new HTTPSampleResult(res)));
+                            res.addSubResult(errorResult(new Exception(url.toString() + " URI can not be normalized", e), new HTTPSampleResult(res)));
-

Lines added: 8. Lines removed: 13. Tot = 21
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
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 60423  0bf26f41b_diff.java
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
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSuccessful() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addSubResult(SampleResult subResult) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addRawSubResult(SampleResult subResult){

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private int getMessage() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public URL getURL() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSuccessful(boolean success) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setThreadName(String threadName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/141/After/Bug 60564  81c34bafc_diff.java
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
Method found in diff:	public String getName() { // HC3.1 does not have the method

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private int getMessage() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
