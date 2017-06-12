File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
Comment: TODO - not sure this is the best method
Initial commit id: 0f131cba
Final commit id: 75e6b13e
   Bugs between [       3]:
db972810f Bug 48300 - Allow override of IP source address for HTTP HttpClient requests
1987e3fdb Bug 47461 - Update Cache Manager to handle Expires HTTP header
22ef64ab6 Bug 47321 -  HTTPSampler2 response timeout not honored
   Bugs after [       6]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
c199d56a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Bugzilla Id: 55023
e554711a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Fixed regression on 51380 introduced by fix Bugzilla Id: 55023
752cde47f Bug 52371 - API Incompatibility - Methods in HTTPSampler2 now require PostMethod instead of HttpMethod[Base]. Reverted to original types.
3ccce7695 Bug 51380 - Control reuse of cached SSL Context from iteration to iteration

Start block index: 1100
End block index: 1111
    public boolean interrupt() {
        HttpClient client = savedClient;
        if (client != null) {
            savedClient = null;
            // TODO - not sure this is the best method
            final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
            if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
            }
        }
        return client != null;
    }

*********************** Method when SATD was removed **************************

    public boolean interrupt() {
        return hc.interrupt();
    }
