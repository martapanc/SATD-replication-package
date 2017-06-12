diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index a88b825536..ed09f42ad6 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2138,2000 +2138,2004 @@ public final class Ruby {
     
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
 
             context.setFile(scriptName);
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
         } finally {
             context.setFile(file);
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
         String file = context.getFile();
 
         try {
             secure(4); /* should alter global state */
 
             context.setFile(extName);
             context.preExtensionLoad(self);
 
             extension.basicLoad(this);
         } catch (IOException ioe) {
             throw newIOErrorFromException(ioe);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
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
         return newRaiseException(getErrno().getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().getClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), "Operation now in progress");
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
         return newErrnoException(getErrno().getClass("EAGAIN"), message);
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
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().getClass("EISCONN"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().getClass("ENOTDIR"), message);
     }
 
+    public RaiseException newErrnoENOTEMPTYError(String message) {
+        return newRaiseException(getErrno().getClass("ENOTEMPTY"), message);
+    }
+
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
     private RaiseException newErrnoException(RubyClass exceptionClass, String message) {
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
 
     // rb_exec_recursive
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, false);
     }
 
     // rb_exec_recursive_outer
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, true);
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
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getBacktraceCount() {
         return backtraceCount.get();
     }
     
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementCallerCount() {
         callerCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getCallerCount() {
         return callerCount.get();
     }
     
     /**
      * Whether the Fixnum class has been reopened and modified
      */
     public boolean isFixnumReopened() {
         return fixnumReopened;
     }
     
     /**
      * Set whether the Fixnum class has been reopened and modified
      */
     public void setFixnumReopened(boolean fixnumReopened) {
         this.fixnumReopened = fixnumReopened;
     }
     
     /**
      * Whether the Float class has been reopened and modified
      */
     public boolean isFloatReopened() {
         return floatReopened;
     }
     
     /**
      * Set whether the Float class has been reopened and modified
      */
     public void setFloatReopened(boolean floatReopened) {
         this.floatReopened = floatReopened;
     }
     
     public boolean isBooting() {
         return booting;
     }
     
     public CoverageData getCoverageData() {
         return coverageData;
     }
     
     public Random getRandom() {
         return random;
     }
     
     public StaticScopeFactory getStaticScopeFactory() {
         return staticScopeFactory;
     }
 
     private final Invalidator constantInvalidator;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private final List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private long globalState = 1;
     
     private int safeLevel = -1;
 
     // Default objects
     private IRubyObject topSelf;
     private IRubyObject rootFiber;
     private RubyNil nilObject;
     private IRubyObject[] singleNilArray;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[2 * RubyFixnum.CACHE_OFFSET];
 
     private boolean verbose, warningsEnabled, debug;
     private IRubyObject verboseValue;
     
     private RubyThreadGroup defaultThreadGroup;
 
     /**
      * All the core classes we keep hard references to. These are here largely
      * so that if someone redefines String or Array we won't start blowing up
      * creating strings and arrays internally. They also provide much faster
      * access than going through normal hash lookup on the Object class.
      */
     private RubyClass
            basicObjectClass, objectClass, moduleClass, classClass, nilClass, trueClass,
             falseClass, numericClass, floatClass, integerClass, fixnumClass,
             complexClass, rationalClass, enumeratorClass, yielderClass,
             arrayClass, hashClass, rangeClass, stringClass, encodingClass, converterClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError, stopIteration,
             syntaxError, standardError, loadError, notImplementedError, securityError, noMemoryError,
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError, mathDomainError,
             encodingError, encodingCompatibilityError, converterNotFoundError, undefinedConversionError,
             invalidByteSequenceError, fiberError, randomClass;
 
     /**
      * All the core modules we keep direct references to, for quick access and
      * to ensure they remain available.
      */
     private RubyModule
             kernelModule, comparableModule, enumerableModule, mathModule,
             marshalModule, etcModule, fileTestModule, gcModule,
             objectSpaceModule, processModule, procUIDModule, procGIDModule,
             procSysModule, precisionModule, errnoModule;
 
     private DynamicMethod privateMethodMissing, protectedMethodMissing, variableMethodMissing,
             superMethodMissing, normalMethodMissing, defaultMethodMissing;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     // The "current line" global variable
     private int currentLine = 0;
 
     private IRubyObject argsFile;
 
     private long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
 
     private final InputStream in;
     private final PrintStream out;
     private final PrintStream err;
 
     // Java support
     private JavaSupport javaSupport;
     private JRubyClassLoader jrubyClassLoader;
     
     // Management/monitoring
     private BeanManager beanManager;
 
     // Parser stats
     private ParserStats parserStats;
     
     // Compilation
     private final JITCompiler jitCompiler;
 
     // Note: this field and the following static initializer
     // must be located be in this order!
     private volatile static boolean securityRestricted = false;
     static {
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflected.handles")) {
             // can't read non-standard properties
             securityRestricted = true;
         } else {
             SecurityManager sm = System.getSecurityManager();
             if (sm != null) {
                 try {
                     sm.checkCreateClassLoader();
                 } catch (SecurityException se) {
                     // can't create custom classloaders
                     securityRestricted = true;
                 }
             }
         }
     }
 
diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index 7052a9bb73..ffa244ddb6 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,871 +1,912 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipEntry;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import jnr.posix.util.Platform;
 
+import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Dir;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ByteList;
 import static org.jruby.CompatVersion.*;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 @JRubyClass(name = "Dir", include = "Enumerable")
 public class RubyDir extends RubyObject {
     private RubyString path;       // What we passed to the constructor for method 'path'
     protected JRubyFile dir;
     private long lastModified = Long.MIN_VALUE;
     private String[] snapshot;     // snapshot of contents of directory
     private int pos;               // current position in directory
     private boolean isOpen = true;
 
     public RubyDir(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     private static final ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyDir(runtime, klass);
         }
     };
 
     public static RubyClass createDirClass(Ruby runtime) {
         RubyClass dirClass = runtime.defineClass("Dir", runtime.getObject(), DIR_ALLOCATOR);
         runtime.setDir(dirClass);
 
         dirClass.index = ClassIndex.DIR;
         dirClass.setReifiedClass(RubyDir.class);
 
         dirClass.includeModule(runtime.getEnumerable());
         dirClass.defineAnnotatedMethods(RubyDir.class);
 
         return dirClass;
     }
 
     private final void checkDir() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4)throw getRuntime().newSecurityError("Insecure: operation on untainted Dir");
 
         testFrozen("Dir");
         update();
         
         if (!isOpen) throw getRuntime().newIOError("closed directory");
     }
 
     private void update() {
         if (snapshot == null || dir.exists() && dir.lastModified() > lastModified) {
             lastModified = dir.lastModified();
             List<String> snapshotList = new ArrayList<String>();
             snapshotList.add(".");
             snapshotList.add("..");
             snapshotList.addAll(getContents(dir));
             snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
         }
     }
     
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject arg) {
         RubyString newPath = arg.convertToString();
         path = newPath;
         pos = 0;
 
         getRuntime().checkSafeString(newPath);
 
         String adjustedPath = RubyFile.adjustRootPathOnWindows(getRuntime(), newPath.toString(), null);
         checkDirIsTwoSlashesOnWindows(getRuntime(), adjustedPath);
 
         dir = JRubyFile.create(getRuntime().getCurrentDirectory(), adjustedPath);
         List<String> snapshotList = RubyDir.getEntries(getRuntime(), adjustedPath);
         snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", compat = RUBY1_9)
     public IRubyObject initialize19(IRubyObject arg) {
         if (arg.respondsTo("to_path")) {
             arg = arg.callMethod(getRuntime().getCurrentContext(), "to_path");
         }
         return initialize(arg);
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
     
     private static List<ByteList> dirGlobs(String cwd, IRubyObject[] args, int flags) {
         List<ByteList> dirs = new ArrayList<ByteList>();
 
         for (int i = 0; i < args.length; i++) {
             ByteList globPattern = args[i].convertToString().getByteList();
             dirs.addAll(Dir.push_glob(cwd, globPattern, flags));
         }
 
         return dirs;
     }
 
     private static IRubyObject asRubyStringList(Ruby runtime, List<ByteList> dirs) {
         List<RubyString> allFiles = new ArrayList<RubyString>();
 
         for (ByteList dir : dirs) {
             allFiles.add(RubyString.newString(runtime, dir));
         }
 
         IRubyObject[] tempFileList = new IRubyObject[allFiles.size()];
         allFiles.toArray(tempFileList);
 
         return runtime.newArrayNoCopy(tempFileList);
     }
 
     private static String getCWD(Ruby runtime) {
         try {
             return new org.jruby.util.NormalizedFile(runtime.getCurrentDirectory()).getCanonicalPath();
         } catch (Exception e) {
             return runtime.getCurrentDirectory();
         }
     }
 
     @JRubyMethod(name = "[]", required = 1, rest = true, meta = true)
     public static IRubyObject aref(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         List<ByteList> dirs;
         if (args.length == 1) {
             Pattern pattern = Pattern.compile("file:(.*)!/(.*)");
             String glob = args[0].toString();
             Matcher matcher = pattern.matcher(glob);
             if (matcher.find()) {
                 String jarFileName = matcher.group(1);
                 String jarUri = "file:" + jarFileName + "!/";
                 String fileGlobString = matcher.group(2);
                 String filePatternString = convertGlobToRegEx(fileGlobString);
                 Pattern filePattern = Pattern.compile(filePatternString);
                 try {
                     JarFile jarFile = new JarFile(jarFileName);
                     List<RubyString> allFiles = new ArrayList<RubyString>();
                     Enumeration<JarEntry> entries = jarFile.entries();
                     while (entries.hasMoreElements()) {
                         String entry = entries.nextElement().getName();
                         String chomped_entry = entry.endsWith("/") ? entry.substring(0, entry.length() - 1) : entry;
                         if (filePattern.matcher(chomped_entry).find()) {
                             allFiles.add(RubyString.newString(runtime, jarUri + chomped_entry.toString()));
                         }
                     }
                     IRubyObject[] tempFileList = new IRubyObject[allFiles.size()];
                     allFiles.toArray(tempFileList);
                     return runtime.newArrayNoCopy(tempFileList);
                 } catch (IOException e) {
                     return runtime.newArrayNoCopy(new IRubyObject[0]);
                 }
             }
             
             ByteList globPattern = null;
             if (runtime.is1_9() && args[0].respondsTo("to_path")) {
                 globPattern = args[0].callMethod(context, "to_path").convertToString().getByteList();
             } else {
                 globPattern = args[0].convertToString().getByteList();
             }
             dirs = Dir.push_glob(getCWD(runtime), globPattern, 0);
         } else {
             dirs = dirGlobs(getCWD(runtime), args, 0);
         }
 
         return asRubyStringList(runtime, dirs);
     }
 
     private static String convertGlobToRegEx(String line) {
         line = line.trim();
         StringBuilder sb = new StringBuilder(line.length());
         sb.append("^");
         boolean escaping = false;
         int inCurlies = 0;
         for (char currentChar : line.toCharArray()) {
             switch (currentChar) {
             case '*':
                 if (escaping)
                     sb.append("\\*");
                 else
                     sb.append("[^/]*");
                 escaping = false;
                 break;
             case '?':
                 if (escaping)
                     sb.append("\\?");
                 else
                     sb.append('.');
                 escaping = false;
                 break;
             case '.':
             case '(':
             case ')':
             case '+':
             case '|':
             case '^':
             case '$':
             case '@':
             case '%':
                 sb.append('\\');
                 sb.append(currentChar);
                 escaping = false;
                 break;
             case '\\':
                 if (escaping) {
                     sb.append("\\\\");
                     escaping = false;
                 } else
                     escaping = true;
                 break;
             case '{':
                 if (escaping) {
                     sb.append("\\{");
                 } else {
                     sb.append('(');
                     inCurlies++;
                 }
                 escaping = false;
                 break;
             case '}':
                 if (inCurlies > 0 && !escaping) {
                     sb.append(')');
                     inCurlies--;
                 } else if (escaping)
                     sb.append("\\}");
                 else
                     sb.append("}");
                 escaping = false;
                 break;
             case ',':
                 if (inCurlies > 0 && !escaping) {
                     sb.append('|');
                 } else if (escaping)
                     sb.append("\\,");
                 else
                     sb.append(",");
                 break;
             default:
                 escaping = false;
                 sb.append(currentChar);
             }
         }
         sb.append("$");
         return sb.toString().replace("[^/]*[^/]*/", ".*").replace("[^/]*[^/]*", ".*");
     }
     
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.  
      */
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject glob(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int flags = args.length == 2 ? RubyNumeric.num2int(args[1]) : 0;
 
         List<ByteList> dirs;
         IRubyObject tmp = args[0].checkArrayType();
         if (tmp.isNil()) {
             ByteList globPattern = null;
             if (runtime.is1_9() && args[0].respondsTo("to_path")) {
                 globPattern = args[0].callMethod(context, "to_path").convertToString().getByteList();
             } else {
                 globPattern = args[0].convertToString().getByteList();
             }
             dirs = Dir.push_glob(runtime.getCurrentDirectory(), globPattern, flags);
         } else {
             dirs = dirGlobs(getCWD(runtime), ((RubyArray) tmp).toJavaArray(), flags);
         }
 
         if (block.isGiven()) {
             for (int i = 0; i < dirs.size(); i++) {
                 block.yield(context, RubyString.newString(runtime, dirs.get(i)));
             }
 
             return runtime.getNil();
         }
 
         return asRubyStringList(runtime, dirs);
     }
 
     /**
      * @return all entries for this Dir
      */
     @JRubyMethod(name = "entries")
     public RubyArray entries() {
         return getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(getRuntime(), snapshot));
     }
 
     /**
      * Returns an array containing all of the filenames in the given directory.
      */
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_8)
     public static RubyArray entries(IRubyObject recv, IRubyObject path) {
         return entriesCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_9)
     public static RubyArray entries19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return entriesCommon(context.getRuntime(), getPath19(context, arg));
     }
 
     @JRubyMethod(name = "entries", meta = true, compat = RUBY1_9)
     public static RubyArray entries19(ThreadContext context, IRubyObject recv, IRubyObject arg, IRubyObject opts) {
         // FIXME: do something with opts
         return entriesCommon(context.getRuntime(), getPath19(context, arg));
     }
 
     private static RubyArray entriesCommon(Ruby runtime, String path) {
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path, null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
 
         Object[] files = getEntries(runtime, adjustedPath).toArray();
         return runtime.newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(runtime, files));
     }
 
     private static List<String> getEntries(Ruby runtime, String path) {
         if (!RubyFileTest.directory_p(runtime, RubyString.newString(runtime, path)).isTrue()) {
             throw runtime.newErrnoENOENTError("No such directory");
         }
 
         if (path.startsWith("file:")) return entriesIntoAJarFile(runtime, path);
 
         return entriesIntoADirectory(runtime, path);
     }
 
     private static List<String> entriesIntoADirectory(Ruby runtime, String path) {
         final JRubyFile directory = JRubyFile.create(runtime.getCurrentDirectory(), path);
 
         List<String> fileList = getContents(directory);
         fileList.add(0, ".");
         fileList.add(1, "..");
         return fileList;
     }
 
     private static List<String> entriesIntoAJarFile(Ruby runtime, String path) {
         String file = path.substring(5);
         int bang = file.indexOf('!');
         if (bang == -1) {
           return entriesIntoADirectory(runtime, path.substring(5));
         }
         if (bang == file.length() - 1) {
             return new ArrayList<String>();
         }
         String jar = file.substring(0, bang);
         String after = file.substring(bang + 2);
         JarFile jf;
         try {
             jf = new JarFile(jar);
         } catch (IOException e) {
             throw new RuntimeException("Valid JAR file expected", e);
         }
         
         List<String> fileList = new ArrayList<String>();
         Enumeration<? extends ZipEntry> entries = jf.entries();
         while (entries.hasMoreElements()) {
             String zipEntry = entries.nextElement().getName();
             if (zipEntry.matches(after + "/" + "[^/]+")) {
                 fileList.add(zipEntry.substring(after.length() + 1));
             }
         }
 
         return fileList;
     }
 
     // MRI behavior: just plain '//' or '\\\\' are considered illegal on Windows.
     private static void checkDirIsTwoSlashesOnWindows(Ruby runtime, String path) {
         if (Platform.IS_WINDOWS && ("//".equals(path) || "\\\\".equals(path))) {
             throw runtime.newErrnoEINVALError("Invalid argument - " + path);
         }
     }
 
     /** Changes the current directory to <code>path</code> */
     @JRubyMethod(optional = 1, meta = true)
     public static IRubyObject chdir(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyString path = args.length == 1 ?
             RubyFile.get_path(context, args[0]) : getHomeDirectoryPath(context);
         String adjustedPath = RubyFile.adjustRootPathOnWindows(runtime, path.getUnicodeValue(), null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
         JRubyFile dir = getDir(runtime, adjustedPath, true);
         String realPath = null;
         String oldCwd = runtime.getCurrentDirectory();
 
         // We get canonical path to try and flatten the path out.
         // a dir '/subdir/..' should return as '/'
         // cnutter: Do we want to flatten path out?
         try {
             realPath = dir.getCanonicalPath();
         } catch (IOException e) {
             realPath = dir.getAbsolutePath();
         }
 
         IRubyObject result = null;
         if (block.isGiven()) {
             // FIXME: Don't allow multiple threads to do this at once
             runtime.setCurrentDirectory(realPath);
             try {
                 result = block.yield(context, path);
             } finally {
                 dir = getDir(runtime, oldCwd, true);
                 runtime.setCurrentDirectory(oldCwd);
             }
         } else {
             runtime.setCurrentDirectory(realPath);
             result = runtime.newFixnum(0);
         }
 
         return result;
     }
 
     /**
      * Changes the root directory (only allowed by super user).  Not available
      * on all platforms.
      */
     @JRubyMethod(name = "chroot", required = 1, meta = true)
     public static IRubyObject chroot(IRubyObject recv, IRubyObject path) {
         throw recv.getRuntime().newNotImplementedError("chroot not implemented: chroot is non-portable and is not supported.");
     }
 
     /**
      * Deletes the directory specified by <code>path</code>.  The directory must
      * be empty.
      */
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject rmdir(IRubyObject recv, IRubyObject path) {
         return rmdirCommon(recv.getRuntime(), path.convertToString().getUnicodeValue());
     }
 
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject rmdir19(ThreadContext context, IRubyObject recv, IRubyObject path) {
         return rmdirCommon(context.getRuntime(), getPath19(context, path));
     }
 
     private static IRubyObject rmdirCommon(Ruby runtime, String path) {
-        JRubyFile directory = getDir(runtime, path, true);
-
+        JRubyFile directory = getDirForRmdir(runtime, path);
+        
+        // at this point, only thing preventing delete should be non-emptiness
         if (!directory.delete()) {
-            throw runtime.newSystemCallError("No such directory");
+            throw runtime.newErrnoENOTEMPTYError(path);
         }
 
         return runtime.newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     @JRubyMethod(meta = true, compat = RUBY1_8)
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject _path, Block block) {
         RubyString pathString = _path.convertToString();
 
         return foreachCommon(context, recv, context.getRuntime(), pathString, block);
     }
 
     @JRubyMethod(name = "foreach", meta = true, compat = RUBY1_9)
     public static IRubyObject foreach19(ThreadContext context, IRubyObject recv, IRubyObject arg, Block block) {
         RubyString pathString = arg instanceof RubyString ? (RubyString) arg : arg.callMethod(context, "to_path").convertToString();
 
         return foreachCommon(context, recv, context.getRuntime(), pathString, block);
     }
 
     private static IRubyObject foreachCommon(ThreadContext context, IRubyObject recv, Ruby runtime, RubyString _path, Block block) {
         if (block.isGiven()) {
             runtime.checkSafeString(_path);
 
             RubyClass dirClass = runtime.getDir();
             RubyDir dir = (RubyDir) dirClass.newInstance(context, new IRubyObject[]{_path}, block);
 
             dir.each(context, block);
             return runtime.getNil();
         }
 
         return enumeratorize(runtime, recv, "foreach", _path);
     }
 
     /** Returns the current directory. */
     @JRubyMethod(name = {"getwd", "pwd"}, meta = true)
     public static RubyString getwd(IRubyObject recv) {
         Ruby ruby = recv.getRuntime();
 
         RubyString pwd = RubyString.newUnicodeString(ruby, getCWD(ruby));
         pwd.setTaint(true);
         return pwd;
     }
 
     /**
      * Returns the home directory of the current user or the named user if given.
      */
     @JRubyMethod(name = "home", optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject home(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length > 0) return getHomeDirectoryPath(context, args[0].toString());
 
         return getHomeDirectoryPath(context);
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = RUBY1_8)
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         RubyString stringArg = args[0].convertToString();
         runtime.checkSafeString(stringArg);
 
         return mkdirCommon(runtime, stringArg.getUnicodeValue(), args);
     }
 
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true, compat = RUBY1_9)
     public static IRubyObject mkdir19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return mkdirCommon(context.getRuntime(), getPath19(context, args[0]), args);
     }
 
     private static IRubyObject mkdirCommon(Ruby runtime, String path, IRubyObject[] args) {
         File newDir = getDir(runtime, path, false);
 
         if (File.separatorChar == '\\') newDir = new File(newDir.getPath());
 
         int mode = args.length == 2 ? ((int) args[1].convertToInteger().getLongValue()) : 0777;
 
         if (runtime.getPosix().mkdir(newDir.getAbsolutePath(), mode) < 0) {
             // FIXME: This is a system error based on errno
             throw runtime.newSystemCallError("mkdir failed");
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /**
      * Returns a new directory object for <code>path</code>.  If a block is
      * provided, a new directory object is passed to the block, which closes the
      * directory object before terminating.
      */
     @JRubyMethod(meta = true, compat = RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyDir directory = (RubyDir) context.getRuntime().getDir().newInstance(context,
                 new IRubyObject[]{path}, Block.NULL_BLOCK);
 
         if (!block.isGiven())return directory;
 
         try {
             return block.yield(context, directory);
         } finally {
             directory.close();
         }
     }
 
     @JRubyMethod(name = "open", meta = true, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyString pathString = path instanceof RubyString ? (RubyString) path : path.callMethod(context, "to_path").convertToString();
         return open(context, recv, pathString, block);
     }
 
 // ----- Ruby Instance Methods -------------------------------------------------
     /**
      * Closes the directory stream.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         // Make sure any read()s after close fail.
         checkDir();
 
         isOpen = false;
 
         return getRuntime().getNil();
     }
 
     /**
      * Executes the block once for each entry in the directory.
      */
     public IRubyObject each(ThreadContext context, Block block) {
         checkDir();
 
         String[] contents = snapshot;
         for (pos = 0; pos < contents.length; pos++) {
             block.yield(context, getRuntime().newString(contents[pos]));
         }
 
         return this;
     }
 
     @JRubyMethod(name = "each")
     public IRubyObject each19(ThreadContext context, Block block) {
         return block.isGiven() ? each(context, block) : enumeratorize(context.getRuntime(), this, "each");
     }
 
     @Override
     @JRubyMethod
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":").append(path.asJavaString()).append(">");
 
         return runtime.newString(part.toString());
     }
 
     /**
      * Returns the current position in the directory.
      */
     @JRubyMethod(name = {"tell", "pos"})
     public RubyInteger tell() {
         checkDir();
         return getRuntime().newFixnum(pos);
     }
 
     /**
      * Moves to a position <code>d</code>.  <code>pos</code> must be a value
      * returned by <code>tell</code> or 0.
      */
     
     @JRubyMethod(name = "seek", required = 1)
     public IRubyObject seek(IRubyObject newPos) {
         checkDir();
 
         set_pos(newPos);
         return this;
     }
 
     @JRubyMethod(name = "pos=", required = 1)
     public IRubyObject set_pos(IRubyObject newPos) {
         int pos2 = RubyNumeric.fix2int(newPos);
         if (pos2 >= 0) this.pos = pos2;
         return newPos;
     }
 
     @JRubyMethod(name = "path")
     public IRubyObject path(ThreadContext context) {
         return path.strDup(context.getRuntime());
     }
 
     /** Returns the next entry from this directory. */
     @JRubyMethod(name = "read")
     public IRubyObject read() {
         checkDir();
 
         if (pos >= snapshot.length) return getRuntime().getNil();
 
         RubyString result = getRuntime().newString(snapshot[pos]);
         pos++;
         return result;
     }
 
     /** Moves position in this directory to the first entry. */
     @JRubyMethod(name = "rewind")
     public IRubyObject rewind() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) throw getRuntime().newSecurityError("Insecure: can't close");
         checkDir();
 
         pos = 0;
         return this;
     }
 
     @JRubyMethod(name = {"exists?", "exist?"}, meta = true, compat = RUBY1_9)
     public static IRubyObject exist(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         try {
             return context.getRuntime().newFileStat(getPath19(context, arg), false).directory_p();
         } catch (Exception e) {
             return context.getRuntime().newBoolean(false);
         }
     }
 
 // ----- Helper Methods --------------------------------------------------------
     protected static String getPath19(ThreadContext context, IRubyObject arg) {
         RubyString pathObject = arg instanceof RubyString ? (RubyString) arg : arg.callMethod(context, "to_path").convertToString();
         return pathObject.getUnicodeValue();
     }
 
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
-        String dir = path;
-        String[] pathParts = RubyFile.splitURI(path);
-        if (pathParts != null) {
-            if (pathParts[0].equals("file:") && pathParts[1].length() > 0 && pathParts[1].indexOf("!/") == -1) {
-                dir = pathParts[1];
-            } else {
-                throw runtime.newErrnoENOTDIRError(dir + " is not a directory");
-            }
-        }
+        String dir = dirFromPath(path, runtime);
 
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(), dir);
 
         if (mustExist && !result.exists()) {
-            throw runtime.newErrnoENOENTError("No such file or directory - " + dir);
+            throw runtime.newErrnoENOENTError(dir);
         }
 
         boolean isDirectory = result.isDirectory();
 
         if (mustExist && !isDirectory) {
-            throw runtime.newErrnoENOTDIRError(path + " is not a directory");
+            throw runtime.newErrnoENOTDIRError(path);
         }
 
         if (!mustExist && isDirectory) {
-            throw runtime.newErrnoEEXISTError("File exists - " + dir);
+            throw runtime.newErrnoEEXISTError(dir);
         }
 
         return result;
     }
+    
+    /**
+     * Similar to getDir, but performs different checks to match rmdir behavior.
+     * @param runtime
+     * @param path
+     * @param mustExist
+     * @return 
+     */
+    protected static JRubyFile getDirForRmdir(final Ruby runtime, final String path) {
+        String dir = dirFromPath(path, runtime);
+
+        JRubyFile directory = JRubyFile.create(runtime.getCurrentDirectory(), dir);
+        
+        // Order is important here...File.exists() will return false if the parent
+        // dir can't be read, so we check permissions first
+        
+        // no permission
+        if (directory.getParentFile().exists() &&
+                !directory.getParentFile().canWrite()) {
+            throw runtime.newErrnoEACCESError(path);
+        }
+
+        // does not exist
+        if (!directory.exists()) {
+            throw runtime.newErrnoENOENTError(path);
+        }
+        
+        // is not directory
+        if (!directory.isDirectory()) {
+            throw runtime.newErrnoENOTDIRError(path);
+        }
+
+        return directory;
+    }
+
+    private static String dirFromPath(final String path, final Ruby runtime) throws RaiseException {
+        String dir = path;
+        String[] pathParts = RubyFile.splitURI(path);
+        if (pathParts != null) {
+            if (pathParts[0].equals("file:") && pathParts[1].length() > 0 && pathParts[1].indexOf("!/") == -1) {
+                dir = pathParts[1];
+            } else {
+                throw runtime.newErrnoENOTDIRError(dir);
+            }
+        }
+        return dir;
+    }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Java Strings.
      */
     protected static List<String> getContents(File directory) {
         String[] contents = directory.list();
         List<String> result = new ArrayList<String>();
 
         // If an IO exception occurs (something odd, but possible)
         // A directory may return null.
         if (contents != null) {
             for (int i = 0; i < contents.length; i++) {
                 result.add(contents[i]);
             }
         }
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Ruby Strings.
      */
     protected static List<RubyString> getContents(File directory, Ruby runtime) {
         List<RubyString> result = new ArrayList<RubyString>();
         String[] contents = directory.list();
 
         for (int i = 0; i < contents.length; i++) {
             result.add(runtime.newString(contents[i]));
         }
         return result;
     }
 
     /**
      * Returns the home directory of the specified <code>user</code> on the
      * system. If the home directory of the specified user cannot be found,
      * an <code>ArgumentError it thrown</code>.
      */
     public static IRubyObject getHomeDirectoryPath(ThreadContext context, String user) {
         /*
          * TODO: This version is better than the hackish previous one. Windows
          *       behavior needs to be defined though. I suppose this version
          *       could be improved more too.
          * TODO: /etc/passwd is also inadequate for MacOSX since it does not
          *       use /etc/passwd for regular user accounts
          */
         Ruby runtime = context.getRuntime();
 
         try {
             // try to use POSIX for this first
             return runtime.newString(runtime.getPosix().getpwnam(user).getHome());
         } catch (Exception e) {
             // otherwise fall back on the old way
             String passwd = null;
             try {
                 FileInputStream stream = new FileInputStream("/etc/passwd");
                 int totalBytes = stream.available();
                 byte[] bytes = new byte[totalBytes];
                 stream.read(bytes);
                 stream.close();
                 passwd = new String(bytes);
             } catch (IOException ioe) {
                 return runtime.getNil();
             }
 
             String[] rows = passwd.split("\n");
             int rowCount = rows.length;
             for (int i = 0; i < rowCount; i++) {
                 String[] fields = rows[i].split(":");
                 if (fields[0].equals(user)) {
                     return runtime.newString(fields[5]);
                 }
             }
         }
 
         throw runtime.newArgumentError("user " + user + " doesn't exist");
     }
 
     public static RubyString getHomeDirectoryPath(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyHash systemHash = (RubyHash) runtime.getObject().getConstant("ENV_JAVA");
         RubyHash envHash = (RubyHash) runtime.getObject().getConstant("ENV");
         IRubyObject home = envHash.op_aref(context, runtime.newString("HOME"));
 
         if (home == null || home.isNil()) {
             home = systemHash.op_aref(context, runtime.newString("user.home"));
         }
 
         if (home == null || home.isNil()) {
             home = envHash.op_aref(context, runtime.newString("LOGDIR"));
         }
 
         if (home == null || home.isNil()) {
             throw runtime.newArgumentError("user.home/LOGDIR not set");
         }
 
         return (RubyString) home;
     }
 }
