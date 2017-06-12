diff --git a/test/src/org/apache/jmeter/timers/PackageTest.java b/test/src/org/apache/jmeter/timers/PackageTest.java
index 00468e814..1afaa5816 100644
--- a/test/src/org/apache/jmeter/timers/PackageTest.java
+++ b/test/src/org/apache/jmeter/timers/PackageTest.java
@@ -1,97 +1,101 @@
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
 
 package org.apache.jmeter.timers;
 
+import static org.junit.Assert.assertEquals;
+
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.TestJMeterContextService;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
+import org.junit.Test;
 
 public class PackageTest extends JMeterTestCase {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
-    public PackageTest(String arg0) {
-        super(arg0);
-    }
 
+    @Test
     public void testTimer1() throws Exception {
         ConstantThroughputTimer timer = new ConstantThroughputTimer();
         assertEquals(0,timer.getCalcMode());// Assume this thread only
         timer.setThroughput(60.0);// 1 per second
         long delay = timer.delay(); // Initialise
         assertEquals(0,delay);
         Thread.sleep(500);
         assertEquals("Expected delay of approx 500",500, timer.delay(), 50);
     }
 
+    @Test
     public void testTimer2() throws Exception {
         ConstantThroughputTimer timer = new ConstantThroughputTimer();
         assertEquals(0,timer.getCalcMode());// Assume this thread only
         timer.setThroughput(60.0);// 1 per second
         assertEquals(1000,timer.calculateCurrentTarget(0)); // Should delay for 1 second
         timer.setThroughput(60000.0);// 1 per milli-second
         assertEquals(1,timer.calculateCurrentTarget(0)); // Should delay for 1 milli-second
     }
 
+    @Test
     public void testTimer3() throws Exception {
         ConstantThroughputTimer timer = new ConstantThroughputTimer();
         timer.setMode(ConstantThroughputTimer.Mode.AllActiveThreads); //$NON-NLS-1$ - all threads
         assertEquals(1,timer.getCalcMode());// All threads
         for(int i=1; i<=10; i++){
             TestJMeterContextService.incrNumberOfThreads();
         }
         assertEquals(10,JMeterContextService.getNumberOfThreads());
         timer.setThroughput(600.0);// 10 per second
         assertEquals(1000,timer.calculateCurrentTarget(0)); // Should delay for 1 second
         timer.setThroughput(600000.0);// 10 per milli-second
         assertEquals(1,timer.calculateCurrentTarget(0)); // Should delay for 1 milli-second
         for(int i=1; i<=990; i++){
             TestJMeterContextService.incrNumberOfThreads();
         }
         assertEquals(1000,JMeterContextService.getNumberOfThreads());
         timer.setThroughput(60000000.0);// 1000 per milli-second
         assertEquals(1,timer.calculateCurrentTarget(0)); // Should delay for 1 milli-second
     }
 
+    @Test
     public void testTimerBSH() throws Exception {
         if (!BeanShellInterpreter.isInterpreterPresent()){
             final String msg = "BeanShell jar not present, test ignored";
             log.warn(msg);
             return;
         }
         BeanShellTimer timer = new BeanShellTimer();
         long delay;
         
         timer.setScript("\"60\"");
         delay = timer.delay();
         assertEquals(60,delay);
         
         timer.setScript("60");
         delay = timer.delay();
         assertEquals(60,delay);
         
         timer.setScript("5*3*4");
         delay = timer.delay();
         assertEquals(60,delay);
     }
 
 }
diff --git a/test/src/org/apache/jmeter/util/PackageTest.java b/test/src/org/apache/jmeter/util/PackageTest.java
index dab3c18b9..035480046 100644
--- a/test/src/org/apache/jmeter/util/PackageTest.java
+++ b/test/src/org/apache/jmeter/util/PackageTest.java
@@ -1,66 +1,68 @@
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
 
 package org.apache.jmeter.util;
 
-import junit.framework.TestCase;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
 
-public class PackageTest extends TestCase {
+import org.junit.Test;
 
-    public PackageTest() {
-        super();
-    }
+public class PackageTest {
 
-    public PackageTest(String arg0) {
-        super(arg0);
-    }
 
+    @Test
     public void testServer() throws Exception {
         BeanShellServer bshs = new BeanShellServer(9876, "");
         assertNotNull(bshs);
         // Not sure we can test anything else here
     }
+    
+    @Test
     public void testSub1() throws Exception {
         String input = "http://jakarta.apache.org/jmeter/index.html";
         String pattern = "jakarta.apache.org";
         String sub = "${server}";
         assertEquals("http://${server}/jmeter/index.html", StringUtilities.substitute(input, pattern, sub));
     }
 
+    @Test
     public void testSub2() throws Exception {
         String input = "arg1=param1;param1";
         String pattern = "param1";
         String sub = "${value}";
         assertEquals("arg1=${value};${value}", StringUtilities.substitute(input, pattern, sub));
     }
 
+    @Test
     public void testSub3() throws Exception {
         String input = "jakarta.apache.org";
         String pattern = "jakarta.apache.org";
         String sub = "${server}";
         assertEquals("${server}", StringUtilities.substitute(input, pattern, sub));
     }
 
+    @Test
     public void testSub4() throws Exception {
         String input = "//a///b////c";
         String pattern = "//";
         String sub = "/";
         assertEquals("/a//b//c", StringUtilities.substitute(input, pattern, sub));
     }
 
 }
diff --git a/test/src/org/apache/jmeter/util/TestJMeterUtils.java b/test/src/org/apache/jmeter/util/TestJMeterUtils.java
index 3e0f91000..3b5d9fc42 100644
--- a/test/src/org/apache/jmeter/util/TestJMeterUtils.java
+++ b/test/src/org/apache/jmeter/util/TestJMeterUtils.java
@@ -1,45 +1,41 @@
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
 
 /**
  * Package to test JMeterUtils methods 
  */
      
 package org.apache.jmeter.util;
 
-import junit.framework.TestCase;
+import static org.junit.Assert.assertEquals;
 
-public class TestJMeterUtils extends TestCase {
+import org.junit.Test;
 
-    public TestJMeterUtils() {
-        super();
-    }
-
-    public TestJMeterUtils(String arg0) {
-        super(arg0);
-    }
+public class TestJMeterUtils {
 
+    @Test
     public void testGetResourceFileAsText() throws Exception{
         String sep = System.getProperty("line.separator");
         assertEquals("line one" + sep + "line two" + sep, JMeterUtils.getResourceFileAsText("resourcefile.txt"));
     }
     
+    @Test
     public void testGetResourceFileAsTextWithMisingResource() throws Exception{
         assertEquals("", JMeterUtils.getResourceFileAsText("not_existant_resourcefile.txt"));
     }
 }
diff --git a/test/src/org/apache/jmeter/visualizers/TestRenderAsJson.java b/test/src/org/apache/jmeter/visualizers/TestRenderAsJson.java
index 4a9a078cd..4b7f81562 100644
--- a/test/src/org/apache/jmeter/visualizers/TestRenderAsJson.java
+++ b/test/src/org/apache/jmeter/visualizers/TestRenderAsJson.java
@@ -1,68 +1,67 @@
 package org.apache.jmeter.visualizers;
 
-import java.lang.reflect.Method;
-
-import junit.framework.TestCase;
+import static org.junit.Assert.assertEquals;
 
+import java.lang.reflect.Method;
+import org.junit.Before;
 import org.junit.Test;
 
-public class TestRenderAsJson extends TestCase {
+public class TestRenderAsJson {
 
     private Method prettyJSON;
     private final String TAB = ":   ";
 
     private String prettyJSON(String prettify) throws Exception {
         return (String) prettyJSON.invoke(null, prettify);
     }
 
-    @Override
-    protected void setUp() throws Exception {
-        super.setUp();
+    @Before
+    public void setUp() throws Exception {
         prettyJSON = RenderAsJSON.class.getDeclaredMethod("prettyJSON",
                 String.class);
         prettyJSON.setAccessible(true);
     }
 
     @Test
     public void testRenderResultWithLongStringBug54826() throws Exception {
         StringBuilder json = new StringBuilder();
         json.append("\"customData\":\"");
         for (int i = 0; i < 100; i++) {
             json.append("somenotsorandomtext");
         }
         json.append("\"");
 
         assertEquals("{\n" + TAB + json.toString() + "\n}",
                 prettyJSON("{" + json.toString() + "}"));
     }
 
     @Test
     public void testRenderResultSimpleObject() throws Exception {
         assertEquals("{\n}", prettyJSON("{}"));
     }
 
     @Test
     public void testRenderResultSimpleArray() throws Exception {
         assertEquals("[\n]", prettyJSON("[]"));
     }
 
     @Test
     public void testRenderResultSimpleNumber() throws Exception {
         assertEquals("42", prettyJSON("42"));
     }
 
     @Test
     public void testRenderResultSimpleString() throws Exception {
         assertEquals("Hello World", prettyJSON("Hello World"));
     }
 
     @Test
     public void testRenderResultSimpleStructure() throws Exception {
         assertEquals(
                 "{\n" + TAB + "\"Hello\": \"World\", \n" + TAB + "\"more\": \n"
                         + TAB + "[\n" + TAB + TAB + "\"Something\", \n" + TAB
                         + TAB + "\"else\", \n" + TAB + "]\n}",
                 prettyJSON("{\"Hello\": \"World\", \"more\": [\"Something\", \"else\", ]}"));
     }
 
 }
diff --git a/test/src/org/apache/jorphan/TestFunctorUsers.java b/test/src/org/apache/jorphan/TestFunctorUsers.java
index f81c9d13e..ba441f8e3 100644
--- a/test/src/org/apache/jorphan/TestFunctorUsers.java
+++ b/test/src/org/apache/jorphan/TestFunctorUsers.java
@@ -1,69 +1,75 @@
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
 
 package org.apache.jorphan;
 
+import static org.junit.Assert.assertTrue;
+
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.ldap.config.gui.LDAPArgumentsPanel;
 import org.apache.jmeter.visualizers.StatGraphVisualizer;
 import org.apache.jmeter.visualizers.StatVisualizer;
 import org.apache.jmeter.visualizers.SummaryReport;
 import org.apache.jmeter.visualizers.TableVisualizer;
+import org.junit.Test;
 
 /*
  * Unit tests for classes that use Functors
  * 
  */
 public class TestFunctorUsers extends JMeterTestCase {
 
-    public TestFunctorUsers(String arg0) {
-        super(arg0);
-    }
-    
+    @Test
     @SuppressWarnings("deprecation")
     public void testSummaryReport() throws Exception{
         assertTrue("SummaryReport Functor",SummaryReport.testFunctors());
     }
     
+    @Test
     public void testTableVisualizer() throws Exception{
         assertTrue("TableVisualizer Functor",TableVisualizer.testFunctors());
     }
     
+    @Test
     public void testStatGraphVisualizer() throws Exception{
         assertTrue("StatGraphVisualizer Functor",StatGraphVisualizer.testFunctors());
     }
     
+    @Test
     @SuppressWarnings("deprecation")
     public void testStatVisualizer() throws Exception{
         assertTrue("StatVisualizer Functor",StatVisualizer.testFunctors());
     }
     
+    @Test
     public void testArgumentsPanel() throws Exception{
         assertTrue("ArgumentsPanel Functor",ArgumentsPanel.testFunctors());
     }
     
+    @Test
     public void testHTTPArgumentsPanel() throws Exception{
         assertTrue("HTTPArgumentsPanel Functor",HTTPArgumentsPanel.testFunctors());
     }
     
+    @Test
     public void testLDAPArgumentsPanel() throws Exception{
         assertTrue("LDAPArgumentsPanel Functor",LDAPArgumentsPanel.testFunctors());
     }
 }
diff --git a/test/src/org/apache/jorphan/TestXMLBuffer.java b/test/src/org/apache/jorphan/TestXMLBuffer.java
index 7bb93ea02..48f3468ff 100644
--- a/test/src/org/apache/jorphan/TestXMLBuffer.java
+++ b/test/src/org/apache/jorphan/TestXMLBuffer.java
@@ -1,56 +1,60 @@
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
 
 package org.apache.jorphan;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jorphan.util.XMLBuffer;
+import org.junit.Test;
 
 public class TestXMLBuffer extends JMeterTestCase {
 
-    public TestXMLBuffer(String arg0) {
-        super(arg0);
-    }
-    
+    @Test
     public void test1() throws Exception{
         XMLBuffer xb = new XMLBuffer();
         xb.openTag("start");
         assertEquals("<start></start>\n",xb.toString());
     }
     
+    @Test
     public void test2() throws Exception{
         XMLBuffer xb = new XMLBuffer();
         xb.tag("start","now");
         assertEquals("<start>now</start>\n",xb.toString());
     }
+    @Test
     public void test3() throws Exception{
         XMLBuffer xb = new XMLBuffer();
         xb.openTag("abc");
         xb.closeTag("abc");
         assertEquals("<abc></abc>\n",xb.toString());
     }
+    @Test
     public void test4() throws Exception{
         XMLBuffer xb = new XMLBuffer();
         xb.openTag("abc");
         try {
             xb.closeTag("abcd");
             fail("Should have caused IllegalArgumentException");
         } catch (IllegalArgumentException e) {
         }
     }
 }
diff --git a/test/src/org/apache/jorphan/collections/PackageTest.java b/test/src/org/apache/jorphan/collections/PackageTest.java
index 1808b7bf2..890cc8ea2 100644
--- a/test/src/org/apache/jorphan/collections/PackageTest.java
+++ b/test/src/org/apache/jorphan/collections/PackageTest.java
@@ -1,175 +1,181 @@
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
 
 package org.apache.jorphan.collections;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertNotSame;
+import static org.junit.Assert.assertTrue;
+
 import java.util.Arrays;
 import java.util.Collection;
-
-import junit.framework.TestCase;
-
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
+import org.junit.Test;
 
-public class PackageTest extends TestCase {
-        public PackageTest(String name) {
-            super(name);
-        }
-
+public class PackageTest {
+        
+        @Test
         public void testAdd1() throws Exception {
             Logger log = LoggingManager.getLoggerForClass();
             Collection<String> treePath = Arrays.asList(new String[] { "1", "2", "3", "4" });
             HashTree tree = new HashTree();
             log.debug("treePath = " + treePath);
             tree.add(treePath, "value");
             log.debug("Now treePath = " + treePath);
             log.debug(tree.toString());
             assertEquals(1, tree.list(treePath).size());
             assertEquals("value", tree.getArray(treePath)[0]);
         }
 
+        @Test
         public void testEqualsAndHashCode1() throws Exception {
             HashTree tree1 = new HashTree("abcd");
             HashTree tree2 = new HashTree("abcd");
             HashTree tree3 = new HashTree("abcde");
             HashTree tree4 = new HashTree("abcde");
 
             assertTrue(tree1.equals(tree1));
             assertTrue(tree1.equals(tree2));
             assertTrue(tree2.equals(tree1));
             assertTrue(tree2.equals(tree2));
             assertEquals(tree1.hashCode(), tree2.hashCode());
 
             assertTrue(tree3.equals(tree3));
             assertTrue(tree3.equals(tree4));
             assertTrue(tree4.equals(tree3));
             assertTrue(tree4.equals(tree4));
             assertEquals(tree3.hashCode(), tree4.hashCode());
 
             assertNotSame(tree1, tree2);
             assertNotSame(tree1, tree3);
             assertNotSame(tree1, tree4);
             assertNotSame(tree2, tree3);
             assertNotSame(tree2, tree4);
 
             assertFalse(tree1.equals(tree3));
             assertFalse(tree1.equals(tree4));
             assertFalse(tree2.equals(tree3));
             assertFalse(tree2.equals(tree4));
 
             assertNotNull(tree1);
             assertNotNull(tree2);
 
             tree1.add("abcd", tree3);
             assertFalse(tree1.equals(tree2));
             assertFalse(tree2.equals(tree1));// Check reflexive
             if (tree1.hashCode() == tree2.hashCode()) {
                 // This is not a requirement
                 System.out.println("WARN: unequal HashTrees should not have equal hashCodes");
             }
             tree2.add("abcd", tree4);
             assertTrue(tree1.equals(tree2));
             assertTrue(tree2.equals(tree1));
             assertEquals(tree1.hashCode(), tree2.hashCode());
         }
 
 
+        @Test
         public void testAddObjectAndTree() throws Exception {
             ListedHashTree tree = new ListedHashTree("key");
             ListedHashTree newTree = new ListedHashTree("value");
             tree.add("key", newTree);
             assertEquals(tree.list().size(), 1);
             assertEquals("key", tree.getArray()[0]);
             assertEquals(1, tree.getTree("key").list().size());
             assertEquals(0, tree.getTree("key").getTree("value").size());
             assertEquals(tree.getTree("key").getArray()[0], "value");
             assertNotNull(tree.getTree("key").get("value"));
         }
 
+        @Test
         public void testEqualsAndHashCode2() throws Exception {
             ListedHashTree tree1 = new ListedHashTree("abcd");
             ListedHashTree tree2 = new ListedHashTree("abcd");
             ListedHashTree tree3 = new ListedHashTree("abcde");
             ListedHashTree tree4 = new ListedHashTree("abcde");
 
             assertTrue(tree1.equals(tree1));
             assertTrue(tree1.equals(tree2));
             assertTrue(tree2.equals(tree1));
             assertTrue(tree2.equals(tree2));
             assertEquals(tree1.hashCode(), tree2.hashCode());
 
             assertTrue(tree3.equals(tree3));
             assertTrue(tree3.equals(tree4));
             assertTrue(tree4.equals(tree3));
             assertTrue(tree4.equals(tree4));
             assertEquals(tree3.hashCode(), tree4.hashCode());
 
             assertNotSame(tree1, tree2);
             assertNotSame(tree1, tree3);
             assertFalse(tree1.equals(tree3));
             assertFalse(tree3.equals(tree1));
             assertFalse(tree1.equals(tree4));
             assertFalse(tree4.equals(tree1));
 
             assertFalse(tree2.equals(tree3));
             assertFalse(tree3.equals(tree2));
             assertFalse(tree2.equals(tree4));
             assertFalse(tree4.equals(tree2));
 
             tree1.add("abcd", tree3);
             assertFalse(tree1.equals(tree2));
             assertFalse(tree2.equals(tree1));
 
             tree2.add("abcd", tree4);
             assertTrue(tree1.equals(tree2));
             assertTrue(tree2.equals(tree1));
             assertEquals(tree1.hashCode(), tree2.hashCode());
 
             tree1.add("a1");
             tree1.add("a2");
             // tree1.add("a3");
             tree2.add("a2");
             tree2.add("a1");
 
             assertFalse(tree1.equals(tree2));
             assertFalse(tree2.equals(tree1));
             if (tree1.hashCode() == tree2.hashCode()) {
                 // This is not a requirement
                 System.out.println("WARN: unequal ListedHashTrees should not have equal hashcodes");
 
             }
 
             tree4.add("abcdef");
             assertFalse(tree3.equals(tree4));
             assertFalse(tree4.equals(tree3));
         }
 
 
+        @Test
         public void testSearch() throws Exception {
             ListedHashTree tree = new ListedHashTree();
             SearchByClass<Integer> searcher = new SearchByClass<>(Integer.class);
             String one = "one";
             String two = "two";
             Integer o = Integer.valueOf(1);
             tree.add(one, o);
             tree.getTree(one).add(o, two);
             tree.traverse(searcher);
             assertEquals(1, searcher.getSearchResults().size());
         }
 
 }
diff --git a/test/src/org/apache/jorphan/exec/TestKeyToolUtils.java b/test/src/org/apache/jorphan/exec/TestKeyToolUtils.java
index 6f2dc995b..eb18a4bf6 100644
--- a/test/src/org/apache/jorphan/exec/TestKeyToolUtils.java
+++ b/test/src/org/apache/jorphan/exec/TestKeyToolUtils.java
@@ -1,60 +1,55 @@
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
 
 /**
  * Package to test JOrphanUtils methods 
  */
      
 package org.apache.jorphan.exec;
 
+import static org.junit.Assert.fail;
+
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
+import org.junit.Test;
 
-import junit.framework.TestCase;
-
-public class TestKeyToolUtils extends TestCase {
+public class TestKeyToolUtils {
 
-    public TestKeyToolUtils() {
-        super();
-    }
-
-    public TestKeyToolUtils(String arg0) {
-        super(arg0);
-    }
 
     /*
      * Check the assumption that a missing executable will generate
      * either an IOException or status which is neither 0 nor 1 
      *
      */
+    @Test
     public void testCheckKeytool() throws Exception {
         SystemCommand sc = new SystemCommand(null, null);
         List<String> arguments = new ArrayList<>();
         arguments.add("xyzqwas"); // should not exist
         try {
             int status = sc.run(arguments);
             if (status == 0 || status ==1) {
                 fail("Unexpected status " + status);
             }
 //            System.out.println("testCheckKeytool:status="+status);
         } catch (IOException expected) {
 //            System.out.println("testCheckKeytool:Exception="+e);
         }
     }
 }
diff --git a/test/src/org/apache/jorphan/math/TestStatCalculator.java b/test/src/org/apache/jorphan/math/TestStatCalculator.java
index 81ebb323f..29a56d557 100644
--- a/test/src/org/apache/jorphan/math/TestStatCalculator.java
+++ b/test/src/org/apache/jorphan/math/TestStatCalculator.java
@@ -1,154 +1,155 @@
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
 
 package org.apache.jorphan.math;
 
-import java.util.Map;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
 
-import junit.framework.TestCase;
+import java.util.Map;
+import org.junit.Before;
+import org.junit.Test;
 
-public class TestStatCalculator extends TestCase {
+public class TestStatCalculator {
 
     private StatCalculatorLong calc;
 
-    /**
-     * 
-     */
-    public TestStatCalculator() {
-        super();
-    }
-
-    public TestStatCalculator(String arg0) {
-        super(arg0);
-    }
 
-    @Override
+    @Before
     public void setUp() {
         calc = new StatCalculatorLong();
     }
 
+    @Test
     public void testPercentagePoint() throws Exception {
         calc.addValue(10);
         calc.addValue(9);
         calc.addValue(5);
         calc.addValue(6);
         calc.addValue(1);
         calc.addValue(3);
         calc.addValue(8);
         calc.addValue(2);
         calc.addValue(7);
         calc.addValue(4);
         assertEquals(10, calc.getCount());
         assertEquals(9, calc.getPercentPoint(0.8999999).intValue());
     }
+    @Test
     public void testCalculation() {
         assertEquals(Long.MIN_VALUE, calc.getMax().longValue());
         assertEquals(Long.MAX_VALUE, calc.getMin().longValue());
         calc.addValue(18);
         calc.addValue(10);
         calc.addValue(9);
         calc.addValue(11);
         calc.addValue(28);
         calc.addValue(3);
         calc.addValue(30);
         calc.addValue(15);
         calc.addValue(15);
         calc.addValue(21);
         assertEquals(16, (int) calc.getMean());
         assertEquals(8.0622577F, (float) calc.getStandardDeviation(), 0F);
         assertEquals(30, calc.getMax().intValue());
         assertEquals(3, calc.getMin().intValue());
         assertEquals(15, calc.getMedian().intValue());
     }
+    @Test
     public void testLong(){
         calc.addValue(0L);
         calc.addValue(2L);
         calc.addValue(2L);
         final Long long0 = Long.valueOf(0);
         final Long long2 = Long.valueOf(2);
         assertEquals(long2,calc.getMax());
         assertEquals(long0,calc.getMin());
         Map<Number, Number[]> map = calc.getDistribution();
         assertTrue(map.containsKey(long0));
         assertTrue(map.containsKey(long2));
     }
     
+    @Test
     public void testInteger(){
         StatCalculatorInteger calci = new StatCalculatorInteger();
         assertEquals(Integer.MIN_VALUE, calci.getMax().intValue());
         assertEquals(Integer.MAX_VALUE, calci.getMin().intValue());
         calci.addValue(0);
         calci.addValue(2);
         calci.addValue(2);
         assertEquals(Integer.valueOf(2),calci.getMax());
         assertEquals(Integer.valueOf(0),calci.getMin());
         Map<Number, Number[]> map = calci.getDistribution();
         assertTrue(map.containsKey(Integer.valueOf(0)));
         assertTrue(map.containsKey(Integer.valueOf(2)));
     }
     
+    @Test
     @SuppressWarnings("boxing")
     public void testBug52125_1(){ // No duplicates when adding
         calc.addValue(1L);
         calc.addValue(2L);
         calc.addValue(3L);
         calc.addValue(2L);
         calc.addValue(2L);
         calc.addValue(2L);
         assertEquals(6, calc.getCount());
-        assertEquals(12.0, calc.getSum());
-        assertEquals(0.5773502691896255, calc.getStandardDeviation());
+        assertEquals(12.0, calc.getSum(), 0.000000000001);
+        assertEquals(0.5773502691896255, calc.getStandardDeviation(), 0.000000000000001);
     }
 
+    @Test
     @SuppressWarnings("boxing")
     public void testBug52125_2(){ // add duplicates
         calc.addValue(1L);
         calc.addValue(2L);
         calc.addValue(3L);
         calc.addEachValue(2L, 3);
         assertEquals(6, calc.getCount());
-        assertEquals(12.0, calc.getSum());
-        assertEquals(0.5773502691896255, calc.getStandardDeviation());
+        assertEquals(12.0, calc.getSum(), 0.000000000001);
+        assertEquals(0.5773502691896255, calc.getStandardDeviation(), 0.000000000000001);
     }
 
+    @Test
     @SuppressWarnings("boxing")
     public void testBug52125_2A(){ // as above, but with aggregate sample instead
         calc.addValue(1L);
         calc.addValue(2L);
         calc.addValue(3L);
         calc.addValue(6L, 3);
         assertEquals(6, calc.getCount());
-        assertEquals(12.0, calc.getSum());
-        assertEquals(0.5773502691896255, calc.getStandardDeviation());
+        assertEquals(12.0, calc.getSum(), 0.00000001);
+        assertEquals(0.5773502691896255, calc.getStandardDeviation(), 0.000000000000001);
     }
 
+    @Test
     @SuppressWarnings("boxing")
     public void testBug52125_3(){ // add duplicates as per bug
         calc.addValue(1L);
         calc.addValue(2L);
         calc.addValue(3L);
         StatCalculatorLong calc2 = new StatCalculatorLong();
         calc2.addValue(2L);
         calc2.addValue(2L);
         calc2.addValue(2L);
         calc.addAll(calc2);
         assertEquals(6, calc.getCount());
-        assertEquals(12.0, calc.getSum());
-        assertEquals(0.5773502691896255, calc.getStandardDeviation());
+        assertEquals(12.0, calc.getSum(), 0.000000000001);
+        assertEquals(0.5773502691896255, calc.getStandardDeviation(), 0.000000000000001);
     }
 }
diff --git a/test/src/org/apache/jorphan/reflect/TestClassTools.java b/test/src/org/apache/jorphan/reflect/TestClassTools.java
index 6f2e65421..be237d090 100644
--- a/test/src/org/apache/jorphan/reflect/TestClassTools.java
+++ b/test/src/org/apache/jorphan/reflect/TestClassTools.java
@@ -1,113 +1,114 @@
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
 package org.apache.jorphan.reflect;
 
-import junit.framework.TestCase;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
 
 import org.apache.jorphan.util.JMeterException;
 import org.junit.Test;
 
 /**
  * Test various aspects of the {@link ClassTools} class
  */
-public class TestClassTools extends TestCase {
+public class TestClassTools {
 
     /**
      * Test that a class can be constructed using the default constructor
      * 
      * @throws JMeterException
      *             when something fails during object construction
      */
     @Test
     public void testConstructString() throws JMeterException {
         String dummy = (String) ClassTools.construct("java.lang.String");
         assertNotNull(dummy);
         assertEquals("", dummy);
     }
 
     /**
      * Test that a class can be constructed using an constructor with an integer
      * parameter
      * 
      * @throws JMeterException
      *             when something fails during object construction
      */
     @Test
     public void testConstructStringInt() throws JMeterException {
         Integer dummy = (Integer) ClassTools.construct("java.lang.Integer", 23);
         assertNotNull(dummy);
         assertEquals(Integer.valueOf(23), dummy);
     }
 
     /**
      * Test that a class can be constructed using an constructor with an string
      * parameter
      * 
      * @throws JMeterException
      *             when something fails during object construction
      */
     @Test
     public void testConstructStringString() throws JMeterException {
         String dummy = (String) ClassTools.construct("java.lang.String",
                 "hello");
         assertNotNull(dummy);
         assertEquals("hello", dummy);
     }
 
     /**
      * Test that a simple method can be invoked on an object
      * 
      * @throws SecurityException
      *             when the method can not be used because of security concerns
      * @throws IllegalArgumentException
      *             when the method parameters does not match the given ones
      * @throws JMeterException
      *             when something fails while invoking the method
      */
     @Test
     public void testInvoke() throws SecurityException,
             IllegalArgumentException, JMeterException {
         Dummy dummy = new Dummy();
         ClassTools.invoke(dummy, "callMe");
         assertEquals(dummy.wasCalled(), true);
     }
 
     /**
      * Dummy class to be used for construction and invocation tests
      *
      */
     public static class Dummy {
         private boolean called = false;
 
         /**
          * @return <code>true</code> if {@link Dummy#callMe()} was called on
          *         this instance
          */
         public boolean wasCalled() {
             return this.called;
         }
 
         /**
          * Simple method to be called on void invocation
          */
         public void callMe() {
             this.called = true;
         }
     }
 
 }
diff --git a/test/src/org/apache/jorphan/reflect/TestFunctor.java b/test/src/org/apache/jorphan/reflect/TestFunctor.java
index c302d68dd..9ea3ce4bb 100644
--- a/test/src/org/apache/jorphan/reflect/TestFunctor.java
+++ b/test/src/org/apache/jorphan/reflect/TestFunctor.java
@@ -1,228 +1,235 @@
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
 
 package org.apache.jorphan.reflect;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
 import java.util.Map;
 import java.util.Properties;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
+import org.junit.Before;
+import org.junit.Test;
 
 /*
  * Unit tests for classes that use Functors
  * 
  */
 public class TestFunctor extends JMeterTestCase {
 
     interface HasName {
         String getName();
     }
     
     interface HasString {
         String getString(String s);
     }
 
     class Test1 implements HasName {
         private final String name;
         public Test1(){
             this("");
         }
         public Test1(String s){
             name=s;
         }
         @Override
         public String getName(){
             return name;
         }
         public String getString(String s){
             return s;
         }
     }
     class Test1a extends Test1{
         Test1a(){
             super("1a");
         }
         Test1a(String s){
             super("1a:"+s);
         }
         @Override
         public String getName(){
             return super.getName()+".";
         }
     }
     static class Test2 implements HasName, HasString {
         private final String name;
         public Test2(){
             this("");
         }
         public Test2(String s){
             name=s;
         }
         @Override
         public String getName(){
             return name;
         }
         @Override
         public String getString(String s){
             return s;
         }
     }
     
-    public TestFunctor(String arg0) {
-        super(arg0);
-    }
-    
-    @Override
+    @Before
     public void setUp(){
         LoggingManager.setPriority("FATAL_ERROR",LoggingManager.removePrefix(Functor.class.getName()));     
     }
 
+    @Test
     public void testName() throws Exception{
         Functor f1 = new Functor("getName");
         Functor f2 = new Functor("getName");
         Functor f1a = new Functor("getName");
         Test1 t1 = new Test1("t1");
         Test2 t2 = new Test2("t2");
         Test1a t1a = new Test1a("aa");
         assertEquals("t1",f1.invoke(t1));
         //assertEquals("t1",f1.invoke());
         try {
             f1.invoke(t2);
             fail("Should have generated error");
         } catch (JMeterError e){
             
         }
         assertEquals("t2",f2.invoke(t2));
         //assertEquals("t2",f2.invoke());
         assertEquals("1a:aa.",f1a.invoke(t1a));
         //assertEquals("1a:aa.",f1a.invoke());
         try {
             f1a.invoke(t1);// can't call invoke using super class
             fail("Should have generated error");
         } catch (JMeterError e){
             
         }
         // OK (currently) to invoke using sub-class 
         assertEquals("1a:aa.",f1.invoke(t1a));
         //assertEquals("1a:aa.",f1.invoke());// N.B. returns different result from before
     }
     
+    @Test
     public void testNameTypes() throws Exception{
         Functor f = new Functor("getString",new Class[]{String.class});
         Functor f2 = new Functor("getString");// Args will be provided later
         Test1 t1 = new Test1("t1");
         assertEquals("x1",f.invoke(t1,new String[]{"x1"}));
         try {
             assertEquals("x1",f.invoke(t1));
             fail("Should have generated an Exception");
         } catch (JMeterError ok){
         }
         assertEquals("x2",f2.invoke(t1,new String[]{"x2"}));
         try {
             assertEquals("x2",f2.invoke(t1));
             fail("Should have generated an Exception");
         } catch (JMeterError ok){
         }
     }
+    @Test
     public void testObjectName() throws Exception{
         Test1 t1 = new Test1("t1");
         Test2 t2 = new Test2("t2");
         Functor f1 = new Functor(t1,"getName");
         assertEquals("t1",f1.invoke(t1));
         assertEquals("t1",f1.invoke(t2)); // should use original object
     }
     
     // Check how Class definition behaves
+    @Test
     public void testClass() throws Exception{
         Test1 t1 = new Test1("t1");
         Test1 t1a = new Test1a("t1a");
         Test2 t2 = new Test2("t2");
         Functor f1 = new Functor(HasName.class,"getName");
         assertEquals("t1",f1.invoke(t1));
         assertEquals("1a:t1a.",f1.invoke(t1a));
         assertEquals("t2",f1.invoke(t2));
         try {
             f1.invoke();
             fail("Should have failed");
         } catch (IllegalStateException ok){
             
         }
         Functor f2 = new Functor(HasString.class,"getString");
         assertEquals("xyz",f2.invoke(t2,new String[]{"xyz"}));
         try {
             f2.invoke(t1,new String[]{"xyz"});
             fail("Should have failed");
         } catch (JMeterError ok){
             
         }
         Functor f3 = new Functor(t2,"getString");
         assertEquals("xyz",f3.invoke(t2,new Object[]{"xyz"}));
         
         Properties p = new Properties();
         p.put("Name","Value");
         Functor fk = new Functor(Map.Entry.class,"getKey");
         Functor fv = new Functor(Map.Entry.class,"getValue");
         Object o = p.entrySet().iterator().next();
         assertEquals("Name",fk.invoke(o));
         assertEquals("Value",fv.invoke(o));
     }
     
+    @Test
     public void testBadParameters() throws Exception{
         try {
             new Functor(null);
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(null,new Class[]{});
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(null,new Object[]{});
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(String.class,null);
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(new Object(),null);
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(new Object(),null, new Class[]{});
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
         try {
             new Functor(new Object(),null, new Object[]{});
             fail("should have generated IllegalArgumentException;");
         } catch (IllegalArgumentException ok){}
     }
+    @Test
     public void testIllegalState() throws Exception{
         Functor f = new Functor("method");
         try {
             f.invoke();
             fail("should have generated IllegalStateException;");
         } catch (IllegalStateException ok){}        
         try {
             f.invoke(new Object[]{});
             fail("should have generated IllegalStateException;");
         } catch (IllegalStateException ok){}        
     }
 }
diff --git a/test/src/org/apache/jorphan/util/TestConverter.java b/test/src/org/apache/jorphan/util/TestConverter.java
index 4b479ff5e..5df0c15ff 100644
--- a/test/src/org/apache/jorphan/util/TestConverter.java
+++ b/test/src/org/apache/jorphan/util/TestConverter.java
@@ -1,135 +1,134 @@
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
 
+import static org.junit.Assert.assertEquals;
+
 import java.text.DateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
-
-import junit.framework.TestCase;
-
 import org.junit.Test;
 
 /**
  * Tests for {@link Converter}
  *
  */
-public class TestConverter extends TestCase {
+public class TestConverter {
 
     /**
      * Test {@link Converter#getCalendar(Object, Calendar)} with a given Date
      * and null as default value
      */
     @Test
     public void testGetCalendarObjectCalendarWithTimeAndNullDefault() {
         Calendar cal = new GregorianCalendar();
         Date time = cal.getTime();
         assertEquals(cal, Converter.getCalendar(time, null));
     }
 
     /**
      * Test {@link Converter#getCalendar(Object, Calendar)} with null as Date
      * and a sensible default value
      */
     @Test
     public void testGetCalendarObjectCalendarWithNullAndCalendarAsDefault() {
         Calendar cal = new GregorianCalendar();
         assertEquals(cal, Converter.getCalendar(null, cal));
     }
 
     /**
      * Test {@link Converter#getCalendar(Object, Calendar)} with correctly
      * formatted strings and <code>null</code> as default value
      */
     @Test
     public void testGetCalendarObjectCalendarWithValidStringAndNullDefault() {
         Calendar cal = new GregorianCalendar();
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         Date time = cal.getTime();
         for (int formatId : new int[]{DateFormat.SHORT, DateFormat.MEDIUM,
                 DateFormat.LONG, DateFormat.FULL}) {
             DateFormat formatter = DateFormat.getDateInstance(formatId);
             assertEquals(cal,
                     Converter.getCalendar(formatter.format(time), null));
         }
     }
 
     /**
      * Test {@link Converter#getCalendar(Object, Calendar)} with an invalid
      * string and <code>null</code> as default value
      */
     @Test
     public void testGetCalendarObjectCalendarWithInvalidStringAndNullDefault() {
         assertEquals(null, Converter.getCalendar("invalid date", null));
     }
 
     /**
      * Test {@link Converter#getDate(Object, Date)} with a given Date
      * and null as default value
      */
     @Test
     public void testGetDateObjectDateWithTimeAndNullDefault() {
         Date time = new Date();
         assertEquals(time, Converter.getDate(time, null));
     }
 
     /**
      * Test {@link Converter#getDate(Object, Date)} with null as Date
      * and a sensible default value
      */
     @Test
     public void testGetDateObjectDateWithNullAndDateAsDefault() {
         Date date = new Date();
         assertEquals(date, Converter.getDate(null, date));
     }
 
     /**
      * Test {@link Converter#getDate(Object, Date)} with correctly
      * formatted strings and <code>null</code> as default value
      */
     @Test
     public void testGetDateObjectDateWithValidStringAndNullDefault() {
         Calendar cal = new GregorianCalendar();
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         Date time = cal.getTime();
         for (int formatId : new int[]{DateFormat.SHORT, DateFormat.MEDIUM,
                 DateFormat.LONG, DateFormat.FULL}) {
             DateFormat formatter = DateFormat.getDateInstance(formatId);
             assertEquals(time,
                     Converter.getDate(formatter.format(time), null));
         }
     }
 
     /**
      * Test {@link Converter#getDate(Object, Date)} with an invalid
      * string and <code>null</code> as default value
      */
     @Test
     public void testGetDateObjectDateWithInvalidStringAndNullDefault() {
         assertEquals(null, Converter.getDate("invalid date", null));
     }
 
 }
diff --git a/test/src/org/apache/jorphan/util/TestJorphanUtils.java b/test/src/org/apache/jorphan/util/TestJorphanUtils.java
index d2f2d498b..866fe7e9a 100644
--- a/test/src/org/apache/jorphan/util/TestJorphanUtils.java
+++ b/test/src/org/apache/jorphan/util/TestJorphanUtils.java
@@ -1,324 +1,349 @@
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
 
 /**
  * Package to test JOrphanUtils methods 
  */
      
 package org.apache.jorphan.util;
 
-import junit.framework.TestCase;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
 
-public class TestJorphanUtils extends TestCase {
+import org.junit.Test;
 
-    public TestJorphanUtils() {
-        super();
-    }
+public class TestJorphanUtils {
 
-    public TestJorphanUtils(String arg0) {
-        super(arg0);
-    }
-    
+    @Test
     public void testReplace1() {
         assertEquals("xyzdef", JOrphanUtils.replaceFirst("abcdef", "abc", "xyz"));
     }
 
+    @Test
     public void testReplace2() {
         assertEquals("axyzdef", JOrphanUtils.replaceFirst("abcdef", "bc", "xyz"));
     }
 
+    @Test
     public void testReplace3() {
         assertEquals("abcxyz", JOrphanUtils.replaceFirst("abcdef", "def", "xyz"));
     }
 
+    @Test
     public void testReplace4() {
         assertEquals("abcdef", JOrphanUtils.replaceFirst("abcdef", "bce", "xyz"));
     }
 
+    @Test
     public void testReplace5() {
         assertEquals("abcdef", JOrphanUtils.replaceFirst("abcdef", "alt=\"\" ", ""));
     }
 
+    @Test
     public void testReplace6() {
         assertEquals("abcdef", JOrphanUtils.replaceFirst("abcdef", "alt=\"\" ", ""));
     }
 
+    @Test
     public void testReplace7() {
         assertEquals("alt=\"\"", JOrphanUtils.replaceFirst("alt=\"\"", "alt=\"\" ", ""));
     }
 
+    @Test
     public void testReplace8() {
         assertEquals("img src=xyz ", JOrphanUtils.replaceFirst("img src=xyz alt=\"\" ", "alt=\"\" ", ""));
     }
 
     // Note: the split tests should agree as far as possible with CSVSaveService.csvSplitString()
     
     // Tests for split(String,String,boolean)
+    @Test
     public void testSplit1() {
         String in = "a,bc,,"; // Test ignore trailing split characters
         String out[] = JOrphanUtils.split(in, ",",true);// Ignore adjacent delimiters
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         out = JOrphanUtils.split(in, ",",false);
         assertEquals("Should detect the trailing split chars; ", 4, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         assertEquals("", out[2]);
         assertEquals("", out[3]);
     }
 
+    @Test
     public void testSplit2() {
         String in = ",,a,bc"; // Test leading split characters
         String out[] = JOrphanUtils.split(in, ",",true);
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         out = JOrphanUtils.split(in, ",",false);
         assertEquals("Should detect the leading split chars; ", 4, out.length);
         assertEquals("", out[0]);
         assertEquals("", out[1]);
         assertEquals("a", out[2]);
         assertEquals("bc", out[3]);
     }
     
+    @Test
     public void testSplit3() {
         String in = "a,bc,,"; // Test ignore trailing split characters
         String out[] = JOrphanUtils.split(in, ",",true);// Ignore adjacent delimiters
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         out = JOrphanUtils.split(in, ",",false);
         assertEquals("Should detect the trailing split chars; ", 4, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         assertEquals("", out[2]);
         assertEquals("", out[3]);
     }
 
+    @Test
     public void testSplit4() {
         String in = " , ,a ,bc"; // Test leading split characters
         String out[] = JOrphanUtils.split(in, " ,",true);
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         out = JOrphanUtils.split(in, " ,",false);
         assertEquals("Should detect the leading split chars; ", 4, out.length);
         assertEquals("", out[0]);
         assertEquals("", out[1]);
         assertEquals("a", out[2]);
         assertEquals("bc", out[3]);
     }
     
+    @Test
     public void testTruncate() throws Exception
     {
         String in = "a;,b;,;,;,d;,e;,;,f";
         String[] out = JOrphanUtils.split(in,";,",true);
         assertEquals(5, out.length);
         assertEquals("a",out[0]);
         assertEquals("b",out[1]);
         assertEquals("d",out[2]);
         assertEquals("e",out[3]);
         assertEquals("f",out[4]);
         out = JOrphanUtils.split(in,";,",false);
         assertEquals(8, out.length);
         assertEquals("a",out[0]);
         assertEquals("b",out[1]);
         assertEquals("", out[2]);
         assertEquals("", out[3]);
         assertEquals("d",out[4]);
         assertEquals("e",out[5]);
         assertEquals("", out[6]);
         assertEquals("f",out[7]);
         
     }
 
+    @Test
     public void testSplit5() throws Exception
     {
         String in = "a;;b;;;;;;d;;e;;;;f";
         String[] out = JOrphanUtils.split(in,";;",true);
         assertEquals(5, out.length);
         assertEquals("a",out[0]);
         assertEquals("b",out[1]);
         assertEquals("d",out[2]);
         assertEquals("e",out[3]);
         assertEquals("f",out[4]);
         out = JOrphanUtils.split(in,";;",false);
         assertEquals(8, out.length);
         assertEquals("a",out[0]);
         assertEquals("b",out[1]);
         assertEquals("", out[2]);
         assertEquals("", out[3]);
         assertEquals("d",out[4]);
         assertEquals("e",out[5]);
         assertEquals("", out[6]);
         assertEquals("f",out[7]);
         
     }
 
     // Empty string
+    @Test
     public void testEmpty(){
         String out[] = JOrphanUtils.split("", ",",false);   
         assertEquals(0,out.length);
     }
 
     // Tests for split(String,String,String)
+    @Test
     public void testSplitSSS1() {
         String in = "a,bc,,"; // Test non-empty parameters
         String out[] = JOrphanUtils.split(in, ",","?");
         assertEquals(4, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         assertEquals("?", out[2]);
         assertEquals("?", out[3]);
     }
 
+    @Test
     public void testSplitSSS2() {
         String in = "a,bc,,"; // Empty default
         String out[] = JOrphanUtils.split(in, ",","");
         assertEquals(4, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         assertEquals("", out[2]);
         assertEquals("", out[3]);
     }
 
+    @Test
     public void testSplitSSS3() {
         String in = "a,bc,,"; // Empty delimiter
         String out[] = JOrphanUtils.split(in, "","?");
         assertEquals(1, out.length);
         assertEquals(in, out[0]);
     }
 
+    @Test
     public void testSplitSSS4() {
         String in = "a,b;c,,"; // Multiple delimiters
         String out[];
         out = JOrphanUtils.split(in, ",;","?");
         assertEquals(5, out.length);
         assertEquals("a", out[0]);
         assertEquals("b", out[1]);
         assertEquals("c", out[2]);
         assertEquals("?", out[3]);
         assertEquals("?", out[4]);
         out = JOrphanUtils.split(in, ",;","");
         assertEquals(5, out.length);
         assertEquals("a", out[0]);
         assertEquals("b", out[1]);
         assertEquals("c", out[2]);
         assertEquals("", out[3]);
         assertEquals("", out[4]);
     }
 
+    @Test
     public void testSplitSSS5() {
         String in = "a,bc,,"; // Delimiter same as splitter
         String out[] = JOrphanUtils.split(in, ",",",");
         assertEquals(4, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
         assertEquals(",", out[2]);
         assertEquals(",", out[3]);
     }
 
+    @Test
     public void testSplitSSSNulls() {
         String in = "a,bc,,";
         String out[];
         try {
             out = JOrphanUtils.split(null, ",","?");
             assertEquals(0, out.length);
             fail("Expecting NullPointerException");
         } catch (NullPointerException ignored){
             //Ignored
         }
         try{
             out = JOrphanUtils.split(in, null,"?");
             assertEquals(0, out.length);
             fail("Expecting NullPointerException");
         } catch (NullPointerException ignored){
             //Ignored
         }
     }
 
+    @Test
     public void testSplitSSSNull() {
         String out[];
         out = JOrphanUtils.split("a,bc,,", ",",null);
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
 
         out = JOrphanUtils.split("a,;bc,;,", ",;",null);
         assertEquals(2, out.length);
         assertEquals("a", out[0]);
         assertEquals("bc", out[1]);
     }
 
+    @Test
     public void testSplitSSSNone() {
         String out[];
         out = JOrphanUtils.split("", "," ,"x");
         assertEquals(0, out.length);
 
         out = JOrphanUtils.split("a,;bc,;,", "","x");
         assertEquals(1, out.length);
         assertEquals("a,;bc,;,", out[0]);
     }
 
+    @Test
     public void testreplaceAllChars(){
         assertEquals(JOrphanUtils.replaceAllChars("",' ', "+"),"");
         String in,out;
         in="source";
         assertEquals(JOrphanUtils.replaceAllChars(in,' ', "+"),in);
         out="so+rce";
         assertEquals(JOrphanUtils.replaceAllChars(in,'u', "+"),out);
         in="A B  C "; out="A+B++C+";
         assertEquals(JOrphanUtils.replaceAllChars(in,' ', "+"),out);
     }
     
+    @Test
     public void testTrim(){
         assertEquals("",JOrphanUtils.trim("", " ;"));
         assertEquals("",JOrphanUtils.trim(" ", " ;"));
         assertEquals("",JOrphanUtils.trim("; ", " ;"));
         assertEquals("",JOrphanUtils.trim(";;", " ;"));
         assertEquals("",JOrphanUtils.trim("  ", " ;"));
         assertEquals("abc",JOrphanUtils.trim("abc ;", " ;"));
     }
     
+    @Test
     public void testbaToHexString(){
         assertEquals("",JOrphanUtils.baToHexString(new byte[]{}));
         assertEquals("00",JOrphanUtils.baToHexString(new byte[]{0}));
         assertEquals("0f107f8081ff",JOrphanUtils.baToHexString(new byte[]{15,16,127,-128,-127,-1}));
     }
 
+    @Test
     public void testbaToByte() throws Exception{
         assertEqualsArray(new byte[]{},JOrphanUtils.baToHexBytes(new byte[]{}));
         assertEqualsArray(new byte[]{'0','0'},JOrphanUtils.baToHexBytes(new byte[]{0}));
         assertEqualsArray("0f107f8081ff".getBytes("UTF-8"),JOrphanUtils.baToHexBytes(new byte[]{15,16,127,-128,-127,-1}));
     }
 
     private void assertEqualsArray(byte[] expected, byte[] actual){
         assertEquals("arrays must be same length",expected.length, actual.length);
         for(int i=0; i < expected.length; i++){
             assertEquals("values must be the same for index: "+i,expected[i],actual[i]);
         }
     }
     
+    @Test
     public void testIsBlank() {
         assertTrue(JOrphanUtils.isBlank(""));
         assertTrue(JOrphanUtils.isBlank(null));
         assertTrue(JOrphanUtils.isBlank("    "));
         assertFalse(JOrphanUtils.isBlank(" zdazd dzd "));
     }
 }
