diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 3ac142d724..f128899b01 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -187,1518 +187,1518 @@ public final class Ruby {
 
     private RubyClass fixnumClass;
     
     private RubyClass arrayClass;
     
     private RubyClass hashClass;    
 
     private IRubyObject tmsStruct;
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 0;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
     
     /**
      * A list of finalizers, weakly referenced, to be executed on tearDown
      */
     private Map finalizers;
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static Ruby getDefaultInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured as provided.
      *
      * @param config the instance configuration
      * @return the JRuby runtime
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured with the given input, output and error streams.
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
 
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScript(String script) {
         return eval(parse(script, "<script>", getCurrentContext().getCurrentScope()));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
 
             return EvaluationState.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw newLocalJumpError("unexpected return");
                 //              return (IRubyObject)je.getSecondaryData();
             } else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw newLocalJumpError("unexpected break");
             }
 
             throw je;
         }
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
             Class scriptClass = compiler.loadClass(this);
 
             Script script = (Script)scriptClass.newInstance();
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } else {
                 throw je;
             }
         } catch (ClassNotFoundException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (InstantiationException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (IllegalAccessException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         }
     }
 
     public IRubyObject ycompileAndRun(Node node) {
         try {
             StandardYARVCompiler compiler = new StandardYARVCompiler(this);
             NodeCompilerFactory.getYARVCompiler().compile(node, compiler);
             org.jruby.lexer.yacc.ISourcePosition p = node.getPosition();
             if(p == null && node instanceof org.jruby.ast.RootNode) {
                 p = ((org.jruby.ast.RootNode)node).getBodyNode().getPosition();
             }
             return new YARVCompiledRunner(this,compiler.getInstructionSequence("<main>",p.getFile(),"toplevel")).run();
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } 
                 
             throw je;
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyModule getKernel() {
         return kernelModule;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
 
     public RubyClass getFixnum() {
         return fixnumClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }    
     
     public RubyClass getArray() {
         return arrayClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
 
     /** Returns the "true" instance from the instance pool.
      * @return The "true" instance.
      */
     public RubyBoolean getTrue() {
         return trueObject;
     }
 
     /** Returns the "false" instance from the instance pool.
      * @return The "false" instance.
      */
     public RubyBoolean getFalse() {
         return falseObject;
     }
 
     /** Returns the "nil" singleton instance.
      * @return "nil"
      */
     public IRubyObject getNil() {
         return nilObject;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
 
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /** Returns a class from the instance pool.
      *
      * @param name The name of the class.
      * @return The class.
      */
     public RubyClass getClass(String name) {
         try {
             return objectClass.getClass(name);
         } catch (ClassCastException e) {
             throw newTypeError(name + " is not a Class");
         }
     }
 
     /** Define a new class with name 'name' and super class 'superClass'.
      *
      * MRI: rb_define_class / rb_define_class_id
      *
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass.getCRef());
     }
 
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         if (superClass == null) {
             superClass = objectClass;
         }
 
         return superClass.newSubClass(name, allocator, parentCRef);
     }
 
     /** rb_define_module / rb_define_module_id
      *
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass.getCRef());
     }
 
     public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef) {
         RubyModule newModule = RubyModule.newModule(this, name, parentCRef);
 
         ((RubyModule)parentCRef.getValue()).setConstant(name, newModule);
 
         return newModule;
     }
 
     /**
      * In the current context, get the named module. If it doesn't exist a
      * new module is created.
      */
     public RubyModule getOrCreateModule(String name) {
         ThreadContext tc = getCurrentContext();
         RubyModule module = (RubyModule) tc.getRubyClass().getConstantAt(name);
 
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         }
 
         return module;
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
      * @see org.jruby.Ruby#getRuntimeInformation
      */
     public Map getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable() : runtimeInformation;
     }
 
     public MethodSelectorTable getSelectorTable() {
         return selectorTable;
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public IRubyObject getTopConstant(String name) {
         IRubyObject constant = getModule(name);
         if (constant == null) {
             constant = getLoadService().autoload(name);
         }
         return constant;
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
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
 
         verbose = falseObject;
         debug = falseObject;
         
         // init selector table, now that classes are done adding methods
         selectorTable.init();
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
     }
 
     private void initLibraries() {
         loadService = new LoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                 }
             });
         
         registerBuiltin("socket.rb", new RubySocket.Service());
         registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
             if(profile.allowBuiltin(BUILTIN_LIBRARIES[i])) {
                 loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
             }
         }
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
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
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("rubygems"));
                     runtime.getTopSelf().callMethod(runtime.getCurrentContext(),"gem",runtime.newString("jruby-openssl"));
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("jopenssl"));
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         RubyClass objectMetaClass = RubyClass.createBootstrapMetaClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR, null);
         RubyObject.createObjectClass(this, objectMetaClass);
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = RubyClass.createBootstrapMetaClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR, objectClass.getCRef());
         objectClass.setConstant("Module", moduleClass);
         RubyClass classClass = RubyClass.newClassClass(this, moduleClass);
         objectClass.setConstant("Class", classClass);
         
         classClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
 
         // I don't think the containment is correct here (parent cref)
         RubyClass metaClass = objectClass.makeMetaClass(classClass, objectMetaClass.getCRef());
         metaClass = moduleClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
         metaClass = classClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
 
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
         RubyEnumerable.createEnumerableModule(this);
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
         if (profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemExit")) {
             defineClass("SystemExit", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Interrupt")) {
             defineClass("Interrupt", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("SignalException")) {
             defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
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
         if (profile.allowClass("NoMethodError")) {
             defineClass("NoMethodError", nameError, nameError.getAllocator());
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
             defineClass("LocalJumpError", standardError, standardError.getAllocator());
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
 
     public Node parse(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope);
     }
 
     public Node parse(String content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope);
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
         java.io.OutputStream os = ((RubyIO) getGlobalVariables().get("$stderr")).getOutStream();
         if(null != os) {
             return new PrintStream(os);
         } else {
             return new PrintStream(new org.jruby.util.SwallowingOutputStream());
         }
     }
 
     public InputStream getInputStream() {
         return ((RubyIO) getGlobalVariables().get("$stdin")).getInStream();
     }
 
     public PrintStream getOutputStream() {
         return new PrintStream(((RubyIO) getGlobalVariables().get("$stdout")).getOutStream());
     }
 
     public RubyModule getClassFromPath(String path) {
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
         }
         IRubyObject type = evalScript(path);
         if (!(type instanceof RubyModule)) {
             throw newTypeError("class path " + path + " does not point class");
         }
         return (RubyModule) type;
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
     public void loadScript(RubyString scriptName, RubyString source) {
         loadScript(scriptName.toString(), new StringReader(source.toString()));
     }
 
     public void loadScript(String scriptName, Reader source) {
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
 
             Node node = parse(source, scriptName, null);
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
     public void loadNode(String scriptName, Node node) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
 
     /** Loads, compiles and interprets a Ruby file.
      *  Used by Kernel#require.
      *
      *  @mri rb_load
      */
     public void loadFile(File file) {
         assert file != null : "No such file to load";
         BufferedReader source = null;
         try {
             source = new BufferedReader(new FileReader(file));
             loadScript(file.getPath().replace(File.separatorChar, '/'), source);
         } catch (IOException ioExcptn) {
             throw newIOErrorFromException(ioExcptn);
         } finally {
             try {
                 if (source == null) {
                     source.close();
                 }
             } catch (IOException ioe) {}
         }
     }
 
     /** Call the trace function
      *
      * MRI: eval.c - call_trace_func
      *
      */
     public void callTraceFunction(ThreadContext context, String event, ISourcePosition position,
-            IRubyObject self, String name, IRubyObject type) {
+            RubyBinding binding, String name, IRubyObject type) {
         if (traceFunction == null) return;
 
         if (!context.isWithinTrace()) {
             context.setWithinTrace(true);
 
             ISourcePosition savePosition = context.getPosition();
             String file = position.getFile();
 
             if (file == null) file = "(ruby)";
             if (type == null) type = getFalse();
 
             context.preTrace();
             try {
                 traceFunction.call(new IRubyObject[] {
                     newString(event), // event name
                     newString(file), // filename
                     newFixnum(position.getStartLine() + 1), // line numbers should be 1-based
                     name != null ? RubySymbol.newSymbol(this, name) : getNil(),
-                    self != null ? self : getNil(),
+                    binding != null ? binding : getNil(),
                     type
                 });
             } finally {
                 context.postTrace();
                 context.setPosition(savePosition);
                 context.setWithinTrace(false);
             }
         }
     }
 
     public RubyProc getTraceFunction() {
         return traceFunction;
     }
 
     public void setTraceFunction(RubyProc traceFunction) {
         this.traceFunction = traceFunction;
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
     
     public void addFinalizer(RubyObject.Finalizer finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap();
             }
         }
         
         synchronized (finalizers) {
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeFinalizer(RubyObject.Finalizer finalizer) {
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
                 for (Iterator finalIter = finalizers.keySet().iterator(); finalIter.hasNext();) {
                     ((RubyObject.Finalizer)finalIter.next()).finalize();
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
 
     public RaiseException newNoMethodError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.getClass("NoMethodError"), message, name), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return new RaiseException(new RubyNameError(this, this.getClass("NameError"), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String message) {
         return newRaiseException(getClass("LocalJumpError"), message);
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
         RaiseException re = newRaiseException(getClass("SystemExit"), "");
         re.getException().setInstanceVariable("status", newFixnum(status));
 
         return re;
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
         return newRaiseException(getClass("TypeError"), "wrong argument type " + receivedObject.getMetaClass() + " (expected " + expectedType);
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
     public boolean registerInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
         if(null == val) {
             val = new java.util.IdentityHashMap();
             inspect.set(val);
         }
         if(val.containsKey(obj)) {
             return false;
         }
         val.put(obj, null);
         return true;
     }
 
     public void unregisterInspecting(Object obj) {
         java.util.Map val = (java.util.Map)inspect.get();
         val.remove(obj);
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
         return jrubyHome;
     }
     
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
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
         return (System.getSecurityManager() != null);
     }
 }
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index bcba400117..499cd51a64 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1191 +1,1194 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Daniel Steer <damian.steer@hp.com>
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
 
 import java.lang.reflect.Array;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Pack;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         arrayc.index = ClassIndex.ARRAY;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyArray.class);
 
         arrayc.includeModule(runtime.getModule("Enumerable"));
         arrayc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
 
         arrayc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         arrayc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s")); 
         arrayc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         arrayc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         arrayc.defineFastMethod("to_ary", callbackFactory.getFastMethod("to_ary"));
         arrayc.defineFastMethod("frozen?", callbackFactory.getFastMethod("frozen"));
 
         arrayc.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         arrayc.defineFastMethod("[]", callbackFactory.getFastOptMethod("aref"));
         arrayc.defineFastMethod("[]=", callbackFactory.getFastOptMethod("aset"));
         arrayc.defineFastMethod("at", callbackFactory.getFastMethod("at", RubyKernel.IRUBY_OBJECT));
         arrayc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
         arrayc.defineFastMethod("first", callbackFactory.getFastOptMethod("first"));
         arrayc.defineFastMethod("last", callbackFactory.getFastOptMethod("last"));
         arrayc.defineFastMethod("concat", callbackFactory.getFastMethod("concat", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("<<", callbackFactory.getFastMethod("append", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("push", callbackFactory.getFastOptMethod("push_m"));
         arrayc.defineFastMethod("pop", callbackFactory.getFastMethod("pop"));
         arrayc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
         arrayc.defineFastMethod("unshift", callbackFactory.getFastOptMethod("unshift_m"));
         arrayc.defineFastMethod("insert", callbackFactory.getFastOptMethod("insert"));
         arrayc.defineMethod("each", callbackFactory.getMethod("each"));
         arrayc.defineMethod("each_index", callbackFactory.getMethod("each_index"));
         arrayc.defineMethod("reverse_each", callbackFactory.getMethod("reverse_each"));
         arrayc.defineFastMethod("length", callbackFactory.getFastMethod("length"));
         arrayc.defineAlias("size", "length");
         arrayc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
         arrayc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("rindex", callbackFactory.getFastMethod("rindex", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indexes"));
         arrayc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indexes"));
         arrayc.defineFastMethod("join", callbackFactory.getFastOptMethod("join_m"));
         arrayc.defineFastMethod("reverse", callbackFactory.getFastMethod("reverse"));
         arrayc.defineFastMethod("reverse!", callbackFactory.getFastMethod("reverse_bang"));
         arrayc.defineMethod("sort", callbackFactory.getMethod("sort"));
         arrayc.defineMethod("sort!", callbackFactory.getMethod("sort_bang"));
         arrayc.defineMethod("collect", callbackFactory.getMethod("collect"));
         arrayc.defineMethod("collect!", callbackFactory.getMethod("collect_bang"));
         arrayc.defineMethod("map", callbackFactory.getMethod("collect"));
         arrayc.defineMethod("map!", callbackFactory.getMethod("collect_bang"));
         arrayc.defineMethod("select", callbackFactory.getMethod("select"));
         arrayc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
         arrayc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
         arrayc.defineFastMethod("delete_at", callbackFactory.getFastMethod("delete_at", RubyKernel.IRUBY_OBJECT));
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
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass);
         }
     };
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte AREF_SWITCHVALUE = 2;
     public static final byte ASET_SWITCHVALUE = 3;
     public static final byte POP_SWITCHVALUE = 4;
     public static final byte PUSH_SWITCHVALUE = 5;
     public static final byte NIL_P_SWITCHVALUE = 6;
     public static final byte EQUALEQUAL_SWITCHVALUE = 7;
     public static final byte UNSHIFT_SWITCHVALUE = 8;
     public static final byte OP_LSHIFT_SWITCHVALUE = 9;
     public static final byte EMPTY_P_SWITCHVALUE = 10;
     public static final byte TO_S_SWITCHVALUE = 11;
     public static final byte AT_SWITCHVALUE = 12;
     public static final byte TO_ARY_SWITCHVALUE = 13;
     public static final byte TO_A_SWITCHVALUE = 14;
     public static final byte HASH_SWITCHVALUE = 15;
     public static final byte OP_TIMES_SWITCHVALUE = 16;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 17;
     public static final byte LENGTH_SWITCHVALUE = 18;
     public static final byte LAST_SWITCHVALUE = 19;
     public static final byte SHIFT_SWITCHVALUE = 20;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex,
             String name, IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_plus(args[0]);
         case AREF_SWITCHVALUE:
             return aref(args);
         case ASET_SWITCHVALUE:
             return aset(args);
         case POP_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return pop();
         case PUSH_SWITCHVALUE:
             return push_m(args);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_equal(args[0]);
         case UNSHIFT_SWITCHVALUE:
             return unshift_m(args);
         case OP_LSHIFT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return append(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty_p();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case AT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return at(args[0]);
         case TO_ARY_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_ary();
         case TO_A_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_a();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_times(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_cmp(args[0]);
         case LENGTH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return length();
         case LAST_SWITCHVALUE:
             return last(args);
         case SHIFT_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return shift();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
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
     private boolean tmpLock = false;
     private boolean shared = false;
 
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
         return !shared ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return (!shared && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len, RubyClass klass, boolean objectSpace) {
         RubyArray sharedArray = new RubyArray(getRuntime(), klass, objectSpace);
         shared = true;
         sharedArray.values = values;
         sharedArray.shared = true;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         testFrozen("array");
 
         if (tmpLock) {
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
         if (shared) {
             IRubyObject[] vals = reserve(realLength);
             shared = false;
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
 
         origArr.shared = true;
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
         shared = true;
 
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
         dup.setTaint(isTaint()); // from DUP_SETUP
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
 
         if (!shared) {
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
 
         if (!shared) shared = true;
 
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
         return getRuntime().newBoolean(isFrozen() || tmpLock);
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
 
     /** rb_ary_inspect
      *
      */
     public IRubyObject inspect() {
         if (realLength == 0) return getRuntime().newString("[]");
 
diff --git a/src/org/jruby/RubyBignum.java b/src/org/jruby/RubyBignum.java
index 622771895e..05eb9835e6 100644
--- a/src/org/jruby/RubyBignum.java
+++ b/src/org/jruby/RubyBignum.java
@@ -1,660 +1,663 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyBignum extends RubyInteger {
     public static RubyClass createBignumClass(Ruby runtime) {
         RubyClass bignum = runtime.defineClass("Bignum", runtime.getClass("Integer"),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         bignum.index = ClassIndex.BIGNUM;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyBignum.class);
 
         bignum.defineFastMethod("to_s", callbackFactory.getFastOptMethod("to_s"));
         bignum.defineFastMethod("coerce", callbackFactory.getFastMethod("coerce", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("-@", callbackFactory.getFastMethod("uminus"));
         bignum.defineFastMethod("+", callbackFactory.getFastMethod("plus", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("-", callbackFactory.getFastMethod("minus", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("*", callbackFactory.getFastMethod("mul", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("/", callbackFactory.getFastMethod("div", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("%", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("div", callbackFactory.getFastMethod("div", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("divmod", callbackFactory.getFastMethod("divmod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("modulo", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("remainder", callbackFactory.getFastMethod("remainder", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("quo", callbackFactory.getFastMethod("quo", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("**", callbackFactory.getFastMethod("pow", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("&", callbackFactory.getFastMethod("and", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("|", callbackFactory.getFastMethod("or", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("^", callbackFactory.getFastMethod("xor", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("~", callbackFactory.getFastMethod("neg"));
         bignum.defineFastMethod("<<", callbackFactory.getFastMethod("lshift", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod(">>", callbackFactory.getFastMethod("rshift", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
 
         bignum.defineFastMethod("<=>", callbackFactory.getFastMethod("cmp", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         bignum.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         bignum.defineFastMethod("abs", callbackFactory.getFastMethod("abs"));
         bignum.defineFastMethod("size", callbackFactory.getFastMethod("size"));
 
         return bignum;
     }
 
     private static final int BIT_SIZE = 64;
     private static final long MAX = (1L << (BIT_SIZE - 1)) - 1;
     private static final BigInteger LONG_MAX = BigInteger.valueOf(MAX);
     private static final BigInteger LONG_MIN = BigInteger.valueOf(-MAX - 1);
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
     public static final byte TO_S_SWITCHVALUE = 4;
     public static final byte TO_I_SWITCHVALUE = 5;
     public static final byte HASH_SWITCHVALUE = 6;
     public static final byte OP_TIMES_SWITCHVALUE = 7;
     public static final byte EQUALEQUAL_SWITCHVALUE = 8;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 9;
 
     private final BigInteger value;
 
     public RubyBignum(Ruby runtime, BigInteger value) {
         super(runtime, runtime.getClass("Bignum"));
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return plus(args[0]);
         case OP_MINUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return minus(args[0]);
         case TO_S_SWITCHVALUE:
             return to_s(args);
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return mul(args[0]);
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return cmp(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.BIGNUM;
     }
 
     public static RubyBignum newBignum(Ruby runtime, long value) {
         return newBignum(runtime, BigInteger.valueOf(value));
     }
 
     public static RubyBignum newBignum(Ruby runtime, double value) {
         return newBignum(runtime, new BigDecimal(value).toBigInteger());
     }
 
     public static RubyBignum newBignum(Ruby runtime, BigInteger value) {
         return new RubyBignum(runtime, value);
     }
 
     public static RubyBignum newBignum(Ruby runtime, String value) {
         return new RubyBignum(runtime, new BigInteger(value));
     }
 
     public double getDoubleValue() {
         return big2dbl(this);
     }
 
     public long getLongValue() {
         return big2long(this);
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public BigInteger getValue() {
         return value;
     }
 
     /*  ================
      *  Utility Methods
      *  ================ 
      */
 
     /* If the value will fit in a Fixnum, return one of those. */
     /** rb_big_norm
      * 
      */
     public static RubyInteger bignorm(Ruby runtime, BigInteger bi) {
         if (bi.compareTo(LONG_MIN) < 0 || bi.compareTo(LONG_MAX) > 0) {
             return newBignum(runtime, bi);
         }
         return runtime.newFixnum(bi.longValue());
     }
 
     /** rb_big2long
      * 
      */
     public static long big2long(RubyBignum value) {
         BigInteger big = value.getValue();
 
         if (big.compareTo(LONG_MIN) < 0 || big.compareTo(LONG_MAX) > 0) {
             throw value.getRuntime().newRangeError("bignum too big to convert into `long'");
     }
         return big.longValue();
         }
 
     /** rb_big2dbl
      * 
      */
     public static double big2dbl(RubyBignum value) {
         BigInteger big = value.getValue();
         double dbl = big.doubleValue();
         if (dbl == Double.NEGATIVE_INFINITY || dbl == Double.POSITIVE_INFINITY) {
             value.getRuntime().getWarnings().warn("Bignum out of Float range");
     }
         return dbl;
     }
 
     /** rb_int2big
      * 
      */
     public static BigInteger fix2big(RubyFixnum arg) {
         return BigInteger.valueOf(arg.getLongValue());
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_big_to_s
      * 
      */
     public IRubyObject to_s(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         int base = args.length == 0 ? 10 : num2int(args[0]);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
     }
         return getRuntime().newString(getValue().toString(base));
     }
 
     /** rb_big_coerce
      * 
      */
     public IRubyObject coerce(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return getRuntime().newArray(newBignum(getRuntime(), ((RubyFixnum) other).getLongValue()), this);
         } else if (other instanceof RubyBignum) {
             return getRuntime().newArray(newBignum(getRuntime(), ((RubyBignum) other).getValue()), this);
     }
 
         throw getRuntime().newTypeError("Can't coerce " + other.getMetaClass().getName() + " to Bignum");
     }
 
     /** rb_big_uminus
      * 
      */
     public IRubyObject uminus() {
         return bignorm(getRuntime(), value.negate());
     }
 
     /** rb_big_plus
      * 
      */
     public IRubyObject plus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.add(fix2big(((RubyFixnum) other))));
         }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.add(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) + ((RubyFloat) other).getDoubleValue());
     }
         return coerceBin("+", other);
     }
 
     /** rb_big_minus
      * 
      */
     public IRubyObject minus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.subtract(fix2big(((RubyFixnum) other))));
     }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.subtract(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) - ((RubyFloat) other).getDoubleValue());
     }
         return coerceBin("-", other);
     }
 
     /** rb_big_mul
      * 
      */
     public IRubyObject mul(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.multiply(fix2big(((RubyFixnum) other))));
         }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.multiply(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("*", other);
     }
 
     /** rb_big_div
      * 
      */
     public IRubyObject div(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyFloat) other).getDoubleValue());
         } else {
             return coerceBin("/", other);
         }
 
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
 
         BigInteger[] results = value.divideAndRemainder(otherValue);
 
         if (results[0].signum() == -1 && results[1].signum() != 0) {
             return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
         }
         return bignorm(getRuntime(), results[0]);
     }
 
     /** rb_big_divmod
      * 
      */
     public IRubyObject divmod(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("divmod", other);
         }
 
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
 
         BigInteger[] results = value.divideAndRemainder(otherValue);
 
         if (results[0].signum() == -1 && results[1].signum() != 0) {
             return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
     	}
         final Ruby runtime = getRuntime();
         return RubyArray.newArray(getRuntime(), bignorm(runtime, results[0]), bignorm(runtime, results[1]));
     }
 
     /** rb_big_modulo
      * 
      */
     public IRubyObject mod(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("%", other);
         }
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
         BigInteger result = value.mod(otherValue.abs());
         if (otherValue.signum() == -1) {
             result = otherValue.add(result);
         }
         return bignorm(getRuntime(), result);
 
             }
 
     /** rb_big_remainder
      * 
      */
     public IRubyObject remainder(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big(((RubyFixnum) other));
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("remainder", other);
         }
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
     }
         return bignorm(getRuntime(), value.remainder(otherValue));
     }
 
     /** rb_big_quo
 
      * 
      */
     public IRubyObject quo(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyNumeric) other).getDoubleValue());
         } else {
             return coerceBin("quo", other);
     	}
     }
 
     /** rb_big_pow
      * 
      */
     public IRubyObject pow(IRubyObject other) {
         double d;
         if (other instanceof RubyFixnum) {
             RubyFixnum fix = (RubyFixnum) other;
             long fixValue = fix.getLongValue();
             // MRI issuses warning here on (RBIGNUM(x)->len * SIZEOF_BDIGITS * yy > 1024*1024)
             if (((value.bitLength() + 7) / 8) * 4 * Math.abs(fixValue) > 1024 * 1024) {
                 getRuntime().getWarnings().warn("in a**b, b may be too big");
     	}
             if (fixValue >= 0) {
                 return bignorm(getRuntime(), value.pow((int) fixValue)); // num2int is also implemented
             } else {
                 return RubyFloat.newFloat(getRuntime(), Math.pow(big2dbl(this), (double)fixValue));
             }
         } else if (other instanceof RubyBignum) {
             getRuntime().getWarnings().warn("in a**b, b may be too big");
             d = ((RubyBignum) other).getDoubleValue();
         } else if (other instanceof RubyFloat) {
             d = ((RubyFloat) other).getDoubleValue();
         } else {
             return coerceBin("**", other);
 
     }
         return RubyFloat.newFloat(getRuntime(), Math.pow(big2dbl(this), d));
     }
 
     /** rb_big_and
      * 
      */
     public IRubyObject and(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.and(((RubyBignum) other).value));
         } else if(other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.and(fix2big((RubyFixnum)other)));
         }
         return coerceBin("&", other);
     }
 
     /** rb_big_or
      * 
      */
     public IRubyObject or(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.or(((RubyBignum) other).value));
         }
         if (other instanceof RubyFixnum) { // no bignorm here needed
             return bignorm(getRuntime(), value.or(fix2big((RubyFixnum)other)));
         }
         return coerceBin("|", other);
     }
 
     /** rb_big_xor
      * 
      */
     public IRubyObject xor(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.xor(((RubyBignum) other).value));
 		}
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.xor(BigInteger.valueOf(((RubyFixnum) other).getLongValue())));
     }
 
         return coerceBin("^", other);
     }
 
     /** rb_big_neg     
      * 
      */
     public IRubyObject neg() {
         return RubyBignum.newBignum(getRuntime(), value.not());
     	}
 
     /** rb_big_lshift     
      * 
      */
     public IRubyObject lshift(IRubyObject other) {
         int width = num2int(other);
         if (width < 0) {
             return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
         }
     	
         return bignorm(getRuntime(), value.shiftLeft(width));
     }
 
     /** rb_big_rshift     
      * 
      */
     public IRubyObject rshift(IRubyObject other) {
         int width = num2int(other);
 
         if (width < 0) {
             return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
         }
         return bignorm(getRuntime(), value.shiftRight(width));
     }
 
     /** rb_big_aref     
      * 
      */
     public RubyFixnum aref(IRubyObject other) {
         if (other instanceof RubyBignum) {
             if (((RubyBignum) other).value.signum() >= 0 || value.signum() == -1) {
                 return RubyFixnum.zero(getRuntime());
             }
             return RubyFixnum.one(getRuntime());
         }
         int position = num2int(other);
         if (position < 0) {
             return RubyFixnum.zero(getRuntime());
         }
         
         return value.testBit(num2int(other)) ? RubyFixnum.one(getRuntime()) : RubyFixnum.zero(getRuntime());
     }
 
     /** rb_big_cmp     
      * 
      */
     public IRubyObject cmp(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             return dbl_cmp(getRuntime(), big2dbl(this), ((RubyFloat) other).getDoubleValue());
         } else {
             return coerceCmp("<=>", other);
         }
 
         // wow, the only time we can use the java protocol ;)        
         return RubyFixnum.newFixnum(getRuntime(), value.compareTo(otherValue));
     }
 
     /** rb_big_eq     
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             double a = ((RubyFloat) other).getDoubleValue();
             if (Double.isNaN(a)) {
                 return getRuntime().getFalse();
             }
             return RubyBoolean.newBoolean(getRuntime(), a == big2dbl(this));
         } else {
             return super.equal(other);
         }
         return RubyBoolean.newBoolean(getRuntime(), value.compareTo(otherValue) == 0);
     }
 
     /** rb_big_eql     
      * 
      */
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return RubyBoolean.newBoolean(getRuntime(), value.compareTo(((RubyBignum)other).value) == 0);
         }
         return getRuntime().getFalse();
     }
 
     /** rb_big_hash
      * 
      */
     public RubyFixnum hash() {
         return getRuntime().newFixnum(value.hashCode());
         }
 
     /** rb_big_to_f
      * 
      */
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
     /** rb_big_abs
      * 
      */
     public IRubyObject abs() {
         return RubyBignum.newBignum(getRuntime(), value.abs());
     }
 
     /** rb_big_size
      * 
      */
     public RubyFixnum size() {
         return getRuntime().newFixnum((value.bitLength() + 7) / 8);
     }
 
     public static void marshalTo(RubyBignum bignum, MarshalStream output) throws IOException {
         output.write(bignum.value.signum() >= 0 ? '+' : '-');
         
         BigInteger absValue = bignum.value.abs();
         
         byte[] digits = absValue.toByteArray();
         
         boolean oddLengthNonzeroStart = (digits.length % 2 != 0 && digits[0] != 0);
         int shortLength = digits.length / 2;
         if (oddLengthNonzeroStart) {
             shortLength++;
         }
         output.writeInt(shortLength);
         
         for (int i = 1; i <= shortLength * 2 && i <= digits.length; i++) {
             output.write(digits[digits.length - i]);
         }
         
         if (oddLengthNonzeroStart) {
             // Pad with a 0
             output.write(0);
         }
     }
 
     public static RubyBignum unmarshalFrom(UnmarshalStream input) throws IOException {
         boolean positive = input.readUnsignedByte() == '+';
         int shortLength = input.unmarshalInt();
 
         // BigInteger required a sign byte in incoming array
         byte[] digits = new byte[shortLength * 2 + 1];
 
         for (int i = digits.length - 1; i >= 1; i--) {
         	digits[i] = input.readSignedByte();
         }
 
         BigInteger value = new BigInteger(digits);
         if (!positive) {
             value = value.negate();
         }
 
         RubyBignum result = newBignum(input.getRuntime(), value);
         input.registerLinkTarget(result);
         return result;
     }
 }
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index c5452e3cbc..f32cb3c5fa 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,391 +1,394 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.IOException;
 import java.util.Map;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyClass extends RubyModule {
 	
     private final Ruby runtime;
     
     // the default allocator
     private final ObjectAllocator allocator;
     
     private ObjectMarshal marshal;
     
     private static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             Map iVars = object.getInstanceVariablesSnapshot();
             
             marshalStream.dumpInstanceVars(iVars);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultInstanceVarsUnmarshal(result);
 
             return result;
         }
     };
 
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     protected RubyClass(RubyClass superClass, ObjectAllocator allocator) {
         this(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, allocator, null, null);
 
         infectBy(superClass);
     }
 
     protected RubyClass(Ruby runtime, RubyClass superClass, ObjectAllocator allocator) {
         this(runtime, null, superClass, allocator, null, null);
     }
 
     protected RubyClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator) {
         this(runtime, metaClass, superClass, allocator, null, null);
     }
     
     protected RubyClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass, superClass, parentCRef, name);
         this.allocator = allocator;
         this.runtime = runtime;
         
         // use parent's marshal, or default object marshal by default
         if (superClass != null) {
             this.marshal = superClass.getMarshal();
         } else {
             this.marshal = DEFAULT_OBJECT_MARSHAL;
         }
     }
     
     /**
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapMetaClass(Ruby runtime, String className, 
             RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList cref) {
         RubyClass objectClass = new RubyClass(runtime, null, superClass, allocator, cref, className);
         
         return objectClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
 
     public static final byte EQQ_SWITCHVALUE = 1;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_eqq(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public final IRubyObject allocate() {
         return getAllocator().allocate(getRuntime(), this);
     }
     
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(getRuntime(), obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(getRuntime(), this, unmarshalStream);
     }
     
     public static RubyClass newClassClass(Ruby runtime, RubyClass moduleClass) {
         ObjectAllocator defaultAllocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 IRubyObject instance = new RubyObject(runtime, klass);
                 instance.setMetaClass(klass);
 
                 return instance;
             }
         };
         
         RubyClass classClass = new RubyClass(
                 runtime,
                 null /* FIXME: should be something else? */,
                 moduleClass,
                 defaultAllocator,
                 null,
                 "Class");
         
         classClass.index = ClassIndex.CLASS;
         
         return classClass;
     }
     
     /* (non-Javadoc)
 	 * @see org.jruby.RubyObject#getRuntime()
 	 */
 	public Ruby getRuntime() {
 		return runtime;
 	}
 
     public boolean isModule() {
         return false;
     }
 
     public boolean isClass() {
         return true;
     }
 
     public static void createClassClass(RubyClass classClass) {
         CallbackFactory callbackFactory = classClass.getRuntime().callbackFactory(RubyClass.class);
         classClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newClass"));
         classClass.defineFastMethod("allocate", callbackFactory.getFastMethod("allocate"));
         classClass.defineMethod("new", callbackFactory.getOptMethod("newInstance"));
         classClass.defineMethod("superclass", callbackFactory.getMethod("superclass"));
         classClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", RubyKernel.IRUBY_OBJECT));
         classClass.defineMethod("inherited", callbackFactory.getSingletonMethod("inherited", RubyKernel.IRUBY_OBJECT));
         classClass.undefineMethod("module_function");
     }
     
     public static IRubyObject inherited(IRubyObject recv, IRubyObject arg, Block block) {
         return recv.getRuntime().getNil();
     }
 
     /** Invokes if  a class is inherited from an other  class.
      * 
      * MRI: rb_class_inherited
      * 
      * @since Ruby 1.6.7
      * 
      */
     public void inheritedBy(RubyClass superType) {
         if (superType == null) {
             superType = getRuntime().getObject();
         }
         superType.callMethod(getRuntime().getCurrentContext(), "inherited", this);
     }
 
     public boolean isSingleton() {
         return false;
     }
 
 //    public RubyClass getMetaClass() {
 //        RubyClass type = super.getMetaClass();
 //
 //        return type != null ? type : getRuntime().getClass("Class");
 //    }
 
     public RubyClass getRealClass() {
         return this;
     }
 
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         return new RubyClass(runtime, runtime.getClass("Class"), superClass, superClass.getAllocator(), parentCRef, name);
     }
 
     public static RubyClass cloneClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         return new RubyClass(runtime, metaClass, superClass, allocator, parentCRef, name);
     }
 
     /** Create a new subclass of this class.
      * @return the new sublass
      * @throws TypeError if this is class `Class'
      * @mri rb_class_new
      */
     protected RubyClass subclass() {
         if (this == getRuntime().getClass("Class")) {
             throw getRuntime().newTypeError("can't make subclass of Class");
         }
         return new RubyClass(this, getAllocator());
     }
 
     /** rb_class_new_instance
      *
      */
     public IRubyObject newInstance(IRubyObject[] args, Block block) {
         IRubyObject obj = (IRubyObject) allocate();
         obj.callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
         return obj;
     }
     
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     /** rb_class_s_new
      *
      */
     public static RubyClass newClass(IRubyObject recv, IRubyObject[] args, Block block) {
         final Ruby runtime = recv.getRuntime();
 
         RubyClass superClass;
         if (args.length > 0) {
             if (args[0] instanceof RubyClass) {
                 superClass = (RubyClass) args[0];
             } else {
                 throw runtime.newTypeError(
                     "wrong argument type " + args[0].getType().getName() + " (expected Class)");
             }
         } else {
             superClass = runtime.getObject();
         }
 
         ThreadContext tc = runtime.getCurrentContext();
         // use allocator of superclass, since this will be a pure Ruby class
         RubyClass newClass = superClass.newSubClass(null, superClass.getAllocator(),tc.peekCRef());
 
         // call "initialize" method
         newClass.callInit(args, block);
 
         // call "inherited" method of the superclass
         newClass.inheritedBy(superClass);
 
 		if (block.isGiven()) block.yield(tc, null, newClass, newClass, false);
 
 		return newClass;
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */
     public IRubyObject superclass(Block block) {
         RubyClass superClass = getSuperClass();
         while (superClass != null && superClass.isIncluded()) {
             superClass = superClass.getSuperClass();
         }
 
         return superClass != null ? superClass : getRuntime().getNil();
     }
 
     /** rb_class_s_inherited
      *
      */
     public static IRubyObject inherited(RubyClass recv) {
         throw recv.getRuntime().newTypeError("can't make subclass of Class");
     }
 
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.writeString(clazz.getName());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream output) throws java.io.IOException {
         return (RubyClass) RubyModule.unmarshalFrom(output);
     }
 
     public RubyClass newSubClass(String name, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         RubyClass classClass = runtime.getClass("Class");
         
         // Cannot subclass 'Class' or metaclasses
         if (this == classClass) {
             throw runtime.newTypeError("can't make subclass of Class");
         } else if (this instanceof MetaClass) {
             throw runtime.newTypeError("can't make subclass of virtual class");
         }
 
         RubyClass newClass = new RubyClass(runtime, classClass, this, allocator, parentCRef, name);
 
         newClass.makeMetaClass(getMetaClass(), newClass.getCRef());
         newClass.inheritedBy(this);
 
         if(null != name) {
             ((RubyModule)parentCRef.getValue()).setConstant(name, newClass);
         }
 
         return newClass;
     }
     
     protected IRubyObject doClone() {
     	return RubyClass.cloneClass(getRuntime(), getMetaClass(), getSuperClass(), getAllocator(), null/*FIXME*/, null);
     }
     
     /** rb_class_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original){
 
         if (((RubyClass) original).isSingleton()){
             throw getRuntime().newTypeError("can't copy singleton class");
         }
         
         super.initialize_copy(original);
         
         return this;        
     }    
 }
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index fe1be0a505..ff7254d779 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,749 +1,752 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
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
 
 import java.math.BigInteger;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Convert;
 
 /** Implementation of the Fixnum class.
  *
  * @author jpetersen
  */
 public class RubyFixnum extends RubyInteger {
     
     public static RubyClass createFixnumClass(Ruby runtime) {
         RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getClass("Integer"),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         fixnum.index = ClassIndex.FIXNUM;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFixnum.class);
 
         fixnum.includeModule(runtime.getModule("Precision"));
         fixnum.getMetaClass().defineFastMethod("induced_from", callbackFactory.getFastSingletonMethod(
                 "induced_from", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("to_s", callbackFactory.getFastOptMethod("to_s"));
 
         fixnum.defineFastMethod("id2name", callbackFactory.getFastMethod("id2name"));
         fixnum.defineFastMethod("to_sym", callbackFactory.getFastMethod("to_sym"));
 
         fixnum.defineFastMethod("-@", callbackFactory.getFastMethod("uminus"));
         fixnum.defineFastMethod("+", callbackFactory.getFastMethod("plus", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("-", callbackFactory.getFastMethod("minus", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("*", callbackFactory.getFastMethod("mul", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("/", callbackFactory.getFastMethod("div_slash", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("div", callbackFactory.getFastMethod("div_div", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("%", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("modulo", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("divmod", callbackFactory.getFastMethod("divmod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("quo", callbackFactory.getFastMethod("quo", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("**", callbackFactory.getFastMethod("pow", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("abs", callbackFactory.getFastMethod("abs"));
 
         fixnum.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<=>", callbackFactory.getFastMethod("cmp", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod(">", callbackFactory.getFastMethod("gt", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod(">=", callbackFactory.getFastMethod("ge", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<", callbackFactory.getFastMethod("lt", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<=", callbackFactory.getFastMethod("le", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("~", callbackFactory.getFastMethod("rev"));
         fixnum.defineFastMethod("&", callbackFactory.getFastMethod("and", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("|", callbackFactory.getFastMethod("or", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("^", callbackFactory.getFastMethod("xor", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<<", callbackFactory.getFastMethod("lshift", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod(">>", callbackFactory.getFastMethod("rshift", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         fixnum.defineFastMethod("size", callbackFactory.getFastMethod("size"));
         fixnum.defineFastMethod("zero?", callbackFactory.getFastMethod("zero_p"));
 
         return fixnum;
     }    
     
     private long value;
     private static final int BIT_SIZE = 64;
     private static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     public static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1;
 
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
     public static final byte TO_S_SWITCHVALUE = 4;
     public static final byte TO_I_SWITCHVALUE = 5;
     public static final byte TO_INT_SWITCHVALUE = 6;
     public static final byte HASH_SWITCHVALUE = 7;
     public static final byte OP_GT_SWITCHVALUE = 8;
     public static final byte OP_TIMES_SWITCHVALUE = 9;
     public static final byte EQUALEQUAL_SWITCHVALUE = 10;
     public static final byte OP_LE_SWITCHVALUE = 11;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 12;
 
     public RubyFixnum(Ruby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(Ruby runtime, long value) {
         super(runtime, runtime.getFixnum());
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return plus(args[0]);
         case OP_MINUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return minus(args[0]);
         case OP_LT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return lt(args[0]);
         case TO_S_SWITCHVALUE:
             return to_s(args);
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case TO_INT_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_int();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_GT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return gt(args[0]);
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return mul(args[0]);
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case OP_LE_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return le(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return cmp(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.FIXNUM;
     }
     
     public boolean isImmediate() {
     	return true;
     }
 
     public Class getJavaClass() {
         return Long.TYPE;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return value;
     }
 
     public static RubyFixnum newFixnum(Ruby runtime, long value) {
         RubyFixnum fixnum;
         RubyFixnum[] fixnumCache = runtime.getFixnumCache();
 
         if (value >= 0 && value < fixnumCache.length) {
             fixnum = fixnumCache[(int) value];
             if (fixnum == null) {
                 fixnum = new RubyFixnum(runtime, value);
                 fixnumCache[(int) value] = fixnum;
             }
         } else {
             fixnum = new RubyFixnum(runtime, value);
         }
         return fixnum;
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public static RubyFixnum zero(Ruby runtime) {
         return newFixnum(runtime, 0);
     }
 
     public static RubyFixnum one(Ruby runtime) {
         return newFixnum(runtime, 1);
     }
 
     public static RubyFixnum minus_one(Ruby runtime) {
         return newFixnum(runtime, -1);
     }
 
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
 
     public final int hashCode() {
         return (int)(value ^ value >>> 32);
     }
 
     public boolean equals(Object other) {
         if (other == this) {
             return true;
         }
         
         if (other instanceof RubyFixnum) { 
             RubyFixnum num = (RubyFixnum)other;
             
             if (num.value == value) {
                 return true;
             }
         }
         
         return false;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** fix_to_s
      * 
      */
     public RubyString to_s(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         
         int base = args.length == 0 ? 10 : num2int(args[0]);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
             }
         return getRuntime().newString(Convert.longToByteList(value, base));
         }
 
     /** fix_id2name
      * 
      */
     public IRubyObject id2name() {
         String symbol = RubySymbol.getSymbol(getRuntime(), value);
         if (symbol != null) {
             return getRuntime().newString(symbol);
     }
         return getRuntime().getNil();
     }
 
     /** fix_to_sym
      * 
      */
     public IRubyObject to_sym() {
         String symbol = RubySymbol.getSymbol(getRuntime(), value);
         if (symbol != null) {
             return RubySymbol.newSymbol(getRuntime(), symbol);
     }
         return getRuntime().getNil();
     }
 
     /** fix_uminus
      * 
      */
     public IRubyObject uminus() {
         if (value == MIN) { // a gotcha
             return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
         }
         return RubyFixnum.newFixnum(getRuntime(), -value);
         }
 
     /** fix_plus
      * 
      */
     public IRubyObject plus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value + otherValue;
             if ((~(value ^ otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).plus(other);
             }
 		return newFixnum(result);
     }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).plus(this);
     }
         if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value + ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("+", other);
     }
 
     /** fix_minus
      * 
      */
     public IRubyObject minus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value - otherValue;
             if ((~(value ^ ~otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).minus(other);
     }
             return newFixnum(result);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), value).minus(other);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value - ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("-", other);
     }
 
     /** fix_mul
      * 
      */
     public IRubyObject mul(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == 0) {
                 return RubyFixnum.zero(getRuntime());
     }
             long result = value * otherValue;
             IRubyObject r = newFixnum(getRuntime(),result);
             if(RubyNumeric.fix2long(r) != result || result/value != otherValue) {
                 return (RubyNumeric) RubyBignum.newBignum(getRuntime(), value).mul(other);
             }
             return r;
         } else if (other instanceof RubyBignum) {
             return ((RubyBignum) other).mul(this);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("*", other);
     }
     
     /** fix_div
      * here is terrible MRI gotcha:
      * 1.div 3.0 -> 0
      * 1 / 3.0   -> 0.3333333333333333
      * 
      * MRI is also able to do it in one place by looking at current frame in rb_num_coerce_bin:
      * rb_funcall(x, ruby_frame->orig_func, 1, y);
      * 
      * also note that RubyFloat doesn't override Numeric.div
      */
     public IRubyObject div_div(IRubyObject other) {
         return idiv(other, "div");
     	}
     	
     public IRubyObject div_slash(IRubyObject other) {
         return idiv(other, "/");
     }
 
     public IRubyObject idiv(IRubyObject other, String method) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
             }
             
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
             }
 
             return getRuntime().newFixnum(div);
         } 
         return coerceBin(method, other);
     }
         
     /** fix_mod
      * 
      */
     public IRubyObject mod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             // Java / and % are not the same as ruby
             long x = value;
             long y = ((RubyFixnum) other).value;
 
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
     }
 
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 mod += y;
             }
                 
             return getRuntime().newFixnum(mod);
 	            }
         return coerceBin("%", other);
     }
                 
     /** fix_divmod
      * 
      */
     public IRubyObject divmod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             final Ruby runtime = getRuntime();
 
             if (y == 0) {
                 throw runtime.newZeroDivisionError();
                 }
 
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
                 mod += y;
             }
 
             IRubyObject fixDiv = RubyFixnum.newFixnum(getRuntime(), div);
             IRubyObject fixMod = RubyFixnum.newFixnum(getRuntime(), mod);
 
             return RubyArray.newArray(runtime, fixDiv, fixMod);
 
     	}
         return coerceBin("divmod", other);
     }
     	
     /** fix_quo
      * 
      */
     public IRubyObject quo(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyFloat.newFloat(getRuntime(), (double) value
                     / (double) ((RubyFixnum) other).value);
     }
         return coerceBin("quo", other);
 	            }
 
     /** fix_pow 
      * 
      */
     public IRubyObject pow(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long b = ((RubyFixnum) other).value;
             if (b == 0) {
                 return RubyFixnum.one(getRuntime());
             }
             if (b == 1) {
                 return this;
             }
             if (b > 0) {
                 return RubyBignum.newBignum(getRuntime(), value).pow(other);
             }
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, b));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyFloat) other)
                     .getDoubleValue()));
         }
         return coerceBin("**", other);
     }
             
     /** fix_abs
      * 
      */
     public IRubyObject abs() {
         if (value < 0) {
             return RubyFixnum.newFixnum(getRuntime(), -value);
             }
         return this;
     }
             
     /** fix_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value == ((RubyFixnum) other).value);
         }
         return super.equal(other);
             }
 
     /** fix_cmp
      * 
      */
     public IRubyObject cmp(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == otherValue) {
                 return RubyFixnum.zero(getRuntime());
     }
             if (value > otherValue) {
                 return RubyFixnum.one(getRuntime());
             }
             return RubyFixnum.minus_one(getRuntime());
         }
         return coerceCmp("<=>", other);
     }
 
     /** fix_gt
      * 
      */
     public IRubyObject gt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value > ((RubyFixnum) other).value);
     }
         return coerceRelOp(">", other);
     }
 
     /** fix_ge
      * 
      */
     public IRubyObject ge(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value >= ((RubyFixnum) other).value);
             }
         return coerceRelOp(">=", other);
     }
 
     /** fix_lt
      * 
      */
     public IRubyObject lt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value < ((RubyFixnum) other).value);
         }
         return coerceRelOp("<", other);
     }
         
     /** fix_le
      * 
      */
     public IRubyObject le(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value <= ((RubyFixnum) other).value);
     }
         return coerceRelOp("<=", other);
     }
 
     /** fix_rev
      * 
      */
     public IRubyObject rev() {
         return newFixnum(~value);
     	}
     	
     /** fix_and
      * 
      */
     public IRubyObject and(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).and(this);
     }
         return RubyFixnum.newFixnum(getRuntime(), value & num2long(other));
     }
 
     /** fix_or 
      * 
      */
     public IRubyObject or(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return newFixnum(value | ((RubyFixnum) other).value);
         }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).or(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value | ((RubyNumeric) other).getLongValue());
         }
         
         return or(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
     }
 
     /** fix_xor 
      * 
      */
     public IRubyObject xor(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             return newFixnum(value ^ ((RubyFixnum) other).value);
             }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).xor(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
         }
 
         return xor(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
         }
 
     /** fix_aref 
      * 
      */
     public IRubyObject aref(IRubyObject other) {
         if(other instanceof RubyBignum) {
             RubyBignum big = (RubyBignum) other;
             RubyObject tryFix = RubyBignum.bignorm(getRuntime(), big.getValue());
             if (!(tryFix instanceof RubyFixnum)) {
                 if (big.getValue().signum() == 0 || value >= 0) {
                     return RubyFixnum.zero(getRuntime());
         }
                 return RubyFixnum.one(getRuntime());
         }
     }
 
         long otherValue = num2long(other);
             
         if (otherValue < 0) {
             return RubyFixnum.zero(getRuntime());
 		    } 
 		      
         if (BIT_SIZE - 1 < otherValue) {
             if (value < 0) {
                 return RubyFixnum.one(getRuntime());
         }
             return RubyFixnum.zero(getRuntime());
         }
         
         return (value & (1L << otherValue)) == 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
     }
 
     /** fix_lshift 
      * 
      */
     public IRubyObject lshift(IRubyObject other) {
         long width = num2long(other);
 
             if (width < 0) {
             return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
 		    }
     	
         if (width == 0) {
             return this;
     }
 
         if (width > BIT_SIZE - 1 || ((~0L << BIT_SIZE - width - 1) & value) != 0) {
             return RubyBignum.newBignum(getRuntime(), value).lshift(other);
         }
         
         return newFixnum(value << width);
     }
 
     /** fix_rshift 
      * 
      */
     public IRubyObject rshift(IRubyObject other) {
         long width = num2long(other);
 
         if (width < 0) {
             return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
     }
     
         if (width == 0) {
             return this;
     }
 
         if (width >= BIT_SIZE - 1) {
             if (value < 0) {
                 return RubyFixnum.minus_one(getRuntime());
     }
             return RubyFixnum.zero(getRuntime());
         }
 
         return newFixnum(value >> width);
         }
 
     /** fix_to_f 
      * 
      */
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), (double) value);
     }
 
     /** fix_size 
      * 
      */
     public IRubyObject size() {
         return newFixnum((long) ((BIT_SIZE + 7) / 8));
         }
 
     /** fix_zero_p 
      * 
      */
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0);
     }
 
     public RubyFixnum id() {
         return newFixnum(value * 2 + 1);
     }
 
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_fix_induced_from
      * 
      */
 
     public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
         return RubyNumeric.num2fix(other);
 }
 }
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 3493eb5d2d..3c554af326 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1153 +1,1156 @@
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
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
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
         hashc.defineMethod("default", callbackFactory.getOptMethod("default_value_get"));
         hashc.defineFastMethod("default=", callbackFactory.getFastMethod("default_value_set", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default_proc", callbackFactory.getMethod("default_proc"));
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
 
 
     public static final byte AREF_SWITCHVALUE = 1;
     public static final byte ASET_SWITCHVALUE = 2;
     public static final byte NIL_P_SWITCHVALUE = 3;
     public static final byte EQUALEQUAL_SWITCHVALUE = 4;
     public static final byte EMPTY_P_SWITCHVALUE = 5;
     public static final byte TO_S_SWITCHVALUE = 6;
     public static final byte TO_A_SWITCHVALUE = 7;
     public static final byte HASH_SWITCHVALUE = 8;
     public static final byte LENGTH_SWITCHVALUE = 9;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case AREF_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return aref(args[0]);
         case ASET_SWITCHVALUE:
             if (args.length != 2) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 2 + ")");
             return aset(args[0],args[1]);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty_p();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_A_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_a();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case LENGTH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return rb_size();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
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
     private boolean deleted = false;
 
     private boolean procDefault = false;
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
         public Object getPlainValue() {
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
             if(key == otherEntry.key && key != NEVER && key.equals(otherEntry.key)){
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
             Object k;
             if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
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
             Object k;
             if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) return entry.value;
 	}
         return null;
     }
 
     private final RubyHashEntry internalGetEntry(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             Object k;
             if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) return entry;
         }
         return null;
     }
 
     private final RubyHashEntry internalDelete(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry entry = table[i];
 
         if (entry == null) return null;
 
         IRubyObject k;
         if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
             table[i] = entry.next;
             size--;
             return entry;
     }
         for (; entry.next != null; entry = entry.next) {
             RubyHashEntry tmp = entry.next;
             if (tmp.hash == hash && ((k = tmp.key) == key || key.equals(k))) {
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
             if (entry.key != NEVER && entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
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
             deleted = true;
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
         if (deleted) {
             internalCleanupSafe();
             deleted = false;
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
             procDefault = true;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 0, 1);
             if (args.length == 1) ifNone = args[0];
         }
         return this;
     }
 
     /** rb_hash_default
      * 
      */
     public IRubyObject default_value_get(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (procDefault) {
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
         procDefault = false;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      * 
      */    
     public IRubyObject default_proc(Block unusedBlock) {
         return procDefault ? ifNone : getRuntime().getNil();
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
 
     /** rb_hash_inspect
      * 
      */
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if (!runtime.registerInspecting(this)) {
             return runtime.newString("{...}");
         }
 
         try {
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
                 firstEntry = false;
             }
             }
             sb.append("}");
             return runtime.newString(sb.toString());
         } finally {
             postIter();
             runtime.unregisterInspecting(this);
         }
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
 
     /** rb_hash_to_s
      * 
      */
     public IRubyObject to_s() {
         if (!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
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
         return ((value = internalGet(key)) == null) ? callMethod(getRuntime().getCurrentContext(), "default", key) : value;        
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
                procDefault != otherHash.procDefault) return runtime.getFalse();
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
                     deleted = true;
                     return result;
     }
             }
         } finally {postIter();}          
 
         if (procDefault) return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, getRuntime().getNil()});
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
                 deleted = true;
                 return entry.value;                
             }
         } else if ((entry = internalDelete(key)) != null) return entry.value;
 
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index a2f4536aa7..af9585f685 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1200 +1,1203 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     private static final String CVAR_TAINT_ERROR =
         "Insecure: can't modify class variable";
     private static final String CVAR_FREEZE_ERROR = "class/module";
 
     // superClass may be null.
     private RubyClass superClass;
 
     public int index;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     //public RubyModule parentModule;
 
     // CRef...to eventually replace parentModule
     public SinglyLinkedList cref;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
 
     protected RubyModule(Ruby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass);
 
         this.superClass = superClass;
 
         setBaseName(name);
 
         // If no parent is passed in, it is safe to assume Object.
         if (parentCRef == null) {
             if (runtime.getObject() != null) {
                 parentCRef = runtime.getObject().getCRef();
             }
         }
         this.cref = new SinglyLinkedList(this, parentCRef);
 
         runtime.moduleLastId++;
         this.id = runtime.moduleLastId;
     }
 
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyModule.class);   
         RubyClass moduleMetaClass = moduleClass.getMetaClass();
         moduleClass.index = ClassIndex.MODULE;
 
         moduleClass.defineFastMethod("===", callbackFactory.getFastMethod("op_eqq", IRubyObject.class));
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
         moduleClass.defineFastMethod("included", callbackFactory.getFastMethod("included", IRubyObject.class));
         moduleClass.defineFastMethod("included_modules", callbackFactory.getFastMethod("included_modules"));
         moduleClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         moduleClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         moduleClass.defineFastMethod("instance_method", callbackFactory.getFastMethod("instance_method", IRubyObject.class));
         moduleClass.defineFastMethod("instance_methods",callbackFactory.getFastOptMethod("instance_methods"));
         moduleClass.defineFastMethod("method_defined?", callbackFactory.getFastMethod("method_defined", IRubyObject.class));
         moduleClass.defineMethod("module_eval", callbackFactory.getOptMethod("module_eval"));
         moduleClass.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         moduleClass.defineFastMethod("private_class_method", callbackFactory.getFastOptMethod("private_class_method"));
         moduleClass.defineFastMethod("private_instance_methods", callbackFactory.getFastOptMethod("private_instance_methods"));
         moduleClass.defineFastMethod("protected_instance_methods", callbackFactory.getFastOptMethod("protected_instance_methods"));
         moduleClass.defineFastMethod("public_class_method", callbackFactory.getFastOptMethod("public_class_method"));
         moduleClass.defineFastMethod("public_instance_methods", callbackFactory.getFastOptMethod("public_instance_methods"));
         moduleClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         
         moduleClass.defineAlias("class_eval", "module_eval");
         
         moduleClass.defineFastPrivateMethod("alias_method", callbackFactory.getFastMethod("alias_method", IRubyObject.class, IRubyObject.class));
         moduleClass.defineFastPrivateMethod("append_features", callbackFactory.getFastMethod("append_features", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("attr", callbackFactory.getFastOptMethod("attr"));
         moduleClass.defineFastPrivateMethod("attr_reader", callbackFactory.getFastOptMethod("attr_reader"));
         moduleClass.defineFastPrivateMethod("attr_writer", callbackFactory.getFastOptMethod("attr_writer"));
         moduleClass.defineFastPrivateMethod("attr_accessor", callbackFactory.getFastOptMethod("attr_accessor"));
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
         moduleClass.defineFastPrivateMethod("undef_method", callbackFactory.getFastMethod("undef_method", IRubyObject.class));
         
         moduleMetaClass.defineMethod("nesting", callbackFactory.getSingletonMethod("nesting"));
 
         callbackFactory = runtime.callbackFactory(RubyKernel.class);
         moduleClass.defineFastMethod("autoload", callbackFactory.getFastSingletonMethod("autoload", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         moduleClass.defineFastMethod("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", RubyKernel.IRUBY_OBJECT));
 
         return moduleClass;
     }    
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyModule.newModule(runtime, klass, null);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     public static final byte EQQ_SWITCHVALUE = 1;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_eqq(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
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
         if (cref.getNext() == null) {
             return null;
         }
 
         return (RubyModule)cref.getNext().getValue();
     }
 
     public void setParent(RubyModule p) {
         cref.setNext(p.getCRef());
     }
 
     public Map getMethods() {
         return methods;
     }
     
     public void putMethod(Object name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         getRuntime().getSelectorTable().table[index][MethodIndex.getIndex((String)name)] = 0;
         getMethods().put(name, method);
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
             result.insert(0, "::").insert(0, p.getBaseName());
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
      * Search this and parent modules for the named variable.
      * 
      * @param name The variable to search for
      * @return The module in which that variable is found, or null if not found
      */
     private RubyModule getModuleWithInstanceVar(String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getInstanceVariable(name) != null) {
                 return p;
             }
         }
         return null;
     }
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module == null) {
             module = this;
         }
 
         return module.setInstanceVariable(name, value, CVAR_TAINT_ERROR, CVAR_FREEZE_ERROR);
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
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module != null) {
             IRubyObject variable = module.getInstanceVariable(name);
 
             return variable == null ? getRuntime().getNil() : variable;
         }
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
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
         return getModuleWithInstanceVar(name) != null;
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      * @see RubyObject#setInstanceVariable(String, IRubyObject, String, String)
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         if(getConstantAt(name) != null) {
             getRuntime().getWarnings().warn("already initialized constant " + name);
         }
 
         IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant",
                 "class/module");
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return result;
     }
 
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module = getConstantAt(name);
 
         return  (module instanceof RubyClass) ? (RubyClass) module : null;
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
 
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
 
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
             for (p = getSuperClass(); p != null; p = p.getSuperClass()) {
                 if (p instanceof IncludedModuleWrapper) {
                     if (p.getNonIncludedClass() == module.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             c = p;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
             if (!skip) {
                 // In the current logic, if we get here we know that module is not an 
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just 
                 // in case the logic should change later, let's do it anyway:
                 c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
             List methodNames = new ArrayList(((RubyModule) arg).getMethods().keySet());
             for (Iterator iter = methodNames.iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
         }
 
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
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
                 IRubyObject obj = getInstanceVariable("__attached__");
 
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
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         }else{
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
     }
     }
 
     private void addCachedMethod(String name, DynamicMethod method) {
         // Included modules modify the original 'included' modules class.  Since multiple
         // classes can include the same module, we cannot cache in the original included module.
         if (!isIncluded()) {
             putMethod(name, method);
             getRuntime().getCacheMap().add(method, this);
         }
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
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
 
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
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
 
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
                         getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
 
                     return constant;
                 }
                 p = p.getSuperClass();
             }
 
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
 
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
         return getInstanceVariable(name);
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
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         putMethod(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getConstantAt(name);
         ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         } 
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (superClazz != null && ((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newTypeError("superclass mismatch for class " + name);
         }
 
         return (RubyClass) type;
     }
 
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz, ObjectAllocator allocator) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         }
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newNameError(name + " is already defined.", name);
         }
 
         return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineModuleUnder(name, cref);
         }
 
         if (!(type instanceof RubyModule)) {
             throw getRuntime().newTypeError(name + " is not a module.");
         }
 
         return (RubyModule) type;
     }
 
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClass("Class")) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
         }
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject value = removeInstanceVariable(name.asSymbol());
 
         if (value != null) {
             return value;
         }
 
         if (isClassVarDefined(name.asSymbol())) {
             throw cannotRemoveError(name.asSymbol());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
         final Ruby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     Arity.checkArgumentCount(getRuntime(), args, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     Arity.checkArgumentCount(getRuntime(), args, 1, 1);
 
                     return self.setInstanceVariable(variableName, args[0]);
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
                 addMethod(name, new FullFunctionCallbackMethod(this, new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         ThreadContext tc = self.getRuntime().getCurrentContext();
                         return self.callSuper(tc, tc.getFrameArgs(), block);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 }, visibility));
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
             return !(checkVisibility && method.getVisibility().isPrivate());
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
 
         if (visibility.isModuleFunction()) visibility = Visibility.PRIVATE;
 
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
             body = proc;
 
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setKlazz(this);
             proc.getBlock().getFrame().setName(name);
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
         callMethod(context, "method_added", symbol);
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
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(Ruby runtime, String name) {
         return newModule(runtime, runtime.getClass("Module"), name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name) {
         return newModule(runtime, type, name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, String name, SinglyLinkedList parentCRef) {
         return newModule(runtime, runtime.getClass("Module"), name, parentCRef);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name, SinglyLinkedList parentCRef) {
         RubyModule module = new RubyModule(runtime, type, null, parentCRef, name);
         
         return module;
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (Iterator iter = p.instanceVariableNames(); iter.hasNext();) {
                 String id = (String) iter.next();
                 if (IdUtil.isClassVariable(id)) {
                     RubyString kval = getRuntime().newString(id);
                     if (!ary.includes(kval)) {
                         ary.append(kval);
                     }
                 }
             }
         }
         return ary;
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     protected IRubyObject doClone() {
         return RubyModule.newModule(getRuntime(), null, cref.getNext());
     }
 
     /** rb_mod_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         assert original instanceof RubyModule;
         
         RubyModule originalModule = (RubyModule)original;
         
         super.initialize_copy(originalModule);
         
         if (!getMetaClass().isSingleton()) {
             setMetaClass(originalModule.getSingletonClassClone());
         }
 
         setSuperClass(originalModule.getSuperClass());
         
         if (originalModule.instanceVariables != null){
             setInstanceVariables(new HashMap(originalModule.getInstanceVariables()));
         }
         
         // no __classpath__ and __classid__ stuff in JRuby here (yet?)        
 
         originalModule.cloneMethods(this);
         
         return this;        
     }
 
     /** rb_mod_included_modules
      *
diff --git a/src/org/jruby/RubyRegexp.java b/src/org/jruby/RubyRegexp.java
index 2deb260951..0b3d9a98b2 100644
--- a/src/org/jruby/RubyRegexp.java
+++ b/src/org/jruby/RubyRegexp.java
@@ -1,817 +1,820 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import jregex.Matcher;
 import jregex.Pattern;
 import jregex.REFlags;
 import org.jruby.parser.ReOptions;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  amoore
  */
 public class RubyRegexp extends RubyObject implements ReOptions {
     private static final RegexpTranslator REGEXP_TRANSLATOR = new RegexpTranslator();
 
     // \013 is a vertical tab. Java does not support the \v notation used by
     // Ruby.
     private static final Pattern SPECIAL_CHARS = new Pattern("([\\\t\\\n\\\f\\\r\\ \\#\\\013\\+\\-\\[\\]\\.\\?\\*\\(\\)\\{\\}\\|\\\\\\^\\$])");    
 
 	/** Class which represents the multibyte character set code.
 	 * (should be an enum in Java 5.0).
 	 * 
 	 * Warning: THIS IS NOT REALLY SUPPORTED BY JRUBY. 
 	 */
 
     private String source;
     private Pattern pattern;
     private KCode code;
     private int flags;
 
     KCode getCode() {
         return code;
     }
 
 	// lastTarget and matcher currently only used by searchAgain
 	private String lastTarget = null;
 	private Matcher matcher = null;
 
     public RubyRegexp(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     private RubyRegexp(Ruby runtime) {
         super(runtime, runtime.getClass("Regexp"));
     }
     
     private static ObjectAllocator REGEXP_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyRegexp instance = new RubyRegexp(runtime, klass);
             
             return instance;
         }
     };
 
     public static RubyClass createRegexpClass(Ruby runtime) {
         RubyClass regexpClass = runtime.defineClass("Regexp", runtime.getObject(), REGEXP_ALLOCATOR);
         regexpClass.index = ClassIndex.REGEXP;
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyRegexp.class);
         
         regexpClass.defineConstant("IGNORECASE", runtime.newFixnum(RE_OPTION_IGNORECASE));
         regexpClass.defineConstant("EXTENDED", runtime.newFixnum(RE_OPTION_EXTENDED));
         regexpClass.defineConstant("MULTILINE", runtime.newFixnum(RE_OPTION_MULTILINE));
 
         regexpClass.defineFastMethod("initialize", callbackFactory.getFastOptMethod("initialize"));
         regexpClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy",RubyKernel.IRUBY_OBJECT));        
         regexpClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("eql?", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("===", callbackFactory.getFastMethod("eqq", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("=~", callbackFactory.getFastMethod("match", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("~", callbackFactory.getFastMethod("match2"));
         regexpClass.defineFastMethod("match", callbackFactory.getFastMethod("match_m", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         regexpClass.defineFastMethod("source", callbackFactory.getFastMethod("source"));
         regexpClass.defineFastMethod("casefold?", callbackFactory.getFastMethod("casefold"));
         regexpClass.defineFastMethod("kcode", callbackFactory.getFastMethod("kcode"));
         regexpClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         regexpClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         regexpClass.getMetaClass().defineFastMethod("new", callbackFactory.getFastOptSingletonMethod("newInstance"));
         regexpClass.getMetaClass().defineFastMethod("compile", callbackFactory.getFastOptSingletonMethod("newInstance"));
         regexpClass.getMetaClass().defineFastMethod("quote", callbackFactory.getFastOptSingletonMethod("quote"));
         regexpClass.getMetaClass().defineFastMethod("escape", callbackFactory.getFastSingletonMethod("quote", RubyString.class));
         regexpClass.getMetaClass().defineFastMethod("last_match", callbackFactory.getFastOptSingletonMethod("last_match_s"));
         regexpClass.getMetaClass().defineFastMethod("union", callbackFactory.getFastOptSingletonMethod("union"));
 
         return regexpClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.REGEXP;
     }
 
 
     public static final byte NIL_P_SWITCHVALUE = 1;
     public static final byte EQUALEQUAL_SWITCHVALUE = 2;
     public static final byte TO_S_SWITCHVALUE = 3;
     public static final byte HASH_SWITCHVALUE = 4;
     public static final byte MATCH_SWITCHVALUE = 5;
     public static final byte EQQ_SWITCHVALUE = 6;
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case MATCH_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return match(args[0]);
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return eqq(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
     public void initialize(String regex, int options) {
         try {
             if(getCode() == KCode.UTF8) {
                 try {
                     regex = new String(ByteList.plain(regex),"UTF8");
                 } catch(Exception e) {
                 }
             }
             source = regex;
             pattern = REGEXP_TRANSLATOR.translate(regex, options, code.flags());
             flags = REGEXP_TRANSLATOR.flagsFor(options, code.flags());
         } catch(jregex.PatternSyntaxException e) {
             //            System.err.println(regex);
             //            e.printStackTrace();
             throw getRuntime().newRegexpError(e.getMessage());
         }
     }
 
     public static String escapeSpecialChars(String original) {
     	return SPECIAL_CHARS.replacer("\\\\$1").replace(original);
     }
 
     private void recompileIfNeeded() {
         checkInitialized();
     }
 
     private void checkInitialized() {
         if (pattern == null) {
             throw getRuntime().newTypeError("uninitialized Regexp");
         }
     }
     
     public static RubyRegexp regexpValue(IRubyObject obj) {
         if (obj instanceof RubyRegexp) {
             return (RubyRegexp) obj;
         } else if (obj instanceof RubyString) {
             return newRegexp(obj.getRuntime().newString(escapeSpecialChars(((RubyString) obj).toString())), 0, null);
         } else {
             throw obj.getRuntime().newArgumentError("can't convert arg to Regexp");
         }
     }
 
     // Methods of the Regexp class (rb_reg_*):
 
     public static RubyRegexp newRegexp(RubyString str, int options, String lang) {
         return newRegexp(str.getRuntime(), str.toString(), options, lang);
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, String source, Pattern pattern, int flags, String lang) {
         RubyRegexp re = new RubyRegexp(runtime);
         re.code = KCode.create(runtime, lang);
         re.source = source;
         re.pattern = pattern;
         re.flags = flags;
         return re;
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, String str, int options, String kcode) {
         RubyRegexp re = new RubyRegexp(runtime);
         re.code = KCode.create(runtime, kcode);
         re.initialize(str, options);
         return re;
     }
     
     public static RubyRegexp newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         
         RubyRegexp re = (RubyRegexp) klass.allocate();
         
         re.callInit(args, Block.NULL_BLOCK);
         
         return re;
     }
 
     public IRubyObject initialize(IRubyObject[] args) {
         String pat =
             (args[0] instanceof RubyRegexp)
                 ? ((RubyRegexp) args[0]).source().toString()
                 : RubyString.stringValue(args[0]).toString();
         int opts = 0;
         if (args.length > 1) {
             if (args[1] instanceof RubyFixnum) {
                 opts = (int) ((RubyFixnum) args[1]).getLongValue();
             } else if (args[1].isTrue()) {
                 opts |= RE_OPTION_IGNORECASE;
             }
         }
         if (args.length > 2) {
         	code = KCode.create(getRuntime(), RubyString.stringValue (args[2]).toString());
         } else {
         	code = KCode.create(getRuntime(), null);
         }
         initialize(pat, opts);
         return getRuntime().getNil();
     }
 
     /** rb_reg_s_quote
      * 
      */
     public static RubyString quote(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0 || args.length > 2) {
             throw recv.getRuntime().newArgumentError(0, args.length);
         }
 
         KCode kcode = recv.getRuntime().getKCode();
         
         if (args.length > 1) {
             kcode = KCode.create(recv.getRuntime(), args[1].toString());
         }
         
         RubyString str = args[0].convertToString();
 
         if (kcode == KCode.NONE) {
             return quote(recv, str);
         }
         
         try {
             // decode with the specified encoding, escape as appropriate, and reencode
             // FIXME: This could probably be more efficent.
             CharBuffer decoded = kcode.decoder().decode(ByteBuffer.wrap(str.getBytes()));
             String escaped = escapeSpecialChars(decoded.toString());
             ByteBuffer encoded = kcode.encoder().encode(CharBuffer.wrap(escaped));
             
             return (RubyString)RubyString.newString(recv.getRuntime(), encoded.array()).infectBy(str);
         } catch (CharacterCodingException ex) {
             throw new RuntimeException(ex);
         }
     }
 
     /**
      * Utility version of quote that doesn't use encoding
      */
     public static RubyString quote(IRubyObject recv, RubyString str) {        
         return (RubyString) recv.getRuntime().newString(escapeSpecialChars(str.toString())).infectBy(str);
     }
 
     /** 
      * 
      */
     public static IRubyObject last_match_s(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             return recv.getRuntime().getCurrentContext().getBackref();
         }
         
         // FIXME: 
         return ((RubyMatchData)recv.getRuntime().getCurrentContext().getBackref()).aref(args);
     }
 
     /** rb_reg_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if (other == this) {
             return getRuntime().getTrue();
         }
         if (!(other instanceof RubyRegexp)) {
             return getRuntime().getFalse();
         }
         RubyRegexp re = (RubyRegexp) other;
         checkInitialized();
         if (!(re.pattern.toString().equals(pattern.toString()) && re.flags == this.flags)) {
             return getRuntime().getFalse();
         }
         
         if (code != re.code) {
         	return getRuntime().getFalse();
         }
         
         return getRuntime().getTrue();
     }
 
     /** rb_reg_match2
      * 
      */
     public IRubyObject match2() {
         IRubyObject target = getRuntime().getCurrentContext().getLastline();
         
         return target instanceof RubyString ? match(target) : getRuntime().getNil();
     }
     
     /** rb_reg_eqq
      * 
      */
     public IRubyObject eqq(IRubyObject target) {
         if(!(target instanceof RubyString)) {
             target = target.checkStringType();
             if(target.isNil()) {
                 getRuntime().getCurrentContext().setBackref(getRuntime().getNil());
                 return getRuntime().getFalse();
             }
         }
         RubyString ss = RubyString.stringValue(target);
     	String string = ss.toString();
         if (string.length() == 0 && "^$".equals(pattern.toString())) {
     		string = "\n";
         }
     	
         int result = search(string, ss, 0);
         
         return result < 0 ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_reg_match
      * 
      */
     public IRubyObject match(IRubyObject target) {
         if (target.isNil()) {
             return getRuntime().getFalse();
         }
         // FIXME:  I think all String expecting functions has this magic via RSTRING
     	if (target instanceof RubySymbol || target instanceof RubyHash || target instanceof RubyArray) {
     		return getRuntime().getFalse();
     	}
     	RubyString ss = RubyString.stringValue(target);
     	String string = ss.toString();
         if (string.length() == 0 && "^$".equals(pattern.toString())) {
     		string = "\n";
         }
     	
         int result = search(string, ss, 0);
         
         return result < 0 ? getRuntime().getNil() :
         	getRuntime().newFixnum(result);
     }
 
     /** rb_reg_match_m
      * 
      */
     public IRubyObject match_m(IRubyObject target) {
         if (target.isNil()) {
             return target;
         }
         IRubyObject result = match(target);
         return result.isNil() ? result : getRuntime().getCurrentContext().getBackref().rbClone();
     }
 
     /** rb_reg_source
      * 
      */
     public RubyString source() {
         checkInitialized();
         return getRuntime().newString(source);
     }
 
     public IRubyObject kcode() {
         if(code == KCode.NIL) {
             return code.kcode(getRuntime());
         } else {
             return getRuntime().newString(code.kcode(getRuntime()).toString().toLowerCase());
         }
     }
 
     /** rb_reg_casefold_p
      * 
      */
     public RubyBoolean casefold() {
         checkInitialized();
         return getRuntime().newBoolean((flags & REFlags.IGNORE_CASE) != 0);
     }
 
     /** rb_reg_nth_match
      *
      */
     public static IRubyObject nth_match(int n, IRubyObject match) {
         IRubyObject nil = match.getRuntime().getNil();
         if (match.isNil()) {
             return nil;
         }
         
         RubyMatchData rmd = (RubyMatchData) match;
         
         if (n > rmd.getSize()) {
             return nil;
         }
         
         if (n < 0) {
             n += rmd.getSize();
             if (n <= 0) {
                 return nil;
             }
         }
         return rmd.group(n);
     }
 
     /** rb_reg_last_match
      *
      */
     public static IRubyObject last_match(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).group(0);
     }
 
     /** rb_reg_match_pre
      *
      */
     public static IRubyObject match_pre(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).pre_match();
     }
 
     /** rb_reg_match_post
      *
      */
     public static IRubyObject match_post(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).post_match();
     }
 
     /** rb_reg_match_last
      *
      */
     public static IRubyObject match_last(IRubyObject match) {
         if (match.isNil()) {
             return match;
         }
         RubyMatchData md = (RubyMatchData) match;
         for (long i = md.getSize() - 1; i > 0; i--) {
             if (!md.group(i).isNil()) {
                 return md.group(i);
             }
         }
         return md.getRuntime().getNil();
     }
 
     /** rb_reg_search
      *
      */
     public int search(String target, RubyString rtarget, int pos) {
         if (pos > target.length()) {
             return -1;
         }
         recompileIfNeeded();
 
         // If nothing match then nil will be returned
         IRubyObject result = match(target, rtarget, pos);
         getRuntime().getCurrentContext().setBackref(result);
 
         // If nothing match then -1 will be returned
         return result instanceof RubyMatchData ? ((RubyMatchData) result).matchStartPosition() : -1;
     }
     
     public IRubyObject search2(String str, RubyString rtarget) {
         IRubyObject result = match(str, rtarget, 0);
         
         getRuntime().getCurrentContext().setBackref(result);
         
     	return result;
     }
 	
     public int searchAgain(String target, RubyString rtarget, boolean utf) {
         if (matcher == null || !target.equals(lastTarget)) {
             matcher = pattern.matcher(target);
             lastTarget = target;
         }
         
         if (!matcher.find()) {
             return -1;
         }
         
         RubyMatchData match = (utf) ? 
             ((RubyMatchData)new RubyMatchData.JavaString(getRuntime(), target, matcher))
             :
             ((RubyMatchData)new RubyMatchData.RString(getRuntime(), rtarget, matcher));
 
         
         getRuntime().getCurrentContext().setBackref(match);
         
         return match.matchStartPosition();
     }
     
     public IRubyObject match(String target, RubyString rtarget, int startPos) {
         boolean utf8 = getCode() == KCode.UTF8;
         String t = target;
         if(utf8) {
             try {
                 t = new String(ByteList.plain(target),"UTF8");
             } catch(Exception e) {
             }
         }
 
     	Matcher aMatcher = pattern.matcher(t);
     	
         aMatcher.setPosition(startPos);
 
         if (aMatcher.find()) {
             return (utf8) ? 
                 ((RubyMatchData)new RubyMatchData.JavaString(getRuntime(), target, aMatcher))
                 :
                 ((RubyMatchData)new RubyMatchData.RString(getRuntime(), rtarget, aMatcher));
 
         }
         return getRuntime().getNil();
     }
 
     public void regsub(RubyString str, RubyMatchData match, ByteList sb) {
         ByteList repl = str.getByteList();
         int pos = 0;
         int end = repl.length();
         char c;
         IRubyObject ins;
         while (pos < end) {
             c = (char)(repl.get(pos++) & 0xFF);
             if (c == '\\' && pos < end) {
                 c = (char)(repl.get(pos++) & 0xFF);
                 switch (c) {
                     case '0' :
                     case '1' :
                     case '2' :
                     case '3' :
                     case '4' :
                     case '5' :
                     case '6' :
                     case '7' :
                     case '8' :
                     case '9' :
                         ins = match.group(c - '0');
                         break;
                     case '&' :
                         ins = match.group(0);
                         break;
                     case '`' :
                         ins = match.pre_match();
                         break;
                     case '\'' :
                         ins = match.post_match();
                         break;
                     case '+' :
                         ins = match_last(match);
                         break;
                     case '\\' :
                         sb.append(c);
                         continue;
                     default :
                         sb.append('\\');
                         sb.append(c);
                         continue;
                 }
                 if (!ins.isNil()) {
                     sb.append(((RubyString) ins).getByteList());
                 }
             } else {
                 sb.append(c);
             }
         }
     }
 
     /** rb_reg_regsub
      *
      */
     public IRubyObject regsub(IRubyObject str, RubyMatchData match) {
         RubyString str2 = str.asString();
         ByteList sb = new ByteList(str2.getByteList().length()+30);
         regsub(str2,match,sb);
         return RubyString.newString(getRuntime(),sb);
     }
 
     /** rb_reg_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         if (this == original) return this;
         
         if (!(getMetaClass() == original.getMetaClass())){ // MRI also does a pointer comparison here
             throw getRuntime().newTypeError("wrong argument class");
         }
 
         RubyRegexp origRegexp = (RubyRegexp)original;
         source = origRegexp.source;
         pattern = origRegexp.pattern;
         code = origRegexp.code;
 
         return this;
     }
 
     /** rb_reg_inspect
      *
      */
     public IRubyObject inspect() {
         final String regex = source;
 		final int length = regex.length();
         StringBuffer sb = new StringBuffer(length + 2);
 
         sb.append('/');
         for (int i = 0; i < length; i++) {
             char c = regex.charAt(i);
 
             if (RubyString.isAlnum(c)) {
                 sb.append(c);
             } else if (c == '/') {
                 if (i == 0 || regex.charAt(i - 1) != '\\') {
                     sb.append("\\");
                 }
             	sb.append(c);
             } else if (RubyString.isPrint(c)) {
                 sb.append(c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(Sprintf.sprintf(getRuntime(),"\\%.3o",c));
             }
         }
         sb.append('/');
 
         if(code == KCode.NONE) {
             sb.append('n');
         } else if(code == KCode.UTF8) {
             sb.append('u');
         } else if(code == KCode.SJIS) {
             sb.append('s');
         }
         if ((flags & REFlags.IGNORE_CASE) > 0) {
             sb.append('i');
         }
   
         if ((flags & REFlags.DOTALL) > 0) {
             sb.append('m');
         }
         
         if ((flags & REFlags.IGNORE_SPACES) > 0) {
             sb.append('x');
         }
 
         return getRuntime().newString(sb.toString());
     }
     
     /**
      * rb_reg_s_union
      */
     public static IRubyObject union(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
         if (args.length == 0) {
             return newInstance(recv, new IRubyObject[] {runtime.newString("(?!)")});
         }
         
         if (args.length == 1) {
             IRubyObject arg = args[0].convertToType(runtime.getClass("Regexp"), 0, "to_regexp", false);
             if (!arg.isNil()) {
                 return arg;
             }
             return newInstance(recv, new IRubyObject[] {quote(recv, args[0].convertToString())});
         }
         
         StringBuffer buffer = new StringBuffer();
         for (int i = 0; i < args.length; i++) {
         	if (i > 0) {
         		buffer.append("|");
             }
         	IRubyObject arg = args[i].convertToType(runtime.getClass("Regexp"), 0, "to_regexp", false);
             if (arg.isNil()) {
                 arg = quote(recv, args[i].convertToString());
             }
             buffer.append(arg.toString());
         }
         
         return newInstance(recv, new IRubyObject[] {runtime.newString(buffer.toString())});
     }
 
     
     public IRubyObject to_s() {
         return getRuntime().newString(toString());
     }
     
     public String toString() {
     	StringBuffer buffer = new StringBuffer(100);
     	StringBuffer off = new StringBuffer(3);
     	
     	buffer.append("(?");
     	
     	flagToString(buffer, off, REFlags.DOTALL, 'm');
     	flagToString(buffer, off, REFlags.IGNORE_CASE, 'i');
     	flagToString(buffer, off, REFlags.IGNORE_SPACES, 'x');
 
 		if (off.length() > 0) {
 			buffer.append('-').append(off);
 		}
 
     	buffer.append(':');
         buffer.append(pattern.toString().replaceAll("^/|([^\\\\])/", "$1\\\\/"));
 		buffer.append(')');
 
     	return buffer.toString();
     }
 
     /** Helper method for the {@link #toString() toString} method which creates
      * an <i>on-off</i> pattern of {@link Pattern Pattern} flags. 
      * 
 	 * @param buffer the default buffer for the output
 	 * @param off temporary buffer for the off flags
 	 * @param flag a Pattern flag
 	 * @param c the char which represents the flag
 	 */
 	private void flagToString(StringBuffer buffer, StringBuffer off, int flag, char c) {
 		if ((flags & flag) != 0) {
     		buffer.append(c);
     	} else {
     		off.append(c);
     	}
 	}
 
     public static RubyRegexp unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyRegexp result = newRegexp(input.getRuntime(), 
                                       RubyString.byteListToString(input.unmarshalString()), input.unmarshalInt(), null);
         input.registerLinkTarget(result);
         return result;
     }
 
     public static void marshalTo(RubyRegexp regexp, MarshalStream output) throws java.io.IOException {
         output.writeString(regexp.pattern.toString());
 
         int _flags = 0;
         if ((regexp.flags & REFlags.DOTALL) > 0) {
             _flags |= RE_OPTION_MULTILINE;
         }
         if ((regexp.flags & REFlags.IGNORE_CASE) > 0) {
             _flags |= RE_OPTION_IGNORECASE;
         }
         if ((regexp.flags & REFlags.IGNORE_SPACES) > 0) {
             _flags |= RE_OPTION_EXTENDED;
         }
         output.writeInt(_flags);
     }
 	
 	public Pattern getPattern() {
 		return this.pattern;
 	}
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(this.pattern.toString().hashCode());
     }
 }
diff --git a/src/org/jruby/RubySymbol.java b/src/org/jruby/RubySymbol.java
index 5021898dc6..a0cdad14db 100644
--- a/src/org/jruby/RubySymbol.java
+++ b/src/org/jruby/RubySymbol.java
@@ -1,377 +1,380 @@
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
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
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
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubySymbol extends RubyObject {
     private final String symbol;
     private final int id;
     
     private RubySymbol(Ruby runtime, String symbol) {
         super(runtime, runtime.getClass("Symbol"));
         this.symbol = symbol;
 
         runtime.symbolLastId++;
         this.id = runtime.symbolLastId;
     }
     
     public static RubyClass createSymbolClass(Ruby runtime) {
         RubyClass symbolClass = runtime.defineClass("Symbol", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubySymbol.class);   
         RubyClass symbolMetaClass = symbolClass.getMetaClass();
         symbolClass.index = ClassIndex.SYMBOL;
 
         
         symbolClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", IRubyObject.class));
         symbolClass.defineFastMethod("freeze", callbackFactory.getFastMethod("freeze"));
         symbolClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         symbolClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         symbolClass.defineFastMethod("taint", callbackFactory.getFastMethod("taint"));
         symbolClass.defineFastMethod("to_i", callbackFactory.getFastMethod("to_i"));
         symbolClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         symbolClass.defineFastMethod("to_sym", callbackFactory.getFastMethod("to_sym"));
         symbolClass.defineAlias("id2name", "to_s");
         symbolClass.defineAlias("to_int", "to_i");
 
         symbolMetaClass.defineFastMethod("all_symbols", callbackFactory.getFastSingletonMethod("all_symbols"));
 
         symbolMetaClass.undefineMethod("new");
         
         return symbolClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.SYMBOL;
     }
 
     public static final byte NIL_P_SWITCHVALUE = 1;
     public static final byte EQUALEQUAL_SWITCHVALUE = 2;
     public static final byte TO_S_SWITCHVALUE = 3;
     public static final byte TO_I_SWITCHVALUE = 4;
     public static final byte TO_SYM_SWITCHVALUE = 5;
     public static final byte HASH_SWITCHVALUE = 6;
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
+        // If tracing is on, don't do STI dispatch
+        if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
+        
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case TO_SYM_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_sym();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
 
     /** rb_to_id
      * 
      * @return a String representation of the symbol 
      */
     public String asSymbol() {
         return symbol;
     }
 
     public boolean isImmediate() {
     	return true;
     }
     
     public static String getSymbol(Ruby runtime, long id) {
         RubySymbol result = runtime.getSymbolTable().lookup(id);
         if (result != null) {
             return result.symbol;
         }
         return null;
     }
 
     /* Symbol class methods.
      * 
      */
 
     public static RubySymbol newSymbol(Ruby runtime, String name) {
         RubySymbol result;
         synchronized (RubySymbol.class) {
             // Locked to prevent the creation of multiple instances of
             // the same symbol. Most code depends on them being unique.
 
             result = runtime.getSymbolTable().lookup(name);
             if (result == null) {
                 result = new RubySymbol(runtime, name);
                 runtime.getSymbolTable().store(result);
             }
         }
         return result;
     }
 
     public IRubyObject equal(IRubyObject other) {
         // Symbol table ensures only one instance for every name,
         // so object identity is enough to compare symbols.
         return RubyBoolean.newBoolean(getRuntime(), this == other);
     }
 
     public RubyFixnum to_i() {
         return getRuntime().newFixnum(id);
     }
 
     public IRubyObject inspect() {
         return getRuntime().newString(":" + 
             (isSymbolName(symbol) ? symbol : getRuntime().newString(symbol).dump().toString())); 
     }
 
     public IRubyObject to_s() {
         return getRuntime().newString(symbol);
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
     
     public int hashCode() {
         return id;
     }
     
     public boolean equals(Object other) {
         return other == this;
     }
     
     public IRubyObject to_sym() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public IRubyObject taint() {
         return this;
     }
     
     private static boolean isIdentStart(char c) {
         return ((c >= 'a' && c <= 'z')|| (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     private static boolean isIdentChar(char c) {
         return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     
     private static boolean isIdentifier(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         } 
         
         if (!isIdentStart(s.charAt(0))) {
             return false;
         }
         for (int i = 1; i < s.length(); i++) {
             if (!isIdentChar(s.charAt(i))) {
                 return false;
             }
         }
         
         return true;
     }
     
     /**
      * is_special_global_name from parse.c.  
      * @param s
      * @return
      */
     private static boolean isSpecialGlobalName(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         }
 
         int length = s.length();
            
         switch (s.charAt(0)) {        
         case '~': case '*': case '$': case '?': case '!': case '@': case '/': case '\\':        
         case ';': case ',': case '.': case '=': case ':': case '<': case '>': case '\"':        
         case '&': case '`': case '\'': case '+': case '0':
             return length == 1;            
         case '-':
             return (length == 1 || (length == 2 && isIdentChar(s.charAt(1))));
             
         default:
             // we already confirmed above that length > 0
             for (int i = 0; i < length; i++) {
                 if (!Character.isDigit(s.charAt(i))) {
                     return false;
                 }
             }
         }
         return true;
     }
     
     private static boolean isSymbolName(String s) {
         if (s == null || s.length() < 1) {
             return false;
         }
 
         int length = s.length();
 
         char c = s.charAt(0);
         switch (c) {
         case '$':
             return length > 1 && isSpecialGlobalName(s.substring(1));
         case '@':
             int offset = 1;
             if (length >= 2 && s.charAt(1) == '@') {
                 offset++;
             }
 
             return isIdentifier(s.substring(offset));
         case '<':
             return (length == 1 || (length == 2 && (s.equals("<<") || s.equals("<="))) || 
                     (length == 3 && s.equals("<=>")));
         case '>':
             return (length == 1) || (length == 2 && (s.equals(">>") || s.equals(">=")));
         case '=':
             return ((length == 2 && (s.equals("==") || s.equals("=~"))) || 
                     (length == 3 && s.equals("===")));
         case '*':
             return (length == 1 || (length == 2 && s.equals("**")));
         case '+':
             return (length == 1 || (length == 2 && s.equals("+@")));
         case '-':
             return (length == 1 || (length == 2 && s.equals("-@")));
         case '|': case '^': case '&': case '/': case '%': case '~': case '`':
             return length == 1;
         case '[':
             return s.equals("[]") || s.equals("[]=");
         }
         
         if (!isIdentStart(c)) {
             return false;
         }
 
         boolean localID = (c >= 'a' && c <= 'z');
         int last = 1;
         
         for (; last < length; last++) {
             char d = s.charAt(last);
             
             if (!isIdentChar(d)) {
                 break;
             }
         }
                     
         if (last == length) {
             return true;
         } else if (localID && last == length - 1) {
             char d = s.charAt(last);
             
             return d == '!' || d == '?' || d == '=';
         }
         
         return false;
     }
     
     public static IRubyObject all_symbols(IRubyObject recv) {
         return recv.getRuntime().newArrayNoCopy(recv.getRuntime().getSymbolTable().all_symbols());
     }
 
     public static RubySymbol unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubySymbol result = RubySymbol.newSymbol(input.getRuntime(), RubyString.byteListToString(input.unmarshalString()));
         input.registerLinkTarget(result);
         return result;
     }
 
     public static class SymbolTable {
        
         private Map table = new HashMap();
         
         public IRubyObject[] all_symbols() {
             int length = table.size();
             IRubyObject[] array = new IRubyObject[length];
             System.arraycopy(table.values().toArray(), 0, array, 0, length);
             return array;
         }
         
         public RubySymbol lookup(long symbolId) {
             Iterator iter = table.values().iterator();
             while (iter.hasNext()) {
                 RubySymbol symbol = (RubySymbol) iter.next();
                 if (symbol != null) {
                     if (symbol.id == symbolId) {
                         return symbol;
                     }
                 }
             }
             return null;
         }
         
         public RubySymbol lookup(String name) {
             return (RubySymbol) table.get(name);
         }
         
         public void store(RubySymbol symbol) {
             table.put(symbol.asSymbol(), symbol);
         }
         
     }
     
 }
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index b66690ce86..be0a7901c5 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,2177 +1,2178 @@
 /*******************************************************************************
  * BEGIN LICENSE BLOCK *** Version: CPL 1.0/GPL 2.0/LGPL 2.1
  * 
  * The contents of this file are subject to the Common Public License Version
  * 1.0 (the "License"); you may not use this file except in compliance with the
  * License. You may obtain a copy of the License at
  * http://www.eclipse.org/legal/cpl-v10.html
  * 
  * Software distributed under the License is distributed on an "AS IS" basis,
  * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  * the specific language governing rights and limitations under the License.
  * 
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
  * Copytight (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"), or
  * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
  * which case the provisions of the GPL or the LGPL are applicable instead of
  * those above. If you wish to allow use of your version of this file only under
  * the terms of either the GPL or the LGPL, and not to allow others to use your
  * version of this file under the terms of the CPL, indicate your decision by
  * deleting the provisions above and replace them with the notice and other
  * provisions required by the GPL or the LGPL. If you do not delete the
  * provisions above, a recipient may use your version of this file under the
  * terms of any one of the CPL, the GPL or the LGPL. END LICENSE BLOCK ****
  ******************************************************************************/
 
 package org.jruby.evaluator;
 
 import org.jruby.Ruby;
 import org.jruby.MetaClass;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
+import org.jruby.RubyBinding;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
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
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OptNNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException.JumpType;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ForBlock;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class EvaluationState {
     public static IRubyObject eval(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         try {
             return evalInternal(runtime, context, node, self, block);
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");
         }
     }
 
     private static IRubyObject evalInternal(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         do {
             if (node == null) return nilNode(runtime, context);
 
             switch (node.nodeId) {
             case NodeTypes.ALIASNODE:
                 return aliasNode(runtime, context, node);
             case NodeTypes.ANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
    
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue;
             }
             case NodeTypes.ARGSCATNODE:
                 return argsCatNode(runtime, context, node, self, aBlock);
             case NodeTypes.ARGSPUSHNODE:
                 return argsPushNode(runtime, context, node, self, aBlock);
             case NodeTypes.ARRAYNODE:
                 return arrayNode(runtime, context, node, self, aBlock);
             case NodeTypes.ATTRASSIGNNODE:
                 return attrAssignNode(runtime, context, node, self, aBlock); 
             case NodeTypes.BACKREFNODE:
                 return backRefNode(context, node);
             case NodeTypes.BEGINNODE: 
                 node = ((BeginNode)node).getBodyNode();
                 continue;
             case NodeTypes.BIGNUMNODE:
                 return bignumNode(runtime, node);
             case NodeTypes.BLOCKNODE:
                 return blockNode(runtime, context, node, self, aBlock);
             case NodeTypes.BLOCKPASSNODE:
             assert false: "Call nodes and friends deal with this";
             case NodeTypes.BREAKNODE:
                 return breakNode(runtime, context, node, self, aBlock);
             case NodeTypes.CALLNODE:
                 return callNode(runtime, context, node, self, aBlock);
             case NodeTypes.CASENODE:
                 return caseNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSNODE:
                 return classNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARASGNNODE:
                 return classVarAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARDECLNODE:
                 return classVarDeclNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARNODE:
                 return classVarNode(runtime, context, node, self);
             case NodeTypes.COLON2NODE:
                 return colon2Node(runtime, context, node, self, aBlock);
             case NodeTypes.COLON3NODE:
                 return colon3Node(runtime, node);
             case NodeTypes.CONSTDECLNODE:
                 return constDeclNode(runtime, context, node, self, aBlock);
             case NodeTypes.CONSTNODE:
                 return constNode(context, node);
             case NodeTypes.DASGNNODE:
                 return dAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.DEFINEDNODE:
                 return definedNode(runtime, context, node, self, aBlock);
             case NodeTypes.DEFNNODE:
                 return defnNode(runtime, context, node);
             case NodeTypes.DEFSNODE:
                 return defsNode(runtime, context, node, self, aBlock);
             case NodeTypes.DOTNODE:
                 return dotNode(runtime, context, node, self, aBlock);
             case NodeTypes.DREGEXPNODE:
                 return dregexpNode(runtime, context, node, self, aBlock);
             case NodeTypes.DSTRNODE:
                 return dStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.DSYMBOLNODE:
                 return dSymbolNode(runtime, context, node, self, aBlock);
             case NodeTypes.DVARNODE:
                 return dVarNode(runtime, context, node);
             case NodeTypes.DXSTRNODE:
                 return dXStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.ENSURENODE:
                 return ensureNode(runtime, context, node, self, aBlock);
             case NodeTypes.EVSTRNODE:
                 return evStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.FALSENODE:
                 return falseNode(runtime, context);
             case NodeTypes.FCALLNODE:
                 return fCallNode(runtime, context, node, self, aBlock);
             case NodeTypes.FIXNUMNODE:
                 return fixnumNode(runtime, node);
             case NodeTypes.FLIPNODE:
                 return flipNode(runtime, context, node, self, aBlock);
             case NodeTypes.FLOATNODE:
                 return floatNode(runtime, node);
             case NodeTypes.FORNODE:
                 return forNode(runtime, context, node, self, aBlock);
             case NodeTypes.GLOBALASGNNODE:
                 return globalAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.GLOBALVARNODE:
                 return globalVarNode(runtime, context, node);
             case NodeTypes.HASHNODE:
                 return hashNode(runtime, context, node, self, aBlock);
             case NodeTypes.IFNODE: {
                 IfNode iVisited = (IfNode) node;
                 IRubyObject result = evalInternal(runtime,context, iVisited.getCondition(), self, aBlock);
 
                 if (result.isTrue()) {
                     node = iVisited.getThenBody();
                 } else {
                     node = iVisited.getElseBody();
                 }
                 continue;
             }
             case NodeTypes.INSTASGNNODE:
                 return instAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.INSTVARNODE:
                 return instVarNode(runtime, node, self);
             case NodeTypes.ITERNODE: 
             assert false: "Call nodes deal with these directly";
             case NodeTypes.LOCALASGNNODE:
                 return localAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.LOCALVARNODE:
                 return localVarNode(runtime, context, node);
             case NodeTypes.MATCH2NODE:
                 return match2Node(runtime, context, node, self, aBlock);
             case NodeTypes.MATCH3NODE:
                 return match3Node(runtime, context, node, self, aBlock);
             case NodeTypes.MATCHNODE:
                 return matchNode(runtime, context, node, self, aBlock);
             case NodeTypes.MODULENODE:
                 return moduleNode(runtime, context, node, self, aBlock);
             case NodeTypes.MULTIPLEASGNNODE:
                 return multipleAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.NEWLINENODE: {
                 NewlineNode iVisited = (NewlineNode) node;
         
                 // something in here is used to build up ruby stack trace...
                 context.setPosition(iVisited.getPosition());
 
                 if (isTrace(runtime)) {
                     callTraceFunction(runtime, context, "line", self);
                 }
 
                 // TODO: do above but not below for additional newline nodes
                 node = iVisited.getNextNode();
                 continue;
             }
             case NodeTypes.NEXTNODE:
                 return nextNode(runtime, context, node, self, aBlock);
             case NodeTypes.NILNODE:
                 return nilNode(runtime, context);
             case NodeTypes.NOTNODE:
                 return notNode(runtime, context, node, self, aBlock);
             case NodeTypes.NTHREFNODE:
                 return nthRefNode(context, node);
             case NodeTypes.OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
         
                 // add in reverse order
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return pollAndReturn(context, result);
                 node = iVisited.getSecondNode();
                 continue;
             }
             case NodeTypes.OPASGNNODE:
                 return opAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPASGNORNODE:
                 return opAsgnOrNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPELEMENTASGNNODE:
                 return opElementAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPTNNODE:
                 return optNNode(runtime, context, node, self, aBlock);
             case NodeTypes.ORNODE:
                 return orNode(runtime, context, node, self, aBlock);
             case NodeTypes.REDONODE: 
                 return redoNode(context, node);
             case NodeTypes.REGEXPNODE:
                 return regexpNode(runtime, node);
             case NodeTypes.RESCUEBODYNODE:
                 node = ((RescueBodyNode)node).getBodyNode();
                 continue;
             case NodeTypes.RESCUENODE:
                 return rescueNode(runtime, context, node, self, aBlock);
             case NodeTypes.RETRYNODE:
                 return retryNode(context);
             case NodeTypes.RETURNNODE: 
                 return returnNode(runtime, context, node, self, aBlock);
             case NodeTypes.ROOTNODE:
                 return rootNode(runtime, context, node, self, aBlock);
             case NodeTypes.SCLASSNODE:
                 return sClassNode(runtime, context, node, self, aBlock);
             case NodeTypes.SELFNODE:
                 return pollAndReturn(context, self);
             case NodeTypes.SPLATNODE:
                 return splatNode(runtime, context, node, self, aBlock);
             case NodeTypes.STRNODE:
                 return strNode(runtime, node);
             case NodeTypes.SUPERNODE:
                 return superNode(runtime, context, node, self, aBlock);
             case NodeTypes.SVALUENODE:
                 return sValueNode(runtime, context, node, self, aBlock);
             case NodeTypes.SYMBOLNODE:
                 return symbolNode(runtime, node);
             case NodeTypes.TOARYNODE:
                 return toAryNode(runtime, context, node, self, aBlock);
             case NodeTypes.TRUENODE:
                 return trueNode(runtime, context);
             case NodeTypes.UNDEFNODE:
                 return undefNode(runtime, context, node);
             case NodeTypes.UNTILNODE:
                 return untilNode(runtime, context, node, self, aBlock);
             case NodeTypes.VALIASNODE:
                 return valiasNode(runtime, node);
             case NodeTypes.VCALLNODE:
                 return vcallNode(runtime, context, node, self);
             case NodeTypes.WHENNODE:
                 assert false;
                 return null;
             case NodeTypes.WHILENODE:
                 return whileNode(runtime, context, node, self, aBlock);
             case NodeTypes.XSTRNODE:
                 return xStrNode(runtime, context, node, self);
             case NodeTypes.YIELDNODE:
                 return yieldNode(runtime, context, node, self, aBlock);
             case NodeTypes.ZARRAYNODE:
                 return zArrayNode(runtime);
             case NodeTypes.ZSUPERNODE:
                 return zsuperNode(runtime, context, node, self, aBlock);
             default:
                 throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
             }
         } while(true);
     }
 
     private static IRubyObject aliasNode(Ruby runtime, ThreadContext context, Node node) {
         AliasNode iVisited = (AliasNode) node;
    
         if (context.getRubyClass() == null) {
             throw runtime.newTypeError("no class to make alias");
         }
    
         context.getRubyClass().defineAlias(iVisited.getNewName(), iVisited.getOldName());
         context.getRubyClass().callMethod(context, "method_added", runtime.newSymbol(iVisited.getNewName()));
    
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
         IRubyObject newValue = value.convertToType(runtime.getArray(), MethodIndex.TO_ARY, "to_ary", false);
         if (newValue.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime
                     .getKernel()) {
                 newValue = value.convertToType(runtime.getArray(), MethodIndex.TO_A, "to_a", false);
                 if (newValue.getType() != runtime.getClass("Array")) {
                     throw runtime.newTypeError("`to_a' did not return Array");
                 }
             } else {
                 newValue = runtime.newArray(value);
             }
         }
 
         return (RubyArray) newValue;
     }
 
     private static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return value.convertToType(runtime.getArray(), MethodIndex.TO_A, "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     private static IRubyObject attrAssignNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         AttrAssignNode iVisited = (AttrAssignNode) node;
    
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
         
         // If reciever is self then we do the call the same way as vcall
         CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
    
         receiver.callMethod(context, iVisited.getName(), args, callType);
         
         return args[args.length - 1];
     }
 
     private static IRubyObject backRefNode(ThreadContext context, Node node) {
         BackRefNode iVisited = (BackRefNode) node;
         IRubyObject backref = context.getBackref();
         switch (iVisited.getType()) {
         case '~':
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
             result = evalInternal(runtime,context, (Node) iVisited.get(i), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject breakNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BreakNode iVisited = (BreakNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         JumpException je = new JumpException(JumpException.JumpType.BreakJump);
    
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject callNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CallNode iVisited = (CallNode) node;
 
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
 
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         RubyModule module = receiver.getMetaClass();
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             if (iVisited.index != 0) {
                 return receiver.callMethod(context, module, iVisited.index, iVisited.getName(), args, CallType.NORMAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
       
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, receiver, method, iVisited.getName(), args, self, CallType.NORMAL, Block.NULL_BLOCK);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, receiver, module, iVisited.getName(), args, false, Block.NULL_BLOCK);
             }
         }
             
         while (true) {
             try {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
 
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, receiver, method, iVisited.getName(), args, self, CallType.NORMAL, block);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, receiver, module, iVisited.getName(), args, false, block);
             } catch (JumpException je) {
                 switch (je.getJumpType().getTypeId()) {
                 case JumpType.RETRY:
                     // allow loop to retry
                 case JumpType.BREAK:
                     return (IRubyObject) je.getValue();
                 default:
                     throw je;
                 }
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
                         callTraceFunction(runtime, context, "line", self);
                     }
 
                     // Ruby grammar has nested whens in a case body because of
                     // productions case_body and when_args.
                     if (tag instanceof WhenNode) {
                         RubyArray expressions = (RubyArray) evalInternal(runtime,context, ((WhenNode) tag)
                                         .getExpressionNodes(), self, aBlock);
 
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
         Node superNode = iVisited.getSuperNode();
         RubyClass superClass = null;
         if(superNode != null) {
             IRubyObject _super = evalInternal(runtime,context, superNode, self, aBlock);
             if(!(_super instanceof RubyClass)) {
                 throw runtime.newTypeError("superclass must be a Class (" + RubyObject.trueFalseNil(_super) + ") given");
             }
             superClass = superNode == null ? null : (RubyClass)_super;
         }
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingClass = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
         RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
    
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), rubyClass, self, aBlock);
     }
 
     private static IRubyObject classVarAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }     
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
    
         RubyModule rubyClass = getClassVariableBase(context, runtime);                
         if (rubyClass == null) {
             throw runtime.newTypeError("no class/module to define class variable");
         }
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         ClassVarNode iVisited = (ClassVarNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }
 
         return rubyClass.getClassVar(iVisited.getName());
     }
 
     private static IRubyObject colon2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
    
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
         if (leftNode == null) {
             return runtime.getObject().getConstantFrom(iVisited.getName());
         } else {
             IRubyObject result = evalInternal(runtime,context, iVisited.getLeftNode(), self, aBlock);
             if (result instanceof RubyModule) {
                 return ((RubyModule) result).getConstantFrom(iVisited.getName());
             } else {
                 return result.callMethod(context, iVisited.getName(), aBlock);
             }
         }
     }
 
     private static IRubyObject colon3Node(Ruby runtime, Node node) {
         return runtime.getObject().getConstantFrom(((Colon3Node)node).getName());
     }
 
     private static IRubyObject constDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ConstDeclNode iVisited = (ConstDeclNode) node;
         Node constNode = iVisited.getConstNode();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (constNode == null) {
             return context.setConstantInCurrent(iVisited.getName(), result);
         } else if (constNode.nodeId == NodeTypes.COLON2NODE) {
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
    
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
             visibility = Visibility.PRIVATE;
         }
         
         DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), visibility, context.peekCRef());
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility().isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                             Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttachedObject().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
    
         return runtime.getNil();
     }
     
     private static IRubyObject defsNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefsNode iVisited = (DefsNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         RubyClass rubyClass;
    
         if (receiver.isNil()) {
             rubyClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             rubyClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             rubyClass = runtime.getClass("FalseClass");
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure; can't define singleton method.");
             }
             if (receiver.isFrozen()) {
                 throw runtime.newFrozenError("object");
             }
             if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
                 throw runtime.newTypeError("can't define singleton method \"" + iVisited.getName()
                                            + "\" for " + receiver.getType());
             }
    
             rubyClass = receiver.getSingletonClass();
         }
    
         if (runtime.getSafeLevel() >= 4) {
             Object method = rubyClass.getMethods().get(iVisited.getName());
             if (method != null) {
                 throw runtime.newSecurityError("Redefining method prohibited.");
             }
         }
    
         DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), 
                 Visibility.PUBLIC, context.peekCRef());
    
         rubyClass.addMethod(iVisited.getName(), newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
    
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
    
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
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
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             RubyModule module = self.getMetaClass();
             if (module.index != 0 && iVisited.index != 0) {
                 return self.callMethod(context, module, iVisited.index, iVisited.getName(), args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
 
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), args, self, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, self, module, iVisited.getName(), args, false, Block.NULL_BLOCK);
             }
         }
 
         while (true) {
             try {
                 RubyModule module = self.getMetaClass();
                 IRubyObject result = self.callMethod(context, module, iVisited.getName(), args,
                                                      CallType.FUNCTIONAL, block);
                 if (result == null) {
                     result = runtime.getNil();
                 }
                     
                 return result; 
             } catch (JumpException je) {
                 switch (je.getJumpType().getTypeId()) {
                 case JumpType.RETRY:
                     // allow loop to retry
                     break;
                 case JumpType.BREAK:
                     // JRUBY-530, Kernel#loop case:
                     if (je.isBreakInKernelLoop()) {
                         // consume and rethrow or just keep rethrowing?
                         if (block == je.getTarget()) je.setBreakInKernelLoop(false);
                             
                         throw je;
                     }
                         
                     return (IRubyObject) je.getValue();
                 default:
                     throw je;
                 }
             }
         }
     }
 
     private static IRubyObject fixnumNode(Ruby runtime, Node node) {
         return ((FixnumNode)node).getFixnum(runtime);
     }
 
     private static IRubyObject flipNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FlipNode iVisited = (FlipNode) node;
         IRubyObject result = runtime.getNil();
    
         if (iVisited.isExclusive()) {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 result = evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getFalse()
                         : runtime.getTrue();
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         } else {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 if (evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(
                             iVisited.getIndex(),
                             evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue() ? runtime.getFalse()
                                     : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } else {
                     return runtime.getFalse();
                 }
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
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
         
         Block block = ForBlock.createBlock(context, iVisited.getVarNode(), 
                 context.getCurrentScope(), iVisited.getCallable(), self);
    
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
    
                     return recv.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallType.NORMAL, block);
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.RETRY:
                         // do nothing, allow loop to retry
                         break;
                     default:
                         throw je;
                     }
                 }
             }
         } catch (JumpException je) {
             switch (je.getJumpType().getTypeId()) {
             case JumpType.BREAK:
                 return (IRubyObject) je.getValue();
             default:
                 throw je;
             }
         }
     }
 
     private static IRubyObject globalAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (iVisited.getName().length() == 2) {
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 context.getCurrentScope().setLastLine(result);
                 return result;
             case '~':
                 context.setBackref(result);
                 return result;
             }
         }
    
         runtime.getGlobalVariables().set(iVisited.getName(), result);
    
         // FIXME: this should be encapsulated along with the set above
         if (iVisited.getName() == "$KCODE") {
             runtime.setKCode(KCode.create(runtime, result.toString()));
         }
    
         return result;
     }
 
     private static IRubyObject globalVarNode(Ruby runtime, ThreadContext context, Node node) {
         GlobalVarNode iVisited = (GlobalVarNode) node;
         
         if (iVisited.getName().length() == 2) {
             IRubyObject value = null;
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 value = context.getCurrentScope().getLastLine();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             case '~':
                 value = context.getCurrentScope().getBackRef();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             }
         }
         
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
         self.setInstanceVariable(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject instVarNode(Ruby runtime, Node node, IRubyObject self) {
         InstVarNode iVisited = (InstVarNode) node;
         IRubyObject variable = self.getInstanceVariable(iVisited.getName());
    
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
 
         //System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         return result == null ? runtime.getNil() : result;
     }
 
     private static IRubyObject match2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match2Node iVisited = (Match2Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         return ((RubyRegexp) recv).match(value);
     }
     
     private static IRubyObject match3Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match3Node iVisited = (Match3Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         if (value instanceof RubyString) {
             return ((RubyRegexp) recv).match(value);
         } else {
             return value.callMethod(context, "=~", recv);
         }
     }
 
     private static IRubyObject matchNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return ((RubyRegexp) evalInternal(runtime,context, ((MatchNode)node).getRegexpNode(), self, aBlock)).match2();
     }
 
     private static IRubyObject moduleNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ModuleNode iVisited = (ModuleNode) node;
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingModule = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
    
         if (enclosingModule == null) {
             throw runtime.newTypeError("no outer class/module");
         }
    
         RubyModule module;
         if (enclosingModule == runtime.getObject()) {
             module = runtime.getOrCreateModule(name);
         } else {
             module = enclosingModule.defineModuleUnder(name);
         }
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), module, self, aBlock);
     }
 
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
         
         switch (iVisited.getValueNode().nodeId) {
         case NodeTypes.ARRAYNODE: {
             ArrayNode iVisited2 = (ArrayNode) iVisited.getValueNode();
             IRubyObject[] array = new IRubyObject[iVisited2.size()];
 
             for (int i = 0; i < iVisited2.size(); i++) {
                 Node next = iVisited2.get(i);
 
                 array[i] = evalInternal(runtime,context, next, self, aBlock);
             }
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
         }
         case NodeTypes.SPLATNODE: {
             SplatNode splatNode = (SplatNode)iVisited.getValueNode();
             RubyArray rubyArray = splatValue(runtime, evalInternal(runtime, context, ((SplatNode) splatNode).getValue(), self, aBlock));
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
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.NextJump);
    
         je.setTarget(iVisited);
         je.setValue(result);
    
         throw je;
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
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getBackref());
     }
     
     private static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
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
             value = value.callMethod(context, iVisited.index, iVisited.getOperatorName(), evalInternal(runtime,context,
                     iVisited.getValueNode(), self, aBlock));
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
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
    
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
             firstValue = firstValue.callMethod(context, iVisited.index, iVisited.getOperatorName(), evalInternal(runtime,context, iVisited
                             .getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         return receiver.callMethod(context, MethodIndex.ASET, "[]=", expandedArgs);
     }
 
     private static IRubyObject optNNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OptNNode iVisited = (OptNNode) node;
    
         IRubyObject result = runtime.getNil();
         outerLoop: while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         // do nothing, this iteration restarts
                         break;
                     case JumpType.NEXT:
                         // recheck condition
                         break loop;
                     case JumpType.BREAK:
                         // end loop
                         result = (IRubyObject) je.getValue();
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.RedoJump);
    
         je.setValue(node);
    
         throw je;
     }
 
     private static IRubyObject regexpNode(Ruby runtime, Node node) {
         RegexpNode iVisited = (RegexpNode) node;
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
         try {
             return RubyRegexp.newRegexp(runtime, iVisited.getValue().toString(), iVisited.getPattern(), iVisited.getFlags(), lang);
         } catch(jregex.PatternSyntaxException e) {
             //                    System.err.println(iVisited.getValue().toString());
             //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
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
                     
                     if (isRescueHandled(runtime, context, raisedException, exceptionNodesList, self)) {
                         try {
                             return evalInternal(runtime,context, rescueNode, self, aBlock);
                         } catch (JumpException je) {
                             if (je.getJumpType() == JumpException.JumpType.RetryJump) {
                                 // should be handled in the finally block below
                                 //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                                 //state.threadContext.setRaisedException(null);
                                 continue RescuedBlock;
                                 
                             } else {
                                 anotherExceptionRaised = true;
                                 throw je;
                             }
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
    
         JumpException je = new JumpException(JumpException.JumpType.RetryJump);
    
         throw je;
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
 
         je.setTarget(context.getFrameJumpTarget());
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject rootNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RootNode iVisited = (RootNode) node;
         DynamicScope scope = iVisited.getScope();
         
         // Serialization killed our dynamic scope.  We can just create an empty one
         // since serialization cannot serialize an eval (which is the only thing
         // which is capable of having a non-empty dynamic scope).
         if (scope == null) {
             scope = new DynamicScope(iVisited.getStaticScope(), null);
         }
         
         // Each root node has a top-level scope that we need to push
         context.preRootNode(scope);
         
         // FIXME: Wire up BEGIN and END nodes
 
         try {
             return evalInternal(runtime, context, iVisited.getBodyNode(), self, aBlock);
         } finally {
             context.postRootNode();
         }
     }
 
     private static IRubyObject sClassNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SClassNode iVisited = (SClassNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
 
         RubyClass singletonClass;
 
         if (receiver.isNil()) {
             singletonClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             singletonClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             singletonClass = runtime.getClass("FalseClass");
         } else if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             singletonClass = receiver.getSingletonClass();
         }
 
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self, aBlock);
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
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // If no explicit block passed to super, then use the one passed in.
         if (!block.isGiven()) block = aBlock;
         
         return self.callSuper(context, args, block);
     }
     
     private static IRubyObject sValueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aValueSplat(runtime, evalInternal(runtime,context, ((SValueNode) node).getValue(), self, aBlock));
     }
     
     private static IRubyObject symbolNode(Ruby runtime, Node node) {
         return runtime.newSymbol(((SymbolNode) node).getName());
     }
     
     private static IRubyObject toAryNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aryToAry(runtime, evalInternal(runtime,context, ((ToAryNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject trueNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getTrue());
     }
     
     private static IRubyObject undefNode(Ruby runtime, ThreadContext context, Node node) {
         UndefNode iVisited = (UndefNode) node;
         
    
         if (context.getRubyClass() == null) {
             throw runtime
                     .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
         }
         context.getRubyClass().undef(iVisited.getName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject untilNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         UntilNode iVisited = (UntilNode) node;
    
         IRubyObject result = runtime.getNil();
         
         outerLoop: while (!(result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530 until case
                         if (je.getTarget() == aBlock) {
                              je.setTarget(null);
                              
                              throw je;
                         }
                         
                         result = (IRubyObject) je.getValue();
                         
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
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
         RubyModule module = self.getMetaClass();
         if (module.index != 0 && iVisited.index != 0) {
             return self.callMethod(context, module, iVisited.index, iVisited.getName(), 
                     IRubyObject.NULL_ARRAY, CallType.VARIABLE, Block.NULL_BLOCK);
         } else {
             DynamicMethod method = module.searchMethod(iVisited.getName());
 
             IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), IRubyObject.NULL_ARRAY, self, CallType.VARIABLE, Block.NULL_BLOCK);
             if (mmResult != null) {
                 return mmResult;
             }
 
             return method.call(context, self, module, iVisited.getName(), IRubyObject.NULL_ARRAY, false, Block.NULL_BLOCK);
         }
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = runtime.getNil();
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || (result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530, while case
                         if (je.getTarget() == aBlock) {
                             je.setTarget(null);
                             
                             throw je;
                         }
                         
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newStringShared((ByteList) ((XStrNode) node).getValue()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getArgsNode(), self, aBlock);
         if (iVisited.getArgsNode() == null) {
             result = null;
         }
 
         Block block = context.getCurrentFrame().getBlock();
 
         return block.yield(context, result, null, null, iVisited.getCheckState());
     }
 
     private static IRubyObject zArrayNode(Ruby runtime) {
         return runtime.newArray();
     }
     
     private static IRubyObject zsuperNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
 
         Block block = getBlock(runtime, context, self, aBlock, ((ZSuperNode) node).getIterNode());
 
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         context.getCurrentScope().getArgValues(context.getFrameArgs(),context.getCurrentFrame().getRequiredArgCount());
         return self.callSuper(context, context.getFrameArgs(), block);
     }
 
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static void callTraceFunction(Ruby runtime, ThreadContext context, String event, IRubyObject zelf) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
-        runtime.callTraceFunction(context, event, context.getPosition(), zelf, name, type);
+        runtime.callTraceFunction(context, event, context.getPosition(), RubyBinding.newBinding(runtime), name, type);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "class", type);
             }
 
             return evalInternal(runtime,context, bodyNode, type, block);
         } finally {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "end", null);
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
             // Create block for this iter node
             // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
             return Block.createBlock(context, iterNode.getVarNode(),
                     new DynamicScope(iterNode.getScope(), context.getCurrentScope()),
                     iterNode.getCallable(), self);
         } else if (blockNode instanceof BlockPassNode) {
             BlockPassNode blockPassNode = (BlockPassNode) blockNode;
             IRubyObject proc = evalInternal(runtime,context, blockPassNode.getBodyNode(), self, currentBlock);
 
             // No block from a nil proc
             if (proc.isNil()) return Block.NULL_BLOCK;
 
             // If not already a proc then we should try and make it one.
             if (!(proc instanceof RubyProc)) {
                 proc = proc.convertToType(runtime.getClass("Proc"), 0, "to_proc", false);
 
                 if (!(proc instanceof RubyProc)) {
                     throw runtime.newTypeError("wrong argument type "
                             + proc.getMetaClass().getName() + " (expected Proc)");
                 }
             }
 
             // TODO: Add safety check for taintedness
             
             if (currentBlock.isGiven()) {
                 RubyProc procObject = currentBlock.getProcObject();
                 // The current block is already associated with proc.  No need to create a new one
                 if (procObject != null && procObject == proc) return currentBlock;
             }
             
             return ((RubyProc) proc).getBlock();
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     private static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         SinglyLinkedList cref = context.peekCRef();
         RubyModule rubyClass = (RubyModule) cref.getValue();
         if (rubyClass.isSingleton()) {
             cref = cref.getNext();
             rubyClass = (RubyModule) cref.getValue();
             if (cref.getNext() == null) {
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
         case NodeTypes.ATTRASSIGNNODE: {
             AttrAssignNode iVisited = (AttrAssignNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime,context, iVisited.getArgsNode(), "assignment", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.BACKREFNODE:
             return "$" + ((BackRefNode) node).getType();
         case NodeTypes.CALLNODE: {
             CallNode iVisited = (CallNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.CLASSVARASGNNODE: case NodeTypes.CLASSVARDECLNODE: case NodeTypes.CONSTDECLNODE:
         case NodeTypes.DASGNNODE: case NodeTypes.GLOBALASGNNODE: case NodeTypes.LOCALASGNNODE:
         case NodeTypes.MULTIPLEASGNNODE: case NodeTypes.OPASGNNODE: case NodeTypes.OPELEMENTASGNNODE:
             return "assignment";
             
         case NodeTypes.CLASSVARNODE: {
             ClassVarNode iVisited = (ClassVarNode) node;
             
             if (context.getRubyClass() == null && self.getMetaClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } else if (!context.getRubyClass().isSingleton() && context.getRubyClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } 
               
             RubyModule module = (RubyModule) context.getRubyClass().getInstanceVariable("__attached__");
             if (module != null && module.isClassVarDefined(iVisited.getName())) return "class_variable"; 
 
             return null;
         }
         case NodeTypes.COLON2NODE: {
             Colon2Node iVisited = (Colon2Node) node;
             
             try {
                 IRubyObject left = EvaluationState.eval(runtime, context, iVisited.getLeftNode(), self, aBlock);
                 if (left instanceof RubyModule &&
                         ((RubyModule) left).getConstantAt(iVisited.getName()) != null) {
                     return "constant";
                 } else if (left.getMetaClass().isMethodBound(iVisited.getName(), true)) {
                     return "method";
                 }
             } catch (JumpException excptn) {}
             
             return null;
         }
         case NodeTypes.CONSTNODE:
             if (context.getConstantDefined(((ConstNode) node).getName())) {
                 return "constant";
             }
             return null;
         case NodeTypes.DVARNODE:
             return "local-variable(in-block)";
         case NodeTypes.FALSENODE:
             return "false";
         case NodeTypes.FCALLNODE: {
             FCallNode iVisited = (FCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.GLOBALVARNODE:
             if (runtime.getGlobalVariables().isDefined(((GlobalVarNode) node).getName())) {
                 return "global-variable";
             }
             return null;
         case NodeTypes.INSTVARNODE:
             if (self.getInstanceVariable(((InstVarNode) node).getName()) != null) {
                 return "instance-variable";
             }
             return null;
         case NodeTypes.LOCALVARNODE:
             return "local-variable";
         case NodeTypes.MATCH2NODE: case NodeTypes.MATCH3NODE:
             return "method";
         case NodeTypes.NILNODE:
             return "nil";
         case NodeTypes.NTHREFNODE:
             return "$" + ((NthRefNode) node).getMatchNumber();
         case NodeTypes.SELFNODE:
             return "state.getSelf()";
         case NodeTypes.SUPERNODE: {
             SuperNode iVisited = (SuperNode) node;
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "super", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.TRUENODE:
             return "true";
         case NodeTypes.VCALLNODE: {
             VCallNode iVisited = (VCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return "method";
             }
             
             return null;
         }
         case NodeTypes.YIELDNODE:
             return aBlock.isGiven() ? "yield" : null;
         case NodeTypes.ZSUPERNODE: {
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return "super";
             }
             return null;
         }
         default:
             try {
                 EvaluationState.eval(runtime, context, node, self, aBlock);
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
             enclosingModule = (RubyModule) context.peekCRef().getValue();
         }
 
         return enclosingModule;
     }
 
     private static boolean isRescueHandled(Ruby runtime, ThreadContext context, RubyException currentException, ListNode exceptionNodes,
             IRubyObject self) {
         if (exceptionNodes == null) {
             return currentException.isKindOf(runtime.getClass("StandardError"));
         }
 
         IRubyObject[] args = setupArgs(runtime, context, exceptionNodes, self);
 
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(runtime.getClass("Module"))) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             if (args[i].callMethod(context, "===", currentException).isTrue()) return true;
         }
         return false;
     }
 
     /**
      * Helper method.
      *
      * test if a trace function is avaiable.
      *
      */
     private static boolean isTrace(Ruby runtime) {
         return runtime.getTraceFunction() != null;
     }
 
     private static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             ISourcePosition position = context.getPosition();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = evalInternal(runtime,context, argsArrayNode.get(i), self, Block.NULL_BLOCK);
             }
 
             context.setPosition(position);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(evalInternal(runtime,context, node, self, Block.NULL_BLOCK));
     }
 
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 }
diff --git a/src/org/jruby/exceptions/RaiseException.java b/src/org/jruby/exceptions/RaiseException.java
index ed8c07cee0..93d3e07d00 100644
--- a/src/org/jruby/exceptions/RaiseException.java
+++ b/src/org/jruby/exceptions/RaiseException.java
@@ -1,169 +1,169 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
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
 package org.jruby.exceptions;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 
 import org.jruby.*;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RaiseException extends JumpException {
     private static final long serialVersionUID = -7612079169559973951L;
     
     private RubyException exception;
 
     public RaiseException(RubyException actException) {
         this(actException, false);
     }
 
     public RaiseException(Ruby runtime, RubyClass excptnClass, String msg, boolean nativeException) {
         super(msg, JumpType.RaiseJump);
         if (msg == null) {
             msg = "No message available";
         }
         setException((RubyException) excptnClass.callMethod(runtime.getCurrentContext(), "new", new IRubyObject[] {excptnClass.getRuntime().newString(msg)}, Block.NULL_BLOCK), nativeException);
     }
 
     public RaiseException(RubyException exception, boolean isNativeException) {
         super(JumpType.RaiseJump);
         setException(exception, isNativeException);
     }
 
     public static RaiseException createNativeRaiseException(Ruby runtime, Throwable cause) {
         NativeException nativeException = new NativeException(runtime, runtime.getClass(NativeException.CLASS_NAME), cause);
         return new RaiseException(cause, nativeException);
     }
 
     private static String buildMessage(Throwable exception) {
         StringBuffer sb = new StringBuffer();
         StringWriter stackTrace = new StringWriter();
         exception.printStackTrace(new PrintWriter(stackTrace));
     
         sb.append("Native Exception: '").append(exception.getClass()).append("'; ");
         sb.append("Message: ").append(exception.getMessage()).append("; ");
         sb.append("StackTrace: ").append(stackTrace.getBuffer().toString());
 
         return sb.toString();
     }
 
     public RaiseException(Throwable cause, NativeException nativeException) {
         super(buildMessage(cause), cause, JumpType.RaiseJump);
         setException(nativeException, false);
     }
 
     /**
      * Gets the exception
      * @return Returns a RubyException
      */
     public RubyException getException() {
         return exception;
     }
 
     /**
      * Sets the exception
      * @param newException The exception to set
      */
     protected void setException(RubyException newException, boolean nativeException) {
         Ruby runtime = newException.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         if (!context.isWithinDefined()) {
             runtime.getGlobalVariables().set("$!", newException);
         }
 
         if (runtime.getTraceFunction() != null) {
             runtime.callTraceFunction(context, "return", context.getPosition(),
-                    context.getFrameSelf(), context.getFrameName(), context.getFrameKlazz());
+                    RubyBinding.newBinding(runtime), context.getFrameName(), context.getFrameKlazz());
         }
 
         this.exception = newException;
 
         if (runtime.getStackTraces() > 5) {
             return;
         }
 
         runtime.setStackTraces(runtime.getStackTraces() + 1);
 
         if (newException.callMethod(context, "backtrace").isNil() && context.getSourceFile() != null) {
             IRubyObject backtrace = context.createBacktrace(0, nativeException);
             newException.callMethod(context, "set_backtrace", backtrace);
         }
 
         runtime.setStackTraces(runtime.getStackTraces() - 1);
     }
 
     public Throwable fillInStackTrace() {
         return originalFillInStackTrace();
     }
     
     public void printStackTrace() {
         printStackTrace(System.err);
     }
     
     public void printStackTrace(PrintStream ps) {
         StackTraceElement[] trace = getStackTrace();
         int externalIndex = 0;
         for (int i = trace.length - 1; i > 0; i--) {
             if (trace[i].getClassName().indexOf("org.jruby.evaluator") >= 0) {
                 break;
             }
             externalIndex = i;
         }
         IRubyObject backtrace = exception.backtrace();
         Ruby runtime = backtrace.getRuntime();
         if (runtime.getNil() != backtrace) {
             String firstLine = backtrace.callMethod(runtime.getCurrentContext(), "first").callMethod(runtime.getCurrentContext(), MethodIndex.TO_S, "to_s").toString();
             ps.print(firstLine + ": ");
         }
         ps.println(exception.message + " (" + exception.getMetaClass().toString() + ")");
         exception.printBacktrace(ps);
         ps.println("\t...internal jruby stack elided...");
         for (int i = externalIndex; i < trace.length; i++) {
             ps.println("\tfrom " + trace[i].toString());
         }
     }
     
     public void printStackTrace(PrintWriter pw) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         printStackTrace(new PrintStream(baos));
         pw.print(baos.toString());
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/CompiledMethod.java b/src/org/jruby/internal/runtime/methods/CompiledMethod.java
index 045566b2ab..3a0d5c394f 100644
--- a/src/org/jruby/internal/runtime/methods/CompiledMethod.java
+++ b/src/org/jruby/internal/runtime/methods/CompiledMethod.java
@@ -1,117 +1,119 @@
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
  * Copyright (C) 2007 Charles Oliver Nutter <headius@headius.com>
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
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Block;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public abstract class CompiledMethod extends DynamicMethod implements Cloneable{
     private Arity arity;
     private SinglyLinkedList cref;
     
     public CompiledMethod(RubyModule implementationClass, Arity arity, Visibility visibility, SinglyLinkedList cref) {
     	super(implementationClass, visibility);
         this.arity = arity;
         this.cref = cref;
     }
 
     public void preMethod(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper) {
         // needed for const lookups to work
         context.preCompiledMethod(implementationClass, cref);
     }
     
     public void postMethod(ThreadContext context) {
         context.postCompiledMethod();
     }
     
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert false;
         return null;
     }
     
     private IRubyObject wrap(ThreadContext context, Ruby runtime, IRubyObject self, IRubyObject[] args, Block block) {
         try {
             return call(context, self, args, block);
         } catch(RaiseException e) {
             throw e;
         } catch(JumpException e) {
             throw e;
         } catch(ThreadKill e) {
             throw e;
         } catch(MainExitException e) {
             throw e;
         } catch(Exception e) {
             runtime.getJavaSupport().handleNativeException(e);
             return runtime.getNil();
         }        
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         Ruby runtime = context.getRuntime();
         arity.checkArity(runtime, args);
 
         if(runtime.getTraceFunction() != null) {
             ISourcePosition position = context.getPosition();
+            RubyBinding binding = RubyBinding.newBinding(runtime);
 
-            runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
+            runtime.callTraceFunction(context, "c-call", position, binding, name, getImplementationClass());
             try {
                 return wrap(context, runtime, self, args, block);
             } finally {
-                runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
+                runtime.callTraceFunction(context, "c-return", position, binding, name, getImplementationClass());
             }
         }
         return wrap(context, runtime, self, args, block);
     }
 
     public abstract IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block);
     
     public DynamicMethod dup() {
         try {
             CompiledMethod msm = (CompiledMethod)clone();
             return msm;
         } catch (CloneNotSupportedException cnse) {
         return null;
     }
     }
 
     public Arity getArity() {
         return arity;
     }
 }// SimpleInvocationMethod
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 3d589ee3b1..67a1d237db 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,323 +1,330 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.internal.runtime.methods;
 
 import java.util.ArrayList;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends DynamicMethod {
     
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
     private int callCount = 0;
     private Script jitCompiledScript;
 
     private static final boolean JIT_ENABLED;
     private static final boolean JIT_LOGGING;
     private static final boolean JIT_LOGGING_VERBOSE;
     private static final int JIT_THRESHOLD;
       
     static {
         if (Ruby.isSecurityRestricted()) {
             JIT_ENABLED = false;
             JIT_LOGGING = false;
             JIT_LOGGING_VERBOSE = false;
             JIT_THRESHOLD = 50;
         } else {
             JIT_ENABLED = Boolean.getBoolean("jruby.jit.enabled");
             JIT_LOGGING = Boolean.getBoolean("jruby.jit.logging");
             JIT_LOGGING_VERBOSE = Boolean.getBoolean("jruby.jit.logging.verbose");
             JIT_THRESHOLD = Integer.parseInt(System.getProperty("jruby.jit.threshold", "50"));
         }
     }
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body, 
             ArgsNode argsNode, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
 		this.cref = cref;
 		
 		assert argsNode != null;
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
         context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, 
             RubyModule clazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         if (jitCompiledScript != null) {
             try {
                 context.preCompiledMethod(implementationClass, cref);
                 // FIXME: pass block when available
                 return jitCompiledScript.run(context, self, args, block);
             } finally {
                 context.postCompiledMethod();
             }
         } 
           
         return super.call(context, self, clazz, name, args, noSuper, block);
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         if (JIT_ENABLED) runJIT(runtime, name);
         
         if (JIT_ENABLED && jitCompiledScript != null) {
             return jitCompiledScript.run(context, self, args, block);
         }
 
         if (argsNode.getBlockArgNode() != null && block.isGiven()) {
             RubyProc blockArg;
             
             if (block.getProcObject() != null) {
                 blockArg = (RubyProc) block.getProcObject();
             } else {
                 blockArg = runtime.newProc(false, block);
                 blockArg.getBlock().isLambda = block.isLambda;
             }
             // We pass depth zero since we know this only applies to newly created local scope
             context.getCurrentScope().setValue(argsNode.getBlockArgNode().getCount(), blockArg, 0);
         }
 
+        RubyBinding binding = null;
         try {
             prepareArguments(context, runtime, args);
             
             getArity().checkArity(runtime, args);
 
-            traceCall(context, runtime, self, name);
+            if (runtime.getTraceFunction() != null) {
+                binding = RubyBinding.newBinding(runtime);
+                traceCall(context, runtime, binding, name);
+            }
                     
             return EvaluationState.eval(runtime, context, body, self, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
-            traceReturn(context, runtime, self, name);
+            if (binding != null) {
+                traceReturn(context, runtime, binding, name);
+            }
         }
     }
 
     private void runJIT(Ruby runtime, String name) {
         if (callCount >= 0 && getArity().isFixed() && argsNode.getBlockArgNode() == null && argsNode.getOptArgs() == null && argsNode.getRestArg() == -1) {
             callCount++;
             if (callCount >= JIT_THRESHOLD) {
                 String className = null;
                 if (JIT_LOGGING) {
                     className = getImplementationClass().getBaseName();
                     if (className == null) {
                         className = "<anon class>";
                     }
                 }
                 
                 try {
                     String cleanName = CodegenUtils.cleanJavaIdentifier(name);
                     StandardASMCompiler compiler = new StandardASMCompiler(cleanName + hashCode(), body.getPosition().getFile());
                     compiler.startScript();
                     Object methodToken = compiler.beginMethod("__file__", getArity().getValue(), staticScope.getNumberOfVariables());
                     NodeCompilerFactory.getCompiler(body).compile(body, compiler);
                     compiler.endMethod(methodToken);
                     compiler.endScript();
                     Class sourceClass = compiler.loadClass(runtime);
                     jitCompiledScript = (Script)sourceClass.newInstance();
                     
                     if (JIT_LOGGING) System.err.println("compiled: " + className + "." + name);
                 } catch (Exception e) {
                     if (JIT_LOGGING_VERBOSE) System.err.println("could not compile: " + className + "." + name + " because of: \"" + e.getMessage() + '"');
                 } finally {
                     callCount = -1;
                 }
             }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         int expectedArgsCount = argsNode.getArgsCount();
 
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
 
         // FIXME: This seems redundant with the arity check in internalCall...is it actually different?
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (expectedArgsCount > 0) {
             context.getCurrentScope().setArgValues(args, expectedArgsCount);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == -1 && hasOptArgs) {
             int opt = expectedArgsCount + argsNode.getOptArgs().size();
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount;
         if (argsNode.getOptArgs() != null) {
             count += argsNode.getOptArgs().size();
         }
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
         
         if (hasOptArgs) {
             ListNode optArgs = argsNode.getOptArgs();
    
             int j = 0;
             for (int i = expectedArgsCount; i < args.length && j < optArgs.size(); i++, j++) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 AssignmentVisitor.assign(runtime, context, context.getFrameSelf(), optArgs.get(j), args[i], Block.NULL_BLOCK, true);
                 expectedArgsCount++;
             }
    
             // assign the default values, adding to the end of allArgs
             while (j < optArgs.size()) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 allArgs.add(EvaluationState.eval(runtime, context, optArgs.get(j++), context.getFrameSelf(), Block.NULL_BLOCK));
             }
         }
         
         // build an array from *rest type args, also adding to allArgs
         
         // ENEBO: Does this next comment still need to be done since I killed hasLocalVars:
         // move this out of the scope.hasLocalVariables() condition to deal
         // with anonymous restargs (* versus *rest)
         
         
         // none present ==> -1
         // named restarg ==> >=0
         // anonymous restarg ==> -2
         if (restArg != -1) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
-    private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
+    private void traceReturn(ThreadContext context, Ruby runtime, RubyBinding binding, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = context.getPreviousFramePosition();
-        runtime.callTraceFunction(context, "return", position, self, name, getImplementationClass());
+        runtime.callTraceFunction(context, "return", position, binding, name, getImplementationClass());
     }
-
-    private void traceCall(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
+    
+    private void traceCall(ThreadContext context, Ruby runtime, RubyBinding binding, String name) {
         if (runtime.getTraceFunction() == null) return;
-
-		ISourcePosition position = body != null ? body.getPosition() : context.getPosition(); 
-
-		runtime.callTraceFunction(context, "call", position, self, name, getImplementationClass());
+        
+        ISourcePosition position = body != null ? body.getPosition() : context.getPosition();
+        
+        runtime.callTraceFunction(context, "call", position, binding, name, getImplementationClass());
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), cref);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java b/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
index 53280cc8f2..b2e07c03f4 100644
--- a/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
+++ b/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
@@ -1,102 +1,105 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  */
 public class FullFunctionCallbackMethod extends DynamicMethod {
     private Callback callback;
 
     public FullFunctionCallbackMethod(RubyModule implementationClass, Callback callback, Visibility visibility) {
         super(implementationClass, visibility);
         this.callback = callback;
     }
 
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         context.preReflectedMethodInternalCall(implementationClass, clazz, self, name, args, getArity().required(), noSuper, block, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postReflectedMethodInternalCall();
     }
     
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         Ruby runtime = context.getRuntime();
         ISourcePosition position = null;
+        RubyBinding binding = null;
         boolean isTrace = runtime.getTraceFunction() != null;
         
         if (isTrace) {
             position = context.getPosition();
+            binding = RubyBinding.newBinding(runtime);
             
-            runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
+            runtime.callTraceFunction(context, "c-call", position, binding, name, getImplementationClass());
         }
         
         try {
             return callback.execute(self, args, block);
         } catch (JumpException je) {
             switch (je.getJumpType().getTypeId()) {
             case JumpException.JumpType.RETURN:
                 if (je.getTarget() == this) return (IRubyObject)je.getValue();
             default:
                 throw je;
             }
         } finally {
             if (isTrace) {
-                runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
+                runtime.callTraceFunction(context, "c-return", position, binding, name, getImplementationClass());
             }
         }
     }
     
     public Callback getCallback() {
         return callback;
     }
 
     public Arity getArity() {
         return getCallback().getArity();
     }
     
     public DynamicMethod dup() {
         return new FullFunctionCallbackMethod(getImplementationClass(), callback, getVisibility());
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java b/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
index e07a14e975..879d8d3cd6 100644
--- a/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
+++ b/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
@@ -1,108 +1,110 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.exceptions.MainExitException;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public abstract class FullInvocationMethod extends DynamicMethod implements Cloneable {
     private Arity arity;
     public FullInvocationMethod(RubyModule implementationClass, Arity arity, Visibility visibility) {
     	super(implementationClass, visibility);
         this.arity = arity;
     }
 
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         context.preReflectedMethodInternalCall(implementationClass, klazz, self, name, args, arity.required(), noSuper, block, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postReflectedMethodInternalCall();
     }
 
     private IRubyObject wrap(Ruby runtime, IRubyObject self, IRubyObject[] args, Block block) {
         try {
             return call(self,args,block);
         } catch(RaiseException e) {
             throw e;
         } catch(JumpException e) {
             throw e;
         } catch(ThreadKill e) {
             throw e;
         } catch(MainExitException e) {
             throw e;
         } catch(Exception e) {
             runtime.getJavaSupport().handleNativeException(e);
             return runtime.getNil();
         }        
     }
     
 	public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         Ruby runtime = context.getRuntime();
         arity.checkArity(runtime, args);
 
         if(runtime.getTraceFunction() != null) {
             ISourcePosition position = context.getPosition();
+            RubyBinding binding = RubyBinding.newBinding(runtime);
 
-            runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
+            runtime.callTraceFunction(context, "c-call", position, binding, name, getImplementationClass());
             try {
                 return wrap(runtime,self,args,block);
             } finally {
-                runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
+                runtime.callTraceFunction(context, "c-return", position, binding, name, getImplementationClass());
             }
         }
         return wrap(runtime,self,args,block);
     }
 
     public abstract IRubyObject call(IRubyObject self, IRubyObject[] args, Block block);
     
 	public DynamicMethod dup() {
         try {
             return (FullInvocationMethod) clone();
         } catch (CloneNotSupportedException e) {
             return null;
         }
     }
 
 	public Arity getArity() {
 		return arity;
 	}
 }
diff --git a/src/org/jruby/internal/runtime/methods/SimpleCallbackMethod.java b/src/org/jruby/internal/runtime/methods/SimpleCallbackMethod.java
index 754e22adef..c9aba9c33e 100644
--- a/src/org/jruby/internal/runtime/methods/SimpleCallbackMethod.java
+++ b/src/org/jruby/internal/runtime/methods/SimpleCallbackMethod.java
@@ -1,93 +1,95 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  */
 public class SimpleCallbackMethod extends DynamicMethod {
     private Callback callback;
 
     public SimpleCallbackMethod(RubyModule implementationClass, Callback callback, Visibility visibility) {
         super(implementationClass, visibility);
         this.callback = callback;
     }
 
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
     }
     
     public void postMethod(ThreadContext context) {
     }
 
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert false;
         return null;
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
     	assert args != null;
         Ruby runtime = context.getRuntime();
         
         if (runtime.getTraceFunction() != null) {
             ISourcePosition position = context.getPosition();
-
-            runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
+            
+            RubyBinding binding = RubyBinding.newBinding(runtime);
+            runtime.callTraceFunction(context, "c-call", position, binding, name, getImplementationClass());
             try {
                 return callback.execute(self, args, Block.NULL_BLOCK);
             } finally {
-                runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
+                runtime.callTraceFunction(context, "c-return", position, binding, name, getImplementationClass());
             }
         }
-		return callback.execute(self, args, Block.NULL_BLOCK);
+        return callback.execute(self, args, Block.NULL_BLOCK);
     }
 
     public Callback getCallback() {
         return callback;
     }
 
     public Arity getArity() {
         return getCallback().getArity();
     }
     
     public DynamicMethod dup() {
         return new SimpleCallbackMethod(getImplementationClass(), callback, getVisibility());
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/SimpleInvocationMethod.java b/src/org/jruby/internal/runtime/methods/SimpleInvocationMethod.java
index 78104cc13f..efdf7c54c0 100644
--- a/src/org/jruby/internal/runtime/methods/SimpleInvocationMethod.java
+++ b/src/org/jruby/internal/runtime/methods/SimpleInvocationMethod.java
@@ -1,111 +1,113 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.exceptions.MainExitException;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public abstract class SimpleInvocationMethod extends DynamicMethod implements Cloneable{
     private Arity arity;
     public SimpleInvocationMethod(RubyModule implementationClass, Arity arity, Visibility visibility) {
     	super(implementationClass, visibility);
         this.arity = arity;
     }
 
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
     }
     
     public void postMethod(ThreadContext context) {
     }
     
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert false;
         return null;
     }
     
     private IRubyObject wrap(Ruby runtime, IRubyObject self, IRubyObject[] args) {
         try {
             return call(self,args);
         } catch(RaiseException e) {
             throw e;
         } catch(JumpException e) {
             throw e;
         } catch(ThreadKill e) {
             throw e;
         } catch(MainExitException e) {
             throw e;
         } catch(Exception e) {
             runtime.getJavaSupport().handleNativeException(e);
             return runtime.getNil();
         }        
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         Ruby runtime = context.getRuntime();
         arity.checkArity(runtime, args);
 
         if(runtime.getTraceFunction() != null) {
             ISourcePosition position = context.getPosition();
+            RubyBinding binding = RubyBinding.newBinding(runtime);
 
-            runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
+            runtime.callTraceFunction(context, "c-call", position, binding, name, getImplementationClass());
             try {
                 return wrap(runtime,self,args);
             } finally {
-                runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
+                runtime.callTraceFunction(context, "c-return", position, binding, name, getImplementationClass());
             }
         }
         return wrap(runtime,self,args);
     }
 
     public abstract IRubyObject call(IRubyObject self, IRubyObject[] args);
     
     public DynamicMethod dup() {
         try {
             return (SimpleInvocationMethod) clone();
         } catch (CloneNotSupportedException e) {
             return null;
         }
     }
 
     public Arity getArity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/YARVMethod.java b/src/org/jruby/internal/runtime/methods/YARVMethod.java
index b542e71055..145b45ed2a 100644
--- a/src/org/jruby/internal/runtime/methods/YARVMethod.java
+++ b/src/org/jruby/internal/runtime/methods/YARVMethod.java
@@ -1,196 +1,203 @@
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
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
 
 import java.util.ArrayList;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
+import org.jruby.RubyBinding;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.ast.executable.YARVMachine;
 import org.jruby.ast.executable.ISeqPosition;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @version $Revision: 1.2 $
  */
 public class YARVMethod extends DynamicMethod {
     private SinglyLinkedList cref;
     private YARVMachine.InstructionSequence iseq;
     private StaticScope staticScope;
     private Arity arity;
 
     public YARVMethod(RubyModule implementationClass, YARVMachine.InstructionSequence iseq, StaticScope staticScope, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.staticScope = staticScope;
         this.iseq = iseq;
 		this.cref = cref;
 
         boolean opts = iseq.args_arg_opts > 0 || iseq.args_rest > 0;
         boolean req = iseq.args_argc > 0;
         if(!req && !opts) {
             this.arity = Arity.noArguments();
         } else if(req && !opts) {
             this.arity = Arity.fixed(iseq.args_argc);
         } else if(opts && !req) {
             this.arity = Arity.optional();
         } else {
             this.arity = Arity.required(iseq.args_argc);
         }
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
         context.preDefMethodInternalCall(clazz, name, self, args, arity.required(), block, noSuper, cref, staticScope, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
     	assert args != null;
         
         Ruby runtime = context.getRuntime();
+        RubyBinding binding = null;
         
         try {
             prepareArguments(context, runtime, args);
             getArity().checkArity(runtime, args);
 
-            traceCall(context, runtime, self, name);
+            if (runtime.getTraceFunction() != null) {
+                binding = RubyBinding.newBindingForEval(runtime);
+                traceCall(context, runtime, binding, name);
+            }
 
             DynamicScope sc = new DynamicScope(staticScope,null);
             for(int i = 0; i<args.length; i++) {
                 sc.setValue(i,args[i],0);
             }
 
             return YARVMachine.INSTANCE.exec(context, self, sc, iseq.body);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
-            traceReturn(context, runtime, self, name);
+            if (binding != null) {
+                traceReturn(context, runtime, binding, name);
+            }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         context.setPosition(new ISeqPosition(iseq));
 
         int expectedArgsCount = iseq.args_argc;
         int restArg = iseq.args_rest;
         boolean hasOptArgs = iseq.args_arg_opts > 0;
 
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == 0 && hasOptArgs) {
             int opt = expectedArgsCount + iseq.args_arg_opts;
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount + iseq.args_arg_opts + iseq.args_rest;
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
 
         if (restArg != 0) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
-
-    private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject receiver, String name) {
+    
+    private void traceReturn(ThreadContext context, Ruby runtime, RubyBinding binding, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
-
+        
         ISourcePosition position = context.getPreviousFramePosition();
-        runtime.callTraceFunction(context, "return", position, receiver, name, getImplementationClass());
+        runtime.callTraceFunction(context, "return", position, binding, name, getImplementationClass());
     }
-
-    private void traceCall(ThreadContext context, Ruby runtime, IRubyObject receiver, String name) {
+    
+    private void traceCall(ThreadContext context, Ruby runtime, RubyBinding binding, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
-
-		ISourcePosition position = context.getPosition(); 
-
-		runtime.callTraceFunction(context, "call", position, receiver, name, getImplementationClass());
+        
+        ISourcePosition position = context.getPosition();
+        
+        runtime.callTraceFunction(context, "call", position, binding, name, getImplementationClass());
     }
-
+    
     public Arity getArity() {
         return this.arity;
     }
     
     public DynamicMethod dup() {
         return new YARVMethod(getImplementationClass(), iseq, staticScope, getVisibility(), cref);
     }	
 }// YARVMethod
diff --git a/test/test_trace_func.rb b/test/test_trace_func.rb
index eb5f2bcfe6..9193545c7a 100644
--- a/test/test_trace_func.rb
+++ b/test/test_trace_func.rb
@@ -1,45 +1,70 @@
 require 'test/unit'
 
 class TestTraceFunc < Test::Unit::TestCase
   def test_class
     output = []
     set_trace_func proc { |event, file, line, id, binding, classname|
       output << sprintf("%s %s:%d %s %s", event, file, line, id ? id : 'nil', classname)
     }
 
     class << self
     end
 
     set_trace_func nil
 
     expected = ["line #{__FILE__}:10 test_class TestTraceFunc",
     "class #{__FILE__}:10 test_class TestTraceFunc",
     "end #{__FILE__}:10 test_class TestTraceFunc",
     "line #{__FILE__}:13 test_class TestTraceFunc",
     "c-call #{__FILE__}:13 set_trace_func Kernel"]
     assert_equal(expected, output);
   end
 
   def test_block_and_vars
     output = []
     set_trace_func proc { |event, file, line, id, binding, classname|
       output << sprintf("%s %s:%d %s %s", event, file, line, id, classname)
     }
 
     1.times {
       a = 1
       b = 2
     }
 
     set_trace_func nil
 
     expected = ["line #{__FILE__}:29 test_block_and_vars TestTraceFunc",
     "c-call #{__FILE__}:29 times Integer",
     "line #{__FILE__}:30 test_block_and_vars TestTraceFunc",
     "line #{__FILE__}:31 test_block_and_vars TestTraceFunc",
     "c-return #{__FILE__}:29 times Integer",
     "line #{__FILE__}:34 test_block_and_vars TestTraceFunc",
     "c-call #{__FILE__}:34 set_trace_func Kernel"]
     assert_equal(expected, output)
   end
+
+  def bogus_method
+  end
+
+  def test_trace_binding
+    a = true
+    expected = [["a", "expected", "results"],
+                ["a", "expected", "results"],
+                ["a", "expected", "results"],
+                ["a", "expected", "results"],
+                [],
+                [],
+                ["a", "expected", "results"],
+                ["a", "expected", "results"]]
+    results = []
+    set_trace_func proc { |event, file, line, id, binding, classname| results << eval('local_variables', binding) }
+
+    1.to_i # c-call, two traces
+    # newline, one trace
+    bogus_method # call, two traces
+    # newline, one trace
+    set_trace_func nil # c-call, two traces
+ 
+    assert_equal(expected, results)
+  end
 end
