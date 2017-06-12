diff --git a/test/src/org/apache/jmeter/util/TestJMeterUtils.java b/test/src/org/apache/jmeter/util/TestJMeterUtils.java
index b6936635f..3e0f91000 100644
--- a/test/src/org/apache/jmeter/util/TestJMeterUtils.java
+++ b/test/src/org/apache/jmeter/util/TestJMeterUtils.java
@@ -1,40 +1,45 @@
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
 
 import junit.framework.TestCase;
 
 public class TestJMeterUtils extends TestCase {
 
     public TestJMeterUtils() {
         super();
     }
 
     public TestJMeterUtils(String arg0) {
         super(arg0);
     }
-    //TODO add some real tests now that split() has been removed
-    public void test1() throws Exception{
-        
+
+    public void testGetResourceFileAsText() throws Exception{
+        String sep = System.getProperty("line.separator");
+        assertEquals("line one" + sep + "line two" + sep, JMeterUtils.getResourceFileAsText("resourcefile.txt"));
+    }
+    
+    public void testGetResourceFileAsTextWithMisingResource() throws Exception{
+        assertEquals("", JMeterUtils.getResourceFileAsText("not_existant_resourcefile.txt"));
     }
 }
