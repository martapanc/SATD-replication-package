diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index afff545adc..be76db14c4 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -514,1082 +514,1089 @@ public class RubyInstanceConfig {
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             jrubyHome = calculateJRubyHome();
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home, error);
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
         if (newEnvironment == null) newEnvironment = new HashMap();
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
     
     public StringBuffer getInlineScript() {
         return inlineScript;
     }
     
     public void setHasInlineScript(boolean hasInlineScript) {
         this.hasInlineScript = hasInlineScript;
     }
     
     public boolean hasInlineScript() {
         return hasInlineScript;
     }
     
     public Collection<String> getRequiredLibraries() {
         return requiredLibraries;
     }
 
     @Deprecated
     public Collection<String> requiredLibraries() {
         return requiredLibraries;
     }
     
     public List<String> getLoadPaths() {
         return loadPaths;
     }
 
     @Deprecated
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public void setLoadPaths(List<String> loadPaths) {
         this.loadPaths = loadPaths;
     }
     
     public void setShouldPrintUsage(boolean shouldPrintUsage) {
         this.shouldPrintUsage = shouldPrintUsage;
     }
     
     public boolean getShouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     @Deprecated
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
     
     public void setShouldPrintProperties(boolean shouldPrintProperties) {
         this.shouldPrintProperties = shouldPrintProperties;
     }
     
     public boolean getShouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     @Deprecated
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
     
     public void setBenchmarking(boolean benchmarking) {
         this.benchmarking = benchmarking;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
     
     public void setAssumeLoop(boolean assumeLoop) {
         this.assumeLoop = assumeLoop;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
     
     public void setAssumePrinting(boolean assumePrinting) {
         this.assumePrinting = assumePrinting;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
     
     public void setProcessLineEnds(boolean processLineEnds) {
         this.processLineEnds = processLineEnds;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
     
     public void setSplit(boolean split) {
         this.split = split;
     }
 
     public boolean isSplit() {
         return split;
     }
     
     public Verbosity getVerbosity() {
         return verbosity;
     }
     
     public void setVerbosity(Verbosity verbosity) {
         this.verbosity = verbosity;
     }
 
     public boolean isVerbose() {
         return verbosity == Verbosity.TRUE;
     }
 
     @Deprecated
     public Boolean getVerbose() {
         return isVerbose();
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
 
     public void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     public void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     public void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
     
     public void setShouldRunInterpreter(boolean shouldRunInterpreter) {
         this.shouldRunInterpreter = shouldRunInterpreter;
     }
     
     public boolean getShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     @Deprecated
     public boolean shouldRunInterpreter() {
         return isShouldRunInterpreter();
     }
 
     @Deprecated
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
     
     public void setShouldCheckSyntax(boolean shouldSetSyntax) {
         this.shouldCheckSyntax = shouldSetSyntax;
     }
 
     public boolean getShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
     
     public void setInputFieldSeparator(String inputFieldSeparator) {
         this.inputFieldSeparator = inputFieldSeparator;
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
     
     public void setInternalEncoding(String internalEncoding) {
         this.internalEncoding = internalEncoding;
     }
 
     public String getInternalEncoding() {
         return internalEncoding;
     }
     
     public void setExternalEncoding(String externalEncoding) {
         this.externalEncoding = externalEncoding;
     }
 
     public String getExternalEncoding() {
         return externalEncoding;
     }
     
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
     
     public void setSafeLevel(int safeLevel) {
         this.safeLevel = safeLevel;
     }
 
     public int getSafeLevel() {
         return safeLevel;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
     
     public void setInPlaceBackupExtension(String inPlaceBackupExtension) {
         this.inPlaceBackupExtension = inPlaceBackupExtension;
     }
 
     public String getInPlaceBackupExtension() {
         return inPlaceBackupExtension;
     }
 
     @Deprecated
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
     
     public void setParserDebug(boolean parserDebug) {
         this.parserDebug = parserDebug;
     }
     
     public boolean getParserDebug() {
         return parserDebug;
     }
 
     public boolean isArgvGlobalsOn() {
         return argvGlobalsOn;
     }
 
     public void setArgvGlobalsOn(boolean argvGlobalsOn) {
         this.argvGlobalsOn = argvGlobalsOn;
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
     
     public void setProfilingMode(ProfilingMode profilingMode) {
         this.profilingMode = profilingMode;
     }
 
     public ProfilingMode getProfilingMode() {
         return profilingMode;
     }
 
     public boolean hasShebangLine() {
         return hasShebangLine;
     }
 
     public void setHasShebangLine(boolean hasShebangLine) {
         this.hasShebangLine = hasShebangLine;
     }
 
     public boolean isDisableGems() {
         return disableGems;
     }
 
     public void setDisableGems(boolean dg) {
         this.disableGems = dg;
     }
 
     public TraceType getTraceType() {
         return traceType;
     }
 
     public void setTraceType(TraceType traceType) {
         this.traceType = traceType;
     }
     
     /**
      * Set whether native code is enabled for this config. Disabling it also
      * disables C extensions (@see RubyInstanceConfig#setCextEnabled).
      * 
      * @param b new value indicating whether native code is enabled
      */
     public void setNativeEnabled(boolean b) {
         _nativeEnabled = false;
     }
     
     /**
      * Get whether native code is enabled for this config.
      * 
      * @return true if native code is enabled; false otherwise.
      */
     public boolean isNativeEnabled() {
         return _nativeEnabled;
     }
     
     /**
      * Set whether C extensions are enabled for this config.
      * 
      * @param b new value indicating whether native code is enabled
      */
     public void setCextEnabled(boolean b) {
         _cextEnabled = b;
     }
     
     /**
      * Get whether C extensions are enabled for this config.
      * 
      * @return true if C extensions are enabled; false otherwise.
      */
     public boolean isCextEnabled() {
         return _cextEnabled;
     }
     
     public void setXFlag(boolean xFlag) {
         this.xFlag = xFlag;
     }
 
     public boolean isXFlag() {
         return xFlag;
     }
     
     @Deprecated
     public boolean isxFlag() {
         return xFlag;
     }
     
     /**
      * True if colorized backtraces are enabled. False otherwise.
      */
     public boolean getBacktraceColor() {
         return backtraceColor;
     }
     
     /**
      * Set to true to enable colorized backtraces.
      */
     public void setBacktraceColor(boolean backtraceColor) {
         this.backtraceColor = backtraceColor;
     }
     
     ////////////////////////////////////////////////////////////////////////////
     // Configuration fields.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * Indicates whether the script must be extracted from script source
      */
     private boolean xFlag;
 
     /**
      * Indicates whether the script has a shebang line or not
      */
     private boolean hasShebangLine;
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled
             = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
 
     /** Environment variables; defaults to System.getenv() in constructor */
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
     private Verbosity verbosity = Verbosity.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
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
     private boolean updateNativeENVEnabled = true;
     
     private int safeLevel = 0;
 
     private String jrubyHome;
     
     /**
      * Whether native code is enabled for this configuration.
      */
     private boolean _nativeEnabled = NATIVE_ENABLED;
     
     /**
      * Whether C extensions are enabled for this configuration.
      */
     private boolean _cextEnabled = CEXT_ENABLED;
 
     private TraceType traceType =
             TraceType.traceTypeFor(SafePropertyAccessor.getProperty("jruby.backtrace.style", "ruby_framed"));
     
     private boolean backtraceColor = SafePropertyAccessor.getBoolean("jruby.backtrace.color", false);
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
     
     ////////////////////////////////////////////////////////////////////////////
     // Support classes, etc.
     ////////////////////////////////////////////////////////////////////////////
     
     public enum Verbosity { NIL, FALSE, TRUE }
 
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
 
     public enum ProfilingMode {
 		OFF, API, FLAT, GRAPH
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
     
     ////////////////////////////////////////////////////////////////////////////
     // Static configuration fields, used as defaults for new JRuby instances.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     public static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     public static final int JIT_MAX_SIZE_LIMIT = 30000;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     public static final int JIT_THRESHOLD = 50;
     
     /**
      * The version to use for generated classes. Set to current JVM version by default
      */
     public static final int JAVA_VERSION = initGlobalJavaVersion();
     
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
      * Enable compiler peephole optimizations.
      *
      * Set with the <tt>jruby.compile.peephole</tt> system property.
      */
     public static final boolean PEEPHOLE_OPTZ
             = SafePropertyAccessor.getBoolean("jruby.compile.peephole", true);
     /**
      * Enable "dynopt" optimizations.
      *
      * Set with the <tt>jruby.compile.dynopt</tt> system property.
      */
     public static boolean DYNOPT_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.dynopt", false);
 
     /**
      * Enable compiler "noguards" optimizations.
      *
      * Set with the <tt>jruby.compile.noguards</tt> system property.
      */
     public static boolean NOGUARDS_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.noguards");
 
     /**
      * Enable compiler "fastest" set of optimizations.
      *
      * Set with the <tt>jruby.compile.fastest</tt> system property.
      */
     public static boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
 
     /**
      * Enable fast operator compiler optimizations.
      *
      * Set with the <tt>jruby.compile.fastops</tt> system property.
      */
     public static boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops", true);
 
     /**
      * Enable "threadless" compile.
      *
      * Set with the <tt>jruby.compile.threadless</tt> system property.
      */
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
 
     /**
      * Enable "fast send" compiler optimizations.
      *
      * Set with the <tt>jruby.compile.fastsend</tt> system property.
      */
     public static boolean FASTSEND_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastsend");
 
     /**
      * Enable lazy handles optimizations.
      *
      * Set with the <tt>jruby.compile.lazyHandles</tt> system property.
      */
     public static boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
 
     /**
      * Inline dynamic calls.
      *
      * Set with the <tt>jruby.compile.inlineDyncalls</tt> system property.
      */
     public static boolean INLINE_DYNCALL_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.inlineDyncalls");
 
     /**
      * Enable fast multiple assignment optimization.
      *
      * Set with the <tt>jruby.compile.fastMasgn</tt> system property.
      */
     public static boolean FAST_MULTIPLE_ASSIGNMENT
             = SafePropertyAccessor.getBoolean("jruby.compile.fastMasgn", false);
 
     /**
      * Enable a thread pool. Each Ruby thread will be mapped onto a thread from this pool.
      *
      * Set with the <tt>jruby.thread.pool.enabled</tt> system property.
      */
     public static final boolean POOLING_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.thread.pool.enabled");
 
     /**
      * Maximum thread pool size (integer, default Integer.MAX_VALUE).
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final int POOL_MAX
             = SafePropertyAccessor.getInt("jruby.thread.pool.max", Integer.MAX_VALUE);
     /**
      * Minimum thread pool size (integer, default 0).
      *
      * Set with the <tt>jruby.thread.pool.min</tt> system property.
      */
     public static final int POOL_MIN
             = SafePropertyAccessor.getInt("jruby.thread.pool.min", 0);
     /**
      * Thread pool time-to-live in seconds.
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final int POOL_TTL
             = SafePropertyAccessor.getInt("jruby.thread.pool.ttl", 60);
 
     /**
      * Enable use of the native Java version of the 'net/protocol' library.
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final boolean NATIVE_NET_PROTOCOL
             = SafePropertyAccessor.getBoolean("jruby.native.net.protocol", false);
 
     /**
      * Enable tracing of method calls.
      *
      * Set with the <tt>jruby.debug.fullTrace</tt> system property.
      */
     public static boolean FULL_TRACE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
 
     /**
      * Comma-separated list of methods to exclude from JIT compilation.
      * Specify as "Module", "Module#method" or "method".
      *
      * Set with the <tt>jruby.jit.exclude</tt> system property.
      */
     public static final String COMPILE_EXCLUDE
             = SafePropertyAccessor.getProperty("jruby.jit.exclude");
 
     /**
      * Indicates the global default for whether native code is enabled. Default
      * is true. This value is used to default new runtime configurations.
      *
      * Set with the <tt>jruby.native.enabled</tt> system property.
      */
     public static final boolean NATIVE_ENABLED = SafePropertyAccessor.getBoolean("jruby.native.enabled", true);
 
     @Deprecated
     public static final boolean nativeEnabled = NATIVE_ENABLED;
 
     /**
      * Indicates the global default for whether C extensions are enabled.
      * Default is the value of RubyInstanceConfig.NATIVE_ENABLED. This value
      * is used to default new runtime configurations.
      *
      * Set with the <tt>jruby.cext.enabled</tt> system property.
      */
     public final static boolean CEXT_ENABLED = SafePropertyAccessor.getBoolean("jruby.cext.enabled", NATIVE_ENABLED);
 
     /**
      * Whether to reify (pre-compile and generate) a Java class per Ruby class.
      *
      * Set with the <tt>jruby.reify.classes</tt> system property.
      */
     public static final boolean REIFY_RUBY_CLASSES
             = SafePropertyAccessor.getBoolean("jruby.reify.classes", false);
 
     /**
      * Log errors that occur during reification.
      *
      * Set with the <tt>jruby.reify.logErrors</tt> system property.
      */
     public static final boolean REIFY_LOG_ERRORS
             = SafePropertyAccessor.getBoolean("jruby.reify.logErrors", false);
 
     /**
      * Whether to use a custom-generated handle for Java methods instead of
      * reflection.
      *
      * Set with the <tt>jruby.java.handles</tt> system property.
      */
     public static final boolean USE_GENERATED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.java.handles", false);
 
     /**
      * Turn on debugging of the load service (requires and loads).
      *
      * Set with the <tt>jruby.debug.loadService</tt> system property.
      */
     public static final boolean DEBUG_LOAD_SERVICE
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService", false);
 
     /**
      * Turn on timings of the load service (requires and loads).
      *
      * Set with the <tt>jruby.debug.loadService.timing</tt> system property.
      */
     public static final boolean DEBUG_LOAD_TIMINGS
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService.timing", false);
 
     /**
      * Turn on debugging of subprocess launching.
      *
      * Set with the <tt>jruby.debug.launch</tt> system property.
      */
     public static final boolean DEBUG_LAUNCHING
             = SafePropertyAccessor.getBoolean("jruby.debug.launch", false);
 
     /**
      * Turn on debugging of script resolution with "-S".
      *
      * Set with the <tt>jruby.debug.scriptResolution</tt> system property.
      */
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
+    /**
+     * In Java integration, allow upper case name for a Java package;
+     * e.g., com.example.UpperCase.Class
+     */
+    public static final boolean UPPER_CASE_PACKAGE_NAME_ALLOWED = 
+            SafePropertyAccessor.getBoolean("jruby.ji.upcase.package.name.allowed", false);
+    
     
     public static final boolean USE_INVOKEDYNAMIC =
             JAVA_VERSION == Opcodes.V1_7
             && SafePropertyAccessor.getBoolean("jruby.compile.invokedynamic", true);
     
     // max times an indy call site can fail before it goes to simple IC
     public static final int MAX_FAIL_COUNT = SafePropertyAccessor.getInt("jruby.invokedynamic.maxfail", 2);
     
     // logging of various indy aspects
     public static final boolean LOG_INDY_BINDINGS = SafePropertyAccessor.getBoolean("jruby.invokedynamic.log.binding");
     public static final boolean LOG_INDY_CONSTANTS = SafePropertyAccessor.getBoolean("jruby.invokedynamic.log.constants");
     
     // properties enabling or disabling certain uses of invokedynamic
     public static final boolean INVOKEDYNAMIC_ALL = USE_INVOKEDYNAMIC && (SafePropertyAccessor.getBoolean("jruby.invokedynamic.all", false));
     public static final boolean INVOKEDYNAMIC_SAFE = USE_INVOKEDYNAMIC && (SafePropertyAccessor.getBoolean("jruby.invokedynamic.safe", false));
     
     public static final boolean INVOKEDYNAMIC_INVOCATION = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && SafePropertyAccessor.getBoolean("jruby.invokedynamic.invocation", true);
     public static final boolean INVOKEDYNAMIC_INVOCATION_SWITCHPOINT = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && SafePropertyAccessor.getBoolean("jruby.invokedynamic.invocation.switchpoint", true);
     public static final boolean INVOKEDYNAMIC_INDIRECT = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && SafePropertyAccessor.getBoolean("jruby.invokedynamic.indirect", true);
     public static final boolean INVOKEDYNAMIC_JAVA = INVOKEDYNAMIC_ALL ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && SafePropertyAccessor.getBoolean("jruby.invokedynamic.java", false);
     public static final boolean INVOKEDYNAMIC_ATTR = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && SafePropertyAccessor.getBoolean("jruby.invokedynamic.attr", true);
     public static final boolean INVOKEDYNAMIC_FASTOPS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && SafePropertyAccessor.getBoolean("jruby.invokedynamic.fastops", true);
     
     public static final boolean INVOKEDYNAMIC_CACHE = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && SafePropertyAccessor.getBoolean("jruby.invokedynamic.cache", true);
     public static final boolean INVOKEDYNAMIC_CONSTANTS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_CACHE && SafePropertyAccessor.getBoolean("jruby.invokedynamic.constants", false);
     public static final boolean INVOKEDYNAMIC_LITERALS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_CACHE && SafePropertyAccessor.getBoolean("jruby.invokedynamic.literals", true);
     
     // properties for logging exceptions, backtraces, and caller invocations
     public static final boolean LOG_EXCEPTIONS = SafePropertyAccessor.getBoolean("jruby.log.exceptions");
     public static final boolean LOG_BACKTRACES = SafePropertyAccessor.getBoolean("jruby.log.backtraces");
     public static final boolean LOG_CALLERS = SafePropertyAccessor.getBoolean("jruby.log.callers");
     
     public static final boolean ERRNO_BACKTRACE
             = SafePropertyAccessor.getBoolean("jruby.errno.backtrace", false);
     
     public static final boolean IR_DEBUG = SafePropertyAccessor.getBoolean("jruby.ir.debug");
     public static final boolean IR_COMPILER_DEBUG = SafePropertyAccessor.getBoolean("jruby.ir.compiler.debug");    
     public static final boolean IR_LIVE_VARIABLE = SafePropertyAccessor.getBoolean("jruby.ir.pass.live_variable");
     public static final boolean IR_DEAD_CODE = SafePropertyAccessor.getBoolean("jruby.ir.pass.dead_code");
     public static final String IR_TEST_INLINER = SafePropertyAccessor.getProperty("jruby.ir.pass.test_inliner");
     
     public static final boolean COROUTINE_FIBERS = SafePropertyAccessor.getBoolean("jruby.fiber.coroutines");
     
     private static volatile boolean loadedNativeExtensions = false;
     
     ////////////////////////////////////////////////////////////////////////////
     // Static initializers
     ////////////////////////////////////////////////////////////////////////////
     
     private static int initGlobalJavaVersion() {
         String specVersion = null;
         try {
             specVersion = System.getProperty("jruby.bytecode.version");
             if (specVersion == null) {
                 specVersion = System.getProperty("java.specification.version");
             }
         } catch (SecurityException se) {
             specVersion = "1.5";
         }
         
         // stack map calculation is failing for some compilation scenarios, so
         // forcing both 1.5 and 1.6 to use 1.5 bytecode for the moment.
         if (specVersion.equals("1.5")) {// || specVersion.equals("1.6")) {
            return Opcodes.V1_5;
         } else if (specVersion.equals("1.6")) {
             return Opcodes.V1_6;
         } else if (specVersion.equals("1.7") || specVersion.equals("1.8")) {
             return Opcodes.V1_7;
         } else {
             throw new RuntimeException("unsupported Java version: " + specVersion);
         }
     }
 }
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 8a5161acdf..637fdf31f0 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,1296 +1,1291 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 package org.jruby.javasupport;
 
 import org.jruby.java.proxies.JavaInterfaceTemplate;
 import org.jruby.java.addons.KernelJavaAddons;
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyClass;
 import org.jruby.RubyClassPathVariable;
 import org.jruby.RubyException;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.RubyUnboundMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodN;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.addons.IOJavaAddons;
 import org.jruby.java.addons.StringJavaAddons;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.java.dispatch.CallableSelector;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.MethodInvoker;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.proxies.ArrayJavaProxyCreator;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.java.proxies.MapJavaProxy;
 import org.jruby.java.proxies.InterfaceJavaProxy;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.java.proxies.RubyObjectHolderProxy;
 import org.jruby.util.ClassCache.OneShotClassLoader;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
     public static final boolean NEW_STYLE_EXTENSION = SafePropertyAccessor.getBoolean("jruby.ji.newStyleExtension", false);
     public static final boolean OBJECT_PROXY_CACHE = SafePropertyAccessor.getBoolean("jruby.ji.objectProxyCache", true);
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().require("builtin/javasupport");
         
         // rewite ArrayJavaProxy superclass to point at Object, so it inherits Object behaviors
         RubyClass ajp = runtime.getClass("ArrayJavaProxy");
         ajp.setSuperClass(runtime.getJavaSupport().getObjectJavaClass().getProxyClass());
         ajp.includeModule(runtime.getEnumerable());
         
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
 
     public static RubyModule createJavaModule(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         RubyModule javaModule = runtime.defineModule("Java");
         
         javaModule.defineAnnotatedMethods(Java.class);
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
         
         // set of utility methods for Java-based proxy objects
         JavaProxyMethods.createJavaProxyMethods(context);
         
         // the proxy (wrapper) type hierarchy
         JavaProxy.createJavaProxy(context);
         ArrayJavaProxyCreator.createArrayJavaProxyCreator(context);
         ConcreteJavaProxy.createConcreteJavaProxy(context);
         InterfaceJavaProxy.createInterfaceJavaProxy(context);
         ArrayJavaProxy.createArrayJavaProxy(context);
 
         // creates ruby's hash methods' proxy for Map interface
         MapJavaProxy.createMapJavaProxy(context);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         // The template for interface modules
         JavaInterfaceTemplate.createJavaInterfaceTemplateModule(context);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         // Now attach Java-related extras to core classes
         runtime.getArray().defineAnnotatedMethods(ArrayJavaAddons.class);
         runtime.getKernel().defineAnnotatedMethods(KernelJavaAddons.class);
         runtime.getString().defineAnnotatedMethods(StringJavaAddons.class);
         runtime.getIO().defineAnnotatedMethods(IOJavaAddons.class);
 
         if (runtime.getObject().isConstantDefined("StringIO")) {
             ((RubyClass)runtime.getObject().getConstant("StringIO")).defineAnnotatedMethods(IOJavaAddons.AnyIO.class);
         }
         
         // add all name-to-class mappings
         addNameClassMappings(runtime, runtime.getJavaSupport().getNameClassMap());
         
         // add some base Java classes everyone will need
         runtime.getJavaSupport().setObjectJavaClass(JavaClass.get(runtime, Object.class));
         
         // finally, set JavaSupport.isEnabled to true
         runtime.getJavaSupport().setActive(true);
 
         return javaModule;
     }
 
     public static class OldStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             return Java.concrete_proxy_inherited(recv, arg0);
         }
     };
 
     public static class NewStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             if (!(arg0 instanceof RubyClass)) {
                 throw recv.getRuntime().newTypeError(arg0, recv.getRuntime().getClassClass());
             }
             
             JavaInterfaceTemplate.addRealImplClassNew((RubyClass)arg0);
             return recv.getRuntime().getNil();
         }
     };
     
     /**
      * This populates the master map from short-cut names to JavaClass instances for
      * a number of core Java types.
      * 
      * @param runtime
      * @param nameClassMap
      */
     private static void addNameClassMappings(Ruby runtime, Map<String, JavaClass> nameClassMap) {
         JavaClass booleanPrimClass = JavaClass.get(runtime, Boolean.TYPE);
         JavaClass booleanClass = JavaClass.get(runtime, Boolean.class);
         nameClassMap.put("boolean", booleanPrimClass);
         nameClassMap.put("Boolean", booleanClass);
         nameClassMap.put("java.lang.Boolean", booleanClass);
         
         JavaClass bytePrimClass = JavaClass.get(runtime, Byte.TYPE);
         JavaClass byteClass = JavaClass.get(runtime, Byte.class);
         nameClassMap.put("byte", bytePrimClass);
         nameClassMap.put("Byte", byteClass);
         nameClassMap.put("java.lang.Byte", byteClass);
         
         JavaClass shortPrimClass = JavaClass.get(runtime, Short.TYPE);
         JavaClass shortClass = JavaClass.get(runtime, Short.class);
         nameClassMap.put("short", shortPrimClass);
         nameClassMap.put("Short", shortClass);
         nameClassMap.put("java.lang.Short", shortClass);
         
         JavaClass charPrimClass = JavaClass.get(runtime, Character.TYPE);
         JavaClass charClass = JavaClass.get(runtime, Character.class);
         nameClassMap.put("char", charPrimClass);
         nameClassMap.put("Character", charClass);
         nameClassMap.put("Char", charClass);
         nameClassMap.put("java.lang.Character", charClass);
         
         JavaClass intPrimClass = JavaClass.get(runtime, Integer.TYPE);
         JavaClass intClass = JavaClass.get(runtime, Integer.class);
         nameClassMap.put("int", intPrimClass);
         nameClassMap.put("Integer", intClass);
         nameClassMap.put("Int", intClass);
         nameClassMap.put("java.lang.Integer", intClass);
         
         JavaClass longPrimClass = JavaClass.get(runtime, Long.TYPE);
         JavaClass longClass = JavaClass.get(runtime, Long.class);
         nameClassMap.put("long", longPrimClass);
         nameClassMap.put("Long", longClass);
         nameClassMap.put("java.lang.Long", longClass);
         
         JavaClass floatPrimClass = JavaClass.get(runtime, Float.TYPE);
         JavaClass floatClass = JavaClass.get(runtime, Float.class);
         nameClassMap.put("float", floatPrimClass);
         nameClassMap.put("Float", floatClass);
         nameClassMap.put("java.lang.Float", floatClass);
         
         JavaClass doublePrimClass = JavaClass.get(runtime, Double.TYPE);
         JavaClass doubleClass = JavaClass.get(runtime, Double.class);
         nameClassMap.put("double", doublePrimClass);
         nameClassMap.put("Double", doubleClass);
         nameClassMap.put("java.lang.Double", doubleClass);
         
         JavaClass bigintClass = JavaClass.get(runtime, BigInteger.class);
         nameClassMap.put("big_int", bigintClass);
         nameClassMap.put("big_integer", bigintClass);
         nameClassMap.put("BigInteger", bigintClass);
         nameClassMap.put("java.math.BigInteger", bigintClass);
         
         JavaClass bigdecimalClass = JavaClass.get(runtime, BigDecimal.class);
         nameClassMap.put("big_decimal", bigdecimalClass);
         nameClassMap.put("BigDecimal", bigdecimalClass);
         nameClassMap.put("java.math.BigDecimal", bigdecimalClass);
         
         JavaClass objectClass = JavaClass.get(runtime, Object.class);
         nameClassMap.put("object", objectClass);
         nameClassMap.put("Object", objectClass);
         nameClassMap.put("java.lang.Object", objectClass);
         
         JavaClass stringClass = JavaClass.get(runtime, String.class);
         nameClassMap.put("string", stringClass);
         nameClassMap.put("String", stringClass);
         nameClassMap.put("java.lang.String", stringClass);
     }
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().getInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyClass) get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
 
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().getInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime,
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
 
     private static final Map<String, Boolean> JAVA_PRIMITIVES = new HashMap<String, Boolean>();
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) {
             JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
         }
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         Ruby runtime = recv.getRuntime();
 
         if (!(module instanceof RubyModule)) {
             throw runtime.newTypeError(module, runtime.getModule());
         }
         IRubyObject proxyClass = get_proxy_class(recv, javaClass);
         RubyModule m = (RubyModule)module;
         String constName = constant.asJavaString();
         IRubyObject existing = m.getConstantNoConstMissing(constName);
 
         if (existing != null
                 && existing != RubyBasicObject.UNDEF
                 && existing != proxyClass) {
             runtime.getWarnings().warn("replacing " + existing + " with " + proxyClass + " in constant '" + constName + " on class/module " + m);
         }
         
         return ((RubyModule) module).setConstantQuiet(constant.asJavaString(), get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Same as Java#getInstance(runtime, rawJavaObject, System.getBoolean('jruby.ji.objectProxyCache')).
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         return getInstance(runtime, rawJavaObject, OBJECT_PROXY_CACHE);
     }
     
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime
      * @param rawJavaObject
      * @param useCache 
      * @return the new (or cached, if useCache is true) proxy for the specified Java object
      * @see JavaUtil#convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject, boolean useCache) {
         if (rawJavaObject != null) {
             if (OBJECT_PROXY_CACHE && useCache) {
                 return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject,
                         (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
             } else {
                 return allocateProxy(rawJavaObject, (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
             }
         }
         return runtime.getNil();
     }
 
     public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         RubyModule interfaceModule;
         if ((interfaceModule = javaClass.getProxyModule()) != null) {
             return interfaceModule;
         }
         javaClass.lockProxy();
         try {
             if ((interfaceModule = javaClass.getProxyModule()) == null) {
                 interfaceModule = (RubyModule) runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.setInstanceVariable("@java_class", javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
                 addToJavaPackageModule(interfaceModule, javaClass);
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(Ruby runtime, IRubyObject javaClassObject) {
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.forNameVerbose(runtime, javaClassObject.asJavaString());
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     public static RubyClass getProxyClassForObject(Ruby runtime, Object object) {
         return (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, object.getClass()));
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         final Class<?> c = javaClass.javaClass();
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         if (c.isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if (c.isArray()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     if (NEW_STYLE_EXTENSION) {
                         proxyClass.getMetaClass().defineAnnotatedMethods(NewStyleExtensionInherited.class);
                     } else {
                         proxyClass.getMetaClass().defineAnnotatedMethods(OldStyleExtensionInherited.class);
                     }
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                         (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                         javaClass, false);
                     // include interface modules into the proxy class
                     Class<?>[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0;) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         // java.util.Map type object has its own proxy, but following
                         // is needed. Unless kind_of?(is_a?) test will fail.
                         //if (interfaces[i] != java.util.Map.class) {
                             proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                         //}
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
 
                 // JRUBY-1000, fail early when attempting to subclass a final Java class;
                 // solved here by adding an exception-throwing "inherited"
                 if (Modifier.isFinal(c.getModifiers())) {
                     proxyClass.getMetaClass().addMethod("inherited", new org.jruby.internal.runtime.methods.JavaMethod(proxyClass, PUBLIC) {
                         @Override
                         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                             throw context.getRuntime().newTypeError("can not extend final Java class: " + c.getCanonicalName());
                         }
                     });
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass) java_class_object;
         } else {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
         // JRUBY-2938 the proxy class might already exist
         RubyClass proxyClass = javaClass.getProxyClass();
         if (proxyClass != null) return proxyClass;
 
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         try {
             javaClass.javaClass().asSubclass(java.util.Map.class);
             proxyClass.setAllocator(runtime.getJavaSupport().getMapJavaProxyClass().getAllocator());
             proxyClass.defineAnnotatedMethods(MapJavaProxy.class);
             proxyClass.includeModule(runtime.getEnumerable());
         } catch (ClassCastException e) {
             proxyClass.setAllocator(superClass.getAllocator());
         }
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
 
         // add java_method for unbound use
         proxyClass.defineAnnotatedMethods(JavaProxyClassMethods.class);
         return proxyClass;
     }
 
     public static class JavaProxyClassMethods {
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName) {
             String name = rubyName.asJavaString();
 
             return getRubyMethod(context, proxyClass, name);
         }
 
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
 
             return getRubyMethod(context, proxyClass, name, argTypesClasses);
         }
 
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName) {
             String name = rubyName.asJavaString();
             Ruby runtime = context.getRuntime();
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
             return method.invokeStaticDirect();
         }
 
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Ruby runtime = context.getRuntime();
 
             if (argTypesAry.size() != 0) {
                 Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]);
                 throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
             }
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
             return method.invokeStaticDirect();
         }
 
         @JRubyMethod(backtrace = true, meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes, IRubyObject arg0) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Ruby runtime = context.getRuntime();
 
             if (argTypesAry.size() != 1) {
                 throw JavaMethod.newArgSizeMismatchError(runtime, (Class) argTypesAry.eltInternal(0).toJava(Class.class));
             }
 
             Class argTypeClass = (Class) argTypesAry.eltInternal(0).toJava(Class.class);
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypeClass));
             return method.invokeStaticDirect(arg0.toJava(argTypeClass));
         }
 
         @JRubyMethod(required = 4, rest = true, backtrace = true, meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
             Ruby runtime = context.getRuntime();
 
             String name = args[0].asJavaString();
             RubyArray argTypesAry = args[1].convertToArray();
             int argsLen = args.length - 2;
 
             if (argTypesAry.size() != argsLen) {
                 throw JavaMethod.newArgSizeMismatchError(runtime, (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]));
             }
 
             Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argsLen]);
 
             Object[] argsAry = new Object[argsLen];
             for (int i = 0; i < argsLen; i++) {
                 argsAry[i] = args[i + 2].toJava(argTypesClasses[i]);
             }
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypesClasses));
             return method.invokeStaticDirect(argsAry);
         }
 
         @JRubyMethod(backtrace = true, meta = true, visibility = PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName) {
             return java_alias(context, proxyClass, newName, rubyName, context.getRuntime().newEmptyArray());
         }
 
         @JRubyMethod(backtrace = true, meta = true, visibility = PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             String newNameStr = newName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             Ruby runtime = context.getRuntime();
             RubyClass rubyClass;
 
             if (proxyClass instanceof RubyClass) {
                 rubyClass = (RubyClass)proxyClass;
             } else {
                 throw runtime.newTypeError(proxyClass, runtime.getModule());
             }
 
             Method method = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
             MethodInvoker invoker = getMethodInvokerForMethod(rubyClass, method);
 
             if (Modifier.isStatic(method.getModifiers())) {
                 // add alias to meta
                 rubyClass.getSingletonClass().addMethod(newNameStr, invoker);
             } else {
                 rubyClass.addMethod(newNameStr, invoker);
             }
 
             return runtime.getNil();
         }
     }
 
     private static IRubyObject getRubyMethod(ThreadContext context, IRubyObject proxyClass, String name, Class... argTypesClasses) {
         Ruby runtime = context.getRuntime();
         RubyClass rubyClass;
         
         if (proxyClass instanceof RubyClass) {
             rubyClass = (RubyClass)proxyClass;
         } else {
             throw runtime.newTypeError(proxyClass, runtime.getModule());
         }
 
         Method jmethod = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
         String prettyName = name + CodegenUtils.prettyParams(argTypesClasses);
         
         if (Modifier.isStatic(jmethod.getModifiers())) {
             MethodInvoker invoker = new StaticMethodInvoker(rubyClass, jmethod);
             return RubyMethod.newMethod(rubyClass, prettyName, rubyClass, name, invoker, proxyClass);
         } else {
             MethodInvoker invoker = new InstanceMethodInvoker(rubyClass, jmethod);
             return RubyUnboundMethod.newUnboundMethod(rubyClass, prettyName, rubyClass, name, invoker);
         }
     }
 
     public static Method getMethodFromClass(Ruby runtime, IRubyObject proxyClass, String name, Class... argTypes) {
         Class jclass = (Class)((JavaClass)proxyClass.callMethod(runtime.getCurrentContext(), "java_class")).getValue();
 
         try {
             return jclass.getMethod(name, argTypes);
         } catch (NoSuchMethodException nsme) {
             String prettyName = name + CodegenUtils.prettyParams(argTypes);
             String errorName = jclass.getName() + "." + prettyName;
             throw runtime.newNameError("Java method not found: " + errorName, name);
         }
     }
 
     private static MethodInvoker getMethodInvokerForMethod(RubyClass metaClass, Method method) {
         if (Modifier.isStatic(method.getModifiers())) {
             return new StaticMethodInvoker(metaClass.getMetaClass(), method);
         } else {
             return new InstanceMethodInvoker(metaClass, method);
         }
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", subclass,
                 Block.NULL_BLOCK);
         return setupJavaSubclass(tc, subclass, recv.callMethod(tc, "java_class"));
     }
 
     private static IRubyObject setupJavaSubclass(ThreadContext context, IRubyObject subclass, IRubyObject java_class) {
         Ruby runtime = context.getRuntime();
 
         if (!(subclass instanceof RubyClass)) {
             throw runtime.newTypeError(subclass, runtime.getClassClass());
         }
         RubyClass rubySubclass = (RubyClass)subclass;
         rubySubclass.getInstanceVariables().setInstanceVariable("@java_proxy_class", runtime.getNil());
 
         RubyClass subclassSingleton = rubySubclass.getSingletonClass();
         subclassSingleton.addReadWriteAttribute(context, "java_proxy_class");
         subclassSingleton.addMethod("java_interfaces", new JavaMethodZero(subclassSingleton, PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 IRubyObject javaInterfaces = self.getInstanceVariables().getInstanceVariable("@java_interfaces");
                 if (javaInterfaces != null) return javaInterfaces.dup();
                 return context.getRuntime().getNil();
             }
         });
 
         rubySubclass.addMethod("__jcreate!", new JavaMethodN(subclassSingleton, PUBLIC) {
             private final Map<Integer, ParameterTypes> methodCache = new HashMap<Integer, ParameterTypes>();
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                 IRubyObject proxyClass = self.getMetaClass().getInstanceVariables().getInstanceVariable("@java_proxy_class");
                 if (proxyClass == null || proxyClass.isNil()) {
                     proxyClass = JavaProxyClass.get_with_class(self, self.getMetaClass());
                     self.getMetaClass().getInstanceVariables().setInstanceVariable("@java_proxy_class", proxyClass);
                 }
                 
                 JavaProxyClass realProxyClass = (JavaProxyClass)proxyClass;
                 RubyArray constructors = realProxyClass.constructors();
                 ArrayList<JavaProxyConstructor> forArity = new ArrayList<JavaProxyConstructor>();
                 for (int i = 0; i < constructors.size(); i++) {
                     JavaProxyConstructor constructor = (JavaProxyConstructor)constructors.eltInternal(i);
                     if (constructor.getParameterTypes().length == args.length) {
                         forArity.add(constructor);
                     }
                 }
                 
                 if (forArity.size() == 0) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments for constructor");
                 }
 
                 JavaProxyConstructor matching = (JavaProxyConstructor)CallableSelector.matchingCallableArityN(
                         methodCache,
                         forArity.toArray(new JavaProxyConstructor[forArity.size()]), args, args.length);
 
                 if (matching == null) {
                     throw context.getRuntime().newArgumentError("wrong number of arguments for constructor");
                 }
 
                 Object[] newArgs = new Object[args.length];
                 Class[] parameterTypes = matching.getParameterTypes();
                 for (int i = 0; i < args.length; i++) {
                     newArgs[i] = args[i].toJava(parameterTypes[i]);
                 }
                 
                 JavaObject newObject = matching.newInstance(self, newArgs);
                 return JavaUtilities.set_java_object(self, self, newObject);
             }
         });
 
         return runtime.getNil();
     }
 
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Ruby runtime = proxyClass.getRuntime();
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         RubyModule parentModule;
         String className;
 
         // inner classes must be nested
         if (fullName.indexOf('$') != -1) {
             IRubyObject declClass = javaClass.declaring_class();
             if (declClass.isNil()) {
                 // no containing class for a $ class; treat it as internal and don't define a constant
                 return;
             }
             parentModule = getProxyClass(runtime, (JavaClass)declClass);
             className = clazz.getSimpleName();
         } else {
             String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
             parentModule = getJavaPackageModule(runtime, packageString);
             className = parentModule == null ? fullName : fullName.substring(endPackage + 1);
         }
         
         if (parentModule != null && IdUtil.isConstant(className)) {
             if (parentModule.getConstantAt(className) == null) {
                 parentModule.setConstant(className, proxyClass);
             }
         }
     }
 
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuilder buf = new StringBuilder();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start))).append(packageString.substring(start + 1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule) packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule) runtime.getJavaSupport().getPackageModuleTemplate().dup();
         packageModule.setInstanceVariable("@package_name", runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass) packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     private static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule) value;
         }
         String packageName;
         if ("Default".equals(name)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(name);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, name, packageName);
     }
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         return getPackageModule(recv.getRuntime(), symObject.asJavaString());
     }
 
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
 
     private static RubyModule getProxyOrPackageUnderPackage(ThreadContext context, final Ruby runtime,
-            RubyModule parentPackage, String sym) {
+            RubyModule parentPackage, String sym) throws Exception {
         IRubyObject packageNameObj = parentPackage.getInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (!Character.isUpperCase(name.charAt(0))) {
             // filter out any Java primitive names
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             }
             
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             // Haven't found a class, continue on as though it were a package
             final RubyModule packageModule = getJavaPackageModule(runtime, fullName);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
 
             // save package in singletonized parent, so we don't come back here
             memoizePackageOrClass(parentPackage, name, packageModule);
             
             return packageModule;
         } else {
-            // upper case name, so most likely a class
-            final RubyModule javaModule = getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
-
-            // save class in singletonized parent, so we don't come back here
-            memoizePackageOrClass(parentPackage, name, javaModule);
-
-            return javaModule;
+            try {
+                // First char is upper case, so assume it's a class name
+                final RubyModule javaModule = getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
+                memoizePackageOrClass(parentPackage, name, javaModule);
+
+                return javaModule;
+            } catch (Exception e) {
+                if (RubyInstanceConfig.UPPER_CASE_PACKAGE_NAME_ALLOWED) {
+                    // but for those not hip to conventions and best practices,
+                    // we'll try as a package
+                    return getJavaPackageModule(runtime, fullName);
+                } else {
+                    throw e;
+                }
+            }
 
-        // FIXME: we should also support orgs that use capitalized package
-        // names (including, embarrassingly, the one I work for), but this
-        // should be enabled by a system property, as the expected default
-        // behavior for an upper-case value should be (and is) to treat it
-        // as a class name, and raise an exception if it's not found 
-
-//            try {
-//                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
-//            } catch (Exception e) {
-//                // but for those not hip to conventions and best practices,
-//                // we'll try as a package
-//                return getJavaPackageModule(runtime, fullName);
-//            }
         }
     }
 
     public static IRubyObject get_proxy_or_package_under_package(
             ThreadContext context,
             IRubyObject recv,
             IRubyObject parentPackage,
-            IRubyObject sym) {
+            IRubyObject sym) throws Exception {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) {
             throw runtime.newTypeError(parentPackage, runtime.getModule());
         }
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(context, runtime,
                 (RubyModule) parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     private static RubyModule getTopLevelProxyOrPackage(ThreadContext context, final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
 
             // TODO: check for Java reserved names and raise exception if encountered
 
             final RubyModule packageModule = getJavaPackageModule(runtime, name);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             }
 
             memoizePackageOrClass(javaModule, name, packageModule);
             
             return packageModule;
         } else {
             RubyModule javaModule = null;
             try {
                 javaModule = getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             if (javaModule == null) {
                 javaModule = getPackageModule(runtime, name);
             }
 
             memoizePackageOrClass(runtime.getJavaSupport().getJavaModule(), name, javaModule);
 
             return javaModule;
         }
     }
 
     private static void memoizePackageOrClass(final RubyModule parentPackage, final String name, final IRubyObject value) {
         RubyClass singleton = parentPackage.getSingletonClass();
         singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, PUBLIC) {
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 if (args.length != 0) {
                     throw context.getRuntime().newArgumentError(
                             "Java package `"
                             + parentPackage.callMethod("package_name")
                             + "' does not have a method `"
                             + name
                             + "'");
                 }
                 return call(context, self, clazz, name);
             }
 
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return value;
             }
 
             @Override
             public Arity getArity() {
                 return Arity.noArguments();
             }
         });
     }
 
     public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject sym) {
         Ruby runtime = context.getRuntime();
         RubyModule result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString());
 
         return result != null ? result : runtime.getNil();
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
     
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         try {
             return JavaUtil.java_to_ruby(recv.getRuntime(), object);
         } catch (RuntimeException e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             // This point is only reached if there was an exception handler installed.
             return recv.getRuntime().getNil();
         }
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility.
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.ruby_to_java(recv, object, unusedBlock);
     }
 
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_primitive(recv, object, unusedBlock);
     }
 
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     @JRubyMethod(required = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject new_proxy_instance2(IRubyObject recv, final IRubyObject wrapper, IRubyObject ifcs, Block block) {
         IRubyObject[] javaClasses = ((RubyArray)ifcs).toJavaArray();
 
         // Create list of interface names to proxy (and make sure they really are interfaces)
         // Also build a hashcode from all classes to use for retrieving previously-created impl
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
 
         return newInterfaceImpl(wrapper, interfaces);
     }
 
     public static IRubyObject newInterfaceImpl(final IRubyObject wrapper, Class[] interfaces) {
         final Ruby runtime = wrapper.getRuntime();
         ClassDefiningClassLoader classLoader;
 
         Class[] tmp_interfaces = interfaces;
         interfaces = new Class[tmp_interfaces.length + 1];
         System.arraycopy(tmp_interfaces, 0, interfaces, 0, tmp_interfaces.length);
         interfaces[tmp_interfaces.length] = RubyObjectHolderProxy.class;
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         if (!RubyInstanceConfig.INTERFACES_USE_PROXY) {
             int interfacesHashCode = interfacesHashCode(interfaces);
             // if it's a singleton class and the real class is proc, we're doing closure conversion
             // so just use Proc's hashcode
             if (wrapper.getMetaClass().isSingleton() && wrapper.getMetaClass().getRealClass() == runtime.getProc()) {
                 interfacesHashCode = 31 * interfacesHashCode + runtime.getProc().hashCode();
                 classLoader = runtime.getJRubyClassLoader();
             } else {
                 // normal new class implementing interfaces
                 interfacesHashCode = 31 * interfacesHashCode + wrapper.getMetaClass().getRealClass().hashCode();
                 classLoader = new OneShotClassLoader(runtime.getJRubyClassLoader());
             }
             String implClassName = "org.jruby.gen.InterfaceImpl" + Math.abs(interfacesHashCode);
             Class proxyImplClass;
             try {
                 proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
             } catch (ClassNotFoundException cnfe) {
                 proxyImplClass = RealClassGenerator.createOldStyleImplClass(interfaces, wrapper.getMetaClass(), runtime, implClassName, classLoader);
             }
 
             try {
                 Constructor proxyConstructor = proxyImplClass.getConstructor(IRubyObject.class);
                 return JavaObject.wrap(runtime, proxyConstructor.newInstance(wrapper));
             } catch (NoSuchMethodException nsme) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
             } catch (InvocationTargetException ite) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
             } catch (InstantiationException ie) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
             } catch (IllegalAccessException iae) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
             }
         } else {
             Object proxyObject = Proxy.newProxyInstance(runtime.getJRubyClassLoader(), interfaces, new InvocationHandler() {
                 private Map parameterTypeCache = new ConcurrentHashMap();
 
                 public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                     String methodName = method.getName();
                     int length = nargs == null ? 0 : nargs.length;
 
                     // FIXME: wtf is this? Why would these use the class?
                     if (methodName == "toString" && length == 0) {
                         return proxy.getClass().getName();
                     } else if (methodName == "hashCode" && length == 0) {
                         return Integer.valueOf(proxy.getClass().hashCode());
                     } else if (methodName == "equals" && length == 1) {
                         Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                         if (parameterTypes == null) {
                             parameterTypes = method.getParameterTypes();
                             parameterTypeCache.put(method, parameterTypes);
                         }
                         if (parameterTypes[0].equals(Object.class)) {
                             return Boolean.valueOf(proxy == nargs[0]);
                         }
                     } else if (methodName == "__ruby_object" && length == 0) {
                         return wrapper;
                     }
 
                     IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, nargs);
                     try {
                         return RuntimeHelpers.invoke(runtime.getCurrentContext(), wrapper, methodName, rubyArgs).toJava(method.getReturnType());
                     } catch (RuntimeException e) { e.printStackTrace(); throw e; }
                 }
             });
             return JavaObject.wrap(runtime, proxyObject);
         }
     }
 
     public static Class generateRealClass(final RubyClass clazz) {
         final Ruby runtime = clazz.getRuntime();
         final Class[] interfaces = getInterfacesFromRubyClass(clazz);
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         int interfacesHashCode = interfacesHashCode(interfaces);
         // normal new class implementing interfaces
         interfacesHashCode = 31 * interfacesHashCode + clazz.hashCode();
         
         String implClassName;
         if (clazz.getBaseName() == null) {
             // no-name class, generate a bogus name for it
             implClassName = "anon_class" + Math.abs(System.identityHashCode(clazz)) + "_" + Math.abs(interfacesHashCode);
         } else {
             implClassName = clazz.getName().replaceAll("::", "\\$\\$") + "_" + Math.abs(interfacesHashCode);
         }
         Class proxyImplClass;
         try {
             proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
         } catch (ClassNotFoundException cnfe) {
             // try to use super's reified class; otherwise, RubyObject (for now)
             Class superClass = clazz.getSuperClass().getRealClass().getReifiedClass();
             if (superClass == null) {
                 superClass = RubyObject.class;
             }
             proxyImplClass = RealClassGenerator.createRealImplClass(superClass, interfaces, clazz, runtime, implClassName);
             
             // add a default initialize if one does not already exist and this is a Java-hierarchy class
             if (NEW_STYLE_EXTENSION &&
                     !(RubyBasicObject.class.isAssignableFrom(proxyImplClass) || clazz.getMethods().containsKey("initialize"))
                     ) {
                 clazz.addMethod("initialize", new JavaMethodZero(clazz, PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                         return context.getRuntime().getNil();
                     }
                 });
             }
         }
         clazz.setReifiedClass(proxyImplClass);
         clazz.setRubyClassAllocator(proxyImplClass);
 
         return proxyImplClass;
     }
 
     public static Constructor getRealClassConstructor(Ruby runtime, Class proxyImplClass) {
         try {
             return proxyImplClass.getConstructor(Ruby.class, RubyClass.class);
         } catch (NoSuchMethodException nsme) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
         }
     }
 
     public static IRubyObject constructProxy(Ruby runtime, Constructor proxyConstructor, RubyClass clazz) {
         try {
             return (IRubyObject)proxyConstructor.newInstance(runtime, clazz);
         } catch (InvocationTargetException ite) {
             ite.printStackTrace();
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
         } catch (InstantiationException ie) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
         }
     }
     
     public static IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
         IRubyObject proxy = clazz.allocate();
         if (proxy instanceof JavaProxy) {
             ((JavaProxy)proxy).setObject(javaObject);
         } else {
             JavaObject wrappedObject = JavaObject.wrap(clazz.getRuntime(), javaObject);
             proxy.dataWrapStruct(wrappedObject);
         }
 
         return proxy;
     }
 
     public static IRubyObject wrapJavaObject(Ruby runtime, Object object) {
         return allocateProxy(object, getProxyClassForObject(runtime, object));
     }
 
     public static Class[] getInterfacesFromRubyClass(RubyClass klass) {
         Set<Class> interfaces = new HashSet<Class>();
         // walk all superclasses aggregating interfaces
         while (klass != null) {
             IRubyObject maybeInterfaces = klass.getInstanceVariables().getInstanceVariable("@java_interfaces");
             if (maybeInterfaces instanceof RubyArray) {
                 RubyArray moreInterfaces = (RubyArray)maybeInterfaces;
                 if (!moreInterfaces.isFrozen()) moreInterfaces.setFrozen(true);
 
                 interfaces.addAll(moreInterfaces);
             }
             klass = klass.getSuperClass();
         }
 
         return interfaces.toArray(new Class[interfaces.size()]);
     }
     
     private static int interfacesHashCode(Class[] a) {
         if (a == null) {
             return 0;
         }
 
         int result = 1;
 
         for (Class element : a)
             result = 31 * result + (element == null ? 0 : element.hashCode());
 
         return result;
     }
 }
diff --git a/src/org/jruby/util/cli/OutputStrings.java b/src/org/jruby/util/cli/OutputStrings.java
index cc6a586047..d635a3b202 100644
--- a/src/org/jruby/util/cli/OutputStrings.java
+++ b/src/org/jruby/util/cli/OutputStrings.java
@@ -1,213 +1,215 @@
 package org.jruby.util.cli;
 
 import jnr.posix.util.Platform;
 import org.jruby.CompatVersion;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.runtime.Constants;
 import org.jruby.util.SafePropertyAccessor;
 
 public class OutputStrings {
     public static String getBasicUsageHelp() {
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
                 .append("  -Eex[:in]       specify the default external and internal character encodings\n")
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
                 .append("  -U              use UTF-8 as default internal encoding\n")
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
 
     public static String getExtendedHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("Extended options:\n")
                 .append("  -X-O        run with ObjectSpace disabled (default; improves performance)\n")
                 .append("  -X+O        run with ObjectSpace enabled (reduces performance)\n")
                 .append("  -X-C        disable all compilation\n")
                 .append("  -X+C        force compilation of all scripts before they are run (except eval)\n");
 
         return sb.toString();
     }
 
     public static String getPropertyHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.\n")
                 .append("Specify them by passing -X<property>=<value>\n")
                 .append("  or if passing directly to Java, -Djruby.<property>=<value>\n")
                 .append("\nCOMPILER SETTINGS:\n")
                 .append("    compile.mode=JIT|FORCE|OFF\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables\n")
                 .append("    compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    compile.dynopt=true|false\n")
                 .append("       (EXPERIMENTAL) Use interpreter to help compiler make direct calls. Default is false\n")
                 .append("    compile.fastops=true|false\n")
                 .append("       Turn on fast operators for Fixnum and Float. Default is true\n")
                 .append("    compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is ").append(RubyInstanceConfig.CHAINED_COMPILE_LINE_COUNT_DEFAULT).append("\n")
                 .append("    compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.\n")
                 .append("    compile.peephole=true|false\n")
                 .append("       Enable or disable peephole optimizations. Default is true (on).\n")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is ").append(RubyInstanceConfig.JIT_THRESHOLD).append(".\n")
                 .append("    jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is ").append(RubyInstanceConfig.JIT_MAX_METHODS_LIMIT).append(" per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is ").append(RubyInstanceConfig.JIT_MAX_SIZE_LIMIT).append(".\n")
                 .append("    jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("    jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
                 .append("    jit.cache=true|false\n")
                 .append("       Cache jitted method in-memory bodies across runtimes and loads. Default is true.\n")
                 .append("    jit.codeCache=<dir>\n")
                 .append("       Save jitted methods to <dir> as they're compiled, for future runs.\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("\nTHREAD POOLING:\n")
                 .append("    thread.pool.enabled=true|false\n")
                 .append("       Enable reuse of native backing threads via a thread pool. Default is false.\n")
                 .append("    thread.pool.min=<min thread count>\n")
                 .append("       The minimum number of threads to keep alive in the pool. Default is 0.\n")
                 .append("    thread.pool.max=<max thread count>\n")
                 .append("       The maximum number of threads to allow in the pool. Default is unlimited.\n")
                 .append("    thread.pool.ttl=<time to live, in seconds>\n")
                 .append("       The maximum number of seconds to keep alive an idle thread. Default is 60.\n")
                 .append("\nMISCELLANY:\n")
                 .append("    compat.version=1.8|1.9\n")
                 .append("       Specify the major Ruby version to be compatible with; Default is RUBY1_8\n")
                 .append("    objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is false.\n")
                 .append("    jump.backtrace=true|false\n")
                 .append("       Make non-local flow jumps generate backtraces. Default is false.\n")
                 .append("    process.noUnwrap=true|false\n")
                 .append("       Do not unwrap process streams (IBM Java 6 issue). Default is false.\n")
                 .append("    reify.classes=true|false\n")
                 .append("       Before instantiation, stand up a real Java class for ever Ruby class. Default is false. \n")
                 .append("    reify.logErrors=true|false\n")
                 .append("       Log errors during reification (reify.classes=true). Default is false. \n")
                 .append("    reflected.handles=true|false\n")
                 .append("       Use reflection for binding methods, not generated bytecode. Default is false.\n")
                 .append("    backtrace.color=true|false\n")
                 .append("       Enable colorized backtraces. Default is false.\n")
                 .append("\nDEBUGGING/LOGGING:\n")
                 .append("    debug.loadService=true|false\n")
                 .append("       LoadService logging\n")
                 .append("    debug.loadService.timing=true|false\n")
                 .append("       Print load timings for each require'd library. Default is false.\n")
                 .append("    debug.launch=true|false\n")
                 .append("       ShellLauncher logging\n")
                 .append("    debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n")
                 .append("    debug.scriptResolution=true|false\n")
                 .append("       Print which script is executed by '-S' flag. Default is false.\n")
                 .append("    errno.backtrace=true|false\n")
                 .append("       Generate backtraces for heavily-used Errno exceptions (EAGAIN). Default is false.\n")
                 .append("\nJAVA INTEGRATION:\n")
                 .append("    ji.setAccessible=true|false\n")
                 .append("       Try to set inaccessible Java methods to be accessible. Default is true.\n")
+                .append("    ji.upper.case.package.name.allowed=true|false\n")
+                .append("       Allow Capitalized Java pacakge names. Default is false.\n")
                 .append("    interfaces.useProxy=true|false\n")
                 .append("       Use java.lang.reflect.Proxy for interface impl. Default is false.\n");
 
         return sb.toString();
     }
 
     public static String getVersionString(CompatVersion compatVersion) {
         String ver = null;
         String patchDelimeter = "-p";
         int patchlevel = 0;
         switch (compatVersion) {
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
                 "jruby %s (ruby-%s%s%d) (%s %s) (%s %s) [%s-%s-java]",
                 Constants.VERSION, ver, patchDelimeter, patchlevel,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 System.getProperty("java.vm.name"), System.getProperty("java.version"),
                 Platform.getOSName(),
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public static String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2011 The JRuby Community (and contribs)";
     }
 }
