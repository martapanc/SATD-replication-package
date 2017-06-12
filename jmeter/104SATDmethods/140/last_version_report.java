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
