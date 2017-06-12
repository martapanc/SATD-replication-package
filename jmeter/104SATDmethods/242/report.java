File path: test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser16.java
Comment: O: Exact ordering is only required for some tests
Initial commit id: db0950a4
Final commit id: 30ff6849
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 162
End block index: 209
        private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection c,
                boolean orderMatters) // Does the order matter?
                throws Exception {
            String parserName = p.getClass().getName().substring("org.apache.jmeter.protocol.http.parser.".length());
            String fname = file.substring(file.indexOf("/")+1);
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
                assertTrue(fname+"::"+parserName + "::Expecting another result " + next, result.hasNext());
                try {
                    assertEquals(fname+"::"+parserName + "(next)", next, ((URL) result.next()).toString());
                } catch (ClassCastException e) {
                    fail(fname+"::"+parserName + "::Expected URL, but got " + e.toString());
                }
            }
            assertFalse(fname+"::"+parserName + "::Should have reached the end of the results", result.hasNext());
        }
