File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
Comment: TODO - belongs elsewhere
Initial commit id: 4b9cb415a
Final commit id: 0cba96d74
   Bugs between [      44]:
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
   Bugs after [       3]:
9eaec178d Bug 56939 - Parameters are not passed with OPTIONS HTTP Request Bugzilla Id: 56939
7266caa52 Bug 54525 Search Feature : Enhance it with ability to replace Bugzilla Id: 54525
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution

Start block index: 217
End block index: 271
    public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere

*********************** Method when SATD was removed **************************

    public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw";
