/140/report.java
Satd-method: protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
********************************************
********************************************
/140/Between/[Bug 52073 a7705d5d5_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res) 

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 44301  255f2d509_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 49374  9de8dfd38_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 50178  3afe57817_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 50684  592bf6b72_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 50686  11214d3a1_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 50686  2ec38fc0e_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 50943  8bc89ddd8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            
+            // For concurrent get resources
+            final ArrayList<ASyncSample> liste = new ArrayList<ASyncSample>();
+            
-                        HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
-                        res.addSubResult(binRes);
-                        res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                        
+                        if (isConcurrentDwn()) {
+                            // if concurrent download emb. resources, add to a list for async gets later
+                            liste.add(new ASyncSample(url, GET, false, frameDepth + 1));
+                        } else {
+                            // default: serial download embedded resources
+                            HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
+                            res.addSubResult(binRes);
+                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                        }
+
+            
+            // IF for download concurrent embedded resources
+            if (isConcurrentDwn()) {
+                int poolSize = CONCURRENT_POOL_SIZE; // init with default value
+                try {
+                    poolSize = Integer.parseInt(getConcurrentPool());
+                } catch (NumberFormatException nfe) {
+                    log.warn("Concurrent download resources selected, "// $NON-NLS-1$
+                            + "but pool size value is bad. Use default value");// $NON-NLS-1$
+                }
+                // Thread pool Executor to get resources 
+                // use a LinkedBlockingQueue, note: max pool size doesn't effect
+                final ThreadPoolExecutor exec = new ThreadPoolExecutor(
+                        poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
+                        new LinkedBlockingQueue<Runnable>());
+
+                try {
+                    // sample all resources with threadpool
+                    final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
+                    // call normal shutdown (wait ending all tasks)
+                    exec.shutdown();
+                    // put a timeout if tasks couldn't terminate
+                    exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
+
+                    // add result to main sampleResult
+                    for (Future<HTTPSampleResult> future : retExec) {
+                        final HTTPSampleResult binRes = future.get();
+                        res.addSubResult(binRes);
+                        res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                    }
+                } catch (InterruptedException ie) {
+                    log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
+                } catch (ExecutionException ee) {
+                    log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
+                }
+            }

Lines added: 51. Lines removed: 3. Tot = 54
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51268  0c9e1f5ac_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51380  3ccce7695_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51861  4b9cb415a_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51876  30860c40e_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51876  3dd627dcf_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51876  524e51555_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51876  6572ccd24_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51918  d62ae342a_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51919  279de7c33_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51925  e52390e12_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                boolean tasksCompleted = false;
+                    tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
+                } finally {
+                    if (!tasksCompleted) {
+                        exec.shutdownNow(); // kill any remaining tasks
+                    }

Lines added: 6. Lines removed: 0. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51939  344c9f27e_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51957  4ab21312f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        final HTTPSampleResult binRes = future.get();
-                        res.addSubResult(binRes);
-                        res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                        HTTPSampleResult binRes;
+                        try {
+                            binRes = future.get(1, TimeUnit.MILLISECONDS);
+                            res.addSubResult(binRes);
+                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
+                        } catch (TimeoutException e) {
+                            errorResult(e, res);
+                        }

Lines added: 8. Lines removed: 3. Tot = 11
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 51981  ec5d61329_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+                // TODO needed here because currently done on sample completion in JMeterThread,
+                // but that only catches top-level samples.
+                res.setThreadName(Thread.currentThread().getName());

Lines added: 3. Lines removed: 0. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 52137  42a20fb88_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 52221  ef3452255_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 52310  3d11fe9b9_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 52409  59553bf42_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 53039  caaf9e666_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 53039  f4f92dac0_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 53042  ed3fd9629_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 53145  de6a0a763_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 53765  472da1514_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 54129  e1c5c20a4_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 54525  297622a69_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 54778  ee7db54f9_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 55023  e554711a8_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 56772  74d599b35_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57107  60ee4df22_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57193: 591c1512b_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57193: ee0c987ff_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57577  1e1fceeb6_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57577  89d0fa45b_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57579  28c1ce150_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57606  74f9d98ee_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57613  6cbf639dd_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57696  302012293_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57696  74c6ad8b0_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 57696  b94669a7e_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 58137  bd765acb3_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 58137: 195fe4c25_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 58137: 3f62343c9_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 58705  3b7e03d0f_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59008  374063362_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59023  2bc066acb_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59023  fc21f0dd2_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59033  b93b3328d_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	+    private LinkExtractorParser getParser(HTTPSampleResult res) 
+    private LinkExtractorParser getParser(HTTPSampleResult res) 

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59034  f9cbd6162_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59249  a5d656bd5_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59382  09cce647f_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 59882  5f87f3092_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 60084  7ffb94bb3_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Bug 60423  0bf26f41b_diff.java
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
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	private LinkExtractorParser getParser(HTTPSampleResult res)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
/140/Between/Correct a  bfb0cd693_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            final ArrayList<ASyncSample> liste = new ArrayList<ASyncSample>();
+            final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
downloadPageResources(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* next
* isSuccessful
* getParser
* getMatcher
* addSubResult
* length
* getMessage
* hasNext
* matches
* getURL
* warn
* getResponseData
* setSuccessful
* equals
* toString
* getPattern
* getEmbeddedResourceURLs
—————————
Method found in diff:	public String toString() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
