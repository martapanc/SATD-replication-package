diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 5b05f005ef..abb8c346c2 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1014,1442 +1014,1442 @@ public final class Ruby {
         if (superClass == null) {
             warnings.warn("no super class for `" + parent.getName() + "::" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, true);
     }
 
     /** rb_define_module
      *
      */
     public RubyModule defineModule(String name) {
         IRubyObject moduleObj = objectClass.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, objectClass, false);
     }
 
     /** rb_define_module_under
     *
     */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, parent, true);
     }
 
     /**
      * In the current context, get the named module. If it doesn't exist a
      * new module is created.
      */
     public RubyModule getOrCreateModule(String name) {
         IRubyObject module = objectClass.getConstantAt(name);
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!module.isModule()) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
     }
 
 
     /** Getter for property securityLevel.
      * @return Value of property securityLevel.
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
     /** Setter for property securityLevel.
      * @param safeLevel New value of property securityLevel.
      */
     public void setSafeLevel(int safeLevel) {
         this.safeLevel = safeLevel;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public void secure(int level) {
         if (level <= safeLevel) {
             throw newSecurityError("Insecure operation '" + getCurrentContext().getFrameName() + "' at level " + safeLevel);
         }
     }
 
     // FIXME moved this hear to get what's obviously a utility method out of IRubyObject.
     // perhaps security methods should find their own centralized home at some point.
     public void checkSafeString(IRubyObject object) {
         if (getSafeLevel() > 0 && object.isTaint()) {
             ThreadContext tc = getCurrentContext();
             if (tc.getFrameName() != null) {
                 throw newSecurityError("Insecure operation - " + tc.getFrameName());
             }
             throw newSecurityError("Insecure operation: -r");
         }
         secure(4);
         if (!(object instanceof RubyString)) {
             throw newTypeError(
                 "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
         }
     }
 
     /**
      * Retrieve mappings of cached methods to where they have been cached.  When a cached
      * method needs to be invalidated this map can be used to remove all places it has been
      * cached.
      *
      * @return the mappings of where cached methods have been stored
      */
     public CacheMap getCacheMap() {
         return cacheMap;
     }
     
     /**
      * Retrieve method cache.
      * 
      * @return method cache where cached methods have been stored
      */
     public MethodCache getMethodCache() {
         return methodCache;
     }
 
     /**
      * @see org.jruby.Ruby#getRuntimeInformation
      */
     public Map<Object, Object> getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable<Object, Object>() : runtimeInformation;
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** Getter for property rubyTopSelf.
      * @return Value of property rubyTopSelf.
      */
     public IRubyObject getTopSelf() {
         return topSelf;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     /** ruby_init
      *
      */
     // TODO: Figure out real dependencies between vars and reorder/refactor into better methods
     private void init() {
         ThreadContext tc = getCurrentContext();
 
         defineGlobalVERBOSE();
         defineGlobalDEBUG();
 
         this.regexpFactory = RegexpFactory.getFactory(this.config.getDefaultRegexpEngine());
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
         
         // core classes are initialized, ensure top scope has Object as cref
         tc.getCurrentScope().getStaticScope().setModule(objectClass);
 
         verbose = falseObject;
         debug = falseObject;
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
         
         methodCache.initialized();
     }
 
     private void initLibraries() {
         loadService = config.createLoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
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
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("jruby/ext.rb", new RubyJRuby.ExtLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
         registerBuiltin("nkf.rb", new NKFLibrary());
         registerBuiltin("stringio.rb", new StringIOLibrary());
         registerBuiltin("strscan.rb", new StringScannerLibrary());
         registerBuiltin("zlib.rb", new ZlibLibrary());
         registerBuiltin("yaml_internal.rb", new YamlLibrary());
         registerBuiltin("enumerator.rb", new EnumeratorLibrary());
         registerBuiltin("generator_internal.rb", new Generator.Service());
         registerBuiltin("readline.rb", new Readline.Service());
         registerBuiltin("thread.so", new ThreadLibrary());
         registerBuiltin("openssl.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("rubygems"));
                     runtime.getTopSelf().callMethod(runtime.getCurrentContext(),"gem",runtime.newString("jruby-openssl"));
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("openssl.rb"));
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
         registerBuiltin("io/wait.so", new IOWaitLibrary());
         registerBuiltin("etc.so", NO_OP_LIBRARY);
         registerBuiltin("fiber.so", new FiberLibrary());
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
 
-    public RubyProc newProc(boolean isLambda, Block block) {
-        if (!isLambda && block.getProcObject() != null) return block.getProcObject();
+    public RubyProc newProc(Block.Type type, Block block) {
+        if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
-        RubyProc proc =  RubyProc.newProc(this, isLambda);
+        RubyProc proc =  RubyProc.newProc(this, type);
 
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
         RubyClass exc = fastGetClass("SystemExit");
         IRubyObject[]exArgs = new IRubyObject[]{newFixnum(status), newString("exit")};
         return new RaiseException((RubyException)exc.newInstance(exArgs, Block.NULL_BLOCK));
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
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 9cac6c6a58..1238da6712 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1582 +1,1582 @@
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
 import org.jruby.anno.JRubyMethod;
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
         runtime.setHash(hashc);
         hashc.index = ClassIndex.HASH;
         hashc.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyHash;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyHash.class);
 
         hashc.includeModule(runtime.getEnumerable());
         
         hashc.defineAnnotatedMethods(RubyHash.class);
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
     @JRubyMethod(name = "[]", rest = true, frame = true, meta = true)
     public static IRubyObject create(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass) recv;
         RubyHash hash;
 
         if (args.length == 1 && args[0] instanceof RubyHash) {
             RubyHash otherHash = (RubyHash)args[0];
             return new RubyHash(recv.getRuntime(), klass, otherHash.internalCopyTable(), otherHash.size); // hash_alloc0
         }
 
         if ((args.length & 1) != 0) throw recv.getRuntime().newArgumentError("odd number of args for Hash");
 
         hash = (RubyHash)klass.allocate();
         for (int i=0; i < args.length; i+=2) hash.op_aset(args[i], args[i+1]);
 
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
     
     /*
      *  Constructor for internal usage (mainly for Array#|, Array#&, Array#- and Array#uniq)
      *  it doesn't initialize ifNone field 
      */
     RubyHash(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getHash(), objectSpace);
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
     @JRubyMethod(name = "initialize", optional = 1, frame = true)
     public IRubyObject initialize(IRubyObject[] args, final Block block) {
             modify();
 
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments");
-            ifNone = getRuntime().newProc(false, block);
+            ifNone = getRuntime().newProc(Block.Type.PROC, block);
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
     @JRubyMethod(name = "default", optional = 1, frame = true)
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
     @JRubyMethod(name = "default=", required = 1)
     public IRubyObject default_value_set(final IRubyObject defaultValue) {
         modify();
 
         ifNone = defaultValue;
         flags &= ~PROCDEFAULT_HASH_F;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      * 
      */    
     @JRubyMethod(name = "default_proc", frame = true)
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
 
     /** inspect_hash
      * 
      */
     public IRubyObject inspectHash() {
         try {
             Ruby runtime = getRuntime();
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = runtime.getCurrentContext();
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (!firstEntry) sb.append(sep);
                     sb.append(entry.key.callMethod(context, "inspect")).append(arrow);
                     sb.append(entry.value.callMethod(context, "inspect"));
                     firstEntry = false;
                 }
             }
             sb.append("}");
             return runtime.newString(sb.toString());
         } finally {
             postIter();
         }         
     }
 
     /** rb_hash_inspect
      * 
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         if (size == 0) return getRuntime().newString("{}");
         if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
         
         try {
             getRuntime().registerInspecting(this);
             return inspectHash();
         } finally {
             getRuntime().unregisterInspecting(this);
         }        
     }
 
     /** rb_hash_size
      * 
      */
     @JRubyMethod(name = {"size", "length"})
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(size);
     }
 
     /** rb_hash_empty_p
      * 
      */
     @JRubyMethod(name = "empty?")
     public RubyBoolean empty_p() {
         return size == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_hash_to_a
      * 
      */
     @JRubyMethod(name = "to_a")
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {                
                     result.append(RubyArray.newArray(runtime, entry.key, entry.value));
                 }
             }
         } finally {postIter();}
 
         result.setTaint(isTaint());
         return result;
     }
 
     /** rb_hash_to_s & to_s_hash
      * 
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
         try {
             getRuntime().registerInspecting(this);
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_hash_rehash
      * 
      */
     @JRubyMethod(name = "rehash")
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
     @JRubyMethod(name = "to_hash")
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
     @JRubyMethod(name = {"[]=", "store"}, required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
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
 
     /**
      * Note: this is included as a compatibility measure for AR-JDBC
      * @deprecated use RubyHash.op_aset instead
      */
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         return op_aset(key, value);
     }
 
     /**
      * Note: this is included as a compatibility measure for Mongrel+JRuby
      * @deprecated use RubyHash.op_aref instead
      */
     public IRubyObject aref(IRubyObject key) {
         return op_aref(key);
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     /** rb_hash_aref
      * 
      */
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {        
         IRubyObject value;
         return ((value = internalGet(key)) == null) ? callMethod(getRuntime().getCurrentContext(), MethodIndex.DEFAULT, "default", key) : value;
     }
 
     /** rb_hash_fetch
      * 
      */
     @JRubyMethod(name = "fetch", required = 1, optional = 1, frame = true)
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
     @JRubyMethod(name = {"has_key?", "key?", "include?", "member?"}, required = 1)
     public RubyBoolean has_key_p(IRubyObject key) {
         return internalGetEntry(key) == null ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_hash_has_value
      * 
      */
     @JRubyMethod(name = {"has_value?", "value?"}, required = 1)
     public RubyBoolean has_value_p(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (equalInternal(context, entry.value, value).isTrue()) return runtime.getTrue();
                 }
             }
         } finally {postIter();}
         return runtime.getFalse();
     }
 
     /** rb_hash_each
      * 
      */
     @JRubyMethod(name = "each", frame = true)
     public RubyHash each(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
     @JRubyMethod(name = "each_pair", frame = true)
     public RubyHash each_pair(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
     @JRubyMethod(name = "each_value", frame = true)
     public RubyHash each_value(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.value);
                 }
             }
         } finally {postIter();}
 
         return this;        
 	}
 
     /** rb_hash_each_key
      * 
      */
     @JRubyMethod(name = "each_key", frame = true)
     public RubyHash each_key(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.key);
                 }
             }
         } finally {postIter();}
 
         return this;  
 	}
 
     /** rb_hash_sort
      * 
      */
     @JRubyMethod(name = "sort", frame = true)
     public RubyArray sort(Block block) {
 		return to_a().sort_bang(block);
 	}
 
     /** rb_hash_index
      * 
      */
     @JRubyMethod(name = "index", required = 1)
     public IRubyObject index(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (equalInternal(context, entry.value, value).isTrue()) return entry.key;
                 }
             }
         } finally {postIter();}
 
         return getRuntime().getNil();        
     }
 
     /** rb_hash_indexes
      * 
      */
     @JRubyMethod(name = {"indexes", "indices"}, rest = true)
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(op_aref(indices[i]));
         }
 
         return values;
     }
 
     /** rb_hash_keys 
      * 
      */
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         Ruby runtime = getRuntime();
         RubyArray keys = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     keys.append(entry.key);
                 }
             }
         } finally {postIter();}
 
         return keys;          
     }
 
     /** rb_hash_values
      * 
      */
     @JRubyMethod(name = "values")
     public RubyArray rb_values() {
         RubyArray values = RubyArray.newArray(getRuntime(), size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
 
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (this == other ) return getRuntime().getTrue();
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) return getRuntime().getFalse();
             return equalInternal(getRuntime().getCurrentContext(), other, this);
         }
 
         RubyHash otherHash = (RubyHash)other;
         if (size != otherHash.size) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();        
         ThreadContext context = runtime.getCurrentContext();
 
         if (EQUAL_CHECK_DEFAULT_VALUE) {
             if (!equalInternal(context, ifNone, otherHash.ifNone).isTrue() &&                    
                (flags & PROCDEFAULT_HASH_F) != (otherHash.flags & PROCDEFAULT_HASH_F)) return runtime.getFalse();
             }
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     IRubyObject value = otherHash.internalGet(entry.key);
                     if (value == null) return runtime.getFalse();
                     if (!equalInternal(context, entry.value, value).isTrue()) return runtime.getFalse();
                 }
             }
         } finally {postIter();}        
 
         return runtime.getTrue();
     }
 
     /** rb_hash_shift
      * 
      */
     @JRubyMethod(name = "shift")
     public IRubyObject shift() {
 		modify();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
     
     public final RubyHashEntry fastDelete(IRubyObject key) {
         return internalDelete(key);
     }
 
     /** rb_hash_delete
      * 
      */
     @JRubyMethod(name = "delete", required = 1, frame = true)
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
     @JRubyMethod(name = "select", frame = true)
     public IRubyObject select(Block block) {
         RubyArray result = getRuntime().newArray();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
     @JRubyMethod(name = "delete_if", frame = true)
     public RubyHash delete_if(Block block) {
         modify();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
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
     @JRubyMethod(name = "reject", frame = true)
     public RubyHash reject(Block block) {
         return ((RubyHash)dup()).delete_if(block);
 	}
 
     /** rb_hash_reject_bang
      * 
      */
     @JRubyMethod(name = "reject!", frame = true)
     public IRubyObject reject_bang(Block block) {
         int n = size;
         delete_if(block);
         if (n == size) return getRuntime().getNil();
         return this;
 			}
 
     /** rb_hash_clear
      * 
      */
     @JRubyMethod(name = "clear")
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
     @JRubyMethod(name = "invert")
     public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     result.op_aset(entry.value, entry.key);
                 }
             }
         } finally {postIter();}        
 
         return result;        
 	}
 
     /** rb_hash_update
      * 
      */
     @JRubyMethod(name = {"merge!", "update"}, required = 1, frame = true)
     public RubyHash merge_bang(IRubyObject other, Block block) {
         modify();
 
         RubyHash otherHash = other.convertToHash();
 
         try {
              otherHash.preIter();
              RubyHashEntry[]ltable = otherHash.table;
         if (block.isGiven()) {
                  Ruby runtime = getRuntime();
                  ThreadContext context = runtime.getCurrentContext();
 
                  for (int i = 0; i < ltable.length; i++) {
                      for (RubyHashEntry entry = ltable[i]; (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                          IRubyObject value;
                          if (internalGet(entry.key) != null)
                              value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{entry.key, op_aref(entry.key), entry.value}));
                          else
                              value = entry.value;
                          op_aset(entry.key, value);
                      }
                  }
             } else { 
                 for (int i = 0; i < ltable.length; i++) {
                     for (RubyHashEntry entry = ltable[i]; (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                         op_aset(entry.key, entry.value);
                     }
                 }
             }  
         } finally {otherHash.postIter();}
 
         return this;
     }
 
     /** rb_hash_merge
      * 
      */
     @JRubyMethod(name = "merge", required = 1, frame = true)
     public RubyHash merge(IRubyObject other, Block block) {
         return ((RubyHash)dup()).merge_bang(other, block);
     }
 
     /** rb_hash_replace
      * 
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1)
     public RubyHash replace(IRubyObject other) {
         RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         try {
             otherHash.preIter();
             RubyHashEntry[]ltable = otherHash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     op_aset(entry.key, entry.value);
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
     @JRubyMethod(name = "values_at", rest = true)
     public RubyArray values_at(IRubyObject[] args) {
         RubyArray result = RubyArray.newArray(getRuntime(), args.length);
         for (int i = 0; i < args.length; i++) {
             result.append(op_aref(args[i]));
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
                 for (RubyHashEntry entry = ltable[i]; (entry = hash.checkIter(ltable, entry)) != null; entry = entry.next) {
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
             result.op_aset(input.unmarshalObject(), input.unmarshalObject());
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
                 for (RubyHashEntry entry = ltable[i]; (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (equalInternal(context, entry.value, element).isTrue()) return true;
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
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index ce39f0a0a6..061c6d2692 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1133 +1,1133 @@
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Iterator;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         runtime.getObject().dispatcher = objectCallbackFactory.createDispatcher(runtime.getObject());
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         return module;
     }
 
     @JRubyMethod(name = "at_exit", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
-        return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
+        return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : recv.getRuntime().getObject();
         String name = module.getName() + "::" + symbol.asSymbol();
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         final String baseName = symbol.asSymbol(); // interned, OK for "fast" methods
         final RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String nm = module.getName() + "::" + baseName;
         
         IRubyObject undef = runtime.getUndef();
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != undef) return runtime.getNil();
         
         module.fastStoreConstant(baseName, undef);
         
         loadService.addAutoload(nm, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 boolean required = loadService.require(file.toString());
                 
                 // File to be loaded by autoload has already been or is being loaded.
                 if (!required) return null;
                 
                 return module.fastGetConstant(baseName);
             }
         });
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw runtime.newArgumentError("no id given");
         
         String name = args[0].asSymbol();
         ThreadContext context = runtime.getCurrentContext();
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         String format = null;
 
         boolean noMethod = true; // NoMethodError
 
         if (lastVis == Visibility.PRIVATE) {
             format = "private method `%s' called for %s";
         } else if (lastVis == Visibility.PROTECTED) {
             format = "protected method `%s' called for %s";
         } else if (lastCallType == CallType.VARIABLE) {
             format = "undefined local variable or method `%s' for %s";
             noMethod = false; // NameError
         } else if (lastCallType == CallType.SUPER) {
             format = "super: no superclass method `%s'";
         }
 
         if (format == null) format = "undefined method `%s' for %s";
 
         String description = null;
         
         if (recv.isNil()) {
             description = "nil";
         } else if (recv instanceof RubyBoolean && recv.isTrue()) {
             description = "true";
         } else if (recv instanceof RubyBoolean && !recv.isTrue()) {
             description = "false";
         } else {
             if (name.equals("inspect") || name.equals("to_s")) {
                 description = recv.anyToString().toString();
             } else {
                 IRubyObject d;
                 try {
                     d = recv.callMethod(context, "inspect");
                     if (d.getMetaClass() == recv.getMetaClass() || (d instanceof RubyString && ((RubyString)d).length().getLongValue() > 65)) {
                         d = recv.anyToString();
                     }
                 } catch (JumpException je) {
                     d = recv.anyToString();
                 }
                 description = d.toString();
             }
         }
         if (description.length() == 0 || (description.length() > 0 && description.charAt(0) != '#')) {
             description = description + ":" + recv.getMetaClass().getRealClass().getName();            
         }
         
         IRubyObject[]exArgs = new IRubyObject[noMethod ? 3 : 2];
 
         RubyArray arr = runtime.newArray(args[0], runtime.newString(description));
         RubyString msg = runtime.newString(Sprintf.sprintf(runtime.newString(format), arr).toString());
         
         if (recv.isTaint()) msg.setTaint(true);
 
         exArgs[0] = msg;
         exArgs[1] = args[0];
 
         RubyClass exc;
         if (noMethod) {
             IRubyObject[]NMEArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, NMEArgs, 0, NMEArgs.length);
             exArgs[2] = runtime.newArrayNoCopy(NMEArgs);
             exc = runtime.fastGetClass("NoMethodError");
         } else {
             exc = runtime.fastGetClass("NameError");
         }
         
         throw new RaiseException((RubyException)exc.newInstance(exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,3);
         String arg = args[0].convertToString().toString();
         Ruby runtime = recv.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             try {
                 Process p = new ShellLauncher(runtime).run(RubyString.newString(runtime,command));
                 RubyIO io = new RubyIO(runtime, p);
                 
                 if (block.isGiven()) {
                     try {
                         block.yield(recv.getRuntime().getCurrentContext(), io);
                         return runtime.getNil();
                     } finally {
                         io.close();
                     }
                 }
 
                 return io;
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } 
 
         return RubyFile.open(runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(Arity.checkArgumentCount(recv.getRuntime(), args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.checkArrayType();
 
         if (value.isNil()) {
             if (object.getMetaClass().searchMethod("to_a").getImplementationClass() != recv.getRuntime().getKernel()) {
                 value = object.callMethod(recv.getRuntime().getCurrentContext(), MethodIndex.TO_A, "to_a");
                 if (!(value instanceof RubyArray)) throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                 return value;
             } else {
                 return recv.getRuntime().newArray(object);
             }
         }
         return value;
     }
 
     @JRubyMethod(name = "Float", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)object.convertToType(recv.getRuntime().getFloat(), MethodIndex.TO_F, "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(recv.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
         }
         
         IRubyObject tmp = object.convertToType(recv.getRuntime().getInteger(), MethodIndex.TO_INT, "to_int", false);
         if (tmp.isNil()) return object.convertToInteger(MethodIndex.TO_I, "to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.convertToType(recv.getRuntime().getString(), MethodIndex.TO_S, "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getPreviousFrame().getLastLine();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     @JRubyMethod(name = "sub!", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     @JRubyMethod(name = "sub", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub!", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     @JRubyMethod(name = "gsub", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chop_bang(IRubyObject recv, Block block) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chop(IRubyObject recv, Block block) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chomp!", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "split", optional = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     @JRubyMethod(name = "select", required = 1, optional = 3, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(recv.getRuntime(), args);
     }
 
     @JRubyMethod(name = "sleep", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sleep(IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw recv.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return recv.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         
         while(milliseconds > 0) {
             long loopStartTime = System.currentTimeMillis();
             try {
                 rubyThread.sleep(milliseconds);
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         }
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = Visibility.PRIVATE)
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         Iterator iter = recv.getRuntime().getGlobalVariables().getNames();
         while (iter.hasNext()) {
             String globalVariableName = (String) iter.next();
 
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = Visibility.PRIVATE)
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: runtime.getCurrentContext().getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     @JRubyMethod(name = "binding", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBoolean block_given_p(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.op_format(newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.fastGetClass("RuntimeError").newInstance(args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.fastGetClass("Exception"))) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject arg1, Block block) {
         RubyString file = arg1.convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
         IRubyObject scope = null;
         String file = "(eval)";
         int line = 1;
 
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
 
                 org.jruby.lexer.yacc.ISourcePosition pos = ((scope instanceof RubyBinding) ? (RubyBinding)scope : (RubyBinding)((RubyProc)scope).binding()).getBlock().getFrame().getPosition();
                 file = pos.getFile();
                 line = pos.getEndLine();
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
 
             if(args.length > 3) {
                 line = RubyNumeric.fix2int(args[3]) - 1;
             }
         }
 
         recv.getRuntime().checkSafeString(src);
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = RubyBinding.newBindingForEval(recv.getRuntime());
         }
         
         return recv.evalWithBinding(context, src, scope, file, line);
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject callcc(IRubyObject recv, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn("Kernel#callcc: Continuations are not implemented in JRuby and will not work");
         IRubyObject cc = runtime.getContinuation().callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return ThreadContext.createBacktraceFromFrames(recv.getRuntime(), recv.getRuntime().getCurrentContext().createBacktrace(level, false));
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return block.yield(context, tag);
         } catch (JumpException.ThrowJump tj) {
             if (tj.getTarget().equals(tag.asSymbol())) return (IRubyObject) tj.getValue();
             
             throw tj;
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         ThreadContext context = runtime.getCurrentContext();
         String[] catches = context.getActiveCatches();
 
         String message = "uncaught throw `" + tag + "'";
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 throw new JumpException.ThrowJump(tag, args.length > 1 ? args[1] : runtime.getNil());
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.getRuntime().getLoadService().require("jsignal");
         return recv.callMethod(recv.getRuntime().getCurrentContext(), "__jtrap", args, CallType.FUNCTIONAL, block);
     }
     
     @JRubyMethod(name = "warn", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         Ruby runtime = recv.getRuntime();
         if (!runtime.getVerbose().isNil()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             out.callMethod(runtime.getCurrentContext(), "puts", new IRubyObject[] { message });
         }
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "set_trace_func", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(name = "trace_var", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject trace_var(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = null;
         
         if (args.length > 1) {
             var = args[0].toString();
         }
         
         if (var.charAt(0) != '$') {
             // ignore if it's not a global var
             return recv.getRuntime().getNil();
         }
         
         if (args.length == 1) {
-            proc = RubyProc.newProc(recv.getRuntime(), block, false);
+            proc = RubyProc.newProc(recv.getRuntime(), block, Block.Type.PROC);
         }
         if (args.length == 2) {
             proc = (RubyProc)args[1].convertToType(recv.getRuntime().getProc(), 0, "to_proc", true);
         }
         
         recv.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "untrace_var", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject untrace_var(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError(0, 1);
         String var = null;
         
         if (args.length >= 1) {
             var = args[0].toString();
         }
         
         if (var.charAt(0) != '$') {
             // ignore if it's not a global var
             return recv.getRuntime().getNil();
         }
         
         if (args.length > 1) {
             ArrayList success = new ArrayList();
             for (int i = 1; i < args.length; i++) {
                 if (recv.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(recv.getRuntime(), success);
         } else {
             recv.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyProc proc(IRubyObject recv, Block block) {
-        return recv.getRuntime().newProc(true, block);
+        return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
 
     @JRubyMethod(name = "loop", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
                 
                 context.pollThreadEvents();
             } catch (JumpException.BreakJump bj) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (bj.getTarget() != null && bj.getTarget() != block) {
                     bj.setBreakInKernelLoop(true);
                 }
                  
                 throw bj;
             }
         }
     }
     
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             // MRI message if no args given
             throw runtime.newArgumentError("wrong number of arguments");
         }
         IRubyObject cmdArg = args[0];
         int cmd;
         if (cmdArg instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum)cmdArg).getLongValue();
         } else if (cmdArg instanceof RubyString &&
                 ((RubyString)cmdArg).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString)cmdArg).getByteList().charAt(0);
         } else {
             cmd = (int)cmdArg.convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         
         // implemented commands
         case 'C': // ?C  | Time    | Last change time for file1
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
         case 'e': // ?e  | boolean | True if file1 exists
         case 'f':
         case 'M':
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size,
                   //     |         | otherwise return nil
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
         case '=': // ?=  | boolean | True if the modification times of file1
                   //     |         | and file2 are equal
         case '<': // ?<  | boolean | True if the modification time of file1
                   //     |         | is prior to that of file2
         case '>': // ?>  | boolean | True if the modification time of file1
                   //     |         | is after that of file2
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             break;
 
         // unimplemented commands
 
         // FIXME: obviously, these are mostly unimplemented.  Raising an
         // ArgumentError 'unimplemented command' for them.
         
         case 'A': // ?A  | Time    | Last access time for file1
         case 'b': // ?b  | boolean | True if file1 is a block device
         case 'c': // ?c  | boolean | True if file1 is a character device
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
         case 'G': // ?G  | boolean | True if file1 exists and has a group
                   //     |         | ownership equal to the caller's group
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
         case 'o': // ?o  | boolean | True if file1 exists and is owned by
                   //     |         | the caller's effective uid  
         case 'O': // ?O  | boolean | True if file1 exists and is owned by 
                   //     |         | the caller's real uid
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
         case 'r': // ?r  | boolean | True if file1 is readable by the effective
                   //     |         | uid/gid of the caller
         case 'R': // ?R  | boolean | True if file is readable by the real
                   //     |         | uid/gid of the caller
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
         case 'w': // ?w  | boolean | True if file1 exists and is writable by
         case 'W': // ?W  | boolean | True if file1 exists and is writable by
                   //     |         | the real uid/gid
         case 'x': // ?x  | boolean | True if file1 exists and is executable by
                   //     |         | the effective uid/gid
         case 'X': // ?X  | boolean | True if file1 exists and is executable by
                   //     |         | the real uid/gid
             throw runtime.newArgumentError("unimplemented command ?"+(char)cmd);
         default:
             // matches MRI message
             throw runtime.newArgumentError("unknown command ?"+(char)cmd);
             
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-':
         case '=':
         case '<':
         case '>':
             if (args.length != 3) {
                 throw runtime.newArgumentError(args.length,3);
             }
             break;
         default:
             if (args.length != 2) {
                 throw runtime.newArgumentError(args.length,2);
             }
             break;
         }
         
         String cwd = runtime.getCurrentDirectory();
         File file1 = org.jruby.util.JRubyFile.create(cwd, args[1].convertToString().toString());
         File file2 = null;
         Calendar calendar = null;
                 
         switch (cmd) {
         case 'C': // ?C  | Time    | Last change time for file1
             return runtime.newFixnum(file1.lastModified());
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return runtime.newBoolean(file1.isDirectory());
         case 'e': // ?e  | boolean | True if file1 exists
             return runtime.newBoolean(file1.exists());
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return runtime.newBoolean(file1.isFile());
         
         case 'M': // ?M  | Time    | Last modification time for file1
             calendar = Calendar.getInstance();
             calendar.setTimeInMillis(file1.lastModified());
             return RubyTime.newTime(runtime, calendar);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size,
                   //     |         | otherwise return nil
             long length = file1.length();
 
             return length == 0 ? runtime.getNil() : runtime.newFixnum(length);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return runtime.newBoolean(file1.exists() && file1.length() == 0);
         case '=': // ?=  | boolean | True if the modification times of file1
                   //     |         | and file2 are equal
             file2 = org.jruby.util.JRubyFile.create(cwd, args[2].convertToString().toString());
             
             return runtime.newBoolean(file1.lastModified() == file2.lastModified());
         case '<': // ?<  | boolean | True if the modification time of file1
                   //     |         | is prior to that of file2
             file2 = org.jruby.util.JRubyFile.create(cwd, args[2].convertToString().toString());
             
             return runtime.newBoolean(file1.lastModified() < file2.lastModified());
         case '>': // ?>  | boolean | True if the modification time of file1
                   //     |         | is after that of file2
             file2 = org.jruby.util.JRubyFile.create(cwd, args[2].convertToString().toString());
             
             return runtime.newBoolean(file1.lastModified() > file2.lastModified());
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             file2 = org.jruby.util.JRubyFile.create(cwd, args[2].convertToString().toString());
 
             return runtime.newBoolean(file1.equals(file2));
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     @JRubyMethod(name = "srand", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = args[0].convertToInteger(MethodIndex.TO_INT, "to_int");
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     @JRubyMethod(name = "rand", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             if (args[0] instanceof RubyBignum) {
                 byte[] bigCeilBytes = ((RubyBignum) args[0]).getValue().toByteArray();
                 BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
                 
                 byte[] randBytes = new byte[bigCeilBytes.length];
                 runtime.getRandom().nextBytes(randBytes);
                 
                 BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
                 
                 return new RubyBignum(runtime, result); 
             }
              
             RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(recv, args[0]); 
             ceil = Math.abs(integerCeil.getLongValue());
         } else {
             throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble()); 
         }
         if (ceil > Integer.MAX_VALUE) {
             return runtime.newFixnum(Math.abs(runtime.getRandom().nextLong()) % ceil);
         }
             
         return runtime.newFixnum(runtime.getRandom().nextInt((int) ceil));
     }
 
     @JRubyMethod(name = {"system","exec"}, required = 1, rest = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode;
         try {
             resultCode = new ShellLauncher(runtime).runAndWait(args);
         } catch (Exception e) {
             resultCode = 127;
         }
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 532fb5b43e..74f958b195 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -87,2039 +87,2058 @@ public class RubyModule extends RubyObject {
     
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         moduleClass.index = ClassIndex.MODULE;
         moduleClass.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyModule;
             }
         };
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyModule.class);            
             
         moduleClass.defineFastMethod("===", callbackFactory.getFastMethod("op_eqq", IRubyObject.class));
         moduleClass.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", IRubyObject.class));
         moduleClass.defineFastMethod("freeze", callbackFactory.getFastMethod("freeze"));        
         moduleClass.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
         moduleClass.defineFastMethod("<", callbackFactory.getFastMethod("op_lt", IRubyObject.class));
         moduleClass.defineFastMethod("<=", callbackFactory.getFastMethod("op_le", IRubyObject.class));
         moduleClass.defineFastMethod(">", callbackFactory.getFastMethod("op_gt", IRubyObject.class));
         moduleClass.defineFastMethod(">=", callbackFactory.getFastMethod("op_ge", IRubyObject.class));
         moduleClass.defineFastMethod("ancestors", callbackFactory.getFastMethod("ancestors"));
         moduleClass.defineFastMethod("class_variables", callbackFactory.getFastMethod("class_variables"));
         moduleClass.defineFastMethod("const_defined?", callbackFactory.getFastMethod("const_defined", IRubyObject.class));
         moduleClass.defineFastMethod("const_get", callbackFactory.getFastMethod("const_get", IRubyObject.class));
         moduleClass.defineMethod("const_missing", callbackFactory.getMethod("const_missing", IRubyObject.class));
         moduleClass.defineFastMethod("const_set", callbackFactory.getFastMethod("const_set", IRubyObject.class, IRubyObject.class));
         moduleClass.defineFastMethod("constants", callbackFactory.getFastMethod("constants"));
         moduleClass.defineMethod("extended", callbackFactory.getMethod("extended", IRubyObject.class));
         moduleClass.defineFastMethod("include?", callbackFactory.getFastMethod("include_p", IRubyObject.class));
         moduleClass.defineFastMethod("included", callbackFactory.getFastMethod("included", IRubyObject.class));
         moduleClass.defineFastMethod("included_modules", callbackFactory.getFastMethod("included_modules"));
         moduleClass.defineMethod("initialize", callbackFactory.getMethod("initialize"));
         moduleClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         moduleClass.defineFastMethod("instance_method", callbackFactory.getFastMethod("instance_method", IRubyObject.class));
         moduleClass.defineFastMethod("instance_methods",callbackFactory.getFastOptMethod("instance_methods"));
         moduleClass.defineFastMethod("method_defined?", callbackFactory.getFastMethod("method_defined", IRubyObject.class));
         moduleClass.defineFastMethod("public_method_defined?", callbackFactory.getFastMethod("public_method_defined", IRubyObject.class));
         moduleClass.defineFastMethod("protected_method_defined?", callbackFactory.getFastMethod("protected_method_defined", IRubyObject.class));
         moduleClass.defineFastMethod("private_method_defined?", callbackFactory.getFastMethod("private_method_defined", IRubyObject.class));
         moduleClass.defineMethod("module_eval", callbackFactory.getOptMethod("module_eval"));
         moduleClass.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         moduleClass.defineFastMethod("private_class_method", callbackFactory.getFastOptMethod("private_class_method"));
         moduleClass.defineFastMethod("private_instance_methods", callbackFactory.getFastOptMethod("private_instance_methods"));
         moduleClass.defineFastMethod("protected_instance_methods", callbackFactory.getFastOptMethod("protected_instance_methods"));
         moduleClass.defineFastMethod("public_class_method", callbackFactory.getFastOptMethod("public_class_method"));
         moduleClass.defineFastMethod("public_instance_methods", callbackFactory.getFastOptMethod("public_instance_methods"));
         moduleClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         moduleClass.defineFastMethod("class_variable_defined?", callbackFactory.getFastMethod("class_variable_defined_p", IRubyObject.class));
         
         moduleClass.defineAlias("class_eval", "module_eval");
         
         moduleClass.defineFastPrivateMethod("alias_method", callbackFactory.getFastMethod("alias_method", IRubyObject.class, IRubyObject.class));
         moduleClass.defineFastPrivateMethod("append_features", callbackFactory.getFastMethod("append_features", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("attr", callbackFactory.getFastOptMethod("attr"));
         moduleClass.defineFastPrivateMethod("attr_reader", callbackFactory.getFastOptMethod("attr_reader"));
         moduleClass.defineFastPrivateMethod("attr_writer", callbackFactory.getFastOptMethod("attr_writer"));
         moduleClass.defineFastPrivateMethod("attr_accessor", callbackFactory.getFastOptMethod("attr_accessor"));
         moduleClass.defineFastPrivateMethod("class_variable_get", callbackFactory.getFastMethod("class_variable_get", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("class_variable_set", callbackFactory.getFastMethod("class_variable_set", IRubyObject.class, IRubyObject.class));
         moduleClass.definePrivateMethod("define_method", callbackFactory.getOptMethod("define_method"));
         moduleClass.defineFastPrivateMethod("extend_object", callbackFactory.getFastMethod("extend_object", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("include", callbackFactory.getFastOptMethod("include"));
         moduleClass.definePrivateMethod("method_added", callbackFactory.getMethod("method_added", IRubyObject.class));
         moduleClass.definePrivateMethod("method_removed", callbackFactory.getMethod("method_removed", IRubyObject.class));
         moduleClass.definePrivateMethod("method_undefined", callbackFactory.getMethod("method_undefined", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("module_function", callbackFactory.getFastOptMethod("module_function"));
         moduleClass.defineFastPrivateMethod("public", callbackFactory.getFastOptMethod("rbPublic"));
         moduleClass.defineFastPrivateMethod("protected", callbackFactory.getFastOptMethod("rbProtected"));
         moduleClass.defineFastPrivateMethod("private", callbackFactory.getFastOptMethod("rbPrivate"));
         moduleClass.defineFastPrivateMethod("remove_class_variable", callbackFactory.getFastMethod("remove_class_variable", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("remove_const", callbackFactory.getFastMethod("remove_const", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("remove_method", callbackFactory.getFastOptMethod("remove_method"));
         moduleClass.defineFastPrivateMethod("undef_method", callbackFactory.getFastOptMethod("undef_method"));
         
         RubyClass moduleMetaClass = moduleClass.getMetaClass();
         moduleMetaClass.defineMethod("nesting", callbackFactory.getSingletonMethod("nesting"));
         
         moduleClass.dispatcher = callbackFactory.createDispatcher(moduleClass);
 
         callbackFactory = runtime.callbackFactory(RubyKernel.class);
         moduleClass.defineFastMethod("autoload", callbackFactory.getFastSingletonMethod("autoload", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         moduleClass.defineFastMethod("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", RubyKernel.IRUBY_OBJECT));
 
         return moduleClass;
     }    
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyModule(runtime, klass);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }    
     
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
     
     public Dispatcher dispatcher = Dispatcher.DEFAULT_DISPATCHER;
 
     public static class KindOf {
         public static final KindOf DEFAULT_KIND_OF = new KindOf();
         public boolean isKindOf(IRubyObject obj, RubyModule type) {
             return obj.getMetaClass().hasModuleInHierarchy(type);
         }
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     public RubyModule parent;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
 
     // CONSTANT TABLE
     
     // Lock used for variableTable/constantTable writes. The RubyObject variableTable
     // write methods are overridden here to use this lock rather than Java
     // synchronization for faster concurrent writes for modules/classes.
     protected final ReentrantLock variableWriteLock = new ReentrantLock();
     
     protected transient volatile ConstantTableEntry[] constantTable =
         new ConstantTableEntry[CONSTANT_TABLE_DEFAULT_CAPACITY];
 
     protected transient int constantTableSize;
 
     protected transient int constantTableThreshold = 
         (int)(CONSTANT_TABLE_DEFAULT_CAPACITY * CONSTANT_TABLE_LOAD_FACTOR);
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
     
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax. 
     private transient List classProviders;
 
     /** separate path for MetaClass construction
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass, boolean objectSpace) {
         super(runtime, metaClass, objectSpace);
         id = ++runtime.moduleLastId;
         // if (parent == null) parent = runtime.getObject();
         setFlag(USER7_F, !isClass());
     }
     
     /** used by MODULE_ALLOCATOR and RubyClass constructors
      * 
      */
     protected RubyModule(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
     
     /** standard path for Module construction
      * 
      */
     protected RubyModule(Ruby runtime) {
         this(runtime, runtime.getModule());
     }
 
     public boolean needsImplementer() {
         return getFlag(USER7_F);
     }
     
     /** rb_module_new
      * 
      */
     public static RubyModule newModule(Ruby runtime) {
         return new RubyModule(runtime);
     }
     
     /** rb_module_new/rb_define_module_id/rb_name_class/rb_set_class_path
      * 
      */
     public static RubyModule newModule(Ruby runtime, String name, RubyModule parent, boolean setParent) {
         RubyModule module = newModule(runtime);
         module.setBaseName(name);
         if (setParent) module.setParent(parent);
         parent.setConstant(name, module);
         return module;
     }
     
     // synchronized method per JRUBY-1173 (unsafe Double-Checked Locking)
     // FIXME: synchronization is still wrong in CP code
     public synchronized void addClassProvider(ClassProvider provider) {
         if (classProviders == null) {
             List cp = Collections.synchronizedList(new ArrayList());
             cp.add(provider);
             classProviders = cp;
         } else {
             synchronized(classProviders) {
                 if (!classProviders.contains(provider)) {
                     classProviders.add(provider);
                 }
             }
         }
     }
 
     public void removeClassProvider(ClassProvider provider) {
         if (classProviders != null) {
             classProviders.remove(provider);
         }
     }
 
     private RubyClass searchProvidersForClass(String name, RubyClass superClazz) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyClass clazz;
                 for (Iterator iter = classProviders.iterator(); iter.hasNext(); ) {
                     if ((clazz = ((ClassProvider)iter.next())
                             .defineClassUnder(this, name, superClazz)) != null) {
                         return clazz;
                     }
                 }
             }
         }
         return null;
     }
 
     private RubyModule searchProvidersForModule(String name) {
         if (classProviders != null) {
             synchronized(classProviders) {
                 RubyModule module;
                 for (Iterator iter = classProviders.iterator(); iter.hasNext(); ) {
                     if ((module = ((ClassProvider)iter.next()).defineModuleUnder(this, name)) != null) {
                         return module;
                     }
                 }
             }
         }
         return null;
     }
 
     public Dispatcher getDispatcher() {
         return dispatcher;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         return parent;
     }
 
     public void setParent(RubyModule parent) {
         this.parent = parent;
     }
 
     public Map getMethods() {
         return methods;
     }
     
     public void putMethod(Object name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         dispatcher.clearIndex(MethodIndex.getIndex((String)name));
         getMethods().put(name, method);
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result.insert(0, "::").insert(0, pName);
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         doIncludeModule(module);
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedMethods(Class clazz) {
         Method[] declaredMethods = clazz.getDeclaredMethods();
         MethodFactory methodFactory = MethodFactory.createFactory(getRuntime().getJRubyClassLoader());
         for (Method method: declaredMethods) {
             defineAnnotatedMethod(method, methodFactory);
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) {
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
         if(getRuntime().getInstanceConfig().isRite() == jrubyMethod.rite()) {
             // select current module or module's metaclass for singleton methods
             RubyModule module = this;
 
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, method);
         
             // if it's a singleton (metaclass) method or a module method, bind to metaclass
             if (jrubyMethod.meta()) {
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = method.getName();
                     metaClass.addMethod(baseName, dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         metaClass.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         metaClass.defineAlias(alias, baseName);
                     }
                 }
             } else {
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = method.getName();
                     module.addMethod(method.getName(), dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         module.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         module.defineAlias(alias, baseName);
                     }
                 }
             
                 if (jrubyMethod.module()) {
                     // module/singleton methods are all defined public
                     DynamicMethod moduleMethod = dynamicMethod.dup();
                     moduleMethod.setVisibility(Visibility.PUBLIC);
                 
                     RubyModule singletonClass = getSingletonClass();
 
                     if (jrubyMethod.name().length == 0) {
                         baseName = method.getName();
                         singletonClass.addMethod(method.getName(), moduleMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             singletonClass.addMethod(name, moduleMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             singletonClass.defineAlias(alias, baseName);
                         }
                     }
                 }
             }
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn("undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         }else{
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
     }
     }
     
     public IRubyObject include_p(IRubyObject arg) {
         if (!arg.isModule()) {
             throw getRuntime().newTypeError(arg, getRuntime().getModule());
         }
         
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if ((p instanceof IncludedModuleWrapper) && ((IncludedModuleWrapper) p).getNonIncludedClass() == arg) {
                 return getRuntime().newBoolean(true);
             }
         }
         
         return getRuntime().newBoolean(false);
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
             getRuntime().getMethodCache().removeMethod(name);
             putMethod(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
         getMethods().remove(name);
     }
 
     public void removeMethod(String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
             
             getRuntime().getMethodCache().removeMethod(name);
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     private DynamicMethod searchMethodWithoutCache(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 
                 if (method != null) {
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         MethodCache cache = getRuntime().getMethodCache();
         MethodCache.CacheEntry entry = cache.getMethod(this, name);
         if (entry.klass == this && name.equals(entry.methodName)) {
             return entry.method;
         }
         
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 
                 if (method != null) {
                     cache.putMethod(this, name, method);
                     /*
                     // TO BE REMOVED
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
                     */
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         DynamicMethod oldMethod = searchMethodWithoutCache(name);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getMethodCache().removeMethod(name);
         getRuntime().getCacheMap().remove(name, method);
         getRuntime().getCacheMap().remove(name, oldMethod);
         getRuntime().getCacheMap().remove(name, oldMethod.getRealMethod());
         
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAt(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAt(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     // FIXME: create AttrReaderMethod, AttrWriterMethod, for faster attr access
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope == Visibility.PRIVATE) {
             //FIXME warning
         } else if (attributeScope == Visibility.MODULE_FUNCTION) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + name).intern();
         final Ruby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             addMethod(name, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) Arity.raiseArgumentError(getRuntime(), args.length, 0, 0);
 
                     IRubyObject variable = self.fastGetInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = (name + "=").intern();
             addMethod(name, new JavaMethod(this, Visibility.PUBLIC) {
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     if (args.length != 1) Arity.raiseArgumentError(getRuntime(), args.length, 1, 1);
 
                     return self.fastSetInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asSymbol(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == Visibility.PRIVATE);
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(method.getImplementationClass(), name, this, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(method.getImplementationClass(), name, this, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     // What is argument 1 for in this method? A Method or Proc object /OB
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility == Visibility.MODULE_FUNCTION) visibility = Visibility.PRIVATE;
-
-        if (args.length == 1 || args[1].isKindOf(getRuntime().getProc())) {
+        if (args.length == 1) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
-            RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
+            RubyProc proc = getRuntime().newProc(Block.Type.LAMBDA, block);
             body = proc;
 
-            proc.getBlock().isLambda = true;
+            // a normal block passed to define_method changes to do arity checking; make it a lambda
+            proc.getBlock().type = Block.Type.LAMBDA;
+            
             proc.getBlock().getFrame().setKlazz(this);
             proc.getBlock().getFrame().setName(name);
             
             // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
             proc.getBlock().getDynamicScope().getStaticScope().setArgumentScope(true);
             // just using required is broken...but no more broken than before zsuper refactoring
             proc.getBlock().getDynamicScope().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
 
             newMethod = new ProcMethod(this, proc, visibility);
-        } else if (args[1].isKindOf(getRuntime().getMethod())) {
-            RubyMethod method = (RubyMethod)args[1];
-            body = method;
+        } else if (args.length == 2) {
+            if (args[1].isKindOf(getRuntime().getProc())) {
+                // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
+                RubyProc proc = (RubyProc)args[1];
+                body = proc;
+                
+                proc.getBlock().getFrame().setKlazz(this);
+                proc.getBlock().getFrame().setName(name);
+
+                // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
+                proc.getBlock().getDynamicScope().getStaticScope().setArgumentScope(true);
+                // just using required is broken...but no more broken than before zsuper refactoring
+                proc.getBlock().getDynamicScope().getStaticScope().setRequiredArgs(proc.getBlock().arity().required());
 
-            newMethod = new MethodMethod(this, method.unbind(null), visibility);
+                newMethod = new ProcMethod(this, proc, visibility);
+            } else if (args[1].isKindOf(getRuntime().getMethod())) {
+                RubyMethod method = (RubyMethod)args[1];
+                body = method;
+
+                newMethod = new MethodMethod(this, method.unbind(null), visibility);
+            } else {
+                throw getRuntime().newTypeError("wrong argument type " + args[1].getType().getName() + " (expected Proc/Method)");
+            }
         } else {
-            throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
+            throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility() == Visibility.MODULE_FUNCTION) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
-        callMethod(context, "method_added", symbol);
+            callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         context.preExecuteUnder(this, block);
 
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method instanceof UndefinedMethod) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
 
         if (originalModule.hasVariables()){
             syncVariables(originalModule.getVariableList());
         }
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     /** rb_mod_included_modules
      *
      */
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuffer buffer = new StringBuffer("#<Class:");
             if(attached instanceof RubyClass || attached instanceof RubyModule){
                 buffer.append(attached.inspect());
             }else{
                 buffer.append(attached.anyToString());
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(obj.isKindOf(this));
     }
 
     public IRubyObject op_equal(IRubyObject other) {
         return super.op_equal(other);
     }
 
     /** rb_mod_freeze
      *
      */   
     public IRubyObject freeze() {
         to_s();
         return super.freeze();
     }
 
     /** rb_mod_le
     *
     */
    public IRubyObject op_le(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
 
        return getRuntime().getNil();
    }
 
    /** rb_mod_lt
     *
     */
    public IRubyObject op_lt(IRubyObject obj) {
     return obj == this ? getRuntime().getFalse() : op_le(obj);
    }
 
    /** rb_mod_ge
     *
     */
    public IRubyObject op_ge(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        return ((RubyModule) obj).op_le(this);
    }
 
    /** rb_mod_gt
     *
     */
    public IRubyObject op_gt(IRubyObject obj) {
        return this == obj ? getRuntime().getFalse() : op_ge(obj);
    }
 
    /** rb_mod_cmp
     *
     */
    public IRubyObject op_cmp(IRubyObject obj) {
        if (this == obj) {
            return getRuntime().newFixnum(0);
        }
 
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError(
                "<=> requires Class or Module (" + getMetaClass().getName() + " given)");
        }
 
        RubyModule module = (RubyModule)obj;
 
        if (module.isKindOfModule(this)) {
            return getRuntime().newFixnum(1);
        } else if (this.isKindOfModule(module)) {
            return getRuntime().newFixnum(-1);
        }
 
        return getRuntime().getNil();
    }
 
    public boolean isKindOfModule(RubyModule type) {
        for (RubyModule p = this; p != null; p = p.getSuperClass()) {
            if (p.isSame(type)) {
                return true;
            }
        }
 
        return false;
    }
 
    protected boolean isSame(RubyModule module) {
        return this == module;
    }
 
     /** rb_mod_initialize
      *
      */
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.setVisibility(Visibility.PUBLIC);
             block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(args[0].asSymbol(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_reader
      *
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     public IRubyObject attr_writer(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_accessor
      *
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided visibility.
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap<String, Boolean> undefinedMethods = new HashMap<String, Boolean>();
         Set<String> added = new HashSet<String>();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (method.isUndefined()) {
                     undefinedMethods.put(methodName, Boolean.TRUE);
                     continue;
                 }
                 if (method.getImplementationClass() == realType &&
                     (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                     undefinedMethods.get(methodName) == null) {
 
                     if (!added.contains(methodName)) {
                         ary.append(getRuntime().newString(methodName));
                         added.add(methodName);
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE, true);
     }
 
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC, false);
     }
 
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asSymbol(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PROTECTED, false);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE, false);
     }
 
     /** rb_mod_append_features
      *
      */
     public RubyModule append_features(IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             // MRI error message says Class, even though Module is ok 
             throw getRuntime().newTypeError(module,getRuntime().getClassClass());
         }
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj = modules[i];
             if (!obj.isModule()) throw getRuntime().newTypeError(obj,getRuntime().getModule());
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     public IRubyObject extended(IRubyObject other, Block block) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             getRuntime().getCurrentContext().setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     public RubyModule rbPublic(IRubyObject[] args) {
         setVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     public RubyModule rbProtected(IRubyObject[] args) {
         setVisibility(args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     public RubyModule rbPrivate(IRubyObject[] args) {
         setVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     public RubyModule module_function(IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
 
     public IRubyObject method_added(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_removed(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_undefined(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
     
     public RubyBoolean method_defined(IRubyObject symbol) {
         return isMethodBound(symbol.asSymbol(), true) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject public_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PUBLIC);
     }
 
     public IRubyObject protected_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PROTECTED);
     }
 	
     public IRubyObject private_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == Visibility.PRIVATE);
     }
 
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     public RubyModule alias_method(IRubyObject newId, IRubyObject oldId) {
         defineAlias(newId.asSymbol(), oldId.asSymbol());
         callMethod(getRuntime().getCurrentContext(), "method_added", newId);
         return this;
     }
 
     public RubyModule undef_method(IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(args[i].asSymbol());
         }
         return this;
     }
 
     public IRubyObject module_eval(IRubyObject[] args, Block block) {
         return specificEval(this, args, block);
     }
 
     public RubyModule remove_method(IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(args[i].asSymbol());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.writeString(MarshalStream.getPathFromClass(module));
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyModule result = UnmarshalStream.getModuleFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     /* Module class methods */
     
     /** 
      * Return an array of nested modules or classes.
      */
     public static RubyArray nesting(IRubyObject recv, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyModule object = runtime.getObject();
         StaticScope scope = runtime.getCurrentContext().getCurrentScope().getStaticScope();
         RubyArray result = runtime.newArray();
         
         for (StaticScope current = scope; current.getModule() != object; current = current.getPreviousCRefScope()) {
             result.append(current.getModule());
         }
         
         return result;
     }
 
     private void doIncludeModule(RubyModule includedModule) {
 
         boolean changed = false;
         boolean skip = false;
 
         RubyModule currentModule = this;
         while (includedModule != null) {
 
             if (getNonIncludedClass() == includedModule.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
 
             // scan class hierarchy for module
             for (RubyModule superClass = this.getSuperClass(); superClass != null; superClass = superClass.getSuperClass()) {
                 if (superClass instanceof IncludedModuleWrapper) {
                     if (superClass.getNonIncludedClass() == includedModule.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             currentModule = superClass;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
 
             if (!skip) {
 
                 // blow away caches for any methods that are redefined by module
                 getRuntime().getCacheMap().moduleIncluded(currentModule, includedModule);
                 
                 // In the current logic, if we get here we know that module is not an
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just
                 // in case the logic should change later, let's do it anyway:
                 currentModule.setSuperClass(new IncludedModuleWrapper(getRuntime(), currentModule.getSuperClass(),
                         includedModule.getNonIncludedClass()));
                 currentModule = currentModule.getSuperClass();
                 changed = true;
             }
 
             includedModule = includedModule.getSuperClass();
             skip = false;
         }
         if (changed) {
             getRuntime().getMethodCache().clearCacheForModule(this);
         }
     }
 
 
     //
     ////////////////// CLASS VARIABLE RUBY METHODS ////////////////
     //
 
     public IRubyObject class_variable_defined_p(IRubyObject var) {
         String internedName = validateClassVariable(var.asSymbol());
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return getRuntime().getTrue();
             }
         } while ((module = module.getSuperClass()) != null);
 
         return getRuntime().getFalse();
     }
 
     /** rb_mod_cvar_get
      *
      */
     public IRubyObject class_variable_get(IRubyObject var) {
         return fastGetClassVar(validateClassVariable(var.asSymbol()));
     }
 
     /** rb_mod_cvar_set
      *
      */
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         return fastSetClassVar(validateClassVariable(var.asSymbol()), value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject remove_class_variable(IRubyObject name) {
         String internedName = validateClassVariable(name.asSymbol());
         IRubyObject value;
 
         if ((value = deleteClassVariable(internedName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(internedName)) {
             throw cannotRemoveError(internedName);
         }
 
         throw getRuntime().newNameError("class variable " + internedName + " not defined for " + getName(), internedName);
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         Set<String> names = new HashSet<String>();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (String name : p.getClassVariableNameList()) {
                 names.add(name);
             }
         }
 
         Ruby runtime = getRuntime();
         RubyArray ary = runtime.newArray();
 
         for (String name : names) {
             ary.add(runtime.newString(name));
         }
 
         return ary;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return getRuntime().newBoolean(fastIsConstantDefined(validateConstant(symbol.asSymbol())));
     }
 
     /** rb_mod_const_get
      *
      */
     public IRubyObject const_get(IRubyObject symbol) {
         return fastGetConstant(validateConstant(symbol.asSymbol()));
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         return fastSetConstant(validateConstant(symbol.asSymbol()), value);
     }
 
     public IRubyObject remove_const(IRubyObject name) {
         String id = validateConstant(name.asSymbol());
         IRubyObject value;
         if ((value = deleteConstant(id)) != null) {
             if (value != getRuntime().getUndef()) {
                 return value;
             }
             getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + id);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return getRuntime().getNil();
         }
 
         if (hasConstantInHierarchy(id)) {
             throw cannotRemoveError(id);
         }
 
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
     }
 
     private boolean hasConstantInHierarchy(final String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.hasConstant(name)) {
                 return true;
             }
         }
         return false;
     }
     
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     public IRubyObject const_missing(IRubyObject name, Block block) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
     /** rb_mod_constants
      *
      */
     public RubyArray constants() {
         Ruby runtime = getRuntime();
         RubyArray array = runtime.newArray();
         RubyModule objectClass = runtime.getObject();
 
         if (getRuntime().getModule() == this) {
 
             for (String name : objectClass.getStoredConstantNameList()) {
                 array.add(runtime.newString(name));
             }
 
         } else if (objectClass == this) {
 
             for (String name : getStoredConstantNameList()) {
                 array.add(runtime.newString(name));
             }
 
         } else {
             Set<String> names = new HashSet<String>();
             for (RubyModule p = this; p != null; p = p.getSuperClass()) {
                 if (objectClass != p) {
                     for (String name : p.getStoredConstantNameList()) {
                         names.add(name);
                     }
                 }
             }
             for (String name : names) {
                 array.add(runtime.newString(name));
             }
         }
 
         return array;
     }
 
 
     //
     ////////////////// CLASS VARIABLE API METHODS ////////////////
     //
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) {
                 return module.storeClassVariable(name, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return storeClassVariable(name, value);
     }
 
     public IRubyObject fastSetClassVar(final String internedName, final IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return module.fastStoreClassVariable(internedName, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return fastStoreClassVariable(internedName, value);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         assert IdUtil.isClassVariable(name);
         IRubyObject value;
         RubyModule module = this;
         
         do {
             if ((value = module.variableTableFetch(name)) != null) return value;
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     public IRubyObject fastGetClassVar(String internedName) {
         assert IdUtil.isClassVariable(internedName);
         IRubyObject value;
         RubyModule module = this;
         
         do {
             if ((value = module.variableTableFastFetch(internedName)) != null) return value; 
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + internedName + " in " + getName(), internedName);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     public boolean fastIsClassVarDefined(String internedName) {
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     
     /** rb_mod_remove_cvar
      *
      * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         String internedName = validateClassVariable(name.asSymbol());
         IRubyObject value;
 
         if ((value = deleteClassVariable(internedName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(internedName)) {
             throw cannotRemoveError(internedName);
         }
 
         throw getRuntime().newNameError("class variable " + internedName + " not defined for " + getName(), internedName);
     }
 
 
     //
     ////////////////// CONSTANT API METHODS ////////////////
     //
 
     public IRubyObject getConstantAt(String name) {
         IRubyObject value;
         if ((value = fetchConstant(name)) != getRuntime().getUndef()) {
             return value;
         }
         deleteConstant(name);
         return getRuntime().getLoadService().autoload(getName() + "::" + name);
     }
 
     public IRubyObject fastGetConstantAt(String internedName) {
         IRubyObject value;
         if ((value = fastFetchConstant(internedName)) != getRuntime().getUndef()) {
             return value;
         }
         deleteConstant(internedName);
         return getRuntime().getLoadService().autoload(getName() + "::" + internedName);
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         assert IdUtil.isConstant(name);
         IRubyObject undef = getRuntime().getUndef();
         boolean retryForModule = false;
         IRubyObject value;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 if ((value = p.constantTableFetch(name)) != null) {
                     if (value != undef) {
                         return value;
                     }
                     p.deleteConstant(name);
                     if (getRuntime().getLoadService().autoload(
                             p.getName() + "::" + name) == null) {
                         break;
                     }
                     continue;
                 }
                 p = p.getSuperClass();
             };
 
             if (!retryForModule && !isClass()) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
     
     public IRubyObject fastGetConstant(String internedName) {
         assert IdUtil.isConstant(internedName);
         IRubyObject undef = getRuntime().getUndef();
         boolean retryForModule = false;
         IRubyObject value;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 if ((value = p.constantTableFastFetch(internedName)) != null) {
                     if (value != undef) {
                         return value;
                     }
                     p.deleteConstant(internedName);
                     if (getRuntime().getLoadService().autoload(
                             p.getName() + "::" + internedName) == null) {
                         break;
                     }
                     continue;
                 }
                 p = p.getSuperClass();
             };
diff --git a/src/org/jruby/RubyObjectSpace.java b/src/org/jruby/RubyObjectSpace.java
index 69580b2344..c3e4763bf5 100644
--- a/src/org/jruby/RubyObjectSpace.java
+++ b/src/org/jruby/RubyObjectSpace.java
@@ -1,147 +1,147 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby;
 
 import java.util.Iterator;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyObjectSpace {
 
     /** Create the ObjectSpace module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createObjectSpaceModule(Ruby runtime) {
         RubyModule objectSpaceModule = runtime.defineModule("ObjectSpace");
         runtime.setObjectSpaceModule(objectSpaceModule);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObjectSpace.class);
         
         objectSpaceModule.defineAnnotatedMethods(RubyObjectSpace.class);
 
         return objectSpaceModule;
     }
 
     @JRubyMethod(name = "define_finalizer", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject define_finalizer(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyProc proc = null;
         if (Arity.checkArgumentCount(runtime, args,1,2) == 2) {
             if(args[1] instanceof RubyProc) {
                 proc = (RubyProc)args[1];
             } else {
                 proc = (RubyProc)args[1].convertToType(runtime.getProc(), 0, "to_proc", true);
             }
         } else {
-            proc = runtime.newProc(false, block);
+            proc = runtime.newProc(Block.Type.PROC, block);
         }
         IRubyObject obj = args[0];
         runtime.getObjectSpace().addFinalizer(obj, proc);
         return recv;
     }
 
     @JRubyMethod(name = "undefine_finalizer", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject undefine_finalizer(IRubyObject recv, IRubyObject arg1, Block block) {
         recv.getRuntime().getObjectSpace().removeFinalizers(RubyNumeric.fix2long(arg1.id()));
         return recv;
     }
 
     @JRubyMethod(name = "_id2ref", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject id2ref(IRubyObject recv, IRubyObject id) {
         Ruby runtime = id.getRuntime();
         if (!(id instanceof RubyFixnum)) {
             throw recv.getRuntime().newTypeError(id, recv.getRuntime().getFixnum());
         }
         RubyFixnum idFixnum = (RubyFixnum) id;
         long longId = idFixnum.getLongValue();
         if (longId == 0) {
             return runtime.getFalse();
         } else if (longId == 2) {
             return runtime.getTrue();
         } else if (longId == 4) {
             return runtime.getNil();
         } else if (longId % 2 != 0) {
             // odd
             return runtime.newFixnum((longId - 1) / 2);
         } else {
             IRubyObject object = runtime.getObjectSpace().id2ref(longId);
             if (object == null) {
                 return runtime.getNil();
             }
             return object;
         }
     }
     
     @JRubyMethod(name = "each_object", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject each_object(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyModule rubyClass;
         if (args.length == 0) {
             rubyClass = recv.getRuntime().getObject();
         } else {
             rubyClass = (RubyModule) args[0];
         }
         Ruby runtime = recv.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         Iterator iter;
         int count = 0;
         if (rubyClass != runtime.getClassClass()) {
             if (!runtime.isObjectSpaceEnabled()) {
                 runtime.getWarnings().warn("ObjectSpace is disabled; each_object will only work with Class");
             }
             iter = recv.getRuntime().getObjectSpace().iterator(rubyClass);
             
             IRubyObject obj = null;
             while ((obj = (IRubyObject)iter.next()) != null) {
                 count++;
                 block.yield(context, obj);
             }
         } else {
             iter = runtime.getObject().subclasses(true).iterator();
             
             while (iter.hasNext()) {
                 count++;
                 block.yield(context, (IRubyObject)iter.next());
             }
         }
         return recv.getRuntime().newFixnum(count);
     }
 
     @JRubyMethod(name = "garbage_collect", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject garbage_collect(IRubyObject recv) {
         return RubyGC.start(recv);
     }
 }
diff --git a/src/org/jruby/RubyProc.java b/src/org/jruby/RubyProc.java
index 18b8819d6e..febe62ec73 100644
--- a/src/org/jruby/RubyProc.java
+++ b/src/org/jruby/RubyProc.java
@@ -1,223 +1,219 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author  jpetersen
  */
 public class RubyProc extends RubyObject implements JumpTarget {
     private Block block = Block.NULL_BLOCK;
-    private boolean isLambda;
+    private Block.Type type;
 
-    public RubyProc(Ruby runtime, RubyClass rubyClass, boolean isLambda) {
+    public RubyProc(Ruby runtime, RubyClass rubyClass, Block.Type type) {
         super(runtime, rubyClass);
         
-        this.isLambda = isLambda;
+        this.type = type;
     }
     
     private static ObjectAllocator PROC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
-            RubyProc instance = RubyProc.newProc(runtime, false);
+            RubyProc instance = RubyProc.newProc(runtime, Block.Type.PROC);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public static RubyClass createProcClass(Ruby runtime) {
         RubyClass procClass = runtime.defineClass("Proc", runtime.getObject(), PROC_ALLOCATOR);
         runtime.setProc(procClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyProc.class);
         
         procClass.defineAnnotatedMethods(RubyProc.class);
         
         return procClass;
     }
 
     public Block getBlock() {
         return block;
     }
 
     // Proc class
 
-    public static RubyProc newProc(Ruby runtime, boolean isLambda) {
-        return new RubyProc(runtime, runtime.getProc(), isLambda);
+    public static RubyProc newProc(Ruby runtime, Block.Type type) {
+        return new RubyProc(runtime, runtime.getProc(), type);
     }
-    public static RubyProc newProc(Ruby runtime, Block block, boolean isLambda) {
-        RubyProc proc = new RubyProc(runtime, runtime.getProc(), isLambda);
+    public static RubyProc newProc(Ruby runtime, Block block, Block.Type type) {
+        RubyProc proc = new RubyProc(runtime, runtime.getProc(), type);
         proc.callInit(NULL_ARRAY, block);
         
         return proc;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block procBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
         if (procBlock == null) {
             throw getRuntime().newArgumentError("tried to create Proc object without a block");
         }
         
-        if (isLambda && procBlock == null) {
+        if (type == Block.Type.LAMBDA && procBlock == null) {
             // TODO: warn "tried to create Proc object without a block"
         }
         
         block = procBlock.cloneBlock();
-        block.isLambda = isLambda;
+        block.type = type;
         block.setProcObject(this);
 
         return this;
     }
     
     @JRubyMethod(name = "clone")
     public IRubyObject rbClone() {
-    	RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), isLambda);
+    	RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), type);
     	newProc.block = getBlock();
     	// TODO: CLONE_SETUP here
     	return newProc;
     }
 
     @JRubyMethod(name = "dup")
     public IRubyObject dup() {
-        RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), isLambda);
+        RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getProc(), type);
         newProc.block = getBlock();
         return newProc;
     }
     
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (!(other instanceof RubyProc)) return getRuntime().getFalse();
         
         if (this == other || this.block == ((RubyProc)other).block) {
             return getRuntime().newBoolean(true);
         }
         
         return getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         return RubyString.newString(getRuntime(), "#<Proc:0x" + Integer.toString(block.hashCode(), 16) + ">");
     }
     
     /**
      * Create a new instance of a Proc object.  We override this method (from RubyClass)
      * since we need to deal with special case of Proc.new with no arguments or block arg.  In 
      * this case, we need to check previous frame for a block to consume.
      */
     @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         IRubyObject obj = ((RubyClass) recv).allocate();
         
         // No passed in block, lets check next outer frame for one ('Proc.new')
         if (!block.isGiven()) {
             block = runtime.getCurrentContext().getPreviousFrame().getBlock();
         }
         
         obj.callMethod(runtime.getCurrentContext(), "initialize", args, block);
         return obj;
     }
 
     @JRubyMethod(name = "binding")
     public IRubyObject binding() {
         return getRuntime().newBinding(block);
     }
 
     public IRubyObject call(IRubyObject[] args) {
         return call(args, null, Block.NULL_BLOCK);
     }
 
     // ENEBO: For method def others are Java to java versions
     @JRubyMethod(name = {"call", "[]"}, rest = true, frame = true)
     public IRubyObject call(IRubyObject[] args, Block unusedBlock) {
         return call(args, null, Block.NULL_BLOCK);
     }
     
     public IRubyObject call(IRubyObject[] args, IRubyObject self, Block unusedBlock) {
         assert args != null;
         
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         
         try {
-            if (block.isLambda) {
-                block.arity().checkArity(getRuntime(), args);
-            }
-            
             Block newBlock = block.cloneBlock();
             if (self != null) newBlock.setSelf(self);
             
-            // if lambda, set new jump target in (duped) frame for returns
-            if (newBlock.isLambda) newBlock.getFrame().setJumpTarget(this);
+            // lambdas want returns
+            if (newBlock.type == Block.Type.LAMBDA) newBlock.getFrame().setJumpTarget(this);
             
             return newBlock.call(context, args);
         } catch (JumpException.BreakJump bj) {
-                if (block.isLambda) return (IRubyObject) bj.getValue();
+                if (block.type == Block.Type.LAMBDA) return (IRubyObject) bj.getValue();
                 
                 throw runtime.newLocalJumpError("break", (IRubyObject)bj.getValue(), "break from proc-closure");
         } catch (JumpException.ReturnJump rj) {
             Object target = rj.getTarget();
 
-            if (target == this || block.isLambda) return (IRubyObject) rj.getValue();
+            if (target == this || block.type == Block.Type.LAMBDA) return (IRubyObject) rj.getValue();
 
             if (target == null) {
                 throw runtime.newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
             }
             throw rj;
         } 
     }
 
     @JRubyMethod(name = "arity")
     public RubyFixnum arity() {
         return getRuntime().newFixnum(block.arity().getValue());
     }
     
     @JRubyMethod(name = "to_proc")
     public RubyProc to_proc() {
     	return this;
     }
 }
diff --git a/src/org/jruby/RubyZlib.java b/src/org/jruby/RubyZlib.java
index da32d85894..af3e47fa1d 100644
--- a/src/org/jruby/RubyZlib.java
+++ b/src/org/jruby/RubyZlib.java
@@ -1,1086 +1,1086 @@
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
 package org.jruby;
 
 import java.io.InputStream;
 import java.io.IOException;
 
 import java.util.List;
 import java.util.ArrayList;
 
 import java.util.zip.GZIPInputStream;
 import java.util.zip.GZIPOutputStream;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.CRC32Ext;
 import org.jruby.util.Adler32Ext;
 import org.jruby.util.ZlibInflate;
 import org.jruby.util.ZlibDeflate;
 
 import org.jruby.util.ByteList;
 
 public class RubyZlib {
     /** Create the Zlib module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createZlibModule(Ruby runtime) {
         RubyModule result = runtime.defineModule("Zlib");
 
         RubyClass gzfile = result.defineClassUnder("GzipFile", runtime.getObject(), RubyGzipFile.GZIPFILE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyGzipFile.class);
         gzfile.defineAnnotatedMethods(RubyGzipFile.class);
         
         CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
         RubyClass gzreader = result.defineClassUnder("GzipReader", gzfile, RubyGzipReader.GZIPREADER_ALLOCATOR);
         gzreader.includeModule(runtime.getEnumerable());
         CallbackFactory callbackFactory2 = runtime.callbackFactory(RubyGzipReader.class);
         gzreader.defineAnnotatedMethods(RubyGzipReader.class);
         
         RubyClass standardError = runtime.fastGetClass("StandardError");
         RubyClass zlibError = result.defineClassUnder("Error", standardError, standardError.getAllocator());
         gzreader.defineClassUnder("Error", zlibError, zlibError.getAllocator());
 
         RubyClass gzwriter = result.defineClassUnder("GzipWriter", gzfile, RubyGzipWriter.GZIPWRITER_ALLOCATOR);
         CallbackFactory callbackFactory3 = runtime.callbackFactory(RubyGzipWriter.class);
         gzwriter.defineAnnotatedMethods(RubyGzipWriter.class);
 
         result.defineConstant("ZLIB_VERSION",runtime.newString("1.2.1"));
         result.defineConstant("VERSION",runtime.newString("0.6.0"));
 
         result.defineConstant("BINARY",runtime.newFixnum(0));
         result.defineConstant("ASCII",runtime.newFixnum(1));
         result.defineConstant("UNKNOWN",runtime.newFixnum(2));
 
         result.defineConstant("DEF_MEM_LEVEL",runtime.newFixnum(8));
         result.defineConstant("MAX_MEM_LEVEL",runtime.newFixnum(9));
 
         result.defineConstant("OS_UNIX",runtime.newFixnum(3));
         result.defineConstant("OS_UNKNOWN",runtime.newFixnum(255));
         result.defineConstant("OS_CODE",runtime.newFixnum(11));
         result.defineConstant("OS_ZSYSTEM",runtime.newFixnum(8));
         result.defineConstant("OS_VMCMS",runtime.newFixnum(4));
         result.defineConstant("OS_VMS",runtime.newFixnum(2));
         result.defineConstant("OS_RISCOS",runtime.newFixnum(13));
         result.defineConstant("OS_MACOS",runtime.newFixnum(7));
         result.defineConstant("OS_OS2",runtime.newFixnum(6));
         result.defineConstant("OS_AMIGA",runtime.newFixnum(1));
         result.defineConstant("OS_QDOS",runtime.newFixnum(12));
         result.defineConstant("OS_WIN32",runtime.newFixnum(11));
         result.defineConstant("OS_ATARI",runtime.newFixnum(5));
         result.defineConstant("OS_MSDOS",runtime.newFixnum(0));
         result.defineConstant("OS_CPM",runtime.newFixnum(9));
         result.defineConstant("OS_TOPS20",runtime.newFixnum(10));
 
         result.defineConstant("DEFAULT_STRATEGY",runtime.newFixnum(0));
         result.defineConstant("FILTERED",runtime.newFixnum(1));
         result.defineConstant("HUFFMAN_ONLY",runtime.newFixnum(2));
 
         result.defineConstant("NO_FLUSH",runtime.newFixnum(0));
         result.defineConstant("SYNC_FLUSH",runtime.newFixnum(2));
         result.defineConstant("FULL_FLUSH",runtime.newFixnum(3));
         result.defineConstant("FINISH",runtime.newFixnum(4));
 
         result.defineConstant("NO_COMPRESSION",runtime.newFixnum(0));
         result.defineConstant("BEST_SPEED",runtime.newFixnum(1));
         result.defineConstant("DEFAULT_COMPRESSION",runtime.newFixnum(-1));
         result.defineConstant("BEST_COMPRESSION",runtime.newFixnum(9));
 
         result.defineConstant("MAX_WBITS",runtime.newFixnum(15));
 
         CallbackFactory cf = runtime.callbackFactory(RubyZlib.class);
         result.defineAnnotatedMethods(RubyZlib.class);
 
         result.defineClassUnder("StreamEnd",zlibError, zlibError.getAllocator());
         result.defineClassUnder("StreamError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("BufError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("NeedDict",zlibError, zlibError.getAllocator());
         result.defineClassUnder("MemError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("VersionError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("DataError",zlibError, zlibError.getAllocator());
 
         RubyClass gzError = gzfile.defineClassUnder("Error",zlibError, zlibError.getAllocator());
         gzfile.defineClassUnder("CRCError",gzError, gzError.getAllocator());
         gzfile.defineClassUnder("NoFooter",gzError, gzError.getAllocator());
         gzfile.defineClassUnder("LengthError",gzError, gzError.getAllocator());
 
         // ZStream actually *isn't* allocatable
         RubyClass zstream = result.defineClassUnder("ZStream", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory zstreamcb = runtime.callbackFactory(ZStream.class);
         zstream.defineAnnotatedMethods(ZStream.class);
         zstream.undefineMethod("new");
 
         RubyClass infl = result.defineClassUnder("Inflate", zstream, Inflate.INFLATE_ALLOCATOR);
         CallbackFactory inflcb = runtime.callbackFactory(Inflate.class);
         infl.defineAnnotatedMethods(Inflate.class);
 
         RubyClass defl = result.defineClassUnder("Deflate", zstream, Deflate.DEFLATE_ALLOCATOR);
         CallbackFactory deflcb = runtime.callbackFactory(Deflate.class);
         defl.defineAnnotatedMethods(Deflate.class);
 
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("stringio"));
 
         return result;
     }
 
     @JRubyMethod(name = "zlib_version", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject zlib_version(IRubyObject recv) {
         return ((RubyModule)recv).fastGetConstant("ZLIB_VERSION");
     }
 
     @JRubyMethod(name = "version", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject version(IRubyObject recv) {
         return ((RubyModule)recv).fastGetConstant("VERSION");
     }
 
     @JRubyMethod(name = "crc32", optional = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject crc32(IRubyObject recv, IRubyObject[] args) throws Exception {
         args = Arity.scanArgs(recv.getRuntime(),args,0,2);
         int crc = 0;
         ByteList bytes = null;
         
         if (!args[0].isNil()) bytes = args[0].convertToString().getByteList();
         if (!args[1].isNil()) crc = RubyNumeric.fix2int(args[1]);
 
         CRC32Ext ext = new CRC32Ext(crc);
         if (bytes != null) {
             ext.update(bytes.unsafeBytes(), bytes.begin(), bytes.length());
         }
         
         return recv.getRuntime().newFixnum(ext.getValue());
     }
 
     @JRubyMethod(name = "adler32", optional = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject adler32(IRubyObject recv, IRubyObject[] args) throws Exception {
         args = Arity.scanArgs(recv.getRuntime(),args,0,2);
         int adler = 1;
         ByteList bytes = null;
         if (!args[0].isNil()) bytes = args[0].convertToString().getByteList();
         if (!args[1].isNil()) adler = RubyNumeric.fix2int(args[1]);
 
         Adler32Ext ext = new Adler32Ext(adler);
         if (bytes != null) {
             ext.update(bytes.unsafeBytes(), bytes.begin(), bytes.length()); // it's safe since adler.update doesn't modify the array
         }
         return recv.getRuntime().newFixnum(ext.getValue());
     }
 
     private final static long[] crctab = new long[]{
         0L, 1996959894L, 3993919788L, 2567524794L, 124634137L, 1886057615L, 3915621685L, 2657392035L, 249268274L, 2044508324L, 3772115230L, 2547177864L, 162941995L, 
         2125561021L, 3887607047L, 2428444049L, 498536548L, 1789927666L, 4089016648L, 2227061214L, 450548861L, 1843258603L, 4107580753L, 2211677639L, 325883990L, 
         1684777152L, 4251122042L, 2321926636L, 335633487L, 1661365465L, 4195302755L, 2366115317L, 997073096L, 1281953886L, 3579855332L, 2724688242L, 1006888145L, 
         1258607687L, 3524101629L, 2768942443L, 901097722L, 1119000684L, 3686517206L, 2898065728L, 853044451L, 1172266101L, 3705015759L, 2882616665L, 651767980L, 
         1373503546L, 3369554304L, 3218104598L, 565507253L, 1454621731L, 3485111705L, 3099436303L, 671266974L, 1594198024L, 3322730930L, 2970347812L, 795835527L, 
         1483230225L, 3244367275L, 3060149565L, 1994146192L, 31158534L, 2563907772L, 4023717930L, 1907459465L, 112637215L, 2680153253L, 3904427059L, 2013776290L, 
         251722036L, 2517215374L, 3775830040L, 2137656763L, 141376813L, 2439277719L, 3865271297L, 1802195444L, 476864866L, 2238001368L, 4066508878L, 1812370925L, 
         453092731L, 2181625025L, 4111451223L, 1706088902L, 314042704L, 2344532202L, 4240017532L, 1658658271L, 366619977L, 2362670323L, 4224994405L, 1303535960L, 
         984961486L, 2747007092L, 3569037538L, 1256170817L, 1037604311L, 2765210733L, 3554079995L, 1131014506L, 879679996L, 2909243462L, 3663771856L, 1141124467L, 
         855842277L, 2852801631L, 3708648649L, 1342533948L, 654459306L, 3188396048L, 3373015174L, 1466479909L, 544179635L, 3110523913L, 3462522015L, 1591671054L, 
         702138776L, 2966460450L, 3352799412L, 1504918807L, 783551873L, 3082640443L, 3233442989L, 3988292384L, 2596254646L, 62317068L, 1957810842L, 3939845945L, 
         2647816111L, 81470997L, 1943803523L, 3814918930L, 2489596804L, 225274430L, 2053790376L, 3826175755L, 2466906013L, 167816743L, 2097651377L, 4027552580L, 
         2265490386L, 503444072L, 1762050814L, 4150417245L, 2154129355L, 426522225L, 1852507879L, 4275313526L, 2312317920L, 282753626L, 1742555852L, 4189708143L, 
         2394877945L, 397917763L, 1622183637L, 3604390888L, 2714866558L, 953729732L, 1340076626L, 3518719985L, 2797360999L, 1068828381L, 1219638859L, 3624741850L, 
         2936675148L, 906185462L, 1090812512L, 3747672003L, 2825379669L, 829329135L, 1181335161L, 3412177804L, 3160834842L, 628085408L, 1382605366L, 3423369109L, 
         3138078467L, 570562233L, 1426400815L, 3317316542L, 2998733608L, 733239954L, 1555261956L, 3268935591L, 3050360625L, 752459403L, 1541320221L, 2607071920L, 
         3965973030L, 1969922972L, 40735498L, 2617837225L, 3943577151L, 1913087877L, 83908371L, 2512341634L, 3803740692L, 2075208622L, 213261112L, 2463272603L, 
         3855990285L, 2094854071L, 198958881L, 2262029012L, 4057260610L, 1759359992L, 534414190L, 2176718541L, 4139329115L, 1873836001L, 414664567L, 2282248934L, 
         4279200368L, 1711684554L, 285281116L, 2405801727L, 4167216745L, 1634467795L, 376229701L, 2685067896L, 3608007406L, 1308918612L, 956543938L, 2808555105L, 
         3495958263L, 1231636301L, 1047427035L, 2932959818L, 3654703836L, 1088359270L, 936918000L, 2847714899L, 3736837829L, 1202900863L, 817233897L, 3183342108L, 
         3401237130L, 1404277552L, 615818150L, 3134207493L, 3453421203L, 1423857449L, 601450431L, 3009837614L, 3294710456L, 1567103746L, 711928724L, 3020668471L, 
         3272380065L, 1510334235L, 755167117};
 
     @JRubyMethod(name = "crc_table", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject crc_table(IRubyObject recv) {
         List ll = new ArrayList(crctab.length);
         for(int i=0;i<crctab.length;i++) {
             ll.add(recv.getRuntime().newFixnum(crctab[i]));
         }
         return recv.getRuntime().newArray(ll);
     }
 
 
     public static abstract class ZStream extends RubyObject {
         protected boolean closed = false;
         protected boolean ended = false;
         protected boolean finished = false;
 
         protected abstract int internalTotalOut();
         protected abstract boolean internalStreamEndP();
         protected abstract void internalEnd();
         protected abstract void internalReset();
         protected abstract int internalAdler();
         protected abstract IRubyObject internalFinish() throws Exception;
         protected abstract int internalTotalIn();
         protected abstract void internalClose();
 
         public ZStream(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize(Block unusedBlock) {
             return this;
         }
 
         @JRubyMethod(name = "flust_next_out")
         public IRubyObject flush_next_out() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "total_out")
         public IRubyObject total_out() {
             return getRuntime().newFixnum(internalTotalOut());
         }
 
         @JRubyMethod(name = "stream_end?")
         public IRubyObject stream_end_p() {
             return internalStreamEndP() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "data_type")
         public IRubyObject data_type() {
             return getRuntime().fastGetModule("Zlib").fastGetConstant("UNKNOWN");
         }
 
         @JRubyMethod(name = "closed?")
         public IRubyObject closed_p() {
             return closed ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "ended?")
         public IRubyObject ended_p() {
             return ended ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "end")
         public IRubyObject end() {
             if(!ended) {
                 internalEnd();
                 ended = true;
             }
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "reset")
         public IRubyObject reset() {
             internalReset();
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "avail_out")
         public IRubyObject avail_out() {
             return RubyFixnum.zero(getRuntime());
         }
 
         @JRubyMethod(name = "avail_out=", required = 1)
         public IRubyObject set_avail_out(IRubyObject p1) {
             return p1;
         }
 
         @JRubyMethod(name = "adler")
         public IRubyObject adler() {
             return getRuntime().newFixnum(internalAdler());
         }
 
         @JRubyMethod(name = "finish")
         public IRubyObject finish() throws Exception {
             if(!finished) {
                 finished = true;
                 return internalFinish();
             }
             return getRuntime().newString("");
         }
 
         @JRubyMethod(name = "avail_in")
         public IRubyObject avail_in() {
             return RubyFixnum.zero(getRuntime());
         }
 
         @JRubyMethod(name = "flush_next_in")
         public IRubyObject flush_next_in() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "total_in")
         public IRubyObject total_in() {
             return getRuntime().newFixnum(internalTotalIn());
         }
 
         @JRubyMethod(name = "finished?")
         public IRubyObject finished_p() {
             return finished ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() {
             if(!closed) {
                 internalClose();
                 closed = true;
             }
             return getRuntime().getNil();
         }
     }
 
     public static class Inflate extends ZStream {
         protected static ObjectAllocator INFLATE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Inflate(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "inflate", required = 1, meta = true)
         public static IRubyObject s_inflate(IRubyObject recv, IRubyObject string) throws Exception {
             return ZlibInflate.s_inflate(recv,string.convertToString().getByteList());
         }
 
         public Inflate(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private ZlibInflate infl;
 
         @JRubyMethod(name = "initialize", rest = true, visibility = Visibility.PRIVATE)
         public IRubyObject _initialize(IRubyObject[] args) throws Exception {
             infl = new ZlibInflate(this);
             return this;
         }
 
         @JRubyMethod(name = "<<", required = 1)
         public IRubyObject append(IRubyObject arg) {
             infl.append(arg);
             return this;
         }
 
         @JRubyMethod(name = "sync_point?")
         public IRubyObject sync_point_p() {
             return infl.sync_point();
         }
 
         @JRubyMethod(name = "set_dictionary", required = 1)
         public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
             return infl.set_dictionary(arg);
         }
 
         @JRubyMethod(name = "inflate", required = 1)
         public IRubyObject inflate(IRubyObject string) throws Exception {
             return infl.inflate(string.convertToString().getByteList());
         }
 
         @JRubyMethod(name = "sync", required = 1)
         public IRubyObject sync(IRubyObject string) {
             return infl.sync(string);
         }
 
         protected int internalTotalOut() {
             return infl.getInflater().getTotalOut();
         }
 
         protected boolean internalStreamEndP() {
             return infl.getInflater().finished();
         }
 
         protected void internalEnd() {
             infl.getInflater().end();
         }
 
         protected void internalReset() {
             infl.getInflater().reset();
         }
 
         protected int internalAdler() {
             return infl.getInflater().getAdler();
         }
 
         protected IRubyObject internalFinish() throws Exception {
             infl.finish();
             return getRuntime().getNil();
         }
 
         public IRubyObject finished_p() {
             return infl.getInflater().finished() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         protected int internalTotalIn() {
             return infl.getInflater().getTotalIn();
         }
 
         protected void internalClose() {
             infl.close();
         }
     }
 
     public static class Deflate extends ZStream {
         protected static ObjectAllocator DEFLATE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Deflate(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "deflate", required = 1, optional = 1, meta = true)
         public static IRubyObject s_deflate(IRubyObject recv, IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(recv.getRuntime(),args,1,1);
             int level = -1;
             if(!args[1].isNil()) {
                 level = RubyNumeric.fix2int(args[1]);
             }
             return ZlibDeflate.s_deflate(recv,args[0].convertToString().getByteList(),level);
         }
 
         public Deflate(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private ZlibDeflate defl;
 
         @JRubyMethod(name = "initialize", optional = 4, visibility = Visibility.PRIVATE)
         public IRubyObject _initialize(IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(getRuntime(),args,0,4);
             int level = -1;
             int window_bits = 15;
             int memlevel = 8;
             int strategy = 0;
             if(!args[0].isNil()) {
                 level = RubyNumeric.fix2int(args[0]);
             }
             if(!args[1].isNil()) {
                 window_bits = RubyNumeric.fix2int(args[1]);
             }
             if(!args[2].isNil()) {
                 memlevel = RubyNumeric.fix2int(args[2]);
             }
             if(!args[3].isNil()) {
                 strategy = RubyNumeric.fix2int(args[3]);
             }
             defl = new ZlibDeflate(this,level,window_bits,memlevel,strategy);
             return this;
         }
 
         @JRubyMethod(name = "<<", required = 1)
         public IRubyObject append(IRubyObject arg) throws Exception {
             defl.append(arg);
             return this;
         }
 
         @JRubyMethod(name = "params", required = 2)
         public IRubyObject params(IRubyObject level, IRubyObject strategy) {
             defl.params(RubyNumeric.fix2int(level),RubyNumeric.fix2int(strategy));
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "set_dictionary", required = 1)
         public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
             return defl.set_dictionary(arg);
         }
         
         @JRubyMethod(name = "flush", optional = 1)
         public IRubyObject flush(IRubyObject[] args) throws Exception {
             int flush = 2; // SYNC_FLUSH
             if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
                 if(!args[0].isNil()) {
                     flush = RubyNumeric.fix2int(args[0]);
                 }
             }
             return defl.flush(flush);
         }
 
         @JRubyMethod(name = "deflate", required = 1, optional = 1)
         public IRubyObject deflate(IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(getRuntime(),args,1,1);
             int flush = 0; // NO_FLUSH
             if(!args[1].isNil()) {
                 flush = RubyNumeric.fix2int(args[1]);
             }
             return defl.deflate(args[0].convertToString().getByteList(),flush);
         }
 
         protected int internalTotalOut() {
             return defl.getDeflater().getTotalOut();
         }
 
         protected boolean internalStreamEndP() {
             return defl.getDeflater().finished();
         }
 
         protected void internalEnd() {
             defl.getDeflater().end();
         }
 
         protected void internalReset() {
             defl.getDeflater().reset();
         }
 
         protected int internalAdler() {
             return defl.getDeflater().getAdler();
         }
 
         protected IRubyObject internalFinish() throws Exception {
             return defl.finish();
         }
 
         protected int internalTotalIn() {
             return defl.getDeflater().getTotalIn();
         }
 
         protected void internalClose() {
             defl.close();
         }
     }
 
     public static class RubyGzipFile extends RubyObject {
         @JRubyMethod(name = "wrap", required = 2, frame = true, meta = true)
         public static IRubyObject wrap(IRubyObject recv, IRubyObject io, IRubyObject proc, Block unusedBlock) throws IOException {
             if (!(io instanceof RubyGzipFile)) throw recv.getRuntime().newTypeError(io, (RubyClass)recv);
             if (!proc.isNil()) {
                 try {
                     ((RubyProc)proc).call(new IRubyObject[]{io});
                 } finally {
                     RubyGzipFile zipIO = (RubyGzipFile)io;
                     if (!zipIO.isClosed()) {
                         zipIO.close();
                     }
                 }
                 return recv.getRuntime().getNil();
             }
 
             return io;
         }
         
         protected static ObjectAllocator GZIPFILE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipFile(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "new", frame = true, meta = true)
         public static RubyGzipFile newInstance(IRubyObject recv, Block block) {
             RubyClass klass = (RubyClass)recv;
             
             RubyGzipFile result = (RubyGzipFile) klass.allocate();
             
             result.callInit(new IRubyObject[0], block);
             
             return result;
         }
 
         protected boolean closed = false;
         protected boolean finished = false;
         private int os_code = 255;
         private int level = -1;
         private String orig_name;
         private String comment;
         protected IRubyObject realIo;
         private IRubyObject mtime;
 
         public RubyGzipFile(Ruby runtime, RubyClass type) {
             super(runtime, type);
             mtime = runtime.getNil();
         }
         
         @JRubyMethod(name = "os_code")
         public IRubyObject os_code() {
             return getRuntime().newFixnum(os_code);
         }
         
         @JRubyMethod(name = "closed?")
         public IRubyObject closed_p() {
             return closed ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         
         protected boolean isClosed() {
             return closed;
         }
         
         @JRubyMethod(name = "orig_name")
         public IRubyObject orig_name() {
             return orig_name == null ? getRuntime().getNil() : getRuntime().newString(orig_name);
         }
         
         @JRubyMethod(name = "to_io")
         public Object to_io() {
             return realIo;
         }
         
         @JRubyMethod(name = "comment")
         public IRubyObject comment() {
             return comment == null ? getRuntime().getNil() : getRuntime().newString(comment);
         }
         
         @JRubyMethod(name = "crc")
         public IRubyObject crc() {
             return RubyFixnum.zero(getRuntime());
         }
         
         @JRubyMethod(name = "mtime")
         public IRubyObject mtime() {
             return mtime;
         }
         
         @JRubyMethod(name = "sync")
         public IRubyObject sync() {
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "finish")
         public IRubyObject finish() throws IOException {
             if (!finished) {
                 //io.finish();
             }
             finished = true;
             return realIo;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             return null;
         }
         
         @JRubyMethod(name = "level")
         public IRubyObject level() {
             return getRuntime().newFixnum(level);
         }
         
         @JRubyMethod(name = "sync=", required = 1)
         public IRubyObject set_sync(IRubyObject ignored) {
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipReader extends RubyGzipFile {
         protected static ObjectAllocator GZIPREADER_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipReader(runtime, klass);
             }
         };
         
         @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
         public static RubyGzipReader newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             RubyClass klass = (RubyClass)recv;
             RubyGzipReader result = (RubyGzipReader)klass.allocate();
             result.callInit(args, block);
             return result;
         }
 
         @JRubyMethod(name = "open", required = 1, frame = true, meta = true)
         public static IRubyObject open(IRubyObject recv, IRubyObject filename, Block block) throws IOException {
             Ruby runtime = recv.getRuntime();
-            IRubyObject proc = block.isGiven() ? runtime.newProc(false, block) : runtime.getNil();
+            IRubyObject proc = block.isGiven() ? runtime.newProc(Block.Type.PROC, block) : runtime.getNil();
             RubyGzipReader io = newInstance(
                     recv,
                     new IRubyObject[]{ runtime.getFile().callMethod(
                             runtime.getCurrentContext(),
                             "open",
                             new IRubyObject[]{filename, runtime.newString("rb")})},
                             block);
             
             return RubyGzipFile.wrap(recv, io, proc, null);
         }
 
         public RubyGzipReader(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
         
         private int line;
         private InputStream io;
         
         @JRubyMethod(name = "initialize", required = 1, frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize(IRubyObject io, Block unusedBlock) {
             realIo = io;
             try {
                 this.io = new GZIPInputStream(new IOInputStream(io));
             } catch (IOException e) {
                 Ruby runtime = io.getRuntime();
                 RubyClass errorClass = runtime.fastGetModule("Zlib").fastGetClass("GzipReader").fastGetClass("Error");
                 throw new RaiseException(RubyException.newException(runtime, errorClass, e.getMessage()));
             }
 
             line = 1;
             
             return this;
         }
         
         @JRubyMethod(name = "rewind")
         public IRubyObject rewind() {
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "lineno")
         public IRubyObject lineno() {
             return getRuntime().newFixnum(line);
         }
 
         @JRubyMethod(name = "readline")
         public IRubyObject readline() throws IOException {
             IRubyObject dst = gets(new IRubyObject[0]);
             if (dst.isNil()) {
                 throw getRuntime().newEOFError();
             }
             return dst;
         }
 
         public IRubyObject internalGets(IRubyObject[] args) throws IOException {
             String sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
             if (args.length > 0) {
                 sep = args[0].toString();
             }
             return internalSepGets(sep);
         }
 
         private IRubyObject internalSepGets(String sep) throws IOException {
             StringBuffer result = new StringBuffer();
             char ce = (char) io.read();
             while (ce != -1 && sep.indexOf(ce) == -1) {
                 result.append((char) ce);
                 ce = (char) io.read();
             }
             line++;
             return getRuntime().newString(result.append(sep).toString());
         }
 
         @JRubyMethod(name = "gets", optional = 1)
         public IRubyObject gets(IRubyObject[] args) throws IOException {
             IRubyObject result = internalGets(args);
             if (!result.isNil()) {
                 getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
             }
             return result;
         }
 
         private final static int BUFF_SIZE = 4096;
         
         @JRubyMethod(name = "read", optional = 1)
         public IRubyObject read(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil()) {
                 ByteList val = new ByteList(10);
                 byte[] buffer = new byte[BUFF_SIZE];
                 int read = io.read(buffer);
                 while (read != -1) {
                     val.append(buffer,0,read);
                     read = io.read(buffer);
                 }
                 return RubyString.newString(getRuntime(),val);
             } 
 
             int len = RubyNumeric.fix2int(args[0]);
             if (len < 0) {
             	throw getRuntime().newArgumentError("negative length " + len + " given");
             } else if (len > 0) {
             	byte[] buffer = new byte[len];
             	int toRead = len;
             	int offset = 0;
             	int read = 0;
             	while (toRead > 0) {
             		read = io.read(buffer,offset,toRead);
             		if (read == -1) {
             			break;
             		}
             		toRead -= read;
             		offset += read;
             	} // hmm...
             	return RubyString.newString(getRuntime(),new ByteList(buffer,0,len-toRead,false));
             }
                 
             return getRuntime().newString("");
         }
 
         @JRubyMethod(name = "lineno=", required = 1)
         public IRubyObject set_lineno(IRubyObject lineArg) {
             line = RubyNumeric.fix2int(lineArg);
             return lineArg;
         }
 
         @JRubyMethod(name = "pos")
         public IRubyObject pos() {
             return RubyFixnum.zero(getRuntime());
         }
         
         @JRubyMethod(name = "readchar")
         public IRubyObject readchar() throws IOException {
             int value = io.read();
             if (value == -1) {
                 throw getRuntime().newEOFError();
             }
             return getRuntime().newFixnum(value);
         }
 
         @JRubyMethod(name = "getc")
         public IRubyObject getc() throws IOException {
             int value = io.read();
             return value == -1 ? getRuntime().getNil() : getRuntime().newFixnum(value);
         }
 
         private boolean isEof() throws IOException {
             return ((GZIPInputStream)io).available() != 1;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "eof")
         public IRubyObject eof() throws IOException {
             return isEof() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "eof?")
         public IRubyObject eof_p() throws IOException {
             return eof();
         }
 
         @JRubyMethod(name = "unused")
         public IRubyObject unused() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "tell")
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "each", optional = 1, frame = true)
         public IRubyObject each(IRubyObject[] args, Block block) throws IOException {
             String sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
             
             if (args.length > 0 && !args[0].isNil()) {
                 sep = args[0].toString();
             }
 
             ThreadContext context = getRuntime().getCurrentContext();
             while (!isEof()) {
                 block.yield(context, internalSepGets(sep));
             }
             
             return getRuntime().getNil();
         }
     
         @JRubyMethod(name = "ungetc", required = 1)
         public IRubyObject ungetc(IRubyObject arg) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "readlines", optional = 1)
         public IRubyObject readlines(IRubyObject[] args) throws IOException {
             List array = new ArrayList();
             
             if (args.length != 0 && args[0].isNil()) {
                 array.add(read(new IRubyObject[0]));
             } else {
                 String seperator = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
                 if (args.length > 0) {
                     seperator = args[0].toString();
                 }
                 while (!isEof()) {
                     array.add(internalSepGets(seperator));
                 }
             }
             return getRuntime().newArray(array);
         }
 
         @JRubyMethod(name = "each_byte", frame = true)
         public IRubyObject each_byte(Block block) throws IOException {
             int value = io.read();
 
             ThreadContext context = getRuntime().getCurrentContext();
             while (value != -1) {
                 block.yield(context, getRuntime().newFixnum(value));
                 value = io.read();
             }
             
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipWriter extends RubyGzipFile {
         protected static ObjectAllocator GZIPWRITER_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipWriter(runtime, klass);
             }
         };
         
         @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
         public static RubyGzipWriter newGzipWriter(IRubyObject recv, IRubyObject[] args, Block block) {
             RubyClass klass = (RubyClass)recv;
             
             RubyGzipWriter result = (RubyGzipWriter)klass.allocate();
             result.callInit(args, block);
             return result;
         }
 
         @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
         public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) throws IOException {
             Ruby runtime = recv.getRuntime();
             IRubyObject level = runtime.getNil();
             IRubyObject strategy = runtime.getNil();
 
             if (args.length > 1) {
                 level = args[1];
                 if (args.length > 2) strategy = args[2];
             }
 
-            IRubyObject proc = block.isGiven() ? runtime.newProc(false, block) : runtime.getNil();
+            IRubyObject proc = block.isGiven() ? runtime.newProc(Block.Type.PROC, block) : runtime.getNil();
             RubyGzipWriter io = newGzipWriter(
                     recv,
                     new IRubyObject[]{ runtime.getFile().callMethod(
                             runtime.getCurrentContext(),
                             "open",
                             new IRubyObject[]{args[0],runtime.newString("wb")}),level,strategy},block);
             
             return RubyGzipFile.wrap(recv, io, proc, null);
         }
 
         public RubyGzipWriter(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private GZIPOutputStream io;
         
         @JRubyMethod(name = "initialize", required = 1, rest = true, frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize2(IRubyObject[] args, Block unusedBlock) throws IOException {
             realIo = (RubyObject)args[0];
             this.io = new GZIPOutputStream(new IOOutputStream(args[0]));
             
             return this;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "append", required = 1)
         public IRubyObject append(IRubyObject p1) throws IOException {
             this.write(p1);
             return this;
         }
 
         @JRubyMethod(name = "printf", required = 1, rest = true)
         public IRubyObject printf(IRubyObject[] args) throws IOException {
             write(RubyKernel.sprintf(this, args));
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "print", rest = true)
         public IRubyObject print(IRubyObject[] args) throws IOException {
             if (args.length != 0) {
                 for (int i = 0, j = args.length; i < j; i++) {
                     write(args[i]);
                 }
             }
             
             IRubyObject sep = getRuntime().getGlobalVariables().get("$\\");
             if (!sep.isNil()) {
                 write(sep);
             }
             
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "pos")
         public IRubyObject pos() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "orig_name=", required = 1)
         public IRubyObject set_orig_name(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "comment=", required = 1)
         public IRubyObject set_comment(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "putc", required = 1)
         public IRubyObject putc(IRubyObject p1) throws IOException {
             io.write(RubyNumeric.fix2int(p1));
             return p1;
         }
         
         @JRubyMethod(name = "puts", rest = true)
         public IRubyObject puts(IRubyObject[] args) throws IOException {
             RubyStringIO sio = (RubyStringIO)getRuntime().fastGetClass("StringIO").newInstance(new IRubyObject[0], Block.NULL_BLOCK);
             sio.puts(args);
             write(sio.string());
             
             return getRuntime().getNil();
         }
 
         public IRubyObject finish() throws IOException {
             if (!finished) {
                 io.finish();
             }
             finished = true;
             return realIo;
         }
 
         @JRubyMethod(name = "flush", optional = 1)
         public IRubyObject flush(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil() || RubyNumeric.fix2int(args[0]) != 0) { // Zlib::NO_FLUSH
                 io.flush();
             }
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "mtime=", required = 1)
         public IRubyObject set_mtime(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "tell")
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "write", required = 1)
         public IRubyObject write(IRubyObject p1) throws IOException {
             ByteList bytes = p1.convertToString().getByteList();
             io.write(bytes.unsafeBytes(), bytes.begin(), bytes.length());
             return getRuntime().newFixnum(bytes.length());
         }
     }
 }
diff --git a/src/org/jruby/compiler/ASTCompiler.java b/src/org/jruby/compiler/ASTCompiler.java
index b3f83d5080..4ea9c9b4c4 100644
--- a/src/org/jruby/compiler/ASTCompiler.java
+++ b/src/org/jruby/compiler/ASTCompiler.java
@@ -1,3019 +1,3189 @@
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
-
 package org.jruby.compiler;
 
 import java.util.Iterator;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.AndNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
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
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.RubyMatchData;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.PostExeNode;
 import org.jruby.ast.PreExeNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author headius
  */
 public class ASTCompiler {
+
     public static void compile(Node node, MethodCompiler context) {
         if (node == null) {
             context.loadNil();
             return;
         }
         switch (node.nodeId) {
-        case ALIASNODE:
-            compileAlias(node, context);
-            break;
-        case ANDNODE:
-            compileAnd(node, context);
-            break;
-        case ARGSCATNODE:
-            compileArgsCat(node, context);
-            break;
-        case ARGSPUSHNODE:
-            compileArgsPush(node, context);
-            break;
-        case ARRAYNODE:
-            compileArray(node, context);
-            break;
-        case ATTRASSIGNNODE:
-            compileAttrAssign(node, context);
-            break;
-        case BACKREFNODE:
-            compileBackref(node, context);
-            break;
-        case BEGINNODE:
-            compileBegin(node, context);
-            break;
-        case BIGNUMNODE:
-            compileBignum(node, context);
-            break;
-        case BLOCKNODE:
-            compileBlock(node, context);
-            break;
-        case BREAKNODE:
-            compileBreak(node, context);
-            break;
-        case CALLNODE:
-            compileCall(node, context);
-            break;
-        case CASENODE:
-            compileCase(node, context);
-            break;
-        case CLASSNODE:
-            compileClass(node, context);
-            break;
-        case CLASSVARNODE:
-            compileClassVar(node, context);
-            break;
-        case CLASSVARASGNNODE:
-            compileClassVarAsgn(node, context);
-            break;
-        case CLASSVARDECLNODE:
-            compileClassVarDecl(node, context);
-            break;
-        case COLON2NODE:
-            compileColon2(node, context);
-            break;
-        case COLON3NODE:
-            compileColon3(node, context);
-            break;
-        case CONSTDECLNODE:
-            compileConstDecl(node, context);
-            break;
-        case CONSTNODE:
-            compileConst(node, context);
-            break;
-        case DASGNNODE:
-            compileDAsgn(node, context);
-            break;
-        case DEFINEDNODE:
-            compileDefined(node, context);
-            break;
-        case DEFNNODE:
-            compileDefn(node, context);
-            break;
-        case DEFSNODE:
-            compileDefs(node, context);
-            break;
-        case DOTNODE:
-            compileDot(node, context);
-            break;
-        case DREGEXPNODE:
-            compileDRegexp(node, context);
-            break;
-        case DSTRNODE:
-            compileDStr(node, context);
-            break;
-        case DSYMBOLNODE:
-            compileDSymbol(node, context);
-            break;
-        case DVARNODE:
-            compileDVar(node, context);
-            break;
-        case DXSTRNODE:
-            compileDXStr(node, context);
-            break;
-        case ENSURENODE:
-            compileEnsureNode(node, context);
-            break;
-        case EVSTRNODE:
-            compileEvStr(node, context);
-            break;
-        case FALSENODE:
-            compileFalse(node, context);
-            break;
-        case FCALLNODE:
-            compileFCall(node, context);
-            break;
-        case FIXNUMNODE:
-            compileFixnum(node, context);
-            break;
-        case FLIPNODE:
-            compileFlip(node, context);
-            break;
-        case FLOATNODE:
-            compileFloat(node, context);
-            break;
-        case FORNODE:
-            compileFor(node, context);
-            break;
-        case GLOBALASGNNODE:
-            compileGlobalAsgn(node, context);
-            break;
-        case GLOBALVARNODE:
-            compileGlobalVar(node, context);
-            break;
-        case HASHNODE:
-            compileHash(node, context);
-            break;
-        case IFNODE:
-            compileIf(node, context);
-            break;
-        case INSTASGNNODE:
-            compileInstAsgn(node, context);
-            break;
-        case INSTVARNODE:
-            compileInstVar(node, context);
-            break;
-        case ITERNODE:
-            compileIter(node, context);
-            break;
-        case LOCALASGNNODE:
-            compileLocalAsgn(node, context);
-            break;
-        case LOCALVARNODE:
-            compileLocalVar(node, context);
-            break;
-        case MATCH2NODE:
-            compileMatch2(node, context);
-            break;
-        case MATCH3NODE:
-            compileMatch3(node, context);
-            break;
-        case MATCHNODE:
-            compileMatch(node, context);
-            break;
-        case MODULENODE:
-            compileModule(node, context);
-            break;
-        case MULTIPLEASGNNODE:
-            compileMultipleAsgn(node, context);
-            break;
-        case NEWLINENODE:
-            compileNewline(node, context);
-            break;
-        case NEXTNODE:
-            compileNext(node, context);
-            break;
-        case NTHREFNODE:
-            compileNthRef(node, context);
-            break;
-        case NILNODE:
-            compileNil(node, context);
-            break;
-        case NOTNODE:
-            compileNot(node, context);
-            break;
-        case OPASGNANDNODE:
-            compileOpAsgnAnd(node, context);
-            break;
-        case OPASGNNODE:
-            compileOpAsgn(node, context);
-            break;
-        case OPASGNORNODE:
-            compileOpAsgnOr(node, context);
-            break;
-        case OPELEMENTASGNNODE:
-            compileOpElementAsgn(node, context);
-            break;
-        case ORNODE:
-            compileOr(node, context);
-            break;
-        case POSTEXENODE:
-            compilePostExe(node, context);
-            break;
-        case PREEXENODE:
-            compilePreExe(node, context);
-            break;
-        case REDONODE:
-            compileRedo(node, context);
-            break;
-        case REGEXPNODE:
-            compileRegexp(node, context);
-            break;
-        case RESCUEBODYNODE:
-            throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
-        case RESCUENODE:
-            compileRescue(node, context);
-            break;
-        case RETRYNODE:
-            compileRetry(node, context);
-            break;
-        case RETURNNODE:
-            compileReturn(node, context);
-            break;
-        case ROOTNODE:
-            throw new NotCompilableException("Use compileRoot(); Root node at: " + node.getPosition());
-        case SCLASSNODE:
-            compileSClass(node, context);
-            break;
-        case SELFNODE:
-            compileSelf(node, context);
-            break;
-        case SPLATNODE:
-            compileSplat(node, context);
-            break;
-        case STRNODE:
-            compileStr(node, context);
-            break;
-        case SUPERNODE:
-            compileSuper(node, context);
-            break;
-        case SVALUENODE:
-            compileSValue(node, context);
-            break;
-        case SYMBOLNODE:
-            compileSymbol(node, context);
-            break;
-        case TOARYNODE:
-            compileToAry(node, context);
-            break;
-        case TRUENODE:
-            compileTrue(node, context);
-            break;
-        case UNDEFNODE:
-            compileUndef(node, context);
-            break;
-        case UNTILNODE:
-            compileUntil(node, context);
-            break;
-        case VALIASNODE:
-            compileVAlias(node, context);
-            break;
-        case VCALLNODE:
-            compileVCall(node, context);
-            break;
-        case WHILENODE:
-            compileWhile(node, context);
-            break;
-        case WHENNODE:
-            assert false: "When nodes are handled by case node compilation.";
-            break;
-        case XSTRNODE:
-            compileXStr(node, context);
-            break;
-        case YIELDNODE:
-            compileYield(node, context);
-            break;
-        case ZARRAYNODE:
-            compileZArray(node, context);
-            break;
-        case ZSUPERNODE:
-            compileZSuper(node, context);
-            break;
-        default:
-            assert false: "Unknown node encountered in compiler: " + node;
+            case ALIASNODE:
+                compileAlias(node, context);
+                break;
+            case ANDNODE:
+                compileAnd(node, context);
+                break;
+            case ARGSCATNODE:
+                compileArgsCat(node, context);
+                break;
+            case ARGSPUSHNODE:
+                compileArgsPush(node, context);
+                break;
+            case ARRAYNODE:
+                compileArray(node, context);
+                break;
+            case ATTRASSIGNNODE:
+                compileAttrAssign(node, context);
+                break;
+            case BACKREFNODE:
+                compileBackref(node, context);
+                break;
+            case BEGINNODE:
+                compileBegin(node, context);
+                break;
+            case BIGNUMNODE:
+                compileBignum(node, context);
+                break;
+            case BLOCKNODE:
+                compileBlock(node, context);
+                break;
+            case BREAKNODE:
+                compileBreak(node, context);
+                break;
+            case CALLNODE:
+                compileCall(node, context);
+                break;
+            case CASENODE:
+                compileCase(node, context);
+                break;
+            case CLASSNODE:
+                compileClass(node, context);
+                break;
+            case CLASSVARNODE:
+                compileClassVar(node, context);
+                break;
+            case CLASSVARASGNNODE:
+                compileClassVarAsgn(node, context);
+                break;
+            case CLASSVARDECLNODE:
+                compileClassVarDecl(node, context);
+                break;
+            case COLON2NODE:
+                compileColon2(node, context);
+                break;
+            case COLON3NODE:
+                compileColon3(node, context);
+                break;
+            case CONSTDECLNODE:
+                compileConstDecl(node, context);
+                break;
+            case CONSTNODE:
+                compileConst(node, context);
+                break;
+            case DASGNNODE:
+                compileDAsgn(node, context);
+                break;
+            case DEFINEDNODE:
+                compileDefined(node, context);
+                break;
+            case DEFNNODE:
+                compileDefn(node, context);
+                break;
+            case DEFSNODE:
+                compileDefs(node, context);
+                break;
+            case DOTNODE:
+                compileDot(node, context);
+                break;
+            case DREGEXPNODE:
+                compileDRegexp(node, context);
+                break;
+            case DSTRNODE:
+                compileDStr(node, context);
+                break;
+            case DSYMBOLNODE:
+                compileDSymbol(node, context);
+                break;
+            case DVARNODE:
+                compileDVar(node, context);
+                break;
+            case DXSTRNODE:
+                compileDXStr(node, context);
+                break;
+            case ENSURENODE:
+                compileEnsureNode(node, context);
+                break;
+            case EVSTRNODE:
+                compileEvStr(node, context);
+                break;
+            case FALSENODE:
+                compileFalse(node, context);
+                break;
+            case FCALLNODE:
+                compileFCall(node, context);
+                break;
+            case FIXNUMNODE:
+                compileFixnum(node, context);
+                break;
+            case FLIPNODE:
+                compileFlip(node, context);
+                break;
+            case FLOATNODE:
+                compileFloat(node, context);
+                break;
+            case FORNODE:
+                compileFor(node, context);
+                break;
+            case GLOBALASGNNODE:
+                compileGlobalAsgn(node, context);
+                break;
+            case GLOBALVARNODE:
+                compileGlobalVar(node, context);
+                break;
+            case HASHNODE:
+                compileHash(node, context);
+                break;
+            case IFNODE:
+                compileIf(node, context);
+                break;
+            case INSTASGNNODE:
+                compileInstAsgn(node, context);
+                break;
+            case INSTVARNODE:
+                compileInstVar(node, context);
+                break;
+            case ITERNODE:
+                compileIter(node, context);
+                break;
+            case LOCALASGNNODE:
+                compileLocalAsgn(node, context);
+                break;
+            case LOCALVARNODE:
+                compileLocalVar(node, context);
+                break;
+            case MATCH2NODE:
+                compileMatch2(node, context);
+                break;
+            case MATCH3NODE:
+                compileMatch3(node, context);
+                break;
+            case MATCHNODE:
+                compileMatch(node, context);
+                break;
+            case MODULENODE:
+                compileModule(node, context);
+                break;
+            case MULTIPLEASGNNODE:
+                compileMultipleAsgn(node, context);
+                break;
+            case NEWLINENODE:
+                compileNewline(node, context);
+                break;
+            case NEXTNODE:
+                compileNext(node, context);
+                break;
+            case NTHREFNODE:
+                compileNthRef(node, context);
+                break;
+            case NILNODE:
+                compileNil(node, context);
+                break;
+            case NOTNODE:
+                compileNot(node, context);
+                break;
+            case OPASGNANDNODE:
+                compileOpAsgnAnd(node, context);
+                break;
+            case OPASGNNODE:
+                compileOpAsgn(node, context);
+                break;
+            case OPASGNORNODE:
+                compileOpAsgnOr(node, context);
+                break;
+            case OPELEMENTASGNNODE:
+                compileOpElementAsgn(node, context);
+                break;
+            case ORNODE:
+                compileOr(node, context);
+                break;
+            case POSTEXENODE:
+                compilePostExe(node, context);
+                break;
+            case PREEXENODE:
+                compilePreExe(node, context);
+                break;
+            case REDONODE:
+                compileRedo(node, context);
+                break;
+            case REGEXPNODE:
+                compileRegexp(node, context);
+                break;
+            case RESCUEBODYNODE:
+                throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
+            case RESCUENODE:
+                compileRescue(node, context);
+                break;
+            case RETRYNODE:
+                compileRetry(node, context);
+                break;
+            case RETURNNODE:
+                compileReturn(node, context);
+                break;
+            case ROOTNODE:
+                throw new NotCompilableException("Use compileRoot(); Root node at: " + node.getPosition());
+            case SCLASSNODE:
+                compileSClass(node, context);
+                break;
+            case SELFNODE:
+                compileSelf(node, context);
+                break;
+            case SPLATNODE:
+                compileSplat(node, context);
+                break;
+            case STRNODE:
+                compileStr(node, context);
+                break;
+            case SUPERNODE:
+                compileSuper(node, context);
+                break;
+            case SVALUENODE:
+                compileSValue(node, context);
+                break;
+            case SYMBOLNODE:
+                compileSymbol(node, context);
+                break;
+            case TOARYNODE:
+                compileToAry(node, context);
+                break;
+            case TRUENODE:
+                compileTrue(node, context);
+                break;
+            case UNDEFNODE:
+                compileUndef(node, context);
+                break;
+            case UNTILNODE:
+                compileUntil(node, context);
+                break;
+            case VALIASNODE:
+                compileVAlias(node, context);
+                break;
+            case VCALLNODE:
+                compileVCall(node, context);
+                break;
+            case WHILENODE:
+                compileWhile(node, context);
+                break;
+            case WHENNODE:
+                assert false : "When nodes are handled by case node compilation.";
+                break;
+            case XSTRNODE:
+                compileXStr(node, context);
+                break;
+            case YIELDNODE:
+                compileYield(node, context);
+                break;
+            case ZARRAYNODE:
+                compileZArray(node, context);
+                break;
+            case ZSUPERNODE:
+                compileZSuper(node, context);
+                break;
+            default:
+                assert false : "Unknown node encountered in compiler: " + node;
         }
     }
-    
+
     public static void compileArguments(Node node, MethodCompiler context) {
         switch (node.nodeId) {
-        case ARGSCATNODE:
-            compileArgsCatArguments(node, context);
-            break;
-        case ARGSPUSHNODE:
-            compileArgsPushArguments(node, context);
-            break;
-        case ARRAYNODE:
-            compileArrayArguments(node, context);
-            break;
-        case SPLATNODE:
-            compileSplatArguments(node, context);
-            break;
-        default:
-            compile(node, context);
-            context.convertToJavaArray();
+            case ARGSCATNODE:
+                compileArgsCatArguments(node, context);
+                break;
+            case ARGSPUSHNODE:
+                compileArgsPushArguments(node, context);
+                break;
+            case ARRAYNODE:
+                compileArrayArguments(node, context);
+                break;
+            case SPLATNODE:
+                compileSplatArguments(node, context);
+                break;
+            default:
+                compile(node, context);
+                context.convertToJavaArray();
         }
     }
-    
+
     public static void compileAssignment(Node node, MethodCompiler context) {
         switch (node.nodeId) {
-        case ATTRASSIGNNODE:
-            compileAttrAssignAssignment(node, context);
-            break;
-        case DASGNNODE:
-            compileDAsgnAssignment(node, context);
-            break;
-        case CLASSVARASGNNODE:
-            compileClassVarAsgnAssignment(node, context);
-            break;
-        case CLASSVARDECLNODE:
-            compileClassVarDeclAssignment(node, context);
-            break;
-        case CONSTDECLNODE:
-            compileConstDeclAssignment(node, context);
-            break;
-        case GLOBALASGNNODE:
-            compileGlobalAsgnAssignment(node, context);
-            break;
-        case INSTASGNNODE:
-            compileInstAsgnAssignment(node, context);
-            break;
-        case LOCALASGNNODE:
-            compileLocalAsgnAssignment(node, context);
-            break;
-        case MULTIPLEASGNNODE:
-            compileMultipleAsgnAssignment(node, context);
-            break;
-        case ZEROARGNODE:
-            throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
-        default:    
-            throw new NotCompilableException("Can't compile assignment node: " + node);
+            case ATTRASSIGNNODE:
+                compileAttrAssignAssignment(node, context);
+                break;
+            case DASGNNODE:
+                compileDAsgnAssignment(node, context);
+                break;
+            case CLASSVARASGNNODE:
+                compileClassVarAsgnAssignment(node, context);
+                break;
+            case CLASSVARDECLNODE:
+                compileClassVarDeclAssignment(node, context);
+                break;
+            case CONSTDECLNODE:
+                compileConstDeclAssignment(node, context);
+                break;
+            case GLOBALASGNNODE:
+                compileGlobalAsgnAssignment(node, context);
+                break;
+            case INSTASGNNODE:
+                compileInstAsgnAssignment(node, context);
+                break;
+            case LOCALASGNNODE:
+                compileLocalAsgnAssignment(node, context);
+                break;
+            case MULTIPLEASGNNODE:
+                compileMultipleAsgnAssignment(node, context);
+                break;
+            case ZEROARGNODE:
+                throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
+            default:
+                throw new NotCompilableException("Can't compile assignment node: " + node);
         }
     }
-    
+
     public static YARVNodesCompiler getYARVCompiler() {
         return new YARVNodesCompiler();
     }
 
     public static void compileAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final AliasNode alias = (AliasNode)node;
-        
-        context.defineAlias(alias.getNewName(),alias.getOldName());
+
+        final AliasNode alias = (AliasNode) node;
+
+        context.defineAlias(alias.getNewName(), alias.getOldName());
     }
-    
+
     public static void compileAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final AndNode andNode = (AndNode)node;
-        
+
+        final AndNode andNode = (AndNode) node;
+
         compile(andNode.getFirstNode(), context);
-        
+
         BranchCallback longCallback = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                compile(andNode.getSecondNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        compile(andNode.getSecondNode(), context);
+                    }
+                };
+
         context.performLogicalAnd(longCallback);
     }
-    
+
     public static void compileArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArrayNode arrayNode = (ArrayNode)node;
-        
+
+        ArrayNode arrayNode = (ArrayNode) node;
+
         ArrayCallback callback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray, int index) {
-                Node node = (Node)((Object[])sourceArray)[index];
-                compile(node, context);
-            }
-        };
-        
+
+                    public void nextValue(MethodCompiler context, Object sourceArray, int index) {
+                        Node node = (Node) ((Object[]) sourceArray)[index];
+                        compile(node, context);
+                    }
+                };
+
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
         context.createNewArray(arrayNode.isLightweight());
     }
-    
+
     public static void compileArgsCat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArgsCatNode argsCatNode = (ArgsCatNode)node;
-        
+
+        ArgsCatNode argsCatNode = (ArgsCatNode) node;
+
         compile(argsCatNode.getFirstNode(), context);
         context.ensureRubyArray();
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
     }
-    
+
     public static void compileArgsPush(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArgsPushNode argsPush = (ArgsPushNode)node;
-        
+
+        ArgsPushNode argsPush = (ArgsPushNode) node;
+
         compile(argsPush.getFirstNode(), context);
         compile(argsPush.getSecondNode(), context);
         context.concatArrays();
     }
-    
+
     public static void compileAttrAssign(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        AttrAssignNode attrAssignNode = (AttrAssignNode)node;
-        
+
+        AttrAssignNode attrAssignNode = (AttrAssignNode) node;
+
         compile(attrAssignNode.getReceiverNode(), context);
         compileArguments(attrAssignNode.getArgsNode(), context);
-        
+
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
     }
-    
+
     public static void compileAttrAssignAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        AttrAssignNode attrAssignNode = (AttrAssignNode)node;
-        
+
+        AttrAssignNode attrAssignNode = (AttrAssignNode) node;
+
         compile(attrAssignNode.getReceiverNode(), context);
         context.swapValues();
         if (attrAssignNode.getArgsNode() != null) {
             compileArguments(attrAssignNode.getArgsNode(), context);
             context.swapValues();
             context.appendToObjectArray();
         } else {
             context.createObjectArray(1);
         }
-        
+
         context.getInvocationCompiler().invokeAttrAssign(attrAssignNode.getName());
     }
-    
+
     public static void compileBackref(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         BackRefNode iVisited = (BackRefNode) node;
-        
+
         context.performBackref(iVisited.getType());
     }
-    
+
     public static void compileBegin(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        BeginNode beginNode = (BeginNode)node;
-        
+
+        BeginNode beginNode = (BeginNode) node;
+
         compile(beginNode.getBodyNode(), context);
     }
 
     public static void compileBignum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        context.createNewBignum(((BignumNode)node).getValue());
+        context.createNewBignum(((BignumNode) node).getValue());
     }
 
     public static void compileBlock(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        BlockNode blockNode = (BlockNode)node;
-        
+
+        BlockNode blockNode = (BlockNode) node;
+
         for (Iterator<Node> iter = blockNode.childNodes().iterator(); iter.hasNext();) {
             Node n = iter.next();
-            
+
             compile(n, context);
-            
+
             if (iter.hasNext()) {
                 // clear result from previous line
                 context.consumeCurrentValue();
             }
         }
     }
-    
+
     public static void compileBreak(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final BreakNode breakNode = (BreakNode)node;
-        
+
+        final BreakNode breakNode = (BreakNode) node;
+
         ClosureCallback valueCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (breakNode.getValueNode() != null) {
-                    ASTCompiler.compile(breakNode.getValueNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (breakNode.getValueNode() != null) {
+                            ASTCompiler.compile(breakNode.getValueNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         context.issueBreakEvent(valueCallback);
     }
-    
+
     public static void compileCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final CallNode callNode = (CallNode)node;
-        
+
+        final CallNode callNode = (CallNode) node;
+
         ClosureCallback receiverCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(callNode.getReceiverNode(), context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(callNode.getReceiverNode(), context);
+                    }
+                };
+
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileArguments(callNode.getArgsNode(), context);
-            }
-        };
-                
+
+                    public void compile(MethodCompiler context) {
+                        compileArguments(callNode.getArgsNode(), context);
+                    }
+                };
+
         if (callNode.getIterNode() == null) {
             // no block, go for simple version
             if (callNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, null, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, null, CallType.NORMAL, null, false);
             }
         } else {
             ClosureCallback closureArg = getBlock(callNode.getIterNode());
-            
+
             if (callNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, argsCallback, CallType.NORMAL, closureArg, false);
             } else {
                 context.getInvocationCompiler().invokeDynamic(callNode.getName(), receiverCallback, null, CallType.NORMAL, closureArg, false);
             }
         }
     }
-    
+
     public static void compileCase(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        CaseNode caseNode = (CaseNode)node;
-        
+
+        CaseNode caseNode = (CaseNode) node;
+
         boolean hasCase = false;
         if (caseNode.getCaseNode() != null) {
             compile(caseNode.getCaseNode(), context);
             hasCase = true;
         }
 
         context.pollThreadEvents();
 
         Node firstWhenNode = caseNode.getFirstWhenNode();
         compileWhen(firstWhenNode, context, hasCase);
     }
-    
+
     public static void compileWhen(Node node, MethodCompiler context, final boolean hasCase) {
         if (node == null) {
             // reached the end of the when chain, pop the case (if provided) and we're done
             if (hasCase) {
                 context.consumeCurrentValue();
             }
             context.loadNil();
             return;
         }
-        
+
         if (!(node instanceof WhenNode)) {
             if (hasCase) {
                 // case value provided and we're going into "else"; consume it.
                 context.consumeCurrentValue();
             }
             compile(node, context);
             return;
         }
-        
-        WhenNode whenNode = (WhenNode)node;
-        
+
+        WhenNode whenNode = (WhenNode) node;
+
         if (whenNode.getExpressionNodes() instanceof ArrayNode) {
-            ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
-            
+            ArrayNode arrayNode = (ArrayNode) whenNode.getExpressionNodes();
+
             compileMultiArgWhen(whenNode, arrayNode, 0, context, hasCase);
         } else {
             if (hasCase) {
                 context.duplicateCurrentValue();
             }
-            
+
             // evaluate the when argument
             compile(whenNode.getExpressionNodes(), context);
 
             final WhenNode currentWhen = whenNode;
 
             if (hasCase) {
                 // we have a case value, call === on the condition value passing the case value
                 context.swapValues();
                 context.createObjectArray(1);
                 context.getInvocationCompiler().invokeEqq();
             }
 
             // check if the condition result is true, branch appropriately
             BranchCallback trueBranch = new BranchCallback() {
-                public void branch(MethodCompiler context) {
-                    // consume extra case value, we won't need it anymore
-                    if (hasCase) {
-                        context.consumeCurrentValue();
-                    }
 
-                    if (currentWhen.getBodyNode() != null) {
-                        ASTCompiler.compile(currentWhen.getBodyNode(), context);
-                    } else {
-                        context.loadNil();
-                    }
-                }
-            };
+                        public void branch(MethodCompiler context) {
+                            // consume extra case value, we won't need it anymore
+                            if (hasCase) {
+                                context.consumeCurrentValue();
+                            }
+
+                            if (currentWhen.getBodyNode() != null) {
+                                ASTCompiler.compile(currentWhen.getBodyNode(), context);
+                            } else {
+                                context.loadNil();
+                            }
+                        }
+                    };
 
             BranchCallback falseBranch = new BranchCallback() {
-                public void branch(MethodCompiler context) {
-                    // proceed to the next when
-                    ASTCompiler.compileWhen(currentWhen.getNextCase(), context, hasCase);
-                }
-            };
+
+                        public void branch(MethodCompiler context) {
+                            // proceed to the next when
+                            ASTCompiler.compileWhen(currentWhen.getNextCase(), context, hasCase);
+                        }
+                    };
 
             context.performBooleanBranch(trueBranch, falseBranch);
         }
     }
-    
-    public static void compileMultiArgWhen(
-            final WhenNode whenNode, final ArrayNode expressionsNode, final int conditionIndex, MethodCompiler context, final boolean hasCase) {
-        
+
+    public static void compileMultiArgWhen(final WhenNode whenNode, final ArrayNode expressionsNode, final int conditionIndex, MethodCompiler context, final boolean hasCase) {
+
         if (conditionIndex >= expressionsNode.size()) {
             // done with conditions, continue to next when in the chain
             compileWhen(whenNode.getNextCase(), context, hasCase);
             return;
         }
-        
+
         Node tag = expressionsNode.get(conditionIndex);
 
         // need to add in position stuff some day :)
         context.setPosition(tag.getPosition());
 
         // reduce the when cases to a true or false ruby value for the branch below
         if (tag instanceof WhenNode) {
             // prepare to handle the when logic
             if (hasCase) {
                 context.duplicateCurrentValue();
             } else {
                 context.loadNull();
             }
-            compile(((WhenNode)tag).getExpressionNodes(), context);
+            compile(((WhenNode) tag).getExpressionNodes(), context);
             context.checkWhenWithSplat();
         } else {
             if (hasCase) {
                 context.duplicateCurrentValue();
             }
-            
+
             // evaluate the when argument
             compile(tag, context);
 
             if (hasCase) {
                 // we have a case value, call === on the condition value passing the case value
                 context.swapValues();
                 context.createObjectArray(1);
                 context.getInvocationCompiler().invokeEqq();
             }
         }
 
         // check if the condition result is true, branch appropriately
         BranchCallback trueBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                // consume extra case value, we won't need it anymore
-                if (hasCase) {
-                    context.consumeCurrentValue();
-                }
 
-                if (whenNode.getBodyNode() != null) {
-                    ASTCompiler.compile(whenNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
+                    public void branch(MethodCompiler context) {
+                        // consume extra case value, we won't need it anymore
+                        if (hasCase) {
+                            context.consumeCurrentValue();
+                        }
+
+                        if (whenNode.getBodyNode() != null) {
+                            ASTCompiler.compile(whenNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
 
         BranchCallback falseBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                // proceed to the next when
-                ASTCompiler.compileMultiArgWhen(whenNode, expressionsNode, conditionIndex + 1, context, hasCase);
-            }
-        };
+
+                    public void branch(MethodCompiler context) {
+                        // proceed to the next when
+                        ASTCompiler.compileMultiArgWhen(whenNode, expressionsNode, conditionIndex + 1, context, hasCase);
+                    }
+                };
 
         context.performBooleanBranch(trueBranch, falseBranch);
     }
-    
+
     public static void compileClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final ClassNode classNode = (ClassNode)node;
-        
+
+        final ClassNode classNode = (ClassNode) node;
+
         final Node superNode = classNode.getSuperNode();
-        
+
         final Node cpathNode = classNode.getCPath();
-        
+
         ClosureCallback superCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(superNode, context);
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(superNode, context);
+                    }
+                };
         if (superNode == null) {
             superCallback = null;
         }
-        
+
         ClosureCallback bodyCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (classNode.getBodyNode() != null) {
-                    ASTCompiler.compile(classNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (classNode.getBodyNode() != null) {
+                            ASTCompiler.compile(classNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         ClosureCallback pathCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (cpathNode instanceof Colon2Node) {
-                    Node leftNode = ((Colon2Node)cpathNode).getLeftNode();
-                    if (leftNode != null) {
-                        ASTCompiler.compile(leftNode, context);
-                    } else {
-                        context.loadNil();
+
+                    public void compile(MethodCompiler context) {
+                        if (cpathNode instanceof Colon2Node) {
+                            Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
+                            if (leftNode != null) {
+                                ASTCompiler.compile(leftNode, context);
+                            } else {
+                                context.loadNil();
+                            }
+                        } else if (cpathNode instanceof Colon3Node) {
+                            context.loadObject();
+                        } else {
+                            context.loadNil();
+                        }
                     }
-                } else if (cpathNode instanceof Colon3Node) {
-                    context.loadObject();
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+                };
+
         context.defineClass(classNode.getCPath().getName(), classNode.getScope(), superCallback, pathCallback, bodyCallback, null);
     }
-    
+
     public static void compileSClass(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final SClassNode sclassNode = (SClassNode)node;
-        
+
+        final SClassNode sclassNode = (SClassNode) node;
+
         ClosureCallback receiverCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(sclassNode.getReceiverNode(), context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(sclassNode.getReceiverNode(), context);
+                    }
+                };
+
         ClosureCallback bodyCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (sclassNode.getBodyNode() != null) {
-                    ASTCompiler.compile(sclassNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (sclassNode.getBodyNode() != null) {
+                            ASTCompiler.compile(sclassNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         context.defineClass("SCLASS", sclassNode.getScope(), null, null, bodyCallback, receiverCallback);
     }
 
     public static void compileClassVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ClassVarNode classVarNode = (ClassVarNode)node;
-        
+
+        ClassVarNode classVarNode = (ClassVarNode) node;
+
         context.retrieveClassVariable(classVarNode.getName());
     }
 
     public static void compileClassVarAsgn(Node node, MethodCompiler context) {
-        ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode)node;
-        
+        ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
+
         // FIXME: probably more efficient with a callback
         compile(classVarAsgnNode.getValueNode(), context);
-        
+
         compileClassVarAsgnAssignment(node, context);
     }
 
     public static void compileClassVarAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode)node;
-        
+
+        ClassVarAsgnNode classVarAsgnNode = (ClassVarAsgnNode) node;
+
         context.assignClassVariable(classVarAsgnNode.getName());
     }
 
     public static void compileClassVarDecl(Node node, MethodCompiler context) {
-        ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode)node;
-        
+        ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
+
         // FIXME: probably more efficient with a callback
         compile(classVarDeclNode.getValueNode(), context);
-        
+
         compileClassVarDeclAssignment(node, context);
     }
 
     public static void compileClassVarDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode)node;
-        
+
+        ClassVarDeclNode classVarDeclNode = (ClassVarDeclNode) node;
+
         context.declareClassVariable(classVarDeclNode.getName());
     }
-    
+
     public static void compileConstDecl(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ConstDeclNode constDeclNode = (ConstDeclNode)node;
+
+        ConstDeclNode constDeclNode = (ConstDeclNode) node;
         Node constNode = constDeclNode.getConstNode();
-        
+
         if (constDeclNode.getConstNode() == null) {
             compile(constDeclNode.getValueNode(), context);
-        
+
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constNode.nodeId == NodeType.COLON2NODE) {
-            compile(((Colon2Node)constNode).getLeftNode(), context);
+            compile(((Colon2Node) constNode).getLeftNode(), context);
             compile(constDeclNode.getValueNode(), context);
-            
+
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             compile(constDeclNode.getValueNode(), context);
-            
+
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
-    
+
     public static void compileConstDeclAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ConstDeclNode constDeclNode = (ConstDeclNode)node;
-        
+
+        ConstDeclNode constDeclNode = (ConstDeclNode) node;
+
         if (constDeclNode.getConstNode() == null) {
             context.assignConstantInCurrent(constDeclNode.getName());
         } else if (constDeclNode.nodeId == NodeType.COLON2NODE) {
             context.assignConstantInModule(constDeclNode.getName());
         } else {// colon3, assign in Object
             context.assignConstantInObject(constDeclNode.getName());
         }
     }
 
     public static void compileConst(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ConstNode constNode = (ConstNode)node;
-        
+
+        ConstNode constNode = (ConstNode) node;
+
         context.retrieveConstant(constNode.getName());
     }
-    
+
     public static void compileColon2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         final Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
-        if(leftNode == null) {
+        if (leftNode == null) {
             context.loadObject();
             context.retrieveConstantFromModule(name);
         } else {
             final ClosureCallback receiverCallback = new ClosureCallback() {
-                public void compile(MethodCompiler context) {
-                    ASTCompiler.compile(iVisited.getLeftNode(), context);
-                }
-            };
-            
+
+                        public void compile(MethodCompiler context) {
+                            ASTCompiler.compile(iVisited.getLeftNode(), context);
+                        }
+                    };
+
             BranchCallback moduleCallback = new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        receiverCallback.compile(context);
-                        context.retrieveConstantFromModule(name);
-                    }
-                };
+
+                        public void branch(MethodCompiler context) {
+                            receiverCallback.compile(context);
+                            context.retrieveConstantFromModule(name);
+                        }
+                    };
 
             BranchCallback notModuleCallback = new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
-                    }
-                };
+
+                        public void branch(MethodCompiler context) {
+                            context.getInvocationCompiler().invokeDynamic(name, receiverCallback, null, CallType.FUNCTIONAL, null, false);
+                        }
+                    };
 
             context.branchIfModule(receiverCallback, moduleCallback, notModuleCallback);
         }
     }
-    
+
     public static void compileColon3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         Colon3Node iVisited = (Colon3Node) node;
         String name = iVisited.getName();
 
         context.loadObject();
         context.retrieveConstantFromModule(name);
     }
-    
+
     public static void compileGetDefinitionBase(final Node node, MethodCompiler context) {
         BranchCallback reg = new BranchCallback() {
-                public void branch(MethodCompiler context) {
-                    context.inDefined();
-                    compileGetDefinition(node, context);
-                }
-            };
+
+                    public void branch(MethodCompiler context) {
+                        context.inDefined();
+                        compileGetDefinition(node, context);
+                    }
+                };
         BranchCallback out = new BranchCallback() {
-                public void branch(MethodCompiler context) {
-                    context.outDefined();
-                }
-            };
-        context.protect(reg,out,String.class);
+
+                    public void branch(MethodCompiler context) {
+                        context.outDefined();
+                    }
+                };
+        context.protect(reg, out, String.class);
     }
 
     public static void compileDefined(final Node node, MethodCompiler context) {
-        compileGetDefinitionBase(((DefinedNode)node).getExpressionNode(), context);
+        compileGetDefinitionBase(((DefinedNode) node).getExpressionNode(), context);
         context.stringOrNil();
     }
 
     public static void compileGetArgumentDefinition(final Node node, MethodCompiler context, String type) {
         if (node == null) {
             context.pushString(type);
-        } else if(node instanceof ArrayNode) {
+        } else if (node instanceof ArrayNode) {
             Object endToken = context.getNewEnding();
-            for (int i = 0; i < ((ArrayNode)node).size(); i++) {
-                Node iterNode = ((ArrayNode)node).get(i);
+            for (int i = 0; i < ((ArrayNode) node).size(); i++) {
+                Node iterNode = ((ArrayNode) node).get(i);
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
 
     public static void compileGetDefinition(final Node node, MethodCompiler context) {
-        switch(node.nodeId) {
-        case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
-        case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE:
-        case MULTIPLEASGNNODE: case OPASGNNODE: case OPELEMENTASGNNODE:
-            context.pushString("assignment");
-            break;
-        case BACKREFNODE:
-            context.backref();
-            context.isInstanceOf(RubyMatchData.class, 
-                                 new BranchCallback(){
-                public void branch(MethodCompiler context) {
-                    context.pushString("$" + ((BackRefNode) node).getType());
-                }},
-                                 new BranchCallback(){
-                                     public void branch(MethodCompiler context) {
-                                         context.pushNull();
-                                     }});
-            break;
-        case DVARNODE:
-            context.pushString("local-variable(in-block)");
-            break;
-        case FALSENODE:
-            context.pushString("false");
-            break;
-        case TRUENODE:
-            context.pushString("true");
-            break;
-        case LOCALVARNODE:
-            context.pushString("local-variable");
-            break;
-        case MATCH2NODE: case MATCH3NODE:
-            context.pushString("method");
-            break;
-        case NILNODE:
-            context.pushString("nil");
-            break;
-        case NTHREFNODE:
-            context.isCaptured(((NthRefNode) node).getMatchNumber(),
-                               new BranchCallback(){
-                                   public void branch(MethodCompiler context) {
-                                       context.pushString("$" + ((NthRefNode) node).getMatchNumber());
-                                   }},
-                               new BranchCallback(){
-                                   public void branch(MethodCompiler context) {
-                                       context.pushNull();
-                                   }});
-            break;
-        case SELFNODE:
-            context.pushString("self");
-            break;
-        case VCALLNODE:
-            context.loadSelf();
-            context.isMethodBound(((VCallNode)node).getName(),
-                                  new BranchCallback(){
-                                      public void branch(MethodCompiler context){
-                                          context.pushString("method");
-                                      }
-                                  },
-                                  new BranchCallback(){
-                                      public void branch(MethodCompiler context){
-                                          context.pushNull();
-                                      }
-                                  });
-            break;
-        case YIELDNODE:
-            context.hasBlock(new BranchCallback(){
-                    public void branch(MethodCompiler context){
-                        context.pushString("yield");
-                    }
-                },
-                new BranchCallback(){
-                    public void branch(MethodCompiler context){
-                        context.pushNull();
-                    }
-                });
-            break;
-        case GLOBALVARNODE:
-            context.isGlobalDefined(((GlobalVarNode) node).getName(),
-                                    new BranchCallback(){
-                                        public void branch(MethodCompiler context){
-                                            context.pushString("global-variable");
-                                        }
-                                    },
-                                    new BranchCallback(){
-                                        public void branch(MethodCompiler context){
-                                            context.pushNull();
-                                        }
-                                    });
-            break;
-        case INSTVARNODE:
-            context.isInstanceVariableDefined(((InstVarNode) node).getName(),
-                                              new BranchCallback(){
-                                                  public void branch(MethodCompiler context){
-                                                      context.pushString("instance-variable");
-                                                  }
-                                              },
-                                              new BranchCallback(){
-                                                  public void branch(MethodCompiler context){
-                                                      context.pushNull();
-                                                  }
-                                              });
-            break;
-        case CONSTNODE:
-            context.isConstantDefined(((ConstNode) node).getName(),
-                                      new BranchCallback(){
-                                          public void branch(MethodCompiler context){
-                                              context.pushString("constant");
-                                          }
-                                      },
-                                      new BranchCallback(){
-                                          public void branch(MethodCompiler context){
-                                              context.pushNull();
-                                          }
-                                      });
-            break;
-        case FCALLNODE:
-            context.loadSelf();
-            context.isMethodBound(((FCallNode)node).getName(),
-                                  new BranchCallback(){
-                                      public void branch(MethodCompiler context){
-                                          compileGetArgumentDefinition(((FCallNode)node).getArgsNode(), context, "method");
-                                      }
-                                  },
-                                  new BranchCallback(){
-                                      public void branch(MethodCompiler context){
-                                          context.pushNull();
-                                      }
-                                  });
-            break;
-        case COLON3NODE:
-        case COLON2NODE: {
-            final Colon3Node iVisited = (Colon3Node) node;
-
-            final String name = iVisited.getName();
-
-            BranchCallback setup = new BranchCallback() {
-                    public void branch(MethodCompiler context){
-                        if(iVisited instanceof Colon2Node) {
-                            final Node leftNode = ((Colon2Node)iVisited).getLeftNode();
-                            ASTCompiler.compile(leftNode, context);
-                        } else {
-                            context.loadObject();
-                        }
-                    }
-                };
-            BranchCallback isConstant = new BranchCallback() {
-                    public void branch(MethodCompiler context){
-                        context.pushString("constant");
-                    }
-                };
-            BranchCallback isMethod = new BranchCallback() {
-                    public void branch(MethodCompiler context){
-                        context.pushString("method");
-                    }
-                };
-            BranchCallback none = new BranchCallback() {
-                    public void branch(MethodCompiler context){
-                        context.pushNull();
-                    }
-                };
-            context.isConstantBranch(setup, isConstant, isMethod, none, name);
-            break;
-        }
-        case CALLNODE: {
-            final CallNode iVisited = (CallNode) node;
-            Object isnull = context.getNewEnding();
-            Object ending = context.getNewEnding();
-            ASTCompiler.compileGetDefinition(iVisited.getReceiverNode(), context);
-            context.ifNull(isnull);
-
-            context.rescue(new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        ASTCompiler.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
-                        context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
-                        context.metaclass(); //[IRubyObject, RubyClass]
-                        context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
-                        context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
-                        context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
-                        final Object isfalse = context.getNewEnding();
-                        Object isreal = context.getNewEnding();
-                        Object ending = context.getNewEnding();
-                        context.isPrivate(isfalse,3); //[IRubyObject, RubyClass, Visibility]
-                        context.isNotProtected(isreal,1); //[IRubyObject, RubyClass]
-                        context.selfIsKindOf(isreal); //[IRubyObject]
-                        context.consumeCurrentValue();
-                        context.go(isfalse);
-                        context.setEnding(isreal); //[]
-                        
-                        context.isMethodBound(iVisited.getName(), new BranchCallback(){
+        switch (node.nodeId) {
+            case CLASSVARASGNNODE:
+            case CLASSVARDECLNODE:
+            case CONSTDECLNODE:
+            case DASGNNODE:
+            case GLOBALASGNNODE:
+            case LOCALASGNNODE:
+            case MULTIPLEASGNNODE:
+            case OPASGNNODE:
+            case OPELEMENTASGNNODE:
+                context.pushString("assignment");
+                break;
+            case BACKREFNODE:
+                context.backref();
+                context.isInstanceOf(RubyMatchData.class,
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("$" + ((BackRefNode) node).getType());
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case DVARNODE:
+                context.pushString("local-variable(in-block)");
+                break;
+            case FALSENODE:
+                context.pushString("false");
+                break;
+            case TRUENODE:
+                context.pushString("true");
+                break;
+            case LOCALVARNODE:
+                context.pushString("local-variable");
+                break;
+            case MATCH2NODE:
+            case MATCH3NODE:
+                context.pushString("method");
+                break;
+            case NILNODE:
+                context.pushString("nil");
+                break;
+            case NTHREFNODE:
+                context.isCaptured(((NthRefNode) node).getMatchNumber(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("$" + ((NthRefNode) node).getMatchNumber());
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case SELFNODE:
+                context.pushString("self");
+                break;
+            case VCALLNODE:
+                context.loadSelf();
+                context.isMethodBound(((VCallNode) node).getName(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("method");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case YIELDNODE:
+                context.hasBlock(new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("yield");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case GLOBALVARNODE:
+                context.isGlobalDefined(((GlobalVarNode) node).getName(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("global-variable");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case INSTVARNODE:
+                context.isInstanceVariableDefined(((InstVarNode) node).getName(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("instance-variable");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case CONSTNODE:
+                context.isConstantDefined(((ConstNode) node).getName(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushString("constant");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case FCALLNODE:
+                context.loadSelf();
+                context.isMethodBound(((FCallNode) node).getName(),
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                compileGetArgumentDefinition(((FCallNode) node).getArgsNode(), context, "method");
+                            }
+                        },
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        });
+                break;
+            case COLON3NODE:
+            case COLON2NODE:
+                {
+                    final Colon3Node iVisited = (Colon3Node) node;
+
+                    final String name = iVisited.getName();
+
+                    BranchCallback setup = new BranchCallback() {
+
                                 public void branch(MethodCompiler context) {
-                                    compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
+                                    if (iVisited instanceof Colon2Node) {
+                                        final Node leftNode = ((Colon2Node) iVisited).getLeftNode();
+                                        ASTCompiler.compile(leftNode, context);
+                                    } else {
+                                        context.loadObject();
+                                    }
                                 }
-                            }, 
-                            new BranchCallback(){
-                                public void branch(MethodCompiler context) { 
-                                    context.go(isfalse); 
-                                }
-                            });
-                        context.go(ending);
-                        context.setEnding(isfalse);
-                        context.pushNull();
-                        context.setEnding(ending);
-                    }}, JumpException.class,
-                new BranchCallback() {
-                        public void branch(MethodCompiler context) {
-                            context.pushNull();
-                        }}, String.class);
-
-            //          context.swapValues();
-            //context.consumeCurrentValue();
-            context.go(ending);
-            context.setEnding(isnull);            
-            context.pushNull();
-            context.setEnding(ending);
-            break;
-        }
-        case CLASSVARNODE: {
-            ClassVarNode iVisited = (ClassVarNode) node;
-            final Object ending = context.getNewEnding();
-            final Object failure = context.getNewEnding();
-            final Object singleton = context.getNewEnding();
-            Object second = context.getNewEnding();
-            Object third = context.getNewEnding();
-            
-            context.loadCurrentModule(); //[RubyClass]
-            context.duplicateCurrentValue(); //[RubyClass, RubyClass]
-            context.ifNotNull(second); //[RubyClass]
-            context.consumeCurrentValue(); //[]
-            context.loadSelf(); //[self]
-            context.metaclass(); //[RubyClass]
-            context.duplicateCurrentValue(); //[RubyClass, RubyClass]
-            context.isClassVarDefined(iVisited.getName(),                 
-                                      new BranchCallback() {
-                                          public void branch(MethodCompiler context) {
-                                              context.consumeCurrentValue();
-                                              context.pushString("class variable");
-                                              context.go(ending);
-                                          }},
-                                      new BranchCallback() {
-                                          public void branch(MethodCompiler context) {}});
-            context.setEnding(second);  //[RubyClass]
-            context.duplicateCurrentValue();
-            context.isClassVarDefined(iVisited.getName(),
-                                      new BranchCallback() {
-                                          public void branch(MethodCompiler context) {
-                                              context.consumeCurrentValue();
-                                              context.pushString("class variable");
-                                              context.go(ending);
-                                          }},
-                                      new BranchCallback() {
-                                          public void branch(MethodCompiler context) {
-                                          }});
-            context.setEnding(third); //[RubyClass]
-            context.duplicateCurrentValue(); //[RubyClass, RubyClass]
-            context.ifSingleton(singleton); //[RubyClass]
-            context.consumeCurrentValue();//[]
-            context.go(failure);
-            context.setEnding(singleton);
-            context.attached();//[RubyClass]
-            context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
-            context.pushString("class variable");
-            context.go(ending);
-            context.setEnding(failure);
-            context.pushNull();
-            context.setEnding(ending);
-        }
-            break;
-        case ZSUPERNODE: {
-            Object fail = context.getNewEnding();
-            Object fail2 = context.getNewEnding();
-            Object fail_easy = context.getNewEnding();
-            Object ending = context.getNewEnding();
-
-            context.getFrameName(); //[String]
-            context.duplicateCurrentValue(); //[String, String]
-            context.ifNull(fail); //[String]
-            context.getFrameKlazz(); //[String, RubyClass]
-            context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
-            context.ifNull(fail2); //[String, RubyClass]
-            context.superClass();
-            context.ifNotSuperMethodBound(fail_easy);
-
-            context.pushString("super");
-            context.go(ending);
-            
-            context.setEnding(fail2);
-            context.consumeCurrentValue();
-            context.setEnding(fail);
-            context.consumeCurrentValue();
-            context.setEnding(fail_easy);
-            context.pushNull();
-            context.setEnding(ending);
-        }
-            break;
-        case SUPERNODE: {
-            Object fail = context.getNewEnding();
-            Object fail2 = context.getNewEnding();
-            Object fail_easy = context.getNewEnding();
-            Object ending = context.getNewEnding();
-
-            context.getFrameName(); //[String]
-            context.duplicateCurrentValue(); //[String, String]
-            context.ifNull(fail); //[String]
-            context.getFrameKlazz(); //[String, RubyClass]
-            context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
-            context.ifNull(fail2); //[String, RubyClass]
-            context.superClass();
-            context.ifNotSuperMethodBound(fail_easy);
-
-            compileGetArgumentDefinition(((SuperNode)node).getArgsNode(), context, "super");
-            context.go(ending);
-            
-            context.setEnding(fail2);
-            context.consumeCurrentValue();
-            context.setEnding(fail);
-            context.consumeCurrentValue();
-            context.setEnding(fail_easy);
-            context.pushNull();
-            context.setEnding(ending);
-            break;
-        }
-        case ATTRASSIGNNODE: {
-            final AttrAssignNode iVisited = (AttrAssignNode) node;
-            Object isnull = context.getNewEnding();
-            Object ending = context.getNewEnding();
-            ASTCompiler.compileGetDefinition(iVisited.getReceiverNode(), context);
-            context.ifNull(isnull);
-
-            context.rescue(new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        ASTCompiler.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
-                        context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
-                        context.metaclass(); //[IRubyObject, RubyClass]
-                        context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
-                        context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
-                        context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
-                        final Object isfalse = context.getNewEnding();
-                        Object isreal = context.getNewEnding();
-                        Object ending = context.getNewEnding();
-                        context.isPrivate(isfalse,3); //[IRubyObject, RubyClass, Visibility]
-                        context.isNotProtected(isreal,1); //[IRubyObject, RubyClass]
-                        context.selfIsKindOf(isreal); //[IRubyObject]
-                        context.consumeCurrentValue();
-                        context.go(isfalse);
-                        context.setEnding(isreal); //[]
+                            };
+                    BranchCallback isConstant = new BranchCallback() {
 
-                        context.isMethodBound(iVisited.getName(), new BranchCallback(){
                                 public void branch(MethodCompiler context) {
-                                    compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
+                                    context.pushString("constant");
                                 }
-                            }, 
-                            new BranchCallback(){
-                                public void branch(MethodCompiler context) { 
-                                    context.go(isfalse); 
+                            };
+                    BranchCallback isMethod = new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    context.pushString("method");
+                                }
+                            };
+                    BranchCallback none = new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    context.pushNull();
+                                }
+                            };
+                    context.isConstantBranch(setup, isConstant, isMethod, none, name);
+                    break;
+                }
+            case CALLNODE:
+                {
+                    final CallNode iVisited = (CallNode) node;
+                    Object isnull = context.getNewEnding();
+                    Object ending = context.getNewEnding();
+                    ASTCompiler.compileGetDefinition(iVisited.getReceiverNode(), context);
+                    context.ifNull(isnull);
+
+                    context.rescue(new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    ASTCompiler.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
+                                    context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
+                                    context.metaclass(); //[IRubyObject, RubyClass]
+                                    context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
+                                    context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
+                                    context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
+                                    final Object isfalse = context.getNewEnding();
+                                    Object isreal = context.getNewEnding();
+                                    Object ending = context.getNewEnding();
+                                    context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
+                                    context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
+                                    context.selfIsKindOf(isreal); //[IRubyObject]
+                                    context.consumeCurrentValue();
+                                    context.go(isfalse);
+                                    context.setEnding(isreal); //[]
+
+                                    context.isMethodBound(iVisited.getName(), new BranchCallback() {
+
+                                                public void branch(MethodCompiler context) {
+                                                    compileGetArgumentDefinition(iVisited.getArgsNode(), context, "method");
+                                                }
+                                            },
+                                            new BranchCallback() {
+
+                                                public void branch(MethodCompiler context) {
+                                                    context.go(isfalse);
+                                                }
+                                            });
+                                    context.go(ending);
+                                    context.setEnding(isfalse);
+                                    context.pushNull();
+                                    context.setEnding(ending);
+                                }
+                            }, JumpException.class,
+                            new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    context.pushNull();
+                                }
+                            }, String.class);
+
+                    //          context.swapValues();
+            //context.consumeCurrentValue();
+                    context.go(ending);
+                    context.setEnding(isnull);
+                    context.pushNull();
+                    context.setEnding(ending);
+                    break;
+                }
+            case CLASSVARNODE:
+                {
+                    ClassVarNode iVisited = (ClassVarNode) node;
+                    final Object ending = context.getNewEnding();
+                    final Object failure = context.getNewEnding();
+                    final Object singleton = context.getNewEnding();
+                    Object second = context.getNewEnding();
+                    Object third = context.getNewEnding();
+
+                    context.loadCurrentModule(); //[RubyClass]
+                    context.duplicateCurrentValue(); //[RubyClass, RubyClass]
+                    context.ifNotNull(second); //[RubyClass]
+                    context.consumeCurrentValue(); //[]
+                    context.loadSelf(); //[self]
+                    context.metaclass(); //[RubyClass]
+                    context.duplicateCurrentValue(); //[RubyClass, RubyClass]
+                    context.isClassVarDefined(iVisited.getName(),
+                            new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    context.consumeCurrentValue();
+                                    context.pushString("class variable");
+                                    context.go(ending);
+                                }
+                            },
+                            new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
                                 }
                             });
-                        context.go(ending);
-                        context.setEnding(isfalse);
-                        context.pushNull();
-                        context.setEnding(ending);
-                    }}, JumpException.class,
-                new BranchCallback() {
-                        public void branch(MethodCompiler context) {
-                            context.pushNull();
-                        }}, String.class);
+                    context.setEnding(second);  //[RubyClass]
+                    context.duplicateCurrentValue();
+                    context.isClassVarDefined(iVisited.getName(),
+                            new BranchCallback() {
 
-            context.go(ending);
-            context.setEnding(isnull);            
-            context.pushNull();
-            context.setEnding(ending);
-            break;
+                                public void branch(MethodCompiler context) {
+                                    context.consumeCurrentValue();
+                                    context.pushString("class variable");
+                                    context.go(ending);
+                                }
+                            },
+                            new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                }
+                            });
+                    context.setEnding(third); //[RubyClass]
+                    context.duplicateCurrentValue(); //[RubyClass, RubyClass]
+                    context.ifSingleton(singleton); //[RubyClass]
+                    context.consumeCurrentValue();//[]
+                    context.go(failure);
+                    context.setEnding(singleton);
+                    context.attached();//[RubyClass]
+                    context.notIsModuleAndClassVarDefined(iVisited.getName(), failure); //[]
+                    context.pushString("class variable");
+                    context.go(ending);
+                    context.setEnding(failure);
+                    context.pushNull();
+                    context.setEnding(ending);
+                }
+                break;
+            case ZSUPERNODE:
+                {
+                    Object fail = context.getNewEnding();
+                    Object fail2 = context.getNewEnding();
+                    Object fail_easy = context.getNewEnding();
+                    Object ending = context.getNewEnding();
+
+                    context.getFrameName(); //[String]
+                    context.duplicateCurrentValue(); //[String, String]
+                    context.ifNull(fail); //[String]
+                    context.getFrameKlazz(); //[String, RubyClass]
+                    context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
+                    context.ifNull(fail2); //[String, RubyClass]
+                    context.superClass();
+                    context.ifNotSuperMethodBound(fail_easy);
+
+                    context.pushString("super");
+                    context.go(ending);
+
+                    context.setEnding(fail2);
+                    context.consumeCurrentValue();
+                    context.setEnding(fail);
+                    context.consumeCurrentValue();
+                    context.setEnding(fail_easy);
+                    context.pushNull();
+                    context.setEnding(ending);
+                }
+                break;
+            case SUPERNODE:
+                {
+                    Object fail = context.getNewEnding();
+                    Object fail2 = context.getNewEnding();
+                    Object fail_easy = context.getNewEnding();
+                    Object ending = context.getNewEnding();
+
+                    context.getFrameName(); //[String]
+                    context.duplicateCurrentValue(); //[String, String]
+                    context.ifNull(fail); //[String]
+                    context.getFrameKlazz(); //[String, RubyClass]
+                    context.duplicateCurrentValue(); //[String, RubyClass, RubyClass]
+                    context.ifNull(fail2); //[String, RubyClass]
+                    context.superClass();
+                    context.ifNotSuperMethodBound(fail_easy);
+
+                    compileGetArgumentDefinition(((SuperNode) node).getArgsNode(), context, "super");
+                    context.go(ending);
+
+                    context.setEnding(fail2);
+                    context.consumeCurrentValue();
+                    context.setEnding(fail);
+                    context.consumeCurrentValue();
+                    context.setEnding(fail_easy);
+                    context.pushNull();
+                    context.setEnding(ending);
+                    break;
+                }
+            case ATTRASSIGNNODE:
+                {
+                    final AttrAssignNode iVisited = (AttrAssignNode) node;
+                    Object isnull = context.getNewEnding();
+                    Object ending = context.getNewEnding();
+                    ASTCompiler.compileGetDefinition(iVisited.getReceiverNode(), context);
+                    context.ifNull(isnull);
+
+                    context.rescue(new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    ASTCompiler.compile(iVisited.getReceiverNode(), context); //[IRubyObject]
+                                    context.duplicateCurrentValue(); //[IRubyObject, IRubyObject]
+                                    context.metaclass(); //[IRubyObject, RubyClass]
+                                    context.duplicateCurrentValue(); //[IRubyObject, RubyClass, RubyClass]
+                                    context.getVisibilityFor(iVisited.getName()); //[IRubyObject, RubyClass, Visibility]
+                                    context.duplicateCurrentValue(); //[IRubyObject, RubyClass, Visibility, Visibility]
+                                    final Object isfalse = context.getNewEnding();
+                                    Object isreal = context.getNewEnding();
+                                    Object ending = context.getNewEnding();
+                                    context.isPrivate(isfalse, 3); //[IRubyObject, RubyClass, Visibility]
+                                    context.isNotProtected(isreal, 1); //[IRubyObject, RubyClass]
+                                    context.selfIsKindOf(isreal); //[IRubyObject]
+                                    context.consumeCurrentValue();
+                                    context.go(isfalse);
+                                    context.setEnding(isreal); //[]
+
+                                    context.isMethodBound(iVisited.getName(), new BranchCallback() {
+
+                                                public void branch(MethodCompiler context) {
+                                                    compileGetArgumentDefinition(iVisited.getArgsNode(), context, "assignment");
+                                                }
+                                            },
+                                            new BranchCallback() {
+
+                                                public void branch(MethodCompiler context) {
+                                                    context.go(isfalse);
+                                                }
+                                            });
+                                    context.go(ending);
+                                    context.setEnding(isfalse);
+                                    context.pushNull();
+                                    context.setEnding(ending);
+                                }
+                            }, JumpException.class,
+                            new BranchCallback() {
+
+                                public void branch(MethodCompiler context) {
+                                    context.pushNull();
+                                }
+                            }, String.class);
+
+                    context.go(ending);
+                    context.setEnding(isnull);
+                    context.pushNull();
+                    context.setEnding(ending);
+                    break;
+                }
+            default:
+                context.rescue(new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                ASTCompiler.compile(node, context);
+                                context.consumeCurrentValue();
+                                context.pushNull();
+                            }
+                        }, JumpException.class,
+                        new BranchCallback() {
+
+                            public void branch(MethodCompiler context) {
+                                context.pushNull();
+                            }
+                        }, String.class);
+                context.consumeCurrentValue();
+                context.pushString("expression");
         }
-        default:
-            context.rescue(new BranchCallback(){
-                    public void branch(MethodCompiler context){
-                        ASTCompiler.compile(node, context);
-                        context.consumeCurrentValue();
-                        context.pushNull();
-                    }
-                },JumpException.class, 
-                new BranchCallback(){public void branch(MethodCompiler context){context.pushNull();}}, String.class);
-            context.consumeCurrentValue();
-            context.pushString("expression");
-        }        
     }
 
     public static void compileDAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        DAsgnNode dasgnNode = (DAsgnNode)node;
-        
+
+        DAsgnNode dasgnNode = (DAsgnNode) node;
+
         compile(dasgnNode.getValueNode(), context);
-        
+
         compileDAsgnAssignment(dasgnNode, context);
     }
 
     public static void compileDAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        DAsgnNode dasgnNode = (DAsgnNode)node;
-        
+
+        DAsgnNode dasgnNode = (DAsgnNode) node;
+
         context.getVariableCompiler().assignLocalVariable(dasgnNode.getIndex(), dasgnNode.getDepth());
     }
-    
+
     public static void compileDefn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final DefnNode defnNode = (DefnNode)node;
+
+        final DefnNode defnNode = (DefnNode) node;
         final ArgsNode argsNode = defnNode.getArgsNode();
-        
+
         ClosureCallback body = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (defnNode.getBodyNode() != null) {
-                    ASTCompiler.compile(defnNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (defnNode.getBodyNode() != null) {
+                            ASTCompiler.compile(defnNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         ClosureCallback args = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileArgs(argsNode, context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        compileArgs(argsNode, context);
+                    }
+                };
+
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defnNode.getArgsNode());
         inspector.inspect(defnNode.getBodyNode());
-        
+
         context.defineNewMethod(defnNode.getName(), defnNode.getScope(), body, args, null, inspector);
     }
-    
+
     public static void compileDefs(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final DefsNode defsNode = (DefsNode)node;
+
+        final DefsNode defsNode = (DefsNode) node;
         final ArgsNode argsNode = defsNode.getArgsNode();
-        
+
         ClosureCallback receiver = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(defsNode.getReceiverNode(), context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(defsNode.getReceiverNode(), context);
+                    }
+                };
+
         ClosureCallback body = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (defsNode.getBodyNode() != null) {
-                    ASTCompiler.compile(defsNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (defsNode.getBodyNode() != null) {
+                            ASTCompiler.compile(defsNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         ClosureCallback args = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileArgs(argsNode, context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        compileArgs(argsNode, context);
+                    }
+                };
+
         // inspect body and args
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(defsNode.getArgsNode());
         inspector.inspect(defsNode.getBodyNode());
-        
+
         context.defineNewMethod(defsNode.getName(), defsNode.getScope(), body, args, receiver, inspector);
     }
-    
+
     public static void compileArgs(Node node, MethodCompiler context) {
-        final ArgsNode argsNode = (ArgsNode)node;
-        
+        final ArgsNode argsNode = (ArgsNode) node;
+
         final int required = argsNode.getRequiredArgsCount();
         final int opt = argsNode.getOptionalArgsCount();
         final int rest = argsNode.getRestArg();
-        
+
         ArrayCallback requiredAssignment = null;
         ArrayCallback optionalGiven = null;
         ArrayCallback optionalNotGiven = null;
         ClosureCallback restAssignment = null;
         ClosureCallback blockAssignment = null;
-        
+
         if (required > 0) {
             requiredAssignment = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object object, int index) {
-                    // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
-                    context.getVariableCompiler().assignLocalVariable(index);
-                }
-            };
+
+                        public void nextValue(MethodCompiler context, Object object, int index) {
+                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
+                            context.getVariableCompiler().assignLocalVariable(index);
+                        }
+                    };
         }
-        
+
         if (opt > 0) {
             optionalGiven = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object object, int index) {
-                    Node optArg = ((ListNode)object).get(index);
 
-                    compileAssignment(optArg, context);
-                }
-            };
+                        public void nextValue(MethodCompiler context, Object object, int index) {
+                            Node optArg = ((ListNode) object).get(index);
+
+                            compileAssignment(optArg, context);
+                        }
+                    };
             optionalNotGiven = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object object, int index) {
-                    Node optArg = ((ListNode)object).get(index);
 
-                    compile(optArg, context);
-                }
-            };
+                        public void nextValue(MethodCompiler context, Object object, int index) {
+                            Node optArg = ((ListNode) object).get(index);
+
+                            compile(optArg, context);
+                        }
+                    };
         }
-            
+
         if (rest > -1) {
             restAssignment = new ClosureCallback() {
-                public void compile(MethodCompiler context) {
-                    context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg());
-                }
-            };
+
+                        public void compile(MethodCompiler context) {
+                            context.getVariableCompiler().assignLocalVariable(argsNode.getRestArg());
+                        }
+                    };
         }
-        
+
         if (argsNode.getBlockArgNode() != null) {
             blockAssignment = new ClosureCallback() {
-                public void compile(MethodCompiler context) {
-                    context.getVariableCompiler().assignLocalVariable(argsNode.getBlockArgNode().getCount());
-                }
-            };
+
+                        public void compile(MethodCompiler context) {
+                            context.getVariableCompiler().assignLocalVariable(argsNode.getBlockArgNode().getCount());
+                        }
+                    };
         }
 
         context.lineNumber(argsNode.getPosition());
-        
+
         context.getVariableCompiler().checkMethodArity(required, opt, rest);
-        context.getVariableCompiler().assignMethodArguments(
-                argsNode.getArgs(),
+        context.getVariableCompiler().assignMethodArguments(argsNode.getArgs(),
                 argsNode.getRequiredArgsCount(),
                 argsNode.getOptArgs(),
                 argsNode.getOptionalArgsCount(),
                 requiredAssignment,
                 optionalGiven,
                 optionalNotGiven,
                 restAssignment,
                 blockAssignment);
     }
-    
+
     public static void compileDot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        DotNode dotNode = (DotNode)node;
+
+        DotNode dotNode = (DotNode) node;
 
         compile(dotNode.getBeginNode(), context);
         compile(dotNode.getEndNode(), context);
-        
+
         context.createNewRange(dotNode.isExclusive());
     }
-    
+
     public static void compileDRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final DRegexpNode dregexpNode = (DRegexpNode)node;
-        
+        final DRegexpNode dregexpNode = (DRegexpNode) node;
+
         ClosureCallback createStringCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ArrayCallback dstrCallback = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object sourceArray,
-                                      int index) {
-                        ASTCompiler.compile(dregexpNode.get(index), context);
+
+                    public void compile(MethodCompiler context) {
+                        ArrayCallback dstrCallback = new ArrayCallback() {
+
+                                    public void nextValue(MethodCompiler context, Object sourceArray,
+                                            int index) {
+                                        ASTCompiler.compile(dregexpNode.get(index), context);
+                                    }
+                                };
+                        context.createNewString(dstrCallback, dregexpNode.size());
+                        context.toJavaString();
                     }
                 };
-                context.createNewString(dstrCallback,dregexpNode.size());
-                context.toJavaString();
-            }
-        };
-   
+
         int opts = dregexpNode.getOptions();
         String lang = ((opts & 16) != 0) ? "n" : null;
-        if((opts & 48) == 48) { // param s
+        if ((opts & 48) == 48) { // param s
             lang = "s";
-        } else if((opts & 32) == 32) { // param e
+        } else if ((opts & 32) == 32) { // param e
             lang = "e";
-        } else if((opts & 64) != 0) { // param s
+        } else if ((opts & 64) != 0) { // param s
             lang = "u";
         }
-        
+
         context.createNewRegexp(createStringCallback, opts, lang);
     }
-    
+
     public static void compileDStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final DStrNode dstrNode = (DStrNode)node;
-        
+        final DStrNode dstrNode = (DStrNode) node;
+
         ArrayCallback dstrCallback = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object sourceArray,
-                                      int index) {
-                    compile(dstrNode.get(index), context);
-                }
-            };
-        context.createNewString(dstrCallback,dstrNode.size());
+
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        compile(dstrNode.get(index), context);
+                    }
+                };
+        context.createNewString(dstrCallback, dstrNode.size());
     }
-    
+
     public static void compileDSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final DSymbolNode dsymbolNode = (DSymbolNode)node;
-        
+        final DSymbolNode dsymbolNode = (DSymbolNode) node;
+
         ArrayCallback dstrCallback = new ArrayCallback() {
-                public void nextValue(MethodCompiler context, Object sourceArray,
-                                      int index) {
-                    compile(dsymbolNode.get(index), context);
-                }
-            };
-        context.createNewSymbol(dstrCallback,dsymbolNode.size());
+
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        compile(dsymbolNode.get(index), context);
+                    }
+                };
+        context.createNewSymbol(dstrCallback, dsymbolNode.size());
     }
-    
+
     public static void compileDVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        DVarNode dvarNode = (DVarNode)node;
-        
+
+        DVarNode dvarNode = (DVarNode) node;
+
         context.getVariableCompiler().retrieveLocalVariable(dvarNode.getIndex(), dvarNode.getDepth());
     }
-    
+
     public static void compileDXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final DXStrNode dxstrNode = (DXStrNode)node;
-        
+        final DXStrNode dxstrNode = (DXStrNode) node;
+
         final ArrayCallback dstrCallback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray,
-                                  int index) {
-                compile(dxstrNode.get(index), context);
-            }
-        };
-        
+
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        compile(dxstrNode.get(index), context);
+                    }
+                };
+
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                context.createNewString(dstrCallback,dxstrNode.size());
-                context.createObjectArray(1);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        context.createNewString(dstrCallback, dxstrNode.size());
+                        context.createObjectArray(1);
+                    }
+                };
+
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
-    
+
     public static void compileEnsureNode(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final EnsureNode ensureNode = (EnsureNode)node;
-        
-        if(ensureNode.getEnsureNode() != null) {
+
+        final EnsureNode ensureNode = (EnsureNode) node;
+
+        if (ensureNode.getEnsureNode() != null) {
             context.protect(new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        if (ensureNode.getBodyNode() != null) {
-                            compile(ensureNode.getBodyNode(), context);
-                        } else {
-                            context.loadNil();
+
+                        public void branch(MethodCompiler context) {
+                            if (ensureNode.getBodyNode() != null) {
+                                compile(ensureNode.getBodyNode(), context);
+                            } else {
+                                context.loadNil();
+                            }
                         }
-                    }
-                },
-                new BranchCallback() {
-                    public void branch(MethodCompiler context) {
-                        compile(ensureNode.getEnsureNode(), context);
-                        context.consumeCurrentValue();
-                    }
-                }, IRubyObject.class);
+                    },
+                    new BranchCallback() {
+
+                        public void branch(MethodCompiler context) {
+                            compile(ensureNode.getEnsureNode(), context);
+                            context.consumeCurrentValue();
+                        }
+                    }, IRubyObject.class);
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
-        
-        final EvStrNode evStrNode = (EvStrNode)node;
-        
+
+        final EvStrNode evStrNode = (EvStrNode) node;
+
         compile(evStrNode.getBody(), context);
         context.asString();
     }
-    
+
     public static void compileFalse(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         context.loadFalse();
 
         context.pollThreadEvents();
     }
-    
+
     public static void compileFCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final FCallNode fcallNode = (FCallNode)node;
-        
+
+        final FCallNode fcallNode = (FCallNode) node;
+
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileArguments(fcallNode.getArgsNode(), context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        compileArguments(fcallNode.getArgsNode(), context);
+                    }
+                };
+
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
-    
+
     private static ClosureCallback getBlock(Node node) {
-        if (node == null) return null;
-        
+        if (node == null) {
+            return null;
+        }
+
         switch (node.nodeId) {
-        case ITERNODE:
-            final IterNode iterNode = (IterNode) node;
+            case ITERNODE:
+                final IterNode iterNode = (IterNode) node;
 
-            return new ClosureCallback() {
-                public void compile(MethodCompiler context) {
-                    ASTCompiler.compile(iterNode, context);
-                }
-            };
-        case BLOCKPASSNODE:
-            final BlockPassNode blockPassNode = (BlockPassNode) node;
-
-            return new ClosureCallback() {
-                public void compile(MethodCompiler context) {
-                    ASTCompiler.compile(blockPassNode.getBodyNode(), context);
-                    context.unwrapPassedBlock();
-                }
-            };
-        default:
-            throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
+                return new ClosureCallback() {
+
+                            public void compile(MethodCompiler context) {
+                                ASTCompiler.compile(iterNode, context);
+                            }
+                        };
+            case BLOCKPASSNODE:
+                final BlockPassNode blockPassNode = (BlockPassNode) node;
+
+                return new ClosureCallback() {
+
+                            public void compile(MethodCompiler context) {
+                                ASTCompiler.compile(blockPassNode.getBodyNode(), context);
+                                context.unwrapPassedBlock();
+                            }
+                        };
+            default:
+                throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public static void compileFixnum(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        FixnumNode fixnumNode = (FixnumNode)node;
-        
+
+        FixnumNode fixnumNode = (FixnumNode) node;
+
         context.createNewFixnum(fixnumNode.getValue());
     }
 
     public static void compileFlip(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final FlipNode flipNode = (FlipNode)node;
-        
+
+        final FlipNode flipNode = (FlipNode) node;
+
         context.getVariableCompiler().retrieveLocalVariable(flipNode.getIndex(), flipNode.getDepth());
-   
+
         if (flipNode.isExclusive()) {
-            context.performBooleanBranch(
-                    new BranchCallback() {
+            context.performBooleanBranch(new BranchCallback() {
+
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
-                            context.performBooleanBranch(
-                                    new BranchCallback() {
+                            context.performBooleanBranch(new BranchCallback() {
+
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
-                                    }, new BranchCallback() {public void branch(MethodCompiler context) {}});
+                                    }, new BranchCallback() {
+
+                                        public void branch(MethodCompiler context) {
+                                        }
+                                    });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
+
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
                             becomeTrueOrFalse(context);
                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                         }
                     });
         } else {
-            context.performBooleanBranch(
-                    new BranchCallback() {
+            context.performBooleanBranch(new BranchCallback() {
+
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getEndNode(), context);
-                            context.performBooleanBranch(
-                                    new BranchCallback() {
+                            context.performBooleanBranch(new BranchCallback() {
+
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                         }
-                                    }, new BranchCallback() {public void branch(MethodCompiler context) {}});
+                                    }, new BranchCallback() {
+
+                                        public void branch(MethodCompiler context) {
+                                        }
+                                    });
                             context.loadTrue();
                         }
                     }, new BranchCallback() {
+
                         public void branch(MethodCompiler context) {
                             compile(flipNode.getBeginNode(), context);
-                            context.performBooleanBranch(
-                                    new BranchCallback() {
+                            context.performBooleanBranch(new BranchCallback() {
+
                                         public void branch(MethodCompiler context) {
                                             compile(flipNode.getEndNode(), context);
                                             flipTrueOrFalse(context);
                                             context.getVariableCompiler().assignLocalVariable(flipNode.getIndex(), flipNode.getDepth());
                                             context.consumeCurrentValue();
                                             context.loadTrue();
                                         }
                                     }, new BranchCallback() {
+
                                         public void branch(MethodCompiler context) {
                                             context.loadFalse();
                                         }
                                     });
                         }
                     });
         }
     }
-    
+
     private static void becomeTrueOrFalse(MethodCompiler context) {
-        context.performBooleanBranch(
-                new BranchCallback() {
+        context.performBooleanBranch(new BranchCallback() {
+
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 }, new BranchCallback() {
+
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 });
     }
-    
+
     private static void flipTrueOrFalse(MethodCompiler context) {
-        context.performBooleanBranch(
-                new BranchCallback() {
+        context.performBooleanBranch(new BranchCallback() {
+
                     public void branch(MethodCompiler context) {
                         context.loadFalse();
                     }
                 }, new BranchCallback() {
+
                     public void branch(MethodCompiler context) {
                         context.loadTrue();
                     }
                 });
     }
-    
+
     public static void compileFloat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        FloatNode floatNode = (FloatNode)node;
-        
+
+        FloatNode floatNode = (FloatNode) node;
+
         context.createNewFloat(floatNode.getValue());
     }
-    
+
     public static void compileFor(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final ForNode forNode = (ForNode)node;
-        
+
+        final ForNode forNode = (ForNode) node;
+
         ClosureCallback receiverCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(forNode.getIterNode(), context);
-            }
-        };
-           
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(forNode.getIterNode(), context);
+                    }
+                };
+
         final ClosureCallback closureArg = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileForIter(forNode, context);
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        compileForIter(forNode, context);
+                    }
+                };
 
         context.getInvocationCompiler().invokeDynamic("each", receiverCallback, null, CallType.NORMAL, closureArg, false);
     }
-    
+
     public static void compileForIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final ForNode forNode = (ForNode)node;
+        final ForNode forNode = (ForNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (forNode.getBodyNode() != null) {
-                    ASTCompiler.compile(forNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        if (forNode.getBodyNode() != null) {
+                            ASTCompiler.compile(forNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
 
         // create the closure class and instantiate it
         final ClosureCallback closureArgs = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (forNode.getVarNode() != null) {
-                    compileAssignment(forNode.getVarNode(), context);
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (forNode.getVarNode() != null) {
+                            compileAssignment(forNode.getVarNode(), context);
+                        }
+                    }
+                };
+
         boolean hasMultipleArgsHead = false;
         if (forNode.getVarNode() instanceof MultipleAsgnNode) {
-            hasMultipleArgsHead = ((MultipleAsgnNode)forNode.getVarNode()).getHeadNode() != null;
+            hasMultipleArgsHead = ((MultipleAsgnNode) forNode.getVarNode()).getHeadNode() != null;
         }
-        
+
         NodeType argsNodeId = null;
         if (forNode.getVarNode() != null) {
             argsNodeId = forNode.getVarNode().nodeId;
         }
-        
+
         if (argsNodeId == null) {
             // no args, do not pass args processor
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, null, hasMultipleArgsHead, argsNodeId);
         } else {
             context.createNewForLoop(Arity.procArityOf(forNode.getVarNode()).getValue(),
                     closureBody, closureArgs, hasMultipleArgsHead, argsNodeId);
         }
     }
-    
+
     public static void compileGlobalAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode)node;
-        
+
+        GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
+
         compile(globalAsgnNode.getValueNode(), context);
-                
+
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
-            case '_':
-                context.getVariableCompiler().assignLastLine();
-                return;
-            case '~':
-                assert false: "Parser shouldn't allow assigning to $~";
-                return;
-            default:
+                case '_':
+                    context.getVariableCompiler().assignLastLine();
+                    return;
+                case '~':
+                    assert false : "Parser shouldn't allow assigning to $~";
+                    return;
+                default:
                 // fall off the end, handle it as a normal global
             }
         }
-        
+
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
-    
+
     public static void compileGlobalAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode)node;
-                
+
+        GlobalAsgnNode globalAsgnNode = (GlobalAsgnNode) node;
+
         if (globalAsgnNode.getName().length() == 2) {
             switch (globalAsgnNode.getName().charAt(1)) {
-            case '_':
-                context.getVariableCompiler().assignLastLine();
-                return;
-            case '~':
-                assert false: "Parser shouldn't allow assigning to $~";
-                return;
-            default:
+                case '_':
+                    context.getVariableCompiler().assignLastLine();
+                    return;
+                case '~':
+                    assert false : "Parser shouldn't allow assigning to $~";
+                    return;
+                default:
                 // fall off the end, handle it as a normal global
             }
         }
-        
+
         context.assignGlobalVariable(globalAsgnNode.getName());
     }
-    
+
     public static void compileGlobalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        GlobalVarNode globalVarNode = (GlobalVarNode)node;
-                
+
+        GlobalVarNode globalVarNode = (GlobalVarNode) node;
+
         if (globalVarNode.getName().length() == 2) {
             switch (globalVarNode.getName().charAt(1)) {
-            case '_':
-                context.getVariableCompiler().retrieveLastLine();
-                return;
-            case '~':
-                context.getVariableCompiler().retrieveBackRef();
-                return;
+                case '_':
+                    context.getVariableCompiler().retrieveLastLine();
+                    return;
+                case '~':
+                    context.getVariableCompiler().retrieveBackRef();
+                    return;
             }
         }
-        
+
         context.retrieveGlobalVariable(globalVarNode.getName());
     }
-    
+
     public static void compileHash(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        HashNode hashNode = (HashNode)node;
-        
+
+        HashNode hashNode = (HashNode) node;
+
         if (hashNode.getListNode() == null || hashNode.getListNode().size() == 0) {
             context.createEmptyHash();
             return;
         }
-        
+
         ArrayCallback hashCallback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray,
-                                  int index) {
-                ListNode listNode = (ListNode)sourceArray;
-                int keyIndex = index * 2;
-                compile(listNode.get(keyIndex), context);
-                compile(listNode.get(keyIndex + 1), context);
-            }
-        };
-        
+
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        ListNode listNode = (ListNode) sourceArray;
+                        int keyIndex = index * 2;
+                        compile(listNode.get(keyIndex), context);
+                        compile(listNode.get(keyIndex + 1), context);
+                    }
+                };
+
         context.createNewHash(hashNode.getListNode(), hashCallback, hashNode.getListNode().size() / 2);
     }
-    
+
     public static void compileIf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final IfNode ifNode = (IfNode)node;
-        
+
+        final IfNode ifNode = (IfNode) node;
+
         compile(ifNode.getCondition(), context);
-        
+
         BranchCallback trueCallback = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (ifNode.getThenBody() != null) {
-                    compile(ifNode.getThenBody(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (ifNode.getThenBody() != null) {
+                            compile(ifNode.getThenBody(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         BranchCallback falseCallback = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (ifNode.getElseBody() != null) {
-                    compile(ifNode.getElseBody(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (ifNode.getElseBody() != null) {
+                            compile(ifNode.getElseBody(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         context.performBooleanBranch(trueCallback, falseCallback);
     }
-    
+
     public static void compileInstAsgn(Node node, MethodCompiler context) {
-        InstAsgnNode instAsgnNode = (InstAsgnNode)node;
-        
+        InstAsgnNode instAsgnNode = (InstAsgnNode) node;
+
         compile(instAsgnNode.getValueNode(), context);
-        
+
         compileInstAsgnAssignment(node, context);
     }
-    
+
     public static void compileInstAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        InstAsgnNode instAsgnNode = (InstAsgnNode)node;
+
+        InstAsgnNode instAsgnNode = (InstAsgnNode) node;
         context.assignInstanceVariable(instAsgnNode.getName());
     }
-    
+
     public static void compileInstVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        InstVarNode instVarNode = (InstVarNode)node;
-        
+
+        InstVarNode instVarNode = (InstVarNode) node;
+
         context.retrieveInstanceVariable(instVarNode.getName());
     }
-    
+
     public static void compileIter(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        final IterNode iterNode = (IterNode)node;
+        final IterNode iterNode = (IterNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (iterNode.getBodyNode() != null) {
-                    ASTCompiler.compile(iterNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        if (iterNode.getBodyNode() != null) {
+                            ASTCompiler.compile(iterNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
 
         // create the closure class and instantiate it
         final ClosureCallback closureArgs = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (iterNode.getVarNode() != null) {
-                    compileAssignment(iterNode.getVarNode(), context);
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (iterNode.getVarNode() != null) {
+                            compileAssignment(iterNode.getVarNode(), context);
+                        }
+                    }
+                };
+
         boolean hasMultipleArgsHead = false;
         if (iterNode.getVarNode() instanceof MultipleAsgnNode) {
-            hasMultipleArgsHead = ((MultipleAsgnNode)iterNode.getVarNode()).getHeadNode() != null;
+            hasMultipleArgsHead = ((MultipleAsgnNode) iterNode.getVarNode()).getHeadNode() != null;
         }
-        
+
         NodeType argsNodeId = null;
         if (iterNode.getVarNode() != null && iterNode.getVarNode().nodeId != NodeType.ZEROARGNODE) {
+            // if we have multiple asgn with just *args, need a special type for that
             argsNodeId = iterNode.getVarNode().nodeId;
+            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
+                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
+                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
+                    // FIXME: This is gross. Don't do this.
+                    argsNodeId = NodeType.SVALUENODE;
+                }
+            }
         }
-        
+
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
-        
-        LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
-        
+
+        LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
+
         compile(localAsgnNode.getValueNode(), context);
-        
+
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
 
     public static void compileLocalAsgnAssignment(Node node, MethodCompiler context) {
         // "assignment" means the value is already on the stack
         context.lineNumber(node.getPosition());
-        
-        LocalAsgnNode localAsgnNode = (LocalAsgnNode)node;
-        
+
+        LocalAsgnNode localAsgnNode = (LocalAsgnNode) node;
+
         context.getVariableCompiler().assignLocalVariable(localAsgnNode.getIndex(), localAsgnNode.getDepth());
     }
-    
+
     public static void compileLocalVar(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        LocalVarNode localVarNode = (LocalVarNode)node;
-        
+
+        LocalVarNode localVarNode = (LocalVarNode) node;
+
         context.getVariableCompiler().retrieveLocalVariable(localVarNode.getIndex(), localVarNode.getDepth());
     }
-    
+
     public static void compileMatch(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        MatchNode matchNode = (MatchNode)node;
+
+        MatchNode matchNode = (MatchNode) node;
 
         compile(matchNode.getRegexpNode(), context);
-        
+
         context.match();
     }
-    
+
     public static void compileMatch2(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        Match2Node matchNode = (Match2Node)node;
+
+        Match2Node matchNode = (Match2Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
-        
+
         context.match2();
     }
+
     public static void compileMatch3(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        Match3Node matchNode = (Match3Node)node;
+
+        Match3Node matchNode = (Match3Node) node;
 
         compile(matchNode.getReceiverNode(), context);
         compile(matchNode.getValueNode(), context);
-        
+
         context.match3();
     }
-    
+
     public static void compileModule(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final ModuleNode moduleNode = (ModuleNode)node;
-        
+
+        final ModuleNode moduleNode = (ModuleNode) node;
+
         final Node cpathNode = moduleNode.getCPath();
-        
+
         ClosureCallback bodyCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (moduleNode.getBodyNode() != null) {
-                    ASTCompiler.compile(moduleNode.getBodyNode(), context);
-                }
-                context.loadNil();
-            }
-        };
-        
-        ClosureCallback pathCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (cpathNode instanceof Colon2Node) {
-                    Node leftNode = ((Colon2Node)cpathNode).getLeftNode();
-                    if (leftNode != null) {
-                        ASTCompiler.compile(leftNode, context);
-                    } else {
+
+                    public void compile(MethodCompiler context) {
+                        if (moduleNode.getBodyNode() != null) {
+                            ASTCompiler.compile(moduleNode.getBodyNode(), context);
+                        }
                         context.loadNil();
                     }
-                } else if (cpathNode instanceof Colon3Node) {
-                    context.loadObject();
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+                };
+
+        ClosureCallback pathCallback = new ClosureCallback() {
+
+                    public void compile(MethodCompiler context) {
+                        if (cpathNode instanceof Colon2Node) {
+                            Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
+                            if (leftNode != null) {
+                                ASTCompiler.compile(leftNode, context);
+                            } else {
+                                context.loadNil();
+                            }
+                        } else if (cpathNode instanceof Colon3Node) {
+                            context.loadObject();
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         context.defineModule(moduleNode.getCPath().getName(), moduleNode.getScope(), pathCallback, bodyCallback);
     }
-    
+
     public static void compileMultipleAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)node;
-        
+
+        MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
+
         // FIXME: This is a little less efficient than it could be, since in the interpreter we avoid objectspace for these arrays
         compile(multipleAsgnNode.getValueNode(), context);
-        
+
         compileMultipleAsgnAssignment(node, context);
     }
-    
+
     public static void compileMultipleAsgnAssignment(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)node;
-        
+
+        final MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode) node;
+
         context.ensureMultipleAssignableRubyArray(multipleAsgnNode.getHeadNode() != null);
-        
+
         // normal items at the "head" of the masgn
         ArrayCallback headAssignCallback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray,
-                                  int index) {
-                ListNode headNode = (ListNode)sourceArray;
-                Node assignNode = headNode.get(index);
 
-                // perform assignment for the next node
-                compileAssignment(assignNode, context);
-            }
-        };
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        ListNode headNode = (ListNode) sourceArray;
+                        Node assignNode = headNode.get(index);
+
+                        // perform assignment for the next node
+                        compileAssignment(assignNode, context);
+                    }
+                };
 
         // head items for which we've run out of assignable elements
         ArrayCallback headNilCallback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray,
-                                  int index) {
-                ListNode headNode = (ListNode)sourceArray;
-                Node assignNode = headNode.get(index);
 
-                // perform assignment for the next node
-                context.loadNil();
-                compileAssignment(assignNode, context);
-            }
-        };
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        ListNode headNode = (ListNode) sourceArray;
+                        Node assignNode = headNode.get(index);
+
+                        // perform assignment for the next node
+                        context.loadNil();
+                        compileAssignment(assignNode, context);
+                    }
+                };
 
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                Node argsNode = multipleAsgnNode.getArgsNode();
-                if (argsNode instanceof StarNode) {
-                    // done processing args
-                } else {
-                    // assign to appropriate variable
-                    ASTCompiler.compileAssignment(argsNode, context);
-                }
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        Node argsNode = multipleAsgnNode.getArgsNode();
+                        if (argsNode instanceof StarNode) {
+                        // done processing args
+                        } else {
+                            // assign to appropriate variable
+                            ASTCompiler.compileAssignment(argsNode, context);
+                        }
+                    }
+                };
 
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
-        
+
         context.setPosition(node.getPosition());
-        
-        NewlineNode newlineNode = (NewlineNode)node;
-        
+
+        NewlineNode newlineNode = (NewlineNode) node;
+
         compile(newlineNode.getNextNode(), context);
     }
-    
+
     public static void compileNext(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final NextNode nextNode = (NextNode)node;
-        
+
+        final NextNode nextNode = (NextNode) node;
+
         ClosureCallback valueCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (nextNode.getValueNode() != null) {
-                    ASTCompiler.compile(nextNode.getValueNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        if (nextNode.getValueNode() != null) {
+                            ASTCompiler.compile(nextNode.getValueNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
+
         context.pollThreadEvents();
         context.issueNextEvent(valueCallback);
     }
+
     public static void compileNthRef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        NthRefNode nthRefNode = (NthRefNode)node;
-        
+
+        NthRefNode nthRefNode = (NthRefNode) node;
+
         context.nthRef(nthRefNode.getMatchNumber());
     }
-    
+
     public static void compileNil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         context.loadNil();
-        
+
         context.pollThreadEvents();
     }
-    
+
     public static void compileNot(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        NotNode notNode = (NotNode)node;
-        
+
+        NotNode notNode = (NotNode) node;
+
         compile(notNode.getConditionNode(), context);
-        
+
         context.negateCurrentValue();
     }
-    
+
     public static void compileOpAsgnAnd(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final BinaryOperatorNode andNode = (BinaryOperatorNode)node;
-        
+
+        final BinaryOperatorNode andNode = (BinaryOperatorNode) node;
+
         compile(andNode.getFirstNode(), context);
-        
+
         BranchCallback longCallback = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                compile(andNode.getSecondNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        compile(andNode.getSecondNode(), context);
+                    }
+                };
+
         context.performLogicalAnd(longCallback);
         context.pollThreadEvents();
     }
 
     public static void compileOpAsgnOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final OpAsgnOrNode orNode = (OpAsgnOrNode)node;
+
+        final OpAsgnOrNode orNode = (OpAsgnOrNode) node;
 
         compileGetDefinitionBase(orNode.getFirstNode(), context);
 
         context.isNull(new BranchCallback() {
-                public void branch(MethodCompiler context) {
-                    compile(orNode.getSecondNode(), context);
-                }}, new BranchCallback() {
-                        public void branch(MethodCompiler context) {
-                            compile(orNode.getFirstNode(), context);
-                            context.duplicateCurrentValue();
-                            context.performBooleanBranch(
-                                                         new BranchCallback() {
-                                                             public void branch(MethodCompiler context) {
-                                                                 //Do nothing
-                                                             }},
-                                                         new BranchCallback() {
-                                                             public void branch(MethodCompiler context) {
-                                                                 context.consumeCurrentValue();
-                                                                 compile(orNode.getSecondNode(), context);
-                                                             }}
-                                                         );
-                        }});
+
+                    public void branch(MethodCompiler context) {
+                        compile(orNode.getSecondNode(), context);
+                    }
+                }, new BranchCallback() {
+
+                    public void branch(MethodCompiler context) {
+                        compile(orNode.getFirstNode(), context);
+                        context.duplicateCurrentValue();
+                        context.performBooleanBranch(new BranchCallback() {
+
+                                    public void branch(MethodCompiler context) {
+                                    //Do nothing
+                                    }
+                                },
+                                new BranchCallback() {
+
+                                    public void branch(MethodCompiler context) {
+                                        context.consumeCurrentValue();
+                                        compile(orNode.getSecondNode(), context);
+                                    }
+                                });
+                    }
+                });
 
         context.pollThreadEvents();
     }
 
     public static void compileOpAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         // FIXME: This is a little more complicated than it needs to be; do we see now why closures would be nice in Java?
-        
-        final OpAsgnNode opAsgnNode = (OpAsgnNode)node;
-        
+
+        final OpAsgnNode opAsgnNode = (OpAsgnNode) node;
+
         final ClosureCallback receiverCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(opAsgnNode.getReceiverNode(), context); // [recv]
-                context.duplicateCurrentValue(); // [recv, recv]
-            }
-        };
-        
-        BranchCallback doneBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                // get rid of extra receiver, leave the variable result present
-                context.swapValues();
-                context.consumeCurrentValue();
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(opAsgnNode.getReceiverNode(), context); // [recv]
+                        context.duplicateCurrentValue(); // [recv, recv]
+                    }
+                };
+
+        BranchCallback doneBranch = new BranchCallback() {
+
+                    public void branch(MethodCompiler context) {
+                        // get rid of extra receiver, leave the variable result present
+                        context.swapValues();
+                        context.consumeCurrentValue();
+                    }
+                };
+
         // Just evaluate the value and stuff it in an argument array
         final ArrayCallback justEvalValue = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray,
-                    int index) {
-                compile(((Node[])sourceArray)[index], context);
-            }
-        };
-        
+
+                    public void nextValue(MethodCompiler context, Object sourceArray,
+                            int index) {
+                        compile(((Node[]) sourceArray)[index], context);
+                    }
+                };
+
         BranchCallback assignBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                // eliminate extra value, eval new one and assign
-                context.consumeCurrentValue();
-                context.createObjectArray(new Node[] {opAsgnNode.getValueNode()}, justEvalValue);
-                context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        // eliminate extra value, eval new one and assign
+                        context.consumeCurrentValue();
+                        context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
+                        context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
+                    }
+                };
+
         ClosureCallback receiver2Callback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                context.getInvocationCompiler().invokeDynamic(opAsgnNode.getVariableName(), receiverCallback, null, CallType.FUNCTIONAL, null, false);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        context.getInvocationCompiler().invokeDynamic(opAsgnNode.getVariableName(), receiverCallback, null, CallType.FUNCTIONAL, null, false);
+                    }
+                };
+
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
-                public void compile(MethodCompiler context) {
-                    context.createObjectArray(new Node[] {opAsgnNode.getValueNode()}, justEvalValue);
-                }
-            };
+
+                        public void compile(MethodCompiler context) {
+                            context.createObjectArray(new Node[]{opAsgnNode.getValueNode()}, justEvalValue);
+                        }
+                    };
             context.getInvocationCompiler().invokeDynamic(opAsgnNode.getOperatorName(), receiver2Callback, argsCallback, CallType.FUNCTIONAL, null, false);
             context.createObjectArray(1);
             context.getInvocationCompiler().invokeAttrAssign(opAsgnNode.getVariableNameAsgn());
         }
 
         context.pollThreadEvents();
     }
-    
+
     public static void compileOpElementAsgn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode)node;
-        
+
+        final OpElementAsgnNode opElementAsgnNode = (OpElementAsgnNode) node;
+
         compile(opElementAsgnNode.getReceiverNode(), context);
         compileArguments(opElementAsgnNode.getArgsNode(), context);
-        
+
         ClosureCallback valueArgsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                ASTCompiler.compile(opElementAsgnNode.getValueNode(), context);
-            }
-        };
-        
+
+                    public void compile(MethodCompiler context) {
+                        ASTCompiler.compile(opElementAsgnNode.getValueNode(), context);
+                    }
+                };
+
         context.getInvocationCompiler().opElementAsgn(valueArgsCallback, opElementAsgnNode.getOperatorName());
     }
-    
+
     public static void compileOr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final OrNode orNode = (OrNode)node;
-        
+
+        final OrNode orNode = (OrNode) node;
+
         compile(orNode.getFirstNode(), context);
-        
+
         BranchCallback longCallback = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                compile(orNode.getSecondNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        compile(orNode.getSecondNode(), context);
+                    }
+                };
+
         context.performLogicalOr(longCallback);
     }
-    
+
     public static void compilePostExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final PostExeNode postExeNode = (PostExeNode)node;
+
+        final PostExeNode postExeNode = (PostExeNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (postExeNode.getBodyNode() != null) {
-                    ASTCompiler.compile(postExeNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        if (postExeNode.getBodyNode() != null) {
+                            ASTCompiler.compile(postExeNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
         context.createNewEndBlock(closureBody);
     }
-    
+
     public static void compilePreExe(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final PreExeNode preExeNode = (PreExeNode)node;
+
+        final PreExeNode preExeNode = (PreExeNode) node;
 
         // create the closure class and instantiate it
         final ClosureCallback closureBody = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                if (preExeNode.getBodyNode() != null) {
-                    ASTCompiler.compile(preExeNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        if (preExeNode.getBodyNode() != null) {
+                            ASTCompiler.compile(preExeNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+                    }
+                };
         context.runBeginBlock(preExeNode.getScope(), closureBody);
     }
-    
+
     public static void compileRedo(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         //RedoNode redoNode = (RedoNode)node;
-        
+
         context.issueRedoEvent();
     }
-    
+
     public static void compileRegexp(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        RegexpNode reNode = (RegexpNode)node;
-        
+
+        RegexpNode reNode = (RegexpNode) node;
+
         int opts = reNode.getOptions();
         String lang = ((opts & 16) == 16) ? "n" : null;
-        if((opts & 48) == 48) { // param s
+        if ((opts & 48) == 48) { // param s
             lang = "s";
-        } else if((opts & 32) == 32) { // param e
+        } else if ((opts & 32) == 32) { // param e
             lang = "e";
-        } else if((opts & 64) != 0) { // param u
+        } else if ((opts & 64) != 0) { // param u
             lang = "u";
         }
 
         context.createNewRegexp(reNode.getValue(), reNode.getOptions(), lang);
     }
-    
+
     public static void compileRescue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final RescueNode rescueNode = (RescueNode)node;
-        
+
+        final RescueNode rescueNode = (RescueNode) node;
+
         BranchCallback body = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (rescueNode.getBodyNode() != null) {
-                    compile(rescueNode.getBodyNode(), context);
-                } else {
-                    context.loadNil();
-                }
-                
-                if (rescueNode.getElseNode() != null) {
-                    context.consumeCurrentValue();
-                    compile(rescueNode.getElseNode(), context);
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (rescueNode.getBodyNode() != null) {
+                            compile(rescueNode.getBodyNode(), context);
+                        } else {
+                            context.loadNil();
+                        }
+
+                        if (rescueNode.getElseNode() != null) {
+                            context.consumeCurrentValue();
+                            compile(rescueNode.getElseNode(), context);
+                        }
+                    }
+                };
+
         BranchCallback handler = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                context.loadException();
-                context.unwrapRaiseException();
-                context.assignGlobalVariable("$!");
-                context.consumeCurrentValue();
-                compileRescueBody(rescueNode.getRescueNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        context.loadException();
+                        context.unwrapRaiseException();
+                        context.assignGlobalVariable("$!");
+                        context.consumeCurrentValue();
+                        compileRescueBody(rescueNode.getRescueNode(), context);
+                    }
+                };
+
         context.rescue(body, RaiseException.class, handler, IRubyObject.class);
     }
-    
+
     public static void compileRescueBody(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final RescueBodyNode rescueBodyNode = (RescueBodyNode)node;
-        
+
+        final RescueBodyNode rescueBodyNode = (RescueBodyNode) node;
+
         context.loadException();
         context.unwrapRaiseException();
-        
+
         Node exceptionList = rescueBodyNode.getExceptionNodes();
         if (exceptionList == null) {
             context.loadClass("StandardError");
             context.createObjectArray(1);
         } else {
             compileArguments(exceptionList, context);
         }
-        
+
         context.checkIsExceptionHandled();
-        
+
         BranchCallback trueBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (rescueBodyNode.getBodyNode() != null) {
-                    compile(rescueBodyNode.getBodyNode(), context);
-                    context.loadNil();
-                    // FIXME: this should reset to what it was before
-                    context.assignGlobalVariable("$!");
-                    context.consumeCurrentValue();
-                } else {
-                    context.loadNil();
-                    // FIXME: this should reset to what it was before
-                    context.assignGlobalVariable("$!");
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (rescueBodyNode.getBodyNode() != null) {
+                            compile(rescueBodyNode.getBodyNode(), context);
+                            context.loadNil();
+                            // FIXME: this should reset to what it was before
+                            context.assignGlobalVariable("$!");
+                            context.consumeCurrentValue();
+                        } else {
+                            context.loadNil();
+                            // FIXME: this should reset to what it was before
+                            context.assignGlobalVariable("$!");
+                        }
+                    }
+                };
+
         BranchCallback falseBranch = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (rescueBodyNode.getOptRescueNode() != null) {
-                    compileRescueBody(rescueBodyNode.getOptRescueNode(), context);
-                } else {
-                    context.rethrowException();
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (rescueBodyNode.getOptRescueNode() != null) {
+                            compileRescueBody(rescueBodyNode.getOptRescueNode(), context);
+                        } else {
+                            context.rethrowException();
+                        }
+                    }
+                };
+
         context.performBooleanBranch(trueBranch, falseBranch);
     }
-    
+
     public static void compileRetry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         context.pollThreadEvents();
-        
+
         context.issueRetryEvent();
     }
-    
+
     public static void compileReturn(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ReturnNode returnNode = (ReturnNode)node;
-        
+
+        ReturnNode returnNode = (ReturnNode) node;
+
         if (returnNode.getValueNode() != null) {
             compile(returnNode.getValueNode(), context);
         } else {
             context.loadNil();
         }
-        
+
         context.performReturn();
     }
-    
+
     public static void compileRoot(Node node, ScriptCompiler context, ASTInspector inspector) {
-        RootNode rootNode = (RootNode)node;
-        
+        RootNode rootNode = (RootNode) node;
+
         context.startScript(rootNode.getStaticScope());
-        
+
         // create method for toplevel of script
         MethodCompiler methodCompiler = context.startMethod("__file__", null, rootNode.getStaticScope(), inspector);
 
         Node nextNode = rootNode.getBodyNode();
         if (nextNode != null) {
             if (nextNode.nodeId == NodeType.BLOCKNODE) {
                 // it's a multiple-statement body, iterate over all elements in turn and chain if it get too long
-                BlockNode blockNode = (BlockNode)nextNode;
-                
+                BlockNode blockNode = (BlockNode) nextNode;
+
                 for (int i = 0; i < blockNode.size(); i++) {
                     if ((i + 1) % 500 == 0) {
                         methodCompiler = methodCompiler.chainToMethod("__file__from_line_" + (i + 1), inspector);
                     }
                     compile(blockNode.get(i), methodCompiler);
-            
+
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
-        
+
         methodCompiler.endMethod();
-        
+
         context.endScript();
     }
-    
+
     public static void compileSelf(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
         context.retrieveSelf();
-    }    
-    
+    }
+
     public static void compileSplat(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        SplatNode splatNode = (SplatNode)node;
-        
+
+        SplatNode splatNode = (SplatNode) node;
+
         compile(splatNode.getValue(), context);
-        
+
         context.splatCurrentValue();
     }
-    
+
     public static void compileStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        StrNode strNode = (StrNode)node;
-        
+
+        StrNode strNode = (StrNode) node;
+
         context.createNewString(strNode.getValue());
     }
-    
+
     public static void compileSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final SuperNode superNode = (SuperNode)node;
-        
+
+        final SuperNode superNode = (SuperNode) node;
+
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                compileArguments(superNode.getArgsNode(), context);
-            }
-        };
-        
-                
+
+                    public void compile(MethodCompiler context) {
+                        compileArguments(superNode.getArgsNode(), context);
+                    }
+                };
+
+
         if (superNode.getIterNode() == null) {
             // no block, go for simple version
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, null);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, null);
             }
         } else {
             ClosureCallback closureArg = getBlock(superNode.getIterNode());
-            
+
             if (superNode.getArgsNode() != null) {
                 context.getInvocationCompiler().invokeSuper(argsCallback, closureArg);
             } else {
                 context.getInvocationCompiler().invokeSuper(null, closureArg);
             }
         }
     }
-    
+
     public static void compileSValue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        SValueNode svalueNode = (SValueNode)node;
-        
+
+        SValueNode svalueNode = (SValueNode) node;
+
         compile(svalueNode.getValue(), context);
-        
+
         context.singlifySplattedValue();
     }
-    
+
     public static void compileSymbol(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        context.createNewSymbol(((SymbolNode)node).getId(),((SymbolNode)node).getName());
-    }    
-    
+        context.createNewSymbol(((SymbolNode) node).getId(), ((SymbolNode) node).getName());
+    }
+
     public static void compileToAry(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
 
-        ToAryNode toAryNode = (ToAryNode)node;
+        ToAryNode toAryNode = (ToAryNode) node;
 
         compile(toAryNode.getValue(), context);
 
         context.aryToAry();
-    }    
-    
+    }
+
     public static void compileTrue(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         context.loadTrue();
-        
+
         context.pollThreadEvents();
     }
-    
+
     public static void compileUndef(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        context.undefMethod(((UndefNode)node).getName());
+
+        context.undefMethod(((UndefNode) node).getName());
     }
-    
+
     public static void compileUntil(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final UntilNode untilNode = (UntilNode)node;
-        
+
+        final UntilNode untilNode = (UntilNode) node;
+
         BranchCallback condition = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                compile(untilNode.getConditionNode(), context);
-                context.negateCurrentValue();
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        compile(untilNode.getConditionNode(), context);
+                        context.negateCurrentValue();
+                    }
+                };
+
         BranchCallback body = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (untilNode.getBodyNode() == null) {
-                    context.loadNil();
-                    return;
-                }
-                compile(untilNode.getBodyNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (untilNode.getBodyNode() == null) {
+                            context.loadNil();
+                            return;
+                        }
+                        compile(untilNode.getBodyNode(), context);
+                    }
+                };
+
         context.performBooleanLoop(condition, body, untilNode.evaluateAtStart());
-        
+
         context.pollThreadEvents();
     }
 
     public static void compileVAlias(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        VAliasNode valiasNode = (VAliasNode)node;
-        
+
+        VAliasNode valiasNode = (VAliasNode) node;
+
         context.aliasGlobal(valiasNode.getNewName(), valiasNode.getOldName());
     }
 
     public static void compileVCall(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        VCallNode vcallNode = (VCallNode)node;
-        
+
+        VCallNode vcallNode = (VCallNode) node;
+
         context.getInvocationCompiler().invokeDynamic(vcallNode.getName(), null, null, CallType.VARIABLE, null, false);
     }
-    
+
     public static void compileWhile(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final WhileNode whileNode = (WhileNode)node;
-        
+
+        final WhileNode whileNode = (WhileNode) node;
+
         BranchCallback condition = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                compile(whileNode.getConditionNode(), context);
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        compile(whileNode.getConditionNode(), context);
+                    }
+                };
+
         BranchCallback body = new BranchCallback() {
-            public void branch(MethodCompiler context) {
-                if (whileNode.getBodyNode() == null) {
-                    context.loadNil();
-                } else {
-                    compile(whileNode.getBodyNode(), context);
-                }
-            }
-        };
-        
+
+                    public void branch(MethodCompiler context) {
+                        if (whileNode.getBodyNode() == null) {
+                            context.loadNil();
+                        } else {
+                            compile(whileNode.getBodyNode(), context);
+                        }
+                    }
+                };
+
         context.performBooleanLoop(condition, body, whileNode.evaluateAtStart());
-        
+
         context.pollThreadEvents();
     }
-    
+
     public static void compileXStr(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        final XStrNode xstrNode = (XStrNode)node;
-        
+
+        final XStrNode xstrNode = (XStrNode) node;
+
         ClosureCallback argsCallback = new ClosureCallback() {
-            public void compile(MethodCompiler context) {
-                context.createNewString(xstrNode.getValue());
-                context.createObjectArray(1);
-            }
-        };
+
+                    public void compile(MethodCompiler context) {
+                        context.createNewString(xstrNode.getValue());
+                        context.createObjectArray(1);
+                    }
+                };
         context.getInvocationCompiler().invokeDynamic("`", null, argsCallback, CallType.FUNCTIONAL, null, false);
     }
-    
+
     public static void compileYield(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        YieldNode yieldNode = (YieldNode)node;
-        
+
+        YieldNode yieldNode = (YieldNode) node;
+
         if (yieldNode.getArgsNode() != null) {
             compile(yieldNode.getArgsNode(), context);
         }
-        
+
         context.getInvocationCompiler().yield(yieldNode.getArgsNode() != null, yieldNode.getCheckState());
     }
-    
+
     public static void compileZArray(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
+
         context.createEmptyArray();
     }
-    
+
     public static void compileZSuper(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ZSuperNode zsuperNode = (ZSuperNode)node;
-        
+
+        ZSuperNode zsuperNode = (ZSuperNode) node;
+
         ClosureCallback closure = getBlock(zsuperNode.getIterNode());
-        
+
         context.callZSuper(closure);
     }
-    
+
     public static void compileArgsCatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArgsCatNode argsCatNode = (ArgsCatNode)node;
-        
+
+        ArgsCatNode argsCatNode = (ArgsCatNode) node;
+
         compileArguments(argsCatNode.getFirstNode(), context);
         // arguments compilers always create IRubyObject[], but we want to use RubyArray.concat here;
         // FIXME: as a result, this is NOT efficient, since it creates and then later unwraps an array
         context.createNewArray(true);
         compile(argsCatNode.getSecondNode(), context);
         context.splatCurrentValue();
         context.concatArrays();
         context.convertToJavaArray();
     }
-    
+
     public static void compileArgsPushArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArgsPushNode argsPushNode = (ArgsPushNode)node;
+
+        ArgsPushNode argsPushNode = (ArgsPushNode) node;
         compile(argsPushNode.getFirstNode(), context);
         compile(argsPushNode.getSecondNode(), context);
         context.appendToArray();
         context.convertToJavaArray();
     }
-    
+
     public static void compileArrayArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        ArrayNode arrayNode = (ArrayNode)node;
-        
+
+        ArrayNode arrayNode = (ArrayNode) node;
+
         ArrayCallback callback = new ArrayCallback() {
-            public void nextValue(MethodCompiler context, Object sourceArray, int index) {
-                Node node = (Node)((Object[])sourceArray)[index];
-                compile(node, context);
-            }
-        };
-        
+
+                    public void nextValue(MethodCompiler context, Object sourceArray, int index) {
+                        Node node = (Node) ((Object[]) sourceArray)[index];
+                        compile(node, context);
+                    }
+                };
+
         context.createObjectArray(arrayNode.childNodes().toArray(), callback);
-        // leave as a normal array
+    // leave as a normal array
     }
-    
+
     public static void compileSplatArguments(Node node, MethodCompiler context) {
         context.lineNumber(node.getPosition());
-        
-        SplatNode splatNode = (SplatNode)node;
-        
+
+        SplatNode splatNode = (SplatNode) node;
+
         compile(splatNode.getValue(), context);
         context.splatCurrentValue();
         context.convertToJavaArray();
     }
-    
+
     /**
      * Check whether the target node can safely be compiled.
      * 
      * @param node 
      */
     public static void confirmNodeIsSafe(Node node) {
         switch (node.nodeId) {
-        case ARGSNODE:
-            ArgsNode argsNode = (ArgsNode)node;
-            // FIXME: We can't compile cases like def(a=(b=1)) because the variables
+            case ARGSNODE:
+                ArgsNode argsNode = (ArgsNode) node;
+                // FIXME: We can't compile cases like def(a=(b=1)) because the variables
             // in the arg list get ordered differently than you might expect (b comes first)
             // So the code below searches through all opt args, ensuring none of them skip
             // indicies. A skipped index means there's a hidden local var/arg like b above
             // and so we shouldn't try to compile.
-            if (argsNode.getOptArgs() != null && argsNode.getOptArgs().size() > 0) {
-                int index = argsNode.getRequiredArgsCount() - 1;
-                
-                for (int i = 0; i < argsNode.getOptArgs().size(); i++) {
-                    int newIndex = ((LocalAsgnNode)argsNode.getOptArgs().get(i)).getIndex();
-                    
-                    if (newIndex - index != 1) {
-                        throw new NotCompilableException("Can't compile def with optional args that assign other variables at: " + node.getPosition());
-                    }
-                    index = newIndex;
+                if (argsNode.getOptArgs() != null && argsNode.getOptArgs().size() > 0) {
+                    int index = argsNode.getRequiredArgsCount() - 1;
+
+                    for (int i = 0; i < argsNode.getOptArgs().size(); i++) {
+                        int newIndex = ((LocalAsgnNode) argsNode.getOptArgs().get(i)).getIndex();
+
+                        if (newIndex - index != 1) {
+                            throw new NotCompilableException("Can't compile def with optional args that assign other variables at: " + node.getPosition());
+                        }
+                        index = newIndex;
+                    }
                 }
-            }
-            break;
+                break;
         }
     }
 }
diff --git a/src/org/jruby/compiler/MethodCompiler.java b/src/org/jruby/compiler/MethodCompiler.java
index 0f9c5c4e38..fead9e0574 100644
--- a/src/org/jruby/compiler/MethodCompiler.java
+++ b/src/org/jruby/compiler/MethodCompiler.java
@@ -1,460 +1,457 @@
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
-
-    public void assignLocalVariableBlockArg(int argIndex, int varIndex);
-    public void assignLocalVariableBlockArg(int argIndex, int varIndex, int depth);
     
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
     public void createNewSymbol(int id, String name);
     
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
     
     public MethodCompiler chainToMethod(String name, ASTInspector inspector);
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 2a68c63871..9f72b03ba8 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1512 +1,1486 @@
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
 import org.jruby.RubyLocalJumpError;
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
 
-        public void assignLocalVariableBlockArg(int argIndex, int varIndex) {
-            // this is copying values, but it would be more efficient to just use the args in-place
-            method.aload(DYNAMIC_SCOPE_INDEX);
-            method.ldc(new Integer(varIndex));
-            method.aload(ARGS_INDEX);
-            method.ldc(new Integer(argIndex));
-            method.arrayload();
-            method.iconst_0();
-            method.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
-        }
-
-        public void assignLocalVariableBlockArg(int argIndex, int varIndex, int depth) {
-            if (depth == 0) {
-                assignLocalVariableBlockArg(argIndex, varIndex);
-                return;
-            }
-
-            method.aload(DYNAMIC_SCOPE_INDEX);
-            method.ldc(new Integer(varIndex));
-            method.aload(ARGS_INDEX);
-            method.ldc(new Integer(argIndex));
-            method.arrayload();
-            method.ldc(new Integer(depth));
-            method.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
-        }
-
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
             invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
         }
 
         public void createNewSymbol(int id, String name) {
             loadRuntime(); 
             if(id != -1) {
                 method.ldc(id);
                 method.invokestatic(cg.p(RubySymbol.class), "getSymbol", cg.sig(RubySymbol.class, cg.params(Ruby.class, int.class)));
             } else {
                 String symField = getNewConstant(cg.ci(int.class), "lit_sym_", new Integer(-1));
 
                 // in current method, load the field to see if we've created a Pattern yet
                 method.aload(THIS); 
                 method.getfield(classname, symField, cg.ci(int.class));
 
                 Label alreadyCreated = new Label();
                 method.ldc(-1);
                 method.if_icmpne(alreadyCreated);
 
                 method.ldc(name); 
                 invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
                 method.dup(); 
                 method.aload(THIS);
                 method.swap(); 
                 method.invokevirtual(cg.p(RubySymbol.class), "getId", cg.sig(int.class, cg.params())); 
                 method.putfield(classname, symField, cg.ci(int.class)); 
                 Label ret = new Label();
                 method.go_to(ret); 
 
                 method.visitLabel(alreadyCreated); 
 
                 method.aload(THIS);
                 method.getfield(classname, symField, cg.ci(int.class)); 
 
                 method.invokestatic(cg.p(RubySymbol.class), "getSymbol", cg.sig(RubySymbol.class, cg.params(Ruby.class, int.class)));
 
                 method.visitLabel(ret);
             }
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
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(Block.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(CompiledSharedScopeBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
         }
 
         public void createNewEndBlock(ClosureCallback body) {
             String closureMethodName = "END_closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, null);
             
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
             method.ldc(new Integer(0));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
             method.ldc(false);
             method.ldc(Block.ZERO_ARGS);
 
             invokeUtilityMethod("createSharedScopeBlock", cg.sig(CompiledSharedScopeBlock.class,
                     cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
             
             loadRuntime();
             invokeUtilityMethod("registerEndBlock", cg.sig(void.class, CompiledSharedScopeBlock.class, Ruby.class));
             loadNil();
         }
 
         private void getCallbackFactory() {
             // FIXME: Perhaps a bit extra code, but only for defn/s; examine
             loadRuntime();
             getCompiledClass();
             method.dup();
             method.invokevirtual(cg.p(Class.class), "getClassLoader", cg.sig(ClassLoader.class));
             method.invokestatic(cg.p(CallbackFactory.class), "createFactory", cg.sig(CallbackFactory.class, cg.params(Ruby.class, Class.class, ClassLoader.class)));
         }
 
         public void getCompiledClass() {
             method.aload(THIS);
             method.getfield(classname, "$class", cg.ci(Class.class));
         }
 
         private void getRubyClass() {
             loadThreadContext();
             invokeThreadContext("getRubyClass", cg.sig(RubyModule.class));
         }
 
         public void println() {
             method.dup();
             method.getstatic(cg.p(System.class), "out", cg.ci(PrintStream.class));
             method.swap();
 
             method.invokevirtual(cg.p(PrintStream.class), "println", cg.sig(Void.TYPE, cg.params(Object.class)));
         }
 
         public void defineAlias(String newName, String oldName) {
             loadThreadContext();
             method.ldc(newName);
             method.ldc(oldName);
             invokeUtilityMethod("defineAlias", cg.sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
         }
 
         public void loadFalse() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getFalse", cg.sig(RubyBoolean.class));
         }
 
         public void loadTrue() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getTrue", cg.sig(RubyBoolean.class));
         }
 
         public void loadCurrentModule() {
             loadThreadContext();
             invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
             method.invokevirtual(cg.p(DynamicScope.class), "getStaticScope", cg.sig(StaticScope.class));
             method.invokevirtual(cg.p(StaticScope.class), "getModule", cg.sig(RubyModule.class));
         }
 
         public void retrieveInstanceVariable(String name) {
             loadRuntime();
             loadSelf();
             method.ldc(name);
             invokeUtilityMethod("fastGetInstanceVariable", cg.sig(IRubyObject.class, Ruby.class, IRubyObject.class, String.class));
         }
 
         public void assignInstanceVariable(String name) {
             // FIXME: more efficient with a callback
             loadSelf();
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             invokeIRubyObject("fastSetInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void retrieveGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(cg.p(GlobalVariables.class), "get", cg.sig(IRubyObject.class, cg.params(String.class)));
         }
 
         public void assignGlobalVariable(String name) {
             // FIXME: more efficient with a callback
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
             method.swap();
             method.ldc(name);
             method.swap();
             method.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
         }
 
         public void negateCurrentValue() {
             loadRuntime();
             invokeUtilityMethod("negate", cg.sig(IRubyObject.class, IRubyObject.class, Ruby.class));
         }
 
         public void splatCurrentValue() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "splatValue", cg.sig(RubyArray.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void singlifySplattedValue() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "aValueSplat", cg.sig(IRubyObject.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void aryToAry() {
             loadRuntime();
             method.invokestatic(cg.p(ASTInterpreter.class), "aryToAry", cg.sig(IRubyObject.class, cg.params(IRubyObject.class, Ruby.class)));
         }
 
         public void ensureRubyArray() {
             invokeUtilityMethod("ensureRubyArray", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
         }
 
         public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
             loadRuntime();
             method.swap();
             method.ldc(new Boolean(masgnHasHead));
             invokeUtilityMethod("ensureMultipleAssignableRubyArray", cg.sig(RubyArray.class, cg.params(Ruby.class, IRubyObject.class, boolean.class)));
         }
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, ClosureCallback argsCallback) {
             // FIXME: This could probably be made more efficient
             for (; start < count; start++) {
                 Label noMoreArrayElements = new Label();
                 Label doneWithElement = new Label();
                 
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(noMoreArrayElements); // if length <= start, end loop
                 
                 // extract item from array
                 method.dup(); // dup the original array object
                 method.ldc(new Integer(start)); // index for the item
                 method.invokevirtual(cg.p(RubyArray.class), "entry", cg.sig(IRubyObject.class, cg.params(Integer.TYPE))); // extract item
                 callback.nextValue(this, source, start);
                 method.go_to(doneWithElement);
                 
                 // otherwise no items left available, use the code from nilCallback
                 method.label(noMoreArrayElements);
                 nilCallback.nextValue(this, source, start);
                 
                 // end of this element
                 method.label(doneWithElement);
                 // normal assignment leaves the value; pop it.
                 method.pop();
             }
             
             if (argsCallback != null) {
                 Label emptyArray = new Label();
                 Label readyForArgs = new Label();
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(emptyArray); // if length <= start, end loop
                 
                 // assign remaining elements as an array for rest args
                 method.dup(); // dup the original array object
                 method.ldc(start);
                 invokeUtilityMethod("createSubarray", cg.sig(RubyArray.class, RubyArray.class, int.class));
                 method.go_to(readyForArgs);
                 
                 // create empty array
                 method.label(emptyArray);
                 createEmptyArray();
                 
                 // assign rest args
                 method.label(readyForArgs);
                 argsCallback.compile(this);
                 //consume leftover assigned value
                 method.pop();
             }
         }
 
         public void asString() {
             method.invokeinterface(cg.p(IRubyObject.class), "asString", cg.sig(RubyString.class, cg.params()));
         }
         
         public void toJavaString() {
             method.invokevirtual(cg.p(Object.class), "toString", cg.sig(String.class));
         }
 
         public void nthRef(int match) {
             method.ldc(new Integer(match));
             backref();
             method.invokestatic(cg.p(RubyRegexp.class), "nth_match", cg.sig(IRubyObject.class, cg.params(Integer.TYPE, IRubyObject.class)));
         }
 
         public void match() {
             method.invokevirtual(cg.p(RubyRegexp.class), "op_match2", cg.sig(IRubyObject.class, cg.params()));
         }
 
         public void match2() {
             method.invokevirtual(cg.p(RubyRegexp.class), "op_match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
         }
 
         public void match3() {
             loadThreadContext();
             invokeUtilityMethod("match3", cg.sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void createNewRegexp(final ByteList value, final int options, final String lang) {
             String regexpField = getNewConstant(cg.ci(RubyRegexp.class), "lit_reg_");
             String patternField = getNewConstant(cg.ci(RegexpPattern.class), "lit_pat_");
 
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
diff --git a/src/org/jruby/evaluator/ASTInterpreter.java b/src/org/jruby/evaluator/ASTInterpreter.java
index da5794aeaa..acaac4dfbf 100644
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
@@ -349,1743 +349,1743 @@ public class ASTInterpreter {
                 continue;
             case RESCUENODE:
                 return rescueNode(runtime, context, node, self, aBlock);
             case RETRYNODE:
                 return retryNode(context);
             case RETURNNODE: 
                 return returnNode(runtime, context, node, self, aBlock);
             case ROOTNODE:
                 return rootNode(runtime, context, node, self, aBlock);
             case SCLASSNODE:
                 return sClassNode(runtime, context, node, self, aBlock);
             case SELFNODE:
                 return pollAndReturn(context, self);
             case SPLATNODE:
                 return splatNode(runtime, context, node, self, aBlock);
             case STRNODE:
                 return strNode(runtime, node);
             case SUPERNODE:
                 return superNode(runtime, context, node, self, aBlock);
             case SVALUENODE:
                 return sValueNode(runtime, context, node, self, aBlock);
             case SYMBOLNODE:
                 return symbolNode(runtime, node);
             case TOARYNODE:
                 return toAryNode(runtime, context, node, self, aBlock);
             case TRUENODE:
                 return trueNode(runtime, context);
             case UNDEFNODE:
                 return undefNode(runtime, context, node);
             case UNTILNODE:
                 return untilNode(runtime, context, node, self, aBlock);
             case VALIASNODE:
                 return valiasNode(runtime, node);
             case VCALLNODE:
                 return vcallNode(runtime, context, node, self);
             case WHENNODE:
                 assert false;
                 return null;
             case WHILENODE:
                 return whileNode(runtime, context, node, self, aBlock);
             case XSTRNODE:
                 return xStrNode(runtime, context, node, self);
             case YIELDNODE:
                 return yieldNode(runtime, context, node, self, aBlock);
             case ZARRAYNODE:
                 return zArrayNode(runtime);
             case ZSUPERNODE:
                 return zsuperNode(runtime, context, node, self, aBlock);
             default:
                 throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
             }
         } while(true);
     }
 
     private static IRubyObject aliasNode(Ruby runtime, ThreadContext context, Node node) {
         AliasNode iVisited = (AliasNode) node;
         RuntimeHelpers.defineAlias(context, iVisited.getNewName(), iVisited.getOldName());
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(iVisited.getNewName(), iVisited.getOldName());
         module.callMethod(context, "method_added", runtime.newSymbol(iVisited.getNewName()));
    
         return runtime.getNil();
     }
     
     private static IRubyObject argsCatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsCatNode iVisited = (ArgsCatNode) node;
    
         IRubyObject args = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         IRubyObject secondArgs = splatValue(runtime, evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
         RubyArray list = args instanceof RubyArray ? (RubyArray) args : runtime.newArray(args);
    
         return list.concat(secondArgs);
     }
 
     private static IRubyObject argsPushNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsPushNode iVisited = (ArgsPushNode) node;
         
         RubyArray args = (RubyArray) evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock).dup();
         return args.append(evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
     }
 
     private static IRubyObject arrayNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
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
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
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
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
    
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
    
         return variable == null ? runtime.getNil() : variable;
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
         
-        runtime.pushExitBlock(runtime.newProc(true, block));
+        runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
         
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
         
         SymbolNode snode = (SymbolNode)node;
         int id = snode.getId();
         if(id != -1) {
             return RubySymbol.getSymbol(runtime, id);
         } else {
             RubySymbol sym = runtime.newSymbol(snode.getName());
             snode.setId(sym.getId());
             return sym;
         }
         
 
         //        return runtime.newSymbol(((SymbolNode) node).getName());
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
diff --git a/src/org/jruby/evaluator/AssignmentVisitor.java b/src/org/jruby/evaluator/AssignmentVisitor.java
index 24402bc450..961b43bfc7 100644
--- a/src/org/jruby/evaluator/AssignmentVisitor.java
+++ b/src/org/jruby/evaluator/AssignmentVisitor.java
@@ -1,236 +1,236 @@
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
  * Copyright (C) 2003-2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.evaluator;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class AssignmentVisitor {
-    public static IRubyObject assign(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, Block block, boolean check) {
+    public static IRubyObject assign(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, Block block, boolean checkArity) {
         IRubyObject result = null;
         
         switch (node.nodeId) {
         case ATTRASSIGNNODE:
             attrAssignNode(runtime, context, self, node, value, block);
             break;
         case CALLNODE:
             callNode(runtime, context, self, node, value, block);
             break;
         case CLASSVARASGNNODE:
             classVarAsgnNode(context, node, value);
             break;
         case CLASSVARDECLNODE:
             classVarDeclNode(runtime, context, node, value);
             break;
         case CONSTDECLNODE:
             constDeclNode(runtime, context, self, node, value, block);
             break;
         case DASGNNODE:
             dasgnNode(context, node, value);
             break;
         case GLOBALASGNNODE:
             globalAsgnNode(runtime, node, value);
             break;
         case INSTASGNNODE:
             instAsgnNode(self, node, value);
             break;
         case LOCALASGNNODE:
             localAsgnNode(context, node, value);
             break;
         case MULTIPLEASGNNODE:
-            result = multipleAsgnNode(runtime, context, self, node, value, check);
+            result = multipleAsgnNode(runtime, context, self, node, value, checkArity);
             break;
         default:
             throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
         }
 
         return result;
     }
 
     private static void attrAssignNode(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, Block block) {
         AttrAssignNode iVisited = (AttrAssignNode) node;
         
         IRubyObject receiver = ASTInterpreter.eval(runtime, context, iVisited.getReceiverNode(), self, block);
         
         // If reciever is self then we do the call the same way as vcall
         CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
 
         if (iVisited.getArgsNode() == null) { // attribute set.
             receiver.callMethod(context, iVisited.getName(), new IRubyObject[] {value}, callType);
         } else { // element set
             RubyArray args = (RubyArray)ASTInterpreter.eval(runtime, context, iVisited.getArgsNode(), self, block);
             args.append(value);
             receiver.callMethod(context, iVisited.getName(), args.toJavaArray(), callType);
         }
     }
 
     private static void callNode(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, Block block) {
         CallNode iVisited = (CallNode)node;
         
         IRubyObject receiver = ASTInterpreter.eval(runtime, context, iVisited.getReceiverNode(), self, block);
 
         if (iVisited.getArgsNode() == null) { // attribute set.
             receiver.callMethod(context, iVisited.getName(), new IRubyObject[] {value}, CallType.NORMAL);
         } else { // element set
             RubyArray args = (RubyArray)ASTInterpreter.eval(runtime, context, iVisited.getArgsNode(), self, block);
             args.append(value);
             receiver.callMethod(context, iVisited.getName(), args.toJavaArray(), CallType.NORMAL);
         }
     }
 
     private static void classVarAsgnNode(ThreadContext context, Node node, IRubyObject value) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode)node;
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, context.getRuntime());
         rubyClass.fastSetClassVar(iVisited.getName(), value);
     }
 
     private static void classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject value) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode)node;
         if (runtime.getVerbose().isTrue()
                 && context.getRubyClass().isSingleton()) {
             runtime.getWarnings().warn(iVisited.getPosition(),
                     "Declaring singleton class variable.");
         }
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, context.getRuntime());
         rubyClass.fastSetClassVar(iVisited.getName(), value);
     }
 
     private static void constDeclNode(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, Block block) {
         ConstDeclNode iVisited = (ConstDeclNode)node;
         Node constNode = iVisited.getConstNode();
 
         IRubyObject module;
 
         if (constNode == null) {
             module = context.getCurrentScope().getStaticScope().getModule();
             
             if (module == null) {
                 // TODO: wire into new exception handling mechanism
                 throw runtime.newTypeError("no class/module to define constant");
             }
         } else if (constNode instanceof Colon2Node) {
             module = ASTInterpreter.eval(runtime, context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, block);
         } else { // Colon3
             module = runtime.getObject();
         } 
 
         ((RubyModule) module).fastSetConstant(iVisited.getName(), value);
     }
 
     private static void dasgnNode(ThreadContext context, Node node, IRubyObject value) {
         DAsgnNode iVisited = (DAsgnNode)node;
         context.getCurrentScope().setValue(iVisited.getIndex(), value, iVisited.getDepth());
     }
 
     private static void globalAsgnNode(Ruby runtime, Node node, IRubyObject value) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode)node;
         runtime.getGlobalVariables().set(iVisited.getName(), value);
     }
 
     private static void instAsgnNode(IRubyObject self, Node node, IRubyObject value) {
         InstAsgnNode iVisited = (InstAsgnNode)node;
         self.fastSetInstanceVariable(iVisited.getName(), value);
     }
 
     private static void localAsgnNode(ThreadContext context, Node node, IRubyObject value) {
         LocalAsgnNode iVisited = (LocalAsgnNode)node;
         
         context.getCurrentScope().setValue(iVisited.getIndex(), value, iVisited.getDepth());
     }
 
-    public static IRubyObject multiAssign(Ruby runtime, ThreadContext context, IRubyObject self, MultipleAsgnNode node, RubyArray value, boolean callAsProc) {
+    public static IRubyObject multiAssign(Ruby runtime, ThreadContext context, IRubyObject self, MultipleAsgnNode node, RubyArray value, boolean checkArity) {
         // Assign the values.
         int valueLen = value.getLength();
         int varLen = node.getHeadNode() == null ? 0 : node.getHeadNode().size();
         
         int j = 0;
         for (; j < valueLen && j < varLen; j++) {
             Node lNode = node.getHeadNode().get(j);
-            assign(runtime, context, self, lNode, value.eltInternal(j), Block.NULL_BLOCK, callAsProc);
+            assign(runtime, context, self, lNode, value.eltInternal(j), Block.NULL_BLOCK, checkArity);
         }
 
-        if (callAsProc && j < varLen) {
+        if (checkArity && j < varLen) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         Node argsNode = node.getArgsNode();
         if (argsNode != null) {
             if (argsNode.nodeId == NodeType.STARNODE) {
                 // no check for '*'
             } else if (varLen < valueLen) {
-                assign(runtime, context, self, argsNode, value.subseqLight(varLen, valueLen), Block.NULL_BLOCK, callAsProc);
+                assign(runtime, context, self, argsNode, value.subseqLight(varLen, valueLen), Block.NULL_BLOCK, checkArity);
             } else {
-                assign(runtime, context, self, argsNode, RubyArray.newArrayLight(runtime, 0), Block.NULL_BLOCK, callAsProc);
+                assign(runtime, context, self, argsNode, RubyArray.newArrayLight(runtime, 0), Block.NULL_BLOCK, checkArity);
             }
-        } else if (callAsProc && valueLen < varLen) {
+        } else if (checkArity && valueLen < varLen) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         while (j < varLen) {
-            assign(runtime, context, self, node.getHeadNode().get(j++), runtime.getNil(), Block.NULL_BLOCK, callAsProc);
+            assign(runtime, context, self, node.getHeadNode().get(j++), runtime.getNil(), Block.NULL_BLOCK, checkArity);
         }
         
         return value;
     }
     
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, IRubyObject self, Node node, IRubyObject value, boolean check) {
         IRubyObject result;
         MultipleAsgnNode iVisited = (MultipleAsgnNode)node;
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, iVisited.getHeadNode() != null);
         }
         result = multiAssign(runtime, context, self, iVisited, (RubyArray) value, check);
         return result;
     }
 }
diff --git a/src/org/jruby/ext/Generator.java b/src/org/jruby/ext/Generator.java
index 24669dbe31..75c976499b 100644
--- a/src/org/jruby/ext/Generator.java
+++ b/src/org/jruby/ext/Generator.java
@@ -1,307 +1,307 @@
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
 package org.jruby.ext;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.runtime.MethodIndex;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class Generator {
     public static class Service implements Library {
         public void load(final Ruby runtime) throws IOException {
             createGenerator(runtime);
         }
     }
 
     public static void createGenerator(Ruby runtime) throws IOException {
         RubyClass cGen = runtime.defineClass("Generator",runtime.getObject(), runtime.getObject().getAllocator());
         cGen.includeModule(runtime.getEnumerable());
 
         CallbackFactory callbackFactory = runtime.callbackFactory(Generator.class);
 
         cGen.getMetaClass().defineMethod("new",callbackFactory.getOptSingletonMethod("new_instance"));
         cGen.defineMethod("initialize",callbackFactory.getOptSingletonMethod("initialize"));
         cGen.defineMethod("yield",callbackFactory.getSingletonMethod("yield",IRubyObject.class));
         cGen.defineFastMethod("end?",callbackFactory.getFastSingletonMethod("end_p"));
         cGen.defineFastMethod("next?",callbackFactory.getFastSingletonMethod("next_p"));
         cGen.defineFastMethod("index",callbackFactory.getFastSingletonMethod("index"));
         cGen.defineAlias("pos","index");
         cGen.defineMethod("next",callbackFactory.getSingletonMethod("next"));
         cGen.defineMethod("current",callbackFactory.getSingletonMethod("current"));
         cGen.defineMethod("rewind",callbackFactory.getSingletonMethod("rewind"));
         cGen.defineMethod("each",callbackFactory.getSingletonMethod("each"));
     }
 
     static class GeneratorData implements Runnable {
         private IRubyObject gen;
         private Object mutex = new Object();
 
         private IRubyObject enm;
         private RubyProc proc;
 
         private Thread t;
         private boolean end;
         private IterBlockCallback ibc;
 
         public GeneratorData(IRubyObject gen) {
             this.gen = gen;
         }
 
         public void setEnum(IRubyObject enm) {
             this.proc = null;
             this.enm = enm;
             start();
         }
 
         public void setProc(RubyProc proc) {
             this.proc = proc;
             this.enm = null;
             start();
         }
 
         public void start() {
             end = false;
             ibc = new IterBlockCallback();
             t = new Thread(this);
             t.setDaemon(true);
             t.start();
             generate();
         }
 
         public boolean isEnd() {
             return end;
         }
 
         private boolean available = false;
 
         public void doWait() {
             available = true;
             if(proc != null) {
                 boolean inter = true;
                 synchronized(mutex) {
                     mutex.notifyAll();
                     while(inter) {
                         try {
                             mutex.wait();
                             inter = false;
                         } catch(InterruptedException e) {
                         }
                     }
                 }
             }
         }
 
         public void generate() {
             if(proc == null) {
                 boolean inter = true;
                 synchronized(mutex) {
                     while(!ibc.haveValue() && !end) {
                         mutex.notifyAll();
                         inter = true;
                         while(inter) {
                             try {
                                 mutex.wait();
                                 inter = false;
                             } catch(InterruptedException e) {
                             }
                         }
                     }
                     if(!end && proc == null) {
                         gen.callMethod(gen.getRuntime().getCurrentContext(),"yield",ibc.pop());
                     }
                 }
             } else {
                 synchronized(mutex) {
                     while(!available && !end) {
                         boolean inter = true;
                         mutex.notifyAll();
                         while(inter) {
                             try {
                                 mutex.wait(20);
                                 inter = false;
                             } catch(InterruptedException e) {
                             }
                         }
                     }
                     available = false;
                 }
             }
 
         }
 
         private class IterBlockCallback implements BlockCallback {
             private IRubyObject obj;
             public IRubyObject call(ThreadContext context, IRubyObject[] iargs, Block block) {
                 boolean inter = true;
                 synchronized(mutex) {
                     mutex.notifyAll();
                     while(inter) {
                         try {
                             mutex.wait();
                             inter = false;
                         } catch(InterruptedException e) {
                         }
                     }
                     if(iargs.length > 1) {
                         obj = gen.getRuntime().newArrayNoCopy(iargs);
                     } else {
                         obj = iargs[0];
                     }
                     mutex.notifyAll();
                     return gen.getRuntime().getNil();
                 }
             }
             public boolean haveValue() {
                 return obj != null;
             }
             public IRubyObject pop() {
                 IRubyObject a = obj;
                 obj = null;
                 return a;
             }
         }
 
         public void run() {
             if(enm != null) {
                 ThreadContext context = gen.getRuntime().getCurrentContext();
                 enm.callMethod(context, "each", new CallBlock(enm,enm.getMetaClass().getRealClass(),Arity.noArguments(),ibc,context));
             } else {
                 proc.call(new IRubyObject[]{gen});
             }
             end = true;
         }
     }
 
     public static IRubyObject new_instance(IRubyObject self, IRubyObject[] args, Block block) {
         // Generator#new
         IRubyObject result = new RubyObject(self.getRuntime(),(RubyClass)self);
         result.dataWrapStruct(new GeneratorData(result));
         result.callMethod(self.getRuntime().getCurrentContext(), "initialize", args, block);
         return result;
     }
 
     public static IRubyObject initialize(IRubyObject self, IRubyObject[] args, Block block) {
         // Generator#initialize
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         
         self.setInstanceVariable("@queue",self.getRuntime().newArray());
         self.setInstanceVariable("@index",self.getRuntime().newFixnum(0));
         
         if(Arity.checkArgumentCount(self.getRuntime(), args,0,1) == 1) {
             d.setEnum(args[0]);
         } else {
-            d.setProc(self.getRuntime().newProc(false, Block.NULL_BLOCK));
+            d.setProc(self.getRuntime().newProc(Block.Type.PROC, Block.NULL_BLOCK));
         }
         return self;
     }
 
     public static IRubyObject yield(IRubyObject self, IRubyObject value, Block block) {
         // Generator#yield
         self.getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"<<",value);
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         d.doWait();
         return self;
     }
 
     public static IRubyObject end_p(IRubyObject self) {
         // Generator#end_p
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         return d.isEnd() ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
     }
 
     public static IRubyObject next_p(IRubyObject self) {
         // Generator#next_p
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         return !d.isEnd() ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
     }
 
     public static IRubyObject index(IRubyObject self) {
         // Generator#index
         return self.getInstanceVariable("@index");
     }
 
     public static IRubyObject next(IRubyObject self, Block block) {
         // Generator#next
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         if(d.isEnd()) {
             throw self.getRuntime().newEOFError();
         }
         d.generate();
         self.setInstanceVariable("@index",self.getInstanceVariable("@index").callMethod(self.getRuntime().getCurrentContext(),MethodIndex.OP_PLUS, "+",self.getRuntime().newFixnum(1)));
         return self.getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"shift");
     }
 
     public static IRubyObject current(IRubyObject self, Block block) {
             // Generator#current
         if(self.getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),MethodIndex.EMPTY_P, "empty?").isTrue()) {
             throw self.getRuntime().newEOFError();
         }
         return self.getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"first");
     }
 
     public static IRubyObject rewind(IRubyObject self, Block block) {
         // Generator#rewind
         if(self.getInstanceVariable("@index").callMethod(self.getRuntime().getCurrentContext(),"nonzero?").isTrue()) {
             GeneratorData d = (GeneratorData)self.dataGetStruct();
 
             self.setInstanceVariable("@queue",self.getRuntime().newArray());
             self.setInstanceVariable("@index",self.getRuntime().newFixnum(0));
             
             d.start();
         }
 
         return self;
     }
 
     public static IRubyObject each(IRubyObject self, Block block) {
         // Generator#each
         rewind(self,Block.NULL_BLOCK);
         ThreadContext ctx = self.getRuntime().getCurrentContext();
         while(next_p(self).isTrue()) {
             block.yield(ctx, next(self, Block.NULL_BLOCK));
         }
         return self;
     }
 }// Generator
diff --git a/src/org/jruby/internal/runtime/RubyNativeThread.java b/src/org/jruby/internal/runtime/RubyNativeThread.java
index 0526b6863b..807d104cc8 100644
--- a/src/org/jruby/internal/runtime/RubyNativeThread.java
+++ b/src/org/jruby/internal/runtime/RubyNativeThread.java
@@ -1,88 +1,88 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.internal.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyProc;
 import org.jruby.RubyThread;
 import org.jruby.RubyThreadGroup;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyNativeThread extends Thread {
     private Ruby runtime;
     private Frame currentFrame;
     private RubyProc proc;
     private IRubyObject[] arguments;
     private RubyThread rubyThread;
     
     public RubyNativeThread(RubyThread rubyThread, IRubyObject[] args, Block currentBlock) {
         super(rubyThread.getRuntime().getThreadService().getRubyThreadGroup(), "Ruby Thread" + rubyThread.hash());
         setDaemon(true);
         this.rubyThread = rubyThread;
         this.runtime = rubyThread.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         
-        proc = runtime.newProc(false, currentBlock);
+        proc = runtime.newProc(Block.Type.PROC, currentBlock);
         currentFrame = tc.getCurrentFrame();
         this.arguments = args;
     }
     
     public RubyThread getRubyThread() {
         return rubyThread;
     }
     
     public void run() {
         runtime.getThreadService().registerNewThread(rubyThread);
         ThreadContext context = runtime.getCurrentContext();
         
         context.preRunThread(currentFrame);
         
         // Call the thread's code
         try {
             IRubyObject result = proc.call(arguments);
             rubyThread.cleanTerminate(result);
         } catch (ThreadKill tk) {
             // notify any killer waiting on our thread that we're going bye-bye
             synchronized (rubyThread.killLock) {
                 rubyThread.killLock.notifyAll();
             }
         } catch (RaiseException e) {
             rubyThread.exceptionRaised(e);
         } finally {
             runtime.getThreadService().setCritical(false);
             runtime.getThreadService().unregisterThread(rubyThread);
             ((RubyThreadGroup)rubyThread.group()).remove(rubyThread);
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/RubyRunnable.java b/src/org/jruby/internal/runtime/RubyRunnable.java
index 3483f9c864..1fc6eda843 100644
--- a/src/org/jruby/internal/runtime/RubyRunnable.java
+++ b/src/org/jruby/internal/runtime/RubyRunnable.java
@@ -1,85 +1,85 @@
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
 package org.jruby.internal.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyProc;
 import org.jruby.RubyThread;
 import org.jruby.RubyThreadGroup;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyRunnable implements Runnable {
     private Ruby runtime;
     private Frame currentFrame;
     private RubyProc proc;
     private IRubyObject[] arguments;
     private RubyThread rubyThread;
     
     public RubyRunnable(RubyThread rubyThread, IRubyObject[] args, Block currentBlock) {
         this.rubyThread = rubyThread;
         this.runtime = rubyThread.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         
-        proc = runtime.newProc(false, currentBlock);
+        proc = runtime.newProc(Block.Type.PROC, currentBlock);
         currentFrame = tc.getCurrentFrame();
         this.arguments = args;
     }
     
     public RubyThread getRubyThread() {
         return rubyThread;
     }
     
     public void run() {
         runtime.getThreadService().registerNewThread(rubyThread);
         ThreadContext context = runtime.getCurrentContext();
         
         context.preRunThread(currentFrame);
         
         // Call the thread's code
         try {
             IRubyObject result = proc.call(arguments);
             rubyThread.cleanTerminate(result);
         } catch (ThreadKill tk) {
             // notify any killer waiting on our thread that we're going bye-bye
             synchronized (rubyThread.killLock) {
                 rubyThread.killLock.notifyAll();
             }
         } catch (RaiseException e) {
             rubyThread.exceptionRaised(e);
         } finally {
             runtime.getThreadService().setCritical(false);
             runtime.getThreadService().unregisterThread(rubyThread);
             ((RubyThreadGroup)rubyThread.group()).remove(rubyThread);
         }
     }
 }
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index f45a9eccd5..e8edc7df1f 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,808 +1,808 @@
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
 
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.util.concurrent.ConcurrentHashMap;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassProvider;
 
 public class Java {
     public static RubyModule createJavaModule(Ruby runtime) {
         RubyModule javaModule = runtime.defineModule("Java");
         CallbackFactory callbackFactory = runtime.callbackFactory(Java.class);
         javaModule.defineModuleFunction("define_exception_handler", callbackFactory.getOptSingletonMethod("define_exception_handler"));
         javaModule.defineModuleFunction("primitive_to_java", callbackFactory.getSingletonMethod("primitive_to_java", IRubyObject.class));
         javaModule.defineModuleFunction("java_to_primitive", callbackFactory.getSingletonMethod("java_to_primitive", IRubyObject.class));
         javaModule.defineModuleFunction("java_to_ruby", callbackFactory.getSingletonMethod("java_to_ruby", IRubyObject.class));
         javaModule.defineModuleFunction("ruby_to_java", callbackFactory.getSingletonMethod("ruby_to_java", IRubyObject.class));
         javaModule.defineModuleFunction("new_proxy_instance", callbackFactory.getOptSingletonMethod("new_proxy_instance"));
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         javaUtils.defineFastModuleFunction("wrap", callbackFactory.getFastSingletonMethod("wrap",IRubyObject.class));
         javaUtils.defineFastModuleFunction("valid_constant_name?", callbackFactory.getFastSingletonMethod("valid_constant_name_p",IRubyObject.class));
         javaUtils.defineFastModuleFunction("primitive_match", callbackFactory.getFastSingletonMethod("primitive_match",IRubyObject.class,IRubyObject.class));
         javaUtils.defineFastModuleFunction("access", callbackFactory.getFastSingletonMethod("access",IRubyObject.class));
         javaUtils.defineFastModuleFunction("matching_method", callbackFactory.getFastSingletonMethod("matching_method", IRubyObject.class, IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_deprecated_interface_proxy", callbackFactory.getFastSingletonMethod("get_deprecated_interface_proxy", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_interface_module", callbackFactory.getFastSingletonMethod("get_interface_module", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_package_module", callbackFactory.getFastSingletonMethod("get_package_module", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_package_module_dot_format", callbackFactory.getFastSingletonMethod("get_package_module_dot_format", IRubyObject.class));
         javaUtils.defineFastModuleFunction("get_proxy_class", callbackFactory.getFastSingletonMethod("get_proxy_class", IRubyObject.class));
 
         // Note: deprecated
         javaUtils.defineFastModuleFunction("add_proxy_extender", callbackFactory.getFastSingletonMethod("add_proxy_extender", IRubyObject.class));
 
         runtime.getJavaSupport().setConcreteProxyCallback(
                 callbackFactory.getFastSingletonMethod("concrete_proxy_inherited", IRubyObject.class));
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.getMetaClass().defineFastMethod("new_instance_for", callbackFactory.getFastSingletonMethod("new_instance_for", IRubyObject.class));
         javaProxy.getMetaClass().defineFastMethod("to_java_object", callbackFactory.getFastSingletonMethod("to_java_object"));
 
         return javaModule;
     }
 
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
         public RubyClass defineClassUnder(final RubyModule pkg, final String name, final RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             final IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.fastGetInstanceVariable("@package_name")) == null) return null;
 
             final Ruby runtime = pkg.getRuntime();
             return (RubyClass)get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forName(runtime, packageName.asSymbol() + name));
         }
         
         public RubyModule defineModuleUnder(final RubyModule pkg, final String name) {
             final IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.fastGetInstanceVariable("@package_name")) == null) return null;
 
             final Ruby runtime = pkg.getRuntime();
             return (RubyModule)get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forName(runtime, packageName.asSymbol() + name));
         }
     };
         
     // JavaProxy
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         IRubyObject new_instance = ((RubyClass)recv).allocate();
         new_instance.fastSetInstanceVariable("@java_object",java_object);
         return new_instance;
     }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.fastGetInstanceVariable("@java_class");
     }
 
     // JavaUtilities
     
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     @Deprecated
     public static IRubyObject add_proxy_extender(final IRubyObject recv, final IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn("JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead");
         final IRubyObject javaClassVar = extender.fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass)javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject get_interface_module(final IRubyObject recv, final IRubyObject javaClassObject) {
         final Ruby runtime = recv.getRuntime();
         final JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass)javaClassObject;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
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
                 interfaceModule = (RubyModule)runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class",javaClass);
                 addToJavaPackageModule(interfaceModule,javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 final Class[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0; ) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = (RubyModule)get_interface_module(recv, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(final IRubyObject recv, final IRubyObject javaClassObject) {
         final Ruby runtime = recv.getRuntime();
         final JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass)javaClassObject;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + javaClassObject);
         }
         RubyClass proxyClass;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if((proxyClass = javaClass.getProxyClass()) == null) {
                 final RubyModule interfaceModule = (RubyModule)get_interface_module(recv, javaClass);
                 RubyClass interfaceJavaProxy = runtime.fastGetClass("InterfaceJavaProxy");
                 proxyClass = RubyClass.newClass(runtime, interfaceJavaProxy);
                 proxyClass.setAllocator(interfaceJavaProxy.getAllocator());
                 proxyClass.makeMetaClass(interfaceJavaProxy.getMetaClass());
                 // parent.setConstant(name, proxyClass); // where the name should come from ?
                 proxyClass.inherit(interfaceJavaProxy);                
                 proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxyClass.includeModule(interfaceModule);
                 javaClass.setupProxy(proxyClass);
                 // add reference to interface module
                 if (proxyClass.fastGetConstantAt("Includable") == null) {
                     proxyClass.fastSetConstant("Includable", interfaceModule);
                 }
 
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(final IRubyObject recv, final IRubyObject java_class_object) {
         final Ruby runtime = recv.getRuntime();
         final JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass)java_class_object;
         } else  {
             throw runtime.newArgumentError("expected JavaClass, got " + java_class_object);
         }
         RubyClass proxyClass;
         final Class c;
         if ((c = javaClass.javaClass()).isInterface()) {
             return get_interface_module(recv,javaClass);
         }
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if(c.isArray()) {
                     proxyClass = createProxyClass(recv,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(recv,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(recv,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(recv,
                             get_proxy_class(recv,runtime.newString(c.getSuperclass().getName())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0; ) {
                         JavaClass ifc = JavaClass.get(runtime,interfaces[i]);
                         proxyClass.includeModule(get_interface_module(recv,ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     private static RubyClass createProxyClass(final IRubyObject recv, final IRubyObject baseType,
             final JavaClass javaClass, final boolean invokeInherited) {
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass)baseType;
         RubyClass proxyClass = RubyClass.newClass(recv.getRuntime(), superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) proxyClass.inherit(superClass);
 
         proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         recv.callMethod(tc,javaProxyClass, "inherited", new IRubyObject[]{subclass},
                 org.jruby.runtime.CallType.SUPER, Block.NULL_BLOCK);
         // TODO: move to Java
         return javaSupport.getJavaUtilitiesModule().callMethod(tc, "setup_java_subclass",
                 new IRubyObject[]{subclass, recv.callMethod(tc,"java_class")});
     }
     
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) return;
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0,endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className),proxyClass);
             }
         }
     }
     
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuffer buf = new StringBuffer();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start)))
                         .append(packageString.substring(start+1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule)packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(final RubyModule parent, final String name, final String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule)runtime.getJavaSupport()
                 .getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name",runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         return packageModule;
     }
     
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         String sym = symObject.asSymbol();
         RubyModule javaModule = recv.getRuntime().getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.fastGetConstantAt(sym)) != null) {
             return value;
         }
         String packageName;
         if ("Default".equals(sym)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(sym);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, sym, packageName);
     }
     
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asSymbol());
         return module == null ? runtime.getNil() : module;
     }
     
     
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List arg_types = new ArrayList();
         int alen = ((RubyArray)args).getLength();
         IRubyObject[] aargs = ((RubyArray)args).toJavaArrayMaybeUnsafe();
         for(int i=0;i<alen;i++) {
             if (aargs[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass)((JavaObject)aargs[i]).java_class()).getValue());
             } else {
                 arg_types.add(aargs[i].getClass());
             }
         }
 
         Map ms = (Map)matchCache.get(methods);
         if(ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject)ms.get(arg_types);
             if(method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray)methods).getLength();
         IRubyObject[] margs = ((RubyArray)methods).toJavaArrayMaybeUnsafe();
 
         for(int i=0;i<2;i++) {
             for(int k=0;k<mlen;k++) {
                 List types = null;
                 IRubyObject method = margs[k];
                 if(method instanceof JavaCallable) {
                     types = java.util.Arrays.asList(((JavaCallable)method).parameterTypes());
                 } else if(method instanceof JavaProxyMethod) {
                     types = java.util.Arrays.asList(((JavaProxyMethod)method).getParameterTypes());
                 } else if(method instanceof JavaProxyConstructor) {
                     types = java.util.Arrays.asList(((JavaProxyConstructor)method).getParameterTypes());
                 }
 
                 // Compatible (by inheritance)
                 if(arg_types.size() == types.size()) {
                     // Exact match
                     if(types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for(int j=0; j<types.size(); j++) {
                         if(!(JavaClass.assignable((Class)types.get(j),(Class)arg_types.get(j)) &&
                              (i > 0 || primitive_match(types.get(j),arg_types.get(j))))
                            && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), (Class)types.get(j))) {
                             match = false;
                             break;
                         }
                     }
                     if(match) {
                         ms.put(arg_types, method);
                         return method;
                     }
                 } // Could check for varargs here?
             }
         }
 
         Object o1 = margs[0];
 
         if(o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod)o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     public static IRubyObject matching_method_internal(IRubyObject recv, IRubyObject methods, IRubyObject[] args, int start, int len) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List arg_types = new ArrayList();
         int aend = start+len;
 
         for(int i=start;i<aend;i++) {
             if (args[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass)((JavaObject)args[i]).java_class()).getValue());
             } else {
                 arg_types.add(args[i].getClass());
             }
         }
 
         Map ms = (Map)matchCache.get(methods);
         if(ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject)ms.get(arg_types);
             if(method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray)methods).getLength();
         IRubyObject[] margs = ((RubyArray)methods).toJavaArrayMaybeUnsafe();
 
         mfor: for(int k=0;k<mlen;k++) {
             Class[] types = null;
             IRubyObject method = margs[k];
             if(method instanceof JavaCallable) {
                 types = ((JavaCallable)method).parameterTypes();
             } else if(method instanceof JavaProxyMethod) {
                 types = ((JavaProxyMethod)method).getParameterTypes();
             } else if(method instanceof JavaProxyConstructor) {
                 types = ((JavaProxyConstructor)method).getParameterTypes();
             }
             // Compatible (by inheritance)
             if(len == types.length) {
                 // Exact match
                 boolean same = true;
                 for(int x=0,y=len;x<y;x++) {
                     if(!types[x].equals(arg_types.get(x))) {
                         same = false;
                         break;
                     }
                 }
                 if(same) {
                     ms.put(arg_types, method);
                     return method;
                 }
                 
                 for(int j=0,m=len; j<m; j++) {
                     if(!(
                          JavaClass.assignable(types[j],(Class)arg_types.get(j)) &&
                          primitive_match(types[j],arg_types.get(j))
                          )) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         mfor: for(int k=0;k<mlen;k++) {
             Class[] types = null;
             IRubyObject method = margs[k];
             if(method instanceof JavaCallable) {
                 types = ((JavaCallable)method).parameterTypes();
             } else if(method instanceof JavaProxyMethod) {
                 types = ((JavaProxyMethod)method).getParameterTypes();
             } else if(method instanceof JavaProxyConstructor) {
                 types = ((JavaProxyConstructor)method).getParameterTypes();
             }
             // Compatible (by inheritance)
             if(len == types.length) {
                 for(int j=0,m=len; j<m; j++) {
                     if(!JavaClass.assignable(types[j],(Class)arg_types.get(j)) 
                         && !JavaUtil.isDuckTypeConvertable((Class)arg_types.get(j), types[j])) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         Object o1 = margs[0];
 
         if(o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod)o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     public static IRubyObject access(IRubyObject recv, IRubyObject java_type) {
         int modifiers = ((JavaClass)java_type).javaClass().getModifiers();
         return recv.getRuntime().newString(Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : "private"));
     }
 
     public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject name) {
         RubyString sname = name.convertToString();
         if(sname.getByteList().length() == 0) {
             return recv.getRuntime().getFalse();
         }
         return Character.isUpperCase(sname.getByteList().charAt(0)) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static boolean primitive_match(Object v1, Object v2) {
         if(((Class)v1).isPrimitive()) {
             if(v1 == Integer.TYPE || v1 == Long.TYPE || v1 == Short.TYPE || v1 == Character.TYPE) {
                 return v2 == Integer.class ||
                     v2 == Long.class ||
                     v2 == Short.class ||
                     v2 == Character.class;
             } else if(v1 == Float.TYPE || v1 == Double.TYPE) {
                 return v2 == Float.class ||
                     v2 == Double.class;
             } else if(v1 == Boolean.TYPE) {
                 return v2 == Boolean.class;
             }
             return false;
         }
         return true;
     }
 
     public static IRubyObject primitive_match(IRubyObject recv, IRubyObject t1, IRubyObject t2) {
         if(((JavaClass)t1).primitive_p().isTrue()) {
             Object v1 = ((JavaObject)t1).getValue();
             Object v2 = ((JavaObject)t2).getValue();
             return primitive_match(v1,v2) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
         }
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject wrap(IRubyObject recv, IRubyObject java_object) {
         return new_instance_for(get_proxy_class(recv, ((JavaObject)java_object).java_class()),java_object);
     }
 
 	// Java methods
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc)args[1];
         } else {
-            handler = recv.getRuntime().newProc(false, block);
+            handler = recv.getRuntime().newProc(Block.Type.PROC, block);
         }
         recv.getRuntime().getJavaSupport().defineExceptionHandler(name, handler);
 
         return recv;
     }
 
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = new Long(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
             try {
                 ByteList bytes = ((RubyString) object).getByteList();
                 javaObject = new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 javaObject = object.toString();
             }
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
         default:
             if (object instanceof RubyTime) {
                 javaObject = ((RubyTime)object).getJavaDate();
             } else {
                 javaObject = object;
             }
         }
         return JavaObject.wrap(runtime, javaObject);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if(object instanceof JavaObject) {
         	object = JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
 
             //if (object.isKindOf(recv.getRuntime().fastGetModule("Java").fastGetClass("JavaObject"))) {
             if(object instanceof JavaObject) {
                 return wrap(recv.getRuntime().getJavaSupport().getJavaUtilitiesModule(), object);
             }
         }
 
 		return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
     	if(object.respondsTo("to_java_object")) {
             IRubyObject result = object.fastGetInstanceVariable("@java_object");
             if(result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             return result;
     	}
     	
     	return primitive_to_java(recv, object, unusedBlock);
     }    
 
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
         	return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
 		return object;
     }
 
     public static IRubyObject new_proxy_instance(final IRubyObject recv, IRubyObject[] args, Block block) {
     	int size = Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1) - 1;
     	final RubyProc proc;
 
     	// Is there a supplied proc argument or do we assume a block was supplied
     	if (args[size] instanceof RubyProc) {
     		proc = (RubyProc) args[size];
     	} else {
-    		proc = recv.getRuntime().newProc(false, block);
+    		proc = recv.getRuntime().newProc(Block.Type.PROC, block);
     		size++;
     	}
     	
     	// Create list of interfaces to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[size];
         for (int i = 0; i < size; i++) {
             if (!(args[i] instanceof JavaClass) || !((JavaClass)args[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + args[i]);
             }
             interfaces[i] = ((JavaClass) args[i]).javaClass();
         }
         
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJavaSupport().getJavaClassLoader(), interfaces, new InvocationHandler() {
             private Map parameterTypeCache = new ConcurrentHashMap();
             public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                 Class[] parameterTypes = (Class[])parameterTypeCache.get(method);
                 if (parameterTypes == null) {
                     parameterTypes = method.getParameterTypes();
                     parameterTypeCache.put(method, parameterTypes);
                 }
             	int methodArgsLength = parameterTypes.length;
             	String methodName = method.getName();
             	
                 if (methodName.equals("toString") && methodArgsLength == 0) {
                     return proxy.getClass().getName();
                 } else if (methodName.equals("hashCode") && methodArgsLength == 0) {
                     return new Integer(proxy.getClass().hashCode());
                 } else if (methodName.equals("equals") && methodArgsLength == 1 && parameterTypes[0].equals(Object.class)) {
                     return Boolean.valueOf(proxy == nargs[0]);
                 }
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(recv.getRuntime(), proxy);
                 rubyArgs[1] = new JavaMethod(recv.getRuntime(), method);
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaObject.wrap(recv.getRuntime(), nargs[i]);
                 }
                 return JavaUtil.convertArgument(proc.call(rubyArgs), method.getReturnType());
             }
         }));
     }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 8847a7fa29..9b90f6e810 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,1386 +1,1386 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.IntHashMap;
 
 public class JavaClass extends JavaObject {
 
     private static class AssignedName {
         // to override an assigned name, the type must be less than
         // or equal to the assigned type. so a field name in a subclass
         // will override an alias in a superclass, but not a method.
         static final int RESERVED = 0;
         static final int METHOD = 1;
         static final int FIELD = 2;
         static final int PROTECTED_METHOD = 3;
         static final int WEAKLY_RESERVED = 4; // we'll be peeved, but not devastated, if you override
         static final int ALIAS = 5;
         // yes, protected fields are weaker than aliases. many conflicts
         // in the old AWT code, for example, where you really want 'size'
         // to mean the public method getSize, not the protected field 'size'.
         static final int PROTECTED_FIELD = 6;
         String name;
         int type;
         AssignedName () {}
         AssignedName(String name, int type) {
             this.name = name;
             this.type = type;
         }
     }
 
     // TODO: other reserved names?
     private static final Map RESERVED_NAMES = new HashMap();
     static {
         RESERVED_NAMES.put("__id__", new AssignedName("__id__", AssignedName.RESERVED));
         RESERVED_NAMES.put("__send__", new AssignedName("__send__", AssignedName.RESERVED));
         RESERVED_NAMES.put("class", new AssignedName("class", AssignedName.RESERVED));
         RESERVED_NAMES.put("initialize", new AssignedName("initialize", AssignedName.RESERVED));
         RESERVED_NAMES.put("object_id", new AssignedName("object_id", AssignedName.RESERVED));
         RESERVED_NAMES.put("private", new AssignedName("private", AssignedName.RESERVED));
         RESERVED_NAMES.put("protected", new AssignedName("protected", AssignedName.RESERVED));
         RESERVED_NAMES.put("public", new AssignedName("public", AssignedName.RESERVED));
 
         // weakly reserved names
         RESERVED_NAMES.put("id", new AssignedName("id", AssignedName.WEAKLY_RESERVED));
     }
     private static final Map STATIC_RESERVED_NAMES = new HashMap(RESERVED_NAMES);
     static {
         STATIC_RESERVED_NAMES.put("new", new AssignedName("new", AssignedName.RESERVED));
     }
     private static final Map INSTANCE_RESERVED_NAMES = new HashMap(RESERVED_NAMES);
 
     private static abstract class NamedCallback implements Callback {
         static final int STATIC_FIELD = 1;
         static final int STATIC_METHOD = 2;
         static final int INSTANCE_FIELD = 3;
         static final int INSTANCE_METHOD = 4;
         String name;
         int type;
         Visibility visibility = Visibility.PUBLIC;
         boolean isProtected;
         NamedCallback () {}
         NamedCallback (String name, int type) {
             this.name = name;
             this.type = type;
         }
         abstract void install(RubyClass proxy);
         // small hack to save a cast later on
         boolean hasLocalMethod() {
             return true;
         }
         boolean isPublic() {
             return visibility == Visibility.PUBLIC;
         }
         boolean isProtected() {
             return visibility == Visibility.PROTECTED;
         }
 //        void logMessage(IRubyObject self, IRubyObject[] args) {
 //            if (!DEBUG) {
 //                return;
 //            }
 //            String type;
 //            switch (this.type) {
 //            case STATIC_FIELD: type = "static field"; break;
 //            case STATIC_METHOD: type = "static method"; break;
 //            case INSTANCE_FIELD: type = "instance field"; break;
 //            case INSTANCE_METHOD: type = "instance method"; break;
 //            default: type = "?"; break;
 //            }
 //            StringBuffer b = new StringBuffer(type).append(" => '").append(name)
 //                .append("'; args.length = ").append(args.length);
 //            for (int i = 0; i < args.length; i++) {
 //                b.append("\n   arg[").append(i).append("] = ").append(args[i]);
 //            }
 //            System.out.println(b);
 //        }
     }
 
     private static abstract class FieldCallback extends NamedCallback {
         Field field;
         JavaField javaField;
         FieldCallback(){}
         FieldCallback(String name, int type, Field field) {
             super(name,type);
             this.field = field;
 //            if (Modifier.isProtected(field.getModifiers())) {
 //                field.setAccessible(true);
 //                this.visibility = Visibility.PROTECTED;
 //            }
         }
     }
 
     private class StaticFieldGetter extends FieldCallback {
         StaticFieldGetter(){}
         StaticFieldGetter(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.getSingletonClass().defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,javaField.static_value(),Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.NO_ARGUMENTS;
         }
     }
 
     private class StaticFieldSetter extends FieldCallback {
         StaticFieldSetter(){}
         StaticFieldSetter(String name, Field field) {
             super(name,STATIC_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.getSingletonClass().defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.set_static_value(Java.ruby_to_java(self,args[0],Block.NULL_BLOCK)),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     private class InstanceFieldGetter extends FieldCallback {
         InstanceFieldGetter(){}
         InstanceFieldGetter(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.value(self.fastGetInstanceVariable("@java_object")),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.NO_ARGUMENTS;
         }
     }
 
     private class InstanceFieldSetter extends FieldCallback {
         InstanceFieldSetter(){}
         InstanceFieldSetter(String name, Field field) {
             super(name,INSTANCE_FIELD,field);
         }
         void install(RubyClass proxy) {
             proxy.defineFastMethod(this.name,this,this.visibility);
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaField == null) {
                 javaField = new JavaField(getRuntime(),field);
             }
             return Java.java_to_ruby(self,
                     javaField.set_value(self.fastGetInstanceVariable("@java_object"),
                             Java.ruby_to_java(self,args[0],Block.NULL_BLOCK)),
                     Block.NULL_BLOCK);
         }
         public Arity getArity() {
             return Arity.ONE_ARGUMENT;
         }
     }
 
     private static abstract class MethodCallback extends NamedCallback {
         boolean haveLocalMethod;
         List methods;
         List aliases;
         IntHashMap javaMethods;
         IntHashMap matchingMethods;
         JavaMethod javaMethod;
         MethodCallback(){}
         MethodCallback(String name, int type) {
             super(name,type);
         }
         void addMethod(Method method, Class javaClass) {
             if (methods == null) {
                 methods = new ArrayList();
             }
             methods.add(method);
 //            if (Modifier.isProtected(method.getModifiers())) {
 //                visibility = Visibility.PROTECTED;
 //            }
             haveLocalMethod |= javaClass == method.getDeclaringClass();
         }
         void addAlias(String alias) {
             if (aliases == null) {
                 aliases = new ArrayList();
             }
             if (!aliases.contains(alias))
                 aliases.add(alias);
         }
         boolean hasLocalMethod () {
             return haveLocalMethod;
         }
         // TODO: varargs?
         // TODO: rework Java.matching_methods_internal and
         // ProxyData.method_cache, since we really don't need to be passing
         // around RubyArray objects anymore.
         void createJavaMethods(Ruby runtime) {
             if (methods != null) {
                 if (methods.size() == 1) {
                     javaMethod = JavaMethod.create(runtime,(Method)methods.get(0));
                 } else {
                     javaMethods = new IntHashMap();
                     matchingMethods = new IntHashMap(); 
                     for (Iterator iter = methods.iterator(); iter.hasNext() ;) {
                         Method method = (Method)iter.next();
                         // TODO: deal with varargs
                         //int arity = method.isVarArgs() ? -1 : method.getParameterTypes().length;
                         int arity = method.getParameterTypes().length;
                         RubyArray methodsForArity = (RubyArray)javaMethods.get(arity);
                         if (methodsForArity == null) {
                             methodsForArity = RubyArray.newArrayLight(runtime);
                             javaMethods.put(arity,methodsForArity);
                         }
                         methodsForArity.append(JavaMethod.create(runtime,method));
                     }
                 }
                 methods = null;
             }
         }
         void raiseNoMatchingMethodError(IRubyObject proxy, IRubyObject[] args, int start) {
             int len = args.length;
             List argTypes = new ArrayList(len - start);
             for (int i = start ; i < len; i++) {
                 argTypes.add(((JavaClass)((JavaObject)args[i]).java_class()).getValue());
             }
             throw proxy.getRuntime().newNameError("no " + this.name + " with arguments matching " + argTypes + " on object " + proxy.callMethod(proxy.getRuntime().getCurrentContext(),"inspect"), null);
         }
     }
 
     private class StaticMethodInvoker extends MethodCallback {
         StaticMethodInvoker(){}
         StaticMethodInvoker(String name) {
             super(name,STATIC_METHOD);
         }
         void install(RubyClass proxy) {
             if (haveLocalMethod) {
                 RubyClass singleton = proxy.getSingletonClass();
                 singleton.defineFastMethod(this.name,this,this.visibility);
                 if (aliases != null && isPublic() ) {
                     for (Iterator iter = aliases.iterator(); iter.hasNext(); ) {
                         singleton.defineAlias((String)iter.next(), this.name);
                     }
                     aliases = null;
                 }
             }
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaMethod == null && javaMethods == null) {
                 createJavaMethods(self.getRuntime());
             }
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             IRubyObject[] convertedArgs = new IRubyObject[len];
             for (int i = len; --i >= 0; ) {
                 convertedArgs[i] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
             if (javaMethods == null) {
                 return Java.java_to_ruby(self,javaMethod.invoke_static(convertedArgs),Block.NULL_BLOCK); 
             } else {
                 int argsTypeHash = 0;
                 for (int i = len; --i >= 0; ) {
                     argsTypeHash += 3*args[i].getMetaClass().id;
                 }
                 IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                 if (match == null) {
                     // TODO: varargs?
                     RubyArray methods = (RubyArray)javaMethods.get(len);
                     if (methods == null) {
                         raiseNoMatchingMethodError(self,convertedArgs,0);
                     }
                     match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 0, len);
                 }
                 return Java.java_to_ruby(self, ((JavaMethod)match).invoke_static(convertedArgs), Block.NULL_BLOCK);
             }
         }
         public Arity getArity() {
             return Arity.OPTIONAL;
         }
     }
 
     private class InstanceMethodInvoker extends MethodCallback {
         InstanceMethodInvoker(){}
         InstanceMethodInvoker(String name) {
             super(name,INSTANCE_METHOD);
         }
         void install(RubyClass proxy) {
             if (haveLocalMethod) {
                 proxy.defineFastMethod(this.name,this,this.visibility);
                 if (aliases != null && isPublic()) {
                     for (Iterator iter = aliases.iterator(); iter.hasNext(); ) {
                         proxy.defineAlias((String)iter.next(), this.name);
                     }
                     aliases = null;
                 }
             }
         }
         public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
             //logMessage(self,args);
             if (javaMethod == null && javaMethods == null) {
                 createJavaMethods(self.getRuntime());
             }
             // TODO: ok to convert args in place, rather than new array?
             int len = args.length;
             if (block.isGiven()) { // convert block to argument
                 len += 1;
                 IRubyObject[] newArgs = new IRubyObject[args.length+1];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
-                newArgs[args.length] = RubyProc.newProc(self.getRuntime(), block, true);
+                newArgs[args.length] = RubyProc.newProc(self.getRuntime(), block, Block.Type.LAMBDA);
                 args = newArgs;
             }
             IRubyObject[] convertedArgs = new IRubyObject[len+1];
             convertedArgs[0] = self.fastGetInstanceVariable("@java_object");
             int i = len;
             if (block.isGiven()) {
                 convertedArgs[len] = args[len - 1];
                 i -= 1;
             }
             for (; --i >= 0; ) {
                 convertedArgs[i+1] = Java.ruby_to_java(self,args[i],Block.NULL_BLOCK);
             }
 
             if (javaMethods == null) {
                 return Java.java_to_ruby(self,javaMethod.invoke(convertedArgs),Block.NULL_BLOCK);
             } else {
                 int argsTypeHash = 0;
                 for (i = len; --i >= 0; ) {
                     argsTypeHash += 3*args[i].getMetaClass().id;
                 }
                 IRubyObject match = (IRubyObject)matchingMethods.get(argsTypeHash);
                 if (match == null) {
                     // TODO: varargs?
                     RubyArray methods = (RubyArray)javaMethods.get(len);
                     if (methods == null) {
                         raiseNoMatchingMethodError(self,convertedArgs,1);
                     }
                     match = Java.matching_method_internal(JAVA_UTILITIES, methods, convertedArgs, 1, len);
                     matchingMethods.put(argsTypeHash, match);
                 }
                 return Java.java_to_ruby(self,((JavaMethod)match).invoke(convertedArgs),Block.NULL_BLOCK);
             }
         }
         public Arity getArity() {
             return Arity.OPTIONAL;
         }
     }
 
     private static class ConstantField {
         static final int CONSTANT = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;
         final Field field;
         ConstantField(Field field) {
             this.field = field;
         }
         void install(final RubyModule proxy) {
             if (proxy.fastGetConstantAt(field.getName()) == null) {
                 JavaField javaField = new JavaField(proxy.getRuntime(),field);
                 proxy.const_set(javaField.name(),Java.java_to_ruby(proxy,javaField.static_value(),Block.NULL_BLOCK));
             }
         }
         static boolean isConstant(final Field field) {
             return (field.getModifiers() & CONSTANT) == CONSTANT &&
                 Character.isUpperCase(field.getName().charAt(0));
         }
     }
     
     private final RubyModule JAVA_UTILITIES = getRuntime().getJavaSupport().getJavaUtilitiesModule();
     
     private Map staticAssignedNames;
     private Map instanceAssignedNames;
     private Map staticCallbacks;
     private Map instanceCallbacks;
     private List constantFields;
     // caching constructors, as they're accessed for each new instance
     private RubyArray constructors;
     
     private volatile ArrayList<IRubyObject> proxyExtenders;
 
     // proxy module for interfaces
     private volatile RubyModule proxyModule;
     
     // proxy class for concrete classes.  also used for
     // "concrete" interfaces, which is why we have two fields
     private volatile RubyClass proxyClass;
 
     // readable only by thread building proxy, so don't need to be
     // volatile. used to handle recursive calls to getProxyClass/Module
     // while proxy is being constructed (usually when a constant
     // defined by a class is of the same type as that class).
     private RubyModule unfinishedProxyModule;
     private RubyClass unfinishedProxyClass;
     
     private final ReentrantLock proxyLock = new ReentrantLock();
     
     public RubyModule getProxyModule() {
         // allow proxy to be read without synchronization. if proxy
         // is under construction, only the building thread can see it.
         RubyModule proxy;
         if ((proxy = proxyModule) != null) {
             // proxy is complete, return it
             return proxy;
         } else if (proxyLock.isHeldByCurrentThread()) {
             // proxy is under construction, building thread can
             // safely read non-volatile value
             return unfinishedProxyModule; 
         }
         return null;
     }
     
     public RubyClass getProxyClass() {
         // allow proxy to be read without synchronization. if proxy
         // is under construction, only the building thread can see it.
         RubyClass proxy;
         if ((proxy = proxyClass) != null) {
             // proxy is complete, return it
             return proxy;
         } else if (proxyLock.isHeldByCurrentThread()) {
             // proxy is under construction, building thread can
             // safely read non-volatile value
             return unfinishedProxyClass; 
         }
         return null;
     }
     
     public void lockProxy() {
         proxyLock.lock();
     }
     
     public void unlockProxy() {
         proxyLock.unlock();
     }
 
     protected Map getStaticAssignedNames() {
         return staticAssignedNames;
     }
     protected Map getInstanceAssignedNames() {
         return instanceAssignedNames;
     }
     
     private JavaClass(Ruby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
         if (javaClass.isInterface()) {
             initializeInterface(javaClass);
         } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
             // TODO: public only?
             initializeClass(javaClass);
         }
     }
     
     private void initializeInterface(Class javaClass) {
         Map staticNames  = new HashMap(STATIC_RESERVED_NAMES);
         List constantFields = new ArrayList(); 
         Field[] fields;
         try {
             fields = javaClass.getDeclaredFields();
         } catch (SecurityException e) {
             fields = javaClass.getFields();
         }
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
             }
         }
         this.staticAssignedNames = staticNames;
         this.constantFields = constantFields;
     }
 
     private void initializeClass(Class javaClass) {
         Class superclass = javaClass.getSuperclass();
         Map staticNames;
         Map instanceNames;
         if (superclass == null) {
             staticNames = new HashMap();
             instanceNames = new HashMap();
         } else {
             JavaClass superJavaClass = get(getRuntime(),superclass);
             staticNames = new HashMap(superJavaClass.getStaticAssignedNames());
             instanceNames = new HashMap(superJavaClass.getInstanceAssignedNames());
         }
         staticNames.putAll(STATIC_RESERVED_NAMES);
         instanceNames.putAll(INSTANCE_RESERVED_NAMES);
         Map staticCallbacks = new HashMap();
         Map instanceCallbacks = new HashMap();
         List constantFields = new ArrayList(); 
         Field[] fields = javaClass.getFields();
         for (int i = fields.length; --i >= 0; ) {
             Field field = fields[i];
             if (javaClass != field.getDeclaringClass()) continue;
 
             if (ConstantField.isConstant(field)) {
                 constantFields.add(new ConstantField(field));
                 continue;
             }
             String name = field.getName();
             int modifiers = field.getModifiers();
             if (Modifier.isStatic(modifiers)) {
                 AssignedName assignedName = (AssignedName)staticNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 staticNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 staticCallbacks.put(name,new StaticFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     staticCallbacks.put(setName,new StaticFieldSetter(setName,field));
                 }
             } else {
                 AssignedName assignedName = (AssignedName)instanceNames.get(name);
                 if (assignedName != null && assignedName.type < AssignedName.FIELD)
                     continue;
                 instanceNames.put(name,new AssignedName(name,AssignedName.FIELD));
                 instanceCallbacks.put(name,new InstanceFieldGetter(name,field));
                 if (!Modifier.isFinal(modifiers)) {
                     String setName = name + '=';
                     instanceCallbacks.put(setName,new InstanceFieldSetter(setName,field));
                 }
             }
         }
         // TODO: protected methods.  this is going to require a rework 
         // of some of the mechanism.  
         Method[] methods = javaClass.getMethods();
         for (int i = methods.length; --i >= 0; ) {
             // we need to collect all methods, though we'll only
             // install the ones that are named in this class
             Method method = methods[i];
             String name = method.getName();
             if (Modifier.isStatic(method.getModifiers())) {
                 AssignedName assignedName = (AssignedName)staticNames.get(name);
                 if (assignedName == null) {
                     staticNames.put(name,new AssignedName(name,AssignedName.METHOD));
                 } else {
                     if (assignedName.type < AssignedName.METHOD)
                         continue;
                     if (assignedName.type != AssignedName.METHOD) {
                         staticCallbacks.remove(name);
                         staticCallbacks.remove(name+'=');
                         staticNames.put(name,new AssignedName(name,AssignedName.METHOD));
                     }
                 }
                 StaticMethodInvoker invoker = (StaticMethodInvoker)staticCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new StaticMethodInvoker(name);
                     staticCallbacks.put(name,invoker);
                 }
                 invoker.addMethod(method,javaClass);
             } else {
                 AssignedName assignedName = (AssignedName)instanceNames.get(name);
                 if (assignedName == null) {
                     instanceNames.put(name,new AssignedName(name,AssignedName.METHOD));
                 } else {
                     if (assignedName.type < AssignedName.METHOD)
                         continue;
                     if (assignedName.type != AssignedName.METHOD) {
                         instanceCallbacks.remove(name);
                         instanceCallbacks.remove(name+'=');
                         instanceNames.put(name,new AssignedName(name,AssignedName.METHOD));
                     }
                 }
                 InstanceMethodInvoker invoker = (InstanceMethodInvoker)instanceCallbacks.get(name);
                 if (invoker == null) {
                     invoker = new InstanceMethodInvoker(name);
                     instanceCallbacks.put(name,invoker);
                 }
                 invoker.addMethod(method,javaClass);
             }
         }
         this.staticAssignedNames = staticNames;
         this.instanceAssignedNames = instanceNames;
         this.staticCallbacks = staticCallbacks;
         this.instanceCallbacks = instanceCallbacks;
         this.constantFields = constantFields;
     }
     
     public void setupProxy(final RubyClass proxy) {
         assert proxyLock.isHeldByCurrentThread();
         proxy.defineFastMethod("__jsend!", __jsend_method);
         final Class javaClass = javaClass();
         if (javaClass.isInterface()) {
             setupInterfaceProxy(proxy);
             return;
         }
         assert this.proxyClass == null;
         this.unfinishedProxyClass = proxy;
         if (javaClass.isArray() || javaClass.isPrimitive()) {
             // see note below re: 2-field kludge
             this.proxyClass = proxy;
             this.proxyModule = proxy;
             return;
         }
 
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ) {
             ((ConstantField)iter.next()).install(proxy);
         }
         for (Iterator iter = staticCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = (NamedCallback)iter.next();
             if (callback.type == NamedCallback.STATIC_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,staticAssignedNames);
             }
             callback.install(proxy);
         }
         for (Iterator iter = instanceCallbacks.values().iterator(); iter.hasNext(); ) {
             NamedCallback callback = (NamedCallback)iter.next();
             if (callback.type == NamedCallback.INSTANCE_METHOD && callback.hasLocalMethod()) {
                 assignAliases((MethodCallback)callback,instanceAssignedNames);
             }
             callback.install(proxy);
         }
         // setup constants for public inner classes
         Class[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class clazz = classes[i];
                 String simpleName = getSimpleName(clazz);
                 
                 if (simpleName.length() == 0) continue;
                 
                 // Ignore bad constant named inner classes pending JRUBY-697
                 if (IdUtil.isConstant(simpleName) && proxy.getConstantAt(simpleName) == null) {
                     proxy.setConstant(simpleName,
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
         // FIXME: bit of a kludge here (non-interface classes assigned to both
         // class and module fields). simplifies proxy extender code, will go away
         // when JI is overhauled (and proxy extenders are deprecated).
         this.proxyClass = proxy;
         this.proxyModule = proxy;
 
         applyProxyExtenders();
 
         // TODO: we can probably release our references to the constantFields
         // array and static/instance callback hashes at this point. 
     }
 
     private static void assignAliases(MethodCallback callback, Map assignedNames) {
         String name = callback.name;
         addUnassignedAlias(getRubyCasedName(name),assignedNames,callback);
         // logic adapted from java.beans.Introspector
         if (!(name.length() > 3 || name.startsWith("is")))
             return;
 
         String javaPropertyName = getJavaPropertyName(name);
         if (javaPropertyName == null)
             return; // not a Java property name, done with this method
 
         for (Iterator iter = callback.methods.iterator(); iter.hasNext(); ) {
             Method method = (Method)iter.next();
             Class[] argTypes = method.getParameterTypes();
             Class resultType = method.getReturnType();
             int argCount = argTypes.length;
             if (argCount == 0) {
                 if (name.startsWith("get")) {
                     addUnassignedAlias(getRubyCasedName(javaPropertyName),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == boolean.class && name.startsWith("is")) {
                     String rubyName = getRubyCasedName(name.substring(2));
                     if (rubyName != null) {
                         addUnassignedAlias(rubyName,assignedNames,callback);
                         addUnassignedAlias(rubyName+'?',assignedNames,callback);
                     }
                     if (!javaPropertyName.equals(rubyName)) {
                         addUnassignedAlias(javaPropertyName,assignedNames,callback);
                         addUnassignedAlias(javaPropertyName+'?',assignedNames,callback);
                     }
                 }
             } else if (argCount == 1) {
                 // indexed get
                 if (argTypes[0] == int.class && name.startsWith("get")) {
                     addUnassignedAlias(getRubyCasedName(name.substring(3)),assignedNames,callback);
                     addUnassignedAlias(javaPropertyName,assignedNames,callback);
                 } else if (resultType == void.class && name.startsWith("set")) {
                     String rubyName = getRubyCasedName(name.substring(3));
                     if (rubyName != null) {
                         addUnassignedAlias(rubyName + '=',assignedNames,callback);
                     }
                     if (!javaPropertyName.equals(rubyName)) {
                         addUnassignedAlias(javaPropertyName + '=',assignedNames,callback);
                     }
                 }
             }
         }
     }
     
     private static void addUnassignedAlias(String name, Map assignedNames,
             MethodCallback callback) {
         if (name != null) {
             AssignedName assignedName = (AssignedName)assignedNames.get(name);
             if (assignedName == null) {
                 callback.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             } else if (assignedName.type == AssignedName.ALIAS) {
                 callback.addAlias(name);
             } else if (assignedName.type > AssignedName.ALIAS) {
                 // TODO: there will be some additional logic in this branch
                 // dealing with conflicting protected fields. 
                 callback.addAlias(name);
                 assignedNames.put(name,new AssignedName(name,AssignedName.ALIAS));
             }
         }
     }
 
     private static final Pattern JAVA_PROPERTY_CHOPPER = Pattern.compile("(get|set|is)([A-Z0-9])(.*)");
     public static String getJavaPropertyName(String beanMethodName) {
         Matcher m = JAVA_PROPERTY_CHOPPER.matcher(beanMethodName);
 
         if (!m.find()) return null;
         String javaPropertyName = m.group(2).toLowerCase() + m.group(3);
         return javaPropertyName;
     }
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");    
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         return rubyCasedName;
     }
     
     
     // old (quasi-deprecated) interface class
     private void setupInterfaceProxy(final RubyClass proxy) {
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyClass == null;
         this.proxyClass = proxy;
         // nothing else to here - the module version will be
         // included in the class.
     }
     
     public void setupInterfaceModule(final RubyModule module) {
         assert javaClass().isInterface();
         assert proxyLock.isHeldByCurrentThread();
         assert this.proxyModule == null;
         this.unfinishedProxyModule = module;
         final Class javaClass = javaClass();
         for (Iterator iter = constantFields.iterator(); iter.hasNext(); ){
             ((ConstantField)iter.next()).install(module);
         }
         // setup constants for public inner classes
         final Class[] classes = javaClass.getClasses();
         for (int i = classes.length; --i >= 0; ) {
             if (javaClass == classes[i].getDeclaringClass()) {
                 Class clazz = classes[i];
                 String simpleName = getSimpleName(clazz);
                 if (simpleName.length() == 0) continue;
                 
                 // Ignore bad constant named inner classes pending JRUBY-697
                 if (IdUtil.isConstant(simpleName) && module.getConstantAt(simpleName) == null) {
                     module.const_set(getRuntime().newString(simpleName),
                         Java.get_proxy_class(JAVA_UTILITIES,get(getRuntime(),clazz)));
                 }
             }
         }
         
         this.proxyModule = module;
         applyProxyExtenders();
     }
 
     public void addProxyExtender(final IRubyObject extender) {
         lockProxy();
         try {
             if (!extender.respondsTo("extend_proxy")) {
                 throw getRuntime().newTypeError("proxy extender must have an extend_proxy method");
             }
             if (proxyModule == null) {
                 if (proxyExtenders == null) {
                     proxyExtenders = new ArrayList<IRubyObject>();
                 }
                 proxyExtenders.add(extender);
             } else {
                 getRuntime().getWarnings().warn(" proxy extender added after proxy class created for " + this);
                 extendProxy(extender);
             }
         } finally {
             unlockProxy();
         }
     }
     
     private void applyProxyExtenders() {
         ArrayList<IRubyObject> extenders;
         if ((extenders = proxyExtenders) != null) {
             for (IRubyObject extender : extenders) {
                 extendProxy(extender);
             }
             proxyExtenders = null;
         }
     }
 
     private void extendProxy(final IRubyObject extender) {
         extender.callMethod(getRuntime().getCurrentContext(), "extend_proxy", proxyModule);
     }
     
     public IRubyObject extend_proxy(final IRubyObject extender) {
         addProxyExtender(extender);
         return getRuntime().getNil();
     }
     
     public static JavaClass get(final Ruby runtime, final Class klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = createJavaClass(runtime,klass);
         }
         return javaClass;
     }
 
     private static synchronized JavaClass createJavaClass(final Ruby runtime, final Class klass) {
         // double-check the cache now that we're synchronized
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = new JavaClass(runtime, klass);
             runtime.getJavaSupport().putJavaClassIntoCache(javaClass);
         }
         return javaClass;
     }
 
     public static RubyClass createJavaClassClass(Ruby runtime, RubyModule javaModule) {
         // FIXME: Determine if a real allocator is needed here. Do people want to extend
         // JavaClass? Do we want them to do that? Can you Class.new(JavaClass)? Should
         // you be able to?
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.fastGetClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
 
     	CallbackFactory callbackFactory = runtime.callbackFactory(JavaClass.class);
         
         result.includeModule(runtime.fastGetModule("Comparable"));
         
         JavaObject.registerRubyMethods(runtime, result);
 
         result.getMetaClass().defineFastMethod("for_name", 
                 callbackFactory.getFastSingletonMethod("for_name", IRubyObject.class));
         result.defineFastMethod("public?", 
                 callbackFactory.getFastMethod("public_p"));
         result.defineFastMethod("protected?", 
                 callbackFactory.getFastMethod("protected_p"));
         result.defineFastMethod("private?", 
                 callbackFactory.getFastMethod("private_p"));
         result.defineFastMethod("final?", 
                 callbackFactory.getFastMethod("final_p"));
         result.defineFastMethod("interface?", 
                 callbackFactory.getFastMethod("interface_p"));
         result.defineFastMethod("array?", 
                 callbackFactory.getFastMethod("array_p"));
         result.defineFastMethod("name", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("simple_name",
                 callbackFactory.getFastMethod("simple_name"));
         result.defineFastMethod("to_s", 
                 callbackFactory.getFastMethod("name"));
         result.defineFastMethod("superclass", 
                 callbackFactory.getFastMethod("superclass"));
         result.defineFastMethod("<=>", 
                 callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
         result.defineFastMethod("java_instance_methods", 
                 callbackFactory.getFastMethod("java_instance_methods"));
         result.defineFastMethod("java_class_methods", 
                 callbackFactory.getFastMethod("java_class_methods"));
         result.defineFastMethod("java_method", 
                 callbackFactory.getFastOptMethod("java_method"));
         result.defineFastMethod("constructors", 
                 callbackFactory.getFastMethod("constructors"));
         result.defineFastMethod("constructor", 
                 callbackFactory.getFastOptMethod("constructor"));
         result.defineFastMethod("array_class", 
                 callbackFactory.getFastMethod("array_class"));
         result.defineFastMethod("new_array", 
                 callbackFactory.getFastMethod("new_array", IRubyObject.class));
         result.defineFastMethod("fields", 
                 callbackFactory.getFastMethod("fields"));
         result.defineFastMethod("field", 
                 callbackFactory.getFastMethod("field", IRubyObject.class));
         result.defineFastMethod("interfaces", 
                 callbackFactory.getFastMethod("interfaces"));
         result.defineFastMethod("primitive?", 
                 callbackFactory.getFastMethod("primitive_p"));
         result.defineFastMethod("assignable_from?", 
                 callbackFactory.getFastMethod("assignable_from_p", IRubyObject.class));
         result.defineFastMethod("component_type", 
                 callbackFactory.getFastMethod("component_type"));
         result.defineFastMethod("declared_instance_methods", 
                 callbackFactory.getFastMethod("declared_instance_methods"));
         result.defineFastMethod("declared_class_methods", 
                 callbackFactory.getFastMethod("declared_class_methods"));
         result.defineFastMethod("declared_fields", 
                 callbackFactory.getFastMethod("declared_fields"));
         result.defineFastMethod("declared_field", 
                 callbackFactory.getFastMethod("declared_field", IRubyObject.class));
         result.defineFastMethod("declared_constructors", 
                 callbackFactory.getFastMethod("declared_constructors"));
         result.defineFastMethod("declared_constructor", 
                 callbackFactory.getFastOptMethod("declared_constructor"));
         result.defineFastMethod("declared_classes", 
                 callbackFactory.getFastMethod("declared_classes"));
         result.defineFastMethod("declared_method", 
                 callbackFactory.getFastOptMethod("declared_method"));
 
         result.defineFastMethod("extend_proxy", 
                 callbackFactory.getFastMethod("extend_proxy", IRubyObject.class));
 
         result.getMetaClass().undefineMethod("new");
         result.getMetaClass().undefineMethod("allocate");
 
         return result;
     }
     
     public static synchronized JavaClass forName(Ruby runtime, String className) {
         Class klass = runtime.getJavaSupport().loadJavaClass(className);
         return JavaClass.get(runtime, klass);
     }
 
     public static JavaClass for_name(IRubyObject recv, IRubyObject name) {
         return forName(recv.getRuntime(), name.asSymbol());
     }
     
     private static final Callback __jsend_method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 String name = args[0].asSymbol();
                 
                 DynamicMethod method = self.getMetaClass().searchMethod(name);
                 int v = method.getArity().getValue();
                 
                 IRubyObject[] newArgs = new IRubyObject[args.length - 1];
                 System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
                 if(v < 0 || v == (newArgs.length)) {
                     return self.callMethod(self.getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
                 } else {
                     return self.callMethod(self.getRuntime().getCurrentContext(),self.getMetaClass().getSuperClass(), name, newArgs, CallType.SUPER, block);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(javaClass().getModifiers()));
     }
 
     public RubyBoolean protected_p() {
         return getRuntime().newBoolean(Modifier.isProtected(javaClass().getModifiers()));
     }
 
     public RubyBoolean private_p() {
         return getRuntime().newBoolean(Modifier.isPrivate(javaClass().getModifiers()));
     }
 
 	public Class javaClass() {
 		return (Class) getValue();
 	}
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(javaClass().getModifiers()));
     }
 
     public RubyBoolean interface_p() {
         return getRuntime().newBoolean(javaClass().isInterface());
     }
 
     public RubyBoolean array_p() {
         return getRuntime().newBoolean(javaClass().isArray());
     }
     
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
     
 
     private static String getSimpleName(Class class_) {
  		if (class_.isArray()) {
  			return getSimpleName(class_.getComponentType()) + "[]";
  		}
  
  		String className = class_.getName();
  
         int i = className.lastIndexOf('$');
  		if (i != -1) {
             do {
  				i++;
  			} while (i < className.length() && Character.isDigit(className.charAt(i)));
  			return className.substring(i);
  		}
  
  		return className.substring(className.lastIndexOf('.') + 1);
  	}
 
     public RubyString simple_name() {
         return getRuntime().newString(getSimpleName(javaClass()));
     }
 
     public IRubyObject superclass() {
         Class superclass = javaClass().getSuperclass();
         if (superclass == null) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), superclass);
     }
 
     public RubyFixnum op_cmp(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("<=> requires JavaClass (" + other.getType() + " given)");
         }
         JavaClass otherClass = (JavaClass) other;
         if (this.javaClass() == otherClass.javaClass()) {
             return getRuntime().newFixnum(0);
         }
         if (otherClass.javaClass().isAssignableFrom(this.javaClass())) {
             return getRuntime().newFixnum(-1);
         }
         return getRuntime().newFixnum(1);
     }
 
     public RubyArray java_instance_methods() {
         return java_methods(javaClass().getMethods(), false);
     }
 
     public RubyArray declared_instance_methods() {
         return java_methods(javaClass().getDeclaredMethods(), false);
     }
 
 	private RubyArray java_methods(Method[] methods, boolean isStatic) {
         RubyArray result = getRuntime().newArray(methods.length);
         for (int i = 0; i < methods.length; i++) {
             Method method = methods[i];
             if (isStatic == Modifier.isStatic(method.getModifiers())) {
                 result.append(JavaMethod.create(getRuntime(), method));
             }
         }
         return result;
 	}
 
 	public RubyArray java_class_methods() {
 	    return java_methods(javaClass().getMethods(), true);
     }
 
 	public RubyArray declared_class_methods() {
 	    return java_methods(javaClass().getDeclaredMethods(), true);
     }
 
 	public JavaMethod java_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     private Class[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         Class[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type = for_name(this, args[i]);
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     public RubyArray constructors() {
         if (constructors == null) {
             constructors = buildConstructors(javaClass().getConstructors());
         }
         return constructors;
     }
     
     public RubyArray declared_classes() {
         Ruby runtime = getRuntime();
         RubyArray result = runtime.newArray();
         Class javaClass = javaClass();
         try {
             Class[] classes = javaClass.getDeclaredClasses();
             for (int i = 0; i < classes.length; i++) {
                 if (Modifier.isPublic(classes[i].getModifiers())) {
                     result.append(get(runtime, classes[i]));
                 }
             }
         } catch (SecurityException e) {
             // restrictive security policy; no matter, we only want public
             // classes anyway
             try {
                 Class[] classes = javaClass.getClasses();
                 for (int i = 0; i < classes.length; i++) {
                     if (javaClass == classes[i].getDeclaringClass()) {
                         result.append(get(runtime, classes[i]));
                     }
                 }
             } catch (SecurityException e2) {
                 // very restrictive policy (disallows Member.PUBLIC)
                 // we'd never actually get this far in that case
             }
         }
         return result;
     }
 
     public RubyArray declared_constructors() {
         return buildConstructors(javaClass().getDeclaredConstructors());
     }
 
     private RubyArray buildConstructors(Constructor[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
             throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class[] buildClassArgs(IRubyObject[] args) {
         Class[] parameterTypes = new Class[args.length];
         for (int i = 0; i < args.length; i++) {
             String name = args[i].asSymbol();
             parameterTypes[i] = getRuntime().getJavaSupport().loadJavaClass(name);
         }
         return parameterTypes;
     }
 
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
    
     public JavaObject new_array(IRubyObject lengthArgument) {
         if (lengthArgument instanceof RubyInteger) {
             // one-dimensional array
         int length = (int) ((RubyInteger) lengthArgument).getLongValue();
         return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
         } else if (lengthArgument instanceof RubyArray) {
             // n-dimensional array
             List list = ((RubyArray)lengthArgument).getList();
             int length = list.size();
             if (length == 0) {
                 throw getRuntime().newArgumentError("empty dimensions specifier for java array");
     }
             int[] dimensions = new int[length];
             for (int i = length; --i >= 0; ) {
                 IRubyObject dimensionLength = (IRubyObject)list.get(i);
                 if ( !(dimensionLength instanceof RubyInteger) ) {
                     throw getRuntime()
                         .newTypeError(dimensionLength, getRuntime().getInteger());
                 }
                 dimensions[i] = (int) ((RubyInteger) dimensionLength).getLongValue();
             }
             return new JavaArray(getRuntime(), Array.newInstance(javaClass(), dimensions));
         } else {
             throw getRuntime().newArgumentError(
                   "invalid length or dimensions specifier for java array" +
                   " - must be Integer or Array of Integer");
         }
     }
 
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
     public RubyArray declared_fields() {
         return buildFieldResults(javaClass().getDeclaredFields());
     }
     
 	private RubyArray buildFieldResults(Field[] fields) {
         RubyArray result = getRuntime().newArray(fields.length);
         for (int i = 0; i < fields.length; i++) {
             result.append(new JavaField(getRuntime(), fields[i]));
         }
         return result;
 	}
 
 	public JavaField field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
 	public JavaField declared_field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getDeclaredField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
             throw undefinedFieldError(stringName);
         }
     }
 
     private RaiseException undefinedFieldError(String name) {
         return getRuntime().newNameError("undefined field '" + name + "' for class '" + javaClass().getName() + "'", name);
     }
 
     public RubyArray interfaces() {
         Class[] interfaces = javaClass().getInterfaces();
         RubyArray result = getRuntime().newArray(interfaces.length);
         for (int i = 0; i < interfaces.length; i++) {
             result.append(JavaClass.get(getRuntime(), interfaces[i]));
         }
         return result;
     }
 
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class otherClass = ((JavaClass) other).javaClass();
         return assignable(javaClass(), otherClass) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     static boolean assignable(Class thisClass, Class otherClass) {
         if(!thisClass.isPrimitive() && otherClass == Void.TYPE ||
             thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
 
         otherClass = JavaUtil.primitiveToWrapper(otherClass);
         thisClass = JavaUtil.primitiveToWrapper(thisClass);
 
         if(thisClass.isAssignableFrom(otherClass)) {
             return true;
         }
         if(Number.class.isAssignableFrom(thisClass)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
             if(otherClass.equals(Character.class)) {
                 return true;
             }
         }
         if(thisClass.equals(Character.class)) {
             if(Number.class.isAssignableFrom(otherClass)) {
                 return true;
             }
         }
         return false;
     }
 
     private boolean isPrimitive() {
         return javaClass().isPrimitive();
     }
 
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
     
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
index 418a42fff6..08359e5ade 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyConstructor.java
@@ -1,254 +1,254 @@
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
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 
 package org.jruby.javasupport.proxy;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaProxyConstructor extends JavaProxyReflectionObject {
 
     private final Constructor proxyConstructor;
     private final Class[] parameterTypes;
 
     private final JavaProxyClass declaringProxyClass;
 
     JavaProxyConstructor(Ruby runtime, JavaProxyClass pClass,
             Constructor constructor) {
         super(runtime, runtime.getJavaSupport().getJavaModule().fastGetClass(
                 "JavaProxyConstructor"));
         this.declaringProxyClass = pClass;
         this.proxyConstructor = constructor;
         this.parameterTypes = proxyConstructor.getParameterTypes();
     }
 
     public Class[] getParameterTypes() {
         Class[] result = new Class[parameterTypes.length - 1];
         System.arraycopy(parameterTypes, 0, result, 0, result.length);
         return result;
     }
 
     public JavaProxyClass getDeclaringClass() {
         return declaringProxyClass;
     }
 
     public Object newInstance(Object[] args, JavaProxyInvocationHandler handler)
             throws IllegalArgumentException, InstantiationException,
             IllegalAccessException, InvocationTargetException {
         if (args.length + 1 != parameterTypes.length) {
             throw new IllegalArgumentException("wrong number of parameters");
         }
 
         Object[] realArgs = new Object[args.length + 1];
         System.arraycopy(args, 0, realArgs, 0, args.length);
         realArgs[args.length] = handler;
 
         return proxyConstructor.newInstance(realArgs);
     }
 
     public static RubyClass createJavaProxyConstructorClass(Ruby runtime,
             RubyModule javaProxyModule) {
         RubyClass result = javaProxyModule.defineClassUnder(
                                                             "JavaProxyConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         CallbackFactory callbackFactory = runtime
                 .callbackFactory(JavaProxyConstructor.class);
 
         JavaProxyReflectionObject.registerRubyMethods(runtime, result);
 
         result.defineFastMethod("argument_types", callbackFactory
                 .getFastMethod("argument_types"));
 
         result.defineFastMethod("declaring_class", callbackFactory
                 .getFastMethod("getDeclaringClass"));
 
         result.defineMethod("new_instance", callbackFactory
                 .getOptMethod("new_instance"));
         
         result.defineMethod("new_instance2", callbackFactory.getOptMethod("new_instance2"));
 
         result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
 
         return result;
 
     }
 
     public RubyFixnum arity() {
         return getRuntime().newFixnum(getParameterTypes().length);
     }
 
     protected String nameOnInspection() {
         return getDeclaringClass().nameOnInspection();
     }
 
     public IRubyObject inspect() {
         StringBuffer result = new StringBuffer();
         result.append(nameOnInspection());
         Class[] parameterTypes = getParameterTypes();
         for (int i = 0; i < parameterTypes.length; i++) {
             result.append(parameterTypes[i].getName());
             if (i < parameterTypes.length - 1) {
                 result.append(',');
             }
         }
         result.append(")>");
         return getRuntime().newString(result.toString());
     }
 
     public RubyArray argument_types() {
         return buildRubyArray(getParameterTypes());
     }
     
     public RubyObject new_instance2(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 2, 2);
 
         final IRubyObject self = args[0];
         final Ruby runtime = self.getRuntime();
         final RubyModule javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         RubyArray constructor_args = (RubyArray) args[1];
         Class[] parameterTypes = getParameterTypes();
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(new IRubyObject[] { getRuntime().newFixnum(i) });
             converted[i] = JavaUtil.convertArgument(Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
             public Object invoke(Object proxy, JavaProxyMethod m, Object[] nargs) throws Throwable {
                 String name = m.getName();
                 DynamicMethod method = self.getMetaClass().searchMethod(name);
                 int v = method.getArity().getValue();
                 IRubyObject[] newArgs = new IRubyObject[nargs.length];
                 for (int i = nargs.length; --i >= 0; ) {
                     newArgs[i] = Java.java_to_ruby(
                             javaUtilities,
                             JavaObject.wrap(runtime, nargs[i]),
                             Block.NULL_BLOCK);
                 }
                 
                 if (v < 0 || v == (newArgs.length)) {
                     return JavaUtil.convertRubyToJava(self.callMethod(runtime.getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, Block.NULL_BLOCK), m.getReturnType());
                 } else {
                     return JavaUtil.convertRubyToJava(self.callMethod(runtime.getCurrentContext(),self.getMetaClass().getSuperClass(), name, newArgs, CallType.SUPER, Block.NULL_BLOCK), m.getReturnType());
                 }
             }
         };
 
         try {
             return JavaObject.wrap(getRuntime(), newInstance(converted, handler));
         } catch (Exception e) {
             RaiseException ex = getRuntime().newArgumentError(
                     "Constructor invocation failed: " + e.getMessage());
             ex.initCause(e);
             throw ex;
         }
     }
 
     public RubyObject new_instance(IRubyObject[] args, Block block) {
         int size = Arity.checkArgumentCount(getRuntime(), args, 1, 2) - 1;
         final RubyProc proc;
 
         // Is there a supplied proc argument or do we assume a block was
         // supplied
         if (args[size] instanceof RubyProc) {
             proc = (RubyProc) args[size];
         } else {
-            proc = getRuntime().newProc(false,block);
+            proc = getRuntime().newProc(Block.Type.PROC,block);
             size++;
         }
 
         RubyArray constructor_args = (RubyArray) args[0];
         Class[] parameterTypes = getParameterTypes();
 
         int count = (int) constructor_args.length().getLongValue();
         Object[] converted = new Object[count];
         for (int i = 0; i < count; i++) {
             // TODO: call ruby method
             IRubyObject ith = constructor_args.aref(new IRubyObject[] { getRuntime().newFixnum(i) });
             converted[i] = JavaUtil.convertArgument(Java.ruby_to_java(this, ith, Block.NULL_BLOCK), parameterTypes[i]);
         }
 
         final IRubyObject recv = this;
 
         JavaProxyInvocationHandler handler = new JavaProxyInvocationHandler() {
 
             public Object invoke(Object proxy, JavaProxyMethod method,
                     Object[] nargs) throws Throwable {
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(recv.getRuntime(), proxy);
                 rubyArgs[1] = method;
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaUtil.convertJavaToRuby(getRuntime(),
                             nargs[i]);
                 }
                 IRubyObject call_result = proc.call(rubyArgs);
                 Object converted_result = JavaUtil.convertRubyToJava(
                         call_result, method.getReturnType());
                 return converted_result;
             }
 
         };
 
         Object result;
         try {
             result = newInstance(converted, handler);
         } catch (Exception e) {
             RaiseException ex = getRuntime().newArgumentError(
                     "Constructor invocation failed: " + e.getMessage());
             ex.initCause(e);
             throw ex;
         }
 
         return JavaObject.wrap(getRuntime(), result);
 
     }
 
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index d8bd0ac66d..f36de082dd 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,803 +1,803 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SimpleSourcePosition;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.regexp.PatternSyntaxException;
 import org.jruby.regexp.RegexpFactory;
 import org.jruby.regexp.RegexpPattern;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static CompiledBlock createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         CompiledBlock block = new CompiledBlock(
                     context,
                     self,
                     Arity.createArity(arity),
                     new DynamicScope(staticScope, context.getCurrentScope()),
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         block.setLight(light);
         
         return block;
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(new DynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = new CompiledBlock(context, self, Arity.createArity(0), 
                 context.getCurrentScope(), callback, false, Block.ZERO_ARGS);
         
         block.yield(context, null);
         
         context.postScopedBody();
         
         return context.getRuntime().getNil();
     }
     
     public static CompiledSharedScopeBlock createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return new CompiledSharedScopeBlock(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
 
         if (name == "__id__" || name == "__send__") {
             runtime.getWarnings().warn("redefining `" + name + "' may cause serious problem"); 
         }
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), Visibility.PRIVATE, scope, scriptObject);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), visibility, scope, scriptObject);
         }
         
         method.setCallConfig(callConfig);
         
         containingClass.addMethod(name, method);
         
         if (visibility == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
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
         
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         method = factory.getCompiledMethod(rubyClass, javaName, 
                 Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject);
         
         method.setCallConfig(callConfig);
         
         rubyClass.addMethod(name, method);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         
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
 
     public static IRubyObject doAttrAssign(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return compilerCallMethod(context, receiver, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         try {
             return compilerCallMethod(context, receiver, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         try {
             return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
 
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public static IRubyObject compilerCallMethodWithIndex(ThreadContext context, IRubyObject receiver, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         RubyModule module = receiver.getMetaClass();
         
         if (module.index != 0) {
             return receiver.callMethod(context, module, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public static IRubyObject compilerCallMethod(ThreadContext context, IRubyObject receiver, String name,
             IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = receiver.getMetaClass();
         method = rubyclass.searchMethod(name);
         
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(caller, callType))) {
             return callMethodMissing(context, receiver, method, name, args, caller, callType, block);
         }
 
         return method.call(context, receiver, rubyclass, name, args, block);
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
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(value.getRuntime(), value);
         }
         return (RubyArray) value;
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(Ruby runtime, IRubyObject value, boolean masgnHasHead) {
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
         
         return (RubyModule)rubyModule;
     }
     
     public static RegexpPattern regexpLiteral(Ruby runtime, String ptr, int options) {
         IRubyObject noCaseGlobal = runtime.getGlobalVariables().get("$=");
 
         int extraOptions = noCaseGlobal.isTrue() ? ReOptions.RE_OPTION_IGNORECASE : 0;
 
         try {
             if((options & 256) == 256 ) {
                 return RegexpFactory.getFactory("java").createPattern(ByteList.create(ptr), (options & ~256) | extraOptions, 0);
             } else {
                 return runtime.getRegexpFactory().createPattern(ByteList.create(ptr), options | extraOptions, 0);
             }
         } catch(PatternSyntaxException e) {
             throw runtime.newRegexpError(e.getMessage());
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
     
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (exception.isKindOf(runtime.fastGetClass("LocalJumpError"))) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asSymbol();
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
         
         RubyProc blockArg;
         
         if (block.getProcObject() != null) {
             blockArg = block.getProcObject();
         } else {
-            blockArg = runtime.newProc(false, block);
-            blockArg.getBlock().isLambda = block.isLambda;
+            blockArg = runtime.newProc(Block.Type.PROC, block);
+            blockArg.getBlock().type = Block.Type.PROC;
         }
         
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         Ruby runtime = proc.getRuntime();
 
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = proc.convertToType(runtime.getProc(), 0, "to_proc", false);
 
             if (!(proc instanceof RubyProc)) {
                 throw runtime.newTypeError("wrong argument type "
                         + proc.getMetaClass().getName() + " (expected Proc)");
             }
         }
 
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) return currentBlock;
         }
 
         return ((RubyProc) proc).getBlock();
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_last(backref);
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
         
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return self.callSuper(context, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static IRubyObject returnJump(IRubyObject result, ThreadContext context) {
         throw new JumpException.ReturnJump(context.getFrameJumpTarget(), result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, Block aBlock) {
         // JRUBY-530, while case
         if (bj.getTarget() == aBlock) {
             bj.setTarget(null);
             
             throw bj;
         }
 
         return (IRubyObject) bj.getValue();
     }
     
     public static IRubyObject breakJump(IRubyObject value) {
         throw new JumpException.BreakJump(null, value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError("break", value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (!exceptions[i].isKindOf(runtime.getModule())) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             IRubyObject result = exceptions[i].callMethod(context, "===", currentException);
             if (result.isTrue()) return result;
         }
         return runtime.getFalse();
     }
     
     public static void checkSuperDisabled(ThreadContext context) {
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw context.getRuntime().newNameError("Superclass method '" + name
                     + "' disabled.", name);
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
         return RubyArray.newArrayNoCopy(runtime, input, start);
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = ASTInterpreter.splatValue(context.getRuntime(), expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                     .isTrue())
                     || (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, (RubyModule)module, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError("next", value, "unexpected next");
     }
     
     public static ISourcePosition constructPosition(String file, int line) {
         return new SimpleSourcePosition(file, line);
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
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.put(key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.put(key1, value1);
         hash.put(key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.put(key1, value1);
         hash.put(key2, value2);
         hash.put(key3, value3);
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
     
     public static IRubyObject getInstanceVariable(Ruby runtime, IRubyObject self, String name) {
         IRubyObject result = self.getInstanceVariable(name);
         if (result == null) return runtime.getNil();
         return result;
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.fastGetInstanceVariable(internedName)) != null) return result;
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
         StaticScope staticScope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = new DynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postScopedBody();
     }
     
     public static void registerEndBlock(CompiledSharedScopeBlock block, Ruby runtime) {
-        runtime.pushExitBlock(runtime.newProc(true, block));
+        runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(value);
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
 }
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index ab36d3eca7..00fe71fc19 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,409 +1,435 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Block {
     // FIXME: Maybe not best place, but move it to a good home
     public static final int ZERO_ARGS = 0;
     public static final int MULTIPLE_ASSIGNMENT = 1;
     public static final int ARRAY = 2;
+    public static final int SINGLE_RESTARG = 3;
+    
+    public enum Type { NORMAL, PROC, LAMBDA }
     
     /**
      * All Block variables should either refer to a real block or this NULL_BLOCK.
      */
     public static final Block NULL_BLOCK = new Block() {
         public boolean isGiven() {
             return false;
         }
         
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
             throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
         }
         
         public Block cloneBlock() {
             return this;
         }
     };
 
     /**
      * 'self' at point when the block is defined
      */
     protected IRubyObject self;
 
     /**
      * AST Node representing the parameter (VARiable) list to the block.
      */
     private IterNode iterNode;
     
     /**
      * frame of method which defined this block
      */
     protected Frame frame;
     protected Visibility visibility;
     protected RubyModule klass;
     
     /**
      * A reference to all variable values (and names) that are in-scope for this block.
      */
     protected DynamicScope dynamicScope;
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
-    public boolean isLambda = false;
+    public Type type = Type.NORMAL;
     
     protected Arity arity;
 
     public static Block createBlock(ThreadContext context, IterNode iterNode, DynamicScope dynamicScope, IRubyObject self) {
         Frame f = context.getCurrentFrame();
         f.setPosition(context.getPosition());
         return new Block(iterNode,
                          self,
                          f,
                          f.getVisibility(),
                          context.getRubyClass(),
                          dynamicScope);
     }
     
     protected Block() {
         this(null, null, null, null, null, null);
     }
 
     public Block(IterNode iterNode, IRubyObject self, Frame frame,
         Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
         this(iterNode, self,iterNode == null ? null : Arity.procArityOf(iterNode.getVarNode()),
                 frame, visibility, klass, dynamicScope);
     }
     
     public Block(IterNode iterNode, IRubyObject self, Arity arity, Frame frame,
             Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
         this.iterNode = iterNode;
         this.self = self;
         this.arity = arity;
         this.frame = frame;
         this.visibility = visibility;
         this.klass = klass;
         this.dynamicScope = dynamicScope;
     }
     
     public static Block createBinding(Frame frame, DynamicScope dynamicScope) {
         ThreadContext context = frame.getSelf().getRuntime().getCurrentContext();
         
         // We create one extra dynamicScope on a binding so that when we 'eval "b=1", binding' the
         // 'b' will get put into this new dynamic scope.  The original scope does not see the new
         // 'b' and successive evals with this binding will.  I take it having the ability to have 
         // succesive binding evals be able to share same scope makes sense from a programmers 
         // perspective.   One crappy outcome of this design is it requires Dynamic and Static 
         // scopes to be mutable for this one case.
         
         // Note: In Ruby 1.9 all of this logic can go away since they will require explicit
         // bindings for evals.
         
         // We only define one special dynamic scope per 'logical' binding.  So all bindings for
         // the same scope should share the same dynamic scope.  This allows multiple evals with
         // different different bindings in the same scope to see the same stuff.
         DynamicScope extraScope = dynamicScope.getBindingScope();
         
         // No binding scope so we should create one
         if (extraScope == null) {
             // If the next scope out has the same binding scope as this scope it means
             // we are evaling within an eval and in that case we should be sharing the same
             // binding scope.
             DynamicScope parent = dynamicScope.getNextCapturedScope(); 
             if (parent != null && parent.getBindingScope() == dynamicScope) {
                 extraScope = dynamicScope;
             } else {
                 extraScope = new DynamicScope(new BlockStaticScope(dynamicScope.getStaticScope()), dynamicScope);
                 dynamicScope.setBindingScope(extraScope);
             }
         } 
         
         // FIXME: Ruby also saves wrapper, which we do not
         return new Block(null, frame.getSelf(), frame.duplicate(), frame.getVisibility(), 
                 context.getBindingRubyClass(), extraScope);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
-        if (!isLambda && args.length == 1 && args[0] instanceof RubyArray && iterNode != null) {
-            Node vNode = iterNode.getVarNode();
-            
-            if (vNode.nodeId == NodeType.MULTIPLEASGNNODE) {
-                args = ((RubyArray) args[0]).toJavaArray();
+        switch (type) {
+        case NORMAL: {
+            assert false : "can this happen?";
+            if (args.length == 1 && args[0] instanceof RubyArray && iterNode != null) {
+                Node vNode = iterNode.getVarNode();
+
+                if (vNode.nodeId == NodeType.MULTIPLEASGNNODE) {
+                    args = ((RubyArray) args[0]).toJavaArray();
+                }
+            }
+            break;
+        }
+        case PROC: {
+            if (args.length == 1 && args[0] instanceof RubyArray && iterNode != null) {
+                Node vNode = iterNode.getVarNode();
+
+                if (vNode.nodeId == NodeType.MULTIPLEASGNNODE) {
+                    // if we only have *arg, we leave it wrapped in the array
+                    if (((MultipleAsgnNode)vNode).getArgsNode() == null) {
+                        args = ((RubyArray) args[0]).toJavaArray();
+                    }
+                }
             }
+            break;
+        }
+        case LAMBDA:
+            arity().checkArity(context.getRuntime(), args);
+            break;
         }
 
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true);
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         context.preYieldSpecificBlock(this, klass);
     }
     
     protected void post(ThreadContext context) {
         context.postYield(this);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return yield(context, value, null, null, false);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
         
         Visibility oldVis = frame.getVisibility();
         pre(context, klass);
 
         try {
             if (iterNode.getVarNode() != null) {
                 if (aValue) {
                     setupBlockArgs(context, iterNode.getVarNode(), value, self);
                 } else {
                     setupBlockArg(context, iterNode.getVarNode(), value, self);
                 }
             }
             
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return ASTInterpreter.eval(context.getRuntime(), context, iterNode.getBodyNode(), self, NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
-            return isLambda ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+            return type == Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             frame.setVisibility(oldVis);
             post(context);
         }
     }
 
     private void setupBlockArgs(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case ZEROARGNODE:
             break;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
             break;
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = runtime.getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 runtime.getWarnings().warn("multiple values for a block parameter (" + length + " for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
 
     private void setupBlockArg(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case ZEROARGNODE:
             return;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode,
                     ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null), false);
             break;
         default:
             if (value == null) {
                 runtime.getWarnings().warn("multiple values for a block parameter (0 for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
     
     protected int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
 
     public Block cloneBlock() {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         Block newBlock = new Block(iterNode, self, frame.duplicate(), visibility, klass, 
                 dynamicScope.cloneScope());
         
-        newBlock.isLambda = isLambda;
+        newBlock.type = type;
 
         return newBlock;
     }
 
     public IterNode getIterNode() {
         return iterNode;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 
     public Visibility getVisibility() {
         return visibility;
     }
 
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     public void setSelf(IRubyObject self) {
         this.self = self;
     }
 
     /**
      * Retrieve the proc object associated with this block
      * 
      * @return the proc or null if this has no proc associated with it
      */
     public RubyProc getProcObject() {
     	return proc;
     }
     
     /**
      * Set the proc object associated with this block
      * 
      * @param procObject
      */
     public void setProcObject(RubyProc procObject) {
     	this.proc = procObject;
     }
 
     /**
      * Gets the dynamicVariables that are local to this block.   Parent dynamic scopes are also
      * accessible via the current dynamic scope.
      * 
      * @return Returns all relevent variable scoping information
      */
     public DynamicScope getDynamicScope() {
         return dynamicScope;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return frame;
     }
 
     /**
      * Gets the klass.
      * @return Returns a RubyModule
      */
     public RubyModule getKlass() {
         return klass;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
     
     /**
      * Compiled codes way of examining arguments
      * 
      * @param nodeId to be considered
      * @return something not linked to AST and a constant to make compiler happy
      */
     public static int asArgumentType(NodeType nodeId) {
         if (nodeId == null) return ZERO_ARGS;
         
         switch (nodeId) {
         case ZEROARGNODE: return ZERO_ARGS;
         case MULTIPLEASGNNODE: return MULTIPLE_ASSIGNMENT;
+        case SVALUENODE: return SINGLE_RESTARG;
         }
         return ARRAY;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlock.java b/src/org/jruby/runtime/CompiledBlock.java
index b176b2e13d..60413adb6d 100644
--- a/src/org/jruby/runtime/CompiledBlock.java
+++ b/src/org/jruby/runtime/CompiledBlock.java
@@ -1,188 +1,208 @@
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
+import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock extends Block {
     private CompiledBlockCallback callback;
     private boolean hasMultipleArgsHead;
     private int argumentType;
     private boolean light;
 
     public CompiledBlock(ThreadContext context, IRubyObject self, Arity arity, DynamicScope dynamicScope,
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         this(self,
              context.getCurrentFrame(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 dynamicScope, arity, callback, hasMultipleArgsHead, argumentType);
     }
 
     private CompiledBlock(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(null, self, arity, frame, visibility, klass, dynamicScope);
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
         this.argumentType = argumentType;
     }
     
     public void setLight(boolean light) {
         this.light = light;
     }
     
     public boolean getLight() {
         return light;
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
-        if (!isLambda && args.length == 1 && args[0] instanceof RubyArray) {
-            if (argumentType == MULTIPLE_ASSIGNMENT) {
-                args = ((RubyArray) args[0]).toJavaArray();
+        switch (type) {
+        case NORMAL: {
+            assert false : "can this happen?";
+            if (args.length == 1 && args[0] instanceof RubyArray) {
+                if (argumentType == MULTIPLE_ASSIGNMENT || argumentType == SINGLE_RESTARG) {
+                    args = ((RubyArray) args[0]).toJavaArray();
+                }
+                break;
+            }
+        }
+        case PROC: {
+            if (args.length == 1 && args[0] instanceof RubyArray) {
+                if (argumentType == MULTIPLE_ASSIGNMENT && argumentType != SINGLE_RESTARG) {
+                    args = ((RubyArray) args[0]).toJavaArray();
+                }
             }
+            break;
+        }
+        case LAMBDA:
+            arity().checkArity(context.getRuntime(), args);
+            break;
         }
 
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
 
         // handle as though it's just an array coming in...i.e. it should be multiassigned or just 
         // assigned as is to var 0.
         // FIXME for now, since masgn isn't supported, this just wraps args in an IRubyObject[], 
         // since single vars will want that anyway
         Visibility oldVis = frame.getVisibility();
         try {
             IRubyObject[] realArgs = aValue ? 
                     setupBlockArgs(context, args, self) : setupBlockArg(context, args, self); 
             pre(context, klass);
             
             // NOTE: Redo jump handling is within compiled closure, wrapping the body
             try {
                 return callback.call(context, self, realArgs);
             } catch (JumpException.BreakJump bj) {
                 if (bj.getTarget() == null) {
                     bj.setTarget(this);
                 }
                 throw bj;
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
-            return isLambda ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+            return type == Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             frame.setVisibility(oldVis);
             post(context);
         }
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         if (light) {
             context.preYieldLightBlock(this, klass);
         } else {
             context.preYieldSpecificBlock(this, klass);
         }
     }
     
     protected void post(ThreadContext context) {
         if (light) {
             context.postYieldLight(this);
         } else {
             context.postYield(this);
         }
     }
 
     private IRubyObject[] setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
+        case SINGLE_RESTARG:
             return new IRubyObject[] {value};
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = context.getRuntime().getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (" +
                         length + " for 1)");
             }
             return new IRubyObject[] {value};
         }
     }
 
     private IRubyObject[] setupBlockArg(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
+        case SINGLE_RESTARG:
             return new IRubyObject[] {ArgsUtil.convertToRubyArray(context.getRuntime(), value, hasMultipleArgsHead)};
         default:
         // FIXME: the test below would be enabled if we could avoid processing block args for the cases where we don't have any args
         // since we can't do that just yet, it's disabled
             if (value == null) {
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (0 for 1)");
                 return new IRubyObject[] {context.getRuntime().getNil()};
             }
             return new IRubyObject[] {value};
         }
     }
 
     public Block cloneBlock() {
         Block newBlock = new CompiledBlock(
                 self,
                 frame.duplicate(),
                 visibility,
                 klass,
                 light ? dynamicScope : dynamicScope.cloneScope(),
                 arity,
                 callback,
                 hasMultipleArgsHead,
                 argumentType);
         
-        newBlock.isLambda = isLambda;
+        newBlock.type = type;
         
         return newBlock;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledSharedScopeBlock.java b/src/org/jruby/runtime/CompiledSharedScopeBlock.java
index bd0cca3f56..8787ea592d 100644
--- a/src/org/jruby/runtime/CompiledSharedScopeBlock.java
+++ b/src/org/jruby/runtime/CompiledSharedScopeBlock.java
@@ -1,146 +1,146 @@
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
 package org.jruby.runtime;
 
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledSharedScopeBlock extends SharedScopeBlock {
     private CompiledBlockCallback callback;
     private boolean hasMultipleArgsHead;
     private int argumentType;
 
     public CompiledSharedScopeBlock(ThreadContext context, IRubyObject self, Arity arity, DynamicScope dynamicScope,
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         this(self,
              context.getCurrentFrame().duplicate(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 dynamicScope, arity, callback, hasMultipleArgsHead, argumentType);
     }
 
     private CompiledSharedScopeBlock(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(null, self, frame, visibility, klass, dynamicScope);
         this.arity = arity;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
         this.argumentType = argumentType;
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
 
         // handle as though it's just an array coming in...i.e. it should be multiassigned or just 
         // assigned as is to var 0.
         // FIXME for now, since masgn isn't supported, this just wraps args in an IRubyObject[], 
         // since single vars will want that anyway
         Visibility oldVis = frame.getVisibility();
         try {
             IRubyObject[] realArgs = aValue ? 
                     setupBlockArgs(context, args, self) : setupBlockArg(context, args, self); 
             pre(context, klass);
             
             // NOTE: Redo jump handling is within compiled closure, wrapping the body
             try {
                 return callback.call(context, self, realArgs);
             } catch (JumpException.BreakJump bj) {
                 if (bj.getTarget() == null) {
                     bj.setTarget(this);
                 }
                 throw bj;
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
-            return isLambda ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+            return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
         } finally {
             frame.setVisibility(oldVis);
             post(context);
         }
     }
 
     private IRubyObject[] setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
             return new IRubyObject[] {value};
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = context.getRuntime().getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (" +
                         length + " for 1)");
             }
             return new IRubyObject[] {value};
         }
     }
 
     private IRubyObject[] setupBlockArg(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return IRubyObject.NULL_ARRAY;
         case MULTIPLE_ASSIGNMENT:
             return new IRubyObject[] {ArgsUtil.convertToRubyArray(context.getRuntime(), value, hasMultipleArgsHead)};
         default:
         // FIXME: the test below would be enabled if we could avoid processing block args for the cases where we don't have any args
         // since we can't do that just yet, it's disabled
             if (value == null) {
                 context.getRuntime().getWarnings().warn("multiple values for a block parameter (0 for 1)");
                 return new IRubyObject[] {context.getRuntime().getNil()};
             }
             return new IRubyObject[] {value};
         }
     }
 
     public Block cloneBlock() {
         Block newBlock = new CompiledSharedScopeBlock(self, frame.duplicate(), visibility, klass, 
                 dynamicScope, arity, callback, hasMultipleArgsHead, argumentType);
         
-        newBlock.isLambda = isLambda;
+        newBlock.type = type;
         
         return newBlock;
     }
 }
diff --git a/src/org/jruby/runtime/MethodBlock.java b/src/org/jruby/runtime/MethodBlock.java
index 0e024aac3f..9be714f464 100644
--- a/src/org/jruby/runtime/MethodBlock.java
+++ b/src/org/jruby/runtime/MethodBlock.java
@@ -1,246 +1,246 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.RubyArray;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class MethodBlock extends Block{
 
     /**
      * 'self' at point when the block is defined
      */
     private IRubyObject self;
     
     private RubyMethod method;
     private Callback callback;
     
     private final Arity arity;
     
     /**
      * frame of method which defined this block
      */
     protected final Frame frame;
     private Visibility visibility;
     private final RubyModule klass;
     
     /**
      * A reference to all variable values (and names) that are in-scope for this block.
      */
     private final DynamicScope dynamicScope;
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
     public boolean isLambda = false;
 
     public static MethodBlock createMethodBlock(ThreadContext context, DynamicScope dynamicScope, Callback callback, RubyMethod method, IRubyObject self) {
         return new MethodBlock(self,
                                context.getCurrentFrame().duplicate(),
                          context.getCurrentFrame().getVisibility(),
                          context.getRubyClass(),
                          dynamicScope,
                          callback,
                          method);
     }
 
     public MethodBlock(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Callback callback, RubyMethod method) {
 
         this.self = self;
         this.frame = frame;
         this.visibility = visibility;
         this.klass = klass;
         this.dynamicScope = dynamicScope;
         this.callback = callback;
         this.method = method;
         this.arity = Arity.createArity((int) method.arity().getLongValue());
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true);
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         context.preYieldSpecificBlock(this, klass);
     }
     
     protected void post(ThreadContext context) {
         context.postYield(this);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return yield(context, value, null, null, false);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
         
         pre(context, klass);
 
         try {
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return callback.execute(value, new IRubyObject[] { method, self }, NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return (IRubyObject)nj.getValue();
         } finally {
             post(context);
         }
     }
 
     public Block cloneBlock() {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         Block newBlock = new MethodBlock(self, frame.duplicate(), visibility, klass, 
                 dynamicScope.cloneScope(), callback, method);
         
-        newBlock.isLambda = isLambda;
+        newBlock.type = type;
 
         return newBlock;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 
     public Visibility getVisibility() {
         return visibility;
     }
 
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     public void setSelf(IRubyObject self) {
         this.self = self;
     }
 
     /**
      * Retrieve the proc object associated with this block
      * 
      * @return the proc or null if this has no proc associated with it
      */
     public RubyProc getProcObject() {
     	return proc;
     }
     
     /**
      * Set the proc object associated with this block
      * 
      * @param procObject
      */
     public void setProcObject(RubyProc procObject) {
     	this.proc = procObject;
     }
 
     /**
      * Gets the dynamicVariables that are local to this block.   Parent dynamic scopes are also
      * accessible via the current dynamic scope.
      * 
      * @return Returns all relevent variable scoping information
      */
     public DynamicScope getDynamicScope() {
         return dynamicScope;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return frame;
     }
 
     /**
      * Gets the klass.
      * @return Returns a RubyModule
      */
     public RubyModule getKlass() {
         return klass;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
 }
diff --git a/test/jruby_index b/test/jruby_index
index 3660bcf12d..931cb36449 100644
--- a/test/jruby_index
+++ b/test/jruby_index
@@ -1,54 +1,55 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 test_globals
 test_argf
 test_array
 test_array_subclass_behavior
 test_autoload
 test_backquote
 test_big_decimal
 test_bignum
 test_block
+test_block_arg_processing
 test_cache_map_leak
 test_case
 test_class
 test_comparable
 test_core_arities
 test_crazy_blocks
 test_date_time
 test_defined
 test_dir
 #test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_file
 test_flip
 test_hash
 test_higher_javasupport
 test_iconv
 test_io
 test_nkf
 test_java_accessible_object
 test_java_extension
 test_local_jump_error
 test_method_missing
 test_methods
 #test_openssl
 test_pack
 test_primitive_to_java
 test_process
 test_parsing
 test_random
 test_rbconfig
 test_string_java_bytes
 test_string_printf
 test_string_sub
 test_string_to_number
 test_symbol
 test_tb_yaml
 test_thread
 test_variables
 test_vietnamese_charset
 #test_trace_func
 test_zlib
 test_yaml
diff --git a/test/test_block_arg_processing.rb b/test/test_block_arg_processing.rb
new file mode 100644
index 0000000000..bad66d318f
--- /dev/null
+++ b/test/test_block_arg_processing.rb
@@ -0,0 +1,214 @@
+require 'test/unit'
+
+class TestVarArgBlock < Test::Unit::TestCase
+  def blockyield(arg)
+    yield arg
+  end
+  
+  def blockarg(arg, &b)
+    b.call(arg)
+  end
+  
+  def test_vararg_blocks
+    Proc.new { |*element| assert_equal [["a"]], element }.call( ["a"] )
+    Proc.new { |*element| assert_equal ["a"], element }.call( "a" )
+    proc { |*element| assert_equal [["a"]], element }.call( ["a"] )
+    proc { |*element| assert_equal ["a"], element }.call( "a" )
+    lambda { |*element| assert_equal [["a"]], element }.call( ["a"] )
+    lambda { |*element| assert_equal ["a"], element }.call( "a" )
+    blockyield(["a"]) { |*element| assert_equal [["a"]], element }
+    blockyield("a") { |*element| assert_equal ["a"], element }
+    blockyield(["a"], &Proc.new { |*element| assert_equal [["a"]], element })
+    blockyield("a", &Proc.new { |*element| assert_equal ["a"], element })
+    blockyield(["a"], &proc { |*element| assert_equal [["a"]], element })
+    blockyield("a", &proc { |*element| assert_equal ["a"], element })
+    blockyield(["a"], &lambda { |*element| assert_equal [["a"]], element })
+    blockyield("a", &lambda { |*element| assert_equal ["a"], element })
+    blockarg(["a"]) { |*element| assert_equal [["a"]], element }
+    blockarg("a") { |*element| assert_equal ["a"], element }
+    blockarg(["a"], &Proc.new { |*element| assert_equal [["a"]], element })
+    blockarg("a", &Proc.new { |*element| assert_equal ["a"], element })
+    blockarg(["a"], &proc { |*element| assert_equal [["a"]], element })
+    blockarg("a", &proc { |*element| assert_equal ["a"], element })
+    blockarg(["a"], &lambda { |*element| assert_equal [["a"]], element })
+    blockarg("a", &lambda { |*element| assert_equal ["a"], element })
+  end
+  
+  def test_requiredarg_blocks
+    Proc.new { |element| assert_equal ["a"], element }.call( ["a"] )
+    Proc.new { |element| assert_equal "a", element }.call( "a" )
+    proc { |element| assert_equal ["a"], element }.call( ["a"] )
+    proc { |element| assert_equal "a", element }.call( "a" )
+    lambda { |element| assert_equal ["a"], element }.call( ["a"] )
+    lambda { |element| assert_equal "a", element }.call( "a" )
+    blockyield(["a"]) { |element| assert_equal ["a"], element }
+    blockyield("a") { |element| assert_equal "a", element }
+    blockyield(["a"], &Proc.new { |element| assert_equal ["a"], element })
+    blockyield("a", &Proc.new { |element| assert_equal "a", element })
+    blockyield(["a"], &proc { |element| assert_equal ["a"], element })
+    blockyield("a", &proc { |element| assert_equal "a", element })
+    blockyield(["a"], &lambda { |element| assert_equal ["a"], element })
+    blockyield("a", &lambda { |element| assert_equal "a", element })
+    blockarg(["a"]) { |element| assert_equal ["a"], element }
+    blockarg("a") { |element| assert_equal "a", element }
+    blockarg(["a"], &Proc.new { |element| assert_equal ["a"], element })
+    blockarg("a", &Proc.new { |element| assert_equal "a", element })
+    blockarg(["a"], &proc { |element| assert_equal ["a"], element })
+    blockarg("a", &proc { |element| assert_equal "a", element })
+    blockarg(["a"], &lambda { |element| assert_equal ["a"], element })
+    blockarg("a", &lambda { |element| assert_equal "a", element })
+  end
+  
+  def test_requiredargs_blocks
+    Proc.new { |element, a| assert_equal "a", element }.call( ["a"] )
+    Proc.new { |element, a| assert_equal "a", element }.call( "a" )
+    assert_raises(ArgumentError) {
+      proc { |element, a| assert_equal ["a"], element }.call( ["a"] )
+    }
+    assert_raises(ArgumentError) {
+      proc { |element, a| assert_equal "a", element }.call( "a" )
+    }
+    assert_raises(ArgumentError) {
+      lambda { |element, a| assert_equal ["a"], element }.call( ["a"] )
+    }
+    assert_raises(ArgumentError) {
+      lambda { |element, a| assert_equal "a", element }.call( "a" )
+    }
+    blockyield(["a"]) { |element, a| assert_equal "a", element }
+    blockyield("a") { |element, a| assert_equal "a", element }
+    blockyield(["a"], &Proc.new { |element, a| assert_equal "a", element })
+    blockyield("a", &Proc.new { |element, a| assert_equal "a", element })
+    blockyield(["a"], &proc { |element, a| assert_equal "a", element })
+    blockyield("a", &proc { |element, a| assert_equal "a", element })
+    blockyield(["a"], &lambda { |element, a| assert_equal "a", element })
+    blockyield("a", &lambda { |element, a| assert_equal "a", element })
+    blockarg(["a"]) { |element, a| assert_equal "a", element }
+    blockarg("a") { |element, a| assert_equal "a", element }
+    blockarg(["a"], &Proc.new { |element, a| assert_equal "a", element })
+    blockarg("a", &Proc.new { |element, a| assert_equal "a", element })
+    assert_raises(ArgumentError) {
+      blockarg(["a"], &proc { |element, a| assert_equal ["a"], element })
+    }
+    assert_raises(ArgumentError) {
+      blockarg("a", &proc { |element, a| assert_equal "a", element })
+    }
+    assert_raises(ArgumentError) {
+      blockarg(["a"], &lambda { |element, a| assert_equal ["a"], element })
+    }
+    assert_raises(ArgumentError) {
+      blockarg("a", &lambda { |element, a| assert_equal "a", element })
+    }
+  end
+  
+  def check_all_definemethods(obj)
+    results = obj.foo1 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo2 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo3 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo4 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo5 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo6 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo7 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo8 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo9 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo10 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo11 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo12 "a"
+    assert_equal(results[0], results[1])
+    results = obj.foo13 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo14 "a"
+    assert_equal(results[0], results[1])
+  end
+  
+  def check_requiredargs_definemethods(obj)
+    results = obj.foo1 ["a"]
+    assert_equal(results[0], results[1])
+    results = obj.foo2 "a"
+    assert_equal(results[0], results[1])
+    assert_raises(ArgumentError) { results = obj.foo3 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo4 "a" }
+    assert_raises(ArgumentError) { results = obj.foo5 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo6 "a" }
+    assert_raises(ArgumentError) { results = obj.foo7 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo8 "a" }
+    assert_raises(ArgumentError) { results = obj.foo9 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo10 "a" }
+    assert_raises(ArgumentError) { results = obj.foo11 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo12 "a" }
+    assert_raises(ArgumentError) { results = obj.foo13 ["a"] }
+    assert_raises(ArgumentError) { results = obj.foo14 "a" }
+  end
+  
+  def test_definemethods
+    obj = Object.new
+    
+    class << obj
+      define_method :foo1, Proc.new { |*element| [[["a"]], element] }
+      define_method :foo2, Proc.new { |*element| [["a"], element] }
+      define_method :foo3, proc { |*element| [[["a"]], element] }
+      define_method :foo4, proc { |*element| [["a"], element] }
+      define_method :foo5, lambda { |*element| [[["a"]], element] }
+      define_method :foo6, lambda { |*element| [["a"], element] }
+      define_method(:foo7) { |*element| [[["a"]], element] }
+      define_method(:foo8) { |*element| [["a"], element] }
+      define_method :foo9, &Proc.new { |*element| [[["a"]], element] }
+      define_method :foo10, &Proc.new { |*element| [["a"], element] }
+      define_method :foo11, &proc { |*element| [[["a"]], element] }
+      define_method :foo12, &proc { |*element| [["a"], element] }
+      define_method :foo13, &lambda { |*element| [[["a"]], element] }
+      define_method :foo14, &lambda { |*element| [["a"], element] }
+    end
+    
+    check_all_definemethods(obj)
+    
+    class << obj
+      define_method :foo1, Proc.new { |element| [["a"], element] }
+      define_method :foo2, Proc.new { |element| ["a", element] }
+      define_method :foo3, proc { |element| [["a"], element] }
+      define_method :foo4, proc { |element| ["a", element] }
+      define_method :foo5, lambda { |element| [["a"], element] }
+      define_method :foo6, lambda { |element| ["a", element] }
+      define_method(:foo7) { |element| [["a"], element] }
+      define_method(:foo8) { |element| ["a", element] }
+      define_method :foo9, &Proc.new { |element| [["a"], element] }
+      define_method :foo10, &Proc.new { |element| ["a", element] }
+      define_method :foo11, &proc { |element| [["a"], element] }
+      define_method :foo12, &proc { |element| ["a", element] }
+      define_method :foo13, &lambda { |element| [["a"], element] }
+      define_method :foo14, &lambda { |element| ["a", element] }
+    end
+    
+    check_all_definemethods(obj)
+    
+    class << obj
+      define_method :foo1, Proc.new { |element, a| ["a", element] }
+      define_method :foo2, Proc.new { |element, a| ["a", element] }
+      define_method :foo3, proc { |element, a| [["a"], element] }
+      define_method :foo4, proc { |element, a| ["a", element] }
+      define_method :foo5, lambda { |element, a| [["a"], element] }
+      define_method :foo6, lambda { |element, a| ["a", element] }
+      define_method(:foo7) { |element, a| [["a"], element] }
+      define_method(:foo8) { |element, a| ["a", element] }
+      define_method :foo9, &Proc.new { |element, a| [["a"], element] }
+      define_method :foo10, &Proc.new { |element, a| ["a", element] }
+      define_method :foo11, &proc { |element, a| [["a"], element] }
+      define_method :foo12, &proc { |element, a| ["a", element] }
+      define_method :foo13, &lambda { |element, a| [["a"], element] }
+      define_method :foo14, &lambda { |element, a| ["a", element] }
+    end
+    
+    check_requiredargs_definemethods(obj)
+  end
+end
+
