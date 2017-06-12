diff --git a/src/org/jruby/IRuby.java b/src/org/jruby/IRuby.java
index 03f39a8180..dd5b0fdcd9 100644
--- a/src/org/jruby/IRuby.java
+++ b/src/org/jruby/IRuby.java
@@ -1,426 +1,426 @@
 package org.jruby;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 
 import org.jruby.ast.Node;
 import org.jruby.common.RubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public interface IRuby {
 
 	/**
 	 * Retrieve mappings of cached methods to where they have been cached.  When a cached
 	 * method needs to be invalidated this map can be used to remove all places it has been
 	 * cached.
 	 * 
 	 * @return the mappings of where cached methods have been stored
 	 */
 	public CacheMap getCacheMap();
 
     /**
      * The contents of the runtimeInformation map are dumped with the JVM exits if
      * JRuby has been invoked via the Main class. Otherwise these contents can be used
      * by embedders to track development-time runtime information such as profiling
      * or logging data during execution.
      * 
      * @return the runtimeInformation map
      * @see org.jruby.Main#runInterpreter
      */
     public Map getRuntimeInformation();
     
 	/**
 	 * Evaluates a script and returns a RubyObject.
 	 */
 	public IRubyObject evalScript(String script);
 
     public IRubyObject eval(Node node);
 
     public IRubyObject compileAndRun(Node node);
 
 	public RubyClass getObject();
     
     public RubyModule getKernel();
     
     public RubyClass getString();
     
     public RubyClass getFixnum();
     
     public IRubyObject getTmsStruct();
 
 	/** Returns the "true" instance from the instance pool.
 	 * @return The "true" instance.
 	 */
 	public RubyBoolean getTrue();
 
 	/** Returns the "false" instance from the instance pool.
 	 * @return The "false" instance.
 	 */
 	public RubyBoolean getFalse();
 
 	/** Returns the "nil" singleton instance.
 	 * @return "nil"
 	 */
 	public IRubyObject getNil();
     
     /**
      * @return The NilClass class
      */
     public RubyClass getNilClass();
 
 	public RubyModule getModule(String name);
 
 	/** Returns a class from the instance pool.
 	 *
 	 * @param name The name of the class.
 	 * @return The class.
 	 */
 	public RubyClass getClass(String name);
 
 	/** Define a new class with name 'name' and super class 'superClass'.
 	 *
 	 * MRI: rb_define_class / rb_define_class_id
 	 *
 	 */
 	public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator);
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef);
 
 	/** rb_define_module / rb_define_module_id
 	 *
 	 */
 	public RubyModule defineModule(String name);
 
 	public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef);
 
 	/**
 	 * In the current context, get the named module. If it doesn't exist a
 	 * new module is created.
 	 */
 	public RubyModule getOrCreateModule(String name);
 
 	/** Getter for property securityLevel.
 	 * @return Value of property securityLevel.
 	 */
 	public int getSafeLevel();
 
 	/** Setter for property securityLevel.
 	 * @param safeLevel New value of property securityLevel.
 	 */
 	public void setSafeLevel(int safeLevel);
 
 	public void secure(int level);
 
 	/** rb_define_global_const
 	 *
 	 */
 	public void defineGlobalConstant(String name, IRubyObject value);
 
 	public IRubyObject getTopConstant(String name);
     
     public String getCurrentDirectory();
     
     public void setCurrentDirectory(String dir);
     
     public long getStartTime();
     
     public InputStream getIn();
     public PrintStream getOut();
     public PrintStream getErr();
 
 	public boolean isClassDefined(String name);
 
     public boolean isObjectSpaceEnabled();
     
 	/** Getter for property rubyTopSelf.
 	 * @return Value of property rubyTopSelf.
 	 */
 	public IRubyObject getTopSelf();
 
     /** Getter for property isVerbose.
 	 * @return Value of property isVerbose.
 	 */
 	public IRubyObject getVerbose();
 
 	/** Setter for property isVerbose.
 	 * @param verbose New value of property isVerbose.
 	 */
 	public void setVerbose(IRubyObject verbose);
 
     /** Getter for property isDebug.
 	 * @return Value of property isDebug.
 	 */
 	public IRubyObject getDebug();
 
 	/** Setter for property isDebug.
 	 * @param verbose New value of property isDebug.
 	 */
 	public void setDebug(IRubyObject debug);
 
     public JavaSupport getJavaSupport();
 
     /** Defines a global variable
 	 */
 	public void defineVariable(final GlobalVariable variable);
 
 	/** defines a readonly global variable
 	 *
 	 */
 	public void defineReadonlyVariable(String name, IRubyObject value);
 
     /**
      * Parse the source specified by the reader and return an AST
      * 
      * @param content to be parsed
      * @param file the name of the file to be used in warnings/errors
      * @param scope that this content is being parsed under
      * @return the top of the AST
      */
 	public Node parse(Reader content, String file, DynamicScope scope);
 
     /**
      * Parse the source specified by the string and return an AST
      * 
      * @param content to be parsed
      * @param file the name of the file to be used in warnings/errors
      * @param scope that this content is being parsed under
      * @return the top of the AST
      */
 	public Node parse(String content, String file, DynamicScope scope);
 
 	public ThreadService getThreadService();
 
 	public ThreadContext getCurrentContext();
 
     /**
 	 * Returns the loadService.
 	 * @return ILoadService
 	 */
 	public LoadService getLoadService();
 
 	public RubyWarnings getWarnings();
 
 	public PrintStream getErrorStream();
 
 	public InputStream getInputStream();
 
 	public PrintStream getOutputStream();
 
 	public RubyModule getClassFromPath(String path);
 
 	/** Prints an error with backtrace to the error stream.
 	 *
 	 * MRI: eval.c - error_print()
 	 *
 	 */
 	public void printError(RubyException excp);
 
 	/** This method compiles and interprets a Ruby script.
 	 *
 	 *  It can be used if you want to use JRuby as a Macro language.
 	 *
 	 */
 	public void loadScript(RubyString scriptName, RubyString source,
 			boolean wrap);
 
 	public void loadScript(String scriptName, Reader source, boolean wrap);
 
 	public void loadNode(String scriptName, Node node, boolean wrap);
 
 	/** Loads, compiles and interprets a Ruby file.
 	 *  Used by Kernel#require.
 	 *
 	 *  @mri rb_load
 	 */
 	public void loadFile(File file, boolean wrap);
 
 	/** Call the trace function
 	 *
 	 * MRI: eval.c - call_trace_func
 	 *
 	 */
 	public void callTraceFunction(ThreadContext context, String event, ISourcePosition position,
 			IRubyObject self, String name, IRubyObject type);
 
 	public RubyProc getTraceFunction();
 
 	public void setTraceFunction(RubyProc traceFunction);
 
 	public GlobalVariables getGlobalVariables();
 	public void setGlobalVariables(GlobalVariables variables);
 
 	public CallbackFactory callbackFactory(Class type);
 
 	/**
 	 * Push block onto exit stack.  When runtime environment exits
 	 * these blocks will be evaluated.
 	 * 
 	 * @return the element that was pushed onto stack
 	 */
 	public IRubyObject pushExitBlock(RubyProc proc);
 
 	/**
 	 * Make sure Kernel#at_exit procs get invoked on runtime shutdown.
 	 * This method needs to be explicitly called to work properly.
 	 * I thought about using finalize(), but that did not work and I
 	 * am not sure the runtime will be at a state to run procs by the
 	 * time Ruby is going away.  This method can contain any other
 	 * things that need to be cleaned up at shutdown.  
 	 */
 	public void tearDown();
 
 	public RubyArray newArray();
 
 	public RubyArray newArray(IRubyObject object);
 
 	public RubyArray newArray(IRubyObject car, IRubyObject cdr);
 
 	public RubyArray newArray(IRubyObject[] objects);
 
 	public RubyArray newArray(List list);
 
 	public RubyArray newArray(int size);
 
 	public RubyBoolean newBoolean(boolean value);
 
-	public RubyFileStat newRubyFileStat(File file);
+	public RubyFileStat newRubyFileStat(String file);
 
 	public RubyFixnum newFixnum(long value);
 
 	public RubyFloat newFloat(double value);
 
 	public RubyNumeric newNumeric();
 
     public RubyProc newProc();
 
     public RubyBinding newBinding();
     public RubyBinding newBinding(Block block);
 
 	public RubyString newString(String string);
 
 	public RubySymbol newSymbol(String string);
 
     public RaiseException newArgumentError(String message);
     
     public RaiseException newArgumentError(int got, int expected);
     
     public RaiseException newErrnoEBADFError();
 
     public RaiseException newErrnoEINVALError();
 
     public RaiseException newErrnoENOENTError();
 
     public RaiseException newErrnoESPIPEError();
 
     public RaiseException newErrnoEBADFError(String message);
 
     public RaiseException newErrnoEINVALError(String message);
 
     public RaiseException newErrnoENOENTError(String message);
 
     public RaiseException newErrnoESPIPEError(String message);
 
     public RaiseException newErrnoEEXISTError(String message);
 
     public RaiseException newIndexError(String message);
     
     public RaiseException newSecurityError(String message);
     
     public RaiseException newSystemCallError(String message);
 
     public RaiseException newTypeError(String message);
     
     public RaiseException newThreadError(String message);
     
     public RaiseException newSyntaxError(String message);
 
     public RaiseException newRangeError(String message);
 
     public RaiseException newNotImplementedError(String message);
 
     public RaiseException newNoMethodError(String message, String name);
 
     public RaiseException newNameError(String message, String name);
 
     public RaiseException newLocalJumpError(String message);
 
     public RaiseException newLoadError(String message);
 
     public RaiseException newFrozenError(String objectType);
 
     public RaiseException newSystemStackError(String message);
     
     public RaiseException newSystemExit(int status);
     
     public RaiseException newIOError(String message);
     
     public RaiseException newIOErrorFromException(IOException ioe);
     
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType);
 
     public RaiseException newEOFError();
     
     public RaiseException newZeroDivisionError();
 
 	public RubySymbol.SymbolTable getSymbolTable();
 
 	public void setStackTraces(int stackTraces);
 
 	public int getStackTraces();
 
 	public void setRandomSeed(long randomSeed);
 
 	public long getRandomSeed();
 
 	public Random getRandom();
 
 	public ObjectSpace getObjectSpace();
 
 	public Hashtable getIoHandlers();
 
 	public RubyFixnum[] getFixnumCache();
 
 	public long incrementRandomSeedSequence();
 
     public RubyTime newTime(long milliseconds);
 
 	public boolean isGlobalAbortOnExceptionEnabled();
 
 	public void setGlobalAbortOnExceptionEnabled(boolean b);
 
 	public boolean isDoNotReverseLookupEnabled();
 
 	public void setDoNotReverseLookupEnabled(boolean b);
 
     public boolean registerInspecting(Object obj);
     public void unregisterInspecting(Object obj);
 
     public void setEncoding(String encoding);
     public String getEncoding();
 
     public Profile getProfile();
     
     public String getJRubyHome();
 }
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index d8116074a1..faddae8fac 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -336,1295 +336,1295 @@ public final class Ruby implements IRuby {
             JRubyClassLoader loader = new JRubyClassLoader();
             Class scriptClass = compiler.loadClasses(loader);
             
             Script script = (Script)scriptClass.newInstance();
             
             return script.run(tc, tc.getFrameSelf());
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject)je.getSecondaryData();
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
 
         if (tc.getWrapper() != null) {
             module.getSingletonClass().includeModule(tc.getWrapper());
             module.includeModule(tc.getWrapper());
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
 
     public void secure(int level) {
         if (level <= safeLevel) {
             throw newSecurityError("Insecure operation '" + getCurrentContext().getFrameLastFunc() + "' at level " + safeLevel);
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
      * @see org.jruby.IRuby#getRuntimeInformation
      */
     public Map getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable() : runtimeInformation;
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
         nilObject = new RubyNil(this);
         trueObject = new RubyBoolean(this, true);
         falseObject = new RubyBoolean(this, false);
 
         verbose = falseObject;
         debug = falseObject;
         
         javaSupport = new JavaSupport(this);
         
         initLibraries();
         
         tc.preInitCoreClasses();
 
         initCoreClasses();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
 
         initBuiltinClasses();
         
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
         
         // Load additional definitions and hacks from etc.rb
         getLoadService().smartLoad("builtin/etc.rb");
     }
 
     private void initLibraries() {
         loadService = new LoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(IRuby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                 }
             });
 
         registerBuiltin("socket.rb", new SocketLibrary());
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
         registerBuiltin("openssl.so", new RubyOpenSSL.Service());
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
         ObjectMetaClass objectMetaClass = new ObjectMetaClass(this);
         objectMetaClass.initializeClass();
         
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = new ModuleMetaClass(this, objectClass);
         objectClass.setConstant("Module", moduleClass);
         RubyClass classClass = RubyClass.newClassClass(this, moduleClass);
         objectClass.setConstant("Class", classClass);
 
         // I don't think the containment is correct here (parent cref)
         RubyClass metaClass = objectClass.makeMetaClass(classClass, objectMetaClass.getCRef());
         metaClass = moduleClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
         metaClass = classClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
 
         ((ObjectMetaClass) moduleClass).initializeBootstrapClass();
         
         kernelModule = RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         RubyClass.createClassClass(classClass);
 
         nilClass = RubyNil.createNilClass(this);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         stringClass = new StringMetaClass(this);
         stringClass.initializeClass();
         new SymbolMetaClass(this).initializeClass();
         if(profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if(profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if(profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
         
         if(profile.allowModule("Precision")) {
             RubyPrecision.createPrecisionModule(this);
         }
 
         if(profile.allowClass("Numeric")) {
             new NumericMetaClass(this).initializeClass();
         }
         if(profile.allowClass("Fixnum")) {
             new IntegerMetaClass(this).initializeClass();        
             fixnumClass = new FixnumMetaClass(this);
             fixnumClass.initializeClass();
         }
         new HashMetaClass(this).initializeClass();
         new IOMetaClass(this).initializeClass();
         new ArrayMetaClass(this).initializeClass();
         
         RubyClass structClass = null;
         if(profile.allowClass("Struct")) {
             structClass = RubyStruct.createStructClass(this);
         }
         
         if(profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                                                new IRubyObject[] {
                                                    newString("Tms"),
                                                    newSymbol("utime"),
                                                    newSymbol("stime"),
                                                    newSymbol("cutime"),
                                                    newSymbol("cstime")});
         }
         
         if(profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }        
 
         if(profile.allowClass("Bignum")) {
             new BignumMetaClass(this).initializeClass();
         }
         if(profile.allowClass("Binding")) {
             new BindingMetaClass(this).initializeClass();
         }
 
         if(profile.allowModule("Math")) {
             RubyMath.createMathModule(this); // depends on all numeric types
         }
         if(profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if(profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if(profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if(profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
 
         if(profile.allowClass("Proc")) {
             new ProcMetaClass(this).initializeClass();
         }
 
         if(profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
 
         if(profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if(profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
 
         if(profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
 
         if(profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
 
         if(profile.allowClass("File")) {
             new FileMetaClass(this).initializeClass(); // depends on IO, FileTest
         }
 
         if(profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if(profile.allowClass("Time")) {
             new TimeMetaClass(this).initializeClass();
         }
         if(profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
         
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
         RubyClass rangeError = null;
         if(profile.allowClass("StandardError")) {
             standardError = defineClass("StandardError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if(profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SystemExit")) {
             defineClass("SystemExit", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("Interrupt")) {
             defineClass("Interrupt", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("SignalException")) {
             defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError, scriptError.getAllocator());
         }
         if(profile.allowClass("NoMethodError")) {
             defineClass("NoMethodError", nameError, nameError.getAllocator());
         }
         if(profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError, ioError.getAllocator());
         }
         if(profile.allowClass("LocalJumpError")) {
             defineClass("LocalJumpError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError, standardError.getAllocator());
         }
         if(profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass, exceptionClass.getAllocator());
         }
         if(profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError, standardError.getAllocator());
         }
         // FIXME: Actually this somewhere
         if(profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError, rangeError.getAllocator());
         }
         if(profile.allowClass("NativeException")) {
             NativeException.createClass(this, runtimeError);
         }
         if(profile.allowClass("SystemCallError")) {
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if(profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
         }
        
         initErrnoErrors();
 
         if(profile.allowClass("Data")) {
             defineClass("Data", objectClass, objectClass.getAllocator());
         }
     }
 
     private void initBuiltinClasses() {
     	try {
 	        new BuiltinScript("FalseClass").load(this);
 	        new BuiltinScript("TrueClass").load(this);
     	} catch (IOException e) {
     		throw new Error("builtin scripts are missing", e);
     	}
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
         if (path.charAt(0) == '#') {
             throw newArgumentError("can't retrieve anonymous class " + path);
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
             if (tc.getFrameLastFunc() != null) {
             	errorStream.print(tc.getPosition());
             	errorStream.print(":in '" + tc.getFrameLastFunc() + '\'');
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
     public void loadScript(RubyString scriptName, RubyString source, boolean wrap) {
         loadScript(scriptName.toString(), new StringReader(source.toString()), wrap);
     }
 
     public void loadScript(String scriptName, Reader source, boolean wrap) {
         IRubyObject self = getTopSelf();
 
         ThreadContext context = getCurrentContext();
 
         RubyModule wrapper = context.getWrapper();
 
         try {
             if (!wrap) {
                 secure(4); /* should alter global state */
 
                 context.preNodeEval(null, objectClass, self);
             } else {
                 /* load in anonymous module as toplevel */
                 context.preNodeEval(RubyModule.newModule(this, null), context.getWrapper(), self);
                 
                 self = getTopSelf().rbClone();
                 self.extendObject(context.getRubyClass());
             }
 
         	Node node = parse(source, scriptName, null);
             self.eval(node);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
         		// Make sure this does not bubble out to java caller.
         	} else {
         		throw je;
         	}
         } finally {
             context.postNodeEval(wrapper);
         }
     }
 
     public void loadNode(String scriptName, Node node, boolean wrap) {
         IRubyObject self = getTopSelf();
 
         ThreadContext context = getCurrentContext();
 
         RubyModule wrapper = context.getWrapper();
 
         try {
             if (!wrap) {
                 secure(4); /* should alter global state */
                 
                 context.preNodeEval(null, objectClass, self);
             } else {
 
                 /* load in anonymous module as toplevel */
                 context.preNodeEval(RubyModule.newModule(this, null), context.getWrapper(), self);
                 
                 self = getTopSelf().rbClone();
                 self.extendObject(context.getRubyClass());
             }
             
             self.eval(node);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
         		// Make sure this does not bubble out to java caller.
         	} else {
         		throw je;
         	}
         } finally {
             context.postNodeEval(wrapper);
         }
     }
 
 
     /** Loads, compiles and interprets a Ruby file.
      *  Used by Kernel#require.
      *
      *  @mri rb_load
      */
     public void loadFile(File file, boolean wrap) {
         assert file != null : "No such file to load";
         try {
             BufferedReader source = new BufferedReader(new FileReader(file));
             loadScript(file.getPath().replace(File.separatorChar, '/'), source, wrap);
             source.close();
         } catch (IOException ioExcptn) {
             throw newIOErrorFromException(ioExcptn);
         }
     }
 
     /** Call the trace function
      *
      * MRI: eval.c - call_trace_func
      *
      */
     public void callTraceFunction(ThreadContext context, String event, ISourcePosition position, 
             IRubyObject self, String name, IRubyObject type) {
         if (traceFunction == null) return;
 
         if (!context.isWithinTrace()) {
             context.setWithinTrace(true);
 
             ISourcePosition savePosition = context.getPosition();
             String file = position.getFile();
 
             if (file == null) file = "(ruby)";
             if (type == null) type = getFalse(); 
 
             context.preTrace();
 
             try {
                 traceFunction.call(new IRubyObject[] { newString(event), newString(file),
                         newFixnum(position.getEndLine()),
                         name != null ? RubySymbol.newSymbol(this, name) : getNil(),
                         self != null ? self : getNil(),
                         type });
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
         return CallbackFactory.createFactory(type);
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
         getObjectSpace().finishFinalizers();
     }
     
     // new factory methods ------------------------------------------------------------------------
     
     public RubyArray newArray() {
     	return RubyArray.newArray(this);
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
     
     public RubyArray newArray(List list) {
     	return RubyArray.newArray(this, list);
     }
     
     public RubyArray newArray(int size) {
     	return RubyArray.newArray(this, size);
     }
     
     public RubyBoolean newBoolean(boolean value) {
     	return RubyBoolean.newBoolean(this, value);
     }
     
-    public RubyFileStat newRubyFileStat(File file) {
-    	return new RubyFileStat(this, JRubyFile.create(currentDirectory,file.getPath()));
+    public RubyFileStat newRubyFileStat(String file) {
+        return (RubyFileStat)getClass("File").getClass("Stat").callMethod(getCurrentContext(),"new",newString(file));
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
     
     public RubyProc newProc() {
     	return RubyProc.newProc(this, false);
     }
     
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this);
     }
     
     public RubyBinding newBinding(Block block) {
     	return RubyBinding.newBinding(this, block);
     }
 
     public RubyString newString(String string) {
     	return RubyString.newString(this, string);
     }
     
     public RubySymbol newSymbol(String string) {
     	return RubySymbol.newSymbol(this, string);
     }
     
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
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
 
     public RaiseException newRangeError(String message) {
     	return newRaiseException(getClass("RangeError"), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
     	return newRaiseException(getClass("NotImplementedError"), message);
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
         val.put(obj,null);
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
 
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
     
     public String getEncoding() {
         return encoding;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             jrubyHome = System.getProperty("jruby.home", System.getProperty("user.home") + "/.jruby");
             new NormalizedFile(jrubyHome).mkdirs();
         }
         return jrubyHome;
     }
 
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 44aae759db..cc5bc2d702 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,306 +1,306 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.IOHandler.InvalidValueException;
 
 /**
  * Ruby File class equivalent in java.
  *
  * @author jpetersen
  **/
 public class RubyFile extends RubyIO {
 	public static final int LOCK_SH = 1;
 	public static final int LOCK_EX = 2;
 	public static final int LOCK_NB = 4;
 	public static final int LOCK_UN = 8;
 	
     protected String path;
     private FileLock currentLock;
     
 	public RubyFile(IRuby runtime, RubyClass type) {
 	    super(runtime, type);
 	}
 
 	public RubyFile(IRuby runtime, String path) {
 		this(runtime, path, open(runtime, path));
     }
 
 	// use static function because constructor call must be first statement in above constructor 
 	private static InputStream open(IRuby runtime, String path) {
 		try {
 			return new FileInputStream(path);
 		} catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
         }
 	}
     
 	// XXX This constructor is a hack to implement the __END__ syntax.
 	//     Converting a reader back into an InputStream doesn't generally work.
 	public RubyFile(IRuby runtime, String path, final Reader reader) {
 		this(runtime, path, new InputStream() {
 			public int read() throws IOException {
 				return reader.read();
 			}
 		});
 	}
 	
 	private RubyFile(IRuby runtime, String path, InputStream in) {
         super(runtime, runtime.getClass("File"));
         this.path = path;
 		try {
             this.handler = new IOHandlerUnseekable(runtime, in, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());  
         }
         this.modes = handler.getModes();
         registerIOHandler(handler);
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
         } catch (FileNotFoundException e) {
         	throw getRuntime().newErrnoENOENTError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
 		}
     }
     
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
 
 	public IRubyObject flock(IRubyObject lockingConstant) {
         FileChannel fileChannel = handler.getFileChannel();
         int lockMode = (int) ((RubyFixnum) lockingConstant.convertToType("Fixnum", "to_int", 
             true)).getLongValue();
 
         try {
 			switch(lockMode) {
 			case LOCK_UN:
 				if (currentLock != null) {
 					currentLock.release();
 					currentLock = null;
 					
 					return getRuntime().newFixnum(0);
 				}
 				break;
 			case LOCK_EX:
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
         	throw new RaiseException(new NativeException(getRuntime(), getRuntime().getClass("IOError"), ioe));
         }
 		
 		return getRuntime().getFalse();
 	}
 
 	public IRubyObject initialize(IRubyObject[] args) {
 	    if (args.length == 0) {
 	        throw getRuntime().newArgumentError(0, 1);
 	    }
 
 	    args[0].checkSafeString();
 	    path = args[0].toString();
 	    modes = args.length > 1 ? getModes(args[1]) :
 	    	new IOModes(getRuntime(), IOModes.RDONLY);
 	    
 	    // One of the few places where handler may be null.
 	    // If handler is not null, it indicates that this object
 	    // is being reused.
 	    if (handler != null) {
 	        close();
 	    }
 	    openInternal(path, modes);
 	    
 	    if (getRuntime().getCurrentContext().isBlockGiven()) {
 	        // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
 	    }
 	    return this;
 	}
 
     public IRubyObject chmod(IRubyObject[] args) {
         checkArgumentCount(args, 1, 1);
         
         RubyInteger mode = args[0].convertToInteger();
         System.out.println(mode);
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
             
         try {
             Process chown = Runtime.getRuntime().exec("chmod " + FileMetaClass.OCTAL_FORMATTER.sprintf(mode.getLongValue()) + " " + path);
             chown.waitFor();
         } catch (IOException ioe) {
             // FIXME: ignore?
         } catch (InterruptedException ie) {
             // FIXME: ignore?
         }
         
         return getRuntime().newFixnum(0);
     }
 
     public IRubyObject chown(IRubyObject[] args) {
         checkArgumentCount(args, 1, 1);
         
         RubyInteger owner = args[0].convertToInteger();
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
             
         try {
             Process chown = Runtime.getRuntime().exec("chown " + owner + " " + path);
             chown.waitFor();
         } catch (IOException ioe) {
             // FIXME: ignore?
         } catch (InterruptedException ie) {
             // FIXME: ignore?
         }
         
         return getRuntime().newFixnum(0);
     }
 
     public IRubyObject ctime() {
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(),this.path).getParentFile().lastModified());
     }
 
 	public RubyString path() {
 		return getRuntime().newString(path);
 	}
 
 	public IRubyObject stat() {
-        return getRuntime().newRubyFileStat(JRubyFile.create(getRuntime().getCurrentDirectory(),path));
+        return getRuntime().newRubyFileStat(path);
 	}
 	
     public IRubyObject truncate(IRubyObject arg) {
     	RubyFixnum newLength = (RubyFixnum) arg.convertToType("Fixnum", "to_int", true);
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
 	private IOModes getModes(IRubyObject object) {
 		if (object instanceof RubyString) {
 			return new IOModes(getRuntime(), ((RubyString)object).toString());
 		} else if (object instanceof RubyFixnum) {
 			return new IOModes(getRuntime(), ((RubyFixnum)object).getLongValue());
 		}
 
 		throw getRuntime().newTypeError("Invalid type for modes");
 	}
 
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer();
         val.append("#<File:").append(path);
         if(!isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
 }
diff --git a/src/org/jruby/RubyFileStat.java b/src/org/jruby/RubyFileStat.java
index 2ad4f8c432..c515deb74b 100644
--- a/src/org/jruby/RubyFileStat.java
+++ b/src/org/jruby/RubyFileStat.java
@@ -1,187 +1,202 @@
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
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 /**
  * note: renamed from FileStatClass.java
  * Implements File::Stat
  */
 public class RubyFileStat extends RubyObject {
     private static final int READ = 0222;
     private static final int WRITE = 0444;
 
     private RubyFixnum blksize;
     private RubyBoolean isDirectory;
     private RubyBoolean isFile;
     private RubyString ftype;
     private RubyFixnum mode;
     private RubyTime mtime;
     private RubyTime ctime;
     private RubyBoolean isReadable;
     private RubyBoolean isWritable;
     private RubyFixnum size;
     private RubyBoolean isSymlink;
 
+    private static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            return new RubyFileStat(runtime, klass);
+        }
+    };
+
     public static RubyClass createFileStatClass(IRuby runtime) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
-        final RubyClass fileStatClass = runtime.getClass("File").defineClassUnder("Stat",runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        final RubyClass fileStatClass = runtime.getClass("File").defineClassUnder("Stat",runtime.getObject(), ALLOCATOR);
         final CallbackFactory callbackFactory = runtime.callbackFactory(RubyFileStat.class);
+
+        fileStatClass.defineFastMethod("initialize",callbackFactory.getMethod("initialize", IRubyObject.class));
         //        fileStatClass.defineMethod("<=>", callbackFactory.getMethod(""));
         //        fileStateClass.includeModule(runtime.getModule("Comparable"));
         //        fileStatClass.defineMethod("atime", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("blksize", callbackFactory.getMethod("blksize"));
         //        fileStatClass.defineMethod("blockdev?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("blocks", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("chardev?", callbackFactory.getMethod(""));
         fileStatClass.defineMethod("ctime", callbackFactory.getMethod("ctime"));
         //        fileStatClass.defineMethod("dev", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("dev_major", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("dev_minor", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("directory?", callbackFactory.getMethod("directory_p"));
         //        fileStatClass.defineMethod("executable?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("executable_real?", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("file?", callbackFactory.getMethod("file_p"));
         fileStatClass.defineFastMethod("ftype", callbackFactory.getMethod("ftype"));
         //        fileStatClass.defineMethod("gid", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("grpowned?", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("ino", callbackFactory.getMethod("ino"));
         fileStatClass.defineFastMethod("mode", callbackFactory.getMethod("mode"));
         fileStatClass.defineFastMethod("mtime", callbackFactory.getMethod("mtime"));
         //        fileStatClass.defineMethod("nlink", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("owned?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("pipe?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("rdev", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("rdev_major", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("rdev_minor", callbackFactory.getMethod(""));
         fileStatClass.defineMethod("readable?", callbackFactory.getMethod("readable_p"));
         //        fileStatClass.defineMethod("readable_real?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("setgid?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("setuid?", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("size", callbackFactory.getMethod("size"));
         //        fileStatClass.defineMethod("size?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("socket?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("sticky?", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("symlink?", callbackFactory.getMethod("symlink_p"));
         //        fileStatClass.defineMethod("uid", callbackFactory.getMethod(""));
         fileStatClass.defineFastMethod("writable?", callbackFactory.getMethod("writable"));
         //        fileStatClass.defineMethod("writable_real?", callbackFactory.getMethod(""));
         //        fileStatClass.defineMethod("zero?", callbackFactory.getMethod(""));
     	
         return fileStatClass;
     }
 
-    protected RubyFileStat(IRuby runtime, JRubyFile file) {
-        super(runtime, runtime.getClass("File").getClass("Stat"));
+    protected RubyFileStat(IRuby runtime, RubyClass clazz) {
+        super(runtime, clazz);
+
+    }
+
+    public IRubyObject initialize(IRubyObject fname) {
+        IRuby runtime = getRuntime();
+        JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(),fname.toString());
 
         if(!file.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + file.getPath());
         }
 
         // We cannot determine, so always return 4096 (better than blowing up)
         blksize = runtime.newFixnum(4096);
         isDirectory = runtime.newBoolean(file.isDirectory());
         isFile = runtime.newBoolean(file.isFile());
         ftype = file.isDirectory()? runtime.newString("directory") : (file.isFile() ? runtime.newString("file") : null);
 
     	// implementation to lowest common denominator...Windows has no file mode, but C ruby returns either 0100444 or 0100666
     	int baseMode = 0100000;
     	if (file.canRead()) {
             baseMode += READ;
     	}    	
     	if (file.canWrite()) {
             baseMode += WRITE;
     	}
     	mode = runtime.newFixnum(baseMode);
         mtime = runtime.newTime(file.lastModified());
         ctime = runtime.newTime(file.getParentFile().lastModified());
         isReadable = runtime.newBoolean(file.canRead());
         isWritable = runtime.newBoolean(file.canWrite());
         size = runtime.newFixnum(file.length());
         // We cannot determine this in Java, so we will always return false (better than blowing up)
         isSymlink = runtime.getFalse();
+        return this;
     }
     
     public RubyFixnum blksize() {
         return blksize;
     }
 
     public RubyBoolean directory_p() {
         return isDirectory;
     }
 
     public RubyBoolean file_p() {
         return isFile;
     }
     
     public RubyString ftype() {
         return ftype;
     }
     
     // Limitation: We have no pure-java way of getting inode.  webrick needs this defined to work.
     public IRubyObject ino() {
         return getRuntime().newFixnum(0);
     }
     
     public IRubyObject mode() {
         return mode;
     }
     
     public IRubyObject mtime() {
         return mtime;
     }
 
     public IRubyObject ctime() {
         return ctime;
     }
     
     public IRubyObject readable_p() {
         return isReadable;
     }
     
     public IRubyObject size() {
         return size;
     }
     
     public IRubyObject symlink_p() {
         return isSymlink;
     }
     
     public IRubyObject writable() {
     	return isWritable;
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
index 9375c56550..f6e3d5b8f0 100644
--- a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
@@ -1,682 +1,681 @@
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
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.builtin.meta;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.nio.channels.FileChannel;
 import java.util.regex.Pattern;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyDir;
 import org.jruby.RubyFile;
 import org.jruby.RubyFileStat;
 import org.jruby.RubyFileTest;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class FileMetaClass extends IOMetaClass {
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
 
     public static final PrintfFormat OCTAL_FORMATTER = new PrintfFormat("%o"); 
     
     public FileMetaClass(IRuby runtime) {
         super("File", RubyFile.class, runtime.getClass("IO"), FILE_ALLOCATOR);
     }
 
     public FileMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         super(name, RubyFile.class, superClass, allocator, parentCRef);
     }
 
     protected class FileMeta extends Meta {
 		protected void initializeClass() {
 			IRuby runtime = getRuntime();
 	        RubyString separator = runtime.newString("/");
 	        separator.freeze();
 	        defineConstant("SEPARATOR", separator);
 	        defineConstant("Separator", separator);
 	
 	        RubyString altSeparator = runtime.newString(File.separatorChar == '/' ? "\\" : "/");
 	        altSeparator.freeze();
 	        defineConstant("ALT_SEPARATOR", altSeparator);
 	        
 	        RubyString pathSeparator = runtime.newString(File.pathSeparator);
 	        pathSeparator.freeze();
 	        defineConstant("PATH_SEPARATOR", pathSeparator);
             
             // TODO: These were missing, so we're not handling them elsewhere?
 	        setConstant("BINARY", runtime.newFixnum(32768));
             setConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
             setConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
             setConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
             setConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
 	        
 	        // Create constants for open flags
 	        setConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
 	        setConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
 	        setConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
 	        setConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
 	        setConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
 	        setConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
 	        setConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
 	        setConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
 	        setConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
 			
 			// Create constants for flock
 			setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
 			setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
 			setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
 			setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
             
             // Create Constants class
             RubyModule constants = defineModuleUnder("Constants");
             
             // TODO: These were missing, so we're not handling them elsewhere?
             constants.setConstant("BINARY", runtime.newFixnum(32768));
             constants.setConstant("FNM_NOESCAPE", runtime.newFixnum(1));
             constants.setConstant("FNM_CASEFOLD", runtime.newFixnum(8));
             constants.setConstant("FNM_DOTMATCH", runtime.newFixnum(4));
             constants.setConstant("FNM_PATHNAME", runtime.newFixnum(2));
             
             // Create constants for open flags
             constants.setConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
             constants.setConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
             constants.setConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
             constants.setConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
             constants.setConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
             constants.setConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
             constants.setConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
             constants.setConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
             constants.setConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
             
             // Create constants for flock
             constants.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
             constants.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
             constants.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
             constants.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
 	
 	        // TODO Singleton methods: atime, blockdev?, chardev?, chown, directory? 
 	        // TODO Singleton methods: executable?, executable_real?, 
 	        // TODO Singleton methods: ftype, grpowned?, lchmod, lchown, link, mtime, owned?
 	        // TODO Singleton methods: pipe?, readlink, setgid?, setuid?, socket?, 
 	        // TODO Singleton methods: stat, sticky?, symlink, symlink?, umask, utime
 	
 	        extendObject(runtime.getModule("FileTest"));
 	        
 			defineFastSingletonMethod("basename", Arity.optional());
             defineFastSingletonMethod("chmod", Arity.required(2));
             defineFastSingletonMethod("chown", Arity.required(2));
 	        defineFastSingletonMethod("delete", Arity.optional(), "unlink");
 			defineFastSingletonMethod("dirname", Arity.singleArgument());
 	        defineFastSingletonMethod("expand_path", Arity.optional());
 			defineFastSingletonMethod("extname", Arity.singleArgument());
             defineFastSingletonMethod("fnmatch", Arity.optional());
             defineFastSingletonMethod("fnmatch?", Arity.optional(), "fnmatch");
 			defineFastSingletonMethod("join", Arity.optional());
 	        defineFastSingletonMethod("lstat", Arity.singleArgument());
             defineFastSingletonMethod("mtime", Arity.singleArgument());
             defineFastSingletonMethod("ctime", Arity.singleArgument());
 	        defineSingletonMethod("open", Arity.optional());
 	        defineFastSingletonMethod("rename", Arity.twoArguments());
             defineFastSingletonMethod("size?", Arity.singleArgument(), "size_p");
 			defineFastSingletonMethod("split", Arity.singleArgument());
 	        defineFastSingletonMethod("stat", Arity.singleArgument(), "lstat");
 	        defineFastSingletonMethod("symlink?", Arity.singleArgument(), "symlink_p");
 			defineFastSingletonMethod("truncate", Arity.twoArguments());
 			defineFastSingletonMethod("utime", Arity.optional());
 	        defineFastSingletonMethod("unlink", Arity.optional());
 			
 	        // TODO: Define instance methods: atime, chmod, chown, lchmod, lchown, lstat, mtime
 			//defineMethod("flock", Arity.singleArgument());
             defineFastMethod("chmod", Arity.required(1));
             defineFastMethod("chown", Arity.required(1));
             defineFastMethod("ctime", Arity.noArguments());
 			defineMethod("initialize", Arity.optional());
 			defineFastMethod("path", Arity.noArguments());
 	        defineFastMethod("stat", Arity.noArguments());
 			defineFastMethod("truncate", Arity.singleArgument());
 			defineFastMethod("flock", Arity.singleArgument());
 			
 	        RubyFileStat.createFileStatClass(runtime);
 	    }
     };
     
     protected Meta getMeta() {
     	return new FileMeta();
     }
 
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
 		return new FileMetaClass(name, this, FILE_ALLOCATOR, parentCRef);
 	}
     
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(IRuby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
 	
     public IRubyObject basename(IRubyObject[] args) {
     	checkArgumentCount(args, 1, 2);
 
     	String name = RubyString.stringValue(args[0]).toString(); 
 		if (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
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
 		return getRuntime().newString(name).infectBy(args[0]);
 	}
 
     public IRubyObject chmod(IRubyObject[] args) {
         checkArgumentCount(args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw getRuntime().newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             try {
                 Process chmod = Runtime.getRuntime().exec("chmod " + OCTAL_FORMATTER.sprintf(mode.getLongValue()) + " " + filename);
                 chmod.waitFor();
                 int result = chmod.exitValue();
                 if (result == 0) {
                     count++;
                 }
             } catch (IOException ioe) {
                 // FIXME: ignore?
             } catch (InterruptedException ie) {
                 // FIXME: ignore?
             }
         }
         
         return getRuntime().newFixnum(count);
     }
 
     public IRubyObject chown(IRubyObject[] args) {
         checkArgumentCount(args, 2, -1);
         
         int count = 0;
         RubyInteger owner = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw getRuntime().newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             try {
                 Process chown = Runtime.getRuntime().exec("chown " + owner + " " + filename);
                 chown.waitFor();
                 int result = chown.exitValue();
                 if (result == 0) {
                     count++;
                 }
             } catch (IOException ioe) {
                 // FIXME: ignore?
             } catch (InterruptedException ie) {
                 // FIXME: ignore?
             }
         }
         
         return getRuntime().newFixnum(count);
     }
     
 	public IRubyObject dirname(IRubyObject arg) {
 		RubyString filename = RubyString.stringValue(arg);
 		String name = filename.toString();
 		if (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
 			name = name.substring(0, name.length() - 1);
 		}
 		//TODO deal with drive letters A: and UNC names 
 		int index = name.lastIndexOf('/');
 		if (index == -1) {
 			// XXX actually, only on windows...
 			index = name.lastIndexOf('\\');
 		}
 		if (index == -1) {
 			return getRuntime().newString("."); 
 		}
 		if (index == 0) {
 			return getRuntime().newString("/");
 		}
 		return getRuntime().newString(name.substring(0, index)).infectBy(filename);
 	}
 
 	public IRubyObject extname(IRubyObject arg) {
 		RubyString filename = RubyString.stringValue(arg);
 		String name = filename.toString();
         int ix = name.lastIndexOf(".");
         if(ix == -1) {
             return getRuntime().newString("");
         } else {
             return getRuntime().newString(name.substring(ix));
         }
 	}
     
     public IRubyObject expand_path(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
         String relativePath = RubyString.stringValue(args[0]).toString();
 		int pathLength = relativePath.length();
 		
 		if (pathLength >= 1 && relativePath.charAt(0) == '~') {
 			// Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
 			int userEnd = relativePath.indexOf('/');
 			
 			if (userEnd == -1) {
 				if (pathLength == 1) { 
 	                // Single '~' as whole path to expand
 					relativePath = RubyDir.getHomeDirectoryPath(this).toString();
 				} else {
 					// No directory delimeter.  Rest of string is username
 					userEnd = pathLength;
 				}
 			}
 			
 			if (userEnd == 1) {
 				// '~/...' as path to expand 
 				relativePath = RubyDir.getHomeDirectoryPath(this).toString() + 
                	    relativePath.substring(1);
 			} else if (userEnd > 1){
 				// '~user/...' as path to expand
 				String user = relativePath.substring(1, userEnd);
 				IRubyObject dir = RubyDir.getHomeDirectoryPath(this, user);
 					
 				if (dir.isNil()) {
 					throw getRuntime().newArgumentError("user " + user + " does not exist");
 				} 
 				
                 relativePath = "" + dir + 
                     (pathLength == userEnd ? "" : relativePath.substring(userEnd));
 			}
 		}
 
         if (new File(relativePath).isAbsolute()) {
             return getRuntime().newString(relativePath);
         }
 
         String cwd = getRuntime().getCurrentDirectory();
         if (args.length == 2 && !args[1].isNil()) {
             cwd = RubyString.stringValue(args[1]).toString();
         }
 
         // Something wrong we don't know the cwd...
         if (cwd == null) {
             return getRuntime().getNil();
         }
 
         JRubyFile path = JRubyFile.create(cwd, relativePath);
 
         String extractedPath;
         try {
             extractedPath = path.getCanonicalPath();
         } catch (IOException e) {
             extractedPath = path.getAbsolutePath();
         }
         return getRuntime().newString(extractedPath);
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
     // Fixme: implement FNM_PATHNAME, FNM_DOTMATCH, and FNM_CASEFOLD
     public IRubyObject fnmatch(IRubyObject[] args) {
         checkArgumentCount(args, 2, -1);
         String pattern = args[0].convertToString().toString();
         RubyString path = args[1].convertToString();
         int opts = (int) (args.length > 2 ? args[2].convertToInteger().getLongValue() : 0);
 
         boolean dot = pattern.startsWith(".");
         
         pattern = pattern.replaceAll("(\\.)", "\\\\$1");
         pattern = pattern.replaceAll("(?<=[^\\\\])\\*", ".*");
         pattern = pattern.replaceAll("^\\*", ".*");
         pattern = pattern.replaceAll("(?<=[^\\\\])\\?", ".");
         pattern = pattern.replaceAll("^\\?", ".");
         if ((opts & FNM_NOESCAPE) != FNM_NOESCAPE) {
             pattern = pattern.replaceAll("\\\\([^\\\\*\\\\?])", "$1");
         }
         pattern = pattern.replaceAll("\\{", "\\\\{");
         pattern = pattern.replaceAll("\\}", "\\\\}");
         pattern = "^" + pattern + "$";
         
         if (path.toString().startsWith(".") && !dot) {
             return getRuntime().newBoolean(false);
         }
 
         return getRuntime().newBoolean(Pattern.matches(pattern, path.toString()));
     }
     
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).  
      */
     public RubyString join(IRubyObject[] args) {
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
 				element = join(((RubyArray) args[i]).toJavaArray()).toString();
 			} else {
 				element = args[i].convertToString().toString();
 			}
 			
 			chomp(buffer);
 			if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
 				buffer.append("/");
 			} 
 			buffer.append(element);
 		}
         
         RubyString fixedStr = RubyString.newString(getRuntime(), buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     private void chomp(StringBuffer buffer) {
     	int lastIndex = buffer.length() - 1;
     	
     	while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) { 
     		buffer.setLength(lastIndex);
     		lastIndex--;
     	}
     }
 
     public IRubyObject lstat(IRubyObject filename) {
     	RubyString name = RubyString.stringValue(filename);
-    	
-        return getRuntime().newRubyFileStat(JRubyFile.create(getRuntime().getCurrentDirectory(),name.toString()));
+        return getRuntime().newRubyFileStat(name.toString());
     }
 
     public IRubyObject ctime(IRubyObject filename) {
         RubyString name = RubyString.stringValue(filename);
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(),name.toString()).getParentFile().lastModified());
     }
     
     public IRubyObject mtime(IRubyObject filename) {
         RubyString name = RubyString.stringValue(filename);
 
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(),name.toString()).lastModified());
     }
 
 	public IRubyObject open(IRubyObject[] args) {
 	    return open(args, true);
 	}
 	
 	public IRubyObject open(IRubyObject[] args, boolean tryToYield) {
         checkArgumentCount(args, 1, -1);
         IRuby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyString pathString = RubyString.stringValue(args[0]);
 	    pathString.checkSafeString();
 	    String path = pathString.toString();
 
 	    IOModes modes = 
 	    	args.length >= 2 ? getModes(args[1]) : new IOModes(runtime, IOModes.RDONLY);
 	    RubyFile file = new RubyFile(runtime, this);
         
         RubyInteger fileMode =
             args.length >= 3 ? args[2].convertToInteger() : null;
 
 	    file.openInternal(path, modes);
 
         if (fileMode != null) {
             chmod(new IRubyObject[] {fileMode, pathString});
         }
 
         if (tryToYield && tc.isBlockGiven()) {
             IRubyObject value = getRuntime().getNil();
 	        try {
 	            value = tc.yield(file);
 	        } finally {
 	            file.close();
 	        }
 	        
 	        return value;
 	    }
 	    
 	    return file;
 	}
 	
     public IRubyObject rename(IRubyObject oldName, IRubyObject newName) {
     	RubyString oldNameString = RubyString.stringValue(oldName);
     	RubyString newNameString = RubyString.stringValue(newName);
         oldNameString.checkSafeString();
         newNameString.checkSafeString();
         JRubyFile oldFile = JRubyFile.create(getRuntime().getCurrentDirectory(),oldNameString.toString());
         JRubyFile newFile = JRubyFile.create(getRuntime().getCurrentDirectory(),newNameString.toString());
 
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
         	throw getRuntime().newErrnoENOENTError("No such file or directory - " + oldNameString + " or " + newNameString);
         }
         oldFile.renameTo(JRubyFile.create(getRuntime().getCurrentDirectory(),newNameString.toString()));
         
         return RubyFixnum.zero(getRuntime());
     }
     
     public IRubyObject size_p(IRubyObject filename) {
         long size = 0;
         
         try {
              FileInputStream fis = new FileInputStream(new File(filename.toString()));
              FileChannel chan = fis.getChannel();
              size = chan.size();
              chan.close();
              fis.close();
         } catch (IOException ioe) {
             // missing files or inability to open should just return nil
         }
         
         if (size == 0) {
             return getRuntime().getNil();
         }
         
         return getRuntime().newFixnum(size);
     }
 	
     public RubyArray split(IRubyObject arg) {
     	RubyString filename = RubyString.stringValue(arg);
     	
     	return filename.getRuntime().newArray(dirname(filename),
     		basename(new IRubyObject[] { filename }));
     }
     
     public IRubyObject symlink_p(IRubyObject arg1) {
     	RubyString filename = RubyString.stringValue(arg1);
         
         JRubyFile file = JRubyFile.create(getRuntime().getCurrentDirectory(), filename.toString());
         
         try {
             // Only way to determine symlink is to compare canonical and absolute files
             // However symlinks in containing path must not produce false positives, so we check that first
             File absoluteParent = file.getAbsoluteFile().getParentFile();
             File canonicalParent = file.getAbsoluteFile().getParentFile().getCanonicalFile();
 
             if (canonicalParent.getAbsolutePath().equals(absoluteParent.getAbsolutePath())) {
                 // parent doesn't change when canonicalized, compare absolute and canonical file directly
                 return file.getAbsolutePath().equals(file.getCanonicalPath()) ? getRuntime().getFalse() : getRuntime().getTrue();
             }
 
             // directory itself has symlinks (canonical != absolute), so build new path with canonical parent and compare
             file = JRubyFile.create(getRuntime().getCurrentDirectory(), canonicalParent.getAbsolutePath() + "/" + file.getName());
             return file.getAbsolutePath().equals(file.getCanonicalPath()) ? getRuntime().getFalse() : getRuntime().getTrue();
         } catch (IOException ioe) {
             // problem canonicalizing the file; nothing we can do but return false
             return getRuntime().getFalse();
         }
     }
 
     // Can we produce IOError which bypasses a close?
     public IRubyObject truncate(IRubyObject arg1, IRubyObject arg2) { 
         RubyString filename = RubyString.stringValue(arg1);
         RubyFixnum newLength = (RubyFixnum) arg2.convertToType("Fixnum", "to_int", true);
         IRubyObject[] args = new IRubyObject[] { filename, getRuntime().newString("w+") };
         RubyFile file = (RubyFile) open(args, false);
         file.truncate(newLength);
         file.close();
         
         return RubyFixnum.zero(getRuntime());
     }
 
     /**
      * This method does NOT set atime, only mtime, since Java doesn't support anything else.
      */
     public IRubyObject utime(IRubyObject[] args) {
         checkArgumentCount(args, 2, -1);
         
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
             filename.checkSafeString();
             JRubyFile fileToTouch = JRubyFile.create(getRuntime().getCurrentDirectory(),filename.toString());
             
             if (!fileToTouch.exists()) {
                 throw getRuntime().newErrnoENOENTError(" No such file or directory - \"" + 
                         filename + "\"");
             }
             
             fileToTouch.setLastModified(mtime);
         }
         
         return getRuntime().newFixnum(args.length - 2);
     }
 	
     public IRubyObject unlink(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
         	RubyString filename = RubyString.stringValue(args[i]);
             filename.checkSafeString();
             JRubyFile lToDelete = JRubyFile.create(getRuntime().getCurrentDirectory(),filename.toString());
             if (!lToDelete.exists()) {
 				throw getRuntime().newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
 			}
             if (!lToDelete.delete()) {
                 return getRuntime().getFalse();
             }
         }
         return getRuntime().newFixnum(args.length);
     }
 	
     // TODO: Figure out to_str and to_int conversion + precedence here...
 	private IOModes getModes(IRubyObject object) {
 		if (object instanceof RubyString) {
 			return new IOModes(getRuntime(), ((RubyString)object).toString());
 		} else if (object instanceof RubyFixnum) {
 			return new IOModes(getRuntime(), ((RubyFixnum)object).getLongValue());
 		}
 
 		throw getRuntime().newTypeError("Invalid type for modes");
 	}
 	
 
 }
diff --git a/test/org/jruby/test/BaseMockRuby.java b/test/org/jruby/test/BaseMockRuby.java
index 376ac0e884..9d7a967fc9 100644
--- a/test/org/jruby/test/BaseMockRuby.java
+++ b/test/org/jruby/test/BaseMockRuby.java
@@ -1,716 +1,716 @@
 package org.jruby.test;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBinding;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFileStat;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.RubyTime;
 import org.jruby.Profile;
 import org.jruby.RubySymbol.SymbolTable;
 import org.jruby.ast.Node;
 import org.jruby.common.RubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class BaseMockRuby implements IRuby {
 
 	public CacheMap getCacheMap() {
 		throw new MockException();
 	}
 
 	public IRubyObject evalScript(String script) {
 		throw new MockException();
 	}
 
 	public IRubyObject eval(Node node) {
 		throw new MockException();
 	}
 
     public RubyClass getObject() {
         throw new MockException();
     }
 
     public RubyModule getKernel() {
         throw new MockException();
     }
     
     public RubyClass getString() {
         throw new MockException();
     }
     
     public RubyClass getFixnum() {
         throw new MockException();
     }
 
 	public RubyBoolean getTrue() {
 		throw new MockException();
 	}
 
 	public RubyBoolean getFalse() {
 		throw new MockException();
 	}
 
 	public IRubyObject getNil() {
 		throw new MockException();
 	}
 
     public RubyClass getNilClass() {
         throw new MockException();
     }
 
 	public RubyModule getModule(String name) {
 		throw new MockException();
 	}
 
 	public RubyClass getClass(String name) {
 		throw new MockException();
 	}
 
 	public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
 		throw new MockException();
 	}
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator,
             SinglyLinkedList parentCRef) {
 		throw new MockException();
 	}
 
 	public RubyModule defineModule(String name) {
 		throw new MockException();
 	}
 
 	public RubyModule defineModuleUnder(String name, SinglyLinkedList parentCRef) {
 		throw new MockException();
 	}
 
 	public RubyModule getOrCreateModule(String name) {
 		throw new MockException();
 	}
 
 	public int getSafeLevel() {
 		throw new MockException();
 	}
 
 	public void setSafeLevel(int safeLevel) {
 		throw new MockException();
 	}
 
 	public void secure(int level) {
 		throw new MockException();
 	}
 
 	public void defineGlobalConstant(String name, IRubyObject value) {
 		throw new MockException();
 	}
 
 	public IRubyObject getTopConstant(String name) {
 		throw new MockException();
 	}
 
 	public boolean isClassDefined(String name) {
 		throw new MockException();
 	}
 
 	public IRubyObject yield(IRubyObject value) {
 		throw new MockException();
 	}
 
 	public IRubyObject yield(IRubyObject value, IRubyObject self,
 			RubyModule klass, boolean checkArguments) {
 		throw new MockException();
 	}
 
 	public IRubyObject getTopSelf() {
 		throw new MockException();
 	}
 
 	public String getSourceFile() {
 		throw new MockException();
 	}
 
 	public int getSourceLine() {
 		throw new MockException();
 	}
 
 	public IRubyObject getVerbose() {
 		throw new MockException();
 	}
 
 	public IRubyObject getDebug() {
 		throw new MockException();
 	}
 
 	public boolean isBlockGiven() {
 		throw new MockException();
 	}
 
 	public boolean isFBlockGiven() {
 		throw new MockException();
 		
 	}
 
 	public void setVerbose(IRubyObject verbose) {
 		throw new MockException();
 
 	}
 
 	public void setDebug(IRubyObject debug) {
 		throw new MockException();
 	}
 
 	public Visibility getCurrentVisibility() {
 		throw new MockException();
 		
 	}
 
 	public void setCurrentVisibility(Visibility visibility) {
 		throw new MockException();
 
 	}
 
 	public void defineVariable(GlobalVariable variable) {
 		throw new MockException();
 
 	}
 
 	public void defineReadonlyVariable(String name, IRubyObject value) {
 		throw new MockException();
 
 	}
 
 	public Node parse(Reader content, String file) {
 		throw new MockException();
 		
 	}
 
 	public Node parse(String content, String file) {
 		throw new MockException();
 		
 	}
 
 	public Parser getParser() {
 		throw new MockException();
 		
 	}
 
 	public ThreadService getThreadService() {
 		throw new MockException();
 		
 	}
 
 	public ThreadContext getCurrentContext() {
 		throw new MockException();
 		
 	}
 
 	public LoadService getLoadService() {
 		throw new MockException();
 		
 	}
 
 	public RubyWarnings getWarnings() {
 		throw new MockException();
 		
 	}
 
 	public PrintStream getErrorStream() {
 		throw new MockException();
 		
 	}
 
 	public InputStream getInputStream() {
 		throw new MockException();
 		
 	}
 
 	public PrintStream getOutputStream() {
 		throw new MockException();
 		
 	}
 
 	public RubyModule getClassFromPath(String path) {
 		throw new MockException();
 		
 	}
 
 	public void printError(RubyException excp) {
 		throw new MockException();
 
 	}
 
 	public void loadScript(RubyString scriptName, RubyString source,
 			boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadScript(String scriptName, Reader source, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadNode(String scriptName, Node node, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void loadFile(File file, boolean wrap) {
 		throw new MockException();
 
 	}
 
 	public void callTraceFunction(ThreadContext context, String event, ISourcePosition position,
 			IRubyObject self, String name, IRubyObject type) {
 		throw new MockException();
 
 	}
 
 	public RubyProc getTraceFunction() {
 		throw new MockException();
 		
 	}
 
 	public void setTraceFunction(RubyProc traceFunction) {
 		throw new MockException();
 
 	}
 
 	public GlobalVariables getGlobalVariables() {
 		throw new MockException();
 		
 	}
 
 	public void setGlobalVariables(GlobalVariables variables) {
 		throw new MockException();
 		
 	}
 
 	public CallbackFactory callbackFactory(Class type) {
 		throw new MockException();
 		
 	}
 
 	public IRubyObject pushExitBlock(RubyProc proc) {
 		throw new MockException();
 		
 	}
 
 	public void tearDown() {
 		throw new MockException();
 
 	}
 
 	public RubyArray newArray() {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject object) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject car, IRubyObject cdr) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(IRubyObject[] objects) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(List list) {
 		throw new MockException();
 		
 	}
 
 	public RubyArray newArray(int size) {
 		throw new MockException();
 		
 	}
 
 	public RubyBoolean newBoolean(boolean value) {
 		throw new MockException();
 		
 	}
 
-	public RubyFileStat newRubyFileStat(File file) {
+	public RubyFileStat newRubyFileStat(String file) {
 		throw new MockException();
 		
 	}
 
 	public RubyFixnum newFixnum(long value) {
 		throw new MockException();
 		
 	}
 
 	public RubyFloat newFloat(double value) {
 		throw new MockException();
 		
 	}
 
 	public RubyNumeric newNumeric() {
 		throw new MockException();
 
     }
 
     public RubyProc newProc() {
         throw new MockException();
         
     }
 
     public RubyBinding newBinding() {
         throw new MockException();
         
     }
 
     public RubyBinding newBinding(Block block) {
         throw new MockException();
         
     }
 
 	public RubyString newString(String string) {
 		throw new MockException();
 		
 	}
 
 	public RubySymbol newSymbol(String string) {
 		throw new MockException();
 		
 	}
     
     public RubyTime newTime(long milliseconds) {
         throw new MockException();
     }
 
 	public RaiseException newArgumentError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newArgumentError(int got, int expected) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEBADFError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEINVALError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoENOENTError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoESPIPEError() {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEBADFError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEINVALError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoENOENTError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoESPIPEError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newErrnoEEXISTError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIndexError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSecurityError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemCallError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newTypeError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newThreadError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSyntaxError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newRangeError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNotImplementedError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNoMethodError(String message, String name) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newNameError(String message, String name) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newLocalJumpError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newLoadError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newFrozenError(String objectType) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemStackError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newSystemExit(int status) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIOError(String message) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newIOErrorFromException(IOException ioe) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newTypeError(IRubyObject receivedObject,
 			RubyClass expectedType) {
 		throw new MockException();
 		
 	}
 
 	public RaiseException newEOFError() {
 		throw new MockException();
 		
 	}
 
 	public SymbolTable getSymbolTable() {
 		throw new MockException();
 		
 	}
 
 	public void setStackTraces(int stackTraces) {
 		throw new MockException();
 
 	}
 
 	public int getStackTraces() {
 		throw new MockException();
 		
 	}
 
 	public void setRandomSeed(long randomSeed) {
 		throw new MockException();
 
 	}
 
 	public long getRandomSeed() {
 		throw new MockException();
 		
 	}
 
 	public Random getRandom() {
 		throw new MockException();
 		
 	}
 
 	public ObjectSpace getObjectSpace() {
 		throw new MockException();
 		
 	}
 
 	public Hashtable getIoHandlers() {
 		throw new MockException();
 		
 	}
 
 	public RubyFixnum[] getFixnumCache() {
 		throw new MockException();
 		
 	}
 
 	public long incrementRandomSeedSequence() {
 		throw new MockException();
 		
 	}
 
 	public JavaSupport getJavaSupport() {
 		throw new MockException();
 	}
 
     public String getCurrentDirectory() {
         throw new MockException();
     }
 
     public void setCurrentDirectory(String dir) {
         throw new MockException();
     }
 
 	public RaiseException newZeroDivisionError() {
         throw new MockException();
 	}
 
 	public InputStream getIn() {
         throw new MockException();
 	}
 
 	public PrintStream getOut() {
         throw new MockException();
 	}
 
 	public PrintStream getErr() {
         throw new MockException();
 	}
 
 	public boolean isGlobalAbortOnExceptionEnabled() {
         throw new MockException();
 	}
 
 	public void setGlobalAbortOnExceptionEnabled(boolean b) {
         throw new MockException();
 	}
 
 	public boolean isDoNotReverseLookupEnabled() {
         throw new MockException();
 	}
 
 	public void setDoNotReverseLookupEnabled(boolean b) {
         throw new MockException();
 	}
 
     public boolean registerInspecting(Object o) {
         throw new MockException();
     }
     public void unregisterInspecting(Object o) {
         throw new MockException();
     }
 
     public boolean isObjectSpaceEnabled() {
         return true;
     }
 
     public IRubyObject compileAndRun(Node node) {
         // TODO Auto-generated method stub
         return null;
     }
 
     public Node parse(Reader content, String file, DynamicScope scope) {
         // TODO Auto-generated method stub
         return null;
     }
 
     public Node parse(String content, String file, DynamicScope scope) {
         // TODO Auto-generated method stub
         return null;
     }
     
     public IRubyObject getTmsStruct() {
         return null;
     }
     
     public long getStartTime() {
         return 0;
     }
     
     public void setEncoding(String encoding) {}
     public String getEncoding() { return null; }
 
     public Profile getProfile() {
         return null;
     }
     
     public Map getRuntimeInformation() {
         return null;
     }
     
     public String getJRubyHome() {
         return null;
     }
 }
