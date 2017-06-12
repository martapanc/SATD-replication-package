diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
index 2627570de..68e22aadb 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
@@ -1,267 +1,267 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.Serializable;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 //For unit tests, @see TestHTTPArgument
 
 /*
  *
  * Represents an Argument for HTTP requests.
  */
 public class HTTPArgument extends Argument implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTTPArgument.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final String ALWAYS_ENCODE = "HTTPArgument.always_encode";
 
     private static final String USE_EQUALS = "HTTPArgument.use_equals";
 
     private static final EncoderCache cache = new EncoderCache(1000);
 
     /**
      * Constructor for the Argument object.
      * <p>
      * The value is assumed to be not encoded.
      *
      * @param name
      *            name of the paramter
      * @param value
      *            value of the parameter
      * @param metadata
      *            the separator to use between name and value
      */
     public HTTPArgument(String name, String value, String metadata) {
         this(name, value, false);
         this.setMetaData(metadata);
     }
 
     public void setUseEquals(boolean ue) {
         if (ue) {
             setMetaData("=");
         } else {
             setMetaData("");
         }
         setProperty(new BooleanProperty(USE_EQUALS, ue));
     }
 
     public boolean isUseEquals() {
         boolean eq = getPropertyAsBoolean(USE_EQUALS);
         if (getMetaData().equals("=") || (getValue() != null && getValue().length() > 0)) {
             setUseEquals(true);
             return true;
         }
         return eq;
 
     }
 
     public void setAlwaysEncoded(boolean ae) {
         setProperty(new BooleanProperty(ALWAYS_ENCODE, ae));
     }
 
     public boolean isAlwaysEncoded() {
         return getPropertyAsBoolean(ALWAYS_ENCODE);
     }
 
     /**
      * Constructor for the Argument object.
      * <p>
      * The value is assumed to be not encoded.
      *
      * @param name
      *            name of the parameter
      * @param value
      *            value of the parameter
      */
     public HTTPArgument(String name, String value) {
         this(name, value, false);
     }
 
     /**
      * @param name
      *            name of the parameter
      * @param value
      *            value of the parameter
      * @param alreadyEncoded
      *            <code>true</code> if the value is already encoded, in which
      *            case they are decoded before storage
      */
     public HTTPArgument(String name, String value, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance; alwaysEncoded is set to true.
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param alreadyEncoded true if the name and value is already encoded, in which case they are decoded before storage.
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, boolean alreadyEncoded, String contentEncoding) {
         setAlwaysEncoded(true);
         if (alreadyEncoded) {
             try {
                 // We assume the name is always encoded according to spec
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding name, calling URLDecoder.decode with '"+name+"' and contentEncoding:"+EncoderCache.URL_ARGUMENT_ENCODING);
                 }
                 name = URLDecoder.decode(name, EncoderCache.URL_ARGUMENT_ENCODING);
                 // The value is encoded in the specified encoding
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding value, calling URLDecoder.decode with '"+value+"' and contentEncoding:"+contentEncoding);
                 }
                 value = URLDecoder.decode(value, contentEncoding);
             } catch (UnsupportedEncodingException e) {
                 log.error(contentEncoding + " encoding not supported!");
                 throw new Error(e.toString(), e);
             }
         }
         setName(name);
         setValue(value);
         setMetaData("=");
     }
 
     /**
      * Construct a new HTTPArgument instance
      *
      * @param name
      *            the name of the parameter
      * @param value
      *            the value of the parameter
      * @param metaData
      *            the separator to use between name and value
      * @param alreadyEncoded
      *            true if the name and value is already encoded
      */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, metaData, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param metaData the separator to use between name and value
      * @param alreadyEncoded true if the name and value is already encoded
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded, String contentEncoding) {
         this(name, value, alreadyEncoded, contentEncoding);
         setMetaData(metaData);
     }
 
     public HTTPArgument(Argument arg) {
         this(arg.getName(), arg.getValue(), arg.getMetaData());
     }
 
     /**
      * Constructor for the Argument object
      */
     public HTTPArgument() {
     }
 
     /**
      * Sets the Name attribute of the Argument object.
      *
      * @param newName
      *            the new Name value
      */
     @Override
     public void setName(String newName) {
         if (newName == null || !newName.equals(getName())) {
             super.setName(newName);
         }
     }
 
     /**
      * Get the argument value encoded using UTF-8
      *
      * @return the argument value encoded in UTF-8
      */
     public String getEncodedValue() {
         // Encode according to the HTTP spec, i.e. UTF-8
         try {
             return getEncodedValue(EncoderCache.URL_ARGUMENT_ENCODING);
         } catch (UnsupportedEncodingException e) {
             // This can't happen (how should utf8 not be supported!?!),
             // so just throw an Error:
             throw new Error("Should not happen: " + e.toString());
         }
     }
 
     /**
      * Get the argument value encoded in the specified encoding
      *
      * @param contentEncoding the encoding to use when encoding the argument value
      * @return the argument value encoded in the specified encoding
      * @throws UnsupportedEncodingException of the encoding is not supported
      */
     public String getEncodedValue(String contentEncoding) throws UnsupportedEncodingException {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getValue(), contentEncoding);
         } else {
             return getValue();
         }
     }
 
     public String getEncodedName() {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getName());
         } else {
             return getName();
         }
 
     }
 
     /**
      * Converts all {@link Argument} entries in the collection to {@link HTTPArgument} entries.
      * 
      * @param args collection of {@link Argument} and/or {@link HTTPArgument} entries
      */
     public static void convertArgumentsToHTTP(Arguments args) {
         List<Argument> newArguments = new LinkedList<>();
         for (JMeterProperty jMeterProperty : args.getArguments()) {
             Argument arg = (Argument) jMeterProperty.getObjectValue();
             if (!(arg instanceof HTTPArgument)) {
                 newArguments.add(new HTTPArgument(arg));
             } else {
                 newArguments.add(arg);
             }
         }
         args.removeAllArguments();
         args.setArguments(newArguments);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
index 9c0cf94e6..2d8904833 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
@@ -1,435 +1,435 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 // For JUnit tests, @see TestLogFilter
 
 /**
  * Description:<br>
  * <br>
  * LogFilter is a basic implementation of Filter interface. This implementation
  * will keep a record of the filtered strings to avoid repeating the process
  * unnecessarily.
  * <p>
  * The current implementation supports replacing the file extension. The reason
  * for supporting this is from first hand experience porting an existing website
  * to Tomcat + JSP. Later on we may want to provide the ability to replace the
  * whole filename. If the need materializes, we can add it later.
  * <p>
  * Example of how to use it is provided in the main method. An example is
  * provided below.
  * <pre>
  * testf = new LogFilter();
  * String[] incl = { &quot;hello.html&quot;, &quot;index.html&quot;, &quot;/index.jsp&quot; };
  * String[] thefiles = { &quot;/test/hello.jsp&quot;, &quot;/test/one/hello.html&quot;, &quot;hello.jsp&quot;, &quot;hello.htm&quot;, &quot;/test/open.jsp&quot;,
  *      &quot;/test/open.html&quot;, &quot;/index.jsp&quot;, &quot;/index.jhtml&quot;, &quot;newindex.jsp&quot;, &quot;oldindex.jsp&quot;, &quot;oldindex1.jsp&quot;,
  *      &quot;oldindex2.jsp&quot;, &quot;oldindex3.jsp&quot;, &quot;oldindex4.jsp&quot;, &quot;oldindex5.jsp&quot;, &quot;oldindex6.jsp&quot;, &quot;/test/index.htm&quot; };
  * testf.excludeFiles(incl);
  * System.out.println(&quot; ------------ exclude test -------------&quot;);
  * for (int idx = 0; idx &lt; thefiles.length; idx++) {
  *  boolean fl = testf.isFiltered(thefiles[idx]);
  *  String line = testf.filter(thefiles[idx]);
  *  if (line != null) {
  *     System.out.println(&quot;the file: &quot; + line);
  *  }
  * }
  * </pre>
  *
  * As a general note. Both isFiltered and filter() have to be called. Calling
  * either one will not produce the desired result. isFiltered(string) will tell
  * you if a string should be filtered. The second step is to filter the string,
  * which will return null if it is filtered and replace any part of the string
  * that should be replaced.
  */
 
 public class LogFilter implements Filter, Serializable {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);
 
     // protected members used by class to filter
 
     protected boolean CHANGEEXT = false;
 
     protected String OLDEXT = null;
 
     protected String NEWEXT = null;
 
     protected String[] INCFILE = null;
 
     protected String[] EXCFILE = null;
 
     protected boolean FILEFILTER = false;
 
     protected boolean USEFILE = true;
 
     protected String[] INCPTRN = null;
 
     protected String[] EXCPTRN = null;
 
     protected boolean PTRNFILTER = false;
 
     protected ArrayList<Pattern> EXCPATTERNS = new ArrayList<>();
 
     protected ArrayList<Pattern> INCPATTERNS = new ArrayList<>();
 
     protected String NEWFILE = null;
 
     /**
      * The default constructor is empty
      */
     public LogFilter() {
         super();
     }
 
     /**
      * The method will replace the file extension with the new one. You can
      * either provide the extension without the period ".", or with. The method
      * will check for period and add it if it isn't present.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#setReplaceExtension(java.lang.String,
      *      java.lang.String)
      */
     @Override
     public void setReplaceExtension(String oldext, String newext) {
         if (oldext != null && newext != null) {
             this.CHANGEEXT = true;
             if (!oldext.contains(".") && !newext.contains(".")) {
                 this.OLDEXT = "." + oldext;
                 this.NEWEXT = "." + newext;
             } else {
                 this.OLDEXT = oldext;
                 this.NEWEXT = newext;
             }
         }
     }
 
     /**
      * Give the filter a list of files to include
      *
      * @param filenames
      *            list of files to include
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#includeFiles(java.lang.String[])
      */
     @Override
     public void includeFiles(String[] filenames) {
         if (filenames != null && filenames.length > 0) {
             INCFILE = filenames;
             this.FILEFILTER = true;
         }
     }
 
     /**
      * Give the filter a list of files to exclude
      *
      * @param filenames
      *            list of files to exclude
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#excludeFiles(java.lang.String[])
      */
     @Override
     public void excludeFiles(String[] filenames) {
         if (filenames != null && filenames.length > 0) {
             EXCFILE = filenames;
             this.FILEFILTER = true;
         }
     }
 
     /**
      * Give the filter a set of regular expressions to filter with for
      * inclusion. This method hasn't been fully implemented and test yet. The
      * implementation is not complete.
      *
      * @param regexp
      *            list of regular expressions
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#includePattern(String[])
      */
     @Override
     public void includePattern(String[] regexp) {
         if (regexp != null && regexp.length > 0) {
             INCPTRN = regexp;
             this.PTRNFILTER = true;
             // now we create the compiled pattern and
             // add it to the arraylist
             for (String includePattern : INCPTRN) {
                 this.INCPATTERNS.add(this.createPattern(includePattern));
             }
         }
     }
 
     /**
      * Give the filter a set of regular expressions to filter with for
      * exclusion. This method hasn't been fully implemented and test yet. The
      * implementation is not complete.
      *
      * @param regexp
      *            list of regular expressions
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#excludePattern(String[])
      */
     @Override
     public void excludePattern(String[] regexp) {
         if (regexp != null && regexp.length > 0) {
             EXCPTRN = regexp;
             this.PTRNFILTER = true;
             // now we create the compiled pattern and
             // add it to the arraylist
             for (String excludePattern : EXCPTRN) {
                 this.EXCPATTERNS.add(this.createPattern(excludePattern));
             }
         }
     }
 
     /**
      * In the case of log filtering the important thing is whether the log entry
      * should be used. Therefore, the method will only return true if the entry
      * should be used. Since the interface defines both inclusion and exclusion,
      * that means by default inclusion filtering assumes all entries are
      * excluded unless it matches. In the case of exclusion filtering, it assumes
      * all entries are included unless it matches, which means it should be
      * excluded.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#isFiltered(String, TestElement)
      * @param path path to be tested
      * @return <code>true</code> if entry should be excluded
      */
     @Override
     public boolean isFiltered(String path,TestElement el) {
         if (this.FILEFILTER) {
             return filterFile(path);
         }
         if (this.PTRNFILTER) {
             return filterPattern(path);
         }
         return false;
     }
 
     /**
      * Filter the file. The implementation performs the exclusion first before
      * the inclusion. This means if a file name is in both string arrays, the
      * exclusion will take priority. Depending on how users expect this to work,
      * we may want to change the priority so that inclusion is performed first
      * and exclusion second. Another possible alternative is to perform both
      * inclusion and exclusion. Doing so would make the most sense if the method
      * throws an exception and tells the user the same filename is in both the
      * include and exclude array.
      *
      * @param file the file to filter
      * @return boolean
      */
     protected boolean filterFile(String file) {
         // double check this logic make sure it
         // makes sense
         if (this.EXCFILE != null) {
             return excFile(file);
         } else if (this.INCFILE != null) {
             return !incFile(file);
         }
         return false;
     }
 
     /**
      * Method implements the logic for filtering file name inclusion. The method
      * iterates through the array and uses indexOf. Once it finds a match, it
      * won't bother with the rest of the filenames in the array.
      *
      * @param text
      *            name of the file to tested (must not be <code>null</code>)
      * @return boolean include
      */
     public boolean incFile(String text) {
         // inclusion filter assumes most of
         // the files are not wanted, therefore
         // usefile is set to false unless it
         // matches.
         this.USEFILE = false;
         for (String includeFile : this.INCFILE) {
             if (text.contains(includeFile)) {
                 this.USEFILE = true;
                 break;
             }
         }
         return this.USEFILE;
     }
 
     /**
      * Method implements the logic for filtering file name exclusion. The method
      * iterates through the array and uses indexOf. Once it finds a match, it
      * won't bother with the rest of the filenames in the array.
      *
      * @param text
      *            name of the file to be tested (must not be null)
      * @return boolean exclude
      */
     public boolean excFile(String text) {
         // exclusion filter assumes most of
         // the files are used, therefore
         // usefile is set to true, unless
         // it matches.
         this.USEFILE = true;
         boolean exc = false;
         for (String excludeFile : this.EXCFILE) {
             if (text.contains(excludeFile)) {
                 exc = true;
                 this.USEFILE = false;
                 break;
             }
         }
         return exc;
     }
 
     /**
      * The current implementation assumes the user has checked the regular
      * expressions so that they don't cancel each other. The basic assumption is
      * the method will return true if the text should be filtered. If not, it
      * will return false, which means it should not be filtered.
      *
      * @param text text to be checked
      * @return boolean
      */
     protected boolean filterPattern(String text) {
         if (this.INCPTRN != null) {
             return !incPattern(text);
         } else if (this.EXCPTRN != null) {
             return excPattern(text);
         }
         return false;
     }
 
     /**
      * By default, the method assumes the entry is not included, unless it
      * matches. In that case, it will return true.
      *
      * @param text text to be checked
      * @return <code>true</code> if text is included
      */
     protected boolean incPattern(String text) {
         this.USEFILE = false;
         for (Pattern includePattern : this.INCPATTERNS) {
             if (JMeterUtils.getMatcher().contains(text, includePattern)) {
                 this.USEFILE = true;
                 break;
             }
         }
         return this.USEFILE;
     }
 
     /**
      * The method assumes by default the text is not excluded. If the text
      * matches the pattern, it will then return true.
      *
      * @param text text to be checked
      * @return <code>true</code> if text is excluded
      */
     protected boolean excPattern(String text) {
         this.USEFILE = true;
         boolean exc = false;
         for (Pattern excludePattern : this.EXCPATTERNS) {
             if (JMeterUtils.getMatcher().contains(text, excludePattern)) {
                 exc = true;
                 this.USEFILE = false;
                 break;
             }
         }
         return exc;
     }
 
     /**
      * Method uses indexOf to replace the old extension with the new extension.
      * It might be good to use regular expression, but for now this is a simple
      * method. The method isn't designed to replace multiple instances of the
      * text, since that isn't how file extensions work. If the string contains
      * more than one instance of the old extension, only the first instance will
      * be replaced.
      *
      * @param text
      *            name of the file in which the extension should be replaced
      *            (must not be null)
      * @return <code>true</code> if the extension could be replaced,
      *         <code>false</code> otherwise
      */
     public boolean replaceExtension(String text) {
         int pt = text.indexOf(this.OLDEXT);
         if (pt > -1) {
             int extsize = this.OLDEXT.length();
             this.NEWFILE = text.substring(0, pt) + this.NEWEXT + text.substring(pt + extsize);
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * The current implementation checks the boolean if the text should be used
      * or not. isFilter( string) has to be called first.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#filter(java.lang.String)
      */
     @Override
     public String filter(String text) {
         if (this.CHANGEEXT) {
             if (replaceExtension(text)) {
                 return this.NEWFILE;
             } else {
                 return text;
             }
         } else if (this.USEFILE) {
             return text;
         } else {
             return null;
         }
     }
 
     /**
      * create a new pattern object from the string.
      *
      * @param pattern
      *            string representation of the perl5 compatible regex pattern
      * @return compiled Pattern, or <code>null</code> if no pattern could be
      *         compiled
      */
     public Pattern createPattern(String pattern) {
         try {
             return JMeterUtils.getPatternCache().getPattern(pattern,
                     Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
         } catch (MalformedCachePatternException exception) {
             log.error("Problem with pattern: "+pattern,exception);
             return null;
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void reset() {
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/SessionFilter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/SessionFilter.java
index 5b2c22b78..aa414d9f1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/SessionFilter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/SessionFilter.java
@@ -1,229 +1,229 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
 import org.apache.jmeter.testelement.TestCloneable;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Provides Session Filtering for the AccessLog Sampler.
  *
  */
 public class SessionFilter implements Filter, Serializable, TestCloneable,ThreadListener {
-    private static final long serialVersionUID = 232L;
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final long serialVersionUID = 233L;
+    private static final Logger log = LoggerFactory.getLogger(SessionFilter.class);
 
     /**
      * Protects access to managersInUse
      */
     private static final Object LOCK = new Object();
     /**
      * These objects are static across multiple threads in a test, via clone()
      * method.
      */
     private final Map<String, CookieManager> cookieManagers;
     private final Set<CookieManager> managersInUse;
 
     private CookieManager lastUsed;
 
     /**
      * Creates a new SessionFilter and initializes its fields to new collections
      */
     public SessionFilter() {
         this(new ConcurrentHashMap<>(), Collections.synchronizedSet(new HashSet<>()));
     }
 
     /**
      * Creates a new SessionFilter, but re-uses the given collections
      * 
      * @param cookieManagers
      *            {@link CookieManager}s to be used for the different IPs
      * @param managersInUse
      *            CookieManagers currently in use by other threads
      */
     public SessionFilter(Map<String, CookieManager> cookieManagers, Set<CookieManager> managersInUse) {
         this.cookieManagers = cookieManagers;
         this.managersInUse = managersInUse;
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.LogFilter#excPattern(java.lang.String)
      */
     protected boolean hasExcPattern(String text) {
         return false;
     }
 
     protected String getIpAddress(String logLine) {
         Pattern incIp = JMeterUtils.getPatternCache().getPattern("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}",
                 Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         matcher.contains(logLine, incIp);
         return matcher.getMatch().group(0);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void reset() {
         cookieManagers.clear();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         return new SessionFilter(cookieManagers, managersInUse);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void excludeFiles(String[] filenames) {
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void excludePattern(String[] regexp) {
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public String filter(String text) {
         return text;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void includeFiles(String[] filenames) {
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void includePattern(String[] regexp) {
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean isFiltered(String path,TestElement sampler) {
         String ipAddr = getIpAddress(path);
         CookieManager cm = getCookieManager(ipAddr);
         ((HTTPSampler)sampler).setCookieManager(cm);
         return false;
     }
 
     protected CookieManager getCookieManager(String ipAddr)
     {
         CookieManager cm;
         // First have to release the cookie we were using so other
         // threads stuck in wait can move on
         synchronized(LOCK) {
             if(lastUsed != null) {
                 managersInUse.remove(lastUsed);
                 LOCK.notifyAll();
             }
         }
         // let notified threads move on and get lock on managersInUse
         if(lastUsed != null) {
             Thread.yield();
         }
         // here is the core routine to find appropriate cookie manager and
         // check it's not being used.  If used, wait until whoever's using it gives
         // it up
         synchronized(LOCK) {
             cm = cookieManagers.get(ipAddr);
             if(cm == null) {
                 cm = new CookieManager();
                 cm.testStarted();
                 cookieManagers.put(ipAddr,cm);
             }
             while(managersInUse.contains(cm)) {
                 try {
                     LOCK.wait();
                 } catch (InterruptedException e) {
                     log.info("SessionFilter wait interrupted");
                     Thread.currentThread().interrupt();
                 }
             }
             managersInUse.add(cm);
             lastUsed = cm;
         }
         return cm;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setReplaceExtension(String oldextension, String newextension) {
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
         synchronized(LOCK) {
             managersInUse.remove(lastUsed);
             LOCK.notifyAll();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void threadStarted() {
         // NOOP
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
index c849e7d9c..f214aec28 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
@@ -1,226 +1,226 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.Serializable;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * Description:<br>
  * <br>
  * StandardGenerator will be the default generator used to pre-process logs. It
  * uses JMeter classes to generate the .jmx file. The first version of the
  * utility only generated the HTTP requests as XML, but it required users to
  * copy and paste it into a blank jmx file. Doing that way isn't flexible and
  * would require changes to keep the format in sync.
  * <p>
  * This version is a completely new class with a totally different
  * implementation, since generating the XML is no longer handled by the
  * generator. The generator is only responsible for handling the parsed results
  * and passing it to the appropriate JMeter class.
  * <p>
  * Notes:<br>
  * the class needs to first create a thread group and add it to the HashTree.
  * Then the samplers should be added to the thread group. Listeners shouldn't be
  * added and should be left up to the user. One option is to provide parameters,
  * so the user can pass the desired listener to the tool.
  */
 
 public class StandardGenerator implements Generator, Serializable {
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StandardGenerator.class);
 
     protected HTTPSamplerBase SAMPLE = null;
 
     protected transient FileWriter WRITER = null;
 
     protected transient OutputStream OUTPUT = null;
 
     protected String FILENAME = null;
 
     protected File FILE = null;
 
     /**
      * The constructor is used by GUI and samplers to generate request objects.
      */
     public StandardGenerator() {
         super();
         init();
     }
 
     /**
      *
      * @param file name of a file (TODO seems not to be used anywhere)
      */
     public StandardGenerator(String file) {
         FILENAME = file;
         init();
     }
 
     /**
      * initialize the generator. It should create the following objects.
      * <p>
      * <ol>
      * <li> ListedHashTree</li>
      * <li> ThreadGroup</li>
      * <li> File object</li>
      * <li> Writer</li>
      * </ol>
      */
     private void init() {// called from ctor, so must not be overridable
         generateRequest();
     }
 
     /**
      * Create the FileWriter to save the JMX file.
      */
     protected void initStream() {
         try {
             this.OUTPUT = new FileOutputStream(FILE);
         } catch (IOException exception) {
             log.error(exception.getMessage());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void close() {
         JOrphanUtils.closeQuietly(OUTPUT);
         JOrphanUtils.closeQuietly(WRITER);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setHost(String host) {
         SAMPLE.setDomain(host);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setLabel(String label) {
 
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setMethod(String post_get) {
         SAMPLE.setMethod(post_get);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setParams(NVPair[] params) {
         for (NVPair param : params) {
             SAMPLE.addArgument(param.getName(), param.getValue());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setPath(String path) {
         SAMPLE.setPath(path);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setPort(int port) {
         SAMPLE.setPort(port);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setQueryString(String querystring) {
         SAMPLE.parseArguments(querystring);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setSourceLogs(String sourcefile) {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setTarget(Object target) {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object generateRequest() {
         SAMPLE = HTTPSamplerFactory.newInstance();
         return SAMPLE;
     }
 
     /**
      * save must be called to write the jmx file, otherwise it will not be
      * saved.
      */
     @Override
     public void save() {
         // no implementation at this time, since
         // we bypass the idea of having a console
         // tool to generate test plans. Instead
         // I decided to have a sampler that uses
         // the generator and parser directly
     }
 
     /**
      * Reset the HTTPSampler to make sure it is a new instance.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void reset() {
         SAMPLE = null;
         generateRequest();
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
index 487c70cf2..eb97984f4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
@@ -1,572 +1,572 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 // For JUnit tests, @see TestTCLogParser
 
 /**
  * Description:<br>
  * <br>
  * Currently the parser only handles GET/POST requests. It's easy enough to add
  * support for other request methods by changing checkMethod. The is a complete
  * rewrite of a tool I wrote for myself earlier. The older algorithm was basic
  * and did not provide the same level of flexibility I want, so I wrote a new
  * one using a totally new algorithm. This implementation reads one line at a
  * time using BufferedReader. When it gets to the end of the file and the
  * sampler needs to get more requests, the parser will re-initialize the
  * BufferedReader. The implementation uses StringTokenizer to create tokens.
  * <p>
  * The parse algorithm is the following:
  * <ol>
  * <li> cleans the entry by looking for backslash "\"
  * <li> looks to see if GET or POST is in the line
  * <li> tokenizes using quotes "
  * <li> finds the token with the request method
  * <li> gets the string of the token and tokenizes it using space
  * <li> finds the first token beginning with slash character
  * <li> tokenizes the string using question mark "?"
  * <li> get the path from the first token
  * <li> returns the second token and checks it for parameters
  * <li> tokenizes the string using ampersand "&amp;"
  * <li> parses each token to name/value pairs
  * </ol>
  * <p>
  * Extending this class is fairly simple. Most access logs use the same format
  * starting from the request method. Therefore, changing the implementation of
  * cleanURL(string) method should be sufficient to support new log formats.
  * Tomcat uses common log format, so any webserver that uses the format should
  * work with this parser. Servers that are known to use non standard formats are
  * IIS and Netscape.
  */
 
 public class TCLogParser implements LogParser {
-    protected static final Logger log = LoggingManager.getLoggerForClass();
+    protected static final Logger log = LoggerFactory.getLogger(TCLogParser.class);
 
     /*
      * TODO should these fields be public?
      * They don't appear to be used externally.
      * 
      * Also, are they any different from HTTPConstants.GET etc. ?
      * In some cases they seem to be used as the method name from the Tomcat log.
      * However the RMETHOD field is used as the value for HTTPSamplerBase.METHOD,
      * for which HTTPConstants is most approriate.
      */
     public static final String GET = "GET";
 
     public static final String POST = "POST";
 
     public static final String HEAD = "HEAD";
 
     /** protected members * */
     protected String RMETHOD = null;
 
     /**
      * The path to the access log file
      */
     protected String URL_PATH = null;
 
     protected boolean useFILE = true;
 
     protected File SOURCE = null;
 
     protected String FILENAME = null;
 
     protected BufferedReader READER = null;
 
     /**
      * Handles to supporting classes
      */
     protected Filter FILTER = null;
 
     /**
      * by default, we probably should decode the parameter values
      */
     protected boolean decode = true;
 
     // TODO downcase UPPER case non-final variables
 
     /**
      *
      */
     public TCLogParser() {
         super();
     }
 
     /**
      * @param source name of the source file
      */
     public TCLogParser(String source) {
         setSourceFile(source);
     }
 
     /**
      * by default decode is set to true. if the parameters shouldn't be
      * decoded, call the method with false
      * @param decodeparams flag whether parameters should be decoded
      */
     public void setDecodeParameterValues(boolean decodeparams) {
         this.decode = decodeparams;
     }
 
     /**
      * decode the parameter values is to true by default
      * @return <code>true</code> if parameter values should be decoded, <code>false</code> otherwise
      */
     public boolean decodeParameterValue() {
         return this.decode;
     }
 
     /**
      * Calls this method to set whether or not to use the path in the log. We
      * may want to provide the ability to filter the log file later on. By
      * default, the parser uses the file in the log.
      *
      * @param file
      *            flag whether to use the path from the log
      */
     public void setUseParsedFile(boolean file) {
         this.useFILE = file;
     }
 
     /**
      * Use the filter to include/exclude files in the access logs. This is
      * provided as a convenience and reduce the need to spend hours cleaning up
      * log files.
      *
      * @param filter {@link Filter} to be used while reading the log lines
      */
     @Override
     public void setFilter(Filter filter) {
         FILTER = filter;
     }
 
     /**
      * Sets the source file.
      *
      * @param source name of the source file
      */
     @Override
     public void setSourceFile(String source) {
         this.FILENAME = source;
     }
 
     /**
      * parse the entire file.
      *
      * @param el TestElement to read the lines into
      * @param parseCount number of max lines to read
      * @return number of read lines, or <code>-1</code> if an error occurred while reading
      */
     public int parse(TestElement el, int parseCount) {
         if (this.SOURCE == null) {
             this.SOURCE = new File(this.FILENAME);
         }
         try {
             if (this.READER == null) {
                 this.READER = getReader(this.SOURCE);
             }
             return parse(this.READER, el, parseCount);
         } catch (Exception exception) {
             log.error("Problem creating samples", exception);
         }
         return -1;// indicate that an error occurred
     }
 
     private static BufferedReader getReader(File file) throws IOException {
         if (! isGZIP(file)) {
             return new BufferedReader(new FileReader(file));
         }
         GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
         return new BufferedReader(new InputStreamReader(in));
     }
 
     private static boolean isGZIP(File file) throws IOException {
         try (FileInputStream in = new FileInputStream(file)) {
             return in.read() == (GZIPInputStream.GZIP_MAGIC & 0xFF)
                 && in.read() == (GZIPInputStream.GZIP_MAGIC >> 8);
         }
     }
 
     /**
      * parse a set number of lines from the access log. Keep in mind the number
      * of lines parsed will depend on the filter and number of lines in the log.
      * The method returns the actual number of lines parsed.
      *
      * @param count number of lines to read
      * @param el {@link TestElement} to read lines into
      * @return lines parsed
      */
     @Override
     public int parseAndConfigure(int count, TestElement el) {
         return this.parse(el, count);
     }
 
     /**
      * The method is responsible for reading each line, and breaking out of the
      * while loop if a set number of lines is given.<br>
      * Note: empty lines will not be counted
      *
      * @param breader {@link BufferedReader} to read lines from
      * @param el {@link TestElement} to read lines into
      * @param parseCount number of lines to read
      * @return number of lines parsed
      */
     protected int parse(BufferedReader breader, TestElement el, int parseCount) {
         int actualCount = 0;
         String line = null;
         try {
             // read one line at a time using
             // BufferedReader
             line = breader.readLine();
             while (line != null) {
                 if (line.length() > 0) {
                     actualCount += this.parseLine(line, el);
                 }
                 // we check the count to see if we have exceeded
                 // the number of lines to parse. There's no way
                 // to know where to stop in the file. Therefore
                 // we use break to escape the while loop when
                 // we've reached the count.
                 if (parseCount != -1 && actualCount >= parseCount) {
                     break;
                 }
                 line = breader.readLine();
             }
             if (line == null) {
                 breader.close();
                 this.READER = null;
                 // this.READER = new BufferedReader(new
                 // FileReader(this.SOURCE));
                 // parse(this.READER,el);
             }
         } catch (IOException ioe) {
             log.error("Error reading log file", ioe);
         }
         return actualCount;
     }
 
     /**
      * parseLine calls the other parse methods to parse the given text.
      *
      * @param line single line to be parsed
      * @param el {@link TestElement} in which the line will be added
      * @return number of lines parsed (zero or one, actually)
      */
     protected int parseLine(String line, TestElement el) {
         int count = 0;
         // we clean the line to get
         // rid of extra stuff
         String cleanedLine = this.cleanURL(line);
         log.debug("parsing line: " + line);
         // now we set request method
         el.setProperty(HTTPSamplerBase.METHOD, RMETHOD);
         if (FILTER != null) {
             log.debug("filter is not null");
             if (!FILTER.isFiltered(line,el)) {
                 log.debug("line was not filtered");
                 // increment the current count
                 count++;
                 // we filter the line first, before we try
                 // to separate the URL into file and
                 // parameters.
                 line = FILTER.filter(cleanedLine);
                 if (line != null) {
                     createUrl(line, el);
                 }
             } else {
                 log.debug("Line was filtered");
             }
         } else {
             log.debug("filter was null");
             // increment the current count
             count++;
             // in the case when the filter is not set, we
             // parse all the lines
             createUrl(cleanedLine, el);
         }
         return count;
     }
 
     /**
      * @param line single line of which the url should be extracted 
      * @param el {@link TestElement} into which the url will be added
      */
     private void createUrl(String line, TestElement el) {
         String paramString = null;
         // check the URL for "?" symbol
         paramString = this.stripFile(line, el);
         if (paramString != null) {
             this.checkParamFormat(line);
             // now that we have stripped the file, we can parse the parameters
             this.convertStringToJMRequest(paramString, el);
         }
     }
 
     /**
      * The method cleans the URL using the following algorithm.
      * <ol>
      * <li> check for double quotes
      * <li> check the request method
      * <li> tokenize using double quotes
      * <li> find first token containing request method
      * <li> tokenize string using space
      * <li> find first token that begins with "/"
      * </ol>
      * Example Tomcat log entry:
      * <p>
      * 127.0.0.1 - - [08/Jan/2003:07:03:54 -0500] "GET /addrbook/ HTTP/1.1" 200
      * 1981
      * <p>
      * would result in the extracted url <code>/addrbook/</code>
      *
      * @param entry line from which the url is to be extracted
      * @return cleaned url
      */
     public String cleanURL(String entry) {
         String url = entry;
         if (entry.contains("\"") && checkMethod(entry)) {
             // we tokenize using double quotes. this means
             // for tomcat we should have 3 tokens if there
             // isn't any additional information in the logs
             StringTokenizer tokens = this.tokenize(entry, "\"");
             while (tokens.hasMoreTokens()) {
                 String token = tokens.nextToken();
                 if (checkMethod(token)) {
                     // we tokenzie it using space and escape
                     // the while loop. Only the first matching
                     // token will be used
                     StringTokenizer token2 = this.tokenize(token, " ");
                     while (token2.hasMoreTokens()) {
                         String t = (String) token2.nextElement();
                         if (t.equalsIgnoreCase(GET)) {
                             RMETHOD = GET;
                         } else if (t.equalsIgnoreCase(POST)) {
                             RMETHOD = POST;
                         } else if (t.equalsIgnoreCase(HEAD)) {
                             RMETHOD = HEAD;
                         }
                         // there should only be one token
                         // that starts with slash character
                         if (t.startsWith("/")) {
                             url = t;
                             break;
                         }
                     }
                     break;
                 }
             }
             return url;
         }
         // we return the original string
         return url;
     }
 
     /**
      * The method checks for <code>POST</code>, <code>GET</code> and <code>HEAD</code> methods currently.
      * The other methods aren't supported yet.
      *
      * @param text text to be checked for HTTP method
      * @return <code>true</code> if method is supported, <code>false</code> otherwise
      */
     public boolean checkMethod(String text) {
         if (text.contains("GET")) {
             this.RMETHOD = GET;
             return true;
         } else if (text.contains("POST")) {
             this.RMETHOD = POST;
             return true;
         } else if (text.contains("HEAD")) {
             this.RMETHOD = HEAD;
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * Tokenize the URL into two tokens. If the URL has more than one "?", the
      * parse may fail. Only the first two tokens are used. The first token is
      * automatically parsed and set at {@link TCLogParser#URL_PATH URL_PATH}.
      *
      * @param url url which should be stripped from parameters
      * @param el {@link TestElement} to parse url into
      * @return String presenting the parameters, or <code>null</code> when none where found
      */
     public String stripFile(String url, TestElement el) {
         if (url.contains("?")) {
             StringTokenizer tokens = this.tokenize(url, "?");
             this.URL_PATH = tokens.nextToken();
             el.setProperty(HTTPSamplerBase.PATH, URL_PATH);
             return tokens.hasMoreTokens() ? tokens.nextToken() : null;
         }
         el.setProperty(HTTPSamplerBase.PATH, url);
         return null;
     }
 
     /**
      * Checks the string to make sure it has <code>/path/file?name=value</code> format. If
      * the string doesn't contains a "?", it will return <code>false</code>.
      *
      * @param url url to check for parameters
      * @return <code>true</code> if url contains a <code>?</code>,
      *         <code>false</code> otherwise
      */
     public boolean checkURL(String url) {
         return url.contains("?");
     }
 
     /**
      * Checks the string to see if it contains "&amp;" and "=". If it does, return
      * <code>true</code>, so that it can be parsed.
      *
      * @param text text to be checked for <code>&amp;</code> and <code>=</code>
      * @return <code>true</code> if <code>text</code> contains both <code>&amp;</code>
      *         and <code>=</code>, <code>false</code> otherwise
      */
     public boolean checkParamFormat(String text) {
         return text.contains("&") && text.contains("=");
     }
 
     /**
      * Convert a single line into XML
      *
      * @param text to be converted
      * @param el {@link HTTPSamplerBase} which consumes the <code>text</code>
      */
     public void convertStringToJMRequest(String text, TestElement el) {
         ((HTTPSamplerBase) el).parseArguments(text);
     }
 
     /**
      * Parse the string parameters into NVPair[] array. Once they are parsed, it
      * is returned. The method uses parseOneParameter(string) to convert each
      * pair.
      *
      * @param stringparams String with parameters to be parsed
      * @return array of {@link NVPair}s
      */
     public NVPair[] convertStringtoNVPair(String stringparams) {
         List<String> vparams = this.parseParameters(stringparams);
         NVPair[] nvparams = new NVPair[vparams.size()];
         // convert the Parameters
         for (int idx = 0; idx < nvparams.length; idx++) {
             nvparams[idx] = this.parseOneParameter(vparams.get(idx));
         }
         return nvparams;
     }
 
     /**
      * Method expects name and value to be separated by an equal sign "=". The
      * method uses StringTokenizer to make a NVPair object. If there happens to
      * be more than one "=" sign, the others are ignored. The chance of a string
      * containing more than one is unlikely and would not conform to HTTP spec.
      * I should double check the protocol spec to make sure this is accurate.
      *
      * @param parameter
      *            to be parsed
      * @return {@link NVPair} with the parsed name and value of the parameter
      */
     protected NVPair parseOneParameter(String parameter) {
         String name = ""; // avoid possible NPE when trimming the name
         String value = null;
         try {
             StringTokenizer param = this.tokenize(parameter, "=");
             name = param.nextToken();
             value = param.nextToken();
         } catch (Exception e) {
             // do nothing. it's naive, but since
             // the utility is meant to parse access
             // logs the formatting should be correct
         }
         if (value == null) {
             value = "";
         } else {
             if (decode) {
                 try {
                     value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
                 } catch (UnsupportedEncodingException e) {
                     log.warn(e.getMessage());
                 }
             }
         }
         return new NVPair(name.trim(), value.trim());
     }
 
     /**
      * Method uses StringTokenizer to convert the string into single pairs. The
      * string should conform to HTTP protocol spec, which means the name/value
      * pairs are separated by the ampersand symbol "&amp;". Someone could write the
      * querystrings by hand, but that would be round about and go against the
      * purpose of this utility.
      *
      * @param parameters string to be parsed
      * @return List of name/value pairs
      */
     protected List<String> parseParameters(String parameters) {
         List<String> parsedParams = new ArrayList<>();
         StringTokenizer paramtokens = this.tokenize(parameters, "&");
         while (paramtokens.hasMoreElements()) {
             parsedParams.add(paramtokens.nextToken());
         }
         return parsedParams;
     }
 
     /**
      * Parses the line using java.util.StringTokenizer.
      *
      * @param line
      *            line to be parsed
      * @param delim
      *            delimiter
      * @return StringTokenizer constructed with <code>line</code> and <code>delim</code>
      */
     public StringTokenizer tokenize(String line, String delim) {
         return new StringTokenizer(line, delim);
     }
 
     @Override
     public void close() {
         try {
             this.READER.close();
             this.READER = null;
             this.SOURCE = null;
         } catch (IOException e) {
             // do nothing
         }
     }
 }
