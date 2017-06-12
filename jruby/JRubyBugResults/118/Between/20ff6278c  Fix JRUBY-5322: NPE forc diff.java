diff --git a/src/org/jruby/Main.java b/src/org/jruby/Main.java
index f74085d51e..2281b52e11 100644
--- a/src/org/jruby/Main.java
+++ b/src/org/jruby/Main.java
@@ -1,454 +1,399 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2005 Jason Voegele <jason@jvoegele.com>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
-import java.io.BufferedReader;
-import java.io.IOException;
 import java.io.InputStream;
-import java.io.InputStreamReader;
 import java.io.PrintStream;
 import java.io.File;
 
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.SimpleSampler;
 
 /**
  * Class used to launch the interpreter.
  * This is the main class as defined in the jruby.mf manifest.
  * It is very basic and does not support yet the same array of switches
  * as the C interpreter.
  *       Usage: java -jar jruby.jar [switches] [rubyfile.rb] [arguments]
  *           -e 'command'    one line of script. Several -e's allowed. Omit [programfile]
  * @author  jpetersen
  */
 public class Main {
-    private boolean hasPrintedUsage = false;
-    private final RubyInstanceConfig config;
-
     public Main(RubyInstanceConfig config) {
         this(config, false);
     }
 
-    private Main(RubyInstanceConfig config, boolean hardExit) {
-        this.config = config;
-        config.setHardExit(hardExit);
-    }
-
     public Main(final InputStream in, final PrintStream out, final PrintStream err) {
         this(new RubyInstanceConfig(){{
             setInput(in);
             setOutput(out);
             setError(err);
         }});
     }
 
     public Main() {
         this(new RubyInstanceConfig());
     }
 
+    private Main(RubyInstanceConfig config, boolean hardExit) {
+        this.config = config;
+        config.setHardExit(hardExit);
+    }
+
     private Main(boolean hardExit) {
         this(new RubyInstanceConfig(), hardExit);
     }
 
+    public static class Status {
+        private boolean isExit = false;
+        private int status = 0;
+
+        /**
+         * Creates a status object with the specified value and with explicit
+         * exit flag. An exit flag means that Kernel.exit() has been explicitly
+         * invoked during the run.
+         *
+         * @param staus The status value.
+         */
+        Status(int status) {
+            this.isExit = true;
+            this.status = status;
+        }
+
+        /**
+         * Creates a status object with 0 value and no explicit exit flag.
+         */
+        Status() {}
+
+        public boolean isExit() { return isExit; }
+        public int getStatus() { return status; }
+    }
+
     /**
      * This is the command-line entry point for JRuby, and should ONLY be used by
      * Java when starting up JRuby from a command-line. Use other mechanisms when
      * embedding JRuby into another application.
      *
      * @param args command-line args, provided by the JVM.
      */
     public static void main(String[] args) {
-        // Ensure we're not running on GCJ, since it's not supported and leads to weird errors
-        if (Platform.IS_GCJ) {
-            System.err.println("Fatal: GCJ (GNU Compiler for Java) is not supported by JRuby.");
-            System.exit(1);
-        }
+        doGCJCheck();
         
         Main main = new Main(true);
         
         try {
             Status status = main.run(args);
             if (status.isExit()) {
                 System.exit(status.getStatus());
             }
         } catch (RaiseException rj) {
-            RubyException raisedException = rj.getException();
-            Ruby runtime = raisedException.getRuntime();
-            if (runtime.getSystemExit().isInstance(raisedException)) {
-                IRubyObject status = raisedException.callMethod(runtime.getCurrentContext(), "status");
-
-                if (status != null && !status.isNil()) {
-                    System.exit(RubyNumeric.fix2int(status));
-                }
-            } else {
-                rj.printStackTrace(System.err);
-                System.exit(1);
-            }
+            System.exit(handleRaiseException(rj));
         } catch (Throwable t) {
             // print out as a nice Ruby backtrace
             System.err.println(ThreadContext.createRawBacktraceStringFromThrowable(t));
             while ((t = t.getCause()) != null) {
                 System.err.println("Caused by:");
                 System.err.println(ThreadContext.createRawBacktraceStringFromThrowable(t));
             }
             System.exit(1);
         }
     }
 
     public Status run(String[] args) {
         try {
             config.processArguments(args);
             return run();
         } catch (MainExitException mee) {
-            if (!mee.isAborted()) {
-                config.getOutput().println(mee.getMessage());
-                if (mee.isUsageError()) {
-                    printUsage();
-                }
-            }
-            return new Status(mee.getStatus());
+            return handleMainExit(mee);
         } catch (OutOfMemoryError oome) {
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
-            if (config.isVerbose()) {
-                config.getError().println("Exception trace follows:");
-                oome.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full OutOfMemoryError stack trace");
-            }
-            return new Status(1);
+            return handleOutOfMemory(oome);
         } catch (StackOverflowError soe) {
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
-            if (config.isVerbose()) {
-                config.getError().println("Exception trace follows:");
-                soe.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full StackOverflowError stack trace");
-            }
-            return new Status(1);
+            return handleStackOverflow(soe);
         } catch (UnsupportedClassVersionError ucve) {
-            config.getError().println("Error: Some library (perhaps JRuby) was built with a later JVM version.");
-            config.getError().println("Please use libraries built with the version you intend to use or an earlier one.");
-            
-            if (config.isVerbose()) {
-                config.getError().println("Exception trace follows:");
-                ucve.printStackTrace();
-            } else {
-                config.getError().println("Specify -w for full UnsupportedClassVersionError stack trace");
-            }
-            return new Status(1);
+            return handleUnsupportedClassVersion(ucve);
         } catch (ThreadKill kill) {
             return new Status();
         }
     }
 
+    @Deprecated
     public Status run() {
-        if (config.isShowVersion()) {
-            showVersion();
-        }
-        
-        if (config.isShowCopyright()) {
-            showCopyright();
-        }
+        return internalRun();
+    }
+
+    private Status internalRun() {
+        doShowVersion();
+        doShowCopyright();
 
         if (!config.shouldRunInterpreter() ) {
-            if (config.shouldPrintUsage()) {
-                printUsage();
-            }
-            if (config.shouldPrintProperties()) {
-                printProperties();
-            }
+            doPrintUsage(false);
+            doPrintProperties();
             return new Status();
         }
 
         InputStream in   = config.getScriptSource();
         String filename  = config.displayedFileName();
-
-        String[] args = parseShebangOptions(in);
-        if (args.length > 0) {
-            config.processArguments(args);
-        }
+        
+        doProcessArguments(in);
         
         Ruby runtime     = Ruby.newInstance(config);
 
         try {
-            // set thread context JRuby classloader here, for the main thread
-            try {
-                Thread.currentThread().setContextClassLoader(runtime.getJRubyClassLoader());
-            } catch (SecurityException se) {
-                // can't set TC classloader
-                if (runtime.getInstanceConfig().isVerbose()) {
-                    config.getError().println("WARNING: Security restrictions disallowed setting context classloader for main thread.");
-                }
-            }
+            doSetContextClassLoader(runtime);
 
             if (in == null) {
-                // no script to run, return success below
+                // no script to run, return success
+                return new Status();
             } else if (config.isxFlag() && !config.hasShebangLine()) {
                 // no shebang was found and x option is set
                 throw new MainExitException(1, "jruby: no Ruby script found in input (LoadError)");
             } else if (config.isShouldCheckSyntax()) {
-                int status = 0;
-                try {
-                    runtime.parseFromMain(in, filename);
-                    config.getOutput().println("Syntax OK for " + filename);
-                } catch (RaiseException re) {
-                    status = -1;
-                    if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
-                        config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
-                    } else {
-                        throw re;
-                    }
-                }
-
-                if (config.getArgv().length > 0) {
-                    for (String arg : config.getArgv()) {
-                        File argFile = new File(arg);
-                        if (argFile.exists()) {
-                            try {
-                                runtime.parseFromMain(new FileInputStream(argFile), arg);
-                                config.getOutput().println("Syntax OK for " + arg);
-                            } catch (FileNotFoundException fnfe) {
-                                status = -1;
-                                config.getOutput().println("File not found: " + arg);
-                            } catch (RaiseException re) {
-                                status = -1;
-                                if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
-                                    config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
-                                } else {
-                                    throw re;
-                                }
-                            }
-                        } else {
-                            status = -1;
-                            config.getOutput().println("File not found: " + arg);
-                        }
-                    }
-                }
-                return new Status(status);
+                // check syntax only and exit
+                return doCheckSyntax(runtime, in, filename);
             } else {
-                long now = -1;
-
-                try {
-                    if (config.isBenchmarking()) {
-                        now = System.currentTimeMillis();
-                    }
-
-                    if (config.isSamplingEnabled()) {
-                        SimpleSampler.startSampleThread();
-                    }
-
-                    try {
-                        runtime.runFromMain(in, filename);
-                    } finally {
-                        if (config.isBenchmarking()) {
-                            config.getOutput().println("Runtime: " + (System.currentTimeMillis() - now) + " ms");
-                        }
-
-                        if (config.isSamplingEnabled()) {
-                            org.jruby.util.SimpleSampler.report();
-                        }
-                    }
-                } catch (RaiseException rj) {
-                    RubyException raisedException = rj.getException();
-                    if (runtime.getSystemExit().isInstance(raisedException)) {
-                        IRubyObject status = raisedException.callMethod(runtime.getCurrentContext(), "status");
-
-                        if (status != null && !status.isNil()) {
-                            return new Status(RubyNumeric.fix2int(status));
-                        } else {
-                            return new Status(0);
-                        }
-                    } else {
-                        runtime.printError(raisedException);
-                        return new Status(1);
-                    }
-                }
+                // proceed to run the script
+                return doRunFromMain(runtime, in, filename);
             }
-            return new Status();
         } finally {
             runtime.tearDown();
         }
     }
 
-    private void showVersion() {
-        config.getOutput().println(config.getVersionString());
+    private Status handleUnsupportedClassVersion(UnsupportedClassVersionError ucve) {
+        config.getError().println("Error: Some library (perhaps JRuby) was built with a later JVM version.");
+        config.getError().println("Please use libraries built with the version you intend to use or an earlier one.");
+        if (config.isVerbose()) {
+            config.getError().println("Exception trace follows:");
+            ucve.printStackTrace();
+        } else {
+            config.getError().println("Specify -w for full UnsupportedClassVersionError stack trace");
+        }
+        return new Status(1);
     }
 
-    private void showCopyright() {
-        config.getOutput().println(config.getCopyrightString());
+    private Status handleStackOverflow(StackOverflowError soe) {
+        // produce a nicer error since Rubyists aren't used to seeing this
+        System.gc();
+        String stackMax = SafePropertyAccessor.getProperty("jruby.stack.max");
+        String message = "";
+        if (stackMax != null) {
+            message = " of " + stackMax;
+        }
+        config.getError().println("Error: Your application used more stack memory than the safety cap" + message + ".");
+        config.getError().println("Specify -J-Xss####k to increase it (#### = cap size in KB).");
+        if (config.isVerbose()) {
+            config.getError().println("Exception trace follows:");
+            soe.printStackTrace();
+        } else {
+            config.getError().println("Specify -w for full StackOverflowError stack trace");
+        }
+        return new Status(1);
     }
 
-    public void printUsage() {
-        if (!hasPrintedUsage) {
-            config.getOutput().print(config.getBasicUsageHelp());
-            hasPrintedUsage = true;
+    private Status handleOutOfMemory(OutOfMemoryError oome) {
+        // produce a nicer error since Rubyists aren't used to seeing this
+        System.gc();
+        String memoryMax = SafePropertyAccessor.getProperty("jruby.memory.max");
+        String message = "";
+        if (memoryMax != null) {
+            message = " of " + memoryMax;
         }
+        config.getError().println("Error: Your application used more memory than the safety cap" + message + ".");
+        config.getError().println("Specify -J-Xmx####m to increase it (#### = cap size in MB).");
+        if (config.isVerbose()) {
+            config.getError().println("Exception trace follows:");
+            oome.printStackTrace();
+        } else {
+            config.getError().println("Specify -w for full OutOfMemoryError stack trace");
+        }
+        return new Status(1);
     }
-    
-    public void printProperties() {
-        config.getOutput().print(config.getPropertyHelp());
+
+    private Status handleMainExit(MainExitException mee) {
+        if (!mee.isAborted()) {
+            config.getOutput().println(mee.getMessage());
+            if (mee.isUsageError()) {
+                doPrintUsage(true);
+            }
+        }
+        return new Status(mee.getStatus());
     }
-    /**
-     * The intent here is to gather up any options that might have
-     * been specified in the shebang line and return them so they can
-     * be merged into the ones specified on the commandline.  This is
-     * kind of a hopeless task because it's impossible to figure out
-     * where the command invocation stops and the parameters start.
-     * We try to work with the common scenarios where /usr/bin/env is
-     * used to invoke the jruby shell script, and skip any parameters
-     * it might have.  Then we look for the interpreter invokation and
-     * assume that the binary will have the word "ruby" in the name.
-     * This is error prone but should cover more cases than the
-     * previous code.
-     */
-    private String[] parseShebangOptions(InputStream in) {
-        BufferedReader reader = null;
-        String[] result = new String[0];
-        if (in == null) return result;
+
+    private Status doRunFromMain(Ruby runtime, InputStream in, String filename) {
+        long now = -1;
         try {
-            in.mark(1024);
-            reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8192);
-            String firstLine = reader.readLine();
-
-            // Search for the shebang line in the given stream
-            // if it wasn't found on the first line and the -x option
-            // was specified
-            if (config.isxFlag()) {
-                while (firstLine != null && !isShebangLine(firstLine)) {
-                    firstLine = reader.readLine();
+            if (config.isBenchmarking()) {
+                now = System.currentTimeMillis();
+            }
+            if (config.isSamplingEnabled()) {
+                SimpleSampler.startSampleThread();
+            }
+            try {
+                runtime.runFromMain(in, filename);
+            } finally {
+                if (config.isBenchmarking()) {
+                    config.getOutput().println("Runtime: " + (System.currentTimeMillis() - now) + " ms");
+                }
+                if (config.isSamplingEnabled()) {
+                    org.jruby.util.SimpleSampler.report();
                 }
             }
+        } catch (RaiseException rj) {
+            return new Status(handleRaiseException(rj));
+        }
+        return new Status();
+    }
 
-            boolean usesEnv = false;
-            if (firstLine.length() > 2 && firstLine.charAt(0) == '#' && firstLine.charAt(1) == '!') {
-                String[] options = firstLine.substring(2).split("\\s+");
-                int i;
-                for (i = 0; i < options.length; i++) {
-                    // Skip /usr/bin/env if it's first
-                    if (i == 0 && options[i].endsWith("/env")) {
-                        usesEnv = true;
-                        continue;
-                    }
-                    // Skip any assignments if /usr/bin/env is in play
-                    if (usesEnv && options[i].indexOf('=') > 0) {
-                        continue;
-                    }
-                    // Skip any commandline args if /usr/bin/env is in play
-                    if (usesEnv && options[i].startsWith("-")) {
-                        continue;
-                    }
-                    String basename = (new File(options[i])).getName();
-                    if (basename.indexOf("ruby") > 0) {
-                        break;
+    private Status doCheckSyntax(Ruby runtime, InputStream in, String filename) throws RaiseException {
+        int status = 0;
+        try {
+            runtime.parseFromMain(in, filename);
+            config.getOutput().println("Syntax OK for " + filename);
+        } catch (RaiseException re) {
+            status = -1;
+            if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
+                config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
+            } else {
+                throw re;
+            }
+        }
+        if (config.getArgv().length > 0) {
+            for (String arg : config.getArgv()) {
+                File argFile = new File(arg);
+                if (argFile.exists()) {
+                    try {
+                        runtime.parseFromMain(new FileInputStream(argFile), arg);
+                        config.getOutput().println("Syntax OK for " + arg);
+                    } catch (FileNotFoundException fnfe) {
+                        status = -1;
+                        config.getOutput().println("File not found: " + arg);
+                    } catch (RaiseException re) {
+                        status = -1;
+                        if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
+                            config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
+                        } else {
+                            throw re;
+                        }
                     }
+                } else {
+                    status = -1;
+                    config.getOutput().println("File not found: " + arg);
                 }
-                config.setHasShebangLine(true);
-                System.arraycopy(options, i, result, 0, options.length - i);
-            } else {
-                // No shebang line found
-                config.setHasShebangLine(false);
             }
-        } catch (Exception ex) {
-            // ignore error
-        } finally {
-            try {
-                in.reset();
-            } catch (IOException ex) {}
         }
-        return result;
+        return new Status(status);
     }
 
-    public static class Status {
-        private boolean isExit = false;
-        private int status = 0;
+    private void doSetContextClassLoader(Ruby runtime) {
+        // set thread context JRuby classloader here, for the main thread
+        try {
+            Thread.currentThread().setContextClassLoader(runtime.getJRubyClassLoader());
+        } catch (SecurityException se) {
+            // can't set TC classloader
+            if (runtime.getInstanceConfig().isVerbose()) {
+                config.getError().println("WARNING: Security restrictions disallowed setting context classloader for main thread.");
+            }
+        }
+    }
 
-        /**
-         * Creates a status object with the specified value and with explicit
-         * exit flag. An exit flag means that Kernel.exit() has been explicitly
-         * invoked during the run.
-         *
-         * @param status
-         *            The status value.
-         */
-        Status(int status) {
-            this.isExit = true;
-            this.status = status;
+    private void doProcessArguments(InputStream in) {
+        String[] args = config.parseShebangOptions(in);
+        if (args.length > 0) {
+            config.processArguments(args);
         }
+    }
 
-        /**
-         * Creates a status object with 0 value and no explicit exit flag. 
-         */
-        Status() {}
+    private void doPrintProperties() {
+        if (config.shouldPrintProperties()) {
+            config.getOutput().print(config.getPropertyHelp());
+        }
+    }
 
-        public boolean isExit() { return isExit; }
-        public int getStatus() { return status; }
+    private void doPrintUsage(boolean force) {
+        if (config.shouldPrintUsage() || force) {
+            config.getOutput().print(config.getBasicUsageHelp());
+        }
     }
 
-    protected boolean isShebangLine(String line) {
-        return (line.length() > 2 && line.charAt(0) == '#' && line.charAt(1) == '!');
+    private void doShowCopyright() {
+        if (config.isShowCopyright()) {
+            config.getOutput().println(config.getCopyrightString());
+        }
     }
+
+    private void doShowVersion() {
+        if (config.isShowVersion()) {
+            config.getOutput().println(config.getVersionString());
+        }
+    }
+
+    private static void doGCJCheck() {
+        // Ensure we're not running on GCJ, since it's not supported and leads to weird errors
+        if (Platform.IS_GCJ) {
+            System.err.println("Fatal: GCJ (GNU Compiler for Java) is not supported by JRuby.");
+            System.exit(1);
+        }
+    }
+
+    private static int handleRaiseException(RaiseException rj) {
+        RubyException raisedException = rj.getException();
+        Ruby runtime = raisedException.getRuntime();
+        if (runtime.getSystemExit().isInstance(raisedException)) {
+            IRubyObject status = raisedException.callMethod(runtime.getCurrentContext(), "status");
+            if (status != null && !status.isNil()) {
+                return RubyNumeric.fix2int(status);
+            } else {
+                return 0;
+            }
+        } else {
+            System.err.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(raisedException));
+            return 1;
+        }
+    }
+
+    private final RubyInstanceConfig config;
 }
 
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 3c5a1bdbdf..7cba07513c 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,3680 +1,3512 @@
 /*
  **** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.util.func.Function1;
 import java.io.ByteArrayInputStream;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Set;
 import java.util.Stack;
 import java.util.Vector;
 import java.util.WeakHashMap;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.SynchronousQueue;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import org.jcodings.Encoding;
 import org.joda.time.DateTimeZone;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.common.RubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.JITCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
-import org.jruby.compiler.NotCompilableException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.JRubyPOSIXHandler;
 import org.jruby.ext.LateLoadingLibrary;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.POSIXFactory;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.management.ClassCache;
 import org.jruby.management.Config;
 import org.jruby.management.ParserStats;
 import org.jruby.parser.EvalStaticScope;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingService;
 import org.jruby.runtime.load.CompiledScriptLoader;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.profile.IProfileData;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KCode;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.io.ChannelDescriptor;
 
 import com.kenai.constantine.Constant;
 import com.kenai.constantine.ConstantSet;
 import com.kenai.constantine.platform.Errno;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.util.EnumSet;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.RubyInstanceConfig.CompileMode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.interpreter.Interpreter;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.management.BeanManager;
 import org.jruby.management.BeanManagerFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.threading.DaemonThreadFactory;
 import org.jruby.util.io.SelectorPool;
 
 /**
  * The Ruby object represents the top-level of a JRuby "instance" in a given VM.
  * JRuby supports spawning multiple instances in the same JVM. Generally, objects
  * created under these instances are tied to a given runtime, for such details
  * as identity and type, because multiple Ruby instances means there are
  * multiple instances of each class. This means that in multi-runtime mode
  * (or really, multi-VM mode, where each JRuby instance is a ruby "VM"), objects
  * generally can't be transported across runtimes without marshaling.
  * 
  * This class roots everything that makes the JRuby runtime function, and
  * provides a number of utility methods for constructing global types and
  * accessing global runtime structures.
  */
 public final class Ruby {
     /**
      * Returns a new instance of the JRuby runtime configured with defaults.
      *
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured as specified.
      *
      * @param config The instance configuration
      * @return The JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         setGlobalRuntimeFirstTimeOnly(ruby);
         return ruby;
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured with the given
      * input, output and error streams and otherwise default configuration
      * (except where specified system properties alter defaults).
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
 
     private static Ruby globalRuntime;
 
     /**
      * Tests whether globalRuntime has been instantiated or not.
      *
      * This method is used by singleton model of org.jruby.embed.ScriptingContainer
      * to decide what RubyInstanceConfig should be used. When a global runtime is
      * not there, RubyInstanceConfig of AbstractContextProvider will be used to enact
      * configurations set by a user. When a global runtime is already instantiated,
      * RubyInstanceConfig of the global runtime should be used in ScriptingContaiener.
      *
      * @return true if a global runtime is instantiated, false for other.
      * 
      */
     public static boolean isGlobalRuntimeReady() {
         return globalRuntime != null;
     }
 
     private static synchronized void setGlobalRuntimeFirstTimeOnly(Ruby runtime) {
         if (globalRuntime == null) {
             globalRuntime = runtime;
         }
     }
 
     public static synchronized Ruby getGlobalRuntime() {
         if (globalRuntime == null) {
             newInstance();
         }
         return globalRuntime;
     }
     
     /**
      * Convenience method for java integrators who may need to switch the notion 
      * of "global" runtime. Use <tt>JRuby.runtime.use_as_global_runtime</tt>
      * from Ruby code to activate the current runtime as the global one.
      */
     public void useAsGlobalRuntime() {
         synchronized(Ruby.class) {
             globalRuntime = null;
             setGlobalRuntimeFirstTimeOnly(this);
         }
     }
 
     /**
      * Create and initialize a new JRuby runtime. The properties of the
      * specified RubyInstanceConfig will be used to determine various JRuby
      * runtime characteristics.
      * 
      * @param config The configuration to use for the new instance
      * @see org.jruby.RubyInstanceConfig
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.is1_9              = config.getCompatVersion() == CompatVersion.RUBY1_9;
         this.doNotReverseLookupEnabled = is1_9;
         this.threadService      = new ThreadService(this);
         if(config.isSamplingEnabled()) {
             org.jruby.util.SimpleSampler.registerThreadContext(threadService.getCurrentContext());
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();
         this.kcode              = config.getKCode();
         this.beanManager        = BeanManagerFactory.create(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
 
         this.runtimeCache = new RuntimeCache();
         runtimeCache.initMethodCache(ClassIndex.MAX_CLASSES * MethodIndex.MAX_METHODS);
     }
     
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      * 
      * @param script The scriptlet to run
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script) {
         ThreadContext context = getCurrentContext();
         DynamicScope currentScope = context.getCurrentScope();
         ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);
 
         return evalScriptlet(script, newScope);
     }
 
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      *
      * This version accepts a scope to use, so you can eval many times against
      * the same scope.
      *
      * @param script The scriptlet to run
      * @param scope The scope to execute against (ManyVarsDynamicScope is
      * recommended, so it can grow as needed)
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script, DynamicScope scope) {
         ThreadContext context = getCurrentContext();
         Node node = parseEval(script, "<script>", scope, 0);
 
         try {
             context.preEvalScriptlet(scope);
             return ASTInterpreter.INTERPRET_ROOT(this, context, node, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.RETURN, (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalScriptlet();
         }
     }
     
     /**
      * Parse and execute the specified script 
      * This differs from the other methods in that it accepts a string-based script and
      * parses and runs it as though it were loaded at a command-line. This is the preferred
      * way to start up a new script when calling directly into the Ruby object (which is
      * generally *dis*couraged.
      * 
      * @param script The contents of the script to run as a normal, root script
      * @return The last value of the script
      */
     public IRubyObject executeScript(String script, String filename) {
         byte[] bytes = script.getBytes();
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(node.getPosition());
             return runInterpreter(node);
         } finally {
             context.setFileAndLine(oldFile, oldLine);
         }
     }
     
     /**
      * Run the script contained in the specified input stream, using the
      * specified filename as the name of the script being executed. The stream
      * will be read fully before being parsed and executed. The given filename
      * will be used for the ruby $PROGRAM_NAME and $0 global variables in this
      * runtime.
      * 
      * This method is intended to be called once per runtime, generally from
      * Main or from main-like top-level entry points.
      * 
      * As part of executing the script loaded from the input stream, various
      * RubyInstanceConfig properties will be used to determine whether to
      * compile the script before execution or run with various wrappers (for
      * looping, printing, and so on, see jruby -help).
      * 
      * @param inputStream The InputStream from which to read the script contents
      * @param filename The filename to use when parsing, and for $PROGRAM_NAME
      * and $0 ruby global variables.
      */
     public void runFromMain(InputStream inputStream, String filename) {
         IAccessor d = new ValueAccessor(newString(filename));
         getGlobalVariables().define("$PROGRAM_NAME", d);
         getGlobalVariables().define("$0", d);
 
         for (Iterator i = config.getOptionGlobals().entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             Object value = entry.getValue();
             IRubyObject varvalue;
             if (value != null) {
                 varvalue = newString(value.toString());
             } else {
                 varvalue = getTrue();
             }
             getGlobalVariables().set("$" + entry.getKey().toString(), varvalue);
         }
 
         if (filename.endsWith(".class")) {
             // we are presumably running a precompiled class; load directly
             Script script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
             if (script == null) {
                 throw new MainExitException(1, "error: .class file specified is not a compiled JRuby script");
             }
             script.setFilename(filename);
             runScript(script);
             return;
         }
         
         Node scriptNode = parseFromMain(inputStream, filename);
 
         // done with the stream, shut it down
         try {inputStream.close();} catch (IOException ioe) {}
 
         ThreadContext context = getCurrentContext();
 
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit());
             } else {
                 runNormally(scriptNode);
             }
         } finally {
             context.setFileAndLine(oldFile, oldLine);
             if (config.isProfilingEntireRun()) {
                 IProfileData profileData = (IProfileData) context.getProfileData();
                 config.makeDefaultProfilePrinter(profileData).printProfile(System.out);
             }
         }
     }
 
     /**
      * Parse the script contained in the given input stream, using the given
      * filename as the name of the script, and return the root Node. This
      * is used to verify that the script syntax is valid, for jruby -c. The
      * current scope (generally the top-level scope) is used as the parent
      * scope for parsing.
      * 
      * @param inputStream The input stream from which to read the script
      * @param filename The filename to use for parsing
      * @returns The root node of the parsed script
      */
     public Node parseFromMain(InputStream inputStream, String filename) {
         if (config.isInlineScript()) {
             return parseInline(inputStream, filename, getCurrentContext().getCurrentScope());
         } else {
             return parseFile(inputStream, filename, getCurrentContext().getCurrentScope());
         }
     }
 
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      *
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     @Deprecated
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean unused) {
         return runWithGetsLoop(scriptNode, printing, processLineEnds, split);
     }
     
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      * 
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
 
         // we do preand post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         RuntimeHelpers.preLoad(context, ((RootNode)scriptNode).getStaticScope().getVariables());
 
         try {
             while (RubyKernel.gets(context, getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                 loop: while (true) { // Used for the 'redo' command
                     try {
                         if (processLineEnds) {
                             getGlobalVariables().get("$_").callMethod(context, "chop!");
                         }
 
                         if (split) {
                             getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                         }
 
                         if (script != null) {
                             runScriptBody(script);
                         } else {
                             runInterpreterBody(scriptNode);
                         }
 
                         if (printing) RubyKernel.print(context, getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
                         break loop;
                     } catch (JumpException.RedoJump rj) {
                         // do nothing, this iteration restarts
                     } catch (JumpException.NextJump nj) {
                         // recheck condition
                         break loop;
                     } catch (JumpException.BreakJump bj) {
                         // end loop
                         return (IRubyObject) bj.getValue();
                     }
                 }
             }
         } finally {
             RuntimeHelpers.postLoad(context);
         }
         
         return getNil();
     }
 
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      *
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     @Deprecated
     public IRubyObject runNormally(Node scriptNode, boolean unused) {
         return runNormally(scriptNode);
     }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode) {
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
-        boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
-        if (compile) {
+        if (compile || config.isShowBytecode()) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
-            if (forceCompile && script == null) {
-                return getNil();
-            }
         }
 
-        if (config.isShowBytecode()) {
-            if (script == null) {
-                // try to force compile the script again, in case we are in a -X-C or dynopt mode
-                script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
-            }
-            // if still null, print error and return
-            if (script == null) {
-                System.err.print("error: bytecode printing only works with JVM bytecode");
+        if (script != null) {
+            if (config.isShowBytecode()) {
+                return getNil();
             }
-            return nilObject;
+            
+            return runScript(script);
         } else {
-            if (script != null) {
-                return runScript(script);
-            } else {
-                return runInterpreter(scriptNode);
-            }
+            failForcedCompile(scriptNode);
+            
+            return runInterpreter(scriptNode);
         }
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled.
      *
      * @param node The node to attempt to compiled
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), false);
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled. This version accepts an ASTInspector instance assumed to
      * have appropriate flags set for compile optimizations, such as to turn
      * on heap-based local variables to share an existing scope.
      *
      * @param node The node to attempt to compiled
      * @param inspector The ASTInspector to use for making optimization decisions
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node, ASTInspector inspector) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), inspector, false);
     }
 
+    private void failForcedCompile(Node scriptNode) throws RaiseException {
+        if (config.getCompileMode().shouldPrecompileAll()) {
+            throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getPosition().getFile());
+        }
+    }
+
+    private void handeCompileError(Node node, Throwable t) {
+        if (config.isJitLoggingVerbose() || config.isDebug()) {
+            System.err.println("warning: could not compile: " + node.getPosition().getFile() + "; full trace follows");
+            t.printStackTrace();
+        }
+    }
+
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
 
         return tryCompile(node, cachedClassName, classLoader, inspector, dump);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, ASTInspector inspector, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             StandardASMCompiler asmCompiler = null;
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 asmCompiler = new StandardASMCompiler(cachedClassName.replace('.', '/'), filename);
             } else {
                 asmCompiler = new StandardASMCompiler(classname, filename);
             }
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
 
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 // save script off to disk
                 String pathName = cachedClassName.replace('.', '/');
                 JITCompiler.saveToCodeCache(this, asmCompiler.getClassByteArray(), "ruby/jit", new File(RubyInstanceConfig.JIT_CODE_CACHE, pathName + ".class"));
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
-        } catch (NotCompilableException nce) {
-            if (config.isJitLoggingVerbose() || config.isDebug()) {
-                System.err.println("warning: not compileable: " + nce.getMessage());
-                nce.printStackTrace();
-            } else {
-                System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
-            }
-        } catch (ClassNotFoundException e) {
-            if (config.isJitLoggingVerbose() || config.isDebug()) {
-                System.err.println("warning: not compileable: " + e.getMessage());
-                e.printStackTrace();
-            } else {
-                System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
-            }
-        } catch (InstantiationException e) {
-            if (config.isJitLoggingVerbose() || config.isDebug()) {
-                System.err.println("warning: not compilable: " + e.getMessage());
-                e.printStackTrace();
-            } else {
-                System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
-            }
-        } catch (IllegalAccessException e) {
-            if (config.isJitLoggingVerbose() || config.isDebug()) {
-                System.err.println("warning: not compilable: " + e.getMessage());
-                e.printStackTrace();
-            } else {
-                System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
-            }
         } catch (Throwable t) {
-            if (config.isJitLoggingVerbose() || config.isDebug()) {
-                System.err.println("warning: could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
-                t.printStackTrace();
-            } else {
-                System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
-            }
+            handeCompileError(node, t);
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         try {
             if (getInstanceConfig().getCompileMode() == CompileMode.OFFIR) {
                 return Interpreter.interpret(this, rootNode, self);
             } else {
                 return ASTInterpreter.INTERPRET_ROOT(this, context, rootNode, getTopSelf(), Block.NULL_BLOCK);
             }
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(((RootNode) scriptNode).getBodyNode());
     }
 
     public Parser getParser() {
         return parser;
     }
     
     public BeanManager getBeanManager() {
         return beanManager;
     }
     
     public JITCompiler getJITCompiler() {
         return jitCompiler;
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
     
     @Deprecated
     public static Ruby getCurrentInstance() {
         return null;
     }
     
     @Deprecated
     public static void setCurrentInstance(Ruby runtime) {
     }
     
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
     public void addModule(RubyModule module) {
         synchronized (allModules) {
             allModules.add(module);
         }
     }
     public void eachModule(Function1<Object, IRubyObject> func) {
         synchronized (allModules) {
             for (RubyModule module : allModules) {
                 func.apply(module);
             }
         }
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace.
      * 
      * @param name The name of the module
      * @return The module or null if not found
      */
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName The name of the module; <em>must</em> be an interned String
      * @return The module or null if not found
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** 
      * Retrieve the class with the given name from the Object namespace.
      *
      * @param name The name of the class
      * @return The class
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * Retrieve the class with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** 
      * Define a new class under the Object namespace. Roughly equivalent to
      * rb_define_class in MRI.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass);
     }
 
     /** 
      * A variation of defineClass that allows passing in an array of subplementary
      * call sites for improving dynamic invocation performance.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator, CallSite[] callSites) {
         return defineClassUnder(name, superClass, allocator, objectClass, callSites);
     }
 
     /**
      * Define a new class with the given name under the given module or class
      * namespace. Roughly equivalent to rb_define_class_under in MRI.
      * 
      * If the name specified is already bound, its value will be returned if:
      * * It is a class
      * * No new superclass is being defined
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         return defineClassUnder(name, superClass, allocator, parent, null);
     }
 
     /**
      * A variation of defineClassUnder that allows passing in an array of
      * supplementary call sites to improve dynamic invocation.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @param callSites The array of call sites to add
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent, CallSite[] callSites) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) {
                 throw newNameError(name + " is already defined", name);
             }
             // If we define a class in Ruby, but later want to allow it to be defined in Java,
             // the allocator needs to be updated
             if (klazz.getAllocator() != allocator) {
                 klazz.setAllocator(allocator);
             }
             return klazz;
         }
         
         boolean parentIsObject = parent == objectClass;
 
         if (superClass == null) {
             String className = parentIsObject ? name : parent.getName() + "::" + name;  
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed", className);
             
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, !parentIsObject, callSites);
     }
 
     /** 
      * Define a new module under the Object namespace. Roughly equivalent to
      * rb_define_module in MRI.
      * 
      * @param name The name of the new module
      * @returns The new module
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass);
     }
 
     /**
      * Define a new module with the given name under the given module or
      * class namespace. Roughly equivalent to rb_define_module_under in MRI.
      * 
      * @param name The name of the new module
      * @param parent The class or module namespace under which to define the
      * module
      * @returns The new module
      */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
         
         boolean parentIsObject = parent == objectClass;
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             
             if (parentIsObject) {
                 throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
             } else {
                 throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
             }
         }
 
         return RubyModule.newModule(this, name, parent, !parentIsObject);
     }
 
     /**
      * From Object, retrieve the named module. If it doesn't exist a
      * new module is created.
      * 
      * @param name The name of the module
      * @returns The existing or new module
      */
     public RubyModule getOrCreateModule(String name) {
         IRubyObject module = objectClass.getConstantAt(name);
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!module.isModule()) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
     }
 
 
     /** 
      * Retrieve the current safe level.
      * 
      * @see org.jruby.Ruby#setSaveLevel
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
 
     /** 
      * Set the current safe level:
      * 
      * 0 - strings from streams/environment/ARGV are tainted (default)
      * 1 - no dangerous operation by tainted value
      * 2 - process/file operations prohibited
      * 3 - all generated objects are tainted
      * 4 - no global (non-tainted) variable modification/no direct output
      * 
      * The safe level is set using $SAFE in Ruby code. It is not particularly
      * well supported in JRuby.
     */
     public void setSafeLevel(int safeLevel) {
         this.safeLevel = safeLevel;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public void secure(int level) {
         if (level <= safeLevel) {
             throw newSecurityError("Insecure operation '" + getCurrentContext().getFrameName() + "' at level " + safeLevel);
         }
     }
 
     // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
     // perhaps security methods should find their own centralized home at some point.
     public void checkSafeString(IRubyObject object) {
         if (getSafeLevel() > 0 && object.isTaint()) {
             ThreadContext tc = getCurrentContext();
             if (tc.getFrameName() != null) {
                 throw newSecurityError("Insecure operation - " + tc.getFrameName());
             }
             throw newSecurityError("Insecure operation: -r");
         }
         secure(4);
         if (!(object instanceof RubyString)) {
             throw newTypeError(
                 "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** 
      * This method is called immediately after constructing the Ruby instance.
      * The main thread is prepared for execution, all core classes and libraries
      * are initialized, and any libraries required on the command line are
      * loaded.
      */
     private void init() {
         safeLevel = config.getSafeLevel();
         
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         javaSupport = new JavaSupport(this);
         
         if (RubyInstanceConfig.POOLING_ENABLED) {
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot();
 
         // Set up the main thread in thread service
         threadService.initMainThread();
 
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.loadPaths());
 
         // initialize builtin libraries
         initBuiltins();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.smartLoad(scriptName);
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initRoot() {
         boolean oneNine = is1_9();
         // Bootstrap the top of the hierarchy
         if (oneNine) {
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
             objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         } else {
             objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         }
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         if (oneNine) basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         if (oneNine) metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         if (oneNine) RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
         
         // set constants now that they're initialized
         if (oneNine) objectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
         
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
     }
 
     private void initEncodings() {
         RubyEncoding.createEncodingClass(this);
         RubyConverter.createConverterClass(this);
         encodingService = new EncodingService(this);
 
         // External should always have a value, but Encoding.external_encoding{,=} will lazily setup
         String encoding = config.getExternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultExternalEncoding(loadedEncoding);
         } else {
             setDefaultExternalEncoding(USASCIIEncoding.INSTANCE);
         }
         
         encoding = config.getInternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultInternalEncoding(loadedEncoding);
         }
     }
 
     private void initCore() {
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             initEncodings();
             RubyRandom.createRandomClass(this);
         }
 
         RubySymbol.createSymbolClass(this);
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (!is1_9()) {
             if (profile.allowModule("Precision")) {
                 RubyPrecision.createPrecisionModule(this);
             }
         }
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         if (is1_9()) {
             if (profile.allowClass("Complex")) {
                 RubyComplex.createComplexClass(this);
             }
             if (profile.allowClass("Rational")) {
                 RubyRational.createRationalClass(this);
             }
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
         }
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass, new IRubyObject[]{newString("Tms"), newSymbol("utime"), newSymbol("stime"), newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) {
             RubyBinding.createBindingClass(this);
         }
         // Math depends on all numeric types
         if (profile.allowModule("Math")) {
             RubyMath.createMathModule(this);
         }
         if (profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if (profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if (profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if (profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
         if (profile.allowClass("Proc")) {
             RubyProc.createProcClass(this);
         }
         if (profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
         if (profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if (profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
         if (profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
         if (profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
         // depends on IO, FileTest
         if (profile.allowClass("File")) {
             RubyFile.createFileClass(this);
         }
         if (profile.allowClass("File::Stat")) {
             RubyFileStat.createFileStatClass(this);
         }
         if (profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if (profile.allowClass("Time")) {
             RubyTime.createTimeClass(this);
         }
         if (profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
         if (profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     public static final int NIL_PREFILLED_ARRAY_SIZE = RubyArray.ARRAY_DEFAULT_SIZE * 8;
     private final IRubyObject nilPrefilledArray[] = new IRubyObject[NIL_PREFILLED_ARRAY_SIZE];
     public IRubyObject[] getNilPrefilledArray() {
         return nilPrefilledArray;
     }
 
     private void initExceptions() {
         standardError = defineClassIfAllowed("StandardError", exceptionClass);
         runtimeError = defineClassIfAllowed("RuntimeError", standardError);
         ioError = defineClassIfAllowed("IOError", standardError);
         scriptError = defineClassIfAllowed("ScriptError", exceptionClass);
         rangeError = defineClassIfAllowed("RangeError", standardError);
         signalException = defineClassIfAllowed("SignalException", exceptionClass);
         
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
             nameErrorMessage = RubyNameError.createNameErrorMessageClass(this, nameError);            
         }
         if (profile.allowClass("NoMethodError")) {
             noMethodError = RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }
         if (profile.allowClass("SystemExit")) {
             systemExit = RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("LocalJumpError")) {
             localJumpError = RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("NativeException")) {
             nativeException = NativeException.createClass(this, runtimeError);
         }
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
 
         fatal = defineClassIfAllowed("Fatal", exceptionClass);
         interrupt = defineClassIfAllowed("Interrupt", signalException);
         typeError = defineClassIfAllowed("TypeError", standardError);
         argumentError = defineClassIfAllowed("ArgumentError", standardError);
         indexError = defineClassIfAllowed("IndexError", standardError);
         stopIteration = defineClassIfAllowed("StopIteration", indexError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator());
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
                 invalidByteSequenceError = defineClassUnder("InvalidByteSequenceError", encodingError, encodingError.getAllocator(), encodingClass);
                 undefinedConversionError = defineClassUnder("UndefinedConversionError", encodingError, encodingError.getAllocator(), encodingClass);
                 converterNotFoundError = defineClassUnder("ConverterNotFoundError", encodingError, encodingError.getAllocator(), encodingClass);
                 fiberError = defineClass("FiberError", standardError, standardError.getAllocator());
             }
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             recursiveKey = newSymbol("__recursive_key__");
         }
 
         initErrno();
     }
     
     private RubyClass defineClassIfAllowed(String name, RubyClass superClass) {
 	// TODO: should probably apply the null object pattern for a
 	// non-allowed class, rather than null
         if (superClass != null && profile.allowClass(name)) {
             return defineClass(name, superClass, superClass.getAllocator());
         }
         return null;
     }
 
     private Map<Integer, RubyClass> errnos = new HashMap<Integer, RubyClass>();
 
     public RubyClass getErrno(int n) {
         return errnos.get(n);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrno() {
         if (profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
             try {
                 // define EAGAIN now, so that future EWOULDBLOCK will alias to it
                 // see MRI's error.c and its explicit ordering of Errno definitions.
                 createSysErr(Errno.EAGAIN.value(), Errno.EAGAIN.name());
                 
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.value(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 e.printStackTrace();
             }
         }
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             if (errnos.get(i) == null) {
                 RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
                 errnos.put(i, errno);
                 errno.defineConstant("Errno", newFixnum(i));
             } else {
                 // already defined a class for this errno, reuse it (JRUBY-4747)
                 getErrno().setConstant(name, errnos.get(i));
             }
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.ext.jruby.JRubyExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.ext.jruby.JRubyCoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("jruby/synchronized.rb", "jruby/synchronized", "org.jruby.ext.jruby.JRubySynchronizedLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.libraries.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             LoadService.reflectedLoad(this, "fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader(), false);
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
         String[] builtins = {"yaml", 
                              "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
                              "yaml/compat", "yaml/constants", "yaml/dbm", 
                              "yaml/emitter", "yaml/encoding", "yaml/error", 
                              "yaml/rubytypes", "yaml/store", "yaml/stream", 
                              "yaml/stringio", "yaml/tag", "yaml/types", 
                              "yaml/yamlnode", "yaml/ypath", 
                              "jsignal_internal", "generator_internal"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             loadFile("builtin/prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/prelude.rb"), false);
             if (!config.isDisableGems()) {
                 // NOTE: This has been disabled because gem_prelude is terribly broken.
                 //       We just require 'rubygems' in gem_prelude, at least for now.
                 //defineModule("Gem"); // dummy Gem module for prelude
                 loadFile("builtin/gem_prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/gem_prelude.rb"), false);
             }
         }
 
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     /** Getter for property rubyTopSelf.
      * @return Value of property rubyTopSelf.
      */
     public IRubyObject getTopSelf() {
         return topSelf;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setCurrentLine(int line) {
         currentLine = line;
     }
 
     public int getCurrentLine() {
         return currentLine;
     }
 
     public void setArgsFile(IRubyObject argsFile) {
         this.argsFile = argsFile;
     }
 
     public IRubyObject getArgsFile() {
         return argsFile;
     }
     
     public RubyModule getEtc() {
         return etcModule;
     }
     
     public void setEtc(RubyModule etcModule) {
         this.etcModule = etcModule;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyClass getBasicObject() {
         return basicObjectClass;
     }
 
     public RubyClass getModule() {
         return moduleClass;
     }
 
     public RubyClass getClassClass() {
         return classClass;
     }
     
     public RubyModule getKernel() {
         return kernelModule;
     }
     void setKernel(RubyModule kernelModule) {
         this.kernelModule = kernelModule;
     }
 
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
     }
     
     public RubyClass getDummy() {
         return dummyClass;
     }
 
     public RubyModule getComparable() {
         return comparableModule;
     }
     void setComparable(RubyModule comparableModule) {
         this.comparableModule = comparableModule;
     }    
 
     public RubyClass getNumeric() {
         return numericClass;
     }
     void setNumeric(RubyClass numericClass) {
         this.numericClass = numericClass;
     }    
 
     public RubyClass getFloat() {
         return floatClass;
     }
     void setFloat(RubyClass floatClass) {
         this.floatClass = floatClass;
     }
     
     public RubyClass getInteger() {
         return integerClass;
     }
     void setInteger(RubyClass integerClass) {
         this.integerClass = integerClass;
     }    
     
     public RubyClass getFixnum() {
         return fixnumClass;
     }
     void setFixnum(RubyClass fixnumClass) {
         this.fixnumClass = fixnumClass;
     }
 
     public RubyClass getComplex() {
         return complexClass;
     }
     void setComplex(RubyClass complexClass) {
         this.complexClass = complexClass;
     }
 
     public RubyClass getRational() {
         return rationalClass;
     }
     void setRational(RubyClass rationalClass) {
         this.rationalClass = rationalClass;
     }
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }
 
     public RubyClass getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
     }
 
     public RubyClass getYielder() {
         return yielderClass;
     }
     void setYielder(RubyClass yielderClass) {
         this.yielderClass = yielderClass;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }
 
     public RubyClass getEncoding() {
         return encodingClass;
     }
     void setEncoding(RubyClass encodingClass) {
         this.encodingClass = encodingClass;
     }
 
     public RubyClass getConverter() {
         return converterClass;
     }
     void setConverter(RubyClass converterClass) {
         this.converterClass = converterClass;
     }
 
     public RubyClass getSymbol() {
         return symbolClass;
     }
     void setSymbol(RubyClass symbolClass) {
         this.symbolClass = symbolClass;
     }
 
     public RubyClass getArray() {
         return arrayClass;
     }    
     void setArray(RubyClass arrayClass) {
         this.arrayClass = arrayClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }
     void setHash(RubyClass hashClass) {
         this.hashClass = hashClass;
     }
 
     public RubyClass getRange() {
         return rangeClass;
     }
     void setRange(RubyClass rangeClass) {
         this.rangeClass = rangeClass;
     }
 
     /** Returns the "true" instance from the instance pool.
      * @return The "true" instance.
      */
     public RubyBoolean getTrue() {
         return trueObject;
     }
 
     /** Returns the "false" instance from the instance pool.
      * @return The "false" instance.
      */
     public RubyBoolean getFalse() {
         return falseObject;
     }
 
     /** Returns the "nil" singleton instance.
      * @return "nil"
      */
     public IRubyObject getNil() {
         return nilObject;
     }
 
     public IRubyObject[] getSingleNilArray() {
         return singleNilArray;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
     void setNilClass(RubyClass nilClass) {
         this.nilClass = nilClass;
     }
 
     public RubyClass getTrueClass() {
         return trueClass;
     }
     void setTrueClass(RubyClass trueClass) {
         this.trueClass = trueClass;
     }
 
     public RubyClass getFalseClass() {
         return falseClass;
     }
     void setFalseClass(RubyClass falseClass) {
         this.falseClass = falseClass;
     }
 
     public RubyClass getProc() {
         return procClass;
     }
     void setProc(RubyClass procClass) {
         this.procClass = procClass;
     }
 
     public RubyClass getBinding() {
         return bindingClass;
     }
     void setBinding(RubyClass bindingClass) {
         this.bindingClass = bindingClass;
     }
 
     public RubyClass getMethod() {
         return methodClass;
     }
     void setMethod(RubyClass methodClass) {
         this.methodClass = methodClass;
     }    
 
     public RubyClass getUnboundMethod() {
         return unboundMethodClass;
     }
     void setUnboundMethod(RubyClass unboundMethodClass) {
         this.unboundMethodClass = unboundMethodClass;
     }    
 
     public RubyClass getMatchData() {
         return matchDataClass;
     }
     void setMatchData(RubyClass matchDataClass) {
         this.matchDataClass = matchDataClass;
     }    
 
     public RubyClass getRegexp() {
         return regexpClass;
     }
     void setRegexp(RubyClass regexpClass) {
         this.regexpClass = regexpClass;
     }    
 
     public RubyClass getTime() {
         return timeClass;
     }
     void setTime(RubyClass timeClass) {
         this.timeClass = timeClass;
     }    
 
     public RubyModule getMath() {
         return mathModule;
     }
     void setMath(RubyModule mathModule) {
         this.mathModule = mathModule;
     }    
 
     public RubyModule getMarshal() {
         return marshalModule;
     }
     void setMarshal(RubyModule marshalModule) {
         this.marshalModule = marshalModule;
     }    
 
     public RubyClass getBignum() {
         return bignumClass;
     }
     void setBignum(RubyClass bignumClass) {
         this.bignumClass = bignumClass;
     }    
 
     public RubyClass getDir() {
         return dirClass;
     }
     void setDir(RubyClass dirClass) {
         this.dirClass = dirClass;
     }    
 
     public RubyClass getFile() {
         return fileClass;
     }
     void setFile(RubyClass fileClass) {
         this.fileClass = fileClass;
     }    
 
     public RubyClass getFileStat() {
         return fileStatClass;
     }
     void setFileStat(RubyClass fileStatClass) {
         this.fileStatClass = fileStatClass;
     }    
 
     public RubyModule getFileTest() {
         return fileTestModule;
     }
     void setFileTest(RubyModule fileTestModule) {
         this.fileTestModule = fileTestModule;
     }
     
     public RubyClass getIO() {
         return ioClass;
     }
     void setIO(RubyClass ioClass) {
         this.ioClass = ioClass;
     }    
 
     public RubyClass getThread() {
         return threadClass;
     }
     void setThread(RubyClass threadClass) {
         this.threadClass = threadClass;
     }    
 
     public RubyClass getThreadGroup() {
         return threadGroupClass;
     }
     void setThreadGroup(RubyClass threadGroupClass) {
         this.threadGroupClass = threadGroupClass;
     }
     
     public RubyThreadGroup getDefaultThreadGroup() {
         return defaultThreadGroup;
     }
     void setDefaultThreadGroup(RubyThreadGroup defaultThreadGroup) {
         this.defaultThreadGroup = defaultThreadGroup;
     }
 
     public RubyClass getContinuation() {
         return continuationClass;
     }
     void setContinuation(RubyClass continuationClass) {
         this.continuationClass = continuationClass;
     }    
 
     public RubyClass getStructClass() {
         return structClass;
     }
     void setStructClass(RubyClass structClass) {
         this.structClass = structClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
     void setTmsStruct(RubyClass tmsStruct) {
         this.tmsStruct = tmsStruct;
     }
     
     public IRubyObject getPasswdStruct() {
         return passwdStruct;
     }
     void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     void setGroupStruct(RubyClass groupStruct) {
         this.groupStruct = groupStruct;
     }
 
     public RubyModule getGC() {
         return gcModule;
     }
     void setGC(RubyModule gcModule) {
         this.gcModule = gcModule;
     }    
 
     public RubyModule getObjectSpaceModule() {
         return objectSpaceModule;
     }
     void setObjectSpaceModule(RubyModule objectSpaceModule) {
         this.objectSpaceModule = objectSpaceModule;
     }    
 
     public RubyModule getProcess() {
         return processModule;
     }
     void setProcess(RubyModule processModule) {
         this.processModule = processModule;
     }    
 
     public RubyClass getProcStatus() {
         return procStatusClass; 
     }
     void setProcStatus(RubyClass procStatusClass) {
         this.procStatusClass = procStatusClass;
     }
     
     public RubyModule getProcUID() {
         return procUIDModule;
     }
     void setProcUID(RubyModule procUIDModule) {
         this.procUIDModule = procUIDModule;
     }
     
     public RubyModule getProcGID() {
         return procGIDModule;
     }
     void setProcGID(RubyModule procGIDModule) {
         this.procGIDModule = procGIDModule;
     }
     
     public RubyModule getProcSysModule() {
         return procSysModule;
     }
     void setProcSys(RubyModule procSysModule) {
         this.procSysModule = procSysModule;
     }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }
 
     public RubyModule getErrno() {
         return errnoModule;
     }
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
 
     public RubyClass getNameError() {
         return nameError;
     }
 
     public RubyClass getNameErrorMessage() {
         return nameErrorMessage;
     }
 
     public RubyClass getNoMethodError() {
         return noMethodError;
     }
 
     public RubyClass getSignalException() {
         return signalException;
     }
 
     public RubyClass getRangeError() {
         return rangeError;
     }
 
     public RubyClass getSystemExit() {
         return systemExit;
     }
 
     public RubyClass getLocalJumpError() {
         return localJumpError;
     }
 
     public RubyClass getNativeException() {
         return nativeException;
     }
 
     public RubyClass getSystemCallError() {
         return systemCallError;
     }
 
     public RubyClass getFatal() {
         return fatal;
     }
     
     public RubyClass getInterrupt() {
         return interrupt;
     }
     
     public RubyClass getTypeError() {
         return typeError;
     }
 
     public RubyClass getArgumentError() {
         return argumentError;
     }
 
     public RubyClass getIndexError() {
         return indexError;
     }
 
     public RubyClass getStopIteration() {
         return stopIteration;
     }
 
     public RubyClass getSyntaxError() {
         return syntaxError;
     }
 
     public RubyClass getStandardError() {
         return standardError;
     }
     
     public RubyClass getRuntimeError() {
         return runtimeError;
     }
     
     public RubyClass getIOError() {
         return ioError;
     }
 
     public RubyClass getLoadError() {
         return loadError;
     }
 
     public RubyClass getNotImplementedError() {
         return notImplementedError;
     }
 
     public RubyClass getSecurityError() {
         return securityError;
     }
 
     public RubyClass getNoMemoryError() {
         return noMemoryError;
     }
 
     public RubyClass getRegexpError() {
         return regexpError;
     }
 
     public RubyClass getEOFError() {
         return eofError;
     }
 
     public RubyClass getThreadError() {
         return threadError;
     }
 
     public RubyClass getConcurrencyError() {
         return concurrencyError;
     }
 
     public RubyClass getSystemStackError() {
         return systemStackError;
     }
 
     public RubyClass getZeroDivisionError() {
         return zeroDivisionError;
     }
 
     public RubyClass getFloatDomainError() {
         return floatDomainError;
     }
 
     public RubyClass getMathDomainError() {
         return mathDomainError;
     }
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
     }
 
     public RubyClass getFiberError() {
         return fiberError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     public RubyClass getRandomClass() {
         return randomClass;
     }
 
     public void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
     }
 
     private RubyHash charsetMap;
     public RubyHash getCharsetMap() {
         if (charsetMap == null) charsetMap = new RubyHash(this);
         return charsetMap;
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verboseValue;
     }
 
     public boolean isVerbose() {
         return verbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose.isTrue();
         this.verboseValue = verbose;
         warningsEnabled = !verbose.isNil();
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug ? trueObject : falseObject;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug.isTrue();
     }
 
     public JavaSupport getJavaSupport() {
         return javaSupport;
     }
 
     public static ClassLoader getClassLoader() {
         // we try to get the classloader that loaded JRuby, falling back on System
         ClassLoader loader = Ruby.class.getClassLoader();
         if (loader == null) {
             loader = ClassLoader.getSystemClassLoader();
         }
         
         return loader;
     }
 
     public synchronized JRubyClassLoader getJRubyClassLoader() {
         // FIXME: Get rid of laziness and handle restricted access elsewhere
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null) {
             jrubyClassLoader = new JRubyClassLoader(config.getLoader());
         }
         
         return jrubyClassLoader;
     }
 
     /** Defines a global variable
      */
     public void defineVariable(final GlobalVariable variable) {
         globalVariables.define(variable.name(), new IAccessor() {
             public IRubyObject getValue() {
                 return variable.get();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 return variable.set(newValue);
             }
         });
     }
 
     /** defines a readonly global variable
      *
      */
     public void defineReadonlyVariable(String name, IRubyObject value) {
         globalVariables.defineReadonly(name, new ValueAccessor(value));
     }
 
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
 
 
     public ThreadService getThreadService() {
         return threadService;
     }
 
     public ThreadContext getCurrentContext() {
         return threadService.getCurrentContext();
     }
 
     /**
      * Returns the loadService.
      * @return ILoadService
      */
     public LoadService getLoadService() {
         return loadService;
     }
 
     public Encoding getDefaultInternalEncoding() {
         return defaultInternalEncoding;
     }
 
     public void setDefaultInternalEncoding(Encoding defaultInternalEncoding) {
         this.defaultInternalEncoding = defaultInternalEncoding;
     }
 
     public Encoding getDefaultExternalEncoding() {
         return defaultExternalEncoding;
     }
 
     public void setDefaultExternalEncoding(Encoding defaultExternalEncoding) {
         this.defaultExternalEncoding = defaultExternalEncoding;
     }
 
     public EncodingService getEncodingService() {
         return encodingService;
     }
 
     public RubyWarnings getWarnings() {
         return warnings;
     }
 
     public PrintStream getErrorStream() {
         // FIXME: We can't guarantee this will always be a RubyIO...so the old code here is not safe
         /*java.io.OutputStream os = ((RubyIO) getGlobalVariables().get("$stderr")).getOutStream();
         if(null != os) {
             return new PrintStream(os);
         } else {
             return new PrintStream(new org.jruby.util.SwallowingOutputStream());
         }*/
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stderr")));
     }
 
     public InputStream getInputStream() {
         return new IOInputStream(getGlobalVariables().get("$stdin"));
     }
 
     public PrintStream getOutputStream() {
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stdout")));
     }
 
     public RubyModule getClassFromPath(String path) {
         RubyModule c = getObject();
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
         }
         int pbeg = 0, p = 0;
         for(int l=path.length(); p<l; ) {
             while(p<l && path.charAt(p) != ':') {
                 p++;
             }
             String str = path.substring(pbeg, p);
 
             if(p<l && path.charAt(p) == ':') {
                 if(p+1 < l && path.charAt(p+1) != ':') {
                     throw newTypeError("undefined class/module " + path.substring(pbeg,p));
                 }
                 p += 2;
                 pbeg = p;
             }
 
             IRubyObject cc = c.getConstant(str);
             if(!(cc instanceof RubyModule)) {
                 throw newTypeError("" + path + " does not refer to class/module");
             }
             c = (RubyModule)cc;
         }
         return c;
     }
 
     /** Prints an error with backtrace to the error stream.
      *
      * MRI: eval.c - error_print()
      *
      */
     public void printError(RubyException excp) {
         if (excp == null || excp.isNil()) {
             return;
         }
 
-        if (RubyException.TRACE_TYPE == RubyException.RUBINIUS) {
-            printRubiniusTrace(excp);
-            return;
-        }
-
-        ThreadContext context = getCurrentContext();
-        IRubyObject backtrace = excp.callMethod(context, "backtrace");
-
         PrintStream errorStream = getErrorStream();
-        if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
-            if (context.getFile() != null) {
-                errorStream.print(context.getFile() + ":" + context.getLine());
-            } else {
-                errorStream.print(context.getLine());
-            }
-        } else if (((RubyArray) backtrace).getLength() == 0) {
-            printErrorPos(context, errorStream);
-        } else {
-            IRubyObject mesg = ((RubyArray) backtrace).first();
-
-            if (mesg.isNil()) {
-                printErrorPos(context, errorStream);
-            } else {
-                errorStream.print(mesg);
-            }
-        }
-
-        RubyClass type = excp.getMetaClass();
-        String info = excp.toString();
-
-        if (type == getRuntimeError() && (info == null || info.length() == 0)) {
-            errorStream.print(": unhandled exception\n");
-        } else {
-            String path = type.getName();
-
-            if (info.length() == 0) {
-                errorStream.print(": " + path + '\n');
-            } else {
-                if (path.startsWith("#")) {
-                    path = null;
-                }
-
-                String tail = null;
-                if (info.indexOf("\n") != -1) {
-                    tail = info.substring(info.indexOf("\n") + 1);
-                    info = info.substring(0, info.indexOf("\n"));
-                }
-
-                errorStream.print(": " + info);
-
-                if (path != null) {
-                    errorStream.print(" (" + path + ")\n");
-                }
-
-                if (tail != null) {
-                    errorStream.print(tail + '\n');
-                }
-            }
-        }
-
-        excp.printBacktrace(errorStream);
-    }
-
-    private static final String FIRST_COLOR = "\033[0;31m";
-    private static final String KERNEL_COLOR = "\033[0;36m";
-    private static final String EVAL_COLOR = "\033[0;33m";
-    private static final String CLEAR_COLOR = "\033[0m";
-
-    private void printRubiniusTrace(RubyException exception) {
-        ThreadContext.RubyStackTraceElement[] frames = exception.getBacktraceElements();
-
-        ArrayList firstParts = new ArrayList();
-        int longestFirstPart = 0;
-        for (ThreadContext.RubyStackTraceElement frame : frames) {
-            String firstPart = frame.getClassName() + "#" + frame.getMethodName();
-            if (firstPart.length() > longestFirstPart) longestFirstPart = firstPart.length();
-            firstParts.add(firstPart);
-        }
-
-        // determine spacing
-        int center = longestFirstPart
-                + 2 // initial spaces
-                + 1; // spaces before "at"
-
-        StringBuilder buffer = new StringBuilder();
-
-        buffer
-                .append("An exception has occurred:\n")
-                .append("    ");
-
-        if (exception.getMetaClass() == getRuntimeError() && exception.message(getCurrentContext()).toString().length() == 0) {
-            buffer.append("No current exception (RuntimeError)");
-        } else {
-            buffer.append(exception.message(getCurrentContext()).toString());
-        }
-
-        buffer
-                .append('\n')
-                .append('\n')
-                .append("Backtrace:\n");
-
-        int i = 0;
-        for (ThreadContext.RubyStackTraceElement frame : frames) {
-            String firstPart = (String)firstParts.get(i);
-            String secondPart = frame.getFileName() + ":" + frame.getLineNumber();
-
-            if (i == 0) {
-                buffer.append(FIRST_COLOR);
-            } else if (frame.isBinding() || frame.getFileName().equals("(eval)")) {
-                buffer.append(EVAL_COLOR);
-            } else if (frame.getFileName().indexOf(".java") != -1) {
-                buffer.append(KERNEL_COLOR);
-            }
-            buffer.append("  ");
-            for (int j = 0; j < center - firstPart.length(); j++) {
-                buffer.append(' ');
-            }
-            buffer.append(firstPart);
-            buffer.append(" at ");
-            buffer.append(secondPart);
-            buffer.append(CLEAR_COLOR);
-            buffer.append('\n');
-            i++;
-        }
-
-        PrintStream errorStream = getErrorStream();
-        errorStream.print(buffer.toString());
-    }
-
-    private void printErrorPos(ThreadContext context, PrintStream errorStream) {
-        if (context.getFile() != null) {
-            if (context.getFrameName() != null) {
-                errorStream.print(context.getFile() + ":" + context.getLine());
-                errorStream.print(":in '" + context.getFrameName() + '\'');
-            } else if (context.getLine() != 0) {
-                errorStream.print(context.getFile() + ":" + context.getLine());
-            } else {
-                errorStream.print(context.getFile());
-            }
-        }
+        errorStream.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(excp));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             runInterpreter(context, parseFile(in, scriptName, null), self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
 
             Script script = null;
             String className = null;
 
             try {
                 // read full contents of file, hash it, and try to load that class first
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 byte[] buffer = new byte[1024];
                 int num;
                 while ((num = in.read(buffer)) > -1) {
                     baos.write(buffer, 0, num);
                 }
                 buffer = baos.toByteArray();
                 String hash = JITCompiler.getHashForBytes(buffer);
                 className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("found jitted code for " + filename + " at class: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
+            Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
-                Node scriptNode = parseFile(readStream, filename, null);
-
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
-            
+
             if (script == null) {
-                System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
-            }
+                failForcedCompile(scriptNode);
 
-            runScript(script);
+                runInterpreter(scriptNode);
+            } else {
+                runScript(script);
+            }
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.setFile(file);
         }
     }
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     public void addBoundMethod(String javaName, String rubyName) {
         boundMethods.put(javaName, rubyName);
     }
 
     public Map<String, String> getBoundMethods() {
         return boundMethods;
     }
 
     public class CallTraceFuncHook extends EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void eventHandler(ThreadContext context, String eventName, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this, context.currentBinding());
 
                 context.preTrace();
                 try {
                     traceFunc.call(context, new IRubyObject[] {
                         newString(eventName), // event name
                         newString(file), // filename
                         newFixnum(line), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(RubyEvent event) {
             return true;
         }
     };
     
     private final CallTraceFuncHook callTraceFuncHook = new CallTraceFuncHook();
     
     public void addEventHook(EventHook hook) {
         if (!RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // without full tracing, many events will not fire
             getWarnings().warn("tracing (e.g. set_trace_func) will not capture all events without --debug flag");
         }
         eventHooks.add(hook);
         hasEventHooks = true;
     }
     
     public void removeEventHook(EventHook hook) {
         eventHooks.remove(hook);
         hasEventHooks = !eventHooks.isEmpty();
     }
 
     public void setTraceFunction(RubyProc traceFunction) {
         removeEventHook(callTraceFuncHook);
         
         if (traceFunction == null) {
             return;
         }
         
         callTraceFuncHook.setTraceFunc(traceFunction);
         addEventHook(callTraceFuncHook);
     }
     
     public void callEventHooks(ThreadContext context, RubyEvent event, String file, int line, String name, IRubyObject type) {
         if (context.isEventHooksEnabled()) {
             for (EventHook eventHook : eventHooks) {
                 if (eventHook.isInterestedInEvent(event)) {
                     eventHook.event(context, event, file, line, name, type);
                 }
             }
         }
     }
     
     public boolean hasEventHooks() {
         return hasEventHooks;
     }
     
     public GlobalVariables getGlobalVariables() {
         return globalVariables;
     }
 
     // For JSR 223 support: see http://scripting.java.net/
     public void setGlobalVariables(GlobalVariables globalVariables) {
         this.globalVariables = globalVariables;
     }
 
     public CallbackFactory callbackFactory(Class<?> type) {
         return CallbackFactory.createFactory(this, type);
     }
 
     /**
      * Push block onto exit stack.  When runtime environment exits
      * these blocks will be evaluated.
      *
      * @return the element that was pushed onto stack
      */
     public IRubyObject pushExitBlock(RubyProc proc) {
         atExitBlocks.push(proc);
         return proc;
     }
 
     // use this for JRuby-internal finalizers
     public void addInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers == null) {
                 internalFinalizers = new WeakHashMap<Finalizable, Object>();
             }
             internalFinalizers.put(finalizer, null);
         }
     }
 
     // this method is for finalizers registered via ObjectSpace
     public void addFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap<Finalizable, Object>();
             }
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 internalFinalizers.remove(finalizer);
             }
         }
     }
 
     public void removeFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers != null) {
                 finalizers.remove(finalizer);
             }
         }
     }
 
     /**
      * Make sure Kernel#at_exit procs get invoked on runtime shutdown.
      * This method needs to be explicitly called to work properly.
      * I thought about using finalize(), but that did not work and I
      * am not sure the runtime will be at a state to run procs by the
      * time Ruby is going away.  This method can contain any other
      * things that need to be cleaned up at shutdown.
      */
     public void tearDown() {
         tearDown(true);
     }
 
     // tearDown(boolean) has been added for embedding API. When an error
     // occurs in Ruby code, JRuby does system exit abruptly, no chance to
     // catch exception. This makes debugging really hard. This is why
     // tearDown(boolean) exists.
     public void tearDown(boolean systemExit) {
         int status = 0;
 
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
             try {
                 proc.call(getCurrentContext(), IRubyObject.NULL_ARRAY);
             } catch (RaiseException rj) {
                 RubyException raisedException = rj.getException();
                 if (!getSystemExit().isInstance(raisedException)) {
                     status = 1;
                     printError(raisedException);
                 } else {
                     IRubyObject statusObj = raisedException.callMethod(
                             getCurrentContext(), "status");
                     if (statusObj != null && !statusObj.isNil()) {
                         status = RubyNumeric.fix2int(statusObj);
                     }
                 }
             }
         }
 
         if (finalizers != null) {
             synchronized (finalizersMutex) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
                     }
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
                     }
                     finalIter.remove();
                 }
             }
         }
 
         getThreadService().disposeCurrentThread();
 
         getBeanManager().unregisterCompiler();
         getBeanManager().unregisterConfig();
         getBeanManager().unregisterParserStats();
         getBeanManager().unregisterClassCache();
         getBeanManager().unregisterMethodCache();
 
         getJRubyClassLoader().tearDown(isDebug());
 
         if (systemExit && status != 0) {
             throw newSystemExit(status);
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
     public RubyArray newEmptyArray() {
         return RubyArray.newEmptyArray(this);
     }
 
     public RubyArray newArray() {
         return RubyArray.newArray(this);
     }
 
     public RubyArray newArrayLight() {
         return RubyArray.newArrayLight(this);
     }
 
     public RubyArray newArray(IRubyObject object) {
         return RubyArray.newArray(this, object);
     }
 
     public RubyArray newArray(IRubyObject car, IRubyObject cdr) {
         return RubyArray.newArray(this, car, cdr);
     }
 
     public RubyArray newArray(IRubyObject... objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject... objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject... objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return value ? trueObject : falseObject;
     }
 
     public RubyFileStat newFileStat(String filename, boolean lstat) {
         return RubyFileStat.newFileStat(this, filename, lstat);
     }
     
     public RubyFileStat newFileStat(FileDescriptor descriptor) {
         return RubyFileStat.newFileStat(this, descriptor);
     }
 
     public RubyFixnum newFixnum(long value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFixnum newFixnum(int value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFixnum newFixnum(Constant value) {
         return RubyFixnum.newFixnum(this, value.value());
     }
 
     public RubyFloat newFloat(double value) {
         return RubyFloat.newFloat(this, value);
     }
 
     public RubyNumeric newNumeric() {
         return RubyNumeric.newNumeric(this);
     }
 
     public RubyRational newRational(long num, long den) {
         return RubyRational.newRationalRaw(this, newFixnum(num), newFixnum(den));
     }
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
         return proc;
     }
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this, getCurrentContext().currentBinding());
     }
 
     public RubyBinding newBinding(Binding binding) {
         return RubyBinding.newBinding(this, binding);
     }
 
     public RubyString newString() {
         return RubyString.newString(this, new ByteList());
     }
 
     public RubyString newString(String string) {
         return RubyString.newString(this, string);
     }
     
     public RubyString newString(ByteList byteList) {
         return RubyString.newString(this, byteList);
     }
 
     @Deprecated
     public RubyString newStringShared(ByteList byteList) {
         return RubyString.newStringShared(this, byteList);
     }    
 
     public RubySymbol newSymbol(String name) {
         return symbolTable.getSymbol(name);
     }
 
     /**
      * Faster than {@link #newSymbol(String)} if you already have an interned
      * name String. Don't intern your string just to call this version - the
      * overhead of interning will more than wipe out any benefit from the faster
      * lookup.
      *   
      * @param internedName the symbol name, <em>must</em> be interned! if in
      *                     doubt, call {@link #newSymbol(String)} instead.
      * @return the symbol for name
      */
     public RubySymbol fastNewSymbol(String internedName) {
         //        assert internedName == internedName.intern() : internedName + " is not interned";
 
         return symbolTable.fastGetSymbol(internedName);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(getRuntimeError(), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(getArgumentError(), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong number of arguments (" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), "Operation now in progress");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().fastGetClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().fastGetClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), message);
     }
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().fastGetClass("EHOSTUNREACH"), message);
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().fastGetClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newRaiseException(getErrno().fastGetClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().fastGetClass("EPERM"), "Operation not permitted - " + name);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), "Socket is not connected");
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().fastGetClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().fastGetClass("EDOM"), "Domain error - " + message);
     }   
     
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().fastGetClass("ECHILD"), "No child processes");
     }    
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRNOTAVAIL"), message);
     }
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().fastGetClass("ESRCH"), null);
     }
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(getIndexError(), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getSecurityError(), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getSystemCallError(), message);
     }
 
     public RaiseException newErrnoFromLastPOSIXErrno() {
         return newRaiseException(getErrno(getPosix().errno()), null);
     }
 
     public RaiseException newErrnoFromInt(int errno, String message) {
         RubyClass errnoClass = getErrno(errno);
         if (errnoClass != null) {
             return newRaiseException(errnoClass, message);
         } else {
             return newSystemCallError("Unknown Error (" + errno + ") - " + message);
         }
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(getTypeError(), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(getThreadError(), message);
     }
 
     public RaiseException newConcurrencyError(String message) {
         return newRaiseException(getConcurrencyError(), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(getSyntaxError(), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(getRegexpError(), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getRangeError(), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getNotImplementedError(), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("InvalidEncoding"), message);
     }
     
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("IllegalSequence"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         RaiseException exception = new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
         exception.preRaise(getCurrentContext());
         return exception;
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, true);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (printWhenVerbose && origException != null && this.isVerbose()) {
             origException.printStackTrace(getErrorStream());
         }
         
         RaiseException exception = new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), false);
         exception.preRaise(getCurrentContext());
 
         return exception;
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         RaiseException exception = new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
         exception.preRaise(getCurrentContext());
         return exception;
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return newLocalJumpError(RubyLocalJumpError.Reason.REDO, getNil(), "unexpected redo");
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         return newFrozenError(objectType, false);
     }
 
     public RaiseException newFrozenError(String objectType, boolean runtimeError) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() || runtimeError ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemStackError(String message, StackOverflowError soe) {
         if (getDebug().isTrue()) {
             soe.printStackTrace(getInstanceConfig().getError());
         }
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemExit(int status) {
         RaiseException exception = new RaiseException(RubySystemExit.newInstance(this, status));
         exception.preRaise(getCurrentContext());
         return exception;
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getIOError(), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getStandardError(), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer")) {
                 throw newErrnoECONNRESETError();
             }
             return newRaiseException(getIOError(), ioe.getMessage());
         } else {
             return newRaiseException(getIOError(), "IO Error");
         }
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(getTypeError(), "wrong argument type " +
                 receivedObject.getMetaClass().getRealClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(getEOFError(), "End of file reached");
     }
 
     public RaiseException newEOFError(String message) {
         return newRaiseException(getEOFError(), message);
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(getZeroDivisionError(), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(getFloatDomainError(), message);
     }
 
     public RaiseException newMathDomainError(String message) {
         return newRaiseException(getMathDomainError(), "Numerical argument is out of domain - \"" + message + "\"");
     }
 
     public RaiseException newEncodingError(String message){
         return newRaiseException(getEncodingError(), message);
     }
 
     public RaiseException newEncodingCompatibilityError(String message){
         return newRaiseException(getEncodingCompatibilityError(), message);
     }
 
     public RaiseException newConverterNotFoundError(String message) {
         return newRaiseException(getConverterNotFoundError(), message);
     }
 
     public RaiseException newFiberError(String message) {
         return newRaiseException(getFiberError(), message);
     }
 
     public RaiseException newUndefinedConversionError(String message) {
         return newRaiseException(getUndefinedConversionError(), message);
     }
 
     public RaiseException newInvalidByteSequenceError(String message) {
         return newRaiseException(getInvalidByteSequenceError(), message);
     }
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         re.preRaise(getCurrentContext());
         return re;
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
     }
 
     public void setRandomSeed(long randomSeed) {
         this.randomSeed = randomSeed;
     }
 
     public long getRandomSeed() {
         return randomSeed;
     }
 
     public Random getRandom() {
         return random;
     }
 
     public ObjectSpace getObjectSpace() {
         return objectSpace;
     }
 
     private final Map<Integer, Integer> filenoExtIntMap = new HashMap<Integer, Integer>();
     private final Map<Integer, Integer> filenoIntExtMap = new HashMap<Integer, Integer>();
 
     public void putFilenoMap(int external, int internal) {
         filenoExtIntMap.put(external, internal);
         filenoIntExtMap.put(internal, external);
     }
 
     public int getFilenoExtMap(int external) {
         Integer internal = filenoExtIntMap.get(external);
         if (internal != null) return internal;
         return external;
     }
 
     public int getFilenoIntMap(int internal) {
         Integer external = filenoIntExtMap.get(internal);
         if (external != null) return external;
         return internal;
     }
 
     /**
      * Get the "external" fileno for a given ChannelDescriptor. Primarily for
      * the shared 0, 1, and 2 filenos, which we can't actually share across
      * JRuby runtimes.
      *
      * @param descriptor The descriptor for which to get the fileno
      * @return The external fileno for the descriptor
      */
     public int getFileno(ChannelDescriptor descriptor) {
         return getFilenoIntMap(descriptor.getFileno());
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
     }
 
     public long incrementRandomSeedSequence() {
         return randomSeedSequence++;
     }
 
     public InputStream getIn() {
         return in;
     }
 
     public PrintStream getOut() {
         return out;
     }
 
     public PrintStream getErr() {
         return err;
     }
 
     public boolean isGlobalAbortOnExceptionEnabled() {
         return globalAbortOnExceptionEnabled;
     }
 
     public void setGlobalAbortOnExceptionEnabled(boolean enable) {
         globalAbortOnExceptionEnabled = enable;
     }
 
     public boolean isDoNotReverseLookupEnabled() {
         return doNotReverseLookupEnabled;
     }
 
     public void setDoNotReverseLookupEnabled(boolean b) {
         doNotReverseLookupEnabled = b;
     }
 
     private ThreadLocal<Map<Object, Object>> inspect = new ThreadLocal<Map<Object, Object>>();
     public void registerInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val == null) inspect.set(val = new IdentityHashMap<Object, Object>());
         val.put(obj, null);
     }
 
     public boolean isInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         return val == null ? false : val.containsKey(obj);
     }
 
     public void unregisterInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val != null ) val.remove(obj);
     }
 
     public static interface RecursiveFunction {
         IRubyObject call(IRubyObject obj, boolean recur);
     }
 
     private static class RecursiveError extends Error implements Unrescuable {
         public RecursiveError(Object tag) {
             this.tag = tag;
         }
         public final Object tag;
         
         @Override
         public synchronized Throwable fillInStackTrace() {
             return this;
         }
     }
 
     private ThreadLocal<Map<String, RubyHash>> recursive = new ThreadLocal<Map<String, RubyHash>>();
     private IRubyObject recursiveListAccess() {
         Map<String, RubyHash> hash = recursive.get();
         String sym = getCurrentContext().getFrameName();
         IRubyObject list = getNil();
         if(hash == null) {
             hash = new HashMap<String, RubyHash>();
             recursive.set(hash);
         } else {
             list = hash.get(sym);
         }
         if(list == null || list.isNil()) {
             list = RubyHash.newHash(this);
             list.setUntrusted(true);
             hash.put(sym, (RubyHash)list);
         }
         return list;
     }
 
     private RubySymbol recursiveKey;
 
     private static class ExecRecursiveParams {
         public ExecRecursiveParams() {}
         public RecursiveFunction func;
         public IRubyObject list;
         public IRubyObject obj;
         public IRubyObject objid;
         public IRubyObject pairid;
     }
 
     private void recursivePush(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         IRubyObject pair_list;
         if(paired_obj == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, getTrue());
         } else if((pair_list = ((RubyHash)list).fastARef(obj)) == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, paired_obj);
         } else {
             if(!(pair_list instanceof RubyHash)) {
                 IRubyObject other_paired_obj = pair_list;
                 pair_list = RubyHash.newHash(this);
                 pair_list.setUntrusted(true);
                 ((RubyHash)pair_list).op_aset(getCurrentContext(), other_paired_obj, getTrue());
                 ((RubyHash)list).op_aset(getCurrentContext(), obj, pair_list);
             }
             ((RubyHash)pair_list).op_aset(getCurrentContext(), paired_obj, getTrue());
         }
     }
 
     private void recursivePop(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         if(paired_obj != null) {
             IRubyObject pair_list = ((RubyHash)list).fastARef(obj);
             if(pair_list == null) {
                 throw newTypeError("invalid inspect_tbl pair_list for " + getCurrentContext().getFrameName());
             }
             if(pair_list instanceof RubyHash) {
                 ((RubyHash)pair_list).delete(getCurrentContext(), paired_obj, Block.NULL_BLOCK);
                 if(!((RubyHash)pair_list).isEmpty()) {
                     return;
                 }
             }
         }
         ((RubyHash)list).delete(getCurrentContext(), obj, Block.NULL_BLOCK);
     }
 
     private boolean recursiveCheck(IRubyObject list, IRubyObject obj_id, IRubyObject paired_obj_id) {
         IRubyObject pair_list = ((RubyHash)list).fastARef(obj_id);
         if(pair_list == null) {
             return false;
         }
         if(paired_obj_id != null) {
             if(!(pair_list instanceof RubyHash)) {
                 if(pair_list != paired_obj_id) {
                     return false;
                 }
             } else {
                 IRubyObject paired_result = ((RubyHash)pair_list).fastARef(paired_obj_id);
                 if(paired_result == null || paired_result.isNil()) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     // exec_recursive_i
     private IRubyObject execRecursiveI(ExecRecursiveParams p) {
         IRubyObject result = null;
         recursivePush(p.list, p.objid, p.pairid);
         try {
             result = p.func.call(p.obj, false);
         } finally {
             recursivePop(p.list, p.objid, p.pairid);
         }
         return result;
     }
 
     // exec_recursive
     private IRubyObject execRecursiveInternal(RecursiveFunction func, IRubyObject obj, IRubyObject pairid, boolean outer) {
         ExecRecursiveParams p = new ExecRecursiveParams();
         p.list = recursiveListAccess();
         p.objid = obj.id();
         boolean outermost = outer && !recursiveCheck(p.list, recursiveKey, null);
         if(recursiveCheck(p.list, p.objid, pairid)) {
             if(outer && !outermost) {
                 throw new RecursiveError(p.list);
             }
diff --git a/src/org/jruby/RubyException.java b/src/org/jruby/RubyException.java
index d1ae784169..e1c751dc40 100644
--- a/src/org/jruby/RubyException.java
+++ b/src/org/jruby/RubyException.java
@@ -1,342 +1,310 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sf.net>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.List;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.JumpException.FlowControlException;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.TraceType;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
-import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Exception")
 public class RubyException extends RubyObject {
     private ThreadContext.RubyStackTraceElement[] backtraceElements;
     private IRubyObject backtrace;
     public IRubyObject message;
     public static final int TRACE_HEAD = 8;
     public static final int TRACE_TAIL = 4;
     public static final int TRACE_MAX = TRACE_HEAD + TRACE_TAIL + 6;
 
     protected RubyException(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, null);
     }
 
     public RubyException(Ruby runtime, RubyClass rubyClass, String message) {
         super(runtime, rubyClass);
         
         this.message = message == null ? runtime.getNil() : runtime.newString(message);
     }
     
     private static ObjectAllocator EXCEPTION_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyException instance = new RubyException(runtime, klass);
             
             // for future compatibility as constructors move toward not accepting metaclass?
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     private static final ObjectMarshal EXCEPTION_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyException exc = (RubyException)obj;
             
             marshalStream.registerLinkTarget(exc);
             List<Variable<Object>> attrs = exc.getVariableList();
             attrs.add(new VariableEntry<Object>(
                     "mesg", exc.message == null ? runtime.getNil() : exc.message));
             attrs.add(new VariableEntry<Object>("bt", exc.getBacktrace()));
             marshalStream.dumpVariables(attrs);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyException exc = (RubyException)type.allocate();
             
             unmarshalStream.registerLinkTarget(exc);
             // FIXME: Can't just pull these off the wire directly? Or maybe we should
             // just use real vars all the time for these?
             unmarshalStream.defaultVariablesUnmarshal(exc);
             
             exc.message = (IRubyObject)exc.removeInternalVariable("mesg");
             exc.set_backtrace((IRubyObject)exc.removeInternalVariable("bt"));
             
             return exc;
         }
     };
 
     public static RubyClass createExceptionClass(Ruby runtime) {
         RubyClass exceptionClass = runtime.defineClass("Exception", runtime.getObject(), EXCEPTION_ALLOCATOR);
         runtime.setException(exceptionClass);
 
         exceptionClass.index = ClassIndex.EXCEPTION;
         exceptionClass.setReifiedClass(RubyException.class);
 
         exceptionClass.setMarshal(EXCEPTION_MARSHAL);
         exceptionClass.defineAnnotatedMethods(RubyException.class);
 
         return exceptionClass;
     }
 
     public static RubyException newException(Ruby runtime, RubyClass excptnClass, String msg) {
         return new RubyException(runtime, excptnClass, msg);
     }
     
     public void setBacktraceElements(ThreadContext.RubyStackTraceElement[] backtraceElements) {
         this.backtraceElements = backtraceElements;
     }
     
     public ThreadContext.RubyStackTraceElement[] getBacktraceElements() {
         return backtraceElements;
     }
 
     public void prepareBacktrace(ThreadContext context, boolean nativeException) {
         // if it's null, build a backtrace
-        boolean fullTrace = false;
         if (backtraceElements == null) {
-            switch (TRACE_TYPE) {
-            case RAW:
-                backtraceElements = ThreadContext.gatherRawBacktrace(getRuntime(), Thread.currentThread().getStackTrace());
-                break;
-            case FULL:
-                fullTrace = true;
-            default:
-                backtraceElements = ThreadContext.gatherHybridBacktrace(
-                        context.getRuntime(),
-                        context.createBacktrace2(0, nativeException),
-                        Thread.currentThread().getStackTrace(),
-                        fullTrace);
-            }
+            backtraceElements = RubyInstanceConfig.TRACE_TYPE.getBacktrace(context, nativeException);
         }
     }
     
-    public static final int RAW = 0;
-    public static final int RUBY_FRAMED = 2;
-    public static final int RUBINIUS = 5;
-    public static final int FULL = 6;
-
-    public static final int RAW_FRAME_CROP_COUNT = 10;
-    
-    public static int TRACE_TYPE;
-    
-    static {
-        String style = SafePropertyAccessor.getProperty("jruby.backtrace.style", "ruby_framed").toLowerCase();
-        
-        if (style.equalsIgnoreCase("raw")) TRACE_TYPE = RAW;
-        else if (style.equalsIgnoreCase("ruby_framed")) TRACE_TYPE = RUBY_FRAMED;
-        else if (style.equalsIgnoreCase("rubinius")) TRACE_TYPE = RUBINIUS;
-        else if (style.equalsIgnoreCase("full")) TRACE_TYPE = FULL;
-        else TRACE_TYPE = RUBY_FRAMED;
-    }
-    
     public IRubyObject getBacktrace() {
         if (backtrace == null) {
             initBacktrace();
         }
         return backtrace;
     }
     
     public void initBacktrace() {
-        backtrace = ThreadContext.renderBacktraceMRI(getRuntime(), backtraceElements);
+        backtrace = TraceType.generateMRIBacktrace(getRuntime(), backtraceElements);
     }
 
     @JRubyMethod(optional = 2, visibility = PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 1) message = args[0];
         return this;
     }
 
     @JRubyMethod
     public IRubyObject backtrace() {
         IRubyObject bt = getBacktrace();
         return bt;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject set_backtrace(IRubyObject obj) {
         if (obj.isNil()) {
             backtrace = null;
         } else if (!isArrayOfStrings(obj)) {
             throw getRuntime().newTypeError("backtrace must be Array of String");
         } else {
             backtrace = (RubyArray) obj;
         }
         return backtrace();
     }
     
     @JRubyMethod(name = "exception", optional = 1, rest = true, meta = true)
     public static IRubyObject exception(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return ((RubyClass) recv).newInstance(context, args, block);
     }
 
     @JRubyMethod(optional = 1)
     public RubyException exception(IRubyObject[] args) {
         switch (args.length) {
             case 0 :
                 return this;
             case 1 :
                 if(args[0] == this) {
                     return this;
                 }
                 RubyException ret = (RubyException)rbClone();
                 ret.initialize(args, Block.NULL_BLOCK); // This looks wrong, but it's the way MRI does it.
                 return ret;
             default :
                 throw getRuntime().newArgumentError("Wrong argument count");
         }
     }
 
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         if (message.isNil()) return context.getRuntime().newString(getMetaClass().getName());
         message.setTaint(isTaint());
         return message;
     }
 
     @JRubyMethod(name = "to_str", compat = CompatVersion.RUBY1_8)
     public IRubyObject to_str(ThreadContext context) {
         return callMethod(context, "to_s");
     }
 
     @JRubyMethod(name = "message")
     public IRubyObject message(ThreadContext context) {
         return callMethod(context, "to_s");
     }
 
     /** inspects an object and return a kind of debug information
      * 
      *@return A RubyString containing the debug information.
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         RubyModule rubyClass = getMetaClass();
         RubyString exception = RubyString.objAsString(context, this);
 
         if (exception.getByteList().getRealSize() == 0) return getRuntime().newString(rubyClass.getName());
         StringBuilder sb = new StringBuilder("#<");
         sb.append(rubyClass.getName()).append(": ").append(exception.getByteList()).append(">");
         return getRuntime().newString(sb.toString());
     }
 
     @JRubyMethod(name = "==", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         boolean equal =
                 context.getRuntime().getException().isInstance(other) &&
                 callMethod(context, "message").equals(other.callMethod(context, "message")) &&
                 callMethod(context, "backtrace").equals(other.callMethod(context, "backtrace"));
         return context.getRuntime().newBoolean(equal);
     }
 
     @JRubyMethod(name = "===", meta = true)
     public static IRubyObject op_eqq(ThreadContext context, IRubyObject recv, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // special case non-FlowControlException Java exceptions so they'll be caught by rescue Exception
         if (recv == runtime.getException() && other instanceof ConcreteJavaProxy) {
             Object object = ((ConcreteJavaProxy)other).getObject();
             if (object instanceof Throwable && !(object instanceof FlowControlException)) {
                 return context.getRuntime().getTrue();
             }
         }
         // fall back on default logic
         return ((RubyClass)recv).op_eqq(context, other);
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyException exception = (RubyException)clone;
         exception.backtraceElements = backtraceElements;
         exception.backtrace = backtrace;
         exception.message = message;
     }
 
     public void printBacktrace(PrintStream errorStream) {
         IRubyObject backtrace = callMethod(getRuntime().getCurrentContext(), "backtrace");
         if (!backtrace.isNil() && backtrace instanceof RubyArray) {
             IRubyObject[] elements = backtrace.convertToArray().toJavaArray();
 
             for (int i = 1; i < elements.length; i++) {
                 IRubyObject stackTraceLine = elements[i];
                 if (stackTraceLine instanceof RubyString) {
                     printStackTraceLine(errorStream, stackTraceLine);
                 }
             }
         }
     }
 
     private void printStackTraceLine(PrintStream errorStream, IRubyObject stackTraceLine) {
             errorStream.print("\tfrom " + stackTraceLine + '\n');
     }
 	
     private boolean isArrayOfStrings(IRubyObject backtrace) {
         if (!(backtrace instanceof RubyArray)) return false; 
             
         IRubyObject[] elements = ((RubyArray) backtrace).toJavaArray();
         
         for (int i = 0 ; i < elements.length ; i++) {
             if (!(elements[i] instanceof RubyString)) return false;
         }
             
         return true;
     }
 
     // rb_exc_new3
     public static IRubyObject newException(ThreadContext context, RubyClass exceptionClass, IRubyObject message) {
         return exceptionClass.callMethod(context, "new", message.convertToString());
     }
 }
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index d6cef7f504..bd9ef6a8e6 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,1704 +1,1781 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2007-2010 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2009 Joseph LaFata <joe@quibb.org>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedReader;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
+import java.io.InputStreamReader;
 import java.io.PrintStream;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
-import org.jcodings.Encoding;
 
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTCompiler19;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Constants;
-import org.jruby.runtime.encoding.EncodingService;
+import org.jruby.runtime.TraceType;
 import org.jruby.runtime.profile.IProfileData;
 import org.jruby.runtime.profile.AbstractProfilePrinter;
 import org.jruby.runtime.profile.FlatProfilePrinter;
 import org.jruby.runtime.profile.GraphProfilePrinter;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.load.LoadService19;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.Opcodes;
 
 public class RubyInstanceConfig {
 
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     public static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     public static final int JIT_MAX_SIZE_LIMIT = 10000;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     public static final int JIT_THRESHOLD = 50;
     
     /** The version to use for generated classes. Set to current JVM version by default */
     public static final int JAVA_VERSION;
     
     /**
      * Default size for chained compilation.
      */
     public static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = 500;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT
             = SafePropertyAccessor.getInt("jruby.compile.chainsize", CHAINED_COMPILE_LINE_COUNT_DEFAULT);
 
     /**
      * Indicates whether the script must be extracted from script source
      */
     private boolean xFlag;
 
     public boolean hasShebangLine() {
         return hasShebangLine;
     }
 
     public void setHasShebangLine(boolean hasShebangLine) {
         this.hasShebangLine = hasShebangLine;
     }
 
     /**
      * Indicates whether the script has a shebang line or not
      */
     private boolean hasShebangLine;
 
     public boolean isxFlag() {
         return xFlag;
     }
 
     public enum CompileMode {
         JIT, FORCE, OFF, OFFIR;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
                 if (DYNOPT_COMPILE_ENABLED) {
                     // don't precompile the CLI script in dynopt mode
                     return false;
                 }
                 return true;
             }
             return false;
         }
 
         public boolean shouldJIT() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled
             = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
     private Map environment;
     private String[] argv = {};
 
     private final boolean jitLogging;
     private final boolean jitDumping;
     private final boolean jitLoggingVerbose;
     private int jitLogEvery;
     private int jitThreshold;
     private int jitMax;
     private int jitMaxSize;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
 
     private String internalEncoding = null;
     private String externalEncoding = null;
 
     public enum ProfilingMode {
 		OFF, API, FLAT, GRAPH
 	}
 		
     private ProfilingMode profilingMode = ProfilingMode.OFF;
     
     private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
     private ClassLoader loader = contextLoader == null ? RubyInstanceConfig.class.getClassLoader() : contextLoader;
 
     private ClassCache<Script> classCache;
 
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private Set<String> excludedMethods = new HashSet<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private Collection<String> requiredLibraries = new LinkedHashSet<String>();
     private boolean benchmarking = false;
     private boolean argvGlobalsOn = false;
     private boolean assumeLoop = false;
     private boolean assumePrinting = false;
     private Map optionGlobals = new HashMap();
     private boolean processLineEnds = false;
     private boolean split = false;
     // This property is a Boolean, to allow three values, so it can match MRI's nil, false and true
     private Boolean verbose = Boolean.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = false;
     private String inPlaceBackupExtension = null;
     private boolean parserDebug = false;
     private String threadDumpSignal = null;
     private boolean hardExit = false;
     private boolean disableGems = false;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean PEEPHOLE_OPTZ
             = SafePropertyAccessor.getBoolean("jruby.compile.peephole", true);
     public static boolean DYNOPT_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.dynopt", false);
     public static boolean NOGUARDS_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.noguards");
     public static boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
     public static boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops");
     public static boolean FRAMELESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static boolean POSITIONLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless", true);
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static boolean FASTCASE_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastcase");
     public static boolean FASTSEND_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastsend");
     public static boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
     public static boolean INLINE_DYNCALL_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.inlineDyncalls");
     public static final boolean FORK_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.fork.enabled");
     public static final boolean POOLING_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.thread.pool.enabled");
     public static final int POOL_MAX
             = SafePropertyAccessor.getInt("jruby.thread.pool.max", Integer.MAX_VALUE);
     public static final int POOL_MIN
             = SafePropertyAccessor.getInt("jruby.thread.pool.min", 0);
     public static final int POOL_TTL
             = SafePropertyAccessor.getInt("jruby.thread.pool.ttl", 60);
 
     public static final boolean NATIVE_NET_PROTOCOL
             = SafePropertyAccessor.getBoolean("jruby.native.net.protocol", false);
 
     public static boolean FULL_TRACE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
 
     public static final String COMPILE_EXCLUDE
             = SafePropertyAccessor.getProperty("jruby.jit.exclude");
     public static boolean nativeEnabled = true;
 
     public static final boolean REIFY_RUBY_CLASSES
             = SafePropertyAccessor.getBoolean("jruby.reify.classes", false);
 
     public static final boolean REIFY_LOG_ERRORS
             = SafePropertyAccessor.getBoolean("jruby.reify.logErrors", false);
 
     public static final boolean USE_GENERATED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.java.handles", false);
 
     public static final boolean DEBUG_LOAD_SERVICE
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService", false);
 
     public static final boolean DEBUG_LOAD_TIMINGS
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService.timing", false);
 
     public static final boolean DEBUG_LAUNCHING
             = SafePropertyAccessor.getBoolean("jruby.debug.launch", false);
 
     public static final boolean DEBUG_SCRIPT_RESOLUTION
             = SafePropertyAccessor.getBoolean("jruby.debug.scriptResolution", false);
 
     public static final boolean JUMPS_HAVE_BACKTRACE
             = SafePropertyAccessor.getBoolean("jruby.jump.backtrace", false);
 
     public static final boolean JIT_CACHE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.jit.cache", true);
 
     public static final String JIT_CODE_CACHE
             = SafePropertyAccessor.getProperty("jruby.jit.codeCache", null);
 
     public static final boolean REFLECTED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.reflected.handles", false)
             || SafePropertyAccessor.getBoolean("jruby.reflection", false);
 
     public static final boolean NO_UNWRAP_PROCESS_STREAMS
             = SafePropertyAccessor.getBoolean("jruby.process.noUnwrap", false);
 
     public static final boolean INTERFACES_USE_PROXY
             = SafePropertyAccessor.getBoolean("jruby.interfaces.useProxy");
 
     public static final boolean JIT_LOADING_DEBUG = SafePropertyAccessor.getBoolean("jruby.jit.debug", false);
 
     public static final boolean CAN_SET_ACCESSIBLE = SafePropertyAccessor.getBoolean("jruby.ji.setAccessible", true);
 
+    public static TraceType TRACE_TYPE =
+            TraceType.traceTypeFor(SafePropertyAccessor.getProperty("jruby.backtrace.style", "ruby_framed"));
+
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
                 public LoadService create(Ruby runtime) {
                     if (runtime.is1_9()) {
                         return new LoadService19(runtime);
                     }
                     return new LoadService(runtime);
                 }
             };
     }
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
 
 
     static {
         String specVersion = null;
         try {
             specVersion = System.getProperty("jruby.bytecode.version");
             if (specVersion == null) {
                 specVersion = System.getProperty("java.specification.version");
             }
             if (System.getProperty("jruby.native.enabled") != null) {
                 nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
             }
         } catch (SecurityException se) {
             nativeEnabled = false;
             specVersion = "1.5";
         }
         
         if (specVersion.equals("1.5")) {
             JAVA_VERSION = Opcodes.V1_5;
         } else {
             JAVA_VERSION = Opcodes.V1_6;
         }
     }
 
     public int characterIndex = 0;
 
     public RubyInstanceConfig(RubyInstanceConfig parentConfig) {
         setCurrentDirectory(parentConfig.getCurrentDirectory());
         samplingEnabled = parentConfig.samplingEnabled;
         compatVersion = parentConfig.compatVersion;
         compileMode = parentConfig.getCompileMode();
         jitLogging = parentConfig.jitLogging;
         jitDumping = parentConfig.jitDumping;
         jitLoggingVerbose = parentConfig.jitLoggingVerbose;
         jitLogEvery = parentConfig.jitLogEvery;
         jitThreshold = parentConfig.jitThreshold;
         jitMax = parentConfig.jitMax;
         jitMaxSize = parentConfig.jitMaxSize;
         managementEnabled = parentConfig.managementEnabled;
         runRubyInProcess = parentConfig.runRubyInProcess;
         excludedMethods = parentConfig.excludedMethods;
         threadDumpSignal = parentConfig.threadDumpSignal;
         
         classCache = new ClassCache<Script>(loader, jitMax);
     }
 
     public RubyInstanceConfig() {
         setCurrentDirectory(Ruby.isSecurityRestricted() ? "/" : JRubyFile.getFileProperty("user.dir"));
         samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
 
         String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             setCompatVersion(CompatVersion.RUBY1_8);
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             setCompatVersion(CompatVersion.RUBY1_9);
         } else {
             error.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             setCompatVersion(CompatVersion.RUBY1_8);
         }
 
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
             jitDumping = false;
             jitLoggingVerbose = false;
             jitLogEvery = 0;
             jitThreshold = -1;
             jitMax = 0;
             jitMaxSize = -1;
             managementEnabled = false;
         } else {
             String threshold = SafePropertyAccessor.getProperty("jruby.jit.threshold");
             String max = SafePropertyAccessor.getProperty("jruby.jit.max");
             String maxSize = SafePropertyAccessor.getProperty("jruby.jit.maxsize");
             
             if (COMPILE_EXCLUDE != null) {
                 String[] elements = COMPILE_EXCLUDE.split(",");
                 excludedMethods.addAll(Arrays.asList(elements));
             }
             
             managementEnabled = SafePropertyAccessor.getBoolean("jruby.management.enabled", false);
             runRubyInProcess = SafePropertyAccessor.getBoolean("jruby.launch.inproc", true);
             boolean jitProperty = SafePropertyAccessor.getProperty("jruby.jit.enabled") != null;
             if (jitProperty) {
                 error.print("jruby.jit.enabled property is deprecated; use jruby.compile.mode=(OFF|JIT|FORCE) for -C, default, and +C flags");
                 compileMode = SafePropertyAccessor.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
             } else {
                 String jitModeProperty = SafePropertyAccessor.getProperty("jruby.compile.mode", "JIT");
 
                 if (jitModeProperty.equals("OFF")) {
                     compileMode = CompileMode.OFF;
                 } else if (jitModeProperty.equals("JIT")) {
                     compileMode = CompileMode.JIT;
                 } else if (jitModeProperty.equals("FORCE")) {
                     compileMode = CompileMode.FORCE;
                 } else {
                     error.print("jruby.compile.mode property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                     compileMode = CompileMode.JIT;
                 }
             }
             jitLogging = SafePropertyAccessor.getBoolean("jruby.jit.logging");
             jitDumping = SafePropertyAccessor.getBoolean("jruby.jit.dumping");
             jitLoggingVerbose = SafePropertyAccessor.getBoolean("jruby.jit.logging.verbose");
             String logEvery = SafePropertyAccessor.getProperty("jruby.jit.logEvery");
             jitLogEvery = logEvery == null ? 0 : Integer.parseInt(logEvery);
             jitThreshold = threshold == null ?
                     JIT_THRESHOLD : Integer.parseInt(threshold);
             jitMax = max == null ?
                     JIT_MAX_METHODS_LIMIT : Integer.parseInt(max);
             jitMaxSize = maxSize == null ?
                     JIT_MAX_SIZE_LIMIT : Integer.parseInt(maxSize);
         }
 
         // default ClassCache using jitMax as a soft upper bound
         classCache = new ClassCache<Script>(loader, jitMax);
         threadDumpSignal = SafePropertyAccessor.getProperty("jruby.thread.dump.signal", "USR2");
 
         if (FORK_ENABLED) {
             error.print("WARNING: fork is highly unlikely to be safe or stable on the JVM. Have fun!\n");
         }
     }
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return this.creator.create(runtime);
     }
 
     public String getBasicUsageHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("Usage: jruby [switches] [--] [programfile] [arguments]\n")
                 .append("  -0[octal]       specify record separator (\\0, if no argument)\n")
                 .append("  -a              autosplit mode with -n or -p (splits $_ into $F)\n")
                 .append("  -b              benchmark mode, times the script execution\n")
                 .append("  -c              check syntax only\n")
                 .append("  -Cdirectory     cd to directory, before executing your script\n")
                 .append("  -d              set debugging flags (set $DEBUG to true)\n")
                 .append("  -e 'command'    one line of script. Several -e's allowed. Omit [programfile]\n")
                 .append("  -Eex[:in]       specify the default external and internal character encodings")
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 .append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode, -Ke for EUC and -Ks\n")
                 .append("                    for SJIS)\n")
                 .append("  -l              enable line ending processing\n")
                 .append("  -n              assume 'while gets(); ... end' loop around your script\n")
                 .append("  -p              assume loop like -n but print line also like sed\n")
                 .append("  -rlibrary       require the library, before executing your script\n")
                 .append("  -s              enable some switch parsing for switches after script name\n")
                 .append("  -S              look for the script in bin or using PATH environment variable\n")
                 .append("  -T[level]       turn on tainting checks\n")
                 .append("  -U              use UTF-8 as default internal encoding")
                 .append("  -v              print version number, then turn on verbose mode\n")
                 .append("  -w              turn warnings on for your script\n")
                 .append("  -W[level]       set warning level; 0=silence, 1=medium, 2=verbose (default)\n")
                 .append("  -x[directory]   strip off text before #!ruby line and perhaps cd to directory\n")
                 .append("  -X[option]      enable extended option (omit option to list)\n")
                 .append("  -y              enable parsing debug output\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger\n")
                 .append("                    functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties\n")
                 .append("                    (pass -X<property without \"jruby.\">=value to set them)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --profile       run with instrumented (timed) profiling, flat format\n")
                 .append("  --profile.api   activate Ruby profiler API\n")
                 .append("  --profile.flat  synonym for --profile\n")
                 .append("  --profile.graph run with instrumented (timed) profiling, graph format\n")
                 .append("  --client        use the non-optimizing \"client\" JVM\n")
                 .append("                    (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM\n")
                 .append("                    and JRuby\n")
                 .append("  --headless      do not launch a GUI window, no matter what\n")
                 .append("  --1.8           specify Ruby 1.8.x compatibility (default)\n")
                 .append("  --1.9           specify Ruby 1.9.x compatibility\n")
                 .append("  --bytecode      show the JVM bytecode produced by compiling specified code\n")
                 .append("  --version       print the version\n");
 
         return sb.toString();
     }
 
     public String getExtendedHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These flags are for extended JRuby options.\n")
                 .append("Specify them by passing -X<option>\n")
                 .append("  -O              run with ObjectSpace disabled (default; improves performance)\n")
                 .append("  +O              run with ObjectSpace enabled (reduces performance)\n")
                 .append("  -C              disable all compilation\n")
                 .append("  +C              force compilation of all scripts before they are run (except eval)\n");
 
         return sb.toString();
     }
 
     public String getPropertyHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.\n")
                 .append("Specify them by passing -J-D<property>=<value>\n")
                 .append("\nCOMPILER SETTINGS:\n")
                 .append("    jruby.compile.mode=JIT|FORCE|OFF\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables\n")
                 .append("    jruby.compile.fastest=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on all experimental compiler optimizations\n")
                 .append("    jruby.compile.frameless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible\n")
                 .append("    jruby.compile.positionless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation that avoids updating Ruby position info. Default is false\n")
                 .append("    jruby.compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    jruby.compile.fastops=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast operators for Fixnum. Default is false\n")
                 .append("    jruby.compile.fastcase=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast case/when for all-Fixnum whens. Default is false\n")
                 .append("    jruby.compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is ").append(CHAINED_COMPILE_LINE_COUNT_DEFAULT).append("\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.\n")
                 .append("    jruby.compile.peephole=true|false\n")
                 .append("       Enable or disable peephole optimizations. Default is true (on).\n")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jruby.jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is ").append(JIT_THRESHOLD).append(".\n")
                 .append("    jruby.jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is ").append(JIT_MAX_METHODS_LIMIT).append(" per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jruby.jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is ").append(JIT_MAX_SIZE_LIMIT).append(".\n")
                 .append("    jruby.jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jruby.jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jruby.jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("    jruby.jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
                 .append("    jruby.jit.cache=true|false\n")
                 .append("       Cache jitted method in-memory bodies across runtimes and loads. Default is true.\n")
                 .append("    jruby.jit.codeCache=<dir>\n")
                 .append("       Save jitted methods to <dir> as they're compiled, for future runs.\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    jruby.native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    jruby.native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("\nTHREAD POOLING:\n")
                 .append("    jruby.thread.pool.enabled=true|false\n")
                 .append("       Enable reuse of native backing threads via a thread pool. Default is false.\n")
                 .append("    jruby.thread.pool.min=<min thread count>\n")
                 .append("       The minimum number of threads to keep alive in the pool. Default is 0.\n")
                 .append("    jruby.thread.pool.max=<max thread count>\n")
                 .append("       The maximum number of threads to allow in the pool. Default is unlimited.\n")
                 .append("    jruby.thread.pool.ttl=<time to live, in seconds>\n")
                 .append("       The maximum number of seconds to keep alive an idle thread. Default is 60.\n")
                 .append("\nMISCELLANY:\n")
                 .append("    jruby.compat.version=RUBY1_8|RUBY1_9\n")
                 .append("       Specify the major Ruby version to be compatible with; Default is RUBY1_8\n")
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is false.\n")
                 .append("    jruby.jump.backtrace=true|false\n")
                 .append("       Make non-local flow jumps generate backtraces. Default is false.\n")
                 .append("    jruby.process.noUnwrap=true|false\n")
                 .append("       Do not unwrap process streams (IBM Java 6 issue). Default is false.\n")
                 .append("    jruby.reify.classes=true|false\n")
                 .append("       Before instantiation, stand up a real Java class for ever Ruby class. Default is false. \n")
                 .append("    jruby.reify.logErrors=true|false\n")
                 .append("       Log errors during reification (reify.classes=true). Default is false. \n")
                 .append("\nDEBUGGING/LOGGING:\n")
                 .append("    jruby.debug.loadService=true|false\n")
                 .append("       LoadService logging\n")
                 .append("    jruby.debug.loadService.timing=true|false\n")
                 .append("       Print load timings for each require'd library. Default is false.\n")
                 .append("    jruby.debug.launch=true|false\n")
                 .append("       ShellLauncher logging\n")
                 .append("    jruby.debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n")
                 .append("    jruby.debug.scriptResolution=true|false\n")
                 .append("       Print which script is executed by '-S' flag. Default is false.\n")
                 .append("    jruby.reflected.handles=true|false\n")
                 .append("       Use reflection for binding methods, not generated bytecode. Default is false.\n")
                 .append("\nJAVA INTEGRATION:\n")
                 .append("    jruby.ji.setAccessible=true|false\n")
                 .append("       Try to set inaccessible Java methods to be accessible. Default is true.\n")
                 .append("    jruby.interfaces.useProxy=true|false\n")
                 .append("       Use java.lang.reflect.Proxy for interface impl. Default is false.\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = null;
         String patchDelimeter = " patchlevel ";
         int patchlevel = 0;
         switch (getCompatVersion()) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             patchlevel = Constants.RUBY_PATCHLEVEL;
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             patchlevel = Constants.RUBY1_9_PATCHLEVEL;
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s%s%d) (%s %s) (%s %s) [%s-%s-java]",
                 Constants.VERSION, ver, patchDelimeter, patchlevel,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 System.getProperty("java.vm.name"), System.getProperty("java.version"),
                 Platform.getOSName(),
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2011 The JRuby Community (and contribs)";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
         tryProcessArgumentsWithRubyopts();
     }
 
     public void tryProcessArgumentsWithRubyopts() {
         try {
             String rubyopt = null;
             if (environment != null && environment.containsKey("RUBYOPT")) {
                 rubyopt = environment.get("RUBYOPT").toString();
             } else {
                 rubyopt = System.getenv("RUBYOPT");
             }
             
             if (rubyopt == null || rubyopt.isEmpty()) return;
 
             if (rubyopt.split("\\s").length != 0) {
                 String[] rubyoptArgs = rubyopt.split("\\s+");
                 new ArgumentProcessor(rubyoptArgs, false, true).processArguments();
             }
         } catch (SecurityException se) {
             // ignore and do nothing
         }
     }
 
+    /**
+     * The intent here is to gather up any options that might have
+     * been specified in the shebang line and return them so they can
+     * be merged into the ones specified on the commandline.  This is
+     * kind of a hopeless task because it's impossible to figure out
+     * where the command invocation stops and the parameters start.
+     * We try to work with the common scenarios where /usr/bin/env is
+     * used to invoke the jruby shell script, and skip any parameters
+     * it might have.  Then we look for the interpreter invokation and
+     * assume that the binary will have the word "ruby" in the name.
+     * This is error prone but should cover more cases than the
+     * previous code.
+     */
+    public String[] parseShebangOptions(InputStream in) {
+        BufferedReader reader = null;
+        String[] result = new String[0];
+        if (in == null) return result;
+        try {
+            in.mark(1024);
+            reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8192);
+            String firstLine = reader.readLine();
+
+            // Search for the shebang line in the given stream
+            // if it wasn't found on the first line and the -x option
+            // was specified
+            if (isxFlag()) {
+                while (firstLine != null && !isShebangLine(firstLine)) {
+                    firstLine = reader.readLine();
+                }
+            }
+
+            boolean usesEnv = false;
+            if (firstLine.length() > 2 && firstLine.charAt(0) == '#' && firstLine.charAt(1) == '!') {
+                String[] options = firstLine.substring(2).split("\\s+");
+                int i;
+                for (i = 0; i < options.length; i++) {
+                    // Skip /usr/bin/env if it's first
+                    if (i == 0 && options[i].endsWith("/env")) {
+                        usesEnv = true;
+                        continue;
+                    }
+                    // Skip any assignments if /usr/bin/env is in play
+                    if (usesEnv && options[i].indexOf('=') > 0) {
+                        continue;
+                    }
+                    // Skip any commandline args if /usr/bin/env is in play
+                    if (usesEnv && options[i].startsWith("-")) {
+                        continue;
+                    }
+                    String basename = (new File(options[i])).getName();
+                    if (basename.indexOf("ruby") > 0) {
+                        break;
+                    }
+                }
+                setHasShebangLine(true);
+                System.arraycopy(options, i, result, 0, options.length - i);
+            } else {
+                // No shebang line found
+                setHasShebangLine(false);
+            }
+        } catch (Exception ex) {
+            // ignore error
+        } finally {
+            try {
+                in.reset();
+            } catch (IOException ex) {}
+        }
+        return result;
+    }
+
+    protected static boolean isShebangLine(String line) {
+        return (line.length() > 2 && line.charAt(0) == '#' && line.charAt(1) == '!');
+    }
+
     public CompileMode getCompileMode() {
         return compileMode;
     }
 
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitDumping() {
         return jitDumping;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public int getJitLogEvery() {
         return jitLogEvery;
     }
 
     public void setJitLogEvery(int jitLogEvery) {
         this.jitLogEvery = jitLogEvery;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
 
     public int getJitThreshold() {
         return jitThreshold;
     }
 
     public void setJitThreshold(int jitThreshold) {
         this.jitThreshold = jitThreshold;
     }
 
     public int getJitMax() {
         return jitMax;
     }
 
     public void setJitMax(int jitMax) {
         this.jitMax = jitMax;
     }
 
     public int getJitMaxSize() {
         return jitMaxSize;
     }
 
     public void setJitMaxSize(int jitMaxSize) {
         this.jitMaxSize = jitMaxSize;
     }
 
     public boolean isRunRubyInProcess() {
         return runRubyInProcess;
     }
 
     public void setRunRubyInProcess(boolean flag) {
         this.runRubyInProcess = flag;
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public CompatVersion getCompatVersion() {
         return compatVersion;
     }
 
     public void setCompatVersion(CompatVersion compatVersion) {
         if (compatVersion == null) compatVersion = CompatVersion.RUBY1_8;
 
         this.compatVersion = compatVersion;
     }
 
     public void setOutput(PrintStream newOutput) {
         output = newOutput;
     }
 
     public PrintStream getOutput() {
         return output;
     }
 
     public void setError(PrintStream newError) {
         error = newError;
     }
 
     public PrintStream getError() {
         return error;
     }
 
     public void setCurrentDirectory(String newCurrentDirectory) {
         currentDirectory = newCurrentDirectory;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setProfile(Profile newProfile) {
         profile = newProfile;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public void setObjectSpaceEnabled(boolean newObjectSpaceEnabled) {
         objectSpaceEnabled = newObjectSpaceEnabled;
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setEnvironment(Map newEnvironment) {
         environment = newEnvironment;
     }
 
     public Map getEnvironment() {
         return environment;
     }
 
     public ClassLoader getLoader() {
         return loader;
     }
 
     public void setLoader(ClassLoader loader) {
         // Setting the loader needs to reset the class cache
         if(this.loader != loader) {
             this.classCache = new ClassCache<Script>(loader, this.classCache.getMax());
         }
         this.loader = loader;
     }
 
     public String[] getArgv() {
         return argv;
     }
 
     public void setArgv(String[] argv) {
         this.argv = argv;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             // try the normal property first
             if (!Ruby.isSecurityRestricted()) {
                 jrubyHome = SafePropertyAccessor.getProperty("jruby.home");
             }
 
             if (jrubyHome != null) {
                 // verify it if it's there
                 jrubyHome = verifyHome(jrubyHome);
             } else {
                 try {
                     // try loading from classloader resources
                     final String jrubyHomePath = "/META-INF/jruby.home";
                     URL jrubyHomeURL = getClass().getResource(jrubyHomePath);
                     // special case for jar:file (most typical case)
                     if (jrubyHomeURL.getProtocol().equals("jar")) {
                         jrubyHome = URLDecoder.decode(jrubyHomeURL.getPath(), "UTF-8");
                     } else {
                         jrubyHome = "classpath:" + jrubyHomePath;
                         return jrubyHome;
                     }
                 } catch (Exception e) {}
 
                 if (jrubyHome != null) {
                     // verify it if it's there
                     jrubyHome = verifyHome(jrubyHome);
                 } else {
                     // otherwise fall back on system temp location
                     jrubyHome = SafePropertyAccessor.getProperty("java.io.tmpdir");
                 }
             }
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = SafePropertyAccessor.getProperty("user.dir");
         }
         if (home.startsWith("cp:")) {
             home = home.substring(3);
         } else if (!home.startsWith("file:") && !home.startsWith("classpath:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             if (!f.exists()) {
                 error.println("Warning: JRuby home \"" + f + "\" does not exist, using " + SafePropertyAccessor.getProperty("java.io.tmpdir"));
                 return System.getProperty("java.io.tmpdir");
             }
         }
         return home;
     }
 
     private final class Argument {
         public final String originalValue;
         public final String dashedValue;
         public Argument(String value, boolean dashed) {
             this.originalValue = value;
             this.dashedValue = dashed && !value.startsWith("-") ? "-" + value : value;
         }
     }
 
     private class ArgumentProcessor {
         private List<Argument> arguments;
         private int argumentIndex = 0;
         private boolean processArgv;
 
         public ArgumentProcessor(String[] arguments) {
             this(arguments, true, false);
         }
 
         public ArgumentProcessor(String[] arguments, boolean processArgv, boolean dashed) {
             this.arguments = new ArrayList<Argument>();
             if (arguments != null && arguments.length > 0) {
                 for (String argument : arguments) {
                     this.arguments.add(new Argument(argument, dashed));
                 }
             }
             this.processArgv = processArgv;
         }
 
         public void processArguments() {
             while (argumentIndex < arguments.size() && isInterpreterArgument(arguments.get(argumentIndex).originalValue)) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript && scriptFileName == null) {
                 if (argumentIndex < arguments.size()) {
                     setScriptFileName(arguments.get(argumentIndex).originalValue); //consume the file name
                     argumentIndex++;
                 }
             }
 
             if (processArgv) processArgv();
         }
 
         private void processArgv() {
             List<String> arglist = new ArrayList<String>();
             for (; argumentIndex < arguments.size(); argumentIndex++) {
                 String arg = arguments.get(argumentIndex).originalValue;
                 if (argvGlobalsOn && arg.startsWith("-")) {
                     arg = arg.substring(1);
                     if (arg.indexOf('=') > 0) {
                         String[] keyvalue = arg.split("=", 2);
                         optionGlobals.put(keyvalue[0], keyvalue[1]);
                     } else {
                         optionGlobals.put(arg, null);
                     }
                 } else {
                     argvGlobalsOn = false;
                     arglist.add(arg);
                 }
             }
 
             // Remaining arguments are for the script itself
             arglist.addAll(Arrays.asList(argv));
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return argument.length() > 0 && (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private String getArgumentError(String additionalError) {
             return "jruby: invalid argument\n" + additionalError + "\n";
         }
 
         private void processArgument() {
             String argument = arguments.get(argumentIndex).dashedValue;
             FOR : for (characterIndex = 1; characterIndex < argument.length(); characterIndex++) {
                 switch (argument.charAt(characterIndex)) {
                 case '0': {
                     String temp = grabOptionalValue();
                     if (null == temp) {
                         recordSeparator = "\u0000";
                     } else if (temp.equals("0")) {
                         recordSeparator = "\n\n";
                     } else if (temp.equals("777")) {
                         recordSeparator = "\uFFFF"; // Specify something that can't separate
                     } else {
                         try {
                             int val = Integer.parseInt(temp, 8);
                             recordSeparator = "" + (char) val;
                         } catch (Exception e) {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -0 must be followed by either 0, 777, or a valid octal value"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     break FOR;
                 }
                 case 'a':
                     split = true;
                     break;
                 case 'b':
                     benchmarking = true;
                     break;
                 case 'c':
                     shouldCheckSyntax = true;
                     break;
                 case 'C':
                     try {
                         String saved = grabValue(getArgumentError(" -C must be followed by a directory expression"));
                         File base = new File(currentDirectory);
                         File newDir = new File(saved);
                         if (newDir.isAbsolute()) {
                             currentDirectory = newDir.getCanonicalPath();
                         } else {
                             currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                         }
                         if (!(new File(currentDirectory).isDirectory())) {
                             MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                             throw mee;
                         }
                     } catch (IOException e) {
                         MainExitException mee = new MainExitException(1, getArgumentError(" -C must be followed by a valid directory"));
                         throw mee;
                     }
                     break FOR;
                 case 'd':
                     debug = true;
                     verbose = Boolean.TRUE;
                     break;
                 case 'e':
                     inlineScript.append(grabValue(getArgumentError(" -e must be followed by an expression to evaluate")));
                     inlineScript.append('\n');
                     hasInlineScript = true;
                     break FOR;
                 case 'E':
                     processEncodingOption(grabValue(getArgumentError("unknown encoding name")));
                     break FOR;
                 case 'F':
                     inputFieldSeparator = grabValue(getArgumentError(" -F must be followed by a pattern for input field separation"));
                     break FOR;
                 case 'h':
                     shouldPrintUsage = true;
                     shouldRunInterpreter = false;
                     break;
                 case 'i' :
                     inPlaceBackupExtension = grabOptionalValue();
                     if(inPlaceBackupExtension == null) inPlaceBackupExtension = "";
                     break FOR;
                 case 'I':
                     String s = grabValue(getArgumentError("-I must be followed by a directory name to add to lib path"));
                     String[] ls = s.split(java.io.File.pathSeparator);
                     loadPaths.addAll(Arrays.asList(ls));
                     break FOR;
                 case 'J':
                     grabOptionalValue();
                     error.println("warning: "+argument+" argument ignored (launched in same VM?)");
                     break FOR;
                 case 'K':
                     // FIXME: No argument seems to work for -K in MRI plus this should not
                     // siphon off additional args 'jruby -K ~/scripts/foo'.  Also better error
                     // processing.
                     String eArg = grabValue(getArgumentError("provide a value for -K"));
                     kcode = KCode.create(null, eArg);
                     break;
                 case 'l':
                     processLineEnds = true;
                     break;
                 case 'n':
                     assumeLoop = true;
                     break;
                 case 'p':
                     assumePrinting = true;
                     assumeLoop = true;
                     break;
                 case 'r':
                     requiredLibraries.add(grabValue(getArgumentError("-r must be followed by a package to require")));
                     break FOR;
                 case 's' :
                     argvGlobalsOn = true;
                     break;
                 case 'S':
                     runBinScript();
                     break FOR;
                 case 'T' :{
                     String temp = grabOptionalValue();
                     int value = 1;
 
                     if(temp!=null) {
                         try {
                             value = Integer.parseInt(temp, 8);
                         } catch(Exception e) {
                             value = 1;
                         }
                     }
 
                     safeLevel = value;
 
                     break FOR;
                 }
                 case 'U':
                     internalEncoding = "UTF-8";
                     break;
                 case 'v':
                     verbose = Boolean.TRUE;
                     setShowVersion(true);
                     break;
                 case 'w':
                     verbose = Boolean.TRUE;
                     break;
                 case 'W': {
                     String temp = grabOptionalValue();
                     int value = 2;
                     if (null != temp) {
                         if (temp.equals("2")) {
                             value = 2;
                         } else if (temp.equals("1")) {
                             value = 1;
                         } else if (temp.equals("0")) {
                             value = 0;
                         } else {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -W must be followed by either 0, 1, 2 or nothing"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     switch (value) {
                     case 0:
                         verbose = null;
                         break;
                     case 1:
                         verbose = Boolean.FALSE;
                         break;
                     case 2:
                         verbose = Boolean.TRUE;
                         break;
                     }
 
 
                     break FOR;
                 }
                case 'x':
                    try {
                        String saved = grabOptionalValue();
                        if (saved != null) {
                            File base = new File(currentDirectory);
                            File newDir = new File(saved);
                            if (newDir.isAbsolute()) {
                                currentDirectory = newDir.getCanonicalPath();
                            } else {
                                currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                            }
                            if (!(new File(currentDirectory).isDirectory())) {
                                MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                                throw mee;
                            }
                        }
                        xFlag = true;
                    } catch (IOException e) {
                        MainExitException mee = new MainExitException(1, getArgumentError(" -x must be followed by a valid directory"));
                        throw mee;
                    }
                    break FOR;
                 case 'X':
                     String extendedOption = grabOptionalValue();
 
                     if (extendedOption == null) {
                         throw new MainExitException(0, "jruby: missing extended option, listing available options\n" + getExtendedHelp());
                     } else if (extendedOption.equals("-O")) {
                         objectSpaceEnabled = false;
                     } else if (extendedOption.equals("+O")) {
                         objectSpaceEnabled = true;
                     } else if (extendedOption.equals("-C")) {
                         compileMode = CompileMode.OFF;
                     } else if (extendedOption.equals("-CIR")) {
                         compileMode = CompileMode.OFFIR;
                     } else if (extendedOption.equals("+C")) {
                         compileMode = CompileMode.FORCE;
                     } else {
                         MainExitException mee =
                                 new MainExitException(1, "jruby: invalid extended option " + extendedOption + " (-X will list valid options)\n");
                         mee.setUsageError(true);
 
                         throw mee;
                     }
                     break FOR;
                 case 'y':
                     parserDebug = true;
                     break FOR;
                 case '-':
                     if (argument.equals("--command") || argument.equals("--bin")) {
                         characterIndex = argument.length();
                         runBinScript();
                         break;
                     } else if (argument.equals("--compat")) {
                         characterIndex = argument.length();
                         setCompatVersion(CompatVersion.getVersionFromString(grabValue(getArgumentError("--compat must be RUBY1_8 or RUBY1_9"))));
                         break FOR;
                     } else if (argument.equals("--copyright")) {
                         setShowCopyright(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--debug")) {
                         FULL_TRACE_ENABLED = true;
                         compileMode = CompileMode.OFF;
                         break FOR;
                     } else if (argument.equals("--jdb")) {
                         debug = true;
                         verbose = Boolean.TRUE;
                         break;
                     } else if (argument.equals("--help")) {
                         shouldPrintUsage = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--properties")) {
                         shouldPrintProperties = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--version")) {
                         setShowVersion(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--bytecode")) {
                         setShowBytecode(true);
                         break FOR;
                     } else if (argument.equals("--fast")) {
                         compileMode = CompileMode.FORCE;
                         FASTEST_COMPILE_ENABLED = true;
                         FASTOPS_COMPILE_ENABLED = true;
                         FRAMELESS_COMPILE_ENABLED = true;
                         POSITIONLESS_COMPILE_ENABLED = true;
                         FASTCASE_COMPILE_ENABLED = true;
                         FASTSEND_COMPILE_ENABLED = true;
                         INLINE_DYNCALL_ENABLED = true;
                         break FOR;
                     } else if (argument.equals("--profile.api")) {
                         profilingMode = ProfilingMode.API;
                         break FOR;
                     } else if (argument.equals("--profile") ||
                             argument.equals("--profile.flat")) {
                         profilingMode = ProfilingMode.FLAT;
                         break FOR;
                     } else if (argument.equals("--profile.graph")) {
                         profilingMode = ProfilingMode.GRAPH;
                         break FOR;
                     } else if (argument.equals("--1.9")) {
                         setCompatVersion(CompatVersion.RUBY1_9);
                         break FOR;
                     } else if (argument.equals("--1.8")) {
                         setCompatVersion(CompatVersion.RUBY1_8);
                         break FOR;
                     } else if (argument.equals("--disable-gems")) {
                         disableGems = true;
                         break FOR;
                     } else {
                         if (argument.equals("--")) {
                             // ruby interpreter compatibilty
                             // Usage: ruby [switches] [--] [programfile] [arguments])
                             endOfArguments = true;
                             break;
                         }
                     }
                 default:
                     throw new MainExitException(1, "jruby: unknown option " + argument);
                 }
             }
         }
 
         private void processEncodingOption(String value) {
             String[] encodings = value.split(":", 3);
 
             switch(encodings.length) {
                 case 3:
                     throw new MainExitException(1, "extra argument for -E: " + encodings[2]);
                 case 2:
                     internalEncoding = encodings[1];
                 case 1:
                     externalEncoding = encodings[0];
                 // Zero is impossible
             }
         }
 
         private void runBinScript() {
             String scriptName = grabValue("jruby: provide a bin script to execute");
             if (scriptName.equals("irb")) {
                 scriptName = "jirb";
             }
 
             scriptFileName = resolveScript(scriptName);
 
             // run as a command if we couldn't find a script
             if (scriptFileName == null) {
                 scriptFileName = scriptName;
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands.").append(scriptName);
                 inlineScript.append("\n");
                 hasInlineScript = true;
             }
 
             endOfArguments = true;
         }
 
         private String resolveScript(String scriptName) {
             // This try/catch is to allow failing over to the "commands" logic
             // when running from within a jruby-complete jar file, which has
             // jruby.home = a jar file URL that does not resolve correctly with
             // JRubyFile.create.
             try {
                 // try cwd first
                 File fullName = JRubyFile.create(currentDirectory, scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     if (DEBUG_SCRIPT_RESOLUTION) {
                         error.println("Found: " + fullName.getAbsolutePath());
                     }
                     return scriptName;
                 }
 
                 fullName = JRubyFile.create(getJRubyHome(), "bin/" + scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     if (DEBUG_SCRIPT_RESOLUTION) {
                         error.println("Found: " + fullName.getAbsolutePath());
                     }
                     return fullName.getAbsolutePath();
                 }
 
                 try {
                     String path = System.getenv("PATH");
                     if (path != null) {
                         String[] paths = path.split(System.getProperty("path.separator"));
                         for (int i = 0; i < paths.length; i++) {
                             fullName = JRubyFile.create(paths[i], scriptName);
                             if (fullName.exists() && fullName.isFile()) {
                                 if (DEBUG_SCRIPT_RESOLUTION) {
                                     error.println("Found: " + fullName.getAbsolutePath());
                                 }
                                 return fullName.getAbsolutePath();
                             }
                         }
                     }
                 } catch (SecurityException se) {
                     // ignore and do nothing
                 }
             } catch (IllegalArgumentException iae) {
                 if (debug) error.println("warning: could not resolve -S script on filesystem: " + scriptName);
             }
             return null;
         }
 
         private String grabValue(String errorMessage) {
             String optValue = grabOptionalValue();
             if (optValue != null) {
                 return optValue;
             }
             argumentIndex++;
             if (argumentIndex < arguments.size()) {
                 return arguments.get(argumentIndex).originalValue;
             }
 
             MainExitException mee = new MainExitException(1, errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
 
         private String grabOptionalValue() {
             characterIndex++;
             String argValue = arguments.get(argumentIndex).originalValue;
             if (characterIndex < argValue.length()) {
                 return argValue.substring(characterIndex);
             }
             return null;
         }
     }
 
     public byte[] inlineScript() {
         return inlineScript.toString().getBytes();
     }
 
     public Collection<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public void setLoadPaths(List<String> loadPaths) {
         this.loadPaths = loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         return isShouldRunInterpreter();
     }
 
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     public InputStream getScriptSource() {
         try {
             // KCode.NONE is used because KCODE does not affect parse in Ruby 1.8
             // if Ruby 2.0 encoding pragmas are implemented, this will need to change
             if (hasInlineScript) {
                 return new ByteArrayInputStream(inlineScript());
             } else if (isSourceFromStdin()) {
                 // can't use -v and stdin
                 if (isShowVersion()) {
                     return null;
                 }
                 return getInput();
             } else {
                 String script = getScriptFileName();
                 InputStream stream = null;
                 if (script.startsWith("file:") && script.indexOf(".jar!/") != -1) {
                     stream = new URL("jar:" + script).openStream();
                 } else if (script.startsWith("classpath:")) {
                     stream = Ruby.getClassLoader().getResourceAsStream(script.substring("classpath:".length()));
                 } else {
                     File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                     if (isxFlag()) {
                         // search for a shebang line and
                         // return the script between shebang and __END__ or CTRL-Z (0x1A)
                         return findScript(file);
                     }
                     stream = new FileInputStream(file);
                 }
 
                 return new BufferedInputStream(stream, 8192);
             }
         } catch (IOException e) {
             // We haven't found any file directly on the file system,
             // now check for files inside the JARs.
             InputStream is = getJarScriptSource();
             if (is != null) {
                 return new BufferedInputStream(is, 8129);
             }
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     private InputStream findScript(File file) throws IOException {
         StringBuffer buf = new StringBuffer();
         BufferedReader br = new BufferedReader(new FileReader(file));
         String currentLine = br.readLine();
         while (currentLine != null && !(currentLine.length() > 2 && currentLine.charAt(0) == '#' && currentLine.charAt(1) == '!')) {
             currentLine = br.readLine();
         }
 
         buf.append(currentLine);
         buf.append("\n");
 
         do {
             currentLine = br.readLine();
             if (currentLine != null) {
             buf.append(currentLine);
             buf.append("\n");
             }
         } while (!(currentLine == null || currentLine.contains("__END__") || currentLine.contains("\026")));
         return new BufferedInputStream(new ByteArrayInputStream(buf.toString().getBytes()), 8192);
     }
 
     private InputStream getJarScriptSource() {
         String name = getScriptFileName();
         boolean looksLikeJarURL = name.startsWith("file:") && name.indexOf("!/") != -1;
         if (!looksLikeJarURL) {
             return null;
         }
 
         String before = name.substring("file:".length(), name.indexOf("!/"));
         String after =  name.substring(name.indexOf("!/") + 2);
 
         try {
             JarFile jFile = new JarFile(before);
             JarEntry entry = jFile.getJarEntry(after);
 
             if (entry != null && !entry.isDirectory()) {
                 return jFile.getInputStream(entry);
             }
         } catch (IOException ignored) {
         }
         return null;
     }
 
     public String displayedFileName() {
         if (hasInlineScript) {
             if (scriptFileName != null) {
                 return scriptFileName;
             } else {
                 return "-e";
             }
         } else if (isSourceFromStdin()) {
             return "-";
         } else {
             return getScriptFileName();
         }
     }
 
     public void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
 
     public boolean isSplit() {
         return split;
     }
 
     public boolean isVerbose() {
         return verbose == Boolean.TRUE;
     }
 
     public Boolean getVerbose() {
         return verbose;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     public boolean isParserDebug() {
         return parserDebug;
     }
 
     public boolean isShowVersion() {
         return showVersion;
     }
     
     public boolean isShowBytecode() {
         return showBytecode;
     }
 
     public boolean isShowCopyright() {
         return showCopyright;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     protected void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     protected void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
 
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     public boolean isShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
 
     public String getInputFieldSeparator() {
         return inputFieldSeparator;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public String getInternalEncoding() {
         return internalEncoding;
     }
 
     public String getExternalEncoding() {
         return externalEncoding;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
 
     public int getSafeLevel() {
         return safeLevel;
     }
 
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
 
     public String getInPlaceBackupExtention() {
         return inPlaceBackupExtension;
     }
 
     public void setClassCache(ClassCache classCache) {
         this.classCache = classCache;
     }
 
     public Map getOptionGlobals() {
         return optionGlobals;
     }
     
     public boolean isManagementEnabled() {
         return managementEnabled;
     }
     
     public Set getExcludedMethods() {
         return excludedMethods;
     }
 
     public ASTCompiler newCompiler() {
         if (getCompatVersion() == CompatVersion.RUBY1_8) {
             return new ASTCompiler();
         } else {
             return new ASTCompiler19();
         }
     }
 
     public String getThreadDumpSignal() {
         return threadDumpSignal;
     }
 
     public boolean isHardExit() {
         return hardExit;
     }
 
     public void setHardExit(boolean hardExit) {
         this.hardExit = hardExit;
     }
 
     public boolean isProfiling() {
         return profilingMode != ProfilingMode.OFF;
     }
     
     public boolean isProfilingEntireRun() {
         return profilingMode != ProfilingMode.OFF && profilingMode != ProfilingMode.API;
     }
 
     public ProfilingMode getProfilingMode() {
         return profilingMode;
     }
     
     public AbstractProfilePrinter makeDefaultProfilePrinter(IProfileData profileData) {
         if (profilingMode == ProfilingMode.FLAT) {
             return new FlatProfilePrinter(profileData);
         }
         else if (profilingMode == ProfilingMode.GRAPH) {
             return new GraphProfilePrinter(profileData);
         }
         return null;
     }
 
     public boolean isDisableGems() {
         return disableGems;
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 3e16194f0f..660fb56877 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -95,1469 +95,1452 @@ public final class ThreadContext {
     private int frameIndex = -1;
 
     private Backtrace[] backtrace = new Backtrace[1000];
     private int backtraceIndex = -1;
 
     public static class Backtrace {
         public Backtrace() {
         }
         public Backtrace(String klass, String method, String filename, int line) {
             this.method = method;
             this.filename = filename;
             this.line = line;
             this.klass = klass;
         }
         @Override
         public String toString() {
             return klass + "#" + method + " at " + filename + ":" + line;
         }
         @Override
         public Backtrace clone() {
             return new Backtrace(klass, method, filename, line);
         }
         public void update(String klass, String method, ISourcePosition position) {
             this.method = method;
             if (position == ISourcePosition.INVALID_POSITION) {
                 // use dummy values; there's no need for a real position here anyway
                 this.filename = "dummy";
                 this.line = -1;
             } else {
                 this.filename = position.getFile();
                 this.line = position.getLine();
             }
             this.klass = klass;
         }
         public void update(String klass, String method, String file, int line) {
             this.method = method;
             this.filename = file;
             this.line = line;
             this.klass = klass;
         }
         
         public String getFilename() {
             return filename;
         }
 
         public void setFilename(String filename) {
             this.filename = filename;
         }
 
         public String getKlass() {
             return klass;
         }
 
         public void setKlass(String klass) {
             this.klass = klass;
         }
 
         public int getLine() {
             return line;
         }
 
         public void setLine(int line) {
             this.line = line;
         }
 
         public String getMethod() {
             return method;
         }
 
         public void setMethod(String method) {
             this.method = method;
         }
         public String klass;
         public String method;
         public String filename;
         public int line;
     }
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
 
     private static final Continuation[] EMPTY_CATCHTARGET_STACK = new Continuation[0];
     private Continuation[] catchStack = EMPTY_CATCHTARGET_STACK;
     private int catchIndex = -1;
     
     private boolean isProfiling = false;
     // The flat profile data for this thread
 	private IProfileData profileData;
 	
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     private boolean eventHooksEnabled = true;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         this.nil = runtime.getNil();
         if (runtime.getInstanceConfig().isProfilingEntireRun())
             startProfiling();
 
         this.runtimeCache = runtime.getRuntimeCache();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
 
         Frame[] stack = frameStack;
         int length = stack.length;
         for (int i = 0; i < length; i++) {
             stack[i] = new Frame();
         }
         Backtrace[] stack2 = backtrace;
         int length2 = stack2.length;
         for (int i = 0; i < length2; i++) {
             stack2[i] = new Backtrace();
         }
         pushBacktrace("", "", "", 0);
         pushBacktrace("", "", "", 0);
     }
 
     @Override
     protected void finalize() throws Throwable {
         thread.dispose();
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return thread.getErrorInfo();
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         thread.setErrorInfo(errorInfo);
         return errorInfo;
     }
     
     public ReturnJump returnJump(IRubyObject value) {
         return new ReturnJump(getFrameJumpTarget(), value);
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public void setLastCallStatusAndVisibility(CallType callType, Visibility visibility) {
         lastCallType = callType;
         lastVisibility = visibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary() {
         int newSize = frameStack.length * 2;
         frameStack = fillNewFrameStack(new Frame[newSize], newSize);
     }
 
     private Frame[] fillNewFrameStack(Frame[] newFrameStack, int newSize) {
         System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
 
         for (int i = frameStack.length; i < newSize; i++) {
             newFrameStack[i] = new Frame();
         }
         
         return newFrameStack;
     }
     
     private void expandParentsIfNecessary() {
         int newSize = parentStack.length * 2;
         RubyModule[] newParentStack = new RubyModule[newSize];
 
         System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
 
         parentStack = newParentStack;
     }
     
     public void pushScope(DynamicScope scope) {
         int index = ++scopeIndex;
         DynamicScope[] stack = scopeStack;
         stack[index] = scope;
         if (index + 1 == stack.length) {
             expandScopesIfNecessary();
         }
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         int newSize = scopeStack.length * 2;
         DynamicScope[] newScopeStack = new DynamicScope[newSize];
 
         System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
 
         scopeStack = newScopeStack;
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
 
         // associate the thread with this context, unless we're clearing the reference
         if (thread != null) {
             thread.setContext(this);
         }
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         if (newSize == 0) newSize = 1;
         Continuation[] newCatchStack = new Continuation[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(Continuation catchTarget) {
         int index = ++catchIndex;
         if (index == catchStack.length) {
             expandCatchIfNecessary();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(Object tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (runtime.is1_9()) {
                 if (c.tag == tag) return c;
             } else {
                 if (c.tag.equals(tag)) return c;
             }
         }
         return null;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(name);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void popFrame() {
         Frame frame = frameStack[frameIndex--];
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         frameStack[frameIndex--] = oldFrame;
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
 
     public int getRubyFrameDelta() {
         return this.rubyFrameDelta;
     }
     
     public void setRubyFrameDelta(int newDelta) {
         this.rubyFrameDelta = newDelta;
     }
 
     public Frame getCurrentRubyFrame() {
         return frameStack[frameIndex-rubyFrameDelta];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
 
     public Frame[] getFrames(int delta) {
         int top = frameIndex + delta;
         Frame[] frames = new Frame[top + 1];
         for (int i = 0; i <= top; i++) {
             frames[i] = frameStack[i].duplicateForBacktrace();
         }
         return frames;
     }
 
     /////////////////// BACKTRACE ////////////////////
 
     private void expandBacktraceIfNecessary() {
         int newSize = backtrace.length * 2;
         backtrace = fillNewBacktrace(new Backtrace[newSize], newSize);
     }
 
     private Backtrace[] fillNewBacktrace(Backtrace[] newBacktrace, int newSize) {
         System.arraycopy(backtrace, 0, newBacktrace, 0, backtrace.length);
 
         for (int i = backtrace.length; i < newSize; i++) {
             newBacktrace[i] = new Backtrace();
         }
 
         return newBacktrace;
     }
 
     public void pushBacktrace(String klass, String method, ISourcePosition position) {
         int index = ++this.backtraceIndex;
         Backtrace[] stack = backtrace;
         stack[index].update(klass, method, position);
         if (index + 1 == stack.length) {
             expandBacktraceIfNecessary();
         }
     }
 
     public void pushBacktrace(String klass, String method, String file, int line) {
         int index = ++this.backtraceIndex;
         Backtrace[] stack = backtrace;
         stack[index].update(klass, method, file, line);
         if (index + 1 == stack.length) {
             expandBacktraceIfNecessary();
         }
     }
 
     public void popBacktrace() {
         backtraceIndex--;
     }
 
     /**
      * Search the frame stack for the given JumpTarget. Return true if it is
      * found and false otherwise. Skip the given number of frames before
      * beginning the search.
      * 
      * @param target The JumpTarget to search for
      * @param skipFrames The number of frames to skip before searching
      * @return
      */
     public boolean isJumpTargetAlive(int target, int skipFrames) {
         for (int i = frameIndex - skipFrames; i >= 0; i--) {
             if (frameStack[i].getJumpTarget() == target) return true;
         }
         return false;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public int getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return backtrace[backtraceIndex].filename;
     }
     
     public int getLine() {
         return backtrace[backtraceIndex].line;
     }
     
     public void setFile(String file) {
         backtrace[backtraceIndex].filename = file;
     }
     
     public void setLine(int line) {
         backtrace[backtraceIndex].line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         backtrace[backtraceIndex].filename = file;
         backtrace[backtraceIndex].line = line;
     }
 
     public void setFileAndLine(ISourcePosition position) {
         backtrace[backtraceIndex].filename = position.getFile();
         backtrace[backtraceIndex].line = position.getStartLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, backtrace[backtraceIndex].filename, backtrace[backtraceIndex].line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         int index = ++parentIndex;
         RubyModule[] stack = parentStack;
         stack[index] = currentModule;
         if (index + 1 == stack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         int index = parentIndex;
         RubyModule[] stack = parentStack;
         RubyModule ret = stack[index];
         stack[index] = null;
         parentIndex = index - 1;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getPreviousRubyClass() {
         assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
         RubyModule parentModule = parentStack[parentIndex - 1];
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject value = getConstant(internedName);
 
         return value != null;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         return getCurrentScope().getStaticScope().getConstant(runtime, internedName, runtime.getObject());
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement element) {
         RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
         backtrace.append(str);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         RubyStackTraceElement[] trace = gatherCallerBacktrace(level);
 
         // scrub out .java core class names and replace with Ruby equivalents
         OUTER: for (int i = 0; i < trace.length; i++) {
             RubyStackTraceElement element = trace[i];
             String classDotMethod = element.getClassName() + "." + element.getMethodName();
             if (runtime.getBoundMethods().containsKey(classDotMethod)) {
                 String rubyName = runtime.getBoundMethods().get(classDotMethod);
                 // find first Ruby file+line
                 RubyStackTraceElement rubyElement = null;
                 for (int j = i; j < trace.length; j++) {
                     rubyElement = trace[j];
                     if (!rubyElement.getFileName().endsWith(".java")) {
                         trace[i] = new RubyStackTraceElement(
                                 element.getClassName(),
                                 rubyName,
                                 rubyElement.getFileName(),
                                 rubyElement.getLineNumber(),
                                 rubyElement.isBinding(),
                                 rubyElement.getFrameType());
                         continue OUTER;
                     }
                 }
                 // no match, leave it as is
 
             }
         }
         
         RubyArray backtrace = runtime.newArray(trace.length - level);
 
         for (int i = level; i < trace.length; i++) {
             addBackTraceElement(runtime, backtrace, trace[i]);
         }
         
         return backtrace;
     }
     
     public RubyStackTraceElement[] gatherCallerBacktrace(int level) {
         Backtrace[] copy = new Backtrace[backtraceIndex + 1];
         System.arraycopy(backtrace, 0, copy, 0, backtraceIndex + 1);
         RubyStackTraceElement[] trace = gatherHybridBacktrace(
                 runtime,
                 copy,
                 Thread.currentThread().getStackTrace(),
                 false);
 
         return trace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
 
     public static class RubyStackTraceElement {
         private final StackTraceElement element;
         private final boolean binding;
         private final FrameType frameType;
 
         public RubyStackTraceElement(StackTraceElement element) {
             this.element = element;
             this.binding = false;
             this.frameType = FrameType.METHOD;
         }
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             this(cls, method, file, line, binding, FrameType.METHOD);
         }
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding, FrameType frameType) {
             this.element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
             this.frameType = frameType;
         }
 
         public StackTraceElement getElement() {
             return element;
         }
 
         public boolean isBinding() {
             return binding;
         }
 
         public String getClassName() {
             return element.getClassName();
         }
 
         public String getFileName() {
             return element.getFileName();
         }
 
         public int getLineNumber() {
             return element.getLineNumber();
         }
 
         public String getMethodName() {
             return element.getMethodName();
         }
 
         public FrameType getFrameType() {
             return frameType;
         }
 
         public String toString() {
             return element.toString();
         }
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Backtrace[] createBacktrace2(int level, boolean nativeException) {
         Backtrace[] newTrace = new Backtrace[backtraceIndex + 1];
         for (int i = 0; i <= backtraceIndex; i++) {
             newTrace[i] = backtrace[i].clone();
         }
         return newTrace;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuffer buffer = new StringBuffer();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
 
     public static RubyStackTraceElement[] gatherRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         List trace = new ArrayList(stackTrace.length);
         
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             trace.add(new RubyStackTraceElement(element));
         }
 
         RubyStackTraceElement[] rubyStackTrace = new RubyStackTraceElement[trace.size()];
         return (RubyStackTraceElement[])trace.toArray(rubyStackTrace);
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_METHOD", FrameType.METHOD);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_EVAL", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_CLASS", FrameType.CLASS);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_BLOCK", FrameType.BLOCK);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_ROOT", FrameType.ROOT);
     }
 
     public static RubyStackTraceElement[] gatherHybridBacktrace(Ruby runtime, Backtrace[] backtraceFrames, StackTraceElement[] stackTrace, boolean fullTrace) {
         List trace = new ArrayList(stackTrace.length);
 
         // a running index into the Ruby backtrace stack, incremented for each
         // interpreter frame we encounter in the Java backtrace.
         int rubyFrameIndex = backtraceFrames == null ? -1 : backtraceFrames.length - 1;
 
         // no Java trace, can't generate hybrid trace
         // TODO: Perhaps just generate the interpreter trace? Is this path ever hit?
         if (stackTrace == null) return null;
 
         // walk the Java stack trace, peeling off Java elements or Ruby elements
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
 
             if (element.getFileName() != null &&
                     (element.getFileName().endsWith(".rb") ||
                     element.getFileName().equals("-e") ||
                     // FIXME: Formalize jitted method structure so this isn't quite as hacky
                     element.getClassName().startsWith(JITCompiler.RUBY_JIT_PREFIX) ||
                     element.getMethodName().contains("$RUBY$"))) {
                 if (element.getLineNumber() == -1) continue;
                 
                 String methodName = element.getMethodName();
                 String className = element.getClassName();
 
                 // FIXME: Formalize jitted method structure so this isn't quite as hacky
                 if (className.startsWith(JITCompiler.RUBY_JIT_PREFIX)) {
                     // pull out and demangle the method name
                     methodName = className.substring(JITCompiler.RUBY_JIT_PREFIX.length() + 1, className.lastIndexOf("_"));
                     methodName = JavaNameMangler.demangleMethodName(methodName);
                     
                     trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
 
                     // if it's a synthetic call, use it but gobble up parent calls
                     // TODO: need to formalize this better
                     if (element.getMethodName().contains("$RUBY$SYNTHETIC")) {
                         // gobble up at least one parent, and keep going if there's more synthetic frames
                         while (element.getMethodName().indexOf("$RUBY$SYNTHETIC") != -1) {
                             i++;
                             element = stackTrace[i];
                         }
                     }
                     continue;
                 }
 
                 // AOT formatted method names, need to be cleaned up
                 int RUBYindex = methodName.indexOf("$RUBY$");
                 if (RUBYindex >= 0) {
                     // if it's a synthetic call, use it but gobble up parent calls
                     // TODO: need to formalize this better
                     methodName = methodName.substring(RUBYindex);
                     if (methodName.startsWith("$RUBY$SYNTHETIC")) {
                         methodName = methodName.substring("$RUBY$SYNTHETIC".length());
                         methodName = JavaNameMangler.demangleMethodName(methodName);
                         if (methodName == "__file__") methodName = "(root)";
                         trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
 
                         // gobble up at least one parent, and keep going if there's more synthetic frames
                         while (element.getMethodName().indexOf("$RUBY$SYNTHETIC") != -1) {
                             i++;
                             element = stackTrace[i];
                         }
                         continue;
                     }
 
                     // not a synthetic body
                     methodName = methodName.substring("$RUBY$".length());
                     methodName = JavaNameMangler.demangleMethodName(methodName);
                     trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
                     continue;
                 }
 
                 // last attempt at AOT compiled backtrace element, looking for __file__
                 if (methodName.equals("__file__")) {
                     methodName = "(root)";
                     trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
                     continue;
                 }
             }
 
             String dotClassMethod = element.getClassName() + "." + element.getMethodName();
             if (fullTrace || runtime.getBoundMethods().containsKey(dotClassMethod)) {
                 String filename = element.getFileName();
                 int lastDot = element.getClassName().lastIndexOf('.');
                 if (lastDot != -1) {
                     filename = element.getClassName().substring(0, lastDot + 1).replaceAll("\\.", "/") + filename;
                 }
                 trace.add(new RubyStackTraceElement(element.getClassName(), element.getMethodName(), filename, element.getLineNumber(), false));
 
                 if (!fullTrace) {
                     // we want to fall through below to add Ruby trace info as well
                     continue;
                 }
             }
 
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null && rubyFrameIndex >= 0) {
                 // Frame matches one of our markers for "interpreted" calls
                 Backtrace rubyElement = backtraceFrames[rubyFrameIndex];
                 trace.add(new RubyStackTraceElement(rubyElement.klass, rubyElement.method, rubyElement.filename, rubyElement.line + 1, false));
                 rubyFrameIndex--;
                 continue;
             } else {
                 // frames not being included...
 //                RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
 //                traceArray.append(str);
                 continue;
             }
         }
 
         RubyStackTraceElement[] rubyStackTrace = new RubyStackTraceElement[trace.size()];
         return (RubyStackTraceElement[])trace.toArray(rubyStackTrace);
     }
-
-    public static IRubyObject renderBacktraceMRI(Ruby runtime, RubyStackTraceElement[] trace) {
-        if (trace == null) {
-            return runtime.getNil();
-        }
-        
-        RubyArray traceArray = RubyArray.newArray(runtime);
-
-        for (int i = 0; i < trace.length; i++) {
-            RubyStackTraceElement element = trace[i];
-
-            RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
-            traceArray.append(str);
-        }
-
-        return traceArray;
-    }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
 
     public void preCompiledClassDummyScope(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(staticScope.getDummyScope());
     }
 
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
 
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
 
     public void preMethodNoFrameAndDummyScope(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
     }
     
     public void preMethodBacktraceOnly(String name) {
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
     }
 
     public void postMethodBacktraceDummyScope() {
         popRubyClass();
         popScope();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame[] currentFrames) {
         for (Frame frame : currentFrames) {
             pushFrame(frame);
         }
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(true);
         Frame lastFrame = pushFrameForEval(binding);
         pushRubyClass(binding.getKlass());
         return lastFrame;
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame();
         return new Binding(frame, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param visibility the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Get the profile data for this thread (ThreadContext).
      *
      * @return the thread's profile data
      */
     public IProfileData getProfileData() {
         if (profileData == null)
             profileData = new ProfileData(this);
         return profileData;
     }
 
     public int profileEnter(int nextMethod) {
         if (isProfiling)
             return getProfileData().profileEnter(nextMethod);
         else
             return -1;
     }
 
     public int profileExit(int nextMethod, long startTime) {
         if (isProfiling)
             return getProfileData().profileExit(nextMethod, startTime);
         else
             return -1;
     }
     
     public void startProfiling() {
         isProfiling = true;
     }
     
     public void stopProfiling() {
         isProfiling = false;
     }
     
     public boolean isProfiling() {
         return isProfiling;
     }
 }
diff --git a/src/org/jruby/util/NailMain.java b/src/org/jruby/util/NailMain.java
index 82454c996b..985717fe63 100644
--- a/src/org/jruby/util/NailMain.java
+++ b/src/org/jruby/util/NailMain.java
@@ -1,104 +1,38 @@
 package org.jruby.util;
 
 import com.martiansoftware.nailgun.NGContext;
 import org.jruby.Main;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.executable.Script;
-import org.jruby.exceptions.MainExitException;
-import org.jruby.exceptions.ThreadKill;
 
 public class NailMain {
     public static final ClassCache<Script> classCache;
 
     static {
          classCache = new ClassCache<Script>(NailMain.class.getClassLoader(), RubyInstanceConfig.JIT_MAX_METHODS_LIMIT);
     }
     public static void nailMain(NGContext context) {
         NailMain main = new NailMain();
         int status = main.run(context);
         if (status != 0) {
             context.exit(status);
         }
         // force a full GC so objects aren't kept alive longer than they should
         System.gc();
     }
 
     public int run(NGContext context) {
         context.assertLoopbackClient();
 
-        // FIXME: This is almost entirely duplicated from Main.java
         RubyInstanceConfig config = new RubyInstanceConfig();
         Main main = new Main(config);
         
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
     }
 }
\ No newline at end of file
