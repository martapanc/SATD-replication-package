diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 97c7cf33e3..1fb6df5a5b 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -105,2001 +105,2001 @@ import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KCode;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.collections.WeakHashSet;
 import org.jruby.util.io.ChannelDescriptor;
 
 /**
  * The Ruby object represents the top-level of a JRuby "instance" in a given VM.
  * JRuby supports spawning multiple instances in the same JVM. Generally, objects
  * created under these instances are tied to a given runtime, for such details
  * as identity and type, because multiple Ruby instances means there are
  * multiple instances of each class. This means that in multi-runtime mode
  * (or really, multi-VM mode, where each JRuby instance is a ruby "VM"), objects
  * generally can't be transported across runtimes without marshaling.
  * 
  * This class roots everything that makes the JRuby runtime function, and
  * provides a number of utility methods for constructing global types and
  * accessing global runtime structures.
  */
 public final class Ruby {
     /**
      * Returns a new instance of the JRuby runtime configured with defaults.
      *
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured as specified.
      *
      * @param config The instance configuration
      * @return The JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         return ruby;
     }
 
     /**
      * Returns a new instance of the JRuby runtime configured with the given
      * input, output and error streams and otherwise default configuration
      * (except where specified system properties alter defaults).
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      * @see org.jruby.RubyInstanceConfig
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
     
     /**
      * Create and initialize a new JRuby runtime. The properties of the
      * specified RubyInstanceConfig will be used to determine various JRuby
      * runtime characteristics.
      * 
      * @param config The configuration to use for the new instance
      * @see org.jruby.RubyInstanceConfig
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.threadService      = new ThreadService(this);
         if(config.isSamplingEnabled()) {
             org.jruby.util.SimpleSampler.registerThreadContext(threadService.getCurrentContext());
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();
         this.kcode              = config.getKCode();
         this.beanManager        = new BeanManager(this, config.isManagementEnabled());
         this.jitCompiler        = new JITCompiler(this);
         this.parserStats        = new ParserStats(this);
         
         this.beanManager.register(new Config(this));
         this.beanManager.register(parserStats);
         this.beanManager.register(new ClassCache(this));
         
         this.cacheMap = new CacheMap(this);
     }
     
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns the result (generally the last value calculated).
      * This version goes straight into the interpreter, bypassing compilation
      * and runtime preparation typical to normal script runs.
      * 
      * @param script The scriptlet to run
      * @returns The result of the eval
      */
     public IRubyObject evalScriptlet(String script) {
         ThreadContext context = getCurrentContext();
         DynamicScope currentScope = context.getCurrentScope();
         ManyVarsDynamicScope newScope = new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope()), currentScope);
         Node node = parseEval(script, "<script>", newScope, 0);
         
         try {
             context.preEvalScriptlet(newScope);
             return node.interpret(this, context, context.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         } finally {
             context.postEvalScriptlet();
         }
     }
     
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
         byte[] bytes;
         
         try {
             bytes = script.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = script.getBytes();
         }
 
         Node node = parseInline(new ByteArrayInputStream(bytes), filename, null);
         ThreadContext context = getCurrentContext();
         
         String oldFile = context.getFile();
         int oldLine = context.getLine();
         try {
             context.setFile(node.getPosition().getFile());
             context.setLine(node.getPosition().getStartLine());
             return runNormally(node, false);
         } finally {
             context.setFile(oldFile);
             context.setLine(oldLine);
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
 
         
         if(config.isYARVEnabled()) {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             new YARVCompiledRunner(this, inputStream, filename).run();
         } else if(config.isRubiniusEnabled()) {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             new RubiniusRunner(this, inputStream, filename).run();
         } else {
             Node scriptNode = parseFromMain(inputStream, filename);
             ThreadContext context = getCurrentContext();
 
             String oldFile = context.getFile();
             int oldLine = context.getLine();
             try {
                 context.setFile(scriptNode.getPosition().getFile());
                 context.setLine(scriptNode.getPosition().getStartLine());
 
                 if (config.isAssumePrinting() || config.isAssumeLoop()) {
                     runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                             config.isSplit(), config.isYARVCompileEnabled());
                 } else {
                     runNormally(scriptNode, config.isYARVCompileEnabled());
                 }
             } finally {
                 context.setFile(oldFile);
                 context.setLine(oldLine);
             }
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
      * @param yarvCompile Whether to compile the target script to YARV (Ruby 1.9)
      * bytecode before executing.
      * @return The result of executing the specified script
      */
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean yarvCompile) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile || !yarvCompile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
         
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
                         runScript(script);
                     } else if (runner != null) {
                         runYarv(runner);
                     } else {
                         runInterpreter(scriptNode);
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
         
         return getNil();
     }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * @param yarvCompile Whether to compile the script to YARV (Ruby 1.9)
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode, boolean yarvCompile) {
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         } else if (compile) {
             script = tryCompile(scriptNode);
             if (forceCompile && script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
                 return getNil();
             }
         }
         
         if (script != null) {
             if (config.isShowBytecode()) {
                 return nilObject;
             } else {
                 return runScript(script);
             }
         } else if (runner != null) {
             return runYarv(runner);
         } else {
             if (config.isShowBytecode()) System.err.print("error: bytecode printing only works with JVM bytecode");
             return runInterpreter(scriptNode);
         }
     }
     
     private Script tryCompile(Node node) {
         return tryCompile(node, new JRubyClassLoader(getJRubyClassLoader()));
     }
     
     private Script tryCompile(Node node, JRubyClassLoader classLoader) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
             StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
             ASTCompiler compiler = new ASTCompiler();
             if (config.isShowBytecode()) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
                 nce.printStackTrace();
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
                 e.printStackTrace();
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("could not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                 t.printStackTrace();
             }
         }
         
         return script;
     }
     
     private YARVCompiledRunner tryCompileYarv(Node node) {
         try {
             StandardYARVCompiler compiler = new StandardYARVCompiler(this);
             ASTCompiler.getYARVCompiler().compile(node, compiler);
             org.jruby.lexer.yacc.ISourcePosition p = node.getPosition();
             if(p == null && node instanceof org.jruby.ast.RootNode) {
                 p = ((org.jruby.ast.RootNode)node).getBodyNode().getPosition();
             }
             return new YARVCompiledRunner(this,compiler.getInstructionSequence("<main>",p.getFile(),"toplevel"));
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException.ReturnJump rj) {
             return null;
         }
     }
     
     private IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runYarv(YARVCompiledRunner runner) {
         try {
             return runner.run();
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         assert scriptNode != null : "scriptNode is not null";
         
         try {
             return scriptNode.interpret(this, context, getTopSelf(), Block.NULL_BLOCK);
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
      * A ThreadFactory for when we're using pooled threads; we want to create
      * the threads with daemon = true so they don't keep us from shutting down.
      */
     public static class DaemonThreadFactory implements ThreadFactory {
         public Thread newThread(Runnable runnable) {
             Thread thread = new Thread(runnable);
             thread.setDaemon(true);
             
             return thread;
         }
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
             Executors.newCachedThreadPool();
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot(tc);
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
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
 
     private void initRoot(ThreadContext context) {
         // Bootstrap the top of the hierarchy
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
         
         // set constants now that they're initialized
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this);
         dummyClass.freeze(context);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
     }
 
     private void initCore() {
         // Pre-create all the core classes potentially referenced during startup
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
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (config.getCompatVersion() == CompatVersion.RUBY1_8) {
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
 
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
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
         if (profile.allowClass("Data")) {
-            defineClass("Data", objectClass, objectClass.getAllocator());
+            defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
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
 
             Field[] fields = IErrno.class.getFields();
 
             for (int i = 0; i < fields.length; i++) {
                 try {
                     createSysErr(fields[i].getInt(IErrno.class), fields[i].getName());
                 } catch (IllegalAccessException e) {
                     throw new RuntimeException("Someone defined a non-public constant in IErrno.java", e);
                 }
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
         addLazyBuiltin("jruby/core_ext.rb", "jruby/ext", "org.jruby.RubyJRuby$CoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.RubyJRuby$TypeLibrary");
         addLazyBuiltin("iconv.so", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.so", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.so", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.so", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.so", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("yaml_internal.rb", "yaml_internal", "org.jruby.libraries.YamlLibrary");
         addLazyBuiltin("enumerator.so", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("generator_internal.rb", "generator_internal", "org.jruby.ext.Generator$Service");
         addLazyBuiltin("readline.so", "readline", "org.jruby.ext.Readline$Service");
         addLazyBuiltin("thread.so", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.so", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.so", "digest/md5", "org.jruby.libraries.DigestLibrary$MD5");
         addLazyBuiltin("digest/rmd160.so", "digest/rmd160", "org.jruby.libraries.DigestLibrary$RMD160");
         addLazyBuiltin("digest/sha1.so", "digest/sha1", "org.jruby.libraries.DigestLibrary$SHA1");
         addLazyBuiltin("digest/sha2.so", "digest/sha2", "org.jruby.libraries.DigestLibrary$SHA2");
         addLazyBuiltin("bigdecimal.so", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.so", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.so", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRef$WeakRefLibrary");
         addLazyBuiltin("socket.so", "socket", "org.jruby.ext.socket.RubySocket$Service");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi.so", "ffi", "org.jruby.ext.ffi.Factory$Service");
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             addLazyBuiltin("fiber.so", "fiber", "org.jruby.libraries.FiberLibrary");
         }
         
         addBuiltinIfAllowed("openssl.so", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
         
         String[] builtins = {"fcntl", "yaml", "yaml/syck", "jsignal" };
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
 
         getLoadService().require("builtin/core_ext/symbol");
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
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
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     public Object getObjectToYamlMethod() {
         return objectToYamlMethod;
     }
 
     void setObjectToYamlMethod(Object otym) {
         this.objectToYamlMethod = otym;
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
 
     public RubyClass getString() {
         return stringClass;
     }    
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
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
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, true));
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
                 new ParserConfiguration(lineNumber, false));
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
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
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
diff --git a/src/org/jruby/RubyException.java b/src/org/jruby/RubyException.java
index edaa393339..fde696ad95 100644
--- a/src/org/jruby/RubyException.java
+++ b/src/org/jruby/RubyException.java
@@ -1,299 +1,297 @@
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
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sf.net>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 import java.io.PrintStream;
 import java.util.List;
+
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
-
 import org.jruby.runtime.Block;
-import org.jruby.runtime.Frame;
-import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Exception")
 public class RubyException extends RubyObject {
     private StackTraceElement[] backtraceFrames;
     private StackTraceElement[] javaStackTrace;
     private IRubyObject backtrace;
     public IRubyObject message;
     public static final int TRACE_HEAD = 8;
     public static final int TRACE_TAIL = 4;
     public static final int TRACE_MAX = TRACE_HEAD + TRACE_TAIL + 6;
 
     protected RubyException(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, null);
     }
 
     public RubyException(Ruby runtime, RubyClass rubyClass, String message) {
         super(runtime, rubyClass);
         
         this.message = message == null ? runtime.getNil() : runtime.newString(message);
     }
     
     private static ObjectAllocator EXCEPTION_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyException instance = new RubyException(runtime, klass);
             
             // for future compatibility as constructors move toward not accepting metaclass?
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     private static final ObjectMarshal EXCEPTION_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyException exc = (RubyException)obj;
             
             marshalStream.registerLinkTarget(exc);
             List<Variable<IRubyObject>> attrs = exc.getVariableList();
             attrs.add(new VariableEntry<IRubyObject>(
                     "mesg", exc.message == null ? runtime.getNil() : exc.message));
             attrs.add(new VariableEntry<IRubyObject>("bt", exc.getBacktrace()));
             marshalStream.dumpVariables(attrs);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyException exc = (RubyException)type.allocate();
             
             unmarshalStream.registerLinkTarget(exc);
             unmarshalStream.defaultVariablesUnmarshal(exc);
             
             exc.message = exc.removeInternalVariable("mesg");
             exc.set_backtrace(exc.removeInternalVariable("bt"));
             
             return exc;
         }
     };
 
     public static RubyClass createExceptionClass(Ruby runtime) {
         RubyClass exceptionClass = runtime.defineClass("Exception", runtime.getObject(), EXCEPTION_ALLOCATOR);
         runtime.setException(exceptionClass);
 
         exceptionClass.setMarshal(EXCEPTION_MARSHAL);
         exceptionClass.defineAnnotatedMethods(RubyException.class);
 
         return exceptionClass;
     }
 
     public static RubyException newException(Ruby runtime, RubyClass excptnClass, String msg) {
         return new RubyException(runtime, excptnClass, msg);
     }
     
     public void setBacktraceFrames(StackTraceElement[] backtraceFrames) {
         this.backtraceFrames = backtraceFrames;
         if (TRACE_TYPE == RAW ||
                 TRACE_TYPE == RAW_FILTERED ||
                 TRACE_TYPE == RUBY_COMPILED ||
                 TRACE_TYPE == RUBY_HYBRID) {
             javaStackTrace = Thread.currentThread().getStackTrace();
         }
     }
     
     public static final int RAW = 0;
     public static final int RAW_FILTERED = 1;
     public static final int RUBY_FRAMED = 2;
     public static final int RUBY_COMPILED = 3;
     public static final int RUBY_HYBRID = 4;
     
     public static final int TRACE_TYPE;
     
     static {
         String style = SafePropertyAccessor.getProperty("jruby.backtrace.style", "ruby_framed").toLowerCase();
         
         if (style.equals("raw")) TRACE_TYPE = RAW;
         else if (style.equals("raw_filtered")) TRACE_TYPE = RAW_FILTERED;
         else if (style.equals("ruby_framed")) TRACE_TYPE = RUBY_FRAMED;
         else if (style.equals("ruby_compiled")) TRACE_TYPE = RUBY_COMPILED;
         else if (style.equals("ruby_hybrid")) TRACE_TYPE = RUBY_HYBRID;
         else TRACE_TYPE = RUBY_FRAMED;
     }
     
     public IRubyObject getBacktrace() {
         if (backtrace == null) {
             initBacktrace();
         }
         return backtrace;
     }
     
     public void initBacktrace() {
         switch (TRACE_TYPE) {
         case RAW:
             backtrace = ThreadContext.createRawBacktrace(getRuntime(), javaStackTrace, false);
             break;
         case RAW_FILTERED:
             backtrace = ThreadContext.createRawBacktrace(getRuntime(), javaStackTrace, true);
             break;
         case RUBY_FRAMED:
             backtrace = backtraceFrames == null ? getRuntime().getNil() : ThreadContext.createBacktraceFromFrames(getRuntime(), backtraceFrames);
             break;
         case RUBY_COMPILED:
             backtrace = ThreadContext.createRubyCompiledBacktrace(getRuntime(), javaStackTrace);
             break;
         case RUBY_HYBRID:
             backtrace = ThreadContext.createRubyHybridBacktrace(getRuntime(), backtraceFrames, javaStackTrace, getRuntime().getDebug().isTrue());
             break;
         }
     }
 
     @JRubyMethod(optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 1) message = args[0];
         return this;
     }
 
     @JRubyMethod
     public IRubyObject backtrace() {
         return getBacktrace(); 
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject set_backtrace(IRubyObject obj) {
         if (obj.isNil()) {
             backtrace = null;
         } else if (!isArrayOfStrings(obj)) {
             throw getRuntime().newTypeError("backtrace must be Array of String");
         } else {
             backtrace = (RubyArray) obj;
         }
         return backtrace();
     }
     
     @JRubyMethod(name = "exception", optional = 1, rest = true, meta = true)
     public static IRubyObject exception(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return ((RubyClass) recv).newInstance(context, args, block);
     }
 
     @JRubyMethod(optional = 1)
     public RubyException exception(IRubyObject[] args) {
         switch (args.length) {
             case 0 :
                 return this;
             case 1 :
                 if(args[0] == this) {
                     return this;
                 }
                 RubyException ret = (RubyException)rbClone();
                 ret.initialize(args, Block.NULL_BLOCK); // This looks wrong, but it's the way MRI does it.
                 return ret;
             default :
                 throw getRuntime().newArgumentError("Wrong argument count");
         }
     }
 
-    @JRubyMethod
-    public IRubyObject to_s() {
-        if (message.isNil()) return getRuntime().newString(getMetaClass().getName());
+    @JRubyMethod(name = "to_s")
+    public IRubyObject to_s(ThreadContext context) {
+        if (message.isNil()) return context.getRuntime().newString(getMetaClass().getName());
         message.setTaint(isTaint());
         return message;
     }
 
-    @JRubyMethod(name = {"to_str", "message"})
-    public IRubyObject to_str(ThreadContext context) {
+    @JRubyMethod(name = "message")
+    public IRubyObject message(ThreadContext context) {
         return callMethod(context, "to_s");
     }
 
     /** inspects an object and return a kind of debug information
      * 
      *@return A RubyString containing the debug information.
      */
-    @JRubyMethod
+    @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         RubyModule rubyClass = getMetaClass();
         RubyString exception = RubyString.objAsString(context, this);
 
         if (exception.getByteList().realSize == 0) return getRuntime().newString(rubyClass.getName());
         StringBuilder sb = new StringBuilder("#<");
         sb.append(rubyClass.getName()).append(": ").append(exception.getByteList()).append(">");
         return getRuntime().newString(sb.toString());
     }
 
     public void printBacktrace(PrintStream errorStream) {
         IRubyObject backtrace = callMethod(getRuntime().getCurrentContext(), "backtrace");
         boolean debug = getRuntime().getDebug().isTrue();
         if (!backtrace.isNil() && backtrace instanceof RubyArray) {
             IRubyObject[] elements = backtrace.convertToArray().toJavaArray();
 
             for (int i = 1; i < elements.length; i++) {
                 IRubyObject stackTraceLine = elements[i];
                     if (stackTraceLine instanceof RubyString) {
                     printStackTraceLine(errorStream, stackTraceLine);
                 }
 
                 if (!debug && i == RubyException.TRACE_HEAD && elements.length > RubyException.TRACE_MAX) {
                     int hiddenLevels = elements.length - RubyException.TRACE_HEAD - RubyException.TRACE_TAIL;
                             errorStream.print("\t ... " + hiddenLevels + " levels...\n");
                     i = elements.length - RubyException.TRACE_TAIL;
                 }
             }
         }
     }
 
     private void printStackTraceLine(PrintStream errorStream, IRubyObject stackTraceLine) {
             errorStream.print("\tfrom " + stackTraceLine + '\n');
     }
 	
     private boolean isArrayOfStrings(IRubyObject backtrace) {
         if (!(backtrace instanceof RubyArray)) return false; 
             
         IRubyObject[] elements = ((RubyArray) backtrace).toJavaArray();
         
         for (int i = 0 ; i < elements.length ; i++) {
             if (!(elements[i] instanceof RubyString)) return false;
         }
             
         return true;
     }
 }
diff --git a/src/org/jruby/RubyNameError.java b/src/org/jruby/RubyNameError.java
index ac945d9f24..65323ed492 100644
--- a/src/org/jruby/RubyNameError.java
+++ b/src/org/jruby/RubyNameError.java
@@ -1,211 +1,211 @@
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
  * Copyright (C) 2006 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 
 import static org.jruby.runtime.Visibility.PRIVATE;
 import static org.jruby.runtime.Visibility.PROTECTED;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Sprintf;
 
 /**
  * @author Anders Bengtsson
  */
 @JRubyClass(name="NameError", parent="StandardError")
 public class RubyNameError extends RubyException {
     private IRubyObject name;
 
     /** 
      * Nested class whose instances act as thunks reacting to to_str method
      * called from (Exception#to_str, Exception#message)
      * MRI equivalent: rb_cNameErrorMesg, class name: "message", construction method: "!",
      * to_str implementation: "name_err_mesg_to_str"
      *
      * TODO: this class should not be lookupable
      */
-    @JRubyClass(name = "NameError::Message", parent = "Object")
+    @JRubyClass(name = "NameError::Message", parent = "Data")
     public static final class RubyNameErrorMessage extends RubyObject {
 
         static ObjectAllocator NAMEERRORMESSAGE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 IRubyObject dummy = new RubyObject(runtime, runtime.getObject());
                 return new RubyNameErrorMessage(runtime, dummy, dummy, Visibility.PRIVATE, CallType.VARIABLE);
             }
         };        
 
         private final IRubyObject object;
         private final IRubyObject method;
         private final Visibility visibility;
         private final CallType callType;
 
         RubyNameErrorMessage(Ruby runtime, IRubyObject object, IRubyObject method, Visibility visibility, CallType callType) {
             super(runtime, runtime.getNameErrorMessage(), false);
             this.object = object;
             this.method = method;
             this.visibility = visibility;
             this.callType = callType;
         }
 
         @JRubyMethod(name = "_load", meta = true)
         public static IRubyObject load(IRubyObject recv, IRubyObject arg) {
             return arg;
         }
 
         @JRubyMethod(name = "_dump")
         public IRubyObject dump(ThreadContext context, IRubyObject arg) {
             return to_str(context);
         }
 
         @JRubyMethod(name = "to_str")
         public IRubyObject to_str(ThreadContext context) {
             String format = null;
 
             if (visibility == PRIVATE) {
                 format = "private method `%s' called for %s";
             } else if (visibility == PROTECTED) {
                 format = "protected method `%s' called for %s";
             } else if (callType == CallType.VARIABLE) {
                 format = "undefined local variable or method `%s' for %s";
             } else if (callType == CallType.SUPER) {
                 format = "super: no superclass method `%s'";
             }
 
             if (format == null) format = "undefined method `%s' for %s";
 
             String description = null;
 
             if (object.isNil()) {
                 description = "nil";
             } else if (object instanceof RubyBoolean && object.isTrue()) {
                 description = "true";
             } else if (object instanceof RubyBoolean && !object.isTrue()) {
                 description = "false";
             } else {
                 try {
                     description = RubyObject.inspect(context, object).toString();
                 } catch(JumpException e) {}
 
                 if (description == null || description.length() > 65) description = object.anyToString().toString();
             }
 
             if (description.length() == 0 || (description.length() > 0 && description.charAt(0) != '#')) {
                 description = description + ":" + object.getMetaClass().getRealClass().getName();            
             }
 
             Ruby runtime = getRuntime();
             RubyArray arr = runtime.newArray(method, runtime.newString(description));
             RubyString msg = runtime.newString(Sprintf.sprintf(runtime.newString(format), arr).toString());
             if (object.isTaint()) msg.setTaint(true);
             return msg;
         }
     }
 
     private static ObjectAllocator NAMEERROR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNameError(runtime, klass);
         }
     };
 
     public static RubyClass createNameErrorClass(Ruby runtime, RubyClass standardErrorClass) {
         RubyClass nameErrorClass = runtime.defineClass("NameError", standardErrorClass, NAMEERROR_ALLOCATOR);
         nameErrorClass.defineAnnotatedMethods(RubyNameError.class);
         return nameErrorClass;
     }
 
     public static RubyClass createNameErrorMessageClass(Ruby runtime, RubyClass nameErrorClass) {
-        RubyClass messageClass = nameErrorClass.defineClassUnder("Message", runtime.getObject(), RubyNameErrorMessage.NAMEERRORMESSAGE_ALLOCATOR);
+        RubyClass messageClass = nameErrorClass.defineClassUnder("Message", runtime.getClass("Data"), RubyNameErrorMessage.NAMEERRORMESSAGE_ALLOCATOR);
         messageClass.defineAnnotatedMethods(RubyNameErrorMessage.class);
         return messageClass;
     }
 
     protected RubyNameError(Ruby runtime, RubyClass exceptionClass) {
         this(runtime, exceptionClass, exceptionClass.getName());
     }
 
     public RubyNameError(Ruby runtime, RubyClass exceptionClass, String message) {
         this(runtime, exceptionClass, message, null);
     }
 
     public RubyNameError(Ruby runtime, RubyClass exceptionClass, String message, String name) {
         super(runtime, exceptionClass, message);
         this.name = name == null ? runtime.getNil() : runtime.newString(name);
     }
 
     @JRubyMethod(name = "exception", rest = true, meta = true)
     public static RubyException newRubyNameError(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         
         RubyException newError = (RubyException) klass.allocate();
         
         newError.callInit(args, Block.NULL_BLOCK);
         
         return newError;
     }
 
     @JRubyMethod(name = "initialize", optional = 2, frame = true)
     @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length > 1) {
             name = args[args.length - 1];
             int newLength = args.length > 2 ? args.length - 2 : args.length - 1;
 
             IRubyObject []tmpArgs = new IRubyObject[newLength];
             System.arraycopy(args, 0, tmpArgs, 0, newLength);
             args = tmpArgs;
         } else {
             name = getRuntime().getNil();
         }
 
         super.initialize(args, block);
         return this;
     }
 
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         if (message.isNil()) return getRuntime().newString(message.getMetaClass().getName());
         RubyString str = message.convertToString();
         if (str != message) message = str;
         if (isTaint()) message.setTaint(true);
         return message;
     }
 
     @JRubyMethod(name = "name")
     public IRubyObject name() {
         return name;
     }
 }
diff --git a/src/org/jruby/exceptions/RaiseException.java b/src/org/jruby/exceptions/RaiseException.java
index 2ca7e163fe..c0ccfb41fa 100644
--- a/src/org/jruby/exceptions/RaiseException.java
+++ b/src/org/jruby/exceptions/RaiseException.java
@@ -1,190 +1,190 @@
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
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RaiseException extends JumpException {
     public static final boolean DEBUG = false;
     private static final long serialVersionUID = -7612079169559973951L;
     
     private RubyException exception;
 
     public RaiseException(RubyException actException) {
         this(actException, false);
     }
 
     public RaiseException(Ruby runtime, RubyClass excptnClass, String msg, boolean nativeException) {
         super(msg);
         if (msg == null) {
             msg = "No message available";
         }
         if (DEBUG) {
             Thread.dumpStack();
         }
         setException((RubyException)RuntimeHelpers.invoke(
                 runtime.getCurrentContext(),
                 excptnClass,
                 "new",
                 RubyString.newUnicodeString(excptnClass.getRuntime(), msg)),
                 nativeException);
     }
 
     public RaiseException(RubyException exception, boolean isNativeException) {
         super();
         if (DEBUG) {
             Thread.dumpStack();
         }
         setException(exception, isNativeException);
     }
 
     public static RaiseException createNativeRaiseException(Ruby runtime, Throwable cause) {
         NativeException nativeException = new NativeException(runtime, runtime.getClass(NativeException.CLASS_NAME), cause);
         return new RaiseException(cause, nativeException);
     }
 
     private static String buildMessage(Throwable exception) {
         StringBuilder sb = new StringBuilder();
         StringWriter stackTrace = new StringWriter();
         exception.printStackTrace(new PrintWriter(stackTrace));
     
         sb.append("Native Exception: '").append(exception.getClass()).append("'; ");
         sb.append("Message: ").append(exception.getMessage()).append("; ");
         sb.append("StackTrace: ").append(stackTrace.getBuffer().toString());
 
         return sb.toString();
     }
 
     public RaiseException(Throwable cause, NativeException nativeException) {
         super(buildMessage(cause), cause);
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
 
         if (runtime.hasEventHooks()) {
             runtime.callEventHooks(
                     context,
                     RubyEvent.RAISE,
                     context.getFile(),
                     context.getLine(),
                     context.getFrameName(),
                     context.getFrameKlazz());
         }
 
         this.exception = newException;
 
         if (runtime.getStackTraces() > 5) {
             return;
         }
 
         runtime.setStackTraces(runtime.getStackTraces() + 1);
 
         StackTraceElement[] stackTrace = context.createBacktrace2(0, nativeException);
         newException.setBacktraceFrames(stackTrace);
 
         // JRUBY-2673: if wrapping a NativeException, use the actual Java exception's trace as our Java trace
         if (newException instanceof NativeException) {
             setStackTrace(((NativeException)newException).getCause().getStackTrace());
         } else {
             setStackTrace(stackTrace);
         }
 
         runtime.setStackTraces(runtime.getStackTraces() - 1);
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
-            String firstLine = backtrace.callMethod(runtime.getCurrentContext(), "first").callMethod(runtime.getCurrentContext(), "to_s").toString();
+            String firstLine = backtrace.callMethod(runtime.getCurrentContext(), "first").convertToString().toString();
             ps.print(firstLine + ": ");
         }
-        ps.println(exception.message + " (" + exception.getMetaClass().toString() + ")");
+        ps.println(exception.message.convertToString() + " (" + exception.getMetaClass().toString() + ")");
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
