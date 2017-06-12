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
