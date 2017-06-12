diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index 442f637cc1..f0b5aeef67 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -24,1335 +24,1333 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTCompiler19;
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
     private static final int JIT_MAX_SIZE_LIMIT = 10000;
 
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
     private Set<String> excludedMethods = new HashSet<String>();
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
     private boolean managementEnabled = true;
     private String inPlaceBackupExtension = null;
     private boolean parserDebug = false;
     private String threadDumpSignal = null;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean PEEPHOLE_OPTZ
             = SafePropertyAccessor.getBoolean("jruby.compile.peephole", true);
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
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless");
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static boolean FASTCASE_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastcase");
     public static boolean FASTSEND_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastsend");
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
 
     public static final boolean USE_GENERATED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.java.handles", false);
 
     public static final boolean DEBUG_LOAD_SERVICE
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService", false);
 
     public static final boolean DEBUG_LAUNCHING
             = SafePropertyAccessor.getBoolean("jruby.debug.launch", false);
 
     public static final boolean JUMPS_HAVE_BACKTRACE
             = SafePropertyAccessor.getBoolean("jruby.jump.backtrace", false);
 
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
 
     public RubyInstanceConfig(RubyInstanceConfig parentConfig) {
         setCurrentDirectory(parentConfig.getCurrentDirectory());
         samplingEnabled = parentConfig.samplingEnabled;
         compatVersion = parentConfig.compatVersion;
         compileMode = parentConfig.getCompileMode();
         jitLogging = parentConfig.jitLogging;
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
                 for (String element : elements) excludedMethods.add(element);
             }
             
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
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 .append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode, -Ke for EUC and -Ks for SJIS)\n")
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
                 .append("  -y              enable parsing debug output\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties (pass -J-Dproperty=value)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --client        use the non-optimizing \"client\" JVM (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM and JRuby\n")
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
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is " + CHAINED_COMPILE_LINE_COUNT_DEFAULT + "\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.\n")
                 .append("    jruby.compile.peephole=true|false\n")
                 .append("       Enable or disable peephole optimizations. Default is true (on).\n")
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
                 .append("    jruby.jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
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
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is true.\n")
                 .append("    jruby.debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n")
                 .append("    jruby.debug.loadService=true|false\n")
                 .append("       Log the process of locating and loading libraries. Default is false.\n")
                 .append("    jruby.jump.backtrace=true|false\n")
                 .append("       Make non-local flow jumps generate backtraces. Default is false.\n")
                 .append("\nDEBUGGING/LOGGING:\n")
                 .append("    jruby.debug.loadService=true|false\n")
                 .append("       LoadService logging\n")
                 .append("    jruby.debug.launch=true|false\n")
                 .append("       ShellLauncher logging\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = null;
         String patchDelimeter = null;
         int patchlevel = 0;
         switch (getCompatVersion()) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             patchlevel = Constants.RUBY_PATCHLEVEL;
             patchDelimeter = " patchlevel ";
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             patchlevel = Constants.RUBY1_9_PATCHLEVEL;
             patchDelimeter = " trunk ";
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s%s%d) (%s %s) (%s %s) [%s-java]",
                 Constants.VERSION, ver, patchDelimeter, patchlevel,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 System.getProperty("java.vm.name"), System.getProperty("java.version"),
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2009 The JRuby Community (and contribs)";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
         try {
             String rubyopt = System.getenv("RUBYOPT");
             if (rubyopt != null) {
                 String[] rubyoptArgs = rubyopt.split("\\s+");
                 for (int i = 0; i < rubyoptArgs.length; i++) {
                     if (!rubyoptArgs[i].startsWith("-")) {
                         rubyoptArgs[i] = "-" + rubyoptArgs[i];
                     }
                 }
                 new ArgumentProcessor(rubyoptArgs, false).processArguments();
             }
         } catch (SecurityException se) {
             // ignore and do nothing
         }
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
 
     public void setCompatVersion(CompatVersion compatVersion) {
         // Until we get a little more solid on 1.9 support we will only run interpreted mode
         if (compatVersion == CompatVersion.RUBY1_9) compileMode = CompileMode.OFF;
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
                     jrubyHome = getClass().getResource("/META-INF/jruby.home")
                         .toURI().getSchemeSpecificPart();
                 } catch (Exception e) {}
 
                 if (jrubyHome != null) {
                     // verify it if it's there
                     jrubyHome = verifyHome(jrubyHome);
                 } else {
                     // otherwise fall back on system temp location
                     jrubyHome = System.getProperty("java.io.tmpdir");
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
             home = System.getProperty("user.dir");
         }
         if (home.startsWith("cp:")) {
             home = home.substring(3);
         } else if (!home.startsWith("file:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             if (!f.exists()) {
                 System.err.println("Warning: JRuby home \"" + f + "\" does not exist, using " + System.getProperty("java.io.tmpdir"));
                 return System.getProperty("java.io.tmpdir");
             }
         }
         return home;
     }
 
     private class ArgumentProcessor {
         private String[] arguments;
         private int argumentIndex = 0;
         private boolean processArgv;
 
         public ArgumentProcessor(String[] arguments) {
             this(arguments, true);
         }
 
         public ArgumentProcessor(String[] arguments, boolean processArgv) {
             this.arguments = arguments;
             this.processArgv = processArgv;
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
 
             if (processArgv) processArgv();
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
             for (String arg : argv) arglist.add(arg);
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return argument.length() > 0 && (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
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
                 case 'i' :
                     inPlaceBackupExtension = grabOptionalValue();
                     if(inPlaceBackupExtension == null) inPlaceBackupExtension = "";
                     break FOR;
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
-                        compileMode = CompileMode.OFF;
                         FULL_TRACE_ENABLED = true;
-                        System.setProperty("jruby.reflection", "true");
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
                         RubyException.TRACE_TYPE = RubyException.RUBY_COMPILED;
                         break FOR;
                     } else if (argument.equals("--1.9")) {
                         setCompatVersion(CompatVersion.RUBY1_9);
                         break FOR;
                     } else if (argument.equals("--1.8")) {
                         setCompatVersion(CompatVersion.RUBY1_8);
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
 
             scriptFileName = resolveScript(scriptName);
 
             if (scriptFileName.equals(scriptName)) {
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands." + scriptName);
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
                     return fullName.getAbsolutePath();
                 }
 
                 fullName = JRubyFile.create(getJRubyHome(), "bin/" + scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     return fullName.getAbsolutePath();
                 }
 
                 try {
                     String path = System.getenv("PATH");
                     if (path != null) {
                         String[] paths = path.split(System.getProperty("path.separator"));
                         for (int i = 0; i < paths.length; i++) {
                             fullName = JRubyFile.create(paths[i], scriptName);
                             if (fullName.exists() && fullName.isFile()) {
                                 return fullName.getAbsolutePath();
                             }
                         }
                     }
                 } catch (SecurityException se) {
                     // ignore and do nothing
                 }
             } catch (IllegalArgumentException iae) {
                 if (debug) System.err.println("warning: could not resolve -S script on filesystem: " + scriptName);
             }
             return scriptName;
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
         return inlineScript.toString().getBytes();
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
                 return new BufferedInputStream(new FileInputStream(file), 8192);
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
 }
diff --git a/src/org/jruby/ast/DefnNode.java b/src/org/jruby/ast/DefnNode.java
index 48cf7612a9..bfee2fb710 100644
--- a/src/org/jruby/ast/DefnNode.java
+++ b/src/org/jruby/ast/DefnNode.java
@@ -1,133 +1,133 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.DynamicMethodFactory;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * method definition node.
  */
 public class DefnNode extends MethodDefNode implements INameNode {
     public DefnNode(ISourcePosition position, ArgumentNode nameNode, ArgsNode argsNode, 
             StaticScope scope, Node bodyNode) {
         super(position, nameNode, argsNode, scope, bodyNode);
     }
 
     public NodeType getNodeType() {
         return NodeType.DEFNNODE;
     }
 
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitDefnNode(this);
     }
     
     /**
      * Get the name of this method
      */
     @Override
     public String getName() {
         return nameNode.getName();
     }
     
     public List<Node> childNodes() {
         return Node.createList(nameNode, argsNode, bodyNode);
     }
     
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         RubyModule containingClass = context.getRubyClass();
    
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
    
         String name = getName();
 
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop", "Object#initialize");
         }
 
         if (name == "__id__" || name == "__send__") {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem", name); 
         }
 
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || name == "initialize_copy" || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         scope.determineModule();
         
         // Make a nil node if no body.  Notice this is not part of AST.
         Node body = bodyNode == null ? new NilNode(getPosition()) : bodyNode;
 
-        DynamicMethod newMethod = DynamicMethodFactory.newInterpretedMethod(
+        DynamicMethod newMethod = DynamicMethodFactory.newDefaultMethod(
                 runtime, containingClass, name, scope, body, argsNode,
                 visibility, getPosition());
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility() == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod, Visibility.PUBLIC));
             
             containingClass.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(context, 
                     "singleton_method_added", runtime.fastNewSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.fastNewSymbol(name));
         }
    
         return runtime.getNil();        
     }
 }
diff --git a/src/org/jruby/ast/DefsNode.java b/src/org/jruby/ast/DefsNode.java
index a869feebd3..153786cdf5 100644
--- a/src/org/jruby/ast/DefsNode.java
+++ b/src/org/jruby/ast/DefsNode.java
@@ -1,133 +1,133 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubySymbol;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.DynamicMethodFactory;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** 
  * Represents a singleton method definition.
  */
 public class DefsNode extends MethodDefNode implements INameNode {
     private final Node receiverNode;
     public DefsNode(ISourcePosition position, Node receiverNode, ArgumentNode nameNode, ArgsNode argsNode, 
             StaticScope scope, Node bodyNode) {
         super(position, nameNode, argsNode, scope, bodyNode);
         
         assert receiverNode != null : "receiverNode is not null";
         
         this.receiverNode = receiverNode;
     }
 
     public NodeType getNodeType() {
         return NodeType.DEFSNODE;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitDefsNode(this);
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
     
     /**
      * Gets the name of this method
      */
     @Override
     public String getName() {
         return nameNode.getName();
     }
     
     public List<Node> childNodes() {
         return Node.createList(receiverNode, nameNode, argsNode, bodyNode);
     }
     
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         IRubyObject receiver = receiverNode.interpret(runtime,context, self, aBlock);
         String name = getName();
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
           throw runtime.newTypeError("can't define singleton method \"" + name
           + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) throw runtime.newFrozenError("object");
 
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
 
         scope.determineModule();
       
         // Make a nil node if no body.  Notice this is not part of AST.
         Node body = bodyNode == null ? new NilNode(getPosition()) : bodyNode;
         
-        DynamicMethod newMethod = DynamicMethodFactory.newInterpretedMethod(
+        DynamicMethod newMethod = DynamicMethodFactory.newDefaultMethod(
                 runtime, rubyClass, name, scope, body, argsNode,
                 Visibility.PUBLIC, getPosition());
    
         rubyClass.addMethod(name, newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
    
         return runtime.getNil();
     }
 }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 2eaa93a834..103b748066 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,3742 +1,3751 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2ConstNode;
 import org.jruby.ast.Colon2MethodNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FileNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.LocalVarNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhenOneArgNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallType;
 
 /**
  *
  * @author headius
  */
 public class ASTCompiler {
     private boolean isAtRoot = true;
     
     public void compile(Node node, BodyCompiler context, boolean expr) {
         if (node == null) {
             if (expr) context.loadNil();
             return;
         }
         switch (node.getNodeType()) {
             case ALIASNODE:
                 compileAlias(node, context, expr);
                 break;
             case ANDNODE:
                 compileAnd(node, context, expr);
                 break;
             case ARGSCATNODE:
                 compileArgsCat(node, context, expr);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPush(node, context, expr);
                 break;
             case ARRAYNODE:
                 compileArray(node, context, expr);
                 break;
             case ATTRASSIGNNODE:
                 compileAttrAssign(node, context, expr);
                 break;
             case BACKREFNODE:
                 compileBackref(node, context, expr);
                 break;
             case BEGINNODE:
                 compileBegin(node, context, expr);
                 break;
             case BIGNUMNODE:
                 compileBignum(node, context, expr);
                 break;
             case BLOCKNODE:
                 compileBlock(node, context, expr);
                 break;
             case BREAKNODE:
                 compileBreak(node, context, expr);
                 break;
             case CALLNODE:
                 compileCall(node, context, expr);
                 break;
             case CASENODE:
                 compileCase(node, context, expr);
                 break;
             case CLASSNODE:
                 compileClass(node, context, expr);
                 break;
             case CLASSVARNODE:
                 compileClassVar(node, context, expr);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgn(node, context, expr);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDecl(node, context, expr);
                 break;
             case COLON2NODE:
                 compileColon2(node, context, expr);
                 break;
             case COLON3NODE:
                 compileColon3(node, context, expr);
                 break;
             case CONSTDECLNODE:
                 compileConstDecl(node, context, expr);
                 break;
             case CONSTNODE:
                 compileConst(node, context, expr);
                 break;
             case DASGNNODE:
                 compileDAsgn(node, context, expr);
                 break;
             case DEFINEDNODE:
                 compileDefined(node, context, expr);
                 break;
             case DEFNNODE:
                 compileDefn(node, context, expr);
                 break;
             case DEFSNODE:
                 compileDefs(node, context, expr);
                 break;
             case DOTNODE:
                 compileDot(node, context, expr);
                 break;
             case DREGEXPNODE:
                 compileDRegexp(node, context, expr);
                 break;
             case DSTRNODE:
                 compileDStr(node, context, expr);
                 break;
             case DSYMBOLNODE:
                 compileDSymbol(node, context, expr);
                 break;
             case DVARNODE:
                 compileDVar(node, context, expr);
                 break;
             case DXSTRNODE:
                 compileDXStr(node, context, expr);
                 break;
             case ENSURENODE:
                 compileEnsureNode(node, context, expr);
                 break;
             case EVSTRNODE:
                 compileEvStr(node, context, expr);
                 break;
             case FALSENODE:
                 compileFalse(node, context, expr);
                 break;
             case FCALLNODE:
                 compileFCall(node, context, expr);
                 break;
             case FIXNUMNODE:
                 compileFixnum(node, context, expr);
                 break;
             case FLIPNODE:
                 compileFlip(node, context, expr);
                 break;
             case FLOATNODE:
                 compileFloat(node, context, expr);
                 break;
             case FORNODE:
                 compileFor(node, context, expr);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgn(node, context, expr);
                 break;
             case GLOBALVARNODE:
                 compileGlobalVar(node, context, expr);
                 break;
             case HASHNODE:
                 compileHash(node, context, expr);
                 break;
             case IFNODE:
                 compileIf(node, context, expr);
                 break;
             case INSTASGNNODE:
                 compileInstAsgn(node, context, expr);
                 break;
             case INSTVARNODE:
                 compileInstVar(node, context, expr);
                 break;
             case ITERNODE:
                 compileIter(node, context);
                 break;
             case LOCALASGNNODE:
                 compileLocalAsgn(node, context, expr);
                 break;
             case LOCALVARNODE:
                 compileLocalVar(node, context, expr);
                 break;
             case MATCH2NODE:
                 compileMatch2(node, context, expr);
                 break;
             case MATCH3NODE:
                 compileMatch3(node, context, expr);
                 break;
             case MATCHNODE:
                 compileMatch(node, context, expr);
                 break;
             case MODULENODE:
                 compileModule(node, context, expr);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgn(node, context, expr);
                 break;
             case NEWLINENODE:
                 compileNewline(node, context, expr);
                 break;
             case NEXTNODE:
                 compileNext(node, context, expr);
                 break;
             case NTHREFNODE:
                 compileNthRef(node, context, expr);
                 break;
             case NILNODE:
                 compileNil(node, context, expr);
                 break;
             case NOTNODE:
                 compileNot(node, context, expr);
                 break;
             case OPASGNANDNODE:
                 compileOpAsgnAnd(node, context, expr);
                 break;
             case OPASGNNODE:
                 compileOpAsgn(node, context, expr);
                 break;
             case OPASGNORNODE:
                 compileOpAsgnOr(node, context, expr);
                 break;
             case OPELEMENTASGNNODE:
                 compileOpElementAsgn(node, context, expr);
                 break;
             case ORNODE:
                 compileOr(node, context, expr);
                 break;
             case POSTEXENODE:
                 compilePostExe(node, context, expr);
                 break;
             case PREEXENODE:
                 compilePreExe(node, context, expr);
                 break;
             case REDONODE:
                 compileRedo(node, context, expr);
                 break;
             case REGEXPNODE:
                 compileRegexp(node, context, expr);
                 break;
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE:
                 compileRescue(node, context, expr);
                 break;
             case RETRYNODE:
                 compileRetry(node, context, expr);
                 break;
             case RETURNNODE:
                 compileReturn(node, context, expr);
                 break;
             case ROOTNODE:
                 throw new NotCompilableException("Use compileRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE:
                 compileSClass(node, context, expr);
                 break;
             case SELFNODE:
                 compileSelf(node, context, expr);
                 break;
             case SPLATNODE:
                 compileSplat(node, context, expr);
                 break;
             case STRNODE:
                 compileStr(node, context, expr);
                 break;
             case SUPERNODE:
                 compileSuper(node, context, expr);
                 break;
             case SVALUENODE:
                 compileSValue(node, context, expr);
                 break;
             case SYMBOLNODE:
                 compileSymbol(node, context, expr);
                 break;
             case TOARYNODE:
                 compileToAry(node, context, expr);
                 break;
             case TRUENODE:
                 compileTrue(node, context, expr);
                 break;
             case UNDEFNODE:
                 compileUndef(node, context, expr);
                 break;
             case UNTILNODE:
                 compileUntil(node, context, expr);
                 break;
             case VALIASNODE:
                 compileVAlias(node, context, expr);
                 break;
             case VCALLNODE:
                 compileVCall(node, context, expr);
                 break;
             case WHILENODE:
                 compileWhile(node, context, expr);
                 break;
             case WHENNODE:
                 assert false : "When nodes are handled by case node compilation.";
                 break;
             case XSTRNODE:
                 compileXStr(node, context, expr);
                 break;
             case YIELDNODE:
                 compileYield(node, context, expr);
                 break;
             case ZARRAYNODE:
                 compileZArray(node, context, expr);
                 break;
             case ZSUPERNODE:
                 compileZSuper(node, context, expr);
                 break;
             default:
                 throw new NotCompilableException("Unknown node encountered in compiler: " + node);
         }
     }
 
     public void compileArguments(Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
             case ARGSCATNODE:
                 compileArgsCatArguments(node, context, true);
                 break;
             case ARGSPUSHNODE:
                 compileArgsPushArguments(node, context, true);
                 break;
             case ARRAYNODE:
                 compileArrayArguments(node, context, true);
                 break;
             case SPLATNODE:
                 compileSplatArguments(node, context, true);
                 break;
             default:
                 compile(node, context, true);
                 context.convertToJavaArray();
         }
     }
     
     public class VariableArityArguments implements ArgumentsCallback {
         private Node node;
         
         public VariableArityArguments(Node node) {
             this.node = node;
         }
         
         public int getArity() {
             return -1;
         }
         
         public void call(BodyCompiler context) {
             compileArguments(node, context);
         }
     }
     
     public class SpecificArityArguments implements ArgumentsCallback {
         private int arity;
         private Node node;
         
         public SpecificArityArguments(Node node) {
             if (node.getNodeType() == NodeType.ARRAYNODE && ((ArrayNode)node).isLightweight()) {
                 // only arrays that are "lightweight" are being used as args arrays
                 this.arity = ((ArrayNode)node).size();
             } else {
                 // otherwise, it's a literal array
                 this.arity = 1;
             }
             this.node = node;
         }
         
         public int getArity() {
             return arity;
         }
         
         public void call(BodyCompiler context) {
             if (node.getNodeType() == NodeType.ARRAYNODE) {
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.isLightweight()) {
                     // explode array, it's an internal "args" array
                     for (Node n : arrayNode.childNodes()) {
                         compile(n, context,true);
                     }
                 } else {
                     // use array as-is, it's a literal array
                     compile(arrayNode, context,true);
                 }
             } else {
                 compile(node, context,true);
             }
         }
     }
 
     public ArgumentsCallback getArgsCallback(Node node) {
         if (node == null) {
             return null;
         }
         // unwrap newline nodes to get their actual type
         while (node.getNodeType() == NodeType.NEWLINENODE) {
             node = ((NewlineNode)node).getNextNode();
         }
         switch (node.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
             case SPLATNODE:
                 return new VariableArityArguments(node);
             case ARRAYNODE:
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.size() == 0) {
                     return null;
                 } else if (arrayNode.size() > 3) {
                     return new VariableArityArguments(node);
                 } else {
                     return new SpecificArityArguments(node);
                 }
             default:
                 return new SpecificArityArguments(node);
         }
     }
 
     public void compileAssignment(Node node, BodyCompiler context, boolean expr) {
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 compileAttrAssignAssignment(node, context, expr);
                 break;
             case DASGNNODE:
                 DAsgnNode dasgnNode = (DAsgnNode)node;
                 context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), expr);
                 break;
             case CLASSVARASGNNODE:
                 compileClassVarAsgnAssignment(node, context, expr);
                 break;
             case CLASSVARDECLNODE:
                 compileClassVarDeclAssignment(node, context, expr);
                 break;
             case CONSTDECLNODE:
                 compileConstDeclAssignment(node, context, expr);
                 break;
             case GLOBALASGNNODE:
                 compileGlobalAsgnAssignment(node, context, expr);
                 break;
             case INSTASGNNODE:
                 compileInstAsgnAssignment(node, context, expr);
                 break;
             case LOCALASGNNODE:
                 LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
                 context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), expr);
                 break;
             case MULTIPLEASGNNODE:
                 compileMultipleAsgnAssignment(node, context, expr);
                 break;
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
 
     public void compileAlias(Node node, BodyCompiler context, boolean expr) {
         final AliasNode alias = (AliasNode) node;
 
         context.defineAlias(alias.getNewName(), alias.getOldName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileAnd(Node node, BodyCompiler context, final boolean expr) {
         final AndNode andNode = (AndNode) node;
 
         if (andNode.getFirstNode().getNodeType().alwaysTrue()) {
             // compile first node as non-expr and then second node
             compile(andNode.getFirstNode(), context, false);
             compile(andNode.getSecondNode(), context, expr);
         } else if (andNode.getFirstNode().getNodeType().alwaysFalse()) {
             // compile first node only
             compile(andNode.getFirstNode(), context, expr);
         } else {
             compile(andNode.getFirstNode(), context, true);
             BranchCallback longCallback = new BranchCallback() {
                         public void branch(BodyCompiler context) {
                             compile(andNode.getSecondNode(), context, true);
                         }
                     };
 
             context.performLogicalAnd(longCallback);
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileArray(Node node, BodyCompiler context, boolean expr) {
         ArrayNode arrayNode = (ArrayNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
         
         if (doit) {
             ArrayCallback callback = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                             Node node = (Node) ((Object[]) sourceArray)[index];
                             compile(node, context, true);
                         }
                     };
 
             context.createNewArray(arrayNode.childNodes().toArray(), callback, arrayNode.isLightweight());
 
             if (popit) context.consumeCurrentValue();
         } else {
             for (Iterator<Node> iter = arrayNode.childNodes().iterator(); iter.hasNext();) {
                 Node nextNode = iter.next();
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileArgsCat(Node node, BodyCompiler context, boolean expr) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compile(argsCatNode.getFirstNode(), context,true);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context,true);
         context.splatCurrentValue();
         context.concatArrays();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsPush(Node node, BodyCompiler context, boolean expr) {
         throw new NotCompilableException("ArgsPush should never be encountered bare in 1.8");
     }
 
     private void compileAttrAssign(Node node, BodyCompiler context, boolean expr) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context,true);
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileAttrAssignAssignment(Node node, BodyCompiler context, boolean expr) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(attrAssignNode.getReceiverNode(), context,true);
             }
         };
         ArgumentsCallback argsCallback = getArgsCallback(attrAssignNode.getArgsNode());
 
         context.getInvocationCompiler().invokeAttrAssignMasgn(attrAssignNode.getName(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileBackref(Node node, BodyCompiler context, boolean expr) {
         BackRefNode iVisited = (BackRefNode) node;
 
         context.performBackref(iVisited.getType());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileBegin(Node node, BodyCompiler context, boolean expr) {
         BeginNode beginNode = (BeginNode) node;
 
         compile(beginNode.getBodyNode(), context, expr);
     }
 
     public void compileBignum(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewBignum(((BignumNode) node).getValue());
         } else {
             context.createNewBignum(((BignumNode) node).getValue());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileBlock(Node node, BodyCompiler context, boolean expr) {
         BlockNode blockNode = (BlockNode) node;
 
         for (Iterator<Node> iter = blockNode.childNodes().iterator(); iter.hasNext();) {
             Node n = iter.next();
 
             compile(n, context, iter.hasNext() ? false : expr);
         }
     }
 
     public void compileBreak(Node node, BodyCompiler context, boolean expr) {
         final BreakNode breakNode = (BreakNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (breakNode.getValueNode() != null) {
                             compile(breakNode.getValueNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.issueBreakEvent(valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileCall(Node node, BodyCompiler context, boolean expr) {
         final CallNode callNode = (CallNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(callNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = getArgsCallback(callNode.getArgsNode());
         CompilerCallback closureArg = getBlock(callNode.getIterNode());
 
         String name = callNode.getName();
         CallType callType = CallType.NORMAL;
 
         if (argsCallback != null && argsCallback.getArity() == 1) {
             Node argument = callNode.getArgsNode().childNodes().get(0);
             if (name.length() == 1) {
                 switch (name.charAt(0)) {
                 case '+': case '-': case '*': case '/': case '<': case '>':
                     if (argument instanceof FixnumNode) {
                         context.getInvocationCompiler().invokeBinaryFixnumRHS(name, receiverCallback, ((FixnumNode)argument).getValue());
                         if (!expr) context.consumeCurrentValue();
                         return;
                     }
                 }
             } else if (name.length() == 2) {
                 if (argument instanceof FixnumNode) {
                     switch (name.charAt(0)) {
                     case '<': case '>': case '=': case '[':
                         switch (name.charAt(1)) {
                         case '=': case '<': case ']':
                             context.getInvocationCompiler().invokeBinaryFixnumRHS(name, receiverCallback, ((FixnumNode)argument).getValue());
                             if (!expr) context.consumeCurrentValue();
                             return;
                         }
                     }
                 }
             }
         }
 
         // if __send__ with a literal symbol, compile it as a direct fcall
         if (RubyInstanceConfig.FASTSEND_COMPILE_ENABLED) {
             String literalSend = getLiteralSend(callNode);
             if (literalSend != null) {
                 name = literalSend;
                 callType = CallType.FUNCTIONAL;
             }
         }
         
         context.getInvocationCompiler().invokeDynamic(
                 name, receiverCallback, argsCallback,
                 callType, closureArg, callNode.getIterNode() instanceof IterNode);
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private String getLiteralSend(CallNode callNode) {
         if (callNode.getName().equals("__send__")) {
             if (callNode.getArgsNode() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)callNode.getArgsNode();
                 if (arrayNode.get(0) instanceof SymbolNode) {
                     return ((SymbolNode)arrayNode.get(0)).getName();
                 } else if (arrayNode.get(0) instanceof StrNode) {
                     return ((StrNode)arrayNode.get(0)).getValue().toString();
                 }
             }
         }
         return null;
     }
 
     public void compileCase(Node node, BodyCompiler context, boolean expr) {
         CaseNode caseNode = (CaseNode) node;
 
         boolean hasCase = caseNode.getCaseNode() != null;
 
         // aggregate when nodes into a list, unfortunately, this is no
         List<Node> cases = caseNode.getCases().childNodes();
 
         // last node, either !instanceof WhenNode or null, is the else
         Node elseNode = caseNode.getElseNode();
 
         compileWhen(caseNode.getCaseNode(), cases, elseNode, context, expr, hasCase);
     }
 
     private FastSwitchType getHomogeneousSwitchType(List<Node> whenNodes) {
         FastSwitchType foundType = null;
         Outer: for (Node node : whenNodes) {
             WhenNode whenNode = (WhenNode)node;
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
 
                 for (Node maybeFixnum : arrayNode.childNodes()) {
                     if (maybeFixnum instanceof FixnumNode) {
                         FixnumNode fixnumNode = (FixnumNode)maybeFixnum;
                         long value = fixnumNode.getValue();
                         if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                             if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
                             if (foundType == null) foundType = FastSwitchType.FIXNUM;
                             continue;
                         } else {
                             return null;
                         }
                     } else {
                         return null;
                     }
                 }
             } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
                 FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
                 long value = fixnumNode.getValue();
                 if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                     if (foundType != null && foundType != FastSwitchType.FIXNUM) return null;
                     if (foundType == null) foundType = FastSwitchType.FIXNUM;
                     continue;
                 } else {
                     return null;
                 }
             } else if (whenNode.getExpressionNodes() instanceof StrNode) {
                 StrNode strNode = (StrNode)whenNode.getExpressionNodes();
                 if (strNode.getValue().length() == 1) {
                     if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_STRING) return null;
                     if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_STRING;
 
                     continue;
                 } else {
                     if (foundType != null && foundType != FastSwitchType.STRING) return null;
                     if (foundType == null) foundType = FastSwitchType.STRING;
 
                     continue;
                 }
             } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
                 SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
                 if (symbolNode.getName().length() == 1) {
                     if (foundType != null && foundType != FastSwitchType.SINGLE_CHAR_SYMBOL) return null;
                     if (foundType == null) foundType = FastSwitchType.SINGLE_CHAR_SYMBOL;
 
                     continue;
                 } else {
                     if (foundType != null && foundType != FastSwitchType.SYMBOL) return null;
                     if (foundType == null) foundType = FastSwitchType.SYMBOL;
 
                     continue;
                 }
             } else {
                 return null;
             }
         }
         return foundType;
     }
 
     public void compileWhen(final Node value, List<Node> whenNodes, final Node elseNode, BodyCompiler context, final boolean expr, final boolean hasCase) {
         CompilerCallback caseValue = null;
         if (value != null) caseValue = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(value, context, true);
                 context.pollThreadEvents();
             }
         };
 
         List<ArgumentsCallback> conditionals = new ArrayList<ArgumentsCallback>();
         List<CompilerCallback> bodies = new ArrayList<CompilerCallback>();
         Map<CompilerCallback, int[]> switchCases = null;
         FastSwitchType switchType = getHomogeneousSwitchType(whenNodes);
-        if (switchType != null) {
+        if (switchType != null && !RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // NOTE: Currently this optimization is limited to the following situations:
             // * All expressions must be int-ranged literal fixnums
             // It also still emits the code for the "safe" when logic, which is rather
             // wasteful (since it essentially doubles each code body). As such it is
             // normally disabled, but it serves as an example of how this optimization
             // could be done. Ideally, it should be combined with the when processing
             // to improve code reuse before it's generally available.
             switchCases = new HashMap<CompilerCallback, int[]>();
         }
         for (Node node : whenNodes) {
             final WhenNode whenNode = (WhenNode)node;
             CompilerCallback body = new CompilerCallback() {
                 public void call(BodyCompiler context) {
+                    if (RubyInstanceConfig.FULL_TRACE_ENABLED) context.traceLine();
                     compile(whenNode.getBodyNode(), context, expr);
                 }
             };
             addConditionalForWhen(whenNode, conditionals, bodies, body);
             if (switchCases != null) switchCases.put(body, getOptimizedCases(whenNode));
         }
         
         CompilerCallback fallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(elseNode, context, expr);
             }
         };
         
         context.compileSequencedConditional(caseValue, switchType, switchCases, conditionals, bodies, fallback);
     }
 
     private int[] getOptimizedCases(WhenNode whenNode) {
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             ArrayNode expression = (ArrayNode)whenNode.getExpressionNodes();
             if (expression.get(expression.size() - 1) instanceof WhenNode) {
                 // splatted when, can't do it yet
                 return null;
             }
 
             int[] cases = new int[expression.size()];
             for (int i = 0; i < cases.length; i++) {
                 switch (expression.get(i).getNodeType()) {
                 case FIXNUMNODE:
                     cases[i] = (int)((FixnumNode)expression.get(i)).getValue();
                     break;
                 default:
                     // can't do it
                     return null;
                 }
             }
             return cases;
         } else if (whenNode.getExpressionNodes() instanceof FixnumNode) {
             FixnumNode fixnumNode = (FixnumNode)whenNode.getExpressionNodes();
             return new int[] {(int)fixnumNode.getValue()};
         } else if (whenNode.getExpressionNodes() instanceof StrNode) {
             StrNode strNode = (StrNode)whenNode.getExpressionNodes();
             if (strNode.getValue().length() == 1) {
                 return new int[] {strNode.getValue().get(0)};
             } else {
                 return new int[] {strNode.getValue().hashCode()};
             }
         } else if (whenNode.getExpressionNodes() instanceof SymbolNode) {
             SymbolNode symbolNode = (SymbolNode)whenNode.getExpressionNodes();
             if (symbolNode.getName().length() == 1) {
                 return new int[] {symbolNode.getName().charAt(0)};
             } else {
                 return new int[] {symbolNode.getName().hashCode()};
             }
         }
         return null;
     }
 
     private void addConditionalForWhen(final WhenNode whenNode, List<ArgumentsCallback> conditionals, List<CompilerCallback> bodies, CompilerCallback body) {
         bodies.add(body);
 
         // If it's a single-arg when but contains an array, we know it's a real literal array
         // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
             if (whenNode instanceof WhenOneArgNode) {
                 // one arg but it's an array, treat it as a proper array
                 conditionals.add(new ArgumentsCallback() {
                     public int getArity() {
                         return 1;
                     }
 
                     public void call(BodyCompiler context) {
                         compile(whenNode.getExpressionNodes(), context, true);
                     }
                 });
                 return;
             }
         }
         // otherwise, use normal args compiler
         conditionals.add(getArgsCallback(whenNode.getExpressionNodes()));
     }
 
     public void compileClass(Node node, BodyCompiler context, boolean expr) {
         final ClassNode classNode = (ClassNode) node;
 
         final Node superNode = classNode.getSuperNode();
 
         final Node cpathNode = classNode.getCPath();
 
         CompilerCallback superCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(superNode, context, true);
                     }
                 };
         if (superNode == null) {
             superCallback = null;
         }
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (classNode.getBodyNode() != null) {
                             compile(classNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 if (leftNode instanceof NilNode) {
                                     context.raiseTypeError("No outer class");
                                 } else {
                                     compile(leftNode, context, true);
                                 }
                             } else {
                                 context.loadNil();
                             }
                         } else if (cpathNode instanceof Colon3Node) {
                             context.loadObject();
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(classNode.getBodyNode());
 
         context.defineClass(classNode.getCPath().getName(), classNode.getScope(), superCallback, pathCallback, bodyCallback, null, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSClass(Node node, BodyCompiler context, boolean expr) {
         final SClassNode sclassNode = (SClassNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(sclassNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         boolean oldIsAtRoot = isAtRoot;
                         isAtRoot = false;
                         if (sclassNode.getBodyNode() != null) {
                             compile(sclassNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                         isAtRoot = oldIsAtRoot;
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(sclassNode.getBodyNode());
 
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVar(Node node, BodyCompiler context, boolean expr) {
         ClassVarNode classVarNode = (ClassVarNode) node;
 
         context.retrieveClassVariable(classVarNode.getName());
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgn(Node node, BodyCompiler context, boolean expr) {
         final ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignClassVariable(classVarAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
 
         context.assignClassVariable(classVarAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDecl(Node node, BodyCompiler context, boolean expr) {
         final ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(classVarDeclNode.getValueNode(), context, true);
             }
         };
         
         context.declareClassVariable(classVarDeclNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileClassVarDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
 
         context.declareClassVariable(classVarDeclNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDecl(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context,true);
 
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConstDeclAssignment(Node node, BodyCompiler context, boolean expr) {
         // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
         ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             compile(((Colon2Node) constNode).getLeftNode(), context,true);
             context.swapValues();
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileConst(Node node, BodyCompiler context, boolean expr) {
         ConstNode constNode = (ConstNode) node;
 
         context.retrieveConstant(constNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
         // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
     }
 
     public void compileColon2(Node node, BodyCompiler context, boolean expr) {
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             if (node instanceof Colon2ConstNode) {
                 compile(iVisited.getLeftNode(), context, true);
                 context.retrieveConstantFromModule(name);
             } else if (node instanceof Colon2MethodNode) {
                 final CompilerCallback receiverCallback = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(iVisited.getLeftNode(), context,true);
                     }
                 };
                 
                 context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileColon3(Node node, BodyCompiler context, boolean expr) {
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.retrieveConstantFromObject(name);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGetDefinitionBase(final Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
         case CONSTDECLNODE:
         case DASGNNODE:
         case GLOBALASGNNODE:
         case LOCALASGNNODE:
         case MULTIPLEASGNNODE:
         case OPASGNNODE:
         case OPELEMENTASGNNODE:
         case DVARNODE:
         case FALSENODE:
         case TRUENODE:
         case LOCALVARNODE:
         case INSTVARNODE:
         case BACKREFNODE:
         case SELFNODE:
         case VCALLNODE:
         case YIELDNODE:
         case GLOBALVARNODE:
         case CONSTNODE:
         case FCALLNODE:
         case CLASSVARNODE:
             // these are all simple cases that don't require the heavier defined logic
             compileGetDefinition(node, context);
             break;
         default:
             BranchCallback reg = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.inDefined();
                             compileGetDefinition(node, context);
                         }
                     };
             BranchCallback out = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.outDefined();
                         }
                     };
             context.protect(reg, out, String.class);
         }
     }
 
     public void compileDefined(final Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
                 context.stringOrNil();
             }
         } else {
             compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
             context.stringOrNil();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileGetArgumentDefinition(final Node node, BodyCompiler context, String type) {
         if (node == null) {
             context.pushString(type);
         } else if (node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 compileGetDefinition(iterNode, context);
                 context.ifNull(endToken);
             }
             context.pushString(type);
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         } else {
             compileGetDefinition(node, context);
             Object endToken = context.getNewEnding();
             context.ifNull(endToken);
             context.pushString(type);
             Object realToken = context.getNewEnding();
             context.go(realToken);
             context.setEnding(endToken);
             context.pushNull();
             context.setEnding(realToken);
         }
     }
 
     public void compileGetDefinition(final Node node, BodyCompiler context) {
         switch (node.getNodeType()) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case OPASGNNODE:
             case OPELEMENTASGNNODE:
                 context.pushString("assignment");
                 break;
             case BACKREFNODE:
                 context.backref();
                 context.isInstanceOf(RubyMatchData.class,
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("$" + ((BackRefNode) node).getType());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case DVARNODE:
                 context.pushString("local-variable(in-block)");
                 break;
             case FALSENODE:
                 context.pushString("false");
                 break;
             case TRUENODE:
                 context.pushString("true");
                 break;
             case LOCALVARNODE:
                 context.pushString("local-variable");
                 break;
             case MATCH2NODE:
             case MATCH3NODE:
                 context.pushString("method");
                 break;
             case NILNODE:
                 context.pushString("nil");
                 break;
             case NTHREFNODE:
                 context.isCaptured(((NthRefNode) node).getMatchNumber(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("$" + ((NthRefNode) node).getMatchNumber());
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case SELFNODE:
                 context.pushString("self");
                 break;
             case VCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((VCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case YIELDNODE:
                 context.hasBlock(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("yield");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case GLOBALVARNODE:
                 context.isGlobalDefined(((GlobalVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("global-variable");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case INSTVARNODE:
                 context.isInstanceVariableDefined(((InstVarNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("instance-variable");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case CONSTNODE:
                 context.isConstantDefined(((ConstNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushString("constant");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case FCALLNODE:
                 context.loadSelf();
                 context.isMethodBound(((FCallNode) node).getName(),
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compileGetArgumentDefinition(((FCallNode) node).getArgsNode(), context, "method");
                             }
                         },
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         });
                 break;
             case COLON3NODE:
             case COLON2NODE:
                 {
                     final Colon3Node iVisited = (Colon3Node) node;
 
                     final String name = iVisited.getName();
 
                     BranchCallback setup = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     if (iVisited instanceof Colon2Node) {
                                         final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
                                         compile(leftNode, context,true);
                                     } else {
                                         context.loadObject();
                                     }
                                 }
                             };
                     BranchCallback isConstant = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushString("constant");
                                 }
                             };
                     BranchCallback isMethod = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushString("method");
                                 }
                             };
                     BranchCallback none = new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             };
                     context.isConstantBranch(setup, isConstant, isMethod, none, name);
                     break;
                 }
             case CALLNODE:
                 {
                     final CallNode iVisited = (CallNode) node;
                     Object isnull = context.getNewEnding();
                     Object ending = context.getNewEnding();
                     compileGetDefinition(iVisited.getReceiverNode(), context);
                     context.ifNull(isnull);
 
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(iVisited.getReceiverNode(), context,true); //[IRubyObject]
                                     context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     context.metaclass(); //[IRubyObject, RubyClass]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = context.getNewEnding();
                                     Object isreal = context.getNewEnding();
                                     Object ending = context.getNewEnding();
                                     context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     context.selfIsKindOf(isreal); //[IRubyObject]
                                     context.consumeCurrentValue();
                                     context.go(isfalse);
                                     context.setEnding(isreal); //[]
 
                                     context.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     context.go(isfalse);
                                                 }
                                             });
                                     context.go(ending);
                                     context.setEnding(isfalse);
                                     context.pushNull();
                                     context.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, String.class);
 
                     //          context.swapValues();
             //context.consumeCurrentValue();
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             case CLASSVARNODE:
                 {
                     ClassVarNode iVisited = (ClassVarNode) node;
                     final Object ending = context.getNewEnding();
                     final Object failure = context.getNewEnding();
                     final Object singleton = context.getNewEnding();
                     Object second = context.getNewEnding();
                     Object third = context.getNewEnding();
 
                     context.loadCurrentModule(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifNotNull(second); //[RubyClass]
                     context.consumeCurrentValue(); //[]
                     context.loadSelf(); //[self]
                     context.metaclass(); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushString("class variable");
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(second);  //[RubyClass]
                     context.duplicateCurrentValue();
                     context.isClassVarDefined(iVisited.getName(),
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.consumeCurrentValue();
                                     context.pushString("class variable");
                                     context.go(ending);
                                 }
                             },
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                 }
                             });
                     context.setEnding(third); //[RubyClass]
                     context.duplicateCurrentValue(); //[RubyClass, RubyClass]
                     context.ifSingleton(singleton); //[RubyClass]
                     context.consumeCurrentValue();//[]
                     context.go(failure);
                     context.setEnding(singleton);
                     context.attached();//[RubyClass]
                     context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
                     context.pushString("class variable");
                     context.go(ending);
                     context.setEnding(failure);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case ZSUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     context.pushString("super");
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                 }
                 break;
             case SUPERNODE:
                 {
                     Object fail = context.getNewEnding();
                     Object fail2 = context.getNewEnding();
                     Object fail_easy = context.getNewEnding();
                     Object ending = context.getNewEnding();
 
                     context.getFrameName(); //[String]
                     context.duplicateCurrentValue(); //[String, String]
                     context.ifNull(fail); //[String]
                     context.getFrameKlazz(); //[String, RubyClass]
                     context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
                     context.ifNull(fail2); //[String, RubyClass]
                     context.superClass();
                     context.ifNotSuperMethodBound(fail_easy);
 
                     compileGetArgumentDefinition(((SuperNode) node).getArgsNode(), context, "super");
                     context.go(ending);
 
                     context.setEnding(fail2);
                     context.consumeCurrentValue();
                     context.setEnding(fail);
                     context.consumeCurrentValue();
                     context.setEnding(fail_easy);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             case ATTRASSIGNNODE:
                 {
                     final AttrAssignNode iVisited = (AttrAssignNode) node;
                     Object isnull = context.getNewEnding();
                     Object ending = context.getNewEnding();
                     compileGetDefinition(iVisited.getReceiverNode(), context);
                     context.ifNull(isnull);
 
                     context.rescue(new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     compile(iVisited.getReceiverNode(), context,true); //[IRubyObject]
                                     context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
                                     context.metaclass(); //[IRubyObject, RubyClass]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
                                     context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
                                     context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
                                     final Object isfalse = context.getNewEnding();
                                     Object isreal = context.getNewEnding();
                                     Object ending = context.getNewEnding();
                                     context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
                                     context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
                                     context.selfIsKindOf(isreal); //[IRubyObject]
                                     context.consumeCurrentValue();
                                     context.go(isfalse);
                                     context.setEnding(isreal); //[]
 
                                     context.isMethodBound(iVisited.getName(), new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
                                                 }
                                             },
                                             new BranchCallback() {
 
                                                 public void branch(BodyCompiler context) {
                                                     context.go(isfalse);
                                                 }
                                             });
                                     context.go(ending);
                                     context.setEnding(isfalse);
                                     context.pushNull();
                                     context.setEnding(ending);
                                 }
                             }, JumpException.class,
                             new BranchCallback() {
 
                                 public void branch(BodyCompiler context) {
                                     context.pushNull();
                                 }
                             }, String.class);
 
                     context.go(ending);
                     context.setEnding(isnull);
                     context.pushNull();
                     context.setEnding(ending);
                     break;
                 }
             default:
                 context.rescue(new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 compile(node, context,true);
                                 context.consumeCurrentValue();
                                 context.pushNull();
                             }
                         }, JumpException.class,
                         new BranchCallback() {
 
                             public void branch(BodyCompiler context) {
                                 context.pushNull();
                             }
                         }, String.class);
                 context.consumeCurrentValue();
                 context.pushString("expression");
         }
     }
 
     public void compileDAsgn(Node node, BodyCompiler context, boolean expr) {
         final DAsgnNode dasgnNode = (DAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(dasgnNode.getValueNode(), context, true);
             }
         };
         
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), value, expr);
     }
 
     public void compileDAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         DAsgnNode dasgnNode = (DAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth(), expr);
     }
 
     public void compileDefn(Node node, BodyCompiler context, boolean expr) {
         final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defnNode.getBodyNode() != null) {
                             if (defnNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as a light rescue
                                 compileRescueInternal(defnNode.getBodyNode(), context, true);
                             } else {
                                 compile(defnNode.getBodyNode(), context, true);
                             }
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         // check args first, since body inspection can depend on args
         inspector.inspect(defnNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defnNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defnNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defnNode.getBodyNode());
         }
 
-        context.defineNewMethod(defnNode.getName(), defnNode.getArgsNode().getArity().getValue(), defnNode.getScope(), body, args, null, inspector, isAtRoot);
+        context.defineNewMethod(
+                defnNode.getName(), defnNode.getArgsNode().getArity().getValue(),
+                defnNode.getScope(), body, args, null, inspector, isAtRoot,
+                defnNode.getPosition().getFile(), defnNode.getPosition().getStartLine());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDefs(Node node, BodyCompiler context, boolean expr) {
         final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
 
         CompilerCallback receiver = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(defsNode.getReceiverNode(), context, true);
                     }
                 };
 
         CompilerCallback body = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (defsNode.getBodyNode() != null) {
                             if (defsNode.getBodyNode() instanceof RescueNode) {
                                 // if root of method is rescue, compile as light rescue
                                 compileRescueInternal(defsNode.getBodyNode(), context, true);
                             } else {
                                 compile(defsNode.getBodyNode(), context, true);
                             }
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         CompilerCallback args = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileArgs(argsNode, context, true);
                     }
                 };
 
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
 
         // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
         // TODO: this is gross.
         if (defsNode.getBodyNode() instanceof RescueNode) {
             RescueNode rescueNode = (RescueNode)defsNode.getBodyNode();
             inspector.inspect(rescueNode.getBodyNode());
             inspector.inspect(rescueNode.getElseNode());
             inspector.inspect(rescueNode.getRescueNode());
         } else {
             inspector.inspect(defsNode.getBodyNode());
         }
 
-        context.defineNewMethod(defsNode.getName(), defsNode.getArgsNode().getArity().getValue(), defsNode.getScope(), body, args, receiver, inspector, false);
+        context.defineNewMethod(
+                defsNode.getName(), defsNode.getArgsNode().getArity().getValue(),
+                defsNode.getScope(), body, args, receiver, inspector, false,
+                defsNode.getPosition().getFile(), defsNode.getPosition().getStartLine());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgs(Node node, BodyCompiler context, boolean expr) {
         final ArgsNode argsNode = (ArgsNode) node;
 
         final int required = argsNode.getRequiredArgsCount();
         final int opt = argsNode.getOptionalArgsCount();
         final int rest = argsNode.getRestArg();
 
         ArrayCallback requiredAssignment = null;
         ArrayCallback optionalGiven = null;
         ArrayCallback optionalNotGiven = null;
         CompilerCallback restAssignment = null;
         CompilerCallback blockAssignment = null;
 
         if (required > 0) {
             requiredAssignment = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                             context.getVariableCompiler().assignLocalVariable(index, false);
                         }
                     };
         }
 
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compileAssignment(optArg, context,true);
                             context.consumeCurrentValue();
                         }
                     };
             optionalNotGiven = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object object, int index) {
                             Node optArg = ((ListNode) object).get(index);
 
                             compile(optArg, context,true);
                             context.consumeCurrentValue();
                         }
                     };
         }
 
         if (rest > -1) {
             restAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg(), false);
                         }
                     };
         }
 
         if (argsNode.getBlock() != null) {
             blockAssignment = new CompilerCallback() {
 
                         public void call(BodyCompiler context) {
                             context.getVariableCompiler().assignLocalVariable(argsNode.getBlock().getCount(), false);
                         }
                     };
         }
 
         context.getVariableCompiler().checkMethodArity(required, opt, rest);
         context.getVariableCompiler().assignMethodArguments(argsNode.getPre(),
                 argsNode.getRequiredArgsCount(),
                 argsNode.getOptArgs(),
                 argsNode.getOptionalArgsCount(),
                 requiredAssignment,
                 optionalGiven,
                 optionalNotGiven,
                 restAssignment,
                 blockAssignment);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileDot(Node node, BodyCompiler context, boolean expr) {
         final DotNode dotNode = (DotNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             CompilerCallback beginEndCallback = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(dotNode.getBeginNode(), context, true);
                     compile(dotNode.getEndNode(), context, true);
                 }
             };
 
             context.createNewRange(beginEndCallback, dotNode.isExclusive());
         }
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileDRegexp(Node node, BodyCompiler context, boolean expr) {
         final DRegexpNode dregexpNode = (DRegexpNode) node;
 
         CompilerCallback createStringCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         ArrayCallback dstrCallback = new ArrayCallback() {
 
                                     public void nextValue(BodyCompiler context, Object sourceArray,
                                             int index) {
                                         compile(dregexpNode.get(index), context, true);
                                     }
                                 };
                         context.createNewString(dstrCallback, dregexpNode.size());
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewRegexp(createStringCallback, dregexpNode.getOptions());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dregexpNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDStr(Node node, BodyCompiler context, boolean expr) {
         final DStrNode dstrNode = (DStrNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dstrNode.get(index), context, true);
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewString(dstrCallback, dstrNode.size());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dstrNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDSymbol(Node node, BodyCompiler context, boolean expr) {
         final DSymbolNode dsymbolNode = (DSymbolNode) node;
 
         ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dsymbolNode.get(index), context, true);
                     }
                 };
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createNewSymbol(dstrCallback, dsymbolNode.size());
             if (popit) context.consumeCurrentValue();
         } else {
             // not an expression, only compile the elements
             for (Node nextNode : dsymbolNode.childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
 
     public void compileDVar(Node node, BodyCompiler context, boolean expr) {
         DVarNode dvarNode = (DVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
         } else {
             context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileDXStr(Node node, BodyCompiler context, boolean expr) {
         final DXStrNode dxstrNode = (DXStrNode) node;
 
         final ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         compile(dxstrNode.get(index), context,true);
                     }
                 };
 
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
                     public int getArity() {
                         return 1;
                     }
                     
                     public void call(BodyCompiler context) {
                         context.createNewString(dstrCallback, dxstrNode.size());
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEnsureNode(Node node, BodyCompiler context, boolean expr) {
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.performEnsure(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context, true);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(ensureNode.getEnsureNode(), context, false);
                         }
                     });
         } else {
             if (ensureNode.getBodyNode() != null) {
                 compile(ensureNode.getBodyNode(), context,true);
             } else {
                 context.loadNil();
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileEvStr(Node node, BodyCompiler context, boolean expr) {
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context,true);
         context.asString();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileFalse(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadFalse();
                 context.pollThreadEvents();
             }
         } else {
             context.loadFalse();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFCall(Node node, BodyCompiler context, boolean expr) {
         final FCallNode fcallNode = (FCallNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(fcallNode.getArgsNode());
         
         CompilerCallback closureArg = getBlock(fcallNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, fcallNode.getIterNode() instanceof IterNode);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private CompilerCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(iterNode, context,true);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new CompilerCallback() {
 
                             public void call(BodyCompiler context) {
                                 compile(blockPassNode.getBodyNode(), context,true);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public void compileFixnum(Node node, BodyCompiler context, boolean expr) {
         FixnumNode fixnumNode = (FixnumNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewFixnum(fixnumNode.getValue());
         } else {
             context.createNewFixnum(fixnumNode.getValue());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFlip(Node node, BodyCompiler context, boolean expr) {
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     becomeTrueOrFalse(context);
                     context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), true);
                 }
             });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getEndNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                         }
                     });
                     context.loadTrue();
                 }
             }, new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(flipNode.getBeginNode(), context,true);
                     context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(flipNode.getEndNode(), context,true);
                             flipTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth(), false);
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             context.loadFalse();
                         }
                     });
                 }
             });
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private void becomeTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
 
     private void flipTrueOrFalse(BodyCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
 
     public void compileFloat(Node node, BodyCompiler context, boolean expr) {
         FloatNode floatNode = (FloatNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewFloat(floatNode.getValue());
         } else {
             context.createNewFloat(floatNode.getValue());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileFor(Node node, BodyCompiler context, boolean expr) {
         final ForNode forNode = (ForNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compile(forNode.getIterNode(), context, true);
                     }
                 };
 
         final CompilerCallback closureArg = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, true);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileForIter(Node node, BodyCompiler context) {
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             compile(forNode.getBodyNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context, false);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().getNodeType();
         }
         
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(forNode.getBodyNode());
         inspector.inspect(forNode.getVarNode());
 
         // force heap-scope behavior, since it uses parent's scope
         inspector.setFlag(ASTInspector.CLOSURE);
 
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public void compileGlobalAsgn(Node node, BodyCompiler context, boolean expr) {
         final GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(globalAsgnNode.getValueNode(), context, true);
             }
         };
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine(value);
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef(value);
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName(), value);
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName(), value);
         }
 
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
             case '_':
                 context.getVariableCompiler().assignLastLine();
                 break;
             case '~':
                 context.getVariableCompiler().assignBackRef();
                 break;
             default:
                 context.assignGlobalVariable(globalAsgnNode.getName());
             }
         } else {
             context.assignGlobalVariable(globalAsgnNode.getName());
         }
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileGlobalVar(Node node, BodyCompiler context, boolean expr) {
         GlobalVarNode globalVarNode = (GlobalVarNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
         
         if (doit) {
             if (globalVarNode.getName().length() == 2) {
                 switch (globalVarNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().retrieveLastLine();
                     break;
                 case '~':
                     context.getVariableCompiler().retrieveBackRef();
                     break;
                 default:
                     context.retrieveGlobalVariable(globalVarNode.getName());
                 }
             } else {
                 context.retrieveGlobalVariable(globalVarNode.getName());
             }
         }
         
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileHash(Node node, BodyCompiler context, boolean expr) {
         compileHashCommon((HashNode) node, context, expr);
     }
     
     protected void compileHashCommon(HashNode hashNode, BodyCompiler context, boolean expr) {
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
                 context.createEmptyHash();
                 return;
             }
 
             ArrayCallback hashCallback = new ArrayCallback() {
 
                         public void nextValue(BodyCompiler context, Object sourceArray,
                                 int index) {
                             ListNode listNode = (ListNode) sourceArray;
                             int keyIndex = index * 2;
                             compile(listNode.get(keyIndex), context, true);
                             compile(listNode.get(keyIndex + 1), context, true);
                         }
                     };
 
             createNewHash(context, hashNode, hashCallback);
             if (popit) context.consumeCurrentValue();
         } else {
             for (Node nextNode : hashNode.getListNode().childNodes()) {
                 compile(nextNode, context, false);
             }
         }
     }
     
     protected void createNewHash(BodyCompiler context, HashNode hashNode, ArrayCallback hashCallback) {
         context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
     }
 
     public void compileIf(Node node, BodyCompiler context, final boolean expr) {
         final IfNode ifNode = (IfNode) node;
 
         // optimizations if we know ahead of time it will always be true or false
         Node actualCondition = ifNode.getCondition();
         while (actualCondition instanceof NewlineNode) {
             actualCondition = ((NewlineNode)actualCondition).getNextNode();
         }
 
         if (actualCondition.getNodeType().alwaysTrue()) {
             // compile condition as non-expr and just compile "then" body
             compile(actualCondition, context, false);
             compile(ifNode.getThenBody(), context, expr);
         } else if (actualCondition.getNodeType().alwaysFalse()) {
             // always false or nil
             compile(ifNode.getElseBody(), context, expr);
         } else {
             BranchCallback trueCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getThenBody() != null) {
                         compile(ifNode.getThenBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
 
             BranchCallback falseCallback = new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     if (ifNode.getElseBody() != null) {
                         compile(ifNode.getElseBody(), context, expr);
                     } else {
                         if (expr) context.loadNil();
                     }
                 }
             };
             
             // normal
             compile(actualCondition, context, true);
             context.performBooleanBranch(trueCallback, falseCallback);
         }
     }
 
     public void compileInstAsgn(Node node, BodyCompiler context, boolean expr) {
         final InstAsgnNode instAsgnNode = (InstAsgnNode) node;
 
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(instAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.assignInstanceVariable(instAsgnNode.getName(), value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileInstVar(Node node, BodyCompiler context, boolean expr) {
         InstVarNode instVarNode = (InstVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.retrieveInstanceVariable(instVarNode.getName());
         } else {
             context.retrieveInstanceVariable(instVarNode.getName());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileIter(Node node, BodyCompiler context) {
         final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (iterNode.getBodyNode() != null) {
                             compile(iterNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final CompilerCallback closureArgs = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (iterNode.getVarNode() != null) {
                             compileAssignment(iterNode.getVarNode(), context, false);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = BlockBody.getArgumentTypeWackyHack(iterNode);
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(iterNode.getBodyNode());
         inspector.inspect(iterNode.getVarNode());
         
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewClosure(iterNode.getPosition().getStartLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewClosure(iterNode.getPosition().getStartLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public void compileLocalAsgn(Node node, BodyCompiler context, boolean expr) {
         final LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         // just push nil for pragmas
         if (ASTInspector.PRAGMAS.contains(localAsgnNode.getName())) {
             if (expr) context.loadNil();
         } else {
             CompilerCallback value = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compile(localAsgnNode.getValueNode(), context,true);
                 }
             };
 
             context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), value, expr);
         }
     }
 
     public void compileLocalAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         // "assignment" means the value is already on the stack
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth(), expr);
     }
 
     public void compileLocalVar(Node node, BodyCompiler context, boolean expr) {
         LocalVarNode localVarNode = (LocalVarNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
         } else {
             context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileMatch(Node node, BodyCompiler context, boolean expr) {
         MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context,true);
 
         context.match();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch2(Node node, BodyCompiler context, boolean expr) {
         final Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         CompilerCallback value = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(matchNode.getValueNode(), context,true);
             }
         };
 
         context.match2(value);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMatch3(Node node, BodyCompiler context, boolean expr) {
         Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context,true);
         compile(matchNode.getValueNode(), context,true);
 
         context.match3();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileModule(Node node, BodyCompiler context, boolean expr) {
         final ModuleNode moduleNode = (ModuleNode) node;
 
         final Node cpathNode = moduleNode.getCPath();
 
         CompilerCallback bodyCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (moduleNode.getBodyNode() != null) {
                             compile(moduleNode.getBodyNode(), context,true);
                         }
                         context.loadNil();
                     }
                 };
 
         CompilerCallback pathCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 compile(leftNode, context,true);
                             } else {
                                 context.loadNil();
                             }
                         } else if (cpathNode instanceof Colon3Node) {
                             context.loadObject();
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(moduleNode.getBodyNode());
 
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback, inspector);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileMultipleAsgn(Node node, BodyCompiler context, boolean expr) {
         MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         if (expr) {
             // need the array, use unoptz version
             compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
         } else {
             // try optz version
             compileOptimizedMultipleAsgn(multipleAsgnNode, context, expr);
         }
     }
 
     private void compileOptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         // expect value to be an array of nodes
         if (multipleAsgnNode.getValueNode() instanceof ArrayNode) {
             // head must not be null and there must be no "args" (like *arg)
             if (multipleAsgnNode.getHeadNode() != null && multipleAsgnNode.getArgsNode() == null) {
                 // sizes must match
                 if (multipleAsgnNode.getHeadNode().size() == ((ArrayNode)multipleAsgnNode.getValueNode()).size()) {
                     // "head" must have no non-trivial assigns (array groupings, basically)
                     boolean normalAssigns = true;
                     for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                         if (asgn instanceof ListNode) {
                             normalAssigns = false;
                             break;
                         }
                     }
                     
                     if (normalAssigns) {
                         // only supports simple parallel assignment of up to 10 values to the same number of assignees
                         int size = multipleAsgnNode.getHeadNode().size();
                         if (size >= 2 && size <= 10) {
                             ArrayNode values = (ArrayNode)multipleAsgnNode.getValueNode();
                             for (Node value : values.childNodes()) {
                                 compile(value, context, true);
                             }
                             context.reverseValues(size);
                             for (Node asgn : multipleAsgnNode.getHeadNode().childNodes()) {
                                 compileAssignment(asgn, context, false);
                             }
                             return;
                         }
                     }
                 }
             }
         }
 
         // if we get here, no optz cases work; fall back on unoptz.
         compileUnoptimizedMultipleAsgn(multipleAsgnNode, context, expr);
     }
 
     private void compileUnoptimizedMultipleAsgn(MultipleAsgnNode multipleAsgnNode, BodyCompiler context, boolean expr) {
         compile(multipleAsgnNode.getValueNode(), context, true);
 
         compileMultipleAsgnAssignment(multipleAsgnNode, context, expr);
     }
 
     public void compileMultipleAsgnAssignment(Node node, BodyCompiler context, boolean expr) {
         final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         compileAssignment(assignNode, context, false);
                     }
                 };
 
         CompilerCallback argsCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         Node argsNode = multipleAsgnNode.getArgsNode();
                         if (argsNode instanceof StarNode) {
                             // done processing args
                             context.consumeCurrentValue();
                         } else {
                             // assign to appropriate variable
                             compileAssignment(argsNode, context, false);
                         }
                     }
                 };
 
         if (multipleAsgnNode.getHeadNode() == null) {
             if (multipleAsgnNode.getArgsNode() == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             } else {
                 if (multipleAsgnNode.getArgsNode() instanceof StarNode) {
                     // do nothing
                 } else {
                     context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
 
                     context.forEachInValueArray(0, 0, null, null, argsCallback);
                 }
             }
         } else {
             context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
             
             if (multipleAsgnNode.getArgsNode() == null) {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, null);
             } else {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, argsCallback);
             }
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNewline(Node node, BodyCompiler context, boolean expr) {
         // TODO: add trace call?
         context.lineNumber(node.getPosition());
 
         context.setLinePosition(node.getPosition());
 
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) context.traceLine();
+
         NewlineNode newlineNode = (NewlineNode) node;
 
         compile(newlineNode.getNextNode(), context, expr);
     }
 
     public void compileNext(Node node, BodyCompiler context, boolean expr) {
         final NextNode nextNode = (NextNode) node;
 
         CompilerCallback valueCallback = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (nextNode.getValueNode() != null) {
                             compile(nextNode.getValueNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileNthRef(Node node, BodyCompiler context, boolean expr) {
         NthRefNode nthRefNode = (NthRefNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.nthRef(nthRefNode.getMatchNumber());
         } else {
             context.nthRef(nthRefNode.getMatchNumber());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileNil(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadNil();
                 context.pollThreadEvents();
             }
         } else {
             context.loadNil();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileNot(Node node, BodyCompiler context, boolean expr) {
         NotNode notNode = (NotNode) node;
 
         compile(notNode.getConditionNode(), context, true);
 
         context.negateCurrentValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnAnd(Node node, BodyCompiler context, boolean expr) {
         final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
 
         compile(andNode.getFirstNode(), context,true);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(BodyCompiler context) {
                         compile(andNode.getSecondNode(), context,true);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnOr(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         if (needsDefinitionCheck(orNode.getFirstNode())) {
             compileGetDefinitionBase(orNode.getFirstNode(), context);
 
             context.isNull(new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getSecondNode(), context,true);
                         }
                     }, new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getFirstNode(), context,true);
                             context.duplicateCurrentValue();
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                         //Do nothing
                                         }
                                     },
                                     new BranchCallback() {
 
                                         public void branch(BodyCompiler context) {
                                             context.consumeCurrentValue();
                                             compile(orNode.getSecondNode(), context,true);
                                         }
                                     });
                         }
                     });
         } else {
             compile(orNode.getFirstNode(), context,true);
             context.duplicateCurrentValue();
             context.performBooleanBranch(new BranchCallback() {
                 public void branch(BodyCompiler context) {
                 //Do nothing
                 }
             },
             new BranchCallback() {
                 public void branch(BodyCompiler context) {
                     context.consumeCurrentValue();
                     compile(orNode.getSecondNode(), context,true);
                 }
             });
 
         }
 
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     /**
      * Check whether the given node is considered always "defined" or whether it
      * has some form of definition check.
      *
      * @param node Then node to check
      * @return Whether the type of node represents a possibly undefined construct
      */
     private boolean needsDefinitionCheck(Node node) {
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE:
         case CLASSVARDECLNODE:
         case CONSTDECLNODE:
         case DASGNNODE:
         case GLOBALASGNNODE:
         case LOCALASGNNODE:
         case MULTIPLEASGNNODE:
         case OPASGNNODE:
         case OPELEMENTASGNNODE:
         case DVARNODE:
         case FALSENODE:
         case TRUENODE:
         case LOCALVARNODE:
         case MATCH2NODE:
         case MATCH3NODE:
         case NILNODE:
         case SELFNODE:
             // all these types are immediately considered "defined"
             return false;
         default:
             return true;
         }
     }
 
     public void compileOpAsgn(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         if (opAsgnNode.getOperatorName().equals("||")) {
             compileOpAsgnWithOr(opAsgnNode, context, true);
         } else if (opAsgnNode.getOperatorName().equals("&&")) {
             compileOpAsgnWithAnd(opAsgnNode, context, true);
         } else {
             compileOpAsgnWithMethod(opAsgnNode, context, true);
         }
 
         context.pollThreadEvents();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithOr(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(opAsgnNode.getValueNode());
         
         context.getInvocationCompiler().invokeOpAsgnWithOr(opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithAnd(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
             }
         };
         
         ArgumentsCallback argsCallback = getArgsCallback(opAsgnNode.getValueNode());
         
         context.getInvocationCompiler().invokeOpAsgnWithAnd(opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpAsgnWithMethod(Node node, BodyCompiler context, boolean expr) {
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final CompilerCallback receiverCallback = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(opAsgnNode.getReceiverNode(), context, true); // [recv]
                     }
                 };
 
         // eval new value, call operator on old value, and assign
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 compile(opAsgnNode.getValueNode(), context, true);
             }
         };
         
         context.getInvocationCompiler().invokeOpAsgnWithMethod(opAsgnNode.getOperatorName(), opAsgnNode.getVariableName(), opAsgnNode.getVariableNameAsgn(), receiverCallback, argsCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgn(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
         
         if (opElementAsgnNode.getOperatorName() == "||") {
             compileOpElementAsgnWithOr(node, context, expr);
         } else if (opElementAsgnNode.getOperatorName() == "&&") {
             compileOpElementAsgnWithAnd(node, context, expr);
         } else {
             compileOpElementAsgnWithMethod(node, context, expr);
         }
     }
     
     private class OpElementAsgnArgumentsCallback implements ArgumentsCallback  {
         private Node node;
 
         public OpElementAsgnArgumentsCallback(Node node) {
             this.node = node;
         }
         
         public int getArity() {
             switch (node.getNodeType()) {
             case ARGSCATNODE:
             case ARGSPUSHNODE:
             case SPLATNODE:
                 return -1;
             case ARRAYNODE:
                 ArrayNode arrayNode = (ArrayNode)node;
                 if (arrayNode.size() == 0) {
                     return 0;
                 } else if (arrayNode.size() > 3) {
                     return -1;
                 } else {
                     return ((ArrayNode)node).size();
                 }
             default:
                 return 1;
             }
         }
 
         public void call(BodyCompiler context) {
             if (getArity() == 1) {
                 // if arity 1, just compile the one element to save us the array cost
                 compile(((ArrayNode)node).get(0), context,true);
             } else {
                 // compile into array
                 compileArguments(node, context);
             }
         }
     };
 
     public void compileOpElementAsgnWithOr(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = new OpElementAsgnArgumentsCallback(opElementAsgnNode.getArgsNode());
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithOr(receiverCallback, argsCallback, valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgnWithAnd(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context, true);
             }
         };
 
         ArgumentsCallback argsCallback = new OpElementAsgnArgumentsCallback(opElementAsgnNode.getArgsNode()); 
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context, true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithAnd(receiverCallback, argsCallback, valueCallback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOpElementAsgnWithMethod(Node node, BodyCompiler context, boolean expr) {
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         CompilerCallback receiverCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getReceiverNode(), context,true);
             }
         };
 
         ArgumentsCallback argsCallback = getArgsCallback(opElementAsgnNode.getArgsNode());
 
         CompilerCallback valueCallback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 compile(opElementAsgnNode.getValueNode(), context,true);
             }
         };
 
         context.getInvocationCompiler().opElementAsgnWithMethod(receiverCallback, argsCallback, valueCallback, opElementAsgnNode.getOperatorName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileOr(Node node, BodyCompiler context, boolean expr) {
         final OrNode orNode = (OrNode) node;
 
         if (orNode.getFirstNode().getNodeType().alwaysTrue()) {
             // compile first node only
             compile(orNode.getFirstNode(), context, expr);
         } else if (orNode.getFirstNode().getNodeType().alwaysFalse()) {
             // compile first node as non-expr and compile second node
             compile(orNode.getFirstNode(), context, false);
             compile(orNode.getSecondNode(), context, expr);
         } else {
             compile(orNode.getFirstNode(), context, true);
 
             BranchCallback longCallback = new BranchCallback() {
 
                         public void branch(BodyCompiler context) {
                             compile(orNode.getSecondNode(), context, true);
                         }
                     };
 
             context.performLogicalOr(longCallback);
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compilePostExe(Node node, BodyCompiler context, boolean expr) {
         final PostExeNode postExeNode = (PostExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (postExeNode.getBodyNode() != null) {
                             compile(postExeNode.getBodyNode(), context, true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.createNewEndBlock(closureBody);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compilePreExe(Node node, BodyCompiler context, boolean expr) {
         final PreExeNode preExeNode = (PreExeNode) node;
 
         // create the closure class and instantiate it
         final CompilerCallback closureBody = new CompilerCallback() {
 
                     public void call(BodyCompiler context) {
                         if (preExeNode.getBodyNode() != null) {
                             compile(preExeNode.getBodyNode(), context,true);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.runBeginBlock(preExeNode.getScope(), closureBody);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRedo(Node node, BodyCompiler context, boolean expr) {
         //RedoNode redoNode = (RedoNode)node;
 
         context.issueRedoEvent();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRegexp(Node node, BodyCompiler context, boolean expr) {
         RegexpNode reNode = (RegexpNode) node;
 
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.createNewRegexp(reNode.getValue(), reNode.getOptions());
         } else {
             context.createNewRegexp(reNode.getValue(), reNode.getOptions());
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileRescue(Node node, BodyCompiler context, boolean expr) {
         compileRescueInternal(node, context, false);
         
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     private void compileRescueInternal(Node node, BodyCompiler context, final boolean light) {
         final RescueNode rescueNode = (RescueNode) node;
 
         BranchCallback body = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 if (rescueNode.getBodyNode() != null) {
                     compile(rescueNode.getBodyNode(), context, true);
                 } else {
                     context.loadNil();
                 }
 
                 if (rescueNode.getElseNode() != null) {
                     context.consumeCurrentValue();
                     compile(rescueNode.getElseNode(), context, true);
                 }
             }
         };
 
         BranchCallback rubyHandler = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 compileRescueBodyInternal(rescueNode.getRescueNode(), context, light);
             }
         };
 
         ASTInspector rescueInspector = new ASTInspector();
         rescueInspector.inspect(rescueNode.getRescueNode());
         if (light) {
             context.performRescueLight(body, rubyHandler, rescueInspector.getFlag(ASTInspector.RETRY));
         } else {
             context.performRescue(body, rubyHandler, rescueInspector.getFlag(ASTInspector.RETRY));
         }
     }
 
     private void compileRescueBodyInternal(Node node, BodyCompiler context, final boolean light) {
         final RescueBodyNode rescueBodyNode = (RescueBodyNode) node;
 
         context.loadException();
 
         final Node exceptionList = rescueBodyNode.getExceptionNodes();
         ArgumentsCallback rescueArgs = getArgsCallback(exceptionList);
         if (rescueArgs == null) rescueArgs = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 context.loadStandardError();
             }
         };
 
         context.checkIsExceptionHandled(rescueArgs);
 
         BranchCallback trueBranch = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 // check if it's an immediate, and don't outline
                 Node realBody = rescueBodyNode.getBodyNode();
                 if (realBody instanceof NewlineNode) {
                     context.setLinePosition(realBody.getPosition());
                     while (realBody instanceof NewlineNode) {
                         realBody = ((NewlineNode)realBody).getNextNode();
                     }
                 }
 
                 if (realBody.getNodeType().isImmediate()) {
                     compile(realBody, context, true);
                     context.clearErrorInfo();
                 } else {
                     context.storeExceptionInErrorInfo();
                     if (light) {
                         compile(rescueBodyNode.getBodyNode(), context, true);
                     } else {
                         BodyCompiler nestedBody = context.outline("rescue_line_" + rescueBodyNode.getPosition().getStartLine());
                         compile(rescueBodyNode.getBodyNode(), nestedBody, true);
                         nestedBody.endBody();
                     }
 
                     // FIXME: this should reset to what it was before
                     context.clearErrorInfo();
                 }
             }
         };
 
         BranchCallback falseBranch = new BranchCallback() {
             public void branch(BodyCompiler context) {
                 if (rescueBodyNode.getOptRescueNode() != null) {
                     compileRescueBodyInternal(rescueBodyNode.getOptRescueNode(), context, light);
                 } else {
                     context.rethrowException();
                 }
             }
         };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public void compileRetry(Node node, BodyCompiler context, boolean expr) {
         context.pollThreadEvents();
 
         context.issueRetryEvent();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileReturn(Node node, BodyCompiler context, boolean expr) {
         ReturnNode returnNode = (ReturnNode) node;
 
         if (returnNode.getValueNode() != null) {
             compile(returnNode.getValueNode(), context,true);
         } else {
             context.loadNil();
         }
 
         context.performReturn();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector) {
         compileRoot(node, context, inspector, true, true);
     }
 
     public void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector, boolean load, boolean main) {
         RootNode rootNode = (RootNode) node;
         StaticScope staticScope = rootNode.getStaticScope();
 
         context.startScript(staticScope);
 
         // force static scope to claim restarg at 0, so it only implements the [] version of __file__
         staticScope.setRestArg(-2);
 
         // create method for toplevel of script
         BodyCompiler methodCompiler = context.startFileMethod(null, staticScope, inspector);
 
         Node nextNode = rootNode.getBodyNode();
         if (nextNode != null) {
             if (nextNode.getNodeType() == NodeType.BLOCKNODE) {
                 // it's a multiple-statement body, iterate over all elements in turn and chain if it get too long
                 BlockNode blockNode = (BlockNode) nextNode;
 
                 for (int i = 0; i < blockNode.size(); i++) {
                     if ((i + 1) % RubyInstanceConfig.CHAINED_COMPILE_LINE_COUNT == 0) {
                         methodCompiler = methodCompiler.chainToMethod("__file__from_line_" + (i + 1));
                     }
                     compile(blockNode.get(i), methodCompiler, i + 1 >= blockNode.size());
                 }
             } else {
                 // single-statement body, just compile it
                 compile(nextNode, methodCompiler,true);
             }
         } else {
             methodCompiler.loadNil();
         }
 
         methodCompiler.endBody();
 
         context.endScript(load, main);
     }
 
     public void compileSelf(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) context.retrieveSelf();
         } else {
             context.retrieveSelf();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileSplat(Node node, BodyCompiler context, boolean expr) {
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context, true);
 
         context.splatCurrentValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileStr(Node node, BodyCompiler context, boolean expr) {
         StrNode strNode = (StrNode) node;
 
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             if (strNode instanceof FileNode) {
                 context.loadFilename();
             } else {
                 context.createNewString(strNode.getValue());
             }
         }
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileSuper(Node node, BodyCompiler context, boolean expr) {
         final SuperNode superNode = (SuperNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(superNode.getArgsNode());
 
         CompilerCallback closureArg = getBlock(superNode.getIterNode());
 
         context.getInvocationCompiler().invokeDynamic(null, null, argsCallback, CallType.SUPER, closureArg, superNode.getIterNode() instanceof IterNode);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSValue(Node node, BodyCompiler context, boolean expr) {
         SValueNode svalueNode = (SValueNode) node;
 
         compile(svalueNode.getValue(), context,true);
 
         context.singlifySplattedValue();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileSymbol(Node node, BodyCompiler context, boolean expr) {
         context.createNewSymbol(((SymbolNode) node).getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }    
     
     public void compileToAry(Node node, BodyCompiler context, boolean expr) {
         ToAryNode toAryNode = (ToAryNode) node;
 
         compile(toAryNode.getValue(), context,true);
 
         context.aryToAry();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileTrue(Node node, BodyCompiler context, boolean expr) {
         if (RubyInstanceConfig.PEEPHOLE_OPTZ) {
             if (expr) {
                 context.loadTrue();
                 context.pollThreadEvents();
             }
         } else {
             context.loadTrue();
             context.pollThreadEvents();
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileUndef(Node node, BodyCompiler context, boolean expr) {
         context.undefMethod(((UndefNode) node).getName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileUntil(Node node, BodyCompiler context, boolean expr) {
         final UntilNode untilNode = (UntilNode) node;
 
         if (untilNode.getConditionNode().getNodeType().alwaysTrue() &&
                 untilNode.evaluateAtStart()) {
             // condition is always true, just compile it and not body
             compile(untilNode.getConditionNode(), context, false);
             if (expr) context.loadNil();
         } else {
             BranchCallback condition = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(untilNode.getConditionNode(), context, true);
                     context.negateCurrentValue();
                 }
             };
 
             BranchCallback body = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     if (untilNode.getBodyNode() != null) {
                         compile(untilNode.getBodyNode(), context, true);
                     }
                 }
             };
 
             if (untilNode.containsNonlocalFlow) {
                 context.performBooleanLoopSafe(condition, body, untilNode.evaluateAtStart());
             } else {
                 context.performBooleanLoopLight(condition, body, untilNode.evaluateAtStart());
             }
 
             context.pollThreadEvents();
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileVAlias(Node node, BodyCompiler context, boolean expr) {
         VAliasNode valiasNode = (VAliasNode) node;
 
         context.aliasGlobal(valiasNode.getNewName(), valiasNode.getOldName());
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileVCall(Node node, BodyCompiler context, boolean expr) {
         VCallNode vcallNode = (VCallNode) node;
         
         context.getInvocationCompiler().invokeDynamic(vcallNode.getName(), null, null, CallType.VARIABLE, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileWhile(Node node, BodyCompiler context, boolean expr) {
         final WhileNode whileNode = (WhileNode) node;
 
         if (whileNode.getConditionNode().getNodeType().alwaysFalse() &&
                 whileNode.evaluateAtStart()) {
             // do nothing
             if (expr) context.loadNil();
         } else {
             BranchCallback condition = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     compile(whileNode.getConditionNode(), context, true);
                 }
             };
 
             BranchCallback body = new BranchCallback() {
 
                 public void branch(BodyCompiler context) {
                     if (whileNode.getBodyNode() != null) {
                         compile(whileNode.getBodyNode(), context, true);
                     }
                 }
             };
 
             if (whileNode.containsNonlocalFlow) {
                 context.performBooleanLoopSafe(condition, body, whileNode.evaluateAtStart());
             } else {
                 context.performBooleanLoopLight(condition, body, whileNode.evaluateAtStart());
             }
 
             context.pollThreadEvents();
             // TODO: don't require pop
             if (!expr) context.consumeCurrentValue();
         }
     }
 
     public void compileXStr(Node node, BodyCompiler context, boolean expr) {
         final XStrNode xstrNode = (XStrNode) node;
 
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return 1;
             }
 
             public void call(BodyCompiler context) {
                 context.createNewString(xstrNode.getValue());
             }
         };
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileYield(Node node, BodyCompiler context, boolean expr) {
         final YieldNode yieldNode = (YieldNode) node;
 
         ArgumentsCallback argsCallback = getArgsCallback(yieldNode.getArgsNode());
 
         // TODO: This filtering is kind of gross...it would be nice to get some parser help here
         if (argsCallback == null || argsCallback.getArity() == 0) {
             context.getInvocationCompiler().yieldSpecific(argsCallback);
         } else if ((argsCallback.getArity() == 1 || argsCallback.getArity() == 2 || argsCallback.getArity() == 3) && yieldNode.getExpandArguments()) {
             // send it along as arity-specific, we don't need the array
             context.getInvocationCompiler().yieldSpecific(argsCallback);
         } else {
             CompilerCallback argsCallback2 = null;
             if (yieldNode.getArgsNode() != null) {
                 argsCallback2 = new CompilerCallback() {
                     public void call(BodyCompiler context) {
                         compile(yieldNode.getArgsNode(), context,true);
                     }
                 };
             }
 
             context.getInvocationCompiler().yield(argsCallback2, yieldNode.getExpandArguments());
         }
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileZArray(Node node, BodyCompiler context, boolean expr) {
         boolean doit = expr || !RubyInstanceConfig.PEEPHOLE_OPTZ;
         boolean popit = !RubyInstanceConfig.PEEPHOLE_OPTZ && !expr;
 
         if (doit) {
             context.createEmptyArray();
         }
 
         if (popit) context.consumeCurrentValue();
     }
 
     public void compileZSuper(Node node, BodyCompiler context, boolean expr) {
         ZSuperNode zsuperNode = (ZSuperNode) node;
 
         CompilerCallback closure = getBlock(zsuperNode.getIterNode());
 
         context.callZSuper(closure);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsCatArguments(Node node, BodyCompiler context, boolean expr) {
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compileArguments(argsCatNode.getFirstNode(), context);
         // arguments compilers always create IRubyObject[], but we want to use RubyArray.concat here;
         // FIXME: as a result, this is NOT efficient, since it creates and then later unwraps an array
         context.createNewArray(true);
         compile(argsCatNode.getSecondNode(), context,true);
         context.splatCurrentValue();
         context.concatArrays();
         context.convertToJavaArray();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArgsPushArguments(Node node, BodyCompiler context, boolean expr) {
         ArgsPushNode argsPushNode = (ArgsPushNode) node;
         compile(argsPushNode.getFirstNode(), context,true);
         compile(argsPushNode.getSecondNode(), context,true);
         context.appendToArray();
         context.convertToJavaArray();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 
     public void compileArrayArguments(Node node, BodyCompiler context, boolean expr) {
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(BodyCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context,true);
                     }
                 };
 
         context.setLinePosition(arrayNode.getPosition());
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     // leave as a normal array
     }
 
     public void compileSplatArguments(Node node, BodyCompiler context, boolean expr) {
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context,true);
         context.splatCurrentValue();
         context.convertToJavaArray();
         // TODO: don't require pop
         if (!expr) context.consumeCurrentValue();
     }
 }
diff --git a/src/org/jruby/compiler/ASTInspector.java b/src/org/jruby/compiler/ASTInspector.java
index 3036a40315..61b308b4eb 100644
--- a/src/org/jruby/compiler/ASTInspector.java
+++ b/src/org/jruby/compiler/ASTInspector.java
@@ -1,801 +1,803 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler;
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.AssignableNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockAcceptingNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.Hash19Node;
 import org.jruby.ast.IArgumentNode;
 import org.jruby.ast.IScopingNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.OpAsgnAndNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.TrueNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author headius
  */
 public class ASTInspector {
     public static final int BLOCK_ARG = 0x1; // block argument to the method
     public static final int CLOSURE = 0x2; // closure present
     public static final int CLASS = 0x4; // class present
     public static final int METHOD = 0x8; // method table mutations, def, defs, undef, alias
     public static final int EVAL = 0x10; // likely call to eval
     public static final int FRAME_AWARE = 0x20; // makes calls that are aware of the frame
     public static final int FRAME_SELF = 0x40; // makes calls that are aware of the frame's self
     public static final int FRAME_VISIBILITY = 0x80; // makes calls that are aware of the frame's visibility
     public static final int FRAME_BLOCK = 0x100; // makes calls that are aware of the frame's block
     public static final int FRAME_NAME = 0x200; // makes calls that are aware of the frame's name
     public static final int BACKREF = 0x400; // makes calls that set or get backref
     public static final int LASTLINE = 0x800; // makes calls that set or get lastline
     public static final int FRAME_CLASS = 0x1000; // makes calls that are aware of the frame's class
     public static final int OPT_ARGS = 0x2000; // optional arguments to the method
     public static final int REST_ARG = 0x4000; // rest arg to the method
     public static final int SCOPE_AWARE = 0x8000; // makes calls that are aware of the scope
     public static final int ZSUPER = 0x10000; // makes a zero-argument super call
     public static final int CONSTANT = 0x20000; // accesses or sets constants
     public static final int CLASS_VAR = 0x40000; // accesses or sets class variables
     public static final int SUPER = 0x80000; // makes normal super call
     public static final int RETRY = 0x100000; // contains a retry
     
     private int flags;
     
     // pragmas
     private boolean noFrame;
     
     public static Set<String> FRAME_AWARE_METHODS = Collections.synchronizedSet(new HashSet<String>());
     public static Set<String> SCOPE_AWARE_METHODS = Collections.synchronizedSet(new HashSet<String>());
     
     public static Set<String> PRAGMAS = Collections.synchronizedSet(new HashSet<String>());
     
     static {
         FRAME_AWARE_METHODS.add("eval");
         FRAME_AWARE_METHODS.add("module_eval");
         FRAME_AWARE_METHODS.add("class_eval");
         FRAME_AWARE_METHODS.add("instance_eval");
         FRAME_AWARE_METHODS.add("binding");
         FRAME_AWARE_METHODS.add("public");
         FRAME_AWARE_METHODS.add("private");
         FRAME_AWARE_METHODS.add("protected");
         FRAME_AWARE_METHODS.add("module_function");
         FRAME_AWARE_METHODS.add("block_given?");
         FRAME_AWARE_METHODS.add("iterator?");
         
         SCOPE_AWARE_METHODS.addAll(RubyModule.SCOPE_CAPTURING_METHODS);
         
         PRAGMAS.add("__NOFRAME__");
     }
     
     public void disable() {
         flags = 0xFFFFFFFF;
     }
 
     public CallConfiguration getCallConfig() {
         if (hasFrameAwareMethods() || hasClosure() || !(noFrame() || RubyInstanceConfig.FRAMELESS_COMPILE_ENABLED)) {
             // We're doing normal framed compilation or the method needs a frame
             if (hasClosure() || hasScopeAwareMethods()) {
                 // The method also needs a scope, do both
                 return CallConfiguration.FrameFullScopeFull;
             } else {
                 if (hasConstant() || hasMethod() || hasClass() || hasClassVar()) {
                     // The method doesn't need a scope, but has static scope needs; use a dummy scope
                     return CallConfiguration.FrameFullScopeDummy;
                 } else {
                     // The method doesn't need a scope or static scope; frame only
                     return CallConfiguration.FrameFullScopeNone;
                 }
             }
         } else {
             if (hasClosure() || hasScopeAwareMethods()) {
                 // TODO: call config with scope but no frame
                 if (RubyInstanceConfig.FASTEST_COMPILE_ENABLED) {
                     return CallConfiguration.FrameNoneScopeFull;
                 } else {
                     return CallConfiguration.FrameBacktraceScopeFull;
                 }
             } else {
                 if (hasConstant() || hasMethod() || hasClass() || hasClassVar()) {
                     if (RubyInstanceConfig.FASTEST_COMPILE_ENABLED || noFrame()) {
                         return CallConfiguration.FrameNoneScopeDummy;
                     } else {
                         return CallConfiguration.FrameBacktraceScopeDummy;
                     }
                 } else {
                     if (RubyInstanceConfig.FASTEST_COMPILE_ENABLED || noFrame()) {
                         return CallConfiguration.FrameNoneScopeNone;
                     } else {
                         return CallConfiguration.FrameBacktraceScopeNone;
                     }
                 }
             }
         }
     }
     
     public static final boolean ENABLED = SafePropertyAccessor.getProperty("jruby.astInspector.enabled", "true").equals("true");
     
     /**
      * Perform an inspection of a subtree or set of subtrees separate from the
      * parent inspection, to make independent decisions based on that subtree(s).
      * 
      * @param nodes The child nodes to walk with a new inspector
      * @return The new inspector resulting from the walk
      */
     public static ASTInspector subInspect(Node... nodes) {
         ASTInspector newInspector = new ASTInspector();
         
         for (Node node : nodes) {
             newInspector.inspect(node);
         }
         
         return newInspector;
     }
     
     public boolean getFlag(int modifier) {
         return (flags & modifier) != 0;
     }
     
     public void setFlag(int modifier) {
         flags |= modifier;
     }
     
     /**
      * Integrate the results of a separate inspection into the state of this
      * inspector.
      * 
      * @param other The other inspector whose state to integrate.
      */
     public void integrate(ASTInspector other) {
         flags |= other.flags;
     }
     
     public void inspect(Node node) {
-        // TODO: This code effectively disables all inspection-based optimizations; none of them are 100% safe yet
-        if (!ENABLED) disable();
+        if (!ENABLED || RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            disable();
+            return;
+        }
 
         if (node == null) return;
         
         switch (node.getNodeType()) {
         case ALIASNODE:
             setFlag(METHOD);
             break;
         case ANDNODE:
             AndNode andNode = (AndNode)node;
             inspect(andNode.getFirstNode());
             inspect(andNode.getSecondNode());
             break;
         case ARGSCATNODE:
             ArgsCatNode argsCatNode = (ArgsCatNode)node;
             inspect(argsCatNode.getFirstNode());
             inspect(argsCatNode.getSecondNode());
             break;
         case ARGSPUSHNODE:
             ArgsPushNode argsPushNode = (ArgsPushNode)node;
             inspect(argsPushNode.getFirstNode());
             inspect(argsPushNode.getSecondNode());
             break;
         case ARGUMENTNODE:
             break;
         case ARRAYNODE:
         case BLOCKNODE:
         case DREGEXPNODE:
         case DSTRNODE:
         case DSYMBOLNODE:
         case DXSTRNODE:
         case LISTNODE:
             ListNode listNode = (ListNode)node;
             for (int i = 0; i < listNode.size(); i++) {
                 inspect(listNode.get(i));
             }
             break;
         case ARGSNODE:
             ArgsNode argsNode = (ArgsNode)node;
             if (argsNode.getBlock() != null) setFlag(BLOCK_ARG);
             if (argsNode.getOptArgs() != null) {
                 setFlag(OPT_ARGS);
                 inspect(argsNode.getOptArgs());
             }
             if (argsNode.getRestArg() == -2 || argsNode.getRestArg() >= 0) setFlag(REST_ARG);
             break;
         case ATTRASSIGNNODE:
             AttrAssignNode attrAssignNode = (AttrAssignNode)node;
             setFlag(FRAME_SELF);
             inspect(attrAssignNode.getArgsNode());
             inspect(attrAssignNode.getReceiverNode());
             break;
         case BACKREFNODE:
             setFlag(BACKREF);
             break;
         case BEGINNODE:
             inspect(((BeginNode)node).getBodyNode());
             break;
         case BIGNUMNODE:
             break;
         case BINARYOPERATORNODE:
             BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)node;
             inspect(binaryOperatorNode.getFirstNode());
             inspect(binaryOperatorNode.getSecondNode());
             break;
         case BLOCKARGNODE:
             break;
         case BLOCKPASSNODE:
             BlockPassNode blockPassNode = (BlockPassNode)node;
             inspect(blockPassNode.getArgsNode());
             inspect(blockPassNode.getBodyNode());
             break;
         case BREAKNODE:
             inspect(((BreakNode)node).getValueNode());
             break;
         case CALLNODE:
             CallNode callNode = (CallNode)node;
             inspect(callNode.getReceiverNode());
             // check for Proc.new, an especially magic method
             if (callNode.getName() == "new" &&
                     callNode.getReceiverNode() instanceof ConstNode &&
                     ((ConstNode)callNode.getReceiverNode()).getName() == "Proc") {
                 // Proc.new needs the caller's block to instantiate a proc
                 setFlag(FRAME_BLOCK);
             }
         case FCALLNODE:
             inspect(((IArgumentNode)node).getArgsNode());
             inspect(((BlockAcceptingNode)node).getIterNode());
         case VCALLNODE:
             INameNode nameNode = (INameNode)node;
             if (FRAME_AWARE_METHODS.contains(nameNode.getName())) {
                 setFlag(FRAME_AWARE);
                 if (nameNode.getName().indexOf("eval") != -1) {
                     setFlag(EVAL);
                 }
             }
             if (SCOPE_AWARE_METHODS.contains(nameNode.getName())) {
                 setFlag(SCOPE_AWARE);
             }
             break;
         case CASENODE:
             CaseNode caseNode = (CaseNode)node;
             inspect(caseNode.getCaseNode());
             for (Node when : caseNode.getCases().childNodes()) inspect(when);
             inspect(caseNode.getElseNode());
             break;
         case CLASSNODE:
             setFlag(CLASS);
             ClassNode classNode = (ClassNode)node;
             inspect(classNode.getCPath());
             inspect(classNode.getSuperNode());
             break;
         case CLASSVARNODE:
             setFlag(CLASS_VAR);
             break;
         case CONSTDECLNODE:
             inspect(((AssignableNode)node).getValueNode());
             setFlag(CONSTANT);
             break;
         case CLASSVARASGNNODE:
             inspect(((AssignableNode)node).getValueNode());
             setFlag(CLASS_VAR);
             break;
         case CLASSVARDECLNODE:
             inspect(((AssignableNode)node).getValueNode());
             setFlag(CLASS_VAR);
             break;
         case COLON2NODE:
             inspect(((Colon2Node)node).getLeftNode());
             break;
         case COLON3NODE:
             break;
         case CONSTNODE:
             setFlag(CONSTANT);
             break;
         case DEFNNODE:
         case DEFSNODE:
             setFlag(METHOD);
             setFlag(FRAME_VISIBILITY);
             break;
         case DEFINEDNODE:
             switch (((DefinedNode)node).getExpressionNode().getNodeType()) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case OPASGNNODE:
             case OPELEMENTASGNNODE:
             case DVARNODE:
             case FALSENODE:
             case TRUENODE:
             case LOCALVARNODE:
             case INSTVARNODE:
             case BACKREFNODE:
             case SELFNODE:
             case VCALLNODE:
             case YIELDNODE:
             case GLOBALVARNODE:
             case CONSTNODE:
             case FCALLNODE:
             case CLASSVARNODE:
                 // ok, we have fast paths
                 inspect(((DefinedNode)node).getExpressionNode());
                 break;
             default:
                 // long, slow way causes disabling
                 disable();
             }
             break;
         case DOTNODE:
             DotNode dotNode = (DotNode)node;
             inspect(dotNode.getBeginNode());
             inspect(dotNode.getEndNode());
             break;
         case DASGNNODE:
             inspect(((AssignableNode)node).getValueNode());
             break;
         case DVARNODE:
             break;
         case ENSURENODE:
             disable();
             break;
         case EVSTRNODE:
             inspect(((EvStrNode)node).getBody());
             break;
         case FALSENODE:
             break;
         case FIXNUMNODE:
             break;
         case FLIPNODE:
             inspect(((FlipNode)node).getBeginNode());
             inspect(((FlipNode)node).getEndNode());
             break;
         case FLOATNODE:
             break;
         case FORNODE:
             setFlag(CLOSURE);
             setFlag(SCOPE_AWARE);
             inspect(((ForNode)node).getIterNode());
             inspect(((ForNode)node).getBodyNode());
             inspect(((ForNode)node).getVarNode());
             break;
         case GLOBALASGNNODE:
             GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode)node;
             if (globalAsgnNode.getName().equals("$_")) {
                 setFlag(LASTLINE);
             } else if (globalAsgnNode.getName().equals("$~")) {
                 setFlag(BACKREF);
             }
             inspect(globalAsgnNode.getValueNode());
             break;
         case GLOBALVARNODE:
             if (((GlobalVarNode)node).getName().equals("$_")) {
                 setFlag(LASTLINE);
             } else if (((GlobalVarNode)node).getName().equals("$~")) {
                 setFlag(BACKREF);
             }
             break;
         case HASHNODE:
             HashNode hashNode = (HashNode)node;
             inspect(hashNode.getListNode());
             break;
         case IFNODE:
             IfNode ifNode = (IfNode)node;
             inspect(ifNode.getCondition());
             inspect(ifNode.getThenBody());
             inspect(ifNode.getElseBody());
             break;
         case INSTASGNNODE:
             inspect(((AssignableNode)node).getValueNode());
             break;
         case INSTVARNODE:
             break;
         case ISCOPINGNODE:
             IScopingNode iscopingNode = (IScopingNode)node;
             inspect(iscopingNode.getCPath());
             break;
         case ITERNODE:
             setFlag(CLOSURE);
             break;
         case LAMBDANODE:
             setFlag(CLOSURE);
             break;
         case LOCALASGNNODE:
             LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
             if (PRAGMAS.contains(localAsgnNode.getName())) {
                 if (localAsgnNode.getName().equals("__NOFRAME__")) {
                     noFrame = localAsgnNode.getValueNode() instanceof TrueNode;
                 }
                 break;
             }
             inspect(localAsgnNode.getValueNode());
             break;
         case LOCALVARNODE:
             break;
         case MATCHNODE:
             inspect(((MatchNode)node).getRegexpNode());
             setFlag(BACKREF);
             break;
         case MATCH2NODE:
             Match2Node match2Node = (Match2Node)node;
             inspect(match2Node.getReceiverNode());
             inspect(match2Node.getValueNode());
             setFlag(BACKREF);
             break;
         case MATCH3NODE:
             Match3Node match3Node = (Match3Node)node;
             inspect(match3Node.getReceiverNode());
             inspect(match3Node.getValueNode());
             setFlag(BACKREF);
             break;
         case MODULENODE:
             setFlag(CLASS);
             inspect(((ModuleNode)node).getCPath());
             break;
         case MULTIPLEASGN19NODE:
             MultipleAsgn19Node multipleAsgn19Node = (MultipleAsgn19Node)node;
             inspect(multipleAsgn19Node.getPre());
             inspect(multipleAsgn19Node.getPost());
             inspect(multipleAsgn19Node.getRest());
             inspect(multipleAsgn19Node.getValueNode());
             break;
         case MULTIPLEASGNNODE:
             MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)node;
             inspect(multipleAsgnNode.getArgsNode());
             inspect(multipleAsgnNode.getHeadNode());
             inspect(multipleAsgnNode.getValueNode());
             break;
         case NEWLINENODE:
             inspect(((NewlineNode)node).getNextNode());
             break;
         case NEXTNODE:
             inspect(((NextNode)node).getValueNode());
             break;
         case NILNODE:
             break;
         case NOTNODE:
             inspect(((NotNode)node).getConditionNode());
             break;
         case NTHREFNODE:
             break;
         case OPASGNANDNODE:
             OpAsgnAndNode opAsgnAndNode = (OpAsgnAndNode)node;
             inspect(opAsgnAndNode.getFirstNode());
             inspect(opAsgnAndNode.getSecondNode());
             break;
         case OPASGNNODE:
             OpAsgnNode opAsgnNode = (OpAsgnNode)node;
             inspect(opAsgnNode.getReceiverNode());
             inspect(opAsgnNode.getValueNode());
             break;
         case OPASGNORNODE:
             switch (((OpAsgnOrNode)node).getFirstNode().getNodeType()) {
             case CLASSVARASGNNODE:
             case CLASSVARDECLNODE:
             case CONSTDECLNODE:
             case DASGNNODE:
             case GLOBALASGNNODE:
             case LOCALASGNNODE:
             case MULTIPLEASGNNODE:
             case OPASGNNODE:
             case OPELEMENTASGNNODE:
             case DVARNODE:
             case FALSENODE:
             case TRUENODE:
             case LOCALVARNODE:
             case INSTVARNODE:
             case BACKREFNODE:
             case SELFNODE:
             case VCALLNODE:
             case YIELDNODE:
             case GLOBALVARNODE:
             case CONSTNODE:
             case FCALLNODE:
             case CLASSVARNODE:
                 // ok, we have fast paths
                 inspect(((OpAsgnOrNode)node).getSecondNode());
                 break;
             default:
                 // long, slow way causes disabling for defined
                 disable();
             }
             break;
         case OPELEMENTASGNNODE:
             OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode)node;
             setFlag(FRAME_SELF);
             inspect(opElementAsgnNode.getArgsNode());
             inspect(opElementAsgnNode.getReceiverNode());
             inspect(opElementAsgnNode.getValueNode());
             break;
         case OPTARGNODE:
             inspect(((OptArgNode)node).getValue());
             break;
         case ORNODE:
             OrNode orNode = (OrNode)node;
             inspect(orNode.getFirstNode());
             inspect(orNode.getSecondNode());
             break;
         case POSTEXENODE:
             PostExeNode postExeNode = (PostExeNode)node;
             setFlag(CLOSURE);
             setFlag(SCOPE_AWARE);
             inspect(postExeNode.getBodyNode());
             inspect(postExeNode.getVarNode());
             break;
         case PREEXENODE:
             PreExeNode preExeNode = (PreExeNode)node;
             setFlag(CLOSURE);
             setFlag(SCOPE_AWARE);
             inspect(preExeNode.getBodyNode());
             inspect(preExeNode.getVarNode());
             break;
         case REDONODE:
             break;
         case REGEXPNODE:
             break;
         case ROOTNODE:
             inspect(((RootNode)node).getBodyNode());
             if (((RootNode)node).getBodyNode() instanceof BlockNode) {
                 BlockNode blockNode = (BlockNode)((RootNode)node).getBodyNode();
                 if (blockNode.size() > 500) {
                     // method has more than 500 lines; we'll need to split it
                     // and therefore need to use a heap-based scope
                     setFlag(SCOPE_AWARE);
                 }
             }
             break;
         case RESCUEBODYNODE:
             RescueBodyNode rescueBody = (RescueBodyNode)node;
             inspect(rescueBody.getExceptionNodes());
             inspect(rescueBody.getBodyNode());
             inspect(rescueBody.getOptRescueNode());
             break;
         case RESCUENODE:
             RescueNode rescueNode = (RescueNode)node;
             inspect(rescueNode.getBodyNode());
             inspect(rescueNode.getElseNode());
             inspect(rescueNode.getRescueNode());
             disable();
             break;
         case RETRYNODE:
             setFlag(RETRY);
             break;
         case RETURNNODE:
             inspect(((ReturnNode)node).getValueNode());
             break;
         case SCLASSNODE:
             setFlag(CLASS);
             SClassNode sclassNode = (SClassNode)node;
             inspect(sclassNode.getReceiverNode());
             break;
         case SCOPENODE:
             break;
         case SELFNODE:
             break;
         case SPLATNODE:
             inspect(((SplatNode)node).getValue());
             break;
         case STARNODE:
             break;
         case STRNODE:
             break;
         case SUPERNODE:
             SuperNode superNode = (SuperNode)node;
             inspect(superNode.getArgsNode());
             inspect(superNode.getIterNode());
             setFlag(SUPER);
             break;
         case SVALUENODE:
             inspect(((SValueNode)node).getValue());
             break;
         case SYMBOLNODE:
             break;
         case TOARYNODE:
             inspect(((ToAryNode)node).getValue());
             break;
         case TRUENODE:
             break;
         case UNDEFNODE:
             setFlag(METHOD);
             break;
         case UNTILNODE:
             UntilNode untilNode = (UntilNode)node;
             ASTInspector untilInspector = subInspect(
                     untilNode.getConditionNode(), untilNode.getBodyNode());
             // a while node could receive non-local flow control from any of these:
             // * a closure within the loop
             // * an eval within the loop
             // * a block-arg-based proc called within the loop
             if (untilInspector.getFlag(CLOSURE) || untilInspector.getFlag(EVAL)) {
                 untilNode.containsNonlocalFlow = true;
                 
                 // we set scope-aware to true to force heap-based locals
                 setFlag(SCOPE_AWARE);
             }
             integrate(untilInspector);
             break;
         case VALIASNODE:
             break;
         case WHENNODE:
             inspect(((WhenNode)node).getBodyNode());
             inspect(((WhenNode)node).getExpressionNodes());
             inspect(((WhenNode)node).getNextCase());
             // Because Regexp#=== sets backref, we have to make this backref-aware
             setFlag(BACKREF);
             break;
         case WHILENODE:
             WhileNode whileNode = (WhileNode)node;
             ASTInspector whileInspector = subInspect(
                     whileNode.getConditionNode(), whileNode.getBodyNode());
             // a while node could receive non-local flow control from any of these:
             // * a closure within the loop
             // * an eval within the loop
             // * a block-arg-based proc called within the loop
             if (whileInspector.getFlag(CLOSURE) || whileInspector.getFlag(EVAL) || getFlag(BLOCK_ARG)) {
                 whileNode.containsNonlocalFlow = true;
                 
                 // we set scope-aware to true to force heap-based locals
                 setFlag(SCOPE_AWARE);
             }
             integrate(whileInspector);
             break;
         case XSTRNODE:
             break;
         case YIELDNODE:
             inspect(((YieldNode)node).getArgsNode());
             break;
         case ZARRAYNODE:
             break;
         case ZEROARGNODE:
             break;
         case ZSUPERNODE:
             setFlag(SCOPE_AWARE);
             setFlag(ZSUPER);
             inspect(((ZSuperNode)node).getIterNode());
             break;
         default:
             // encountered a node we don't recognize, set everything to true to disable optz
             assert false : "All nodes should be accounted for in AST inspector: " + node;
             disable();
         }
     }
 
     public boolean hasClass() {
         return getFlag(CLASS);
     }
 
     public boolean hasClosure() {
         return getFlag(CLOSURE);
     }
 
     /**
      * Whether the tree under inspection contains any method-table mutations,
      * including def, defs, undef, and alias.
      * 
      * @return True if there are mutations, false otherwise
      */
     public boolean hasMethod() {
         return getFlag(METHOD);
     }
 
     public boolean hasFrameAwareMethods() {
         return getFlag(
                 FRAME_AWARE | FRAME_BLOCK | FRAME_CLASS | FRAME_NAME | FRAME_SELF | FRAME_VISIBILITY |
                 CLOSURE | EVAL | ZSUPER | SUPER);
     }
 
     public boolean hasScopeAwareMethods() {
         return getFlag(SCOPE_AWARE | BACKREF | LASTLINE);
     }
 
     public boolean hasBlockArg() {
         return getFlag(BLOCK_ARG);
     }
 
     public boolean hasOptArgs() {
         return getFlag(OPT_ARGS);
     }
 
     public boolean hasRestArg() {
         return getFlag(REST_ARG);
     }
     
     public boolean hasConstant() {
         return getFlag(CONSTANT);
     }
     
     public boolean hasClassVar() {
         return getFlag(CLASS_VAR);
     }
     
     public boolean noFrame() {
         return noFrame;
     }
 }
diff --git a/src/org/jruby/compiler/BodyCompiler.java b/src/org/jruby/compiler/BodyCompiler.java
index 13c6c87033..0db028a8f5 100644
--- a/src/org/jruby/compiler/BodyCompiler.java
+++ b/src/org/jruby/compiler/BodyCompiler.java
@@ -1,599 +1,603 @@
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
 
 package org.jruby.compiler;
 
 import java.util.List;
 import java.util.Map;
 import org.jruby.ast.NodeType;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author headius
  */
 public interface BodyCompiler {
     /**
      * End compilation for the method associated with the specified token. This should
      * close out all structures created for compilation of the method.
      * 
      * @param token A token identifying the method to be terminated.
      */
     public void endBody();
     
     /**
      * As code executes, values are assumed to be "generated", often by being pushed
      * on to some execution stack. Generally, these values are consumed by other
      * methods on the context, but occasionally a value must be "thrown out". This method
      * provides a way to discard the previous value generated by some other call(s).
      */
     public void consumeCurrentValue();
     
     /**
      * Push a copy the topmost value on the stack.
      */
     public void duplicateCurrentValue();
     
     /**
      * For logging, println the object reference currently atop the stack
      */
     public void aprintln();
     
     /**
      * Swap the top and second values on the stack.
      */
     public void swapValues();
 
     /**
      * Reverse the top n values on the stack.
      *
      * @param n The number of values to reverse.
      */
     public void reverseValues(int n);
     
     /**
      * This method provides a way to specify a line number for the current piece of code
      * being compiled. The compiler may use this information to create debugging
      * information in a bytecode-format-dependent way.
      * 
      * @param position The ISourcePosition information to use.
      */
     public void lineNumber(ISourcePosition position);
     
     public VariableCompiler getVariableCompiler();
     
     public InvocationCompiler getInvocationCompiler();
     
     /**
      * Retrieve the current "self" and put a reference on top of the stack.
      */
     public void retrieveSelf();
     
     /**
      * Retrieve the current "self" object's metaclass and put a reference on top of the stack
      */
     public void retrieveSelfClass();
     
     public void retrieveClassVariable(String name);
     
     public void assignClassVariable(String name);
     
     public void assignClassVariable(String name, CompilerCallback value);
     
     public void declareClassVariable(String name);
     
     public void declareClassVariable(String name, CompilerCallback value);
     
     /**
      * Generate a new "Fixnum" value.
      */
     public void createNewFixnum(long value);
 
     /**
      * Generate a new "Float" value.
      */
     public void createNewFloat(double value);
 
     /**
      * Generate a new "Bignum" value.
      */
     public void createNewBignum(java.math.BigInteger value);
     
     /**
      * Generate a new "String" value.
      */
     public void createNewString(ByteList value);
 
     /**
      * Generate a new dynamic "String" value.
      */
     public void createNewString(ArrayCallback callback, int count);
     public void createNewSymbol(ArrayCallback callback, int count);
 
     /**
      * Generate a new "Symbol" value (or fetch the existing one).
      */
     public void createNewSymbol(String name);
     
     public void createObjectArray(Object[] elementArray, ArrayCallback callback);
 
     /**
      * Combine the top <pre>elementCount</pre> elements into a single element, generally
      * an array or similar construct. The specified number of elements are consumed and
      * an aggregate element remains.
      * 
      * @param elementCount The number of elements to consume
      */
     public void createObjectArray(int elementCount);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object.
      */
     public void createNewArray(boolean lightweight);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object. This version accepts an array of objects
      * to feed to an ArrayCallback to construct the elements of the array.
      */
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight);
 
     /**
      * Create an empty Ruby array
      */
     public void createEmptyArray();
     
     /**
      * Create an empty Ruby Hash object and put a reference on top of the stack.
      */
     public void createEmptyHash();
     
     /**
      * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
      * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
      * collection in and dealing with the sequential indices passed to the callback.
      * 
      * @param elements An object holding the elements from which to create the Hash.
      * @param callback An ArrayCallback implementation to which the elements array and iteration counts
      * are passed in sequence.
      * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
      */
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount);
     
     /**
     * @see createNewHash
     *
     * Create new hash running in ruby 1.9 compat version.
     */
     public void createNewHash19(Object elements, ArrayCallback callback, int keyCount);
     
     /**
      * Create a new range. It is expected that the stack will contain the end and begin values for the range as
      * its topmost and second topmost elements.
      * 
      * @param isExclusive Whether the range is exclusive or not (inclusive)
      */
     public void createNewRange(CompilerCallback beginEndCalback, boolean isExclusive);
 
     /**
      * Create a new literal lambda. The stack should contain a reference to the closure object.
      */
     public void createNewLambda(CompilerCallback closure);
     
     /**
      * Perform a boolean branch operation based on the Ruby "true" value of the top value
      * on the stack. If Ruby "true", invoke the true branch callback. Otherwise, invoke the false branch callback.
      * 
      * @param trueBranch The callback for generating code for the "true" condition
      * @param falseBranch The callback for generating code for the "false" condition
      */
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a logical short-circuited Ruby "and" operation, using Ruby notions of true and false.
      * If the value on top of the stack is false, it remains and the branch is not executed. If it is true,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "and" operation does not short-circuit.
      */
     public void performLogicalAnd(BranchCallback longBranch);
     
     
     /**
      * Perform a logical short-circuited Ruby "or" operation, using Ruby notions of true and false.
      * If the value on top of the stack is true, it remains and the branch is not executed. If it is false,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "or" operation does not short-circuit.
      */
     public void performLogicalOr(BranchCallback longBranch);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version ensures the stack is maintained so while results can be used in any context.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * This version does not handle non-local flow control which can bubble out of
      * eval or closures, and only expects normal flow control to be used within
      * its body.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Return the current value on the top of the stack, taking into consideration surrounding blocks.
      */
     public void performReturn();
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      *
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewClosure(int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
 
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      *
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewClosure19(int line, StaticScope scope, int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Create a new closure (block) for a for loop with the given call arity and
      * body generated by the body callback.
      * 
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Define a new method with the given name, arity, local variable count, and body callback.
      * This will create a new compiled method and bind it to the given name at this point in
      * the program's execution.
      * 
      * @param name The name to which to bind the resulting method.
      * @param arity The arity of the method's argument list
      * @param localVarCount The number of local variables within the method
      * @param body The callback which will generate the method's body.
      */
     public void defineNewMethod(String name, int methodArity, StaticScope scope,
             CompilerCallback body, CompilerCallback args,
-            CompilerCallback receiver, ASTInspector inspector, boolean root);
+            CompilerCallback receiver, ASTInspector inspector, boolean root, String filename, int line);
     
     /**
      * Define an alias for a new name to an existing oldName'd method.
      * 
      * @param newName The new alias to create
      * @param oldName The name of the existing method or alias
      */
     public void defineAlias(String newName, String oldName);
     
     public void assignConstantInCurrent(String name);
     
     public void assignConstantInModule(String name);
     
     public void assignConstantInObject(String name);
     
     /**
      * Retrieve the constant with the specified name available at the current point in the
      * program's execution.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstant(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstantFromModule(String name);
 
     /**
      * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
      *
      * @param name The name of the constant
      */
     public void retrieveConstantFromObject(String name);
     
     /**
      * Load a Ruby "false" value on top of the stack.
      */
     public void loadFalse();
     
     /**
      * Load a Ruby "true" value on top of the stack.
      */
     public void loadTrue();
     
     /**
      * Load a Ruby "nil" value on top of the stack.
      */
     public void loadNil();
     
     public void loadNull();
     
     /**
      * Load the Object class
      */
     public void loadObject();
     
     /**
      * Retrieve the instance variable with the given name, based on the current "self".
      * 
      * @param name The name of the instance variable to retrieve.
      */
     public void retrieveInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      */
     public void assignInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      * @param value A callback for compiling the value to assign
      */
     public void assignInstanceVariable(String name, CompilerCallback value);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      */
     public void assignGlobalVariable(String name);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      * @param value The callback to compile the value to assign
      */
     public void assignGlobalVariable(String name, CompilerCallback value);
     
     /**
      * Retrieve the global variable with the specified name to the top of the stack.
      * 
      * @param name The name of the global variable.
      */
     public void retrieveGlobalVariable(String name);
     
     /**
      * Perform a logical Ruby "not" operation on the value on top of the stack, leaving the
      * negated result.
      */
     public void negateCurrentValue();
     
     /**
      * Convert the current value into a "splatted value" suitable for passing as
      * method arguments or disassembling into multiple variables.
      */
     public void splatCurrentValue();
     
     /**
      * Given a splatted value, extract a single value. If no splat or length is
      * zero, use nil
      */
     public void singlifySplattedValue();
     
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback, CompilerCallback argsCallback);
 
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      * Each call to callback will have a value from the input array on the stack; once the items are exhausted,
      * the code in nilCallback will be invoked *with no value on the stack*.
      */
     public void forEachInValueArray(int count, int preSize, Object preSource, int postSize, Object postSource, ArrayCallback callback, CompilerCallback argsCallback);
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
      */
     public void ensureRubyArray();
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one or coercing it if it is not.
      */
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead);
     
     public void issueBreakEvent(CompilerCallback value);
     
     public void issueNextEvent(CompilerCallback value);
     
     public void issueRedoEvent();
     
     public void issueRetryEvent();
 
     public void asString();
 
     public void nthRef(int match);
 
     public void match();
 
     public void match2(CompilerCallback value);
 
     public void match3();
 
     public void createNewRegexp(ByteList value, int options);
     public void createNewRegexp(CompilerCallback createStringCallback, int options);
     
     public void pollThreadEvents();
 
     /**
      * Push the current back reference
      */
     public void backref();
     /**
      * Call a static helper method on RubyRegexp with the current backref 
      */
     public void backrefMethod(String methodName);
     
     public void nullToNil();
 
     /**
      * Makes sure that the code in protectedCode will always run after regularCode.
      */
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret);
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback protectedCode, Class ret);
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry);
     public void performRescueLight(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry);
     public void performEnsure(BranchCallback regularCode, BranchCallback ensuredCode);
     public void inDefined();
     public void outDefined();
     public void stringOrNil();
     public void pushNull();
     public void pushString(String strVal);
     public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isConstantDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isInstanceVariableDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch);
     public Object getNewEnding();
     public void ifNull(Object gotoToken);
     public void isNil(BranchCallback trueBranch, BranchCallback falseBranch);
     public void isNull(BranchCallback trueBranch, BranchCallback falseBranch);
     public void ifNotNull(Object gotoToken);
     public void setEnding(Object endingToken);
     public void go(Object gotoToken);
     public void isConstantBranch(BranchCallback setup, BranchCallback isConstant, BranchCallback isMethod, BranchCallback none, String name);
     public void metaclass();
     public void getVisibilityFor(String name);
     public void isPrivate(Object gotoToken, int toConsume);
     public void isNotProtected(Object gotoToken, int toConsume);
     public void selfIsKindOf(Object gotoToken);
     public void loadCurrentModule();
     public void notIsModuleAndClassVarDefined(String name, Object gotoToken);
     public void loadSelf();
     public void ifSingleton(Object gotoToken);
     public void getInstanceVariable(String name);
     public void getFrameName();
     public void getFrameKlazz(); 
     public void superClass();
     public void attached();    
     public void ifNotSuperMethodBound(Object token);
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch);
     public void isCaptured(int number, BranchCallback trueBranch, BranchCallback falseBranch);
     public void concatArrays();
     public void appendToArray();
     public void convertToJavaArray();
     public void aryToAry();
     public void toJavaString();
     public void aliasGlobal(String newName, String oldName);
     public void undefMethod(String name);
     public void defineClass(String name, StaticScope staticScope, CompilerCallback superCallback, CompilerCallback pathCallback, CompilerCallback bodyCallback, CompilerCallback receiverCallback, ASTInspector inspector);
     public void defineModule(String name, StaticScope staticScope, CompilerCallback pathCallback, CompilerCallback bodyCallback, ASTInspector inspector);
     public void unwrapPassedBlock();
     public void performBackref(char type);
     public void callZSuper(CompilerCallback closure);
     public void appendToObjectArray();
     public void checkIsExceptionHandled(ArgumentsCallback rescueArgs);
     public void rethrowException();
     public void loadClass(String name);
     public void loadStandardError();
     public void unwrapRaiseException();
     public void loadException();
     public void setFilePosition(ISourcePosition position);
     public void setLinePosition(ISourcePosition position);
     public void checkWhenWithSplat();
     public void createNewEndBlock(CompilerCallback body);
     public void runBeginBlock(StaticScope scope, CompilerCallback body);
     public void rethrowIfSystemExit();
 
     public BodyCompiler chainToMethod(String name);
     public BodyCompiler outline(String methodName);
     public void wrapJavaException();
     public void literalSwitch(int[] caseInts, Object[] caseBodies, ArrayCallback casesCallback, CompilerCallback defaultCallback);
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback);
     public void loadFilename();
     public void storeExceptionInErrorInfo();
     public void clearErrorInfo();
 
     public void compileSequencedConditional(
             CompilerCallback inputValue,
             FastSwitchType fastSwitchType,
             Map<CompilerCallback, int[]> switchCases,
             List<ArgumentsCallback> conditionals,
             List<CompilerCallback> bodies,
             CompilerCallback fallback);
 
     public void raiseTypeError(String string);
+
+    public void traceLine();
+    public void traceClass();
+    public void traceEnd();
 }
diff --git a/src/org/jruby/compiler/impl/BaseBodyCompiler.java b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
index deacbe4419..cf583203b5 100644
--- a/src/org/jruby/compiler/impl/BaseBodyCompiler.java
+++ b/src/org/jruby/compiler/impl/BaseBodyCompiler.java
@@ -1,1063 +1,1064 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.compiler.impl;
 
 import java.io.PrintStream;
 import java.lang.reflect.InvocationTargetException;
 import java.math.BigInteger;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArgumentsCallback;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.BodyCompiler;
 import org.jruby.compiler.FastSwitchType;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
+import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.util.ByteList;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.Label;
 import static org.objectweb.asm.Opcodes.*;
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * BaseBodyCompiler encapsulates all common behavior between BodyCompiler
  * implementations.
  */
 public abstract class BaseBodyCompiler implements BodyCompiler {
     protected SkinnyMethodAdapter method;
     protected VariableCompiler variableCompiler;
     protected InvocationCompiler invocationCompiler;
     protected int argParamCount;
     protected Label[] currentLoopLabels;
     protected Label scopeStart = new Label();
     protected Label scopeEnd = new Label();
     protected Label redoJump;
     protected boolean inNestedMethod = false;
     private int lastLine = -1;
     private int lastPositionLine = -1;
     protected StaticScope scope;
     protected ASTInspector inspector;
     protected String methodName;
     protected StandardASMCompiler script;
 
     public BaseBodyCompiler(StandardASMCompiler scriptCompiler, String methodName, ASTInspector inspector, StaticScope scope) {
         this.script = scriptCompiler;
         this.scope = scope;
         this.inspector = inspector;
         this.methodName = methodName;
         if (shouldUseBoxedArgs(scope)) {
             argParamCount = 1; // use IRubyObject[]
         } else {
             argParamCount = scope.getRequiredArgs(); // specific arity
         }
 
         method = new SkinnyMethodAdapter(script.getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, methodName, getSignature(), null, null));
 
         createVariableCompiler();
         if (StandardASMCompiler.invDynInvCompilerConstructor != null) {
             try {
                 invocationCompiler = (InvocationCompiler) StandardASMCompiler.invDynInvCompilerConstructor.newInstance(this, method);
             } catch (InstantiationException ie) {
                 // do nothing, fall back on default compiler below
                 } catch (IllegalAccessException ie) {
                 // do nothing, fall back on default compiler below
                 } catch (InvocationTargetException ie) {
                 // do nothing, fall back on default compiler below
                 }
         }
         if (invocationCompiler == null) {
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
     }
 
     protected boolean shouldUseBoxedArgs(StaticScope scope) {
         return scope.getRestArg() >= 0 || scope.getRestArg() == -2 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3;
     }
 
     protected abstract String getSignature();
 
     protected abstract void createVariableCompiler();
 
     public abstract void beginMethod(CompilerCallback args, StaticScope scope);
 
     public abstract void endBody();
 
     public BodyCompiler chainToMethod(String methodName) {
         BodyCompiler compiler = outline(methodName);
         endBody();
         return compiler;
     }
 
     public void beginChainedMethod() {
         method.start();
 
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
         method.astore(getRuntimeIndex());
 
         // grab nil for local variables
         method.aload(getRuntimeIndex());
         method.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
         method.astore(getNilIndex());
 
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
         method.astore(getDynamicScopeIndex());
 
         // if more than 4 locals, get the locals array too
         if (scope.getNumberOfVariables() > 4) {
             method.aload(getDynamicScopeIndex());
             method.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             method.astore(getVarsArrayIndex());
         }
 
         // visit a label to start scoping for local vars in this method
         method.label(scopeStart);
     }
 
     public abstract BaseBodyCompiler outline(String methodName);
 
     public StandardASMCompiler getScriptCompiler() {
         return script;
     }
 
     public void lineNumber(ISourcePosition position) {
         int thisLine = position.getStartLine();
 
         // No point in updating number if last number was same value.
         if (thisLine != lastLine) {
             lastLine = thisLine;
         } else {
             return;
         }
 
         Label line = new Label();
         method.label(line);
         method.visitLineNumber(thisLine + 1, line);
     }
 
     public void loadThreadContext() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
     }
 
     public void loadSelf() {
         method.aload(StandardASMCompiler.SELF_INDEX);
     }
 
     protected int getClosureIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.CLOSURE_OFFSET;
     }
 
     protected int getRuntimeIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.RUNTIME_OFFSET;
     }
 
     protected int getNilIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.NIL_OFFSET;
     }
 
     protected int getPreviousExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.PREVIOUS_EXCEPTION_OFFSET;
     }
 
     protected int getDynamicScopeIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.DYNAMIC_SCOPE_OFFSET;
     }
 
     protected int getVarsArrayIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.VARS_ARRAY_OFFSET;
     }
 
     protected int getFirstTempIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.FIRST_TEMP_OFFSET;
     }
 
     protected int getExceptionIndex() {
         return StandardASMCompiler.ARGS_INDEX + argParamCount + StandardASMCompiler.EXCEPTION_OFFSET;
     }
 
     public void loadThis() {
         method.aload(StandardASMCompiler.THIS);
     }
 
     public void loadRuntime() {
         method.aload(getRuntimeIndex());
     }
 
     public void loadBlock() {
         method.aload(getClosureIndex());
     }
 
     public void loadNil() {
         method.aload(getNilIndex());
     }
 
     public void loadNull() {
         method.aconst_null();
     }
 
     public void loadObject() {
         loadRuntime();
 
         invokeRuby("getObject", sig(RubyClass.class, params()));
     }
 
     /**
      * This is for utility methods used by the compiler, to reduce the amount of code generation
      * necessary.  All of these live in CompilerHelpers.
      */
     public void invokeUtilityMethod(String methodName, String signature) {
         method.invokestatic(p(RuntimeHelpers.class), methodName, signature);
     }
 
     public void invokeThreadContext(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.THREADCONTEXT, methodName, signature);
     }
 
     public void invokeRuby(String methodName, String signature) {
         method.invokevirtual(StandardASMCompiler.RUBY, methodName, signature);
     }
 
     public void invokeIRubyObject(String methodName, String signature) {
         method.invokeinterface(StandardASMCompiler.IRUBYOBJECT, methodName, signature);
     }
 
     public void consumeCurrentValue() {
         method.pop();
     }
 
     public void duplicateCurrentValue() {
         method.dup();
     }
 
     public void swapValues() {
         method.swap();
     }
 
     public void reverseValues(int count) {
         switch (count) {
         case 2:
             method.swap();
             break;
         case 3:
             method.dup_x2();
             method.pop();
             method.swap();
             break;
         case 4:
             method.swap();
             method.dup2_x2();
             method.pop2();
             method.swap();
             break;
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
             // up to ten, stuff into tmp locals, load in reverse order, and assign
             // FIXME: There's probably a slightly smarter way, but is it important?
             int[] tmpLocals = new int[count];
             for (int i = 0; i < count; i++) {
                 tmpLocals[i] = getVariableCompiler().grabTempLocal();
                 getVariableCompiler().setTempLocal(tmpLocals[i]);
             }
             for (int i = 0; i < count; i++) {
                 getVariableCompiler().getTempLocal(tmpLocals[i]);
                 getVariableCompiler().releaseTempLocal();
             }
             break;
         default:
             throw new NotCompilableException("can't reverse more than ten values on the stack");
         }
     }
 
     public void retrieveSelf() {
         loadSelf();
     }
 
     public void retrieveSelfClass() {
         loadSelf();
         metaclass();
     }
 
     public VariableCompiler getVariableCompiler() {
         return variableCompiler;
     }
 
     public InvocationCompiler getInvocationCompiler() {
         return invocationCompiler;
     }
 
     public void assignConstantInCurrent(String name) {
         loadThreadContext();
         method.ldc(name);
         invokeUtilityMethod("setConstantInCurrent", sig(IRubyObject.class, params(IRubyObject.class, ThreadContext.class, String.class)));
     }
 
     public void assignConstantInModule(String name) {
         method.ldc(name);
         loadThreadContext();
         invokeUtilityMethod("setConstantInModule", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
     }
 
     public void assignConstantInObject(String name) {
         // load Object under value
         loadObject();
         method.swap();
 
         assignConstantInModule(name);
     }
 
     public void retrieveConstant(String name) {
         script.getCacheCompiler().cacheConstant(this, name);
     }
 
     public void retrieveConstantFromModule(String name) {
         invokeUtilityMethod("checkIsModule", sig(RubyModule.class, IRubyObject.class));
         script.getCacheCompiler().cacheConstantFrom(this, name);
     }
 
     public void retrieveConstantFromObject(String name) {
         loadObject();
         script.getCacheCompiler().cacheConstantFrom(this, name);
     }
 
     public void retrieveClassVariable(String name) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
 
         invokeUtilityMethod("fastFetchClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
     }
 
     public void assignClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void assignClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name) {
         loadThreadContext();
         method.swap();
         loadRuntime();
         method.swap();
         loadSelf();
         method.swap();
         method.ldc(name);
         method.swap();
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void declareClassVariable(String name, CompilerCallback value) {
         loadThreadContext();
         loadRuntime();
         loadSelf();
         method.ldc(name);
         value.call(this);
 
         invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
     }
 
     public void createNewFloat(double value) {
         loadRuntime();
         method.ldc(new Double(value));
 
         invokeRuby("newFloat", sig(RubyFloat.class, params(Double.TYPE)));
     }
 
     public void createNewFixnum(long value) {
         script.getCacheCompiler().cacheFixnum(this, value);
     }
 
     public void createNewBignum(BigInteger value) {
         loadRuntime();
         script.getCacheCompiler().cacheBigInteger(this, value);
         method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
     }
 
     public void createNewString(ArrayCallback callback, int count) {
         loadRuntime();
         method.ldc(StandardASMCompiler.STARTING_DSTR_SIZE);
         method.invokestatic(p(RubyString.class), "newStringLight", sig(RubyString.class, Ruby.class, int.class));
         for (int i = 0; i < count; i++) {
             callback.nextValue(this, null, i);
             method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
         }
     }
 
     public void createNewSymbol(ArrayCallback callback, int count) {
         loadRuntime();
         createNewString(callback, count);
         toJavaString();
         invokeRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
     }
 
     public void createNewString(ByteList value) {
         script.getCacheCompiler().cacheString(this, value);
     }
 
     public void createNewSymbol(String name) {
         script.getCacheCompiler().cacheSymbol(this, name);
     }
 
     public void createNewArray(boolean lightweight) {
         loadRuntime();
         // put under object array already present
         method.swap();
 
         if (lightweight) {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         } else {
             method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, params(Ruby.class, IRubyObject[].class)));
         }
     }
 
     public void createNewArray(Object[] sourceArray, ArrayCallback callback, boolean lightweight) {
         loadRuntime();
 
         buildRubyArray(sourceArray, callback, lightweight);
     }
 
     public void createEmptyArray() {
         loadRuntime();
 
         invokeRuby("newArray", sig(RubyArray.class));
     }
 
     public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
         buildObjectArray(StandardASMCompiler.IRUBYOBJECT, sourceArray, callback);
     }
 
     public void createObjectArray(int elementCount) {
         // if element count is less than 6, use helper methods
         if (elementCount < 6) {
             Class[] params = new Class[elementCount];
             Arrays.fill(params, IRubyObject.class);
             invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params));
         } else {
             // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
             throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
         }
     }
 
     private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
         if (sourceArray.length == 0) {
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
             // if we have a specific-arity helper to construct an array for us, use that
             for (int i = 0; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
             }
             invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params(IRubyObject.class, sourceArray.length)));
         } else {
             // brute force construction inline
             method.pushInt(sourceArray.length);
             method.anewarray(type);
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.pushInt(i);
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
         }
     }
 
     private void buildRubyArray(Object[] sourceArray, ArrayCallback callback, boolean light) {
         if (sourceArray.length == 0) {
             method.invokestatic(p(RubyArray.class), "newEmptyArray", sig(RubyArray.class, Ruby.class));
         } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
             // if we have a specific-arity helper to construct an array for us, use that
             for (int i = 0; i < sourceArray.length; i++) {
                 callback.nextValue(this, sourceArray, i);
             }
             invokeUtilityMethod("constructRubyArray", sig(RubyArray.class, params(Ruby.class, IRubyObject.class, sourceArray.length)));
         } else {
             // brute force construction inline
             method.pushInt(sourceArray.length);
             method.anewarray(p(IRubyObject.class));
 
             for (int i = 0; i < sourceArray.length; i++) {
                 method.dup();
                 method.pushInt(i);
 
                 callback.nextValue(this, sourceArray, i);
 
                 method.arraystore();
             }
             
             if (light) {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopyLight", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             } else {
                 method.invokestatic(p(RubyArray.class), "newArrayNoCopy", sig(RubyArray.class, Ruby.class, IRubyObject[].class));
             }
         }
     }
 
     public void createEmptyHash() {
         loadRuntime();
 
         method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
     }
 
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
         createNewHashCommon(elements, callback, keyCount, "constructHash", "fastASetCheckString");
     }
     
     public void createNewHash19(Object elements, ArrayCallback callback, int keyCount) {
         createNewHashCommon(elements, callback, keyCount, "constructHash19", "fastASetCheckString19");
     }
     
     private void createNewHashCommon(Object elements, ArrayCallback callback, int keyCount,
             String constructorName, String methodName) {
         loadRuntime();
 
         // use specific-arity for as much as possible
         int i = 0;
         for (; i < keyCount && i < RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH; i++) {
             callback.nextValue(this, elements, i);
         }
 
         invokeUtilityMethod(constructorName, sig(RubyHash.class, params(Ruby.class, IRubyObject.class, i * 2)));
 
         for (; i < keyCount; i++) {
             method.dup();
             loadRuntime();
             callback.nextValue(this, elements, i);
             method.invokevirtual(p(RubyHash.class), methodName, sig(void.class, params(Ruby.class, IRubyObject.class, IRubyObject.class)));
         }
     }
 
     public void createNewRange(CompilerCallback beginEndCallback, boolean isExclusive) {
         loadRuntime();
         loadThreadContext();
         beginEndCallback.call(this);
 
         if (isExclusive) {
             method.invokestatic(p(RubyRange.class), "newExclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         } else {
             method.invokestatic(p(RubyRange.class), "newInclusiveRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class)));
         }
     }
 
     public void createNewLambda(CompilerCallback closure) {
         loadThreadContext();
         closure.call(this);
         loadSelf();
 
         invokeUtilityMethod("newLiteralLambda", sig(RubyProc.class, ThreadContext.class, Block.class, IRubyObject.class));
     }
 
     /**
      * Invoke IRubyObject.isTrue
      */
     private void isTrue() {
         invokeIRubyObject("isTrue", sig(Boolean.TYPE));
     }
 
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         // call isTrue on the result
         isTrue();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(afterJmp);
 
         // FIXME: optimize for cases where we have no false branch
         method.label(falseJmp);
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void performLogicalAnd(BranchCallback longBranch) {
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performLogicalOr(BranchCallback longBranch) {
         // FIXME: after jump is not in here.  Will if ever be?
         //Label afterJmp = new Label();
         Label falseJmp = new Label();
 
         // dup it since we need to return appropriately if it's false
         method.dup();
 
         // call isTrue on the result
         isTrue();
 
         method.ifne(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         method.pop();
         longBranch.branch(this);
         method.label(falseJmp);
     }
 
     public void performBooleanLoopSafe(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         String mname = getNewRescueName();
         BaseBodyCompiler nested = outline(mname);
         nested.performBooleanLoopSafeInner(condition, body, checkFirst);
     }
 
     private void performBooleanLoopSafeInner(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         performBooleanLoop(condition, body, checkFirst);
 
         endBody();
     }
 
     public void performBooleanLoop(BranchCallback condition, final BranchCallback body, boolean checkFirst) {
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label catchNext = new Label();
         Label catchBreak = new Label();
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         final Label topOfBody = new Label();
         Label done = new Label();
         Label normalLoopEnd = new Label();
         method.trycatch(tryBegin, tryEnd, catchNext, p(JumpException.NextJump.class));
         method.trycatch(tryBegin, tryEnd, catchBreak, p(JumpException.BreakJump.class));
 
         method.label(tryBegin);
         {
 
             Label[] oldLoopLabels = currentLoopLabels;
 
             currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
             // FIXME: if we terminate immediately, this appears to break while in method arguments
             // we need to push a nil for the cases where we will never enter the body
             if (checkFirst) {
                 method.go_to(conditionCheck);
             }
 
             method.label(topOfBody);
 
             Runnable redoBody = new Runnable() { public void run() {
                 Runnable raiseBody = new Runnable() { public void run() {
                     body.branch(BaseBodyCompiler.this);
                 }};
                 Runnable raiseCatch = new Runnable() { public void run() {
                     loadThreadContext();
                     invokeUtilityMethod("unwrapRedoNextBreakOrJustLocalJump", sig(Throwable.class, RaiseException.class, ThreadContext.class));
                     method.athrow();
                 }};
                 method.trycatch(p(RaiseException.class), raiseBody, raiseCatch);
             }};
             Runnable redoCatch = new Runnable() { public void run() {
                 method.pop();
                 method.go_to(topOfBody);
             }};
             method.trycatch(p(JumpException.RedoJump.class), redoBody, redoCatch);
 
             method.label(endOfBody);
 
             // clear body or next result after each successful loop
             method.pop();
 
             method.label(conditionCheck);
 
             // check the condition
             condition.branch(this);
             isTrue();
             method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
             currentLoopLabels = oldLoopLabels;
         }
 
         method.label(tryEnd);
         // skip catch block
         method.go_to(normalLoopEnd);
 
         // catch logic for flow-control: next, break
         {
             // next jump
             {
                 method.label(catchNext);
                 method.pop();
                 // exceptionNext target is for a next that doesn't push a new value, like this one
                 method.go_to(conditionCheck);
             }
 
             // break jump
             {
                 method.label(catchBreak);
                 loadThreadContext();
                 invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, ThreadContext.class));
                 method.go_to(done);
             }
         }
 
         method.label(normalLoopEnd);
         loadNil();
         method.label(done);
     }
 
     public void performBooleanLoopLight(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         Label endOfBody = new Label();
         Label conditionCheck = new Label();
         Label topOfBody = new Label();
         Label done = new Label();
 
         Label[] oldLoopLabels = currentLoopLabels;
 
         currentLoopLabels = new Label[]{endOfBody, topOfBody, done};
 
         // FIXME: if we terminate immediately, this appears to break while in method arguments
         // we need to push a nil for the cases where we will never enter the body
         if (checkFirst) {
             method.go_to(conditionCheck);
         }
 
         method.label(topOfBody);
 
         body.branch(this);
 
         method.label(endOfBody);
 
         // clear body or next result after each successful loop
         method.pop();
 
         method.label(conditionCheck);
 
         // check the condition
         condition.branch(this);
         isTrue();
         method.ifne(topOfBody); // NE == nonzero (i.e. true)
 
         currentLoopLabels = oldLoopLabels;
 
         loadNil();
         method.label(done);
     }
 
     public void createNewClosure(
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + "__block__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure(this, closureMethodName, arity, scope, hasMultipleArgsHead, argsNodeId, inspector);
 
         invokeUtilityMethod("createBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void createNewClosure19(
             int line,
             StaticScope scope,
             int arity,
             CompilerCallback body,
             CompilerCallback args,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$" + "__block__";
 
         ChildScopedBodyCompiler19 closureCompiler = new ChildScopedBodyCompiler19(script, closureMethodName, inspector, scope);
 
         closureCompiler.beginMethod(args, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
 
         loadThreadContext();
         loadSelf();
         script.getCacheCompiler().cacheClosure19(this, closureMethodName, arity, scope, hasMultipleArgsHead, argsNodeId, inspector);
 
         invokeUtilityMethod("createBlock19", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, BlockBody.class)));
     }
 
     public void runBeginBlock(StaticScope scope, CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__begin__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, scope);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
 
         StandardASMCompiler.buildStaticScopeNames(method, scope);
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                 params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
     }
 
     public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__for__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, inspector, scope);
 
         closureCompiler.beginMethod(args, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.pushInt(arity);
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         method.ldc(Boolean.valueOf(hasMultipleArgsHead));
         method.ldc(BlockBody.asArgumentType(argsNodeId));
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
     }
 
     public void createNewEndBlock(CompilerCallback body) {
         String closureMethodName = "block_" + script.getAndIncrementInnerIndex() + "$RUBY$__end__";
 
         ChildScopedBodyCompiler closureCompiler = new ChildScopedBodyCompiler(script, closureMethodName, null, scope);
 
         closureCompiler.beginMethod(null, null);
 
         body.call(closureCompiler);
 
         closureCompiler.endBody();
 
         // Done with closure compilation
         loadThreadContext();
         loadSelf();
         method.iconst_0();
 
         script.getCacheCompiler().cacheSpecialClosure(this, closureMethodName);
 
         method.iconst_0(); // false
         method.iconst_0(); // zero
 
         invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                 params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
 
         loadRuntime();
         invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
         loadNil();
     }
 
     public void getCompiledClass() {
         method.aload(StandardASMCompiler.THIS);
         method.getfield(script.getClassname(), "$class", ci(Class.class));
     }
 
     public void println() {
         method.dup();
         method.getstatic(p(System.class), "out", ci(PrintStream.class));
         method.swap();
 
         method.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, params(Object.class)));
     }
 
     public void defineAlias(String newName, String oldName) {
         loadThreadContext();
         method.ldc(newName);
         method.ldc(oldName);
         invokeUtilityMethod("defineAlias", sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
     }
 
     public void loadFalse() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getFalse", sig(RubyBoolean.class));
     }
 
     public void loadTrue() {
         // TODO: cache?
         loadRuntime();
         invokeRuby("getTrue", sig(RubyBoolean.class));
     }
 
     public void loadCurrentModule() {
         loadThreadContext();
         invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
         method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
         method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
     }
 
     public void retrieveInstanceVariable(String name) {
         script.getCacheCompiler().cachedGetVariable(this, name);
     }
 
     public void assignInstanceVariable(String name) {
         final int tmp = getVariableCompiler().grabTempLocal();
         getVariableCompiler().setTempLocal(tmp);
         CompilerCallback callback = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 context.getVariableCompiler().getTempLocal(tmp);
             }
         };
         script.getCacheCompiler().cachedSetVariable(this, name, callback);
     }
 
     public void assignInstanceVariable(String name, CompilerCallback value) {
         script.getCacheCompiler().cachedSetVariable(this, name, value);
     }
 
     public void retrieveGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("getGlobalVariable", sig(IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name) {
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void assignGlobalVariable(String name, CompilerCallback value) {
         value.call(this);
         loadRuntime();
         method.ldc(name);
         invokeUtilityMethod("setGlobalVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void negateCurrentValue() {
         loadRuntime();
         invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
     }
 
     public void splatCurrentValue() {
         method.invokestatic(p(RuntimeHelpers.class), "splatValue", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void singlifySplattedValue() {
         method.invokestatic(p(RuntimeHelpers.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void aryToAry() {
         method.invokestatic(p(RuntimeHelpers.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void ensureRubyArray() {
         invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
         loadRuntime();
         method.pushBoolean(masgnHasHead);
         invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(IRubyObject.class, Ruby.class, boolean.class)));
     }
 
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < count || argsCallback != null) {
@@ -1078,1499 +1079,1524 @@ public abstract class BaseBodyCompiler implements BodyCompiler {
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, source, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
                 argsCallback.call(this);
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void forEachInValueArray(int start, int preCount, Object preSource, int postCount, Object postSource, ArrayCallback callback, CompilerCallback argsCallback) {
         if (start < preCount || argsCallback != null) {
             int tempLocal = getVariableCompiler().grabTempLocal();
             getVariableCompiler().setTempLocal(tempLocal);
 
             for (; start < preCount; start++) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 switch (start) {
                 case 0:
                     invokeUtilityMethod("arrayEntryOrNilZero", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 1:
                     invokeUtilityMethod("arrayEntryOrNilOne", sig(IRubyObject.class, RubyArray.class));
                     break;
                 case 2:
                     invokeUtilityMethod("arrayEntryOrNilTwo", sig(IRubyObject.class, RubyArray.class));
                     break;
                 default:
                     method.pushInt(start);
                     invokeUtilityMethod("arrayEntryOrNil", sig(IRubyObject.class, RubyArray.class, int.class));
                     break;
                 }
                 callback.nextValue(this, preSource, start);
             }
 
             if (argsCallback != null) {
                 getVariableCompiler().getTempLocal(tempLocal);
                 loadRuntime();
                 method.pushInt(start);
                 invokeUtilityMethod("subarrayOrEmpty", sig(RubyArray.class, RubyArray.class, Ruby.class, int.class));
                 argsCallback.call(this);
             }
 
             if (postCount > 0) {
                 throw new NotCompilableException("1.9 mode can't handle post variables in masgn yet");
             }
 
             getVariableCompiler().getTempLocal(tempLocal);
             getVariableCompiler().releaseTempLocal();
         }
     }
 
     public void asString() {
         method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     public void toJavaString() {
         method.invokevirtual(p(Object.class), "toString", sig(String.class));
     }
 
     public void nthRef(int match) {
         method.pushInt(match);
         backref();
         method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
     }
 
     public void match() {
         loadThreadContext();
         method.invokevirtual(p(RubyRegexp.class), "op_match2", sig(IRubyObject.class, params(ThreadContext.class)));
     }
 
     public void match2(CompilerCallback value) {
         loadThreadContext();
         value.call(this);
         method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
     }
 
     public void match3() {
         loadThreadContext();
         invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void createNewRegexp(final ByteList value, final int options) {
         script.getCacheCompiler().cacheRegexp(this, value.toString(), options);
     }
 
     public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
         boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
 
         if (onceOnly) {
             script.getCacheCompiler().cacheDRegexp(this, createStringCallback, options);
         } else {
             loadRuntime();
             createStringCallback.call(this);
             method.pushInt(options);
             method.invokestatic(p(RubyRegexp.class), "newDRegexp", sig(RubyRegexp.class, params(Ruby.class, RubyString.class, int.class))); //[reg]
         }
     }
 
     public void pollThreadEvents() {
         if (!RubyInstanceConfig.THREADLESS_COMPILE_ENABLED) {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", sig(Void.TYPE));
         }
     }
 
     public void nullToNil() {
         Label notNull = new Label();
         method.dup();
         method.ifnonnull(notNull);
         method.pop();
         loadNil();
         method.label(notNull);
     }
 
     public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.instance_of(p(clazz));
 
         Label falseJmp = new Label();
         Label afterJmp = new Label();
 
         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
 
         method.go_to(afterJmp);
         method.label(falseJmp);
 
         falseBranch.branch(this);
 
         method.label(afterJmp);
     }
 
     public void isCaptured(final int number, final BranchCallback trueBranch, final BranchCallback falseBranch) {
         backref();
         method.dup();
         isInstanceOf(RubyMatchData.class, new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                 method.pushInt(number);
                 method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(int.class)));
                 method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
                 Label isNil = new Label();
                 Label after = new Label();
 
                 method.ifne(isNil);
                 trueBranch.branch(context);
                 method.go_to(after);
 
                 method.label(isNil);
                 falseBranch.branch(context);
                 method.label(after);
             }
         }, new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 method.pop();
                 falseBranch.branch(context);
             }
         });
     }
 
     public void backref() {
         loadRuntime();
         loadThreadContext();
         invokeUtilityMethod("getBackref", sig(IRubyObject.class, Ruby.class, ThreadContext.class));
     }
 
     public void backrefMethod(String methodName) {
         backref();
         method.invokestatic(p(RubyRegexp.class), methodName, sig(IRubyObject.class, params(IRubyObject.class)));
     }
 
     public void issueLoopBreak() {
         // inside a loop, break out of it
         // go to end of loop, leaving break value on stack
         method.go_to(currentLoopLabels[2]);
     }
 
     public void issueLoopNext() {
         // inside a loop, jump to conditional
         method.go_to(currentLoopLabels[0]);
     }
 
     public void issueLoopRedo() {
         // inside a loop, jump to body
         method.go_to(currentLoopLabels[1]);
     }
 
     protected String getNewEnsureName() {
         return "ensure_" + (script.getAndIncrementEnsureNumber()) + "$RUBY$__ensure__";
     }
 
     public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
         String mname = getNewEnsureName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                 script.getClassVisitor().visitMethod(
                 ACC_PUBLIC | ACC_SYNTHETIC | ACC_STATIC,
                 mname,
                 sig(ret, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject.class, Block.class),
                 null,
                 null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         boolean oldInNestedMethod = inNestedMethod;
         inNestedMethod = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.visitCode();
             // set up a local IRuby variable
 
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.dup();
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.dup();
             mv.astore(getRuntimeIndex());
 
             // grab nil for local variables
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.dup();
             mv.astore(getDynamicScopeIndex());
             mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
             mv.astore(getVarsArrayIndex());
             Label codeBegin = new Label();
             Label codeEnd = new Label();
             Label ensureBegin = new Label();
             Label ensureEnd = new Label();
             method.label(codeBegin);
 
             regularCode.branch(this);
 
             method.label(codeEnd);
 
             protectedCode.branch(this);
             mv.areturn();
 
             method.label(ensureBegin);
             method.astore(getExceptionIndex());
             method.label(ensureEnd);
             protectedCode.branch(this);
 
             method.aload(getExceptionIndex());
             method.athrow();
 
             method.trycatch(codeBegin, codeEnd, ensureBegin, null);
             method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
             mv.visitMaxs(1, 1);
             mv.visitEnd();
         } finally {
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             inNestedMethod = oldInNestedMethod;
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokestatic(
                 script.getClassname(),
                 mname,
                 sig(ret, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject.class, Block.class));
     }
 
     public void performEnsure(BranchCallback regularCode, BranchCallback protectedCode) {
         String mname = getNewEnsureName();
         BaseBodyCompiler ensure = outline(mname);
         ensure.performEnsureInner(regularCode, protectedCode);
     }
 
     private void performEnsureInner(BranchCallback regularCode, BranchCallback protectedCode) {
         Label codeBegin = new Label();
         Label codeEnd = new Label();
         Label ensureBegin = new Label();
         Label ensureEnd = new Label();
         method.label(codeBegin);
 
         regularCode.branch(this);
 
         method.label(codeEnd);
 
         protectedCode.branch(this);
         method.areturn();
 
         method.label(ensureBegin);
         method.astore(getExceptionIndex());
         method.label(ensureEnd);
 
         protectedCode.branch(this);
 
         method.aload(getExceptionIndex());
         method.athrow();
 
         method.trycatch(codeBegin, codeEnd, ensureBegin, null);
         method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
         loadNil();
         endBody();
     }
 
     protected String getNewRescueName() {
         return "rescue_" + (script.getAndIncrementRescueNumber()) + "$RUBY$__rescue__";
     }
 
     public void storeExceptionInErrorInfo() {
         loadException();
         loadThreadContext();
         invokeUtilityMethod("storeExceptionInErrorInfo", sig(void.class, Throwable.class, ThreadContext.class));
     }
 
     public void clearErrorInfo() {
         loadThreadContext();
         invokeUtilityMethod("clearErrorInfo", sig(void.class, ThreadContext.class));
     }
 
     public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
         String mname = getNewRescueName();
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                 script.getClassVisitor().visitMethod(
                     ACC_PUBLIC | ACC_SYNTHETIC | ACC_STATIC,
                     mname,
                     sig(ret, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject.class, Block.class),
                     null,
                     null));
         SkinnyMethodAdapter old_method = null;
         SkinnyMethodAdapter var_old_method = null;
         SkinnyMethodAdapter inv_old_method = null;
         Label afterMethodBody = new Label();
         Label catchRetry = new Label();
         Label catchRaised = new Label();
         Label catchJumps = new Label();
         Label exitRescue = new Label();
         boolean oldWithinProtection = inNestedMethod;
         inNestedMethod = true;
         Label[] oldLoopLabels = currentLoopLabels;
         currentLoopLabels = null;
         int oldArgCount = argParamCount;
         argParamCount = 0; // synthetic methods always have zero arg parameters
         try {
             old_method = this.method;
             var_old_method = getVariableCompiler().getMethodAdapter();
             inv_old_method = getInvocationCompiler().getMethodAdapter();
             this.method = mv;
             getVariableCompiler().setMethodAdapter(mv);
             getInvocationCompiler().setMethodAdapter(mv);
 
             mv.start();
 
             // set up a local IRuby variable
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.astore(getRuntimeIndex());
 
             // store previous exception for restoration if we rescue something
             loadThreadContext();
             invokeThreadContext("getErrorInfo", sig(IRubyObject.class));
             mv.astore(getPreviousExceptionIndex());
 
             // grab nil for local variables
             loadRuntime();
             mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             mv.astore(getNilIndex());
 
             mv.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
             mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
             mv.astore(getDynamicScopeIndex());
 
             // if more than 4 vars, get values array too
             if (scope.getNumberOfVariables() > 4) {
                 mv.aload(getDynamicScopeIndex());
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
                 mv.astore(getVarsArrayIndex());
             }
 
             Label beforeBody = new Label();
             Label afterBody = new Label();
             Label catchBlock = new Label();
             mv.trycatch(beforeBody, afterBody, catchBlock, p(exception));
             mv.label(beforeBody);
 
             regularCode.branch(this);
 
             mv.label(afterBody);
             mv.go_to(exitRescue);
             mv.label(catchBlock);
             mv.astore(getExceptionIndex());
 
             catchCode.branch(this);
 
             mv.label(afterMethodBody);
             mv.go_to(exitRescue);
 
             // retry handling in the rescue block
             mv.trycatch(catchBlock, afterMethodBody, catchRetry, p(JumpException.RetryJump.class));
             mv.label(catchRetry);
             mv.pop();
             mv.go_to(beforeBody);
 
             // any exceptions raised must continue to be raised, skipping $! restoration
             mv.trycatch(beforeBody, afterMethodBody, catchRaised, p(RaiseException.class));
             mv.label(catchRaised);
             mv.athrow();
 
             // and remaining jump exceptions should restore $!
             mv.trycatch(beforeBody, afterMethodBody, catchJumps, p(JumpException.class));
             mv.label(catchJumps);
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
             mv.athrow();
 
             mv.label(exitRescue);
 
             // restore the original exception
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
 
             mv.areturn();
             mv.end();
         } finally {
             inNestedMethod = oldWithinProtection;
             this.method = old_method;
             getVariableCompiler().setMethodAdapter(var_old_method);
             getInvocationCompiler().setMethodAdapter(inv_old_method);
             currentLoopLabels = oldLoopLabels;
             argParamCount = oldArgCount;
         }
 
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         if (this instanceof ChildScopedBodyCompiler) {
             pushNull();
         } else {
             loadBlock();
         }
         method.invokestatic(
                 script.getClassname(),
                 mname,
                 sig(ret, "L" + script.getClassname() + ";", ThreadContext.class, IRubyObject.class, Block.class));
     }
 
     public void performRescue(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry) {
         String mname = getNewRescueName();
         BaseBodyCompiler rescueMethod = outline(mname);
         rescueMethod.performRescueLight(regularCode, rubyCatchCode, needsRetry);
         rescueMethod.endBody();
     }
 
     public void performRescueLight(BranchCallback regularCode, BranchCallback rubyCatchCode, boolean needsRetry) {
         Label afterRubyCatchBody = new Label();
         Label catchRetry = new Label();
         Label catchJumps = new Label();
         Label exitRescue = new Label();
 
         // store previous exception for restoration if we rescue something
         loadThreadContext();
         invokeThreadContext("getErrorInfo", sig(IRubyObject.class));
         method.astore(getPreviousExceptionIndex());
 
         Label beforeBody = new Label();
         Label afterBody = new Label();
         Label rubyCatchBlock = new Label();
         Label flowCatchBlock = new Label();
         method.visitTryCatchBlock(beforeBody, afterBody, flowCatchBlock, p(JumpException.FlowControlException.class));
         method.visitTryCatchBlock(beforeBody, afterBody, rubyCatchBlock, p(Throwable.class));
 
         method.visitLabel(beforeBody);
         {
             regularCode.branch(this);
         }
         method.label(afterBody);
         method.go_to(exitRescue);
 
         // Handle Flow exceptions, just propagating them
         method.label(flowCatchBlock);
         {
             // rethrow to outer flow catcher
             method.athrow();
         }
 
         // Handle Ruby exceptions (RaiseException)
         method.label(rubyCatchBlock);
         {
             method.astore(getExceptionIndex());
 
             rubyCatchCode.branch(this);
             method.label(afterRubyCatchBody);
             method.go_to(exitRescue);
         }
 
         // retry handling in the rescue blocks
         if (needsRetry) {
             method.trycatch(rubyCatchBlock, afterRubyCatchBody, catchRetry, p(JumpException.RetryJump.class));
             method.label(catchRetry);
             {
                 method.pop();
             }
             method.go_to(beforeBody);
         }
 
         // and remaining jump exceptions should restore $!
         method.trycatch(beforeBody, afterRubyCatchBody, catchJumps, p(JumpException.FlowControlException.class));
         method.label(catchJumps);
         {
             loadThreadContext();
             method.aload(getPreviousExceptionIndex());
             invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
             method.pop();
             method.athrow();
         }
 
         method.label(exitRescue);
 
         // restore the original exception
         loadThreadContext();
         method.aload(getPreviousExceptionIndex());
         invokeThreadContext("setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
         method.pop();
     }
 
     public void wrapJavaException() {
         loadRuntime();
         loadException();
         wrapJavaObject();
     }
 
     public void wrapJavaObject() {
         method.invokestatic(p(JavaUtil.class), "convertJavaToUsableRubyObject", sig(IRubyObject.class, Ruby.class, Object.class));
     }
 
     public void inDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_1();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void outDefined() {
         method.aload(StandardASMCompiler.THREADCONTEXT_INDEX);
         method.iconst_0();
         invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
     }
 
     public void stringOrNil() {
         loadRuntime();
         loadNil();
         invokeUtilityMethod("stringOrNil", sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
     }
 
     public void pushNull() {
         method.aconst_null();
     }
 
     public void pushString(String str) {
         method.ldc(str);
     }
 
     public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         metaclass();
         method.ldc(name);
         method.iconst_0(); // push false
         method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void hasBlock(BranchCallback trueBranch, BranchCallback falseBranch) {
         loadBlock();
         method.invokevirtual(p(Block.class), "isGiven", sig(boolean.class));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isGlobalDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadRuntime();
         invokeRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(name);
         method.invokevirtual(p(GlobalVariables.class), "isDefined", sig(boolean.class, params(String.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isConstantDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadThreadContext();
         method.ldc(name);
         invokeThreadContext("getConstantDefined", sig(boolean.class, params(String.class)));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isInstanceVariableDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         loadSelf();
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.ldc(name);
         //method.invokeinterface(p(IRubyObject.class), "getInstanceVariable", sig(IRubyObject.class, params(String.class)));
         method.invokeinterface(p(InstanceVariables.class), "fastHasInstanceVariable", sig(boolean.class, params(String.class)));
         Label trueLabel = new Label();
         Label exitLabel = new Label();
         //method.ifnonnull(trueLabel);
         method.ifne(trueLabel);
         falseBranch.branch(this);
         method.go_to(exitLabel);
         method.label(trueLabel);
         trueBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "fastIsClassVarDefined", sig(boolean.class, params(String.class)));
         Label trueLabel = new Label();
         Label exitLabel = new Label();
         method.ifne(trueLabel);
         falseBranch.branch(this);
         method.go_to(exitLabel);
         method.label(trueLabel);
         trueBranch.branch(this);
         method.label(exitLabel);
     }
 
     public Object getNewEnding() {
         return new Label();
     }
 
     public void isNil(BranchCallback trueBranch, BranchCallback falseBranch) {
         method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifeq(falseLabel); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void isNull(BranchCallback trueBranch, BranchCallback falseBranch) {
         Label falseLabel = new Label();
         Label exitLabel = new Label();
         method.ifnonnull(falseLabel);
         trueBranch.branch(this);
         method.go_to(exitLabel);
         method.label(falseLabel);
         falseBranch.branch(this);
         method.label(exitLabel);
     }
 
     public void ifNull(Object gotoToken) {
         method.ifnull((Label) gotoToken);
     }
 
     public void ifNotNull(Object gotoToken) {
         method.ifnonnull((Label) gotoToken);
     }
 
     public void setEnding(Object endingToken) {
         method.label((Label) endingToken);
     }
 
     public void go(Object gotoToken) {
         method.go_to((Label) gotoToken);
     }
 
     public void isConstantBranch(final BranchCallback setup, final BranchCallback isConstant, final BranchCallback isMethod, final BranchCallback none, final String name) {
         rescue(new BranchCallback() {
 
             public void branch(BodyCompiler context) {
                 setup.branch(BaseBodyCompiler.this);
                 method.dup(); //[C,C]
                 method.instance_of(p(RubyModule.class)); //[C, boolean]
 
                 Label falseJmp = new Label();
                 Label afterJmp = new Label();
                 Label nextJmp = new Label();
                 Label nextJmpPop = new Label();
 
                 method.ifeq(nextJmp); // EQ == 0 (i.e. false)   //[C]
                 method.visitTypeInsn(CHECKCAST, p(RubyModule.class));
                 method.dup(); //[C, C]
                 method.ldc(name); //[C, C, String]
                 method.invokevirtual(p(RubyModule.class), "fastGetConstantAt", sig(IRubyObject.class, params(String.class))); //[C, null|C]
                 method.dup();
                 method.ifnull(nextJmpPop);
                 method.pop();
                 method.pop();
 
                 isConstant.branch(BaseBodyCompiler.this);
 
                 method.go_to(afterJmp);
 
                 method.label(nextJmpPop);
                 method.pop();
 
                 method.label(nextJmp); //[C]
 
                 metaclass();
                 method.ldc(name);
                 method.iconst_1(); // push true
                 method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
                 method.ifeq(falseJmp); // EQ == 0 (i.e. false)
 
                 isMethod.branch(BaseBodyCompiler.this);
                 method.go_to(afterJmp);
 
                 method.label(falseJmp);
                 none.branch(BaseBodyCompiler.this);
 
                 method.label(afterJmp);
             }
         }, JumpException.class, none, String.class);
     }
 
     public void metaclass() {
         invokeIRubyObject("getMetaClass", sig(RubyClass.class));
     }
 
     public void aprintln() {
         method.aprintln();
     }
 
     public void getVisibilityFor(String name) {
         method.ldc(name);
         method.invokevirtual(p(RubyClass.class), "searchMethod", sig(DynamicMethod.class, params(String.class)));
         method.invokevirtual(p(DynamicMethod.class), "getVisibility", sig(Visibility.class));
     }
 
     public void isPrivate(Object gotoToken, int toConsume) {
         method.getstatic(p(Visibility.class), "PRIVATE", ci(Visibility.class));
         Label temp = new Label();
         method.if_acmpne(temp);
         while ((toConsume--) > 0) {
             method.pop();
         }
         method.go_to((Label) gotoToken);
         method.label(temp);
     }
 
     public void isNotProtected(Object gotoToken, int toConsume) {
         method.getstatic(p(Visibility.class), "PROTECTED", ci(Visibility.class));
         Label temp = new Label();
         method.if_acmpeq(temp);
         while ((toConsume--) > 0) {
             method.pop();
         }
         method.go_to((Label) gotoToken);
         method.label(temp);
     }
 
     public void selfIsKindOf(Object gotoToken) {
         method.invokevirtual(p(RubyClass.class), "getRealClass", sig(RubyClass.class));
         loadSelf();
         method.invokevirtual(p(RubyModule.class), "isInstance", sig(boolean.class, params(IRubyObject.class)));
         method.ifne((Label) gotoToken); // EQ != 0 (i.e. true)
     }
 
     public void notIsModuleAndClassVarDefined(String name, Object gotoToken) {
         method.dup(); //[?, ?]
         method.instance_of(p(RubyModule.class)); //[?, boolean]
         Label falsePopJmp = new Label();
         Label successJmp = new Label();
         method.ifeq(falsePopJmp);
 
         method.visitTypeInsn(CHECKCAST, p(RubyModule.class)); //[RubyModule]
         method.ldc(name); //[RubyModule, String]
 
         method.invokevirtual(p(RubyModule.class), "fastIsClassVarDefined", sig(boolean.class, params(String.class))); //[boolean]
         method.ifeq((Label) gotoToken);
         method.go_to(successJmp);
         method.label(falsePopJmp);
         method.pop();
         method.go_to((Label) gotoToken);
         method.label(successJmp);
     }
 
     public void ifSingleton(Object gotoToken) {
         method.invokevirtual(p(RubyModule.class), "isSingleton", sig(boolean.class));
         method.ifne((Label) gotoToken); // EQ == 0 (i.e. false)
     }
 
     public void getInstanceVariable(String name) {
         method.ldc(name);
         invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
         method.invokeinterface(p(InstanceVariables.class), "fastGetInstanceVariable", sig(IRubyObject.class, params(String.class)));
     }
 
     public void getFrameName() {
         loadThreadContext();
         invokeThreadContext("getFrameName", sig(String.class));
     }
 
     public void getFrameKlazz() {
         loadThreadContext();
         invokeThreadContext("getFrameKlazz", sig(RubyModule.class));
     }
 
     public void superClass() {
         method.invokevirtual(p(RubyModule.class), "getSuperClass", sig(RubyClass.class));
     }
 
     public void attached() {
         method.visitTypeInsn(CHECKCAST, p(MetaClass.class));
         method.invokevirtual(p(MetaClass.class), "getAttached", sig(IRubyObject.class));
     }
 
     public void ifNotSuperMethodBound(Object token) {
         method.swap();
         method.iconst_0();
         method.invokevirtual(p(RubyModule.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
         method.ifeq((Label) token);
     }
 
     public void concatArrays() {
         method.invokevirtual(p(RubyArray.class), "concat", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void concatObjectArrays() {
         invokeUtilityMethod("concatObjectArrays", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject[].class)));
     }
 
     public void appendToArray() {
         method.invokevirtual(p(RubyArray.class), "append", sig(RubyArray.class, params(IRubyObject.class)));
     }
 
     public void appendToObjectArray() {
         invokeUtilityMethod("appendToObjectArray", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject.class)));
     }
 
     public void convertToJavaArray() {
         method.invokestatic(p(ArgsUtil.class), "convertToJavaArray", sig(IRubyObject[].class, params(IRubyObject.class)));
     }
 
     public void aliasGlobal(String newName, String oldName) {
         loadRuntime();
         invokeRuby("getGlobalVariables", sig(GlobalVariables.class));
         method.ldc(newName);
         method.ldc(oldName);
         method.invokevirtual(p(GlobalVariables.class), "alias", sig(Void.TYPE, params(String.class, String.class)));
         loadNil();
     }
     
     public void raiseTypeError(String msg) {
         loadRuntime();        
         method.ldc(msg);
         invokeRuby("newTypeError", sig(RaiseException.class, params(String.class)));
         method.athrow();                                    
     }    
 
     public void undefMethod(String name) {
         loadThreadContext();
         invokeThreadContext("getRubyClass", sig(RubyModule.class));
 
         Label notNull = new Label();
         method.dup();
         method.ifnonnull(notNull);
         method.pop();
         loadRuntime();
         method.ldc("No class to undef method '" + name + "'.");
         invokeRuby("newTypeError", sig(RaiseException.class, params(String.class)));
         method.athrow();
 
         method.label(notNull);
         loadThreadContext();
         method.ldc(name);
         method.invokevirtual(p(RubyModule.class), "undef", sig(Void.TYPE, params(ThreadContext.class, String.class)));
 
         loadNil();
     }
 
     public void defineClass(
             final String name,
             final StaticScope staticScope,
             final CompilerCallback superCallback,
             final CompilerCallback pathCallback,
             final CompilerCallback bodyCallback,
             final CompilerCallback receiverCallback,
             final ASTInspector inspector) {
         String classMethodName = null;
         if (receiverCallback == null) {
             String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             classMethodName = "class_" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
         } else {
             classMethodName = "sclass_" + script.getAndIncrementMethodIndex() + "$RUBY$__singleton__";
         }
 
         final RootScopedBodyCompiler classBody = new ClassBodyCompiler(script, classMethodName, inspector, staticScope);
 
         CompilerCallback bodyPrep = new CompilerCallback() {
             public void call(BodyCompiler context) {
                 if (receiverCallback == null) {
                     // no receiver for singleton class
                     if (superCallback != null) {
                         // but there's a superclass passed in, use it
                         classBody.loadRuntime();
                         classBody.method.aload(StandardASMCompiler.SELF_INDEX);
 
                         classBody.invokeUtilityMethod("prepareSuperClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                     } else {
                         classBody.method.aconst_null();
                     }
 
                     classBody.loadThreadContext();
 
                     pathCallback.call(classBody);
 
                     classBody.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                     classBody.method.swap();
 
                     classBody.method.ldc(name);
 
                     classBody.method.swap();
 
                     classBody.method.invokevirtual(p(RubyModule.class), "defineOrGetClassUnder", sig(RubyClass.class, params(String.class, RubyClass.class)));
                 } else {
                     classBody.loadRuntime();
 
                     // we re-set self to the class, but store the old self in a temporary local variable
                     // this is to prevent it GCing in case the singleton is short-lived
                     classBody.method.aload(StandardASMCompiler.SELF_INDEX);
                     int selfTemp = classBody.getVariableCompiler().grabTempLocal();
                     classBody.getVariableCompiler().setTempLocal(selfTemp);
                     classBody.method.aload(StandardASMCompiler.SELF_INDEX);
 
                     classBody.invokeUtilityMethod("getSingletonClass", sig(RubyClass.class, params(Ruby.class, IRubyObject.class)));
                 }
 
                 // set self to the class
                 classBody.method.dup();
                 classBody.method.astore(StandardASMCompiler.SELF_INDEX);
 
                 // CLASS BODY
                 classBody.loadThreadContext();
                 classBody.method.swap();
 
                 // static scope
                 script.getCacheCompiler().cacheStaticScope(classBody, staticScope);
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 } else {
                     classBody.invokeThreadContext("preCompiledClassDummyScope", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 }
+
+                if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceClass();
             }
         };
 
         // Here starts the logic for the class definition
         Label start = new Label();
         Label end = new Label();
         Label after = new Label();
         Label noException = new Label();
         classBody.method.trycatch(start, end, after, null);
 
         classBody.beginMethod(bodyPrep, staticScope);
 
         classBody.method.label(start);
 
         bodyCallback.call(classBody);
         classBody.method.label(end);
         // finally with no exception
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceEnd();
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
 
         classBody.method.go_to(noException);
 
         classBody.method.label(after);
         // finally with exception
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceEnd();
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
         classBody.method.athrow();
 
         classBody.method.label(noException);
 
         classBody.endBody();
 
         // prepare to call class definition method
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         if (receiverCallback == null) {
             // if there's no receiver, evaluate and pass in the superclass, or
             // pass self if it no superclass
             if (superCallback != null) {
                 superCallback.call(this);
             } else {
                 method.aload(StandardASMCompiler.SELF_INDEX);
             }
         } else {
             // otherwise, there's a receiver, so we pass that in directly for the sclass logic
             receiverCallback.call(this);
         }
         method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
         method.invokestatic(script.getClassname(), classMethodName, StandardASMCompiler.getStaticMethodSignature(script.getClassname(), 0));
     }
 
     public void defineModule(final String name, final StaticScope staticScope, final CompilerCallback pathCallback, final CompilerCallback bodyCallback, final ASTInspector inspector) {
         String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
         String moduleMethodName = "module__" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
 
         final RootScopedBodyCompiler classBody = new ClassBodyCompiler(script, moduleMethodName, inspector, staticScope);
 
         CompilerCallback bodyPrep = new CompilerCallback() {
 
             public void call(BodyCompiler context) {
                 classBody.loadThreadContext();
 
                 pathCallback.call(classBody);
 
                 classBody.invokeUtilityMethod("prepareClassNamespace", sig(RubyModule.class, params(ThreadContext.class, IRubyObject.class)));
 
                 classBody.method.ldc(name);
 
                 classBody.method.invokevirtual(p(RubyModule.class), "defineOrGetModuleUnder", sig(RubyModule.class, params(String.class)));
 
                 // set self to the class
                 classBody.method.dup();
                 classBody.method.astore(StandardASMCompiler.SELF_INDEX);
 
                 // CLASS BODY
                 classBody.loadThreadContext();
                 classBody.method.swap();
 
                 // static scope
                 script.getCacheCompiler().cacheStaticScope(classBody, staticScope);
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     classBody.invokeThreadContext("preCompiledClass", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 } else {
                     classBody.invokeThreadContext("preCompiledClassDummyScope", sig(Void.TYPE, params(RubyModule.class, StaticScope.class)));
                 }
+
+                if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceClass();
             }
         };
 
         // Here starts the logic for the class definition
         Label start = new Label();
         Label end = new Label();
         Label after = new Label();
         Label noException = new Label();
         classBody.method.trycatch(start, end, after, null);
 
         classBody.beginMethod(bodyPrep, staticScope);
 
         classBody.method.label(start);
 
         bodyCallback.call(classBody);
         classBody.method.label(end);
 
         classBody.method.go_to(noException);
 
         classBody.method.label(after);
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceEnd();
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
         classBody.method.athrow();
 
         classBody.method.label(noException);
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) classBody.traceEnd();
         classBody.loadThreadContext();
         classBody.invokeThreadContext("postCompiledClass", sig(Void.TYPE, params()));
 
         classBody.endBody();
 
         // prepare to call class definition method
         method.aload(StandardASMCompiler.THIS);
         loadThreadContext();
         loadSelf();
         method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
         method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
         method.invokestatic(script.getClassname(), moduleMethodName, StandardASMCompiler.getStaticMethodSignature(script.getClassname(), 4));
     }
 
     public void unwrapPassedBlock() {
         loadBlock();
         invokeUtilityMethod("getBlockFromBlockPassBody", sig(Block.class, params(IRubyObject.class, Block.class)));
     }
 
     public void performBackref(char type) {
         loadThreadContext();
         switch (type) {
         case '~':
             invokeUtilityMethod("backref", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '&':
             invokeUtilityMethod("backrefLastMatch", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '`':
             invokeUtilityMethod("backrefMatchPre", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '\'':
             invokeUtilityMethod("backrefMatchPost", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         case '+':
             invokeUtilityMethod("backrefMatchLast", sig(IRubyObject.class, params(ThreadContext.class)));
             break;
         default:
             throw new NotCompilableException("ERROR: backref with invalid type");
         }
     }
 
     public void callZSuper(CompilerCallback closure) {
         ArgumentsCallback argsCallback = new ArgumentsCallback() {
             public int getArity() {
                 return -1;
             }
 
             public void call(BodyCompiler context) {
                 loadThreadContext();
                 invokeUtilityMethod("getArgValues", sig(IRubyObject[].class, ThreadContext.class));
             }
         };
         getInvocationCompiler().invokeDynamic(null, null, argsCallback, CallType.SUPER, closure, false);
     }
 
     public void checkIsExceptionHandled(ArgumentsCallback rescueArgs) {
         // original exception is on stack
         rescueArgs.call(this);
         loadThreadContext();
 
         switch (rescueArgs.getArity()) {
         case 1:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, ThreadContext.class));
             break;
         case 2:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
             break;
         case 3:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
             break;
         default:
             invokeUtilityMethod("isJavaExceptionHandled", sig(IRubyObject.class, Throwable.class, IRubyObject[].class, ThreadContext.class));
         }
     }
 
     public void rethrowException() {
         loadException();
         method.athrow();
     }
 
     public void loadClass(String name) {
         loadRuntime();
         method.ldc(name);
         invokeRuby("getClass", sig(RubyClass.class, String.class));
     }
 
     public void loadStandardError() {
         loadRuntime();
         invokeRuby("getStandardError", sig(RubyClass.class));
     }
 
     public void unwrapRaiseException() {
         // RaiseException is on stack, get RubyException out
         method.invokevirtual(p(RaiseException.class), "getException", sig(RubyException.class));
     }
 
     public void loadException() {
         method.aload(getExceptionIndex());
     }
 
     public void setFilePosition(ISourcePosition position) {
         if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
             loadThreadContext();
             method.ldc(position.getFile());
             invokeThreadContext("setFile", sig(void.class, params(String.class)));
         }
     }
 
     public void setLinePosition(ISourcePosition position) {
         if (!RubyInstanceConfig.POSITIONLESS_COMPILE_ENABLED) {
             if (lastPositionLine == position.getStartLine()) {
                 // updating position for same line; skip
                 return;
             } else {
                 lastPositionLine = position.getStartLine();
                 loadThreadContext();
                 method.pushInt(position.getStartLine());
                 method.invokestatic(script.getClassname(), "setPosition", sig(void.class, params(ThreadContext.class, int.class)));
             }
         }
     }
 
     public void checkWhenWithSplat() {
         loadThreadContext();
         invokeUtilityMethod("isWhenTriggered", sig(RubyBoolean.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
     }
 
     public void issueRetryEvent() {
         invokeUtilityMethod("retryJump", sig(IRubyObject.class));
     }
 
     public void defineNewMethod(String name, int methodArity, StaticScope scope,
             CompilerCallback body, CompilerCallback args,
-            CompilerCallback receiver, ASTInspector inspector, boolean root) {
+            CompilerCallback receiver, ASTInspector inspector, boolean root, String filename, int line) {
         // TODO: build arg list based on number of args, optionals, etc
         String newMethodName;
         if (root && Boolean.getBoolean("jruby.compile.toplevel")) {
             newMethodName = name;
         } else {
             String mangledName = JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             newMethodName = "method__" + script.getAndIncrementMethodIndex() + "$RUBY$" + mangledName;
         }
 
         BodyCompiler methodCompiler = script.startMethod(name, newMethodName, args, scope, inspector);
 
         // callbacks to fill in method body
         body.call(methodCompiler);
 
         methodCompiler.endBody();
 
         // prepare to call "def" utility method to handle def logic
         loadThreadContext();
 
         loadSelf();
 
         if (receiver != null) {
             receiver.call(this);        // script object
         }
         method.aload(StandardASMCompiler.THIS);
 
         method.ldc(name);
 
         method.ldc(newMethodName);
 
         StandardASMCompiler.buildStaticScopeNames(method, scope);
 
         method.pushInt(methodArity);
 
         // arities
         method.pushInt(scope.getRequiredArgs());
         method.pushInt(scope.getOptionalArgs());
         method.pushInt(scope.getRestArg());
+        method.ldc(filename);
+        method.ldc(line);
         method.getstatic(p(CallConfiguration.class), inspector.getCallConfig().name(), ci(CallConfiguration.class));
 
         if (receiver != null) {
             invokeUtilityMethod("defs", sig(IRubyObject.class,
-                    params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
+                    params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, String.class, int.class, CallConfiguration.class)));
         } else {
             invokeUtilityMethod("def", sig(IRubyObject.class,
-                    params(ThreadContext.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
+                    params(ThreadContext.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, String.class, int.class, CallConfiguration.class)));
         }
     }
 
     public void rethrowIfSystemExit() {
         loadRuntime();
         method.ldc("SystemExit");
         method.invokevirtual(p(Ruby.class), "fastGetClass", sig(RubyClass.class, String.class));
         method.swap();
         method.invokevirtual(p(RubyModule.class), "isInstance", sig(boolean.class, params(IRubyObject.class)));
         method.iconst_0();
         Label ifEnd = new Label();
         method.if_icmpeq(ifEnd);
         loadException();
         method.athrow();
         method.label(ifEnd);
     }
 
     public void literalSwitch(int[] cases, Object[] bodies, ArrayCallback arrayCallback, CompilerCallback defaultCallback) {
         // TODO assuming case is a fixnum
         method.checkcast(p(RubyFixnum.class));
         method.invokevirtual(p(RubyFixnum.class), "getLongValue", sig(long.class));
         method.l2i();
 
         Map<Object, Label> labelMap = new HashMap<Object, Label>();
         Label[] labels = new Label[cases.length];
         for (int i = 0; i < labels.length; i++) {
             Object body = bodies[i];
             Label label = labelMap.get(body);
             if (label == null) {
                 label = new Label();
                 labelMap.put(body, label);
             }
             labels[i] = label;
         }
         Label defaultLabel = new Label();
         Label endLabel = new Label();
 
         method.lookupswitch(defaultLabel, cases, labels);
         Set<Label> labelDone = new HashSet<Label>();
         for (int i = 0; i < cases.length; i++) {
             if (labelDone.contains(labels[i])) continue;
             labelDone.add(labels[i]);
             method.label(labels[i]);
             arrayCallback.nextValue(this, bodies, i);
             method.go_to(endLabel);
         }
 
         method.label(defaultLabel);
         defaultCallback.call(this);
         method.label(endLabel);
     }
 
     public void typeCheckBranch(Class type, BranchCallback trueCallback, BranchCallback falseCallback) {
         Label elseLabel = new Label();
         Label done = new Label();
 
         method.dup();
         method.instance_of(p(type));
         method.ifeq(elseLabel);
 
         trueCallback.branch(this);
         method.go_to(done);
 
         method.label(elseLabel);
         falseCallback.branch(this);
 
         method.label(done);
     }
     
     public void loadFilename() {
         loadRuntime();
         loadThis();
         method.getfield(getScriptCompiler().getClassname(), "filename", ci(String.class));
         method.invokestatic(p(RubyString.class), "newString", sig(RubyString.class, Ruby.class, CharSequence.class));
     }
 
     public void compileSequencedConditional(
             CompilerCallback inputValue,
             FastSwitchType fastSwitchType,
             Map<CompilerCallback, int[]> switchCases,
             List<ArgumentsCallback> conditionals,
             List<CompilerCallback> bodies,
             CompilerCallback fallback) {
         Map<CompilerCallback, Label> bodyLabels = new HashMap<CompilerCallback, Label>();
         Label defaultCase = new Label();
         Label slowPath = new Label();
         CompilerCallback getCaseValue = null;
         final int tmp = getVariableCompiler().grabTempLocal();
         
         if (inputValue != null) {
             // we have an input case, prepare branching logic
             inputValue.call(this);
             getVariableCompiler().setTempLocal(tmp);
             getCaseValue = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     getVariableCompiler().getTempLocal(tmp);
                 }
             };
 
             if (switchCases != null) {
                 // we have optimized switch cases, build a lookupswitch
 
                 SortedMap<Integer, Label> optimizedLabels = new TreeMap<Integer, Label>();
                 for (Map.Entry<CompilerCallback, int[]> entry : switchCases.entrySet()) {
                     Label lbl = new Label();
 
                     bodyLabels.put(entry.getKey(), lbl);
                     
                     for (int i : entry.getValue()) {
                         optimizedLabels.put(i, lbl);
                     }
                 }
 
                 int[] caseValues = new int[optimizedLabels.size()];
                 Label[] caseLabels = new Label[optimizedLabels.size()];
                 Set<Map.Entry<Integer, Label>> entrySet = optimizedLabels.entrySet();
                 Iterator<Map.Entry<Integer, Label>> iterator = entrySet.iterator();
                 for (int i = 0; i < entrySet.size(); i++) {
                     Map.Entry<Integer, Label> entry = iterator.next();
                     caseValues[i] = entry.getKey();
                     caseLabels[i] = entry.getValue();
                 }
 
                 // checkcast the value; if match, fast path; otherwise proceed to slow logic
                 getCaseValue.call(this);
                 method.instance_of(p(fastSwitchType.getAssociatedClass()));
                 method.ifeq(slowPath);
 
                 switch (fastSwitchType) {
                 case FIXNUM:
                     getCaseValue.call(this);
                     method.checkcast(p(RubyFixnum.class));
                     method.invokevirtual(p(RubyFixnum.class), "getLongValue", sig(long.class));
                     method.l2i();
                     break;
                 case SINGLE_CHAR_STRING:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSingleCharString", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSingleCharString", sig(int.class, IRubyObject.class));
                     break;
                 case STRING:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableString", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchString", sig(int.class, IRubyObject.class));
                     break;
                 case SINGLE_CHAR_SYMBOL:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSingleCharSymbol", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSingleCharSymbol", sig(int.class, IRubyObject.class));
                     break;
                 case SYMBOL:
                     getCaseValue.call(this);
                     invokeUtilityMethod("isFastSwitchableSymbol", sig(boolean.class, IRubyObject.class));
                     method.ifeq(slowPath);
                     getCaseValue.call(this);
                     invokeUtilityMethod("getFastSwitchSymbol", sig(int.class, IRubyObject.class));
                     break;
                 }
 
                 method.lookupswitch(defaultCase, caseValues, caseLabels);
             }
         }
 
         Label done = new Label();
 
         // expression-based tests + bodies
         Label currentLabel = slowPath;
         for (int i = 0; i < conditionals.size(); i++) {
             ArgumentsCallback conditional = conditionals.get(i);
             CompilerCallback body = bodies.get(i);
 
             method.label(currentLabel);
 
             getInvocationCompiler().invokeEqq(conditional, getCaseValue);
             if (i + 1 < conditionals.size()) {
                 // normal case, create a new label
                 currentLabel = new Label();
             } else {
                 // last conditional case, use defaultCase
                 currentLabel = defaultCase;
             }
             method.ifeq(currentLabel);
 
             Label bodyLabel = bodyLabels.get(body);
             if (bodyLabel != null) method.label(bodyLabel);
 
             body.call(this);
 
             method.go_to(done);
         }
 
         // "else" body
         method.label(currentLabel);
         fallback.call(this);
 
         method.label(done);
 
         getVariableCompiler().releaseTempLocal();
     }
+
+    public void traceLine() {
+        loadThreadContext();
+        invokeUtilityMethod("traceLine", sig(void.class, ThreadContext.class));
+    }
+
+    public void traceClass() {
+        loadThreadContext();
+        invokeUtilityMethod("traceClass", sig(void.class, ThreadContext.class));
+    }
+
+    public void traceEnd() {
+        loadThreadContext();
+        invokeUtilityMethod("traceEnd", sig(void.class, ThreadContext.class));
+    }
 }
\ No newline at end of file
diff --git a/src/org/jruby/internal/runtime/methods/CompiledMethod.java b/src/org/jruby/internal/runtime/methods/CompiledMethod.java
index fc8f961522..723c60f0c5 100644
--- a/src/org/jruby/internal/runtime/methods/CompiledMethod.java
+++ b/src/org/jruby/internal/runtime/methods/CompiledMethod.java
@@ -1,243 +1,246 @@
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
  * Copyright (C) 2007 Charles Oliver Nutter <headius@headius.com>
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
 import org.jruby.internal.runtime.JumpTarget;
+import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodFactory;
 
 public abstract class CompiledMethod extends JavaMethod implements JumpTarget, Cloneable {
     protected Object $scriptObject;
     
     public static class LazyCompiledMethod extends DynamicMethod implements JumpTarget, Cloneable {
         private final String method;
         private final Arity arity;
         private final StaticScope scope;
         private final Object scriptObject;
         private MethodFactory factory;
         private DynamicMethod compiledMethod;
+        private final ISourcePosition position;
     
         public LazyCompiledMethod(RubyModule implementationClass, String method, Arity arity, 
-            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig, MethodFactory factory) {
+            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig, ISourcePosition position, MethodFactory factory) {
             super(implementationClass, visibility, callConfig);
             this.method = method;
             this.arity = arity;
             this.scope = scope;
             this.scriptObject = scriptObject;
             this.factory = factory;
+            this.position = position;
         }
         
         private synchronized void initializeMethod() {
             if (compiledMethod != null) return;
-            compiledMethod = factory.getCompiledMethod(implementationClass, method, arity, visibility, scope, scriptObject, callConfig);
+            compiledMethod = factory.getCompiledMethod(implementationClass, method, arity, visibility, scope, scriptObject, callConfig, position);
             factory = null;
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0, arg1);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0, arg1, arg2);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, args);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, block);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0, block);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0, arg1, block);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, arg0, arg1, arg2, block);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.call(context, self, clazz, name, args, block);
         }
 
         @Override
         public Arity getArity() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getArity();
         }
 
         @Override
         public CallConfiguration getCallConfig() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getCallConfig();
         }
 
         @Override
         public RubyModule getImplementationClass() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getImplementationClass();
         }
 
         @Override
         protected RubyModule getProtectedClass() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getProtectedClass();
         }
 
         @Override
         public DynamicMethod getRealMethod() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getRealMethod();
         }
 
         @Override
         public Visibility getVisibility() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.getVisibility();
         }
 
         @Override
         public boolean isCallableFrom(IRubyObject caller, CallType callType) {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.isCallableFrom(caller, callType);
         }
 
         @Override
         public boolean isNative() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.isNative();
         }
 
         @Override
         public void setCallConfig(CallConfiguration callConfig) {
             if (compiledMethod == null) initializeMethod();
             compiledMethod.setCallConfig(callConfig);
         }
 
         @Override
         public void setImplementationClass(RubyModule implClass) {
             if (compiledMethod == null) initializeMethod();
             compiledMethod.setImplementationClass(implClass);
         }
 
         @Override
         public void setVisibility(Visibility visibility) {
             if (compiledMethod == null) initializeMethod();
             compiledMethod.setVisibility(visibility);
         }
 
         @Override
         public DynamicMethod dup() {
             if (compiledMethod == null) initializeMethod();
             return compiledMethod.dup();
         }
         
     }
     
     protected CompiledMethod() {}
     
     protected void init(RubyModule implementationClass, Arity arity, Visibility visibility, StaticScope staticScope, Object scriptObject, CallConfiguration callConfig) {
         this.$scriptObject = scriptObject;
         super.init(implementationClass, arity, visibility, staticScope, callConfig);
     }
         
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
         return call(context, self, clazz, name, Block.NULL_BLOCK);
     }
         
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
         return call(context, self, clazz, name, arg, Block.NULL_BLOCK);
     }
         
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2) {
         return call(context, self, clazz, name, arg1, arg2, Block.NULL_BLOCK);
     }
         
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return call(context, self, clazz, name, arg1, arg2, arg3, Block.NULL_BLOCK);
     }
     
     @Override
     public DynamicMethod dup() {
         try {
             CompiledMethod msm = (CompiledMethod)clone();
             return msm;
         } catch (CloneNotSupportedException cnse) {
             return null;
         }
     }
 
     @Override
     public boolean isNative() {
         return false;
     }
 }// SimpleInvocationMethod
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index a89d0ed261..1f3fd3901c 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,355 +1,357 @@
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
  * Copyright (C) 2004-2008 Thomas E Enebo <enebo@acm.org>
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
 
 
 ////////////////////////////////////////////////////////////////////////////////
 // NOTE: THIS FILE IS GENERATED! DO NOT EDIT THIS FILE!
 // generated from: src/org/jruby/internal/runtime/methods/DefaultMethod.erb
 // using arities: src/org/jruby/internal/runtime/methods/DefaultMethod.arities.erb
 ////////////////////////////////////////////////////////////////////////////////
 
 
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.RubyModule;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This is the mixed-mode method type.  It will call out to JIT compiler to see if the compiler
  * wants to JIT or not.  If the JIT compiler does JIT this it will return the new method
  * to be executed here instead of executing the interpreted version of this method.  The next
  * invocation of the method will end up causing the runtime to load and execute the newly JIT'd
  * method.
  *
  */
 public class DefaultMethod extends DynamicMethod implements JumpTarget, MethodArgs {
     private static class DynamicMethodBox {
         public DynamicMethod actualMethod;
         public int callCount = 0;
     }
 
     private DynamicMethodBox box = new DynamicMethodBox();
     private final StaticScope staticScope;
     private final Node body;
     private final ArgsNode argsNode;
     private final ISourcePosition position;
     private final InterpretedMethod interpretedMethod;
 
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body,
             ArgsNode argsNode, Visibility visibility, ISourcePosition position) {
         super(implementationClass, visibility, CallConfiguration.FrameFullScopeFull);
-        this.interpretedMethod = new InterpretedMethod(implementationClass, staticScope, body, argsNode, visibility, position);
+        this.interpretedMethod = DynamicMethodFactory.newInterpretedMethod(
+                implementationClass.getRuntime(), implementationClass, staticScope,
+                body, argsNode, visibility, position);
         this.box.actualMethod = interpretedMethod;
         this.argsNode = argsNode;
         this.body = body;
         this.staticScope = staticScope;
         this.position = position;
 
         assert argsNode != null;
     }
 
     public int getCallCount() {
         return box.callCount;
     }
 
     public int incrementCallCount() {
         return ++box.callCount;
     }
 
     public void setCallCount(int callCount) {
         this.box.callCount = callCount;
     }
 
     public Node getBodyNode() {
         return body;
     }
 
     public ArgsNode getArgsNode() {
         return argsNode;
     }
 
     public StaticScope getStaticScope() {
         return staticScope;
     }
 
     public DynamicMethod getMethodForCaching() {
         DynamicMethod method = box.actualMethod;
         if (method instanceof JittedMethod) {
             return method;
         }
         return this;
     }
 
     public void switchToJitted(Script jitCompiledScript, CallConfiguration jitCallConfig) {
-        this.box.actualMethod = new JittedMethod(
-                getImplementationClass(), staticScope, jitCompiledScript,
-                jitCallConfig, getVisibility(), argsNode.getArity(), position,
+        this.box.actualMethod = DynamicMethodFactory.newJittedMethod(
+                getImplementationClass().getRuntime(), getImplementationClass(),
+                staticScope, jitCompiledScript, jitCallConfig, getVisibility(), argsNode.getArity(), position,
                 this);
         this.box.callCount = -1;
         getImplementationClass().invalidateCacheDescendants();
     }
 
     private DynamicMethod tryJitReturnMethod(ThreadContext context, String name) {
         context.getRuntime().getJITCompiler().tryJIT(this, context, name);
         return box.actualMethod;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, args, block);
         }
         
         return box.actualMethod.call(context, self, clazz, name, args, block);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, args);
         }
 
         return box.actualMethod.call(context, self, clazz, name, args);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name);
         }
 
         return box.actualMethod.call(context, self, clazz, name );
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4, arg5);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4, arg5, arg6);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, block);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, IRubyObject arg9) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
         }
 
         return box.actualMethod.call(context, self, clazz, name , arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, IRubyObject arg9, Block block) {
         if (box.callCount >= 0) {
             return tryJitReturnMethod(context, name).call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, block);
         }
 
         return box.actualMethod.call(context, self, clazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, block);
     }
 
 
     public ISourcePosition getPosition() {
         return position;
     }
 
     @Override
     public Arity getArity() {
         return argsNode.getArity();
     }
 
     public DynamicMethod dup() {
         DefaultMethod newMethod = new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), position);
         newMethod.setIsBuiltin(this.builtin);
         newMethod.box = this.box;
         return newMethod;
     }
 
     @Override
     public void setVisibility(Visibility visibility) {
         // We promote our box to being its own box since we're changing
         // visibilities, and need it to be reflected on this method object
         // independent of any other sharing the box.
         DynamicMethodBox newBox = new DynamicMethodBox();
         newBox.actualMethod = box.actualMethod.dup();
         newBox.callCount = box.callCount;
         box = newBox;
         super.setVisibility(visibility);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DynamicMethodFactory.java b/src/org/jruby/internal/runtime/methods/DynamicMethodFactory.java
index 80b7d80dfd..7c339e3fcb 100644
--- a/src/org/jruby/internal/runtime/methods/DynamicMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/DynamicMethodFactory.java
@@ -1,56 +1,82 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyInstanceConfig.CompileMode;
 import org.jruby.RubyModule;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
+import org.jruby.ast.executable.Script;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
+import org.jruby.runtime.Arity;
 import org.jruby.runtime.Visibility;
 
 public class DynamicMethodFactory {
-    public static DynamicMethod newInterpretedMethod(
+    public static DynamicMethod newDefaultMethod(
             Ruby runtime, RubyModule container, String name, StaticScope scope,
             Node body, ArgsNode argsNode, Visibility visibility, ISourcePosition position) {
 
         if (runtime.getInstanceConfig().getCompileMode() == CompileMode.OFF) {
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
-                return new TraceableInterpretedMethod(container, name, scope, body, argsNode,
+                return new TraceableInterpretedMethod(container, scope, body, argsNode,
                         visibility, position);
             } else {
                 return new InterpretedMethod(container, scope, body, argsNode, visibility,
                         position);
             }
         } else  {
             return new DefaultMethod(container, scope, body, argsNode, visibility, position);
         }
     }
+    
+    public static InterpretedMethod newInterpretedMethod(
+            Ruby runtime, RubyModule container, StaticScope scope,
+            Node body, ArgsNode argsNode, Visibility visibility, ISourcePosition position) {
+
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            return new TraceableInterpretedMethod(container, scope, body, argsNode,
+                    visibility, position);
+        } else {
+            return new InterpretedMethod(container, scope, body, argsNode, visibility,
+                    position);
+        }
+    }
+
+    public static DynamicMethod newJittedMethod(
+            Ruby runtime, RubyModule container, StaticScope scope, Script script,
+            CallConfiguration config, Visibility visibility, Arity arity, ISourcePosition position, DefaultMethod defaultMethod) {
+
+        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+            return new TraceableJittedMethod(container, scope, script, config, visibility, arity, position, defaultMethod);
+        } else {
+            return new JittedMethod(container, scope, script, config, visibility, arity, position, defaultMethod);
+        }
+    }
 }
diff --git a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
index 943e55bb2f..0bace3a2c6 100644
--- a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
@@ -1,1345 +1,1436 @@
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
 import java.lang.reflect.Modifier;
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyKernel;
 import org.jruby.parser.StaticScope;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
+import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.MethodFactory;
+import org.jruby.runtime.RubyEvent;
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
 
     private final static String BLOCK_CALL_SIG = sig(RubyKernel.IRUBY_OBJECT, params(
             ThreadContext.class, RubyKernel.IRUBY_OBJECT, IRubyObject.class));
     private final static String BLOCK_CALL_SIG19 = sig(RubyKernel.IRUBY_OBJECT, params(
             ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class));
     
     /** The super constructor signature for Java-based method handles. */
     private final static String JAVA_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class));
     
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
     protected final JRubyClassLoader classLoader;
     
     /**
      * Whether this factory has seen undefined methods already. This is used to
      * detect likely method handle collisions when we expect to create a new
      * handle for each call.
      */
     private boolean seenUndefinedClasses = false;
 
     /**
      * Whether we've informed the user that we've seen undefined methods; this
      * is to avoid a flood of repetitive information.
      */
     private boolean haveWarnedUser = false;
     
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
-            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
-        return new CompiledMethod.LazyCompiledMethod(implementationClass, method, arity, visibility, scope, scriptObject, callConfig,
+            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig, ISourcePosition position) {
+        return new CompiledMethod.LazyCompiledMethod(implementationClass, method, arity, visibility, scope, scriptObject, callConfig, position,
                 new InvocationMethodFactory(classLoader));
     }
             
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, Arity arity, 
-            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
+            Visibility visibility, StaticScope scope, Object scriptObject, CallConfiguration callConfig, ISourcePosition position) {
         String sup = COMPILED_SUPER_CLASS;
         Class scriptClass = scriptObject.getClass();
         String mname = scriptClass.getName() + "Invoker" + method + arity;
         synchronized (classLoader) {
             Class generatedClass = tryClass(mname, scriptClass);
 
             try {
                 if (generatedClass == null) {
                     String typePath = p(scriptClass);
                     String mnamePath = typePath + "Invoker" + method + arity;
                     ClassWriter cw;
                     int dotIndex = typePath.lastIndexOf('/');
                     cw = createCompiledCtor(mnamePath, typePath.substring(dotIndex + 1) + "#" + method.substring(method.lastIndexOf('$') + 1), sup);
                     SkinnyMethodAdapter mv = null;
                     String signature = null;
                     boolean specificArity = false;
+
+                    // if trace, need to at least populate a backtrace frame
+                    if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                        switch (callConfig) {
+                        case FrameNoneScopeDummy:
+                            callConfig = CallConfiguration.FrameBacktraceScopeDummy;
+                            break;
+                        case FrameNoneScopeFull:
+                            callConfig = CallConfiguration.FrameBacktraceScopeFull;
+                            break;
+                        case FrameNoneScopeNone:
+                            callConfig = CallConfiguration.FrameBacktraceScopeDummy;
+                            break;
+                        }
+                    }
                     
                     if (scope.getRestArg() >= 0 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3) {
                         signature = COMPILED_CALL_SIG_BLOCK;
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", signature, null, null));
                     } else {
                         specificArity = true;
                         
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null));
                         mv.start();
                         
                         // check arity
                         mv.aloadMany(0, 1, 4, 5); // method, context, name, args, required
                         mv.pushInt(scope.getRequiredArgs());
                         mv.invokestatic(p(JavaMethod.class), "checkArgumentCount", sig(void.class, JavaMethod.class, ThreadContext.class, String.class, IRubyObject[].class, int.class));
 
                         mv.aloadMany(0, 1, 2, 3, 4);
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
 
                         // Define a second version that doesn't take a block, so we have unique code paths for both cases.
                         switch (scope.getRequiredArgs()) {
                         case 0:
                             signature = COMPILED_CALL_SIG_ZERO;
                             break;
                         case 1:
                             signature = COMPILED_CALL_SIG_ONE;
                             break;
                         case 2:
                             signature = COMPILED_CALL_SIG_TWO;
                             break;
                         case 3:
                             signature = COMPILED_CALL_SIG_THREE;
                             break;
                         }
                         mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "call", signature, null, null));
                         mv.start();
 
                         mv.aloadMany(0, 1, 2, 3, 4);
                         for (int i = 1; i <= scope.getRequiredArgs(); i++) {
                             mv.aload(4 + i);
                         }
                         mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
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
-                    
-//                    // check arity
-//                    checkArity(mv, scope);
 
                     // invoke pre method stuff
-                    if (!callConfig.isNoop()) {
+                    if (!callConfig.isNoop() || RubyInstanceConfig.FULL_TRACE_ENABLED) {
                         if (specificArity) {
                             invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, scope.getRequiredArgs(), true, callConfig);
                         } else {
                             invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, -1, true, callConfig);
                         }
                     }
 
+                    int traceBoolIndex = -1;
+                    if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                        // load and store trace enabled flag
+                        if (specificArity) {
+                            switch (scope.getRequiredArgs()) {
+                            case -1:
+                                traceBoolIndex = ARGS_INDEX + 1/*block*/ + 1;
+                                break;
+                            case 0:
+                                traceBoolIndex = ARGS_INDEX + 1/*block*/;
+                                break;
+                            default:
+                                traceBoolIndex = ARGS_INDEX + scope.getRequiredArgs() + 1/*block*/ + 1;
+                            }
+                        } else {
+                            traceBoolIndex = ARGS_INDEX + 1/*block*/ + 1;
+                        }
+
+                        mv.aload(1);
+                        mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
+                        mv.invokevirtual(p(Ruby.class), "hasEventHooks", sig(boolean.class));
+                        mv.istore(traceBoolIndex);
+                        // tracing pre
+                        invokeTraceCompiledPre(mv, COMPILED_SUPER_CLASS, traceBoolIndex, position);
+                    }
+
                     Label tryBegin = new Label();
                     Label tryEnd = new Label();
                     Label doFinally = new Label();
                     Label doReturnFinally = new Label();
                     Label doRedoFinally = new Label();
                     Label catchReturnJump = new Label();
                     Label catchRedoJump = new Label();
 
                     boolean heapScoped = callConfig.scoping() == Scoping.Full;
                     boolean framed = callConfig.framing() == Framing.Full;
 
                     if (heapScoped)             mv.trycatch(tryBegin, tryEnd, catchReturnJump, p(JumpException.ReturnJump.class));
                     if (framed)                 mv.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
                     if (framed || heapScoped)   mv.trycatch(tryBegin, tryEnd, doFinally, null);
                     if (heapScoped)             mv.trycatch(catchReturnJump, doReturnFinally, doFinally, null);
                     if (framed)                 mv.trycatch(catchRedoJump, doRedoFinally, doFinally, null);
                     if (framed || heapScoped)   mv.label(tryBegin);
 
                     // main body
                     {
                         mv.aload(0);
                         // FIXME we want to eliminate these type casts when possible
                         mv.getfield(mnamePath, "$scriptObject", ci(Object.class));
                         mv.checkcast(typePath);
                         mv.aloadMany(THREADCONTEXT_INDEX, RECEIVER_INDEX);
                         if (specificArity) {
                             for (int i = 0; i < scope.getRequiredArgs(); i++) {
                                 mv.aload(ARGS_INDEX + i);
                             }
                             mv.aload(ARGS_INDEX + scope.getRequiredArgs());
                             mv.invokestatic(typePath, method, StandardASMCompiler.getStaticMethodSignature(typePath, scope.getRequiredArgs()));
                         } else {
                             mv.aloadMany(ARGS_INDEX, BLOCK_INDEX);
                             mv.invokestatic(typePath, method, StandardASMCompiler.getStaticMethodSignature(typePath, 4));
                         }
                     }
                     if (framed || heapScoped) {
                         mv.label(tryEnd);
                     }
                     
                     // normal exit, perform finally and return
                     {
+                        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                            invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
+                        }
                         if (!callConfig.isNoop()) {
                             invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                         }
                         mv.visitInsn(ARETURN);
                     }
 
                     // return jump handling
                     if (heapScoped) {
                         mv.label(catchReturnJump);
                         {
                             mv.aload(0);
                             mv.swap();
                             mv.aload(1);
                             mv.swap();
                             mv.invokevirtual(COMPILED_SUPER_CLASS, "handleReturn", sig(IRubyObject.class, ThreadContext.class, JumpException.ReturnJump.class));
                             mv.label(doReturnFinally);
 
                             // finally
+                            if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                                invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
+                            }
                             if (!callConfig.isNoop()) {
                                 invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                             }
 
                             // return result if we're still good
                             mv.areturn();
                         }
                     }
 
                     if (framed) {
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
+                            if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                                invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
+                            }
                             if (!callConfig.isNoop()) {
                                 invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                             }
 
                             // throw redo error if we're still good
                             mv.athrow();
                         }
                     }
 
                     // finally handling for abnormal exit
                     if (framed || heapScoped) {
                         mv.label(doFinally);
 
                         //call post method stuff (exception raised)
+                        if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
+                            invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
+                        }
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
     
     private class DescriptorInfo {
         private int min;
         private int max;
         private boolean frame;
         private boolean scope;
         private boolean backtrace;
         private boolean rest;
         private boolean block;
         
         public DescriptorInfo(List<JavaMethodDescriptor> descs) {
             min = Integer.MAX_VALUE;
             max = 0;
             frame = false;
             scope = false;
             backtrace = false;
             rest = false;
             block = false;
 
             for (JavaMethodDescriptor desc: descs) {
                 int specificArity = -1;
                 if (desc.hasVarArgs) {
                     if (desc.optional == 0 && !desc.rest) {
                         throw new RuntimeException("IRubyObject[] args but neither of optional or rest specified for method " + desc.declaringClassName + "." + desc.name);
                     }
                     rest = true;
                 } else {
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             // No required specified, check actual number of required args
                             if (desc.actualRequired <= 3) {
                                 // actual required is less than 3, so we use specific arity
                                 specificArity = desc.actualRequired;
                             } else {
                                 // actual required is greater than 3, raise error (we don't support actual required > 3)
                                 throw new RuntimeException("Invalid specific-arity number of arguments (" + desc.actualRequired + ") on method " + desc.declaringClassName + "." + desc.name);
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             if (desc.actualRequired != desc.required) {
                                 throw new RuntimeException("Specified required args does not match actual on method " + desc.declaringClassName + "." + desc.name);
                             }
                             specificArity = desc.required;
                         }
                     }
 
                     if (specificArity < min) {
                         min = specificArity;
                     }
 
                     if (specificArity > max) {
                         max = specificArity;
                     }
                 }
 
                 frame |= desc.anno.frame();
                 scope |= desc.anno.scope();
                 backtrace |= desc.anno.backtrace();
                 block |= desc.hasBlock;
             }
         }
         
         public boolean isBacktrace() {
             return backtrace;
         }
 
         public boolean isFrame() {
             return frame;
         }
 
         public int getMax() {
             return max;
         }
 
         public int getMin() {
             return min;
         }
 
         public boolean isScope() {
             return scope;
         }
         
         public boolean isRest() {
             return rest;
         }
         
         public boolean isBlock() {
             return block;
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
         
         synchronized (classLoader) {
             try {
                 Class c = getAnnotatedMethodClass(descs);
                 
                 DescriptorInfo info = new DescriptorInfo(descs);
                 if (DEBUG) out.println(" min: " + info.getMin() + ", max: " + info.getMax());
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc1.anno.visibility()});
 
                 ic.setArity(Arity.OPTIONAL);
                 ic.setJavaName(javaMethodName);
                 ic.setSingleton(desc1.isStatic);
                 ic.setCallConfig(CallConfiguration.getCallConfig(info.isFrame(), info.isScope(), info.isBacktrace()));
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
         JavaMethodDescriptor desc1 = descs.get(0);
 
         if (descs.size() == 1) {
             // simple path, no multimethod
             return getAnnotatedMethodClass(desc1);
         }
 
         if (!Modifier.isPublic(desc1.getDeclaringClass().getModifiers())) {
             System.err.println("warning: binding non-public class" + desc1.declaringClassName + "; reflected handles won't work");
         }
         
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, true, desc1.anno.frame());
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // in debug mode we append _DBG to class name to force it to regenerate (or use pre-generated debug version)
             generatedClassName += "_DBG";
         }
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName, desc1.getDeclaringClass());
 
             DescriptorInfo info = new DescriptorInfo(descs);
             if (DEBUG) out.println(" min: " + info.getMin() + ", max: " + info.getMax() + ", hasBlock: " + info.isBlock() + ", rest: " + info.isRest());
 
             if (c == null) {
                 Class superClass = null;
                 if (info.getMin() == -1) {
                     // normal all-rest method
                     superClass = JavaMethod.JavaMethodN.class;
                 } else {
                     if (info.isRest()) {
                         if (info.isBlock()) {
                             superClass = JavaMethod.BLOCK_REST_METHODS[info.getMin()][info.getMax()];
                         } else {
                             superClass = JavaMethod.REST_METHODS[info.getMin()][info.getMax()];
                         }
                     } else {
                         if (info.isBlock()) {
                             superClass = JavaMethod.BLOCK_METHODS[info.getMin()][info.getMax()];
                         } else {
                             superClass = JavaMethod.METHODS[info.getMin()][info.getMax()];
                         }
                     }
                 }
                 
                 if (superClass == null) throw new RuntimeException("invalid multi combination");
                 String superClassString = p(superClass);
                 int dotIndex = desc1.declaringClassName.lastIndexOf('.');
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, desc1.declaringClassName.substring(dotIndex + 1) + "#" + desc1.name, superClassString);
 
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
 
                     createAnnotatedMethodInvocation(desc, mv, superClassString, specificArity, hasBlock);
 
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
 
         if (!Modifier.isPublic(desc.getDeclaringClass().getModifiers())) {
             System.err.println("warning: binding non-public class " + desc.declaringClassName + "; reflected handles won't work");
         }
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc.declaringClassName, desc.isStatic, desc.actualRequired, desc.optional, false, desc.anno.frame());
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // in debug mode we append _DBG to class name to force it to regenerate (or use pre-generated debug version)
             generatedClassName += "_DBG";
         }
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName, desc.getDeclaringClass());
 
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
 
                 int dotIndex = desc.declaringClassName.lastIndexOf('.');
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, desc.declaringClassName.substring(dotIndex + 1) + "#" + desc.name, superClass);
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
 
     public CompiledBlockCallback getBlockCallback(String method, Object scriptObject) {
         Class typeClass = scriptObject.getClass();
         String typePathString = p(typeClass);
         String mname = typeClass.getName() + "BlockCallback$" + method + "xx1";
         String mnamePath = typePathString + "BlockCallback$" + method + "xx1";
         synchronized (classLoader) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     ClassWriter cw = createBlockCtor(mnamePath, typeClass);
                     SkinnyMethodAdapter mv = startBlockCall(cw);
                     mv.aload(0);
                     mv.getfield(mnamePath, "$scriptObject", ci(typeClass));
                     mv.aloadMany(1, 2, 3);
                     mv.invokestatic(typePathString, method, sig(
                             RubyKernel.IRUBY_OBJECT, "L" + typePathString + ";", ThreadContext.class,
                                     RubyKernel.IRUBY_OBJECT, IRubyObject.class));
                     mv.areturn();
 
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 CompiledBlockCallback ic = (CompiledBlockCallback) c.getConstructor(Object.class).newInstance(scriptObject);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public CompiledBlockCallback19 getBlockCallback19(String method, Object scriptObject) {
         Class typeClass = scriptObject.getClass();
         String typePathString = p(typeClass);
         String mname = typeClass.getName() + "BlockCallback$" + method + "xx1";
         String mnamePath = typePathString + "BlockCallback$" + method + "xx1";
         synchronized (classLoader) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     ClassWriter cw = createBlockCtor19(mnamePath, typeClass);
                     SkinnyMethodAdapter mv = startBlockCall19(cw);
                     mv.aload(0);
                     mv.getfield(mnamePath, "$scriptObject", ci(typeClass));
                     mv.aloadMany(1, 2, 3, 4);
                     mv.invokestatic(typePathString, method, sig(
                             IRubyObject.class, "L" + typePathString + ";", ThreadContext.class,
                                     IRubyObject.class, IRubyObject[].class, Block.class));
                     mv.areturn();
 
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 CompiledBlockCallback19 ic = (CompiledBlockCallback19) c.getConstructor(Object.class).newInstance(scriptObject);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     private SkinnyMethodAdapter startBlockCall(ClassWriter cw) {
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC | ACC_FINAL, "call", BLOCK_CALL_SIG, null, null));
 
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         return mv;
     }
 
     private SkinnyMethodAdapter startBlockCall19(ClassWriter cw) {
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC | ACC_SYNTHETIC | ACC_FINAL, "call", BLOCK_CALL_SIG19, null, null));
 
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         return mv;
     }
 
     private ClassWriter createBlockCtor(String namePath, Class fieldClass) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, p(CompiledBlockCallback.class), null);
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "$scriptObject", ci(fieldClass), null, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Object.class)), null, null));
         mv.start();
         mv.aload(0);
         mv.invokespecial(p(CompiledBlockCallback.class), "<init>", sig(void.class));
         mv.aloadMany(0, 1);
         mv.checkcast(p(fieldClass));
         mv.putfield(namePath, "$scriptObject", ci(fieldClass));
         mv.voidreturn();
         mv.end();
 
         return cw;
     }
 
     private ClassWriter createBlockCtor19(String namePath, Class fieldClass) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, p(Object.class), new String[] {p(CompiledBlockCallback19.class)});
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "$scriptObject", ci(fieldClass), null, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Object.class)), null, null));
         mv.start();
         mv.aload(0);
         mv.invokespecial(p(Object.class), "<init>", sig(void.class));
         mv.aloadMany(0, 1);
         mv.checkcast(p(fieldClass));
         mv.putfield(namePath, "$scriptObject", ci(fieldClass));
         mv.voidreturn();
         mv.end();
 
         return cw;
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
             boolean checkArity = false;
             if (jrubyMethod.rest()) {
                 if (jrubyMethod.required() > 0) {
                     // just confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                     checkArity = true;
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
                 checkArity = true;
             } else {
                 // just confirm args length == required
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required());
                 method.if_icmpne(arityError);
                 checkArity = true;
             }
 
             if (checkArity) {
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
     }
 
     private ClassWriter createCompiledCtor(String namePath, String shortPath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         cw.visitSource(shortPath, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null));
         mv.visitCode();
         mv.aload(0);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", "()V");
         mv.visitLineNumber(0, new Label());
         mv.voidreturn();
         mv.end();
 
         return cw;
     }
 
     private ClassWriter createJavaMethodCtor(String namePath, String shortPath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         cw.visitSource(namePath.replace('.', '/') + ".gen", null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", JAVA_SUPER_SIG, null, null));
         mv.start();
         mv.aloadMany(0, 1, 2);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_SUPER_SIG);
         mv.visitLineNumber(0, new Label());
         mv.voidreturn();
         mv.end();
         
         return cw;
     }
 
     private void invokeCallConfigPre(SkinnyMethodAdapter mv, String superClass, int specificArity, boolean block, CallConfiguration callConfig) {
         // invoke pre method stuff
         if (callConfig.isNoop()) return;
 
         prepareForPre(mv, specificArity, block, callConfig);
         mv.invokevirtual(superClass, getPreMethod(callConfig), getPreSignature(callConfig));
     }
 
     private void invokeCallConfigPost(SkinnyMethodAdapter mv, String superClass, CallConfiguration callConfig) {
         if (callConfig.isNoop()) return;
 
         mv.aload(1);
         mv.invokestatic(superClass, getPostMethod(callConfig), sig(void.class, params(ThreadContext.class)));
     }
 
     private void prepareForPre(SkinnyMethodAdapter mv, int specificArity, boolean block, CallConfiguration callConfig) {
         if (callConfig.isNoop()) return;
         
         mv.aloadMany(0, THREADCONTEXT_INDEX);
         
         switch (callConfig.framing()) {
         case Full:
             mv.aloadMany(RECEIVER_INDEX, NAME_INDEX); // self, name
             loadBlockForPre(mv, specificArity, block);
             break;
         case Backtrace:
             mv.aload(NAME_INDEX); // name
             break;
         case None:
             break;
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private String getPreMethod(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return "preFrameAndScope";
         case FrameFullScopeDummy: return "preFrameAndDummyScope";
         case FrameFullScopeNone: return "preFrameOnly";
         case FrameBacktraceScopeFull: return "preBacktraceAndScope";
         case FrameBacktraceScopeDummy: return "preBacktraceDummyscope";
         case FrameBacktraceScopeNone:  return "preBacktraceOnly";
         case FrameNoneScopeFull: return "preScopeOnly";
         case FrameNoneScopeDummy: return "preNoFrameDummyScope";
         case FrameNoneScopeNone: return "preNoop";
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private String getPreSignature(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameFullScopeDummy: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameFullScopeNone: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameBacktraceScopeFull: return sig(void.class, params(ThreadContext.class, String.class));
         case FrameBacktraceScopeDummy: return sig(void.class, params(ThreadContext.class, String.class));
         case FrameBacktraceScopeNone:  return sig(void.class, params(ThreadContext.class, String.class));
         case FrameNoneScopeFull: return sig(void.class, params(ThreadContext.class));
         case FrameNoneScopeDummy: return sig(void.class, params(ThreadContext.class));
         case FrameNoneScopeNone: return sig(void.class);
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     public static String getPostMethod(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return "postFrameAndScope";
         case FrameFullScopeDummy: return "postFrameAndScope";
         case FrameFullScopeNone: return "postFrameOnly";
         case FrameBacktraceScopeFull: return "postBacktraceAndScope";
         case FrameBacktraceScopeDummy: return "postBacktraceDummyscope";
         case FrameBacktraceScopeNone:  return "postBacktraceOnly";
         case FrameNoneScopeFull: return "postScopeOnly";
         case FrameNoneScopeDummy: return "postNoFrameDummyScope";
         case FrameNoneScopeNone: return "postNoop";
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private void loadArguments(SkinnyMethodAdapter mv, JavaMethodDescriptor desc, int specificArity) {
         switch (specificArity) {
         default:
         case -1:
             mv.aload(ARGS_INDEX);
             break;
         case 0:
             // no args
             break;
         case 1:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             break;
         case 2:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             loadArgumentWithCast(mv, 2, desc.argumentTypes[1]);
             break;
         case 3:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             loadArgumentWithCast(mv, 2, desc.argumentTypes[1]);
             loadArgumentWithCast(mv, 3, desc.argumentTypes[2]);
             break;
         }
     }
 
     private void loadArgumentWithCast(SkinnyMethodAdapter mv, int argNumber, Class coerceType) {
         mv.aload(ARGS_INDEX + (argNumber - 1));
         if (coerceType != IRubyObject.class && coerceType != IRubyObject[].class) {
             if (coerceType == RubyString.class) {
                 mv.invokeinterface(p(IRubyObject.class), "convertToString", sig(RubyString.class));
             } else {
                 throw new RuntimeException("Unknown coercion target: " + coerceType);
             }
         }
     }
 
     /** load block argument for pre() call.  Since we have fixed-arity call
      * paths we need calculate where the last var holding the block is.
      *
      * is we don't have a block we setup NULL_BLOCK as part of our null pattern
      * strategy (we do not allow null in any field which accepts block).
      */
     private void loadBlockForPre(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         if (!getsBlock) {            // No block so load null block instance
             mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             return;
         }
 
         loadBlock(mv, specificArity, getsBlock);
     }
 
     /** load the block argument from the correct position.  Since we have fixed-
      * arity call paths we need to calculate where the last var holding the
      * block is.
      * 
      * If we don't have a block then this does nothing.
      */
     private void loadBlock(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         if (!getsBlock) return;         // No block so nothing more to do
         
         switch (specificArity) {        // load block since it accepts a block
         case 0: case 1: case 2: case 3: // Fixed arities signatures
             mv.aload(BLOCK_INDEX - 1 + specificArity);
             break;
         default: case -1:
             mv.aload(BLOCK_INDEX);      // Generic arity signature
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
     private Class tryClass(String name, Class targetClass) {
         try {
             Class c = null;
             if (classLoader == null) {
                 c = Class.forName(name, true, classLoader);
             } else {
                 c = classLoader.loadClass(name);
             }
             
             if (c != null && seenUndefinedClasses && !haveWarnedUser) {
                 haveWarnedUser = true;
                 System.err.println("WARNING: while creating new bindings for " + targetClass + ",\n" +
                         "found an existing binding; you may want to run a clean build.");
             }
             
             return c;
         } catch(Exception e) {
             seenUndefinedClasses = true;
             return null;
         }
     }
 
     private Class tryClass(String name) {
         try {
             return classLoader.loadClass(name);
         } catch (Exception e) {
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
         default: case -1:
             return block ? JavaMethod.class :
                 JavaMethod.JavaMethodN.class;
         case 0:
             return block ? JavaMethod.JavaMethodZeroBlock.class :
                 JavaMethod.JavaMethodZero.class;
         case 1:
             return block ? JavaMethod.JavaMethodOneBlock.class :
                 JavaMethod.JavaMethodOne.class;
         case 2:
             return block ? JavaMethod.JavaMethodTwoBlock.class :
                 JavaMethod.JavaMethodTwo.class;
         case 3:
             return block ? JavaMethod.JavaMethodThreeBlock.class :
                 JavaMethod.JavaMethodThree.class;
         }
     }
 
     private void createAnnotatedMethodInvocation(JavaMethodDescriptor desc, SkinnyMethodAdapter method, String superClass, int specificArity, boolean block) {
         String typePath = desc.declaringClassPath;
         String javaMethodName = desc.name;
 
         checkArity(desc.anno, method, specificArity);
         
         CallConfiguration callConfig = CallConfiguration.getCallConfigByAnno(desc.anno);
         if (!callConfig.isNoop()) {
             invokeCallConfigPre(method, superClass, specificArity, block, callConfig);
         }
-        
+
+        int traceBoolIndex = -1;
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
-            invokeCCallTrace(method);
+            // load and store trace enabled flag
+            switch (specificArity) {
+            case -1:
+                traceBoolIndex = ARGS_INDEX + (block ? 1 : 0) + 1;
+                break;
+            case 0:
+                traceBoolIndex = ARGS_INDEX + (block ? 1 : 0);
+                break;
+            default:
+                traceBoolIndex = ARGS_INDEX + specificArity + (block ? 1 : 0) + 1;
+            }
+
+            method.aload(1);
+            method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
+            method.invokevirtual(p(Ruby.class), "hasEventHooks", sig(boolean.class));
+            method.istore(traceBoolIndex);
+
+            // call trace
+            invokeCCallTrace(method, traceBoolIndex);
         }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label doFinally = new Label();
 
         if (!callConfig.isNoop()) {
             method.trycatch(tryBegin, tryEnd, doFinally, null);
         }
         
         method.label(tryBegin);
         {
             loadReceiver(typePath, desc, method);
             
             loadArguments(method, desc, specificArity);
             
             loadBlock(method, specificArity, block);
 
             if (Modifier.isStatic(desc.modifiers)) {
                 // static invocation
                 method.invokestatic(typePath, javaMethodName, desc.signature);
             } else {
                 // virtual invocation
                 method.invokevirtual(typePath, javaMethodName, desc.signature);
             }
 
             if (desc.getReturnClass() == void.class) {
                 // void return type, so we need to load a nil for returning below
                 method.aload(THREADCONTEXT_INDEX);
                 method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 method.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             }
         }
         method.label(tryEnd);
         
         // normal finally and exit
         {
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
-                invokeCReturnTrace(method);
+                invokeCReturnTrace(method, traceBoolIndex);
             }
             
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(method, superClass, callConfig);
             }
 
             // return
             method.visitInsn(ARETURN);
         }
         
         // these are only needed if we have a non-noop call config
         if (!callConfig.isNoop()) {
             // finally handling for abnormal exit
             {
                 method.label(doFinally);
                 
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
-                    invokeCReturnTrace(method);
+                    invokeCReturnTrace(method, traceBoolIndex);
                 }
 
                 //call post method stuff (exception raised)
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(method, superClass, callConfig);
                 }
 
                 // rethrow exception
                 method.athrow(); // rethrow it
             }
         }
     }
 
-    private void invokeCCallTrace(SkinnyMethodAdapter method) {
-        method.aloadMany(0, 1, 4); // method, threadContext, invokedName
-        method.invokevirtual(p(JavaMethod.class), "callTrace", sig(void.class, ThreadContext.class, String.class));
+    private void invokeCCallTrace(SkinnyMethodAdapter method, int traceBoolIndex) {
+        method.aloadMany(0, 1); // method, threadContext
+        method.iload(traceBoolIndex); // traceEnable
+        method.aload(4); // invokedName
+        method.invokevirtual(p(JavaMethod.class), "callTrace", sig(void.class, ThreadContext.class, boolean.class, String.class));
     }
     
-    private void invokeCReturnTrace(SkinnyMethodAdapter method) {
-        method.aloadMany(0, 1, 4); // method, threadContext, invokedName
-        method.invokevirtual(p(JavaMethod.class), "returnTrace", sig(void.class, ThreadContext.class, String.class));
+    private void invokeCReturnTrace(SkinnyMethodAdapter method, int traceBoolIndex) {
+        method.aloadMany(0, 1); // method, threadContext
+        method.iload(traceBoolIndex); // traceEnable
+        method.aload(4); // invokedName
+        method.invokevirtual(p(JavaMethod.class), "returnTrace", sig(void.class, ThreadContext.class, boolean.class, String.class));
+    }
+
+    private void invokeTraceCompiledPre(SkinnyMethodAdapter mv, String superClass, int traceBoolIndex, ISourcePosition position) {
+        mv.aloadMany(0, 1); // method, threadContext
+        mv.iload(traceBoolIndex); // traceEnable
+        mv.aload(4); // invokedName
+        mv.ldc(position.getFile());
+        mv.ldc(position.getStartLine());
+        mv.invokevirtual(superClass, "callTraceCompiled", sig(void.class, ThreadContext.class, boolean.class, String.class, String.class, int.class));
+    }
+
+    private void invokeTraceCompiledPost(SkinnyMethodAdapter mv, String superClass, int traceBoolIndex) {
+        mv.aloadMany(0, 1); // method, threadContext
+        mv.iload(traceBoolIndex); // traceEnable
+        mv.aload(4); // invokedName
+        mv.invokevirtual(superClass, "returnTraceCompiled", sig(void.class, ThreadContext.class, boolean.class, String.class));
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/JavaMethod.java b/src/org/jruby/internal/runtime/methods/JavaMethod.java
index f560b5f5c1..a8aee9e0a8 100644
--- a/src/org/jruby/internal/runtime/methods/JavaMethod.java
+++ b/src/org/jruby/internal/runtime/methods/JavaMethod.java
@@ -1,1029 +1,1037 @@
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
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  */
 public abstract class JavaMethod extends DynamicMethod implements JumpTarget, Cloneable {
     protected int arityValue;
     protected Arity arity = Arity.OPTIONAL;
     private String javaName;
     private boolean isSingleton;
     protected StaticScope staticScope;
 
     public static final Class[][] METHODS = {
         {JavaMethodZero.class, JavaMethodZeroOrOne.class, JavaMethodZeroOrOneOrTwo.class, JavaMethodZeroOrOneOrTwoOrThree.class},
         {null, JavaMethodOne.class, JavaMethodOneOrTwo.class, JavaMethodOneOrTwoOrThree.class},
         {null, null, JavaMethodTwo.class, JavaMethodTwoOrThree.class},
         {null, null, null, JavaMethodThree.class},
     };
 
     public static final Class[][] REST_METHODS = {
         {JavaMethodZeroOrN.class, JavaMethodZeroOrOneOrN.class, JavaMethodZeroOrOneOrTwoOrN.class, JavaMethodZeroOrOneOrTwoOrThreeOrN.class},
         {null, JavaMethodOneOrN.class, JavaMethodOneOrTwoOrN.class, JavaMethodOneOrTwoOrThreeOrN.class},
         {null, null, JavaMethodTwoOrN.class, JavaMethodTwoOrThreeOrN.class},
         {null, null, null, JavaMethodThreeOrN.class},
     };
 
     public static final Class[][] BLOCK_METHODS = {
         {JavaMethodZeroBlock.class, JavaMethodZeroOrOneBlock.class, JavaMethodZeroOrOneOrTwoBlock.class, JavaMethodZeroOrOneOrTwoOrThreeBlock.class},
         {null, JavaMethodOneBlock.class, JavaMethodOneOrTwoBlock.class, JavaMethodOneOrTwoOrThreeBlock.class},
         {null, null, JavaMethodTwoBlock.class, JavaMethodTwoOrThreeBlock.class},
         {null, null, null, JavaMethodThreeBlock.class},
     };
 
     public static final Class[][] BLOCK_REST_METHODS = {
         {JavaMethodZeroOrNBlock.class, JavaMethodZeroOrOneOrNBlock.class, JavaMethodZeroOrOneOrTwoOrNBlock.class, JavaMethodZeroOrOneOrTwoOrThreeOrNBlock.class},
         {null, JavaMethodOneOrNBlock.class, JavaMethodOneOrTwoOrNBlock.class, JavaMethodOneOrTwoOrThreeOrNBlock.class},
         {null, null, JavaMethodTwoOrNBlock.class, JavaMethodTwoOrThreeOrNBlock.class},
         {null, null, null, JavaMethodThreeOrNBlock.class},
     };
 
     
     public JavaMethod(RubyModule implementationClass, Visibility visibility) {
         this(implementationClass, visibility, CallConfiguration.FrameFullScopeNone);
     }
 
     public JavaMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
         super(implementationClass, visibility, callConfig);
     }
     
     protected JavaMethod() {}
     
     public void init(RubyModule implementationClass, Arity arity, Visibility visibility, StaticScope staticScope, CallConfiguration callConfig) {
         this.staticScope = staticScope;
         this.arity = arity;
         this.arityValue = arity.getValue();
         super.init(implementationClass, visibility, callConfig);
     }
     
     public DynamicMethod dup() {
         try {
             JavaMethod msm = (JavaMethod)clone();
             return msm;
         } catch (CloneNotSupportedException cnse) {
             return null;
         }
     }
 
     protected final void preFrameAndScope(ThreadContext context, IRubyObject self, String name, Block block) {
         context.preMethodFrameAndScope(implementationClass, name, self, block, staticScope);
     }
     
     protected final void preFrameAndDummyScope(ThreadContext context, IRubyObject self, String name, Block block) {
         context.preMethodFrameAndDummyScope(implementationClass, name, self, block, staticScope);
     }
     
     protected final void preFrameOnly(ThreadContext context, IRubyObject self, String name, Block block) {
         context.preMethodFrameOnly(implementationClass, name, self, block);
     }
     
     protected final void preScopeOnly(ThreadContext context) {
         context.preMethodScopeOnly(implementationClass, staticScope);
     }
 
     protected final void preNoFrameDummyScope(ThreadContext context) {
         context.preMethodNoFrameAndDummyScope(implementationClass, staticScope);
     }
     
     protected final void preBacktraceOnly(ThreadContext context, String name) {
         context.preMethodBacktraceOnly(name);
     }
 
     protected final void preBacktraceDummyScope(ThreadContext context, String name) {
         context.preMethodBacktraceDummyScope(implementationClass, name, staticScope);
     }
     
     protected final void preBacktraceAndScope(ThreadContext context, String name) {
         context.preMethodBacktraceAndScope(name, implementationClass, staticScope);
     }
 
     protected final void preNoop() {}
     
     protected final static void postFrameAndScope(ThreadContext context) {
         context.postMethodFrameAndScope();
     }
     
     protected final static void postFrameOnly(ThreadContext context) {
         context.postMethodFrameOnly();
     }
     
     protected final static void postScopeOnly(ThreadContext context) {
         context.postMethodScopeOnly();
     }
 
     protected final static void postNoFrameDummyScope(ThreadContext context) {
         context.postMethodScopeOnly();
     }
     
     protected final static void postBacktraceOnly(ThreadContext context) {
         context.postMethodBacktraceOnly();
     }
 
     protected final static void postBacktraceDummyScope(ThreadContext context) {
         context.postMethodBacktraceDummyScope();
     }
     
     protected final static void postBacktraceAndScope(ThreadContext context) {
         context.postMethodBacktraceAndScope();
     }
 
     protected final static void postNoop(ThreadContext context) {}
     
-    protected final void callTrace(ThreadContext context, String name) {
-        context.trace(RubyEvent.C_CALL, name, getImplementationClass());
+    protected final void callTrace(ThreadContext context, boolean enabled, String name) {
+        if (enabled) context.trace(RubyEvent.C_CALL, name, getImplementationClass());
     }
     
-    protected final void returnTrace(ThreadContext context, String name) {
-        context.trace(RubyEvent.C_CALL, name, getImplementationClass());
+    protected final void returnTrace(ThreadContext context, boolean enabled, String name) {
+        if (enabled) context.trace(RubyEvent.C_RETURN, name, getImplementationClass());
+    }
+
+    protected final void callTraceCompiled(ThreadContext context, boolean enabled, String name, String file, int line) {
+        if (enabled) context.trace(RubyEvent.CALL, name, getImplementationClass(), file, line);
+    }
+
+    protected final void returnTraceCompiled(ThreadContext context, boolean enabled, String name) {
+        if (enabled) context.trace(RubyEvent.RETURN, name, getImplementationClass());
     }
     
     public void setArity(Arity arity) {
         this.arity = arity;
         this.arityValue = arity.getValue();
     }
 
     @Override
     public Arity getArity() {
         return arity;
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
 
     protected static IRubyObject raiseArgumentError(JavaMethod method, ThreadContext context, String name, int given, int min, int max) {
         try {
             method.preBacktraceOnly(context, name);
             Arity.raiseArgumentError(context.getRuntime(), given, min, max);
         } finally {
             postBacktraceOnly(context);
         }
         // never reached
         return context.getRuntime().getNil();
     }
 
     protected static void checkArgumentCount(JavaMethod method, ThreadContext context, String name, IRubyObject[] args, int num) {
         if (args.length != num) raiseArgumentError(method, context, name, args.length, num, num);
     }
 
     // promise to implement N with block
     public static abstract class JavaMethodNBlock extends JavaMethod {
         public JavaMethodNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
     }
 
 
     // promise to implement zero to N with block
     public static abstract class JavaMethodZeroOrNBlock extends JavaMethodNBlock {
         public JavaMethodZeroOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             return call(context, self, clazz, name, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block);
     }
 
     public static abstract class JavaMethodZeroOrOneOrNBlock extends JavaMethodZeroOrNBlock {
         public JavaMethodZeroOrOneOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             return call(context, self, clazz, name, arg0, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrNBlock extends JavaMethodZeroOrOneOrNBlock {
         public JavaMethodZeroOrOneOrTwoOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             return call(context, self, clazz, name, arg0, arg1, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrThreeOrNBlock extends JavaMethodZeroOrOneOrTwoOrNBlock {
         public JavaMethodZeroOrOneOrTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return call(context, self, clazz, name, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block);
     }
 
 
     // promise to implement one to N with block
     public static abstract class JavaMethodOneOrNBlock extends JavaMethodNBlock {
         public JavaMethodOneOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             return call(context, self, clazz, name, arg0, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block);
     }
 
     public static abstract class JavaMethodOneOrTwoOrNBlock extends JavaMethodOneOrNBlock {
         public JavaMethodOneOrTwoOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             return call(context, self, clazz, name, arg0, arg1, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
     }
 
     public static abstract class JavaMethodOneOrTwoOrThreeOrNBlock extends JavaMethodOneOrTwoOrNBlock {
         public JavaMethodOneOrTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return call(context, self, clazz, name, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block);
     }
 
 
     // promise to implement two to N with block
     public static abstract class JavaMethodTwoOrNBlock extends JavaMethodNBlock {
         public JavaMethodTwoOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             return call(context, self, clazz, name, arg0, arg1, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block);
     }
 
     public static abstract class JavaMethodTwoOrThreeOrNBlock extends JavaMethodTwoOrNBlock {
         public JavaMethodTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrThreeOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return call(context, self, clazz, name, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block);
     }
 
 
     // promise to implement three to N with block
     public static abstract class JavaMethodThreeOrNBlock extends JavaMethodNBlock {
         public JavaMethodThreeOrNBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThreeOrNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return call(context, self, clazz, name, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block);
     }
 
 
     // promise to implement zero to three with block
     public static abstract class JavaMethodZeroBlock extends JavaMethodZeroOrNBlock {
         public JavaMethodZeroBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 0) return raiseArgumentError(this, context, name, args.length, 0, 0);
             return call(context, self, clazz, name, block);
         }
     }
 
     public static abstract class JavaMethodZeroOrOneBlock extends JavaMethodZeroOrOneOrNBlock {
         public JavaMethodZeroOrOneBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name, block);
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             default:
                 return raiseArgumentError(this, context, name, args.length, 0, 1);
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoBlock extends JavaMethodZeroOrOneOrTwoOrNBlock {
         public JavaMethodZeroOrOneOrTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name, block);
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             default:
                 return raiseArgumentError(this, context, name, args.length, 0, 2);
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrThreeBlock extends JavaMethodZeroOrOneOrTwoOrThreeOrNBlock {
         public JavaMethodZeroOrOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
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
                 return raiseArgumentError(this, context, name, args.length, 0, 3);
             }
         }
     }
 
     // promise to implement one to three with block
     public static abstract class JavaMethodOneBlock extends JavaMethodOneOrNBlock {
         public JavaMethodOneBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 1) return raiseArgumentError(this, context, name, args.length, 1, 1);
             return call(context, self, clazz, name, args[0], block);
         }
 
         @Override
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     public static abstract class JavaMethodOneOrTwoBlock extends JavaMethodOneOrTwoOrNBlock {
         public JavaMethodOneOrTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0], block);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             default:
                 return raiseArgumentError(this, context, name, args.length, 1, 2);
             }
         }
     }
 
     public static abstract class JavaMethodOneOrTwoOrThreeBlock extends JavaMethodOneOrTwoOrThreeOrNBlock {
         public JavaMethodOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 0: throw context.getRuntime().newArgumentError(0, 1);
             case 1: return call(context, self, clazz, name, args[0], block);
             case 2: return call(context, self, clazz, name, args[0], args[1], block);
             case 3: return call(context, self, clazz, name, args[0], args[1], args[2], block);
             default: return raiseArgumentError(this, context, name, args.length, 3, 3);
             }
         }
     }
 
 
     // promise to implement two to three with block
     public static abstract class JavaMethodTwoBlock extends JavaMethodTwoOrNBlock {
         public JavaMethodTwoBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 2) return raiseArgumentError(this, context, name, args.length, 2, 2);
             return call(context, self, clazz, name, args[0], args[1], block);
         }
     }
 
     public static abstract class JavaMethodTwoOrThreeBlock extends JavaMethodTwoOrThreeOrNBlock {
         public JavaMethodTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             switch (args.length) {
             case 2:
                 return call(context, self, clazz, name, args[0], args[1], block);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2], block);
             default:
                 return raiseArgumentError(this, context, name, args.length, 2, 3);
             }
         }
     }
 
 
     // promise to implement three with block
     public static abstract class JavaMethodThreeBlock extends JavaMethodThreeOrNBlock {
         public JavaMethodThreeBlock(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThreeBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             if (args.length != 3) return raiseArgumentError(this, context, name, args.length, 3, 3);
             return call(context, self, clazz, name, args[0], args[1], args[2], block);
         }
     }
 
     // promise to implement N
     public static abstract class JavaMethodN extends JavaMethodNBlock {
         public JavaMethodN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args);
 
         // Normally we could leave these to fall back on the superclass, but
         // since it dispatches through the [] version below, which may
         // dispatch through the []+block version, we can save it a couple hops
         // by overriding these here.
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             return call(context, self, clazz, name, IRubyObject.NULL_ARRAY);
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             return call(context, self, clazz, name, new IRubyObject[] {arg0});
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             return call(context, self, clazz, name, new IRubyObject[] {arg0, arg1});
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return call(context, self, clazz, name, new IRubyObject[] {arg0, arg1, arg2});
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return call(context, self, clazz, name, args);
         }
     }
 
 
     // promise to implement zero to N
     public static abstract class JavaMethodZeroOrN extends JavaMethodN {
         public JavaMethodZeroOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             return call(context, self, clazz, name);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name);
     }
 
     public static abstract class JavaMethodZeroOrOneOrN extends JavaMethodZeroOrN {
         public JavaMethodZeroOrOneOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             return call(context, self, clazz, name, arg0);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg);
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrN extends JavaMethodZeroOrOneOrN {
         public JavaMethodZeroOrOneOrTwoOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             return call(context, self, clazz, name, arg0, arg1);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1);
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrThreeOrN extends JavaMethodZeroOrOneOrTwoOrN {
         public JavaMethodZeroOrOneOrTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return call(context, self, clazz, name, arg0, arg1, arg2);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2);
     }
 
 
     // promise to implement one to N
     public static abstract class JavaMethodOneOrN extends JavaMethodN {
         public JavaMethodOneOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
         
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             return call(context, self, clazz, name, arg0);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0);
     }
 
     public static abstract class JavaMethodOneOrTwoOrN extends JavaMethodOneOrN {
         public JavaMethodOneOrTwoOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             return call(context, self, clazz, name, arg0, arg1);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1);
     }
 
     public static abstract class JavaMethodOneOrTwoOrThreeOrN extends JavaMethodOneOrTwoOrN {
         public JavaMethodOneOrTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return call(context, self, clazz, name, arg0, arg1, arg2);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2);
     }
 
 
     // promise to implement two to N
     public static abstract class JavaMethodTwoOrN extends JavaMethodN {
         public JavaMethodTwoOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             return call(context, self, clazz, name, arg0, arg1);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1);
     }
 
     public static abstract class JavaMethodTwoOrThreeOrN extends JavaMethodTwoOrN {
         public JavaMethodTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrThreeOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return call(context, self, clazz, name, arg0, arg1, arg2);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2);
     }
 
 
     // promise to implement three to N
     public static abstract class JavaMethodThreeOrN extends JavaMethodN {
         public JavaMethodThreeOrN(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThreeOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return call(context, self, clazz, name, arg0, arg1, arg2);
         }
 
         @Override
         public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2);
     }
 
 
     // promise to implement zero to three
     public static abstract class JavaMethodZero extends JavaMethodZeroOrN {
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             if (args.length != 0) return raiseArgumentError(this, context, name, args.length, 0, 0);
             return call(context, self, clazz, name);
         }
         @Override
         public Arity getArity() {
             return Arity.NO_ARGUMENTS;
         }
     }
 
     public static abstract class JavaMethodZeroOrOne extends JavaMethodZeroOrOneOrN {
         public JavaMethodZeroOrOne(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOne(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name);
             case 1:
                 return call(context, self, clazz, name, args[0]);
             default:
                 return raiseArgumentError(this, context, name, args.length, 0, 1);
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwo extends JavaMethodZeroOrOneOrTwoOrN {
         public JavaMethodZeroOrOneOrTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             switch (args.length) {
             case 0:
                 return call(context, self, clazz, name);
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             default:
                 return raiseArgumentError(this, context, name, args.length, 0, 2);
             }
         }
     }
 
     public static abstract class JavaMethodZeroOrOneOrTwoOrThree extends JavaMethodZeroOrOneOrTwoOrThreeOrN {
         public JavaMethodZeroOrOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodZeroOrOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
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
                 return raiseArgumentError(this, context, name, args.length, 0, 3);
             }
         }
     }
 
 
     // promise to implement one to three
     public static abstract class JavaMethodOne extends JavaMethodOneOrN {
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             if (args.length != 1) return raiseArgumentError(this, context, name, args.length, 1, 1);
             return call(context, self, clazz, name, args[0]);
         }
 
         @Override
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     public static abstract class JavaMethodOneOrTwo extends JavaMethodOneOrTwoOrN {
         public JavaMethodOneOrTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             default:
                 return raiseArgumentError(this, context, name, args.length, 1, 2);
             }
         }
     }
 
     public static abstract class JavaMethodOneOrTwoOrThree extends JavaMethodOneOrTwoOrThreeOrN {
         public JavaMethodOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodOneOrTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             switch (args.length) {
             case 1:
                 return call(context, self, clazz, name, args[0]);
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2]);
             default:
                 return raiseArgumentError(this, context, name, args.length, 1, 3);
             }
         }
     }
 
 
     // promise to implement two to three
     public static abstract class JavaMethodTwo extends JavaMethodTwoOrN {
         public JavaMethodTwo(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwo(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             if (args.length != 2) return raiseArgumentError(this, context, name, args.length, 2, 2);
             return call(context, self, clazz, name, args[0], args[1]);
         }
 
         @Override
         public Arity getArity() {
             return Arity.TWO_ARGUMENTS;
         }
     }
 
     public static abstract class JavaMethodTwoOrThree extends JavaMethodTwoOrThreeOrN {
         public JavaMethodTwoOrThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodTwoOrThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             switch (args.length) {
             case 2:
                 return call(context, self, clazz, name, args[0], args[1]);
             case 3:
                 return call(context, self, clazz, name, args[0], args[1], args[2]);
             default:
                 return raiseArgumentError(this, context, name, args.length, 2, 3);
             }
         }
     }
 
 
     // promise to implement three
     public static abstract class JavaMethodThree extends JavaMethodThreeOrN {
         public JavaMethodThree(RubyModule implementationClass, Visibility visibility) {
             super(implementationClass, visibility);
         }
         public JavaMethodThree(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
             super(implementationClass, visibility, callConfig);
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             if (args.length != 3) return raiseArgumentError(this, context, name, args.length, 3, 3);
             return call(context, self, clazz, name, args[0], args[1], args[2]);
         }
 
         @Override
         public Arity getArity() {
             return Arity.THREE_ARGUMENTS;
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/ReflectedCompiledMethod.java b/src/org/jruby/internal/runtime/methods/ReflectedCompiledMethod.java
index e32f1a2485..1105a87fac 100644
--- a/src/org/jruby/internal/runtime/methods/ReflectedCompiledMethod.java
+++ b/src/org/jruby/internal/runtime/methods/ReflectedCompiledMethod.java
@@ -1,100 +1,102 @@
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
  * Copyright (c) 2007 Peter Brant <peter.brant@gmail.com>
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
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
+import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ReflectedCompiledMethod extends CompiledMethod {
     private final Method method;
+    private final ISourcePosition position;
     
     public ReflectedCompiledMethod(RubyModule implementationClass, Arity arity,
-            Visibility visibility, StaticScope staticScope, Object scriptObject, Method method, CallConfiguration callConfig) {
+            Visibility visibility, StaticScope staticScope, Object scriptObject, Method method, CallConfiguration callConfig, ISourcePosition position) {
         super();
         init(implementationClass, arity, visibility, staticScope, scriptObject, callConfig);
         
         this.method = method;
+        this.position = position;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name,
             IRubyObject[] args, Block block) {
         callConfig.pre(context, self, getImplementationClass(), name, block, staticScope, this);
         
         Ruby runtime = context.getRuntime();
         try {
             boolean isTrace = runtime.hasEventHooks();
             try {
                 if (isTrace) {
-                    // XXX Wrong, but will have to do for now
-                    runtime.callEventHooks(context, RubyEvent.CALL, context.getFile(), context.getLine(), name, getImplementationClass());
+                    runtime.callEventHooks(context, RubyEvent.CALL, position.getFile(), position.getStartLine(), name, getImplementationClass());
                 }
                 return (IRubyObject)method.invoke(null, $scriptObject, context, self, args, block);
             } finally {
                 if (isTrace) {
                     Frame frame = context.getPreviousFrame();
 
                     runtime.callEventHooks(context, RubyEvent.RETURN, frame.getFile(), frame.getLine(), name, getImplementationClass());
                 }
             }
             
         } catch (IllegalArgumentException e) {
             throw RaiseException.createNativeRaiseException(runtime, e, method);
         } catch (IllegalAccessException e) {
             throw RaiseException.createNativeRaiseException(runtime, e, method);
         } catch (InvocationTargetException e) {
             Throwable cause = e.getCause();
             if (cause instanceof JumpException.ReturnJump) {
                 return handleReturn(context, (JumpException.ReturnJump)cause);
             } else if (cause instanceof JumpException.RedoJump) {
                 return handleRedo(runtime);
             } else if (cause instanceof RuntimeException) {
                 throw (RuntimeException)cause;
             } else if (cause instanceof Error) {
                 throw (Error)cause;                
             } else {
                 throw RaiseException.createNativeRaiseException(runtime, cause, method);
             }
         } finally {
             callConfig.post(context);
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java b/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
index 5b627215a0..99db489469 100644
--- a/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
@@ -1,202 +1,203 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (c) 2007 Peter Brant <peter.brant@gmail.com>
  * Copyright (C) 2008 The JRuby Community <www.headius.com>
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
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.compiler.impl.StandardASMCompiler;
+import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This MethodFactory uses reflection to provide method handles. Reflection is
  * typically slower than code-generated handles, but it does provide a simple
  * mechanism for binding in environments where code-generation isn't supported.
  * 
  * @see org.jruby.internal.runtime.methods.MethodFactory
  */
 public class ReflectionMethodFactory extends MethodFactory {
     /**
      * Use reflection to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethodLazily(RubyModule implementationClass,
             String methodName, Arity arity, Visibility visibility, 
-            StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
-        return getCompiledMethod(implementationClass, methodName, arity, visibility, scope, scriptObject, callConfig);
+            StaticScope scope, Object scriptObject, CallConfiguration callConfig, ISourcePosition position) {
+        return getCompiledMethod(implementationClass, methodName, arity, visibility, scope, scriptObject, callConfig, position);
     }
     
     /**
      * Use reflection to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(RubyModule implementationClass,
             String methodName, Arity arity, Visibility visibility, 
-            StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
+            StaticScope scope, Object scriptObject, CallConfiguration callConfig, ISourcePosition position) {
         try {
             Class scriptClass = scriptObject.getClass();
             Method method = scriptClass.getMethod(methodName, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
-            return new ReflectedCompiledMethod(implementationClass, arity, visibility, scope, scriptObject, method, callConfig);
+            return new ReflectedCompiledMethod(implementationClass, arity, visibility, scope, scriptObject, method, callConfig, position);
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException("No method with name " + methodName + " found in " + scriptObject.getClass());
         }
     }
     
     /**
      * Use reflection to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc) {
         try {
             if (!Modifier.isPublic(desc.getDeclaringClass().getModifiers())) {
                 System.err.println("warning: binding non-public class" + desc.declaringClassName + "; reflected handles won't work");
             }
 
             Method method = desc.getDeclaringClass().getDeclaredMethod(desc.name, desc.getParameterClasses());
             JavaMethod ic = new ReflectedJavaMethod(implementationClass, method, desc.anno);
 
             ic.setJavaName(method.getName());
             ic.setSingleton(Modifier.isStatic(method.getModifiers()));
             ic.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
             return ic;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
     
     /**
      * Use reflection to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> descs) {
         try {
             if (!Modifier.isPublic(descs.get(0).getDeclaringClass().getModifiers())) {
                 System.err.println("warning: binding non-public class" + descs.get(0).declaringClassName + "; reflected handles won't work");
             }
             
             List<Method> methods = new ArrayList();
             List<JRubyMethod> annotations = new ArrayList();
             
             for (JavaMethodDescriptor desc: descs) {
                 methods.add(desc.getDeclaringClass().getDeclaredMethod(desc.name, desc.getParameterClasses()));
                 annotations.add(desc.anno);
             }
             Method method0 = methods.get(0);
             JRubyMethod anno0 = annotations.get(0);
             
             JavaMethod ic = new ReflectedJavaMultiMethod(implementationClass, methods, annotations);
 
             ic.setJavaName(method0.getName());
             ic.setSingleton(Modifier.isStatic(method0.getModifiers()));
             ic.setCallConfig(CallConfiguration.getCallConfigByAnno(anno0));
             return ic;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
 
     public CompiledBlockCallback getBlockCallback(String method, final Object scriptObject) {
         try {
             Class scriptClass = scriptObject.getClass();
             final Method blockMethod = scriptClass.getMethod(method, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject.class);
             return new CompiledBlockCallback() {
                 public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject args) {
                     try {
                         return (IRubyObject)blockMethod.invoke(null, scriptObject, context, self, args);
                     } catch (IllegalAccessException ex) {
                         throw new RuntimeException(ex);
                     } catch (IllegalArgumentException ex) {
                         throw new RuntimeException(ex);
                     } catch (InvocationTargetException ex) {
                         Throwable cause = ex.getCause();
                         if (cause instanceof RuntimeException) {
                             throw (RuntimeException) cause;
                         } else if (cause instanceof Error) {
                             throw (Error) cause;
                         } else {
                             throw new RuntimeException(ex);
                         }
                     }
                 }
             };
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     public CompiledBlockCallback19 getBlockCallback19(String method, final Object scriptObject) {
         try {
             Class scriptClass = scriptObject.getClass();
             final Method blockMethod = scriptClass.getMethod(method, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
             return new CompiledBlockCallback19() {
                 public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
                     try {
                         return (IRubyObject)blockMethod.invoke(null, scriptObject, context, self, args, block);
                     } catch (IllegalAccessException ex) {
                         throw new RuntimeException(ex);
                     } catch (IllegalArgumentException ex) {
                         throw new RuntimeException(ex);
                     } catch (InvocationTargetException ex) {
                         Throwable cause = ex.getCause();
                         if (cause instanceof RuntimeException) {
                             throw (RuntimeException) cause;
                         } else if (cause instanceof Error) {
                             throw (Error) cause;
                         } else {
                             throw new RuntimeException(ex);
                         }
                     }
                 }
             };
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/TraceableInterpretedMethod.java b/src/org/jruby/internal/runtime/methods/TraceableInterpretedMethod.java
index 94b5dd4566..af625d9f1f 100644
--- a/src/org/jruby/internal/runtime/methods/TraceableInterpretedMethod.java
+++ b/src/org/jruby/internal/runtime/methods/TraceableInterpretedMethod.java
@@ -1,90 +1,90 @@
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
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  */
 public class TraceableInterpretedMethod extends InterpretedMethod {
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private ISourcePosition position;
     private String name;
 
-    public TraceableInterpretedMethod(RubyModule implementationClass, String name, StaticScope staticScope, Node body,
+    public TraceableInterpretedMethod(RubyModule implementationClass, StaticScope staticScope, Node body,
             ArgsNode argsNode, Visibility visibility, ISourcePosition position) {
         super(implementationClass, staticScope, body, argsNode,
             visibility, position);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
         this.position = position;
 		
         assert argsNode != null;
     }
 
     @Override
     protected void pre(ThreadContext context, String name, IRubyObject self, Block block, Ruby runtime) {
         context.preMethodFrameAndScope(getImplementationClass(), name, self, block, staticScope);
 
         if (runtime.hasEventHooks()) traceCall(context, runtime, name);
     }
 
     @Override
     protected void post(Ruby runtime, ThreadContext context, String name) {
         if (runtime.hasEventHooks()) traceReturn(context, runtime, name);
 
         context.postMethodFrameAndScope();
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, String name) {
         runtime.callEventHooks(context, RubyEvent.RETURN, context.getFile(), context.getLine(), name, getImplementationClass());
     }
     
     private void traceCall(ThreadContext context, Ruby runtime, String name) {
         runtime.callEventHooks(context, RubyEvent.CALL, position.getFile(), position.getStartLine(), name, getImplementationClass());
     }
 
     @Override
     public DynamicMethod dup() {
-        return new TraceableInterpretedMethod(getImplementationClass(), name, staticScope, body, argsNode, getVisibility(), position);
+        return new TraceableInterpretedMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), position);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/TraceableJittedMethod.java b/src/org/jruby/internal/runtime/methods/TraceableJittedMethod.java
new file mode 100644
index 0000000000..ca18eaed74
--- /dev/null
+++ b/src/org/jruby/internal/runtime/methods/TraceableJittedMethod.java
@@ -0,0 +1,287 @@
+/***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Copyright (C) 2009 Charles Oliver Nutter <headius@headius.com>
+ *
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+package org.jruby.internal.runtime.methods;
+
+import org.jruby.Ruby;
+import org.jruby.RubyModule;
+import org.jruby.ast.executable.Script;
+import org.jruby.exceptions.JumpException;
+import org.jruby.internal.runtime.JumpTarget;
+import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.parser.StaticScope;
+import org.jruby.runtime.Arity;
+import org.jruby.runtime.Block;
+import org.jruby.runtime.RubyEvent;
+import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.Visibility;
+import org.jruby.runtime.builtin.IRubyObject;
+
+/**
+ * This is the mixed-mode method type.  It will call out to JIT compiler to see if the compiler
+ * wants to JIT or not.  If the JIT compiler does JIT this it will return the new method
+ * to be executed here instead of executing the interpreted version of this method.  The next
+ * invocation of the method will end up causing the runtime to load and execute the newly JIT'd
+ * method.
+ *
+ */
+public class TraceableJittedMethod extends DynamicMethod implements JumpTarget {
+    private final StaticScope staticScope;
+    private final Script jitCompiledScript;
+    private final ISourcePosition position;
+    private final Arity arity;
+    private final DefaultMethod realMethod;
+    
+    public TraceableJittedMethod(RubyModule implementationClass, StaticScope staticScope, Script jitCompiledScript,
+            CallConfiguration jitCallConfig, Visibility visibility, Arity arity, ISourcePosition position,
+            DefaultMethod realMethod) {
+        super(implementationClass, visibility, jitCallConfig);
+        this.position = position;
+        this.jitCompiledScript = jitCompiledScript;
+        this.staticScope = staticScope;
+        this.arity = arity;
+        this.realMethod = realMethod;
+    }
+
+    public StaticScope getStaticScope() {
+        return staticScope;
+    }
+
+    @Override
+    public DynamicMethod getRealMethod() {
+        return realMethod;
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
+        Ruby runtime = context.getRuntime();
+        
+        try {
+            pre(context, self, name, block, args.length);
+
+            return jitCompiledScript.__file__(context, self, args, block);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, Block.NULL_BLOCK, args.length);
+
+            return jitCompiledScript.__file__(context, self, args, Block.NULL_BLOCK);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime,context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, Block.NULL_BLOCK, 0);
+
+            return jitCompiledScript.__file__(context, self, Block.NULL_BLOCK);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, block, 0);
+
+            return jitCompiledScript.__file__(context, self, block);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, Block.NULL_BLOCK, 1);
+
+            return jitCompiledScript.__file__(context, self, arg0, Block.NULL_BLOCK);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, block, 1);
+
+            return jitCompiledScript.__file__(context, self, arg0, block);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, Block.NULL_BLOCK, 2);
+
+            return jitCompiledScript.__file__(context, self, arg0, arg1, Block.NULL_BLOCK);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, block, 2);
+
+            return jitCompiledScript.__file__(context, self, arg0, arg1, block);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, Block.NULL_BLOCK, 3);
+
+            return jitCompiledScript.__file__(context, self, arg0, arg1, arg2, Block.NULL_BLOCK);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        Ruby runtime = context.getRuntime();
+
+        try {
+            pre(context, self, name, block, 3);
+
+            return jitCompiledScript.__file__(context, self, arg0, arg1, arg2, block);
+        } catch (JumpException.ReturnJump rj) {
+            return handleReturn(context, rj);
+        } catch (JumpException.RedoJump rj) {
+            return handleRedo(runtime);
+        } finally {
+            post(runtime, context, name);
+        }
+    }
+
+    protected void pre(ThreadContext context, IRubyObject self, String name, Block block, int argsLength) {
+        Ruby runtime = context.getRuntime();
+
+        callConfig.pre(context, self, getImplementationClass(), name, block, staticScope, this);
+
+        getArity().checkArity(runtime, argsLength);
+        
+        if (runtime.hasEventHooks()) traceCall(context, runtime, name);
+    }
+
+    protected void post(Ruby runtime, ThreadContext context, String name) {
+        if (runtime.hasEventHooks()) traceReturn(context, runtime, name);
+        
+        callConfig.post(context);
+    }
+
+    private void traceReturn(ThreadContext context, Ruby runtime, String name) {
+        runtime.callEventHooks(context, RubyEvent.RETURN, context.getFile(), context.getLine(), name, getImplementationClass());
+    }
+
+    private void traceCall(ThreadContext context, Ruby runtime, String name) {
+        runtime.callEventHooks(context, RubyEvent.CALL, position.getFile(), position.getStartLine(), name, getImplementationClass());
+    }
+
+    public ISourcePosition getPosition() {
+        return position;
+    }
+
+    @Override
+    public Arity getArity() {
+        return arity;
+    }
+
+    public DynamicMethod dup() {
+        return new TraceableJittedMethod(getImplementationClass(), staticScope, jitCompiledScript, callConfig, getVisibility(), arity, position, realMethod);
+    }
+
+
+}
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 553162b67a..f5be4fcc0b 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,1651 +1,1675 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaUtil;
+import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.lexer.yacc.SimpleSourcePosition;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlock19;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledBlockLight19;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Interpreted19Block;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.MethodFactory;
+import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.TypeConverter;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static CallSite selectAttrAsgnCallSite(IRubyObject receiver, IRubyObject self, CallSite normalSite, CallSite variableSite) {
         if (receiver == self) return variableSite;
         return normalSite;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, IRubyObject value, ThreadContext context, IRubyObject caller) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         callSite.call(context, caller, receiver, newArgs);
         return value;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             IRubyObject receiver = receivers[i];
             if (invokeEqqForCaseWhen(callSite, context, caller, arg, receiver)) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver) {
         IRubyObject result = callSite.call(context, caller, receiver, arg);
         if (result.isTrue()) return true;
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1);
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1, receiver2);
     }
 
     public static boolean areAnyTrueForCaselessWhen(IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             if (receivers[i].isTrue()) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver) {
         return receiver.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1) {
         return receiver0.isTrue() || receiver1.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         return receiver0.isTrue() || receiver1.isTrue() || receiver2.isTrue();
     }
     
     public static CompiledBlockCallback createBlockCallback(Ruby runtime, Object scriptObject, String closureMethod) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
         
         return factory.getBlockCallback(closureMethod, scriptObject);
     }
 
     public static CompiledBlockCallback19 createBlockCallback19(Ruby runtime, Object scriptObject, String closureMethod) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
 
         return factory.getBlockCallback19(closureMethod, scriptObject);
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String closureMethod, int arity,
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope =
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
 
         if (light) {
             return CompiledBlockLight19.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock19.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(context.getRuntime(), scriptObject, closureMethod),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
     
     public static Block createBlock(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock.newCompiledClosure(
                 context,
                 self,
                 body);
     }
 
     public static Block createBlock19(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock19.newCompiledClosure(
                 context,
                 self,
                 body);
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, BlockBody.ZERO_ARGS);
         
         try {
             block.yield(context, null);
         } finally {
             context.postScopedBody();
         }
         
         return context.getRuntime().getNil();
     }
     
     public static Block createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return CompiledSharedScopeBlock.newCompiledSharedScopeClosure(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
-            int arity, int required, int optional, int rest, CallConfiguration callConfig) {
+            int arity, int required, int optional, int rest, String filename, int line, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
-        DynamicMethod method = constructNormalMethod(name, visibility, factory, containingClass, javaName, arity, scope, scriptObject, callConfig);
+        DynamicMethod method = constructNormalMethod(
+                factory, javaName,
+                name, containingClass, new SimpleSourcePosition(filename, line), arity, scope, visibility, scriptObject,
+                callConfig);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
-            int arity, int required, int optional, int rest, CallConfiguration callConfig) {
+            int arity, int required, int optional, int rest, String filename, int line, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         StaticScope scope = creatScopeForClass(context, scopeNames, required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
-        DynamicMethod method = constructSingletonMethod(factory, rubyClass, javaName, arity, scope,scriptObject, callConfig);
+        DynamicMethod method = constructSingletonMethod( factory, javaName, rubyClass, new SimpleSourcePosition(filename, line), arity, scope,scriptObject, callConfig);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             return receiver.getSingletonClass();
         }
     }
 
     // TODO: Only used by interface implementation; eliminate it
     public static IRubyObject invokeMethodMissing(IRubyObject receiver, String name, IRubyObject[] args) {
         ThreadContext context = receiver.getRuntime().getCurrentContext();
 
         // store call information so method_missing impl can use it
         context.setLastCallStatusAndVisibility(CallType.FUNCTIONAL, Visibility.PUBLIC);
 
         if (name == "method_missing") {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject[] args, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, args, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, arg2, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, block);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = receiver.getMetaClass().searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.getRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing == runtime.getDefaultMethodMissing()) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing);
     }
 
     private static class MethodMissingMethod extends DynamicMethod {
         private DynamicMethod delegate;
 
         public MethodMissingMethod(DynamicMethod delegate) {
             this.delegate = delegate;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return this.delegate.call(context, self, clazz, "method_missing", prepareMethodMissingArgs(args, context, name), block);
         }
 
         @Override
         public DynamicMethod dup() {
             return this;
         }
     }
 
     private static DynamicMethod selectInternalMM(Ruby runtime, Visibility visibility, CallType callType) {
         if (visibility == Visibility.PRIVATE) {
             return runtime.getPrivateMethodMissing();
         } else if (visibility == Visibility.PROTECTED) {
             return runtime.getProtectedMethodMissing();
         } else if (callType == CallType.VARIABLE) {
             return runtime.getVariableMethodMissing();
         } else if (callType == CallType.SUPER) {
             return runtime.getSuperMethodMissing();
         } else {
             return runtime.getNormalMethodMissing();
         }
     }
 
     private static IRubyObject[] prepareMethodMissingArgs(IRubyObject[] args, ThreadContext context, String name) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return newArgs;
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, Block block) {
         return self.getMetaClass().finvoke(context, self, name, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return self.getMetaClass().finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvoke(context, self, name);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0) {
         return self.getMetaClass().finvoke(context, self, name, arg0);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args) {
         return self.getMetaClass().finvoke(context, self, name, args);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, CallType callType) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, callType, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, arg, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return asClass.finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, Block block) {
         return asClass.finvoke(context, self, name, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return asClass.finvoke(context, self, name, arg0, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, arg2, block);
     }
 
     /**
      * The protocol for super method invocation is a bit complicated
      * in Ruby. In real terms it involves first finding the real
      * implementation class (the super class), getting the name of the
      * method to call from the frame, and then invoke that on the
      * super class with the current self as the actual object
      * invoking.
      */
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
         
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, args, block);
         }
         return method.call(context, self, superClass, name, args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, block);
         }
         return method.call(context, self, superClass, name, arg0, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, arg2, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, arg2, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         return ensureRubyArray(value.getRuntime(), value);
     }
 
     public static RubyArray ensureRubyArray(Ruby runtime, IRubyObject value) {
         return value instanceof RubyArray ? (RubyArray)value : RubyArray.newArray(runtime, value);
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(IRubyObject value, Ruby runtime, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.fastGetClassVar(internedName);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.getRuntime();
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(runtime, internedName, runtime.getObject());
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
 
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
 
         if (rubyModule instanceof RubyModule) {
             return (RubyModule)rubyModule;
         } else {
             throw context.getRuntime().newTypeError(rubyModule + " is not a class/module");
         }
     }
     
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     /**
      * If it's Redo, Next, or Break, rethrow it as a normal exception for while to handle
      * @param re
      * @param runtime
      */
     public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
         RubyException exception = re.getException();
         if (context.getRuntime().getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             switch (jumpError.getReason()) {
             case REDO:
                 return JumpException.REDO_JUMP;
             case NEXT:
                 return new JumpException.NextJump(jumpError.exit_value());
             case BREAK:
                 return new JumpException.BreakJump(context.getFrameJumpTarget(), jumpError.exit_value());
             }
         }
         return re;
     }
     
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asJavaString();
         }
 
         throw re;
     }
     
     public static IRubyObject unwrapLocalJumpErrorValue(RaiseException re) {
         return ((RubyLocalJumpError)re.getException()).exit_value();
     }
     
     public static IRubyObject processBlockArgument(Ruby runtime, Block block) {
         if (!block.isGiven()) {
             return runtime.getNil();
         }
         
         return processGivenBlock(block, runtime);
     }
 
     private static IRubyObject processGivenBlock(Block block, Ruby runtime) {
         RubyProc blockArg = block.getProcObject();
 
         if (blockArg == null) {
             blockArg = runtime.newBlockPassProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
 
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(Ruby runtime, IRubyObject proc, Block currentBlock) {
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = coerceProc(proc, runtime);
         }
 
         return getBlockFromProc(currentBlock, proc);
     }
 
     private static IRubyObject coerceProc(IRubyObject proc, Ruby runtime) throws RaiseException {
         proc = TypeConverter.convertToType(proc, runtime.getProc(), "to_proc", false);
 
         if (!(proc instanceof RubyProc)) {
             throw runtime.newTypeError("wrong argument type " + proc.getMetaClass().getName() + " (expected Proc)");
         }
         return proc;
     }
 
     private static Block getBlockFromProc(Block currentBlock, IRubyObject proc) {
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) {
                 return currentBlock;
             }
         }
 
         return ((RubyProc) proc).getBlock();       
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         return getBlockFromBlockPassBody(proc.getRuntime(), proc, currentBlock);
 
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.getRuntime());
         
         return RubyRegexp.match_last(backref);
     }
 
     public static IRubyObject[] getArgValues(ThreadContext context) {
         return context.getCurrentScope().getArgValues();
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return RuntimeHelpers.invokeSuper(context, self, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static JumpException.ReturnJump returnJump(IRubyObject result, ThreadContext context) {
         return context.returnJump(result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, ThreadContext context) {
         // JRUBY-530, while case
         if (bj.getTarget() == context.getFrameJumpTarget()) {
             return (IRubyObject) bj.getValue();
         }
 
         throw bj;
     }
     
     public static IRubyObject breakJump(ThreadContext context, IRubyObject value) {
         throw new JumpException.BreakJump(context.getFrameJumpTarget(), value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, ThreadContext context) {
         for (int i = 0; i < exceptions.length; i++) {
             IRubyObject result = isExceptionHandled(currentException, exceptions[i], context);
             if (result.isTrue()) return result;
         }
         return context.getRuntime().getFalse();
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception, ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (!runtime.getModule().isInstance(exception)) {
             throw runtime.newTypeError("class or module required for rescue clause");
         }
         IRubyObject result = invoke(context, exception, "===", currentException);
         if (result.isTrue()) return result;
         return runtime.getFalse();
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, context);
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, IRubyObject exception2, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, exception2, context);
     }
 
     private static boolean checkJavaException(Throwable currentThrowable, IRubyObject throwable) {
         if (throwable instanceof RubyClass) {
             RubyClass rubyClass = (RubyClass)throwable;
             JavaClass javaClass = (JavaClass)rubyClass.fastGetInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(currentThrowable)) {
                     return true;
                 }
             }
         }
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject[] throwables, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwables, context);
         } else {
             for (int i = 0; i < throwables.length; i++) {
                 if (checkJavaException(currentThrowable, throwables[0])) {
                     return context.getRuntime().getTrue();
                 }
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable, context);
         } else {
             if (checkJavaException(currentThrowable, throwable)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, IRubyObject throwable2, ThreadContext context) {
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, throwable2, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1)) {
                 return context.getRuntime().getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2)) {
                 return context.getRuntime().getTrue();
             }
 
             return context.getRuntime().getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.getRuntime(), currentThrowable);
         }
         context.setErrorInfo(exception);
     }
 
     public static void clearErrorInfo(ThreadContext context) {
         context.setErrorInfo(context.getRuntime().getNil());
     }
     
     public static void checkSuperDisabledOrOutOfMethod(ThreadContext context) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             if (name != null) {
                 throw context.getRuntime().newNameError("superclass method '" + name + "' disabled", name);
             } else {
                 throw context.getRuntime().newNoMethodError("super called outside of method", null, context.getRuntime().getNil());
             }
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
         if (implementationClass != null && implementationClass.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             return clazz.findImplementer(implementationClass);
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             return implementationClass;
         }
     }
     
     public static RubyArray createSubarray(RubyArray input, int start) {
         return (RubyArray)input.subseqLight(start, input.size() - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         if (start >= input.length) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start);
         }
     }
 
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start, int exclude) {
         int length = input.length - exclude - start;
         if (length <= 0) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start, length);
         }
     }
 
     public static IRubyObject elementOrNull(IRubyObject[] input, int element) {
         if (element >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject elementOrNil(IRubyObject[] input, int element, IRubyObject nil) {
         if (element >= input.length) {
             return nil;
         } else {
             return input[element];
         }
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, "===", expression)
                     .isTrue())
                     || (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
 
     public static IRubyObject setConstantInCurrent(IRubyObject value, ThreadContext context, String name) {
         return context.setConstantInCurrent(name, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.NEXT, value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 5;
     
     public static IRubyObject[] constructObjectArray(IRubyObject one) {
         return new IRubyObject[] {one};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two) {
         return new IRubyObject[] {one, two};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three) {
         return new IRubyObject[] {one, two, three};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return new IRubyObject[] {one, two, three, four};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return new IRubyObject[] {one, two, three, four, five};
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one) {
         return RubyArray.newArrayLight(runtime, one);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two) {
         return RubyArray.newArrayLight(runtime, one, two);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three) {
         return RubyArray.newArrayLight(runtime, one, two, three);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return RubyArray.newArrayLight(runtime, one, two, three, four);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five);
     }
     
     public static String[] constructStringArray(String one) {
         return new String[] {one};
     }
     
     public static String[] constructStringArray(String one, String two) {
         return new String[] {one, two};
     }
     
     public static String[] constructStringArray(String one, String two, String three) {
         return new String[] {one, two, three};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four) {
         return new String[] {one, two, three, four};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five) {
         return new String[] {one, two, three, four, five};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six) {
         return new String[] {one, two, three, four, five, six};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven) {
         return new String[] {one, two, three, four, five, six, seven};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight) {
         return new String[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine, String ten) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         hash.fastASetCheckString(runtime, key3, value3);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         hash.fastASetCheckString19(runtime, key3, value3);
         return hash;
     }
     
     public static IRubyObject defineAlias(ThreadContext context, String newName, String oldName) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(String value, Ruby runtime, IRubyObject nil) {
         if (value == null) return nil;
         return RubyString.newString(runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static IRubyObject setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         return context.getCurrentScope().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentScope().getLastLine(runtime);
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentScope().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, IRubyObject self, CallSite varSite) {
         return varSite.call(context, self, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, IRubyObject self, CallSite varAsgnSite) {
         varAsgnSite.call(context, self, receiver, value);
         return value;
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, arg);
         opAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, appendToObjectArray(args, result));
 
         return result;
     }
 
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, self, receiver, newArgs);
         return value;
     }
 
     public static RubyArray arrayValue(IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             Ruby runtime = value.getRuntime();
             
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
-    private static DynamicMethod constructNormalMethod(String name, Visibility visibility, MethodFactory factory, RubyModule containingClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
+    private static DynamicMethod constructNormalMethod( MethodFactory factory, String javaName, String name, RubyModule containingClass, ISourcePosition position, int arity, StaticScope scope, Visibility visibility, Object scriptObject, CallConfiguration callConfig) {
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
-            method = factory.getCompiledMethodLazily(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
+            method = factory.getCompiledMethodLazily(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig, position);
         } else {
-            method = factory.getCompiledMethod(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
+            method = factory.getCompiledMethod(containingClass, javaName, Arity.createArity(arity), visibility, scope, scriptObject, callConfig, position);
         }
 
         return method;
     }
 
-    private static DynamicMethod constructSingletonMethod(MethodFactory factory, RubyClass rubyClass, String javaName, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
+    private static DynamicMethod constructSingletonMethod(MethodFactory factory, String javaName, RubyClass rubyClass, ISourcePosition position, int arity, StaticScope scope, Object scriptObject, CallConfiguration callConfig) {
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
-            return factory.getCompiledMethodLazily(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
+            return factory.getCompiledMethodLazily(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig, position);
         } else {
-            return factory.getCompiledMethod(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
+            return factory.getCompiledMethod(rubyClass, javaName, Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig, position);
         }
     }
 
     private static StaticScope creatScopeForClass(ThreadContext context, String[] scopeNames, int required, int optional, int rest) {
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop", "Object#initialize");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem", name);
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, int index) {
         if (index < array.getLength()) {
             return array.eltInternal(index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilZero(RubyArray array) {
         if (0 < array.getLength()) {
             return array.eltInternal(0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilOne(RubyArray array) {
         if (1 < array.getLength()) {
             return array.eltInternal(1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilTwo(RubyArray array) {
         if (2 < array.getLength()) {
             return array.eltInternal(2);
         } else {
             return array.getRuntime().getNil();
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
     
     public static IRubyObject getGlobalVariable(Ruby runtime, String name) {
         return runtime.getGlobalVariables().get(name);
     }
     
     public static IRubyObject setGlobalVariable(IRubyObject value, Ruby runtime, String name) {
         return runtime.getGlobalVariables().set(name, value);
     }
 
     public static IRubyObject getInstanceVariable(IRubyObject self, Ruby runtime, String internedName) {
         IRubyObject result = self.getInstanceVariables().fastGetInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().fastSetInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.getRuntime(), block, Block.Type.LAMBDA);
     }
 
     public static void fillNil(IRubyObject[]arr, int from, int to, Ruby runtime) {
         IRubyObject nils[] = runtime.getNilPrefilledArray();
         int i;
 
         for (i = from; i + Ruby.NIL_PREFILLED_ARRAY_SIZE < to; i += Ruby.NIL_PREFILLED_ARRAY_SIZE) {
             System.arraycopy(nils, 0, arr, i, Ruby.NIL_PREFILLED_ARRAY_SIZE);
         }
         System.arraycopy(nils, 0, arr, i, to - i);
     }
 
     public static void fillNil(IRubyObject[]arr, Ruby runtime) {
         fillNil(arr, 0, arr.length, runtime);
     }
 
     public static boolean isFastSwitchableString(IRubyObject str) {
         return str instanceof RubyString;
     }
 
     public static boolean isFastSwitchableSingleCharString(IRubyObject str) {
         return str instanceof RubyString && ((RubyString)str).getByteList().length() == 1;
     }
 
     public static int getFastSwitchString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.hashCode();
     }
 
     public static int getFastSwitchSingleCharString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.get(0);
     }
 
     public static boolean isFastSwitchableSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol;
     }
 
     public static boolean isFastSwitchableSingleCharSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol && ((RubySymbol)sym).asJavaString().length() == 1;
     }
 
     public static int getFastSwitchSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return str.hashCode();
     }
 
     public static int getFastSwitchSingleCharSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return (int)str.charAt(0);
     }
 
     public static Block getBlock(ThreadContext context, IRubyObject self, Node node) {
         IterNode iter = (IterNode)node;
         iter.getScope().determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         if (iter.getBlockBody() instanceof InterpretedBlock) {
             return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
         } else {
             return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
         }
     }
 
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Node node, Block aBlock) {
         return RuntimeHelpers.getBlockFromBlockPassBody(runtime, node.interpret(runtime, context, self, aBlock), aBlock);
     }
 
     /**
      * Equivalent to rb_equal in MRI
      *
      * @param context
      * @param a
      * @param b
      * @return
      */
     public static IRubyObject rbEqual(ThreadContext context, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.getRuntime();
         if (a == b) return runtime.getTrue();
         return a.callMethod(context, "==", b);
     }
+
+    public static void traceLine(ThreadContext context) {
+        String name = context.getFrameName();
+        RubyModule type = context.getFrameKlazz();
+        context.getRuntime().callEventHooks(context, RubyEvent.LINE, context.getFile(), context.getLine(), name, type);
+    }
+
+    public static void traceClass(ThreadContext context) {
+        String name = context.getFrameName();
+        RubyModule type = context.getFrameKlazz();
+        context.getRuntime().callEventHooks(context, RubyEvent.CLASS, context.getFile(), context.getLine(), name, type);
+    }
+
+    public static void traceEnd(ThreadContext context) {
+        String name = context.getFrameName();
+        RubyModule type = context.getFrameKlazz();
+        context.getRuntime().callEventHooks(context, RubyEvent.END, context.getFile(), context.getLine(), name, type);
+    }
 }
diff --git a/src/org/jruby/runtime/MethodFactory.java b/src/org/jruby/runtime/MethodFactory.java
index dae6b3b076..67d9fbce85 100644
--- a/src/org/jruby/runtime/MethodFactory.java
+++ b/src/org/jruby/runtime/MethodFactory.java
@@ -1,203 +1,204 @@
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
  * Copyright (C) 2008 The JRuby Community <www.jruby.org>
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
 package org.jruby.runtime;
 
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.anno.JavaMethodDescriptor;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.ReflectionMethodFactory;
 import org.jruby.internal.runtime.methods.InvocationMethodFactory;
 import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
+import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  * MethodFactory is used to generate "invokers" or "method handles" given a target
  * class, method name, and other characteristics. In order to bind methods into
  * Ruby's reified class hierarchy, we need a way to treat individual methods as
  * objects. Implementers of this class provide that functionality.
  */
 public abstract class MethodFactory {
     /**
      * A Class[] representing the signature of compiled Ruby method.
      */
     public final static Class[] COMPILED_METHOD_PARAMS = new Class[] {ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class};
     
     /**
      * For batched method construction, the logic necessary to bind resulting
      * method objects into a target module/class must be provided as a callback.
      * This interface should be implemented by code calling any batched methods
      * on this MethodFactory.
      */
     @Deprecated
     public interface MethodDefiningCallback {
         public void define(RubyModule targetMetaClass, JavaMethodDescriptor desc, DynamicMethod dynamicMethod);
     }
 
     /**
      * Based on optional properties, create a new MethodFactory. By default,
      * this will create a code-generation-based InvocationMethodFactory. If
      * security restricts code generation, ReflectionMethodFactory will be used.
      * If we are dumping class definitions, DumpingInvocationMethodFactory will
      * be used. See MethodFactory's static initializer for more details.
      * 
      * @param classLoader The classloader to use for searching for and
      * dynamically loading code.
      * @return A new MethodFactory.
      */
     public static MethodFactory createFactory(ClassLoader classLoader) {
         if (reflection) return new ReflectionMethodFactory();
         if (dumping) return new DumpingInvocationMethodFactory(dumpingPath, classLoader);
 
         return new InvocationMethodFactory(classLoader);
     }
     
     /**
      * Get a new method handle based on the target JRuby-compiled method.
      * Because compiled Ruby methods have additional requirements and
      * characteristics not typically found in Java-based methods, this is
      * provided as a separate way to define such method handles.
      * 
      * @param implementationClass The class to which the method will be bound.
      * @param method The name of the method
      * @param arity The Arity of the method
      * @param visibility The method's visibility on the target type.
      * @param scope The methods static scoping information.
      * @param scriptObject An instace of the target compiled method class.
      * @param callConfig The call configuration to use for this method.
      * @return A new method handle for the target compiled method.
      */
     public abstract DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, 
             Arity arity, Visibility visibility, StaticScope scope, 
-            Object scriptObject, CallConfiguration callConfig);
+            Object scriptObject, CallConfiguration callConfig, ISourcePosition position);
     
     /**
      * Like getCompiledMethod, but postpones any heavy lifting involved in
      * creating the method until first invocation. This helps reduce the cost
      * of starting up AOT-compiled code, by spreading out the heavy lifting
      * across the run rather than causing all method handles to be immediately
      * instantiated.
      * 
      * @param implementationClass The class to which the method will be bound.
      * @param method The name of the method
      * @param arity The Arity of the method
      * @param visibility The method's visibility on the target type.
      * @param scope The methods static scoping information.
      * @param scriptObject An instace of the target compiled method class.
      * @param callConfig The call configuration to use for this method.
      * @return A new method handle for the target compiled method.
      */
     public abstract DynamicMethod getCompiledMethodLazily(
             RubyModule implementationClass, String method, 
             Arity arity, Visibility visibility, StaticScope scope, 
-            Object scriptObject, CallConfiguration callConfig);
+            Object scriptObject, CallConfiguration callConfig, ISourcePosition position);
     
     /**
      * Based on a list of annotated Java methods, generate a method handle using
      * the annotation and the target signatures. The annotation and signatures
      * will be used to dynamically generate the appropriate call logic for the
      * handle. This differs from the single-method version in that it will dispatch
      * multiple specific-arity paths to different target methods.
      * 
      * @param implementationClass The target class or module on which the method
      * will be bound.
      * @param descs A list of JavaMethodDescriptors describing the target methods
      * @return A method handle for the target object.
      */
     public abstract DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> desc);
     
     /**
      * Based on an annotated Java method object, generate a method handle using
      * the annotation and the target signature. The annotation and signature
      * will be used to dynamically generate the appropriate call logic for the
      * handle.
      * 
      * @param implementationClass The target class or module on which the method
      * will be bound.
      * @param desc A JavaMethodDescriptor describing the target method
      * @return A method handle for the target object.
      */
     public abstract DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc);
     
     /**
      * Get a CompiledBlockCallback for the specified block
      *
      * @param method The name of the method
      * @param scriptObject The object in which the method can be found
      * @return A new CompiledBlockCallback for the method
      */
     public abstract CompiledBlockCallback getBlockCallback(String method, Object scriptObject);
 
     /**
      * Get a CompiledBlockCallback for the specified block
      *
      * @param method The name of the method
      * @param scriptObject The object in which the method can be found
      * @return A new CompiledBlockCallback for the method
      */
     public abstract CompiledBlockCallback19 getBlockCallback19(String method, Object scriptObject);
 
     /**
      * Use the reflection-based factory.
      */
     private static final boolean reflection;
     /**
      * User the dumping-based factory, which generates .class files as it runs.
      */
     private static final boolean dumping;
     /**
      * The target path for the dumping factory to save the .class files.
      */
     private static final String dumpingPath;
     
     static {
         boolean reflection_ = false, dumping_ = false;
         String dumpingPath_ = null;
         // initialize the static settings to determine which factory to use
         if (Ruby.isSecurityRestricted()) {
             reflection_ = true;
         } else {
             if (SafePropertyAccessor.getProperty("jruby.reflection") != null && SafePropertyAccessor.getBoolean("jruby.reflection")) {
                 reflection_ = true;
             }
             if (SafePropertyAccessor.getProperty("jruby.dump_invocations") != null) {
                 dumping_ = true;
                 dumpingPath_ = SafePropertyAccessor.getProperty("jruby.dump_invocations");
             }
         }
         reflection = reflection_;
         dumping = dumping_;
         dumpingPath = dumpingPath_;
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 27c55396a5..1cb14a8a66 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1467 +1,1471 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
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
     
     private final static int INITIAL_SIZE = 10;
     private final static String UNKNOWN_NAME = "(unknown)";
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
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
     private boolean eventHooksEnabled = true;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
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
         int index = ++catchIndex;
         CatchTarget[] stack = catchStack;
         stack[index] = catchTarget;
         if (index + 1 == stack.length) {
             expandCatchIfNecessary();
         }
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         int index = catchIndex;
         if (index < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[index + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, index + 1);
         return activeCatches;
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
         stack[index].updateFrame(clazz, self, name, block, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, file, line);
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
         stack[index].updateFrame(name, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex--];
         setFileAndLine(frame);
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         int index = frameIndex;
         Frame frame = frameStack[index];
         frameStack[index] = oldFrame;
         frameIndex = index - 1;
         setFileAndLine(frame);
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
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     @Deprecated
     public void setFrameJumpTarget(JumpTarget target) {
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
     
     public void setFileAndLine(Frame frame) {
         this.file = frame.getFile();
         this.line = frame.getLine();
     }
 
     public void setFileAndLine(ISourcePosition position) {
         this.file = position.getFile();
         this.line = position.getStartLine();
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
     
     int calls = 0;
     
     public void callThreadPoll() {
         if ((calls++ & 0xFF) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.calls++ & 0xFF) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
+        trace(event, name, implClass, file, line);
+    }
+
+    public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
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
     
     @Deprecated
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         addBackTraceElement(backtrace.getRuntime(), backtrace, frame, previousFrame);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLine() == previousFrame.getLine() &&
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile())) {
             return;
         }
         
         RubyString traceLine;
         if (previousFrame.getName() != null) {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
         } else if (runtime.is1_9()) {
             // TODO: This probably isn't the best hack, but it works until we can have different
             // root frame setup for 1.9 easily.
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
         } else {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLineNumber() == previousFrame.getLineNumber() &&
                 frame.getMethodName() != null &&
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName() != null &&
                 frame.getFileName().equals(previousFrame.getFileName())) {
             return;
         }
         
         RubyString traceLine;
         String fileName = frame.getFileName();
         if (fileName == null) fileName = "";
         if (previousFrame.getMethodName() == UNKNOWN_NAME) {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()));
         } else {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()) + ":in `" + previousFrame.getMethodName() + '\'');
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame, FrameType frameType) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getMethodName() != null && 
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName().equals(previousFrame.getFileName()) &&
                 frame.getLineNumber() == previousFrame.getLineNumber()) {
             return;
         }
         
         StringBuilder buf = new StringBuilder(60);
         buf.append(frame.getFileName()).append(':').append(frame.getLineNumber());
         
         if (previousFrame.getMethodName() != null) {
             switch (frameType) {
             case METHOD:
                 buf.append(":in `");
                 buf.append(previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case BLOCK:
                 buf.append(":in `");
                 buf.append("block in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case EVAL:
                 buf.append(":in `");
                 buf.append("eval in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case CLASS:
                 buf.append(":in `");
                 buf.append("class in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case ROOT:
                 buf.append(":in `<toplevel>'");
                 break;
             }
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
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames) {
         return createBacktraceFromFrames(runtime, backtraceFrames, true);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         int traceSize = frameIndex - level + 1;
         RubyArray backtrace = runtime.newArray(traceSize);
 
         for (int i = traceSize - 1; i > 0; i--) {
             addBackTraceElement(runtime, backtrace, frameStack[i], frameStack[i - 1]);
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
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames, boolean cropAtEval) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = 0; i < traceSize - 1; i++) {
             RubyStackTraceElement frame = backtraceFrames[i];
             // We are in eval with binding break out early
             // FIXME: This is broken with the new backtrace stuff
             if (cropAtEval && frame.isBinding()) break;
 
             addBackTraceElement(runtime, backtrace, frame, backtraceFrames[i + 1]);
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
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
 
     public static class RubyStackTraceElement {
         private StackTraceElement element;
         private boolean binding;
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
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
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public RubyStackTraceElement[] createBacktrace2(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         RubyStackTraceElement[] newTrace;
         
         if (traceSize <= 0) return null;
 
         int totalSize = traceSize;
         if (nativeException) {
             // assert level == 0;
             totalSize = traceSize + 1;
         }
         newTrace = new RubyStackTraceElement[totalSize];
 
         return buildTrace(newTrace);
     }
 
     private RubyStackTraceElement[] buildTrace(RubyStackTraceElement[] newTrace) {
         for (int i = 0; i < newTrace.length; i++) {
             Frame current = frameStack[i];
             String klazzName = getClassNameFromFrame(current);
             String methodName = getMethodNameFromFrame(current);
             newTrace[newTrace.length - 1 - i] = 
                     new RubyStackTraceElement(klazzName, methodName, current.getFile(), current.getLine() + 1, current.isBindingFrame());
         }
         
         return newTrace;
     }
 
     private String getClassNameFromFrame(Frame current) {
         String klazzName;
         if (current.getKlazz() == null) {
             klazzName = UNKNOWN_NAME;
         } else {
             klazzName = current.getKlazz().getName();
         }
         return klazzName;
     }
     
     private String getMethodNameFromFrame(Frame current) {
         String methodName = current.getName();
         if (current.getName() == null) {
             methodName = UNKNOWN_NAME;
         }
         return methodName;
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
     
     public static IRubyObject createRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace, boolean filter) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             
             if (filter) {
                 if (element.getClassName().startsWith("org.jruby") ||
                         element.getLineNumber() < 0) {
                     continue;
                 }
             }
             RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
             traceArray.append(str);
         }
         
         return traceArray;
     }
     
     public static IRubyObject createRubyCompiledBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index < 0) continue;
             String unmangledMethod = element.getMethodName().substring(index + 6);
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
             traceArray.append(str);
         }
         
         return traceArray;
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
 
         // set the binding's frame's "previous" file and line to current, so
         // trace will show who called the block
         f.setFileAndLine(file, line);
         
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(DefaultMethod.class.getName() + ".interpretedCall", FrameType.METHOD);
         
         INTERPRETED_FRAMES.put(InterpretedBlock.class.getName() + ".evalBlockBody", FrameType.BLOCK);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalWithBinding", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalSimple", FrameType.EVAL);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalClassDefinitionBody", FrameType.CLASS);
         
         INTERPRETED_FRAMES.put(Ruby.class.getName() + ".runInterpreter", FrameType.ROOT);
     }
     
     public static IRubyObject createRubyHybridBacktrace(Ruby runtime, RubyStackTraceElement[] backtraceFrames, RubyStackTraceElement[] stackTrace, boolean debug) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         ThreadContext context = runtime.getCurrentContext();
         
         int rubyFrameIndex = backtraceFrames.length - 1;
         for (int i = 0; i < stackTrace.length; i++) {
             RubyStackTraceElement element = stackTrace[i];
             
             // look for mangling markers for compiled Ruby in method name
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index >= 0) {
                 String unmangledMethod = element.getMethodName().substring(index + 6);
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 
                 // if it's not a rescue or ensure, there's a frame associated, so decrement
                 if (!(element.getMethodName().contains("__rescue__") || element.getMethodName().contains("__ensure__"))) {
                     rubyFrameIndex--;
                 }
                 continue;
             }
             
             // look for __file__ method name for compiled roots
             if (element.getMethodName().equals("__file__")) {
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ": `<toplevel>'");
                 traceArray.append(str);
                 rubyFrameIndex--;
                 continue;
             }
             
             // look for mangling markers for bound, unframed methods in class name
             index = element.getClassName().indexOf("$RUBYINVOKER$");
             if (index >= 0) {
                 // unframed invokers have no Ruby frames, so pull from class name
                 // but use current frame as file and line
                 String unmangledMethod = element.getClassName().substring(index + 13);
                 Frame current = context.frameStack[rubyFrameIndex];
                 RubyString str = RubyString.newString(runtime, current.getFile() + ":" + (current.getLine() + 1) + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 continue;
             }
             
             // look for mangling markers for bound, framed methods in class name
             index = element.getClassName().indexOf("$RUBYFRAMEDINVOKER$");
             if (index >= 0) {
                 // framed invokers will have Ruby frames associated with them
                 addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], FrameType.METHOD);
                 rubyFrameIndex--;
                 continue;
             }
             
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null) {
                 // Frame matches one of our markers for "interpreted" calls
                 if (rubyFrameIndex == 0) {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex], frameType);
                 } else {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], frameType);
                     rubyFrameIndex--;
                 }
                 continue;
             } else {
                 // Frame is extraneous runtime information, skip it unless debug
                 if (debug) {
                     RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element.getElement()));
                     traceArray.append(str);
                 }
                 continue;
             }
         }
         
         return traceArray;
     }
     
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
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         pushBacktraceFrame(name);
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
 
     public void postMethodBacktraceDummyScope() {
         popFrame();
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
         setFileAndLine(getCurrentFrame());
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
         return new Binding(frame, getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), file, line);
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
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), file, line);
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
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, file, line);
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
         return new Binding(self, frame, visibility, getRubyClass(), scope, file, line);
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 }
