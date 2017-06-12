diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index d5dd19826c..3af70706a6 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1764 +1,1798 @@
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
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.util.Hashtable;
+import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
+import java.util.WeakHashMap;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.YARVCompiledRunner;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.yarv.StandardYARVCompiler;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.IConvLibrary;
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.libraries.BigDecimalLibrary;
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.libraries.ThreadLibrary;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.ext.Generator;
 import org.jruby.ext.Readline;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.MethodSelectorTable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.BindingMetaClass;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.HashMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.builtin.meta.ModuleMetaClass;
 import org.jruby.runtime.builtin.meta.ObjectMetaClass;
 import org.jruby.runtime.builtin.meta.ProcMetaClass;
 import org.jruby.runtime.builtin.meta.SymbolMetaClass;
 import org.jruby.runtime.builtin.meta.TimeMetaClass;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "nkf", "yaml/syck" };
 
     private CacheMap cacheMap = new CacheMap(this);
     private ThreadService threadService = new ThreadService(this);
     private Hashtable runtimeInformation;
     private final MethodSelectorTable selectorTable = new MethodSelectorTable();
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable ioHandlers = new Hashtable();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private RubyProc traceFunction;
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private final boolean objectSpaceEnabled;
 
     private long globalState = 1;
 
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
     private RubyClass stringClass;
     private RubyClass systemCallError = null;
     private RubyModule errnoModule = null;
     private IRubyObject topSelf;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private RubyInstanceConfig config;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
     private IRubyObject verbose;
     private IRubyObject debug;
 
     // Java support
     private JavaSupport javaSupport;
     // FIXME: THIS IS WRONG. We need to correct the classloading problems.
     private static JRubyClassLoader jrubyClassLoader = new JRubyClassLoader(Ruby.class.getClassLoader());
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack atExitBlocks = new Stack();
 
     private RubyModule kernelModule;
 
     private RubyClass nilClass;
 
     private RubyClass fixnumClass;
     
     private RubyClass arrayClass;
 
     private IRubyObject tmsStruct;
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 0;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
+    
+    /**
+     * A list of finalizers, weakly referenced, to be executed on tearDown
+     */
+    private Map finalizers;
 
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
 
         initBuiltinClasses();
 
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
         ObjectMetaClass objectMetaClass = new ObjectMetaClass(this);
         objectMetaClass.initializeClass();
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = new ModuleMetaClass(this, objectClass);
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
 
         ((ObjectMetaClass) moduleClass).initializeBootstrapClass();
 
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
             RubyNumeric.createNumericClass(this);
         }
 
         if(profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
 
         if(profile.allowClass("Fixnum")) {
             fixnumClass = RubyFixnum.createFixnumClass(this);
         }
         new HashMetaClass(this).initializeClass();
         new IOMetaClass(this).initializeClass();
 
         if(profile.allowClass("Array")) {
             arrayClass = RubyArray.createArrayClass(this);
         }
 
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
                                                    newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if(profile.allowClass("Float")) {
            RubyFloat.createFloatClass(this);
         }
 
         if(profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
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
         // FIXME: Actually this somewhere <- fixed
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
 
         if(profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
         }
 
         if(profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     private void initBuiltinClasses() {
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
         if (path.charAt(0) == '#') {
             //throw newArgumentError("can't retrieve anonymous class " + path);
             throw new RuntimeException();
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
     public void loadScript(RubyString scriptName, RubyString source, boolean wrap) {
         loadScript(scriptName.toString(), new StringReader(source.toString()), wrap);
     }
 
     public void loadScript(String scriptName, Reader source, boolean wrap) {
         File f = new File(scriptName);
         if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
             scriptName = "./" + scriptName;
         }
 
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
                 context.getRubyClass().extend_object(self);
             }
 
             Node node = parse(source, scriptName, null);
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
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
                 context.getRubyClass().extend_object(self);
             }
 
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
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
                 traceFunction.call(new IRubyObject[] {
                     newString(event), // event name
                     newString(file), // filename
                     newFixnum(position.getStartLine() + 1), // line numbers should be 1-based
                     name != null ? RubySymbol.newSymbol(this, name) : getNil(),
                     self != null ? self : getNil(),
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
+    
+    public void addFinalizer(RubyObject.Finalizer finalizer) {
+        synchronized (this) {
+            if (finalizers == null) {
+                finalizers = new WeakHashMap();
+            }
+        }
+        
+        synchronized (finalizers) {
+            finalizers.put(finalizer, null);
+        }
+    }
+    
+    public void removeFinalizer(RubyObject.Finalizer finalizer) {
+        if (finalizers != null) {
+            synchronized (finalizers) {
+                finalizers.remove(finalizer);
+            }
+        }
+    }
 
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
-        getObjectSpace().finishFinalizers();
+        if (finalizers != null) {
+            synchronized (finalizers) {
+                for (Iterator finalIter = finalizers.keySet().iterator(); finalIter.hasNext();) {
+                    ((RubyObject.Finalizer)finalIter.next()).finalize();
+                    finalIter.remove();
+                }
+            }
+        }
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
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 8b0efecc7a..2d711a0edf 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1368 +1,1417 @@
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
 
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.Sprintf;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
+import java.util.List;
 import java.util.Map;
 import org.jruby.ast.Node;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     // The class of this object
     protected RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     private transient Object dataStruct;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
+    
+    private Finalizer finalizer;
+    
+    public class Finalizer {
+        private long id;
+        private List finalizers;
+        
+        public Finalizer(long id) {
+            this.id = id;
+        }
+        
+        public void addFinalizer(RubyProc finalizer) {
+            if (finalizers == null) {
+                finalizers = new ArrayList();
+            }
+            finalizers.add(finalizer);
+        }
+
+        public void removeFinalizers() {
+            finalizers = null;
+        }
+    
+        public void finalize() {
+            if (finalizers != null) {
+                IRubyObject idFixnum = getRuntime().newFixnum(id);
+                for (int i = 0; i < finalizers.size(); i++) {
+                    ((RubyProc)finalizers.get(i)).call(
+                            new IRubyObject[] {idFixnum});
+                }
+            }
+        }
+    }
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
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
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
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
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
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
         return other == this || 
                 other instanceof IRubyObject && 
                 callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
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
     public final RubyClass getMetaClass() {
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
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
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
 
     public final boolean isTrue() {
         return isTrue;
     }
 
     public final boolean isFalse() {
         return !isTrue;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, self, callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
     
     public static IRubyObject callMethodMissingIfNecessary(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(self, callType))) {
 
             if (callType == CallType.SUPER) {
                 throw self.getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(self, args, block);
             }
 
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
             return receiver.callMethod(context, "method_missing", newArgs, block);
         }
         
         // kludgy.
         return null;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, methodIndex, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, rubyclass, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, context.getFrameSelf(), callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return callMethod(context, getMetaClass(), methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, block);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
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
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
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
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType(getRuntime().getArray(), MethodIndex.TO_ARY, true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType(getRuntime().getClass("Float"), MethodIndex.TO_F, true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType(getRuntime().getClass("Integer"), MethodIndex.TO_INT, true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType(getRuntime().getString(), MethodIndex.TO_STR, true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(RubyClass targetType, int convertMethodIndex, String convertMethod) {
         return convertToType(targetType, convertMethodIndex, convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raise) {
         return convertToType(targetType, convertMethodIndex, convertMethod, raise, false, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, boolean raise) {
         return convertToType(targetType, convertMethodIndex, MethodIndex.NAMES[convertMethodIndex], raise, false, false);
     }
     
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough) {
         if (isKindOf(targetType)) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raiseOnMissingMethod) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         
         IRubyObject value = callMethod(getRuntime().getCurrentContext(), convertMethodIndex, convertMethod, IRubyObject.NULL_ARRAY);
         
         if (allowNilThrough && value.isNil()) {
             return value;
         }
         
         if (raiseOnWrongTypeResult && !value.isKindOf(targetType)) {
             throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod +
                     " should return " + targetType);
         }
         
         return value;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck(getRuntime().getString(), MethodIndex.TO_STR, "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck(getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameName();
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
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
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
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this }, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
         ISourcePosition savedPosition = threadContext.getPosition();
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             IRubyObject newSelf = threadContext.getFrameSelf();
             Node node = getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope());
 
             return EvaluationState.eval(getRuntime(), threadContext, node, newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding();
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             Node node = getRuntime().parse(src.toString(), file, context.getCurrentScope());
             
             return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
 //        if (isNil()) {
 //            return getRuntime().newBoolean(obj.isNil());
 //        }
 //        return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
     
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
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
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return System.identityHashCode(this);
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
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         initCopy(clone, this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         initCopy(dup, this);
 
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
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(sep);
                         part.append(" ");
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
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
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
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
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
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
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
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
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args, Block block) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = anyToString().toString();
         } else {
             description = inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         Ruby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : getType().getName())
                 })).toString();
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
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
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
     }
+        
+    public void addFinalizer(RubyProc finalizer) {
+        if (this.finalizer == null) {
+            this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
+            getRuntime().addFinalizer(this.finalizer);
+        }
+        this.finalizer.addFinalizer(finalizer);
+    }
+
+    public void removeFinalizers() {
+        if (finalizer != null) {
+            finalizer.removeFinalizers();
+            finalizer = null;
+            getRuntime().removeFinalizer(this.finalizer);
+        }
+    }
 }
diff --git a/src/org/jruby/RubyObjectSpace.java b/src/org/jruby/RubyObjectSpace.java
index 2e839cef14..103225e1ff 100644
--- a/src/org/jruby/RubyObjectSpace.java
+++ b/src/org/jruby/RubyObjectSpace.java
@@ -1,125 +1,124 @@
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
 
 import java.util.Iterator;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyObjectSpace {
 
     /** Create the ObjectSpace module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createObjectSpaceModule(Ruby runtime) {
         RubyModule objectSpaceModule = runtime.defineModule("ObjectSpace");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObjectSpace.class);
         objectSpaceModule.defineModuleFunction("each_object", callbackFactory.getOptSingletonMethod("each_object"));
         objectSpaceModule.defineFastModuleFunction("garbage_collect", callbackFactory.getFastSingletonMethod("garbage_collect"));
         objectSpaceModule.defineFastModuleFunction("_id2ref", callbackFactory.getFastSingletonMethod("id2ref", RubyFixnum.class));
         objectSpaceModule.defineModuleFunction("define_finalizer", 
         		callbackFactory.getOptSingletonMethod("define_finalizer"));
         objectSpaceModule.defineModuleFunction("undefine_finalizer", 
                 callbackFactory.getOptSingletonMethod("undefine_finalizer"));
 
         return objectSpaceModule;
     }
 
     public static IRubyObject define_finalizer(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyProc proc = null;
         if(Arity.checkArgumentCount(runtime, args,1,2) == 2) {
             if(args[1] instanceof RubyProc) {
                 proc = (RubyProc)args[1];
             } else {
                 proc = (RubyProc)args[1].convertToType(runtime.getClass("Proc"), 0, "to_proc", true);
             }
         } else {
             proc = runtime.newProc(false, block);
         }
         IRubyObject obj = args[0];
-        long id = RubyNumeric.fix2long(obj.id());
-        runtime.getObjectSpace().addFinalizer(obj, id, proc);
+        runtime.getObjectSpace().addFinalizer(obj, proc);
         return recv;
     }
 
     public static IRubyObject undefine_finalizer(IRubyObject recv, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,1);
         recv.getRuntime().getObjectSpace().removeFinalizers(RubyNumeric.fix2long(args[0].id()));
         return recv;
     }
 
     public static IRubyObject id2ref(IRubyObject recv, RubyFixnum id) {
         Ruby runtime = id.getRuntime();
         long longId = id.getLongValue();
         if (longId == 0) {
             return runtime.getFalse();
         } else if (longId == 2) {
             return runtime.getTrue();
         } else if (longId == 4) {
             return runtime.getNil();
         } else if (longId % 2 != 0) { // odd
             return runtime.newFixnum((longId - 1) / 2);
         } else {
             IRubyObject object = runtime.getObjectSpace().id2ref(longId);
             if (object == null)
                 runtime.newRangeError("not an id value");
             return object;
         }
     }
     
     public static IRubyObject each_object(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyModule rubyClass;
         if (args.length == 0) {
             rubyClass = recv.getRuntime().getObject();
         } else {
             rubyClass = (RubyModule) args[0];
         }
         int count = 0;
         Iterator iter = recv.getRuntime().getObjectSpace().iterator(rubyClass);
         IRubyObject obj = null;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while ((obj = (IRubyObject)iter.next()) != null) {
             count++;
             block.yield(context, obj);
         }
         return recv.getRuntime().newFixnum(count);
     }
 
     public static IRubyObject garbage_collect(IRubyObject recv) {
         return RubyGC.start(recv);
     }
 }
diff --git a/src/org/jruby/runtime/ObjectSpace.java b/src/org/jruby/runtime/ObjectSpace.java
index 61c2208dd6..af12e770f6 100644
--- a/src/org/jruby/runtime/ObjectSpace.java
+++ b/src/org/jruby/runtime/ObjectSpace.java
@@ -1,309 +1,203 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.WeakIdentityHashMap;
 
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.WeakReference;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
 import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
 import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
 import org.jruby.RubyNumeric;
 
 /**
  * FIXME: This version is faster than the previous, but both suffer from a
  * crucial flaw: It is impossible to create an ObjectSpace with an iterator
  * that doesn't either: a. hold on to objects that might otherwise be collected
  * or b. have no way to guarantee that a call to hasNext() will be correct or
  * that a subsequent call to next() will produce an object. For our purposes,
  * for now, this may be acceptable.
  */
 public class ObjectSpace {
     private ReferenceQueue deadReferences = new ReferenceQueue();
     private WeakReferenceListNode top;
 
     private ReferenceQueue deadIdentityReferences = new ReferenceQueue();
     private final Map identities = new HashMap();
     private final Map identitiesByObject = new WeakIdentityHashMap();
 
     private long maxId = 4; // Highest reserved id
 
     public long idOf(IRubyObject rubyObject) {
         synchronized (identities) {
             Long longId = (Long) identitiesByObject.get(rubyObject);
             if (longId == null) {
                 longId = createId(rubyObject);
             }
             return longId.longValue();
         }
     }
 
     private Long createId(IRubyObject object) {
         cleanIdentities();
         maxId += 2; // id must always be even
         Long longMaxId = new Long(maxId);
         identities.put(longMaxId, new IdReference(object, maxId, deadIdentityReferences));
         identitiesByObject.put(object, longMaxId);
         return longMaxId;
     }
 
     public IRubyObject id2ref(long id) {
         synchronized (identities) {
             cleanIdentities();
             IdReference reference = (IdReference) identities.get(new Long(id));
             if (reference == null)
                 return null;
             return (IRubyObject) reference.get();
         }
     }
 
     private void cleanIdentities() {
         IdReference ref;
         while ((ref = (IdReference) deadIdentityReferences.poll()) != null)
             identities.remove(new Long(ref.id()));
     }
-
-    private Map finalizableWeakRefs          = new ConcurrentHashMap();
-    private Map weakRefs            = new ConcurrentHashMap();
-    private LinkedBlockingQueue finalizersToRun   = new LinkedBlockingQueue();
-    private FinalizerThread finalizerThread = new FinalizerThread();
-
-    private class FinalizerThread extends Thread {
-        private boolean running;
-        public FinalizerThread() {
-            super("Ruby Finalizer Thread");
-            setDaemon(true);
-        }
-
-        public void run() {
-            while (true) {
-                Runnable finalizer;
-                try {
-                    while ((finalizer = (Runnable) finalizersToRun.poll(100, TimeUnit.MILLISECONDS)) != null) {
-                        try {
-                            finalizer.run();
-                        } catch (Exception e) {
-                        }
-                    }
-                } catch (InterruptedException ie) {
-                    break;
-                }
-                synchronized (this) {
-                    if (finalizersToRun.peek() == null && !running) {
-                        break;
-                    }
-                }
-            }
-        }
-
-        public synchronized void startRunning() {
-            if(!running) {
-                running = true;
-                start();
-            }
-        }
-
-        public synchronized void stopRunning() {
-            running = false;
-        }
+    
+    public void addFinalizer(IRubyObject object, RubyProc proc) {
+        object.addFinalizer(proc);
     }
-
-    private class FinalizerEntry implements Runnable {
-        private long id;
-        private RubyProc proc;
-        private IRubyObject fid;
-        public FinalizerEntry(long id, RubyProc proc) {
-            this.id = id;
-            this.proc = proc;
-            this.fid = proc.getRuntime().newFixnum(id);
-        }
-
-        public void _finalize() {
-            finalizersToRun.offer(this);
-        }
-
-        public void run() {
-            proc.call(new IRubyObject[]{fid});
-        }
-    }
-
-    public void finishFinalizers() {
-        for(Iterator iter = finalizableWeakRefs.keySet().iterator();iter.hasNext();) {
-            Object key = iter.next();
-            WeakReferenceListNode weakRef = (WeakReferenceListNode)finalizableWeakRefs.get(key);
-            weakRef.finishFinalizers();
-        }
-        finalizerThread.stopRunning();
-        try { finalizerThread.join(); } catch (InterruptedException ie) {}
-    }
-
-    public void addFinalizer(IRubyObject obj, long id, RubyProc proc) {
-        WeakReferenceListNode finalizableWeakRef = (WeakReferenceListNode)finalizableWeakRefs.get(new Long(id));
-        if(finalizableWeakRef == null) {
-            finalizableWeakRef = new WeakReferenceListNode(obj, deadReferences, top);
-            finalizableWeakRefs.put(new Long(id),finalizableWeakRef);
-        }
-        synchronized(finalizableWeakRef) {
-            finalizableWeakRef.addFinalizer(new FinalizerEntry(id,proc));
-        }
-        finalizerThread.startRunning();
-    }
-
+    
     public void removeFinalizers(long id) {
-        WeakReferenceListNode fins = (WeakReferenceListNode)finalizableWeakRefs.get(new Long(id));
-        if(null != fins) {
-            fins.removeFinalizers();
+        IRubyObject object = id2ref(id);
+        if (object != null) {
+            object.removeFinalizers();
         }
-        finalizableWeakRefs.remove(new Long(id));
     }
-
+    
     public synchronized void add(IRubyObject object) {
         cleanup();
         top = new WeakReferenceListNode(object, deadReferences, top);
     }
 
     public synchronized Iterator iterator(RubyModule rubyClass) {
         final List objList = new ArrayList();
         WeakReferenceListNode current = top;
         while (current != null) {
             IRubyObject obj = (IRubyObject)current.get();
             if (obj != null && obj.isKindOf(rubyClass)) {
                 objList.add(current);
             }
 
             current = current.nextNode;
         }
 
         return new Iterator() {
             private Iterator iter = objList.iterator();
 
             public boolean hasNext() {
                 throw new UnsupportedOperationException();
             }
 
             public Object next() {
                 Object obj = null;
                 while (iter.hasNext()) {
                     WeakReferenceListNode node = (WeakReferenceListNode)iter.next();
 
                     obj = node.get();
 
                     if (obj != null) break;
                 }
                 return obj;
             }
 
             public void remove() {
                 throw new UnsupportedOperationException();
             }
         };
     }
 
     private synchronized void cleanup() {
         WeakReferenceListNode reference;
         while ((reference = (WeakReferenceListNode)deadReferences.poll()) != null) {
             reference.remove();
         }
     }
 
     private class WeakReferenceListNode extends WeakReference {
         private WeakReferenceListNode prevNode;
         private WeakReferenceListNode nextNode;
-        private List finalizers;
-        private long finalizerID;
 
         public WeakReferenceListNode(Object ref, ReferenceQueue queue, WeakReferenceListNode next) {
             super(ref, queue);
 
             this.nextNode = next;
             if (next != null) {
                 next.prevNode = this;
             }
         }
 
         public void remove() {
             synchronized (ObjectSpace.this) {
                 if (prevNode != null) {
                     prevNode.nextNode = nextNode;
                 } else {
                     top = nextNode;
                 }
                 if (nextNode != null) {
                     nextNode.prevNode = prevNode;
                 }
             }
-            
-            if (finalizerID != 0) {
-                finalizersToRun.addAll(finalizers);
-                finalizableWeakRefs.remove(new Long(finalizerID));
-            }
-        }
-        
-        public void addFinalizer(FinalizerEntry finalizer) {
-            if (finalizers == null) {
-                finalizers = new ArrayList();
-            }
-            finalizerID = finalizer.id;
-            finalizers.add(finalizer);
-        }
-        
-        public void removeFinalizers() {
-            finalizers = null;
-        }
-        
-        public void finishFinalizers() {
-            finalizersToRun.addAll(finalizers);
         }
     }
 
     private static class IdReference extends WeakReference {
         private final long id;
 
         public IdReference(IRubyObject object, long id, ReferenceQueue queue) {
             super(object, queue);
             this.id = id;
         }
 
         public long id() {
             return id;
         }
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index b7f15b2d05..7e17bb9cd6 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,537 +1,542 @@
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
 package org.jruby.runtime.builtin;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyFixnum;
+import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     /**
      *
      */
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * Return the ClassIndex value for the native type this object was
      * constructed from. Particularly useful for determining marshalling
      * format. All instances of subclasses of Hash, for example
      * are of Java type RubyHash, and so should utilize RubyHash marshalling
      * logic in addition to user-defined class marshalling logic.
      *
      * @return the ClassIndex of the native type this object was constructed from
      */
     int getNativeTypeIndex();
     
     /**
      * Gets a copy of the instance variables for this object, if any exist.
      * Returns null if this object has no instance variables.
      * "safe" in that it doesn't cause the instance var map to be created.
      *
      * @return A snapshot of the instance vars, or null if none.
      */
     Map safeGetInstanceVariables();
     
     /**
      * Returns true if the object has any instance variables, false otherwise.
      * "safe" in that it doesn't cause the instance var map to be created.
      *
      * @return true if the object has instance variables, false otherwise.
      */
     boolean safeHasInstanceVariables();
     
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
     
     /**
      *
      * @return
      */
     Map getInstanceVariables();
     
     /**
      *
      * 
      * @param instanceVariables 
      */
     void setInstanceVariables(Map instanceVariables);
     
     /**
      *
      * @return
      */
     Map getInstanceVariablesSnapshot();
     
     /**
      *
      * @param context
      * @param rubyclass
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param rubyclass
      * @param methodIndex
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block);
     // Used by the compiler, to allow visibility checks
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethod(ThreadContext context, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param args
      * @param block
      * @return
      */
     IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block);
     /**
      *
      * @param context
      * @param string
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string);
     /**
      *
      * @param context
      * @param string
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String string);
     /**
      *
      * @param context
      * @param string
      * @param aBlock
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, Block aBlock);
     /**
      *
      * @param context
      * @param string
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs, Block block);
     
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
     
     /**
      *
      * @return
      */
     boolean isTrue();
     
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
     
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
     
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
     
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
     
     /**
      *
      * @return
      */
     boolean isImmediate();
     
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
     
     /**
      * Infect this object using the taint of another object
      * @param obj
      * @return
      */
     IRubyObject infectBy(IRubyObject obj);
     
     /**
      * RubyMethod getRubyClass.
      * @return
      */
     RubyClass getMetaClass();
     
     /**
      *
      * @param metaClass
      */
     void setMetaClass(RubyClass metaClass);
     
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     RubyClass getSingletonClass();
     
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
     
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
     
     /**
      * RubyMethod getRuntime.
      * @return
      */
     Ruby getRuntime();
     
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
     
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(ThreadContext context, IRubyObject evalString, IRubyObject binding, String file);
     
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
     
     /**
      * Convert the object into a symbol name if possible.
      *
      * @return String the symbol name
      */
     String asSymbol();
     
     /** rb_obj_as_string
      * @return
      */
     RubyString asString();
     
     /**
      * Methods which perform to_xxx if the object has such a method
      * @return
      */
     RubyArray convertToArray();
     /**
      *
      * @return
      */
     RubyFloat convertToFloat();
     /**
      *
      * @return
      */
     RubyInteger convertToInteger();
     /**
      *
      * @return
      */
     RubyString convertToString();
     
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnError);
     
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(RubyClass targetType, int convertMethodIndex, String convertMethod);
    
     /**
      * 
      * @param targetType 
      * @param convertMethod 
      * @param raiseOnMissingMethod 
      * @param raiseOnWrongTypeResult 
      * @param allowNilThrough 
      * @return 
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough);
     
     /**
      *
      * @return
      */
     IRubyObject anyToString();
     
     /**
      *
      * @return
      */
     IRubyObject checkStringType();
     
     /**
      *
      * @return
      */
     IRubyObject checkArrayType();
 
     /**
      * RubyMethod dup.
      * @return
      */
     IRubyObject dup();
     
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
     
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
     
     
     /**
      *
      * @return
      */
     boolean isSingleton();
     
     /**
      *
      * @return
      */
     Iterator instanceVariableNames();
     
     /**
      * Our version of Data_Wrap_Struct.
      *
      * This method will just set a private pointer to the object provided. This pointer is transient
      * and will not be accessible from Ruby.
      *
      * @param obj the object to wrap
      */
     void dataWrapStruct(Object obj);
     
     /**
      * Our version of Data_Get_Struct.
      *
      * Returns a wrapped data value if there is one, otherwise returns null.
      *
      * @return the object wrapped.
      */
     Object dataGetStruct();
     
     /**
      *
      * @return
      */
     RubyFixnum id();
     
     /**
      *
      * @param context
      * @param other
      * @return
      */
     IRubyObject equalInternal(final ThreadContext context, final IRubyObject other);
+        
+    public void addFinalizer(RubyProc finalizer);
+
+    public void removeFinalizers();
 }
