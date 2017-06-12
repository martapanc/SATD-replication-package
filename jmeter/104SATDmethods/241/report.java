File path: src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
Comment: O: Exact ordering is only required for some tests
Initial commit id: 76159a5b
Final commit id: c87bbdb4
   Bugs between [       0]:

   Bugs after [      14]:
1e4a1ca55 Bug 60842 - jmeter chokes on newline Optimize by using static Pattern Factor our code in base class Use code in JSoup based implementation Add Junit tests for it Bugzilla Id: 60842
3ff0e6095 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
b93b3328d Bug 59033 - Parallel Download : Rework Parser classes hierarchy to allow pluging parsers for different mime types Bugzilla Id: 59033
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
0d45f17f0 Bug 57193: Escape entities like &, < and >, as they are not  allowed in javadocs. Place code tag around code-fragments in javadoc. Bugzilla Id: 57193
74d599b35 Bug 56772 - Handle IE Conditional comments when parsing embedded resources Commit missing class and handle null UA Bugzilla Id: 56772
be4d1fe65 Bug 56772 - Handle IE Conditional comments when parsing embedded resources Bugzilla Id: 56772
15ca0337b Bug 55632 - Have a new implementation of htmlParser for embedded resources parsing with better performances Switch default to Lagarto Parser implementation Bugzilla Id: 55632
6ee224f07 Bug 55632 - Have a new implementation of htmlParser for embedded resources parsing with better performances Rollback default for now Comment on performances Bugzilla Id: 55632
99b941c59 Bug 55632 - Have a new implementation of htmlParser for embedded resources parsing with better performances Bugzilla Id: 55632
b8a912a15 Bug 54629 - HTMLParser does not extract <object> tag urls Bugzilla Id: 54629
9de8dfd38 Bug 49374 - Encoding of embedded element URLs depend on the file.encoding property Now using SampleResult#getDataEncodingWithDefault() to avoid relying on file.encoding of the JVM. Modified HTMLParserTestFile_2.xml to take into account the impact of encoding change.
f2955e7ac Bug 51750 - Retrieve all embedded resources doesn't follow IFRAME
0ba6e2ba4 Bug 40696 - retrieve embedded resources from STYLE URL() attributes

Start block index: 396
End block index: 442
		private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection c,
				boolean orderMatters) // Does the order matter?
				throws Exception {
			String parserName = p.getClass().getName().substring("org.apache.jmeter.protocol.http.parser".length());
			log.debug("file   " + file);
			File f = findTestFile(file);
			byte[] buffer = new byte[(int) f.length()];
			int len = new FileInputStream(f).read(buffer);
			assertEquals(len, buffer.length);
			Iterator result;
			if (c == null) {
				result = p.getEmbeddedResourceURLs(buffer, new URL(url));
			} else {
				result = p.getEmbeddedResourceURLs(buffer, new URL(url), c);
			}
			/*
			 * TODO: Exact ordering is only required for some tests; change the
			 * comparison to do a set compare where necessary.
			 */
			Iterator expected;
			if (orderMatters) {
				expected = getFile(resultFile).iterator();
			} else {
				// Convert both to Sets
				expected = new TreeSet(getFile(resultFile)).iterator();
				TreeSet temp = new TreeSet(new Comparator() {
					public int compare(Object o1, Object o2) {
						return (o1.toString().compareTo(o2.toString()));
					}
				});
				while (result.hasNext()) {
					temp.add(result.next());
				}
				result = temp.iterator();
			}

			while (expected.hasNext()) {
				Object next = expected.next();
				assertTrue(parserName + "::Expecting another result " + next, result.hasNext());
				try {
					assertEquals(parserName + "(" + file + ")", next, ((URL) result.next()).toString());
				} catch (ClassCastException e) {
					fail(parserName + "::Expected URL, but got " + e.toString());
				}
			}
			assertFalse(parserName + "::Should have reached the end of the results", result.hasNext());
		}
