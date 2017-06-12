diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index bcd8ce177c..9ad7c1c429 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2138,2000 +2138,2004 @@ public final class Ruby {
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
 
         // clear out threadlocals so they don't leak
         recursive = new ThreadLocal<Map<String, RubyHash>>();
 
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
 
         getJITCompiler().tearDown();
 
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
 
     public RubyRational newRationalReduced(long num, long den) {
         return (RubyRational)RubyRational.newRationalConvert(getCurrentContext(), newFixnum(num), newFixnum(den));
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
 
+    public RaiseException newArgumentError(String name, int got, int expected) {
+        return newRaiseException(getArgumentError(), "wrong number of arguments calling `" + name + "` (" + got + " for " + expected + ")");
+    }
+
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
         boolean outermost = outer && !recursiveCheck(p.list, recursiveKey, null);
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
                 recursivePush(p.list, recursiveKey, null);
                 try {
                     result = execRecursiveI(p);
                 } catch(RecursiveError e) {
                     if(e.tag != p.list) {
                         throw e;
                     } else {
                         result = p.list;
                     }
                 }
                 recursivePop(p.list, recursiveKey, null);
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
      *
      * @return a SelectorPool from which to get Selector instances
      */
     public SelectorPool getSelectorPool() {
         return selectorPool;
     }
 
     /**
      * Get the core class RuntimeCache instance, for doing dynamic calls from
      * core class methods.
      */
     public RuntimeCache getRuntimeCache() {
         return runtimeCache;
     }
 
     /**
      * Get the list of method names being profiled
      */
     public String[] getProfiledNames() {
         return profiledNames;
     }
 
     /**
      * Get the list of method objects for methods being profiled
      */
     public DynamicMethod[] getProfiledMethods() {
         return profiledMethods;
     }
 
     /**
      * Add a method and its name to the profiling arrays, so it can be printed out
      * later.
      *
      * @param name the name of the method
      * @param method
      */
     public synchronized void addProfiledMethod(String name, DynamicMethod method) {
         if (!config.isProfiling()) return;
         if (method.isUndefined()) return;
         if (method.getSerialNumber() > MAX_PROFILE_METHODS) return;
 
         int index = (int)method.getSerialNumber();
         if (profiledMethods.length <= index) {
             int newSize = Math.min((int)index * 2 + 1, MAX_PROFILE_METHODS);
             String[] newProfiledNames = new String[newSize];
             System.arraycopy(profiledNames, 0, newProfiledNames, 0, profiledNames.length);
             profiledNames = newProfiledNames;
             DynamicMethod[] newProfiledMethods = new DynamicMethod[newSize];
             System.arraycopy(profiledMethods, 0, newProfiledMethods, 0, profiledMethods.length);
             profiledMethods = newProfiledMethods;
         }
 
         // only add the first one we encounter, since others will probably share the original
         if (profiledNames[index] == null) {
             profiledNames[index] = name;
             profiledMethods[index] = method;
         }
     }
     
     /**
      * Increment the count of exceptions generated by code in this runtime.
      */
     public void incrementExceptionCount() {
         exceptionCount.incrementAndGet();
     }
     
     /**
      * Get the current exception count.
      * 
      * @return he current exception count
      */
     public int getExceptionCount() {
         return exceptionCount.get();
     }
     
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementBacktraceCount() {
         backtraceCount.incrementAndGet();
     }
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index 8ac77109cc..572c8d2084 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,774 +1,774 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.IdUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.ClassIndex;
 
 import java.util.concurrent.Callable;
 
 import static org.jruby.runtime.Visibility.*;
 
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.HASH;
 
 /**
  * @author  jpetersen
  */
 @JRubyClass(name="Struct")
 public class RubyStruct extends RubyObject {
     private IRubyObject[] values;
 
     /**
      * Constructor for RubyStruct.
      * @param runtime
      * @param rubyClass
      */
     private RubyStruct(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
         
         int size = RubyNumeric.fix2int(getInternalVariable((RubyClass)rubyClass, "__size__"));
 
         values = new IRubyObject[size];
 
         RuntimeHelpers.fillNil(values, runtime);
     }
 
     public static RubyClass createStructClass(Ruby runtime) {
         RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setStructClass(structClass);
         structClass.index = ClassIndex.STRUCT;
         structClass.includeModule(runtime.getEnumerable());
         structClass.defineAnnotatedMethods(RubyStruct.class);
 
         return structClass;
     }
     
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRUCT;
     }
     
     private static IRubyObject getInternalVariable(RubyClass type, String internedName) {
         RubyClass structClass = type.getRuntime().getStructClass();
         IRubyObject variable;
 
         while (type != null && type != structClass) {
             if ((variable = (IRubyObject)type.getInternalVariable(internedName)) != null) {
                 return variable;
             }
 
             type = type.getSuperClass();
         }
 
         return type.getRuntime().getNil();
     }
 
     private RubyClass classOf() {
         return getMetaClass() instanceof MetaClass ? getMetaClass().getSuperClass() : getMetaClass();
     }
 
     private void modify() {
         testFrozen();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify struct");
         }
     }
     
     @JRubyMethod
     public RubyFixnum hash(ThreadContext context) {
         Ruby runtime = getRuntime();
         int h = getMetaClass().getRealClass().hashCode();
 
         for (int i = 0; i < values.length; i++) {
             h = (h << 1) | (h < 0 ? 1 : 0);
             h ^= RubyNumeric.num2long(invokedynamic(context, values[i], HASH));
         }
         
         return runtime.newFixnum(h);
     }
 
     private IRubyObject setByName(String name, IRubyObject value) {
         RubyArray member = (RubyArray) getInternalVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asJavaString().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInternalVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asJavaString().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     // Struct methods
 
     /** Create new Struct class.
      *
      * MRI: rb_struct_s_def / make_struct
      *
      */
     @JRubyMethod(name = "new", required = 1, rest = true, meta = true)
     public static RubyClass newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = null;
         boolean nilName = false;
         Ruby runtime = recv.getRuntime();
 
         if (args.length > 0) {
             IRubyObject firstArgAsString = args[0].checkStringType();
             if (!firstArgAsString.isNil()) {
                 name = ((RubyString)firstArgAsString).getByteList().toString();
             } else if (args[0].isNil()) {
                 nilName = true;
             }
         }
 
         RubyArray member = runtime.newArray();
 
         for (int i = (name == null && !nilName) ? 0 : 1; i < args.length; i++) {
             member.append(runtime.newSymbol(args[i].asJavaString()));
         }
 
         RubyClass newStruct;
         RubyClass superClass = (RubyClass)recv;
 
         if (name == null || nilName) {
             newStruct = RubyClass.newClass(runtime, superClass);
             newStruct.setAllocator(STRUCT_INSTANCE_ALLOCATOR);
             newStruct.makeMetaClass(superClass.getMetaClass());
             newStruct.inherit(superClass);
         } else {
             if (!IdUtil.isConstant(name)) {
                 throw runtime.newNameError("identifier " + name + " needs to be constant", name);
             }
 
             IRubyObject type = superClass.getConstantAt(name);
             if (type != null) {
                 ThreadContext context = runtime.getCurrentContext();
                 runtime.getWarnings().warn(ID.STRUCT_CONSTANT_REDEFINED, context.getFile(), context.getLine(), "redefining constant Struct::" + name);
                 superClass.remove_const(context, runtime.newString(name));
             }
             newStruct = superClass.defineClassUnder(name, superClass, STRUCT_INSTANCE_ALLOCATOR);
         }
 
         // set reified class to RubyStruct, for Java subclasses to use
         newStruct.setReifiedClass(RubyStruct.class);
         newStruct.index = ClassIndex.STRUCT;
         
         newStruct.setInternalVariable("__size__", member.length());
         newStruct.setInternalVariable("__member__", member);
 
         newStruct.getSingletonClass().defineAnnotatedMethods(StructMethods.class);
 
         // define access methods.
         for (int i = (name == null && !nilName) ? 0 : 1; i < args.length; i++) {
             final String memberName = args[i].asJavaString();
             // if we are storing a name as well, index is one too high for values
             final int index = (name == null && !nilName) ? i : i - 1;
             newStruct.addMethod(memberName, new DynamicMethod(newStruct, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone) {
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
-                    Arity.checkArgumentCount(self.getRuntime(), args, 0, 0);
+                    Arity.checkArgumentCount(context.runtime, name, args, 0, 0);
                     return ((RubyStruct)self).get(index);
                 }
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     return ((RubyStruct)self).get(index);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
             newStruct.addMethod(memberName + "=", new DynamicMethod(newStruct, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone) {
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
-                    Arity.checkArgumentCount(self.getRuntime(), args, 1, 1);
+                    Arity.checkArgumentCount(context.runtime, name, args, 1, 1);
                     return ((RubyStruct)self).set(args[0], index);
                 }
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                     return ((RubyStruct)self).set(arg, index);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
         }
         
         if (block.isGiven()) {
             // Struct bodies should be public by default, so set block visibility to public. JRUBY-1185.
             block.getBinding().setVisibility(Visibility.PUBLIC);
             block.yieldNonArray(runtime.getCurrentContext(), null, newStruct, newStruct);
         }
 
         return newStruct;
     }
     
     // For binding purposes on the newly created struct types
     public static class StructMethods {
         @JRubyMethod(name = {"new", "[]"}, rest = true)
         public static IRubyObject newStruct(IRubyObject recv, IRubyObject[] args, Block block) {
             return RubyStruct.newStruct(recv, args, block);
         }
 
         @JRubyMethod(name = {"new", "[]"})
         public static IRubyObject newStruct(IRubyObject recv, Block block) {
             return RubyStruct.newStruct(recv, block);
         }
 
         @JRubyMethod(name = {"new", "[]"})
         public static IRubyObject newStruct(IRubyObject recv, IRubyObject arg0, Block block) {
             return RubyStruct.newStruct(recv, arg0, block);
         }
 
         @JRubyMethod(name = {"new", "[]"})
         public static IRubyObject newStruct(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
             return RubyStruct.newStruct(recv, arg0, arg1, block);
         }
 
         @JRubyMethod(name = {"new", "[]"})
         public static IRubyObject newStruct(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             return RubyStruct.newStruct(recv, arg0, arg1, arg2, block);
         }
 
         @JRubyMethod(name = "members", compat = CompatVersion.RUBY1_8)
         public static IRubyObject members(IRubyObject recv, Block block) {
             return RubyStruct.members(recv, block);
         }
 
         @JRubyMethod(name = "members", compat = CompatVersion.RUBY1_9)
         public static IRubyObject members19(IRubyObject recv, Block block) {
             return RubyStruct.members19(recv, block);
         }
     }
 
     /** Create new Structure.
      *
      * MRI: struct_alloc
      *
      */
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         struct.callInit(args, block);
 
         return struct;
     }
 
     public static RubyStruct newStruct(IRubyObject recv, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         struct.callInit(block);
 
         return struct;
     }
 
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject arg0, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         struct.callInit(arg0, block);
 
         return struct;
     }
 
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         struct.callInit(arg0, arg1, block);
 
         return struct;
     }
 
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         struct.callInit(arg0, arg1, arg2, block);
 
         return struct;
     }
 
     private void checkSize(int length) {
         if (length > values.length) {
             throw getRuntime().newArgumentError("struct size differs (" + length +" for " + values.length + ")");
         }
     }
 
     @JRubyMethod(rest = true, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
         modify();
         checkSize(args.length);
 
         System.arraycopy(args, 0, values, 0, args.length);
         RuntimeHelpers.fillNil(values, args.length, values.length, context.runtime);
 
         return context.nil;
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context) {
         IRubyObject nil = context.nil;
         return initializeInternal(context, 0, nil, nil, nil);
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0) {
         IRubyObject nil = context.nil;
         return initializeInternal(context, 1, arg0, nil, nil);
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         return initializeInternal(context, 2, arg0, arg1, context.nil);
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return initializeInternal(context, 3, arg0, arg1, arg2);
     }
     
     public IRubyObject initializeInternal(ThreadContext context, int provided, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         modify();
         checkSize(provided);
 
         switch (provided) {
         case 3:
             values[2] = arg2;
         case 2:
             values[1] = arg1;
         case 1:
             values[0] = arg0;
         }
         if (provided < values.length) {
             RuntimeHelpers.fillNil(values, provided, values.length, context.runtime);
         }
 
         return getRuntime().getNil();
     }
     
     public static RubyArray members(IRubyObject recv, Block block) {
         RubyArray member = (RubyArray) getInternalVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0,k=member.getLength(); i < k; i++) {
             // this looks weird, but it's because they're RubySymbol and that's java.lang.String internally
             result.append(recv.getRuntime().newString(member.eltInternal(i).asJavaString()));
         }
 
         return result;
     }
 
     public static RubyArray members19(IRubyObject recv, Block block) {
         RubyArray member = (RubyArray) getInternalVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0,k=member.getLength(); i < k; i++) {
             result.append(member.eltInternal(i));
         }
 
         return result;
     }
 
     @JRubyMethod(name = "members", compat = CompatVersion.RUBY1_8)
     public RubyArray members() {
         return members(classOf(), Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "members", compat = CompatVersion.RUBY1_9)
     public RubyArray members19() {
         return members19(classOf(), Block.NULL_BLOCK);
     }
     
     @JRubyMethod
     public RubyArray select(ThreadContext context, Block block) {
         RubyArray array = RubyArray.newArray(context.getRuntime());
         
         for (int i = 0; i < values.length; i++) {
             if (block.yield(context, values[i]).isTrue()) {
                 array.append(values[i]);
             }
         }
         
         return array;
     }
 
     public IRubyObject set(IRubyObject value, int index) {
         modify();
 
         return values[index] = value;
     }
 
     private RaiseException notStructMemberError(String name) {
         return getRuntime().newNameError(name + " is not struct member", name);
     }
 
     public IRubyObject get(int index) {
         return values[index];
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyStruct struct = (RubyStruct)clone;
         struct.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, struct.values, 0, values.length);
     }
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(final ThreadContext context, IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
         if (getMetaClass().getRealClass() != other.getMetaClass().getRealClass()) return getRuntime().getFalse();
         
         final Ruby runtime = getRuntime();
         final RubyStruct otherStruct = (RubyStruct)other;
 
         // identical
         if (other == this) return runtime.getTrue();
 
         // recursion guard
         return runtime.execRecursiveOuter(new Ruby.RecursiveFunction() {
             public IRubyObject call(IRubyObject obj, boolean recur) {
                 if (recur) {
                     return runtime.getTrue();
                 }
                 for (int i = 0; i < values.length; i++) {
                     if (!equalInternal(context, values[i], otherStruct.values[i])) return runtime.getFalse();
                 }
                 return runtime.getTrue();
             }
         }, this);
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(final ThreadContext context, IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
         if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
         
         final Ruby runtime = getRuntime();
         final RubyStruct otherStruct = (RubyStruct)other;
 
         // identical
         if (other == this) return runtime.getTrue();
 
         // recursion guard
         return runtime.execRecursiveOuter(new Ruby.RecursiveFunction() {
             public IRubyObject call(IRubyObject obj, boolean recur) {
                 if (recur) {
                     return runtime.getTrue();
                 }
                 for (int i = 0; i < values.length; i++) {
                     if (!eqlInternal(context, values[i], otherStruct.values[i])) return runtime.getFalse();
                 }
                 return runtime.getTrue();
             }
         }, this);
     }
 
     /** inspect_struct
     *
     */
     private IRubyObject inspectStruct(final ThreadContext context) {    
         RubyArray member = (RubyArray) getInternalVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         ByteList buffer = new ByteList("#<struct ".getBytes());
         buffer.append(getMetaClass().getRealClass().getRealClass().getName().getBytes());
         buffer.append(' ');
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (i > 0) buffer.append(',').append(' ');
             // FIXME: MRI has special case for constants here 
             buffer.append(RubyString.objAsString(context, member.eltInternal(i)).getByteList());
             buffer.append('=');
             buffer.append(inspect(context, values[i]).getByteList());
         }
 
         buffer.append('>');
         return getRuntime().newString(buffer); // OBJ_INFECT        
     }
 
     @JRubyMethod(name = {"inspect", "to_s"})
     public IRubyObject inspect(ThreadContext context) {
         if (getRuntime().isInspecting(this)) return getRuntime().newString("#<struct " + getMetaClass().getRealClass().getName() + ":...>");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectStruct(context);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     @JRubyMethod(name = {"to_a", "values"})
     public RubyArray to_a() {
         return getRuntime().newArray(values);
     }
 
     @JRubyMethod(name = {"size", "length"} )
     public RubyFixnum size() {
         return getRuntime().newFixnum(values.length);
     }
 
     public IRubyObject eachInternal(ThreadContext context, Block block) {
         for (int i = 0; i < values.length; i++) {
             block.yield(context, values[i]);
         }
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each(final ThreadContext context, final Block block) {
         return block.isGiven() ? eachInternal(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     public IRubyObject each_pairInternal(ThreadContext context, Block block) {
         RubyArray member = (RubyArray) getInternalVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0; i < values.length; i++) {
             block.yield(context, getRuntime().newArrayNoCopy(new IRubyObject[]{member.eltInternal(i), values[i]}));
         }
 
         return this;
     }
 
     @JRubyMethod
     public IRubyObject each_pair(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_pairInternal(context, block) : enumeratorize(context.getRuntime(), this, "each_pair");
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject aref(IRubyObject key) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return getByName(key.asJavaString());
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         return values[idx];
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return setByName(key.asJavaString(), value);
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         modify();
         return values[idx] = value;
     }
     
     // FIXME: This is copied code from RubyArray.  Both RE, Struct, and Array should share one impl
     // This is also hacky since I construct ruby objects to access ruby arrays through aref instead
     // of something lower.
     @JRubyMethod(rest = true)
     public IRubyObject values_at(IRubyObject[] args) {
         int olen = values.length;
         RubyArray result = getRuntime().newArray(args.length);
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyFixnum) {
                 result.append(aref(args[i]));
                 continue;
             }
 
             int beglen[];
             if (!(args[i] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[i]).begLenInt(olen, 0)) == null) {
                 continue;
             } else {
                 int beg = beglen[0];
                 int len = beglen[1];
                 int end = len;
                 for (int j = 0; j < end; j++) {
                     result.append(aref(getRuntime().newFixnum(j + beg)));
                 }
                 continue;
             }
             result.append(aref(getRuntime().newFixnum(RubyNumeric.num2long(args[i]))));
         }
 
         return result;
     }
 
     public static void marshalTo(RubyStruct struct, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(struct);
         output.dumpDefaultObjectHeader('S', struct.getMetaClass());
 
         RubyArray array = (RubyArray)getInternalVariable(struct.classOf(), "__member__");
         output.writeInt(array.size());
 
         for (int i = 0; i < array.size(); i++) {
             RubySymbol name = (RubySymbol) array.eltInternal(i);
             output.dumpObject(name);
             output.dumpObject(struct.values[i]);
         }
     }
 
     public static RubyStruct unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         Ruby runtime = input.getRuntime();
 
         RubySymbol className = (RubySymbol) input.unmarshalObject(false);
         RubyClass rbClass = pathToClass(runtime, className.asJavaString());
         if (rbClass == null) {
             throw runtime.newNameError("uninitialized constant " + className, className.asJavaString());
         }
 
         RubyArray mem = members(rbClass, Block.NULL_BLOCK);
 
         int len = input.unmarshalInt();
         IRubyObject[] values;
         if (len == 0) {
             values = IRubyObject.NULL_ARRAY;
         } else {
             values = new IRubyObject[len];
             RuntimeHelpers.fillNil(values, runtime);
         }
 
         // FIXME: This could all be more efficient, but it's how struct works
         RubyStruct result;
         if (runtime.is1_9()) {
             // 1.9 does not appear to call initialize (JRUBY-5875)
             result = new RubyStruct(runtime, rbClass);
         } else {
             result = newStruct(rbClass, values, Block.NULL_BLOCK);
         }
         input.registerLinkTarget(result);
         
         for(int i = 0; i < len; i++) {
             IRubyObject slot = input.unmarshalObject(false);
             if(!mem.eltInternal(i).toString().equals(slot.toString())) {
                 throw runtime.newTypeError("struct " + rbClass.getName() + " not compatible (:" + slot + " for :" + mem.eltInternal(i) + ")");
             }
             result.aset(runtime.newFixnum(i), input.unmarshalObject());
         }
         return result;
     }
 
     private static RubyClass pathToClass(Ruby runtime, String path) {
         // FIXME: Throw the right ArgumentError's if the class is missing
         // or if it's a module.
         return (RubyClass) runtime.getClassFromPath(path);
     }
     
     private static ObjectAllocator STRUCT_INSTANCE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyStruct instance = new RubyStruct(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     @Override
     @JRubyMethod(required = 1)
     public IRubyObject initialize_copy(IRubyObject arg) {
         if (this == arg) return this;
         RubyStruct original = (RubyStruct) arg;
         
         values = new IRubyObject[original.values.length];
         System.arraycopy(original.values, 0, values, 0, original.values.length);
 
         return this;
     }
     
 }
diff --git a/src/org/jruby/ast/ArgsNode.java b/src/org/jruby/ast/ArgsNode.java
index feed6341d4..47d9d40a78 100644
--- a/src/org/jruby/ast/ArgsNode.java
+++ b/src/org/jruby/ast/ArgsNode.java
@@ -1,453 +1,455 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Mirko Stocker <me@misto.ch>
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
 // generated from: src/org/jruby/ast/ArgsNode.erb
 // using arities: src/org/jruby/ast/ArgsNode.arities.erb
 ////////////////////////////////////////////////////////////////////////////////
 
 
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Represents the argument declarations of a method.  The fields:
  * foo(p1, ..., pn, o1 = v1, ..., on = v2, *r, q1, ..., qn)
  *
  * p1...pn = pre arguments
  * o1...on = optional arguments
  * r       = rest argument
  * q1...qn = post arguments (only in 1.9)
  */
 public class ArgsNode extends Node {
     private final ListNode pre;
     private final int preCount;
     private final ListNode optArgs;
     protected final ArgumentNode restArgNode;
     protected final int restArg;
     private final BlockArgNode blockArgNode;
     protected Arity arity;
     private final int requiredArgsCount;
     protected final boolean hasOptArgs;
     protected final boolean hasMasgnArgs;
     protected int maxArgsCount;
     protected final boolean isSimple;
 
     // Only in ruby 1.9 methods
     private final ListNode post;
     private final int postCount;
     private final int postIndex;
     /**
      *
      * @param optionalArguments  Node describing the optional arguments
      * 				This Block will contain assignments to locals (LAsgnNode)
      * @param restArguments  index of the rest argument in the local table
      * 				(the array argument prefixed by a * which collects
      * 				all additional params)
      * 				or -1 if there is none.
      * @param argsCount number of regular arguments
      * @param restArgNode The rest argument (*args).
      * @param blockArgNode An optional block argument (&amp;arg).
      **/
     public ArgsNode(ISourcePosition position, ListNode pre, ListNode optionalArguments,
             RestArgNode rest, ListNode post, BlockArgNode blockArgNode) {
         super(position);
 
         this.pre = pre;
         this.preCount = pre == null ? 0 : pre.size();
         this.post = post;
         this.postCount = post == null ? 0 : post.size();
         int optArgCount = optionalArguments == null ? 0 : optionalArguments.size();
         this.postIndex = getPostCount(preCount, optArgCount, rest);
         this.optArgs = optionalArguments;
         this.restArg = rest == null ? -1 : rest.getIndex();
         this.restArgNode = rest;
         this.blockArgNode = blockArgNode;
         this.requiredArgsCount = preCount + postCount;
         this.hasOptArgs = getOptArgs() != null;
         this.hasMasgnArgs = hasMasgnArgs();
         this.maxArgsCount = getRestArg() >= 0 ? -1 : getRequiredArgsCount() + getOptionalArgsCount();
         this.arity = calculateArity();
 
         this.isSimple = !(hasMasgnArgs || hasOptArgs || restArg >= 0 || postCount > 0);
     }
 
     private int getPostCount(int preCount, int optArgCount, RestArgNode rest) {
         // Simple-case: If we have a rest we know where it is
         if (rest != null) return rest.getIndex() + 1;
 
         return preCount + optArgCount;
     }
 
     public NodeType getNodeType() {
         return NodeType.ARGSNODE;
     }
 
     protected Arity calculateArity() {
         if (getOptArgs() != null || getRestArg() >= 0) return Arity.required(getRequiredArgsCount());
 
         return Arity.createArity(getRequiredArgsCount());
     }
 
     protected boolean hasMasgnArgs() {
         if (preCount > 0) for (Node node : pre.childNodes()) {
             if (node instanceof AssignableNode) return true;
         }
         if (postCount > 0) for (Node node : post.childNodes()) {
             if (node instanceof AssignableNode) return true;
         }
         return false;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitArgsNode(this);
     }
 
     /**
      * Gets the required arguments at the beginning of the argument definition
      */
     public ListNode getPre() {
         return pre;
     }
 
     @Deprecated
     public ListNode getArgs() {
         return pre;
     }
 
     public Arity getArity() {
         return arity;
     }
 
     public int getRequiredArgsCount() {
         return requiredArgsCount;
     }
 
     public int getOptionalArgsCount() {
         return optArgs == null ? 0 : optArgs.size();
     }
 
     public ListNode getPost() {
         return post;
     }
 
     public int getMaxArgumentsCount() {
         return maxArgsCount;
     }
 
     /**
      * Gets the optArgs.
      * @return Returns a ListNode
      */
     public ListNode getOptArgs() {
         return optArgs;
     }
 
     /**
      * Gets the restArg.
      * @return Returns a int
      */
     public int getRestArg() {
         return restArg;
     }
 
     /**
      * Gets the restArgNode.
      * @return Returns an ArgumentNode
      */
     public ArgumentNode getRestArgNode() {
         return restArgNode;
     }
 
     @Deprecated
     public BlockArgNode getBlockArgNode() {
         return blockArgNode;
     }
 
     /**
      * Gets the explicit block argument of the parameter list (&block).
      *
      * @return Returns a BlockArgNode
      */
     public BlockArgNode getBlock() {
         return blockArgNode;
     }
 
     public int getPostCount() {
         return postCount;
     }
 
     public int getPostIndex() {
         return postIndex;
     }
 
     public int getPreCount() {
         return preCount;
     }
 
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject[] args, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (!hasMasgnArgs) {
             // no arg grouping, just use bulk assignment methods
             if (preCount > 0) scope.setArgValues(args, Math.min(args.length, preCount));
             if (postCount > 0 && args.length > preCount) scope.setEndArgValues(args, postIndex, Math.min(args.length - preCount, postCount));
         } else {
             masgnAwareArgAssign(context, runtime, self, args, block, scope);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) prepareOptOrRestArgs(context, runtime, scope, self, args);
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
 
     private void masgnAwareArgAssign(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject[] args, Block block, DynamicScope scope) {
         // arg grouping, use slower arg walking logic
         if (preCount > 0) {
             int size = pre.size();
             for (int i = 0; i < size && i < args.length; i++) {
                 Node next = pre.get(i);
                 if (next instanceof AssignableNode) {
                     ((AssignableNode)next).assign(runtime, context, self, args[i], block, false);
                 } else if (next instanceof ArgumentNode) {
                     ArgumentNode argNode = (ArgumentNode) next;
                     scope.setValue(argNode.getIndex(), args[i], argNode.getDepth());
                 } else {
                     // TODO: Replace with assert later
                     throw new RuntimeException("Whoa..not assignable and not an argument...what is it: " + next);
                 }
             }
         }
         if (postCount > 0) {
             int size = post.size();
             int argsLength = args.length;
             for (int i = 0; i < size; i++) {
                 Node next = post.get(i);
                 if (next instanceof AssignableNode) {
                     ((AssignableNode)next).assign(runtime, context, self, args[argsLength - postCount + i], block, false);
                 } else if (next instanceof ArgumentNode) {
                     ArgumentNode argNode = (ArgumentNode) next;
                     scope.setValue(argNode.getIndex(), args[argsLength - postCount + i], argNode.getDepth());
                 } else {
                     // TODO: Replace with assert later
                     throw new RuntimeException("Whoa..not assignable and not an argument...what is it: " + next);
                 }
             }
         }
     }
 
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues();
         } else {
             prepare(context, runtime, self, IRubyObject.NULL_ARRAY, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4, arg5);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
     public void prepare(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, IRubyObject arg9, Block block) {
         DynamicScope scope = context.getCurrentScope();
 
         if (isSimple) {
             scope.setArgValues(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
         } else {
             prepare(context, runtime, self, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9}, block);
         }
         if (getBlock() != null) processBlockArg(scope, runtime, block);
     }
 
-
     public void checkArgCount(Ruby runtime, int argsLength) {
-//        arity.checkArity(runtime, argsLength);
         Arity.checkArgumentCount(runtime, argsLength, requiredArgsCount, maxArgsCount);
     }
 
+    public void checkArgCount(Ruby runtime, String name, int argsLength) {
+        Arity.checkArgumentCount(runtime, name, argsLength, requiredArgsCount, maxArgsCount);
+    }
+
     protected void prepareOptOrRestArgs(ThreadContext context, Ruby runtime, DynamicScope scope,
             IRubyObject self, IRubyObject[] args) {
         prepareRestArg(context, runtime, scope, args, prepareOptionalArguments(context, runtime, self, args));
     }
 
     protected int prepareOptionalArguments(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject[] args) {
         return hasOptArgs ? assignOptArgs(args, runtime, context, self, preCount) : preCount;
     }
 
     protected void prepareRestArg(ThreadContext context, Ruby runtime, DynamicScope scope,
             IRubyObject[] args, int givenArgsCount) {
         if (restArg >= 0) {
             int sizeOfRestArg = args.length - postCount - givenArgsCount;
             if (sizeOfRestArg <= 0) { // no more values to stick in rest arg
                 scope.setValue(restArg, RubyArray.newArray(runtime), 0);
             } else {
                 scope.setValue(restArg, RubyArray.newArrayNoCopy(runtime, args, givenArgsCount, sizeOfRestArg), 0);
             }
         }
     }
 
     protected int assignOptArgs(IRubyObject[] args, Ruby runtime, ThreadContext context, IRubyObject self, int givenArgsCount) {
         // assign given optional arguments to their variables
         int j = 0;
         for (int i = preCount; i < args.length - postCount && j < optArgs.size(); i++, j++) {
             // in-frame EvalState should already have receiver set as self, continue to use it
             optArgs.get(j).assign(runtime, context, self, args[i], Block.NULL_BLOCK, true);
             givenArgsCount++;
         }
 
         // assign the default values, adding to the end of allArgs
         for (int i = 0; j < optArgs.size(); i++, j++) {
             optArgs.get(j).interpret(runtime, context, self, Block.NULL_BLOCK);
         }
 
         return givenArgsCount;
     }
 
     protected void processBlockArg(DynamicScope scope, Ruby runtime, Block block) {
         scope.setValue(getBlock().getCount(), RuntimeHelpers.processBlockArgument(runtime, block), 0);
     }
 
     public List<Node> childNodes() {
         if (post != null) return Node.createList(pre, optArgs, restArgNode, post, blockArgNode);
 
         return Node.createList(pre, optArgs, restArgNode, blockArgNode);
     }
 }
diff --git a/src/org/jruby/ext/ffi/jffi/NativeInvoker.java b/src/org/jruby/ext/ffi/jffi/NativeInvoker.java
index 5fa11c6a31..3e13e1678d 100644
--- a/src/org/jruby/ext/ffi/jffi/NativeInvoker.java
+++ b/src/org/jruby/ext/ffi/jffi/NativeInvoker.java
@@ -1,103 +1,103 @@
 
 package org.jruby.ext.ffi.jffi;
 
 import com.kenai.jffi.CallContext;
 import org.jruby.RubyModule;
 import org.jruby.ext.ffi.CallbackInfo;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  */
 abstract public class NativeInvoker extends DynamicMethod {
     protected final Arity arity;
     protected final com.kenai.jffi.Function function;
     private final int cbIndex;
     private final NativeCallbackFactory cbFactory;
     private final Signature signature;
 
 
     public NativeInvoker(RubyModule implementationClass, com.kenai.jffi.Function function, Signature signature) {
         super(implementationClass, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone);
         this.arity = Arity.fixed(signature.getParameterCount());
         this.function = function;
         this.signature = signature;
 
         int cbIndex = -1;
         NativeCallbackFactory cbFactory = null;
         for (int i = 0; i < signature.getParameterCount(); ++i) {
             if (signature.getParameterType(i) instanceof CallbackInfo) {
                 cbFactory = CallbackManager.getInstance().getCallbackFactory(implementationClass.getRuntime(),
                         (CallbackInfo) signature.getParameterType(i));
                 cbIndex = i;
                 break;
             }
         }
         this.cbIndex = cbIndex;
         this.cbFactory = cbFactory;
     }
 
     @Override
     public final DynamicMethod dup() {
         return this;
     }
 
     @Override
     public final Arity getArity() {
         return arity;
     }
     @Override
     public final boolean isNative() {
         return true;
     }
 
     Signature getSignature() {
         return signature;
     }
 
     CallContext getCallContext() {
         return function.getCallContext();
     }
 
     long getFunctionAddress() {
         return function.getFunctionAddress();
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self,
                             RubyModule clazz, String name, IRubyObject[] args, Block block) {
 
         if (!block.isGiven() || cbIndex < 0) {
             arity.checkArity(context.getRuntime(), args);
             return call(context, self, clazz, name, args);
 
         } else {
-            Arity.checkArgumentCount(context.getRuntime(), args,
+            Arity.checkArgumentCount(context.runtime, name, args,
                     arity.getValue() - 1, arity.getValue());
 
             IRubyObject[] params = new IRubyObject[arity.getValue()];
             for (int i = 0; i < cbIndex; i++) {
                 params[i] = args[i];
             }
 
             NativeCallbackPointer cb;
             params[cbIndex] = cb = cbFactory.newCallback(block);
 
             for (int i = cbIndex + 1; i < params.length; i++) {
                 params[i] = args[i - 1];
             }
 
             try {
                 return call(context, self, clazz, name, params);
             } finally {
                 cb.dispose();
             }
         }
     }
 }
diff --git a/src/org/jruby/ext/socket/RubyUNIXSocket.java b/src/org/jruby/ext/socket/RubyUNIXSocket.java
index b8dca78a98..969b863ef7 100644
--- a/src/org/jruby/ext/socket/RubyUNIXSocket.java
+++ b/src/org/jruby/ext/socket/RubyUNIXSocket.java
@@ -1,325 +1,324 @@
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
  * Copyright (C) 2008 Ola Bini <ola.bini@gmail.com>
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
 package org.jruby.ext.socket;
 
 import jnr.constants.platform.Fcntl;
 import jnr.constants.platform.OpenFlags;
 import jnr.constants.platform.SocketLevel;
 import jnr.constants.platform.SocketOption;
 import jnr.ffi.LastError;
 import jnr.unixsocket.UnixServerSocket;
 import jnr.unixsocket.UnixServerSocketChannel;
 import jnr.unixsocket.UnixSocketAddress;
 import jnr.unixsocket.UnixSocketChannel;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 
 import java.io.File;
 import java.io.IOException;
 import java.nio.channels.Channel;
 
 
 @JRubyClass(name="UNIXSocket", parent="BasicSocket")
 public class RubyUNIXSocket extends RubyBasicSocket {
     private static ObjectAllocator UNIXSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyUNIXSocket(runtime, klass);
         }
     };
 
     static void createUNIXSocket(Ruby runtime) {
         RubyClass rb_cUNIXSocket = runtime.defineClass("UNIXSocket", runtime.getClass("BasicSocket"), UNIXSOCKET_ALLOCATOR);
         runtime.getObject().setConstant("UNIXsocket", rb_cUNIXSocket);
         
         rb_cUNIXSocket.defineAnnotatedMethods(RubyUNIXSocket.class);
     }
 
     public RubyUNIXSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject path) {
         init_unixsock(context.runtime, path, false);
 
         return context.nil;
     }
 
     @JRubyMethod
     public IRubyObject recvfrom(ThreadContext context, IRubyObject _length) {
         Ruby runtime = context.runtime;
 
         IRubyObject result = recv(context, _length);
 
         IRubyObject addressArray = runtime.newArray(
                 runtime.newString("AF_UNIX"),
                 RubyString.newEmptyString(runtime));
 
         return runtime.newArray(result, addressArray);
     }
 
     @JRubyMethod
     public IRubyObject path(ThreadContext context) {
         return RubyString.newEmptyString(context.runtime);
     }
 
     @JRubyMethod
     public IRubyObject addr(ThreadContext context) {
         Ruby runtime = context.runtime;
 
         return runtime.newArray(
                 runtime.newString("AF_UNIX"),
                 RubyString.newEmptyString(runtime));
     }
 
     @JRubyMethod
     public IRubyObject peeraddr(ThreadContext context) {
         Ruby runtime = context.runtime;
 
         return runtime.newArray(
                 runtime.newString("AF_UNIX"),
                 runtime.newString(fpath));
     }
 
     @JRubyMethod(name = "recvfrom", required = 1, optional = 1)
     public IRubyObject recvfrom(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
 
         IRubyObject _length = args[0];
         IRubyObject _flags;
 
         if(args.length == 2) {
             _flags = args[1];
         } else {
             _flags = runtime.getNil();
         }
 
         // TODO
         int flags;
 
         _length = args[0];
 
         if(_flags.isNil()) {
             flags = 0;
         } else {
             flags = RubyNumeric.fix2int(_flags);
         }
 
         return runtime.newArray(
                 recv(context, _length),
                 peeraddr(context));
     }
 
     @JRubyMethod(notImplemented = true)
     public IRubyObject send_io(IRubyObject path) {
         //TODO: implement, won't do this now
         return  getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true, notImplemented = true)
     public IRubyObject recv_io(IRubyObject[] args) {
         //TODO: implement, won't do this now
         return  getRuntime().getNil();
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path) {
         return RuntimeHelpers.invoke(context, recv, "new", path);
     }
 
     @JRubyMethod(name = {"socketpair", "pair"}, optional = 2, meta = true)
     public static IRubyObject socketpair(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
-        Arity.checkArgumentCount(runtime, args, 0, 2);
 
         // TODO: type and protocol
 
         UnixSocketChannel[] sp;
 
         try {
             sp = UnixSocketChannel.pair();
 
             RubyUNIXSocket sock = (RubyUNIXSocket)(RuntimeHelpers.invoke(context, runtime.getClass("UNIXSocket"), "allocate"));
             sock.channel = sp[0];
             sock.fpath = "";
             sock.init_sock(runtime);
 
             RubyUNIXSocket sock2 = (RubyUNIXSocket)(RuntimeHelpers.invoke(context, runtime.getClass("UNIXSocket"), "allocate"));
             sock2.channel = sp[1];
             sock2.fpath = "";
             sock2.init_sock(runtime);
 
             return runtime.newArray(sock, sock2);
 
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
 
         }
     }
 
     @Override
     public IRubyObject close() {
         Ruby runtime = getRuntime();
 
         super.close();
 
         try {
             channel.close();
 
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
 
         return runtime.getNil();
     }
 
     @Override
     public IRubyObject setsockopt(ThreadContext context, IRubyObject _level, IRubyObject _opt, IRubyObject val) {
         SocketLevel level = levelFromArg(_level);
         SocketOption opt = optionFromArg(_opt);
 
         switch(level) {
             case SOL_SOCKET:
                 switch(opt) {
                     case SO_KEEPALIVE: {
                         // TODO: socket options
                     }
                     break;
                     default:
                         throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
                 break;
             default:
                 throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return context.getRuntime().newFixnum(0);
     }
 
     protected static void rb_sys_fail(Ruby runtime, String message) {
         final int n = LastError.getLastError(jnr.ffi.Runtime.getSystemRuntime());
 
         RubyClass instance = runtime.getErrno(n);
 
         if(instance == null) {
             throw runtime.newSystemCallError(message);
 
         } else {
             throw runtime.newErrnoFromInt(n, message);
         }
     }
 
     protected void init_unixsock(Ruby runtime, IRubyObject _path, boolean server) {
         ByteList path = _path.convertToString().getByteList();
         fpath = path.toString();
 
         int maxSize = 103; // Max size from Darwin, lowest common value we know of
         if (fpath.length() > 103) {
             throw runtime.newArgumentError("too long unix socket path (max: " + maxSize + "bytes)");
         }
 
         try {
             if(server) {
                 UnixServerSocketChannel channel = UnixServerSocketChannel.open();
                 UnixServerSocket socket = channel.socket();
 
                 socket.bind(new UnixSocketAddress(new File(fpath)));
 
                 this.channel = channel;
 
             } else {
                 File fpathFile = new File(fpath);
 
                 if (!fpathFile.exists()) {
                     throw runtime.newErrnoENOENTError("unix socket");
                 }
                 
                 UnixSocketChannel channel = UnixSocketChannel.open();
 
                 channel.connect(new UnixSocketAddress(fpathFile));
 
                 this.channel = channel;
 
             }
 
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
 
         if(server) {
             // TODO: listen backlog
         }
 
         init_sock(runtime);
 
         if(server) {
             openFile.setPath(fpath);
         }
     }
 
     protected void init_sock(Ruby runtime) {
         try {
             ModeFlags modes = newModeFlags(runtime, ModeFlags.RDWR);
 
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel, modes)));
             openFile.setPipeStream(openFile.getMainStreamSafe());
             openFile.setMode(modes.getOpenFileFlags());
             openFile.getMainStreamSafe().setSync(true);
 
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
     }
 
     private UnixSocketChannel asUnixSocket() {
         return (UnixSocketChannel)channel;
     }
 
     protected Channel channel;
     protected String fpath;
 
     protected final static int F_GETFL = Fcntl.F_GETFL.intValue();
     protected final static int F_SETFL = Fcntl.F_SETFL.intValue();
 
     protected final static int O_NONBLOCK = OpenFlags.O_NONBLOCK.intValue();
 }
diff --git a/src/org/jruby/internal/runtime/methods/InterpretedMethod.java b/src/org/jruby/internal/runtime/methods/InterpretedMethod.java
index 2c392234a0..9446960928 100644
--- a/src/org/jruby/internal/runtime/methods/InterpretedMethod.java
+++ b/src/org/jruby/internal/runtime/methods/InterpretedMethod.java
@@ -1,339 +1,339 @@
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
 import org.jruby.compiler.ASTInspector;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.PositionAware;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  */
 public class InterpretedMethod extends DynamicMethod implements MethodArgs, PositionAware {
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private ISourcePosition position;
     private String file;
     private int line;
     private boolean needsScope;
 
     public InterpretedMethod(RubyModule implementationClass, StaticScope staticScope, Node body,
             String name, ArgsNode argsNode, Visibility visibility, ISourcePosition position) {
         super(implementationClass, visibility, CallConfiguration.FrameFullScopeFull, name);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
         this.position = position;
         
         // we get these out ahead of time
         this.file = position.getFile();
         this.line = position.getLine();
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(body);
         inspector.inspect(argsNode);
 
         // This optimization is temporarily disabled because of the complications
         // arising from moving backref/lastline into scope and not being able
         // to accurately detect that situation.
 //        if (inspector.hasClosure() || inspector.hasScopeAwareMethods() || staticScope.getNumberOfVariables() != 0) {
 //            // must have scope
             needsScope = true;
 //        } else {
 //            needsScope = false;
 //        }
 		
         assert argsNode != null;
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
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, block, runtime);
-            argsNode.checkArgCount(runtime, args.length);
+            argsNode.checkArgCount(runtime, name, args.length);
             argsNode.prepare(context, runtime, self, args, block);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, block, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
         return call(context, self, clazz, name, args, Block.NULL_BLOCK);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, Block.NULL_BLOCK, runtime);
-            argsNode.checkArgCount(runtime, 0);
+            argsNode.checkArgCount(runtime, name, 0);
             argsNode.prepare(context, runtime, self, Block.NULL_BLOCK);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, Block.NULL_BLOCK, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, block, runtime);
-            argsNode.checkArgCount(runtime, 0);
+            argsNode.checkArgCount(runtime, name, 0);
             argsNode.prepare(context, runtime, self, block);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, block, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, Block.NULL_BLOCK, runtime);
-            argsNode.checkArgCount(runtime, 1);
+            argsNode.checkArgCount(runtime, name, 1);
             argsNode.prepare(context, runtime, self, arg0, Block.NULL_BLOCK);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, Block.NULL_BLOCK, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, block, runtime);
-            argsNode.checkArgCount(runtime, 1);
+            argsNode.checkArgCount(runtime, name, 1);
             argsNode.prepare(context, runtime, self, arg0, block);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, block, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, Block.NULL_BLOCK, runtime);
-            argsNode.checkArgCount(runtime, 2);
+            argsNode.checkArgCount(runtime, name, 2);
             argsNode.prepare(context, runtime, self, arg0, arg1, Block.NULL_BLOCK);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, Block.NULL_BLOCK, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, block, runtime);
-            argsNode.checkArgCount(runtime, 2);
+            argsNode.checkArgCount(runtime, name, 2);
             argsNode.prepare(context, runtime, self, arg0, arg1, block);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, block, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, Block.NULL_BLOCK, runtime);
-            argsNode.checkArgCount(runtime, 3);
+            argsNode.checkArgCount(runtime, name, 3);
             argsNode.prepare(context, runtime, self, arg0, arg1, arg2, Block.NULL_BLOCK);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, Block.NULL_BLOCK, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         Ruby runtime = context.getRuntime();
         int callNumber = context.callNumber;
 
         try {
             pre(context, name, self, block, runtime);
-            argsNode.checkArgCount(runtime, 3);
+            argsNode.checkArgCount(runtime, name, 3);
             argsNode.prepare(context, runtime, self, arg0, arg1, arg2, block);
 
             return ASTInterpreter.INTERPRET_METHOD(runtime, context, file, line, getImplementationClass(), body, name, self, block, isTraceable());
         } catch (JumpException.ReturnJump rj) {
             return handleReturn(context, rj, callNumber);
         } catch (JumpException.RedoJump rj) {
             return handleRedo(runtime);
         } catch (JumpException.BreakJump bj) {
             return handleBreak(context, runtime, bj, callNumber);
         } finally {
             post(runtime, context, name);
         }
     }
 
 
     protected void pre(ThreadContext context, String name, IRubyObject self, Block block, Ruby runtime) {
         if (needsScope) {
             context.preMethodFrameAndScope(getImplementationClass(), name, self, block, staticScope);
         } else {
             context.preMethodFrameAndDummyScope(getImplementationClass(), name, self, block, staticScope);
         }
     }
 
     protected void post(Ruby runtime, ThreadContext context, String name) {
         context.postMethodFrameAndScope();
     }
 
     protected boolean isTraceable() {
         return false;
     }
 
     public ISourcePosition getPosition() {
         return position;
     }
 
     public String getFile() {
         return position.getFile();
     }
 
     public int getLine() {
         return position.getLine();
     }
 
     @Override
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new InterpretedMethod(getImplementationClass(), staticScope, body, name, argsNode, getVisibility(), position);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/JavaMethod.java b/src/org/jruby/internal/runtime/methods/JavaMethod.java
index fec23845c6..897f73fa72 100644
--- a/src/org/jruby/internal/runtime/methods/JavaMethod.java
+++ b/src/org/jruby/internal/runtime/methods/JavaMethod.java
@@ -1,1058 +1,1058 @@
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
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  */
 public abstract class JavaMethod extends DynamicMethod implements Cloneable {
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
     
     public JavaMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
         super(implementationClass, visibility, callConfig, name);
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
     
     protected final void callTrace(ThreadContext context, boolean enabled, String name) {
         if (enabled) context.trace(RubyEvent.C_CALL, name, getImplementationClass());
     }
     
     protected final void returnTrace(ThreadContext context, boolean enabled, String name) {
         if (enabled) context.trace(RubyEvent.C_RETURN, name, getImplementationClass());
     }
 
     protected final void callTraceCompiled(ThreadContext context, boolean enabled, String name, String file, int line) {
         if (enabled) context.trace(RubyEvent.CALL, name, getImplementationClass(), file, line);
     }
 
     protected final void returnTraceCompiled(ThreadContext context, boolean enabled, String name) {
         if (enabled) context.trace(RubyEvent.RETURN, name, getImplementationClass());
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
-            Arity.raiseArgumentError(context.getRuntime(), given, min, max);
+            Arity.raiseArgumentError(context.runtime, name, given, min, max);
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
         public JavaMethodNBlock(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
         public JavaMethodN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
         public JavaMethodZeroOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
         public JavaMethodOneOrN(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
         public JavaMethodZero(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
         public JavaMethodOne(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
             super(implementationClass, visibility, callConfig, name);
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
diff --git a/src/org/jruby/internal/runtime/methods/ReflectedJavaMethod.java b/src/org/jruby/internal/runtime/methods/ReflectedJavaMethod.java
index 15487b9697..56401e90e4 100644
--- a/src/org/jruby/internal/runtime/methods/ReflectedJavaMethod.java
+++ b/src/org/jruby/internal/runtime/methods/ReflectedJavaMethod.java
@@ -1,196 +1,196 @@
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
 import java.lang.reflect.Modifier;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ReflectedJavaMethod extends JavaMethod {
     private final Method method;
     
     private final boolean needsBlock;
     private final boolean isStatic;
     private final int required;
     private final int optional;
     private final boolean rest;
     private final int max;
     
     private final boolean argsAsIs;
 
     private final boolean needsThreadContext;
 
     public ReflectedJavaMethod(
             RubyModule implementationClass, Method method, JRubyMethod annotation) {
         super(implementationClass, annotation.visibility());
         
         this.method = method;
         
         Class<?>[] params = method.getParameterTypes();
         this.needsBlock = params.length > 0 && params[params.length - 1] == Block.class;
         this.isStatic = Modifier.isStatic(method.getModifiers());
         
         Arity arity = Arity.fromAnnotation(annotation, params, this.isStatic);
         setArity(arity);
 
         this.required = arity.getValue() >= 0 ? arity.getValue() : Math.abs(arity.getValue())-1;
         this.optional = annotation.optional();
         this.rest = annotation.rest();
         
         this.needsThreadContext = params.length > 0 && params[0] == ThreadContext.class;
         this.argsAsIs = ! isStatic && optional == 0 && !rest && !needsBlock && !needsThreadContext;
         
         if (rest) {
             max = -1;
         } else {
             max = required + optional;
         }
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name,
             IRubyObject[] args, Block block) {
-        Ruby runtime = context.getRuntime();
-        Arity.checkArgumentCount(runtime, args, required, max);
+        Ruby runtime = context.runtime;
+        Arity.checkArgumentCount(runtime, name, args, required, max);
         
         callConfig.pre(context, self, getImplementationClass(), name, block, null);
         
         try {
             if (! isStatic && ! method.getDeclaringClass().isAssignableFrom(self.getClass())) {
                 throw new ClassCastException(
                         self.getClass().getName() + " cannot be converted to " +
                         method.getDeclaringClass().getName());
             }
             
             if (argsAsIs) {
                 boolean isTrace = runtime.hasEventHooks();
                 try {
                     if (isTrace) {
                         runtime.callEventHooks(context, RubyEvent.C_CALL, context.getFile(), context.getLine(), name, getImplementationClass());
                     }  
                     return (IRubyObject)method.invoke(self, (Object[])args);
                 } finally {
                     if (isTrace) {
                         runtime.callEventHooks(context, RubyEvent.C_RETURN, context.getFile(), context.getLine(), name, getImplementationClass());
                     }
                 }                    
             } else {
                 int argsLength = calcArgsLength();
                 
                 Object[] params = new Object[argsLength];
                 int offset = 0;
                 if (needsThreadContext) {
                     params[offset++] = context;
                 }
                 if (isStatic) {
                     params[offset++] = self;
                 }
                 if (required < 4 && optional == 0 && !rest) {
                     for (int i = 0; i < args.length; i++) {
                         if (method.getParameterTypes()[offset] == RubyString.class) {
                             params[offset++] = args[i].convertToString();
                         } else {
                             params[offset++] = args[i];
                         }
                     }
                 } else {
                     params[offset++] = args;
                 }
                 if (needsBlock) {
                     params[offset++] = block;
                 }
                 
                 boolean isTrace = runtime.hasEventHooks();
                 try {
                     if (isTrace) {
                         runtime.callEventHooks(context, RubyEvent.C_CALL, context.getFile(), context.getLine(), name, getImplementationClass());
                     }
                     IRubyObject result;
                     if (isStatic) {
                         result = (IRubyObject)method.invoke(null, params);
                     } else {
                         result = (IRubyObject)method.invoke(self, params);
                     }
 
                     return result == null ? runtime.getNil() : result;
                 } finally {
                     if (isTrace) {
                         runtime.callEventHooks(context, RubyEvent.C_RETURN, context.getFile(), context.getLine(), name, getImplementationClass());
                     }
                 }
             }
         } catch (IllegalArgumentException e) {
             throw RaiseException.createNativeRaiseException(runtime, e, method);
         } catch (IllegalAccessException e) {
             throw RaiseException.createNativeRaiseException(runtime, e, method);
         } catch (InvocationTargetException e) {
             Throwable cause = e.getCause();
             if (cause instanceof RuntimeException) {
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
 
     private int calcArgsLength() {
         int argsLength = 0;
         
         if (needsThreadContext) {
             argsLength++;
         }
 
         if (isStatic) {
             argsLength++;
         }
         if (required < 4 && optional == 0 && !rest) {
             argsLength += required;
         } else {
             argsLength++;
         }
         if (needsBlock) {
             argsLength++;
         }
         return argsLength;
     }
 }
diff --git a/src/org/jruby/java/proxies/JavaInterfaceTemplate.java b/src/org/jruby/java/proxies/JavaInterfaceTemplate.java
index 9e3622162b..06e91a80db 100644
--- a/src/org/jruby/java/proxies/JavaInterfaceTemplate.java
+++ b/src/org/jruby/java/proxies/JavaInterfaceTemplate.java
@@ -1,384 +1,384 @@
 package org.jruby.java.proxies;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Method;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodN;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodOne;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodOneBlock;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtilities;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaInterfaceTemplate {
     public static RubyModule createJavaInterfaceTemplateModule(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyModule javaInterfaceTemplate = runtime.defineModule("JavaInterfaceTemplate");
 
         RubyClass singleton = javaInterfaceTemplate.getSingletonClass();
         singleton.addReadAttribute(context, "java_class");
         singleton.defineAnnotatedMethods(JavaInterfaceTemplate.class);
 
         return javaInterfaceTemplate;
     }
 
     // not intended to be called directly by users (private)
     // OLD TODO from Ruby code:
     // This should be implemented in JavaClass.java, where we can
     // check for reserved Ruby names, conflicting methods, etc.
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public static IRubyObject implement(ThreadContext context, IRubyObject self, IRubyObject clazz) {
         Ruby runtime = context.getRuntime();
 
         if (!(clazz instanceof RubyModule)) {
             throw runtime.newTypeError(clazz, runtime.getModule());
         }
 
         RubyModule targetModule = (RubyModule)clazz;
         JavaClass javaClass = (JavaClass)self.getInstanceVariables().getInstanceVariable("@java_class");
         
         Method[] javaInstanceMethods = javaClass.javaClass().getMethods();
         DynamicMethod dummyMethod = new org.jruby.internal.runtime.methods.JavaMethod(targetModule, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 // dummy bodies for default impls
                 return context.getRuntime().getNil();
             }
         };
         
         for (int i = 0; i < javaInstanceMethods.length; i++) {
             Method method = javaInstanceMethods[i];
             String name = method.getName();
             if (targetModule.searchMethod(name) != UndefinedMethod.INSTANCE) continue;
             
             targetModule.addMethod(name, dummyMethod);
         }
         
         return runtime.getNil();
     }
 
     // framed for invokeSuper
     @JRubyMethod(frame = true)
     public static IRubyObject append_features(ThreadContext context, IRubyObject self, IRubyObject clazz, Block block) {
         if (clazz instanceof RubyClass) {
             appendFeaturesToClass(context, self, (RubyClass)clazz);
         } else if (clazz instanceof RubyModule) {
             appendFeaturesToModule(context, self, (RubyModule)clazz);
         } else {
             throw context.getRuntime().newTypeError("received " + clazz + ", expected Class/Module");
         }
 
         return RuntimeHelpers.invokeSuper(context, self, clazz, block);
     }
 
     private static void appendFeaturesToClass(ThreadContext context, IRubyObject self, final RubyClass clazz) {
         Ruby runtime = context.getRuntime();
         checkAlreadyReified(clazz, runtime);
 
         IRubyObject javaClassObj = RuntimeHelpers.getInstanceVariable(self, runtime, "@java_class");
         IRubyObject javaInterfaces;
         if (!clazz.hasInstanceVariable("@java_interfaces")) {
             javaInterfaces = RubyArray.newArray(runtime, javaClassObj);
             RuntimeHelpers.setInstanceVariable(javaInterfaces, clazz, "@java_interfaces");
 
             initInterfaceImplMethods(context, clazz);
         } else {
             javaInterfaces = RuntimeHelpers.getInstanceVariable(clazz, runtime, "@java_interfaces");
             // we've already done the above priming logic, just add another interface
             // to the list of intentions unless we're past the point of no return or
             // already intend to implement the given interface
             if (!(javaInterfaces.isFrozen() || ((RubyArray)javaInterfaces).includes(context, javaClassObj))) {
                 ((RubyArray)javaInterfaces).append(javaClassObj);
             }
         }
     }
 
     private static void checkAlreadyReified(final RubyClass clazz, Ruby runtime) throws RaiseException {
         // not allowed for original (non-generated) Java classes
         // note: not allowing for any previously created class right now;
         // this restriction might be loosened later for generated classes
         if ((Java.NEW_STYLE_EXTENSION && clazz.getReifiedClass() != null)
                 ||
                 (clazz.hasInstanceVariable("@java_class")
                     && clazz.getInstanceVariable("@java_class").isTrue()
                     && !clazz.getSingletonClass().isMethodBound("java_proxy_class", false))
                 ||
                 (clazz.hasInstanceVariable("@java_proxy_class")
                     && clazz.getInstanceVariable("@java_proxy_class").isTrue())) {
             throw runtime.newArgumentError("can not add Java interface to existing Java class");
         }
     }
 
     private static void initInterfaceImplMethods(ThreadContext context, RubyClass clazz) {
         // setup new, etc unless this is a ConcreteJavaProxy subclass
         // For JRUBY-4571, check both these, since JavaProxy extension stuff adds the former and this code adds the latter
         if (!(clazz.isMethodBound("__jcreate!", false) || clazz.isMethodBound("__jcreate_meta!", false))) {
             // First we make modifications to the class, to adapt it to being
             // both a Ruby class and a proxy for a Java type
 
             RubyClass singleton = clazz.getSingletonClass();
 
             // list of interfaces we implement
             singleton.addReadAttribute(context, "java_interfaces");
 
             if (
                     (!Java.NEW_STYLE_EXTENSION && clazz.getSuperClass().getRealClass().hasInstanceVariable("@java_class"))
                     || RubyInstanceConfig.INTERFACES_USE_PROXY) {
                 // superclass is a Java class...use old style impl for now
 
                 // The replacement "new" allocates and inits the Ruby object as before, but
                 // also instantiates our proxified Java object by calling __jcreate!
                 final ObjectAllocator proxyAllocator = clazz.getAllocator();
                 clazz.setAllocator(new ObjectAllocator() {
                     public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                         IRubyObject newObj = proxyAllocator.allocate(runtime, klazz);
                         RuntimeHelpers.invoke(runtime.getCurrentContext(), newObj, "__jcreate!");
                         return newObj;
                     }
                 });
 
                 // jcreate instantiates the proxy object which implements all interfaces
                 // and which is wrapped and implemented by this object
                 clazz.addMethod("__jcreate!", new JavaMethodN(clazz, Visibility.PRIVATE) {
 
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                         return jcreateProxy(self);
                     }
                 });
             } else {
                 // The new "new" actually generates a real Java class to use for the Ruby class's
                 // backing store, instantiates that, and then calls initialize on it.
                 addRealImplClassNew(clazz);
             }
 
             // Next, we define a few private methods that we'll use to manipulate
             // the Java object contained within this Ruby object
 
             // Used by our duck-typification of Proc into interface types, to allow
             // coercing a simple proc into an interface parameter.
             clazz.addMethod("__jcreate_meta!", new JavaMethodN(clazz, Visibility.PRIVATE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                     IRubyObject result = jcreateProxy(self);
                     return result;
                 }
             });
 
             // If we hold a Java object, we need a java_class accessor
             clazz.addMethod("java_class", new JavaMethodZero(clazz, Visibility.PUBLIC) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     return ((JavaObject) self.dataGetStruct()).java_class();
                 }
             });
 
             // Because we implement Java interfaces now, we need a new === that's
             // aware of those additional "virtual" supertypes
             if (!clazz.searchMethod("===").isUndefined()) {
                 clazz.defineAlias("old_eqq", "===");
                 clazz.addMethod("===", new JavaMethodOne(clazz, Visibility.PUBLIC) {
 
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                         // TODO: WRONG - get interfaces from class
                         if (arg.respondsTo("java_object")) {
                             IRubyObject interfaces = self.getMetaClass().getInstanceVariables().getInstanceVariable("@java_interfaces");
                             assert interfaces instanceof RubyArray : "interface list was not an array";
 
                             return context.getRuntime().newBoolean(((RubyArray) interfaces).op_diff(
                                     ((JavaClass) ((JavaObject) arg.dataGetStruct()).java_class()).interfaces()).equals(RubyArray.newArray(context.getRuntime())));
                         } else {
                             return RuntimeHelpers.invoke(context, self, "old_eqq", arg);
                         }
                     }
                 });
             }
         }
 
         // Now we add an "implement" and "implement_all" methods to the class
         if (!clazz.isMethodBound("implement", false)) {
             RubyClass singleton = clazz.getSingletonClass();
 
             // implement is called to force this class to create stubs for all
             // methods in the given interface, so they'll show up in the list
             // of methods and be invocable without passing through method_missing
             singleton.addMethod("implement", new JavaMethodOne(clazz, Visibility.PRIVATE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                     IRubyObject javaInterfaces = self.getInstanceVariables().getInstanceVariable("@java_interfaces");
                     if (javaInterfaces != null && ((RubyArray) javaInterfaces).includes(context, arg)) {
                         return RuntimeHelpers.invoke(context, arg, "implement", self);
                     }
                     return context.getRuntime().getNil();
                 }
             });
 
             // implement all forces implementation of all interfaces we intend
             // for this class to implement
             singleton.addMethod("implement_all", new JavaMethodOne(clazz, Visibility.PRIVATE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                     RubyArray javaInterfaces = (RubyArray) self.getInstanceVariables().getInstanceVariable("@java_interfaces");
                     for (int i = 0; i < javaInterfaces.size(); i++) {
                         RuntimeHelpers.invoke(context, JavaUtilities.get_interface_module(self, javaInterfaces.eltInternal(i)), "implement", self);
                     }
                     return javaInterfaces;
                 }
             });
         }
     }
 
     public static void addRealImplClassNew(RubyClass clazz) {
         clazz.setAllocator(new ObjectAllocator() {
             private Constructor proxyConstructor;
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 // if we haven't been here before, reify the class
                 Class reifiedClass = klazz.getReifiedClass();
                 if (proxyConstructor == null || proxyConstructor.getDeclaringClass() != reifiedClass) {
                     if (reifiedClass == null) {
                         reifiedClass = Java.generateRealClass(klazz);
                     }
                     proxyConstructor = Java.getRealClassConstructor(runtime, reifiedClass);
                 }
                 IRubyObject newObj = Java.constructProxy(runtime, proxyConstructor, klazz);
 
                 return newObj;
             }
         });
     }
 
     private static IRubyObject jcreateProxy(IRubyObject self) {
         RubyClass current = self.getMetaClass();
 
         // construct the new interface impl and set it into the object
         IRubyObject newObject = Java.newInterfaceImpl(self, Java.getInterfacesFromRubyClass(current));
         return JavaUtilities.set_java_object(self, self, newObject);
     }
 
     private static void appendFeaturesToModule(ThreadContext context, IRubyObject self, RubyModule module) {
         // assuming the user wants a collection of interfaces that can be
         // included together. make it so.
         
         Ruby runtime = context.getRuntime();
 
         // not allowed for existing Java interface modules
         if (module.getInstanceVariables().hasInstanceVariable("@java_class") &&
                 module.getInstanceVariables().getInstanceVariable("@java_class").isTrue()) {
             throw runtime.newTypeError("can not add Java interface to existing Java interface");
         }
         
         // To turn a module into an "interface collection" we add a class instance
         // variable to hold the list of interfaces, and modify append_features
         // for this module to call append_features on each of those interfaces as
         // well
         synchronized (module) {
             if (!module.getInstanceVariables().hasInstanceVariable("@java_interface_mods")) {
                 RubyArray javaInterfaceMods = RubyArray.newArray(runtime, self);
                 module.getInstanceVariables().setInstanceVariable("@java_interface_mods", javaInterfaceMods);
                 RubyClass singleton = module.getSingletonClass();
 
                 singleton.addMethod("append_features", new JavaMethodOneBlock(singleton, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg, Block block) {
                         if (!(arg instanceof RubyClass)) {
                             throw context.getRuntime().newTypeError("append_features called with non-class");
                         }
                         RubyClass target = (RubyClass)arg;
                         RubyArray javaInterfaceMods = (RubyArray)self.getInstanceVariables().getInstanceVariable("@java_interface_mods");
 
                         target.include(javaInterfaceMods.toJavaArray());
 
                         return RuntimeHelpers.invokeAs(context, clazz.getSuperClass(), self, name, arg, block);
                     }
                 });
             } else {
                 // already set up append_features, just add the interface if we haven't already
                 RubyArray javaInterfaceMods =(RubyArray)module.getInstanceVariables().getInstanceVariable("@java_interface_mods");
                 if (!javaInterfaceMods.includes(context, self)) {
                     javaInterfaceMods.append(self);
                 }
             }
         }
     }
 
     @JRubyMethod
     public static IRubyObject extended(ThreadContext context, IRubyObject self, IRubyObject object) {
         if (!(self instanceof RubyModule)) {
             throw context.getRuntime().newTypeError(self, context.getRuntime().getModule());
         }
         RubyClass singleton = object.getSingletonClass();
         singleton.include(new IRubyObject[] {self});
         return singleton;
     }
 
     @JRubyMethod(name = "[]", rest = true)
     public static IRubyObject op_aref(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return JavaProxy.op_aref(context, self, args);
     }
 
     @JRubyMethod(rest = true)
     public static IRubyObject impl(ThreadContext context, IRubyObject self, IRubyObject[] args, final Block implBlock) {
         Ruby runtime = context.getRuntime();
 
         if (!implBlock.isGiven()) throw runtime.newArgumentError("block required to call #impl on a Java interface");
 
         final RubyArray methodNames = (args.length > 0) ? runtime.newArray(args) : null;
 
         RubyClass implClass = RubyClass.newClass(runtime, runtime.getObject());
         implClass.include(new IRubyObject[] {self});
 
         IRubyObject implObject = implClass.callMethod(context, "new");
 
         implClass.addMethod("method_missing",
                 new org.jruby.internal.runtime.methods.JavaMethod(implClass, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
-                        Arity.checkArgumentCount(context.getRuntime(), args.length, 1, -1);
+                        Arity.checkArgumentCount(context.runtime, name, args.length, 1, -1);
 
                         if (methodNames == null || methodNames.include_p(context, args[0]).isTrue()) {
                             return implBlock.call(context, args);
                         } else {
                             return clazz.getSuperClass().callMethod(context, "method_missing", args, block);
                         }
                     }
                 });
 
         return implObject;
     }
 
     @JRubyMethod(name = "new", rest = true)
     public static IRubyObject rbNew(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         RubyClass implClass = (RubyClass)self.getInstanceVariables().getInstanceVariable("@__implementation");
         if (implClass == null) {
             implClass = RubyClass.newClass(runtime, (RubyClass)runtime.getClass("InterfaceJavaProxy"));
             implClass.include(new IRubyObject[] {self});
             RuntimeHelpers.setInstanceVariable(implClass, self, "@__implementation");
         }
 
         return RuntimeHelpers.invoke(context, implClass, "new", args, block);
     }
 }
diff --git a/src/org/jruby/runtime/Arity.java b/src/org/jruby/runtime/Arity.java
index 860d7b76c8..efabc1c138 100644
--- a/src/org/jruby/runtime/Arity.java
+++ b/src/org/jruby/runtime/Arity.java
@@ -1,256 +1,272 @@
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
 package org.jruby.runtime;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.types.IArityNode;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * The arity of a method is the number of arguments it takes.
  */
 public final class Arity implements Serializable {
     private static final long serialVersionUID = 1L;
     private static final Map<Integer, Arity> arities = new HashMap<Integer, Arity>();
     private final int value;
     
     public final static Arity NO_ARGUMENTS = newArity(0);
     public final static Arity ONE_ARGUMENT = newArity(1);
     public final static Arity TWO_ARGUMENTS = newArity(2);
     public final static Arity THREE_ARGUMENTS = newArity(3);
     public final static Arity OPTIONAL = newArity(-1);
     public final static Arity ONE_REQUIRED = newArity(-2);
     public final static Arity TWO_REQUIRED = newArity(-3);
     public final static Arity THREE_REQUIRED = newArity(-4);
 
     private Arity(int value) {
         this.value = value;
     }
     
     private static Arity createArity(int required, int optional, boolean rest) {
         return createArity((optional > 0 || rest) ? -(required + 1) : required);
     }
 
     public static Arity createArity(int value) {
         switch (value) {
         case -4:
             return THREE_REQUIRED;
         case -3:
             return TWO_REQUIRED;
         case -2:
             return ONE_REQUIRED;
         case -1:
             return OPTIONAL;
         case 0:
             return NO_ARGUMENTS;
         case 1:
             return ONE_ARGUMENT;
         case 2:
             return TWO_ARGUMENTS;
         case 3:
             return THREE_ARGUMENTS;
         }
         return newArity(value);
     }
     
     public static Arity fromAnnotation(JRubyMethod anno) {
         return createArity(anno.required(), anno.optional(), anno.rest());
     }
     
     public static Arity fromAnnotation(JRubyMethod anno, int required) {
         return createArity(required, anno.optional(), anno.rest());
     }
     
     public static Arity fromAnnotation(JRubyMethod anno, Class[] parameterTypes, boolean isStatic) {
         int required;
         if (anno.optional() == 0 && !anno.rest() && anno.required() == 0) {
             // try count specific args to determine required
             int i = parameterTypes.length;
             if (isStatic) i--;
             if (parameterTypes.length > 0) {
                 if (parameterTypes[0] == ThreadContext.class) i--;
                 if (parameterTypes[parameterTypes.length - 1] == Block.class) i--;
             }
 
             required = i;
         } else {
             required = anno.required();
         }
         
         return createArity(required, anno.optional(), anno.rest());
     }
     
     private static Arity newArity(int value) {
         Arity result;
         synchronized (arities) {
             result = arities.get(value);
             if (result == null) {
                 result = new Arity(value);
                 arities.put(value, result);
             }
         }
         return result;
     }
 
     public static Arity fixed(int arity) {
         assert arity >= 0;
         return createArity(arity);
     }
 
     public static Arity optional() {
         return OPTIONAL;
     }
 
     public static Arity required(int minimum) {
         assert minimum >= 0;
         return createArity(-(1 + minimum));
     }
 
     public static Arity noArguments() {
         return NO_ARGUMENTS;
     }
 
     public static Arity singleArgument() {
         return ONE_ARGUMENT;
     }
 
     public static Arity twoArguments() {
         return TWO_ARGUMENTS;
     }
     
     public static Arity procArityOf(Node node) {
         if (node instanceof AttrAssignNode && node != null) {
             node = ((AttrAssignNode) node).getArgsNode();
         }
         if (node == null) {
             return Arity.optional();
         } else if (node instanceof IArityNode) {
             return ((IArityNode) node).getArity();
         } else if (node instanceof CallNode) {
             return Arity.singleArgument();
         } else if (node instanceof ArrayNode) {
             return Arity.singleArgument();
         } else if (node instanceof ArgsNode) {
             return ((ArgsNode)node).getArity();
         }
 
         throw new Error("unexpected type " + node.getClass() + " at " + node.getPosition());
     }
 
     public int getValue() {
         return value;
     }
 
     public void checkArity(Ruby runtime, IRubyObject[] args) {
 		  checkArity(runtime, args.length);
     }
 
     public void checkArity(Ruby runtime, int length) {
         if (isFixed()) {
             if (length != required()) {
                 throw runtime.newArgumentError("wrong number of arguments (" + length + " for " + required() + ")");
             }
         } else {
             if (length < required()) {
                 throw runtime.newArgumentError("wrong number of arguments (" + length + " for " + required() + ")");
             }
         }
     }
 
     public boolean isFixed() {
         return value >= 0;
     }
 
     public int required() {
         return value < 0 ? -(1 + value) : value;
     }
 
     @Override
     public boolean equals(Object other) {
         return this == other;
     }
 
     @Override
     public int hashCode() {
         return value;
     }
 
     @Override
     public String toString() {
         return isFixed() ? "Fixed" + required() : "Opt";
     }
 
     // Some helper functions:
 
     public static int checkArgumentCount(Ruby runtime, IRubyObject[] args, int min, int max) {
         return checkArgumentCount(runtime, args.length, min, max);
     }
 
+    public static int checkArgumentCount(Ruby runtime, String name, IRubyObject[] args, int min, int max) {
+        return checkArgumentCount(runtime, name, args.length, min, max);
+    }
+
     public static int checkArgumentCount(Ruby runtime, int length, int min, int max) {
         raiseArgumentError(runtime, length, min, max);
 
         return length;
     }
+
+    public static int checkArgumentCount(Ruby runtime, String name, int length, int min, int max) {
+        raiseArgumentError(runtime, name, length, min, max);
+
+        return length;
+    }
     
     // FIXME: JRuby 2/next should change this name since it only sometimes raises an error    
     public static void raiseArgumentError(Ruby runtime, IRubyObject[] args, int min, int max) {
         raiseArgumentError(runtime, args.length, min, max);
     }
 
     // FIXME: JRuby 2/next should change this name since it only sometimes raises an error
     public static void raiseArgumentError(Ruby runtime, int length, int min, int max) {
         if (length < min) throw runtime.newArgumentError(length, min);
         if (max > -1 && length > max) throw runtime.newArgumentError(length, max);
     }
 
+    // FIXME: JRuby 2/next should change this name since it only sometimes raises an error
+    public static void raiseArgumentError(Ruby runtime, String name, int length, int min, int max) {
+        if (length < min) throw runtime.newArgumentError(name, length, min);
+        if (max > -1 && length > max) throw runtime.newArgumentError(name, length, max);
+    }
+
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
     public static IRubyObject[] scanArgs(Ruby runtime, IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = checkArgumentCount(runtime, args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = runtime.getNil();
         }
         return narr;
     }
 }
