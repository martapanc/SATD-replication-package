60/report.java
Satd-method: public int executeInVM(CommandlineJava commandline) throws BuildException {
********************************************
********************************************
60/Between/[PATCH] Misspel 98c3a0ea1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
executeInVM(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setWriter
* getMessage
* addDirectory
* JDepend
* list
* setFilter
* PackageFilter
* analyze
* getPath
* addElement
* getExcludePatterns
* isDirectory
********************************************
********************************************
60/Between/Fix NPE, PR: 24 eba9a3c2d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
executeInVM(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setWriter
* getMessage
* addDirectory
* JDepend
* list
* setFilter
* PackageFilter
* analyze
* getPath
* addElement
* getExcludePatterns
* isDirectory
********************************************
********************************************
60/Between/JDependTask did e160d8323_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        FileWriter fw = null;
-            FileWriter fw;
-        if (getClassespath() != null) {
-            // This is the new, better way - use classespath instead
-            // of sourcespath.  The code is currently the same - you
-            // need class files in a directory to use this - jar files
-            // coming soon....
-            String[] classesPath = getClassespath().list();
-            for (int i = 0; i < classesPath.length; i++) {
-                File f = new File(classesPath[i]);
-                // not necessary as JDepend would fail, but why loose
-                // some time?
-                if (!f.exists() || !f.isDirectory()) {
-                    String msg = "\""
-                        + f.getPath()
-                        + "\" does not represent a valid"
-                        + " directory. JDepend would fail.";
-                    log(msg);
-                    throw new BuildException(msg);
+
+        try {
+            if (getClassespath() != null) {
+                // This is the new, better way - use classespath instead
+                // of sourcespath.  The code is currently the same - you
+                // need class files in a directory to use this - jar files
+                // coming soon....
+                String[] classesPath = getClassespath().list();
+                for (int i = 0; i < classesPath.length; i++) {
+                    File f = new File(classesPath[i]);
+                    // not necessary as JDepend would fail, but why loose
+                    // some time?
+                    if (!f.exists() || !f.isDirectory()) {
+                        String msg = "\""
+                            + f.getPath()
+                            + "\" does not represent a valid"
+                            + " directory. JDepend would fail.";
+                        log(msg);
+                        throw new BuildException(msg);
+                    }
+                    try {
+                        jdepend.addDirectory(f.getPath());
+                    } catch (IOException e) {
+                        String msg =
+                            "JDepend Failed when adding a class directory: "
+                            + e.getMessage();
+                        log(msg);
+                        throw new BuildException(msg);
+                    }
-                try {
-                    jdepend.addDirectory(f.getPath());
-                } catch (IOException e) {
-                    String msg =
-                        "JDepend Failed when adding a class directory: "
-                        + e.getMessage();
-                    log(msg);
-                    throw new BuildException(msg);
+
+            } else if (getSourcespath() != null) {
+
+                // This is the old way and is deprecated - classespath is
+                // the right way to do this and is above
+                String[] sourcesPath = getSourcespath().list();
+                for (int i = 0; i < sourcesPath.length; i++) {
+                    File f = new File(sourcesPath[i]);
+
+                    // not necessary as JDepend would fail, but why loose
+                    // some time?
+                    if (!f.exists() || !f.isDirectory()) {
+                        String msg = "\""
+                            + f.getPath()
+                            + "\" does not represent a valid"
+                            + " directory. JDepend would fail.";
+                        log(msg);
+                        throw new BuildException(msg);
+                    }
+                    try {
+                        jdepend.addDirectory(f.getPath());
+                    } catch (IOException e) {
+                        String msg =
+                            "JDepend Failed when adding a source directory: "
+                            + e.getMessage();
+                        log(msg);
+                        throw new BuildException(msg);
+                    }
-        } else if (getSourcespath() != null) {
-
-            // This is the old way and is deprecated - classespath is
-            // the right way to do this and is above
-            String[] sourcesPath = getSourcespath().list();
-            for (int i = 0; i < sourcesPath.length; i++) {
-                File f = new File(sourcesPath[i]);
-
-                // not necessary as JDepend would fail, but why loose
-                // some time?
-                if (!f.exists() || !f.isDirectory()) {
-                    String msg = "\""
-                        + f.getPath()
-                        + "\" does not represent a valid"
-                        + " directory. JDepend would fail.";
-                    log(msg);
-                    throw new BuildException(msg);
-                }
-                try {
-                    jdepend.addDirectory(f.getPath());
-                } catch (IOException e) {
-                    String msg =
-                        "JDepend Failed when adding a source directory: "
-                        + e.getMessage();
-                    log(msg);
-                    throw new BuildException(msg);
+            // This bit turns <exclude> child tags into patters to ignore
+            String[] patterns = defaultPatterns.getExcludePatterns(getProject());
+            if (patterns != null && patterns.length > 0) {
+                if (setFilter != null) {
+                    Vector v = new Vector();
+                    for (int i = 0; i < patterns.length; i++) {
+                        v.addElement(patterns[i]);
+                    }
+                    try {
+                        Object o = packageFilterC.newInstance(new Object[] {v});
+                        setFilter.invoke(jdepend, new Object[] {o});
+                    } catch (Throwable e) {
+                        log("excludes will be ignored as JDepend doesn't like me: "
+                            + e.getMessage(), Project.MSG_WARN);
+                    }
+                } else {
+                    log("Sorry, your version of JDepend doesn't support excludes",
+                        Project.MSG_WARN);
-        }
-        // This bit turns <exclude> child tags into patters to ignore
-        String[] patterns = defaultPatterns.getExcludePatterns(getProject());
-        if (patterns != null && patterns.length > 0) {
-            if (setFilter != null) {
-                Vector v = new Vector();
-                for (int i = 0; i < patterns.length; i++) {
-                    v.addElement(patterns[i]);
-                }
+            jdepend.analyze();
+        } finally {
+            if (fw != null) {
-                    Object o = packageFilterC.newInstance(new Object[] {v});
-                    setFilter.invoke(jdepend, new Object[] {o});
-                } catch (Throwable e) {
-                    log("excludes will be ignored as JDepend doesn't like me: "
-                        + e.getMessage(), Project.MSG_WARN);
+                    fw.close();
+                } catch (Throwable t) {
+                    // Ignore
-            } else {
-                log("Sorry, your version of JDepend doesn't support excludes",
-                    Project.MSG_WARN);
-
-        jdepend.analyze();

Lines added: 82. Lines removed: 71. Tot = 153
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
executeInVM(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setWriter
* getMessage
* addDirectory
* JDepend
* list
* setFilter
* PackageFilter
* analyze
* getPath
* addElement
* getExcludePatterns
* isDirectory
********************************************
********************************************
60/Between/provide a Map b ff41336fc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
executeInVM(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setWriter
* getMessage
* addDirectory
* JDepend
* list
* setFilter
* PackageFilter
* analyze
* getPath
* addElement
* getExcludePatterns
* isDirectory
********************************************
********************************************
60/Between/remove authors  c885f5683_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
executeInVM(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setWriter
* getMessage
* addDirectory
* JDepend
* list
* setFilter
* PackageFilter
* analyze
* getPath
* addElement
* getExcludePatterns
* isDirectory
********************************************
********************************************
