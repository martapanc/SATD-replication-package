diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 62b3e8d612..2d6c630023 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -151,1489 +151,1494 @@ public final class Ruby implements IRuby {
     /** safe-level:
     		0 - strings from streams/environment/ARGV are tainted (default)
     		1 - no dangerous operation by tainted value
     		2 - process/file operations prohibited
     		3 - all genetated objects are tainted
     		4 - no global (non-tainted) variable modification/no direct output
     */
     private int safeLevel = 0;
 
     // Default classes/objects
     private IRubyObject nilObject;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     private RubyClass objectClass;
     private StringMetaClass stringClass;
     private RubyClass systemCallError = null;
     private RubyModule errnoModule = null;
     private IRubyObject topSelf;
     
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
     
     private long startTime = System.currentTimeMillis();
     
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
     
     private IRubyObject verbose;
     private IRubyObject debug;
 
     // Java support
     private JavaSupport javaSupport;
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack atExitBlocks = new Stack();
 
     private RubyModule kernelModule;
 
     private RubyClass nilClass;
 
     private FixnumMetaClass fixnumClass;
     
     private IRubyObject tmsStruct;
 
     private Profile profile;
 
     private String jrubyHome;
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err) {
         this(in,out,err,true,Profile.DEFAULT);
     }
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err, boolean osEnabled) {
         this(in,out,err,osEnabled,Profile.DEFAULT);
     }
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err, boolean osEnabled, Profile profile) {
         this.in = in;
         this.out = out;
         this.err = err;
         this.selectorTable = new MethodSelectorTable();
         
         objectSpaceEnabled = osEnabled;
 
         this.profile = profile;
         
         try {
             currentDirectory = JRubyFile.getFileProperty("user.dir");
         } catch (AccessControlException accessEx) {
             // default to "/" as current dir for applets (which can't read from FS anyway)
             currentDirectory = "/";
         }
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby getDefaultInstance() {
         Ruby ruby;
         if(System.getProperty("jruby.objectspace.enabled") != null) {
             ruby = new Ruby(System.in, System.out, System.err, Boolean.getBoolean("jruby.objectspace.enabled"));
         } else {
             ruby = new Ruby(System.in, System.out, System.err);
         }
 
         ruby.init();
         
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err, boolean osEnabled, Profile profile) {
         Ruby ruby = new Ruby(in, out, err, osEnabled, profile);
         ruby.init();
         
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err, Profile profile) {
         return newInstance(in,out,err,true,profile);
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err, boolean osEnabled) {
         return newInstance(in,out,err,osEnabled,Profile.DEFAULT);
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err) {
         return newInstance(in, out, err, true, Profile.DEFAULT);
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
             
             return EvaluationState.eval(tc, node, tc.getFrameSelf());
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw newLocalJumpError("unexpected return");
                 //	            return (IRubyObject)je.getSecondaryData();
         	} else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw newLocalJumpError("unexpected break");
             }
 
             throw je;
         }
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             ISourcePosition position = node.getPosition();
             InstructionCompiler2 compiler = new InstructionCompiler2();
             String classname = null;
             
             if (position != null) {
                 classname = node.getPosition().getFile();
                 if (classname.endsWith(".rb")) {
                     classname = classname.substring(0, classname.length() - 3);
                 }
                 compiler.compile(classname, position.getFile(), node);
             } else {
                 classname = "EVAL";
                 compiler.compile(classname, "EVAL", node);
             }
             
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
+        File f = new File(scriptName);
+        if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
+            scriptName = "./" + scriptName;
+        }
+
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
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 58b02179f3..dcd50b7fcd 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1099 +1,1098 @@
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
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 package org.jruby.runtime;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.ZeroArgNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SourcePositionFactory;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author jpetersen
  */
 public class ThreadContext {
     private final static int INITIAL_SIZE = 50;
     
     private final IRuby runtime;
 
     // Is this thread currently with in a function trace? 
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
 
     private Block blockStack;
 
     private RubyThread thread;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
 	
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     //private UnsynchronizedStack iterStack;
     private Iter[] iterStack = new Iter[INITIAL_SIZE];
     private int iterIndex = -1;
     //private UnsynchronizedStack crefStack;
     private SinglyLinkedList[] crefStack = new SinglyLinkedList[INITIAL_SIZE];
     private int crefIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private String[] catchStack = new String[INITIAL_SIZE];
     private int catchIndex = -1;
 
     private int[] bindingFrameStack = new int[INITIAL_SIZE];
     private int bindingFrameIndex = -1;
 
     private RubyModule wrapper;
 
     private ISourcePosition sourcePosition = new SourcePositionFactory(null).getDummyPosition();
 
     /**
      * Constructor for Context.
      */
     public ThreadContext(IRuby runtime) {
         this.runtime = runtime;
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
        pushScope(new DynamicScope(new LocalStaticScope(null), null));
     }
     
     Visibility lastVis;
     CallType lastCallType;
     
     public IRuby getRuntime() {
         return runtime;
     }
 
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(Visibility vis, CallType callType) {
         lastVis = vis;
         lastCallType = callType;
     }
     
     public Visibility getLastVisibility() {
         return lastVis;
     }
     
     public CallType getLastCallType() {
         return lastCallType;
     }
     
     private void pushBlock(Block block) {
         block.setNext(this.blockStack);
         this.blockStack = block;
     }
     
     private Block popBlock() {
         if (blockStack == null) {
             return null;
         }
         
         Block current = blockStack;
         blockStack = (Block)blockStack.getNext();
         
         return current;
     }
     
     public Block getCurrentBlock() {
         return blockStack;
     }
     
     public boolean isBlockGiven() {
         return getCurrentFrame().isBlockGiven();
     }
 
     public boolean isFBlockGiven() {
         Frame previous = getPreviousFrame();
         if (previous == null) {
             return false;
         }
         return previous.isBlockGiven();
     }
     
     public void restoreBlockState(Block block, RubyModule klass) {
         //System.out.println("IN RESTORE BLOCK (" + block.getDynamicScope() + ")");
         pushFrame(block.getFrame());
 
         setCRef(block.getCRef());
         
         getCurrentFrame().setScope(block.getScope());
 
 
         if (block.getDynamicScope() != null) {
             pushScope(block.getDynamicScope().cloneScope());
         }
 
         pushRubyClass((klass != null) ? klass : block.getKlass()); 
 
         pushIter(block.getIter());
     }
 
     private void flushBlockState(Block block) {
         //System.out.println("FLUSH");
         popIter();
         
         // For loop eval has no dynamic scope
         if (block.getDynamicScope() != null) {
             popScope();
         }
         popFrame();
         
         unsetCRef();
         
         popRubyClass();
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
     
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     private void expandFramesIfNecessary() {
         if (frameIndex + 1 == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
             frameStack = newFrameStack;
         }
     }
     
     private void expandParentsIfNecessary() {
         if (parentIndex + 1 == parentStack.length) {
             int newSize = parentStack.length * 2;
             RubyModule[] newParentStack = new RubyModule[newSize];
             
             System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
             
             parentStack = newParentStack;
         }
     }
     
     private void expandItersIfNecessary() {
         if (iterIndex + 1 == iterStack.length) {
             int newSize = iterStack.length * 2;
             Iter[] newIterStack = new Iter[newSize];
             
             System.arraycopy(iterStack, 0, newIterStack, 0, iterStack.length);
             
             iterStack = newIterStack;
         }
     }
     
     private void expandCrefsIfNecessary() {
         if (crefIndex + 1 == crefStack.length) {
             int newSize = crefStack.length * 2;
             SinglyLinkedList[] newCrefStack = new SinglyLinkedList[newSize];
             
             System.arraycopy(crefStack, 0, newCrefStack, 0, crefStack.length);
             
             crefStack = newCrefStack;
         }
     }
     
     public void pushScope(DynamicScope scope) {
         scopeStack[++scopeIndex] = scope;
         expandScopesIfNecessary();
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
 
     private void expandScopesIfNecessary() {
         if (scopeIndex + 1 == scopeStack.length) {
             int newSize = scopeStack.length * 2;
             DynamicScope[] newScopeStack = new DynamicScope[newSize];
             
             System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
             
             scopeStack = newScopeStack;
         }
     }
 
     public RubyThread getThread() {
         return thread;
     }
 
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
 
     public IRubyObject getLastline() {
         return getCurrentScope().getLastLine();
     }
 
     public void setLastline(IRubyObject value) {
         getCurrentScope().setLastLine(value);
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             String[] newCatchStack = new String[newSize];
     
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(String catchSymbol) {
         catchStack[++catchIndex] = catchSymbol;
         expandCatchIfNecessary();
     }
 
     public void popCatch() {
         catchIndex--;
     }
 
     public String[] getActiveCatches() {
         if (catchIndex < 0) return new String[0];
 
         String[] activeCatches = new String[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         pushFrame(getCurrentFrame().duplicate());
     }
     
     private void pushCallFrame(IRubyObject self, IRubyObject[] args, 
             String lastFunc, RubyModule lastClass) {
         Iter iter = null;
         if(getCurrentFrame().getCallingZSuper()) {
             iter = (!getCurrentIter().isNot()) ? getCurrentIter() : getCurrentFrame().getIter();
         } else {
             iter = getCurrentIter();
         }
         pushFrame(new Frame(this, self, args, lastFunc, lastClass, getPosition(), iter, getCurrentBlock()));        
     }
     
     private void pushFrame() {
         pushFrame(new Frame(this, getCurrentIter(), getCurrentBlock()));
     }
     
     private void pushFrameNoBlock() {
         pushFrame(new Frame(this, Iter.ITER_NOT, null));
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary();
     }
     
     private void popFrame() {
         Frame frame = (Frame)frameStack[frameIndex--];
 
         setPosition(frame.getPosition());
     }
     
     public Frame getCurrentFrame() {
         return (Frame)frameStack[frameIndex];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : (Frame) frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameLastFunc() {
         return getCurrentFrame().getLastFunc();
     }
     
     public Iter getFrameIter() {
         return getCurrentFrame().getIter();
     }
     
     public void setFrameIter(Iter iter) {
         getCurrentFrame().setIter(iter);
     }
     
     public Iter getPreviousFrameIter() {
         return getPreviousFrame().getIter();
     }
     
     public IRubyObject[] getFrameArgs() {
         return getCurrentFrame().getArgs();
     }
     
     public void setFrameArgs(IRubyObject[] args) {
         getCurrentFrame().setArgs(args);
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlockArg();
     }
     
     public Block getFrameBlockOrRaise() {
         if (! isBlockGiven()) {
             throw runtime.newLocalJumpError("yield called out of block");
         }
         
         return getCurrentFrame().getBlockArg();
     }
     
     public void setFrameSelf(IRubyObject self) {
         getCurrentFrame().setSelf(self);
     }
 
     public IRubyObject getFramePreviousSelf() {
         return getPreviousFrame().getSelf();
     }
 
     public void setSelfToPrevious() {
         getCurrentFrame().setSelf(getPreviousFrame().getSelf());
     }
 
     public RubyModule getFrameLastClass() {
         return getCurrentFrame().getLastClass();
     }
     
     public RubyModule getPreviousFrameLastClass() {
         return getPreviousFrame().getLastClass();
     }
     
     public ISourcePosition getFramePosition() {
         return getCurrentFrame().getPosition();
     }
     
     public ISourcePosition getPreviousFramePosition() {
         return getPreviousFrame().getPosition();
     }
 
     private void expandBindingFrameIfNecessary() {
         if (bindingFrameIndex + 1 == bindingFrameStack.length) {
             int newSize = bindingFrameStack.length * 2;
             int[] newbindingFrameStack = new int[newSize];
     
             System.arraycopy(bindingFrameStack, 0, newbindingFrameStack, 0, bindingFrameStack.length);
             bindingFrameStack = newbindingFrameStack;
         }
     }
     
     public void pushBindingFrame(int bindingDepth) {
         bindingFrameStack[++bindingFrameIndex] = bindingDepth;
         expandBindingFrameIfNecessary();
     }
 
     public void popBindingFrame() {
         bindingFrameIndex--;
     }
 
-
     public int currentBindingFrame() {
         if(bindingFrameIndex == -1) {
             return 0;
         } else {
             return bindingFrameStack[bindingFrameIndex];
         }
     }
 
     
     /////////////////////////// ITER MANAGEMENT //////////////////////////
     private Iter popIter() {
         Iter ret = (Iter) iterStack[iterIndex];
         iterStack[iterIndex--] = null;
         return ret;
     }
     
     private void pushIter(Iter iter) {
         iterStack[++iterIndex] = iter;
         expandItersIfNecessary();
     }
     
     public void setNoBlock() {
         pushIter(Iter.ITER_NOT);
     }
     
     private void setNoBlockIfNoBlock() {
         pushIter(getCurrentIter().isNot() ? Iter.ITER_NOT : Iter.ITER_PRE);
     }
     
     public void clearNoBlock() {
         popIter();
     }
     
     public void setBlockAvailable() {
         pushIter(Iter.ITER_PRE);
     }
     
     public void clearBlockAvailable() {
         popIter();
     }
     
     public void setIfBlockAvailable() {
         pushIter(isBlockGiven() ? Iter.ITER_PRE : Iter.ITER_NOT);
     }
     
     public void clearIfBlockAvailable() {
         popIter();
     }
     
     public void setInBlockIfBlock() {
         pushIter(getCurrentIter().isPre() ? Iter.ITER_CUR : Iter.ITER_NOT);
     }
     
     public void setInBlock() {
         pushIter(Iter.ITER_CUR);
     }
     
     public void clearInBlock() {
         popIter();
     }
 
     public Iter getCurrentIter() {
         return (Iter) iterStack[iterIndex];
     }
     
     public Scope getFrameScope() {
         return getCurrentFrame().getScope();
     }
 
     public ISourcePosition getPosition() {
         return sourcePosition;
     }
     
     public String getSourceFile() {
         return sourcePosition.getFile();
     }
     
     public int getSourceLine() {
         return sourcePosition.getEndLine();
     }
 
     public void setPosition(ISourcePosition position) {
         sourcePosition = position;
     }
 
     public IRubyObject getBackref() {
         IRubyObject value = getCurrentScope().getBackRef();
         
         // DynamicScope does not preinitialize these values since they are virtually
         // never used.
         return value == null ? runtime.getNil() : value; 
     }
 
     public void setBackref(IRubyObject backref) {
         getCurrentScope().setBackRef(backref);
     }
 
     public Visibility getCurrentVisibility() {
         return getFrameScope().getVisibility();
     }
 
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getScope().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility vis) {
         getFrameScope().setVisibility(vis);
     }
 
     public IRubyObject callSuper(IRubyObject[] args, boolean zSuper) {
         Frame frame = getCurrentFrame();
         
         frame.setCallingZSuper(zSuper);        
         
         if (frame.getLastClass() == null) {
             String name = frame.getLastFunc();
             throw runtime.newNameError("superclass method '" + name + "' must be enabled by enableSuper().", name);
         }
         setNoBlockIfNoBlock();
         try {
             RubyClass superClass = frame.getLastClass().getSuperClass();
 
             // Modules do not directly inherit Object so we have hacks like this
             if (superClass == null) {
                 // TODO cnutter: I believe modules, the only ones here to have no superclasses, should have Module as their superclass
             	superClass = runtime.getClass("Module");
             }
             return frame.getSelf().callMethod(this, superClass,
                                    frame.getLastFunc(), args, CallType.SUPER);
         } finally {
             clearNoBlock();
             // must reset to false after calling so there's no screwy handling
             // of other calls from this frame
             frame.setCallingZSuper(false);            
         }
     }    
 
     public IRubyObject callSuper(IRubyObject[] args) {
         return callSuper(args, false);
     }
 
     /**
      * Yield to the block passed to the current frame.
      * 
      * @param value The value to yield, either a single value or an array of values
      * @return The result of the yield
      */
     public IRubyObject yield(IRubyObject value) {
         return getFrameBlockOrRaise().yield(this, value, null, null, false);
     }
 
     public void pollThreadEvents() {
         getThread().pollThreadEvents();
     }
     
     public SinglyLinkedList peekCRef() {
         return (SinglyLinkedList)crefStack[crefIndex];
     }
     
     public void setCRef(SinglyLinkedList newCRef) {
         crefStack[++crefIndex] = newCRef;
         expandCrefsIfNecessary();
     }
     
     public void unsetCRef() {
         crefStack[crefIndex--] = null;
     }
     
     public SinglyLinkedList pushCRef(RubyModule newModule) {
         if (crefIndex == -1) {
             crefStack[++crefIndex] = new SinglyLinkedList(newModule, null);
         } else {
             crefStack[crefIndex] = new SinglyLinkedList(newModule, (SinglyLinkedList)crefStack[crefIndex]);
         }
         
         return (SinglyLinkedList)peekCRef();
     }
     
     public RubyModule popCRef() {
         assert !(crefIndex == -1) : "Tried to pop from empty CRef stack";
         
         RubyModule module = (RubyModule)peekCRef().getValue();
         
         SinglyLinkedList next = ((SinglyLinkedList)crefStack[crefIndex--]).getNext();
         
         if (next != null) {
             crefStack[++crefIndex] = next;
         } else {
             crefStack[crefIndex+1] = null;
         }
         
         return module;
     }
 
     public void pushRubyClass(RubyModule currentModule) {
         assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = (RubyModule)parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
 	
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = (RubyModule)parentStack[parentIndex];
 
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = (RubyModule)parentStack[parentIndex];
         } else {
             parentModule = (RubyModule)parentStack[parentIndex-1];
 
         }
         return parentModule.getNonIncludedClass();
     }
 
     public boolean isTopLevel() {
         return parentIndex == 0;
     }
 
     public RubyModule getWrapper() {
         return wrapper;
     }
 
     public void setWrapper(RubyModule wrapper) {
         this.wrapper = wrapper;
     }
 
     public boolean getConstantDefined(String name) {
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         for (SinglyLinkedList cbase = peekCRef(); cbase != null; cbase = cbase.getNext()) {
           result = ((RubyModule) cbase.getValue()).getConstantAt(name);
           if (result != null || runtime.getLoadService().autoload(name) != null) {
               return true;
           }
         } 
         
         return false;
     }
 
     public IRubyObject getConstant(String name) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = peekCRef();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
           RubyModule klass = (RubyModule) cbase.getValue();
           
           // Not sure how this can happen
           //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
           result = klass.getConstantAt(name);
           if (result == null) {
               if (runtime.getLoadService().autoload(name) != null) {
                   continue;
               }
           } else {
               return result;
           }
           cbase = cbase.getNext();
         } while (cbase != null);
         
         //System.out.println("CREF is " + state.threadContext.getCRef().getValue());  
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
 
     public IRubyObject getConstant(String name, RubyModule module) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = module.getCRef();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
           RubyModule klass = (RubyModule) cbase.getValue();
           
           // Not sure how this can happen
           //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
           result = klass.getConstantAt(name);
           if (result == null) {
               if (runtime.getLoadService().autoload(name) != null) {
                   continue;
               }
           } else {
               return result;
           }
           cbase = cbase.getNext();
         } while (cbase != null);
         
         //System.out.println("CREF is " + state.threadContext.getCRef().getValue());  
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
 
     private void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         StringBuffer sb = new StringBuffer(100);
         ISourcePosition position = frame.getPosition();
     
         sb.append(position.getFile()).append(':').append(position.getEndLine());
     
         if (previousFrame != null && previousFrame.getLastFunc() != null) {
             sb.append(":in `").append(previousFrame.getLastFunc()).append('\'');
         } else if (previousFrame == null && frame.getLastFunc() != null) {
             sb.append(":in `").append(frame.getLastFunc()).append('\'');
         }
     
         backtrace.append(backtrace.getRuntime().newString(sb.toString()));
     }
 
     /** 
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace 
      */
     public IRubyObject createBacktrace(int level, boolean nativeException) {
         RubyArray backtrace = runtime.newArray();
         int base = currentBindingFrame();
         int traceSize = frameIndex - level;
-        
+
         if (traceSize <= 0) {
         	return backtrace;
         }
-        
+
         if (nativeException) {
             // assert level == 0;
             addBackTraceElement(backtrace, (Frame) frameStack[frameIndex], null);
         }
         
-        for (int i = traceSize; i > currentBindingFrame(); i--) {
+        for (int i = traceSize; i > base; i--) {
             addBackTraceElement(backtrace, (Frame) frameStack[i], (Frame) frameStack[i-1]);
         }
     
         return backtrace;
     }
     
     public void beginCallArgs() {
         //Block block = getCurrentBlock();
 
         //if (getCurrentIter().isPre() && getCurrentBlock() != null) {
             //pushdownBlocks((Block)getCurrentBlock().getNext());
         //}
         setNoBlock();
         //return block;
     }
 
     public void endCallArgs(){//Block block) {
         //setCurrentBlock(block);
         clearNoBlock();
         //if (getCurrentIter().isPre() && !blockStack.isEmpty()) {
             //popupBlocks();
         //}
     }
     
     public void preAdoptThread() {
         setNoBlock();
         pushFrameNoBlock();
         getCurrentFrame().newScope();
         pushRubyClass(runtime.getObject());
         pushCRef(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushCRef(type);
         pushRubyClass(type); 
         pushFrameCopy();
         getCurrentFrame().newScope();
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
     }
     
     public void postClassEval() {
         popCRef();
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrameNoBlock();
         getCurrentFrame().newScope();
     }
     
     public void postBsfApply() {
         popFrame();
     }
 
     public void preMethodCall(RubyModule implementationClass, RubyModule lastClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         setInBlockIfBlock();
         pushCallFrame(recv, args, name, noSuper ? null : lastClass);
     }
     
     public void postMethodCall() {
         popFrame();
         clearInBlock();
         popRubyClass();
     }
     
     public void preDefMethodInternalCall(RubyModule lastClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper, SinglyLinkedList cref, StaticScope staticScope) {
         RubyModule implementationClass = (RubyModule)cref.getValue();
         setCRef(cref);
         setInBlockIfBlock();
         pushCallFrame(recv, args, name, noSuper ? null : lastClass);
         getCurrentFrame().newScope();
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
         pushRubyClass(implementationClass);
     }
     
     public void postDefMethodInternalCall() {
         popRubyClass();
         popScope();
         popFrame();
         clearInBlock();
         unsetCRef();
     }
     
     // NEW! Push a scope into the frame, since this is now required to use it
     // XXX: This is screwy...apparently Ruby runs internally-implemented methods in their own frames but in the *caller's* scope
     public void preReflectedMethodInternalCall(RubyModule implementationClass, RubyModule lastClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         setInBlockIfBlock();
         pushCallFrame(recv, args, name, noSuper ? null : lastClass);
         getCurrentFrame().setScope(getPreviousFrame().getScope());
     }
     
     public void postReflectedMethodInternalCall() {
         popFrame();
         clearInBlock();
         popRubyClass();
     }
     
     public void preInitCoreClasses() {
         setNoBlock();
         pushFrameNoBlock();
         getCurrentFrame().newScope();
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void preInitBuiltinClasses(RubyClass objectClass, IRubyObject topSelf) {
         pushRubyClass(objectClass);
         setCRef(objectClass.getCRef());
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
     }
     
     public void preNodeEval(RubyModule newWrapper, RubyModule rubyClass, IRubyObject self) {
         setWrapper(newWrapper);
         pushRubyClass(rubyClass);
         pushCallFrame(self, IRubyObject.NULL_ARRAY, null, null);
         
         setCRef(rubyClass.getCRef());
     }
     
     public void postNodeEval(RubyModule newWrapper) {
         popFrame();
         popRubyClass();
         setWrapper(newWrapper);
         unsetCRef();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         pushCRef(executeUnderClass);
         pushCallFrame(null, frame.getArgs(), frame.getLastFunc(), frame.getLastClass());
         getCurrentFrame().setScope(getPreviousFrame().getScope());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popRubyClass();
         popCRef();
     }
     
     public void preMproc() {
         setInBlock();
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
         clearInBlock();
     }
     
     public void preRunThread(Frame currentFrame, Block currentBlock) {
         pushFrame(currentFrame);
         // create a new eval state for the the block frame (since it has been adopted by the created thread)
         // XXX: this is kind of a hack, since eval state holds ThreadContext, and when it's created it's in the other thread :(
         // we'll want to revisit these issues of block ownership since the block is created in one thread and used in another
         //currentBlock.getFrame().setEvalState(new EvaluationState(runtime, currentBlock.getFrame().getSelf()));
         blockStack = currentBlock;
     }
     
     public void preTrace() {
         pushFrameNoBlock();
     }
     
     public void postTrace() {
         popFrame();
     }
     
     public void preBlockPassEval(Block block) {
         pushBlock(block);
         setBlockAvailable();
     }
     
     public void postBlockPassEval() {
         clearBlockAvailable();
         popBlock();
     }
     
     public void preForLoopEval(Block block) {
         pushBlock(block);
         setBlockAvailable();
     }
     
     public void postForLoopEval() {
         clearBlockAvailable();
         popBlock();
     }
     
     public void preIterEval(Block block) {
         pushBlock(block);
     }
     
     public void postIterEval() {
         popBlock();
     }
     
     public void preToProc(Block block) {
         setBlockAvailable();
         pushBlock(block);
     }
     
     public void postToProc() {
         clearBlockAvailable();
         popBlock();
     }
     
     public void preProcBlockCall() {
         setInBlock();
         getCurrentFrame().setIter(Iter.ITER_CUR);
     }
     
     public void postProcBlockCall() {
         clearInBlock();
     }
     
     public void preYieldSpecificBlock(Block specificBlock, RubyModule klass) {
         restoreBlockState(specificBlock, klass);
     }
 
     public void preEvalWithBinding(Block block) {
         pushBindingFrame(frameIndex);
         pushFrame(block.getFrame());
         setCRef(block.getCRef());        
         getCurrentFrame().setScope(block.getScope());
         pushRubyClass(block.getKlass()); 
         pushIter(block.getIter());
     }
 
     public void postEvalWithBinding(Block block) {
         popIter();
         popFrame();
         unsetCRef();
         popRubyClass();
         popBindingFrame();
     }
     
     public void postYield(Block block) {
         flushBlockState(block);
     }
 
     public void preRootNode(DynamicScope scope) {
         pushScope(scope);
         getCurrentFrame().newScope();
     }
 
     public void postRootNode() {
         popScope();
     }
 
     /**
      * Is this thread actively tracing at this moment.
      * 
      * @return true if so
      * @see org.jruby.IRuby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
 
     /**
      * Set whether we are actively tracing or not on this thread.
      * 
      * @param isWithinTrace true is so
      * @see org.jruby.IRuby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      * 
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      * 
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
index f6e3d5b8f0..8d142e1e9b 100644
--- a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
@@ -1,681 +1,685 @@
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
-            return getRuntime().newString(relativePath);
+            try {
+                return getRuntime().newString(new File(relativePath).getCanonicalPath());
+            } catch(IOException e) {
+                return getRuntime().newString(relativePath);
+            }
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
         return getRuntime().newRubyFileStat(name.toString());
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
