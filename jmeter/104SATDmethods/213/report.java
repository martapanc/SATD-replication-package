File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
Comment: TODO - not sure this is the best method
Initial commit id: 803af3bd
Final commit id: 2651c6ff
   Bugs between [      25]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
0bf26f41b Bug 60423 - Drop Monitor Results listener Part 1 Bugzilla Id: 60423
caaf9e666 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Bugzilla Id: 53039
528484883 Bug 59079 - "httpsampler.max_redirects" property is not enforced when "Redirect Automatically" is used Bugzilla Id: 59079
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
13de0f65d Bug 57956 - The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin Rollback and fix differently. Bugzilla Id: 57956
6318068ef Bug 57956 - The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin Bugzilla Id: 57956
795c1a3d1 Bug 57995 - Use FileServer in HTTP Request files
ee7db54f9 Bug 54778 - HTTP Sampler should not return 204 when resource is found in Cache Bugzilla Id: 54778
05cccf1b4 Bug 54778 - HTTP Sampler should not return 204 when resource is found in Cache Factor out common code Bugzilla Id: 54778
9c53b7a16 Bug 55717 - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient31 Add property to control redirect handling See: http://mail-archives.apache.org/mod_mbox/jmeter-dev/201312.mbox/%3CCAOGo0VaYNmSw9wEA_jx8qb3g1NTAOHsF360aWGyevAsGXJ7D6Q%40mail.gmail.com%3E Bugzilla Id: 55717
61c1eed7a Bug 55717 - Bad handling of Redirect when URLs are in relative format by HttpClient4 and HttpClient31 Bugzilla Id: 55717
8075cd904 Bug 54482 - HC fails to follow redirects with non-encoded chars Oups take into account new exceptions Make error message more complete Bugzilla Id: 54482
d91a728ee Bug 54482 - HC fails to follow redirects with non-encoded chars Bugzilla Id: 54482
c199d56a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Bugzilla Id: 55023
78f927f9c Bug 55255 - Allow Body in HTTP DELETE method to support API that use it (like ElasticSearch) Bugzilla Id: 55255
fd31714f1 Bug 54482 - HC fails to follow redirects with non-encoded chars Apply fix to HTTPHC3Impl Factor out sanitize code in ConversionUtils Bugzilla Id: 54482
c8d0b33ac Bug 51882 - HTTPHC3Client uses a default retry count of 3, make it configurable
3ccce7695 Bug 51380 - Control reuse of cached SSL Context from iteration to iteration
9d9fc5b67 Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x) Simplify and improve last fix. Thanks sebb.
b3732e9fd Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x)
98a9ad03e Bug 50516 - "Host" header in HTTP Header Manager is not included in generated HTTP request
a75d1b6fe Change strategy to get response size (use CountingInputStream from Commons IO) View Results Tree - Add new size fields: response headers and response body (in bytes) - derived from Bug 43363 Size Assertion - Add response size scope (full, headers, body, code, message) - derived from Bug 43363
592bf6b72 Bug 50684 - Optionally disable Content-Type and Transfer-Encoding in Multipart POST
   Bugs after [       0]:


Start block index: 1154
End block index: 1165
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

(removed)

-    @Override
-    public boolean interrupt() {
-        HttpClient client = savedClient;
-        if (client != null) {
-            savedClient = null;
-            // TODO - not sure this is the best method
-            final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
-            if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
-                ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
-            }
-        }
-        return client != null;
-    }
