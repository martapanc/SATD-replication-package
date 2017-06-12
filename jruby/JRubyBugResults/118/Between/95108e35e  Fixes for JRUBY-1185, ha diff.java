diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 523c1c9b89..533f954215 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1100 +1,1101 @@
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
 import java.util.ArrayList;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.LinkedList;
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
 import org.jruby.libraries.IOWaitLibrary;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.ext.Generator;
 import org.jruby.ext.Readline;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.MethodCache;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "yaml/syck", "jsignal" };
 
     private CacheMap cacheMap = new CacheMap(this);
     private MethodCache methodCache = new MethodCache();
     private ThreadService threadService = new ThreadService(this);
     private Hashtable runtimeInformation;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable ioHandlers = new Hashtable();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private List eventHooks = new LinkedList();
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
 
     private static boolean securityRestricted = false;
 
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
 
     public IRubyObject evalScript(Reader reader, String name) {
         return eval(parse(reader, name, getCurrentContext().getCurrentScope(), 0, false));
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
                 throw newLocalJumpError("return", (IRubyObject)je.getValue(), "unexpected return");
                 //              return (IRubyObject)je.getSecondaryData();
             } else if(je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw newLocalJumpError("break", (IRubyObject)je.getValue(), "unexpected break");
             } else if(je.getJumpType() == JumpException.JumpType.RedoJump) {
                 throw newLocalJumpError("redo", (IRubyObject)je.getValue(), "unexpected redo");
             }
 
             throw je;
         }
     }
     
     public IRubyObject compileOrFallbackAndRun(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             
             // do the compile if JIT is enabled
             if (config.isJitEnabled() && !hasEventHooks()) {
             Script script = null;
                 try {
                     StandardASMCompiler compiler = new StandardASMCompiler(node);
                     NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
                     Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
 
                     script = (Script)scriptClass.newInstance();
                     if (config.isJitLogging()) {
                         System.err.println("compiled: " + node.getPosition().getFile());
                     }
                 } catch (Throwable t) {
                     if (config.isJitLoggingVerbose()) {
                         System.err.println("coult not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
                     }
                     return eval(node);
                 }
             
                 // FIXME: Pass something better for args and block here?
                 return script.run(getCurrentContext(), tc.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
             } else {
                 return eval(node);
             }
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
             ThreadContext tc = getCurrentContext();
             
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
 
             Class scriptClass = compiler.loadClass(this.getJRubyClassLoader());
 
             Script script = (Script)scriptClass.newInstance();
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), tc.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
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
      * Retrieve method cache.
      * 
      * @return method cache where cached methods have been stored
      */
     public MethodCache getMethodCache() {
         return methodCache;
     }
 
     /**
      * @see org.jruby.Ruby#getRuntimeInformation
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
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
         
         methodCache.initialized();
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
 
         final Library NO_OP_LIBRARY = new Library() {
                 public void load(Ruby runtime) throws IOException {
                 }
             };
 
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
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
         registerBuiltin("io/wait.so", new IOWaitLibrary());
         registerBuiltin("etc.so", NO_OP_LIBRARY);
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
 
     public Node parse(Reader content, String file, DynamicScope scope, int lineNumber, boolean isInlineScript) {
         return parser.parse(file, content, scope, lineNumber, false, isInlineScript);
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, lineNumber);
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content, scope, lineNumber, extraPositionInformation);
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
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index e84b7a10a3..f753d02d5f 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -496,1468 +496,1472 @@ public class RubyModule extends RubyObject {
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
 
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
             for (p = getSuperClass(); p != null; p = p.getSuperClass()) {
                 if (p instanceof IncludedModuleWrapper) {
                     if (p.getNonIncludedClass() == module.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             c = p;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
             if (!skip) {
                 // In the current logic, if we get here we know that module is not an 
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just 
                 // in case the logic should change later, let's do it anyway:
                 c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             getRuntime().getMethodCache().clearCache();
             /*
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
             List methodNames = new ArrayList(((RubyModule) arg).getMethods().keySet());
             for (Iterator iter = methodNames.iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
             */
         }
 
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn("undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = getInstanceVariable("__attached__");
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         }else{
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
     }
     }
     
     public IRubyObject include_p(IRubyObject arg) {
         if (!((arg instanceof RubyModule) && ((RubyModule)arg).isModule())){
             throw getRuntime().newTypeError(arg, getRuntime().getClass("Module"));
         }
         
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if ((p instanceof IncludedModuleWrapper) && ((IncludedModuleWrapper) p).getNonIncludedClass() == arg) {
                 return getRuntime().newBoolean(true);
             }
         }
         
         return getRuntime().newBoolean(false);
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             /*
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
             */
             getRuntime().getMethodCache().removeMethod(name);
             putMethod(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
         getMethods().remove(name);
     }
 
     public void removeMethod(String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
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
             
             getRuntime().getMethodCache().removeMethod(name);
 
             //getRuntime().getCacheMap().remove(name, method);
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
         MethodCache cache = getRuntime().getMethodCache();
         MethodCache.CacheEntry entry = cache.getMethod(this, name);
         if (entry.klass == this && name.equals(entry.mid)) {
             return entry.method;
         }
         
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 
                 if (method != null) {
                     cache.putMethod(this, name, method);
                     /*
                     // TO BE REMOVED
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
                     */
 
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
         IRubyObject undef = getRuntime().getUndef();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getInstanceVariable(name);
 
                 if (constant == undef) {
                     p.removeInstanceVariable(name);
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) == null) break;
                     continue;
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
         IRubyObject constant = getInstanceVariable(name);
 
         if (constant != getRuntime().getUndef()) return constant;
         
         removeInstanceVariable(name);
         return getRuntime().getLoadService().autoload(getName() + "::" + name);
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
         getRuntime().getMethodCache().removeMethod(name);
         //getRuntime().getCacheMap().remove(name, searchMethod(name));
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getInstanceVariable(name);
         
         if (type == null || (type instanceof RubyUndef)) {
             if (classProviders != null) {
                 if ((type = searchClassProviders(name, superClazz)) != null) {
                     return (RubyClass)type;
                 }
             }
             ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
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
         IRubyObject type = getInstanceVariable(name);
 
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
                     Arity.checkArgumentCount(getRuntime(), args, 0, 0);
 
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
                     // ENEBO: Can anyone get args to be anything but length 1?
                     Arity.checkArgumentCount(getRuntime(), args, 1, 1);
 
                     return self.setInstanceVariable(variableName, args[0]);
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
 
     /** rb_mod_cvar_get
     *
     */
     public IRubyObject class_variable_get(IRubyObject var) {
         String varName = var.asSymbol();
 
         if (!IdUtil.isClassVariable(varName)) {
             throw getRuntime().newNameError("`" + varName + "' is not allowed as a class variable name", varName);
         }
 
         return getClassVar(varName);
     }
 
     /** rb_mod_cvar_set
     *
     */
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         String varName = var.asSymbol();
 
         if (!IdUtil.isClassVariable(varName)) {
             throw getRuntime().newNameError("`" + varName + "' is not allowed as a class variable name", varName);
         }
 
         return setClassVar(varName, value);
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method instanceof UndefinedMethod) {
                 
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
-        if (block.isGiven()) block.yield(getRuntime().getCurrentContext(), null, this, this, false);
+        if (block.isGiven()) {
+            // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
+            block.setVisibility(Visibility.PUBLIC);
+            block.yield(getRuntime().getCurrentContext(), null, this, this, false);
+        }
         
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
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
         
         return getRuntime().newBoolean(getInstanceVariable(name) != null);
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
             if (variable == getRuntime().getUndef()) {
                 getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + id);
             }
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
     public RubyModule append_features(IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             // MRI error message says Class, even though Module is ok 
             throw getRuntime().newTypeError(module,getRuntime().getClass("Class"));
         }
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj;
             if (!(((obj = modules[i]) instanceof RubyModule) && ((RubyModule)obj).isModule())){
                 throw getRuntime().newTypeError(obj,getRuntime().getClass("Module"));
             }
         }
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
 
     public IRubyObject public_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility().isPublic());
     }
 
     public IRubyObject protected_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility().isProtected());
     }
 	
     public IRubyObject private_method_defined(IRubyObject symbol) {
 	    DynamicMethod method = searchMethod(symbol.asSymbol());
 	    
 		return getRuntime().newBoolean(!method.isUndefined() && method.getVisibility().isPrivate());
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
 
     public RubyModule undef_method(IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(args[i].asSymbol());
         }
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
     
     /* Module class methods */
     
     /** 
      * Return an array of nested modules or classes.
      */
     public static RubyArray nesting(IRubyObject recv, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyModule object = runtime.getObject();
         SinglyLinkedList base = runtime.getCurrentContext().peekCRef();
         RubyArray result = runtime.newArray();
         
         for (SinglyLinkedList current = base; current.getValue() != object; current = current.getNext()) {
             result.append((RubyModule)current.getValue());
         }
         
         return result;
     }
 }
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index c5df58d701..b33116d831 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,577 +1,580 @@
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
 package org.jruby;
 
 import java.util.List;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 
 /**
  * @author  jpetersen
  */
 public class RubyStruct extends RubyObject {
     private IRubyObject[] values;
 
     /**
      * Constructor for RubyStruct.
      * @param runtime
      * @param rubyClass
      */
     public RubyStruct(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public static RubyClass createStructClass(Ruby runtime) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
         // work with marshalling. Confirm behavior and ensure we're doing this correctly. JRUBY-415
         RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         structClass.index = ClassIndex.STRUCT;
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
         structClass.includeModule(runtime.getModule("Enumerable"));
 
         structClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
 
         structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         structClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", RubyKernel.IRUBY_OBJECT));
         structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));
 
         structClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         structClass.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
 
         structClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         structClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         structClass.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("values", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("size", callbackFactory.getFastMethod("size"));
         structClass.defineFastMethod("length", callbackFactory.getFastMethod("size"));
         structClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
 
         structClass.defineMethod("each", callbackFactory.getMethod("each"));
         structClass.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));
         structClass.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         structClass.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         structClass.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
 
         structClass.defineFastMethod("members", callbackFactory.getFastMethod("members"));
 
         return structClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.STRUCT;
     }
     
     private static IRubyObject getInstanceVariable(RubyClass type, String name) {
         RubyClass structClass = type.getRuntime().getClass("Struct");
 
         while (type != null && type != structClass) {
             IRubyObject variable = type.getInstanceVariable(name);
             if (variable != null) {
                 return variable;
             }
 
             type = type.getSuperClass();
         }
 
         return type.getRuntime().getNil();
     }
 
     private RubyClass classOf() {
         return getMetaClass() instanceof MetaClass ? getMetaClass().getSuperClass() : getMetaClass();
     }
 
     private void modify() {
         testFrozen("Struct is frozen");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify struct");
         }
     }
     
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         int h = getMetaClass().getRealClass().hashCode();
 
         for (int i = 0; i < values.length; i++) {
             h = (h << 1) | (h < 0 ? 1 : 0);
             h ^= RubyNumeric.num2long(values[i].callMethod(context, MethodIndex.HASH, "hash"));
         }
         
         return runtime.newFixnum(h);
     }
 
     private IRubyObject setByName(String name, IRubyObject value) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     // Struct methods
 
     /** Create new Struct class.
      *
      * MRI: rb_struct_s_def / make_struct
      *
      */
     public static RubyClass newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = null;
         boolean nilName = false;
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, -1);
 
         if (args.length > 0) {
             if (args[0] instanceof RubyString) {
                 name = args[0].toString();
             } else if (args[0].isNil()) {
                 nilName = true;
             }
         }
 
         RubyArray member = runtime.newArray();
 
         for (int i = (name == null && !nilName) ? 0 : 1; i < args.length; i++) {
             member.append(RubySymbol.newSymbol(runtime, args[i].asSymbol()));
         }
 
         RubyClass newStruct;
         RubyClass superClass = (RubyClass)recv;
 
         if (name == null || nilName) {
             newStruct = new RubyClass(superClass, STRUCT_INSTANCE_ALLOCATOR);
         } else {
             if (!IdUtil.isConstant(name)) {
                 throw runtime.newNameError("identifier " + name + " needs to be constant", name);
             }
 
             IRubyObject type = superClass.getConstantAt(name);
 
             if (type != null) {
                 runtime.getWarnings().warn(runtime.getCurrentContext().getFramePosition(), "redefining constant Struct::" + name);
             }
             newStruct = superClass.newSubClass(name, STRUCT_INSTANCE_ALLOCATOR, superClass.getCRef(), false);
         }
 
         newStruct.index = ClassIndex.STRUCT;
         
         newStruct.setInstanceVariable("__size__", member.length());
         newStruct.setInstanceVariable("__member__", member);
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
         newStruct.getSingletonClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("members", callbackFactory.getSingletonMethod("members"));
 
         // define access methods.
         for (int i = (name == null && !nilName) ? 0 : 1; i < args.length; i++) {
             String memberName = args[i].asSymbol();
             newStruct.defineMethod(memberName, callbackFactory.getMethod("get"));
             newStruct.defineMethod(memberName + "=", callbackFactory.getMethod("set", RubyKernel.IRUBY_OBJECT));
         }
         
         if (block.isGiven()) {
+            // Struct bodies should be public by default, so set block visibility to public. JRUBY-1185.
+            block.setVisibility(Visibility.PUBLIC);
             block.yield(runtime.getCurrentContext(), null, newStruct, newStruct, false);
         }
 
         return newStruct;
     }
 
     /** Create new Structure.
      *
      * MRI: struct_alloc
      *
      */
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         int size = RubyNumeric.fix2int(getInstanceVariable((RubyClass) recv, "__size__"));
 
         struct.values = new IRubyObject[size];
 
         struct.callInit(args, block);
 
         return struct;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         modify();
 
         int size = RubyNumeric.fix2int(getInstanceVariable(getMetaClass(), "__size__"));
 
         if (args.length > size) {
             throw getRuntime().newArgumentError("struct size differs (" + args.length +" for " + size + ")");
         }
 
         for (int i = 0; i < args.length; i++) {
             values[i] = args[i];
         }
 
         for (int i = args.length; i < size; i++) {
             values[i] = getRuntime().getNil();
         }
 
         return getRuntime().getNil();
     }
     
     public static RubyArray members(IRubyObject recv, Block block) {
         RubyArray member = (RubyArray) getInstanceVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0,k=member.getLength(); i < k; i++) {
             result.append(recv.getRuntime().newString(member.eltInternal(i).asSymbol()));
         }
 
         return result;
     }
 
     public RubyArray members() {
         return members(classOf(), Block.NULL_BLOCK);
     }
 
     public IRubyObject set(IRubyObject value, Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
         if (name.endsWith("=")) {
             name = name.substring(0, name.length() - 1);
         }
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private RaiseException notStructMemberError(String name) {
         return getRuntime().newNameError(name + " is not struct member", name);
     }
 
     public IRubyObject get(Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     public IRubyObject rbClone(Block block) {
         RubyStruct clone = new RubyStruct(getRuntime(), getMetaClass());
 
         clone.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, clone.values, 0, values.length);
 
         clone.setFrozen(this.isFrozen());
         clone.setTaint(this.isTaint());
 
         return clone;
     }
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
         if (getMetaClass().getRealClass() != other.getMetaClass().getRealClass()) return getRuntime().getFalse();
         
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyStruct otherStruct = (RubyStruct)other;
             for (int i = 0; i < values.length; i++) {
             if (!values[i].equalInternal(context, otherStruct.values[i]).isTrue()) {
                 return runtime.getFalse();
                 }
             }
         return runtime.getTrue();
         }
     
     public IRubyObject eql_p(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
         if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
         
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyStruct otherStruct = (RubyStruct)other;
         for (int i = 0; i < values.length; i++) {
             if (!values[i].eqlInternal(context, otherStruct.values[i])) {
                 return runtime.getFalse();
     }
         }
         return runtime.getTrue();        
     }
 
     public IRubyObject to_s() {
         return inspect();
     }
 
     public IRubyObject inspect() {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         StringBuffer sb = new StringBuffer(100);
 
         sb.append("#<struct ").append(getMetaClass().getRealClass().getName()).append(' ');
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (i > 0) {
                 sb.append(", ");
             }
 
             sb.append(member.eltInternal(i).asSymbol()).append("=");
             sb.append(values[i].callMethod(getRuntime().getCurrentContext(), "inspect"));
         }
 
         sb.append('>');
 
         return getRuntime().newString(sb.toString()); // OBJ_INFECT
     }
 
     public RubyArray to_a() {
         return getRuntime().newArray(values);
     }
 
     public RubyFixnum size() {
         return getRuntime().newFixnum(values.length);
     }
 
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, values[i]);
         }
 
         return this;
     }
 
     public IRubyObject each_pair(Block block) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, getRuntime().newArrayNoCopy(new IRubyObject[]{member.eltInternal(i), values[i]}));
         }
 
         return this;
     }
 
     public IRubyObject aref(IRubyObject key) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return getByName(key.asSymbol());
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         return values[idx];
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return setByName(key.asSymbol(), value);
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         modify();
         return values[idx] = value;
     }
     
     // FIXME: This is copied code from RubyArray.  Both RE, Struct, and Array should share one impl
     // This is also hacky since I construct ruby objects to access ruby arrays through aref instead
     // of something lower.
     public IRubyObject values_at(IRubyObject[] args) {
         long olen = values.length;
         RubyArray result = getRuntime().newArray(args.length);
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyFixnum) {
                 result.append(aref(args[i]));
                 continue;
             }
 
             long beglen[];
             if (!(args[i] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[i]).begLen(olen, 0)) == null) {
                 continue;
             } else {
                 int beg = (int) beglen[0];
                 int len = (int) beglen[1];
                 int end = len;
                 for (int j = 0; j < end; j++) {
                     result.append(aref(getRuntime().newFixnum(j + beg)));
                 }
                 continue;
             }
             result.append(aref(getRuntime().newFixnum(RubyNumeric.num2long(args[i]))));
         }
 
         return result;
     }
 
     public static void marshalTo(RubyStruct struct, MarshalStream output) throws java.io.IOException {
         output.dumpDefaultObjectHeader('S', struct.getMetaClass());
 
         List members = ((RubyArray) getInstanceVariable(struct.classOf(), "__member__")).getList();
         output.writeInt(members.size());
 
         for (int i = 0; i < members.size(); i++) {
             RubySymbol name = (RubySymbol) members.get(i);
             output.dumpObject(name);
             output.dumpObject(struct.values[i]);
         }
     }
 
     public static RubyStruct unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         Ruby runtime = input.getRuntime();
 
         RubySymbol className = (RubySymbol) input.unmarshalObject();
         RubyClass rbClass = pathToClass(runtime, className.asSymbol());
         if (rbClass == null) {
             throw runtime.newNameError("uninitialized constant " + className, className.asSymbol());
         }
 
         RubyArray mem = members(rbClass, Block.NULL_BLOCK);
 
         int len = input.unmarshalInt();
         IRubyObject[] values = new IRubyObject[len];
         for(int i = 0; i < len; i++) {
             values[i] = runtime.getNil();
         }
         RubyStruct result = newStruct(rbClass, values, Block.NULL_BLOCK);
         input.registerLinkTarget(result);
         for(int i = 0; i < len; i++) {
             IRubyObject slot = input.unmarshalObject();
             if(!mem.eltInternal(i).toString().equals(slot.toString())) {
                 throw runtime.newTypeError("struct " + rbClass.getName() + " not compatible (:" + slot + " for :" + mem.eltInternal(i) + ")");
             }
             result.aset(runtime.newFixnum(i), input.unmarshalObject());
         }
         return result;
     }
 
     private static RubyClass pathToClass(Ruby runtime, String path) {
         // FIXME: Throw the right ArgumentError's if the class is missing
         // or if it's a module.
         return (RubyClass) runtime.getClassFromPath(path);
     }
     
     private static ObjectAllocator STRUCT_INSTANCE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyStruct instance = new RubyStruct(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     public IRubyObject initialize_copy(IRubyObject arg) {
         if (this == arg) return this;
         RubyStruct original = (RubyStruct) arg;
         
         values = new IRubyObject[original.values.length];
         System.arraycopy(original.values, 0, values, 0, original.values.length);
 
         return this;
     }
     
 }
diff --git a/src/org/jruby/TopSelfFactory.java b/src/org/jruby/TopSelfFactory.java
index 9bcc134797..08bf20d846 100644
--- a/src/org/jruby/TopSelfFactory.java
+++ b/src/org/jruby/TopSelfFactory.java
@@ -1,121 +1,123 @@
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
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  * 
  * @author jpetersen
  */
 public final class TopSelfFactory {
 
     /**
      * Constructor for TopSelfFactory.
      */
     private TopSelfFactory() {
         super();
     }
     
     public static IRubyObject createTopSelf(final Ruby runtime) {
         IRubyObject topSelf = new RubyObject(runtime, runtime.getObject());
         
         topSelf.getSingletonClass().defineMethod("to_s", new Callback() {
             /**
              * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
              */
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 return runtime.newString("main");
             }
 
             /**
              * @see org.jruby.runtime.callback.Callback#getArity()
              */
             public Arity getArity() {
                 return Arity.noArguments();
             }
         });
         
-        topSelf.getSingletonClass().defineMethod("include", new Callback() {
+        // The following three methods must be defined fast, since they expect to modify the current frame
+        // (i.e. they expect no frame will be allocated for them). JRUBY-1185.
+        topSelf.getSingletonClass().defineFastPrivateMethod("include", new Callback() {
             /**
              * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
              */
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 runtime.secure(4);
                 return runtime.getObject().include(args);
             }
 
             /**
              * @see org.jruby.runtime.callback.Callback#getArity()
              */
             public Arity getArity() {
                 return Arity.optional();
             }
         });
         
-        topSelf.getSingletonClass().defineMethod("public", new Callback() {
+        topSelf.getSingletonClass().defineFastPrivateMethod("public", new Callback() {
             /**
              * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
              */
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                 return runtime.getObject().rbPublic(args);
             }
 
             /**
              * @see org.jruby.runtime.callback.Callback#getArity()
              */
             public Arity getArity() {
                 return Arity.optional();
             }
         });
         
-        topSelf.getSingletonClass().defineMethod("private", new Callback() {
+        topSelf.getSingletonClass().defineFastPrivateMethod("private", new Callback() {
             /**
              * @see org.jruby.runtime.callback.Callback#execute(IRubyObject, IRubyObject[])
              */
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                 return runtime.getObject().rbPrivate(args);
             }
 
             /**
              * @see org.jruby.runtime.callback.Callback#getArity()
              */
             public Arity getArity() {
                 return Arity.optional();
             }
         });
         
         return topSelf;
     }
 }
diff --git a/src/org/jruby/ast/executable/YARVMachine.java b/src/org/jruby/ast/executable/YARVMachine.java
index 41f557ba19..c9f08e8b2d 100644
--- a/src/org/jruby/ast/executable/YARVMachine.java
+++ b/src/org/jruby/ast/executable/YARVMachine.java
@@ -1,703 +1,703 @@
 package org.jruby.ast.executable;
 
 import org.jruby.Ruby;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.MetaClass;
 import org.jruby.parser.StaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.internal.runtime.methods.YARVMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.MethodIndex;
 
 public class YARVMachine {
     private static final boolean TAILCALL_OPT = Boolean.getBoolean("jruby.tailcall.enabled");
 
     public static final YARVMachine INSTANCE = new YARVMachine();
 
     public static int instruction(String name) {
         return YARVInstructions.instruction(name);
     }
 
     public static class InstructionSequence {
         public String magic;
         public int major;
         public int minor;
         public int format_type;
         public Object misc;
         public String name;
         public String filename;
         public Object[] line;
         public String type;
 
         public String[] locals;
 
         public int args_argc;
         public int args_arg_opts;
         public String[] args_opt_labels;
         public int args_rest;
         public int args_block;
 
         public Object[] exception;
 
         public Instruction[] body;
 
         public InstructionSequence(Ruby runtime, String name, String file, String type) {
             magic = "YARVInstructionSimpledataFormat";
             major = 1;
             minor = 1;
             format_type = 1;
             misc = runtime.getNil();
             this.name = name;
             this.filename = file;
             this.line = new Object[0];
             this.type = type;
             this.locals = new String[0];
             this.args_argc = 0;
             this.args_arg_opts = 0;
             this.exception = new Object[0];
         }
     }
 
     public static class Instruction {
         final public int bytecode;
         public int line_no;
         public String s_op0;
         public IRubyObject o_op0;
         public Object _tmp;
         public long l_op0;
         public long l_op1;
         public int i_op1;
         public InstructionSequence iseq_op;
         public Instruction[] ins_op;
         public int i_op3;
 
         public int index;
 
         public Instruction(int bytecode) {
             this.bytecode = bytecode;
         }
 
         public Instruction(int bytecode, String op) {
             this.bytecode = bytecode;
             this.s_op0 = op;
         }
 
         public Instruction(int bytecode, String op, InstructionSequence op1) {
             this.bytecode = bytecode;
             this.s_op0 = op;
             this.iseq_op = op1;
         }
 
         public Instruction(int bytecode, long op) {
             this.bytecode = bytecode;
             this.l_op0 = op;
         }
 
         public Instruction(int bytecode, IRubyObject op) {
             this.bytecode = bytecode;
             this.o_op0 = op;
         }
 
         public Instruction(int bytecode, String op, int op1, Instruction[] op2, int op3) {
             this.bytecode = bytecode;
             this.s_op0 = op;
             this.i_op1 = op1;
             this.ins_op = op2;
             this.i_op3 = op3;
         }
 
         public String toString() {
             return "[:" + YARVInstructions.name(bytecode) + ", " + (s_op0 != null ? s_op0 : (o_op0 != null ? o_op0.toString() : ("" + l_op0))) + "]";
         }
     }
     
     public IRubyObject exec(ThreadContext context, IRubyObject self, StaticScope scope, Instruction[] bytecodes) {
         return exec(context,self, new DynamicScope(scope),bytecodes);
     }
 
     public IRubyObject exec(ThreadContext context, IRubyObject self, DynamicScope scope, Instruction[] bytecodes) {
         IRubyObject[] stack = new IRubyObject[255];
         int stackTop = 0;
         stack[stackTop] = context.getRuntime().getNil();
         int ip = 0;
         Ruby runtime = context.getRuntime();
         context.preRootNode(scope);
         IRubyObject recv;
         IRubyObject other;
 
         yarvloop: while (ip < bytecodes.length) {
             //System.err.println("Executing: " + YARVInstructions.name(bytecodes[ip].bytecode));
             switch (bytecodes[ip].bytecode) {
             case YARVInstructions.NOP:
                 break;
             case YARVInstructions.GETLOCAL:
                 stack[++stackTop] = context.getCurrentScope().getValue((int)bytecodes[ip].l_op0,0);
                 break;
             case YARVInstructions.SETLOCAL:
                 context.getCurrentScope().setValue((int)bytecodes[ip].l_op0, stack[stackTop--],0);
                 break;
             case YARVInstructions.GETSPECIAL:
                 System.err.println("Not implemented, YARVMachine." + YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.SETSPECIAL:
                 System.err.println("Not implemented, YARVMachine." + YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.GETDYNAMIC:
                 System.err.println("Not implemented, YARVMachine." + YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.SETDYNAMIC:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.GETINSTANCEVARIABLE:
                 stack[++stackTop] = self.getInstanceVariable(bytecodes[ip].s_op0);
                 break;
             case YARVInstructions.SETINSTANCEVARIABLE:
                 self.setInstanceVariable(bytecodes[ip].s_op0, stack[stackTop--]);
                 break;
             case YARVInstructions.GETCLASSVARIABLE: {
                 RubyModule rubyClass = context.getRubyClass();
                 String name = bytecodes[ip].s_op0;
     
                 if (rubyClass == null) {
                     stack[++stackTop] = self.getMetaClass().getClassVar(name);
                 } else if (!rubyClass.isSingleton()) {
                     stack[++stackTop] = rubyClass.getClassVar(name);
                 } else {
                     RubyModule module = (RubyModule) rubyClass.getInstanceVariable("__attached__");
     
                     if (module != null) {
                         stack[++stackTop] = module.getClassVar(name);
                     } else {
                         stack[++stackTop] = context.getRuntime().getNil();
                     }
                 }
                 break;
             }
             case YARVInstructions.SETCLASSVARIABLE: {
                 RubyModule rubyClass = (RubyModule) context.peekCRef().getValue();
     
                 if (rubyClass == null) {
                     rubyClass = self.getMetaClass();
                 } else if (rubyClass.isSingleton()) {
                     rubyClass = (RubyModule) rubyClass.getInstanceVariable("__attached__");
                 }
     
                 rubyClass.setClassVar(bytecodes[ip].s_op0, stack[stackTop--]);
                 break;
             }
             case YARVInstructions.GETCONSTANT:
                 IRubyObject cls = stack[stackTop--];
                 stack[++stackTop] = context.getConstant(bytecodes[ip].s_op0);
                 break;
             case YARVInstructions.SETCONSTANT:
                 RubyModule module = (RubyModule) context.peekCRef().getValue();
                 module.setConstant(bytecodes[ip].s_op0,stack[stackTop--]);
                 runtime.incGlobalState();
                 break;
             case YARVInstructions.GETGLOBAL:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.SETGLOBAL:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.PUTNIL:
                 stack[++stackTop] = context.getRuntime().getNil();
                 break;
             case YARVInstructions.PUTSELF:
                 stack[++stackTop] = self;
                 break;
             case YARVInstructions.PUTUNDEF:
                 // ko1 said this is going away
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.PUTOBJECT:
                 stack[++stackTop] = bytecodes[ip].o_op0;
                 break;
             case YARVInstructions.PUTSTRING:
                 stack[++stackTop] = context.getRuntime().newString(bytecodes[ip].s_op0);
                 break;
             case YARVInstructions.CONCATSTRINGS: {
                 StringBuffer concatter = new StringBuffer();
                 for (int i = 0; i < bytecodes[ip].l_op0; i++) {
                     concatter.append(stack[stackTop--].toString());
                 }
                 stack[++stackTop] = context.getRuntime().newString(concatter.toString());
                 break;
             }
             case YARVInstructions.TOSTRING:
                 if(!(stack[stackTop] instanceof RubyString)) {
                     stack[stackTop] = (stack[stackTop]).callMethod(context, MethodIndex.TO_S, "to_s");
                 }
                 break;
             case YARVInstructions.TOREGEXP:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.NEWARRAY: {
                 int size = (int)bytecodes[ip].l_op0;
                 IRubyObject[] arr = new IRubyObject[size];
                 for(int i = size - 1; i >= 0; i--) {
                     arr[i] =  stack[stackTop--];
                 }
                 stack[++stackTop] = context.getRuntime().newArrayNoCopy(arr);
                 break;
             }
             case YARVInstructions.DUPARRAY:
                 stack[++stackTop] = bytecodes[ip].o_op0.dup();
                 break;
             case YARVInstructions.EXPANDARRAY:
                 // masgn array to values
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.CONCATARRAY:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.SPLATARRAY:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.CHECKINCLUDEARRAY:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.NEWHASH:
                 int hsize = (int)bytecodes[ip].l_op0;
                 RubyHash h = RubyHash.newHash(runtime);
                 IRubyObject v,k;
                 for(int i = hsize; i>0; i -= 2) {
                     v = stack[stackTop--];
                     k = stack[stackTop--];
                     h.aset(k,v);
                 }
                 stack[++stackTop] = h;
                 break;
             case YARVInstructions.NEWRANGE:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.PUTNOT:
                 stack[stackTop+1] = stack[stackTop].isTrue() ? runtime.getFalse() : runtime.getTrue();
                 stackTop++;
                 break;
             case YARVInstructions.POP:
                 stackTop--;
                 break;
             case YARVInstructions.DUP:
                 stack[stackTop + 1] = stack[stackTop];
                 stackTop++;
                 break;
             case YARVInstructions.DUPN: {
                 int size = (int)bytecodes[ip].l_op0;
                 for (int i = 0; i < size; i++) {
                     stack[stackTop + 1] = stack[stackTop - size];
                     stackTop++;
                 }
                 break;
             }
             case YARVInstructions.SWAP:
                 stack[stackTop + 1] = stack[stackTop];
                 stack[stackTop] = stack[stackTop - 1];
                 stack[stackTop - 1] = stack[stackTop + 1];
             case YARVInstructions.REPUT:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.TOPN: {
                 int n = (int)bytecodes[ip].l_op0;
                 other = stack[stackTop - n];
                 stack[++stackTop] = other;
                 break;
             }
             case YARVInstructions.SETN: {
                 int n = (int)bytecodes[ip].l_op0;
                 stack[stackTop - n] = stack[stackTop];
                 break;
             }
             case YARVInstructions.EMPTSTACK:
                 stackTop = 0;
                 break;
             case YARVInstructions.DEFINEMETHOD: 
                 RubyModule containingClass = context.getRubyClass();
     
                 if (containingClass == null) {
                     throw runtime.newTypeError("No class to add method.");
                 }
 
                 String mname = bytecodes[ip].iseq_op.name;
                 if (containingClass == runtime.getObject() && mname == "initialize") {
                     runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
                 }
     
                 Visibility visibility = context.getCurrentVisibility();
-                if (mname == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
+                if (mname == "initialize" || visibility.isModuleFunction()) {
                     visibility = Visibility.PRIVATE;
                 }
                 
                 if (containingClass.isSingleton()) {
                     IRubyObject attachedObject = ((MetaClass) containingClass).getAttachedObject();
                     
                     if (attachedObject.getMetaClass() == runtime.getFixnum() || attachedObject.getMetaClass() == runtime.getClass("Symbol")) {
                         throw runtime.newTypeError("can't define singleton method \"" + 
                                 mname + "\" for " + attachedObject.getType());
                     }
                 }
 
                 StaticScope sco = new LocalStaticScope(null);
                 sco.setVariables(bytecodes[ip].iseq_op.locals);
                 YARVMethod newMethod = new YARVMethod(containingClass, bytecodes[ip].iseq_op, sco, visibility, context.peekCRef());
 
                 containingClass.addMethod(mname, newMethod);
     
                 if (context.getCurrentVisibility().isModuleFunction()) {
                     containingClass.getSingletonClass().addMethod(
                             mname,
                             new WrapperMethod(containingClass.getSingletonClass(), newMethod,
                                     Visibility.PUBLIC));
                     containingClass.callMethod(context, "singleton_method_added", runtime.newSymbol(mname));
                 }
     
                 // 'class << state.self' and 'class << obj' uses defn as opposed to defs
                 if (containingClass.isSingleton()) {
                     ((MetaClass) containingClass).getAttachedObject().callMethod(
                             context, "singleton_method_added", runtime.newSymbol(mname));
                 } else {
                     containingClass.callMethod(context, "method_added", runtime.newSymbol(mname));
                 }
                 stack[++stackTop] = runtime.getNil();
                 runtime.incGlobalState();
                 break;
             case YARVInstructions.ALIAS: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.UNDEF: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 runtime.incGlobalState();
                 break;
             case YARVInstructions.DEFINED: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.POSTEXE: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.TRACE: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.DEFINECLASS: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.SEND: {
                 String name = bytecodes[ip].s_op0;
                 IRubyObject[] args = new IRubyObject[bytecodes[ip].i_op1];
 
                 Instruction[] blockBytecodes = bytecodes[ip].ins_op;
                 // TODO: block stuff
                 int flags = bytecodes[ip].i_op3;
                 CallType callType;
 
                 for (int i = args.length; i > 0; i--) {
                     args[i-1] = stack[stackTop--];
                 }
 
                 if ((flags & YARVInstructions.VCALL_FLAG) == 0) {
                     if ((flags & YARVInstructions.FCALL_FLAG) == 0) {
                         recv = stack[stackTop--];
                         callType = CallType.NORMAL;
                     } else {
                         stackTop--;
                         recv = self;
                         callType = CallType.FUNCTIONAL;
                     }
                 } else {
                     recv = self;
                     callType = CallType.VARIABLE;
                 }
                 assert recv.getMetaClass() != null : recv.getClass().getName();
 
                 if(TAILCALL_OPT && (bytecodes[ip+1].bytecode == YARVInstructions.LEAVE || 
                                     (flags & YARVInstructions.TAILCALL_FLAG) == YARVInstructions.TAILCALL_FLAG) &&
                    recv == self && name.equals(context.getFrameName())) {
                     stackTop = 0;
                     ip = -1;
                     
                     for(int i=0;i<args.length;i++) {
                         context.getCurrentScope().getValues()[i] = args[i];
                     }
                 } else {
                     stack[++stackTop] = recv.callMethod(context, name, args, callType);
                 }
                 break;
             }
             case YARVInstructions.INVOKESUPER: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.INVOKEBLOCK: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.LEAVE:
                 break yarvloop;
             case YARVInstructions.FINISH: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.THROW: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.JUMP:
                 ip = (int)bytecodes[ip].l_op0;
                 continue yarvloop;
             case YARVInstructions.BRANCHIF:
                 if (stack[stackTop--].isTrue()) {
                     ip = (int)bytecodes[ip].l_op0;
                 } else {
                     ip++;
                 }
                 continue yarvloop;
             case YARVInstructions.BRANCHUNLESS:
                 if (!stack[stackTop--].isTrue()) {
                     ip = (int)bytecodes[ip].l_op0;
                 } else {
                     ip++;
                 }
                 continue yarvloop;
             case YARVInstructions.GETINLINECACHE: 
                 if(bytecodes[ip].l_op1 == runtime.getGlobalState()) {
                     stack[++stackTop] = bytecodes[ip].o_op0;
                     ip = (int)bytecodes[ip].l_op0;
                     continue yarvloop;
                 }
                 break;
             case YARVInstructions.ONCEINLINECACHE: 
                 if(bytecodes[ip].l_op1 > 0) {
                     stack[++stackTop] = bytecodes[ip].o_op0;
                     ip = (int)bytecodes[ip].l_op0;
                     continue yarvloop;
                 }
                 break;
             case YARVInstructions.SETINLINECACHE: 
                 int we = (int)bytecodes[ip].l_op0;
                 bytecodes[we].o_op0 = stack[stackTop];
                 bytecodes[we].l_op1 = runtime.getGlobalState();
                 break;
             case YARVInstructions.OPT_CASE_DISPATCH:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.OPT_CHECKENV:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
                 break;
             case YARVInstructions.OPT_PLUS:
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_PLUS, "+",other);
                 break;
             case YARVInstructions.OPT_MINUS: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_MINUS, "-",other);
                 break;
             case YARVInstructions.OPT_MULT: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_TIMES, "*",other);
                 break;
             case YARVInstructions.OPT_DIV: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,"/",other);
                 break;
             case YARVInstructions.OPT_MOD: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,"%",other);
                 break;
             case YARVInstructions.OPT_EQ:
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.EQUALEQUAL, "==",other);
                 break;
             case YARVInstructions.OPT_LT:
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_LT, "<",other);
                 break;
             case YARVInstructions.OPT_LE: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_LE, "<=",other);
                 break;
             case YARVInstructions.OPT_LTLT: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.OP_LSHIFT, "<<",other);
                 break;
             case YARVInstructions.OPT_AREF: 
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.AREF, "[]",other);
                 break;
             case YARVInstructions.OPT_ASET: 
                 //YARV will never emit this, for some reason.
                 IRubyObject value = stack[stackTop--];
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,MethodIndex.ASET, "[]=",new IRubyObject[]{other,value});
                 break;
             case YARVInstructions.OPT_LENGTH: 
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,"length");
                 break;
             case YARVInstructions.OPT_SUCC: 
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,"succ");
                 break;
             case YARVInstructions.OPT_REGEXPMATCH1: 
                 stack[stackTop] = bytecodes[ip].o_op0.callMethod(context,"=~",stack[stackTop]);
                 break;
             case YARVInstructions.OPT_REGEXPMATCH2:
                 other = stack[stackTop--];
                 recv = stack[stackTop--];
                 stack[++stackTop] = recv.callMethod(context,"=~",other);
                 break;
             case YARVInstructions.OPT_CALL_NATIVE_COMPILED: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.BITBLT: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.ANSWER: 
                 stack[++stackTop] = runtime.newFixnum(42);
                 break;
             case YARVInstructions.GETLOCAL_OP_2: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETLOCAL_OP_3: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETLOCAL_OP_4: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETLOCAL_OP_2:
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETLOCAL_OP_3: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETLOCAL_OP_4: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETDYNAMIC_OP__WC__0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETDYNAMIC_OP_1_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETDYNAMIC_OP_2_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETDYNAMIC_OP_3_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.GETDYNAMIC_OP_4_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETDYNAMIC_OP__WC__0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETDYNAMIC_OP_1_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETDYNAMIC_OP_2_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETDYNAMIC_OP_3_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SETDYNAMIC_OP_4_0: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.PUTOBJECT_OP_INT2FIX_0_0_C_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.PUTOBJECT_OP_INT2FIX_0_1_C_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.PUTOBJECT_OP_QTRUE: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.PUTOBJECT_OP_QFALSE: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC___WC__QFALSE_0__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__0_QFALSE_0__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__1_QFALSE_0__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__2_QFALSE_0__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__3_QFALSE_0__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC___WC__QFALSE_0X04__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__0_QFALSE_0X04__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__1_QFALSE_0X04__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__2_QFALSE_0X04__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__3_QFALSE_0X04__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.SEND_OP__WC__0_QFALSE_0X0C__WC_: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTOBJECT_PUTOBJECT: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTOBJECT_PUTSTRING: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTOBJECT_SETLOCAL: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTOBJECT_SETDYNAMIC: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTSTRING_PUTSTRING: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTSTRING_PUTOBJECT: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTSTRING_SETLOCAL: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_PUTSTRING_SETDYNAMIC: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_DUP_SETLOCAL: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_GETLOCAL_GETLOCAL: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             case YARVInstructions.UNIFIED_GETLOCAL_PUTOBJECT: 
                 System.err.println("Not implemented, YARVMachine." +YARVInstructions.name(bytecodes[ip].bytecode));
 break;
             }
             ip++;
         }
 
         context.postRootNode();
         return stack[stackTop];
     }
 }
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index 105e1b6fd9..d784dc107e 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,1807 +1,1807 @@
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
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
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
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
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
                     callTraceFunction(runtime, context, EventHook.RUBY_EVENT_LINE);
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
    
         RubyModule module = receiver.getMetaClass();
         
         String name = iVisited.getName();
 
         DynamicMethod method = module.searchMethod(name);
 
         if (method.isUndefined() || (!method.isCallableFrom(self, callType))) {
             return RubyObject.callMethodMissing(context, receiver, method, name, args, self, callType, Block.NULL_BLOCK);
         }
 
         method.call(context, receiver, module, name, args, false, Block.NULL_BLOCK);
 
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
         String name = iVisited.getName();
         int index = iVisited.index;
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             if (index != 0) {
                 return receiver.callMethod(context, module, index, name, args, CallType.NORMAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(name);
       
                 if (method.isUndefined() || (!method.isCallableFrom(self, CallType.NORMAL))) {
                     return RubyObject.callMethodMissing(context, receiver, method, name, args, self, CallType.NORMAL, Block.NULL_BLOCK);
                 }
 
                 return method.call(context, receiver, module, name, args, false, Block.NULL_BLOCK);
             }
         }
             
         while (true) {
             try {
                 DynamicMethod method = module.searchMethod(name);
 
                 if (method.isUndefined() || (index != MethodIndex.METHOD_MISSING && !method.isCallableFrom(self, CallType.NORMAL))) {
                     return RubyObject.callMethodMissing(context, receiver, method, name, index, args, self, CallType.NORMAL, block);
                 }
 
                 return method.call(context, receiver, module, name, args, false, block);
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
                         callTraceFunction(runtime, context, EventHook.RUBY_EVENT_LINE);
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
-        if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
+        if (name == "initialize" || visibility.isModuleFunction()) {
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
    
         int opts = iVisited.getOptions();
         String lang = ((opts & 16) != 0) ? "n" : null;
         if((opts & 48) == 48) { // param s
             lang = "s";
         } else if((opts & 32) == 32) { // param e
             lang = "e";
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
         
         String name = iVisited.getName();
         int index = iVisited.index;
 
         // No block provided lets look at fast path for STI dispatch.
         if (!block.isGiven()) {
             RubyModule module = self.getMetaClass();
             if (module.index != 0 && index != 0) {
                 return self.callMethod(context, module, iVisited.index, name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } else {
                 DynamicMethod method = module.searchMethod(name);
                 if (method.isUndefined() || (!method.isCallableFrom(self, CallType.FUNCTIONAL))) {
                     return RubyObject.callMethodMissing(context, self, method, name, args, self, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 }
 
                 return method.call(context, self, module, name, args, false, Block.NULL_BLOCK);
             }
         }
 
         while (true) {
             try {
                 RubyModule module = self.getMetaClass();
                 IRubyObject result = self.callMethod(context, module, name, args,
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
         IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
    
         if (iVisited.isExclusive()) {
             if (result == null || !result.isTrue()) {
                 result = evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getTrue() : runtime.getFalse();
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                 return result;
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                 }
                 
                 return runtime.getTrue();
             }
         } else {
             if (result == null || !result.isTrue()) {
                 if (evalInternal(runtime, context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                     context.getCurrentScope().setValue(iVisited.getIndex(),
                             evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue() ? 
                                     runtime.getFalse() : runtime.getTrue(), iVisited.getDepth());
                     return runtime.getTrue();
                 } 
 
                 return runtime.getFalse();
             } else {
                 if (evalInternal(runtime, context, iVisited.getEndNode(), self, aBlock).isTrue()) {
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
 
         //        System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
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
         int opts = iVisited.getOptions();
         String lang = ((opts & 16) == 16) ? "n" : null;
         if((opts & 48) == 48) { // param s
             lang = "s";
         } else if((opts & 32) == 32) { // param e
             lang = "e";
         } else if((opts & 64) != 0) { // param u
             lang = "u";
         }
         
         IRubyObject noCaseGlobal = runtime.getGlobalVariables().get("$=");
         
         int extraOptions = noCaseGlobal.isTrue() ? ReOptions.RE_OPTION_IGNORECASE : 0;
 
         try {
             return RubyRegexp.newRegexp(runtime, iVisited.getValue(), 
                     iVisited.getPattern(extraOptions), iVisited.getFlags(extraOptions), lang);
         } catch(jregex.PatternSyntaxException e) {
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
             scope = new DynamicScope(iVisited.getStaticScope());
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
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || !(evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
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
 
         if (result == null) {
             result = runtime.getNil();
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
         String name = iVisited.getName();
         int index = iVisited.index;
 
         if (module.index != 0 && index != 0) {
             return self.callMethod(context, module, index, name, 
                     IRubyObject.NULL_ARRAY, CallType.VARIABLE, Block.NULL_BLOCK);
         } else {
             DynamicMethod method = module.searchMethod(name);
             
             if (method.isUndefined() || (index != MethodIndex.METHOD_MISSING  && !method.isCallableFrom(self, CallType.VARIABLE))) {
                 return RubyObject.callMethodMissing(context, self, method, name, index, IRubyObject.NULL_ARRAY, self, CallType.VARIABLE, Block.NULL_BLOCK);
             }
 
             return method.call(context, self, module, name, IRubyObject.NULL_ARRAY, false, Block.NULL_BLOCK);
         }
     }
 
     private static IRubyObject whileNode(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         WhileNode iVisited = (WhileNode) node;
    
         IRubyObject result = null;
         boolean firstTest = iVisited.evaluateAtStart();
         
         outerLoop: while (!firstTest || evalInternal(runtime,context, iVisited.getConditionNode(), self, aBlock).isTrue()) {
             firstTest = true;
             loop: while (true) { // Used for the 'redo' command
                 try {
                     evalInternal(runtime,context, iVisited.getBodyNode(), self, aBlock);
                     break loop;
                 } catch (RaiseException re) {
                     if (re.getException().isKindOf(runtime.getClass("LocalJumpError"))) {
                         RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
                         
                         IRubyObject reason = jumpError.reason();
                         
                         // admittedly inefficient
                         if (reason.asSymbol().equals("break")) {
                             return jumpError.exitValue();
                         } else if (reason.asSymbol().equals("next")) {
                             break loop;
                         } else if (reason.asSymbol().equals("redo")) {
                             continue;
                         }
                     }
                     
                     throw re;
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
diff --git a/src/org/jruby/javasupport/util/CompilerHelpers.java b/src/org/jruby/javasupport/util/CompilerHelpers.java
index 11e8c6caaf..48ae9f678d 100644
--- a/src/org/jruby/javasupport/util/CompilerHelpers.java
+++ b/src/org/jruby/javasupport/util/CompilerHelpers.java
@@ -1,268 +1,266 @@
 package org.jruby.javasupport.util;
 
 import jregex.Pattern;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class CompilerHelpers {
     private final static org.jruby.RegexpTranslator TRANS = new org.jruby.RegexpTranslator();
 
     public static CompiledBlock createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         
         return new CompiledBlock(context, self, Arity.createArity(arity), 
                 new DynamicScope(staticScope, context.getCurrentScope()), callback);
     }
     
     public static IRubyObject def(ThreadContext context, Visibility visibility, IRubyObject self, Class compiledClass, String name, String javaName, String[] scopeNames, int arity) {
         Ruby runtime = context.getRuntime();
         
-        // FIXME: This is what the old def did, but doesn't work in the compiler for top-level methods. Hmm.
         RubyModule containingClass = context.getRubyClass();
-        //RubyModule containingClass = self.getMetaClass();
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
         if (containingClass == runtime.getObject() && name == "initialize") {
             runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
         }
         
         SinglyLinkedList cref = context.peekCRef();
         StaticScope scope = new LocalStaticScope(null, scopeNames);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
-        if (name == "initialize" || visibility.isModuleFunction() || context.isTopLevel()) {
+        if (name == "initialize" || visibility.isModuleFunction()) {
             method = factory.getCompiledMethod(containingClass, compiledClass, javaName, 
                     Arity.createArity(arity), Visibility.PRIVATE, cref, scope);
         } else {
             method = factory.getCompiledMethod(containingClass, compiledClass, javaName, 
                     Arity.createArity(arity), visibility, cref, scope);
         }
         
         containingClass.addMethod(name, method);
         
         if (visibility.isModuleFunction()) {
             containingClass.getSingletonClass().addMethod(name,
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
     
     public static IRubyObject doAttrAssign(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return receiver.compilerCallMethod(context, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         try {
             return receiver.compilerCallMethodWithIndex(context, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         try {
             return receiver.compilerCallMethod(context, name, args, caller, callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         try {
             return receiver.compilerCallMethodWithIndex(context, methodIndex, name, args, caller, 
                     callType, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(value.getRuntime(), value);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = EvaluationState.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
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
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         if (rubyClass != null) {
             if (!(rubyClass instanceof RubyClass)) {
                 throw runtime.newTypeError("superclass must be a Class (" + 
                         RubyObject.trueFalseNil(rubyClass) + ") given");
             }
             return (RubyClass)rubyClass;
         }
         return (RubyClass)null;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = (RubyModule) context.peekCRef().getValue();
             
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
         
         return (RubyModule)rubyModule;
     }
     
     public static int regexpLiteralFlags(int options) {
         return TRANS.flagsFor(options,0);
     }
 
     public static Pattern regexpLiteral(Ruby runtime, String ptr, int options) {
         IRubyObject noCaseGlobal = runtime.getGlobalVariables().get("$=");
 
         int extraOptions = noCaseGlobal.isTrue() ? ReOptions.RE_OPTION_IGNORECASE : 0;
 
         try {
             return TRANS.translate(ptr, options | extraOptions, 0);
         } catch(jregex.PatternSyntaxException e) {
             throw runtime.newRegexpError(e.getMessage());
         }
     }
 
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = EvaluationState.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static void raiseArgumentError(Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     public static void processBlockArgument(Ruby runtime, ThreadContext context, Block block, int index) {
         if (!block.isGiven()) return;
         
         RubyProc blockArg;
         
         if (block.getProcObject() != null) {
             blockArg = (RubyProc) block.getProcObject();
         } else {
             blockArg = runtime.newProc(false, block);
             blockArg.getBlock().isLambda = block.isLambda;
         }
         // We pass depth zero since we know this only applies to newly created local scope
         context.getCurrentScope().setValue(index, blockArg, 0);
     }
 }
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index 3d20326451..11647ef8bf 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,383 +1,385 @@
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
         
         public IRubyObject yield(ThreadContext context, IRubyObject[] args, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
             // FIXME: with args as IRubyObject[], no obvious second param here...
             throw context.getRuntime().newLocalJumpError("noreason", self, "yield called out of block");
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
         return new Block(null, frame.getSelf(), frame.duplicate(), context.peekCRef(), frame.getVisibility(), 
                 context.getBindingRubyClass(), extraScope);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args) {
         return yield(context, args, null, null, true);
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         context.preYieldSpecificBlock(this, klass);
     }
     
     protected void post(ThreadContext context) {
         context.postYield();
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value) {
         return yield(context, new IRubyObject[] {value}, null, null, false);
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
     public IRubyObject yield(ThreadContext context, IRubyObject[] args, IRubyObject self, 
             RubyModule klass, boolean useArrayWithoutConversion) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
         
+        Visibility oldVis = frame.getVisibility();
         pre(context, klass);
 
         try {
             if (iterNode.getVarNode() != null) {
                 if (useArrayWithoutConversion) {
                     setupBlockArgs(context, iterNode.getVarNode(), RubyArray.newArrayNoCopyLight(context.getRuntime(), args), self);
                 } else {
                     setupBlockArg(context, iterNode.getVarNode(), args[0], self);
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
         	if (je.getJumpType() == JumpException.JumpType.NextJump) return isLambda ? context.getRuntime().getNil() : (IRubyObject)je.getValue();
 
             throw je;
         } finally {
+            frame.setVisibility(oldVis);
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
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 2a2dffb4d1..04b6211b61 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,945 +1,943 @@
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author jpetersen
  */
 public class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     
     // Error info is per-thread
     private IRubyObject errorInfo;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     //private UnsynchronizedStack crefStack;
     private SinglyLinkedList[] crefStack = new SinglyLinkedList[INITIAL_SIZE];
     private int crefIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private String[] catchStack = new String[INITIAL_SIZE];
     private int catchIndex = -1;
     
     private ISourcePosition sourcePosition = new ISourcePosition() {
         public void adjustStartOffset(int relativeValue) {}
         public int getEndLine() { return 0; }
         public int getEndOffset() { return 0; }
         public String getFile() { return ""; }
         public int getStartLine() { return 0; }
         public int getStartOffset() { return 0; }
         public ISourcePosition union(ISourcePosition position) { return this; }
     };
     
     public JumpException prepareJumpException(JumpException.JumpType jumpType, Object target, Object value) {
         JumpException controlException = new JumpException();
         
         controlException.setJumpType(jumpType);
         controlException.setTarget(target);
         controlException.setValue(value);
         
         return controlException;
     }
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // init errorInfo to nil
         errorInfo = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         pushScope(new DynamicScope(new LocalStaticScope(null), null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return errorInfo;
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
         return errorInfo;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
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
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary() {
         if (frameIndex + 1 == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
             for (int i = frameStack.length; i < newSize; i++) {
                 newFrameStack[i] = new Frame();
             }
             
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
         IRubyObject value = getCurrentScope().getLastLine();
         
         // DynamicScope does not preinitialize these values since they are virtually never used.
         return value == null ? runtime.getNil() : value;
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
         Frame currentFrame = getCurrentFrame();
         frameStack[++frameIndex].updateFrame(currentFrame);
         expandFramesIfNecessary();
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary();
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, IRubyObject[] args, int req, Block block, Object jumpTarget) {
         pushFrame(clazz, name, self, args, req, block, jumpTarget);        
     }
     
     private void pushFrame(RubyModule clazz, String name, 
                                IRubyObject self, IRubyObject[] args, int req, Block block, Object jumpTarget) {
         frameStack[++frameIndex].updateFrame(clazz, self, name, args, req, block, getPosition(), jumpTarget);
         expandFramesIfNecessary();
     }
     
     private void pushFrame() {
         frameStack[++frameIndex].updateFrame(getPosition());
         expandFramesIfNecessary();
     }
     
     private void popFrame() {
         Frame frame = (Frame)frameStack[frameIndex];
         //frameStack[frameIndex--] = null;
         frameIndex--;
         setPosition(frame.getPosition());
     }
 
     private void popFrameReal() {
         Frame frame = (Frame)frameStack[frameIndex];
         //frameStack[frameIndex--] = null;
         frameStack[frameIndex] = new Frame();
         frameIndex--;
         setPosition(frame.getPosition());
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
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
     
     public Object getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(Object target) {
         getCurrentFrame().setJumpTarget(target);
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public ISourcePosition getFramePosition() {
         return getCurrentFrame().getPosition();
     }
     
     public ISourcePosition getPreviousFramePosition() {
         return getPreviousFrame().getPosition();
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
         
         // DynamicScope does not preinitialize these values since they are virtually never used.
         return value == null ? runtime.getNil() : value;
     }
     
     public void setBackref(IRubyObject backref) {
         if (!(backref instanceof RubyMatchData) && !backref.isNil()) {
             throw runtime.newTypeError(backref, runtime.getClass("MatchData"));
         }
         getCurrentScope().setBackRef(backref);
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
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
     
-    public boolean isTopLevel() {
-        return parentIndex == 0;
-    }
-    
     public boolean getConstantDefined(String name) {
         IRubyObject result = null;
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         for (SinglyLinkedList cbase = peekCRef(); cbase != null; cbase = cbase.getNext()) {
             RubyModule module = (RubyModule) cbase.getValue();
             result = module.getInstanceVariable(name);
             if (result == undef) {
                 module.removeInstanceVariable(name);
                 return runtime.getLoadService().autoload(module.getName() + "::" + name) != null;
             }
             if (result != null) return true;
         }
         
         return false;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String name) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = peekCRef();
         RubyClass object = runtime.getObject();
         IRubyObject result = null;
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getInstanceVariable(name);
             if (result == undef) {
                 klass.removeInstanceVariable(name);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + name) == null) break;
                 continue;
             } else if (result != null) {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null && cbase.getValue() != object);
         
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String name, IRubyObject result) {
         RubyModule module;
 
         // FIXME: why do we check RubyClass and then use CRef?
         if (getRubyClass() == null) {
             // TODO: wire into new exception handling mechanism
             throw runtime.newTypeError("no class/module to define constant");
         }
         module = (RubyModule) peekCRef().getValue();
    
         setConstantInModule(name, module, result);
    
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String name, RubyModule module, IRubyObject result) {
         ((RubyModule) module).setConstant(name, result);
    
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String name, IRubyObject result) {
         setConstantInModule(name, runtime.getObject(), result);
    
         return result;
     }
     
     public IRubyObject getConstant(String name, RubyModule module) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = module.getCRef();
         IRubyObject result = null;
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         redo: do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getInstanceVariable(name);
             if (result == undef) {
                 klass.removeInstanceVariable(name);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + name) == null) break;
                 continue redo;
             } else if (result != null) {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null);
         
         //System.out.println("CREF is " + state.threadContext.getCRef().getValue());
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     private static void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         StringBuffer sb = new StringBuffer(100);
         ISourcePosition position = frame.getPosition();
         
         if(previousFrame != null && frame.getName() != null && previousFrame.getName() != null &&
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getPosition().getFile().equals(previousFrame.getPosition().getFile()) &&
                 frame.getPosition().getEndLine() == previousFrame.getPosition().getEndLine()) {
             return;
         }
         
         sb.append(position.getFile()).append(':').append(position.getEndLine() + 1);
         
         if (previousFrame != null && previousFrame.getName() != null) {
             sb.append(":in `").append(previousFrame.getName()).append('\'');
         } else if (previousFrame == null && frame.getName() != null) {
             sb.append(":in `").append(frame.getName()).append('\'');
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
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames) {
         RubyArray backtrace = runtime.newArray();
         int traceSize = backtraceFrames.length;
         
         if (traceSize <= 0) return backtrace;
 
         for (int i = traceSize - 1; i > 0; i--) {
             Frame frame = backtraceFrames[i];
             // We are in eval with binding break out early
             if (frame != null && frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, backtraceFrames[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         pushCRef(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type) {
         pushCRef(type);
         pushRubyClass(type);
     }
     
     public void postCompiledClass() {
         popCRef();
         popRubyClass();
     }
     
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushCRef(type);
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(new DynamicScope(staticScope, null));
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
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
 
     public void preMethodCall(RubyModule implementationClass, RubyModule clazz, 
                               IRubyObject self, String name, IRubyObject[] args, int req, Block block, boolean noSuper, Object jumpTarget) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         pushCallFrame(noSuper ? null : clazz, name, self, args, req, block, jumpTarget);
     }
     
     public void postMethodCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preDefMethodInternalCall(RubyModule clazz, String name, 
                                          IRubyObject self, IRubyObject[] args, int req, Block block, boolean noSuper, 
             SinglyLinkedList cref, StaticScope staticScope, Object jumpTarget) {
         RubyModule implementationClass = (RubyModule)cref.getValue();
         setCRef(cref);
         pushCallFrame(noSuper ? null : clazz, name, self, args, req, block, jumpTarget);
         pushScope(new DynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postDefMethodInternalCall() {
         popRubyClass();
         popScope();
         popFrame();
         unsetCRef();
     }
     
     public void preCompiledMethod(RubyModule implementationClass, SinglyLinkedList cref) {
         pushRubyClass(implementationClass);
         setCRef(cref);
     }
     
     public void postCompiledMethod() {
         popRubyClass();
         unsetCRef();
     }
     
     // NEW! Push a scope into the frame, since this is now required to use it
     // XXX: This is screwy...apparently Ruby runs internally-implemented methods in their own frames but in the *caller's* scope
     public void preReflectedMethodInternalCall(
             RubyModule implementationClass, RubyModule klazz, IRubyObject self, 
             String name, IRubyObject[] args, int req, boolean noSuper, 
             Block block, Object jumpTarget) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
         pushCallFrame(noSuper ? null : klazz, name, self, args, req, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postReflectedMethodInternalCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preInitCoreClasses() {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void preInitBuiltinClasses(RubyClass objectClass, IRubyObject topSelf) {
         pushRubyClass(objectClass);
         setCRef(objectClass.getCRef());
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK, null);
+        // set visibility to private, since toplevel of scripts always started out private
+        setCurrentVisibility(Visibility.PRIVATE);
         setCRef(rubyClass.getCRef());
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
         unsetCRef();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         pushCRef(executeUnderClass);
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), frame.getArgs(), frame.getRequiredArgCount(), block, frame.getJumpTarget());
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popRubyClass();
         popCRef();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public void preForBlock(Block block, RubyModule klass) {
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preYieldSpecificBlock(Block block, RubyModule klass) {
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope().cloneScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preEvalWithBinding(Block block) {
         Frame frame = block.getFrame();
         
         frame.setIsBindingFrame(true);
         pushFrame(frame);
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushRubyClass(block.getKlass());
     }
     
     public void postEvalWithBinding(Block block) {
         block.getFrame().setIsBindingFrame(false);
         popFrameReal();
         unsetCRef();
         popRubyClass();
     }
     
     public void postYield() {
         popScope();
         popFrameReal();
         unsetCRef();
         popRubyClass();
     }
     
     public void preRootNode(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postRootNode() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
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
diff --git a/test/testVisibility.rb b/test/testVisibility.rb
index 785c53dd45..570c4d4692 100644
--- a/test/testVisibility.rb
+++ b/test/testVisibility.rb
@@ -1,34 +1,66 @@
 require 'test/minirunit'
 test_check "Test Visibility:"
 
 class VisibilityTest
   private
   def foo
     "foo"
   end
 
   def bar
     foo
   end
 
   public :bar
 end
 
 foo = VisibilityTest.new
 
 test_equal(foo.bar, "foo")
 test_exception(NameError) {
   foo.foo
 }
 
 $a = false
 class A
   class << self
     protected
     def a=(b); $a = true;end
 
   end
   self.a=:whatever
 end
 
-test_equal(true, $a)
\ No newline at end of file
+test_equal(true, $a)
+
+def foo
+end
+# top-level scope defaults to private
+test_no_exception { foo }
+test_exception { self.foo }
+
+# top-level scope should allow setting visibility
+public
+def foo
+end
+test_no_exception { self.foo }
+
+# module, class, struct bodies should default to public
+private
+s = "bar"
+m = Module.new { def foo; end }
+s.extend(m)
+test_no_exception { s.foo }
+
+s = Struct.new(:foo, :bar) {
+  def foo; end
+}
+test_no_exception { s.new(0,0).foo }
+
+c = Class.new { def foo; end }
+test_no_exception { c.new.foo }
+
+# blocks can't permanently modify containing frame's visibility
+1.times { public }
+def foo; end
+test_exception { self.foo }
\ No newline at end of file
