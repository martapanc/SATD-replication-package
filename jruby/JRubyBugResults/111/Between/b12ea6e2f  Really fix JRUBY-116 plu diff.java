diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 1deecf8c6c..1bc8c96ae7 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1092 +1,1091 @@
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
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Random;
 import java.util.Stack;
 
 import org.jruby.ast.Node;
 import org.jruby.common.RubyWarnings;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.SocketLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.ArrayMetaClass;
 import org.jruby.runtime.builtin.meta.BignumMetaClass;
 import org.jruby.runtime.builtin.meta.BindingMetaClass;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.FixnumMetaClass;
 import org.jruby.runtime.builtin.meta.HashMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.builtin.meta.IntegerMetaClass;
 import org.jruby.runtime.builtin.meta.ModuleMetaClass;
 import org.jruby.runtime.builtin.meta.NumericMetaClass;
 import org.jruby.runtime.builtin.meta.ObjectMetaClass;
 import org.jruby.runtime.builtin.meta.ProcMetaClass;
 import org.jruby.runtime.builtin.meta.StringMetaClass;
 import org.jruby.runtime.builtin.meta.SymbolMetaClass;
 import org.jruby.runtime.builtin.meta.TimeMetaClass;
-import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby implements IRuby {
 	private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "etc", "nkf" };
     
 	private CacheMap cacheMap = new CacheMap();
     private ThreadService threadService = new ThreadService(this);
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable ioHandlers = new Hashtable();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private RubyProc traceFunction;
     private boolean isWithinTrace = false;
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
 
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
     private String currentDirectory = JRubyFile.getFileProperty("user.dir");
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
     
     private IRubyObject verbose;
 
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
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err) {
     	this.in = in;
     	this.out = out;
     	this.err = err;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby getDefaultInstance() {
         Ruby ruby = new Ruby(System.in, System.out, System.err);
         ruby.init();
         
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err) {
         Ruby ruby = new Ruby(in, out, err);
         ruby.init();
         
         return ruby;
     }
 
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScript(String script) {
         return eval(parse(script, "<script>"));
     }
 
     public IRubyObject eval(Node node) {
         try {
             return getCurrentContext().getFrameEvalState().begin(node);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
 	            return (IRubyObject)je.getSecondaryData();
         	} else {
         		throw je;
         	}
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
     public RubyClass defineClass(String name, RubyClass superClass) {
         return defineClassUnder(name, superClass, objectClass.getCRef());
     }
     
     public RubyClass defineClassUnder(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
         if (superClass == null) {
             superClass = objectClass;
         }
 
         return superClass.newSubClass(name, parentCRef);
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
             module = (RubyModule) tc.getRubyClass().setConstant(name, 
             		defineModule(name)); 
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
         
         javaSupport = new JavaSupport(this);
         
         initLibraries();
         
         tc.preInitCoreClasses();
 
         initCoreClasses();
 
         RubyGlobal.createGlobals(this);
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         initBuiltinClasses();
         
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
         
         // Load additional definitions and hacks from etc.rb
         getLoadService().smartLoad("builtin/etc.rb");
     }
 
     private void initLibraries() {
         loadService = new LoadService(this);
         loadService.registerBuiltin("java.rb", new BuiltinScript("javasupport"));
         loadService.registerBuiltin("socket.rb", new SocketLibrary());
         loadService.registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
         	loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
         }
         
         
         loadService.registerBuiltin("jruby.rb", new JRubyLibrary());
         loadService.registerBuiltin("stringio.rb", new StringIOLibrary());
         loadService.registerBuiltin("strscan.rb", new StringScannerLibrary());
         loadService.registerBuiltin("zlib.rb", new ZlibLibrary());
     }
 
     private void initCoreClasses() {
         ObjectMetaClass objectMetaClass = new ObjectMetaClass(this);
         objectMetaClass.initializeClass();
         
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = new ModuleMetaClass(this, objectClass);
         objectClass.setConstant("Module", moduleClass);
         RubyClass classClass = new RubyClass(this, null /* Would be Class if it could */, moduleClass, null, "Class");
         objectClass.setConstant("Class", classClass);
 
         // I don't think the containment is correct here (parent cref)
         RubyClass metaClass = objectClass.makeMetaClass(classClass, objectMetaClass.getCRef());
         metaClass = moduleClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
         metaClass = classClass.makeMetaClass(metaClass, objectMetaClass.getCRef());
 
         ((ObjectMetaClass) moduleClass).initializeBootstrapClass();
         
         kernelModule = RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         RubyClass.createClassClass(classClass);
 
         RubyNil.createNilClass(this);
 
         // We cannot define this constant until nil itself was made
         objectClass.defineConstant("NIL", getNil());
         
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         stringClass = new StringMetaClass(this);
         stringClass.initializeClass();
         new SymbolMetaClass(this).initializeClass();
         RubyThreadGroup.createThreadGroupClass(this);
         RubyThread.createThreadClass(this);
         RubyException.createExceptionClass(this);
         
         RubyPrecision.createPrecisionModule(this);
         new NumericMetaClass(this).initializeClass();
         new IntegerMetaClass(this).initializeClass();        
         new FixnumMetaClass(this).initializeClass();
         new HashMetaClass(this).initializeClass();
         new IOMetaClass(this).initializeClass();
         new ArrayMetaClass(this).initializeClass();
         
         Java.createJavaModule(this);
         RubyStruct.createStructClass(this);
         RubyFloat.createFloatClass(this);
         
         new BignumMetaClass(this).initializeClass();
         new BindingMetaClass(this).initializeClass();
         
         RubyMath.createMathModule(this); // depends on all numeric types
         RubyRegexp.createRegexpClass(this);
         RubyRange.createRangeClass(this);
         RubyObjectSpace.createObjectSpaceModule(this);
         RubyGC.createGCModule(this);
         
         new ProcMetaClass(this).initializeClass();
         
         RubyMethod.createMethodClass(this);
         RubyMatchData.createMatchDataClass(this);
         RubyMarshal.createMarshalModule(this);
         RubyDir.createDirClass(this);
         RubyFileTest.createFileTestModule(this);
         
         new FileMetaClass(this).initializeClass(); // depends on IO, FileTest
         
         RubyProcess.createProcessModule(this);
         new TimeMetaClass(this).initializeClass();
         RubyUnboundMethod.defineUnboundMethodClass(this);
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = defineClass("StandardError", exceptionClass);
         RubyClass runtimeError = defineClass("RuntimeError", standardError);
         RubyClass ioError = defineClass("IOError", standardError);
         RubyClass scriptError = defineClass("ScriptError", exceptionClass);
         RubyClass nameError = defineClass("NameError", standardError);
         RubyClass rangeError = defineClass("RangeError", standardError);
         defineClass("SystemExit", exceptionClass);
         defineClass("Fatal", exceptionClass);
         defineClass("Interrupt", exceptionClass);
         defineClass("SignalException", exceptionClass);
         defineClass("TypeError", standardError);
         defineClass("ArgumentError", standardError);
         defineClass("IndexError", standardError);
         defineClass("SyntaxError", scriptError);
         defineClass("LoadError", scriptError);
         defineClass("NotImplementedError", scriptError);
         defineClass("NoMethodError", nameError);
         defineClass("SecurityError", standardError);
         defineClass("NoMemoryError", exceptionClass);
         defineClass("RegexpError", standardError);
         defineClass("EOFError", ioError);
         defineClass("LocalJumpError", standardError);
         defineClass("ThreadError", standardError);
         defineClass("SystemStackError", exceptionClass);
         defineClass("ZeroDivisionError", standardError);
         // FIXME: Actually this somewhere
         defineClass("FloatDomainError", rangeError);
         NativeException.createClass(this, runtimeError);
         systemCallError = defineClass("SystemCallError", standardError);
         errnoModule = defineModule("Errno");
         
         initErrnoErrors();
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
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         errnoModule.defineClassUnder(name, systemCallError).defineConstant("Errno", newFixnum(i));
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
 
     public Node parse(Reader content, String file) {
         return parser.parse(file, content);
     }
 
     public Node parse(String content, String file) {
         return parser.parse(file, content);
     }
 
     public Parser getParser() {
         return parser;
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
         return new PrintStream(((RubyIO) getGlobalVariables().get("$stderr")).getOutStream());
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
 
         RubyArray backtrace = (RubyArray) excp.callMethod("backtrace");
 
         PrintStream errorStream = getErrorStream();
 		if (backtrace.isNil()) {
             ThreadContext tc = getCurrentContext();
             
             if (tc.getSourceFile() != null) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceLine());
             }
         } else if (backtrace.getLength() == 0) {
             printErrorPos(errorStream);
         } else {
             IRubyObject mesg = backtrace.first(IRubyObject.NULL_ARRAY);
 
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
 
         if (!backtrace.isNil()) {
             excp.printBacktrace(errorStream);
         }
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
 
             /* default visibility is private at loading toplevel */
             context.setCurrentVisibility(Visibility.PRIVATE);
 
         	Node node = parse(source, scriptName);
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
             
             /* default visibility is private at loading toplevel */
             context.setCurrentVisibility(Visibility.PRIVATE);
             
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
     public synchronized void callTraceFunction(
         String event,
         ISourcePosition position,
         IRubyObject self,
         String name,
         IRubyObject type) {
         if (!isWithinTrace && traceFunction != null) {
             ThreadContext tc = getCurrentContext();
             isWithinTrace = true;
 
             ISourcePosition savePosition = tc.getPosition();
             String file = position.getFile();
 
             if (file == null) {
                 file = "(ruby)";
             }
             if (type == null) {
 				type = getFalse();
 			}
             tc.preTrace();
 
             try {
                 traceFunction
                     .call(new IRubyObject[] {
                         newString(event),
                         newString(file),
                         newFixnum(position.getEndLine()),
                         name != null ? RubySymbol.newSymbol(this, name) : getNil(),
                         self != null ? self: getNil(),
                         type });
             } finally {
                 tc.postTrace();
                 tc.setPosition(savePosition);
                 isWithinTrace = false;
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
     
     public RubyFileStat newRubyFileStat(File file) {
     	return new RubyFileStat(this, JRubyFile.create(currentDirectory,file.getPath()));
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
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 8b6ed2927f..09cfc5fc0b 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,989 +1,1007 @@
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
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.Iterator;
 import java.util.List;
 import java.util.StringTokenizer;
 import java.util.regex.Pattern;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ICallable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.builtin.meta.StringMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.UnsynchronizedStack;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public static RubyModule createKernelModule(IRuby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineModuleFunction("Array", callbackFactory.getSingletonMethod("new_array", IRubyObject.class));
         module.defineModuleFunction("Float", callbackFactory.getSingletonMethod("new_float", IRubyObject.class));
         module.defineModuleFunction("Integer", callbackFactory.getSingletonMethod("new_integer", IRubyObject.class));
         module.defineModuleFunction("String", callbackFactory.getSingletonMethod("new_string", IRubyObject.class));
         module.defineModuleFunction("`", callbackFactory.getSingletonMethod("backquote", IRubyObject.class));
         // TODO: Implement Kernel#abort
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineModuleFunction("autoload", callbackFactory.getSingletonMethod("autoload", IRubyObject.class, IRubyObject.class));
         // TODO: Implement Kernel#autoload?
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         // TODO: Implement Kernel#callcc
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRubyObject.class));
         module.defineModuleFunction("chomp", callbackFactory.getOptSingletonMethod("chomp"));
         module.defineModuleFunction("chomp!", callbackFactory.getOptSingletonMethod("chomp_bang"));
         module.defineModuleFunction("chop", callbackFactory.getSingletonMethod("chop"));
         module.defineModuleFunction("chop!", callbackFactory.getSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineModuleFunction("exit", callbackFactory.getOptSingletonMethod("exit"));
         module.defineModuleFunction("exit!", callbackFactory.getOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineModuleFunction("format", callbackFactory.getOptSingletonMethod("sprintf"));
         module.defineModuleFunction("gets", callbackFactory.getOptSingletonMethod("gets"));
         module.defineModuleFunction("global_variables", callbackFactory.getSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineAlias("iterator?", "block_given?");
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineModuleFunction("local_variables", callbackFactory.getSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineModuleFunction("p", callbackFactory.getOptSingletonMethod("p"));
         module.defineModuleFunction("print", callbackFactory.getOptSingletonMethod("print"));
         module.defineModuleFunction("printf", callbackFactory.getOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineModuleFunction("puts", callbackFactory.getOptSingletonMethod("puts"));
         module.defineAlias("raise", "fail");
         module.defineModuleFunction("rand", callbackFactory.getOptSingletonMethod("rand"));
         module.defineModuleFunction("readline", callbackFactory.getOptSingletonMethod("readline"));
         module.defineModuleFunction("readlines", callbackFactory.getOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRubyObject.class));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRubyObject.class));
         module.defineModuleFunction("select", callbackFactory.getOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRubyObject.class));
         module.defineModuleFunction("sleep", callbackFactory.getSingletonMethod("sleep", IRubyObject.class));
         module.defineModuleFunction("split", callbackFactory.getOptSingletonMethod("split"));
         module.defineAlias("sprintf", "format");
         module.defineModuleFunction("srand", callbackFactory.getOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineModuleFunction("system", callbackFactory.getOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineAlias("exec", "system");
         // TODO: Implement Kernel#test (partial impl)
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineModuleFunction("warn", callbackFactory.getSingletonMethod("warn", IRubyObject.class));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRubyObject.class));
         
         // Object methods
         module.definePublicModuleFunction("==", objectCallbackFactory.getMethod("equal", IRubyObject.class));
         module.defineAlias("===", "==");
         module.defineAlias("eql?", "==");
         module.definePublicModuleFunction("to_s", objectCallbackFactory.getMethod("to_s"));
         module.definePublicModuleFunction("nil?", objectCallbackFactory.getMethod("nil_p"));
         module.definePublicModuleFunction("to_a", callbackFactory.getSingletonMethod("to_a"));
         module.definePublicModuleFunction("hash", objectCallbackFactory.getMethod("hash"));
         module.definePublicModuleFunction("id", objectCallbackFactory.getMethod("id"));
         module.defineAlias("__id__", "id");
         module.defineAlias("object_id", "id");
         module.definePublicModuleFunction("is_a?", objectCallbackFactory.getMethod("kind_of", IRubyObject.class));
         module.defineAlias("kind_of?", "is_a?");
         module.definePublicModuleFunction("dup", objectCallbackFactory.getMethod("dup"));
         module.definePublicModuleFunction("equal?", objectCallbackFactory.getMethod("same", IRubyObject.class));
         module.definePublicModuleFunction("type", objectCallbackFactory.getMethod("type_deprecated"));
         module.definePublicModuleFunction("class", objectCallbackFactory.getMethod("type"));
         module.definePublicModuleFunction("inspect", objectCallbackFactory.getMethod("inspect"));
         module.definePublicModuleFunction("=~", objectCallbackFactory.getMethod("match", IRubyObject.class));
         module.definePublicModuleFunction("clone", objectCallbackFactory.getMethod("rbClone"));
         module.definePublicModuleFunction("display", objectCallbackFactory.getOptMethod("display"));
         module.definePublicModuleFunction("extend", objectCallbackFactory.getOptMethod("extend"));
         module.definePublicModuleFunction("freeze", objectCallbackFactory.getMethod("freeze"));
         module.definePublicModuleFunction("frozen?", objectCallbackFactory.getMethod("frozen"));
         module.defineModuleFunction("initialize_copy", objectCallbackFactory.getMethod("initialize_copy", IRubyObject.class));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.definePublicModuleFunction("instance_of?", objectCallbackFactory.getMethod("instance_of", IRubyObject.class));
         module.definePublicModuleFunction("instance_variables", objectCallbackFactory.getMethod("instance_variables"));
         module.definePublicModuleFunction("instance_variable_get", objectCallbackFactory.getMethod("instance_variable_get", IRubyObject.class));
         module.definePublicModuleFunction("instance_variable_set", objectCallbackFactory.getMethod("instance_variable_set", IRubyObject.class, IRubyObject.class));
         module.definePublicModuleFunction("method", objectCallbackFactory.getMethod("method", IRubyObject.class));
         module.definePublicModuleFunction("methods", objectCallbackFactory.getOptMethod("methods"));
         module.definePublicModuleFunction("private_methods", objectCallbackFactory.getMethod("private_methods"));
         module.definePublicModuleFunction("protected_methods", objectCallbackFactory.getMethod("protected_methods"));
         module.definePublicModuleFunction("public_methods", objectCallbackFactory.getOptMethod("public_methods"));
         module.defineModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRubyObject.class));
         module.definePublicModuleFunction("respond_to?", objectCallbackFactory.getOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.definePublicModuleFunction("singleton_methods", objectCallbackFactory.getMethod("singleton_methods"));
         module.definePublicModuleFunction("taint", objectCallbackFactory.getMethod("taint"));
         module.definePublicModuleFunction("tainted?", objectCallbackFactory.getMethod("tainted"));
         module.definePublicModuleFunction("untaint", objectCallbackFactory.getMethod("untaint"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc());
     }
 
     public static IRubyObject autoload(IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         loadService.addAutoload(symbol.asSymbol(), new IAutoloadMethod() {
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(IRuby, String)
              */
             public IRubyObject load(IRuby runtime, String name) {
                 loadService.require(file.toString());
                 return runtime.getObject().getConstant(name);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = recv.callMethod("inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
             noClass ? "" : ":", noClass ? "" : recv.getType().getName()});
 
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg) : runtime.newNoMethodError(msg);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args) {
         String arg = args[0].convertToString().toString();
 
         // Should this logic be pushed into RubyIO Somewhere?
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
         	// exec process, create IO with process
         	try {
                 // TODO: may need to part cli parms out ourself?
                 Process p = Runtime.getRuntime().exec(command);
                 RubyIO io = new RubyIO(recv.getRuntime(), p);
                 ThreadContext tc = recv.getRuntime().getCurrentContext();
         		
         	    if (tc.isBlockGiven()) {
         	        try {
         	            tc.yield(io);
         	            
             	        return recv.getRuntime().getNil();
         	        } finally {
         	            io.close();
         	        }
         	    }
 
                 return io;
         	} catch (IOException ioe) {
         		throw recv.getRuntime().newIOErrorFromException(ioe);
         	}
         } 
 
         return ((FileMetaClass) recv.getRuntime().getClass("File")).open(args);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         IRubyObject line = argsFile.internalGets(args);
 
         recv.getRuntime().getCurrentContext().setLastline(line);
 
         return line;
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
-        return object.callMethod("to_a");
+        IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
+        
+        if (value.isNil()) {
+            ICallable method = object.getMetaClass().searchMethod("to_a");
+            
+            if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
+                return recv.getRuntime().newArray(object);
+            }
+            
+            // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
+            value = object.callMethod("to_a");
+            if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
+                throw recv.getRuntime().newTypeError("`to_a' did not return Array");
+               
+            }
+        }
+        
+        return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         return object.callMethod("to_f");
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         return object.callMethod("to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod("to_s");
     }
     
     
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod("write", args[i].callMethod("inspect"));
                 defout.callMethod("write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         
         defout.callMethod("puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
         defout.callMethod("print", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
             	defout = args[0];
             	args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod("write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         RubyArray lines = recv.getRuntime().newArray();
 
         IRubyObject line = argsFile.internalGets(args);
         while (!line.isNil()) {
             lines.append(line);
 
             line = argsFile.internalGets(args);
         }
 
         return lines;
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(IRuby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).sub_bang(args);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chop_bang(IRubyObject recv) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     public static IRubyObject chop(IRubyObject recv) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().setLastline(dup);
         return dup;
     }
 
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern) {
         return getLastlineString(recv.getRuntime()).scan(pattern);
     }
 
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return IOMetaClass.select_static(recv.getRuntime(), args);
     }
 
     public static IRubyObject sleep(IRubyObject recv, IRubyObject seconds) {
     	long milliseconds = (long) (seconds.convertToFloat().getDoubleValue() * 1000);
     	long startTime = System.currentTimeMillis();
     	
     	RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         try {
         	rubyThread.sleep(milliseconds);
         } catch (InterruptedException iExcptn) {
         }
 
         return recv.getRuntime().newFixnum(
         		Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
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
 
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
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
     public static RubyArray local_variables(IRubyObject recv) {
         final IRuby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         ThreadContext tc = runtime.getCurrentContext();
 
         if (tc.getFrameScope().getLocalNames() != null) {
             for (int i = 2; i < tc.getFrameScope().getLocalNames().length; i++) {
 				String variableName = (String) tc.getFrameScope().getLocalNames()[i];
                 if (variableName != null) {
                     localVariables.append(runtime.newString(variableName));
                 }
             }
         }
 
         Iterator dynamicNames = tc.getCurrentDynamicVars().names().iterator();
         while (dynamicNames.hasNext()) {
             String name = (String) dynamicNames.next();
             localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv) {
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().isFBlockGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArray(args);
         newArgs.shift();
 
         return ((StringMetaClass)str.getMetaClass()).format.call(recv.getRuntime(), str, str.getMetaClass(), "%", new IRubyObject[] {newArgs}, false);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args) {
         recv.checkArgumentCount(args, 0, 3); 
         IRuby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException(RubyException.newInstance(runtime.getClass("RuntimeError"), args));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod("exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod("exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
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
     public static IRubyObject require(IRubyObject recv, IRubyObject name) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args) {
         RubyString file = (RubyString)args[0];
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
         RubyString src = (RubyString) args[0];
         IRubyObject scope = null;
         String file = "(eval)";
         
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
         }
         // FIXME: line number is not supported yet
         //int line = args.length > 3 ? RubyNumeric.fix2int(args[3]) : 1;
 
         src.checkSafeString();
         ThreadContext tc = runtime.getCurrentContext();
 
         if (scope == null && tc.getPreviousFrame() != null) {
             try {
                 tc.preKernelEval();
                 return recv.evalSimple(src, file);
             } finally {
                 tc.postKernelEval();
             }
         }
         return recv.evalWithBinding(src, scope, file);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag) {
         try {
             return recv.getRuntime().getCurrentContext().yield(tag);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ThrowJump) {
 	            if (je.getPrimaryData().equals(tag.asSymbol())) {
 	                return (IRubyObject)je.getSecondaryData();
 	            }
         	}
        		throw je;
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
         JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
         String tag = args[0].asSymbol();
         IRubyObject value = args.length > 1 ? args[1] : recv.getRuntime().getNil();
         RubyException nameException = new RubyException(runtime, runtime.getClass("NameError"), "uncaught throw '" + tag + '\'');
         
         je.setPrimaryData(tag);
         je.setSecondaryData(value);
         je.setTertiaryData(nameException);
         
         throw je;
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
     	IRubyObject out = recv.getRuntime().getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType("IO", "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
     	return recv.getRuntime().getNil();
     }
 
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId) {
         return recv.getRuntime().getNil();
     }
 
     public static RubyProc proc(IRubyObject recv) {
         return RubyProc.newProc(recv.getRuntime(), true);
     }
 
     public static IRubyObject loop(IRubyObject recv) {
         while (true) {
             recv.getRuntime().getCurrentContext().yield(recv.getRuntime().getNil());
 
             Thread.yield();
         }
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         IRuby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = runInShell(runtime, new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return recv.getRuntime().newString(output.toString());
     }
     
     private static final Pattern PATH_SEPARATORS = Pattern.compile("[/\\\\]");
     
     /**
      * For the first full token on the command, most likely the actual executable to run, replace
      * all dir separators with that which is appropriate for the current platform. Return the new
      * with this executable string at the beginning.
      * 
      * @param command The all-forward-slashes command to be "fixed"
      * @return The "fixed" full command line
      */
     private static String repairDirSeps(String command) {
         String executable = "", remainder = "";
         command = command.trim();
         if (command.startsWith("'")) {
             String [] tokens = command.split("'", 3);
             executable = "'"+tokens[1]+"'";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else if (command.startsWith("\"")) {
             String [] tokens = command.split("\"", 3);
             executable = "\""+tokens[1]+"\"";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else {
             String [] tokens = command.split(" ", 2);
             executable = tokens[0];
             if (tokens.length > 1)
                 remainder = " "+tokens[1];
         }
         
         // Matcher.replaceAll treats backslashes in the replacement string as escaped characters
         String replacement = File.separator;
         if (File.separatorChar == '\\')
             replacement = "\\\\";
             
         return PATH_SEPARATORS.matcher(executable).replaceAll(replacement) + remainder;
                 }
 
     private static List parseCommandLine(IRubyObject[] rawArgs) {
         // first parse the first element of rawArgs since this may contain
         // the whole command line
         String command = rawArgs[0].toString();
         UnsynchronizedStack args = new UnsynchronizedStack();
         StringTokenizer st = new StringTokenizer(command, " ");
         String quoteChar = null;
 
         while (st.hasMoreTokens()) {
             String token = st.nextToken();
             if (quoteChar == null) {
                 // not currently in the middle of a quoted token
                 if (token.startsWith("'") || token.startsWith("\"")) {
                     // note quote char and remove from beginning of token
                     quoteChar = token.substring(0, 1);
                     token = token.substring(1);
                 }
                 if (quoteChar!=null && token.endsWith(quoteChar)) {
                     // quoted token self contained, remove from end of token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // add new token to list
                 args.push(token);
             } else {
                 // in the middle of quoted token
                 if (token.endsWith(quoteChar)) {
                     // end of quoted token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // update token at end of list
                 token = args.pop() + " " + token;
                 args.push(token);
             }
         }
         
         // now append the remaining raw args to the cooked arg list
         for (int i=1;i<rawArgs.length;i++) {
             args.push(rawArgs[i].toString());
         }
         
         return args;
     }
         
     private static boolean isRubyCommand(String command) {
         command = command.trim();
         String [] spaceDelimitedTokens = command.split(" ", 2);
         String [] slashDelimitedTokens = spaceDelimitedTokens[0].split("/");
         String finalToken = slashDelimitedTokens[slashDelimitedTokens.length-1];
         if (finalToken.indexOf("ruby") != -1 || finalToken.endsWith(".rb"))
             return true;
         else
             return false;
     }
     
     private static class InProcessScript extends Thread {
     	private String[] argArray;
     	private InputStream in;
     	private PrintStream out;
     	private PrintStream err;
     	private int result;
     	
     	public InProcessScript(String[] argArray, InputStream in, PrintStream out, PrintStream err) {
     		this.argArray = argArray;
     		this.in = in;
     		this.out = out;
     		this.err = err;
     	}
 
 		public int getResult() {
 			return result;
 		}
 
 		public void setResult(int result) {
 			this.result = result;
 		}
 		
         public void run() {
             result = new Main(in, out, err).run(argArray);
         }
     }
 
     public static int runInShell(IRuby runtime, IRubyObject[] rawArgs, OutputStream output) {
         try {
             // startup scripts set jruby.shell to /bin/sh for Unix, cmd.exe for Windows
             String shell = System.getProperty("jruby.shell");
             rawArgs[0] = runtime.newString(repairDirSeps(rawArgs[0].toString()));
             Process aProcess = null;
             InProcessScript ipScript = null;
             
             if (isRubyCommand(rawArgs[0].toString())) {
                 List args = parseCommandLine(rawArgs);
                 PrintStream redirect = new PrintStream(output);
                 String command = (String)args.get(0);
 
                 String[] argArray = new String[args.size()-1];
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 args.subList(startIndex,args.size()).toArray(argArray);
 
                 // FIXME: Where should we get in and err from?
                 ipScript = new InProcessScript(argArray, System.in, redirect, redirect);
                 
                 // execute ruby command in-process
                 ipScript.start();
                 ipScript.join();
             } else if (shell != null && rawArgs.length == 1) {
                 // execute command with sh -c or cmd.exe /c
                 // this does shell expansion of wildcards
                 String shellSwitch = shell.endsWith("sh") ? "-c" : "/c";
                 String[] argArray = new String[3];
                 argArray[0] = shell;
                 argArray[1] = shellSwitch;
                 argArray[2] = rawArgs[0].toString();
                 aProcess = Runtime.getRuntime().exec(argArray);
             } else {
                 // execute command directly, no wildcard expansion
                 if (rawArgs.length > 1) {
                     String[] argArray = new String[rawArgs.length];
                     for (int i=0;i<rawArgs.length;i++) {
                         argArray[i] = rawArgs[i].toString();
                     }
                     aProcess = Runtime.getRuntime().exec(argArray);
                 } else {
                     aProcess = Runtime.getRuntime().exec(rawArgs[0].toString());
                 }
             }
             
             if (aProcess != null) {
                 InputStream processOutput = aProcess.getInputStream();
                 
                 // Fairly innefficient impl, but readLine is unable to tell
                 // whether the last line in a process ended with a newline or not.
                 int b;
                 boolean crSeen = false;
                 while ((b = processOutput.read()) != -1) {
                     if (b == '\r') {
                         crSeen = true;
                     } else {
                         if (crSeen) {
                             if (b != '\n') {
                                 output.write('\r');
                             }
                             crSeen = false;
                         }
                         output.write(b);
                     }
                 }
                 if (crSeen) {
                     output.write('\r');
                 }
                 aProcess.getErrorStream().close();
                 aProcess.getOutputStream().close();
                 processOutput.close();
                 
                 return aProcess.waitFor();
             } else if (ipScript != null) {
             	return ipScript.getResult();
             } else {
                 return 0;
             }
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = 
             	(RubyInteger) args[0].convertToType("Integer", "to_i", true);
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
         	// Not sure how well this works, but it works much better than
         	// just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
 			  recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
 			  runtime.getRandom().nextInt(Math.abs((int)runtime.getRandomSeed())));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             RubyInteger integerCeil = (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             ceil = integerCeil.getLongValue();
             ceil = Math.abs(ceil);
             if (ceil > Integer.MAX_VALUE) {
                 throw recv.getRuntime().newNotImplementedError("Random values larger than Integer.MAX_VALUE not supported");
             }
         } else {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             double result = recv.getRuntime().getRandom().nextDouble();
             return RubyFloat.newFloat(recv.getRuntime(), result);
         }
 		return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextInt((int) ceil));
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         int resultCode = runInShell(runtime, args, output);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index f2a3596837..c97bf7bb44 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1176 +1,1195 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.Iter;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
 	
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
 
 	public RubyObject(IRuby runtime, RubyClass metaClass) {
         this(runtime, metaClass, true);
     }
 
     public RubyObject(IRuby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public MetaClass makeMetaClass(RubyClass type, SinglyLinkedList parentCRef) {
         MetaClass newMetaClass = type.newSingletonClass(parentCRef);
 		
 		if (!isNil()) {
 			setMetaClass(newMetaClass);
 		}
         newMetaClass.attachToObject(this);
         return newMetaClass;
     }
 
     public boolean singletonMethodsAllowed() {
         return true;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod("==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return ((RubyString) callMethod("to_s")).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public IRuby getRuntime() {
         return metaClass.getRuntime();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
 	    			instanceVariables = new HashMap();
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = instanceVariables;
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      * 
      */
     public RubyClass getMetaClass() {
     	// TODO: Can we assert MetaClass on metaClass here?  This should simplify some callers
         if (isNil()) {
             return getRuntime().getClass("NilClass");
         }
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen " + getMetaClass().getName());
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public boolean isTrue() {
         return !isNil();
     }
 
     public boolean isFalse() {
         return isNil();
     }
 
     public boolean respondsTo(String name) {
         return getMetaClass().isMethodBound(name, false);
     }
 
     // Some helper functions:
 
     public int checkArgumentCount(IRubyObject[] args, int min, int max) {
         if (args.length < min) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + min + ")");
         }
         if (max > -1 && args.length > max) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + max + ")");
         }
         return args.length;
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().ancestors().includes(type);
     }
 
     /** rb_singleton_class
      *
      */
     public MetaClass getSingletonClass() {
         RubyClass type = getMetaClass();
         if (!type.isSingleton()) { 
             type = makeMetaClass(type, type.getCRef());
         }
 
         assert type instanceof MetaClass; 
 
 		if (!isNil()) {
 			type.setTaint(isTaint());
 			type.setFrozen(isFrozen());
 		}
 
         return (MetaClass)type;
     }
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     public void addSingletonMethod(String name, ICallable method) {
         getSingletonClass().addMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod("initialize_copy", original);        
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
         
         return this;
     }
 
     /**
      * 
      */
     public IRubyObject callMethod(String name, IRubyObject[] args) {
         return callMethod(getMetaClass(), name, args, CallType.FUNCTIONAL);
     }
 
     /**
      * 
      */
     public IRubyObject callMethod(String name, IRubyObject[] args,
             CallType callType) {
         return callMethod(getMetaClass(), name, args, callType);
     }
 
     /**
      * 
      */
     public IRubyObject callMethod(RubyModule context, String name, IRubyObject[] args, 
             CallType callType) {
         assert args != null;
         ICallable method = null;
         
         method = context.searchMethod(name);
         
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(getRuntime().getCurrentContext().getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'");
             }
 
             // store call information so method_missing impl can use it
             getRuntime().getCurrentContext().setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod("method_missing", newArgs);
         }
         
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = context.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
         
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         IRubyObject result = method.call(getRuntime(), this, implementer, name, args, false);
         
         return result;
     }
 
     public IRubyObject callMethod(String name) {
         return callMethod(name, IRubyObject.NULL_ARRAY);
     }
 
     /**
      * rb_funcall
      * 
      */
     public IRubyObject callMethod(String name, IRubyObject arg) {
         return callMethod(name, new IRubyObject[] { arg });
     }
     
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
     	
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
     	}
     	
     	IRubyObject variable = getInstanceVariable(varName); 
     	
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable; 
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
     
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
     	
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
     	}
     	
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
     
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value, 
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         EvaluationState state = getRuntime().getCurrentContext().getFrameEvalState();
         IRubyObject oldSelf = state.getSelf();
         state.setSelf(this);
         try {
             return state.begin(n);
         } finally {
             state.setSelf(oldSelf);
         }
     }
 
     public void callInit(IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
         
         tc.setIfBlockAvailable();
         try {
             callMethod("initialize", args);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
 
     public void extendObject(RubyModule module) {
         getSingletonClass().includeModule(module);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
-    /** 
-     * Converts this object to type 'targetType' using 'convertMethod' method.
-     * 
-     * @see IRubyObject.convertToType
-	 */
+    /*
+     * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
+     */
+    public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
+        if (targetType.equals(getMetaClass().getName())) {
+            return this;
+        }
+        
+        IRubyObject value = convertToType(targetType, convertMethod, false);
+        if (value.isNil()) {
+            return value;
+        }
+        
+        if (!targetType.equals(value.getMetaClass().getName())) {
+            throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
+                    "should return " + targetType);
+        }
+        
+        return value;
+    }
+    
+    /*
+     * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
+     */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         // No need to convert something already of the correct type.
         // XXXEnebo - Could this pass actual class reference instead of String?
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 throw getRuntime().newTypeError(
                     "cannot convert " + getMetaClass().getName() + " into " + targetType);
                 // FIXME nil, true and false instead of NilClass, TrueClass, FalseClass;
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(convertMethod);
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType("Array", "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType("Float", "to_f", true);
     }
     
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType("Integer", "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType("String", "to_str", true);
     }
 
     /** rb_convert_type
      *
      */
     public IRubyObject convertType(Class type, String targetType, String convertMethod) {
         if (type.isAssignableFrom(getClass())) {
             return this;
         }
 
         IRubyObject result = convertToType(targetType, convertMethod, true);
 
         if (!type.isAssignableFrom(result.getClass())) {
             throw getRuntime().newTypeError(
                 getMetaClass().getName() + "#" + convertMethod + " should return " + targetType + ".");
         }
 
         return result;
     }
 
     public void checkSafeString() {
         if (getRuntime().getSafeLevel() > 0 && isTaint()) {
             ThreadContext tc = getRuntime().getCurrentContext();
             if (tc.getFrameLastFunc() != null) {
                 throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameLastFunc());
             }
             throw getRuntime().newSecurityError("Insecure operation: -r");
         }
         getRuntime().secure(4);
         if (!(this instanceof RubyString)) {
             throw getRuntime().newTypeError(
                 "wrong argument type " + getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
         
         if (tc.isBlockGiven()) {
             if (args.length > 0) {
                 throw getRuntime().newArgumentError(args.length, 0);
             }
             return yieldUnder(mod);
         }
 		if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameLastFunc();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
                 
                 return args[0].evalSimple(source,
                                   ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line });
     }
 
     private IRubyObject yieldUnder(RubyModule under) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Block block = (Block) context.getCurrentBlock();
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return context.yieldCurrentBlock(valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
                     
                 		return breakValue == null ? getRuntime().getNil() : breakValue;
                 	} else {
                 		throw je;
                 	}
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this });
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
         
         ThreadContext threadContext = getRuntime().getCurrentContext();
         
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject oldSelf = null;
         EvaluationState state = null;
         IRubyObject result = getRuntime().getNil();
         
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
         
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding((RubyBinding)scope);            
             newSelf = threadContext.getFrameSelf();
 
             state = threadContext.getFrameEvalState();
             oldSelf = state.getSelf();
             state.setSelf(newSelf);
             
             result = state.begin(getRuntime().parse(src.toString(), file));
         } finally {
             // return the eval state to its original self
             state.setSelf(oldSelf);
             threadContext.postBoundEvalOrYield();
             
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
         
         ThreadContext threadContext = getRuntime().getCurrentContext();
         
         ISourcePosition savedPosition = threadContext.getPosition();
         // no binding, just eval in "current" frame (caller's frame)
         Iter iter = threadContext.getFrameIter();
         EvaluationState state = threadContext.getFrameEvalState();
         IRubyObject oldSelf = state.getSelf();
         IRubyObject result = getRuntime().getNil();
         
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
             
             state.setSelf(this);
             
             result = state.begin(getRuntime().parse(src.toString(), file));
         } finally {
             // return the eval state to its original self
             state.setSelf(oldSelf);
             
             // FIXME: this is broken for Proc, see above
             threadContext.setFrameIter(iter);
             
             // restore position
             threadContext.setPosition(savedPosition);
         }
         
         return result;
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject equal(IRubyObject obj) {
         if (isNil()) {
             return getRuntime().newBoolean(obj.isNil());
         }
         return getRuntime().newBoolean(this == obj);
     }
     
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 	
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this != original) {
 	        checkFrozen();
 	        if (!getClass().equals(original.getClass())) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	        }
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <b>Warning:</b> In JRuby there is no guarantee that two objects have different ids.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public RubyFixnum id() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
     
     public final int hashCode() {
     	return RubyNumeric.fix2int(callMethod("hash"));
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *
      */
     public IRubyObject rbClone() {
         IRubyObject clone = doClone();
         clone.setMetaClass(getMetaClass().getSingletonClassClone());
         clone.setTaint(this.isTaint());
         clone.initCopy(this);
         clone.setFrozen(isFrozen());
         return clone;
     }
     
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
     	return getMetaClass().getRealClass().allocate();
     }
     
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
         
         port.callMethod("write", this);
 
         return getRuntime().getNil();
     }
     
     /** rb_obj_dup
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod("clone");
         if (!dup.getClass().equals(getClass())) {
             throw getRuntime().newTypeError("duplicated object must be same type");
         }
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         // TODO Review this and either remove the comment, or do it
         //     if (TYPE(obj) == T_OBJECT
         // 	&& ROBJECT(obj)->iv_tbl
         // 	&& ROBJECT(obj)->iv_tbl->num_entries > 0) {
         // 	VALUE str;
         // 	char *c;
         //
         // 	c = rb_class2name(CLASS_OF(obj));
         // 	/*if (rb_inspecting_p(obj)) {
         // 	    str = rb_str_new(0, strlen(c)+10+16+1); /* 10:tags 16:addr 1:eos */
         // 	    sprintf(RSTRING(str)->ptr, "#<%s:0x%lx ...>", c, obj);
         // 	    RSTRING(str)->len = strlen(RSTRING(str)->ptr);
         // 	    return str;
         // 	}*/
         // 	str = rb_str_new(0, strlen(c)+6+16+1); /* 6:tags 16:addr 1:eos */
         // 	sprintf(RSTRING(str)->ptr, "-<%s:0x%lx ", c, obj);
         // 	RSTRING(str)->len = strlen(RSTRING(str)->ptr);
         // 	return rb_protect_inspect(inspect_obj, obj, str);
         //     }
         //     return rb_funcall(obj, rb_intern("to_s"), 0, 0);
         // }
         return callMethod("to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         Iterator iter = instanceVariableNames();
         while (iter.hasNext()) {
             String name = (String) iter.next();
             names.add(getRuntime().newString(name));
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	checkArgumentCount(args, 0, 1);
     	
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 	
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods() {
         RubyArray result = getRuntime().newArray();
         
         for (RubyClass type = getMetaClass(); type != null && type instanceof MetaClass; 
              type = type.getSuperClass()) { 
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 ICallable method = (ICallable) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type) {
                 	continue;
                 }
                 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     protected IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
     
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args) {
         return specificEval(getSingletonClass(), args);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         checkArgumentCount(args, 1, -1);
         
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
         
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod("extend_object", this);
             args[i].callMethod("extended", this);
         }
         return this;
     }
     
     public IRubyObject inherited(IRubyObject arg) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = callMethod("inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
             noClass ? "" : ":", noClass ? "" : getType().getName()});
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg);
         }
         throw getRuntime().newNoMethodError(msg);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
         
         ThreadContext tc = getRuntime().getCurrentContext();
         
         tc.setIfBlockAvailable();
         try {
             return callMethod(name, newArgs, CallType.FUNCTIONAL);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined");
    }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('o');
         RubySymbol classname = RubySymbol.newSymbol(getRuntime(), getMetaClass().getName());
         output.dumpObject(classname);
 
         output.dumpInt(getInstanceVariables().size());
         
         for (Iterator iter = instanceVariableNames(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = getInstanceVariable(name);
 
             // Between getting name and retrieving value the instance variable could have been
             // removed
             if (value != null) {
             	output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
             	output.dumpObject(value);
             }
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index c4d5dafbd3..708fc941bf 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,303 +1,313 @@
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
 package org.jruby.runtime.builtin;
 
 import java.io.IOException;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.MetaClass;
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
 
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
     Map getInstanceVariables();
 
     IRubyObject callMethod(RubyModule context, String name, IRubyObject[] args, CallType callType);
     
     IRubyObject callMethod(String name, IRubyObject[] args, CallType callType);
     
     /**
      * RubyMethod funcall.
      * @param string
      * @return RubyObject
      */
     IRubyObject callMethod(String string);
 
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
 
     boolean isTrue();
 
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
 
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
 
     /**
      * RubyMethod funcall.
      * @param string
      * @param arg
      * @return RubyObject
      */
     IRubyObject callMethod(String string, IRubyObject arg);
 
     /**
      * RubyMethod getRubyClass.
      */
     RubyClass getMetaClass();
 
     void setMetaClass(RubyClass metaClass);
 
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     MetaClass getSingletonClass();
 
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
 
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
 
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
 
     /**
      * RubyMethod getRuntime.
      */
     IRuby getRuntime();
 
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
 
     /**
      * RubyMethod callMethod.
      * @param method
      * @param rubyArgs
      * @return IRubyObject
      */
     IRubyObject callMethod(String method, IRubyObject[] rubyArgs);
 
     /**
      * RubyMethod eval.
      * @param iNode
      * @return IRubyObject
      */
     IRubyObject eval(Node iNode);
 
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(IRubyObject evalString, IRubyObject binding, String file);
 
     /**
      * Evaluate the given string.
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(IRubyObject evalString, String file);
 
     /**
      * RubyMethod extendObject.
      * @param rubyModule
      */
     void extendObject(RubyModule rubyModule);
 
     /**
      * Convert the object into a symbol name if possible.
      * 
      * @return String the symbol name
      */
     String asSymbol();
 
     /**
      * Methods which perform to_xxx if the object has such a method
      */
     RubyArray convertToArray();
     RubyFloat convertToFloat();
     RubyInteger convertToInteger();
     RubyString convertToString();
 
     /**
-     * Converts this object to type 'targetType' using 'convertMethod' method.
+     * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
+     * @return the converted value
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
 
+    /**
+     * Higher level conversion utility similiar to convertToType but it can throw an
+     * additional TypeError during conversion (MRI: rb_check_convert_type).
+     * 
+     * @param targetType is the type we are trying to convert to
+     * @param convertMethod is the method to be called to try and convert to targeType
+     * @return the converted value
+     */
+    IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
 
 
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
 
     /**
      * RubyMethod checkSafeString.
      */
     void checkSafeString();
 
     /**
      * RubyMethod marshalTo.
      * @param marshalStream
      */
     void marshalTo(MarshalStream marshalStream) throws IOException;
 
     /**
      * RubyMethod convertType.
      * @param type
      * @param string
      * @param string1
      */
     IRubyObject convertType(Class type, String string, String string1);
 
     /**
      * RubyMethod dup.
      */
     IRubyObject dup();
 
     /**
      * RubyMethod setupClone.
      * @param original
      */
     void initCopy(IRubyObject original);
 
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
 
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
 
     /**
      * Make sure the arguments fit the range specified by minimum and maximum.  On
      * a failure, The Ruby runtime will generate an ArgumentError.
      * 
      * @param arguments to check
      * @param minimum number of args
      * @param maximum number of args (-1 for any number of args)
      * @return the number of arguments in args
      */
     int checkArgumentCount(IRubyObject[] arguments, int minimum, int maximum);
 
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
 
 
     public void callInit(IRubyObject[] args);
 
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
 
     boolean singletonMethodsAllowed();
 
 	Iterator instanceVariableNames();
 }
diff --git a/test/testKernel.rb b/test/testKernel.rb
index b100818bd6..334bf1c5da 100644
--- a/test/testKernel.rb
+++ b/test/testKernel.rb
@@ -1,19 +1,44 @@
 require 'test/minirunit'
 test_check "kernel"
 
 test_ok(! eval("defined? some_unknown_variable"))
 x = 1
 test_equal(1, eval("x"))
 eval("x = 2")
 test_equal(2, x)
 eval("unknown = 3")
 test_equal(2, x)     # Make sure eval() didn't destroy locals
 test_ok(! defined? unknown)
 test_equal(nil, true && defined?(Bogus))
 
-# JRUBY-116 - Should not see warning
-# Array(:abc)
 # JRUBY-117 - to_a should be public
 test_equal(["to_a"], Object.public_instance_methods.grep(/to_a/))
 # JRUBY-117 - remove_instance_variable should be private
-test_equal(["remove_instance_variable"], Object.private_instance_methods.grep(/remove_instance_variable/))
\ No newline at end of file
+test_equal(["remove_instance_variable"], Object.private_instance_methods.grep(/remove_instance_variable/))
+
+# JRUBY-116 (Array())
+class A1; def to_ary; [1]; end; end
+class A2; def to_a  ; [2]; end; end
+class A3; def to_ary;   3; end; end
+class A4; def to_a  ;   4; end; end
+class A5; def to_ary; [5]; end; def to_a  ; [:no]; end; end
+class A6; def to_ary; :no; end; def to_a  ;   [6]; end; end
+class A7; end
+class A8; def to_ary; nil; end; end
+class A9; def to_a  ; nil; end; end
+
+
+test_equal([], Array(nil))
+# No warning for this first case either
+test_equal([1], Array(1))
+test_equal([1], Array(A1.new))
+test_equal([2], Array(A2.new))
+test_exception(TypeError) { Array(A3.new) }
+test_exception(TypeError) { Array(A4.new) }
+test_equal([5], Array(A5.new))
+test_exception(TypeError) { Array(A6.new) }
+a = A7.new
+test_equal([a], Array(a))
+a = A8.new
+test_equal([a], Array(a))
+test_exception(TypeError) { Array(A9.new) }
