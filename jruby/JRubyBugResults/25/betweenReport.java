25/report.java
Satd-method: public int run(NGContext context) {
********************************************
********************************************
25/Between/20ff6278c  Fix JRUBY-5322: NPE forc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        // FIXME: This is almost entirely duplicated from Main.java
-        try {
-            // populate commandline with NG-provided stuff
-            config.processArguments(context.getArgs());
-            config.setCurrentDirectory(context.getWorkingDirectory());
-            config.setEnvironment(context.getEnv());
+        config.setCurrentDirectory(context.getWorkingDirectory());
+        config.setEnvironment(context.getEnv());
-            // reuse one cache of compiled bodies
-            config.setClassCache(classCache);
+        // reuse one cache of compiled bodies
+        config.setClassCache(classCache);
-            return main.run().getStatus();
-        } catch (MainExitException mee) {
-            if (!mee.isAborted()) {
-                config.getOutput().println(mee.getMessage());
-                if (mee.isUsageError()) {
-                    main.printUsage();
-                }
-            }
-            return mee.getStatus();
-        } catch (OutOfMemoryError oome) {
-            // produce a nicer error since Rubyists aren't used to seeing this
-            System.gc();
-
-            String memoryMax = SafePropertyAccessor.getProperty("jruby.memory.max");
-            String message = "";
-            if (memoryMax != null) {
-                message = " of " + memoryMax;
-            }
-            config.getError().println("Error: Your application used more memory than the safety cap" + message + ".");
-            config.getError().println("Specify -J-Xmx####m to increase it (#### = cap size in MB).");
-
-            if (config.getVerbose()) {
-                config.getError().println("Exception trace follows:");
-                oome.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full OutOfMemoryError stack trace");
-            }
-            return 1;
-        } catch (StackOverflowError soe) {
-            // produce a nicer error since Rubyists aren't used to seeing this
-            System.gc();
-
-            String stackMax = SafePropertyAccessor.getProperty("jruby.stack.max");
-            String message = "";
-            if (stackMax != null) {
-                message = " of " + stackMax;
-            }
-            config.getError().println("Error: Your application used more stack memory than the safety cap" + message + ".");
-            config.getError().println("Specify -J-Xss####k to increase it (#### = cap size in KB).");
-
-            if (config.getVerbose()) {
-                config.getError().println("Exception trace follows:");
-                soe.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full StackOverflowError stack trace");
-            }
-            return 1;
-        } catch (UnsupportedClassVersionError ucve) {
-            config.getError().println("Error: Some library (perhaps JRuby) was built with a later JVM version.");
-            config.getError().println("Please use libraries built with the version you intend to use or an earlier one.");
-
-            if (config.getVerbose()) {
-                config.getError().println("Exception trace follows:");
-                ucve.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full UnsupportedClassVersionError stack trace");
-            }
-            return 1;
-        } catch (ThreadKill kill) {
-            return 0;
-        }
+        return main.run(context.getArgs()).getStatus();

Lines added: 5. Lines removed: 69. Tot = 74
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
run(
-            return main.run().getStatus();
+        return main.run(context.getArgs()).getStatus();

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* assertLoopbackClient
* getStatus
* setEnvironment
* printUsage
* gc
* getOutput
* setCurrentDirectory
* getWorkingDirectory
* getProperty
* printStackTrace
* getMessage
* processArguments
* getArgs
* getError
* getVerbose
* isUsageError
* getEnv
* isAborted
—————————
Method found in diff:	+        public int getStatus() { return status; }
+        public int getStatus() { return status; }

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public void setEnvironment(Map newEnvironment) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public void printUsage() {
-    public void printUsage() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public PrintStream getOutput() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setCurrentDirectory(String dir) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void processArguments(String[] arguments) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PrintStream getError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getVerbose() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
