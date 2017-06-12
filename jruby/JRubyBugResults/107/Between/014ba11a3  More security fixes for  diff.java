diff --git a/samples/applet.html b/samples/applet.html
index c56ba327bf..70670419b9 100644
--- a/samples/applet.html
+++ b/samples/applet.html
@@ -1,2 +1,6 @@
-<applet archive="jruby-complete.jar" code="org.jruby.JRubyApplet.class" width="500" height="300">
+<applet
+  code="org.jruby.demo.IRBApplet.class"
+  codebase="../lib/"
+  archive="jruby-complete.jar"
+  width="500" height="500">
 </applet>
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 0aea5d951c..f9ef0d843f 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1476,1108 +1476,1111 @@ public final class Ruby {
         if (profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError, ioError.getAllocator());
         }
         if (profile.allowClass("LocalJumpError")) {
             RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError, standardError.getAllocator());
         }
         // FIXME: Actually this somewhere <- fixed
         if (profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError, rangeError.getAllocator());
         }
         if (profile.allowClass("NativeException")) NativeException.createClass(this, runtimeError);
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
         if (!isSecurityRestricted()) {
             // Signal uses sun.misc.* classes, this is not allowed
             // in the security-sensitive environments
             if (profile.allowModule("Signal")) RubySignal.createSignal(this);
         }
         if (profile.allowClass("Continuation")) RubyContinuation.createContinuation(this);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrnoErrors() {
         createSysErr(IErrno.ENOTEMPTY, "ENOTEMPTY");
         createSysErr(IErrno.ERANGE, "ERANGE");
         createSysErr(IErrno.ESPIPE, "ESPIPE");
         createSysErr(IErrno.ENFILE, "ENFILE");
         createSysErr(IErrno.EXDEV, "EXDEV");
         createSysErr(IErrno.ENOMEM, "ENOMEM");
         createSysErr(IErrno.E2BIG, "E2BIG");
         createSysErr(IErrno.ENOENT, "ENOENT");
         createSysErr(IErrno.ENOSYS, "ENOSYS");
         createSysErr(IErrno.EDOM, "EDOM");
         createSysErr(IErrno.ENOSPC, "ENOSPC");
         createSysErr(IErrno.EINVAL, "EINVAL");
         createSysErr(IErrno.EEXIST, "EEXIST");
         createSysErr(IErrno.EAGAIN, "EAGAIN");
         createSysErr(IErrno.ENXIO, "ENXIO");
         createSysErr(IErrno.EILSEQ, "EILSEQ");
         createSysErr(IErrno.ENOLCK, "ENOLCK");
         createSysErr(IErrno.EPIPE, "EPIPE");
         createSysErr(IErrno.EFBIG, "EFBIG");
         createSysErr(IErrno.EISDIR, "EISDIR");
         createSysErr(IErrno.EBUSY, "EBUSY");
         createSysErr(IErrno.ECHILD, "ECHILD");
         createSysErr(IErrno.EIO, "EIO");
         createSysErr(IErrno.EPERM, "EPERM");
         createSysErr(IErrno.EDEADLOCK, "EDEADLOCK");
         createSysErr(IErrno.ENAMETOOLONG, "ENAMETOOLONG");
         createSysErr(IErrno.EMLINK, "EMLINK");
         createSysErr(IErrno.ENOTTY, "ENOTTY");
         createSysErr(IErrno.ENOTDIR, "ENOTDIR");
         createSysErr(IErrno.EFAULT, "EFAULT");
         createSysErr(IErrno.EBADF, "EBADF");
         createSysErr(IErrno.EINTR, "EINTR");
         createSysErr(IErrno.EWOULDBLOCK, "EWOULDBLOCK");
         createSysErr(IErrno.EDEADLK, "EDEADLK");
         createSysErr(IErrno.EROFS, "EROFS");
         createSysErr(IErrno.EMFILE, "EMFILE");
         createSysErr(IErrno.ENODEV, "ENODEV");
         createSysErr(IErrno.EACCES, "EACCES");
         createSysErr(IErrno.ENOEXEC, "ENOEXEC");
         createSysErr(IErrno.ESRCH, "ESRCH");
         createSysErr(IErrno.ECONNREFUSED, "ECONNREFUSED");
         createSysErr(IErrno.ECONNRESET, "ECONNRESET");
         createSysErr(IErrno.EADDRINUSE, "EADDRINUSE");
         createSysErr(IErrno.ECONNABORTED, "ECONNABORTED");
         createSysErr(IErrno.EPROTO, "EPROTO");
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             errnoModule.defineClassUnder(name, systemCallError, systemCallError.getAllocator()).defineConstant("Errno", newFixnum(i));
         }
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verbose;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug;
     }
 
     public JavaSupport getJavaSupport() {
         return javaSupport;
     }
 
     public JRubyClassLoader getJRubyClassLoader() {
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
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
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
 
         ThreadContext tc = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(tc, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (tc.getSourceFile() != null) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first(IRubyObject.NULL_ARRAY);
 
             if (mesg.isNil()) {
                 printErrorPos(errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == fastGetClass("RuntimeError") && (info == null || info.length() == 0)) {
             errorStream.print(": unhandled exception\n");
         } else {
             String path = type.getName();
 
             if (info.length() == 0) {
                 errorStream.print(": " + path + '\n');
             } else {
                 if (path.startsWith("#")) {
                     path = null;
                 }
 
                 String tail = null;
                 if (info.indexOf("\n") != -1) {
                     tail = info.substring(info.indexOf("\n") + 1);
                     info = info.substring(0, info.indexOf("\n"));
                 }
 
                 errorStream.print(": " + info);
 
                 if (path != null) {
                     errorStream.print(" (" + path + ")\n");
                 }
 
                 if (tail != null) {
                     errorStream.print(tail + '\n');
                 }
             }
         }
 
         excp.printBacktrace(errorStream);
     }
 
     private void printErrorPos(PrintStream errorStream) {
         ThreadContext tc = getCurrentContext();
         if (tc.getSourceFile() != null) {
             if (tc.getFrameName() != null) {
                 errorStream.print(tc.getPosition());
                 errorStream.print(":in '" + tc.getFrameName() + '\'');
             } else if (tc.getSourceLine() != 0) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceFile());
             }
         }
     }
     
     public void loadFile(String scriptName, InputStream in) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             }
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parseFile(in, scriptName, null);
             ASTInterpreter.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in) {
         // FIXME: what is this for?
 //        if (!Ruby.isSecurityRestricted()) {
 //            File f = new File(scriptName);
 //            if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
 //                scriptName = "./" + scriptName;
 //            };
 //        }
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
             
             Node scriptNode = parseFile(in, filename, null);
             
             Script script = tryCompile(scriptNode, new JRubyClassLoader(jrubyClassLoader));
             if (script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
             }
 
             runScript(script);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public class CallTraceFuncHook implements EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void event(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this);
 
                 context.preTrace();
                 try {
                     traceFunc.call(new IRubyObject[] {
                         newString(EVENT_NAMES[event]), // event name
                         newString(file), // filename
                         newFixnum(line + 1), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(int event) {
             return true;
         }
     };
     
     private final CallTraceFuncHook callTraceFuncHook = new CallTraceFuncHook();
     
     public void addEventHook(EventHook hook) {
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
     
     public void callEventHooks(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
         for (EventHook eventHook : eventHooks) {
             if (eventHook.isInterestedInEvent(event)) {
                 eventHook.event(context, event, file, line, name, type);
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
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             } 
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
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
 
     public RubyArray newArray(IRubyObject[] objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return RubyBoolean.newBoolean(this, value);
     }
 
     public RubyFileStat newFileStat(String filename, boolean lstat) {
         return RubyFileStat.newFileStat(this, filename, lstat);
     }
 
     public RubyFixnum newFixnum(long value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFloat newFloat(double value) {
         return RubyFloat.newFloat(this, value);
     }
 
     public RubyNumeric newNumeric() {
         return RubyNumeric.newNumeric(this);
     }
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
 
     public RubyBinding newBinding(Binding binding) {
         return RubyBinding.newBinding(this, binding);
     }
 
     public RubyString newString() {
         return RubyString.newString(this, "");
     }
 
     public RubyString newString(String string) {
         return RubyString.newString(this, string);
     }
     
     public RubyString newString(ByteList byteList) {
         return RubyString.newString(this, byteList);
     }
     
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
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         return symbolTable.fastGetSymbol(internedName);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(fastGetClass("RuntimeError"), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(fastGetClass("ArgumentError"), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(fastGetClass("ArgumentError"), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(
                 fastGetModule("Errno").fastGetClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newRaiseException(
                 fastGetModule("Errno").fastGetClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EISDIR"), "Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(fastGetModule("Errno").fastGetClass("EDOM"), "Domain error - " + message);
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(fastGetClass("IndexError"), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(fastGetClass("SecurityError"), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(fastGetClass("SystemCallError"), message);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(fastGetClass("TypeError"), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(fastGetClass("ThreadError"), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(fastGetClass("SyntaxError"), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(fastGetClass("RegexpError"), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(fastGetClass("RangeError"), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(fastGetClass("NotImplementedError"), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("InvalidEncoding"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, this.fastGetClass("NoMethodError"), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.fastGetClass("NameError"), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, fastGetClass("LocalJumpError"), message, reason, exitValue), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(fastGetClass("LoadError"), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(fastGetClass("TypeError"), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(fastGetClass("SystemStackError"), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(fastGetClass("IOError"), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(fastGetClass("StandardError"), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         return newRaiseException(fastGetClass("IOError"), ioe.getMessage());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(fastGetClass("TypeError"), "wrong argument type " +
                 receivedObject.getMetaClass().getRealClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(fastGetClass("EOFError"), "End of file reached");
     }
 
     public RaiseException newEOFError(String message) {
         return newRaiseException(fastGetClass("EOFError"), message);
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(fastGetClass("ZeroDivisionError"), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(fastGetClass("FloatDomainError"), message);
     }
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         return re;
     }
 
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
     }
 
     public void setStackTraces(int stackTraces) {
         this.stackTraces = stackTraces;
     }
 
     public int getStackTraces() {
         return stackTraces;
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
 
     public Hashtable<Integer, WeakReference<IOHandler>> getIoHandlers() {
         return ioHandlers;
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
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
-            jrubyHome = verifyHome(System.getProperty("jruby.home", System.getProperty("user.home") + "/.jruby"));
+            if (isSecurityRestricted()) {
+                return "SECURITY RESTRICTED";
+            }
+            jrubyHome = verifyHome(SafePropertyAccessor.getProperty("jruby.home", SafePropertyAccessor.getProperty("user.home") + "/.jruby"));
         }
         
         try {
             // This comment also in rbConfigLibrary
             // Our shell scripts pass in non-canonicalized paths, but even if we didn't
             // anyone who did would become unhappy because Ruby apps expect no relative
             // operators in the pathname (rubygems, for example).
             return new NormalizedFile(jrubyHome).getCanonicalPath();
         } catch (IOException e) {}
         
         return new NormalizedFile(jrubyHome).getAbsolutePath();
     }
     
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = System.getProperty("user.dir");
         }
         NormalizedFile f = new NormalizedFile(home);
         if (!f.isAbsolute()) {
             home = f.getAbsolutePath();
         }
         f.mkdirs();
         return home;
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
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
     
     private void defineGlobalVERBOSE() {
         // $VERBOSE can be true, false, or nil.  Any non-false-nil value will get stored as true
         getGlobalVariables().define("$VERBOSE", new IAccessor() {
             public IRubyObject getValue() {
                 return getVerbose();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 if (newValue.isNil()) {
                     setVerbose(newValue);
                 } else {
                     setVerbose(newBoolean(newValue != getFalse()));
                 }
 
                 return newValue;
             }
         });
     }
 
     private void defineGlobalDEBUG() {
         IAccessor d = new IAccessor() {
             public IRubyObject getValue() {
                 return getDebug();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 if (newValue.isNil()) {
                     setDebug(newValue);
                 } else {
                     setDebug(newBoolean(newValue != getFalse()));
                 }
 
                 return newValue;
             }
             };
         getGlobalVariables().define("$DEBUG", d);
         getGlobalVariables().define("$-d", d);
     }
     
     public void setRecordSeparatorVar(GlobalVariable recordSeparatorVar) {
         this.recordSeparatorVar = recordSeparatorVar;
     }
     
     public GlobalVariable getRecordSeparatorVar() {
         return recordSeparatorVar;
     }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 35762efd61..4fa8620fe8 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1227 +1,1231 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2007 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DirectoryAsFileException;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.IOHandler.InvalidValueException;
 import org.jruby.util.TypeConverter;
 
 /**
  * Ruby File class equivalent in java.
  **/
 public class RubyFile extends RubyIO {
     private static final long serialVersionUID = 1L;
     
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
 
     static final boolean IS_WINDOWS;
     static {
         String osname = System.getProperty("os.name");
         IS_WINDOWS = osname != null && osname.toLowerCase().indexOf("windows") != -1;
     }
 
     protected String path;
     private FileLock currentLock;
     
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     public RubyFile(Ruby runtime, String path) {
         this(runtime, path, open(runtime, path));
     }
     
     // use static function because constructor call must be first statement in above constructor
     private static InputStream open(Ruby runtime, String path) {
         try {
             return new FileInputStream(path);
         } catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
+        } catch (SecurityException se) {
+            throw runtime.newIOError(se.getMessage());
         }
     }
     
     // XXX This constructor is a hack to implement the __END__ syntax.
     //     Converting a reader back into an InputStream doesn't generally work.
     public RubyFile(Ruby runtime, String path, final Reader reader) {
         this(runtime, path, new InputStream() {
             public int read() throws IOException {
                 return reader.read();
             }
         });
     }
     
     public RubyFile(Ruby runtime, String path, InputStream in) {
         super(runtime, runtime.getFile());
         this.path = path;
         try {
             this.handler = new IOHandlerUnseekable(runtime, in, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         this.modes = handler.getModes();
         registerIOHandler(handler);
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         runtime.setFile(fileClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFile.class);   
         RubyString separator = runtime.newString("/");
         
         fileClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyFile;
                 }
             };
 
         separator.freeze();
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
         
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze();
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
         
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze();
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
         
         // TODO: These were missing, so we're not handling them elsewhere?
         // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
         // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
         fileClass.fastSetConstant("BINARY", runtime.newFixnum(IOModes.BINARY));
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         fileClass.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         fileClass.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         fileClass.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         fileClass.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         fileClass.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         fileClass.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         fileClass.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
         
         // TODO: These were missing, so we're not handling them elsewhere?
         constants.fastSetConstant("BINARY", runtime.newFixnum(32768));
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(1));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(8));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(4));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(2));
         
         // Create constants for open flags
         constants.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         constants.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         constants.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         constants.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         constants.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         constants.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         constants.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         constants.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         constants.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // TODO Singleton methods: readlink, umask 
         
         runtime.getFileTest().extend_object(fileClass);
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
         fileClass.dispatcher = callbackFactory.createDispatcher(fileClass);
         
         return fileClass;
     }
     
     public void openInternal(String newPath, IOModes newModes) {
         this.path = newPath;
         this.modes = newModes;
         
         try {
             if (newPath.equals("/dev/null")) {
                 handler = new IOHandlerNull(getRuntime(), newModes);
             } else {
                 handler = new IOHandlerSeekable(getRuntime(), newPath, newModes);
             }
             
             registerIOHandler(handler);
         } catch (InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (DirectoryAsFileException e) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileNotFoundException e) {
             // FNFException can be thrown in both cases, when the file
             // is not found, or when permission is denied.
-            if (new File(newPath).exists()) {
+            if (Ruby.isSecurityRestricted() || new File(newPath).exists()) {
                 throw getRuntime().newErrnoEACCESError(
                         "Permission denied - " + newPath);
             }
             throw getRuntime().newErrnoENOENTError(
                     "File not found - " + newPath);
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
-		}
+        } catch (SecurityException se) {
+            throw getRuntime().newIOError(se.getMessage());
+        }
     }
     
     @JRubyMethod
     public IRubyObject close() {
         // Make sure any existing lock is released before we try and close the file
         if (currentLock != null) {
             try {
                 currentLock.release();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         return super.close();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject flock(IRubyObject lockingConstant) {
         FileChannel fileChannel = handler.getFileChannel();
         int lockMode = RubyNumeric.num2int(lockingConstant);
 
         try {
             switch (lockMode) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
 
                         return getRuntime().newFixnum(0);
                     }
                     break;
                 case LOCK_EX:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.lock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_EX | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.tryLock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 default:
             }
         } catch (IOException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         } catch (java.nio.channels.OverlappingFileLockException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         }
 
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError(0, 1);
         }
         else if (args.length < 3) {
             IRubyObject fd = TypeConverter.convertToTypeWithCheck(args[0], getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         getRuntime().checkSafeString(args[0]);
         path = args[0].toString();
         modes = args.length > 1 ? getModes(getRuntime(), args[1]) : new IOModes(getRuntime(), IOModes.RDONLY);
 
         // One of the few places where handler may be null.
         // If handler is not null, it indicates that this object
         // is being reused.
         if (handler != null) {
             close();
         }
         openInternal(path, modes);
 
         if (block.isGiven()) {
             // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
         }
         return this;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().chmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(IRubyObject arg1, IRubyObject arg2) {
         int owner = (int) arg1.convertToInteger().getLongValue();
         int group = (int) arg2.convertToInteger().getLongValue();
         
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().chown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject atime() {
         return getRuntime().newFileStat(path, false).atime();
     }
 
     @JRubyMethod
     public IRubyObject ctime() {
         return getRuntime().newFileStat(path, false).ctime();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject lchmod(IRubyObject arg) {
         int mode = (int) arg.convertToInteger().getLongValue();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().lchmod(path, mode));
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject lchown(IRubyObject arg1, IRubyObject arg2) {
         int owner = (int) arg1.convertToInteger().getLongValue();
         int group = (int) arg2.convertToInteger().getLongValue();
         
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         return getRuntime().newFixnum(getRuntime().getPosix().lchown(path, owner, group));
     }
 
     @JRubyMethod
     public IRubyObject lstat() {
         return getRuntime().newFileStat(path, true);
     }
     
     @JRubyMethod
     public IRubyObject mtime() {
         return getLastModified(getRuntime(), path);
     }
 
     @JRubyMethod
     public RubyString path() {
         return getRuntime().newString(path);
     }
 
     @JRubyMethod
     public IRubyObject stat() {
         return getRuntime().newFileStat(path, false);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw getRuntime().newErrnoEINVALError("invalid argument: " + path);
         }
         try {
             handler.truncate(newLength.getLongValue());
         } catch (IOHandler.PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     public String toString() {
         return "RubyFile(" + path + ", " + modes + ", " + fileno + ")";
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
     private static IOModes getModes(Ruby runtime, IRubyObject object) {
         if (object instanceof RubyString) {
             return new IOModes(runtime, ((RubyString) object).toString());
         } else if (object instanceof RubyFixnum) {
             return new IOModes(runtime, ((RubyFixnum) object).getLongValue());
         }
 
         throw runtime.newTypeError("Invalid type for modes");
     }
 
     @JRubyMethod
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer();
         val.append("#<File:").append(path);
         if(!isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(IRubyObject recv, IRubyObject[] args) {
         Arity.checkArgumentCount(recv.getRuntime(), args, 1, 2);
         
         String name = RubyString.stringValue(args[0]).toString();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return recv.getRuntime().newString("").infectBy(args[0]);
                 case 3:
                     return recv.getRuntime().newString(name.substring(2)).infectBy(args[0]);
                 default:
                     switch (name.charAt(2)) {
                     case '/':
                     case '\\':
                         break;
                     default:
                         // strip c: away from relative-pathed name
                         name = name.substring(2);
                         break;
                     }
                     break;
                 }
             }
         }
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             name = name.substring(0, name.length() - 1);
         }
         
         // Paths which end in "/" or "\\" must be stripped off.
         int slashCount = 0;
         int length = name.length();
         for (int i = length - 1; i >= 0; i--) {
             char c = name.charAt(i);
             if (c != '/' && c != '\\') {
                 break;
             }
             slashCount++;
         }
         if (slashCount > 0 && length > 1) {
             name = name.substring(0, name.length() - slashCount);
         }
         
         int index = name.lastIndexOf('/');
         if (index == -1) {
             // XXX actually only on windows...
             index = name.lastIndexOf('\\');
         }
         
         if (!name.equals("/") && index != -1) {
             name = name.substring(index + 1);
         }
         
         if (args.length == 2) {
             String ext = RubyString.stringValue(args[1]).toString();
             if (".*".equals(ext)) {
                 index = name.lastIndexOf('.');
                 if (index > 0) {  // -1 no match; 0 it is dot file not extension
                     name = name.substring(0, index);
                 }
             } else if (name.endsWith(ext)) {
                 name = name.substring(0, name.length() - ext.length());
             }
         }
         return recv.getRuntime().newString(name).infectBy(args[0]);
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().chmod(filename.toString(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 3, -1);
         
         int count = 0;
         RubyInteger owner = args[0].convertToInteger();
         RubyInteger group = args[1].convertToInteger();
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().chown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         String jfilename = filename.toString();
         String name = jfilename.replace('\\', '/');
         boolean trimmedSlashes = false;
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (IS_WINDOWS && name.length() == 2 &&
                 isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
             // C:\ is returned unchanged (after slash trimming)
             if (trimmedSlashes) {
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
             if (index == -1) return recv.getRuntime().newString(".");
             if (index == 0) return recv.getRuntime().newString("/");
 
             // Include additional path separator (e.g. C:\myfile.txt becomes C:\, not C:)
             if (IS_WINDOWS && index == 2 && 
                     isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
                 index++;
             }
             
             result = jfilename.substring(0, index);
          }
 
          return recv.getRuntime().newString(result).infectBy(filename);
 
     }
 
     private static boolean isWindowsDriveLetter(char c) {
         return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
     
     /**
      * Returns the extension name of the file. An empty string is returned if 
      * the filename (not the entire path) starts or ends with a dot.
      * @param recv
      * @param arg Path to get extension name of
      * @return Extension, including the dot, or an empty string
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject extname(IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(recv, new IRubyObject[] { arg });
         String filename = RubyString.stringValue(baseFilename).toString();
         String result = "";
         
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0  && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return recv.getRuntime().newString(result);
     }
     
     /**
      * Converts a pathname to an absolute pathname. Relative paths are 
      * referenced from the current working directory of the process unless 
      * a second argument is given, in which case it will be used as the 
      * starting point. If the second argument is also relative, it will 
      * first be converted to an absolute pathname.
      * @param recv
      * @param args 
      * @return Resulting absolute path as a String
      */
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject expand_path(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, 2);
         
         String relativePath = RubyString.stringValue(args[0]).toString();
         String cwd = null;
         
         // Handle ~user paths 
         relativePath = expandUserPath(recv, relativePath);
         
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             
             String cwdArg = RubyString.stringValue(args[1]).toString();
             
             // Handle ~user paths.
             cwd = expandUserPath(recv, cwdArg);
             
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if ( cwd.charAt(0) != '/' ) {
                 cwd = JRubyFile.create(runtime.getCurrentDirectory(), cwd)
                     .getAbsolutePath();
             }
             
         } else {
             // If there's no second argument, simply use the working directory 
             // of the runtime.
             cwd = runtime.getCurrentDirectory();
         }
         
         // Something wrong we don't know the cwd...
         // TODO: Is this behavior really desirable? /mov
         if (cwd == null) return runtime.getNil();
         
         /* The counting of slashes that follows is simply a way to adhere to 
          * Ruby's UNC (or something) compatibility. When Ruby's expand_path is 
          * called with "//foo//bar" it will return "//foo/bar". JRuby uses 
          * java.io.File, and hence returns "/foo/bar". In order to retain 
          * java.io.File in the lower layers and provide full Ruby 
          * compatibility, the number of extra slashes must be counted and 
          * prepended to the result.
          */ 
         
         // Find out which string to check.
         String padSlashes = "";
         if (relativePath.length() > 0 && relativePath.charAt(0) == '/') {
             padSlashes = countSlashes(relativePath);
         } else if (cwd.length() > 0 && cwd.charAt(0) == '/') {
             padSlashes = countSlashes(cwd);
         }
         
         JRubyFile path;
         
         if (relativePath.length() == 0) {
             path = JRubyFile.create(relativePath, cwd);
         } else {
             path = JRubyFile.create(cwd, relativePath);
         }
         
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
     
     /**
      * This method checks a path, and if it starts with ~, then it expands 
      * the path to the absolute path of the user's home directory. If the 
      * string does not begin with ~, then the string is simply retuned 
      * unaltered.
      * @param recv
      * @param path Path to check
      * @return Expanded path
      */
     private static String expandUserPath( IRubyObject recv, String path ) {
         
         int pathLength = path.length();
 
         if (pathLength >= 1 && path.charAt(0) == '~') {
             // Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
             int userEnd = path.indexOf('/');
             
             if (userEnd == -1) {
                 if (pathLength == 1) {
                     // Single '~' as whole path to expand
                     path = RubyDir.getHomeDirectoryPath(recv).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
             
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 path = RubyDir.getHomeDirectoryPath(recv).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(recv, user);
                 
                 if (dir.isNil()) {
                     Ruby runtime = recv.getRuntime();
                     throw runtime.newArgumentError("user " + user + " does not exist");
                 }
                 
                 path = "" + dir +
                         (pathLength == userEnd ? "" : path.substring(userEnd));
             }
         }
         return path;
     }
     
     /**
      * Returns a string consisting of <code>n-1</code> slashes, where 
      * <code>n</code> is the number of slashes at the beginning of the input 
      * string.
      * @param stringToCheck
      * @return
      */
     private static String countSlashes( String stringToCheck ) {
         
         // Count number of extra slashes in the beginning of the string.
         int slashCount = 0;
         for (int i = 0; i < stringToCheck.length(); i++) {
             if (stringToCheck.charAt(i) == '/') {
                 slashCount++;
             } else {
                 break;
             }
         }
 
         // If there are N slashes, then we want N-1.
         if (slashCount > 0) {
             slashCount--;
         }
         
         // Prepare a string with the same number of redundant slashes so that 
         // we easily can prepend it to the result.
         byte[] slashes = new byte[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes); 
         
     }
 
     private static String canonicalize(String path) {
         return canonicalize(null, path);
     }
 
     private static String canonicalize(String canonicalPath, String remaining) {
 
         if (remaining == null) return canonicalPath;
 
         String child;
         int slash = remaining.indexOf('/');
         if (slash == -1) {
             child = remaining;
             remaining = null;
         } else {
             child = remaining.substring(0, slash);
             remaining = remaining.substring(slash + 1);
         }
 
         if (child.equals(".")) {
             // skip it
             if (canonicalPath != null && canonicalPath.length() == 0 ) canonicalPath += "/";
         } else if (child.equals("..")) {
             if (canonicalPath == null) throw new IllegalArgumentException("Cannot have .. at the start of an absolute path");
             int lastDir = canonicalPath.lastIndexOf('/');
             if (lastDir == -1) {
                 canonicalPath = "";
             } else {
                 canonicalPath = canonicalPath.substring(0, lastDir);
             }
         } else if (canonicalPath == null) {
             canonicalPath = child;
         } else {
             canonicalPath += "/" + child;
         }
 
         return canonicalize(canonicalPath, remaining);
     }
 
     /**
      * Returns true if path matches against pattern The pattern is not a regular expression;
      * instead it follows rules similar to shell filename globbing. It may contain the following
      * metacharacters:
      *   *:  Glob - match any sequence chars (re: .*).  If like begins with '.' then it doesn't.
      *   ?:  Matches a single char (re: .).
      *   [set]:  Matches a single char in a set (re: [...]).
      *
      */
     @JRubyMethod(name = {"fnmatch", "fnmatch?"}, required = 2, optional = 1, meta = true)
     public static IRubyObject fnmatch(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int flags;
         if (Arity.checkArgumentCount(runtime, args, 2, 3) == 3) {
             flags = RubyNumeric.num2int(args[2]);
         } else {
             flags = 0;
         }
         
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
         if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.realSize , path.bytes, path.begin, path.realSize, flags) == 0) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
     
     @JRubyMethod(name = "ftype", required = 1)
     public static IRubyObject ftype(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), true).ftype();
     }
 
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(IRubyObject recv, IRubyObject[] args) {
         boolean isTainted = false;
         StringBuffer buffer = new StringBuffer();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].toString();
             } else if (args[i] instanceof RubyArray) {
                 // Fixme: Need infinite recursion check to put [...] and not go into a loop
                 element = join(recv, ((RubyArray) args[i]).toJavaArray()).toString();
             } else {
                 element = args[i].convertToString().toString();
             }
             
             chomp(buffer);
             if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
                 buffer.append("/");
             }
             buffer.append(element);
         }
         
         RubyString fixedStr = RubyString.newString(recv.getRuntime(), buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     private static void chomp(StringBuffer buffer) {
         int lastIndex = buffer.length() - 1;
         
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
     
     @JRubyMethod(name = "lstat", required = 1, meta = true)
     public static IRubyObject lstat(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), true);
     }
 
     @JRubyMethod(name = "stat", required = 1, meta = true)
     public static IRubyObject stat(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false);
     }
 
     @JRubyMethod(name = "atime", required = 1, meta = true)
     public static IRubyObject atime(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false).atime();
     }
 
     @JRubyMethod(name = "ctime", required = 1, meta = true)
     public static IRubyObject ctime(IRubyObject recv, IRubyObject filename) {
         return recv.getRuntime().newFileStat(filename.convertToString().toString(), false).ctime();
     }
 
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject lchmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().lchmod(filename.toString(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject lchown(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 3, -1);
         
         int count = 0;
         RubyInteger owner = args[0].convertToInteger();
         RubyInteger group = args[1].convertToInteger();
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == runtime.getPosix().lchown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
 
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject link(IRubyObject recv, IRubyObject from, IRubyObject to) {
         if (recv.getRuntime().getPosix().link(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = "mtime", required = 1, meta = true)
     public static IRubyObject mtime(IRubyObject recv, IRubyObject filename) {
         return getLastModified(recv.getRuntime(), filename.convertToString().toString());
     }
     
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv, args, true, block);
     }
     
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, boolean tryToYield, Block block) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, -1);
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyFile file;
 
         if (args[0] instanceof RubyInteger) { // open with file descriptor
             file = new RubyFile(runtime, (RubyClass) recv);
             file.initialize(args, Block.NULL_BLOCK);            
         } else {
             RubyString pathString = RubyString.stringValue(args[0]);
             runtime.checkSafeString(pathString);
             String path = pathString.toString();
 
             IOModes modes = args.length >= 2 ? getModes(runtime, args[1]) : new IOModes(runtime, IOModes.RDONLY);
             file = new RubyFile(runtime, (RubyClass) recv);
 
             RubyInteger fileMode = args.length >= 3 ? args[2].convertToInteger() : null;
 
             file.openInternal(path, modes);
 
             if (fileMode != null) chmod(recv, new IRubyObject[] {fileMode, pathString});
         }
         
         if (tryToYield && block.isGiven()) {
             try {
                 return block.yield(tc, file);
             } finally {
                 file.close();
             }
         }
         
         return file;
     }
 
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = recv.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameString.toString());
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + 
                     " or " + newNameString);
         }
 
         JRubyFile dest = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
 
         if (oldFile.renameTo(dest)) {  // rename is successful
             return RubyFixnum.zero(runtime);
         }
 
         // rename via Java API call wasn't successful, let's try some tricks, similar to MRI 
 
         if (newFile.exists()) {
             recv.getRuntime().getPosix().chmod(newNameString.toString(), 0666);
             newFile.delete();
         }
 
         if (oldFile.renameTo(dest)) { // try to rename one more time
             return RubyFixnum.zero(runtime);
         }
 
         throw runtime.newErrnoEACCESError("Permission denied - " + oldNameString + " or " + 
                 newNameString);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return filename.getRuntime().newArray(dirname(recv, filename),
                 basename(recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(IRubyObject recv, IRubyObject from, IRubyObject to) {
         if (recv.getRuntime().getPosix().symlink(from.toString(),to.toString()) == -1) {
             // FIXME: When we get JNA3 we need to properly write this to errno.
             recv.getRuntime().newSystemCallError("bad symlink");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject truncate(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         RubyInteger newLength = arg2.convertToInteger(); 
         
         if (!new File(filename.getByteList().toString()).exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + filename.getByteList().toString());
         }
 
         if (newLength.getLongValue() < 0) {
             throw runtime.newErrnoEINVALError("invalid argument: " + filename);
         }
         
         IRubyObject[] args = new IRubyObject[] { filename, runtime.newString("r+") };
         RubyFile file = (RubyFile) open(recv, args, false, null);
         file.truncate(newLength);
         file.close();
         
         return RubyFixnum.zero(runtime);
     }
     
     /**
      * This method does NOT set atime, only mtime, since Java doesn't support anything else.
      */
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         // Ignore access_time argument since Java does not support it.
         
         long mtime;
         if (args[1] instanceof RubyTime) {
             mtime = ((RubyTime) args[1]).getJavaDate().getTime();
         } else if (args[1] instanceof RubyNumeric) {
             mtime = RubyNumeric.num2long(args[1]);
         } else {
             mtime = 0;
         }
         
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             fileToTouch.setLastModified(mtime);
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!lToDelete.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             if (!lToDelete.delete()) return runtime.getFalse();
         }
         
         return runtime.newFixnum(args.length);
     }
 
     // Fast path since JNA stat is about 10x slower than this
     private static IRubyObject getLastModified(Ruby runtime, String path) {
         long lastModified = JRubyFile.create(runtime.getCurrentDirectory(), path).lastModified();
         
         // 0 according to API does is non-existent file or IOError
         if (lastModified == 0L) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + path);
         }
         
         return runtime.newTime(lastModified);
     }
 }
diff --git a/src/org/jruby/RubyFileTest.java b/src/org/jruby/RubyFileTest.java
index 8d4b8010f5..ed7ec0a089 100644
--- a/src/org/jruby/RubyFileTest.java
+++ b/src/org/jruby/RubyFileTest.java
@@ -1,227 +1,230 @@
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
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 public class RubyFileTest {
     public static RubyModule createFileTestModule(Ruby runtime) {
         RubyModule fileTestModule = runtime.defineModule("FileTest");
         runtime.setFileTest(fileTestModule);
         
         fileTestModule.defineAnnotatedMethods(RubyFileTest.class);
         
         return fileTestModule;
     }
 
     @JRubyMethod(name = "blockdev?", required = 1, module = true)
     public static IRubyObject blockdev_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isBlockDev());
     }
     
     @JRubyMethod(name = "chardev?", required = 1, module = true)
     public static IRubyObject chardev_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isCharDev());
     }
 
     @JRubyMethod(name = "directory?", required = 1, module = true)
     public static IRubyObject directory_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isDirectory());
     }
     
     @JRubyMethod(name = "executable?", required = 1, module = true)
     public static IRubyObject executable_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isExecutable());
     }
     
     @JRubyMethod(name = "executable_real?", required = 1, module = true)
     public static IRubyObject executable_real_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isExecutableReal());
     }
     
     @JRubyMethod(name = {"exist?", "exists?"}, required = 1, module = true)
     public static IRubyObject exist_p(IRubyObject recv, IRubyObject filename) {
+        if (Ruby.isSecurityRestricted()) {
+            return recv.getRuntime().newBoolean(false);
+        }
         return recv.getRuntime().newBoolean(file(filename).exists());
     }
 
     @JRubyMethod(name = "file?", required = 1, module = true)
     public static RubyBoolean file_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
         
         return filename.getRuntime().newBoolean(file.exists() && file.isFile());
     }
 
     @JRubyMethod(name = "grpowned?", required = 1, module = true)
     public static IRubyObject grpowned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isGroupOwned());
     }
     
     @JRubyMethod(name = "identical?", required = 2, module = true)
     public static IRubyObject identical_p(IRubyObject recv, IRubyObject filename1, IRubyObject filename2) {
         throw recv.getRuntime().newNotImplementedError("FileTest#identical? not yet implemented");
     }
 
     @JRubyMethod(name = "owned?", required = 1, module = true)
     public static IRubyObject owned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isOwned());
     }
     
     @JRubyMethod(name = "pipe?", required = 1, module = true)
     public static IRubyObject pipe_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isNamedPipe());
     }
 
     // We use file test since it is faster than a stat; also euid == uid in Java always
     @JRubyMethod(name = {"readable?", "readable_real?"}, required = 1, module = true)
     public static IRubyObject readable_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         return recv.getRuntime().newBoolean(file.exists() && file.canRead());
     }
 
     // Not exposed by filetest, but so similiar in nature that it is stored here
     public static IRubyObject rowned_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isROwned());
     }
     
     @JRubyMethod(name = "setgid?", required = 1, module = true)
     public static IRubyObject setgid_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSetgid());
     }
     
     @JRubyMethod(name = "setuid?", required = 1, module = true)
     public static IRubyObject setuid_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSetuid());
     }
     
     @JRubyMethod(name = "size", required = 1, module = true)
     public static IRubyObject size(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         if (!file.exists()) noFileError(filename);
         
         return recv.getRuntime().newFixnum(file.length());
     }
     
     @JRubyMethod(name = "size?", required = 1, module = true)
     public static IRubyObject size_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
 
         if (!file.exists()) return recv.getRuntime().getNil();
         
         return recv.getRuntime().newFixnum(file.length());
     }
     
     @JRubyMethod(name = "socket?", required = 1, module = true)
     public static IRubyObject socket_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSocket());
     }
     
     @JRubyMethod(name = "sticky?", required = 1, module = true)
     public static IRubyObject sticky_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
         
         return runtime.newBoolean(file.exists() && runtime.getPosix().stat(file.getAbsolutePath()).isSticky());
     }
         
     @JRubyMethod(name = "symlink?", required = 1, module = true)
     public static RubyBoolean symlink_p(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         JRubyFile file = file(filename);
 
         return runtime.newBoolean(file.exists() && runtime.getPosix().lstat(file.getAbsolutePath()).isSymlink());
     }
 
     // We do both writable and writable_real through the same method because
     // in our java process effective and real userid will always be the same.
     @JRubyMethod(name = {"writable?", "writable_real?"}, required = 1, module = true)
     public static RubyBoolean writable_p(IRubyObject recv, IRubyObject filename) {
         return filename.getRuntime().newBoolean(file(filename).canWrite());
     }
     
     @JRubyMethod(name = "zero?", required = 1, module = true)
     public static RubyBoolean zero_p(IRubyObject recv, IRubyObject filename) {
         JRubyFile file = file(filename);
         
         return filename.getRuntime().newBoolean(file.exists() && file.length() == 0L);
     }
 
     private static JRubyFile file(IRubyObject path) {
         String filename = path.convertToString().toString();
         
         return JRubyFile.create(path.getRuntime().getCurrentDirectory(), filename);
     }
     
     private static void noFileError(IRubyObject filename) {
         throw filename.getRuntime().newErrnoENOENTError("No such file or directory - " + 
                 filename.convertToString());
     }
 }
diff --git a/src/org/jruby/ext/LateLoadingLibrary.java b/src/org/jruby/ext/LateLoadingLibrary.java
index edc60ffc82..1d2094089b 100644
--- a/src/org/jruby/ext/LateLoadingLibrary.java
+++ b/src/org/jruby/ext/LateLoadingLibrary.java
@@ -1,58 +1,61 @@
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
  * Copyright (C) 2007 JRuby Community
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
 package org.jruby.ext;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 public class LateLoadingLibrary implements Library {
     private String libraryName;
     private String className;
     private ClassLoader classLoader;
     
     public LateLoadingLibrary(String libraryName, String className, ClassLoader classLoader) {
         this.libraryName = libraryName;
         this.className = className;
         this.classLoader = classLoader;
     }
     
     public void load(Ruby runtime) throws IOException {
         try {
+            if (classLoader == null && Ruby.isSecurityRestricted()) {
+                classLoader = runtime.getInstanceConfig().getLoader();
+            }
             Class libraryClass = classLoader.loadClass(className);
             
             Library library = (Library)libraryClass.newInstance();
             
             library.load(runtime);
         } catch (Throwable e) {
             throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
         }
     }
 
 }
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index 141b90000c..08a9d5c9f4 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
@@ -1,261 +1,264 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import java.lang.ref.WeakReference;
 import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.util.WeakIdentityHashMap;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 public class JavaSupport {
     private final Ruby runtime;
 
     private final Map exceptionHandlers = new HashMap();
 
     private final Map instanceCache = Collections.synchronizedMap(new WeakIdentityHashMap(100));
     
     // There's not a compelling reason to keep JavaClass instances in a weak map
     // (any proxies created are [were] kept in a non-weak map, so in most cases they will
     // stick around anyway), and some good reasons not to (JavaClass creation is
     // expensive, for one; many lookups are performed when passing parameters to/from
     // methods; etc.).
     // TODO: faster custom concurrent map
     private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
         new ConcurrentHashMap<Class,JavaClass>(128);
     
     // FIXME: needs to be rethought
     private final Map matchCache = Collections.synchronizedMap(new HashMap(128));
 
     private Callback concreteProxyCallback;
 
     private RubyModule javaModule;
     private RubyModule javaUtilitiesModule;
     private RubyClass javaObjectClass;
     private RubyClass javaClassClass;
     private RubyClass javaArrayClass;
     private RubyClass javaProxyClass;
     private RubyModule javaInterfaceTemplate;
     private RubyModule packageModuleTemplate;
     private RubyClass arrayProxyClass;
     private RubyClass concreteProxyClass;
     
     
     public JavaSupport(Ruby ruby) {
         this.runtime = ruby;
     }
 
     final synchronized void setConcreteProxyCallback(Callback concreteProxyCallback) {
         if (this.concreteProxyCallback == null) {
             this.concreteProxyCallback = concreteProxyCallback;
         }
     }
     
     final Callback getConcreteProxyCallback() {
         return concreteProxyCallback;
     }
     
     final Map getMatchCache() {
         return matchCache;
     }
 
     
     public Class loadJavaClass(String className) {
+        if (Ruby.isSecurityRestricted() && className.startsWith("sun.misc.")) {
+            throw runtime.newNameError("security: cannot load class " + className, className);
+        }
         try {
             Class result = primitiveClass(className);
             if(result == null) {
                 return (Ruby.isSecurityRestricted()) ? Class.forName(className) :
                    Class.forName(className, true, runtime.getJRubyClassLoader());
             }
             return result;
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className);
         }
     }
 
     public JavaClass getJavaClassFromCache(Class clazz) {
         return javaClassCache.get(clazz);
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         javaClassCache.put(clazz.javaClass(), clazz);
     }
     
     public void defineExceptionHandler(String exceptionClass, RubyProc handler) {
         exceptionHandlers.put(exceptionClass, handler);
     }
 
     public void handleNativeException(Throwable exception) {
         if (exception instanceof RaiseException) {
             throw (RaiseException) exception;
         }
         Class excptnClass = exception.getClass();
         RubyProc handler = (RubyProc)exceptionHandlers.get(excptnClass.getName());
         while (handler == null &&
                excptnClass != Throwable.class) {
             excptnClass = excptnClass.getSuperclass();
         }
         if (handler != null) {
             handler.call(new IRubyObject[]{JavaUtil.convertJavaToRuby(runtime, exception)});
         } else {
             throw createRaiseException(exception);
         }
     }
 
     private RaiseException createRaiseException(Throwable exception) {
         RaiseException re = RaiseException.createNativeRaiseException(runtime, exception);
         
         return re;
     }
 
     private static Class primitiveClass(String name) {
         if (name.equals("long")) {
             return Long.TYPE;
         } else if (name.equals("int")) {
             return Integer.TYPE;
         } else if (name.equals("boolean")) {
             return Boolean.TYPE;
         } else if (name.equals("char")) {
             return Character.TYPE;
         } else if (name.equals("short")) {
             return Short.TYPE;
         } else if (name.equals("byte")) {
             return Byte.TYPE;
         } else if (name.equals("float")) {
             return Float.TYPE;
         } else if (name.equals("double")) {
             return Double.TYPE;
         }
         return null;
     }
 
     public JavaObject getJavaObjectFromCache(Object object) {
     	WeakReference ref = (WeakReference)instanceCache.get(object);
     	if (ref == null) {
     		return null;
     	}
     	JavaObject javaObject = (JavaObject) ref.get();
     	if (javaObject != null && javaObject.getValue() == object) {
     		return javaObject;
     	}
         return null;
     }
     
     public void putJavaObjectIntoCache(JavaObject object) {
     	instanceCache.put(object.getValue(), new WeakReference(object));
     }
 
     // not synchronizing these methods, no harm if these values get set twice...
     
     public RubyModule getJavaModule() {
         if (javaModule == null) {
             javaModule = runtime.fastGetModule("Java");
         }
         return javaModule;
     }
     
     public RubyModule getJavaUtilitiesModule() {
         if (javaUtilitiesModule == null) {
             javaUtilitiesModule = runtime.fastGetModule("JavaUtilities");
         }
         return javaUtilitiesModule;
     }
     
     public RubyClass getJavaObjectClass() {
         if (javaObjectClass == null) {
             javaObjectClass = getJavaModule().fastGetClass("JavaObject");
         }
         return javaObjectClass;
     }
 
     public RubyClass getJavaArrayClass() {
         if (javaArrayClass == null) {
             javaArrayClass = getJavaModule().fastGetClass("JavaArray");
         }
         return javaArrayClass;
     }
     
     public RubyClass getJavaClassClass() {
         if(javaClassClass == null) {
             javaClassClass = getJavaModule().fastGetClass("JavaClass");
         }
         return javaClassClass;
     }
     
     public RubyModule getJavaInterfaceTemplate() {
         if (javaInterfaceTemplate == null) {
             javaInterfaceTemplate = runtime.fastGetModule("JavaInterfaceTemplate");
         }
         return javaInterfaceTemplate;
     }
     
     public RubyModule getPackageModuleTemplate() {
         if (packageModuleTemplate == null) {
             packageModuleTemplate = runtime.fastGetModule("JavaPackageModuleTemplate");
         }
         return packageModuleTemplate;
     }
     
     public RubyClass getJavaProxyClass() {
         if (javaProxyClass == null) {
             javaProxyClass = runtime.fastGetClass("JavaProxy");
         }
         return javaProxyClass;
     }
     
     public RubyClass getConcreteProxyClass() {
         if (concreteProxyClass == null) {
             concreteProxyClass = runtime.fastGetClass("ConcreteJavaProxy");
         }
         return concreteProxyClass;
     }
     
     public RubyClass getArrayProxyClass() {
         if (arrayProxyClass == null) {
             arrayProxyClass = runtime.fastGetClass("ArrayJavaProxy");
         }
         return arrayProxyClass;
     }
 
 }
diff --git a/src/org/jruby/libraries/RbConfigLibrary.java b/src/org/jruby/libraries/RbConfigLibrary.java
index b2c9adafec..3844428d27 100644
--- a/src/org/jruby/libraries/RbConfigLibrary.java
+++ b/src/org/jruby/libraries/RbConfigLibrary.java
@@ -1,149 +1,154 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter
  * Copyright (C) 2006 Nick Sieger
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 import java.net.URL;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.NormalizedFile;
 
 public class RbConfigLibrary implements Library {
     /**
      * Just enough configuration settings (most don't make sense in Java) to run the rubytests
      * unit tests. The tests use <code>bindir</code>, <code>RUBY_INSTALL_NAME</code> and
      * <code>EXEEXT</code>.
      */
     public void load(Ruby runtime) {
         RubyModule configModule = runtime.defineModule("Config");
         RubyHash configHash = RubyHash.newHash(runtime);
         configModule.defineConstant("CONFIG", configHash);
         runtime.getObject().defineConstant("RbConfig", configModule);
 
         String[] versionParts = Constants.RUBY_VERSION.split("\\.");
         setConfig(configHash, "MAJOR", versionParts[0]);
         setConfig(configHash, "MINOR", versionParts[1]);
         setConfig(configHash, "TEENY", versionParts[2]);
         setConfig(configHash, "ruby_version", versionParts[0] + '.' + versionParts[1]);
         setConfig(configHash, "arch", System.getProperty("os.arch") + "-java" + System.getProperty("java.specification.version"));
 
-        String normalizedHome = new NormalizedFile(runtime.getJRubyHome()).getAbsolutePath();
+        String normalizedHome;
+        if (Ruby.isSecurityRestricted()) {
+            normalizedHome = "SECURITY RESTRICTED";
+        } else {
+            normalizedHome = new NormalizedFile(runtime.getJRubyHome()).getAbsolutePath();
+        }
         setConfig(configHash, "bindir", new NormalizedFile(normalizedHome, "bin").getAbsolutePath());
         setConfig(configHash, "RUBY_INSTALL_NAME", jruby_script());
         setConfig(configHash, "ruby_install_name", jruby_script());
         setConfig(configHash, "SHELL", jruby_shell());
         setConfig(configHash, "prefix", normalizedHome);
         setConfig(configHash, "exec_prefix", normalizedHome);
 
         setConfig(configHash, "host_os", System.getProperty("os.name"));
         setConfig(configHash, "host_vendor", System.getProperty("java.vendor"));
         setConfig(configHash, "host_cpu", System.getProperty("os.arch"));
         
         String target_os = System.getProperty("os.name");
         if (target_os.compareTo("Mac OS X") == 0) {
             target_os = "darwin";
         }
         setConfig(configHash, "target_os", target_os);
         
         setConfig(configHash, "target_cpu", System.getProperty("os.arch"));
         
         String jrubyJarFile = "jruby.jar";
         URL jrubyPropertiesUrl = Ruby.class.getClassLoader().getResource("jruby.properties");
         if (jrubyPropertiesUrl != null) {
             Pattern jarFile = Pattern.compile("jar:file:.*?([a-zA-Z0-9.\\-]+\\.jar)!/jruby.properties");
             Matcher jarMatcher = jarFile.matcher(jrubyPropertiesUrl.toString());
             jarMatcher.find();
             if (jarMatcher.matches()) {
                 jrubyJarFile = jarMatcher.group(1);
             }
         }
         setConfig(configHash, "LIBRUBY", jrubyJarFile);
         setConfig(configHash, "LIBRUBY_SO", jrubyJarFile);
         
         setConfig(configHash, "build", Constants.BUILD);
         setConfig(configHash, "target", Constants.TARGET);
         
         String libdir = System.getProperty("jruby.lib");
         if (libdir == null) {
             libdir = new NormalizedFile(normalizedHome, "lib").getAbsolutePath();
         } else {
             try {
             // Our shell scripts pass in non-canonicalized paths, but even if we didn't
             // anyone who did would become unhappy because Ruby apps expect no relative
             // operators in the pathname (rubygems, for example).
                 libdir = new NormalizedFile(libdir).getCanonicalPath();
             } catch (IOException e) {
                 libdir = new NormalizedFile(libdir).getAbsolutePath();
             }
         }
 
         setConfig(configHash, "libdir", libdir);
         setConfig(configHash, "rubylibdir",     new NormalizedFile(libdir, "ruby/1.8").getAbsolutePath());
         setConfig(configHash, "sitedir",        new NormalizedFile(libdir, "ruby/site_ruby").getAbsolutePath());
         setConfig(configHash, "sitelibdir",     new NormalizedFile(libdir, "ruby/site_ruby/1.8").getAbsolutePath());
         setConfig(configHash, "sitearchdir",    new NormalizedFile(libdir, "ruby/site_ruby/1.8/java").getAbsolutePath());
         setConfig(configHash, "archdir",    new NormalizedFile(libdir, "ruby/site_ruby/1.8/java").getAbsolutePath());
         setConfig(configHash, "configure_args", "");
         setConfig(configHash, "datadir", new NormalizedFile(normalizedHome, "share").getAbsolutePath());
         setConfig(configHash, "mandir", new NormalizedFile(normalizedHome, "man").getAbsolutePath());
         setConfig(configHash, "sysconfdir", new NormalizedFile(normalizedHome, "etc").getAbsolutePath());
         setConfig(configHash, "DLEXT", "jar");
 
         if (isWindows()) {
             setConfig(configHash, "EXEEXT", ".exe");
         } else {
             setConfig(configHash, "EXEEXT", "");
         }
     }
 
     private static void setConfig(RubyHash configHash, String key, String value) {
         Ruby runtime = configHash.getRuntime();
         configHash.op_aset(runtime.newString(key), runtime.newString(value));
     }
 
     private static boolean isWindows() {
         return System.getProperty("os.name", "").startsWith("Windows");
     }
 
     private String jruby_script() {
         return System.getProperty("jruby.script", isWindows() ? "jruby.bat" : "jruby").replace('\\', '/');
     }
 
     private String jruby_shell() {
         return System.getProperty("jruby.shell", isWindows() ? "cmd.exe" : "/bin/sh").replace('\\', '/');
     }
 }
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 3ed8f6fb1b..01e34fe637 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,627 +1,630 @@
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
 package org.jruby.runtime.load;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyHash;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.JRubyFile;
 
 /**
  * <b>How require works in JRuby</b>
  * When requiring a name from Ruby, JRuby will first remove any file extension it knows about,
  * thereby making it possible to use this string to see if JRuby has already loaded
  * the name in question. If a .rb extension is specified, JRuby will only try
  * those extensions when searching. If a .so, .o, .dll, or .jar extension is specified, JRuby
  * will only try .so or .jar when searching. Otherwise, JRuby goes through the known suffixes
  * (.rb, .rb.ast.ser, .so, and .jar) and tries to find a library with this name. The process for finding a library follows this order
  * for all searchable extensions:
  * <ol>
  * <li>First, check if the name starts with 'jar:', then the path points to a jar-file resource which is returned.</li>
  * <li>Second, try searching for the file in the current dir</li>
  * <li>Then JRuby looks through the load path trying these variants:
  *   <ol>
  *     <li>See if the current load path entry starts with 'jar:', if so check if this jar-file contains the name</li>
  *     <li>Otherwise JRuby tries to construct a path by combining the entry and the current working directy, and then see if 
  *         a file with the correct name can be reached from this point.</li>
  *   </ol>
  * </li>
  * <li>If all these fail, try to load the name as a resource from classloader resources, using the bare name as
  *     well as the load path entries</li>
  * <li>When we get to this state, the normal JRuby loading has failed. At this stage JRuby tries to load 
  *     Java native extensions, by following this process:
  *   <ol>
  *     <li>First it checks that we haven't already found a library. If we found a library of type JarredScript, the method continues.</li>
  *     <li>The first step is translating the name given into a valid Java Extension class name. First it splits the string into 
  *     each path segment, and then makes all but the last downcased. After this it takes the last entry, removes all underscores
  *     and capitalizes each part separated by underscores. It then joins everything together and tacks on a 'Service' at the end.
  *     Lastly, it removes all leading dots, to make it a valid Java FWCN.</li>
  *     <li>If the previous library was of type JarredScript, we try to add the jar-file to the classpath</li>
  *     <li>Now JRuby tries to instantiate the class with the name constructed. If this works, we return a ClassExtensionLibrary. Otherwise,
  *     the old library is put back in place, if there was one.
  *   </ol>
  * </li>
  * <li>When all separate methods have been tried and there was no result, a LoadError will be raised.</li>
  * <li>Otherwise, the name will be added to the loaded features, and the library loaded</li>
  * </ol>
  *
  * <b>How to make a class that can get required by JRuby</b>
  * <p>First, decide on what name should be used to require the extension.
  * In this purely hypothetical example, this name will be 'active_record/connection_adapters/jdbc_adapter'.
  * Then create the class name for this require-name, by looking at the guidelines above. Our class should
  * be named active_record.connection_adapters.JdbcAdapterService, and implement one of the library-interfaces.
  * The easiest one is BasicLibraryService, where you define the basicLoad-method, which will get called
  * when your library should be loaded.</p>
  * <p>The next step is to either put your compiled class on JRuby's classpath, or package the class/es inside a
  * jar-file. To package into a jar-file, we first create the file, then rename it to jdbc_adapter.jar. Then 
  * we put this jar-file in the directory active_record/connection_adapters somewhere in JRuby's load path. For
  * example, copying jdbc_adapter.jar into JRUBY_HOME/lib/ruby/site_ruby/1.8/active_record/connection_adapters
  * will make everything work. If you've packaged your extension inside a RubyGem, write a setub.rb-script that 
  * copies the jar-file to this place.</p>
  * <p>If you don't want to have the name of your extension-class to be prescribed, you can also put a file called
  * jruby-ext.properties in your jar-files META-INF directory, where you can use the key <full-extension-name>.impl
  * to make the extension library load the correct class. An example for the above would have a jruby-ext.properties
  * that contained a ruby like: "active_record/connection_adapters/jdbc_adapter=org.jruby.ar.JdbcAdapter". (NOTE: THIS
  * FEATURE IS NOT IMPLEMENTED YET.)</p>
  *
  * @author jpetersen
  */
 public class LoadService {
     protected static final String JRUBY_BUILTIN_SUFFIX = ".rb";
 
     protected static final String[] sourceSuffixes = { ".class", ".rb" };
     protected static final String[] extensionSuffixes = { ".so", ".jar" };
     protected static final String[] allSuffixes = { ".class", ".rb", ".so", ".jar" };
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|jar)$");
 
     protected final RubyArray loadPath;
     protected final RubyArray loadedFeatures;
     protected final Set loadedFeaturesInternal = Collections.synchronizedSet(new HashSet());
     protected final Set firstLineLoadedFeatures = Collections.synchronizedSet(new HashSet());
     protected final Map builtinLibraries = new HashMap();
 
     protected final Map jarFiles = new HashMap();
 
     protected final Map autoloadMap = new HashMap();
 
     protected final Ruby runtime;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);    
     }
 
     public void init(List additionalDirectories) {
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // add $RUBYLIB paths
        RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
        RubyString env_rubylib = runtime.newString("RUBYLIB");
        if (env.has_key_p(env_rubylib).isTrue()) {
            String rubylib = env.op_aref(env_rubylib).toString();
            String[] paths = rubylib.split(File.pathSeparator);
            for (int i = 0; i < paths.length; i++) {
                addPath(paths[i]);
            }
        }
 
         // wrap in try/catch for security exceptions in an applet
         if (!Ruby.isSecurityRestricted()) {
           String jrubyHome = runtime.getJRubyHome();
           if (jrubyHome != null) {
               char sep = '/';
               String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
               addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + "site_ruby");
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
               addPath(rubyDir + Constants.RUBY_MAJOR_VERSION + sep + "java");
 
               // Added to make sure we find default distribution files within jar file.
               // TODO: Either make jrubyHome become the jar file or allow "classpath-only" paths
               addPath("lib" + sep + "ruby" + sep + Constants.RUBY_MAJOR_VERSION);
           }
         }
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     private void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
         
         synchronized(loadPath) {
             loadPath.add(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         Library library = null;
         
         library = findLibrary(file, false);
 
         if (library == null) {
             library = findLibraryWithClassloaders(file);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime);
         } catch (IOException e) {
             e.printStackTrace();
             throw runtime.newLoadError("IO error -- " + file);
         }
     }
 
     public boolean smartLoad(String file) {
         if (file.equals("")) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
         if(firstLineLoadedFeatures.contains(file)) {
             return false;
         }
         Library library = null;
         String loadName = file;
         String[] extensionsToSearch = null;
         
         // if an extension is specified, try more targetted searches
         if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
             Matcher matcher = null;
             if ((matcher = sourcePattern.matcher(file)).find()) {
                 // source extensions
                 extensionsToSearch = sourceSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else if ((matcher = extensionPattern.matcher(file)).find()) {
                 // extension extensions
                 extensionsToSearch = extensionSuffixes;
                 
                 // trim extension to try other options
                 file = file.substring(0,matcher.start());
             } else {
                 // unknown extension, fall back to search with extensions
                 extensionsToSearch = allSuffixes;
             }
         } else {
             // try all extensions
             extensionsToSearch = allSuffixes;
         }
         
         // First try suffixes with normal loading
         for (int i = 0; i < extensionsToSearch.length; i++) {
             if (Ruby.isSecurityRestricted()) {
                 // search in CWD only in if no security restrictions
                 library = findLibrary(file + extensionsToSearch[i], false);
             } else {
                 library = findLibrary(file + extensionsToSearch[i], true);
             }
 
             if (library != null) {
                 loadName = file + extensionsToSearch[i];
                 break;
             }
         }
 
         // Then try suffixes with classloader loading
         if (library == null) {
             for (int i = 0; i < extensionsToSearch.length; i++) {
                 library = findLibraryWithClassloaders(file + extensionsToSearch[i]);
                 if (library != null) {
                     loadName = file + extensionsToSearch[i];
                     break;
                 }
             }
         }
 
         library = tryLoadExtension(library,file);
 
         // no library or extension found, try to load directly as a class
         Script script = null;
         if (library == null) {
             String className = file;
             if (file.lastIndexOf(".") != -1) className = file.substring(0, file.lastIndexOf("."));
             className = className.replace('-', '_').replace('.', '_');
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 &&
                     lastSlashIndex < className.length() - 1 &&
                     !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script)scriptClass.newInstance();
             } catch (Exception cnfe) {
                 throw runtime.newLoadError("no such file to load -- " + file);
             }
         }
         
         if (loadedFeaturesInternal.contains(loadName) || loadedFeatures.include_p(runtime.newString(loadName)).isTrue()) {
             return false;
         }
         
         // attempt to load the found library
         try {
             loadedFeaturesInternal.add(loadName);
             firstLineLoadedFeatures.add(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.append(runtime.newString(loadName));
             }
 
             if (script != null) {
                 runtime.loadScript(script);
                 return true;
             }
             
             library.load(runtime);
             return true;
         } catch (Throwable e) {
             if(library instanceof JarredScript && file.endsWith(".jar")) {
                 return true;
             }
 
             loadedFeaturesInternal.remove(loadName);
             firstLineLoadedFeatures.remove(file);
             synchronized(loadedFeatures) {
                 loadedFeatures.remove(runtime.newString(loadName));
             }
             if (e instanceof RaiseException) throw (RaiseException) e;
 
             if(runtime.getDebug().isTrue()) e.printStackTrace();
 e.printStackTrace();
             RaiseException re = runtime.newLoadError("IO error -- " + file);
             re.initCause(e);
             throw re;
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
         return smartLoad(file);
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return (IAutoloadMethod)autoloadMap.get(name);
     }
     
     public void removeAutoLoadFor(String name) {
         autoloadMap.remove(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = (IAutoloadMethod)autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void registerBuiltin(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void registerRubyBuiltin(String libraryName) {
         registerBuiltin(libraryName + JRUBY_BUILTIN_SUFFIX, new BuiltinScript(libraryName));
     }
 
     private Library findLibrary(String file, boolean checkCWD) {
         if (builtinLibraries.containsKey(file)) {
             return (Library) builtinLibraries.get(file);
         }
         
         LoadServiceResource resource = findFile(file, checkCWD);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     private Library findLibraryWithClassloaders(String file) {
         LoadServiceResource resource = findFileInClasspath(file);
         if (resource == null) {
             return null;
         }
 
         if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFile(String name, boolean checkCWD) {
         // if a jar URL, return load service resource directly without further searching
         if (name.startsWith("jar:")) {
             try {
                 return new LoadServiceResource(new URL(name), name);
             } catch (MalformedURLException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         } else if(name.startsWith("file:") && name.indexOf("!/") != -1) {
             try {
                 JarFile file = new JarFile(name.substring(5, name.indexOf("!/")));
                 String filename = name.substring(name.indexOf("!/") + 2);
                 if(file.getJarEntry(filename) != null) {
                     return new LoadServiceResource(new URL("jar:" + name), name);
                 }
             } catch(Exception e) {}
         }
 
         if (checkCWD) {
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name);
                 if (file.isFile() && file.isAbsolute()) {
                     try {
                         return new LoadServiceResource(file.toURI().toURL(), name);
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
 
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             // TODO this is really ineffient, ant potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             String entry = ((IRubyObject)pathIter.next()).toString();
             if (entry.startsWith("jar:") || (entry.startsWith("file:") && entry.indexOf("!/") != -1)) {
                 JarFile current = (JarFile)jarFiles.get(entry);
                 String after = entry.startsWith("file:") ? entry.substring(entry.indexOf("!/") + 2) + "/" : "";
                 String before = entry.startsWith("file:") ? entry.substring(0, entry.indexOf("!/")) : entry;
 
                 if(null == current) {
                     try {
                         if(entry.startsWith("jar:")) {
                             current = new JarFile(entry.substring(4));
                         } else {
                             current = new JarFile(entry.substring(5,entry.indexOf("!/")));
                         }
                         jarFiles.put(entry,current);
                     } catch (FileNotFoundException ignored) {
                     } catch (IOException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
                 String canonicalEntry = after+name;
                 if(after.length()>0) {
                     try {
                         canonicalEntry = new File(after+name).getCanonicalPath().substring(new File(".").getCanonicalPath().length()+1);
                     } catch(Exception e) {}
                 }
                 if (current.getJarEntry(canonicalEntry) != null) {
                     try {
                         if(entry.startsWith("file:")) {
                             return new LoadServiceResource(new URL("jar:" + before + "!/" + canonicalEntry), entry + "/" + name);
                         } else {
                             return new LoadServiceResource(new URL("jar:file:" + entry.substring(4) + "!/" + name), entry + name);
                         }
                     } catch (MalformedURLException e) {
                         throw runtime.newIOErrorFromException(e);
                     }
                 }
             } 
             try {
-                JRubyFile current = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(),entry).getAbsolutePath(), name);
-                if (current.isFile()) {
-                    try {
-                        return new LoadServiceResource(current.toURI().toURL(), current.getPath());
-                    } catch (MalformedURLException e) {
-                        throw runtime.newIOErrorFromException(e);
+                if (!Ruby.isSecurityRestricted()) {
+                    JRubyFile current = JRubyFile.create(JRubyFile.create(
+                            runtime.getCurrentDirectory(),entry).getAbsolutePath(), name);
+                    if (current.isFile()) {
+                        try {
+                            return new LoadServiceResource(current.toURI().toURL(), current.getPath());
+                        } catch (MalformedURLException e) {
+                            throw runtime.newIOErrorFromException(e);
+                        }
                     }
                 }
             } catch (SecurityException secEx) { }
         }
 
         return null;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     private LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
         // handle security-sensitive case
         if (Ruby.isSecurityRestricted() && classLoader == null) {
             classLoader = runtime.getInstanceConfig().getLoader();
         }
 
         for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
             String entry = pathIter.next().toString();
 
             // if entry starts with a slash, skip it since classloader resources never start with a /
             if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
             
             // otherwise, try to load from classpath (Note: Jar resources always uses '/')
             URL loc = classLoader.getResource(entry + "/" + name);
 
             // Make sure this is not a directory or unavailable in some way
             if (isRequireable(loc)) {
                 return new LoadServiceResource(loc, loc.getPath());
             }
         }
 
         // if name starts with a / we're done (classloader resources won't load with an initial /)
         if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
         
         // Try to load from classpath without prefix. "A/b.rb" will not load as 
         // "./A/b.rb" in a jar file.
         URL loc = classLoader.getResource(name);
 
         return isRequireable(loc) ? new LoadServiceResource(loc, loc.getPath()) : null;
     }
 
     private Library tryLoadExtension(Library library, String file) {
         // This code exploits the fact that all .jar files will be found for the JarredScript feature.
         // This is where the basic extension mechanism gets fixed
         Library oldLibrary = library;
         
         if((library == null || library instanceof JarredScript) && !file.equalsIgnoreCase("")) {
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = file.split("/");
 
             StringBuffer finName = new StringBuffer();
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
                 if(library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     runtime.getJRubyClassLoader().addURL(((JarredScript)library).getResource().getURL());
                 }
 
                 Class theClass = runtime.getJavaSupport().loadJavaClass(className);
                 library = new ClassExtensionLibrary(theClass);
             } catch(Exception ee) {
                 library = null;
                 runtime.getGlobalVariables().set("$!", runtime.getNil());
             }
         }
         
         // If there was a good library before, we go back to that
         if(library == null && oldLibrary != null) {
             library = oldLibrary;
         }
         return library;
     }
     
     /* Directories and unavailable resources are not able to open a stream. */
     private boolean isRequireable(URL loc) {
         if (loc != null) {
         	if (loc.getProtocol().equals("file") && new java.io.File(loc.getFile()).isDirectory()) {
         		return false;
         	}
         	
         	try {
                 loc.openConnection();
                 return true;
             } catch (Exception e) {}
         }
         return false;
     }
 }
diff --git a/src/org/jruby/util/JRubyFile.java b/src/org/jruby/util/JRubyFile.java
index 82311e8776..5a3c8a42d5 100644
--- a/src/org/jruby/util/JRubyFile.java
+++ b/src/org/jruby/util/JRubyFile.java
@@ -1,190 +1,192 @@
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
 /**
  * $Id$
  */
 package org.jruby.util;
 
 import java.io.File;
 import java.io.FileFilter;
 import java.io.FilenameFilter;
 import java.io.IOException;
 
+import org.jruby.Ruby;
+
 /**
  * <p>This file acts as an alternative to NormalizedFile, due to the problems with current working 
  * directory.</p>
  *
  */
 public class JRubyFile extends File {
     private static final long serialVersionUID = 435364547567567L;
 
     public static JRubyFile create(String cwd, String pathname) {
-        if (pathname == null || pathname.equals("")) {
+        if (pathname == null || pathname.equals("") || Ruby.isSecurityRestricted()) {
             return JRubyNonExistentFile.NOT_EXIST;
         }
         File internal = new File(pathname);
         if(!internal.isAbsolute()) {
             internal = new File(cwd,pathname);
             if(!internal.isAbsolute()) {
                 throw new IllegalArgumentException("Neither current working directory ("+cwd+") nor pathname ("+pathname+") led to an absolute path");
             }
         }
         return new JRubyFile(internal);
     }
 
     public static String getFileProperty(String property) {
         String value = SafePropertyAccessor.getProperty(property, "/");
         
         return value.replace(File.separatorChar, '/');
     }
 
     private JRubyFile(File file) {
         this(file.getAbsolutePath());
     }
 
     protected JRubyFile(String filename) {
         super(filename);
     }
 
     public String getAbsolutePath() {
         return new File(super.getPath()).getAbsolutePath().replace(File.separatorChar, '/'); 
     }
 
     public String getCanonicalPath() throws IOException {
         String canonicalPath = super.getCanonicalPath().replace(File.separatorChar, '/');
         
         // Java 1.4 canonicalPath does not strip off '.'
         if (canonicalPath.endsWith("/.")) {
             canonicalPath = canonicalPath.substring(0, canonicalPath.length() - 1);
         }
         
         return canonicalPath;
     }
 
     public String getPath() {
         return super.getPath().replace(File.separatorChar, '/');
     }
 
     public String toString() {
         return super.toString().replace(File.separatorChar, '/');
     }
 
     public File getAbsoluteFile() {
         return new JRubyFile(getAbsolutePath());
     }
 
     public File getCanonicalFile() throws IOException {
         return new JRubyFile(getCanonicalPath());
     }
 
     public String getParent() {
         String par = super.getParent();
         if (par != null) {
             par = par.replace(File.separatorChar, '/');
         }
         return par;
     }
 
     public File getParentFile() {
         String par = getParent();
         if (par == null) {
             return this;
         } else {
             return new JRubyFile(par);
         }
     }
     
     public static File[] listRoots() {
         File[] roots = File.listRoots();
         JRubyFile[] smartRoots = new JRubyFile[roots.length];
         for(int i = 0, j = roots.length; i < j; i++) {
             smartRoots[i] = new JRubyFile(roots[i].getPath());
         }
         return smartRoots;
     }
     
     public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix,directory));
     }
     
     public static File createTempFile(String prefix, String suffix) throws IOException {
         return new JRubyFile(File.createTempFile(prefix, suffix));
     }
 
     public String[] list(FilenameFilter filter) {
         String[] files = super.list(filter);
         if (files == null) {
             return null;
         }
         
         String[] smartFiles = new String[files.length];
         for (int i = 0; i < files.length; i++) {
             smartFiles[i] = files[i].replace(File.separatorChar, '/');
         }
         return smartFiles;
     }
 
     public File[] listFiles() {
         File[] files = super.listFiles();
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0, j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FileFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 
     public File[] listFiles(final FilenameFilter filter) {
         final File[] files = super.listFiles(filter);
         if (files == null) {
             return null;
         }
         
         JRubyFile[] smartFiles = new JRubyFile[files.length];
         for (int i = 0,j = files.length; i < j; i++) {
             smartFiles[i] = create(super.getAbsolutePath(),files[i].getPath());
         }
         return smartFiles;
     }
 }
diff --git a/src/org/jruby/util/JRubyNonExistentFile.java b/src/org/jruby/util/JRubyNonExistentFile.java
index 303a3f1f1a..c78e8683f0 100644
--- a/src/org/jruby/util/JRubyNonExistentFile.java
+++ b/src/org/jruby/util/JRubyNonExistentFile.java
@@ -1,105 +1,113 @@
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
 
 package org.jruby.util;
 
 import java.io.File;
 import java.io.FileFilter;
 import java.io.FileNotFoundException;
 import java.io.FilenameFilter;
 import java.io.IOException;
 
 /**
  * @author nicksieger
  */
 public class JRubyNonExistentFile extends JRubyFile {
     static JRubyNonExistentFile NOT_EXIST = new JRubyNonExistentFile();
     private JRubyNonExistentFile() {
         super("");
     }
 
     public String getAbsolutePath() {
         return "";
     }
+    
+    public boolean isDirectory() {
+        return false;
+    }
+    
+    public boolean exists() {
+        return false;
+    }
 
     public String getCanonicalPath() throws IOException {
         throw new FileNotFoundException("File does not exist");
     }
 
     public String getPath() {
         return "";
     }
 
     public String toString() {
         return "";
     }
 
     public File getAbsoluteFile() {
         return this;
     }
 
     public File getCanonicalFile() throws IOException {
         throw new FileNotFoundException("File does not exist");
     }
 
     public String getParent() {
         return "";
     }
 
     public File getParentFile() {
         return this;
     }
 
     public static File[] listRoots() {
         return new File[0];
     }
 
     public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
         return createTempFile(prefix, suffix);
     }
 
     public static File createTempFile(String prefix, String suffix) throws IOException {
         throw new FileNotFoundException("File does not exist");
     }
 
     public String[] list(FilenameFilter filter) {
         return new String[0];
     }
 
     public File[] listFiles() {
         return new File[0];
     }
 
     public File[] listFiles(final FileFilter filter) {
         return new File[0];
     }
 
     public File[] listFiles(final FilenameFilter filter) {
         return new File[0];
     }
 }
\ No newline at end of file
