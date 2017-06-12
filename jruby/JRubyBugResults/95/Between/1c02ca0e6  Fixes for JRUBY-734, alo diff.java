diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 7e43fa31c6..83c2d1e5f0 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1726 +1,1743 @@
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
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
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
 import org.jruby.runtime.builtin.meta.StringMetaClass;
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
     private StringMetaClass stringClass;
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
 
+    // FIXME moved this hear to get what's obviously a utility method out of IRubyObject.
+    // perhaps security methods should find their own centralized home at some point.
+    public void checkSafeString(IRubyObject object) {
+        if (getSafeLevel() > 0 && object.isTaint()) {
+            ThreadContext tc = getCurrentContext();
+            if (tc.getFrameName() != null) {
+                throw newSecurityError("Insecure operation - " + tc.getFrameName());
+            }
+            throw newSecurityError("Insecure operation: -r");
+        }
+        secure(4);
+        if (!(object instanceof RubyString)) {
+            throw newTypeError(
+                "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
+        }
+    }
+
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
         nilObject = new RubyNil(this);
         trueObject = new RubyBoolean(this, true);
         falseObject = new RubyBoolean(this, false);
 
         verbose = falseObject;
         debug = falseObject;
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
         
         // init selector table, now that classes are done adding methods
         selectorTable.init();
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
 
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
-                self.extendObject(context.getRubyClass());
+                context.getRubyClass().extend_object(self);
             }
 
             Node node = parse(source, scriptName, null);
-            self.eval(node);
+            EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
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
-                self.extendObject(context.getRubyClass());
+                context.getRubyClass().extend_object(self);
             }
 
-            self.eval(node);
+            EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
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
diff --git a/src/org/jruby/RubyArgsFile.java b/src/org/jruby/RubyArgsFile.java
index 256055d43a..f0fb892b2c 100644
--- a/src/org/jruby/RubyArgsFile.java
+++ b/src/org/jruby/RubyArgsFile.java
@@ -1,415 +1,415 @@
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
 package org.jruby;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyArgsFile extends RubyObject {
 
     public RubyArgsFile(Ruby runtime) {
         super(runtime, runtime.getObject());
     }
 
     private IRubyObject currentFile = null;
     private int currentLineNumber;
     
     public void setCurrentLineNumber(int newLineNumber) {
         this.currentLineNumber = newLineNumber;
     }
     
     public void initArgsFile() {
-        extendObject(getRuntime().getModule("Enumerable"));
+        getRuntime().getModule("Enumerable").extend_object(this);
         
         getRuntime().defineReadonlyVariable("$<", this);
         getRuntime().defineGlobalConstant("ARGF", this);
         
         CallbackFactory callbackFactory = getRuntime().callbackFactory(RubyArgsFile.class);
         
         getMetaClass().defineFastMethod("read", callbackFactory.getFastOptMethod("read"));
         getMetaClass().defineFastMethod("getc", callbackFactory.getFastMethod("getc"));
         getMetaClass().defineFastMethod("readchar", callbackFactory.getFastMethod("readchar"));
         getMetaClass().defineFastMethod("seek", callbackFactory.getFastOptMethod("seek"));
         getMetaClass().defineFastMethod("pos=", callbackFactory.getFastMethod("set_pos",IRubyObject.class));
         getMetaClass().defineFastMethod("tell", callbackFactory.getFastMethod("tell"));
         getMetaClass().defineFastMethod("pos", callbackFactory.getFastMethod("tell"));
 
         getMetaClass().defineFastMethod("rewind", callbackFactory.getFastMethod("rewind"));
 
         getMetaClass().defineFastMethod("eof", callbackFactory.getFastMethod("eof"));
         getMetaClass().defineFastMethod("eof?", callbackFactory.getFastMethod("eof"));
         getMetaClass().defineFastMethod("binmode", callbackFactory.getFastMethod("binmode"));
 
         getMetaClass().defineFastMethod("fileno", callbackFactory.getFastMethod("fileno"));
         getMetaClass().defineFastMethod("to_i", callbackFactory.getFastMethod("fileno"));
         getMetaClass().defineFastMethod("to_io", callbackFactory.getFastMethod("to_io"));
 
         getMetaClass().defineMethod("each_byte", callbackFactory.getMethod("each_byte"));
         getMetaClass().defineMethod("each", callbackFactory.getOptMethod("each_line"));
         getMetaClass().defineMethod("each_line", callbackFactory.getOptMethod("each_line"));
 
         getMetaClass().defineFastMethod("path", callbackFactory.getFastMethod("filename"));
         getMetaClass().defineFastMethod("filename", callbackFactory.getFastMethod("filename"));
         getMetaClass().defineFastMethod("file", callbackFactory.getFastMethod("file"));
         getMetaClass().defineFastMethod("skip", callbackFactory.getFastMethod("skip"));
         getMetaClass().defineFastMethod("close", callbackFactory.getFastMethod("close_m"));
         getMetaClass().defineFastMethod("closed?", callbackFactory.getFastMethod("closed_p"));
         getMetaClass().defineFastMethod("gets", callbackFactory.getFastOptMethod("gets"));
         getMetaClass().defineFastMethod("readline", callbackFactory.getFastOptMethod("readline"));
         getMetaClass().defineFastMethod("readlines", callbackFactory.getFastOptMethod("readlines"));
 		
         getMetaClass().defineFastMethod("lineno", callbackFactory.getFastMethod("lineno"));
         getMetaClass().defineFastMethod("lineno=", callbackFactory.getFastMethod("set_lineno",IRubyObject.class));
         getMetaClass().defineFastMethod("to_a", callbackFactory.getFastOptMethod("readlines"));
         getMetaClass().defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
 
         getRuntime().defineReadonlyVariable("$FILENAME", getRuntime().newString("-"));
 
         // This is ugly.  nextArgsFile both checks existence of another
         // file and the setup of any files.  On top of that it handles
         // the logic for figuring out stdin versus a list of files.
         // I hacked this to make a null currentFile indicate that things
         // have not been set up yet.  This seems fragile, but it at least
         // it passes tests now.
         //        currentFile = (RubyIO) getRuntime().getGlobalVariables().get("$stdin");
     }
 
     protected boolean nextArgsFile() {
         RubyArray args = (RubyArray)getRuntime().getGlobalVariables().get("$*");
 
         if (args.getLength() == 0) {
             if (currentFile == null) { 
                 currentFile = getRuntime().getGlobalVariables().get("$stdin");
                 ((RubyString) getRuntime().getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer("-"));
                 currentLineNumber = 0;
                 return true;
             }
 
             return false;
         }
 
         String filename = ((RubyString) args.shift()).toString();
         ((RubyString) getRuntime().getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer(filename));
 
         if (filename.equals("-")) {
             currentFile = getRuntime().getGlobalVariables().get("$stdin");
         } else {
             currentFile = new RubyFile(getRuntime(), filename); 
         }
 
         return true;
     }
 
     public IRubyObject fileno() {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream");
         }
         return ((RubyIO)currentFile).fileno();
     }
 
     public IRubyObject to_io() {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream");
         }
         return currentFile;
     }
     
     public IRubyObject internalGets(IRubyObject[] args) {
         if (currentFile == null && !nextArgsFile()) {
             return getRuntime().getNil();
         }
         
         ThreadContext context = getRuntime().getCurrentContext();
         
         IRubyObject line = currentFile.callMethod(context, "gets", args);
         
         while (line instanceof RubyNil) {
             currentFile.callMethod(context, "close");
             if (! nextArgsFile()) {
                 currentFile = null;
                 return line;
         	}
             line = currentFile.callMethod(context, "gets", args);
         }
         
         currentLineNumber++;
         getRuntime().getGlobalVariables().set("$.", getRuntime().newFixnum(currentLineNumber));
         
         return line;
     }
     
     // ARGF methods
 
     /** Read a line.
      * 
      */
     public IRubyObject gets(IRubyObject[] args) {
         IRubyObject result = internalGets(args);
 
         if (!result.isNil()) {
             getRuntime().getCurrentContext().setLastline(result);
         }
 
         return result;
     }
     
     /** Read a line.
      * 
      */
     public IRubyObject readline(IRubyObject[] args) {
         IRubyObject line = gets(args);
 
         if (line.isNil()) {
             throw getRuntime().newEOFError();
         }
         
         return line;
     }
 
     public RubyArray readlines(IRubyObject[] args) {
         IRubyObject[] separatorArgument;
         if (args.length > 0) {
             if (!args[0].isKindOf(getRuntime().getNilClass()) &&
                 !args[0].isKindOf(getRuntime().getString())) {
                 throw getRuntime().newTypeError(args[0], 
                         getRuntime().getString());
             } 
             separatorArgument = new IRubyObject[] { args[0] };
         } else {
             separatorArgument = IRubyObject.NULL_ARRAY;
         }
 
         RubyArray result = getRuntime().newArray();
         IRubyObject line;
         while (! (line = internalGets(separatorArgument)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     public IRubyObject each_byte(Block block) {
         IRubyObject bt;
         ThreadContext ctx = getRuntime().getCurrentContext();
 
         while(!(bt = getc()).isNil()) {
             block.yield(ctx, bt);
         }
 
         return this;
     }
 
     /** Invoke a block for each line.
      * 
      */
     public IRubyObject each_line(IRubyObject[] args, Block block) {
         IRubyObject nextLine = internalGets(args);
         
         while (!nextLine.isNil()) {
         	block.yield(getRuntime().getCurrentContext(), nextLine);
         	nextLine = internalGets(args);
         }
         
         return this;
     }
     
 	public IRubyObject file() {
         if(currentFile == null && !nextArgsFile()) {
             return getRuntime().getNil();
         }
         
         return currentFile;
     }
 
 	public IRubyObject skip() {
         currentFile = null;
         return this;
     }
 
 
 	public IRubyObject close_m() {
         if(currentFile == null && !nextArgsFile()) {
             return this;
         }
         currentFile = null;
         currentLineNumber = 0;
         return this;
     }
 
 	public IRubyObject closed_p() {
         if(currentFile == null && !nextArgsFile()) {
             return this;
         }
         return ((RubyIO)currentFile).closed();
     }
 
 	public IRubyObject binmode() {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream");
         }
         
         return ((RubyIO)currentFile).binmode();
     }
 
 	public IRubyObject lineno() {
         return getRuntime().newFixnum(currentLineNumber);
     }
 
 	public IRubyObject tell() {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream to tell");
         }
         return ((RubyIO)currentFile).pos();
     }
 
  	public IRubyObject rewind() {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream to rewind");
         }
         return ((RubyIO)currentFile).rewind();
     }
 
 	public IRubyObject eof() {
         if(currentFile != null && !nextArgsFile()) {
             return getRuntime().getTrue();
         }
         return ((RubyIO)currentFile).eof();
     }
 
 	public IRubyObject set_pos(IRubyObject offset) {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream to set position");
         }
         return ((RubyIO)currentFile).pos_set(offset);
     }
 
  	public IRubyObject seek(IRubyObject[] args) {
         if(currentFile == null && !nextArgsFile()) {
             throw getRuntime().newArgumentError("no stream to seek");
         }
         return ((RubyIO)currentFile).seek(args);
     }
 
 	public IRubyObject set_lineno(IRubyObject line) {
         currentLineNumber = RubyNumeric.fix2int(line);
         return getRuntime().getNil();
     }
 
 	public IRubyObject readchar() {
         IRubyObject c = getc();
         if(c.isNil()) {
             throw getRuntime().newEOFError();
         }
         return c;
     }
 
 	public IRubyObject getc() {
         IRubyObject bt;
         while(true) {
             if(currentFile == null && !nextArgsFile()) {
                 return getRuntime().getNil();
             }
             if(!(currentFile instanceof RubyFile)) {
                 bt = currentFile.callMethod(getRuntime().getCurrentContext(),"getc");
             } else {
                 bt = ((RubyIO)currentFile).getc();
             }
             if(bt.isNil()) {
                 currentFile = null;
                 continue;
             }
             return bt;
         }
     }
 
  	public IRubyObject read(IRubyObject[] args) {
         IRubyObject tmp, str, length;
         long len = 0;
         checkArgumentCount(args,0,2);
         if(args.length > 0) {
             length = args[0];
             if(args.length > 1) {
                 str = args[1];
             } else {
                 str = getRuntime().getNil();
             }
         } else {
             length = getRuntime().getNil();
             str = getRuntime().getNil();
         }
 
         if(!length.isNil()) {
             len = RubyNumeric.num2long(length);
         }
         if(!str.isNil()) {
             str = str.convertToString();
             ((RubyString)str).getByteList().length(0);
             ((RubyString)str).stringMutated();
             args[1] = getRuntime().getNil();
         }
         while(true) {
             if(currentFile == null && !nextArgsFile()) {
                 return str;
             }
             if(!(currentFile instanceof RubyIO)) {
                 tmp = currentFile.callMethod(getRuntime().getCurrentContext(),"read",args);
             } else {
                 tmp = ((RubyIO)currentFile).read(args);
             }
             if(str.isNil()) {
                 str = tmp;
             } else if(!tmp.isNil()) {
                 ((RubyString)str).append(tmp);
             }
             if(tmp.isNil() || length.isNil()) {
                 currentFile = null;
                 continue;
             } else if(args.length >= 1) {
                 if(((RubyString)str).getByteList().length() < len) {
                     len -= ((RubyString)str).getByteList().length();
                     args[0] = getRuntime().newFixnum(len);
                     continue;
                 }
             }
             return str;
         }
     }
 
 	public RubyString filename() {
         return (RubyString)getRuntime().getGlobalVariables().get("$FILENAME");
     }
 
 	public IRubyObject to_s() {
         return getRuntime().newString("ARGF");
     }
 }
diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index 6f6135c910..b7421aaba8 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,459 +1,459 @@
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
 package org.jruby;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Glob;
 import org.jruby.util.JRubyFile;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 public class RubyDir extends RubyObject {
 	// What we passed to the constructor for method 'path'
     private RubyString    path;
     protected JRubyFile      dir;
     private   String[]  snapshot;   // snapshot of contents of directory
     private   int       pos;        // current position in directory
     private boolean isOpen = true;
 
     public RubyDir(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     private static ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyDir(runtime, klass);
         }
     };
 
     public static RubyClass createDirClass(Ruby runtime) {
         RubyClass dirClass = runtime.defineClass("Dir", runtime.getObject(), DIR_ALLOCATOR);
 
         dirClass.includeModule(runtime.getModule("Enumerable"));
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyDir.class);
 
         dirClass.getMetaClass().defineMethod("glob", callbackFactory.getSingletonMethod("glob", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineFastMethod("entries", callbackFactory.getFastSingletonMethod("entries", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineMethod("[]", callbackFactory.getSingletonMethod("glob", RubyKernel.IRUBY_OBJECT));
         // dirClass.defineAlias("[]", "glob");
         dirClass.getMetaClass().defineMethod("chdir", callbackFactory.getOptSingletonMethod("chdir"));
         dirClass.getMetaClass().defineFastMethod("chroot", callbackFactory.getFastSingletonMethod("chroot", RubyKernel.IRUBY_OBJECT));
         //dirClass.defineSingletonMethod("delete", callbackFactory.getSingletonMethod(RubyDir.class, "delete", RubyString.class));
         dirClass.getMetaClass().defineMethod("foreach", callbackFactory.getSingletonMethod("foreach", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineFastMethod("getwd", callbackFactory.getFastSingletonMethod("getwd"));
         dirClass.getMetaClass().defineFastMethod("pwd", callbackFactory.getFastSingletonMethod("getwd"));
         // dirClass.defineAlias("pwd", "getwd");
         dirClass.getMetaClass().defineFastMethod("mkdir", callbackFactory.getFastOptSingletonMethod("mkdir"));
         dirClass.getMetaClass().defineMethod("open", callbackFactory.getSingletonMethod("open", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineFastMethod("rmdir", callbackFactory.getFastSingletonMethod("rmdir", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineFastMethod("unlink", callbackFactory.getFastSingletonMethod("rmdir", RubyKernel.IRUBY_OBJECT));
         dirClass.getMetaClass().defineFastMethod("delete", callbackFactory.getFastSingletonMethod("rmdir", RubyKernel.IRUBY_OBJECT));
         // dirClass.defineAlias("unlink", "rmdir");
         // dirClass.defineAlias("delete", "rmdir");
 
         dirClass.defineFastMethod("close", callbackFactory.getFastMethod("close"));
         dirClass.defineMethod("each", callbackFactory.getMethod("each"));
         dirClass.defineFastMethod("entries", callbackFactory.getFastMethod("entries"));
         dirClass.defineFastMethod("path", callbackFactory.getFastMethod("path"));
         dirClass.defineFastMethod("tell", callbackFactory.getFastMethod("tell"));
         dirClass.defineAlias("pos", "tell");
         dirClass.defineFastMethod("seek", callbackFactory.getFastMethod("seek", RubyKernel.IRUBY_OBJECT));
         dirClass.defineFastMethod("pos=", callbackFactory.getFastMethod("setPos", RubyKernel.IRUBY_OBJECT));
         dirClass.defineFastMethod("read", callbackFactory.getFastMethod("read"));
         dirClass.defineFastMethod("rewind", callbackFactory.getFastMethod("rewind"));
         dirClass.defineMethod("initialize", callbackFactory.getMethod("initialize", RubyKernel.IRUBY_OBJECT));
 
         return dirClass;
     }
 
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     public IRubyObject initialize(IRubyObject _newPath, Block unusedBlock) {
         RubyString newPath = _newPath.convertToString();
-        newPath.checkSafeString();
+        getRuntime().checkSafeString(newPath);
         dir = JRubyFile.create(getRuntime().getCurrentDirectory(),newPath.toString());
         if (!dir.isDirectory()) {
             dir = null;
             throw getRuntime().newErrnoENOENTError(newPath.toString() + " is not a directory");
         }
         path = newPath;
 		List snapshotList = new ArrayList();
 		snapshotList.add(".");
 		snapshotList.add("..");
 		snapshotList.addAll(getContents(dir));
 		snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 		pos = 0;
 
         return this;
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
 
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.  
      */
     public static IRubyObject glob(IRubyObject recv, IRubyObject pat, Block block) {
         String pattern = pat.convertToString().toString();
         String[] files = new Glob(recv.getRuntime().getCurrentDirectory(), pattern).getNames();
         if (block.isGiven()) {
             ThreadContext context = recv.getRuntime().getCurrentContext();
             
             for (int i = 0; i < files.length; i++) {
                 block.yield(context, JavaUtil.convertJavaToRuby(recv.getRuntime(), files[i]));
             }
             return recv.getRuntime().getNil();
         }            
         return recv.getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(recv.getRuntime(), files));
     }
 
     /**
      * @return all entries for this Dir
      */
     public RubyArray entries() {
         return getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(getRuntime(), snapshot));
     }
     
     /**
      * Returns an array containing all of the filenames in the given directory.
      */
     public static RubyArray entries(IRubyObject recv, IRubyObject path) {
         final JRubyFile directory = JRubyFile.create(recv.getRuntime().getCurrentDirectory(),path.convertToString().toString());
         
         if (!directory.isDirectory()) {
             throw recv.getRuntime().newErrnoENOENTError("No such directory");
         }
         List fileList = getContents(directory);
 		fileList.add(0,".");
 		fileList.add(1,"..");
         Object[] files = fileList.toArray();
         return recv.getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(recv.getRuntime(), files));
     }
 
     /** Changes the current directory to <code>path</code> */
     public static IRubyObject chdir(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.checkArgumentCount(args, 0, 1);
         RubyString path = args.length == 1 ? 
             (RubyString) args[0].convertToString() : getHomeDirectoryPath(recv); 
         JRubyFile dir = getDir(recv.getRuntime(), path.toString(), true);
         String realPath = null;
         String oldCwd = recv.getRuntime().getCurrentDirectory();
         
         // We get canonical path to try and flatten the path out.
         // a dir '/subdir/..' should return as '/'
         // cnutter: Do we want to flatten path out?
         try {
             realPath = dir.getCanonicalPath();
         } catch (IOException e) {
             realPath = dir.getAbsolutePath();
         }
         
         IRubyObject result = null;
         if (block.isGiven()) {
         	// FIXME: Don't allow multiple threads to do this at once
             recv.getRuntime().setCurrentDirectory(realPath);
             try {
                 result = block.yield(recv.getRuntime().getCurrentContext(), path);
             } finally {
                 recv.getRuntime().setCurrentDirectory(oldCwd);
             }
         } else {
         	recv.getRuntime().setCurrentDirectory(realPath);
         	result = recv.getRuntime().newFixnum(0);
         }
         
         return result;
     }
 
     /**
      * Changes the root directory (only allowed by super user).  Not available
      * on all platforms.
      */
     public static IRubyObject chroot(IRubyObject recv, IRubyObject path) {
         throw recv.getRuntime().newNotImplementedError("chroot not implemented: chroot is non-portable and is not supported.");
     }
 
     /**
      * Deletes the directory specified by <code>path</code>.  The directory must
      * be empty.
      */
     public static IRubyObject rmdir(IRubyObject recv, IRubyObject path) {
         JRubyFile directory = getDir(recv.getRuntime(), path.convertToString().toString(), true);
         
         if (!directory.delete()) {
             throw recv.getRuntime().newSystemCallError("No such directory");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     public static IRubyObject foreach(IRubyObject recv, IRubyObject _path, Block block) {
         RubyString path = _path.convertToString();
-        path.checkSafeString();
+        recv.getRuntime().checkSafeString(path);
 
         RubyClass dirClass = recv.getRuntime().getClass("Dir");
         RubyDir dir = (RubyDir) dirClass.newInstance(new IRubyObject[] { path }, block);
         
         dir.each(block);
         return recv.getRuntime().getNil();
     }
 
     /** Returns the current directory. */
     public static RubyString getwd(IRubyObject recv) {
         return recv.getRuntime().newString(recv.getRuntime().getCurrentDirectory());
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         if (args.length < 1) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
         if (args.length > 2) {
             throw recv.getRuntime().newArgumentError(args.length, 2);
         }
 
-        args[0].checkSafeString();
+        recv.getRuntime().checkSafeString(args[0]);
         String path = args[0].toString();
 
         File newDir = getDir(recv.getRuntime(), path, false);
         if (File.separatorChar == '\\') {
             newDir = new File(newDir.getPath());
         }
         
         return newDir.mkdirs() ? RubyFixnum.zero(recv.getRuntime()) :
             RubyFixnum.one(recv.getRuntime());
     }
 
     /**
      * Returns a new directory object for <code>path</code>.  If a block is
      * provided, a new directory object is passed to the block, which closes the
      * directory object before terminating.
      */
     public static IRubyObject open(IRubyObject recv, IRubyObject path, Block block) {
         RubyDir directory = 
             (RubyDir) recv.getRuntime().getClass("Dir").newInstance(
                     new IRubyObject[] { path }, Block.NULL_BLOCK);
 
         if (!block.isGiven()) return directory;
         
         try {
             block.yield(recv.getRuntime().getCurrentContext(), directory);
         } finally {
             directory.close();
         }
             
         return recv.getRuntime().getNil();
     }
 
 // ----- Ruby Instance Methods -------------------------------------------------
 
     /**
      * Closes the directory stream.
      */
     public IRubyObject close() {
         // Make sure any read()s after close fail.
         isOpen = false;
 
         return getRuntime().getNil();
     }
 
     /**
      * Executes the block once for each entry in the directory.
      */
     public IRubyObject each(Block block) {
         String[] contents = snapshot;
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i=0; i<contents.length; i++) {
             block.yield(context, getRuntime().newString(contents[i]));
         }
         return this;
     }
 
     /**
      * Returns the current position in the directory.
      */
     public RubyInteger tell() {
         return getRuntime().newFixnum(pos);
     }
 
     /**
      * Moves to a position <code>d</code>.  <code>pos</code> must be a value
      * returned by <code>tell</code> or 0.
      */
     public IRubyObject seek(IRubyObject newPos) {
         setPos(newPos);
         return this;
     }
     
     public IRubyObject setPos(IRubyObject newPos) {
         this.pos = RubyNumeric.fix2int(newPos);
         return newPos;
     }
 
     public IRubyObject path() {
         if (!isOpen) {
             throw getRuntime().newIOError("closed directory");
         }
         
         return path;
     }
 
     /** Returns the next entry from this directory. */
     public IRubyObject read() {
 	if (!isOpen) {
 	    throw getRuntime().newIOError("Directory already closed");
 	}
 
         if (pos >= snapshot.length) {
             return getRuntime().getNil();
         }
         RubyString result = getRuntime().newString(snapshot[pos]);
         pos++;
         return result;
     }
 
     /** Moves position in this directory to the first entry. */
     public IRubyObject rewind() {
         pos = 0;
         return getRuntime().newFixnum(pos);
     }
 
 // ----- Helper Methods --------------------------------------------------------
 
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(),path);
         boolean isDirectory = result.isDirectory();
         
         if (mustExist && !isDirectory) {
             throw runtime.newErrnoENOENTError(path + " is not a directory");
         } else if (!mustExist && isDirectory) {
             throw runtime.newErrnoEEXISTError("File exists - " + path); 
         }
 
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Java Strings.
      */
     protected static List getContents(File directory) {
         String[] contents = directory.list();
         List result = new ArrayList();
 
         // If an IO exception occurs (something odd, but possible)
         // A directory may return null.
         if (contents != null) {
             for (int i=0; i<contents.length; i++) {
                 result.add(contents[i]);
             }
         }
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Ruby Strings.
      */
     protected static List getContents(File directory, Ruby runtime) {
         List result = new ArrayList();
         String[] contents = directory.list();
         
         for (int i = 0; i < contents.length; i++) {
             result.add(runtime.newString(contents[i]));
         }
         return result;
     }
 	
 	/*
 	 * Poor mans find home directory.  I am not sure how windows ruby behaves with '~foo', but
 	 * this mostly will work on any unix/linux/cygwin system.  When someone wants to extend this
 	 * to include the windows way, we should consider moving this to an external ruby file.
 	 */
 	public static IRubyObject getHomeDirectoryPath(IRubyObject recv, String user) {
 		// TODO: Having a return where I set user inside readlines created a JumpException.  It seems that
 		// evalScript should catch that and return?
 		return recv.getRuntime().evalScript("File.open('/etc/passwd') do |f| f.readlines.each do" +
 				"|l| f = l.split(':'); return f[5] if f[0] == '" + user + "'; end; end; nil");
 	}
 	
 	public static RubyString getHomeDirectoryPath(IRubyObject recv) {
 		RubyHash hash = (RubyHash) recv.getRuntime().getObject().getConstant("ENV_JAVA");
 		IRubyObject home = hash.aref(recv.getRuntime().newString("user.home"));
 		
 		if (home == null || home.isNil()) {
 			home = hash.aref(recv.getRuntime().newString("LOGDIR"));
 		}
 		
 		if (home == null || home.isNil()) {
 			throw recv.getRuntime().newArgumentError("user.home/LOGDIR not set");
 		}
 		
 		return (RubyString) home;
 	}
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index f57cb6120f..cef43e44fe 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,307 +1,307 @@
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
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.Sprintf;
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
     
 	public RubyFile(Ruby runtime, RubyClass type) {
 	    super(runtime, type);
 	}
 
 	public RubyFile(Ruby runtime, String path) {
 		this(runtime, path, open(runtime, path));
     }
 
 	// use static function because constructor call must be first statement in above constructor 
 	private static InputStream open(Ruby runtime, String path) {
 		try {
 			return new FileInputStream(path);
 		} catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
         }
 	}
     
 	// XXX This constructor is a hack to implement the __END__ syntax.
 	//     Converting a reader back into an InputStream doesn't generally work.
 	public RubyFile(Ruby runtime, String path, final Reader reader) {
 		this(runtime, path, new InputStream() {
 			public int read() throws IOException {
 				return reader.read();
 			}
 		});
 	}
 	
 	private RubyFile(Ruby runtime, String path, InputStream in) {
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
 
 	public IRubyObject initialize(IRubyObject[] args, Block block) {
 	    if (args.length == 0) {
 	        throw getRuntime().newArgumentError(0, 1);
 	    }
 
-	    args[0].checkSafeString();
+	    getRuntime().checkSafeString(args[0]);
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
 	    
 	    if (block.isGiven()) {
 	        // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
 	    }
 	    return this;
 	}
 
     public IRubyObject chmod(IRubyObject[] args) {
         checkArgumentCount(args, 1, 1);
         
         RubyInteger mode = args[0].convertToInteger();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
             
         try {
             Process chown = Runtime.getRuntime().exec("chmod " + Sprintf.sprintf(getRuntime(), "%o", mode.getLongValue()) + " " + path);
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
         return getRuntime().newRubyFileStat(path);
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
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index e7c526d768..691d870d32 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,819 +1,819 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
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
 
 import java.io.IOException;
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Hash class.
  *
  * @author  jpetersen
  */
 public class RubyHash extends RubyObject implements Map {
     private Map valueMap;
     // Place we capture any explicitly set proc so we can return it for default_proc
     private IRubyObject capturedDefaultProc;
     private static final Callback NIL_DEFAULT_VALUE  = new Callback() {
         public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
             return recv.getRuntime().getNil();
         }
     
         public Arity getArity() {
             return Arity.optional();
         }
     };
     
     // Holds either default value or default proc.  Executing whatever is here will return the
     // correct default value.
     private Callback defaultValueCallback;
     
     private boolean isRehashing = false;
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getClass("Hash"));
         this.valueMap = new HashMap();
         this.capturedDefaultProc = runtime.getNil();
         setDefaultValue(defaultValue);
     }
 
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getClass("Hash"));
         this.valueMap = new HashMap(valueMap);
         this.capturedDefaultProc = runtime.getNil();
         setDefaultValue(defaultValue);
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }
     
     public IRubyObject getDefaultValue(IRubyObject[] args, Block unusedBlock) {
         if(defaultValueCallback == null || (args.length == 0 && !capturedDefaultProc.isNil())) {
             return getRuntime().getNil();
         }
         return defaultValueCallback.execute(this, args, Block.NULL_BLOCK);
     }
 
     public IRubyObject setDefaultValue(final IRubyObject defaultValue) {
         capturedDefaultProc = getRuntime().getNil();
         if (defaultValue == getRuntime().getNil()) {
             defaultValueCallback = NIL_DEFAULT_VALUE;
         } else {
             defaultValueCallback = new Callback() {
                 public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                     return defaultValue;
                 }
 
                 public Arity getArity() {
                     return Arity.optional();
                 }
             };
         }
         
         return defaultValue;
     }
 
     public void setDefaultProc(final RubyProc newProc) {
         final IRubyObject self = this;
         capturedDefaultProc = newProc;
         defaultValueCallback = new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                 IRubyObject[] nargs = args.length == 0 ? new IRubyObject[] { self } :
                      new IRubyObject[] { self, args[0] };
 
                 return newProc.call(nargs);
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
     }
     
     public IRubyObject default_proc(Block unusedBlock) {
         return capturedDefaultProc;
     }
 
     public Map getValueMap() {
         return valueMap;
     }
 
     public void setValueMap(Map valueMap) {
         this.valueMap = valueMap;
     }
 
 	/**
 	 * gets an iterator on a copy of the keySet.
 	 * modifying the iterator will NOT modify the map.
 	 * if the map is modified while iterating on this iterator, the iterator
 	 * will not be invalidated but the content will be the same as the old one.
 	 * @return the iterator
 	 **/
 	private Iterator keyIterator() {
 		return new ArrayList(valueMap.keySet()).iterator();
 	}
 
 	private Iterator valueIterator() {
 		return new ArrayList(valueMap.values()).iterator();
 	}
 
 
 	/**
 	 * gets an iterator on the entries.
 	 * modifying this iterator WILL modify the map.
 	 * the iterator will be invalidated if the map is modified.
 	 * @return the iterator
 	 */
 	private Iterator modifiableEntryIterator() {
 		return valueMap.entrySet().iterator();
 	}
 
 	/**
 	 * gets an iterator on a copy of the entries.
 	 * modifying this iterator will NOT modify the map.
 	 * if the map is modified while iterating on this iterator, the iterator
 	 * will not be invalidated but the content will be the same as the old one.
 	 * @return the iterator
 	 */
 	private Iterator entryIterator() {
 		return new ArrayList(valueMap.entrySet()).iterator();		//in general we either want to modify the map or make sure we don't when we use this, so skip the copy
 	}
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("Hash");
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify hash");
         }
     }
 
     private int length() {
         return valueMap.size();
     }
 
     // Hash methods
 
     public static RubyHash newHash(Ruby runtime) {
     	return new RubyHash(runtime);
     }
 
 	public static RubyHash newHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
 		assert defaultValue != null;
 		
 		return new RubyHash(runtime, valueMap, defaultValue);
 	}
 
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             setDefaultProc(getRuntime().newProc(false, block));
         } else if (args.length > 0) {
             modify();
 
             setDefaultValue(args[0]);
         }
         return this;
     }
     
     public IRubyObject inspect() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = getRuntime().getCurrentContext();
         
             for (Iterator iter = valueMap.entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 IRubyObject key = (IRubyObject) entry.getKey();
                 IRubyObject value = (IRubyObject) entry.getValue();
                 if (!firstEntry) {
                     sb.append(sep);
                 }
             
                 sb.append(key.callMethod(context, "inspect")).append(arrow);
                 sb.append(value.callMethod(context, "inspect"));
                 firstEntry = false;
             }
             sb.append("}");
             return getRuntime().newString(sb.toString());
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(length());
     }
 
     public RubyBoolean empty_p() {
         return length() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, length());
         
         for(Iterator iter = valueMap.entrySet().iterator(); iter.hasNext();) {
             Map.Entry entry = (Map.Entry) iter.next();
             result.append(RubyArray.newArray(runtime, (IRubyObject) entry.getKey(), (IRubyObject) entry.getValue()));
         }
         return result;
     }
 
     public IRubyObject to_s() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     public RubyHash rehash() {
         modify();
         try {
             isRehashing = true;
             valueMap = new HashMap(valueMap);
         } finally {
             isRehashing = false;
         }
         return this;
     }
 
     public RubyHash to_hash() {
         return this;
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         modify();
         
         if (!(key instanceof RubyString) || valueMap.get(key) != null) {
             valueMap.put(key, value);
         } else {
             IRubyObject realKey = key.dup();
             realKey.setFrozen(true);
             valueMap.put(realKey, value);
         }
         return value;
     }
 
     public IRubyObject aref(IRubyObject key) {
         IRubyObject value = (IRubyObject) valueMap.get(key);
 
         return value != null ? value : callMethod(getRuntime().getCurrentContext(), "default", new IRubyObject[] {key});
     }
 
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         IRubyObject key = args[0];
         IRubyObject result = (IRubyObject) valueMap.get(key);
         if (result == null) {
             if (args.length > 1) return args[1]; 
                 
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key); 
 
             throw getRuntime().newIndexError("key not found");
         }
         return result;
     }
 
 
     public RubyBoolean has_key(IRubyObject key) {
         return getRuntime().newBoolean(valueMap.containsKey(key));
     }
 
     public RubyBoolean has_value(IRubyObject value) {
         return getRuntime().newBoolean(valueMap.containsValue(value));
     }
 
 	public RubyHash each(Block block) {
 		return eachInternal(false, block);
 	}
 
 	public RubyHash each_pair(Block block) {
 		return eachInternal(true, block);
 	}
 
     protected RubyHash eachInternal(boolean aValue, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (Iterator iter = entryIterator(); iter.hasNext();) {
             checkRehashing();
             Map.Entry entry = (Map.Entry) iter.next();
             block.yield(context, getRuntime().newArray((IRubyObject)entry.getKey(), (IRubyObject)entry.getValue()), null, null, aValue);
         }
         return this;
     }
 
 	
 
     private void checkRehashing() {
         if (isRehashing) {
             throw getRuntime().newIndexError("rehash occured during iteration");
         }
     }
 
     public RubyHash each_value(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = valueIterator(); iter.hasNext();) {
             checkRehashing();
 			IRubyObject value = (IRubyObject) iter.next();
 			block.yield(context, value);
 		}
 		return this;
 	}
 
 	public RubyHash each_key(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = keyIterator(); iter.hasNext();) {
 			checkRehashing();
             IRubyObject key = (IRubyObject) iter.next();
 			block.yield(context, key);
 		}
 		return this;
 	}
 
 	public RubyArray sort(Block block) {
 		return (RubyArray) to_a().sort_bang(block);
 	}
 
     public IRubyObject index(IRubyObject value) {
         for (Iterator iter = valueMap.keySet().iterator(); iter.hasNext(); ) {
             Object key = iter.next();
             if (value.equals(valueMap.get(key))) {
                 return (IRubyObject) key;
             }
         }
         return getRuntime().getNil();
     }
 
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(aref(indices[i]));
         }
 
         return values;
     }
 
     public RubyArray keys() {
         return RubyArray.newArray(getRuntime(), valueMap.keySet());
     }
 
     public RubyArray rb_values() {
         return RubyArray.newArray(getRuntime(), valueMap.values());
     }
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other) {
             return getRuntime().getTrue();
         } else if (!(other instanceof RubyHash)) {
             return getRuntime().getFalse();
         } else if (length() != ((RubyHash)other).length()) {
             return getRuntime().getFalse();
         }
 
         for (Iterator iter = modifiableEntryIterator(); iter.hasNext();) {
             checkRehashing();
             Map.Entry entry = (Map.Entry) iter.next();
 
             Object value = ((RubyHash)other).valueMap.get(entry.getKey());
             if (value == null || !entry.getValue().equals(value)) {
                 return getRuntime().getFalse();
             }
         }
         return getRuntime().getTrue();
     }
 
     public RubyArray shift() {
 		modify();
         Iterator iter = modifiableEntryIterator();
         Map.Entry entry = (Map.Entry)iter.next();
         iter.remove();
         return RubyArray.newArray(getRuntime(), (IRubyObject)entry.getKey(), (IRubyObject)entry.getValue());
     }
 
 	public IRubyObject delete(IRubyObject key, Block block) {
 		modify();
 		IRubyObject result = (IRubyObject) valueMap.remove(key);
         
 		if (result != null) return result;
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
 
 		return getDefaultValue(new IRubyObject[] {key}, null);
 	}
 
 	public RubyHash delete_if(Block block) {
 		reject_bang(block);
 		return this;
 	}
 
 	public RubyHash reject(Block block) {
 		RubyHash result = (RubyHash) dup();
 		result.reject_bang(block);
 		return result;
 	}
 
 	public IRubyObject reject_bang(Block block) {
 		modify();
 		boolean isModified = false;
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = keyIterator(); iter.hasNext();) {
 			IRubyObject key = (IRubyObject) iter.next();
 			IRubyObject value = (IRubyObject) valueMap.get(key);
 			IRubyObject shouldDelete = block.yield(context, getRuntime().newArray(key, value), null, null, true);
 			if (shouldDelete.isTrue()) {
 				valueMap.remove(key);
 				isModified = true;
 			}
 		}
 
 		return isModified ? this : getRuntime().getNil(); 
 	}
 
 	public RubyHash rb_clear() {
 		modify();
 		valueMap.clear();
 		return this;
 	}
 
 	public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 		
 		for (Iterator iter = modifiableEntryIterator(); iter.hasNext();) {
 			Map.Entry entry = (Map.Entry) iter.next();
 			result.aset((IRubyObject) entry.getValue(), 
 					(IRubyObject) entry.getKey());
 		}
 		return result;
 	}
 
     public RubyHash update(IRubyObject freshElements, Block block) {
         modify();
         RubyHash freshElementsHash =
-            (RubyHash) freshElements.convertType(RubyHash.class, "Hash", "to_hash");
+            (RubyHash) freshElements.convertToTypeWithCheck("Hash", "to_hash");
         ThreadContext ctx = getRuntime().getCurrentContext();
         if (block.isGiven()) {
             Map other = freshElementsHash.valueMap;
             for(Iterator iter = other.keySet().iterator();iter.hasNext();) {
                 IRubyObject key = (IRubyObject)iter.next();
                 IRubyObject oval = (IRubyObject)valueMap.get(key);
                 if(null == oval) {
                     valueMap.put(key,other.get(key));
                 } else {
                     valueMap.put(key,block.yield(ctx, getRuntime().newArrayNoCopy(new IRubyObject[]{key,oval,(IRubyObject)other.get(key)})));
                 }
             }
         } else {
             valueMap.putAll(freshElementsHash.valueMap);
         }
         return this;
     }
     
     public RubyHash merge(IRubyObject freshElements, Block block) {
         return ((RubyHash) dup()).update(freshElements, block);
     }
 
     public RubyHash replace(IRubyObject replacement) {
         modify();
         RubyHash replacementHash =
-            (RubyHash) replacement.convertType(RubyHash.class, "Hash", "to_hash");
+            (RubyHash) replacement.convertToTypeWithCheck("Hash", "to_hash");
         valueMap.clear();
         valueMap.putAll(replacementHash.valueMap);
         defaultValueCallback = replacementHash.defaultValueCallback;
         return this;
     }
 
     public RubyArray values_at(IRubyObject[] argv) {
         RubyArray result = RubyArray.newArray(getRuntime());
         for (int i = 0; i < argv.length; i++) {
             result.append(aref(argv[i]));
         }
         return result;
     }
     
     public boolean hasNonProcDefault() {
         return defaultValueCallback != NIL_DEFAULT_VALUE;
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(RubyHash hash, MarshalStream output) throws IOException {
         output.writeInt(hash.getValueMap().size());
 
         for (Iterator iter = hash.entryIterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
 
                 output.dumpObject((IRubyObject) entry.getKey());
                 output.dumpObject((IRubyObject) entry.getValue());
         }
 		
         // handle default value
         if (hash.hasNonProcDefault()) {
             output.dumpObject(hash.defaultValueCallback.execute(null, NULL_ARRAY, null));
         }
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             IRubyObject key = input.unmarshalObject();
             IRubyObject value = input.unmarshalObject();
             result.aset(key, value);
         }
         if (defaultValue) {
             result.setDefaultValue(input.unmarshalObject());
         }
         return result;
     }
 
     public Class getJavaClass() {
         return Map.class;
     }
 	
     // Satisfy java.util.Set interface (for Java integration)
 
 	public boolean isEmpty() {
 		return valueMap.isEmpty();
 	}
 
 	public boolean containsKey(Object key) {
 		return keySet().contains(key);
 	}
 
 	public boolean containsValue(Object value) {
 		IRubyObject element = JavaUtil.convertJavaToRuby(getRuntime(), value);
 		
 		for (Iterator iter = valueMap.values().iterator(); iter.hasNext(); ) {
 			if (iter.next().equals(element)) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public Object get(Object key) {
 		return JavaUtil.convertRubyToJava((IRubyObject) valueMap.get(JavaUtil.convertJavaToRuby(getRuntime(), key)));
 	}
 
 	public Object put(Object key, Object value) {
 		return valueMap.put(JavaUtil.convertJavaToRuby(getRuntime(), key),
 				JavaUtil.convertJavaToRuby(getRuntime(), value));
 	}
 
 	public Object remove(Object key) {
 		return valueMap.remove(JavaUtil.convertJavaToRuby(getRuntime(), key));
 	}
 
 	public void putAll(Map map) {
 		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
 			Object key = iter.next();
 			
 			put(key, map.get(key));
 		}
 	}
 
 
 	public Set entrySet() {
 		return new ConversionMapEntrySet(getRuntime(), valueMap.entrySet());
 	}
 
 	public int size() {
 		return valueMap.size();
 	}
 
 	public void clear() {
 		valueMap.clear();
 	}
 
 	public Collection values() {
 		return new AbstractCollection() {
 			public Iterator iterator() {
 				return new IteratorAdapter(entrySet().iterator()) {
 					public Object next() {
 						return ((Map.Entry) super.next()).getValue();
 					}
 				};
 			}
 
 			public int size() {
 				return RubyHash.this.size();
 			}
 
 			public boolean contains(Object v) {
 				return RubyHash.this.containsValue(v);
 			}
 		};
 	}
 	
 	public Set keySet() {
 		return new AbstractSet() {
 			public Iterator iterator() {
 				return new IteratorAdapter(entrySet().iterator()) {
 					public Object next() {
 						return ((Map.Entry) super.next()).getKey();
 					}
 				};
 			}
 
 			public int size() {
 				return RubyHash.this.size();
 			}
 		};
 	}	
 
 	/**
 	 * Convenience adaptor for delegating to an Iterator.
 	 *
 	 */
 	private static class IteratorAdapter implements Iterator {
 		private Iterator iterator;
 		
 		public IteratorAdapter(Iterator iterator) {
 			this.iterator = iterator;
 		}
 		public boolean hasNext() {
 			return iterator.hasNext();
 		}
 		public Object next() {
 			return iterator.next();
 		}
 		public void remove() {
 			iterator.remove();
 		}		
 	}
 	
 	
     /**
      * Wraps a Set of Map.Entry (See #entrySet) such that JRuby types are mapped to Java types and vice verce.
      *
      */
     private static class ConversionMapEntrySet extends AbstractSet {
 		protected Set mapEntrySet;
 		protected Ruby runtime;
 
 		public ConversionMapEntrySet(Ruby runtime, Set mapEntrySet) {
 			this.mapEntrySet = mapEntrySet;
 			this.runtime = runtime;
 		}
         public Iterator iterator() {
             return new ConversionMapEntryIterator(runtime, mapEntrySet.iterator());
         }
         public boolean contains(Object o) {
             if (!(o instanceof Map.Entry)) {
                 return false;
             }
             return mapEntrySet.contains(getRubifiedMapEntry((Map.Entry) o));
         }
         
         public boolean remove(Object o) {
             if (!(o instanceof Map.Entry)) {
                 return false;
             }
             return mapEntrySet.remove(getRubifiedMapEntry((Map.Entry) o));
         }
 		public int size() {
 			return mapEntrySet.size();
 		}
         public void clear() {
         	mapEntrySet.clear();
         }
 		private Entry getRubifiedMapEntry(final Map.Entry mapEntry) {
 			return new Map.Entry(){
 				public Object getKey() {
 					return JavaUtil.convertJavaToRuby(runtime, mapEntry.getKey());
 				}
 				public Object getValue() {
 					return JavaUtil.convertJavaToRuby(runtime, mapEntry.getValue());
 				}
 				public Object setValue(Object arg0) {
 					// This should never get called in this context, but if it did...
 					throw new UnsupportedOperationException("unexpected call in this context");
 				}
             };
 		}
     }    
     
     /**
      * Wraps a RubyHash#entrySet#iterator such that the Map.Entry returned by next() will have its key and value 
      * mapped from JRuby types to Java types where applicable.
      */
     private static class ConversionMapEntryIterator implements Iterator {
         private Iterator iterator;
 		private Ruby runtime;
 
         public ConversionMapEntryIterator(Ruby runtime, Iterator iterator) {
             this.iterator = iterator;
             this.runtime = runtime;            
         }
 
         public boolean hasNext() {
             return iterator.hasNext();
         }
 
         public Object next() {
             return new ConversionMapEntry(runtime, ((Map.Entry) iterator.next())); 
         }
 
         public void remove() {
             iterator.remove();
         }
     }
     
    
     /**
      * Wraps a Map.Entry from RubyHash#entrySet#iterator#next such that the the key and value 
      * are mapped from/to JRuby/Java types where applicable.
      */
     private static class ConversionMapEntry implements Map.Entry {
         private Entry entry;
 		private Ruby runtime;
 
         public ConversionMapEntry(Ruby runtime, Map.Entry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
         
         public Object getKey() {
             IRubyObject rubyObject = (IRubyObject) entry.getKey();
             return JavaUtil.convertRubyToJava(rubyObject, Object.class); 
         }
         
         public Object getValue() {
             IRubyObject rubyObject = (IRubyObject) entry.getValue();
             return JavaUtil.convertRubyToJava(rubyObject, Object.class); 
         }
         
         public Object setValue(Object value) {
             return entry.setValue(JavaUtil.convertJavaToRuby(runtime, value));            
         }
     }
     
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index f1fe0acb8c..46e31e5231 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,968 +1,968 @@
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
 import java.io.File;
 import java.io.IOException;
 import java.util.Calendar;
 import java.util.Iterator;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRUBY_OBJECT));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRUBY_OBJECT));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRUBY_OBJECT));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRUBY_OBJECT));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRUBY_OBJECT));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRUBY_OBJECT));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("callcc", callbackFactory.getOptSingletonMethod("callcc"));
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRUBY_OBJECT));
         module.defineFastModuleFunction("chomp", callbackFactory.getFastOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getFastOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getFastSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getFastSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getFastOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getFastOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getFastOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getFastSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getFastSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getFastOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getFastOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getFastOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("putc", callbackFactory.getFastSingletonMethod("putc", IRubyObject.class));
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRUBY_OBJECT));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRUBY_OBJECT));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRUBY_OBJECT));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRUBY_OBJECT));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         module.defineFastModuleFunction("test", callbackFactory.getFastOptSingletonMethod("test"));
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRUBY_OBJECT));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_removed", callbackFactory.getSingletonMethod("singleton_method_removed", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_undefined", callbackFactory.getSingletonMethod("singleton_method_undefined", IRUBY_OBJECT));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRUBY_OBJECT));
 
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id_deprecated"));
         module.defineFastPublicModuleFunction("object_id", objectCallbackFactory.getFastMethod("id"));
         module.defineAlias("__id__", "object_id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRUBY_OBJECT));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRUBY_OBJECT));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if (recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if(autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         final String baseName = symbol.asSymbol();
         String nm = baseName;
         if(recv instanceof RubyModule) {
             nm = ((RubyModule)recv).getName() + "::" + nm;
         }
         loadService.addAutoload(nm, new IAutoloadMethod() {
                 public String file() {
                     return file.toString();
                 }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = recv.anyToString().toString();
         } else {
             description = recv.inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         // FIXME: Modify sprintf to accept Object[] as well...
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), 
                         runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : recv.getType().getName())
                 })).toString();
         
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.checkArgumentCount(args,1,3);
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
 
         return ((FileMetaClass) runtime.getClass("File")).open(args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(recv.checkArgumentCount(args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
         
         if (value.isNil()) {
             DynamicMethod method = object.getMetaClass().searchMethod("to_a");
             
             if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
                 return recv.getRuntime().newArray(object);
             }
             
             // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
             value = object.callMethod(recv.getRuntime().getCurrentContext(), "to_a");
             if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
                 throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                
             }
         }
         
         return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(Double.isNaN(rFloat.getDoubleValue())){
                 recv.getRuntime().newArgumentError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
                     }
         return object.callMethod(context,"to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod(recv.getRuntime().getCurrentContext(), "to_s");
     }
     
     
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
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
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
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
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
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
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
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         recv.checkArgumentCount(args, 0, 3); 
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args, block));
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
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
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
 
-        src.checkSafeString();
+        recv.getRuntime().checkSafeString(src);
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = recv.getRuntime().newBinding();
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject callcc(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn("Kernel#callcc: Continuations are not implemented in JRuby and will not work");
         IRubyObject cc = runtime.getClass("Continuation").callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return block.yield(context, tag);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ThrowJump &&
                 je.getTarget().equals(tag.asSymbol())) {
                     return (IRubyObject) je.getValue();
             }
             throw je;
         } finally {
             context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setTarget(tag);
                 je.setValue(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         IRubyObject out = recv.getRuntime().getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType("IO", "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
         return recv.getRuntime().getNil();
     }
 
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
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
 
                 Thread.yield();
             } catch (JumpException je) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                     if (je.getTarget() != null && je.getTarget() != block) {
                         je.setBreakInKernelLoop(true);
                     }
                 }
                  
                 throw je;
             }
         }
     }
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         int cmd = (int) args[0].convertToInteger().getLongValue();
         Ruby runtime = recv.getRuntime();
         File pwd = new File(recv.getRuntime().getCurrentDirectory());
         File file1 = new File(pwd, args[1].toString());
         Calendar calendar;
         switch (cmd) {
         //        ?A  | Time    | Last access time for file1
         //        ?b  | boolean | True if file1 is a block device
         //        ?c  | boolean | True if file1 is a character device
         //        ?C  | Time    | Last change time for file1
         //        ?d  | boolean | True if file1 exists and is a directory
         //        ?e  | boolean | True if file1 exists
         //        ?f  | boolean | True if file1 exists and is a regular file
         case 'f':
             return RubyBoolean.newBoolean(runtime, file1.isFile());
         //        ?g  | boolean | True if file1 has the \CF{setgid} bit
         //            |         | set (false under NT)
         //        ?G  | boolean | True if file1 exists and has a group
         //            |         | ownership equal to the caller's group
         //        ?k  | boolean | True if file1 exists and has the sticky bit set
         //        ?l  | boolean | True if file1 exists and is a symbolic link
         //        ?M  | Time    | Last modification time for file1
         case 'M':
             calendar = Calendar.getInstance();
             calendar.setTimeInMillis(file1.lastModified());
             return RubyTime.newTime(runtime, calendar);
         //        ?o  | boolean | True if file1 exists and is owned by
         //            |         | the caller's effective uid
         //        ?O  | boolean | True if file1 exists and is owned by
         //            |         | the caller's real uid
         //        ?p  | boolean | True if file1 exists and is a fifo
         //        ?r  | boolean | True if file1 is readable by the effective
         //            |         | uid/gid of the caller
         //        ?R  | boolean | True if file is readable by the real
         //            |         | uid/gid of the caller
         //        ?s  | int/nil | If file1 has nonzero size, return the size,
         //            |         | otherwise return nil
         //        ?S  | boolean | True if file1 exists and is a socket
         //        ?u  | boolean | True if file1 has the setuid bit set
         //        ?w  | boolean | True if file1 exists and is writable by
         //            |         | the effective uid/gid
         //        ?W  | boolean | True if file1 exists and is writable by
         //            |         | the real uid/gid
         //        ?x  | boolean | True if file1 exists and is executable by
         //            |         | the effective uid/gid
         //        ?X  | boolean | True if file1 exists and is executable by
         //            |         | the real uid/gid
         //        ?z  | boolean | True if file1 exists and has a zero length
         //
         //        Tests that take two files:
         //
         //        ?-  | boolean | True if file1 and file2 are identical
         //        ?=  | boolean | True if the modification times of file1
         //            |         | and file2 are equal
         //        ?<  | boolean | True if the modification time of file1
         //            |         | is prior to that of file2
         //        ?>  | boolean | True if the modification time of file1
         //            |         | is after that of file2
         }
         throw RaiseException.createNativeRaiseException(runtime, 
             new UnsupportedOperationException("test flag " + ((char) cmd) + " is not implemented"));
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
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
         } else {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             double result = recv.getRuntime().getRandom().nextDouble();
             return RubyFloat.newFloat(recv.getRuntime(), result);
         }
         if(ceil > Integer.MAX_VALUE) {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextLong()%ceil);
         } else {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextInt((int)ceil));
         }
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode = new ShellLauncher(runtime).runAndWait(args);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 53448c0685..0a9835850f 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -540,1156 +540,1156 @@ public class RubyModule extends RubyObject {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
 
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
 
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
                         getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
 
                     return constant;
                 }
                 p = p.getSuperClass();
             }
 
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
 
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
         return getInstanceVariable(name);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         putMethod(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getConstantAt(name);
         ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         } 
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (superClazz != null && ((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newTypeError("superclass mismatch for class " + name);
         }
 
         return (RubyClass) type;
     }
 
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz, ObjectAllocator allocator) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         }
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newNameError(name + " is already defined.", name);
         }
 
         return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineModuleUnder(name, cref);
         }
 
         if (!(type instanceof RubyModule)) {
             throw getRuntime().newTypeError(name + " is not a module.");
         }
 
         return (RubyModule) type;
     }
 
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClass("Class")) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
         }
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject value = removeInstanceVariable(name.asSymbol());
 
         if (value != null) {
             return value;
         }
 
         if (isClassVarDefined(name.asSymbol())) {
             throw cannotRemoveError(name.asSymbol());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
         final Ruby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     checkArgumentCount(args, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     IRubyObject[] fargs = runtime.getCurrentContext().getFrameArgs();
 
                     if (fargs.length != 1) {
                         throw runtime.newArgumentError("wrong # of arguments(" + fargs.length + "for 1)");
                     }
 
                     return self.setInstanceVariable(variableName, fargs[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asSymbol(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 addMethod(name, new FullFunctionCallbackMethod(this, new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         ThreadContext tc = self.getRuntime().getCurrentContext();
                         return self.callSuper(tc, tc.getFrameArgs(), block);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 }, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(method.getImplementationClass(), name, this, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(method.getImplementationClass(), name, this, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     // What is argument 1 for in this method? A Method or Proc object /OB
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) visibility = Visibility.PRIVATE;
 
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
             body = proc;
 
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setKlazz(this);
             proc.getBlock().getFrame().setName(name);
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
         callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         context.preExecuteUnder(this, block);
 
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(Ruby runtime, String name) {
         return newModule(runtime, runtime.getClass("Module"), name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name) {
         return newModule(runtime, type, name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, String name, SinglyLinkedList parentCRef) {
         return newModule(runtime, runtime.getClass("Module"), name, parentCRef);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name, SinglyLinkedList parentCRef) {
         RubyModule module = new RubyModule(runtime, type, null, parentCRef, name);
         
         return module;
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (Iterator iter = p.instanceVariableNames(); iter.hasNext();) {
                 String id = (String) iter.next();
                 if (IdUtil.isClassVariable(id)) {
                     RubyString kval = getRuntime().newString(id);
                     if (!ary.includes(kval)) {
                         ary.append(kval);
                     }
                 }
             }
         }
         return ary;
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     protected IRubyObject doClone() {
         return RubyModule.newModule(getRuntime(), null, cref.getNext());
     }
 
     /** rb_mod_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         assert original instanceof RubyModule;
         
         RubyModule originalModule = (RubyModule)original;
         
         super.initialize_copy(originalModule);
         
         if (!getMetaClass().isSingleton()) {
             setMetaClass(originalModule.getSingletonClassClone());
         }
 
         setSuperClass(originalModule.getSuperClass());
         
         if (originalModule.instanceVariables != null){
             setInstanceVariables(new HashMap(originalModule.getInstanceVariables()));
         }
         
         // no __classpath__ and __classid__ stuff in JRuby here (yet?)        
 
         originalModule.cloneMethods(this);
         
         return this;        
     }
 
     /** rb_mod_included_modules
      *
      */
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List getAncestorList() {
         ArrayList list = new ArrayList();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = getInstanceVariable("__attached__");
             StringBuffer buffer = new StringBuffer("#<Class:");
             if(attached instanceof RubyClass || attached instanceof RubyModule){
                 buffer.append(attached.inspect());
             }else{
                 buffer.append(attached.anyToString());
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(obj.isKindOf(this));
     }
 
     /** rb_mod_le
     *
     */
    public IRubyObject op_le(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
 
        return getRuntime().getNil();
    }
 
    /** rb_mod_lt
     *
     */
    public IRubyObject op_lt(IRubyObject obj) {
     return obj == this ? getRuntime().getFalse() : op_le(obj);
    }
 
    /** rb_mod_ge
     *
     */
    public IRubyObject op_ge(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        return ((RubyModule) obj).op_le(this);
    }
 
    /** rb_mod_gt
     *
     */
    public IRubyObject op_gt(IRubyObject obj) {
        return this == obj ? getRuntime().getFalse() : op_ge(obj);
    }
 
    /** rb_mod_cmp
     *
     */
    public IRubyObject op_cmp(IRubyObject obj) {
        if (this == obj) {
            return getRuntime().newFixnum(0);
        }
 
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError(
                "<=> requires Class or Module (" + getMetaClass().getName() + " given)");
        }
 
        RubyModule module = (RubyModule)obj;
 
        if (module.isKindOfModule(this)) {
            return getRuntime().newFixnum(1);
        } else if (this.isKindOfModule(module)) {
            return getRuntime().newFixnum(-1);
        }
 
        return getRuntime().getNil();
    }
 
    public boolean isKindOfModule(RubyModule type) {
        for (RubyModule p = this; p != null; p = p.getSuperClass()) {
            if (p.isSame(type)) {
                return true;
            }
        }
 
        return false;
    }
 
    public boolean isSame(RubyModule module) {
        return this == module;
    }
 
     /** rb_mod_initialize
      *
      */
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (block.isGiven()) block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(args[0].asSymbol(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_reader
      *
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     public IRubyObject attr_writer(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_accessor
      *
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_const_get
      *
      */
     public IRubyObject const_get(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getConstant(name);
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return setConstant(name, value);
     }
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getRuntime().newBoolean(getConstantAt(name) != null);
     }
 
     private RaiseException wrongConstantNameError(String name) {
         return getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap undefinedMethods = new HashMap();
         Set added = new HashSet();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (method.isUndefined()) {
                     undefinedMethods.put(methodName, Boolean.TRUE);
                     continue;
                 }
                 if (method.getImplementationClass() == realType &&
                     method.getVisibility().is(visibility) && undefinedMethods.get(methodName) == null) {
 
                     if (!added.contains(methodName)) {
                         ary.append(getRuntime().newString(methodName));
                         added.add(methodName);
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC_PROTECTED);
     }
 
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC);
     }
 
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asSymbol(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PROTECTED);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE);
     }
 
     /** rb_mod_constants
      *
      */
     public RubyArray constants() {
         ArrayList constantNames = new ArrayList();
         RubyModule objectClass = getRuntime().getObject();
 
         if (getRuntime().getClass("Module") == this) {
             for (Iterator vars = objectClass.instanceVariableNames();
                  vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         } else if (getRuntime().getObject() == this) {
             for (Iterator vars = instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         }
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (objectClass == p) {
                 continue;
             }
 
             for (Iterator vars = p.instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
         }
 
         return getRuntime().newArray(constantNames);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject remove_class_variable(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isClassVariable(id)) {
             throw getRuntime().newNameError("wrong class variable name " + id, id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = removeInstanceVariable(id);
         if (variable != null) {
             return variable;
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("class variable " + id + " not defined for " + getName(), id);
     }
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
     public IRubyObject remove_const(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isConstant(id)) {
             throw wrongConstantNameError(id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = getInstanceVariable(id);
         if (variable != null) {
             return removeInstanceVariable(id);
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
     }
 
     /** rb_mod_append_features
      *
      */
     // TODO: Proper argument check (conversion?)
     public RubyModule append_features(IRubyObject module) {
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     public IRubyObject extend_object(IRubyObject obj) {
-        obj.extendObject(this);
+        obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     public IRubyObject extended(IRubyObject other, Block block) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             getRuntime().getCurrentContext().setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     public RubyModule rbPublic(IRubyObject[] args) {
         setVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     public RubyModule rbProtected(IRubyObject[] args) {
         setVisibility(args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     public RubyModule rbPrivate(IRubyObject[] args) {
         setVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     public RubyModule module_function(IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
 
     public IRubyObject method_added(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_removed(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_undefined(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
     
     public RubyBoolean method_defined(IRubyObject symbol) {
         return isMethodBound(symbol.asSymbol(), true) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     public RubyModule alias_method(IRubyObject newId, IRubyObject oldId) {
         defineAlias(newId.asSymbol(), oldId.asSymbol());
         return this;
     }
 
     public RubyModule undef_method(IRubyObject name) {
         undef(name.asSymbol());
         return this;
     }
 
     public IRubyObject module_eval(IRubyObject[] args, Block block) {
         return specificEval(this, args, block);
     }
 
     public RubyModule remove_method(IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(args[i].asSymbol());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.writeString(module.name().toString());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         Ruby runtime = input.getRuntime();
         RubyModule result = runtime.getClassFromPath(name);
         if (result == null) {
             throw runtime.newNameError("uninitialized constant " + name, name);
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 6c38e4f73c..b1a79e89bf 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1437 +1,1407 @@
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
 
 import org.jruby.ast.Node;
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
 import java.util.Map;
 import org.jruby.runtime.ClassIndex;
-import org.jruby.runtime.Frame;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
-	
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
 
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
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
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
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** init_copy
      * 
      */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
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
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),switchValue,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),switchValue,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, byte switchValue, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),switchValue,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, byte methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
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
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, switchvalue, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
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
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, block);
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
 
-    /** rb_eval
-     *
-     */
-    public IRubyObject eval(Node n) {
-        //return new EvaluationState(getRuntime(), this).begin(n);
-        // need to continue evaluation with a new self, so save the old one (should be a stack?)
-        return EvaluationState.eval(getRuntime(), getRuntime().getCurrentContext(), n, this, Block.NULL_BLOCK);
-    }
-
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
-    public void extendObject(RubyModule module) {
-        getSingletonClass().includeModule(module);
-    }
-
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
+    public static String trueFalseNil(IRubyObject v) {
+        return trueFalseNil(v.getMetaClass().getName());
+    }
+
+    public static String trueFalseNil(String v) {
+        if("TrueClass".equals(v)) {
+            return "true";
+        } else if("FalseClass".equals(v)) {
+            return "false";
+        } else if("NilClass".equals(v)) {
+            return "nil";
+        }
+        return v;
+    }
+
+    public RubyArray convertToArray() {
+        return (RubyArray) convertToType("Array", "to_ary", true);
+    }
+
+    public RubyFloat convertToFloat() {
+        return (RubyFloat) convertToType("Float", "to_f", true);
+    }
+
+    public RubyInteger convertToInteger() {
+        return (RubyInteger) convertToType("Integer", "to_int", true);
+    }
+
+    public RubyString convertToString() {
+        return (RubyString) convertToType("String", "to_str", true);
+    }
+
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
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
-    public static String trueFalseNil(IRubyObject v) {
-        return trueFalseNil(v.getMetaClass().getName());
-    }
-
-    public static String trueFalseNil(String v) {
-        if("TrueClass".equals(v)) {
-            return "true";
-        } else if("FalseClass".equals(v)) {
-            return "false";
-        } else if("NilClass".equals(v)) {
-            return "nil";
-        }
-        return v;
-    }
-
-    public RubyArray convertToArray() {
-        return (RubyArray) convertToType("Array", "to_ary", true);
-    }
-
-    public RubyFloat convertToFloat() {
-        return (RubyFloat) convertToType("Float", "to_f", true);
-    }
-
-    public RubyInteger convertToInteger() {
-        return (RubyInteger) convertToType("Integer", "to_int", true);
-    }
-
-    public RubyString convertToString() {
-        return (RubyString) convertToType("String", "to_str", true);
-    }
-
     /** rb_obj_as_string
      */
-    public RubyString objAsString() {
+    public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), "to_s");
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
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
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck("String","to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck("Array","to_ary");
     }
 
-    public void checkSafeString() {
-        if (getRuntime().getSafeLevel() > 0 && isTaint()) {
-            ThreadContext tc = getRuntime().getCurrentContext();
-            if (tc.getFrameName() != null) {
-                throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameName());
-            }
-            throw getRuntime().newSecurityError("Insecure operation: -r");
-        }
-        getRuntime().secure(4);
-        if (!(this instanceof RubyString)) {
-            throw getRuntime().newTypeError(
-                "wrong argument type " + getMetaClass().getName() + " (expected String)");
-        }
-    }
-
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
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
 
             result = EvaluationState.eval(getRuntime(), threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding();
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
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
             return EvaluationState.eval(getRuntime(), context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
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
         checkArgumentCount(args, 1, 2);
 
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
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         
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
         clone.initCopy(this);
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
         
         dup.initCopy(this);
 
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
         return callMethod(getRuntime().getCurrentContext(), "to_s");
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
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(checkArgumentCount(args,0,1) == 1) {
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
         checkArgumentCount(args, 1, -1);
 
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
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
     public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = checkArgumentCount(args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 
     private transient Object dataStruct;
 
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
         if(this == other || callMethod(getRuntime().getCurrentContext(), "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, "==", other);
     }
 }
diff --git a/src/org/jruby/RubyRegexp.java b/src/org/jruby/RubyRegexp.java
index bd3c51bd9c..68871dcc3d 100644
--- a/src/org/jruby/RubyRegexp.java
+++ b/src/org/jruby/RubyRegexp.java
@@ -1,760 +1,760 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import jregex.Matcher;
 import jregex.Pattern;
 import jregex.REFlags;
 import org.jruby.parser.ReOptions;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  amoore
  */
 public class RubyRegexp extends RubyObject implements ReOptions {
     private static final RegexpTranslator REGEXP_TRANSLATOR = new RegexpTranslator();
 
     // \013 is a vertical tab. Java does not support the \v notation used by
     // Ruby.
     private static final Pattern SPECIAL_CHARS = new Pattern("([\\\t\\\n\\\f\\\r\\ \\#\\\013\\+\\-\\[\\]\\.\\?\\*\\(\\)\\{\\}\\|\\\\\\^\\$])");    
 
 	/** Class which represents the multibyte character set code.
 	 * (should be an enum in Java 5.0).
 	 * 
 	 * Warning: THIS IS NOT REALLY SUPPORTED BY JRUBY. 
 	 */
 
     private Pattern pattern;
     private KCode code;
     private int flags;
 
     KCode getCode() {
         return code;
     }
 
 	// lastTarget and matcher currently only used by searchAgain
 	private String lastTarget = null;
 	private Matcher matcher = null;
 
     public RubyRegexp(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     private RubyRegexp(Ruby runtime) {
         super(runtime, runtime.getClass("Regexp"));
     }
     
     private static ObjectAllocator REGEXP_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyRegexp instance = new RubyRegexp(runtime, klass);
             
             return instance;
         }
     };
 
     public static RubyClass createRegexpClass(Ruby runtime) {
         RubyClass regexpClass = runtime.defineClass("Regexp", runtime.getObject(), REGEXP_ALLOCATOR);
         regexpClass.index = ClassIndex.REGEXP;
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyRegexp.class);
         
         regexpClass.defineConstant("IGNORECASE", runtime.newFixnum(RE_OPTION_IGNORECASE));
         regexpClass.defineConstant("EXTENDED", runtime.newFixnum(RE_OPTION_EXTENDED));
         regexpClass.defineConstant("MULTILINE", runtime.newFixnum(RE_OPTION_MULTILINE));
 
         regexpClass.defineFastMethod("initialize", callbackFactory.getFastOptMethod("initialize"));
         regexpClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy",RubyKernel.IRUBY_OBJECT));        
         regexpClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("eql?", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("===", callbackFactory.getFastMethod("eqq", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("=~", callbackFactory.getFastMethod("match", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("~", callbackFactory.getFastMethod("match2"));
         regexpClass.defineFastMethod("match", callbackFactory.getFastMethod("match_m", RubyKernel.IRUBY_OBJECT));
         regexpClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         regexpClass.defineFastMethod("source", callbackFactory.getFastMethod("source"));
         regexpClass.defineFastMethod("casefold?", callbackFactory.getFastMethod("casefold"));
         regexpClass.defineFastMethod("kcode", callbackFactory.getFastMethod("kcode"));
         regexpClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         regexpClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         regexpClass.getMetaClass().defineFastMethod("new", callbackFactory.getFastOptSingletonMethod("newInstance"));
         regexpClass.getMetaClass().defineFastMethod("compile", callbackFactory.getFastOptSingletonMethod("newInstance"));
         regexpClass.getMetaClass().defineFastMethod("quote", callbackFactory.getFastOptSingletonMethod("quote"));
         regexpClass.getMetaClass().defineFastMethod("escape", callbackFactory.getFastSingletonMethod("quote", RubyString.class));
         regexpClass.getMetaClass().defineFastMethod("last_match", callbackFactory.getFastSingletonMethod("last_match_s"));
         regexpClass.getMetaClass().defineFastMethod("union", callbackFactory.getFastOptSingletonMethod("union"));
 
         return regexpClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.REGEXP;
     }
 
     public void initialize(String regex, int options) {
         try {
             if(getCode() == KCode.UTF8) {
                 try {
                     regex = new String(ByteList.plain(regex),"UTF8");
                 } catch(Exception e) {
                 }
             }
             pattern = REGEXP_TRANSLATOR.translate(regex, options, code.flags());
             flags = REGEXP_TRANSLATOR.flagsFor(options, code.flags());
         } catch(jregex.PatternSyntaxException e) {
             //            System.err.println(regex);
             //            e.printStackTrace();
             throw getRuntime().newRegexpError(e.getMessage());
         }
     }
 
     public static String escapeSpecialChars(String original) {
     	return SPECIAL_CHARS.replacer("\\\\$1").replace(original);
     }
 
     private void recompileIfNeeded() {
         checkInitialized();
     }
 
     private void checkInitialized() {
         if (pattern == null) {
             throw getRuntime().newTypeError("uninitialized Regexp");
         }
     }
     
     public static RubyRegexp regexpValue(IRubyObject obj) {
         if (obj instanceof RubyRegexp) {
             return (RubyRegexp) obj;
         } else if (obj instanceof RubyString) {
             return newRegexp(obj.getRuntime().newString(escapeSpecialChars(((RubyString) obj).toString())), 0, null);
         } else {
             throw obj.getRuntime().newArgumentError("can't convert arg to Regexp");
         }
     }
 
     // Methods of the Regexp class (rb_reg_*):
 
     public static RubyRegexp newRegexp(RubyString str, int options, String lang) {
         return newRegexp(str.getRuntime(), str.toString(), options, lang);
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, Pattern pattern, int flags, String lang) {
         RubyRegexp re = new RubyRegexp(runtime);
         re.code = KCode.create(runtime, lang);
         re.pattern = pattern;
         re.flags = flags;
         return re;
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, String str, int options, String kcode) {
         RubyRegexp re = new RubyRegexp(runtime);
         re.code = KCode.create(runtime, kcode);
         re.initialize(str, options);
         return re;
     }
     
     public static RubyRegexp newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         
         RubyRegexp re = (RubyRegexp) klass.allocate();
         
         re.callInit(args, Block.NULL_BLOCK);
         
         return re;
     }
 
     public IRubyObject initialize(IRubyObject[] args) {
         String pat =
             (args[0] instanceof RubyRegexp)
                 ? ((RubyRegexp) args[0]).source().toString()
                 : RubyString.stringValue(args[0]).toString();
         int opts = 0;
         if (args.length > 1) {
             if (args[1] instanceof RubyFixnum) {
                 opts = (int) ((RubyFixnum) args[1]).getLongValue();
             } else if (args[1].isTrue()) {
                 opts |= RE_OPTION_IGNORECASE;
             }
         }
         if (args.length > 2) {
         	code = KCode.create(getRuntime(), RubyString.stringValue (args[2]).toString());
         } else {
         	code = KCode.create(getRuntime(), null);
         }
         initialize(pat, opts);
         return getRuntime().getNil();
     }
 
     /** rb_reg_s_quote
      * 
      */
     public static RubyString quote(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0 || args.length > 2) {
             throw recv.getRuntime().newArgumentError(0, args.length);
         }
 
         KCode kcode = recv.getRuntime().getKCode();
         
         if (args.length > 1) {
             kcode = KCode.create(recv.getRuntime(), args[1].toString());
         }
         
         RubyString str = args[0].convertToString();
 
         if (kcode == KCode.NONE) {
             return quote(recv, str);
         }
         
         try {
             // decode with the specified encoding, escape as appropriate, and reencode
             // FIXME: This could probably be more efficent.
             CharBuffer decoded = kcode.decoder().decode(ByteBuffer.wrap(str.getBytes()));
             String escaped = escapeSpecialChars(decoded.toString());
             ByteBuffer encoded = kcode.encoder().encode(CharBuffer.wrap(escaped));
             
             return (RubyString)RubyString.newString(recv.getRuntime(), encoded.array()).infectBy(str);
         } catch (CharacterCodingException ex) {
             throw new RuntimeException(ex);
         }
     }
 
     /**
      * Utility version of quote that doesn't use encoding
      */
     public static RubyString quote(IRubyObject recv, RubyString str) {        
         return (RubyString) recv.getRuntime().newString(escapeSpecialChars(str.toString())).infectBy(str);
     }
 
     /** 
      * 
      */
     public static IRubyObject last_match_s(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getBackref();
     }
 
     /** rb_reg_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if (other == this) {
             return getRuntime().getTrue();
         }
         if (!(other instanceof RubyRegexp)) {
             return getRuntime().getFalse();
         }
         RubyRegexp re = (RubyRegexp) other;
         checkInitialized();
         if (!(re.pattern.toString().equals(pattern.toString()) && re.flags == this.flags)) {
             return getRuntime().getFalse();
         }
         
         if (code != re.code) {
         	return getRuntime().getFalse();
         }
         
         return getRuntime().getTrue();
     }
 
     /** rb_reg_match2
      * 
      */
     public IRubyObject match2() {
         IRubyObject target = getRuntime().getCurrentContext().getLastline();
         
         return target instanceof RubyString ? match(target) : getRuntime().getNil();
     }
     
     /** rb_reg_eqq
      * 
      */
     public IRubyObject eqq(IRubyObject target) {
         if(!(target instanceof RubyString)) {
             target = target.checkStringType();
             if(target.isNil()) {
                 getRuntime().getCurrentContext().setBackref(getRuntime().getNil());
                 return getRuntime().getFalse();
             }
         }
     	String string = RubyString.stringValue(target).toString();
         if (string.length() == 0 && "^$".equals(pattern.toString())) {
     		string = "\n";
         }
     	
         int result = search(string, 0);
         
         return result < 0 ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_reg_match
      * 
      */
     public IRubyObject match(IRubyObject target) {
         if (target.isNil()) {
             return getRuntime().getFalse();
         }
         // FIXME:  I think all String expecting functions has this magic via RSTRING
     	if (target instanceof RubySymbol || target instanceof RubyHash || target instanceof RubyArray) {
     		return getRuntime().getFalse();
     	}
     	
     	String string = RubyString.stringValue(target).toString();
         if (string.length() == 0 && "^$".equals(pattern.toString())) {
     		string = "\n";
         }
     	
         int result = search(string, 0);
         
         return result < 0 ? getRuntime().getNil() :
         	getRuntime().newFixnum(result);
     }
 
     /** rb_reg_match_m
      * 
      */
     public IRubyObject match_m(IRubyObject target) {
         if (target.isNil()) {
             return target;
         }
         IRubyObject result = match(target);
         return result.isNil() ? result : getRuntime().getCurrentContext().getBackref().rbClone();
     }
 
     /** rb_reg_source
      * 
      */
     public RubyString source() {
         checkInitialized();
         return getRuntime().newString(pattern.toString());
     }
 
     public IRubyObject kcode() {
         if(code == KCode.NIL) {
             return code.kcode(getRuntime());
         } else {
             return getRuntime().newString(code.kcode(getRuntime()).toString().toLowerCase());
         }
     }
 
     /** rb_reg_casefold_p
      * 
      */
     public RubyBoolean casefold() {
         checkInitialized();
         return getRuntime().newBoolean((flags & REFlags.IGNORE_CASE) != 0);
     }
 
     /** rb_reg_nth_match
      *
      */
     public static IRubyObject nth_match(int n, IRubyObject match) {
         IRubyObject nil = match.getRuntime().getNil();
         if (match.isNil()) {
             return nil;
         }
         
         RubyMatchData rmd = (RubyMatchData) match;
         
         if (n > rmd.getSize()) {
             return nil;
         }
         
         if (n < 0) {
             n += rmd.getSize();
             if (n <= 0) {
                 return nil;
             }
         }
         return rmd.group(n);
     }
 
     /** rb_reg_last_match
      *
      */
     public static IRubyObject last_match(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).group(0);
     }
 
     /** rb_reg_match_pre
      *
      */
     public static IRubyObject match_pre(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).pre_match();
     }
 
     /** rb_reg_match_post
      *
      */
     public static IRubyObject match_post(IRubyObject match) {
         return match.isNil() ? match : ((RubyMatchData) match).post_match();
     }
 
     /** rb_reg_match_last
      *
      */
     public static IRubyObject match_last(IRubyObject match) {
         if (match.isNil()) {
             return match;
         }
         RubyMatchData md = (RubyMatchData) match;
         for (long i = md.getSize() - 1; i > 0; i--) {
             if (!md.group(i).isNil()) {
                 return md.group(i);
             }
         }
         return md.getRuntime().getNil();
     }
 
     /** rb_reg_search
      *
      */
     public int search(String target, int pos) {
         if (pos > target.length()) {
             return -1;
         }
         recompileIfNeeded();
 
         // If nothing match then nil will be returned
         IRubyObject result = match(target, pos);
         getRuntime().getCurrentContext().setBackref(result);
 
         // If nothing match then -1 will be returned
         return result instanceof RubyMatchData ? ((RubyMatchData) result).matchStartPosition() : -1;
     }
     
     public IRubyObject search2(String str) {
         IRubyObject result = match(str, 0);
         
         getRuntime().getCurrentContext().setBackref(result);
         
     	return result;
     }
 	
     public int searchAgain(String target) {
         if (matcher == null || !target.equals(lastTarget)) {
             matcher = pattern.matcher(target);
             lastTarget = target;
         }
         
         if (!matcher.find()) {
             return -1;
         }
         
         RubyMatchData match = new RubyMatchData(getRuntime(), target, matcher);
         
         getRuntime().getCurrentContext().setBackref(match);
         
         return match.matchStartPosition();
     }
     
     public IRubyObject match(String target, int startPos) {
         boolean utf8 = getCode() == KCode.UTF8;
         String t = target;
         if(utf8) {
             try {
                 t = new String(ByteList.plain(target),"UTF8");
             } catch(Exception e) {
             }
         }
 
     	Matcher aMatcher = pattern.matcher(t);
     	
         aMatcher.setPosition(startPos);
 
         if (aMatcher.find()) {
             return new RubyMatchData(getRuntime(), target, aMatcher);
         }
         return getRuntime().getNil();
     }
 
     public void regsub(RubyString str, RubyMatchData match, ByteList sb) {
         ByteList repl = str.getByteList();
         int pos = 0;
         int end = repl.length();
         char c;
         IRubyObject ins;
         while (pos < end) {
             c = (char)(repl.get(pos++) & 0xFF);
             if (c == '\\' && pos < end) {
                 c = (char)(repl.get(pos++) & 0xFF);
                 switch (c) {
                     case '0' :
                     case '1' :
                     case '2' :
                     case '3' :
                     case '4' :
                     case '5' :
                     case '6' :
                     case '7' :
                     case '8' :
                     case '9' :
                         ins = match.group(c - '0');
                         break;
                     case '&' :
                         ins = match.group(0);
                         break;
                     case '`' :
                         ins = match.pre_match();
                         break;
                     case '\'' :
                         ins = match.post_match();
                         break;
                     case '+' :
                         ins = match_last(match);
                         break;
                     case '\\' :
                         sb.append(c);
                         continue;
                     default :
                         sb.append('\\');
                         sb.append(c);
                         continue;
                 }
                 if (!ins.isNil()) {
                     sb.append(((RubyString) ins).getByteList());
                 }
             } else {
                 sb.append(c);
             }
         }
     }
 
     /** rb_reg_regsub
      *
      */
     public IRubyObject regsub(IRubyObject str, RubyMatchData match) {
-        RubyString str2 = str.objAsString();
+        RubyString str2 = str.asString();
         ByteList sb = new ByteList(str2.getByteList().length()+30);
         regsub(str2,match,sb);
         return RubyString.newString(getRuntime(),sb);
     }
 
     /** rb_reg_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         if (this == original) return this;
         
         if (!(getMetaClass() == original.getMetaClass())){ // MRI also does a pointer comparison here
             throw getRuntime().newTypeError("wrong argument class");
         }
 
         RubyRegexp origRegexp = (RubyRegexp)original;
         pattern = origRegexp.pattern;
         code = origRegexp.code;
 
         return this;
     }
 
     /** rb_reg_inspect
      *
      */
     public IRubyObject inspect() {
         final String regex = pattern.toString();
 		final int length = regex.length();
         StringBuffer sb = new StringBuffer(length + 2);
 
         sb.append('/');
         for (int i = 0; i < length; i++) {
             char c = regex.charAt(i);
 
             if (RubyString.isAlnum(c)) {
                 sb.append(c);
             } else if (c == '/') {
                 if (i == 0 || regex.charAt(i - 1) != '\\') {
                     sb.append("\\");
                 }
             	sb.append(c);
             } else if (RubyString.isPrint(c)) {
                 sb.append(c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(Sprintf.sprintf(getRuntime(),"\\%.3o",c));
             }
         }
         sb.append('/');
 
         if(code == KCode.NONE) {
             sb.append('n');
         } else if(code == KCode.UTF8) {
             sb.append('u');
         } else if(code == KCode.SJIS) {
             sb.append('s');
         }
         if ((flags & REFlags.IGNORE_CASE) > 0) {
             sb.append('i');
         }
   
         if ((flags & REFlags.DOTALL) > 0) {
             sb.append('m');
         }
         
         if ((flags & REFlags.IGNORE_SPACES) > 0) {
             sb.append('x');
         }
 
         return getRuntime().newString(sb.toString());
     }
     
     /**
      * rb_reg_s_union
      */
     public static IRubyObject union(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             return newInstance(recv, new IRubyObject[] {recv.getRuntime().newString("(?!)")});
         }
         
         if (args.length == 1) {
             IRubyObject arg = args[0].convertToType("Regexp", "to_regexp", false);
             if (!arg.isNil()) {
                 return arg;
             }
             return newInstance(recv, new IRubyObject[] {quote(recv, args[0].convertToString())});
         }
         
         StringBuffer buffer = new StringBuffer();
         for (int i = 0; i < args.length; i++) {
         	if (i > 0) {
         		buffer.append("|");
             }
         	IRubyObject arg = args[i].convertToType("Regexp", "to_regexp", false);
             if (arg.isNil()) {
                 arg = quote(recv, args[i].convertToString());
             }
             buffer.append(arg.toString());
         }
         
         return newInstance(recv, new IRubyObject[] {recv.getRuntime().newString(buffer.toString())});
     }
 
     
     public IRubyObject to_s() {
         return getRuntime().newString(toString());
     }
     
     public String toString() {
     	StringBuffer buffer = new StringBuffer(100);
     	StringBuffer off = new StringBuffer(3);
     	
     	buffer.append("(?");
     	
     	flagToString(buffer, off, REFlags.DOTALL, 'm');
     	flagToString(buffer, off, REFlags.IGNORE_CASE, 'i');
     	flagToString(buffer, off, REFlags.IGNORE_SPACES, 'x');
 
 		if (off.length() > 0) {
 			buffer.append('-').append(off);
 		}
 
     	buffer.append(':');
         buffer.append(pattern.toString().replaceAll("^/|([^\\\\])/", "$1\\\\/"));
 		buffer.append(')');
 
     	return buffer.toString();
     }
 
     /** Helper method for the {@link #toString() toString} method which creates
      * an <i>on-off</i> pattern of {@link Pattern Pattern} flags. 
      * 
 	 * @param buffer the default buffer for the output
 	 * @param off temporary buffer for the off flags
 	 * @param flag a Pattern flag
 	 * @param c the char which represents the flag
 	 */
 	private void flagToString(StringBuffer buffer, StringBuffer off, int flag, char c) {
 		if ((flags & flag) != 0) {
     		buffer.append(c);
     	} else {
     		off.append(c);
     	}
 	}
 
     public static RubyRegexp unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyRegexp result = newRegexp(input.getRuntime(), 
                                       RubyString.byteListToString(input.unmarshalString()), input.unmarshalInt(), null);
         input.registerLinkTarget(result);
         return result;
     }
 
     public static void marshalTo(RubyRegexp regexp, MarshalStream output) throws java.io.IOException {
         output.writeString(regexp.pattern.toString());
 
         int _flags = 0;
         if ((regexp.flags & REFlags.DOTALL) > 0) {
             _flags |= RE_OPTION_MULTILINE;
         }
         if ((regexp.flags & REFlags.IGNORE_CASE) > 0) {
             _flags |= RE_OPTION_IGNORECASE;
         }
         if ((regexp.flags & REFlags.IGNORE_SPACES) > 0) {
             _flags |= RE_OPTION_EXTENDED;
         }
         output.writeInt(_flags);
     }
 	
 	public Pattern getPattern() {
 		return this.pattern;
 	}
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(this.pattern.toString().hashCode());
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index e0e2ef9538..5adc05c1db 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,2865 +1,2874 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Locale;
 import jregex.Matcher;
 import jregex.Pattern;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyString extends RubyObject {
     // Default record seperator
     private static final String DEFAULT_RS = "\n";
 
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_LT_SWITCHVALUE = 2;
     public static final byte AREF_SWITCHVALUE = 3;
     public static final byte ASET_SWITCHVALUE = 4;
     public static final byte NIL_P_SWITCHVALUE = 5;
     public static final byte EQUALEQUAL_SWITCHVALUE = 6;
     public static final byte OP_GE_SWITCHVALUE = 7;
     public static final byte OP_LSHIFT_SWITCHVALUE = 8;
     public static final byte EMPTY_P_SWITCHVALUE = 9;
     public static final byte TO_S_SWITCHVALUE = 10;
     public static final byte TO_I_SWITCHVALUE = 11;
 
     private ByteList value;
     private int hash;
     private RubyFixnum r_hash;
     private boolean validHash = false;
     private String stringValue;
 
     // @see IRuby.newString(...)
     private RubyString(Ruby runtime, CharSequence value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, byte[] value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, ByteList value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = new ByteList(ByteList.plain(value),false);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = new ByteList(value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = value;
     }
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType, Block block) {
         switch (switchvalue) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_plus(args[0]);
         case OP_LT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_lt(args[0]);
         case AREF_SWITCHVALUE:
             return aref(args);
         case ASET_SWITCHVALUE:
             return aset(args);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return eql(args[0]);
         case OP_GE_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_ge(args[0]);
         case OP_LSHIFT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return concat(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_I_SWITCHVALUE:
             return to_i(args);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     public Class getJavaClass() {
         return String.class;
     }
 
     /**
      * Remembers toString value, which is expensive for StringBuffer.
      */
     public String toString() {
         if (stringValue == null) {
             stringValue = new String(ByteList.plain(value.bytes),0,value.realSize);
         }
         return stringValue;
     }
 
     /**
      * This string has been changed, so invalidate stringValue.
      */
     void stringMutated() {
         stringValue = null;
         validHash = false;
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes), beg, len);
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.unsafeBytes(),0,bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes,0,bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     public static boolean isDigit(int c) {
         return c >= '0' && c <= '9';
     }
 
     public static boolean isUpper(int c) {
         return c >= 'A' && c <= 'Z';
     }
 
     public static boolean isLower(int c) {
         return c >= 'a' && c <= 'z';
     }
 
     public static boolean isLetter(int c) {
         return isUpper(c) || isLower(c);
     }
 
     public static boolean isAlnum(int c) {
         return isUpper(c) || isLower(c) || isDigit(c);
     }
 
     public static boolean isPrint(int c) {
         return c >= 0x20 && c <= 0x7E;
     }
     
     public IRubyObject checkStringType() {
         return this;
     }
 
     public IRubyObject to_s() {
         return this;
     }
 
     /* rb_str_cmp_m */
     public IRubyObject op_cmp(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newFixnum(cmp((RubyString)other));
         }
 
         // FIXME: This code does not appear to be applicable for <=> according to ri.
         // ri docs claim the other operand *must* be a String.
         /*ThreadContext context = getRuntime().getCurrentContext();
 
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject tmp = other.callMethod(context, "<=>", this);
 
             if (!tmp.isNil()) {
                 return tmp instanceof RubyFixnum ? tmp.callMethod(context, "-") :
                     getRuntime().newFixnum(0).callMethod(context, "-", tmp);
             }
         }*/
 
         return getRuntime().getNil();
     }
 
     public IRubyObject eql(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other == this) {
             return runtime.getTrue();
         } else if (!(other instanceof RubyString)) {
             if(other.respondsTo("to_str")) {
                 return other.callMethod(runtime.getCurrentContext(), "==", this);
             }
             return runtime.getFalse();
         }
         /* use Java implementation if both different String instances */
         return runtime.newBoolean(value.equals(((RubyString) other).value));
     }
 
     public IRubyObject op_plus(IRubyObject other) {
         RubyString str = RubyString.stringValue(other);
 
         ByteList newValue = new ByteList(value);
         newValue.append(str.value);
         return newString(getRuntime(), newValue).infectBy(other).infectBy(this);
     }
 
     public IRubyObject op_mul(IRubyObject other) {
         RubyInteger otherInteger =
-                (RubyInteger) other.convertType(RubyInteger.class, "Integer", "to_i");
+                (RubyInteger) other.convertToInteger();
         long len = otherInteger.getLongValue();
 
         if (len < 0) {
             throw getRuntime().newArgumentError("negative argument");
         }
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.length()) {
             throw getRuntime().newArgumentError("argument too big");
         }
         ByteList newBytes = new ByteList(value.length() * (int)len);
 
         for (int i = 0; i < len; i++) {
             newBytes.append(value);
         }
 
         RubyString newString = newString(getRuntime(), newBytes);
         newString.setTaint(isTaint());
         return newString;
     }
 
     public IRubyObject format(IRubyObject arg) {
         // FIXME: Should we make this work with platform's locale, or continue hardcoding US?
         return getRuntime().newString((ByteList)Sprintf.sprintf(Locale.US,getByteList(),arg));
     }
 
     public RubyFixnum hash() {
         hashCode();
         return r_hash;
     }
 
     public int hashCode() {
         if(!validHash) {
             hash = value.hashCode();
             r_hash = getRuntime().newFixnum(hash);
             validHash = true;
         }
         return hash;
     }
 
     public boolean equals(Object other) {
         if (this == other) {
             return true;
         }
 
         if (other instanceof RubyString) {
             RubyString string = (RubyString)other;
 
             if (string.value.equals(value)) {
                 return true;
             }
         }
 
         return false;
     }
 
     // Common enough check to make it a convenience method.
     private boolean sameAs(RubyString other) {
         return value.equals(other.value);
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
 
         IRubyObject str = obj.callMethod(obj.getRuntime().getCurrentContext(), "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
 
         if (obj.isTaint()) str.setTaint(true);
 
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public int cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         return toString();
     }
 
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     // Methods of the String class (rb_str_*):
 
     /** rb_str_new2
      *
      */
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, str);
     }
 
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] bytes2 = new byte[length];
         System.arraycopy(bytes, start, bytes2, 0, length);
         return new RubyString(runtime, bytes2);
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), (ByteList)value.clone());
     }
 
     public RubyString cat(byte[] str) {
         value.append(str);
         stringMutated();
         return this;
     }
 
     public RubyString cat(ByteList str) {
         value.append(str);
         stringMutated();
         return this;
     }
 
     public RubyString cat(byte ch) {
         value.append(ch);
         stringMutated();
         return this;
     }
 
     public IRubyObject to_str() {
         if (getMetaClass().getRealClass() != getRuntime().getString()) {
             return newString(getRuntime(), value.bytes());
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     public RubyString replace(IRubyObject other) {
         testFrozen("String");
          
         RubyString newValue = stringValue(other);
         if (this == other || sameAs(newValue)) {
             return this;
         }
         value.replace(newValue.value.bytes());
         stringMutated();
         return (RubyString) infectBy(other);
     }
 
     public RubyString reverse() {
         return newString(getRuntime(), (ByteList)value.clone()).reverse_bang();
     }
 
     public RubyString reverse_bang() {
         for (int i = 0; i < (value.length() / 2); i++) {
             byte b = (byte)value.get(i);
             value.set(i, value.get(value.length() - i - 1));
             value.set(value.length() - i - 1, b);
         }
         stringMutated();
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newString(recv.getRuntime(), "");
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (checkArgumentCount(args, 0, 1) == 1) {
             replace(args[0]);
         }
         return this;
     }
 
     public IRubyObject casecmp(IRubyObject other) {
         int compare = toString().compareToIgnoreCase(stringValue(other).toString());
         return RubyFixnum.newFixnum(getRuntime(), compare == 0 ? 0 : (compare < 0 ? -1 : 1));
     }
 
     /** rb_str_match
      *
      */
     public IRubyObject match(IRubyObject other) {
         if (other instanceof RubyRegexp) {
             return ((RubyRegexp) other).match(this);
         } else if (other instanceof RubyString) {
             throw getRuntime().newTypeError("type mismatch: String given");
         }
         return other.callMethod(getRuntime().getCurrentContext(), "=~", this);
     }
 
     /** rb_str_match2
      *
      */
     public IRubyObject match2() {
         return RubyRegexp.newRegexp(this, 0, null).match2();
     }
 
     /**
      * String#match(pattern)
      *
      * @param pattern Regexp or String
      */
     public IRubyObject match3(IRubyObject pattern) {
         if (pattern instanceof RubyRegexp) {
             return ((RubyRegexp)pattern).search2(toString());
         } else if (pattern instanceof RubyString) {
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern, 0, null);
             return regexp.search2(toString());
         } else if (pattern.respondsTo("to_str")) {
             // FIXME: is this cast safe?
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern.callMethod(getRuntime().getCurrentContext(), "to_str"), 0, null);
             return regexp.search2(toString());
         }
 
         // not regexp and not string, can't convert
         throw getRuntime().newTypeError("wrong argument type " + pattern.getMetaClass().getBaseName() + " (expected Regexp)");
     }
 
     /** rb_str_capitalize
      *
      */
     public IRubyObject capitalize() {
         RubyString result = (RubyString) dup();
         result.capitalize_bang();
         return result;
     }
 
     /** rb_str_capitalize_bang
      *
      */
     public IRubyObject capitalize_bang() {
         if (isEmpty()) {
             return getRuntime().getNil();
         }
         char capital = value.charAt(0);
         boolean changed = false;
         if (Character.isLetter(capital) && Character.isLowerCase(capital)) {
             value.set(0, (byte)Character.toUpperCase(capital));
             changed = true;
         }
 
         for (int i = 1; i < value.length(); i++) {
             capital = value.charAt(i);
             if (Character.isLetter(capital) && Character.isUpperCase(capital)) {
                 value.set(i, (byte)Character.toLowerCase(capital));
                 changed = true;
             }
         }
 
         if (changed) {
             stringMutated();
             return this;
         } else {
             return getRuntime().getNil();
         }
     }
 
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) >= 0);
         }
 
         return RubyComparable.op_ge(this, other);
     }
 
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) > 0);
         }
 
         return RubyComparable.op_gt(this, other);
     }
 
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) <= 0);
         }
 
         return RubyComparable.op_le(this, other);
     }
 
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) < 0);
         }
 
         return RubyComparable.op_lt(this, other);
     }
 
     public IRubyObject op_eql(IRubyObject other) {
         return equals(other) ? other.getRuntime().getTrue() : other.getRuntime().getFalse();
     }
 
     /** rb_str_upcase
      *
      */
     public RubyString upcase() {
         return newString(toString().toUpperCase());
     }
 
     /** rb_str_upcase_bang
      *
      */
     public IRubyObject upcase_bang() {
         boolean changed = false;
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
             if (!Character.isLetter(c) || Character.isUpperCase(c)) continue;
             value.set(i, (byte)Character.toUpperCase(c));
             changed = true;
         }
         if (changed) {
             stringMutated();
             return this;
         } else {
             return getRuntime().getNil();
         }
     }
 
     /** rb_str_downcase
      *
      */
     public RubyString downcase() {
         return newString(toString().toLowerCase());
     }
 
     /** rb_str_downcase_bang
      *
      */
     public IRubyObject downcase_bang() {
         boolean changed = false;
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
             if (!Character.isLetter(c) || Character.isLowerCase(c)) continue;
             value.set(i, (byte)Character.toLowerCase(c));
             changed = true;
         }
         if (changed) {
             stringMutated();
             return this;
         } else {
             return getRuntime().getNil();
         }
     }
 
     /** rb_str_swapcase
      *
      */
     public RubyString swapcase() {
         RubyString newString = newString(getRuntime(), (ByteList)value.clone());
         IRubyObject swappedString = newString.swapcase_bang();
 
         return (RubyString) (swappedString.isNil() ? newString : swappedString);
     }
 
     /** rb_str_swapcase_bang
      *
      */
     public IRubyObject swapcase_bang() {
         boolean changesMade = false;
 
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
 
             if (!Character.isLetter(c)) {
                 continue;
             } else if (Character.isLowerCase(c)) {
                 changesMade = true;
                 value.set(i, (byte)Character.toUpperCase(c));
             } else {
                 changesMade = true;
                 value.set(i, (byte)Character.toLowerCase(c));
             }
         }
         if (changesMade) {
             stringMutated();
             return this;
         }
         return getRuntime().getNil();
     }
 
     /** rb_str_dump
      *
      */
     public RubyString dump() {
         return inspect(true);
     }
 
     public IRubyObject insert(IRubyObject indexArg, IRubyObject stringArg) {
         int index = (int) indexArg.convertToInteger().getLongValue();
         if (index < 0) {
             index += value.length() + 1;
         }
 
         if (index < 0 || index > value.length()) {
             throw getRuntime().newIndexError("index " + index + " out of range");
         }
 
         ByteList insert = ((RubyString)stringArg.convertToString()).value;
         value.unsafeReplace(index, 0, insert);
         stringMutated();
         return this;
     }
 
     /** rb_str_inspect
      *
      */
     public IRubyObject inspect() {
         return inspect(false);
     }
 
     private RubyString inspect(boolean dump) {
         final int length = value.length();
         Ruby runtime = getRuntime();
 
         ByteList sb = new ByteList(length + 2 + length / 100);
 
         sb.append('\"');
 
         // FIXME: This may not be unicode-safe
         for (int i = 0; i < length; i++) {
             int c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 sb.append((char)c);
             } else if (runtime.getKCode() == KCode.UTF8 && c == 0xEF) {
                 // don't escape encoded UTF8 characters, leave them as bytes
                 // append byte order mark plus two character bytes
                 sb.append((char)c);
                 sb.append((char)(value.get(++i) & 0xFF));
                 sb.append((char)(value.get(++i) & 0xFF));
             } else if (c == '\"' || c == '\\') {
                 sb.append('\\').append((char)c);
             } else if (c == '#') {
                 sb.append('\\').append((char)c);
             } else if (isPrint(c)) {
                 sb.append((char)c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(ByteList.plain(Sprintf.sprintf(runtime,"\\%.3o",c)));
             }
         }
 
         sb.append('\"');
         return getRuntime().newString(sb);
     }
 
     /** rb_str_length
      *
      */
     public RubyFixnum length() {
         return getRuntime().newFixnum(value.length());
     }
 
     /** rb_str_empty
      *
      */
     public RubyBoolean empty() {
         return getRuntime().newBoolean(isEmpty());
     }
 
     private boolean isEmpty() {
         return value.length() == 0;
     }
 
     /** rb_str_append
      *
      */
     public RubyString append(IRubyObject other) {
         infectBy(other);
         return cat(stringValue(other).value);
     }
 
     /** rb_str_concat
      *
      */
     public RubyString concat(IRubyObject other) {
         if ((other instanceof RubyFixnum) && ((RubyFixnum) other).getLongValue() < 256) {
             return cat((byte) ((RubyFixnum) other).getLongValue());
         }
         return append(other);
     }
 
     /** rb_str_crypt
      *
      */
     public RubyString crypt(IRubyObject other) {
         String salt = stringValue(other).getValue().toString();
         if(salt.length()<2) {
             throw getRuntime().newArgumentError("salt too short(need >=2 bytes)");
         }
 
         salt = salt.substring(0,2);
         return getRuntime().newString(JavaCrypt.crypt(salt, this.toString()));
     }
 
 
     public static class JavaCrypt {
         private static java.util.Random r_gen = new java.util.Random();
 
         private static final char theBaseSalts[] = {
             'a','b','c','d','e','f','g','h','i','j','k','l','m',
             'n','o','p','q','r','s','t','u','v','w','x','y','z',
             'A','B','C','D','E','F','G','H','I','J','K','L','M',
             'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
             '0','1','2','3','4','5','6','7','8','9','/','.'};
 
         private static final int ITERATIONS = 16;
 
         private static final int con_salt[] = {
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
             0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
             0x0A, 0x0B, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A,
             0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12,
             0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
             0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22,
             0x23, 0x24, 0x25, 0x20, 0x21, 0x22, 0x23, 0x24,
             0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C,
             0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34,
             0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C,
             0x3D, 0x3E, 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00,
         };
 
         private static final boolean shifts2[] = {
             false, false, true, true, true, true, true, true,
             false, true,  true, true, true, true, true, false };
 
         private static final int skb[][] = {
             {
                 /* for C bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x00000010, 0x20000000, 0x20000010,
                 0x00010000, 0x00010010, 0x20010000, 0x20010010,
                 0x00000800, 0x00000810, 0x20000800, 0x20000810,
                 0x00010800, 0x00010810, 0x20010800, 0x20010810,
                 0x00000020, 0x00000030, 0x20000020, 0x20000030,
                 0x00010020, 0x00010030, 0x20010020, 0x20010030,
                 0x00000820, 0x00000830, 0x20000820, 0x20000830,
                 0x00010820, 0x00010830, 0x20010820, 0x20010830,
                 0x00080000, 0x00080010, 0x20080000, 0x20080010,
                 0x00090000, 0x00090010, 0x20090000, 0x20090010,
                 0x00080800, 0x00080810, 0x20080800, 0x20080810,
                 0x00090800, 0x00090810, 0x20090800, 0x20090810,
                 0x00080020, 0x00080030, 0x20080020, 0x20080030,
                 0x00090020, 0x00090030, 0x20090020, 0x20090030,
                 0x00080820, 0x00080830, 0x20080820, 0x20080830,
                 0x00090820, 0x00090830, 0x20090820, 0x20090830,
             },{
                 /* for C bits (numbered as per FIPS 46) 7 8 10 11 12 13 */
                 0x00000000, 0x02000000, 0x00002000, 0x02002000,
                 0x00200000, 0x02200000, 0x00202000, 0x02202000,
                 0x00000004, 0x02000004, 0x00002004, 0x02002004,
                 0x00200004, 0x02200004, 0x00202004, 0x02202004,
                 0x00000400, 0x02000400, 0x00002400, 0x02002400,
                 0x00200400, 0x02200400, 0x00202400, 0x02202400,
                 0x00000404, 0x02000404, 0x00002404, 0x02002404,
                 0x00200404, 0x02200404, 0x00202404, 0x02202404,
                 0x10000000, 0x12000000, 0x10002000, 0x12002000,
                 0x10200000, 0x12200000, 0x10202000, 0x12202000,
                 0x10000004, 0x12000004, 0x10002004, 0x12002004,
                 0x10200004, 0x12200004, 0x10202004, 0x12202004,
                 0x10000400, 0x12000400, 0x10002400, 0x12002400,
                 0x10200400, 0x12200400, 0x10202400, 0x12202400,
                 0x10000404, 0x12000404, 0x10002404, 0x12002404,
                 0x10200404, 0x12200404, 0x10202404, 0x12202404,
             },{
                 /* for C bits (numbered as per FIPS 46) 14 15 16 17 19 20 */
                 0x00000000, 0x00000001, 0x00040000, 0x00040001,
                 0x01000000, 0x01000001, 0x01040000, 0x01040001,
                 0x00000002, 0x00000003, 0x00040002, 0x00040003,
                 0x01000002, 0x01000003, 0x01040002, 0x01040003,
                 0x00000200, 0x00000201, 0x00040200, 0x00040201,
                 0x01000200, 0x01000201, 0x01040200, 0x01040201,
                 0x00000202, 0x00000203, 0x00040202, 0x00040203,
                 0x01000202, 0x01000203, 0x01040202, 0x01040203,
                 0x08000000, 0x08000001, 0x08040000, 0x08040001,
                 0x09000000, 0x09000001, 0x09040000, 0x09040001,
                 0x08000002, 0x08000003, 0x08040002, 0x08040003,
                 0x09000002, 0x09000003, 0x09040002, 0x09040003,
                 0x08000200, 0x08000201, 0x08040200, 0x08040201,
                 0x09000200, 0x09000201, 0x09040200, 0x09040201,
                 0x08000202, 0x08000203, 0x08040202, 0x08040203,
                 0x09000202, 0x09000203, 0x09040202, 0x09040203,
             },{
                 /* for C bits (numbered as per FIPS 46) 21 23 24 26 27 28 */
                 0x00000000, 0x00100000, 0x00000100, 0x00100100,
                 0x00000008, 0x00100008, 0x00000108, 0x00100108,
                 0x00001000, 0x00101000, 0x00001100, 0x00101100,
                 0x00001008, 0x00101008, 0x00001108, 0x00101108,
                 0x04000000, 0x04100000, 0x04000100, 0x04100100,
                 0x04000008, 0x04100008, 0x04000108, 0x04100108,
                 0x04001000, 0x04101000, 0x04001100, 0x04101100,
                 0x04001008, 0x04101008, 0x04001108, 0x04101108,
                 0x00020000, 0x00120000, 0x00020100, 0x00120100,
                 0x00020008, 0x00120008, 0x00020108, 0x00120108,
                 0x00021000, 0x00121000, 0x00021100, 0x00121100,
                 0x00021008, 0x00121008, 0x00021108, 0x00121108,
                 0x04020000, 0x04120000, 0x04020100, 0x04120100,
                 0x04020008, 0x04120008, 0x04020108, 0x04120108,
                 0x04021000, 0x04121000, 0x04021100, 0x04121100,
                 0x04021008, 0x04121008, 0x04021108, 0x04121108,
             },{
                 /* for D bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x10000000, 0x00010000, 0x10010000,
                 0x00000004, 0x10000004, 0x00010004, 0x10010004,
                 0x20000000, 0x30000000, 0x20010000, 0x30010000,
                 0x20000004, 0x30000004, 0x20010004, 0x30010004,
                 0x00100000, 0x10100000, 0x00110000, 0x10110000,
                 0x00100004, 0x10100004, 0x00110004, 0x10110004,
                 0x20100000, 0x30100000, 0x20110000, 0x30110000,
                 0x20100004, 0x30100004, 0x20110004, 0x30110004,
                 0x00001000, 0x10001000, 0x00011000, 0x10011000,
                 0x00001004, 0x10001004, 0x00011004, 0x10011004,
                 0x20001000, 0x30001000, 0x20011000, 0x30011000,
                 0x20001004, 0x30001004, 0x20011004, 0x30011004,
                 0x00101000, 0x10101000, 0x00111000, 0x10111000,
                 0x00101004, 0x10101004, 0x00111004, 0x10111004,
                 0x20101000, 0x30101000, 0x20111000, 0x30111000,
                 0x20101004, 0x30101004, 0x20111004, 0x30111004,
             },{
                 /* for D bits (numbered as per FIPS 46) 8 9 11 12 13 14 */
                 0x00000000, 0x08000000, 0x00000008, 0x08000008,
                 0x00000400, 0x08000400, 0x00000408, 0x08000408,
                 0x00020000, 0x08020000, 0x00020008, 0x08020008,
                 0x00020400, 0x08020400, 0x00020408, 0x08020408,
                 0x00000001, 0x08000001, 0x00000009, 0x08000009,
                 0x00000401, 0x08000401, 0x00000409, 0x08000409,
                 0x00020001, 0x08020001, 0x00020009, 0x08020009,
                 0x00020401, 0x08020401, 0x00020409, 0x08020409,
                 0x02000000, 0x0A000000, 0x02000008, 0x0A000008,
                 0x02000400, 0x0A000400, 0x02000408, 0x0A000408,
                 0x02020000, 0x0A020000, 0x02020008, 0x0A020008,
                 0x02020400, 0x0A020400, 0x02020408, 0x0A020408,
                 0x02000001, 0x0A000001, 0x02000009, 0x0A000009,
                 0x02000401, 0x0A000401, 0x02000409, 0x0A000409,
                 0x02020001, 0x0A020001, 0x02020009, 0x0A020009,
                 0x02020401, 0x0A020401, 0x02020409, 0x0A020409,
             },{
                 /* for D bits (numbered as per FIPS 46) 16 17 18 19 20 21 */
                 0x00000000, 0x00000100, 0x00080000, 0x00080100,
                 0x01000000, 0x01000100, 0x01080000, 0x01080100,
                 0x00000010, 0x00000110, 0x00080010, 0x00080110,
                 0x01000010, 0x01000110, 0x01080010, 0x01080110,
                 0x00200000, 0x00200100, 0x00280000, 0x00280100,
                 0x01200000, 0x01200100, 0x01280000, 0x01280100,
                 0x00200010, 0x00200110, 0x00280010, 0x00280110,
                 0x01200010, 0x01200110, 0x01280010, 0x01280110,
                 0x00000200, 0x00000300, 0x00080200, 0x00080300,
                 0x01000200, 0x01000300, 0x01080200, 0x01080300,
                 0x00000210, 0x00000310, 0x00080210, 0x00080310,
                 0x01000210, 0x01000310, 0x01080210, 0x01080310,
                 0x00200200, 0x00200300, 0x00280200, 0x00280300,
                 0x01200200, 0x01200300, 0x01280200, 0x01280300,
                 0x00200210, 0x00200310, 0x00280210, 0x00280310,
                 0x01200210, 0x01200310, 0x01280210, 0x01280310,
             },{
                 /* for D bits (numbered as per FIPS 46) 22 23 24 25 27 28 */
                 0x00000000, 0x04000000, 0x00040000, 0x04040000,
                 0x00000002, 0x04000002, 0x00040002, 0x04040002,
                 0x00002000, 0x04002000, 0x00042000, 0x04042000,
                 0x00002002, 0x04002002, 0x00042002, 0x04042002,
                 0x00000020, 0x04000020, 0x00040020, 0x04040020,
                 0x00000022, 0x04000022, 0x00040022, 0x04040022,
                 0x00002020, 0x04002020, 0x00042020, 0x04042020,
                 0x00002022, 0x04002022, 0x00042022, 0x04042022,
                 0x00000800, 0x04000800, 0x00040800, 0x04040800,
                 0x00000802, 0x04000802, 0x00040802, 0x04040802,
                 0x00002800, 0x04002800, 0x00042800, 0x04042800,
                 0x00002802, 0x04002802, 0x00042802, 0x04042802,
                 0x00000820, 0x04000820, 0x00040820, 0x04040820,
                 0x00000822, 0x04000822, 0x00040822, 0x04040822,
                 0x00002820, 0x04002820, 0x00042820, 0x04042820,
                 0x00002822, 0x04002822, 0x00042822, 0x04042822,
             }
         };
 
         private static final int SPtrans[][] = {
             {
                 /* nibble 0 */
                 0x00820200, 0x00020000, 0x80800000, 0x80820200,
                 0x00800000, 0x80020200, 0x80020000, 0x80800000,
                 0x80020200, 0x00820200, 0x00820000, 0x80000200,
                 0x80800200, 0x00800000, 0x00000000, 0x80020000,
                 0x00020000, 0x80000000, 0x00800200, 0x00020200,
                 0x80820200, 0x00820000, 0x80000200, 0x00800200,
                 0x80000000, 0x00000200, 0x00020200, 0x80820000,
                 0x00000200, 0x80800200, 0x80820000, 0x00000000,
                 0x00000000, 0x80820200, 0x00800200, 0x80020000,
                 0x00820200, 0x00020000, 0x80000200, 0x00800200,
                 0x80820000, 0x00000200, 0x00020200, 0x80800000,
                 0x80020200, 0x80000000, 0x80800000, 0x00820000,
                 0x80820200, 0x00020200, 0x00820000, 0x80800200,
                 0x00800000, 0x80000200, 0x80020000, 0x00000000,
                 0x00020000, 0x00800000, 0x80800200, 0x00820200,
                 0x80000000, 0x80820000, 0x00000200, 0x80020200,
             },{
                 /* nibble 1 */
                 0x10042004, 0x00000000, 0x00042000, 0x10040000,
                 0x10000004, 0x00002004, 0x10002000, 0x00042000,
                 0x00002000, 0x10040004, 0x00000004, 0x10002000,
                 0x00040004, 0x10042000, 0x10040000, 0x00000004,
                 0x00040000, 0x10002004, 0x10040004, 0x00002000,
                 0x00042004, 0x10000000, 0x00000000, 0x00040004,
                 0x10002004, 0x00042004, 0x10042000, 0x10000004,
                 0x10000000, 0x00040000, 0x00002004, 0x10042004,
                 0x00040004, 0x10042000, 0x10002000, 0x00042004,
                 0x10042004, 0x00040004, 0x10000004, 0x00000000,
                 0x10000000, 0x00002004, 0x00040000, 0x10040004,
                 0x00002000, 0x10000000, 0x00042004, 0x10002004,
                 0x10042000, 0x00002000, 0x00000000, 0x10000004,
                 0x00000004, 0x10042004, 0x00042000, 0x10040000,
                 0x10040004, 0x00040000, 0x00002004, 0x10002000,
                 0x10002004, 0x00000004, 0x10040000, 0x00042000,
             },{
                 /* nibble 2 */
                 0x41000000, 0x01010040, 0x00000040, 0x41000040,
                 0x40010000, 0x01000000, 0x41000040, 0x00010040,
                 0x01000040, 0x00010000, 0x01010000, 0x40000000,
                 0x41010040, 0x40000040, 0x40000000, 0x41010000,
                 0x00000000, 0x40010000, 0x01010040, 0x00000040,
                 0x40000040, 0x41010040, 0x00010000, 0x41000000,
                 0x41010000, 0x01000040, 0x40010040, 0x01010000,
                 0x00010040, 0x00000000, 0x01000000, 0x40010040,
                 0x01010040, 0x00000040, 0x40000000, 0x00010000,
                 0x40000040, 0x40010000, 0x01010000, 0x41000040,
                 0x00000000, 0x01010040, 0x00010040, 0x41010000,
                 0x40010000, 0x01000000, 0x41010040, 0x40000000,
                 0x40010040, 0x41000000, 0x01000000, 0x41010040,
                 0x00010000, 0x01000040, 0x41000040, 0x00010040,
                 0x01000040, 0x00000000, 0x41010000, 0x40000040,
                 0x41000000, 0x40010040, 0x00000040, 0x01010000,
             },{
                 /* nibble 3 */
                 0x00100402, 0x04000400, 0x00000002, 0x04100402,
                 0x00000000, 0x04100000, 0x04000402, 0x00100002,
                 0x04100400, 0x04000002, 0x04000000, 0x00000402,
                 0x04000002, 0x00100402, 0x00100000, 0x04000000,
                 0x04100002, 0x00100400, 0x00000400, 0x00000002,
                 0x00100400, 0x04000402, 0x04100000, 0x00000400,
                 0x00000402, 0x00000000, 0x00100002, 0x04100400,
                 0x04000400, 0x04100002, 0x04100402, 0x00100000,
                 0x04100002, 0x00000402, 0x00100000, 0x04000002,
                 0x00100400, 0x04000400, 0x00000002, 0x04100000,
                 0x04000402, 0x00000000, 0x00000400, 0x00100002,
                 0x00000000, 0x04100002, 0x04100400, 0x00000400,
                 0x04000000, 0x04100402, 0x00100402, 0x00100000,
                 0x04100402, 0x00000002, 0x04000400, 0x00100402,
                 0x00100002, 0x00100400, 0x04100000, 0x04000402,
                 0x00000402, 0x04000000, 0x04000002, 0x04100400,
             },{
                 /* nibble 4 */
                 0x02000000, 0x00004000, 0x00000100, 0x02004108,
                 0x02004008, 0x02000100, 0x00004108, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x00004100,
                 0x02000108, 0x02004008, 0x02004100, 0x00000000,
                 0x00004100, 0x02000000, 0x00004008, 0x00000108,
                 0x02000100, 0x00004108, 0x00000000, 0x02000008,
                 0x00000008, 0x02000108, 0x02004108, 0x00004008,
                 0x02004000, 0x00000100, 0x00000108, 0x02004100,
                 0x02004100, 0x02000108, 0x00004008, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x02000100,
                 0x02000000, 0x00004100, 0x02004108, 0x00000000,
                 0x00004108, 0x02000000, 0x00000100, 0x00004008,
                 0x02000108, 0x00000100, 0x00000000, 0x02004108,
                 0x02004008, 0x02004100, 0x00000108, 0x00004000,
                 0x00004100, 0x02004008, 0x02000100, 0x00000108,
                 0x00000008, 0x00004108, 0x02004000, 0x02000008,
             },{
                 /* nibble 5 */
                 0x20000010, 0x00080010, 0x00000000, 0x20080800,
                 0x00080010, 0x00000800, 0x20000810, 0x00080000,
                 0x00000810, 0x20080810, 0x00080800, 0x20000000,
                 0x20000800, 0x20000010, 0x20080000, 0x00080810,
                 0x00080000, 0x20000810, 0x20080010, 0x00000000,
                 0x00000800, 0x00000010, 0x20080800, 0x20080010,
                 0x20080810, 0x20080000, 0x20000000, 0x00000810,
                 0x00000010, 0x00080800, 0x00080810, 0x20000800,
                 0x00000810, 0x20000000, 0x20000800, 0x00080810,
                 0x20080800, 0x00080010, 0x00000000, 0x20000800,
                 0x20000000, 0x00000800, 0x20080010, 0x00080000,
                 0x00080010, 0x20080810, 0x00080800, 0x00000010,
                 0x20080810, 0x00080800, 0x00080000, 0x20000810,
                 0x20000010, 0x20080000, 0x00080810, 0x00000000,
                 0x00000800, 0x20000010, 0x20000810, 0x20080800,
                 0x20080000, 0x00000810, 0x00000010, 0x20080010,
             },{
                 /* nibble 6 */
                 0x00001000, 0x00000080, 0x00400080, 0x00400001,
                 0x00401081, 0x00001001, 0x00001080, 0x00000000,
                 0x00400000, 0x00400081, 0x00000081, 0x00401000,
                 0x00000001, 0x00401080, 0x00401000, 0x00000081,
                 0x00400081, 0x00001000, 0x00001001, 0x00401081,
                 0x00000000, 0x00400080, 0x00400001, 0x00001080,
                 0x00401001, 0x00001081, 0x00401080, 0x00000001,
                 0x00001081, 0x00401001, 0x00000080, 0x00400000,
                 0x00001081, 0x00401000, 0x00401001, 0x00000081,
                 0x00001000, 0x00000080, 0x00400000, 0x00401001,
                 0x00400081, 0x00001081, 0x00001080, 0x00000000,
                 0x00000080, 0x00400001, 0x00000001, 0x00400080,
                 0x00000000, 0x00400081, 0x00400080, 0x00001080,
                 0x00000081, 0x00001000, 0x00401081, 0x00400000,
                 0x00401080, 0x00000001, 0x00001001, 0x00401081,
                 0x00400001, 0x00401080, 0x00401000, 0x00001001,
             },{
                 /* nibble 7 */
                 0x08200020, 0x08208000, 0x00008020, 0x00000000,
                 0x08008000, 0x00200020, 0x08200000, 0x08208020,
                 0x00000020, 0x08000000, 0x00208000, 0x00008020,
                 0x00208020, 0x08008020, 0x08000020, 0x08200000,
                 0x00008000, 0x00208020, 0x00200020, 0x08008000,
                 0x08208020, 0x08000020, 0x00000000, 0x00208000,
                 0x08000000, 0x00200000, 0x08008020, 0x08200020,
                 0x00200000, 0x00008000, 0x08208000, 0x00000020,
                 0x00200000, 0x00008000, 0x08000020, 0x08208020,
                 0x00008020, 0x08000000, 0x00000000, 0x00208000,
                 0x08200020, 0x08008020, 0x08008000, 0x00200020,
                 0x08208000, 0x00000020, 0x00200020, 0x08008000,
                 0x08208020, 0x00200000, 0x08200000, 0x08000020,
                 0x00208000, 0x00008020, 0x08008020, 0x08200000,
                 0x00000020, 0x08208000, 0x00208020, 0x00000000,
                 0x08000000, 0x08200020, 0x00008000, 0x00208020
             }
         };
 
         private static final int cov_2char[] = {
             0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
             0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44,
             0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C,
             0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54,
             0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x61, 0x62,
             0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
             0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72,
             0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
         };
 
         private static final int byteToUnsigned(byte b) {
             return b & 0xFF;
         }
 
         private static int fourBytesToInt(byte b[], int offset) {
             int value;
             value  =  byteToUnsigned(b[offset++]);
             value |= (byteToUnsigned(b[offset++]) <<  8);
             value |= (byteToUnsigned(b[offset++]) << 16);
             value |= (byteToUnsigned(b[offset++]) << 24);
             return(value);
         }
 
         private static final void intToFourBytes(int iValue, byte b[], int offset) {
             b[offset++] = (byte)((iValue)        & 0xff);
             b[offset++] = (byte)((iValue >>> 8 ) & 0xff);
             b[offset++] = (byte)((iValue >>> 16) & 0xff);
             b[offset++] = (byte)((iValue >>> 24) & 0xff);
         }
 
         private static final void PERM_OP(int a, int b, int n, int m, int results[]) {
             int t;
 
             t = ((a >>> n) ^ b) & m;
             a ^= t << n;
             b ^= t;
 
             results[0] = a;
             results[1] = b;
         }
 
         private static final int HPERM_OP(int a, int n, int m) {
             int t;
 
             t = ((a << (16 - n)) ^ a) & m;
             a = a ^ t ^ (t >>> (16 - n));
 
             return(a);
         }
 
         private static int [] des_set_key(byte key[]) {
             int schedule[] = new int[ITERATIONS * 2];
 
             int c = fourBytesToInt(key, 0);
             int d = fourBytesToInt(key, 4);
 
             int results[] = new int[2];
 
             PERM_OP(d, c, 4, 0x0f0f0f0f, results);
             d = results[0]; c = results[1];
 
             c = HPERM_OP(c, -2, 0xcccc0000);
             d = HPERM_OP(d, -2, 0xcccc0000);
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             PERM_OP(c, d, 8, 0x00ff00ff, results);
             c = results[0]; d = results[1];
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             d = (((d & 0x000000ff) <<  16) |  (d & 0x0000ff00)     |
                  ((d & 0x00ff0000) >>> 16) | ((c & 0xf0000000) >>> 4));
             c &= 0x0fffffff;
 
             int s, t;
             int j = 0;
 
             for(int i = 0; i < ITERATIONS; i ++) {
                 if(shifts2[i]) {
                     c = (c >>> 2) | (c << 26);
                     d = (d >>> 2) | (d << 26);
                 } else {
                     c = (c >>> 1) | (c << 27);
                     d = (d >>> 1) | (d << 27);
                 }
 
                 c &= 0x0fffffff;
                 d &= 0x0fffffff;
 
                 s = skb[0][ (c       ) & 0x3f                       ]|
                     skb[1][((c >>>  6) & 0x03) | ((c >>>  7) & 0x3c)]|
                     skb[2][((c >>> 13) & 0x0f) | ((c >>> 14) & 0x30)]|
                     skb[3][((c >>> 20) & 0x01) | ((c >>> 21) & 0x06) |
                            ((c >>> 22) & 0x38)];
 
                 t = skb[4][ (d     )  & 0x3f                       ]|
                     skb[5][((d >>> 7) & 0x03) | ((d >>>  8) & 0x3c)]|
                     skb[6][ (d >>>15) & 0x3f                       ]|
                     skb[7][((d >>>21) & 0x0f) | ((d >>> 22) & 0x30)];
 
                 schedule[j++] = ((t <<  16) | (s & 0x0000ffff)) & 0xffffffff;
                 s             = ((s >>> 16) | (t & 0xffff0000));
 
                 s             = (s << 4) | (s >>> 28);
                 schedule[j++] = s & 0xffffffff;
             }
             return(schedule);
         }
 
         private static final int D_ENCRYPT(int L, int R, int S, int E0, int E1, int s[]) {
             int t, u, v;
 
             v = R ^ (R >>> 16);
             u = v & E0;
             v = v & E1;
             u = (u ^ (u << 16)) ^ R ^ s[S];
             t = (v ^ (v << 16)) ^ R ^ s[S + 1];
             t = (t >>> 4) | (t << 28);
 
             L ^= SPtrans[1][(t       ) & 0x3f] |
                 SPtrans[3][(t >>>  8) & 0x3f] |
                 SPtrans[5][(t >>> 16) & 0x3f] |
                 SPtrans[7][(t >>> 24) & 0x3f] |
                 SPtrans[0][(u       ) & 0x3f] |
                 SPtrans[2][(u >>>  8) & 0x3f] |
                 SPtrans[4][(u >>> 16) & 0x3f] |
                 SPtrans[6][(u >>> 24) & 0x3f];
 
             return(L);
         }
 
         private static final int [] body(int schedule[], int Eswap0, int Eswap1) {
             int left = 0;
             int right = 0;
             int t     = 0;
 
             for(int j = 0; j < 25; j ++) {
                 for(int i = 0; i < ITERATIONS * 2; i += 4) {
                     left  = D_ENCRYPT(left,  right, i,     Eswap0, Eswap1, schedule);
                     right = D_ENCRYPT(right, left,  i + 2, Eswap0, Eswap1, schedule);
                 }
                 t     = left;
                 left  = right;
                 right = t;
             }
 
             t = right;
 
             right = (left >>> 1) | (left << 31);
             left  = (t    >>> 1) | (t    << 31);
 
             left  &= 0xffffffff;
             right &= 0xffffffff;
 
             int results[] = new int[2];
 
             PERM_OP(right, left, 1, 0x55555555, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 8, 0x00ff00ff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 2, 0x33333333, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 16, 0x0000ffff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 4, 0x0f0f0f0f, results);
             right = results[0]; left = results[1];
 
             int out[] = new int[2];
 
             out[0] = left; out[1] = right;
 
             return(out);
         }
 
         public static final String crypt(String salt, String original) {
             while(salt.length() < 2)
                 salt += getSaltChar();
 
             StringBuffer buffer = new StringBuffer("             ");
 
             char charZero = salt.charAt(0);
             char charOne  = salt.charAt(1);
 
             buffer.setCharAt(0, charZero);
             buffer.setCharAt(1, charOne);
 
             int Eswap0 = con_salt[(int)charZero];
             int Eswap1 = con_salt[(int)charOne] << 4;
 
             byte key[] = new byte[8];
 
             for(int i = 0; i < key.length; i ++) {
                 key[i] = (byte)0;
             }
 
             for(int i = 0; i < key.length && i < original.length(); i ++) {
                 int iChar = (int)original.charAt(i);
 
                 key[i] = (byte)(iChar << 1);
             }
 
             int schedule[] = des_set_key(key);
             int out[]      = body(schedule, Eswap0, Eswap1);
 
             byte b[] = new byte[9];
 
             intToFourBytes(out[0], b, 0);
             intToFourBytes(out[1], b, 4);
             b[8] = 0;
 
             for(int i = 2, y = 0, u = 0x80; i < 13; i ++) {
                 for(int j = 0, c = 0; j < 6; j ++) {
                     c <<= 1;
 
                     if(((int)b[y] & u) != 0)
                         c |= 1;
 
                     u >>>= 1;
 
                     if(u == 0) {
                         y++;
                         u = 0x80;
                     }
                     buffer.setCharAt(i, (char)cov_2char[c]);
                 }
             }
             return(buffer.toString());
         }
 
         private static String getSaltChar() {
             return JavaCrypt.getSaltChar(1);
         }
 
         private static String getSaltChar(int amount) {
             StringBuffer sb = new StringBuffer();
             for(int i=amount;i>0;i--) {
                 sb.append(theBaseSalts[(Math.abs(r_gen.nextInt())%64)]);
             }
             return sb.toString();
         }
 
         public static boolean check(String theClear,String theCrypt) {
             String theTest = JavaCrypt.crypt(theCrypt.substring(0,2),theClear);
             return theTest.equals(theCrypt);
         }
 
         public static String crypt(String theClear) {
             return JavaCrypt.crypt(getSaltChar(2),theClear);
         }
     }
 
     /* RubyString aka rb_string_value */
     public static RubyString stringValue(IRubyObject object) {
         return (RubyString) (object instanceof RubyString ? object :
-            object.convertType(RubyString.class, "String", "to_str"));
+            object.convertToString());
     }
 
     /** rb_str_sub
      *
      */
     public IRubyObject sub(IRubyObject[] args, Block block) {
         return sub(args, false, block);
     }
 
     /** rb_str_sub_bang
      *
      */
     public IRubyObject sub_bang(IRubyObject[] args, Block block) {
         return sub(args, true, block);
     }
 
     private IRubyObject sub(IRubyObject[] args, boolean bang, Block block) {
         IRubyObject repl = getRuntime().getNil();
         boolean iter = false;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 1 && block.isGiven()) {
             iter = true;
         } else if (args.length == 2) {
             repl = args[1];
+            if (!repl.isKindOf(getRuntime().getString())) {
+                repl = repl.convertToString();
+            }
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments");
         }
         RubyRegexp pat = RubyRegexp.regexpValue(args[0]);
 
         String intern = toString();
 
         if (pat.search(intern, 0) >= 0) {
             RubyMatchData match = (RubyMatchData) tc.getBackref();
             RubyString newStr = match.pre_match();
             newStr.append(iter ? block.yield(tc, match.group(0)) : pat.regsub(repl, match));
             newStr.append(match.post_match());
             if (bang) {
                 value = newStr.value;
                 stringMutated();
                 infectBy(repl);
                 return this;
             }
 
             newStr.setTaint(isTaint() || repl.isTaint());
             
             return newStr;
         }
 
         return bang ? getRuntime().getNil() : this;
     }
 
     /** rb_str_gsub
      *
      */
     public IRubyObject gsub(IRubyObject[] args, Block block) {
         return gsub(args, false, block);
     }
 
     /** rb_str_gsub_bang
      *
      */
     public IRubyObject gsub_bang(IRubyObject[] args, Block block) {
         return gsub(args, true, block);
     }
 
     private IRubyObject gsub(IRubyObject[] args, boolean bang, Block block) {
         // TODO: improve implementation. this is _really_ slow
         IRubyObject repl = getRuntime().getNil();
         RubyMatchData match;
         boolean iter = false;
         if (args.length == 1 && block.isGiven()) {
             iter = true;
         } else if (args.length == 2) {
             repl = args[1];
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments");
         }
         boolean taint = repl.isTaint();
-        RubyRegexp pat = RubyRegexp.regexpValue(args[0]);
+        RubyRegexp pat = null;
+         if (args[0].isKindOf(getRuntime().getClass("Regexp"))) {
+            pat = (RubyRegexp)args[0];
+        } else if (args[0].isKindOf(getRuntime().getString())) {
+            pat = RubyRegexp.regexpValue(args[0]);
+        } else {
+            // FIXME: This should result in an error about not converting to regexp, no?
+            pat = RubyRegexp.regexpValue(args[0].convertToString());
+        }
 
         String str = toString();
         int beg = pat.search(str, 0);
         if (beg < 0) {
             return bang ? getRuntime().getNil() : dup();
         }
         ByteList sbuf = new ByteList(this.value.length());
         IRubyObject newStr;
         int offset = 0;
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         ThreadContext tc = getRuntime().getCurrentContext();
         if(iter) {
             while (beg >= 0) {
                 match = (RubyMatchData) tc.getBackref();
                 sbuf.append(this.value,offset,beg-offset);
                 newStr = block.yield(tc, match.group(0));
                 taint |= newStr.isTaint();
-                sbuf.append(newStr.objAsString().getByteList());
+                sbuf.append(newStr.asString().getByteList());
                 offset = match.matchEndPosition();
                 beg = pat.search(str, offset == beg ? beg + 1 : offset);
             }
         } else {
             RubyString r = stringValue(repl);
             while (beg >= 0) {
                 match = (RubyMatchData) tc.getBackref();
                 sbuf.append(this.value,offset,beg-offset);
                 pat.regsub(r, match, sbuf);
                 offset = match.matchEndPosition();
                 beg = pat.search(str, offset == beg ? beg + 1 : offset);
             }
         }
 
         sbuf.append(this.value,offset,this.value.length()-offset);
 
         if (bang) {
             setTaint(isTaint() || taint);
             setValue(sbuf);
             return this;
         }
         RubyString result = newString(sbuf);
         result.setTaint(isTaint() || taint);
         return result;
     }
 
     /** rb_str_index_m
      *
      */
     public IRubyObject index(IRubyObject[] args) {
         return index(args, false);
     }
 
     /** rb_str_rindex_m
      *
      */
     public IRubyObject rindex(IRubyObject[] args) {
         return index(args, true);
     }
 
     /**
      *	@fixme may be a problem with pos when doing reverse searches
      */
     private IRubyObject index(IRubyObject[] args, boolean reverse) {
         //FIXME may be a problem with pos when doing reverse searches
         int pos = 0;
         if (reverse) {
             pos = value.length();
         }
         if (checkArgumentCount(args, 1, 2) == 2) {
             pos = RubyNumeric.fix2int(args[1]);
         }
         if (pos < 0) {
             pos += value.length();
             if (pos < 0) {
                 return getRuntime().getNil();
             }
         }
         if (args[0] instanceof RubyRegexp) {
             int doNotLookPastIfReverse = pos;
 
             // RubyRegexp doesn't (yet?) support reverse searches, so we
             // find all matches and use the last one--very inefficient.
             // XXX - find a better way
             pos = ((RubyRegexp) args[0]).search(toString(), reverse ? 0 : pos);
 
             int dummy = pos;
             while (reverse && dummy > -1 && dummy <= doNotLookPastIfReverse) {
                 pos = dummy;
                 dummy = ((RubyRegexp) args[0]).search(toString(), pos + 1);
             }
         } else if (args[0] instanceof RubyString) {
             ByteList sub = ((RubyString) args[0]).value;
             ByteList sb = (ByteList)value.clone();
             pos = reverse ? sb.lastIndexOf(sub, pos) : sb.indexOf(sub, pos);
         } else if (args[0] instanceof RubyFixnum) {
             char c = (char) ((RubyFixnum) args[0]).getLongValue();
             pos = reverse ? value.lastIndexOf(c, pos) : value.indexOf(c, pos);
         } else {
             throw getRuntime().newArgumentError("wrong type of argument");
         }
 
         return pos == -1 ? getRuntime().getNil() : getRuntime().newFixnum(pos);
     }
 
     /* rb_str_substr */
     public IRubyObject substr(int beg, int len) {
         int length = value.length();
         if (len < 0 || beg > length) {
             return getRuntime().getNil();
         }
         if (beg < 0) {
             beg += length;
             if (beg < 0) {
                 return getRuntime().getNil();
             }
         }
         int end = Math.min(length, beg + len);
         ByteList newValue = new ByteList(value, beg, end - beg);
         return newString(getRuntime(), newValue).infectBy(this);
     }
 
     /* rb_str_replace */
     public IRubyObject replace(int beg, int len, RubyString replaceWith) {
         if (beg + len >= value.length()) {
             len = value.length() - beg;
         }
 
         value.unsafeReplace(beg,len,replaceWith.value);
         stringMutated();
         return infectBy(replaceWith);
     }
 
     /** rb_str_aref, rb_str_aref_m
      *
      */
     public IRubyObject aref(IRubyObject[] args) {
         if (checkArgumentCount(args, 1, 2) == 2) {
             if (args[0] instanceof RubyRegexp) {
                 IRubyObject match = RubyRegexp.regexpValue(args[0]).match(toString(), 0);
                 long idx = args[1].convertToInteger().getLongValue();
                 getRuntime().getCurrentContext().setBackref(match);
                 return RubyRegexp.nth_match((int) idx, match);
             }
             return substr(RubyNumeric.fix2int(args[0]), RubyNumeric.fix2int(args[1]));
         }
 
         if (args[0] instanceof RubyRegexp) {
             return RubyRegexp.regexpValue(args[0]).search(toString(), 0) >= 0 ?
                 RubyRegexp.last_match(getRuntime().getCurrentContext().getBackref()) :
                 getRuntime().getNil();
         } else if (args[0] instanceof RubyString) {
             return toString().indexOf(stringValue(args[0]).toString()) != -1 ?
                 args[0] : getRuntime().getNil();
         } else if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).getBeginLength(value.length(), true, false);
             return begLen == null ? getRuntime().getNil() :
                 substr((int) begLen[0], (int) begLen[1]);
         }
         int idx = (int) args[0].convertToInteger().getLongValue();
         if (idx < 0) {
             idx += value.length();
         }
         if (idx < 0 || idx >= value.length()) {
             return getRuntime().getNil();
         } else {
             RubyFixnum result = getRuntime().newFixnum(value.get(idx) & 0xFF);
             return result;
         }
     }
 
     /**
      * rb_str_subpat_set
      *
      */
     private void subpatSet(RubyRegexp regexp, int nth, IRubyObject repl) {
         int found = regexp.search(this.toString(), 0);
         if (found == -1) {
             throw getRuntime().newIndexError("regexp not matched");
         }
 
         RubyMatchData match = (RubyMatchData) getRuntime().getCurrentContext()
                 .getBackref();
 
         if (nth >= match.getSize()) {
             throw getRuntime().newIndexError("index " + nth + " out of regexp");
         }
         if (nth < 0) {
             if (-nth >= match.getSize()) {
                 throw getRuntime().newIndexError("index " + nth + " out of regexp");
             }
             nth += match.getSize();
         }
 
         IRubyObject group = match.group(nth);
         if (getRuntime().getNil().equals(group)) {
             throw getRuntime().newIndexError(
                     "regexp group " + nth + " not matched");
         }
 
         int beg = (int) match.begin(nth);
         int len = (int) (match.end(nth) - beg);
 
         replace(beg, len, stringValue(repl));
 
     }
 
     /** rb_str_aset, rb_str_aset_m
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         testFrozen("class");
         int strLen = value.length();
         if (checkArgumentCount(args, 2, 3) == 3) {
             if (args[0] instanceof RubyFixnum) {
                 RubyString repl = stringValue(args[2]);
                 int beg = RubyNumeric.fix2int(args[0]);
                 int len = RubyNumeric.fix2int(args[1]);
                 if (len < 0) {
                     throw getRuntime().newIndexError("negative length");
                 }
                 if (beg < 0) {
                     beg += strLen;
                 }
                 if (beg < 0 || (beg > 0 && beg >= strLen)) {
                     throw getRuntime().newIndexError(
                             "string index out of bounds");
                 }
                 if (beg + len > strLen) {
                     len = strLen - beg;
                 }
                 replace(beg, len, repl);
                 return repl;
             }
             if (args[0] instanceof RubyRegexp) {
                 RubyString repl = stringValue(args[2]);
                 int nth = RubyNumeric.fix2int(args[1]);
                 subpatSet((RubyRegexp) args[0], nth, repl);
                 return repl;
             }
         }
         if (args[0] instanceof RubyFixnum) { // RubyNumeric?
             int idx = RubyNumeric.fix2int(args[0]); // num2int?
             if (idx < 0) {
                 idx += value.length();
             }
             if (idx < 0 || idx >= value.length()) {
                 throw getRuntime().newIndexError("string index out of bounds");
             }
             if (args[1] instanceof RubyFixnum) {
                 value.set(idx, (byte) RubyNumeric.fix2int(args[1]));
                 stringMutated();
             } else {
                 replace(idx, 1, stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRegexp) {
             sub_bang(args, null);
             return args[1];
         }
         if (args[0] instanceof RubyString) {
             RubyString orig = stringValue(args[0]);
             int beg = toString().indexOf(orig.toString());
             if (beg != -1) {
                 replace(beg, orig.value.length(), stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] idxs = ((RubyRange) args[0]).getBeginLength(value.length(), true, true);
             replace((int) idxs[0], (int) idxs[1], stringValue(args[1]));
             return args[1];
         }
         throw getRuntime().newTypeError("wrong argument type");
     }
 
     /** rb_str_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 1, 2);
         IRubyObject[] newArgs = new IRubyObject[argc + 1];
         newArgs[0] = args[0];
         if (argc > 1) {
             newArgs[1] = args[1];
         }
         newArgs[argc] = newString("");
         IRubyObject result = aref(args);
         if (result.isNil()) {
             return result;
         }
         aset(newArgs);
         return result;
     }
 
     public IRubyObject succ() {
         return ((RubyString) dup()).succ_bang();
     }
 
     public IRubyObject succ_bang() {
         if (value.length() == 0) {
             return this;
         }
 
         boolean alnumSeen = false;
         int pos = -1;
         int c = 0;
         int n = 0;
         for (int i = value.length() - 1; i >= 0; i--) {
             c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 alnumSeen = true;
                 if ((isDigit(c) && c < '9') || (isLower(c) && c < 'z') || (isUpper(c) && c < 'Z')) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = isDigit(c) ? '1' : (isLower(c) ? 'a' : 'A');
                 value.set(i, (byte)(isDigit(c) ? '0' : (isLower(c) ? 'a' : 'A')));
             }
         }
         if (!alnumSeen) {
             for (int i = value.length() - 1; i >= 0; i--) {
                 c = value.get(i) & 0xFF;
                 if (c < 0xff) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = '\u0001';
                 value.set(i, 0);
             }
         }
         if (pos > -1) {
             // This represents left most digit in a set of incremented
             // values?  Therefore leftmost numeric must be '1' and not '0'
             // 999 -> 1000, not 999 -> 0000.  whereas chars should be
             // zzz -> aaaa and non-alnum byte values should be "\377" -> "\001\000"
             value.prepend((byte)n);
         }
         stringMutated();
         return this;
     }
 
     /** rb_str_upto_m
      *
      */
     public IRubyObject upto(IRubyObject str, Block block) {
         return upto(str, false, block);
     }
 
     /* rb_str_upto */
     public IRubyObject upto(IRubyObject str, boolean excl, Block block) {
         // alias 'this' to 'beg' for ease of comparison with MRI
         RubyString beg = this;
         RubyString end = stringValue(str);
 
         int n = beg.cmp(end);
         if (n > 0 || (excl && n == 0)) {
             return beg;
         }
 
         RubyString afterEnd = stringValue(end.succ());
         RubyString current = beg;
 
         ThreadContext context = getRuntime().getCurrentContext();
         while (!current.equals(afterEnd)) {
             block.yield(context, current);
             if (!excl && current.equals(end)) {
                 break;
             }
 
             current = (RubyString) current.succ();
             if (excl && current.equals(end)) {
                 break;
             }
             if (current.length().getLongValue() > end.length().getLongValue()) {
                 break;
             }
         }
 
         return beg;
 
     }
 
 
     /** rb_str_include
      *
      */
     public RubyBoolean include(IRubyObject obj) {
         if (obj instanceof RubyFixnum) {
             int c = RubyNumeric.fix2int(obj);
             for (int i = 0; i < value.length(); i++) {
                 if (value.get(i) == (byte)c) {
                     return getRuntime().getTrue();
                 }
             }
             return getRuntime().getFalse();
         }
         ByteList str = stringValue(obj).value;
         return getRuntime().newBoolean(value.indexOf(str) != -1);
     }
 
     /** rb_str_to_i
      *
      */
     public IRubyObject to_i(IRubyObject[] args) {
-        long base = checkArgumentCount(args, 0, 1) == 0 ? 10 : ((RubyInteger) args[0].convertType(RubyInteger.class,
-                "Integer", "to_i")).getLongValue();
+        long base = checkArgumentCount(args, 0, 1) == 0 ? 10 : args[0].convertToInteger().getLongValue();
         return RubyNumeric.str2inum(getRuntime(), this, (int) base);
     }
 
     /** rb_str_oct
      *
      */
     public IRubyObject oct() {
         if (isEmpty()) {
             return getRuntime().newFixnum(0);
         }
 
         int base = 8;
         String str = toString().trim();
         int pos = (str.charAt(0) == '-' || str.charAt(0) == '+') ? 1 : 0;
         if (str.indexOf("0x") == pos || str.indexOf("0X") == pos) {
             base = 16;
         } else if (str.indexOf("0b") == pos || str.indexOf("0B") == pos) {
             base = 2;
         }
         return RubyNumeric.str2inum(getRuntime(), this, base);
     }
 
     /** rb_str_hex
      *
      */
     public IRubyObject hex() {
         return RubyNumeric.str2inum(getRuntime(), this, 16);
     }
 
     /** rb_str_to_f
      *
      */
     public IRubyObject to_f() {
         return RubyNumeric.str2fnum(getRuntime(), this);
     }
 
     /** rb_str_split
      *
      */
     public RubyArray split(IRubyObject[] args) {
         RubyRegexp pattern;
         Ruby runtime = getRuntime();
         boolean isWhitespace = false;
 
         // get the pattern based on args
         if (args.length == 0 || args[0].isNil()) {
             isWhitespace = true;
             IRubyObject defaultPattern = runtime.getGlobalVariables().get("$;");
             
             if (defaultPattern.isNil()) {
                 pattern = RubyRegexp.newRegexp(runtime, "\\s+", 0, null);
             } else {
                 // FIXME: Is toString correct here?
                 pattern = RubyRegexp.newRegexp(runtime, defaultPattern.toString(), 0, null);
             }
         } else if (args[0] instanceof RubyRegexp) {
             // Even if we have whitespace-only explicit regexp we do not
             // mark it as whitespace.  Apparently, this is so ruby can
             // still get the do not ignore the front match behavior.
             pattern = RubyRegexp.regexpValue(args[0]);
         } else {
             String stringPattern = RubyString.stringValue(args[0]).toString();
 
             if (stringPattern.equals(" ")) {
                 isWhitespace = true;
                 pattern = RubyRegexp.newRegexp(getRuntime(), "\\s+", 0, null);
             } else {
                 pattern = RubyRegexp.newRegexp(getRuntime(), RubyRegexp.escapeSpecialChars(stringPattern), 0, null);
             }
         }
 
         int limit = getLimit(args);
         String[] result = null;
         // attempt to convert to Unicode when appropriate
         String splitee = toString();
         boolean unicodeSuccess = false;
         if (getRuntime().getKCode() == KCode.UTF8) {
             // We're in UTF8 mode; try to convert the string to UTF8, but fall back on raw bytes if we can't decode
             // TODO: all this decoder and charset stuff could be centralized...in KCode perhaps?
             CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
             decoder.onMalformedInput(CodingErrorAction.REPORT);
             decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
 
             try {
                 splitee = decoder.decode(ByteBuffer.wrap(value.bytes())).toString();
                 unicodeSuccess = true;
             } catch (CharacterCodingException cce) {
                 // ignore, just use the unencoded string
             }
         }
 
 
         if (limit == 1) {
             if (splitee.length() == 0) {
                 return runtime.newArray();
             } else {
                 return runtime.newArray(this);
             }
         } else {
             List list = new ArrayList();
             int numberOfHits = 0;
             int stringLength = splitee.length();
 
             Pattern pat = pattern.getPattern();
             Matcher matt = pat.matcher(splitee);
 
             int startOfCurrentHit = 0;
             int endOfCurrentHit = 0;
             String group = null;
 
             // TODO: There's a fast path in here somewhere that could just use Pattern.split
 
             if (matt.find()) {
                 // we have matches, proceed
 
                 // end of current hit is start of first match
                 endOfCurrentHit = matt.start();
 
                 // filter out starting whitespace matches for non-regex whitespace splits
                 if (endOfCurrentHit != 0 || !isWhitespace) {
                     // not a non-regex whitespace split, proceed
 
                     numberOfHits++;
 
                     // skip first positive lookahead match
                     if (matt.end() != 0) {
 
                         // add the first hit
                         list.add(splitee.substring(startOfCurrentHit, endOfCurrentHit));
 
                         // add any matched groups found while splitting the first hit
                         for (int groupIndex = 1; groupIndex < matt.groupCount(); groupIndex++) {
                             group = matt.group(groupIndex);
                             if (group == null) continue;
                             
                             list.add(group);
                         }
                     }
                 }
 
                 // advance start to the end of the current hit
                 startOfCurrentHit = matt.end();
 
                 // ensure we haven't exceeded the hit limit
                 if (numberOfHits + 1 != limit) {
                     // loop over the remaining matches
                     while (matt.find()) {
                         // end of current hit is start of the next match
                         endOfCurrentHit = matt.start();
                         numberOfHits++;
 
                         // add the current hit
                         list.add(splitee.substring(startOfCurrentHit, endOfCurrentHit));
 
                         // add any matched groups found while splitting the current hit
                         for (int groupIndex = 1; groupIndex < matt.groupCount(); groupIndex++) {
                             group = matt.group(groupIndex);
                             if (group == null) continue;
                             
                             list.add(group);
                         }
 
                         // advance start to the end of the current hit
                         startOfCurrentHit = matt.end();
                     }
                 }
             }
 
             if (numberOfHits == 0) {
                 // we found no hits, use the entire string
                 list.add(splitee);
             } else if (startOfCurrentHit <= stringLength) {
                 // our last match ended before the end of the string, add remainder
                 list.add(splitee.substring(startOfCurrentHit, stringLength));
             }
 
             // Remove trailing whitespace when limit 0 is specified
             if (limit == 0 && list.size() > 0) {
                 for (int size = list.size() - 1;
                         size >= 0 && ((String) list.get(size)).length() == 0;
                         size--) {
                     list.remove(size);
                 }
             }
 
             result = (String[])list.toArray(new String[list.size()]);
         }
 
         // convert arraylist of strings to RubyArray of RubyStrings
         RubyArray resultArray = getRuntime().newArray(result.length);
 
         for (int i = 0; i < result.length; i++) {
             RubyString string = RubyString.newString(runtime, getMetaClass(), result[i]);
 
             // if we're in unicode mode and successfully converted to a unicode string before,
             // make sure to keep unicode in the split values
             if (unicodeSuccess && getRuntime().getKCode() == KCode.UTF8) {
                 string.setUnicodeValue(result[i]);
             }
 
             resultArray.append(string);
         }
 
         return resultArray;
     }
 
     private static int getLimit(IRubyObject[] args) {
         if (args.length == 2) {
             return RubyNumeric.fix2int(args[1]);
         }
         return 0;
     }
 
     /** rb_str_scan
      *
      */
     public IRubyObject scan(IRubyObject arg, Block block) {
         RubyRegexp pattern = RubyRegexp.regexpValue(arg);
         int start = 0;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         // Move toString() call outside loop.
         String toString = toString();
 
         if (!block.isGiven()) {
             RubyArray ary = getRuntime().newArray();
             while (pattern.search(toString, start) != -1) {
                 RubyMatchData md = (RubyMatchData) tc.getBackref();
 
                 ary.append(md.getSize() == 1 ? md.group(0) : md.subseq(1, md.getSize()));
 
                 if (md.matchEndPosition() == md.matchStartPosition()) {
                     start++;
                 } else {
                     start = md.matchEndPosition();
                 }
 
             }
             return ary;
         }
 
         while (pattern.search(toString, start) != -1) {
             RubyMatchData md = (RubyMatchData) tc.getBackref();
 
             block.yield(tc, md.getSize() == 1 ? md.group(0) : md.subseq(1, md.getSize()));
 
             if (md.matchEndPosition() == md.matchStartPosition()) {
                 start++;
             } else {
                 start = md.matchEndPosition();
             }
 
         }
         return this;
     }
     
     private static ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
 
     private IRubyObject justify(IRubyObject [] args, boolean leftJustify) {
         checkArgumentCount(args, 1, 2);
 
         ByteList paddingArg;
 
         if (args.length == 2) {
             paddingArg = args[1].convertToString().value;
             if (paddingArg.length() == 0) {
                 throw getRuntime().newArgumentError("zero width padding");
             }
         } else {
             paddingArg = SPACE_BYTELIST;
         }
         
         int length = RubyNumeric.fix2int(args[0]);
         if (length <= value.length()) {
             return dup();
         }
 
         ByteList sbuf = new ByteList(length);
         ByteList thisStr = value;
 
         if (leftJustify) {
             sbuf.append(thisStr);
         }
 
         // Add n whole paddings
         int whole = (length - thisStr.length()) / paddingArg.length();
         for (int w = 0; w < whole; w++ ) {
             sbuf.append(paddingArg);
         }
 
         // Add fractional amount of padding to make up difference
         int fractionalLength = (length - thisStr.length()) % paddingArg.length();
         if (fractionalLength > 0) {
             sbuf.append((ByteList)paddingArg.subSequence(0, fractionalLength));
         }
 
         if (!leftJustify) {
             sbuf.append(thisStr);
         }
 
         RubyString ret = newString(sbuf);
 
         if (args.length == 2) {
             ret.infectBy(args[1]);
         }
 
         return ret;
     }
 
     /** rb_str_ljust
      *
      */
     public IRubyObject ljust(IRubyObject [] args) {
         return justify(args, true);
     }
 
     /** rb_str_rjust
      *
      */
     public IRubyObject rjust(IRubyObject [] args) {
         return justify(args, false);
     }
 
     public IRubyObject center(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
         int len = RubyNumeric.fix2int(args[0]);
         ByteList pad = args.length == 2 ? args[1].convertToString().value : SPACE_BYTELIST;
         int strLen = value.length();
         int padLen = pad.length();
 
         if (padLen == 0) {
             throw getRuntime().newArgumentError("zero width padding");
         }
         if (len <= strLen) {
             return dup();
         }
         ByteList sbuf = new ByteList(len);
         int lead = (len - strLen) / 2;
         for (int i = 0; i < lead; i++) {
             sbuf.append(pad.charAt(i % padLen));
         }
         sbuf.append(value);
         int remaining = len - (lead + strLen);
         for (int i = 0; i < remaining; i++) {
             sbuf.append(pad.charAt(i % padLen));
         }
         return newString(sbuf);
     }
 
     public IRubyObject chop() {
         RubyString newString = (RubyString) dup();
 
         newString.chop_bang();
 
         return newString;
     }
 
     public IRubyObject chop_bang() {
         int end = value.length() - 1;
 
         if (end < 0) {
             return getRuntime().getNil();
         }
 
         if ((value.get(end) & 0xFF) == '\n') {
             if (end > 0 && (value.get(end-1) & 0xFF) == '\r') {
                 end--;
             }
         }
 
         value.length(end);
         stringMutated();
         return this;
     }
 
     public RubyString chomp(IRubyObject[] args) {
         RubyString result = (RubyString) dup();
 
         result.chomp_bang(args);
 
         return result;
     }
 
     /**
      * rb_str_chomp_bang
      *
      * In the common case, removes CR and LF characters in various ways depending on the value of
      *   the optional args[0].
      * If args.length==0 removes one instance of CR, CRLF or LF from the end of the string.
      * If args.length>0 and args[0] is "\n" then same behaviour as args.length==0 .
      * If args.length>0 and args[0] is "" then removes trailing multiple LF or CRLF (but no CRs at
      *   all(!)).
      * @param args See method description.
      */
     public IRubyObject chomp_bang(IRubyObject[] args) {
         if (isEmpty()) {
             return getRuntime().getNil();
         }
 
         // Separator (a.k.a. $/) can be overriden by the -0 (zero) command line option
         String separator = (args.length == 0) ?
             getRuntime().getGlobalVariables().get("$/").asSymbol() : args[0].asSymbol();
 
         if (separator.equals(DEFAULT_RS)) {
             int end = value.length() - 1;
             int removeCount = 0;
 
             if (end < 0) {
                 return getRuntime().getNil();
             }
 
             if ((value.get(end) & 0xFF) == '\n') {
                 removeCount++;
                 if (end > 0 && (value.get(end-1) & 0xFF) == '\r') {
                     removeCount++;
                 }
             } else if ((value.get(end) & 0xFF) == '\r') {
                 removeCount++;
             }
 
             if (removeCount == 0) {
                 return getRuntime().getNil();
             }
 
             value.length(end - removeCount + 1);
             stringMutated();
             return this;
         }
 
         if (separator.length() == 0) {
             int end = value.length() - 1;
             int removeCount = 0;
             while(end - removeCount >= 0 && (value.get(end - removeCount) & 0xFF) == '\n') {
                 removeCount++;
                 if (end - removeCount >= 0 && (value.get(end - removeCount) & 0xFF) == '\r') {
                     removeCount++;
                 }
             }
             if (removeCount == 0) {
                 return getRuntime().getNil();
             }
 
             value.length(end - removeCount + 1);
             stringMutated();
             return this;
         }
 
         // Uncommon case of str.chomp!("xxx")
         if (toString().endsWith(separator)) {
             value.length(value.length() - separator.length());
             stringMutated();
             return this;
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject lstrip() {
         return newString(getRuntime(), lstripInternal());
     }
 
     public ByteList lstripInternal() {
         int length = value.length();
         int i = 0;
 
         for (; i < length; i++) {
             if (!Character.isWhitespace(value.charAt(i))) {
                 break;
             }
         }
 
         return new ByteList(value, i, value.length() - i);
     }
 
     public IRubyObject lstrip_bang() {
         ByteList newBytes = lstripInternal();
         if (value.equals(newBytes)) {
             return getRuntime().getNil();
         }
         value = newBytes;
         stringMutated();
 
         return this;
     }
 
     public IRubyObject rstrip() {
         return newString(getRuntime(), rstripInternal());
     }
 
     public ByteList rstripInternal() {
         int i = value.length() - 1;
 
         for (; i >= 0; i--) {
             if (!Character.isWhitespace(value.charAt(i))) {
                 break;
             }
         }
 
         return new ByteList(value, 0, i + 1);
     }
 
     public IRubyObject rstrip_bang() {
         ByteList newBytes = rstripInternal();
         if (value.equals(newBytes)) {
             return getRuntime().getNil();
         }
         value = newBytes;
         stringMutated();
 
         return this;
     }
 
     /** rb_str_strip
      *
      */
     public IRubyObject strip() {
         if (isEmpty()) {
             return dup();
         }
         ByteList bytes = stripInternal();
         if (bytes == null) {
             return newString(getRuntime(), (ByteList)value.clone());
         }
 
         return newString(getRuntime(), bytes);
     }
 
     /** rb_str_strip_bang
      *
      */
     public IRubyObject strip_bang() {
         if (isEmpty()) {
             return getRuntime().getNil();
         }
         ByteList bytes = stripInternal();
         if (bytes == null) {
             return getRuntime().getNil();
         }
         value = bytes;
         stringMutated();
         return this;
     }
 
     public ByteList stripInternal() {
         int head = 0;
         while (head < value.length() && Character.isWhitespace(value.charAt(head))) head++;
         int tail = value.length() - 1;
         while (tail > head && Character.isWhitespace(value.charAt(tail))) tail--;
 
         if (head == 0 && tail == value.length() - 1) {
             return null;
         }
 
         if (head <= tail) {
             return new ByteList(value, head, tail - head + 1);
         }
         
         if (head > tail) {
             return new ByteList();
         }
 
         return null;
     }
 
     private static ByteList expandTemplate(ByteList spec, boolean invertOK) {
         int len = spec.length();
         if (len <= 1) {
             return spec;
         }
         ByteList sbuf = new ByteList();
         int pos = (invertOK && spec.charAt(0) == '^') ? 1 : 0;
         while (pos < len) {
             char c1 = spec.charAt(pos), c2;
             if (pos + 2 < len && spec.charAt(pos + 1) == '-') {
                 if ((c2 = spec.charAt(pos + 2)) > c1) {
                     for (int i = c1; i <= c2; i++) {
                         sbuf.append((char) i);
                     }
                 }
                 pos += 3;
                 continue;
             }
             sbuf.append(c1);
             pos++;
         }
         return sbuf;
     }
 
     private ByteList setupTable(ByteList[] specs) {
         int[] table = new int[256];
         int numSets = 0;
         for (int i = 0; i < specs.length; i++) {
             ByteList template = expandTemplate(specs[i], true);
             boolean invert = specs[i].length() > 1 && specs[i].charAt(0) == '^';
             for (int j = 0; j < 256; j++) {
                 if (template.indexOf(j) != -1) {
                     table[j] += invert ? -1 : 1;
                 }
             }
             numSets += invert ? 0 : 1;
         }
         ByteList sbuf = new ByteList();
         for (int k = 0; k < 256; k++) {
             if (table[k] == numSets) {
                 sbuf.append((char) k);
             }
         }
         return sbuf;
     }
 
     /** rb_str_count
      *
      */
     public IRubyObject count(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 1, -1);
         ByteList[] specs = new ByteList[argc];
         for (int i = 0; i < argc; i++) {
             specs[i] = stringValue(args[i]).value;
         }
         ByteList table = setupTable(specs);
 
         int count = 0;
         for (int j = 0; j < value.length(); j++) {
             if (table.indexOf(value.get(j) & 0xFF) != -1) {
                 count++;
             }
         }
         return getRuntime().newFixnum(count);
     }
 
     private ByteList getDelete(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 1, -1);
         ByteList[] specs = new ByteList[argc];
         for (int i = 0; i < argc; i++) {
             specs[i] = stringValue(args[i]).value;
         }
         ByteList table = setupTable(specs);
 
         int strLen = value.length();
         ByteList sbuf = new ByteList(strLen);
         int c;
         for (int j = 0; j < strLen; j++) {
             c = value.get(j) & 0xFF;
             if (table.indexOf(c) == -1) {
                 sbuf.append((char)c);
             }
         }
         return sbuf;
     }
 
     /** rb_str_delete
      *
      */
     public IRubyObject delete(IRubyObject[] args) {
         return newString(getRuntime(), getDelete(args)).infectBy(this);
     }
 
     /** rb_str_delete_bang
      *
      */
     public IRubyObject delete_bang(IRubyObject[] args) {
         ByteList newStr = getDelete(args);
         if (value.equals(newStr)) {
             return getRuntime().getNil();
         }
         value = newStr;
         stringMutated();
         return this;
     }
 
     private ByteList getSqueeze(IRubyObject[] args) {
         int argc = args.length;
         ByteList[] specs = null;
         if (argc > 0) {
             specs = new ByteList[argc];
             for (int i = 0; i < argc; i++) {
                 specs[i] = stringValue(args[i]).value;
             }
         }
         ByteList table = specs == null ? null : setupTable(specs);
 
         int strLen = value.length();
         if (strLen <= 1) {
             return value;
         }
         ByteList sbuf = new ByteList(strLen);
         int c1 = value.get(0) & 0xFF;
         sbuf.append((char)c1);
         int c2;
         for (int j = 1; j < strLen; j++) {
             c2 = value.get(j) & 0xFF;
             if (c2 == c1 && (table == null || table.indexOf(c2) != -1)) {
                 continue;
             }
             sbuf.append((char)c2);
             c1 = c2;
         }
         return sbuf;
     }
 
     /** rb_str_squeeze
      *
      */
     public IRubyObject squeeze(IRubyObject[] args) {
         return newString(getRuntime(), getSqueeze(args)).infectBy(this);
     }
 
     /** rb_str_squeeze_bang
      *
      */
     public IRubyObject squeeze_bang(IRubyObject[] args) {
         ByteList newStr = getSqueeze(args);
         if (value.equals(newStr)) {
             return getRuntime().getNil();
         }
         value = newStr;
         stringMutated();
         return this;
     }
 
     private ByteList tr(IRubyObject search, IRubyObject replace, boolean squeeze) {
         ByteList srchSpec = search.convertToString().value;
         ByteList srch = expandTemplate(srchSpec, true);
         if (srchSpec.charAt(0) == '^') {
             ByteList sbuf = new ByteList(256);
             for (int i = 0; i < 256; i++) {
                 char c = (char) i;
                 if (srch.indexOf(c) == -1) {
                     sbuf.append(c);
                 }
             }
             srch = sbuf;
         }
         ByteList repl = expandTemplate(replace.convertToString().value, false);
 
         int strLen = value.length();
         if (strLen == 0 || srch.length() == 0) {
             return value;
         }
         int repLen = repl.length();
         ByteList sbuf = new ByteList(strLen);
         int last = -1;
         for (int i = 0; i < strLen; i++) {
             int cs = value.get(i) & 0xFF;
             int pos = srch.indexOf(cs);
             if (pos == -1) {
                 sbuf.append((char)cs);
                 last = -1;
             } else if (repLen > 0) {
                 char cr = repl.charAt(Math.min(pos, repLen - 1));
                 if (squeeze && cr == last) {
                     continue;
                 }
                 sbuf.append((char)cr);
                 last = cr;
             }
         }
         return sbuf;
     }
 
     /** rb_str_tr
      *
      */
     public IRubyObject tr(IRubyObject search, IRubyObject replace) {
         return newString(getRuntime(), tr(search, replace, false)).infectBy(this);
     }
 
     /** rb_str_tr_bang
      *
      */
     public IRubyObject tr_bang(IRubyObject search, IRubyObject replace) {
         ByteList newStr = tr(search, replace, false);
         if (value.equals(newStr)) {
             return getRuntime().getNil();
         }
         value = newStr;
         stringMutated();
         return this;
     }
 
     /** rb_str_tr_s
      *
      */
     public IRubyObject tr_s(IRubyObject search, IRubyObject replace) {
         return newString(getRuntime(), tr(search, replace, true)).infectBy(this);
     }
 
     /** rb_str_tr_s_bang
      *
      */
     public IRubyObject tr_s_bang(IRubyObject search, IRubyObject replace) {
         ByteList newStr = tr(search, replace, true);
         if (value.equals(newStr)) {
             return getRuntime().getNil();
         }
         value = newStr;
         stringMutated();
         return this;
     }
 
     /** rb_str_each_line
      *
      */
     public IRubyObject each_line(IRubyObject[] args, Block block) {
         int strLen = value.length();
         if (strLen == 0) {
             return this;
         }
         String sep;
         if (checkArgumentCount(args, 0, 1) == 1) {
             sep = RubyRegexp.escapeSpecialChars(stringValue(args[0]).toString());
         } else {
             sep = RubyRegexp.escapeSpecialChars(getRuntime().getGlobalVariables().get("$/").asSymbol());
         }
         if (sep == null) {
             sep = "(?:\\n|\\r\\n?)";
         } else if (sep.length() == 0) {
             sep = "(?:\\n|\\r\\n?){2,}";
         }
         RubyRegexp pat = RubyRegexp.newRegexp(getRuntime(), ".*?" + sep, RubyRegexp.RE_OPTION_MULTILINE, null);
         int start = 0;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         // Move toString() call outside loop.
         String toString = toString();
 
         if (pat.search(toString, start) != -1) {
             RubyMatchData md = (RubyMatchData) tc.getBackref();
             
             block.yield(tc, md.group(0));
             start = md.end(0);
             while (md.find()) {
                 block.yield(tc, md.group(0));
                 start = md.end(0);
             }
         }
         if (start < strLen) {
             block.yield(tc, substr(start, strLen - start));
         }
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     public RubyString each_byte(Block block) {
         int lLength = value.length();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < lLength; i++) {
             block.yield(context, getRuntime().newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     public RubySymbol intern() {
         String s = toString();
         if (s.equals("")) {
             throw getRuntime().newArgumentError("interning empty string");
         }
         if (s.indexOf('\0') >= 0) {
             throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return RubySymbol.newSymbol(getRuntime(), toString());
     }
 
     public RubySymbol to_sym() {
         return intern();
     }
 
     public RubyInteger sum(IRubyObject[] args) {
         long bitSize = 16;
         if (args.length > 0) {
-            bitSize = ((RubyInteger) args[0].convertType(RubyInteger.class,
-                    "Integer", "to_i")).getLongValue();
+            bitSize = ((RubyInteger) args[0].convertToInteger()).getLongValue();
         }
 
         long result = 0;
         for (int i = 0; i < value.length(); i++) {
             result += value.get(i) & 0xFF;
         }
         return getRuntime().newFixnum(bitSize == 0 ? result : result % (long) Math.pow(2, bitSize));
     }
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      */
     public void setValue(CharSequence value) {
         this.value.replace(ByteList.plain(value));
         stringMutated();
     }
 
     public void setValue(ByteList value) {
         this.value = value;
         stringMutated();
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public String getUnicodeValue() {
         try {
             return new String(value.bytes,0,value.realSize, "UTF8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     public void setUnicodeValue(String newValue) {
         try {
             value.replace(newValue.getBytes("UTF8"));
             stringMutated();
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 }
diff --git a/src/org/jruby/compiler/Compiler.java b/src/org/jruby/compiler/Compiler.java
index 3431008b2f..06858845f0 100644
--- a/src/org/jruby/compiler/Compiler.java
+++ b/src/org/jruby/compiler/Compiler.java
@@ -1,443 +1,443 @@
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
 
 package org.jruby.compiler;
 
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.CallType;
 import org.jruby.util.ByteList;
 
 /**
  * Compiler represents the current state of a compiler and all appropriate
  * transitions and modifications that can be made within it. The methods here begin
  * and end a class for a given compile run, begin and end methods for the script being
  * compiled, set line number information, and generate code for all the basic
  * operations necessary for a script to run.
  * 
  * The intent of this interface is to provide a library-neutral set of functions for
  * compiling a given script using any backend or any output format.
  */
 public interface Compiler {
     /**
      * Begin compilation for a script, preparing all necessary context and code
      * to support this script's compiled representation.
      */
     public void startScript();
     
     /**
      * End compilation for the current script, closing all context and structures
      * used for the compilation.
      */
     public void endScript();
     
     /**
      * Begin compilation for a method that has the specified number of local variables.
      * The returned value is a token that can be used to end the method later.
      * 
      * @param friendlyName The outward user-readable name of the method. A unique name will be generated based on this.
      * @param arity The arity of the method's argument list
      * @param localVarCount The number of local variables that will be used by the method.
      * @return An Object that represents the method within this compiler. Used in calls to
      * endMethod once compilation for this method is completed.
      */
     public Object beginMethod(String friendlyName, int arity, int localVarCount);
     
     /**
      * End compilation for the method associated with the specified token. This should
      * close out all structures created for compilation of the method.
      * 
      * @param token A token identifying the method to be terminated.
      */
     public void endMethod(Object token);
     
     /**
      * As code executes, values are assumed to be "generated", often by being pushed
      * on to some execution stack. Generally, these values are consumed by other
      * methods on the context, but occasionally a value must be "thrown out". This method
      * provides a way to discard the previous value generated by some other call(s).
      */
     public void consumeCurrentValue();
     
     /**
      * Push a copy the topmost value on the stack.
      */
     public void duplicateCurrentValue();
     
     /**
      * Swap the top and second values on the stack.
      */
     public void swapValues();
     
     /**
      * This method provides a way to specify a line number for the current piece of code
      * being compiled. The compiler may use this information to create debugging
      * information in a bytecode-format-dependent way.
      * 
      * @param position The ISourcePosition information to use.
      */
     public void lineNumber(ISourcePosition position);
     
     /**
      * Invoke the named method as a "function", i.e. as a method on the current "self"
      * object, using the specified argument count. It is expected that previous calls
      * to the compiler has prepared the exact number of argument values necessary for this
      * call. Those values will be consumed, and the result of the call will be generated.
      */
     public void invokeDynamic(String name, boolean hasReceiver, boolean hasArgs, CallType callType, ClosureCallback closureArg);
     
     /**
      * Attr assign calls have slightly different semantics that normal calls, so this method handles those additional semantics.
      */
     public void invokeAttrAssign(String name);
     
     /**
      * Invoke the block passed into this method, or throw an error if no block is present.
      * If arguments have been prepared for the block, specify true. Otherwise the default
      * empty args will be used.
      */
     public void yield(boolean hasArgs);
     
     /**
      * Assigns the value on top of the stack to a local variable at the specified index, consuming
      * that value in the process. This assumes a lexical scoping depth of 0.
      * 
      * @param index The index of the local variable to which to assign the value.
      */
     public void assignLocalVariable(int index);
     
     /**
      * Retrieve the local variable at the specified index to the top of the stack, using whatever local variable store is appropriate.
      * This assumes the local variable in question should be present at the current lexical scoping depth (0).
      * 
      * @param index The index of the local variable to retrieve
      */
     public void retrieveLocalVariable(int index);
     
     /**
      * Assign the value on top of the stack to a local variable at the specified index and
      * lexical scoping depth (0 = current scope), consuming that value in the process.
      * 
      * @param index The index in which to store the local variable
      * @param depth The lexical scoping depth in which to store the variable
      */
     public void assignLocalVariable(int index, int depth);
     
     /**
      * Retrieve the local variable as the specified index and lexical scoping depth to the top of the stack,
      * using whatever local variable store is appropriate.
      * 
      * @param index The index of the local variable to retrieve
      * @param depth The lexical scoping depth from which to retrieve the variable
      */
     public void retrieveLocalVariable(int index, int depth);
     
     /**
      * Retrieve the current "self" and put a reference on top of the stack.
      */
     public void retrieveSelf();
     
     /**
      * Retrieve the current "self" object's metaclass and put a reference on top of the stack
      */
     public void retrieveSelfClass();
     
     /**
      * Generate a new "Fixnum" value.
      */
     public void createNewFixnum(long value);
 
     /**
      * Generate a new "Float" value.
      */
     public void createNewFloat(double value);
 
     /**
      * Generate a new "Bignum" value.
      */
     public void createNewBignum(java.math.BigInteger value);
     
     /**
      * Generate a new "String" value.
      */
     public void createNewString(ByteList value);
 
     /**
      * Generate a new dynamic "String" value.
      */
     public void createNewString(ArrayCallback callback, int count);
 
     /**
      * Generate a new "Symbol" value (or fetch the existing one).
      */
     public void createNewSymbol(String name);
     
     /**
      * Combine the top <pre>elementCount</pre> elements into a single element, generally
      * an array or similar construct. The specified number of elements are consumed and
      * an aggregate element remains.
      * 
      * @param elementCount The number of elements to consume
      */
     public void createObjectArray(Object[] elementArray, ArrayCallback callback);
 
     /**
      * Given an aggregated set of objects (likely created through a call to createObjectArray)
      * create a Ruby array object.
      */
     public void createNewArray();
 
     /**
      * Create an empty Ruby array
      */
     public void createEmptyArray();
     
     /**
      * Create an empty Ruby Hash object and put a reference on top of the stack.
      */
     public void createEmptyHash();
     
     /**
      * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
      * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
      * collection in and dealing with the sequential indices passed to the callback.
      * 
      * @param elements An object holding the elements from which to create the Hash.
      * @param callback An ArrayCallback implementation to which the elements array and iteration counts
      * are passed in sequence.
      * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
      */
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount);
     
     /**
      * Create a new range. It is expected that the stack will contain the end and begin values for the range as
      * its topmost and second topmost elements.
      * 
      * @param isExclusive Whether the range is exclusive or not (inclusive)
      */
     public void createNewRange(boolean isExclusive);
     
     /**
      * Perform a boolean branch operation based on the Ruby "true" value of the top value
      * on the stack. If Ruby "true", invoke the true branch callback. Otherwise, invoke the false branch callback.
      * 
      * @param trueBranch The callback for generating code for the "true" condition
      * @param falseBranch The callback for generating code for the "false" condition
      */
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a logical short-circuited Ruby "and" operation, using Ruby notions of true and false.
      * If the value on top of the stack is false, it remains and the branch is not executed. If it is true,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "and" operation does not short-circuit.
      */
     public void performLogicalAnd(BranchCallback longBranch);
     
     
     /**
      * Perform a logical short-circuited Ruby "or" operation, using Ruby notions of true and false.
      * If the value on top of the stack is true, it remains and the branch is not executed. If it is false,
      * the top of the stack is replaced with the result of the branch.
      * 
      * @param longBranch The branch to execute if the "or" operation does not short-circuit.
      */
     public void performLogicalOr(BranchCallback longBranch);
     
     /**
      * Perform a boolean loop using the given condition-calculating branch and body branch. For
      * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
      * unless loops, reverse the result of the condition after calculating it.
      * 
      * @param condition The code to execute for calculating the loop condition. A Ruby true result will
      * cause the body to be executed again.
      * @param body The body to executed for the loop.
      * @param checkFirst whether to check the condition the first time through or not.
      */
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
     
     /**
      * Create a new closure (block) using the given lexical scope information, call arity, and
      * body generated by the body callback. The closure will capture containing scopes and related information.
      * 
      * @param scope The static scoping information
      * @param arity The arity of the block's argument list
      * @param body The callback which will generate the closure's body
      */
     public void createNewClosure(StaticScope scope, int arity, ClosureCallback body);
     
     /**
      * Define a new method with the given name, arity, local variable count, and body callback.
      * This will create a new compiled method and bind it to the given name at this point in
      * the program's execution.
      * 
      * @param name The name to which to bind the resulting method.
      * @param arity The arity of the method's argument list
      * @param localVarCount The number of local variables within the method
      * @param body The callback which will generate the method's body.
      */
     public void defineNewMethod(String name, int arity, int localVarCount, ClosureCallback body);
     
     /**
      * Define an alias for a new name to an existing oldName'd method.
      * 
      * @param newName The new alias to create
      * @param oldName The name of the existing method or alias
      */
     public void defineAlias(String newName, String oldName);
     
     /**
      * Retrieve the constant with the specified name available at the current point in the
      * program's execution.
      * 
      * @param name The name of the constant
      */
     public void retrieveConstant(String name);
     
     /**
      * Load a Ruby "false" value on top of the stack.
      */
     public void loadFalse();
     
     /**
      * Load a Ruby "true" value on top of the stack.
      */
     public void loadTrue();
     
     /**
      * Load a Ruby "nil" value on top of the stack.
      */
     public void loadNil();
     
     /**
      * Load the given string as a symbol on to the top of the stack.
      * 
      * @param symbol The symbol to load.
      */
     public void loadSymbol(String symbol);
     
     /**
      * Retrieve the instance variable with the given name, based on the current "self".
      * 
      * @param name The name of the instance variable to retrieve.
      */
     public void retrieveInstanceVariable(String name);
     
     /**
      * Assign the value on top of the stack to the instance variable with the specified name
      * on the current "self". The value is consumed.
      * 
      * @param name The name of the value to assign.
      */
     public void assignInstanceVariable(String name);
     
     /**
      * Assign the top of the stack to the global variable with the specified name.
      * 
      * @param name The name of the global variable.
      */
     public void assignGlobalVariable(String name);
     
     /**
      * Retrieve the global variable with the specified name to the top of the stack.
      * 
      * @param name The name of the global variable.
      */
     public void retrieveGlobalVariable(String name);
     
     /**
      * Perform a logical Ruby "not" operation on the value on top of the stack, leaving the
      * negated result.
      */
     public void negateCurrentValue();
     
     /**
      * Convert the current value into a "splatted value" suitable for passing as
      * method arguments or disassembling into multiple variables.
      */
     public void splatCurrentValue();
     
     /**
      * Given a splatted value, extract a single value. If no splat or length is
      * zero, use nil
      */
     public void singlifySplattedValue();
     
     /**
      * Given an IRubyObject[] on the stack (or otherwise available as the present object)
      * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
      */
     public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback);
     
     /**
      * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
      */
     public void ensureRubyArray();
     
     /**
      * Load an integer value suitable for numeric comparisons
      */
     public void loadInteger(int value);
     
     /**
      * Perform a greater-than-or-equal test and branch, given the provided true and false branches.
      */
     public void performGEBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a greater-than test and branch, given the provided true and false branches.
      */
     public void performGTBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a greater-than-or-equal test and branch, given the provided true and false branches.
      */
     public void performLEBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     /**
      * Perform a greater-than test and branch, given the provided true and false branches.
      */
     public void performLTBranch(BranchCallback trueBranch, BranchCallback falseBranch);
     
     public void loadRubyArraySize();
     
     public void issueBreakEvent();
 
-    public void objAsString();
+    public void asString();
 
     public void nthRef(int match);
 
     public void match();
 
     public void match2();
 
     public void match3();
 
     public void createNewRegexp(ByteList value, int options, String lang);
 }
diff --git a/src/org/jruby/compiler/EvStrNodeCompiler.java b/src/org/jruby/compiler/EvStrNodeCompiler.java
index 2468cf981d..c4e84779d0 100644
--- a/src/org/jruby/compiler/EvStrNodeCompiler.java
+++ b/src/org/jruby/compiler/EvStrNodeCompiler.java
@@ -1,45 +1,45 @@
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
 package org.jruby.compiler;
 
 import org.jruby.ast.Node;
 import org.jruby.ast.EvStrNode;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class EvStrNodeCompiler implements NodeCompiler {
     public void compile(Node node, Compiler context) {
         context.lineNumber(node.getPosition());
         
         final EvStrNode evStrNode = (EvStrNode)node;
         
         NodeCompilerFactory.getCompiler(evStrNode.getBody()).compile(evStrNode.getBody(), context);
-        context.objAsString();
+        context.asString();
     }
 }// EvStrNodeCompiler
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 7c3d7226c5..d5ee9ff9e6 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -414,1116 +414,1116 @@ public class StandardASMCompiler implements Compiler, Opcodes {
                 // receiver already present
                 // empty args list
                 mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
                 
             } else {
                 // VCall
                 // no receiver present, use self
                 loadSelf();
                 
                 // empty args list
                 mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
             }
         }
 
         loadThreadContext();
 
         if (index != 0) {
             // load method index
             mv.ldc(new Byte(index));
         }
 
         mv.ldc(name);
         
         // load self for visibility checks
         loadSelf();
         
         mv.getstatic(cg.p(CallType.class), callType.toString(), cg.ci(CallType.class));
         
         if (closureArg == null) {
             mv.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
         } else {
             closureArg.compile(this);
         }
         
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label tryCatch = new Label();
         if (closureArg != null) {
             // wrap with try/catch for block flow-control exceptions
             // FIXME: for flow-control from containing blocks, but it's not working right;
             // stack is not as expected for invoke calls below...
             //mv.trycatch(tryBegin, tryEnd, tryCatch, cg.p(JumpException.class));
 
             mv.label(tryBegin);
         }
         
         if (index != 0) {
             invokeUtilityMethod("doInvokeDynamicIndexed", callSigIndexed);
         } else {
             invokeUtilityMethod("doInvokeDynamic", callSig);
         }
         
         if (closureArg != null) {
             mv.label(tryEnd);
 
             // no physical break, terminate loop and skip catch block
             Label normalEnd = new Label();
             mv.go_to(normalEnd);
 
             mv.label(tryCatch);
             {
                 loadClosure();
                 invokeUtilityMethod("handleJumpException", cg.sig(IRubyObject.class, cg.params(JumpException.class, Block.class)));
             }
 
             mv.label(normalEnd);
         }
     }
     
     public static IRubyObject handleJumpException(JumpException je, Block block) {
         // JRUBY-530, Kernel#loop case:
         if (je.isBreakInKernelLoop()) {
             // consume and rethrow or just keep rethrowing?
             if (block == je.getTarget()) je.setBreakInKernelLoop(false);
 
             throw je;
         }
 
         return (IRubyObject) je.getValue();
     }
     
     public void yield(boolean hasArgs) {
         loadClosure();
         
         SkinnyMethodAdapter method = getMethodAdapter();
         
         if (hasArgs) {
             method.swap();
             
             loadThreadContext();
             method.swap();
             
             // args now here
         } else {
             loadThreadContext();
             
             // empty args
             method.aconst_null();
         }
         
         loadSelf();
         getRubyClass();
         method.ldc(Boolean.FALSE);
         
         method.invokevirtual(cg.p(Block.class), "yield", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, IRubyObject.class, IRubyObject.class, RubyModule.class, Boolean.TYPE)));
     }
     
     private void invokeIRubyObject(String methodName, String signature) {
         getMethodAdapter().invokeinterface(IRUBYOBJECT, methodName, signature);
     }
     
     public void loadThreadContext() {
         getMethodAdapter().aload(THREADCONTEXT_INDEX);
     }
     
     public void loadClosure() {
         getMethodAdapter().aload(CLOSURE_INDEX);
     }
     
     public void loadSelf() {
         getMethodAdapter().aload(SELF_INDEX);
     }
     
     public void loadRuntime() {
         getMethodAdapter().aload(RUNTIME_INDEX);
     }
     
     public void loadNil() {
         loadRuntime();
         invokeIRuby("getNil", cg.sig(IRubyObject.class));
     }
     
     public void loadSymbol(String symbol) {
         loadRuntime();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         mv.ldc(symbol);
         
         invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
     }
     
     public void consumeCurrentValue() {
         getMethodAdapter().pop();
     }
     
     public void duplicateCurrentValue() {
         getMethodAdapter().dup();
     }
     
     public void swapValues() {
         getMethodAdapter().swap();
     }
     
     public void retrieveSelf() {
         loadSelf();
     }
     
     public void retrieveSelfClass() {
         loadSelf();
         invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
     }
     
     public void assignLocalVariable(int index) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.dup();
         //if ((index - 2) < Math.abs(getArity())) {
             // load from the incoming params
             // index is 2-based, and our zero is runtime
             
             // load args array
         //    mv.aload(ARGS_INDEX);
         //    index = index - 2;
         //} else {
             mv.aload(LOCAL_VARS_INDEX);
         //}
         
         mv.swap();
         mv.ldc(new Integer(index));
         mv.swap();
         mv.arraystore();
     }
     
     public void retrieveLocalVariable(int index) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         
         // check if it's an argument
         //if ((index - 2) < Math.abs(getArity())) {
             // load from the incoming params
             // index is 2-based, and our zero is runtime
             
             // load args array
         //    mv.aload(ARGS_INDEX);
         //    mv.ldc(new Integer(index - 2));
         //    mv.arrayload();
         //} else {
             mv.aload(LOCAL_VARS_INDEX);
             mv.ldc(new Integer(index));
             mv.arrayload();
         //}
     }
     
     public void assignLocalVariable(int index, int depth) {
         if (depth == 0) {
             assignLocalVariable(index);
             return;
         }
 
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.dup();
         
         // get the appropriate array out of the scopes
         mv.aload(SCOPE_INDEX);
         mv.ldc(new Integer(depth - 1));
         mv.arrayload();
         
         // insert the value into the array at the specified index
         mv.swap();
         mv.ldc(new Integer(index));
         mv.swap();
         mv.arraystore();
     }
     
     public void retrieveLocalVariable(int index, int depth) {
         if (depth == 0) {
             retrieveLocalVariable(index);
             return;
         }
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // get the appropriate array out of the scopes
         mv.aload(SCOPE_INDEX);
         mv.ldc(new Integer(depth - 1));
         mv.arrayload();
         
         // load the value from the array at the specified index
         mv.ldc(new Integer(index));
         mv.arrayload();
     }
     
     public void retrieveConstant(String name) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // FIXME this doesn't work right yet since TC.getConstant depends on TC state
         loadThreadContext();
         mv.ldc(name);
         invokeThreadContext("getConstant", cg.sig(IRubyObject.class, cg.params(String.class)));
     }
     
     public void createNewFloat(double value) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         mv.ldc(new Double(value));
         
         invokeIRuby("newFloat", cg.sig(RubyFloat.class, cg.params(Double.TYPE)));
     }
 
     public void createNewFixnum(long value) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         mv.ldc(new Long(value));
         
         invokeIRuby("newFixnum", cg.sig(RubyFixnum.class, cg.params(Long.TYPE)));
     }
     
     public void createNewBignum(BigInteger value) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         mv.ldc(value.toString());
         
         mv.invokestatic(cg.p(RubyBignum.class) , "newBignum", cg.sig(RubyBignum.class,cg.params(Ruby.class,String.class)));
     }
     
     public void createNewString(ArrayCallback callback, int count) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         loadRuntime();
         invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
         mv.dup();
         for(int i = 0; i < count; i++) {
             callback.nextValue(this, null, i);
             mv.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
         }
     }
 
     public void createNewString(ByteList value) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // FIXME: this is sub-optimal, storing string value in a java.lang.String again
         loadRuntime();
         mv.ldc(value.toString());
         
         invokeIRuby("newString", cg.sig(RubyString.class, cg.params(String.class)));
     }
     
     public void createNewSymbol(String name) {
         loadRuntime();
         getMethodAdapter().ldc(name);
         invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
     }
     
     public void createNewArray() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         // put under object array already present
         mv.swap();
         
         invokeIRuby("newArrayNoCopy", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
     }
     
     public void createEmptyArray() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         
         invokeIRuby("newArray", cg.sig(RubyArray.class, cg.params()));
     }
     
     public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
         buildObjectArray(IRUBYOBJECT, sourceArray, callback);
     }
     
     private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         mv.ldc(new Integer(sourceArray.length));
         mv.anewarray(type);
         
         for (int i = 0; i < sourceArray.length; i++) {
             mv.dup();
             mv.ldc(new Integer(i));
             
             callback.nextValue(this, sourceArray, i);
             
             mv.arraystore();
         }
     }
     
     public void createEmptyHash() {
         SkinnyMethodAdapter mv = getMethodAdapter();
 
         loadRuntime();
         
         mv.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
     }
     
     public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         
         // create a new hashmap
         mv.newobj(cg.p(HashMap.class));
         mv.dup();       
         mv.invokespecial(cg.p(HashMap.class), "<init>", cg.sig(Void.TYPE));
         
         for (int i = 0; i < keyCount; i++) {
             mv.dup();       
             callback.nextValue(this, elements, i);
             mv.invokevirtual(cg.p(HashMap.class), "put", cg.sig(Object.class, cg.params(Object.class, Object.class)));
             mv.pop();
         }
         
         loadNil();
         mv.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class, Map.class, IRubyObject.class)));
     }
     
     public void createNewRange(boolean isExclusive) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadRuntime();
         
         mv.dup_x2();
         mv.pop();
 
         mv.ldc(new Boolean(isExclusive));
         
         mv.invokestatic(cg.p(RubyRange.class), "newRange", cg.sig(RubyRange.class, cg.params(Ruby.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
     }
     
     /**
      * Invoke IRubyObject.isTrue
      */
     private void isTrue() {
         invokeIRubyObject("isTrue", cg.sig(Boolean.TYPE));
     }
     
     public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // call isTrue on the result
         isTrue();
         
         mv.ifeq(falseJmp); // EQ == 0 (i.e. false)
         trueBranch.branch(this);
         mv.go_to(afterJmp);
         
         // FIXME: optimize for cases where we have no false branch
         mv.label(falseJmp);
         falseBranch.branch(this);
         
         mv.label(afterJmp);
     }
     
     public void performLogicalAnd(BranchCallback longBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // dup it since we need to return appropriately if it's false
         mv.dup();
         
         // call isTrue on the result
         isTrue();
         
         mv.ifeq(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         mv.pop();
         longBranch.branch(this);
         mv.label(falseJmp);
     }
     
     public void performLogicalOr(BranchCallback longBranch) {
         Label afterJmp = new Label();
         Label falseJmp = new Label();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // dup it since we need to return appropriately if it's false
         mv.dup();
         
         // call isTrue on the result
         isTrue();
         
         mv.ifne(falseJmp); // EQ == 0 (i.e. false)
         // pop the extra result and replace with the send part of the AND
         mv.pop();
         longBranch.branch(this);
         mv.label(falseJmp);
     }
     
     public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
         // FIXME: handle next/continue, break, etc
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label tryCatch = new Label();
         
         mv.trycatch(tryBegin, tryEnd, tryCatch, cg.p(JumpException.class));
         
         mv.label(tryBegin);
         {
             Label endJmp = new Label();
             if (checkFirst) {
                 // calculate condition
                 condition.branch(this);
                 // call isTrue on the result
                 isTrue();
 
                 mv.ifeq(endJmp); // EQ == 0 (i.e. false)
             }
 
             Label topJmp = new Label();
 
             mv.label(topJmp);
 
             body.branch(this);
 
             // clear result after each loop
             mv.pop();
 
             // calculate condition
             condition.branch(this);
             // call isTrue on the result
             isTrue();
 
             mv.ifne(topJmp); // NE == nonzero (i.e. true)
 
             if (checkFirst) {
                 mv.label(endJmp);
             }
         }
         
         mv.label(tryEnd);
         
         // no physical break, terminate loop and skip catch block
         Label normalBreak = new Label();
         mv.go_to(normalBreak);
         
         mv.label(tryCatch);
         {
             mv.dup();
             mv.invokevirtual(cg.p(JumpException.class), "getJumpType", cg.sig(JumpException.JumpType.class));
             mv.invokevirtual(cg.p(JumpException.JumpType.class), "getTypeId", cg.sig(Integer.TYPE));
 
             Label tryDefault = new Label();
             Label breakLabel = new Label();
 
             mv.lookupswitch(tryDefault, new int[] {JumpException.JumpType.BREAK}, new Label[] {breakLabel});
 
             // default is to just re-throw unhandled exception
             mv.label(tryDefault);
             mv.athrow();
 
             // break just terminates the loop normally, unless it's a block break...
             mv.label(breakLabel);
             
             // JRUBY-530 behavior
             mv.dup();
             mv.invokevirtual(cg.p(JumpException.class), "getTarget", cg.sig(Object.class));
             loadClosure();
             Label notBlockBreak = new Label();
             mv.if_acmpne(notBlockBreak);
             mv.dup();
             mv.aconst_null();
             mv.invokevirtual(cg.p(JumpException.class), "setTarget", cg.sig(Void.TYPE, cg.params(Object.class)));
             mv.athrow();
 
             mv.label(notBlockBreak);
             // target is not == closure, normal loop exit, pop remaining exception object
             mv.pop();
         }
         
         mv.label(normalBreak);
         loadNil();
     }
     
     public static CompiledBlock createBlock(ThreadContext context, IRubyObject self, int arity, IRubyObject[][] scopes, Block block, CompiledBlockCallback callback) {
         return new CompiledBlock(context, self, Arity.createArity(arity), scopes, block, callback);
     }
     
     public void createNewClosure(StaticScope scope, int arity, ClosureCallback body) {
         // FIXME: This isn't quite done yet; waiting to have full support for passing closures so we can test it
         ClassVisitor cv = getClassVisitor();
         SkinnyMethodAdapter method;
         
         String closureMethodName = "closure" + ++innerIndex;
         String closureFieldName = "_" + closureMethodName;
         
         // declare the field
         cv.visitField(ACC_PRIVATE | ACC_STATIC, closureFieldName, cg.ci(CompiledBlockCallback.class), null, null);
         
         ////////////////////////////
         // closure implementation
         method = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, closureMethodName, CLOSURE_SIGNATURE, null, null));
         pushMethodAdapter(method);
         
         method.start();
         
         // logic to start off the closure with dvar slots
         method.ldc(new Integer(scope.getNumberOfVariables()));
         method.anewarray(cg.p(IRubyObject.class));
         
         // store the dvars in a local variable
         method.astore(LOCAL_VARS_INDEX);
         
         // arraycopy arguments into local vars array
         if (arity != 0) {
             // array index OOB for some reason; perhaps because we're not actually handling args right?
             /*method.aload(ARGS_INDEX);
             method.iconst_0();
             method.aload(LOCAL_VARS_INDEX);
             mv.iconst_2();
             method.ldc(new Integer(arity));
             method.invokestatic(cg.p(System.class), "arraycopy", cg.sig(Void.TYPE, cg.params(Object.class, Integer.TYPE, Object.class, Integer.TYPE, Integer.TYPE)));*/
         }
         
         // Containing scopes are passed as IRubyObject[][] in the SCOPE_INDEX var
         
         // set up a local IRuby variable
         method.aload(THREADCONTEXT_INDEX);
         invokeThreadContext("getRuntime", cg.sig(Ruby.class));
         method.astore(RUNTIME_INDEX);
         
         // start of scoping for closure's vars
         Label start = new Label();
         method.label(start);
         
         // visit the body of the closure
         body.compile(this);
         
         method.areturn();
         
         // end of scoping for closure's vars
         Label end = new Label();
         method.label(end);
         method.end();
         
         popMethodAdapter();
         
         method = getMethodAdapter();
         
         // Now, store a compiled block object somewhere we can access it in the future
         
         // in current method, load the field to see if we've created a BlockCallback yet
         method.getstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
         Label alreadyCreated = new Label();
         method.ifnonnull(alreadyCreated);
         
         // no callback, construct it
         getCallbackFactory();
         
         method.ldc(closureMethodName);
         method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class)));
         method.putstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
         
         method.label(alreadyCreated);
         
         // Construct the block for passing to the target method
         loadThreadContext();
         loadSelf();
         method.ldc(new Integer(arity));
         
         // create an array of scopes to use
         
         // check if we have containing scopes
         method.aload(SCOPE_INDEX);
         Label noScopes = new Label();
         Label copyLocals = new Label();
         method.ifnull(noScopes);
         
         // we have containing scopes, include them
         
         // get length of current scopes array, add one
         method.aload(SCOPE_INDEX);
         method.arraylength();
         method.iconst_1();
         method.iadd();
         
         // create new scopes array
         method.anewarray(cg.p(IRubyObject[].class));
         
         // copy containing scopes to index one and on
         method.dup();
         method.aload(SCOPE_INDEX);
         method.swap();
         method.iconst_0();
         method.swap();
         // new scopes array is here now
         method.iconst_1();
         method.aload(SCOPE_INDEX);
         method.arraylength();
         method.invokestatic(cg.p(System.class), "arraycopy", cg.sig(Void.TYPE, cg.params(Object.class, Integer.TYPE, Object.class, Integer.TYPE, Integer.TYPE)));
 
         method.go_to(copyLocals);
         
         method.label(noScopes);
 
         // create new scopes array
         method.iconst_1();
         method.anewarray(cg.p(IRubyObject[].class));
         
         method.label(copyLocals);
 
         // store local vars at index zero
         method.dup();
         method.iconst_0();
         method.aload(LOCAL_VARS_INDEX);
         method.arraystore();
         
         loadClosure();
         
         method.getstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
         
         invokeUtilityMethod("createBlock", cg.sig(CompiledBlock.class, cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, IRubyObject[][].class, Block.class, CompiledBlockCallback.class)));
     }
     
     /**
      * This is for utility methods used by the compiler, to reduce the amount of code generation necessary.
      * Most of these currently live on StandardASMCompiler, but should be moved to a more appropriate location.
      */
     private void invokeUtilityMethod(String methodName, String signature) {
         getMethodAdapter().invokestatic(cg.p(StandardASMCompiler.class), methodName, signature);
     }
     
     private void invokeThreadContext(String methodName, String signature) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.invokevirtual(THREADCONTEXT, methodName, signature);
     }
     
     private void invokeIRuby(String methodName, String signature) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.invokevirtual(RUBY, methodName, signature);
     }
     
     private void getCallbackFactory() {
         loadRuntime();
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.ldc(classname);
         mv.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
         invokeIRuby("callbackFactory", cg.sig(CallbackFactory.class, cg.params(Class.class)));
     }
     
     private void getRubyClass() {
         loadSelf();
         // FIXME: This doesn't seem *quite* right. If actually within a class...end, is self.getMetaClass the correct class? should be self, no?
         invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
     }
     
     private void getCRef() {
         loadThreadContext();
         // FIXME: This doesn't seem *quite* right. If actually within a class...end, is self.getMetaClass the correct class? should be self, no?
         invokeThreadContext("peekCRef", cg.sig(SinglyLinkedList.class));
     }
     
     private void newTypeError(String error) {
         loadRuntime();
         getMethodAdapter().ldc(error);
         invokeIRuby("newTypeError", cg.sig(RaiseException.class, cg.params(String.class)));
     }
     
     private void getCurrentVisibility() {
         loadThreadContext();
         invokeThreadContext("getCurrentVisibility", cg.sig(Visibility.class));
     }
     
     private void println() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         mv.dup();
         mv.getstatic(cg.p(System.class), "out", cg.ci(PrintStream.class));
         mv.swap();
         
         mv.invokevirtual(cg.p(PrintStream.class), "println", cg.sig(Void.TYPE, cg.params(Object.class)));
     }
     
     public void defineAlias(String newName, String oldName) {
         getRubyClass();
         getMethodAdapter().ldc(newName);
         getMethodAdapter().ldc(oldName);
         getMethodAdapter().invokevirtual(cg.p(RubyModule.class), "defineAlias", cg.sig(Void.TYPE,cg.params(String.class,String.class)));
         loadNil();
         // TODO: should call method_added, and possibly push nil.
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Class compiledClass, String name, String javaName, int arity) {
         Ruby runtime = context.getRuntime();
         
         // FIXME: This is what the old def did, but doesn't work in the compiler for top-level methods. Hmm.
         //RubyModule containingClass = context.getRubyClass();
         RubyModule containingClass = self.getMetaClass();
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
         
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
             visibility = Visibility.PRIVATE;
         }
         
         SinglyLinkedList cref = context.peekCRef();
         
         MethodFactory factory = MethodFactory.createFactory();
         DynamicMethod method = factory.getCompiledMethod(containingClass, compiledClass, javaName, Arity.createArity(arity), visibility, cref);
         
         containingClass.addMethod(name, method);
         
         if (context.getCurrentVisibility().isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttachedObject().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
         
         return runtime.getNil();
     }
     
     public void defineNewMethod(String name, int arity, int localVarCount, ClosureCallback body) {
         // TODO: build arg list based on number of args, optionals, etc
         ++methodIndex;
         String methodName = cg.cleanJavaIdentifier(name) + "__" + methodIndex;
         
         beginMethod(methodName, arity, localVarCount);
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         // put a null at register 4, for closure creation to know we're at top-level or local scope
         mv.aconst_null();
         mv.astore(SCOPE_INDEX);
         
         // callback to fill in method body
         body.compile(this);
         
         endMethod(mv);
         
         // return to previous method
         mv = getMethodAdapter();
         
         // prepare to call "def" utility method to handle def logic
         loadThreadContext();
         
         loadSelf();
         
         // load the class we're creating, for binding purposes
         mv.ldc(classname.replace('/', '.'));
         mv.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
         
         mv.ldc(name);
         
         mv.ldc(methodName);
         
         mv.ldc(new Integer(arity));
         
         mv.invokestatic(cg.p(StandardASMCompiler.class),
                 "def",
                 cg.sig(IRubyObject.class, cg.params(ThreadContext.class, IRubyObject.class, Class.class, String.class, String.class, Integer.TYPE)));
     }
     
     public void loadFalse() {
         loadRuntime();
         invokeIRuby("getFalse", cg.sig(RubyBoolean.class));
     }
     
     public void loadTrue() {
         loadRuntime();
         invokeIRuby("getTrue", cg.sig(RubyBoolean.class));
     }
     
     public void retrieveInstanceVariable(String name) {
         loadSelf();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         mv.ldc(name);
         invokeIRubyObject("getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
         
         // check if it's null; if so, load nil
         mv.dup();
         Label notNull = new Label();
         mv.ifnonnull(notNull);
         
         // pop the dup'ed null
         mv.pop();
         // replace it with nil
         loadNil();
         
         mv.label(notNull);
     }
     
     public void assignInstanceVariable(String name) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         loadSelf();
         mv.swap();
         
         mv.ldc(name);
         mv.swap();
         
         invokeIRubyObject("setInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
     }
     
     public void retrieveGlobalVariable(String name) {
         loadRuntime();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
         mv.ldc(name);
         mv.invokevirtual(cg.p(GlobalVariables.class), "get", cg.sig(IRubyObject.class, cg.params(String.class)));
     }
     
     public void assignGlobalVariable(String name) {
         loadRuntime();
         
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
         mv.swap();
         mv.ldc(name);
         mv.swap();
         mv.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
     }
     
     public void negateCurrentValue() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         isTrue();
         Label isTrue = new Label();
         Label end = new Label();
         mv.ifne(isTrue);
         loadTrue();
         mv.go_to(end);
         mv.label(isTrue);
         loadFalse();
         mv.label(end);
     }
     
     public void splatCurrentValue() {
         SkinnyMethodAdapter method = getMethodAdapter();
         
         method.invokestatic(cg.p(EvaluationState.class), "splatValue", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
     }
     
     public void singlifySplattedValue() {
         SkinnyMethodAdapter method = getMethodAdapter();
         method.invokestatic(cg.p(EvaluationState.class), "aValueSplat", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
     }
     
     public void ensureRubyArray() {
         SkinnyMethodAdapter method = getMethodAdapter();
         
         method.invokestatic(cg.p(StandardASMCompiler.class), "ensureRubyArray", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
     }
     
     public static RubyArray ensureRubyArray(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(value.getRuntime(), value);
         }
         return (RubyArray)value;
     }
     
     public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback) {
         SkinnyMethodAdapter method = getMethodAdapter();
         
         Label noMoreArrayElements = new Label();
         for (; start < count; start++) {
             // confirm we're not past the end of the array
             method.dup(); // dup the original array object
             method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE, cg.params()));
             method.ldc(new Integer(start));
             method.ifle(noMoreArrayElements); // if length <= start, end loop
             
             // extract item from array
             method.dup(); // dup the original array object
             method.ldc(new Integer(start)); // index for the item
             method.invokevirtual(cg.p(RubyArray.class), "entry",
                     cg.sig(IRubyObject.class, cg.params(Long.TYPE))); // extract item
             callback.nextValue(this, source, start);
         }
         method.label(noMoreArrayElements);
     }
 
     public void loadInteger(int value) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public void performGEBranch(BranchCallback trueBranch,
                                 BranchCallback falseBranch) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public void performGTBranch(BranchCallback trueBranch,
                                 BranchCallback falseBranch) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public void performLEBranch(BranchCallback trueBranch,
                                 BranchCallback falseBranch) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public void performLTBranch(BranchCallback trueBranch,
                                 BranchCallback falseBranch) {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     public void loadRubyArraySize() {
         throw new UnsupportedOperationException("Not supported yet.");
     }
     
     public void issueBreakEvent() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         
         mv.newobj(cg.p(JumpException.class));
         mv.dup();
         mv.getstatic(cg.p(JumpException.JumpType.class), "BreakJump", cg.ci(JumpException.JumpType.class));
         mv.invokespecial(cg.p(JumpException.class), "<init>", cg.sig(Void.TYPE, cg.params(JumpException.JumpType.class)));
         
         // set result into jump exception
         mv.dup_x1();
         mv.swap();
         mv.invokevirtual(cg.p(JumpException.class), "setValue", cg.sig(Void.TYPE, cg.params(Object.class)));
         
         mv.athrow();
     }
 
-    public void objAsString() {
+    public void asString() {
         SkinnyMethodAdapter mv = getMethodAdapter();
-        mv.invokeinterface(cg.p(IRubyObject.class), "objAsString", cg.sig(RubyString.class, cg.params()));
+        mv.invokeinterface(cg.p(IRubyObject.class), "asString", cg.sig(RubyString.class, cg.params()));
     }
 
     public void nthRef(int match) {
         SkinnyMethodAdapter mv = getMethodAdapter();
 
         mv.ldc(new Integer(match));
         loadThreadContext();
         invokeThreadContext("getBackref", cg.sig(IRubyObject.class, cg.params()));
         mv.invokestatic(cg.p(RubyRegexp.class), "nth_match", cg.sig(IRubyObject.class, cg.params(Integer.TYPE,IRubyObject.class)));
     }
 
     public void match() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.invokevirtual(cg.p(RubyRegexp.class), "match2", cg.sig(IRubyObject.class, cg.params()));
     }
 
     public void match2() {
         SkinnyMethodAdapter mv = getMethodAdapter();
         mv.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
     }
 
     public void match3() {
         SkinnyMethodAdapter mv = getMethodAdapter();
 
         mv.dup();
         mv.visitTypeInsn(INSTANCEOF, cg.p(RubyString.class));
 
         Label l0 = new Label();
         mv.visitJumpInsn(IFEQ, l0);
 
         mv.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
 
         Label l1 = new Label();
         mv.visitJumpInsn(GOTO, l1);
         mv.visitLabel(l0);
 
         mv.swap();
         loadThreadContext();
         mv.swap();
         mv.ldc("=~");
         mv.swap();
 
         mv.invokeinterface(cg.p(IRubyObject.class), "callMethod", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, String.class, IRubyObject.class)));
         mv.visitLabel(l1);
     }
 
     private int constants = 0;
     private String getNewConstant(String type, String name_prefix) {
         ClassVisitor cv = getClassVisitor();
 
         String realName;
         synchronized(this) {
             realName = name_prefix + constants++;
         }
 
         // declare the field
         cv.visitField(ACC_PRIVATE|ACC_STATIC, realName, type, null, null).visitEnd();
         return realName;
     }
 
     private final static org.jruby.RegexpTranslator TRANS = new org.jruby.RegexpTranslator();
 
     public static int regexpLiteralFlags(int options) {
         return TRANS.flagsFor(options,0);
     }
 
     public static Pattern regexpLiteral(Ruby runtime, String ptr, int options) {
         try {
             return TRANS.translate(ptr, options, 0);
         } catch(jregex.PatternSyntaxException e) {
             throw runtime.newRegexpError(e.getMessage());
         }
     }
 
     public void createNewRegexp(final ByteList value, final int options, final String lang) {
         SkinnyMethodAdapter mv = getMethodAdapter();
         String name = getNewConstant(cg.ci(Pattern.class),"literal_re_");
         String name_flags = getNewConstant(cg.ci(Integer.TYPE),"literal_re_flags_");
 
         loadRuntime();
 
         // in current method, load the field to see if we've created a Pattern yet
 
         mv.visitFieldInsn(GETSTATIC, classname, name, cg.ci(Pattern.class));
         mv.dup();
 
         Label alreadyCreated = new Label();
         mv.ifnonnull(alreadyCreated);
         mv.pop();
         mv.ldc(new Integer(options));
         invokeUtilityMethod("regexpLiteralFlags",cg.sig(Integer.TYPE,cg.params(Integer.TYPE)));
         mv.visitFieldInsn(PUTSTATIC, classname, name_flags, cg.ci(Integer.TYPE));
 
         loadRuntime();
         mv.ldc(value.toString());
         mv.ldc(new Integer(options));
         invokeUtilityMethod("regexpLiteral",cg.sig(Pattern.class,cg.params(Ruby.class,String.class,Integer.TYPE)));
         mv.dup();
 
         mv.visitFieldInsn(PUTSTATIC, classname, name, cg.ci(Pattern.class));
 
         mv.label(alreadyCreated);
         
         mv.visitFieldInsn(GETSTATIC, classname, name_flags, cg.ci(Integer.TYPE));
         if(null == lang) {
             mv.aconst_null();
         } else {
             mv.ldc(lang);
         }
 
         mv.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, Pattern.class, Integer.TYPE, String.class)));
     }
 }
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index a26eba0412..93d2bf946b 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,2177 +1,2177 @@
 /*******************************************************************************
  * BEGIN LICENSE BLOCK *** Version: CPL 1.0/GPL 2.0/LGPL 2.1
  * 
  * The contents of this file are subject to the Common Public License Version
  * 1.0 (the "License"); you may not use this file except in compliance with the
  * License. You may obtain a copy of the License at
  * http://www.eclipse.org/legal/cpl-v10.html
  * 
  * Software distributed under the License is distributed on an "AS IS" basis,
  * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  * the specific language governing rights and limitations under the License.
  * 
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
  * Copytight (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"), or
  * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
  * which case the provisions of the GPL or the LGPL are applicable instead of
  * those above. If you wish to allow use of your version of this file only under
  * the terms of either the GPL or the LGPL, and not to allow others to use your
  * version of this file under the terms of the CPL, indicate your decision by
  * deleting the provisions above and replace them with the notice and other
  * provisions required by the GPL or the LGPL. If you do not delete the
  * provisions above, a recipient may use your version of this file under the
  * terms of any one of the CPL, the GPL or the LGPL. END LICENSE BLOCK ****
  ******************************************************************************/
 
 package org.jruby.evaluator;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.MetaClass;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.LocalVarNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OptNNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.ZSuperNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException.JumpType;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ForBlock;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class EvaluationState {
     public static IRubyObject eval(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         try {
             return evalInternal(runtime, context, node, self, block);
         } catch (StackOverflowError sfe) {
             throw runtime.newSystemStackError("stack level too deep");
         }
     }
 
     private static IRubyObject evalInternal(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         do {
             if (node == null) return nilNode(runtime);
 
             switch (node.nodeId) {
             case NodeTypes.ALIASNODE:
                 return aliasNode(runtime, context, node);
             case NodeTypes.ANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
    
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue;
             }
             case NodeTypes.ARGSCATNODE:
                 return argsCatNode(runtime, context, node, self, aBlock);
             case NodeTypes.ARGSPUSHNODE:
                 return argsPushNode(runtime, context, node, self, aBlock);
             case NodeTypes.ARRAYNODE:
                 return arrayNode(runtime, context, node, self, aBlock);
             case NodeTypes.ATTRASSIGNNODE:
                 return attrAssignNode(runtime, context, node, self, aBlock); 
             case NodeTypes.BACKREFNODE:
                 return backRefNode(context, node);
             case NodeTypes.BEGINNODE: 
                 node = ((BeginNode)node).getBodyNode();
                 continue;
             case NodeTypes.BIGNUMNODE:
                 return bignumNode(runtime, node);
             case NodeTypes.BLOCKNODE:
                 return blockNode(runtime, context, node, self, aBlock);
             case NodeTypes.BLOCKPASSNODE:
             assert false: "Call nodes and friends deal with this";
             case NodeTypes.BREAKNODE:
                 return breakNode(runtime, context, node, self, aBlock);
             case NodeTypes.CALLNODE:
                 return callNode(runtime, context, node, self, aBlock);
             case NodeTypes.CASENODE:
                 return caseNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSNODE:
                 return classNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARASGNNODE:
                 return classVarAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARDECLNODE:
                 return classVarDeclNode(runtime, context, node, self, aBlock);
             case NodeTypes.CLASSVARNODE:
                 return classVarNode(runtime, context, node, self);
             case NodeTypes.COLON2NODE:
                 return colon2Node(runtime, context, node, self, aBlock);
             case NodeTypes.COLON3NODE:
                 return colon3Node(runtime, node);
             case NodeTypes.CONSTDECLNODE:
                 return constDeclNode(runtime, context, node, self, aBlock);
             case NodeTypes.CONSTNODE:
                 return constNode(context, node);
             case NodeTypes.DASGNNODE:
                 return dAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.DEFINEDNODE:
                 return definedNode(runtime, context, node, self, aBlock);
             case NodeTypes.DEFNNODE:
                 return defnNode(runtime, context, node);
             case NodeTypes.DEFSNODE:
                 return defsNode(runtime, context, node, self, aBlock);
             case NodeTypes.DOTNODE:
                 return dotNode(runtime, context, node, self, aBlock);
             case NodeTypes.DREGEXPNODE:
                 return dregexpNode(runtime, context, node, self, aBlock);
             case NodeTypes.DSTRNODE:
                 return dStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.DSYMBOLNODE:
                 return dSymbolNode(runtime, context, node, self, aBlock);
             case NodeTypes.DVARNODE:
                 return dVarNode(runtime, context, node);
             case NodeTypes.DXSTRNODE:
                 return dXStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.ENSURENODE:
                 return ensureNode(runtime, context, node, self, aBlock);
             case NodeTypes.EVSTRNODE:
                 return evStrNode(runtime, context, node, self, aBlock);
             case NodeTypes.FALSENODE:
                 return falseNode(runtime, context);
             case NodeTypes.FCALLNODE:
                 return fCallNode(runtime, context, node, self, aBlock);
             case NodeTypes.FIXNUMNODE:
                 return fixnumNode(runtime, node);
             case NodeTypes.FLIPNODE:
                 return flipNode(runtime, context, node, self, aBlock);
             case NodeTypes.FLOATNODE:
                 return floatNode(runtime, node);
             case NodeTypes.FORNODE:
                 return forNode(runtime, context, node, self, aBlock);
             case NodeTypes.GLOBALASGNNODE:
                 return globalAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.GLOBALVARNODE:
                 return globalVarNode(runtime, context, node);
             case NodeTypes.HASHNODE:
                 return hashNode(runtime, context, node, self, aBlock);
             case NodeTypes.IFNODE: {
                 IfNode iVisited = (IfNode) node;
                 IRubyObject result = evalInternal(runtime,context, iVisited.getCondition(), self, aBlock);
 
                 if (result.isTrue()) {
                     node = iVisited.getThenBody();
                 } else {
                     node = iVisited.getElseBody();
                 }
                 continue;
             }
             case NodeTypes.INSTASGNNODE:
                 return instAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.INSTVARNODE:
                 return instVarNode(runtime, node, self);
             case NodeTypes.ITERNODE: 
             assert false: "Call nodes deal with these directly";
             case NodeTypes.LOCALASGNNODE:
                 return localAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.LOCALVARNODE:
                 return localVarNode(runtime, context, node);
             case NodeTypes.MATCH2NODE:
                 return match2Node(runtime, context, node, self, aBlock);
             case NodeTypes.MATCH3NODE:
                 return match3Node(runtime, context, node, self, aBlock);
             case NodeTypes.MATCHNODE:
                 return matchNode(runtime, context, node, self, aBlock);
             case NodeTypes.MODULENODE:
                 return moduleNode(runtime, context, node, self, aBlock);
             case NodeTypes.MULTIPLEASGNNODE:
                 return multipleAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.NEWLINENODE: {
                 NewlineNode iVisited = (NewlineNode) node;
         
                 // something in here is used to build up ruby stack trace...
                 context.setPosition(iVisited.getPosition());
 
                 if (isTrace(runtime)) {
                     callTraceFunction(runtime, context, "line", self);
                 }
 
                 // TODO: do above but not below for additional newline nodes
                 node = iVisited.getNextNode();
                 continue;
             }
             case NodeTypes.NEXTNODE:
                 return nextNode(runtime, context, node, self, aBlock);
             case NodeTypes.NILNODE:
                 return nilNode(runtime);
             case NodeTypes.NOTNODE:
                 return notNode(runtime, context, node, self, aBlock);
             case NodeTypes.NTHREFNODE:
                 return nthRefNode(context, node);
             case NodeTypes.OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
         
                 // add in reverse order
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue;
             }
             case NodeTypes.OPASGNNODE:
                 return opAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPASGNORNODE:
                 return opAsgnOrNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPELEMENTASGNNODE:
                 return opElementAsgnNode(runtime, context, node, self, aBlock);
             case NodeTypes.OPTNNODE:
                 return optNNode(runtime, context, node, self, aBlock);
             case NodeTypes.ORNODE:
                 return orNode(runtime, context, node, self, aBlock);
             case NodeTypes.REDONODE: 
                 return redoNode(context, node);
             case NodeTypes.REGEXPNODE:
                 return regexpNode(runtime, node);
             case NodeTypes.RESCUEBODYNODE:
                 node = ((RescueBodyNode)node).getBodyNode();
                 continue;
             case NodeTypes.RESCUENODE:
                 return rescueNode(runtime, context, node, self, aBlock);
             case NodeTypes.RETRYNODE:
                 return retryNode(context);
             case NodeTypes.RETURNNODE: 
                 return returnNode(runtime, context, node, self, aBlock);
             case NodeTypes.ROOTNODE:
                 return rootNode(runtime, context, node, self, aBlock);
             case NodeTypes.SCLASSNODE:
                 return sClassNode(runtime, context, node, self, aBlock);
             case NodeTypes.SELFNODE:
                 return self;
             case NodeTypes.SPLATNODE:
                 return splatNode(runtime, context, node, self, aBlock);
             case NodeTypes.STRNODE:
                 return strNode(runtime, node);
             case NodeTypes.SUPERNODE:
                 return superNode(runtime, context, node, self, aBlock);
             case NodeTypes.SVALUENODE:
                 return sValueNode(runtime, context, node, self, aBlock);
             case NodeTypes.SYMBOLNODE:
                 return symbolNode(runtime, node);
             case NodeTypes.TOARYNODE:
                 return toAryNode(runtime, context, node, self, aBlock);
             case NodeTypes.TRUENODE:
                 return trueNode(runtime, context);
             case NodeTypes.UNDEFNODE:
                 return undefNode(runtime, context, node);
             case NodeTypes.UNTILNODE:
                 return untilNode(runtime, context, node, self, aBlock);
             case NodeTypes.VALIASNODE:
                 return valiasNode(runtime, node);
             case NodeTypes.VCALLNODE:
                 return vcallNode(runtime, context, node, self);
             case NodeTypes.WHENNODE:
                 assert false;
                 return null;
             case NodeTypes.WHILENODE:
                 return whileNode(runtime, context, node, self, aBlock);
             case NodeTypes.XSTRNODE:
                 return xStrNode(runtime, context, node, self);
             case NodeTypes.YIELDNODE:
                 return yieldNode(runtime, context, node, self, aBlock);
             case NodeTypes.ZARRAYNODE:
                 return zArrayNode(runtime);
             case NodeTypes.ZSUPERNODE:
                 return zsuperNode(runtime, context, node, self, aBlock);
             default:
                 throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
             }
         } while(true);
     }
 
     private static IRubyObject aliasNode(Ruby runtime, ThreadContext context, Node node) {
         AliasNode iVisited = (AliasNode) node;
    
         if (context.getRubyClass() == null) {
             throw runtime.newTypeError("no class to make alias");
         }
    
         context.getRubyClass().defineAlias(iVisited.getNewName(), iVisited.getOldName());
         context.getRubyClass().callMethod(context, "method_added", runtime.newSymbol(iVisited.getNewName()));
    
         return runtime.getNil();
     }
     
     private static IRubyObject argsCatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsCatNode iVisited = (ArgsCatNode) node;
    
         IRubyObject args = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         IRubyObject secondArgs = splatValue(runtime, evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
         RubyArray list = args instanceof RubyArray ? (RubyArray) args : runtime.newArray(args);
    
         return list.concat(secondArgs);
     }
 
     private static IRubyObject argsPushNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArgsPushNode iVisited = (ArgsPushNode) node;
         
         RubyArray args = (RubyArray) evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock).dup();
         return args.append(evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock));
     }
 
     private static IRubyObject arrayNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ArrayNode iVisited = (ArrayNode) node;
         IRubyObject[] array = new IRubyObject[iVisited.size()];
         
         for (int i = 0; i < iVisited.size(); i++) {
             Node next = iVisited.get(i);
    
             array[i] = evalInternal(runtime,context, next, self, aBlock);
         }
    
         if (iVisited.isLightweight()) {
             return runtime.newArrayNoCopyLight(array);
         }
         
         return runtime.newArrayNoCopy(array);
     }
 
     public static RubyArray arrayValue(Ruby runtime, IRubyObject value) {
         IRubyObject newValue = value.convertToType("Array", "to_ary", false);
         if (newValue.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime
                     .getKernel()) {
                 newValue = value.convertToType("Array", "to_a", false);
                 if (newValue.getType() != runtime.getClass("Array")) {
                     throw runtime.newTypeError("`to_a' did not return Array");
                 }
             } else {
                 newValue = runtime.newArray(value);
             }
         }
 
         return (RubyArray) newValue;
     }
 
     private static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return value.convertToType("Array", "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     private static IRubyObject attrAssignNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         AttrAssignNode iVisited = (AttrAssignNode) node;
    
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
         
         // If reciever is self then we do the call the same way as vcall
         CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
    
         receiver.callMethod(context, iVisited.getName(), args, callType);
         
         return args[args.length - 1];
     }
 
     private static IRubyObject backRefNode(ThreadContext context, Node node) {
         BackRefNode iVisited = (BackRefNode) node;
         IRubyObject backref = context.getBackref();
         switch (iVisited.getType()) {
         case '~':
             return backref;
         case '&':
             return RubyRegexp.last_match(backref);
         case '`':
             return RubyRegexp.match_pre(backref);
         case '\'':
             return RubyRegexp.match_post(backref);
         case '+':
             return RubyRegexp.match_last(backref);
         default:
             assert false: "backref with invalid type";
             return null;
         }
     }
 
     private static IRubyObject bignumNode(Ruby runtime, Node node) {
         return RubyBignum.newBignum(runtime, ((BignumNode)node).getValue());
     }
 
     private static IRubyObject blockNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BlockNode iVisited = (BlockNode) node;
    
         IRubyObject result = runtime.getNil();
         for (int i = 0; i < iVisited.size(); i++) {
             result = evalInternal(runtime,context, (Node) iVisited.get(i), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject breakNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         BreakNode iVisited = (BreakNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         JumpException je = new JumpException(JumpException.JumpType.BreakJump);
    
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject callNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CallNode iVisited = (CallNode) node;
 
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         
         assert receiver.getMetaClass() != null : receiver.getClass().getName();
 
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         RubyModule module = receiver.getMetaClass();
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             if (module.index != 0 && iVisited.index != 0) {
                 return receiver.callMethod(context, module,
                         runtime.getSelectorTable().table[module.index][iVisited.index],
                         iVisited.getName(), args, CallType.NORMAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
       
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, receiver, method, iVisited.getName(), args, self, CallType.NORMAL, Block.NULL_BLOCK);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, receiver, module, iVisited.getName(), args, false, Block.NULL_BLOCK);
             }
         }
             
         while (true) {
             try {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
 
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, receiver, method, iVisited.getName(), args, self, CallType.NORMAL, block);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, receiver, module, iVisited.getName(), args, false, block);
             } catch (JumpException je) {
                 switch (je.getJumpType().getTypeId()) {
                 case JumpType.RETRY:
                     // allow loop to retry
                 case JumpType.BREAK:
                     return (IRubyObject) je.getValue();
                 default:
                     throw je;
                 }
             }
         }
     }
 
     private static IRubyObject caseNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         CaseNode iVisited = (CaseNode) node;
         IRubyObject expression = null;
         if (iVisited.getCaseNode() != null) {
             expression = evalInternal(runtime,context, iVisited.getCaseNode(), self, aBlock);
         }
 
         context.pollThreadEvents();
 
         IRubyObject result = runtime.getNil();
 
         Node firstWhenNode = iVisited.getFirstWhenNode();
         while (firstWhenNode != null) {
             if (!(firstWhenNode instanceof WhenNode)) {
                 node = firstWhenNode;
                 return evalInternal(runtime, context, node, self, aBlock);
             }
 
             WhenNode whenNode = (WhenNode) firstWhenNode;
 
             if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                 ArrayNode arrayNode = (ArrayNode)whenNode.getExpressionNodes();
                 for (int i = 0; i < arrayNode.size(); i++) {
                     Node tag = arrayNode.get(i);
 
                     context.setPosition(tag.getPosition());
                     if (isTrace(runtime)) {
                         callTraceFunction(runtime, context, "line", self);
                     }
 
                     // Ruby grammar has nested whens in a case body because of
                     // productions case_body and when_args.
                     if (tag instanceof WhenNode) {
                         RubyArray expressions = (RubyArray) evalInternal(runtime,context, ((WhenNode) tag)
                                         .getExpressionNodes(), self, aBlock);
 
                         for (int j = 0,k = expressions.getLength(); j < k; j++) {
                             IRubyObject condition = expressions.eltInternal(j);
 
                             if ((expression != null && condition.callMethod(context, "===", expression)
                                     .isTrue())
                                     || (expression == null && condition.isTrue())) {
                                 node = ((WhenNode) firstWhenNode).getBodyNode();
                                 return evalInternal(runtime, context, node, self, aBlock);
                             }
                         }
                         continue;
                     }
 
                     result = evalInternal(runtime,context, tag, self, aBlock);
 
                     if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                             || (expression == null && result.isTrue())) {
                         node = whenNode.getBodyNode();
                         return evalInternal(runtime, context, node, self, aBlock);
                     }
                 }
             } else {
                 result = evalInternal(runtime,context, whenNode.getExpressionNodes(), self, aBlock);
 
                 if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                         || (expression == null && result.isTrue())) {
                     node = ((WhenNode) firstWhenNode).getBodyNode();
                     return evalInternal(runtime, context, node, self, aBlock);
                 }
             }
 
             context.pollThreadEvents();
 
             firstWhenNode = whenNode.getNextCase();
         }
 
         return runtime.getNil();
     }
 
     private static IRubyObject classNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassNode iVisited = (ClassNode) node;
         Node superNode = iVisited.getSuperNode();
         RubyClass superClass = null;
         if(superNode != null) {
             IRubyObject _super = evalInternal(runtime,context, superNode, self, aBlock);
             if(!(_super instanceof RubyClass)) {
                 throw runtime.newTypeError("superclass must be a Class (" + RubyObject.trueFalseNil(_super) + ") given");
             }
             superClass = superNode == null ? null : (RubyClass)_super;
         }
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingClass = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
         RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
    
         if (context.getWrapper() != null) {
-            rubyClass.extendObject(context.getWrapper());
+            context.getWrapper().extend_object(rubyClass);
             rubyClass.includeModule(context.getWrapper());
         }
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), rubyClass, self, aBlock);
     }
 
     private static IRubyObject classVarAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }     
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
    
         RubyModule rubyClass = getClassVariableBase(context, runtime);                
         if (rubyClass == null) {
             throw runtime.newTypeError("no class/module to define class variable");
         }
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         rubyClass.setClassVar(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject classVarNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         ClassVarNode iVisited = (ClassVarNode) node;
         RubyModule rubyClass = getClassVariableBase(context, runtime);
    
         if (rubyClass == null) {
             rubyClass = self.getMetaClass();
         }
 
         return rubyClass.getClassVar(iVisited.getName());
     }
 
     private static IRubyObject colon2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Colon2Node iVisited = (Colon2Node) node;
         Node leftNode = iVisited.getLeftNode();
    
         // TODO: Made this more colon3 friendly because of cpath production
         // rule in grammar (it is convenient to think of them as the same thing
         // at a grammar level even though evaluation is).
         if (leftNode == null) {
             return runtime.getObject().getConstantFrom(iVisited.getName());
         } else {
             IRubyObject result = evalInternal(runtime,context, iVisited.getLeftNode(), self, aBlock);
             if (result instanceof RubyModule) {
                 return ((RubyModule) result).getConstantFrom(iVisited.getName());
             } else {
                 return result.callMethod(context, iVisited.getName(), aBlock);
             }
         }
     }
 
     private static IRubyObject colon3Node(Ruby runtime, Node node) {
         return runtime.getObject().getConstantFrom(((Colon3Node)node).getName());
     }
 
     private static IRubyObject constDeclNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ConstDeclNode iVisited = (ConstDeclNode) node;
         Node constNode = iVisited.getConstNode();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         IRubyObject module;
 
         if (constNode == null) {
             // FIXME: why do we check RubyClass and then use CRef?
             if (context.getRubyClass() == null) {
                 // TODO: wire into new exception handling mechanism
                 throw runtime.newTypeError("no class/module to define constant");
             }
             module = (RubyModule) context.peekCRef().getValue();
         } else if (constNode instanceof Colon2Node) {
             module = evalInternal(runtime,context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, aBlock);
         } else { // Colon3
             module = runtime.getObject();
         } 
    
         // FIXME: shouldn't we use the result of this set in setResult?
         ((RubyModule) module).setConstant(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject constNode(ThreadContext context, Node node) {
         return context.getConstant(((ConstNode)node).getName());
     }
 
     private static IRubyObject dAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DAsgnNode iVisited = (DAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
 
         // System.out.println("DSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
    
         return result;
     }
 
     private static IRubyObject definedNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefinedNode iVisited = (DefinedNode) node;
         String definition = getDefinition(runtime, context, iVisited.getExpressionNode(), self, aBlock);
         if (definition != null) {
             return runtime.newString(definition);
         } else {
             return runtime.getNil();
         }
     }
 
     private static IRubyObject defnNode(Ruby runtime, ThreadContext context, Node node) {
         DefnNode iVisited = (DefnNode) node;
         
         RubyModule containingClass = context.getRubyClass();
    
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
    
         String name = iVisited.getName();
 
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
    
         Visibility visibility = context.getCurrentVisibility();
         if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
             visibility = Visibility.PRIVATE;
         }
         
         DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), visibility, context.peekCRef());
    
         containingClass.addMethod(name, newMethod);
    
         if (context.getCurrentVisibility().isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(
                     name,
                     new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                             Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(name));
         }
    
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttachedObject().callMethod(
                     context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
         } else {
             containingClass.callMethod(context, "method_added", runtime.newSymbol(name));
         }
    
         return runtime.getNil();
     }
     
     private static IRubyObject defsNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DefsNode iVisited = (DefsNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         RubyClass rubyClass;
    
         if (receiver.isNil()) {
             rubyClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             rubyClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             rubyClass = runtime.getClass("FalseClass");
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure; can't define singleton method.");
             }
             if (receiver.isFrozen()) {
                 throw runtime.newFrozenError("object");
             }
             if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
                 throw runtime.newTypeError("can't define singleton method \"" + iVisited.getName()
                                            + "\" for " + receiver.getType());
             }
    
             rubyClass = receiver.getSingletonClass();
         }
    
         if (runtime.getSafeLevel() >= 4) {
             Object method = rubyClass.getMethods().get(iVisited.getName());
             if (method != null) {
                 throw runtime.newSecurityError("Redefining method prohibited.");
             }
         }
    
         DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getScope(), 
                 iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), 
                 Visibility.PUBLIC, context.peekCRef());
    
         rubyClass.addMethod(iVisited.getName(), newMethod);
         receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
    
         return runtime.getNil();
     }
 
     private static IRubyObject dotNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DotNode iVisited = (DotNode) node;
         return RubyRange.newRange(runtime, 
                 evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock), 
                 evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock), 
                 iVisited.isExclusive());
     }
 
     private static IRubyObject dregexpNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DRegexpNode iVisited = (DRegexpNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
 
         try {
             return RubyRegexp.newRegexp(runtime, string.toString(), iVisited.getOptions(), lang);
         } catch(jregex.PatternSyntaxException e) {
         //                    System.err.println(iVisited.getValue().toString());
         //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
     }
     
     private static IRubyObject dStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DStrNode iVisited = (DStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return string;
     }
 
     private static IRubyObject dSymbolNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DSymbolNode iVisited = (DSymbolNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return runtime.newSymbol(string.toString());
     }
 
     private static IRubyObject dVarNode(Ruby runtime, ThreadContext context, Node node) {
         DVarNode iVisited = (DVarNode) node;
 
         // System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject obj = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         // FIXME: null check is removable once we figure out how to assign to unset named block args
         return obj == null ? runtime.getNil() : obj;
     }
 
     private static IRubyObject dXStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         DXStrNode iVisited = (DXStrNode) node;
    
         RubyString string = runtime.newString(new ByteList());
         for (int i = 0; i < iVisited.size(); i++) {
             Node iterNode = iVisited.get(i);
             if (iterNode instanceof StrNode) {
                 string.getByteList().append(((StrNode) iterNode).getValue());
             } else {
                 string.append(evalInternal(runtime,context, iterNode, self, aBlock));
             }
         }
    
         return self.callMethod(context, "`", string);
     }
 
     private static IRubyObject ensureNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         EnsureNode iVisited = (EnsureNode) node;
         
         // save entering the try if there's nothing to ensure
         if (iVisited.getEnsureNode() != null) {
             IRubyObject result = runtime.getNil();
 
             try {
                 result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
             } finally {
                 evalInternal(runtime,context, iVisited.getEnsureNode(), self, aBlock);
             }
 
             return result;
         }
 
         node = iVisited.getBodyNode();
         return evalInternal(runtime, context, node, self, aBlock);
     }
 
     private static IRubyObject evStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
-        return evalInternal(runtime,context, ((EvStrNode)node).getBody(), self, aBlock).objAsString();
+        return evalInternal(runtime,context, ((EvStrNode)node).getBody(), self, aBlock).asString();
     }
     
     private static IRubyObject falseNode(Ruby runtime, ThreadContext context) {
         context.pollThreadEvents();
         return runtime.getFalse();
     }
 
     private static IRubyObject fCallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FCallNode iVisited = (FCallNode) node;
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             RubyModule module = self.getMetaClass();
             if (module.index != 0 && iVisited.index != 0) {
                 return self.callMethod(context, module,
                         runtime.getSelectorTable().table[module.index][iVisited.index],
                         iVisited.getName(), args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(iVisited.getName());
 
                 IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), args, self, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 if (mmResult != null) {
                     return mmResult;
                 }
 
                 return method.call(context, self, module, iVisited.getName(), args, false, Block.NULL_BLOCK);
             }
         }
 
         while (true) {
             try {
                 RubyModule module = self.getMetaClass();
                 IRubyObject result = self.callMethod(context, module, iVisited.getName(), args,
                                                      CallType.FUNCTIONAL, block);
                 if (result == null) {
                     result = runtime.getNil();
                 }
                     
                 return result; 
             } catch (JumpException je) {
                 switch (je.getJumpType().getTypeId()) {
                 case JumpType.RETRY:
                     // allow loop to retry
                     break;
                 case JumpType.BREAK:
                     // JRUBY-530, Kernel#loop case:
                     if (je.isBreakInKernelLoop()) {
                         // consume and rethrow or just keep rethrowing?
                         if (block == je.getTarget()) je.setBreakInKernelLoop(false);
                             
                         throw je;
                     }
                         
                     return (IRubyObject) je.getValue();
                 default:
                     throw je;
                 }
             }
         }
     }
 
     private static IRubyObject fixnumNode(Ruby runtime, Node node) {
         return ((FixnumNode)node).getFixnum(runtime);
     }
 
     private static IRubyObject flipNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FlipNode iVisited = (FlipNode) node;
         IRubyObject result = runtime.getNil();
    
         if (iVisited.isExclusive()) {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 result = evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getFalse()
                         : runtime.getTrue();
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         } else {
             if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                 if (evalInternal(runtime,context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(
                             iVisited.getIndex(),
                             evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue() ? runtime.getFalse()
                                     : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } else {
                     return runtime.getFalse();
                 }
             } else {
                 if (evalInternal(runtime,context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 return runtime.getTrue();
             }
         }
     }
 
     private static IRubyObject floatNode(Ruby runtime, Node node) {
         return RubyFloat.newFloat(runtime, ((FloatNode)node).getValue());
     }
 
     private static IRubyObject forNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ForNode iVisited = (ForNode) node;
         
         Block block = ForBlock.createBlock(context, iVisited.getVarNode(), 
                 context.getCurrentScope(), iVisited.getCallable(), self);
    
         try {
             while (true) {
                 try {
                     ISourcePosition position = context.getPosition();
    
                     IRubyObject recv = null;
                     try {
                         recv = evalInternal(runtime,context, iVisited.getIterNode(), self, aBlock);
                     } finally {
                         context.setPosition(position);
                     }
    
                     return recv.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallType.NORMAL, block);
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.RETRY:
                         // do nothing, allow loop to retry
                         break;
                     default:
                         throw je;
                     }
                 }
             }
         } catch (JumpException je) {
             switch (je.getJumpType().getTypeId()) {
             case JumpType.BREAK:
                 return (IRubyObject) je.getValue();
             default:
                 throw je;
             }
         }
     }
 
     private static IRubyObject globalAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         if (iVisited.getName().length() == 2) {
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 context.getCurrentScope().setLastLine(result);
                 return result;
             case '~':
                 context.getCurrentScope().setBackRef(result);
                 return result;
             }
         }
    
         runtime.getGlobalVariables().set(iVisited.getName(), result);
    
         // FIXME: this should be encapsulated along with the set above
         if (iVisited.getName() == "$KCODE") {
             runtime.setKCode(KCode.create(runtime, result.toString()));
         }
    
         return result;
     }
 
     private static IRubyObject globalVarNode(Ruby runtime, ThreadContext context, Node node) {
         GlobalVarNode iVisited = (GlobalVarNode) node;
         
         if (iVisited.getName().length() == 2) {
             IRubyObject value = null;
             switch (iVisited.getName().charAt(1)) {
             case '_':
                 value = context.getCurrentScope().getLastLine();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             case '~':
                 value = context.getCurrentScope().getBackRef();
                 if (value == null) {
                     return runtime.getNil();
                 }
                 return value;
             }
         }
         
         return runtime.getGlobalVariables().get(iVisited.getName());
     }
 
     private static IRubyObject hashNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         HashNode iVisited = (HashNode) node;
    
         Map hash = null;
         if (iVisited.getListNode() != null) {
             hash = new HashMap(iVisited.getListNode().size() / 2);
    
         for (int i = 0; i < iVisited.getListNode().size();) {
                 // insert all nodes in sequence, hash them in the final instruction
                 // KEY
                 IRubyObject key = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
                 IRubyObject value = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
    
                 hash.put(key, value);
             }
         }
    
         if (hash == null) {
             return RubyHash.newHash(runtime);
         }
    
         return RubyHash.newHash(runtime, hash, runtime.getNil());
     }
 
     private static IRubyObject instAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         InstAsgnNode iVisited = (InstAsgnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         self.setInstanceVariable(iVisited.getName(), result);
    
         return result;
     }
 
     private static IRubyObject instVarNode(Ruby runtime, Node node, IRubyObject self) {
         InstVarNode iVisited = (InstVarNode) node;
         IRubyObject variable = self.getInstanceVariable(iVisited.getName());
    
         return variable == null ? runtime.getNil() : variable;
     }
 
     private static IRubyObject localAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         LocalAsgnNode iVisited = (LocalAsgnNode) node;
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         
         //System.out.println("LSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
 
         return result;
     }
 
     private static IRubyObject localVarNode(Ruby runtime, ThreadContext context, Node node) {
         LocalVarNode iVisited = (LocalVarNode) node;
 
         //System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
         return result == null ? runtime.getNil() : result;
     }
 
     private static IRubyObject match2Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match2Node iVisited = (Match2Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         return ((RubyRegexp) recv).match(value);
     }
     
     private static IRubyObject match3Node(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         Match3Node iVisited = (Match3Node) node;
         IRubyObject recv = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         if (value instanceof RubyString) {
             return ((RubyRegexp) recv).match(value);
         } else {
             return value.callMethod(context, "=~", recv);
         }
     }
 
     private static IRubyObject matchNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return ((RubyRegexp) evalInternal(runtime,context, ((MatchNode)node).getRegexpNode(), self, aBlock)).match2();
     }
 
     private static IRubyObject moduleNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ModuleNode iVisited = (ModuleNode) node;
         Node classNameNode = iVisited.getCPath();
         String name = ((INameNode) classNameNode).getName();
         RubyModule enclosingModule = getEnclosingModule(runtime, context, classNameNode, self, aBlock);
    
         if (enclosingModule == null) {
             throw runtime.newTypeError("no outer class/module");
         }
    
         RubyModule module;
         if (enclosingModule == runtime.getObject()) {
             module = runtime.getOrCreateModule(name);
         } else {
             module = enclosingModule.defineModuleUnder(name);
         }
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), module, self, aBlock);
     }
 
     private static IRubyObject multipleAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
         IRubyObject value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
 
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(runtime, value);
         }
         return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, (RubyArray) value, false);
     }
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.NextJump);
    
         je.setTarget(iVisited);
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject nilNode(Ruby runtime) {
         return runtime.getNil();
     }
 
     private static IRubyObject notNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NotNode iVisited = (NotNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock);
         return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
     }
 
     private static IRubyObject nthRefNode(ThreadContext context, Node node) {
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getBackref());
     }
 
     private static IRubyObject opAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnNode iVisited = (OpAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
    
         if (iVisited.getOperatorName() == "||") {
             if (value.isTrue()) {
                 return value;
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!value.isTrue()) {
                 return value;
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             value = value.callMethod(context, iVisited.getOperatorName(), evalInternal(runtime,context,
                     iVisited.getValueNode(), self, aBlock));
         }
    
         receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
    
         context.pollThreadEvents();
    
         return value;
     }
 
     private static IRubyObject opAsgnOrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
         String def = getDefinition(runtime, context, iVisited.getFirstNode(), self, aBlock);
    
         IRubyObject result = runtime.getNil();
         if (def != null) {
             result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
         }
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject opElementAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
    
         IRubyObject firstValue = receiver.callMethod(context, "[]", args);
    
         if (iVisited.getOperatorName() == "||") {
             if (firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!firstValue.isTrue()) {
                 return firstValue;
             }
             firstValue = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             firstValue = firstValue.callMethod(context, iVisited.getOperatorName(), evalInternal(runtime,context, iVisited
                             .getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         return receiver.callMethod(context, "[]=", expandedArgs);
     }
 
     private static IRubyObject optNNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OptNNode iVisited = (OptNNode) node;
    
         IRubyObject result = runtime.getNil();
         while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         // do nothing, this iteration restarts
                         break;
                     case JumpType.NEXT:
                         // recheck condition
                         break loop;
                     case JumpType.BREAK:
                         // end loop
                         return (IRubyObject) je.getValue();
                     default:
                         throw je;
                     }
                 }
             }
         }
         return result;
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         JumpException je = new JumpException(JumpException.JumpType.RedoJump);
    
         je.setValue(node);
    
         throw je;
     }
 
     private static IRubyObject regexpNode(Ruby runtime, Node node) {
         RegexpNode iVisited = (RegexpNode) node;
         String lang = null;
         int opts = iVisited.getOptions();
         if((opts & 16) != 0) { // param n
             lang = "n";
         } else if((opts & 48) != 0) { // param s
             lang = "s";
         } else if((opts & 64) != 0) { // param s
             lang = "u";
         }
         try {
             return RubyRegexp.newRegexp(runtime, iVisited.getPattern(), iVisited.getFlags(), lang);
         } catch(jregex.PatternSyntaxException e) {
             //                    System.err.println(iVisited.getValue().toString());
             //                    e.printStackTrace();
             throw runtime.newRegexpError(e.getMessage());
         }
     }
 
     private static IRubyObject rescueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RescueNode iVisited = (RescueNode)node;
         RescuedBlock : while (true) {
             IRubyObject globalExceptionState = runtime.getGlobalVariables().get("$!");
             boolean anotherExceptionRaised = false;
             try {
                 // Execute rescue block
                 IRubyObject result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
 
                 // If no exception is thrown execute else block
                 if (iVisited.getElseNode() != null) {
                     if (iVisited.getRescueNode() == null) {
                         runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
                     }
                     result = evalInternal(runtime,context, iVisited.getElseNode(), self, aBlock);
                 }
 
                 return result;
             } catch (RaiseException raiseJump) {
                 RubyException raisedException = raiseJump.getException();
                 // TODO: Rubicon TestKernel dies without this line.  A cursory glance implies we
                 // falsely set $! to nil and this sets it back to something valid.  This should 
                 // get fixed at the same time we address bug #1296484.
                 runtime.getGlobalVariables().set("$!", raisedException);
 
                 RescueBodyNode rescueNode = iVisited.getRescueNode();
 
                 while (rescueNode != null) {
                     Node  exceptionNodes = rescueNode.getExceptionNodes();
                     ListNode exceptionNodesList;
                     
                     if (exceptionNodes instanceof SplatNode) {                    
                         exceptionNodesList = (ListNode) evalInternal(runtime,context, exceptionNodes, self, aBlock);
                     } else {
                         exceptionNodesList = (ListNode) exceptionNodes;
                     }
                     
                     if (isRescueHandled(runtime, context, raisedException, exceptionNodesList, self)) {
                         try {
                             return evalInternal(runtime,context, rescueNode, self, aBlock);
                         } catch (JumpException je) {
                             if (je.getJumpType() == JumpException.JumpType.RetryJump) {
                                 // should be handled in the finally block below
                                 //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                                 //state.threadContext.setRaisedException(null);
                                 continue RescuedBlock;
                                 
                             } else {
                                 anotherExceptionRaised = true;
                                 throw je;
                             }
                         }
                     }
                     
                     rescueNode = rescueNode.getOptRescueNode();
                 }
 
                 // no takers; bubble up
                 throw raiseJump;
             } finally {
                 // clear exception when handled or retried
                 if (!anotherExceptionRaised)
                     runtime.getGlobalVariables().set("$!", globalExceptionState);
             }
         }
     }
 
     private static IRubyObject retryNode(ThreadContext context) {
         context.pollThreadEvents();
    
         JumpException je = new JumpException(JumpException.JumpType.RetryJump);
    
         throw je;
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
    
         je.setTarget(iVisited.getTarget());
         je.setValue(result);
    
         throw je;
     }
 
     private static IRubyObject rootNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         RootNode iVisited = (RootNode) node;
         DynamicScope scope = iVisited.getScope();
         
         // Serialization killed our dynamic scope.  We can just create an empty one
         // since serialization cannot serialize an eval (which is the only thing
         // which is capable of having a non-empty dynamic scope).
         if (scope == null) {
             scope = new DynamicScope(iVisited.getStaticScope(), null);
         }
         
         // Each root node has a top-level scope that we need to push
         context.preRootNode(scope);
         
         // FIXME: Wire up BEGIN and END nodes
 
         try {
             return evalInternal(runtime, context, iVisited.getBodyNode(), self, aBlock);
         } finally {
             context.postRootNode();
         }
     }
 
     private static IRubyObject sClassNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SClassNode iVisited = (SClassNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
 
         RubyClass singletonClass;
 
         if (receiver.isNil()) {
             singletonClass = runtime.getNilClass();
         } else if (receiver == runtime.getTrue()) {
             singletonClass = runtime.getClass("TrueClass");
         } else if (receiver == runtime.getFalse()) {
             singletonClass = runtime.getClass("FalseClass");
         } else if (receiver.getMetaClass() == runtime.getFixnum() || receiver.getMetaClass() == runtime.getClass("Symbol")) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             singletonClass = receiver.getSingletonClass();
         }
 
         
 
         if (context.getWrapper() != null) {
-            singletonClass.extendObject(context.getWrapper());
+            context.getWrapper().extend_object(singletonClass);
             singletonClass.includeModule(context.getWrapper());
         }
 
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self, aBlock);
     }
 
     private static IRubyObject splatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return splatValue(runtime, evalInternal(runtime,context, ((SplatNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject strNode(Ruby runtime, Node node) {
         return runtime.newString((ByteList) ((StrNode) node).getValue().clone());
     }
     
     private static IRubyObject superNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         SuperNode iVisited = (SuperNode) node;
    
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // If no explicit block passed to super, then use the one passed in.
         if (!block.isGiven()) block = aBlock;
         
         return self.callSuper(context, args, block);
     }
     
     private static IRubyObject sValueNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aValueSplat(runtime, evalInternal(runtime,context, ((SValueNode) node).getValue(), self, aBlock));
     }
     
     private static IRubyObject symbolNode(Ruby runtime, Node node) {
         return runtime.newSymbol(((SymbolNode) node).getName());
     }
     
     private static IRubyObject toAryNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return aryToAry(runtime, evalInternal(runtime,context, ((ToAryNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject trueNode(Ruby runtime, ThreadContext context) {
         context.pollThreadEvents();
         return runtime.getTrue();
     }
     
     private static IRubyObject undefNode(Ruby runtime, ThreadContext context, Node node) {
         UndefNode iVisited = (UndefNode) node;
         
    
         if (context.getRubyClass() == null) {
             throw runtime
                     .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
         }
         context.getRubyClass().undef(iVisited.getName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject untilNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         UntilNode iVisited = (UntilNode) node;
    
         IRubyObject result = runtime.getNil();
         
         while (!(result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     result = evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530 until case
                         if (je.getTarget() == aBlock) {
                              je.setTarget(null);
                              
                              throw je;
                         }
                         
                         return (IRubyObject) je.getValue();
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return result;
     }
 
     private static IRubyObject valiasNode(Ruby runtime, Node node) {
         VAliasNode iVisited = (VAliasNode) node;
         runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
    
         return runtime.getNil();
     }
 
     private static IRubyObject vcallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         VCallNode iVisited = (VCallNode) node;
         RubyModule module = self.getMetaClass();
         if (module.index != 0 && iVisited.index != 0) {
             return self.callMethod(context, module, runtime.getSelectorTable().table[module.index][iVisited.index], iVisited.getName(), 
                     IRubyObject.NULL_ARRAY, CallType.VARIABLE, Block.NULL_BLOCK);
         } else {
             DynamicMethod method = module.searchMethod(iVisited.getName());
 
             IRubyObject mmResult = RubyObject.callMethodMissingIfNecessary(context, self, method, iVisited.getName(), IRubyObject.NULL_ARRAY, self, CallType.VARIABLE, Block.NULL_BLOCK);
             if (mmResult != null) {
                 return mmResult;
             }
 
             return method.call(context, self, module, iVisited.getName(), IRubyObject.NULL_ARRAY, false, Block.NULL_BLOCK);
         }
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = runtime.getNil();
         boolean firstTest = iVisited.evaluateAtStart();
         
         while (!firstTest || (result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.REDO:
                         continue;
                     case JumpType.NEXT:
                         break loop;
                     case JumpType.BREAK:
                         // JRUBY-530, while case
                         if (je.getTarget() == aBlock) {
                             je.setTarget(null);
                             
                             throw je;
                         }
                         
                         return result;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return result;
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newString((ByteList) ((XStrNode) node).getValue().clone()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getArgsNode(), self, aBlock);
         if (iVisited.getArgsNode() == null) {
             result = null;
         }
 
         Block block = context.getCurrentFrame().getBlock();
 
         return block.yield(context, result, null, null, iVisited.getCheckState());
     }
 
     private static IRubyObject zArrayNode(Ruby runtime) {
         return runtime.newArray();
     }
     
     private static IRubyObject zsuperNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
 
         Block block = getBlock(runtime, context, self, aBlock, ((ZSuperNode) node).getIterNode());
 
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         context.getCurrentScope().getArgValues(context.getFrameArgs(),context.getCurrentFrame().getRequiredArgCount());
         return self.callSuper(context, context.getFrameArgs(), block);
     }
 
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static void callTraceFunction(Ruby runtime, ThreadContext context, String event, IRubyObject zelf) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callTraceFunction(context, event, context.getPosition(), zelf, name, type);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "class", type);
             }
 
             return evalInternal(runtime,context, bodyNode, type, block);
         } finally {
             context.postClassEval();
 
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "end", null);
             }
         }
     }
 
     private static String getArgumentDefinition(Ruby runtime, ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             for (int i = 0; i < ((ArrayNode)node).size(); i++) {
                 Node iterNode = ((ArrayNode)node).get(i);
                 if (getDefinitionInner(runtime, context, iterNode, self, block) == null) return null;
             }
         } else if (getDefinitionInner(runtime, context, node, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock, Node blockNode) {
         if (blockNode == null) return Block.NULL_BLOCK;
         
         if (blockNode instanceof IterNode) {
             IterNode iterNode = (IterNode) blockNode;
             // Create block for this iter node
             return Block.createBlock(context, iterNode.getVarNode(),
                     new DynamicScope(iterNode.getScope(), context.getCurrentScope()),
                     iterNode.getCallable(), self);
         } else if (blockNode instanceof BlockPassNode) {
             BlockPassNode blockPassNode = (BlockPassNode) blockNode;
             IRubyObject proc = evalInternal(runtime,context, blockPassNode.getBodyNode(), self, currentBlock);
 
             // No block from a nil proc
             if (proc.isNil()) return Block.NULL_BLOCK;
 
             // If not already a proc then we should try and make it one.
             if (!(proc instanceof RubyProc)) {
                 proc = proc.convertToType("Proc", "to_proc", false);
 
                 if (!(proc instanceof RubyProc)) {
                     throw runtime.newTypeError("wrong argument type "
                             + proc.getMetaClass().getName() + " (expected Proc)");
                 }
             }
 
             // TODO: Add safety check for taintedness
             
             if (currentBlock.isGiven()) {
                 RubyProc procObject = currentBlock.getProcObject();
                 // The current block is already associated with proc.  No need to create a new one
                 if (procObject != null && procObject == proc) return currentBlock;
             }
             
             return ((RubyProc) proc).getBlock();
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     private static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         SinglyLinkedList cref = context.peekCRef();
         RubyModule rubyClass = (RubyModule) cref.getValue();
         if (rubyClass.isSingleton()) {
             cref = cref.getNext();
             rubyClass = (RubyModule) cref.getValue();
             if (cref.getNext() == null) {
                 runtime.getWarnings().warn("class variable access from toplevel singleton method");
             }            
         }
         return rubyClass;
     }
 
     private static String getDefinition(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return getDefinitionInner(runtime, context, node, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
 
     private static String getDefinitionInner(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return "expression";
         
         switch(node.nodeId) {
         case NodeTypes.ATTRASSIGNNODE: {
             AttrAssignNode iVisited = (AttrAssignNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime,context, iVisited.getArgsNode(), "assignment", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.BACKREFNODE:
             return "$" + ((BackRefNode) node).getType();
         case NodeTypes.CALLNODE: {
             CallNode iVisited = (CallNode) node;
             
             if (getDefinitionInner(runtime, context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(runtime, context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
                         }
                     }
                 } catch (JumpException excptn) {
                 }
             }
 
             return null;
         }
         case NodeTypes.CLASSVARASGNNODE: case NodeTypes.CLASSVARDECLNODE: case NodeTypes.CONSTDECLNODE:
         case NodeTypes.DASGNNODE: case NodeTypes.GLOBALASGNNODE: case NodeTypes.LOCALASGNNODE:
         case NodeTypes.MULTIPLEASGNNODE: case NodeTypes.OPASGNNODE: case NodeTypes.OPELEMENTASGNNODE:
             return "assignment";
             
         case NodeTypes.CLASSVARNODE: {
             ClassVarNode iVisited = (ClassVarNode) node;
             
             if (context.getRubyClass() == null && self.getMetaClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } else if (!context.getRubyClass().isSingleton() && context.getRubyClass().isClassVarDefined(iVisited.getName())) {
                 return "class_variable";
             } 
               
             RubyModule module = (RubyModule) context.getRubyClass().getInstanceVariable("__attached__");
             if (module != null && module.isClassVarDefined(iVisited.getName())) return "class_variable"; 
 
             return null;
         }
         case NodeTypes.COLON2NODE: {
             Colon2Node iVisited = (Colon2Node) node;
             
             try {
                 IRubyObject left = EvaluationState.eval(runtime, context, iVisited.getLeftNode(), self, aBlock);
                 if (left instanceof RubyModule &&
                         ((RubyModule) left).getConstantAt(iVisited.getName()) != null) {
                     return "constant";
                 } else if (left.getMetaClass().isMethodBound(iVisited.getName(), true)) {
                     return "method";
                 }
             } catch (JumpException excptn) {}
             
             return null;
         }
         case NodeTypes.CONSTNODE:
             if (context.getConstantDefined(((ConstNode) node).getName())) {
                 return "constant";
             }
             return null;
         case NodeTypes.DVARNODE:
             return "local-variable(in-block)";
         case NodeTypes.FALSENODE:
             return "false";
         case NodeTypes.FCALLNODE: {
             FCallNode iVisited = (FCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "method", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.GLOBALVARNODE:
             if (runtime.getGlobalVariables().isDefined(((GlobalVarNode) node).getName())) {
                 return "global-variable";
             }
             return null;
         case NodeTypes.INSTVARNODE:
             if (self.getInstanceVariable(((InstVarNode) node).getName()) != null) {
                 return "instance-variable";
             }
             return null;
         case NodeTypes.LOCALVARNODE:
             return "local-variable";
         case NodeTypes.MATCH2NODE: case NodeTypes.MATCH3NODE:
             return "method";
         case NodeTypes.NILNODE:
             return "nil";
         case NodeTypes.NTHREFNODE:
             return "$" + ((NthRefNode) node).getMatchNumber();
         case NodeTypes.SELFNODE:
             return "state.getSelf()";
         case NodeTypes.SUPERNODE: {
             SuperNode iVisited = (SuperNode) node;
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return getArgumentDefinition(runtime, context, iVisited.getArgsNode(), "super", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.TRUENODE:
             return "true";
         case NodeTypes.VCALLNODE: {
             VCallNode iVisited = (VCallNode) node;
             if (self.getMetaClass().isMethodBound(iVisited.getName(), false)) {
                 return "method";
             }
             
             return null;
         }
         case NodeTypes.YIELDNODE:
             return aBlock.isGiven() ? "yield" : null;
         case NodeTypes.ZSUPERNODE: {
             String name = context.getFrameName();
             RubyModule klazz = context.getFrameKlazz();
             if (name != null && klazz != null && klazz.getSuperClass().isMethodBound(name, false)) {
                 return "super";
             }
             return null;
         }
         default:
             try {
                 EvaluationState.eval(runtime, context, node, self, aBlock);
                 return "expression";
             } catch (JumpException jumpExcptn) {}
         }
         
         return null;
     }
 
     private static RubyModule getEnclosingModule(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         RubyModule enclosingModule = null;
 
         if (node instanceof Colon2Node) {
             IRubyObject result = evalInternal(runtime,context, ((Colon2Node) node).getLeftNode(), self, block);
 
             if (result != null && !result.isNil()) {
                 enclosingModule = (RubyModule) result;
             }
         } else if (node instanceof Colon3Node) {
             enclosingModule = runtime.getObject();
         }
 
         if (enclosingModule == null) {
             enclosingModule = (RubyModule) context.peekCRef().getValue();
         }
 
         return enclosingModule;
     }
 
     private static boolean isRescueHandled(Ruby runtime, ThreadContext context, RubyException currentException, ListNode exceptionNodes,
             IRubyObject self) {
         if (exceptionNodes == null) {
             return currentException.isKindOf(runtime.getClass("StandardError"));
         }
 
         IRubyObject[] args = setupArgs(runtime, context, exceptionNodes, self);
 
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(runtime.getClass("Module"))) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             if (args[i].callMethod(context, "===", currentException).isTrue()) return true;
         }
         return false;
     }
 
     /**
      * Helper method.
      *
      * test if a trace function is avaiable.
      *
      */
     private static boolean isTrace(Ruby runtime) {
         return runtime.getTraceFunction() != null;
     }
 
     private static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             ISourcePosition position = context.getPosition();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = evalInternal(runtime,context, argsArrayNode.get(i), self, Block.NULL_BLOCK);
             }
 
             context.setPosition(position);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(evalInternal(runtime,context, node, self, Block.NULL_BLOCK));
     }
 
     public static IRubyObject splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 }
diff --git a/src/org/jruby/evaluator/ValueConverter.java b/src/org/jruby/evaluator/ValueConverter.java
index 06ddf5f9b3..19bacb3930 100644
--- a/src/org/jruby/evaluator/ValueConverter.java
+++ b/src/org/jruby/evaluator/ValueConverter.java
@@ -1,110 +1,110 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public final class ValueConverter {
     private Ruby runtime;
 
     public ValueConverter(Ruby runtime) {
         this.runtime = runtime;
     }
 
     public RubyArray singleToArray(IRubyObject value) {
         if (value == null || value.isNil()) {
             return runtime.newArray(0);
         } else if (value instanceof RubyArray) {
             if (((RubyArray)value).getLength() == 1) {
                 return (RubyArray) value;
             }
 			return runtime.newArray(value);
         } else {
             return toArray(value);
         }
     }
     
     public IRubyObject arrayToSingle(IRubyObject value, boolean useUndefined) {
         if (!(value instanceof RubyArray)) {
             value = toArray(value);
         }
         switch (((RubyArray)value).getLength()) {
             case 0:
                 return useUndefined ? null : runtime.getNil();
             case 1:
                 return ((RubyArray)value).eltInternal(0);
             default:
                 return value;
         }
     }
 
     public RubyArray singleToMultiple(IRubyObject value) {
         if (value == null || value.isNil()) {
             return runtime.newArray(0);
         } else if (value instanceof RubyArray) {
             return (RubyArray) value;
         } else {
             return toArray(value);
         }
     }
 
     public IRubyObject multipleToSingle(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = toArray(value);
         }
         switch (((RubyArray)value).getLength()) {
             case 0:
                 return runtime.getNil();
             case 1:
                 if (!(((RubyArray)value).eltInternal(0) instanceof RubyArray)) {
                     return ((RubyArray)value).eltInternal(0);
                 }
             default:
                 return value;
         }
     }
 
     private RubyArray toArray(IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(0);
         } else if (value instanceof RubyArray) {
             return (RubyArray)value;
         }
         if (value.respondsTo("to_ary")) {
-            return (RubyArray)value.convertType(RubyArray.class, "Array", "to_ary");
+            return (RubyArray)value.convertToArray();
         }
         return runtime.newArray(value);
     }
 }
diff --git a/src/org/jruby/javasupport/bsf/JRubyEngine.java b/src/org/jruby/javasupport/bsf/JRubyEngine.java
index 38adb9e24d..33e3bc4309 100644
--- a/src/org/jruby/javasupport/bsf/JRubyEngine.java
+++ b/src/org/jruby/javasupport/bsf/JRubyEngine.java
@@ -1,234 +1,236 @@
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
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.javasupport.bsf;
 
 import java.util.Arrays;
 import java.util.List;
 import java.util.Vector;
 
 import org.apache.bsf.BSFDeclaredBean;
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.bsf.util.BSFEngineImpl;
 import org.apache.bsf.util.BSFFunctions;
 import org.jruby.Ruby;
 import org.jruby.ast.Node;
+import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaEmbedUtils;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** An implementation of a JRuby BSF implementation.
  *
  * @author  jpetersen
  */
 public class JRubyEngine extends BSFEngineImpl {
     private Ruby runtime;
 
     public Object apply(String file, int line, int col, Object funcBody, Vector paramNames, Vector args) {
         ThreadContext threadContext = runtime.getCurrentContext();
         try {
             // add a new method conext
             String[] names = new String[paramNames.size()];
             paramNames.toArray(names);
 
             threadContext.preBsfApply(names);
             
             // FIXME: This is broken.  We are assigning BSF globals as local vars in the top-level
             // scope.  This may be ok, but we are overwriting $~ and $_.  Leaving for now.
             DynamicScope scope = threadContext.getCurrentScope();
 
             // set global variables
             for (int i = 0, size = args.size(); i < size; i++) {
                 scope.setValue(i, JavaEmbedUtils.javaToRuby(runtime, args.get(i)), 0);
             }
 
         	// See eval todo about why this is commented out
             //runtime.setPosition(file, line);
 
             Node node = runtime.parse(file, funcBody.toString(), null);
-            return JavaEmbedUtils.rubyToJava(runtime, runtime.getTopSelf().eval(node), Object.class);
+            IRubyObject result = EvaluationState.eval(runtime, runtime.getCurrentContext(), node, runtime.getTopSelf(), Block.NULL_BLOCK);
+            return JavaEmbedUtils.rubyToJava(runtime, result, Object.class);
         } finally {
             threadContext.postBsfApply();
         }
     }
 
     public Object eval(String file, int line, int col, Object expr) throws BSFException {
         try {
         	// TODO: [JRUBY-24] This next line never would have worked correctly as a LexerSource
         	// would have thrown a parsing error with a name of "<script>" and a line
         	// value of whatever line in the string it is in.  Find real way of returning
         	// what is expected.
             //runtime.setPosition(file, line);
             IRubyObject result = runtime.evalScript(expr.toString());
             return JavaEmbedUtils.rubyToJava(runtime, result, Object.class);
         } catch (Exception excptn) {
             throw new BSFException(BSFException.REASON_EXECUTION_ERROR, "Exception", excptn);
         }
     }
 
     public void exec(String file, int line, int col, Object expr) throws BSFException {
         try {
         	// See eval todo about why this is commented out
             //runtime.setPosition(file, line);
             runtime.evalScript(expr.toString());
         } catch (Exception excptn) {
             throw new BSFException(BSFException.REASON_EXECUTION_ERROR, "Exception", excptn);
         }
     }
 
     public Object call(Object recv, String method, Object[] args) throws BSFException {
         try {
         	return JavaEmbedUtils.invokeMethod(runtime, recv, method, args, Object.class);
         } catch (Exception excptn) {
             printException(runtime, excptn);
             throw new BSFException(BSFException.REASON_EXECUTION_ERROR, excptn.getMessage(), excptn);
         }
     }
 
     public void initialize(BSFManager manager, String language, Vector someDeclaredBeans) throws BSFException {
         super.initialize(manager, language, someDeclaredBeans);
 
         runtime = JavaEmbedUtils.initialize(getClassPath(manager));
 
         for (int i = 0, size = someDeclaredBeans.size(); i < size; i++) {
             BSFDeclaredBean bean = (BSFDeclaredBean) someDeclaredBeans.elementAt(i);
             runtime.getGlobalVariables().define(
                 GlobalVariable.variableName(bean.name),
                 new BeanGlobalVariable(runtime, bean));
         }
 
         runtime.getGlobalVariables().defineReadonly("$bsf", new FunctionsGlobalVariable(runtime, new BSFFunctions(manager, this)));
     }
     
     private List getClassPath(BSFManager manager) {
     	return Arrays.asList(manager.getClassPath().split(System.getProperty("path.separator")));
     }
 
     public void declareBean(BSFDeclaredBean bean) throws BSFException {
         runtime.getGlobalVariables().define(
             GlobalVariable.variableName(bean.name),
             new BeanGlobalVariable(runtime, bean));
     }
 
     public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
         runtime.getGlobalVariables().set(GlobalVariable.variableName(bean.name), runtime.getNil());
     }
 
     public void handleException(BSFException bsfExcptn) {
         printException(runtime, (Exception) bsfExcptn.getTargetException());
     }
 
     /**
      *
      * Prints out an error message.
      *
      * @param exception An Exception thrown by JRuby
      */
     private static void printException(Ruby runtime, Exception exception) {
     	if (exception instanceof JumpException) {
 	    	JumpException je = (JumpException)exception;
 	    	if (je.getJumpType() == JumpException.JumpType.RaiseJump) {
 	            runtime.printError(((RaiseException)je).getException());
 	    	} else if (je.getJumpType() == JumpException.JumpType.ThrowJump) {
 	            runtime.getErrorStream().println("internal error: throw jump caught");
 	    	} else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
 	            runtime.getErrorStream().println("break without block.");
 	        } else if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
 	            runtime.getErrorStream().println("return without block.");
 	        }
     	}
     }
 
     private static class BeanGlobalVariable implements IAccessor {
         private Ruby runtime;
         private BSFDeclaredBean bean;
 
         public BeanGlobalVariable(Ruby runtime, BSFDeclaredBean bean) {
             this.runtime = runtime;
             this.bean = bean;
         }
 
         public IRubyObject getValue() {
             IRubyObject result = JavaUtil.convertJavaToRuby(runtime, bean.bean, bean.type);
             if (result instanceof JavaObject) {
                 return runtime.getModule("JavaUtilities").callMethod(runtime.getCurrentContext(), "wrap", result);
             }
             return result;
         }
 
         public IRubyObject setValue(IRubyObject value) {
             bean.bean = JavaUtil.convertArgument(Java.ruby_to_java(runtime.getObject(), value, Block.NULL_BLOCK), bean.type);
             return value;
         }
     }
 
     private static class FunctionsGlobalVariable implements IAccessor {
         private Ruby runtime;
         private BSFFunctions functions;
 
         public FunctionsGlobalVariable(Ruby runtime, BSFFunctions functions) {
             this.runtime = runtime;
             this.functions = functions;
         }
 
         public IRubyObject getValue() {
             IRubyObject result = JavaUtil.convertJavaToRuby(runtime, functions, BSFFunctions.class);
             if (result instanceof JavaObject) {
                 return runtime.getModule("JavaUtilities").callMethod(runtime.getCurrentContext(), "wrap", result);
             }
             return result;
         }
 
         public IRubyObject setValue(IRubyObject value) {
             return value;
         }
     }
 
     /**
      * @see org.apache.bsf.BSFEngine#terminate()
      */
     public void terminate() {
     	JavaEmbedUtils.terminate(runtime);
         runtime = null;
         super.terminate();
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index 3e38083b60..a5de78edf0 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,415 +1,578 @@
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
- * 
+ *
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
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.callback.Callback;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
+    /**
+     *
+     */
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * Return the ClassIndex value for the native type this object was
      * constructed from. Particularly useful for determining marshalling
-     * format. All instances of Hash instances of subclasses of Hash, for example
+     * format. All instances of subclasses of Hash, for example
      * are of Java type RubyHash, and so should utilize RubyHash marshalling
      * logic in addition to user-defined class marshalling logic.
-     * 
+     *
      * @return the ClassIndex of the native type this object was constructed from
      */
     int getNativeTypeIndex();
     
     /**
      * Gets a copy of the instance variables for this object, if any exist.
      * Returns null if this object has no instance variables.
      * "safe" in that it doesn't cause the instance var map to be created.
-     * 
+     *
      * @return A snapshot of the instance vars, or null if none.
      */
     Map safeGetInstanceVariables();
     
     /**
      * Returns true if the object has any instance variables, false otherwise.
      * "safe" in that it doesn't cause the instance var map to be created.
-     * 
+     *
      * @return true if the object has instance variables, false otherwise.
      */
     boolean safeHasInstanceVariables();
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
-
+    
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
+    /**
+     *
+     * @return
+     */
     Map getInstanceVariables();
+    /**
+     *
+     * @return
+     */
     Map getInstanceVariablesSnapshot();
-
+    
+    /**
+     *
+     * @param context
+     * @param rubyclass
+     * @param name
+     * @param args
+     * @param callType
+     * @param block
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType, Block block);
+    /**
+     *
+     * @param context
+     * @param rubyclass
+     * @param switchvalue
+     * @param name
+     * @param args
+     * @param callType
+     * @param block
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name, IRubyObject[] args, CallType callType, Block block);
-    
+    /**
+     *
+     * @param context
+     * @param switchValue
+     * @param name
+     * @param arg
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject arg);
+    /**
+     *
+     * @param context
+     * @param switchValue
+     * @param name
+     * @param args
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject[] args);
+    /**
+     *
+     * @param context
+     * @param switchValue
+     * @param name
+     * @param args
+     * @param callType
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject[] args, CallType callType);
-    
+    /**
+     *
+     * @param context
+     * @param name
+     * @param args
+     * @param callType
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
+    /**
+     *
+     * @param context
+     * @param name
+     * @param args
+     * @param callType
+     * @param block
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block);
-    
     // Used by the compiler, to allow visibility checks
+    /**
+     *
+     * @param context
+     * @param name
+     * @param args
+     * @param caller
+     * @param callType
+     * @param block
+     * @return
+     */
     IRubyObject compilerCallMethod(ThreadContext context, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
+    /**
+     *
+     * @param context
+     * @param methodIndex
+     * @param name
+     * @param args
+     * @param caller
+     * @param callType
+     * @param block
+     * @return
+     */
     IRubyObject compilerCallMethodWithIndex(ThreadContext context, byte methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
-    
+    /**
+     *
+     * @param context
+     * @param args
+     * @param block
+     * @return
+     */
     IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block);
-    
     /**
-     * RubyMethod funcall.
-     * @param context TODO
+     *
+     * @param context
      * @param string
-     * @return RubyObject
+     * @return
      */
     IRubyObject callMethod(ThreadContext context, String string);
+    /**
+     *
+     * @param context
+     * @param string
+     * @param aBlock
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, String string, Block aBlock);
-
     /**
-     * RubyMethod funcall.
-     * @param context TODO
+     *
+     * @param context
      * @param string
      * @param arg
-     * @return RubyObject
+     * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
-
     /**
-     * RubyMethod callMethod.
-     * @param context TODO
+     *
+     * @param context
      * @param method
      * @param rubyArgs
-     * @return IRubyObject
+     * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
+    /**
+     *
+     * @param context
+     * @param method
+     * @param rubyArgs
+     * @param block
+     * @return
+     */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs, Block block);
-
+    
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
-
+    
+    /**
+     *
+     * @return
+     */
     boolean isTrue();
-
+    
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
     
     /**
-     * Infect this object using the taint of another object
+     * RubyMethod setTaint.
+     * @param b
      */
-    IRubyObject infectBy(IRubyObject obj);
-
+    void setTaint(boolean b);
+    
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
-
+    
+    /**
+     *
+     * @return
+     */
     boolean isImmediate();
-
+    
+    /**
+     * RubyMethod isKindOf.
+     * @param rubyClass
+     * @return boolean
+     */
+    boolean isKindOf(RubyModule rubyClass);
+    
+    /**
+     * Infect this object using the taint of another object
+     * @param obj
+     * @return
+     */
+    IRubyObject infectBy(IRubyObject obj);
+    
     /**
      * RubyMethod getRubyClass.
+     * @return
      */
     RubyClass getMetaClass();
-
+    
+    /**
+     *
+     * @param metaClass
+     */
     void setMetaClass(RubyClass metaClass);
-
+    
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     RubyClass getSingletonClass();
-
+    
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
-
-    /**
-     * RubyMethod isKindOf.
-     * @param rubyClass
-     * @return boolean
-     */
-    boolean isKindOf(RubyModule rubyClass);
-
+    
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
-
+    
     /**
      * RubyMethod getRuntime.
+     * @return
      */
     Ruby getRuntime();
-
+    
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
-
-    /**
-     * RubyMethod eval.
-     * @param iNode
-     * @return IRubyObject
-     */
-    IRubyObject eval(Node iNode);
-
+    
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
-
+    
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
-     * @param binding The binding object under which to perform the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
-
-    /**
-     * RubyMethod extendObject.
-     * @param rubyModule
-     */
-    void extendObject(RubyModule rubyModule);
-
+    
     /**
      * Convert the object into a symbol name if possible.
-     * 
+     *
      * @return String the symbol name
      */
     String asSymbol();
-
+    
+    /** rb_obj_as_string
+     * @return
+     */
+    RubyString asString();
+    
     /**
      * Methods which perform to_xxx if the object has such a method
+     * @return
      */
     RubyArray convertToArray();
+    /**
+     *
+     * @return
+     */
     RubyFloat convertToFloat();
+    /**
+     *
+     * @return
+     */
     RubyInteger convertToInteger();
-    RubyString convertToString();
-
-    /** rb_obj_as_string
+    /**
+     *
+     * @return
      */
-    RubyString objAsString();
-
+    RubyString convertToString();
+    
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
-     * 
+     *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
-
+    
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
-     * 
+     *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
-
-
-    /**
-     * RubyMethod setTaint.
-     * @param b
-     */
-    void setTaint(boolean b);
-
-    /**
-     * RubyMethod checkSafeString.
-     */
-    void checkSafeString();
-
-    /**
-     * RubyMethod convertType.
-     * @param type
-     * @param string
-     * @param string1
-     */
-    IRubyObject convertType(Class type, String string, String string1);
-
+    
     /**
      * RubyMethod dup.
+     * @return
      */
     IRubyObject dup();
-
+    
     /**
      * RubyMethod setupClone.
      * @param original
      */
     void initCopy(IRubyObject original);
-
+    
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
-
+    
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
-
+    
     /**
      * Make sure the arguments fit the range specified by minimum and maximum.  On
      * a failure, The Ruby runtime will generate an ArgumentError.
-     * 
+     *
      * @param arguments to check
      * @param minimum number of args
      * @param maximum number of args (-1 for any number of args)
      * @return the number of arguments in args
      */
     int checkArgumentCount(IRubyObject[] arguments, int minimum, int maximum);
-
+    
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
-
-
+    
+    
+    /**
+     *
+     * @param args
+     * @param block
+     */
     public void callInit(IRubyObject[] args, Block block);
-
+    
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
-
     
+    
+    /**
+     *
+     * @return
+     */
     boolean isSingleton();
-	Iterator instanceVariableNames();
-
+    
+    /**
+     *
+     * @return
+     */
+    Iterator instanceVariableNames();
+    
     /**
      * rb_scan_args
      *
      * This method will take the arguments specified, fill in an array and return it filled
      * with nils for every argument not provided. It's guaranteed to always return a new array.
-     * 
+     *
      * @param args the arguments to check
      * @param required the amount of required arguments
      * @param optional the amount of optional arguments
      * @return a new array containing all arguments provided, and nils in those spots not provided.
-     * 
+     *
      */
     IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional);
-
+    
     /**
      * Our version of Data_Wrap_Struct.
      *
      * This method will just set a private pointer to the object provided. This pointer is transient
      * and will not be accessible from Ruby.
      *
      * @param obj the object to wrap
      */
     void dataWrapStruct(Object obj);
-
+    
     /**
      * Our version of Data_Get_Struct.
      *
      * Returns a wrapped data value if there is one, otherwise returns null.
      *
      * @return the object wrapped.
      */
     Object dataGetStruct();
-
+    
+    /**
+     *
+     * @return
+     */
     RubyFixnum id();
-
+    
+    /**
+     *
+     * @return
+     */
     IRubyObject anyToString();
-
+    
+    /**
+     *
+     * @return
+     */
     IRubyObject checkStringType();
-
+    
+    /**
+     *
+     * @return
+     */
     IRubyObject checkArrayType();
     
+    /**
+     *
+     * @param context
+     * @param other
+     * @return
+     */
     IRubyObject equalInternal(final ThreadContext context, final IRubyObject other);
-
+    
+    /**
+     *
+     */
     void attachToObjectSpace();
-
+    
+    /**
+     *
+     * @param args
+     * @param block
+     * @return
+     */
     IRubyObject send(IRubyObject[] args, Block block);
+    /**
+     *
+     * @param method
+     * @return
+     */
     IRubyObject method(IRubyObject method);
 }
diff --git a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
index d83df57746..c9eb0ce665 100644
--- a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
@@ -1,704 +1,704 @@
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
 
 import org.jruby.Ruby;
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
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.Sprintf;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class FileMetaClass extends IOMetaClass {
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
     
     public FileMetaClass(Ruby runtime) {
         super("File", RubyFile.class, runtime.getClass("IO"), FILE_ALLOCATOR);
     }
     
     public FileMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         super(name, RubyFile.class, superClass, allocator, parentCRef);
     }
     
     protected class FileMeta extends Meta {
         protected void initializeClass() {
             Ruby runtime = getRuntime();
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
             // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
             // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
             setConstant("BINARY", runtime.newFixnum(IOModes.BINARY));
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
             
-            extendObject(runtime.getModule("FileTest"));
+            runtime.getModule("FileTest").extend_object(FileMetaClass.this);
             
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
             defineFastSingletonMethod("symlink", Arity.twoArguments(), "symlink");
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
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
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
                 Process chmod = Runtime.getRuntime().exec("chmod " + Sprintf.sprintf(getRuntime(), "%o", mode.getLongValue()) + " " + filename);
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
         String name = filename.toString().replace('\\', '/');
         if (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             name = name.substring(0, name.length() - 1);
         }
         //TODO deal with drive letters A: and UNC names
         int index = name.lastIndexOf('/');
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
         
         // trim off dir name, since it may have dots in it
         //TODO deal with drive letters A: and UNC names
         int index = name.lastIndexOf('/');
         if (index == -1) {
             // XXX actually, only on windows...
             index = name.lastIndexOf('\\');
         }
         name = name.substring(index + 1);
         
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
             try {
                 return getRuntime().newString(JRubyFile.create(relativePath, "").getCanonicalPath());
             } catch(IOException e) {
                 return getRuntime().newString(relativePath);
             }
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
     
     public IRubyObject open(IRubyObject[] args, Block block) {
         return open(args, true, block);
     }
     
     public IRubyObject open(IRubyObject[] args, boolean tryToYield, Block block) {
         checkArgumentCount(args, 1, -1);
         Ruby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyString pathString = RubyString.stringValue(args[0]);
-        pathString.checkSafeString();
+        runtime.checkSafeString(pathString);
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
         
         if (tryToYield && block.isGiven()) {
             try {
                 return block.yield(tc, file);
             } finally {
                 file.close();
             }
         }
         
         return file;
     }
     
     public IRubyObject rename(IRubyObject oldName, IRubyObject newName) {
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
-        oldNameString.checkSafeString();
-        newNameString.checkSafeString();
+        getRuntime().checkSafeString(oldNameString);
+        getRuntime().checkSafeString(newNameString);
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
     
     public IRubyObject symlink(IRubyObject from, IRubyObject to) {
         try {
             Ruby runtime = getRuntime();
             int result = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {
                 runtime.newString("ln"), runtime.newString("-s"), from, to
             });
             return runtime.newFixnum(result);
         } catch (Exception e) {
             throw getRuntime().newNotImplementedError("symlinks");
         }
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
         RubyFile file = (RubyFile) open(args, false, null);
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
-            filename.checkSafeString();
+            getRuntime().checkSafeString(filename);
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
-            filename.checkSafeString();
+            getRuntime().checkSafeString(filename);
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
diff --git a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
index cbf0d20878..9dfc2a4866 100644
--- a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
@@ -1,388 +1,388 @@
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
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
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
 
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.Selector;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Channel;
 import java.nio.channels.Pipe;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.RubyKernel;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOHandler;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class IOMetaClass extends ObjectMetaClass {
     
     public IOMetaClass(Ruby runtime) {
         this("IO", RubyIO.class, runtime.getObject(), IO_ALLOCATOR);
     }
     
     public IOMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         this(name, RubyIO.class, superClass, allocator, parentCRef);
     }
     
     public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
         super(name, clazz, superClass, allocator);
     }
     
     public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         super(name, clazz, superClass, allocator, parentCRef);
     }
     
     protected class IOMeta extends Meta {
         protected void initializeClass() {
             includeModule(getRuntime().getModule("Enumerable"));
             
             // TODO: Implement tty? and isatty.  We have no real capability to
             // determine this from java, but if we could set tty status, then
             // we could invoke jruby differently to allow stdin to return true
             // on this.  This would allow things like cgi.rb to work properly.
             
             defineSingletonMethod("foreach", Arity.optional());
             defineSingletonMethod("read", Arity.optional());
             defineSingletonMethod("readlines", Arity.optional());
             defineSingletonMethod("popen", Arity.optional());
             defineFastSingletonMethod("select", Arity.optional());
             defineFastSingletonMethod("pipe", Arity.noArguments());
             
             defineFastMethod("<<", Arity.singleArgument(), "addString");
             defineFastMethod("binmode", Arity.noArguments());
             defineFastMethod("close", Arity.noArguments());
             defineFastMethod("closed?", Arity.noArguments(), "closed");
             defineMethod("each", Arity.optional(), "each_line");
             defineMethod("each_byte", Arity.noArguments());
             defineMethod("each_line", Arity.optional());
             defineFastMethod("eof", Arity.noArguments());
             defineAlias("eof?", "eof");
             defineFastMethod("fcntl", Arity.twoArguments());
             defineFastMethod("fileno", Arity.noArguments());
             defineFastMethod("flush", Arity.noArguments());
             defineFastMethod("fsync", Arity.noArguments());
             defineFastMethod("getc", Arity.noArguments());
             defineFastMethod("gets", Arity.optional());
             defineMethod("initialize", Arity.optional());
             defineFastMethod("initialize_copy", Arity.singleArgument());
             defineFastMethod("lineno", Arity.noArguments());
             defineFastMethod("lineno=", Arity.singleArgument(), "lineno_set");
             defineFastMethod("pid", Arity.noArguments());
             defineFastMethod("pos", Arity.noArguments());
             defineFastMethod("pos=", Arity.singleArgument(), "pos_set");
             defineFastMethod("print", Arity.optional());
             defineFastMethod("printf", Arity.optional());
             defineFastMethod("putc", Arity.singleArgument());
             defineFastMethod("puts", Arity.optional());
             defineFastMethod("readpartial", Arity.optional());
             defineFastMethod("read", Arity.optional());
             defineFastMethod("readchar", Arity.noArguments());
             defineFastMethod("readline", Arity.optional());
             defineFastMethod("readlines", Arity.optional());
             defineFastMethod("reopen", Arity.optional());
             defineFastMethod("rewind", Arity.noArguments());
             defineFastMethod("seek", Arity.optional());
             defineFastMethod("sync", Arity.noArguments());
             defineFastMethod("sync=", Arity.singleArgument(), "sync_set");
             defineFastMethod("sysread", Arity.singleArgument());
             defineFastMethod("syswrite", Arity.singleArgument());
             defineAlias("tell", "pos");
             defineAlias("to_i", "fileno");
             defineFastMethod("to_io", Arity.noArguments());
             defineFastMethod("ungetc", Arity.singleArgument());
             defineFastMethod("write", Arity.singleArgument());
             defineFastMethod("tty?", Arity.noArguments(), "tty");
             defineAlias("isatty", "tty?");
             
             // Constants for seek
             setConstant("SEEK_SET", getRuntime().newFixnum(IOHandler.SEEK_SET));
             setConstant("SEEK_CUR", getRuntime().newFixnum(IOHandler.SEEK_CUR));
             setConstant("SEEK_END", getRuntime().newFixnum(IOHandler.SEEK_END));
         }
     };
     
     protected Meta getMeta() {
         return new IOMeta();
     }
     
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
         return new IOMetaClass(name, this, IO_ALLOCATOR, parentCRef);
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
     
     /** rb_io_s_foreach
      *
      */
     public IRubyObject foreach(IRubyObject[] args, Block block) {
         int count = checkArgumentCount(args, 1, -1);
         IRubyObject filename = args[0].convertToString();
-        filename.checkSafeString();
+        getRuntime().checkSafeString(filename);
         RubyIO io = (RubyIO) ((FileMetaClass) getRuntime().getClass("File")).open(new IRubyObject[] { filename }, false, block);
         
         if (!io.isNil() && io.isOpen()) {
             try {
                 IRubyObject[] newArgs = new IRubyObject[count - 1];
                 System.arraycopy(args, 1, newArgs, 0, count - 1);
                 
                 IRubyObject nextLine = io.internalGets(newArgs);
                 while (!nextLine.isNil()) {
                     block.yield(getRuntime().getCurrentContext(), nextLine);
                     nextLine = io.internalGets(newArgs);
                 }
             } finally {
                 io.close();
             }
         }
         
         return getRuntime().getNil();
     }
     
     private static void registerSelect(Selector selector, IRubyObject obj, int ops) throws IOException {
         RubyIO ioObj;
         
         if(!(obj instanceof RubyIO)) {
             // invoke to_io
             if(!obj.respondsTo("to_io")) {
                 return;
             }
             ioObj = (RubyIO) obj.callMethod(obj.getRuntime().getCurrentContext(), "to_io");
         } else {
             ioObj = (RubyIO) obj;
         }
         
         Channel channel = ioObj.getChannel();
         if(channel == null || !(channel instanceof SelectableChannel)) {
             return;
         }
         
         ((SelectableChannel) channel).configureBlocking(false);
         int real_ops = ((SelectableChannel) channel).validOps() & ops;
         SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
         
         if(key == null) {
             ((SelectableChannel) channel).register(selector, real_ops, obj);
         } else {
             key.interestOps(key.interestOps()|real_ops);
         }
     }
     
     public IRubyObject select(IRubyObject[] args) {
         return select_static(getRuntime(), args);
     }
     
     public static IRubyObject select_static(Ruby runtime, IRubyObject[] args) {
         try {
             boolean atLeastOneDescriptor = false;
             
             Selector selector = Selector.open();
             if (!args[0].isNil()) {
                 atLeastOneDescriptor = true;
                 
                 // read
                 for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                     IRubyObject obj = (IRubyObject) i.next();
                     registerSelect(selector, obj, SelectionKey.OP_READ|SelectionKey.OP_ACCEPT);
                 }
             }
             if (args.length > 1 && !args[1].isNil()) {
                 atLeastOneDescriptor = true;
                 // write
                 for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                     IRubyObject obj = (IRubyObject) i.next();
                     registerSelect(selector, obj, SelectionKey.OP_WRITE);
                 }
             }
             if (args.length > 2 && !args[2].isNil()) {
                 atLeastOneDescriptor = true;
                 // Java's select doesn't do anything about this, so we leave it be.
             }
             
             long timeout = 0;
             if(args.length > 3 && !args[3].isNil()) {
                 if (args[3] instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat) args[3]).getDoubleValue() * 1000);
                 } else {
                     timeout = Math.round(((RubyFixnum) args[3]).getDoubleValue() * 1000);
                 }
                 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
             
             if (!atLeastOneDescriptor) {
                 return runtime.getNil();
             }
             
             if(args.length > 3) {
                 selector.select(timeout);
             } else {
                 selector.select();
             }
             
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                 SelectionKey key = (SelectionKey) i.next();
                 if ((key.interestOps() & key.readyOps()
                         & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                     r.add(key.attachment());
                 }
                 if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                     w.add(key.attachment());
                 }
             }
             
             // make all sockets blocking as configured again
             for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                 SelectionKey key = (SelectionKey) i.next();
                 SelectableChannel channel = key.channel();
                 synchronized(channel.blockingLock()) {
                     boolean blocking = ((RubyIO) key.attachment()).getBlocking();
                     key.cancel();
                     channel.configureBlocking(blocking);
                 }
             }
             selector.close();
             
             if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                 return runtime.getNil();
             }
             
             List ret = new ArrayList();
             
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
             
             return RubyArray.newArray(runtime, ret);
         } catch(IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
     
     public IRubyObject read(IRubyObject[] args, Block block) {
         checkArgumentCount(args, 1, 3);
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments, block);
         IRubyObject[] readArguments;
         
         if (args.length >= 2) {
             readArguments = new IRubyObject[] {args[1].convertToType("Fixnum", "to_int", true)};
         } else {
             readArguments = new IRubyObject[] {};
         }
         
         try {
             
             if (args.length == 3) {
                 file.seek(new IRubyObject[] {args[2].convertToType("Fixnum", "to_int", true)});
             }
             
             return file.read(readArguments);
         } finally {
             file.close();
         }
     }
     
     public RubyArray readlines(IRubyObject[] args, Block block) {
         int count = checkArgumentCount(args, 1, 2);
         
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments, block);
         try {
             return file.readlines(separatorArguments);
         } finally {
             file.close();
         }
     }
     
     //XXX Hacked incomplete popen implementation to make
     public IRubyObject popen(IRubyObject[] args, Block block) {
         Ruby runtime = getRuntime();
         checkArgumentCount(args, 1, 2);
         IRubyObject cmdObj = args[0].convertToString();
-        cmdObj.checkSafeString();
+        getRuntime().checkSafeString(cmdObj);
         ThreadContext tc = runtime.getCurrentContext();
         
         try {
             Process process = new ShellLauncher(runtime).run(cmdObj);            
             RubyIO io = new RubyIO(runtime, process);
             
             if (block.isGiven()) {
                 try {
                     block.yield(tc, io);
                     return runtime.getNil();
                 } finally {
                     io.close();
                     runtime.getGlobalVariables().set("$?", runtime.newFixnum(process.waitFor() * 256));
                 }
             }
             return io;
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
     
     // NIO based pipe
     public IRubyObject pipe() throws Exception {
         Ruby runtime = getRuntime();
         Pipe pipe = Pipe.open();
         return runtime.newArrayNoCopy(new IRubyObject[]{
             new RubyIO(runtime, pipe.source()),
             new RubyIO(runtime, pipe.sink())
         });
     }
 }
diff --git a/src/org/jruby/util/Sprintf.java b/src/org/jruby/util/Sprintf.java
index b5533ca6b3..0319fc9ae9 100644
--- a/src/org/jruby/util/Sprintf.java
+++ b/src/org/jruby/util/Sprintf.java
@@ -1,1419 +1,1419 @@
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
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import java.math.BigInteger;
 import java.text.DecimalFormatSymbols;
 import java.util.List;
 import java.util.Locale;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyKernel;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.builtin.IRubyObject;
 
 
 /**
  * @author Bill Dortch
  *
  */
 public class Sprintf {
     private static final int FLAG_NONE        = 0;
     private static final int FLAG_SPACE       = 1 << 0;
     private static final int FLAG_ZERO        = 1 << 1;
     private static final int FLAG_PLUS        = 1 << 2;
     private static final int FLAG_MINUS       = 1 << 3;
     private static final int FLAG_SHARP       = 1 << 4;
     private static final int FLAG_WIDTH       = 1 << 5;
     private static final int FLAG_PRECISION   = 1 << 6;
     
     private static final byte[] PREFIX_OCTAL     = {'0'};
     private static final byte[] PREFIX_HEX_LC    = {'0','x'};
     private static final byte[] PREFIX_HEX_UC    = {'0','X'};
     private static final byte[] PREFIX_BINARY_LC = {'0','b'};
     private static final byte[] PREFIX_BINARY_UC = {'0','B'};
     
     private static final byte[] PREFIX_NEGATIVE = {'.','.'};
     
     private static final byte[] NAN_VALUE       = {'N','a','N'};
     private static final byte[] INFINITY_VALUE  = {'I','n','f'};
        
     private static final BigInteger BIG_32 = BigInteger.valueOf(((long)Integer.MAX_VALUE + 1L) << 1);
     private static final BigInteger BIG_64 = BIG_32.shiftLeft(32);
     private static final BigInteger BIG_MINUS_32 = BigInteger.valueOf((long)Integer.MIN_VALUE << 1);
     private static final BigInteger BIG_MINUS_64 = BIG_MINUS_32.shiftLeft(32);
 
     private static final int INITIAL_BUFFER_SIZE = 128; // try to avoid reallocs
     
     
     private static class Args {
         Ruby runtime;
         Locale locale;
         IRubyObject rubyObject;
         List rubyArray;
         int length;
         int unnumbered; // last index (+1) accessed by next()
         int numbered;   // last index (+1) accessed by get()
         
         Args(Locale locale, IRubyObject rubyObject) {
             if (rubyObject == null) {
                 throw new IllegalArgumentException("null IRubyObject passed to sprintf");
             }
             this.locale = locale == null ? Locale.getDefault() : locale;
             this.rubyObject = rubyObject;
             if (rubyObject instanceof RubyArray) {
                 this.rubyArray = ((RubyArray)rubyObject).getList();
                 this.length = rubyArray.size();
             } else {
                 this.length = 1;
             }
             this.runtime = rubyObject.getRuntime();
         }
         
         Args(IRubyObject rubyObject) {
             this(Locale.getDefault(),rubyObject);
         }
 
         // temporary hack to handle non-Ruby values
         // will come up with better solution shortly
         Args(Ruby runtime, long value) {
             this(RubyFixnum.newFixnum(runtime,value));
         }
         
         final void raiseArgumentError(String message) {
             throw runtime.newArgumentError(message);
         }
         
         final void warn(String message) {
             runtime.getWarnings().warn(message);
         }
         
         final void warning(String message) {
             runtime.getWarnings().warning(message);
         }
         
         final IRubyObject next() {
             // this is the order in which MRI does these two tests
             if (numbered > 0) {
                 raiseArgumentError("unnumbered" + (unnumbered + 1) + "mixed with numbered");
             }
             
             if (unnumbered >= length) raiseArgumentError("too few arguments");
 
             IRubyObject object = rubyArray == null ? rubyObject : 
                 (IRubyObject)rubyArray.get(unnumbered);
             unnumbered++;
             return object;
         }
         
         final IRubyObject get(int index) {
             // this is the order in which MRI does these tests
             if (unnumbered > 0) {
                 raiseArgumentError("numbered("+(numbered+1)+") after unnumbered("+unnumbered+")");
             }
             if (index < 0) raiseArgumentError("invalid index - " + (index + 1) + '$');
             if (index >= length) raiseArgumentError("too few arguments");
 
             numbered = index + 1;
             return (rubyArray == null ? rubyObject : (IRubyObject)rubyArray.get(index));
         }
         
         final IRubyObject getNth(int formatIndex) {
             return get(formatIndex - 1);
         }
         
         final int nextInt() {
             return intValue(next());
         }
         
         final int getInt(int index) {
             return intValue(get(index));
         }
         
         final int getNthInt(int formatIndex) {
             return intValue(get(formatIndex - 1));
         }
         
         final int intValue(IRubyObject obj) {
             if (obj instanceof RubyNumeric) return (int)((RubyNumeric)obj).getLongValue();
 
             // basically just forcing a TypeError here to match MRI
             obj = obj.convertToType("Fixnum", "to_int", true);
             return (int)((RubyFixnum)obj).getLongValue();
         }
         
         final byte getDecimalSeparator() {
             // not saving DFS instance, as it will only be used once (at most) per call
             return (byte)new DecimalFormatSymbols(locale).getDecimalSeparator();
         }
     } // Args
 
     /*
      * Using this class to buffer output during formatting, rather than
      * the eventual ByteList itself. That way this buffer can be initialized
      * to a size large enough to prevent reallocations (in most cases), while
      * the resultant ByteList will only be as large as necessary.
      * 
      * (Also, the Buffer class's buffer grows by a factor of 2, as opposed
      * to ByteList's 1.5, which I felt might result in a lot of reallocs.)
      */
     private static class Buffer {
         byte[] buf;
         int size;
          
         Buffer() {
             buf = new byte[INITIAL_BUFFER_SIZE];
         }
         
         Buffer(int initialSize) {
             buf = new byte[initialSize];
         }
         
         final void write(int b) {
             int newSize = size + 1;
             if (newSize > buf.length) {
                 byte[] newBuf = new byte[Math.max(buf.length << 1,newSize)];
                 System.arraycopy(buf,0,newBuf,0,size);
                 buf = newBuf;
             }
             buf[size] = (byte)(b & 0xff);
             size = newSize;
         }
         
         final void write(byte[] b, int off, int len) {
             if (len <=0 || off < 0) return;
             
             int newSize = size + len;
             if (newSize > buf.length) {
                 byte[] newBuf = new byte[Math.max(buf.length << 1,newSize)];
                 System.arraycopy(buf,0,newBuf,0,size);
                 buf = newBuf;
             }
             System.arraycopy(b,off,buf,size,len);
             size = newSize;
         }
         
         final void write(byte[] b) {
             write(b,0,b.length);
         }
         
         final void fill(int b, int len) {
             if (len <= 0) return;
             
             int newSize = size + len;
             if (newSize > buf.length) {
                 byte[] newBuf = new byte[Math.max(buf.length << 1,newSize)];
                 System.arraycopy(buf,0,newBuf,0,size);
                 buf = newBuf;
             }
             byte fillval = (byte)(b & 0xff);
             for ( ; --len >= 0; ) {
                 buf[size+len] = fillval;
             }
             size = newSize;
         }
         
         final void set(int b, int pos) {
             if (pos < 0) pos += size;
             if (pos >= 0 && pos < size) buf[pos] = (byte)(b & 0xff);
         }
         
         // sets last char
         final void set(int b) {
             if (size > 0) buf[size-1] = (byte)(b & 0xff);
         }
         
         final ByteList toByteList() {
             return new ByteList(buf,0,size);
         }
         
         public final String toString() {
             return new String(buf,0,size);
         }
     } // Buffer
 
     // static methods only
     private Sprintf () {}
     
     public static CharSequence sprintf(Locale locale, CharSequence format, IRubyObject args) {
         return rubySprintf(format, new Args(locale,args));
     }
 
     public static CharSequence sprintf(CharSequence format, IRubyObject args) {
         return rubySprintf(format, new Args(args));
     }
     
     public static CharSequence sprintf(Ruby runtime, CharSequence format, int arg) {
         return rubySprintf(format, new Args(runtime,(long)arg));
     }
     
     public static CharSequence sprintf(Ruby runtime, CharSequence format, long arg) {
         return rubySprintf(format, new Args(runtime,arg));
     }
     
     public static CharSequence sprintf(Locale locale, RubyString format, IRubyObject args) {
         return rubySprintf(format.getByteList(), new Args(locale,args));
     }
     
     public static CharSequence sprintf(RubyString format, IRubyObject args) {
         return rubySprintf(format.getByteList(), new Args(args));
     }
     
     private static CharSequence rubySprintf(CharSequence charFormat, Args args) {
         byte[] format;
         Buffer buf = new Buffer();
         if (charFormat instanceof ByteList) {
             format = ((ByteList)charFormat).unsafeBytes();
         } else {
             format = stringToBytes(charFormat,false);
         }
         int offset = 0;
         int length = charFormat.length();
         int start = 0;
         int mark = 0;
         while (offset < length) {
             start = offset;
             for ( ; offset < length && format[offset] != '%'; offset++) ;
             if (offset > start) {
                 buf.write(format,start,offset-start);
                 //showLiteral(format,start,offset);
                 start = offset;
             }
             if (offset >= length)
                 break;
             checkOffset(args,++offset,length);
 
             IRubyObject arg = null;
             int flags = 0;
             int width = 0;
             int precision = 0;
             int number = 0;
             byte fchar = 0;
             boolean incomplete = true;
             for ( ; incomplete && offset < length ; ) {
                 switch(fchar = format[offset]) {
                 default:
                     if (isPrintable(fchar)) {
                         raiseArgumentError(args,"malformed format string - %" + (char)fchar);
                     } else {
                         raiseArgumentError(args,"malformed format string");
                     }
                     break;
 
                 case ' ':
                     flags |= FLAG_SPACE;
                     checkOffset(args,++offset,length);
                     break;
                 case '0':
                     flags |= FLAG_ZERO;
                     checkOffset(args,++offset,length);
                     break;
                 case '+':
                     flags |= FLAG_PLUS;
                     checkOffset(args,++offset,length);
                     break;
                 case '-':
                     flags |= FLAG_MINUS;
                     checkOffset(args,++offset,length);
                     break;
                 case '#':
                     flags |= FLAG_SHARP;
                     checkOffset(args,++offset,length);
                     break;
                 case '1':
                 case '2':
                 case '3':
                 case '4':
                 case '5':
                 case '6':
                 case '7':
                 case '8':
                 case '9':
                     // MRI doesn't flag it as an error if width is given multiple
                     // times as a number (but it does for *)
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args, number, fchar);
                     }
                     
                     if (offset >= length) raiseArgumentError(args, "malformed format string - %[0-9]");
 
                     if (fchar == '$') {
                         if (arg != null) {
                             raiseArgumentError(args,"value given twice - " + number + "$");
                         }
                         arg = args.getNth(number);
                         checkOffset(args, ++offset, length);
                     } else {
                         width = number;
                         checkOffset(args, offset, length);
                         flags |= FLAG_WIDTH;
                     }
                     break;
                 
                 case '*':
                     if ((flags & FLAG_WIDTH) != 0) raiseArgumentError(args,"width given twice");
 
                     flags |= FLAG_WIDTH;
                     // TODO: factor this chunk as in MRI/YARV GETASTER
                     checkOffset(args,++offset,length);
                     mark = offset;
                     number = 0;
                     for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                         number = extendWidth(args,number,fchar);
                     }
                     if (offset >= length) {
                         raiseArgumentError(args,"malformed format string - %*[0-9]");
                     }
                     if (fchar == '$') {
                         width = args.getNthInt(number);
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         checkOffset(args,++offset,length);
                     } else {
                         width = args.nextInt();
                         if (width < 0) {
                             flags |= FLAG_MINUS;
                             width = -width;
                         }
                         // let the width (if any), get processed in the next loop,
                         // so any leading 0 gets treated correctly 
                         offset = mark;
                     }
                     break;
                 
                 case '.':
                     if ((flags & FLAG_PRECISION) != 0) {
                         raiseArgumentError(args,"precision given twice");
                     }
                     flags |= FLAG_PRECISION;
                     checkOffset(args,++offset,length);
                     fchar = format[offset];
                     if (fchar == '*') {
                         // TODO: factor this chunk as in MRI/YARV GETASTER
                         checkOffset(args,++offset,length);
                         mark = offset;
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         if (offset >= length) {
                             raiseArgumentError(args,"malformed format string - %*[0-9]");
                         }
                         if (fchar == '$') {
                             precision = args.getNthInt(number);
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             checkOffset(args,++offset,length);
                         } else {
                             precision = args.nextInt();
                             if (precision < 0) {
                                 flags &= ~FLAG_PRECISION;
                             }
                             // let the width (if any), get processed in the next loop,
                             // so any leading 0 gets treated correctly 
                             offset = mark;
                         }
                     } else {
                         number = 0;
                         for ( ; offset < length && isDigit(fchar = format[offset]); offset++) {
                             number = extendWidth(args,number,fchar);
                         }
                         if (offset >= length) {
                             raiseArgumentError(args,"malformed format string - %.[0-9]");
                         }
                         precision = number;
                     }
                     break;
 
                 case '\n':
                     offset--;
                 case '\0':
                 case '%':
                     if (flags != FLAG_NONE) {
                         raiseArgumentError(args,"illegal format character - %");
                     }
                     buf.write('%');
                     offset++;
                     incomplete = false;
                     break;
 
                 case 'c': {
                     if (arg == null) arg = args.next();
                     
                     int c = 0;
                     // MRI 1.8.5-p12 doesn't support 1-char strings, but
                     // YARV 0.4.1 does. I don't think it hurts to include
                     // this; sprintf('%c','a') is nicer than sprintf('%c','a'[0])
                     if (arg instanceof RubyString) {
                         ByteList bytes = ((RubyString)arg).getByteList();
                         if (bytes.length() == 1) {
                             c = bytes.unsafeBytes()[0];
                         } else {
                             raiseArgumentError(args,"%c requires a character");
                         }
                     } else {
                         c = args.intValue(arg);
                     }
                     if ((flags & FLAG_WIDTH) != 0 && width > 1) {
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.write(c);
                             buf.fill(' ', width-1);
                         } else {
                             buf.fill(' ',width-1);
                             buf.write(c);
                         }
                     } else {
                         buf.write(c);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'p':
                 case 's': {
                     if (arg == null) arg = args.next();
 
                     if (fchar == 'p') {
                         arg = arg.callMethod(arg.getRuntime().getCurrentContext(),"inspect");
                     }
-                    ByteList bytes = arg.objAsString().getByteList();
+                    ByteList bytes = arg.asString().getByteList();
                     int len = bytes.length();
                     if ((flags & FLAG_PRECISION) != 0 && precision < len) {
                         len = precision;
                     }
                     // TODO: adjust length so it won't fall in the middle 
                     // of a multi-byte character. MRI's sprintf.c uses tables
                     // in a modified version of regex.c, which assume some
                     // particular  encoding for a given installation/application.
                     // (See regex.c#re_mbcinit in ruby-1.8.5-p12) 
                     //
                     // This is only an issue if the user specifies a precision
                     // that causes the string to be truncated. The same issue
                     // would arise taking a substring of a ByteList-backed RubyString.
 
                     if ((flags & FLAG_WIDTH) != 0 && width > len) {
                         width -= len;
                         if ((flags & FLAG_MINUS) != 0) {
                             buf.write(bytes.unsafeBytes(),0,len);
                             buf.fill(' ',width);
                         } else {
                             buf.fill(' ',width);
                             buf.write(bytes.unsafeBytes(),0,len);
                         }
                     } else {
                         buf.write(bytes.unsafeBytes(),0,len);
                     }
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'd':
                 case 'i':
                 case 'o':
                 case 'x':
                 case 'X':
                 case 'b':
                 case 'B':
                 case 'u': {
                     if (arg == null) arg = args.next();
 
                     int type = arg.getMetaClass().index;
                     if (type != ClassIndex.FIXNUM && type != ClassIndex.BIGNUM) {
                         switch(type) {
                         case ClassIndex.FLOAT:
                             arg = RubyNumeric.dbl2num(arg.getRuntime(),((RubyFloat)arg).getValue());
                             break;
                         case ClassIndex.STRING:
                             arg = RubyNumeric.str2inum(arg.getRuntime(),(RubyString)arg,0,true);
                             break;
                         default:
                             arg = arg.convertToType("Integer","to_i",true);
                             break;
                         }
                         type = arg.getMetaClass().index;
                     }
                     byte[] bytes = null;
                     int first = 0;
                     byte[] prefix = null;
                     boolean sign;
                     boolean negative;
                     byte signChar = 0;
                     byte leadChar = 0;
                     int base;
 
                     // 'd' and 'i' are the same
                     if (fchar == 'i') fchar = 'd';
 
                     // 'u' with space or plus flags is same as 'd'
                     if (fchar == 'u' && (flags & (FLAG_SPACE | FLAG_PLUS)) != 0) {
                         fchar = 'd';
                     }
                     sign = (fchar == 'd' || (flags & (FLAG_SPACE | FLAG_PLUS)) != 0);
 
                     switch (fchar) {
                     case 'o':
                         base = 8; break;
                     case 'x':
                     case 'X':
                         base = 16; break;
                     case 'b':
                     case 'B':
                         base = 2; break;
                     case 'u':
                     case 'd':
                     default:
                         base = 10; break;
                     }
                     if ((flags & FLAG_SHARP) != 0) {
                         switch(fchar) {
                         case 'o': prefix = PREFIX_OCTAL; break;
                         case 'x': prefix = PREFIX_HEX_LC; break;
                         case 'X': prefix = PREFIX_HEX_UC; break;
                         case 'b': prefix = PREFIX_BINARY_LC; break;
                         case 'B': prefix = PREFIX_BINARY_UC; break;
                         }
                         if (prefix != null) width -= prefix.length;
                     }
                     // We depart here from strict adherence to MRI code, as MRI
                     // uses C-sprintf, in part, to format numeric output, while
                     // we'll use Java's numeric formatting code (and our own).
                     if (type == ClassIndex.FIXNUM) {
                         negative = ((RubyFixnum)arg).getLongValue() < 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyFixnum)arg);
                         } else {
                             bytes = getFixnumBytes((RubyFixnum)arg,base,sign,fchar=='X');
                         }
                     } else {
                         negative = ((RubyBignum)arg).getValue().signum() < 0;
                         if (negative && fchar == 'u') {
                             bytes = getUnsignedNegativeBytes((RubyBignum)arg);
                         } else {
                             bytes = getBignumBytes((RubyBignum)arg,base,sign,fchar=='X');
                         }
                     }
                     int len = 0;
                     if (sign) {
                         if (negative) {
                             signChar = '-';
                             width--;
                             first = 1; // skip '-' in bytes, will add where appropriate
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         }
                     } else if (negative) {
                         if (base == 10) {
                             warning(args,"negative number for %u specifier");
                             leadChar = '.';
                             len += 2;
                         } else {
                             if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0) len += 2; // ..
 
                             first = skipSignBits(bytes,base);
                             switch(fchar) {
                             case 'b':
                             case 'B':
                                 leadChar = '1';
                                 break;
                             case 'o':
                                 leadChar = '7';
                                 break;
                             case 'x':
                                 leadChar = 'f';
                                 break;
                             case 'X':
                                 leadChar = 'F';
                                 break;
                             }
                             if (leadChar != 0) len++;
                         }
                     }
                     int numlen = bytes.length - first;
                     len += numlen;
                     
                     if ((flags & (FLAG_ZERO|FLAG_PRECISION)) == FLAG_ZERO) {
                         precision = width;
                         width = 0;
                     } else {
                         if (precision < len) precision = len;
 
                         width -= precision;
                     }
                     if ((flags & FLAG_MINUS) == 0) {
                         buf.fill(' ',width);
                         width = 0;
                     }
                     if (signChar != 0) buf.write(signChar);
                     if (prefix != null) buf.write(prefix);
 
                     if (len < precision) {
                         if (leadChar == 0) {
                             buf.fill('0', precision - len);
                         } else if (leadChar == '.') {
                             buf.fill(leadChar,precision-len);
                             buf.write(PREFIX_NEGATIVE);
                         } else {
                             buf.fill(leadChar,precision-len+1); // the 1 is for the stripped sign char
                         }
                     } else if (leadChar != 0) {
                         if ((flags & (FLAG_PRECISION | FLAG_ZERO)) == 0) {
                             buf.write(PREFIX_NEGATIVE);
                         }
                         if (leadChar != '.') buf.write(leadChar);
                     }
                     buf.write(bytes,first,numlen);
 
                     if (width > 0) buf.fill(' ',width);
                                         
                     offset++;
                     incomplete = false;
                     break;
                 }
                 case 'E':
                 case 'e':
                 case 'f':
                 case 'G':
                 case 'g': {
                     if (arg == null) arg = args.next();
                     
                     if (!(arg instanceof RubyFloat)) {
                         // FIXME: what is correct 'recv' argument?
                         // (this does produce the desired behavior)
                         arg = RubyKernel.new_float(arg,arg);
                     }
                     double dval = ((RubyFloat)arg).getDoubleValue();
                     boolean nan = dval != dval;
                     boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
                     boolean negative = dval < 0.0d;
                     byte[] digits;
                     int nDigits = 0;
                     int exponent = 0;
 
                     int len = 0;
                     byte signChar;
                     
                     if (nan || inf) {
                         if (nan) {
                             digits = NAN_VALUE;
                             len = NAN_VALUE.length;
                         } else {
                             digits = INFINITY_VALUE;
                             len = INFINITY_VALUE.length;
                         }
                         if (negative) {
                             signChar = '-';
                             width--;
                         } else if ((flags & FLAG_PLUS) != 0) {
                             signChar = '+';
                             width--;
                         } else if ((flags & FLAG_SPACE) != 0) {
                             signChar = ' ';
                             width--;
                         } else {
                             signChar = 0;
                         }
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) buf.write(signChar);
 
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         buf.write(digits);
                         if (width > 0) buf.fill(' ', width);
 
                         offset++;
                         incomplete = false;
                         break;
                     }
                     String str = Double.toString(dval);
                     // grrr, arghh, want to subclass sun.misc.FloatingDecimal, but can't,
                     // so we must do all this (the next 70 lines of code), which has already
                     // been done by FloatingDecimal.
                     int strlen = str.length();
                     digits = new byte[strlen];
                     int nTrailingZeroes = 0;
                     int i = negative ? 1 : 0;
                     int decPos = 0;
                     byte ival;
                 int_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) nTrailingZeroes++;
 
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes-- ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case '.':
                             break int_loop;
                         }
                     }
                     decPos = nDigits + nTrailingZeroes;
                 dec_loop:
                     for ( ; i < strlen ; ) {
                         switch(ival = (byte)str.charAt(i++)) {
                         case '0':
                             if (nDigits > 0) {
                                 nTrailingZeroes++;
                             } else {
                                 exponent--;
                             }
                             break; // switch
                         case '1': case '2': case '3': case '4':
                         case '5': case '6': case '7': case '8': case '9':
                             if (nTrailingZeroes > 0) {
                                 for ( ; nTrailingZeroes > 0 ; nTrailingZeroes--  ) {
                                     digits[nDigits++] = '0';
                                 }
                             }
                             digits[nDigits++] = ival;
                             break; // switch
                         case 'E':
                             break dec_loop;
                         }
                     }
                     if ( i < strlen) {
                         int expSign;
                         int expVal = 0;
                         if (str.charAt(i) == '-') {
                             expSign = -1;
                             i++;
                         } else {
                             expSign = 1;
                         }
                         for ( ; i < strlen ; ) {
                             expVal = expVal * 10 + ((int)str.charAt(i++)-(int)'0');
                         }
                         exponent += expVal * expSign;
                     }
                     exponent += decPos - nDigits;
 
                     // gotta have at least a zero...
                     if (nDigits == 0) {
                         digits[0] = '0';
                         nDigits = 1;
                         exponent = 0;
                     }
 
                     // OK, we now have the significand in digits[0...nDigits]
                     // and the exponent in exponent.  We're ready to format.
 
                     int intDigits, intZeroes, intLength;
                     int decDigits, decZeroes, decLength;
                     byte expChar;
 
                     if (negative) {
                         signChar = '-';
                         width--;
                     } else if ((flags & FLAG_PLUS) != 0) {
                         signChar = '+';
                         width--;
                     } else if ((flags & FLAG_SPACE) != 0) {
                         signChar = ' ';
                         width--;
                     } else {
                         signChar = 0;
                     }
                     if ((flags & FLAG_PRECISION) == 0) {
                         precision = 6;
                     }
                     
                     switch(fchar) {
                     case 'E':
                     case 'G':
                         expChar = 'E';
                         break;
                     case 'e':
                     case 'g':
                         expChar = 'e';
                         break;
                     default:
                         expChar = 0;
                     }
 
                     switch(fchar) {
                     case 'g':
                     case 'G':
                         // an empirically derived rule: precision applies to
                         // significand length, irrespective of exponent
 
                         // an official rule, clarified: if the exponent
                         // <clarif>after adjusting for exponent form</clarif>
                         // is < -4,  or the exponent <clarif>after adjusting 
                         // for exponent form</clarif> is greater than the
                         // precision, use exponent form
                         boolean expForm = (exponent + nDigits - 1 < -4 ||
                             exponent + nDigits > (precision == 0 ? 1 : precision));
                         // it would be nice (and logical!) if exponent form 
                         // behaved like E/e, and decimal form behaved like f,
                         // but no such luck. hence: 
                         if (expForm) {
                             // intDigits isn't used here, but if it were, it would be 1
                             /* intDigits = 1; */
                             decDigits = nDigits - 1;
                             // precision for G/g includes integer digits
                             precision = Math.max(0,precision - 1);
                             
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,precision,precision!=0);
                                 if (n > nDigits) {
                                     nDigits = n;
                                 }
                                 decDigits = Math.min(nDigits - 1,precision);
                             }
                             exponent += nDigits - 1;
                             if (precision > 0) {
                                 len += 1 + precision; // n.prec
                             } else {
                                 len += 1; // n
                                 if ((flags & FLAG_SHARP) != 0) {
                                     len++; // will have a trailing '.'
                                 }
                             }
                             
                             width -= len + 5; // 5 -> e+nnn / e-nnn
 
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.write(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
                             // now some data...
                             buf.write(digits[0]);
                             if (precision > 0) {
                                 buf.write(args.getDecimalSeparator()); // '.'
                                 if (decDigits > 0) {
                                     buf.write(digits,1,decDigits);
                                     precision -= decDigits;
                                 }
                             } else if ((flags & FLAG_SHARP) != 0) {
                                 buf.write(args.getDecimalSeparator());
                             }
                             buf.write(expChar); // E or e
                             buf.write(exponent >= 0 ? '+' : '-');
                             if (exponent < 0) {
                                 exponent = -exponent;
                             }
                             buf.write(exponent / 100 + '0');
                             buf.write(exponent % 100 / 10 + '0');
                             buf.write(exponent % 10 + '0');
                             if (width > 0) {
                                 buf.fill(' ', width);
                             }
                         } else { // decimal form, like (but not *just* like!) 'f'
                             intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                             intZeroes = Math.max(0,exponent);
                             intLength = intDigits + intZeroes;
                             decDigits = nDigits - intDigits;
                             decZeroes = Math.max(0,-(decDigits + exponent));
                             decLength = decZeroes + decDigits;
                             precision = Math.max(0,precision - intLength);
                             
                             if (precision < decDigits) {
                                 int n = round(digits,nDigits,intDigits+precision-1,precision!=0);
                                 if (n > nDigits) {
                                     // digits array shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     precision = Math.max(0,precision-1);
                                 }
                                 decDigits = precision;
                                 decLength = decZeroes + decDigits;
                             }
                             len += intLength;
                             if (decLength > 0) {
                                 len += decLength + 1;
                             } else {
                                 if ((flags & FLAG_SHARP) != 0) {
                                     len++; // will have a trailing '.'
                                     if (precision > 0) { // g fills trailing zeroes if #
                                         len += precision;
                                     }
                                 }
                             }
                             
                             width -= len;
                             
                             if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                                 buf.fill(' ',width);
                                 width = 0;
                             }
                             if (signChar != 0) {
                                 buf.write(signChar);
                             }
                             if (width > 0 && (flags & FLAG_MINUS) == 0) {
                                 buf.fill('0',width);
                                 width = 0;
                             }
                             // now some data...
                             if (intLength > 0){
                                 if (intDigits > 0) { // s/b true, since intLength > 0
                                     buf.write(digits,0,intDigits);
                                 }
                                 if (intZeroes > 0) {
                                     buf.fill('0',intZeroes);
                                 }
                             } else {
                                 // always need at least a 0
                                 buf.write('0');
                             }
                             if (decLength > 0 || (flags & FLAG_SHARP) != 0) {
                                 buf.write(args.getDecimalSeparator());
                             }
                             if (decLength > 0) {
                                 if (decZeroes > 0) {
                                     buf.fill('0',decZeroes);
                                     precision -= decZeroes;
                                 }
                                 if (decDigits > 0) {
                                     buf.write(digits,intDigits,decDigits);
                                     precision -= decDigits;
                                 }
                                 if ((flags & FLAG_SHARP) != 0 && precision > 0) {
                                     buf.fill('0',precision);
                                  }
                             }
                             if ((flags & FLAG_SHARP) != 0 && precision > 0) {
                                 buf.fill('0',precision);
                             }
                             if (width > 0) {
                                 buf.fill(' ', width);
                             }
                         }
                         break;
                     
                     case 'f':
                         intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                         intZeroes = Math.max(0,exponent);
                         intLength = intDigits + intZeroes;
                         decDigits = nDigits - intDigits;
                         decZeroes = Math.max(0,-(decDigits + exponent));
                         decLength = decZeroes + decDigits;
                         
                         if (precision < decLength) {
                             if (precision < decZeroes) {
                                 decDigits = 0;
                                 decZeroes = precision;
                             } else {
                                 int n = round(digits,nDigits,intDigits+precision-decZeroes-1,precision!=0);
                                 if (n > nDigits) {
                                     // digits arr shifted, update all
                                     nDigits = n;
                                     intDigits = Math.max(0,Math.min(nDigits + exponent,nDigits));
                                     intLength = intDigits + intZeroes;
                                     decDigits = nDigits - intDigits;
                                     decZeroes = Math.max(0,-(decDigits + exponent));
                                     decLength = decZeroes + decDigits;
                                 }
                                 decDigits = precision - decZeroes;
                             }
                             decLength = decZeroes + decDigits;
                         }
                         if (precision > 0) {
                             len += Math.max(1,intLength) + 1 + precision;
                             // (1|intlen).prec
                         } else {
                             len += Math.max(1,intLength);
                             // (1|intlen)
                             if ((flags & FLAG_SHARP) != 0) {
                                 len++; // will have a trailing '.'
                             }
                         }
                         
                         width -= len;
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.write(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         if (intLength > 0){
                             if (intDigits > 0) { // s/b true, since intLength > 0
                                 buf.write(digits,0,intDigits);
                             }
                             if (intZeroes > 0) {
                                 buf.fill('0',intZeroes);
                             }
                         } else {
                             // always need at least a 0
                             buf.write('0');
                         }
                         if (precision > 0 || (flags & FLAG_SHARP) != 0) {
                             buf.write(args.getDecimalSeparator());
                         }
                         if (precision > 0) {
                             if (decZeroes > 0) {
                                 buf.fill('0',decZeroes);
                                 precision -= decZeroes;
                             }
                             if (decDigits > 0) {
                                 buf.write(digits,intDigits,decDigits);
                                 precision -= decDigits;
                             }
                             // fill up the rest with zeroes
                             if (precision > 0) {
                                 buf.fill('0',precision);
                             }
                         }
                         if (width > 0) {
                             buf.fill(' ', width);
                         }
                         break;
                     case 'E':
                     case 'e':
                         // intDigits isn't used here, but if it were, it would be 1
                         /* intDigits = 1; */
                         decDigits = nDigits - 1;
                         
                         if (precision < decDigits) {
                             int n = round(digits,nDigits,precision,precision!=0);
                             if (n > nDigits) {
                                 nDigits = n;
                             }
                             decDigits = Math.min(nDigits - 1,precision);
                         }
                         exponent += nDigits - 1;
                         if (precision > 0) {
                             len += 2 + precision; // n.prec
                         } else {
                             len += 1; // n
                             if ((flags & FLAG_SHARP) != 0) {
                                 len++; // will have a trailing '.'
                             }
                         }
                         
                         width -= len + 5; // 5 -> e+nnn / e-nnn
                         
                         if (width > 0 && (flags & (FLAG_ZERO|FLAG_MINUS)) == 0) {
                             buf.fill(' ',width);
                             width = 0;
                         }
                         if (signChar != 0) {
                             buf.write(signChar);
                         }
                         if (width > 0 && (flags & FLAG_MINUS) == 0) {
                             buf.fill('0',width);
                             width = 0;
                         }
                         // now some data...
                         buf.write(digits[0]);
                         if (precision > 0) {
                             buf.write(args.getDecimalSeparator()); // '.'
                             if (decDigits > 0) {
                                 buf.write(digits,1,decDigits);
                                 precision -= decDigits;
                             }
                             if (precision > 0) {
                                 buf.fill('0',precision);
                             }
 
                         } else if ((flags & FLAG_SHARP) != 0) {
                             buf.write(args.getDecimalSeparator());
                         }
                         buf.write(expChar); // E or e
                         buf.write(exponent >= 0 ? '+' : '-');
                         if (exponent < 0) {
                             exponent = -exponent;
                         }
                         buf.write(exponent / 100 + '0');
                         buf.write(exponent % 100 / 10 + '0');
                         buf.write(exponent % 10 + '0');
                         if (width > 0) {
                             buf.fill(' ', width);
                         }
                         break;
                     } // switch (format char E,e,f,G,g)
                     
                     offset++;
                     incomplete = false;
                     break;
                 } // block (case E,e,f,G,g)
                 } // switch (each format char in spec)
             } // for (each format spec)
         } // main while loop (offset < length)
         
         return buf.toByteList();
     }
 
     // debugging code, keeping for now
     private static final void showLiteral(byte[] format, int start, int offset) {
         System.out.println("literal: ["+ new String(format,start,offset-start)+ "], " +
                 " s="+ start + " o="+ offset);
     }
     
     // debugging code, keeping for now
     private static final void showVals(byte[] format,int start,int offset, byte fchar,
             int flags, int width, int precision, Object arg) {
         System.out.println(new StringBuffer()
         .append("value: ").append(new String(format,start,offset-start+1)).append('\n')
         .append("type: ").append((char)fchar).append('\n')
         .append("start: ").append(start).append('\n')
         .append("length: ").append(offset-start).append('\n')
         .append("flags: ").append(Integer.toBinaryString(flags)).append('\n')
         .append("width: ").append(width).append('\n')
         .append("precision: ").append(precision).append('\n')
         .append("arg: ").append(arg).append('\n')
         .toString());
         
     }
     
     private static final void raiseArgumentError(Args args, String message) {
         args.raiseArgumentError(message);
     }
     
     /**
      * 
      * @param args
      * @param message
      */
     private static final void warn(Args args, String message) {
         args.warn(message);
     }
     
     private static final void warning(Args args, String message) {
         args.warning(message);
     }
     
     private static final void checkOffset(Args args, int offset, int length) {
         if (offset >= length) {
             raiseArgumentError(args,"malformed format string");
         }
     }
     
     private static final int extendWidth(Args args, int oldWidth, byte newChar) {
         int newWidth = oldWidth * 10 + (newChar - '0');
         if (newWidth / 10 != oldWidth) {
             raiseArgumentError(args,"width too big");
         }
         return newWidth;
     }
     
     private static final boolean isDigit(byte aChar) {
         return (aChar >= '0' && aChar <= '9');
     }
     
     private static final boolean isPrintable(byte aChar) {
         return (aChar > 32 && aChar < 127);
     }
 
     private static final int skipSignBits(byte[] bytes, int base) {
         int skip = 0;
         int length = bytes.length;
         byte b;
         switch(base) {
         case 2:
             for ( ; skip < length && bytes[skip] == '1'; skip++ ) ;
             break;
         case 8:
             if (length > 0 && bytes[0] == '3') {
                 skip++;
             }
             for ( ; skip < length && bytes[skip] == '7'; skip++ ) ;
             break;
         case 10:
             if (length > 0 && bytes[0] == '-') {
                 skip++;
             }
             break;
         case 16:
             for ( ; skip < length && ((b = bytes[skip]) == 'f' || b == 'F'); skip++ ) ;
         }
         return skip;
     }
     
     private static final int round(byte[] bytes, int nDigits, int roundPos, boolean roundDown) {
         int next = roundPos + 1;
         if (next >= nDigits || bytes[next] < '5' ||
                 // MRI rounds up on nnn5nnn, but not nnn5 --
                 // except for when they do
                 (roundDown && bytes[next] == '5' && next == nDigits - 1)) {
             return nDigits;
         }
         if (roundPos < 0) { // "%.0f" % 0.99
             System.arraycopy(bytes,0,bytes,1,nDigits);
             bytes[0] = '1';
             return nDigits + 1;
         }
         bytes[roundPos] += 1;
         while (bytes[roundPos] > '9') {
             bytes[roundPos] = '0';
             roundPos--;
             if (roundPos >= 0) {
                 bytes[roundPos] += 1;
             } else {
                 System.arraycopy(bytes,0,bytes,1,nDigits);
                 bytes[0] = '1';
                 return nDigits + 1;
             }
         }
         return nDigits;
     }
 
     private static final byte[] getFixnumBytes(RubyFixnum arg, int base, boolean sign, boolean upper) {
         long val = arg.getLongValue();
 
         // limit the length of negatives if possible (also faster)
         if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
             if (sign) {
                 return Convert.intToByteArray((int)val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return Convert.intToBinaryBytes((int)val);
                 case 8:  return Convert.intToOctalBytes((int)val);
                 case 10:
                 default: return Convert.intToCharBytes((int)val);
                 case 16: return Convert.intToHexBytes((int)val,upper);
                 }
             }
         } else {
             if (sign) {
                 return Convert.longToByteArray(val,base,upper);
             } else {
                 switch(base) {
                 case 2:  return Convert.longToBinaryBytes(val);
                 case 8:  return Convert.longToOctalBytes(val);
                 case 10:
                 default: return Convert.longToCharBytes(val);
                 case 16: return Convert.longToHexBytes(val,upper);
                 }
             }
         }
     }
     
     private static final byte[] getBignumBytes(RubyBignum arg, int base, boolean sign, boolean upper) {
         BigInteger val = arg.getValue();
         if (sign || base == 10 || val.signum() >= 0) {
             return stringToBytes(val.toString(base),upper);
         }
 
         // negative values
         byte[] bytes = val.toByteArray();
         switch(base) {
         case 2:  return Convert.twosComplementToBinaryBytes(bytes);
         case 8:  return Convert.twosComplementToOctalBytes(bytes);
         case 16: return Convert.twosComplementToHexBytes(bytes,upper);
         default: return stringToBytes(val.toString(base),upper);
         }
     }
     
     private static final byte[] getUnsignedNegativeBytes(RubyInteger arg) {
         // calculation for negatives when %u specified
         // for values >= Integer.MIN_VALUE * 2, MRI uses (the equivalent of)
         //   long neg_u = (((long)Integer.MAX_VALUE + 1) << 1) + val
         // for smaller values, BigInteger math is required to conform to MRI's
         // result.
         long longval;
         BigInteger bigval;
         
         if (arg instanceof RubyFixnum) {
             // relatively cheap test for 32-bit values
             longval = ((RubyFixnum)arg).getLongValue();
             if (longval >= (long)Integer.MIN_VALUE << 1) {
                 return Convert.longToCharBytes((((long)Integer.MAX_VALUE + 1L) << 1) + longval);
             }
             // no such luck...
             bigval = BigInteger.valueOf(longval);
         } else {
             bigval = ((RubyBignum)arg).getValue();
         }
         // ok, now it gets expensive...
         int shift = 0;
         // go through negated powers of 32 until we find one small enough 
         for (BigInteger minus = BIG_MINUS_64 ;
                 bigval.compareTo(minus) < 0 ;
                 minus = minus.shiftLeft(32), shift++) ;
         // add to the corresponding positive power of 32 for the result.
         // meaningful? no. conformant? yes. I just write the code...
         BigInteger nPower32 = shift > 0 ? BIG_64.shiftLeft(32 * shift) : BIG_64;
         return stringToBytes(nPower32.add(bigval).toString(),false);
     }
     
     private static final byte[] stringToBytes(CharSequence s, boolean upper) {
         int len = s.length();
         byte[] bytes = new byte[len];
         if (upper) {
             for (int i = len; --i >= 0; ) {
                 int b = (byte)((int)s.charAt(i) & (int)0xff);
                 if (b >= 'a' && b <= 'z') {
                     bytes[i] = (byte)(b & ~0x20);
                 } else {
                     bytes[i] = (byte)b;
                 }
             }
         } else {
             for (int i = len; --i >= 0; ) {
                 bytes[i] = (byte)((int)s.charAt(i) & (int)0xff); 
             }
         }
         return bytes;
     }
 }
diff --git a/test/testHash.rb b/test/testHash.rb
index b094389bd2..496dc8d125 100644
--- a/test/testHash.rb
+++ b/test/testHash.rb
@@ -1,94 +1,116 @@
 require 'test/minirunit'
 test_check "Test hash:"
 
 h = {1=>2,3=>4,5=>6}
 h.replace({1=>100})
 test_equal({1=>100}, h)
 
 h = {1=>2,3=>4}
 h2 = {3=>300, 4=>400}
 h.update(h2)
 test_equal(2, h[1])
 test_equal(300, h[3])
 test_equal(400, h[4])
 
 num = 0
 h1 = { "a" => 100, "b" => 200 }
 h2 = { "b" => 254, "c" => 300 }
 h1.merge!(h2) {|k,o,n| num += 1; o+n }
 test_equal(h1,{"a"=>100, "b"=>454, "c"=>300})
 test_equal(num, 1)
 
 h = {1=>2,3=>4}
 test_exception(IndexError) { h.fetch(10) }
 test_equal(2, h.fetch(1))
 test_equal("hello", h.fetch(10, "hello"))
 test_equal("hello 10", h.fetch(10) { |e| "hello #{e}" })
 
 h = {}
 k1 = [1]
 h[k1] = 1
 k1[0] = 100
 test_equal(nil, h[k1])
 h.rehash
 test_equal(1, h[k1])
 
 h = {1=>2,3=>4,5=>6}
 test_equal([2, 6], h.values_at(1, 5))
 
 h = {1=>2,3=>4}
 test_equal(1, h.index(2))
 test_equal(nil, h.index(10))
 test_equal(nil, h.default_proc)
 h.default = :hello
 test_equal(nil, h.default_proc)
 test_equal(1, h.index(2))
 test_equal(nil, h.index(10))
 
 h = Hash.new {|h,k| h[k] = k.to_i*10 }
 
 test_ok(!nil, h.default_proc)
 test_equal(100, h[10])
 test_equal(20, h.default(2))
 
 #behavior change in 1.8.5 led to this:
 test_equal(nil, h.default)
 
 h.default = 5
 test_equal(5,h.default)
 test_equal(nil, h.default_proc)
 
 test_equal(5, h[12])
 
 class << h
  def default(k); 2; end
 end
 
 test_equal(nil, h.default_proc)
 test_equal(2, h[30])
 
 # test that extensions of the base classes are typed correctly
 class HashExt < Hash
 end
 test_equal(HashExt, HashExt.new.class)
 test_equal(HashExt, HashExt[:foo => :bar].class)
 
 # make sure hash yields look as expected (copied from MRI iterator test)
 
 class H
   def each
     yield [:key, :value]
   end
 end
 
 [{:key=>:value}, H.new].each {|h|
   h.each{|a| test_equal([:key, :value], a)}
   h.each{|*a| test_equal([[:key, :value]], a)}
   h.each{|k,v| test_equal([:key, :value], [k,v])}
 }
 
 # each_pair should splat args correctly
 {:a=>:b}.each_pair do |*x|
         test_equal(:a,x[0])
         test_equal(:b,x[1])
 end
+
+# Test hash coercion
+class MyHash
+  def initialize(hash)
+    @hash = hash
+  end
+  def to_hash
+    @hash
+  end
+end
+
+x = {:a => 1, :b => 2}
+x.update(MyHash.new({:a => 10, :b => 20}))
+test_equal(10, x[:a])
+test_equal(20, x[:b])
+test_exception(TypeError) { x.update(MyHash.new(4)) }
+
+x = {:a => 1, :b => 2}
+x.replace(MyHash.new({:a => 10, :b => 20}))
+test_equal(10, x[:a])
+test_equal(20, x[:b])
+test_exception(TypeError) { x.replace(MyHash.new(4)) }
\ No newline at end of file
diff --git a/test/testString.rb b/test/testString.rb
index ba50bbb14d..838fe20c32 100644
--- a/test/testString.rb
+++ b/test/testString.rb
@@ -1,523 +1,595 @@
 require 'test/minirunit'
 test_check "Test string:"
 
 # Generic class to test integer functions.
 class IntClass
   def initialize(num); @num = num; end
   def to_int; @num; end; 
 end
 
 ##### misc #####
 
 test_equal("hihihi", "hi" * 3)
 test_equal(9, "alphabetagamma" =~ /gamma$/)
 test_equal(nil, "alphabetagamma" =~ /GAMMA$/)
 test_equal(false, "foo" == :foo)
 
 test_equal(11, "\v"[0])
 test_equal(27, "\e"[0])
 # Test round trip of each ASCII character
 0.upto(255) do |ch|
   test_equal(ch,eval(ch.chr.inspect)[0])
 end
 
 ##### [] (aref) ######
 s = "hello there"
 
 test_equal(101, s[1])
 test_equal("ell", s[1, 3])
 test_equal("l", s[3, 1])
 test_equal("ell", s[1..3])
 test_equal("el", s[1...3])
 test_equal("er", s[-3, 2])
 test_equal("her", s[-4..-2])
 test_equal("", s[-2..-4])
 test_equal("", s[6..2])
 t = ""
 test_equal(nil, t[6..2])
 test_equal(nil, t[-2..-4])
 test_equal("ell", s[/[aeiow](.)\1/])
 test_equal("ell", s[/[aeiow](.)\1/, 0])
 test_equal("l", s[/[aeiow](.)\1/, 1])
 test_equal(nil, s[/[aeiow](.)\1/, 2])
 # negative subscripts exercising rubicon test case
 test_equal("o", s[/[aeiow](.)\1(.)/, -1])
 test_equal("l", s[/[aeiow](.)\1(.)/, -2])
 # zero subscript should capture whole matched pattern
 test_equal("ello", s[/[aeiow](.)\1(.)/, 0])
 test_equal("the", s[/(..)e/])
 test_equal("th", s[/(..)e/, 1])
 test_equal("lo", s["lo"])
 test_equal(nil, s["bye"])
 
 ##### []= (aset) #######
 
 s = "foobar"
 s["foo"] = "baz"
 test_equal("bazbar", s)
 s[2] = 79
 test_equal("baObar", s)
 
 # ""[0,0]="foo" is valid
 s = ""
 s[0,0]="foo"
 
 test_equal("foo", s)
 
 # regexp, integer asets from rubicon
 
 s="BarFoo"
 test_equal("Foo", s[/([A-Z]..)([A-Z]..)/, 1] = "Foo")
 test_equal("FooFoo", s)
 test_equal("Bar", s[/([A-Z]..)([A-Z]..)/, 2] = "Bar")
 test_equal("FooBar", s)
 test_exception(IndexError) { s[/([A-Z]..)([A-Z]..)/, 3] = "None" }
 test_equal("FooBar", s)
 test_equal("Foo", s[/([A-Z]..)([A-Z]..)/, -1] = "Foo")
 test_equal("FooFoo", s)
 test_equal("Bar", s[/([A-Z]..)([A-Z]..)/, -2] = "Bar")
 test_equal("BarFoo", s)
 test_exception(IndexError) { s[/([A-Z]..)([A-Z]..)/, -3] = "None" }
 
 ##### capitalize/capitalize! ######
 
 test_equal("Hello", "hello".capitalize)
 test_equal("123abc", "123ABC".capitalize)
 
 s ="hello"
 s.capitalize!
 test_equal("Hello", s)
 test_equal(101, s[IntClass.new(1)])
 test_equal(nil, s.capitalize!)
 s = "123ABC"
 s.capitalize!
 test_equal("123abc", s)
 test_equal(nil, s.capitalize!)
 
 ##### center ######
 
 test_equal("hello", "hello".center(4))
 test_equal("       hello        ", "hello".center(20))
 test_equal("hello", "hello".center(4, "_-^-"))
 test_equal("_-^-_-^helloe_-^-_-^", "helloe".center(20, "_-^-"))
 test_equal("-------hello--------", "hello".center(20, "-"))
 test_exception(ArgumentError) { "hello".center(11, "") }
 
 ##### chomp ######
 
 # See test/testStringChomp.rb
 
 ##### chop/chop! ######
 
 test_equal("", "".chop)
 test_equal(nil, "".chop!)
 test_equal("", "\n".chop)
 s = "\n"
 test_equal("", s.chop!)
 test_equal("", s)
 
 test_equal("string", "string\r\n".chop)
 test_equal("string\n", "string\n\r".chop)
 test_equal("string", "string\n".chop)
 test_equal("strin", "string".chop)
 test_equal("", "x".chop.chop)
 
 
 ##### <=> (cmp) #####
 
 test_equal(-1, 'A' <=> 'B')
 test_equal(0, 'A' <=> 'A')
 test_equal(1, 'B' <=> 'A')
 test_equal(nil, 'A' <=> 3)
 test_equal(nil, 'A' <=> 3.to_f)
 
 
 ##### <</concat ######
 s = "a"
 test_equal("aa", s << "a")
 test_equal("aaa", s.concat("a"))
 test_equal("aaaa", s << 97)
 test_equal("aaaaa", s.concat(97))
 test_exception(TypeError) { s << 300 }
 test_exception(TypeError) { s.concat(300) }
 
 ##### downcase/downcase! ######
 
 test_equal("hello", "HELlo".downcase)
 s = "HELlo"
 test_equal("hello", s.downcase!)
 test_equal(nil, s.downcase!)
 
 ##### each_byte #####
 
 "\x80".each_byte {|c| test_equal(128, c) }
 
 ##### gsub #####
 test_equal("h*ll*", "hello".gsub(/[aeiou]/, '*'))
 test_equal("h<e>ll<o>", "hello".gsub(/([aeiou])/, '<\1>'))
 test_equal("104 101 108 108 111 ", "hello".gsub(/./) {|s| s[0].to_s + ' '})
 test_equal("a-b-c", "a+b+c".gsub("+", "-"))
 test_equal("", "".gsub(/\r\n|\n/, "\n"))
 
 ##### index/rindex ######
 testcase='toto'
 test_ok(1 == idx = testcase.index('o'))
 test_ok(3 == testcase.index('o',idx.succ))
 test_ok(3 == idx = testcase.rindex('o'))
 test_ok(1 == testcase.rindex('o', idx-1))
 
 ##### insert #####
 
 s = "abcd"
 test_equal("Xabcd", s.insert(0, 'X'))
 test_equal("Xabcd", s)
 test_equal("abcXd", "abcd".insert(3, 'X'))
 test_equal("abcdX", "abcd".insert(4, 'X'))
 test_equal("abXcd", "abcd".insert(-3, 'X'))
 test_equal("abcdX", "abcd".insert(-1, 'X'))
 
 test_exception(IndexError) { "".insert(-100, 'X') }
 test_exception(IndexError) { "".insert(100, 'X') }
 test_exception(TypeError) { "abcd".insert(1, nil) }
 
 
 ##### intern #####
 for method in [:intern, :to_sym] do
     test_equal(:koala, "koala".send(method))
     test_ok(:koala != "Koala".send(method))
    
     for str in ["identifier", "with spaces", "9with_digits", "9and spaces"]
       sym = str.send(method)
       test_equal(Symbol, sym.class)
       test_equal(str, sym.to_s)
     end
    
     test_exception(ArgumentError) { "".send(method) }
     test_exception(ArgumentError) { "with\0null\0inside".send(method) }
 end
 
 
 ##### ljust,rjust #####
 
 test_equal("hello", "hello".ljust(4))
 test_equal("hello      ", "hello".ljust(11))
 
 # with explicit padding
 test_equal("hi111111111", "hi".ljust(11, "1"))
 test_equal("hi121212121", "hi".ljust(11, "12"))
 test_equal("hi123412341", "hi".ljust(11, "1234"))
 
 # zero width padding
 test_exception(ArgumentError)  { "hello".ljust(11, "") }
 
 test_equal("hello", "hello".rjust(4))
 test_equal("      hello", "hello".rjust(11))
 
 # with explicit padding
 test_equal("111111111hi", "hi".rjust(11, "1"))
 test_equal("121212121hi", "hi".rjust(11, "12"))
 test_equal("123412341hi", "hi".rjust(11, "1234"))
 
 # zero width padding
 test_exception(ArgumentError)  { "hi".rjust(11, "") }
 
 ##### oct #####
 # oct should return zero in appropriate cases
 test_equal(0, "b".oct)
 test_equal(0, "".oct)
 
 ##### replace #####
 t = "hello"
 s = "world"
 s.replace t
 test_equal("hello", s)
 s.chop!
 test_equal("hello", t)
 
 ##### reverse/reverse! #####
 s = "abc"
 test_equal("cba", s.reverse)
 test_equal("abc", s)
 test_equal("cba", s.reverse!)
 test_equal("cba", s)
 
 ##### rjust (see ljust) #####
 
 ##### scan
 
 s = "cruel world"
 test_equal(["cruel", "world"], s.scan(/\w+/))
 test_equal(["cru", "el ", "wor"], s.scan(/.../))
 test_equal([["cru"], ["el "], ["wor"]], s.scan(/(...)/))
 test_equal([["cr", "ue"], ["l ", "wo"]], s.scan(/(..)(..)/))
 
 l = []
 s.scan(/\w+/) { |w| l << "<<#{w}>>" }
 test_equal(["<<cruel>>", "<<world>>"], l)
 l = ""
 s.scan(/(.)(.)/) { |a,b|  l << b; l << a }
 test_equal("rceu lowlr", l)
 
 ##### slice! ######
 
 o = "FooBar"
 
 s = o.dup
 test_equal(?F, s.slice!(0))
 test_equal("ooBar", s)
 test_equal("FooBar", o)
 s = o.dup
 test_equal(?r, s.slice!(-1))
 test_equal("FooBa", s)
 
 s = o.dup
 test_equal(nil, s.slice!(6))
 test_equal("FooBar", s)
 s = o.dup
 test_equal(nil, s.slice!(-7))
 test_equal("FooBar", s)
 
 s = o.dup
 test_equal("Foo", s.slice!(0,3))
 test_equal("Bar", s)
 s = o.dup
 test_equal("Bar", s.slice!(-3,3))
 test_equal("Foo", s)
 
 s = o.dup
 test_equal(nil, s.slice!(7,2))      # Maybe should be six?
 test_equal("FooBar", s)
 s = o.dup
 test_equal(nil, s.slice!(-7,10))
 test_equal("FooBar", s)
 
 s = o.dup
 test_equal("Foo", s.slice!(0..2))
 test_equal("Bar", s)
 s = o.dup
 test_equal("Bar", s.slice!(-3..-1))
 test_equal("Foo", s)
 
 s = o.dup
 test_equal("", s.slice!(6..2))
 test_equal("FooBar", s)
 s = o.dup
 test_equal(nil, s.slice!(-10..-7))
 test_equal("FooBar", s)
 
 s = o.dup
 test_equal("Foo", s.slice!(/^F../))
 test_equal("Bar", s)
 s = o.dup
 test_equal("Bar", s.slice!(/..r$/))
 test_equal("Foo", s)
 
 s = o.dup
 test_equal(nil, s.slice!(/xyzzy/))
 test_equal("FooBar", s)
 
 s = o.dup
 test_equal("Foo", s.slice!("Foo"))
 test_equal("Bar", s)
 s = o.dup
 test_equal("Bar", s.slice!("Bar"))
 test_equal("Foo", s)
 
 ##### split ######
 
 test_equal(["1", "2", "3"], "1x2x3".split('x'))
 test_equal(["1", "2", "3"], "1   2     3".split(' '))
 test_equal(["1", "2", "3"], "  1   2     3".split(' '))
 # explicit Regex will not ignore leading whitespace
 test_equal(["", "1", "2", "3"], "  1   2     3".split(/\s+/))
 test_equal(["1", "2", "3"], "1 2 3".split())
 test_equal(["1", "2", "3"], "1x2y3".split(/x|y/))
 test_equal(["1", "x", "2", "y", "3"], "1x2y3".split(/(x|y)/))
 test_equal(["1", "x", "a", "2", "x", "a", "3"], "1xta2xta3".split(/(x)t(.)/))
 test_equal(["foo"], "foo".split("whatever", 1))
 test_equal(["", "a", "b", "c"], "/a/b/c".split("/"))
 test_equal(["a", "b", "c"], "abc".split(//))
 test_equal(["/home", "/jruby"], "/home/jruby".split(%r<(?=/)>))
 test_equal(["///home", "///jruby"], "///home///jruby".split(%r<(?=///)>))
 
 ##### sub #####
 
 test_equal("h*llo", "hello".sub(/[aeiou]/, '*'))
 test_equal("h<e>llo", "hello".sub(/([aeiou])/, '<\1>'))
 test_equal("104 ello", "hello".sub(/./) {|s| s[0].to_s + ' ' })
 special_chars = "{}(){}|*.\\?+^\$".split(//)
 special_chars.each {|c| test_equal("H", c.sub(c, "H")) }
    
 ##### succ/succ! #####
 
 def test_succ!(expected, start); start.succ!; test_equal(expected, start); end
 test_equal("abce", "abcd".succ)
 test_succ!("abce", "abcd")
 test_equal("THX1139", "THX1138".succ)
 test_succ!("THX1139", "THX1138")
 test_equal("<<koalb>>", "<<koala>>".succ)
 test_succ!("<<koalb>>", "<<koala>>")
 test_equal("2000aaa", "1999zzz".succ)
 test_succ!("2000aaa", "1999zzz")
 test_equal("AAAA0000", "ZZZ9999".succ)
 test_succ!("AAAA0000", "ZZZ9999")
 test_equal("**+", "***".succ)
 test_succ!("**+", "***")
 
 ##### sum #####
 
 test_equal(2, "\001\001\000".sum)
 test_equal(1408, "now is the time".sum)
 test_equal(128, "now is the time".sum(8))
 test_equal(128, "\x80".sum)
 
 def check_sum(str, bits=16)
   sum = 0
   str.each_byte {|c| sum += c}
   sum = sum & ((1 << bits) - 1) if bits != 0
   test_equal(sum, str.sum(bits))
 end
 
 0.upto(70) {|bits|
   check_sum("xyz", bits)
 }
 
 
 ##### swapcase/swapcase! #####
 
 s = "abC"
 test_equal("ABc", s.swapcase)
 test_equal("abC", s)
 test_equal("ABc", s.swapcase!)
 test_equal("ABc", s)
 s = "111"
 test_equal("111", s.swapcase)
 test_equal(nil, s.swapcase!)
 
 ##### to_i #####
 
 test_equal(12345, "12345".to_i)
 test_equal(99, "99 red balloons".to_i)
 test_equal(0, "0a".to_i)
 test_equal(10, "0a".to_i(16))
 test_equal(0, "0x10".to_i)
 test_equal(16, "0x10".to_i(0))
 test_equal(-16,"-0x10".to_i(0))
 test_equal(0, "hello".to_i)
 test_equal(14167554, "hello".to_i(30))
 test_equal(101, "1100101".to_i(2))
 test_equal(294977, "1100101".to_i(8))
 test_equal(1100101, "1100101".to_i(10))
 test_equal(17826049, "1100101".to_i(16))
 test_equal(199066177, "1100101".to_i(24))
 
 
 ##### to_sym (see intern) #####
 
 ##### upcase/upcase! ######
 
 test_equal("HELLO", "HELlo".upcase)
 s = "HeLLo"
 test_equal("HELLO", s.upcase!)
 test_equal(nil, s.upcase!)
 
 ##### upto ######
 
 UPTO_ANS = ["a8", "a9", "b0"]
 s = "a8"
 ans = []
 s.upto("b0") { |e| ans << e }
 test_equal(UPTO_ANS, ans)
 test_equal("a8", s)
 
 ##### formatting with % and a string #####
 test_equal(" 5", '%02s' % '5')
 test_equal("05", '%02d' % '5')
 test_equal("05", '%02g' % '5')
 test_equal("05", '%02G' % '5')
 test_equal("  ", '%2s' % nil)
 
 # test that extensions of the base classes are typed correctly
 class StringExt < String
 end
 test_equal(StringExt, StringExt.new.class)
 test_equal(StringExt, StringExt.new("test").class)
 
 test_equal("foa3VCPbMb8XQ", "foobar".crypt("foo"))
 
 test_exception(TypeError) { "this" =~ "that" }
 
-class MyString
-  include Comparable
-
-  def to_str
-    "foo"
-  end
-
-  def <=>(other)
-    return "foo".<=>(other)
-  end
-end
-
-# String#== should call other.==(str) when other respond_to "to_str"
-test_equal("foo", MyString.new)
-
-# ...but .eql? should still fail, since it only does a strict comparison between strings
-test_ok(!"foo".eql?(MyString.new))
-
 # UTF behavior around inspect, to_s, and split
 # NOTE: the "ffi" in the lines below is a single unicode character "ﬃ"; do not replace it with the normalized characters.
 
 old_code = $KCODE
 x = "eﬃcient"
 test_equal("\"e\\357\\254\\203cient\"", x.inspect)
 test_equal("eﬃcient", x.to_s)
 test_equal(["e", "\357", "\254", "\203", "c", "i", "e", "n", "t"], x.split(//))
 
 $KCODE = "UTF8"
 
 test_equal("\"eﬃcient\"", x.inspect)
 test_equal("eﬃcient", x.to_s)
 test_equal(["e", "ﬃ", "c", "i", "e", "n", "t"], x.split(//))
 
 # splitting by character should fall back on raw bytes when it's not valid unicode
 
 x2 = "\270\236\b\210\245"
 
 test_equal(["\270", "\236", "\b", "\210", "\245"], x2.split(//u))
 
 $KCODE = old_code
 
 # unpack("U*") should raise ArgumentError when the string is not valid UTF8
 test_exception(ArgumentError) { x2.unpack("U*") }
 
 # and just for kicks, make sure we're returning appropriate byte values for each_byte!
 
 bytes = []
 x2.each_byte { |b| bytes << b }
 test_equal([184, 158, 8, 136, 165], bytes)
 
 # JRUBY-280
 test_equal("1234567890.51",("%01.2f" % 1234567890.506))
 
 # test protocol conversion
 class GooStr < String
 end
 
 f = GooStr.new("AAAA")
 g= f.to_str
 test_ok(f.object_id != g.object_id)
 test_equal(String, g.class)
 test_equal("AAAA", g)
 
+class MyString
+  include Comparable
+
+  def to_str
+    "foo"
+  end
+
+  def <=>(other)
+    return "foo".<=>(other)
+  end
+end
+
+# String#== should call other.==(str) when other respond_to "to_str"
+test_equal("foo", MyString.new)
+
+# ...but .eql? should still fail, since it only does a strict comparison between strings
+test_ok(!"foo".eql?(MyString.new))
+
 class FooStr < String
   # Should not get called
   def to_str
     123
   end
 end
 
 f = FooStr.new("AAAA")
 
 test_equal("AAAA", [f].join(','))
 
+# test coercion for multiple methods
+class Foo
+  def to_int
+    3
+  end
+  def to_str
+    "hello"
+  end
+end
+
+class Five
+  def to_str
+    "5"
+  end
+end
+
+class Unpack
+  def to_str
+    "A"
+  end
+end
+
+test_equal("strstrstr", "str" * Foo.new)
+test_equal("strhello", "str" + Foo.new)
+test_equal("hello", "str".replace(Foo.new))
+test_equal(0, "hello".casecmp(Foo.new))
+test_equal("heXHujxX8gm/M", "str".crypt(Foo.new))
+test_equal("shellor", "str".gsub("t", Foo.new))
+test_equal("str", "shellor".gsub(Foo.new, "t"))
+test_equal(108, "shellor"[Foo.new])
+x = "sxxxxxr"
+x[1, 5] = Foo.new
+test_equal("shellor", x)
+x = "str"
+x[/t/, 0] = Foo.new
+test_equal("shellor", x)
+x = "str"
+x[1] = Foo.new
+test_equal("shellor", x)
+x = "str"
+x[/t/] = Foo.new
+test_equal("shellor", x)
+x = "str"
+x["t"] = Foo.new
+test_equal("shellor", x)
+x = "shellor"
+z = Foo.new
+# this appears to be broken in MRI...potentially a bug there?
+#x[z] = "t"
+#test_equal("str", x)
+x = "str"
+x[1..2] = Foo.new
+test_equal("shello", x)
+x = []
+"1".upto(Five.new) {|y| x << y}
+test_equal(["1", "2", "3", "4", "5"], x)
+test_ok("shellor".include?(Foo.new))
+test_equal(["s", "r"], "shellor".split(Foo.new))
+test_equal(5, "shellor".count(Foo.new))
+test_equal("sr", "shellor".delete(Foo.new))
+test_equal("sr", "shellor".delete!(Foo.new))
+test_equal("shelor", "shellor".squeeze(Foo.new))
+test_equal("shelor", "shellor".squeeze!(Foo.new))
+# Broken in JRuby, but not due to coercion
+#test_equal("sgoddbr", "shellor".tr(Foo.new, "goodbye"))
+#test_equal("shlllooor", "sgoodbyer".tr("goodbye", Foo.new))
+a = []
+"shellor".each_line(Foo.new) { |x| a << x }
+test_equal(["shello", "r"], a)
+test_equal(["a"], s.unpack(Unpack.new))
+test_equal(291, "123".to_i(IntClass.new(16)))
+test_equal(345, "str".sum(IntClass.new(16)))
