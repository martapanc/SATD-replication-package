diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 86e934f23..a0f04d4e1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,220 +1,236 @@
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
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
 import org.slf4j.LoggerFactory;
 import org.slf4j.Logger;
 
 /**
  * {@link HTMLParser} subclasses can parse HTML content to obtain URLs.
  *
  */
 public abstract class HTMLParser extends BaseParser {
 
     private static final Logger log = LoggerFactory.getLogger(HTMLParser.class);
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
     protected static final String ATT_CODE          = "code";// $NON-NLS-1$
     protected static final String ATT_CODEBASE      = "codebase";// $NON-NLS-1$
     protected static final String ATT_DATA          = "data";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_BODY          = "body";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     protected static final String IE_UA             = "MSIE ([0-9]+.[0-9]+)";// $NON-NLS-1$
     protected static final Pattern IE_UA_PATTERN    = Pattern.compile(IE_UA);
     private   static final float IE_10                = 10.0f;
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
+    private static final Pattern NORMALIZE_URL_PATTERN = Pattern.compile("[\n\r\b\f]+"); //$NON-NLS-1$
+
     /**
      * Protected constructor to prevent instantiation except from within
      * subclasses.
      */
     protected HTMLParser() {
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
      * @param userAgent
      *            User Agent
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<>();
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(col),encoding);
 
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
      * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) 
                     throws HTMLParseException {
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(coll), encoding);
     }
     
     /**
      * 
      * @param ieVersion Float IE version
      * @return true if IE version &lt; IE v10
      */
     protected final boolean isEnableConditionalComments(Float ieVersion) {
         // Conditional comment have been dropped in IE10
         // http://msdn.microsoft.com/en-us/library/ie/hh801214%28v=vs.85%29.aspx
         return ieVersion != null && ieVersion.floatValue() < IE_10;
     }
     
     /**
      * 
      * @param userAgent User Agent
      * @return version null if not IE or the version after MSIE
      */
     protected Float extractIEVersion(String userAgent) {
         if (StringUtils.isEmpty(userAgent)) {
             log.info("userAgent is null");
             return null;
         }
         Matcher matcher = IE_UA_PATTERN.matcher(userAgent);
         String ieVersion = null;
         if (matcher.find()) {
             if (matcher.groupCount() > 0) {
                 ieVersion = matcher.group(1);
             } else {
                 ieVersion = matcher.group();
             }
         }
         if (ieVersion != null) {
             return Float.valueOf(ieVersion);
         } else {
             return null;
         }
     }
+
+    /**
+     * Normalizes URL as browsers do
+     * @param url {@link CharSequence}
+     */
+    protected static String normalizeUrlValue(CharSequence url) {
+        if (!StringUtils.isEmpty(url)) {
+            String trimmed = NORMALIZE_URL_PATTERN.matcher(url.toString().trim()).replaceAll("");
+            if (!trimmed.isEmpty()) {
+                return trimmed;
+            }
+        }
+        return null;
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
index e3c21575e..a5f14a6ba 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
@@ -1,150 +1,151 @@
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
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.select.NodeTraversor;
 import org.jsoup.select.NodeVisitor;
 
 /**
  * Parser based on JSOUP
  * @since 2.10
  * TODO Factor out common code between {@link LagartoBasedHtmlParser} and this one (adapter pattern)
  */
 public class JsoupBasedHtmlParser extends HTMLParser {
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
 
     private static final class JMeterNodeVisitor implements NodeVisitor {
 
         private URLCollection urls;
         private URLPointer baseUrl;
 
         /**
          * @param baseUrl base url to extract possibly missing information from urls found in <code>urls</code>
          * @param urls collection of urls to consider
          */
         public JMeterNodeVisitor(final URLPointer baseUrl, URLCollection urls) {
             this.urls = urls;
             this.baseUrl = baseUrl;
         }
 
         private void extractAttribute(Element tag, String attributeName) {
             String url = tag.attr(attributeName);
-            if (!StringUtils.isEmpty(url)) {
-                urls.addURL(url, baseUrl.url);
+            String normalizedUrl = normalizeUrlValue(url);
+            if(normalizedUrl != null) {
+                urls.addURL(normalizedUrl, baseUrl.url);
             }
         }
 
         @Override
         public void head(Node node, int depth) {
             if (!(node instanceof Element)) {
                 return;
             }
             Element tag = (Element) node;
             String tagName = tag.tagName().toLowerCase();
             if (tagName.equals(TAG_BODY)) {
                 extractAttribute(tag, ATT_BACKGROUND);
             } else if (tagName.equals(TAG_SCRIPT)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BASE)) {
                 String baseref = tag.attr(ATT_HREF);
                 try {
                     if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                     {
                         baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
                     }
                 } catch (MalformedURLException e1) {
                     throw new RuntimeException(e1);
                 }
             } else if (tagName.equals(TAG_IMAGE)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_APPLET)) {
                 extractAttribute(tag, ATT_CODE);
             } else if (tagName.equals(TAG_OBJECT)) {
                 extractAttribute(tag, ATT_CODEBASE);
                 extractAttribute(tag, ATT_DATA);
             } else if (tagName.equals(TAG_INPUT)) {
                 // we check the input tag type for image
                 if (ATT_IS_IMAGE.equalsIgnoreCase(tag.attr(ATT_TYPE))) {
                     // then we need to download the binary
                     extractAttribute(tag, ATT_SRC);
                 }
                 // Bug 51750
             } else if (tagName.equals(TAG_FRAME) || tagName.equals(TAG_IFRAME)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_EMBED)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BGSOUND)){
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_LINK)) {
                 // Putting the string first means it works even if the attribute is null
                 if (STYLESHEET.equalsIgnoreCase(tag.attr(ATT_REL))) {
                     extractAttribute(tag, ATT_HREF);
                 }
             } else {
                 extractAttribute(tag, ATT_BACKGROUND);
             }
 
 
             // Now look for URLs in the STYLE attribute
             String styleTagStr = tag.attr(ATT_STYLE);
             if(styleTagStr != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
             }
         }
 
         @Override
         public void tail(Node arg0, int arg1) {
             // Noop
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             // TODO Handle conditional comments for IE
             String contents = new String(html,encoding);
             Document doc = Jsoup.parse(contents);
             JMeterNodeVisitor nodeVisitor = new JMeterNodeVisitor(new URLPointer(baseUrl), coll);
             new NodeTraversor(nodeVisitor).traverse(doc);
             return coll.iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
index 1f45ff6ef..a116ff21e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
@@ -1,234 +1,233 @@
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
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Stack;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.slf4j.Logger;
 
 import jodd.lagarto.EmptyTagVisitor;
 import jodd.lagarto.LagartoException;
 import jodd.lagarto.LagartoParser;
 import jodd.lagarto.LagartoParserConfig;
 import jodd.lagarto.Tag;
 import jodd.lagarto.TagType;
 import jodd.lagarto.TagUtil;
 import jodd.lagarto.dom.HtmlCCommentExpressionMatcher;
 import jodd.log.LoggerFactory;
 import jodd.log.impl.Slf4jLoggerFactory;
 
 /**
  * Parser based on Lagarto
  * @since 2.10
  */
 public class LagartoBasedHtmlParser extends HTMLParser {
     private static final Logger log = org.slf4j.LoggerFactory.getLogger(LagartoBasedHtmlParser.class);
     static {
         LoggerFactory.setLoggerFactory(new Slf4jLoggerFactory());
     }
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
     
     private static final class JMeterTagVisitor extends EmptyTagVisitor {
         private HtmlCCommentExpressionMatcher htmlCCommentExpressionMatcher;
         private URLCollection urls;
         private URLPointer baseUrl;
         private Float ieVersion;
         private Stack<Boolean> enabled = new Stack<>();
 
         /**
          * @param baseUrl base url to add possibly missing information to urls found in <code>urls</code>
          * @param urls collection of urls to consider
          * @param ieVersion version number of IE to emulate
          */
         public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls, Float ieVersion) {
             this.urls = urls;
             this.baseUrl = baseUrl;
             this.ieVersion = ieVersion;
         }
 
         private void extractAttribute(Tag tag, String attributeName) {
             CharSequence url = tag.getAttributeValue(attributeName);
-            if (!StringUtils.isEmpty(url)) {
-                String trimmed = url.toString().trim().replaceAll("[\n\r\b\f]+", "");
-                if (!trimmed.isEmpty()) {
-                    urls.addURL(trimmed, baseUrl.url);
-                }
+            String normalizedUrl = normalizeUrlValue(url);
+            if(normalizedUrl != null) {
+                urls.addURL(normalizedUrl, baseUrl.url);
             }
         }
+        
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#script(jodd.lagarto.Tag,
          * java.lang.CharSequence)
          */
         @Override
         public void script(Tag tag, CharSequence body) {
             if (!enabled.peek().booleanValue()) {
                 return;
             }
             extractAttribute(tag, ATT_SRC);
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#tag(jodd.lagarto.Tag)
          */
         @Override
         public void tag(Tag tag) {
             if (!enabled.peek().booleanValue()) {
                 return;
             }
             TagType tagType = tag.getType();
             switch (tagType) {
             case START:
             case SELF_CLOSING:
                 if (tag.nameEquals(TAG_BODY)) {
                     extractAttribute(tag, ATT_BACKGROUND);
                 } else if (tag.nameEquals(TAG_BASE)) {
                     CharSequence baseref = tag.getAttributeValue(ATT_HREF);
                     try {
                         if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                         {
                             baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref.toString());
                         }
                     } catch (MalformedURLException e1) {
                         throw new RuntimeException(e1);
                     }
                 } else if (tag.nameEquals(TAG_IMAGE)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_APPLET)) {
                     extractAttribute(tag, ATT_CODE);
                 } else if (tag.nameEquals(TAG_OBJECT)) {
                     extractAttribute(tag, ATT_CODEBASE);                
                     extractAttribute(tag, ATT_DATA);                 
                 } else if (tag.nameEquals(TAG_INPUT)) {
                     // we check the input tag type for image
                     CharSequence type = tag.getAttributeValue(ATT_TYPE);
                     if (type != null && TagUtil.equalsIgnoreCase(ATT_IS_IMAGE, type)) {
                         // then we need to download the binary
                         extractAttribute(tag, ATT_SRC);
                     }
                 } else if (tag.nameEquals(TAG_SCRIPT)) {
                     extractAttribute(tag, ATT_SRC);
                     // Bug 51750
                 } else if (tag.nameEquals(TAG_FRAME) || tag.nameEquals(TAG_IFRAME)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_EMBED)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_BGSOUND)){
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_LINK)) {
                     CharSequence relAttribute = tag.getAttributeValue(ATT_REL);
                     // Putting the string first means it works even if the attribute is null
                     if (relAttribute != null && TagUtil.equalsIgnoreCase(STYLESHEET,relAttribute)) {
                         extractAttribute(tag, ATT_HREF);
                     }
                 } else {
                     extractAttribute(tag, ATT_BACKGROUND);
                 }
     
     
                 // Now look for URLs in the STYLE attribute
                 CharSequence styleTagStr = tag.getAttributeValue(ATT_STYLE);
                 if(!StringUtils.isEmpty(styleTagStr)) {
                     HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr.toString());
                 }
                 break;
             case END:
                 break;
             default:
                 throw new IllegalStateException("Unexpected tagType " + tagType);
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#condComment(java.lang.CharSequence, boolean, boolean, boolean)
          */
         @Override
         public void condComment(CharSequence expression, boolean isStartingTag,
                 boolean isHidden, boolean isHiddenEndTag) {
             // See http://css-tricks.com/how-to-create-an-ie-only-stylesheet/
             if(!isStartingTag) {
                 enabled.pop();
             } else {
                 if (htmlCCommentExpressionMatcher == null) {
                     htmlCCommentExpressionMatcher = new HtmlCCommentExpressionMatcher();
                 }
                 String expressionString = expression.toString().trim();
                 enabled.push(Boolean.valueOf(htmlCCommentExpressionMatcher.match(ieVersion.floatValue(),
                         expressionString)));
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#start()
          */
         @Override
         public void start() {
             super.start();
             enabled.clear();
             enabled.push(Boolean.TRUE);
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             Float ieVersion = extractIEVersion(userAgent);
             
             String contents = new String(html,encoding); 
             // As per Jodd javadocs, emitStrings should be false for visitor for better performances
             LagartoParser lagartoParser = new LagartoParser(contents, false);
             LagartoParserConfig<LagartoParserConfig<?>> config = new LagartoParserConfig<>();
             config.setCaseSensitive(false);
             // Conditional comments only apply for IE < 10
             config.setEnableConditionalComments(isEnableConditionalComments(ieVersion));
             
             lagartoParser.setConfig(config);
             JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll, ieVersion);
             lagartoParser.parse(tagVisitor);
             return coll.iterator();
         } catch (LagartoException e) {
             // TODO is it the best way ? https://bz.apache.org/bugzilla/show_bug.cgi?id=55634
             if(log.isDebugEnabled()) {
                 log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
             }
             return Collections.<URL>emptyList().iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 }
diff --git a/test/src/org/apache/jmeter/protocol/http/parser/TestJSoupBasedHtmlParser.java b/test/src/org/apache/jmeter/protocol/http/parser/TestJSoupBasedHtmlParser.java
new file mode 100644
index 000000000..42bd99f14
--- /dev/null
+++ b/test/src/org/apache/jmeter/protocol/http/parser/TestJSoupBasedHtmlParser.java
@@ -0,0 +1,94 @@
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
+import static org.junit.Assert.assertThat;
+
+import java.net.URL;
+import java.nio.charset.StandardCharsets;
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.Collections;
+import java.util.List;
+import java.util.stream.Collectors;
+
+import org.hamcrest.CoreMatchers;
+import org.junit.Test;
+import org.junit.runner.RunWith;
+import org.junit.runners.Parameterized;
+import org.junit.runners.Parameterized.Parameters;
+
+@RunWith(Parameterized.class)
+public class TestJSoupBasedHtmlParser {
+    
+    private List<String> links;
+    private String html;
+
+    public TestJSoupBasedHtmlParser(String html, String links) {
+        this.html = html;
+        if (links.isEmpty()) {
+            this.links = Collections.emptyList();
+        } else {
+            this.links = Arrays.asList(links.split(","));
+        }
+    }
+
+    @Parameters
+    public static Object[][] params() {
+        return new Object[][] {
+                new Object[] {"<body background='abc.png'/>", "http://example.org/abc.png"},
+                new Object[] {"<link href='abc.css' rel='stylesheet'/>", "http://example.org/abc.css"},
+                new Object[] {"<img src='abc.png'/>", "http://example.org/abc.png"},
+                new Object[] {"<base href='http://another.org'/><img src='one.png'/>", "http://another.org/one.png"},
+                new Object[] {"<applet code='abc.jar'/>", "http://example.org/abc.jar"},
+                new Object[] {"<object codebase='abc.jar' data='something'/>", "http://example.org/abc.jar,http://example.org/something"},
+                new Object[] {"<object data='something'/>", "http://example.org/something"},
+                new Object[] {"<object codebase='abc.jar'/>", "http://example.org/abc.jar"},
+                new Object[] {"<input type='image' src='foo'/>", "http://example.org/foo"},
+                new Object[] {"<input type='text' src='foo'/>", ""},
+                // JSOUP Fails to parse this one
+//                new Object[] {"<frame src='foo'/>", "http://example.org/foo"},
+                new Object[] {"<iframe src='foo'/>", "http://example.org/foo"},
+                new Object[] {"<embed src='foo'/>", "http://example.org/foo"},
+                new Object[] {"<bgsound src='foo'/>", "http://example.org/foo"},
+                new Object[] {"<anytag background='foo'/>", "http://example.org/foo"},
+                new Object[] {"<anytag style='foo: url(\"bar\")'/>", "http://example.org/bar"},
+                new Object[] {"<anytag style=\"foo: url('bar')'\"/>", "http://example.org/bar"},
+                // new Object[] {"<anytag style=\"foo: url(bar)'\"/>", "http://example.org/bar"},
+                // new Object[] {"<anytag style=\"foo: url(bar)'; other: url(something);\"/>", "http://example.org/bar,http://example.org/something"},
+                new Object[] {"<link href='  abc\n.css  ' rel='stylesheet'/>", "http://example.org/abc.css"},
+                new Object[] {"<link href='  with spaces\n.css  ' rel='stylesheet'/>", "http://example.org/with spaces.css"},
+                new Object[] {"<embed src=''/>", ""},
+                new Object[] {"<embed src='  '/>", ""},
+        };
+    }
+    
+    @Test
+    public void testJsoupGetEmbeddedResourceURLsStringByteArrayURLURLCollectionString() throws Exception {
+        HTMLParser parser = new JsoupBasedHtmlParser();
+        final ArrayList<URLString> c = new ArrayList<>();
+        parser.getEmbeddedResourceURLs("Mozilla",
+                html.getBytes(StandardCharsets.UTF_8),
+                new URL("http://example.org"), new URLCollection(c),
+                StandardCharsets.UTF_8.name());
+        List<String> urlNames = c.stream().map(u -> u.toString()).collect(Collectors.toList());
+        assertThat(urlNames, CoreMatchers.is(CoreMatchers.equalTo(links)));
+    }
+
+}
