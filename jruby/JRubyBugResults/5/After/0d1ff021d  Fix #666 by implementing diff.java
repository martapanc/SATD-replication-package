diff --git a/src/jruby/kernel20.rb b/src/jruby/kernel20.rb
index efc1fe4e56..dde3c809a2 100644
--- a/src/jruby/kernel20.rb
+++ b/src/jruby/kernel20.rb
@@ -1,10 +1,11 @@
 # This is the Ruby 2.0-specific kernel file.
 
 # Currently, all 1.9 features are in 2.0. We will need to
 # differentiate when there are features from 1.9 removed
 # in 2.0.
 
 # These are loads so they don't pollute LOADED_FEATURES
 load 'jruby/kernel19.rb'
 load 'jruby/kernel20/enumerable.rb'
-load 'jruby/kernel20/range.rb'
\ No newline at end of file
+load 'jruby/kernel20/range.rb'
+load 'jruby/kernel20/load_error.rb'
\ No newline at end of file
diff --git a/src/jruby/kernel20/load_error.rb b/src/jruby/kernel20/load_error.rb
new file mode 100644
index 0000000000..3fa6c63c77
--- /dev/null
+++ b/src/jruby/kernel20/load_error.rb
@@ -0,0 +1,3 @@
+class LoadError
+  attr_reader :path
+end
\ No newline at end of file
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 4aeee020f0..f74a389659 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2565,2000 +2565,2006 @@ public final class Ruby {
      * or passed in via -E.
      * 
      * @return null or encoding
      */
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
 
     public void setJavaProxyClassFactory(JavaProxyClassFactory factory) {
         this.javaProxyClassFactory = factory;
     }
     
     public JavaProxyClassFactory getJavaProxyClassFactory() {
         return javaProxyClassFactory;
     }
             
     private static final EnumSet<RubyEvent> EVENTS2_0 = EnumSet.of(RubyEvent.B_CALL, RubyEvent.B_RETURN, RubyEvent.THREAD_BEGIN, RubyEvent.THREAD_END);
     public class CallTraceFuncHook extends EventHook {
         private RubyProc traceFunc;
         // filter out 2.0 events on non 2.0
         private EnumSet<RubyEvent> interest =
                 is2_0() ?
                 EnumSet.complementOf(EVENTS2_0) :
                 EnumSet.allOf(RubyEvent.class);
         
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
 
         @Override
         public boolean isInterestedInEvent(RubyEvent event) {
             return interest.contains(event);
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
             ProfileData profileData = threadService.getMainThread().getContext().getProfileData();
             printProfileData(profileData);
         }
 
         if (systemExit && status != 0) {
             throw newSystemExit(status);
         }
     }
     
     /**
      * Print the gathered profiling data.
      * @param profileData
      * @param out
      * @see RubyInstanceConfig#getProfilingMode()
      * @deprecated use printProfileData(ProfileData) or printProfileData(ProfileData,ProfileOutput)
      */
     public void printProfileData(ProfileData profileData, PrintStream out) {
         printProfileData(profileData, new ProfileOutput(out));
     }
 
     public void printProfileData(ProfileData profileData) {
         printProfileData(profileData, config.getProfileOutput());
     }
 
     public void printProfileData(ProfileData profileData, ProfileOutput output) {
         ProfilePrinter profilePrinter = ProfilePrinter.newPrinter(config.getProfilingMode(), profileData);
         if (profilePrinter != null) {
             output.printProfile(profilePrinter);
         } else {
             out.println("\nno printer for profile mode: " + config.getProfilingMode() + " !");
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
     
     public RubyArray getEmptyFrozenArray() {
         return emptyFrozenArray;
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
 
     public RaiseException newArgumentError(String name, int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong number of arguments calling `" + name + "` (" + got + " for " + expected + ")");
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
         return newLightweightErrnoException(getModule("IO").getClass("EAGAINWaitReadable"), message);
     }
 
     public RaiseException newErrnoEAGAINWritableError(String message) {
         return newLightweightErrnoException(getModule("IO").getClass("EAGAINWaitWritable"), message);
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
         RubyClass errnoClass = getErrno(getPosix().errno());
         if (errnoClass == null) errnoClass = systemCallError;
 
         return newRaiseException(errnoClass, null);
     }
     
     public RaiseException newErrnoFromInt(int errno, String methodName, String message) {
         if (Platform.IS_WINDOWS && ("stat".equals(methodName) || "lstat".equals(methodName))) {
             if (errno == 20047) return newErrnoENOENTError(message); // boo:bar UNC stat failure
             if (errno == Errno.ESRCH.intValue()) return newErrnoENOENTError(message); // ESRCH on stating ""
         }
         
         return newErrnoFromInt(errno, message);
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
 		return newErrnoEADDRFromBindException(be, null);
 	}
 
     public RaiseException newErrnoEADDRFromBindException(BindException be, String contextMessage) {
         String msg = be.getMessage();
         if (msg == null) {
             msg = "bind";
         } else {
             msg = "bind - " + msg;
         }
         if (contextMessage != null) {
             msg = msg + contextMessage;
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
     
     // This name sucks and should be replaced by newNameErrorfor 9k.
     public RaiseException newNameErrorObject(String message, IRubyObject name) {
         RubyException error = new RubyNameError(this, getNameError(), message, name);
 
         return new RaiseException(error, false);
     }
     
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, false);
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
 
+    public RaiseException newLoadError(String message, String path) {
+        RaiseException loadError = newRaiseException(getLoadError(), message);
+        if (is2_0()) loadError.getException().setInstanceVariable("@path", newString(path));
+        return loadError;
+    }
+
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
             throw newErrnoEBADFError();
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
     
     // You cannot set siphashEnabled property except via RubyInstanceConfig to avoid mixing hash functions.
     public boolean isSiphashEnabled() {
         return siphashEnabled;
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
     
     public Invalidator getCheckpointInvalidator() {
         return checkpointInvalidator;
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
      * Get the list of method holders for methods being profiled.
      */
     public ProfiledMethod[] getProfiledMethods() {
         return profiledMethods;
     }
     
     /**
      * Add a method, so it can be printed out later.
      *
      * @param name the name of the method
      * @param method
      */
     void addProfiledMethod(final String name, final DynamicMethod method) {
         if (!config.isProfiling()) return;
         if (method.isUndefined()) return;
 
         final int index = (int) method.getSerialNumber();
 
         if (index >= config.getProfileMaxMethods()) {
             warnings.warnOnce(ID.PROFILE_MAX_METHODS_EXCEEDED, "method count exceeds max of " + config.getProfileMaxMethods() + "; no new methods will be profiled");
             return;
         }
 
         synchronized(this) {
             if (profiledMethods.length <= index) {
                 int newSize = Math.min((int) index * 2 + 1, config.getProfileMaxMethods());
                 ProfiledMethod[] newProfiledMethods = new ProfiledMethod[newSize];
                 System.arraycopy(profiledMethods, 0, newProfiledMethods, 0, profiledMethods.length);
                 profiledMethods = newProfiledMethods;
             }
     
             // only add the first one we encounter, since others will probably share the original
             if (profiledMethods[index] == null) {
                 profiledMethods[index] = new ProfiledMethod(name, method);
             }
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
      * Increment the count of backtraces generated for warnings in this runtime.
      */
     public void incrementWarningCount() {
         warningCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getWarningCount() {
         return warningCount.get();
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
      * Mark Fixnum as reopened
      */
     public void reopenFixnum() {
         fixnumInvalidator.invalidate();
         fixnumReopened = true;
     }
     
     /**
      * Retrieve the invalidator for Fixnum reopening
      */
     public Invalidator getFixnumInvalidator() {
         return fixnumInvalidator;
     }
     
     /**
      * Whether the Float class has been reopened and modified
      */
     public boolean isFixnumReopened() {
         return fixnumReopened;
     }
     
     /**
      * Mark Float as reopened
      */
     public void reopenFloat() {
         floatInvalidator.invalidate();
         floatReopened = true;
     }
     
     /**
      * Retrieve the invalidator for Float reopening
      */
     public Invalidator getFloatInvalidator() {
         return floatInvalidator;
     }
     
     /**
      * Whether the Float class has been reopened and modified
      */
     public boolean isFloatReopened() {
         return floatReopened;
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
 
     public long getHashSeedK0() {
         return hashSeedK0;
     }
 
     public long getHashSeedK1() {
         return hashSeedK1;
     }
     
     public StaticScopeFactory getStaticScopeFactory() {
         return staticScopeFactory;
     }
 
     public FFI getFFI() {
         return ffi;
     }
 
     public void setFFI(FFI ffi) {
         this.ffi = ffi;
     }
 
     public RubyString getDefinedMessage(DefinedMessage definedMessage) {
         return definedMessages.get(definedMessage);
     }
 
     @Deprecated
     public int getSafeLevel() {
         return 0;
     }
 
     @Deprecated
     public void setSafeLevel(int safeLevel) {
     }
 
     @Deprecated
     public void checkSafeString(IRubyObject object) {
     }
 
     @Deprecated
     public void secure(int level) {
     }
 
     @Deprecated
     public CallbackFactory callbackFactory(Class<?> type) {
         throw new RuntimeException("callback-style handles are no longer supported in JRuby");
     }
 
     private final Invalidator constantInvalidator;
     private final Invalidator checkpointInvalidator;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private final List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     private boolean siphashEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private long globalState = 1;
 
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
             invalidByteSequenceError, fiberError, randomClass, keyError, locationClass;
 
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
             superMethodMissing, normalMethodMissing, defaultMethodMissing, respondTo;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private volatile String currentDirectory;
 
     // The "current line" global variable
     private volatile int currentLine = 0;
 
     private volatile IRubyObject argsFile;
 
     private final long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private boolean is1_9;
     private boolean is2_0;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
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
 
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index dc1bac84c2..82912288fc 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,1621 +1,1621 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002-2011 JRuby Community
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime.load;
 
 import org.jruby.util.collections.StringArraySet;
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URI;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFile;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.rbconfig.RbConfigLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import static org.jruby.util.URLUtil.getPath;
 import org.jruby.util.cli.Options;
 
 /**
  * <h2>How require works in JRuby</h2>
  *
  * When requiring a name from Ruby, JRuby will first remove any file
  * extension it knows about, thereby making it possible to use this string
  * to see if JRuby has already loaded the name in question. If a .rb
  * extension is specified, JRuby will only try those extensions when
  * searching. If a .so, .o, .dll, or .jar extension is specified, JRuby will
  * only try .so or .jar when searching. Otherwise, JRuby goes through the
  * known suffixes (.rb, .rb.ast.ser, .so, and .jar) and tries to find a
  * library with this name. The process for finding a library follows this
  * order for all searchable extensions:
  *
  * <ol>
  * <li>First, check if the name starts with 'jar:', then the path points to
  * a jar-file resource which is returned.</li>
  * <li>Second, try searching for the file in the current dir</li>
  * <li>Then JRuby looks through the load path trying these variants:
  *   <ol>
  *     <li>See if the current load path entry starts with 'jar:', if so
  *     check if this jar-file contains the name</li>
  *     <li>Otherwise JRuby tries to construct a path by combining the entry
  *     and the current working directy, and then see if a file with the
  *     correct name can be reached from this point.</li>
  *   </ol>
  * </li>
  * <li>If all these fail, try to load the name as a resource from
  * classloader resources, using the bare name as well as the load path
  * entries</li>
  * <li>When we get to this state, the normal JRuby loading has failed. At
  * this stage JRuby tries to load Java native extensions, by following this
  * process:
  *   <ol>
  *     <li>First it checks that we haven't already found a library. If we
  *     found a library of type JarredScript, the method continues.</li>
  *
  *     <li>The first step is translating the name given into a valid Java
  *     Extension class name. First it splits the string into each path
  *     segment, and then makes all but the last downcased. After this it
  *     takes the last entry, removes all underscores and capitalizes each
  *     part separated by underscores. It then joins everything together and
  *     tacks on a 'Service' at the end. Lastly, it removes all leading dots,
  *     to make it a valid Java FWCN.</li>
  *
  *     <li>If the previous library was of type JarredScript, we try to add
  *     the jar-file to the classpath</li>
  *
  *     <li>Now JRuby tries to instantiate the class with the name
  *     constructed. If this works, we return a ClassExtensionLibrary.
  *     Otherwise, the old library is put back in place, if there was one.
  *   </ol>
  * </li>
  * <li>When all separate methods have been tried and there was no result, a
  * LoadError will be raised.</li>
  * <li>Otherwise, the name will be added to the loaded features, and the
  * library loaded</li>
  * </ol>
  *
  * <h2>How to make a class that can get required by JRuby</h2>
  *
  * <p>First, decide on what name should be used to require the extension. In
  * this purely hypothetical example, this name will be
  * 'active_record/connection_adapters/jdbc_adapter'. Then create the class
  * name for this require-name, by looking at the guidelines above. Our class
  * should be named active_record.connection_adapters.JdbcAdapterService, and
  * implement one of the library-interfaces. The easiest one is
  * BasicLibraryService, where you define the basicLoad-method, which will
  * get called when your library should be loaded.</p>
  *
  * <p>The next step is to either put your compiled class on JRuby's
  * classpath, or package the class/es inside a jar-file. To package into a
  * jar-file, we first create the file, then rename it to jdbc_adapter.jar.
  * Then we put this jar-file in the directory
  * active_record/connection_adapters somewhere in JRuby's load path. For
  * example, copying jdbc_adapter.jar into
  * JRUBY_HOME/lib/ruby/site_ruby/1.8/active_record/connection_adapters will
  * make everything work. If you've packaged your extension inside a RubyGem,
  * write a setub.rb-script that copies the jar-file to this place.</p>
  *
  * @author jpetersen
  */
 public class LoadService {
     private static final Logger LOG = LoggerFactory.getLogger("LoadService");
 
     private final LoadTimer loadTimer;
 
     public enum SuffixType {
         Source, Extension, Both, Neither;
 
         private static final String[] emptySuffixes = { "" };
         // NOTE: always search .rb first for speed
         public static final String[] sourceSuffixes = { ".rb", ".class" };
         public static final String[] extensionSuffixes;
         private static final String[] allSuffixes;
 
         static {                // compute based on platform
             if (Options.CEXT_ENABLED.load()) {
                 if (Platform.IS_WINDOWS) {
                     extensionSuffixes = new String[]{".jar", ".dll", ".jar.rb"};
                 } else if (Platform.IS_MAC) {
                     extensionSuffixes = new String[]{".jar", ".bundle", ".jar.rb"};
                 } else {
                     extensionSuffixes = new String[]{".jar", ".so", ".jar.rb"};
                 }
             } else {
                 extensionSuffixes = new String[]{".jar", ".jar.rb"};
             }
             allSuffixes = new String[sourceSuffixes.length + extensionSuffixes.length];
             System.arraycopy(sourceSuffixes, 0, allSuffixes, 0, sourceSuffixes.length);
             System.arraycopy(extensionSuffixes, 0, allSuffixes, sourceSuffixes.length, extensionSuffixes.length);
         }
 
         public String[] getSuffixes() {
             switch (this) {
             case Source:
                 return sourceSuffixes;
             case Extension:
                 return extensionSuffixes;
             case Both:
                 return allSuffixes;
             case Neither:
                 return emptySuffixes;
             }
             throw new RuntimeException("Unknown SuffixType: " + this);
         }
     }
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|bundle|jar)$");
 
     protected RubyArray loadPath;
     protected StringArraySet loadedFeatures;
     protected RubyArray loadedFeaturesDup;
     private final Map<String, String> loadedFeaturesIndex = new ConcurrentHashMap<String, String>();
     protected final Map<String, Library> builtinLibraries = new HashMap<String, Library>();
 
     protected final Map<String, JarFile> jarFiles = new HashMap<String, JarFile>();
 
     protected final Ruby runtime;
 
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         if (RubyInstanceConfig.DEBUG_LOAD_TIMINGS) {
             loadTimer = new TracingLoadTimer();
         } else {
             loadTimer = new LoadTimer();
         }
     }
 
     /**
      * Called to initialize the load path with a set of optional prepended
      * directories and then the standard set of dirs.
      *
      * This should only be called once, at load time, since it wipes out loaded
      * features.
      *
      * @param prependDirectories
      */
     public void init(List prependDirectories) {
         loadPath = RubyArray.newArray(runtime);
 
         String jrubyHome = runtime.getJRubyHome();
         loadedFeatures = new StringArraySet(runtime);
 
         // add all startup load paths to the list first
         addPaths(prependDirectories);
 
         // add $RUBYLIB paths
         RubyHash env = (RubyHash) runtime.getObject().getConstant("ENV");
         RubyString env_rubylib = runtime.newString("RUBYLIB");
         if (env.has_key_p(env_rubylib).isTrue()) {
             String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
             String[] paths = rubylib.split(File.pathSeparator);
             addPaths(paths);
         }
 
         // wrap in try/catch for security exceptions in an applet
         try {
             if (jrubyHome != null) {
                 // siteDir has to come first, because rubygems insert paths after it
                 // and we must to prefer Gems to rubyLibDir/rubySharedLibDir (same as MRI)
                 addPath(RbConfigLibrary.getSiteDir(runtime));
                 // if vendorDirGeneral is different than siteDirGeneral,
                 // add vendorDir, too
                 // adding {vendor,site}{Lib,Arch}Dir dirs is not necessary,
                 // since they should be the same as {vendor,site}Dir
                 if (!RbConfigLibrary.isSiteVendorSame(runtime)) {
                     addPath(RbConfigLibrary.getVendorDir(runtime));
                 }
                 String rubygemsDir = RbConfigLibrary.getRubygemsDir(runtime);
                 if (rubygemsDir != null) {
                     addPath(rubygemsDir);
                 }
                 addPath(RbConfigLibrary.getRubySharedLibDir(runtime));
                 // if 2.0, we append 1.9 libs; our copy of 2.0 only has diffs right now
                 if (runtime.is2_0()) {
                     addPath(RbConfigLibrary.getRubyLibDirFor(runtime, "2.0"));
                 }
                 addPath(RbConfigLibrary.getRubyLibDir(runtime));
             }
 
         } catch(SecurityException ignore) {}
 
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (!runtime.is1_9()) {
             addPath(".");
         }
     }
 
     /**
      * Add additional directories to the load path.
      *
      * @param additionalDirectories a List of additional dirs to append to the load path
      */
     public void addPaths(List<String> additionalDirectories) {
         for (String dir : additionalDirectories) {
             addPath(dir);
         }
     }
 
     /**
      * Add additional directories to the load path.
      *
      * @param additionalDirectories an array of additional dirs to append to the load path
      */
     public void addPaths(String... additionalDirectories) {
         for (String dir : additionalDirectories) {
             addPath(dir);
         }
     }
     
     protected boolean isFeatureInIndex(String shortName) {
         return loadedFeaturesIndex.containsKey(shortName);
     }
 
     @Deprecated
     protected void addLoadedFeature(String name) {
         addLoadedFeature(name, name);
     }
 
     protected void addLoadedFeature(String shortName, String name) {
         loadedFeatures.append(RubyString.newString(runtime, name));
         
         addFeatureToIndex(shortName, name);
     }
     
     protected void addFeatureToIndex(String shortName, String name) {
         loadedFeaturesDup = (RubyArray)loadedFeatures.dup();
         loadedFeaturesIndex.put(shortName, name);
     }
 
     protected void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
 
         synchronized(loadPath) {
             loadPath.append(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file, boolean wrap) {
         if(!runtime.getProfile().allowLoad(file)) {
-            throw runtime.newLoadError("no such file to load -- " + file);
+            throw runtime.newLoadError("no such file to load -- " + file, file);
         }
 
         SearchState state = new SearchState(file);
         state.prepareLoadSearch(file);
 
         Library library = findBuiltinLibrary(state, state.searchFile, state.suffixType);
         if (library == null) library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
 
         if (library == null) {
             library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
             if (library == null) {
-                throw runtime.newLoadError("no such file to load -- " + file);
+                throw runtime.newLoadError("no such file to load -- " + file, file);
             }
         }
         try {
             library.load(runtime, wrap);
         } catch (IOException e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             throw newLoadErrorFromThrowable(runtime, file, e);
         }
     }
 
     public void loadFromClassLoader(ClassLoader classLoader, String file, boolean wrap) {
         SearchState state = new SearchState(file);
         state.prepareLoadSearch(file);
 
         Library library = null;
         LoadServiceResource resource = getClassPathResource(classLoader, file);
         if (resource != null) {
             state.loadName = resolveLoadName(resource, file);
             library = createLibrary(state, resource);
         }
         if (library == null) {
             throw runtime.newLoadError("no such file to load -- " + file);
         }
         try {
             library.load(runtime, wrap);
         } catch (IOException e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             throw newLoadErrorFromThrowable(runtime, file, e);
         }
     }
 
     public SearchState findFileForLoad(String file) {
         if (Platform.IS_WINDOWS) {
             file = file.replace('\\', '/');
         }
         // Even if we don't support .so, some stdlib require .so directly.
         // Replace it with .jar to look for a java extension
         // JRUBY-5033: The ExtensionSearcher will locate C exts, too, this way.
         if (file.endsWith(".so")) {
             file = file.replaceAll(".so$", ".jar");
         }
 
         SearchState state = new SearchState(file);
         state.prepareRequireSearch(file);
 
         for (LoadSearcher searcher : searchers) {
             if (searcher.shouldTrySearch(state)) {
                 if (!searcher.trySearch(state)) {
                     return null;
                 }
             }
         }
 
         return state;
     }
 
     public boolean require(String requireName) {
         return requireCommon(requireName, true) == RequireState.LOADED;
     }
 
     public boolean autoloadRequire(String requireName) {
         return requireCommon(requireName, false) != RequireState.CIRCULAR;
     }
 
     private enum RequireState {
         LOADED, ALREADY_LOADED, CIRCULAR
     };
 
     private RequireState requireCommon(String requireName, boolean circularRequireWarning) {
         // check for requiredName without extension.
         if (featureAlreadyLoaded(requireName)) {
             return RequireState.ALREADY_LOADED;
         }
 
         if (!requireLocks.lock(requireName)) {
             if (circularRequireWarning && runtime.isVerbose() && runtime.is1_9()) {
                 warnCircularRequire(requireName);
             }
             return RequireState.CIRCULAR;
         }
         try {
             if (!runtime.getProfile().allowRequire(requireName)) {
-                throw runtime.newLoadError("no such file to load -- " + requireName);
+                throw runtime.newLoadError("no such file to load -- " + requireName, requireName);
             }
 
             // check for requiredName again now that we're locked
             if (featureAlreadyLoaded(requireName)) {
                 return RequireState.ALREADY_LOADED;
             }
 
             // numbers from loadTimer does not include lock waiting time.
             long startTime = loadTimer.startLoad(requireName);
             try {
                 boolean loaded = smartLoadInternal(requireName);
                 return loaded ? RequireState.LOADED : RequireState.ALREADY_LOADED;
             } finally {
                 loadTimer.endLoad(requireName, startTime);
             }
         } finally {
             requireLocks.unlock(requireName);
         }
     }
     
     protected final RequireLocks requireLocks = new RequireLocks();
 
     private class RequireLocks {
         private final Map<String, ReentrantLock> pool;
         // global lock for require must be fair
         private final ReentrantLock globalLock;
 
         private RequireLocks() {
             this.pool = new HashMap<String, ReentrantLock>();
             this.globalLock = new ReentrantLock(true);
         }
 
         /**
          * Get exclusive lock for the specified requireName. Acquire sync object
          * for the requireName from the pool, then try to lock it. NOTE: This
          * lock is not fair for now.
          * 
          * @param requireName
          *            just a name for the lock.
          * @return If the sync object already locked by current thread, it just
          *         returns false without getting a lock. Otherwise true.
          */
         private boolean lock(String requireName) {
             ReentrantLock lock;
 
             while (true) {
                 synchronized (pool) {
                     lock = pool.get(requireName);
                     if (lock == null) {
                         if (runtime.getInstanceConfig().isGlobalRequireLock()) {
                             lock = globalLock;
                         } else {
                             lock = new ReentrantLock();
                         }
                         pool.put(requireName, lock);
                     } else if (lock.isHeldByCurrentThread()) {
                         return false;
                     }
                 }
 
                 lock.lock();
 
                 // repeat until locked object still in requireLocks.
                 synchronized (pool) {
                     if (pool.get(requireName) == lock) {
                         // the object is locked && the lock is in the pool
                         return true;
                     }
                     // go next try
                     lock.unlock();
                 }
             }
         }
 
         /**
          * Unlock the lock for the specified requireName.
          * 
          * @param requireName
          *            name of the lock to be unlocked.
          */
         private void unlock(String requireName) {
             synchronized (pool) {
                 ReentrantLock lock = pool.get(requireName);
                 if (lock != null) {
                     assert lock.isHeldByCurrentThread();
                     lock.unlock();
                     pool.remove(requireName);
                 }
             }
         }
     }
 
     protected void warnCircularRequire(String requireName) {
         runtime.getWarnings().warn("loading in progress, circular require considered harmful - " + requireName);
         // it's a hack for c:rb_backtrace impl.
         // We should introduce new method to Ruby.TraceType when rb_backtrace is widely used not only for this purpose.
         RaiseException ex = new RaiseException(runtime, runtime.getRuntimeError(), null, false);
         String trace = runtime.getInstanceConfig().getTraceType().printBacktrace(ex.getException(), runtime.getPosix().isatty(FileDescriptor.err));
         // rb_backtrace dumps to stderr directly.
         System.err.print(trace.replaceFirst("[^\n]*\n", ""));
     }
 
     /**
      * This method did require the specified file without getting a lock.
      * Now we offer safe version only. Use {@link LoadService#require(String)} instead.
      */
     @Deprecated
     public boolean smartLoad(String file) {
         return require(file);
     }
 
     private boolean smartLoadInternal(String file) {
         checkEmptyLoad(file);
         SearchState state = findFileForLoad(file);
         if (state == null) {
             return false;
         }
         if (state.library == null) {
-            throw runtime.newLoadError("no such file to load -- " + state.searchFile);
+            throw runtime.newLoadError("no such file to load -- " + state.searchFile, state.searchFile);
         }
 
         // check with long name
         if (featureAlreadyLoaded(state.loadName)) {
             return false;
         }
 
         boolean loaded = tryLoadingLibraryOrScript(runtime, state);
         if (loaded) {
             addLoadedFeature(file, state.loadName);
         }
         return loaded;
     }
 
     private static class LoadTimer {
         public long startLoad(String file) { return 0L; }
         public void endLoad(String file, long startTime) {}
     }
 
     private static class TracingLoadTimer extends LoadTimer {
         private final AtomicInteger indent = new AtomicInteger(0);
         private String getIndentString() {
             StringBuilder buf = new StringBuilder();
             int i = indent.get();
             for (int j = 0; j < i; j++) {
                 buf.append("  ");
             }
             return buf.toString();
         }
         @Override
         public long startLoad(String file) {
             indent.incrementAndGet();
             LOG.info(getIndentString() + "-> " + file);
             return System.currentTimeMillis();
         }
         @Override
         public void endLoad(String file, long startTime) {
             LOG.info(getIndentString() + "<- " + file + " - "
                     + (System.currentTimeMillis() - startTime) + "ms");
             indent.decrementAndGet();
         }
     }
 
     /**
      * Load the org.jruby.runtime.load.Library implementation specified by
      * className. The purpose of using this method is to avoid having static
      * references to the given library class, thereby avoiding the additional
      * classloading when the library is not in use.
      *
      * @param runtime The runtime in which to load
      * @param libraryName The name of the library, to use for error messages
      * @param className The class of the library
      * @param classLoader The classloader to use to load it
      * @param wrap Whether to wrap top-level in an anonymous module
      */
     public static void reflectedLoad(Ruby runtime, String libraryName, String className, ClassLoader classLoader, boolean wrap) {
         try {
             if (classLoader == null && Ruby.isSecurityRestricted()) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
 
             Object libObject = classLoader.loadClass(className).newInstance();
             if (libObject instanceof Library) {
                 Library library = (Library)libObject;
                 library.load(runtime, false);
             } else if (libObject instanceof BasicLibraryService) {
                 BasicLibraryService service = (BasicLibraryService)libObject;
                 service.basicLoad(runtime);
             } else {
                 // invalid type of library, raise error
-                throw runtime.newLoadError("library `" + libraryName + "' is not of type Library or BasicLibraryService");
+                throw runtime.newLoadError("library `" + libraryName + "' is not of type Library or BasicLibraryService", libraryName);
             }
         } catch (RaiseException re) {
             throw re;
         } catch (Throwable e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace();
-            throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
+            throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e, libraryName);
         }
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public void addBuiltinLibrary(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void removeBuiltinLibrary(String name) {
         builtinLibraries.remove(name);
     }
 
     public void removeInternalLoadedFeature(String name) {
         RubyString nameRubyString = runtime.newString(name);
         loadedFeatures.delete(runtime.getCurrentContext(), nameRubyString, Block.NULL_BLOCK);
     }
     
     private boolean isFeaturesIndexUpToDate() {
         // disable tracing during index check
         runtime.getCurrentContext().preTrace();
         try {
             return loadedFeaturesDup != null && loadedFeaturesDup.eql(loadedFeatures);
         } finally {
             runtime.getCurrentContext().postTrace();
         }
     }
 
     protected boolean featureAlreadyLoaded(String name) {
         if (loadedFeatures.containsString(name)) return true;
         
         // Bail if our features index fell out of date.
         if (!isFeaturesIndexUpToDate()) { 
             loadedFeaturesIndex.clear();
             return false;
         }
         
         return isFeatureInIndex(name);
     }
 
     protected boolean isJarfileLibrary(SearchState state, final String file) {
         return state.library instanceof JarredScript && file.endsWith(".jar");
     }
 
     protected void reraiseRaiseExceptions(Throwable e) throws RaiseException {
         if (e instanceof RaiseException) {
             throw (RaiseException) e;
         }
     }
 
     public interface LoadSearcher {
         /**
          * @param state
          * @return true if trySearch should be called.
          */
         public boolean shouldTrySearch(SearchState state);
 
         /**
          * @param state
          * @return false if loadSearch must be bail-out.
          */
         public boolean trySearch(SearchState state);
     }
 
     public class BailoutSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
 
         protected boolean trySearch(String file, SuffixType suffixType) {
             for (String suffix : suffixType.getSuffixes()) {
                 String searchName = file + suffix;
                 if (featureAlreadyLoaded(searchName)) {
                     return false;
                 }
             }
             return true;
         }
 
         public boolean trySearch(SearchState state) {
             return trySearch(state.searchFile, state.suffixType);
         }
     }
 
     public class SourceBailoutSearcher extends BailoutSearcher {
         public boolean shouldTrySearch(SearchState state) {
             // JRUBY-5032: Load extension files if they are required
             // explicitly, and even if an rb file of the same name
             // has already been loaded (effectively skipping the search for a source file).
             return !extensionPattern.matcher(state.loadName).find();
         }
 
         // According to Rubyspec, source files should be loaded even if an equally named
         // extension is loaded already. So we use the bailout search twice, once only
         // for source files and once for whatever suffix type the state determines
         public boolean trySearch(SearchState state) {
             return super.trySearch(state.searchFile, SuffixType.Source);
         }
     }
 
     public class NormalSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
 
         public boolean trySearch(SearchState state) {
             state.library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
             return true;
         }
     }
 
     public class ClassLoaderSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
 
         public boolean trySearch(SearchState state) {
             state.library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
             return true;
         }
     }
 
     public class ExtensionSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return (state.library == null || state.library instanceof JarredScript) && !state.searchFile.equalsIgnoreCase("");
         }
 
         public boolean trySearch(SearchState state) {
             debugLogTry("jarWithExtension", state.searchFile);
             
             // This code exploits the fact that all .jar files will be found for the JarredScript feature.
             // This is where the basic extension mechanism gets fixed
             Library oldLibrary = state.library;
 
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = state.searchFile.split("/");
 
             StringBuilder finName = new StringBuilder();
             for(int i=0, j=(all.length-1); i<j; i++) {
                 finName.append(all[i].toLowerCase()).append(".");
             }
 
             try {
                 // Make the class name look nice, by splitting on _ and capitalize each segment, then joining
                 // the, together without anything separating them, and last put on "Service" at the end.
                 String[] last = all[all.length-1].split("_");
                 for(int i=0, j=last.length; i<j; i++) {
                     finName.append(Character.toUpperCase(last[i].charAt(0))).append(last[i].substring(1));
                 }
                 finName.append("Service");
 
                 // We don't want a package name beginning with dots, so we remove them
                 String className = finName.toString().replaceAll("^\\.*","");
 
                 // If there is a jar-file with the required name, we add this to the class path.
                 if(state.library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     URL jarURL = ((JarredScript)state.library).getResource().getURL();
                     runtime.getJRubyClassLoader().addURL(jarURL);
                     debugLogFound("jarWithoutExtension", jarURL.toString());
                 }
 
                 // quietly try to load the class
                 Class theClass = runtime.getJavaSupport().loadJavaClass(className);
                 state.library = new ClassExtensionLibrary(className + ".java", theClass);
                 debugLogFound("jarWithExtension", className);
             } catch (ClassNotFoundException cnfe) {
                 if (runtime.isDebug()) cnfe.printStackTrace();
                 // we ignore this and assume the jar is not an extension
             } catch (UnsupportedClassVersionError ucve) {
                 if (runtime.isDebug()) ucve.printStackTrace();
-                throw runtime.newLoadError("JRuby ext built for wrong Java version in `" + finName + "': " + ucve);
+                throw runtime.newLoadError("JRuby ext built for wrong Java version in `" + finName + "': " + ucve, finName.toString());
             } catch (IOException ioe) {
                 if (runtime.isDebug()) ioe.printStackTrace();
-                throw runtime.newLoadError("IOException loading extension `" + finName + "`: " + ioe);
+                throw runtime.newLoadError("IOException loading extension `" + finName + "`: " + ioe, finName.toString());
             } catch (Exception e) {
                 if (runtime.isDebug()) e.printStackTrace();
-                throw runtime.newLoadError("Exception loading extension `" + finName + "`: " + e);
+                throw runtime.newLoadError("Exception loading extension `" + finName + "`: " + e, finName.toString());
             }
 
             // If there was a good library before, we go back to that
             if(state.library == null && oldLibrary != null) {
                 state.library = oldLibrary;
             }
             return true;
         }
     }
 
     public class ScriptClassSearcher implements LoadSearcher {
         public class ScriptClassLibrary implements Library {
             private Script script;
 
             public ScriptClassLibrary(Script script) {
                 this.script = script;
             }
 
             public void load(Ruby runtime, boolean wrap) {
                 runtime.loadScript(script, wrap);
             }
         }
 
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
 
         public boolean trySearch(SearchState state) throws RaiseException {
             // no library or extension found, try to load directly as a class
             Script script;
             String className = buildClassName(state.searchFile);
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 && lastSlashIndex < className.length() - 1 && !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script) scriptClass.newInstance();
             } catch (Exception cnfe) {
                 return true;
             }
             state.library = new ScriptClassLibrary(script);
             return true;
         }
     }
 
     public static class SearchState {
         public Library library;
         public String loadName;
         public SuffixType suffixType;
         public String searchFile;
 
         public SearchState(String file) {
             loadName = file;
         }
 
         public void prepareRequireSearch(final String file) {
             // if an extension is specified, try more targetted searches
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else if ((matcher = extensionPattern.matcher(file)).find()) {
                     // extension extensions
                     suffixType = SuffixType.Extension;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else if (file.endsWith(".class")) {
                     // For JRUBY-6731, treat require 'foo.class' as no other filename than 'foo.class'.
                     suffixType = SuffixType.Neither;
                     searchFile = file;
                 } else {
                     // unknown extension, fall back to search with extensions
                     suffixType = SuffixType.Both;
                     searchFile = file;
                 }
             } else {
                 // try all extensions
                 suffixType = SuffixType.Both;
                 searchFile = file;
             }
         }
 
         public void prepareLoadSearch(final String file) {
             // if a source extension is specified, try all source extensions
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to exact search
                     suffixType = SuffixType.Neither;
                     searchFile = file;
                 }
             } else {
                 // try only literal search
                 suffixType = SuffixType.Neither;
                 searchFile = file;
             }
         }
 
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(this.getClass().getName()).append(": ");
             sb.append("library=").append(library.toString());
             sb.append(", loadName=").append(loadName);
             sb.append(", suffixType=").append(suffixType.toString());
             sb.append(", searchFile=").append(searchFile);
             return sb.toString();
         }
     }
 
     protected boolean tryLoadingLibraryOrScript(Ruby runtime, SearchState state) {
         // attempt to load the found library
         try {
             state.library.load(runtime, false);
             return true;
         } catch (MainExitException mee) {
             // allow MainExitException to propagate out for exec and friends
             throw mee;
         } catch (Throwable e) {
             if(isJarfileLibrary(state, state.searchFile)) {
                 return true;
             }
             reraiseRaiseExceptions(e);
 
             if(runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
 
             RaiseException re = newLoadErrorFromThrowable(runtime, state.searchFile, e);
             re.initCause(e);
             throw re;
         }
     }
 
     private static RaiseException newLoadErrorFromThrowable(Ruby runtime, String file, Throwable t) {
         if (RubyInstanceConfig.DEBUG_PARSER) t.printStackTrace();
         
-        return runtime.newLoadError(String.format("load error: %s -- %s: %s", file, t.getClass().getName(), t.getMessage()));
+        return runtime.newLoadError(String.format("load error: %s -- %s: %s", file, t.getClass().getName(), t.getMessage()), file);
     }
 
     // Using the BailoutSearch twice, once only for source files and once for state suffixes,
     // in order to adhere to Rubyspec
     protected final List<LoadSearcher> searchers = new ArrayList<LoadSearcher>();
     {
         searchers.add(new SourceBailoutSearcher());
         searchers.add(new NormalSearcher());
         searchers.add(new ClassLoaderSearcher());
         searchers.add(new BailoutSearcher());
         searchers.add(new ExtensionSearcher());
         searchers.add(new ScriptClassSearcher());
     }
 
     protected String buildClassName(String className) {
         // Remove any relative prefix, e.g. "./foo/bar" becomes "foo/bar".
         className = className.replaceFirst("^\\.\\/", "");
         if (className.lastIndexOf(".") != -1) {
             className = className.substring(0, className.lastIndexOf("."));
         }
         className = className.replace("-", "_minus_").replace('.', '_');
         return className;
     }
 
     protected void checkEmptyLoad(String file) throws RaiseException {
         if (file.equals("")) {
-            throw runtime.newLoadError("no such file to load -- " + file);
+            throw runtime.newLoadError("no such file to load -- " + file, file);
         }
     }
 
     protected void debugLogTry(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             LOG.info( "LoadService: trying " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             LOG.info( "LoadService: found " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound( LoadServiceResource resource ) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             String resourceUrl;
             try {
                 resourceUrl = resource.getURL().toString();
             } catch (IOException e) {
                 resourceUrl = e.getMessage();
             }
             LOG.info( "LoadService: found: " + resourceUrl );
         }
     }
 
     protected Library findBuiltinLibrary(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             debugLogTry( "builtinLib",  namePlusSuffix );
             if (builtinLibraries.containsKey(namePlusSuffix)) {
                 state.loadName = namePlusSuffix;
                 Library lib = builtinLibraries.get(namePlusSuffix);
                 debugLogFound( "builtinLib", namePlusSuffix );
                 return lib;
             }
         }
         return null;
     }
 
     protected Library findLibraryWithoutCWD(SearchState state, String baseName, SuffixType suffixType) {
         Library library = null;
 
         switch (suffixType) {
         case Both:
             library = findBuiltinLibrary(state, baseName, SuffixType.Source);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Source));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Source));
             // If we fail to find as a normal Ruby script, we try to find as an extension,
             // checking for a builtin first.
             if (library == null) library = findBuiltinLibrary(state, baseName, SuffixType.Extension);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Extension));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Extension));
             break;
         case Source:
         case Extension:
             // Check for a builtin first.
             library = findBuiltinLibrary(state, baseName, suffixType);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, suffixType));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, suffixType));
             break;
         case Neither:
             library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Neither));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Neither));
             break;
         }
 
         return library;
     }
 
     protected Library findLibraryWithClassloaders(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String file = baseName + suffix;
             LoadServiceResource resource = findFileInClasspath(file);
             if (resource != null) {
                 state.loadName = resolveLoadName(resource, file);
                 return createLibrary(state, resource);
             }
         }
         return null;
     }
 
     protected Library createLibrary(SearchState state, LoadServiceResource resource) {
         if (resource == null) {
             return null;
         }
         String file = state.loadName;
         if (file.endsWith(".so") || file.endsWith(".dll") || file.endsWith(".bundle")) {
             if (runtime.getInstanceConfig().isCextEnabled()) {
                 return new CExtension(resource);
             } else {
-                throw runtime.newLoadError("C extensions are disabled, can't load `" + resource.getName() + "'");
+                throw runtime.newLoadError("C extensions are disabled, can't load `" + resource.getName() + "'", resource.getName());
             }
         } else if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     protected LoadServiceResource tryResourceFromCWD(SearchState state, String baseName,SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromCWD", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
                     foundResource = new LoadServiceResource(file, getFileName(file, namePlusSuffix), absolute);
                     debugLogFound(foundResource);
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
 
         return foundResource;
     }
 
     /**
      * Try loading the resource from the current dir by appending suffixes and
      * passing it to tryResourceAsIs to have the ./ replaced by CWD.
      */
     protected LoadServiceResource tryResourceFromDotSlash(SearchState state, String baseName, SuffixType suffixType) throws RaiseException {
         if (!runtime.is1_9()) return tryResourceFromCWD(state, baseName, suffixType);
         
         LoadServiceResource foundResource = null;
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             
             foundResource = tryResourceAsIs(namePlusSuffix, "resourceFromDotSlash");
             
             if (foundResource != null) break;
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromHome(SearchState state, String baseName, SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         RubyHash env = (RubyHash) runtime.getObject().getConstant("ENV");
         RubyString env_home = runtime.newString("HOME");
         if (env.has_key_p(env_home).isFalse()) {
             return null;
         }
         String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
         String path = baseName.substring(2);
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = path + suffix;
             // check home directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(home, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromHome", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
 
                     state.loadName = file.getPath();
                     foundResource = new LoadServiceResource(file, state.loadName, absolute);
                     debugLogFound(foundResource);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromJarURL(SearchState state, String baseName, SuffixType suffixType) {
         // if a jar or file URL, return load service resource directly without further searching
         LoadServiceResource foundResource = null;
         if (baseName.startsWith("jar:file:")) {
             return tryResourceFromJarURL(state, baseName.replaceFirst("jar:", ""), suffixType);
         } else if (baseName.startsWith("jar:")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     URI resourceUri = new URI("jar", namePlusSuffix.substring(4), null);
                     URL url = resourceUri.toURL();
                     debugLogTry("resourceFromJarURL", url.toString());
                     if (url.openStream() != null) {
                         foundResource = new LoadServiceResource(url, namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch (FileNotFoundException e) {
                 } catch (URISyntaxException e) {
                     throw runtime.newIOError(e.getMessage());
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 } catch (IOException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }
         } else if(baseName.startsWith("file:") && baseName.indexOf("!/") != -1) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     String jarFile = namePlusSuffix.substring(5, namePlusSuffix.indexOf("!/"));
                     JarFile file = new JarFile(jarFile);
                     String expandedFilename = expandRelativeJarPath(namePlusSuffix.substring(namePlusSuffix.indexOf("!/") + 2));
 
                     debugLogTry("resourceFromJarURL", expandedFilename.toString());
                     if(file.getJarEntry(expandedFilename) != null) {
                         URI resourceUri = new URI("jar", "file:" + jarFile + "!/" + expandedFilename, null);
                         foundResource = new LoadServiceResource(resourceUri.toURL(), namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch (URISyntaxException e) {
                     throw runtime.newIOError(e.getMessage());
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 } catch(Exception e) {}
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromLoadPathOrURL(SearchState state, String baseName, SuffixType suffixType) {
         LoadServiceResource foundResource = null;
 
         // if it's a ./ baseName, use ./ logic
         if (baseName.startsWith("./")) {
             foundResource = tryResourceFromDotSlash(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if it's a ~/ baseName use HOME logic
         if (baseName.startsWith("~/")) {
             foundResource = tryResourceFromHome(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if given path is absolute, just try it as-is (with extensions) and no load path
         if (new File(baseName).isAbsolute() || baseName.startsWith("../")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 foundResource = tryResourceAsIs(namePlusSuffix);
 
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     return foundResource;
                 }
             }
 
             return null;
         }
 
         Outer: for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             String loadPathEntry = getLoadPathEntry(loadPath.eltInternal(i));
 
             if (loadPathEntry.equals(".") || loadPathEntry.equals("")) {
                 foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
                 if (foundResource != null) {
                     String ss = foundResource.getName();
                     if(ss.startsWith("./")) {
                         ss = ss.substring(2);
                     }
                     state.loadName = resolveLoadName(foundResource, ss);
                     break Outer;
                 }
             } else {
                 boolean looksLikeJarURL = loadPathLooksLikeJarURL(loadPathEntry);
                 boolean looksLikeClasspathURL = loadPathLooksLikeClasspathURL(loadPathEntry);
                 for (String suffix : suffixType.getSuffixes()) {
                     String namePlusSuffix = baseName + suffix;
 
                     if (looksLikeJarURL) {
                         foundResource = tryResourceFromJarURLWithLoadPath(namePlusSuffix, loadPathEntry);
                     } else if (looksLikeClasspathURL) {
                         foundResource = findFileInClasspath(loadPathEntry + "/" + namePlusSuffix);
                     } else {
                         foundResource = tryResourceFromLoadPath(namePlusSuffix, loadPathEntry);
                     }
 
                     if (foundResource != null) {
                         String ss = namePlusSuffix;
                         if(ss.startsWith("./")) {
                             ss = ss.substring(2);
                         }
                         state.loadName = resolveLoadName(foundResource, ss);
                         break Outer; // end suffix iteration
                     }
                 }
             }
         }
 
         return foundResource;
     }
     
     protected String getLoadPathEntry(IRubyObject entry) {
         RubyString entryString = entry.convertToString();
         return entryString.asJavaString();
     }
 
     protected LoadServiceResource tryResourceFromJarURLWithLoadPath(String namePlusSuffix, String loadPathEntry) {
         LoadServiceResource foundResource = null;
 
         String[] urlParts = splitJarUrl(loadPathEntry);
         String jarFileName = urlParts[0];
         String entryPath = urlParts[1];
 
         JarFile current = getJarFile(jarFileName);
         if (current != null ) {
             String canonicalEntry = (entryPath.length() > 0 ? entryPath + "/" : "") + namePlusSuffix;
             debugLogTry("resourceFromJarURLWithLoadPath", current.getName() + "!/" + canonicalEntry);
             if (current.getJarEntry(canonicalEntry) != null) {
                 try {
                     URI resourceUri = new URI("jar", "file:" + jarFileName + "!/" + canonicalEntry, null);
                     foundResource = new LoadServiceResource(resourceUri.toURL(), resourceUri.toString());
                     debugLogFound(foundResource);
                 } catch (URISyntaxException e) {
                     throw runtime.newIOError(e.getMessage());
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
             }
         }
 
         return foundResource;
     }
 
     public JarFile getJarFile(String jarFileName) {
         JarFile jarFile = jarFiles.get(jarFileName);
         if(null == jarFile) {
             try {
                 jarFile = new JarFile(jarFileName);
                 jarFiles.put(jarFileName, jarFile);
             } catch (ZipException ignored) {
                 if (runtime.getInstanceConfig().isDebug()) {
                     LOG.info("ZipException trying to access " + jarFileName + ", stack trace follows:");
                     ignored.printStackTrace(runtime.getErr());
                 }
             } catch (FileNotFoundException ignored) {
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         }
         return jarFile;
     }
 
     protected boolean loadPathLooksLikeJarURL(String loadPathEntry) {
         return loadPathEntry.startsWith("jar:") || loadPathEntry.endsWith(".jar") || (loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!") != -1);
     }
 
     protected boolean loadPathLooksLikeClasspathURL(String loadPathEntry) {
         return loadPathEntry.startsWith("classpath:");
     }
     
     private String[] splitJarUrl(String loadPathEntry) {
         int idx = loadPathEntry.indexOf("!");
         if (idx == -1) {
             return new String[]{loadPathEntry, ""};
         }
 
         String filename = loadPathEntry.substring(0, idx);
         String entry = idx + 2 < loadPathEntry.length() ? loadPathEntry.substring(idx + 2) : "";
 
         if(filename.startsWith("jar:")) {
             filename = filename.substring(4);
         }
         
         if(filename.startsWith("file:")) {
             filename = filename.substring(5);
         }
         
         return new String[]{filename, entry};
     }
 
     protected LoadServiceResource tryResourceFromLoadPath( String namePlusSuffix,String loadPathEntry) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = loadPathEntry + "/" + namePlusSuffix;
                 boolean absolute = true;
                 // we check length == 0 for 'load', which does not use load path
                 if (!new File(reportedPath).isAbsolute()) {
                     absolute = false;
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) != '.') {
                         reportedPath = "./" + reportedPath;
                     }
                     loadPathEntry = JRubyFile.create(runtime.getCurrentDirectory(), loadPathEntry).getAbsolutePath();
                 }
                 JRubyFile actualPath = JRubyFile.create(loadPathEntry, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
                     debugLogTry("resourceFromLoadPath", "'" + actualPath.toString() + "' " + actualPath.isFile() + " " + actualPath.canRead());
                 }
                 if (actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath, absolute);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceAsIs(String namePlusSuffix) throws RaiseException {
         return tryResourceAsIs(namePlusSuffix, "resourceAsIs");
     }
 
     protected LoadServiceResource tryResourceAsIs(String namePlusSuffix, String debugName) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = namePlusSuffix;
                 File actualPath;
                 
                 if (new File(reportedPath).isAbsolute()) {
                     // it's an absolute path, use it as-is
                     actualPath = new File(RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     // replace leading ./ with current directory
                     if (reportedPath.charAt(0) == '.' && reportedPath.charAt(1) == '/') {
                         reportedPath = reportedPath.replaceFirst("\\./", runtime.getCurrentDirectory());
                     }
 
                     actualPath = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
                 
                 debugLogTry(debugName, actualPath.toString());
                 
                 if (reportedPath.contains("..") && runtime.is1_9()) {
                     // try to canonicalize if path contains ..
                     try {
                         actualPath = actualPath.getCanonicalFile();
                     } catch (IOException ioe) {
                     }
                 }
                 
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     protected LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
         // handle security-sensitive case
         if (Ruby.isSecurityRestricted() && classLoader == null) {
             classLoader = runtime.getInstanceConfig().getLoader();
         }
 
         // absolute classpath URI, no need to iterate over loadpaths
         if (name.startsWith("classpath:/")) {
             LoadServiceResource foundResource = getClassPathResource(classLoader, name);
             if (foundResource != null) {
                 return foundResource;
             }
         } else if (name.startsWith("classpath:")) {
             // "relative" classpath URI
             name = name.substring("classpath:".length());
         }
 
         for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             String entry = getLoadPathEntry(loadPath.eltInternal(i));
 
             // if entry is an empty string, skip it
             if (entry.length() == 0) continue;
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
 
             if (entry.startsWith("classpath:/")) {
                 entry = entry.substring("classpath:/".length());
             } else if (entry.startsWith("classpath:")) {
                 entry = entry.substring("classpath:".length());
             }
 
             String entryName;
             if (name.startsWith(entry)) {
                 entryName = name.substring(entry.length());
             } else {
                 entryName = name;
             }
 
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             LoadServiceResource foundResource = getClassPathResource(classLoader, entry + "/" + entryName);
             if (foundResource != null) {
                 return foundResource;
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
 
         // Try to load from classpath without prefix. "A/b.rb" will not load as
         // "./A/b.rb" in a jar file.
         LoadServiceResource foundResource = getClassPathResource(classLoader, name);
         if (foundResource != null) {
             return foundResource;
         }
 
         return null;
     }
 
     /* Directories and unavailable resources are not able to open a stream. */
     protected boolean isRequireable(URL loc) {
         if (loc != null) {
                 if (loc.getProtocol().equals("file") && new java.io.File(getPath(loc)).isDirectory()) {
                         return false;
                 }
 
                 try {
                 loc.openConnection();
                 return true;
             } catch (Exception e) {}
         }
         return false;
     }
 
     protected LoadServiceResource getClassPathResource(ClassLoader classLoader, String name) {
         boolean isClasspathScheme = false;
 
         // strip the classpath scheme first
         if (name.startsWith("classpath:/")) {
             isClasspathScheme = true;
             name = name.substring("classpath:/".length());
         } else if (name.startsWith("classpath:")) {
             isClasspathScheme = true;
             name = name.substring("classpath:".length());
         } else if(name.startsWith("file:") && name.indexOf("!/") != -1) {
             name = name.substring(name.indexOf("!/") + 2);
         }
 
         debugLogTry("fileInClasspath", name);
         URL loc = classLoader.getResource(name);
 
         if (loc != null) { // got it
             String path = "classpath:/" + name;
             // special case for typical jar:file URLs, but only if the name didn't have
             // the classpath scheme explicitly
             if (!isClasspathScheme &&
                     (loc.getProtocol().equals("jar") || loc.getProtocol().equals("file"))
                     && isRequireable(loc)) {
                 path = getPath(loc);
                 // On windows file: urls converted to names will return /C:/foo from
                 // getPath versus C:/foo.  Since getPath is used in a million places
                 // putting the newFile.getPath broke some file with-in Jar loading. 
                 // So I moved it to only this site.
                 if (Platform.IS_WINDOWS && loc.getProtocol().equals("file")) {
                     path = new File(path).getPath();
                 }
             }
             LoadServiceResource foundResource = new LoadServiceResource(loc, path);
             debugLogFound(foundResource);
             return foundResource;
         }
         return null;
     }
 
     private String expandRelativeJarPath(String path) {
         return path.replaceAll("/[^/]+/\\.\\.|[^/]+/\\.\\./|\\./","").replace("^\\\\","/");
     }
 
     protected String resolveLoadName(LoadServiceResource foundResource, String previousPath) {
         return previousPath;
     }
 
     protected String getFileName(JRubyFile file, String namePlusSuffix) {
         String s = namePlusSuffix;
         if(!namePlusSuffix.startsWith("./")) {
             s = "./" + s;
         }
         return s;
     }
 }
