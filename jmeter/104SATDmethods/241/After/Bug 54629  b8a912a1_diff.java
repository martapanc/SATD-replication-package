diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 49c204348..9735bd4f4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,207 +1,208 @@
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
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
+    protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     // Cache of parsers - parsers must be re-usable
     private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(3);
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser"; // $NON-NLS-1$
 
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
index bc865416e..bf6ec47bc 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
@@ -1,191 +1,206 @@
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
 
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.htmlparser.Node;
 import org.htmlparser.Parser;
 import org.htmlparser.Tag;
 import org.htmlparser.tags.AppletTag;
 import org.htmlparser.tags.BaseHrefTag;
 import org.htmlparser.tags.BodyTag;
 import org.htmlparser.tags.CompositeTag;
 import org.htmlparser.tags.FrameTag;
 import org.htmlparser.tags.ImageTag;
 import org.htmlparser.tags.InputTag;
+import org.htmlparser.tags.ObjectTag;
 import org.htmlparser.tags.ScriptTag;
 import org.htmlparser.util.NodeIterator;
 import org.htmlparser.util.ParserException;
 
 /**
  * HtmlParser implementation using SourceForge's HtmlParser.
  *
  */
 class HtmlParserHTMLParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     static{
         org.htmlparser.scanners.ScriptScanner.STRICT = false; // Try to ensure that more javascript code is processed OK ...
     }
 
     protected HtmlParserHTMLParser() {
         super();
         log.info("Using htmlparser version: "+Parser.getVersion());
     }
 
     @Override
     protected boolean isReusable() {
         return true;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
 
         if (log.isDebugEnabled()) {
             log.debug("Parsing html of: " + baseUrl);
         }
 
         Parser htmlParser = null;
         try {
             String contents = new String(html,encoding); 
             htmlParser = new Parser();
             htmlParser.setInputHTML(contents);
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
 
         // Now parse the DOM tree
         try {
             // we start to iterate through the elements
             parseNodes(htmlParser.elements(), new URLPointer(baseUrl), urls);
             log.debug("End   : parseNodes");
         } catch (ParserException e) {
             throw new HTMLParseException(e);
         }
 
         return urls.iterator();
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
 
     /**
      * Recursively parse all nodes to pick up all URL s.
      * @see e the nodes to be parsed
      * @see baseUrl Base URL from which the HTML code was obtained
      * @see urls URLCollection
      */
     private void parseNodes(final NodeIterator e,
             final URLPointer baseUrl, final URLCollection urls)
         throws HTMLParseException, ParserException {
         while(e.hasMoreNodes()) {
             Node node = e.nextNode();
             // a url is always in a Tag.
             if (!(node instanceof Tag)) {
                 continue;
             }
             Tag tag = (Tag) node;
             String tagname=tag.getTagName();
             String binUrlStr = null;
 
             // first we check to see if body tag has a
             // background set
             if (tag instanceof BodyTag) {
                 binUrlStr = tag.getAttribute(ATT_BACKGROUND);
             } else if (tag instanceof BaseHrefTag) {
                 BaseHrefTag baseHref = (BaseHrefTag) tag;
                 String baseref = baseHref.getBaseUrl();
                 try {
                     if (!baseref.equals(""))// Bugzilla 30713
                     {
                         baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseHref.getBaseUrl());
                     }
                 } catch (MalformedURLException e1) {
                     throw new HTMLParseException(e1);
                 }
             } else if (tag instanceof ImageTag) {
                 ImageTag image = (ImageTag) tag;
                 binUrlStr = image.getImageURL();
             } else if (tag instanceof AppletTag) {
                 // look for applets
 
                 // This will only work with an Applet .class file.
                 // Ideally, this should be upgraded to work with Objects (IE)
                 // and archives (.jar and .zip) files as well.
                 AppletTag applet = (AppletTag) tag;
                 binUrlStr = applet.getAppletClass();
+            } else if (tag instanceof ObjectTag) {
+                // look for Objects
+                ObjectTag applet = (ObjectTag) tag; 
+                String data = applet.getAttribute("codebase");
+                if(!StringUtils.isEmpty(data)) {
+                    binUrlStr = data;               
+                }
+                
+                data = applet.getAttribute("data");
+                if(!StringUtils.isEmpty(data)) {
+                    binUrlStr = data;                    
+                }
+                
             } else if (tag instanceof InputTag) {
                 // we check the input tag type for image
                 if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttribute(ATT_TYPE))) {
                     // then we need to download the binary
                     binUrlStr = tag.getAttribute(ATT_SRC);
                 }
             } else if (tag instanceof ScriptTag) {
                 binUrlStr = tag.getAttribute(ATT_SRC);
                 // Bug 51750
             } else if (tag instanceof FrameTag || tagname.equalsIgnoreCase(TAG_IFRAME)) {
                 binUrlStr = tag.getAttribute(ATT_SRC);
             } else if (tagname.equalsIgnoreCase(TAG_EMBED)
                 || tagname.equalsIgnoreCase(TAG_BGSOUND)){
                 binUrlStr = tag.getAttribute(ATT_SRC);
             } else if (tagname.equalsIgnoreCase(TAG_LINK)) {
                 // Putting the string first means it works even if the attribute is null
                 if (STYLESHEET.equalsIgnoreCase(tag.getAttribute(ATT_REL))) {
                     binUrlStr = tag.getAttribute(ATT_HREF);
                 }
             } else {
                 binUrlStr = tag.getAttribute(ATT_BACKGROUND);
             }
 
             if (binUrlStr != null) {
                 urls.addURL(binUrlStr, baseUrl.url);
             }
 
             // Now look for URLs in the STYLE attribute
             String styleTagStr = tag.getAttribute(ATT_STYLE);
             if(styleTagStr != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
             }
 
             // second, if the tag was a composite tag,
             // recursively parse its children.
             if (tag instanceof CompositeTag) {
                 CompositeTag composite = (CompositeTag) tag;
                 parseNodes(composite.elements(), baseUrl, urls);
             }
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
index 5e2803550..801624fa0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
@@ -1,233 +1,248 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.SAXException;
 
 /**
  * HtmlParser implementation using JTidy.
  *
  */
 class JTidyHTMLParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected JTidyHTMLParser() {
         super();
     }
 
     @Override
     protected boolean isReusable() {
         return true;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
         Document dom = null;
         try {
             dom = (Document) getDOM(html, encoding);
         } catch (SAXException se) {
             throw new HTMLParseException(se);
         }
 
         // Now parse the DOM tree
 
         scanNodes(dom, urls, baseUrl);
 
         return urls.iterator();
     }
 
     /**
      * Scan nodes recursively, looking for embedded resources
      *
      * @param node -
      *            initial node
      * @param urls -
      *            container for URLs
      * @param baseUrl -
      *            used to create absolute URLs
      *
      * @return new base URL
      */
     private URL scanNodes(Node node, URLCollection urls, URL baseUrl) throws HTMLParseException {
         if (node == null) {
             return baseUrl;
         }
 
         String name = node.getNodeName();
 
         int type = node.getNodeType();
 
         switch (type) {
 
         case Node.DOCUMENT_NODE:
             scanNodes(((Document) node).getDocumentElement(), urls, baseUrl);
             break;
 
         case Node.ELEMENT_NODE:
 
             NamedNodeMap attrs = node.getAttributes();
             if (name.equalsIgnoreCase(TAG_BASE)) {
                 String tmp = getValue(attrs, ATT_HREF);
                 if (tmp != null) {
                     try {
                         baseUrl = ConversionUtils.makeRelativeURL(baseUrl, tmp);
                     } catch (MalformedURLException e) {
                         throw new HTMLParseException(e);
                     }
                 }
                 break;
             }
 
             if (name.equalsIgnoreCase(TAG_IMAGE) || name.equalsIgnoreCase(TAG_EMBED)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
 
             if (name.equalsIgnoreCase(TAG_APPLET)) {
                 urls.addURL(getValue(attrs, "code"), baseUrl);
                 break;
             }
+            
+            if (name.equalsIgnoreCase(TAG_OBJECT)) {
+                String data = getValue(attrs, "codebase");
+                if(!StringUtils.isEmpty(data)) {
+                    urls.addURL(data, baseUrl);                    
+                }
+                
+                data = getValue(attrs, "data");
+                if(!StringUtils.isEmpty(data)) {
+                    urls.addURL(data, baseUrl);                    
+                }
+                break;
+            }
+            
             if (name.equalsIgnoreCase(TAG_INPUT)) {
                 String src = getValue(attrs, ATT_SRC);
                 String typ = getValue(attrs, ATT_TYPE);
                 if ((src != null) && (typ.equalsIgnoreCase(ATT_IS_IMAGE))) {
                     urls.addURL(src, baseUrl);
                 }
                 break;
             }
             if (name.equalsIgnoreCase(TAG_LINK) && getValue(attrs, ATT_REL).equalsIgnoreCase(STYLESHEET)) {
                 urls.addURL(getValue(attrs, ATT_HREF), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_SCRIPT)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_FRAME)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_IFRAME)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             String back = getValue(attrs, ATT_BACKGROUND);
             if (back != null) {
                 urls.addURL(back, baseUrl);
             }
             if (name.equalsIgnoreCase(TAG_BGSOUND)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
 
             String style = getValue(attrs, ATT_STYLE);
             if (style != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl, urls, style);
             }
 
             NodeList children = node.getChildNodes();
             if (children != null) {
                 int len = children.getLength();
                 for (int i = 0; i < len; i++) {
                     baseUrl = scanNodes(children.item(i), urls, baseUrl);
                 }
             }
 
             break;
 
         // case Node.TEXT_NODE:
         // break;
 
         default:
             // ignored
             break;
         }
 
         return baseUrl;
 
     }
 
     /*
      * Helper method to get an attribute value, if it exists @param attrs list
      * of attributs @param attname attribute name @return
      */
     private String getValue(NamedNodeMap attrs, String attname) {
         String v = null;
         Node n = attrs.getNamedItem(attname);
         if (n != null) {
             v = n.getNodeValue();
         }
         return v;
     }
 
     /**
      * Returns <code>tidy</code> as HTML parser.
      *
      * @return a <code>tidy</code> HTML parser
      */
     private static Tidy getTidyParser(String encoding) {
         log.debug("Start : getParser");
         Tidy tidy = new Tidy();
         tidy.setInputEncoding(encoding);
         tidy.setOutputEncoding("UTF8");
         tidy.setQuiet(true);
         tidy.setShowWarnings(false);
         if (log.isDebugEnabled()) {
             log.debug("getParser : tidy parser created - " + tidy);
         }
         log.debug("End   : getParser");
         return tidy;
     }
 
     /**
      * Returns a node representing a whole xml given an xml document.
      *
      * @param text
      *            an xml document (as a byte array)
      * @return a node representing a whole xml
      *
      * @throws SAXException
      *             indicates an error parsing the xml document
      */
     private static Node getDOM(byte[] text, String encoding) throws SAXException {
         log.debug("Start : getDOM");
         Node node = getTidyParser(encoding).parseDOM(new ByteArrayInputStream(text), null);
         if (log.isDebugEnabled()) {
             log.debug("node : " + node);
         }
         log.debug("End   : getDOM");
         return node;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
index b3210b6b9..14e1e316a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
@@ -1,202 +1,202 @@
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
 
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * HtmlParser implementation using regular expressions.
  * <p>
  * This class will find RLs specified in the following ways (where <b>url</b>
  * represents the RL being found:
  * <ul>
  * <li>&lt;img src=<b>url</b> ... &gt;
  * <li>&lt;script src=<b>url</b> ... &gt;
  * <li>&lt;applet code=<b>url</b> ... &gt;
  * <li>&lt;input type=image src=<b>url</b> ... &gt;
  * <li>&lt;body background=<b>url</b> ... &gt;
  * <li>&lt;table background=<b>url</b> ... &gt;
  * <li>&lt;td background=<b>url</b> ... &gt;
  * <li>&lt;tr background=<b>url</b> ... &gt;
  * <li>&lt;applet ... codebase=<b>url</b> ... &gt;
  * <li>&lt;embed src=<b>url</b> ... &gt;
  * <li>&lt;embed codebase=<b>url</b> ... &gt;
  * <li>&lt;object codebase=<b>url</b> ... &gt;
  * <li>&lt;link rel=stylesheet href=<b>url</b>... gt;
  * <li>&lt;bgsound src=<b>url</b> ... &gt;
  * <li>&lt;frame src=<b>url</b> ... &gt;
  * </ul>
  *
  * <p>
  * This class will take into account the following construct:
  * <ul>
  * <li>&lt;base href=<b>url</b>&gt;
  * </ul>
  *
  * <p>
  * But not the following:
  * <ul>
  * <li>&lt; ... codebase=<b>url</b> ... &gt;
  * </ul>
  *
  */
 class RegexpHTMLParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Regexp fragment matching a tag attribute's value (including the equals
      * sign and any spaces before it). Note it matches unquoted values, which to
      * my understanding, are not conformant to any of the HTML specifications,
      * but are still quite common in the web and all browsers seem to understand
      * them.
      */
     private static final String VALUE = "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'\\s>\\\\][^\\s>]*)(?=[\\s>]))";
 
     // Note there's 3 capturing groups per value
 
     /**
      * Regexp fragment matching the separation between two tag attributes.
      */
     private static final String SEP = "\\s(?:[^>]*\\s)?";
 
     /**
      * Regular expression used against the HTML code to find the URIs of images,
      * etc.:
      */
     private static final String REGEXP =
               "<(?:" + "!--.*?-->"
             + "|BASE" + SEP + "HREF" + VALUE
             + "|(?:IMG|SCRIPT|FRAME|IFRAME|BGSOUND)" + SEP + "SRC" + VALUE
             + "|APPLET" + SEP + "CODE(?:BASE)?" + VALUE
-            + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE)" + VALUE
+            + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE|DATA)" + VALUE
             + "|(?:BODY|TABLE|TR|TD)" + SEP + "BACKGROUND" + VALUE
             + "|[^<]+?STYLE\\s*=['\"].*?URL\\(\\s*['\"](.+?)['\"]\\s*\\)"
             + "|INPUT(?:" + SEP + "(?:SRC" + VALUE
             + "|TYPE\\s*=\\s*(?:\"image\"|'image'|image(?=[\\s>])))){2,}"
             + "|LINK(?:" + SEP + "(?:HREF" + VALUE
             + "|REL\\s*=\\s*(?:\"stylesheet\"|'stylesheet'|stylesheet(?=[\\s>])))){2,}" + ")";
 
     // Number of capturing groups possibly containing Base HREFs:
     private static final int NUM_BASE_GROUPS = 3;
 
     /**
      * Thread-local input:
      */
     private static final ThreadLocal<PatternMatcherInput> localInput =
         new ThreadLocal<PatternMatcherInput>() {
         @Override
         protected PatternMatcherInput initialValue() {
             return new PatternMatcherInput(new char[0]);
         }
     };
 
     /**
      * {@inheritDoc}
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 
     /**
      * Make sure to compile the regular expression upon instantiation:
      */
     protected RegexpHTMLParser() {
         super();
     }
 
     /**
      * {@inheritDoc}
      * @throws HTMLParseException 
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
         Pattern pattern= null;
         Perl5Matcher matcher = null;
         try {
             matcher = JMeterUtils.getMatcher();
 			PatternMatcherInput input = localInput.get();
 			// TODO: find a way to avoid the cost of creating a String here --
 			// probably a new PatternMatcherInput working on a byte[] would do
 			// better.
 			input.setInput(new String(html, encoding)); 
 			pattern=JMeterUtils.getPatternCache().getPattern(
 			        REGEXP,
 			        Perl5Compiler.CASE_INSENSITIVE_MASK
 			        | Perl5Compiler.SINGLELINE_MASK
 			        | Perl5Compiler.READ_ONLY_MASK);
 
 			while (matcher.contains(input, pattern)) {
 			    MatchResult match = matcher.getMatch();
 			    String s;
 			    if (log.isDebugEnabled()) {
 			        log.debug("match groups " + match.groups() + " " + match.toString());
 			    }
 			    // Check for a BASE HREF:
 			    for (int g = 1; g <= NUM_BASE_GROUPS && g <= match.groups(); g++) {
 			        s = match.group(g);
 			        if (s != null) {
 			            if (log.isDebugEnabled()) {
 			                log.debug("new baseUrl: " + s + " - " + baseUrl.toString());
 			            }
 			            try {
 			                baseUrl = ConversionUtils.makeRelativeURL(baseUrl, s);
 			            } catch (MalformedURLException e) {
 			                // Doesn't even look like a URL?
 			                // Maybe it isn't: Ignore the exception.
 			                if (log.isDebugEnabled()) {
 			                    log.debug("Can't build base URL from RL " + s + " in page " + baseUrl, e);
 			                }
 			            }
 			        }
 			    }
 			    for (int g = NUM_BASE_GROUPS + 1; g <= match.groups(); g++) {
 			        s = match.group(g);
 			        if (s != null) {
 			            if (log.isDebugEnabled()) {
 			                log.debug("group " + g + " - " + match.group(g));
 			            }
 			            urls.addURL(s, baseUrl);
 			        }
 			    }
 			}
 			return urls.iterator();
 		} catch (UnsupportedEncodingException e) {
 			throw new HTMLParseException(e.getMessage(), e);
 		} catch (MalformedCachePatternException e) {
 			throw new HTMLParseException(e.getMessage(), e);
 		} finally {
             JMeterUtils.clearMatcherMemory(matcher, pattern);
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b7c1035b0..e5a0a1541 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,178 +1,179 @@
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
 
 
 <!--  =================== 2.9 =================== -->
 
 <h1>Version 2.9</h1>
 
 <h2>New and Noteworthy</h2>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </p>
 
 <p>
 Changing language can break part of the configuration of the following elements (see <bugzilla>53679</bugzilla>):
 <ul>
     <li>CSV Data Set Config (sharing mode will be lost)</li>
     <li>Constant Throughput Timer (Calculate throughput based on will be lost)</li>
 </ul>
 </p>
 
 <p>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
 </p>
 
 <p>
 Note that there is a bug in Java on some Linux systems that manifests
 itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation.
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>54482</bugzilla> - HC fails to follow redirects with non-encoded chars</li>
 <li><bugzilla>54293</bugzilla> - JMeter rejects html tags '&lt;' in query params as invalid when they are accepted by the browser</li>
 <li><bugzilla>54142</bugzilla> - HTTP Proxy Server throws an exception when path contains "|" character </li>
 <li><bugzilla>54627</bugzilla> - JMeter Proxy GUI: Type of sampler settings takes the whole screen with when there are samplers with long name</li>
+<li><bugzilla>54629</bugzilla> 54629 - HTMLParser does not extract &lt;object&gt; tag urls</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54467</bugzilla> - Loop controller Controller check conditions each request</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54589</bugzilla> - View Results Tree have a lot of Garbage characters if html page uses double-byte charset</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54540</bugzilla> - "HTML Parameter Mask" are not marked deprecated in the IHM</li>
 <li><bugzilla>54575</bugzilla> - CSS/JQuery Extractor : Choosing JODD Implementation always uses JSOUP</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54504</bugzilla> - Resource string not found: [clipboard_node_read_error]</li>
 <li><bugzilla>54538</bugzilla> - GUI: context menu is too big</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54532</bugzilla> - Improve Response Time Graph Y axis scale with huge values or small values (&lt; 1000ms). Add a new field to define increment scale</li>
 <li><bugzilla>54576</bugzilla> - View Results Tree : Add a CSS/JQuery Tester</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Updated to jsoup-1.7.2</li>
 </ul>
 
 </section> 
 </body> 
 </document>
