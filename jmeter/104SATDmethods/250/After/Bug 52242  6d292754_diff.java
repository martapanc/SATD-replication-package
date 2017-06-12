diff --git a/src/core/org/apache/jmeter/testbeans/gui/FileEditor.java b/src/core/org/apache/jmeter/testbeans/gui/FileEditor.java
index 5631daff7..1bd676e7f 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/FileEditor.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/FileEditor.java
@@ -1,235 +1,222 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Graphics;
 import java.awt.Rectangle;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.beans.IntrospectionException;
 import java.beans.PropertyChangeListener;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditor;
 import java.beans.PropertyEditorSupport;
 import java.io.File;
 
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 
 import org.apache.jmeter.gui.util.FileDialoger;
 
 /**
  * A property editor for File properties.
  * <p>
  * Note that it never gives out File objects, but always Strings. This is
  * because JMeter is now too dumb to handle File objects (there's no
  * FileProperty).
  *
  */
 public class FileEditor implements PropertyEditor, ActionListener {
 
     /**
      * The editor's panel.
      */
     private final JPanel panel;
 
     /**
      * The editor handling the text field inside:
      */
     private final PropertyEditor editor;
 
     /**
      * @throws IntrospectionException 
      * @deprecated Only for use by test cases
      */
     @Deprecated
     public FileEditor() throws IntrospectionException {
         this(new PropertyDescriptor("dummy", null, null));
     }
 
     public FileEditor(PropertyDescriptor descriptor) {
         if (descriptor == null) {
             throw new NullPointerException("Descriptor must not be null");
         }
 
         // Create a button to trigger the file chooser:
         JButton button = new JButton("Browse...");
         button.addActionListener(this);
 
         // Get a WrapperEditor to provide the field or combo -- we'll delegate
         // most methods to it:
         boolean notNull = GenericTestBeanCustomizer.notNull(descriptor);
         boolean notExpression = GenericTestBeanCustomizer.notExpression(descriptor);
         boolean notOther = GenericTestBeanCustomizer.notOther(descriptor);
         Object defaultValue = descriptor.getValue(GenericTestBeanCustomizer.DEFAULT);
         ComboStringEditor cse = new ComboStringEditor();
         cse.setNoUndefined(notNull);
         cse.setNoEdit(notExpression && notOther);
         editor = new WrapperEditor(this, new SimpleFileEditor(), cse,
                 !notNull, // acceptsNull
                 !notExpression, // acceptsExpressions
                 !notOther, // acceptsOther
                 defaultValue); // default
 
         // Create a panel containing the combo and the button:
         panel = new JPanel(new BorderLayout(5, 0));
         panel.add(editor.getCustomEditor(), BorderLayout.CENTER);
         panel.add(button, BorderLayout.EAST);
     }
 
     /**
      * {@inheritDoc}
      */
     public void actionPerformed(ActionEvent e) {
         JFileChooser chooser = FileDialoger.promptToOpenFile();
 
         if (chooser == null){
             return;
         }
 
         setValue(chooser.getSelectedFile().getPath());
     }
 
     /**
      * @param listener
      */
     public void addPropertyChangeListener(PropertyChangeListener listener) {
         editor.addPropertyChangeListener(listener);
     }
 
     /**
      * @return the text
      */
     public String getAsText() {
         return editor.getAsText();
     }
 
     /**
      * @return custom editor panel
      */
     public Component getCustomEditor() {
         return panel;
     }
 
     /**
      * @return the Java initialisation string
      */
     public String getJavaInitializationString() {
         return editor.getJavaInitializationString();
     }
 
     /**
      * @return the editor tags
      */
     public String[] getTags() {
         return editor.getTags();
     }
 
     /**
      * @return the value
      */
     public Object getValue() {
         return editor.getValue();
     }
 
     /**
      * @return true if the editor is paintable
      */
     public boolean isPaintable() {
         return editor.isPaintable();
     }
 
     /**
      * @param gfx
      * @param box
      */
     public void paintValue(Graphics gfx, Rectangle box) {
         editor.paintValue(gfx, box);
     }
 
     /**
      * @param listener
      */
     public void removePropertyChangeListener(PropertyChangeListener listener) {
         editor.removePropertyChangeListener(listener);
     }
 
     /**
      * @param text
      * @throws java.lang.IllegalArgumentException
      */
     public void setAsText(String text) throws IllegalArgumentException {
         editor.setAsText(text);
     }
 
     /**
      * @param value
      */
     public void setValue(Object value) {
         editor.setValue(value);
     }
 
     /**
      * @return true if supports a custom editor
      */
     public boolean supportsCustomEditor() {
         return editor.supportsCustomEditor();
     }
 
     private static class SimpleFileEditor extends PropertyEditorSupport {
 
-        /**
-         * {@inheritDoc}
-         */
         @Override
         public String getAsText() {
-            return ((File) super.getValue()).getPath();
+            Object value = super.getValue();
+            if (value instanceof File) {
+                return ((File) value).getPath();
+            }
+            return (String) value; // assume it's string
         }
 
-        /**
-         * {@inheritDoc}
-         */
         @Override
         public void setAsText(String text) throws IllegalArgumentException {
             super.setValue(new File(text));
         }
 
-        /**
-         * JMeter doesn't support File properties yet. Need to
-         * work on this as a String :-(
-         * <p>
-         * {@inheritDoc}
-         */
         @Override
         public Object getValue() {
-            return getAsText(); // should be super.getValue();
+            return super.getValue();
         }
 
-        /**
-         * I need to handle Strings when setting too.
-         * <p>
-         * {@inheritDoc}
-         */
         @Override
         public void setValue(Object file) {
-            setAsText((String) file);
+            super.setValue(file);
         }
     }
 }
\ No newline at end of file
diff --git a/src/jorphan/org/apache/jorphan/util/Converter.java b/src/jorphan/org/apache/jorphan/util/Converter.java
index bafb6f6d6..ae2525eda 100644
--- a/src/jorphan/org/apache/jorphan/util/Converter.java
+++ b/src/jorphan/org/apache/jorphan/util/Converter.java
@@ -1,370 +1,383 @@
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
 package org.apache.jorphan.util;
 
+import java.io.File;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.StringTokenizer;
 
 /**
  * Converter utilities for TestBeans
  */
 public class Converter {
 
     /**
      * Convert the given value object to an object of the given type
      *
      * @param value
      * @param toType
      * @return Object
      */
     public static Object convert(Object value, Class<?> toType) {
         if (value == null) {
-            value = "";
+            value = ""; // TODO should we allow null for non-primitive types?
         } else if (toType.isAssignableFrom(value.getClass())) {
             return value;
         } else if (toType.equals(float.class) || toType.equals(Float.class)) {
             return new Float(getFloat(value));
         } else if (toType.equals(double.class) || toType.equals(Double.class)) {
             return new Double(getDouble(value));
         } else if (toType.equals(String.class)) {
             return getString(value);
         } else if (toType.equals(int.class) || toType.equals(Integer.class)) {
             return Integer.valueOf(getInt(value));
         } else if (toType.equals(char.class) || toType.equals(Character.class)) {
             return Character.valueOf(getChar(value));
         } else if (toType.equals(long.class) || toType.equals(Long.class)) {
             return Long.valueOf(getLong(value));
         } else if (toType.equals(boolean.class) || toType.equals(Boolean.class)) {
             return  Boolean.valueOf(getBoolean(value));
         } else if (toType.equals(java.util.Date.class)) {
             return getDate(value);
         } else if (toType.equals(Calendar.class)) {
             return getCalendar(value);
+        } else if (toType.equals(File.class)) {
+            return getFile(value);
         } else if (toType.equals(Class.class)) {
             try {
                 return Class.forName(value.toString());
             } catch (Exception e) {
                 // don't do anything
             }
         }
         return value;
     }
 
     /**
      * Converts the given object to a calendar object. Defaults to the current
      * date if the given object can't be converted.
      *
      * @param date
      * @return Calendar
      */
     public static Calendar getCalendar(Object date, Calendar defaultValue) {
         Calendar cal = new GregorianCalendar();
         if (date != null && date instanceof java.util.Date) {
             cal.setTime((java.util.Date) date);
             return cal;
         } else if (date != null) {
             DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
             java.util.Date d = null;
             try {
                 d = formatter.parse(date.toString());
             } catch (ParseException e) {
                 formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                 try {
                     d = formatter.parse((String) date);
                 } catch (ParseException e1) {
                     formatter = DateFormat.getDateInstance(DateFormat.LONG);
                     try {
                         d = formatter.parse((String) date);
                     } catch (ParseException e2) {
                         formatter = DateFormat.getDateInstance(DateFormat.FULL);
                         try {
                             d = formatter.parse((String) date);
                         } catch (ParseException e3) {
                             return defaultValue;
                         }
                     }
                 }
             }
             cal.setTime(d);
         } else {
             cal = defaultValue;
         }
         return cal;
     }
 
     public static Calendar getCalendar(Object o) {
         return getCalendar(o, new GregorianCalendar());
     }
 
     public static Date getDate(Object date) {
         return getDate(date, Calendar.getInstance().getTime());
     }
 
     public static Date getDate(Object date, Date defaultValue) {
         Date val = null;
         if (date != null && date instanceof java.util.Date) {
             return (Date) date;
         } else if (date != null) {
             DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
             // java.util.Date d = null;
             try {
                 val = formatter.parse(date.toString());
             } catch (ParseException e) {
                 formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                 try {
                     val = formatter.parse((String) date);
                 } catch (ParseException e1) {
                     formatter = DateFormat.getDateInstance(DateFormat.LONG);
                     try {
                         val = formatter.parse((String) date);
                     } catch (ParseException e2) {
                         formatter = DateFormat.getDateInstance(DateFormat.FULL);
                         try {
                             val = formatter.parse((String) date);
                         } catch (ParseException e3) {
                             return defaultValue;
                         }
                     }
                 }
             }
         } else {
             return defaultValue;
         }
         return val;
     }
 
     public static float getFloat(Object o, float defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).floatValue();
             }
             return Float.parseFloat(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
     public static float getFloat(Object o) {
         return getFloat(o, 0);
     }
 
     public static double getDouble(Object o, double defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).doubleValue();
             }
             return Double.parseDouble(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
     public static double getDouble(Object o) {
         return getDouble(o, 0);
     }
 
     public static boolean getBoolean(Object o) {
         return getBoolean(o, false);
     }
 
     public static boolean getBoolean(Object o, boolean defaultValue) {
         if (o == null) {
             return defaultValue;
         } else if (o instanceof Boolean) {
             return ((Boolean) o).booleanValue();
         }
         return Boolean.valueOf(o.toString()).booleanValue();
     }
 
     /**
      * Convert object to integer, return defaultValue if object is not
      * convertible or is null.
      *
      * @param o
      * @param defaultValue
      * @return int
      */
     public static int getInt(Object o, int defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).intValue();
             }
             return Integer.parseInt(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
     public static char getChar(Object o) {
         return getChar(o, ' ');
     }
 
     public static char getChar(Object o, char defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Character) {
                 return ((Character) o).charValue();
             } else if (o instanceof Byte) {
                 return (char) ((Byte) o).byteValue();
             } else if (o instanceof Integer) {
                 return (char) ((Integer) o).intValue();
             } else {
                 String s = o.toString();
                 if (s.length() > 0) {
                     return o.toString().charAt(0);
                 }
                 return defaultValue;
             }
         } catch (Exception e) {
             return defaultValue;
         }
     }
 
     /**
      * Converts object to an integer, defaults to 0 if object is not convertible
      * or is null.
      *
      * @param o
      * @return int
      */
     public static int getInt(Object o) {
         return getInt(o, 0);
     }
 
     /**
      * Converts object to a long, return defaultValue if object is not
      * convertible or is null.
      *
      * @param o
      * @param defaultValue
      * @return long
      */
     public static long getLong(Object o, long defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).longValue();
             }
             return Long.parseLong(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
     /**
      * Converts object to a long, defaults to 0 if object is not convertible or
      * is null
      *
      * @param o
      * @return long
      */
     public static long getLong(Object o) {
         return getLong(o, 0);
     }
 
     public static String formatDate(Date date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date);
     }
 
     public static String formatDate(java.sql.Date date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date);
     }
 
     public static String formatDate(String date, String pattern) {
         return formatDate(getCalendar(date, null), pattern);
     }
 
     public static String formatDate(Calendar date, String pattern) {
         return formatCalendar(date, pattern);
     }
 
     public static String formatCalendar(Calendar date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date.getTime());
     }
 
     /**
      * Converts object to a String, return defaultValue if object is null.
      *
      * @param o
      * @param defaultValue
      * @return String
      */
     public static String getString(Object o, String defaultValue) {
         if (o == null) {
             return defaultValue;
         }
         return o.toString();
     }
 
     public static String insertLineBreaks(String v, String insertion) {
         if (v == null) {
             return "";
         }
         StringBuilder replacement = new StringBuilder();
         StringTokenizer tokens = new StringTokenizer(v, "\n", true);
         while (tokens.hasMoreTokens()) {
             String token = tokens.nextToken();
             if (token.compareTo("\n") == 0) {
                 replacement.append(insertion);
             } else {
                 replacement.append(token);
             }
         }
         return replacement.toString();
     }
 
     /**
      * Converts object to a String, defaults to empty string if object is null.
      *
      * @param o
      * @return String
      */
     public static String getString(Object o) {
         return getString(o, "");
     }
+    
+    public static File getFile(Object o){
+        if (o instanceof File) {
+            return (File) o;
+        }
+        if (o instanceof String) {
+            return new File((String) o);
+        }
+        throw new IllegalArgumentException("Expected String or file, actual "+o.getClass().getName());
+    }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index fde67f987..f9896b172 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,244 +1,245 @@
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
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.5.2 =================== -->
 
 <h1>Version 2.5.2</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode (see Bugs 40671, 41286, 44973, 50898). 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>The If Controller may cause an infinite loop if the condition is always false from the first iteration. 
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 JMeter versions since 2.1 failed to create a container sample when loading embedded resources.
 This has been corrected; can still revert to the Bug 51939 behaviour by setting the following property:
 <code>httpsampler.separate.container=false</code>
 </p>
 <p>
 Mirror server now uses default port 8081, was 8080 before 2.5.1.
 </p>
 <p>
 TCP Sampler handles SocketTimeoutException, SocketException and InterruptedIOException differently since 2.5.2, when
 these occurs, Sampler is marked as failed.
 </p>
 <p>
 Sample Sender implementations know resolve their configuration on Client side since 2.5.2.
 This behaviour can be changed with property sample_sender_client_configured (set it to false).
 </p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 51932 - CacheManager does not handle cache-control header with any attributes after max-age</li>
 <li>Bug 51918 - GZIP compressed traffic produces errors, when multiple connections allowed</li>
 <li>Bug 51939 - Should generate new parent sample if necessary when retrieving embedded resources</li>
 <li>Bug 51942 - Synchronisation issue on CacheManager when Concurrent Download is used</li>
 <li>Bug 51957 - Concurrent get can hang if a task does not complete</li>
 <li>Bug 51925 - Calling Stop on Test leaks executor threads when concurrent download of resources is on</li>
 <li>Bug 51980 - HtmlParserHTMLParser double-counts images used in links</li>
 <li>Bug 52064 - OutOfMemory Risk in CacheManager</li>
 <li>Bug 51919 - Random ConcurrentModificationException or NoSuchElementException in CookieManager#removeMatchingCookies when using Concurrent Download</li>
 <li>Bug 52126 - HttpClient4 does not clear cookies between iterations</li>
 <li>Bug 52129 - Reported Body Size is wrong when using HTTP Client 4 and Keep Alive connection</li>
 <li>Bug 52137 - Problems with HTTP Cache Manager</li>
 <li>Bug 52221 - Nullpointer Exception with use Retrieve Embedded Resource without HTTP Cache Manager</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51996 - JMS Initial Context leak newly created Context when Multiple Thread enter InitialContextFactory#lookupContext at the same time</li>
 <li>Bug 51691 - Authorization does not work for JMS Publisher and JMS Subscriber</li>
 <li>Bug 52036 - Durable Subscription fails with ActiveMQ due to missing clientId field</li>
 <li>Bug 52044 - JMS Subscriber used with many threads leads to javax.naming.NamingException: Something already bound with ActiveMQ</li>
 <li>Bug 52072 - LengthPrefixedBinaryTcpClientImpl may end a sample prematurely</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 51865 - Infinite loop inside thread group does not work properly if "Start next loop after a Sample error" option set</li>
 <li>Bug 51868 - A lot of exceptions in jmeter.log while using option "Start next loop" for thread</li>
 <li>Bug 51866 - Counter under loop doesn't work properly if "Start next loop on error" option set for thread group</li>
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
 <li>Bug 51937 - JMeter does not handle missing TestPlan entry well</li>
 <li>Bug 51988 - CSV Data Set Configuration does not resolve default delimiter for header parsing when variables field is empty</li>
 <li>Bug 52003 - View Results Tree "Scroll automatically" does not scroll properly in case nodes are expanded</li>
 <li>Bug 27112 - User Parameters should use scrollbars</li>
 <li>Bug 52029 - Command-line shutdown only gets sent to last engine that was started</li>
 <li>Bug 52093 - Toolbar ToolTips don't switch language</li>
 <li>Bug 51733 - SyncTimer is messed up if you a interrupt a test plan</li>
 <li>Bug 52118 - New toolbar : shutdown and stop buttons not disabled when no test is running</li>
 <li>Bug 52125 - StatCalculator.addAll(StatCalculator calc) joins incorrect if there are more samples with the same response time in one of the TreeMap</li>
 <li>Bug 52215 - Confusing synchronization in StatVisualizer, SummaryReport ,Summariser and issue in StatGraphVisualizer</li>
 <li>Bug 52216 - TableVisualizer : currentData field is badly synchronized</li>
 <li>Bug 52217 - ViewResultsFullVisualizer : Synchronization issues on root and treeModel</li>
 <li>Bug 43294 - XPath Extractor namespace problems</li>
 <li>Bug 52224 - TestBeanHelper does not support NOT_UNDEFINED == Boolean.FALSE</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 51981 - Better support for file: protocol in HTTP sampler</li>
 <li>Bug 52033 - Allowing multiple certificates (JKS)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 51419 - JMS Subscriber: ability to use Selectors</li>
 <li>Bug 52088 - JMS Sampler : Add a selector when REQUEST / RESPONSE is chosen</li>
 <li>Bug 52104 - TCP Sampler handles badly errors</li>
 <li>Bug 52087 - TCPClient interface does not allow for partial reads</li>
 <li>Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found</li>
 <li>Bug 40750 - TCPSampler : Behaviour when sockets are closed by remote host</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured</li>
 <li>Bug 52201 - Add option to TableVisualiser to display child samples instead of parent </li>
 <li>Bug 52214 - Save Responses to a file - improve naming algorithm</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 52128 - Add JDBC pre- and post-processor</li>
 <li>Bug 52183 - SyncTimer could be improved (performance+reliability)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51892 - Default mirror port should be different from default proxy port</li>
 <li>Bug 51817 - Moving variables up and down in User Defined Variables control</li>
 <li>Bug 51876 - Functionality to search in Samplers TreeView</li>
 <li>Bug 52019 - Add menu option to Start a test ignoring Pause Timers</li>
 <li>Bug 52027 - Allow System or CrossPlatform LAF to be set from options menu</li>
 <li>Bug 52037 - Remember user-set LaF over restarts.</li>
 <li>Bug 51861 - Improve HTTP Request GUI to better show parameters without name (GWT RPC requests for example) (UNDER DEVELOPMENT)</li>
 <li>Bug 52040 - Add a toolbar in JMeter main window</li>
 <li>Bug 51816 - Comment Field in User Defined Variables control.</li>
 <li>Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber</li>
 <li>Bug 52103 - Add automatic scrolling option to table visualizer</li>
 <li>Bug 52097 - Save As should point to same folder that was used to open a file if MRU list is used</li>
 <li>Bug 52085 - Allow multiple selection in arguments panel</li>
 <li>Bug 52099 - Allow to set the transaction isolation in the JDBC Connection Configuration</li>
 <li>Bug 52116 - Allow to add (paste) entries from the clipboard to an arguments list</li>
 <li>Bug 51091 - New function returning the name of the current "Test Plan"</li>
 <li>Bug 52160 - Don't display TestBeanGui items which are flagged as hidden</li>
 <li>Bug 51886 - SampleSender configuration resolved partly on client and partly on server</li>
 <li>Bug 52161 - Enable plugins to add own translation rules in addition to upgrade.properties.
 Loads any additional properties found in META-INF/resources/org.apache.jmeter.nameupdater.properties files</li>
 <li>Bug 42538 - Add "duplicate node" in context menu</li>
 <li>Bug 46921 - Add Ability to Change Controller elements</li>
 <li>Bug 52240 - TestBeans should support Boolean, Integer and Long</li>
 <li>Bug 52241 - GenericTestBeanCustomizer assumes that the default value is the empty string</li>
+<li>Bug 52242 - FileEditor does not allow output to be saved in a File </li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>fixes to build.xml: support scripts; localise re-usable property names</li>
 <li>Bug 51923 - Counter function bug or documentation issue ? (fixed docs)</li>
 <li>Update velocity.jar to 1.7 (from 1.6.2)</li>
 <li>Bug 51954 - Generated documents include &lt;/br&gt; entries which cause extra blank lines </li>
 <li>Bug 52075 - JMeterProperty.clone() currently returns Object; it should return JMeterProperty</li>
 </ul>
 
 </section> 
 </body> 
 </document>
