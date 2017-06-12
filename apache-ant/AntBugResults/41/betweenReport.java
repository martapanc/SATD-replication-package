41/report.java
Satd-method: public void execute() {
********************************************
********************************************
41/Between/deprecate rever d8922d6d2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getProperty
* printStackTrace
* getAbsolutePath
* addLoaderPackageRoot
* addReference
* getClassLoader
* list
* addPathElement
* setCoreLoader
* getReference
********************************************
********************************************
41/Between/make log more u b6aa5cb51_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                        acl.addPathElement(f.getAbsolutePath());
+                        acl.addPathElement(f.getAbsolutePath());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getProperty
* printStackTrace
* getAbsolutePath
* addLoaderPackageRoot
* addReference
* getClassLoader
* list
* addPathElement
* setCoreLoader
* getReference
********************************************
********************************************
41/Between/only add classp e0d63af0c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            boolean existingLoader = acl != null;
-            if (classpath != null) {
+
+            if (existingLoader && classpath != null) {

Lines added: 3. Lines removed: 1. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getProperty
* printStackTrace
* getAbsolutePath
* addLoaderPackageRoot
* addReference
* getClassLoader
* list
* addPathElement
* setCoreLoader
* getReference
********************************************
********************************************
41/Between/remove authors  c885f5683_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getProperty
* printStackTrace
* getAbsolutePath
* addLoaderPackageRoot
* addReference
* getClassLoader
* list
* addPathElement
* setCoreLoader
* getReference
********************************************
********************************************
41/Between/Remove direct c 3396e7c32_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            if ("only".equals(project.getProperty("build.sysclasspath"))
+            if ("only".equals(getProject().getProperty("build.sysclasspath"))
-            Object obj = project.getReference(loaderName);
+            Object obj = getProject().getReference(loaderName);
-                    parent = project.getReference(parentName);
+                    parent = getProject().getReference(parentName);
-                project.log("Setting parent loader " + name + " "
+                getProject().log("Setting parent loader " + name + " "
-                        project, classpath, parentFirst);
+                         getProject(), classpath, parentFirst);
-                project.addReference(loaderName, acl);
+                getProject().addReference(loaderName, acl);
-                    project.setCoreLoader(acl);
+                    getProject().setCoreLoader(acl);

Lines added: 7. Lines removed: 7. Tot = 14
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
execute(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getProperty
* printStackTrace
* getAbsolutePath
* addLoaderPackageRoot
* addReference
* getClassLoader
* list
* addPathElement
* setCoreLoader
* getReference
********************************************
********************************************
