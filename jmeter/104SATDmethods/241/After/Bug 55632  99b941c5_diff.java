diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 9735bd4f4..8cfaababb 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,208 +1,212 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HtmlParsers can parse HTML content to obtain URLs.
  *
  */
 public abstract class HTMLParser {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
+    protected static final String ATT_CODE          = "code";// $NON-NLS-1$
+    protected static final String ATT_CODEBASE      = "codebase";// $NON-NLS-1$
+    protected static final String ATT_DATA          = "data";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
+    protected static final String TAG_BODY          = "body";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     // Cache of parsers - parsers must be re-usable
-    private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(3);
+    private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(4);
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
-        "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser"; // $NON-NLS-1$
+        "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
     /**
      * Protected constructor to prevent instantiation except from within
      * subclasses.
      */
     protected HTMLParser() {
     }
 
     public static final HTMLParser getParser() {
         return getParser(JMeterUtils.getPropDefault(PARSER_CLASSNAME, DEFAULT_PARSER));
     }
 
     public static final HTMLParser getParser(String htmlParserClassName) {
 
         // Is there a cached parser?
         HTMLParser pars = parsers.get(htmlParserClassName);
         if (pars != null) {
             log.debug("Fetched " + htmlParserClassName);
             return pars;
         }
 
         try {
             Object clazz = Class.forName(htmlParserClassName).newInstance();
             if (clazz instanceof HTMLParser) {
                 pars = (HTMLParser) clazz;
             } else {
                 throw new HTMLParseError(new ClassCastException(htmlParserClassName));
             }
         } catch (InstantiationException e) {
             throw new HTMLParseError(e);
         } catch (IllegalAccessException e) {
             throw new HTMLParseError(e);
         } catch (ClassNotFoundException e) {
             throw new HTMLParseError(e);
         }
         log.info("Created " + htmlParserClassName);
         if (pars.isReusable()) {
             parsers.put(htmlParserClassName, pars);// cache the parser
         }
 
         return pars;
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * URLs should not appear twice in the returned iterator.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<URLString>();
         return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(col),encoding);
 
         // An additional note on using HashSets to store URLs: I just
         // discovered that obtaining the hashCode of a java.net.URL implies
         // a domain-name resolution process. This means significant delays
         // can occur, even more so if the domain name is not resolvable.
         // Whether this can be a problem in practical situations I can't tell,
         // but
         // thought I'd keep a note just in case...
         // BTW, note that using a List and removing duplicates via scan
         // would not help, since URL.equals requires name resolution too.
         // The above problem has now been addressed with the URLString and
         // URLCollection classes.
 
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * All URLs should be added to the Collection.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      *
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      *
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) throws HTMLParseException {
         return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(coll), encoding);
     }
 
     /**
      * Parsers should over-ride this method if the parser class is re-usable, in
      * which case the class will be cached for the next getParser() call.
      *
      * @return true if the Parser is reusable
      */
     protected boolean isReusable() {
         return false;
     }
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
new file mode 100644
index 000000000..95d0c7f74
--- /dev/null
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
@@ -0,0 +1,160 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+
+package org.apache.jmeter.protocol.http.parser;
+
+import java.net.MalformedURLException;
+import java.net.URL;
+import java.util.Iterator;
+
+import jodd.lagarto.EmptyTagVisitor;
+import jodd.lagarto.LagartoParser;
+import jodd.lagarto.Tag;
+
+import org.apache.commons.lang3.StringUtils;
+import org.apache.jmeter.protocol.http.util.ConversionUtils;
+
+/**
+ * Parser based on Lagarto
+ * @since 2.10
+ */
+public class LagartoBasedHtmlParser extends HTMLParser {
+    /*
+     * A dummy class to pass the pointer of URL.
+     */
+    private static class URLPointer {
+        private URLPointer(URL newUrl) {
+            url = newUrl;
+        }
+        private URL url;
+    }
+    
+    private static final class JMeterTagVisitor extends EmptyTagVisitor {
+
+        private URLCollection urls;
+        private URLPointer baseUrl;
+
+        /**
+         * @param baseUrl 
+         * @param urls 
+         */
+        public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls) {
+            this.urls = urls;
+            this.baseUrl = baseUrl;
+        }
+
+        private final void extractAttribute(Tag tag, String attributeName) {
+            String url = tag.getAttributeValue(attributeName, false);
+            if (!StringUtils.isEmpty(url)) {
+                urls.addURL(url, baseUrl.url);
+            }
+        }
+        /*
+         * (non-Javadoc)
+         * 
+         * @see jodd.lagarto.EmptyTagVisitor#script(jodd.lagarto.Tag,
+         * java.lang.CharSequence)
+         */
+        @Override
+        public void script(Tag tag, CharSequence body) {
+            extractAttribute(tag, ATT_SRC);
+        }
+
+        /*
+         * (non-Javadoc)
+         * 
+         * @see jodd.lagarto.EmptyTagVisitor#tag(jodd.lagarto.Tag)
+         */
+        @Override
+        public void tag(Tag tag) {
+
+            String tagName = tag.getName().toLowerCase();
+            if (tagName.equals(TAG_BODY)) {
+                extractAttribute(tag, ATT_BACKGROUND);
+            } else if (tagName.equals(TAG_BASE)) {
+                String baseref = tag.getAttributeValue(ATT_HREF, false);
+                try {
+                    if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
+                    {
+                        baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
+                    }
+                } catch (MalformedURLException e1) {
+                    throw new RuntimeException(e1);
+                }
+            } else if (tagName.equals(TAG_IMAGE)) {
+                extractAttribute(tag, ATT_SRC);
+            } else if (tagName.equals(TAG_APPLET)) {
+                extractAttribute(tag, ATT_CODE);
+            } else if (tagName.equals(TAG_OBJECT)) {
+                extractAttribute(tag, ATT_CODEBASE);                
+                extractAttribute(tag, ATT_DATA);                 
+            } else if (tagName.equals(TAG_INPUT)) {
+                // we check the input tag type for image
+                if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttributeValue(ATT_TYPE, false))) {
+                    // then we need to download the binary
+                    extractAttribute(tag, ATT_SRC);
+                }
+            } else if (tagName.equals(TAG_SCRIPT)) {
+                extractAttribute(tag, ATT_SRC);
+                // Bug 51750
+            } else if (tagName.equals(TAG_FRAME) || tagName.equals(TAG_IFRAME)) {
+                extractAttribute(tag, ATT_SRC);
+            } else if (tagName.equals(TAG_EMBED)) {
+                extractAttribute(tag, ATT_SRC);
+            } else if (tagName.equals(TAG_BGSOUND)){
+                extractAttribute(tag, ATT_SRC);
+            } else if (tagName.equals(TAG_LINK)) {
+                // Putting the string first means it works even if the attribute is null
+                if (STYLESHEET.equalsIgnoreCase(tag.getAttributeValue(ATT_REL, false))) {
+                    extractAttribute(tag, ATT_HREF);
+                }
+            } else {
+                extractAttribute(tag, ATT_BACKGROUND);
+            }
+
+
+            // Now look for URLs in the STYLE attribute
+            String styleTagStr = tag.getAttributeValue(ATT_STYLE, false);
+            if(styleTagStr != null) {
+                HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
+            }
+        }
+    }
+
+    @Override
+    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl,
+            URLCollection coll, String encoding) throws HTMLParseException {
+        try {
+            String contents = new String(html,encoding); 
+            LagartoParser lagartoParser = new LagartoParser(contents);
+            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll);
+            lagartoParser.parse(tagVisitor);
+            return coll.iterator();
+        } catch (Exception e) {
+            throw new HTMLParseException(e);
+        }
+    }
+
+    /* (non-Javadoc)
+     * @see org.apache.jmeter.protocol.http.parser.HTMLParser#isReusable()
+     */
+    @Override
+    protected boolean isReusable() {
+        return true;
+    }
+}
diff --git a/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java b/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
index 294e5a336..8d573e9e0 100644
--- a/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
+++ b/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
@@ -1,319 +1,320 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * 
  */
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 import java.util.TreeSet;
 import java.util.Vector;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 import junit.framework.TestSuite;
 
 public class TestHTMLParser extends JMeterTestCase {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public TestHTMLParser(String arg0) {
         super(arg0);
     }
         private String parserName;
 
         private int testNumber = 0;
 
         public TestHTMLParser(String name, int test) {
             super(name);
             testNumber = test;
         }
 
         public TestHTMLParser(String name, String parser, int test) {
             super(name);
             testNumber = test;
             parserName = parser;
         }
 
         private static class StaticTestClass // Can't instantiate
         {
             private StaticTestClass() {
             }
         }
 
         private class TestClass // Can't instantiate
         {
             private TestClass() {
             }
         }
 
         private static class TestData {
             private String fileName;
 
             private String baseURL;
 
             private String expectedSet;
 
             private String expectedList;
 
             private TestData(String f, String b, String s, String l) {
                 fileName = f;
                 baseURL = b;
                 expectedSet = s;
                 expectedList = l;
             }
 
 //            private TestData(String f, String b, String s) {
 //                this(f, b, s, null);
 //            }
         }
 
         // List of parsers to test. Should probably be derived automatically
         private static final String[] PARSERS = { 
             "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser",
             "org.apache.jmeter.protocol.http.parser.JTidyHTMLParser",
-            "org.apache.jmeter.protocol.http.parser.RegexpHTMLParser" 
+            "org.apache.jmeter.protocol.http.parser.RegexpHTMLParser",
+            "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"
             };
 
         private static final TestData[] TESTS = new TestData[] {
                 new TestData("testfiles/HTMLParserTestCase.html",
                         "http://localhost/mydir/myfile.html",
                         "testfiles/HTMLParserTestCase.set",
                         "testfiles/HTMLParserTestCase.all"),
                 new TestData("testfiles/HTMLParserTestCaseWithBaseHRef.html", 
                         "http://localhost/mydir/myfile.html",
                         "testfiles/HTMLParserTestCaseBase.set", 
                         "testfiles/HTMLParserTestCaseBase.all"),
                 new TestData("testfiles/HTMLParserTestCaseWithBaseHRef2.html", 
                         "http://localhost/mydir/myfile.html",
                          "testfiles/HTMLParserTestCaseBase.set", 
                          "testfiles/HTMLParserTestCaseBase.all"),
                 new TestData("testfiles/HTMLParserTestCaseWithMissingBaseHRef.html",
                         "http://localhost/mydir/images/myfile.html", 
                         "testfiles/HTMLParserTestCaseBase.set",
                         "testfiles/HTMLParserTestCaseBase.all"),
                 new TestData("testfiles/HTMLParserTestCase2.html",
                         "http:", "", ""), // Dummy as the file has no entries
                 new TestData("testfiles/HTMLParserTestCase3.html",
                         "http:", "", ""), // Dummy as the file has no entries
                 new TestData("testfiles/HTMLParserTestCaseWithComments.html",
                         "http://localhost/mydir/myfile.html",
                         "testfiles/HTMLParserTestCaseBase.set",
                         "testfiles/HTMLParserTestCaseBase.all"),
                 new TestData("testfiles/HTMLScript.html",
                         "http://localhost/",
                         "testfiles/HTMLScript.set",
                         "testfiles/HTMLScript.all"),
                 new TestData("testfiles/HTMLParserTestFrames.html",
                         "http://localhost/",
                         "testfiles/HTMLParserTestFrames.all",
                         "testfiles/HTMLParserTestFrames.all"), 
                 // Relative filenames
                 new TestData("testfiles/HTMLParserTestFile_2.html",
                         "file:HTMLParserTestFile_2.html",
                         "testfiles/HTMLParserTestFile_2.all",
                         "testfiles/HTMLParserTestFile_2.all"), 
                          };
 
         public static junit.framework.Test suite() {
             TestSuite suite = new TestSuite("TestHTMLParser");
             suite.addTest(new TestHTMLParser("testDefaultParser"));
             suite.addTest(new TestHTMLParser("testParserDefault"));
             suite.addTest(new TestHTMLParser("testParserMissing"));
             suite.addTest(new TestHTMLParser("testNotParser"));
             suite.addTest(new TestHTMLParser("testNotCreatable"));
             suite.addTest(new TestHTMLParser("testNotCreatableStatic"));
             for (int i = 0; i < PARSERS.length; i++) {
                 TestSuite ps = new TestSuite(PARSERS[i]);// Identify subtests
                 ps.addTest(new TestHTMLParser("testParserProperty", PARSERS[i], 0));
                 for (int j = 0; j < TESTS.length; j++) {
                     TestSuite ts = new TestSuite(TESTS[j].fileName);
                     ts.addTest(new TestHTMLParser("testParserSet", PARSERS[i], j));
                     ts.addTest(new TestHTMLParser("testParserList", PARSERS[i], j));
                     ps.addTest(ts);
                 }
                 suite.addTest(ps);
             }
             return suite;
         }
 
         // Test if can instantiate parser using property name
         public void testParserProperty() throws Exception {
             Properties p = JMeterUtils.getJMeterProperties();
             if (p == null) {
                 p = JMeterUtils.getProperties("jmeter.properties");
             }
             p.setProperty(HTMLParser.PARSER_CLASSNAME, parserName);
             HTMLParser.getParser();
         }
 
         public void testDefaultParser() throws Exception {
             HTMLParser.getParser();
         }
 
         public void testParserDefault() throws Exception {
             HTMLParser.getParser(HTMLParser.DEFAULT_PARSER);
         }
 
         public void testParserMissing() throws Exception {
             try {
                 HTMLParser.getParser("no.such.parser");
                 fail("Should not have been able to create the parser");
             } catch (HTMLParseError e) {
                 if (e.getCause() instanceof ClassNotFoundException) {
                     // This is OK
                 } else {
                     throw e;
                 }
             }
         }
 
         public void testNotParser() throws Exception {
             try {
                 HTMLParser.getParser("java.lang.String");
                 fail("Should not have been able to create the parser");
             } catch (HTMLParseError e) {
                 if (e.getCause() instanceof ClassCastException) {
                     return;
                 }
                 throw e;
             }
         }
 
         public void testNotCreatable() throws Exception {
             try {
                 HTMLParser.getParser(TestClass.class.getName());
                 fail("Should not have been able to create the parser");
             } catch (HTMLParseError e) {
                 if (e.getCause() instanceof InstantiationException) {
                     return;
                 }
                 throw e;
             }
         }
 
         public void testNotCreatableStatic() throws Exception {
             try {
                 HTMLParser.getParser(StaticTestClass.class.getName());
                 fail("Should not have been able to create the parser");
             } catch (HTMLParseError e) {
                 if (e.getCause() instanceof ClassCastException) {
                     return;
                 }
                 if (e.getCause() instanceof IllegalAccessException) {
                     return;
                 }
                 throw e;
             }
         }
 
         public void testParserSet() throws Exception {
             HTMLParser p = HTMLParser.getParser(parserName);
             filetest(p, TESTS[testNumber].fileName, TESTS[testNumber].baseURL, TESTS[testNumber].expectedSet, null,
                     false);
         }
 
         public void testParserList() throws Exception {
             HTMLParser p = HTMLParser.getParser(parserName);
             filetest(p, TESTS[testNumber].fileName, TESTS[testNumber].baseURL, TESTS[testNumber].expectedList,
                     new Vector<URLString>(), true);
         }
 
         private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection<URLString> c,
                 boolean orderMatters) // Does the order matter?
                 throws Exception {
             String parserName = p.getClass().getName().substring("org.apache.jmeter.protocol.http.parser.".length());
             String fname = file.substring(file.indexOf('/')+1);
             log.debug("file   " + file);
             File f = findTestFile(file);
             byte[] buffer = new byte[(int) f.length()];
             InputStream is = null;
             try {
                 is = new FileInputStream(f);
                 int len = is.read(buffer);
                 assertEquals(len, buffer.length);
             } finally {
                 IOUtils.closeQuietly(is);
             }
             Iterator<URL> result;
             if (c == null) {
                 result = p.getEmbeddedResourceURLs(buffer, new URL(url), System.getProperty("file.encoding"));
             } else {
                 result = p.getEmbeddedResourceURLs(buffer, new URL(url), c,System.getProperty("file.encoding"));
             }
             /*
              * TODO: Exact ordering is only required for some tests; change the
              * comparison to do a set compare where necessary.
              */
             Iterator<String> expected;
             if (orderMatters) {
                 expected = getFile(resultFile).iterator();
             } else {
                 // Convert both to Sets
                 expected = new TreeSet<String>(getFile(resultFile)).iterator();
                 TreeSet<URL> temp = new TreeSet<URL>(new Comparator<Object>() {
                     @Override
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
                     assertEquals(fname+"::"+parserName + "(next)", next, result.next().toString());
                 } catch (ClassCastException e) {
                     fail(fname+"::"+parserName + "::Expected URL, but got " + e.toString());
                 }
             }
             assertFalse(fname+"::"+parserName + "::Should have reached the end of the results", result.hasNext());
         }
 
         // Get expected results as a List
         private static List<String> getFile(String file) throws Exception {
             ArrayList<String> al = new ArrayList<String>();
             if (file != null && file.length() > 0) {
                 BufferedReader br = new BufferedReader(new FileReader(findTestFile(file)));
                 String line = br.readLine();
                 while (line != null) {
                     al.add(line);
                     line = br.readLine();
                 }
                 br.close();
             }
             return al;
         }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index cc39222f3..d233ea25f 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,519 +1,520 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.10 =================== -->
 
 <h1>Version 2.10</h1>
 
 <h2>New and Noteworthy</h2>
 
 <h3><u>Core Improvements:</u></h3>
 
 <h4>* New Performance improvements</h4>
 <p>
 <ul>
 <li>A Huge performance improvement has been made on High Throughput Tests (no pause), see <bugzilla>54777</bugzilla></li>
 <li>An issue with unnecessary SSL Context reset has been fixed which improves performances of pure HTTP tests, see <bugzilla>55023</bugzilla></li>
 </ul>
 </p>
 
 <h4>* New CSS/JQuery Tester in View Tree Results that makes CSS/JQuery Extractor a first class
 citizen in JMeter, you can now test your expressions very easily</h4>
 <p>
 <figure width="1144" height="638" image="changes/2.10/01_css_jquery_tester.png"></figure>
 </p>
 
 <h4>* Many improvements in HTTP(S) Recording have been made</h4>
 <p>
 <note>
 The "HTTP Proxy Server" test element has been renamed as "HTTP(S) Test Script Recorder".
 </note>
 <ul>
 <li>Better recording of HTTPS sites, embedded resources using subdomains will more easily be recorded when using JDK 7. See <bugzilla>55507</bugzilla>.
 See updated documentation: <complink name="HTTP(S) Test Script Recorder"/>
 </li>
 <li>Redirection are now more smartly detected by HTTP Proxy Server, see <bugzilla>55531</bugzilla></li>
 <li>Many fixes on edge cases with HTTPS have been made, see <bugzilla>55502</bugzilla>, <bugzilla>55504</bugzilla>, <bugzilla>55506</bugzilla></li>
 <li>Many encoding fixes have been made, see <bugzilla>54482</bugzilla>, <bugzilla>54142</bugzilla>, <bugzilla>54293</bugzilla></li>
 </ul>
 </p>
 
 <h4>* You can now load test MongoDB through new MongoDB Source Config</h4>
 <p>
 <figure width="912" height="486" image="changes/2.10/02_mongodb_source_config.png"></figure>
 </p>
 <p>
 <figure width="850" height="687" image="changes/2.10/14_mongodb_jsr223.png"></figure>
 </p>
 
 <h4>* Kerberos authentication has been added to Auth Manager</h4>
 <p>
 <figure width="1005" height="364" image="changes/2.10/15_kerberos.png"></figure>
 </p>
 
 <h4>* device can now be used in addition to source IP address</h4>
 
 <p>
 <figure width="1087" height="699" image="changes/2.10/16_device.png"></figure>
 </p>
 
 <h4>* You can now do functional testing of MongoDB scripts through new MongoDB Script</h4>
 <p>
 <figure width="906" height="313" image="changes/2.10/03_mongodb_script_alpha.png"></figure>
 </p>
 
 <h4>* Timeout has been added to OS Process Sampler</h4>
 <p>
 <figure width="684" height="586" image="changes/2.10/17_os_process_timeout.png"></figure>
 </p>
 
 <h4>* Query timeout has been added to JDBC Request</h4>
 <p>
 <figure width="540" height="600" image="changes/2.10/04_jdbc_request_timeout.png"></figure>
 </p>
 
 <h4>* New functions (__urlencode and __urldecode) are now available to encode/decode URL encoded chars</h4>
 <p>
 <figure width="512" height="240" image="changes/2.10/05_urlencode_function.png"></figure>
 </p>
 
 <h4>* Continuous Integration is now eased by addition of a new flag that forces NON-GUI JVM to exit after test end</h4>
 <p>See jmeter property:</p>
 <code>jmeterengine.force.system.exit</code>
 
 <h4>* HttpSampler now allows DELETE Http Method to have a body (works for HC4 and HC31 implementations). This allows for example to test Elastic Search APIs</h4>
 <p>
 <figure width="573" height="444" image="changes/2.10/06_http_request_delete_method.png"></figure>
 </p>
 
 <h4>* Distributed testing has been improved</h4>
 <p>
 <ul>
 <li>
 Number of threads on each node are now reported to controller.
 <p>
 <figure width="988" height="355" image="changes/2.10/17_threads_summariser.png"></figure>
 </p>
 <p>
 <figure width="125" height="33" image="changes/2.10/17_threads_gui.png"></figure>
 </p>
 
 </li>
 <li>Performance improvement on BatchSampleSender(<bugzilla>55423</bugzilla>)</li>
 <li>Addition of 2 SampleSender modes (StrippedAsynch and StrippedDiskStore), see jmeter.properties</li>
 </ul>
 </p>
 
 <h4>* ModuleController has been improved to better handle changes to referenced controllers</h4>
 
 <h4>* Improved class loader configuration, see <bugzilla>55503</bugzilla></h4>
 <p>
 <ul>
 <li>New property "plugin_dependency_paths" for plugin dependencies</li>
 <li>Properties "search_paths", "user.classpath" and "plugin_dependency_paths"
     now automatically add all jars from configured directories</li>
 </ul>
 </p>
 
 <h4>* Best-practices section has been improved, ensure you read it to get the most out of JMeter</h4>
 
 <h3><u>GUI and ergonomy Improvements:</u></h3>
 
 
 <h4>* New Templates feature that allows you to create test plan from existing template or merge
 template into your Test Plan</h4>
 <p>
 <figure width="428" height="130" image="changes/2.10/07_jmeter_templates_icon.png"></figure>
 </p>
 <p>
 <figure width="781" height="472" image="changes/2.10/08_jmeter_templates_box.png"></figure>
 </p>
 
 <h4>* Workbench can now be saved</h4>
 <p>
 <figure width="489" height="198" image="changes/2.10/09_save_workbench.png"></figure>
 </p>
 
 <h4>* Syntax color has been added to scripts elements (BeanShell, BSF, and JSR223), MongoDB and JDBC elements making code much more readable and allowing <b>UNDO/REDO</b> through CTRL+Z/CTRL+Y</h4>
 <p>BSF Sampler with syntax color
 <figure width="915" height="620" image="changes/2.10/10_color_syntax_bsf_sampler.png"></figure>
 </p>
 <p>JSR223 Pre Processor with syntax color
 <figure width="911" height="614" image="changes/2.10/11_color_syntax_jsr223_preprocessor.png"></figure>
 </p>
 
 <h4>* Better editors are now available for Test Elements with large text content, like HTTP Sampler, and JMS related Test Element providing line numbering and allowing <b>UNDO/REDO</b> through CTRL+Z/CTRL+Y</h4>
 
 <h4>* JMeter GUI can now be fully Internationalized, all remaining issues have been fixed</h4>
 <h5>Currently French has all its labels translated. Other languages are partly translated, feel free to 
 contribute translations by reading <a href="localising/index.html">Localisation (Translator's Guide)</a></h5>
 
 <h4>* Moving elements in Test plan has been improved in many ways</h4>
 <h5>Drag and drop of elements in Test Plan tree is now much easier and possible on multiple nodes</h5>
 <p>
 <figure width="894" height="236" image="changes/2.10/12_drap_n-drop_multiple.png"></figure>
 </p>
 <p>
 <b>Note that due to this <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6560955">bug in Java</a>,
 you cannot drop a node after last node. The workaround is to drop it before this last node and then Drag and Drop the last node 
 before the one you just dropped.</b>
 </p>
 <h5>New shortcuts have been added to move elements in the tree. </h5>
 <p>(alt + Arrow Up) and (alt + Arrow Down) move the element within the parent node.<br/>
 (alt + Arrow Left) and (alt + Arrow Right) move the element up and down in the tree depth</p>
 
 <h4>* Response Time Graph Y axis can now be scaled</h4>
 <p>
 <figure width="947" height="596" image="changes/2.10/13_response_time_graph_y_scale.png"></figure>
 </p>
 
 <h4>* JUnit Sampler gives now more details on configuration errors</h4>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>SMTP Sampler now uses eml file subject if subject field is empty</p>
 
 <p>With this version autoFlush has been turned off on PrintWriter in charge of writing test results. 
 This results in improved throughput for intensive tests but can result in more test data loss in case
 of JMeter crash (very rare). To revert to previous behaviour set jmeter.save.saveservice.autoflush property to true. </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+SHIFT+F1 (CMD + SHIFT + F1 for Mac OS).
 The original key sequence (Ctrl+F1) did not work in some locations (it is consumed by the Java Swing ToolTipManager).
 It was therefore necessary to change the shortcut.
 </p>
 
 <p>
 Webservice (SOAP) Request has been removed by default from GUI as Element is deprecated (use HTTP Sampler with Body Data), if you need to show it, see property not_in_menu in jmeter.properties
 </p>
 
 <p>
 Transaction Controller now sets Response Code of Generated Parent Sampler (if Generate Parent Sampler is checked) to response code of first failing child in case of failure of one of the children, in previous versions Response Code was empty.
 </p>
 
 <p>
 In previous versions, IncludeController could run Test Elements located inside a Thread Group, this behaviour (which was not documented) could result in weird behaviour, it has been removed in this version (see <bugzilla>55464</bugzilla>). 
 The correct way to include Test Elements is to use Test Fragment as stated in documentation of Include Controller.
 </p>
 
 <p>
 The retry count for the HttpClient 3.1 and HttpClient 4.x samplers has been changed to 0.
 Previously the default was 1, which could cause unexpected additional traffic.
 </p>
 
 <p>Starting with this version, the HTTP(S) Test Script Recorder tries to detect when a sample is the result of a previous
 redirect. If the current response is a redirect, JMeter will save the redirect URL. When the next request is received, 
 it is compared with the saved redirect URL and if there is a match, JMeter will disable the generated sample.
 To revert to previous behaviour, set the property <code>proxy.redirect.disabling=false</code>
  </p>
 
 <p>__escapeOroRegexpChars function (which escapes ORO reserved characters) no longer trims the value (see <bugzilla>55328</bugzilla>)</p>
 
 <p>The commons-lang-2.6.jar has been removed from embedded libraries in jmeter/lib folder as it is not needed by JMeter at run-time (it is only used by Apache Velocity for generating documentation).
 If you use any plugin or third-party code that depends on it, you need to add it in jmeter/lib folder</p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>54627</bugzilla> - JMeter Proxy GUI: Type of sampler setting takes the whole screen when there are samplers with long names.</li>
 <li><bugzilla>54629</bugzilla> - HTMLParser does not extract &amp;lt;object&amp;gt; tag urls.</li>
 <li><bugzilla>55023</bugzilla> - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput. based on analysis by Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li><bugzilla>55092</bugzilla> - Log message "WARN - jmeter.protocol.http.sampler.HTTPSamplerBase: Null URL detected (should not happen)" displayed when embedded resource URL is malformed.</li>
 <li><bugzilla>55161</bugzilla> - Useless processing in SoapSampler.setPostHeaders. Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>54482</bugzilla> - HC fails to follow redirects with non-encoded chars.</li>
 <li><bugzilla>54142</bugzilla> - HTTP Proxy Server throws an exception when path contains "|" character.</li>
 <li><bugzilla>55388</bugzilla> - HC3 does not allow IP Source field to override httpclient.localaddress.</li>
 <li><bugzilla>55450</bugzilla> - HEAD redirects should remain as HEAD</li>
 <li><bugzilla>55455</bugzilla> - HTTPS with HTTPClient4 ignores cps setting</li>
 <li><bugzilla>55502</bugzilla> - Proxy generates empty http:/ entries when recording</li>
 <li><bugzilla>55504</bugzilla> - Proxy incorrectly issues CONNECT requests when browser prompts for certificate override</li>
 <li><bugzilla>55506</bugzilla> - Proxy should deliver failed requests to any configured Listeners</li>
 <li><bugzilla>55545</bugzilla> - HTTP Proxy Server GUI should not allow both Follow and Auto redirect to be selected</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>54913</bugzilla> - JMSPublisherGui incorrectly restores its state. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55027</bugzilla> - Test Action regression, duration value is not recorded (nightly build).</li>
 <li><bugzilla>55163</bugzilla> - BeanShellTestElement fails to quote string when calling testStarted(String)/testEnded(String).</li>
 <li><bugzilla>55349</bugzilla> - NativeCommand hangs if no input file is specified and the application requests input.</li>
 <li><bugzilla>55462</bugzilla> - System Sampler should not change the sampler label if a sample fails</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54467</bugzilla> - Loop Controller: compute loop value only once per parent iteration.</li>
 <li><bugzilla>54985</bugzilla> - Make Transaction Controller set Response Code of Generated Parent Sampler to response code of first failing child in case of failure of one of its children. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54950</bugzilla> - ModuleController : Changes to referenced Module are not taken into account if changes occur after first run and referenced node is disabled.</li>
 <li><bugzilla>55201</bugzilla> - ForEach controller excludes start index and includes end index (clarified documentation).</li>
 <li><bugzilla>55334</bugzilla> - Adding Include Controller to test plan (made of Include Controllers) without saving TestPlan leads to included code not being taken into account until save.</li>
 <li><bugzilla>55375</bugzilla> -  StackOverflowError with ModuleController in Non-GUI mode if its name is the same as the target node.</li>
 <li><bugzilla>55464</bugzilla> - Include Controller running included thread group</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54589</bugzilla> - View Results Tree have a lot of Garbage characters if html page uses double-byte charset.</li>
 <li><bugzilla>54753</bugzilla> - StringIndexOutOfBoundsException at SampleResult.getSampleLabel() if key_on_threadname=false when using Statistical mode.</li>
 <li><bugzilla>54685</bugzilla> - ArrayIndexOutOfBoundsException if "sample_variable" is set in client but not server.</li>
 <li><bugzilla>55111</bugzilla> - ViewResultsTree: text not refitted if vertical scrollbar is required. Contributed by Milamber</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54540</bugzilla> - "HTML Parameter Mask" are not marked deprecated in the IHM.</li>
 <li><bugzilla>54575</bugzilla> - CSS/JQuery Extractor : Choosing JODD Implementation always uses JSOUP.</li>
 <li><bugzilla>54901</bugzilla> - Response Assertion GUI behaves weirdly.</li>
 <li><bugzilla>54924</bugzilla> - XMLAssertion uses JMeter JVM file.encoding instead of response encoding and does not clean threadlocal variable.</li>
 <li><bugzilla>53679</bugzilla> -  Constant Throughput Timer bug with localization. Reported by Ludovic Garcia</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>55328</bugzilla> - __escapeOroRegexpChars trims spaces.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55437</bugzilla> - ComboStringEditor does not translate EDIT and UNDEFINED strings on language change</li>
 <li><bugzilla>55501</bugzilla> - Incorrect encoding for French description of __char function. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54504</bugzilla> - Resource string not found: [clipboard_node_read_error].</li>
 <li><bugzilla>54538</bugzilla> - GUI: context menu is too big.</li>
 <li><bugzilla>54847</bugzilla> - Cut &amp; Paste is broken with tree multi-selection. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54870</bugzilla> - Tree drag and drop may lose leaf nodes (affected nightly build). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55056</bugzilla> - wasted work in Data.append(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55129</bugzilla> -  Change Javadoc generation per CVE-2013-1571, VU#225657.</li>
 <li><bugzilla>55187</bugzilla> - Integer overflow when computing ONE_YEAR_MS in HTTP CacheManager.</li>
 <li><bugzilla>55208</bugzilla> - JSR223 language entries are duplicated; fold to lower case.</li>
 <li><bugzilla>55203</bugzilla> - TestBeanGUI - wrong language settings found.</li>
 <li><bugzilla>55065</bugzilla> - Useless processing in Spline3.converge(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55064</bugzilla> - Useless processing in ReportTreeListener.isValidDragAction(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55242</bugzilla> - BeanShell Client jar throws exceptions after upgrading to 2.8.</li>
 <li><bugzilla>55288</bugzilla> - JMeter should default to 0 retries for HTTP requests.</li>
 <li><bugzilla>55405</bugzilla> - ant download_jars task fails if lib/api or lib/doc are missing. Contributed by Antonio Gomes Rodrigues.</li>
 <li><bugzilla>55427</bugzilla> - TestBeanHelper should ignore properties not supported by GenericTestBeanCustomizer</li>
 <li><bugzilla>55459</bugzilla> - Elements using ComboStringEditor lose the input value if user selects another Test Element</li>
 <li><bugzilla>54152</bugzilla> - In distributed testing : activeThreads always show 0 in GUI and Summariser</li>
 <li><bugzilla>55509</bugzilla> - Allow Plugins to be notified of remote thread number progression</li>
 <li><bugzilla>55572</bugzilla> - Detail popup of parameter does not show a Scrollbar when content exceeds display</li>
 <li><bugzilla>55580</bugzilla> -  Help pane does not scroll to start for &lt;a href="#"&gt; links</li>
 <li><bugzilla>55600</bugzilla> - JSyntaxTextArea : Strange behaviour on first undo</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>HTTP Request: Small user interaction improvements in Row parameter Detail Box. Contributed by Milamber</li>
 <li><bugzilla>55255</bugzilla> - Allow Body in HTTP DELETE method to support API that use it (like ElasticSearch).</li>
 <li><bugzilla>53480</bugzilla> - Add Kerberos support to Http Sampler (HttpClient4). Based on patch by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>54874</bugzilla> - Support device in addition to source IP address. Based on patch by Dan Fruehauf (malkodan at gmail.com)</li>
 <li><bugzilla>55488</bugzilla> - Add .ico and .woff file extension to default suggested exclusions in proxy recorder. Contributed by Antonio Gomes Rodrigues</li>
 <li><bugzilla>55525</bugzilla> - Proxy should support alias for keyserver entry</li>
 <li><bugzilla>55531</bugzilla> - Proxy recording and redirects. Added code to disable redirected samples.</li>
 <li><bugzilla>55507</bugzilla> - Proxy SSL recording does not handle external embedded resources well</li>
+<li><bugzilla>55632</bugzilla> - Have a new implementation of htmlParser for embedded resources parsing with better performances</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54788</bugzilla> - JMS Point-to-Point Sampler - GUI enhancements to increase readability and ease of use. Contributed by Bruno Antunes (b.m.antunes at gmail.com)</li>
 <li><bugzilla>54798</bugzilla> - Using subject from EML-file for SMTP Sampler. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54759</bugzilla> - SSLPeerUnverifiedException using HTTPS , property documented.</li>
 <li><bugzilla>54896</bugzilla> - JUnit sampler gives only "failed to create an instance of the class" message with constructor problems.</li>
 <li><bugzilla>55084</bugzilla> - Add timeout support for JDBC Request. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55403</bugzilla> - Enhancement to OS sampler: Support for timeout</li>
 <li><bugzilla>55518</bugzilla> - Add ability to limit number of cached PreparedStatements per connection when "Prepared Select Statement", "Prepared Update Statement" or "Callable Statement" query type is selected</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54271</bugzilla> - Module Controller breaks if test plan is renamed.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54532</bugzilla> - Improve Response Time Graph Y axis scale with huge values or small values (&amp;lt; 1000ms). Add a new field to define increment scale. Contributed by Milamber based on patch by Luca Maragnani (luca.maragnani at gmail.com)</li>
 <li><bugzilla>54576</bugzilla> - View Results Tree : Add a CSS/JQuery Tester.</li>
 <li><bugzilla>54777</bugzilla> - Improve Performance of default ResultCollector. Based on patch by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55389</bugzilla> - Show IP source address in request data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54789</bugzilla> - XPath Assertion - GUI enhancements to increase readability and ease of use.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>54991</bugzilla> - Add functions to encode/decode URL encoded chars (__urlencode and __urldecode). Contributed by Milamber.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55241</bugzilla> - Need GUI Editor to process fields which are based on Enums with localised display strings</li>
 <li><bugzilla>55440</bugzilla> - ComboStringEditor should allow tags to be language dependent</li>
 <li><bugzilla>55432</bugzilla> - CSV Dataset Config loses sharing mode when switching languages</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54584</bugzilla> - MongoDB plugin. Based on patch by Jan Paul Ettles (janpaulettles at gmail.com)</li>
 <li><bugzilla>54669</bugzilla> - Add flag forcing non-GUI JVM to exit after test. Contributed by Scott Emmons</li>
 <li><bugzilla>42428</bugzilla> - Workbench not saved with Test Plan. Contributed by Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bugzilla>54825</bugzilla> - Add shortcuts to move elements in the tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54834</bugzilla> - Improve Drag &amp; Drop in the jmeter tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54839</bugzilla> - Set the application name on Mac. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54841</bugzilla> - Correctly handle the quit shortcut on Mac Os (CMD-Q). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54844</bugzilla> - Set the application icon on Mac Os. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54864</bugzilla> - Enable multi selection drag &amp; drop in the tree without having to start dragging before releasing Shift or Control. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54945</bugzilla> - Add Shutdown Hook to enable trapping kill or CTRL+C signals.</li>
 <li><bugzilla>54990</bugzilla> - Download large files avoiding outOfMemory.</li>
 <li><bugzilla>55085</bugzilla> - UX Improvement : Ability to create New Test Plan from Templates. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55172</bugzilla> - Provide plugins a way to add Top Menu and menu items.</li>
 <li><bugzilla>55202</bugzilla> - Add syntax color for scripts elements (BeanShell, BSF, and JSR223) and JDBC elements with RSyntaxTextArea. Contributed by Milamber based on patch by Marko Vlahovic (vlahovic74 at gmail.com)</li>
 <li><bugzilla>55175</bugzilla> - HTTPHC4Impl refactoring to allow better inheritance.</li>
 <li><bugzilla>55236</bugzilla> - Templates - provide button to reload template details.</li>
 <li><bugzilla>55237</bugzilla> - Template system should support relative fileName entries.</li>
 <li><bugzilla>55423</bugzilla> - BatchSampleSender: Reduce locking granularity by moving listener.processBatch outside of synchronized block</li>
 <li><bugzilla>55424</bugzilla> - Add Stripping to existing SampleSenders</li>
 <li><bugzilla>55451</bugzilla> - Test Element GUI with JSyntaxTextArea scroll down when text content is long enough to add a Scrollbar</li>
 <li><bugzilla>55513</bugzilla> - StreamCopier cannot be used with System.err or System.out as it closes the output stream</li>
 <li><bugzilla>55514</bugzilla> - SystemCommand should support arbitrary input and output streams</li>
 <li><bugzilla>55515</bugzilla> - SystemCommand should support chaining of commands</li>
 <li><bugzilla>55606</bugzilla> - Use JSyntaxtTextArea for Http Request, JMS Test Elements</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Updated to jsoup-1.7.2</li>
 <li><bugzilla>54776</bugzilla> - Update the dependency on Bouncy Castle to 1.48. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Updated to HttpComponents Client 4.2.6 (from 4.2.3)</li>
 <li>Updated to HttpComponents Core 4.2.5 (from 4.2.3)</li>
 <li>Updated to commons-codec 1.8 (from 1.6)</li>
 <li>Updated to commons-io 2.4 (from 2.2)</li>
 <li>Updated to commons-logging 1.1.3 (from 1.1.1)</li>
 <li>Updated to commons-net 3.3 (from 3.1)</li>
 <li>Updated to jdom-1.1.3 (from 1.1.2)</li>
 <li>Updated to jodd-lagarto and jodd-core 3.4.4 (from 3.4.1)</li>
 <li>Updated to junit 4.11 (from 4.10)</li>
 <li>Updated to slf4j-api 1.7.5 (from 1.7.2)</li>
 <li>Updated to tika 1.4 (from 1.3)</li>
 <li>Updated to xmlgraphics-commons 1.5 (from 1.3.1)</li>
 <li>Updated to xstream 1.4.4 (from 1.4.2)</li>
 <li>Updated to BouncyCastle 1.49 (from 1.48)</li>
 <li><bugzilla>54912</bugzilla> - JMeterTreeListener should use constants. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54903</bugzilla> - Remove the dependency on the Activation Framework. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Moved commons-lang (2.6) to lib/doc as it's only needed by Velocity.</li>
 <li>Re-organised and simplified NOTICE and LICENSE files.</li>
 <li><bugzilla>55411</bugzilla> -  NativeCommand could be useful elsewhere. Copied code to o.a.jorphan.exec.</li>
 <li><bugzilla>55435</bugzilla> - ComboStringEditor could be simplified to make most settings final</li>
 <li><bugzilla>55436</bugzilla> - ComboStringEditor should implement ClearGui</li>
 <li><bugzilla>55463</bugzilla> - Component.requestFocus() is discouraged; use requestFocusInWindow() instead</li>
 <li><bugzilla>55486</bugzilla> - New JMeter Logo. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55548</bugzilla> - Tidy up use of TestElement.ENABLED; use TestElement.isEnabled()/setEnabled() throughout</li>
 <li><bugzilla>55617</bugzilla> - Improvements to jorphan collection. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55623</bugzilla> - Invalid/unexpected configuration values should not be silently ignored</li>
 <li><bugzilla>55626</bugzilla> - Rename HTTP Proxy Server as HTTP(S) Test Script Recorder</li>
 </ul>
 
 <h2>Thanks</h2>
 <p>We thank all contributors mentioned in bug and improvement sections above.<br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Immanuel Hayden (immanuel.hayden at gmail.com)</li>
 <li>Danny Lade (dlade at web.de)</li>
 <li>Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li>Wolfgang Heider (wolfgang.heider at racon.at)</li>
 <li>Shmuel Krakower (shmulikk at gmail.com)</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
