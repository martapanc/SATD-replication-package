File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
Comment: TODO needed here because currently done on sample completion in JMeterThread
Initial commit id: ec5d61329
Final commit id: 2b2038ae7
   Bugs between [      20]:
60ee4df22 Bug 57107 - Patch proposal: Add DAV verbs to HTTP Sampler Bugzilla Id: 57107
ee7db54f9 Bug 54778 - HTTP Sampler should not return 204 when resource is found in Cache Bugzilla Id: 54778
74d599b35 Bug 56772 - Handle IE Conditional comments when parsing embedded resources Commit missing class and handle null UA Bugzilla Id: 56772
e554711a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Fixed regression on 51380 introduced by fix Bugzilla Id: 55023
e1c5c20a4 Bug 54129 - Search Feature does not find text although existing in elements Bugzilla Id: 54129
472da1514 Bug 53765 - Switch to commons-lang3-3.1 Bugzilla Id: 53765
de6a0a763 Bug 53145 - HTTP Sampler - function in path evaluated too early
ed3fd9629 Bug 53042 - Introduce a new Interface to be implemented by AbstractSampler to allow Sampler to decide wether a config element applies to Sampler
255f2d509 Bug 44301 - Enable "ignore failed" for embedded resources
59553bf42 Bug 52409 - HttpSamplerBase#errorResult modifies sampleResult passed as parameter; fix code which assumes that a new instance is created (i.e. when adding a sub-sample)
9de8dfd38 Bug 49374 - Encoding of embedded element URLs depend on the file.encoding property Now using SampleResult#getDataEncodingWithDefault() to avoid relying on file.encoding of the JVM. Modified HTMLParserTestFile_2.xml to take into account the impact of encoding change.
3d11fe9b9 Bug 52310 - variable in IPSource failed HTTP request if "Concurrent Pool Size" is enabled Fix by making child get context of the parent.
ef3452255 Bug 52221 - Nullpointer Exception with use Retrieve Embedded Resource without HTTP Cache Manager
42a20fb88 Bug 52137 - Problems with HTTP Cache Manager
524e51555 Bug 51876 - Functionnality to search in Samplers TreeView Changed implementation to: - Add ability to search with regexp - Add ability to search in case sensitive and insentive modes - Plug additional search implementations
279de7c33 Bug 51919 - Random ConcurrentModificationException or NoSuchElementException in CookieManager#removeMatchingCookies when using Concurrent Download
4b9cb415a Bug 51861 - Improve HTTP Request GUI to better show parameters without name (GWT RPC requests for example)
6572ccd24 Bug 51876 - Functionnality to search in Samplers TreeView
30860c40e Bug 51876 - Functionnality to search in Samplers TreeView
3dd627dcf Bug 51876 - Functionnality to search in Samplers TreeView
   Bugs after [      31]:
9eaec178d Bug 56939 - Parameters are not passed with OPTIONS HTTP Request Bugzilla Id: 56939
7266caa52 Bug 54525 Search Feature : Enhance it with ability to replace Bugzilla Id: 54525
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
297622a69 Bug 54525 - Search Feature : Enhance it with ability to replace Implement feature for Sampler subclasses Bugzilla Id: 54525
0bf26f41b Bug 60423 - Drop Monitor Results listener Part 1 Bugzilla Id: 60423
f4f92dac0 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Fix bug as per Felix Schumacher review, thx Bugzilla Id: 53039
caaf9e666 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Bugzilla Id: 53039
7ffb94bb3 Bug 60084 - JMeter 3.0 embedded resource URL is silently encoded Bugzilla Id: 60084
5f87f3092 Bug 59882 - Reduce memory allocations for better throughput Based on PR 217 contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)
09cce647f Bug 59382 - More realistic default value for httpsampler.max_redirects Bugzilla Id: 59382
a5d656bd5 Bug 59249 - Http Request Defaults : Add "Source address" and "Save responses as MD5" Oups forgot 1 class Bugzilla Id: 59249
a7705d5d5 [Bug 52073] Embedded Resources Parallel download : Improve performances by avoiding shutdown of ThreadPoolExecutor at each sample Based on PR by Benoit Wiart + the addition (blame me) of JMeterPoolingClientConnectionManager  (see mailing list mail I will send) Bugzilla Id: 52073
b93b3328d Bug 59033 - Parallel Download : Rework Parser classes hierarchy to allow pluging parsers for different mime types Bugzilla Id: 59033
374063362 Bug 59008 - Fix Infinite recursion SampleResult on frame depth limit reached Bugzilla Id: 59008
f9cbd6162 Bug 59034 - Parallel downloads connection management is not realistic Bugzilla Id: 59034
2bc066acb Bug 59023 - HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6 Fix effectively the issue (thanks sebb for the note) Bugzilla Id: 59023
89d0fa45b Bug 57577 - HttpSampler : Retrieve All Embedded Resources should only compute size or hash by default Take into account sebb notes Bugzilla Id: 57577
fc21f0dd2 Bug 59023 - HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6 Bugzilla Id: 59023
1e1fceeb6 Bug 57577 - HttpSampler : Retrieve All Embedded Resources should only compute size or hash by default #resolve #127 Bugzilla Id: 57577
302012293 Bug 57696 HTTP Request : Improve responseMessage when resource download fails Bugzilla Id: 57696
3b7e03d0f Bug 58705 - Make org.apache.jmeter.testelement.property.MultiProperty iterable #resolve #48 Bugzilla Id: 58705
195fe4c25 Bug 58137: Warn about urls that had to be escaped. Bugzilla Id: 58137
3f62343c9 Bug 58137: Don't escape file protocol urls Bugzilla Id: 58137
bd765acb3 Bug 58137 - JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them) Bugzilla Id: 58137
74c6ad8b0 Bug 57696 - HTTP Request : Improve responseMessage when resource download fails Oups : Fix test failure Bugzilla Id: 57696
b94669a7e Bug 57696 - HTTP Request : Improve responseMessage when resource download fails Bugzilla Id: 57696
6cbf639dd Bug 57613 - HTTP Sampler : Added CalDAV verbs (REPORT, MKCALENDAR) Bugzilla Id: 57613
74f9d98ee Bug 57606 - HTTPSamplerBase#errorResult changes the sample label on exception Bugzilla Id: 57606
28c1ce150 Bug 57579 - NullPointerException error is raised on main sample if "RETURN_NO_SAMPLE" is used (default) and "Use Cache-Control / Expires header..." is checked in HTTP Cache Manager Bugzilla Id: 57579
591c1512b Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193

Start block index: 1051
End block index: 1204

protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
        Iterator<URL> urls = null;
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
                // TODO needed here because currently done on sample completion in JMeterThread,
                // but that only catches top-level samples.
                res.setThreadName(Thread.currentThread().getName());
                container = new HTTPSampleResult(res);
                container.addRawSubResult(res);
            }
            res = container;

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

            // For concurrent get resources
            final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();

            while (urls.hasNext()) {
                Object binURL = urls.next(); // See catch clause below
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

                        if (isConcurrentDwn()) {
                            // if concurrent download emb. resources, add to a list for async gets later
                            liste.add(new ASyncSample(url, GET, false, frameDepth + 1));
                        } else {
                            // default: serial download embedded resources
                            HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                            res.addSubResult(binRes);
                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                        }

                    }
                } catch (ClassCastException e) { // TODO can this happen?
                    res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                    res.setSuccessful(false);
                    continue;
                }
            }

            // IF for download concurrent embedded resources
            if (isConcurrentDwn()) {
                int poolSize = CONCURRENT_POOL_SIZE; // init with default value
                try {
                    poolSize = Integer.parseInt(getConcurrentPool());
                } catch (NumberFormatException nfe) {
                    log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                            + "but pool size value is bad. Use default value");// $NON-NLS-1$
                }
                // Thread pool Executor to get resources
                // use a LinkedBlockingQueue, note: max pool size doesn't effect
                final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                        poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>());

                boolean tasksCompleted = false;
                try {
                    // sample all resources with threadpool
                    final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
                    // call normal shutdown (wait ending all tasks)
                    exec.shutdown();
                    // put a timeout if tasks couldn't terminate
                    exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);

                    // add result to main sampleResult
                    for (Future<HTTPSampleResult> future : retExec) {
                        HTTPSampleResult binRes;
                        try {
                            binRes = future.get(1, TimeUnit.MILLISECONDS);
                            res.addSubResult(binRes);
                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                        } catch (TimeoutException e) {
                            errorResult(e, res);
                        }
                    }
                    tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
                } catch (InterruptedException ie) {
                    log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
                } catch (ExecutionException ee) {
                    log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                } finally {
                    if (!tasksCompleted) {
                        exec.shutdownNow(); // kill any remaining tasks
                    }
                }
            }
        }
        return res;
    }


*********************** Method when SATD was removed **************************

protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
    Iterator<URL> urls = null;
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
                String userAgent = getUserAgent(res);
                urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
            }
        }
    } catch (HTMLParseException e) {
        // Don't break the world just because this failed:
        res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
        setParentSampleSuccess(res, false);
    }

    // Iterate through the URLs and download each image:
    if (urls != null && urls.hasNext()) {
        if (container == null) {
            container = new HTTPSampleResult(res);
            container.addRawSubResult(res);
        }
        res = container;

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

        // For concurrent get resources
        final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<Callable<AsynSamplerResultHolder>>();

        while (urls.hasNext()) {
            Object binURL = urls.next(); // See catch clause below
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
                            res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
                            setParentSampleSuccess(res, false);
                            continue;
                        }
                    }
                    // I don't think localMatcher can be null here, but check just in case
                    if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                        continue; // we have a pattern and the URL does not match, so skip it
                    }

                    if (isConcurrentDwn()) {
                        // if concurrent download emb. resources, add to a list for async gets later
                        liste.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                    } else {
                        // default: serial download embedded resources
                        HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                        res.addSubResult(binRes);
                        setParentSampleSuccess(res, res.isSuccessful() && (binRes != null ? binRes.isSuccessful() : true));
                    }

                }
            } catch (ClassCastException e) { // TODO can this happen?
                res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                setParentSampleSuccess(res, false);
                continue;
            }
        }
        // IF for download concurrent embedded resources
        if (isConcurrentDwn()) {
            int poolSize = CONCURRENT_POOL_SIZE; // init with default value
            try {
                poolSize = Integer.parseInt(getConcurrentPool());
            } catch (NumberFormatException nfe) {
                log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                        + "but pool size value is bad. Use default value");// $NON-NLS-1$
            }
            // Thread pool Executor to get resources
            // use a LinkedBlockingQueue, note: max pool size doesn't effect
            final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                    poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new ThreadFactory() {
                        @Override
                        public Thread newThread(final Runnable r) {
                            Thread t = new CleanerThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        r.run();
                                    } finally {
                                        ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
                                    }
                                }
                            });
                            return t;
                        }
                    });

            boolean tasksCompleted = false;
            try {
                // sample all resources with threadpool
                final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(liste);
                // call normal shutdown (wait ending all tasks)
                exec.shutdown();
                // put a timeout if tasks couldn't terminate
                exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
                CookieManager cookieManager = getCookieManager();
                // add result to main sampleResult
                for (Future<AsynSamplerResultHolder> future : retExec) {
                    AsynSamplerResultHolder binRes;
                    try {
                        binRes = future.get(1, TimeUnit.MILLISECONDS);
                        if(cookieManager != null) {
                            CollectionProperty cookies = binRes.getCookies();
                            PropertyIterator iter = cookies.iterator();
                            while (iter.hasNext()) {
                                Cookie cookie = (Cookie) iter.next().getObjectValue();
                                cookieManager.add(cookie) ;
                            }
                        }
                        res.addSubResult(binRes.getResult());
                        setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                    } catch (TimeoutException e) {
                        errorResult(e, res);
                    }
                }
                tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
            } catch (InterruptedException ie) {
                log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
            } catch (ExecutionException ee) {
                log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
            } finally {
                if (!tasksCompleted) {
                    exec.shutdownNow(); // kill any remaining tasks
                }
            }
        }
    }
    return res;
}
