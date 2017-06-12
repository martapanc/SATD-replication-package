diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 85401a1157..00106af37e 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -774,1089 +774,1087 @@ public final class Ruby {
         RubyObject.createObjectClass(this, objectMetaClass);
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = RubyClass.createBootstrapMetaClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR, objectClass);
         objectClass.setConstant("Module", moduleClass);
         RubyClass classClass = RubyClass.newClassClass(this, moduleClass);
         objectClass.setConstant("Class", classClass);
         
         classClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
 
         // I don't think the containment is correct here (parent cref)
         RubyClass metaClass = objectClass.makeMetaClass(classClass, objectMetaClass);
         metaClass = moduleClass.makeMetaClass(metaClass, objectMetaClass);
         metaClass = classClass.makeMetaClass(metaClass, objectMetaClass);
 
         RubyModule.createModuleClass(this, moduleClass);
 
         kernelModule = RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         RubyClass.createClassClass(classClass);
 
         nilClass = RubyNil.createNilClass(this);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         
         nilObject = new RubyNil(this);
         trueObject = new RubyBoolean(this, true);
         falseObject = new RubyBoolean(this, false);
         
         RubyComparable.createComparable(this);
         enumerableModule = RubyEnumerable.createEnumerableModule(this);
         stringClass = RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) fixnumClass = RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) hashClass = RubyHash.createHashClass(this);
         
         RubyIO.createIOClass(this);
 
         if (profile.allowClass("Array")) arrayClass = RubyArray.createArrayClass(this);
 
         RubyClass structClass = null;
         if (profile.allowClass("Struct")) structClass = RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
         if (profile.allowClass("Binding")) RubyBinding.createBindingClass(this);
         // Math depends on all numeric types
         if (profile.allowModule("Math")) RubyMath.createMathModule(this); 
         if (profile.allowClass("Regexp")) RubyRegexp.createRegexpClass(this);
         if (profile.allowClass("Range")) RubyRange.createRangeClass(this);
         if (profile.allowModule("ObjectSpace")) RubyObjectSpace.createObjectSpaceModule(this);
         if (profile.allowModule("GC")) RubyGC.createGCModule(this);
         if (profile.allowClass("Proc")) RubyProc.createProcClass(this);
         if (profile.allowClass("Method")) RubyMethod.createMethodClass(this);
         if (profile.allowClass("MatchData")) RubyMatchData.createMatchDataClass(this);
         if (profile.allowModule("Marshal")) RubyMarshal.createMarshalModule(this);
         if (profile.allowClass("Dir")) RubyDir.createDirClass(this);
         if (profile.allowModule("FileTest")) RubyFileTest.createFileTestModule(this);
         // depends on IO, FileTest
         if (profile.allowClass("File")) RubyFile.createFileClass(this);
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
         RubyClass signalException = null;
         
         RubyClass rangeError = null;
         if (profile.allowClass("StandardError")) {
             standardError = defineClass("StandardError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if (profile.allowClass("NoMethodError")) {
             RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }        
         if (profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemExit")) {
             RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("SignalException")) {
             signalException = defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Interrupt")) {
             defineClass("Interrupt", signalException, signalException.getAllocator());
         }
         if (profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError, standardError.getAllocator());
         }
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
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
         if (profile.allowModule("Signal")) RubySignal.createSignal(this);
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
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null)
             jrubyClassLoader = new JRubyClassLoader(Thread.currentThread().getContextClassLoader());
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
     
     public Node parseFile(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, new StringReader(content), scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, new StringReader(content), scope, 
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
 
     public RegexpFactory getRegexpFactory() {
         return this.regexpFactory;
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
                 throw newTypeError("" + str + " does not refer to class/module");
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
 
         if (type == getClass("RuntimeError") && (info == null || info.length() == 0)) {
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
 
     /** This method compiles and interprets a Ruby script.
      *
      *  It can be used if you want to use JRuby as a Macro language.
      *
      */
     public void loadFile(String scriptName, Reader source) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             };
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parseFile(source, scriptName, null);
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
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
 
             script.run(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
                         name != null ? RubySymbol.newSymbol(Ruby.this, name) : getNil(),
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
     }
     
     public void removeEventHook(EventHook hook) {
         eventHooks.remove(hook);
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
         for (int i = 0; i < eventHooks.size(); i++) {
             EventHook eventHook = (EventHook)eventHooks.get(i);
             if (eventHook.isInterestedInEvent(event)) {
                 eventHook.event(context, event, file, line, name, type);
             }
         }
     }
     
     public boolean hasEventHooks() {
         return !eventHooks.isEmpty();
     }
     
     public GlobalVariables getGlobalVariables() {
         return globalVariables;
     }
 
     // For JSR 223 support: see http://scripting.java.net/
     public void setGlobalVariables(GlobalVariables globalVariables) {
         this.globalVariables = globalVariables;
     }
 
     public CallbackFactory callbackFactory(Class type) {
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
     
     public void addFinalizer(Finalizable finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap();
             }
         }
         
         synchronized (finalizers) {
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeFinalizer(Finalizable finalizer) {
         if (finalizers != null) {
             synchronized (finalizers) {
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
             RubyProc proc = (RubyProc) atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator finalIter = new ArrayList(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     ((Finalizable) finalIter.next()).finalize();
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
     
     public RubyArray newArray(List list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return RubyBoolean.newBoolean(this, value);
     }
 
     public RubyFileStat newRubyFileStat(String file) {
         return (RubyFileStat)getClass("File").getClass("Stat").callMethod(getCurrentContext(),"new",newString(file));
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
 
     public RubyProc newProc(boolean isLambda, Block block) {
         if (!isLambda && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, isLambda);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
 
     public RubyBinding newBinding(Block block) {
         return RubyBinding.newBinding(this, block);
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
 
     public RubySymbol newSymbol(String string) {
         return RubySymbol.newSymbol(this, string);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(getClass("RuntimeError"), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(getClass("ArgumentError"), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(getClass("ArgumentError"), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getModule("Errno").getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getModule("Errno").getClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getModule("Errno").getClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getModule("Errno").getClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getModule("Errno").getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getModule("Errno").getClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getModule("Errno").getClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getModule("Errno").getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getModule("Errno").getClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getModule("Errno").getClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getModule("Errno").getClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getModule("Errno").getClass("EDOM"), "Domain error - " + message);
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(getClass("IndexError"), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getClass("SecurityError"), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getClass("SystemCallError"), message);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(getClass("TypeError"), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(getClass("ThreadError"), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(getClass("SyntaxError"), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(getClass("RegexpError"), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getClass("RangeError"), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getClass("NotImplementedError"), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(getClass("Iconv").getClass("InvalidEncoding"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, this.getClass("NoMethodError"), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.getClass("NameError"), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getClass("LocalJumpError"), message, reason, exitValue), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getClass("LoadError"), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(getClass("TypeError"), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getClass("SystemStackError"), message);
     }
 
     public RaiseException newSystemExit(int status) {
         RubyClass exc = getClass("SystemExit");
         IRubyObject[]exArgs = new IRubyObject[]{newFixnum(status), newString("exit")};
         return new RaiseException((RubyException)exc.newInstance(exArgs, Block.NULL_BLOCK));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getClass("IOError"), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getClass("StandardError"), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         return newRaiseException(getClass("IOError"), ioe.getMessage());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(getClass("TypeError"), "wrong argument type " +
                 receivedObject.getMetaClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(getClass("EOFError"), "End of file reached");
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(getClass("ZeroDivisionError"), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(getClass("FloatDomainError"), message);
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
 
     public Hashtable getIoHandlers() {
         return ioHandlers;
     }
 
     public RubyFixnum[] getFixnumCache() {
         return fixnumCache;
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
 
     private ThreadLocal inspect = new ThreadLocal();
-    public boolean registerInspecting(Object obj) {
+    public void registerInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
-        if(null == val) {
-            val = new java.util.IdentityHashMap();
-            inspect.set(val);
-        }
-        if(val.containsKey(obj)) {
-            return false;
-        }
+        if (val == null) inspect.set(val = new java.util.IdentityHashMap());
         val.put(obj, null);
-        return true;
+    }
+
+    public boolean isInspecting(Object obj) {
+        java.util.Map val = (java.util.Map)inspect.get();
+        return val == null ? false : val.containsKey(obj);
     }
 
     public void unregisterInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
-        val.remove(obj);
+        if (val != null ) val.remove(obj);
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
             jrubyHome = verifyHome(System.getProperty("jruby.home", System.getProperty("user.home") + "/.jruby"));
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
 }
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 806c0f07f9..fd6025a29b 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -129,2166 +129,2178 @@ public class RubyArray extends RubyObject implements List {
         arrayc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
         arrayc.defineMethod("reject", callbackFactory.getMethod("reject"));
         arrayc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
         arrayc.defineMethod("zip", callbackFactory.getOptMethod("zip"));
         arrayc.defineFastMethod("transpose", callbackFactory.getFastMethod("transpose"));
         arrayc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
         arrayc.defineMethod("fill", callbackFactory.getOptMethod("fill"));
         arrayc.defineFastMethod("include?", callbackFactory.getFastMethod("include_p", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("slice", callbackFactory.getFastOptMethod("aref"));
         arrayc.defineFastMethod("slice!", callbackFactory.getFastOptMethod("slice_bang"));
 
         arrayc.defineFastMethod("assoc", callbackFactory.getFastMethod("assoc", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("rassoc", callbackFactory.getFastMethod("rassoc", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("*", callbackFactory.getFastMethod("op_times", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("-", callbackFactory.getFastMethod("op_diff", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("&", callbackFactory.getFastMethod("op_and", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("|", callbackFactory.getFastMethod("op_or", RubyKernel.IRUBY_OBJECT));
 
         arrayc.defineFastMethod("uniq", callbackFactory.getFastMethod("uniq"));
         arrayc.defineFastMethod("uniq!", callbackFactory.getFastMethod("uniq_bang"));
         arrayc.defineFastMethod("compact", callbackFactory.getFastMethod("compact"));
         arrayc.defineFastMethod("compact!", callbackFactory.getFastMethod("compact_bang"));
 
         arrayc.defineFastMethod("flatten", callbackFactory.getFastMethod("flatten"));
         arrayc.defineFastMethod("flatten!", callbackFactory.getFastMethod("flatten_bang"));
 
         arrayc.defineFastMethod("nitems", callbackFactory.getFastMethod("nitems"));
 
         arrayc.defineFastMethod("pack", callbackFactory.getFastMethod("pack", RubyKernel.IRUBY_OBJECT));
         
         arrayc.dispatcher = callbackFactory.createDispatcher(arrayc);
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
     }
 
     /** rb_ary_s_create
      * 
      */
     public static IRubyObject create(IRubyObject klass, IRubyObject[] args, Block block) {
         RubyArray arr = (RubyArray) ((RubyClass) klass).allocate();
         arr.callInit(IRubyObject.NULL_ARRAY, block);
     
         if (args.length > 0) {
             arr.alloc(args.length);
             System.arraycopy(args, 0, arr.values, 0, args.length);
             arr.realLength = args.length;
         }
         return arr;
     }
 
     /** rb_ary_new2
      *
      */
     public static final RubyArray newArray(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len);
     }
     public static final RubyArray newArrayLight(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len, false);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArray(final Ruby runtime) {
         return new RubyArray(runtime, ARRAY_DEFAULT_SIZE);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArrayLight(final Ruby runtime) {
         /* Ruby arrays default to holding 16 elements, so we create an
          * ArrayList of the same size if we're not told otherwise
          */
         RubyArray arr = new RubyArray(runtime, false);
         arr.alloc(ARRAY_DEFAULT_SIZE);
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, IRubyObject obj) {
         return new RubyArray(runtime, new IRubyObject[] { obj });
     }
 
     /** rb_assoc_new
      *
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject car, IRubyObject cdr) {
         return new RubyArray(runtime, new IRubyObject[] { car, cdr });
     }
 
     /** rb_ary_new4, rb_ary_new3
      *   
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, args.length);
         System.arraycopy(args, 0, arr.values, 0, args.length);
         arr.realLength = args.length;
         return arr;
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args) {
         return new RubyArray(runtime, args);
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args, int begin) {
         return new RubyArray(runtime, args, begin);
     }
     
     public static RubyArray newArrayNoCopyLight(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, false);
         arr.values = args;
         arr.realLength = args.length;
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, Collection collection) {
         RubyArray arr = new RubyArray(runtime, collection.size());
         collection.toArray(arr.values);
         arr.realLength = arr.values.length;
         return arr;
     }
 
     public static final int ARRAY_DEFAULT_SIZE = 16;    
 
     private IRubyObject[] values;
 
     private static final int TMPLOCK_ARR_F = 1 << 9;
     private static final int SHARED_ARR_F = 1 << 10;
 
     private int begin = 0;
     private int realLength = 0;
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         values = vals;
         realLength = vals.length;
     }
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals, int begin) {
         super(runtime, runtime.getArray());
         this.values = vals;
         this.begin = begin;
         this.realLength = vals.length - begin;
         flags |= SHARED_ARR_F;
     }
     
     /* rb_ary_new2
      * just allocates the internal array
      */
     private RubyArray(Ruby runtime, long length) {
         super(runtime, runtime.getArray());
         checkLength(length);
         alloc((int) length);
     }
 
     private RubyArray(Ruby runtime, long length, boolean objectspace) {
         super(runtime, runtime.getArray(), objectspace);
         checkLength(length);
         alloc((int)length);
     }     
 
     /* rb_ary_new3, rb_ary_new4
      * allocates the internal array of size length and copies the 'length' elements
      */
     public RubyArray(Ruby runtime, long length, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         checkLength(length);
         int ilength = (int) length;
         alloc(ilength);
         if (ilength > 0 && vals.length > 0) System.arraycopy(vals, 0, values, 0, ilength);
 
         realLength = ilength;
     }
 
     /* NEWOBJ and OBJSETUP equivalent
      * fastest one, for shared arrays, optional objectspace
      */
     private RubyArray(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
     }
 
     private RubyArray(Ruby runtime) {
         super(runtime, runtime.getArray());
         alloc(ARRAY_DEFAULT_SIZE);
     }
 
     public RubyArray(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         alloc(ARRAY_DEFAULT_SIZE);
     }
     
     /* Array constructors taking the MetaClass to fulfil MRI Array subclass behaviour
      * 
      */
     private RubyArray(Ruby runtime, RubyClass klass, int length) {
         super(runtime, klass);
         alloc(length);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, long length) {
         super(runtime, klass);
         checkLength(length);
         alloc((int)length);
     }
 
     private RubyArray(Ruby runtime, RubyClass klass, long length, boolean objectspace) {
         super(runtime, klass, objectspace);
         checkLength(length);
         alloc((int)length);
     }    
 
     private RubyArray(Ruby runtime, RubyClass klass, boolean objectSpace) {
         super(runtime, klass, objectSpace);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, RubyArray original) {
         super(runtime, klass);
         realLength = original.realLength;
         alloc(realLength);
         System.arraycopy(original.values, original.begin, values, 0, realLength);
     }
     
     private final IRubyObject[] reserve(int length) {
         return new IRubyObject[length];
     }
 
     private final void alloc(int length) {
         values = new IRubyObject[length];
     }
 
     private final void realloc(int newLength) {
         IRubyObject[] reallocated = new IRubyObject[newLength];
         System.arraycopy(values, 0, reallocated, 0, newLength > realLength ? realLength : newLength);
         values = reallocated;
     }
 
     private final void checkLength(long length) {
         if (length < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         if (length >= Integer.MAX_VALUE) {
             throw getRuntime().newArgumentError("array size too big");
         }
     }
 
     /** Getter for property list.
      * @return Value of property list.
      */
     public List getList() {
         return Arrays.asList(toJavaArray()); 
     }
 
     public int getLength() {
         return realLength;
     }
 
     public IRubyObject[] toJavaArray() {
         IRubyObject[] copy = reserve(realLength);
         System.arraycopy(values, begin, copy, 0, realLength);
         return copy;
     }
     
     public IRubyObject[] toJavaArrayUnsafe() {
         return (flags & SHARED_ARR_F) == 0 ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return ((flags & SHARED_ARR_F) == 0 && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len, RubyClass klass, boolean objectSpace) {
         RubyArray sharedArray = new RubyArray(getRuntime(), klass, objectSpace);
         flags |= SHARED_ARR_F;
         sharedArray.values = values;
         sharedArray.flags |= SHARED_ARR_F;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         testFrozen("array");
 
         if ((flags & TMPLOCK_ARR_F) != 0) {
             throw getRuntime().newTypeError("can't modify array during iteration");
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify array");
         }
     }
 
     /** rb_ary_modify
      *
      */
     private final void modify() {
         modifyCheck();
         if ((flags & SHARED_ARR_F) != 0) {
             IRubyObject[] vals = reserve(realLength);
             flags &= ~SHARED_ARR_F;
             System.arraycopy(values, begin, vals, 0, realLength);
             begin = 0;            
             values = vals;
         }
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_ary_initialize
      * 
      */
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 2);
         Ruby runtime = getRuntime();
 
         if (argc == 0) {
             realLength = 0;
             if (block.isGiven()) runtime.getWarnings().warn("given block not used");
 
     	    return this;
     	}
 
         if (argc == 1 && !(args[0] instanceof RubyFixnum)) {
             IRubyObject val = args[0].checkArrayType();
             if (!val.isNil()) {
                 replace(val);
                 return this;
             }
         }
 
         long len = RubyNumeric.num2long(args[0]);
 
         if (len < 0) throw runtime.newArgumentError("negative array size");
 
         if (len >= Integer.MAX_VALUE) throw runtime.newArgumentError("array size too big");
 
         int ilen = (int) len;
 
         modify();
 
         if (ilen > values.length) values = reserve(ilen);
 
         if (block.isGiven()) {
             if (argc == 2) {
                 runtime.getWarnings().warn("block supersedes default value argument");
             }
 
             ThreadContext context = runtime.getCurrentContext();
             for (int i = 0; i < ilen; i++) {
                 store(i, block.yield(context, new RubyFixnum(runtime, i)));
                 realLength = i + 1;
             }
         } else {
             Arrays.fill(values, 0, ilen, (argc == 2) ? args[1] : runtime.getNil());
             realLength = ilen;
         }
     	return this;
     }
 
     /** rb_ary_replace
      *
      */
     public IRubyObject replace(IRubyObject orig) {
         modifyCheck();
 
         RubyArray origArr = orig.convertToArray();
 
         if (this == orig) return this;
 
         origArr.flags |= SHARED_ARR_F;
         flags |= SHARED_ARR_F;        
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
 
 
         return this;
     }
 
     /** rb_ary_to_s
      *
      */
     public IRubyObject to_s() {
         if (realLength == 0) return getRuntime().newString("");
 
         return join(getRuntime().getGlobalVariables().get("$,"));
     }
 
     public boolean includes(IRubyObject item) {
         final ThreadContext context = getRuntime().getCurrentContext();
         int begin = this.begin;
         
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].equalInternal(context,item ).isTrue()) return true;
     	}
         
         return false;
     }
 
     /** rb_ary_hash
      * 
      */
     public RubyFixnum hash() {
         int h = realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             h = (h << 1) | (h < 0 ? 1 : 0);
             h ^= RubyNumeric.num2long(values[i].callMethod(context, MethodIndex.HASH, "hash"));
         }
 
         return runtime.newFixnum(h);
     }
 
     /** rb_ary_store
      *
      */
     public final IRubyObject store(long index, IRubyObject value) {
         if (index < 0) {
             index += realLength;
             if (index < 0) {
                 throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
             }
         }
 
         modify();
 
         if (index >= realLength) {
         if (index >= values.length) {
                 long newLength = values.length >> 1;
 
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += index;
             if (newLength >= Integer.MAX_VALUE) {
                 throw getRuntime().newArgumentError("index too big");
             }
             realloc((int) newLength);
         }
             if(index != realLength) Arrays.fill(values, realLength, (int) index + 1, getRuntime().getNil());
             
             realLength = (int) index + 1;
         }
 
         values[(int) index] = value;
         return value;
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(long offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(int offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(long offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(int offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(long offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(int offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
     public final IRubyObject eltInternal(int offset) {
         return values[begin + offset];
     }
     
     public final IRubyObject eltInternalSet(int offset, IRubyObject item) {
         return values[begin + offset] = item;
     }
     
     /** rb_ary_fetch
      *
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         long index = RubyNumeric.num2long(args[0]);
 
         if (index < 0) index += realLength;
 
         if (index < 0 || index >= realLength) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
 
             if (args.length == 1) {
                 throw getRuntime().newIndexError("index " + index + " out of array");
             }
             
             return args[1];
         }
         
         return values[begin + (int) index];
     }
 
     /** rb_ary_to_ary
      * 
      */
     private static RubyArray aryToAry(IRubyObject obj) {
         if (obj instanceof RubyArray) return (RubyArray) obj;
 
         if (obj.respondsTo("to_ary")) return obj.convertToArray();
 
         RubyArray arr = new RubyArray(obj.getRuntime(), false); // possibly should not in object space
         arr.alloc(1);
         arr.values[0] = obj;
         arr.realLength = 1;
         return arr;
     }
 
     /** rb_ary_splice
      * 
      */
     private final void splice(long beg, long len, IRubyObject rpl) {
         int rlen;
 
         if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
 
         if (beg < 0) {
             beg += realLength;
             if (beg < 0) {
                 beg -= realLength;
                 throw getRuntime().newIndexError("index " + beg + " out of array");
             }
         }
         
         if (beg + len > realLength) len = realLength - beg;
 
         RubyArray rplArr;
         if (rpl == null || rpl.isNil()) {
             rplArr = null;
             rlen = 0;
         } else {
             rplArr = aryToAry(rpl);
             rlen = rplArr.realLength;
         }
 
         modify();
 
         if (beg >= realLength) {
             len = beg + rlen;
 
             if (len >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(len > tryNewLength ? (int)len : tryNewLength);
             }
 
             Arrays.fill(values, realLength, (int) beg, getRuntime().getNil());
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
             realLength = (int) len;
         } else {
             long alen;
 
             if (beg + len > realLength) len = realLength - beg;
 
             alen = realLength + rlen - len;
             if (alen >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(alen > tryNewLength ? (int)alen : tryNewLength);
             }
 
             if (len != rlen) {
                 System.arraycopy(values, (int) (beg + len), values, (int) beg + rlen, realLength - (int) (beg + len));
                 realLength = (int) alen;
             }
 
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
         }
     }
 
     /** rb_ary_insert
      * 
      */
     public IRubyObject insert(IRubyObject[] args) {
         if (args.length == 1) return this;
 
         if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments (at least 1)");
         }
 
         long pos = RubyNumeric.num2long(args[0]);
 
         if (pos == -1) pos = realLength;
         if (pos < 0) pos++;
 
         RubyArray inserted = new RubyArray(getRuntime(), false);
         inserted.values = args;
         inserted.begin = 1;
         inserted.realLength = args.length - 1;
         
         splice(pos, 0, inserted); // rb_ary_new4
         
         return this;
     }
 
     /** rb_ary_dup
      * 
      */
     private final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), getMetaClass(), this);
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         return dup;
     }
 
     /** rb_ary_transpose
      * 
      */
     public RubyArray transpose() {
         RubyArray tmp, result = null;
 
         int alen = realLength;
         if (alen == 0) return aryDup();
     
         Ruby runtime = getRuntime();
         int elen = -1;
         int end = begin + alen;
         for (int i = begin; i < end; i++) {
             tmp = elt(i).convertToArray();
             if (elen < 0) {
                 elen = tmp.realLength;
                 result = new RubyArray(runtime, elen);
                 for (int j = 0; j < elen; j++) {
                     result.store(j, new RubyArray(runtime, alen));
                 }
             } else if (elen != tmp.realLength) {
                 throw runtime.newIndexError("element size differs (" + tmp.realLength
                         + " should be " + elen + ")");
             }
             for (int j = 0; j < elen; j++) {
                 ((RubyArray) result.elt(j)).store(i - begin, tmp.elt(j));
             }
         }
         return result;
     }
 
     /** rb_values_at (internal)
      * 
      */
     private final IRubyObject values_at(long olen, IRubyObject[] args) {
         RubyArray result = new RubyArray(getRuntime(), args.length);
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyFixnum) {
                 result.append(entry(((RubyFixnum)args[i]).getLongValue()));
                 continue;
             }
 
             long beglen[];
             if (!(args[i] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[i]).begLen(olen, 0)) == null) {
                 continue;
             } else {
                 int beg = (int) beglen[0];
                 int len = (int) beglen[1];
                 int end = begin + len;
                 for (int j = begin; j < end; j++) {
                     result.append(entry(j + beg));
                 }
                 continue;
             }
             result.append(entry(RubyNumeric.num2long(args[i])));
         }
 
         return result;
     }
 
     /** rb_values_at
      * 
      */
     public IRubyObject values_at(IRubyObject[] args) {
         return values_at(realLength, args);
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseq(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), true);
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseqLight(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0, false);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), false);
     }
 
     /** rb_ary_length
      *
      */
     public RubyFixnum length() {
         return getRuntime().newFixnum(realLength);
     }
 
     /** rb_ary_push - specialized rb_ary_store 
      *
      */
     public RubyArray append(IRubyObject item) {
         modify();
         
         if (realLength == values.length) {
         if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
             
             long newLength = values.length + (values.length >> 1);
             if ( newLength > Integer.MAX_VALUE ) {
                 newLength = Integer.MAX_VALUE;
             }else if ( newLength < ARRAY_DEFAULT_SIZE ) {
                 newLength = ARRAY_DEFAULT_SIZE;
             }
 
             realloc((int) newLength);
         }
         
         values[realLength++] = item;
         return this;
     }
 
     /** rb_ary_push_m
      *
      */
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     public IRubyObject pop() {
         modifyCheck();
         
         if (realLength == 0) return getRuntime().getNil();
 
         if ((flags & SHARED_ARR_F) == 0) {
             int index = begin + --realLength;
             IRubyObject obj = values[index];
             values[index] = null;
             return obj;
         } 
 
         return values[begin + --realLength];
     }
 
     /** rb_ary_shift
      *
      */
     public IRubyObject shift() {
         modifyCheck();
 
         if (realLength == 0) return getRuntime().getNil();
 
         IRubyObject obj = values[begin];
 
         flags |= SHARED_ARR_F;
 
         begin++;
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_unshift
      *
      */
     public RubyArray unshift(IRubyObject item) {
         modify();
 
         if (realLength == values.length) {
             int newLength = values.length >> 1;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += values.length;
             realloc(newLength);
         }
         System.arraycopy(values, 0, values, 1, realLength);
 
         realLength++;
         values[0] = item;
 
         return this;
     }
 
     /** rb_ary_unshift_m
      *
      */
     public RubyArray unshift_m(IRubyObject[] items) {
         long len = realLength;
 
         if (items.length == 0) return this;
 
         store(len + items.length - 1, getRuntime().getNil());
 
         // it's safe to use zeroes here since modified by store()
         System.arraycopy(values, 0, values, items.length, (int) len);
         System.arraycopy(items, 0, values, 0, items.length);
         
         return this;
     }
 
     /** rb_ary_includes
      * 
      */
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /** rb_ary_aref
      */
     public IRubyObject aref(IRubyObject[] args) {
         long beg, len;
 
         if(args.length == 1) {
             if (args[0] instanceof RubyFixnum) return entry(((RubyFixnum)args[0]).getLongValue());
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             
             long[] beglen;
             if (!(args[0] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[0]).begLen(realLength, 0)) == null) {
                 return getRuntime().getNil();
             } else {
                 beg = beglen[0];
                 len = beglen[1];
                 return subseq(beg, len);
             }
 
             return entry(RubyNumeric.num2long(args[0]));            
         }        
 
         if (args.length == 2) {
             if (args[0] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as array index");
             }
             beg = RubyNumeric.num2long(args[0]);
             len = RubyNumeric.num2long(args[1]);
 
             if (beg < 0) beg += realLength;
 
             return subseq(beg, len);
         }
 
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         return null;
         }
 
     /** rb_ary_aset
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         if (args.length == 2) {
         if (args[0] instanceof RubyFixnum) {
                 store(((RubyFixnum)args[0]).getLongValue(), args[1]);
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] beglen = ((RubyRange) args[0]).begLen(realLength, 1);
             splice(beglen[0], beglen[1], args[1]);
             return args[1];
         }
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
 
         store(RubyNumeric.num2long(args[0]), args[1]);
         return args[1];
     }
 
         if (args.length == 3) {
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             if (args[1] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as subarray length");
 
             splice(RubyNumeric.num2long(args[0]), RubyNumeric.num2long(args[1]), args[2]);
             return args[2];
         }
 
         throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
     }
 
     /** rb_ary_at
      *
      */
     public IRubyObject at(IRubyObject pos) {
         return entry(RubyNumeric.num2long(pos));
     }
 
 	/** rb_ary_concat
      *
      */
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary);
 
         return this;
     }
 
-    /** rb_ary_inspect
-     *
+    /** inspect_ary
+     * 
      */
-    public IRubyObject inspect() {
-        if (realLength == 0) return getRuntime().newString("[]");
+    private IRubyObject inspectAry() {
+        StringBuffer buffer = new StringBuffer("[");
+        Ruby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        boolean tainted = isTaint();
 
-        if (!getRuntime().registerInspecting(this)) return getRuntime().newString("[...]");
+        for (int i = 0; i < realLength; i++) {
+            RubyString s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
 
-        RubyString s;
-        try {
-            StringBuffer buffer = new StringBuffer("[");
-            Ruby runtime = getRuntime();
-            ThreadContext context = runtime.getCurrentContext();
-            boolean tainted = isTaint();
-            for (int i = 0; i < realLength; i++) {
-                s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
-                
-                if (s.isTaint()) tainted = true;
+            if (s.isTaint()) tainted = true;
 
-                if (i > 0) buffer.append(", ");
+            if (i > 0) buffer.append(", ");
 
-                buffer.append(s.toString());
-            }
-            buffer.append("]");
-            if (tainted) setTaint(true);
+            buffer.append(s.toString());
+        }
+        buffer.append("]");
+
+        RubyString str = runtime.newString(buffer.toString());
+        if (tainted) str.setTaint(true);
+
+        return str;
+    }
+
+    /** rb_ary_inspect
+    *
+    */    
+    public IRubyObject inspect() {
+        if (realLength == 0) return getRuntime().newString("[]");
+        if (getRuntime().isInspecting(this)) return  getRuntime().newString("[...]");
 
-            return runtime.newString(buffer.toString());
+        try {
+            getRuntime().registerInspecting(this);
+            return inspectAry();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_first
      *
      */
     public IRubyObject first(IRubyObject[] args) {
     	if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
         return makeShared(begin, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
         if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin + realLength - 1];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - (int) n, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 block.yield(context, values[i]);
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, values[i]);
             }
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     public IRubyObject each_index(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             block.yield(context, runtime.newFixnum(i));
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverse_each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         int len = realLength;
         
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
-    private final IRubyObject inspectJoin(IRubyObject sep) {
-        IRubyObject result = join(sep);
-        getRuntime().unregisterInspecting(this);
-        return result;
+    private IRubyObject inspectJoin(RubyArray tmp, IRubyObject sep) {
+        try {
+            getRuntime().registerInspecting(this);
+            return tmp.join(sep);
+        } finally {
+            getRuntime().unregisterInspecting(this);
+        }
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(IRubyObject sep) {
         if (realLength == 0) return getRuntime().newString("");
 
         boolean taint = isTaint() || sep.isTaint();
 
         long len = 1;
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject tmp = values[i].checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         RubyString strSep = null;
         if (!sep.isNil()) {
             sep = strSep = sep.convertToString();
             len += strSep.getByteList().length() * (realLength - 1);
         }
 
         ByteList buf = new ByteList((int)len);
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
-                if (!runtime.registerInspecting(tmp)) {
+                if (runtime.isInspecting(tmp)) {
                     tmp = runtime.newString("[...]");
                 } else {
-                    tmp = ((RubyArray) tmp).inspectJoin(sep);
+                    tmp = inspectJoin((RubyArray)tmp, sep);
                 }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
 
             if (i > begin && !sep.isNil()) buf.append(strSep.getByteList());
 
             buf.append(tmp.asString().getByteList());
-            taint |= tmp.isTaint();
+            if (tmp.isTaint()) taint = true;
         }
 
         RubyString result = runtime.newString(buf); 
 
         if (taint) result.setTaint(true);
 
         return result;
     }
 
     /** rb_ary_join_m
      *
      */
     public RubyString join_m(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), true);
 
             flags |= SHARED_ARR_F;
             dup.flags |= SHARED_ARR_F;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
     public IRubyObject to_ary() {
     	return this;
     }
 
     public RubyArray convertToArray() {
         return this;
     }
     
     public IRubyObject checkArrayType(){
         return this;
     }
 
     /** rb_ary_equal
      *
      */
     public IRubyObject op_equal(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 return obj.equalInternal(getRuntime().getCurrentContext(), this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!elt(i).equalInternal(context, ary.elt(i)).isTrue()) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             if (!elt(i).eqlInternal(context, ary.elt(i))) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     public IRubyObject compact_bang() {
         modify();
 
         int p = 0;
         int t = 0;
         int end = p + realLength;
 
         while (t < end) {
             if (values[t].isNil()) {
                 t++;
             } else {
                 values[p++] = values[t++];
             }
         }
 
         if (realLength == p) return getRuntime().getNil();
 
         realloc(p);
         realLength = p;
         return this;
     }
 
     /** rb_ary_compact
      *
      */
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if((flags & SHARED_ARR_F) != 0){
             alloc(ARRAY_DEFAULT_SIZE);
             flags |= SHARED_ARR_F;
         } else if (values.length > ARRAY_DEFAULT_SIZE << 1){
             alloc(ARRAY_DEFAULT_SIZE << 1);
         }
 
         begin = 0;
         realLength = 0;
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     public IRubyObject fill(IRubyObject[] args, Block block) {
         IRubyObject item = null;
         IRubyObject begObj = null;
         IRubyObject lenObj = null;
         int argc = args.length;
 
         if (block.isGiven()) {
             Arity.checkArgumentCount(getRuntime(), args, 0, 2);
             item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 1, 3);
             item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
 
         long beg = 0, end = 0, len = 0;
         switch (argc) {
         case 1:
             beg = 0;
             len = realLength;
             break;
         case 2:
             if (begObj instanceof RubyRange) {
                 long[] beglen = ((RubyRange) begObj).begLen(realLength, 1);
                 beg = (int) beglen[0];
                 len = (int) beglen[1];
                 break;
             }
             /* fall through */
         case 3:
             beg = begObj.isNil() ? 0 : RubyNumeric.num2long(begObj);
             if (beg < 0) {
                 beg = realLength + beg;
                 if (beg < 0) beg = 0;
             }
             len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2long(lenObj);
             break;
         }
 
         modify();
 
         end = beg + len;
         if (end > realLength) {
             if (end >= values.length) realloc((int) end);
 
             Arrays.fill(values, realLength, (int) end, getRuntime().getNil());
             realLength = (int) end;
         }
 
         if (block.isGiven()) {
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             for (int i = (int) beg; i < (int) end; i++) {
                 IRubyObject v = block.yield(context, runtime.newFixnum(i));
                 if (i >= realLength) break;
 
                 values[i] = v;
             }
         } else {
             if(len > 0) Arrays.fill(values, (int) beg, (int) (beg + len), item);
         }
         
         return this;
     }
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].equalInternal(context, obj).isTrue()) return runtime.newFixnum(i - begin);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (values[begin + i].equalInternal(context, obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn("Array#indexes is deprecated; use Array#values_at");
 
         RubyArray ary = new RubyArray(getRuntime(), args.length);
 
         IRubyObject[] arefArgs = new IRubyObject[1];
         for (int i = 0; i < args.length; i++) {
             arefArgs[0] = args[i];
             ary.append(aref(arefArgs));
         }
 
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     public IRubyObject reverse_bang() {
         modify();
 
         IRubyObject tmp;
         if (realLength > 1) {
             int p1 = 0;
             int p2 = p1 + realLength - 1;
 
             while (p1 < p2) {
                 tmp = values[p1];
                 values[p1++] = values[p2];
                 values[p2--] = tmp;
             }
         }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     public IRubyObject reverse() {
         return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
     public RubyArray collect(Block block) {
         Ruby runtime = getRuntime();
         
         if (!block.isGiven()) return new RubyArray(getRuntime(), runtime.getArray(), this);
         
         ThreadContext context = runtime.getCurrentContext();
         RubyArray collect = new RubyArray(runtime, realLength);
         
         for (int i = begin; i < begin + realLength; i++) {
             collect.append(block.yield(context, values[i]));
         }
         
         return collect;
     }
 
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collect_bang(Block block) {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_select
      *
      */
     public RubyArray select(Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         ThreadContext context = runtime.getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i - begin));
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i));
             }
         }
         return result;
     }
 
     /** rb_ary_delete
      *
      */
     public IRubyObject delete(IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (e.equalInternal(context, item).isTrue()) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
         
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         if (realLength > i2) {
             realLength = i2;
             if (i2 << 1 < values.length && values.length > ARRAY_DEFAULT_SIZE) realloc(i2 << 1);
         }
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     private final IRubyObject delete_at(int pos) {
         int len = realLength;
 
         if (pos >= len) return getRuntime().getNil();
 
         if (pos < 0) pos += len;
 
         if (pos < 0) return getRuntime().getNil();
 
         modify();
 
         IRubyObject obj = values[pos];
         System.arraycopy(values, pos + 1, values, pos, len - (pos + 1));
 
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_delete_at_m
      * 
      */
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     public IRubyObject reject(Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject reject_bang(Block block) {
         int i2 = 0;
         modify();
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject v = values[i1];
             if (block.yield(context, v).isTrue()) continue;
 
             if (i1 != i2) store(i2, v);
             i2++;
         }
         if (realLength == i2) return getRuntime().getNil();
 
         if (i2 < realLength) realLength = i2;
 
         return this;
     }
 
     /** rb_ary_delete_if
      *
      */
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
     public IRubyObject zip(IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 RubyArray tmp = new RubyArray(runtime, args.length + 1);
                 tmp.append(elt(i));
                 for (int j = 0; j < args.length; j++) {
                     tmp.append(((RubyArray) args[j]).elt(i));
                 }
                 block.yield(context, tmp);
             }
             return runtime.getNil();
         }
         
         int len = realLength;
         RubyArray result = new RubyArray(runtime, len);
         for (int i = 0; i < len; i++) {
             RubyArray tmp = new RubyArray(runtime, args.length + 1);
             tmp.append(elt(i));
             for (int j = 0; j < args.length; j++) {
                 tmp.append(((RubyArray) args[j]).elt(i));
             }
             result.append(tmp);
         }
         return result;
     }
 
     /** rb_ary_cmp
      *
      */
     public IRubyObject op_cmp(IRubyObject obj) {
         RubyArray ary2 = obj.convertToArray();
 
         int len = realLength;
 
         if (len > ary2.realLength) len = ary2.realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < len; i++) {
             IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
             if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
         }
         len = realLength - ary2.realLength;
 
         if (len == 0) return RubyFixnum.zero(runtime);
         if (len > 0) return RubyFixnum.one(runtime);
 
         return RubyFixnum.minus_one(runtime);
     }
 
     /** rb_ary_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             long pos = RubyNumeric.num2long(args[0]);
             long len = RubyNumeric.num2long(args[1]);
             
             if (pos < 0) pos = realLength + pos;
 
             args[1] = subseq(pos, len);
             splice(pos, len, null);
             
             return args[1];
         }
         
         IRubyObject arg = args[0];
         if (arg instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg).begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
 
             if (pos < 0) {
                 pos = realLength + pos;
             }
             arg = subseq(pos, len);
             splice(pos, len, null);
             return arg;
         }
 
         return delete_at((int) RubyNumeric.num2long(args[0]));
     }
 
     /** rb_ary_assoc
      *
      */
     public IRubyObject assoc(IRubyObject key) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
                     && ((RubyArray) v).values[0].equalInternal(context, key).isTrue()) {
                 return v;
             }
         }
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     public IRubyObject rassoc(IRubyObject value) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
                     && ((RubyArray) v).values[1].equalInternal(context, value).isTrue()) {
                 return v;
             }
         }
 
         return runtime.getNil();
     }
 
     /** flatten
      * 
      */
     private final int flatten(int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(i, (RubyArray) tmp, memo);
                 i += n;
                 lim += n;
             }
             i++;
         }
         memo.pop();
         return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     public IRubyObject flatten_bang() {
         int i = 0;
         RubyArray memo = null;
 
         while (i < realLength) {
             IRubyObject ary2 = values[begin + i];
             IRubyObject tmp = ary2.checkArrayType();
             if (!tmp.isNil()) {
                 if (memo == null) {
                     memo = new RubyArray(getRuntime(), false);
                     memo.values = reserve(ARRAY_DEFAULT_SIZE);
                 }
 
                 i += flatten(i, (RubyArray) tmp, memo);
             }
             i++;
         }
         if (memo == null) return getRuntime().getNil();
 
         return this;
     }
 
     /** rb_ary_flatten
      *
      */
     public IRubyObject flatten() {
         RubyArray ary = aryDup();
         ary.flatten_bang();
         return ary;
     }
 
     /** rb_ary_nitems
      *
      */
     public IRubyObject nitems() {
         int n = 0;
 
         for (int i = begin; i < begin + realLength; i++) {
             if (!values[i].isNil()) n++;
         }
         
         return getRuntime().newFixnum(n);
     }
 
     /** rb_ary_plus
      *
      */
     public IRubyObject op_plus(IRubyObject obj) {
         RubyArray y = obj.convertToArray();
         int len = realLength + y.realLength;
         RubyArray z = new RubyArray(getRuntime(), len);
         System.arraycopy(values, begin, z.values, 0, realLength);
         System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
         z.realLength = len;
         return z;
     }
 
     /** rb_ary_times
      *
      */
     public IRubyObject op_times(IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(tmp);
 
         long len = RubyNumeric.num2long(times);
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw getRuntime().newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         RubyArray ary2 = new RubyArray(getRuntime(), getMetaClass(), len);
         ary2.realLength = (int) len;
 
         for (int i = 0; i < len; i += realLength) {
             System.arraycopy(values, begin, ary2.values, i, realLength);
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** ary_make_hash
      * 
      */
     private final Set makeSet(RubyArray ary2) {
         final Set set = new HashSet();
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             set.add(values[i]);
         }
 
         if (ary2 != null) {
             begin = ary2.begin;            
             for (int i = begin; i < begin + ary2.realLength; i++) {
                 set.add(ary2.values[i]);
             }
         }
         return set;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     public IRubyObject uniq_bang() {
         Set set = makeSet(null);
 
         if (realLength == set.size()) return getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     public IRubyObject uniq() {
         RubyArray ary = aryDup();
         ary.uniq_bang();
         return ary;
     }
 
     /** rb_ary_diff
      *
      */
     public IRubyObject op_diff(IRubyObject other) {
         Set set = other.convertToArray().makeSet(null);
         RubyArray ary3 = new RubyArray(getRuntime());
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             if (set.contains(values[i])) continue;
 
             ary3.append(elt(i - begin));
         }
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         Set set = ary2.makeSet(null);
         RubyArray ary3 = new RubyArray(getRuntime(), 
                 realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) ary3.append(v);
         }
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         Set set = makeSet(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
             if (set.remove(v)) ary3.append(v);
         }
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     public RubyArray sort(Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     public RubyArray sort_bang(Block block) {
         modify();
         if (realLength > 1) {
             flags |= TMPLOCK_ARR_F;
             try {
                 if (block.isGiven()) {
                     Arrays.sort(values, 0, realLength, new BlockComparator(block));
                 } else {
                     Arrays.sort(values, 0, realLength, new DefaultComparator());
                 }
             } finally {
                 flags &= ~TMPLOCK_ARR_F;
             }
         }
         return this;
     }
 
     final class BlockComparator implements Comparator {
         private Block block;
 
         public BlockComparator(Block block) {
             this.block = block;
         }
 
         public int compare(Object o1, Object o2) {
             ThreadContext context = getRuntime().getCurrentContext();
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
             IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     final class DefaultComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                 long a = ((RubyFixnum) o1).getLongValue();
                 long b = ((RubyFixnum) o2).getLongValue();
                 if (a > b) return 1;
                 if (a < b) return -1;
                 return 0;
             }
             if (o1 instanceof RubyString && o2 instanceof RubyString) {
                 return ((RubyString) o1).cmp((RubyString) o2);
             }
 
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
 
             IRubyObject ret = obj1.callMethod(obj1.getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", obj2);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.writeInt(array.getList().size());
         for (Iterator iter = array.getList().iterator(); iter.hasNext();) {
             output.dumpObject((IRubyObject) iter.next());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         RubyArray result = input.getRuntime().newArray();
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     public RubyString pack(IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(obj);
         return Pack.pack(getRuntime(), this, iFmt.getByteList());
     }
 
     public Class getJavaClass() {
         return List.class;
     }
 
     // Satisfy java.util.List interface (for Java integration)
 
 	public int size() {
         return realLength;
 	}
 
 	public boolean isEmpty() {
         return realLength == 0;
 	}
 
 	public boolean contains(Object element) {
         return indexOf(element) != -1;
 	}
 
 	public Object[] toArray() {
         Object[] array = new Object[realLength];
         for (int i = begin; i < realLength; i++) {
             array[i - begin] = JavaUtil.convertRubyToJava(values[i]);
         }
 		return array;
 	}
 
 	public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
         if (array.length < realLength) {
             Class type = array.getClass().getComponentType();
             array = (Object[]) Array.newInstance(type, realLength);
         }
         int length = realLength - begin;
 
         for (int i = 0; i < length; i++) {
            array[i] = JavaUtil.convertRubyToJava(values[i + begin]); 
         }
         return array;
 	}
 
 	public boolean add(Object element) {
         append(JavaUtil.convertJavaToRuby(getRuntime(), element));
         return true;
 	}
 
 	public boolean remove(Object element) {
         IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
         return deleted.isNil() ? false : true; // TODO: is this correct ?
 	}
 
 	public boolean containsAll(Collection c) {
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (indexOf(iter.next()) == -1) return false;
 		}
         
 		return true;
 	}
 
 	public boolean addAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
 			add(iter.next());
 		}
 		return !c.isEmpty();
 	}
 
 	public boolean addAll(int index, Collection c) {
 		Iterator iter = c.iterator();
 		for (int i = index; iter.hasNext(); i++) {
 			add(i, iter.next());
 		}
 		return !c.isEmpty();
 	}
 
 	public boolean removeAll(Collection c) {
         boolean listChanged = false;
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (remove(iter.next())) {
                 listChanged = true;
 			}
 		}
         return listChanged;
 	}
 
 	public boolean retainAll(Collection c) {
 		boolean listChanged = false;
 
 		for (Iterator iter = iterator(); iter.hasNext();) {
 			Object element = iter.next();
 			if (!c.contains(element)) {
 				remove(element);
 				listChanged = true;
 			}
 		}
 		return listChanged;
 	}
 
 	public Object get(int index) {
         return JavaUtil.convertRubyToJava((IRubyObject) elt(index), Object.class);
 	}
 
 	public Object set(int index, Object element) {
         return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
     // TODO: make more efficient by not creating IRubyArray[]
 	public void add(int index, Object element) {
         insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element) });
 	}
 
 	public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 293460b5fd..1ebfe7771a 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1639 +1,1648 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
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
 
 import java.io.IOException;
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Hash class.
  *
  * @author  jpetersen
  */
 public class RubyHash extends RubyObject implements Map {
     
     public static RubyClass createHashClass(Ruby runtime) {
         RubyClass hashc = runtime.defineClass("Hash", runtime.getObject(), HASH_ALLOCATOR);
         hashc.index = ClassIndex.HASH;
         hashc.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyHash;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyHash.class);
 
         hashc.includeModule(runtime.getModule("Enumerable"));
         hashc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
 
         hashc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         hashc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("rehash", callbackFactory.getFastMethod("rehash"));
 
         hashc.defineFastMethod("to_hash", callbackFactory.getFastMethod("to_hash"));        
         hashc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         hashc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));        
         hashc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
 
         hashc.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
         hashc.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("store", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default", callbackFactory.getFastOptMethod("default_value_get"));
         hashc.defineFastMethod("default=", callbackFactory.getFastMethod("default_value_set", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default_proc", callbackFactory.getFastMethod("default_proc"));
         hashc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("size", callbackFactory.getFastMethod("rb_size"));
         hashc.defineFastMethod("length", callbackFactory.getFastMethod("rb_size"));        
         hashc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
 
         hashc.defineMethod("each", callbackFactory.getMethod("each"));
         hashc.defineMethod("each_value", callbackFactory.getMethod("each_value"));
         hashc.defineMethod("each_key", callbackFactory.getMethod("each_key"));
         hashc.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));        
         hashc.defineMethod("sort", callbackFactory.getMethod("sort"));
 
         hashc.defineFastMethod("keys", callbackFactory.getFastMethod("keys"));
         hashc.defineFastMethod("values", callbackFactory.getFastMethod("rb_values"));
         hashc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
 
         hashc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
         hashc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
         hashc.defineMethod("select", callbackFactory.getOptMethod("select"));
         hashc.defineMethod("reject", callbackFactory.getMethod("reject"));
         hashc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
         hashc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
         hashc.defineFastMethod("invert", callbackFactory.getFastMethod("invert"));
         hashc.defineMethod("update", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge!", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge", callbackFactory.getMethod("merge", RubyKernel.IRUBY_OBJECT));
 
         hashc.defineFastMethod("include?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("member?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
         
         hashc.dispatcher = callbackFactory.createDispatcher(hashc);
 
         return hashc;
     }
 
     private final static ObjectAllocator HASH_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyHash(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }
 
     /** rb_hash_s_create
      * 
      */
     public static IRubyObject create(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass) recv;
         RubyHash hash;
 
         if (args.length == 1 && args[0] instanceof RubyHash) {
             RubyHash otherHash = (RubyHash)args[0];
             return new RubyHash(recv.getRuntime(), klass, otherHash.internalCopyTable(), otherHash.size); // hash_alloc0
         }
 
         if ((args.length & 1) != 0) throw recv.getRuntime().newArgumentError("odd number of args for Hash");
 
         hash = (RubyHash)klass.allocate();
         for (int i=0; i < args.length; i+=2) hash.aset(args[i], args[i+1]);
 
         return hash;
     }
 
     /** rb_hash_new
      * 
      */
     public static final RubyHash newHash(Ruby runtime) {
         return new RubyHash(runtime);
     }
 
     /** rb_hash_new
      * 
      */
     public static final RubyHash newHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         assert defaultValue != null;
     
         return new RubyHash(runtime, valueMap, defaultValue);
     }
 
     private RubyHashEntry[] table;
     private int size = 0;
     private int threshold;
 
     private int iterLevel = 0;
 
     private static final int DELETED_HASH_F = 1 << 9;    
     private static final int PROCDEFAULT_HASH_F = 1 << 10;
 
     private IRubyObject ifNone;
 
     private RubyHash(Ruby runtime, RubyClass klass, RubyHashEntry[]newTable, int newSize) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         threshold = INITIAL_THRESHOLD;
         table = newTable;
         size = newSize;
     }    
 
     public RubyHash(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         alloc();
     }
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         alloc();
     }
 
     // TODO should this be deprecated ? (to be efficient, internals should deal with RubyHash directly) 
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = runtime.getNil();
         alloc();
 
         for (Iterator iter = valueMap.entrySet().iterator();iter.hasNext();) {
             Map.Entry e = (Map.Entry)iter.next();
             internalPut((IRubyObject)e.getKey(), (IRubyObject)e.getValue());
     }
     }
 
     private final void alloc() {
         threshold = INITIAL_THRESHOLD;
         table = new RubyHashEntry[MRI_HASH_RESIZE ? MRI_INITIAL_CAPACITY : JAVASOFT_INITIAL_CAPACITY];
     }    
 
     /* ============================
      * Here are hash internals 
      * (This could be extracted to a separate class but it's not too large though)
      * ============================
      */    
 
     private static final int MRI_PRIMES[] = {
         8 + 3, 16 + 3, 32 + 5, 64 + 3, 128 + 3, 256 + 27, 512 + 9, 1024 + 9, 2048 + 5, 4096 + 3,
         8192 + 27, 16384 + 43, 32768 + 3, 65536 + 45, 131072 + 29, 262144 + 3, 524288 + 21, 1048576 + 7,
         2097152 + 17, 4194304 + 15, 8388608 + 9, 16777216 + 43, 33554432 + 35, 67108864 + 15,
         134217728 + 29, 268435456 + 3, 536870912 + 11, 1073741824 + 85, 0
     };    
 
     private static final int JAVASOFT_INITIAL_CAPACITY = 8; // 16 ?
     private static final int MRI_INITIAL_CAPACITY = MRI_PRIMES[0];
 
     private static final int INITIAL_THRESHOLD = JAVASOFT_INITIAL_CAPACITY - (JAVASOFT_INITIAL_CAPACITY >> 2);
     private static final int MAXIMUM_CAPACITY = 1 << 30;
 
     static final class RubyHashEntry implements Map.Entry {
         private IRubyObject key; 
         private IRubyObject value; 
         private RubyHashEntry next; 
         private int hash;
 
         RubyHashEntry(int h, IRubyObject k, IRubyObject v, RubyHashEntry e) {
             key = k; value = v; next = e; hash = h;
         }
         public Object getKey() {
             return key;
     }
         public Object getJavaifiedKey(){
             return JavaUtil.convertRubyToJava(key);
         }
         public Object getValue() {
             return value;            
         }
         public Object getJavaifiedValue() {
             return JavaUtil.convertRubyToJava(value);
         }
         public Object setValue(Object value) {            
             IRubyObject oldValue = this.value;
             if (value instanceof IRubyObject) {
                 this.value = (IRubyObject)value; 
         } else {
                 throw new UnsupportedOperationException("directEntrySet() doesn't support setValue for non IRubyObject instance entries, convert them manually or use entrySet() instead");                
                 }
             return oldValue;
         }
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
             if(key == otherEntry.key && key != NEVER && key.eql(otherEntry.key)){
                 if(value == otherEntry.value || value.equals(otherEntry.value)) return true;
             }            
             return false;
         }
         public int hashCode(){
             return key.hashCode() ^ value.hashCode();
         }
     }
 
     private static int JavaSoftHashValue(int h) {
         h ^= (h >>> 20) ^ (h >>> 12);
         return h ^ (h >>> 7) ^ (h >>> 4);
                 }
 
     private static int JavaSoftBucketIndex(final int h, final int length) {
         return h & (length - 1);
         }
 
     private static int MRIHashValue(int h) {
         return h & HASH_SIGN_BIT_MASK;
     }
 
     private static final int HASH_SIGN_BIT_MASK = ~(1 << 31);
     private static int MRIBucketIndex(final int h, final int length) {
         return (h % length);
     }
 
     private final void resize(int newCapacity) {
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[newCapacity];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {    
                 RubyHashEntry next = entry.next;
                 int i = bucketIndex(entry.hash, newCapacity);
                 entry.next = newTable[i];
                 newTable[i] = entry;
                 entry = next;
             }
         }
         table = newTable;
     }
 
     private final void JavaSoftCheckResize() {
         if (size > threshold) {
             int oldCapacity = table.length; 
             if (oldCapacity == MAXIMUM_CAPACITY) {
                 threshold = Integer.MAX_VALUE;
                 return;
             }
             int newCapacity = table.length << 1;
             resize(newCapacity);
             threshold = newCapacity - (newCapacity >> 2);
     }
     }
 
     private static final int MIN_CAPA = 8;
     private static final int ST_DEFAULT_MAX_DENSITY = 5;    
     private final void MRICheckResize() {
         if (size / table.length > ST_DEFAULT_MAX_DENSITY) {           
             int forSize = table.length + 1; // size + 1;         
             for (int i=0, newCapacity = MIN_CAPA; i < MRI_PRIMES.length; i++, newCapacity <<= 1) {
                 if (newCapacity > forSize) {                  
                     resize(MRI_PRIMES[i]);                  
                     return;                 
     }
             }
             return; // suboptimal for large hashes (> 1073741824 + 85 entries) not very likely to happen
         }
     }
     // ------------------------------   
     private static boolean MRI_HASH = true; 
     private static boolean MRI_HASH_RESIZE = true;
 
     private static int hashValue(final int h) {
         return MRI_HASH ? MRIHashValue(h) : JavaSoftHashValue(h);
     }
 
     private static int bucketIndex(final int h, final int length) {
         return MRI_HASH ? MRIBucketIndex(h, length) : JavaSoftBucketIndex(h, length); 
     }   
 
     private void checkResize() {
         if (MRI_HASH_RESIZE) MRICheckResize(); else JavaSoftCheckResize();
 	}
     // ------------------------------
     public static long collisions = 0;
 
     private final void internalPut(final IRubyObject key, final IRubyObject value) {
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
 
         // if (table[i] != null) collisions++;
 
         for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.value = value;
                 return;
 	}
         }
 
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
     }
 
     private final void internalPutDirect(final IRubyObject key, final IRubyObject value){
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
 	}
 
     private final IRubyObject internalGet(IRubyObject key) { // specialized for value
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry.value;
 	}
         return null;
     }
 
     private final RubyHashEntry internalGetEntry(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry;
         }
         return null;
     }
 
     private final RubyHashEntry internalDelete(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry entry = table[i];
 
         if (entry == null) return null;
 
         IRubyObject k;
         if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
             table[i] = entry.next;
             size--;
             return entry;
     }
         for (; entry.next != null; entry = entry.next) {
             RubyHashEntry tmp = entry.next;
             if (tmp.hash == hash && ((k = tmp.key) == key || key.eql(k))) {
                 entry.next = entry.next.next;
                 size--;
                 return tmp;
             }
         }
         return null;
     }
 
     private final RubyHashEntry internalDeleteSafe(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         RubyHashEntry entry = table[bucketIndex(hash, table.length)];
 
         if (entry == null) return null;
         IRubyObject k;
 
         for (; entry != null; entry = entry.next) {           
             if (entry.key != NEVER && entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.key = NEVER; // make it a skip node 
                 size--;             
                 return entry;
     }
         }
         return null;
     }   
 
     private final RubyHashEntry internalDeleteEntry(RubyHashEntry entry) {
         final int hash = hashValue(entry.key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry prev = table[i];
         RubyHashEntry e = prev;
         while (e != null){
             RubyHashEntry next = e.next;
             if (e.hash == hash && e.equals(entry)) {
                 size--;
                 if(iterLevel > 0){
                     if (prev == e) table[i] = next; else prev.next = next;
                 } else {
                     e.key = NEVER;
                 }
                 return e;
             }
             prev = e;
             e = next;
         }
         return e;
     }
 
     private final void internalCleanupSafe() { // synchronized ?
         for (int i=0; i < table.length; i++) {
             RubyHashEntry entry = table[i];
             while (entry != null && entry.key == NEVER) table[i] = entry = entry.next;
             if (entry != null) {
                 RubyHashEntry prev = entry;
                 entry = entry.next;
                 while (entry != null) {
                     if (entry.key == NEVER) { 
                         prev.next = entry.next;
                     } else {
                         prev = prev.next;
                     }
                     entry = prev.next;
                 }
             }
         }        
     }
 
     private final RubyHashEntry[] internalCopyTable() {
          RubyHashEntry[]newTable = new RubyHashEntry[table.length];
 
          for (int i=0; i < table.length; i++) {
              for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
                  if (entry.key != NEVER) newTable[i] = new RubyHashEntry(entry.hash, entry.key, entry.value, newTable[i]);
              }
          }
          return newTable;
     }
 
     // flags for callback based interation
     public static final int ST_CONTINUE = 0;    
     public static final int ST_STOP = 1;
     public static final int ST_DELETE = 2;
     public static final int ST_CHECK = 3;
 
     private void rehashOccured(){
         throw getRuntime().newRuntimeError("rehash occurred during iteration");
     }
 
     public static abstract class Callback { // a class to prevent invokeinterface
         public abstract int call(RubyHash hash, RubyHashEntry entry);
     }    
 
     private final int hashForEachEntry(final RubyHashEntry entry, final Callback callback) {
         if (entry.key == NEVER) return ST_CONTINUE;
         RubyHashEntry[]ltable = table;
 		
         int status = callback.call(this, entry);
         
         if (ltable != table) rehashOccured();
         
         switch (status) {
         case ST_DELETE:
             internalDeleteSafe(entry.key);
             flags |= DELETED_HASH_F;
         case ST_CONTINUE:
             break;
         case ST_STOP:
             return ST_STOP;
 	}
         return ST_CHECK;
     }    
 
     private final boolean internalForEach(final Callback callback) {
         RubyHashEntry entry, last, tmp;
         int length = table.length;
         for (int i = 0; i < length; i++) {
             last = null;
             for (entry = table[i]; entry != null;) {
                 switch (hashForEachEntry(entry, callback)) {
                 case ST_CHECK:
                     tmp = null;
                     if (i < length) for (tmp = table[i]; tmp != null && tmp != entry; tmp = tmp.next);
                     if (tmp == null) return true;
                 case ST_CONTINUE:
                     last = entry;
                     entry = entry.next;
                     break;
                 case ST_STOP:
                     return false;
                 case ST_DELETE:
                     tmp = entry;
                     if (last == null) table[i] = entry.next; else last.next = entry.next;
                     entry = entry.next;
                     size--;
                 }
             }
         }
         return false;
     }
 
     public final void forEach(final Callback callback) {
         try{
             preIter();
             if (internalForEach(callback)) rehashOccured();
         }finally{
             postIter();
         }
     }
 
     private final void preIter() {
         iterLevel++;
     }
 
     private final void postIter() {
         iterLevel--;
         if ((flags & DELETED_HASH_F) != 0) {
             internalCleanupSafe();
             flags &= ~DELETED_HASH_F;
         }
     }
 
     private final RubyHashEntry checkIter(RubyHashEntry[]ltable, RubyHashEntry node) {
         while (node != null && node.key == NEVER) node = node.next;
         if (ltable != table) rehashOccured();
         return node;
     }      
 
     /* ============================
      * End of hash internals
      * ============================
      */
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_hash_initialize
      * 
      */
     public IRubyObject initialize(IRubyObject[] args, final Block block) {
             modify();
 
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments");
             ifNone = getRuntime().newProc(false, block);
             flags |= PROCDEFAULT_HASH_F;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 0, 1);
             if (args.length == 1) ifNone = args[0];
         }
         return this;
     }
 
     /** rb_hash_default
      * 
      */
     public IRubyObject default_value_get(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             if (args.length == 0) return getRuntime().getNil();
             return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, args[0]});
         }
         return ifNone;
     }
 
     /** rb_hash_set_default
      * 
      */
     public IRubyObject default_value_set(final IRubyObject defaultValue) {
         modify();
 
         ifNone = defaultValue;
         flags &= ~PROCDEFAULT_HASH_F;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      * 
      */    
     public IRubyObject default_proc() {
         return (flags & PROCDEFAULT_HASH_F) != 0 ? ifNone : getRuntime().getNil();
     }
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("hash");
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify hash");
         }
     }
 
-    /** rb_hash_inspect
+    /** inspect_hash
      * 
      */
-    public IRubyObject inspect() {
-        Ruby runtime = getRuntime();
-        if (!runtime.registerInspecting(this)) {
-            return runtime.newString("{...}");
-        }
-
+    public IRubyObject inspectHash() {
         try {
+            Ruby runtime = getRuntime();
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = runtime.getCurrentContext();
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (!firstEntry) sb.append(sep);
                     sb.append(entry.key.callMethod(context, "inspect")).append(arrow);
                     sb.append(entry.value.callMethod(context, "inspect"));
-                firstEntry = false;
-            }
+                    firstEntry = false;
+                }
             }
             sb.append("}");
             return runtime.newString(sb.toString());
         } finally {
             postIter();
-            runtime.unregisterInspecting(this);
-        }
+        }         
+    }
+
+    /** rb_hash_inspect
+     * 
+     */
+    public IRubyObject inspect() {
+        if (size == 0) return getRuntime().newString("{}");
+        if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
+        
+        try {
+            getRuntime().registerInspecting(this);
+            return inspectHash();
+        } finally {
+            getRuntime().unregisterInspecting(this);
+        }        
     }
 
     /** rb_hash_size
      * 
      */    
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(size);
     }
 
     /** rb_hash_empty_p
      * 
      */
     public RubyBoolean empty_p() {
         return size == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_hash_to_a
      * 
      */
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {                
                     result.append(RubyArray.newArray(runtime, entry.key, entry.value));
         }
             }
         } finally {postIter();}
 
         result.setTaint(isTaint());
         return result;
     }
 
-    /** rb_hash_to_s
+    /** rb_hash_to_s & to_s_hash
      * 
      */
     public IRubyObject to_s() {
-        if (!getRuntime().registerInspecting(this)) {
-            return getRuntime().newString("{...}");
-        }
+        if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
         try {
+            getRuntime().registerInspecting(this);
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_hash_rehash
      * 
      */
     public RubyHash rehash() {
         modify();
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[oldTable.length];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {    
                 RubyHashEntry next = entry.next;
                 if (entry.key != NEVER) {
                     entry.hash = entry.key.hashCode(); // update the hash value
                     int i = bucketIndex(entry.hash, newTable.length);
                     entry.next = newTable[i];
                     newTable[i] = entry;
         }
                 entry = next;
             }
         }
         table = newTable;
         return this;
     }
 
     /** rb_hash_to_hash
      * 
      */
     public RubyHash to_hash() {
         return this;        
     }
 
     public RubyHash convertToHash() {    
         return this;
     }
 
     public final void fastASet(IRubyObject key, IRubyObject value) {
         internalPut(key, value);
     }
 
     /** rb_hash_aset
      * 
      */
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         modify();
 
         if (!(key instanceof RubyString)) {
             internalPut(key, value);
             return value;
         } 
 
         RubyHashEntry entry = null;        
         if ((entry = internalGetEntry(key)) != null) {
             entry.value = value;
         } else {
           IRubyObject realKey = ((RubyString)key).strDup();
             realKey.setFrozen(true);
           internalPutDirect(realKey, value);
         }
 
         return value;
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     /** rb_hash_aref
      * 
      */
     public IRubyObject aref(IRubyObject key) {        
         IRubyObject value;
         return ((value = internalGet(key)) == null) ? callMethod(getRuntime().getCurrentContext(), MethodIndex.DEFAULT, "default", key) : value;
     }
 
     /** rb_hash_fetch
      * 
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         IRubyObject value;
         if ((value = internalGet(args[0])) == null) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
             if (args.length == 1) throw getRuntime().newIndexError("key not found");
             return args[1];
         }
         return value;
     }
 
     /** rb_hash_has_key
      * 
      */
     public RubyBoolean has_key(IRubyObject key) {
         return internalGetEntry(key) == null ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_hash_has_value
      * 
      */
     public RubyBoolean has_value(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return runtime.getTrue();
     }
             }
         } finally {postIter();}
         return runtime.getFalse();
     }
 
     /** rb_hash_each
      * 
      */
 	public RubyHash each(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_assoc_new equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, false);
 	}
             }
         } finally {postIter();}
 
         return this;
     }
 
     /** rb_hash_each_pair
      * 
      */
 	public RubyHash each_pair(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_yield_values(2,...) equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true);                    
         }
     }
         } finally {postIter();}
 
         return this;	
 	}
 
     /** rb_hash_each_value
      * 
      */
     public RubyHash each_value(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.value);
 		}
 	}
         } finally {postIter();}
 
         return this;        
 	}
 
     /** rb_hash_each_key
      * 
      */
 	public RubyHash each_key(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.key);
 		}
 	}
         } finally {postIter();}
 
         return this;  
 	}
 
     /** rb_hash_sort
      * 
      */
 	public RubyArray sort(Block block) {
 		return to_a().sort_bang(block);
 	}
 
     /** rb_hash_index
      * 
      */
     public IRubyObject index(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return entry.key;
             }
         }
         } finally {postIter();}
 
         return getRuntime().getNil();        
     }
 
     /** rb_hash_indexes
      * 
      */
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(aref(indices[i]));
         }
 
         return values;
     }
 
     /** rb_hash_keys 
      * 
      */
     public RubyArray keys() {
         Ruby runtime = getRuntime();
         RubyArray keys = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     keys.append(entry.key);
     }
             }
         } finally {postIter();}
 
         return keys;          
     }
 
     /** rb_hash_values
      * 
      */
     public RubyArray rb_values() {
         RubyArray values = RubyArray.newArray(getRuntime(), size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     values.append(entry.value);
     }
             }
         } finally {postIter();}
 
         return values;
     }
 
     /** rb_hash_equal
      * 
      */
 
     private static final boolean EQUAL_CHECK_DEFAULT_VALUE = false; 
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other ) return getRuntime().getTrue();
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) return getRuntime().getFalse();
             return other.equalInternal(getRuntime().getCurrentContext(), this);
         }
 
         RubyHash otherHash = (RubyHash)other;
         if (size != otherHash.size) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();        
         ThreadContext context = runtime.getCurrentContext();
 
         if (EQUAL_CHECK_DEFAULT_VALUE) {
             if (!ifNone.equalInternal(context, otherHash.ifNone).isTrue() &&
                (flags & PROCDEFAULT_HASH_F) != (otherHash.flags & PROCDEFAULT_HASH_F)) return runtime.getFalse();
             }
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     IRubyObject value = otherHash.internalGet(entry.key);
                     if (value == null) return runtime.getFalse();
                     if (!entry.value.equalInternal(context, value).isTrue()) return runtime.getFalse();
         }
     }
         } finally {postIter();}        
 
         return runtime.getTrue();
     }
 
     /** rb_hash_shift
      * 
      */
     public IRubyObject shift() {
 		modify();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     RubyArray result = RubyArray.newArray(getRuntime(), entry.key, entry.value);
                     internalDeleteSafe(entry.key);
                     flags |= DELETED_HASH_F;
                     return result;
     }
             }
         } finally {postIter();}          
 
         if ((flags & PROCDEFAULT_HASH_F) != 0) return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, getRuntime().getNil()});
         return ifNone;
     }
 
     /** rb_hash_delete
      * 
      */
 	public IRubyObject delete(IRubyObject key, Block block) {
 		modify();
 
         RubyHashEntry entry;
         if (iterLevel > 0) {
             if ((entry = internalDeleteSafe(key)) != null) {
                 flags |= DELETED_HASH_F;
                 return entry.value;                
             }
         } else if ((entry = internalDelete(key)) != null) return entry.value;
 
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
         return getRuntime().getNil();
     }
 
     /** rb_hash_select
      * 
      */
     public IRubyObject select(IRubyObject[] args, Block block) {
         if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 0)");
         RubyArray result = getRuntime().newArray();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, runtime.newArray(entry.key, entry.value), null, null, true).isTrue()) {
                         result.append(runtime.newArray(entry.key, entry.value));
                     }
                 }
             }
         } finally {postIter();}
         return result;
     }
 
     /** rb_hash_delete_if
      * 
      */
 	public RubyHash delete_if(Block block) {
         modify();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true).isTrue())
                         delete(entry.key, block);
                 }
             }
         } finally {postIter();}        
 
 		return this;
 	}
 
     /** rb_hash_reject
      * 
      */
 	public RubyHash reject(Block block) {
         return ((RubyHash)dup()).delete_if(block);
 	}
 
     /** rb_hash_reject_bang
      * 
      */
 	public IRubyObject reject_bang(Block block) {
         int n = size;
         delete_if(block);
         if (n == size) return getRuntime().getNil();
         return this;
 			}
 
     /** rb_hash_clear
      * 
      */
 	public RubyHash rb_clear() {
 		modify();
 
         if (size > 0) { 
             alloc();
             size = 0;
             flags &= ~DELETED_HASH_F;
 	}
 
 		return this;
 	}
 
     /** rb_hash_invert
      * 
      */
 	public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     result.aset(entry.value, entry.key);
 		}
 	}
         } finally {postIter();}        
 
         return result;        
 	}
 
     /** rb_hash_update
      * 
      */
     public RubyHash update(IRubyObject other, Block block) {
         modify();
 
         RubyHash otherHash = other.convertToHash();
 
         try {
              otherHash.preIter();
              RubyHashEntry[]ltable = otherHash.table;
         if (block.isGiven()) {
                  Ruby runtime = getRuntime();
                  ThreadContext context = runtime.getCurrentContext();
 
                  for (int i = 0; i < ltable.length; i++) {
                      for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                          IRubyObject value;
                          if (internalGet(entry.key) != null)
                              value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{entry.key, aref(entry.key), entry.value}));
                          else
                              value = entry.value;
                          aset(entry.key, value);
                 }
             }
             } else { 
                 for (int i = 0; i < ltable.length; i++) {
                     for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                         aset(entry.key, entry.value);
         }
                 }
             }  
         } finally {otherHash.postIter();}
 
         return this;
     }
 
     /** rb_hash_merge
      * 
      */
     public RubyHash merge(IRubyObject other, Block block) {
         return ((RubyHash)dup()).update(other, block);
     }
 
     /** rb_hash_replace
      * 
      */
     public RubyHash replace(IRubyObject other) {
         RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         try {
             otherHash.preIter();
             RubyHashEntry[]ltable = otherHash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     aset(entry.key, entry.value);
                 }
             }
         } finally {otherHash.postIter();}
 
         ifNone = otherHash.ifNone;
 
         if ((otherHash.flags & PROCDEFAULT_HASH_F) != 0) {
             flags |= PROCDEFAULT_HASH_F;
         } else {
             flags &= ~PROCDEFAULT_HASH_F;
         }
 
         return this;
     }
 
     /** rb_hash_values_at
      * 
      */
     public RubyArray values_at(IRubyObject[] args) {
         RubyArray result = RubyArray.newArray(getRuntime(), args.length);
         for (int i = 0; i < args.length; i++) {
             result.append(aref(args[i]));
         }
         return result;
     }
 
     public boolean hasDefaultProc() {
         return (flags & PROCDEFAULT_HASH_F) != 0;
     }
 
     public IRubyObject getIfNone(){
         return ifNone;
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(RubyHash hash, MarshalStream output) throws IOException {
         output.writeInt(hash.size);
         try {
             hash.preIter();
             RubyHashEntry[]ltable = hash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = hash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     output.dumpObject(entry.key);
                     output.dumpObject(entry.value);
         }
         }
         } finally {hash.postIter();}         
 
         if (!hash.ifNone.isNil()) output.dumpObject(hash.ifNone);
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.aset(input.unmarshalObject(), input.unmarshalObject());
         }
         if (defaultValue) result.default_value_set(input.unmarshalObject());
         return result;
     }
 
     public Class getJavaClass() {
         return Map.class;
     }
 
     // Satisfy java.util.Set interface (for Java integration)
 
     public int size() {
         return size;
 	}
 
     public boolean isEmpty() {
         return size == 0;
     }    
 
 	public boolean containsKey(Object key) {
 		return internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)) != null;
 	}
 
 	public boolean containsValue(Object value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 		IRubyObject element = JavaUtil.convertJavaToRuby(runtime, value);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, element).isTrue()) return true;
 			}
 		}
         } finally {postIter();}        
 
 		return false;
 	}
 
 	public Object get(Object key) {
 		return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)));
 	}
 
 	public Object put(Object key, Object value) {
 		internalPut(JavaUtil.convertJavaToRuby(getRuntime(), key), JavaUtil.convertJavaToRuby(getRuntime(), value));
         return value;
 	}
 
 	public Object remove(Object key) {
         IRubyObject rubyKey = JavaUtil.convertJavaToRuby(getRuntime(), key);
         RubyHashEntry entry;
         if (iterLevel > 0) {
             entry = internalDeleteSafe(rubyKey);
             flags |= DELETED_HASH_F;
         } else {
             entry = internalDelete(rubyKey);
 	}
 		 
         return entry != null ? entry.value : null;
 	}
 
 	public void putAll(Map map) {
         Ruby runtime = getRuntime();
 		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
 			Object key = iter.next();
 			internalPut(JavaUtil.convertJavaToRuby(runtime, key), JavaUtil.convertJavaToRuby(runtime, map.get(key))); 
 		}
 	}
 
 	public void clear() {
         rb_clear();
 	}
 
     private abstract class RubyHashIterator implements Iterator {
         RubyHashEntry entry, current;
         int index;
         RubyHashEntry[]iterTable;
         Ruby runtime = getRuntime();
         
         public RubyHashIterator(){
             iterTable = table;
             if(size > 0) seekNextValidEntry();
 	}
 
         private final void seekNextValidEntry(){
             do {
                 while (index < iterTable.length && (entry = iterTable[index++]) == null);
                 while (entry != null && entry.key == NEVER) entry = entry.next;
             } while (entry == null && index < iterTable.length);
 	}
 
         public boolean hasNext() {
             return entry != null;
 	}
 
         public final RubyHashEntry nextEntry() {
             if (entry == null) throw new NoSuchElementException();
             RubyHashEntry e = current = entry;
             if ((entry = checkIter(iterTable, entry.next)) == null) seekNextValidEntry(); 
             return e;
         }
 
         public void remove() {
             if (current == null) throw new IllegalStateException();
             internalDeleteSafe(current.key);
             flags |= DELETED_HASH_F;
         }
         
     }
 
     private final class KeyIterator extends RubyHashIterator {
 					public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().key);
 					}
 			}
 
     private class KeySet extends AbstractSet {
         public Iterator iterator() {
             return new KeyIterator();
         }
 			public int size() {
             return size;
 			}
         public boolean contains(Object o) {
             return containsKey(o);
 			}
         public boolean remove(Object o) {
             return RubyHash.this.remove(o) != null;
 	}
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
 	public Set keySet() {
         return new KeySet();
 					}
 
     private final class DirectKeyIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().key;
 			}
 	}	
 
     private final class DirectKeySet extends KeySet {
         public Iterator iterator() {
             return new DirectKeyIterator();
         }        
 		}
 
     public Set directKeySet() {
         return new DirectKeySet();
 		}
 
     private final class ValueIterator extends RubyHashIterator {
 		public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().value);
 		}
 		}		
 
     private class Values extends AbstractCollection {
         public Iterator iterator() {
             return new ValueIterator();
         }
         public int size() {
             return size;
         }
         public boolean contains(Object o) {
             return containsValue(o);
             }
         public void clear() {
             RubyHash.this.clear();
         }
             }
 
     public Collection values() {
         return new Values();
         }
 
     private final class DirectValueIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().value;
 		}
         }
 
     private final class DirectValues extends Values {
         public Iterator iterator() {
             return new DirectValueIterator();
         }        
     }
 
     public Collection directValues() {
         return new DirectValues();
     }    
 
     static final class ConversionMapEntry implements Map.Entry {
         private final RubyHashEntry entry;
         private final Ruby runtime;
 
         public ConversionMapEntry(Ruby runtime, RubyHashEntry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
 
 				public Object getKey() {
             return JavaUtil.convertRubyToJava(entry.key, Object.class); 
 				}
 
 				public Object getValue() {
             return JavaUtil.convertRubyToJava(entry.value, Object.class);
 				}
 
         public Object setValue(Object value) {
             return entry.value = JavaUtil.convertJavaToRuby(runtime, value);            
 				}
 
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
             if(entry.key != NEVER && entry.key == otherEntry.key && entry.key.eql(otherEntry.key)){
                 if(entry.value == otherEntry.value || entry.value.equals(otherEntry.value)) return true;
             }            
             return false;
 		}
         public int hashCode(){
             return entry.hashCode();
         }
     }    
 
     private final class EntryIterator extends RubyHashIterator {
         public Object next() {
             return new ConversionMapEntry(runtime, nextEntry());
         }
     }
 
     private final class EntrySet extends AbstractSet {
         public Iterator iterator() {
             return new EntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof ConversionMapEntry))
                 return false;
             ConversionMapEntry entry = (ConversionMapEntry)o;
             if (entry.entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.entry.key);
             return candidate != null && candidate.equals(entry.entry);
         }
         public boolean remove(Object o) {
             if (!(o instanceof ConversionMapEntry)) return false;
             return internalDeleteEntry(((ConversionMapEntry)o).entry) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     public Set entrySet() {
         return new EntrySet();
     }    
 
     private final class DirectEntryIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry();
         }
     }    
 
     private final class DirectEntrySet extends AbstractSet {
         public Iterator iterator() {
             return new DirectEntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof RubyHashEntry))
                 return false;
             RubyHashEntry entry = (RubyHashEntry)o;
             if (entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.key);
             return candidate != null && candidate.equals(entry);
     }
         public boolean remove(Object o) {
             if (!(o instanceof RubyHashEntry)) return false;
             return internalDeleteEntry((RubyHashEntry)o) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     /** return an entry set who's entries do not convert their values, faster
      * 
      */
     public Set directEntrySet() {
         return new DirectEntrySet();
     }       
 
     public boolean equals(Object other){
         if (!(other instanceof RubyHash)) return false;
         if (this == other) return true;
         return equal((RubyHash)other).isTrue() ? true : false;
         }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index b8cea40461..b226610642 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -159,1312 +159,1322 @@ public class RubyObject implements Cloneable, IRubyObject {
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         if (runtime.getSafeLevel() >= 3) flags |= TAINTED_F;
     }
     
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObject.class);   
         objectClass.index = ClassIndex.OBJECT;
         
         objectClass.definePrivateMethod("initialize", callbackFactory.getOptMethod("initialize"));
         objectClass.definePrivateMethod("inherited", callbackFactory.getMethod("inherited", IRubyObject.class));
         
         return objectClass;
     }
     
     public static ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             IRubyObject instance = new RubyObject(runtime, klass);
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, RubyModule parent) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parent);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || 
                 other instanceof IRubyObject && 
                 callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return (flags & FROZEN_F) != 0;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         if (frozen) {
             flags |= FROZEN_F;
         } else {
             flags &= ~FROZEN_F;
     }
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return (flags & TAINTED_F) != 0; 
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         if (taint) {
             flags |= TAINTED_F;
         } else {
             flags &= ~TAINTED_F;
     }
     }
 
     public final boolean isNil() {
         return (flags & NIL_F) != 0;
     }
 
     public final boolean isTrue() {
         return (flags & FALSE_F) == 0;
     }
 
     public final boolean isFalse() {
         return (flags & FALSE_F) != 0;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     public boolean isKindOf(RubyModule type) {
         return type.kindOf.isKindOf(this, type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         if (original.safeHasInstanceVariables()) {
             clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         }
         
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, getMetaClass(), name, new IRubyObject[] { arg }, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, block);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return callMethod(context, getMetaClass(), methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg) {
         return callMethod(context,getMetaClass(),methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,callType, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name, IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, methodIndex, name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         if (context.getRuntime().hasEventHooks()) return callMethod(context, rubyclass, name, args, callType, block);
         
         return rubyclass.dispatcher.callMethod(context, this, rubyclass, methodIndex, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         
 
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(context.getFrameSelf(), callType))) {
             return callMethodMissing(context, this, method, name, args, context.getFrameSelf(), callType, block);
         }
 
         return method.call(context, this, rubyclass, name, args, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(self, callType))) {
             return callMethodMissing(context, this, method, name, args, self, callType, block);
         }
 
         return method.call(context, this, rubyclass, name, args, block);
     }
     
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, int methodIndex,
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (methodIndex == MethodIndex.METHOD_MISSING) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isValidInstanceVariableName(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject instance_variable_defined_p(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isValidInstanceVariableName(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
         return (variable != null) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isValidInstanceVariableName(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getRealClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType(getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     public RubyHash convertToHash() {
         return (RubyHash)convertToType(getRuntime().getHash(), MethodIndex.TO_HASH, "to_hash");
     }
     
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType(getRuntime().getClass("Float"), MethodIndex.TO_F, "to_f");
     }
 
     public RubyInteger convertToInteger() {
         return convertToInteger(MethodIndex.TO_INT, "to_int");
     }
 
     public RubyInteger convertToInteger(int convertMethodIndex, String convertMethod) {
         IRubyObject val = convertToType(getRuntime().getClass("Integer"), convertMethodIndex, convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType(getRuntime().getString(), MethodIndex.TO_STR, "to_str");
     }
 
     /** convert_type
      * 
      */
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex, String convertMethod, boolean raise) {
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 String type;
                 if (isNil()) {
                     type = "nil";
                 } else if (this instanceof RubyBoolean) {
                     type = isTrue() ? "true" : "false";
                 } else {
                     type = target.getName();
                 }
                 throw getRuntime().newTypeError("can't convert " + getMetaClass().getName() + " into " + type);
             } else {
                 return getRuntime().getNil();
             }
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethodIndex, convertMethod);
     }
     
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex) {
         return convertToType(target, convertMethodIndex, (String)MethodIndex.NAMES.get(convertMethodIndex));
     }
 
     /** rb_convert_type
      * 
      */
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex, String convertMethod) {
         if (isKindOf(target)) return this;
         IRubyObject val = convertToType(target, convertMethodIndex, convertMethod, true);
         if (!val.isKindOf(target)) throw getRuntime().newTypeError(getMetaClass() + "#" + convertMethod + " should return " + target.getName());
         return val;
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     /** rb_check_convert_type
      * 
      */
     public final IRubyObject convertToTypeWithCheck(RubyClass target, int convertMethodIndex, String convertMethod) {  
         if (isKindOf(target)) return this;
         IRubyObject val = convertToType(target, convertMethodIndex, convertMethod, false);
         if (val.isNil()) return val;
         if (!val.isKindOf(target)) throw getRuntime().newTypeError(getMetaClass() + "#" + convertMethod + " should return " + target.getName());
         return val;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         IRubyObject str = callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
         
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck(getRuntime().getString(), MethodIndex.TO_STR, "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck(getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, new IRubyObject[] { this }, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameName();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, filename.convertToString().toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, IRubyObject[] args, Block block) {
         final IRubyObject selfInYield = this;
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield;
                     boolean aValue;
                     if (args.length == 1) {
                         valueInYield = args[0];
                         aValue = false;
                     } else {
                         valueInYield = RubyArray.newArray(getRuntime(), args);
                         aValue = true;
                     }
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), aValue);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException.BreakJump bj) {
                         return (IRubyObject) bj.getValue();
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, args, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
             String file, int lineNumber) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         // FIXME:  This determine module is in a strange location and should somehow be in block
         blockOfBinding.getDynamicScope().getStaticScope().determineModule();
 
         try {
             // Binding provided for scope, use it
             context.preEvalWithBinding(blockOfBinding);
             IRubyObject newSelf = context.getFrameSelf();
             Node node = 
                 getRuntime().parseEval(src.toString(), file, blockOfBinding.getDynamicScope(), lineNumber);
 
             return EvaluationState.eval(getRuntime(), context, node, newSelf, blockOfBinding);
         } catch (JumpException.BreakJump bj) {
             throw getRuntime().newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw getRuntime().newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalWithBinding(blockOfBinding);
 
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             Node node = getRuntime().parseEval(src.toString(), file, context.getCurrentScope(), 0);
             
             return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException.BreakJump bj) {
             throw getRuntime().newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
             return getRuntime().getTrue();
 	}
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
     }
 
     /** rb_eql
      *  this method is not defind for Ruby objects directly.
      *  notably overriden by RubyFixnum, RubyString, RubySymbol - these do a short-circuit calls.
      *  see: rb_any_cmp() in hash.c
      *  do not confuse this method with eql_p methods (which it calls by default), eql is mainly used for hash key comparison 
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     public final boolean eqlInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return true;
         return callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return super.hashCode();
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone(Block unusedBlock) {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         initCopy(clone, this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         initCopy(dup, this);
 
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
+    /** inspect_obj
+     * 
+     */
+    private StringBuffer inspectObj(StringBuffer part) {
+        String sep = "";
+        Map iVars = getInstanceVariablesSnapshot();
+        for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
+            String name = (String) iter.next();
+            if(IdUtil.isInstanceVariable(name)) {
+                part.append(sep);
+                part.append(" ");
+                part.append(name);
+                part.append("=");
+                part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
+                sep = ",";
+            }
+        }
+        part.append(">");
+        return part;
+    }
+
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
+        Ruby runtime = getRuntime();
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
-                this != getRuntime().getObject() &&
-                this != getRuntime().getClass("Module") &&
+                this != runtime.getObject() &&
+                this != runtime.getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
-            if(!getRuntime().registerInspecting(this)) {
+
+            if (runtime.isInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
-                return getRuntime().newString(part.toString());
+                return runtime.newString(part.toString());
             }
             try {
-                String sep = "";
-                Map iVars = getInstanceVariablesSnapshot();
-                for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
-                    String name = (String) iter.next();
-                    if(IdUtil.isInstanceVariable(name)) {
-                        part.append(sep);
-                        part.append(" ");
-                        part.append(name);
-                        part.append("=");
-                        part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
-                        sep = ",";
-                    }
-                }
-                part.append(">");
-                return getRuntime().newString(part.toString());
+                runtime.registerInspecting(this);
+                return runtime.newString(inspectObj(part).toString());
             } finally {
-                getRuntime().unregisterInspecting(this);
+                runtime.unregisterInspecting(this);
             }
         }
-        
+
         if (isNil()) return RubyNil.inspect(this);
-        return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
+        return callMethod(runtime.getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!(type instanceof RubyModule)) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().protected_instance_methods(args);
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().private_instance_methods(args);
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject instance_exec(IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw getRuntime().newArgumentError("block not supplied");
         }
         return yieldUnder(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             IRubyObject obj;
             if (!(((obj = args[i]) instanceof RubyModule) && ((RubyModule)obj).isModule())){
                 throw getRuntime().newTypeError(obj,getRuntime().getClass("Module"));
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
     	return getRuntime().getNil();
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
  
     public void addFinalizer(RubyProc finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 }
diff --git a/test/test_array.rb b/test/test_array.rb
index 21b917b0a5..b546e13599 100644
--- a/test/test_array.rb
+++ b/test/test_array.rb
@@ -1,216 +1,224 @@
 require 'test/unit'
 
 class TestArray < Test::Unit::TestCase
 
   def test_unshift_and_leftshift_op
     arr = ["zero", "first"]
     arr.unshift "second", "third"
     assert_equal(["second", "third", "zero", "first"], arr)
     assert_equal(["first"], arr[-1..-1])
     assert_equal(["first"], arr[3..3])
     assert_equal([], arr[3..2])
     assert_equal([], arr[3..1])
     assert(["third", "zero", "first"] == arr[1..4])
     assert('["third", "zero", "first"]' == arr[1..4].inspect)
   
     arr << "fourth"
 
     assert("fourth" == arr.pop());
     assert("second" == arr.shift());
   end
   
   class MyArray < Array
     def [](arg)
       arg
     end
   end
 
   def test_aref
     assert_equal(nil, [].slice(-1..1))
     # test that overriding in child works correctly
     assert_equal(2, MyArray.new[2])
   end
 
   def test_class
     assert(Array == ["zero", "first"].class)
     assert("Array" == Array.to_s)
   end
 
   def test_dup_and_reverse
     arr = [1, 2, 3]
     arr2 = arr.dup
     arr2.reverse!
     assert_equal([1,2,3], arr)
     assert_equal([3,2,1], arr2)
 
     assert_equal([1,2,3], [1,2,3,1,2,3,1,1,1,2,3,2,1].uniq)
 
     assert_equal([1,2,3,4], [[[1], 2], [3, [4]]].flatten)
     assert_equal(nil, [].flatten!)
   end
   
   def test_delete
     arr = [1, 2, 3]
     arr2 = []
     arr.each { |x|
       arr2 << x
       arr.delete(x) if x == 2
     }
     assert_equal([1,2], arr2)
   end
   
   def test_fill
     arr = [1,2,3,4]
     arr.fill(1,10)
     assert_equal([1,2,3,4], arr)
     arr.fill(1,0)
     assert_equal([1,1,1,1], arr)
   end
   
   def test_flatten
     arr = []
     arr << [[[arr]]]
     assert_raises(ArgumentError) {
       arr.flatten
     }
   end
 
   # To test int coersion for indicies
   class IntClass
     def initialize(num); @num = num; end
     def to_int; @num; end; 
   end
 
   def test_conversion
     arr = [1, 2, 3]
 
     index = IntClass.new(1)
     arr[index] = 4
     assert_equal(4, arr[index])
     eindex = IntClass.new(2)
     arr[index, eindex] = 5
     assert_equal([1,5], arr)
     arr.delete_at(index)
     assert_equal([1], arr)
     arr = arr * eindex
     assert_equal([1, 1], arr)
   end
 
   def test_unshift_nothing
     assert_nothing_raised { [].unshift(*[]) }
     assert_nothing_raised { [].unshift() }
   end
 
   ##### Array#[] #####
 
   def test_indexing
     assert_equal([1], Array[1])
     assert_equal([], Array[])
     assert_equal([1,2], Array[1,2])
   end
 
   ##### insert ####
 
   def test_insert
     a = [10, 11]
     a.insert(1, 12)
     assert_equal([10, 12, 11], a)
     a = []
     a.insert(-1, 10)
     assert_equal([10], a)
     a.insert(-2, 11)
     assert_equal([11, 10], a)
     a = [10]
     a.insert(-1, 11)
     assert_equal([10, 11], a)
   end
 
   ##### == #####
   
   def test_ary
     o = Object.new
     def o.to_ary; end
     def o.==(o); true; end
     assert_equal(true, [].==(o))
   end
   
   # test that extensions of the base classes are typed correctly
   class ArrayExt < Array
   end
 
   def test_array_extension
     assert_equal(ArrayExt, ArrayExt.new.class)
     assert_equal(ArrayExt, ArrayExt[:foo, :bar].class)
   end
 
   ##### flatten #####
   def test_flatten
     a = [2,[3,[4]]]
     assert_equal([1,2,3,4],[1,a].flatten)
     assert_equal([2,[3,[4]]],a)
     a = [[1,2,[3,[4],[5]],6,[7,[8]]],9]
     assert_equal([1,2,3,4,5,6,7,8,9],a.flatten)
     assert(a.flatten!,"We did flatten")
     assert(!a.flatten!,"We didn't flatten")
   end
 
   ##### splat test #####
   class ATest
     def to_a; 1; end
   end
 
   def test_splatting
     proc { |a| assert_equal(1, a) }.call(*1)
     assert_raises(TypeError) { proc { |a| }.call(*ATest.new) }
   end
 
   #### index test ####
   class AlwaysEqual
     def ==(arg)
       true
     end
   end
 
   def test_index
     array_of_alwaysequal = [AlwaysEqual.new]
     # this should pass because index should call AlwaysEqual#== when searching
     assert_equal(0, array_of_alwaysequal.index("foo"))
     assert_equal(0, array_of_alwaysequal.rindex("foo"))
   end
 
   def test_spaceship
     assert_equal(0, [] <=> [])
     assert_equal(0, [1] <=> [1])
     assert_equal(-1, [1] <=> [2])
     assert_equal(1, [2] <=> [1])
     assert_equal(1, [1] <=> [])
     assert_equal(-1, [] <=> [1])
 
     assert_equal(0, [1, 1] <=> [1, 1])
     assert_equal(-1, [1, 1] <=> [1, 2])
 
     assert_equal(1, [1,6,1] <=> [1,5,0,1])
     assert_equal(-1, [1,5,0,1] <=> [1,6,1])
   end
   
   class BadComparator
     def <=>(other)
       "hello"
     end
   end
 
   def test_bad_comparator
     assert_equal("hello", [BadComparator.new] <=> [BadComparator.new])
   end
 
   def test_raises_stack_exception
     assert_raises(SystemStackError) { a = []; a << a; a <=> a }
   end
   
   def test_multiline_array_not_really_add
     assert_raises(NoMethodError) do
   	  [1,2,3]
   	  +[2,3]
   	end
   end
+
+  def test_recursive_join
+    arr = []
+    arr << [arr]
+    arr << 1
+    assert_equal("[...]1", arr.join)
+  end
+
 end
