diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 14c2800ae5..e20ed6f057 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -255,2330 +255,2333 @@ public final class Ruby {
     private JRubyClassLoader jrubyClassLoader;
 
     private static boolean securityRestricted = false;
 
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
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     private AtomicInteger symbolLastId = new AtomicInteger(128);
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     private AtomicInteger moduleLastId = new AtomicInteger(0);
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
 
     private Object respondToMethod;
 
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
     
     private String[] argv;
 
     /**
      * Create and initialize a new JRuby Runtime.
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
         this.jrubyClassLoader   = config.getJRubyClassLoader();
         this.argv               = config.getArgv();
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static Ruby newInstance() {
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
         if (RUNTIME_THREADLOCAL) {
             setCurrentInstance(ruby);
         }
         return ruby;
     }
     
     public static Ruby getCurrentInstance() {
         return currentRuntime.get();
     }
     
     public static void setCurrentInstance(Ruby runtime) {
         currentRuntime.set(runtime);
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
 
     public IRubyObject evalFile(InputStream in, String name) {
         return eval(parseFile(in, name, getCurrentContext().getCurrentScope()));
     }
     
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScriptlet(String script) {
         return eval(parseEval(script, "<script>", getCurrentContext().getCurrentScope(), 0));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             return ASTInterpreter.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         }
     }
     
     /**
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
         
         getCurrentContext().getCurrentFrame().setPosition(node.getPosition());
         return runNormally(node, false);
     }
     
     public void runFromMain(InputStream in, String filename) {
         if(config.isYARVEnabled()) {
             new YARVCompiledRunner(this, in, filename).run();
         } else if(config.isRubiniusEnabled()) {
             new RubiniusRunner(this, in, filename).run();
         } else {
             Node scriptNode;
             if (config.isInlineScript()) {
                 scriptNode = parseInline(in, filename, getCurrentContext().getCurrentScope());
             } else {
                 scriptNode = parseFile(in, filename, getCurrentContext().getCurrentScope());
             }
             
             getCurrentContext().getCurrentFrame().setPosition(scriptNode.getPosition());
 
             if (config.isAssumePrinting() || config.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, config.isAssumePrinting(), config.isProcessLineEnds(),
                         config.isSplit(), config.isYARVCompileEnabled());
             } else {
                 runNormally(scriptNode, config.isYARVCompileEnabled());
             }
         }
     }
     
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
         
         while (RubyKernel.gets(getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
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
                     
                     if (printing) RubyKernel.print(getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
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
     
     public IRubyObject runNormally(Node scriptNode, boolean yarvCompile) {
         Script script = null;
         YARVCompiledRunner runner = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         boolean forceCompile = getInstanceConfig().getCompileMode().shouldPrecompileAll();
         if (compile) {
             script = tryCompile(scriptNode);
             if (forceCompile && script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (script != null) {
             return runScript(script);
         } else if (runner != null) {
             return runYarv(runner);
         } else {
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
             compiler.compileRoot(node, asmCompiler, inspector);
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
             return script.load(getCurrentContext(), context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
         
         try {
             return ASTInterpreter.eval(this, context, scriptNode, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
     
     public IRubyObject getUndef() {
         return undef;
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
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
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
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }    
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
     
     public RubyClass getStandardError() {
         return standardError;
     }
 
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * 
      * @param internedName the name of the module; <em>must</em> be an interned String!
      * @return
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** Returns a class from the instance pool.
      *
      * @param name The name of the class.
      * @return The class.
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** rb_define_class
      *
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         IRubyObject classObj = objectClass.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) throw newNameError(name + " is already defined", name);
             return klazz;
         }
 
         if (superClass == null) {
             warnings.warn("no super class for `" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, objectClass, false);
     }
 
     /** rb_define_class_under
     *
     */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
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
 
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         
         defineGlobalVERBOSE();
         defineGlobalDEBUG();
 
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
     }
 
     private void initLibraries() {
         loadService = config.createLoadService(this);
         registerBuiltin("java.rb", new Library() {
-                public void load(Ruby runtime) throws IOException {
+                public void load(Ruby runtime, boolean wrap) throws IOException {
                     Java.createJavaModule(runtime);
                     runtime.getLoadService().smartLoad("builtin/javasupport");
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
-                public void load(Ruby runtime) throws IOException {
+                public void load(Ruby runtime, boolean wrap) throws IOException {
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
-                public void load(Ruby runtime) throws IOException {
+                public void load(Ruby runtime, boolean wrap) throws IOException {
                     runtime.getLoadService().require("jruby/openssl/stub");
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
         registerBuiltin("weakref.rb", new LateLoadingLibrary("weakref", "org.jruby.ext.WeakRef$WeakRefLibrary", getJRubyClassLoader()));
         
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             registerBuiltin("fiber.so", new LateLoadingLibrary("fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader()));
         }
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
     
-    public void loadFile(String scriptName, InputStream in) {
+    public void loadFile(String scriptName, InputStream in, boolean wrap) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             }
         }
 
-        IRubyObject self = getTopSelf();
+        IRubyObject self = null;
+        if (wrap) {
+            self = TopSelfFactory.createTopSelf(this);
+        } else {
+            self = getTopSelf();
+        }
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
     
-    public void compileAndLoadFile(String filename, InputStream in) {
-        // FIXME: what is this for?
-//        if (!Ruby.isSecurityRestricted()) {
-//            File f = new File(scriptName);
-//            if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
-//                scriptName = "./" + scriptName;
-//            };
-//        }
-        IRubyObject self = getTopSelf();
+    public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
+        IRubyObject self = null;
+        if (wrap) {
+            self = TopSelfFactory.createTopSelf(this);
+        } else {
+            self = getTopSelf();
+        }
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
             if (isSecurityRestricted()) {
                 return "SECURITY RESTRICTED";
             }
             jrubyHome = verifyHome(SafePropertyAccessor.getProperty("jruby.home", SafePropertyAccessor.getProperty("user.home") + "/.jruby"));
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
diff --git a/src/org/jruby/RubyJRuby.java b/src/org/jruby/RubyJRuby.java
index 38220ecf96..f588cdf9e4 100644
--- a/src/org/jruby/RubyJRuby.java
+++ b/src/org/jruby/RubyJRuby.java
@@ -1,232 +1,232 @@
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
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
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 
 import org.jruby.ast.Node;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.runtime.InterpretedBlock;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 /**
  * Module which defines JRuby-specific methods for use. 
  */
 public class RubyJRuby {
     public static RubyModule createJRuby(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule jrubyModule = runtime.defineModule("JRuby");
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyJRuby.class);
         jrubyModule.defineAnnotatedMethods(RubyJRuby.class);
 
         RubyClass compiledScriptClass = jrubyModule.defineClassUnder("CompiledScript",runtime.getObject(), runtime.getObject().getAllocator());
         CallbackFactory compiledScriptCallbackFactory = runtime.callbackFactory(JRubyCompiledScript.class);
 
         compiledScriptClass.attr_accessor(new IRubyObject[]{runtime.newSymbol("name"), runtime.newSymbol("class_name"), runtime.newSymbol("original_script"), runtime.newSymbol("code")});
         compiledScriptClass.defineAnnotatedMethods(JRubyCompiledScript.class);
 
         return jrubyModule;
     }
 
     public static RubyModule createJRubyExt(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule mJRubyExt = runtime.getOrCreateModule("JRuby").defineModuleUnder("Extensions");
         CallbackFactory cf = runtime.callbackFactory(JRubyExtensions.class);
         
         mJRubyExt.defineAnnotatedMethods(JRubyExtensions.class);
 
         runtime.getObject().includeModule(mJRubyExt);
 
         return mJRubyExt;
     }
 
     public static class ExtLibrary implements Library {
-        public void load(Ruby runtime) throws IOException {
+        public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyJRuby.createJRubyExt(runtime);
         }
     }
     
     @JRubyMethod(name = "runtime", frame = true, module = true)
     public static IRubyObject runtime(IRubyObject recv, Block unusedBlock) {
         return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), recv.getRuntime()), Block.NULL_BLOCK);
     }
     
     @JRubyMethod(name = {"parse", "ast_for"}, optional = 3, frame = true, module = true)
     public static IRubyObject parse(IRubyObject recv, IRubyObject[] args, Block block) {
         if(block.isGiven()) {
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), ((InterpretedBlock)block.getBody()).getIterNode().getBodyNode()), Block.NULL_BLOCK);
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             String filename = "-";
             boolean extraPositionInformation = false;
             RubyString content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
             return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), 
                recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation)), Block.NULL_BLOCK);
         }
     }
 
     @JRubyMethod(name = "compile", optional = 3, frame = true, module = true)
     public static IRubyObject compile(IRubyObject recv, IRubyObject[] args, Block block) {
         Node node;
         String filename;
         RubyString content = recv.getRuntime().newString("");
         if(block.isGiven()) {
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             if(block.getBody() instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             Node bnode = ((InterpretedBlock)block.getBody()).getIterNode().getBodyNode();
             node = new org.jruby.ast.RootNode(bnode.getPosition(), block.getBinding().getDynamicScope(), bnode);
             filename = "__block_" + node.getPosition().getFile();
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             filename = "-";
             boolean extraPositionInformation = false;
             content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
 
             node = recv.getRuntime().parse(content.getByteList(), filename, null, 0, extraPositionInformation);
         }
 
         String classname;
         if (filename.equals("-e")) {
             classname = "__dash_e__";
         } else {
             classname = filename.replace('\\', '/').replaceAll(".rb", "").replaceAll("-","_dash_");
         }
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
             
         StandardASMCompiler asmCompiler = new StandardASMCompiler(classname, filename);
         ASTCompiler compiler = new ASTCompiler();
         compiler.compileRoot(node, asmCompiler, inspector);
         byte[] bts = asmCompiler.getClassByteArray();
 
         IRubyObject compiledScript = ((RubyModule)recv).fastGetConstant("CompiledScript").callMethod(recv.getRuntime().getCurrentContext(),"new");
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "name=", recv.getRuntime().newString(filename));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "class_name=", recv.getRuntime().newString(classname));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "original_script=", content);
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "code=", Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), bts), Block.NULL_BLOCK));
 
         return compiledScript;
     }
 
     @JRubyMethod(name = "reference", required = 1, module = true)
     public static IRubyObject reference(IRubyObject recv, IRubyObject obj) {
         return Java.wrap(recv.getRuntime().getJavaSupport().getJavaUtilitiesModule(),
                 JavaObject.wrap(recv.getRuntime(), obj));
     }
 
     public static class JRubyCompiledScript {
         @JRubyMethod(name = "to_s")
         public static IRubyObject compiled_script_to_s(IRubyObject recv) {
             return recv.getInstanceVariables().fastGetInstanceVariable("@original_script");
         }
 
         @JRubyMethod(name = "inspect")
         public static IRubyObject compiled_script_inspect(IRubyObject recv) {
             return recv.getRuntime().newString("#<JRuby::CompiledScript " + recv.getInstanceVariables().fastGetInstanceVariable("@name") + ">");
         }
 
         @JRubyMethod(name = "inspect_bytecode")
         public static IRubyObject compiled_script_inspect_bytecode(IRubyObject recv) {
             StringWriter sw = new StringWriter();
             ClassReader cr = new ClassReader((byte[])org.jruby.javasupport.JavaUtil.convertRubyToJava(recv.getInstanceVariables().fastGetInstanceVariable("@code"),byte[].class));
             TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(sw));
             cr.accept(cv, ClassReader.SKIP_DEBUG);
             return recv.getRuntime().newString(sw.toString());
         }
     }
 
     public static class JRubyExtensions {
         @JRubyMethod(name = "steal_method", required = 2, module = true)
         public static IRubyObject steal_method(IRubyObject recv, IRubyObject type, IRubyObject methodName) {
             RubyModule to_add = null;
             if(recv instanceof RubyModule) {
                 to_add = (RubyModule)recv;
             } else {
                 to_add = recv.getSingletonClass();
             }
             String name = methodName.toString();
             if(!(type instanceof RubyModule)) {
                 throw recv.getRuntime().newArgumentError("First argument must be a module/class");
             }
 
             DynamicMethod method = ((RubyModule)type).searchMethod(name);
             if(method == null || method.isUndefined()) {
                 throw recv.getRuntime().newArgumentError("No such method " + name + " on " + type);
             }
 
             to_add.addMethod(name, method);
             return recv.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "steal_methods", required = 1, rest = true, module = true)
         public static IRubyObject steal_methods(IRubyObject recv, IRubyObject[] args) {
             Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1);
             IRubyObject type = args[0];
             for(int i=1;i<args.length;i++) {
                 steal_method(recv, type, args[i]);
             }
             return recv.getRuntime().getNil();
         }
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 822987be8d..3c5d7c5934 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1132 +1,1132 @@
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
 import java.io.IOException;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
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
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : recv.getRuntime().getObject();
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
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
                 boolean required = loadService.require(file());
                 
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
         
         String name = args[0].asJavaString();
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
 
     @JRubyMethod(name = "getc", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getc(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("getc is obsolete; use STDIN.getc instead");
         IRubyObject defin = recv.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(recv.getRuntime().getCurrentContext(), "getc");
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
             if(((RubyString)object).getByteList().realSize == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), MethodIndex.TO_F, "to_f");
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
         
         IRubyObject tmp = TypeConverter.convertToType(object, recv.getRuntime().getInteger(), MethodIndex.TO_INT, "to_int", false);
         if (tmp.isNil()) return object.convertToInteger(MethodIndex.TO_I, "to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, recv.getRuntime().getString(), MethodIndex.TO_S, "to_s");
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
 
         if (str.getByteList().realSize > 0) {
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
         
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 rubyThread.sleep(milliseconds);
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
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
 
         for (String globalVariableName : recv.getRuntime().getGlobalVariables().getNames()) {
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
         
         if (!runtime.fastGetClass("Exception").isInstance(exception)) {
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
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         boolean wrap = false;
         if (args.length == 2) {
             wrap = args[1].isTrue();
         }
-        recv.getRuntime().getLoadService().load(file.getByteList().toString());
+        recv.getRuntime().getLoadService().load(file.getByteList().toString(), wrap);
         return recv.getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
             
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
         
         IRubyObject scope = null;
         String file = "(eval)";
         int line = 1;
         
         // determine scope and position
         if (args.length > 1 && !args[1].isNil()) {
             scope = args[1];
 
             Binding binding;
             if (scope instanceof RubyBinding) {
                 binding = ((RubyBinding)scope).getBinding();
             } else if (scope instanceof RubyProc) {
                 RubyProc proc = (RubyProc)scope;
                 binding = proc.getBlock().getBinding();
             } else {
                 throw runtime.newTypeError("Wrong argument type " + scope.getMetaClass() + "(expected Proc/Binding)");
             }
 
             Frame frame = binding.getFrame();
             ISourcePosition pos = frame.getPosition();
 
             file = pos.getFile();
             line = pos.getEndLine();
         } else {
             scope = RubyBinding.newBindingForEval(runtime);
         }
             
         // if we have additional args, use them for file and line number
         if (args.length > 2) {
             file = args[2].convertToString().toString();
         } else {
             file = "(eval)";
         }
 
         if (args.length > 3) {
             line = (int)args[3].convertToInteger().getLongValue();
         } else {
             line = 1;
         }
         
         ThreadContext context = runtime.getCurrentContext();
         
         return ASTInterpreter.evalWithBinding(context, src, scope, file, line);
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
         CatchTarget target = new CatchTarget(tag.asJavaString());
         try {
             context.pushCatch(target);
             return block.yield(context, tag);
         } catch (JumpException.ThrowJump tj) {
             if (tj.getTarget() == target) return (IRubyObject) tj.getValue();
             
             throw tj;
         } finally {
             context.popCatch();
         }
     }
     
     public static class CatchTarget implements JumpTarget {
         private final String tag;
         public CatchTarget(String tag) { this.tag = tag; }
         public String getTag() { return tag; }
     }
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asJavaString();
         ThreadContext context = runtime.getCurrentContext();
         CatchTarget[] catches = context.getActiveCatches();
 
         String message = "uncaught throw `" + tag + "'";
 
         // Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i].getTag())) {
                 //Catch active, throw for catch to handle
                 throw new JumpException.ThrowJump(catches[i], args.length > 1 ? args[1] : runtime.getNil());
             }
         }
 
         // No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.getRuntime().getLoadService().require("jsignal");
         return RuntimeHelpers.invoke(recv.getRuntime().getCurrentContext(), recv, "__jtrap", args, CallType.FUNCTIONAL, block);
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
             proc = RubyProc.newProc(recv.getRuntime(), block, Block.Type.PROC);
         }
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], recv.getRuntime().getProc(), 0, "to_proc", true);
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
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
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
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"lambda"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc lambda(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"proc"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc proc_1_9(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.PROC, block);
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
                 if (bj.getTarget() != null && bj.getTarget() != block.getBody()) {
                     bj.setBreakInKernelLoop(true);
                 }
                  
                 throw bj;
             }
         }
     }
     
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError("wrong number of arguments");
 
         int cmd;
         if (args[0] instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubyString &&
                 ((RubyString) args[0]).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString) args[0]).getByteList().charAt(0);
         } else {
             cmd = (int) args[0].convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         case 'A': case 'b': case 'c': case 'C': case 'd': case 'e': case 'f': case 'g': case 'G': 
         case 'k': case 'M': case 'l': case 'o': case 'O': case 'p': case 'r': case 'R': case 's':
         case 'S': case 'u': case 'w': case 'W': case 'x': case 'X': case 'z': case '=': case '<':
         case '>': case '-':
             break;
         default:
             throw recv.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw recv.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw recv.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return RubyFileTest.directory_p(recv, args[1]);
         case 'e': // ?e  | boolean | True if file1 exists
             return RubyFileTest.exist_p(recv, args[1]);
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyFileTest.file_p(recv, args[1]);
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
             return RubyFileTest.setgid_p(recv, args[1]);
         case 'G': // ?G  | boolean | True if file1 exists and has a group ownership equal to the caller's group
             return RubyFileTest.grpowned_p(recv, args[1]);
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
             return RubyFileTest.sticky_p(recv, args[1]);
         case 'M': // ?M  | Time    | Last modification time for file1
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
             return RubyFileTest.symlink_p(recv, args[1]);
         case 'o': // ?o  | boolean | True if file1 exists and is owned by the caller's effective uid
             return RubyFileTest.owned_p(recv, args[1]);
         case 'O': // ?O  | boolean | True if file1 exists and is owned by the caller's real uid 
             return RubyFileTest.rowned_p(recv, args[1]);
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
             return RubyFileTest.pipe_p(recv, args[1]);
         case 'r': // ?r  | boolean | True if file1 is readable by the effective uid/gid of the caller
             return RubyFileTest.readable_p(recv, args[1]);
         case 'R': // ?R  | boolean | True if file is readable by the real uid/gid of the caller
             // FIXME: Need to implement an readable_real_p in FileTest
             return RubyFileTest.readable_p(recv, args[1]);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size, otherwise nil
             return RubyFileTest.size_p(recv, args[1]);
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
             return RubyFileTest.socket_p(recv, args[1]);
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
             return RubyFileTest.setuid_p(recv, args[1]);
         case 'w': // ?w  | boolean | True if file1 exists and is writable by effective uid/gid
             return RubyFileTest.writable_p(recv, args[1]);
         case 'W': // ?W  | boolean | True if file1 exists and is writable by the real uid/gid
             // FIXME: Need to implement an writable_real_p in FileTest
             return RubyFileTest.writable_p(recv, args[1]);
         case 'x': // ?x  | boolean | True if file1 exists and is executable by the effective uid/gid
             return RubyFileTest.executable_p(recv, args[1]);
         case 'X': // ?X  | boolean | True if file1 exists and is executable by the real uid/gid
             return RubyFileTest.executable_real_p(recv, args[1]);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return RubyFileTest.zero_p(recv, args[1]);
         case '=': // ?=  | boolean | True if the modification times of file1 and file2 are equal
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         RubyString string = aString.convertToString();
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {string}, output);
         
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
diff --git a/src/org/jruby/ext/Generator.java b/src/org/jruby/ext/Generator.java
index f427f43d67..be9bc1d58e 100644
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
 
 import org.jruby.javasupport.util.RuntimeHelpers;
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
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
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
             if(enm != null) {
                 ThreadContext context = gen.getRuntime().getCurrentContext();
                 enm.callMethod(context, "each", IRubyObject.NULL_ARRAY, 
                         CallBlock.newCallClosure(enm,enm.getMetaClass().getRealClass(),Arity.noArguments(),ibc,context));
             } else {
                 proc.call(new IRubyObject[]{gen});
             }
             end = true;
             synchronized(mutex) {
                 mutex.notifyAll();
             }
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
         
         self.getInstanceVariables().setInstanceVariable("@queue",self.getRuntime().newArray());
         self.getInstanceVariables().setInstanceVariable("@index",self.getRuntime().newFixnum(0));
         
         if(Arity.checkArgumentCount(self.getRuntime(), args,0,1) == 1) {
             d.setEnum(args[0]);
         } else {
             d.setProc(self.getRuntime().newProc(Block.Type.PROC, block));
         }
         return self;
     }
 
     public static IRubyObject yield(IRubyObject self, IRubyObject value, Block block) {
         // Generator#yield
         self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"<<",value);
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         d.doWait();
         return self;
     }
 
     public static IRubyObject end_p(IRubyObject self) {
         // Generator#end_p
         GeneratorData d = (GeneratorData)self.dataGetStruct();
         
         boolean emptyQueue = self.getInstanceVariables().getInstanceVariable("@queue").callMethod(
                 self.getRuntime().getCurrentContext(), MethodIndex.EMPTY_P, "empty?").isTrue();
         
         return (d.isEnd() && emptyQueue) ? self.getRuntime().getTrue() : self.getRuntime().getFalse();
     }
 
     public static IRubyObject next_p(IRubyObject self) {
         // Generator#next_p        
         return RuntimeHelpers.negate(
                 RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, "end?"),
                 self.getRuntime());
     }
 
     public static IRubyObject index(IRubyObject self) {
         // Generator#index
         return self.getInstanceVariables().getInstanceVariable("@index");
     }
 
     public static IRubyObject next(IRubyObject self, Block block) {
         // Generator#next
         GeneratorData d = (GeneratorData)self.dataGetStruct();
 
         if(RuntimeHelpers.invoke(self.getRuntime().getCurrentContext(), self, "end?").isTrue()) {
             throw self.getRuntime().newEOFError("no more elements available");
         }
 
         d.generate();
         self.getInstanceVariables().setInstanceVariable("@index",self.getInstanceVariables().getInstanceVariable("@index").callMethod(self.getRuntime().getCurrentContext(),MethodIndex.OP_PLUS, "+",self.getRuntime().newFixnum(1)));
         return self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"shift");
     }
 
     public static IRubyObject current(IRubyObject self, Block block) {
         // Generator#current
         if(self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),MethodIndex.EMPTY_P, "empty?").isTrue()) {
             throw self.getRuntime().newEOFError("no more elements available");
         }
         return self.getInstanceVariables().getInstanceVariable("@queue").callMethod(self.getRuntime().getCurrentContext(),"first");
     }
 
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
diff --git a/src/org/jruby/ext/LateLoadingLibrary.java b/src/org/jruby/ext/LateLoadingLibrary.java
index 1d2094089b..f35bc84c25 100644
--- a/src/org/jruby/ext/LateLoadingLibrary.java
+++ b/src/org/jruby/ext/LateLoadingLibrary.java
@@ -1,61 +1,61 @@
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
     
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         try {
             if (classLoader == null && Ruby.isSecurityRestricted()) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
             Class libraryClass = classLoader.loadClass(className);
             
             Library library = (Library)libraryClass.newInstance();
             
-            library.load(runtime);
+            library.load(runtime, wrap);
         } catch (Throwable e) {
             throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
         }
     }
 
 }
diff --git a/src/org/jruby/ext/Readline.java b/src/org/jruby/ext/Readline.java
index 82e3e2fdbe..a5535b7ae9 100644
--- a/src/org/jruby/ext/Readline.java
+++ b/src/org/jruby/ext/Readline.java
@@ -1,240 +1,240 @@
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
  * Copyright (C) 2006 Damian Steer <pldms@mac.com>
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
 import java.util.List;
 import java.util.Iterator;
 import java.util.Collections;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.RubyArray;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import jline.ConsoleReader;
 import jline.Completor;
 import jline.FileNameCompletor;
 import jline.CandidateListCompletionHandler;
 import jline.History;
 import org.jruby.runtime.MethodIndex;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @author <a href="mailto:pldms@mac.com">Damian Steer</a>
  */
 public class Readline {
     public static class Service implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             createReadline(runtime);
         }
     }
 
     private static ConsoleReader readline;
     private static Completor currentCompletor;
     private static History history;
 
     public static void createReadline(Ruby runtime) throws IOException {
         history = new History();
         currentCompletor = null;
         
         RubyModule mReadline = runtime.defineModule("Readline");
         CallbackFactory readlinecb = runtime.callbackFactory(Readline.class);
         mReadline.defineMethod("readline",readlinecb.getFastSingletonMethod("s_readline",IRubyObject.class,IRubyObject.class));
         mReadline.module_function(new IRubyObject[]{runtime.newSymbol("readline")});
         mReadline.defineMethod("completion_append_character=",readlinecb.getFastSingletonMethod("s_set_completion_append_character",IRubyObject.class));
         mReadline.module_function(new IRubyObject[]{runtime.newSymbol("completion_append_character=")});
         mReadline.defineMethod("completion_proc=",readlinecb.getFastSingletonMethod("s_set_completion_proc",IRubyObject.class));
         mReadline.module_function(new IRubyObject[]{runtime.newSymbol("completion_proc=")});
         IRubyObject hist = runtime.getObject().callMethod(runtime.getCurrentContext(), "new");
         mReadline.fastSetConstant("HISTORY",hist);
         hist.getSingletonClass().includeModule(runtime.getEnumerable());
         hist.getSingletonClass().defineMethod("push",readlinecb.getFastOptSingletonMethod("s_push"));
         hist.getSingletonClass().defineMethod("pop",readlinecb.getFastSingletonMethod("s_pop"));
         hist.getSingletonClass().defineMethod("to_a",readlinecb.getFastSingletonMethod("s_hist_to_a"));
         hist.getSingletonClass().defineMethod("to_s", readlinecb.getFastSingletonMethod("s_hist_to_s"));
         hist.getSingletonClass().defineMethod("[]", readlinecb.getFastSingletonMethod("s_hist_get", IRubyObject.class));
         hist.getSingletonClass().defineMethod("[]=", readlinecb.getFastSingletonMethod("s_hist_set", IRubyObject.class, IRubyObject.class));
         hist.getSingletonClass().defineMethod("<<", readlinecb.getFastOptSingletonMethod("s_push"));
         hist.getSingletonClass().defineMethod("shift", readlinecb.getFastSingletonMethod("s_hist_shift"));
         hist.getSingletonClass().defineMethod("each", readlinecb.getSingletonMethod("s_hist_each"));
         hist.getSingletonClass().defineMethod("length", readlinecb.getFastSingletonMethod("s_hist_length"));
         hist.getSingletonClass().defineMethod("size", readlinecb.getFastSingletonMethod("s_hist_length"));
         hist.getSingletonClass().defineMethod("empty?", readlinecb.getFastSingletonMethod("s_hist_empty_p"));
         hist.getSingletonClass().defineMethod("delete_at", readlinecb.getFastSingletonMethod("s_hist_delete_at", IRubyObject.class));
     }
     
     // We lazily initialise this in case Readline.readline has been overriden in ruby (s_readline)
     protected static void initReadline() throws IOException {
         readline = new ConsoleReader();
         readline.setUseHistory(false);
         readline.setUsePagination(true);
         readline.setBellEnabled(false);
         ((CandidateListCompletionHandler) readline.getCompletionHandler()).setAlwaysIncludeNewline(false);
         if (currentCompletor == null)
             currentCompletor = new RubyFileNameCompletor();
         readline.addCompletor(currentCompletor);
         readline.setHistory(history);
     }
     
     public static History getHistory() {
         return history;
     }
     
     public static void setCompletor(Completor completor) {
         if (readline != null) readline.removeCompletor(currentCompletor);
         currentCompletor = completor;
         if (readline != null) readline.addCompletor(currentCompletor);
     }
     
     public static Completor getCompletor() {
         return currentCompletor;
     }
     
     public static IRubyObject s_readline(IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
         if (readline == null) initReadline(); // not overridden, let's go
         IRubyObject line = recv.getRuntime().getNil();
         readline.getTerminal().disableEcho();
         String v = readline.readLine(prompt.toString());
         readline.getTerminal().enableEcho();
         if(null != v) {
             if (add_to_hist.isTrue())
                 readline.getHistory().addToHistory(v);
             line = recv.getRuntime().newString(v);
         }
         return line;
     }
 
     public static IRubyObject s_push(IRubyObject recv, IRubyObject[] lines) throws Exception {
         for (int i = 0; i < lines.length; i++) {
             history.addToHistory(lines[i].toString());
         }
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject s_pop(IRubyObject recv) throws Exception {
         return recv.getRuntime().getNil();
     }
 	
 	public static IRubyObject s_hist_to_a(IRubyObject recv) throws Exception {
 		RubyArray histList = recv.getRuntime().newArray();
 		for (Iterator i = history.getHistoryList().iterator(); i.hasNext();) {
 			histList.append(recv.getRuntime().newString((String) i.next()));
 		}
 		return histList;
 	}
 	
 	public static IRubyObject s_hist_to_s(IRubyObject recv) {
 	    return recv.getRuntime().newString("HISTORY");
 	}
 	
 	public static IRubyObject s_hist_get(IRubyObject recv, IRubyObject index) {
 	    int i = (int) index.convertToInteger().getLongValue();
 	    return recv.getRuntime().newString((String) history.getHistoryList().get(i));
 	}
 	
 	public static IRubyObject s_hist_set(IRubyObject recv, IRubyObject index, IRubyObject val) {
 	    throw recv.getRuntime().newNotImplementedError("the []=() function is unimplemented on this machine");
 	}
 	
 	public static IRubyObject s_hist_shift(IRubyObject recv) {
 	    throw recv.getRuntime().newNotImplementedError("the shift function is unimplemented on this machine");
 	}
 	
 	public static IRubyObject s_hist_length(IRubyObject recv) {
 	    return recv.getRuntime().newFixnum(history.size());
 	}
 	
 	public static IRubyObject s_hist_empty_p(IRubyObject recv) {
         return recv.getRuntime().newBoolean(history.size() == 0);
     }
 	
 	public static IRubyObject s_hist_delete_at(IRubyObject recv, IRubyObject index) {
 	    throw recv.getRuntime().newNotImplementedError("the delete_at function is unimplemented on this machine");
 	}
 	
 	public static IRubyObject s_hist_each(IRubyObject recv, Block block) {
 	    for (Iterator i = history.getHistoryList().iterator(); i.hasNext();) {
 	        block.yield(recv.getRuntime().getCurrentContext(), recv.getRuntime().newString((String) i.next()));
 	    }
 	    return recv;
 	}
 	
     public static IRubyObject s_set_completion_append_character(IRubyObject recv, IRubyObject achar) throws Exception {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject s_set_completion_proc(IRubyObject recv, IRubyObject proc) throws Exception {
     	if (!proc.respondsTo("call"))
     		throw recv.getRuntime().newArgumentError("argument must respond to call");
 		setCompletor(new ProcCompletor(proc));
         return recv.getRuntime().getNil();
     }
 	
 	// Complete using a Proc object
     public static class ProcCompletor implements Completor {
         IRubyObject procCompletor;
 		
 		public ProcCompletor(IRubyObject procCompletor) {
 			this.procCompletor = procCompletor;
 		}
 
         public int complete(String buffer, int cursor, List candidates) {
             buffer = buffer.substring(0, cursor);
             int index = buffer.lastIndexOf(" ");
             if (index != -1) buffer = buffer.substring(index + 1);
             ThreadContext context = procCompletor.getRuntime().getCurrentContext();
             
             IRubyObject comps = procCompletor.callMethod(context, "call", new IRubyObject[] { procCompletor.getRuntime().newString(buffer) }).callMethod(context, MethodIndex.TO_A, "to_a");
             if (comps instanceof List) {
                 for (Iterator i = ((List) comps).iterator(); i.hasNext();) {
                     Object obj = i.next();
                     if (obj != null) candidates.add(obj.toString());
                 }
                 Collections.sort(candidates);
             }
             return cursor - buffer.length();
         }
     }
     
     // Fix FileNameCompletor to work mid-line
     public static class RubyFileNameCompletor extends FileNameCompletor {
     	public int complete(String buffer, int cursor, List candidates) {
     		buffer = buffer.substring(0, cursor);
             int index = buffer.lastIndexOf(" ");
             if (index != -1) buffer = buffer.substring(index + 1);
             return index + 1 + super.complete(buffer, cursor, candidates);
         }
    	}
    	
 }// Readline
diff --git a/src/org/jruby/ext/WeakRef.java b/src/org/jruby/ext/WeakRef.java
index fd8a2d8252..168ac608c1 100644
--- a/src/org/jruby/ext/WeakRef.java
+++ b/src/org/jruby/ext/WeakRef.java
@@ -1,94 +1,94 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.ext;
 
 import java.io.IOException;
 import java.lang.ref.WeakReference;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 
 /**
  *
  * @author headius
  */
 public class WeakRef extends RubyObject {
     private WeakReference<IRubyObject> ref;
     
     private static final ObjectAllocator WEAKREF_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
             return new WeakRef(runtime, klazz);
         }
     };
     
     public static class WeakRefLibrary implements Library {
-        public void load(Ruby runtime) throws IOException {
+        public void load(Ruby runtime, boolean wrap) throws IOException {
             RubyKernel.require(runtime.getKernel(), runtime.newString("delegate"), Block.NULL_BLOCK);
             
             RubyClass delegatorClass = (RubyClass)runtime.getClassFromPath("Delegator");
             RubyClass weakrefClass = runtime.defineClass("WeakRef", delegatorClass, WEAKREF_ALLOCATOR);
             
             weakrefClass.defineAnnotatedMethods(WeakRef.class);
             
             RubyClass referrorClass = runtime.defineClass("RefError", runtime.getStandardError(), runtime.getStandardError().getAllocator());
         }
     }
     
     public WeakRef(Ruby runtime, RubyClass klazz) {
         super(runtime, klazz);
     }
     
     @JRubyMethod(name = "__getobj__")
     public IRubyObject getobj() {
         IRubyObject obj = ref.get();
         
         if (obj == null) {
             // FIXME weakref.rb also does caller(2) here for the backtrace
             throw newRefError("Illegal Reference - probably recycled");
         }
         
         return obj;
     }
     
     @JRubyMethod(name = "new", required = 1, meta = true)
     public static IRubyObject newInstance(IRubyObject clazz, IRubyObject arg) {
         WeakRef weakRef = (WeakRef)((RubyClass)clazz).allocate();
         
         weakRef.callInit(new IRubyObject[] {arg}, Block.NULL_BLOCK);
         
         return weakRef;
     }
     
     @JRubyMethod(name = "initialize", required = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject obj) {
         ref = new WeakReference<IRubyObject>(obj);
         
         return callSuper(getRuntime().getCurrentContext(), new IRubyObject[] {obj}, Block.NULL_BLOCK);
     }
     
     @JRubyMethod(name = "weakref_alive?")
     public IRubyObject weakref_alive_p() {
         return ref.get() != null ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     private RaiseException newRefError(String message) {
         RubyException exception =
                 (RubyException)getRuntime().getClass("RefError").newInstance(
                 new IRubyObject[] {getRuntime().newString(message)}, Block.NULL_BLOCK);
         
         return new RaiseException(exception);
     }
 }
diff --git a/src/org/jruby/ext/socket/RubySocket.java b/src/org/jruby/ext/socket/RubySocket.java
index 5912ae5376..bd6c358832 100644
--- a/src/org/jruby/ext/socket/RubySocket.java
+++ b/src/org/jruby/ext/socket/RubySocket.java
@@ -1,315 +1,315 @@
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
 package org.jruby.ext.socket;
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubySocket extends RubyBasicSocket {
 
     public static class Service implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             runtime.defineClass("SocketError",runtime.fastGetClass("StandardError"), runtime.fastGetClass("StandardError").getAllocator());
             RubyBasicSocket.createBasicSocket(runtime);
             RubySocket.createSocket(runtime);
             RubyIPSocket.createIPSocket(runtime);
             RubyTCPSocket.createTCPSocket(runtime);
             RubyTCPServer.createTCPServer(runtime);
             RubyUDPSocket.createUDPSocket(runtime);
         }
     }
 
     private static ObjectAllocator SOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubySocket(runtime, klass);
         }
     };
 
     public static final int NI_DGRAM = 16;
     public static final int NI_MAXHOST = 1025;
     public static final int NI_MAXSERV = 32;
     public static final int NI_NAMEREQD = 4;
     public static final int NI_NOFQDN = 1;
     public static final int NI_NUMERICHOST = 2;
     public static final int NI_NUMERICSERV = 8;
 
     static void createSocket(Ruby runtime) {
         RubyClass rb_cSocket = runtime.defineClass("Socket", runtime.fastGetClass("BasicSocket"), SOCKET_ALLOCATOR);
         CallbackFactory cfact = runtime.callbackFactory(RubySocket.class);
         
         RubyModule rb_mConstants = rb_cSocket.defineModuleUnder("Constants");
         // we don't have to define any that we don't support; see socket.c
         
         rb_mConstants.fastSetConstant("SOCK_STREAM", runtime.newFixnum(1));
         rb_mConstants.fastSetConstant("SOCK_DGRAM", runtime.newFixnum(2));
         rb_mConstants.fastSetConstant("PF_UNSPEC", runtime.newFixnum(0));
         rb_mConstants.fastSetConstant("AF_UNSPEC", runtime.newFixnum(0));
         rb_mConstants.fastSetConstant("PF_INET", runtime.newFixnum(2));
         rb_mConstants.fastSetConstant("AF_INET", runtime.newFixnum(2));
         // mandatory constants we haven't implemented
         rb_mConstants.fastSetConstant("MSG_OOB", runtime.newFixnum(0x01));
         rb_mConstants.fastSetConstant("SOL_SOCKET", runtime.newFixnum(1));
         rb_mConstants.fastSetConstant("SOL_IP", runtime.newFixnum(0));
         rb_mConstants.fastSetConstant("SOL_TCP", runtime.newFixnum(6));
         rb_mConstants.fastSetConstant("SOL_UDP", runtime.newFixnum(17));
         rb_mConstants.fastSetConstant("IPPROTO_IP", runtime.newFixnum(0));
         rb_mConstants.fastSetConstant("IPPROTO_ICMP", runtime.newFixnum(1));
         rb_mConstants.fastSetConstant("IPPROTO_TCP", runtime.newFixnum(6));
         rb_mConstants.fastSetConstant("IPPROTO_UDP", runtime.newFixnum(17));
         //  IPPROTO_RAW = 255
         rb_mConstants.fastSetConstant("INADDR_ANY", runtime.newFixnum(0x00000000));
         rb_mConstants.fastSetConstant("INADDR_BROADCAST", runtime.newFixnum(0xffffffff));
         rb_mConstants.fastSetConstant("INADDR_LOOPBACK", runtime.newFixnum(0x7f000001));
         rb_mConstants.fastSetConstant("INADDR_UNSPEC_GROUP", runtime.newFixnum(0xe0000000));
         rb_mConstants.fastSetConstant("INADDR_ALLHOSTS_GROUP", runtime.newFixnum(0xe0000001));
         rb_mConstants.fastSetConstant("INADDR_MAX_LOCAL_GROUP", runtime.newFixnum(0xe00000ff));
         rb_mConstants.fastSetConstant("INADDR_NONE", runtime.newFixnum(0xffffffff));
         rb_mConstants.fastSetConstant("SO_REUSEADDR", runtime.newFixnum(2));
         rb_mConstants.fastSetConstant("SHUT_RD", runtime.newFixnum(0));
         rb_mConstants.fastSetConstant("SHUT_WR", runtime.newFixnum(1));
         rb_mConstants.fastSetConstant("SHUT_RDWR", runtime.newFixnum(2));
     
         // constants webrick crashes without
         rb_mConstants.fastSetConstant("AI_PASSIVE", runtime.newFixnum(1));
 
         // constants Rails > 1.1.4 ActiveRecord's default mysql adapter dies without during scaffold generation
         rb_mConstants.fastSetConstant("SO_KEEPALIVE", runtime.newFixnum(9));
     
         // drb needs defined
         rb_mConstants.fastSetConstant("TCP_NODELAY", runtime.newFixnum(1));
 
         // flags/limits used by Net::SSH
         rb_mConstants.fastSetConstant("NI_DGRAM", runtime.newFixnum(NI_DGRAM));
         rb_mConstants.fastSetConstant("NI_MAXHOST", runtime.newFixnum(NI_MAXHOST));
         rb_mConstants.fastSetConstant("NI_MAXSERV", runtime.newFixnum(NI_MAXSERV));
         rb_mConstants.fastSetConstant("NI_NAMEREQD", runtime.newFixnum(NI_NAMEREQD));
         rb_mConstants.fastSetConstant("NI_NOFQDN", runtime.newFixnum(NI_NOFQDN));
         rb_mConstants.fastSetConstant("NI_NUMERICHOST", runtime.newFixnum(NI_NUMERICHOST));
         rb_mConstants.fastSetConstant("NI_NUMERICSERV", runtime.newFixnum(NI_NUMERICSERV));
        
         
         rb_cSocket.includeModule(rb_mConstants);
 
         rb_cSocket.getMetaClass().defineFastMethod("gethostname", cfact.getFastSingletonMethod("gethostname"));
         rb_cSocket.getMetaClass().defineFastMethod("gethostbyaddr", cfact.getFastOptSingletonMethod("gethostbyaddr"));
         rb_cSocket.getMetaClass().defineFastMethod("gethostbyname", cfact.getFastSingletonMethod("gethostbyname", IRubyObject.class));
         rb_cSocket.getMetaClass().defineFastMethod("getaddrinfo", cfact.getFastOptSingletonMethod("getaddrinfo"));
         rb_cSocket.getMetaClass().defineFastMethod("getnameinfo", cfact.getFastOptSingletonMethod("getnameinfo"));
     }
     
     public RubySocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     private static RuntimeException sockerr(IRubyObject recv, String msg) {
         return new RaiseException(recv.getRuntime(), recv.getRuntime().fastGetClass("SocketError"), msg, true);
     }
 
     public static IRubyObject gethostname(IRubyObject recv) {
         try {
             return recv.getRuntime().newString(InetAddress.getLocalHost().getHostName());
         } catch(UnknownHostException e) {
             try {
                 return recv.getRuntime().newString(InetAddress.getByAddress(new byte[]{0,0,0,0}).getHostName());
             } catch(UnknownHostException e2) {
                 throw sockerr(recv, "gethostname: name or service not known");
             }
         }
     }
 
     private static InetAddress intoAddress(IRubyObject recv, String s) {
         try {
             byte[] bs = ByteList.plain(s);
             return InetAddress.getByAddress(bs);
         } catch(Exception e) {
             throw sockerr(recv, "strtoaddr: " + e.toString());
         }
     }
 
     private static String intoString(IRubyObject recv, InetAddress as) {
         try {
             return new String(ByteList.plain(as.getAddress()));
         } catch(Exception e) {
             throw sockerr(recv, "addrtostr: " + e.toString());
         }
     }
 
     public static IRubyObject gethostbyaddr(IRubyObject recv, IRubyObject[] args) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,2);
         Ruby runtime = recv.getRuntime();
         IRubyObject[] ret = new IRubyObject[4];
         ret[0] = runtime.newString(intoAddress(recv,args[0].convertToString().toString()).getCanonicalHostName());
         ret[1] = runtime.newArray();
         ret[2] = runtime.newFixnum(2); // AF_INET
         ret[3] = args[0];
         return runtime.newArrayNoCopy(ret);
     }
 
     public static IRubyObject gethostbyname(IRubyObject recv, IRubyObject hostname) {
         try {
             InetAddress addr = InetAddress.getByName(hostname.convertToString().toString());
             Ruby runtime = recv.getRuntime();
             IRubyObject[] ret = new IRubyObject[4];
             ret[0] = runtime.newString(addr.getCanonicalHostName());
             ret[1] = runtime.newArray();
             ret[2] = runtime.newFixnum(2); // AF_INET
             ret[3] = runtime.newString(intoString(recv,addr));
             return runtime.newArrayNoCopy(ret);
         } catch(UnknownHostException e) {
             throw sockerr(recv, "gethostbyname: name or service not known");
         }
     }
 
     //def self.getaddrinfo(host, port, family = nil, socktype = nil, protocol = nil, flags = nil)
     public static IRubyObject getaddrinfo(IRubyObject recv, IRubyObject[] args) {
         args = Arity.scanArgs(recv.getRuntime(),args,2,4);
         try {
             Ruby r = recv.getRuntime();
             IRubyObject host = args[0];
             IRubyObject port = args[1];
             //IRubyObject family = args[2];
             IRubyObject socktype = args[3];
             //IRubyObject protocol = args[4];
             //IRubyObject flags = args[5];
             boolean sock_stream = true;
             boolean sock_dgram = true;
             if(!socktype.isNil()) {
                 int val = RubyNumeric.fix2int(socktype);
                 if(val == 1) {
                     sock_dgram = false;
                 } else if(val == 2) {
                     sock_stream = false;
                 }
             }
             InetAddress[] addrs = InetAddress.getAllByName(host.isNil() ? null : host.convertToString().toString());
             List l = new ArrayList();
             for(int i=0;i<addrs.length;i++) {
                 IRubyObject[] c;
                 if(sock_stream) {
                     c = new IRubyObject[7];
                     c[0] = r.newString("AF_INET");
                     c[1] = port;
                     c[2] = r.newString(addrs[i].getCanonicalHostName());
                     c[3] = r.newString(addrs[i].getHostAddress());
                     c[4] = r.newFixnum(2); // PF_INET
                     c[5] = r.newFixnum(1); // SOCK_STREAM
                     c[6] = r.newFixnum(6); // Protocol TCP
                     l.add(r.newArrayNoCopy(c));
                 }
                 if(sock_dgram) {
                     c = new IRubyObject[7];
                     c[0] = r.newString("AF_INET");
                     c[1] = port;
                     c[2] = r.newString(addrs[i].getCanonicalHostName());
                     c[3] = r.newString(addrs[i].getHostAddress());
                     c[4] = r.newFixnum(2); // PF_INET
                     c[5] = r.newFixnum(2); // SOCK_DRGRAM
                     c[6] = r.newFixnum(17); // Protocol UDP
                     l.add(r.newArrayNoCopy(c));
                 }
             }
             return r.newArray(l);
         } catch(UnknownHostException e) {
             throw sockerr(recv, "getaddrinfo: name or service not known");
         }
     }
 
     // FIXME: may need to broaden for IPV6 IP address strings
     private static final Pattern STRING_ADDRESS_PATTERN =
         Pattern.compile("((.*)\\/)?([\\.0-9]+)(:([0-9]+))?");
     
     private static final int HOST_GROUP = 3;
     private static final int PORT_GROUP = 5;
     
     public static IRubyObject getnameinfo(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int argc = Arity.checkArgumentCount(runtime, args, 1, 2);
         int flags = argc == 2 ? RubyNumeric.num2int(args[1]) : 0;
         IRubyObject arg0 = args[0];
 
         String host, port;
         if (arg0 instanceof RubyArray) {
             List list = ((RubyArray)arg0).getList();
             int len = list.size();
             if (len < 3 || len > 4) {
                 throw runtime.newArgumentError("array size should be 3 or 4, "+len+" given");
             }
             // TODO: validate port as numeric
             host = list.get(2).toString();
             port = list.get(1).toString();
         } else if (arg0 instanceof RubyString) {
             String arg = ((RubyString)arg0).toString();
             Matcher m = STRING_ADDRESS_PATTERN.matcher(arg);
             if (!m.matches()) {
                 throw runtime.newArgumentError("invalid address string");
             }
             if ((host = m.group(HOST_GROUP)) == null || host.length() == 0 ||
                     (port = m.group(PORT_GROUP)) == null || port.length() == 0) {
                 throw runtime.newArgumentError("invalid address string");
             }
         } else {
             throw runtime.newArgumentError("invalid args");
         }
         
         InetAddress addr;
         try {
             addr = InetAddress.getByName(host);
         } catch (UnknownHostException e) {
             throw sockerr(recv, "unknown host: "+ host);
         }
         if ((flags & NI_NUMERICHOST) == 0) {
             host = addr.getCanonicalHostName();
         } else {
             host = addr.getHostAddress();
         }
         return runtime.newArray(runtime.newString(host), runtime.newString(port));
 
     }
 }// RubySocket
diff --git a/src/org/jruby/libraries/BigDecimalLibrary.java b/src/org/jruby/libraries/BigDecimalLibrary.java
index f7ba1ed94d..dfae081b7b 100644
--- a/src/org/jruby/libraries/BigDecimalLibrary.java
+++ b/src/org/jruby/libraries/BigDecimalLibrary.java
@@ -1,47 +1,47 @@
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
  * $Id: $
  */
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.RubyBigDecimal;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @version $Revision: 1.2 $
  */
 public class BigDecimalLibrary implements Library {
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         RubyBigDecimal.createBigDecimal(runtime);
     }
 }// BigDecimalLibrary
diff --git a/src/org/jruby/libraries/DigestLibrary.java b/src/org/jruby/libraries/DigestLibrary.java
index fe71352def..bda37380d8 100644
--- a/src/org/jruby/libraries/DigestLibrary.java
+++ b/src/org/jruby/libraries/DigestLibrary.java
@@ -1,66 +1,66 @@
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class DigestLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         org.jruby.RubyDigest.createDigest(runtime);
     }
 
     public static class MD5 implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             org.jruby.RubyDigest.createDigestMD5(runtime);
         }
     }
 
     public static class RMD160 implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             org.jruby.RubyDigest.createDigestRMD160(runtime);
         }
     }
 
     public static class SHA1 implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             org.jruby.RubyDigest.createDigestSHA1(runtime);
         }
     }
 
     public static class SHA2 implements Library {
-        public void load(final Ruby runtime) throws IOException {
+        public void load(final Ruby runtime, boolean wrap) throws IOException {
             org.jruby.RubyDigest.createDigestSHA2(runtime);
         }
     }
 }// DigestLibrary
diff --git a/src/org/jruby/libraries/EnumeratorLibrary.java b/src/org/jruby/libraries/EnumeratorLibrary.java
index 98f865b700..f03169f0f2 100644
--- a/src/org/jruby/libraries/EnumeratorLibrary.java
+++ b/src/org/jruby/libraries/EnumeratorLibrary.java
@@ -1,41 +1,41 @@
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyEnumerator;
 
 import org.jruby.runtime.load.Library;
 
 public class EnumeratorLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         RubyEnumerator.defineEnumerator(runtime);
     }
 }
diff --git a/src/org/jruby/libraries/FiberLibrary.java b/src/org/jruby/libraries/FiberLibrary.java
index 57fd9b5552..3cdbccb22f 100644
--- a/src/org/jruby/libraries/FiberLibrary.java
+++ b/src/org/jruby/libraries/FiberLibrary.java
@@ -1,143 +1,143 @@
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
 
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyObject;
 import org.jruby.RubyClass;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A basic implementation of Ruby 1.9 Fiber library.
  */
 public class FiberLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         Fiber.setup(runtime);
     }
 
     public static class Fiber extends RubyObject {
         private Block block;
         private Object yieldLock = new Object();
         private IRubyObject result;
         private Thread thread;
         private boolean alive = false;
         
         public static Fiber newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             Fiber result = new Fiber(recv.getRuntime(), (RubyClass)recv);
             result.initialize(args, block);
             return result;
         }
         
         public IRubyObject initialize(final IRubyObject[] args, Block block) {
             this.block = block;
             final Ruby runtime = getRuntime();
             this.result = runtime.getNil();
             this.thread = new Thread() {
                 public void run() {
                     synchronized (yieldLock) {
                         alive = true;
                         ThreadContext context = runtime.getCurrentContext();
                         context.setFiber(Fiber.this);
                         try {
                             result = Fiber.this.block.yield(runtime.getCurrentContext(), result, null, null, true);
                         } finally {
                             yieldLock.notify();
                         }
                     }
                 }
             };
             // FIXME: Is this appropriate? Should still-running fibers just die on exit?
             this.thread.setDaemon(true);
             return this;
         }
 
         public Fiber(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         public static void setup(Ruby runtime) {
             RubyClass cFiber = runtime.defineClass("Fiber", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
             CallbackFactory cb = runtime.callbackFactory(Fiber.class);
             cFiber.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
             cFiber.defineFastMethod("resume", cb.getFastOptMethod("resume"));
             // FIXME: Not sure what the semantics of transfer are
             //cFiber.defineFastMethod("transfer", cb.getFastOptMethod("transfer"));
             cFiber.defineFastMethod("alive?", cb.getFastMethod("alive_p"));
             cFiber.getMetaClass().defineFastMethod("yield", cb.getFastSingletonMethod("yield", IRubyObject.class));
             cFiber.getMetaClass().defineFastMethod("current", cb.getFastSingletonMethod("current"));
         }
 
         public IRubyObject resume(IRubyObject[] args) throws InterruptedException {
             synchronized (yieldLock) {
                 result = getRuntime().newArrayNoCopyLight(args);
                 if (!alive) {
                     thread.start();
                     yieldLock.wait();
                 } else {
                     yieldLock.notify();
                     yieldLock.wait();
                 }
             }
             return result;
         }
 
         public IRubyObject transfer(IRubyObject[] args) throws InterruptedException {
             synchronized (yieldLock) {
                 yieldLock.notify();
                 yieldLock.wait();
             }
             return result;
         }
 
         public IRubyObject alive_p() {
             return getRuntime().newBoolean(alive);
         }
 
         public static IRubyObject yield(IRubyObject recv, IRubyObject value) throws InterruptedException {
             Fiber fiber = recv.getRuntime().getCurrentContext().getFiber();
             fiber.result = value;
             synchronized (fiber.yieldLock) {
                 fiber.yieldLock.notify();
                 fiber.yieldLock.wait();
             }
             return recv.getRuntime().getNil();
         }
 
         public static IRubyObject current(IRubyObject recv) {
             return recv.getRuntime().getCurrentContext().getFiber();
         }
     }
 }
diff --git a/src/org/jruby/libraries/IConvLibrary.java b/src/org/jruby/libraries/IConvLibrary.java
index b9363879a3..8b56da7d14 100644
--- a/src/org/jruby/libraries/IConvLibrary.java
+++ b/src/org/jruby/libraries/IConvLibrary.java
@@ -1,42 +1,42 @@
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
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyIconv;
 import org.jruby.runtime.load.Library;
 
 public class IConvLibrary implements Library {
 
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         RubyIconv.createIconv(runtime);
     }
 
 }
diff --git a/src/org/jruby/libraries/IOWaitLibrary.java b/src/org/jruby/libraries/IOWaitLibrary.java
index ed7659f888..d58136bac5 100644
--- a/src/org/jruby/libraries/IOWaitLibrary.java
+++ b/src/org/jruby/libraries/IOWaitLibrary.java
@@ -1,47 +1,47 @@
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
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author Nick Sieger
  */
 public class IOWaitLibrary implements Library {
-    public void load(Ruby runtime) {
+    public void load(Ruby runtime, boolean wrap) {
         RubyClass ioClass = runtime.getIO();
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyIO.class);
         ioClass.defineFastMethod("ready?", callbackFactory.getFastMethod("ready"));
         ioClass.defineFastMethod("wait", callbackFactory.getFastMethod("io_wait"));
     }
 }
diff --git a/src/org/jruby/libraries/JRubyLibrary.java b/src/org/jruby/libraries/JRubyLibrary.java
index fcd1785bcc..81d5da8b0e 100644
--- a/src/org/jruby/libraries/JRubyLibrary.java
+++ b/src/org/jruby/libraries/JRubyLibrary.java
@@ -1,41 +1,41 @@
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyJRuby;
 import org.jruby.runtime.load.Library;
 
 public class JRubyLibrary implements Library {
 
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         RubyJRuby.createJRuby(runtime);
     }
 }
diff --git a/src/org/jruby/libraries/NKFLibrary.java b/src/org/jruby/libraries/NKFLibrary.java
index d16ad9660d..2ae21fa6aa 100644
--- a/src/org/jruby/libraries/NKFLibrary.java
+++ b/src/org/jruby/libraries/NKFLibrary.java
@@ -1,14 +1,14 @@
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyNKF;
 import org.jruby.runtime.load.Library;
 
 public class NKFLibrary implements Library {
 
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         RubyNKF.createNKF(runtime);
     }
 }
diff --git a/src/org/jruby/libraries/RbConfigLibrary.java b/src/org/jruby/libraries/RbConfigLibrary.java
index 3844428d27..7a25e69528 100644
--- a/src/org/jruby/libraries/RbConfigLibrary.java
+++ b/src/org/jruby/libraries/RbConfigLibrary.java
@@ -1,154 +1,154 @@
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
-    public void load(Ruby runtime) {
+    public void load(Ruby runtime, boolean wrap) {
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
 
         String normalizedHome;
         if (Ruby.isSecurityRestricted()) {
             normalizedHome = "SECURITY RESTRICTED";
         } else {
             normalizedHome = new NormalizedFile(runtime.getJRubyHome()).getAbsolutePath();
         }
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
diff --git a/src/org/jruby/libraries/StringIOLibrary.java b/src/org/jruby/libraries/StringIOLibrary.java
index 74035a3167..5c664889a1 100644
--- a/src/org/jruby/libraries/StringIOLibrary.java
+++ b/src/org/jruby/libraries/StringIOLibrary.java
@@ -1,40 +1,40 @@
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.RubyStringIO;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 public class StringIOLibrary implements Library {
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         RubyStringIO.createStringIOClass(runtime);
     }
 }
diff --git a/src/org/jruby/libraries/StringScannerLibrary.java b/src/org/jruby/libraries/StringScannerLibrary.java
index 5831ca0695..22e57653f4 100644
--- a/src/org/jruby/libraries/StringScannerLibrary.java
+++ b/src/org/jruby/libraries/StringScannerLibrary.java
@@ -1,22 +1,22 @@
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyStringScanner;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author kscott
  *
  */
 public class StringScannerLibrary implements Library {
 
 	/**
 	 * @see org.jruby.runtime.load.Library#load(org.jruby.Ruby)
 	 */
-	public void load(Ruby runtime) throws IOException {
+	public void load(Ruby runtime, boolean wrap) throws IOException {
 		RubyStringScanner.createScannerClass(runtime);
 	}
 
 }
diff --git a/src/org/jruby/libraries/ThreadLibrary.java b/src/org/jruby/libraries/ThreadLibrary.java
index edd4b451f8..3b343485f7 100644
--- a/src/org/jruby/libraries/ThreadLibrary.java
+++ b/src/org/jruby/libraries/ThreadLibrary.java
@@ -1,361 +1,361 @@
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
  * Copyright (C) 2006 MenTaLguY <mental@rydia.net>
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
 
 /* Portions loosely based on public-domain JSR-166 code by Doug Lea et al. */
 
 package org.jruby.libraries;
 
 import java.io.IOException;
 import java.util.LinkedList;
 
 import org.jruby.Ruby;
 import org.jruby.RubyObject;
 import org.jruby.RubyClass;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyThread;
 import org.jruby.RubyInteger;
 import org.jruby.RubyNumeric;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author <a href="mailto:mental@rydia.net">MenTaLguY</a>
  */
 public class ThreadLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         Mutex.setup(runtime);
         ConditionVariable.setup(runtime);
         Queue.setup(runtime);
         SizedQueue.setup(runtime);
     }
 
     public static class Mutex extends RubyObject {
         private RubyThread owner = null;
 
         public static Mutex newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             Mutex result = new Mutex(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args, block);
             return result;
         }
 
         public Mutex(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         public static void setup(Ruby runtime) {
             RubyClass cMutex = runtime.defineClass("Mutex", runtime.getObject(), new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                     return new Mutex(runtime, klass);
                 }
             });
             CallbackFactory cb = runtime.callbackFactory(Mutex.class);
             cMutex.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
             cMutex.defineFastMethod("locked?", cb.getFastMethod("locked_p"));
             cMutex.defineFastMethod("try_lock", cb.getFastMethod("try_lock"));
             cMutex.defineFastMethod("lock", cb.getFastMethod("lock"));
             cMutex.defineFastMethod("unlock", cb.getFastMethod("unlock"));
             cMutex.defineMethod("synchronize", cb.getMethod("synchronize"));
         }
 
         public synchronized RubyBoolean locked_p() {
             return ( owner != null ? getRuntime().getTrue() : getRuntime().getFalse() );
         }
 
         public RubyBoolean try_lock() throws InterruptedException {
             //if (Thread.interrupted()) {
             //    throw new InterruptedException();
             //}
             synchronized (this) {
                 if ( owner != null ) {
                     return getRuntime().getFalse();
                 }
                 lock();
             }
             return getRuntime().getTrue();
         }
 
         public IRubyObject lock() throws InterruptedException {
             //if (Thread.interrupted()) {
             //    throw new InterruptedException();
             //}
             synchronized (this) {
                 try {
                     while ( owner != null ) {
                         wait();
                     }
                     owner = getRuntime().getCurrentContext().getThread();
                 } catch (InterruptedException ex) {
                     if ( owner == null ) {
                         notify();
                     }
                     throw ex;
                 }
             }
             return this;
         }
 
         public synchronized RubyBoolean unlock() {
             if ( owner != null ) {
                 owner = null;
                 notify();
                 return getRuntime().getTrue();
             } else {
                 return getRuntime().getFalse();
             }
         }
 
         public IRubyObject synchronize(Block block) throws InterruptedException {
             try {
                 lock();
                 return block.yield(getRuntime().getCurrentContext(), null);
             } finally {
                 unlock();
             }
         }
     }
 
     public static class ConditionVariable extends RubyObject {
         public static ConditionVariable newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             ConditionVariable result = new ConditionVariable(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args, block);
             return result;
         }
 
         public ConditionVariable(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         public static void setup(Ruby runtime) {
             RubyClass cConditionVariable = runtime.defineClass("ConditionVariable", runtime.getObject(), new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                     return new ConditionVariable(runtime, klass);
                 }
             });
             CallbackFactory cb = runtime.callbackFactory(ConditionVariable.class);
             cConditionVariable.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
             cConditionVariable.defineFastMethod("wait", cb.getFastMethod("wait_ruby", Mutex.class));
             cConditionVariable.defineFastMethod("broadcast", cb.getFastMethod("broadcast"));
             cConditionVariable.defineFastMethod("signal", cb.getFastMethod("signal"));
         }
 
         public IRubyObject wait_ruby(Mutex mutex) throws InterruptedException {
             if (Thread.interrupted()) {
                 throw new InterruptedException();
             }
             try {
                 synchronized (this) {
                     mutex.unlock();
                     try {
                         wait();
                     } catch (InterruptedException e) {
                         notify();
                         throw e;
                     }
                 }
             } finally {
                 mutex.lock();
             }
             return getRuntime().getNil();
         }
 
         public synchronized IRubyObject broadcast() {
             notifyAll();
             return getRuntime().getNil();
         }
 
         public synchronized IRubyObject signal() {
             notify();
             return getRuntime().getNil();
         }
     }
 
     public static class Queue extends RubyObject {
         private LinkedList entries;
 
         public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             Queue result = new Queue(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args, block);
             return result;
         }
 
         public Queue(Ruby runtime, RubyClass type) {
             super(runtime, type);
             entries = new LinkedList();
         }
 
         public static void setup(Ruby runtime) {
             RubyClass cQueue = runtime.defineClass("Queue", runtime.getObject(), new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                     return new Queue(runtime, klass);
                 }
             });
             CallbackFactory cb = runtime.callbackFactory(Queue.class);
             cQueue.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
 
             cQueue.defineFastMethod("clear", cb.getFastMethod("clear"));
             cQueue.defineFastMethod("empty?", cb.getFastMethod("empty_p"));
             cQueue.defineFastMethod("length", cb.getFastMethod("length"));
             cQueue.defineFastMethod("num_waiting", cb.getFastMethod("num_waiting"));
             cQueue.defineFastMethod("pop", cb.getFastOptMethod("pop"));
             cQueue.defineFastMethod("push", cb.getFastMethod("push", IRubyObject.class));
             
             cQueue.defineAlias("<<", "push");
             cQueue.defineAlias("deq", "pop");
             cQueue.defineAlias("shift", "pop");
             cQueue.defineAlias("size", "length");
             cQueue.defineAlias("enq", "push");
         }
 
         public synchronized IRubyObject clear() {
             entries.clear();
             return getRuntime().getNil();
         }
 
         public synchronized RubyBoolean empty_p() {
             return ( entries.size() == 0 ? getRuntime().getTrue() : getRuntime().getFalse() );
         }
 
         public synchronized RubyNumeric length() {
             return RubyNumeric.int2fix(getRuntime(), entries.size());
         }
 
         public RubyNumeric num_waiting() { return getRuntime().newFixnum(0); }
 
         public synchronized IRubyObject pop(IRubyObject[] args) {
             boolean should_block = true;
             if ( Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1 ) {
                 should_block = !args[0].isTrue();
             }
             if ( !should_block && entries.size() == 0 ) {
                 throw new RaiseException(getRuntime(), getRuntime().fastGetClass("ThreadError"), "queue empty", false);
             }
             while ( entries.size() == 0 ) {
                 try {
                     wait();
                 } catch (InterruptedException e) {
                 }
             }
             return (IRubyObject)entries.removeFirst();
         }
 
         public synchronized IRubyObject push(IRubyObject value) {
             entries.addLast(value);
             notify();
             return getRuntime().getNil();
         }
     }
 
 
     public static class SizedQueue extends Queue {
         private int capacity;
 
         public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             SizedQueue result = new SizedQueue(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args, block);
             return result;
         }
 
         public SizedQueue(Ruby runtime, RubyClass type) {
             super(runtime, type);
             capacity = 1;
         }
 
         public static void setup(Ruby runtime) {
             RubyClass cSizedQueue = runtime.defineClass("SizedQueue", runtime.fastGetClass("Queue"), new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                     return new SizedQueue(runtime, klass);
                 }
             });
             CallbackFactory cb = runtime.callbackFactory(SizedQueue.class);
             cSizedQueue.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
 
             cSizedQueue.defineFastMethod("initialize", cb.getFastMethod("max_set", RubyInteger.class));
 
             cSizedQueue.defineFastMethod("clear", cb.getFastMethod("clear"));
             cSizedQueue.defineFastMethod("max", cb.getFastMethod("max"));
             cSizedQueue.defineFastMethod("max=", cb.getFastMethod("max_set", RubyInteger.class));
             cSizedQueue.defineFastMethod("pop", cb.getFastOptMethod("pop"));
             cSizedQueue.defineFastMethod("push", cb.getFastMethod("push", IRubyObject.class));
 
             cSizedQueue.defineAlias("<<", "push");
             cSizedQueue.defineAlias("deq", "pop");
             cSizedQueue.defineAlias("shift", "pop");
         }
 
         public synchronized IRubyObject clear() {
             super.clear();
             notifyAll();
             return getRuntime().getNil();
         }
 
         public synchronized RubyNumeric max() {
             return RubyNumeric.int2fix(getRuntime(), capacity);
         }
 
         public synchronized IRubyObject max_set(RubyInteger arg) {
             int new_capacity = RubyNumeric.fix2int(arg);
             if ( new_capacity <= 0 ) {
                 getRuntime().newArgumentError("queue size must be positive");
             }
             int difference;
             if ( new_capacity > capacity ) {
                 difference = new_capacity - capacity;
             } else {
                 difference = 0;
             }
             capacity = new_capacity;
             if ( difference > 0 ) {
                 notifyAll();
             }
             return getRuntime().getNil();
         }
 
         public synchronized IRubyObject pop(IRubyObject args[]) {
             IRubyObject result = super.pop(args);
             notifyAll();
             return result;
         }
 
         public synchronized IRubyObject push(IRubyObject value) {
             while ( RubyNumeric.fix2int(length()) >= capacity ) {
                 try {
                     wait();
                 } catch (InterruptedException e) {
                 }
             }
             super.push(value);
             notifyAll();
             return getRuntime().getNil();
         }
     }
 }
diff --git a/src/org/jruby/libraries/YamlLibrary.java b/src/org/jruby/libraries/YamlLibrary.java
index 56daf916d5..e7ae9fe66d 100644
--- a/src/org/jruby/libraries/YamlLibrary.java
+++ b/src/org/jruby/libraries/YamlLibrary.java
@@ -1,47 +1,47 @@
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
  * $Id: $
  */
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.RubyYAML;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @version $Revision: $
  */
 public class YamlLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         RubyYAML.createYAMLModule(runtime);
     }
 }// YamlLibrary
diff --git a/src/org/jruby/libraries/ZlibLibrary.java b/src/org/jruby/libraries/ZlibLibrary.java
index b276b7a337..5cbbc381a2 100644
--- a/src/org/jruby/libraries/ZlibLibrary.java
+++ b/src/org/jruby/libraries/ZlibLibrary.java
@@ -1,40 +1,40 @@
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.RubyZlib;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 public class ZlibLibrary implements Library {
-    public void load(final Ruby runtime) throws IOException {
+    public void load(final Ruby runtime, boolean wrap) throws IOException {
         RubyZlib.createZlibModule(runtime);
     }
 }
diff --git a/src/org/jruby/runtime/load/ClassExtensionLibrary.java b/src/org/jruby/runtime/load/ClassExtensionLibrary.java
index 450c1b1b9d..1d0ed7b77e 100644
--- a/src/org/jruby/runtime/load/ClassExtensionLibrary.java
+++ b/src/org/jruby/runtime/load/ClassExtensionLibrary.java
@@ -1,56 +1,56 @@
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
 package org.jruby.runtime.load;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 
 /**
  * The ClassExtensionLibrary wraps a class which implements BasicLibraryService,
  * and when asked to load the service, does a basicLoad of the BasicLibraryService.
  * 
  * When the time comes to add other loading mechanisms for loading a class, this
  * is the place where they will be added. The load method will check interface
  * you can load a class with, and do the right thing.
  */
 public class ClassExtensionLibrary implements Library {
     private Class theClass;
     public ClassExtensionLibrary(Class extension) {
         theClass = extension;
     }
 
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         if(BasicLibraryService.class.isAssignableFrom(theClass)) {
             try {
                 ((BasicLibraryService)theClass.newInstance()).basicLoad(runtime);
             } catch(final Exception ee) {
                 throw new RuntimeException(ee.getMessage(),ee);
             }
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/ExternalScript.java b/src/org/jruby/runtime/load/ExternalScript.java
index f3cb4e31dd..623ea05be6 100644
--- a/src/org/jruby/runtime/load/ExternalScript.java
+++ b/src/org/jruby/runtime/load/ExternalScript.java
@@ -1,59 +1,59 @@
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
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2005 Thomas E. Enebo <enebo@acm.org>
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
 
 import java.io.BufferedInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import org.jruby.Ruby;
 
 public class ExternalScript implements Library {
     private final LoadServiceResource resource;
     
     public ExternalScript(LoadServiceResource resource, String name) {
         this.resource = resource;
     }
 
-    public void load(Ruby runtime) {
+    public void load(Ruby runtime, boolean wrap) {
         try {
             InputStream in = new BufferedInputStream(resource.getURL().openStream());
             if (runtime.getInstanceConfig().getCompileMode().shouldPrecompileAll()) {
-                runtime.compileAndLoadFile(resource.getName(), in);
+                runtime.compileAndLoadFile(resource.getName(), in, wrap);
             } else {
-                runtime.loadFile(resource.getName(), in);
+                runtime.loadFile(resource.getName(), in, wrap);
             }
             // FIXME: This should be in finally
             in.close();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/JarredScript.java b/src/org/jruby/runtime/load/JarredScript.java
index d7770cad8f..3299b67670 100644
--- a/src/org/jruby/runtime/load/JarredScript.java
+++ b/src/org/jruby/runtime/load/JarredScript.java
@@ -1,96 +1,96 @@
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
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2005 Thomas E. Enebo <enebo@acm.org>
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
 
 import java.io.BufferedInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.URL;
 import java.util.jar.JarEntry;
 import java.util.jar.JarInputStream;
 import java.util.jar.Manifest;
 
 import org.jruby.Ruby;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Loading of Ruby scripts packaged in Jar files.
  *
  * Usually the Ruby scripts are accompanied by Java class files in the Jar.
  *
  */
 public class JarredScript implements Library {
     private final LoadServiceResource resource;
 
     public JarredScript(LoadServiceResource resource) {
         this.resource = resource;
     }
 
     public LoadServiceResource getResource() {
         return this.resource;
     }
 
-    public void load(Ruby runtime) {
+    public void load(Ruby runtime, boolean wrap) {
         URL jarFile = resource.getURL();
 
         // Make Java class files in the jar reachable from Ruby
         runtime.getJRubyClassLoader().addURL(jarFile);
 
         try {
             JarInputStream in = new JarInputStream(new BufferedInputStream(jarFile.openStream()));
 
             Manifest mf = in.getManifest();
             if (mf != null) {
                 String rubyInit = mf.getMainAttributes().getValue("Ruby-Init");
                 if (rubyInit != null) {
                     JarEntry entry = in.getNextJarEntry();
                     while (entry != null && !entry.getName().equals(rubyInit)) {
                         entry = in.getNextJarEntry();
                     }
                     if (entry != null) {
                         IRubyObject old = runtime.getGlobalVariables().isDefined("$JAR_URL") ? runtime.getGlobalVariables().get("$JAR_URL") : runtime.getNil();
                         try {
                             runtime.getGlobalVariables().set("$JAR_URL", runtime.newString("jar:" + jarFile + "!/"));
-                            runtime.loadFile("init", in);
+                            runtime.loadFile("init", in, wrap);
                         } finally {
                             runtime.getGlobalVariables().set("$JAR_URL", old);
                         }
                     }
                 }
             }
             in.close();
         } catch (FileNotFoundException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/JavaCompiledScript.java b/src/org/jruby/runtime/load/JavaCompiledScript.java
index f7b1b2e6fb..94656c3152 100644
--- a/src/org/jruby/runtime/load/JavaCompiledScript.java
+++ b/src/org/jruby/runtime/load/JavaCompiledScript.java
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
  * Copyright (C) 2007 The JRuby Community
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
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import org.jruby.Ruby;
 import org.jruby.ast.executable.Script;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 
 public class JavaCompiledScript implements Library {
     private final LoadServiceResource resource;
     
     public JavaCompiledScript(LoadServiceResource resource) {
         this.resource = resource;
     }
 
-    public void load(Ruby runtime) {
+    public void load(Ruby runtime, boolean wrap) {
         InputStream in = null;
         try {
             in = new BufferedInputStream(resource.getURL().openStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             byte[] buf = new byte[8196];
             int read = 0;
             while ((read = in.read(buf)) != -1) {
                 baos.write(buf, 0, read);
             }
             buf = baos.toByteArray();
             JRubyClassLoader jcl = runtime.getJRubyClassLoader();
             ClassReader cr = new ClassReader(buf);
             String className = cr.getClassName().replace('/', '.');
 
             Class clazz = null;
             try {
                 clazz = jcl.loadClass(className);
             } catch (ClassNotFoundException cnfe) {
                 clazz = jcl.defineClass(className, buf);
             }
             
             ((Script)clazz.newInstance()).load(runtime.getCurrentContext(), runtime.getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InstantiationException ie) {
             throw runtime.newLoadError("Error loading compiled script: " + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newLoadError("Error loading compiled script: " + iae);
         } finally {
             try {
                 in.close();
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         }
     }
 }
diff --git a/src/org/jruby/runtime/load/Library.java b/src/org/jruby/runtime/load/Library.java
index 2428c5e55a..c03f79e365 100644
--- a/src/org/jruby/runtime/load/Library.java
+++ b/src/org/jruby/runtime/load/Library.java
@@ -1,38 +1,38 @@
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.runtime.load;
 
 import java.io.IOException;
 
 import org.jruby.Ruby;
 
 public interface Library {
 
-    void load(Ruby runtime) throws IOException;
+    void load(Ruby runtime, boolean wrap) throws IOException;
 }
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 46ce0c4be8..bc6bdf353b 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,631 +1,631 @@
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
 
-    public void load(String file) {
+    public void load(String file, boolean wrap) {
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
-            library.load(runtime);
+            library.load(runtime, wrap);
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
             
-            library.load(runtime);
+            library.load(runtime, false);
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
                         canonicalEntry = new File(after+name).getCanonicalPath().substring(new File(".")
                                                              .getCanonicalPath().length()+1).replaceAll("\\\\","/");
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
                 if (!Ruby.isSecurityRestricted()) {
                     JRubyFile current = JRubyFile.create(JRubyFile.create(
                             runtime.getCurrentDirectory(),entry).getAbsolutePath(), name);
                     if (current.isFile()) {
                         try {
                             return new LoadServiceResource(current.toURI().toURL(), current.getPath());
                         } catch (MalformedURLException e) {
                             throw runtime.newIOErrorFromException(e);
                         }
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
diff --git a/src/org/jruby/util/BuiltinScript.java b/src/org/jruby/util/BuiltinScript.java
index 633df5803d..940e5b89db 100644
--- a/src/org/jruby/util/BuiltinScript.java
+++ b/src/org/jruby/util/BuiltinScript.java
@@ -1,60 +1,60 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.util;
 
 import java.io.BufferedInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 /**
  * Loading of Ruby scripts that are built into JRuby.
  */
 public class BuiltinScript implements Library {
     private final String name;
 
     public BuiltinScript(String name) {
         this.name = name;
     }
 
-    public void load(Ruby runtime) throws IOException {
+    public void load(Ruby runtime, boolean wrap) throws IOException {
         String resourceName = "/builtin/" + name + ".rb";
         InputStream in = getClass().getResourceAsStream(resourceName);
 
         if (in == null) throw runtime.newIOError("Resource not found: " + resourceName);
 
-        runtime.loadFile(name, new BufferedInputStream(in));
+        runtime.loadFile(name, new BufferedInputStream(in), wrap);
         
         in.close();
     }
 }
