File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
Comment: NON-NLS-1$ TODO should this be variable?
Initial commit id: 46069a43
Final commit id: 2651c6ff
   Bugs between [       9]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
81c34bafc Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
0bf26f41b Bug 60423 - Drop Monitor Results listener Part 1 Bugzilla Id: 60423
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
697113b02 Bug 55161 - Useless processing in SoapSampler.setPostHeaders Bugzilla Id: 55161
1ccef3c2d Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found
9c8cf8cfa Bug 48542 - SoapSampler uses wrong response header field to decide if response is gzip encoded
056429bae Bug 48451 - Error in: SoapSampler.setPostHeaders(PostMethod post) in the else branch
   Bugs after [       0]:


Start block index: 63
End block index: 63
    private static final String ENCODING = "utf-8"; //$NON-NLS-1$ TODO should this be variable?
