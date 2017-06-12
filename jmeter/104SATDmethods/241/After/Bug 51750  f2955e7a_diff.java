diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 7b82b9439..22a42bb74 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,203 +1,204 @@
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
+    protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
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
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<URLString>();
         return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(col));
 
         // An additional note on using HashSets to store URLs: I just
         // discovered that obtaining the hashCode of a java.net.URL implies
         // a domain-name resolution process. This means significant delays
         // can occur, even more so if the domain name is not resolvable.
         // Whether this can be a problem in practical situations I can't tell,
         // but
         // thought I'd keep a note just in case...
         // BTW, note that using a Vector and removing duplicates via scan
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
      * @return an Iterator for the resource URLs
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection coll)
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
      * @return an Iterator for the resource URLs
      */
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, Collection<URLString> coll) throws HTMLParseException {
         return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(coll));
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
index d06e1dd33..a4c1fa3b1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
@@ -1,197 +1,198 @@
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
 import org.htmlparser.tags.LinkTag;
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
     public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls) throws HTMLParseException {
 
         if (log.isDebugEnabled()) {
             log.debug("Parsing html of: " + baseUrl);
         }
 
         Parser htmlParser = null;
         try {
             String contents = new String(html); // TODO - charset?
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
             } else if (tag instanceof InputTag) {
                 // we check the input tag type for image
                 if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttribute(ATT_TYPE))) {
                     // then we need to download the binary
                     binUrlStr = tag.getAttribute(ATT_SRC);
                 }
             } else if (tag instanceof LinkTag) {
                 LinkTag link = (LinkTag) tag;
                 if (link.getChild(0) instanceof ImageTag) {
                     ImageTag img = (ImageTag) link.getChild(0);
                     binUrlStr = img.getImageURL();
                 }
             } else if (tag instanceof ScriptTag) {
                 binUrlStr = tag.getAttribute(ATT_SRC);
-            } else if (tag instanceof FrameTag) {
+                // Bug 51750
+            } else if (tag instanceof FrameTag || tagname.equalsIgnoreCase(TAG_IFRAME)) {
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b18ef5926..020ff228b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,141 +1,142 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h1>Version 2.5.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
+<li>Bug 51750 - Retrieve all embedded resources doesn't follow IFRAME</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
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
 
 <!-- ==================================================== -->
 
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
 </ul>
 
 </section> 
 </body> 
 </document>
