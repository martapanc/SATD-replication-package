diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 5fce8e7930..fcd22e12f3 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -581,2001 +581,2001 @@ public final class Ruby {
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             } else {
                 System.err.println("Error, could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         }
         
         return script;
     }
     
     private IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     private IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         assert scriptNode != null : "scriptNode is not null";
         
         try {
             return scriptNode.interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         ThreadContext context = getCurrentContext();
 
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode;
 
         try {
             return ((RootNode)scriptNode).interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public Parser getParser() {
         return parser;
     }
     
     public BeanManager getBeanManager() {
         return beanManager;
     }
     
     public JITCompiler getJITCompiler() {
         return jitCompiler;
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
     
     @Deprecated
     public static Ruby getCurrentInstance() {
         return null;
     }
     
     @Deprecated
     public static void setCurrentInstance(Ruby runtime) {
     }
     
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace.
      * 
      * @param name The name of the module
      * @return The module or null if not found
      */
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName The name of the module; <em>must</em> be an interned String
      * @return The module or null if not found
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** 
      * Retrieve the class with the given name from the Object namespace.
      *
      * @param name The name of the class
      * @return The class
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * Retrieve the class with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** 
      * Define a new class under the Object namespace. Roughly equivalent to
      * rb_define_class in MRI.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass);
     }
 
     /** 
      * A variation of defineClass that allows passing in an array of subplementary
      * call sites for improving dynamic invocation performance.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator, CallSite[] callSites) {
         return defineClassUnder(name, superClass, allocator, objectClass, callSites);
     }
 
     /**
      * Define a new class with the given name under the given module or class
      * namespace. Roughly equivalent to rb_define_class_under in MRI.
      * 
      * If the name specified is already bound, its value will be returned if:
      * * It is a class
      * * No new superclass is being defined
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         return defineClassUnder(name, superClass, allocator, parent, null);
     }
 
     /**
      * A variation of defineClassUnder that allows passing in an array of
      * supplementary call sites to improve dynamic invocation.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @param callSites The array of call sites to add
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent, CallSite[] callSites) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) {
                 throw newNameError(name + " is already defined", name);
             }
             // If we define a class in Ruby, but later want to allow it to be defined in Java,
             // the allocator needs to be updated
             if (klazz.getAllocator() != allocator) {
                 klazz.setAllocator(allocator);
             }
             return klazz;
         }
         
         boolean parentIsObject = parent == objectClass;
 
         if (superClass == null) {
             String className = parentIsObject ? name : parent.getName() + "::" + name;  
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed", className);
             
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, !parentIsObject, callSites);
     }
 
     /** 
      * Define a new module under the Object namespace. Roughly equivalent to
      * rb_define_module in MRI.
      * 
      * @param name The name of the new module
      * @returns The new module
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass);
     }
 
     /**
      * Define a new module with the given name under the given module or
      * class namespace. Roughly equivalent to rb_define_module_under in MRI.
      * 
      * @param name The name of the new module
      * @param parent The class or module namespace under which to define the
      * module
      * @returns The new module
      */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
         
         boolean parentIsObject = parent == objectClass;
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             
             if (parentIsObject) {
                 throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
             } else {
                 throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
             }
         }
 
         return RubyModule.newModule(this, name, parent, !parentIsObject);
     }
 
     /**
      * From Object, retrieve the named module. If it doesn't exist a
      * new module is created.
      * 
      * @param name The name of the module
      * @returns The existing or new module
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
 
 
     /** 
      * Retrieve the current safe level.
      * 
      * @see org.jruby.Ruby#setSaveLevel
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
 
     /** 
      * Set the current safe level:
      * 
      * 0 - strings from streams/environment/ARGV are tainted (default)
      * 1 - no dangerous operation by tainted value
      * 2 - process/file operations prohibited
      * 3 - all generated objects are tainted
      * 4 - no global (non-tainted) variable modification/no direct output
      * 
      * The safe level is set using $SAFE in Ruby code. It is not particularly
      * well supported in JRuby.
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
 
     // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
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
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** 
      * This method is called immediately after constructing the Ruby instance.
      * The main thread is prepared for execution, all core classes and libraries
      * are initialized, and any libraries required on the command line are
      * loaded.
      */
     private void init() {
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
         safeLevel = config.getSafeLevel();
         
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         javaSupport = new JavaSupport(this);
         
         if (RubyInstanceConfig.POOLING_ENABLED) {
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot();
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.loadPaths());
 
         // initialize builtin libraries
         initBuiltins();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             RubyKernel.require(getTopSelf(), newString(scriptName), Block.NULL_BLOCK);
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initRoot() {
         boolean oneNine = is1_9();
         // Bootstrap the top of the hierarchy
         if (oneNine) {
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.OBJECT_ALLOCATOR);
             objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         } else {
             objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         }
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         if (oneNine) basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         if (oneNine) metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         if (oneNine) RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
         
         // set constants now that they're initialized
         if (oneNine) objectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
     }
 
     private void initCore() {
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             RubyEncoding.createEncodingClass(this);
             RubyConverter.createConverterClass(this);
             encodingService = new EncodingService(this);
         }
 
         RubySymbol.createSymbolClass(this);
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (!is1_9()) {
             if (profile.allowModule("Precision")) {
                 RubyPrecision.createPrecisionModule(this);
             }
         }
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         if (is1_9()) {
             if (profile.allowClass("Complex")) {
                 RubyComplex.createComplexClass(this);
             }
             if (profile.allowClass("Rational")) {
                 RubyRational.createRationalClass(this);
             }
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
         }
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass, new IRubyObject[]{newString("Tms"), newSymbol("utime"), newSymbol("stime"), newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) {
             RubyBinding.createBindingClass(this);
         }
         // Math depends on all numeric types
         if (profile.allowModule("Math")) {
             RubyMath.createMathModule(this);
         }
         if (profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if (profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if (profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if (profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
         if (profile.allowClass("Proc")) {
             RubyProc.createProcClass(this);
         }
         if (profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
         if (profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if (profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
         if (profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
         if (profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
         // depends on IO, FileTest
         if (profile.allowClass("File")) {
             RubyFile.createFileClass(this);
         }
         if (profile.allowClass("File::Stat")) {
             RubyFileStat.createFileStatClass(this);
         }
         if (profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if (profile.allowClass("Time")) {
             RubyTime.createTimeClass(this);
         }
         if (profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
         if (!isSecurityRestricted()) {
             // Signal uses sun.misc.* classes, this is not allowed
             // in the security-sensitive environments
             if (profile.allowModule("Signal")) {
                 RubySignal.createSignal(this);
             }
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     public static final int NIL_PREFILLED_ARRAY_SIZE = RubyArray.ARRAY_DEFAULT_SIZE * 8;
     private final IRubyObject nilPrefilledArray[] = new IRubyObject[NIL_PREFILLED_ARRAY_SIZE];
     public IRubyObject[] getNilPrefilledArray() {
         return nilPrefilledArray;
     }
 
     private void initExceptions() {
         standardError = defineClassIfAllowed("StandardError", exceptionClass);
         runtimeError = defineClassIfAllowed("RuntimeError", standardError);
         ioError = defineClassIfAllowed("IOError", standardError);
         scriptError = defineClassIfAllowed("ScriptError", exceptionClass);
         rangeError = defineClassIfAllowed("RangeError", standardError);
         signalException = defineClassIfAllowed("SignalException", exceptionClass);
         
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
             nameErrorMessage = RubyNameError.createNameErrorMessageClass(this, nameError);            
         }
         if (profile.allowClass("NoMethodError")) {
             noMethodError = RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }
         if (profile.allowClass("SystemExit")) {
             systemExit = RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("LocalJumpError")) {
             localJumpError = RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("NativeException")) {
             nativeException = NativeException.createClass(this, runtimeError);
         }
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
 
         fatal = defineClassIfAllowed("Fatal", exceptionClass);
         interrupt = defineClassIfAllowed("Interrupt", signalException);
         typeError = defineClassIfAllowed("TypeError", standardError);
         argumentError = defineClassIfAllowed("ArgumentError", standardError);
         indexError = defineClassIfAllowed("IndexError", standardError);
         stopIteration = defineClassIfAllowed("StopIteration", indexError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator()); 
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
             }
         }
 
         initErrno();
     }
     
     private RubyClass defineClassIfAllowed(String name, RubyClass superClass) {
 	// TODO: should probably apply the null object pattern for a
 	// non-allowed class, rather than null
         if (superClass != null && profile.allowClass(name)) {
             return defineClass(name, superClass, superClass.getAllocator());
         }
         return null;
     }
 
     private Map<Integer, RubyClass> errnos = new HashMap<Integer, RubyClass>();
 
     public RubyClass getErrno(int n) {
         return errnos.get(n);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrno() {
         if (profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
             try {
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.value(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 e.printStackTrace();
             }
         }
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
             errnos.put(i, errno);
             errno.defineConstant("Errno", newFixnum(i));
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         
         addLazyBuiltin("minijava.rb", "minijava", "org.jruby.java.MiniJava");
         
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.RubyJRuby$ExtLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("generator_internal.rb", "generator_internal", "org.jruby.ext.Generator$Service");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.DigestLibrary$MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRef$WeakRefLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.Factory$Service");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             addLazyBuiltin("fiber.jar", "fiber", "org.jruby.libraries.FiberLibrary");
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
         
         String[] builtins = {"yaml", 
                              "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
                              "yaml/compat", "yaml/constants", "yaml/dbm", 
                              "yaml/emitter", "yaml/encoding", "yaml/error", 
                              "yaml/rubytypes", "yaml/store", "yaml/stream", 
                              "yaml/stringio", "yaml/tag", "yaml/types", 
                              "yaml/yamlnode", "yaml/ypath", 
                              "jsignal" };
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             getLoadService().require("builtin/prelude.rb");
         }
 
         getLoadService().require("builtin/core_ext/symbol");
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getJRubyClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
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
     
     public RubyModule getEtc() {
         return etcModule;
     }
     
     public void setEtc(RubyModule etcModule) {
         this.etcModule = etcModule;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyClass getBasicObject() {
         return basicObjectClass;
     }
 
     public RubyClass getModule() {
         return moduleClass;
     }
 
     public RubyClass getClassClass() {
         return classClass;
     }
     
     public RubyModule getKernel() {
         return kernelModule;
     }
     void setKernel(RubyModule kernelModule) {
         this.kernelModule = kernelModule;
     }
 
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
     }
     
     public RubyClass getDummy() {
         return dummyClass;
     }
 
     public RubyModule getComparable() {
         return comparableModule;
     }
     void setComparable(RubyModule comparableModule) {
         this.comparableModule = comparableModule;
     }    
 
     public RubyClass getNumeric() {
         return numericClass;
     }
     void setNumeric(RubyClass numericClass) {
         this.numericClass = numericClass;
     }    
 
     public RubyClass getFloat() {
         return floatClass;
     }
     void setFloat(RubyClass floatClass) {
         this.floatClass = floatClass;
     }
     
     public RubyClass getInteger() {
         return integerClass;
     }
     void setInteger(RubyClass integerClass) {
         this.integerClass = integerClass;
     }    
     
     public RubyClass getFixnum() {
         return fixnumClass;
     }
     void setFixnum(RubyClass fixnumClass) {
         this.fixnumClass = fixnumClass;
     }
 
     public RubyClass getComplex() {
         return complexClass;
     }
     void setComplex(RubyClass complexClass) {
         this.complexClass = complexClass;
     }
 
     public RubyClass getRational() {
         return rationalClass;
     }
     void setRational(RubyClass rationalClass) {
         this.rationalClass = rationalClass;
     }
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }
 
     public RubyClass getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
     }
 
     public RubyClass getGenerator() {
         return generatorClass;
     }
-    void setGenerator(RubyClass generatorClass) {
+    public void setGenerator(RubyClass generatorClass) {
         this.generatorClass = generatorClass;
     }
 
     public RubyClass getYielder() {
         return yielderClass;
     }
     void setYielder(RubyClass yielderClass) {
         this.yielderClass = yielderClass;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }
 
     public RubyClass getEncoding() {
         return encodingClass;
     }
     void setEncoding(RubyClass encodingClass) {
         this.encodingClass = encodingClass;
     }
 
     public RubyClass getConverter() {
         return converterClass;
     }
     void setConverter(RubyClass converterClass) {
         this.converterClass = converterClass;
     }
 
     public RubyClass getSymbol() {
         return symbolClass;
     }
     void setSymbol(RubyClass symbolClass) {
         this.symbolClass = symbolClass;
     }
 
     public RubyClass getArray() {
         return arrayClass;
     }    
     void setArray(RubyClass arrayClass) {
         this.arrayClass = arrayClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }
     void setHash(RubyClass hashClass) {
         this.hashClass = hashClass;
     }
 
     public RubyClass getRange() {
         return rangeClass;
     }
     void setRange(RubyClass rangeClass) {
         this.rangeClass = rangeClass;
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
 
     public IRubyObject[] getSingleNilArray() {
         return singleNilArray;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
     void setNilClass(RubyClass nilClass) {
         this.nilClass = nilClass;
     }
 
     public RubyClass getTrueClass() {
         return trueClass;
     }
     void setTrueClass(RubyClass trueClass) {
         this.trueClass = trueClass;
     }
 
     public RubyClass getFalseClass() {
         return falseClass;
     }
     void setFalseClass(RubyClass falseClass) {
         this.falseClass = falseClass;
     }
 
     public RubyClass getProc() {
         return procClass;
     }
     void setProc(RubyClass procClass) {
         this.procClass = procClass;
     }
 
     public RubyClass getBinding() {
         return bindingClass;
     }
     void setBinding(RubyClass bindingClass) {
         this.bindingClass = bindingClass;
     }
 
     public RubyClass getMethod() {
         return methodClass;
     }
     void setMethod(RubyClass methodClass) {
         this.methodClass = methodClass;
     }    
 
     public RubyClass getUnboundMethod() {
         return unboundMethodClass;
     }
     void setUnboundMethod(RubyClass unboundMethodClass) {
         this.unboundMethodClass = unboundMethodClass;
     }    
 
     public RubyClass getMatchData() {
         return matchDataClass;
     }
     void setMatchData(RubyClass matchDataClass) {
         this.matchDataClass = matchDataClass;
     }    
 
     public RubyClass getRegexp() {
         return regexpClass;
     }
     void setRegexp(RubyClass regexpClass) {
         this.regexpClass = regexpClass;
     }    
 
     public RubyClass getTime() {
         return timeClass;
     }
     void setTime(RubyClass timeClass) {
         this.timeClass = timeClass;
     }    
 
     public RubyModule getMath() {
         return mathModule;
     }
     void setMath(RubyModule mathModule) {
         this.mathModule = mathModule;
     }    
 
     public RubyModule getMarshal() {
         return marshalModule;
     }
     void setMarshal(RubyModule marshalModule) {
         this.marshalModule = marshalModule;
     }    
 
     public RubyClass getBignum() {
         return bignumClass;
     }
     void setBignum(RubyClass bignumClass) {
         this.bignumClass = bignumClass;
     }    
 
     public RubyClass getDir() {
         return dirClass;
     }
     void setDir(RubyClass dirClass) {
         this.dirClass = dirClass;
     }    
 
     public RubyClass getFile() {
         return fileClass;
     }
     void setFile(RubyClass fileClass) {
         this.fileClass = fileClass;
     }    
 
     public RubyClass getFileStat() {
         return fileStatClass;
     }
     void setFileStat(RubyClass fileStatClass) {
         this.fileStatClass = fileStatClass;
     }    
 
     public RubyModule getFileTest() {
         return fileTestModule;
     }
     void setFileTest(RubyModule fileTestModule) {
         this.fileTestModule = fileTestModule;
     }
     
     public RubyClass getIO() {
         return ioClass;
     }
     void setIO(RubyClass ioClass) {
         this.ioClass = ioClass;
     }    
 
     public RubyClass getThread() {
         return threadClass;
     }
     void setThread(RubyClass threadClass) {
         this.threadClass = threadClass;
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
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
     void setTmsStruct(RubyClass tmsStruct) {
         this.tmsStruct = tmsStruct;
     }
     
     public IRubyObject getPasswdStruct() {
         return passwdStruct;
     }
     void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     void setGroupStruct(RubyClass groupStruct) {
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
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
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
         return verbose;
     }
 
     public boolean isVerbose() {
         return isVerbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
         isVerbose = verbose.isTrue();
         warningsEnabled = !verbose.isNil();
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
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
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
 
         if (RubyException.TRACE_TYPE == RubyException.RUBINIUS) {
             printRubiniusTrace(excp);
             return;
         }
 
         ThreadContext context = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(context, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (context.getFile() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(context, errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first();
 
             if (mesg.isNil()) {
                 printErrorPos(context, errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == getRuntimeError() && (info == null || info.length() == 0)) {
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
 
     private void printRubiniusTrace(RubyException exception) {
 
         ThreadContext.RubyStackTraceElement[] frames = exception.getBacktraceFrames();
 
         ArrayList firstParts = new ArrayList();
         int longestFirstPart = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = frame.getClassName() + "#" + frame.getMethodName();
             if (firstPart.length() > longestFirstPart) longestFirstPart = firstPart.length();
             firstParts.add(firstPart);
         }
 
         // determine spacing
         int center = longestFirstPart
                 + 2 // initial spaces
                 + 1; // spaces before "at"
 
         StringBuffer buffer = new StringBuffer();
 
         buffer
                 .append("An exception has occurred:\n")
                 .append("    ");
 
         if (exception.getMetaClass() == getRuntimeError() && exception.message(getCurrentContext()).toString().length() == 0) {
             buffer.append("No current exception (RuntimeError)");
         } else {
             buffer.append(exception.message(getCurrentContext()).toString());
         }
 
         buffer
                 .append('\n')
                 .append('\n')
                 .append("Backtrace:\n");
 
         int i = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = (String)firstParts.get(i);
             String secondPart = frame.getFileName() + ":" + frame.getLineNumber();
             
             buffer.append("  ");
             for (int j = 0; j < center - firstPart.length(); j++) {
                 buffer.append(' ');
             }
             buffer.append(firstPart);
             buffer.append(" at ");
             buffer.append(secondPart);
             buffer.append('\n');
             i++;
         }
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(buffer.toString());
     }
 
     private void printErrorPos(ThreadContext context, PrintStream errorStream) {
         if (context.getFile() != null) {
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
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             parseFile(in, scriptName, null).interpret(this, context, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
             
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
             context.setFile(file);
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
diff --git a/src/org/jruby/RubyEnumerator.java b/src/org/jruby/RubyEnumerator.java
index d5b9a076a5..5087111cd4 100644
--- a/src/org/jruby/RubyEnumerator.java
+++ b/src/org/jruby/RubyEnumerator.java
@@ -1,374 +1,411 @@
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
  * Copyright (C) 2006 Michael Studman <me@michaelstudman.com>
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
 import org.jruby.anno.JRubyModule;
+import org.jruby.ext.Generator;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * Implementation of Ruby's Enumerator module.
  */
 @JRubyModule(name="Enumerable::Enumerator", include="Enumerable")
 public class RubyEnumerator extends RubyObject {
     /** target for each operation */
     private IRubyObject object;
 
     /** method to invoke for each operation */
     private IRubyObject method;
 
     /** args to each method */
     private IRubyObject[] methodArgs;
 
     public static void defineEnumerator(Ruby runtime) {
         runtime.getKernel().defineAnnotatedMethods(RubyEnumeratorKernel.class);
 
         RubyModule enm = runtime.getClassFromPath("Enumerable");
         enm.defineAnnotatedMethods(RubyEnumeratorEnumerable.class);
 
         final RubyClass enmr;
         if (runtime.is1_9()) {
             enmr = runtime.defineClass("Enumerator", runtime.getObject(), ENUMERATOR_ALLOCATOR);
         } else {
             enmr = enm.defineClassUnder("Enumerator", runtime.getObject(), ENUMERATOR_ALLOCATOR);
         }
 
         enmr.includeModule(enm);
         enmr.defineAnnotatedMethods(RubyEnumerator.class);
         runtime.setEnumerator(enmr);
 
         if (runtime.is1_9()) {
-            RubyGenerator.createGeneratorClass(runtime);
+            Generator.createGenerator(runtime);
             RubyYielder.createYielderClass(runtime);
         }
     }
 
     private static ObjectAllocator ENUMERATOR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyEnumerator(runtime, klass);
         }
     };
 
     private RubyEnumerator(Ruby runtime, RubyClass type) {
         super(runtime, type);
         object = method = runtime.getNil();
     }
 
     private RubyEnumerator(Ruby runtime, IRubyObject object, IRubyObject method, IRubyObject[]args) {
         super(runtime, runtime.getEnumerator());
         this.object = object;
         this.method = method;
         this.methodArgs = args;
     }
 
     static IRubyObject enumeratorize(Ruby runtime, IRubyObject object, String method) {
         return new RubyEnumerator(runtime, object, runtime.fastNewSymbol(method), IRubyObject.NULL_ARRAY);
     }
 
     static IRubyObject enumeratorize(Ruby runtime, IRubyObject object, String method, IRubyObject arg) {
         return new RubyEnumerator(runtime, object, runtime.fastNewSymbol(method), new IRubyObject[]{arg});
     }
 
     static IRubyObject enumeratorize(Ruby runtime, IRubyObject object, String method, IRubyObject[]args) {
         return new RubyEnumerator(runtime, object, runtime.fastNewSymbol(method), args); // TODO: make sure it's really safe to not to copy it
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context) {
         throw context.getRuntime().newArgumentError(0, 1);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject object) {
         return initialize(object, context.getRuntime().fastNewSymbol("each"), NULL_ARRAY);
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject object, IRubyObject method) {
         return initialize(object, method, NULL_ARRAY);
     }
 
     private IRubyObject initialize(IRubyObject object, IRubyObject method, IRubyObject[] methodArgs) {
         this.object = object;
         this.method = method;
         this.methodArgs = methodArgs;
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject object, IRubyObject method, IRubyObject methodArg) {
         return initialize(object, method, new IRubyObject[] { methodArg });
     }
 
     @JRubyMethod(name = "initialize", required = 1, rest = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
         IRubyObject[] methArgs = new IRubyObject[args.length - 2];
         System.arraycopy(args, 2, methArgs, 0, methArgs.length);
         return initialize(args[0], args[1], methArgs);
     }
 
     /**
      * Send current block and supplied args to method on target. According to MRI
      * Block may not be given and "each" should just ignore it and call on through to
      * underlying method.
      */
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each(ThreadContext context, Block block) {
         return object.callMethod(context, method.asJavaString(), methodArgs, block);
     }
 
     @JRubyMethod(name = "inspect", compat = CompatVersion.RUBY1_9)
     public IRubyObject inspect19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.isInspecting(this)) return inspect(context, true);
 
         try {
             runtime.registerInspecting(this);
             return inspect(context, false);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     private IRubyObject inspect(ThreadContext context, boolean recurse) {
         Ruby runtime = context.getRuntime();
         ByteList bytes = new ByteList();
         bytes.append((byte)'#').append((byte)'<');
         bytes.append(getMetaClass().getName().getBytes());
         bytes.append((byte)':').append((byte)' ');
 
         if (recurse) {
             bytes.append("...>".getBytes());
             return RubyString.newStringNoCopy(runtime, bytes).taint(context);
         } else {
             boolean tainted = isTaint();
             bytes.append(RubyObject.inspect(context, object).getByteList());
             bytes.append((byte)':');
             bytes.append(method.asString().getByteList());
             if (methodArgs.length > 0) {
                 bytes.append((byte)'(');
                 for (int i= 0; i < methodArgs.length; i++) {
                     bytes.append(RubyObject.inspect(context, methodArgs[i]).getByteList());
                     if (i < methodArgs.length - 1) {
                         bytes.append((byte)',').append((byte)' ');
                     } else {
                         bytes.append((byte)')');
                     }
                     if (methodArgs[i].isTaint()) tainted = true;
                 }
             }
             bytes.append((byte)'>');
             RubyString result = RubyString.newStringNoCopy(runtime, bytes);
             if (tainted) result.setTaint(true);
             return result;
         }
     }
 
     protected static IRubyObject newEnumerator(ThreadContext context, IRubyObject arg) {
         return context.getRuntime().getEnumerator().callMethod(context, "new", arg);
     }
 
     protected static IRubyObject newEnumerator(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getEnumerator(), "new", arg1, arg2);
     }
 
     protected static IRubyObject newEnumerator(ThreadContext context, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getEnumerator(), "new", arg1, arg2, arg3);
     }
 
     public static final class RubyEnumeratorKernel {
         @JRubyMethod(name = {"to_enum", "enum_for"}, frame = true)
         public static IRubyObject obj_to_enum(ThreadContext context, IRubyObject self, Block block) {
             return newEnumerator(context, self);
         }
 
         @JRubyMethod(name = {"to_enum", "enum_for"}, frame = true)
         public static IRubyObject obj_to_enum(ThreadContext context, IRubyObject self, IRubyObject arg, Block block) {
             return newEnumerator(context, self, arg);
         }
 
         @JRubyMethod(name = {"to_enum", "enum_for"}, frame = true)
         public static IRubyObject obj_to_enum(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
             return newEnumerator(context, self, arg0, arg1);
         }
 
         @JRubyMethod(name = {"to_enum", "enum_for"}, optional = 1, rest = true, frame = true)
         public static IRubyObject obj_to_enum(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             newArgs[0] = self;
             System.arraycopy(args, 0, newArgs, 1, args.length);
 
             return context.getRuntime().getEnumerator().callMethod(context, "new", newArgs);
         }
     }
 
     public static final class RubyEnumeratorEnumerable {
         public static IRubyObject each_slice(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             final int size = RubyNumeric.num2int(arg);
             final Ruby runtime = context.getRuntime();
             if (size <= 0) throw runtime.newArgumentError("invalid slice size");
 
             final RubyArray result[] = new RubyArray[]{runtime.newArray(size)};
 
             RubyEnumerable.callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     result[0].append(largs[0]);
                     if (result[0].size() == size) {
                         block.yield(ctx, result[0]);
                         result[0] = runtime.newArray(size);
                     }
                     return runtime.getNil();
                 }
             });
 
             if (result[0].size() > 0) block.yield(context, result[0]);
             return context.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "each_slice")
         public static IRubyObject each_slice19(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             return block.isGiven() ? each_slice(context, self, arg, block) : enumeratorize(context.getRuntime(), self, "each_slice", arg);
         }
 
         @JRubyMethod(name = "enum_slice")
         public static IRubyObject enum_slice19(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             return block.isGiven() ? each_slice(context, self, arg, block) : enumeratorize(context.getRuntime(), self, "enum_slice", arg);
         }
 
         public static IRubyObject each_cons(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             final int size = (int)RubyNumeric.num2long(arg);
             final Ruby runtime = context.getRuntime();
             if (size <= 0) throw runtime.newArgumentError("invalid size");
 
             final RubyArray result = runtime.newArray(size);
 
             RubyEnumerable.callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result.size() == size) result.shift(ctx);
                     result.append(largs[0]);
                     if (result.size() == size) block.yield(ctx, result.aryDup());
                     return runtime.getNil();
                 }
             });
 
             return runtime.getNil();        
         }
 
         @JRubyMethod(name = "each_cons")
         public static IRubyObject each_cons19(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             return block.isGiven() ? each_cons(context, self, arg, block) : enumeratorize(context.getRuntime(), self, "each_cons", arg);
         }
 
         @JRubyMethod(name = "enum_cons")
         public static IRubyObject enum_cons19(ThreadContext context, IRubyObject self, IRubyObject arg, final Block block) {
             return block.isGiven() ? each_cons(context, self, arg, block) : enumeratorize(context.getRuntime(), self, "enum_cons", arg);
         }
 
         @JRubyMethod(name = "each_with_object", frame = true, compat = CompatVersion.RUBY1_9)
         public static IRubyObject each_with_object(ThreadContext context, IRubyObject self, final IRubyObject arg, final Block block) {
             return with_object_common(context, self, arg, block, "each_with_object");
         }
 
         @JRubyMethod(name = "with_object", frame = true, compat = CompatVersion.RUBY1_9)
         public static IRubyObject with_object(ThreadContext context, IRubyObject self, final IRubyObject arg, final Block block) {
             return with_object_common(context, self, arg, block, "with_object");
         }
 
         private static IRubyObject with_object_common(ThreadContext context, IRubyObject self,
                 final IRubyObject arg, final Block block, final String rubyMethodName) {
             final Ruby runtime = context.getRuntime();
             if (!block.isGiven()) return enumeratorize(runtime, self , rubyMethodName, arg);
 
             RubyEnumerable.callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     block.call(ctx, new IRubyObject[]{runtime.newArray(largs[0], arg)});
                     return runtime.getNil();
                 }
             });
             return arg;
         }
     }
     private static class EachWithIndex implements BlockCallback {
         private int index = 0;
         private final Block block;
         private final Ruby runtime;
 
         public EachWithIndex(ThreadContext ctx, Block block) {
             this.block = block;
             this.runtime = ctx.getRuntime();
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject[] iargs, Block block) {
             return this.block.call(context, new IRubyObject[] { runtime.newArray(RubyEnumerable.checkArgs(runtime, iargs), runtime.newFixnum(index++)) });
         }
     }
 
     private static IRubyObject with_index_common(ThreadContext context, IRubyObject self, 
             final Block block, final String rubyMethodName) {
         final Ruby runtime = context.getRuntime();
         if (!block.isGiven()) return enumeratorize(runtime, self , rubyMethodName);
         
         IRubyObject[] args = new IRubyObject[0];
 
         RubyEnumerator e = (RubyEnumerator)self;
         if(e.methodArgs != null) {
             args = e.methodArgs;
         }
 
         return RubyEnumerable.callEach(runtime, context, self, args, new EachWithIndex(context, block));
     }
 
     @JRubyMethod(name = "each_with_index", frame = true)
     public static IRubyObject each_with_index(ThreadContext context, IRubyObject self, final Block block) {
         return with_index_common(context, self, block, "each_with_index");
     }
 
     @JRubyMethod(name = "with_index", frame = true)
     public static IRubyObject with_index(ThreadContext context, IRubyObject self, final Block block) {
         return with_index_common(context, self, block, "with_index");
     }
 
-    @JRubyMethod(name = "next", frame = true)
+    @JRubyMethod(name = "next", frame = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject next(ThreadContext context, IRubyObject self, Block block) {
         context.getRuntime().getLoadService().lockAndRequire("generator");
-        return self.callMethod(context, "next", new IRubyObject[0], block);
+        return self.callMethod(context, "next", IRubyObject.NULL_ARRAY, block);
     }
 
-    @JRubyMethod(name = "rewind", frame = true)
+    @JRubyMethod(name = "rewind", frame = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject rewind(ThreadContext context, IRubyObject self, Block block) {
         context.getRuntime().getLoadService().lockAndRequire("generator");
-        return self.callMethod(context, "rewind", new IRubyObject[0], block);
+        return self.callMethod(context, "rewind", IRubyObject.NULL_ARRAY, block);
+    }
+
+    private static final ByteList ITER_END_MESSAGE = ByteList.create("iteration reached at end");
+
+    @JRubyMethod(name = "next", frame = true, compat = CompatVersion.RUBY1_9)
+    public static IRubyObject next19(ThreadContext context, IRubyObject self) {
+        Ruby runtime = context.getRuntime();
+        IRubyObject currentGen = ensureGenerator(context, self);
+
+        if (currentGen.callMethod(context, "end?").isTrue()) {
+            currentGen.callMethod(context, "rewind");
+            return self.callMethod(context, "raise",
+                    new IRubyObject[] {runtime.getStopIteration(), RubyString.newStringShared(runtime, ITER_END_MESSAGE)},
+                    Block.NULL_BLOCK);
+        }
+        return currentGen.callMethod(context, "next");
+    }
+
+    @JRubyMethod(name = "rewind", frame = true, compat = CompatVersion.RUBY1_9)
+    public static IRubyObject rewind19(ThreadContext context, IRubyObject self, Block block) {
+        IRubyObject currentGen = ensureGenerator(context, self);
+        
+        currentGen.callMethod(context, "rewind");
+        return self;
+    }
+
+    private static IRubyObject ensureGenerator(ThreadContext context, IRubyObject self) {
+        Ruby runtime = context.getRuntime();
+        
+        IRubyObject currentGen = self.getInstanceVariables().fastGetInstanceVariable("@generator");
+        if (currentGen == null || !currentGen.isTrue()) {
+            currentGen = runtime.getGenerator().callMethod(context, "new", self);
+            self.getInstanceVariables().fastSetInstanceVariable("@generator", currentGen);
+        }
+
+        return currentGen;
     }
 }
diff --git a/src/org/jruby/ext/Generator.java b/src/org/jruby/ext/Generator.java
index c421723a10..eb9375cd8a 100644
--- a/src/org/jruby/ext/Generator.java
+++ b/src/org/jruby/ext/Generator.java
@@ -1,337 +1,337 @@
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
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.builtin.IRubyObject;
 
-import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.Visibility;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="Generator", include="Enumerable")
 public class Generator {
     public static class Service implements Library {
         public void load(final Ruby runtime, boolean wrap) throws IOException {
             createGenerator(runtime);
         }
     }
 
-    public static void createGenerator(Ruby runtime) throws IOException {
+    public static void createGenerator(Ruby runtime) {
         RubyClass cGen = runtime.defineClass("Generator",runtime.getObject(), runtime.getObject().getAllocator());
         cGen.includeModule(runtime.getEnumerable());
         cGen.defineAnnotatedMethods(Generator.class);
+        runtime.setGenerator(cGen);
     }
 
     static class GeneratorData implements Runnable {
         private IRubyObject gen;
         private Object mutex = new Object();
 
         private IRubyObject enm;
         private RubyProc proc;
 
         private Thread t;
         private volatile boolean end;
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
             if (t != null) {
                 // deal with previously started thread first
                 t.interrupt();
                 try {
                     t.join();
                 } catch (InterruptedException e) {
                     // do nothing
                 }
             }
 
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
                     if(ibc.haveValue() && proc == null) {
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
             private boolean shouldSkip = false;
             public IRubyObject call(ThreadContext context, IRubyObject[] iargs, Block block) {
                 if (shouldSkip) {
                     // the thread was interrupted, this is a signal
                     // that we should not do any work, and exit the thread.
                     return gen.getRuntime().getNil();
                 }
                 boolean inter = true;
                 synchronized(mutex) {
                     mutex.notifyAll();
                     while(inter) {
                         try {
                             mutex.wait();
                             inter = false;
                         } catch(InterruptedException e) {
                             shouldSkip = true;
                             return gen.getRuntime().getNil();
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
             ThreadContext context = gen.getRuntime().getCurrentContext();
             if(enm != null) {
                 RuntimeHelpers.invoke(context, enm, "each", 
                         CallBlock.newCallClosure(enm,enm.getMetaClass().getRealClass(),Arity.noArguments(),ibc,context));
             } else {
                 proc.call(context, new IRubyObject[]{gen});
             }
             end = true;
             synchronized(mutex) {
                 mutex.notifyAll();
             }
         }
     }
 
     @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
     public static IRubyObject new_instance(IRubyObject self, IRubyObject[] args, Block block) {
         // Generator#new
         IRubyObject result = new RubyObject(self.getRuntime(),(RubyClass)self);
         result.dataWrapStruct(new GeneratorData(result));
         result.callMethod(self.getRuntime().getCurrentContext(), "initialize", args, block);
         return result;
     }
 
     @JRubyMethod(optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public static IRubyObject initialize(IRubyObject self, IRubyObject[] args, Block block) {
         // Generator#initialize
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         
         self.getInstanceVariables().setInstanceVariable("@queue",self.getRuntime().newArray());
         self.getInstanceVariables().setInstanceVariable("@index",self.getRuntime().newFixnum(0));
         
         if(Arity.checkArgumentCount(self.getRuntime(), args,0,1) == 1) {
             d.setEnum(args[0]);
         } else {
             d.setProc(self.getRuntime().newProc(Block.Type.PROC, block));
         }
         return self;
     }
 
     @JRubyMethod(frame = true)
     public static IRubyObject yield(IRubyObject self, IRubyObject value, Block block) {
         // Generator#yield
         self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"<<",value);
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         d.doWait();
         return self;
     }
 
     @JRubyMethod(name = "end?")
     public static IRubyObject end_p(IRubyObject self) {
         // Generator#end_p
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         
         boolean emptyQueue = self.getInstanceVariables().getInstanceVariable("@queue").callMethod(
                 self.getRuntime().getCurrentContext(), "empty?").isTrue();
         
         return (d.isEnd() && emptyQueue) ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "next?")
     public static IRubyObject next_p(IRubyObject self) {
         // Generator#next_p        
         return RuntimeHelpers.negate(
                 RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, "end?"),
                 self.getRuntime());
     }
 
     @JRubyMethod(name = {"index", "pos"})
     public static IRubyObject index(IRubyObject self) {
         // Generator#index
         return self.getInstanceVariables().getInstanceVariable("@index");
     }
 
     @JRubyMethod(frame = true)
     public static IRubyObject next(IRubyObject self, Block block) {
         // Generator#next
         GeneratorData d = (GeneratorData)self.dataGetStruct();
 
         if(RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, "end?").isTrue()) {
             throw self.getRuntime().newEOFError("no more elements available");
         }
 
         d.generate();
         self.getInstanceVariables().setInstanceVariable("@index",self.getInstanceVariables().getInstanceVariable("@index").callMethod(self.getRuntime().getCurrentContext(), "+",self.getRuntime().newFixnum(1)));
         return self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"shift");
     }
 
     @JRubyMethod(frame = true)
     public static IRubyObject current(IRubyObject self, Block block) {
         // Generator#current
         if(self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(), "empty?").isTrue()) {
             throw self.getRuntime().newEOFError("no more elements available");
         }
         return self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"first");
     }
 
     @JRubyMethod(frame = true)
     public static IRubyObject rewind(IRubyObject self, Block block) {
         // Generator#rewind
         if(self.getInstanceVariables().getInstanceVariable("@index").callMethod(self.getRuntime().getCurrentContext(),"nonzero?").isTrue()) {
             GeneratorData d = (GeneratorData)self.dataGetStruct();
 
             self.getInstanceVariables().setInstanceVariable("@queue",self.getRuntime().newArray());
             self.getInstanceVariables().setInstanceVariable("@index",self.getRuntime().newFixnum(0));
             
             d.start();
         }
 
         return self;
     }
 
     @JRubyMethod(frame = true)
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
