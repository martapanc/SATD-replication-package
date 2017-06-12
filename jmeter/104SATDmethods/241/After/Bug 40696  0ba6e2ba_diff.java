diff --git a/bin/testfiles/HTMLParserTestCase.html b/bin/testfiles/HTMLParserTestCase.html
index 0f593c3eb..b520ed310 100644
--- a/bin/testfiles/HTMLParserTestCase.html
+++ b/bin/testfiles/HTMLParserTestCase.html
@@ -1,45 +1,45 @@
 <HTML>
 <head>
 </head>
 <body background="images/body.gif">
 <table background="images/table.gif">
-<tr background="images/tr.gif">
+<tr style="background url('images/tr.gif')">
 <td background="images/td.gif"><img name="a" src="images/image-a.gif" align="top"></td>
 </tr>
 </table>
 <img src="images/image-b.gif" name="b" align="top"/>
 <img src="images/image-b.gif" name="b" align="top"/>
 <img align="top" name="c" src= "images/image-c.gif">
 <img name="d" align="top" src ="images/image-d.gif"/>
 <img
   align="top"
   name="e"
   src =
     "images/image-e.gif"
 >
 <img
   align="top"
   Name="F"
   sRc = "images/sub/image-f.gif"
 />
 <input name="a" src="images/image-a2.gif" type="image"/>
 <input src="images/image-b2.gif" name="b" type="image">
 <input type= "image" name="c" src= "images/sub/image-c2.gif"/>
 <input name="d" type ="image" src ="images/image-d2.gif">
 <input name="d2" type ="image" src ="images/image-d2.gif">
 <input name="d3" type ="imagex" src ="images/image-d2.gif">
 <input
   type =
     "image"
   name="e"
   src =
     "images/image-e2.gif"
 />
 <input
   type = "image"
   Name="F"
   sRc = "images/image-f2.gif"
 >
 
 </body>
 </html>
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 138e9cb53..b9e009658 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,230 +1,231 @@
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
 import java.util.Hashtable;
 import java.util.Iterator;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HtmlParsers can parse HTML content to obtain URLs.
  * 
  * @author <a href="mailto:jsalvata@apache.org">Jordi Salvat i Alabart</a>
  * @version $Revision$ updated on $Date$
  */
 public abstract class HTMLParser {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
+    protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
 	// Cache of parsers - parsers must be re-usable
 	private static Hashtable parsers = new Hashtable(3);
 
 	public final static String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
 	public final static String DEFAULT_PARSER = 
         "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser"; // $NON-NLS-1$
 
     private static final String JAVA_UTIL_LINKED_HASH_SET = "java.util.LinkedHashSet"; // $NON-NLS-1$
 
 	/**
 	 * Protected constructor to prevent instantiation except from within
 	 * subclasses.
 	 */
 	protected HTMLParser() {
 	}
 
 	public static final HTMLParser getParser() {
 		return getParser(JMeterUtils.getPropDefault(PARSER_CLASSNAME, DEFAULT_PARSER));
 	}
 
 	public static final synchronized HTMLParser getParser(String htmlParserClassName) {
 
 		// Is there a cached parser?
 		HTMLParser pars = (HTMLParser) parsers.get(htmlParserClassName);
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
 	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl) throws HTMLParseException {
 		// The Set is used to ignore duplicated binary files.
 		// Using a LinkedHashSet to avoid unnecessary overhead in iterating
 		// the elements in the set later on. As a side-effect, this will keep
 		// them roughly in order, which should be a better model of browser
 		// behaviour.
 
 		Collection col;
 
 		// N.B. LinkedHashSet is Java 1.4
 		if (hasLinkedHashSet) {
 			try {
 				col = (Collection) Class.forName(JAVA_UTIL_LINKED_HASH_SET).newInstance();
 			} catch (Exception e) {
 				throw new Error("Should not happen:" + e.toString());
 			}
 		} else {
 			col = new java.util.HashSet(); // TODO: improve JDK1.3 solution
 		}
 
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
 
 	// See whether we can use LinkedHashSet or not:
 	private static final boolean hasLinkedHashSet;
 
 	static {
 		boolean b;
 		try {
 			Class.forName(JAVA_UTIL_LINKED_HASH_SET);
 			b = true;
 		} catch (ClassNotFoundException e) {
 			b = false;
 		}
 		hasLinkedHashSet = b;
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
 	public abstract Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection coll)
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
 	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, Collection coll) throws HTMLParseException {
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
index d72994c48..9cee1537b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
@@ -1,187 +1,195 @@
-/*
- * Licensed to the Apache Software Foundation (ASF) under one or more
- * contributor license agreements.  See the NOTICE file distributed with
- * this work for additional information regarding copyright ownership.
- * The ASF licenses this file to You under the Apache License, Version 2.0
- * (the "License"); you may not use this file except in compliance with
- * the License.  You may obtain a copy of the License at
- *
- *   http://www.apache.org/licenses/LICENSE-2.0
- *
- * Unless required by applicable law or agreed to in writing, software
- * distributed under the License is distributed on an "AS IS" BASIS,
- * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- * See the License for the specific language governing permissions and
- * limitations under the License.
- * 
- */
-
-package org.apache.jmeter.protocol.http.parser;
-
-import java.net.MalformedURLException;
-import java.net.URL;
-import java.util.Iterator;
-
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-
-import org.htmlparser.Node;
-import org.htmlparser.Parser;
-import org.htmlparser.Tag;
-import org.htmlparser.tags.AppletTag;
-import org.htmlparser.tags.BaseHrefTag;
-import org.htmlparser.tags.BodyTag;
-import org.htmlparser.tags.CompositeTag;
-import org.htmlparser.tags.FrameTag;
-import org.htmlparser.tags.ImageTag;
-import org.htmlparser.tags.InputTag;
-import org.htmlparser.tags.LinkTag;
-import org.htmlparser.tags.ScriptTag;
-import org.htmlparser.util.NodeIterator;
-import org.htmlparser.util.ParserException;
-
-/**
- * HtmlParser implementation using SourceForge's HtmlParser.
- * 
- */
-public class HtmlParserHTMLParser extends HTMLParser {
-    private static final Logger log = LoggingManager.getLoggerForClass();
-
-    static{
-    	org.htmlparser.scanners.ScriptScanner.STRICT = false; // Try to ensure that more javascript code is processed OK ...
-    }
-	protected HtmlParserHTMLParser() {
-		super();
-        log.info("Using htmlparser version 2.0");
-	}
-
-	protected boolean isReusable() {
-		return true;
-	}
-
-	/*
-	 * (non-Javadoc)
-	 * 
-	 * @see org.apache.jmeter.protocol.http.parser.HtmlParser#getEmbeddedResourceURLs(byte[],
-	 *      java.net.URL)
-	 */
-	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls) throws HTMLParseException {
-        
-        if (log.isDebugEnabled()) log.debug("Parsing html of: " + baseUrl);
-        
-        Parser htmlParser = null;
-		try {
-			String contents = new String(html);
-			htmlParser = new Parser();
-            htmlParser.setInputHTML(contents);
-		} catch (Exception e) {
-			throw new HTMLParseException(e);
-		}
-
-		// Now parse the DOM tree
-		try {
-			// we start to iterate through the elements
-			parseNodes(htmlParser.elements(), new URLPointer(baseUrl), urls);
-			log.debug("End   : parseNodes");
-		} catch (ParserException e) {
-			throw new HTMLParseException(e);
-		}
-
-		return urls.iterator();
-	}
-	
-    /*
-	 * A dummy class to pass the pointer of URL.
-	 */
-    private static class URLPointer {
-    	private URLPointer(URL newUrl) {
-    		url = newUrl;
-    	}
-    	private URL url;
-    }
-    
-    /**
-     * Recursively parse all nodes to pick up all URL s.
-     * @see e the nodes to be parsed
-     * @see baseUrl Base URL from which the HTML code was obtained
-     * @see urls URLCollection
-     */
-    private void parseNodes(final NodeIterator e,
-    		final URLPointer baseUrl, final URLCollection urls) 
-        throws HTMLParseException, ParserException {
-        while(e.hasMoreNodes()) {
-            Node node = e.nextNode();
-            // a url is always in a Tag.
-            if (!(node instanceof Tag)) {
-                continue;
-            }
-            Tag tag = (Tag) node;
-            String tagname=tag.getTagName();
-            String binUrlStr = null;
-
-            // first we check to see if body tag has a
-            // background set
-            if (tag instanceof BodyTag) {
-                binUrlStr = tag.getAttribute(ATT_BACKGROUND);
-            } else if (tag instanceof BaseHrefTag) {
-                BaseHrefTag baseHref = (BaseHrefTag) tag;
-                String baseref = baseHref.getBaseUrl().toString();
-                try {
-                    if (!baseref.equals(""))// Bugzilla 30713
-                    {
-                        baseUrl.url = new URL(baseUrl.url, baseHref.getBaseUrl());
-                    }
-                } catch (MalformedURLException e1) {
-                    throw new HTMLParseException(e1);
-                }
-            } else if (tag instanceof ImageTag) {
-                ImageTag image = (ImageTag) tag;
-                binUrlStr = image.getImageURL();
-            } else if (tag instanceof AppletTag) {
-        		// look for applets
-
-        		// This will only work with an Applet .class file.
-        		// Ideally, this should be upgraded to work with Objects (IE)
-        		// and archives (.jar and .zip) files as well.
-                AppletTag applet = (AppletTag) tag;
-                binUrlStr = applet.getAppletClass();
-            } else if (tag instanceof InputTag) {
-                // we check the input tag type for image
-                if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttribute(ATT_TYPE))) {
-                    // then we need to download the binary
-                    binUrlStr = tag.getAttribute(ATT_SRC);
-                }
-            } else if (tag instanceof LinkTag) {
-                LinkTag link = (LinkTag) tag;
-                if (link.getChild(0) instanceof ImageTag) {
-                    ImageTag img = (ImageTag) link.getChild(0);
-                    binUrlStr = img.getImageURL();
-                }
-            } else if (tag instanceof ScriptTag) {
-                binUrlStr = tag.getAttribute(ATT_SRC);
-            } else if (tag instanceof FrameTag) {
-                binUrlStr = tag.getAttribute(ATT_SRC);
-            } else if (tagname.equalsIgnoreCase(TAG_EMBED)
-                || tagname.equalsIgnoreCase(TAG_BGSOUND)){
-                binUrlStr = tag.getAttribute(ATT_SRC);  
-            } else if (tagname.equalsIgnoreCase(TAG_LINK)) {
-                // Putting the string first means it works even if the attribute is null
-                if (STYLESHEET.equalsIgnoreCase(tag.getAttribute(ATT_REL))) {
-                    binUrlStr = tag.getAttribute(ATT_HREF);
-                }
-            } else {
-                binUrlStr = tag.getAttribute(ATT_BACKGROUND);
-            }
-
-            if (binUrlStr != null) {
-                urls.addURL(binUrlStr, baseUrl.url);
-            }
-            // second, if the tag was a composite tag,
-            // recursively parse its children.
-            if (tag instanceof CompositeTag) {
-                CompositeTag composite = (CompositeTag) tag;
-                parseNodes(composite.elements(), baseUrl, urls);
-            }
-        }
-    }
-}
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
+import org.apache.jorphan.logging.LoggingManager;
+import org.apache.log.Logger;
+import org.htmlparser.Node;
+import org.htmlparser.Parser;
+import org.htmlparser.Tag;
+import org.htmlparser.tags.AppletTag;
+import org.htmlparser.tags.BaseHrefTag;
+import org.htmlparser.tags.BodyTag;
+import org.htmlparser.tags.CompositeTag;
+import org.htmlparser.tags.FrameTag;
+import org.htmlparser.tags.ImageTag;
+import org.htmlparser.tags.InputTag;
+import org.htmlparser.tags.LinkTag;
+import org.htmlparser.tags.ScriptTag;
+import org.htmlparser.util.NodeIterator;
+import org.htmlparser.util.ParserException;
+
+/**
+ * HtmlParser implementation using SourceForge's HtmlParser.
+ * 
+ */
+class HtmlParserHTMLParser extends HTMLParser {
+    private static final Logger log = LoggingManager.getLoggerForClass();
+
+    static{
+    	org.htmlparser.scanners.ScriptScanner.STRICT = false; // Try to ensure that more javascript code is processed OK ...
+    }
+	
+    protected HtmlParserHTMLParser() {
+		super();
+        log.info("Using htmlparser version: "+Parser.getVersion());
+	}
+
+	protected boolean isReusable() {
+		return true;
+	}
+
+	/*
+	 * (non-Javadoc)
+	 * 
+	 * @see org.apache.jmeter.protocol.http.parser.HtmlParser#getEmbeddedResourceURLs(byte[],
+	 *      java.net.URL)
+	 */
+	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls) throws HTMLParseException {
+        
+        if (log.isDebugEnabled()) log.debug("Parsing html of: " + baseUrl);
+        
+        Parser htmlParser = null;
+		try {
+			String contents = new String(html);
+			htmlParser = new Parser();
+            htmlParser.setInputHTML(contents);
+		} catch (Exception e) {
+			throw new HTMLParseException(e);
+		}
+
+		// Now parse the DOM tree
+		try {
+			// we start to iterate through the elements
+			parseNodes(htmlParser.elements(), new URLPointer(baseUrl), urls);
+			log.debug("End   : parseNodes");
+		} catch (ParserException e) {
+			throw new HTMLParseException(e);
+		}
+
+		return urls.iterator();
+	}
+	
+    /*
+	 * A dummy class to pass the pointer of URL.
+	 */
+    private static class URLPointer {
+    	private URLPointer(URL newUrl) {
+    		url = newUrl;
+    	}
+    	private URL url;
+    }
+    
+    /**
+     * Recursively parse all nodes to pick up all URL s.
+     * @see e the nodes to be parsed
+     * @see baseUrl Base URL from which the HTML code was obtained
+     * @see urls URLCollection
+     */
+    private void parseNodes(final NodeIterator e,
+    		final URLPointer baseUrl, final URLCollection urls) 
+        throws HTMLParseException, ParserException {
+        while(e.hasMoreNodes()) {
+            Node node = e.nextNode();
+            // a url is always in a Tag.
+            if (!(node instanceof Tag)) {
+                continue;
+            }
+            Tag tag = (Tag) node;
+            String tagname=tag.getTagName();
+            String binUrlStr = null;
+
+            // first we check to see if body tag has a
+            // background set
+            if (tag instanceof BodyTag) {
+                binUrlStr = tag.getAttribute(ATT_BACKGROUND);
+            } else if (tag instanceof BaseHrefTag) {
+                BaseHrefTag baseHref = (BaseHrefTag) tag;
+                String baseref = baseHref.getBaseUrl().toString();
+                try {
+                    if (!baseref.equals(""))// Bugzilla 30713
+                    {
+                        baseUrl.url = new URL(baseUrl.url, baseHref.getBaseUrl());
+                    }
+                } catch (MalformedURLException e1) {
+                    throw new HTMLParseException(e1);
+                }
+            } else if (tag instanceof ImageTag) {
+                ImageTag image = (ImageTag) tag;
+                binUrlStr = image.getImageURL();
+            } else if (tag instanceof AppletTag) {
+        		// look for applets
+
+        		// This will only work with an Applet .class file.
+        		// Ideally, this should be upgraded to work with Objects (IE)
+        		// and archives (.jar and .zip) files as well.
+                AppletTag applet = (AppletTag) tag;
+                binUrlStr = applet.getAppletClass();
+            } else if (tag instanceof InputTag) {
+                // we check the input tag type for image
+                if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttribute(ATT_TYPE))) {
+                    // then we need to download the binary
+                    binUrlStr = tag.getAttribute(ATT_SRC);
+                }
+            } else if (tag instanceof LinkTag) {
+                LinkTag link = (LinkTag) tag;
+                if (link.getChild(0) instanceof ImageTag) {
+                    ImageTag img = (ImageTag) link.getChild(0);
+                    binUrlStr = img.getImageURL();
+                }
+            } else if (tag instanceof ScriptTag) {
+                binUrlStr = tag.getAttribute(ATT_SRC);
+            } else if (tag instanceof FrameTag) {
+                binUrlStr = tag.getAttribute(ATT_SRC);
+            } else if (tagname.equalsIgnoreCase(TAG_EMBED)
+                || tagname.equalsIgnoreCase(TAG_BGSOUND)){
+                binUrlStr = tag.getAttribute(ATT_SRC);  
+            } else if (tagname.equalsIgnoreCase(TAG_LINK)) {
+                // Putting the string first means it works even if the attribute is null
+                if (STYLESHEET.equalsIgnoreCase(tag.getAttribute(ATT_REL))) {
+                    binUrlStr = tag.getAttribute(ATT_HREF);
+                }
+            } else {
+                binUrlStr = tag.getAttribute(ATT_BACKGROUND);
+            }
+
+            if (binUrlStr != null) {
+                urls.addURL(binUrlStr, baseUrl.url);
+            }
+
+            // Now look for URLs in the STYLE attribute
+            String styleTagStr = tag.getAttribute(ATT_STYLE);
+            if(styleTagStr != null) {
+            	HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
+            }
+
+            // second, if the tag was a composite tag,
+            // recursively parse its children.
+            if (tag instanceof CompositeTag) {
+                CompositeTag composite = (CompositeTag) tag;
+                parseNodes(composite.elements(), baseUrl, urls);
+            }
+        }
+    }
+
+}
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
index b9a6bd8bc..97f2bf486 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
@@ -1,315 +1,325 @@
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
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.testelement.property.PropertyIterator;
+import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
-import org.apache.oro.text.PatternCacheLRU;
+import org.apache.oro.text.regex.MatchResult;
+import org.apache.oro.text.regex.Pattern;
+import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 
 // For Junit tests @see TestHtmlParsingUtils
 
 /**
  * @author Michael Stover Created June 14, 2001
  */
 public final class HtmlParsingUtils {
 	transient private static Logger log = LoggingManager.getLoggerForClass();
 
 	/*
 	 * NOTUSED private int compilerOptions = Perl5Compiler.CASE_INSENSITIVE_MASK |
 	 * Perl5Compiler.MULTILINE_MASK | Perl5Compiler.READ_ONLY_MASK;
 	 */
 
-	private static PatternCacheLRU patternCache = new PatternCacheLRU(1000, new Perl5Compiler());
-
-	private static ThreadLocal localMatcher = new ThreadLocal() {
-		protected Object initialValue() {
-			return new Perl5Matcher();
-		}
-	};
-
 	/**
 	 * Private constructor to prevent instantiation.
 	 */
 	private HtmlParsingUtils() {
 	}
 
 	public static synchronized boolean isAnchorMatched(HTTPSamplerBase newLink, HTTPSamplerBase config)
 	{
 		boolean ok = true;
-		Perl5Matcher matcher = (Perl5Matcher) localMatcher.get();
+		Perl5Matcher matcher = JMeterUtils.getMatcher();
 		PropertyIterator iter = config.getArguments().iterator();
 
 		String query = null;
 		try {
 			query = URLDecoder.decode(newLink.getQueryString(), "UTF-8");
 		} catch (UnsupportedEncodingException e) {
 			// UTF-8 unsupported? You must be joking!
 			log.error("UTF-8 encoding not supported!");
 			throw new Error("Should not happen: " + e.toString());
 		}
 
 		if (query == null && config.getArguments().getArgumentCount() > 0) {
 			return false;
 		}
 
 		while (iter.hasNext()) {
 			Argument item = (Argument) iter.next().getObjectValue();
 			if (query.indexOf(item.getName() + "=") == -1) {
 				if (!(ok = ok
-						&& matcher.contains(query, patternCache
+						&& matcher.contains(query, JMeterUtils.getPatternCache()
 								.getPattern(item.getName(), Perl5Compiler.READ_ONLY_MASK)))) {
 					return false;
 				}
 			}
 		}
 
 		if (config.getDomain() != null && config.getDomain().length() > 0
 				&& !newLink.getDomain().equals(config.getDomain())) {
 			if (!(ok = ok
-					&& matcher.matches(newLink.getDomain(), patternCache.getPattern(config.getDomain(),
+					&& matcher.matches(newLink.getDomain(), JMeterUtils.getPatternCache().getPattern(config.getDomain(),
 							Perl5Compiler.READ_ONLY_MASK)))) {
 				return false;
 			}
 		}
 
 		if (!newLink.getPath().equals(config.getPath())
-				&& !matcher.matches(newLink.getPath(), patternCache.getPattern("[/]*" + config.getPath(),
+				&& !matcher.matches(newLink.getPath(), JMeterUtils.getPatternCache().getPattern("[/]*" + config.getPath(),
 						Perl5Compiler.READ_ONLY_MASK))) {
 			return false;
 		}
 
 		if (!(ok = ok
-				&& matcher.matches(newLink.getProtocol(), patternCache.getPattern(config.getProtocol(),
+				&& matcher.matches(newLink.getProtocol(), JMeterUtils.getPatternCache().getPattern(config.getProtocol(),
 						Perl5Compiler.READ_ONLY_MASK)))) {
 			return false;
 		}
 
 		return ok;
 	}
 
 	public static synchronized boolean isArgumentMatched(Argument arg, Argument patternArg) {
-		Perl5Matcher matcher = (Perl5Matcher) localMatcher.get();
-		return (arg.getName().equals(patternArg.getName()) || matcher.matches(arg.getName(), patternCache.getPattern(
+		Perl5Matcher matcher = JMeterUtils.getMatcher();
+		return (arg.getName().equals(patternArg.getName()) || matcher.matches(arg.getName(), JMeterUtils.getPatternCache().getPattern(
 				patternArg.getName(), Perl5Compiler.READ_ONLY_MASK)))
-				&& (arg.getValue().equals(patternArg.getValue()) || matcher.matches(arg.getValue(), patternCache
+				&& (arg.getValue().equals(patternArg.getValue()) || matcher.matches(arg.getValue(), JMeterUtils.getPatternCache()
 						.getPattern(patternArg.getValue(), Perl5Compiler.READ_ONLY_MASK)));
 	}
 
 	/**
 	 * Returns <code>tidy</code> as HTML parser.
 	 * 
 	 * @return a <code>tidy</code> HTML parser
 	 */
 	public static Tidy getParser() {
 		log.debug("Start : getParser1");
 		Tidy tidy = new Tidy();
 		tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
 		tidy.setQuiet(true);
 		tidy.setShowWarnings(false);
 
 		if (log.isDebugEnabled()) {
 			log.debug("getParser1 : tidy parser created - " + tidy);
 		}
 
 		log.debug("End : getParser1");
 
 		return tidy;
 	}
 
 	/**
 	 * Returns a node representing a whole xml given an xml document.
 	 * 
 	 * @param text
 	 *            an xml document
 	 * @return a node representing a whole xml
 	 */
 	public static Node getDOM(String text) {
 		log.debug("Start : getDOM1");
 
 		try {
 			Node node = getParser().parseDOM(new ByteArrayInputStream(text.getBytes("UTF-8")), null);// $NON-NLS-1$
 
 			if (log.isDebugEnabled()) {
 				log.debug("node : " + node);
 			}
 
 			log.debug("End : getDOM1");
 
 			return node;
 		} catch (UnsupportedEncodingException e) {
 			log.error("getDOM1 : Unsupported encoding exception - " + e);
 			log.debug("End : getDOM1");
 			throw new RuntimeException("UTF-8 encoding failed");
 		}
 	}
 
 	public static Document createEmptyDoc() {
 		return Tidy.createEmptyDocument();
 	}
 
 	/**
 	 * Create a new Sampler based on an HREF string plus a contextual URL
 	 * object. Given that an HREF string might be of three possible forms, some
 	 * processing is required.
 	 */
 	public static HTTPSamplerBase createUrlFromAnchor(String parsedUrlString, URL context) throws MalformedURLException {
 		if (log.isDebugEnabled()) {
 			log.debug("Creating URL from Anchor: " + parsedUrlString + ", base: " + context);
 		}
 		URL url = new URL(context, parsedUrlString);
 		HTTPSamplerBase sampler =HTTPSamplerFactory.newInstance();
 		sampler.setDomain(url.getHost());
 		sampler.setProtocol(url.getProtocol());
 		sampler.setPort(url.getPort());
 		sampler.setPath(url.getPath());
 		sampler.parseArguments(url.getQuery());
 
 		return sampler;
 	}
 
 	public static List createURLFromForm(Node doc, URL context) {
 		String selectName = null;
 		LinkedList urlConfigs = new LinkedList();
 		recurseForm(doc, urlConfigs, context, selectName, false);
 		/*
 		 * NamedNodeMap atts = formNode.getAttributes();
 		 * if(atts.getNamedItem("action") == null) { throw new
 		 * MalformedURLException(); } String action =
 		 * atts.getNamedItem("action").getNodeValue(); UrlConfig url =
 		 * createUrlFromAnchor(action, context); recurseForm(doc, url,
 		 * selectName,true,formStart);
 		 */
 		return urlConfigs;
 	}
 
     // N.B. Since the tags are extracted from an HTML Form, any values must already have been encoded
 	private static boolean recurseForm(Node tempNode, LinkedList urlConfigs, URL context, String selectName,
 			boolean inForm) {
 		NamedNodeMap nodeAtts = tempNode.getAttributes();
 		String tag = tempNode.getNodeName();
 		try {
 			if (inForm) {
 				HTTPSamplerBase url = (HTTPSamplerBase) urlConfigs.getLast();
 				if (tag.equalsIgnoreCase("form")) {
 					try {
 						urlConfigs.add(createFormUrlConfig(tempNode, context));
 					} catch (MalformedURLException e) {
 						inForm = false;
 					}
 				} else if (tag.equalsIgnoreCase("input")) {
 					url.addEncodedArgument(getAttributeValue(nodeAtts, "name"), getAttributeValue(nodeAtts, "value"));
 				} else if (tag.equalsIgnoreCase("textarea")) {
 					try {
 						url.addEncodedArgument(getAttributeValue(nodeAtts, "name"), tempNode.getFirstChild().getNodeValue());
 					} catch (NullPointerException e) {
 						url.addArgument(getAttributeValue(nodeAtts, "name"), "");
 					}
 				} else if (tag.equalsIgnoreCase("select")) {
 					selectName = getAttributeValue(nodeAtts, "name");
 				} else if (tag.equalsIgnoreCase("option")) {
 					String value = getAttributeValue(nodeAtts, "value");
 					if (value == null) {
 						try {
 							value = tempNode.getFirstChild().getNodeValue();
 						} catch (NullPointerException e) {
 							value = "";
 						}
 					}
 					url.addEncodedArgument(selectName, value);
 				}
 			} else if (tag.equalsIgnoreCase("form")) {
 				try {
 					urlConfigs.add(createFormUrlConfig(tempNode, context));
 					inForm = true;
 				} catch (MalformedURLException e) {
 					inForm = false;
 				}
 				// I can't see the point for this code being here. Looks like
 				// a really obscure performance optimization feature :-)
 				// Seriously: I'll comment it out... I just don't dare to
 				// remove it completely, in case there *is* a reason.
 				/*
 				 * try { Thread.sleep(5000); } catch (Exception e) { }
 				 */
 			}
 		} catch (Exception ex) {
 			log.warn("Some bad HTML " + printNode(tempNode), ex);
 		}
 		NodeList childNodes = tempNode.getChildNodes();
 		for (int x = 0; x < childNodes.getLength(); x++) {
 			inForm = recurseForm(childNodes.item(x), urlConfigs, context, selectName, inForm);
 		}
 		return inForm;
 	}
 
 	private static String getAttributeValue(NamedNodeMap att, String attName) {
 		try {
 			return att.getNamedItem(attName).getNodeValue();
 		} catch (Exception ex) {
 			return "";
 		}
 	}
 
 	private static String printNode(Node node) {
 		StringBuffer buf = new StringBuffer();
 		buf.append("<");
 		buf.append(node.getNodeName());
 		NamedNodeMap atts = node.getAttributes();
 		for (int x = 0; x < atts.getLength(); x++) {
 			buf.append(" ");
 			buf.append(atts.item(x).getNodeName());
 			buf.append("=\"");
 			buf.append(atts.item(x).getNodeValue());
 			buf.append("\"");
 		}
 
 		buf.append(">");
 
 		return buf.toString();
 	}
 
 	private static HTTPSamplerBase createFormUrlConfig(Node tempNode, URL context) throws MalformedURLException {
 		NamedNodeMap atts = tempNode.getAttributes();
 		if (atts.getNamedItem("action") == null) {
 			throw new MalformedURLException();
 		}
 		String action = atts.getNamedItem("action").getNodeValue();
 		HTTPSamplerBase url = createUrlFromAnchor(action, context);
 		return url;
 	}
+	
+	public static void extractStyleURLs(final URL baseUrl, final URLCollection urls, String styleTagStr) {
+		Perl5Matcher matcher = JMeterUtils.getMatcher();
+		Pattern pattern = JMeterUtils.getPatternCache().getPattern(
+				"URL\\(\\s*('|\")(.*)('|\")\\s*\\)",
+				Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.SINGLELINE_MASK | Perl5Compiler.READ_ONLY_MASK);
+		PatternMatcherInput input = null;
+		input = new PatternMatcherInput(styleTagStr);
+		while (matcher.contains(input, pattern)) {
+		    MatchResult match = matcher.getMatch();
+		    // The value is in the second group
+		    String styleUrl = match.group(2);
+		    urls.addURL(styleUrl, baseUrl);
+		}
+	}
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
index 9f5fc6629..cb980f508 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
@@ -1,217 +1,223 @@
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
 
 	protected boolean isReusable() {
 		return true;
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.parser.HTMLParser#getEmbeddedResourceURLs(byte[],
 	 *      java.net.URL)
 	 */
 	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls) throws HTMLParseException {
 		Document dom = null;
 		try {
 			dom = (Document) getDOM(html);
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
 				if (tmp != null)
 					try {
 						baseUrl = new URL(baseUrl, tmp);
 					} catch (MalformedURLException e) {
 						throw new HTMLParseException(e);
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
 			String back = getValue(attrs, ATT_BACKGROUND);
 			if (back != null) {
 				urls.addURL(back, baseUrl);
 			}
 			if (name.equalsIgnoreCase(TAG_BGSOUND)) {
 				urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
 				break;
 			}
 
+			String style = getValue(attrs, ATT_STYLE);
+			if (style != null) {
+            	HtmlParsingUtils.extractStyleURLs(baseUrl, urls, style);
+			}
+
 			NodeList children = node.getChildNodes();
 			if (children != null) {
 				int len = children.getLength();
 				for (int i = 0; i < len; i++) {
 					baseUrl = scanNodes(children.item(i), urls, baseUrl);
 				}
 			}
+
 			break;
 
 		// case Node.TEXT_NODE:
 		// break;
 
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
 		if (n != null)
 			v = n.getNodeValue();
 		return v;
 	}
 
 	/**
 	 * Returns <code>tidy</code> as HTML parser.
 	 * 
 	 * @return a <code>tidy</code> HTML parser
 	 */
 	private static Tidy getTidyParser() {
 		log.debug("Start : getParser");
 		Tidy tidy = new Tidy();
 		tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
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
 	private static Node getDOM(byte[] text) throws SAXException {
 		log.debug("Start : getDOM");
 		Node node = getTidyParser().parseDOM(new ByteArrayInputStream(text), null);
 		if (log.isDebugEnabled()) {
 			log.debug("node : " + node);
 		}
 		log.debug("End   : getDOM");
 		return node;
 	}
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
index d7c499c25..39c5e4ac1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
@@ -1,210 +1,201 @@
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
 
+import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 // NOTE: Also looked at using Java 1.4 regexp instead of ORO. The change was
 // trivial. Performance did not improve -- at least not significantly.
 // Finally decided for ORO following advise from Stefan Bodewig (message
 // to jmeter-dev dated 25 Nov 2003 8:52 CET) [Jordi]
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.apache.oro.text.regex.MalformedPatternException;
 
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
  * @author <a href="mailto:jsalvata@apache.org">Jordi Salvat i Alabart</a>
- * @version $Revision$ updated on $Date$
  */
 class RegexpHTMLParser extends HTMLParser {
+    private static final Logger log = LoggingManager.getLoggerForClass();
 
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
-	private static final String REGEXP = "<(?:" + "!--.*?-->" + "|BASE" + SEP + "HREF" + VALUE
-			+ "|(?:IMG|SCRIPT|FRAME|IFRAME|BGSOUND|FRAME)" + SEP + "SRC" + VALUE + "|APPLET" + SEP + "CODE(?:BASE)?"
-			+ VALUE + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE)" + VALUE + "|(?:BODY|TABLE|TR|TD)" + SEP
-			+ "BACKGROUND" + VALUE + "|INPUT(?:" + SEP + "(?:SRC" + VALUE
-			+ "|TYPE\\s*=\\s*(?:\"image\"|'image'|image(?=[\\s>])))){2,}" + "|LINK(?:" + SEP + "(?:HREF" + VALUE
+	private static final String REGEXP = 
+		      "<(?:" + "!--.*?-->"
+		    + "|BASE" + SEP + "HREF" + VALUE
+			+ "|(?:IMG|SCRIPT|FRAME|IFRAME|BGSOUND|FRAME)" + SEP + "SRC" + VALUE
+			+ "|APPLET" + SEP + "CODE(?:BASE)?"	+ VALUE
+			+ "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE)" + VALUE
+			+ "|(?:BODY|TABLE|TR|TD)" + SEP + "BACKGROUND" + VALUE
+			+ "|[^<]+?STYLE\\s*=['\"].*?URL\\(\\s*['\"](.+?)['\"]\\s*\\)"
+			+ "|INPUT(?:" + SEP + "(?:SRC" + VALUE
+			+ "|TYPE\\s*=\\s*(?:\"image\"|'image'|image(?=[\\s>])))){2,}"
+			+ "|LINK(?:" + SEP + "(?:HREF" + VALUE
 			+ "|REL\\s*=\\s*(?:\"stylesheet\"|'stylesheet'|stylesheet(?=[\\s>])))){2,}" + ")";
 
 	// Number of capturing groups possibly containing Base HREFs:
 	private static final int NUM_BASE_GROUPS = 3;
 
 	/**
 	 * Compiled regular expression.
 	 */
 	static Pattern pattern;
 
 	/**
-	 * Thread-local matcher:
-	 */
-	private static ThreadLocal localMatcher = new ThreadLocal() {
-		protected Object initialValue() {
-			return new Perl5Matcher();
-		}
-	};
-
-	/**
 	 * Thread-local input:
 	 */
 	private static ThreadLocal localInput = new ThreadLocal() {
 		protected Object initialValue() {
 			return new PatternMatcherInput(new char[0]);
 		}
 	};
 
-	/** Used to store the Logger (used for debug and error messages). */
-	transient private static Logger log;
-
 	protected boolean isReusable() {
 		return true;
 	}
 
 	/**
 	 * Make sure to compile the regular expression upon instantiation:
 	 */
 	protected RegexpHTMLParser() {
 		super();
 
-		// Define this here to ensure it's ready to report any trouble
-		// with the regexp:
-		log = LoggingManager.getLoggerForClass();
-
 		// Compile the regular expression:
 		try {
 			Perl5Compiler c = new Perl5Compiler();
 			pattern = c.compile(REGEXP, Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.SINGLELINE_MASK
 					| Perl5Compiler.READ_ONLY_MASK);
 		} catch (MalformedPatternException mpe) {
 			log.error("Internal error compiling regular expression in ParseRegexp.");
 			log.error("MalformedPatternException - " + mpe);
 			throw new Error(mpe);
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.parser.HtmlParser#getEmbeddedResourceURLs(byte[],
 	 *      java.net.URL)
 	 */
 	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls) {
 
-		Perl5Matcher matcher = (Perl5Matcher) localMatcher.get();
+		Perl5Matcher matcher = JMeterUtils.getMatcher();
 		PatternMatcherInput input = (PatternMatcherInput) localInput.get();
 		// TODO: find a way to avoid the cost of creating a String here --
 		// probably a new PatternMatcherInput working on a byte[] would do
 		// better.
 		input.setInput(new String(html));
 		while (matcher.contains(input, pattern)) {
 			MatchResult match = matcher.getMatch();
 			String s;
 			if (log.isDebugEnabled())
-				log.debug("match groups " + match.groups());
+				log.debug("match groups " + match.groups() + " " + match.toString());
 			// Check for a BASE HREF:
 			for (int g = 1; g <= NUM_BASE_GROUPS && g <= match.groups(); g++) {
 				s = match.group(g);
 				if (s != null) {
 					if (log.isDebugEnabled()) {
 						log.debug("new baseUrl: " + s + " - " + baseUrl.toString());
 					}
 					try {
 						baseUrl = new URL(baseUrl, s);
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
-				if (log.isDebugEnabled()) {
-					log.debug("group " + g + " - " + match.group(g));
-				}
 				if (s != null) {
+					if (log.isDebugEnabled()) {
+						log.debug("group " + g + " - " + match.group(g));
+					}
 					urls.addURL(s, baseUrl);
 				}
 			}
 		}
 		return urls.iterator();
 	}
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index c2c1e557c..f9b31f236 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,761 +1,762 @@
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
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>History of Changes</title>   
 </properties> 
 <body> 
 <section name="History of Changes"> 
 <p><b>Changes are chronologically ordered from top (most recent) to bottom 
 (least recent)</b></p>  
 
 <!--  ===================  -->
 
 <h3>Version 2.2.1</h3>
 <h4>Known problems:</h4>
 <p>Thread active counts are always zero in CSV and XML files when running remote tests.
 </p>
 <p>The property file_format.testlog=2.1 is treated the same as 2.2.
 However JMeter does honour the 3 testplan versions.</p>
 <p>
 Bug 22510 - JMeter always uses the first entry in the keystore.
 </p>
 <h4>Incompatible changes (usage):</h4>
 <p>
 Bug 41104: JMeterThread behaviour was changed so that PostProcessors are run in forward order
 (as they appear in the test plan) rather than reverse order as previously.
 The original behaviour can be restored by setting the following JMeter property:
 <br/>
 jmeterthread.reversePostProcessors=true
 </p>
 <p>
 The HTTP Authorisation Manager now has extra columns for domain and realm, 
 so the temporary work-round of using '\' and '@' in the username to delimit the domain and realm
 has been removed.
 </p>
 <h4>Incompatible changes (development):</h4>
 <p>
 Calulator and SamplingStatCalculator classes no longer provide any formatting of their data.
 Formatting should now be done using the jorphan.gui Renderer classes.
 </p>
 <h4>New functionality:</h4>
 <ul>
 <li>Added httpclient.parameters.file to allow HttpClient parameters to be defined</li>
 <li>Added beanshell.init.file property to run a BeanShell script at startup</li>
 <li>Added timeout for WebService (SOAP) Sampler</li>
 <li>Bug 40804 - Change Counter default to max = Long.MAX_VALUE</li>
 <li>BeanShell Post-Processor no longer ignores samples with zero-length result data</li>
 <li>Use property jmeter.home (if present) to override user.dir when starting JMeter</li>
 <li>Bug 41457 - Add TCP Sampler option to not re-use connections</li>
 <li>Bug 41522 - Use JUnit sampler name in sample results</li>
 <li>HttpClient now behaves the same as the JDK http sampler for invalid certificates etc</li>
 <li>Add Domain and Realm support to HTTP Authorisation Manager</li>
 <li>Bug 33964 - send file as entire post body if name/type are omitted</li>
 <li>HTTP Mirror Server Workbench element</li>
 <li>Bug 41253 - extend XPathExtractor to work with non-NodeList XPath expressions</li>
 <li>Bug 39717 - use icons in the results tree instead of colors</li>
 <li>Added __V variable function to resolve nested variable names</li>
 </ul>
 
 <h4>Non-functional improvements:</h4>
 <ul>
 <li>Change to htmlparser 2.0</li>
 <li>Updated to xstream 1.2.1/xpp3_min-1.1.3.4.O</li>
 <li>Functor calls can now be unit tested</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 39773 - NTLM now needs local host name - fix other call</li>
 <li>Bug 40438 - setting "httpclient.localaddress" has no effect</li>
 <li>Bug 40419 - Chinese messages translation fix</li>
 <li>Bug 39861 - fix typo</li>
 <li>Bug 40562 - redirects no longer invoke RE post processors</li>
 <li>Bug 40451 - set label if not set by sampler</li>
 <li>Fix NPE in CounterConfig.java in Remote mode</li>
 <li>Bug 40791 - Calculator used by Summary Report</li>
 <li>Bug 40772 - correctly parse missing fields</li>
 <li>Bug 40773 - XML JTL not parsed correctly</li>
 <li>Bug 41029 - JMeter -t fails to close input JMX file</li>
 <li>Bug 40954 - Statistical mode in distributed testing shows wrong results</li>
 <li>Fix ClassCast Exception when using sampler that returns null, e..g TestAction</li>
 <li>Bug 41277 - add Latency and Encoding to CSV output</li>
 <li>Bug 41414 - Mac OS X may add extra item to -jar classpath</li>
 <li>Fix NPE when saving thread counts in remote testing</li>
 <li>Bug 34261 - NPE in HtmlParser (allow for missing attributes)</li>
 <li>Bug 40100 - check FileServer type before calling close</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Bug 39887 - jmeter.util.SSLManager: Couldn't load keystore error message</li>
 <li>Bug 41543 - exception when webserver returns "500 Internal Server Error" and content-length is 0</li>
 <li>Bug 41416 - don't use chunked input for text-box input in SOAP-RPC sampler</li>
 <li>Bug 39827 - SOAP Sampler content length for files</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 <li>Bug 40369 (partial) Equals Response Assertion</li>
 <li>Fix Class cast exception in Clear.java</li>
 <li>Bug 40383 - don't set content-type if already set</li>
 <li>Mailer Visualiser test button now works if test plan has not yet been saved</li>
 <li>Bug 36959 - Shortcuts "ctrl c" and "ctrl v" don't work on the tree elements</li>
+<li>Bug 40696 - retrieve embedded resources from STYLE URL() attributes</li>
 </ul>
 
 <h3>Version 2.2</h3>
 
 <h4>Incompatible changes:</h4>
 <p>
 The time stamp is now set to the sampler start time (it was the end).
 To revert to the previous behaviour, change the property <b>sampleresult.timestamp.start</b> to false (or comment it)
 </p>
 <p>The JMX output format has been simplified and files are not backwards compatible</p>
 <p>
 The JMeter.BAT file no longer changes directory to JMeter home, but runs from the current working directory.
 The jmeter-n.bat and jmeter-t.bat files change to the directory containing the input file.
 </p>
 <p>
 Listeners are now started slightly later in order to allow variable names to be used.
 This may cause some problems; if so define the following in jmeter.properties:
 <br/>
 jmeterengine.startlistenerslater=false
 </p>
 
 <h4>Known problems:</h4>
 <ul>
 <li>Post-processors run in reverse order (see bug 41140)</li>
 <li>Module Controller does not work in non-GUI mode</li>
 <li>Aggregate Report and some other listeners use increasing amounts of memory as a test progresses</li>
 <li>Does not always handle non-default encoding properly</li>
 <li>Spaces in the installation path cause problems for client-server mode</li>
 <li>Change of Language does not propagate to all test elements</li>
 <li>SamplingStatCalculator keeps a List of all samples for calculation purposes; 
 this can cause memory exhaustion in long-running tests</li>
 <li>Does not properly handle server certificates if they are expired or not installed locally</li>
 </ul>
 
 <h4>New functionality:</h4>
 <ul>
 <li>Report function</li>
 <li>XPath Extractor Post-Processor. Handles single and multiple matches.</li>
 <li>Simpler JMX file format (2.2)</li>
 <li>BeanshellSampler code can update ResponseData directly</li>
 <li>Bug 37490 - Allow UDV as delay in Duration Assertion</li>
 <li>Slow connection emulation for HttpClient</li>
 <li>Enhanced JUnitSampler so that by default assert errors and exceptions are not appended to the error message. 
 Users must explicitly check append in the sampler</li>
 <li>Enhanced the documentation for webservice sampler to explain how it works with CSVDataSet</li>
 <li>Enhanced the documentation for javascript function to explain escaping comma</li>
 <li>Allow CSV Data Set file names to be absolute</li>
 <li>Report Tree compiler errors better</li>
 <li>Don't reset Regex Extractor variable if default is empty</li>
 <li>includecontroller.prefix property added</li>
 <li>Regular Expression Extractor sets group count</li>
 <li>Can now save entire screen as an image, not just the right-hand pane</li>
 <li>Bug 38901 - Add optional SOAPAction header to SOAP Sampler</li>
 <li>New BeanShell test elements: Timer, PreProcessor, PostProcessor, Listener</li>
 <li>__split() function now clears next variable, so it can be used with ForEach Controller</li>
 <li>Bug 38682 - add CallableStatement functionality to JDBC Sampler</li>
 <li>Make it easier to change the RMI/Server port</li>
 <li>Add property jmeter.save.saveservice.xml_pi to provide optional xml processing instruction in JTL files</li>
 <li>Add bytes and URL to items that can be saved in sample log files (XML and CSV)</li>
 <li>The Post-Processor "Save Responses to a File" now saves the generated file name with the
 sample, and the file name can be included in the sample log file.
 </li>
 <li>Change jmeter.bat DOS script so it works from any directory</li>
 <li>New -N option to define nonProxyHosts from command-line</li>
 <li>New -S option to define system properties from input file</li>
 <li>Bug 26136 - allow configuration of local address</li>
 <li>Expand tree by default when loading a test plan - can be disabled by setting property onload.expandtree=false</li>
 <li>Bug 11843 - URL Rewriter can now cache the session id</li>
 <li>Counter Pre-Processor now supports formatted numbers</li>
 <li>Add support for HEAD PUT OPTIONS TRACE and DELETE methods</li>
 <li>Allow default HTTP implementation to be changed</li>
 <li>Optionally save active thread counts (group and all) to result files</li>
 <li>Variables/functions can now be used in Listener file names</li>
 <li>New __time() function; define START.MS/START.YMD/START.HMS properties and variables</li>
 <li>Add Thread Name to Tree and Table Views</li>
 <li>Add debug functions: What class, debug on, debug off</li>
 <li>Non-caching Calculator - used by Table Visualiser to reduce memory footprint</li>
 <li>Summary Report - similar to Aggregate Report, but uses less memory</li>
 <li>Bug 39580 - recycle option for CSV Dataset</li>
 <li>Bug 37652 - support for Ajp Tomcat protocol</li>
 <li>Bug 39626 - Loading SOAP/XML-RPC requests from file</li>
 <li>Bug 39652 - Allow truncation of labels on AxisGraph</li>
 <li>Allow use of htmlparser 1.6</li>
 <li>Bug 39656 - always use SOAP action if it is provided</li>
 <li>Automatically include properties from user.properties file</li>
 <li>Add __jexl() function - evaluates Commons JEXL expressions</li>
 <li>Optionally load JMeter properties from user.properties and system properties from system.properties.</li>
 <li>Bug 39707 - allow Regex match against URL</li>
 <li>Add start time to Table Visualiser</li>
 <li>HTTP Samplers can now extract embedded resources for any required media types</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Fix NPE when no module selected in Module Controller</li>
 <li>Fix NPE in XStream when no ResponseData present</li>
 <li>Remove ?xml prefix when running with Java 1.5 and no x-jars</li>
 <li>Bug 37117 - setProperty() function should return ""; added optional return of original setting</li>
 <li>Fix CSV output time format</li>
 <li>Bug 37140 - handle encoding better in RegexFunction</li>
 <li>Load all cookies, not just the first; fix class cast exception</li>
 <li>Fix default Cookie path name (remove page name)</li>
 <li>Fixed resultcode attribute name</li>
 <li>Bug 36898 - apply encoding to RegexExtractor</li>
 <li>Add properties for saving subresults, assertions, latency, samplerData, responseHeaders, requestHeaders &amp; encoding</li>
 <li>Bug 37705 - Synch Timer now works OK after run is stopped</li>
 <li>Bug 37716 - Proxy request now handles file Post correctly</li>
 <li>HttpClient Sampler now saves latency</li>
 <li>Fix NPE when using JavaScript function on Test Plan</li>
 <li>Fix Base Href parsing in htmlparser</li>
 <li>Bug 38256 - handle cookie with no path</li>
 <li>Bug 38391 - use long when accumulating timer delays</li>
 <li>Bug 38554 - Random function now uses long numbers</li>
 <li>Bug 35224 - allow duplicate attributes for LDAP sampler</li>
 <li>Bug 38693 - Webservice sampler can now use https protocol</li>
 <li>Bug 38646 - Regex Extractor now clears old variables on match failure</li>
 <li>Bug 38640 - fix WebService Sampler pooling</li>
 <li>Bug 38474 - HTML Link Parser doesn't follow frame links</li>
 <li>Bug 36430 - Counter now uses long rather than int to increase the range</li>
 <li>Bug 38302 - fix XPath function</li>
 <li>Bug 38748 - JDBC DataSourceElement fails with remote testing</li>
 <li>Bug 38902 - sometimes -1 seems to be returned unnecessarily for response code</li>
 <li>Bug 38840 - make XML Assertion thread-safe</li>
 <li>Bug 38681 - Include controller now works in non-GUI mode</li>
 <li>Add write(OS,IS) implementation to TCPClientImpl</li>
 <li>Sample Result converter saves response code as "rc". Previously it saved as "rs" but read with "rc"; it will now also read with "rc".
 The XSL stylesheets also now accept either "rc" or "rs"</li>
 <li>Fix counter function so each counter instance is independent (previously the per-user counters were shared between instances of the function)</li>
 <li>Fix TestBean Examples so that they work</li>
 <li>Fix JTidy parser so it does not skip body tags with background images</li>
 <li>Fix HtmlParser parser so it catches all background images</li>
 <li>Bug 39252 set SoapSampler sample result from XML data</li>
 <li>Bug 38694 - WebServiceSampler not setting data encoding correctly</li>
 <li>Result Collector now closes input files read by listeners</li>
 <li>Bug 25505 - First HTTP sampling fails with "HTTPS hostname wrong: should be 'localhost'"</li>
 <li>Bug 25236 - remove double scrollbar from Assertion Result Listener</li>
 <li>Bug 38234 - Graph Listener divide by zero problem</li>
 <li>Bug 38824 - clarify behaviour of Ignore Status</li>
 <li>Bug 38250 - jmeter.properties "language" now supports country suffix, for zh_CN and zh_TW etc</li>
 <li>jmeter.properties file is now closed after it has been read</li>
 <li>Bug 39533 - StatCalculator added wrong items</li>
 <li>Bug 39599 - ConcurrentModificationException</li>
 <li>HTTPSampler2 now handles Auto and Follow redirects correctly</li>
 <li>Bug 29481 - fix reloading sample results so subresults not counted twice</li>
 <li>Bug 30267 - handle AutoRedirects properly</li>
 <li>Bug 39677 - allow for space in JMETER_BIN variable</li>
 <li>Use Commons HttpClient cookie parsing and management. Fix various problems with cookie handling.</li>
 <li>Bug 39773 - NTCredentials needs host name</li>
 </ul>	
 	
 <h4>Other changes</h4>
 <ul>
 <li>Updated to HTTPClient 3.0 (from 2.0)</li>
 <li>Updated to Commons Collections 3.1</li>
 <li>Improved formatting of Request Data in Tree View</li>
 <li>Expanded user documentation</li>
 <li>Added MANIFEST, NOTICE and LICENSE to all jars</li>
 <li>Extract htmlparser interface into separate jarfile to make it possible to replace the parser</li>
 <li>Removed SQL Config GUI as no longer needed (or working!)</li>
 <li>HTTPSampler no longer logs a warning for Page not found (404)</li>
 <li>StringFromFile now callable as __StringFromFile (as well as _StringFromFile)</li>
 <li>Updated to Commons Logging 1.1</li>
 </ul>
 
 <!--  ===================  -->
 
 
 <hr/>
 <h3>Version 2.1.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Include Controller allows a test plan to reference an external jmx file</li>
 <li>New JUnitSampler added for using JUnit Test classes</li>
 <li>New Aggregate Graph listener is capable of graphing aggregate statistics</li>
 <li>Can provide additional classpath entries using the property user.classpath and on the Test Plan element</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>AccessLog Sampler and JDBC test elements populated correctly from 2.0 test plans</li>
 <li>BSF Sampler now populates filename and parameters from saved test plan</li>
 <li>Bug 36500 - handle missing data more gracefully in WebServiceSampler</li>
 <li>Bug 35546 - add merge to right-click menu</li>
 <li>Bug 36642 - Summariser stopped working in 2.1</li>
 <li>Bug 36618 - CSV header line did not match saved data</li>
 <li>JMeter should now run under JVM 1.3 (but does not build with 1.3)</li>
 </ul>	
 	
 
 <!--  ===================  -->
 
 <h3>Version 2.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Test Script file format - smaller, more compact, more readable</li>
 <li>New Sample Result file format - smaller, more compact</li>
 <li>XSchema Assertion</li>
 <li>XML Tree display</li>
 <li>CSV DataSet Config item</li>
 <li>New JDBC Connection Pool Config Element</li>
 <li>Synchronisation Timer</li>
 <li>setProperty function</li>
 <li>Save response data on error</li>
 <li>Ant JMeter XSLT now optionally shows failed responses and has internal links</li>
 <li>Allow JavaScript variable name to be omitted</li>
 <li>Changed following Samplers to set sample label from sampler name</li>
 <li>All Test elements can be saved as a graphics image to a file</li>
 <li>Bug 35026 - add RE pattern matching to Proxy</li>
 <li>Bug 34739 - Enhance constant Throughput timer</li>
 <li>Bug 25052 - use response encoding to create comparison string in Response Assertion</li>
 <li>New optional icons</li>
 <li>Allow icons to be defined via property files</li>
 <li>New stylesheets for 2.1 format XML test output</li>
 <li>Save samplers, config element and listeners as PNG</li>
 <li>Enhanced support for WSDL processing</li>
 <li>New JMS sampler for topic and queue messages</li>
 <li>How-to for JMS samplers</li>
 <li>Bug 35525 - Added Spanish localisation</li>
 <li>Bug 30379 - allow server.rmi.port to be overridden</li>
 <li>enhanced the monitor listener to save the calculated stats</li>
 <li>Functions and variables now work at top level of test plan</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 34586 - XPath always remained as /</li>
 <li>BeanShellInterpreter did not handle null objects properly</li>
 <li>Fix Chinese resource bundle names</li>
 <li>Save field names if required to CSV files</li>
 <li>Ensure XML file is closed</li>
 <li>Correct icons now displayed for TestBean components</li>
 <li>Allow for missing optional jar(s) in creating menus</li>
 <li>Changed Samplers to set sample label from sampler name as was the case for HTTP</li>
 <li>Fix various samplers to avoid NPEs when incomplete data is provided</li>
 <li>Fix Cookie Manager to use seconds; add debug</li>
 <li>Bug 35067 - set up filename when using -t option</li>
 <li>Don't substitute TestElement.* properties by UDVs in Proxy</li>
 <li>Bug 35065 - don't save old extensions in File Saver</li>
 <li>Bug 25413 - don't enable Restart button unnecessarily</li>
 <li>Bug 35059 - Runtime Controller stopped working</li>
 <li>Clear up any left-over connections created by LDAP Extended Sampler</li>
 <li>Bug 23248 - module controller didn't remember stuff between save and reload</li>
 <li>Fix Chinese locales</li>
 <li>Bug 29920 - change default locale if necessary to ensure default properties are picked up when English is selected.</li>
 <li>Bug fixes for Tomcat monitor captions</li> 
 <li>Fixed webservice sampler so it works with user defined variables</li>
 <li>Fixed screen borders for LDAP config GUI elements</li>
 <li>Bug 31184 - make sure encoding is specified in JDBC sampler</li>
 <li>TCP sampler - only share sockets with same host:port details; correct the manual</li>
 <li>Extract src attribute for embed tags in JTidy and Html Parsers</li>
 </ul>	
 
 <!--  ===================  -->
 
 <h3>Version 2.0.3</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>XPath Assertion and XPath Function</li>
 <li>Switch Controller</li>
 <li>ForEach Controller can now loop through sets of groups</li>
 <li>Allow CSVRead delimiter to be changed (see jmeter.properties)</li>
 <li>Bug 33920 - allow additional property files</li>
 <li>Bug 33845 - allow direct override of Home dir</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Regex Extractor nested constant not put in correct place (32395)</li>
 <li>Start time reset to now if necessary so that delay works OK.</li>
 <li>Missing start/end times in scheduler are assumed to be now, not 1970</li>
 <li>Bug 28661 - 304 responses not appearing in listeners</li>
 <li>DOS scripts now handle different disks better</li>
 <li>Bug 32345 - HTTP Rewriter does not work with HTTP Request default</li>
 <li>Catch Runtime Exceptions so an error in one Listener does not affect others</li>
 <li>Bug 33467 - __threadNum() extracted number wrongly </li>
 <li>Bug 29186,33299 - fix CLI parsing of "-" in second argument</li>
 <li>Fix CLI parse bug: -D arg1=arg2. Log more startup parameters.</li>
 <li>Fix JTidy and HTMLParser parsers to handle form src= and link rel=stylesheet</li>
 <li>JMeterThread now logs Errors to jmeter.log which were appearing on console</li>
 <li>Ensure WhileController condition is dynamically checked</li>
 <li>Bug 32790 ensure If Controller condition is re-evaluated each time</li>
 <li>Bug 30266 - document how to display proxy recording responses</li>
 <li>Bug 33921 - merge should not change file name</li>
 <li>Close file now gives chance to save changes</li>
 <li>Bug 33559 - fixes to Runtime Controller</li>
 </ul>
 <h4>Other changes:</h4>
 <ul>
 <li>To help with variable evaluation, JMeterThread sets "sampling started" a bit earlier (see jmeter.properties)</li>
 <li>Bug 33796 - delete cookies with null/empty values</li>
 <li>Better checking of parameter count in JavaScript function</li>
 <li>Thread Group now defaults to 1 loop instead of forever</li>
 <li>All Beanshell access is now via a single class; only need BSH jar at run-time</li>
 <li>Bug 32464 - document Direct Draw settings in jmeter.bat</li>
 <li>Bug 33919 - increase Counter field sizes</li>
 <li>Bug 32252 - ForEach was not initialising counters</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.0.2</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>While Controller</li>
 <li>BeanShell intilisation scripts</li>
 <li>Result Saver can optionally save failed results only</li>
 <li>Display as HTML has option not to download frames and images etc</li>
 <li>Multiple Tree elements can now be enabled/disabled/copied/pasted at once</li>
 <li>__split() function added</li>
 <li>(28699) allow Assertion to regard unsuccessful responses - e.g. 404 - as successful</li>
 <li>(29075) Regex Extractor can now extract data out of http response header as well as the body</li>
 <li>__log() functions can now write to stdout and stderr</li>
 <li>URL Modifier can now optionally ignore query parameters</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>If controller now works after the first false condition (31390)</li>
 <li>Regex GUI was losing track of Header/Body checkbox (29853)</li>
 <li>Display as HTML now handles frames and relative images</li>
 <li>Right-click open replaced by merge</li>
 <li>Fix some drag and drop problems</li>
 <li>Fixed foreach demo example so it works</li>
 <li>(30741) SSL password prompt now works again </li>
 <li>StringFromFile now closes files at end of test; start and end now optional as intended</li>
 <li>(31342) Fixed text of SOAP Sampler headers</li>
 <li>Proxy must now be stopped before it can be removed (25145)</li>
 <li>Link Parser now supports BASE href (25490)</li>
 <li>(30917) Classfinder ignores duplicate names</li>
 <li>(22820) Allow Counter value to be cleared</li>
 <li>(28230) Fix NPE in HTTP Sampler retrieving embedded resources</li>
 <li>Improve handling of StopTest; catch and log some more errors</li>
 <li>ForEach Controller no longer runs any samples if first variable is not defined</li>
 <li>(28663) NPE in remote JDBC execution</li>
 <li>(30110) Deadlock in stopTest processing</li>
 <li>(31696) Duration not working correctly when using Scheduler</li>
 <li>JMeterContext now uses ThreadLocal - should fix some potential NPE errors</li>
 </ul>
 <h3>Version 2.0.1</h3>
 <p>Bug fix release. TBA.</p>
 <h3>Version 2.0</h3>
 <ul>
 	<li>HTML parsing improved; now has choice of 3 parsers, and most embedded elements can now be detected and downloaded.</li>
 <li>Redirects can now be delegated to URLConnection by defining the JMeter property HTTPSamper.delegateRedirects=true (default is false) </li>
 <li>Stop Thread and Stop Test methods added for Samplers and Assertions etc. Samplers can call setStopThread(true) or setStopTest(true) if they detect an error that needs to stop the thread of the test after the sample has been processed </li>
 <li>Thread Group Gui now has an extra pane to specify what happens after a Sampler error: Continue (as now), Stop Thread or Stop Test. 
     This needs to be extended to a lower level at some stage. </li>
 <li>Added Shutdown to Run Menu. This is the same as Stop except that it lets the Threads finish normally (i.e. after the next sample has been completed) </li>
 <li>Remote samples can be cached until the end of a test by defining the property hold_samples=true when running the server.
 More work is needed to be able to control this from the GUI </li>
 <li>Proxy server has option to skip recording browser headers </li>
 <li>Proxy restart works better (stop waits for daemon to finish) </li>
 <li>Scheduler ignores start if it has already passed </li>
 <li>Scheduler now has delay function </li>
 <li>added Summariser test element (mainly for non-GUI) testing. This prints summary statistics to System.out and/or the log file every so oftem (3 minutes by default). Multiple summarisers can be used; samples are accumulated by summariser name. </li>
 <li>Extra Proxy Server options: 
 Create all samplers with keep-alive disabled 
 Add Separator markers between sets of samples 
 Add Response Assertion to first sampler in each set </li>
 <li>Test Plan has a comment field</li>
 	
 	<li>Help Page can now be pushed to background</li>
 	<li>Separate Function help page</li>
 	<li>New / amended functions</li>
 	<ul>
 	  <li>__property() and __P() functions</li>
 	  <li>__log() and __logn() - for writing to the log file</li>
       <li>_StringFromFile can now process a sequence of files, e.g. dir/file01.txt, dir/file02.txt etc </li>
       <li>_StringFromFile() funtion can now use a variable or function for the file name </li>
 	</ul>
 	<li>New / amended Assertions</li>
 	<ul>
         <li>Response Assertion now works for URLs, and it handles null data better </li>
         <li>Response Assertion can now match on Response Code and Response message as well </li>
 		<li>HTML Assertion using JTidy to check for well-formed HTML</li>
 	</ul>
 	<li>If Controller (not fully functional yet)</li>
 	<li>Transaction Controller (aggregates the times of its children)</li>
 	<li>New Samplers</li>
 		<ul>
 			<li>Basic BSF Sampler (optional)</li>
 			<li>BeanShell Sampler (optional, needs to be downloaded from www.beanshell.org</li>
 			<li>Basic TCP Sampler</li>
 		</ul>
      <li>Optionally start BeanShell server (allows remote access to JMeter variables and methods) </li>
 </ul>
 <h3>Version 1.9.1</h3>
 <p>TBA</p>
 <h3>Version 1.9</h3>
 <ul>
 <li>Sample result log files can now be in CSV or XML format</li>
 <li>New Event model for notification of iteration events during test plan run</li>
 <li>New Javascript function for executing arbitrary javascript statements</li>
 <li>Many GUI improvements</li>
 <li>New Pre-processors and Post-processors replace Modifiers and Response-Based Modifiers. </li>
 <li>Compatible with jdk1.3</li>
 <li>JMeter functions are now fully recursive and universal (can use functions as parameters to functions)</li>
 <li>Integrated help window now supports hypertext links</li>
 <li>New Random Function</li>
 <li>New XML Assertion</li>
 <li>New LDAP Sampler (alpha code)</li>
 <li>New Ant Task to run JMeter (in extras folder)</li>
 <li>New Java Sampler test implementation (to assist developers)</li>
 <li>More efficient use of memory, faster loading of .jmx files</li>
 <li>New SOAP Sampler (alpha code)</li>
 <li>New Median calculation in Graph Results visualizer</li>
 <li>Default config element added for developer benefit</li>
 <li>Various performance enhancements during test run</li>
 <li>New Simple File recorder for minimal GUI overhead during test run</li>
 <li>New Function: StringFromFile - grabs values from a file</li>
 <li>New Function: CSVRead - grabs multiple values from a file</li>
 <li>Functions now longer need to be encoded - special values should be escaped 
 with "\" if they are literal values</li>
 <li>New cut/copy/paste functionality</li>
 <li>SSL testing should work with less user-fudging, and in non-gui mode</li>
 <li>Mailer Model works in non-gui mode</li>
 <li>New Througput Controller</li>
 <li>New Module Controller</li>
 <li>Tests can now be scheduled to run from a certain time till a certain time</li>
 <li>Remote JMeter servers can be started from a non-gui client.  Also, in gui mode, all remote servers can be started with a single click</li>
 <li>ThreadGroups can now be run either serially or in parallel (default)</li>
 <li>New command line options to override properties</li>
 <li>New Size Assertion</li>
 
 </ul>
 
 <h3>Version 1.8.1</h3>
 <ul>
 <li>Bug Fix Release.  Many bugs were fixed.</li>
 <li>Removed redundant "Root" node from test tree.</li>
 <li>Re-introduced Icons in test tree.</li>
 <li>Some re-organization of code to improve build process.</li>
 <li>View Results Tree has added option to view results as web document (still buggy at this point).</li>
 <li>New Total line in Aggregate Listener (still buggy at this point).</li>
 <li>Improvements to ability to change JMeter's Locale settings.</li>
 <li>Improvements to SSL Manager.</li>
 </ul>
 
 <h3>Version 1.8</h3>
 <ul>
 <li>Improvement to Aggregate report's calculations.</li>
 <li>Simplified application logging.</li>
 <li>New Duration Assertion.</li>
 <li>Fixed and improved Mailer Visualizer.</li>
 <li>Improvements to HTTP Sampler's recovery of resources (sockets and file handles).</li>
 <li>Improving JMeter's internal handling of test start/stop.</li>
 <li>Fixing and adding options to behavior of Interleave and Random Controllers.</li>
 <li>New Counter config element.</li>
 <li>New User Parameters config element.</li>
 <li>Improved performance of file opener.</li>
 <li>Functions and other elements can access global variables.</li>
 <li>Help system available within JMeter's GUI.</li>
 <li>Test Elements can be disabled.</li>
 <li>Language/Locale can be changed while running JMeter (mostly).</li>
 <li>View Results Tree can be configured to record only errors.</li>
 <li>Various bug fixes.</li>
 </ul>
 
 <b>Changes: for more info, contact <a href="mailto:mstover1@apache.org">Michael Stover</a></b>
 <h3>Version 1.7.3</h3>
 <ul>
 <li>New Functions that provide more ability to change requests dynamically during test runs.</li>
 <li>New language translations in Japanese and German.</li>
 <li>Removed annoying Log4J error messages.</li>
 <li>Improved support for loading JMeter 1.7 version test plan files (.jmx files).</li>
 <li>JMeter now supports proxy servers that require username/password authentication.</li>
 <li>Dialog box indicating test stopping doesn't hang JMeter on problems with stopping test.</li>
 <li>GUI can run multiple remote JMeter servers (fixes GUI bug that prevented this).</li>
 <li>Dialog box to help created function calls in GUI.</li>
 <li>New Keep-alive switch in HTTP Requests to indicate JMeter should or should not use Keep-Alive for sockets.</li>
 <li>HTTP Post requests can have GET style arguments in Path field.  Proxy records them correctly now.</li>
 <li>New User-defined test-wide static variables.</li>
 <li>View Results Tree now displays more information, including name of request (matching the name
 in the test tree) and full request and POST data.</li>
 <li>Removed obsolete View Results Visualizer (use View Results Tree instead).</li>
 <li>Performance enhancements.</li>
 <li>Memory use enhancements.</li>
 <li>Graph visualizer GUI improvements.</li>
 <li>Updates and fixes to Mailer Visualizer.</li>
 </ul>
  
 <h3>Version 1.7.2</h3>
 <ul>
 <li>JMeter now notifies user when test has stopped running.</li>
 <li>HTTP Proxy server records HTTP Requests with re-direct turned off.</li>
 <li>HTTP Requests can be instructed to either follow redirects or ignore them.</li>
 <li>Various GUI improvements.</li>
 <li>New Random Controller.</li>
 <li>New SOAP/XML-RPC Sampler.</li>
 </ul>
 
 <h3>Version 1.7.1</h3>
 <ul>
 <li>JMeter's architecture revamped for a more complete separation between GUI code and
 test engine code.</li>
 <li>Use of Avalon code to save test plans to XML as Configuration Objects</li>
 <li>All listeners can save data to file and load same data at later date.</li>
 </ul>
 
 <h3>Version 1.7Beta</h3> 
 <ul> 
 	<li>Better XML support for special characters (Tushar Bhatia) </li> 
 	<li>Non-GUI functioning  &amp; Non-GUI test plan execution  (Tushar Bhatia)</li> 
 	<li>Removing Swing dependence from base JMeter classes</li> 
 	<li>Internationalization (Takashi Okamoto)</li> 
 	<li>AllTests bug fix (neth6@atozasia.com)</li> 
 	<li>ClassFinder bug fix (neth6@atozasia.com)</li> 
 	<li>New Loop Controller</li> 
 	<li>Proxy Server records HTTP samples from browser 
 		(and documented in the user manual)</li> <li>Multipart Form support</li> 
 	<li>HTTP Header class for Header customization</li> 
 	<li>Extracting HTTP Header information from responses (Jamie Davidson)</li> 
 	<li>Mailer Visualizer re-added to JMeter</li> 
 	<li>JMeter now url encodes parameter names and values</li> 
 	<li>listeners no longer give exceptions if their gui's haven't been initialized</li> 
 	<li>HTTPS and Authorization working together</li> 
 	<li>New Http sampling that automatically parses HTML response 
 		for images to download, and includes the downloading of these 
 		images in total time for request (Neth neth6@atozasia.com) </li> 
 	<li>HTTP responses from server can be parsed for links and forms, 
 		and dynamic data can be extracted and added to test samples 
 		at run-time (documented)</li>  
 	<li>New Ramp-up feature (Jonathan O'Keefe)</li> 
 	<li>New visualizers (Neth)</li> 
 	<li>New Assertions for functional testing</li> 
 </ul>  
 
 <h3>Version 1.6.1</h3> 
 <ul> 
 	<li>Fixed saving and loading of test scripts (no more extra lines)</li> 
 	<li>Can save and load special characters (such as &quot;&amp;&quot; and &quot;&lt;&quot;).</li> 
 	<li>Can save and load timers and listeners.</li> 
 	<li>Minor bug fix for cookies (if you cookie value 
 		contained an &quot;=&quot;, then it broke).</li> 
 	<li>URL's can sample ports other than 80, and can test HTTPS, 
 		provided you have the necessary jars (JSSE)</li> 
 </ul> 
 
 <h3>Version 1.6 Alpha</h3> 
 <ul> 
 	<li>New UI</li> 
 	<li>Separation of GUI and Logic code</li> 	
 	<li>New Plug-in framework for new modules</li> 
 	<li>Enhanced performance</li> 
 	<li>Layering of test logic for greater flexibility</li> 
 	<li>Added support for saving of test elements</li> 
 	<li>Added support for distributed testing using a single client</li> 
 
 </ul> 
 <h3>Version 1.5.1</h3> 
 <ul> 
 	<li>Fixed bug that caused cookies not to be read if header name case not as expected.</li> 
 	<li>Clone entries before sending to sampler - prevents relocations from messing up 
 		information across threads</li> 
 	<li>Minor bug fix to convenience dialog for adding paramters to test sample.  
 		Bug prevented entries in dialog from appearing in test sample.</li> 
 	<li>Added xerces.jar to distribution</li> 
 	<li>Added junit.jar to distribution and created a few tests.</li> 
 	<li>Started work on new framework.  New files in cvs, but do not effect program yet.</li> 
 	<li>Fixed bug that prevent HTTPJMeterThread from delaying according to chosen timer.</li> 
 </ul>  
 <p> 
 <h3>Version 1.5</h3> 
 <ul>   
 	<li>Abstracted out the concept of the Sampler, SamplerController, and TestSample.   
 		A Sampler represents code that understands a protocol (such as HTTP, 
 		or FTP, RMI,   SMTP, etc..).  It is the code that actually makes the 
 		connection to whatever is   being tested.   A SamplerController 
 		represents code that understands how to organize and run a group   
 		of test samples.  It is what binds together a Sampler and it's test 
 		samples and runs them.   A TestSample represents code that understands 
 		how to gather information from the   user about a particular test.  
 		For a website, it would represent a URL and any   information to be sent 
 		with the URL.</li>   
 	<li>The UI has been updated to make entering test samples more convenient.</li>   
 	<li>Thread groups have been added, allowing a user to setup multiple test to run   
 		concurrently, and to allow sharing of test samples between those tests.</li>   
 	<li>It is now possible to save and load test samples.</li>   
 	<li>....and many more minor changes/improvements...</li> 
 </ul> 
 </p> 
 <p> 
 <b>Apache JMeter 1.4.1-dev</b> (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Cleaned up URLSampler code after tons of patches for better readability. (SM)</li>
    <li>Made JMeter send a special &quot;user-agent&quot; identifier. (SM)</li>
    <li>Fixed problems with redirection not sending cookies and authentication info and removed
      a warning with jikes compilation. Thanks to <a href="mailto:wtanaka@yahoo.com">Wesley
      Tanaka</a> for the patches (SM)</li>
    <li>Fixed a bug in the URLSampler that caused to skip one URL when testing lists of URLs and
      a problem with Cookie handling. Thanks to <a
      href="mailto:gjohnson@investlearning.com">Graham Johnson</a> for the patches (SM)</li>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:sschaub@bju.edu">Stephen
      Schaub</a> for the patch (SM)</li>
  </ul>
  </p>
  <p>
  <b>Apache JMeter 1.4</b> - Jul 11 1999 (<a href="mailto:cimjpno@be.ibm.com">Jean-Pierre Norguet</a>,
  <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)
   <ul>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:bburns@labs.gte.com">Brendan
      Burns</a> for the patch (SM)</li>
    <li>Added close button to the About box for those window managers who don't provide it.
      Thanks to Jan-Henrik Haukeland for pointing it out. (SM)</li>
    <li>Added the simple Spline sample visualizer (JPN)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.3</b> - Apr 16 1999
   (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>,
  <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>)
 <ul>
    <li>Run the Garbage Collector and run finalization before starting to sampling to ensure
      same state every time (SM)</li>
    <li>Fixed some NullPointerExceptions here and there (SM)</li>
    <li>Added HTTP authentication capabilities (RL)</li>
    <li>Added windowed sample visualizer (SM)</li>
    <li>Fixed stupid bug for command line arguments. Thanks to <a
      href="mailto:jbracer@infoneers.com">Jorge Bracer</a> for pointing this out (SM)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.2</b> - Mar 17 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Integrated cookie capabilities with JMeter (SM)</li>
    <li>Added the Cookie manager and Netscape file parser (SD)</li>
    <li>Fixed compilation error for JDK 1.1 (SD)</li> </ul> </p>  
 <p> <b>Apache JMeter 1.1</b> - Feb 24 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Created the opportunity to create URL aliasing from the properties file as well as the
      ability to associate aliases to URL sequences instead of single URLs (SM) Thanks to <a
      href="mailto:chatfield@evergreen.com">Simon Chatfield</a> for the very nice suggestions
      and code examples.</li>
    <li>Removed the TextVisualizer and replaced it with the much more useful FileVisualizer (SM)</li>
    <li>Added the known bug list (SM)</li>
    <li>Removed the Java Apache logo (SM)</li>
    <li>Fixed a couple of typos (SM)</li>
    <li>Added UNIX makefile (SD)</li> </ul> </p> 
 <p> <b>Apache JMeter 1.0.1</b> - Jan 25 1999 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Removed pending issues doc issues (SM)</li>
    <li>Fixed the unix script (SM)</li>
    <li>Added the possibility of running the JAR directly using &quot;java -jar
      ApacheJMeter.jar&quot; with Java 2 (SM)</li>
    <li>Some small updates: fixed Swing location after Java 2(tm) release, license update and
      small cleanups (SM)</li> 
 </ul> </p> 
 <p> <b>Apache JMeter 1.0</b> - Dec 15 1998 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>) 
 <ul>
    <li>Initial version. (SM)</li> 
 </ul> </p> 
 </section> 
 </body> 
 </document>
