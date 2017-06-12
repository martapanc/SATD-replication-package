diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index fcacbac828..3dc2798225 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -323,3526 +323,3536 @@ public final class Ruby {
     /**
      * Parse and execute the specified script 
      * This differs from the other methods in that it accepts a string-based script and
      * parses and runs it as though it were loaded at a command-line. This is the preferred
      * way to start up a new script when calling directly into the Ruby object (which is
      * generally *dis*couraged.
      * 
      * @param script The contents of the script to run as a normal, root script
      * @return The last value of the script
      */
     public IRubyObject executeScript(String script, String filename) {
         byte[] bytes = script.getBytes();
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(node.getPosition());
             return runNormally(node);
         } finally {
             context.setFileAndLine(oldFile, oldLine);
         }
     }
     
     /**
      * Run the script contained in the specified input stream, using the
      * specified filename as the name of the script being executed. The stream
      * will be read fully before being parsed and executed. The given filename
      * will be used for the ruby $PROGRAM_NAME and $0 global variables in this
      * runtime.
      * 
      * This method is intended to be called once per runtime, generally from
      * Main or from main-like top-level entry points.
      * 
      * As part of executing the script loaded from the input stream, various
      * RubyInstanceConfig properties will be used to determine whether to
      * compile the script before execution or run with various wrappers (for
      * looping, printing, and so on, see jruby -help).
      * 
      * @param inputStream The InputStream from which to read the script contents
      * @param filename The filename to use when parsing, and for $PROGRAM_NAME
      * and $0 ruby global variables.
      */
     public void runFromMain(InputStream inputStream, String filename) {
         IAccessor d = new ValueAccessor(newString(filename));
         getGlobalVariables().define("$PROGRAM_NAME", d);
         getGlobalVariables().define("$0", d);
 
         for (Iterator i = config.getOptionGlobals().entrySet().iterator(); i.hasNext();) {
             Map.Entry entry = (Map.Entry) i.next();
             Object value = entry.getValue();
             IRubyObject varvalue;
             if (value != null) {
                 varvalue = newString(value.toString());
             } else {
                 varvalue = getTrue();
             }
             getGlobalVariables().set("$" + entry.getKey().toString(), varvalue);
         }
 
         if (filename.endsWith(".class")) {
             // we are presumably running a precompiled class; load directly
             Script script = CompiledScriptLoader.loadScriptFromFile(this, inputStream, filename);
             if (script == null) {
                 throw new MainExitException(1, "error: .class file specified is not a compiled JRuby script");
             }
             script.setFilename(filename);
             runScript(script);
             return;
         }
         
         Node scriptNode = parseFromMain(inputStream, filename);
 
         // done with the stream, shut it down
         try {inputStream.close();} catch (IOException ioe) {}
 
         ThreadContext context = getCurrentContext();
 
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFileAndLine(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit());
             } else {
                 runNormally(scriptNode);
             }
         } finally {
             context.setFileAndLine(oldFile, oldLine);
         }
     }
 
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
             return parseFile(inputStream, filename, getCurrentContext().getCurrentScope());
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
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (compile) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
             if (forceCompile && script == null) {
                 return getNil();
             }
         }
         
         if (script != null) {
             if (config.isShowBytecode()) {
                 return nilObject;
             } else {
                 return runScript(script);
             }
         } else {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             return runInterpreter(scriptNode);
         }
     }
     
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()));
     }
     
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader) {
         return tryCompile(node, cachedClassName, classLoader, false);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
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
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compileable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compilable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: not compilable: " + e.getMessage());
                 e.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose() || config.isDebug()) {
                 System.err.println("warning: could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             } else {
                 System.err.println("warning: could not compile; pass -d or -J-Djruby.jit.logging.verbose=true for more details");
             }
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
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
 
         // Modified for JRUBY-4484
         // prepare a temporary toplevel frame during init, for consumers like TOPLEVEL_BINDING
         tc.preTopLevel(objectClass, topSelf);
 
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
 
         // Modified for JRUBY-4484
         // remove the temporary toplevel frame
         tc.postTopLevel();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.smartLoad(scriptName);
         }
 
         // Modified for JRUBY-4484
         // Construct the final toplevel frame and scope for the main thread
         tc.preTopLevel(objectClass, topSelf);
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
 
             RubyRandom.createRandomClass(this);
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
             }
+
+            mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
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
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.RubyJRuby$ExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.RubyJRuby$UtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
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
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
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
         
         String[] builtins = {"yaml", 
                              "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
                              "yaml/compat", "yaml/constants", "yaml/dbm", 
                              "yaml/emitter", "yaml/encoding", "yaml/error", 
                              "yaml/rubytypes", "yaml/store", "yaml/stream", 
                              "yaml/stringio", "yaml/tag", "yaml/types", 
                              "yaml/yamlnode", "yaml/ypath", 
                              "jsignal", "generator", "prelude"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             defineModule("Gem"); // dummy Gem module for prelude
             getLoadService().require("builtin/prelude.rb");
             getLoadService().require("builtin/gem_prelude.rb");
         }
 
         getLoadService().require("builtin/core_ext/symbol");
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
 
+    public RubyClass getMathDomainError() {
+        return mathDomainError;
+    }
+
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
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
         byte[] bytes = content.getBytes();
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, bytes, scope,
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes = content.getBytes();
 
         return parser.parse(file, bytes, scope,
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
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
 
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
                 className = "ruby.jit.FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (JITCompiler.DEBUG) {
                         System.err.println("found jitted code in classloader: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             if (script == null) {
                 Node scriptNode = parseFile(readStream, filename, null);
 
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader));
             }
             
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
                         f.finalize();
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
                         f.finalize();
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
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
         proc.initialize(getCurrentContext(), block);
 
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
         return newRaiseException(getArgumentError(), "wrong # of arguments(" + got + " for " + expected + ")");
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
                 this, getNameError(), message, name), true);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", RubyLocalJumpError.Reason.REDO, getNil()), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
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
 
+    public RaiseException newMathDomainError(String message) {
+        return newRaiseException(getMathDomainError(), "Numerical argument is out of domain - \"" + message + "\"");
+    }
+
     public RaiseException newEncodingError(String message){
         return newRaiseException(getEncodingError(), message);
     }
 
     public RaiseException newEncodingCompatibilityError(String message){
         return newRaiseException(getEncodingCompatibilityError(), message);
     }
 
     public RaiseException newConverterNotFoundError(String message) {
         return newRaiseException(getConverterNotFoundError(), message);
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
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         return re;
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
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
 
     private class WeakDescriptorReference extends WeakReference {
         private int fileno;
 
         public WeakDescriptorReference(ChannelDescriptor descriptor, ReferenceQueue queue) {
             super(descriptor, queue);
             this.fileno = descriptor.getFileno();
         }
 
         public int getFileno() {
             return fileno;
         }
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
         Integer filenoKey = descriptor.getFileno();
         retainedDescriptors.put(filenoKey, descriptor);
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor) {
         registerDescriptor(descriptor,false); // default: don't retain
     }
 
     public void unregisterDescriptor(int aFileno) {
         Integer aFilenoKey = aFileno;
         retainedDescriptors.remove(aFilenoKey);
     }
 
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return retainedDescriptors.get(aFileno);
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
 
     private static class RecursiveError extends Error {
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
 
     // The method is intentionally not public, since it typically should
     // not be used outside of the core.
     /* package-private */ void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
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
      * Get the global object used to synchronize class-hierarchy modifications like
      * cache invalidation, subclass sets, and included hierarchy sets.
      *
      * @return The object to use for locking when modifying the hierarchy
      */
     public Object getHierarchyLock() {
         return hierarchyLock;
     }
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Map<Integer, ChannelDescriptor> retainedDescriptors = new ConcurrentHashMap<Integer, ChannelDescriptor>();
 
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private static ThreadLocal<Ruby> currentRuntime = new ThreadLocal<Ruby>();
     
     private long globalState = 1;
     
     private int safeLevel = -1;
 
     // Default objects
     private IRubyObject topSelf;
     private RubyNil nilObject;
     private IRubyObject[] singleNilArray;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[2 * RubyFixnum.CACHE_OFFSET];
 
     private IRubyObject verbose;
     private boolean isVerbose, warningsEnabled;
     private IRubyObject debug;
     
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
-            regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError,
+            regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError, mathDomainError,
             encodingError, encodingCompatibilityError, converterNotFoundError, undefinedConversionError,
             invalidByteSequenceError, randomClass;
 
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
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
 
     private Encoding defaultInternalEncoding, defaultExternalEncoding;
     private EncodingService encodingService;
 
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private AtomicInteger symbolLastId = new AtomicInteger(128);
     private AtomicInteger moduleLastId = new AtomicInteger(0);
 
     private Object respondToMethod;
 
     private Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
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
     private Object hierarchyLock = new Object();
 
     // An atomic long for generating DynamicMethod serial numbers
     private AtomicLong dynamicMethodSerial = new AtomicLong(0);
 }
diff --git a/src/org/jruby/RubyMath.java b/src/org/jruby/RubyMath.java
index 0812e84014..a477ebeeb4 100644
--- a/src/org/jruby/RubyMath.java
+++ b/src/org/jruby/RubyMath.java
@@ -1,958 +1,956 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
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
 import org.jruby.anno.JRubyModule;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.TypeConverter;
 
 @JRubyModule(name="Math")
 public class RubyMath {
     /** Create the Math module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createMathModule(Ruby runtime) {
         RubyModule result = runtime.defineModule("Math");
         runtime.setMath(result);
         
         result.defineConstant("E", RubyFloat.newFloat(runtime, Math.E));
         result.defineConstant("PI", RubyFloat.newFloat(runtime, Math.PI));
         
         result.defineAnnotatedMethods(RubyMath.class);
 
         return result;
     }
     
     
-    private static void domainCheck(IRubyObject recv, double value, String msg) {  
+    private static void domainCheck(IRubyObject recv, double value, String msg) {
         if (Double.isNaN(value)) {
             throw recv.getRuntime().newErrnoEDOMError(msg);
         }
     }
 
+    private static void domainCheck19(IRubyObject recv, double value, String msg) {
+        if (Double.isNaN(value)) {
+            throw recv.getRuntime().newMathDomainError(msg);
+        }
+    }
+
     private static void zeroInLogCheck(IRubyObject recv, double value) {
         if (value == 0.0) {
             throw recv.getRuntime().newErrnoEDOMError("log");
         }
     }
     
     private static double chebylevSerie(double x, double coef[]) {
         double  b0, b1, b2, twox;
         int i;
         b1 = 0.0;
         b0 = 0.0;
         b2 = 0.0;
         twox = 2.0 * x;
         for (i = coef.length-1; i >= 0; i--) {
             b2 = b1;
             b1 = b0;
             b0 = twox * b1 - b2 + coef[i];
         }
         return 0.5*(b0 - b2);
     }
     
     private static double sign(double x, double y) {
         double abs = ((x < 0) ? -x : x);
         return (y < 0.0) ? -abs : abs;
     }
     
     private static RubyFloat needFloat(IRubyObject x) {
         if(x instanceof RubyFloat) {
             return (RubyFloat)x;
         }
 
         if(!x.getRuntime().getNumeric().isInstance(x)) {
             TypeConverter.handleUncoercibleObject(true, x, x.getRuntime().getFloat());
         }
 
         return (RubyFloat) TypeConverter.convertToType19(x, x.getRuntime().getFloat(), "to_f", true);
     }
 
     @JRubyMethod(name = "atan2", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat atan2(IRubyObject recv, IRubyObject x, IRubyObject y) {
         double valuea = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double valueb = ((RubyFloat)RubyKernel.new_float(recv,y)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(), Math.atan2(valuea, valueb));
     }
 
     @JRubyMethod(name = "atan2", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat atan219(IRubyObject recv, IRubyObject x, IRubyObject y) {
         double valuea = needFloat(x).getDoubleValue();
         double valueb = needFloat(y).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(), Math.atan2(valuea, valueb));
     }
 
     @JRubyMethod(name = "cos", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat cos(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.cos(value));
     }
 
     @JRubyMethod(name = "cos", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat cos19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.cos(value));
     }
 
     @JRubyMethod(name = "sin", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat sin(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.sin(value));
     }
 
     @JRubyMethod(name = "sin", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat sin19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.sin(value));
     }
 
     @JRubyMethod(name = "tan", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat tan(IRubyObject recv,  IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.tan(value));
     }
 
     @JRubyMethod(name = "tan", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat tan19(IRubyObject recv,  IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.tan(value));
     }
     
     @JRubyMethod(name = "asin", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat asin(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double result = Math.asin(value);
         domainCheck(recv, result, "asin");        
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     @JRubyMethod(name = "asin", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat asin19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double result = Math.asin(value);
         domainCheck(recv, result, "asin");        
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     @JRubyMethod(name = "acos", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat acos(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double result = Math.acos(value);  
         domainCheck(recv, result, "acos");
         return RubyFloat.newFloat(recv.getRuntime(), result);
     }
 
     @JRubyMethod(name = "acos", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat acos19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double result = Math.acos(value);  
         domainCheck(recv, result, "acos");
         return RubyFloat.newFloat(recv.getRuntime(), result);
     }
-
-
-
-
-
-
-
-
-
-
-
-
-
-
-
-
-
     
     @JRubyMethod(name = "atan", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat atan(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.atan(value));
     }
 
     @JRubyMethod(name = "atan", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat atan19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.atan(value));
     }
 
     @JRubyMethod(name = "cosh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat cosh(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),(Math.exp(value) + Math.exp(-value)) / 2.0);
     }    
 
     @JRubyMethod(name = "cosh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat cosh19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),(Math.exp(value) + Math.exp(-value)) / 2.0);
     }    
 
     @JRubyMethod(name = "sinh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat sinh(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),(Math.exp(value) - Math.exp(-value)) / 2.0);
     }
 
     @JRubyMethod(name = "sinh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat sinh19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),(Math.exp(value) - Math.exp(-value)) / 2.0);
     }
     
     @JRubyMethod(name = "tanh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat tanh(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(), Math.tanh(value));
     }          
 
     @JRubyMethod(name = "tanh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat tanh19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(), Math.tanh(value));
     }          
     
     @JRubyMethod(name = "acosh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat acosh(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double result;
         if (Double.isNaN(value) || value < 1) {
             result = Double.NaN;
         } else if (value < 94906265.62) {
             result = Math.log(value + Math.sqrt(value * value - 1.0));
         } else{
             result = 0.69314718055994530941723212145818 + Math.log(value);
         }
         
         domainCheck(recv, result, "acosh");
         
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     @JRubyMethod(name = "acosh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat acosh19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double result;
         if (Double.isNaN(value) || value < 1) {
             result = Double.NaN;
         } else if (value < 94906265.62) {
             result = Math.log(value + Math.sqrt(value * value - 1.0));
         } else{
             result = 0.69314718055994530941723212145818 + Math.log(value);
         }
         
         domainCheck(recv, result, "acosh");
         
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
     
     private static final double ASINH_COEF[] = {
         -.12820039911738186343372127359268e+0,
         -.58811761189951767565211757138362e-1,
         .47274654322124815640725249756029e-2,
         -.49383631626536172101360174790273e-3,
         .58506207058557412287494835259321e-4,
         -.74669983289313681354755069217188e-5,
         .10011693583558199265966192015812e-5,
         -.13903543858708333608616472258886e-6,
         .19823169483172793547317360237148e-7,
         -.28847468417848843612747272800317e-8,
         .42672965467159937953457514995907e-9,
         -.63976084654366357868752632309681e-10,
         .96991686089064704147878293131179e-11,
         -.14844276972043770830246658365696e-11,
         .22903737939027447988040184378983e-12,
         -.35588395132732645159978942651310e-13,
         .55639694080056789953374539088554e-14,
         -.87462509599624678045666593520162e-15,
         .13815248844526692155868802298129e-15,
         -.21916688282900363984955142264149e-16,
         .34904658524827565638313923706880e-17
     };      
     
     @JRubyMethod(name = "asinh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat asinh(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double  y = Math.abs(value);
         double result;
         
         if (Double.isNaN(value)) {
             result = Double.NaN;
         } else if (y <= 1.05367e-08) {
             result = value;
         } else if (y <= 1.0) {          
             result = value * (1.0 + chebylevSerie(2.0 * value * value - 1.0, ASINH_COEF));
         } else if (y < 94906265.62) {
             result = Math.log(value + Math.sqrt(value * value + 1.0));
         } else {    
             result = 0.69314718055994530941723212145818 + Math.log(y);
             if (value < 0) result *= -1;
         }
 
         return RubyFloat.newFloat(recv.getRuntime(),result);        
     }
 
     @JRubyMethod(name = "asinh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat asinh19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double  y = Math.abs(value);
         double result;
         
         if (Double.isNaN(value)) {
             result = Double.NaN;
         } else if (y <= 1.05367e-08) {
             result = value;
         } else if (y <= 1.0) {          
             result = value * (1.0 + chebylevSerie(2.0 * value * value - 1.0, ASINH_COEF));
         } else if (y < 94906265.62) {
             result = Math.log(value + Math.sqrt(value * value + 1.0));
         } else {    
             result = 0.69314718055994530941723212145818 + Math.log(y);
             if (value < 0) result *= -1;
         }
 
         return RubyFloat.newFloat(recv.getRuntime(),result);        
     }
     
     private static final double ATANH_COEF[] = {
         .9439510239319549230842892218633e-1,
         .4919843705578615947200034576668e-1,
         .2102593522455432763479327331752e-2,
         .1073554449776116584640731045276e-3,
         .5978267249293031478642787517872e-5,
         .3505062030889134845966834886200e-6,
         .2126374343765340350896219314431e-7,
         .1321694535715527192129801723055e-8,
         .8365875501178070364623604052959e-10,
         .5370503749311002163881434587772e-11,
         .3486659470157107922971245784290e-12,
         .2284549509603433015524024119722e-13,
         .1508407105944793044874229067558e-14,
         .1002418816804109126136995722837e-15,
         .6698674738165069539715526882986e-17,
         .4497954546494931083083327624533e-18
     };    
 
     @JRubyMethod(name = "atanh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat atanh(IRubyObject recv, IRubyObject x) {
-        return atanh_common(recv, x);
+        double result = atanh_common(recv, x);
+        domainCheck(recv, result, "atanh");
+        return RubyFloat.newFloat(recv.getRuntime(), result);
     }
 
     @JRubyMethod(name = "atanh", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat atanh_19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double  y = Math.abs(value);
         if (y==1.0) {
             throw recv.getRuntime().newErrnoEDOMError("atanh");
         }
-        return atanh_common(recv, x);
+        double result = atanh_common(recv, x);
+        domainCheck19(recv, result, "atanh");
+        return RubyFloat.newFloat(recv.getRuntime(), result);
     }
 
-    private static RubyFloat atanh_common(IRubyObject recv, IRubyObject x) {
+    private static double atanh_common(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double  y = Math.abs(value);
         double  result;
 
         if (Double.isNaN(value)) {
             result = Double.NaN;
         } else if (y < 1.82501e-08) {
             result = value;
         } else if (y <= 0.5) {
             result = value * (1.0 + chebylevSerie(8.0 * value * value - 1.0, ATANH_COEF));
         } else if (y < 1.0) {
             result = 0.5 * Math.log((1.0 + value) / (1.0 - value));
         } else if (y == 1.0) {
             result = value * Double.POSITIVE_INFINITY;
         } else {
             result = Double.NaN;
         }
 
-        domainCheck(recv, result, "atanh");
-        return RubyFloat.newFloat(recv.getRuntime(),result);        
+        return result;
+
     }
     
     @JRubyMethod(name = "exp", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat exp(IRubyObject recv, IRubyObject exponent) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,exponent)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.exp(value));
     }
 
     @JRubyMethod(name = "exp", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat exp19(IRubyObject recv, IRubyObject exponent) {
         double value = needFloat(exponent).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),Math.exp(value));
     }
 
     private static RubyFloat log_common(IRubyObject recv, double value, double base) {
         double result = Math.log(value)/Math.log(base);
         domainCheck(recv, result, "log");
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     /** Returns the natural logarithm of x.
      * 
      */
     @JRubyMethod(name = "log", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat log(IRubyObject recv, IRubyObject x) {
         return log_common(recv, ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue(), Math.E);
     }
 
     @JRubyMethod(name = "log", required = 1, optional = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat log_19(IRubyObject recv, IRubyObject[] args) {
         double value = needFloat(args[0]).getDoubleValue();
         double base = Math.E;
         if (args.length == 2) {
             base = needFloat(args[1]).getDoubleValue();
         }
         zeroInLogCheck(recv, value);
         return log_common(recv, value, base);
     }
 
     /** Returns the base 10 logarithm of x.
      * 
      */
     @JRubyMethod(name = "log10", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat log10(IRubyObject recv, IRubyObject x) {
         return log_common(recv, ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue(), 10);
     }
 
     @JRubyMethod(name = "log10", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat log10_19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         zeroInLogCheck(recv, value);
         return log_common(recv, value, 10);
     }
 
     /** Returns the base 2 logarithm of x.
      *
      */
     @JRubyMethod(name = "log2", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat log2(IRubyObject recv, IRubyObject x) {
         return log_common(recv, ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue(), 2);
     }
 
     @JRubyMethod(name = "log2", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat log2_19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         zeroInLogCheck(recv, value);
         return log_common(recv, value, 2);
     }
 
     @JRubyMethod(name = "sqrt", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat sqrt(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double result;
 
         if (value < 0) {
             result = Double.NaN;
         } else{
             result = Math.sqrt(value);
         }
         
         domainCheck(recv, result, "sqrt");
         return RubyFloat.newFloat(recv.getRuntime(), result);
     }
 
     @JRubyMethod(name = "sqrt", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat sqrt19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double result;
 
         if (value < 0) {
             result = Double.NaN;
         } else{
             result = Math.sqrt(value);
         }
         
-        domainCheck(recv, result, "sqrt");
+        domainCheck19(recv, result, "sqrt");
         return RubyFloat.newFloat(recv.getRuntime(), result);
     }
     
     @JRubyMethod(name = "cbrt", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat cbrt(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double result;
 
         if (value < 0) {
             result = -Math.pow(-value, 1/3.0);
         } else{
             result = Math.pow(value, 1/3.0);
         }
 
         domainCheck(recv, result, "cbrt");
         return RubyFloat.newFloat(recv.getRuntime(), result);
     }
 
     @JRubyMethod(name = "hypot", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat hypot(IRubyObject recv, IRubyObject x, IRubyObject y) {
         double valuea = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue(); 
         double valueb = ((RubyFloat)RubyKernel.new_float(recv,y)).getDoubleValue();
         double result;
         
         if (Math.abs(valuea) > Math.abs(valueb)) {
             result = valueb / valuea;
             result = Math.abs(valuea) * Math.sqrt(1 + result * result);
         } else if (valueb != 0) {
             result = valuea / valueb;
             result = Math.abs(valueb) * Math.sqrt(1 + result * result);
         } else {
             result = 0;
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }    
 
     @JRubyMethod(name = "hypot", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat hypot19(IRubyObject recv, IRubyObject x, IRubyObject y) {
         double valuea = needFloat(x).getDoubleValue(); 
         double valueb = needFloat(y).getDoubleValue();
         double result;
         
         if (Math.abs(valuea) > Math.abs(valueb)) {
             result = valueb / valuea;
             result = Math.abs(valuea) * Math.sqrt(1 + result * result);
         } else if (valueb != 0) {
             result = valuea / valueb;
             result = Math.abs(valueb) * Math.sqrt(1 + result * result);
         } else {
             result = 0;
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }    
     
     
     /*
      * x = mantissa * 2 ** exponent
      *
      * Where mantissa is in the range of [.5, 1)
      *
      */
     @JRubyMethod(name = "frexp", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyArray frexp(IRubyObject recv, IRubyObject other) {
         double mantissa = ((RubyFloat)RubyKernel.new_float(recv,other)).getDoubleValue();
         short sign = 1;
         long exponent = 0;
 
         if (!Double.isInfinite(mantissa) && mantissa != 0.0) {
             // Make mantissa same sign so we only have one code path.
             if (mantissa < 0) {
                 mantissa = -mantissa;
                 sign = -1;
             }
 
             // Increase value to hit lower range.
             for (; mantissa < 0.5; mantissa *= 2.0, exponent -=1) { }
 
             // Decrease value to hit upper range.  
             for (; mantissa >= 1.0; mantissa *= 0.5, exponent +=1) { }
         }
 	 
         return RubyArray.newArray(recv.getRuntime(), 
                                  RubyFloat.newFloat(recv.getRuntime(), sign * mantissa),
                                  RubyNumeric.int2fix(recv.getRuntime(), exponent));
     }
 
     @JRubyMethod(name = "frexp", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyArray frexp19(IRubyObject recv, IRubyObject other) {
         double mantissa = needFloat(other).getDoubleValue();
         short sign = 1;
         long exponent = 0;
 
         if (!Double.isInfinite(mantissa) && mantissa != 0.0) {
             // Make mantissa same sign so we only have one code path.
             if (mantissa < 0) {
                 mantissa = -mantissa;
                 sign = -1;
             }
 
             // Increase value to hit lower range.
             for (; mantissa < 0.5; mantissa *= 2.0, exponent -=1) { }
 
             // Decrease value to hit upper range.  
             for (; mantissa >= 1.0; mantissa *= 0.5, exponent +=1) { }
         }
 	 
         return RubyArray.newArray(recv.getRuntime(), 
                                  RubyFloat.newFloat(recv.getRuntime(), sign * mantissa),
                                  RubyNumeric.int2fix(recv.getRuntime(), exponent));
     }
 
     /*
      * r = x * 2 ** y
      */
     @JRubyMethod(name = "ldexp", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat ldexp(IRubyObject recv, IRubyObject mantissa, IRubyObject exponent) {
         double mantissaValue = ((RubyFloat)RubyKernel.new_float(recv, mantissa)).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),mantissaValue * Math.pow(2.0, RubyNumeric.num2int(exponent)));
     }
 
     @JRubyMethod(name = "ldexp", required = 2, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat ldexp19(IRubyObject recv, IRubyObject mantissa, IRubyObject exponent) {
         double mantissaValue = needFloat(mantissa).getDoubleValue();
         return RubyFloat.newFloat(recv.getRuntime(),mantissaValue * Math.pow(2.0, RubyNumeric.num2int(exponent)));
     }
 
     private static final double ERFC_COEF[] = {
          -.490461212346918080399845440334e-1,
          -.142261205103713642378247418996e0,
          .100355821875997955757546767129e-1,
          -.576876469976748476508270255092e-3,
          .274199312521960610344221607915e-4,
          -.110431755073445076041353812959e-5,
          .384887554203450369499613114982e-7,
          -.118085825338754669696317518016e-8,
          .323342158260509096464029309534e-10,
          -.799101594700454875816073747086e-12,
          .179907251139614556119672454866e-13,
          -.371863548781869263823168282095e-15,
          .710359900371425297116899083947e-17,
          -.126124551191552258324954248533e-18
     };
     
     @JRubyMethod(name = "erf", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat erf(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
 
         double  result;
         double  y = Math.abs(value);
 
         if (y <= 1.49012e-08) {
             result = 2 * value / 1.77245385090551602729816748334;
         } else if (y <= 1) {
             result = value * (1 + chebylevSerie(2 * value * value - 1, ERFC_COEF));
         } else if (y < 6.013687357) {
             result = sign(1 - erfc(recv, RubyFloat.newFloat(recv.getRuntime(),y)).getDoubleValue(), value);
         } else {
             result = sign(1, value);
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     @JRubyMethod(name = "erf", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat erf19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
 
         double  result;
         double  y = Math.abs(value);
 
         if (y <= 1.49012e-08) {
             result = 2 * value / 1.77245385090551602729816748334;
         } else if (y <= 1) {
             result = value * (1 + chebylevSerie(2 * value * value - 1, ERFC_COEF));
         } else if (y < 6.013687357) {
             result = sign(1 - erfc(recv, RubyFloat.newFloat(recv.getRuntime(),y)).getDoubleValue(), value);
         } else {
             result = sign(1, value);
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);
     }
 
     private static final double ERFC2_COEF[] = {
          -.69601346602309501127391508262e-1,
          -.411013393626208934898221208467e-1,
          .391449586668962688156114370524e-2,
          -.490639565054897916128093545077e-3,
          .715747900137703638076089414183e-4,
          -.115307163413123283380823284791e-4,
          .199467059020199763505231486771e-5,
          -.364266647159922287393611843071e-6,
          .694437261000501258993127721463e-7,
          -.137122090210436601953460514121e-7,
          .278838966100713713196386034809e-8,
          -.581416472433116155186479105032e-9,
          .123892049175275318118016881795e-9,
          -.269063914530674343239042493789e-10,
          .594261435084791098244470968384e-11,
          -.133238673575811957928775442057e-11,
          .30280468061771320171736972433e-12,
          -.696664881494103258879586758895e-13,
          .162085454105392296981289322763e-13,
          -.380993446525049199987691305773e-14,
          .904048781597883114936897101298e-15,
          -.2164006195089607347809812047e-15,
          .522210223399585498460798024417e-16,
          -.126972960236455533637241552778e-16,
          .310914550427619758383622741295e-17,
          -.766376292032038552400956671481e-18,
          .190081925136274520253692973329e-18
     };
 
     private static final double ERFCC_COEF[] = {
          .715179310202924774503697709496e-1,
          -.265324343376067157558893386681e-1,
          .171115397792085588332699194606e-2,
          -.163751663458517884163746404749e-3,
          .198712935005520364995974806758e-4,
          -.284371241276655508750175183152e-5,
          .460616130896313036969379968464e-6,
          -.822775302587920842057766536366e-7,
          .159214187277090112989358340826e-7,
          -.329507136225284321486631665072e-8,
          .72234397604005554658126115389e-9,
          -.166485581339872959344695966886e-9,
          .401039258823766482077671768814e-10,
          -.100481621442573113272170176283e-10,
          .260827591330033380859341009439e-11,
          -.699111056040402486557697812476e-12,
          .192949233326170708624205749803e-12,
          -.547013118875433106490125085271e-13,
          .158966330976269744839084032762e-13,
          -.47268939801975548392036958429e-14,
          .14358733767849847867287399784e-14,
          -.444951056181735839417250062829e-15,
          .140481088476823343737305537466e-15,
          -.451381838776421089625963281623e-16,
          .147452154104513307787018713262e-16,
          -.489262140694577615436841552532e-17,
          .164761214141064673895301522827e-17,
          -.562681717632940809299928521323e-18,
          .194744338223207851429197867821e-18
     };
         
     @JRubyMethod(name = "erfc", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyFloat erfc(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat)RubyKernel.new_float(recv,x)).getDoubleValue();
         double  result;
         double  y = Math.abs(value);
 
         if (value <= -6.013687357) {
             result = 2;
         } else if (y < 1.49012e-08) {
             result = 1 - 2 * value / 1.77245385090551602729816748334;
         } else {
             double ysq = y*y;
             if (y < 1) {
                 result = 1 - value * (1 + chebylevSerie(2 * ysq - 1, ERFC_COEF));
             } else if (y <= 4.0) {
                 result = Math.exp(-ysq)/y*(0.5+chebylevSerie((8.0 / ysq - 5.0) / 3.0, ERFC2_COEF));
                 if (value < 0) result = 2.0 - result;
                 if (value < 0) result = 2.0 - result;
                 if (value < 0) result = 2.0 - result;
             } else {
                 result = Math.exp(-ysq) / y * (0.5 + chebylevSerie(8.0 / ysq - 1, ERFCC_COEF));
                 if (value < 0) result = 2.0 - result;
             }
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);        
     }
 
     @JRubyMethod(name = "erfc", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat erfc19(IRubyObject recv, IRubyObject x) {
         double value = needFloat(x).getDoubleValue();
         double  result;
         double  y = Math.abs(value);
 
         if (value <= -6.013687357) {
             result = 2;
         } else if (y < 1.49012e-08) {
             result = 1 - 2 * value / 1.77245385090551602729816748334;
         } else {
             double ysq = y*y;
             if (y < 1) {
                 result = 1 - value * (1 + chebylevSerie(2 * ysq - 1, ERFC_COEF));
             } else if (y <= 4.0) {
                 result = Math.exp(-ysq)/y*(0.5+chebylevSerie((8.0 / ysq - 5.0) / 3.0, ERFC2_COEF));
                 if (value < 0) result = 2.0 - result;
                 if (value < 0) result = 2.0 - result;
                 if (value < 0) result = 2.0 - result;
             } else {
                 result = Math.exp(-ysq) / y * (0.5 + chebylevSerie(8.0 / ysq - 1, ERFCC_COEF));
                 if (value < 0) result = 2.0 - result;
             }
         }
         return RubyFloat.newFloat(recv.getRuntime(),result);        
     }
 
     private static final double FACTORIAL[] = {
         /*  0! */ 1.0,
         /*  1! */ 1.0,
         /*  2! */ 2.0,
         /*  3! */ 6.0,
         /*  4! */ 24.0,
         /*  5! */ 120.0,
         /*  6! */ 720.0,
         /*  7! */ 5040.0,
         /*  8! */ 40320.0,
         /*  9! */ 362880.0,
         /* 10! */ 3628800.0,
         /* 11! */ 39916800.0,
         /* 12! */ 479001600.0,
         /* 13! */ 6227020800.0,
         /* 14! */ 87178291200.0,
         /* 15! */ 1307674368000.0,
         /* 16! */ 20922789888000.0,
         /* 17! */ 355687428096000.0,
         /* 18! */ 6402373705728000.0,
         /* 19! */ 121645100408832000.0,
         /* 20! */ 2432902008176640000.0,
         /* 21! */ 51090942171709440000.0,
         /* 22! */ 1124000727777607680000.0
     };
 
     private static final double NEMES_GAMMA_COEFF[] = {
         1.00000000000000000000000000000000000,
         0                                    ,
         0.08333333333333333333333333333333333,
         0                                    ,
         0.00069444444444444444444444444444444,
         0                                    ,
         0.00065861992945326278659611992945326,
         0                                    ,
        -0.00053287817827748383303938859494415,
         0                                    ,
         0.00079278588700608376534302460228386,
         0                                    ,
        -0.00184758189322033028400606295961969,
         0                                    ,
         0.00625067824784941846328836824623616,
         0                                    ,
        -0.02901710246301150993444701506844402,
         0                                    ,
         0.17718457242491308890302832366796470,
         0                                    ,
        -1.37747681703993534399676348903067470
     };
 
 
     /**
      * Based on Gerg&#337; Nemes's Gamma Function approximation formula, we compute
      * approximate value of Gamma function of x.
      * @param recv Math module
      * @param x a real number
      * @return &Gamma;(x) for real number x
      * @see <a href="http://www.ebyte.it/library/downloads/2008_MTH_Nemes_GammaApproximationUpdate.pdf">
      * New asymptotic expansion for the &Gamma;(x) function</a>
      */
 
     @JRubyMethod(name = "gamma", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyFloat gamma(IRubyObject recv, IRubyObject x) {
         double value = ((RubyFloat) RubyKernel.new_float(recv, x)).getDoubleValue();
         double result = nemes_gamma(value);
         /* note nemes_gamma can return Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY
          * when value is an integer less than 1.
          * We treat 0 as a special case to avoid Domain error.
          */
         if (Double.isInfinite(result)) {
             if (value < 0) {
                 result = Double.NaN;
             } else {
                 result = Double.POSITIVE_INFINITY;
             }
         }
 
         if (Double.isNaN(value)) {
             return RubyFloat.newFloat(recv.getRuntime(), Double.NaN);
         }
 
-        domainCheck(recv, result, "gamma");
+        domainCheck19(recv, result, "gamma");
         return RubyFloat.newFloat(recv.getRuntime(), result);
 
     }
 
     /**
      * Based on Gerg&#337; Nemes's Gamma Function approximation formula, we compute
      * Log Gamma function for real number x.
      * @param recv Math module
      * @param x a real number
      * @return 2-element array [ln(&Gamma;(x)), sgn] for real number x,
      *  where sgn is the sign of &Gamma;(x) when exponentiated
      * @see #gamma(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject)
      */
 
     @JRubyMethod(name = "lgamma", required = 1, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyArray lgamma(IRubyObject recv, IRubyObject x) {
         Ruby runtime      = recv.getRuntime();
         double value      = RubyKernel.new_float(recv, x).getDoubleValue();
+        // JRUBY-4653: Could this error checking done more elegantly?
+        if (value < 0 && Double.isInfinite(value)) {
+            throw recv.getRuntime().newMathDomainError("lgamma");
+        }
+
         NemesLogGamma l   = new NemesLogGamma(value);
         IRubyObject[] ary = new IRubyObject[2];
         ary[0] = RubyFloat.newFloat(runtime, l.value);
         ary[1] = RubyInteger.int2fix(runtime, (int) l.sign);
 
         return RubyArray.newArray(recv.getRuntime(), ary);
     }
 
     private static double nemes_gamma(double x) {
         double int_part = (int) x;
 
         if ((x - int_part) == 0.0 && 0 < int_part && int_part <= FACTORIAL.length) {
             return FACTORIAL[(int) int_part - 1];
         }
         NemesLogGamma l = new NemesLogGamma(x);
         return l.sign * Math.exp(l.value);
     }
 
     /**
      * Inner class to help with &Gamma; functions
      */
     private static class NemesLogGamma {
         double value;
         double sign = 1;
 
         private NemesLogGamma(double x) {
             if (Double.isInfinite(x)) {
                 value = Double.POSITIVE_INFINITY;
                 return;
             }
 
             if (Double.isNaN(x)) {
                 value = Double.NaN;
                 return;
             }
 
             double int_part = (int) x;
             sign = (int_part % 2 == 0 && (x - int_part) != 0.0 && (x < 0)) ? -1 : 1;
             if ((x - int_part) == 0.0 && 0 < int_part && int_part <= FACTORIAL.length) {
                 value = Math.log(FACTORIAL[(int) int_part - 1]);
             }
             else if (x < 10) {
                 double rising_factorial = 1;
                 for (int i = 0; i < (int) Math.abs(x) - int_part + 10; i++) {
                     rising_factorial *= (x + i);
                 }
                 NemesLogGamma l = new NemesLogGamma(x + (int) Math.abs(x) - int_part + 10);
                 value = l.value - Math.log(Math.abs(rising_factorial));
             } else {
                 double temp = 0.0;
                 for (int i = 0; i < NEMES_GAMMA_COEFF.length; i++) {
                     temp += NEMES_GAMMA_COEFF[i] * 1.0 / Math.pow(x, i);
                 }
 
                 value = x * (Math.log(x) - 1 + Math.log(temp)) +
                         (Math.log(2) + Math.log(Math.PI) - Math.log(x)) / 2.0;
             }
         }
     }
 }
