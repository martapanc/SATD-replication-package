diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index 3ac0786b89..98ba4474f9 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,1187 +1,1193 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.LoadService;
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
     private static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     private static final int JIT_MAX_SIZE_LIMIT = Integer.MAX_VALUE;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     private static final int JIT_THRESHOLD = 50;
     
     /** The version to use for generated classes. Set to current JVM version by default */
     public static final int JAVA_VERSION;
     
     /**
      * Default size for chained compilation.
      */
     private static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = 500;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT
             = SafePropertyAccessor.getInt("jruby.compile.chainsize", CHAINED_COMPILE_LINE_COUNT_DEFAULT);
 
     public enum CompileMode {
         JIT, FORCE, OFF;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
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
     private final boolean jitLoggingVerbose;
     private final int jitLogEvery;
     private final int jitThreshold;
     private final int jitMax;
     private final int jitMaxSize;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
 
     private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
     private ClassLoader loader = contextLoader == null ? RubyInstanceConfig.class.getClassLoader() : contextLoader;
 
     private ClassCache<Script> classCache;
 
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private List<String> requiredLibraries = new ArrayList<String>();
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
     private boolean showCopyright = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private boolean yarv = false;
     private boolean rubinius = false;
     private boolean yarvCompile = false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = true;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
     public static final boolean BOXED_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.boxed");
     public static final boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops");
     public static final boolean FRAMELESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static final boolean POSITIONLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless");
     public static final boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static final boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
     public static final boolean INDEXED_METHODS
             = SafePropertyAccessor.getBoolean("jruby.indexed.methods");
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
 
+    public static boolean FULL_TRACE_ENABLED
+            = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
+
     public static boolean nativeEnabled = true;
     
 
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
                 public LoadService create(Ruby runtime) {
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
 
     public RubyInstanceConfig() {
         if (Ruby.isSecurityRestricted())
             currentDirectory = "/";
         else {
             currentDirectory = JRubyFile.getFileProperty("user.dir");
         }
 
         samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
         String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             compatVersion = CompatVersion.RUBY1_8;
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             compatVersion = CompatVersion.RUBY1_9;
         } else {
             System.err.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             compatVersion = CompatVersion.RUBY1_8;
         }
 
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
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
 
             managementEnabled = SafePropertyAccessor.getBoolean("jruby.management.enabled", true);
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
                 .append("  -0[octal]       specify record separator (\0, if no argument)\n")
                 .append("  -a              autosplit mode with -n or -p (splits $_ into $F)\n")
                 .append("  -b              benchmark mode, times the script execution\n")
                 .append("  -c              check syntax only\n")
                 .append("  -Cdirectory     cd to directory, before executing your script\n")
                 .append("  -d              set debugging flags (set $DEBUG to true)\n")
                 .append("  -e 'command'    one line of script. Several -e's allowed. Omit [programfile]\n")
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 //.append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode\n")
                 .append("  -l              enable line ending processing\n")
                 .append("  -n              assume 'while gets(); ... end' loop around your script\n")
                 .append("  -p              assume loop like -n but print line also like sed\n")
                 .append("  -rlibrary       require the library, before executing your script\n")
                 .append("  -s              enable some switch parsing for switches after script name\n")
                 .append("  -S              look for the script in bin or using PATH environment variable\n")
                 .append("  -T[level]       turn on tainting checks\n")
                 .append("  -v              print version number, then turn on verbose mode\n")
                 .append("  -w              turn warnings on for your script\n")
                 .append("  -W[level]       set warning level; 0=silence, 1=medium, 2=verbose (default)\n")
                 //.append("  -x[directory]   strip off text before #!ruby line and perhaps cd to directory\n")
                 .append("  -X[option]      enable extended option (omit option to list)\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties (pass -J-Dproperty=value)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --client        use the non-optimizing \"client\" JVM (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM and JRuby\n")
                 .append("  --1.8           specify Ruby 1.8.x compatibility (default)\n")
                 .append("  --1.9           specify Ruby 1.9.x compatibility\n")
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
                 .append("  +C              force compilation of all scripts before they are run (except eval)\n")
                 .append("  -y              read a YARV-compiled Ruby script and run that (EXPERIMENTAL)\n")
                 .append("  -Y              compile a Ruby script into YARV bytecodes and run this (EXPERIMENTAL)\n")
                 .append("  -R              read a Rubinius-compiled Ruby script and run that (EXPERIMENTAL)\n");
 
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
                 .append("    jruby.compile.boxed=true|false\n")
                 .append("       (EXPERIMENTAL) Use boxed variables; this can speed up some methods. Default is false\n")
                 .append("    jruby.compile.frameless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible\n")
                 .append("    jruby.compile.positionless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation that avoids updating Ruby position info. Default is false\n")
                 .append("    jruby.compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    jruby.compile.fastops=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast operators for Fixnum. Default is false\n")
                 .append("    jruby.compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is " + CHAINED_COMPILE_LINE_COUNT_DEFAULT + "\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jruby.jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is " + JIT_THRESHOLD + ".\n")
                 .append("    jruby.jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is " + JIT_MAX_METHODS_LIMIT + " per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jruby.jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is Integer.MAX_VALUE\n")
                 .append("    jruby.jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jruby.jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jruby.jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    jruby.native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    jruby.native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("    jruby.fork.enabled=true|false\n")
                 .append("       (EXPERIMENTAL, maybe dangerous) Enable fork(2) on platforms that support it.\n")
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
                 .append("    jruby.indexed.methods=true|false\n")
                 .append("       Generate \"invokers\" for core classes using a single indexed class\n")
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
-                .append("       Set whether JMX management is enabled. Default is true.\n");
+                .append("       Set whether JMX management is enabled. Default is true.\n")
+                .append("    jruby.debug.fullTrace=true|false\n")
+                .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = Constants.RUBY_VERSION;
         switch (compatVersion) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s patchlevel %s) (%s rev %s) [%s-java]\n",
                 Constants.VERSION, ver, Constants.RUBY_PATCHLEVEL,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2008 The JRuby Community (and contribs)\n";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
     }
 
     public CompileMode getCompileMode() {
         return compileMode;
     }
 
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public int getJitLogEvery() {
         return jitLogEvery;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
 
     public int getJitThreshold() {
         return jitThreshold;
     }
 
     public int getJitMax() {
         return jitMax;
     }
 
     public int getJitMaxSize() {
         return jitMaxSize;
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
             if (Ruby.isSecurityRestricted()) {
                 return "SECURITY RESTRICTED";
             }
             jrubyHome = verifyHome(SafePropertyAccessor.getProperty("jruby.home",
                     SafePropertyAccessor.getProperty("user.home") + "/.jruby"));
 
             try {
                 // This comment also in rbConfigLibrary
                 // Our shell scripts pass in non-canonicalized paths, but even if we didn't
                 // anyone who did would become unhappy because Ruby apps expect no relative
                 // operators in the pathname (rubygems, for example).
                 jrubyHome = new NormalizedFile(jrubyHome).getCanonicalPath();
             } catch (IOException e) { }
 
             jrubyHome = new NormalizedFile(jrubyHome).getAbsolutePath();
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = System.getProperty("user.dir");
         }
         if (!home.startsWith("file:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             f.mkdirs();
         }
         return home;
     }
 
     private class ArgumentProcessor {
         private String[] arguments;
         private int argumentIndex = 0;
 
         public ArgumentProcessor(String[] arguments) {
             this.arguments = arguments;
         }
 
         public void processArguments() {
             while (argumentIndex < arguments.length && isInterpreterArgument(arguments[argumentIndex])) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript && scriptFileName == null) {
                 if (argumentIndex < arguments.length) {
                     setScriptFileName(arguments[argumentIndex]); //consume the file name
                     argumentIndex++;
                 }
             }
 
             processArgv();
         }
 
         private void processArgv() {
             List<String> arglist = new ArrayList<String>();
             for (; argumentIndex < arguments.length; argumentIndex++) {
                 String arg = arguments[argumentIndex];
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
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private String getArgumentError(String additionalError) {
             return "jruby: invalid argument\n" + additionalError + "\n";
         }
 
         private void processArgument() {
             String argument = arguments[argumentIndex];
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
                             mee.setUsageError(true);
                             throw mee;
                         }
                     } catch (IOException e) {
                         MainExitException mee = new MainExitException(1, getArgumentError(" -C must be followed by a valid directory"));
                         mee.setUsageError(true);
                         throw mee;
                     }
                     break;
                 case 'd':
                     debug = true;
                     verbose = Boolean.TRUE;
                     break;
                 case 'e':
                     inlineScript.append(grabValue(getArgumentError(" -e must be followed by an expression to evaluate")));
                     inlineScript.append('\n');
                     hasInlineScript = true;
                     break FOR;
                 case 'F':
                     inputFieldSeparator = grabValue(getArgumentError(" -F must be followed by a pattern for input field separation"));
                     break;
                 case 'h':
                     shouldPrintUsage = true;
                     shouldRunInterpreter = false;
                     break;
                 // FIXME: -i flag not supported
 //                    case 'i' :
 //                        break;
                 case 'I':
                     String s = grabValue(getArgumentError("-I must be followed by a directory name to add to lib path"));
                     String[] ls = s.split(java.io.File.pathSeparator);
                     for (int i = 0; i < ls.length; i++) {
                         loadPaths.add(ls[i]);
                     }
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
                 // FIXME: -x flag not supported
 //                    case 'x' :
 //                        break;
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
                     } else if (extendedOption.equals("+C")) {
                         compileMode = CompileMode.FORCE;
                     } else if (extendedOption.equals("y")) {
                         yarv = true;
                     } else if (extendedOption.equals("Y")) {
                         yarvCompile = true;
                     } else if (extendedOption.equals("R")) {
                         rubinius = true;
                     } else {
                         MainExitException mee =
                                 new MainExitException(1, "jruby: invalid extended option " + extendedOption + " (-X will list valid options)\n");
                         mee.setUsageError(true);
 
                         throw mee;
                     }
                     break FOR;
                 case '-':
                     if (argument.equals("--command") || argument.equals("--bin")) {
                         characterIndex = argument.length();
                         runBinScript();
                         break;
                     } else if (argument.equals("--compat")) {
                         characterIndex = argument.length();
                         compatVersion = CompatVersion.getVersionFromString(grabValue(getArgumentError("--compat must be RUBY1_8 or RUBY1_9")));
                         if (compatVersion == null) {
                             compatVersion = CompatVersion.RUBY1_8;
                         }
                         break FOR;
                     } else if (argument.equals("--copyright")) {
                         setShowCopyright(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--debug")) {
                         compileMode = CompileMode.OFF;
+                        FULL_TRACE_ENABLED = true;
                         System.setProperty("jruby.reflection", "true");
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
 
         private void runBinScript() {
             String scriptName = grabValue("jruby: provide a bin script to execute");
             if (scriptName.equals("irb")) {
                 scriptName = "jirb";
             }
 
             scriptFileName = scriptName;
 
             if (!new File(scriptFileName).exists()) {
                 try {
                     String jrubyHome = JRubyFile.create(System.getProperty("user.dir"), JRubyFile.getFileProperty("jruby.home")).getCanonicalPath();
                     scriptFileName = JRubyFile.create(jrubyHome + JRubyFile.separator + "bin", scriptName).getCanonicalPath();
                 } catch (IOException io) {
                     MainExitException mee = new MainExitException(1, "jruby: Can't determine script filename");
                     mee.setUsageError(true);
                     throw mee;
                 }
             }
 
             // route 'gem' through ruby code in case we're running out of the complete jar
             if (scriptName.equals("gem") || !new File(scriptFileName).exists()) {
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands." + scriptName);
                 inlineScript.append("\n");
                 hasInlineScript = true;
             }
             endOfArguments = true;
         }
 
         private String grabValue(String errorMessage) {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             argumentIndex++;
             if (argumentIndex < arguments.length) {
                 return arguments[argumentIndex];
             }
 
             MainExitException mee = new MainExitException(1, errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
 
         private String grabOptionalValue() {
             characterIndex++;
             if (characterIndex < arguments[argumentIndex].length()) {
                 return arguments[argumentIndex].substring(characterIndex);
             }
             return null;
         }
     }
 
     public byte[] inlineScript() {
         try {
             return inlineScript.toString().getBytes("UTF-8");
         } catch (UnsupportedEncodingException e) {
             return inlineScript.toString().getBytes();
         }
     }
 
     public List<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         if(isShowVersion() && (hasInlineScript || scriptFileName != null)) {
             return true;
         }
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
                 File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                 return new BufferedInputStream(new FileInputStream(file));
             }
         } catch (IOException e) {
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
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
 
     private void setScriptFileName(String scriptFileName) {
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
 
     public boolean isShowVersion() {
         return showVersion;
     }
 
     public boolean isShowCopyright() {
         return showCopyright;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
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
 
     public boolean isYARVEnabled() {
         return yarv;
     }
 
     public String getInputFieldSeparator() {
         return inputFieldSeparator;
     }
 
     public boolean isRubiniusEnabled() {
         return rubinius;
     }
 
     public boolean isYARVCompileEnabled() {
         return yarvCompile;
     }
 
     public KCode getKCode() {
         return kcode;
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
 
     public void setClassCache(ClassCache classCache) {
         this.classCache = classCache;
     }
 
     public Map getOptionGlobals() {
         return optionGlobals;
     }
     
     public boolean isManagementEnabled() {
         return managementEnabled;
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 2b9820f894..6e3eeb21c0 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1645 +1,1624 @@
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
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyConstant;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.anno.TypePopulator;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.Dispatcher;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ClassProvider;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Module")
 public class RubyModule extends RubyObject {
     private static final boolean DEBUG = false;
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyModule;
             }
         };
         
         moduleClass.defineAnnotatedMethods(RubyModule.class);
         moduleClass.defineAnnotatedMethods(ModuleKernelMethods.class);
 
         return moduleClass;
     }
     
     public static class ModuleKernelMethods {
         @JRubyMethod
         public static IRubyObject autoload(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return RubyKernel.autoload(recv, arg0, arg1);
         }
         
         @JRubyMethod(name = "autoload?")
         public static IRubyObject autoload_p(IRubyObject recv, IRubyObject arg0) {
             return RubyKernel.autoload_p(recv, arg0);
         }
     }
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }    
     
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
     
     public Dispatcher dispatcher = Dispatcher.DEFAULT_DISPATCHER;
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
     
     public boolean isInstance(IRubyObject object) {
         return kindOf.isKindOf(object, this);
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     public RubyModule parent;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     protected String classId;
 
 
     // CONSTANT TABLE
     
     // Lock used for variableTable/constantTable writes. The RubyObject variableTable
     // write methods are overridden here to use this lock rather than Java
     // synchronization for faster concurrent writes for modules/classes.
     protected final ReentrantLock variableWriteLock = new ReentrantLock();
     
     protected transient volatile ConstantTableEntry[] constantTable =
         new ConstantTableEntry[CONSTANT_TABLE_DEFAULT_CAPACITY];
 
     protected transient int constantTableSize;
 
     protected transient int constantTableThreshold = 
         (int)(CONSTANT_TABLE_DEFAULT_CAPACITY * CONSTANT_TABLE_LOAD_FACTOR);
 
     private final Map<String, DynamicMethod> methods = new ConcurrentHashMap<String, DynamicMethod>(12, 0.75f, 1);
     
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax. 
     private transient List<ClassProvider> classProviders;
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = runtime.allocModuleId();
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (classProviders == null) {
             List<ClassProvider> cp = Collections.synchronizedList(new ArrayList<ClassProvider>());
             cp.add(provider);
             classProviders = cp;
         } else {
             synchronized(classProviders) {
                 if (!classProviders.contains(provider)) {
                     classProviders.add(provider);
                 }
             }
         }
     }
 
     public void removeClassProvider(ClassProvider provider) {
         if (classProviders != null) {
             classProviders.remove(provider);
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyClass clazz;
                 for (ClassProvider classProvider: classProviders) {
                     if ((clazz = classProvider.defineClassUnder(this, name, superClazz)) != null) {
                         return clazz;
                     }
                 }
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyModule module;
                 for (ClassProvider classProvider: classProviders) {
                     if ((module = classProvider.defineModuleUnder(this, name)) != null) {
                         return module;
                     }
                 }
             }
         }
         return null;
     }
 
     public Dispatcher getDispatcher() {
         return dispatcher;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
 
     public Map<String, DynamicMethod> getMethods() {
         return methods;
     }
     
 
     // note that addMethod now does its own put, so any change made to
     // functionality here should be made there as well 
     private void putMethod(String name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         dispatcher.clearIndex(MethodIndex.getIndex(name));
         getMethods().put(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
     
     private volatile String bareName;
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (bareName == null) {
                 if (isClass()) {
                     bareName = "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
                 } else {
                     bareName = "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
                 }
             }
 
             return bareName;
         }
 
         String result = getBaseName();
         RubyClass objectClass = getRuntime().getObject();
 
         // TODO: maybe, we should cache the whole calculation:
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result = pName + "::" + result;
         }
 
         return result;
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         doIncludeModule(module);
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if(Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal = getRuntime().getNil();
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             }
         } catch(Exception e) {}
 
         
         for(String name : names) {
             this.fastSetConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         if (RubyInstanceConfig.INDEXED_METHODS) {
             defineAnnotatedMethodsIndexed(clazz);
         } else {
             defineAnnotatedMethodsIndividually(clazz);
         }
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         
         public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == CompatVersion.RUBY1_8) {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == CompatVersion.RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
                 
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         String x = clazz.getSimpleName();
-        int a = 1 + 1;
-        try {
-            String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
-            
-            if (DEBUG) System.out.println("looking for " + qualifiedName + "$Populator");
-            
-            Class populatorClass = Class.forName(qualifiedName + "$Populator");
-            TypePopulator populator = (TypePopulator)populatorClass.newInstance();
-            populator.populate(this);
-        } catch (Throwable t) {
-            if (DEBUG) System.out.println("Could not find it!");
-            // fallback on non-pregenerated logic
-            MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
-            
-            MethodClumper clumper = new MethodClumper();
-            clumper.clump(clazz);
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
-            }
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
-            }
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
-            }
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
-            }
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
-            }
-            
-            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
-                defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+        TypePopulator populator = null;
+        
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            // we need full traces, use default (slow) populator
+            if (DEBUG) System.out.println("trace mode, using default populator");
+            populator = TypePopulator.DEFAULT;
+        } else {
+            try {
+                String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
+
+                if (DEBUG) System.out.println("looking for " + qualifiedName + "$Populator");
+
+                Class populatorClass = Class.forName(qualifiedName + "$Populator");
+                populator = (TypePopulator)populatorClass.newInstance();
+            } catch (Throwable t) {
+                if (DEBUG) System.out.println("Could not find it, using default populator");
+                populator = TypePopulator.DEFAULT;
             }
         }
         
+        populator.populate(this, clazz);
     }
     
     private void defineAnnotatedMethodsIndexed(Class clazz) {
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         methodFactory.defineIndexedAnnotatedMethods(this, clazz, methodDefiningCallback);
     }
     
     private static MethodFactory.MethodDefiningCallback methodDefiningCallback = new MethodFactory.MethodDefiningCallback() {
         public void define(RubyModule module, JavaMethodDescriptor desc, DynamicMethod dynamicMethod) {
             JRubyMethod jrubyMethod = desc.anno;
             if (jrubyMethod.frame()) {
                 for (String name : jrubyMethod.name()) {
                     ASTInspector.FRAME_AWARE_METHODS.add(name);
                 }
             }
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 RubyModule singletonClass;
 
                 if (jrubyMethod.meta()) {
                     singletonClass = module.getSingletonClass();
                     dynamicMethod.setImplementationClass(singletonClass);
 
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         singletonClass.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             singletonClass.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             singletonClass.defineAlias(alias, baseName);
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         module.addMethod(baseName, dynamicMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             module.addMethod(name, dynamicMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             module.defineAlias(alias, baseName);
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         singletonClass = module.getSingletonClass();
                         // module/singleton methods are all defined public
                         DynamicMethod moduleMethod = dynamicMethod.dup();
                         moduleMethod.setVisibility(PUBLIC);
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = desc.name;
                             singletonClass.addMethod(desc.name, moduleMethod);
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 singletonClass.addMethod(name, moduleMethod);
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 singletonClass.defineAlias(alias, baseName);
                             }
                         }
                     }
                 }
             }
         }
     };
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             methodDefiningCallback.define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == CompatVersion.BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             methodDefiningCallback.define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_undefined", getRuntime().newSymbol(name));
         }
     }
     
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(IRubyObject arg) {
         if (!arg.isModule()) {
             throw getRuntime().newTypeError(arg, getRuntime().getModule());
         }
         
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if ((p instanceof IncludedModuleWrapper) && ((IncludedModuleWrapper) p).getNonIncludedClass() == arg) {
                 return getRuntime().newBoolean(true);
             }
         }
         
         return getRuntime().newBoolean(false);
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = getMethods().put(name, method);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(existingMethod);
             }
             // note: duplicating functionality from putMethod, since we
             // remove/put atomically here
             dispatcher.clearIndex(MethodIndex.getIndex(name));
         }
     }
 
     public void removeMethod(ThreadContext context, String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
             
             getRuntime().getCacheMap().remove(method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", getRuntime().newSymbol(name));
         } else {
             callMethod(context, "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // See if current class has method or if it has been cached here already
             DynamicMethod method = searchModule.retrieveMethod(name);
 
             if (method != null) {
                 return method;
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         DynamicMethod oldMethod = searchMethod(name);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         cacheMap.remove(oldMethod);
         if (oldMethod != oldMethod.getRealMethod()) {
             cacheMap.remove(oldMethod.getRealMethod());
         }
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         CacheMap cacheMap = runtime.getCacheMap();
         cacheMap.remove(method);
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
             DynamicMethod oldMethod = searchMethod(name);
             cacheMap.remove(oldMethod);
             if (oldMethod != oldMethod.getRealMethod()) {
                 cacheMap.remove(oldMethod.getRealMethod());
             }
             putMethod(name, new AliasMethod(this, method, oldName));
         }
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAt(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAt(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     // FIXME: create AttrReaderMethod, AttrWriterMethod, for faster attr access
     private void addAccessor(ThreadContext context, String internedName, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = getRuntime();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = context.getCurrentVisibility();
         if (attributeScope == PRIVATE) {
             //FIXME warning
         } else if (attributeScope == MODULE_FUNCTION) {
             attributeScope = PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(runtime, args.length, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariables().fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             // FIXME: should visibility be set to current visibility?
             addMethod(internedName, new JavaMethod(this, PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     if (args.length != 1) Arity.raiseArgumentError(runtime, args.length, 1, 1);
 
                     return self.getInstanceVariables().fastSetInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, name, originModule, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, name, originModule, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     @JRubyMethod(name = "define_method", frame = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, Block block) {
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         RubyProc proc = getRuntime().newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getPreviousVisibility(), context, context.getRuntime());
 
         return proc;
     }
     @JRubyMethod(name = "define_method", frame = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         if (getRuntime().getProc().isInstance(arg1)) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (RubyProc)arg1;
             body = proc;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (getRuntime().getMethod().isInstance(arg1)) {
             RubyMethod method = (RubyMethod)arg1;
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getPreviousVisibility(), context, context.getRuntime());
 
         return body;
     }
     @Deprecated
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1:
             return define_method(context, args[0], block);
         case 2:
             return define_method(context, args[0], args[1], block);
         default:
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 2)");
         }
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         Block block = proc.getBlock();
         block.getBinding().getFrame().setKlazz(this);
         block.getBinding().getFrame().setName(name);
         
         StaticScope scope = block.getBody().getStaticScope();
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         scope.setArgumentScope(true);
 
         Arity arity = block.arity();
         // just using required is broken...but no more broken than before zsuper refactoring
         scope.setRequiredArgs(arity.required());
 
         if(!arity.isFixed()) {
             scope.setRestArg(arity.required());
         }
 
         return new ProcMethod(this, proc, visibility);
     }
 
     @Deprecated
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method instanceof UndefinedMethod) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
 
         if (originalModule.hasVariables()){
             syncVariables(originalModule.getVariableList());
         }
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuilder buffer = new StringBuilder("#<Class:");
             if (attached != null) { // FIXME: figure out why we get null sometimes
                 if(attached instanceof RubyClass || attached instanceof RubyModule){
                     buffer.append(attached.inspect());
                 }else{
                     buffer.append(attached.anyToString());
                 }
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     @JRubyMethod(name = "===", required = 1)
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(isInstance(obj));
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze() {
         to_s();
         return super.freeze();
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) {
             return getRuntime().getTrue();
         } else if (((RubyModule) obj).isKindOfModule(this)) {
             return getRuntime().getFalse();
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) return getRuntime().newFixnum(0);
         if (!(obj instanceof RubyModule)) return getRuntime().getNil();
 
         RubyModule module = (RubyModule) obj;
 
         if (module.isKindOfModule(this)) {
             return getRuntime().newFixnum(1);
         } else if (this.isKindOfModule(module)) {
             return getRuntime().newFixnum(-1);
         }
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.isSame(type)) {
                 return true;
             }
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(PUBLIC);
             block.yield(getRuntime().getCurrentContext(), this, this, this, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(context, args[0].asJavaString().intern(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * @deprecated
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_accessor(ThreadContext context, IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided 
diff --git a/src/org/jruby/anno/AnnotationBinder.java b/src/org/jruby/anno/AnnotationBinder.java
index 46736409a7..a1157ef22a 100644
--- a/src/org/jruby/anno/AnnotationBinder.java
+++ b/src/org/jruby/anno/AnnotationBinder.java
@@ -1,491 +1,491 @@
 package org.jruby.anno;
 
 import com.sun.mirror.apt.*;
 import com.sun.mirror.declaration.*;
 import com.sun.mirror.util.*;
 
 import java.io.ByteArrayOutputStream;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Set;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.CompatVersion;
 
 import org.jruby.util.CodegenUtils;
 import static java.util.Collections.*;
 import static com.sun.mirror.util.DeclarationVisitors.*;
 
 /*
  * This class is used to run an annotation processor that lists class
  * names.  The functionality of the processor is analogous to the
  * ListClass doclet in the Doclet Overview.
  */
 public class AnnotationBinder implements AnnotationProcessorFactory {
     // Process any set of annotations
     private static final Collection<String> supportedAnnotations = unmodifiableCollection(Arrays.asList("org.jruby.anno.JRubyMethod", "org.jruby.anno.JRubyClass"));    // No supported options
     private static final Collection<String> supportedOptions = emptySet();
 
     public Collection<String> supportedAnnotationTypes() {
         return supportedAnnotations;
     }
 
     public Collection<String> supportedOptions() {
         return supportedOptions;
     }
 
     public AnnotationProcessor getProcessorFor(
             Set<AnnotationTypeDeclaration> atds,
             AnnotationProcessorEnvironment env) {
         return new AnnotationBindingProcessor(env);
     }
 
     private static class AnnotationBindingProcessor implements AnnotationProcessor {
 
         private final AnnotationProcessorEnvironment env;
         private final List<String> classNames = new ArrayList<String>();
 
         AnnotationBindingProcessor(AnnotationProcessorEnvironment env) {
             this.env = env;
         }
 
         public void process() {
             for (TypeDeclaration typeDecl : env.getSpecifiedTypeDeclarations()) {
                 typeDecl.accept(getDeclarationScanner(new RubyClassVisitor(),
                         NO_OP));
             }
             try {
                 FileWriter fw = new FileWriter("src_gen/annotated_classes.txt");
                 for (String name : classNames) {
                     fw.write(name);
                     fw.write('\n');
                 }
                 fw.close();
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         }
 
         private class RubyClassVisitor extends SimpleDeclarationVisitor {
 
             private PrintStream out;
             private static final boolean DEBUG = false;
 
             @Override
             public void visitClassDeclaration(ClassDeclaration cd) {
                 try {
                     String qualifiedName = cd.getQualifiedName().replace('.', '$');
 
                     // skip anything not related to jruby
                     if (!qualifiedName.contains("org$jruby")) {
                         return;
                     }
                     ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
                     out = new PrintStream(bytes);
 
                     // start a new populator
                     out.println("/* THIS FILE IS GENERATED. DO NOT EDIT */");
                     out.println("package org.jruby.gen;");
 
                     out.println("import org.jruby.RubyModule;");
                     out.println("import org.jruby.RubyClass;");
                     out.println("import org.jruby.CompatVersion;");
                     out.println("import org.jruby.anno.TypePopulator;");
                     out.println("import org.jruby.internal.runtime.methods.CallConfiguration;");
                     out.println("import org.jruby.internal.runtime.methods.JavaMethod;");
                     out.println("import org.jruby.internal.runtime.methods.DynamicMethod;");
                     out.println("import org.jruby.runtime.Arity;");
                     out.println("import org.jruby.runtime.Visibility;");
                     out.println("import org.jruby.compiler.ASTInspector;");
 
                     out.println("public class " + qualifiedName + "$Populator extends TypePopulator {");
-                    out.println("    public void populate(RubyModule cls) {");
+                    out.println("    public void populate(RubyModule cls, Class clazz) {");
                     if (DEBUG) {
                         out.println("        System.out.println(\"Using pregenerated populator: \" + \"" + cd.getSimpleName() + "Populator\");");
                     }
                     out.println("        JavaMethod javaMethod;");
                     out.println("        DynamicMethod moduleMethod;");
                     out.println("        RubyClass metaClass;");
                     out.println("        CompatVersion compatVersion = cls.getRuntime().getInstanceConfig().getCompatVersion();");
 
                     Map<String, List<MethodDeclaration>> annotatedMethods = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_8 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_8 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_9 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_9 = new HashMap<String, List<MethodDeclaration>>();
 
                     List<String> frameAwareMethods = new ArrayList<String>();
 
                     int methodCount = 0;
                     for (MethodDeclaration md : cd.getMethods()) {
                         JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                         if (anno == null) {
                             continue;
                         }
                         methodCount++;
 
                         String name = anno.name().length == 0 ? md.getSimpleName() : anno.name()[0];
 
                         List<MethodDeclaration> methodDescs;
                         Map<String, List<MethodDeclaration>> methodsHash = null;
                         if (md.getModifiers().contains(Modifier.STATIC)) {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = staticAnnotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = staticAnnotatedMethods1_9;
                             } else {
                                 methodsHash = staticAnnotatedMethods;
                             }
                         } else {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = annotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = annotatedMethods1_9;
                             } else {
                                 methodsHash = annotatedMethods;
                             }
                         }
 
                         methodDescs = methodsHash.get(name);
                         if (methodDescs == null) {
                             methodDescs = new ArrayList<MethodDeclaration>();
                             methodsHash.put(name, methodDescs);
                         }
 
                         methodDescs.add(md);
 
                         if (anno.frame() || (anno.reads() != null && anno.reads().length >= 1) || (anno.writes() != null && anno.writes().length >= 1)) {
                             frameAwareMethods.add(name);
                         }
                     }
 
                     if (methodCount == 0) {
                         // no annotated methods found, skip
                         return;
                     }
 
                     classNames.add(getActualQualifiedName(cd));
 
                     processMethodDeclarations(staticAnnotatedMethods);
 
                     if (!staticAnnotatedMethods1_8.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(staticAnnotatedMethods1_8);
                         out.println("        }");
                     }
 
                     if (!staticAnnotatedMethods1_9.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(staticAnnotatedMethods1_9);
                         out.println("        }");
                     }
 
                     processMethodDeclarations(annotatedMethods);
 
                     if (!annotatedMethods1_8.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(annotatedMethods1_8);
                         out.println("        }");
                     }
 
                     if (!annotatedMethods1_9.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(annotatedMethods1_9);
                         out.println("        }");
                     }
 
                     for (String name : frameAwareMethods) {
                         out.println("        ASTInspector.FRAME_AWARE_METHODS.add(\"" + name + "\");");
                     }
 
                     out.println("    }");
                     out.println("}");
                     out.close();
                     out = null;
 
                     FileOutputStream fos = new FileOutputStream("src_gen/" + qualifiedName + "$Populator.java");
                     fos.write(bytes.toByteArray());
                     fos.close();
                 } catch (IOException ioe) {
                     System.err.println("FAILED TO GENERATE:");
                     ioe.printStackTrace();
                     System.exit(1);
                 }
             }
 
             public void processMethodDeclarations(Map<String, List<MethodDeclaration>> declarations) {
                 for (Map.Entry<String, List<MethodDeclaration>> entry : declarations.entrySet()) {
                     List<MethodDeclaration> list = entry.getValue();
 
                     if (list.size() == 1) {
                         // single method, use normal logic
                         processMethodDeclaration(list.get(0));
                     } else {
                         // multimethod, new logic
                         processMethodDeclarationMulti(list.get(0));
                     }
                 }
             }
 
             public void processMethodDeclaration(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
                     String qualifiedName = getActualQualifiedName(md.getDeclaringType());
 
                     boolean hasContext = false;
                     boolean hasBlock = false;
 
                     for (ParameterDeclaration pd : md.getParameters()) {
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
 
                     int actualRequired = calculateActualRequired(md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
 
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             false);
                     String implClass = anno.meta() ? "cls.getSingletonClass()" : "cls";
 
                     out.println("        javaMethod = new " + annotatedBindingName + "(" + implClass + ", Visibility." + anno.visibility() + ");");
                     out.println("        populateMethod(javaMethod, " +
                             getArityValue(anno, actualRequired) + ", \"" +
                             md.getSimpleName() + "\", " +
                             isStatic + ", " +
                             "CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
 
             public void processMethodDeclarationMulti(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
                     String qualifiedName = getActualQualifiedName(md.getDeclaringType());
 
                     boolean hasContext = false;
                     boolean hasBlock = false;
 
                     for (ParameterDeclaration pd : md.getParameters()) {
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
 
                     int actualRequired = calculateActualRequired(md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
 
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             true);
                     String implClass = anno.meta() ? "cls.getSingletonClass()" : "cls";
 
                     out.println("        javaMethod = new " + annotatedBindingName + "(" + implClass + ", Visibility." + anno.visibility() + ");");
                     out.println("        populateMethod(javaMethod, " +
                             "-1, \"" +
                             md.getSimpleName() + "\", " +
                             isStatic + ", " +
                             "CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
 
             private String getActualQualifiedName(TypeDeclaration td) {
                 // declared type returns the qualified name without $ for inner classes!!!
                 String qualifiedName;
                 if (td.getDeclaringType() != null) {
                     // inner class, use $ to delimit
                     if (td.getDeclaringType().getDeclaringType() != null) {
                         qualifiedName = td.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + td.getDeclaringType().getSimpleName() + "$" + td.getSimpleName();
                     } else {
                         qualifiedName = td.getDeclaringType().getQualifiedName() + "$" + td.getSimpleName();
                     }
                 } else {
                     qualifiedName = td.getQualifiedName();
                 }
 
                 return qualifiedName;
             }
 
             private boolean tryClass(String className) {
                 Class tryClass = null;
                 try {
                     tryClass = Class.forName(className);
                 } catch (ClassNotFoundException cnfe) {
                 }
 
                 return tryClass != null;
             }
 
             private int calculateActualRequired(int paramsLength, int optional, boolean rest, boolean isStatic, boolean hasContext, boolean hasBlock) {
                 int actualRequired;
                 if (optional == 0 && !rest) {
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) {
                             args--;
                         }
                         if (hasContext) {
                             args--;
                         }
                         if (hasBlock) {
                             args--;                        // TODO: confirm expected args are IRubyObject (or similar)
                         }
                         actualRequired = args;
                     }
                 } else {
                     // optional args, so we have IRubyObject[]
                     // TODO: confirm
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) {
                             args--;
                         }
                         if (hasContext) {
                             args--;
                         }
                         if (hasBlock) {
                             args--;                        // minus one more for IRubyObject[]
                         }
                         args--;
 
                         // TODO: confirm expected args are IRubyObject (or similar)
                         actualRequired = args;
                     }
 
                     if (actualRequired != 0) {
                         throw new RuntimeException("Combining specific args with IRubyObject[] is not yet supported");
                     }
                 }
 
                 return actualRequired;
             }
 
             public void generateMethodAddCalls(MethodDeclaration md, JRubyMethod jrubyMethod) {
                 // TODO: This information
                 if (jrubyMethod.frame()) {
                     for (String name : jrubyMethod.name()) {
                         out.println("        ASTInspector.FRAME_AWARE_METHODS.add(\"" + name + "\");");
                     }
                 }
                 // TODO: compat version
                 //                if(jrubyMethod.compat() == CompatVersion.BOTH ||
                 //                        module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
                 //RubyModule metaClass = module.metaClass;
 
                 if (jrubyMethod.meta()) {
                     out.println("        metaClass = cls.getMetaClass();");
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = md.getSimpleName();
                         out.println("        metaClass.addMethod(\"" + baseName + "\", javaMethod);");
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             out.println("        metaClass.addMethod(\"" + name + "\", javaMethod);");
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             out.println("        metaClass.defineAlias(\"" + alias + "\", \"" + baseName + "\");");
                         }
                     }
                 } else {
                     String baseName;
                     if (jrubyMethod.name().length == 0) {
                         baseName = md.getSimpleName();
                         out.println("        cls.addMethod(\"" + baseName + "\", javaMethod);");
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             out.println("        cls.addMethod(\"" + name + "\", javaMethod);");
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             out.println("        cls.defineAlias(\"" + alias + "\", \"" + baseName + "\");");
                         }
                     }
 
                     if (jrubyMethod.module()) {
                         out.println("        metaClass = cls.getSingletonClass();");
                         // module/singleton methods are all defined public
                         out.println("        moduleMethod = javaMethod.dup();");
                         out.println("        moduleMethod.setImplementationClass(cls.getSingletonClass());");
                         out.println("        moduleMethod.setVisibility(Visibility.PUBLIC);");
 
                         //                        RubyModule singletonClass = module.getSingletonClass();
 
                         if (jrubyMethod.name().length == 0) {
                             baseName = md.getSimpleName();
                             out.println("        metaClass.addMethod(\"" + baseName + "\", moduleMethod);");
                         } else {
                             baseName = jrubyMethod.name()[0];
                             for (String name : jrubyMethod.name()) {
                                 out.println("        metaClass.addMethod(\"" + name + "\", moduleMethod);");
                             }
                         }
 
                         if (jrubyMethod.alias().length > 0) {
                             for (String alias : jrubyMethod.alias()) {
                                 out.println("        metaClass.defineAlias(\"" + alias + "\", \"" + baseName + "\");");
                             }
                         }
                     }
                 }
             //                }
             }
         }
 
         public static int getArityValue(JRubyMethod anno, int actualRequired) {
             if (anno.optional() > 0 || anno.rest()) {
                 return -(actualRequired + 1);
             }
             return actualRequired;
         }
 
         public static String getCallConfigNameByAnno(JRubyMethod anno) {
             return getCallConfigName(anno.frame(), anno.scope(), anno.backtrace());
         }
 
         public static String getCallConfigName(boolean frame, boolean scope, boolean backtrace) {
             if (frame) {
                 if (scope) {
                     return "FRAME_AND_SCOPE";
                 } else {
                     return "FRAME_ONLY";
                 }
             } else if (scope) {
                 if (backtrace) {
                     return "BACKTRACE_AND_SCOPE";
                 } else {
                     return "SCOPE_ONLY";
                 }
             } else if (backtrace) {
                 return "BACKTRACE_ONLY";
             } else {
                 return "NO_FRAME_NO_SCOPE";
             }
         }
     }
 }
diff --git a/src/org/jruby/anno/TypePopulator.java b/src/org/jruby/anno/TypePopulator.java
index 9fd2ca43a6..83c560fab3 100644
--- a/src/org/jruby/anno/TypePopulator.java
+++ b/src/org/jruby/anno/TypePopulator.java
@@ -1,26 +1,64 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.anno;
 
+import java.util.List;
+import java.util.Map;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.MethodFactory;
 
 /**
  *
  * @author headius
  */
 public abstract class TypePopulator {
     public void populateMethod(JavaMethod javaMethod, int arity, String simpleName, boolean isStatic, CallConfiguration callConfig) {
         javaMethod.setArity(Arity.createArity(arity));
         javaMethod.setJavaName(simpleName);
         javaMethod.setSingleton(isStatic);
         javaMethod.setCallConfig(callConfig);
     }
     
-    public abstract void populate(RubyModule clsmod);
+    public abstract void populate(RubyModule clsmod, Class clazz);
+    
+    public static final TypePopulator DEFAULT = new DefaultTypePopulator();
+    public static class DefaultTypePopulator extends TypePopulator {
+        public void populate(RubyModule clsmod, Class clazz) {
+            // fallback on non-pregenerated logic
+            MethodFactory methodFactory = MethodFactory.createFactory(clsmod.getRuntime().getJRubyClassLoader());
+            
+            RubyModule.MethodClumper clumper = new RubyModule.MethodClumper();
+            clumper.clump(clazz);
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+            
+            for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
+                clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
+            }
+        }
+    }
 }
diff --git a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
index 4062eaf0b7..f687fe15e1 100644
--- a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
@@ -1,1423 +1,1461 @@
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
  * Copyright (C) 2006 The JRuby Community <www.jruby.org>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 package org.jruby.internal.runtime.methods;
 
 import java.io.PrintWriter;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.parser.StaticScope;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import static org.jruby.util.CodegenUtils.*;
 import static java.lang.System.*;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  * In order to avoid the overhead with reflection-based method handles, this
  * MethodFactory uses ASM to generate tiny invoker classes. This allows for
  * better performance and more specialization per-handle than can be supported
  * via reflection. It also allows optimizing away many conditionals that can
  * be determined once ahead of time.
  * 
  * When running in secured environments, this factory may not function. When
  * this can be detected, MethodFactory will fall back on the reflection-based
  * factory instead.
  * 
  * @see org.jruby.internal.runtime.methods.MethodFactory
  */
 public class InvocationMethodFactory extends MethodFactory implements Opcodes {
     private static final boolean DEBUG = false;
     
     /** The pathname of the super class for compiled Ruby method handles. */ 
     private final static String COMPILED_SUPER_CLASS = p(CompiledMethod.class);
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class));
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
     
     /** The super constructor signature for Java-based method handles. */
     private final static String JAVA_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class));
     
     /** The super constructor signature for indexed Java-based method handles. */
     private final static String JAVA_INDEXED_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class, int.class));
     
     /** The lvar index of "this" */
     public static final int THIS_INDEX = 0;
     
     /** The lvar index of the passed-in ThreadContext */
     public static final int THREADCONTEXT_INDEX = 1;
     
     /** The lvar index of the method-receiving object */
     public static final int RECEIVER_INDEX = 2;
     
     /** The lvar index of the RubyClass being invoked against */
     public static final int CLASS_INDEX = 3;
     
     /** The lvar index method name being invoked */
     public static final int NAME_INDEX = 4;
     
     /** The lvar index of the method args on the call */
     public static final int ARGS_INDEX = 5;
     
     /** The lvar index of the passed-in Block on the call */
     public static final int BLOCK_INDEX = 6;
 
     /** The classloader to use for code loading */
     protected JRubyClassLoader classLoader;
     
     /**
      * Whether this factory has seen undefined methods already. This is used to
      * detect likely method handle collisions when we expect to create a new
      * handle for each call.
      */
     private boolean seenUndefinedClasses = false;
     
     /**
      * Construct a new InvocationMethodFactory using the specified classloader
      * to load code. If the target classloader is not an instance of
      * JRubyClassLoader, it will be wrapped with one.
      * 
      * @param classLoader The classloader to use, or to wrap if it is not a
      * JRubyClassLoader instance.
      */
     public InvocationMethodFactory(ClassLoader classLoader) {
         if (classLoader instanceof JRubyClassLoader) {
             this.classLoader = (JRubyClassLoader)classLoader;
         } else {
            this.classLoader = new JRubyClassLoader(classLoader);
         }
     }
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethodLazily(
             RubyModule implementationClass, String method, Arity arity, 
             Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         return new CompiledMethod.LazyCompiledMethod(implementationClass, method, arity, visibility, scope, scriptObject, callConfig, this);
     }
             
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, Arity arity, 
             Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         String sup = COMPILED_SUPER_CLASS;
         Class scriptClass = scriptObject.getClass();
         String mname = scriptClass.getName() + "Invoker" + method + arity;
         synchronized (classLoader) {
             Class generatedClass = tryClass(mname);
 
             try {
                 if (generatedClass == null) {
                     String typePath = p(scriptClass);
                     String mnamePath = typePath + "Invoker" + method + arity;
                     ClassWriter cw = createCompiledCtor(mnamePath,sup);
                     SkinnyMethodAdapter mv = null;
                     String signature = null;
                     boolean specificArity = false;
                     
                     if (scope.getRestArg() >= 0 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3) {
                         signature = COMPILED_CALL_SIG_BLOCK;
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", signature, null, null));
                     } else {
                         specificArity = true;
                         
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null));
                         mv.start();
                         
                         // check arity
                         mv.aload(1);
                         mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                         if (scope.getRequiredArgs() == 0) {
                             mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
                         } else {
                             mv.aload(5);
                         }
                         mv.pushInt(scope.getRequiredArgs());
                         mv.pushInt(scope.getRequiredArgs());
                         mv.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
                         mv.pop();
                         
                         mv.aload(0);
                         mv.aload(1);
                         mv.aload(2);
                         mv.aload(3);
                         mv.aload(4);
                         for (int i = 0; i < scope.getRequiredArgs(); i++) {
                             mv.aload(5);
                             mv.ldc(i);
                             mv.arrayload();
                         }
                         mv.aload(6);
 
                         switch (scope.getRequiredArgs()) {
                         case 0:
                             signature = COMPILED_CALL_SIG_ZERO_BLOCK;
                             break;
                         case 1:
                             signature = COMPILED_CALL_SIG_ONE_BLOCK;
                             break;
                         case 2:
                             signature = COMPILED_CALL_SIG_TWO_BLOCK;
                             break;
                         case 3:
                             signature = COMPILED_CALL_SIG_THREE_BLOCK;
                             break;
                         }
                         
                         mv.invokevirtual(mnamePath, "call", signature);
                         mv.areturn();
                         mv.end();
                         
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", signature, null, null));
                     }
 
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
                     
 //                    // check arity
 //                    checkArity(mv, scope);
 
                     // invoke pre method stuff
                     if (!callConfig.isNoop()) {
                         if (specificArity) {
                             invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, scope.getRequiredArgs(), true, callConfig);
                         } else {
                             invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, -1, true, callConfig);
                         }
                     }
 
                     Label tryBegin = new Label();
                     Label tryEnd = new Label();
                     Label doFinally = new Label();
                     Label doReturnFinally = new Label();
                     Label doRedoFinally = new Label();
                     Label catchReturnJump = new Label();
                     Label catchRedoJump = new Label();
 
                     if (callConfig != CallConfiguration.FRAME_ONLY) {
                         mv.trycatch(tryBegin, tryEnd, catchReturnJump, p(JumpException.ReturnJump.class));
                     }
                     mv.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
                     mv.trycatch(tryBegin, tryEnd, doFinally, null);
                     if (callConfig != CallConfiguration.FRAME_ONLY) {
                         mv.trycatch(catchReturnJump, doReturnFinally, doFinally, null);
                     }
                     mv.trycatch(catchRedoJump, doRedoFinally, doFinally, null);
                     mv.label(tryBegin);
                     {
                         mv.aload(0);
                         // FIXME we want to eliminate these type casts when possible
                         mv.getfield(mnamePath, "$scriptObject", ci(Object.class));
                         mv.checkcast(typePath);
                         mv.aload(THREADCONTEXT_INDEX);
                         mv.aload(RECEIVER_INDEX);
                         if (specificArity) {
                             for (int i = 0; i < scope.getRequiredArgs(); i++) {
                                 mv.aload(ARGS_INDEX + i);
                             }
                             mv.aload(ARGS_INDEX + scope.getRequiredArgs());
                             mv.invokevirtual(typePath, method, StandardASMCompiler.METHOD_SIGNATURES[scope.getRequiredArgs()]);
                         } else {
                             mv.aload(ARGS_INDEX);
                             mv.aload(BLOCK_INDEX);
                             mv.invokevirtual(typePath, method, StandardASMCompiler.METHOD_SIGNATURES[4]);
                         }
                     }
                     mv.label(tryEnd);
                     
                     // normal exit, perform finally and return
                     {
                         if (!callConfig.isNoop()) {
                             invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                         }
                         mv.visitInsn(ARETURN);
                     }
 
                     // return jump handling
                     if (callConfig != CallConfiguration.FRAME_ONLY) {
                         mv.label(catchReturnJump);
                         {
                             mv.aload(0);
                             mv.swap();
                             mv.invokevirtual(COMPILED_SUPER_CLASS, "handleReturnJump", sig(IRubyObject.class, JumpException.ReturnJump.class));
                             mv.label(doReturnFinally);
 
                             // finally
                             if (!callConfig.isNoop()) {
                                 invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                             }
 
                             // return result if we're still good
                             mv.areturn();
                         }
                     }
 
                     // redo jump handling
                     mv.label(catchRedoJump);
                     {
                         // clear the redo
                         mv.pop();
                         
                         // get runtime, create jump error, and throw it
                         mv.aload(1);
                         mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                         mv.invokevirtual(p(Ruby.class), "newRedoLocalJumpError", sig(RaiseException.class));
                         mv.label(doRedoFinally);
                         
                         // finally
                         if (!callConfig.isNoop()) {
                             invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                         }
                         
                         // throw redo error if we're still good
                         mv.athrow();
                     }
 
                     // finally handling for abnormal exit
                     {
                         mv.label(doFinally);
 
                         //call post method stuff (exception raised)
                         if (!callConfig.isNoop()) {
                             invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                         }
 
                         // rethrow exception
                         mv.athrow(); // rethrow it
                     }
 
                     generatedClass = endCall(cw,mv,mname);
                 }
 
                 CompiledMethod compiledMethod = (CompiledMethod)generatedClass.newInstance();
                 compiledMethod.init(implementationClass, arity, visibility, scope, scriptObject, callConfig);
                 return compiledMethod;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> descs) {
         JavaMethodDescriptor desc1 = descs.get(0);
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
-        String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, true);
-        String generatedClassPath = generatedClassName.replace('.', '/');
-        
         synchronized (classLoader) {
-
             try {
                 Class c = getAnnotatedMethodClass(descs);
                 int min = Integer.MAX_VALUE;
                 int max = 0;
                 boolean frame = false;
                 boolean scope = false;
                 boolean backtrace = false;
 
                 for (JavaMethodDescriptor desc: descs) {
                     int specificArity = -1;
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             if (desc.actualRequired <= 3) {
                                 specificArity = desc.actualRequired;
                             } else {
                                 specificArity = -1;
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             specificArity = desc.required;
                         }
                     }
 
                     if (specificArity < min) {
                         min = specificArity;
                     }
 
                     if (specificArity > max) {
                         max = specificArity;
                     }
                     
                     frame |= desc.anno.frame();
                     scope |= desc.anno.scope();
                     backtrace |= desc.anno.backtrace();
                 }
 
                 if (DEBUG) out.println(" min: " + min + ", max: " + max);
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc1.anno.visibility()});
 
                 ic.setArity(Arity.OPTIONAL);
                 ic.setJavaName(javaMethodName);
                 ic.setSingleton(desc1.isStatic);
                 ic.setCallConfig(CallConfiguration.getCallConfig(frame, scope, backtrace));
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method. Return the resulting generated or loaded class.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public Class getAnnotatedMethodClass(List<JavaMethodDescriptor> descs) throws Exception {
         if (descs.size() == 1) {
             // simple path, no multimethod
             return getAnnotatedMethodClass(descs.get(0));
         }
+        
         JavaMethodDescriptor desc1 = descs.get(0);
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, true);
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            // in debug mode we append _DBG to class name to force it to regenerate (or use pre-generated debug version)
+            generatedClassName += "_DBG";
+        }
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             int min = Integer.MAX_VALUE;
             int max = 0;
             boolean block = false;
 
             for (JavaMethodDescriptor desc: descs) {
                 int specificArity = -1;
                 if (desc.optional == 0 && !desc.rest) {
                     if (desc.required == 0) {
                         if (desc.actualRequired <= 3) {
                             specificArity = desc.actualRequired;
                         } else {
                             specificArity = -1;
                         }
                     } else if (desc.required >= 0 && desc.required <= 3) {
                         specificArity = desc.required;
                     }
                 }
 
                 if (specificArity < min) {
                     min = specificArity;
                 }
 
                 if (specificArity > max) {
                     max = specificArity;
                 }
 
                 block |= desc.hasBlock;
             }
 
             if (DEBUG) out.println(" min: " + min + ", max: " + max + ", hasBlock: " + block);
 
             if (c == null) {
                 String superClass = null;
                 switch (min) {
                 case 0:
                     switch (max) {
                     case 1:
                         if (block) {
                             superClass = p(JavaMethod.JavaMethodZeroOrOneBlock.class);
                         } else {
                             superClass = p(JavaMethod.JavaMethodZeroOrOne.class);
                         }
                         break;
                     case 2:
                         if (block) {
                             superClass = p(JavaMethod.JavaMethodZeroOrOneOrTwoBlock.class);
                         } else {
                             superClass = p(JavaMethod.JavaMethodZeroOrOneOrTwo.class);
                         }
                         break;
                     case 3:
                         if (block) {
                             superClass = p(JavaMethod.JavaMethodZeroOrOneOrTwoOrThreeBlock.class);
                         } else {
                             superClass = p(JavaMethod.JavaMethodZeroOrOneOrTwoOrThree.class);
                         }
                         break;
                     }
                     break;
                 case 1:
                     switch (max) {
                     case 2:
                         if (block) {
                             superClass = p(JavaMethod.JavaMethodOneOrTwoBlock.class);
                         } else {
                             superClass = p(JavaMethod.JavaMethodOneOrTwo.class);
                         }
                         break;
                     case 3:
                         superClass = p(JavaMethod.JavaMethodOneOrTwoOrThree.class);
                         break;
                     }
                     break;
                 case 2:
                     switch (max) {
                     case 3:
                         superClass = p(JavaMethod.JavaMethodTwoOrThree.class);
                         break;
                     }
                     break;
                 case -1:
                     // rest arg, use normal JavaMethod since N case will be defined
                     superClass = p(JavaMethod.JavaMethodNoBlock.class);
                     break;
                 }
                 if (superClass == null) throw new RuntimeException("invalid multi combination");
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, superClass);
 
                 for (JavaMethodDescriptor desc: descs) {
                     int specificArity = -1;
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             if (desc.actualRequired <= 3) {
                                 specificArity = desc.actualRequired;
                             } else {
                                 specificArity = -1;
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             specificArity = desc.required;
                         }
                     }
 
                     boolean hasBlock = desc.hasBlock;
                     SkinnyMethodAdapter mv = null;
 
                     mv = beginMethod(cw, "call", specificArity, hasBlock);
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
 
                     createAnnotatedMethodInvocation(desc, mv, superClass, specificArity, hasBlock);
 
                     endMethod(mv);
                 }
 
                 c = endClass(cw, generatedClassName);
             }
 
             return c;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc.declaringClassName, desc.isStatic, desc.actualRequired, desc.optional, false);
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             try {
                 Class c = getAnnotatedMethodClass(desc);
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc.anno.visibility()});
 
                 ic.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
                 ic.setJavaName(javaMethodName);
                 ic.setSingleton(desc.isStatic);
                 ic.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public Class getAnnotatedMethodClass(JavaMethodDescriptor desc) throws Exception {
         String javaMethodName = desc.name;
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc.declaringClassName, desc.isStatic, desc.actualRequired, desc.optional, false);
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            // in debug mode we append _DBG to class name to force it to regenerate (or use pre-generated debug version)
+            generatedClassName += "_DBG";
+        }
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             if (c == null) {
                 int specificArity = -1;
                 if (desc.optional == 0 && !desc.rest) {
                     if (desc.required == 0) {
                         if (desc.actualRequired <= 3) {
                             specificArity = desc.actualRequired;
                         } else {
                             specificArity = -1;
                         }
                     } else if (desc.required >= 0 && desc.required <= 3) {
                         specificArity = desc.required;
                     }
                 }
 
                 boolean block = desc.hasBlock;
 
                 String superClass = p(selectSuperClass(specificArity, block));
 
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, superClass);
                 SkinnyMethodAdapter mv = null;
 
                 mv = beginMethod(cw, "call", specificArity, block);
                 mv.visitCode();
                 Label line = new Label();
                 mv.visitLineNumber(0, line);
 
                 createAnnotatedMethodInvocation(desc, mv, superClass, specificArity, block);
 
                 endMethod(mv);
 
                 c = endClass(cw, generatedClassName);
             }
             
             return c;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public void prepareAnnotatedMethod(RubyModule implementationClass, JavaMethod javaMethod, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         javaMethod.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
         javaMethod.setJavaName(javaMethodName);
         javaMethod.setSingleton(desc.isStatic);
         javaMethod.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
     }
 
     /**
      * Use code generation to generate a set of method handles based on all
      * annotated methods in the target class.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#defineIndexedAnnotatedMethods
      */
     public void defineIndexedAnnotatedMethods(RubyModule implementationClass, Class type, MethodDefiningCallback callback) {
         String typePath = p(type);
         String superClass = p(JavaMethod.class);
         
         String generatedClassName = type.getName() + "Invoker";
         String generatedClassPath = typePath + "Invoker";
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName);
 
             try {
                 ArrayList<Method> annotatedMethods = new ArrayList();
                 Method[] methods = type.getDeclaredMethods();
                 for (Method method : methods) {
                     JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
                     if (jrubyMethod == null) continue;
 
                     annotatedMethods.add(method);
                 }
                 // To ensure the method cases are generated the same way every time, we make a second sorted list
                 ArrayList<Method> sortedMethods = new ArrayList(annotatedMethods);
                 Collections.sort(sortedMethods, new Comparator<Method>() {
                     public int compare(Method a, Method b) {
                         return a.getName().compareTo(b.getName());
                     }
                 });
                 // But when binding the methods, we want to use the order from the original class, so we save the indices
                 HashMap<Method,Integer> indexMap = new HashMap();
                 for (int index = 0; index < sortedMethods.size(); index++) {
                     indexMap.put(sortedMethods.get(index), index);
                 }
 
                 if (c == null) {
                     ClassWriter cw = createIndexedJavaMethodCtor(generatedClassPath, superClass);
                     SkinnyMethodAdapter mv = null;
 
                     mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null));
                     mv.visitCode();
                     Label line = new Label();
                     mv.visitLineNumber(0, line);
 
                     Label defaultCase = new Label();
                     Label[] cases = new Label[sortedMethods.size()];
                     for (int i = 0; i < cases.length; i++) cases[i] = new Label();
 
                     // load method index
                     mv.aload(THIS_INDEX);
                     mv.getfield(generatedClassPath, "methodIndex", ci(int.class));
 
                     mv.tableswitch(0, cases.length - 1, defaultCase, cases);
 
                     for (int i = 0; i < sortedMethods.size(); i++) {
                         mv.label(cases[i]);
                         String callName = getAnnotatedMethodForIndex(cw, sortedMethods.get(i), i, superClass);
 
                         // invoke call#_method for method
                         mv.aload(THIS_INDEX);
                         mv.aload(THREADCONTEXT_INDEX);
                         mv.aload(RECEIVER_INDEX);
                         mv.aload(CLASS_INDEX);
                         mv.aload(NAME_INDEX);
                         mv.aload(ARGS_INDEX);
                         mv.aload(BLOCK_INDEX);
 
                         mv.invokevirtual(generatedClassPath, callName, COMPILED_CALL_SIG_BLOCK);
                         mv.areturn();
                     }
 
                     // if we fall off the switch, error.
                     mv.label(defaultCase);
                     mv.aload(THREADCONTEXT_INDEX);
                     mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                     mv.ldc("Error: fell off switched invoker for class: " + implementationClass.getBaseName());
                     mv.invokevirtual(p(Ruby.class), "newRuntimeError", sig(RaiseException.class, String.class));
                     mv.athrow();
 
                     c = endCall(cw, mv, generatedClassName);
                 }
 
                 for (int i = 0; i < annotatedMethods.size(); i++) {
                     Method method = annotatedMethods.get(i);
                     JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
                     if (jrubyMethod.frame()) {
                         for (String name : jrubyMethod.name()) {
                             ASTInspector.FRAME_AWARE_METHODS.add(name);
                         }
                     }
 
                     int index = indexMap.get(method);
                     JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class, int.class}).newInstance(new Object[]{implementationClass, jrubyMethod.visibility(), index});
 
                     ic.setArity(Arity.fromAnnotation(jrubyMethod));
                     ic.setJavaName(method.getName());
                     ic.setSingleton(Modifier.isStatic(method.getModifiers()));
                     ic.setCallConfig(CallConfiguration.getCallConfigByAnno(jrubyMethod));
 
                     callback.define(implementationClass, new JavaMethodDescriptor(method), ic);
                 }
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Emit code to check the arity of a call to a Ruby-based method.
      * 
      * @param jrubyMethod The annotation of the called method
      * @param method The code generator for the handle being created
      */
     private void checkArity(SkinnyMethodAdapter method, StaticScope scope) {
         Label arityError = new Label();
         Label noArityError = new Label();
 
         if (scope.getRestArg() >= 0) {
             if (scope.getRequiredArgs() > 0) {
                 // just confirm minimum args provided
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(scope.getRequiredArgs());
                 method.if_icmplt(arityError);
             }
         } else if (scope.getOptionalArgs() > 0) {
             if (scope.getRequiredArgs() > 0) {
                 // confirm minimum args provided
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(scope.getRequiredArgs());
                 method.if_icmplt(arityError);
             }
 
             // confirm maximum not greater than optional
             method.aload(ARGS_INDEX);
             method.arraylength();
             method.ldc(scope.getRequiredArgs() + scope.getOptionalArgs());
             method.if_icmpgt(arityError);
         } else {
             // just confirm args length == required
             method.aload(ARGS_INDEX);
             method.arraylength();
             method.ldc(scope.getRequiredArgs());
             method.if_icmpne(arityError);
         }
 
         method.go_to(noArityError);
 
         // Raise an error if arity does not match requirements
         method.label(arityError);
         method.aload(THREADCONTEXT_INDEX);
         method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
         method.aload(ARGS_INDEX);
         method.ldc(scope.getRequiredArgs());
         method.ldc(scope.getRequiredArgs() + scope.getOptionalArgs());
         method.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
         method.pop();
 
         method.label(noArityError);
     }
 
     /**
      * Emit code to check the arity of a call to a Java-based method.
      * 
      * @param jrubyMethod The annotation of the called method
      * @param method The code generator for the handle being created
      */
     private void checkArity(JRubyMethod jrubyMethod, SkinnyMethodAdapter method, int specificArity) {
         Label arityError = new Label();
         Label noArityError = new Label();
         
         switch (specificArity) {
         case 0:
         case 1:
         case 2:
         case 3:
             // for zero, one, two, three arities, JavaMethod.JavaMethod*.call(...IRubyObject[] args...) will check
             return;
         default:
             if (jrubyMethod.rest()) {
                 if (jrubyMethod.required() > 0) {
                     // just confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                 }
             } else if (jrubyMethod.optional() > 0) {
                 if (jrubyMethod.required() > 0) {
                     // confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                 }
 
                 // confirm maximum not greater than optional
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required() + jrubyMethod.optional());
                 method.if_icmpgt(arityError);
             } else {
                 // just confirm args length == required
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required());
                 method.if_icmpne(arityError);
             }
 
             method.go_to(noArityError);
 
             // Raise an error if arity does not match requirements
             method.label(arityError);
             method.aload(THREADCONTEXT_INDEX);
             method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             method.aload(ARGS_INDEX);
             method.ldc(jrubyMethod.required());
             method.ldc(jrubyMethod.required() + jrubyMethod.optional());
             method.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
             method.pop();
 
             method.label(noArityError);
         }
     }
 
     private ClassWriter createCompiledCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", "()V");
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createJavaMethodCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", JAVA_SUPER_SIG, null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_SUPER_SIG);
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createIndexedJavaMethodCtor(String namePath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", JAVA_INDEXED_SUPER_SIG, null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitVarInsn(ILOAD, 3);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_INDEXED_SUPER_SIG);
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(0,0);
         mv.visitEnd();
         return cw;
     }
 
     private void handleRedo(Label tryRedoJump, SkinnyMethodAdapter mv, Label tryFinally) {
         mv.label(tryRedoJump);
 
         // clear the redo
         mv.pop();
 
         // get runtime, dup it
         mv.aload(1);
         mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
         mv.invokevirtual(p(Ruby.class), "newRedoLocalJumpError", sig(RaiseException.class));
         mv.go_to(tryFinally);
     }
 
     private void handleReturn(Label catchReturnJump, SkinnyMethodAdapter mv, Label doFinally, Label normalExit, String typePath) {
         mv.label(catchReturnJump);
 
         mv.aload(0);
         mv.swap();
         mv.invokevirtual(typePath, "handleReturnJump", sig(IRubyObject.class, JumpException.ReturnJump.class));
 
         mv.astore(8);
         mv.go_to(normalExit);
     }
 
     private void invokeCallConfigPost(SkinnyMethodAdapter mv, String superClass, CallConfiguration callConfig) {
         if (callConfig != CallConfiguration.NO_FRAME_NO_SCOPE) {
             mv.aload(0);
             mv.aload(1);
             if (callConfig == CallConfiguration.FRAME_AND_SCOPE) {
                 mv.invokevirtual(superClass, "postFrameAndScope", sig(void.class, params(ThreadContext.class)));
             } else if (callConfig == CallConfiguration.FRAME_ONLY) {
                 mv.invokevirtual(superClass, "postFrameOnly", sig(void.class, params(ThreadContext.class)));
             } else if (callConfig == CallConfiguration.SCOPE_ONLY) {
                 mv.invokevirtual(superClass, "postScopeOnly", sig(void.class, params(ThreadContext.class)));
             } else if (callConfig == CallConfiguration.BACKTRACE_ONLY) {
                 mv.invokevirtual(superClass, "postBacktraceOnly", sig(void.class, params(ThreadContext.class)));
             } else if (callConfig == CallConfiguration.BACKTRACE_AND_SCOPE) {
                 mv.invokevirtual(superClass, "postBacktraceAndScope", sig(void.class, params(ThreadContext.class)));
             }
         }
     }
 
     private void invokeCallConfigPre(SkinnyMethodAdapter mv, String superClass, int specificArity, boolean block, CallConfiguration callConfig) {
         // invoke pre method stuff
         if (callConfig != CallConfiguration.NO_FRAME_NO_SCOPE) {
             mv.aload(0); 
             mv.aload(THREADCONTEXT_INDEX); // tc
 
 
             if (callConfig == CallConfiguration.FRAME_AND_SCOPE) {
                 mv.aload(RECEIVER_INDEX); // self
                 mv.aload(NAME_INDEX); // name
                 loadBlockForPre(mv, specificArity, block);
                 mv.invokevirtual(superClass, "preFrameAndScope", sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class)));
             } else if (callConfig == CallConfiguration.FRAME_ONLY) {
                 mv.aload(RECEIVER_INDEX); // self
                 mv.aload(NAME_INDEX); // name
                 loadBlockForPre(mv, specificArity, block);
                 mv.invokevirtual(superClass, "preFrameOnly", sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class)));
             } else if (callConfig == CallConfiguration.SCOPE_ONLY) {
                 mv.invokevirtual(superClass, "preScopeOnly", sig(void.class, params(ThreadContext.class)));
             } else if (callConfig == CallConfiguration.BACKTRACE_ONLY) {
                 mv.aload(NAME_INDEX); // name
                 mv.invokevirtual(superClass, "preBacktraceOnly", sig(void.class, params(ThreadContext.class, String.class)));
             } else if (callConfig == CallConfiguration.BACKTRACE_AND_SCOPE) {
                 mv.aload(NAME_INDEX); // name
                 mv.invokevirtual(superClass, "preBacktraceAndScope", sig(void.class, params(ThreadContext.class, String.class)));
             }
         }
     }
 
     private void loadArguments(SkinnyMethodAdapter mv, JRubyMethod jrubyMethod, int specificArity) {
         switch (specificArity) {
         default:
         case -1:
             mv.aload(ARGS_INDEX);
             break;
         case 0:
             // no args
             break;
         case 1:
             mv.aload(ARGS_INDEX);
             break;
         case 2:
             mv.aload(ARGS_INDEX);
             mv.aload(ARGS_INDEX + 1);
             break;
         case 3:
             mv.aload(ARGS_INDEX);
             mv.aload(ARGS_INDEX + 1);
             mv.aload(ARGS_INDEX + 2);
             break;
         }
     }
 
     private void loadBlockForPre(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         switch (specificArity) {
         default:
         case -1:
             if (getsBlock) {
                 // variable args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // variable args no block, load null block
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 0:
             if (getsBlock) {
                 // zero args with block
                 // FIXME: omit args index; subtract one from normal block index
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX - 1);
             } else {
                 // zero args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 1:
             if (getsBlock) {
                 // one arg with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // one arg, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 2:
             if (getsBlock) {
                 // two args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 1);
             } else {
                 // two args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         case 3:
             if (getsBlock) {
                 // three args with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 2);
             } else {
                 // three args, no block; load NULL_BLOCK
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             }
             break;
         }
     }
 
     private void loadBlock(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         // load block if it accepts block
         switch (specificArity) {
         default:
         case -1:
             if (getsBlock) {
                 // all other arg cases with block
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // all other arg cases without block
             }
             break;
         case 0:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX - 1);
             } else {
                 // zero args, no block; do nothing
             }
             break;
         case 1:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX);
             } else {
                 // one arg, no block; do nothing
             }
             break;
         case 2:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 1);
             } else {
                 // two args, no block; do nothing
             }
             break;
         case 3:
             if (getsBlock) {
                 mv.visitVarInsn(ALOAD, BLOCK_INDEX + 2);
             } else {
                 // three args, no block; do nothing
             }
             break;
         }
     }
 
     private void loadReceiver(String typePath, JavaMethodDescriptor desc, SkinnyMethodAdapter mv) {
         // load target for invocations
         if (Modifier.isStatic(desc.modifiers)) {
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
             
             // load self object as IRubyObject, for recv param
             mv.aload(RECEIVER_INDEX);
         } else {
             // load receiver as original type for virtual invocation
             mv.aload(RECEIVER_INDEX);
             mv.checkcast(typePath);
             
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
         }
     }
     private Class tryClass(String name) {
         try {
             Class c = null;
             if (classLoader == null) {
                 c = Class.forName(name, true, classLoader);
             } else {
                 c = classLoader.loadClass(name);
             }
             
             if (c != null && seenUndefinedClasses) {
                 System.err.println("WARNING: while creating new bindings, found an existing binding; likely a collision: " + name);
                 Thread.dumpStack();
             }
             
             return c;
         } catch(Exception e) {
             seenUndefinedClasses = true;
             return null;
         }
     }
 
     protected Class endCall(ClassWriter cw, MethodVisitor mv, String name) {
         endMethod(mv);
         return endClass(cw, name);
     }
 
     protected void endMethod(MethodVisitor mv) {
         mv.visitMaxs(0,0);
         mv.visitEnd();
     }
 
     protected Class endClass(ClassWriter cw, String name) {
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         if (DEBUG) CheckClassAdapter.verify(new ClassReader(code), false, new PrintWriter(System.err));
          
         return classLoader.defineClass(name, code);
     }
     
     private void loadArgument(MethodVisitor mv, int argsIndex, int argIndex) {
         mv.visitVarInsn(ALOAD, argsIndex);
         mv.visitLdcInsn(new Integer(argIndex));
         mv.visitInsn(AALOAD);
     }
     
     private SkinnyMethodAdapter beginMethod(ClassWriter cw, String methodName, int specificArity, boolean block) {
         switch (specificArity) {
         default:
         case -1:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG, null, null));
             }
         case 0:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO, null, null));
             }
         case 1:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE, null, null));
             }
         case 2:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO, null, null));
             }
         case 3:
             if (block) {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE_BLOCK, null, null));
             } else {
                 return new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE, null, null));
             }
         }
     }
     
     private Class selectSuperClass(int specificArity, boolean block) {
         switch (specificArity) {
         default:
         case -1:
             if (block) {
                 return JavaMethod.class;
             } else {
                 return JavaMethod.JavaMethodNoBlock.class;
             }
         case 0:
             if (block) {
                 return JavaMethod.JavaMethodZeroBlock.class;
             } else {
                 return JavaMethod.JavaMethodZero.class;
             }
         case 1:
             if (block) {
                 return JavaMethod.JavaMethodOneBlock.class;
             } else {
                 return JavaMethod.JavaMethodOne.class;
             }
         case 2:
             if (block) {
                 return JavaMethod.JavaMethodTwoBlock.class;
             } else {
                 return JavaMethod.JavaMethodTwo.class;
             }
         case 3:
             if (block) {
                 return JavaMethod.JavaMethodThreeBlock.class;
             } else {
                 return JavaMethod.JavaMethodThree.class;
             }
         }
     }
 
     private String getAnnotatedMethodForIndex(ClassWriter cw, Method method, int index, String superClass) {
         String methodName = "call" + index + "_" + method.getName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, methodName, COMPILED_CALL_SIG_BLOCK, null, null));
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         // TODO: indexed methods do not use specific arity yet
         createAnnotatedMethodInvocation(new JavaMethodDescriptor(method), mv, superClass, -1, true);
         endMethod(mv);
         
         return methodName;
     }
 
     private void createAnnotatedMethodInvocation(JavaMethodDescriptor desc, SkinnyMethodAdapter method, String superClass, int specificArity, boolean block) {
         String typePath = desc.declaringClassPath;
         String javaMethodName = desc.name;
 
         checkArity(desc.anno, method, specificArity);
         
         CallConfiguration callConfig = CallConfiguration.getCallConfigByAnno(desc.anno);
         if (!callConfig.isNoop()) {
             invokeCallConfigPre(method, superClass, specificArity, block, callConfig);
         }
+        
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            invokeCCallTrace(method);
+        }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label doFinally = new Label();
         Label doRedoFinally = new Label();
         Label catchRedoJump = new Label();
 
         if (!callConfig.isNoop()) {
             // only need return/redo handling for block-receiving methods
             if (block) {
                 method.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
                 method.trycatch(catchRedoJump, doRedoFinally, doFinally, null);
             }
             method.trycatch(tryBegin, tryEnd, doFinally, null);
         }
         
         method.label(tryBegin);
         {
             loadReceiver(typePath, desc, method);
             
             loadArguments(method, desc.anno, specificArity);
             
             loadBlock(method, specificArity, block);
 
             if (Modifier.isStatic(desc.modifiers)) {
                 // static invocation
                 method.invokestatic(typePath, javaMethodName, desc.signature);
             } else {
                 // virtual invocation
                 method.invokevirtual(typePath, javaMethodName, desc.signature);
             }
         }
         method.label(tryEnd);
         
         // normal finally and exit
         {
+            if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                invokeCReturnTrace(method);
+            }
+            
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(method, superClass, callConfig);
             }
 
             // return
             method.visitInsn(ARETURN);
         }
         
         // these are only needed if we have a non-noop call config
         if (!callConfig.isNoop()) {
             // we only need redo jump handling if we've got a block, since it would only come from yields
             if (block) {
                 // redo jump handling
                 method.label(catchRedoJump);
                 {
                     // clear the redo
                     method.pop();
 
                     // get runtime, create jump error, and throw it
                     method.aload(1);
                     method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                     method.invokevirtual(p(Ruby.class), "newRedoLocalJumpError", sig(RaiseException.class));
 
-                    // finally
-                    method.label(doRedoFinally);
-                    if (!callConfig.isNoop()) {
-                        invokeCallConfigPost(method, superClass, callConfig);
+                    // finally logic
+                    {
+                        method.label(doRedoFinally);
+
+                        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                            invokeCReturnTrace(method);
+                        }
+
+                        if (!callConfig.isNoop()) {
+                            invokeCallConfigPost(method, superClass, callConfig);
+                        }
                     }
 
                     // throw redo error if we're still good
                     method.athrow();
                 }
             }
 
             // finally handling for abnormal exit
             {
                 method.label(doFinally);
+                
+                if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                    invokeCReturnTrace(method);
+                }
 
                 //call post method stuff (exception raised)
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(method, superClass, callConfig);
                 }
 
                 // rethrow exception
                 method.athrow(); // rethrow it
             }
         }
     }
+    
+    private void invokeCCallTrace(SkinnyMethodAdapter method) {
+        method.aload(0); // method itself
+        method.aload(1); // ThreadContext
+        method.aload(4); // invoked name
+        method.invokevirtual(p(JavaMethod.class), "callTrace", sig(void.class, ThreadContext.class, String.class));
+    }
+    
+    private void invokeCReturnTrace(SkinnyMethodAdapter method) {
+        method.aload(0); // method itself
+        method.aload(1); // ThreadContext
+        method.aload(4); // invoked name
+        method.invokevirtual(p(JavaMethod.class), "returnTrace", sig(void.class, ThreadContext.class, String.class));
+    }
 }
diff --git a/src/org/jruby/internal/runtime/methods/JavaMethod.java b/src/org/jruby/internal/runtime/methods/JavaMethod.java
index 98d205feb9..343287a3b3 100644
--- a/src/org/jruby/internal/runtime/methods/JavaMethod.java
+++ b/src/org/jruby/internal/runtime/methods/JavaMethod.java
@@ -1,666 +1,675 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
+import org.jruby.runtime.EventHook;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  */
 public abstract class JavaMethod extends DynamicMethod implements JumpTarget, Cloneable {
     protected int arityValue;
     protected Arity arity;
     private Class[] argumentTypes;
     private String javaName;
     private boolean isSingleton;
     protected StaticScope staticScope;
     
     public static abstract class JavaMethodNoBlock extends JavaMethod {
         public JavaMethodNoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodNoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodNoBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return call(context, self, clazz, name, args);
         }
     }
     
     public static abstract class JavaMethodZero extends JavaMethod {
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 0) throw context.getRuntime().newArgumentError(args.length, 0);
             return call(context, self, clazz, name);
         }
         
         public Arity getArity() {return Arity.NO_ARGUMENTS;}
     }
     
     public static abstract class JavaMethodZeroOrOne extends JavaMethod {
         public JavaMethodZeroOrOne(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOne(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOne(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name);
             case 1:
                 return call(context, self, clazz, name, args[0]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 1);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodZeroOrOneBlock extends JavaMethod {
         public JavaMethodZeroOrOneBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOneBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name, block);
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 1);
                 return null; // never reached
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwo extends JavaMethod {
         public JavaMethodZeroOrOneOrTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOneOrTwo(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name);
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 2);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodZeroOrOneOrTwoBlock extends JavaMethod {
         public JavaMethodZeroOrOneOrTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name, block);
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 2);
                 return null; // never reached
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrThree extends JavaMethod {
         public JavaMethodZeroOrOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name);
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 3);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodZeroOrOneOrTwoOrThreeBlock extends JavaMethod {
         public JavaMethodZeroOrOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroOrOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
 
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block);
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name, block);
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2], block);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 0, 3);
                 return null; // never reached
             }
         }
     }
 
     public static abstract class JavaMethodZeroBlock extends JavaMethod {
         public JavaMethodZeroBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodZeroBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 0) throw context.getRuntime().newArgumentError(args.length, 0);
             return call(context, self, clazz, name, block);
         }
         
         public Arity getArity() {return Arity.NO_ARGUMENTS;}
     }
     
     public static abstract class JavaMethodOne extends JavaMethod {
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 1) throw context.getRuntime().newArgumentError(args.length, 1);
             return call(context, self, clazz, name, args[0]);
         }
         
         public Arity getArity() {return Arity.ONE_ARGUMENT;}
     }
     
     public static abstract class JavaMethodOneOrTwo extends JavaMethod {
         public JavaMethodOneOrTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodOneOrTwo(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodOneOrTwoOrThree extends JavaMethod {
         public JavaMethodOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 3);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodOneBlock extends JavaMethod {
         public JavaMethodOneBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodOneBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 1) throw context.getRuntime().newArgumentError(args.length, 1);
             return call(context, self, clazz, name, args[0], block);
         }
         
         public Arity getArity() {return Arity.ONE_ARGUMENT;}
     }
     
     public static abstract class JavaMethodOneOrTwoBlock extends JavaMethod {
         public JavaMethodOneOrTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 1, 2);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodTwo extends JavaMethod {
         public JavaMethodTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodTwo(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             return call(context, self, clazz, name, args[0], args[1]);
         }
         
         public Arity getArity() {return Arity.TWO_ARGUMENTS;}
     }
     
     public static abstract class JavaMethodTwoOrThree extends JavaMethod {
         public JavaMethodTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodTwoOrThree(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2);
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2]);
             default:
                 Arity.raiseArgumentError(context.getRuntime(), args.length, 2, 3);
                 return null; // never reached
             }
         }
     }
     
     public static abstract class JavaMethodTwoBlock extends JavaMethod {
         public JavaMethodTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodTwoBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, Block block);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             return call(context, self, clazz, name, args[0], args[1], block);
         }
         
         public Arity getArity() {return Arity.TWO_ARGUMENTS;}
     }
     
     public static abstract class JavaMethodThree extends JavaMethod {
         public JavaMethodThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodThree(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             return call(context, self, clazz, name, args[0], args[1], args[2]);
         }
         
         public Arity getArity() {return Arity.THREE_ARGUMENTS;}
     }
     
     public static abstract class JavaMethodThreeBlock extends JavaMethod {
         public JavaMethodThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
             super(implementationClass, visibility, callConfig, staticScope, arity);
         }
         public JavaMethodThreeBlock(RubyModule implementationClass, Visibility visibility, int methodIndex) {
             super(implementationClass, visibility, methodIndex);
         }
         
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block);
         
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             return call(context, self, clazz, name, args[0], args[1], args[2], block);
         }
         
         public Arity getArity() {return Arity.THREE_ARGUMENTS;}
     }
 
     public JavaMethod(RubyModule implementationClass, Visibility visibility) {
         super(implementationClass, visibility, CallConfiguration.FRAME_ONLY);
         this.staticScope = null;
     }
 
     public JavaMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, StaticScope staticScope, Arity arity) {
         super(implementationClass, visibility, callConfig);
         this.staticScope = staticScope;
         this.arity = arity;
         this.arityValue = arity.getValue();
     }
 
     public JavaMethod(RubyModule implementationClass, Visibility visibility, int methodIndex) {
         super(implementationClass, visibility, CallConfiguration.FRAME_ONLY);
         this.staticScope = null;
     }
     
     protected JavaMethod() {}
     
     public void init(RubyModule implementationClass, Arity arity, Visibility visibility, StaticScope staticScope, CallConfiguration callConfig) {
         this.staticScope = staticScope;
         this.arity = arity;
         this.arityValue = arity.getValue();
         super.init(implementationClass, visibility, callConfig);
     }
 
     public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block);
     
     public DynamicMethod dup() {
         try {
             JavaMethod msm = (JavaMethod)clone();
             return msm;
         } catch (CloneNotSupportedException cnse) {
             return null;
         }
     }
     
     protected final void preFrameAndScope(ThreadContext context, IRubyObject self, String name, Block block) {
         context.preMethodFrameAndScope(implementationClass, name, self, block, staticScope, this);
     }
     
     protected final void preFrameOnly(ThreadContext context, IRubyObject self, String name, Block block) {
         context.preMethodFrameOnly(implementationClass, name, self, block, this);
     }
     
     protected final void preScopeOnly(ThreadContext context) {
         context.preMethodScopeOnly(implementationClass, staticScope);
     }
     
     protected final void preBacktraceOnly(ThreadContext context, String name) {
         context.preMethodBacktraceOnly(name);
     }
     
     protected final void preBacktraceAndScope(ThreadContext context, String name) {
         context.preMethodBacktraceAndScope(name, implementationClass, staticScope);
     }
     
     protected final void postFrameAndScope(ThreadContext context) {
         context.postMethodFrameAndScope();
     }
     
     protected final void postFrameOnly(ThreadContext context) {
         context.postMethodFrameOnly();
     }
     
     protected final void postScopeOnly(ThreadContext context) {
         context.postMethodScopeOnly();
     }
     
     protected final void postBacktraceOnly(ThreadContext context) {
         context.postMethodBacktraceOnly();
     }
     
     protected final void postBacktraceAndScope(ThreadContext context) {
         context.postMethodBacktraceAndScope();
     }
     
+    protected final void callTrace(ThreadContext context, String name) {
+        context.trace(EventHook.RUBY_EVENT_C_CALL, name, getImplementationClass());
+    }
+    
+    protected final void returnTrace(ThreadContext context, String name) {
+        context.trace(EventHook.RUBY_EVENT_C_CALL, name, getImplementationClass());
+    }
+    
     protected IRubyObject handleReturnJump(ReturnJump rj) {
         if (rj.getTarget() == this) {
             return (IRubyObject)rj.getValue();
         } else {
             throw rj;
         }
     }
     
     public void setArity(Arity arity) {
         this.arity = arity;
         this.arityValue = arity.getValue();
     }
 
     public Arity getArity() {
         return arity;
     }
     
     @Deprecated
     public void setArgumentTypes(Class[] argumentTypes) {
         this.argumentTypes = argumentTypes;
     }
     
     @Deprecated
     public Class[] getArgumentTypes() {
         return argumentTypes;   
     }
     
     public void setJavaName(String javaName) {
         this.javaName = javaName;
     }
     
     public String getJavaName() {
         return javaName;
     }
     
     public void setSingleton(boolean isSingleton) {
         this.isSingleton = isSingleton;
     }
     
     public boolean isSingleton() {
         return isSingleton;
     }
     
     @Override
     public boolean isNative() {
         return true;
     }
 }
diff --git a/src/org/jruby/management/Config.java b/src/org/jruby/management/Config.java
index 320d40ef3f..e8f43ffa6b 100644
--- a/src/org/jruby/management/Config.java
+++ b/src/org/jruby/management/Config.java
@@ -1,157 +1,170 @@
 package org.jruby.management;
 
 import java.util.Arrays;
 
 import org.jruby.Ruby;
+import org.jruby.RubyInstanceConfig;
 
 public class Config implements ConfigMBean {
     private Ruby ruby;
     
     public Config(Ruby ruby) {
         this.ruby = ruby;
     }
     
     public String getVersionString() {
         return ruby.getInstanceConfig().getVersionString();
     }
 
     public String getCopyrightString() {
         return ruby.getInstanceConfig().getCopyrightString();
     }
 
     public String getCompileMode() {
         return ruby.getInstanceConfig().getCompileMode().name();
     }
 
     public boolean isJitLogging() {
         return ruby.getInstanceConfig().isJitLogging();
     }
 
     public boolean isJitLoggingVerbose() {
         return ruby.getInstanceConfig().isJitLoggingVerbose();
     }
 
     public int getJitLogEvery() {
         return ruby.getInstanceConfig().getJitLogEvery();
     }
 
     public boolean isSamplingEnabled() {
         return ruby.getInstanceConfig().isSamplingEnabled();
     }
 
     public int getJitThreshold() {
         return ruby.getInstanceConfig().getJitThreshold();
     }
 
     public int getJitMax() {
         return ruby.getInstanceConfig().getJitMax();
     }
 
+    public int getJitMaxSize() {
+        return ruby.getInstanceConfig().getJitMaxSize();
+    }
+
     public boolean isRunRubyInProcess() {
         return ruby.getInstanceConfig().isRunRubyInProcess();
     }
 
     public String getCompatVersion() {
         return ruby.getInstanceConfig().getCompatVersion().name();
     }
 
     public String getCurrentDirectory() {
         return ruby.getInstanceConfig().getCurrentDirectory();
     }
 
     public boolean isObjectSpaceEnabled() {
         return ruby.getInstanceConfig().isObjectSpaceEnabled();
     }
 
     public String getEnvironment() {
         return ruby.getInstanceConfig().getEnvironment().toString();
     }
 
     public String getArgv() {
         return Arrays.deepToString(ruby.getInstanceConfig().getArgv());
     }
 
     public String getJRubyHome() {
         return ruby.getInstanceConfig().getJRubyHome();
     }
 
     public String getRequiredLibraries() {
         return ruby.getInstanceConfig().requiredLibraries().toString();
     }
 
     public String getLoadPaths() {
         return ruby.getInstanceConfig().loadPaths().toString();
     }
 
     public String getDisplayedFileName() {
         return ruby.getInstanceConfig().displayedFileName();
     }
 
     public String getScriptFileName() {
         return ruby.getInstanceConfig().getScriptFileName();
     }
 
     public boolean isBenchmarking() {
         return ruby.getInstanceConfig().isBenchmarking();
     }
 
     public boolean isAssumeLoop() {
         return ruby.getInstanceConfig().isAssumeLoop();
     }
 
     public boolean isAssumePrinting() {
         return ruby.getInstanceConfig().isAssumePrinting();
     }
 
     public boolean isProcessLineEnds() {
         return ruby.getInstanceConfig().isProcessLineEnds();
     }
 
     public boolean isSplit() {
         return ruby.getInstanceConfig().isSplit();
     }
 
     public boolean isVerbose() {
         return ruby.getInstanceConfig().isVerbose();
     }
 
     public boolean isDebug() {
         return ruby.getInstanceConfig().isDebug();
     }
 
     public boolean isYARVEnabled() {
         return ruby.getInstanceConfig().isYARVEnabled();
     }
 
     public String getInputFieldSeparator() {
         return ruby.getInstanceConfig().getInputFieldSeparator();
     }
 
     public boolean isRubiniusEnabled() {
         return ruby.getInstanceConfig().isRubiniusEnabled();
     }
 
     public boolean isYARVCompileEnabled() {
         return ruby.getInstanceConfig().isYARVCompileEnabled();
     }
 
     public String getKCode() {
         return ruby.getInstanceConfig().getKCode().name();
     }
 
     public String getRecordSeparator() {
         return ruby.getInstanceConfig().getRecordSeparator();
     }
 
     public int getSafeLevel() {
         return ruby.getInstanceConfig().getSafeLevel();
     }
 
     public String getOptionGlobals() {
         return ruby.getInstanceConfig().getOptionGlobals().toString();
     }
     
     public boolean isManagementEnabled() {
         return ruby.getInstanceConfig().isManagementEnabled();
     }
+    
+    public boolean isFullTraceEnabled() {
+        return RubyInstanceConfig.FULL_TRACE_ENABLED;
+    }
+    
+    public boolean isLazyHandlesEnabled() {
+        return RubyInstanceConfig.LAZYHANDLES_COMPILE;
+    }
 }
diff --git a/src/org/jruby/management/ConfigMBean.java b/src/org/jruby/management/ConfigMBean.java
index fbfe4b2c12..294418f71b 100644
--- a/src/org/jruby/management/ConfigMBean.java
+++ b/src/org/jruby/management/ConfigMBean.java
@@ -1,40 +1,43 @@
 package org.jruby.management;
 
 public interface ConfigMBean {
     public String getVersionString();
     public String getCopyrightString();
     public String getCompileMode();
     public boolean isJitLogging();
     public boolean isJitLoggingVerbose();
     public int getJitLogEvery();
     public boolean isSamplingEnabled();
     public int getJitThreshold();
     public int getJitMax();
+    public int getJitMaxSize();
     public boolean isRunRubyInProcess();
     public String getCompatVersion();
     public String getCurrentDirectory();
     public boolean isObjectSpaceEnabled();
     public String getEnvironment();
     public String getArgv();
     public String getJRubyHome();
     public String getRequiredLibraries();
     public String getLoadPaths();
     public String getDisplayedFileName();
     public String getScriptFileName();
     public boolean isBenchmarking();
     public boolean isAssumeLoop();
     public boolean isAssumePrinting();
     public boolean isProcessLineEnds();
     public boolean isSplit();
     public boolean isVerbose();
     public boolean isDebug();
     public boolean isYARVEnabled();
     public String getInputFieldSeparator();
     public boolean isRubiniusEnabled();
     public boolean isYARVCompileEnabled();
     public String getKCode();
     public String getRecordSeparator();
     public int getSafeLevel();
     public String getOptionGlobals();
     public boolean isManagementEnabled();
+    public boolean isFullTraceEnabled();
+    public boolean isLazyHandlesEnabled();
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index e202ebd609..17b9574661 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,980 +1,984 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 package org.jruby.runtime;
 
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyObject;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public final class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         //        if(runtime.getInstanceConfig().isSamplingEnabled()) {
         //    org.jruby.util.SimpleSampler.registerThreadContext(context);
         //}
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     // Error info is per-thread
     private IRubyObject errorInfo;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private CatchTarget[] catchStack = new CatchTarget[INITIAL_SIZE];
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
 
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // init errorInfo to nil
         errorInfo = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
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
         return errorInfo;
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
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
         scopeStack[++scopeIndex] = scope;
         if (scopeIndex + 1 == scopeStack.length) {
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
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
 //    public IRubyObject getLastline() {
 //        IRubyObject value = getCurrentScope().getLastLine();
 //        
 //        // DynamicScope does not preinitialize these values since they are virtually never used.
 //        return value == null ? runtime.getNil() : value;
 //    }
 //    
 //    public void setLastline(IRubyObject value) {
 //        getCurrentScope().setLastLine(value);
 //    }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         CatchTarget[] newCatchStack = new CatchTarget[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(CatchTarget catchTarget) {
         catchStack[++catchIndex] = catchTarget;
         if (catchIndex + 1 == catchStack.length) {
             expandCatchIfNecessary();
         }
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         if (catchIndex < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         Frame currentFrame = getCurrentFrame();
         frameStack[++frameIndex].updateFrame(currentFrame);
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrameCopy(Frame frame) {
         frameStack[++frameIndex].updateFrame(frame);
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
         return frameStack[frameIndex];
     }
     
     private Frame pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         frameStack[++frameIndex].updateFrame(clazz, self, name, block, file, line, jumpTarget);
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         frameStack[++frameIndex].updateFrame(name, file, line);
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame() {
         frameStack[++frameIndex].updateFrame(file, line);
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex];
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         Frame frame = frameStack[frameIndex];
         frameStack[frameIndex] = oldFrame;
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
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
         if (frameIndex + 1 == frameStack.length) {
             expandFramesIfNecessary();
         }
         return frameStack[frameIndex + 1];
     }
     
     public Frame getPreviousFrame() {
         return frameIndex < 1 ? null : frameStack[frameIndex - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(JumpTarget target) {
         getCurrentFrame().setJumpTarget(target);
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         this.file = file;
         this.line = line;
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
         getThread().pollThreadEvents(this);
     }
     
     int calls = 0;
     
     public void callThreadPoll() {
         if ((calls++ & 0xFF) == 0) pollThreadEvents();
     }
     
+    public void trace(int event, String name, RubyModule implClass) {
+        runtime.callEventHooks(this, event, file, line, name, implClass);
+    }
+    
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         if (parentIndex + 1 == parentStack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = parentStack[parentIndex];
         } else {
             parentModule = parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject result;
         
         // flipped from while to do to search current class first
         for (StaticScope scope = getCurrentScope().getStaticScope(); scope != null; scope = scope.getPreviousCRefScope()) {
             RubyModule module = scope.getModule();
             if ((result = module.fastFetchConstant(internedName)) != null) {
                 if (result != RubyObject.UNDEF) return true;
                 return runtime.getLoadService().autoloadFor(module.getName() + "::" + internedName) != null;
             }
         }
         
         return getCurrentScope().getStaticScope().getModule().fastIsConstantDefined(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         StaticScope scope = getCurrentScope().getStaticScope();
         RubyClass object = runtime.getObject();
         IRubyObject result;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = scope.getModule();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             if ((result = klass.fastFetchConstant(internedName)) != null) {
                 if (result != RubyObject.UNDEF) {
                     return result;
                 }
                 klass.deleteConstant(internedName);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + internedName) == null) break;
                 continue;
             }
             scope = scope.getPreviousCRefScope();
         } while (scope != null && scope.getModule() != object);
         
         return getCurrentScope().getStaticScope().getModule().fastGetConstant(internedName);
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
     
     private static void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile()) &&
                 frame.getLine() == previousFrame.getLine()) {
             return;
         }
         
         StringBuilder buf = new StringBuilder(60);
         buf.append(frame.getFile()).append(':').append(frame.getLine() + 1);
         
         if (previousFrame.getName() != null) {
             buf.append(":in `").append(previousFrame.getName()).append('\'');
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames) {
         return createBacktraceFromFrames(runtime, backtraceFrames, true);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames, boolean cropAtEval) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = traceSize - 1; i > 0; i--) {
             Frame frame = backtraceFrames[i];
             // We are in eval with binding break out early
             if (cropAtEval && frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, backtraceFrames[i - 1]);
         }
         
         return backtrace;
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
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, String[] scopeNames) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         StaticScope staticScope = new LocalStaticScope(getCurrentScope().getStaticScope(), scopeNames);
         staticScope.setModule(type);
         pushScope(new ManyVarsDynamicScope(staticScope, null));
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
             StaticScope staticScope, JumpTarget jumpTarget) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block, jumpTarget);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block,
             JumpTarget jumpTarget) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
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
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
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
         setFile(name);
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
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
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block, frame.getJumpTarget());
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
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
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
         Frame lastFrame = getNextFrame();
         Frame f = binding.getFrame();
         f.setFile(file);
         f.setLine(line);
         pushFrame(f);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushScope(binding.getDynamicScope());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return lastFrame;
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame frame = binding.getFrame();
         frame.setIsBindingFrame(true);
         pushFrame(frame);
         getCurrentFrame().setVisibility(binding.getVisibility());
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
 }
