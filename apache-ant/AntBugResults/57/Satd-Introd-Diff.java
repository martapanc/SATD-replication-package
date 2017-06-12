diff --git a/src/testcases/org/apache/tools/ant/taskdefs/InputTest.java b/src/testcases/org/apache/tools/ant/taskdefs/InputTest.java
index 9b4993352..4faaa4bd7 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/InputTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/InputTest.java
@@ -1,79 +1,69 @@
 /*
  * Copyright  2001-2002,2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import org.apache.tools.ant.BuildFileTest;
 import org.apache.tools.ant.input.PropertyFileInputHandler;
-import org.apache.tools.ant.util.JavaEnvUtils;
 
-/**
- */
-public class InputTest extends BuildFileTest {
 
-    private String targetPostfix = "";
+public class InputTest extends BuildFileTest {
 
     public InputTest(String name) {
         super(name);
-        if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
-            targetPostfix = ".1";
-        }
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/input.xml");
         System.getProperties()
             .put(PropertyFileInputHandler.FILE_NAME_KEY,
                  getProject().resolveFile("input.properties")
                  .getAbsolutePath());
         getProject().setInputHandler(new PropertyFileInputHandler());
     }
 
     public void test1() {
-        executeTarget("test1" + targetPostfix);
+        executeTarget("test1");
     }
 
     public void test2() {
-        executeTarget("test2" + targetPostfix);
+        executeTarget("test2");
     }
 
     public void test3() {
-        expectSpecificBuildException("test3" + targetPostfix, "invalid input",
+        expectSpecificBuildException("test3", "invalid input",
                                      "Found invalid input test for \'"
                                      + getKey("All data is"
                                               + " going to be deleted from DB"
                                               + " continue?")
                                      + "\'");
     }
 
     public void test5() {
-        executeTarget("test5" + targetPostfix);
+        executeTarget("test5");
     }
 
     public void test6() {
-        executeTarget("test6" + targetPostfix);
+        executeTarget("test6");
         assertEquals("scott", project.getProperty("db.user"));
     }
 
     private String getKey(String key) {
-        if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
-            key = key.replace(' ', '_');
-        }
-        return key;
+        return key; // XXX what is this for?
     }
 
 }
