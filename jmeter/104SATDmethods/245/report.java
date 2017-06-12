File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/ParseRegexp.java
Comment: TODO: find a way to avoid the cost of creating a String here 
Initial commit id: b2b4794c
Final commit id: d095f26d
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 191
End block index: 317
    protected static SampleResult parseForImages(SampleResult res, HTTPSampler sampler)
    {
        URL baseUrl;

        String displayName = res.getSampleLabel();

        try
        {
            baseUrl = sampler.getUrl();
            if(log.isDebugEnabled())
            {
                log.debug("baseUrl - " + baseUrl.toString());
            }
        }
        catch(MalformedURLException mfue)
        {
            log.error("Error creating URL '" + displayName + "'");
            log.error("MalformedURLException - " + mfue);
            res.setResponseData(mfue.toString().getBytes());
            res.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
            res.setResponseMessage(HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
            res.setSuccessful(false);
            return res;
        }
        
        // This is used to ignore duplicated binary files.
        // Using a LinkedHashSet to avoid unnecessary overhead in iterating
        // the elements in the set later on. As a side-effect, this will keep
        // them roughly in order, which should be a better model of browser
        // behaviour.
        Set uniqueRLs = new LinkedHashSet();
        
        // Look for unique RLs to be sampled.
        Perl5Matcher matcher = (Perl5Matcher) localMatcher.get();
        PatternMatcherInput input = (PatternMatcherInput) localInput.get();
        // TODO: find a way to avoid the cost of creating a String here --
        // probably a new PatternMatcherInput working on a byte[] would do
        // better.
        input.setInput(new String(res.getResponseData()));
        while (matcher.contains(input, pattern)) {
            MatchResult match= matcher.getMatch();
            String s;
            if (log.isDebugEnabled()) log.debug("match groups "+match.groups());
            // Check for a BASE HREF:
            s= match.group(1);
            if (s!=null) {
                try {
                    baseUrl= new URL(baseUrl, s);
                    log.debug("new baseUrl from - "+s+" - " + baseUrl.toString());
                }
                catch(MalformedURLException mfue)
                {
                    log.error("Error creating base URL from BASE HREF '" + displayName + "'");
                    log.error("MalformedURLException - " + mfue);
                    res.setResponseData(mfue.toString().getBytes());
                    res.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                    res.setResponseMessage(HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                    res.setSuccessful(false);
                    return res;
                }
            }
            for (int g= 2; g < match.groups(); g++) {
                s= match.group(g);
                if (log.isDebugEnabled()) log.debug("group "+g+" - "+match.group(g));
                if (s!=null) uniqueRLs.add(s);
            }
        }

        // Iterate through the RLs and download each image:
        Iterator rls= uniqueRLs.iterator();
        while (rls.hasNext()) {
            String binUrlStr= (String)rls.next();
            SampleResult binRes = new SampleResult();
            
            // set the baseUrl and binUrl so that if error occurs
            // due to MalformedException then at least the values will be
            // visible to the user to aid correction
            binRes.setSampleLabel(baseUrl + "," + binUrlStr);

            URL binUrl;
            try {
                binUrl= new URL(baseUrl, binUrlStr);
            }
            catch(MalformedURLException mfue)
            {
                log.error("Error creating URL '" + baseUrl +
                          " , " + binUrlStr + "'");
                log.error("MalformedURLException - " + mfue);
                binRes.setResponseData(mfue.toString().getBytes());
                binRes.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                binRes.setResponseMessage(
                    HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                binRes.setSuccessful(false);
                res.addSubResult(binRes);
                break;
            }
            if(log.isDebugEnabled())
            {
                log.debug("Binary url - " + binUrlStr);
                log.debug("Full Binary url - " + binUrl);
            }
            binRes.setSampleLabel(binUrl.toString());
            try
            {
                HTTPSamplerFull.loadBinary(binUrl, binRes, sampler);
            }
            catch(Exception ioe)
            {
                log.error("Error reading from URL - " + ioe);
                binRes.setResponseData(ioe.toString().getBytes());
                binRes.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                binRes.setResponseMessage(
                    HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                binRes.setSuccessful(false);
            }
            log.debug("Adding result");
            res.addSubResult(binRes);
            res.setTime(res.getTime() + binRes.getTime());
        }

        // Okay, we're all done now
        if(log.isDebugEnabled())
        {
            log.debug("Total time - " + res.getTime());
        }
        return res;
    }
