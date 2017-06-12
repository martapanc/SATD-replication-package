File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSampler2.java
Comment: TODO - what other headers are not allowed?
Initial commit id: 1dfa40fb
Final commit id: 75e6b13e
   Bugs between [      15]:
db972810f Bug 48300 - Allow override of IP source address for HTTP HttpClient requests
1987e3fdb Bug 47461 - Update Cache Manager to handle Expires HTTP header
22ef64ab6 Bug 47321 -  HTTPSampler2 response timeout not honored
006b977a0 Bug 44521 - empty variables for a POST in the HTTP Request dont get ignored
2526e684a Bug 28502 - HTTP Resource Cache - initial implementation
0431342fc Bug 19128 - Added multiple file POST support to HTTP Samplers
4f047a40b Bug 44852 SOAP/ XML-RPC Request does not show Request details in View Results Tree - give access to method
6ccc5cf06 Implement Bug 41921 for HTTP Samplers
7cb1d6daa Bug 42674 - default to pre-emptive authorisation if not specified
a8276cd51 Bug 42156 - HTTPRequest HTTPClient incorrectly urlencodes parameter value in POST
862840473 Bug 41518 - JMeter changes the HTTP header Content Type for POST request
3bf1a1ade Bug 27780 (patch 19792) - update POST handling Also updated PostWriterTest to include additional headers
ae48b9189 Bug 41928 - Make all request headers sent by HTTP Request sampler appear in sample result
90684a56d Bug 41705 - add content-encoding option to HTTP samplers
079bbb1e5 Bug 33964 - send file as entire post body if name & type are omitted
   Bugs after [       6]:
2651c6ffc Bug 60727 - Drop commons-httpclient-3.1 and related elements Contributed by UbikLoadPack support Bugzilla Id: 60727
fd8938f04 Bug 59038 - Deprecate HTTPClient 3.1 related elements Bugzilla Id: 59038
c199d56a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Bugzilla Id: 55023
e554711a8 Bug 55023 - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput Fixed regression on 51380 introduced by fix Bugzilla Id: 55023
752cde47f Bug 52371 - API Incompatibility - Methods in HTTPSampler2 now require PostMethod instead of HttpMethod[Base]. Reverted to original types.
3ccce7695 Bug 51380 - Control reuse of cached SSL Context from iteration to iteration

Start block index: 452
End block index: 478
	public String setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager) {
		StringBuffer hdrs = new StringBuffer(100);
		if (headerManager != null) {
			CollectionProperty headers = headerManager.getHeaders();
			if (headers != null) {
				PropertyIterator i = headers.iterator();
				while (i.hasNext()) {
					org.apache.jmeter.protocol.http.control.Header header 
                    = (org.apache.jmeter.protocol.http.control.Header) 
                       i.next().getObjectValue();
					String n = header.getName();
					// Don't allow override of Content-Length
					// This helps with SoapSampler hack too
					// TODO - what other headers are not allowed?
					if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
						String v = header.getValue();
						method.addRequestHeader(n, v);
						hdrs.append(n);
						hdrs.append(": "); // $NON-NLS-1$
						hdrs.append(v);
						hdrs.append("\n"); // $NON-NLS-1$
					}
				}
			}
		}
		return hdrs.toString();
	}
