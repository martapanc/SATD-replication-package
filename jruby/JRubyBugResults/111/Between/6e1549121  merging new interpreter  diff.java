diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 2fb68c320b..98c038b90b 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1210 +1,1212 @@
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
+import org.jruby.evaluator.EvaluationState;
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
 
     private RubyClass nilClass;
 
     private FixnumMetaClass fixnumClass;
 
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
-            return getCurrentContext().getFrameEvalState().begin(node);
+            ThreadContext tc = getCurrentContext();
+            return EvaluationState.eval(tc, node, tc.getFrameSelf());
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
     
     public RubyClass getFixnum() {
         return fixnumClass;
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
 
         nilClass = RubyNil.createNilClass(this);
 
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
         fixnumClass = new FixnumMetaClass(this);
         fixnumClass.initializeClass();
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
 
     public RaiseException newNoMethodError(String message) {
     	return newRaiseException(getClass("NoMethodError"), message);
     }
 
     public RaiseException newNameError(String message) {
     	return newRaiseException(getClass("NameError"), message);
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
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index c8dd4864ea..dce8696649 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1203 +1,1181 @@
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
 package org.jruby;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Collections;
 
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
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      * 
      */
     public RubyClass getMetaClass() {
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
         return getMetaClass().hasModuleInHierarchy(type);
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
-        EvaluationState state = getRuntime().getCurrentContext().getFrameEvalState();
-        IRubyObject oldSelf = state.getSelf();
-        state.setSelf(this);
-        try {
-            return state.begin(n);
-        } finally {
-            state.setSelf(oldSelf);
-        }
+        return EvaluationState.eval(getRuntime().getCurrentContext(), n, this);
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
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         IRubyObject value = convertToType(targetType, convertMethod, false);
         if (value.isNil()) {
             return value;
         }
         
         if (!targetType.equals(value.getMetaClass().getName())) {
             throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
                     "should return " + targetType);
         }
         
         return value;
     }
     
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
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
-        IRubyObject oldSelf = null;
-        EvaluationState state = null;
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
 
-            state = threadContext.getFrameEvalState();
-            oldSelf = state.getSelf();
-            state.setSelf(newSelf);
-            
-            result = state.begin(getRuntime().parse(src.toString(), file));
+            result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file), newSelf);
         } finally {
-            // return the eval state to its original self
-            state.setSelf(oldSelf);
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
-        EvaluationState state = threadContext.getFrameEvalState();
-        IRubyObject oldSelf = state.getSelf();
         IRubyObject result = getRuntime().getNil();
         
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
             
-            state.setSelf(this);
-            
-            result = state.begin(getRuntime().parse(src.toString(), file));
+            result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file), this);
         } finally {
-            // return the eval state to its original self
-            state.setSelf(oldSelf);
-            
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
     	return (int) RubyNumeric.fix2int(callMethod("hash"));
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
         if(getInstanceVariables().size() > 0) {
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             part.append(" ");
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append("...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     part.append(sep);
                     part.append(name);
                     part.append("=");
                     part.append(((IRubyObject)(iVars.get(name))).callMethod("inspect"));
                     sep = ", ";
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
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
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             names.add(getRuntime().newString((String)iter.next()));
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
         Map iVars = getInstanceVariablesSnapshot();
         output.dumpInt(iVars.size());
         for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = (IRubyObject)iVars.get(name);
             
             output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
             output.dumpObject(value);
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 }
diff --git a/src/org/jruby/ast/AliasNode.java b/src/org/jruby/ast/AliasNode.java
index cac8648097..fec2ad892e 100644
--- a/src/org/jruby/ast/AliasNode.java
+++ b/src/org/jruby/ast/AliasNode.java
@@ -1,93 +1,93 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** An AliasNode represents an alias statement.
  * ast node for the 
  * <code>alias newName oldName</code>
  * @author  jpetersen
  */
 public class AliasNode extends Node {
     static final long serialVersionUID = -498707070925086399L;
 
     private String oldName;
     private String newName;
 
     public AliasNode(ISourcePosition position, String newName, String oldName) {
-        super(position);
+        super(position, NodeTypes.ALIASNODE);
         this.oldName = oldName.intern();
         this.newName = newName.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         oldName = oldName.intern();
         newName = newName.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitAliasNode(this);
     }
 
     /**
      * Gets the newName.
      * @return the newName as in the alias statement :  <code> alias <b>newName</b> oldName </code>
      */
     public String getNewName() {
         return newName;
     }
 
     /**
      * Gets the oldName.
      * @return the oldName as in the alias statement :  <code> alias newName <b>oldName</b></code>
      */
     public String getOldName() {
         return oldName;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/AndNode.java b/src/org/jruby/ast/AndNode.java
index 2795a18f26..51d83b140f 100644
--- a/src/org/jruby/ast/AndNode.java
+++ b/src/org/jruby/ast/AndNode.java
@@ -1,79 +1,79 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** An AndNode represents a && operator.
  *
  * @author  jpetersen
  */
 public class AndNode extends Node implements BinaryOperatorNode {
     static final long serialVersionUID = 1716928209521564017L;
 
     private final Node firstNode;
     private final Node secondNode;
 
     public AndNode(ISourcePosition position, Node firstNode, Node secondNode) {
-        super(position);
+        super(position, NodeTypes.ANDNODE);
         this.firstNode = firstNode;
         this.secondNode = secondNode;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitAndNode(this);
     }
 
     /**
      * Gets the secondNode.
      * @return Returns a Node
      */
     public Node getSecondNode() {
         return secondNode;
     }
 
     /**
      * Gets the firstNode.
      * @return Returns a Node
      */
     public Node getFirstNode() {
         return firstNode;
     }
     
     public List childNodes() {
         return Node.createList(firstNode, secondNode);
     }
 
 }
diff --git a/src/org/jruby/ast/ArgsCatNode.java b/src/org/jruby/ast/ArgsCatNode.java
index 784089c076..f30f813b03 100644
--- a/src/org/jruby/ast/ArgsCatNode.java
+++ b/src/org/jruby/ast/ArgsCatNode.java
@@ -1,67 +1,67 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 public class ArgsCatNode extends Node {
 	private static final long serialVersionUID = 3906082365066327860L;
 
 	private final Node firstNode;
     private final Node secondNode;
 
     public ArgsCatNode(ISourcePosition position, Node firstNode, Node secondNode) {
-        super(position);
+        super(position, NodeTypes.ARGSCATNODE);
         this.firstNode = firstNode;
         this.secondNode = secondNode;
     }
 
     public Instruction accept(NodeVisitor visitor) {
         return visitor.visitArgsCatNode(this);
     }
     
     public Node getFirstNode() {
         return firstNode;
     }
     
     public Node getSecondNode() {
         return secondNode;
     }
     
     public List childNodes() {
         return Node.createList(firstNode, secondNode);
     }
 
 }
diff --git a/src/org/jruby/ast/ArgsNode.java b/src/org/jruby/ast/ArgsNode.java
index 11cee513ba..4c7a08f185 100644
--- a/src/org/jruby/ast/ArgsNode.java
+++ b/src/org/jruby/ast/ArgsNode.java
@@ -1,148 +1,148 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /**
  * arguments for a function.
  *  this is used both in the function definition
  * and in actual function calls                                     
  * <ul>
  * <li>
  * u1 ==&gt; optNode (BlockNode) Optional argument description
  * </li>
  * <li>
  * u2 ==&gt; rest (int) index of the rest argument (the array arg with a * in front 
  * </li>
  * <li>
  * u3 ==&gt; count (int) number of arguments
  * </li>
  * </ul>
  *
  * @author  jpetersen
  */
 public class ArgsNode extends Node {
     static final long serialVersionUID = 3709437716296564785L;
 
     private final ListNode arguments;
     private final ListNode optArgs;
     private final int restArg;
     private final BlockArgNode blockArgNode;
     private final Arity arity;
 
     /**
      * 
      * @param optionalArguments  Node describing the optional arguments
      * 				This Block will contain assignments to locals (LAsgnNode)
      * @param restArguments  index of the rest argument in the local table
      * 				(the array argument prefixed by a * which collects 
      * 				all additional params)
      * 				or -1 if there is none.
      * @param argsCount number of regular arguments
      * @param blockArgNode An optional block argument (&amp;arg).
      **/
     public ArgsNode(ISourcePosition position, ListNode arguments, ListNode optionalArguments, 
             int restArguments, BlockArgNode blockArgNode) {
-        super(position);
+        super(position, NodeTypes.ARGSNODE);
 
         this.arguments = arguments;
         this.optArgs = optionalArguments;
         this.restArg = restArguments;
         this.blockArgNode = blockArgNode;
         
         if (getRestArg() == -2) {
             arity = Arity.optional();
         } else if (getOptArgs() != null || getRestArg() >= 0) {
             arity = Arity.required(getArgsCount());
         } else {   
             arity = Arity.createArity(getArgsCount());
         }
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitArgsNode(this);
     }
 
     /**
      * Gets main arguments (as Tokens)
      */
     public ListNode getArgs() {
         return arguments;
     }
 
     public Arity getArity() {
         return arity;
     }
     
     public int getArgsCount() {
         return arguments == null ? 0 : arguments.size();
     }
 
     /**
      * Gets the optArgs.
      * @return Returns a ListNode
      */
     public ListNode getOptArgs() {
         return optArgs;
     }
 
     /**
      * Gets the restArg.
      * @return Returns a int
      */
     public int getRestArg() {
         return restArg;
     }
 
     /**
      * Gets the blockArgNode.
      * @return Returns a BlockArgNode
      */
     public BlockArgNode getBlockArgNode() {
         return blockArgNode;
     }
     
     public List childNodes() {
         return Node.createList(arguments, optArgs, blockArgNode);
     }
 
 }
diff --git a/src/org/jruby/ast/ArgumentNode.java b/src/org/jruby/ast/ArgumentNode.java
index 77739cf08e..8c4ec8e38f 100644
--- a/src/org/jruby/ast/ArgumentNode.java
+++ b/src/org/jruby/ast/ArgumentNode.java
@@ -1,68 +1,68 @@
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Simple Node that allows editor projects to keep position info in AST
  * (evaluation does not need this).
  */
 public class ArgumentNode extends Node {
     private static final long serialVersionUID = -6375678995811376530L;
     private String identifier;
     
     public ArgumentNode(ISourcePosition position, String identifier) {
-        super(position);
+        super(position, NodeTypes.ARGUMENTNODE);
         
         this.identifier = identifier.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         identifier = identifier.intern();
     }
 
     public Instruction accept(NodeVisitor visitor) {
         throw new RuntimeException("ArgumentNode should never be evaluated");
     }
     
     public String getName() {
         return identifier;
     }
 
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/ArrayNode.java b/src/org/jruby/ast/ArrayNode.java
index 1c0456c374..1bd408513e 100644
--- a/src/org/jruby/ast/ArrayNode.java
+++ b/src/org/jruby/ast/ArrayNode.java
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Represents an array. This could be an array literal, quoted words or
  * some args stuff.
  *
  * @author  jpetersen
  */
 public class ArrayNode extends ListNode implements ILiteralNode {
     static final long serialVersionUID = 6279246130032958596L;
 
     public ArrayNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.ARRAYNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitArrayNode(this);
     }
 }
diff --git a/src/org/jruby/ast/AssignableNode.java b/src/org/jruby/ast/AssignableNode.java
index 6539c14917..f3d8de5ccd 100644
--- a/src/org/jruby/ast/AssignableNode.java
+++ b/src/org/jruby/ast/AssignableNode.java
@@ -1,70 +1,70 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import org.jruby.ast.types.IArityNode;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /**
  * Base class of any node which can be assigned to.
  */
 public abstract class AssignableNode extends Node implements IArityNode {
 	static final long serialVersionUID= 7997990944631594662L;
 	
     private Node valueNode;
 
-	public AssignableNode(ISourcePosition position) {
-		super(position);
+	public AssignableNode(ISourcePosition position, int id) {
+		super(position, id);
 	}
 
 	/**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
 
     /**
      * Sets the valueNode.
      * @param valueNode The valueNode to set
      */
     public void setValueNode(Node valueNode) {
         this.valueNode = valueNode;
     }
     
     /**
 	 * Almost all assignables are only assigned a single value.
      */
     public Arity getArity() {
     	return Arity.singleArgument();
     }
 }
diff --git a/src/org/jruby/ast/BackRefNode.java b/src/org/jruby/ast/BackRefNode.java
index fd057e546e..61ab118a09 100644
--- a/src/org/jruby/ast/BackRefNode.java
+++ b/src/org/jruby/ast/BackRefNode.java
@@ -1,89 +1,89 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *	Regexp backref.
  *	generated when one of the following special global variables are found
  *
  *
  *    - $&amp; last succesfull match
  *     
  *    - $+ highest numbered group matched in last successful match.
  *     
  *    - $` what precedes the last successful match
  *    
  *    - $' what follows the last successful match
  *
  *	
  * @author  jpetersen
  */
 public class BackRefNode extends Node {
     static final long serialVersionUID = 5321267679438359590L;
 
 	/**
 	 * the character which generated the backreference
 	 **/
     private final char type;
 
     public BackRefNode(ISourcePosition position, char type) {
-        super(position);
+        super(position, NodeTypes.BACKREFNODE);
         this.type = type;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBackRefNode(this);
     }
 
     /**
      * Gets the type.
 	 * the type is the character which generates the backreference
      * @return type
      */
     public char getType() {
         return type;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/BeginNode.java b/src/org/jruby/ast/BeginNode.java
index 47c833bad2..ad8b3f2544 100644
--- a/src/org/jruby/ast/BeginNode.java
+++ b/src/org/jruby/ast/BeginNode.java
@@ -1,74 +1,74 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Begin/End block.
  *  A Begin ... End block without rescue.
  *
  * @author  jpetersen
  */
 public class BeginNode extends Node {
     static final long serialVersionUID = 7295877486186461712L;
 
     private final Node bodyNode;
 
     public BeginNode(ISourcePosition position, Node bodyNode) {
-        super(position);
+        super(position, NodeTypes.BEGINNODE);
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBeginNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a INode
      */
     public Node getBodyNode() {
         return bodyNode;
     }
     
     public List childNodes() {
         return createList(bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/BignumNode.java b/src/org/jruby/ast/BignumNode.java
index 6bb0e70ffe..b8eec2ef6f 100644
--- a/src/org/jruby/ast/BignumNode.java
+++ b/src/org/jruby/ast/BignumNode.java
@@ -1,72 +1,72 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.math.BigInteger;
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a big integer literal.
  *
  * @author  jpetersen
  */
 public class BignumNode extends Node implements ILiteralNode {
     static final long serialVersionUID = -8646636291868912747L;
 
     private final BigInteger value;
 
     public BignumNode(ISourcePosition position, BigInteger value) {
-        super(position);
+        super(position, NodeTypes.BIGNUMNODE);
         this.value = value;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBignumNode(this);
     }
 
     /**
      * Gets the value.
      * @return Returns a BigInteger
      */
     public BigInteger getValue() {
         return value;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/BlockArgNode.java b/src/org/jruby/ast/BlockArgNode.java
index 0da2d1225c..2f7bee25f7 100644
--- a/src/org/jruby/ast/BlockArgNode.java
+++ b/src/org/jruby/ast/BlockArgNode.java
@@ -1,78 +1,78 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *	a block argument.
  *	A block argument, when present in a function declaration is the last argument
  *	and it is preceded by an ampersand:<br>
  *	
  *	<code>def tutu(a, b, &amp;c)</code>
  *	in this example c is a BlockArgNode
  * @author  jpetersen
  */
 public class BlockArgNode extends Node {
     static final long serialVersionUID = 8374824536805365398L;
 
     private final int count;
 
     public BlockArgNode(ISourcePosition position, int count) {
-        super(position);
+        super(position, NodeTypes.BLOCKARGNODE);
         this.count = count;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBlockArgNode(this);
     }
 
     /**
      * Gets the count.
      * @return Returns a int
      */
     public int getCount() {
         return count;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/BlockNode.java b/src/org/jruby/ast/BlockNode.java
index edb4e6eead..56517a5c84 100644
--- a/src/org/jruby/ast/BlockNode.java
+++ b/src/org/jruby/ast/BlockNode.java
@@ -1,62 +1,62 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby.ast;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * A structuring node (linked list of other nodes).
  * This type of node is used to structure the AST.
  * Used in many places it is created throught the
  * {@link org.jruby.parser.ParserSupport#appendToBlock appendToBlock()} method
  * 
  * @author  jpetersen
  */
 public class BlockNode extends ListNode {
     static final long serialVersionUID = 6070308619613804520L;
     
     public BlockNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.BLOCKNODE);
     }
 
     /**
      * RubyMethod used by visitors.
      * accepts the visitor
      * @param iVisitor the visitor to accept
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBlockNode(this);
     }
 }
diff --git a/src/org/jruby/ast/BlockPassNode.java b/src/org/jruby/ast/BlockPassNode.java
index be220e133e..21d5c3a1b9 100644
--- a/src/org/jruby/ast/BlockPassNode.java
+++ b/src/org/jruby/ast/BlockPassNode.java
@@ -1,113 +1,113 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Block passed explicitly as an argument in a method call.
  * A block passing argument in a method call (last argument prefixed by an ampersand).
  * 
  * @author  jpetersen
  */
 public class BlockPassNode extends Node {
     static final long serialVersionUID = 7201862349971094217L;
 
     private final Node bodyNode;
     private Node iterNode;
 
     /** Used by the arg_blk_pass and new_call, new_fcall and new_super
      * methods in ParserSupport to temporary save the args node.
      */
     private Node argsNode;
 
     public BlockPassNode(ISourcePosition position, Node bodyNode) {
-        super(position);
+        super(position, NodeTypes.BLOCKPASSNODE);
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBlockPassNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the iterNode.
      * @return Returns a Node
      */
     public Node getIterNode() {
         return iterNode;
     }
 
     /**
      * Sets the iterNode.
      * @param iterNode The iterNode to set
      */
     public void setIterNode(Node iterNode) {
         this.iterNode = iterNode;
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a IListNode
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Sets the argsNode.
      * @param argsNode The argsNode to set
      */
     public void setArgsNode(Node argsNode) {
         this.argsNode = argsNode;
     }
     
     public List childNodes() {
         return Node.createList(argsNode, iterNode, bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/BreakNode.java b/src/org/jruby/ast/BreakNode.java
index 88b62fe343..b5ca31b5fb 100644
--- a/src/org/jruby/ast/BreakNode.java
+++ b/src/org/jruby/ast/BreakNode.java
@@ -1,77 +1,77 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a 'break' statement.
  *
  * @author  jpetersen
  */
 public class BreakNode extends Node {
     static final long serialVersionUID = 1491046888629861035L;
 
     private final Node valueNode;
     
     public BreakNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.BREAKNODE);
         valueNode = null;
     }
     
     public BreakNode(ISourcePosition position, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.BREAKNODE);
         this.valueNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitBreakNode(this);
     }
     
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
     
     public List childNodes() {
         return createList(valueNode);
     }
 }
diff --git a/src/org/jruby/ast/CallNode.java b/src/org/jruby/ast/CallNode.java
index ecc01a80c2..9cf7bd66b3 100644
--- a/src/org/jruby/ast/CallNode.java
+++ b/src/org/jruby/ast/CallNode.java
@@ -1,105 +1,105 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * A method or operator call.
  * 
  * @author  jpetersen
  */
 public final class CallNode extends Node {
     static final long serialVersionUID = -1993752395320088525L;
 
     private final Node receiverNode;
     private String name;
     private final Node argsNode;
 
     public CallNode(ISourcePosition position, Node receiverNode, String name, Node argsNode) {
-        super(position);
+        super(position, NodeTypes.CALLNODE);
         this.receiverNode = receiverNode;
         this.name = name.intern();
         this.argsNode = argsNode;
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitCallNode(this);
     }
 
     /**
      * Gets the argsNode.
 	 * argsNode representing the method's arguments' value for this call.
      * @return argsNode
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the name.
 	 * name is the name of the method called
      * @return name
      */
     public String getName() {
         return name;
     }
 
     /**
      * Gets the receiverNode.
 	 * receiverNode is the object on which the method is being called
      * @return receiverNode
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, argsNode);
     }
 }
diff --git a/src/org/jruby/ast/CaseNode.java b/src/org/jruby/ast/CaseNode.java
index 5feb56243d..053881334f 100644
--- a/src/org/jruby/ast/CaseNode.java
+++ b/src/org/jruby/ast/CaseNode.java
@@ -1,95 +1,95 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * A Case statement.
  * 
  * Represents a complete case statement, including the body with its
  * when statements.
  * 
  * @author  jpetersen
  */
 public class CaseNode extends Node {
     static final long serialVersionUID = -2824917272720800901L;
 
 	/**
 	 * the case expression.
 	 **/
     private final Node caseNode;
 	/**
 	 * the body of the case.
 	 */
     private final Node caseBody;
     
     public CaseNode(ISourcePosition position, Node caseNode, Node caseBody) {
-        super(position);
+        super(position, NodeTypes.CASENODE);
         this.caseNode = caseNode;
         this.caseBody = caseBody;
     }
 
  	/**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitCaseNode(this);
     }
 
     /**
      * Gets the caseNode.
 	 * caseNode is the case expression 
      * @return caseNode
      */
     public Node getCaseNode() {
         return caseNode;
     }
 
     /**
      * Gets the first whenNode.
 	 * the body of the case statement, the first of a list of WhenNodes
      * @return whenNode
      */
     public Node getFirstWhenNode() {
         return caseBody;
     }
     
     public List childNodes() {
         return Node.createList(caseNode, caseBody);
     }
 
 }
diff --git a/src/org/jruby/ast/ClassNode.java b/src/org/jruby/ast/ClassNode.java
index 8db7e8b91b..3237c823e0 100644
--- a/src/org/jruby/ast/ClassNode.java
+++ b/src/org/jruby/ast/ClassNode.java
@@ -1,101 +1,101 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * A class statement.
  * A class statement is defined by its name, its supertype and its body.
  * The body is a separate naming scope.
  * This node is for a regular class definition, Singleton classes get their own
  * node, the SClassNode
  * 
  * @author  jpetersen
  */
 public class ClassNode extends Node implements IScopingNode {
     static final long serialVersionUID = -1369424045737867587L;
 
     private final Node cpath;
     private final ScopeNode bodyNode;
     private final Node superNode;
     
     public ClassNode(ISourcePosition position, Node cpath, ScopeNode bodyNode, Node superNode) {
-        super(position);
+        super(position, NodeTypes.CLASSNODE);
         this.cpath = cpath;
         this.bodyNode = bodyNode;
         this.superNode = superNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitClassNode(this);
     }
     /**
      * Gets the bodyNode.
      * @return Returns a ScopeNode
      */
     public ScopeNode getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the className.
      * @return Returns representation of class path+name
      */
     public Node getCPath() {
         return cpath;
     }
 
     /**
      * Gets the superNode.
      * @return Returns a Node
      */
     public Node getSuperNode() {
         return superNode;
     }
 
     public List childNodes() {
         return Node.createList(cpath, bodyNode, superNode);
     }
     
     public String toString() {
         return "ClassNode [" + cpath + "]";
     }
 
 }
diff --git a/src/org/jruby/ast/ClassVarAsgnNode.java b/src/org/jruby/ast/ClassVarAsgnNode.java
index c9334834ff..e7772f50c2 100644
--- a/src/org/jruby/ast/ClassVarAsgnNode.java
+++ b/src/org/jruby/ast/ClassVarAsgnNode.java
@@ -1,89 +1,89 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Class variable assignment node.
  * 
  * @author  jpetersen
  */
 public class ClassVarAsgnNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = -2960487069128667341L;
 
     private String name;
 
     /**
      * @param name id of the class variable to assign to
      * @param valueNode  Node used to compute the new value when the assignment is evaled
      */
     public ClassVarAsgnNode(ISourcePosition position, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.CLASSVARASGNNODE);
         this.name = name.intern();
         
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitClassVarAsgnNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/ClassVarDeclNode.java b/src/org/jruby/ast/ClassVarDeclNode.java
index cb5d909d5d..1423c13d1f 100644
--- a/src/org/jruby/ast/ClassVarDeclNode.java
+++ b/src/org/jruby/ast/ClassVarDeclNode.java
@@ -1,87 +1,87 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Class variable declaration.
  * 
  * @author  jpetersen
  */
 public class ClassVarDeclNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = -6227934966029974915L;
 
     private String name;
 
     public ClassVarDeclNode(ISourcePosition position, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.CLASSVARDECLNODE);
 
         this.name = name.intern();
         
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitClassVarDeclNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/ClassVarNode.java b/src/org/jruby/ast/ClassVarNode.java
index b2ad5486e7..eaab0a8af2 100644
--- a/src/org/jruby/ast/ClassVarNode.java
+++ b/src/org/jruby/ast/ClassVarNode.java
@@ -1,82 +1,82 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Access to a class variable.
  * 
  * @author  jpetersen
  */
 public class ClassVarNode extends Node {
     static final long serialVersionUID = -228883683599457381L;
 
     private String name;
 
     public ClassVarNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.CLASSVARNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitClassVarNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/Colon2Node.java b/src/org/jruby/ast/Colon2Node.java
index 9d5dc1debe..426e7f08f0 100644
--- a/src/org/jruby/ast/Colon2Node.java
+++ b/src/org/jruby/ast/Colon2Node.java
@@ -1,99 +1,99 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a '::' constant access or method call.
  *
  * @author  jpetersen
  */
 public class Colon2Node extends Node implements INameNode {
     static final long serialVersionUID = -3250593470034657352L;
 
     private final Node leftNode;
     private String name;
 
     public Colon2Node(ISourcePosition position, Node leftNode, String name) {
-        super(position);
+        super(position, NodeTypes.COLON2NODE);
         this.leftNode = leftNode;
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitColon2Node(this);
     }
 
     /**
      * Gets the leftNode.
      * @return Returns a Node
      */
     public Node getLeftNode() {
         return leftNode;
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return Node.createList(leftNode);
     }
     
     public String toString() {
         String result = "Colon2Node [";
         if (leftNode != null)
             result += leftNode;
         result += name;
         return result+"]";
     }
 }
diff --git a/src/org/jruby/ast/Colon3Node.java b/src/org/jruby/ast/Colon3Node.java
index 7b5603955a..6ec38b666e 100644
--- a/src/org/jruby/ast/Colon3Node.java
+++ b/src/org/jruby/ast/Colon3Node.java
@@ -1,87 +1,87 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Global scope node.
  * Node produced when using :: without a scope in front.
  * This is used to gain access to the global scope (that of the Object class)
  * when refering to a constant or method.  This is the same as a Colon2Node but with 
  * no leftNode which implicitly uses the Object class as a left node.
  * 
  * @author  jpetersen
  */
 public class Colon3Node extends Node implements INameNode {
     static final long serialVersionUID = 8860717109371016871L;
 
     private String name;
 
     public Colon3Node(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.COLON3NODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitColon3Node(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/ConstDeclNode.java b/src/org/jruby/ast/ConstDeclNode.java
index cb5f1b8e49..b0554b6db2 100644
--- a/src/org/jruby/ast/ConstDeclNode.java
+++ b/src/org/jruby/ast/ConstDeclNode.java
@@ -1,87 +1,87 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Declaration (and assignment) of a Constant.
  * 
  * @author  jpetersen
  */
 public class ConstDeclNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = -6260931203887158208L;
 
     private final String name;
     private final Node pathNode;
 
     public ConstDeclNode(ISourcePosition position, Node pathNode, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.CONSTDECLNODE);
         this.name = name.intern();
         this.pathNode = pathNode;
         setValueNode(valueNode);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitConstDeclNode(this);
     }
 
     /**
      * Gets the name (this is the rightmost element of lhs (in Foo::BAR it is BAR).
 	 * name is the constant Name, it normally starts with a Capital
      * @return name
      */
     public String getName() {
     	return name;
     }
     
     /**
      * Get the path the name is associated with or null (in Foo::BAR it is Foo).
      * @return pathNode
      */
     public Node getPathNode() {
         return pathNode;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/ConstNode.java b/src/org/jruby/ast/ConstNode.java
index 659506f0ef..6419ff70e8 100644
--- a/src/org/jruby/ast/ConstNode.java
+++ b/src/org/jruby/ast/ConstNode.java
@@ -1,87 +1,87 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * The access to a Constant.
  * 
  * @author  jpetersen
  */
 public class ConstNode extends Node implements INameNode {
     static final long serialVersionUID = -5190161028130457944L;
 
     private String name;
     
     public ConstNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.CONSTNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitConstNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
     
     public String toString() {
         return "ConstNode [" + name + "]";
     }
 
 }
diff --git a/src/org/jruby/ast/DAsgnNode.java b/src/org/jruby/ast/DAsgnNode.java
index cee46060b2..49aa7068dc 100644
--- a/src/org/jruby/ast/DAsgnNode.java
+++ b/src/org/jruby/ast/DAsgnNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * assignment to dynamic variable
  * @author  jpetersen
  */
 public class DAsgnNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = 2396008643154044043L;
 
     private String name;
 
     public DAsgnNode(ISourcePosition position, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.DASGNNODE);
         this.name = name.intern();
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDAsgnNode(this);
     }
     
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/DRegexpNode.java b/src/org/jruby/ast/DRegexpNode.java
index 1df726c4b4..c166a55c73 100644
--- a/src/org/jruby/ast/DRegexpNode.java
+++ b/src/org/jruby/ast/DRegexpNode.java
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *	Dynamic regexp node.
  *	a regexp is dynamic if it contains some expressions which will need to be evaluated
  *	everytime the regexp is used for a match
  * @author  jpetersen
  */
 public class DRegexpNode extends ListNode implements ILiteralNode {
     static final long serialVersionUID = 7307853378003210140L;
 
     private final int options;
     private final boolean once;
     
     public DRegexpNode(ISourcePosition position) {
         this(position, 0, false);
     }
 
     public DRegexpNode(ISourcePosition position, int options, boolean once) {
-        super(position);
+        super(position, NodeTypes.DREGEXPNODE);
 
         this.options = options;
         this.once = once;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDRegxNode(this);
     }
 
     /**
      * Gets the once.
      * @return Returns a boolean
      */
     public boolean getOnce() {
         return once;
     }
 
     /**
      * Gets the options.
      * @return Returns a int
      */
     public int getOptions() {
         return options;
     }
 }
diff --git a/src/org/jruby/ast/DStrNode.java b/src/org/jruby/ast/DStrNode.java
index 06da2e2ac6..c27cb878c9 100644
--- a/src/org/jruby/ast/DStrNode.java
+++ b/src/org/jruby/ast/DStrNode.java
@@ -1,58 +1,58 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * a Dynamic String node.
  * A string which contains some dynamic elements which needs to be evaluated (introduced by #).
  * @author  jpetersen
  */
 public class DStrNode extends ListNode implements ILiteralNode {
     static final long serialVersionUID = -1488812415812799395L;
 
     public DStrNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.DSTRNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDStrNode(this);
     }
 }
diff --git a/src/org/jruby/ast/DSymbolNode.java b/src/org/jruby/ast/DSymbolNode.java
index 0c7b6d29a0..9dc91b3787 100644
--- a/src/org/jruby/ast/DSymbolNode.java
+++ b/src/org/jruby/ast/DSymbolNode.java
@@ -1,64 +1,64 @@
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
  package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Node representing symbol in a form like ':"3jane"'.
  * 
  * @author enebo
  */
 public class DSymbolNode extends Node {
 	private static final long serialVersionUID = 3763093063878326071L;
 
 	private final DStrNode node;
 
 	public DSymbolNode(ISourcePosition position, DStrNode node) {
-		super(position);
+		super(position, NodeTypes.DSYMBOLNODE);
 		this.node = node;
 	}
 
 	public Instruction accept(NodeVisitor visitor) {
 		return visitor.visitDSymbolNode(this);
 	}
 	
 	public DStrNode getNode() {
 		return node;
 	}
     
     public List childNodes() {
         return createList(node);
     }
 
 }
diff --git a/src/org/jruby/ast/DVarNode.java b/src/org/jruby/ast/DVarNode.java
index a813c1d256..38b64c54cf 100644
--- a/src/org/jruby/ast/DVarNode.java
+++ b/src/org/jruby/ast/DVarNode.java
@@ -1,82 +1,82 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Access to a Dynamic variable.
  * Dynamic variable are those defined in a block.
  * @author  jpetersen
  */
 public class DVarNode extends Node {
     static final long serialVersionUID = -8479281167248673970L;
 
     private String name;
 
     public DVarNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.DVARNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDVarNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/DXStrNode.java b/src/org/jruby/ast/DXStrNode.java
index b904f056b8..7e4e3f61ff 100644
--- a/src/org/jruby/ast/DXStrNode.java
+++ b/src/org/jruby/ast/DXStrNode.java
@@ -1,58 +1,58 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Dynamic backquote string.
  * backquote strings are eXecuted using the shell, hence the X 
  * or maybe the X is due to the %x general quote syntax
  * @author  jpetersen
  */
 public class DXStrNode extends ListNode implements ILiteralNode {
     static final long serialVersionUID = 7165988969190553667L;
 
     public DXStrNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.DXSTRNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDXStrNode(this);
     }
 }
diff --git a/src/org/jruby/ast/DefinedNode.java b/src/org/jruby/ast/DefinedNode.java
index cff154e176..26b3d2022d 100644
--- a/src/org/jruby/ast/DefinedNode.java
+++ b/src/org/jruby/ast/DefinedNode.java
@@ -1,74 +1,74 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * a defined statement.
  * @author  jpetersen
  */
 public class DefinedNode extends Node {
     static final long serialVersionUID = -6942286690645861964L;
 
     private final Node expressionNode;
 
     public DefinedNode(ISourcePosition position, Node expressionNode) {
-        super(position);
+        super(position, NodeTypes.DEFINEDNODE);
         this.expressionNode = expressionNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDefinedNode(this);
     }
 
     /**
      * Gets the expressionNode.
      * @return Returns a Node
      */
     public Node getExpressionNode() {
         return expressionNode;
     }
     
     public List childNodes() {
         return createList(expressionNode);
     }
 
 }
diff --git a/src/org/jruby/ast/DefnNode.java b/src/org/jruby/ast/DefnNode.java
index bcd1da69c9..c4c8440107 100644
--- a/src/org/jruby/ast/DefnNode.java
+++ b/src/org/jruby/ast/DefnNode.java
@@ -1,106 +1,106 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Visibility;
 
 /**
  * method definition node.
  * 
  * @author  jpetersen
  */
 public class DefnNode extends Node {
     static final long serialVersionUID = -7634791007500033454L;
 
     private final ArgumentNode nameNode;
     private final Node argsNode;
     private final ScopeNode bodyNode;
     private final Visibility visibility;
     
     public DefnNode(ISourcePosition position, ArgumentNode nameNode, Node argsNode, ScopeNode bodyNode, Visibility visibility) {
-        super(position);
+        super(position, NodeTypes.DEFNNODE);
         
         this.nameNode = nameNode;
         this.argsNode = argsNode;
         this.bodyNode = bodyNode;
         this.visibility = visibility;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDefnNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a ScopeNode
      */
     public ScopeNode getBodyNode() {
         return bodyNode;
     }
 
     public ArgumentNode getNameNode() {
         return nameNode;
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return nameNode.getName();
     }
 
     /**
      * Gets the noex.
      * @return Returns a int
      */
     public Visibility getVisibility() {
         return visibility;
     }
     
     public List childNodes() {
         return Node.createList(nameNode, argsNode, bodyNode);
     }
 }
diff --git a/src/org/jruby/ast/DefsNode.java b/src/org/jruby/ast/DefsNode.java
index fd9bc9ac6b..71bfe2d3b7 100644
--- a/src/org/jruby/ast/DefsNode.java
+++ b/src/org/jruby/ast/DefsNode.java
@@ -1,111 +1,111 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a singleton method definition.
  *
  * @author  jpetersen
  */
 public class DefsNode extends Node {
     static final long serialVersionUID = -4472719020304670080L;
 
     private final Node receiverNode;
     private String name;
     private final Node argsNode;
     private final ScopeNode bodyNode;
 
     public DefsNode(ISourcePosition position, Node receiverNode, String name, Node argsNode, ScopeNode bodyNode) {
-        super(position);
+        super(position, NodeTypes.DEFSNODE);
         this.receiverNode = receiverNode;
         this.name = name.intern();
         this.argsNode = argsNode;
         this.bodyNode = bodyNode;
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDefsNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a ScopeNode
      */
     public ScopeNode getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, argsNode, bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/DotNode.java b/src/org/jruby/ast/DotNode.java
index e194740cc9..cf23f63d33 100644
--- a/src/org/jruby/ast/DotNode.java
+++ b/src/org/jruby/ast/DotNode.java
@@ -1,94 +1,94 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a range literal.
  *
  * @author  jpetersen
  */
 public class DotNode extends Node {
     static final long serialVersionUID = 2763797850980107429L;
 
     private final Node beginNode;
     private final Node endNode;
     private final boolean exclusive;
 
     public DotNode(ISourcePosition position, Node beginNode, Node endNode, boolean exclusive) {
-        super(position);
+        super(position, NodeTypes.DOTNODE);
         this.beginNode = beginNode;
         this.endNode = endNode;
         this.exclusive = exclusive;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitDotNode(this);
     }
 
     /**
      * Gets the beginNode.
      * @return Returns a Node
      */
     public Node getBeginNode() {
         return beginNode;
     }
 
     /**
      * Gets the endNode.
      * @return Returns a Node
      */
     public Node getEndNode() {
         return endNode;
     }
 
     /**
      * Gets the exclusive.
      * @return Returns a boolean
      */
     public boolean isExclusive() {
         return exclusive;
     }
     
     public List childNodes() {
         return Node.createList(beginNode, endNode);
     }
 
 }
diff --git a/src/org/jruby/ast/EnsureNode.java b/src/org/jruby/ast/EnsureNode.java
index 391d9273c0..b1d75e4ab0 100644
--- a/src/org/jruby/ast/EnsureNode.java
+++ b/src/org/jruby/ast/EnsureNode.java
@@ -1,82 +1,82 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *	an ensure statement.
  * @author  jpetersen
  */
 public class EnsureNode extends Node {
     static final long serialVersionUID = -409805241533215981L;
 
     private final Node bodyNode;
     private final Node ensureNode;
 
     public EnsureNode(ISourcePosition position, Node bodyNode, Node ensureNode) {
-        super(position);
+        super(position, NodeTypes.ENSURENODE);
         this.bodyNode = bodyNode;
         this.ensureNode = ensureNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitEnsureNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the ensureNode.
      * @return Returns a Node
      */
     public Node getEnsureNode() {
         return ensureNode;
     }
     
     public List childNodes() {
         return Node.createList(bodyNode, ensureNode);
     }
 }
diff --git a/src/org/jruby/ast/EvStrNode.java b/src/org/jruby/ast/EvStrNode.java
index 1bfc478b2a..ca9f812dfc 100644
--- a/src/org/jruby/ast/EvStrNode.java
+++ b/src/org/jruby/ast/EvStrNode.java
@@ -1,78 +1,78 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an #{} expression in a string. This Node is always a subnode
  * of a DStrNode, DXStrNode or a DRegexpNode.
  * 
  * Before this Node is evaluated it contains the code as a String (value). After
  * the first evaluation this String is parsed into the evaluatedNode Node.
  *
  * @author  jpetersen
  */
 public class EvStrNode extends Node {
     static final long serialVersionUID = 1681935012117120817L;
 
     private final Node body;
 
     public EvStrNode(ISourcePosition position, Node body) {
-        super(position);
+        super(position, NodeTypes.EVSTRNODE);
         this.body = body;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitEvStrNode(this);
     }
 
     /**
      * Gets the evaluatedNode.
      * @return Returns a Node
      */
     public Node getBody() {
         return body;
     }
     
     public List childNodes() {
         return createList(body);
     }
 
 }
diff --git a/src/org/jruby/ast/FCallNode.java b/src/org/jruby/ast/FCallNode.java
index 16383ce13e..2a69221c06 100644
--- a/src/org/jruby/ast/FCallNode.java
+++ b/src/org/jruby/ast/FCallNode.java
@@ -1,92 +1,92 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a method call with self as receiver.
  *
  * @author  jpetersen
  */
 public class FCallNode extends Node {
     static final long serialVersionUID = 3590332973770104094L;
 
     private String name;
     private final Node argsNode;
 
     public FCallNode(ISourcePosition position, String name, Node argsNode) {
-        super(position);
+        super(position, NodeTypes.FCALLNODE);
         this.name = name.intern();
         this.argsNode = argsNode;
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitFCallNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(argsNode);
     }
 
 }
diff --git a/src/org/jruby/ast/FalseNode.java b/src/org/jruby/ast/FalseNode.java
index 30582cbda3..ace2fd891b 100644
--- a/src/org/jruby/ast/FalseNode.java
+++ b/src/org/jruby/ast/FalseNode.java
@@ -1,70 +1,70 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a false literal.
  *
  * @author  jpetersen
  */
 public class FalseNode extends Node implements INameNode {
     static final long serialVersionUID = 8153681841075601779L;
 
 
     public FalseNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.FALSENODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitFalseNode(this);
     }
     
     /**
      * Name of false node.
      */
     public String getName() {
         return "false";
     }
 
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/FixnumNode.java b/src/org/jruby/ast/FixnumNode.java
index fbee9f38cd..1773810dcc 100644
--- a/src/org/jruby/ast/FixnumNode.java
+++ b/src/org/jruby/ast/FixnumNode.java
@@ -1,70 +1,70 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an integer literal.
  *
  * @author  jpetersen
  */
 public class FixnumNode extends Node implements ILiteralNode {
     static final long serialVersionUID = 2236565825959274729L;
 
     private final long value;
 
     public FixnumNode(ISourcePosition position, long value) {
-        super(position);
+        super(position, NodeTypes.FIXNUMNODE);
         this.value = value;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitFixnumNode(this);
     }
 
     /**
      * Gets the value.
      * @return Returns a long
      */
     public long getValue() {
         return value;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/FlipNode.java b/src/org/jruby/ast/FlipNode.java
index 1b462da796..efdbb77693 100644
--- a/src/org/jruby/ast/FlipNode.java
+++ b/src/org/jruby/ast/FlipNode.java
@@ -1,109 +1,109 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * a Range in a boolean expression.
  * named after a FlipFlop component in electronic I believe.
  * 
  * @author  jpetersen
  */
 public class FlipNode extends Node {
     static final long serialVersionUID = -4735579451657299802L;
 
     private final Node beginNode;
     private final Node endNode;
     private final boolean exclusive;
     private final int count;
     
     public FlipNode(ISourcePosition position, Node beginNode, Node endNode, boolean exclusive, int count) {
-        super(position);
+        super(position, NodeTypes.FLIPNODE);
         this.beginNode = beginNode;
         this.endNode = endNode;
         this.exclusive = exclusive;
         this.count = count;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitFlipNode(this);
     }
 
     /**
      * Gets the beginNode.
 	 * beginNode will set the FlipFlop the first time it is true
      * @return Returns a Node
      */
     public Node getBeginNode() {
         return beginNode;
     }
 
     /**
      * Gets the endNode.
 	 * endNode will reset the FlipFlop when it is true while the FlipFlop is set.
      * @return Returns a Node
      */
     public Node getEndNode() {
         return endNode;
     }
 
     /**
      * Gets the exclusive.
 	 * if the range is a 2 dot range it is false if it is a three dot it is true
      * @return Returns a boolean
      */
     public boolean isExclusive() {
         return exclusive;
     }
 
     /**
      * Gets the count.
      * @return Returns a int
      */
     public int getCount() {
         return count;
     }
     
     public List childNodes() {
         return Node.createList(beginNode, endNode);
     }
 
 }
diff --git a/src/org/jruby/ast/FloatNode.java b/src/org/jruby/ast/FloatNode.java
index b2bf201479..613daaa8fc 100644
--- a/src/org/jruby/ast/FloatNode.java
+++ b/src/org/jruby/ast/FloatNode.java
@@ -1,70 +1,70 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a float literal.
  *
  * @author  jpetersen
  */
 public class FloatNode extends Node implements ILiteralNode {
     static final long serialVersionUID = -6358513813684285950L;
 
     private final double value;
     
     public FloatNode(ISourcePosition position, double value) {
-        super(position);
+        super(position, NodeTypes.FLOATNODE);
         this.value = value;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitFloatNode(this);
     }
 
     /**
      * Gets the value.
      * @return Returns a double
      */
     public double getValue() {
         return value;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/ForNode.java b/src/org/jruby/ast/ForNode.java
index 6a3e655f0a..3513c26fcd 100644
--- a/src/org/jruby/ast/ForNode.java
+++ b/src/org/jruby/ast/ForNode.java
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * a For statement.
  * this is almost equivalent to an iternode (the difference being the visibility of the
  * local variables defined in the iterator).
  * 
  * @see IterNode
  * @author  jpetersen
  */
 public class ForNode extends IterNode {
     static final long serialVersionUID = -8319863477790150586L;
 
     public ForNode(ISourcePosition position, Node varNode, Node bodyNode, Node iterNode) {
-        super(position, varNode, bodyNode, iterNode);
+        super(position, varNode, bodyNode, iterNode, NodeTypes.FORNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitForNode(this);
     }
 }
diff --git a/src/org/jruby/ast/GlobalAsgnNode.java b/src/org/jruby/ast/GlobalAsgnNode.java
index cc42a7ae15..8763214d77 100644
--- a/src/org/jruby/ast/GlobalAsgnNode.java
+++ b/src/org/jruby/ast/GlobalAsgnNode.java
@@ -1,87 +1,87 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Represents an assignment to a global variable.
  * 
  * @author  jpetersen
  */
 public class GlobalAsgnNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = 2278414591762936906L;
 
     private String name;
 
     public GlobalAsgnNode(ISourcePosition position, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.GLOBALASGNNODE);
 
         this.name = name.intern();
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * RubyMethod used by visitors.
      * accepts the visitor 
      * @param iVisitor the visitor to accept
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitGlobalAsgnNode(this);
     }
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/GlobalVarNode.java b/src/org/jruby/ast/GlobalVarNode.java
index 704c14d159..5520eb8090 100644
--- a/src/org/jruby/ast/GlobalVarNode.java
+++ b/src/org/jruby/ast/GlobalVarNode.java
@@ -1,80 +1,80 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *	access to a global variable.
  * @author  jpetersen
  */
 public class GlobalVarNode extends Node {
     static final long serialVersionUID = -8913633094119740033L;
 
     private String name;
 
     public GlobalVarNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.GLOBALVARNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitGlobalVarNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
 
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/HashNode.java b/src/org/jruby/ast/HashNode.java
index b0f9a17650..f9fa633c8c 100644
--- a/src/org/jruby/ast/HashNode.java
+++ b/src/org/jruby/ast/HashNode.java
@@ -1,75 +1,75 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * a Literal Hash.
  * this can represent either a {a=&amp;b, c=&amp;d} type expression or the list of default 
  * values in a method call.
  * @author  jpetersen
  */
 public class HashNode extends Node {
     static final long serialVersionUID = -7554050553303344025L;
 
     private final ListNode listNode;
     
     public HashNode(ISourcePosition position, ListNode listNode) {
-        super(position);
+        super(position, NodeTypes.HASHNODE);
         this.listNode = listNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitHashNode(this);
     }
 
     /**
      * Gets the listNode.
      * @return Returns a IListNode
      */
     public ListNode getListNode() {
         return listNode;
     }
     
     public List childNodes() {
         return createList(listNode);
     }
 
 }
diff --git a/src/org/jruby/ast/IfNode.java b/src/org/jruby/ast/IfNode.java
index dc6de50428..85159ab17e 100644
--- a/src/org/jruby/ast/IfNode.java
+++ b/src/org/jruby/ast/IfNode.java
@@ -1,94 +1,94 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * an 'if' statement.
  * @author  jpetersen
  */
 public class IfNode extends Node {
     static final long serialVersionUID = -163780144332979551L;
 
     private final Node condition;
     private final Node thenBody;
     private final Node elseBody;
 
     public IfNode(ISourcePosition position, Node condition, Node thenBody, Node elseBody) {
-        super(position);
+        super(position, NodeTypes.IFNODE);
         this.condition = condition;
         this.thenBody = thenBody;
         this.elseBody = elseBody;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitIfNode(this);
     }
 
     /**
      * Gets the condition.
      * @return Returns a Node
      */
     public Node getCondition() {
         return condition;
     }
 
     /**
      * Gets the elseBody.
      * @return Returns a Node
      */
     public Node getElseBody() {
         return elseBody;
     }
 
     /**
      * Gets the thenBody.
      * @return Returns a Node
      */
     public Node getThenBody() {
         return thenBody;
     }
     
     public List childNodes() {
         return Node.createList(condition, thenBody, elseBody);
     }
 
 }
diff --git a/src/org/jruby/ast/InstAsgnNode.java b/src/org/jruby/ast/InstAsgnNode.java
index f0fc1c943c..7e29c77116 100644
--- a/src/org/jruby/ast/InstAsgnNode.java
+++ b/src/org/jruby/ast/InstAsgnNode.java
@@ -1,89 +1,89 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an instance variable assignment.
  *
  * @author  jpetersen
  */
 public class InstAsgnNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = 64498126883104604L;
 
     private String name;
 
     /**
      * Construtor.
      * @param name the name of the instance variable
      * @param valueNode the value of the variable
      **/
     public InstAsgnNode(ISourcePosition position, String name, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.INSTASGNNODE);
         this.name = name.intern();
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitInstAsgnNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/InstVarNode.java b/src/org/jruby/ast/InstVarNode.java
index dc2111ef8b..3811d03ddf 100644
--- a/src/org/jruby/ast/InstVarNode.java
+++ b/src/org/jruby/ast/InstVarNode.java
@@ -1,91 +1,91 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.IArityNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /** 
  * Represents an instance variable accessor.
  */
 public class InstVarNode extends Node implements IArityNode, INameNode {
     static final long serialVersionUID = 6839063763576230282L;
 
     private String name;
 
     public InstVarNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.INSTVARNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitInstVarNode(this);
     }
 
 	/**
 	 * A variable accessor takes no arguments.
 	 */
 	public Arity getArity() {
 		return Arity.noArguments();
 	}
 	
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
 
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/IterNode.java b/src/org/jruby/ast/IterNode.java
index 73e69ab101..5e75aaae97 100644
--- a/src/org/jruby/ast/IterNode.java
+++ b/src/org/jruby/ast/IterNode.java
@@ -1,108 +1,115 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.internal.runtime.methods.EvaluateCallable;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.ICallable;
 
 /**
  *
  * @see ForNode
  * @author  jpetersen
  */
 public class IterNode extends Node {
     static final long serialVersionUID = -9181965000180892184L;
 
     private final Node varNode;
     private final Node bodyNode;
     private Node iterNode;
     private transient ICallable callable;
 
     public IterNode(ISourcePosition position, Node varNode, Node bodyNode, Node iterNode) {
-        super(position);
+        super(position, NodeTypes.ITERNODE);
+        this.varNode = varNode;
+        this.bodyNode = bodyNode;
+        this.iterNode = iterNode;
+    }
+
+    public IterNode(ISourcePosition position, Node varNode, Node bodyNode, Node iterNode, int id) {
+        super(position, id);
         this.varNode = varNode;
         this.bodyNode = bodyNode;
         this.iterNode = iterNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitIterNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the iterNode.
      * @return Returns a Node
      */
     public Node getIterNode() {
         return iterNode;
     }
 
     /**
      * Sets the iterNode.
      * @param iterNode The iterNode to set
      */
     public void setIterNode(Node iterNode) {
         this.iterNode = iterNode;
     }
 
     /**
      * Gets the varNode.
      * @return Returns a Node
      */
     public Node getVarNode() {
         return varNode;
     }
     
     public List childNodes() {
         return Node.createList(varNode, bodyNode, iterNode);
     }
 
     public ICallable getCallable() {
         return callable == null ? callable = new EvaluateCallable(bodyNode, varNode) : callable;
     }
 }
diff --git a/src/org/jruby/ast/ListNode.java b/src/org/jruby/ast/ListNode.java
index 8682416551..ff6a50ec44 100644
--- a/src/org/jruby/ast/ListNode.java
+++ b/src/org/jruby/ast/ListNode.java
@@ -1,114 +1,118 @@
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
 package org.jruby.ast;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * All Nodes which have a list representation inherit this.  This is also used
  * as generic container for additional information that is not directly evaluated.
  * In particular, f_arg production rule uses this to capture arg information for
  * the editor projects who want position info saved.
  */
 public class ListNode extends Node {
     private static final long serialVersionUID = 1L;
     
     private List list = null;
 
-	public ListNode(ISourcePosition position) {
-		super(position);
-	}
+    public ListNode(ISourcePosition position, int id) {
+        super(position, id);
+    }
+
+    public ListNode(ISourcePosition position) {
+        super(position, NodeTypes.LISTNODE);
+    }
 	
     public ListNode add(Node node) {
         if (list == null) {
             list = new ArrayList();
         }
         list.add(node);
         return this;
     }
 
     public Iterator iterator() {
         return list == null ? EMPTY_LIST.iterator() : list.iterator();
     }
     
     public ListIterator reverseIterator() {
     	return list == null ? EMPTY_LIST.listIterator() : list.listIterator(list.size());
     }
     
     public int size() {
         return list == null ? 0 : list.size();
     }
     
     public ListNode addAll(ListNode other) {
         if (other != null) {
         	for (Iterator iter = other.iterator(); iter.hasNext();) {
                 add((Node) iter.next());
             }
         }
         return this;
     }
     
     public Node getLast() {
     	return list == null ? null : (Node) list.get(list.size() - 1);
     }
     
     public String toString() {
         String string = super.toString();
     	if (list == null) {
     		return string + ": {}";
     	}
     	StringBuffer b = new StringBuffer();
     	for (int i = 0; i < list.size(); i++) {
     		b.append(list.get(i));
             if (i + 1 < list.size()) {
                 b.append(", ");
             }
     	}
     	return string + ": {" + b.toString() + "}";
     }
     
     public List childNodes() {
     	return list;
     }
     
     public Instruction accept(NodeVisitor visitor) {
         throw new RuntimeException("Base class ListNode should never be evaluated");
     }
     
     public Node get(int idx) {
         return (Node)list.get(idx);
     }
 }
diff --git a/src/org/jruby/ast/LocalAsgnNode.java b/src/org/jruby/ast/LocalAsgnNode.java
index 12bad8996b..f4e4d873c0 100644
--- a/src/org/jruby/ast/LocalAsgnNode.java
+++ b/src/org/jruby/ast/LocalAsgnNode.java
@@ -1,93 +1,93 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * local variable assignment node.
  * @author  jpetersen
  */
 public class LocalAsgnNode extends AssignableNode implements INameNode {
     static final long serialVersionUID = 1118108700098164006L;
 
     private final int count;
     private String name;
 
     public LocalAsgnNode(ISourcePosition position, String name, int count, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.LOCALASGNNODE);
         this.name = name.intern();
         this.count = count;
         setValueNode(valueNode);
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitLocalAsgnNode(this);
     }
     
     /**
      * Name of the local assignment.
      **/
     public String getName() {
         return name;
     }
 
     /**
      * Gets the count.
      * @return Returns a int
      */
     public int getCount() {
         return count;
     }
     
     public List childNodes() {
         return createList(getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/LocalVarNode.java b/src/org/jruby/ast/LocalVarNode.java
index 1a661e1e9e..9133a27fe4 100644
--- a/src/org/jruby/ast/LocalVarNode.java
+++ b/src/org/jruby/ast/LocalVarNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class LocalVarNode extends Node {
     static final long serialVersionUID = 8562701804939317217L;
 
     private final int count;
 
     public LocalVarNode(ISourcePosition position, int count) {
-        super(position);
+        super(position, NodeTypes.LOCALVARNODE);
         this.count = count;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitLocalVarNode(this);
     }
 
     /**
      * Gets the count.
      * @return Returns a int
      */
     public int getCount() {
         return count;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/Match2Node.java b/src/org/jruby/ast/Match2Node.java
index b98115faea..06aa250d44 100644
--- a/src/org/jruby/ast/Match2Node.java
+++ b/src/org/jruby/ast/Match2Node.java
@@ -1,84 +1,84 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class Match2Node extends Node {
     static final long serialVersionUID = -5637326290741724784L;
 
     private final Node receiverNode;
     private final Node valueNode;
 
     public Match2Node(ISourcePosition position, Node receiverNode, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.MATCH2NODE);
 
         this.receiverNode = receiverNode;
         this.valueNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitMatch2Node(this);
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
 
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, valueNode);
     }
 
 }
diff --git a/src/org/jruby/ast/Match3Node.java b/src/org/jruby/ast/Match3Node.java
index 550567bf6a..0b10651b2f 100644
--- a/src/org/jruby/ast/Match3Node.java
+++ b/src/org/jruby/ast/Match3Node.java
@@ -1,84 +1,84 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class Match3Node extends Node {
     static final long serialVersionUID = -6147075329935023633L;
 
     private final Node receiverNode;
     private final Node valueNode;
 
     public Match3Node(ISourcePosition position, Node receiverNode, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.MATCH3NODE);
 
         this.receiverNode = receiverNode;
         this.valueNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitMatch3Node(this);
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
 
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, valueNode);
     }
 
 }
diff --git a/src/org/jruby/ast/MatchNode.java b/src/org/jruby/ast/MatchNode.java
index 5f7d11d23b..32c1d5b5df 100644
--- a/src/org/jruby/ast/MatchNode.java
+++ b/src/org/jruby/ast/MatchNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class MatchNode extends Node {
     static final long serialVersionUID = 9098121695708691474L;
 
     private final Node regexpNode;
 
     public MatchNode(ISourcePosition position, Node regexpNode) {
-        super(position);
+        super(position, NodeTypes.MATCHNODE);
         this.regexpNode = regexpNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitMatchNode(this);
     }
 
     /**
      * Gets the regexpNode.
      * @return Returns a Node
      */
     public Node getRegexpNode() {
         return regexpNode;
     }
 
     public List childNodes() {
         return createList(regexpNode);
     }
 
 }
diff --git a/src/org/jruby/ast/ModuleNode.java b/src/org/jruby/ast/ModuleNode.java
index f2b2b0c4b4..d966b4f824 100644
--- a/src/org/jruby/ast/ModuleNode.java
+++ b/src/org/jruby/ast/ModuleNode.java
@@ -1,82 +1,82 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a module definition.
  *
  * @author  jpetersen
  */
 public class ModuleNode extends Node implements IScopingNode {
     static final long serialVersionUID = 4938115602547834310L;
 
     private final Node cpath;
     private final ScopeNode bodyNode;
 
     public ModuleNode(ISourcePosition position, Node cpath, ScopeNode bodyNode) {
-        super(position);
+        super(position, NodeTypes.MODULENODE);
         this.cpath = cpath;
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitModuleNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a ScopeNode
      */
     public ScopeNode getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the name.
      * @return Representation of the module path+name
      */
     public Node getCPath() {
         return cpath;
     }
     
     public List childNodes() {
         return Node.createList(cpath, bodyNode);
     }
 }
diff --git a/src/org/jruby/ast/MultipleAsgnNode.java b/src/org/jruby/ast/MultipleAsgnNode.java
index 303a06cfec..50aa4cc5eb 100644
--- a/src/org/jruby/ast/MultipleAsgnNode.java
+++ b/src/org/jruby/ast/MultipleAsgnNode.java
@@ -1,95 +1,95 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /**
  *
  * @author  jpetersen
  */
 public class MultipleAsgnNode extends AssignableNode {
     static final long serialVersionUID = 5016291105152162748L;
 
     private final ListNode headNode;
     private final Node argsNode;
 
     public MultipleAsgnNode(ISourcePosition position, ListNode headNode, Node argsNode) {
-        super(position);
+        super(position, NodeTypes.MULTIPLEASGNNODE);
         this.headNode = headNode;
         this.argsNode = argsNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitMultipleAsgnNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a INode
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the headNode.
      * @return Returns a ListNode
      */
     public ListNode getHeadNode() {
         return headNode;
     }
 	
     /**
      * Number of arguments is dependent on headNodes size
      */
 	public Arity getArity() {
 		if (argsNode != null) {
 			return Arity.required(headNode == null ? 0 : headNode.size());
 		}
 		
 		return Arity.fixed(headNode.size()); 
 	}
     
     public List childNodes() {
         return Node.createList(headNode, argsNode, getValueNode());
     }
 
 }
diff --git a/src/org/jruby/ast/NewlineNode.java b/src/org/jruby/ast/NewlineNode.java
index c98dbcaaad..e35c8cc160 100644
--- a/src/org/jruby/ast/NewlineNode.java
+++ b/src/org/jruby/ast/NewlineNode.java
@@ -1,86 +1,86 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * A new (logical) source code line.
  * This is used to change the value of the ruby interpreter        
  * source and line values.
  * There is one such node for each logical line.  Logical line differs
  * from physical line in that a ';' can be used to make several logical
  * line out of a physical line and a physical line if it is in a comment
  * or in a string does not necessarily correspond to a physical line.
  * This is normally a wrapper around another more significant node.
  * The parser generates such a node around each separate statement.  
  *
  * @author  jpetersen
  */
 public class NewlineNode extends Node {
     static final long serialVersionUID = -6180129177863553832L;
 
     private final Node nextNode;
 
     public NewlineNode(ISourcePosition position, Node nextNode) {
-        super(position);
+        super(position, NodeTypes.NEWLINENODE);
 
         this.nextNode = nextNode;
     }
 
     /**
      * RubyMethod used by visitors.
      * accepts the visitor
      * @param iVisitor the visitor to accept
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitNewlineNode(this);
     }
 
     /**
      * Gets the nextNode.
      * @return Returns a Node
      */
     public Node getNextNode() {
         return nextNode;
     }
     
     public List childNodes() {
         return createList(nextNode);
     }
 
 
 }
diff --git a/src/org/jruby/ast/NextNode.java b/src/org/jruby/ast/NextNode.java
index 20d56ad665..ca2d1eec79 100644
--- a/src/org/jruby/ast/NextNode.java
+++ b/src/org/jruby/ast/NextNode.java
@@ -1,78 +1,78 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a 'next' statement.
  *
  * @author  jpetersen
  */
 public class NextNode extends Node {
     static final long serialVersionUID = -6688896555206419923L;
 
     private final Node valueNode;
     
     public NextNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.NEXTNODE);
         valueNode = null;
     }
 
     public NextNode(ISourcePosition position, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.NEXTNODE);
         this.valueNode = valueNode;
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitNextNode(this);
     }
     
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
     
     public List childNodes() {
         return createList(valueNode);
     }
 
 }
diff --git a/src/org/jruby/ast/NilNode.java b/src/org/jruby/ast/NilNode.java
index 0c564f25f2..195a84059a 100644
--- a/src/org/jruby/ast/NilNode.java
+++ b/src/org/jruby/ast/NilNode.java
@@ -1,70 +1,70 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class NilNode extends Node implements INameNode {
     static final long serialVersionUID = -8702073984472296708L;
 
     public NilNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.NILNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitNilNode(this);
     }
     
     /**
      * Name of nil node.
      **/
     public String getName() {
         return "nil";
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/Node.java b/src/org/jruby/ast/Node.java
index c2b9a38176..ea6f247388 100644
--- a/src/org/jruby/ast/Node.java
+++ b/src/org/jruby/ast/Node.java
@@ -1,108 +1,112 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.ast;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.evaluator.InstructionBundle;
 import org.jruby.evaluator.InstructionContext;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.ISourcePositionHolder;
 
 /**
  *
  * @author  jpetersen
  */
 public abstract class Node implements ISourcePositionHolder, InstructionContext, Serializable {
     static final long serialVersionUID = -5962822607672530224L;
     // We define an actual list to get around bug in java integration (1387115)
     static final List EMPTY_LIST = new ArrayList();
+    
+    public final int nodeId;
+    
     public InstructionBundle instruction;
 
     private ISourcePosition position;
 
-    public Node(ISourcePosition position) {
+    public Node(ISourcePosition position, int id) {
         this.position = position;
+        this.nodeId = id;
     }
 
     /**
      * Location of this node within the source
      */
     public ISourcePosition getPosition() {
         return position;
     }
 
 	public void setPosition(ISourcePosition position) {
 		this.position = position;
 	}
     
 	public abstract Instruction accept(NodeVisitor visitor);
 	public abstract List childNodes();
 
     static void addNode(Node node, List list) {
         if (node != null)
             list.add(node);
     }
 
     protected static List createList(Node node) {
         List list = new ArrayList();
         Node.addNode(node, list);
         return list;
     }
 
     protected  static List createList(Node node1, Node node2) {
         List list = createList(node1);
         Node.addNode(node2, list);
         return list;
     }
 
     protected  static List createList(Node node1, Node node2, Node node3) {
         List list = createList(node1, node2);
         Node.addNode(node3, list);
         return list;
     }
     
     public String toString() {
         return getNodeName() + "[]";
     }
 
     protected String getNodeName() {
         String name = getClass().getName();
         int i = name.lastIndexOf('.');
         String nodeType = name.substring(i + 1);
         return nodeType;
     }
 }
diff --git a/src/org/jruby/ast/NodeTypes.java b/src/org/jruby/ast/NodeTypes.java
new file mode 100644
index 0000000000..43f074ac43
--- /dev/null
+++ b/src/org/jruby/ast/NodeTypes.java
@@ -0,0 +1,106 @@
+package org.jruby.ast;
+
+public final class NodeTypes {
+    public static final int ALIASNODE = 0;
+    public static final int ANDNODE = 1;
+    public static final int ARGSCATNODE = 2;
+    public static final int ARGSNODE = 3;
+    public static final int ARGUMENTNODE = 4;
+    public static final int ARRAYNODE = 5;
+    public static final int ASSIGNABLENODE = 6;
+    public static final int BACKREFNODE = 7;
+    public static final int BEGINNODE = 8;
+    public static final int BIGNUMNODE = 9;
+    public static final int BINARYOPERATORNODE = 10;
+    public static final int BLOCKARGNODE = 11;
+    public static final int BLOCKNODE = 12;
+    public static final int BLOCKPASSNODE = 13;
+    public static final int BREAKNODE = 14;
+    public static final int CALLNODE = 15;
+    public static final int CASENODE = 16;
+    public static final int CLASSNODE = 17;
+    public static final int CLASSVARASGNNODE = 18;
+    public static final int CLASSVARDECLNODE = 19;
+    public static final int CLASSVARNODE = 20;
+    public static final int COLON2NODE = 21;
+    public static final int COLON3NODE = 22;
+    public static final int CONSTDECLNODE = 23;
+    public static final int CONSTNODE = 24;
+    public static final int DASGNNODE = 25;
+    public static final int DEFINEDNODE = 26;
+    public static final int DEFNNODE = 27;
+    public static final int DEFSNODE = 28;
+    public static final int DOTNODE = 29;
+    public static final int DREGEXPNODE = 30;
+    public static final int DSTRNODE = 31;
+    public static final int DSYMBOLNODE = 32;
+    public static final int DVARNODE = 33;
+    public static final int DXSTRNODE = 34;
+    public static final int ENSURENODE = 35;
+    public static final int EVSTRNODE = 36;
+    public static final int FALSENODE = 37;
+    public static final int FCALLNODE = 38;
+    public static final int FIXNUMNODE = 39;
+    public static final int FLIPNODE = 40;
+    public static final int FLOATNODE = 41;
+    public static final int FORNODE = 42;
+    public static final int GLOBALASGNNODE = 43;
+    public static final int GLOBALVARNODE = 44;
+    public static final int HASHNODE = 45;
+    public static final int IFNODE = 46;
+    public static final int INSTASGNNODE = 47;
+    public static final int INSTVARNODE = 48;
+    public static final int ISCOPINGNODE = 49;
+    public static final int ITERNODE = 50;
+    public static final int LISTNODE = 51;
+    public static final int LOCALASGNNODE = 52;
+    public static final int LOCALVARNODE = 53;
+    public static final int MATCH2NODE = 54;
+    public static final int MATCH3NODE = 55;
+    public static final int MATCHNODE = 56;
+    public static final int MODULENODE = 57;
+    public static final int MULTIPLEASGNNODE = 58;
+    public static final int NEWLINENODE = 59;
+    public static final int NEXTNODE = 60;
+    public static final int NILNODE = 61;
+    public static final int NODETYPES = 62;
+    public static final int NOTNODE = 63;
+    public static final int NTHREFNODE = 64;
+    public static final int OPASGNANDNODE = 65;
+    public static final int OPASGNNODE = 66;
+    public static final int OPASGNORNODE = 67;
+    public static final int OPELEMENTASGNNODE = 68;
+    public static final int OPTNNODE = 69;
+    public static final int ORNODE = 70;
+    public static final int POSTEXENODE = 71;
+    public static final int REDONODE = 72;
+    public static final int REGEXPNODE = 73;
+    public static final int RESCUEBODYNODE = 74;
+    public static final int RESCUENODE = 75;
+    public static final int RETRYNODE = 76;
+    public static final int RETURNNODE = 77;
+    public static final int SCLASSNODE = 78;
+    public static final int SCOPENODE = 79;
+    public static final int SELFNODE = 80;
+    public static final int SPLATNODE = 81;
+    public static final int STARNODE = 82;
+    public static final int STRNODE = 83;
+    public static final int SUPERNODE = 84;
+    public static final int SVALUENODE = 85;
+    public static final int SYMBOLNODE = 86;
+    public static final int TOARYNODE = 87;
+    public static final int TRUENODE = 88;
+    public static final int UNDEFNODE = 89;
+    public static final int UNTILNODE = 90;
+    public static final int VALIASNODE = 91;
+    public static final int VCALLNODE = 92;
+    public static final int WHENNODE = 93;
+    public static final int WHILENODE = 94;
+    public static final int XSTRNODE = 95;
+    public static final int YIELDNODE = 96;
+    public static final int ZARRAYNODE = 97;
+    public static final int ZEROARGNODE = 98;
+    public static final int ZSUPERNODE = 99;
+    
+    private NodeTypes() {}
+}
diff --git a/src/org/jruby/ast/NotNode.java b/src/org/jruby/ast/NotNode.java
index 61bd637182..2bbb14c7f5 100644
--- a/src/org/jruby/ast/NotNode.java
+++ b/src/org/jruby/ast/NotNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class NotNode extends Node {
     static final long serialVersionUID = -9044821606260233871L;
 
     private final Node conditionNode;
 
     public NotNode(ISourcePosition position, Node conditionNode) {
-        super(position);
+        super(position, NodeTypes.NOTNODE);
         this.conditionNode = conditionNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitNotNode(this);
     }
 
     /**
      * Gets the conditionNode.
      * @return Returns a Node
      */
     public Node getConditionNode() {
         return conditionNode;
     }
     
     public List childNodes() {
         return createList(conditionNode);
     }
 
 }
diff --git a/src/org/jruby/ast/NthRefNode.java b/src/org/jruby/ast/NthRefNode.java
index a1b38e9d59..c420735270 100644
--- a/src/org/jruby/ast/NthRefNode.java
+++ b/src/org/jruby/ast/NthRefNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a $number variable.
  *
  * @author  jpetersen
  */
 public class NthRefNode extends Node {
     static final long serialVersionUID = -3301605695065934063L;
 
     private final int matchNumber;
 
     public NthRefNode(ISourcePosition position, int matchNumber) {
-        super(position);
+        super(position, NodeTypes.NTHREFNODE);
         this.matchNumber = matchNumber;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitNthRefNode(this);
     }
 
     /**
      * Gets the matchNumber.
      * @return Returns a int
      */
     public int getMatchNumber() {
         return matchNumber;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/OpAsgnAndNode.java b/src/org/jruby/ast/OpAsgnAndNode.java
index 7f1fbfe749..52f2250068 100644
--- a/src/org/jruby/ast/OpAsgnAndNode.java
+++ b/src/org/jruby/ast/OpAsgnAndNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class OpAsgnAndNode extends Node implements BinaryOperatorNode {
     static final long serialVersionUID = 7366271929271260664L;
 
     private final Node firstNode;
     private final Node secondNode;
 
     public OpAsgnAndNode(ISourcePosition position, Node headNode, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.OPASGNANDNODE);
         firstNode = headNode;
         secondNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOpAsgnAndNode(this);
     }
 
     /**
      * Gets the firstNode.
      * @return Returns a Node
      */
     public Node getFirstNode() {
         return firstNode;
     }
 
     /**
      * Gets the secondNode.
      * @return Returns a Node
      */
     public Node getSecondNode() {
         return secondNode;
     }
 
     public List childNodes() {
         return Node.createList(firstNode, secondNode);
     }
 
 }
diff --git a/src/org/jruby/ast/OpAsgnNode.java b/src/org/jruby/ast/OpAsgnNode.java
index 9160f85461..a659084708 100644
--- a/src/org/jruby/ast/OpAsgnNode.java
+++ b/src/org/jruby/ast/OpAsgnNode.java
@@ -1,119 +1,119 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class OpAsgnNode extends Node {
     static final long serialVersionUID = -1950295226516487753L;
 
     private final Node receiverNode;
     private final Node valueNode;
     private String variableName;
     private String operatorName;
     private String variableNameAsgn;
 
     public OpAsgnNode(ISourcePosition position, Node receiverNode, Node valueNode, String variableName, String methodName) {
-        super(position);
+        super(position, NodeTypes.OPASGNNODE);
         this.receiverNode = receiverNode;
         this.valueNode = valueNode;
         this.variableName = variableName.intern();
         this.operatorName = methodName.intern();
         this.variableNameAsgn = (variableName + "=").intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         variableName = variableName.intern();
         operatorName = operatorName.intern();
         variableNameAsgn = variableNameAsgn.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOpAsgnNode(this);
     }
 
     /**
      * Gets the methodName.
      * @return Returns a String
      */
     public String getOperatorName() {
         return operatorName;
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
 
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
 
     /**
      * Gets the varibaleName.
      * @return Returns a String
      */
     public String getVariableName() {
         return variableName;
     }
     
     public String getVariableNameAsgn() {
         return variableNameAsgn;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, valueNode);
     }
 
 }
diff --git a/src/org/jruby/ast/OpAsgnOrNode.java b/src/org/jruby/ast/OpAsgnOrNode.java
index 7399d778df..eace80542c 100644
--- a/src/org/jruby/ast/OpAsgnOrNode.java
+++ b/src/org/jruby/ast/OpAsgnOrNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class OpAsgnOrNode extends Node implements BinaryOperatorNode {
     static final long serialVersionUID = -1503963105325984745L;
 
     private final Node firstNode;
     private final Node secondNode;
 
     public OpAsgnOrNode(ISourcePosition position, Node headNode, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.OPASGNORNODE);
         firstNode = headNode;
         secondNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOpAsgnOrNode(this);
     }
 
     /**
      * Gets the firstNode.
      * @return Returns a Node
      */
     public Node getFirstNode() {
         return firstNode;
     }
 
     /**
      * Gets the secondNode.
      * @return Returns a Node
      */
     public Node getSecondNode() {
         return secondNode;
     }
     
     public List childNodes() {
         return Node.createList(firstNode, secondNode);
     }
 
 }
diff --git a/src/org/jruby/ast/OpElementAsgnNode.java b/src/org/jruby/ast/OpElementAsgnNode.java
index 1b95c69509..88e199b47f 100644
--- a/src/org/jruby/ast/OpElementAsgnNode.java
+++ b/src/org/jruby/ast/OpElementAsgnNode.java
@@ -1,117 +1,117 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an operator assignment to an element.
  * 
  * This could be for example:
  * 
  * <pre>
  * a[4] += 5
  * a[3] &&= true
  * </pre>
  *
  * @author  jpetersen
  */
 public class OpElementAsgnNode extends Node {
     static final long serialVersionUID = 1509701560452403776L;
 
     private final Node receiverNode;
     private String operatorName;
     private final Node argsNode;
     private final Node valueNode;
 
     public OpElementAsgnNode(ISourcePosition position, Node receiverNode, String operatorName, Node argsNode, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.OPELEMENTASGNNODE);
         this.receiverNode = receiverNode;
         this.operatorName = operatorName.intern();
         this.argsNode = argsNode;
         this.valueNode = valueNode;
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         operatorName = operatorName.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOpElementAsgnNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
 
     /**
      * Gets the operatorName.
      * @return Returns a String
      */
     public String getOperatorName() {
         return operatorName;
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
 
     /**
      * Gets the valueNode.
      * @return Returns a Node
      */
     public Node getValueNode() {
         return valueNode;
     }
 
     public List childNodes() {
         return Node.createList(receiverNode, argsNode, valueNode);
     }
 }
diff --git a/src/org/jruby/ast/OptNNode.java b/src/org/jruby/ast/OptNNode.java
index e27a39b256..8a278eee25 100644
--- a/src/org/jruby/ast/OptNNode.java
+++ b/src/org/jruby/ast/OptNNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class OptNNode extends Node {
     static final long serialVersionUID = -742216664550880045L;
 
     private final Node bodyNode;
 
     public OptNNode(ISourcePosition position, Node bodyNode) {
-        super(position);
+        super(position, NodeTypes.OPTNNODE);
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOptNNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
     
     public List childNodes() {
         return createList(bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/OrNode.java b/src/org/jruby/ast/OrNode.java
index 91265297db..f225536c6e 100644
--- a/src/org/jruby/ast/OrNode.java
+++ b/src/org/jruby/ast/OrNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class OrNode extends Node implements BinaryOperatorNode {
     static final long serialVersionUID = 2822549471181976227L;
 
     private final Node firstNode;
     private final Node secondNode;
 
     public OrNode(ISourcePosition position, Node firstNode, Node secondNode) {
-        super(position);
+        super(position, NodeTypes.ORNODE);
         this.firstNode = firstNode;
         this.secondNode = secondNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitOrNode(this);
     }
 
     /**
      * Gets the firstNode.
      * @return Returns a Node
      */
     public Node getFirstNode() {
         return firstNode;
     }
 
     /**
      * Gets the secondNode.
      * @return Returns a Node
      */
     public Node getSecondNode() {
         return secondNode;
     }
 
     public List childNodes() {
         return Node.createList(firstNode, secondNode);
     }
 
 }
diff --git a/src/org/jruby/ast/PostExeNode.java b/src/org/jruby/ast/PostExeNode.java
index fe3f2a59b6..576faab640 100644
--- a/src/org/jruby/ast/PostExeNode.java
+++ b/src/org/jruby/ast/PostExeNode.java
@@ -1,61 +1,61 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class PostExeNode extends Node {
     static final long serialVersionUID = -2851659895226590014L;
 
     public PostExeNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.POSTEXENODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitPostExeNode(this);
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/RedoNode.java b/src/org/jruby/ast/RedoNode.java
index b32f8872c9..77014fa05e 100644
--- a/src/org/jruby/ast/RedoNode.java
+++ b/src/org/jruby/ast/RedoNode.java
@@ -1,61 +1,61 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class RedoNode extends Node {
     static final long serialVersionUID = -356433067591852187L;
 
     public RedoNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.REDONODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitRedoNode(this);
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/RegexpNode.java b/src/org/jruby/ast/RegexpNode.java
index 75d9cc6688..31e9a14b4d 100644
--- a/src/org/jruby/ast/RegexpNode.java
+++ b/src/org/jruby/ast/RegexpNode.java
@@ -1,93 +1,93 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 import java.util.regex.Pattern;
 
 import org.jruby.RegexpTranslator;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a simple regular expression literal.
  *
  * @author  jpetersen
  */
 public class RegexpNode extends Node implements ILiteralNode {
     static final long serialVersionUID = -1566813018564622077L;
 
     private static final RegexpTranslator translator = new RegexpTranslator();
     
     private Pattern pattern;
     private final String value;
     private final int options;
     
     public RegexpNode(ISourcePosition position, String value, int options) {
-        super(position);
+        super(position, NodeTypes.REGEXPNODE);
         
         this.value = value;
         this.options = options;
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitRegexpNode(this);
     }
 
     /**
      * Gets the options.
      * @return Returns a int
      */
     public int getOptions() {
         return options;
     }
 
     /**
      * Gets the value.
      * @return Returns a String
      */
     public String getValue() {
         return value;
     }
     
     public Pattern getPattern() {
         if (pattern == null) {
             pattern = translator.translate(value, options, Pattern.UNIX_LINES);
         }
         return pattern;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/RescueBodyNode.java b/src/org/jruby/ast/RescueBodyNode.java
index 93df695b44..5bbfb91daf 100644
--- a/src/org/jruby/ast/RescueBodyNode.java
+++ b/src/org/jruby/ast/RescueBodyNode.java
@@ -1,94 +1,94 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class RescueBodyNode extends Node {
     static final long serialVersionUID = -6414517081810625663L;
 
     private final Node exceptionNodes;
     private final Node bodyNode;
     private final RescueBodyNode optRescueNode;
 
     public RescueBodyNode(ISourcePosition position, Node exceptionNodes, Node bodyNode, RescueBodyNode optRescueNode) {
-        super(position);
+        super(position, NodeTypes.RESCUEBODYNODE);
         this.exceptionNodes = exceptionNodes;
         this.bodyNode = bodyNode;
         this.optRescueNode = optRescueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitRescueBodyNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Get the next rescue node (if any).
      */
     public RescueBodyNode getOptRescueNode() {
         return optRescueNode;
     }
 
     /**
      * Gets the exceptionNodes.
      * @return Returns a Node
      */
     public Node getExceptionNodes() {
         return exceptionNodes;
     }
     
     public List childNodes() {
     	if (optRescueNode != null)
     		return Node.createList(exceptionNodes, bodyNode, optRescueNode);
     	return Node.createList(exceptionNodes, bodyNode);
     	
     }
 }
diff --git a/src/org/jruby/ast/RescueNode.java b/src/org/jruby/ast/RescueNode.java
index b23f3608c5..ac3325c4c9 100644
--- a/src/org/jruby/ast/RescueNode.java
+++ b/src/org/jruby/ast/RescueNode.java
@@ -1,94 +1,94 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class RescueNode extends Node {
     static final long serialVersionUID = -4757038578511808125L;
 
     private final Node bodyNode;
     private final RescueBodyNode rescueNode;
     private final Node elseNode;
     
     public RescueNode(ISourcePosition position, Node bodyNode, RescueBodyNode rescueNode, Node elseNode) {
-        super(position);
+        super(position, NodeTypes.RESCUENODE);
         this.bodyNode = bodyNode;
         this.rescueNode = rescueNode;
         this.elseNode = elseNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitRescueNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the elseNode.
      * @return Returns a Node
      */
     public Node getElseNode() {
         return elseNode;
     }
 
     /**
      * Gets the first rescueNode.
      * @return Returns a Node
      */
     public RescueBodyNode getRescueNode() {
         return rescueNode;
     }
     
     public List childNodes() {
         return Node.createList(rescueNode, bodyNode, elseNode);
     }
 
 }
diff --git a/src/org/jruby/ast/RetryNode.java b/src/org/jruby/ast/RetryNode.java
index 858b826539..280649b499 100644
--- a/src/org/jruby/ast/RetryNode.java
+++ b/src/org/jruby/ast/RetryNode.java
@@ -1,62 +1,62 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a 'retry' statement.
  *
  * @author  jpetersen
  */
 public class RetryNode extends Node {
     static final long serialVersionUID = 4648280998968560181L;
 
     public RetryNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.RETRYNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitRetryNode(this);
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/ReturnNode.java b/src/org/jruby/ast/ReturnNode.java
index d6bfc77d35..51d7b649ec 100644
--- a/src/org/jruby/ast/ReturnNode.java
+++ b/src/org/jruby/ast/ReturnNode.java
@@ -1,78 +1,78 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a return statement.
  *
  * @author  jpetersen
  */
 public class ReturnNode extends Node {
     static final long serialVersionUID = -6549592319167820636L;
 
     private final Node valueNode;
     private Object target;
 
     public ReturnNode(ISourcePosition position, Node valueNode) {
-        super(position);
+        super(position, NodeTypes.RETURNNODE);
         this.valueNode = valueNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitReturnNode(this);
     }
 
     public Node getValueNode() {
         return valueNode;
     }
 
     public Object getTarget() {
         return target;
     }
 
     public void setTarget(Object target) {
         this.target = target;
     }
     
     public List childNodes() {
         return createList(valueNode);
     }
 
 }
diff --git a/src/org/jruby/ast/SClassNode.java b/src/org/jruby/ast/SClassNode.java
index 6aae04f628..d4c5e5c4c5 100644
--- a/src/org/jruby/ast/SClassNode.java
+++ b/src/org/jruby/ast/SClassNode.java
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Singleton class definition.
  * 
  * <pre>
  * class &lt;&lt; anObject
  * 
  * end
  * </pre>
  *
  * @author  jpetersen
  */
 public class SClassNode extends Node {
     static final long serialVersionUID = -3706492163082062224L;
 
     private final Node receiverNode;
     private final ScopeNode bodyNode;
 
     public SClassNode(ISourcePosition position, Node recvNode, ScopeNode bodyNode) {
-        super(position);
+        super(position, NodeTypes.SCLASSNODE);
         this.receiverNode = recvNode;
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitSClassNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a ScopeNode
      */
     public ScopeNode getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the receiverNode.
      * @return Returns a Node
      */
     public Node getReceiverNode() {
         return receiverNode;
     }
     
     public List childNodes() {
         return Node.createList(receiverNode, bodyNode);
     }
 }
diff --git a/src/org/jruby/ast/SValueNode.java b/src/org/jruby/ast/SValueNode.java
index 5d4e046359..2f2429c845 100644
--- a/src/org/jruby/ast/SValueNode.java
+++ b/src/org/jruby/ast/SValueNode.java
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 public class SValueNode extends Node {
 	private static final long serialVersionUID = 3834587720830891576L;
 
 	private final Node node;
     
     public SValueNode(ISourcePosition position, Node node) {
-        super(position);
+        super(position, NodeTypes.SVALUENODE);
         this.node = node;
     }
 
     public Instruction accept(NodeVisitor visitor) {
         return visitor.visitSValueNode(this);
     }
     
     public Node getValue() {
         return node;
     }
 
     public List childNodes() {
         return createList(node);
     }
 
 }
diff --git a/src/org/jruby/ast/ScopeNode.java b/src/org/jruby/ast/ScopeNode.java
index 7410cbd9c5..9840915855 100644
--- a/src/org/jruby/ast/ScopeNode.java
+++ b/src/org/jruby/ast/ScopeNode.java
@@ -1,106 +1,106 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Scope in the parse tree.
  * indicates where in the parse tree a new scope should be started when evaling.
  * Unlike many node this is not created directly as part of the parce process,
  * rather it is created as a side effect of other things like creating a ClassNode
  * or SClassNode.  It can also be created by evaling a DefnNode or DefsNode as
  * part of the call to copyNodeScope.
  *
  * @author  jpetersen
  */
 public class ScopeNode extends Node {
     static final long serialVersionUID = 3694868125861223886L;
 
     private String[] localNames;
     private final Node bodyNode;
 
     public ScopeNode(ISourcePosition position, String[] table, Node bodyNode) {
-        super(position);
+        super(position, NodeTypes.SCOPENODE);
         this.localNames = new String[table.length];
         for (int i = 0; i < table.length; i++) 
             if (table[i] != null)
                 localNames[i] = table[i].intern();
         this.bodyNode = bodyNode;
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         String[] old = localNames;
         this.localNames = new String[old.length];
         for (int i = 0; i < old.length; i++) 
             if (old[i] != null)
                 localNames[i] = old[i].intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitScopeNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the localNames.
      * @return Returns a List
      */
     public String[] getLocalNames() {
         return localNames;
     }
     
     public List childNodes() {
         return createList(bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/SelfNode.java b/src/org/jruby/ast/SelfNode.java
index f67965ada8..1bc6f43855 100644
--- a/src/org/jruby/ast/SelfNode.java
+++ b/src/org/jruby/ast/SelfNode.java
@@ -1,71 +1,71 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class SelfNode extends Node implements INameNode {
     static final long serialVersionUID = 7003057726029491707L;
 
 
     public SelfNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.SELFNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitSelfNode(this);
     }
     
     /**
      * Get name of self node.
      */
     public String getName() {
         return "self";
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/SplatNode.java b/src/org/jruby/ast/SplatNode.java
index e44dc6b40d..b7a788c71e 100644
--- a/src/org/jruby/ast/SplatNode.java
+++ b/src/org/jruby/ast/SplatNode.java
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 
 public class SplatNode extends Node {
     static final long serialVersionUID = -1649004231006940340L;
     
     private final Node node;
 
     public SplatNode(ISourcePosition position, Node node) {
-        super(position);
+        super(position, NodeTypes.SPLATNODE);
         this.node = node;
     }
 
     public Instruction accept(NodeVisitor visitor) {
         return visitor.visitSplatNode(this);
     }
     
     public Node getValue() {
         return node;
     }
 
     public List childNodes() {
         return createList(node);
     }
 
 }
diff --git a/src/org/jruby/ast/StarNode.java b/src/org/jruby/ast/StarNode.java
index 751f7582c1..3547ef3cfb 100644
--- a/src/org/jruby/ast/StarNode.java
+++ b/src/org/jruby/ast/StarNode.java
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a star in a multiple assignent.
  * only used in an instanceof check, this node is never visited.
  * @author  jpetersen
  */
 public class StarNode extends Node {
     static final long serialVersionUID = 8314131941892458677L;
 
 
     /**
      * Constructor for StarNode.
      */
     public StarNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.STARNODE);
     }
 
     /**
      * @see Node#accept(NodeVisitor)
      */
     public Instruction accept(NodeVisitor visitor) {
     	return null; // never visited, should be fine
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/StrNode.java b/src/org/jruby/ast/StrNode.java
index f910fd904f..767ff081b6 100644
--- a/src/org/jruby/ast/StrNode.java
+++ b/src/org/jruby/ast/StrNode.java
@@ -1,75 +1,75 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Representing a simple String literal.
  *
  * @author  jpetersen
  */
 public class StrNode extends Node implements ILiteralNode {
     static final long serialVersionUID = 4544779503072130759L;
 
     private final String value;
 
     public StrNode(ISourcePosition position, String value) {
-        super(position);
+        super(position, NodeTypes.STRNODE);
         this.value = value;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitStrNode(this);
     }
 
     /**
      * Gets the value.
      * @return Returns a String
      */
     public String getValue() {
         return value;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/SuperNode.java b/src/org/jruby/ast/SuperNode.java
index 092fea745e..79b11fb6ae 100644
--- a/src/org/jruby/ast/SuperNode.java
+++ b/src/org/jruby/ast/SuperNode.java
@@ -1,73 +1,73 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class SuperNode extends Node {
     static final long serialVersionUID = 5158689332796676417L;
 
     private final Node argsNode;
 
     public SuperNode(ISourcePosition position, Node argsNode) {
-        super(position);
+        super(position, NodeTypes.SUPERNODE);
         this.argsNode = argsNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitSuperNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
     
     public List childNodes() {
         return createList(argsNode);
     }
 
 }
diff --git a/src/org/jruby/ast/SymbolNode.java b/src/org/jruby/ast/SymbolNode.java
index b640fe4251..1641018de5 100644
--- a/src/org/jruby/ast/SymbolNode.java
+++ b/src/org/jruby/ast/SymbolNode.java
@@ -1,80 +1,80 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a symbol (:symbol_name).
  *
  * @author  jpetersen
  */
 public class SymbolNode extends Node implements ILiteralNode, INameNode {
     static final long serialVersionUID = 3168450881711346709L;
 
 	private String name;
 
 	public SymbolNode(ISourcePosition position, String name) {
-	    super(position);
+	    super(position, NodeTypes.SYMBOLNODE);
 	    this.name = name.intern();
 	}
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitSymbolNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/ToAryNode.java b/src/org/jruby/ast/ToAryNode.java
index 6ebec642a8..b7c4f53562 100644
--- a/src/org/jruby/ast/ToAryNode.java
+++ b/src/org/jruby/ast/ToAryNode.java
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 public class ToAryNode extends Node {
 	private static final long serialVersionUID = 3256723961709802546L;
 
 	private final Node node;
 
     public ToAryNode(ISourcePosition position, Node node) {
-        super(position);
+        super(position, NodeTypes.TOARYNODE);
 
         this.node = node;
     }
 
     public Instruction accept(NodeVisitor visitor) {
         return visitor.visitToAryNode(this);
     }
     
     public Node getValue() {
         return node;
     }
     
     public List childNodes() {
         return createList(node);
     }
 
 }
diff --git a/src/org/jruby/ast/TrueNode.java b/src/org/jruby/ast/TrueNode.java
index a17ff8d1f0..cd30cb7890 100644
--- a/src/org/jruby/ast/TrueNode.java
+++ b/src/org/jruby/ast/TrueNode.java
@@ -1,71 +1,71 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class TrueNode extends Node implements INameNode {
     static final long serialVersionUID = -8198252481133454778L;
 
 
     public TrueNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.TRUENODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitTrueNode(this);
     }
     
     /**
      * Name of the true node.
      */
     public String getName() {
         return "true";
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/UndefNode.java b/src/org/jruby/ast/UndefNode.java
index 7b7591d561..db92b4bca6 100644
--- a/src/org/jruby/ast/UndefNode.java
+++ b/src/org/jruby/ast/UndefNode.java
@@ -1,80 +1,80 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an undef statement.
  *
  * @author  jpetersen
  */
 public class UndefNode extends Node {
     static final long serialVersionUID = -8829084073375820727L;
 
     private String name;
 
     public UndefNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.UNDEFNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitUndefNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/UntilNode.java b/src/org/jruby/ast/UntilNode.java
index 26b9eef923..e6e683ad4c 100644
--- a/src/org/jruby/ast/UntilNode.java
+++ b/src/org/jruby/ast/UntilNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an until statement.
  *
  * @author  jpetersen
  */
 public class UntilNode extends Node {
     static final long serialVersionUID = -2929327250252365636L;
 
     private final Node conditionNode;
     private final Node bodyNode;
 
     public UntilNode(ISourcePosition position, Node conditionNode, Node bodyNode) {
-        super(position);
+        super(position, NodeTypes.UNTILNODE);
         this.conditionNode = conditionNode;
         this.bodyNode = bodyNode;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitUntilNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the conditionNode.
      * @return Returns a Node
      */
     public Node getConditionNode() {
         return conditionNode;
     }
 
     public List childNodes() {
         return Node.createList(conditionNode, bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/VAliasNode.java b/src/org/jruby/ast/VAliasNode.java
index 6deaa2e73a..2de2d55727 100644
--- a/src/org/jruby/ast/VAliasNode.java
+++ b/src/org/jruby/ast/VAliasNode.java
@@ -1,92 +1,92 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents an alias of a global variable.
  *
  * @author  jpetersen
  */
 public class VAliasNode extends Node {
     static final long serialVersionUID = 8647860367861922838L;
 
     private String oldName;
     private String newName;
 
     public VAliasNode(ISourcePosition position, String newName, String oldName) {
-        super(position);
+        super(position, NodeTypes.VALIASNODE);
         this.oldName = oldName.intern();
         this.newName = newName.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         oldName = oldName.intern();
         newName = newName.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitVAliasNode(this);
     }
 
     /**
      * Gets the newName.
      * @return Returns a String
      */
     public String getNewName() {
         return newName;
     }
 
     /**
      * Gets the oldName.
      * @return Returns a String
      */
     public String getOldName() {
         return oldName;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/VCallNode.java b/src/org/jruby/ast/VCallNode.java
index 6e2ba693af..72c28186d5 100644
--- a/src/org/jruby/ast/VCallNode.java
+++ b/src/org/jruby/ast/VCallNode.java
@@ -1,83 +1,83 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby.ast;
 
 import java.io.IOException;
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * RubyMethod call without any arguments
  *
  * @author  jpetersen
  */
 public class VCallNode extends Node {
     static final long serialVersionUID = -7678578490000574578L;
 
     private String name;
 
     public VCallNode(ISourcePosition position, String name) {
-        super(position);
+        super(position, NodeTypes.VCALLNODE);
         this.name = name.intern();
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         
         // deserialized strings are not interned; intern it now
         name = name.intern();
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitVCallNode(this);
     }
 
     /**
      * Gets the methodName.
      * @return Returns a String
      */
     public String getMethodName() {
         return name;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/WhenNode.java b/src/org/jruby/ast/WhenNode.java
index ddf6b0cc56..3ae1e872a1 100644
--- a/src/org/jruby/ast/WhenNode.java
+++ b/src/org/jruby/ast/WhenNode.java
@@ -1,90 +1,90 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * @author  jpetersen
  */
 public class WhenNode extends Node {
     static final long serialVersionUID = 9099987602002276708L;
 
     private final Node expressionNodes;
     private final Node bodyNode;
     private final Node nextCase;
 
     public WhenNode(ISourcePosition position, Node expressionNodes, Node bodyNode, Node nextCase) {
-        super(position);
+        super(position, NodeTypes.WHENNODE);
         this.expressionNodes = expressionNodes;
         this.bodyNode = bodyNode;
         this.nextCase = nextCase;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitWhenNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a INode
      */
     public Node getBodyNode() {
         return bodyNode;
     }
     
     /**
      * Gets the next case node (if any).
      */
     public Node getNextCase() {
         return nextCase;
     }
 
     /**
      * Get the expressionNode(s).
      */
     public Node getExpressionNodes() {
         return expressionNodes;
     }
 
     public List childNodes() {
         return Node.createList(expressionNodes, bodyNode, nextCase);
     }
 }
diff --git a/src/org/jruby/ast/WhileNode.java b/src/org/jruby/ast/WhileNode.java
index 881400781a..af390d65d3 100644
--- a/src/org/jruby/ast/WhileNode.java
+++ b/src/org/jruby/ast/WhileNode.java
@@ -1,105 +1,105 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a while stetement. This could be the both versions:
  * 
  * while &lt;condition&gt;
  *    &lt;body&gt;
  * end
  * 
  * and
  * 
  * &lt;body&gt; 'while' &lt;condition&gt;
  *
  * @author  jpetersen
  */
 public class WhileNode extends Node {
     static final long serialVersionUID = -5355364190446060873L;
 
     private final Node conditionNode;
     private final Node bodyNode;
     private final boolean evaluateAtStart;
 
     public WhileNode(ISourcePosition position, Node conditionNode, Node bodyNode) {
 	this(position, conditionNode, bodyNode, true);
     }
 
     public WhileNode(ISourcePosition position, Node conditionNode, Node bodyNode,
             boolean evalAtStart) {
-        super(position);
+        super(position, NodeTypes.WHILENODE);
         this.conditionNode = conditionNode;
         this.bodyNode = bodyNode;
         this.evaluateAtStart = evalAtStart;
     }
     
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitWhileNode(this);
     }
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the conditionNode.
      * @return Returns a Node
      */
     public Node getConditionNode() {
         return conditionNode;
     }
     
     /**
      * Determine whether this is while or do while
      * @return true if you are a while, false if do while
      */
     public boolean evaluateAtStart() {
         return evaluateAtStart;
     }
 
     public List childNodes() {
         return Node.createList(conditionNode, bodyNode);
     }
 
 }
diff --git a/src/org/jruby/ast/XStrNode.java b/src/org/jruby/ast/XStrNode.java
index a1c6e9bad7..a455dada83 100644
--- a/src/org/jruby/ast/XStrNode.java
+++ b/src/org/jruby/ast/XStrNode.java
@@ -1,75 +1,75 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  * Backtick string
  *
  * @author  jpetersen
  */
 public class XStrNode extends Node implements ILiteralNode {
     static final long serialVersionUID = 1371310021447439748L;
 
     private final String value;
 
     public XStrNode(ISourcePosition position, String value) {
-        super(position);
+        super(position, NodeTypes.XSTRNODE);
         this.value = value;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitXStrNode(this);
     }
 
     /**
      * Gets the value.
      * @return Returns a String
      */
     public String getValue() {
         return value;
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/YieldNode.java b/src/org/jruby/ast/YieldNode.java
index a90fd2cf8b..1136180b1d 100644
--- a/src/org/jruby/ast/YieldNode.java
+++ b/src/org/jruby/ast/YieldNode.java
@@ -1,79 +1,79 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /** Represents a yield statement.
  *
  * @author  jpetersen
  */
 public class YieldNode extends Node {
     static final long serialVersionUID = -4136185449481135660L;
 
     private final Node argsNode;
     private final boolean checkState;
 
     public YieldNode(ISourcePosition position, Node argsNode, boolean checkState) {
-        super(position);
+        super(position, NodeTypes.YIELDNODE);
         this.argsNode = argsNode;
         this.checkState = checkState;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitYieldNode(this);
     }
 
     /**
      * Gets the argsNode.
      * @return Returns a Node
      */
     public Node getArgsNode() {
         return argsNode;
     }
     
     public boolean getCheckState() {
         return checkState;
     }
 
     public List childNodes() {
         return createList(argsNode);
     }
 
 }
diff --git a/src/org/jruby/ast/ZArrayNode.java b/src/org/jruby/ast/ZArrayNode.java
index 59afcbb9f1..a2e5d417e2 100644
--- a/src/org/jruby/ast/ZArrayNode.java
+++ b/src/org/jruby/ast/ZArrayNode.java
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 /**
  *
  * zero length list
  *
  * @author  jpetersen
  */
 public class ZArrayNode extends Node implements ILiteralNode {
     static final long serialVersionUID = -5004157166982016917L;
 
 
     public ZArrayNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.ZARRAYNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitZArrayNode(this);
     }
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 
 }
diff --git a/src/org/jruby/ast/ZSuperNode.java b/src/org/jruby/ast/ZSuperNode.java
index 84972608ac..5238a19796 100644
--- a/src/org/jruby/ast/ZSuperNode.java
+++ b/src/org/jruby/ast/ZSuperNode.java
@@ -1,69 +1,69 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.IArityNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /**
  * a call to 'super(...)' in a method.
  */
 public class ZSuperNode extends Node implements IArityNode {
     static final long serialVersionUID = 6109129030317216863L;
 
     public ZSuperNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.ZSUPERNODE);
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Instruction accept(NodeVisitor iVisitor) {
         return iVisitor.visitZSuperNode(this);
     }
 	
 	/**
 	 * 'super' can take any number of arguments.
 	 */
 	public Arity getArity() {
 		return Arity.optional();
 	}
     
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/ast/ZeroArgNode.java b/src/org/jruby/ast/ZeroArgNode.java
index 5b58a0f1c5..b0666fa1f6 100644
--- a/src/org/jruby/ast/ZeroArgNode.java
+++ b/src/org/jruby/ast/ZeroArgNode.java
@@ -1,75 +1,75 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.ast;
 
 import java.util.List;
 
 import org.jruby.ast.types.IArityNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.evaluator.Instruction;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 
 /** Represents a zero arg in a block.
  * this is never visited and is used only in an instanceof check
  * <pre>
  * do ||
  * end
  * </pre>
  *
  * @author  jpetersen
  */
 public class ZeroArgNode extends Node implements IArityNode {
     static final long serialVersionUID = 6596791950608957025L;
 
     public ZeroArgNode(ISourcePosition position) {
-        super(position);
+        super(position, NodeTypes.ZEROARGNODE);
     }
 
     /**
      * @see Node#accept(NodeVisitor)
      */
     public Instruction accept(NodeVisitor visitor) {
     	return null; // never visited, should be ok
     }
 	
 	/**
      * Zero arguments...
      */
 	public Arity getArity() {
 		return Arity.noArguments();
 	}
 
     public List childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/src/org/jruby/evaluator/AssignmentVisitor.java b/src/org/jruby/evaluator/AssignmentVisitor.java
index 20fa70a709..800d7627f1 100644
--- a/src/org/jruby/evaluator/AssignmentVisitor.java
+++ b/src/org/jruby/evaluator/AssignmentVisitor.java
@@ -1,172 +1,176 @@
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
 
+import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.visitor.AbstractVisitor;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class AssignmentVisitor extends AbstractVisitor {
-    private EvaluationState state;
     private IRubyObject value;
     private boolean check;
+    private IRubyObject result;
+    private IRubyObject self;
+    private IRuby runtime;
 
-    public AssignmentVisitor(EvaluationState state) {
-        this.state = state;
+    public AssignmentVisitor(IRubyObject self) {
+        this.self = self;
+        this.runtime = self.getRuntime();
     }
 
     public IRubyObject assign(Node node, IRubyObject aValue, boolean aCheck) {
         this.value = aValue;
         this.check = aCheck;
 
         acceptNode(node);
 
-        return state.getResult();
+        return result;
     }
 
 	/**
 	 * @see AbstractVisitor#visitNode(Node)
 	 */
 	protected Instruction visitNode(Node iVisited) {
 		assert false;
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitCallNode(CallNode)
 	 */
 	public Instruction visitCallNode(CallNode iVisited) {
-        IRubyObject receiver = state.begin(iVisited.getReceiverNode());
+        IRubyObject receiver = EvaluationState.eval(runtime.getCurrentContext(), iVisited.getReceiverNode(), runtime.getCurrentContext().getFrameSelf());
 
         if (iVisited.getArgsNode() == null) { // attribute set.
             receiver.callMethod(iVisited.getName(), new IRubyObject[] {value}, CallType.NORMAL);
         } else { // element set
-            RubyArray args = (RubyArray)state.begin(iVisited.getArgsNode());
+            RubyArray args = (RubyArray)EvaluationState.eval(runtime.getCurrentContext(), iVisited.getArgsNode(), runtime.getCurrentContext().getFrameSelf());
             args.append(value);
             receiver.callMethod(iVisited.getName(), args.toJavaArray(), CallType.NORMAL);
         }
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitClassVarAsgnNode(ClassVarAsgnNode)
 	 */
 	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
-		state.getThreadContext().getRubyClass().setClassVar(iVisited.getName(), value);
+		runtime.getCurrentContext().getRubyClass().setClassVar(iVisited.getName(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitClassVarDeclNode(ClassVarDeclNode)
 	 */
 	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
-        ThreadContext tc = state.getThreadContext();
-		if (state.runtime.getVerbose().isTrue()
+        ThreadContext tc = runtime.getCurrentContext();
+		if (runtime.getVerbose().isTrue()
 				&& tc.getRubyClass().isSingleton()) {
-            state.runtime.getWarnings().warn(iVisited.getPosition(),
+            runtime.getWarnings().warn(iVisited.getPosition(),
 					"Declaring singleton class variable.");
 		}
         tc.getRubyClass().setClassVar(iVisited.getName(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitConstDeclNode(ConstDeclNode)
 	 */
 	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
 		if (iVisited.getPathNode() == null) {
-			state.getThreadContext().getRubyClass().defineConstant(iVisited.getName(), value);
+			runtime.getCurrentContext().getRubyClass().defineConstant(iVisited.getName(), value);
 		} else {
-			((RubyModule) state.begin(iVisited.getPathNode())).defineConstant(iVisited.getName(), value);
+			((RubyModule) EvaluationState.eval(runtime.getCurrentContext(), iVisited.getPathNode(), runtime.getCurrentContext().getFrameSelf())).defineConstant(iVisited.getName(), value);
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitDAsgnNode(DAsgnNode)
 	 */
 	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
-        state.getThreadContext().getCurrentDynamicVars().set(iVisited.getName(), value);
+        runtime.getCurrentContext().getCurrentDynamicVars().set(iVisited.getName(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitGlobalAsgnNode(GlobalAsgnNode)
 	 */
 	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
-        state.runtime.getGlobalVariables().set(iVisited.getName(), value);
+        runtime.getGlobalVariables().set(iVisited.getName(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitInstAsgnNode(InstAsgnNode)
 	 */
 	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
-		state.getSelf().setInstanceVariable(iVisited.getName(), value);
+		self.setInstanceVariable(iVisited.getName(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitLocalAsgnNode(LocalAsgnNode)
 	 */
 	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
-        state.runtime.getCurrentContext().getFrameScope().setValue(iVisited.getCount(), value);
+        runtime.getCurrentContext().getFrameScope().setValue(iVisited.getCount(), value);
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitMultipleAsgnNode(MultipleAsgnNode)
 	 */
 	public Instruction visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
 		if (!(value instanceof RubyArray)) {
-			value = RubyArray.newArray(state.runtime, value);
+			value = RubyArray.newArray(runtime, value);
 		}
-        state.setResult(state.getThreadContext()
-				.mAssign(state.getSelf(), iVisited, (RubyArray) value, check));
+        result = runtime.getCurrentContext()
+				.mAssign(self, iVisited, (RubyArray) value, check);
 		return null;
 	}
 }
\ No newline at end of file
diff --git a/src/org/jruby/evaluator/DefinedVisitor.java b/src/org/jruby/evaluator/DefinedVisitor.java
index 183cac6d81..5db83a088c 100644
--- a/src/org/jruby/evaluator/DefinedVisitor.java
+++ b/src/org/jruby/evaluator/DefinedVisitor.java
@@ -1,451 +1,452 @@
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
 package org.jruby.evaluator;
 
 import java.util.Iterator;
 
+import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FalseNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.LocalVarNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NilNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.SelfNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.TrueNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.visitor.AbstractVisitor;
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This visitor is used to evaluate a defined? statement.
  * 
  * @author jpetersen
  */
 public class DefinedVisitor extends AbstractVisitor {
-    private EvaluationState state;
+    private IRuby runtime;
 
     private String definition;
 
-    public DefinedVisitor(EvaluationState state) {
-        this.state = state;
+    public DefinedVisitor(IRuby runtime) {
+        this.runtime = runtime;
     }
 
     public String getDefinition(Node expression) {
         definition = null;
 
         acceptNode(expression);
 
         return definition;
     }
 
     public String getArgumentDefinition(Node node, String type) {
         if (node == null) {
             return type;
         } else if (node instanceof ArrayNode) {
             Iterator iter = ((ArrayNode)node).iterator();
             while (iter.hasNext()) {
                 if (getDefinition((Node)iter.next()) == null) {
                     return null;
                 }
             }
         } else if (getDefinition(node) == null) {
             return null;
         }
         return type;
     }
 
     /**
      * @see AbstractVisitor#visitNode(Node)
      */
     protected Instruction visitNode(Node iVisited) {
         try {
-            new EvaluationState(state.runtime, state.getSelf()).begin(iVisited);
+            EvaluationState.eval(runtime.getCurrentContext(), iVisited, runtime.getCurrentContext().getFrameSelf());
             definition = "expression";
         } catch (JumpException jumpExcptn) {
         }
 		return null;
     }
 
 	/**
 	 * @see AbstractVisitor#visitSuperNode(SuperNode)
 	 */
 	public Instruction visitSuperNode(SuperNode iVisited) {
-        ThreadContext tc = state.getThreadContext();
+        ThreadContext tc = runtime.getCurrentContext();
 		String lastMethod = tc.getFrameLastFunc();
 		RubyModule lastClass = tc.getFrameLastClass();
 		if (lastMethod != null && lastClass != null
 				&& lastClass.getSuperClass().isMethodBound(lastMethod, false)) {
 			definition = getArgumentDefinition(iVisited.getArgsNode(), "super");
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitZSuperNode(ZSuperNode)
 	 */
 	public Instruction visitZSuperNode(ZSuperNode iVisited) {
-        ThreadContext tc = state.getThreadContext();
+        ThreadContext tc = runtime.getCurrentContext();
 		String lastMethod = tc.getFrameLastFunc();
 		RubyModule lastClass = tc.getFrameLastClass();
 		if (lastMethod != null && lastClass != null
 				&& lastClass.getSuperClass().isMethodBound(lastMethod, false)) {
 			definition = "super";
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitCallNode(CallNode)
 	 */
 	public Instruction visitCallNode(CallNode iVisited) {
 		if (getDefinition(iVisited.getReceiverNode()) != null) {
 			try {
-                IRubyObject receiver = new EvaluationState(state.runtime, state.getSelf()).begin(iVisited.getReceiverNode());
+                IRubyObject receiver = EvaluationState.eval(runtime.getCurrentContext(), iVisited.getReceiverNode(), runtime.getCurrentContext().getFrameSelf());
 				RubyClass metaClass = receiver.getMetaClass();
 				ICallable method = metaClass.searchMethod(iVisited.getName());
 				Visibility visibility = method.getVisibility();
 
 				if (!visibility.isPrivate()
-						&& (!visibility.isProtected() || state.getSelf()
+						&& (!visibility.isProtected() || runtime.getCurrentContext().getFrameSelf()
 								.isKindOf(metaClass.getRealClass()))) {
 					if (metaClass.isMethodBound(iVisited.getName(), false)) {
 						definition = getArgumentDefinition(iVisited
 								.getArgsNode(), "method");
 						return null;
 					}
 				}
 			} catch (JumpException excptn) {
 			}
 		}
 		definition = null;
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitFCallNode(FCallNode)
 	 */
 	public Instruction visitFCallNode(FCallNode iVisited) {
-		if (state.getSelf().getMetaClass().isMethodBound(iVisited.getName(), false)) {
+		if (runtime.getCurrentContext().getFrameSelf().getMetaClass().isMethodBound(iVisited.getName(), false)) {
 			definition = getArgumentDefinition(iVisited.getArgsNode(), "method");
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitVCallNode(VCallNode)
 	 */
 	public Instruction visitVCallNode(VCallNode iVisited) {
-		if (state.getSelf().getMetaClass().isMethodBound(iVisited.getMethodName(), false)) {
+		if (runtime.getCurrentContext().getFrameSelf().getMetaClass().isMethodBound(iVisited.getMethodName(), false)) {
 			definition = "method";
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitMatch2Node(Match2Node)
 	 */
 	public Instruction visitMatch2Node(Match2Node iVisited) {
 		definition = "method";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitMatch3Node(Match3Node)
 	 */
 	public Instruction visitMatch3Node(Match3Node iVisited) {
 		definition = "method";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitFalseNode(FalseNode)
 	 */
 	public Instruction visitFalseNode(FalseNode iVisited) {
 		definition = "false";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitNilNode(NilNode)
 	 */
 	public Instruction visitNilNode(NilNode iVisited) {
 		definition = "nil";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitNullNode()
 	 */
 	public Instruction visitNullNode() {
 		definition = "expression";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitNode(state.getSelf()Node)
 	 */
 	public Instruction visitSelfNode(SelfNode iVisited) {
 		definition = "state.getSelf()";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitTrueNode(TrueNode)
 	 */
 	public Instruction visitTrueNode(TrueNode iVisited) {
 		definition = "true";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitYieldNode(YieldNode)
 	 */
 	public Instruction visitYieldNode(YieldNode iVisited) {
-		if (state.getThreadContext().isBlockGiven()) {
+		if (runtime.getCurrentContext().isBlockGiven()) {
 			definition = "yield";
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitClassVarAsgnNode(ClassVarAsgnNode)
 	 */
 	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitClassVarDeclNode(ClassVarDeclNode)
 	 */
 	public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitConstDeclNode(ConstDeclNode)
 	 */
 	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitDAsgnNode(DAsgnNode)
 	 */
 	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitGlobalAsgnNode(GlobalAsgnNode)
 	 */
 	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitLocalAsgnNode(LocalAsgnNode)
 	 */
 	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitMultipleAsgnNode(MultipleAsgnNode)
 	 */
 	public Instruction visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitOpAsgnNode(OpAsgnNode)
 	 */
 	public Instruction visitOpAsgnNode(OpAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitOpElementAsgnNode(OpElementAsgnNode)
 	 */
 	public Instruction visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
 		definition = "assignment";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitDVarNode(DVarNode)
 	 */
 	public Instruction visitDVarNode(DVarNode iVisited) {
 		definition = "local-variable(in-block)";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitLocalVarNode(LocalVarNode)
 	 */
 	public Instruction visitLocalVarNode(LocalVarNode iVisited) {
 		definition = "local-variable";
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitClassVarNode(ClassVarNode)
 	 */
 	public Instruction visitClassVarNode(ClassVarNode iVisited) {
-        ThreadContext tc = state.getThreadContext();
+        ThreadContext tc = runtime.getCurrentContext();
         
 		if (tc.getRubyClass() == null
-				&& state.getSelf().getMetaClass().isClassVarDefined(iVisited.getName())) {
+				&& runtime.getCurrentContext().getFrameSelf().getMetaClass().isClassVarDefined(iVisited.getName())) {
 			definition = "class_variable";
 		} else if (!tc.getRubyClass().isSingleton()
 				&& tc.getRubyClass().isClassVarDefined(
 						iVisited.getName())) {
 			definition = "class_variable";
 		} else {
 			RubyModule module = (RubyModule) tc.getRubyClass()
 					.getInstanceVariable("__attached__");
 
 			if (module != null && module.isClassVarDefined(iVisited.getName())) {
 				definition = "class_variable";
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitConstNode(ConstNode)
 	 */
 	public Instruction visitConstNode(ConstNode iVisited) {
-	    if (state.getThreadContext().getConstantDefined(iVisited.getName())) {
+	    if (runtime.getCurrentContext().getConstantDefined(iVisited.getName())) {
 	        definition = "constant";
 	    }
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitGlobalVarNode(GlobalVarNode)
 	 */
 	public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
-		if (state.runtime.getGlobalVariables().isDefined(iVisited.getName())) {
+		if (runtime.getGlobalVariables().isDefined(iVisited.getName())) {
 			definition = "global-variable";
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitInstVarNode(InstVarNode)
 	 */
 	public Instruction visitInstVarNode(InstVarNode iVisited) {
-		if (state.getSelf().getInstanceVariable(iVisited.getName()) != null) {
+		if (runtime.getCurrentContext().getFrameSelf().getInstanceVariable(iVisited.getName()) != null) {
 			definition = "instance-variable";
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitColon2Node(Colon2Node)
 	 */
 	public Instruction visitColon2Node(Colon2Node iVisited) {
 		try {
-            IRubyObject left = new EvaluationState(state.runtime, state.getSelf()).begin(iVisited.getLeftNode());
+            IRubyObject left = EvaluationState.eval(runtime.getCurrentContext(), iVisited.getLeftNode(), runtime.getCurrentContext().getFrameSelf());
 			if (left instanceof RubyModule) {
 				if (((RubyModule) left).getConstantAt(iVisited.getName()) != null) {
 					definition = "constant";
 				}
 			} else if (left.getMetaClass().isMethodBound(iVisited.getName(),
 					true)) {
 				definition = "method";
 			}
 		} catch (JumpException excptn) {
 		}
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitBackRefNode(BackRefNode)
 	 * 
 	 * @fixme Add test if back ref exists.
 	 */
 	public Instruction visitBackRefNode(BackRefNode iVisited) {
 		// if () {
 		definition = "$" + iVisited.getType();
 		// }
 		return null;
 	}
 
 	/**
 	 * @see AbstractVisitor#visitNthRefNode(NthRefNode)
 	 * 
 	 * @fixme Add test if nth ref exists.
 	 */
 	public Instruction visitNthRefNode(NthRefNode iVisited) {
 		// if () {
 		definition = "$" + iVisited.getMatchNumber();
 		// }
 		return null;
 	}
 }
\ No newline at end of file
diff --git a/src/org/jruby/evaluator/EvaluateVisitor.java b/src/org/jruby/evaluator/EvaluateVisitor.java
deleted file mode 100644
index 428cc0e178..0000000000
--- a/src/org/jruby/evaluator/EvaluateVisitor.java
+++ /dev/null
@@ -1,2927 +0,0 @@
-/***** BEGIN LICENSE BLOCK *****
- * Version: CPL 1.0/GPL 2.0/LGPL 2.1
- *
- * The contents of this file are subject to the Common Public
- * License Version 1.0 (the "License"); you may not use this file
- * except in compliance with the License. You may obtain a copy of
- * the License at http://www.eclipse.org/legal/cpl-v10.html
- *
- * Software distributed under the License is distributed on an "AS
- * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
- * implied. See the License for the specific language governing
- * rights and limitations under the License.
- *
- * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
- * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
- * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
- * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
- * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
- * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
- * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
- * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
- * 
- * Alternatively, the contents of this file may be used under the terms of
- * either of the GNU General Public License Version 2 or later (the "GPL"),
- * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
- * in which case the provisions of the GPL or the LGPL are applicable instead
- * of those above. If you wish to allow use of your version of this file only
- * under the terms of either the GPL or the LGPL, and not to allow others to
- * use your version of this file under the terms of the CPL, indicate your
- * decision by deleting the provisions above and replace them with the notice
- * and other provisions required by the GPL or the LGPL. If you do not delete
- * the provisions above, a recipient may use your version of this file under
- * the terms of any one of the CPL, the GPL or the LGPL.
- ***** END LICENSE BLOCK *****/
-package org.jruby.evaluator;
-
-import java.util.ArrayList;
-import java.util.Iterator;
-import java.util.List;
-import java.util.ListIterator;
-
-import org.jruby.IRuby;
-import org.jruby.MetaClass;
-import org.jruby.RubyArray;
-import org.jruby.RubyBignum;
-import org.jruby.RubyClass;
-import org.jruby.RubyFloat;
-import org.jruby.RubyHash;
-import org.jruby.RubyKernel;
-import org.jruby.RubyModule;
-import org.jruby.RubyProc;
-import org.jruby.RubyRange;
-import org.jruby.RubyRegexp;
-import org.jruby.RubyString;
-import org.jruby.ast.AliasNode;
-import org.jruby.ast.AndNode;
-import org.jruby.ast.ArgsCatNode;
-import org.jruby.ast.ArgsNode;
-import org.jruby.ast.ArrayNode;
-import org.jruby.ast.BackRefNode;
-import org.jruby.ast.BeginNode;
-import org.jruby.ast.BignumNode;
-import org.jruby.ast.BinaryOperatorNode;
-import org.jruby.ast.BlockArgNode;
-import org.jruby.ast.BlockNode;
-import org.jruby.ast.BlockPassNode;
-import org.jruby.ast.BreakNode;
-import org.jruby.ast.CallNode;
-import org.jruby.ast.CaseNode;
-import org.jruby.ast.ClassNode;
-import org.jruby.ast.ClassVarAsgnNode;
-import org.jruby.ast.ClassVarDeclNode;
-import org.jruby.ast.ClassVarNode;
-import org.jruby.ast.Colon2Node;
-import org.jruby.ast.Colon3Node;
-import org.jruby.ast.ConstDeclNode;
-import org.jruby.ast.ConstNode;
-import org.jruby.ast.DAsgnNode;
-import org.jruby.ast.DRegexpNode;
-import org.jruby.ast.DStrNode;
-import org.jruby.ast.DSymbolNode;
-import org.jruby.ast.DVarNode;
-import org.jruby.ast.DXStrNode;
-import org.jruby.ast.DefinedNode;
-import org.jruby.ast.DefnNode;
-import org.jruby.ast.DefsNode;
-import org.jruby.ast.DotNode;
-import org.jruby.ast.EnsureNode;
-import org.jruby.ast.EvStrNode;
-import org.jruby.ast.FCallNode;
-import org.jruby.ast.FalseNode;
-import org.jruby.ast.FixnumNode;
-import org.jruby.ast.FlipNode;
-import org.jruby.ast.FloatNode;
-import org.jruby.ast.ForNode;
-import org.jruby.ast.GlobalAsgnNode;
-import org.jruby.ast.GlobalVarNode;
-import org.jruby.ast.HashNode;
-import org.jruby.ast.IfNode;
-import org.jruby.ast.InstAsgnNode;
-import org.jruby.ast.InstVarNode;
-import org.jruby.ast.IterNode;
-import org.jruby.ast.ListNode;
-import org.jruby.ast.LocalAsgnNode;
-import org.jruby.ast.LocalVarNode;
-import org.jruby.ast.Match2Node;
-import org.jruby.ast.Match3Node;
-import org.jruby.ast.MatchNode;
-import org.jruby.ast.ModuleNode;
-import org.jruby.ast.MultipleAsgnNode;
-import org.jruby.ast.NewlineNode;
-import org.jruby.ast.NextNode;
-import org.jruby.ast.NilNode;
-import org.jruby.ast.Node;
-import org.jruby.ast.NotNode;
-import org.jruby.ast.NthRefNode;
-import org.jruby.ast.OpAsgnAndNode;
-import org.jruby.ast.OpAsgnNode;
-import org.jruby.ast.OpAsgnOrNode;
-import org.jruby.ast.OpElementAsgnNode;
-import org.jruby.ast.OptNNode;
-import org.jruby.ast.OrNode;
-import org.jruby.ast.PostExeNode;
-import org.jruby.ast.RedoNode;
-import org.jruby.ast.RegexpNode;
-import org.jruby.ast.RescueBodyNode;
-import org.jruby.ast.RescueNode;
-import org.jruby.ast.RetryNode;
-import org.jruby.ast.ReturnNode;
-import org.jruby.ast.SClassNode;
-import org.jruby.ast.SValueNode;
-import org.jruby.ast.ScopeNode;
-import org.jruby.ast.SelfNode;
-import org.jruby.ast.SplatNode;
-import org.jruby.ast.StrNode;
-import org.jruby.ast.SuperNode;
-import org.jruby.ast.SymbolNode;
-import org.jruby.ast.ToAryNode;
-import org.jruby.ast.TrueNode;
-import org.jruby.ast.UndefNode;
-import org.jruby.ast.UntilNode;
-import org.jruby.ast.VAliasNode;
-import org.jruby.ast.VCallNode;
-import org.jruby.ast.WhenNode;
-import org.jruby.ast.WhileNode;
-import org.jruby.ast.XStrNode;
-import org.jruby.ast.YieldNode;
-import org.jruby.ast.ZArrayNode;
-import org.jruby.ast.ZSuperNode;
-import org.jruby.ast.types.INameNode;
-import org.jruby.ast.util.ArgsUtil;
-import org.jruby.ast.visitor.NodeVisitor;
-import org.jruby.exceptions.JumpException;
-import org.jruby.internal.runtime.methods.DefaultMethod;
-import org.jruby.internal.runtime.methods.WrapperCallable;
-import org.jruby.lexer.yacc.ISourcePosition;
-import org.jruby.runtime.Block;
-import org.jruby.runtime.CallType;
-import org.jruby.runtime.ICallable;
-import org.jruby.runtime.ThreadContext;
-import org.jruby.runtime.Visibility;
-import org.jruby.runtime.builtin.IRubyObject;
-
-// TODO this visitor often leads to very deep stacks.  If it happens to be a
-// real problem, the trampoline method of tail call elimination could be used.
-/**
- *
- */
-public final class EvaluateVisitor implements NodeVisitor {
-	private static final EvaluateVisitor evaluator = new EvaluateVisitor();
-	
-    public static EvaluateVisitor getInstance() {
-        return evaluator;
-    }
-
-    /**
-     * Helper method.
-     *
-     * test if a trace function is avaiable.
-     *
-     */
-    private static boolean isTrace(EvaluationState state) {
-        return state.runtime.getTraceFunction() != null;
-    }
-
-    private static void callTraceFunction(EvaluationState state, String event, IRubyObject zelf) {
-        ThreadContext tc = state.getThreadContext();
-        String name = tc.getFrameLastFunc();
-        RubyModule type = tc.getFrameLastClass();
-        state.runtime.callTraceFunction(event, tc.getPosition(), zelf, name, type);
-    }
-    
-    // Collapsing further needs method calls
-    private static class AliasNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		AliasNode iVisited = (AliasNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            if (tc.getRubyClass() == null) {
-            	throw state.runtime.newTypeError("no class to make alias");
-            }
-
-            tc.getRubyClass().defineAlias(iVisited.getNewName(), iVisited.getOldName());
-            tc.getRubyClass().callMethod("method_added", state.runtime.newSymbol(iVisited.getNewName()));
-    	}
-    }
-    private static final AliasNodeVisitor aliasNodeVisitor = new AliasNodeVisitor();
-    
-    // And nodes are collapsed completely
-    private static class AndNodeImplVisitor implements Instruction {
-		public void execute(EvaluationState state, InstructionContext ctx) {
-			if (state.getResult().isTrue()) {
-				state.addNodeInstruction(((BinaryOperatorNode)ctx).getSecondNode());
-			}
-		}
-    }
-    private static final AndNodeImplVisitor andNodeImplVisitor = new AndNodeImplVisitor();
-    private static class AndNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BinaryOperatorNode iVisited = (BinaryOperatorNode)ctx;
-    		
-    		// add in reverse order
-    		state.addInstruction((Node)iVisited, andNodeImplVisitor);
-    		state.addNodeInstruction((Node)iVisited.getFirstNode());
-    	}
-    }
-    // used also for OpAsgnAndNode
-    private static final AndNodeVisitor andNodeVisitor = new AndNodeVisitor();
-    
-    // Used for pushing down the result stack
-    private static class AggregateResultInstruction implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            state.aggregateResult();
-        }
-    }
-    private static final AggregateResultInstruction aggregateResult = new AggregateResultInstruction();
-    
-    // Used for popping the result stack
-    private static class DeaggregateResultInstruction implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            state.deaggregateResult();
-        }
-    }
-    private static final DeaggregateResultInstruction deaggregateResult = new DeaggregateResultInstruction();
-
-    // Collapsed; use of stack to aggregate args could be improved (see FIXME in EvaluationState#aggregateResult) but it's not bad
-    private static class ArgsCatNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            IRubyObject secondArgs = splatValue(state, state.deaggregateResult());
-            IRubyObject args = state.getResult();
-            RubyArray list = args instanceof RubyArray ? (RubyArray) args :
-                state.runtime.newArray(args);
-            
-            state.setResult(list.concat(secondArgs)); 
-        }
-    }
-    private static final ArgsCatNodeVisitor2 argsCatNodeVisitor2 = new ArgsCatNodeVisitor2();
-    private static class ArgsCatNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            ArgsCatNode iVisited = (ArgsCatNode)ctx;
-            
-            state.addInstruction(iVisited, argsCatNodeVisitor2);
-            state.addNodeInstruction(iVisited.getSecondNode());
-            state.addInstruction(iVisited, aggregateResult);
-            state.addNodeInstruction(iVisited.getFirstNode());
-        }
-    }
-    private static final ArgsCatNodeVisitor argsCatNodeVisitor = new ArgsCatNodeVisitor();
-    
-    // Collapsed; use of stack to aggregate Array elements could be optimized a bit
-    private static class ArrayBuilder implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            ListNode iVisited = (ListNode)ctx;
-            int size = iVisited.size();
-            ArrayList list = new ArrayList(size);
-            
-            // backwards on purpose
-            for (int i = 0; i < size - 1; i++) {
-                list.add(0, state.deaggregateResult());
-            }
-            
-            if (size > 0) {
-                // normal add for the last one
-                list.add(0, state.getResult());
-            }
-            
-            state.setResult(state.runtime.newArray(list));
-        }
-    }
-    private static final ArrayBuilder arrayBuilder = new ArrayBuilder();
-    private static class ArrayNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ArrayNode iVisited = (ArrayNode)ctx;
-            
-            state.addInstruction(iVisited, arrayBuilder);
-            
-            // backwards so nodes are eval'ed forwards
-            for (ListIterator iterator = iVisited.reverseIterator(); iterator.hasPrevious();) {
-                Node node = (Node)iterator.previous();
-                
-                state.addNodeInstruction(node);
-                
-                if (iterator.hasPrevious()) {
-                    // more nodes coming; aggregate. Last node is not aggregated
-                    state.addInstruction(iVisited, aggregateResult);
-                }
-            }
-    	}
-    }
-    private static final ArrayNodeVisitor arrayNodeVisitor = new ArrayNodeVisitor();
-    
-    // Collapsing requires a way to break out method calls
-    // CON20060104: This actually may not be a big deal, since the RubyRegexp functions are lightweight and run inline
-    private static class BackRefNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BackRefNode iVisited = (BackRefNode)ctx;
-            IRubyObject backref = state.getThreadContext().getBackref();
-            switch (iVisited.getType()) {
-    	        case '~' :
-    	            state.setResult(backref);
-    	            break;
-            	case '&' :
-                    state.setResult(RubyRegexp.last_match(backref));
-                    break;
-                case '`' :
-                    state.setResult(RubyRegexp.match_pre(backref));
-                    break;
-                case '\'' :
-                    state.setResult(RubyRegexp.match_post(backref));
-                    break;
-                case '+' :
-                    state.setResult(RubyRegexp.match_last(backref));
-                    break;
-            }
-    	}
-    }
-    private static final BackRefNodeVisitor backRefNodeVisitor = new BackRefNodeVisitor();
-    
-    // Collapsed
-    private static class BeginNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BeginNode iVisited = (BeginNode)ctx;
-    		state.addNodeInstruction(iVisited.getBodyNode());
-    	}
-    }
-    private static final BeginNodeVisitor beginNodeVisitor = new BeginNodeVisitor();
-
-    // Collapsed
-    private static class BlockNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BlockNode iVisited = (BlockNode)ctx;
-            for (ListIterator iter = iVisited.reverseIterator(); iter.hasPrevious(); ) {
-                state.addNodeInstruction((Node)iter.previous());
-            }
-    	}
-    }
-    private static final BlockNodeVisitor blockNodeVisitor = new BlockNodeVisitor();
-    
-    // Big; collapsing will require multiple visitors
-    private static class BlockPassNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BlockPassNode iVisited = (BlockPassNode)ctx;
-            IRubyObject proc = state.begin(iVisited.getBodyNode());
-            ThreadContext tc = state.getThreadContext();
-
-            if (proc.isNil()) {
-                tc.setNoBlock();
-                try {
-                    state.begin(iVisited.getIterNode());
-                    return;
-                } finally {
-                    tc.clearNoBlock();
-                }
-            }
-            
-            // If not already a proc then we should try and make it one.
-            if (!(proc instanceof RubyProc)) {
-            	proc = proc.convertToType("Proc", "to_proc", false);
-            	
-            	if (!(proc instanceof RubyProc)) {
-                    throw state.runtime.newTypeError("wrong argument type " + proc.getMetaClass().getName() + " (expected Proc)");
-            	}
-            }
-
-            // TODO: Add safety check for taintedness
-            
-            Block block = (Block) tc.getCurrentBlock();
-            if (block != null) {
-                IRubyObject blockObject = block.getBlockObject();
-                // The current block is already associated with the proc.  No need to create new
-                // block for it.  Just eval!
-                if (blockObject != null && blockObject == proc) {
-            	    try {
-                        tc.setBlockAvailable();
-                	    state.begin(iVisited.getIterNode());
-                	    return;
-            	    } finally {
-                        tc.clearBlockAvailable();
-            	    }
-                }
-            }
-
-            tc.preBlockPassEval(((RubyProc)proc).getBlock());
-            
-            try {
-                state.begin(iVisited.getIterNode());
-            } finally {
-                tc.postBlockPassEval();
-            }
-    	}
-    }
-    private static final BlockPassNodeVisitor blockPassNodeVisitor = new BlockPassNodeVisitor();
-    
-    // Collapsed using new interpreter events
-    private static class BreakThrower implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            // throw exception for code that continues to need it
-            JumpException je = new JumpException(JumpException.JumpType.BreakJump);
-            
-            je.setPrimaryData(state.getResult());
-            je.setSecondaryData(ctx);
-            
-            state.setCurrentException(je);
-            
-            throw je;
-        }
-    }
-    private static final BreakThrower breakThrower = new BreakThrower();
-    private static class BreakNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BreakNode iVisited = (BreakNode)ctx;
-
-            state.setResult(state.runtime.getNil());
-            state.addInstruction(iVisited, breakThrower);
-            if (iVisited.getValueNode() != null) {
-                state.addNodeInstruction(iVisited.getValueNode());
-            }
-    	}
-    }
-    private static final BreakNodeVisitor breakNodeVisitor = new BreakNodeVisitor();
-    
-    // Collapsed, other than exception handling
-    private static class ConstDeclNodeVisitor2 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ConstDeclNode iVisited = (ConstDeclNode)ctx;
-            IRubyObject module;
-            IRubyObject value;
-
-    		if (iVisited.getPathNode() != null) {
-   			 	module = state.deaggregateResult();
-   			 	value = state.getResult();
-    		} else { 
-                ThreadContext tc = state.getThreadContext();
-                
-                // FIXME: why do we check RubyClass and then use CRef?
-                if (tc.getRubyClass() == null) {
-                    // TODO: wire into new exception handling mechanism
-                    throw state.runtime.newTypeError("no class/module to define constant");
-                }
-                module = (RubyModule) tc.peekCRef().getValue();
-                value = state.getResult();
-            } 
-
-            // FIXME: shouldn't we use the result of this set in setResult?
-    		((RubyModule) module).setConstant(iVisited.getName(), value);
-    		state.setResult(value);
-    	}
-    }
-    private static final ConstDeclNodeVisitor2 constDeclNodeVisitor2 = new ConstDeclNodeVisitor2();
-    private static class ConstDeclNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            ConstDeclNode iVisited = (ConstDeclNode)ctx;
-            
-            state.clearResult();
-            state.addInstruction(ctx, constDeclNodeVisitor2);
-            if (iVisited.getPathNode() != null) {
-                state.addNodeInstruction(iVisited.getPathNode());
-                state.addInstruction(iVisited, aggregateResult);
-            } 
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final ConstDeclNodeVisitor constDeclNodeVisitor = new ConstDeclNodeVisitor();
-    
-    // Collapsed
-    private static class ClassVarAsgnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ClassVarAsgnNode iVisited = (ClassVarAsgnNode)ctx;
-    		RubyModule rubyClass = (RubyModule) state.getThreadContext().peekCRef().getValue();
-    		
-            if (rubyClass == null) {
-            	rubyClass = state.getSelf().getMetaClass();
-            } else if (rubyClass.isSingleton()) {
-                rubyClass = (RubyModule) rubyClass.getInstanceVariable("__attached__");
-            }
-            
-            // FIXME shouldn't we use the return value for setResult?
-        	rubyClass.setClassVar(iVisited.getName(), state.getResult());
-    	}
-    }
-    private static final ClassVarAsgnNodeVisitor1 classVarAsgnNodeVisitor1 = new ClassVarAsgnNodeVisitor1();
-    private static class ClassVarAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ClassVarAsgnNode iVisited = (ClassVarAsgnNode)ctx;
-    		state.addInstruction(ctx, classVarAsgnNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final ClassVarAsgnNodeVisitor classVarAsgnNodeVisitor = new ClassVarAsgnNodeVisitor();
-    
-    // Collapsed
-    private static class ClassVarDeclNodeVisitor2 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ClassVarDeclNode iVisited = (ClassVarDeclNode)ctx;
-            ((RubyModule)state.getThreadContext().peekCRef().getValue()).setClassVar(iVisited.getName(), state.getResult());
-    	}
-    }
-    private static final ClassVarDeclNodeVisitor2 classVarDeclNodeVisitor2 = new ClassVarDeclNodeVisitor2();
-    private static class ClassVarDeclNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		state.clearResult();
-            ClassVarDeclNode iVisited = (ClassVarDeclNode)ctx;
-            
-            // FIXME: shouldn't we use cref here?
-            if (state.getThreadContext().getRubyClass() == null) {
-                throw state.runtime.newTypeError("no class/module to define class variable");
-            }
-            state.addInstruction(ctx, classVarDeclNodeVisitor2);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final ClassVarDeclNodeVisitor classVarDeclNodeVisitor = new ClassVarDeclNodeVisitor();
-    
-    // Not collapsed, but maybe nothing to be done?
-    private static class ClassVarNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ClassVarNode iVisited = (ClassVarNode)ctx;
-        	RubyModule rubyClass = state.getThreadContext().getRubyClass();
-        	
-            if (rubyClass == null) {
-                state.setResult(state.getSelf().getMetaClass().getClassVar(iVisited.getName()));
-            } else if (! rubyClass.isSingleton()) {
-            	state.setResult(rubyClass.getClassVar(iVisited.getName()));
-            } else {
-                RubyModule module = (RubyModule) rubyClass.getInstanceVariable("__attached__");
-                	
-                if (module != null) {
-                    state.setResult(module.getClassVar(iVisited.getName()));
-                }
-            }
-    	}
-    }
-    private static final ClassVarNodeVisitor classVarNodeVisitor = new ClassVarNodeVisitor();
-    
-    // Not collapsed; probably will depend on exception handling
-    private static class CallNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		CallNode iVisited = (CallNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            tc.beginCallArgs();
-            IRubyObject receiver = null;
-            IRubyObject[] args = null;
-            try {
-                receiver = state.begin(iVisited.getReceiverNode());
-                args = setupArgs(state, state.runtime, tc, iVisited.getArgsNode());
-            } finally {
-            	tc.endCallArgs();
-            }
-            assert receiver.getMetaClass() != null : receiver.getClass().getName();
-            // If reciever is self then we do the call the same way as vcall
-            CallType callType = (receiver == state.getSelf() ? CallType.VARIABLE : CallType.NORMAL);
-            
-            state.setResult(receiver.callMethod(iVisited.getName(), args, callType));
-        }
-    }
-    private static final CallNodeVisitor callNodeVisitor = new CallNodeVisitor();
-    
-    // Not collapsed; it's a big'un, will take some work
-    private static class CaseNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		CaseNode iVisited = (CaseNode)ctx;
-            IRubyObject expression = null;
-            if (iVisited.getCaseNode() != null) {
-                expression = state.begin(iVisited.getCaseNode());
-            }
-
-            state.getThreadContext().pollThreadEvents();
-            
-            Node firstWhenNode = iVisited.getFirstWhenNode();
-            while (firstWhenNode != null) {
-                if (!(firstWhenNode instanceof WhenNode)) {
-                    state.begin(firstWhenNode);
-                    return;
-                }
-
-                WhenNode whenNode = (WhenNode) firstWhenNode;
-
-                if (whenNode.getExpressionNodes() instanceof ArrayNode) {
-    		        for (Iterator iter = ((ArrayNode) whenNode.getExpressionNodes()).iterator(); iter.hasNext(); ) {
-    		            Node tag = (Node) iter.next();
-
-                        state.getThreadContext().setPosition(tag.getPosition());
-                        if (isTrace(state)) {
-                            callTraceFunction(state, "line", state.getSelf());
-                        }
-
-                        // Ruby grammar has nested whens in a case body because of
-                        // productions case_body and when_args.
-                	    if (tag instanceof WhenNode) {
-                		    RubyArray expressions = (RubyArray) state.begin(((WhenNode) tag).getExpressionNodes());
-                        
-                            for (int j = 0; j < expressions.getLength(); j++) {
-                        	    IRubyObject condition = expressions.entry(j);
-                        	
-                                if ((expression != null && 
-                            	    condition.callMethod("===", expression).isTrue()) || 
-    							    (expression == null && condition.isTrue())) {
-                                     state.begin(((WhenNode) firstWhenNode).getBodyNode());
-                                     return;
-                                }
-                            }
-                            continue;
-                	    }
-
-                        state.begin(tag);
-                        
-                        if ((expression != null && state.getResult().callMethod("===", expression).isTrue()) ||
-                            (expression == null && state.getResult().isTrue())) {
-                            state.begin(whenNode.getBodyNode());
-                            return;
-                        }
-                    }
-    	        } else {
-                    state.begin(whenNode.getExpressionNodes());
-
-                    if ((expression != null && state.getResult().callMethod("===", expression).isTrue())
-                        || (expression == null && state.getResult().isTrue())) {
-                        state.begin(((WhenNode) firstWhenNode).getBodyNode());
-                        return;
-                    }
-                }
-
-                state.getThreadContext().pollThreadEvents();
-                
-                firstWhenNode = whenNode.getNextCase();
-            }
-            
-            state.setResult(state.runtime.getNil());
-    	}
-    }
-    private static final CaseNodeVisitor caseNodeVisitor = new CaseNodeVisitor();
-    
-    // Not collapsed; another big one
-    private static class ClassNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ClassNode iVisited = (ClassNode)ctx;
-            RubyClass superClass = getSuperClassFromNode(state, iVisited.getSuperNode());
-            Node classNameNode = iVisited.getCPath();
-            String name = ((INameNode) classNameNode).getName();
-            RubyModule enclosingClass = getEnclosingModule(state, classNameNode);
-            RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
-            ThreadContext tc = state.getThreadContext();
-
-            if (tc.getWrapper() != null) {
-                rubyClass.extendObject(tc.getWrapper());
-                rubyClass.includeModule(tc.getWrapper());
-            }
-            evalClassDefinitionBody(state, iVisited.getBodyNode(), rubyClass);
-    	}
-    	
-    	private RubyClass getSuperClassFromNode(EvaluationState state, Node superNode) {
-            if (superNode == null) {
-                return null;
-            }
-            RubyClass superClazz;
-            try {
-                superClazz = (RubyClass) state.begin(superNode);
-            } catch (Exception e) {
-                if (superNode instanceof INameNode) {
-                    String name = ((INameNode) superNode).getName();
-                    throw state.runtime.newTypeError("undefined superclass '" + name + "'");
-                }
-    			throw state.runtime.newTypeError("superclass undefined");
-            }
-            if (superClazz instanceof MetaClass) {
-                throw state.runtime.newTypeError("can't make subclass of virtual class");
-            }
-            return superClazz;
-        }
-    }
-    private static final ClassNodeVisitor classNodeVisitor = new ClassNodeVisitor();
-    
-    // Collapsed, other than a method call
-    private static class Colon2NodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		Colon2Node iVisited = (Colon2Node)ctx;
-            if (state.getResult() instanceof RubyModule) {
-                state.setResult(((RubyModule) state.getResult()).getConstantFrom(iVisited.getName()));
-            } else {
-                state.setResult(state.getResult().callMethod(iVisited.getName()));
-            }
-    	}
-    }
-    private static final Colon2NodeVisitor1 colon2NodeVisitor1 = new Colon2NodeVisitor1();
-    private static class Colon2NodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		Colon2Node iVisited = (Colon2Node)ctx;
-            Node leftNode = iVisited.getLeftNode();
-
-            // TODO: Made this more colon3 friendly because of cpath production
-            // rule in grammar (it is convenient to think of them as the same thing
-            // at a grammar level even though evaluation is).
-            if (leftNode == null) {
-                state.setResult(state.runtime.getObject().getConstantFrom(iVisited.getName()));
-            } else {
-            	state.clearResult();
-            	state.addInstruction(ctx, colon2NodeVisitor1);
-                state.addNodeInstruction(iVisited.getLeftNode());
-            }
-    	}
-    }
-    private static final Colon2NodeVisitor colon2NodeVisitor = new Colon2NodeVisitor();
-
-    // No collapsing to do (depending on getConstant())
-    private static class Colon3NodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		Colon3Node iVisited = (Colon3Node)ctx;
-            state.setResult(state.runtime.getObject().getConstantFrom(iVisited.getName()));
-    	}
-    }
-    private static final Colon3NodeVisitor colon3NodeVisitor = new Colon3NodeVisitor();
-    
-    // No collapsing to do (depending on getConstant())
-    private static class ConstNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ConstNode iVisited = (ConstNode)ctx;
-            state.setResult(state.getThreadContext().getConstant(iVisited.getName()));
-    	}
-    }
-    private static final ConstNodeVisitor constNodeVisitor = new ConstNodeVisitor();
-    
-    // Collapsed
-    private static class DAsgnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DAsgnNode iVisited = (DAsgnNode)ctx;
-            state.getThreadContext().getCurrentDynamicVars().set(iVisited.getName(), state.getResult());
-    	}
-    }
-    private static final DAsgnNodeVisitor1 dAsgnNodeVisitor1 = new DAsgnNodeVisitor1();
-    private static class DAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DAsgnNode iVisited = (DAsgnNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, dAsgnNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final DAsgnNodeVisitor dAsgnNodeVisitor = new DAsgnNodeVisitor();
-    
-    // Collapsed; use of stack to aggregate strings could be optimized
-    private static class DRegexpNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DRegexpNode iVisited = (DRegexpNode)ctx;
-            
-            // FIXME: oughta just stay as RubyString, rather than toString
-            state.setResult(RubyRegexp.newRegexp(state.runtime, state.getResult().toString(), iVisited.getOptions(), null));
-        }
-    }
-    private static final DRegexpNodeVisitor2 dRegexpNodeVisitor2 = new DRegexpNodeVisitor2();
-    private static class DRegexpNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DRegexpNode iVisited = (DRegexpNode)ctx;
-
-            state.addInstruction(iVisited, dRegexpNodeVisitor2);
-            state.addInstruction(iVisited, dStrStringBuilder);
-            
-            for (ListIterator iterator = iVisited.reverseIterator(); iterator.hasPrevious();) {
-                Node iterNode = (Node) iterator.previous();
-                state.addNodeInstruction(iterNode);
-                
-                // aggregate if there's more nodes coming
-                if (iterator.hasPrevious()) {
-                    state.addInstruction(iVisited, aggregateResult);
-                }
-            }
-        }
-    }
-    private static final DRegexpNodeVisitor dRegexpNodeVisitor = new DRegexpNodeVisitor();
-    
-    // Collapsed using result stack
-    private static class DStrStringBuilder implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            ListNode iVisited = (ListNode)ctx;
-            int size = iVisited.size();
-            String[] strArray = new String[size];
-            StringBuffer sb = new StringBuffer();
-            
-            // has to be done backwards, which kinda sucks
-            for (int i = 0; i < size - 1; i++) {
-                strArray[i] = state.deaggregateResult().toString();
-            }
-            
-            if (size > 0) {
-                // normal add for the last one
-                strArray[size - 1] = state.getResult().toString();
-            }
-            
-            for (int i = size - 1; i >= 0; i--) {
-                sb.append(strArray[i]);
-            }
-            
-            state.setResult(state.runtime.newString(sb.toString()));
-        }
-    }
-    private static final DStrStringBuilder dStrStringBuilder = new DStrStringBuilder();
-    private static class DStrNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DStrNode iVisited = (DStrNode)ctx;
-            
-            state.addInstruction(iVisited, dStrStringBuilder);
-
-            for (ListIterator iterator = iVisited.reverseIterator(); iterator.hasPrevious();) {
-                Node iterNode = (Node) iterator.previous();
-                
-                // FIXME: skipping null node...but why would we find null nodes in here?!
-                if (iterNode == null) continue;
-                
-                state.addNodeInstruction(iterNode);
-
-                // aggregate if there's more nodes coming
-                if (iterator.hasPrevious()) {
-                    state.addInstruction(iVisited, aggregateResult);
-                }
-            }
-        }
-    }
-    private static final DStrNodeVisitor dStrNodeVisitor = new DStrNodeVisitor();
-    
-    // Collapsed using result stack
-    private static class DSymbolNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            // FIXME: oughta just stay RubyString rather than toString()
-            state.setResult(state.runtime.newSymbol(state.getResult().toString()));
-        }
-    }
-    private static final DSymbolNodeVisitor2 dSymbolNodeVisitor2 = new DSymbolNodeVisitor2();
-    private static class DSymbolNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DSymbolNode iVisited = (DSymbolNode)ctx;
-            
-            state.addInstruction(iVisited, dSymbolNodeVisitor2);
-            state.addInstruction(iVisited.getNode(), dStrStringBuilder);
-
-            for (ListIterator iterator = iVisited.getNode().reverseIterator(); 
-                iterator.hasPrevious();) {
-                Node iterNode = (Node) iterator.previous();
-                
-                state.addNodeInstruction(iterNode);
-
-                // aggregate if there's more nodes coming
-                if (iterator.hasPrevious()) {
-                    state.addInstruction(iVisited, aggregateResult);
-                }
-            }
-        }
-    }
-    private static final DSymbolNodeVisitor dSymbolNodeVisitor = new DSymbolNodeVisitor();
-
-    // Done; nothing to do
-    private static class DVarNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DVarNode iVisited = (DVarNode)ctx;
-            state.setResult(state.getThreadContext().getDynamicValue(iVisited.getName()));
-    	}
-    }
-    private static final DVarNodeVisitor dVarNodeVisitor = new DVarNodeVisitor();
-    
-    // Collapsed using result stack, other than a method call
-    private static class DXStrNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.getSelf().callMethod("`", state.getResult()));
-        }
-    }
-    private static final DXStrNodeVisitor2 dXStrNodeVisitor2 = new DXStrNodeVisitor2();
-    private static class DXStrNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DXStrNode iVisited = (DXStrNode)ctx;
-            
-            // Reuse string builder instruction for DStrNode
-            state.addInstruction(iVisited, dXStrNodeVisitor2);
-            state.addInstruction(iVisited, dStrStringBuilder);
-
-            for (ListIterator iterator = iVisited.reverseIterator(); iterator.hasPrevious();) {
-                Node iterNode = (Node) iterator.previous();
-                
-                state.addNodeInstruction(iterNode);
-                
-                if (iterator.hasPrevious()) {
-                    state.addInstruction(iVisited, aggregateResult);
-                }
-            }
-        }
-    }
-    private static final DXStrNodeVisitor dXStrNodeVisitor = new DXStrNodeVisitor();
-    
-    // Not collapsed; calls out to DefinedVisitor
-    private static class DefinedNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DefinedNode iVisited = (DefinedNode)ctx;
-            String def = new DefinedVisitor(state).getDefinition(iVisited.getExpressionNode());
-            if (def != null) {
-                state.setResult(state.runtime.newString(def));
-            } else {
-                state.setResult(state.runtime.getNil());
-            }
-    	}
-    }
-    private static final DefinedNodeVisitor definedNodeVisitor = new DefinedNodeVisitor();
-    
-    // Not collapsed; big
-    private static class DefnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DefnNode iVisited = (DefnNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            RubyModule containingClass = tc.getRubyClass();
-            
-            if (containingClass == null) {
-                throw state.runtime.newTypeError("No class to add method.");
-            }
-
-            String name = iVisited.getName();
-            if (containingClass == state.runtime.getObject() && name.equals("initialize")) {
-                state.runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
-            }
-
-            Visibility visibility = tc.getCurrentVisibility();
-            if (name.equals("initialize") || visibility.isModuleFunction()) {
-                visibility = Visibility.PRIVATE;
-            }
-
-            DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getBodyNode(),
-                                                        (ArgsNode) iVisited.getArgsNode(),
-                                                        visibility,
-                                                        tc.peekCRef());
-            
-            iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
-            
-            containingClass.addMethod(name, newMethod);
-
-            if (tc.getCurrentVisibility().isModuleFunction()) {
-                containingClass.getSingletonClass().addMethod(name, new WrapperCallable(containingClass.getSingletonClass(), newMethod, Visibility.PUBLIC));
-                containingClass.callMethod("singleton_method_added", state.runtime.newSymbol(name));
-            }
-
-    		// 'class << state.self' and 'class << obj' uses defn as opposed to defs
-            if (containingClass.isSingleton()) {
-    			((MetaClass)containingClass).getAttachedObject().callMethod("singleton_method_added", state.runtime.newSymbol(iVisited.getName()));
-            } else {
-            	containingClass.callMethod("method_added", state.runtime.newSymbol(name));
-            }
-    	}
-    }
-    private static final DefnNodeVisitor defnNodeVisitor = new DefnNodeVisitor();
-
-    // Not collapsed; big
-    private static class DefsNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		DefsNode iVisited = (DefsNode)ctx;
-            IRubyObject receiver = state.begin(iVisited.getReceiverNode());
-
-            if (state.runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
-                throw state.runtime.newSecurityError("Insecure; can't define singleton method.");
-            }
-            if (receiver.isFrozen()) {
-                throw state.runtime.newFrozenError("object");
-            }
-            if (! receiver.singletonMethodsAllowed()) {
-                throw state.runtime.newTypeError("can't define singleton method \"" +
-                                    iVisited.getName() +
-                                    "\" for " +
-                                    receiver.getType());
-            }
-
-            RubyClass rubyClass = receiver.getSingletonClass();
-
-            if (state.runtime.getSafeLevel() >= 4) {
-                ICallable method = (ICallable) rubyClass.getMethods().get(iVisited.getName());
-                if (method != null) {
-                    throw state.runtime.newSecurityError("Redefining method prohibited.");
-                }
-            }
-
-            DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getBodyNode(),
-                                                        (ArgsNode) iVisited.getArgsNode(),
-                                                        Visibility.PUBLIC,
-    													state.getThreadContext().peekCRef());
-
-            iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
-
-            rubyClass.addMethod(iVisited.getName(), newMethod);
-            receiver.callMethod("singleton_method_added", state.runtime.newSymbol(iVisited.getName()));
-
-            state.clearResult();
-    	}
-    }
-    private static final DefsNodeVisitor defsNodeVisitor = new DefsNodeVisitor();
-    
-    // Collapsed using result stack
-    private static class DotNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DotNode iVisited = (DotNode)ctx;
-            IRubyObject end = state.deaggregateResult();
-            state.setResult(RubyRange.newRange(state.runtime, state.getResult(), end, iVisited.isExclusive()));
-        }
-    }
-    private static final DotNodeVisitor2 dotNodeVisitor2 = new DotNodeVisitor2();
-    private static class DotNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            DotNode iVisited = (DotNode)ctx;
-            
-            state.addInstruction(iVisited, dotNodeVisitor2);
-
-            state.addNodeInstruction(iVisited.getEndNode());
-            state.addInstruction(iVisited, aggregateResult);
-            state.addNodeInstruction(iVisited.getBeginNode());
-        }
-    }
-    private static final DotNodeVisitor dotNodeVisitor = new DotNodeVisitor();
-    
-    // Collapsed
-    private static class Ensurer implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            state.addInstruction(ctx, deaggregateResult);
-            state.addNodeInstruction(ctx);
-            state.addInstruction(ctx, aggregateResult);
-        }
-    }
-    private static final Ensurer ensurer = new Ensurer();
-    private static class EnsureNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		EnsureNode iVisited = (EnsureNode)ctx;
-
-            if (iVisited.getEnsureNode() != null) {
-                state.addEnsuredInstruction(iVisited.getEnsureNode(), ensurer);
-            }
-            
-            state.addNodeInstruction(iVisited.getBodyNode());
-    	}
-    }
-    private static final EnsureNodeVisitor ensureNodeVisitor = new EnsureNodeVisitor();
-    
-    // Collapsed
-    private static class EvStrNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		EvStrNode iVisited = (EvStrNode)ctx;
-    		state.clearResult();
-    		state.addNodeInstruction(iVisited.getBody());
-    	}
-    }
-    private static final EvStrNodeVisitor evStrNodeVisitor = new EvStrNodeVisitor();
-    
-    // Not collapsed; function call and needs exception handling
-    private static class FCallNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		FCallNode iVisited = (FCallNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            tc.beginCallArgs();
-            IRubyObject[] args = null;
-            try {
-                args = setupArgs(state, state.runtime, tc, iVisited.getArgsNode());
-            } finally {
-            	tc.endCallArgs();
-            }
-
-            state.setResult(state.getSelf().callMethod(iVisited.getName(), args, CallType.FUNCTIONAL));
-    	}
-    }
-    private static final FCallNodeVisitor fCallNodeVisitor = new FCallNodeVisitor();
-    
-    // Nothing to do
-    private static class FalseNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.runtime.getFalse());
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final FalseNodeVisitor falseNodeVisitor = new FalseNodeVisitor();
-    
-    // Not collapsed; I do not understand this. It's pretty heinous.
-    private static class FlipNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		FlipNode iVisited = (FlipNode)ctx;
-            ThreadContext tc = state.runtime.getCurrentContext();
-            if (iVisited.isExclusive()) {
-                if (! tc.getFrameScope().getValue(iVisited.getCount()).isTrue()) {
-                    //Benoit: I don't understand why the state.result is inversed
-                    state.setResult(state.begin(iVisited.getBeginNode()).isTrue() ? state.runtime.getFalse() : state.runtime.getTrue());
-                    tc.getFrameScope().setValue(iVisited.getCount(), state.getResult());
-                } else {
-                    if (state.begin(iVisited.getEndNode()).isTrue()) {
-                        tc.getFrameScope().setValue(iVisited.getCount(), state.runtime.getFalse());
-                    }
-                    state.setResult(state.runtime.getTrue());
-                }
-            } else {
-                if (! tc.getFrameScope().getValue(iVisited.getCount()).isTrue()) {
-                    if (state.begin(iVisited.getBeginNode()).isTrue()) {
-                        //Benoit: I don't understand why the state.result is inversed
-                        tc.getFrameScope().setValue(iVisited.getCount(), state.begin(iVisited.getEndNode()).isTrue() ? state.runtime.getFalse() : state.runtime.getTrue());
-                        state.setResult(state.runtime.getTrue());
-                    } else {
-                        state.setResult(state.runtime.getFalse());
-                    }
-                } else {
-                    if (state.begin(iVisited.getEndNode()).isTrue()) {
-                        tc.getFrameScope().setValue(iVisited.getCount(), state.runtime.getFalse());
-                    }
-                    state.setResult(state.runtime.getTrue());
-                }
-            }
-    	}
-    }
-    private static final FlipNodeVisitor flipNodeVisitor = new FlipNodeVisitor();
-    
-    // Not collapsed; big and use of flow-control exceptions
-    private static class ForNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ForNode iVisited = (ForNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            tc.preForLoopEval(Block.createBlock(iVisited.getVarNode(), iVisited.getCallable(), state.getSelf()));
-        	
-            try {
-                while (true) {
-                    try {
-                        ISourcePosition position = tc.getPosition();
-                        tc.beginCallArgs();
-
-                        IRubyObject recv = null;
-                        try {
-                            recv = state.begin(iVisited.getIterNode());
-                        } finally {
-                            tc.setPosition(position);
-                            tc.endCallArgs();
-                        }
-                        
-                        state.setResult(recv.callMethod("each", IRubyObject.NULL_ARRAY, CallType.NORMAL));
-                        return;
-                    } catch (JumpException je) {
-                    	if (je.getJumpType() == JumpException.JumpType.RetryJump) {
-                    		// do nothing, allow loop to retry
-                    	} else {
-                            state.setCurrentException(je);
-                    		throw je;
-                    	}
-                    }
-                }
-            } catch (JumpException je) {
-            	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
-	                IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
-	                
-	                state.setResult(breakValue == null ? state.runtime.getNil() : breakValue);
-            	} else {
-                    state.setCurrentException(je);
-            		throw je;
-            	}
-            } finally {
-                tc.postForLoopEval();
-            }
-    	}
-    }
-    private static final ForNodeVisitor forNodeVisitor = new ForNodeVisitor();
-    
-    // Collapsed
-    private static class GlobalAsgnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		GlobalAsgnNode iVisited = (GlobalAsgnNode)ctx;
-            state.runtime.getGlobalVariables().set(iVisited.getName(), state.getResult());
-    	}
-    }
-    private static final GlobalAsgnNodeVisitor1 globalAsgnNodeVisitor1 = new GlobalAsgnNodeVisitor1();
-    private static class GlobalAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		GlobalAsgnNode iVisited = (GlobalAsgnNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, globalAsgnNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final GlobalAsgnNodeVisitor globalAsgnNodeVisitor = new GlobalAsgnNodeVisitor();
-    
-    // Nothing to do
-    private static class GlobalVarNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		GlobalVarNode iVisited = (GlobalVarNode)ctx;
-            state.setResult(state.runtime.getGlobalVariables().get(iVisited.getName()));
-    	}
-    }
-    private static final GlobalVarNodeVisitor globalVarNodeVisitor = new GlobalVarNodeVisitor();
-    
-    // Collapsed using result stack
-    private static class HashNodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            HashNode iVisited = (HashNode)ctx;
-            RubyHash hash = RubyHash.newHash(state.runtime);
-
-            if (iVisited.getListNode() != null) {
-                int size = iVisited.getListNode().size();
-                
-                for (int i = 0; i < (size / 2) - 1; i++) {
-                    IRubyObject value = state.deaggregateResult();
-                    IRubyObject key = state.deaggregateResult();
-                    hash.aset(key, value);
-                }
-                
-                if (size > 0) {
-                    IRubyObject value = state.deaggregateResult();
-                    IRubyObject key = state.getResult();
-                    hash.aset(key, value);
-                }
-            }
-            state.setResult(hash);
-        }
-    }
-    private static final HashNodeVisitor2 hashNodeVisitor2 = new HashNodeVisitor2();
-    private static class HashNodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            HashNode iVisited = (HashNode)ctx;
-            
-            state.addInstruction(iVisited, hashNodeVisitor2);
-
-            if (iVisited.getListNode() != null) {
-                for (ListIterator iterator = iVisited.getListNode().reverseIterator(); iterator.hasPrevious();) {
-                    // insert all nodes in sequence, hash them in the final instruction
-                    // KEY
-                    state.addNodeInstruction((Node) iterator.previous());
-                    
-                    state.addInstruction(iVisited, aggregateResult);
-                    
-                    if (iterator.hasPrevious()) {
-                        // VALUE
-                        state.addNodeInstruction((Node) iterator.previous());
-                        
-                        if (iterator.hasPrevious()) {
-                            state.addInstruction(iVisited, aggregateResult);
-                        }
-                    } else {
-                        // XXX
-                        throw new RuntimeException("[BUG] odd number list for Hash");
-                        // XXX
-                    }
-                }
-            }
-        }
-    }
-    private static final HashNodeVisitor hashNodeVisitor = new HashNodeVisitor();
-    
-    // Collapsed
-    private static class InstAsgnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		InstAsgnNode iVisited = (InstAsgnNode)ctx;
-            state.getSelf().setInstanceVariable(iVisited.getName(), state.getResult());
-    	}
-    }
-    private static final InstAsgnNodeVisitor1 instAsgnNodeVisitor1 = new InstAsgnNodeVisitor1();
-    private static class InstAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		InstAsgnNode iVisited = (InstAsgnNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, instAsgnNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final InstAsgnNodeVisitor instAsgnNodeVisitor = new InstAsgnNodeVisitor();
-    
-    // Nothing to do
-    private static class InstVarNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		InstVarNode iVisited = (InstVarNode)ctx;
-        	IRubyObject variable = state.getSelf().getInstanceVariable(iVisited.getName());
-        	
-            state.setResult(variable == null ? state.runtime.getNil() : variable);
-    	}
-    }
-    private static final InstVarNodeVisitor instVarNodeVisitor = new InstVarNodeVisitor();
-    
-    // Collapsed
-    private static class IfNodeImplVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		IfNode iVisited = (IfNode)ctx;
-    		IRubyObject result = state.getResult();
-    		
-    		// Must set to nil; ifs or logical statements without then/else return nil
-    		state.clearResult();
-    		
-    		if (result.isTrue()) {
-    			if (iVisited.getThenBody() != null) {
-    				state.addNodeInstruction(iVisited.getThenBody());
-    			}
-            } else {
-            	if (iVisited.getElseBody() != null) {
-            		state.addNodeInstruction(iVisited.getElseBody());
-            	}
-            }
-    	}
-    }
-    private static final IfNodeImplVisitor ifNodeImplVisitor = new IfNodeImplVisitor();
-    private static class IfNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		IfNode iVisited = (IfNode)ctx;
-    		// add in reverse order
-    		state.addInstruction(iVisited, ifNodeImplVisitor);
-    		state.addNodeInstruction(iVisited.getCondition());
-       	}
-    }
-    private static final IfNodeVisitor ifNodeVisitor = new IfNodeVisitor();
-    
-    // Not collapsed, depends on exception handling and function calls
-    private static class IterNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		IterNode iVisited = (IterNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            tc.preIterEval(Block.createBlock(iVisited.getVarNode(), iVisited.getCallable(), state.getSelf()));
-                try {
-                    while (true) {
-                        try {
-                            tc.setBlockAvailable();
-                            state.setResult(state.begin(iVisited.getIterNode()));
-                            return;
-                        } catch (JumpException je) {
-                        	if (je.getJumpType() == JumpException.JumpType.RetryJump) {
-                        		// allow loop to retry
-                        	} else {
-                                state.setCurrentException(je);
-                        		throw je;
-                        	}
-                        } finally {
-                            tc.clearBlockAvailable();
-                        }
-                    }
-                } catch (JumpException je) {
-                	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
-	                    IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
-	
-	                    state.setResult(breakValue == null ? state.runtime.getNil() : breakValue);
-                	} else {
-                        state.setCurrentException(je);
-                		throw je;
-                	}
-                } finally {
-                    tc.postIterEval();
-                }
-    	}
-    }
-    private static final IterNodeVisitor iterNodeVisitor = new IterNodeVisitor();
-    
-    // Collapsed
-    private static class LocalAsgnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		LocalAsgnNode iVisited = (LocalAsgnNode)ctx;
-            state.runtime.getCurrentContext().getFrameScope().setValue(iVisited.getCount(), state.getResult());
-        }
-	}
-    private static final LocalAsgnNodeVisitor1 localAsgnNodeVisitor1 = new LocalAsgnNodeVisitor1();
-    private static class LocalAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		LocalAsgnNode iVisited = (LocalAsgnNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, localAsgnNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValueNode());
-    	}
-    }
-    private static final LocalAsgnNodeVisitor localAsgnNodeVisitor = new LocalAsgnNodeVisitor();
-    
-    // Nothing to do assuming getValue() isn't recursing
-    private static class LocalVarNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		LocalVarNode iVisited = (LocalVarNode)ctx;
-            state.setResult(state.runtime.getCurrentContext().getFrameScope().getValue(iVisited.getCount()));
-    	}
-    }
-    private static final LocalVarNodeVisitor localVarNodeVisitor = new LocalVarNodeVisitor();
-    
-    // Not collapsed, calls out to AssignmentVisitor
-    private static class MultipleAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		MultipleAsgnNode iVisited = (MultipleAsgnNode)ctx;
-            state.setResult(new AssignmentVisitor(state).assign(iVisited, state.begin(iVisited.getValueNode()), false));
-    	}
-    }
-    private static final MultipleAsgnNodeVisitor multipleAsgnNodeVisitor = new MultipleAsgnNodeVisitor(); 
-    
-    // Collapsed using result stack
-    private static class Match2NodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            IRubyObject value = state.deaggregateResult();
-            IRubyObject recv = state.getResult();
-            
-            state.setResult(((RubyRegexp) recv).match(value));
-        }
-    }
-    private static final Match2NodeVisitor2 match2NodeVisitor2 = new Match2NodeVisitor2();
-    private static class Match2NodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            Match2Node iVisited = (Match2Node)ctx;
-            
-            state.addInstruction(iVisited, match2NodeVisitor2);
-            
-            state.addNodeInstruction(iVisited.getValueNode());
-            state.addInstruction(iVisited, aggregateResult);
-            state.addNodeInstruction(iVisited.getReceiverNode());
-        }
-    }
-    private static final Match2NodeVisitor match2NodeVisitor = new Match2NodeVisitor();
-    
-    // Collapsed using result stack, other than a method call
-    private static class Match3NodeVisitor2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            IRubyObject value = state.deaggregateResult();
-            IRubyObject receiver = state.getResult();
-            if (value instanceof RubyString) {
-                state.setResult(((RubyRegexp) receiver).match(value));
-            } else {
-                state.setResult(value.callMethod("=~", receiver));
-            }
-        }
-    }
-    private static final Match3NodeVisitor2 match3NodeVisitor2 = new Match3NodeVisitor2();
-    private static class Match3NodeVisitor implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            Match3Node iVisited = (Match3Node)ctx;
-            
-            state.addInstruction(iVisited, match3NodeVisitor2);
-            
-            state.addNodeInstruction(iVisited.getValueNode());
-            state.addInstruction(iVisited, aggregateResult);
-            state.addNodeInstruction(iVisited.getReceiverNode());
-        }
-    }
-    private static final Match3NodeVisitor match3NodeVisitor = new Match3NodeVisitor();
-    
-    // Collapsed
-    private static class MatchNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(((RubyRegexp) state.getResult()).match2());
-    	}
-    }
-    private static final MatchNodeVisitor1 matchNodeVisitor1 = new MatchNodeVisitor1();
-    private static class MatchNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		MatchNode iVisited = (MatchNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, matchNodeVisitor1);
-            state.addNodeInstruction(iVisited.getRegexpNode());
-    	}
-    }
-    private static final MatchNodeVisitor matchNodeVisitor = new MatchNodeVisitor();
-    
-    // Not collapsed; exceptions
-    private static class ModuleNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ModuleNode iVisited = (ModuleNode)ctx;
-            Node classNameNode = iVisited.getCPath();
-            String name = ((INameNode) classNameNode).getName();
-            RubyModule enclosingModule = getEnclosingModule(state, classNameNode);
-
-            if (enclosingModule == null) {
-                throw state.runtime.newTypeError("no outer class/module");
-            }
-
-            RubyModule module;
-            if (enclosingModule == state.runtime.getObject()) {
-                module = state.runtime.getOrCreateModule(name);
-            } else {
-                module = enclosingModule.defineModuleUnder(name);
-            }
-            evalClassDefinitionBody(state, iVisited.getBodyNode(), module);
-    	}
-    }
-    private static final ModuleNodeVisitor moduleNodeVisitor = new ModuleNodeVisitor();
-    
-    // Collapsed
-    private static class NewlineNodeTraceVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		NewlineNode iVisited = (NewlineNode)ctx;
-    		
-    		// something in here is used to build up ruby stack trace...
-            state.getThreadContext().setPosition(iVisited.getPosition());
-            
-            if (isTrace(state)) {
-               callTraceFunction(state, "line", state.getSelf());
-            }
-    	}
-    }
-    private static final NewlineNodeTraceVisitor newlineNodeTraceVisitor = new NewlineNodeTraceVisitor();
-    private static class NewlineNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		List l = new ArrayList();
-    		while (ctx instanceof NewlineNode) {
-    			l.add(0, ctx);
-    			ctx = ((NewlineNode)ctx).getNextNode();
-    		}
-    		state.addNodeInstruction(ctx);
-    		
-    		for (Iterator i = l.iterator(); i.hasNext();) {
-    			state.addInstruction((Node)i.next(), newlineNodeTraceVisitor);
-    		}
-            
-    		// Newlines flush result (result from previous line is not available in next line...perhaps makes sense)
-            state.clearResult();
-    	}
-    }
-    private static final NewlineNodeVisitor newlineNodeVisitor = new NewlineNodeVisitor();
-    
-    // Collapsed
-    private static class NextThrower implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            // now used as an interpreter event
-            JumpException je = new JumpException(JumpException.JumpType.NextJump);
-            
-            je.setPrimaryData(state.getResult());
-            je.setSecondaryData(ctx);
-
-            //state.setCurrentException(je);
-            throw je;
-        }
-    }
-    private static final NextThrower nextThrower = new NextThrower();
-    private static class NextNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		NextNode iVisited = (NextNode)ctx;
-
-            state.getThreadContext().pollThreadEvents();
-            
-            state.setResult(state.runtime.getNil());
-            state.addInstruction(iVisited, nextThrower);
-            if (iVisited.getValueNode() != null) {
-                state.addNodeInstruction(iVisited.getValueNode());
-            }
-    	}
-    }
-    private static final NextNodeVisitor nextNodeVisitor = new NextNodeVisitor();
-    
-    // Nothing to do
-    // FIXME: This is called for "NilNode" visited...shouldn't NilNode visits setResult(nil)?
-    private static class NoopVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    	}
-    }
-    private static final NoopVisitor noopVisitor = new NoopVisitor();
-    
-    // Collapsed
-    private static class NotNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.getResult().isTrue() ? state.runtime.getFalse() : state.runtime.getTrue());
-    	}
-    }
-    private static final NotNodeVisitor1 notNodeVisitor1 = new NotNodeVisitor1();    
-    private static class NotNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		NotNode iVisited = (NotNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, notNodeVisitor1);
-            state.addNodeInstruction(iVisited.getConditionNode());
-    	}
-    }
-    private static final NotNodeVisitor notNodeVisitor = new NotNodeVisitor();
-    
-    // Not collapsed, method call - maybe ok since no dispatching
-    private static class NthRefNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		NthRefNode iVisited = (NthRefNode)ctx;
-            state.setResult(RubyRegexp.nth_match(iVisited.getMatchNumber(), state.getThreadContext().getBackref()));
-    	}
-    }
-    private static final NthRefNodeVisitor nthRefNodeVisitor = new NthRefNodeVisitor();
-    
-    // Not collapsed, multiple evals to resolve
-    private static class OpElementAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		OpElementAsgnNode iVisited = (OpElementAsgnNode)ctx;
-            IRubyObject receiver = state.begin(iVisited.getReceiverNode());
-
-            IRubyObject[] args = setupArgs(state, state.runtime, state.getThreadContext(), iVisited.getArgsNode());
-
-            IRubyObject firstValue = receiver.callMethod("[]", args);
-
-            if (iVisited.getOperatorName().equals("||")) {
-                if (firstValue.isTrue()) {
-                    state.setResult(firstValue);
-                    return;
-                }
-    			firstValue = state.begin(iVisited.getValueNode());
-            } else if (iVisited.getOperatorName().equals("&&")) {
-                if (!firstValue.isTrue()) {
-                    state.setResult(firstValue);
-                    return;
-                }
-    			firstValue = state.begin(iVisited.getValueNode());
-            } else {
-                firstValue = firstValue.callMethod(iVisited.getOperatorName(), state.begin(iVisited.getValueNode()));
-            }
-
-            IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
-            System.arraycopy(args, 0, expandedArgs, 0, args.length);
-            expandedArgs[expandedArgs.length - 1] = firstValue;
-            state.setResult(receiver.callMethod("[]=", expandedArgs));
-    	}
-    }
-    private static final OpElementAsgnNodeVisitor opElementAsgnNodeVisitor = new OpElementAsgnNodeVisitor();
-    
-    // Not collapsed, multiple evals to resolve
-    private static class OpAsgnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		OpAsgnNode iVisited = (OpAsgnNode)ctx;
-            IRubyObject receiver = state.begin(iVisited.getReceiverNode());
-            IRubyObject value = receiver.callMethod(iVisited.getVariableName());
-
-            if (iVisited.getOperatorName().equals("||")) {
-                if (value.isTrue()) {
-                    state.setResult(value);
-                    return;
-                }
-    			value = state.begin(iVisited.getValueNode());
-            } else if (iVisited.getOperatorName().equals("&&")) {
-                if (!value.isTrue()) {
-                    state.setResult(value);
-                    return;
-                }
-    			value = state.begin(iVisited.getValueNode());
-            } else {
-                value = value.callMethod(iVisited.getOperatorName(), state.begin(iVisited.getValueNode()));
-            }
-
-            receiver.callMethod(iVisited.getVariableName() + "=", value);
-
-            state.setResult(value);
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final OpAsgnNodeVisitor opAsgnNodeVisitor = new OpAsgnNodeVisitor();
-    
-    private static class OpAsgnOrNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		OpAsgnOrNode iVisited = (OpAsgnOrNode) ctx;
-    		String def = new DefinedVisitor(state).getDefinition(iVisited.getFirstNode());
-    		
-    		state.clearResult();
-    		state.addInstruction(ctx, orNodeImplVisitor);
-    		if (def != null) {
-    			state.addNodeInstruction(iVisited.getFirstNode());
-    		}
-    	}
-    }
-    private static final OpAsgnOrNodeVisitor opAsgnOrNodeVisitor = new OpAsgnOrNodeVisitor();
-    
-    // Collapsed
-    private static class OptNNodeGets implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            OptNNode iVisited = (OptNNode)ctx;
-            
-            // FIXME: exceptions could bubble out of this, but no exception handling available in system yet..
-            if (RubyKernel.gets(state.runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
-                // re-push body and self
-                state.addBreakableInstruction(ctx, this);
-                state.addNodeInstruction(iVisited.getBodyNode());
-            } // else continue on out
-        }
-    }
-    private static final OptNNodeGets optNNodeGets = new OptNNodeGets();
-    private static class OptNNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		OptNNode iVisited = (OptNNode)ctx;
-            
-            state.addBreakableInstruction(iVisited, optNNodeGets);
-            state.addRedoMarker(iVisited.getBodyNode());
-            state.addNodeInstruction(iVisited.getBodyNode());
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final OptNNodeVisitor optNNodeVisitor = new OptNNodeVisitor();
-    
-    // Collapsed
-    private static class OrNodeImplVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BinaryOperatorNode iVisited = (BinaryOperatorNode)ctx;
-            if (!state.getResult().isTrue()) {
-                state.addNodeInstruction(iVisited.getSecondNode());
-            }
-    	}
-    }
-    private static final OrNodeImplVisitor orNodeImplVisitor = new OrNodeImplVisitor();
-    private static class OrNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BinaryOperatorNode iVisited = (BinaryOperatorNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, orNodeImplVisitor);
-    		state.addNodeInstruction(iVisited.getFirstNode());	
-    	}
-    }
-    private static final OrNodeVisitor orNodeVisitor = new OrNodeVisitor();
-    
-    // Collapsed; exception is now an interpreter event trigger
-    private static class RedoNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.getThreadContext().pollThreadEvents();
-            
-            // now used as an interpreter event
-            JumpException je = new JumpException(JumpException.JumpType.RedoJump);
-            
-            je.setSecondaryData(ctx);
-            
-            throw je;
-            //state.setCurrentException(je);
-    	}
-    }
-    private static final RedoNodeVisitor redoNodeVisitor = new RedoNodeVisitor();
-    
-    // Collapsed, but FIXME I don't like the null check
-    private static class RescueBodyNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		RescueBodyNode iVisited = (RescueBodyNode)ctx;
-            if (iVisited.getBodyNode() != null) {
-                state.addNodeInstruction(iVisited.getBodyNode());
-            }
-    	}
-    }
-    private static final RescueBodyNodeVisitor rescueBodyNodeVisitor = new RescueBodyNodeVisitor();
-    
-    // Collapsed
-    private static class Rescuer implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            // dummy, just to mark enclosing rescuers
-        }
-    }
-    private static final Rescuer rescuer = new Rescuer();
-    private static class RescueNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		RescueNode iVisited = (RescueNode)ctx;
-            
-            state.addRescuableInstruction(iVisited, rescuer);
-            if (iVisited.getElseNode() != null) {
-                state.addNodeInstruction(iVisited.getElseNode());
-            }
-            state.addNodeInstruction(iVisited.getBodyNode());
-    	}
-    }
-    private static final RescueNodeVisitor rescueNodeVisitor = new RescueNodeVisitor();
-    
-    // Collapsed; exception is now an interpreter event trigger
-    private static class RetryNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.getThreadContext().pollThreadEvents();
-            
-    		JumpException je = new JumpException(JumpException.JumpType.RetryJump);
-
-            state.setCurrentException(je);
-    		throw je;
-    	}
-    }
-    private static final RetryNodeVisitor retryNodeVisitor = new RetryNodeVisitor();
-    
-    // Collapsed; exception is now an interpreter event trigger
-    private static class ReturnNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ReturnNode iVisited = (ReturnNode)ctx;
-    		
-    		JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
-        		
-    		je.setPrimaryData(iVisited.getTarget());
-    		je.setSecondaryData(state.getResult());
-            je.setTertiaryData(iVisited);
-
-            state.setCurrentException(je);
-    		throw je;
-    	}
-    }
-    private static final ReturnNodeVisitor1 returnNodeVisitor1 = new ReturnNodeVisitor1();
-    private static class ReturnNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ReturnNode iVisited = (ReturnNode)ctx;
-    		
-    		state.addInstruction(ctx, returnNodeVisitor1);
-    		if (iVisited.getValueNode() != null) {
-    			state.addNodeInstruction(iVisited.getValueNode());
-    		}
-    		
-    		state.clearResult();
-    	}
-    }
-    private static final ReturnNodeVisitor returnNodeVisitor = new ReturnNodeVisitor();
-    
-    // Not collapsed, evalClassBody will take some work
-    private static class SClassNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		SClassNode iVisited = (SClassNode)ctx;
-            IRubyObject receiver = state.begin(iVisited.getReceiverNode());
-
-            RubyClass singletonClass;
-
-            if (receiver.isNil()) {
-                singletonClass = state.runtime.getNilClass();
-            } else if (receiver == state.runtime.getTrue()) {
-                singletonClass = state.runtime.getClass("True");
-            } else if (receiver == state.runtime.getFalse()) {
-                singletonClass = state.runtime.getClass("False");
-            } else {
-                if (state.runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
-                    throw state.runtime.newSecurityError("Insecure: can't extend object.");
-                }
-
-                singletonClass = receiver.getSingletonClass();
-            }
-            
-            ThreadContext tc = state.getThreadContext();
-            
-            if (tc.getWrapper() != null) {
-                singletonClass.extendObject(tc.getWrapper());
-                singletonClass.includeModule(tc.getWrapper());
-            }
-
-            evalClassDefinitionBody(state, iVisited.getBodyNode(), singletonClass);
-    	}
-    }
-    private static final SClassNodeVisitor sClassNodeVisitor = new SClassNodeVisitor();
-    
-    // Not collapsed, exception handling needed
-    private static class ScopeNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ScopeNode iVisited = (ScopeNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            tc.preScopedBody(iVisited.getLocalNames());
-            try {
-                state.begin(iVisited.getBodyNode());
-            } finally {
-                tc.postScopedBody();
-            }
-    	}
-    }
-    private static final ScopeNodeVisitor scopeNodeVisitor = new ScopeNodeVisitor();
-    
-    // Nothing to do
-    private static class SelfNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.getSelf());
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final SelfNodeVisitor selfNodeVisitor = new SelfNodeVisitor();
-    
-    // Collapsed
-    private static class SplatNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(splatValue(state, state.getResult()));
-    	}
-    }
-    private static final SplatNodeVisitor1 splatNodeVisitor1 = new SplatNodeVisitor1();
-    private static class SplatNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		SplatNode iVisited = (SplatNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, splatNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValue());
-    	}
-    }
-    private static final SplatNodeVisitor splatNodeVisitor = new SplatNodeVisitor();
-    
-    // Nothing to do, other than concerns about newString recursing
-    private static class StrNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		StrNode iVisited = (StrNode)ctx;
-            state.setResult(state.runtime.newString(iVisited.getValue()));
-    	}
-    }
-    private static final StrNodeVisitor strNodeVisitor = new StrNodeVisitor();
-    
-    // Collapsed
-    private static class SValueNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(aValueSplat(state, state.getResult()));
-    	}
-    }
-    private static final SValueNodeVisitor1 sValueNodeVisitor1 = new SValueNodeVisitor1();
-    private static class SValueNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		SValueNode iVisited = (SValueNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, sValueNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValue());
-    	}
-    }
-    private static final SValueNodeVisitor sValueNodeVisitor = new SValueNodeVisitor();
-    
-    // Not collapsed, exceptions
-    private static class SuperNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		SuperNode iVisited = (SuperNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            if (tc.getFrameLastClass() == null) {
-                throw state.runtime.newNameError("Superclass method '" + tc.getFrameLastFunc() + "' disabled.");
-            }
-
-            tc.beginCallArgs();
-
-            IRubyObject[] args = null;
-            try {
-                args = setupArgs(state, state.runtime, tc, iVisited.getArgsNode());
-            } finally {
-            	tc.endCallArgs();
-            }
-            state.setResult(tc.callSuper(args));
-    	}
-    }
-    private static final SuperNodeVisitor superNodeVisitor = new SuperNodeVisitor();
-    
-    // Collapsed
-    private static class ToAryNodeVisitor1 implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(aryToAry(state, state.getResult()));
-    	}
-    }
-    private static final ToAryNodeVisitor1 toAryNodeVisitor1 = new ToAryNodeVisitor1();
-    private static class ToAryNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		ToAryNode iVisited = (ToAryNode)ctx;
-    		state.clearResult();
-    		state.addInstruction(ctx, toAryNodeVisitor1);
-            state.addNodeInstruction(iVisited.getValue());
-    	}
-    }
-    private static final ToAryNodeVisitor toAryNodeVisitor = new ToAryNodeVisitor();
-
-    // Nothing to do
-    private static class TrueNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.runtime.getTrue());
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final TrueNodeVisitor trueNodeVisitor = new TrueNodeVisitor();
-    
-    // Not collapsed, exceptions
-    private static class UndefNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		UndefNode iVisited = (UndefNode)ctx;
-            ThreadContext tc = state.getThreadContext();
-            
-            if (tc.getRubyClass() == null) {
-                throw state.runtime.newTypeError("No class to undef method '" + iVisited.getName() + "'.");
-            }
-            tc.getRubyClass().undef(iVisited.getName());
-    	}
-    }
-    private static final UndefNodeVisitor undefNodeVisitor = new UndefNodeVisitor();
-    
-    // Collapsed
-    private static class UntilConditionCheck implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            UntilNode iVisited = (UntilNode)ctx;
-            
-            // result contains condition check
-            IRubyObject condition = state.getResult();
-            
-            if (!condition.isTrue()) {
-                // re-push body, condition, and check
-                state.addBreakableInstruction(iVisited, this);
-                state.addNodeInstruction(iVisited.getConditionNode());
-                if (iVisited.getBodyNode() != null) {
-                    // FIXME: Hack?...bodynode came up as null for lex method in irb's ruby-lex.rb
-                    state.addRedoMarker(iVisited.getBodyNode());
-                    state.addNodeInstruction(iVisited.getBodyNode());
-                }
-            }
-            // else loop terminates
-        }
-    }
-    private static final UntilConditionCheck untilConditionCheck = new UntilConditionCheck();
-    private static class UntilNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		UntilNode iVisited = (UntilNode)ctx;
-                    
-            state.addBreakableInstruction(iVisited, untilConditionCheck);
-            state.addNodeInstruction(iVisited.getConditionNode());
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final UntilNodeVisitor untilNodeVisitor = new UntilNodeVisitor();
-    
-    // Nothing to do, but examine aliasing side effects
-    private static class VAliasNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		VAliasNode iVisited = (VAliasNode)ctx;
-            state.runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
-    	}
-    }
-    private static final VAliasNodeVisitor vAliasNodeVisitor = new VAliasNodeVisitor();
-    
-    // Not collapsed, method call
-    private static class VCallNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		VCallNode iVisited = (VCallNode)ctx;
-            state.setResult(state.getSelf().callMethod(iVisited.getMethodName(), IRubyObject.NULL_ARRAY, CallType.VARIABLE));
-    	}
-    }
-    private static final VCallNodeVisitor vCallNodeVisitor = new VCallNodeVisitor();
-    
-    // Collapsed
-    private static class WhileConditionCheck implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            WhileNode iVisited = (WhileNode)ctx;
-            
-            // result contains condition check
-            IRubyObject condition = state.getResult();
-            
-            if (condition.isTrue()) {
-                // re-push body, condition, and check
-                state.addBreakableInstruction(iVisited, whileConditionCheck);
-                state.addNodeInstruction(iVisited.getConditionNode());
-                // FIXME: Hack? See UntilConditionCheck for explanation of why this may not be kosher
-                if (iVisited.getBodyNode() != null) {
-                    state.addRedoMarker(iVisited.getBodyNode());
-                    state.addNodeInstruction(iVisited.getBodyNode());
-                }
-            }
-            // else loop terminates
-        }
-    }
-    private static final WhileConditionCheck whileConditionCheck = new WhileConditionCheck();
-    private static class WhileNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		WhileNode iVisited = (WhileNode)ctx;
-            
-            state.addBreakableInstruction(iVisited, whileConditionCheck);
-            state.addNodeInstruction(iVisited.getConditionNode());
-
-            if (!iVisited.evaluateAtStart() && iVisited.getBodyNode() != null) {
-                state.addRedoMarker(iVisited.getBodyNode());
-                state.addNodeInstruction(iVisited.getBodyNode());
-            }
-
-            state.getThreadContext().pollThreadEvents();
-    	}
-    }
-    private static final WhileNodeVisitor whileNodeVisitor = new WhileNodeVisitor();
-    
-    // Not collapsed, method call
-    private static class XStrNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		XStrNode iVisited = (XStrNode)ctx;
-            state.setResult(state.getSelf().callMethod("`", state.runtime.newString(iVisited.getValue())));
-    	}
-    }
-    private static final XStrNodeVisitor xStrNodeVisitor = new XStrNodeVisitor();
-    
-    // Not collapsed, yield is like a method call, needs research
-    private static class Yield2 implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            YieldNode iVisited = (YieldNode)ctx;    
-            // Special Hack...We cannot tell between no args and a nil one.
-            // Change it back to null for now until a better solution is 
-            // found
-            // TODO: Find better way of differing...
-            if (iVisited.getArgsNode() == null) {
-                state.setResult(null);
-            }
-                
-            state.setResult(state.getThreadContext().yieldCurrentBlock(state.getResult(), null, null, iVisited.getCheckState()));
-        }
-    }
-    private static final Yield2 yield2 = new Yield2();
-    private static class YieldNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		YieldNode iVisited = (YieldNode)ctx;
-            
-            state.addInstruction(iVisited, yield2);
-            if (iVisited.getArgsNode() != null) {
-                state.addNodeInstruction(iVisited.getArgsNode());
-            }
-    	}
-    }
-    private static final YieldNodeVisitor yieldNodeVisitor = new YieldNodeVisitor();
-    
-    // Nothing to do, other than array creation side effects?
-    private static class ZArrayNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            state.setResult(state.runtime.newArray());
-    	}
-    }
-    private static final ZArrayNodeVisitor zArrayNodeVisitor = new ZArrayNodeVisitor();
-    
-    // Not collapsed, is this a call?
-    private static class ZSuperNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-            ThreadContext tc = state.getThreadContext();
-    		
-            if (tc.getFrameLastClass() == null) {
-                throw state.runtime.newNameError("superclass method '" + tc.getFrameLastFunc() + "' disabled");
-            }
-
-            state.setResult(tc.callSuper(tc.getFrameArgs()));
-    	}
-    }
-    private static final ZSuperNodeVisitor zSuperNodeVisitor = new ZSuperNodeVisitor();
-    
-    // Nothing to do, other than side effects
-    private static class BignumNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		BignumNode iVisited = (BignumNode)ctx;
-            state.setResult(RubyBignum.newBignum(state.runtime, iVisited.getValue()));
-    	}
-    }
-    private static final BignumNodeVisitor bignumNodeVisitor = new BignumNodeVisitor();
-    
-//  Nothing to do, other than side effects
-    private static class FixnumNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		FixnumNode iVisited = (FixnumNode)ctx;
-            state.setResult(state.runtime.newFixnum(iVisited.getValue()));
-    	}
-    }
-    private static final FixnumNodeVisitor fixnumNodeVisitor = new FixnumNodeVisitor();
-    
-//  Nothing to do, other than side effects
-    private static class FloatNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		FloatNode iVisited = (FloatNode)ctx;
-            state.setResult(RubyFloat.newFloat(state.runtime, iVisited.getValue()));
-    	}
-    }
-    private static final FloatNodeVisitor floatNodeVisitor = new FloatNodeVisitor();
-    
-//  Nothing to do, other than side effects
-    private static class RegexpNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		RegexpNode iVisited = (RegexpNode)ctx;
-            state.setResult(RubyRegexp.newRegexp(state.runtime, iVisited.getPattern(), null));
-    	}
-    }
-    private static final RegexpNodeVisitor regexpNodeVisitor = new RegexpNodeVisitor();
-    
-//  Nothing to do, other than side effects
-    private static class SymbolNodeVisitor implements Instruction {
-    	public void execute(EvaluationState state, InstructionContext ctx) {
-    		SymbolNode iVisited = (SymbolNode)ctx;
-            state.setResult(state.runtime.newSymbol(iVisited.getName()));
-    	}
-    }
-    private static final SymbolNodeVisitor symbolNodeVisitor = new SymbolNodeVisitor();
-
-    /**
-     * @see NodeVisitor#visitAliasNode(AliasNode)
-     */
-    public Instruction visitAliasNode(AliasNode iVisited) {
-    	return aliasNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitAndNode(AndNode)
-     */
-    public Instruction visitAndNode(AndNode iVisited) {
-    	return andNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitArgsNode(ArgsNode)
-     */
-    public Instruction visitArgsNode(ArgsNode iVisited) {
-        assert false;
-        return null;
-    }
-
-    /**
-     * @see NodeVisitor#visitArgsCatNode(ArgsCatNode)
-     */
-    public Instruction visitArgsCatNode(ArgsCatNode iVisited) {
-    	return argsCatNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitArrayNode(ArrayNode)
-     */
-    public Instruction visitArrayNode(ArrayNode iVisited) {
-    	return arrayNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitBackRefNode(BackRefNode)
-     */
-    public Instruction visitBackRefNode(BackRefNode iVisited) {
-    	return backRefNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitBeginNode(BeginNode)
-     */
-    public Instruction visitBeginNode(BeginNode iVisited) {
-    	return beginNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitBlockArgNode(BlockArgNode)
-     */
-    public Instruction visitBlockArgNode(BlockArgNode iVisited) {
-        assert false;
-        return null;
-    }
-    
-    /**
-     * @see NodeVisitor#visitBlockNode(BlockNode)
-     */
-    public Instruction visitBlockNode(BlockNode iVisited) {
-    	return blockNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitBlockPassNode(BlockPassNode)
-     */
-    public Instruction visitBlockPassNode(BlockPassNode iVisited) {
-    	return blockPassNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitBreakNode(BreakNode)
-     */
-    public Instruction visitBreakNode(BreakNode iVisited) {
-    	return breakNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitConstDeclNode(ConstDeclNode)
-     */
-    public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
-    	return constDeclNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitClassVarAsgnNode(ClassVarAsgnNode)
-     */
-    public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
-    	return classVarAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitClassVarDeclNode(ClassVarDeclNode)
-     */
-    public Instruction visitClassVarDeclNode(ClassVarDeclNode iVisited) {
-    	return classVarDeclNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitClassVarNode(ClassVarNode)
-     */
-    public Instruction visitClassVarNode(ClassVarNode iVisited) {
-    	return classVarNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitCallNode(CallNode)
-     */
-    public Instruction visitCallNode(CallNode iVisited) {
-    	return callNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitCaseNode(CaseNode)
-     */
-    public Instruction visitCaseNode(CaseNode iVisited) {
-    	return caseNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitClassNode(ClassNode)
-     */
-    public Instruction visitClassNode(ClassNode iVisited) {
-    	return classNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitColon2Node(Colon2Node)
-     */
-    public Instruction visitColon2Node(Colon2Node iVisited) {
-    	return colon2NodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitColon3Node(Colon3Node)
-     */
-    public Instruction visitColon3Node(Colon3Node iVisited) {
-    	return colon3NodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitConstNode(ConstNode)
-     */
-    public Instruction visitConstNode(ConstNode iVisited) {
-    	return constNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDAsgnNode(DAsgnNode)
-     */
-    public Instruction visitDAsgnNode(DAsgnNode iVisited) {
-    	return dAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDRegxNode(DRegexpNode)
-     */
-    public Instruction visitDRegxNode(DRegexpNode iVisited) {
-    	return dRegexpNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDStrNode(DStrNode)
-     */
-    public Instruction visitDStrNode(DStrNode iVisited) {
-    	return dStrNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitSymbolNode(SymbolNode)
-     */
-    public Instruction visitDSymbolNode(DSymbolNode iVisited) {
-    	return dSymbolNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDVarNode(DVarNode)
-     */
-    public Instruction visitDVarNode(DVarNode iVisited) {
-    	return dVarNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDXStrNode(DXStrNode)
-     */
-    public Instruction visitDXStrNode(DXStrNode iVisited) {
-    	return dXStrNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDefinedNode(DefinedNode)
-     */
-    public Instruction visitDefinedNode(DefinedNode iVisited) {
-    	return definedNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDefnNode(DefnNode)
-     */
-    public Instruction visitDefnNode(DefnNode iVisited) {
-    	return defnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDefsNode(DefsNode)
-     */
-    public Instruction visitDefsNode(DefsNode iVisited) {
-    	return defsNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitDotNode(DotNode)
-     */
-    public Instruction visitDotNode(DotNode iVisited) {
-    	return dotNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitEnsureNode(EnsureNode)
-     */
-    public Instruction visitEnsureNode(EnsureNode iVisited) {
-    	return ensureNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitEvStrNode(EvStrNode)
-     */
-    public final Instruction visitEvStrNode(final EvStrNode iVisited) {
-    	return evStrNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitFCallNode(FCallNode)
-     */
-    public Instruction visitFCallNode(FCallNode iVisited) {
-    	return fCallNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitFalseNode(FalseNode)
-     */
-    public Instruction visitFalseNode(FalseNode iVisited) {
-    	return falseNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitFlipNode(FlipNode)
-     */
-    public Instruction visitFlipNode(FlipNode iVisited) {
-    	return flipNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitForNode(ForNode)
-     */
-    public Instruction visitForNode(ForNode iVisited) {
-    	return forNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitGlobalAsgnNode(GlobalAsgnNode)
-     */
-    public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
-    	return globalAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitGlobalVarNode(GlobalVarNode)
-     */
-    public Instruction visitGlobalVarNode(GlobalVarNode iVisited) {
-    	return globalVarNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitHashNode(HashNode)
-     */
-    public Instruction visitHashNode(HashNode iVisited) {
-    	return hashNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitInstAsgnNode(InstAsgnNode)
-     */
-    public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
-    	return instAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitInstVarNode(InstVarNode)
-     */
-    public Instruction visitInstVarNode(InstVarNode iVisited) {
-    	return instVarNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitIfNode(IfNode)
-     */
-    public Instruction visitIfNode(IfNode iVisited) {
-    	return ifNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitIterNode(IterNode)
-     */
-    public Instruction visitIterNode(IterNode iVisited) {
-    	return iterNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitLocalAsgnNode(LocalAsgnNode)
-     */
-    public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
-    	return localAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitLocalVarNode(LocalVarNode)
-     */
-    public Instruction visitLocalVarNode(LocalVarNode iVisited) {
-    	return localVarNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitMultipleAsgnNode(MultipleAsgnNode)
-     */
-    public Instruction visitMultipleAsgnNode(MultipleAsgnNode iVisited) {
-    	return multipleAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitMatch2Node(Match2Node)
-     */
-    public Instruction visitMatch2Node(Match2Node iVisited) {
-    	return match2NodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitMatch3Node(Match3Node)
-     */
-    public Instruction visitMatch3Node(Match3Node iVisited) {
-    	return match3NodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitMatchNode(MatchNode)
-     */
-    public Instruction visitMatchNode(MatchNode iVisited) {
-    	return matchNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitModuleNode(ModuleNode)
-     */
-    public Instruction visitModuleNode(ModuleNode iVisited) {
-    	return moduleNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitNewlineNode(NewlineNode)
-     */
-    public Instruction visitNewlineNode(NewlineNode iVisited) {
-    	return newlineNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitNextNode(NextNode)
-     */
-    public Instruction visitNextNode(NextNode iVisited) {
-    	return nextNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitNilNode(NilNode)
-     */
-    public Instruction visitNilNode(NilNode iVisited) {
-    	return noopVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitNotNode(NotNode)
-     */
-    public Instruction visitNotNode(NotNode iVisited) {
-    	return notNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitNthRefNode(NthRefNode)
-     */
-    public Instruction visitNthRefNode(NthRefNode iVisited) {
-    	return nthRefNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOpElementAsgnNode(OpElementAsgnNode)
-     */
-    public Instruction visitOpElementAsgnNode(OpElementAsgnNode iVisited) {
-    	return opElementAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOpAsgnNode(OpAsgnNode)
-     */
-    public Instruction visitOpAsgnNode(OpAsgnNode iVisited) {
-    	return opAsgnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOpAsgnAndNode(OpAsgnAndNode)
-     */
-    public Instruction visitOpAsgnAndNode(OpAsgnAndNode iVisited) {
-    	return andNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOpAsgnOrNode(OpAsgnOrNode)
-     */
-    public Instruction visitOpAsgnOrNode(OpAsgnOrNode iVisited) {
-    	return opAsgnOrNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOptNNode(OptNNode)
-     */
-    public Instruction visitOptNNode(OptNNode iVisited) {
-    	return optNNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitOrNode(OrNode)
-     */
-    public Instruction visitOrNode(OrNode iVisited) {
-    	return orNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitPostExeNode(PostExeNode)
-     */
-    public Instruction visitPostExeNode(PostExeNode iVisited) {
-    	return noopVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitRedoNode(RedoNode)
-     */
-    public Instruction visitRedoNode(RedoNode iVisited) {
-    	return redoNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitRescueBodyNode(RescueBodyNode)
-     */
-    public Instruction visitRescueBodyNode(RescueBodyNode iVisited) {
-    	return rescueBodyNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitRescueNode(RescueNode)
-     */
-    public Instruction visitRescueNode(RescueNode iVisited) {
-    	return rescueNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitRetryNode(RetryNode)
-     */
-    public Instruction visitRetryNode(RetryNode iVisited) {
-    	return retryNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitReturnNode(ReturnNode)
-     */
-    public Instruction visitReturnNode(ReturnNode iVisited) {
-    	return returnNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitSClassNode(SClassNode)
-     */
-    public Instruction visitSClassNode(SClassNode iVisited) {
-    	return sClassNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitScopeNode(ScopeNode)
-     */
-    public Instruction visitScopeNode(ScopeNode iVisited) {
-    	return scopeNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitSelfNode(SelfNode)
-     */
-    public Instruction visitSelfNode(SelfNode iVisited) {
-    	return selfNodeVisitor;
-    }
-
-    public Instruction visitSplatNode(SplatNode iVisited) {
-    	return splatNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitStrNode(StrNode)
-     */
-    public Instruction visitStrNode(StrNode iVisited) {
-    	return strNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitSValueNode(SValueNode)
-     */
-    public Instruction visitSValueNode(SValueNode iVisited) {
-    	return sValueNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitSuperNode(SuperNode)
-     */
-    public Instruction visitSuperNode(SuperNode iVisited) {
-    	return superNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitToAryNode(ToAryNode)
-     */
-    public Instruction visitToAryNode(ToAryNode iVisited) {
-    	return toAryNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitTrueNode(TrueNode)
-     */
-    public Instruction visitTrueNode(TrueNode iVisited) {
-    	return trueNodeVisitor;
-    }
-    
-    /**
-     * @see NodeVisitor#visitUndefNode(UndefNode)
-     */
-    public Instruction visitUndefNode(UndefNode iVisited) {
-    	return undefNodeVisitor;    	
-    }
-
-    /**
-     * @see NodeVisitor#visitUntilNode(UntilNode)
-     */
-    public Instruction visitUntilNode(UntilNode iVisited) {
-    	return untilNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitVAliasNode(VAliasNode)
-     */
-    public Instruction visitVAliasNode(VAliasNode iVisited) {
-    	return vAliasNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitVCallNode(VCallNode)
-     */
-    public Instruction visitVCallNode(VCallNode iVisited) {
-    	return vCallNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitWhenNode(WhenNode)
-     */
-    public Instruction visitWhenNode(WhenNode iVisited) {
-        assert false;
-        return null;
-    }
-
-    /**
-     * @see NodeVisitor#visitWhileNode(WhileNode)
-     */
-    public Instruction visitWhileNode(WhileNode iVisited) {
-    	return whileNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitXStrNode(XStrNode)
-     */
-    public Instruction visitXStrNode(XStrNode iVisited) {
-    	return xStrNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitYieldNode(YieldNode)
-     */
-    public Instruction visitYieldNode(YieldNode iVisited) {
-    	return yieldNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitZArrayNode(ZArrayNode)
-     */
-    public Instruction visitZArrayNode(ZArrayNode iVisited) {
-    	return zArrayNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitZSuperNode(ZSuperNode)
-     */
-    public Instruction visitZSuperNode(ZSuperNode iVisited) {
-    	return zSuperNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitBignumNode(BignumNode)
-     */
-    public Instruction visitBignumNode(BignumNode iVisited) {
-    	return bignumNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitFixnumNode(FixnumNode)
-     */
-    public Instruction visitFixnumNode(FixnumNode iVisited) {
-    	return fixnumNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitFloatNode(FloatNode)
-     */
-    public Instruction visitFloatNode(FloatNode iVisited) {
-    	return floatNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitRegexpNode(RegexpNode)
-     */
-    public Instruction visitRegexpNode(RegexpNode iVisited) {
-    	return regexpNodeVisitor;
-    }
-
-    /**
-     * @see NodeVisitor#visitSymbolNode(SymbolNode)
-     */
-    public Instruction visitSymbolNode(SymbolNode iVisited) {
-    	return symbolNodeVisitor;
-    }
-
-    /** Evaluates the body in a class or module definition statement.
-     *
-     */
-    private static void evalClassDefinitionBody(EvaluationState state, ScopeNode iVisited, RubyModule type) {
-        ThreadContext tc = state.getThreadContext();
-        
-		tc.preClassEval(iVisited.getLocalNames(), type);
-
-        IRubyObject oldSelf = state.getSelf();
-
-        try {
-            if (isTrace(state)) {
-                callTraceFunction(state, "class", type);
-            }
-
-            state.setSelf(type);
-            state.begin(iVisited.getBodyNode());
-        } finally {
-            state.setSelf(oldSelf);
-
-            tc.postClassEval();
-
-            if (isTrace(state)) {
-                callTraceFunction(state, "end", null);
-            }
-        }
-    }
-
-    private static IRubyObject aryToAry(EvaluationState state, IRubyObject value) {
-        if (value instanceof RubyArray) {
-            return value;
-        }
-        
-        if (value.respondsTo("to_ary")) {
-            return value.convertToType("Array", "to_ary", false);
-        }
-        
-        return state.runtime.newArray(value);
-    }
-    
-    private static IRubyObject splatValue(EvaluationState state, IRubyObject value) {
-        if (value.isNil()) {
-            return state.runtime.newArray(value);
-        }
-        
-        return arrayValue(state, value);
-    }
-
-    private static IRubyObject aValueSplat(EvaluationState state, IRubyObject value) {
-        if (!(value instanceof RubyArray) ||
-            ((RubyArray) value).length().getLongValue() == 0) {
-            return state.runtime.getNil();
-        }
-        
-        RubyArray array = (RubyArray) value;
-        
-        return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
-    }
-
-    private static RubyArray arrayValue(EvaluationState state, IRubyObject value) {
-        IRubyObject newValue = value.convertToType("Array", "to_ary", false);
-
-        if (newValue.isNil()) {
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
-            // remove this hack too.
-            if (value.getType().searchMethod("to_a").getImplementationClass() != state.runtime.getKernel()) {
-                newValue = value.convertToType("Array", "to_a", false);
-                if(newValue.getType() != state.runtime.getClass("Array")) {
-                    throw state.runtime.newTypeError("`to_a' did not return Array");
-                }
-            } else {
-                newValue = state.runtime.newArray(value);
-            }
-        }
-        
-        return (RubyArray) newValue;
-    }
-    
-    private static IRubyObject[] setupArgs(EvaluationState state, IRuby runtime, ThreadContext context, Node node) {
-        if (node == null) {
-            return IRubyObject.NULL_ARRAY;
-        }
-
-        if (node instanceof ArrayNode) {
-        	ISourcePosition position = context.getPosition();
-            ArrayList list = new ArrayList(((ArrayNode) node).size());
-            
-            for (Iterator iter=((ArrayNode)node).iterator(); iter.hasNext();){
-                final Node next = (Node) iter.next();
-                if (next instanceof SplatNode) {
-                    list.addAll(((RubyArray) state.begin(next)).getList());
-                } else {
-                    list.add(state.begin(next));
-                }
-            }
-
-            context.setPosition(position);
-
-            return (IRubyObject[]) list.toArray(new IRubyObject[list.size()]);
-        }
-
-        return ArgsUtil.arrayify(state.begin(node));
-    }
-
-    private static RubyModule getEnclosingModule(EvaluationState state, Node node) {
-        RubyModule enclosingModule = null;
-        
-        if (node instanceof Colon2Node) {
-        	state.begin(((Colon2Node) node).getLeftNode());
-        	
-        	if (state.getResult() != null && !state.getResult().isNil()) {
-        		enclosingModule = (RubyModule) state.getResult();
-        	}
-        } else if (node instanceof Colon3Node) {
-            enclosingModule = state.runtime.getObject(); 
-        }
-        
-        if (enclosingModule == null) {
-        	enclosingModule = (RubyModule)state.getThreadContext().peekCRef().getValue();
-        }
-
-        return enclosingModule;
-    }
-}
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index 8f665758c7..d0fdea61dd 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,642 +1,1574 @@
 /*
  * Created on Sep 11, 2005
  *
  * TODO To change the template for this generated file go to
  * Window - Preferences - Java - Code Style - Code Templates
  */
 package org.jruby.evaluator;
 
 import java.util.ArrayList;
+import java.util.HashMap;
 import java.util.Iterator;
+import java.util.Map;
 
 import org.jruby.IRuby;
+import org.jruby.MetaClass;
 import org.jruby.RubyArray;
+import org.jruby.RubyBignum;
+import org.jruby.RubyClass;
 import org.jruby.RubyException;
+import org.jruby.RubyFloat;
+import org.jruby.RubyHash;
+import org.jruby.RubyKernel;
+import org.jruby.RubyModule;
+import org.jruby.RubyProc;
+import org.jruby.RubyRange;
+import org.jruby.RubyRegexp;
+import org.jruby.RubyString;
+import org.jruby.ast.AliasNode;
+import org.jruby.ast.ArgsCatNode;
+import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArrayNode;
+import org.jruby.ast.BackRefNode;
+import org.jruby.ast.BeginNode;
+import org.jruby.ast.BignumNode;
+import org.jruby.ast.BinaryOperatorNode;
+import org.jruby.ast.BlockNode;
+import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
+import org.jruby.ast.CallNode;
+import org.jruby.ast.CaseNode;
+import org.jruby.ast.ClassNode;
+import org.jruby.ast.ClassVarAsgnNode;
+import org.jruby.ast.ClassVarDeclNode;
+import org.jruby.ast.ClassVarNode;
+import org.jruby.ast.Colon2Node;
+import org.jruby.ast.Colon3Node;
+import org.jruby.ast.ConstDeclNode;
+import org.jruby.ast.ConstNode;
+import org.jruby.ast.DAsgnNode;
+import org.jruby.ast.DRegexpNode;
+import org.jruby.ast.DStrNode;
+import org.jruby.ast.DSymbolNode;
+import org.jruby.ast.DVarNode;
+import org.jruby.ast.DXStrNode;
+import org.jruby.ast.DefinedNode;
+import org.jruby.ast.DefnNode;
+import org.jruby.ast.DefsNode;
+import org.jruby.ast.DotNode;
+import org.jruby.ast.EnsureNode;
+import org.jruby.ast.EvStrNode;
+import org.jruby.ast.FCallNode;
+import org.jruby.ast.FixnumNode;
+import org.jruby.ast.FlipNode;
+import org.jruby.ast.FloatNode;
+import org.jruby.ast.ForNode;
+import org.jruby.ast.GlobalAsgnNode;
+import org.jruby.ast.GlobalVarNode;
+import org.jruby.ast.HashNode;
+import org.jruby.ast.IfNode;
+import org.jruby.ast.InstAsgnNode;
+import org.jruby.ast.InstVarNode;
+import org.jruby.ast.IterNode;
 import org.jruby.ast.ListNode;
+import org.jruby.ast.LocalAsgnNode;
+import org.jruby.ast.LocalVarNode;
+import org.jruby.ast.Match2Node;
+import org.jruby.ast.Match3Node;
+import org.jruby.ast.MatchNode;
+import org.jruby.ast.ModuleNode;
+import org.jruby.ast.MultipleAsgnNode;
+import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
-import org.jruby.ast.RedoNode;
+import org.jruby.ast.NodeTypes;
+import org.jruby.ast.NotNode;
+import org.jruby.ast.NthRefNode;
+import org.jruby.ast.OpAsgnNode;
+import org.jruby.ast.OpAsgnOrNode;
+import org.jruby.ast.OpElementAsgnNode;
+import org.jruby.ast.OptNNode;
+import org.jruby.ast.OrNode;
+import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
+import org.jruby.ast.ReturnNode;
+import org.jruby.ast.SClassNode;
+import org.jruby.ast.SValueNode;
+import org.jruby.ast.ScopeNode;
 import org.jruby.ast.SplatNode;
+import org.jruby.ast.StrNode;
+import org.jruby.ast.SuperNode;
+import org.jruby.ast.SymbolNode;
+import org.jruby.ast.ToAryNode;
+import org.jruby.ast.UndefNode;
+import org.jruby.ast.UntilNode;
+import org.jruby.ast.VAliasNode;
+import org.jruby.ast.VCallNode;
+import org.jruby.ast.WhenNode;
+import org.jruby.ast.WhileNode;
+import org.jruby.ast.XStrNode;
+import org.jruby.ast.YieldNode;
+import org.jruby.ast.types.INameNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException.JumpType;
+import org.jruby.internal.runtime.methods.DefaultMethod;
+import org.jruby.internal.runtime.methods.WrapperCallable;
 import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.runtime.Block;
+import org.jruby.runtime.CallType;
+import org.jruby.runtime.ICallable;
 import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
-import org.jruby.util.UnsynchronizedStack;
 
 public class EvaluationState {
-	private IRubyObject result;
-    private UnsynchronizedStack results = null;
+    public static IRubyObject eval(ThreadContext context, Node node, IRubyObject self) {
+        IRuby runtime = context.getRuntime();
+        if (node == null) return runtime.getNil();
+
+        try {
+            switch (node.nodeId) {
+            case NodeTypes.ALIASNODE: {
+                AliasNode iVisited = (AliasNode) node;
     
-	public final IRuby runtime;
-	private IRubyObject self;
-	public final EvaluateVisitor evaluator;
+                if (context.getRubyClass() == null) {
+                    throw runtime.newTypeError("no class to make alias");
+                }
     
-    private UnsynchronizedStack instructionBundleStacks = null;
-    private InstructionBundle currentStack;
+                context.getRubyClass().defineAlias(iVisited.getNewName(), iVisited.getOldName());
+                context.getRubyClass().callMethod("method_added", runtime.newSymbol(iVisited.getNewName()));
     
-    private JumpException currentException;
+                return runtime.getNil();
+            }
+            case NodeTypes.ANDNODE: {
+                BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
     
-    public EvaluationState(IRuby runtime, IRubyObject self) {
-        this.runtime = runtime;
-        this.evaluator = EvaluateVisitor.getInstance();
-        
-        result = runtime.getNil();
-         
-        setSelf(self);
-    }
+                // add in reverse order
+                IRubyObject result = eval(context, iVisited.getFirstNode(), self);
+                if (!result.isTrue()) return result;
+                return eval(context, iVisited.getSecondNode(), self);
+            }
+            case NodeTypes.ARGSCATNODE: {
+                ArgsCatNode iVisited = (ArgsCatNode) node;
     
-    // FIXME: exceptions thrown during aggregation cause this to leak
-    public void aggregateResult() {
-        if (results == null) {
-            results = new UnsynchronizedStack();
-            results.push(result);
-        }
-        results.push(runtime.getNil());
-    }
+                IRubyObject args = eval(context, iVisited.getFirstNode(), self);
+                IRubyObject secondArgs = splatValue(eval(context, iVisited.getSecondNode(), self));
+                RubyArray list = args instanceof RubyArray ? (RubyArray) args : runtime.newArray(args);
     
-    public IRubyObject deaggregateResult() {
-        return (IRubyObject)results.pop();
-    }
+                return list.concat(secondArgs);
+            }
+                //                case NodeTypes.ARGSNODE:
+                //                EvaluateVisitor.argsNodeVisitor.execute(this, node);
+                //                break;
+                //                case NodeTypes.ARGUMENTNODE:
+                //                EvaluateVisitor.argumentNodeVisitor.execute(this, node);
+                //                break;
+            case NodeTypes.ARRAYNODE: {
+                ArrayNode iVisited = (ArrayNode) node;
+                IRubyObject[] array = new IRubyObject[iVisited.size()];
+                int i = 0;
+                for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
+                    Node next = (Node) iterator.next();
     
-    /**
-     * @param result The result to set.
-     */
-    public void setResult(IRubyObject result) {
-        if (results == null) {
-            this.result = result;
-        } else {
-            results.set(results.size() - 1, result);
-        }
-    }
+                    array[i++] = eval(context, next, self);
+                }
     
-    /**
-     * @return Returns the result.
-     */
-    public IRubyObject getResult() {
-        if (results == null) {
-            return result;
-        } else {
-            return (IRubyObject)results.peek();
-        }
-    }
+                return runtime.newArray(array);
+            }
+                //                case NodeTypes.ASSIGNABLENODE:
+                //                EvaluateVisitor.assignableNodeVisitor.execute(this, node);
+                //                break;
+            case NodeTypes.BACKREFNODE: {
+                BackRefNode iVisited = (BackRefNode) node;
+                IRubyObject backref = context.getBackref();
+                switch (iVisited.getType()) {
+                case '~':
+                    return backref;
+                case '&':
+                    return RubyRegexp.last_match(backref);
+                case '`':
+                    return RubyRegexp.match_pre(backref);
+                case '\'':
+                    return RubyRegexp.match_post(backref);
+                case '+':
+                    return RubyRegexp.match_last(backref);
+                }
+                break;
+            }
+            case NodeTypes.BEGINNODE: {
+                BeginNode iVisited = (BeginNode) node;
     
-    public void clearResult() {
-        setResult(runtime.getNil());
-    }
+                return eval(context, iVisited.getBodyNode(), self);
+            }
+            case NodeTypes.BIGNUMNODE: {
+                BignumNode iVisited = (BignumNode) node;
+                return RubyBignum.newBignum(runtime, iVisited.getValue());
+            }
+                //                case NodeTypes.BINARYOPERATORNODE:
+                //                EvaluateVisitor.binaryOperatorNodeVisitor.execute(this, node);
+                //                break;
+                //                case NodeTypes.BLOCKARGNODE:
+                //                EvaluateVisitor.blockArgNodeVisitor.execute(this, node);
+                //                break;
+            case NodeTypes.BLOCKNODE: {
+                BlockNode iVisited = (BlockNode) node;
     
-    public void flushResults() {
-        results = null;
-        result = runtime.getNil();
-    }
+                IRubyObject result = runtime.getNil();
+                for (Iterator iter = iVisited.iterator(); iter.hasNext();) {
+                    result = eval(context, (Node) iter.next(), self);
+                }
     
-    public Instruction peekCurrentInstruction() {
-        return getCurrentInstructionStack().instruction;
-    }
+                return result;
+            }
+            case NodeTypes.BLOCKPASSNODE: {
+                BlockPassNode iVisited = (BlockPassNode) node;
+                IRubyObject proc = eval(context, iVisited.getBodyNode(), self);
+                
     
-    public InstructionContext peekCurrentInstructionContext() {
-        return getCurrentInstructionStack().instructionContext;
-    }
+                if (proc.isNil()) {
+                    context.setNoBlock();
+                    try {
+                        return eval(context, iVisited.getIterNode(), self);
+                    } finally {
+                        context.clearNoBlock();
+                    }
+                }
     
-    /**
-	 * Return the current top stack of nodes
-	 * @return
-	 */
-	public InstructionBundle getCurrentInstructionStack() {
-		return currentStack;
-	}
-	
-	/**
-	 * Pop and discard the current top stacks of nodes and visitors
-	 */
-	public void popCurrentInstructionStack() {
-        if (instructionBundleStacks.isEmpty()) {
-            currentStack = null;
-            return;
-        }
-        while (currentStack != null) {
-            currentStack = currentStack.nextInstruction;
-        }
-        currentStack = (InstructionBundle)instructionBundleStacks.pop();
-	}
-	
-	/**
-	 * Pop and discard the top item on the current top stacks of nodes and visitors
-	 */
-	public InstructionBundle popCurrentInstruction() {
-        InstructionBundle prev = currentStack;
-        if (currentStack != null) {
-            currentStack = currentStack.nextInstruction;
-        }
-        return prev;
-	}
-	
-	/**
-	 * Push down a new pair of node and visitor stacks
-	 */
-	public void pushCurrentInstructionStack() {
-        if (instructionBundleStacks == null) instructionBundleStacks = new UnsynchronizedStack();
-	    if (currentStack == null && instructionBundleStacks.isEmpty()) {
-            return;
-        }
-        
-        instructionBundleStacks.push(currentStack);
-        currentStack = null;
-	}
-	
-	/**
-	 * Add a node and visitor to the top node and visitor stacks, using the default visitor for the given node.
-	 * The default visitor is determined by calling node.accept() with the current state's evaluator.
-	 * 
-	 * @param ctx
-	 */
-	public void addNodeInstruction(InstructionContext ctx) {
-        Node node = (Node)ctx;
-        
-        if (node.instruction != null || (node.instruction = new InstructionBundle(node.accept(evaluator), node)) != null) {
-            addInstructionBundle(node.instruction);
-        }
-	}
-	
-	/**
-	 * Add the specified node and visitor to the top of the node and visitor stacks.
-	 * The node will be passed to the visitor when it is evaluated.
-	 * 
-	 * @param node
-	 * @param visitor
-	 */
-	public void addInstruction(InstructionContext ctx, Instruction visitor) {
-        InstructionBundle ib = new InstructionBundle(visitor, ctx);
-        addInstructionBundle(ib);
-	}
-    
-    public void addInstructionBundle(InstructionBundle ib) {
-        ib.nextInstruction = currentStack; 
-        currentStack = ib;
-    }
+                // If not already a proc then we should try and make it one.
+                if (!(proc instanceof RubyProc)) {
+                    proc = proc.convertToType("Proc", "to_proc", false);
     
-    private static class RedoMarker implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-        }
-    }
+                    if (!(proc instanceof RubyProc)) {
+                        throw runtime.newTypeError("wrong argument type "
+                                + proc.getMetaClass().getName() + " (expected Proc)");
+                    }
+                }
     
-    private static final RedoMarker redoMarker = new RedoMarker();
+                // TODO: Add safety check for taintedness
     
-    public void addRedoMarker(Node redoableNode) {
-        InstructionBundle ib = new InstructionBundle(redoMarker, redoableNode);
-        
-        ib.redoable = true;
-        
-        addInstructionBundle(ib);
-    }
+                Block block = (Block) context.getCurrentBlock();
+                if (block != null) {
+                    IRubyObject blockObject = block.getBlockObject();
+                    // The current block is already associated with the proc.  No need to create new
+                    // block for it.  Just eval!
+                    if (blockObject != null && blockObject == proc) {
+                        try {
+                            context.setBlockAvailable();
+                            return eval(context, iVisited.getIterNode(), self);
+                        } finally {
+                            context.clearBlockAvailable();
+                        }
+                    }
+                }
     
-    public void addBreakableInstruction(InstructionContext ic, Instruction i) {
-        InstructionBundle ib = new InstructionBundle(i, ic);
-        
-        ib.breakable = true;
-        
-        addInstructionBundle(ib);
-    }
+                context.preBlockPassEval(((RubyProc) proc).getBlock());
     
-    public void addEnsuredInstruction(InstructionContext ic, Instruction i) {
-        InstructionBundle ib = new InstructionBundle(i, ic);
-        
-        ib.ensured = true;
-        
-        addInstructionBundle(ib);
-    }
+                try {
+                    return eval(context, iVisited.getIterNode(), self);
+                } finally {
+                    context.postBlockPassEval();
+                }
+            }
+            case NodeTypes.BREAKNODE: {
+                BreakNode iVisited = (BreakNode) node;
     
-    public void addRetriableInstruction(InstructionContext ic) {
-        InstructionBundle ib = new InstructionBundle(retrier, ic);
-        
-        ib.retriable = true;
-        
-        addInstructionBundle(ib);
-    }
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
     
-    public void addRescuableInstruction(InstructionContext ic, Instruction i) {
-        InstructionBundle ib = new InstructionBundle(i, ic);
-        
-        ib.rescuable = true;
-        
-        addInstructionBundle(ib);
-    }
+                JumpException je = new JumpException(JumpException.JumpType.BreakJump);
     
-	/**
-	 * @param self The self to set.
-	 */
-	public void setSelf(IRubyObject self) {
-		this.self = self;
-	}
-    
-	/**
-	 * @return Returns the self.
-	 */
-	public IRubyObject getSelf() {
-		return self;
-	}
-    
-    private static class ExceptionRethrower implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            throw state.getCurrentException();
-        }
-    }
-    private static final ExceptionRethrower exceptionRethrower = new ExceptionRethrower();
+                je.setPrimaryData(result);
+                je.setSecondaryData(node);
     
-    private static class RaiseRethrower implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            throw state.getCurrentException();
-        }
-    }
-    private static final RaiseRethrower raiseRethrower = new RaiseRethrower();
+                throw je;
+            }
+            case NodeTypes.CALLNODE: {
+                CallNode iVisited = (CallNode) node;
     
-    private static class Retrier implements Instruction {
-        public void execute(EvaluationState state, InstructionContext ctx) {
-            // dummy, only used to store the current "retriable" node and clear exceptions after a rescue block
-            state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
-        }
-    }
-    private static final Retrier retrier = new Retrier();
+                context.beginCallArgs();
+                IRubyObject receiver = null;
+                IRubyObject[] args = null;
+                try {
+                    receiver = eval(context, iVisited.getReceiverNode(), self);
+                    args = setupArgs(context, iVisited.getArgsNode(), self);
+                } finally {
+                    context.endCallArgs();
+                }
+                
+                assert receiver.getMetaClass() != null : receiver.getClass().getName();
+                // If reciever is self then we do the call the same way as vcall
+                CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
     
-    /**
-     * Call the topmost visitor in the current top visitor stack with the topmost node in the current top node stack.
-     */
-    public void executeNext() {
-        try {
-        	InstructionBundle ib = popCurrentInstruction();
-            
-            ib.instruction.execute(this, ib.instructionContext);
-        } catch (JumpException je) {
-            switch (je.getJumpType().getTypeId()) {
-            case JumpType.BREAK:
-                handleBreak(je);
-                break;
-            case JumpType.NEXT:
-                handleNext(je);
-                break;
-            case JumpType.RETURN:
-                handleReturn(je);
-                break;
-            case JumpType.RAISE:
-                handleRaise(je);
-                break;
-            case JumpType.REDO:
-                handleRedo(je);
-                break;
-            case JumpType.RETRY:
-                handleRetry(je);
-                break;
-            case JumpType.THROW:
-                handleThrow(je);
+                return receiver.callMethod(iVisited.getName(), args, callType);
+            }
+            case NodeTypes.CASENODE: {
+                CaseNode iVisited = (CaseNode) node;
+                IRubyObject expression = null;
+                if (iVisited.getCaseNode() != null) {
+                    expression = eval(context, iVisited.getCaseNode(), self);
+                }
+    
+                context.pollThreadEvents();
+    
+                IRubyObject result = runtime.getNil();
+    
+                Node firstWhenNode = iVisited.getFirstWhenNode();
+                while (firstWhenNode != null) {
+                    if (!(firstWhenNode instanceof WhenNode)) {
+                        return eval(context, firstWhenNode, self);
+                    }
+    
+                    WhenNode whenNode = (WhenNode) firstWhenNode;
+    
+                    if (whenNode.getExpressionNodes() instanceof ArrayNode) {
+                        for (Iterator iter = ((ArrayNode) whenNode.getExpressionNodes()).iterator(); iter
+                                .hasNext();) {
+                            Node tag = (Node) iter.next();
+    
+                            context.setPosition(tag.getPosition());
+                            if (isTrace(runtime)) {
+                                callTraceFunction(context, "line", self);
+                            }
+    
+                            // Ruby grammar has nested whens in a case body because of
+                            // productions case_body and when_args.
+                            if (tag instanceof WhenNode) {
+                                RubyArray expressions = (RubyArray) eval(context, ((WhenNode) tag)
+                                                .getExpressionNodes(), self);
+    
+                                for (int j = 0; j < expressions.getLength(); j++) {
+                                    IRubyObject condition = expressions.entry(j);
+    
+                                    if ((expression != null && condition.callMethod("===", expression)
+                                            .isTrue())
+                                            || (expression == null && condition.isTrue())) {
+                                        return eval(context, ((WhenNode) firstWhenNode).getBodyNode(), self);
+                                    }
+                                }
+                                continue;
+                            }
+    
+                            result = eval(context, tag, self);
+    
+                            if ((expression != null && result.callMethod("===", expression).isTrue())
+                                    || (expression == null && result.isTrue())) {
+                                return eval(context, whenNode.getBodyNode(), self);
+                            }
+                        }
+                    } else {
+                        result = eval(context, whenNode.getExpressionNodes(), self);
+    
+                        if ((expression != null && result.callMethod("===", expression).isTrue())
+                                || (expression == null && result.isTrue())) {
+                            return eval(context, ((WhenNode) firstWhenNode).getBodyNode(), self);
+                        }
+                    }
+    
+                    context.pollThreadEvents();
+    
+                    firstWhenNode = whenNode.getNextCase();
+                }
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.CLASSNODE: {
+                ClassNode iVisited = (ClassNode) node;
+                RubyClass superClass = getSuperClassFromNode(context, iVisited.getSuperNode(), self);
+                Node classNameNode = iVisited.getCPath();
+                String name = ((INameNode) classNameNode).getName();
+                RubyModule enclosingClass = getEnclosingModule(context, classNameNode, self);
+                RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
+    
+                if (context.getWrapper() != null) {
+                    rubyClass.extendObject(context.getWrapper());
+                    rubyClass.includeModule(context.getWrapper());
+                }
+                return evalClassDefinitionBody(context, iVisited.getBodyNode(), rubyClass, self);
+            }
+            case NodeTypes.CLASSVARASGNNODE: {
+                ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                RubyModule rubyClass = (RubyModule) context.peekCRef().getValue();
+    
+                if (rubyClass == null) {
+                    rubyClass = self.getMetaClass();
+                } else if (rubyClass.isSingleton()) {
+                    rubyClass = (RubyModule) rubyClass.getInstanceVariable("__attached__");
+                }
+    
+                rubyClass.setClassVar(iVisited.getName(), result);
+    
+                return result;
+            }
+            case NodeTypes.CLASSVARDECLNODE: {
+    
+                ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
+    
+                // FIXME: shouldn't we use cref here?
+                if (context.getRubyClass() == null) {
+                    throw runtime.newTypeError("no class/module to define class variable");
+                }
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                ((RubyModule) context.peekCRef().getValue()).setClassVar(iVisited.getName(),
+                        result);
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.CLASSVARNODE: {
+                ClassVarNode iVisited = (ClassVarNode) node;
+                RubyModule rubyClass = context.getRubyClass();
+    
+                if (rubyClass == null) {
+                    return self.getMetaClass().getClassVar(iVisited.getName());
+                } else if (!rubyClass.isSingleton()) {
+                    return rubyClass.getClassVar(iVisited.getName());
+                } else {
+                    RubyModule module = (RubyModule) rubyClass.getInstanceVariable("__attached__");
+    
+                    if (module != null) {
+                        return module.getClassVar(iVisited.getName());
+                    }
+                }
                 break;
             }
-        }
-    }
+            case NodeTypes.COLON2NODE: {
+                Colon2Node iVisited = (Colon2Node) node;
+                Node leftNode = iVisited.getLeftNode();
     
-    // works like old recursive evaluation, for Assignment and Defined visitors.
-    public IRubyObject begin(Node node) {
-        if (node == null) {
-            clearResult(); // must clear result in case we are still in use
-            
-            return runtime.getNil();
-        }
-        
-        // TODO: This is a minor hack that depends on us being called in recursion; safe for now
-        InstructionBundle previousStack = currentStack;
-        currentStack = null;
-        
-        begin2(node);
-        
-        try {
-            // TODO: once we're ready to have an external entity run this loop (i.e. thread scheduler) move this out
-            while (hasNext()) {                 
-                // invoke the next instruction
-                executeNext();
-            }
-        } catch (StackOverflowError soe) {
-                // TODO: perhaps a better place to catch this (although it will go away)
-                throw runtime.newSystemStackError("stack level too deep");
-        } finally {
-            currentStack = previousStack;
-            end();
-        }
-        
-        return getResult();
-    }
+                // TODO: Made this more colon3 friendly because of cpath production
+                // rule in grammar (it is convenient to think of them as the same thing
+                // at a grammar level even though evaluation is).
+                if (leftNode == null) {
+                    return runtime.getObject().getConstantFrom(iVisited.getName());
+                } else {
+                    IRubyObject result = eval(context, iVisited.getLeftNode(), self);
+                    if (result instanceof RubyModule) {
+                        return ((RubyModule) result).getConstantFrom(iVisited.getName());
+                    } else {
+                        return result.callMethod(iVisited.getName());
+                    }
+                }
+            }
+            case NodeTypes.COLON3NODE: {
+                Colon3Node iVisited = (Colon3Node) node;
+                return runtime.getObject().getConstantFrom(iVisited.getName());
+            }
+            case NodeTypes.CONSTDECLNODE: {
+                ConstDeclNode iVisited = (ConstDeclNode) node;
     
-    public void begin2(Node node) {
-        clearResult();
-        
-        if (node != null) {
-            // for each call to internalEval, push down new stacks (to isolate eval runs that still want to be logically separate
-            
-            // TODO: If we ever go pure iterative, this will need to be added back or re-thought. See TODO above in begin().
-            //if (currentStack != null) pushCurrentInstructionStack();
-            
-            addNodeInstruction(node);
-        }
-    }
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                IRubyObject module;
     
-    public void end() {
-        // FIXME: If we ever go pure iterative, this will need to be added back or re-thought. See TODO above in begin().
-        //if (instructionBundleStacks != null) popCurrentInstructionStack();
-    }
+                if (iVisited.getPathNode() != null) {
+                    module = eval(context, iVisited.getPathNode(), self);
+                } else {
+                    
     
-    private void handleNext(JumpException je) {
-        NextNode iVisited = (NextNode)je.getSecondaryData();
-        
-        while (!(getCurrentInstructionStack() == null || getCurrentInstructionStack().redoable)) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            if (ib.ensured) {
-                // exec ensured node, return to "nexting" afterwards
-                popCurrentInstruction();
-                //handlingException = true;
-                setCurrentException(je);
-                addInstruction(iVisited, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            popCurrentInstruction();
-        }
-        
-        if (getCurrentInstructionStack() == null ) {
-            // rethrow next to previous level
-            throw je;
-        } else {
-            // pop the redoable and continue
-            popCurrentInstruction();
-            setCurrentException(null);
-            //handlingException = false;
+                    // FIXME: why do we check RubyClass and then use CRef?
+                    if (context.getRubyClass() == null) {
+                        // TODO: wire into new exception handling mechanism
+                        throw runtime.newTypeError("no class/module to define constant");
+                    }
+                    module = (RubyModule) context.peekCRef().getValue();
+                }
+    
+                // FIXME: shouldn't we use the result of this set in setResult?
+                ((RubyModule) module).setConstant(iVisited.getName(), result);
+    
+                return result;
+            }
+            case NodeTypes.CONSTNODE: {
+                ConstNode iVisited = (ConstNode) node;
+                return context.getConstant(iVisited.getName());
+            }
+            case NodeTypes.DASGNNODE: {
+                DAsgnNode iVisited = (DAsgnNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                context.getCurrentDynamicVars().set(iVisited.getName(), result);
+    
+                return result;
+            }
+            case NodeTypes.DEFINEDNODE: {
+                DefinedNode iVisited = (DefinedNode) node;
+                String def = new DefinedVisitor(runtime).getDefinition(iVisited.getExpressionNode());
+                if (def != null) {
+                    return runtime.newString(def);
+                } else {
+                    return runtime.getNil();
+                }
+            }
+            case NodeTypes.DEFNNODE: {
+                DefnNode iVisited = (DefnNode) node;
+                
+                RubyModule containingClass = context.getRubyClass();
+    
+                if (containingClass == null) {
+                    throw runtime.newTypeError("No class to add method.");
+                }
+    
+                String name = iVisited.getName();
+                if (containingClass == runtime.getObject() && name.equals("initialize")) {
+                    runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
+                }
+    
+                Visibility visibility = context.getCurrentVisibility();
+                if (name.equals("initialize") || visibility.isModuleFunction()) {
+                    visibility = Visibility.PRIVATE;
+                }
+    
+                DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getBodyNode(),
+                        (ArgsNode) iVisited.getArgsNode(), visibility, context.peekCRef());
+    
+                iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
+    
+                containingClass.addMethod(name, newMethod);
+    
+                if (context.getCurrentVisibility().isModuleFunction()) {
+                    containingClass.getSingletonClass().addMethod(
+                            name,
+                            new WrapperCallable(containingClass.getSingletonClass(), newMethod,
+                                    Visibility.PUBLIC));
+                    containingClass.callMethod("singleton_method_added", runtime.newSymbol(name));
+                }
+    
+                // 'class << state.self' and 'class << obj' uses defn as opposed to defs
+                if (containingClass.isSingleton()) {
+                    ((MetaClass) containingClass).getAttachedObject().callMethod(
+                            "singleton_method_added", runtime.newSymbol(iVisited.getName()));
+                } else {
+                    containingClass.callMethod("method_added", runtime.newSymbol(name));
+                }
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.DEFSNODE: {
+                DefsNode iVisited = (DefsNode) node;
+                IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self);
+    
+                if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
+                    throw runtime.newSecurityError("Insecure; can't define singleton method.");
+                }
+                if (receiver.isFrozen()) {
+                    throw runtime.newFrozenError("object");
+                }
+                if (!receiver.singletonMethodsAllowed()) {
+                    throw runtime.newTypeError("can't define singleton method \"" + iVisited.getName()
+                            + "\" for " + receiver.getType());
+                }
+    
+                RubyClass rubyClass = receiver.getSingletonClass();
+    
+                if (runtime.getSafeLevel() >= 4) {
+                    ICallable method = (ICallable) rubyClass.getMethods().get(iVisited.getName());
+                    if (method != null) {
+                        throw runtime.newSecurityError("Redefining method prohibited.");
+                    }
+                }
+    
+                DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getBodyNode(),
+                        (ArgsNode) iVisited.getArgsNode(), Visibility.PUBLIC, context
+                                .peekCRef());
+    
+                iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
+    
+                rubyClass.addMethod(iVisited.getName(), newMethod);
+                receiver.callMethod("singleton_method_added", runtime.newSymbol(iVisited.getName()));
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.DOTNODE: {
+                DotNode iVisited = (DotNode) node;
+                return RubyRange.newRange(runtime, eval(context, iVisited.getBeginNode(), self), eval(context, iVisited
+                                .getEndNode(), self), iVisited.isExclusive());
+            }
+            case NodeTypes.DREGEXPNODE: {
+                DRegexpNode iVisited = (DRegexpNode) node;
+    
+                StringBuffer sb = new StringBuffer();
+                for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
+                    Node iterNode = (Node) iterator.next();
+    
+                    sb.append(eval(context, iterNode, self).toString());
+                }
+    
+                return RubyRegexp.newRegexp(runtime, sb.toString(), iVisited.getOptions(), null);
+            }
+            case NodeTypes.DSTRNODE: {
+                DStrNode iVisited = (DStrNode) node;
+    
+                StringBuffer sb = new StringBuffer();
+                for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
+                    Node iterNode = (Node) iterator.next();
+    
+                    sb.append(eval(context, iterNode, self).toString());
+                }
+    
+                return runtime.newString(sb.toString());
+            }
+            case NodeTypes.DSYMBOLNODE: {
+                DSymbolNode iVisited = (DSymbolNode) node;
+    
+                StringBuffer sb = new StringBuffer();
+                for (Iterator iterator = iVisited.getNode().iterator(); iterator.hasNext();) {
+                    Node iterNode = (Node) iterator.next();
+    
+                    sb.append(eval(context, iterNode, self).toString());
+                }
+    
+                return runtime.newSymbol(sb.toString());
+            }
+            case NodeTypes.DVARNODE: {
+                DVarNode iVisited = (DVarNode) node;
+                return context.getDynamicValue(iVisited.getName());
+            }
+            case NodeTypes.DXSTRNODE: {
+                DXStrNode iVisited = (DXStrNode) node;
+    
+                StringBuffer sb = new StringBuffer();
+                for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
+                    Node iterNode = (Node) iterator.next();
+    
+                    sb.append(eval(context, iterNode, self).toString());
+                }
+    
+                return self.callMethod("`", runtime.newString(sb.toString()));
+            }
+            case NodeTypes.ENSURENODE: {
+                EnsureNode iVisited = (EnsureNode) node;
+    
+                // save entering the try if there's nothing to ensure
+                if (iVisited.getEnsureNode() != null) {
+                    IRubyObject result = runtime.getNil();
+    
+                    try {
+                        result = eval(context, iVisited.getBodyNode(), self);
+                    } finally {
+                        eval(context, iVisited.getEnsureNode(), self);
+                    }
+    
+                    return result;
+                }
+    
+                return eval(context, iVisited.getBodyNode(), self);
+            }
+            case NodeTypes.EVSTRNODE: {
+                EvStrNode iVisited = (EvStrNode) node;
+    
+                return eval(context, iVisited.getBody(), self);
+            }
+            case NodeTypes.FALSENODE: {
+                context.pollThreadEvents();
+                return runtime.getFalse();
+            }
+            case NodeTypes.FCALLNODE: {
+                FCallNode iVisited = (FCallNode) node;
+                
+    
+                context.beginCallArgs();
+                IRubyObject[] args;
+                try {
+                    args = setupArgs(context, iVisited.getArgsNode(), self);
+                } finally {
+                    context.endCallArgs();
+                }
+    
+                return self.callMethod(iVisited.getName(), args, CallType.FUNCTIONAL);
+            }
+            case NodeTypes.FIXNUMNODE: {
+                FixnumNode iVisited = (FixnumNode) node;
+                return runtime.newFixnum(iVisited.getValue());
+            }
+            case NodeTypes.FLIPNODE: {
+                FlipNode iVisited = (FlipNode) node;
+                IRubyObject result = runtime.getNil();
+    
+                if (iVisited.isExclusive()) {
+                    if (!context.getFrameScope().getValue(iVisited.getCount()).isTrue()) {
+                        result = eval(context, iVisited.getBeginNode(), self).isTrue() ? runtime.getFalse()
+                                : runtime.getTrue();
+                        context.getFrameScope().setValue(iVisited.getCount(), result);
+                        return result;
+                    } else {
+                        if (eval(context, iVisited.getEndNode(), self).isTrue()) {
+                            context.getFrameScope().setValue(iVisited.getCount(), runtime.getFalse());
+                        }
+                        return runtime.getTrue();
+                    }
+                } else {
+                    if (!context.getFrameScope().getValue(iVisited.getCount()).isTrue()) {
+                        if (eval(context, iVisited.getBeginNode(), self).isTrue()) {
+                            context.getFrameScope().setValue(
+                                    iVisited.getCount(),
+                                    eval(context, iVisited.getEndNode(), self).isTrue() ? runtime.getFalse()
+                                            : runtime.getTrue());
+                            return runtime.getTrue();
+                        } else {
+                            return runtime.getFalse();
+                        }
+                    } else {
+                        if (eval(context, iVisited.getEndNode(), self).isTrue()) {
+                            context.getFrameScope().setValue(iVisited.getCount(), runtime.getFalse());
+                        }
+                        return runtime.getTrue();
+                    }
+                }
+            }
+            case NodeTypes.FLOATNODE: {
+                FloatNode iVisited = (FloatNode) node;
+                return RubyFloat.newFloat(runtime, iVisited.getValue());
+            }
+            case NodeTypes.FORNODE: {
+                ForNode iVisited = (ForNode) node;
+                
+    
+                context.preForLoopEval(Block
+                        .createBlock(iVisited.getVarNode(), iVisited.getCallable(), self));
+    
+                try {
+                    while (true) {
+                        try {
+                            ISourcePosition position = context.getPosition();
+                            context.beginCallArgs();
+    
+                            IRubyObject recv = null;
+                            try {
+                                recv = eval(context, iVisited.getIterNode(), self);
+                            } finally {
+                                context.setPosition(position);
+                                context.endCallArgs();
+                            }
+    
+                            return recv.callMethod("each", IRubyObject.NULL_ARRAY, CallType.NORMAL);
+                        } catch (JumpException je) {
+                            switch (je.getJumpType().getTypeId()) {
+                            case JumpType.RETRY:
+                                // do nothing, allow loop to retry
+                                break;
+                            default:
+                                throw je;
+                            }
+                        }
+                    }
+                } catch (JumpException je) {
+                    switch (je.getJumpType().getTypeId()) {
+                    case JumpType.BREAK:
+                        IRubyObject breakValue = (IRubyObject) je.getPrimaryData();
+    
+                        return breakValue == null ? runtime.getNil() : breakValue;
+                    default:
+                        throw je;
+                    }
+                } finally {
+                    context.postForLoopEval();
+                }
+            }
+            case NodeTypes.GLOBALASGNNODE: {
+                GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+    
+                runtime.getGlobalVariables().set(iVisited.getName(), result);
+    
+                return result;
+            }
+            case NodeTypes.GLOBALVARNODE: {
+                GlobalVarNode iVisited = (GlobalVarNode) node;
+                return runtime.getGlobalVariables().get(iVisited.getName());
+            }
+            case NodeTypes.HASHNODE: {
+                HashNode iVisited = (HashNode) node;
+    
+                Map hash = null;
+                if (iVisited.getListNode() != null) {
+                    hash = new HashMap(iVisited.getListNode().size() / 2);
+    
+                    for (Iterator iterator = iVisited.getListNode().iterator(); iterator.hasNext();) {
+                        // insert all nodes in sequence, hash them in the final instruction
+                        // KEY
+                        IRubyObject key = eval(context, (Node) iterator.next(), self);
+                        IRubyObject value = eval(context, (Node) iterator.next(), self);
+    
+                        hash.put(key, value);
+                    }
+                }
+    
+                if (hash == null) {
+                    return RubyHash.newHash(runtime);
+                }
+    
+                return RubyHash.newHash(runtime, hash, runtime.getNil());
+            }
+            case NodeTypes.IFNODE: {
+                IfNode iVisited = (IfNode) node;
+                IRubyObject result = eval(context, iVisited.getCondition(), self);
+    
+                if (result.isTrue()) {
+                    return eval(context, iVisited.getThenBody(), self);
+                } else {
+                    return eval(context, iVisited.getElseBody(), self);
+                }
+            }
+            case NodeTypes.INSTASGNNODE: {
+                InstAsgnNode iVisited = (InstAsgnNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                self.setInstanceVariable(iVisited.getName(), result);
+    
+                return result;
+            }
+            case NodeTypes.INSTVARNODE: {
+                InstVarNode iVisited = (InstVarNode) node;
+                IRubyObject variable = self.getInstanceVariable(iVisited.getName());
+    
+                return variable == null ? runtime.getNil() : variable;
+            }
+                //                case NodeTypes.ISCOPINGNODE:
+                //                EvaluateVisitor.iScopingNodeVisitor.execute(this, node);
+                //                break;
+            case NodeTypes.ITERNODE: {
+                IterNode iVisited = (IterNode) node;
+                
+    
+                context.preIterEval(Block.createBlock(iVisited.getVarNode(), iVisited.getCallable(), self));
+                try {
+                    while (true) {
+                        try {
+                            context.setBlockAvailable();
+                            return eval(context, iVisited.getIterNode(), self);
+                        } catch (JumpException je) {
+                            switch (je.getJumpType().getTypeId()) {
+                            case JumpType.RETRY:
+                                // allow loop to retry
+                                break;
+                            default:
+                                throw je;
+                            }
+                        } finally {
+                            context.clearBlockAvailable();
+                        }
+                    }
+                } catch (JumpException je) {
+                    switch (je.getJumpType().getTypeId()) {
+                    case JumpType.BREAK:
+                        IRubyObject breakValue = (IRubyObject) je.getPrimaryData();
+    
+                        return breakValue == null ? runtime.getNil() : breakValue;
+                    default:
+                        throw je;
+                    }
+                } finally {
+                    context.postIterEval();
+                }
+            }
+            case NodeTypes.LOCALASGNNODE: {
+                LocalAsgnNode iVisited = (LocalAsgnNode) node;
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+                context.getFrameScope().setValue(iVisited.getCount(), result);
+                return result;
+            }
+            case NodeTypes.LOCALVARNODE: {
+                LocalVarNode iVisited = (LocalVarNode) node;
+                return context.getFrameScope().getValue(iVisited.getCount());
+            }
+            case NodeTypes.MATCH2NODE: {
+                Match2Node iVisited = (Match2Node) node;
+                IRubyObject recv = eval(context, iVisited.getReceiverNode(), self);
+                IRubyObject value = eval(context, iVisited.getValueNode(), self);
+    
+                return ((RubyRegexp) recv).match(value);
+            }
+            case NodeTypes.MATCH3NODE: {
+                Match3Node iVisited = (Match3Node) node;
+                IRubyObject recv = eval(context, iVisited.getReceiverNode(), self);
+                IRubyObject value = eval(context, iVisited.getValueNode(), self);
+    
+                if (value instanceof RubyString) {
+                    return ((RubyRegexp) recv).match(value);
+                } else {
+                    return value.callMethod("=~", recv);
+                }
+            }
+            case NodeTypes.MATCHNODE: {
+                MatchNode iVisited = (MatchNode) node;
+                return ((RubyRegexp) eval(context, iVisited.getRegexpNode(), self)).match2();
+            }
+            case NodeTypes.MODULENODE: {
+                ModuleNode iVisited = (ModuleNode) node;
+                Node classNameNode = iVisited.getCPath();
+                String name = ((INameNode) classNameNode).getName();
+                RubyModule enclosingModule = getEnclosingModule(context, classNameNode, self);
+    
+                if (enclosingModule == null) {
+                    throw runtime.newTypeError("no outer class/module");
+                }
+    
+                RubyModule module;
+                if (enclosingModule == runtime.getObject()) {
+                    module = runtime.getOrCreateModule(name);
+                } else {
+                    module = enclosingModule.defineModuleUnder(name);
+                }
+                return evalClassDefinitionBody(context, iVisited.getBodyNode(), module, self);
+            }
+            case NodeTypes.MULTIPLEASGNNODE: {
+                MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
+                return new AssignmentVisitor(self).assign(iVisited, eval(context,
+                        iVisited.getValueNode(), self), false);
+            }
+            case NodeTypes.NEWLINENODE: {
+                NewlineNode iVisited = (NewlineNode) node;
+    
+                // something in here is used to build up ruby stack trace...
+                context.setPosition(iVisited.getPosition());
+    
+                if (isTrace(runtime)) {
+                    callTraceFunction(context, "line", self);
+                }
+    
+                // TODO: do above but not below for additional newline nodes
+                return eval(context, iVisited.getNextNode(), self);
+            }
+            case NodeTypes.NEXTNODE: {
+                NextNode iVisited = (NextNode) node;
+    
+                context.pollThreadEvents();
+    
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+    
+                // now used as an interpreter event
+                JumpException je = new JumpException(JumpException.JumpType.NextJump);
+    
+                je.setPrimaryData(result);
+                je.setSecondaryData(iVisited);
+    
+                //state.setCurrentException(je);
+                throw je;
+            }
+            case NodeTypes.NILNODE:
+                return runtime.getNil();
+            case NodeTypes.NOTNODE: {
+                NotNode iVisited = (NotNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getConditionNode(), self);
+                return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
+            }
+            case NodeTypes.NTHREFNODE: {
+                NthRefNode iVisited = (NthRefNode) node;
+                return RubyRegexp.nth_match(iVisited.getMatchNumber(), context.getBackref());
+            }
+            case NodeTypes.OPASGNANDNODE: {
+                BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
+    
+                // add in reverse order
+                IRubyObject result = eval(context, iVisited.getFirstNode(), self);
+                if (!result.isTrue()) return result;
+                return eval(context, iVisited.getSecondNode(), self);
+            }
+            case NodeTypes.OPASGNNODE: {
+                OpAsgnNode iVisited = (OpAsgnNode) node;
+                IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self);
+                IRubyObject value = receiver.callMethod(iVisited.getVariableName());
+    
+                if (iVisited.getOperatorName().equals("||")) {
+                    if (value.isTrue()) {
+                        return value;
+                    }
+                    value = eval(context, iVisited.getValueNode(), self);
+                } else if (iVisited.getOperatorName().equals("&&")) {
+                    if (!value.isTrue()) {
+                        return value;
+                    }
+                    value = eval(context, iVisited.getValueNode(), self);
+                } else {
+                    value = value.callMethod(iVisited.getOperatorName(), eval(context,
+                            iVisited.getValueNode(), self));
+                }
+    
+                receiver.callMethod(iVisited.getVariableName() + "=", value);
+    
+                context.pollThreadEvents();
+    
+                return value;
+            }
+            case NodeTypes.OPASGNORNODE: {
+                OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
+                String def = new DefinedVisitor(runtime).getDefinition(iVisited.getFirstNode());
+    
+                IRubyObject result = runtime.getNil();
+                if (def != null) {
+                    result = eval(context, iVisited.getFirstNode(), self);
+                }
+                if (!result.isTrue()) {
+                    result = eval(context, iVisited.getSecondNode(), self);
+                }
+    
+                return result;
+            }
+            case NodeTypes.OPELEMENTASGNNODE: {
+                OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
+                IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self);
+    
+                IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
+    
+                IRubyObject firstValue = receiver.callMethod("[]", args);
+    
+                if (iVisited.getOperatorName().equals("||")) {
+                    if (firstValue.isTrue()) {
+                        return firstValue;
+                    }
+                    firstValue = eval(context, iVisited.getValueNode(), self);
+                } else if (iVisited.getOperatorName().equals("&&")) {
+                    if (!firstValue.isTrue()) {
+                        return firstValue;
+                    }
+                    firstValue = eval(context, iVisited.getValueNode(), self);
+                } else {
+                    firstValue = firstValue.callMethod(iVisited.getOperatorName(), eval(context, iVisited
+                                    .getValueNode(), self));
+                }
+    
+                IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
+                System.arraycopy(args, 0, expandedArgs, 0, args.length);
+                expandedArgs[expandedArgs.length - 1] = firstValue;
+                return receiver.callMethod("[]=", expandedArgs);
+            }
+            case NodeTypes.OPTNNODE: {
+                OptNNode iVisited = (OptNNode) node;
+    
+                IRubyObject result = runtime.getNil();
+                while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
+                    loop: while (true) { // Used for the 'redo' command
+                        try {
+                            result = eval(context, iVisited.getBodyNode(), self);
+                            break;
+                        } catch (JumpException je) {
+                            switch (je.getJumpType().getTypeId()) {
+                            case JumpType.REDO:
+                                // do nothing, this iteration restarts
+                                break;
+                            case JumpType.NEXT:
+                                // recheck condition
+                                break loop;
+                            case JumpType.BREAK:
+                                // end loop
+                                return (IRubyObject) je.getPrimaryData();
+                            default:
+                                throw je;
+                            }
+                        }
+                    }
+                }
+                return result;
+            }
+            case NodeTypes.ORNODE: {
+                OrNode iVisited = (OrNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getFirstNode(), self);
+    
+                if (!result.isTrue()) {
+                    result = eval(context, iVisited.getSecondNode(), self);
+                }
+    
+                return result;
+            }
+                //                case NodeTypes.POSTEXENODE:
+                //                EvaluateVisitor.postExeNodeVisitor.execute(this, node);
+                //                break;
+            case NodeTypes.REDONODE: {
+                context.pollThreadEvents();
+    
+                // now used as an interpreter event
+                JumpException je = new JumpException(JumpException.JumpType.RedoJump);
+    
+                je.setSecondaryData(node);
+    
+                throw je;
+            }
+            case NodeTypes.REGEXPNODE: {
+                RegexpNode iVisited = (RegexpNode) node;
+    
+                // FIXME: don't pass null
+                return RubyRegexp.newRegexp(runtime, iVisited.getPattern(), null);
+            }
+            case NodeTypes.RESCUEBODYNODE: {
+                RescueBodyNode iVisited = (RescueBodyNode) node;
+                return eval(context, iVisited.getBodyNode(), self);
+            }
+            case NodeTypes.RESCUENODE: {
+                RescueNode iVisited = (RescueNode)node;
+                RescuedBlock : while (true) {
+                    try {
+                        // Execute rescue block
+                        IRubyObject result = eval(context, iVisited.getBodyNode(), self);
+
+                        // If no exception is thrown execute else block
+                        if (iVisited.getElseNode() != null) {
+                            if (iVisited.getRescueNode() == null ||
+                                    iVisited.getRescueNode().getExceptionNodes().childNodes().isEmpty()) {
+                                runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
+                            }
+                            result = eval(context, iVisited.getElseNode(), self);
+                        }
+
+                        return result;
+                    } catch (RaiseException raiseJump) {
+                        RubyException raisedException = raiseJump.getException();
+                        // TODO: Rubicon TestKernel dies without this line.  A cursory glance implies we
+                        // falsely set $! to nil and this sets it back to something valid.  This should 
+                        // get fixed at the same time we address bug #1296484.
+                        runtime.getGlobalVariables().set("$!", raisedException);
+
+                        RescueBodyNode rescueNode = iVisited.getRescueNode();
+
+                        while (rescueNode != null) {
+                            Node  exceptionNodes = rescueNode.getExceptionNodes();
+                            ListNode exceptionNodesList;
+                            
+                            if (exceptionNodes instanceof SplatNode) {                    
+                                exceptionNodesList = (ListNode) eval(context, exceptionNodes, self);
+                            } else {
+                                exceptionNodesList = (ListNode) exceptionNodes;
+                            }
+                            
+                            if (isRescueHandled(context, raisedException, exceptionNodesList, self)) {
+                                try {
+                                    return eval(context, rescueNode, self);
+                                } catch (JumpException je) {
+                                    if (je.getJumpType() == JumpException.JumpType.RetryJump) {
+                                        // should be handled in the finally block below
+                                        //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
+                                        //state.threadContext.setRaisedException(null);
+                                        continue RescuedBlock;
+                                        
+                                    } else {
+                                        throw je;
+                                    }
+                                }
+                            }
+                            
+                            rescueNode = rescueNode.getOptRescueNode();
+                        }
+
+                        // no takers; bubble up
+                        throw raiseJump;
+                    } finally {
+                        // clear exception when handled or retried
+                        runtime.getGlobalVariables().set("$!", runtime.getNil());
+                    }
+                }
+            }
+            case NodeTypes.RETRYNODE: {
+                context.pollThreadEvents();
+    
+                JumpException je = new JumpException(JumpException.JumpType.RetryJump);
+    
+                throw je;
+            }
+            case NodeTypes.RETURNNODE: {
+                ReturnNode iVisited = (ReturnNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getValueNode(), self);
+    
+                JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
+    
+                je.setPrimaryData(iVisited.getTarget());
+                je.setSecondaryData(result);
+                je.setTertiaryData(iVisited);
+    
+                throw je;
+            }
+            case NodeTypes.SCLASSNODE: {
+                SClassNode iVisited = (SClassNode) node;
+                IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self);
+    
+                RubyClass singletonClass;
+    
+                if (receiver.isNil()) {
+                    singletonClass = runtime.getNilClass();
+                } else if (receiver == runtime.getTrue()) {
+                    singletonClass = runtime.getClass("True");
+                } else if (receiver == runtime.getFalse()) {
+                    singletonClass = runtime.getClass("False");
+                } else {
+                    if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
+                        throw runtime.newSecurityError("Insecure: can't extend object.");
+                    }
+    
+                    singletonClass = receiver.getSingletonClass();
+                }
+    
+                
+    
+                if (context.getWrapper() != null) {
+                    singletonClass.extendObject(context.getWrapper());
+                    singletonClass.includeModule(context.getWrapper());
+                }
+    
+                return evalClassDefinitionBody(context, iVisited.getBodyNode(), singletonClass, self);
+            }
+            case NodeTypes.SCOPENODE: {
+                ScopeNode iVisited = (ScopeNode) node;
+                
+    
+                context.preScopedBody(iVisited.getLocalNames());
+                try {
+                    return eval(context, iVisited.getBodyNode(), self);
+                } finally {
+                    context.postScopedBody();
+                }
+            }
+            case NodeTypes.SELFNODE:
+                return self;
+            case NodeTypes.SPLATNODE: {
+                SplatNode iVisited = (SplatNode) node;
+                return splatValue(eval(context, iVisited.getValue(), self));
+            }
+                ////                case NodeTypes.STARNODE:
+                ////                EvaluateVisitor.starNodeVisitor.execute(this, node);
+                ////                break;
+            case NodeTypes.STRNODE: {
+                StrNode iVisited = (StrNode) node;
+                return runtime.newString(iVisited.getValue());
+            }
+            case NodeTypes.SUPERNODE: {
+                SuperNode iVisited = (SuperNode) node;
+                
+    
+                if (context.getFrameLastClass() == null) {
+                    throw runtime.newNameError("Superclass method '" + context.getFrameLastFunc()
+                            + "' disabled.");
+                }
+    
+                context.beginCallArgs();
+    
+                IRubyObject[] args = null;
+                try {
+                    args = setupArgs(context, iVisited.getArgsNode(), self);
+                } finally {
+                    context.endCallArgs();
+                }
+                return context.callSuper(args);
+            }
+            case NodeTypes.SVALUENODE: {
+                SValueNode iVisited = (SValueNode) node;
+                return aValueSplat(eval(context, iVisited.getValue(), self));
+            }
+            case NodeTypes.SYMBOLNODE: {
+                SymbolNode iVisited = (SymbolNode) node;
+                return runtime.newSymbol(iVisited.getName());
+            }
+            case NodeTypes.TOARYNODE: {
+                ToAryNode iVisited = (ToAryNode) node;
+                return aryToAry(eval(context, iVisited.getValue(), self));
+            }
+            case NodeTypes.TRUENODE: {
+                context.pollThreadEvents();
+                return runtime.getTrue();
+            }
+            case NodeTypes.UNDEFNODE: {
+                UndefNode iVisited = (UndefNode) node;
+                
+    
+                if (context.getRubyClass() == null) {
+                    throw runtime
+                            .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
+                }
+                context.getRubyClass().undef(iVisited.getName());
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.UNTILNODE: {
+                UntilNode iVisited = (UntilNode) node;
+    
+                IRubyObject result = runtime.getNil();
+                
+                while (!(result = eval(context, iVisited.getConditionNode(), self)).isTrue()) {
+                    loop: while (true) { // Used for the 'redo' command
+                        try {
+                            result = eval(context, iVisited.getBodyNode(), self);
+                            break loop;
+                        } catch (JumpException je) {
+                            switch (je.getJumpType().getTypeId()) {
+                            case JumpType.REDO:
+                                continue;
+                            case JumpType.NEXT:
+                                break loop;
+                            case JumpType.BREAK:
+                                return (IRubyObject) je.getPrimaryData();
+                            default:
+                                throw je;
+                            }
+                        }
+                    }
+                }
+                
+                return result;
+            }
+            case NodeTypes.VALIASNODE: {
+                VAliasNode iVisited = (VAliasNode) node;
+                runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
+    
+                return runtime.getNil();
+            }
+            case NodeTypes.VCALLNODE: {
+                VCallNode iVisited = (VCallNode) node;
+                return self.callMethod(iVisited.getMethodName(), IRubyObject.NULL_ARRAY,
+                        CallType.VARIABLE);
+            }
+            case NodeTypes.WHENNODE:
+                assert false;
+                return null;
+            case NodeTypes.WHILENODE: {
+                WhileNode iVisited = (WhileNode) node;
+    
+                IRubyObject result = runtime.getNil();
+                boolean firstTest = iVisited.evaluateAtStart();
+                
+                while (!firstTest || (result = eval(context, iVisited.getConditionNode(), self)).isTrue()) {
+                    firstTest = true;
+                    loop: while (true) { // Used for the 'redo' command
+                        try {
+                            eval(context, iVisited.getBodyNode(), self);
+                            break loop;
+                        } catch (JumpException je) {
+                            switch (je.getJumpType().getTypeId()) {
+                            case JumpType.REDO:
+                                continue;
+                            case JumpType.NEXT:
+                                break loop;
+                            case JumpType.BREAK:
+                                return result;
+                            default:
+                                throw je;
+                            }
+                        }
+                    }
+                }
+                
+                return result;
+            }
+            case NodeTypes.XSTRNODE: {
+                XStrNode iVisited = (XStrNode) node;
+                return self.callMethod("`", runtime.newString(iVisited.getValue()));
+            }
+            case NodeTypes.YIELDNODE: {
+                YieldNode iVisited = (YieldNode) node;
+    
+                IRubyObject result = eval(context, iVisited.getArgsNode(), self);
+                if (iVisited.getArgsNode() == null) {
+                    result = null;
+                }
+    
+                return  context.yieldCurrentBlock(result, null, null,
+                        iVisited.getCheckState());
+                
+            }
+            case NodeTypes.ZARRAYNODE: {
+                return runtime.newArray();
+            }
+            case NodeTypes.ZSUPERNODE: {
+                
+    
+                if (context.getFrameLastClass() == null) {
+                    throw runtime.newNameError("superclass method '" + context.getFrameLastFunc()
+                            + "' disabled");
+                }
+    
+                return context.callSuper(context.getFrameArgs());
+            }
+            }
+        } catch (StackOverflowError sfe) {
+            throw runtime.newSystemStackError("stack level too deep");
         }
+
+        return runtime.getNil();
     }
-    
-    private void handleRedo(JumpException je) {
-        RedoNode iVisited = (RedoNode)je.getSecondaryData();
-        
-        while (!(getCurrentInstructionStack() == null  || getCurrentInstructionStack().redoable)) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            if (ib.ensured) {
-                // exec ensured node, return to "redoing" afterwards
-                popCurrentInstruction();
-                //handlingException = true;
-                setCurrentException(je);
-                addInstruction(iVisited, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            popCurrentInstruction();
+
+    private static IRubyObject aryToAry(IRubyObject value) {
+        if (value instanceof RubyArray) {
+            return value;
         }
-        
-        if (getCurrentInstructionStack() == null ) {
-            // rethrow next to previous level
-            throw je;
-        } else {
-            // pop the redoable leave the redo body
-            Node nodeToRedo = (Node)peekCurrentInstructionContext();
-            popCurrentInstruction();
-            addRedoMarker(nodeToRedo);
-            addNodeInstruction(nodeToRedo);
-            //setCurrentException(null);
-            //handlingException = false;
+
+        if (value.respondsTo("to_ary")) {
+            return value.convertToType("Array", "to_ary", false);
         }
+
+        return value.getRuntime().newArray(value);
     }
-    
-    private void handleBreak(JumpException je) {
-        BreakNode iVisited = (BreakNode)je.getSecondaryData();
-        
-//      pop everything but nearest breakable
-        while (!(getCurrentInstructionStack() == null  || getCurrentInstructionStack().breakable)) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            if (ib.ensured) {
-                // exec ensured node, return to "breaking" afterwards
-                popCurrentInstruction();
-                setCurrentException(je);
-                addInstruction(iVisited, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            popCurrentInstruction();
-        }
-        
-        if (getCurrentInstructionStack() == null ) {
-            // rethrow to next level
-            throw je;
-        } else {
-            // pop breakable and push previously-calculated break value
-            popCurrentInstruction();
-            setResult((IRubyObject)getCurrentException().getPrimaryData());
-            setCurrentException(null);
+
+    /** Evaluates the body in a class or module definition statement.
+     *
+     */
+    private static IRubyObject evalClassDefinitionBody(ThreadContext context, ScopeNode iVisited, RubyModule type,
+            IRubyObject self) {
+        IRuby runtime = context.getRuntime();
+        context.preClassEval(iVisited.getLocalNames(), type);
+
+        try {
+            if (isTrace(runtime)) {
+                callTraceFunction(context, "class", type);
+            }
+
+            return eval(context, iVisited.getBodyNode(), type);
+        } finally {
+            context.postClassEval();
+
+            if (isTrace(runtime)) {
+                callTraceFunction(context, "end", null);
+            }
         }
     }
-    
-    private void handleRaise(JumpException je) {
-        RaiseException re = (RaiseException)je;
-        RubyException raisedException = re.getException();
-        
-        // clear out result aggregator, in case we were aggregating
-        flushResults();
-        
-        // TODO: Rubicon TestKernel dies without this line.  A cursory glance implies we
-        // falsely set $! to nil and this sets it back to something valid.  This should 
-        // get fixed at the same time we address bug #1296484.
-        runtime.getGlobalVariables().set("$!", raisedException);
-
-        // FIXME: don't use the raise rethrower; work with the exception rethrower like all other handlers do
-        
-//      pop everything but nearest rescuable
-        while (!(getCurrentInstructionStack() == null  || getCurrentInstructionStack().rescuable)) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            if (ib.ensured) {
-                // exec ensured node, return to "breaking" afterwards
-                popCurrentInstruction();
-                setCurrentException(je);
-                addInstruction(ib.instructionContext, raiseRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            popCurrentInstruction();
+
+    private static RubyClass getSuperClassFromNode(ThreadContext context, Node superNode, IRubyObject self) {
+        if (superNode == null) {
+            return null;
         }
-        
-        if (getCurrentInstructionStack() == null ) {
-            // no rescuers, throw exception to next level
-            throw re;
+        RubyClass superClazz;
+        IRuby runtime = context.getRuntime();
+        try {
+            superClazz = (RubyClass) eval(context, superNode, self);
+        } catch (Exception e) {
+            if (superNode instanceof INameNode) {
+                String name = ((INameNode) superNode).getName();
+                throw runtime.newTypeError("undefined superclass '" + name + "'");
+            }
+            throw runtime.newTypeError("superclass undefined");
         }
-        
-        // we're at rescuer now
-        RescueNode iVisited = (RescueNode)getCurrentInstructionStack().instructionContext;
-        popCurrentInstruction();
-        RescueBodyNode rescueBodyNode = iVisited.getRescueNode();
-
-        while (rescueBodyNode != null) {
-            Node exceptionNodes = rescueBodyNode.getExceptionNodes();
-            ListNode exceptionNodesList;
-            
-            // need to make these iterative
-            if (exceptionNodes instanceof SplatNode) {                    
-                exceptionNodesList = (ListNode) begin(exceptionNodes);
-            } else {
-                exceptionNodesList = (ListNode) exceptionNodes;
-            }
-            
-            if (isRescueHandled(raisedException, exceptionNodesList)) {
-                addRetriableInstruction(iVisited);
-                addNodeInstruction(rescueBodyNode);
-                setCurrentException(null);
-                clearResult();
-                return;
-            }
-            
-            rescueBodyNode = rescueBodyNode.getOptRescueNode();
+        if (superClazz instanceof MetaClass) {
+            throw runtime.newTypeError("can't make subclass of virtual class");
         }
-
-        // no takers; bubble up
-        throw je;
+        return superClazz;
     }
-    
-    private void handleRetry(JumpException je) {
-//      pop everything but nearest rescuable
-        while (!(getCurrentInstructionStack() == null  || getCurrentInstructionStack().retriable)) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            
-            // ensured fires when retrying a method?
-            if (ib.ensured) {
-                // exec ensured node, return to "breaking" afterwards
-                popCurrentInstruction();
-                setCurrentException(je);
-                addInstruction(ib.instructionContext, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            
-            popCurrentInstruction();
-        }
-        
-        if (getCurrentInstructionStack() == null ) {
-            throw je;
-        }
-        
-        InstructionBundle ib = getCurrentInstructionStack();
-        
-        popCurrentInstruction();
-        
-        // re-run the retriable node, clearing any exceptions
-        runtime.getGlobalVariables().set("$!", runtime.getNil());
-        setCurrentException(null);
-        addNodeInstruction(ib.instructionContext);
+
+    /**
+     * Helper method.
+     *
+     * test if a trace function is avaiable.
+     *
+     */
+    private static boolean isTrace(IRuby runtime) {
+        return runtime.getTraceFunction() != null;
     }
-    
-    private void handleReturn(JumpException je) {
-        // make sure ensures fire
-        while (getCurrentInstructionStack() != null ) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            
-            if (ib.ensured) {
-                // exec ensured node, return to "breaking" afterwards
-                popCurrentInstruction();
-                setResult((IRubyObject)je.getSecondaryData());
-                setCurrentException(je);
-                addInstruction(ib.instructionContext, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            
-            popCurrentInstruction();
-        }
-        throw je;
+
+    private static void callTraceFunction(ThreadContext context, String event, IRubyObject zelf) {
+        IRuby runtime = context.getRuntime();
+        String name = context.getFrameLastFunc();
+        RubyModule type = context.getFrameLastClass();
+        runtime.callTraceFunction(event, context.getPosition(), zelf, name, type);
     }
-    
-    private void handleThrow(JumpException je) {
-        while (getCurrentInstructionStack() != null ) {
-            InstructionBundle ib = getCurrentInstructionStack();
-            
-            if (ib.ensured) {
-                // exec ensured node, return to "breaking" afterwards
-                popCurrentInstruction();
-                setCurrentException(je);
-                addInstruction(ib.instructionContext, exceptionRethrower);
-                addInstructionBundle(ib);
-                return;
-            }
-            
-            popCurrentInstruction();
+
+    private static IRubyObject splatValue(IRubyObject value) {
+        if (value.isNil()) {
+            return value.getRuntime().newArray(value);
         }
-        throw je;
+
+        return arrayValue(value);
     }
-    
-    private boolean isRescueHandled(RubyException currentException, ListNode exceptionNodes) {
-        if (exceptionNodes == null) {
-            return currentException.isKindOf(runtime.getClass("StandardError"));
+
+    private static IRubyObject aValueSplat(IRubyObject value) {
+        IRuby runtime = value.getRuntime();
+        if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
+            return runtime.getNil();
         }
-        
-        ThreadContext tc = getThreadContext();
 
-        tc.beginCallArgs();
+        RubyArray array = (RubyArray) value;
 
-        IRubyObject[] args = null;
-        try {
-            args = setupArgs(runtime, tc, exceptionNodes);
-        } finally {
-            tc.endCallArgs();
-        }
+        return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
+    }
 
-        for (int i = 0; i < args.length; i++) {
-            if (! args[i].isKindOf(runtime.getClass("Module"))) {
-                throw runtime.newTypeError("class or module required for rescue clause");
+    private static RubyArray arrayValue(IRubyObject value) {
+        IRubyObject newValue = value.convertToType("Array", "to_ary", false);
+
+        if (newValue.isNil()) {
+            IRuby runtime = value.getRuntime();
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
+            // remove this hack too.
+            if (value.getType().searchMethod("to_a").getImplementationClass() != runtime
+                    .getKernel()) {
+                newValue = value.convertToType("Array", "to_a", false);
+                if (newValue.getType() != runtime.getClass("Array")) {
+                    throw runtime.newTypeError("`to_a' did not return Array");
+                }
+            } else {
+                newValue = runtime.newArray(value);
             }
-            if (args[i].callMethod("===", currentException).isTrue())
-                return true;
         }
-        return false;
+
+        return (RubyArray) newValue;
     }
 
-    private IRubyObject[] setupArgs(IRuby runtime, ThreadContext context, Node node) {
+    private static IRubyObject[] setupArgs(ThreadContext context, Node node,
+            IRubyObject self) {
         if (node == null) {
             return IRubyObject.NULL_ARRAY;
         }
 
         if (node instanceof ArrayNode) {
             ISourcePosition position = context.getPosition();
             ArrayList list = new ArrayList(((ArrayNode) node).size());
-            
-            for (Iterator iter=((ArrayNode)node).iterator(); iter.hasNext();){
+
+            for (Iterator iter = ((ArrayNode) node).iterator(); iter.hasNext();) {
                 final Node next = (Node) iter.next();
                 if (next instanceof SplatNode) {
-                    list.addAll(((RubyArray) begin(next)).getList());
+                    list.addAll(((RubyArray) eval(context, next, self)).getList());
                 } else {
-                    list.add(begin(next));
+                    list.add(eval(context, next, self));
                 }
             }
 
             context.setPosition(position);
 
             return (IRubyObject[]) list.toArray(new IRubyObject[list.size()]);
         }
 
-        return ArgsUtil.arrayify(begin(node));
+        return ArgsUtil.arrayify(eval(context, node, self));
     }
 
-    public boolean hasNext() {
-        return getCurrentInstructionStack() != null ;
-    }
+    private static RubyModule getEnclosingModule(ThreadContext context, Node node, IRubyObject self) {
+        RubyModule enclosingModule = null;
 
-    // Had to make it work this way because eval states are sometimes created in one thread for use in another...
-    // For example, block creation for a new Thread; block, frame, and evalstate for that Thread are created in the caller
-    // but used in the new Thread.
-    public ThreadContext getThreadContext() {
-        return runtime.getCurrentContext();
-    }
+        if (node instanceof Colon2Node) {
+            IRubyObject result = eval(context, ((Colon2Node) node).getLeftNode(), self);
 
-    public JumpException getCurrentException() {
-        return currentException;
+            if (result != null && !result.isNil()) {
+                enclosingModule = (RubyModule) result;
+            }
+        } else if (node instanceof Colon3Node) {
+            enclosingModule = context.getRuntime().getObject();
+        }
+
+        if (enclosingModule == null) {
+            enclosingModule = (RubyModule) context.peekCRef().getValue();
+        }
+
+        return enclosingModule;
     }
 
-    public void setCurrentException(JumpException currentException) {
-        this.currentException = currentException;
+    private static boolean isRescueHandled(ThreadContext context, RubyException currentException, ListNode exceptionNodes,
+            IRubyObject self) {
+        IRuby runtime = context.getRuntime();
+        if (exceptionNodes == null) {
+            return currentException.isKindOf(runtime.getClass("StandardError"));
+        }
+
+        context.beginCallArgs();
+
+        IRubyObject[] args = null;
+        try {
+            args = setupArgs(context, exceptionNodes, self);
+        } finally {
+            context.endCallArgs();
+        }
+
+        for (int i = 0; i < args.length; i++) {
+            if (!args[i].isKindOf(runtime.getClass("Module"))) {
+                throw runtime.newTypeError("class or module required for rescue clause");
+            }
+            if (args[i].callMethod("===", currentException).isTrue()) return true;
+        }
+        return false;
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 7bf1235d28..b40bfba9db 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,241 +1,242 @@
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
 import java.util.Iterator;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.ScopeNode;
 import org.jruby.evaluator.AssignmentVisitor;
+import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.Scope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends AbstractMethod {
     private ScopeNode body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
 
     public DefaultMethod(RubyModule implementationClass, ScopeNode body, ArgsNode argsNode, 
         Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.argsNode = argsNode;
 		this.cref = cref;
 		
 		assert body != null;
 		assert argsNode != null;
     }
     
     public void preMethod(IRuby runtime, RubyModule lastClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper) {
         ThreadContext context = runtime.getCurrentContext();
         
         context.preDefMethodInternalCall(lastClass, recv, name, args, noSuper, cref);
     }
     
     public void postMethod(IRuby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(IRuby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(IRuby runtime, IRubyObject receiver, RubyModule lastClass, String name, IRubyObject[] args, boolean noSuper) {
     	assert args != null;
 
         ThreadContext context = runtime.getCurrentContext();
         
         Scope scope = context.getFrameScope();
         if (body.getLocalNames() != null) {
             scope.resetLocalVariables(body.getLocalNames());
         }
         
         if (argsNode.getBlockArgNode() != null && context.isBlockGiven()) {
             scope.setValue(argsNode.getBlockArgNode().getCount(), runtime.newProc());
         }
 
         try {
             prepareArguments(context, runtime, scope, receiver, args);
             
             getArity().checkArity(runtime, args);
 
             traceCall(runtime, receiver, name);
 
             return receiver.eval(body.getBodyNode());
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
 	            if (je.getPrimaryData() == this) {
 	                return (IRubyObject)je.getSecondaryData();
 	            }
         	}
        		throw je;
         } finally {
             traceReturn(runtime, receiver, name);
         }
     }
 
     private void prepareArguments(ThreadContext context, IRuby runtime, Scope scope, IRubyObject receiver, IRubyObject[] args) {
         int expectedArgsCount = argsNode.getArgsCount();
 
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
 
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         if (scope.hasLocalVariables() && expectedArgsCount > 0) {
             for (int i = 0; i < expectedArgsCount; i++) {
                 scope.setValue(i + 2, args[i]);
             }
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, scope, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, IRuby runtime, Scope scope, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
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
         
         if (scope.hasLocalVariables()) {
             if (hasOptArgs) {
                 ListNode optArgs = argsNode.getOptArgs();
    
                 Iterator iter = optArgs.iterator();
                 for (int i = expectedArgsCount; i < args.length && iter.hasNext(); i++) {
                     //new AssignmentVisitor(new EvaluationState(runtime, receiver)).assign((Node)iter.next(), args[i], true);
    //                  in-frame EvalState should already have receiver set as self, continue to use it
-                    new AssignmentVisitor(context.getFrameEvalState()).assign((Node)iter.next(), args[i], true);
+                    new AssignmentVisitor(context.getFrameSelf()).assign((Node)iter.next(), args[i], true);
                     expectedArgsCount++;
                 }
    
                 // assign the default values, adding to the end of allArgs
                 while (iter.hasNext()) {
                     //new EvaluationState(runtime, receiver).begin((Node)iter.next());
                     //EvaluateVisitor.getInstance().eval(receiver.getRuntime(), receiver, (Node)iter.next());
                     // in-frame EvalState should already have receiver set as self, continue to use it
-                    allArgs.add(context.getFrameEvalState().begin((Node)iter.next()));
+                    allArgs.add(EvaluationState.eval(runtime.getCurrentContext(), ((Node)iter.next()), runtime.getCurrentContext().getFrameSelf()));
                 }
             }
         }
         
         // build an array from *rest type args, also adding to allArgs
         
         // move this out of the scope.hasLocalVariables() condition to deal
         // with anonymous restargs (* versus *rest)
         // none present ==> -1
         // named restarg ==> >=0
         // anonymous restarg ==> -2
         if (restArg != -1) {
             RubyArray array = runtime.newArray(args.length - expectedArgsCount);
             for (int i = expectedArgsCount; i < args.length; i++) {
                 array.append(args[i]);
                 allArgs.add(args[i]);
             }
             // only set in scope if named
             if (restArg >= 0) {
                 scope.setValue(restArg, array);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(IRuby runtime, IRubyObject receiver, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = runtime.getCurrentContext().getPreviousFramePosition();
         runtime.callTraceFunction("return", position, receiver, name, getImplementationClass()); // XXX
     }
 
     private void traceCall(IRuby runtime, IRubyObject receiver, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
 		ISourcePosition position = body.getBodyNode() != null ? 
             body.getBodyNode().getPosition() : body.getPosition();  
 
 		runtime.callTraceFunction("call", position, receiver, name, getImplementationClass()); // XXX
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public ICallable dup() {
         return new DefaultMethod(getImplementationClass(), body, argsNode, getVisibility(), cref);
     }	
 }
diff --git a/src/org/jruby/internal/runtime/methods/EvaluateCallable.java b/src/org/jruby/internal/runtime/methods/EvaluateCallable.java
index d2f8514215..536017be74 100644
--- a/src/org/jruby/internal/runtime/methods/EvaluateCallable.java
+++ b/src/org/jruby/internal/runtime/methods/EvaluateCallable.java
@@ -1,101 +1,91 @@
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
 
 import org.jruby.IRuby;
 import org.jruby.RubyModule;
 import org.jruby.ast.Node;
 import org.jruby.ast.types.IArityNode;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author  jpetersen
  */
 public class EvaluateCallable extends AbstractCallable {
     private final Node node;
     private final Arity arity;
 
     private EvaluateCallable(Node node, Visibility visibility, Arity arity) {
         super(null, visibility);
         this.node = node;
         this.arity = arity;
     }
 
     public EvaluateCallable(Node node, Node vars) {
     	this(node, null, procArityOf(vars));
     }
     
     public void preMethod(IRuby runtime, RubyModule implementationClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper) {
     }
     
     public void postMethod(IRuby runtime) {
     }
 
     public IRubyObject internalCall(IRuby runtime, IRubyObject receiver, RubyModule lastClass, String name, IRubyObject[] args, boolean noSuper) {
-        return new EvaluationState(runtime, receiver).begin(node);
-        // REVIST: we will execute under a different self, so save it (should be a stack?)
-        // This almost works, but causes rubicon TestThread to run forever
-//        EvaluationState evalState = runtime.getCurrentContext().getCurrentFrame().getEvalState();
-//        IRubyObject oldSelf = evalState.getSelf();
-//        evalState.setSelf(receiver);
-//        try {
-//            return evalState.begin(node);
-//        } finally {
-//            evalState.setSelf(oldSelf);
-//        }
+        return EvaluationState.eval(runtime.getCurrentContext(), node, receiver);
     }
 
     public Node getNode() {
         return node;
     }
 
     public Arity getArity() {
     	return arity;
     }
     
     private static Arity procArityOf(Node node) {
         if (node == null) {
             return Arity.optional();
         } else if (node instanceof IArityNode) {
             return ((IArityNode) node).getArity();
         } 
 
         throw new Error("unexpected type " + node.getClass() + " at " + node.getPosition());
     }
     
     public ICallable dup() {
         return new EvaluateCallable(node, getVisibility(), arity);
     }
 }
diff --git a/src/org/jruby/runtime/Frame.java b/src/org/jruby/runtime/Frame.java
index 96f0dde286..e3d8409a5f 100644
--- a/src/org/jruby/runtime/Frame.java
+++ b/src/org/jruby/runtime/Frame.java
@@ -1,207 +1,197 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime;
 
 import org.jruby.IRuby;
 import org.jruby.RubyModule;
-import org.jruby.evaluator.EvaluationState;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  */
 public class Frame {
     private IRubyObject self;
     private IRubyObject[] args;
     private String lastFunc;
     private RubyModule lastClass;
     private final ISourcePosition position;
     private Iter iter;
     private IRuby runtime;
-    private EvaluationState evalState;
     private Block blockArg;
 
     private Scope scope;
     
     public Frame(ThreadContext threadContext, Iter iter, Block blockArg) {
         this(threadContext.getRuntime(), null, IRubyObject.NULL_ARRAY, null, null, threadContext.getPosition(), 
              iter, blockArg);   
     }
 
     public Frame(ThreadContext threadContext, IRubyObject self, IRubyObject[] args, 
     		String lastFunc, RubyModule lastClass, ISourcePosition position, Iter iter, Block blockArg) {
     	this(threadContext.getRuntime(), self, args, lastFunc, lastClass, position, iter, blockArg);
     }
 
     private Frame(IRuby runtime, IRubyObject self, IRubyObject[] args, String lastFunc,
                  RubyModule lastClass, ISourcePosition position, Iter iter, Block blockArg) {
         this.self = self;
         this.args = args;
         this.lastFunc = lastFunc;
         this.lastClass = lastClass;
         this.position = position;
         this.iter = iter;
         this.runtime = runtime;
         this.blockArg = blockArg;
     }
 
     /** Getter for property args.
      * @return Value of property args.
      */
     IRubyObject[] getArgs() {
         return args;
     }
 
     /** Setter for property args.
      * @param args New value of property args.
      */
     void setArgs(IRubyObject[] args) {
         this.args = args;
     }
 
     /**
      * @return the frames current position
      */
     ISourcePosition getPosition() {
         return position;
     }
 
     /** Getter for property iter.
      * @return Value of property iter.
      */
     Iter getIter() {
         return iter;
     }
 
     /** Setter for property iter.
      * @param iter New value of property iter.
      */
     void setIter(Iter iter) {
         this.iter = iter;
     }
 
     boolean isBlockGiven() {
         return iter.isBlockGiven();
     }
 
     /** Getter for property lastClass.
      * @return Value of property lastClass.
      */
     RubyModule getLastClass() {
         return lastClass;
     }
     
     public void setLastClass(RubyModule lastClass) {
         this.lastClass = lastClass;
     }
     
     public void setLastFunc(String lastFunc) {
         this.lastFunc = lastFunc;
     }
 
     /** Getter for property lastFunc.
      * @return Value of property lastFunc.
      */
     String getLastFunc() {
         return lastFunc;
     }
 
     /** Getter for property self.
      * @return Value of property self.
      */
     IRubyObject getSelf() {
         return self;
     }
 
     /** Setter for property self.
      * @param self New value of property self.
      */
     void setSelf(IRubyObject self) {
         this.self = self;
     }
     
     void newScope(String[] localNames) {
         setScope(new Scope(runtime, localNames));
     }
     
     Scope getScope() {
         return scope;
     }
     
     Scope setScope(Scope newScope) {
         Scope oldScope = scope;
         
         scope = newScope;
         
         return oldScope;
     }
     
     public Frame duplicate() {
         IRubyObject[] newArgs;
         if (args.length != 0) {
             newArgs = new IRubyObject[args.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
         } else {
         	newArgs = args;
         }
 
         return new Frame(runtime, self, newArgs, lastFunc, lastClass, position, iter, blockArg);
     }
 
     /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
     public String toString() {
         StringBuffer sb = new StringBuffer(50);
         sb.append(position != null ? position.toString() : "-1");
         sb.append(':');
         sb.append(lastClass + " " + lastFunc);
         if (lastFunc != null) {
             sb.append("in ");
             sb.append(lastFunc);
         }
         return sb.toString();
     }
-
-    EvaluationState getEvalState() {
-        return evalState != null ? evalState : (evalState = new EvaluationState(runtime, self));
-    }
-
-    void setEvalState(EvaluationState evalState) {
-        this.evalState = evalState;
-    }
     
     Block getBlockArg() {
         return blockArg;
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 46a1304dd9..2ef97de596 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1028 +1,1016 @@
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
 import org.jruby.RubyBinding;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.StarNode;
 import org.jruby.ast.ZeroArgNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
-import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SourcePositionFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.UnsynchronizedStack;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author jpetersen
  */
 public class ThreadContext {
     private final IRuby runtime;
 
     private Block blockStack;
     private UnsynchronizedStack dynamicVarsStack;
 
     private RubyThread thread;
     
     private UnsynchronizedStack parentStack;
 	
     private UnsynchronizedStack frameStack;
     private UnsynchronizedStack iterStack;
     private UnsynchronizedStack crefStack;
 
     private RubyModule wrapper;
 
     private ISourcePosition sourcePosition = new SourcePositionFactory(null).getDummyPosition();
 
     /**
      * Constructor for Context.
      */
     public ThreadContext(IRuby runtime) {
         this.runtime = runtime;
 
         this.dynamicVarsStack = new UnsynchronizedStack();
         this.frameStack = new UnsynchronizedStack();
         this.iterStack = new UnsynchronizedStack();
         this.parentStack = new UnsynchronizedStack();
         this.crefStack = new UnsynchronizedStack();
 
         pushDynamicVars();
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
     
     private void restoreBlockState(Block block, RubyModule klass) {
         pushFrame(block.getFrame());
 
         setCRef(block.getCRef());
         
         getCurrentFrame().setScope(block.getScope());
 
         pushDynamicVars(block.getDynamicVariables());
 
         pushRubyClass((klass != null) ? klass : block.getKlass()); 
 
         pushIter(block.getIter());
     }
 
     private void flushBlockState() {
         popIter();
         popDynamicVars();
         popFrame();
         
         unsetCRef();
         
         popRubyClass();
     }
 
     public DynamicVariableSet getCurrentDynamicVars() {
         return (DynamicVariableSet) dynamicVarsStack.peek();
     }
 
     private void pushDynamicVars() {
         dynamicVarsStack.push(new DynamicVariableSet());
     }
 
     private void pushDynamicVars(DynamicVariableSet dynVars) {
         dynamicVarsStack.push(dynVars);
     }
 
     private void popDynamicVars() {
         dynamicVarsStack.pop();
     }
 
     public RubyThread getThread() {
         return thread;
     }
 
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
 
     public IRubyObject getLastline() {
         return getFrameScope().getLastLine();
     }
 
     public void setLastline(IRubyObject value) {
         getFrameScope().setLastLine(value);
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         pushFrame(getCurrentFrame().duplicate());
     }
     
     private void pushCallFrame(IRubyObject self, IRubyObject[] args, 
             String lastFunc, RubyModule lastClass) {
         pushFrame(new Frame(this, self, args, lastFunc, lastClass, getPosition(), getCurrentIter(), getCurrentBlock()));
     }
     
     private void pushFrame() {
         pushFrame(new Frame(this, getCurrentIter(), getCurrentBlock()));
     }
     
     private void pushFrameNoBlock() {
         pushFrame(new Frame(this, Iter.ITER_NOT, null));
     }
     
     private void pushFrame(Frame frame) {
         frameStack.push(frame);
     }
     
     private void popFrame() {
         Frame frame = (Frame)frameStack.pop();
 
         setPosition(frame.getPosition());
     }
     
     public Frame getCurrentFrame() {
         return (Frame)frameStack.peek();
     }
     
     public Frame getPreviousFrame() {
         int size = frameStack.size();
         return size <= 1 ? null : (Frame) frameStack.get(size - 2);
     }
     
     public int getFrameCount() {
         return frameStack.size();
     }
     
-    public EvaluationState getFrameEvalState() {
-        return getCurrentFrame().getEvalState();
-    }
-    
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
     
     /////////////////////////// ITER MANAGEMENT //////////////////////////
     private Iter popIter() {
         return (Iter) iterStack.pop();
     }
     
     private void pushIter(Iter iter) {
         iterStack.push(iter);
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
         pushIter(getRuntime().getCurrentContext().isBlockGiven() ? Iter.ITER_PRE : Iter.ITER_NOT);
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
         return (Iter) iterStack.peek();
     }
     
     public UnsynchronizedStack getIterStack() {
         return iterStack;
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
         return getFrameScope().getBackref();
     }
 
     public void setBackref(IRubyObject backref) {
         getFrameScope().setBackref(backref);
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
 
     public IRubyObject callSuper(IRubyObject[] args) {
     	Frame frame = getCurrentFrame();
     	
         if (frame.getLastClass() == null) {
             throw runtime.newNameError("superclass method '" + frame.getLastFunc() + "' must be enabled by enableSuper().");
         }
         setNoBlockIfNoBlock();
         try {
             RubyClass superClass = frame.getLastClass().getSuperClass();
 
             // Modules do not directly inherit Object so we have hacks like this
             if (superClass == null) {
                 // TODO cnutter: I believe modules, the only ones here to have no superclasses, should have Module as their superclass
             	superClass = runtime.getClass("Module");
             }
             return frame.getSelf().callMethod(superClass, frame.getLastFunc(),
                                    args, CallType.SUPER);
         } finally {
             clearNoBlock();
         }
     }
 
     public IRubyObject yield(IRubyObject value) {
         return yieldCurrentBlock(value, null, null, false);
     }
 
     /**
      * Yield to the block passed to the current frame.
      * 
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param yieldProc
      * @param aValue
      * @return
      */
     public IRubyObject yieldCurrentBlock(IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue) {
         if (! isBlockGiven()) {
             throw runtime.newLocalJumpError("yield called out of block");
         }
         
         Block currentBlock = preYieldCurrentBlock(klass);
 
         try {
             return yieldInternal(currentBlock, value, self, klass, aValue);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.NextJump) {
 	            IRubyObject nextValue = (IRubyObject)je.getPrimaryData();
 	            return nextValue == null ? runtime.getNil() : nextValue;
         	} else {
         		throw je;
         	}
         } finally {
             postBoundEvalOrYield();
         }
     }
 
     /**
      * Yield to a specific block.
      * 
      * @param yieldBlock The block to which to yield
      * @param value
      * @param self
      * @param klass
      * @param yieldProc
      * @param aValue
      * @return
      */
     public IRubyObject yieldSpecificBlock(Block yieldBlock, IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue) {
         preProcBlockCall();
         preYieldSpecificBlock(yieldBlock, klass);
         try {
             return yieldInternal(yieldBlock, value, self, klass, aValue);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.NextJump) {
                 IRubyObject nextValue = (IRubyObject)je.getPrimaryData();
                 return nextValue == null ? runtime.getNil() : nextValue;
             } else {
                 throw je;
             }
         } finally {
             postBoundEvalOrYield();
             postProcBlockCall();
         }
     }
     
     private IRubyObject yieldInternal(Block yieldBlock, IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue) {
         // block is executed under its own self, so save the old one (use a stack?)
-        IRubyObject oldSelf = getCurrentFrame().getEvalState().getSelf();
-        
         try {
             setCRef(yieldBlock.getCRef());
             
             if (klass == null) {
                 self = yieldBlock.getSelf();               
             }
-    
-            getCurrentFrame().getEvalState().setSelf(getCurrentFrame().getSelf()); 
             
             // FIXME: during refactoring, it was determined that all calls to yield are passing false for yieldProc; is this still needed?
             IRubyObject[] args = getBlockArgs(value, self, false, aValue, yieldBlock);
             while (true) {
                 try {
                     // FIXME: is it appropriate to use the current frame's (the block's frame's) lastClass?
                     IRubyObject result = yieldBlock.getMethod().call(runtime, self, getCurrentFrame().getLastClass(), null, args, false);
                     
                     return result;
                 } catch (JumpException je) {
                     if (je.getJumpType() == JumpException.JumpType.RedoJump) {
                         // do nothing, allow loop to redo
                     } else {
                         throw je;
                     }
                 }
             }
         } finally {
-            getCurrentFrame().getEvalState().setSelf(oldSelf);
             unsetCRef();
         }
     }
 
     private IRubyObject[] getBlockArgs(IRubyObject value, IRubyObject self, boolean yieldProc, boolean aValue, Block currentBlock) {
         Node blockVar = currentBlock.getVar();
         if (blockVar != null) {
             if (blockVar instanceof ZeroArgNode) {
                 // Better not have arguments for a no-arg block.
                 if (yieldProc && arrayLength(value) != 0) { 
                     throw runtime.newArgumentError("wrong # of arguments(" + 
                             ((RubyArray)value).getLength() + "for 0)");
                 }
             } else if (blockVar instanceof MultipleAsgnNode) {
                 if (!aValue) {
                     value = sValueToMRHS(value, ((MultipleAsgnNode)blockVar).getHeadNode());
                 }
 
                 value = mAssign(self, (MultipleAsgnNode)blockVar, (RubyArray)value, yieldProc);
             } else {
                 if (aValue) {
                     int length = arrayLength(value);
                 
                     if (length == 0) {
                         value = runtime.getNil();
                     } else if (length == 1) {
                         value = ((RubyArray)value).first(IRubyObject.NULL_ARRAY);
                     } else {
                         // XXXEnebo - Should be warning not error.
                         //throw runtime.newArgumentError("wrong # of arguments(" + 
                         //        length + "for 1)");
                     }
                 } else if (value == null) { 
                     // XXXEnebo - Should be warning not error.
                     //throw runtime.newArgumentError("wrong # of arguments(0 for 1)");
                 }
 
-                new AssignmentVisitor(getCurrentFrame().getEvalState()).assign(blockVar, value, yieldProc); 
+                new AssignmentVisitor(getFrameSelf()).assign(blockVar, value, yieldProc); 
             }
         }
 
         IRubyObject[] args = ArgsUtil.arrayify(value);
         return args;
     }
     
     public IRubyObject mAssign(IRubyObject self, MultipleAsgnNode node, RubyArray value, boolean pcall) {
         // Assign the values.
         int valueLen = value.getLength();
         int varLen = node.getHeadNode() == null ? 0 : node.getHeadNode().size();
         
         Iterator iter = node.getHeadNode() != null ? node.getHeadNode().iterator() : Collections.EMPTY_LIST.iterator();
         for (int i = 0; i < valueLen && iter.hasNext(); i++) {
             Node lNode = (Node) iter.next();
-            new AssignmentVisitor(getCurrentFrame().getEvalState()).assign(lNode, value.entry(i), pcall);
+            new AssignmentVisitor(getFrameSelf()).assign(lNode, value.entry(i), pcall);
         }
 
         if (pcall && iter.hasNext()) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         if (node.getArgsNode() != null) {
             if (node.getArgsNode() instanceof StarNode) {
                 // no check for '*'
             } else if (varLen < valueLen) {
                 ArrayList newList = new ArrayList(value.getList().subList(varLen, valueLen));
-                new AssignmentVisitor(getCurrentFrame().getEvalState()).assign(node.getArgsNode(), runtime.newArray(newList), pcall);
+                new AssignmentVisitor(getFrameSelf()).assign(node.getArgsNode(), runtime.newArray(newList), pcall);
             } else {
-                new AssignmentVisitor(getCurrentFrame().getEvalState()).assign(node.getArgsNode(), runtime.newArray(0), pcall);
+                new AssignmentVisitor(getFrameSelf()).assign(node.getArgsNode(), runtime.newArray(0), pcall);
             }
         } else if (pcall && valueLen < varLen) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         while (iter.hasNext()) {
-            new AssignmentVisitor(getCurrentFrame().getEvalState()).assign((Node)iter.next(), runtime.getNil(), pcall);
+            new AssignmentVisitor(getFrameSelf()).assign((Node)iter.next(), runtime.getNil(), pcall);
         }
         
         return value;
     }
 
     private IRubyObject sValueToMRHS(IRubyObject value, Node leftHandSide) {
         if (value == null) {
             return runtime.newArray(0);
         }
         
         if (leftHandSide == null) {
             return runtime.newArray(value);
         }
         
         IRubyObject newValue = value.convertToType("Array", "to_ary", false);
 
         if (newValue.isNil()) {
             return runtime.newArray(value);
         }
         
         return newValue;
     }
     
     private int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
 
     public void pollThreadEvents() {
         getThread().pollThreadEvents();
     }
     
     public SinglyLinkedList peekCRef() {
         return (SinglyLinkedList)crefStack.peek();
     }
     
     public void setCRef(SinglyLinkedList newCRef) {
         crefStack.push(newCRef);
     }
     
     public void unsetCRef() {
         crefStack.pop();
     }
     
     public SinglyLinkedList pushCRef(RubyModule newModule) {
         if (crefStack.isEmpty()) {
             crefStack.push(new SinglyLinkedList(newModule, null));
         } else {
             crefStack.push(new SinglyLinkedList(newModule, (SinglyLinkedList)crefStack.pop()));
         }
         
         return (SinglyLinkedList)peekCRef();
     }
     
     public RubyModule popCRef() {
         assert !crefStack.isEmpty() : "Tried to pop from empty CRef stack";
         
         RubyModule module = (RubyModule)peekCRef().getValue();
         
         SinglyLinkedList next = ((SinglyLinkedList)crefStack.pop()).getNext();
         
         if (next != null) {
             crefStack.push(next);
         }
         
         return module;
     }
 
     public void pushRubyClass(RubyModule currentModule) {
         assert currentModule != null : "Can't push null RubyClass";
         
         parentStack.push(currentModule);
     }
     
     public RubyModule popRubyClass() {
         return (RubyModule)parentStack.pop();
     }
 	
     public RubyModule getRubyClass() {
         assert !parentStack.isEmpty() : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = (RubyModule)parentStack.peek();
 
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getWrapper() {
         return wrapper;
     }
 
     public void setWrapper(RubyModule wrapper) {
         this.wrapper = wrapper;
     }
 
     public IRubyObject getDynamicValue(String name) {
         IRubyObject result = ((DynamicVariableSet) dynamicVarsStack.peek()).get(name);
 
         return result == null ? runtime.getNil() : result;
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
         int traceSize = frameStack.size() - level - 1;
         
         if (traceSize <= 0) {
         	return backtrace;
         }
         
         if (nativeException) {
             // assert level == 0;
             addBackTraceElement(backtrace, (Frame) frameStack.get(frameStack.size() - 1), null);
         }
         
         for (int i = traceSize; i > 0; i--) {
             addBackTraceElement(backtrace, (Frame) frameStack.get(i), (Frame) frameStack.get(i-1));
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
         getCurrentFrame().newScope(null);
         pushRubyClass(runtime.getObject());
         pushCRef(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
-        getCurrentFrame().getEvalState().setSelf(runtime.getTopSelf());
     }
 
     public void preClassEval(String[] localNames, RubyModule type) {
         pushCRef(type);
         pushRubyClass(type); 
         pushFrameCopy();
         getCurrentFrame().newScope(localNames);
         pushDynamicVars();
     }
     
     public void postClassEval() {
         popCRef();
         popDynamicVars();
         popRubyClass();
         popFrame();
     }
     
     public void preScopedBody(String[] localNames) {
         assert false;
         getCurrentFrame().newScope(localNames);
     }
     
     public void postScopedBody() {
     }
     
     public void preBsfApply(String[] localNames) {
         pushFrameNoBlock();
         pushDynamicVars();
         getCurrentFrame().newScope(localNames);
     }
     
     public void postBsfApply() {
         popDynamicVars();
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
     
     public void preDefMethodInternalCall(RubyModule lastClass, IRubyObject recv, String name, IRubyObject[] args, boolean noSuper, SinglyLinkedList cref) {
         RubyModule implementationClass = (RubyModule)cref.getValue();
         setCRef(cref);
         setInBlockIfBlock();
         pushCallFrame(recv, args, name, noSuper ? null : lastClass);
         getCurrentFrame().newScope(null);
         pushDynamicVars();
         pushRubyClass(implementationClass);
     }
     
     public void postDefMethodInternalCall() {
         popRubyClass();
         popDynamicVars();
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
         getCurrentFrame().newScope(null);
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void preInitBuiltinClasses(RubyClass objectClass, IRubyObject topSelf) {
         pushRubyClass(objectClass);
         setCRef(objectClass.getCRef());
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
-        frame.getEvalState().setSelf(topSelf);
     }
     
     public void preNodeEval(RubyModule newWrapper, RubyModule rubyClass, IRubyObject self) {
         pushDynamicVars();
         setWrapper(newWrapper);
         pushRubyClass(rubyClass);
         pushCallFrame(self, IRubyObject.NULL_ARRAY, null, null);
         getCurrentFrame().newScope(null);
         setCRef(rubyClass.getCRef());
     }
     
     public void postNodeEval(RubyModule newWrapper) {
         popFrame();
         popRubyClass();
         setWrapper(newWrapper);
         popDynamicVars();
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
     
     public void preKernelEval() {
         // we pop here and push in the post so the eval runs under the previous frame
         // pop the frame created for us by the method call
         popFrame();
     }
     
     public void postKernelEval() {
         // push a dummy frame back to the stack for the method call to pop
         pushFrameNoBlock();
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
 
     private Block preYieldCurrentBlock(RubyModule klass) {
         Block currentBlock = getCurrentFrame().getBlockArg();
 
         restoreBlockState(currentBlock, klass);
 
         return currentBlock;
     }
     
     private void preYieldSpecificBlock(Block specificBlock, RubyModule klass) {
         restoreBlockState(specificBlock, klass);
     }
 
     public void preEvalWithBinding(RubyBinding binding) {
         Block bindingBlock = binding.getBlock();
 
         restoreBlockState(bindingBlock, null);
     }
 
     public void postBoundEvalOrYield() {
         flushBlockState();
     }
 }
