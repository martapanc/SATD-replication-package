diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional1.html b/bin/testfiles/HTMLParserTestCaseWithConditional1.html
new file mode 100644
index 000000000..332f93777
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional1.html
@@ -0,0 +1,51 @@
+<HTML>
+<head>
+<!--[if IE 7]>
+<link rel="stylesheet" type="text/css" href="fileForIE7.css">
+<![endif]-->
+<!--[if IE 8]>
+<link rel="stylesheet" type="text/css" href="fileForIE8.css">
+<![endif]-->
+</head>
+<body background="images/body&amp;soul.gif">
+<table background="images/table.gif">
+<tr style="background url('images/tr.gif')">
+<td background="images/td.gif"><img name="a" src="images/image-a.gif" align="top"></td>
+</tr>
+</table>
+<img src="images/image-b.gif" name="b" align="top"/>
+<a href="somewhere"><img src="images/image-b.gif" name="b" align="top"/></a>
+<img align="top" name="c" src= "images/image-c.gif">
+<img name="d" align="top" src ="images/image-d.gif"/>
+<img
+  align="top"
+  name="e"
+  src =
+    "images/image-e.gif"
+>
+<img
+  align="top"
+  Name="F"
+  sRc = "images/sub/image-f.gif"
+/>
+<input name="a" src="images/image-a2.gif" type="image"/>
+<input src="images/image-b2.gif" name="b" type="image">
+<input type= "image" name="c" src= "images/sub/image-c2.gif"/>
+<input name="d" type ="image" src ="images/image-d2.gif">
+<input name="d2" type ="image" src ="images/image-d2.gif">
+<input name="d3" type ="imagex" src ="images/image-d2.gif">
+<input
+  type =
+    "image"
+  name="e"
+  src =
+    "images/image-e2.gif"
+/>
+<input
+  type = "image"
+  Name="F"
+  sRc = "images/image-f2.gif"
+>
+
+</body>
+</html>
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional1_FF.all b/bin/testfiles/HTMLParserTestCaseWithConditional1_FF.all
new file mode 100644
index 000000000..1b3116280
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional1_FF.all
@@ -0,0 +1,18 @@
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional1_IE6.all b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE6.all
new file mode 100644
index 000000000..1b3116280
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE6.all
@@ -0,0 +1,18 @@
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional1_IE7.all b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE7.all
new file mode 100644
index 000000000..220542630
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE7.all
@@ -0,0 +1,19 @@
+http://localhost/mydir/fileForIE7.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional1_IE8.all b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE8.all
new file mode 100644
index 000000000..cdb9e3a92
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional1_IE8.all
@@ -0,0 +1,19 @@
+http://localhost/mydir/fileForIE8.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional2.html b/bin/testfiles/HTMLParserTestCaseWithConditional2.html
new file mode 100644
index 000000000..fbe27eac5
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional2.html
@@ -0,0 +1,54 @@
+<HTML>
+<head>
+<!--[if gt IE 6]>
+    <!--[if lte IE 8]>
+        <!--[if IE 7]>
+            <link rel="stylesheet" type="text/css" href="fileForIE7.css">
+        <![endif]-->
+        <link rel="stylesheet" type="text/css" href="fileForIE7or8.css">
+    <![endif]-->
+    <link rel="stylesheet" type="text/css" href="fileForIE7or8or9.css">
+<![endif]-->
+</head>
+<body background="images/body&amp;soul.gif">
+<table background="images/table.gif">
+<tr style="background url('images/tr.gif')">
+<td background="images/td.gif"><img name="a" src="images/image-a.gif" align="top"></td>
+</tr>
+</table>
+<img src="images/image-b.gif" name="b" align="top"/>
+<a href="somewhere"><img src="images/image-b.gif" name="b" align="top"/></a>
+<img align="top" name="c" src= "images/image-c.gif">
+<img name="d" align="top" src ="images/image-d.gif"/>
+<img
+  align="top"
+  name="e"
+  src =
+    "images/image-e.gif"
+>
+<img
+  align="top"
+  Name="F"
+  sRc = "images/sub/image-f.gif"
+/>
+<input name="a" src="images/image-a2.gif" type="image"/>
+<input src="images/image-b2.gif" name="b" type="image">
+<input type= "image" name="c" src= "images/sub/image-c2.gif"/>
+<input name="d" type ="image" src ="images/image-d2.gif">
+<input name="d2" type ="image" src ="images/image-d2.gif">
+<input name="d3" type ="imagex" src ="images/image-d2.gif">
+<input
+  type =
+    "image"
+  name="e"
+  src =
+    "images/image-e2.gif"
+/>
+<input
+  type = "image"
+  Name="F"
+  sRc = "images/image-f2.gif"
+>
+
+</body>
+</html>
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional2_FF.all b/bin/testfiles/HTMLParserTestCaseWithConditional2_FF.all
new file mode 100644
index 000000000..5cd48dba9
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional2_FF.all
@@ -0,0 +1,20 @@
+http://localhost/mydir/fileForIE7or8.css
+http://localhost/mydir/fileForIE7or8or9.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional2_IE7.all b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE7.all
new file mode 100644
index 000000000..0b9e4650f
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE7.all
@@ -0,0 +1,21 @@
+http://localhost/mydir/fileForIE7.css
+http://localhost/mydir/fileForIE7or8.css
+http://localhost/mydir/fileForIE7or8or9.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional2_IE8.all b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE8.all
new file mode 100644
index 000000000..5cd48dba9
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE8.all
@@ -0,0 +1,20 @@
+http://localhost/mydir/fileForIE7or8.css
+http://localhost/mydir/fileForIE7or8or9.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional2_IE9.all b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE9.all
new file mode 100644
index 000000000..f03ccce60
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional2_IE9.all
@@ -0,0 +1,19 @@
+http://localhost/mydir/fileForIE7or8or9.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional3.html b/bin/testfiles/HTMLParserTestCaseWithConditional3.html
new file mode 100644
index 000000000..ec0aec1b8
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional3.html
@@ -0,0 +1,50 @@
+<HTML>
+<head>
+<!--[if gt IE 5]>
+    <![if lt IE 6]>
+        <link rel="stylesheet" type="text/css" href="fileForIE55.css">
+    <![endif]>
+<![endif]-->
+</head>
+<body background="images/body&amp;soul.gif">
+<table background="images/table.gif">
+<tr style="background url('images/tr.gif')">
+<td background="images/td.gif"><img name="a" src="images/image-a.gif" align="top"></td>
+</tr>
+</table>
+<img src="images/image-b.gif" name="b" align="top"/>
+<a href="somewhere"><img src="images/image-b.gif" name="b" align="top"/></a>
+<img align="top" name="c" src= "images/image-c.gif">
+<img name="d" align="top" src ="images/image-d.gif"/>
+<img
+  align="top"
+  name="e"
+  src =
+    "images/image-e.gif"
+>
+<img
+  align="top"
+  Name="F"
+  sRc = "images/sub/image-f.gif"
+/>
+<input name="a" src="images/image-a2.gif" type="image"/>
+<input src="images/image-b2.gif" name="b" type="image">
+<input type= "image" name="c" src= "images/sub/image-c2.gif"/>
+<input name="d" type ="image" src ="images/image-d2.gif">
+<input name="d2" type ="image" src ="images/image-d2.gif">
+<input name="d3" type ="imagex" src ="images/image-d2.gif">
+<input
+  type =
+    "image"
+  name="e"
+  src =
+    "images/image-e2.gif"
+/>
+<input
+  type = "image"
+  Name="F"
+  sRc = "images/image-f2.gif"
+>
+
+</body>
+</html>
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional3_FF.all b/bin/testfiles/HTMLParserTestCaseWithConditional3_FF.all
new file mode 100644
index 000000000..1b3116280
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional3_FF.all
@@ -0,0 +1,18 @@
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional3_IE10.all b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE10.all
new file mode 100644
index 000000000..1b3116280
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE10.all
@@ -0,0 +1,18 @@
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional3_IE55.all b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE55.all
new file mode 100644
index 000000000..b969c0918
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE55.all
@@ -0,0 +1,19 @@
+http://localhost/mydir/fileForIE55.css
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/bin/testfiles/HTMLParserTestCaseWithConditional3_IE6.all b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE6.all
new file mode 100644
index 000000000..1b3116280
--- /dev/null
+++ b/bin/testfiles/HTMLParserTestCaseWithConditional3_IE6.all
@@ -0,0 +1,18 @@
+http://localhost/mydir/images/body&soul.gif
+http://localhost/mydir/images/table.gif
+http://localhost/mydir/images/tr.gif
+http://localhost/mydir/images/td.gif
+http://localhost/mydir/images/image-a.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-b.gif
+http://localhost/mydir/images/image-c.gif
+http://localhost/mydir/images/image-d.gif
+http://localhost/mydir/images/image-e.gif
+http://localhost/mydir/images/sub/image-f.gif
+http://localhost/mydir/images/image-a2.gif
+http://localhost/mydir/images/image-b2.gif
+http://localhost/mydir/images/sub/image-c2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-d2.gif
+http://localhost/mydir/images/image-e2.gif
+http://localhost/mydir/images/image-f2.gif
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 722af3be1..8eece68c9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,212 +1,262 @@
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
 
+
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
 
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
 
+    protected static final String IE_UA             = "MSIE ([0-9]+.[0-9]+)";// $NON-NLS-1$
+    protected static final Pattern IE_UA_PATTERN    = Pattern.compile(IE_UA);
+    private   static final float IE_10                = 10.0f;
+
     // Cache of parsers - parsers must be re-usable
     private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(4);
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
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
+     * @param userAgent
+     *            User Agent
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<URLString>();
-        return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(col),encoding);
+        return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(col),encoding);
 
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
+     * @param userAgent
+     *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
-    public abstract Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection coll, String encoding)
+    public abstract Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      *
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
+     * @param userAgent
+     *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      */
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) throws HTMLParseException {
-        return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(coll), encoding);
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) throws HTMLParseException {
+        return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(coll), encoding);
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
+    
+    /**
+     * 
+     * @param ieVersion Float IE version
+     * @return
+     */
+    protected final boolean isEnableConditionalComments(Float ieVersion) {
+        if(ieVersion == null) {
+            return false;
+        }
+        // Conditionnal comment have been dropped in IE10
+        // http://msdn.microsoft.com/en-us/library/ie/hh801214%28v=vs.85%29.aspx
+        return ieVersion.floatValue() < IE_10;
+    }
+    
+    /**
+     * 
+     * @param userAgent User Agent
+     * @return version null if not IE or the version after MSIE
+     */
+    protected Float extractIEVersion(String userAgent) {
+        Matcher matcher = IE_UA_PATTERN.matcher(userAgent);
+        String ieVersion = null;
+        while (matcher.find()) {
+            if (matcher.groupCount() > 0) {
+                ieVersion = matcher.group(1);
+            } else {
+                ieVersion = matcher.group();
+            }
+            break;
+        }
+        if(ieVersion != null) {
+            return Float.valueOf(ieVersion);
+        } else {
+            return null;
+        }
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
index ac308f397..65f505838 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParserHTMLParser.java
@@ -1,206 +1,206 @@
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
 import org.htmlparser.tags.ObjectTag;
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
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
 
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
                         baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
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
             } else if (tag instanceof ObjectTag) {
                 // look for Objects
                 ObjectTag applet = (ObjectTag) tag; 
                 String data = applet.getAttribute(ATT_CODEBASE);
                 if(!StringUtils.isEmpty(data)) {
                     binUrlStr = data;               
                 }
                 
                 data = applet.getAttribute(ATT_DATA);
                 if(!StringUtils.isEmpty(data)) {
                     binUrlStr = data;                    
                 }
                 
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
index 801624fa0..27b7fb435 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
@@ -1,248 +1,248 @@
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
 
 import org.apache.commons.lang3.StringUtils;
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
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
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
             
             if (name.equalsIgnoreCase(TAG_OBJECT)) {
                 String data = getValue(attrs, "codebase");
                 if(!StringUtils.isEmpty(data)) {
                     urls.addURL(data, baseUrl);                    
                 }
                 
                 data = getValue(attrs, "data");
                 if(!StringUtils.isEmpty(data)) {
                     urls.addURL(data, baseUrl);                    
                 }
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
index ece65c377..f7689d04c 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
@@ -1,162 +1,163 @@
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
 //import org.apache.jorphan.logging.LoggingManager;
 //import org.apache.log.Logger;
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
 //    private static final Logger log = LoggingManager.getLoggerForClass();
 
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
          * @param baseUrl
          * @param urls
          */
         public JMeterNodeVisitor(final URLPointer baseUrl, URLCollection urls) {
             this.urls = urls;
             this.baseUrl = baseUrl;
         }
 
         private final void extractAttribute(Element tag, String attributeName) {
             String url = tag.attr(attributeName);
             if (!StringUtils.isEmpty(url)) {
                 urls.addURL(url, baseUrl.url);
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
             } else if (tagName.equals(TAG_SCRIPT)) {
                 extractAttribute(tag, ATT_SRC);
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
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl,
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
+            // TODO Handle conditional comments for IE
             String contents = new String(html,encoding);
             Document doc = Jsoup.parse(contents);
             JMeterNodeVisitor nodeVisitor = new JMeterNodeVisitor(new URLPointer(baseUrl), coll);
             new NodeTraversor(nodeVisitor).traverse(doc);
             return coll.iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.parser.HTMLParser#isReusable()
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
index 5d802f742..5a6badf6d 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
@@ -1,177 +1,241 @@
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
+import java.util.Stack;
 
 import jodd.lagarto.EmptyTagVisitor;
 import jodd.lagarto.LagartoException;
 import jodd.lagarto.LagartoParser;
+import jodd.lagarto.LagartoParserConfig;
 import jodd.lagarto.Tag;
+import jodd.lagarto.TagType;
+import jodd.lagarto.TagUtil;
+import jodd.lagarto.dom.HtmlCCommentExpressionMatcher;
 import jodd.log.LoggerFactory;
 import jodd.log.impl.Slf4jLoggerFactory;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Parser based on Lagarto
  * @since 2.10
  */
 public class LagartoBasedHtmlParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
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
-
+        private HtmlCCommentExpressionMatcher htmlCCommentExpressionMatcher;
         private URLCollection urls;
         private URLPointer baseUrl;
+        private Float ieVersion;
+        private Stack<Boolean> enabled = new Stack<Boolean>();
 
         /**
          * @param baseUrl 
          * @param urls 
+         * @param ieVersion 
          */
-        public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls) {
+        public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls, Float ieVersion) {
             this.urls = urls;
             this.baseUrl = baseUrl;
+            this.ieVersion = ieVersion;
         }
 
         private final void extractAttribute(Tag tag, String attributeName) {
-            String url = tag.getAttributeValue(attributeName, false);
+            CharSequence url = tag.getAttributeValue(attributeName);
             if (!StringUtils.isEmpty(url)) {
-                urls.addURL(url, baseUrl.url);
+                urls.addURL(url.toString(), baseUrl.url);
             }
         }
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#script(jodd.lagarto.Tag,
          * java.lang.CharSequence)
          */
         @Override
         public void script(Tag tag, CharSequence body) {
+            if (!enabled.peek()) {
+                return;
+            }
             extractAttribute(tag, ATT_SRC);
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#tag(jodd.lagarto.Tag)
          */
         @Override
         public void tag(Tag tag) {
-
-            String tagName = tag.getName().toLowerCase();
-            if (tagName.equals(TAG_BODY)) {
-                extractAttribute(tag, ATT_BACKGROUND);
-            } else if (tagName.equals(TAG_BASE)) {
-                String baseref = tag.getAttributeValue(ATT_HREF, false);
-                try {
-                    if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
-                    {
-                        baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
+            if (!enabled.peek()) {
+                return;
+            }
+            TagType tagType = tag.getType();
+            switch (tagType) {
+            case START:
+            case SELF_CLOSING:
+                if (tag.nameEquals(TAG_BODY)) {
+                    extractAttribute(tag, ATT_BACKGROUND);
+                } else if (tag.nameEquals(TAG_BASE)) {
+                    CharSequence baseref = tag.getAttributeValue(ATT_HREF);
+                    try {
+                        if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
+                        {
+                            baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref.toString());
+                        }
+                    } catch (MalformedURLException e1) {
+                        throw new RuntimeException(e1);
                     }
-                } catch (MalformedURLException e1) {
-                    throw new RuntimeException(e1);
-                }
-            } else if (tagName.equals(TAG_IMAGE)) {
-                extractAttribute(tag, ATT_SRC);
-            } else if (tagName.equals(TAG_APPLET)) {
-                extractAttribute(tag, ATT_CODE);
-            } else if (tagName.equals(TAG_OBJECT)) {
-                extractAttribute(tag, ATT_CODEBASE);                
-                extractAttribute(tag, ATT_DATA);                 
-            } else if (tagName.equals(TAG_INPUT)) {
-                // we check the input tag type for image
-                if (ATT_IS_IMAGE.equalsIgnoreCase(tag.getAttributeValue(ATT_TYPE, false))) {
-                    // then we need to download the binary
+                } else if (tag.nameEquals(TAG_IMAGE)) {
+                    extractAttribute(tag, ATT_SRC);
+                } else if (tag.nameEquals(TAG_APPLET)) {
+                    extractAttribute(tag, ATT_CODE);
+                } else if (tag.nameEquals(TAG_OBJECT)) {
+                    extractAttribute(tag, ATT_CODEBASE);                
+                    extractAttribute(tag, ATT_DATA);                 
+                } else if (tag.nameEquals(TAG_INPUT)) {
+                    // we check the input tag type for image
+                    if (TagUtil.equalsIgnoreCase(ATT_IS_IMAGE, tag.getAttributeValue(ATT_TYPE))) {
+                        // then we need to download the binary
+                        extractAttribute(tag, ATT_SRC);
+                    }
+                } else if (tag.nameEquals(TAG_SCRIPT)) {
                     extractAttribute(tag, ATT_SRC);
+                    // Bug 51750
+                } else if (tag.nameEquals(TAG_FRAME) || tag.nameEquals(TAG_IFRAME)) {
+                    extractAttribute(tag, ATT_SRC);
+                } else if (tag.nameEquals(TAG_EMBED)) {
+                    extractAttribute(tag, ATT_SRC);
+                } else if (tag.nameEquals(TAG_BGSOUND)){
+                    extractAttribute(tag, ATT_SRC);
+                } else if (tag.nameEquals(TAG_LINK)) {
+                    CharSequence relAttribute = tag.getAttributeValue(ATT_REL);
+                    // Putting the string first means it works even if the attribute is null
+                    if (TagUtil.equalsIgnoreCase(STYLESHEET,relAttribute)) {
+                        extractAttribute(tag, ATT_HREF);
+                    }
+                } else {
+                    extractAttribute(tag, ATT_BACKGROUND);
                 }
-            } else if (tagName.equals(TAG_SCRIPT)) {
-                extractAttribute(tag, ATT_SRC);
-                // Bug 51750
-            } else if (tagName.equals(TAG_FRAME) || tagName.equals(TAG_IFRAME)) {
-                extractAttribute(tag, ATT_SRC);
-            } else if (tagName.equals(TAG_EMBED)) {
-                extractAttribute(tag, ATT_SRC);
-            } else if (tagName.equals(TAG_BGSOUND)){
-                extractAttribute(tag, ATT_SRC);
-            } else if (tagName.equals(TAG_LINK)) {
-                // Putting the string first means it works even if the attribute is null
-                if (STYLESHEET.equalsIgnoreCase(tag.getAttributeValue(ATT_REL, false))) {
-                    extractAttribute(tag, ATT_HREF);
+    
+    
+                // Now look for URLs in the STYLE attribute
+                CharSequence styleTagStr = tag.getAttributeValue(ATT_STYLE);
+                if(!StringUtils.isEmpty(styleTagStr)) {
+                    HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr.toString());
                 }
-            } else {
-                extractAttribute(tag, ATT_BACKGROUND);
+                break;
+            case END:
+                break;
             }
+        }
 
-
-            // Now look for URLs in the STYLE attribute
-            String styleTagStr = tag.getAttributeValue(ATT_STYLE, false);
-            if(styleTagStr != null) {
-                HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
+        /* (non-Javadoc)
+         * @see jodd.lagarto.EmptyTagVisitor#condComment(java.lang.CharSequence, boolean, boolean, boolean)
+         */
+        @Override
+        public void condComment(CharSequence expression, boolean isStartingTag,
+                boolean isHidden, boolean isHiddenEndTag) {
+            // See http://css-tricks.com/how-to-create-an-ie-only-stylesheet/
+            if(!isStartingTag) {
+                enabled.pop();
+            } else {
+                if (htmlCCommentExpressionMatcher == null) {
+                    htmlCCommentExpressionMatcher = new HtmlCCommentExpressionMatcher();
+                }
+                String expressionString = expression.toString().trim();
+                enabled.push(Boolean.valueOf(htmlCCommentExpressionMatcher.match(ieVersion.floatValue(),
+                        expressionString)));                
             }
         }
+
+        /* (non-Javadoc)
+         * @see jodd.lagarto.EmptyTagVisitor#start()
+         */
+        @Override
+        public void start() {
+            super.start();
+            enabled.clear();
+            enabled.push(Boolean.TRUE);
+        }
     }
 
     @Override
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl,
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
+            Float ieVersion = extractIEVersion(userAgent);
+            
             String contents = new String(html,encoding); 
-            LagartoParser lagartoParser = new LagartoParser(contents);
-            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll);
+            // As per Jodd javadocs, emitStrings should be false for visitor for better performances
+            LagartoParser lagartoParser = new LagartoParser(contents, false);
+            LagartoParserConfig<LagartoParserConfig> config = new LagartoParserConfig<LagartoParserConfig>();
+            config.setCaseSensitive(false);
+            // Conditional comments only apply for IE < 10
+            config.setEnableConditionalComments(isEnableConditionalComments(ieVersion));
+            
+            lagartoParser.setConfig(config);
+            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll, ieVersion);
             lagartoParser.parse(tagVisitor);
             return coll.iterator();
         } catch (LagartoException e) {
             // TODO is it the best way ? https://issues.apache.org/bugzilla/show_bug.cgi?id=55634
             if(log.isDebugEnabled()) {
                 log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
             }
             return Collections.<URL>emptyList().iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 
+    
+
+
+
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.parser.HTMLParser#isReusable()
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
index db4a02521..49f16cfdd 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
@@ -1,206 +1,206 @@
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
 //NOTE: Also looked at using Java 1.4 regexp instead of ORO. The change was
 //trivial. Performance did not improve -- at least not significantly.
 //Finally decided for ORO following advise from Stefan Bodewig (message
 //to jmeter-dev dated 25 Nov 2003 8:52 CET) [Jordi]
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
             + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE|DATA)" + VALUE
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
-    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
+    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
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
diff --git a/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java b/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
index d367794a0..de20d9e30 100644
--- a/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
+++ b/test/src/org/apache/jmeter/protocol/http/parser/TestHTMLParser.java
@@ -1,328 +1,444 @@
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
 
+    private static final String DEFAULT_UA  = "Apache-HttpClient/4.2.6";
+    private static final String UA_FF       = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
+    private static final String UA_IE55     = "Mozilla/4.0 (compatible;MSIE 5.5; Windows 98)";
+    private static final String UA_IE6      = "Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)";
+    private static final String UA_IE7      = "Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)";
+    private static final String UA_IE8      = "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB7.4; InfoPath.2; SV1; .NET CLR 3.3.69573; WOW64; en-US)";
+    private static final String UA_IE9      = "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))";
+    private static final String UA_IE10     = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)";
+
     public TestHTMLParser(String arg0) {
         super(arg0);
     }
         private String parserName;
 
         private int testNumber = 0;
 
+
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
 
+            public String userAgent;
+
             /**
              * 
              * @param htmlFileName HTML File with content
              * @param baseUrl Base URL
              * @param expectedSet Set of expected URLs
              * @param expectedList List of expected URLs
+             * @param userAgent User Agent
              */
             private TestData(String htmlFileName, String baseUrl, String expectedSet, String expectedList) {
+                this(htmlFileName, baseUrl, expectedList, expectedList, DEFAULT_UA);
+            }
+            /**
+             * 
+             * @param htmlFileName HTML File with content
+             * @param baseUrl Base URL
+             * @param expectedSet Set of expected URLs
+             * @param expectedList List of expected URLs
+             * @param userAgent User Agent
+             */
+            private TestData(String htmlFileName, String baseUrl, String expectedSet, String expectedList, String userAgent) {
                 this.fileName = htmlFileName;
                 this.baseURL = baseUrl;
                 this.expectedSet = expectedSet;
                 this.expectedList = expectedList;
+                this.userAgent = userAgent;
             }
 
 //            private TestData(String f, String b, String s) {
 //                this(f, b, s, null);
 //            }
         }
-
+        
+        private static final String DEFAULT_JMETER_PARSER = 
+                "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser";
+        
         // List of parsers to test. Should probably be derived automatically
         private static final String[] PARSERS = { 
             "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser",
             "org.apache.jmeter.protocol.http.parser.JTidyHTMLParser",
             "org.apache.jmeter.protocol.http.parser.RegexpHTMLParser",
-            "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser",
+            DEFAULT_JMETER_PARSER,
             "org.apache.jmeter.protocol.http.parser.JsoupBasedHtmlParser"
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
 
+
+        private static final TestData[] SPECIFIC_PARSER_TESTS = new TestData[] {
+            new TestData("testfiles/HTMLParserTestCaseWithConditional1.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional1_FF.all",
+                    UA_FF),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional1.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional1_IE6.all",
+                    UA_IE6),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional1.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional1_IE7.all",
+                    UA_IE7),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional1.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional1_IE8.all",
+                    UA_IE8),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional1.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional1_IE8.all",
+                    UA_IE8),
+
+            // FF gets mixed up by nested comments
+            new TestData("testfiles/HTMLParserTestCaseWithConditional2.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional2_FF.all",
+                    UA_FF),
+
+            new TestData("testfiles/HTMLParserTestCaseWithConditional2.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional2_IE7.all",
+                    UA_IE7),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional2.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional2_IE8.all",
+                    UA_IE8),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional2.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional2_IE9.all",
+                    UA_IE9),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional3.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional3_FF.all",
+                    UA_FF),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional3.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional3_IE10.all",
+                    UA_IE10),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional3.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional3_IE55.all",
+                    UA_IE55),
+            new TestData("testfiles/HTMLParserTestCaseWithConditional3.html",
+                    "http://localhost/mydir/myfile.html",
+                    null,
+                    "testfiles/HTMLParserTestCaseWithConditional3_IE6.all",
+                    UA_IE6)        
+        };   
+
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
+            
+            TestSuite ps = new TestSuite(DEFAULT_JMETER_PARSER+"_conditional_comments");// Identify subtests
+            for (int j = 0; j < SPECIFIC_PARSER_TESTS.length; j++) {
+                TestSuite ts = new TestSuite(SPECIFIC_PARSER_TESTS[j].fileName);
+                ts.addTest(new TestHTMLParser("testSpecificParserList", DEFAULT_JMETER_PARSER, j));
+                ps.addTest(ts);
+            }
+            suite.addTest(ps);
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
-                    false);
+                    false, TESTS[testNumber].userAgent);
         }
 
         public void testParserList() throws Exception {
             HTMLParser p = HTMLParser.getParser(parserName);
             filetest(p, TESTS[testNumber].fileName, TESTS[testNumber].baseURL, TESTS[testNumber].expectedList,
-                    new Vector<URLString>(), true);
+                    new Vector<URLString>(), true, TESTS[testNumber].userAgent);
+        }
+        
+        public void testSpecificParserList() throws Exception {
+            HTMLParser p = HTMLParser.getParser(parserName);
+            filetest(p, SPECIFIC_PARSER_TESTS[testNumber].fileName, SPECIFIC_PARSER_TESTS[testNumber].baseURL, SPECIFIC_PARSER_TESTS[testNumber].expectedList,
+                    new ArrayList<URLString>(), true, SPECIFIC_PARSER_TESTS[testNumber].userAgent);
         }
 
+
         private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection<URLString> c,
-                boolean orderMatters) // Does the order matter?
+                boolean orderMatters, // Does the order matter?
+                String userAgent)
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
-                result = p.getEmbeddedResourceURLs(buffer, new URL(url), System.getProperty("file.encoding"));
+                result = p.getEmbeddedResourceURLs(userAgent, buffer, new URL(url), System.getProperty("file.encoding"));
             } else {
-                result = p.getEmbeddedResourceURLs(buffer, new URL(url), c,System.getProperty("file.encoding"));
+                result = p.getEmbeddedResourceURLs(userAgent, buffer, new URL(url), c,System.getProperty("file.encoding"));
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
-                assertTrue(fname+"::"+parserName + "::Expecting another result " + next, result.hasNext());
+                assertTrue(userAgent+"::"+fname+"::"+parserName + "::Expecting another result " + next, result.hasNext());
                 try {
-                    assertEquals(fname+"::"+parserName + "(next)", next, result.next().toString());
+                    assertEquals(userAgent+"::"+fname+"::"+parserName + "(next)", next, result.next().toString());
                 } catch (ClassCastException e) {
-                    fail(fname+"::"+parserName + "::Expected URL, but got " + e.toString());
+                    fail(userAgent+"::"+fname+"::"+parserName + "::Expected URL, but got " + e.toString());
                 }
             }
-            assertFalse(fname+"::"+parserName + "::Should have reached the end of the results", result.hasNext());
+            assertFalse(userAgent+"::"+fname+"::"+parserName + "::Should have reached the end of the results", result.hasNext());
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
index a0c0895a9..bce8fe110 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,294 +1,295 @@
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
 
 
 <!--  =================== 2.12 =================== -->
 
 <h1>Version 2.12</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
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
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 Cut and Paste don't work on MacOSX with all LookAndFeel types.
 The default value for MacOSX is System, which should work OK.
 The current setting is logged at start-up, for example:
 <pre>
 jmeter.gui.action.LookAndFeelCommand: Using look and feel: com.apple.laf.AquaLookAndFeel [Mac OS X, System] 
 </pre>
 </li>
 
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 <li>Since JMeter 2.12, Mail Reader Sampler will show 1 for number of samples instead of number of messages retrieved, see <bugzilla>56539</bugzilla></li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55998</bugzilla> - HTTP recording – Replacing port value by user defined variable does not work</li>
 <li><bugzilla>56178</bugzilla> - keytool error: Invalid escaped character in AVA: - some characters must be escaped</li>
 <li><bugzilla>56222</bugzilla> - NPE if jmeter.httpclient.strict_rfc2616=true and location is not absolute</li>
 <li><bugzilla>56263</bugzilla> - DefaultSamplerCreator should set BrowserCompatible Multipart true</li>
 <li><bugzilla>56231</bugzilla> - Move redirect location processing from HC3/HC4 samplers to HTTPSamplerBase#followRedirects()</li>
 <li><bugzilla>56207</bugzilla> - URLs get encoded on redirects in HC3.1 &amp; HC4 samplers</li>
 <li><bugzilla>56303</bugzilla> - The width of target controller's combo list should be set to the current panel size, not on label size of the controllers</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>55977</bugzilla> - JDBC pool keepalive flooding</li>
 <li><bugzilla>55999</bugzilla> - Scroll bar on jms point-to-point sampler does not work when content exceeds display</li>
 <li><bugzilla>56198</bugzilla> - JMSSampler : NullPointerException is thrown when JNDI underlying implementation of JMS provider does not comply with Context.getEnvironment contract</li>
 <li><bugzilla>56428</bugzilla> - MailReaderSampler - should it use mail.pop3s.* properties?</li>
 <li><bugzilla>46932</bugzilla> - Alias given in select statement is not used as column header in response data for a JDBC request.Based on report and analysis of Nicola Ambrosetti</li>
 <li><bugzilla>56539</bugzilla> - Mail reader sampler: When Number of messages to retrieve is superior to 1, Number of samples should only show 1 not the number of messages retrieved</li>
 <li><bugzilla>56809</bugzilla> - JMSSampler closes InitialContext too early. Contributed by Bradford Hovinen (hovinen at gmail.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56243</bugzilla> - Foreach works incorrectly with indexes on subsequent iterations </li>
 <li><bugzilla>56276</bugzilla> - Loop controller becomes broken once loop count evaluates to zero </li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56706</bugzilla> - SampleResult#getResponseDataAsString() does not use encoding in response body impacting PostProcessors and ViewResultsTree. Contributed by Ubik Load Pack(support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56162</bugzilla> -  HTTP Cache Manager should not cache PUT/POST etc.</li>
 <li><bugzilla>56227</bugzilla> - AssertionGUI : NPE in assertion on mouse selection</li>
 <li><bugzilla>41319</bugzilla> - URLRewritingModifier : Allow Parameter value to be url encoded</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>56111</bugzilla> - "comments" in german translation is not correct</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>56059</bugzilla> - Older TestBeans incompatible with 2.11 when using TextAreaEditor</li>
 <li><bugzilla>56080</bugzilla> - Conversion error com.thoughtworks.xstream.converters.ConversionException with Java 8 Early Access Build</li>
 <li><bugzilla>56182</bugzilla> - Can't trigger bsh script using bshclient.jar; socket is closed unexpectedly </li>
 <li><bugzilla>56360</bugzilla> - HashTree and ListedHashTree fail to compile with Java 8</li>
 <li><bugzilla>56419</bugzilla> - Jmeter silently fails to save results</li>
 <li><bugzilla>56662</bugzilla> - Save as xml in a listener is not remembered</li>
 <li><bugzilla>56367</bugzilla> - JMeter 2.11 on maven central triggers a not existing dependency rsyntaxtextarea 2.5.1, upgrade to 2.5.3</li>
 <li><bugzilla>56743</bugzilla> - Wrong mailing list archives on mail2.xml. Contributed by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56763</bugzilla> - Removing the Oracle icons, not used by JMeter (and missing license)</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bugzilla>55959</bugzilla> - Improve error message when Test Script Recorder fails due to I/O problem</li>
 <li><bugzilla>52013</bugzilla> - Test Script Recorder's Child View Results Tree does not take into account Test Script Recorder excluded/included URLs. Based on report and analysis of James Liang</li>
 <li><bugzilla>56119</bugzilla> - File uploads fail every other attempt using timers. Enable idle timeouts for servers that don't send Keep-Alive headers.</li>
 <li><bugzilla>56272</bugzilla> - MirrorServer should support query parameters for status and redirects</li>
+<li><bugzilla>56772</bugzilla> - Handle IE Conditional comments when parsing embedded resources</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>56033</bugzilla> - Add Connection timeout and Read timeout to SMTP Sampler</li>
 <li><bugzilla>56429</bugzilla> - MailReaderSampler - no need to fetch all Messages if not all wanted</li>
 <li><bugzilla>56427</bugzilla> - MailReaderSampler enhancement: read message header only</li>
 <li><bugzilla>56510</bugzilla> - JMS Publisher/Point to Point: Add JMSPriority and JMSExpiration</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>56728</bugzilla> - New Critical Section Controller to serialize blocks of a Test. Based partly on a patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>56228</bugzilla> - View Results Tree : Improve ergonomy by changing placement of Renderers and allowing custom ordering</li>
 <li><bugzilla>56349</bugzilla> - "summary" is a bad name for a Generate Summary Results component, documentation clarified</li>
 <li><bugzilla>56769</bugzilla> - Adds the ability for the Response Time Graph listener to save/restore format settings in/from the jmx file</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>56691</bugzilla> - Synchronizing Timer : Add timeout on waiting</li>
 <li><bugzilla>56701</bugzilla> - HTTP Authorization Manager/ Kerberos Authentication: add port to SPN when server port is neither 80 nor 443. Based on patches from Dan Haughey (dan.haughey at swinton.co.uk) and Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>56841</bugzilla> - New configuration element: DNS Cache Manager to improve the testing of CDN. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bugzilla>52061</bugzilla> - Allow access to Request Headers in Regex Extractor. Based on patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>56708</bugzilla> - __jexl2 doesn't scale with multiple CPU cores. Based on analysis and patch contributed by Mikhail Epikhin(epihin-m at yandex.ru)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>21695</bugzilla> - Unix jmeter start script assumes it is on PATH, not a link</li>
 <li><bugzilla>56292</bugzilla> - Add the check of the Java's version in startup files and disable some options when is Java v8 engine</li>
 <li><bugzilla>56298</bugzilla> - JSR223 language display does not show which engine will be used</li>
 <li><bugzilla>56455</bugzilla> - Batch files: drop support for non-NT Windows shell scripts</li>
 <li><bugzilla>56807</bugzilla> - Ability to force flush of ResultCollector file. Contributed by Andrey Pohilko (apc4 at ya.ru)</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to commons-lang3 3.3.2 (from 3.1)</li>
 <li>Updated to commons-codec 1.9 (from 1.8)</li>
 <li>Updated to commons-logging 1.2 (from 1.1.3)</li>
 <li>Updated to tika 1.5 (from 1.4)</li>
 <li>Updated to xercesImpl 2.11.0 (from 2.9.1)</li>
 <li>Updated to xml-apis 1.4.01 (from 1.3.04)</li>
 <li>Updated to xstream 1.4.7 (from 1.4.4)</li>
 <li>Updated to jodd 3.5.2 (from 3.4.10)</li>
 <li>Updated to rsyntaxtextarea 2.5.3 (from 2.5.1)</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li>James Liang (jliang at andera.com)</li>
 <li>Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Nicola Ambrosetti (ambrosetti.nicola at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack support</a></li>
 <li>Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li>Dan Haughey (dan.haughey at swinton.co.uk)</li>
 <li>Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li>Andrey Pohilko (apc4 at ya.ru)</li>
 <li>Bradford Hovinen (hovinen at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Oliver LLoyd (email at oliverlloyd.com) for his help on <bugzilla>56119</bugzilla></li>
 <li>Vladimir Ryabtsev (greatvovan at gmail.com) for his help on <bugzilla>56243</bugzilla> and <bugzilla>56276</bugzilla></li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
