diff --git a/spec/tags/1.8/ruby/core/process/kill_tags.txt b/spec/tags/1.8/ruby/core/process/kill_tags.txt
index b1c5233d11..7df3bb32a4 100644
--- a/spec/tags/1.8/ruby/core/process/kill_tags.txt
+++ b/spec/tags/1.8/ruby/core/process/kill_tags.txt
@@ -1,9 +1,8 @@
-fails(JRUBY-5265):Process.kill accepts symbols as signal names
 fails:Process.kill tests for the existence of a process without sending a signal
 fails:Process.kill sends the given signal to the specified process
 fails:Process.kill kills process groups if signal is negative
 fails:Process.kill kills process groups if signal starts with a minus sign
 fails:Process.kill kills process groups if signal starts with a minus sign and 'SIG'
 windows(JRUBY-4317):Process.kill raises an ArgumentError for unknown signals
 windows(JRUBY-4317):Process.kill doesn't accept lowercase signal names
 windows(JRUBY-4317):Process.kill doesn't tolerate leading or trailing spaces in signal names
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 2a417481c3..813cea6aa5 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2115,1856 +2115,1865 @@ public final class Ruby {
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
         ParserConfiguration parserConfig =
                 new ParserConfiguration(this, 0, false, true, false, config);
         if (is1_9) parserConfig.setDefaultEncoding(getEncodingService().getLocaleEncoding());
         return parser.parse(file, in, scope, parserConfig);
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
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(config.getTraceType().printBacktrace(excp));
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
             Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
 
             if (script == null) {
                 failForcedCompile(scriptNode);
 
                 runInterpreter(scriptNode);
             } else {
                 runScript(script);
             }
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
 
         if (getJRubyClassLoader() != null) {
             getJRubyClassLoader().tearDown(isDebug());
         }
 
         if (config.isProfilingEntireRun()) {
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
 
+    public RaiseException newErrnoFromInt(int errno) {
+        Errno errnoObj = Errno.valueOf(errno);
+        if (errnoObj == null) {
+            return newSystemCallError("Unknown Error (" + errno + ")");
+        }
+        String message = errnoObj.description();
+        return newErrnoFromInt(errno, message);
+    }
+
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
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
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
             soe.printStackTrace(getInstanceConfig().getError());
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
         return new RaiseException(this, exceptionClass, message, true);
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
 
     public int getConstantGeneration() {
         return constantGeneration;
     }
 
     public synchronized void incrementConstantGeneration() {
         constantGeneration++;
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
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
     public void addProfiledMethod(String name, DynamicMethod method) {
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
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
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
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflection")) {
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
 
     private final Parser parser = new Parser(this);
 
     private LoadService loadService;
 
     private Encoding defaultInternalEncoding, defaultExternalEncoding;
     private EncodingService encodingService;
 
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private final RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private final Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private final AtomicInteger symbolLastId = new AtomicInteger(128);
     private final AtomicInteger moduleLastId = new AtomicInteger(0);
 
     // Weak map of all Modules in the system (and by extension, all Classes
     private final Set<RubyModule> allModules = new WeakHashSet<RubyModule>();
 
     private Object respondToMethod;
 
     private final Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
     /**
      * A list of "external" finalizers (the ones, registered via ObjectSpace),
      * weakly referenced, to be executed on tearDown.
      */
     private Map<Finalizable, Object> finalizers;
     
     /**
      * A list of JRuby-internal finalizers,  weakly referenced,
      * to be executed on tearDown.
      */
     private Map<Finalizable, Object> internalFinalizers;
 
     // mutex that controls modifications of user-defined finalizers
     private final Object finalizersMutex = new Object();
 
     // mutex that controls modifications of internal finalizers
     private final Object internalFinalizersMutex = new Object();
     
     // A thread pool to use for executing this runtime's Ruby threads
     private ExecutorService executor;
 
     // A global object lock for class hierarchy mutations
     private final Object hierarchyLock = new Object();
 
     // An atomic long for generating DynamicMethod serial numbers
     private final AtomicLong dynamicMethodSerial = new AtomicLong(1);
 
     // An atomic int for generating class generation numbers
     private final AtomicInteger moduleGeneration = new AtomicInteger(1);
 
     // A list of Java class+method names to include in backtraces
     private final Map<String, String> boundMethods = new HashMap();
 
     // A soft pool of selectors for blocking IO operations
     private final SelectorPool selectorPool = new SelectorPool();
 
     // A global cache for Java-to-Ruby calls
     private final RuntimeCache runtimeCache;
 
     // The maximum number of methods we will track for profiling purposes
     private static final int MAX_PROFILE_METHODS = 100000;
 
     // The list of method names associated with method serial numbers
     public String[] profiledNames = new String[0];
 
     // The method objects for serial numbers
     public DynamicMethod[] profiledMethods = new DynamicMethod[0];
 }
diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index 4d16468549..7b0dc95d29 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,959 +1,976 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import com.kenai.constantine.platform.Errno;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ShellLauncher;
 
 import static org.jruby.ext.JRubyPOSIXHelper.checkErrno;
 
 
 /**
  */
 
 @JRubyModule(name="Process")
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         runtime.setProcess(process);
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyClass process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProcStatus(process_status);
         
         RubyModule process_uid = process.defineModuleUnder("UID");
         runtime.setProcUID(process_uid);
         
         RubyModule process_gid = process.defineModuleUnder("GID");
         runtime.setProcGID(process_gid);
         
         RubyModule process_sys = process.defineModuleUnder("Sys");
         runtime.setProcSys(process_sys);
         
         process.defineAnnotatedMethods(RubyProcess.class);
         process_status.defineAnnotatedMethods(RubyStatus.class);
         process_uid.defineAnnotatedMethods(UserID.class);
         process_gid.defineAnnotatedMethods(GroupID.class);
         process_sys.defineAnnotatedMethods(Sys.class);
 
         runtime.loadConstantSet(process, com.kenai.constantine.platform.PRIO.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIM.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIMIT.class);
         
         process.defineConstant("WNOHANG", runtime.newFixnum(1));
         process.defineConstant("WUNTRACED", runtime.newFixnum(2));
         
         return process;
     }
     
     @JRubyClass(name="Process::Status")
     public static class RubyStatus extends RubyObject {
         private long status = 0L;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
             super(runtime, metaClass);
             this.status = status;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status) {
             return new RubyStatus(runtime, runtime.getProcStatus(), status);
         }
         
         // Bunch of methods still not implemented
         @JRubyMethod(name = {"to_int", "pid", "stopped?", "stopsig", "signaled?", "termsig?", "exited?", "coredump?"}, frame = true)
         public IRubyObject not_implemented() {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod(name = {"&"}, frame = true)
         public IRubyObject not_implemented1(IRubyObject arg) {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod
         public IRubyObject exitstatus() {
             return getRuntime().newFixnum(status);
         }
         
         @Deprecated
         public IRubyObject op_rshift(IRubyObject other) {
             return op_rshift(getRuntime(), other);
         }
         @JRubyMethod(name = ">>")
         public IRubyObject op_rshift(ThreadContext context, IRubyObject other) {
             return op_rshift(context.getRuntime(), other);
         }
         public IRubyObject op_rshift(Ruby runtime, IRubyObject other) {
             long shiftValue = other.convertToInteger().getLongValue();
             return runtime.newFixnum(status >> shiftValue);
         }
 
         @Override
         @JRubyMethod(name = "==")
         public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
             return other.callMethod(context, "==", this.to_i(context.getRuntime()));
         }
         
         @Deprecated
         public IRubyObject to_i() {
             return to_i(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_i(ThreadContext context) {
             return to_i(context.getRuntime());
         }
         public IRubyObject to_i(Ruby runtime) {
             return runtime.newFixnum(shiftedValue());
         }
         
         @Override
         public IRubyObject to_s() {
             return to_s(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_s(ThreadContext context) {
             return to_s(context.getRuntime());
         }
         public IRubyObject to_s(Ruby runtime) {
             return runtime.newString(String.valueOf(shiftedValue()));
         }
         
         @Override
         public IRubyObject inspect() {
             return inspect(getRuntime());
         }
         @JRubyMethod
         public IRubyObject inspect(ThreadContext context) {
             return inspect(context.getRuntime());
         }
         public IRubyObject inspect(Ruby runtime) {
             return runtime.newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
         }
         
         @JRubyMethod(name = "success?")
         public IRubyObject success_p(ThreadContext context) {
             return context.getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
         
         private long shiftedValue() {
             return status << 8;
         }
     }
 
     @JRubyModule(name="Process::UID")
     public static class UserID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::change_privilege not implemented yet");
         }
         
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return euid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return uid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
-            int uid = runtime.getPosix().getuid();
-            int euid = runtime.getPosix().geteuid();
+            int uid = checkErrno(runtime, runtime.getPosix().getuid());
+            int euid = checkErrno(runtime, runtime.getPosix().geteuid());
             
             if (block.isGiven()) {
                 try {
-                    runtime.getPosix().seteuid(uid);
-                    runtime.getPosix().setuid(euid);
+                    checkErrno(runtime, runtime.getPosix().seteuid(uid));
+                    checkErrno(runtime, runtime.getPosix().setuid(euid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
-                    runtime.getPosix().seteuid(euid);
-                    runtime.getPosix().setuid(uid);
+                    checkErrno(runtime, runtime.getPosix().seteuid(euid));
+                    checkErrno(runtime, runtime.getPosix().setuid(uid));
                 }
             } else {
-                runtime.getPosix().seteuid(uid);
-                runtime.getPosix().setuid(euid);
+                checkErrno(runtime, runtime.getPosix().seteuid(uid));
+                checkErrno(runtime, runtime.getPosix().setuid(euid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::GID")
     public static class GroupID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::change_privilege not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return eid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return eid(context.getRuntime());
         }
         public static IRubyObject eid(Ruby runtime) {
             return egid(runtime);
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return RubyProcess.egid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return gid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
-            int gid = runtime.getPosix().getgid();
-            int egid = runtime.getPosix().getegid();
+            int gid = checkErrno(runtime, runtime.getPosix().getgid());
+            int egid = checkErrno(runtime, runtime.getPosix().getegid());
             
             if (block.isGiven()) {
                 try {
-                    runtime.getPosix().setegid(gid);
-                    runtime.getPosix().setgid(egid);
+                    checkErrno(runtime, runtime.getPosix().setegid(gid));
+                    checkErrno(runtime, runtime.getPosix().setgid(egid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
-                    runtime.getPosix().setegid(egid);
-                    runtime.getPosix().setgid(gid);
+                    checkErrno(runtime, runtime.getPosix().setegid(egid));
+                    checkErrno(runtime, runtime.getPosix().setgid(gid));
                 }
             } else {
-                runtime.getPosix().setegid(gid);
-                runtime.getPosix().setgid(egid);
+                checkErrno(runtime, runtime.getPosix().setegid(gid));
+                checkErrno(runtime, runtime.getPosix().setgid(egid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::Sys")
     public static class Sys {
         @Deprecated
         public static IRubyObject getegid(IRubyObject self) {
             return egid(self.getRuntime());
         }
         @JRubyMethod(name = "getegid", module = true, visibility = PRIVATE)
         public static IRubyObject getegid(ThreadContext context, IRubyObject self) {
             return egid(context.getRuntime());
         }
         
         @Deprecated
         public static IRubyObject geteuid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "geteuid", module = true, visibility = PRIVATE)
         public static IRubyObject geteuid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getgid(IRubyObject self) {
             return gid(self.getRuntime());
         }
         @JRubyMethod(name = "getgid", module = true, visibility = PRIVATE)
         public static IRubyObject getgid(ThreadContext context, IRubyObject self) {
             return gid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getuid(IRubyObject self) {
             return uid(self.getRuntime());
         }
         @JRubyMethod(name = "getuid", module = true, visibility = PRIVATE)
         public static IRubyObject getuid(ThreadContext context, IRubyObject self) {
             return uid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject setegid(IRubyObject recv, IRubyObject arg) {
             return egid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setegid", module = true, visibility = PRIVATE)
         public static IRubyObject setegid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return egid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject seteuid(IRubyObject recv, IRubyObject arg) {
             return euid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "seteuid", module = true, visibility = PRIVATE)
         public static IRubyObject seteuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return euid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setgid(IRubyObject recv, IRubyObject arg) {
             return gid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setgid", module = true, visibility = PRIVATE)
         public static IRubyObject setgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return gid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setuid(IRubyObject recv, IRubyObject arg) {
             return uid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setuid", module = true, visibility = PRIVATE)
         public static IRubyObject setuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return uid_set(context.getRuntime(), arg);
         }
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.abort(context, recv, args);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit_bang(recv, args);
     }
 
     @JRubyMethod(name = "groups", module = true, visibility = PRIVATE)
     public static IRubyObject groups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "setrlimit", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject setrlimit(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#setrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpgrp(IRubyObject recv) {
         return getpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "getpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject getpgrp(ThreadContext context, IRubyObject recv) {
         return getpgrp(context.getRuntime());
     }
     public static IRubyObject getpgrp(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpgrp());
     }
 
     @JRubyMethod(name = "groups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject groups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject waitpid(IRubyObject recv, IRubyObject[] args) {
         return waitpid(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid(context.getRuntime(), args);
     }
     public static IRubyObject waitpid(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
-        pid = runtime.getPosix().waitpid(pid, status, flags);
-        
-        if (pid == -1) {
-            throw runtime.newErrnoECHILDError();
-        }
+        pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
+    private interface NonNativeErrno {
+        public int handle(Ruby runtime, int result);
+    }
+
+    private static final NonNativeErrno ECHILD = new NonNativeErrno() {
+        public int handle(Ruby runtime, int result) {
+            throw runtime.newErrnoECHILDError();
+        }
+    };
+
     @Deprecated
     public static IRubyObject wait(IRubyObject recv, IRubyObject[] args) {
         return wait(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return wait(context.getRuntime(), args);
     }
     public static IRubyObject wait(Ruby runtime, IRubyObject[] args) {
         
         if (args.length > 0) {
             return waitpid(runtime, args);
         }
         
         int[] status = new int[1];
-        int pid = runtime.getPosix().wait(status);
-        
-        if (pid == -1) {
-            throw runtime.newErrnoECHILDError();
-        }
+        int pid = checkErrno(runtime, runtime.getPosix().wait(status), ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
 
     @Deprecated
     public static IRubyObject waitall(IRubyObject recv) {
         return waitall(recv.getRuntime());
     }
     @JRubyMethod(name = "waitall", module = true, visibility = PRIVATE)
     public static IRubyObject waitall(ThreadContext context, IRubyObject recv) {
         return waitall(context.getRuntime());
     }
     public static IRubyObject waitall(Ruby runtime) {
         POSIX posix = runtime.getPosix();
         RubyArray results = runtime.newArray();
         
         int[] status = new int[1];
         int result = posix.wait(status);
         while (result != -1) {
             results.append(runtime.newArray(runtime.newFixnum(result), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0])));
             result = posix.wait(status);
         }
         
         return results;
     }
 
     @Deprecated
     public static IRubyObject setsid(IRubyObject recv) {
         return setsid(recv.getRuntime());
     }
     @JRubyMethod(name = "setsid", module = true, visibility = PRIVATE)
     public static IRubyObject setsid(ThreadContext context, IRubyObject recv) {
         return setsid(context.getRuntime());
     }
     public static IRubyObject setsid(Ruby runtime) {
-        return runtime.newFixnum(runtime.getPosix().setsid());
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setsid()));
     }
 
     @Deprecated
     public static IRubyObject setpgrp(IRubyObject recv) {
         return setpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "setpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject setpgrp(ThreadContext context, IRubyObject recv) {
         return setpgrp(context.getRuntime());
     }
     public static IRubyObject setpgrp(Ruby runtime) {
-        return runtime.newFixnum(runtime.getPosix().setpgid(0, 0));
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(0, 0)));
     }
 
     @Deprecated
     public static IRubyObject egid_set(IRubyObject recv, IRubyObject arg) {
         return egid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "egid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject egid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return egid_set(context.getRuntime(), arg);
     }
     public static IRubyObject egid_set(Ruby runtime, IRubyObject arg) {
-        runtime.getPosix().setegid((int)arg.convertToInteger().getLongValue());
+        checkErrno(runtime, runtime.getPosix().setegid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject euid(IRubyObject recv) {
         return euid(recv.getRuntime());
     }
     @JRubyMethod(name = "euid", module = true, visibility = PRIVATE)
     public static IRubyObject euid(ThreadContext context, IRubyObject recv) {
         return euid(context.getRuntime());
     }
     public static IRubyObject euid(Ruby runtime) {
-        return runtime.newFixnum(runtime.getPosix().geteuid());
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().geteuid()));
     }
 
     @Deprecated
     public static IRubyObject uid_set(IRubyObject recv, IRubyObject arg) {
         return uid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "uid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject uid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return uid_set(context.getRuntime(), arg);
     }
     public static IRubyObject uid_set(Ruby runtime, IRubyObject arg) {
-        runtime.getPosix().setuid((int)arg.convertToInteger().getLongValue());
+        checkErrno(runtime, runtime.getPosix().setuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject gid(IRubyObject recv) {
         return gid(recv.getRuntime());
     }
     @JRubyMethod(name = "gid", module = true, visibility = PRIVATE)
     public static IRubyObject gid(ThreadContext context, IRubyObject recv) {
         return gid(context.getRuntime());
     }
     public static IRubyObject gid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
-        return runtime.newFixnum(runtime.getPosix().getgid());
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getgid()));
     }
 
     @JRubyMethod(name = "maxgroups", module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "getpriority", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject getpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject getpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
-        int result = runtime.getPosix().getpriority(which, who);
+        int result = checkErrno(runtime, runtime.getPosix().getpriority(which, who));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject uid(IRubyObject recv) {
         return uid(recv.getRuntime());
     }
     @JRubyMethod(name = "uid", module = true, visibility = PRIVATE)
     public static IRubyObject uid(ThreadContext context, IRubyObject recv) {
         return uid(context.getRuntime());
     }
     public static IRubyObject uid(Ruby runtime) {
-        return runtime.newFixnum(runtime.getPosix().getuid());
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getuid()));
     }
 
     @Deprecated
     public static IRubyObject waitpid2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
     public static IRubyObject waitpid2(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
-        pid = runtime.getPosix().waitpid(pid, status, flags);
-        
-        if (pid == -1) {
-            throw runtime.newErrnoECHILDError();
-        }
+        pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
         
         return runtime.newArray(runtime.newFixnum(pid), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
     }
 
     @JRubyMethod(name = "initgroups", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject initgroups(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#initgroups not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups_set not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject ppid(IRubyObject recv) {
         return ppid(recv.getRuntime());
     }
     @JRubyMethod(name = "ppid", module = true, visibility = PRIVATE)
     public static IRubyObject ppid(ThreadContext context, IRubyObject recv) {
         return ppid(context.getRuntime());
     }
     public static IRubyObject ppid(Ruby runtime) {
-        return runtime.newFixnum(runtime.getPosix().getppid());
+        int result = checkErrno(runtime, runtime.getPosix().getppid());
+
+        return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject gid_set(IRubyObject recv, IRubyObject arg) {
         return gid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "gid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return gid_set(context.getRuntime(), arg);
     }
     public static IRubyObject gid_set(Ruby runtime, IRubyObject arg) {
-        return runtime.newFixnum(runtime.getPosix().setgid((int)arg.convertToInteger().getLongValue()));
+        int result = checkErrno(runtime, runtime.getPosix().setgid((int)arg.convertToInteger().getLongValue()));
+
+        return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject wait2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
 
     @Deprecated
     public static IRubyObject euid_set(IRubyObject recv, IRubyObject arg) {
         return euid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "euid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject euid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return euid_set(context.getRuntime(), arg);
     }
     public static IRubyObject euid_set(Ruby runtime, IRubyObject arg) {
-        runtime.getPosix().seteuid((int)arg.convertToInteger().getLongValue());
+        checkErrno(runtime, runtime.getPosix().seteuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject setpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(recv.getRuntime(), arg1, arg2, arg3);
     }
     @JRubyMethod(name = "setpriority", required = 3, module = true, visibility = PRIVATE)
     public static IRubyObject setpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(context.getRuntime(), arg1, arg2, arg3);
     }
     public static IRubyObject setpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int prio = (int)arg3.convertToInteger().getLongValue();
         runtime.getPosix().errno(0);
-        int result = runtime.getPosix().setpriority(which, who, prio);
-        if (result == -1) {
-            if (runtime.getPosix().errno() == Errno.EACCES.value()) {
-                throw runtime.newErrnoEACCESError("Permission denied");
-            }
-        }
+        int result = checkErrno(runtime, runtime.getPosix().setpriority(which, who, prio));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject setpgid(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "setpgid", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject setpgid(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject setpgid(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int pid = (int)arg1.convertToInteger().getLongValue();
         int gid = (int)arg2.convertToInteger().getLongValue();
-        return runtime.newFixnum(runtime.getPosix().setpgid(pid, gid));
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(pid, gid)));
     }
 
     @Deprecated
     public static IRubyObject getpgid(IRubyObject recv, IRubyObject arg) {
         return getpgid(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getpgid", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getpgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getpgid(context.getRuntime(), arg);
     }
     public static IRubyObject getpgid(Ruby runtime, IRubyObject arg) {
-        return runtime.newFixnum(runtime.getPosix().getpgid((int)arg.convertToInteger().getLongValue()));
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getpgid((int)arg.convertToInteger().getLongValue())));
     }
 
     @Deprecated
     public static IRubyObject getrlimit(IRubyObject recv, IRubyObject arg) {
         return getrlimit(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getrlimit", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getrlimit(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getrlimit(context.getRuntime(), arg);
     }
     public static IRubyObject getrlimit(Ruby runtime, IRubyObject arg) {
         throw runtime.newNotImplementedError("Process#getrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject egid(IRubyObject recv) {
         return egid(recv.getRuntime());
     }
     @JRubyMethod(name = "egid", module = true, visibility = PRIVATE)
     public static IRubyObject egid(ThreadContext context, IRubyObject recv) {
         return egid(context.getRuntime());
     }
     public static IRubyObject egid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
-        return runtime.newFixnum(runtime.getPosix().getegid());
+        return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getegid()));
     }
     
     private static String[] signals = new String[] {"EXIT", "HUP", "INT", "QUIT", "ILL", "TRAP", 
         "ABRT", "POLL", "FPE", "KILL", "BUS", "SEGV", "SYS", "PIPE", "ALRM", "TERM", "URG", "STOP",
         "TSTP", "CONT", "CHLD", "TTIN", "TTOU", "XCPU", "XFSZ", "VTALRM", "PROF", "USR1", "USR2"};
     
     private static int parseSignalString(Ruby runtime, String value) {
         int startIndex = 0;
         boolean negative = value.startsWith("-");
         
         if (negative) startIndex++;
         
         boolean signalString = value.startsWith("SIG", startIndex);
         
         if (signalString) startIndex += 3;
        
         String signalName = value.substring(startIndex);
         
         // FIXME: This table will get moved into POSIX library so we can get all actual supported
         // signals.  This is a quick fix to support basic signals until that happens.
         for (int i = 0; i < signals.length; i++) {
             if (signals[i].equals(signalName)) return negative ? -i : i;
         }
         
         throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
     }
 
     @Deprecated
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) {
         return kill(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "kill", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject kill(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return kill(context.getRuntime(), args);
     }
     public static IRubyObject kill(Ruby runtime, IRubyObject[] args) {
         if (args.length < 2) {
             throw runtime.newArgumentError("wrong number of arguments -- kill(sig, pid...)");
         }
 
         // Windows does not support these functions, so we won't even try
         // This also matches Ruby behavior for JRUBY-2353.
         if (Platform.IS_WINDOWS) {
             return runtime.getNil();
         }
         
         int signal;
         if (args[0] instanceof RubyFixnum) {
             signal = (int) ((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubySymbol) {
             signal = parseSignalString(runtime, args[0].toString());
         } else if (args[0] instanceof RubyString) {
             signal = parseSignalString(runtime, args[0].toString());
         } else {
             signal = parseSignalString(runtime, args[0].checkStringType().toString());
         }
 
         boolean processGroupKill = signal < 0;
         
         if (processGroupKill) signal = -signal;
         
         POSIX posix = runtime.getPosix();
         for (int i = 1; i < args.length; i++) {
             int pid = RubyNumeric.num2int(args[i]);
 
             // FIXME: It may be possible to killpg on systems which support it.  POSIX library
             // needs to tell whether a particular method works or not
             if (pid == 0) pid = runtime.getPosix().getpid();
             checkErrno(runtime, posix.kill(processGroupKill ? -pid : pid, signal));
         }
         
         return runtime.newFixnum(args.length - 1);
 
     }
 
     @JRubyMethod(name = "detach", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject detach(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         final int pid = (int)arg.convertToInteger().getLongValue();
         Ruby runtime = context.getRuntime();
         
         BlockCallback callback = new BlockCallback() {
             public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
                 int[] status = new int[1];
-                int result = context.getRuntime().getPosix().waitpid(pid, status, 0);
+                Ruby runtime = context.runtime;
+                int result = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, 0));
                 
-                return context.getRuntime().newFixnum(result);
+                return runtime.newFixnum(result);
             }
         };
         
         return RubyThread.newInstance(
                 runtime.getThread(),
                 IRubyObject.NULL_ARRAY,
                 CallBlock.newCallClosure(recv, (RubyModule)recv, Arity.NO_ARGUMENTS, callback, context));
     }
 
     @Deprecated
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         return times(recv.getRuntime());
     }
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject times(ThreadContext context, IRubyObject recv, Block unusedBlock) {
         return times(context.getRuntime());
     }
     public static IRubyObject times(Ruby runtime) {
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     @Deprecated
     public static IRubyObject pid(IRubyObject recv) {
         return pid(recv.getRuntime());
     }
     @JRubyMethod(name = "pid", module = true, visibility = PRIVATE)
     public static IRubyObject pid(ThreadContext context, IRubyObject recv) {
         return pid(context.getRuntime());
     }
     public static IRubyObject pid(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpid());
     }
     
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         return RubyKernel.fork(context, recv, block);
     }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, compat = CompatVersion.RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long pid = ShellLauncher.runWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
     
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit(recv, args);
     }
+    
+    private static final NonNativeErrno IGNORE = new NonNativeErrno() {
+        public int handle(Ruby runtime, int result) {return result;}
+    };
+
+    private static int checkErrno(Ruby runtime, int result) {
+        return checkErrno(runtime, result, IGNORE);
+    }
+
+    private static int checkErrno(Ruby runtime, int result, NonNativeErrno nonNative) {
+        if (result == -1) {
+            if (runtime.getPosix().isNative()) {
+                throw runtime.newErrnoFromInt(runtime.getPosix().errno());
+            } else {
+                nonNative.handle(runtime, result);
+            }
+        }
+        return result;
+    }
 }
diff --git a/src/org/jruby/runtime/TraceType.java b/src/org/jruby/runtime/TraceType.java
index e12e6223bf..1a807dd92a 100644
--- a/src/org/jruby/runtime/TraceType.java
+++ b/src/org/jruby/runtime/TraceType.java
@@ -1,345 +1,390 @@
 package org.jruby.runtime;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyString;
 import org.jruby.runtime.ThreadContext.RubyStackTraceElement;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class TraceType {
     private final Gather gather;
     private final Format format;
 
     public TraceType(Gather gather, Format format) {
         this.gather = gather;
         this.format = format;
     }
 
     public RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException) {
         return gather.getBacktrace(context, nativeException);
     }
 
     public String printBacktrace(RubyException exception) {
         return format.printBacktrace(exception);
     }
 
     public static TraceType traceTypeFor(String style) {
         if (style.equalsIgnoreCase("raw")) return new TraceType(Gather.RAW, Format.JRUBY);
         else if (style.equalsIgnoreCase("ruby_framed")) return new TraceType(Gather.NORMAL, Format.JRUBY);
         else if (style.equalsIgnoreCase("rubinius")) return new TraceType(Gather.NORMAL, Format.RUBINIUS);
         else if (style.equalsIgnoreCase("full")) return new TraceType(Gather.FULL, Format.JRUBY);
         else if (style.equalsIgnoreCase("mri")) return new TraceType(Gather.NORMAL, Format.MRI);
         else return new TraceType(Gather.NORMAL, Format.JRUBY);
     }
     
     public enum Gather {
         /**
          * Full raw backtraces with all Java frames included.
          */
         RAW {
             public RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException) {
                 return ThreadContext.gatherRawBacktrace(context.runtime, Thread.currentThread().getStackTrace());
             }
         },
 
         /**
          * A backtrace with interpreted frames intact, but don't remove Java frames.
          */
         FULL {
             public RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException) {
                 return TraceType.getBacktrace(context, nativeException, true);
             }
         },
 
         /**
          * Normal Ruby-style backtrace, showing only Ruby and core class methods.
          */
         NORMAL {
             public RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException) {
                 return TraceType.getBacktrace(context, nativeException, false);
             }
         };
 
         public abstract RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException);
     }
     
     public enum Format {
         /**
          * Formatting like C Ruby
          */
         MRI {
             public String printBacktrace(RubyException exception) {
                 return printBacktraceMRI(exception);
             }
         },
 
         /**
          * New JRuby formatting
          */
         JRUBY {
             public RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException) {
                 return TraceType.getBacktrace(context, nativeException, true);
             }
 
             public String printBacktrace(RubyException exception) {
                 return printBacktraceJRuby(exception);
             }
         },
 
         /**
          * Rubinius-style formatting
          */
         RUBINIUS {
             public String printBacktrace(RubyException exception) {
                 return printBacktraceRubinius(exception);
             }
         };
 
         public abstract String printBacktrace(RubyException exception);
     }
 
     protected static String printBacktraceMRI(RubyException exception) {
         Ruby runtime = exception.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         IRubyObject backtrace = exception.callMethod(context, "backtrace");
 
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PrintStream errorStream = new PrintStream(baos);
         boolean printedPosition = false;
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (context.getFile() != null && context.getFile().length() > 0) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
                 printedPosition = true;
             } else {
                 errorStream.print(context.getLine());
                 printedPosition = true;
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(context, errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first();
 
             if (mesg.isNil()) {
                 printErrorPos(context, errorStream);
             } else {
                 errorStream.print(mesg);
                 printedPosition = true;
             }
         }
 
         RubyClass type = exception.getMetaClass();
         String info = exception.toString();
 
         if (printedPosition) errorStream.print(": ");
 
         if (type == runtime.getRuntimeError() && (info == null || info.length() == 0)) {
             errorStream.print(": unhandled exception\n");
         } else {
             String path = type.getName();
 
             if (info.length() == 0) {
                 errorStream.print(path + '\n');
             } else {
                 if (path.startsWith("#")) {
                     path = null;
                 }
 
                 String tail = null;
                 if (info.indexOf("\n") != -1) {
                     tail = info.substring(info.indexOf("\n") + 1);
                     info = info.substring(0, info.indexOf("\n"));
                 }
 
                 errorStream.print(info);
 
                 if (path != null) {
                     errorStream.print(" (" + path + ")\n");
                 }
 
                 if (tail != null) {
                     errorStream.print(tail + '\n');
                 }
             }
         }
 
         exception.printBacktrace(errorStream);
 
         return new String(baos.toByteArray());
     }
 
     private static final String FIRST_COLOR = "\033[0;31m";
     private static final String KERNEL_COLOR = "\033[0;36m";
     private static final String EVAL_COLOR = "\033[0;33m";
     private static final String CLEAR_COLOR = "\033[0m";
 
     protected static String printBacktraceRubinius(RubyException exception) {
         Ruby runtime = exception.getRuntime();
         RubyStackTraceElement[] frames = exception.getBacktraceElements();
         if (frames == null) frames = new RubyStackTraceElement[0];
 
         ArrayList firstParts = new ArrayList();
         int longestFirstPart = 0;
         for (RubyStackTraceElement frame : frames) {
             String firstPart = frame.getClassName() + "#" + frame.getMethodName();
             if (firstPart.length() > longestFirstPart) longestFirstPart = firstPart.length();
             firstParts.add(firstPart);
         }
 
         // determine spacing
         int center = longestFirstPart
                 + 2 // initial spaces
                 + 1; // spaces before "at"
 
         StringBuilder buffer = new StringBuilder();
 
         buffer
                 .append("An exception has occurred:\n")
                 .append("    ");
 
         if (exception.getMetaClass() == runtime.getRuntimeError() && exception.message(runtime.getCurrentContext()).toString().length() == 0) {
             buffer.append("No current exception (RuntimeError)");
         } else {
             buffer.append(exception.message(runtime.getCurrentContext()).toString());
         }
 
         buffer
                 .append('\n')
                 .append('\n')
                 .append("Backtrace:\n");
 
         int i = 0;
         for (RubyStackTraceElement frame : frames) {
             String firstPart = (String)firstParts.get(i);
             String secondPart = frame.getFileName() + ":" + frame.getLineNumber();
 
             if (i == 0) {
                 buffer.append(FIRST_COLOR);
             } else if (frame.isBinding() || frame.getFileName().equals("(eval)")) {
                 buffer.append(EVAL_COLOR);
             } else if (frame.getFileName().indexOf(".java") != -1) {
                 buffer.append(KERNEL_COLOR);
             }
             buffer.append("  ");
             for (int j = 0; j < center - firstPart.length(); j++) {
                 buffer.append(' ');
             }
             buffer.append(firstPart);
             buffer.append(" at ");
             buffer.append(secondPart);
             buffer.append(CLEAR_COLOR);
             buffer.append('\n');
             i++;
         }
 
         return buffer.toString();
     }
 
     protected static String printBacktraceJRuby(RubyException exception) {
         Ruby runtime = exception.getRuntime();
         RubyStackTraceElement[] frames = exception.getBacktraceElements();
         if (frames == null) frames = new RubyStackTraceElement[0];
 
+        // find longest method name
+        int longestMethod = 0;
+        for (RubyStackTraceElement frame : frames) {
+            longestMethod = Math.max(longestMethod, frame.getMethodName().length());
+        }
+
+        StringBuilder buffer = new StringBuilder();
+
+        // exception line
+        String message = exception.message(runtime.getCurrentContext()).toString();
+        if (exception.getMetaClass() == runtime.getRuntimeError() && message.length() == 0) {
+            message = "No current exception";
+        }
+        buffer
+                .append(exception.getMetaClass().getName())
+                .append(": ")
+                .append(message)
+                .append('\n');
+
+        // backtrace lines
+        for (RubyStackTraceElement frame : frames) {
+            buffer.append("  ");
+
+            // method name
+            String methodName = frame.getMethodName();
+            for (int j = 0; j < longestMethod - methodName.length(); j++) {
+                buffer.append(' ');
+            }
+            buffer
+                    .append(methodName)
+                    .append(" at ")
+                    .append(frame.getFileName())
+                    .append(':')
+                    .append(frame.getLineNumber())
+                    .append('\n');
+        }
+
+        return buffer.toString();
+    }
+
+    protected static String printBacktraceJRuby2(RubyException exception) {
+        Ruby runtime = exception.getRuntime();
+        RubyStackTraceElement[] frames = exception.getBacktraceElements();
+        if (frames == null) frames = new RubyStackTraceElement[0];
+
         List<String> lineNumbers = new ArrayList(frames.length);
 
         // find longest filename and line number
         int longestFileName = 0;
         int longestLineNumber = 0;
         for (RubyStackTraceElement frame : frames) {
             String lineNumber = String.valueOf(frame.getLineNumber());
             lineNumbers.add(lineNumber);
             
             longestFileName = Math.max(longestFileName, frame.getFileName().length());
             longestLineNumber = Math.max(longestLineNumber, String.valueOf(frame.getLineNumber()).length());
         }
 
         StringBuilder buffer = new StringBuilder();
 
         // exception line
         String message = exception.message(runtime.getCurrentContext()).toString();
         if (exception.getMetaClass() == runtime.getRuntimeError() && message.length() == 0) {
             message = "No current exception";
         }
         buffer
                 .append(exception.getMetaClass().getName())
                 .append(": ")
                 .append(message)
                 .append('\n');
 
         // backtrace lines
         int i = 0;
         for (RubyStackTraceElement frame : frames) {
             buffer.append("  ");
 
             // file and line, centered on :
             String fileName = frame.getFileName();
             String lineNumber = lineNumbers.get(i);
             for (int j = 0; j < longestFileName - fileName.length(); j++) {
                 buffer.append(' ');
             }
             buffer
                     .append(fileName)
                     .append(":")
                     .append(lineNumber);
 
             // padding to center remainder on "in"
             for (int l = 0; l < longestLineNumber - lineNumber.length(); l++) {
                 buffer.append(' ');
             }
 
             // method name
             buffer
                     .append(' ')
                     .append("in ")
                     .append(frame.getMethodName())
                     .append('\n');
             
             i++;
         }
 
         return buffer.toString();
     }
 
     public static IRubyObject generateMRIBacktrace(Ruby runtime, RubyStackTraceElement[] trace) {
         if (trace == null) {
             return runtime.getNil();
         }
 
         RubyArray traceArray = RubyArray.newArray(runtime);
 
         for (int i = 0; i < trace.length; i++) {
             RubyStackTraceElement element = trace[i];
 
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
             traceArray.append(str);
         }
 
         return traceArray;
     }
 
     protected static RubyStackTraceElement[] getBacktrace(ThreadContext context, boolean nativeException, boolean full) {
           return ThreadContext.gatherHybridBacktrace(
                         context.getRuntime(),
                         context.createBacktrace2(0, nativeException),
                         Thread.currentThread().getStackTrace(),
                         full);
     }
 
     private static void printErrorPos(ThreadContext context, PrintStream errorStream) {
         if (context.getFile() != null && context.getFile().length() > 0) {
             if (context.getFrameName() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
                 errorStream.print(":in '" + context.getFrameName() + '\'');
             } else if (context.getLine() != 0) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getFile());
             }
         }
     }
 }
\ No newline at end of file
