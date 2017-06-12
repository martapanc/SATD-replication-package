diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index a4ece6c3f5..5ef5105b07 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -443,2002 +443,2001 @@ public final class Ruby {
     /**
      * Parse the script contained in the given input stream, using the given
      * filename as the name of the script, and return the root Node. This
      * is used to verify that the script syntax is valid, for jruby -c. The
      * current scope (generally the top-level scope) is used as the parent
      * scope for parsing.
      * 
      * @param inputStream The input stream from which to read the script
      * @param filename The filename to use for parsing
      * @returns The root node of the parsed script
      */
     public Node parseFromMain(InputStream inputStream, String filename) {
         if (config.isInlineScript()) {
             return parseInline(inputStream, filename, getCurrentContext().getCurrentScope());
         } else {
             return parseFileFromMain(inputStream, filename, getCurrentContext().getCurrentScope());
         }
     }
 
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      *
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     @Deprecated
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean unused) {
         return runWithGetsLoop(scriptNode, printing, processLineEnds, split);
     }
     
     /**
      * Run the given script with a "while gets; end" loop wrapped around it.
      * This is primarily used for the -n command-line flag, to allow writing
      * a short script that processes input lines using the specified code.
      * 
      * @param scriptNode The root node of the script to execute
      * @param printing Whether $_ should be printed after each loop (as in the
      * -p command-line flag)
      * @param processLineEnds Whether line endings should be processed by
      * setting $\ to $/ and <code>chop!</code>ing every line read
      * @param split Whether to split each line read using <code>String#split</code>
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
 
         // we do preand post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         RuntimeHelpers.preLoad(context, ((RootNode)scriptNode).getStaticScope().getVariables());
 
         try {
             while (RubyKernel.gets(context, getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                 loop: while (true) { // Used for the 'redo' command
                     try {
                         if (processLineEnds) {
                             getGlobalVariables().get("$_").callMethod(context, "chop!");
                         }
 
                         if (split) {
                             getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                         }
 
                         if (script != null) {
                             runScriptBody(script);
                         } else {
                             runInterpreterBody(scriptNode);
                         }
 
                         if (printing) RubyKernel.print(context, getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
                         break loop;
                     } catch (JumpException.RedoJump rj) {
                         // do nothing, this iteration restarts
                     } catch (JumpException.NextJump nj) {
                         // recheck condition
                         break loop;
                     } catch (JumpException.BreakJump bj) {
                         // end loop
                         return (IRubyObject) bj.getValue();
                     }
                 }
             }
         } finally {
             RuntimeHelpers.postLoad(context);
         }
         
         return getNil();
     }
 
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      *
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     @Deprecated
     public IRubyObject runNormally(Node scriptNode, boolean unused) {
         return runNormally(scriptNode);
     }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode) {
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile || config.isShowBytecode()) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
         }
 
         if (script != null) {
             if (config.isShowBytecode()) {
                 return getNil();
             }
             
             return runScript(script);
         } else {
             failForcedCompile(scriptNode);
             
             return runInterpreter(scriptNode);
         }
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled.
      *
      * @param node The node to attempt to compiled
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), false);
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled. This version accepts an ASTInspector instance assumed to
      * have appropriate flags set for compile optimizations, such as to turn
      * on heap-based local variables to share an existing scope.
      *
      * @param node The node to attempt to compiled
      * @param inspector The ASTInspector to use for making optimization decisions
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node, ASTInspector inspector) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), inspector, false);
     }
 
     private void failForcedCompile(Node scriptNode) throws RaiseException {
         if (config.getCompileMode().shouldPrecompileAll()) {
             throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getPosition().getFile());
         }
     }
 
     private void handeCompileError(Node node, Throwable t) {
         if (config.isJitLoggingVerbose() || config.isDebug()) {
             LOG.debug("warning: could not compile: {}; full trace follows", node.getPosition().getFile());
             LOG.debug(t.getMessage(), t);
         }
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
 
         return tryCompile(node, cachedClassName, classLoader, inspector, dump);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, ASTInspector inspector, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             StandardASMCompiler asmCompiler = null;
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 asmCompiler = new StandardASMCompiler(cachedClassName.replace('.', '/'), filename);
             } else {
                 asmCompiler = new StandardASMCompiler(classname, filename);
             }
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
 
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 // save script off to disk
                 String pathName = cachedClassName.replace('.', '/');
                 JITCompiler.saveToCodeCache(this, asmCompiler.getClassByteArray(), "ruby/jit", new File(RubyInstanceConfig.JIT_CODE_CACHE, pathName + ".class"));
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 LOG.info("compiled: " + node.getPosition().getFile());
             }
         } catch (Throwable t) {
             handeCompileError(node, t);
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         return runScript(script, false);
     }
     
     public IRubyObject runScript(Script script, boolean wrap) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), wrap);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         try {
             if (getInstanceConfig().getCompileMode() == CompileMode.OFFIR) {
                 return Interpreter.interpret(this, rootNode, self);
             } else {
                 return ASTInterpreter.INTERPRET_ROOT(this, context, rootNode, getTopSelf(), Block.NULL_BLOCK);
             }
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(((RootNode) scriptNode).getBodyNode());
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
     public void addModule(RubyModule module) {
         synchronized (allModules) {
             allModules.add(module);
         }
     }
     public void eachModule(Function1<Object, IRubyObject> func) {
         synchronized (allModules) {
             for (RubyModule module : allModules) {
                 func.apply(module);
             }
         }
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
 
     @Deprecated
     public RubyModule fastGetModule(String internedName) {
         return getModule(internedName);
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
     @Deprecated
     public RubyClass fastGetClass(String internedName) {
         return getClass(internedName);
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
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed");
             
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
 
         // Set up the main thread in thread service
         threadService.initMainThread();
 
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
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
         
         booting = false;
 
         // initialize builtin libraries
         initBuiltins();
         
         if(config.isProfiling()) {
             getLoadService().require("jruby/profiler/shutdown_hook");
         }
 
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.require(scriptName);
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
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
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
         
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
     }
 
     private void initCore() {
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         encodingService = new EncodingService(this);
 
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
             // RubyRandom depends on Bignum existence.
             if (is1_9()) {
                 RubyRandom.createRandomClass(this);
             } else {
                 setDefaultRand(new RubyRandom.RandomType(this));
             }
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
         if (profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
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
                 invalidByteSequenceError = defineClassUnder("InvalidByteSequenceError", encodingError, encodingError.getAllocator(), encodingClass);
                 undefinedConversionError = defineClassUnder("UndefinedConversionError", encodingError, encodingError.getAllocator(), encodingClass);
                 converterNotFoundError = defineClassUnder("ConverterNotFoundError", encodingError, encodingError.getAllocator(), encodingClass);
                 fiberError = defineClass("FiberError", standardError, standardError.getAllocator());
             }
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             recursiveKey = newSymbol("__recursive_key__");
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
                 // define EAGAIN now, so that future EWOULDBLOCK will alias to it
                 // see MRI's error.c and its explicit ordering of Errno definitions.
                 createSysErr(Errno.EAGAIN.intValue(), Errno.EAGAIN.name());
                 
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.intValue(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 LOG.error(e.getMessage(), e);
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
             if (errnos.get(i) == null) {
                 RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
                 errnos.put(i, errno);
                 errno.defineConstant("Errno", newFixnum(i));
             } else {
                 // already defined a class for this errno, reuse it (JRUBY-4747)
                 getErrno().setConstant(name, errnos.get(i));
             }
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.ext.jruby.JRubyExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.ext.jruby.JRubyCoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("jruby/synchronized.rb", "jruby/synchronized", "org.jruby.ext.jruby.JRubySynchronizedLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
-        addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
-        addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
+        addLazyBuiltin("digest.jar", "digest.so", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
         addLazyBuiltin("yecht.jar", "yecht", "YechtService");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.libraries.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             LoadService.reflectedLoad(this, "fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader(), false);
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
         String[] builtins = {"jsignal_internal", "generator_internal"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             loadFile("builtin/prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/prelude.rb"), false);
             if (!config.isDisableGems()) {
                 // NOTE: This has been disabled because gem_prelude is terribly broken.
                 //       We just require 'rubygems' in gem_prelude, at least for now.
                 //defineModule("Gem"); // dummy Gem module for prelude
                 loadFile("builtin/gem_prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/gem_prelude.rb"), false);
             }
         }
 
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
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
 
     public IRubyObject getRootFiber() {
         return rootFiber;
     }
 
     public void setRootFiber(IRubyObject fiber) {
         rootFiber = fiber;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setCurrentLine(int line) {
         currentLine = line;
     }
 
     public int getCurrentLine() {
         return currentLine;
     }
 
     public void setArgsFile(IRubyObject argsFile) {
         this.argsFile = argsFile;
     }
 
     public IRubyObject getArgsFile() {
         return argsFile;
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
     
     public RubyClass getRandomClass() {
         return randomClass;
     }
     void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
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
diff --git a/src/org/jruby/RubyDigest.java b/src/org/jruby/RubyDigest.java
index f6b53e3d11..e7119bb206 100644
--- a/src/org/jruby/RubyDigest.java
+++ b/src/org/jruby/RubyDigest.java
@@ -1,495 +1,460 @@
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
  * Copyright (C) 2006, 2007 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2008 Vladimir Sizikov <vsizikov@gmail.com>
  * Copyright (C) 2009 Joseph LaFata <joe@quibb.org>
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
 
 import java.security.AccessController;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.security.PrivilegedAction;
 import java.security.Provider;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyModule(name="Digest")
 public class RubyDigest {
     private static Provider provider = null;
 
     public static void createDigest(Ruby runtime) {
         // We're not setting the provider or anything, but it seems that BouncyCastle does some internal things in its
         // provider's constructor which require it to be executed in a secure context.
         // Ideally this hack should be removed. See JRUBY-3919 and this BC bug:
         //   http://www.bouncycastle.org/jira/browse/BJA-227
         provider = (Provider) AccessController.doPrivileged(new PrivilegedAction() {
             public Object run() {
                 try {
                     return Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").newInstance();
                 } catch(Throwable t) {
                     // provider is not available
                     return null;
                 }
             }
         });
 
         RubyModule mDigest = runtime.defineModule("Digest");
         mDigest.defineAnnotatedMethods(RubyDigest.class);
         RubyModule mDigestInstance = mDigest.defineModuleUnder("Instance");
         mDigestInstance.defineAnnotatedMethods(DigestInstance.class);
         RubyClass cDigestClass = mDigest.defineClassUnder("Class", runtime.getObject(), DigestClass.DIGEST_CLASS_ALLOCATOR);
         cDigestClass.defineAnnotatedMethods(DigestClass.class);
         cDigestClass.includeModule(mDigestInstance);
         RubyClass cDigestBase = mDigest.defineClassUnder("Base", cDigestClass, DigestBase.DIGEST_BASE_ALLOCATOR);
         cDigestBase.defineAnnotatedMethods(DigestBase.class);
     }
 
     private static MessageDigest createMessageDigest(Ruby runtime, String providerName) throws NoSuchAlgorithmException {
         if(provider != null) {
             try {
                 return MessageDigest.getInstance(providerName, provider);
             } catch(NoSuchAlgorithmException e) {
                 // bouncy castle doesn't support algorithm
             }
         }
         // fall back to system JCA providers
         return MessageDigest.getInstance(providerName);
     }
 
     private final static byte[] digits = {
         '0', '1', '2', '3', '4', '5',
         '6', '7', '8', '9', 'a', 'b',
         'c', 'd', 'e', 'f', 'g', 'h',
         'i', 'j', 'k', 'l', 'm', 'n',
         'o', 'p', 'q', 'r', 's', 't',
         'u', 'v', 'w', 'x', 'y', 'z'
     };
 
     private static ByteList toHex(byte[] val) {
         ByteList byteList = new ByteList(val.length * 2);
         for (int i = 0, j = val.length; i < j; i++) {
             int b = val[i] & 0xFF;
             byteList.append(digits[b >> 4]);
             byteList.append(digits[b & 0xF]);
         }
         return byteList;
     }
 
     private static IRubyObject toHexString(Ruby runtime, byte[] val) {
         return RubyString.newStringNoCopy(runtime, ByteList.plain(toHex(val)));
     }
 
     @JRubyMethod(name = "hexencode", required = 1, meta = true)
     public static IRubyObject s_hexencode(IRubyObject recv, IRubyObject arg) {
         return toHexString(recv.getRuntime(), arg.convertToString().getBytes());
     }
 
     @JRubyMethod(name = "const_missing", required = 1, module = true)
     public static IRubyObject const_missing(ThreadContext ctx, IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = ctx.getRuntime();
         String sym = ((RubySymbol)symbol).asJavaString();
         String libName;
         if("SHA256".equals(sym) || "SHA384".equals(sym) || "SHA512".equals(sym)) {
             libName = "digest/sha2.jar";
         }
         else {
             libName = "digest/" + sym.toLowerCase();
         }
 
         runtime.getLoadService().require(libName);
         RubyModule digest = runtime.getModule("Digest");
         if(!digest.hasConstant(sym)) {
             throw runtime.newNameError("unitialized constant Digest::" + sym, "Digest::" + sym);
         }
         return digest.getConstant(sym);
     }
 
 
     private static class Metadata {
 
         private final String name;
         private final int blockLength;
 
         Metadata(String name, int blockLength) {
             this.name = name;
             this.blockLength = blockLength;
         }
 
         String getName() {
             return name;
         }
 
         int getBlockLength() {
             return blockLength;
         }
     }
 
 
     @JRubyClass(name="Digest::MD5", parent="Digest::Base")
     public static class MD5 {}
     @JRubyClass(name="Digest::RMD160", parent="Digest::Base")
     public static class RMD160 {}
     @JRubyClass(name="Digest::SHA1", parent="Digest::Base")
     public static class SHA1 {}
     @JRubyClass(name="Digest::SHA256", parent="Digest::Base")
     public static class SHA256 {}
     @JRubyClass(name="Digest::SHA384", parent="Digest::Base")
     public static class SHA384 {}
     @JRubyClass(name="Digest::SHA512", parent="Digest::Base")
     public static class SHA512 {}
 
     public static void createDigestMD5(Ruby runtime) {
-        runtime.getLoadService().require("digest.so");
+        runtime.getLoadService().require("digest");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_MD5 = mDigest.defineClassUnder("MD5",cDigestBase,cDigestBase.getAllocator());
         cDigest_MD5.setInternalVariable("metadata", new Metadata("MD5", 64));
     }
 
     public static void createDigestRMD160(Ruby runtime) {
-        runtime.getLoadService().require("digest.so");
+        runtime.getLoadService().require("digest");
         if(provider == null) {
             throw runtime.newLoadError("RMD160 not supported without BouncyCastle");
         }
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_RMD160 = mDigest.defineClassUnder("RMD160",cDigestBase,cDigestBase.getAllocator());
         cDigest_RMD160.setInternalVariable("metadata", new Metadata("RIPEMD160", 64));
     }
 
     public static void createDigestSHA1(Ruby runtime) {
-        runtime.getLoadService().require("digest.so");
+        runtime.getLoadService().require("digest");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA1 = mDigest.defineClassUnder("SHA1",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA1.setInternalVariable("metadata", new Metadata("SHA1", 64));
     }
 
     public static void createDigestSHA2(Ruby runtime) {
-        runtime.getLoadService().require("digest.so");
+        runtime.getLoadService().require("digest");
         try {
             createMessageDigest(runtime, "SHA-256");
         } catch(NoSuchAlgorithmException e) {
             throw runtime.newLoadError("SHA2 not supported");
         }
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA2_256 = mDigest.defineClassUnder("SHA256",cDigestBase,cDigestBase.getAllocator());
         Metadata sha256Metadata = new Metadata("SHA-256", 64);
         cDigest_SHA2_256.setInternalVariable("metadata", sha256Metadata);
         RubyClass cDigest_SHA2_384 = mDigest.defineClassUnder("SHA384",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_384.setInternalVariable("metadata", new Metadata("SHA-384", 128));
         RubyClass cDigest_SHA2_512 = mDigest.defineClassUnder("SHA512",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_512.setInternalVariable("metadata", new Metadata("SHA-512", 128));
     }
 
     @JRubyModule(name = "Digest::Instance")
     public static class DigestInstance {
 
         private static IRubyObject throwUnimplError(IRubyObject self, String name) {
             throw self.getRuntime().newRuntimeError(String.format("%s does not implement %s()", self.getMetaClass().getRealClass().getName(), name));
         }
 
         /* instance methods that should be overridden */
         @JRubyMethod(name = {"update", "<<"}, required = 1)
         public static IRubyObject update(ThreadContext ctx, IRubyObject self, IRubyObject arg) {
             return throwUnimplError(self, "update");
         }
 
         @JRubyMethod()
         public static IRubyObject finish(ThreadContext ctx, IRubyObject self) {
             return throwUnimplError(self, "finish");
         }
 
         @JRubyMethod()
         public static IRubyObject reset(ThreadContext ctx, IRubyObject self) {
             return throwUnimplError(self, "reset");
         }
 
         @JRubyMethod()
         public static IRubyObject digest_length(ThreadContext ctx, IRubyObject self) {
             return digest(ctx, self, null).convertToString().length();
         }
 
         @JRubyMethod()
         public static IRubyObject block_length(ThreadContext ctx, IRubyObject self) {
             return throwUnimplError(self, "block_length");
         }
 
         /* instance methods that may be overridden */
         @JRubyMethod(name = "==", required = 1)
         public static IRubyObject op_equal(ThreadContext ctx, IRubyObject self, IRubyObject oth) {
             RubyString str1, str2;
             RubyModule instance = (RubyModule)self.getRuntime().getModule("Digest").getConstantAt("Instance");
             if (oth.getMetaClass().getRealClass().hasModuleInHierarchy(instance)) {
                 str1 = digest(ctx, self, null).convertToString();
                 str2 = digest(ctx, oth, null).convertToString();
             } else {
                 str1 = to_s(ctx, self).convertToString();
                 str2 = oth.convertToString();
             }
             boolean ret = str1.length().eql(str2.length()) && (str1.eql(str2));
             return ret ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
         }
 
         @JRubyMethod()
         public static IRubyObject inspect(ThreadContext ctx, IRubyObject self) {
             return RubyString.newStringNoCopy(self.getRuntime(), ByteList.plain("#<" + self.getMetaClass().getRealClass().getName() + ": " + hexdigest(ctx, self, null) + ">"));
         }
 
         /* instance methods that need not usually be overridden */
         @JRubyMethod(name = "new")
         public static IRubyObject newObject(ThreadContext ctx, IRubyObject self) {
             return self.rbClone().callMethod(ctx, "reset");
         }
 
         @JRubyMethod(optional = 1)
         public static IRubyObject digest(ThreadContext ctx, IRubyObject self, IRubyObject[] args) {
             IRubyObject value = null;
             if (args != null && args.length > 0) {
                 self.callMethod(ctx, "reset");
                 self.callMethod(ctx, "update", args[0]);
                 value = self.callMethod(ctx, "finish");
                 self.callMethod(ctx, "reset");
             } else {
                 IRubyObject clone = self.rbClone();
                 value = clone.callMethod(ctx, "finish");
                 clone.callMethod(ctx, "reset");
             }
             return value;
         }
 
         @JRubyMethod(name = "digest!")
         public static IRubyObject digest_bang(ThreadContext ctx, IRubyObject self) {
             IRubyObject value = self.callMethod(ctx, "finish");
             self.callMethod(ctx, "reset");
             return value;
         }
 
         @JRubyMethod(optional = 1)
         public static IRubyObject hexdigest(ThreadContext ctx, IRubyObject self, IRubyObject[] args) {
             return toHexString(ctx.getRuntime(), digest(ctx, self, args).convertToString().getBytes());
         }
 
         @JRubyMethod(name = "hexdigest!")
         public static IRubyObject hexdigest_bang(ThreadContext ctx, IRubyObject self) {
             return toHexString(ctx.getRuntime(), digest_bang(ctx, self).convertToString().getBytes());
         }
 
         @JRubyMethod()
         public static IRubyObject to_s(ThreadContext ctx, IRubyObject self) {
             return self.callMethod(ctx, "hexdigest");
         }
 
         @JRubyMethod(name = {"length", "size"})
         public static IRubyObject length(ThreadContext ctx, IRubyObject self) {
             return self.callMethod(ctx, "digest_length");
         }
-
-        @JRubyMethod
-        public static IRubyObject file(ThreadContext ctx, IRubyObject self, IRubyObject filename) {
-            Ruby runtime = self.getRuntime();
-            RubyString filenameStr = filename.convertToString();
-            
-            if (RubyFileTest.directory_p(runtime, filenameStr).isTrue()) {
-                if (Platform.IS_WINDOWS) {
-                    throw runtime.newErrnoEACCESError(filenameStr.asJavaString());
-                } else {
-                    throw runtime.newErrnoEISDirError(filenameStr.asJavaString());
-                }
-            }
-
-            IRubyObject io = RuntimeHelpers.invoke(ctx, runtime.getFile(),
-                    "open", filenameStr, runtime.newString("rb"));
-
-            try {
-                RubyString buf = runtime.newString();
-                final RubyFixnum bufSize = runtime.newFixnum(16 * 1024);
-                while(!RuntimeHelpers.invoke(ctx, io, "read", bufSize, buf).isNil()) {
-                    self.callMethod(ctx, "update", buf);
-                }
-            } finally {
-                io.callMethod(ctx, "close");
-            }
-
-            return self;
-        }
     }
 
 
     @JRubyClass(name="Digest::Class")
     public static class DigestClass extends RubyObject {
         protected static final ObjectAllocator DIGEST_CLASS_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new DigestClass(runtime, klass);
             }
         };
 
         public DigestClass(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
         
         @JRubyMethod(name = "digest", required = 1, rest = true, meta = true)
         public static IRubyObject s_digest(ThreadContext ctx, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
             Ruby runtime = recv.getRuntime();
             if (args.length < 1) {
                 throw runtime.newArgumentError("no data given");
             }
             RubyString str = args[0].convertToString();
             IRubyObject[] newArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, newArgs, 0, args.length - 1);
             IRubyObject obj = ((RubyClass)recv).newInstance(ctx, newArgs, Block.NULL_BLOCK);
             return obj.callMethod(ctx, "digest", str);
         }
 
         @JRubyMethod(name = "hexdigest", required = 1, optional = 1, meta = true)
         public static IRubyObject s_hexdigest(ThreadContext ctx, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
             Ruby runtime = recv.getRuntime();
             byte[] digest = recv.callMethod(ctx, "digest", args, Block.NULL_BLOCK).convertToString().getBytes();
             return RubyDigest.toHexString(runtime, digest);
         }
-
-        @JRubyMethod(meta = true)
-        public static IRubyObject file(ThreadContext ctx, IRubyObject recv, IRubyObject filename) {
-            IRubyObject obj = ((RubyClass)recv).newInstance(ctx, new IRubyObject[0], Block.NULL_BLOCK);
-            return obj.callMethod(ctx, "file", filename);
-        }
     }
 
 
     @JRubyClass(name="Digest::Base")
     public static class DigestBase extends RubyObject {
         protected static final ObjectAllocator DIGEST_BASE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new DigestBase(runtime, klass);
             }
         };
 
         private MessageDigest algo;
         private int blockLength = 0;
 
         public DigestBase(Ruby runtime, RubyClass type) {
             super(runtime,type);
 
             if(type == runtime.getModule("Digest").getClass("Base")) {
                 throw runtime.newNotImplementedError("Digest::Base is an abstract class");
             }
 
             Metadata metadata = getMetadata(type);
             if(metadata == null) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
             
             try {
                 setAlgorithm(metadata);
             } catch(NoSuchAlgorithmException e) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
 
         }
 
         // if subclass extends particular digest we need to walk to find it...we should rearchitect digest to avoid walking type systems
         private Metadata getMetadata(RubyModule type) {
             for (RubyModule current = type; current != null; current = current.getSuperClass()) {
                 Metadata metadata = (Metadata) current.getInternalVariable("metadata");
 
                 if (metadata != null) return metadata;
             }
 
             return null;
         }
 
         @JRubyMethod(required = 1)
         public IRubyObject initialize_copy(IRubyObject obj) {
             if(this == obj) {
                 return this;
             }
             ((RubyObject)obj).checkFrozen();
 
             String name = ((DigestBase)obj).algo.getAlgorithm();
             try {
                 algo = (MessageDigest)((DigestBase)obj).algo.clone();
             } catch(CloneNotSupportedException e) {
                 throw getRuntime().newTypeError("Could not initialize copy of digest (" + name + ")");
             }
             return this;
         }
 
         @JRubyMethod(name = {"update", "<<"}, required = 1)
         public IRubyObject update(IRubyObject obj) {
             ByteList bytes = obj.convertToString().getByteList();
             algo.update(bytes.getUnsafeBytes(), bytes.getBegin(), bytes.getRealSize());
             return this;
         }
 
         @JRubyMethod()
         public IRubyObject finish() {
             IRubyObject digest = RubyString.newStringNoCopy(getRuntime(), algo.digest());
             algo.reset();
             return digest;
         }
         
         @JRubyMethod()
         public IRubyObject digest_length() {
             return RubyFixnum.newFixnum(getRuntime(), algo.getDigestLength());
         }
 
         @JRubyMethod()
         public IRubyObject block_length() {
             if (blockLength == 0) {
                 throw getRuntime().newRuntimeError(
                         this.getMetaClass() + " doesn't implement block_length()");
             }
             return RubyFixnum.newFixnum(getRuntime(), blockLength);
         }
 
         @JRubyMethod()
         public IRubyObject reset() {
             algo.reset();
             return this;
         }
 
         private void setAlgorithm(Metadata metadata) throws NoSuchAlgorithmException {
            this.algo = createMessageDigest(getRuntime(), metadata.getName());
            this.blockLength = metadata.getBlockLength();
         }
     }
 }// RubyDigest
