diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index f56afba0ee..cd5b9181ce 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1763 +1,1763 @@
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
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
 import java.util.WeakHashMap;
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
 import org.jruby.libraries.NKFLibrary;
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
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "yaml/syck" };
 
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
     private RubyModule enumerableModule;
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
     private static JRubyClassLoader jrubyClassLoader;
 
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
     
     private RubyClass hashClass;    
 
     private IRubyObject tmsStruct;
     
     private IRubyObject undef;
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 0;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
     
     /**
      * A list of finalizers, weakly referenced, to be executed on tearDown
      */
     private Map finalizers;
 
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
         return eval(parse(script, "<script>", getCurrentContext().getCurrentScope(), 0));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
 
             return EvaluationState.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
-                throw newLocalJumpError("unexpected return");
+                throw newLocalJumpError("return", (IRubyObject)je.getValue(), "unexpected return");
                 //              return (IRubyObject)je.getSecondaryData();
             } else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
-                throw newLocalJumpError("unexpected break");
+                throw newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
             }
 
             throw je;
         }
     }
     
     public IRubyObject compileOrFallbackAndRun(Node node) {
         try {
             // do the compile
             Script script = null;
             try {
                 StandardASMCompiler compiler = new StandardASMCompiler(node);
                 NodeCompilerFactory.getCompiler(node).compile(node, compiler);
                 
                 Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
                 
                 script = (Script)scriptClass.newInstance();
             } catch (Throwable t) { // The rest of these are all fallbacks
                 return eval(node);
             }
             
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 return (IRubyObject) je.getValue();
             } else {
                 throw je;
             }
         }
         
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
             Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
 
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
             System.err.println("Error -- Not compileable: " + e.getMessage());
             return null;
         } catch (InstantiationException e) {
             System.err.println("Error -- Not compileable: " + e.getMessage());
             return null;
         } catch (IllegalAccessException e) {
             System.err.println("Error -- Not compileable: " + e.getMessage());
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
     
     public IRubyObject getUndef() {
         return undef;
     }
 
     public RubyModule getKernel() {
         return kernelModule;
     }
     
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     
 
     public RubyClass getString() {
         return stringClass;
     }
 
     public RubyClass getFixnum() {
         return fixnumClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
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
         if (superClass == null) superClass = objectClass;
 
         return superClass.newSubClass(name, allocator, parentCRef, true);
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
         IRubyObject module = objectClass.getConstantAt(name);
         
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!(module instanceof RubyModule)) {
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
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
     }
 
     private void initLibraries() {
         loadService = config.createLoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
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
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
         registerBuiltin("nkf.rb", new NKFLibrary());
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
                     runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("openssl.rb"));
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
         registerBuiltin("etc.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     //TODO: implement this
                 }});
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         undef = new RubyUndef();
 
         RubyClass objectMetaClass = RubyClass.createBootstrapMetaClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR, null);
         RubyObject.createObjectClass(this, objectMetaClass);
 
         objectClass = objectMetaClass;
         objectClass.setConstant("Object", objectClass);
         RubyClass moduleClass = RubyClass.createBootstrapMetaClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR, objectClass.getCRef());
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
 
         RubyModule.createModuleClass(this, moduleClass);
 
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
         enumerableModule = RubyEnumerable.createEnumerableModule(this);
         stringClass = RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) fixnumClass = RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) hashClass = RubyHash.createHashClass(this);
         
         RubyIO.createIOClass(this);
 
         if (profile.allowClass("Array")) arrayClass = RubyArray.createArrayClass(this);
 
         RubyClass structClass = null;
         if (profile.allowClass("Struct")) structClass = RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
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
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
         RubyClass exceptionClass = getClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
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
         if (profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemExit")) {
             defineClass("SystemExit", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Interrupt")) {
             defineClass("Interrupt", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("SignalException")) {
             defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
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
         if (profile.allowClass("NoMethodError")) {
             defineClass("NoMethodError", nameError, nameError.getAllocator());
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
-            defineClass("LocalJumpError", standardError, standardError.getAllocator());
+            RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
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
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
         if (profile.allowModule("Signal")) RubySignal.createSignal(this);
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
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null)
             jrubyClassLoader = new JRubyClassLoader(Thread.currentThread().getContextClassLoader());
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
 
     public Node parse(Reader content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, lineNumber);
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, lineNumber);
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
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
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
     public void loadScript(RubyString scriptName, RubyString source) {
         loadScript(scriptName.toString(), new StringReader(source.toString()));
     }
 
     public void loadScript(String scriptName, Reader source) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             };
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parse(source, scriptName, null, 0);
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
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
 
             script.run(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
     public void loadNode(String scriptName, Node node) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             EvaluationState.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 // Make sure this does not bubble out to java caller.
             } else {
                 throw je;
             }
         } finally {
             context.postNodeEval();
         }
     }
 
 
     /** Loads, compiles and interprets a Ruby file.
      *  Used by Kernel#require.
      *
      *  @mri rb_load
      */
     public void loadFile(File file) {
         assert file != null : "No such file to load";
         BufferedReader source = null;
         try {
             source = new BufferedReader(new FileReader(file));
             loadScript(file.getPath().replace(File.separatorChar, '/'), source);
         } catch (IOException ioExcptn) {
             throw newIOErrorFromException(ioExcptn);
         } finally {
             try {
                 if (source == null) {
                     source.close();
                 }
             } catch (IOException ioe) {}
         }
     }
 
     /** Call the trace function
      *
      * MRI: eval.c - call_trace_func
      *
      */
     public void callTraceFunction(ThreadContext context, String event, ISourcePosition position,
             RubyBinding binding, String name, IRubyObject type) {
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
                     binding != null ? binding : getNil(),
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
     
     public void addFinalizer(RubyObject.Finalizer finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap();
             }
         }
         
         synchronized (finalizers) {
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeFinalizer(RubyObject.Finalizer finalizer) {
         if (finalizers != null) {
             synchronized (finalizers) {
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
             RubyProc proc = (RubyProc) atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator finalIter = finalizers.keySet().iterator(); finalIter.hasNext();) {
                     ((RubyObject.Finalizer)finalIter.next()).finalize();
                     finalIter.remove();
                 }
             }
         }
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
     
     public RubyString newStringShared(ByteList byteList) {
         return RubyString.newStringShared(this, byteList);
     }    
 
     public RubySymbol newSymbol(String string) {
         return RubySymbol.newSymbol(this, string);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(getClass("RuntimeError"), message);
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
 
-    public RaiseException newLocalJumpError(String message) {
-        return newRaiseException(getClass("LocalJumpError"), message);
+    public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
+        return new RaiseException(new RubyLocalJumpError(this, getClass("LocalJumpError"), message, reason, exitValue), true);
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
         return newRaiseException(getClass("TypeError"), "wrong argument type " +
                 receivedObject.getMetaClass() + " (expected " + expectedType + ")");
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
 
     public static boolean isSecurityRestricted() {
         return (System.getSecurityManager() != null);
     }
 }
diff --git a/src/org/jruby/RubyLocalJumpError.java b/src/org/jruby/RubyLocalJumpError.java
new file mode 100644
index 0000000000..39419fdad8
--- /dev/null
+++ b/src/org/jruby/RubyLocalJumpError.java
@@ -0,0 +1,71 @@
+/***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
+ * 
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+
+package org.jruby;
+
+import org.jruby.runtime.Arity;
+import org.jruby.runtime.Block;
+import org.jruby.runtime.ObjectAllocator;
+import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.runtime.CallbackFactory;
+
+public class RubyLocalJumpError extends RubyException {
+    private static ObjectAllocator LOCALJUMPERROR_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
+            return new RubyLocalJumpError(runtime, klass);
+        }
+    };
+
+    public static RubyClass createLocalJumpErrorClass(Ruby runtime, RubyClass standardErrorClass) {
+        RubyClass nameErrorClass = runtime.defineClass("LocalJumpError", standardErrorClass, LOCALJUMPERROR_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyLocalJumpError.class);	
+        
+        nameErrorClass.defineFastMethod("reason", callbackFactory.getFastMethod("reason"));
+        nameErrorClass.defineFastMethod("exit_value", callbackFactory.getFastMethod("exitValue"));
+
+        return nameErrorClass;
+    }
+    
+    private RubyLocalJumpError(Ruby runtime, RubyClass exceptionClass) {
+        super(runtime, exceptionClass);
+    }
+
+    public RubyLocalJumpError(Ruby runtime, RubyClass exceptionClass, String message, String reason, IRubyObject exitValue) {
+        super(runtime, exceptionClass, message);
+        setInstanceVariable("reason", runtime.newSymbol(reason));
+        setInstanceVariable("exit_value", exitValue);
+    }
+
+    public IRubyObject reason() {
+        return getInstanceVariable("reason");
+    }
+    
+    public IRubyObject exitValue() {
+        return getInstanceVariable("exit_value");
+    }
+}
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index d3e30feeac..6665ce92c0 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1485 +1,1485 @@
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
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 
 import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
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
 import java.util.List;
 import java.util.Map;
 import org.jruby.ast.Node;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     
     private RubyObject(){};
     // An instance that never equals any other instance
     public static final IRubyObject NEVER = new RubyObject();
     
     // The class of this object
     protected RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     private transient Object dataStruct;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
     
     private Finalizer finalizer;
     
     public class Finalizer {
         private long id;
         private List finalizers;
         private AtomicBoolean finalized;
         
         public Finalizer(long id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
         
         public void addFinalizer(RubyProc finalizer) {
             if (finalizers == null) {
                 finalizers = new ArrayList();
             }
             finalizers.add(finalizer);
         }
 
         public void removeFinalizers() {
             finalizers = null;
         }
     
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (finalizers != null) {
                     IRubyObject idFixnum = getRuntime().newFixnum(id);
                     for (int i = 0; i < finalizers.size(); i++) {
                         ((RubyProc)finalizers.get(i)).call(
                                 new IRubyObject[] {idFixnum});
                     }
                 }
             }
         }
     }
 
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
     
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObject.class);   
         objectClass.index = ClassIndex.OBJECT;
         
         objectClass.definePrivateMethod("initialize", callbackFactory.getOptMethod("initialize"));
         objectClass.definePrivateMethod("inherited", callbackFactory.getMethod("inherited", IRubyObject.class));
         
         return objectClass;
     }
     
     public static ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             IRubyObject instance = new RubyObject(runtime, klass);
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
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
 
         if (original.safeHasInstanceVariables()) {
             clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         }
         
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
 
     public RubyHash convertToHash() {
         return (RubyHash)convertToType(getRuntime().getHash(), MethodIndex.TO_HASH, "to_hash", true, true, false);
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
                 throw getRuntime().newTypeError("can't convert " + trueFalseNil(this) + " into " + trueFalseNil(targetType.getName()));
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
 
             return yieldUnder(mod, new IRubyObject[] { this }, block);
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
 
     private IRubyObject yieldUnder(RubyModule under, IRubyObject[] args, Block block) {
         final IRubyObject selfInYield = this;
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield;
                     boolean aValue;
                     if (args.length == 1) {
                         valueInYield = args[0];
                         aValue = false;
                     } else {
                         valueInYield = RubyArray.newArray(getRuntime(), args);
                         aValue = true;
                     }
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), aValue);
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
         }, args, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
             String file, int lineNumber) {
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
             Node node = 
                 getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope(), lineNumber);
 
             return EvaluationState.eval(getRuntime(), threadContext, node, newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
-                throw getRuntime().newLocalJumpError("unexpected break");
+                throw getRuntime().newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
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
             Node node = getRuntime().parse(src.toString(), file, context.getCurrentScope(), 0);
             
             return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
-                throw getRuntime().newLocalJumpError("unexpected break");
+                throw getRuntime().newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
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
 
     /** rb_eql
      *  this method is not defind for Ruby objects directly.
      *  notably overriden by RubyFixnum, RubyString, RubySymbol - these do a short-circuit calls.
      *  see: rb_any_cmp() in hash.c
      *  do not confuse this method with eql_p methods (which it calls by default), eql is mainly used for hash key comparison 
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     public final boolean eqlInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return true;
         return callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
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
         return getRuntime().newFixnum(super.hashCode());
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return super.hashCode();
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
     public IRubyObject rbClone(Block unusedBlock) {
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
 
     public IRubyObject instance_exec(IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw getRuntime().newArgumentError("block not supplied");
         }
         return yieldUnder(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             IRubyObject obj;
             if (!(((obj = args[i]) instanceof RubyModule) && ((RubyModule)obj).isModule())){
                 throw getRuntime().newTypeError(obj,getRuntime().getClass("Module"));
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
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
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
                         runtime.newString(name),
                         runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : getType().getName()),
                         runtime.newString(name),
                         runtime.newString(description),
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
  
     public void addFinalizer(RubyProc finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 }
diff --git a/src/org/jruby/RubyProc.java b/src/org/jruby/RubyProc.java
index 884652cbe9..9e8c7835bb 100644
--- a/src/org/jruby/RubyProc.java
+++ b/src/org/jruby/RubyProc.java
@@ -1,194 +1,195 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author  jpetersen
  */
 public class RubyProc extends RubyObject {
     private Block block = Block.NULL_BLOCK;
     private boolean isLambda;
 
     public RubyProc(Ruby runtime, RubyClass rubyClass, boolean isLambda) {
         super(runtime, rubyClass);
         
         this.isLambda = isLambda;
     }
     
     private static ObjectAllocator PROC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyProc instance = RubyProc.newProc(runtime, false);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public static RubyClass createProcClass(Ruby runtime) {
         RubyClass procClass = runtime.defineClass("Proc", runtime.getObject(), PROC_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyProc.class);
         
         procClass.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
         procClass.defineFastMethod("binding", callbackFactory.getFastMethod("binding"));
         procClass.defineMethod("call", callbackFactory.getOptMethod("call"));
         procClass.defineAlias("[]", "call");
         procClass.defineFastMethod("to_proc", callbackFactory.getFastMethod("to_proc"));
         procClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
 
         procClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
         
         return procClass;
     }
 
     public Block getBlock() {
         return block;
     }
 
     // Proc class
 
     public static RubyProc newProc(Ruby runtime, boolean isLambda) {
         return new RubyProc(runtime, runtime.getClass("Proc"), isLambda);
     }
     
     public IRubyObject initialize(IRubyObject[] args, Block procBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
         if (procBlock == null) {
             throw getRuntime().newArgumentError("tried to create Proc object without a block");
         }
         
         if (isLambda && procBlock == null) {
             // TODO: warn "tried to create Proc object without a block"
         }
         
         block = procBlock.cloneBlock();
         block.isLambda = isLambda;
         block.setProcObject(this);
 
         return this;
     }
     
     protected IRubyObject doClone() {
     	RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getClass("Proc"), isLambda);
     	
     	newProc.block = getBlock();
     	
     	return newProc;
     }
     
     /**
      * Create a new instance of a Proc object.  We override this method (from RubyClass)
      * since we need to deal with special case of Proc.new with no arguments or block arg.  In 
      * this case, we need to check previous frame for a block to consume.
      */
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         IRubyObject obj = ((RubyClass) recv).allocate();
         
         // No passed in block, lets check next outer frame for one ('Proc.new')
         if (!block.isGiven()) {
             block = runtime.getCurrentContext().getPreviousFrame().getBlock();
         }
         
         obj.callMethod(runtime.getCurrentContext(), "initialize", args, block);
         return obj;
     }
 
     public IRubyObject binding() {
         return getRuntime().newBinding(block);
     }
 
     public IRubyObject call(IRubyObject[] args) {
         return call(args, null, Block.NULL_BLOCK);
     }
 
     // ENEBO: For method def others are Java to java versions
     public IRubyObject call(IRubyObject[] args, Block unusedBlock) {
         return call(args, null, Block.NULL_BLOCK);
     }
     
     public IRubyObject call(IRubyObject[] args, IRubyObject self, Block unusedBlock) {
         assert args != null;
         
-        ThreadContext context = getRuntime().getCurrentContext();
+        Ruby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
         
         try {
             if (block.isLambda) {
                 block.arity().checkArity(getRuntime(), args);
             }
             
             Block newBlock = block.cloneBlock();
             if (self != null) newBlock.setSelf(self);
             
             // if lambda, set new jump target in (duped) frame for returns
             if (newBlock.isLambda) newBlock.getFrame().setJumpTarget(this);
             
             return newBlock.call(context, args);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 if (block.isLambda) return (IRubyObject) je.getValue();
                 
-                throw getRuntime().newLocalJumpError("unexpected return");
+                throw runtime.newLocalJumpError("break", (IRubyObject)je.getValue(), "break from proc-closure");
             } else if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 Object target = je.getTarget();
                 
                 if (target == this || block.isLambda) return (IRubyObject) je.getValue();
                 
                 if (target == null) {
-                    throw getRuntime().newLocalJumpError("unexpected return");
+                    throw runtime.newLocalJumpError("return", (IRubyObject)je.getValue(), "unexpected return");
                 }
                 throw je;
             } else {
                 throw je;
             }
         } 
     }
 
     public RubyFixnum arity() {
         return getRuntime().newFixnum(block.arity().getValue());
     }
     
     public RubyProc to_proc() {
     	return this;
     }
 }
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index c975f34884..f718a786e4 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,2172 +1,2190 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.MetaClass;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBinding;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
+import org.jruby.RubyLocalJumpError;
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
 import org.jruby.ast.PostExeNode;
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
 import org.jruby.runtime.SharedScopeBlock;
 import org.jruby.runtime.MethodIndex;
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
             if (node == null) return nilNode(runtime, context);
 
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
                 return nilNode(runtime, context);
             case NodeTypes.NOTNODE:
                 return notNode(runtime, context, node, self, aBlock);
             case NodeTypes.NTHREFNODE:
                 return nthRefNode(context, node);
             case NodeTypes.OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
         
                 // add in reverse order
                 IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return pollAndReturn(context, result);
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
             case NodeTypes.POSTEXENODE:
                 return postExeNode(runtime, context, node, self, aBlock);
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
                 return pollAndReturn(context, self);
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
         IRubyObject newValue = value.convertToType(runtime.getArray(), MethodIndex.TO_ARY, "to_ary", false);
         if (newValue.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime
                     .getKernel()) {
                 newValue = value.convertToType(runtime.getArray(), MethodIndex.TO_A, "to_a", false);
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
             return value.convertToType(runtime.getArray(), MethodIndex.TO_A, "to_ary", false);
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
    
         throw context.prepareJumpException(JumpException.JumpType.BreakJump, null, result);
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
             if (iVisited.index != 0) {
                 return receiver.callMethod(context, module, iVisited.index, iVisited.getName(), args, CallType.NORMAL, Block.NULL_BLOCK);
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
 
                             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                                     .isTrue())
                                     || (expression == null && condition.isTrue())) {
                                 node = ((WhenNode) firstWhenNode).getBodyNode();
                                 return evalInternal(runtime, context, node, self, aBlock);
                             }
                         }
                         continue;
                     }
 
                     result = evalInternal(runtime,context, tag, self, aBlock);
 
                     if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
                             || (expression == null && result.isTrue())) {
                         node = whenNode.getBodyNode();
                         return evalInternal(runtime, context, node, self, aBlock);
                     }
                 }
             } else {
                 result = evalInternal(runtime,context, whenNode.getExpressionNodes(), self, aBlock);
 
                 if ((expression != null && result.callMethod(context, MethodIndex.OP_EQQ, "===", expression).isTrue())
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
         
         if (constNode == null) {
             return context.setConstantInCurrent(iVisited.getName(), result);
         } else if (constNode.nodeId == NodeTypes.COLON2NODE) {
             RubyModule module = (RubyModule)evalInternal(runtime,context, ((Colon2Node) iVisited.getConstNode()).getLeftNode(), self, aBlock);
             return context.setConstantInModule(iVisited.getName(), module, result);
         } else { // colon3
             return context.setConstantInObject(iVisited.getName(), result);
         }
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
         return evalInternal(runtime,context, ((EvStrNode)node).getBody(), self, aBlock).asString();
     }
     
     private static IRubyObject falseNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getFalse());
     }
 
     private static IRubyObject fCallNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         FCallNode iVisited = (FCallNode) node;
         
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
         Block block = getBlock(runtime, context, self, aBlock, iVisited.getIterNode());
         
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             RubyModule module = self.getMetaClass();
             if (module.index != 0 && iVisited.index != 0) {
                 return self.callMethod(context, module, iVisited.index, iVisited.getName(), args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
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
         
         Block block = SharedScopeBlock.createSharedScopeBlock(context, iVisited, 
                 context.getCurrentScope(), self);
    
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
                 context.setBackref(result);
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
    
         RubyHash hash = null;
         if (iVisited.getListNode() != null) {
             hash = RubyHash.newHash(runtime);
    
         for (int i = 0; i < iVisited.getListNode().size();) {
                 // insert all nodes in sequence, hash them in the final instruction
                 // KEY
                 IRubyObject key = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
                 IRubyObject value = evalInternal(runtime,context, iVisited.getListNode().get(i++), self, aBlock);
    
                 hash.fastASet(key, value);
             }
         }
    
         if (hash == null) {
             return RubyHash.newHash(runtime);
         }
    
         return hash;
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
         
         switch (iVisited.getValueNode().nodeId) {
         case NodeTypes.ARRAYNODE: {
             ArrayNode iVisited2 = (ArrayNode) iVisited.getValueNode();
             IRubyObject[] array = new IRubyObject[iVisited2.size()];
 
             for (int i = 0; i < iVisited2.size(); i++) {
                 Node next = iVisited2.get(i);
 
                 array[i] = evalInternal(runtime,context, next, self, aBlock);
             }
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
         }
         case NodeTypes.SPLATNODE: {
             SplatNode splatNode = (SplatNode)iVisited.getValueNode();
             RubyArray rubyArray = splatValue(runtime, evalInternal(runtime, context, ((SplatNode) splatNode).getValue(), self, aBlock));
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, rubyArray, false);
         }
         default:
             IRubyObject value = evalInternal(runtime, context, iVisited.getValueNode(), self, aBlock);
 
             if (!(value instanceof RubyArray)) {
                 value = RubyArray.newArray(runtime, value);
             }
             
             return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, (RubyArray)value, false);
         }
     }
 
     private static IRubyObject nextNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NextNode iVisited = (NextNode) node;
    
         context.pollThreadEvents();
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         // now used as an interpreter event
         throw context.prepareJumpException(JumpException.JumpType.NextJump, iVisited, result);
     }
 
     private static IRubyObject nilNode(Ruby runtime, ThreadContext context) {
         return pollAndReturn(context, runtime.getNil());
     }
 
     private static IRubyObject notNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         NotNode iVisited = (NotNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock);
         return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
     }
 
     private static IRubyObject nthRefNode(ThreadContext context, Node node) {
         return RubyRegexp.nth_match(((NthRefNode)node).getMatchNumber(), context.getBackref());
     }
     
     private static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
         context.pollThreadEvents();
         return result;
     }
 
     private static IRubyObject opAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpAsgnNode iVisited = (OpAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
         IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
    
         if (iVisited.getOperatorName() == "||") {
             if (value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else if (iVisited.getOperatorName() == "&&") {
             if (!value.isTrue()) {
                 return pollAndReturn(context, value);
             }
             value = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
         } else {
             value = value.callMethod(context, iVisited.index, iVisited.getOperatorName(), evalInternal(runtime,context,
                     iVisited.getValueNode(), self, aBlock));
         }
    
         receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
    
         return pollAndReturn(context, value);
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
    
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject opElementAsgnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
         IRubyObject receiver = evalInternal(runtime,context, iVisited.getReceiverNode(), self, aBlock);
    
         IRubyObject[] args = setupArgs(runtime, context, iVisited.getArgsNode(), self);
    
         IRubyObject firstValue = receiver.callMethod(context, MethodIndex.AREF, "[]", args);
    
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
             firstValue = firstValue.callMethod(context, iVisited.index, iVisited.getOperatorName(), evalInternal(runtime,context, iVisited
                             .getValueNode(), self, aBlock));
         }
    
         IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, expandedArgs, 0, args.length);
         expandedArgs[expandedArgs.length - 1] = firstValue;
         return receiver.callMethod(context, MethodIndex.ASET, "[]=", expandedArgs);
     }
 
     private static IRubyObject optNNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OptNNode iVisited = (OptNNode) node;
    
         IRubyObject result = runtime.getNil();
         outerLoop: while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
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
                         result = (IRubyObject) je.getValue();
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject orNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         OrNode iVisited = (OrNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getFirstNode(), self, aBlock);
    
         if (!result.isTrue()) {
             result = evalInternal(runtime,context, iVisited.getSecondNode(), self, aBlock);
         }
    
         return result;
     }
 
     private static IRubyObject postExeNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         PostExeNode iVisited = (PostExeNode) node;
         
         // FIXME: I use a for block to implement END node because we need a proc which captures
         // its enclosing scope.   ForBlock now represents these node and should be renamed.
         Block block = SharedScopeBlock.createSharedScopeBlock(context, iVisited, context.getCurrentScope(), self);
         
         runtime.pushExitBlock(runtime.newProc(true, block));
         
         return runtime.getNil();
     }
 
     private static IRubyObject redoNode(ThreadContext context, Node node) {
         context.pollThreadEvents();
    
         // now used as an interpreter event
         throw context.prepareJumpException(JumpException.JumpType.RedoJump, null, node);
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
             return RubyRegexp.newRegexp(runtime, iVisited.getValue().toString(), iVisited.getPattern(), iVisited.getFlags(), lang);
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
    
         throw context.prepareJumpException(JumpException.JumpType.RetryJump, null, null);
     }
     
     private static IRubyObject returnNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         ReturnNode iVisited = (ReturnNode) node;
    
         IRubyObject result = evalInternal(runtime,context, iVisited.getValueNode(), self, aBlock);
    
         throw context.prepareJumpException(JumpException.JumpType.ReturnJump, context.getFrameJumpTarget(), result);
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
 
         return evalClassDefinitionBody(runtime, context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self, aBlock);
     }
 
     private static IRubyObject splatNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         return splatValue(runtime, evalInternal(runtime,context, ((SplatNode) node).getValue(), self, aBlock));
     }
 
     private static IRubyObject strNode(Ruby runtime, Node node) {
         return runtime.newStringShared((ByteList) ((StrNode) node).getValue());
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
         return pollAndReturn(context, runtime.getTrue());
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
         
         outerLoop: while (!(result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
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
                         
                         result = (IRubyObject) je.getValue();
                         
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return pollAndReturn(context, result);
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
             return self.callMethod(context, module, iVisited.index, iVisited.getName(), 
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
         
         outerLoop: while (!firstTest || (result = evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
+                } catch (RaiseException re) {
+                    if (re.getException().isKindOf(runtime.getClass("LocalJumpError"))) {
+                        RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
+                        
+                        IRubyObject reason = jumpError.reason();
+                        
+                        // admittedly inefficient
+                        if (reason.asSymbol().equals("break")) {
+                            return jumpError.exitValue();
+                        } else if (reason.asSymbol().equals("next")) {
+                            break loop;
+                        } else if (reason.asSymbol().equals("redo")) {
+                            continue;
+                        }
+                    }
+                    
+                    throw re;
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
                         
                         break outerLoop;
                     default:
                         throw je;
                     }
                 }
             }
         }
         
         return pollAndReturn(context, result);
     }
 
     private static IRubyObject xStrNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self) {
         return self.callMethod(context, "`", runtime.newStringShared((ByteList) ((XStrNode) node).getValue()));
     }
 
     private static IRubyObject yieldNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         YieldNode iVisited = (YieldNode) node;
    
         IRubyObject result = null;
         if (iVisited.getArgsNode() != null) {
             result = evalInternal(runtime, context, iVisited.getArgsNode(), self, aBlock);
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
         runtime.callTraceFunction(context, event, context.getPosition(), RubyBinding.newBinding(runtime), name, type);
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
             if (isTrace(runtime)) {
                 callTraceFunction(runtime, context, "end", null);
             }
             
             context.postClassEval();
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
             // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
             return Block.createBlock(context, iterNode,
                     new DynamicScope(iterNode.getScope(), context.getCurrentScope()), self);
         } else if (blockNode instanceof BlockPassNode) {
             BlockPassNode blockPassNode = (BlockPassNode) blockNode;
             IRubyObject proc = evalInternal(runtime,context, blockPassNode.getBodyNode(), self, currentBlock);
 
             // No block from a nil proc
             if (proc.isNil()) return Block.NULL_BLOCK;
 
             // If not already a proc then we should try and make it one.
             if (!(proc instanceof RubyProc)) {
                 proc = proc.convertToType(runtime.getClass("Proc"), 0, "to_proc", false);
 
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
     public static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
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
 
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 }
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index d1ad29e3b3..d8d285b432 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,382 +1,382 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Block {
     /**
      * All Block variables should either refer to a real block or this NULL_BLOCK.
      */
     public static final Block NULL_BLOCK = new Block() {
         public boolean isGiven() {
             return false;
         }
         
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
-            throw context.getRuntime().newLocalJumpError("yield called out of block");
+            throw context.getRuntime().newLocalJumpError("noreason", (IRubyObject)value, "yield called out of block");
         }
         
         public Block cloneBlock() {
             return this;
         }
     };
 
     /**
      * 'self' at point when the block is defined
      */
     protected IRubyObject self;
 
     /**
      * AST Node representing the parameter (VARiable) list to the block.
      */
     private IterNode iterNode;
     
     /**
      * frame of method which defined this block
      */
     protected Frame frame;
     protected SinglyLinkedList cref;
     protected Visibility visibility;
     protected RubyModule klass;
     
     /**
      * A reference to all variable values (and names) that are in-scope for this block.
      */
     protected DynamicScope dynamicScope;
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
     public boolean isLambda = false;
     
     protected Arity arity;
 
     public static Block createBlock(ThreadContext context, IterNode iterNode, DynamicScope dynamicScope, IRubyObject self) {
         return new Block(iterNode,
                          self,
                          context.getCurrentFrame(),
                          context.peekCRef(),
                          context.getCurrentFrame().getVisibility(),
                          context.getRubyClass(),
                          dynamicScope);
     }
     
     protected Block() {
         this(null, null, null, null, null, null, null);
     }
 
     public Block(IterNode iterNode, IRubyObject self, Frame frame,
         SinglyLinkedList cref, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope) {
     	
         //assert method != null;
 
         this.iterNode = iterNode;
         this.self = self;
         this.frame = frame;
         this.visibility = visibility;
         this.klass = klass;
         this.cref = cref;
         this.dynamicScope = dynamicScope;
         this.arity = iterNode == null ? null : Arity.procArityOf(iterNode.getVarNode());
     }
     
     public static Block createBinding(Frame frame, DynamicScope dynamicScope) {
         ThreadContext context = frame.getSelf().getRuntime().getCurrentContext();
         
         // We create one extra dynamicScope on a binding so that when we 'eval "b=1", binding' the
         // 'b' will get put into this new dynamic scope.  The original scope does not see the new
         // 'b' and successive evals with this binding will.  I take it having the ability to have 
         // succesive binding evals be able to share same scope makes sense from a programmers 
         // perspective.   One crappy outcome of this design is it requires Dynamic and Static 
         // scopes to be mutable for this one case.
         
         // Note: In Ruby 1.9 all of this logic can go away since they will require explicit
         // bindings for evals.
         
         // We only define one special dynamic scope per 'logical' binding.  So all bindings for
         // the same scope should share the same dynamic scope.  This allows multiple evals with
         // different different bindings in the same scope to see the same stuff.
         DynamicScope extraScope = dynamicScope.getBindingScope();
         
         // No binding scope so we should create one
         if (extraScope == null) {
             // If the next scope out has the same binding scope as this scope it means
             // we are evaling within an eval and in that case we should be sharing the same
             // binding scope.
             DynamicScope parent = dynamicScope.getNextCapturedScope(); 
             if (parent != null && parent.getBindingScope() == dynamicScope) {
                 extraScope = dynamicScope;
             } else {
                 extraScope = new DynamicScope(new BlockStaticScope(dynamicScope.getStaticScope()), dynamicScope);
                 dynamicScope.setBindingScope(extraScope);
             }
         } 
         
         // FIXME: Ruby also saves wrapper, which we do not
         return new Block(null, frame.getSelf(), frame, context.peekCRef(), frame.getVisibility(), 
                 context.getBindingRubyClass(), extraScope);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true);
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         context.preYieldSpecificBlock(this, klass);
     }
     
     protected void post(ThreadContext context) {
         context.postYield();
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return yield(context, value, null, null, false);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
         
         pre(context, klass);
 
         try {
             if (iterNode.getVarNode() != null) {
                 if (aValue) {
                     setupBlockArgs(context, iterNode.getVarNode(), value, self);
                 } else {
                     setupBlockArg(context, iterNode.getVarNode(), value, self);
                 }
             }
             
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return EvaluationState.eval(context.getRuntime(), context, iterNode.getBodyNode(), self, NULL_BLOCK);
                 } catch (JumpException je) {
                     if (je.getJumpType() == JumpException.JumpType.RedoJump) {
                         context.pollThreadEvents();
                         // do nothing, allow loop to redo
                     } else {
                         if (je.getJumpType() == JumpException.JumpType.BreakJump && je.getTarget() == null) {
                             je.setTarget(this);                            
                         }                        
                         throw je;
                     }
                 }
             }
             
         } catch (JumpException je) {
             // A 'next' is like a local return from the block, ending this call or yield.
         	if (je.getJumpType() == JumpException.JumpType.NextJump) return (IRubyObject) je.getValue();
 
             throw je;
         } finally {
             post(context);
         }
     }
 
     private void setupBlockArgs(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case NodeTypes.ZEROARGNODE:
             break;
         case NodeTypes.MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
             break;
         default:
             int length = arrayLength(value);
             switch (length) {
             case 0:
                 value = runtime.getNil();
                 break;
             case 1:
                 value = ((RubyArray)value).eltInternal(0);
                 break;
             default:
                 runtime.getWarnings().warn("multiple values for a block parameter (" + length + " for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
 
     private void setupBlockArg(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
         case NodeTypes.ZEROARGNODE:
             return;
         case NodeTypes.MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode,
                     ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null), false);
             break;
         default:
             if (value == null) {
                 runtime.getWarnings().warn("multiple values for a block parameter (0 for 1)");
             }
             AssignmentVisitor.assign(runtime, context, self, varNode, value, Block.NULL_BLOCK, false);
         }
     }
     
     private int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
 
     public Block cloneBlock() {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         Block newBlock = new Block(iterNode, self, frame.duplicate(), cref, visibility, klass, 
                 dynamicScope.cloneScope());
         
         newBlock.isLambda = isLambda;
 
         return newBlock;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 
     public Visibility getVisibility() {
         return visibility;
     }
 
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     public void setSelf(IRubyObject self) {
         this.self = self;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 
     /**
      * Retrieve the proc object associated with this block
      * 
      * @return the proc or null if this has no proc associated with it
      */
     public RubyProc getProcObject() {
     	return proc;
     }
     
     /**
      * Set the proc object associated with this block
      * 
      * @param procObject
      */
     public void setProcObject(RubyProc procObject) {
     	this.proc = procObject;
     }
 
     /**
      * Gets the dynamicVariables that are local to this block.   Parent dynamic scopes are also
      * accessible via the current dynamic scope.
      * 
      * @return Returns all relevent variable scoping information
      */
     public DynamicScope getDynamicScope() {
         return dynamicScope;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return frame;
     }
 
     /**
      * Gets the klass.
      * @return Returns a RubyModule
      */
     public RubyModule getKlass() {
         return klass;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
 }
diff --git a/test/test_block.rb b/test/test_block.rb
index cb44613e64..409b3b8006 100644
--- a/test/test_block.rb
+++ b/test/test_block.rb
@@ -1,236 +1,258 @@
 require 'test/unit'
 
 class TestBlock < Test::Unit::TestCase
   def test_block_variable_closure
     values = []
     5.times do |i|
       values.push i
     end
     assert_equal([0,1,2,3,4], values)
 
     values = []
     2.step 10, 2 do |i|
       values.push i
     end
     assert_equal([2,4,6,8,10], values)
   end
 
   def test_block_break
     values = []
     [1,2,3].each {|v| values << v; break }
     assert_equal([1], values)
 
     values = []
     result = [1,2,3,4,5].collect {|v|
       if v > 2
         break
       end
       values << v
       v
     }
     assert_equal([1,2], values)
     assert(result.nil?)
   end
 
   def method1
     if object_id   # Any non-toplevel method will do
       yield
     end
   end
   def method2
     method1 {
       yield
     }
   end
 
   def test_block_yield
     flag = false
     method2 {
       flag = true
     }
     assert(flag)
   end
 
   class TestBlock_Foo
     def foo
       Proc.new { self }
     end
   end
 
   def test_proc_as_block_arg
     proc = TestBlock_Foo.new.foo
     o = Object.new
     assert_equal(o, o.instance_eval(&proc))
   end
 
   def test_proc_arity
     assert_equal(-1, Proc.new { 1 }.arity)
     #assert_equal(0, Proc.new{|| 1 }.arity)
     #assert_equal(2, Proc.new {|x,y| 1}.arity)
     assert_equal(-1, Proc.new{|*x| 1}.arity)
   end
 
   def f; yield; end
   def test_yield_with_zero_arity
     f {|*a| assert(a == []) }
   end
 
   class A
     def foo
       yield
     end
   end
 
   class B < A
     def foo
       super
     end
   end
 
   def test_block_passed_to_super
     assert_equal("bar", B.new.foo { "bar" })
   end
 
   # test blocks being available to procs (JRUBY-91)
   class Baz
     def foo
       bar do
         qux
       end
     end
 
     def bar(&block)
       block.call
     end
 
     def qux
       if block_given?
         return false
       end
       return true
     end
   end
 
   def test_block_available_to_proc
     assert(Baz.new.foo { })
   end
 
   # test instance_evaling with more complicated block passing (JRUBY-88)
   $results = []
   class C
     def t(&block)
       if block
         instance_eval &block
       end
     end
     def method_missing(sym, *args, &block)
       $results << "C: #{sym} #{!block}"
       if sym == :b
         return D.new { |block|
           t(&block)
         }
       end
       t(&block)
     end
   end
 
   class D
     def initialize(&blk)
       @blk = blk
     end
 
     def method_missing(sym, *args, &block)
       $results << "D: #{sym} #{!block}"
       @blk.call(block)
     end
   end
   def do_it(&blk)
     C.new.b.c {
       a 'hello'
     }
   end
 
   def test_block_passing_with_instance_eval
     do_it {
     }
     assert_equal(["C: b true", "D: c false", "C: a true"], $results)
   end
 
+  if defined? instance_exec
   def test_instance_exec_self
     o = Object.new
     assert_equal(o, o.instance_exec { self })
   end
 
   def test_instance_exec_self_args
     o = Object.new
     assert_equal(o, o.instance_exec(1) { self })
   end
 
   def test_instance_exec_args_result
     o = Object.new
     assert_equal(2, o.instance_exec(1) { |x| x + 1 })
   end
 
   def test_instance_exec_args_multiple_result
     o = Object.new
     assert_equal([1, 3], o.instance_exec(1, 2, 3) { |a, b, c| [a, c] })
   end
 
   def test_instance_exec_no_block
     o = Object.new
     assert_raise(ArgumentError) { o.instance_exec }
   end
 
   def test_instance_exec_no_block_args
     o = Object.new
     assert_raise(ArgumentError) { o.instance_exec(1) }
   end
-
+  end # if defined? instance_exec
+  
   # ensure proc-ified blocks can be yielded to when no block arg is specified in declaration
   class Holder
     def call_block
       yield
     end
   end
 
   class Creator
     def create_block
       proc do
         yield
       end
     end
   end
 
   def test_block_converted_to_proc_yields
     block = Creator.new.create_block { "here" }
     assert_nothing_raised {Holder.new.call_block(&block)}
     assert_equal("here", Holder.new.call_block(&block))
   end
 
   def proc_call(&b)
     b.call
   end
 
   def proc_return1
     proc_call{return 42}+1
   end
 
   def proc_return2
     puts proc_call{return 42}+1
   end
 
   def test_proc_or_block_return
     assert_nothing_raised { assert_equal 42, proc_return1 }
     assert_nothing_raised { assert_equal 42, proc_return2 }
   end
 
   def bar(a, b)
     yield a, b
   end
   
   def test_block_hash_args
     h = Hash.new
     bar(1, 2) { |h[:v], h[:u]| }
     puts h[:v], h[:u]
   end
+
+  def block_arg_that_breaks_while(&block)
+    while true
+      block.call
+    end
+  end
+  
+  def block_that_breaks_while
+    while true
+      yield
+    end
+  end
+
+  def test_block_arg_that_breaks_while
+    assert_nothing_raised { block_arg_that_breaks_while { break }}
+  end
+  
+  def test_block_that_breaks_while
+    assert_nothing_raised { block_that_breaks_while { break }}
+  end
 end
