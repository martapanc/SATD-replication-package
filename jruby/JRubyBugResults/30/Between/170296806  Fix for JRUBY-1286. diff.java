diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 5335a2201c..03b2af0c67 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1202,1266 +1202,1264 @@ public final class Ruby {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                     RubyClassPathVariable.createClassPathVariable(runtime);
                 }
             });
         
         registerBuiltin("socket.rb", new RubySocket.Service());
         registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
             if(profile.allowBuiltin(BUILTIN_LIBRARIES[i])) {
                 loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
             }
         }
 
         final Library NO_OP_LIBRARY = new Library() {
                 public void load(Ruby runtime) throws IOException {
                 }
             };
 
         registerBuiltin("jruby.rb", new LateLoadingLibrary("jruby", "org.jruby.libraries.JRubyLibrary", getJRubyClassLoader()));
         registerBuiltin("jruby/ext.rb", new LateLoadingLibrary("jruby/ext", "org.jruby.RubyJRuby$ExtLibrary", getJRubyClassLoader()));
         registerBuiltin("iconv.rb", new LateLoadingLibrary("iconv", "org.jruby.libraries.IConvLibrary", getJRubyClassLoader()));
         registerBuiltin("nkf.rb", new LateLoadingLibrary("nkf", "org.jruby.libraries.NKFLibrary", getJRubyClassLoader()));
         registerBuiltin("stringio.rb", new LateLoadingLibrary("stringio", "org.jruby.libraries.StringIOLibrary", getJRubyClassLoader()));
         registerBuiltin("strscan.rb", new LateLoadingLibrary("strscan", "org.jruby.libraries.StringScannerLibrary", getJRubyClassLoader()));
         registerBuiltin("zlib.rb", new LateLoadingLibrary("zlib", "org.jruby.libraries.ZlibLibrary", getJRubyClassLoader()));
         registerBuiltin("yaml_internal.rb", new LateLoadingLibrary("yaml_internal", "org.jruby.libraries.YamlLibrary", getJRubyClassLoader()));
         registerBuiltin("enumerator.rb", new LateLoadingLibrary("enumerator", "org.jruby.libraries.EnumeratorLibrary", getJRubyClassLoader()));
         registerBuiltin("generator_internal.rb", new LateLoadingLibrary("generator_internal", "org.jruby.ext.Generator$Service", getJRubyClassLoader()));
         registerBuiltin("readline.rb", new LateLoadingLibrary("readline", "org.jruby.ext.Readline$Service", getJRubyClassLoader()));
         registerBuiltin("thread.so", new LateLoadingLibrary("thread", "org.jruby.libraries.ThreadLibrary", getJRubyClassLoader()));
         registerBuiltin("openssl.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("rubygems"));
                     runtime.getTopSelf().callMethod(runtime.getCurrentContext(),"gem",runtime.newString("jruby-openssl"));
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("openssl.rb"));
                 }
             });
         registerBuiltin("digest.so", new LateLoadingLibrary("digest", "org.jruby.libraries.DigestLibrary", getJRubyClassLoader()));
         registerBuiltin("digest.rb", new LateLoadingLibrary("digest", "org.jruby.libraries.DigestLibrary", getJRubyClassLoader()));
         registerBuiltin("digest/md5.rb", new LateLoadingLibrary("digest/md5", "org.jruby.libraries.DigestLibrary$MD5", getJRubyClassLoader()));
         registerBuiltin("digest/rmd160.rb", new LateLoadingLibrary("digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160", getJRubyClassLoader()));
         registerBuiltin("digest/sha1.rb", new LateLoadingLibrary("digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1", getJRubyClassLoader()));
         registerBuiltin("digest/sha2.rb", new LateLoadingLibrary("digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2", getJRubyClassLoader()));
         registerBuiltin("bigdecimal.rb", new LateLoadingLibrary("bigdecimal", "org.jruby.libraries.BigDecimalLibrary", getJRubyClassLoader()));
         registerBuiltin("io/wait.so", new LateLoadingLibrary("io/wait", "org.jruby.libraries.IOWaitLibrary", getJRubyClassLoader()));
         registerBuiltin("etc.so", NO_OP_LIBRARY);
         registerBuiltin("fiber.so", new LateLoadingLibrary("fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader()));
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         undef = new RubyUndef();
 
         objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass; 
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
         
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
 
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         
         nilObject = new RubyNil(this);
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) RubyHash.createHashClass(this);
         if (profile.allowClass("Array")) RubyArray.createArrayClass(this);
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = newArray();
         for (int i = 0; i < argv.length; i++) {
             argvArray.add(newString(argv[i]));
         }
         defineGlobalConstant("ARGV", argvArray);
         getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
         
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
 
         ioClass = RubyIO.createIOClass(this);        
         
         if (profile.allowClass("Struct")) RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
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
         if (profile.allowClass("File::Stat")) RubyFileStat.createFileStatClass(this);
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
         RubyClass exceptionClass = fastGetClass("Exception");
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
         createSysErr(IErrno.ECONNABORTED, "ECONNABORTED");
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
 
     /** This method compiles and interprets a Ruby script.
      *
      *  It can be used if you want to use JRuby as a Macro language.
      *
      */
     public void loadFile(String scriptName, InputStream in) {
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
 
             Node node = parseFile(in, scriptName, null);
             ASTInterpreter.eval(this, context, node, self, Block.NULL_BLOCK);
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
     
     public void addFinalizer(Finalizable finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap<Finalizable, Object>();
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
 
     public RubyFileStat newRubyFileStat(String file) {
         return (RubyFileStat)fileStatClass.callMethod(getCurrentContext(),"new",newString(file));
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
-        RubyClass exc = fastGetClass("SystemExit");
-        IRubyObject[]exArgs = new IRubyObject[]{newFixnum(status), newString("exit")};
-        return new RaiseException((RubyException)exc.newInstance(exArgs, Block.NULL_BLOCK));
+        return new RaiseException(RubySystemExit.newInstance(this, status));
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
     
     private static POSIX loadPosix() {
         // check for native library support
         boolean nativeEnabled = true;
         if (System.getProperty("jruby.native.enabled") != null) {
             nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
         }
 
         if (nativeEnabled) {
             try {
                 // confirm we have library link permissions for the C library
                 System.getSecurityManager().checkLink("*");
                 HashMap options = new HashMap();
                 options.put(com.sun.jna.Library.OPTION_FUNCTION_MAPPER, new POSIXFunctionMapper());
 
                 boolean isWindows = System.getProperty("os.name").startsWith("Windows");
                 POSIX posix = (POSIX) Native.loadLibrary(isWindows ? "msvcrt" : "c", POSIX.class, options);
                 if (posix != null) {
                     return posix;
                 }
             } catch (Throwable t) {
             }
         }
         // on any error or if native is disabled, fall back on our own stupid POSIX impl
         return new JavaBasedPOSIX();
     }
     
     public static POSIX getPosix() {
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
 }
diff --git a/src/org/jruby/RubySystemExit.java b/src/org/jruby/RubySystemExit.java
index 9607da9987..4c68674a57 100644
--- a/src/org/jruby/RubySystemExit.java
+++ b/src/org/jruby/RubySystemExit.java
@@ -1,84 +1,92 @@
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
 
 package org.jruby;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubySystemExit extends RubyException {
     IRubyObject status;
 
     private static ObjectAllocator SYSTEMEXIT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubySystemExit(runtime, klass);
         }
     };    
     
     public static RubyClass createSystemExitClass(Ruby runtime, RubyClass exceptionClass) {
         RubyClass systemExitClass = runtime.defineClass("SystemExit", exceptionClass, SYSTEMEXIT_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubySystemExit.class);
 
         systemExitClass.defineAnnotatedMethods(RubySystemExit.class);
         
         return systemExitClass;
-    }    
+    }  
+    
+    public static RubySystemExit newInstance(Ruby runtime, int status) {
+        RubyClass exc = runtime.fastGetClass("SystemExit");
+        IRubyObject[] exArgs = new IRubyObject[] {
+                runtime.newFixnum(status),
+                runtime.newString("exit") };
+        return (RubySystemExit) exc.newInstance(exArgs, Block.NULL_BLOCK);
+    }
 
     protected RubySystemExit(Ruby runtime, RubyClass exceptionClass) {
         super(runtime, exceptionClass);
         status = runtime.getNil();
     }
 
     @JRubyMethod(name = "initialize", optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[]args, Block block) {
         status = RubyFixnum.zero(getRuntime());
         if (args.length > 0 && args[0] instanceof RubyFixnum) {
             status = args[0];
             IRubyObject[]tmpArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, tmpArgs, 0, tmpArgs.length);
             args = tmpArgs;
         }
         super.initialize(args, block);
         return this;
     }
 
     @JRubyMethod(name = "status")
     public IRubyObject status() {
         return status;
     }
 
     @JRubyMethod(name = "success?")
     public IRubyObject success_p() {
         if (status.isNil()) return getRuntime().getTrue();
         if (status.equals(RubyFixnum.zero(getRuntime()))) return getRuntime().getTrue();
         return getRuntime().getFalse();
     }
 
 }
\ No newline at end of file
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 61f8d1403d..d5ab3a3c0a 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,712 +1,714 @@
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
  * Copyright (C) 2002 Jason Voegele <jason@jvoegele.com>
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
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.TimeoutException;
 import java.util.concurrent.locks.ReentrantLock;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.Visibility;
 
 /**
  * Implementation of Ruby's <code>Thread</code> class.  Each Ruby thread is
  * mapped to an underlying Java Virtual Machine thread.
  * <p>
  * Thread encapsulates the behavior of a thread of execution, including the main
  * thread of the Ruby script.  In the descriptions that follow, the parameter
  * <code>aSymbol</code> refers to a symbol, which is either a quoted string or a
  * <code>Symbol</code> (such as <code>:name</code>).
  * 
  * Note: For CVS history, see ThreadClass.java.
  */
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private RubyFixnum priority;
     private Map threadLocalVariables = new HashMap();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private ThreadService threadService;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
     
     public final ReentrantLock lock = new ReentrantLock();
     
     private static boolean USE_POOLING;
     
     private static final boolean DEBUG = false;
     
    static {
        if (Ruby.isSecurityRestricted()) USE_POOLING = false;
        else USE_POOLING = Boolean.getBoolean("jruby.thread.pooling");
    }
    
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setThread(threadClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyThread.class);
 
         threadClass.defineAnnotatedMethods(RubyThread.class);
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
         threadClass.setMarshal(ObjectMarshal.NOT_MARSHALABLE_MARSHAL);
         
         return threadClass;
     }
 
     /**
      * <code>Thread.new</code>
      * <p>
      * Thread.new( <i>[ arg ]*</i> ) {| args | block } -> aThread
      * <p>
      * Creates a new thread to execute the instructions given in block, and
      * begins running it. Any arguments passed to Thread.new are passed into the
      * block.
      * <pre>
      * x = Thread.new { sleep .1; print "x"; print "y"; print "z" }
      * a = Thread.new { print "a"; print "b"; sleep .2; print "c" }
      * x.join # Let the threads finish before
      * a.join # main thread exits...
      * </pre>
      * <i>produces:</i> abxyzc
      */
     @JRubyMethod(name = {"new", "fork"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
     @JRubyMethod(name = "start", rest = true, frame = true, meta = true)
     public static RubyThread start(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, false, block);
     }
     
     public static RubyThread adopt(IRubyObject recv, Thread t) {
         return adoptThread(recv, t, Block.NULL_BLOCK);
     }
 
     private static RubyThread adoptThread(final IRubyObject recv, Thread t, Block block) {
         final Ruby runtime = recv.getRuntime();
         final RubyThread rubyThread = new RubyThread(runtime, (RubyClass) recv);
         
         rubyThread.threadImpl = new NativeThread(rubyThread, t);
         runtime.getThreadService().registerNewThread(rubyThread);
         
         runtime.getCurrentContext().preAdoptThread();
 
         // adoptThread does not call init, since init expects a block to be passed in
         if (USE_POOLING) {
             rubyThread.threadImpl = new FutureThread(rubyThread, new RubyRunnable(rubyThread, IRubyObject.NULL_ARRAY, block));
         } else {
             rubyThread.threadImpl = new NativeThread(rubyThread, new RubyNativeThread(rubyThread, IRubyObject.NULL_ARRAY, block));
         }
         rubyThread.threadImpl.start();
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw getRuntime().newThreadError("must be called with a block");
 
         if (USE_POOLING) {
             threadImpl = new FutureThread(this, new RubyRunnable(this, args, block));
         } else {
             threadImpl = new NativeThread(this, new RubyNativeThread(this, args, block));
         }
         threadImpl.start();
         
         return this;
     }
     
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) {
             rubyThread.callInit(args, block);
         } else {
             // for Thread::start, which does not call the subclass's initialize
             rubyThread.initialize(args, block);
         }
         
         return rubyThread;
     }
     
     private void ensureCurrent() {
         if (this != getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     private void ensureNotCurrent() {
         if (this == getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     public void cleanTerminate(IRubyObject result) {
         finalResult = result;
         isStopped = true;
     }
 
     public void pollThreadEvents() {
         // check for criticalization *before* locking ourselves
         threadService.waitForCritical();
 
         ensureCurrent();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before");
         if (killed) throw new ThreadKill();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " after");
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getKernel();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + killed);
             kernelModule.callMethod(getRuntime().getCurrentContext(), "raise", raiseException);
         }
     }
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getThreadGroup().fastGetConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
     }
 
     /**
      * Returns the status of the global ``abort on exception'' condition. The
      * default is false. When set to true, will cause all threads to abort (the
      * process will exit(0)) if an exception is raised in any thread. See also
      * Thread.abort_on_exception= .
      */
     @JRubyMethod(name = "abort_on_exception", meta = true)
     public static RubyBoolean abort_on_exception(IRubyObject recv) {
     	Ruby runtime = recv.getRuntime();
         return runtime.isGlobalAbortOnExceptionEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1, meta = true)
     public static IRubyObject abort_on_exception_set(IRubyObject recv, IRubyObject value) {
         recv.getRuntime().setGlobalAbortOnExceptionEnabled(value.isTrue());
         return value;
     }
 
     @JRubyMethod(name = "current", meta = true)
     public static RubyThread current(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getThread();
     }
 
     @JRubyMethod(name = "main", meta = true)
     public static RubyThread main(IRubyObject recv) {
         return recv.getRuntime().getThreadService().getMainThread();
     }
 
     @JRubyMethod(name = "pass", meta = true)
     public static IRubyObject pass(IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         ThreadService ts = runtime.getThreadService();
         boolean critical = ts.getCritical();
         
         ts.setCritical(false);
         
         Thread.yield();
         
         ts.setCritical(critical);
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "list", meta = true)
     public static RubyArray list(IRubyObject recv) {
     	RubyThread[] activeThreads = recv.getRuntime().getThreadService().getActiveRubyThreads();
         
         return recv.getRuntime().newArrayNoCopy(activeThreads);
     }
     
     private IRubyObject getSymbolKey(IRubyObject originalKey) {
         if (originalKey instanceof RubySymbol) {
             return originalKey;
         } else if (originalKey instanceof RubyString) {
             return getRuntime().fastNewSymbol(originalKey.asSymbol());
         } else if (originalKey instanceof RubyFixnum) {
             getRuntime().getWarnings().warn("Do not use Fixnums as Symbols");
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         } else {
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         }
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {
         key = getSymbolKey(key);
         
         if (!threadLocalVariables.containsKey(key)) {
             return getRuntime().getNil();
         }
         return (IRubyObject) threadLocalVariables.get(key);
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
         return value;
     }
 
     @JRubyMethod(name = "abort_on_exception")
     public RubyBoolean abort_on_exception() {
         return abortOnException ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1)
     public IRubyObject abort_on_exception_set(IRubyObject val) {
         abortOnException = val.isTrue();
         return val;
     }
 
     @JRubyMethod(name = "alive?")
     public RubyBoolean alive_p() {
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "join", optional = 1)
     public RubyThread join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
                 return null;
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         try {
             if (threadService.getCritical()) {
                 threadImpl.interrupt(); // break target thread out of critical
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException iExcptn) {
             assert false : iExcptn;
         } catch (TimeoutException iExcptn) {
             assert false : iExcptn;
         } catch (ExecutionException iExcptn) {
             assert false : iExcptn;
         }
         if (exitingException != null) {
             throw exitingException;
         }
         return null;
     }
 
     @JRubyMethod(name = "value")
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     @JRubyMethod(name = "group")
     public IRubyObject group() {
         if (threadGroup == null) {
         	return getRuntime().getNil();
         }
         
         return threadGroup;
     }
     
     void setThreadGroup(RubyThreadGroup rubyThreadGroup) {
     	threadGroup = rubyThreadGroup;
     }
     
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuffer part = new StringBuffer();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         
         if (threadImpl.isAlive()) {
             if (isStopped) {
                 part.append(getRuntime().newString(" sleep"));
             } else if (killed) {
                 part.append(getRuntime().newString(" aborting"));
             } else {
                 part.append(getRuntime().newString(" run"));
             }
         } else {
             part.append(" dead");
         }
         
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     @JRubyMethod(name = "key?", required = 1)
     public RubyBoolean key_p(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     @JRubyMethod(name = "critical=", required = 1, meta = true)
     public static IRubyObject critical_set(IRubyObject receiver, IRubyObject value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     @JRubyMethod(name = "critical", meta = true)
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     @JRubyMethod(name = "stop", meta = true)
     public static IRubyObject stop(IRubyObject receiver) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         Object stopLock = rubyThread.stopLock;
         
         synchronized (stopLock) {
             try {
                 rubyThread.isStopped = true;
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
                 
                 stopLock.wait();
             } catch (InterruptedException ie) {
                 // ignore, continue;
             }
             rubyThread.isStopped = false;
         }
         
         return receiver.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "kill", required = 1, frame = true, meta = true)
     public static IRubyObject kill(IRubyObject receiver, IRubyObject rubyThread, Block block) {
         if (!(rubyThread instanceof RubyThread)) throw receiver.getRuntime().newTypeError(rubyThread, receiver.getRuntime().getThread());
         return ((RubyThread)rubyThread).kill();
     }
     
     @JRubyMethod(name = "exit", frame = true, meta = true)
     public static IRubyObject s_exit(IRubyObject receiver, Block block) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         
         rubyThread.killed = true;
         // attempt to decriticalize all if we're the critical thread
         receiver.getRuntime().getThreadService().setCritical(false);
         
         throw new ThreadKill();
     }
 
     @JRubyMethod(name = "stop?")
     public RubyBoolean stop_p() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     @JRubyMethod(name = "wakeup")
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
     	return this;
     }
     
     @JRubyMethod(name = "priority")
     public RubyFixnum priority() {
         return priority;
     }
 
     @JRubyMethod(name = "priority=", required = 1)
     public IRubyObject priority_set(IRubyObject priority) {
         // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
         int iPriority = RubyNumeric.fix2int(priority);
         
         if (iPriority < Thread.MIN_PRIORITY) {
             iPriority = Thread.MIN_PRIORITY;
         } else if (iPriority > Thread.MAX_PRIORITY) {
             iPriority = Thread.MAX_PRIORITY;
         }
         
         this.priority = RubyFixnum.newFixnum(getRuntime(), iPriority);
         
         if (threadImpl.isAlive()) {
             threadImpl.setPriority(iPriority);
         }
         return this.priority;
     }
 
     @JRubyMethod(name = "raise", optional = 1, frame = true)
     public IRubyObject raise(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         ensureNotCurrent();
         Ruby runtime = getRuntime();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         try {
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
             receivedException = prepareRaiseException(runtime, args, block);
 
             // interrupt the target thread in case it's blocking or waiting
             threadImpl.interrupt();
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 3); 
 
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.fastGetClass("RuntimeError").newInstance(args, block);
             }
             
             if(!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getException())) {
             return runtime.newTypeError("exception object expected").getException();
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         return exception;
     }
     
     @JRubyMethod(name = "run")
     public IRubyObject run() {
         // if stopped, unstop
         synchronized (stopLock) {
             if (isStopped) {
                 isStopped = false;
                 stopLock.notifyAll();
             }
         }
     	
     	return this;
     }
     
     public void sleep(long millis) throws InterruptedException {
         ensureCurrent();
         synchronized (stopLock) {
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     @JRubyMethod(name = "status")
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
         	if (isStopped) {
             	return getRuntime().newString("sleep");
             } else if (killed) {
                 return getRuntime().newString("aborting");
             }
         	
             return getRuntime().newString("run");
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newBoolean(false);
         }
     }
 
     @JRubyMethod(name = {"kill", "exit", "terminate"})
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         try {
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
 
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
             killed = true;
 
             threadImpl.interrupt(); // break out of wait states and blocking IO
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
         
         try {
             threadImpl.join();
         } catch (InterruptedException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         } catch (ExecutionException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         }
         
         return this;
     }
     
     @JRubyMethod(name = {"kill!", "exit!", "terminate!"})
     public IRubyObject kill_bang() {
         throw getRuntime().newNotImplementedError("Thread#kill!, exit!, and terminate! are not safe and not supported");
     }
     
     @JRubyMethod(name = "safe_level")
     public IRubyObject safe_level() {
         throw getRuntime().newNotImplementedError("Thread-specific SAFE levels are not supported");
     }
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
-        Ruby runtime = exception.getException().getRuntime();
-        if (abortOnException(runtime)) {
+        RubyException rubyException = exception.getException();
+        Ruby runtime = rubyException.getRuntime();
+        if (rubyException.isKindOf(runtime.fastGetClass("SystemExit"))) {
+            threadService.getMainThread().raise(new IRubyObject[] {rubyException}, Block.NULL_BLOCK);
+        } else if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
-        	// TODO: Doesn't SystemExit have its own method to make this less wordy..
-            RubyException re = RubyException.newException(getRuntime(), getRuntime().fastGetClass("SystemExit"), exception.getMessage());
-            re.fastSetInternalVariable("status", getRuntime().newFixnum(1));
-            threadService.getMainThread().raise(new IRubyObject[] {re}, Block.NULL_BLOCK);
+            RubyException systemExit = RubySystemExit.newInstance(runtime, 1);
+            systemExit.message = rubyException.message;
+            threadService.getMainThread().raise(new IRubyObject[] {systemExit}, Block.NULL_BLOCK);
             return;
         } else if (runtime.getDebug().isTrue()) {
             runtime.printError(exception.getException());
         }
         exitingException = exception;
     }
 
     private boolean abortOnException(Ruby runtime) {
         return (runtime.isGlobalAbortOnExceptionEnabled() || abortOnException);
     }
 
     public static RubyThread mainThread(IRubyObject receiver) {
         return receiver.getRuntime().getThreadService().getMainThread();
     }
 }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index 5c27c71cf1..b2c82bc16b 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1781,1409 +1781,1411 @@ public class ASTCompiler {
         final ArrayCallback dstrCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(dxstrNode.get(index), context);
                     }
                 };
 
         ClosureCallback argsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         context.createNewString(dstrCallback, dxstrNode.size());
                         context.createObjectArray(1);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
 
     public static void compileEnsureNode(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final EnsureNode ensureNode = (EnsureNode) node;
 
         if (ensureNode.getEnsureNode() != null) {
             context.protect(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             if (ensureNode.getBodyNode() != null) {
                                 compile(ensureNode.getBodyNode(), context);
                             } else {
                                 context.loadNil();
                             }
                         }
                     },
                     new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(ensureNode.getEnsureNode(), context);
                             context.consumeCurrentValue();
                         }
                     }, IRubyObject.class);
         } else {
             if (ensureNode.getBodyNode() != null) {
                 compile(ensureNode.getBodyNode(), context);
             } else {
                 context.loadNil();
             }
         }
     }
 
     public static void compileEvStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final EvStrNode evStrNode = (EvStrNode) node;
 
         compile(evStrNode.getBody(), context);
         context.asString();
     }
 
     public static void compileFalse(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadFalse();
 
         context.pollThreadEvents();
     }
 
     public static void compileFCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final FCallNode fcallNode = (FCallNode) node;
 
         ClosureCallback argsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         compileArguments(fcallNode.getArgsNode(), context);
                     }
                 };
 
         if (fcallNode.getIterNode() == null) {
             // no block, go for simple version
             if (fcallNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, null, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, null, CallType.FUNCTIONAL, null, false);
             }
         } else {
             ClosureCallback closureArg = getBlock(fcallNode.getIterNode());
 
             if (fcallNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, argsCallback, CallType.FUNCTIONAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(fcallNode.getName(), null, null, CallType.FUNCTIONAL, closureArg, false);
             }
         }
     }
 
     private static ClosureCallback getBlock(Node node) {
         if (node == null) {
             return null;
         }
 
         switch (node.nodeId) {
             case ITERNODE:
                 final IterNode iterNode = (IterNode) node;
 
                 return new ClosureCallback() {
 
                             public void compile(MethodCompiler context) {
                                 ASTCompiler.compile(iterNode, context);
                             }
                         };
             case BLOCKPASSNODE:
                 final BlockPassNode blockPassNode = (BlockPassNode) node;
 
                 return new ClosureCallback() {
 
                             public void compile(MethodCompiler context) {
                                 ASTCompiler.compile(blockPassNode.getBodyNode(), context);
                                 context.unwrapPassedBlock();
                             }
                         };
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public static void compileFixnum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         FixnumNode fixnumNode = (FixnumNode) node;
 
         context.createNewFixnum(fixnumNode.getValue());
     }
 
     public static void compileFlip(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final FlipNode flipNode = (FlipNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
 
         if (flipNode.isExclusive()) {
             context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                         }
                                     });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             becomeTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                         }
                     });
         } else {
             context.performBooleanBranch(new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                         }
                                     });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
 
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             context.performBooleanBranch(new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             compile(flipNode.getEndNode(), context);
                                             flipTrueOrFalse(context);
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                             context.loadTrue();
                                         }
                                     }, new BranchCallback() {
 
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                         }
                                     });
                         }
                     });
         }
     }
 
     private static void becomeTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
 
     private static void flipTrueOrFalse(MethodCompiler context) {
         context.performBooleanBranch(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
 
     public static void compileFloat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         FloatNode floatNode = (FloatNode) node;
 
         context.createNewFloat(floatNode.getValue());
     }
 
     public static void compileFor(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ForNode forNode = (ForNode) node;
 
         ClosureCallback receiverCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         ASTCompiler.compile(forNode.getIterNode(), context);
                     }
                 };
 
         final ClosureCallback closureArg = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         compileForIter(forNode, context);
                     }
                 };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, false);
     }
 
     public static void compileForIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (forNode.getBodyNode() != null) {
                             ASTCompiler.compile(forNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final ClosureCallback closureArgs = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (forNode.getVarNode() != null) {
                             compileAssignment(forNode.getVarNode(), context);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().nodeId;
         }
 
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId);
         } else {
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId);
         }
     }
 
     public static void compileGlobalAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         compile(globalAsgnNode.getValueNode(), context);
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().assignLastLine();
                     return;
                 case '~':
                     assert false : "Parser shouldn't allow assigning to $~";
                     return;
                 default:
                 // fall off the end, handle it as a normal global
             }
         }
 
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
 
     public static void compileGlobalAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
 
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().assignLastLine();
                     return;
                 case '~':
                     assert false : "Parser shouldn't allow assigning to $~";
                     return;
                 default:
                 // fall off the end, handle it as a normal global
             }
         }
 
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
 
     public static void compileGlobalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         GlobalVarNode globalVarNode = (GlobalVarNode) node;
 
         if (globalVarNode.getName().length() == 2) {
             switch (globalVarNode.getName().charAt(1)) {
                 case '_':
                     context.getVariableCompiler().retrieveLastLine();
                     return;
                 case '~':
                     context.getVariableCompiler().retrieveBackRef();
                     return;
             }
         }
 
         context.retrieveGlobalVariable(globalVarNode.getName());
     }
 
     public static void compileHash(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         HashNode hashNode = (HashNode) node;
 
         if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
             context.createEmptyHash();
             return;
         }
 
         ArrayCallback hashCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode listNode = (ListNode) sourceArray;
                         int keyIndex = index * 2;
                         compile(listNode.get(keyIndex), context);
                         compile(listNode.get(keyIndex + 1), context);
                     }
                 };
 
         context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
     }
 
     public static void compileIf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final IfNode ifNode = (IfNode) node;
 
         compile(ifNode.getCondition(), context);
 
         BranchCallback trueCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (ifNode.getThenBody() != null) {
                             compile(ifNode.getThenBody(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         BranchCallback falseCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (ifNode.getElseBody() != null) {
                             compile(ifNode.getElseBody(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.performBooleanBranch(trueCallback, falseCallback);
     }
 
     public static void compileInstAsgn(Node node, MethodCompiler context) {
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
 
         compile(instAsgnNode.getValueNode(), context);
 
         compileInstAsgnAssignment(node, context);
     }
 
     public static void compileInstAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
     }
 
     public static void compileInstVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         InstVarNode instVarNode = (InstVarNode) node;
 
         context.retrieveInstanceVariable(instVarNode.getName());
     }
 
     public static void compileIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (iterNode.getBodyNode() != null) {
                             ASTCompiler.compile(iterNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         // create the closure class and instantiate it
         final ClosureCallback closureArgs = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (iterNode.getVarNode() != null) {
                             compileAssignment(iterNode.getVarNode(), context);
                         }
                     }
                 };
 
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
             hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
 
         NodeType argsNodeId = null;
         if (iterNode.getVarNode() != null && iterNode.getVarNode().nodeId != NodeType.ZEROARGNODE) {
             // if we have multiple asgn with just *args, need a special type for that
             argsNodeId = iterNode.getVarNode().nodeId;
             if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
                 MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
                 if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
                     // FIXME: This is gross. Don't do this.
                     argsNodeId = NodeType.SVALUENODE;
                 }
             }
         }
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(iterNode.getBodyNode());
         inspector.inspect(iterNode.getVarNode());
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewClosure(iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId, inspector);
         } else {
             context.createNewClosure(iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId, inspector);
         }
     }
 
     public static void compileLocalAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         compile(localAsgnNode.getValueNode(), context);
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
 
     public static void compileLocalAsgnAssignment(Node node, MethodCompiler context) {
         // "assignment" means the value is already on the stack
         context.lineNumber(node.getPosition());
 
         LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
 
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
 
     public static void compileLocalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         LocalVarNode localVarNode = (LocalVarNode) node;
 
         context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
     }
 
     public static void compileMatch(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context);
 
         context.match();
     }
 
     public static void compileMatch2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
 
         context.match2();
     }
 
     public static void compileMatch3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
 
         context.match3();
     }
 
     public static void compileModule(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final ModuleNode moduleNode = (ModuleNode) node;
 
         final Node cpathNode = moduleNode.getCPath();
 
         ClosureCallback bodyCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (moduleNode.getBodyNode() != null) {
                             ASTCompiler.compile(moduleNode.getBodyNode(), context);
                         }
                         context.loadNil();
                     }
                 };
 
         ClosureCallback pathCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (cpathNode instanceof Colon2Node) {
                             Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
                             if (leftNode != null) {
                                 ASTCompiler.compile(leftNode, context);
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
 
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback);
     }
 
     public static void compileMultipleAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         // FIXME: This is a little less efficient than it could be, since in the interpreter we avoid objectspace for these arrays
         compile(multipleAsgnNode.getValueNode(), context);
 
         compileMultipleAsgnAssignment(node, context);
     }
 
     public static void compileMultipleAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
 
         context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
 
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         compileAssignment(assignNode, context);
                     }
                 };
 
         // head items for which we've run out of assignable elements
         ArrayCallback headNilCallback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         ListNode headNode = (ListNode) sourceArray;
                         Node assignNode = headNode.get(index);
 
                         // perform assignment for the next node
                         context.loadNil();
                         compileAssignment(assignNode, context);
                     }
                 };
 
         ClosureCallback argsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         Node argsNode = multipleAsgnNode.getArgsNode();
                         if (argsNode instanceof StarNode) {
                         // done processing args
                         } else {
                             // assign to appropriate variable
                             ASTCompiler.compileAssignment(argsNode, context);
                         }
                     }
                 };
 
         if (multipleAsgnNode.getHeadNode() == null) {
             if (multipleAsgnNode.getArgsNode() == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             } else {
                 context.forEachInValueArray(0, 0, null, null, null, argsCallback);
             }
         } else {
             if (multipleAsgnNode.getArgsNode() == null) {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, headNilCallback, null);
             } else {
                 context.forEachInValueArray(0, multipleAsgnNode.getHeadNode().size(), multipleAsgnNode.getHeadNode(), headAssignCallback, headNilCallback, argsCallback);
             }
         }
     }
 
     public static void compileNewline(Node node, MethodCompiler context) {
         // TODO: add trace call?
         context.lineNumber(node.getPosition());
 
         context.setPosition(node.getPosition());
 
         NewlineNode newlineNode = (NewlineNode) node;
 
         compile(newlineNode.getNextNode(), context);
     }
 
     public static void compileNext(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final NextNode nextNode = (NextNode) node;
 
         ClosureCallback valueCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (nextNode.getValueNode() != null) {
                             ASTCompiler.compile(nextNode.getValueNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
 
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
     }
 
     public static void compileNthRef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         NthRefNode nthRefNode = (NthRefNode) node;
 
         context.nthRef(nthRefNode.getMatchNumber());
     }
 
     public static void compileNil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadNil();
 
         context.pollThreadEvents();
     }
 
     public static void compileNot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         NotNode notNode = (NotNode) node;
 
         compile(notNode.getConditionNode(), context);
 
         context.negateCurrentValue();
     }
 
     public static void compileOpAsgnAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
 
         compile(andNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(andNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
     }
 
     public static void compileOpAsgnOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         compileGetDefinitionBase(orNode.getFirstNode(), context);
 
         context.isNull(new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getSecondNode(), context);
                     }
                 }, new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getFirstNode(), context);
                         context.duplicateCurrentValue();
                         context.performBooleanBranch(new BranchCallback() {
 
                                     public void branch(MethodCompiler context) {
                                     //Do nothing
                                     }
                                 },
                                 new BranchCallback() {
 
                                     public void branch(MethodCompiler context) {
                                         context.consumeCurrentValue();
                                         compile(orNode.getSecondNode(), context);
                                     }
                                 });
                     }
                 });
 
         context.pollThreadEvents();
     }
 
     public static void compileOpAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         // FIXME: This is a little more complicated than it needs to be; do we see now why closures would be nice in Java?
 
         final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
 
         final ClosureCallback receiverCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         ASTCompiler.compile(opAsgnNode.getReceiverNode(), context); // [recv]
                         context.duplicateCurrentValue(); // [recv, recv]
                     }
                 };
 
         BranchCallback doneBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // get rid of extra receiver, leave the variable result present
                         context.swapValues();
                         context.consumeCurrentValue();
                     }
                 };
 
         // Just evaluate the value and stuff it in an argument array
         final ArrayCallback justEvalValue = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray,
                             int index) {
                         compile(((Node[]) sourceArray)[index], context);
                     }
                 };
 
         BranchCallback assignBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         // eliminate extra value, eval new one and assign
                         context.consumeCurrentValue();
                         context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
                         context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
                     }
                 };
 
         ClosureCallback receiver2Callback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         context.getInvocationCompiler().invokeDynamic(opAsgnNode.getVariableName(), receiverCallback, null, CallType.FUNCTIONAL, null, false);
                     }
                 };
 
         if (opAsgnNode.getOperatorName() == "||") {
             // if lhs is true, don't eval rhs and assign
             receiver2Callback.compile(context);
             context.duplicateCurrentValue();
             context.performBooleanBranch(doneBranch, assignBranch);
         } else if (opAsgnNode.getOperatorName() == "&&") {
             // if lhs is true, eval rhs and assign
             receiver2Callback.compile(context);
             context.duplicateCurrentValue();
             context.performBooleanBranch(assignBranch, doneBranch);
         } else {
             // eval new value, call operator on old value, and assign
             ClosureCallback argsCallback = new ClosureCallback() {
 
                         public void compile(MethodCompiler context) {
                             context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
                         }
                     };
             context.getInvocationCompiler().invokeDynamic(opAsgnNode.getOperatorName(), receiver2Callback, argsCallback, CallType.FUNCTIONAL, null, false);
             context.createObjectArray(1);
             context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
         }
 
         context.pollThreadEvents();
     }
 
     public static void compileOpElementAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
 
         compile(opElementAsgnNode.getReceiverNode(), context);
         compileArguments(opElementAsgnNode.getArgsNode(), context);
 
         ClosureCallback valueArgsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         ASTCompiler.compile(opElementAsgnNode.getValueNode(), context);
                     }
                 };
 
         context.getInvocationCompiler().opElementAsgn(valueArgsCallback, opElementAsgnNode.getOperatorName());
     }
 
     public static void compileOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final OrNode orNode = (OrNode) node;
 
         compile(orNode.getFirstNode(), context);
 
         BranchCallback longCallback = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(orNode.getSecondNode(), context);
                     }
                 };
 
         context.performLogicalOr(longCallback);
     }
 
     public static void compilePostExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final PostExeNode postExeNode = (PostExeNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (postExeNode.getBodyNode() != null) {
                             ASTCompiler.compile(postExeNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.createNewEndBlock(closureBody);
     }
 
     public static void compilePreExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final PreExeNode preExeNode = (PreExeNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         if (preExeNode.getBodyNode() != null) {
                             ASTCompiler.compile(preExeNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
                     }
                 };
         context.runBeginBlock(preExeNode.getScope(), closureBody);
     }
 
     public static void compileRedo(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         //RedoNode redoNode = (RedoNode)node;
 
         context.issueRedoEvent();
     }
 
     public static void compileRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         RegexpNode reNode = (RegexpNode) node;
 
         int opts = reNode.getOptions();
         String lang = ((opts & 16) == 16) ? "n" : null;
         if ((opts & 48) == 48) { // param s
             lang = "s";
         } else if ((opts & 32) == 32) { // param e
             lang = "e";
         } else if ((opts & 64) != 0) { // param u
             lang = "u";
         }
 
         context.createNewRegexp(reNode.getValue(), reNode.getOptions(), lang);
     }
 
     public static void compileRescue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final RescueNode rescueNode = (RescueNode) node;
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueNode.getBodyNode() != null) {
                             compile(rescueNode.getBodyNode(), context);
                         } else {
                             context.loadNil();
                         }
 
                         if (rescueNode.getElseNode() != null) {
                             context.consumeCurrentValue();
                             compile(rescueNode.getElseNode(), context);
                         }
                     }
                 };
 
         BranchCallback handler = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         context.loadException();
                         context.unwrapRaiseException();
+                        context.duplicateCurrentValue();
                         context.assignGlobalVariable("$!");
                         context.consumeCurrentValue();
+                        context.rethrowIfSystemExit();
                         compileRescueBody(rescueNode.getRescueNode(), context);
                     }
                 };
 
         context.rescue(body, RaiseException.class, handler, IRubyObject.class);
     }
 
     public static void compileRescueBody(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final RescueBodyNode rescueBodyNode = (RescueBodyNode) node;
 
         context.loadException();
         context.unwrapRaiseException();
 
         Node exceptionList = rescueBodyNode.getExceptionNodes();
         if (exceptionList == null) {
             context.loadClass("StandardError");
             context.createObjectArray(1);
         } else {
             compileArguments(exceptionList, context);
         }
 
         context.checkIsExceptionHandled();
 
         BranchCallback trueBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueBodyNode.getBodyNode() != null) {
                             compile(rescueBodyNode.getBodyNode(), context);
                             context.loadNil();
                             // FIXME: this should reset to what it was before
                             context.assignGlobalVariable("$!");
                             context.consumeCurrentValue();
                         } else {
                             context.loadNil();
                             // FIXME: this should reset to what it was before
                             context.assignGlobalVariable("$!");
                         }
                     }
                 };
 
         BranchCallback falseBranch = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (rescueBodyNode.getOptRescueNode() != null) {
                             compileRescueBody(rescueBodyNode.getOptRescueNode(), context);
                         } else {
                             context.rethrowException();
                         }
                     }
                 };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
 
     public static void compileRetry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.pollThreadEvents();
 
         context.issueRetryEvent();
     }
 
     public static void compileReturn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ReturnNode returnNode = (ReturnNode) node;
 
         if (returnNode.getValueNode() != null) {
             compile(returnNode.getValueNode(), context);
         } else {
             context.loadNil();
         }
 
         context.performReturn();
     }
 
     public static void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector) {
         RootNode rootNode = (RootNode) node;
 
         context.startScript(rootNode.getStaticScope());
 
         // create method for toplevel of script
         MethodCompiler methodCompiler = context.startMethod("__file__", null, rootNode.getStaticScope(), inspector);
 
         Node nextNode = rootNode.getBodyNode();
         if (nextNode != null) {
             if (nextNode.nodeId == NodeType.BLOCKNODE) {
                 // it's a multiple-statement body, iterate over all elements in turn and chain if it get too long
                 BlockNode blockNode = (BlockNode) nextNode;
 
                 for (int i = 0; i < blockNode.size(); i++) {
                     if ((i + 1) % 500 == 0) {
                         methodCompiler = methodCompiler.chainToMethod("__file__from_line_" + (i + 1), inspector);
                     }
                     compile(blockNode.get(i), methodCompiler);
 
                     if (i + 1 < blockNode.size()) {
                         // clear result from previous line
                         methodCompiler.consumeCurrentValue();
                     }
                 }
             } else {
                 // single-statement body, just compile it
                 compile(nextNode, methodCompiler);
             }
         } else {
             methodCompiler.loadNil();
         }
 
         methodCompiler.endMethod();
 
         context.endScript();
     }
 
     public static void compileSelf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.retrieveSelf();
     }
 
     public static void compileSplat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context);
 
         context.splatCurrentValue();
     }
 
     public static void compileStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         StrNode strNode = (StrNode) node;
 
         context.createNewString(strNode.getValue());
     }
 
     public static void compileSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final SuperNode superNode = (SuperNode) node;
 
         ClosureCallback argsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         compileArguments(superNode.getArgsNode(), context);
                     }
                 };
 
 
         if (superNode.getIterNode() == null) {
             // no block, go for simple version
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, null);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, null);
             }
         } else {
             ClosureCallback closureArg = getBlock(superNode.getIterNode());
 
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, closureArg);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, closureArg);
             }
         }
     }
 
     public static void compileSValue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SValueNode svalueNode = (SValueNode) node;
 
         compile(svalueNode.getValue(), context);
 
         context.singlifySplattedValue();
     }
 
     public static void compileSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.createNewSymbol(((SymbolNode) node).getName());
     }    
     
     public static void compileToAry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ToAryNode toAryNode = (ToAryNode) node;
 
         compile(toAryNode.getValue(), context);
 
         context.aryToAry();
     }
 
     public static void compileTrue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.loadTrue();
 
         context.pollThreadEvents();
     }
 
     public static void compileUndef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.undefMethod(((UndefNode) node).getName());
     }
 
     public static void compileUntil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final UntilNode untilNode = (UntilNode) node;
 
         BranchCallback condition = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(untilNode.getConditionNode(), context);
                         context.negateCurrentValue();
                     }
                 };
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (untilNode.getBodyNode() == null) {
                             context.loadNil();
                             return;
                         }
                         compile(untilNode.getBodyNode(), context);
                     }
                 };
 
         context.performBooleanLoop(condition, body, untilNode.evaluateAtStart());
 
         context.pollThreadEvents();
     }
 
     public static void compileVAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         VAliasNode valiasNode = (VAliasNode) node;
 
         context.aliasGlobal(valiasNode.getNewName(), valiasNode.getOldName());
     }
 
     public static void compileVCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         VCallNode vcallNode = (VCallNode) node;
 
         context.getInvocationCompiler().invokeDynamic(vcallNode.getName(), null, null, CallType.VARIABLE, null, false);
     }
 
     public static void compileWhile(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final WhileNode whileNode = (WhileNode) node;
 
         BranchCallback condition = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         compile(whileNode.getConditionNode(), context);
                     }
                 };
 
         BranchCallback body = new BranchCallback() {
 
                     public void branch(MethodCompiler context) {
                         if (whileNode.getBodyNode() == null) {
                             context.loadNil();
                         } else {
                             compile(whileNode.getBodyNode(), context);
                         }
                     }
                 };
 
         context.performBooleanLoop(condition, body, whileNode.evaluateAtStart());
 
         context.pollThreadEvents();
     }
 
     public static void compileXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         final XStrNode xstrNode = (XStrNode) node;
 
         ClosureCallback argsCallback = new ClosureCallback() {
 
                     public void compile(MethodCompiler context) {
                         context.createNewString(xstrNode.getValue());
                         context.createObjectArray(1);
                     }
                 };
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
 
     public static void compileYield(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         YieldNode yieldNode = (YieldNode) node;
 
         if (yieldNode.getArgsNode() != null) {
             compile(yieldNode.getArgsNode(), context);
         }
 
         context.getInvocationCompiler().yield(yieldNode.getArgsNode() != null, yieldNode.getCheckState());
     }
 
     public static void compileZArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         context.createEmptyArray();
     }
 
     public static void compileZSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ZSuperNode zsuperNode = (ZSuperNode) node;
 
         ClosureCallback closure = getBlock(zsuperNode.getIterNode());
 
         context.callZSuper(closure);
     }
 
     public static void compileArgsCatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsCatNode argsCatNode = (ArgsCatNode) node;
 
         compileArguments(argsCatNode.getFirstNode(), context);
         // arguments compilers always create IRubyObject[], but we want to use RubyArray.concat here;
         // FIXME: as a result, this is NOT efficient, since it creates and then later unwraps an array
         context.createNewArray(true);
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
         context.convertToJavaArray();
     }
 
     public static void compileArgsPushArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArgsPushNode argsPushNode = (ArgsPushNode) node;
         compile(argsPushNode.getFirstNode(), context);
         compile(argsPushNode.getSecondNode(), context);
         context.appendToArray();
         context.convertToJavaArray();
     }
 
     public static void compileArrayArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         ArrayNode arrayNode = (ArrayNode) node;
 
         ArrayCallback callback = new ArrayCallback() {
 
                     public void nextValue(MethodCompiler context, Object sourceArray, int index) {
                         Node node = (Node) ((Object[]) sourceArray)[index];
                         compile(node, context);
                     }
                 };
 
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
     // leave as a normal array
     }
 
     public static void compileSplatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
         SplatNode splatNode = (SplatNode) node;
 
         compile(splatNode.getValue(), context);
         context.splatCurrentValue();
         context.convertToJavaArray();
     }
 
     /**
      * Check whether the target node can safely be compiled.
      * 
      * @param node 
      */
     public static void confirmNodeIsSafe(Node node) {
         switch (node.nodeId) {
             case ARGSNODE:
                 ArgsNode argsNode = (ArgsNode) node;
                 // FIXME: We can't compile cases like def(a=(b=1)) because the variables
             // in the arg list get ordered differently than you might expect (b comes first)
             // So the code below searches through all opt args, ensuring none of them skip
             // indicies. A skipped index means there's a hidden local var/arg like b above
             // and so we shouldn't try to compile.
                 if (argsNode.getOptArgs() != null && argsNode.getOptArgs().size() > 0) {
                     int index = argsNode.getRequiredArgsCount() - 1;
 
                     for (int i = 0; i < argsNode.getOptArgs().size(); i++) {
                         int newIndex = ((LocalAsgnNode) argsNode.getOptArgs().get(i)).getIndex();
 
                         if (newIndex - index != 1) {
                             throw new NotCompilableException("Can't compile def with optional args that assign other variables at: " + node.getPosition());
                         }
                         index = newIndex;
                     }
                 }
                 break;
         }
     }
 }
diff --git a/src/org/jruby/compiler/MethodCompiler.java b/src/org/jruby/compiler/MethodCompiler.java
index d4af667783..86a263afd1 100644
--- a/src/org/jruby/compiler/MethodCompiler.java
+++ b/src/org/jruby/compiler/MethodCompiler.java
@@ -1,456 +1,457 @@
 /*
  * MethodCompiler.java
  * 
  * Created on Jul 10, 2007, 6:18:19 PM
  * 
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.compiler;
 
 import org.jruby.ast.NodeType;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author headius
  */
 public interface MethodCompiler {
     /**
      * End compilation for the method associated with the specified token. This should
      * close out all structures created for compilation of the method.
      * 
      * @param token A token identifying the method to be terminated.
      */
     public void endMethod();
     
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
      * Swap the top and second values on the stack.
      */
     public void swapValues();
     
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
     
     public void declareClassVariable(String name);
     
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
      * Create a new range. It is expected that the stack will contain the end and begin values for the range as
      * its topmost and second topmost elements.
      * 
      * @param isExclusive Whether the range is exclusive or not (inclusive)
      */
     public void createNewRange(boolean isExclusive);
     
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
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
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
     public void createNewClosure(StaticScope scope, int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId, ASTInspector inspector);
     
     /**
      * Create a new closure (block) for a for loop with the given call arity and
      * body generated by the body callback.
      * 
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewForLoop(int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId);
     
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
     public void defineNewMethod(String name, StaticScope scope, ClosureCallback body, ClosureCallback args, ClosureCallback receiver, ASTInspector inspector);
     
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
      * Load the given string as a symbol on to the top of the stack.
      * 
      * @param symbol The symbol to load.
      */
     public void loadSymbol(String symbol);
     
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
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      */
     public void assignGlobalVariable(String name);
     
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
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback, ArrayCallback nilCallback, ClosureCallback argsCallback);
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
      */
     public void ensureRubyArray();
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one or coercing it if it is not.
      */
     public void ensureMultipleAssignableRubyArray(boolean masgnHasHead);
     
     public void issueBreakEvent(ClosureCallback value);
     
     public void issueNextEvent(ClosureCallback value);
     
     public void issueRedoEvent();
     
     public void issueRetryEvent();
 
     public void asString();
 
     public void nthRef(int match);
 
     public void match();
 
     public void match2();
 
     public void match3();
 
     public void createNewRegexp(ByteList value, int options, String lang);
     public void createNewRegexp(ClosureCallback createStringCallback, int options, String lang);
     
     public void pollThreadEvents();
 
     public void branchIfModule(ClosureCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback);
 
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
     public void defineClass(String name, StaticScope staticScope, ClosureCallback superCallback, ClosureCallback pathCallback, ClosureCallback bodyCallback, ClosureCallback receiverCallback);
     public void defineModule(String name, StaticScope staticScope, ClosureCallback pathCallback, ClosureCallback bodyCallback);
     public void unwrapPassedBlock();
     public void performBackref(char type);
     public void callZSuper(ClosureCallback closure);
     public void appendToObjectArray();
     public void checkIsExceptionHandled();
     public void rethrowException();
     public void loadClass(String name);
     public void unwrapRaiseException();
     public void loadException();
     public void setPosition(ISourcePosition position);
     public void checkWhenWithSplat();
     public void createNewEndBlock(ClosureCallback body);
     public void runBeginBlock(StaticScope scope, ClosureCallback body);
-    
+    public void rethrowIfSystemExit();
+
     public MethodCompiler chainToMethod(String name, ASTInspector inspector);
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 701646b5ee..cff220c8a2 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1052 +1,1051 @@
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
 
 package org.jruby.compiler.impl;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.math.BigInteger;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 
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
-import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.ClosureCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.MethodCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.regexp.RegexpPattern;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallAdapter;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  *
  * @author headius
  */
 public class StandardASMCompiler implements ScriptCompiler, Opcodes {
     private static final CodegenUtils cg = CodegenUtils.cg;
     
     private static final String THREADCONTEXT = cg.p(ThreadContext.class);
     private static final String RUBY = cg.p(Ruby.class);
     private static final String IRUBYOBJECT = cg.p(IRubyObject.class);
 
     private static final String METHOD_SIGNATURE = cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class});
     private static final String CLOSURE_SIGNATURE = cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class});
 
     private static final int THIS = 0;
     private static final int THREADCONTEXT_INDEX = 1;
     private static final int SELF_INDEX = 2;
     private static final int ARGS_INDEX = 3;
     private static final int CLOSURE_INDEX = 4;
     private static final int DYNAMIC_SCOPE_INDEX = 5;
     private static final int RUNTIME_INDEX = 6;
     private static final int VARS_ARRAY_INDEX = 7;
     private static final int NIL_INDEX = 8;
     private static final int EXCEPTION_INDEX = 9;
     private static final int PREVIOUS_EXCEPTION_INDEX = 10;
     
     private String classname;
     private String sourcename;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     int methodIndex = -1;
     int innerIndex = -1;
     int fieldIndex = 0;
     int rescueNumber = 1;
     int ensureNumber = 1;
     StaticScope topLevelScope;
     
     Map<String, String> sourcePositions = new HashMap<String, String>();
     Map<String, String> byteLists = new HashMap<String, String>();
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(cg.c(classname), classWriter.toByteArray());
         return classLoader.loadClass(cg.c(classname));
     }
 
     public void writeClass(File destination) throws IOException {
         writeClass(classname, destination, classWriter);
     }
 
     private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
         String fullname = classname + ".class";
         String filename = null;
         String path = null;
         
         // verify the class
         byte[] bytecode = writer.toByteArray();
         CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(System.err));
         
         if (fullname.lastIndexOf("/") == -1) {
             filename = fullname;
             path = "";
         } else {
             filename = fullname.substring(fullname.lastIndexOf("/") + 1);
             path = fullname.substring(0, fullname.lastIndexOf("/"));
         }
         // create dir if necessary
         File pathfile = new File(destination, path);
         pathfile.mkdirs();
 
         FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
 
         out.write(bytecode);
     }
 
     public String getClassname() {
         return classname;
     }
 
     public String getSourcename() {
         return sourcename;
     }
 
     public ClassVisitor getClassVisitor() {
         return classWriter;
     }
 
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, cg.p(Object.class), new String[]{cg.p(Script.class)});
         classWriter.visitSource(sourcename, null);
         
         topLevelScope = scope;
 
         beginInit();
         beginClassInit();
     }
 
     public void endScript() {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "run", METHOD_SIGNATURE, null, null));
         method.start();
 
         // invoke __file__ with threadcontext, self, args (null), and block (null)
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(CLOSURE_INDEX);
 
         method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         method.areturn();
         
         method.end();
         
         // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
         method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "load", METHOD_SIGNATURE, null, null));
         method.start();
 
         // invoke __file__ with threadcontext, self, args (null), and block (null)
         Label tryBegin = new Label();
         Label tryFinally = new Label();
         
         method.label(tryBegin);
         method.aload(THREADCONTEXT_INDEX);
         buildStaticScopeNames(method, topLevelScope);
         method.invokestatic(cg.p(RuntimeHelpers.class), "preLoad", cg.sig(void.class, ThreadContext.class, String[].class));
         
         method.aload(THIS);
         method.aload(THREADCONTEXT_INDEX);
         method.aload(SELF_INDEX);
         method.aload(ARGS_INDEX);
         method.aload(CLOSURE_INDEX);
 
         method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         method.aload(THREADCONTEXT_INDEX);
         method.invokestatic(cg.p(RuntimeHelpers.class), "postLoad", cg.sig(void.class, ThreadContext.class));
         method.areturn();
         
         method.label(tryFinally);
         method.aload(THREADCONTEXT_INDEX);
         method.invokestatic(cg.p(RuntimeHelpers.class), "postLoad", cg.sig(void.class, ThreadContext.class));
         method.athrow();
         
         method.trycatch(tryBegin, tryFinally, tryFinally, null);
         
         method.end();
 
         // add main impl, used for detached or command-line execution of this script with a new runtime
         // root method of a script is always in stub0, method0
         method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", cg.sig(Void.TYPE, cg.params(String[].class)), null, null));
         method.start();
 
         // new instance to invoke run against
         method.newobj(classname);
         method.dup();
         method.invokespecial(classname, "<init>", cg.sig(Void.TYPE));
 
         // invoke run with threadcontext and topself
         method.invokestatic(cg.p(Ruby.class), "getDefaultInstance", cg.sig(Ruby.class));
         method.dup();
 
         method.invokevirtual(RUBY, "getCurrentContext", cg.sig(ThreadContext.class));
         method.swap();
         method.invokevirtual(RUBY, "getTopSelf", cg.sig(IRubyObject.class));
         method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
         method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
         method.invokevirtual(classname, "load", METHOD_SIGNATURE);
         method.voidreturn();
         method.end();
         
         endInit();
         endClassInit();
     }
 
     public void buildStaticScopeNames(SkinnyMethodAdapter method, StaticScope scope) {
         // construct static scope list of names
         method.ldc(new Integer(scope.getNumberOfVariables()));
         method.anewarray(cg.p(String.class));
         for (int i = 0; i < scope.getNumberOfVariables(); i++) {
             method.dup();
             method.ldc(new Integer(i));
             method.ldc(scope.getVariables()[i]);
             method.arraystore();
         }
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", cg.sig(Void.TYPE), null, null));
         initMethod.start();
         initMethod.aload(THIS);
         initMethod.invokespecial(cg.p(Object.class), "<init>", cg.sig(Void.TYPE));
         
         cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", cg.ci(Class.class), null, null);
         
         // FIXME: this really ought to be in clinit, but it doesn't matter much
         initMethod.aload(THIS);
         initMethod.ldc(cg.c(classname));
         initMethod.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
         initMethod.putfield(classname, "$class", cg.ci(Class.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", cg.sig(Void.TYPE), null, null));
         clinitMethod.start();
     }
 
     private void endClassInit() {
         clinitMethod.voidreturn();
         clinitMethod.end();
     }
     
     public MethodCompiler startMethod(String friendlyName, ClosureCallback args, StaticScope scope, ASTInspector inspector) {
         ASMMethodCompiler methodCompiler = new ASMMethodCompiler(friendlyName, inspector);
         
         methodCompiler.beginMethod(args, scope);
         
         return methodCompiler;
     }
 
     public abstract class AbstractMethodCompiler implements MethodCompiler {
         protected SkinnyMethodAdapter method;
         protected VariableCompiler variableCompiler;
         protected InvocationCompiler invocationCompiler;
         
         protected Label[] currentLoopLabels;
         protected Label scopeStart;
         protected Label scopeEnd;
         protected Label redoJump;
         protected boolean withinProtection = false;
         
         // The current local variable count, to use for temporary locals during processing
         protected int localVariable = PREVIOUS_EXCEPTION_INDEX + 1;
 
         public abstract void beginMethod(ClosureCallback args, StaticScope scope);
 
         public abstract void endMethod();
         
         public MethodCompiler chainToMethod(String methodName, ASTInspector inspector) {
             // chain to the next segment of this giant method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, methodName, cg.sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
             endMethod();
 
             ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, inspector);
 
             methodCompiler.beginChainedMethod();
 
             return methodCompiler;
         }
         
         public StandardASMCompiler getScriptCompiler() {
             return StandardASMCompiler.this;
         }
 
         public void lineNumber(ISourcePosition position) {
             Label line = new Label();
             method.label(line);
             method.visitLineNumber(position.getStartLine() + 1, line);
         }
 
         public void loadThreadContext() {
             method.aload(THREADCONTEXT_INDEX);
         }
 
         public void loadSelf() {
             method.aload(SELF_INDEX);
         }
 
         public void loadRuntime() {
             method.aload(RUNTIME_INDEX);
         }
 
         public void loadBlock() {
             method.aload(CLOSURE_INDEX);
         }
 
         public void loadNil() {
             method.aload(NIL_INDEX);
         }
         
         public void loadNull() {
             method.aconst_null();
         }
 
         public void loadSymbol(String symbol) {
             loadRuntime();
 
             method.ldc(symbol);
 
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void loadObject() {
             loadRuntime();
 
             invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
         }
 
         /**
          * This is for utility methods used by the compiler, to reduce the amount of code generation
          * necessary.  All of these live in CompilerHelpers.
          */
         public void invokeUtilityMethod(String methodName, String signature) {
             method.invokestatic(cg.p(RuntimeHelpers.class), methodName, signature);
         }
 
         public void invokeThreadContext(String methodName, String signature) {
             method.invokevirtual(THREADCONTEXT, methodName, signature);
         }
 
         public void invokeIRuby(String methodName, String signature) {
             method.invokevirtual(RUBY, methodName, signature);
         }
 
         public void invokeIRubyObject(String methodName, String signature) {
             method.invokeinterface(IRUBYOBJECT, methodName, signature);
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
             method.dup2_x1();
             method.pop2();
             invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void assignConstantInModule(String name) {
             method.ldc(name);
             loadThreadContext();
             invokeUtilityMethod("setConstantInModule", cg.sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
         }
 
         public void assignConstantInObject(String name) {
             // load Object under value
             loadRuntime();
             invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
             method.swap();
 
             assignConstantInModule(name);
         }
 
         public void retrieveConstant(String name) {
             loadThreadContext();
             method.ldc(name);
             invokeThreadContext("getConstant", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void retrieveConstantFromModule(String name) {
             method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class));
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "fastGetConstantFrom", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void retrieveClassVariable(String name) {
             loadThreadContext();
             loadRuntime();
             loadSelf();
             method.ldc(name);
 
             invokeUtilityMethod("fastFetchClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
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
 
             invokeUtilityMethod("fastSetClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
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
 
             invokeUtilityMethod("fastDeclareClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void createNewFloat(double value) {
             loadRuntime();
             method.ldc(new Double(value));
 
             invokeIRuby("newFloat", cg.sig(RubyFloat.class, cg.params(Double.TYPE)));
         }
 
         public void createNewFixnum(long value) {
             loadRuntime();
             method.ldc(new Long(value));
 
             invokeIRuby("newFixnum", cg.sig(RubyFixnum.class, cg.params(Long.TYPE)));
         }
 
         public void createNewBignum(BigInteger value) {
             loadRuntime();
             method.ldc(value.toString());
 
             method.invokestatic(cg.p(RubyBignum.class), "newBignum", cg.sig(RubyBignum.class, cg.params(Ruby.class, String.class)));
         }
 
         public void createNewString(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
             }
         }
 
         public void createNewSymbol(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
             }
             toJavaString();
             loadRuntime();
             method.swap();
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void createNewString(ByteList value) {
             // FIXME: this is sub-optimal, storing string value in a java.lang.String again
             String fieldName = cacheByteList(value.toString());
             loadRuntime();
             method.getstatic(classname, fieldName, cg.ci(ByteList.class));
 
             invokeIRuby("newStringShared", cg.sig(RubyString.class, cg.params(ByteList.class)));
         }
 
         public void createNewSymbol(String name) {
             loadRuntime();
             method.ldc(name);
             invokeIRuby("fastNewSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void createNewArray(boolean lightweight) {
             loadRuntime();
             // put under object array already present
             method.swap();
 
             if (lightweight) {
                 invokeIRuby("newArrayNoCopyLight", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
             } else {
                 invokeIRuby("newArrayNoCopy", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
             }
         }
 
         public void createEmptyArray() {
             loadRuntime();
 
             invokeIRuby("newArray", cg.sig(RubyArray.class, cg.params()));
         }
 
         public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
             buildObjectArray(IRUBYOBJECT, sourceArray, callback);
         }
 
         public void createObjectArray(int elementCount) {
             // if element count is less than 6, use helper methods
             if (elementCount < 6) {
                 Class[] params = new Class[elementCount];
                 Arrays.fill(params, IRubyObject.class);
                 invokeUtilityMethod("constructObjectArray", cg.sig(IRubyObject[].class, params));
             } else {
                 // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
                 throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
             }
         }
 
         private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
             if (sourceArray.length == 0) {
                 method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             } else if (sourceArray.length < RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
                 // if we have a specific-arity helper to construct an array for us, use that
                 for (int i = 0; i < sourceArray.length; i++) {
                     callback.nextValue(this, sourceArray, i);
                 }
                 invokeUtilityMethod("constructObjectArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject.class, sourceArray.length)));
             } else {
                 // brute force construction inline
                 method.ldc(new Integer(sourceArray.length));
                 method.anewarray(type);
 
                 for (int i = 0; i < sourceArray.length; i++) {
                     method.dup();
                     method.ldc(new Integer(i));
 
                     callback.nextValue(this, sourceArray, i);
 
                     method.arraystore();
                 }
             }
         }
 
         public void createEmptyHash() {
             loadRuntime();
 
             method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
         }
 
         public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
             loadRuntime();
             
             if (keyCount < RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
                 // we have a specific-arity method we can use to construct, so use that
                 for (int i = 0; i < keyCount; i++) {
                     callback.nextValue(this, elements, i);
                 }
                 
                 invokeUtilityMethod("constructHash", cg.sig(RubyHash.class, cg.params(Ruby.class, IRubyObject.class, keyCount * 2)));
             } else {
                 // create a new hashmap the brute-force way
                 method.newobj(cg.p(HashMap.class));
                 method.dup();
                 method.invokespecial(cg.p(HashMap.class), "<init>", cg.sig(Void.TYPE));
 
                 for (int i = 0; i < keyCount; i++) {
                     method.dup();
                     callback.nextValue(this, elements, i);
                     method.invokevirtual(cg.p(HashMap.class), "put", cg.sig(Object.class, cg.params(Object.class, Object.class)));
                     method.pop();
                 }
 
                 loadNil();
                 method.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class, Map.class, IRubyObject.class)));
             }
         }
 
         public void createNewRange(boolean isExclusive) {
             loadRuntime();
 
             // could be more efficient with a callback
             method.dup_x2();
             method.pop();
 
             method.ldc(new Boolean(isExclusive));
 
             method.invokestatic(cg.p(RubyRange.class), "newRange", cg.sig(RubyRange.class, cg.params(Ruby.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
         }
 
         /**
          * Invoke IRubyObject.isTrue
          */
         private void isTrue() {
             invokeIRubyObject("isTrue", cg.sig(Boolean.TYPE));
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
 
         public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
             // FIXME: handle next/continue, break, etc
             Label tryBegin = new Label();
             Label tryEnd = new Label();
             Label catchRedo = new Label();
             Label catchNext = new Label();
             Label catchBreak = new Label();
             Label catchRaised = new Label();
             Label endOfBody = new Label();
             Label conditionCheck = new Label();
             Label topOfBody = new Label();
             Label done = new Label();
             Label normalLoopEnd = new Label();
             method.trycatch(tryBegin, tryEnd, catchRedo, cg.p(JumpException.RedoJump.class));
             method.trycatch(tryBegin, tryEnd, catchNext, cg.p(JumpException.NextJump.class));
             method.trycatch(tryBegin, tryEnd, catchBreak, cg.p(JumpException.BreakJump.class));
             if (checkFirst) {
                 // only while loops seem to have this RaiseException magic
                 method.trycatch(tryBegin, tryEnd, catchRaised, cg.p(RaiseException.class));
             }
             
             method.label(tryBegin);
             {
                 
                 Label[] oldLoopLabels = currentLoopLabels;
                 
                 currentLoopLabels = new Label[] {endOfBody, topOfBody, done};
                 
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
             }
 
             method.label(tryEnd);
             // skip catch block
             method.go_to(normalLoopEnd);
 
             // catch logic for flow-control exceptions
             {
                 // redo jump
                 {
                     method.label(catchRedo);
                     method.pop();
                     method.go_to(topOfBody);
                 }
 
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
                     loadBlock();
                     invokeUtilityMethod("breakJumpInWhile", cg.sig(IRubyObject.class, JumpException.BreakJump.class, Block.class));
                     method.go_to(done);
                 }
 
                 // FIXME: This generates a crapload of extra code that is frequently *never* needed
                 // raised exception
                 if (checkFirst) {
                     // only while loops seem to have this RaiseException magic
                     method.label(catchRaised);
                     Label raiseNext = new Label();
                     Label raiseRedo = new Label();
                     Label raiseRethrow = new Label();
                     method.dup();
                     invokeUtilityMethod("getLocalJumpTypeOrRethrow", cg.sig(String.class, cg.params(RaiseException.class)));
                     // if we get here we have a RaiseException we know is a local jump error and an error type
 
                     // is it break?
                     method.dup(); // dup string
                     method.ldc("break");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseNext);
                     // pop the extra string, get the break value, and end the loop
                     method.pop();
                     invokeUtilityMethod("unwrapLocalJumpErrorValue", cg.sig(IRubyObject.class, cg.params(RaiseException.class)));
                     method.go_to(done);
 
                     // is it next?
                     method.label(raiseNext);
                     method.dup();
                     method.ldc("next");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseRedo);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(conditionCheck);
 
                     // is it redo?
                     method.label(raiseRedo);
                     method.dup();
                     method.ldc("redo");
                     method.invokevirtual(cg.p(String.class), "equals", cg.sig(boolean.class, cg.params(Object.class)));
                     method.ifeq(raiseRethrow);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(topOfBody);
 
                     // just rethrow it
                     method.label(raiseRethrow);
                     method.pop(); // pop extra string
                     method.athrow();
                 }
             }
             
             method.label(normalLoopEnd);
             loadNil();
             method.label(done);
         }
 
         public void createNewClosure(
                 StaticScope scope,
                 int arity,
                 ClosureCallback body,
                 ClosureCallback args,
                 boolean hasMultipleArgsHead,
                 NodeType argsNodeId,
                 ASTInspector inspector) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, inspector);
             
             closureCompiler.beginMethod(args, scope);
             
             body.compile(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(Block.asArgumentType(argsNodeId));
             // if there's a sub-closure or there's scope-aware methods, it can't be "light"
             method.ldc(!(inspector.hasClosure() || inspector.hasScopeAwareMethods()));
 
             invokeUtilityMethod("createBlock", cg.sig(CompiledBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE, boolean.class)));
         }
 
         public void runBeginBlock(StaticScope scope, ClosureCallback body) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, scope);
             
             body.compile(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             invokeUtilityMethod("runBeginBlock", cg.sig(IRubyObject.class,
                     cg.params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
         }
 
         public void createNewForLoop(int arity, ClosureCallback body, ClosureCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(args, null);
             
             body.compile(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
@@ -1320,1363 +1319,1376 @@ public class StandardASMCompiler implements ScriptCompiler, Opcodes {
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
 
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             loadRuntime();
 
             // load string, for Regexp#source and Regexp#inspect
             String regexpString = null;
             if ((options & ReOptions.RE_UNICODE) > 0) {
                 regexpString = value.toUtf8String();
             } else {
                 regexpString = value.toString();
             }
 
             loadRuntime();
             method.ldc(regexpString);
             method.ldc(new Integer(options));
             invokeUtilityMethod("regexpLiteral", cg.sig(RegexpPattern.class, cg.params(Ruby.class, String.class, Integer.TYPE)));
             method.dup();
 
             method.aload(THIS);
             method.swap();
             method.putfield(classname, patternField, cg.ci(RegexpPattern.class));
 
             if (null == lang) {
                 method.aconst_null();
             } else {
                 method.ldc(lang);
             }
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, RegexpPattern.class, String.class)));
 
             method.aload(THIS);
             method.swap();
             method.putfield(classname, regexpField, cg.ci(RubyRegexp.class));
             method.label(alreadyCreated);
             method.aload(THIS);
             method.getfield(classname, regexpField, cg.ci(RubyRegexp.class));
         }
 
         public void createNewRegexp(ClosureCallback createStringCallback, final int options, final String lang) {
             loadRuntime();
 
             loadRuntime();
             createStringCallback.compile(this);
             method.ldc(new Integer(options));
             invokeUtilityMethod("regexpLiteral", cg.sig(RegexpPattern.class, cg.params(Ruby.class, String.class, Integer.TYPE)));
 
             if (null == lang) {
                 method.aconst_null();
             } else {
                 method.ldc(lang);
             }
 
             method.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, RegexpPattern.class, String.class)));
         }
 
         public void pollThreadEvents() {
             loadThreadContext();
             invokeThreadContext("pollThreadEvents", cg.sig(Void.TYPE));
         }
 
         public void nullToNil() {
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             method.aload(NIL_INDEX);
             method.label(notNull);
         }
 
         public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
             method.instance_of(cg.p(clazz));
 
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
 
                 public void branch(MethodCompiler context) {
                     method.visitTypeInsn(CHECKCAST, cg.p(RubyMatchData.class));
                     method.dup();
                     method.invokevirtual(cg.p(RubyMatchData.class), "use", cg.sig(void.class));
                     method.ldc(new Long(number));
                     method.invokevirtual(cg.p(RubyMatchData.class), "group", cg.sig(IRubyObject.class, cg.params(long.class)));
                     method.invokeinterface(cg.p(IRubyObject.class), "isNil", cg.sig(boolean.class));
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
 
                 public void branch(MethodCompiler context) {
                     method.pop();
                     falseBranch.branch(context);
                 }
             });
         }
 
         public void branchIfModule(ClosureCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback) {
             receiverCallback.compile(this);
             isInstanceOf(RubyModule.class, moduleCallback, notModuleCallback);
         }
 
         public void backref() {
             loadThreadContext();
             invokeThreadContext("getCurrentFrame", cg.sig(Frame.class));
             method.invokevirtual(cg.p(Frame.class), "getBackRef", cg.sig(IRubyObject.class));
         }
 
         public void backrefMethod(String methodName) {
             backref();
             method.invokestatic(cg.p(RubyRegexp.class), methodName, cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
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
             return "__ensure_" + (ensureNumber++);
         }
 
         public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
             String mname = getNewEnsureName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
                 // set up a local IRuby variable
 
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
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
                 method.astore(EXCEPTION_INDEX);
                 method.label(ensureEnd);
 
                 protectedCode.branch(this);
 
                 method.aload(EXCEPTION_INDEX);
                 method.athrow();
                 
                 method.trycatch(codeBegin, codeEnd, ensureBegin, null);
                 method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
                 withinProtection = oldWithinProtection;
             }
 
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         protected String getNewRescueName() {
             return "__rescue_" + (rescueNumber++);
         }
 
         public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
             String mname = getNewRescueName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             Label afterMethodBody = new Label();
             Label catchRetry = new Label();
             Label catchRaised = new Label();
             Label catchJumps = new Label();
             Label exitRescue = new Label();
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
 
                 // set up a local IRuby variable
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
                 
                 // store previous exception for restoration if we rescue something
                 loadRuntime();
                 invokeUtilityMethod("getErrorInfo", cg.sig(IRubyObject.class, Ruby.class));
                 mv.astore(PREVIOUS_EXCEPTION_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label beforeBody = new Label();
                 Label afterBody = new Label();
                 Label catchBlock = new Label();
                 mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, cg.p(exception));
                 mv.visitLabel(beforeBody);
 
                 regularCode.branch(this);
 
                 mv.label(afterBody);
                 mv.go_to(exitRescue);
                 mv.label(catchBlock);
                 mv.astore(EXCEPTION_INDEX);
 
                 catchCode.branch(this);
                 
                 mv.label(afterMethodBody);
                 mv.go_to(exitRescue);
                 
                 mv.trycatch(beforeBody, afterMethodBody, catchRetry, cg.p(JumpException.RetryJump.class));
                 mv.label(catchRetry);
                 mv.pop();
                 mv.go_to(beforeBody);
                 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(beforeBody, afterMethodBody, catchRaised, cg.p(RaiseException.class));
                 mv.label(catchRaised);
                 mv.athrow();
                 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(beforeBody, afterMethodBody, catchJumps, cg.p(JumpException.class));
                 mv.label(catchJumps);
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
                 mv.athrow();
                 
                 mv.label(exitRescue);
                 
                 // restore the original exception
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", cg.sig(void.class, Ruby.class, IRubyObject.class));
                 
                 mv.areturn();
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 withinProtection = oldWithinProtection;
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
             }
             
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, cg.sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         public void inDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_1();
             invokeThreadContext("setWithinDefined", cg.sig(void.class, cg.params(boolean.class)));
         }
 
         public void outDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_0();
             invokeThreadContext("setWithinDefined", cg.sig(void.class, cg.params(boolean.class)));
         }
 
         public void stringOrNil() {
             loadRuntime();
             loadNil();
             invokeUtilityMethod("stringOrNil", cg.sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
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
             method.invokevirtual(cg.p(RubyClass.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
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
             method.invokevirtual(cg.p(Block.class), "isGiven", cg.sig(boolean.class));
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
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(cg.p(GlobalVariables.class), "isDefined", cg.sig(boolean.class, cg.params(String.class)));
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
             invokeThreadContext("getConstantDefined", cg.sig(boolean.class, cg.params(String.class)));
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
             method.ldc(name);
             //method.invokeinterface(cg.p(IRubyObject.class), "getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
             method.invokeinterface(cg.p(IRubyObject.class), "fastHasInstanceVariable", cg.sig(boolean.class, cg.params(String.class)));
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
         
         public void isClassVarDefined(String name, BranchCallback trueBranch, BranchCallback falseBranch){
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "fastIsClassVarDefined", cg.sig(boolean.class, cg.params(String.class)));
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
             method.invokeinterface(cg.p(IRubyObject.class), "isNil", cg.sig(boolean.class));
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
             method.ifnull((Label)gotoToken);
         }
         
         public void ifNotNull(Object gotoToken) {
             method.ifnonnull((Label)gotoToken);
         }
         
         public void setEnding(Object endingToken){
             method.label((Label)endingToken);
         }
         
         public void go(Object gotoToken) {
             method.go_to((Label)gotoToken);
         }
         
         public void isConstantBranch(final BranchCallback setup, final BranchCallback isConstant, final BranchCallback isMethod, final BranchCallback none, final String name) {
             rescue(new BranchCallback() {
                     public void branch(MethodCompiler context) {
                         setup.branch(AbstractMethodCompiler.this);
                         method.dup(); //[C,C]
                         method.instance_of(cg.p(RubyModule.class)); //[C, boolean]
 
                         Label falseJmp = new Label();
                         Label afterJmp = new Label();
                         Label nextJmp = new Label();
                         Label nextJmpPop = new Label();
 
                         method.ifeq(nextJmp); // EQ == 0 (i.e. false)   //[C]
                         method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class));
                         method.dup(); //[C, C]
                         method.ldc(name); //[C, C, String]
                         method.invokevirtual(cg.p(RubyModule.class), "fastGetConstantAt", cg.sig(IRubyObject.class, cg.params(String.class))); //[C, null|C]
                         method.dup();
                         method.ifnull(nextJmpPop);
                         method.pop(); method.pop();
 
                         isConstant.branch(AbstractMethodCompiler.this);
 
                         method.go_to(afterJmp);
                         
                         method.label(nextJmpPop);
                         method.pop();
 
                         method.label(nextJmp); //[C]
 
                         metaclass();
                         method.ldc(name);
                         method.iconst_1(); // push true
                         method.invokevirtual(cg.p(RubyClass.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
                         method.ifeq(falseJmp); // EQ == 0 (i.e. false)
                         
                         isMethod.branch(AbstractMethodCompiler.this);
                         method.go_to(afterJmp);
 
                         method.label(falseJmp);
                         none.branch(AbstractMethodCompiler.this);
             
                         method.label(afterJmp);
                     }}, JumpException.class, none, String.class);
         }
         
         public void metaclass() {
             invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
         }
         
         public void getVisibilityFor(String name) {
             method.ldc(name);
             method.invokevirtual(cg.p(RubyClass.class), "searchMethod", cg.sig(DynamicMethod.class, cg.params(String.class)));
             method.invokevirtual(cg.p(DynamicMethod.class), "getVisibility", cg.sig(Visibility.class));
         }
         
         public void isPrivate(Object gotoToken, int toConsume) {
             method.invokevirtual(cg.p(Visibility.class), "isPrivate", cg.sig(boolean.class));
             Label temp = new Label();
             method.ifeq(temp); // EQ == 0 (i.e. false)
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void isNotProtected(Object gotoToken, int toConsume) {
             method.invokevirtual(cg.p(Visibility.class), "isProtected", cg.sig(boolean.class));
             Label temp = new Label();
             method.ifne(temp);
             while((toConsume--) > 0) {
                   method.pop();
             }
             method.go_to((Label)gotoToken);
             method.label(temp);
         }
         
         public void selfIsKindOf(Object gotoToken) {
             loadSelf();
             method.swap();
             method.invokevirtual(cg.p(RubyClass.class), "getRealClass", cg.sig(RubyClass.class));
             method.invokeinterface(cg.p(IRubyObject.class), "isKindOf", cg.sig(boolean.class, cg.params(RubyModule.class)));
             method.ifne((Label)gotoToken); // EQ != 0 (i.e. true)
         }
         
         public void notIsModuleAndClassVarDefined(String name, Object gotoToken) {
             method.dup(); //[?, ?]
             method.instance_of(cg.p(RubyModule.class)); //[?, boolean]
             Label falsePopJmp = new Label();
             Label successJmp = new Label();
             method.ifeq(falsePopJmp);
 
             method.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class)); //[RubyModule]
             method.ldc(name); //[RubyModule, String]
             
             method.invokevirtual(cg.p(RubyModule.class), "fastIsClassVarDefined", cg.sig(boolean.class, cg.params(String.class))); //[boolean]
             method.ifeq((Label)gotoToken);
             method.go_to(successJmp);
             method.label(falsePopJmp);
             method.pop();
             method.go_to((Label)gotoToken);
             method.label(successJmp);
         }
         
         public void ifSingleton(Object gotoToken) {
             method.invokevirtual(cg.p(RubyModule.class), "isSingleton", cg.sig(boolean.class));
             method.ifne((Label)gotoToken); // EQ == 0 (i.e. false)
         }
         
         public void getInstanceVariable(String name) {
             method.ldc(name);
             method.invokeinterface(cg.p(IRubyObject.class), "fastGetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
         
         public void getFrameName() {
             loadThreadContext();
             invokeThreadContext("getFrameName", cg.sig(String.class));
         }
         
         public void getFrameKlazz() {
             loadThreadContext();
             invokeThreadContext("getFrameKlazz", cg.sig(RubyModule.class));
         }
         
         public void superClass() {
             method.invokevirtual(cg.p(RubyModule.class), "getSuperClass", cg.sig(RubyClass.class));
         }
         public void attached() {
             method.visitTypeInsn(CHECKCAST, cg.p(MetaClass.class));
             method.invokevirtual(cg.p(MetaClass.class), "getAttached", cg.sig(IRubyObject.class));
         }
         public void ifNotSuperMethodBound(Object token) {
             method.swap();
             method.iconst_0();
             method.invokevirtual(cg.p(RubyModule.class), "isMethodBound", cg.sig(boolean.class, cg.params(String.class, boolean.class)));
             method.ifeq((Label)token);
         }
         
         public void concatArrays() {
             method.invokevirtual(cg.p(RubyArray.class), "concat", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
         
         public void concatObjectArrays() {
             invokeUtilityMethod("concatObjectArrays", cg.sig(IRubyObject[].class, cg.params(IRubyObject[].class, IRubyObject[].class)));
         }
         
         public void appendToArray() {
             method.invokevirtual(cg.p(RubyArray.class), "append", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
         
         public void appendToObjectArray() {
             invokeUtilityMethod("appendToObjectArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject[].class, IRubyObject.class)));
         }
         
         public void convertToJavaArray() {
             method.invokestatic(cg.p(ArgsUtil.class), "convertToJavaArray", cg.sig(IRubyObject[].class, cg.params(IRubyObject.class)));
         }
 
         public void aliasGlobal(String newName, String oldName) {
             loadRuntime();
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(newName);
             method.ldc(oldName);
             method.invokevirtual(cg.p(GlobalVariables.class), "alias", cg.sig(Void.TYPE, cg.params(String.class, String.class)));
             loadNil();
         }
         
         public void undefMethod(String name) {
             loadThreadContext();
             invokeThreadContext("getRubyClass", cg.sig(RubyModule.class));
             
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             loadRuntime();
             method.ldc("No class to undef method '" + name + "'.");
             invokeIRuby("newTypeError", cg.sig(RaiseException.class, cg.params(String.class)));
             method.athrow();
             
             method.label(notNull);
             method.ldc(name);
             method.invokevirtual(cg.p(RubyModule.class), "undef", cg.sig(Void.TYPE, cg.params(String.class)));
             
             loadNil();
         }
 
         public void defineClass(
                 final String name, 
                 final StaticScope staticScope, 
                 final ClosureCallback superCallback, 
                 final ClosureCallback pathCallback, 
                 final ClosureCallback bodyCallback, 
                 final ClosureCallback receiverCallback) {
             String methodName = "rubyclass__" + cg.cleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
             
             ClosureCallback bodyPrep = new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     if (receiverCallback == null) {
                         if (superCallback != null) {
                             methodCompiler.loadRuntime();
                             superCallback.compile(methodCompiler);
 
                             methodCompiler.invokeUtilityMethod("prepareSuperClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
                         } else {
                             methodCompiler.method.aconst_null();
                         }
 
                         methodCompiler.loadThreadContext();
 
                         pathCallback.compile(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.ldc(name);
 
                         methodCompiler.method.swap();
 
                         methodCompiler.method.invokevirtual(cg.p(RubyModule.class), "defineOrGetClassUnder", cg.sig(RubyClass.class, cg.params(String.class, RubyClass.class)));
                     } else {
                         methodCompiler.loadRuntime();
 
                         receiverCallback.compile(methodCompiler);
 
                         methodCompiler.invokeUtilityMethod("getSingletonClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
                     }
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
                     methodCompiler.invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class, String[].class)));
                 }
             };
 
             // Here starts the logic for the class definition
             Label start = new Label();
             Label end = new Label();
             Label after = new Label();
             Label noException = new Label();
             methodCompiler.method.trycatch(start, end, after, null);
 
             methodCompiler.beginClass(bodyPrep, staticScope);
 
             methodCompiler.method.label(start);
 
             bodyCallback.compile(methodCompiler);
             methodCompiler.method.label(end);
             // finally with no exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             // finally with exception
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         }
 
         public void defineModule(final String name, final StaticScope staticScope, final ClosureCallback pathCallback, final ClosureCallback bodyCallback) {
             String methodName = "rubyclass__" + cg.cleanJavaIdentifier(name) + "__" + ++methodIndex;
 
             final ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, null);
 
             ClosureCallback bodyPrep = new ClosureCallback() {
                 public void compile(MethodCompiler context) {
                     methodCompiler.loadThreadContext();
 
                     pathCallback.compile(methodCompiler);
 
                     methodCompiler.invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
 
                     methodCompiler.method.ldc(name);
 
                     methodCompiler.method.invokevirtual(cg.p(RubyModule.class), "defineOrGetModuleUnder", cg.sig(RubyModule.class, cg.params(String.class)));
 
                     // set self to the class
                     methodCompiler.method.dup();
                     methodCompiler.method.astore(SELF_INDEX);
 
                     // CLASS BODY
                     methodCompiler.loadThreadContext();
                     methodCompiler.method.swap();
 
                     // static scope
                     buildStaticScopeNames(methodCompiler.method, staticScope);
 
                     methodCompiler.invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class, String[].class)));
                 }
             };
 
             // Here starts the logic for the class definition
             Label start = new Label();
             Label end = new Label();
             Label after = new Label();
             Label noException = new Label();
             methodCompiler.method.trycatch(start, end, after, null);
             
             methodCompiler.beginClass(bodyPrep, staticScope);
 
             methodCompiler.method.label(start);
 
             bodyCallback.compile(methodCompiler);
             methodCompiler.method.label(end);
             
             methodCompiler.method.go_to(noException);
             
             methodCompiler.method.label(after);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
             methodCompiler.method.athrow();
             
             methodCompiler.method.label(noException);
             methodCompiler.loadThreadContext();
             methodCompiler.invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
 
             methodCompiler.endMethod();
 
             // prepare to call class definition method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
         }
         
         public void unwrapPassedBlock() {
             loadBlock();
             invokeUtilityMethod("getBlockFromBlockPassBody", cg.sig(Block.class, cg.params(IRubyObject.class, Block.class)));
         }
         
         public void performBackref(char type) {
             loadThreadContext();
             switch (type) {
             case '~':
                 invokeUtilityMethod("backref", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '&':
                 invokeUtilityMethod("backrefLastMatch", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '`':
                 invokeUtilityMethod("backrefMatchPre", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '\'':
                 invokeUtilityMethod("backrefMatchPost", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             case '+':
                 invokeUtilityMethod("backrefMatchLast", cg.sig(IRubyObject.class, cg.params(ThreadContext.class)));
                 break;
             default:
                 throw new NotCompilableException("ERROR: backref with invalid type");
             }
         }
         
         public void callZSuper(ClosureCallback closure) {
             loadRuntime();
             loadThreadContext();
             if (closure != null) {
                 closure.compile(this);
             } else {
                 method.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
             }
             loadSelf();
             
             invokeUtilityMethod("callZSuper", cg.sig(IRubyObject.class, cg.params(Ruby.class, ThreadContext.class, Block.class, IRubyObject.class)));
         }
         
         public void checkIsExceptionHandled() {
             // ruby exception and list of exception types is on the stack
             loadRuntime();
             loadThreadContext();
             loadSelf();
             invokeUtilityMethod("isExceptionHandled", cg.sig(IRubyObject.class, RubyException.class, IRubyObject[].class, Ruby.class, ThreadContext.class, IRubyObject.class));
         }
         
         public void rethrowException() {
             loadException();
             method.athrow();
         }
         
         public void loadClass(String name) {
             loadRuntime();
             method.ldc(name);
             invokeIRuby("getClass", cg.sig(RubyClass.class, String.class));
         }
         
         public void unwrapRaiseException() {
             // RaiseException is on stack, get RubyException out
             method.invokevirtual(cg.p(RaiseException.class), "getException", cg.sig(RubyException.class));
         }
         
         public void loadException() {
             method.aload(EXCEPTION_INDEX);
         }
         
         public void setPosition(ISourcePosition position) {
             // FIXME I'm still not happy with this additional overhead per line,
             // nor about the extra script construction cost, but it will have to do for now.
             loadThreadContext();
             method.getstatic(classname, cachePosition(position.getFile(), position.getEndLine()), cg.ci(ISourcePosition.class));
             invokeThreadContext("setPosition", cg.sig(void.class, ISourcePosition.class));
         }
         
         public void checkWhenWithSplat() {
             loadThreadContext();
             invokeUtilityMethod("isWhenTriggered", cg.sig(RubyBoolean.class, IRubyObject.class, IRubyObject.class, ThreadContext.class));
         }
         
         public void issueRetryEvent() {
             invokeUtilityMethod("retryJump", cg.sig(IRubyObject.class));
         }
 
         public void defineNewMethod(String name, StaticScope scope, ClosureCallback body, ClosureCallback args, ClosureCallback receiver, ASTInspector inspector) {
             // TODO: build arg list based on number of args, optionals, etc
             ++methodIndex;
             String methodName = cg.cleanJavaIdentifier(name) + "__" + methodIndex;
 
             MethodCompiler methodCompiler = startMethod(methodName, args, scope, inspector);
 
             // callbacks to fill in method body
             body.compile(methodCompiler);
 
             methodCompiler.endMethod();
 
             // prepare to call "def" utility method to handle def logic
             loadThreadContext();
 
             loadSelf();
             
             if (receiver != null) receiver.compile(this);
             
             // script object
             method.aload(THIS);
 
             method.ldc(name);
 
             method.ldc(methodName);
 
             buildStaticScopeNames(method, scope);
 
             method.ldc(scope.getArity().getValue());
             
             // arities
             method.ldc(scope.getRequiredArgs());
             method.ldc(scope.getOptionalArgs());
             method.ldc(scope.getRestArg());
             
             if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 method.getstatic(cg.p(CallConfiguration.class), "RUBY_FULL", cg.ci(CallConfiguration.class));
             } else {
                 method.getstatic(cg.p(CallConfiguration.class), "JAVA_FULL", cg.ci(CallConfiguration.class));
             }
             
             if (receiver != null) {
                 invokeUtilityMethod("defs", cg.sig(IRubyObject.class, 
                         cg.params(ThreadContext.class, IRubyObject.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
             } else {
                 invokeUtilityMethod("def", cg.sig(IRubyObject.class, 
                         cg.params(ThreadContext.class, IRubyObject.class, Object.class, String.class, String.class, String[].class, int.class, int.class, int.class, int.class, CallConfiguration.class)));
             }
         }
+
+        public void rethrowIfSystemExit() {
+            loadRuntime();
+            method.ldc("SystemExit");
+            method.invokevirtual(cg.p(Ruby.class), "fastGetClass", cg.sig(RubyClass.class, String.class));
+            method.invokevirtual(cg.p(RubyException.class), "isKindOf", cg.sig(boolean.class, cg.params(RubyModule.class)));
+            method.iconst_0();
+            Label ifEnd = new Label();
+            method.if_icmpeq(ifEnd);
+            loadException();
+            method.athrow();
+            method.label(ifEnd);
+        }
     }
 
     public class ASMClosureCompiler extends AbstractMethodCompiler {
         private String closureMethodName;
         
         public ASMClosureCompiler(String closureMethodName, String closureFieldName, ASTInspector inspector) {
             this.closureMethodName = closureMethodName;
 
             // declare the field
             getClassVisitor().visitField(ACC_PRIVATE, closureFieldName, cg.ci(CompiledBlockCallback.class), null, null);
             
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, closureMethodName, CLOSURE_SIGNATURE, null, null));
             if (inspector == null || inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
 
         public void beginMethod(ClosureCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginClosure(args, scope);
 
             // start of scoping for closure's vars
             scopeStart = new Label();
             scopeEnd = new Label();
             redoJump = new Label();
             method.label(scopeStart);
         }
 
         public void beginClass(ClosureCallback bodyPrep, StaticScope scope) {
             throw new NotCompilableException("ERROR: closure compiler should not be used for class bodies");
         }
 
         public void endMethod() {
             // end of scoping for closure's vars
             scopeEnd = new Label();
             method.areturn();
             method.label(scopeEnd);
             
             // handle redos by restarting the block
             method.pop();
             method.go_to(scopeStart);
             
             method.trycatch(scopeStart, scopeEnd, scopeEnd, cg.p(JumpException.RedoJump.class));
             method.end();
         }
 
         public void loadBlock() {
             loadThreadContext();
             invokeThreadContext("getFrameBlock", cg.sig(Block.class));
         }
 
         protected String getNewRescueName() {
             return closureMethodName + "_" + super.getNewRescueName();
         }
 
         protected String getNewEnsureName() {
             return closureMethodName + "_" + super.getNewEnsureName();
         }
 
         public void performReturn() {
             loadThreadContext();
             invokeUtilityMethod("returnJump", cg.sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void processRequiredArgs(Arity arity, int requiredArgs, int optArgs, int restArg) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void assignOptionalArgs(Object object, int expectedArgsCount, int size, ArrayCallback optEval) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void processRestArg(int startIndex, int restArg) {
             throw new NotCompilableException("Shouldn't be calling this...");
         }
 
         public void processBlockArgument(int index) {
             loadRuntime();
             loadThreadContext();
             loadBlock();
             method.ldc(new Integer(index));
             invokeUtilityMethod("processBlockArgument", cg.sig(void.class, cg.params(Ruby.class, ThreadContext.class, Block.class, int.class)));
         }
         
         public void issueBreakEvent(ClosureCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.compile(this);
                 invokeUtilityMethod("breakJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.compile(this);
                 issueLoopBreak();
             }
         }
 
         public void issueNextEvent(ClosureCallback value) {
             if (withinProtection || currentLoopLabels == null) {
                 value.compile(this);
                 invokeUtilityMethod("nextJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else {
                 value.compile(this);
                 issueLoopNext();
             }
         }
 
         public void issueRedoEvent() {
             // FIXME: This isn't right for within ensured/rescued code
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", cg.sig(IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 issueLoopRedo();
             } else {
                 // jump back to the top of the main body of this closure
                 method.go_to(scopeStart);
             }
         }
     }
 
     public class ASMMethodCompiler extends AbstractMethodCompiler {
         private String friendlyName;
 
         public ASMMethodCompiler(String friendlyName, ASTInspector inspector) {
             this.friendlyName = friendlyName;
 
             method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, friendlyName, METHOD_SIGNATURE, null, null));
             if (inspector == null || inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                 variableCompiler = new HeapBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, VARS_ARRAY_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             } else {
                 variableCompiler = new StackBasedVariableCompiler(this, method, DYNAMIC_SCOPE_INDEX, ARGS_INDEX, CLOSURE_INDEX);
             }
             invocationCompiler = new StandardInvocationCompiler(this, method);
         }
         
         public void beginChainedMethod() {
             method.aload(THREADCONTEXT_INDEX);
             method.dup();
             method.invokevirtual(cg.p(ThreadContext.class), "getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
 
             // grab nil for local variables
             method.invokevirtual(cg.p(Ruby.class), "getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
 
             method.invokevirtual(cg.p(ThreadContext.class), "getCurrentScope", cg.sig(DynamicScope.class));
             method.dup();
             method.astore(DYNAMIC_SCOPE_INDEX);
             method.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
             method.astore(VARS_ARRAY_INDEX);
         }
 
         public void beginMethod(ClosureCallback args, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginMethod(args, scope);
 
             // visit a label to start scoping for local vars in this method
             Label start = new Label();
             method.label(start);
 
             scopeStart = start;
         }
 
         public void beginClass(ClosureCallback bodyPrep, StaticScope scope) {
             method.start();
 
             // set up a local IRuby variable
             method.aload(THREADCONTEXT_INDEX);
             invokeThreadContext("getRuntime", cg.sig(Ruby.class));
             method.dup();
             method.astore(RUNTIME_INDEX);
             
             // grab nil for local variables
             invokeIRuby("getNil", cg.sig(IRubyObject.class));
             method.astore(NIL_INDEX);
             
             variableCompiler.beginClass(bodyPrep, scope);
 
             // visit a label to start scoping for local vars in this method
             Label start = new Label();
             method.label(start);
 
             scopeStart = start;
         }
 
         public void endMethod() {
             // return last value from execution
             method.areturn();
 
             // end of variable scope
             Label end = new Label();
             method.label(end);
 
             method.end();
         }
         
         public void performReturn() {
             // normal return for method body. return jump for within a begin/rescue/ensure
             if (withinProtection) {
                 loadThreadContext();
                 invokeUtilityMethod("returnJump", cg.sig(IRubyObject.class, IRubyObject.class, ThreadContext.class));
             } else {
                 method.areturn();
             }
         }
 
         public void issueBreakEvent(ClosureCallback value) {
             if (withinProtection) {
                 value.compile(this);
                 invokeUtilityMethod("breakJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.compile(this);
                 issueLoopBreak();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.compile(this);
                 invokeUtilityMethod("breakLocalJumpError", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueNextEvent(ClosureCallback value) {
             if (withinProtection) {
                 value.compile(this);
                 invokeUtilityMethod("nextJump", cg.sig(IRubyObject.class, IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 value.compile(this);
                 issueLoopNext();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 value.compile(this);
                 invokeUtilityMethod("nextLocalJumpError", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class));
             }
         }
 
         public void issueRedoEvent() {
             if (withinProtection) {
                 invokeUtilityMethod("redoJump", cg.sig(IRubyObject.class));
             } else if (currentLoopLabels != null) {
                 issueLoopRedo();
             } else {
                 // in method body with no containing loop, issue jump error
                 // load runtime and value, issue jump error
                 loadRuntime();
                 invokeUtilityMethod("redoLocalJumpError", cg.sig(IRubyObject.class, Ruby.class));
             }
         }
     }
 
     private int constants = 0;
 
     public String getNewConstant(String type, String name_prefix) {
         return getNewConstant(type, name_prefix, null);
     }
 
     public String getNewConstant(String type, String name_prefix, Object init) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "_" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE, realName, type, null, null).visitEnd();
 
         if(init != null) {
             initMethod.aload(THIS);
             initMethod.ldc(init);
             initMethod.putfield(classname, realName, type);
         }
 
         return realName;
     }
 
     public String getNewStaticConstant(String type, String name_prefix) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized (this) {
             realName = "__" + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE | ACC_STATIC, realName, type, null, null).visitEnd();
         return realName;
     }
     
     public String cacheCallAdapter(String name, CallType callType) {
         String fieldName = getNewConstant(cg.ci(CallAdapter.class), cg.cleanJavaIdentifier(name));
         
         // retrieve call adapter
         initMethod.aload(THIS);
         initMethod.ldc(name);
         if (callType.equals(CallType.NORMAL)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getCallAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         } else if (callType.equals(CallType.FUNCTIONAL)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getFunctionAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         } else if (callType.equals(CallType.VARIABLE)) {
             initMethod.invokestatic(cg.p(MethodIndex.class), "getVariableAdapter", cg.sig(CallAdapter.class, cg.params(String.class)));
         }
         initMethod.putfield(classname, fieldName, cg.ci(CallAdapter.class));
         
         return fieldName;
     }
     
     public String cachePosition(String file, int line) {
         String cleanName = cg.cleanJavaIdentifier(file + "$" + line);
         String fieldName = sourcePositions.get(cleanName);
         if (fieldName == null) {
             fieldName = getNewStaticConstant(cg.ci(ISourcePosition.class), cleanName);
             sourcePositions.put(cg.cleanJavaIdentifier(file + "$" + line), fieldName);
 
             clinitMethod.ldc(file);
             clinitMethod.ldc(line);
             clinitMethod.invokestatic(cg.p(RuntimeHelpers.class), "constructPosition", cg.sig(ISourcePosition.class, String.class, int.class));
             clinitMethod.putstatic(classname, fieldName, cg.ci(ISourcePosition.class));
         }
         
         return fieldName;
     }
     
     public String cacheByteList(String contents) {
         String fieldName = byteLists.get(contents);
         if (fieldName == null) {
             fieldName = getNewStaticConstant(cg.ci(ByteList.class), "byteList");
             byteLists.put(contents, fieldName);
 
             clinitMethod.ldc(contents);
             clinitMethod.invokestatic(cg.p(ByteList.class), "create", cg.sig(ByteList.class, CharSequence.class));
             clinitMethod.putstatic(classname, fieldName, cg.ci(ByteList.class));
         }
         
         return fieldName;
     }
 }
diff --git a/src/org/jruby/evaluator/ASTInterpreter.java b/src/org/jruby/evaluator/ASTInterpreter.java
index 7ba3869d7b..688dedc077 100644
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
@@ -434,1650 +434,1654 @@ public class ASTInterpreter {
         ArrayNode iVisited = (ArrayNode) node;
         IRubyObject[] array = new IRubyObject[iVisited.size()];
         
         for (int i = 0; i < iVisited.size(); i++) {
             Node next = iVisited.get(i);
    
             array[i] = evalInternal(runtime,context, next, self, aBlock);
         }
    
         if (iVisited.isLightweight()) {
             return runtime.newArrayNoCopyLight(array);
         }
         
         return runtime.newArrayNoCopy(array);
     }
 
     public static RubyArray arrayValue(Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), MethodIndex.TO_A, "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return value.convertToType(runtime.getArray(), MethodIndex.TO_A, "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     private static IRubyObject attrAssignNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         AttrAssignNode iVisited = (AttrAssignNode) node;
    
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
         
         // If reciever is self then we do the call the same way as vcall
         CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
    
         RubyModule module = receiver.getMetaClass();
         
         String name = iVisited.getName();
 
         DynamicMethod method = module.searchMethod(name);
 
         if (method.isUndefined() || (!method.isCallableFrom(self, callType))) {
             return RuntimeHelpers.callMethodMissing(context, receiver, method, name, args, self, callType, Block.NULL_BLOCK);
         }
 
         method.call(context, receiver, module, name, args, Block.NULL_BLOCK);
 
         return args[args.length - 1];
     }
 
     private static IRubyObject backRefNode(ThreadContext context, Node node) {
         BackRefNode iVisited = (BackRefNode) node;
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         switch (iVisited.getType()) {
         case '~':
             if(backref instanceof RubyMatchData) {
                 ((RubyMatchData)backref).use();
             }
             return backref;
         case '&':
             return RubyRegexp.last_match(backref);
         case '`':
             return RubyRegexp.match_pre(backref);
         case '\'':
             return RubyRegexp.match_post(backref);
         case '+':
             return RubyRegexp.match_last(backref);
         default:
             assert false: "backref with invalid type";
             return null;
         }
     }
 
     private static IRubyObject bignumNode(Ruby runtime, Node node) {
         return RubyBignum.newBignum(runtime, ((BignumNode)node).getValue());
     }
 
     private static IRubyObject blockNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BlockNode iVisited = (BlockNode) node;
    
         IRubyObject result = runtime.getNil();
         for (int i = 0; i < iVisited.size(); i++) {
             result = evalInternal(runtime,context, iVisited.get(i), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject breakNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BreakNode iVisited = (BreakNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         throw new JumpException.BreakJump(null, result);
     }
 
     private static IRubyObject callNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CallNode iVisited = (CallNode) node;
 
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
 
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             return iVisited.callAdapter.call(context, receiver, args);
         }
             
         while (true) {
             try {
                 return iVisited.callAdapter.call(context, receiver, args, block);
             } catch (JumpException.RetryJump rj) {
                 // allow loop to retry
             } catch (JumpException.BreakJump bj) {
                 return (IRubyObject) bj.getValue();
             }
         }
     }
 
     private static IRubyObject caseNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CaseNode iVisited = (CaseNode) node;
         IRubyObject expression = null;
         if (iVisited.getCaseNode() != null) {
             expression = evalInternal(runtime,context, iVisited.getCaseNode(), self, aBlock);
         }
 
         context.pollThreadEvents();
 
         IRubyObject result = runtime.getNil();
 
         Node firstWhenNode = iVisited.getFirstWhenNode();
         while (firstWhenNode != null) {
             if (!(firstWhenNode instanceof WhenNode)) {
                 node = firstWhenNode;
                 return evalInternal(runtime, context, node, self, aBlock);
             }
 
             WhenNode whenNode = (WhenNode) firstWhenNode;
 
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
                 for (int i = 0; i < arrayNode.size(); i++) {
                     Node tag = arrayNode.get(i);
 
                     context.setPosition(tag.getPosition());
                     if (isTrace(runtime)) {
                         callTraceFunction(runtime, context, EventHook.RUBY_EVENT_LINE);
                     }
 
                     // Ruby grammar has nested whens in a case body because of
                     // productions case_body and when_args.
                     if (tag instanceof WhenNode) {
                         IRubyObject expressionsObject = evalInternal(runtime,context, ((WhenNode) tag)
                                         .getExpressionNodes(), self, aBlock);
                         RubyArray expressions = splatValue(runtime, expressionsObject);
 
                         for (int j = 0,k = expressions.getLength(); j < k; j++) {
                             IRubyObject condition = expressions.eltInternal(j);
 
                             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                                     .isTrue())
                                     || (expression == null && condition.isTrue())) {
                                 node = ((WhenNode) firstWhenNode).getBodyNode();
                                 return evalInternal(runtime, context, node, self, aBlock);
                             }
                         }
                         continue;
                     }
 
                     result = evalInternal(runtime,context, tag, self, aBlock);
 
                     if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
                             || (expression == null && result.isTrue())) {
                         node = whenNode.getBodyNode();
                         return evalInternal(runtime, context, node, self, aBlock);
                     }
                 }
             } else {
                 result = evalInternal(runtime,context, whenNode.getExpressionNodes(), self, aBlock);
 
                 if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
                         || (expression == null && result.isTrue())) {
                     node = ((WhenNode) firstWhenNode).getBodyNode();
                     return evalInternal(runtime, context, node, self, aBlock);
                 }
             }
 
             context.pollThreadEvents();
 
             firstWhenNode = whenNode.getNextCase();
         }
 
         return runtime.getNil();
     }
 
     private static IRubyObject classNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassNode iVisited = (ClassNode) node;
         Node classNameNode = iVisited.getCPath();
 
         RubyModule enclosingClass = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
 
         if (enclosingClass == null) throw runtime.newTypeError("no outer class/module");
 
         Node superNode = iVisited.getSuperNode();
 
         RubyClass superClass = null;
 
         if (superNode != null) {
             IRubyObject superObj = evalInternal(runtime, context, superNode, self, aBlock);
             RubyClass.checkInheritable(superObj);
             superClass = (RubyClass)superObj;
         }
 
         String name = ((INameNode) classNameNode).getName();        
 
         RubyClass clazz = enclosingClass.defineOrGetClassUnder(name, superClass);
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(clazz);
 
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), clazz, self, aBlock);
     }
 
     private static IRubyObject classVarAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
         
         if (rubyClass == null) {
             throw runtime.newTypeError("no class/module to define class variable");
         }
         
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         rubyClass.fastSetClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         ClassVarNode iVisited = (ClassVarNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(iVisited.getName());
     }
 
     private static IRubyObject colon2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
         if (leftNode == null) {
             return runtime.getObject().fastGetConstantFrom(iVisited.getName());
         } else {
             IRubyObject result = evalInternal(runtime,context, iVisited.getLeftNode(), self, aBlock);
             if (result instanceof RubyModule) {
                 return ((RubyModule) result).fastGetConstantFrom(iVisited.getName());
             } else {
                 return result.callMethod(context, iVisited.getName(), aBlock);
             }
         }
     }
 
     private static IRubyObject colon3Node(Ruby runtime, Node node) {
         return runtime.getObject().fastGetConstantFrom(((Colon3Node)node).getName());
     }
 
     private static IRubyObject constDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ConstDeclNode iVisited = (ConstDeclNode) node;
         Node constNode = iVisited.getConstNode();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (constNode == null) {
             return context.setConstantInCurrent(iVisited.getName(), result);
         } else if (constNode.nodeId == NodeType.COLON2NODE) {
             RubyModule module = (RubyModule)evalInternal(runtime,context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, aBlock);
             return context.setConstantInModule(iVisited.getName(), module, result);
         } else { // colon3
             return context.setConstantInObject(iVisited.getName(), result);
         }
     }
 
     private static IRubyObject constNode(ThreadContext context, Node node) {
         return context.getConstant(((ConstNode)node).getName());
     }
 
     private static IRubyObject dAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DAsgnNode iVisited = (DAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
 
         // System.out.println("DSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
    
         return result;
     }
 
     private static IRubyObject definedNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefinedNode iVisited = (DefinedNode) node;
         String definition = getDefinition(runtime, context, iVisited.getExpressionNode(), self, aBlock);
         if (definition != null) {
             return runtime.newString(definition);
         } else {
             return runtime.getNil();
         }
     }
 
     private static IRubyObject defnNode(Ruby runtime, ThreadContext context, Node node) {
         DefnNode iVisited = (DefnNode) node;
         
         RubyModule containingClass = context.getRubyClass();
    
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
    
         String name = iVisited.getName();
 
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
 
         if (name == "__id__" || name == "__send__") {
             runtime.getWarnings().warn("redefining `" + name + "' may cause serious problem"); 
         }
 
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         StaticScope scope = iVisited.getScope();
         scope.determineModule();
         
         DefaultMethod newMethod = new DefaultMethod(containingClass, scope, 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), visibility);
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility() == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                             Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.fastNewSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.fastNewSymbol(name));
         }
    
         return runtime.getNil();
     }
     
     private static IRubyObject defsNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefsNode iVisited = (DefsNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         String name = iVisited.getName();
 
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
 
         StaticScope scope = iVisited.getScope();
         scope.determineModule();
       
         DefaultMethod newMethod = new DefaultMethod(rubyClass, scope, iVisited.getBodyNode(), 
                 (ArgsNode) iVisited.getArgsNode(), Visibility.PUBLIC);
    
         rubyClass.addMethod(name, newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
    
         return runtime.getNil();
     }
 
     private static IRubyObject dotNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DotNode iVisited = (DotNode) node;
         return RubyRange.newRange(runtime, 
                 evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock), 
                 evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock), 
                 iVisited.isExclusive());
     }
 
     private static IRubyObject dregexpNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DRegexpNode iVisited = (DRegexpNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         int opts = iVisited.getOptions();
         String lang = ((opts & 16) != 0) ? "n" : null;
         if((opts & 48) == 48) { // param s
             lang = "s";
         } else if((opts & 32) == 32) { // param e
             lang = "e";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
         
         try {
             return RubyRegexp.newRegexp(runtime, string.toString(), iVisited.getOptions(), lang);
         } catch(jregex.PatternSyntaxException e) {
         //                    System.err.println(iVisited.getValue().toString());
         //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
     }
     
     private static IRubyObject dStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DStrNode iVisited = (DStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return string;
     }
 
     private static IRubyObject dSymbolNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DSymbolNode iVisited = (DSymbolNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return runtime.newSymbol(string.toString());
     }
 
     private static IRubyObject dVarNode(Ruby runtime, ThreadContext context, Node node) {
         DVarNode iVisited = (DVarNode) node;
 
         // System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject obj = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         // FIXME: null check is removable once we figure out how to assign to unset named block args
         return obj == null ? runtime.getNil() : obj;
     }
 
     private static IRubyObject dXStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DXStrNode iVisited = (DXStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return self.callMethod(context, "`", string);
     }
 
     private static IRubyObject ensureNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         EnsureNode iVisited = (EnsureNode) node;
         
         // save entering the try if there's nothing to ensure
         if (iVisited.getEnsureNode() != null) {
             IRubyObject result = runtime.getNil();
 
             try {
                 result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
             } finally {
                 evalInternal(runtime,context, iVisited.getEnsureNode(), self, aBlock);
             }
 
             return result;
         }
 
         node = iVisited.getBodyNode();
         return evalInternal(runtime, context, node, self, aBlock);
     }
 
     private static IRubyObject evStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return evalInternal(runtime,context, ((EvStrNode)node).getBody(), self, aBlock).asString();
     }
     
     private static IRubyObject falseNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getFalse());
     }
 
     private static IRubyObject fCallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FCallNode iVisited = (FCallNode) node;
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             return iVisited.callAdapter.call(context, self, args);
         }
 
         while (true) {
             try {
                 return iVisited.callAdapter.call(context, self, args, block);
             } catch (JumpException.RetryJump rj) {
                 // allow loop to retry
             }
         }
     }
 
     private static IRubyObject fixnumNode(Ruby runtime, Node node) {
         return ((FixnumNode)node).getFixnum(runtime);
     }
 
     private static IRubyObject flipNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FlipNode iVisited = (FlipNode) node;
         DynamicScope scope = context.getCurrentScope();
         IRubyObject result = scope.getValue(iVisited.getIndex(), iVisited.getDepth());
    
         if (iVisited.isExclusive()) {
             if (result == null || !result.isTrue()) {
                 result = evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getTrue() : runtime.getFalse();
                 scope.setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 
                 return runtime.getTrue();
             }
         } else {
             if (result == null || !result.isTrue()) {
                 if (evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(),
                             evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue() ? 
                                     runtime.getFalse() : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } 
 
                 return runtime.getFalse();
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     scope.setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         }
     }
 
     private static IRubyObject floatNode(Ruby runtime, Node node) {
         return RubyFloat.newFloat(runtime, ((FloatNode)node).getValue());
     }
 
     private static IRubyObject forNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ForNode iVisited = (ForNode) node;
         
         Block block = SharedScopeBlock.createSharedScopeBlock(context, iVisited, 
                 context.getCurrentScope(), self);
    
         try {
             while (true) {
                 try {
                     ISourcePosition position = context.getPosition();
    
                     IRubyObject recv = null;
                     try {
                         recv = evalInternal(runtime,context, iVisited.getIterNode(), self, aBlock);
                     } finally {
                         context.setPosition(position);
                     }
    
                     return ForNode.callAdapter.call(context, recv, block);
                 } catch (JumpException.RetryJump rj) {
                     // do nothing, allow loop to retry
                 }
             }
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         }
     }
 
     private static IRubyObject globalAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         runtime.getGlobalVariables().set(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject globalVarNode(Ruby runtime, ThreadContext context, Node node) {
         GlobalVarNode iVisited = (GlobalVarNode) node;
         
         return runtime.getGlobalVariables().get(iVisited.getName());
     }
 
     private static IRubyObject hashNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         HashNode iVisited = (HashNode) node;
    
         RubyHash hash = null;
         if (iVisited.getListNode() != null) {
             hash = RubyHash.newHash(runtime);
    
         for (int i = 0; i < iVisited.getListNode().size();) {
                 // insert all nodes in sequence, hash them in the final instruction
                 // KEY
                 IRubyObject key = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
                 IRubyObject value = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
    
                 hash.fastASet(key, value);
             }
         }
    
         if (hash == null) {
             return RubyHash.newHash(runtime);
         }
    
         return hash;
     }
 
     private static IRubyObject instAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         InstAsgnNode iVisited = (InstAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         self.fastSetInstanceVariable(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject instVarNode(Ruby runtime, Node node, IRubyObject self) {
         InstVarNode iVisited = (InstVarNode) node;
         IRubyObject variable = self.fastGetInstanceVariable(iVisited.getName());
    
         if (variable != null) return variable;
         
         runtime.getWarnings().warning(iVisited.getPosition(), "instance variable " + iVisited.getName() + " not initialized");
         
         return runtime.getNil();
     }
 
     private static IRubyObject localAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         LocalAsgnNode iVisited = (LocalAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         //System.out.println("LSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
 
         return result;
     }
 
     private static IRubyObject localVarNode(Ruby runtime, ThreadContext context, Node node) {
         LocalVarNode iVisited = (LocalVarNode) node;
 
         //        System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         return result == null ? runtime.getNil() : result;
     }
 
     private static IRubyObject match2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match2Node iVisited = (Match2Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         return ((RubyRegexp) recv).op_match(value);
     }
     
     private static IRubyObject match3Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match3Node iVisited = (Match3Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         if (value instanceof RubyString) {
             return ((RubyRegexp) recv).op_match(value);
         } else {
             return Match3Node.callAdapter.call(context, value, recv);
         }
     }
 
     private static IRubyObject matchNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return ((RubyRegexp) evalInternal(runtime,context, ((MatchNode)node).getRegexpNode(), self, aBlock)).op_match2();
     }
 
     private static IRubyObject moduleNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ModuleNode iVisited = (ModuleNode) node;
         Node classNameNode = iVisited.getCPath();
 
         RubyModule enclosingModule = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
 
         if (enclosingModule == null) throw runtime.newTypeError("no outer class/module");
 
         String name = ((INameNode) classNameNode).getName();        
 
         RubyModule module = enclosingModule.defineOrGetModuleUnder(name);
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(module);        
 
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), module, self, aBlock);
     }
 
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
         
         switch (iVisited.getValueNode().nodeId) {
         case ARRAYNODE: {
             ArrayNode iVisited2 = (ArrayNode) iVisited.getValueNode();
             return multipleAsgnArrayNode(runtime, context, iVisited, iVisited2, self, aBlock);
         }
         case SPLATNODE: {
             SplatNode splatNode = (SplatNode)iVisited.getValueNode();
             RubyArray rubyArray = splatValue(runtime, evalInternal(runtime, context, splatNode.getValue(), self, aBlock));
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, rubyArray, false);
         }
         default:
             IRubyObject value = evalInternal(runtime, context, iVisited.getValueNode(), self, aBlock);
 
             if (!(value instanceof RubyArray)) {
                 value = RubyArray.newArray(runtime, value);
             }
             
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, (RubyArray)value, false);
         }
     }
 
     private static IRubyObject multipleAsgnArrayNode(Ruby runtime, ThreadContext context, MultipleAsgnNode iVisited, ArrayNode node, IRubyObject self, Block aBlock) {
         IRubyObject[] array = new IRubyObject[node.size()];
 
         for (int i = 0; i < node.size(); i++) {
             Node next = node.get(i);
 
             array[i] = evalInternal(runtime,context, next, self, aBlock);
         }
         return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
     }
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         throw new JumpException.NextJump(result);
     }
 
     private static IRubyObject nilNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getNil());
     }
 
     private static IRubyObject notNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NotNode iVisited = (NotNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock);
         return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
     }
 
     private static IRubyObject nthRefNode(ThreadContext context, Node node) {
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getCurrentFrame().getBackRef());
     }
     
     public static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
         context.pollThreadEvents();
         return result;
     }
 
     private static IRubyObject opAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnNode iVisited = (OpAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
    
         if (iVisited.getOperatorName() == "||") {
             if (value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             value = iVisited.operatorCallAdapter.call(context, value, 
                     evalInternal(runtime, context, iVisited.getValueNode(), self, aBlock));
         }
    
         receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
    
         return pollAndReturn(context, value);
     }
 
     private static IRubyObject opAsgnOrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
         String def = getDefinition(runtime, context, iVisited.getFirstNode(), self, aBlock);
    
         IRubyObject result = runtime.getNil();
         if (def != null) {
             result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         }
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject opElementAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
    
         IRubyObject firstValue = receiver.callMethod(context, MethodIndex.AREF, "[]", args);
    
         if (iVisited.getOperatorName() == "||") {
             if (firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             firstValue = iVisited.callAdapter.call(context, firstValue, 
                     evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         return receiver.callMethod(context, MethodIndex.ASET, "[]=", expandedArgs);
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject postExeNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         PostExeNode iVisited = (PostExeNode) node;
         
         Block block = SharedScopeBlock.createSharedScopeBlock(context, iVisited, context.getCurrentScope(), self);
         
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
         
         return runtime.getNil();
     }
 
     private static IRubyObject preExeNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         PreExeNode iVisited = (PreExeNode) node;
         
         DynamicScope scope = new DynamicScope(iVisited.getScope());
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
 
         // FIXME: I use a for block to implement END node because we need a proc which captures
         // its enclosing scope.   ForBlock now represents these node and should be renamed.
         Block block = Block.createBlock(context, iVisited, context.getCurrentScope(), self);
         
         block.yield(context, null);
         
         context.postScopedBody();
 
         return runtime.getNil();
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         throw JumpException.REDO_JUMP;
     }
 
     private static IRubyObject regexpNode(Ruby runtime, Node node) {
         RegexpNode iVisited = (RegexpNode) node;
         if(iVisited.literal == null) {
             int opts = iVisited.getOptions();
             String lang = ((opts & 16) == 16) ? "n" : null;
             if((opts & 48) == 48) { // param s
                 lang = "s";
             } else if((opts & 32) == 32) { // param e
                 lang = "e";
             } else if((opts & 64) != 0) { // param u
                 lang = "u";
             }
         
             IRubyObject noCaseGlobal = runtime.getGlobalVariables().get("$=");
         
             int extraOptions = noCaseGlobal.isTrue() ? ReOptions.RE_OPTION_IGNORECASE : 0;
 
             try {
                 iVisited.literal = RubyRegexp.newRegexp(runtime, iVisited.getPattern(runtime, extraOptions), lang);
             } catch(PatternSyntaxException e) {
                 throw runtime.newRegexpError(e.getMessage());
             }
         }
         return iVisited.literal;
     }
 
     private static IRubyObject rescueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RescueNode iVisited = (RescueNode)node;
         RescuedBlock : while (true) {
             IRubyObject globalExceptionState = runtime.getGlobalVariables().get("$!");
             boolean anotherExceptionRaised = false;
             try {
                 // Execute rescue block
                 IRubyObject result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
 
                 // If no exception is thrown execute else block
                 if (iVisited.getElseNode() != null) {
                     if (iVisited.getRescueNode() == null) {
                         runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
                     }
                     result = evalInternal(runtime,context, iVisited.getElseNode(), self, aBlock);
                 }
 
                 return result;
             } catch (RaiseException raiseJump) {
                 RubyException raisedException = raiseJump.getException();
                 // TODO: Rubicon TestKernel dies without this line.  A cursory glance implies we
                 // falsely set $! to nil and this sets it back to something valid.  This should 
                 // get fixed at the same time we address bug #1296484.
                 runtime.getGlobalVariables().set("$!", raisedException);
 
+                if (raisedException.isKindOf(runtime.fastGetClass("SystemExit"))) {
+                    throw raiseJump;
+                }
+
                 RescueBodyNode rescueNode = iVisited.getRescueNode();
 
                 while (rescueNode != null) {
                     Node  exceptionNodes = rescueNode.getExceptionNodes();
                     ListNode exceptionNodesList;
                     
                     if (exceptionNodes instanceof SplatNode) {                    
                         exceptionNodesList = (ListNode) evalInternal(runtime,context, exceptionNodes, self, aBlock);
                     } else {
                         exceptionNodesList = (ListNode) exceptionNodes;
                     }
 
                     IRubyObject[] exceptions;
                     if (exceptionNodesList == null) {
                         exceptions = new IRubyObject[] {runtime.fastGetClass("StandardError")};
                     } else {
                         exceptions = setupArgs(runtime, context, exceptionNodes, self, aBlock);
                     }
                     if (RuntimeHelpers.isExceptionHandled(raisedException, exceptions, runtime, context, self).isTrue()) {
                         try {
                             return evalInternal(runtime,context, rescueNode, self, aBlock);
                         } catch (JumpException.RetryJump rj) {
                             // should be handled in the finally block below
                             //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                             //state.threadContext.setRaisedException(null);
                             continue RescuedBlock;
                         } catch (RaiseException je) {
                             anotherExceptionRaised = true;
                             throw je;
                         }
                     }
                     
                     rescueNode = rescueNode.getOptRescueNode();
                 }
 
                 // no takers; bubble up
                 throw raiseJump;
             } finally {
                 // clear exception when handled or retried
                 if (!anotherExceptionRaised)
                     runtime.getGlobalVariables().set("$!", globalExceptionState);
             }
         }
     }
 
     private static IRubyObject retryNode(ThreadContext context) {
         context.pollThreadEvents();
    
         throw JumpException.RETRY_JUMP;
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         throw new JumpException.ReturnJump(context.getFrameJumpTarget(), result);
     }
 
     private static IRubyObject rootNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RootNode iVisited = (RootNode) node;
         DynamicScope scope = iVisited.getScope();
         
         // Serialization killed our dynamic scope.  We can just create an empty one
         // since serialization cannot serialize an eval (which is the only thing
         // which is capable of having a non-empty dynamic scope).
         if (scope == null) {
             scope = new DynamicScope(iVisited.getStaticScope());
         }
         
         StaticScope staticScope = scope.getStaticScope();
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         
         if (staticScope.getModule() == null) {
             staticScope.setModule(runtime.getObject());
         }
 
         try {
             return evalInternal(runtime, context, iVisited.getBodyNode(), self, aBlock);
         } finally {
             context.postScopedBody();
         }
     }
 
     private static IRubyObject sClassNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SClassNode iVisited = (SClassNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
 
         RubyClass singletonClass;
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             singletonClass = receiver.getSingletonClass();
         }
 
         StaticScope scope = iVisited.getScope();
         scope.setModule(singletonClass);
         
         return evalClassDefinitionBody(runtime, context, scope, iVisited.getBodyNode(), singletonClass, self, aBlock);
     }
 
     private static IRubyObject splatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return splatValue(runtime, evalInternal(runtime,context, ((SplatNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject strNode(Ruby runtime, Node node) {
         return runtime.newStringShared((ByteList) ((StrNode) node).getValue());
     }
     
     private static IRubyObject superNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SuperNode iVisited = (SuperNode) node;
    
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self, aBlock);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // If no explicit block passed to super, then use the one passed in, unless it's explicitly cleared with nil
         if (iVisited.getIterNode() == null) {
             if (!block.isGiven()) block = aBlock;
         }
         
         return self.callSuper(context, args, block);
     }
     
     private static IRubyObject sValueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aValueSplat(runtime, evalInternal(runtime,context, ((SValueNode) node).getValue(), self, aBlock));
     }
     
     private static IRubyObject symbolNode(Ruby runtime, Node node) {
         return ((SymbolNode)node).getSymbol(runtime);
     }
     
     private static IRubyObject toAryNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aryToAry(runtime, evalInternal(runtime,context, ((ToAryNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject trueNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getTrue());
     }
     
     private static IRubyObject undefNode(Ruby runtime, ThreadContext context, Node node) {
         UndefNode iVisited = (UndefNode) node;
         RubyModule module = context.getRubyClass();
    
         if (module == null) {
             throw runtime.newTypeError("No class to undef method '" + iVisited.getName() + "'.");
         }
         
         module.undef(iVisited.getName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject untilNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         UntilNode iVisited = (UntilNode) node;
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || !(evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException.RedoJump rj) {
                     continue;
                 } catch (JumpException.NextJump nj) {
                     break loop;
                 } catch (JumpException.BreakJump bj) {
                     // JRUBY-530 until case
                     if (bj.getTarget() == aBlock) {
                          bj.setTarget(null);
 
                          throw bj;
                     }
 
                     result = (IRubyObject) bj.getValue();
 
                     break outerLoop;
                 }
             }
         }
 
         if (result == null) {
             result = runtime.getNil();
         }
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject valiasNode(Ruby runtime, Node node) {
         VAliasNode iVisited = (VAliasNode) node;
         runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject vcallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         VCallNode iVisited = (VCallNode) node;
 
         return iVisited.callAdapter.call(context, self);
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (RaiseException re) {
                     if (re.getException().isKindOf(runtime.fastGetClass("LocalJumpError"))) {
                         RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
                         
                         IRubyObject reason = jumpError.reason();
                         
                         // admittedly inefficient
                         if (reason.asSymbol().equals("break")) {
                             return jumpError.exit_value();
                         } else if (reason.asSymbol().equals("next")) {
                             break loop;
                         } else if (reason.asSymbol().equals("redo")) {
                             continue;
                         }
                     }
                     
                     throw re;
                 } catch (JumpException.RedoJump rj) {
                     continue;
                 } catch (JumpException.NextJump nj) {
                     break loop;
                 } catch (JumpException.BreakJump bj) {
                     // JRUBY-530, while case
                     if (bj.getTarget() == aBlock) {
                         bj.setTarget(null);
 
                         throw bj;
                     }
 
                     result = (IRubyObject) bj.getValue();
                     break outerLoop;
                 }
             }
         }
         if (result == null) {
             result = runtime.getNil();
         }
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newStringShared((ByteList) ((XStrNode) node).getValue()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = null;
         if (iVisited.getArgsNode() != null) {
             result = evalInternal(runtime, context, iVisited.getArgsNode(), self, aBlock);
         }
 
         Block block = context.getCurrentFrame().getBlock();
 
         return block.yield(context, result, null, null, iVisited.getCheckState());
     }
 
     private static IRubyObject zArrayNode(Ruby runtime) {
         return runtime.newArray();
     }
     
     private static IRubyObject zsuperNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Block block = getBlock(runtime, context, self, aBlock, ((ZSuperNode) node).getIterNode());
         return RuntimeHelpers.callZSuper(runtime, context, block, self);
     }
 
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static void callTraceFunction(Ruby runtime, ThreadContext context, int event) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callEventHooks(context, event, context.getPosition().getFile(), context.getPosition().getStartLine(), name, type);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, EventHook.RUBY_EVENT_CLASS);
             }
 
             return evalInternal(runtime,context, bodyNode, type, block);
         } finally {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, EventHook.RUBY_EVENT_END);
             }
             
             context.postClassEval();
         }
     }
 
     private static String getArgumentDefinition(Ruby runtime, ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             for (int i = 0; i < ((ArrayNode)node).size(); i++) {
                 Node iterNode = ((ArrayNode)node).get(i);
                 if (getDefinitionInner(runtime, context, iterNode, self, block) == null) return null;
             }
         } else if (getDefinitionInner(runtime, context, node, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock, Node blockNode) {
         if (blockNode == null) return Block.NULL_BLOCK;
         
         if (blockNode instanceof IterNode) {
             IterNode iterNode = (IterNode) blockNode;
 
             StaticScope scope = iterNode.getScope();
             scope.determineModule();
             
             // Create block for this iter node
             // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
             return Block.createBlock(context, iterNode, 
                     new DynamicScope(scope, context.getCurrentScope()), self);
         } else if (blockNode instanceof BlockPassNode) {
             BlockPassNode blockPassNode = (BlockPassNode) blockNode;
             IRubyObject proc = evalInternal(runtime,context, blockPassNode.getBodyNode(), self, currentBlock);
             
             return RuntimeHelpers.getBlockFromBlockPassBody(proc, currentBlock);
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     public static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyModule rubyClass = scope.getModule();
         if (rubyClass.isSingleton()) {
             scope = scope.getPreviousCRefScope();
             rubyClass = scope.getModule();
             if (scope.getPreviousCRefScope() == null) {
                 runtime.getWarnings().warn("class variable access from toplevel singleton method");
             }            
         }
         return rubyClass;
     }
 
     private static String getDefinition(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return getDefinitionInner(runtime, context, node, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
 
     private static String getDefinitionInner(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return "expression";
         
         switch(node.nodeId) {
         case ATTRASSIGNNODE: {
             AttrAssignNode iVisited = (AttrAssignNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (visibility != Visibility.PRIVATE && 
                             (visibility != Visibility.PROTECTED || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime,context, iVisited.getArgsNode(), "assignment", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case BACKREFNODE: {
             IRubyObject backref = context.getCurrentFrame().getBackRef();
             if(backref instanceof RubyMatchData) {
                 return "$" + ((BackRefNode) node).getType();
             } else {
                 return null;
             }
         }
         case CALLNODE: {
             CallNode iVisited = (CallNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (visibility != Visibility.PRIVATE && 
                             (visibility != Visibility.PROTECTED || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
         case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE:
         case MULTIPLEASGNNODE: case OPASGNNODE: case OPELEMENTASGNNODE:
             return "assignment";
             
         case CLASSVARNODE: {
             ClassVarNode iVisited = (ClassVarNode) node;
             //RubyModule module = context.getRubyClass();
             RubyModule module = context.getCurrentScope().getStaticScope().getModule();
             if (module == null && self.getMetaClass().fastIsClassVarDefined(iVisited.getName())) {
                 return "class variable";
             } else if (module.fastIsClassVarDefined(iVisited.getName())) {
                 return "class variable";
             } 
             IRubyObject attached = null;
             if (module.isSingleton()) attached = ((MetaClass)module).getAttached();
             if (attached instanceof RubyModule) {
                 module = (RubyModule)attached;
 
                 if (module.fastIsClassVarDefined(iVisited.getName())) return "class variable"; 
             }
 
             return null;
         }
         case COLON3NODE:
         case COLON2NODE: {
             Colon3Node iVisited = (Colon3Node) node;
 
             try {
                 IRubyObject left = runtime.getObject();
                 if (iVisited instanceof Colon2Node) {
                     left = ASTInterpreter.eval(runtime, context, ((Colon2Node) iVisited).getLeftNode(), self, aBlock);
                 }
 
                 if (left instanceof RubyModule &&
                         ((RubyModule) left).fastGetConstantAt(iVisited.getName()) != null) {
                     return "constant";
                 } else if (left.getMetaClass().isMethodBound(iVisited.getName(), true)) {
                     return "method";
                 }
             } catch (JumpException excptn) {}
             
             return null;
         }
         case CONSTNODE:
             if (context.getConstantDefined(((ConstNode) node).getName())) {
                 return "constant";
             }
             return null;
         case DVARNODE:
             return "local-variable(in-block)";
         case FALSENODE:
             return "false";
         case FCALLNODE: {
             FCallNode iVisited = (FCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
             }
             
             return null;
         }
         case GLOBALVARNODE:
             if (runtime.getGlobalVariables().isDefined(((GlobalVarNode) node).getName())) {
                 return "global-variable";
             }
             return null;
         case INSTVARNODE:
             if (self.fastHasInstanceVariable(((InstVarNode) node).getName())) {
                 return "instance-variable";
             }
             return null;
         case LOCALVARNODE:
             return "local-variable";
         case MATCH2NODE: case MATCH3NODE:
             return "method";
         case NILNODE:
             return "nil";
         case NTHREFNODE: {
             IRubyObject backref = context.getCurrentFrame().getBackRef();
             if(backref instanceof RubyMatchData) {
                 ((RubyMatchData)backref).use();
                 if(!((RubyMatchData)backref).group(((NthRefNode) node).getMatchNumber()).isNil()) {
                     return "$" + ((NthRefNode) node).getMatchNumber();
                 }
             }
             return null;
         }
         case SELFNODE:
             return "self";
         case SUPERNODE: {
             SuperNode iVisited = (SuperNode) node;
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "super", self, aBlock);
             }
             
             return null;
         }
         case TRUENODE:
             return "true";
         case VCALLNODE: {
             VCallNode iVisited = (VCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return "method";
             }
             
             return null;
         }
         case YIELDNODE:
             return aBlock.isGiven() ? "yield" : null;
         case ZSUPERNODE: {
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return "super";
             }
             return null;
         }
         default:
             try {
                 ASTInterpreter.eval(runtime, context, node, self, aBlock);
                 return "expression";
             } catch (JumpException jumpExcptn) {}
         }
         
         return null;
     }
 
     private static RubyModule getEnclosingModule(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         RubyModule enclosingModule = null;
 
         if (node instanceof Colon2Node) {
             IRubyObject result = evalInternal(runtime,context, ((Colon2Node) node).getLeftNode(), self, block);
 
             if (result != null && !result.isNil()) {
                 enclosingModule = (RubyModule) result;
             }
         } else if (node instanceof Colon3Node) {
             enclosingModule = runtime.getObject();
         }
 
         if (enclosingModule == null) {
             enclosingModule = context.getCurrentScope().getStaticScope().getModule();
         }
 
         return enclosingModule;
     }
 
     /**
      * Helper method.
      *
      * test if a trace function is avaiable.
      *
      */
     private static boolean isTrace(Ruby runtime) {
         return runtime.hasEventHooks();
     }
 
     private static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             ISourcePosition position = context.getPosition();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = evalInternal(runtime,context, argsArrayNode.get(i), self, aBlock);
             }
 
             context.setPosition(position);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(evalInternal(runtime,context, node, self, aBlock));
     }
 
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 
     // Used by the compiler to simplify arg processing
     public static RubyArray splatValue(IRubyObject value, Ruby runtime) {
         return splatValue(runtime, value);
     }
     public static IRubyObject aValueSplat(IRubyObject value, Ruby runtime) {
         return aValueSplat(runtime, value);
     }
     public static IRubyObject aryToAry(IRubyObject value, Ruby runtime) {
         return aryToAry(runtime, value);
     }
 }
