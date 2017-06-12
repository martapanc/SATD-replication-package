diff --git a/spec/tags/1.9/ruby/core/time/gm_tags.txt b/spec/tags/1.9/ruby/core/time/gm_tags.txt
deleted file mode 100644
index 030242543f..0000000000
--- a/spec/tags/1.9/ruby/core/time/gm_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails:Time.gm handles fractional microseconds as a Float
-fails:Time.gm handles fractional microseconds as a Rational
-fails:Time.gm ignores fractional seconds if a passed whole number of microseconds
-fails:Time.gm ignores fractional seconds if a passed fractional number of microseconds
diff --git a/spec/tags/1.9/ruby/core/time/local_tags.txt b/spec/tags/1.9/ruby/core/time/local_tags.txt
deleted file mode 100644
index f182c87e79..0000000000
--- a/spec/tags/1.9/ruby/core/time/local_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails:Time.local handles fractional microseconds as a Float
-fails:Time.local handles fractional microseconds as a Rational
-fails:Time.local ignores fractional seconds if a passed whole number of microseconds
-fails:Time.local ignores fractional seconds if a passed fractional number of microseconds
diff --git a/spec/tags/1.9/ruby/core/time/mktime_tags.txt b/spec/tags/1.9/ruby/core/time/mktime_tags.txt
deleted file mode 100644
index f69d56d443..0000000000
--- a/spec/tags/1.9/ruby/core/time/mktime_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails:Time.mktime handles fractional microseconds as a Float
-fails:Time.mktime handles fractional microseconds as a Rational
-fails:Time.mktime ignores fractional seconds if a passed whole number of microseconds
-fails:Time.mktime ignores fractional seconds if a passed fractional number of microseconds
diff --git a/spec/tags/1.9/ruby/core/time/nsec_tags.txt b/spec/tags/1.9/ruby/core/time/nsec_tags.txt
deleted file mode 100644
index 325b522120..0000000000
--- a/spec/tags/1.9/ruby/core/time/nsec_tags.txt
+++ /dev/null
@@ -1,5 +0,0 @@
-fails:Time#nsec returns the nanoseconds part of a Time constructed with a Float number of seconds
-fails:Time#nsec returns the nanoseconds part of a Time constructed with an Integer number of microseconds
-fails:Time#nsec returns the nanoseconds part of a Time constructed with an Float number of microseconds
-fails:Time#nsec returns the nanoseconds part of a Time constructed with a Rational number of seconds
-fails:Time#nsec returns the nanoseconds part of a Time constructed with an Rational number of microseconds
diff --git a/spec/tags/1.9/ruby/core/time/strftime_tags.txt b/spec/tags/1.9/ruby/core/time/strftime_tags.txt
deleted file mode 100644
index d82062e270..0000000000
--- a/spec/tags/1.9/ruby/core/time/strftime_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails:Time#strftime returns the fractional seconds digits, default is 9 digits (nanosecond) with %N
diff --git a/spec/tags/1.9/ruby/core/time/subsec_tags.txt b/spec/tags/1.9/ruby/core/time/subsec_tags.txt
deleted file mode 100644
index 641c304a11..0000000000
--- a/spec/tags/1.9/ruby/core/time/subsec_tags.txt
+++ /dev/null
@@ -1,6 +0,0 @@
-fails:Time#subsec returns 0 as a Fixnum for a Time with a whole number of seconds
-fails:Time#subsec returns the fractional seconds as a Rational for a Time constructed with a Rational number of seconds
-fails:Time#subsec returns the fractional seconds as a Rational for a Time constructed with a Float number of seconds
-fails:Time#subsec returns the fractional seconds as a Rational for a Time constructed with an Integer number of microseconds
-fails:Time#subsec returns the fractional seconds as a Rational for a Time constructed with an Rational number of microseconds
-fails:Time#subsec returns the fractional seconds as a Rational for a Time constructed with an Float number of microseconds
diff --git a/spec/tags/1.9/ruby/core/time/utc_tags.txt b/spec/tags/1.9/ruby/core/time/utc_tags.txt
deleted file mode 100644
index e6cbb71e37..0000000000
--- a/spec/tags/1.9/ruby/core/time/utc_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails:Time.utc handles fractional microseconds as a Float
-fails:Time.utc handles fractional microseconds as a Rational
-fails:Time.utc ignores fractional seconds if a passed whole number of microseconds
-fails:Time.utc ignores fractional seconds if a passed fractional number of microseconds
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index f5e0aaf90c..4b8ad1b3c6 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2045,2000 +2045,2004 @@ public final class Ruby {
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
     
     public RubyClass getRandomClass() {
         return randomClass;
     }
     void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
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
     public void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     public void setGroupStruct(RubyClass groupStruct) {
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
     
     public RubyHash getENV() {
         return envObject;
     }
     
     public void setENV(RubyHash env) {
         envObject = env;
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
 
     public RubyClass getKeyError() {
         return keyError;
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
 
     private RubyRandom.RandomType defaultRand;
     public RubyRandom.RandomType getDefaultRand() {
         return defaultRand;
     }
     
     public void setDefaultRand(RubyRandom.RandomType defaultRand) {
         this.defaultRand = defaultRand;
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
 
     public Node parseFileFromMain(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 0, false, false, true, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         ParserConfiguration parserConfig =
                 new ParserConfiguration(this, 0, false, true, false, config);
         if (is1_9) parserConfig.setDefaultEncoding(getEncodingService().getLocaleEncoding());
         return parser.parse(file, in, scope, parserConfig);
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, false, config));
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
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(config.getTraceType().printBacktrace(excp, errorStream == System.err && getPosix().isatty(FileDescriptor.err)));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             ThreadContext.pushBacktrace(context, "(root)", file, 0);
             context.preNodeEval(objectClass, self, scriptName);
 
             Node node = parseFile(in, scriptName, null);
             if (wrap) {
                 // toss an anonymous module into the search path
                 ((RootNode)node).getStaticScope().setModule(RubyModule.newModule(this));
             }
             runInterpreter(context, node, self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             ThreadContext.popBacktrace(context);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
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
                         LOG.info("found jitted code for " + filename + " at class: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         LOG.info("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         LOG.info("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         LOG.info("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
 
             if (script == null) {
                 failForcedCompile(scriptNode);
 
                 runInterpreter(scriptNode);
             } else {
                 runScript(script, wrap);
             }
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     public void loadScript(Script script) {
         loadScript(script, false);
     }
 
     public void loadScript(Script script, boolean wrap) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
             
             script.load(context, self, wrap);
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     /**
      * Load the given BasicLibraryService instance, wrapping it in Ruby framing
      * to ensure it is isolated from any parent scope.
      * 
      * @param extName The name of the extension, to go on the frame wrapping it
      * @param extension The extension object to load
      * @param wrap Whether to use a new "self" for toplevel
      */
     public void loadExtension(String extName, BasicLibraryService extension, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preExtensionLoad(self);
 
             extension.basicLoad(this);
         } catch (IOException ioe) {
             throw newIOErrorFromException(ioe);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public void addBoundMethod(String className, String methodName, String rubyName) {
         Map<String, String> javaToRuby = boundMethods.get(className);
         if (javaToRuby == null) {
             javaToRuby = new HashMap<String, String>();
             boundMethods.put(className, javaToRuby);
         }
         javaToRuby.put(methodName, rubyName);
     }
 
     public Map<String, Map<String, String>> getBoundMethods() {
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
         getBeanManager().unregisterRuntime();
 
         getSelectorPool().cleanup();
 
         if (getJRubyClassLoader() != null) {
             getJRubyClassLoader().tearDown(isDebug());
         }
 
         if (config.isProfilingEntireRun()) {
             // not using logging because it's formatted
             System.err.println("\nmain thread profile results:");
             IProfileData profileData = (IProfileData) threadService.getMainThread().getContext().getProfileData();
             config.makeDefaultProfilePrinter(profileData).printProfile(System.err);
         }
 
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
         return RubyFixnum.newFixnum(this, value.intValue());
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
 
+    public RubyRational newRationalReduced(long num, long den) {
+        return (RubyRational)RubyRational.newRationalConvert(getCurrentContext(), newFixnum(num), newFixnum(den));
+    }
+
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
 
     public RubySymbol newSymbol(ByteList name) {
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
         return newRaiseException(getErrno().getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().getClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), "Operation now in progress");
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError() {
         return newLightweightErrnoException(getModule("JRuby").getClass("EINPROGRESSWritable"), "");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().getClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().getClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().getClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().getClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().getClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), message);
     }
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().getClass("EHOSTUNREACH"), message);
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().getClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().getClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newLightweightErrnoException(getErrno().getClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEAGAINReadableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EAGAINReadable"), message);
     }
 
     public RaiseException newErrnoEAGAINWritableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EAGAINWritable"), message);
     }
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().getClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().getClass("EPERM"), "Operation not permitted - " + name);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().getClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().getClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EINPROGRESSWritable"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().getClass("EISCONN"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().getClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTEMPTYError(String message) {
         return newRaiseException(getErrno().getClass("ENOTEMPTY"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().getClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().getClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().getClass("ENOTCONN"), "Socket is not connected");
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().getClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().getClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().getClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().getClass("EDOM"), "Domain error - " + message);
     }   
     
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().getClass("ECHILD"), "No child processes");
     }    
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().getClass("EADDRNOTAVAIL"), message);
     }
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().getClass("ESRCH"), null);
     }
 
     public RaiseException newErrnoEWOULDBLOCKError() {
         return newRaiseException(getErrno().getClass("EWOULDBLOCK"), null);
     }
 
     public RaiseException newErrnoEDESTADDRREQError(String func) {
         return newRaiseException(getErrno().getClass("EDESTADDRREQ"), func);
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
 
     public RaiseException newKeyError(String message) {
         return newRaiseException(getKeyError(), message);
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
 
     public RaiseException newErrnoFromInt(int errno) {
         Errno errnoObj = Errno.valueOf(errno);
         if (errnoObj == null) {
             return newSystemCallError("Unknown Error (" + errno + ")");
         }
         String message = errnoObj.description();
         return newErrnoFromInt(errno, message);
     }
 
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
 
     public RaiseException newErrnoEADDRFromBindException(BindException be) {
         String msg = be.getMessage();
         if (msg == null) {
             msg = "bind";
         } else {
             msg = "bind - " + msg;
         }
         // This is ugly, but what can we do, Java provides the same BindingException
         // for both EADDRNOTAVAIL and EADDRINUSE, so we differentiate the errors
         // based on BindException's message.
         if(ADDR_NOT_AVAIL_PATTERN.matcher(msg).find()) {
             return newErrnoEADDRNOTAVAILError(msg);
         } else {
             return newErrnoEADDRINUSEError(msg);
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
         return newRaiseException(fastGetClass("Iconv").getClass("InvalidEncoding"), message);
     }
     
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").getClass("IllegalSequence"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, true);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (origException != null) {
             if (printWhenVerbose && isVerbose()) {
                 LOG.error(origException.getMessage(), origException);
             } else if (isDebug()) {
                 LOG.debug(origException.getMessage(), origException);
             }
         }
         
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), false);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
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
             LOG.debug(soe.getMessage(), soe);
         }
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getIOError(), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getStandardError(), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         if (ioe instanceof ClosedChannelException) {
             throw newIOError("closed stream");
         }
 
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer") ||
                     (Platform.IS_WINDOWS && ioe.getMessage().contains("connection was aborted"))) {
                 throw newErrnoECONNRESETError();
             }
             return newRaiseException(getIOError(), ioe.getMessage());
         } else {
             return newRaiseException(getIOError(), "IO Error");
         }
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyModule expectedType) {
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, String expectedType) {
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
     public RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         return new RaiseException(this, exceptionClass, message, true);
     }
 
     /**
      * Generate one of the ERRNO exceptions. This differs from the normal logic
      * by avoiding the generation of a backtrace. Many ERRNO values are expected,
      * such as EAGAIN, and JRuby pays a very high cost to generate backtraces that
      * are never used. The flags -Xerrno.backtrace=true or the property
      * jruby.errno.backtrace=true forces all errno exceptions to generate a backtrace.
      * 
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newLightweightErrnoException(RubyClass exceptionClass, String message) {
         if (RubyInstanceConfig.ERRNO_BACKTRACE) {
             return new RaiseException(this, exceptionClass, message, true);
         } else {
             return new RaiseException(this, exceptionClass, ERRNO_BACKTRACE_MESSAGE, RubyArray.newEmptyArray(this), true);
         }
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
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
 
     public int getFilenoIntMapSize() {
         return filenoIntExtMap.size();
     }
 
     public void removeFilenoIntMap(int internal) {
         filenoIntExtMap.remove(internal);
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
 
     private void recursiveListClear() {
         Map<String, RubyHash> hash = recursive.get();
         if(hash != null) {
             hash.clear();
         }
     }
 
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
         boolean outermost = outer && !recursiveCheck(p.list, recursiveKey.get(), null);
         if(recursiveCheck(p.list, p.objid, pairid)) {
             if(outer && !outermost) {
                 throw new RecursiveError(p.list);
             }
             return func.call(obj, true); 
         } else {
             IRubyObject result = null;
             p.func = func;
             p.obj = obj;
             p.pairid = pairid;
 
             if(outermost) {
                 recursivePush(p.list, recursiveKey.get(), null);
                 try {
                     result = execRecursiveI(p);
                 } catch(RecursiveError e) {
                     if(e.tag != p.list) {
                         throw e;
                     } else {
                         result = p.list;
                     }
                 }
                 recursivePop(p.list, recursiveKey.get(), null);
                 if(result == p.list) {
                     result = func.call(obj, true);
                 }
             } else {
                 result = execRecursiveI(p);
             }
 
             return result;
         }
     }
 
     /**
      * Perform a recursive walk on the given object using the given function.
      *
      * Do not call this method directly unless you know you're within a call
      * to {@link Ruby#recursiveListOperation(java.util.concurrent.Callable) recursiveListOperation},
      * which will ensure the thread-local recursion tracking data structs are
      * cleared.
      *
      * MRI: rb_exec_recursive
      *
      * Calls func(obj, arg, recursive), where recursive is non-zero if the
      * current method is called recursively on obj
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         if (!inRecursiveListOperation.get()) {
             throw newThreadError("BUG: execRecursive called outside recursiveListOperation");
         }
         return execRecursiveInternal(func, obj, null, false);
     }
 
     /**
      * Perform a recursive walk on the given object using the given function.
      * Treat this as the outermost call, cleaning up recursive structures.
      *
      * MRI: rb_exec_recursive_outer
      *
      * If recursion is detected on the current method and obj, the outermost
      * func will be called with (obj, arg, Qtrue). All inner func will be
      * short-circuited using throw.
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         try {
             return execRecursiveInternal(func, obj, null, true);
         } finally {
             recursiveListClear();
         }
     }
 
     /**
      * Begin a recursive walk that may make one or more calls to
      * {@link Ruby#execRecursive(org.jruby.Ruby.RecursiveFunction, org.jruby.runtime.builtin.IRubyObject) execRecursive}.
      * Clean up recursive structures once complete.
      *
      * @param body
      * @param <T>
      * @return
      */
     public <T extends IRubyObject> T recursiveListOperation(Callable<T> body) {
         try {
             inRecursiveListOperation.set(true);
             return body.call();
         } catch (Exception e) {
             UnsafeFactory.getUnsafe().throwException(e);
             return null; // not reached
         } finally {
             recursiveListClear();
             inRecursiveListOperation.set(false);
         }
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
         this.objectSpaceEnabled = objectSpaceEnabled;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         return config.getJRubyHome();
     }
 
     public void setJRubyHome(String home) {
         config.setJRubyHome(home);
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
     }
 
     public boolean is1_9() {
         return is1_9;
     }
 
     public boolean is2_0() {
         return is2_0;
     }
 
     /** GET_VM_STATE_VERSION */
     public long getGlobalState() {
         synchronized(this) {
             return globalState;
         }
     }
 
     /** INC_VM_STATE_VERSION */
     public void incGlobalState() {
         synchronized(this) {
             globalState = (globalState+1) & 0x8fffffff;
         }
     }
 
     public static boolean isSecurityRestricted() {
         return securityRestricted;
     }
     
     public static void setSecurityRestricted(boolean restricted) {
         securityRestricted = restricted;
     }
     
     public POSIX getPosix() {
         return posix;
     }
     
     public void setRecordSeparatorVar(GlobalVariable recordSeparatorVar) {
         this.recordSeparatorVar = recordSeparatorVar;
     }
     
     public GlobalVariable getRecordSeparatorVar() {
         return recordSeparatorVar;
     }
     
     public Set<Script> getJittedMethods() {
         return jittedMethods;
     }
     
     public ExecutorService getExecutor() {
         return executor;
     }
 
     public Map<String, DateTimeZone> getTimezoneCache() {
         return timeZoneCache;
     }
 
     @Deprecated
     public int getConstantGeneration() {
         return -1;
     }
 
     @Deprecated
     public synchronized void incrementConstantGeneration() {
         constantInvalidator.invalidate();
     }
     
     public Invalidator getConstantInvalidator() {
         return constantInvalidator;
     }
     
     public void invalidateConstants() {
         
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
             }
         }
     }
 
     /**
      * Get a new serial number for a new DynamicMethod instance
      * @return a new serial number
      */
     public long getNextDynamicMethodSerial() {
         return dynamicMethodSerial.getAndIncrement();
     }
 
     /**
      * Get a new generation number for a module or class.
      *
      * @return a new generation number
      */
     public int getNextModuleGeneration() {
         return moduleGeneration.incrementAndGet();
     }
 
     /**
      * Get the global object used to synchronize class-hierarchy modifications like
      * cache invalidation, subclass sets, and included hierarchy sets.
      *
      * @return The object to use for locking when modifying the hierarchy
      */
     public Object getHierarchyLock() {
         return hierarchyLock;
     }
 
     /**
      * Get the runtime-global selector pool
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index f57adf899e..174847f039 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,1132 +1,1182 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.PRIVATE;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.RubyDateFormat;
 import static org.jruby.CompatVersion.*;
 
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.OP_CMP;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 @JRubyClass(name="Time", include="Comparable")
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
     private DateTime dt;
-    private long usec;
+    private long nsec;
     
     private final static DateTimeFormatter ONE_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TWO_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER_19 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER_19 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withLocale(Locale.ENGLISH);
     // There are two different popular TZ formats: legacy (AST+3:00:00, GMT-3), and
     // newer one (US/Pacific, America/Los_Angeles). This pattern is to detect
     // the legacy TZ format in order to convert it to the newer format
     // understood by Java API.
     private static final Pattern TZ_PATTERN
             = Pattern.compile("(\\D+?)([\\+-]?)(\\d+)(:\\d+)?(:\\d+)?");
     
     private static final Pattern TIME_OFFSET_PATTERN
             = Pattern.compile("([\\+-])(\\d\\d):(\\d\\d)");
 
     private static final ByteList TZ_STRING = ByteList.create("TZ");
     
     /* JRUBY-3560
      * joda-time disallows use of three-letter time zone IDs.
      * Since MRI accepts these values, we need to translate them.
      */
     private static final Map<String, String> LONG_TZNAME = new HashMap<String, String>() {{
         put("MET", "CET"); // JRUBY-2579
         put("ROC", "Asia/Taipei"); // Republic of China
         put("WET", "Europe/Lisbon"); // Western European Time
         
     }};
     
     /* Some TZ values need to be overriden for Time#zone
      */
     private static final Map<String, String> SHORT_STD_TZNAME = new HashMap<String, String>() {{
         put("Etc/UCT", "UCT");
         put("MET", "MET"); // needs to be overriden
         put("UCT","UCT");
     }};
 
     private static final Map<String, String> SHORT_DL_TZNAME = new HashMap<String, String>() {{
         put("Etc/UCT", "UCT");
         put("MET", "MEST"); // needs to be overriden
         put("UCT","UCT");
     }};
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.TIME;
     }
     
     private static IRubyObject getEnvTimeZone(Ruby runtime) {
         RubyString tzVar = runtime.newString(TZ_STRING);
         RubyHash h = ((RubyHash)runtime.getObject().getConstant("ENV"));
         IRubyObject tz = h.op_aref(runtime.getCurrentContext(), tzVar);
         return tz;
     }
 
     public static DateTimeZone getLocalTimeZone(Ruby runtime) {
         IRubyObject tz = getEnvTimeZone(runtime);
 
         if (tz == null || ! (tz instanceof RubyString)) {
             return DateTimeZone.getDefault();
         } else {
             return getTimeZone(runtime, tz.toString());
         }
     }
      
     public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
         DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);
 
         if (cachedZone != null) return cachedZone;
 
         String originalZone = zone;
         TimeZone tz = TimeZone.getTimeZone(getEnvTimeZone(runtime).toString());
 
         // Value of "TZ" property is of a bit different format,
         // which confuses the Java's TimeZone.getTimeZone(id) method,
         // and so, we need to convert it.
 
         Matcher tzMatcher = TZ_PATTERN.matcher(zone);
         if (tzMatcher.matches()) {                    
             String sign = tzMatcher.group(2);
             String hours = tzMatcher.group(3);
             String minutes = tzMatcher.group(4);
                 
             // GMT+00:00 --> Etc/GMT, see "MRI behavior"
             // comment below.
             if (("00".equals(hours) || "0".equals(hours)) &&
                     (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
                 zone = "Etc/GMT";
             } else {
                 // Invert the sign, since TZ format and Java format
                 // use opposite signs, sigh... Also, Java API requires
                 // the sign to be always present, be it "+" or "-".
                 sign = ("-".equals(sign)? "+" : "-");
 
                 // Always use "GMT" since that's required by Java API.
                 zone = "GMT" + sign + hours;
 
                 if (minutes != null) {
                     zone += minutes;
                 }
             }
             
             tz = TimeZone.getTimeZone(zone);
         } else {
             if (LONG_TZNAME.containsKey(zone)) tz.setID(LONG_TZNAME.get(zone.toUpperCase()));
         }
 
         // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
         // is *NOT* considered as a proper GMT/UTC time:
         //   ENV['TZ']="GMT"
         //   Time.now.gmt? ==> false
         //   ENV['TZ']="UTC"
         //   Time.now.utc? ==> false
         // Hence, we need to adjust for that.
         if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
             zone = "Etc/" + zone;
             tz = TimeZone.getTimeZone(zone);
         }
 
         DateTimeZone dtz = DateTimeZone.forTimeZone(tz);
         runtime.getTimezoneCache().put(originalZone, dtz);
         return dtz;
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, DateTime dt) {
         super(runtime, rubyClass);
         this.dt = dt;
     }
 
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             DateTimeZone dtz = getLocalTimeZone(runtime);
             DateTime dt = new DateTime(dtz);
             RubyTime rt =  new RubyTime(runtime, klass, dt);
-            rt.setUSec(0);
+            rt.setNSec(0);
 
             return rt;
         }
     };
 
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
 
         timeClass.index = ClassIndex.TIME;
         timeClass.setReifiedClass(RubyTime.class);
         
         runtime.setTime(timeClass);
         
         timeClass.includeModule(runtime.getComparable());
         
         timeClass.defineAnnotatedMethods(RubyTime.class);
         
         return timeClass;
     }
     
+    public void setNSec(long nsec) {
+        this.nsec = nsec;
+    }
+
+    public long getNSec() {
+        return nsec;
+    }
+
     public void setUSec(long usec) {
-        this.usec = usec;
+        this.nsec = 1000 * usec;
     }
     
     public long getUSec() {
-        return usec;
+        return nsec / 1000;
     }
     
     public void updateCal(DateTime dt) {
         this.dt = dt;
     }
     
     protected long getTimeInMillis() {
-        return dt.getMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
+        return dt.getMillis();
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         return newTime(runtime, new DateTime(milliseconds));
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt) {
         return new RubyTime(runtime, runtime.getTime(), dt);
     }
     
-    public static RubyTime newTime(Ruby runtime, DateTime dt, long usec) {
+    public static RubyTime newTime(Ruby runtime, DateTime dt, long nsec) {
         RubyTime t = new RubyTime(runtime, runtime.getTime(), dt);
-        t.setUSec(usec);
+        t.setNSec(nsec);
         return t;
     }
     
     @Override
     public Class<?> getJavaClass() {
         return Date.class;
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         // We can just use dt, since it is immutable
         dt = originalTime.dt;
-        usec = originalTime.usec;
+        nsec = originalTime.nsec;
         
         return this;
     }
 
     @JRubyMethod(name = "succ")
     public RubyTime succ() {
         return newTime(getRuntime(),dt.plusSeconds(1));
     }
 
     @JRubyMethod(name = {"gmtime", "utc"})
     public RubyTime gmtime() {
         dt = dt.withZone(DateTimeZone.UTC);
         return this;
     }
 
     @JRubyMethod(name = "localtime")
     public RubyTime localtime() {
         dt = dt.withZone(getLocalTimeZone(getRuntime()));
         return this;
     }
     
     @JRubyMethod(name = "localtime", optional = 1, compat = RUBY1_9)
     public RubyTime localtime19(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) return localtime();
         String offset = args[0].asJavaString();
 
         Matcher offsetMatcher = TIME_OFFSET_PATTERN.matcher(offset);
         if (! offsetMatcher.matches()) {
             throw context.getRuntime().newArgumentError("\"+HH:MM\" or \"-HH:MM\" expected for utc_offset");
         }
 
         String sign = offsetMatcher.group(1);
         String hours = offsetMatcher.group(2);
         String minutes = offsetMatcher.group(3);
         String zone;
 
         if ("00".equals(hours) && "00".equals(minutes)) {
             zone = "Etc/GMT";
         } else {
             // Java needs the sign inverted
             String sgn = "+".equals(sign) ? "-" : "+";
             zone = "GMT" + sgn + hours + minutes;
         }
 
         DateTimeZone dtz = getTimeZone(context.getRuntime(), zone);
-        return newTime(context.getRuntime(), dt.withZone(dtz), usec);
+        return newTime(context.getRuntime(), dt.withZone(dtz), nsec);
     }
     
     @JRubyMethod(name = {"gmt?", "utc?", "gmtime?"})
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(dt.getZone().getID().equals("UTC"));
     }
     
     @JRubyMethod(name = {"getgm", "getutc"})
     public RubyTime getgm() {
         return newTime(getRuntime(), dt.withZone(DateTimeZone.UTC), getUSec());
     }
 
     @JRubyMethod(name = "getlocal")
     public RubyTime getlocal() {
         return newTime(getRuntime(), dt.withZone(getLocalTimeZone(getRuntime())), getUSec());
     }
 
     @JRubyMethod(name = "strftime", required = 1)
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US, getRuntime().is1_9());
         rubyDateFormat.applyPattern(format.convertToString().getUnicodeValue());
         rubyDateFormat.setDateTime(dt);
+        rubyDateFormat.setNSec(nsec);
         String result = rubyDateFormat.format(null);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "==", required = 1, compat= CompatVersion.RUBY1_9)
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (other.isNil()) {
             return RubyBoolean.newBoolean(getRuntime(), false);
         } else if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) == 0);
         }
 
         return RubyComparable.op_equal(context, this, other);
     }
     
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(context, this, other);
     }
     
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(context, this, other);
     }
     
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(context, this, other);
     }
     
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(context, this, other);
     }
     
     private int cmp(RubyTime other) {
+        Ruby runtime = getRuntime();
+
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
-        long usec_other = other.usec;
-        
-		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
+        // ignore < usec on 1.8
+        long nsec = runtime.is1_9() ? this.nsec : (this.nsec / 1000 * 1000);
+        long nsec_other = runtime.is1_9() ? other.nsec : (other.nsec / 1000 * 1000);
+
+		if (millis > millis_other || (millis == millis_other && nsec > nsec_other)) {
 		    return 1;
-		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
+		} else if (millis < millis_other || (millis == millis_other && nsec < nsec_other)) {
 		    return -1;
-		} 
+		}
 
         return 0;
     }
 
     @JRubyMethod(name = "+", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_plus(IRubyObject other) {
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         long adjustment = Math.round(RubyNumeric.num2dbl(other) * 1000000);
 
-        return opPlusCommon(adjustment);
+        return opPlusMicros(adjustment);
     }
 
     @JRubyMethod(name = "+", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, IRubyObject other) {
         checkOpCoercion(context, other);
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         other = other.callMethod(context, "to_r");
 
-        long adjustment = new Double(RubyNumeric.num2dbl(other) * 1000000).longValue();
-        return opPlusCommon(adjustment);
+        long adjustNanos = (long)(RubyNumeric.num2dbl(other) * 1000000000);
+        return opPlusNanos(adjustNanos);
     }
 
-    private IRubyObject opPlusCommon(long adjustment) {
-        long micro = adjustment % 1000;
-        adjustment = adjustment / 1000;
+    private IRubyObject opPlusMicros(long adjustMicros) {
+        long adjustNanos = adjustMicros * 1000;
 
-        long time = getTimeInMillis();
-        time += adjustment;
+        long currentNanos = getTimeInMillis() * 1000000 + nsec;
 
-        if ((getUSec() + micro) >= 1000) {
-            time++;
-            micro = (getUSec() + micro) - 1000;
-        } else {
-            micro = getUSec() + micro;
-        }
+        long newNanos = currentNanos += adjustNanos;
+        long newMillisPart = newNanos / 1000000;
+        long newNanosPart = newNanos % 1000000;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
-        newTime.dt = new DateTime(time).withZone(dt.getZone());
-        newTime.setUSec(micro);
+        newTime.dt = new DateTime(newMillisPart).withZone(dt.getZone());
+        newTime.setNSec(newNanosPart);
+
+        return newTime;
+    }
+
+    private IRubyObject opPlusNanos(long adjustNanos) {
+        double currentNanos = getTimeInMillis() * 1000000 + nsec;
+
+        double newNanos = currentNanos + adjustNanos;
+        double newMillisPart = newNanos / 1000000;
+        double newNanosPart = newNanos % 1000000;
+
+        RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
+        newTime.dt = new DateTime((long)newMillisPart).withZone(dt.getZone());
+        newTime.setNSec((long)newNanosPart);
 
         return newTime;
     }
 
     private void checkOpCoercion(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             throw context.getRuntime().newTypeError("no implicit conversion to rational from string");
         } else if (other.isNil()) {
             throw context.getRuntime().newTypeError("no implicit conversion to rational from nil");
         } else if (!other.respondsTo("to_r")){
             throw context.getRuntime().newTypeError("can't convert " + other.getMetaClass().getBaseName() + " into Rational");
         }
     }
 
     private IRubyObject opMinus(RubyTime other) {
-        long time = getTimeInMillis() * 1000 + getUSec();
+        long time = getTimeInMillis() * 1000000 + getNSec();
 
-        time -= other.getTimeInMillis() * 1000 + other.getUSec();
+        time -= other.getTimeInMillis() * 1000000 + other.getNSec();
         
-        return RubyFloat.newFloat(getRuntime(), time / 1000000.0); // float number of seconds
+        return RubyFloat.newFloat(getRuntime(), time / 1000000000.0); // float number of seconds
     }
 
     @JRubyMethod(name = "-", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_minus(IRubyObject other) {
         if (other instanceof RubyTime) return opMinus((RubyTime) other);
         return opMinusCommon(other);
     }
 
     @JRubyMethod(name = "-", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_minus19(ThreadContext context, IRubyObject other) {
         checkOpCoercion(context, other);
         if (other instanceof RubyTime) return opMinus((RubyTime) other);
         return opMinusCommon(other.callMethod(context, "to_r"));
     }
 
     private IRubyObject opMinusCommon(IRubyObject other) {
         long time = getTimeInMillis();
         long adjustment = Math.round(RubyNumeric.num2dbl(other) * 1000000);
-        long micro = adjustment % 1000;
+        long nano = (adjustment % 1000) * 1000;
         adjustment = adjustment / 1000;
 
         time -= adjustment;
 
-        if (getUSec() < micro) {
+        if (getNSec() < nano) {
             time--;
-            micro = 1000 - (micro - getUSec());
+            nano = 1000000 - (nano - getNSec());
         } else {
-            micro = getUSec() - micro;
+            nano = getNSec() - nano;
         }
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
-        newTime.setUSec(micro);
+        newTime.setNSec(nano);
 
         return newTime;
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return (RubyNumeric.fix2int(invokedynamic(context, this, OP_CMP, other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return context.getRuntime().newFixnum(cmp((RubyTime) other));
         }
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     @Override
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyTime) {
             RubyTime otherTime = (RubyTime)other; 
-            return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
+            return (nsec == otherTime.nsec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"asctime", "ctime"})
     public RubyString asctime() {
         DateTimeFormatter simpleDateFormat;
 
         if (dt.getDayOfMonth() < 10) {
             simpleDateFormat = ONE_DAY_CTIME_FORMATTER;
         } else {
             simpleDateFormat = TWO_DAY_CTIME_FORMATTER;
         }
         String result = simpleDateFormat.print(dt);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"}, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject to_s() {
         return inspectCommon(TO_S_FORMATTER, TO_S_UTC_FORMATTER);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"}, compat = CompatVersion.RUBY1_9)
     public IRubyObject to_s19() {
         return inspectCommon(TO_S_FORMATTER_19, TO_S_UTC_FORMATTER_19);
     }
 
     private IRubyObject inspectCommon(DateTimeFormatter formatter, DateTimeFormatter utcFormatter) {
         DateTimeFormatter simpleDateFormat;
         if (dt.getZone() == DateTimeZone.UTC) {
             simpleDateFormat = utcFormatter;
         } else {
             simpleDateFormat = formatter;
         }
 
         String result = simpleDateFormat.print(dt);
 
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     @JRubyMethod(name = "to_f")
     public RubyFloat to_f() {
-        long time = getTimeInMillis();
-        time = time * 1000 + usec;
-        return RubyFloat.newFloat(getRuntime(), time / 1000000.0);
+        long millis = getTimeInMillis();
+        long nanos = nsec;
+        double secs = 0;
+        if (millis != 0) secs += (millis / 1000.0);
+        if (nanos != 0) secs += (nanos / 1000000000.0);
+        return RubyFloat.newFloat(getRuntime(), secs);
     }
 
     @JRubyMethod(name = {"to_i", "tv_sec"})
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     @JRubyMethod(name = {"nsec", "tv_nsec"}, compat = RUBY1_9)
     public RubyInteger nsec() {
-        return getRuntime().newFixnum(0);
+        return getRuntime().newFixnum((getTimeInMillis() % 1000) * 1000000 + nsec);
     }
 
     @JRubyMethod(name = "to_r", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_r(ThreadContext context) {
         IRubyObject rational = to_f().to_r(context);
         if (rational instanceof RubyRational) {
             IRubyObject denominator = ((RubyRational)rational).denominator(context);
             if (RubyNumeric.num2long(denominator) == 1) {
                 return ((RubyRational)rational).numerator(context);
             }
         }
 
         return rational;
     }
 
     @JRubyMethod(name = {"usec", "tv_usec"})
     public RubyInteger usec() {
         return getRuntime().newFixnum(dt.getMillisOfSecond() * 1000 + getUSec());
     }
 
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         dt = dt.withMillis(withoutMillis);
-        usec = mic % 1000;
+        nsec = (mic % 1000) * 1000;
     }
     
     public long microseconds() {
-    	return getTimeInMillis() % 1000 * 1000 + usec;
+    	return getTimeInMillis() % 1000 * 1000 + getUSec();
     }
 
     @JRubyMethod(name = "sec")
     public RubyInteger sec() {
         return getRuntime().newFixnum(dt.getSecondOfMinute());
     }
 
     @JRubyMethod(name = "min")
     public RubyInteger min() {
         return getRuntime().newFixnum(dt.getMinuteOfHour());
     }
 
     @JRubyMethod(name = "hour")
     public RubyInteger hour() {
         return getRuntime().newFixnum(dt.getHourOfDay());
     }
 
     @JRubyMethod(name = {"mday", "day"})
     public RubyInteger mday() {
         return getRuntime().newFixnum(dt.getDayOfMonth());
     }
 
     @JRubyMethod(name = {"month", "mon"})
     public RubyInteger month() {
         return getRuntime().newFixnum(dt.getMonthOfYear());
     }
 
     @JRubyMethod(name = "year")
     public RubyInteger year() {
         return getRuntime().newFixnum(dt.getYear());
     }
 
     @JRubyMethod(name = "wday")
     public RubyInteger wday() {
         return getRuntime().newFixnum((dt.getDayOfWeek()%7));
     }
 
     @JRubyMethod(name = "yday")
     public RubyInteger yday() {
         return getRuntime().newFixnum(dt.getDayOfYear());
     }
 
     @JRubyMethod(name = "subsec", compat = CompatVersion.RUBY1_9)
-    public RubyRational subsec() {
-        // TODO: nanosecond resolution (JSR310?)
-        return getRuntime().newRational(dt.getMillisOfSecond(),1000);
+    public IRubyObject subsec() {
+        Ruby runtime = getRuntime();
+        long nsec = dt.getMillisOfSecond() * 1000000 + this.nsec;
+
+        if (nsec % 1000000000 == 0) return RubyFixnum.zero(runtime);
+
+        return runtime.newRationalReduced(
+                nsec, 1000000000);
     }
 
     @JRubyMethod(name = {"gmt_offset", "gmtoff", "utc_offset"})
     public RubyInteger gmt_offset() {
         int offset = dt.getZone().getOffset(dt.getMillis());
         
         return getRuntime().newFixnum((int)(offset/1000));
     }
 
     @JRubyMethod(name = {"isdst", "dst?"})
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(!dt.getZone().isStandardOffset(dt.getMillis()));
     }
 
     @JRubyMethod(name = "zone")
     public RubyString zone() {
         Ruby runtime = getRuntime();
         String envTZ = getEnvTimeZone(runtime).toString();
         // see declaration of SHORT_TZNAME
         if (SHORT_STD_TZNAME.containsKey(envTZ) && ! dt.getZone().toTimeZone().inDaylightTime(dt.toDate())) {
             return runtime.newString(SHORT_STD_TZNAME.get(envTZ));
         }
         
         if (SHORT_DL_TZNAME.containsKey(envTZ) && dt.getZone().toTimeZone().inDaylightTime(dt.toDate())) {
             return runtime.newString(SHORT_DL_TZNAME.get(envTZ));
         }
         
         String zone = dt.getZone().getShortName(dt.getMillis());
         
         Matcher offsetMatcher = TIME_OFFSET_PATTERN.matcher(zone);
         
         if (offsetMatcher.matches()) {
             boolean minus_p = offsetMatcher.group(1).toString().equals("-");
             int hourOffset  = Integer.valueOf(offsetMatcher.group(2));
                         
             if (zone.equals("+00:00")) {
                 zone = "GMT";
             } else {
                 // try non-localized time zone name
                 zone = dt.getZone().getNameKey(dt.getMillis());
                 if (zone == null) {
                     char sign = minus_p ? '+' : '-';
                     zone = "GMT" + sign + hourOffset;
                 }
             }
         }
         
         return runtime.newString(zone);
     }
 
     public void setDateTime(DateTime dt) {
         this.dt = dt;
     }
 
     public DateTime getDateTime() {
         return this.dt;
     }
 
     public Date getJavaDate() {
         return this.dt.toDate();
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((dt.getMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     @JRubyMethod(name = "_dump", optional = 1)
     public RubyString dump(IRubyObject[] args, Block unusedBlock) {
         RubyString str = (RubyString) mdump();
         str.syncVariables(this);
         return str;
     }    
 
     public RubyObject mdump() {
         RubyTime obj = this;
         DateTime dateTime = obj.dt.toDateTime(DateTimeZone.UTC);
         byte dumpValue[] = new byte[8];
         
         int pe = 
             0x1                                 << 31 |
             ((obj.gmt().isTrue())? 0x1 : 0x0)   << 30 |
             (dateTime.getYear()-1900)           << 14 |
             (dateTime.getMonthOfYear()-1)       << 10 |
             dateTime.getDayOfMonth()            << 5  |
             dateTime.getHourOfDay();
         int se =
             dateTime.getMinuteOfHour()          << 26 |
             dateTime.getSecondOfMinute()        << 20 |
-            (dateTime.getMillisOfSecond() * 1000 + (int)usec); // dump usec, not msec
+            (dateTime.getMillisOfSecond() * 1000 + (int)getUSec()); // dump usec, not msec
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue));
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
         time.callInit(args,block);
         return time;
     }
 
     /**
      * @deprecated Use {@link #newInstance(ThreadContext, IRubyObject)}
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return newInstance(context, recv);
     }
 
     @JRubyMethod(name = "times", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject times(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn("obsolete method Time::times; use Process::times");
         return RubyProcess.times(context, recv, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "now", meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv) {
         IRubyObject obj = ((RubyClass) recv).allocate();
         obj.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, obj);
         return obj;
     }
 
     @JRubyMethod(name = "at",  meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         final RubyTime time;
 
         if (arg instanceof RubyTime) {
             RubyTime other = (RubyTime) arg;
             time = new RubyTime(runtime, (RubyClass) recv, other.dt);
-            time.setUSec(other.getUSec());
+            time.setNSec(other.getNSec());
         } else {
             time = new RubyTime(runtime, (RubyClass) recv,
                     new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg);
             long millisecs = 0;
-            long microsecs = 0;
+            long nanosecs = 0;
 
             // In the case of two arguments, MRI will discard the portion of
             // the first argument after a decimal point (i.e., "floor").
             // However in the case of a single argument, any portion after
             // the decimal point is honored.
             if (arg instanceof RubyFloat || arg instanceof RubyRational) {
                 double dbl = RubyNumeric.num2dbl(arg);
-                long micro = Math.round((dbl - seconds) * 1000000);
-                if (dbl < 0 && micro != 0) {
-                    micro += 1000000;
+                long nano = Math.round((dbl - seconds) * 1000000000);
+                if (dbl < 0 && nano != 0) {
+                    nano += 1000000000;
                 }
-                millisecs = micro / 1000;
-                microsecs = micro % 1000;
+                millisecs = nano / 1000000;
+                nanosecs = nano % 1000000;
             }
-            time.setUSec(microsecs);
+            time.setNSec(nanosecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
         }
 
         time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = "at", meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv,
                 new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg1);
             long millisecs = 0;
-            long microsecs = 0;
+            long nanosecs = 0;
 
-            long tmp = RubyNumeric.num2long(arg2);
-            millisecs = tmp / 1000;
-            microsecs = tmp % 1000;
+            if (arg2 instanceof RubyFloat || arg2 instanceof RubyRational) {
+                double micros = RubyNumeric.num2dbl(arg2);
+                double nanos = micros * 1000;
+                millisecs = (long)(nanos / 1000000);
+                nanosecs = (long)(nanos % 1000000);
+            } else {
+                long micros = RubyNumeric.num2long(arg2);
+                long nanos = micros * 1000;
+                millisecs = nanos / 1000000;
+                nanosecs = nanos % 1000000;
+            }
 
-            time.setUSec(microsecs);
+            time.setNSec(nanosecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
 
             time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = {"local", "mktime"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = "new", optional = 10, meta = true, compat = RUBY1_9)
     public static IRubyObject new19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             return newInstance(context, recv);
         }
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = {"utc", "gm"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     @JRubyMethod(name = "_load", meta = true)
     public static RubyTime load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
     @Override
     public Object toJava(Class target) {
         if (target.equals(Date.class)) {
             return getJavaDate();
         } else if (target.equals(Calendar.class)) {
             Calendar cal = GregorianCalendar.getInstance();
             cal.setTime(getJavaDate());
             return cal;
         } else if (target.equals(DateTime.class)) {
             return this.dt;
         } else if (target.equals(java.sql.Date.class)) {
             return new java.sql.Date(dt.getMillis());
         } else if (target.equals(java.sql.Time.class)) {
             return new java.sql.Time(dt.getMillis());
         } else if (target.equals(java.sql.Timestamp.class)) {
             return new java.sql.Timestamp(dt.getMillis());
         } else if (target.isAssignableFrom(Date.class)) {
             return getJavaDate();
         } else {
             return super.toJava(target);
         }
     }
     
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
 
         DateTime dt = new DateTime(DateTimeZone.UTC);
 
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         boolean utc = false;
         if ((p & (1<<31)) == 0) {
             dt = dt.withMillis(p * 1000L);
             time.setUSec((s & 0xFFFFF) % 1000);
         } else {
             p &= ~(1<<31);
             utc = ((p >>> 30 & 0x1) == 0x1);
             dt = dt.withYear(((p >>> 14) & 0xFFFF) + 1900);
             dt = dt.withMonthOfYear(((p >>> 10) & 0xF) + 1);
             dt = dt.withDayOfMonth(((p >>> 5)  & 0x1F));
             dt = dt.withHourOfDay((p & 0x1F));
             dt = dt.withMinuteOfHour(((s >>> 26) & 0x3F));
             dt = dt.withSecondOfMinute(((s >>> 20) & 0x3F));
             // marsaling dumps usec, not msec
             dt = dt.withMillisOfSecond((s & 0xFFFFF) / 1000);
             time.setUSec((s & 0xFFFFF) % 1000);
         }
         time.setDateTime(dt);
         if (!utc) time.localtime();
 
         from.getInstanceVariables().copyInstanceVariablesInto(time);
         return time;
     }
 
     private static final String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
 
     private static final Map<String, Integer> MONTHS_MAP = new HashMap<String, Integer>();
     static {
         for (int i = 0; i < MONTHS.length; i++) {
             MONTHS_MAP.put(MONTHS[i], i + 1);
         }
     }
 
     private static final int[] time_min = {1, 0, 0, 0, Integer.MIN_VALUE};
     private static final int[] time_max = {31, 23, 59, 60, Integer.MAX_VALUE};
 
     private static final int ARG_SIZE = 7;
 
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = ARG_SIZE;
         Boolean isDst = null;
 
         DateTimeZone dtz;
         if (gmt) {
             dtz = DateTimeZone.UTC;
         } else if (args.length == 10 && args[9] instanceof RubyString) {
             dtz = getTimeZone(runtime, ((RubyString) args[9]).toString());
         } else {
             dtz = getLocalTimeZone(runtime);
         }
  
         if (args.length == 10) {
 	    if(args[8] instanceof RubyBoolean) {
 	        isDst = ((RubyBoolean)args[8]).isTrue();
 	    }
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0], runtime.getNil() };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = args.length;
 
             if (len < ARG_SIZE) {
                 IRubyObject[] newArgs = new IRubyObject[ARG_SIZE];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 for (int i = len; i < ARG_SIZE; i++) {
                     newArgs[i] = runtime.getNil();
                 }
                 args = newArgs;
                 len = ARG_SIZE;
             }
         }
 
         if (args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
 
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 1;
 
         if (len > 1) {
             if (!args[1].isNil()) {
                 IRubyObject tmp = args[1].checkStringType();
                 if (!tmp.isNil()) {
                     String monthString = tmp.toString().toLowerCase();
                     Integer monthInt = MONTHS_MAP.get(monthString);
 
                     if (monthInt != null) {
                         month = monthInt;
                     } else {
                         try {
                             month = Integer.parseInt(monthString);
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int) RubyNumeric.num2long(args[1]);
                 }
             }
             if (1 > month || month > 12) {
                 throw runtime.newArgumentError("Argument out of range: for month: " + month);
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0, 0 };
 
         for (int i = 0; int_args.length >= i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if (!(args[i + 2] instanceof RubyNumeric)) {
                     args[i + 2] = args[i + 2].callMethod(
                             runtime.getCurrentContext(), "to_i");
                 }
 
                 long value = RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > value || value > time_max[i]) {
                     throw runtime.newArgumentError("argument out of range.");
                 }
                 int_args[i] = (int) value;
             }
         }
 
         if (!runtime.is1_9()) {
             if (0 <= year && year < 39) {
                 year += 2000;
             } else if (69 <= year && year < 139) {
                 year += 1900;
             }
         }
 
         DateTime dt;
         // set up with min values and then add to allow rolling over
         try {
             dt = new DateTime(year, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
 
             dt = dt.plusMonths(month - 1)
                     .plusDays(int_args[0] - 1)
                     .plusHours(int_args[1])
                     .plusMinutes(int_args[2])
                     .plusSeconds(int_args[3]);
-            if (runtime.is1_9() && !args[5].isNil()) {
+
+            // 1.9 will observe fractional seconds *if* not given usec
+            if (runtime.is1_9() && !args[5].isNil()
+                    && args[6].isNil()) {
                 double millis = RubyFloat.num2dbl(args[5]);
                 int int_millis = (int) (millis * 1000) % 1000;
                 dt = dt.plusMillis(int_millis);
             }
 
             dt = dt.withZoneRetainFields(dtz);
 
             // we might need to perform a DST correction
             if (isDst != null) {
                 // the instant at which we will ask dtz what the difference between DST and
                 // standard time is
                 long offsetCalculationInstant = dt.getMillis();
 
                 // if we might be moving this time from !DST -> DST, the offset is assumed
                 // to be the same as it was just before we last moved from DST -> !DST
                 if (dtz.isStandardOffset(dt.getMillis())) {
                     offsetCalculationInstant = dtz.previousTransition(offsetCalculationInstant);
                 }
 
                 int offset = dtz.getStandardOffset(offsetCalculationInstant)
                         - dtz.getOffset(offsetCalculationInstant);
 
                 if (!isDst && !dtz.isStandardOffset(dt.getMillis())) {
                     dt = dt.minusMillis(offset);
                 }
                 if (isDst && dtz.isStandardOffset(dt.getMillis())) {
                     dt = dt.plusMillis(offset);
                 }
             }
         } catch (org.joda.time.IllegalFieldValueException e) {
             throw runtime.newArgumentError("time out of range");
         }
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, dt);
         // Ignores usec if 8 args (for compatibility with parsedate) or if not supplied.
         if (args.length != 8 && !args[6].isNil()) {
-            int usec = int_args[4] % 1000;
-            int msec = int_args[4] / 1000;
+            boolean fractionalUSecGiven = args[6] instanceof RubyFloat || args[6] instanceof RubyRational;
 
-            if (int_args[4] < 0) {
-                msec -= 1;
-                usec += 1000;
+            if (runtime.is1_9() && fractionalUSecGiven) {
+                double micros = RubyNumeric.num2dbl(args[6]);
+                double nanos = micros * 1000;
+                time.dt = dt.withMillis(dt.getMillis() + Math.round(micros / 1000));
+                time.setNSec((long)(nanos % 1000000));
+            } else {
+                int usec = int_args[4] % 1000;
+                int msec = int_args[4] / 1000;
+
+                if (int_args[4] < 0) {
+                    msec -= 1;
+                    usec += 1000;
+                }
+                time.dt = dt.withMillis(dt.getMillis() + msec);
+                time.setUSec(usec);
             }
-            time.dt = dt.withMillis(dt.getMillis() + msec);
-            time.setUSec(usec);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         return time;
     }
 }
diff --git a/src/org/jruby/util/RubyDateFormat.java b/src/org/jruby/util/RubyDateFormat.java
index 1e3fdb03ac..530f904d33 100644
--- a/src/org/jruby/util/RubyDateFormat.java
+++ b/src/org/jruby/util/RubyDateFormat.java
@@ -1,583 +1,590 @@
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
  * Copyright (C) 2002, 2009 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.util;
 
 import java.text.DateFormat;
 import java.text.DateFormatSymbols;
 import java.text.FieldPosition;
 import java.text.ParsePosition;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import org.joda.time.DateTime;
 
 public class RubyDateFormat extends DateFormat {
     private static final long serialVersionUID = -250429218019023997L;
 
     private boolean ruby_1_9;
     private List<Token> compiledPattern;
 
     private final DateFormatSymbols formatSymbols;
 
     private static final int FORMAT_STRING = 0;
     private static final int FORMAT_WEEK_LONG = 1;
     private static final int FORMAT_WEEK_SHORT = 2;
     private static final int FORMAT_MONTH_LONG = 3;
     private static final int FORMAT_MONTH_SHORT = 4;
     private static final int FORMAT_DAY = 5;
     private static final int FORMAT_DAY_S = 6;
     private static final int FORMAT_HOUR = 7;
     private static final int FORMAT_HOUR_M = 8;
     private static final int FORMAT_HOUR_S = 9;
     private static final int FORMAT_DAY_YEAR = 10;
     private static final int FORMAT_MINUTES = 11;
     private static final int FORMAT_MONTH = 12;
     private static final int FORMAT_MERIDIAN = 13;
     private static final int FORMAT_MERIDIAN_LOWER_CASE = 14;
     private static final int FORMAT_SECONDS = 15;
     private static final int FORMAT_WEEK_YEAR_S = 16;
     private static final int FORMAT_WEEK_YEAR_M = 17;
     private static final int FORMAT_DAY_WEEK = 18;
     private static final int FORMAT_YEAR_LONG = 19;
     private static final int FORMAT_YEAR_SHORT = 20;
     private static final int FORMAT_ZONE_OFF = 21;
     private static final int FORMAT_ZONE_ID = 22;
     private static final int FORMAT_CENTURY = 23;
     private static final int FORMAT_HOUR_BLANK = 24;
     private static final int FORMAT_MILLISEC = 25;
     private static final int FORMAT_EPOCH = 26;
     private static final int FORMAT_DAY_WEEK2 = 27;
     private static final int FORMAT_WEEK_WEEKYEAR = 28;
     private static final int FORMAT_NANOSEC = 29;
     private static final int FORMAT_PRECISION = 30;
     private static final int FORMAT_WEEKYEAR = 31;
     private static final int FORMAT_OUTPUT = 32;
 
 
     private static class Token {
         private int format;
         private Object data;
         private TimeOutputFormatter outputFormatter;
         
         public Token(int format) {
             this(format, null);
         }
 
         public Token(int format, Object data) {
             this.format = format;
             this.data = data;
         }
         
         /**
          * Gets the data.
          * @return Returns a Object
          */
         public Object getData() {
             return data;
         }
 
         /**
          * Gets the format.
          * @return Returns a int
          */
         public int getFormat() {
             return format;
         }
     }
 
     /**
      * Constructor for RubyDateFormat.
      */
     public RubyDateFormat() {
         this("", new DateFormatSymbols());
     }
 
     public RubyDateFormat(String pattern, Locale aLocale) {
         this(pattern, new DateFormatSymbols(aLocale));
     }
 
     public RubyDateFormat(String pattern, Locale aLocale, boolean ruby_1_9) {
         this(pattern, aLocale);
         this.ruby_1_9 = ruby_1_9;
     }
     
     public RubyDateFormat(String pattern, DateFormatSymbols formatSymbols) {
         super();
 
         this.formatSymbols = formatSymbols;
         applyPattern(pattern);
     }
     
     public void applyPattern(String pattern) {
         compilePattern(pattern);
     }
 
     private void compilePattern(String pattern) {
         compiledPattern = new LinkedList<Token>();
         
         int len = pattern.length();
         for (int i = 0; i < len;) {
             if (pattern.charAt(i) == '%') {
                 i++;
 
                 if(i == len) {
                     compiledPattern.add(new Token(FORMAT_STRING, "%"));
                 } else {
                     i = addOutputFormatter(pattern, i);
 
                     switch (pattern.charAt(i)) {
                     case 'A' :
                         compiledPattern.add(new Token(FORMAT_WEEK_LONG));
                         break;
                     case 'a' :
                         compiledPattern.add(new Token(FORMAT_WEEK_SHORT));
                         break;
                     case 'B' :
                         compiledPattern.add(new Token(FORMAT_MONTH_LONG));
                         break;
                     case 'b' :
                     case 'h' :
                         compiledPattern.add(new Token(FORMAT_MONTH_SHORT));
                         break;
                     case 'C' :
                         compiledPattern.add(new Token(FORMAT_CENTURY));
                         break;
                     case 'c' :
                         compiledPattern.add(new Token(FORMAT_WEEK_SHORT));
                         compiledPattern.add(new Token(FORMAT_STRING, " "));
                         compiledPattern.add(new Token(FORMAT_MONTH_SHORT));
                         compiledPattern.add(new Token(FORMAT_STRING, " "));
                         compiledPattern.add(new Token(FORMAT_DAY));
                         compiledPattern.add(new Token(FORMAT_STRING, " "));
                         compiledPattern.add(new Token(FORMAT_HOUR));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_SECONDS));
                         compiledPattern.add(new Token(FORMAT_STRING, " "));
                         compiledPattern.add(new Token(FORMAT_YEAR_LONG));
                         break;
                     case 'D':
                         compiledPattern.add(new Token(FORMAT_MONTH));
                         compiledPattern.add(new Token(FORMAT_STRING, "/"));
                         compiledPattern.add(new Token(FORMAT_DAY));
                         compiledPattern.add(new Token(FORMAT_STRING, "/"));
                         compiledPattern.add(new Token(FORMAT_YEAR_SHORT));
                         break;
                     case 'd':
                         compiledPattern.add(new Token(FORMAT_DAY));
                         break;
                     case 'e':
                         compiledPattern.add(new Token(FORMAT_DAY_S));
                         break;
                     case 'F':
                         compiledPattern.add(new Token(FORMAT_YEAR_LONG));
                         compiledPattern.add(new Token(FORMAT_STRING, "-"));
                         compiledPattern.add(new Token(FORMAT_MONTH));
                         compiledPattern.add(new Token(FORMAT_STRING, "-"));
                         compiledPattern.add(new Token(FORMAT_DAY));
                         break;
                     case 'G':
                         compiledPattern.add(new Token(FORMAT_WEEKYEAR));
                         break;
                     case 'H':
                         compiledPattern.add(new Token(FORMAT_HOUR));
                         break;
                     case 'I':
                         compiledPattern.add(new Token(FORMAT_HOUR_M));
                         break;
                     case 'j':
                         compiledPattern.add(new Token(FORMAT_DAY_YEAR));
                         break;
                     case 'k':
                         compiledPattern.add(new Token(FORMAT_HOUR_BLANK));
                         break;
                     case 'L':
                         compiledPattern.add(new Token(FORMAT_MILLISEC));
                         break;
                     case 'l':
                         compiledPattern.add(new Token(FORMAT_HOUR_S));
                         break;
                     case 'M':
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         break;
                     case 'm':
                         compiledPattern.add(new Token(FORMAT_MONTH));
                         break;
                     case 'N':
                         compiledPattern.add(new Token(FORMAT_NANOSEC));
                         break;
                     case 'n':
                         compiledPattern.add(new Token(FORMAT_STRING, "\n"));
                         break;
                     case 'p':
                         compiledPattern.add(new Token(FORMAT_MERIDIAN));
                         break;
                     case 'P':
                         compiledPattern.add(new Token(FORMAT_MERIDIAN_LOWER_CASE));
                         break;
                     case 'R':
                         compiledPattern.add(new Token(FORMAT_HOUR));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         break;
                     case 'r':
                         compiledPattern.add(new Token(FORMAT_HOUR_M));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_SECONDS));
                         compiledPattern.add(new Token(FORMAT_STRING, " "));
                         compiledPattern.add(new Token(FORMAT_MERIDIAN));
                         break;
                     case 's':
                         compiledPattern.add(new Token(FORMAT_EPOCH));
                         break;
                     case 'S':
                         compiledPattern.add(new Token(FORMAT_SECONDS));
                         break;
                     case 'T':
                         compiledPattern.add(new Token(FORMAT_HOUR));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_SECONDS));
                         break;
                     case 't':
                         compiledPattern.add(new Token(FORMAT_STRING,"\t"));
                         break;
                     case 'u':
                         compiledPattern.add(new Token(FORMAT_DAY_WEEK2));
                         break;
                     case 'U':
                         compiledPattern.add(new Token(FORMAT_WEEK_YEAR_S));
                         break;
                     case 'v':
                         compiledPattern.add(new Token(FORMAT_DAY_S));
                         compiledPattern.add(new Token(FORMAT_STRING, "-"));
                         compiledPattern.add(new Token(FORMAT_MONTH_SHORT));
                         compiledPattern.add(new Token(FORMAT_STRING, "-"));
                         compiledPattern.add(new Token(FORMAT_YEAR_LONG));
                         break;
                     case 'V':
                         compiledPattern.add(new Token(FORMAT_WEEK_WEEKYEAR));
                         break;
                     case 'W':
                         compiledPattern.add(new Token(FORMAT_WEEK_YEAR_M));
                         break;
                     case 'w':
                         compiledPattern.add(new Token(FORMAT_DAY_WEEK));
                         break;
                     case 'X':
                         compiledPattern.add(new Token(FORMAT_HOUR));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_MINUTES));
                         compiledPattern.add(new Token(FORMAT_STRING, ":"));
                         compiledPattern.add(new Token(FORMAT_SECONDS));
                         break;
                     case 'x':
                         compiledPattern.add(new Token(FORMAT_MONTH));
                         compiledPattern.add(new Token(FORMAT_STRING, "/"));
                         compiledPattern.add(new Token(FORMAT_DAY));
                         compiledPattern.add(new Token(FORMAT_STRING, "/"));
                         compiledPattern.add(new Token(FORMAT_YEAR_SHORT));
                         break;
                     case 'Y':
                         compiledPattern.add(new Token(FORMAT_YEAR_LONG));
                         break;
                     case 'y':
                         compiledPattern.add(new Token(FORMAT_YEAR_SHORT));
                         break;
                     case 'Z':
                         compiledPattern.add(new Token(FORMAT_ZONE_ID));
                         break;
                     case 'z':
                         compiledPattern.add(new Token(FORMAT_ZONE_OFF));
                         break;
                     case '%':
                         compiledPattern.add(new Token(FORMAT_STRING, "%"));
                         break;
                     default:
                         compiledPattern.add(new Token(FORMAT_STRING, "%" + pattern.charAt(i)));
                     }
                     i++;
                 }
             } else {
                 StringBuilder sb = new StringBuilder();
                 for (;i < len && pattern.charAt(i) != '%'; i++) {
                     sb.append(pattern.charAt(i));
                 }
                 compiledPattern.add(new Token(FORMAT_STRING, sb.toString()));
             }
         }
     }
 
     private int addOutputFormatter(String pattern, int index) {
         if (ruby_1_9) {
             TimeOutputFormatter outputFormatter = TimeOutputFormatter.getFormatter(pattern.substring(index - 1));
             if (outputFormatter != null) {
                 index += outputFormatter.getFormatter().length();
                 compiledPattern.add(new Token(FORMAT_OUTPUT, outputFormatter));
             }
         }
         return index;
     }
 
     private String formatOutput(TimeOutputFormatter formatter, String output) {
         if (formatter == null) return output;
         output = formatter.format(output);
         formatter = null;
         return output;
     }
 
     private DateTime dt;
+    private long nsec;
 
     public void setDateTime(final DateTime dt) {
         this.dt = dt;
     }
 
+    public void setNSec(long nsec) {
+        this.nsec = nsec;
+    }
+
     /**
      * @see DateFormat#format(Date, StringBuffer, FieldPosition)
      */
     public StringBuffer format(Date ignored, StringBuffer toAppendTo, FieldPosition fieldPosition) {
         TimeOutputFormatter formatter = null;
         for (Token token: compiledPattern) {
             String output = null;
+            long value = 0;
             boolean format = true;
 
             switch (token.getFormat()) {
                 case FORMAT_OUTPUT:
                     formatter = (TimeOutputFormatter) token.getData();
                     break;
                 case FORMAT_STRING:
                     output = token.getData().toString();
                     format = false;
                     break;
                 case FORMAT_WEEK_LONG:
                     // This is GROSS, but Java API's aren't ISO 8601 compliant at all
                     int v = (dt.getDayOfWeek()+1)%8;
                     if(v == 0) {
                         v++;
                     }
                     output = formatSymbols.getWeekdays()[v];
                     break;
                 case FORMAT_WEEK_SHORT:
                     // This is GROSS, but Java API's aren't ISO 8601 compliant at all
                     v = (dt.getDayOfWeek()+1)%8;
                     if(v == 0) {
                         v++;
                     }
                     output = formatSymbols.getShortWeekdays()[v];
                     break;
                 case FORMAT_MONTH_LONG:
                     output = formatSymbols.getMonths()[dt.getMonthOfYear()-1];
                     break;
                 case FORMAT_MONTH_SHORT:
                     output = formatSymbols.getShortMonths()[dt.getMonthOfYear()-1];
                     break;
                 case FORMAT_DAY:
-                    int value = dt.getDayOfMonth();
+                    value = dt.getDayOfMonth();
                     output = String.format("%02d", value);
                     break;
                 case FORMAT_DAY_S: 
                     value = dt.getDayOfMonth();
-                    output = (value < 10 ? " " : "") + Integer.toString(value);
+                    output = (value < 10 ? " " : "") + Long.toString(value);
                     break;
                 case FORMAT_HOUR:
                 case FORMAT_HOUR_BLANK:
                     value = dt.getHourOfDay();
                     output = "";
                     if (value < 10) {
                         output += token.getFormat() == FORMAT_HOUR ? "0" : " ";
                     }
                     output += value;
                     break;
                 case FORMAT_HOUR_M:
                 case FORMAT_HOUR_S:
                     value = dt.getHourOfDay();
 
                     if(value > 12) {
                         value-=12;
                     }
 
                     if(value == 0) {
                         output = "12";
                     } else {
                         output = "";
                         if (value < 10) {
                             output += token.getFormat() == FORMAT_HOUR_M ? "0" : " ";
                         }
                         output += value;
                     }
                     break;
                 case FORMAT_DAY_YEAR:
                     value = dt.getDayOfYear();
                     output = String.format("%03d", value);
                     break;
                 case FORMAT_MINUTES:
                     value = dt.getMinuteOfHour();
                     output = String.format("%02d", value);
                     break;
                 case FORMAT_MONTH:
                     value = dt.getMonthOfYear();
                     output = String.format("%02d", value);
                     break;
                 case FORMAT_MERIDIAN:
                 case FORMAT_MERIDIAN_LOWER_CASE:
                     if (dt.getHourOfDay() < 12) {
                         output = token.getFormat() == FORMAT_MERIDIAN ? "AM" : "am";
                     } else {
                         output = token.getFormat() == FORMAT_MERIDIAN ? "PM" : "pm";
                     }
                     break;
                 case FORMAT_SECONDS:
                     value = dt.getSecondOfMinute();
-                    output = (value < 10 ? "0" : "") + Integer.toString(value);
+                    output = (value < 10 ? "0" : "") + Long.toString(value);
                     break;
                 case FORMAT_WEEK_YEAR_M:
                     output = formatWeekYear(java.util.Calendar.MONDAY);
                     break;
                 case FORMAT_WEEK_YEAR_S:
                     output = formatWeekYear(java.util.Calendar.SUNDAY);
                     break;
                 case FORMAT_DAY_WEEK:
                 case FORMAT_DAY_WEEK2:
                     value = dt.getDayOfWeek() ;
                     if (token.getFormat() == FORMAT_DAY_WEEK) {
                         value = value % 7;
                     }
-                    output = Integer.toString(value);
+                    output = Long.toString(value);
                     break;
                 case FORMAT_YEAR_LONG:
                     value = dt.getYear();
                     output = String.format("%04d", value);
                     break;
                 case FORMAT_YEAR_SHORT:
                     value = dt.getYear() % 100;
                     output = String.format("%02d", value);
                     break;
                 case FORMAT_ZONE_OFF:
                     value = dt.getZone().getOffset(dt.getMillis());
                     output = value < 0 ? "-" : "+";
 
                     value = Math.abs(value);
                     if (value / 3600000 < 10) {
                         output += "0";
                     }
                     output += (value / 3600000);
                     value = value % 3600000 / 60000;
                     if (value < 10) {
                         output += "0";
                     }
                     output += value;
                     break;
                 case FORMAT_ZONE_ID:
                     toAppendTo.append(dt.getZone().getShortName(dt.getMillis()));
                     break;
                 case FORMAT_CENTURY:
                     toAppendTo.append(dt.getCenturyOfEra());
                     break;
                 case FORMAT_MILLISEC:
                     value = dt.getMillisOfSecond();
                     output = String.format("%03d", value);
                     break;
                 case FORMAT_EPOCH:
                     output = Long.toString(dt.getMillis()/1000);
                     break;
                 case FORMAT_WEEK_WEEKYEAR:
                     value = dt.getWeekOfWeekyear();
                     output = String.format("%02d", value);
                     break;
                 case FORMAT_NANOSEC:
                     value = dt.getMillisOfSecond() * 1000000;
-                    String width = "3";
+                    if (ruby_1_9) value += nsec;
+                    String width = ruby_1_9 ? "9" : "3";
                     if (formatter != null) width = formatter.getFormatter();
                     output = formatTruncate(String.valueOf(value), Integer.valueOf(width), "0");
                     break;
                 case FORMAT_WEEKYEAR:
                     output = Integer.toString(dt.getWeekyear());
                     break;
             }
 
             if (output != null) {
                 toAppendTo.append(format ? formatOutput(formatter, output) : output);
             }
         }
 
         return toAppendTo;
     }
 
 	private String formatWeekYear(int firstDayOfWeek) {
             java.util.Calendar dtCalendar = dt.toGregorianCalendar();
             dtCalendar.setFirstDayOfWeek(firstDayOfWeek);
             dtCalendar.setMinimalDaysInFirstWeek(7);
             int value = dtCalendar.get(java.util.Calendar.WEEK_OF_YEAR);
             if ((value == 52 || value == 53) &&
                     (dtCalendar.get(Calendar.MONTH) == Calendar.JANUARY )) {
                 // MRI behavior: Week values are monotonous.
                 // So, weeks that effectively belong to previous year,
                 // will get the value of 0, not 52 or 53, as in Java.
                 value = 0;
             }
             return String.format("%02d", value);
 	}
 
     /**
      * @see DateFormat#parse(String, ParsePosition)
      */
     public Date parse(String source, ParsePosition pos) {
         throw new UnsupportedOperationException();
     }
     
     /**
      * Return the String obtained by truncating orig to the first len characters
      * 
      * @param orig Original string
      * @param len Maximum length for the returned String
      * @return String thus obtained
      */
     private String formatTruncate(String orig, int len, String pad) {
         if (len == 0) return "";
         if (orig.length() > len) {
             return orig.substring(0, len);
         } else {
             StringBuilder sb = new StringBuilder(len);
             sb.append(orig);
             while (sb.length() < len) {
                 sb = sb.append(pad);
             }
             return sb.toString().substring(0,len);
         }
     }
 }
