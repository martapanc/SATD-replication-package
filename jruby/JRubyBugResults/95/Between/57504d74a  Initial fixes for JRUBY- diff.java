diff --git a/src/org/jruby/IncludedModuleWrapper.java b/src/org/jruby/IncludedModuleWrapper.java
index 1720b0936a..801d8f738e 100644
--- a/src/org/jruby/IncludedModuleWrapper.java
+++ b/src/org/jruby/IncludedModuleWrapper.java
@@ -1,136 +1,137 @@
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
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import java.util.Map;
 
 /**
  * This class is used to provide an intermediate superclass for modules and classes that include
  * other modules. It inserts itself as the immediate superClass of the includer, but defers all
  * module methods to the actual superclass. Multiple of these intermediate superclasses can be
  * added for multiple included modules.
  * 
  * This allows the normal superclass-based searches (searchMethod, getConstant, etc) to traverse
  * the superclass ancestors as normal while the included modules do not actually show up in
  * direct inheritance traversal.
  * 
  * @see org.jruby.RubyModule
  */
 public final class IncludedModuleWrapper extends RubyClass {
     private RubyModule delegate;
 
     public IncludedModuleWrapper(IRuby runtime, RubyClass superClass, RubyModule delegate) {
-        super(runtime, superClass);
+        super(runtime, superClass, null);
+        // FIXME: The null makes me nervous, but it makes sense that an included wrapper would never have an allocator
 
         this.delegate = delegate;
     }
 
     /**
      * Overridden newIncludeClass implementation to allow attaching future includes to the correct module
      * (i.e. the one to which this is attached)
      * 
      * @see org.jruby.RubyModule#newIncludeClass(RubyClass)
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClass) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClass, getNonIncludedClass());
         
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
         
         return includedModule;
     }
 
     public boolean isModule() {
         return false;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isIncluded() {
         return true;
     }
     
     public boolean isImmediate() {
         return true;
     }
 
     public RubyClass getMetaClass() {
 		return delegate.getMetaClass();
     }
 
     public void setMetaClass(RubyClass newRubyClass) {
         throw new UnsupportedOperationException("An included class is only a wrapper for a module");
     }
 
     public Map getMethods() {
         return delegate.getMethods();
     }
 
     public void setMethods(Map newMethods) {
         throw new UnsupportedOperationException("An included class is only a wrapper for a module");
     }
 
     public Map getInstanceVariables() {
         return delegate.getInstanceVariables();
     }
 
     public void setInstanceVariables(Map newMethods) {
         throw new UnsupportedOperationException("An included class is only a wrapper for a module");
     }
 
     public String getName() {
 		return delegate.getName();
     }
 
     public RubyModule getNonIncludedClass() {
         return delegate;
     }
     
     public RubyClass getRealClass() {
         return getSuperClass().getRealClass();
     }
 
     public boolean isSame(RubyModule module) {
         return delegate.isSame(module);
     }
     
    /**
     * We don't want to reveal ourselves to Ruby code, so delegate this
     * operation.
     */    
     public RubyFixnum id() {
         return delegate.id();
     }
 }
diff --git a/src/org/jruby/MetaClass.java b/src/org/jruby/MetaClass.java
index 2ff9429440..c9b50d5272 100644
--- a/src/org/jruby/MetaClass.java
+++ b/src/org/jruby/MetaClass.java
@@ -1,82 +1,83 @@
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
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
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
 
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class MetaClass extends RubyClass {
 
-    public MetaClass(IRuby runtime, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(runtime, runtime.getClass("Class"), superClass, parentCRef, null);
+    public MetaClass(IRuby runtime, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(runtime, runtime.getClass("Class"), superClass, allocator, parentCRef, null);
     }
  
     public boolean isSingleton() {
         return true;
     }
     
     public boolean isImmediate() {
         return true;
     }
 
     protected RubyClass subclass() {
         throw getRuntime().newTypeError("can't make subclass of virtual class");
     }
 
     public void attachToObject(IRubyObject object) {
         setInstanceVariable("__attached__", object);
     }
     
 	public String getName() {
 		return "#<Class:" + getInstanceVariable("__attached__").toString() + ">";
 	}
 	
 	/**
 	 * If an object uses an anonymous class 'class << obj', then this grabs the original 
 	 * metaclass and not the one that get injected as a result of 'class << obj'.
 	 */
 	public RubyClass getRealClass() {
         return getSuperClass().getRealClass();
     }
     
     public void methodAdded(RubySymbol symbol) {
         getAttachedObject().callMethod(getRuntime().getCurrentContext(), "singleton_method_added", symbol);
     }
 
     public IRubyObject getAttachedObject() {
     	// Though it may not be obvious, attachToObject is always called just after instance
     	// creation.  Kind of a brittle arrangement here...
         return getInstanceVariable("__attached__");
     }
 
     public IRubyObject allocateObject() {
         throw getRuntime().newTypeError("can't create instance of virtual class");
     }
 }
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 06215915ea..0679e53f7f 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1618 +1,1618 @@
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
 import java.security.AccessControlException;
 import java.util.Hashtable;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
 
 import org.jruby.ast.Node;
 import org.jruby.compiler.InstructionCompiler2;
 import org.jruby.ast.executable.Script;
 import org.jruby.common.RubyWarnings;
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
 import org.jruby.libraries.SocketLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.libraries.BigDecimalLibrary;
 import org.jruby.ext.openssl.RubyOpenSSL;
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.ext.Generator;
 import org.jruby.ext.Readline;
 import org.jruby.parser.Parser;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
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
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby implements IRuby {
 	private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "etc", "nkf" };
     
 	private CacheMap cacheMap = new CacheMap();
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
 
     private RubyProc traceFunction;
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private final boolean objectSpaceEnabled;
     
     /**
      * What encoding should we read source files in as...
      * @see org.jruby.util.CommandlineParser#processArgument()
      */
     private String encoding = "ISO8859_1";
 
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
-        RubyClass classClass = new RubyClass(this, null /* Would be Class if it could */, moduleClass, null, "Class");
+        RubyClass classClass = RubyClass.newClassClass(this, moduleClass);
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
             standardError = defineClass("StandardError", exceptionClass);
         }
         if(profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError);
         }
         if(profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError);
         }
         if(profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass);
         }
         if(profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if(profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError);
         }
         if(profile.allowClass("SystemExit")) {
             defineClass("SystemExit", exceptionClass);
         }
         if(profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass);
         }
         if(profile.allowClass("Interrupt")) {
             defineClass("Interrupt", exceptionClass);
         }
         if(profile.allowClass("SignalException")) {
             defineClass("SignalException", exceptionClass);
         }
         if(profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError);
         }
         if(profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError);
         }
         if(profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError);
         }
         if(profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError);
         }
         if(profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError);
         }
         if(profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError);
         }
         if(profile.allowClass("NoMethodError")) {
             defineClass("NoMethodError", nameError);
         }
         if(profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError);
         }
         if(profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass);
         }
         if(profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError);
         }
         if(profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError);
         }
         if(profile.allowClass("LocalJumpError")) {
             defineClass("LocalJumpError", standardError);
         }
         if(profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError);
         }
         if(profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass);
         }
         if(profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError);
         }
         // FIXME: Actually this somewhere
         if(profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError);
         }
         if(profile.allowClass("NativeException")) {
             NativeException.createClass(this, runtimeError);
         }
         if(profile.allowClass("SystemCallError")) {
             systemCallError = defineClass("SystemCallError", standardError);
         }
         if(profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
         }
        
         initErrnoErrors();
 
         if(profile.allowClass("Data")) {
             defineClass("Data",objectClass);
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
             errnoModule.defineClassUnder(name, systemCallError).defineConstant("Errno", newFixnum(i));
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
 }
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 3cdae715c4..664de29538 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -26,1641 +26,1642 @@
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
 
 import java.lang.reflect.Array;
 
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Set;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.ConversionIterator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.ArrayMetaClass;
 import org.jruby.runtime.builtin.meta.StringMetaClass;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Pack;
 import org.jruby.util.collections.IdentitySet;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
     private List list;
     private boolean tmpLock;
 
 	private RubyArray(IRuby runtime, List list) {
 		super(runtime, runtime.getClass("Array"));
         this.list = list;
     }
 
     /** Getter for property list.
      * @return Value of property list.
      */
     public List getList() {
         return list;
     }
 
     public IRubyObject[] toJavaArray() {
         return (IRubyObject[])list.toArray(new IRubyObject[getLength()]);
     }
     
     public RubyArray convertToArray() {
     	return this;
     }
 
     /** Getter for property tmpLock.
      * @return Value of property tmpLock.
      */
     public boolean isTmpLock() {
         return tmpLock;
     }
 
     /** Setter for property tmpLock.
      * @param tmpLock New value of property tmpLock.
      */
     public void setTmpLock(boolean tmpLock) {
         this.tmpLock = tmpLock;
     }
 
     public int getLength() {
         return list.size();
     }
 
     public boolean includes(IRubyObject item) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, n = getLength(); i < n; i++) {
             if (item.callMethod(context, "==", entry(i)).isTrue()) {
                 return true;
             }
         }
         return false;
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(list.hashCode());
     }
 
     /** rb_ary_modify
      *
      */
     public void modify() {
     	testFrozen("Array");
         if (isTmpLock()) {
             throw getRuntime().newTypeError("can't modify array during sort");
         }
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify array");
         }
     }
 
     /* if list's size is not at least 'toLength', add nil's until it is */
     private void autoExpand(long toLength) {
         //list.ensureCapacity((int) toLength);
         for (int i = getLength(); i < toLength; i++) {
             list.add(getRuntime().getNil());
         }
     }
 
     /** rb_ary_store
      *
      */
     private IRubyObject store(long index, IRubyObject value) {
         modify();
         if (index < 0) {
             index += getLength();
             if (index < 0) {
                 throw getRuntime().newIndexError("index " + (index - getLength()) + " out of array");
             }
         }
         autoExpand(index + 1);
         list.set((int) index, value);
         return value;
     }
 
     public IRubyObject entry(long offset) {
     	return entry(offset, false);
     }
     
     /** rb_ary_entry
      *
      */
     public IRubyObject entry(long offset, boolean throwException) {
         if (getLength() == 0) {
         	if (throwException) {
         		throw getRuntime().newIndexError("index " + offset + " out of array");
         	} 
         	return getRuntime().getNil();
         }
         if (offset < 0) {
             offset += getLength();
         }
         if (offset < 0 || getLength() <= offset) {
         	if (throwException) {
         		throw getRuntime().newIndexError("index " + offset + " out of array");
         	} 
             return getRuntime().getNil();
         }
         return (IRubyObject) list.get((int) offset);
     }
     
     public IRubyObject fetch(IRubyObject[] args) {
     	checkArgumentCount(args, 1, 2);
 
     	RubyInteger index = args[0].convertToInteger();
     	try {
     		return entry(index.getLongValue(), true);
     	} catch (RaiseException e) {
             ThreadContext tc = getRuntime().getCurrentContext();
     		// FIXME: use constant or method for IndexError lookup?
     		RubyException raisedException = e.getException();
     		if (raisedException.isKindOf(getRuntime().getClassFromPath("IndexError"))) {
 	    		if (args.length > 1) {
 	    			return args[1];
 	    		} else if (tc.isBlockGiven()) {
 	    			return tc.yield(index);
 	    		}
     		}
     		
     		throw e;
     	}
     }
     
     public IRubyObject insert(IRubyObject[] args) {
     	checkArgumentCount(args, 1, -1);
     	// ruby does not bother to bounds check index, if no elements are to be added.
     	if (args.length == 1) {
     	    return this;
     	}
     	
     	// too negative of an offset will throw an IndexError
     	long offset = args[0].convertToInteger().getLongValue();
     	if (offset < 0 && getLength() + offset < -1) {
     		throw getRuntime().newIndexError("index " + 
     				(getLength() + offset) + " out of array");
     	}
     	
     	// An offset larger than the current length will pad with nils
     	// to length
     	if (offset > getLength()) {
     		long difference = offset - getLength();
     		IRubyObject nil = getRuntime().getNil();
     		for (long i = 0; i < difference; i++) {
     			list.add(nil);
     		}
     	}
     	
     	if (offset < 0) {
     		offset += getLength() + 1;
     	}
     	
     	for (int i = 1; i < args.length; i++) {
     		list.add((int) (offset + i - 1), args[i]);
     	}
     	
     	return this;
     }
 
     public RubyArray transpose() {
     	RubyArray newArray = getRuntime().newArray();
     	int length = getLength();
     	
     	if (length == 0) {
     		return newArray;
     	}
 
     	for (int i = 0; i < length; i++) {
     	    if (!(entry(i) instanceof RubyArray)) {
     		    throw getRuntime().newTypeError("Some error");
     	    }
     	}
     	
     	int width = ((RubyArray) entry(0)).getLength();
 
 		for (int j = 0; j < width; j++) {
     		RubyArray columnArray = getRuntime().newArray(length);
     		
 			for (int i = 0; i < length; i++) {
 				try {
 				    columnArray.append((IRubyObject) ((RubyArray) entry(i)).list.get(j));
 				} catch (IndexOutOfBoundsException e) {
 					throw getRuntime().newIndexError("element size differ (" + i +
 							" should be " + width + ")");
 				}
     		}
 			
 			newArray.append(columnArray);
     	}
     	
     	return newArray;
     }
 
     public IRubyObject values_at(IRubyObject[] args) {
     	RubyArray newArray = getRuntime().newArray();
 
     	for (int i = 0; i < args.length; i++) {
     		IRubyObject o = aref(new IRubyObject[] {args[i]});
     		if (args[i] instanceof RubyRange) {
     			if (o instanceof RubyArray) {
     				for (Iterator j = ((RubyArray) o).getList().iterator(); j.hasNext();) {
     					newArray.append((IRubyObject) j.next());
     				}
     			}
     		} else {
     			newArray.append(o);    			
     		}
     	}
     	return newArray;
     }
     
     /** rb_ary_unshift
      *
      */
     public RubyArray unshift(IRubyObject item) {
         modify();
         list.add(0, item);
         return this;
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseq(long beg, long len) {
         int length = getLength();
 
         if (beg > length || beg < 0 || len < 0) {
             return getRuntime().getNil();
         }
 
         if (beg + len > length) {
             len = length - beg;
         }
         return len <= 0 ? getRuntime().newArray(0) :
         	getRuntime().newArray( 
         			new ArrayList(list.subList((int)beg, (int) (len + beg))));
     }
 
     /** rb_ary_replace
      *	@todo change the algorythm to make it efficient
      *			there should be no need to do any deletion or addition
      *			when the replacing object is an array of the same length
      *			and in any case we should minimize them, they are costly
      */
     public void replace(long beg, long len, IRubyObject repl) {
         int length = getLength();
 
         if (len < 0) {
             throw getRuntime().newIndexError("Negative array length: " + len);
         }
         if (beg < 0) {
             beg += length;
         }
         if (beg < 0) {
             throw getRuntime().newIndexError("Index out of bounds: " + beg);
         }
 
         modify();
 
         for (int i = 0; beg < getLength() && i < len; i++) {
             list.remove((int) beg);
         }
         autoExpand(beg);
         if (repl instanceof RubyArray) {
             List repList = ((RubyArray) repl).getList();
             //list.ensureCapacity(getLength() + repList.size());
             list.addAll((int) beg, new ArrayList(repList));
         } else if (!repl.isNil()) {
             list.add((int) beg, repl);
         }
     }
 
     private boolean flatten(List array) {
         return flatten(array, new IdentitySet(), null, -1);
     }
 
     private boolean flatten(List array, IdentitySet visited, List toModify, int index) {
         if (visited.contains(array)) {
             throw getRuntime().newArgumentError("tried to flatten recursive array");
         }
         visited.add(array);
         boolean isModified = false;
         for (int i = array.size() - 1; i >= 0; i--) {
             Object elem = array.get(i);
             if (elem instanceof RubyArray) {
                 if (toModify == null) { // This is the array to flatten
                     array.remove(i);
                     flatten(((RubyArray) elem).getList(), visited, array, i);
                 } else { // Sub-array, recurse
                     flatten(((RubyArray) elem).getList(), visited, toModify, index);
                 }
                 isModified = true;
             } else if (toModify != null) { // Add sub-list element to flattened array
                 toModify.add(index, elem);
             }
         }
         visited.remove(array);
         return isModified;
     }
 
     //
     // Methods of the Array Class (rb_ary_*):
     //
 
     /** rb_ary_new2
      *
      */
     public static final RubyArray newArray(final IRuby runtime, final long len) {
         return new RubyArray(runtime, new ArrayList((int) len));
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArray(final IRuby runtime) {
         /* Ruby arrays default to holding 16 elements, so we create an
          * ArrayList of the same size if we're not told otherwise
          */
     	
         return new RubyArray(runtime, new ArrayList(16));
     }
 
     /**
      *
      */
     public static RubyArray newArray(IRuby runtime, IRubyObject obj) {
         ArrayList list = new ArrayList(1);
         list.add(obj);
         return new RubyArray(runtime, list);
     }
 
     /** rb_assoc_new
      *
      */
     public static RubyArray newArray(IRuby runtime, IRubyObject car, IRubyObject cdr) {
         ArrayList list = new ArrayList(2);
         list.add(car);
         list.add(cdr);
         return new RubyArray(runtime, list);
     }
 
     public static final RubyArray newArray(final IRuby runtime, final List list) {
         return new RubyArray(runtime, list);
     }
 
     public static RubyArray newArray(IRuby runtime, IRubyObject[] args) {
         final ArrayList list = new ArrayList(args.length);
         for (int i = 0; i < args.length; i++) {
             list.add(args[i]);
         }
         return new RubyArray(runtime, list);
     }
 
     /** rb_ary_length
      *
      */
     public RubyFixnum length() {
         return getRuntime().newFixnum(getLength());
     }
 
     /** rb_ary_push_m
      *
      */
     public RubyArray push(IRubyObject[] items) {
         modify();
         boolean tainted = false;
         for (int i = 0; i < items.length; i++) {
             tainted |= items[i].isTaint();
             list.add(items[i]);
         }
         setTaint(isTaint() || tainted);
         return this;
     }
 
     public RubyArray append(IRubyObject value) {
         modify();
         list.add(value);
         infectBy(value);
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     public IRubyObject pop() {
         modify();
         int length = getLength();
         return length == 0 ? getRuntime().getNil() : 
         	(IRubyObject) list.remove(length - 1);
     }
 
     /** rb_ary_shift
      *
      */
     public IRubyObject shift() {
         modify();
         return getLength() == 0 ? getRuntime().getNil() : 
         	(IRubyObject) list.remove(0);
     }
 
     /** rb_ary_unshift_m
      *
      */
     public RubyArray unshift(IRubyObject[] items) {
         modify();
         boolean taint = false;
         for (int i = 0; i < items.length; i++) {
             taint |= items[i].isTaint();
             list.add(i, items[i]);
         }
         setTaint(isTaint() || taint);
         return this;
     }
 
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen() || isTmpLock());
     }
 
     /** rb_ary_initialize
      */
     public IRubyObject initialize(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 0, 2);
         RubyArray arrayInitializer = null;
         long len = 0;
         if (argc > 0) {
         	if (args[0] instanceof RubyArray) {
         		arrayInitializer = (RubyArray)args[0];
         	} else {
         		len = convertToLong(args[0]);
         	}
         }
 
         modify();
 
         // Array initializer is provided
         if (arrayInitializer != null) {
         	list = new ArrayList(arrayInitializer.list);
         	return this;
         }
         
         // otherwise, continue with Array.new(fixnum, obj)
         if (len < 0) {
             throw getRuntime().newArgumentError("negative array size");
         }
         if (len > Integer.MAX_VALUE) {
             throw getRuntime().newArgumentError("array size too big");
         }
         list = new ArrayList((int) len);
         ThreadContext tc = getRuntime().getCurrentContext();
         if (len > 0) {
         	if (tc.isBlockGiven()) {
         		// handle block-based array initialization
                 for (int i = 0; i < len; i++) {
                     list.add(tc.yield(new RubyFixnum(getRuntime(), i)));
                 }
         	} else {
         		IRubyObject obj = (argc == 2) ? args[1] : getRuntime().getNil();
         		list.addAll(Collections.nCopies((int)len, obj));
         	}
         }
         return this;
     }
 
     /** rb_ary_aref
      */
     public IRubyObject aref(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 1, 2);
         if (argc == 2) {
             long beg = RubyNumeric.fix2long(args[0]);
             long len = RubyNumeric.fix2long(args[1]);
             if (beg < 0) {
                 beg += getLength();
             }
             return subseq(beg, len);
         }
         if (args[0] instanceof RubyFixnum) {
             return entry(RubyNumeric.fix2long(args[0]));
         }
         if (args[0] instanceof RubyBignum) {
             throw getRuntime().newIndexError("index too big");
         }
         if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), true, false);
 
             return begLen == null ? newArray(getRuntime()) : subseq(begLen[0], begLen[1]);
         }
         if(args[0] instanceof RubySymbol) {
             throw getRuntime().newTypeError("Symbol as array index");
         }
         return entry(args[0].convertToInteger().getLongValue());
     }
 
     /** rb_ary_aset
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 2, 3);
         if (argc == 3) {
             long beg = args[0].convertToInteger().getLongValue();
             long len = args[1].convertToInteger().getLongValue();
             replace(beg, len, args[2]);
             return args[2];
         }
         if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), false, true);
             replace(begLen[0], begLen[1], args[1]);
             return args[1];
         }
         if (args[0] instanceof RubyBignum) {
             throw getRuntime().newIndexError("Index too large");
         }
         return store(args[0].convertToInteger().getLongValue(), args[1]);
     }
 
     /** rb_ary_at
      *
      */
     public IRubyObject at(IRubyObject pos) {
         return entry(convertToLong(pos));
     }
 
 	private long convertToLong(IRubyObject pos) {
 		if (pos instanceof RubyNumeric) {
 			return ((RubyNumeric) pos).getLongValue();
 		}
 		throw getRuntime().newTypeError("cannot convert " + pos.getType().getBaseName() + " to Integer");
 	}
 
 	/** rb_ary_concat
      *
      */
     public RubyArray concat(IRubyObject obj) {
         modify();
         RubyArray other = obj.convertToArray();
         list.addAll(other.getList());
         infectBy(other);
         return this;
     }
 
     /** rb_ary_inspect
      *
      */
     public IRubyObject inspect() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("[...]");
         }
         try {
             int length = getLength();
 
             if (length == 0) {
                 return getRuntime().newString("[]");
             }
             RubyString result = getRuntime().newString("[");
             RubyString separator = getRuntime().newString(", ");
             ThreadContext context = getRuntime().getCurrentContext();
             for (int i = 0; i < length; i++) {
                 if (i > 0) {
                     result.append(separator);
                 }
                 result.append(entry(i).callMethod(context, "inspect"));
             }
             result.cat("]");
             return result;
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_first
      *
      */
     public IRubyObject first(IRubyObject[] args) {
     	checkArgumentCount(args, 0, 1);
 
     	if (args.length == 0) {
     		return getLength() == 0 ? getRuntime().getNil() : entry(0);
     	}
     	
     	// TODO: See if enough integer-only conversions to make this
     	// convenience function (which could replace RubyNumeric#fix2long).
     	if (!(args[0] instanceof RubyInteger)) {
             throw getRuntime().newTypeError("Cannot convert " + 
             		args[0].getType() + " into Integer");
     	}
     	
     	long length = ((RubyInteger)args[0]).getLongValue();
     	
     	if (length < 0) {
     		throw getRuntime().newArgumentError(
     				"negative array size (or size too big)");
     	}
     	
     	return subseq(0, length);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
         int count = checkArgumentCount(args, 0, 1);
     	int length = getLength();
     	
     	int listSize = list.size();
     	int sublistSize = 0;
     	int startIndex = 0;
     		
     	switch (count) {
         case 0:
             return length == 0 ? getRuntime().getNil() : entry(length - 1);
         case 1:
             sublistSize = RubyNumeric.fix2int(args[0]);
             if (sublistSize == 0) {
                 return getRuntime().newArray();
             }
             if (sublistSize < 0) {
                 throw getRuntime().newArgumentError("negative array size (or size too big)");
             }
 
             startIndex = (sublistSize > listSize) ? 0 : listSize - sublistSize;
             return getRuntime().newArray(list.subList(startIndex, listSize));
         default:
             assert false;
         	return null;
         }
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each() {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = getLength(); i < len; i++) {
             context.yield(entry(i));
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     public IRubyObject each_index() {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = getLength(); i < len; i++) {
             context.yield(getRuntime().newFixnum(i));
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverse_each() {
         ThreadContext context = getRuntime().getCurrentContext();
         for (long i = getLength(); i > 0; i--) {
             context.yield(entry(i - 1));
         }
         return this;
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(RubyString sep) {
         StringBuffer buf = new StringBuffer();
         int length = getLength();
         if (length == 0) {
             getRuntime().newString("");
         }
         boolean taint = isTaint() || sep.isTaint();
         RubyString str;
         IRubyObject tmp = null;
         for (long i = 0; i < length; i++) {
             tmp = entry(i);
             taint |= tmp.isTaint();
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
                 tmp = ((RubyArray) tmp).to_s_join(sep);
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
             
             if (i > 0 && !sep.isNil()) {
                 buf.append(sep.toString());
             }
             buf.append(((RubyString)tmp).toString());
         }
         str = RubyString.newString(getRuntime(), buf.toString());
         str.setTaint(taint);
         return str;
     }
 
     /** rb_ary_join_m
      *
      */
     public RubyString join(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         return join(sep.isNil() ? getRuntime().newString("") : RubyString.stringValue(sep));
     }
 
     /** rb_ary_to_s
      *
      */
     public IRubyObject to_s() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("[...]");
         }
         try {
             IRubyObject separatorObject = getRuntime().getGlobalVariables().get("$,");
             RubyString separator;
             if (separatorObject.isNil()) {
                 separator = getRuntime().newString("");
             } else {
                 separator = RubyString.stringValue(separatorObject);
             }
             return join(separator);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     private IRubyObject to_s_join(RubyString sep) {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("[...]");
         }
         try {
             return join(sep);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         return this;
     }
     
     public IRubyObject to_ary() {
     	return this;
     }
 
     /** rb_ary_equal
      *
      */
     public IRubyObject array_op_equal(IRubyObject obj) {
         if (this == obj) {
             return getRuntime().getTrue();
         }
 
         RubyArray ary;
         
         if (!(obj instanceof RubyArray)) {
             if (obj.respondsTo("to_ary")) {
                 ary = obj.convertToArray();
             } else {
                 return getRuntime().getFalse();
             }
         } else {
         	ary = (RubyArray) obj;
         }
         
         int length = getLength();
 
         if (length != ary.getLength()) {
             return getRuntime().getFalse();
         }
 
         for (long i = 0; i < length; i++) {
             if (!entry(i).callMethod(getRuntime().getCurrentContext(), "==", ary.entry(i)).isTrue()) {
                 return getRuntime().getFalse();
             }
         }
         return getRuntime().getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql(IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             return getRuntime().getFalse();
         }
         int length = getLength();
 
         RubyArray ary = (RubyArray) obj;
         
         if (length != ary.getLength()) {
             return getRuntime().getFalse();
         }
         
         ThreadContext context = getRuntime().getCurrentContext();
 
         for (long i = 0; i < length; i++) {
             if (!entry(i).callMethod(context, "eql?", ary.entry(i)).isTrue()) {
                 return getRuntime().getFalse();
             }
         }
         return getRuntime().getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     public IRubyObject compact_bang() {
         modify();
         boolean isChanged = false;
         for (int i = getLength() - 1; i >= 0; i--) {
             if (entry(i).isNil()) {
                 list.remove(i);
                 isChanged = true;
             }
         }
         return isChanged ? (IRubyObject) this : (IRubyObject) getRuntime().getNil();
     }
 
     /** rb_ary_compact
      *
      */
     public IRubyObject compact() {
         RubyArray ary = (RubyArray) dup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     public IRubyObject empty_p() {
         return getLength() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     public IRubyObject rb_clear() {
         modify();
         list.clear();
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     public IRubyObject fill(IRubyObject[] args) {
         int beg = 0;
         int len = getLength();
         int argc;
         IRubyObject filObj;
         IRubyObject begObj;
         IRubyObject lenObj;
         IRuby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         if (tc.isBlockGiven()) {
         	argc = checkArgumentCount(args, 0, 2);
         	filObj = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
         	argc = checkArgumentCount(args, 1, 3);
         	filObj = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
         switch (argc) {
             case 1 :
                 break;
             case 2 :
                 if (begObj instanceof RubyRange) {
                     long[] begLen = ((RubyRange) begObj).getBeginLength(len, false, true);
                     beg = (int) begLen[0];
                     len = (int) begLen[1];
                     break;
                 }
                 /* fall through */
             default :
                 beg = begObj.isNil() ? beg : RubyNumeric.fix2int(begObj);
                 if (beg < 0 && (beg += len) < 0) {
                     throw getRuntime().newIndexError("Negative array index");
                 }
                 len -= beg;
                 if (argc == 3 && !lenObj.isNil()) {
                     len = RubyNumeric.fix2int(lenObj);
                 }
         }
 
         modify();
         autoExpand(beg + len);
         for (int i = beg; i < beg + len; i++) {
         	if (filObj == null) {
         		list.set(i, tc.yield(runtime.newFixnum(i)));
         	} else {
         		list.set(i, filObj);
         	}
         }
         return this;
     }
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(IRubyObject obj) {
         ThreadContext context = getRuntime().getCurrentContext();
         int len = getLength();
         for (int i = 0; i < len; i++) {
             if (entry(i).callMethod(context, "==", obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
         return getRuntime().getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(IRubyObject obj) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = getLength() - 1; i >= 0; i--) {
             if (entry(i).callMethod(context, "==", obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject indices(IRubyObject[] args) {
         IRubyObject[] result = new IRubyObject[args.length];
         boolean taint = false;
         for (int i = 0; i < args.length; i++) {
             result[i] = entry(RubyNumeric.fix2int(args[i]));
             taint |= result[i].isTaint();
         }
+        // TODO: Why was is calling create, which used to skip array initialization?
         IRubyObject ary = ((ArrayMetaClass) getMetaClass()).create(result);
         ary.setTaint(taint);
         return ary;
     }
 
     /** rb_ary_clone
      *
      */
     public IRubyObject rbClone() {
         RubyArray result = getRuntime().newArray(new ArrayList(list));
         result.setTaint(isTaint());
         result.initCopy(this);
         result.setFrozen(isFrozen());
         return result;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     public IRubyObject reverse_bang() {
         modify();
         Collections.reverse(list);
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     public IRubyObject reverse() {
         RubyArray result = (RubyArray) dup();
         result.reverse_bang();
         return result;
     }
 
     /** rb_ary_collect
      *
      */
     public RubyArray collect() {
         ThreadContext tc = getRuntime().getCurrentContext();
         if (!tc.isBlockGiven()) {
             return (RubyArray) dup();
         }
         ArrayList ary = new ArrayList();
         for (int i = 0, len = getLength(); i < len; i++) {
             ary.add(tc.yield(entry(i)));
         }
         return new RubyArray(getRuntime(), ary);
     }
 
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collect_bang() {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = getLength(); i < len; i++) {
             list.set(i, context.yield(entry(i)));
         }
         return this;
     }
 
     /** rb_ary_delete
      *
      */
     public IRubyObject delete(IRubyObject obj) {
         modify();
         ThreadContext tc = getRuntime().getCurrentContext();
         IRubyObject result = getRuntime().getNil();
         for (int i = getLength() - 1; i >= 0; i--) {
             if (obj.callMethod(tc, "==", entry(i)).isTrue()) {
                 result = (IRubyObject) list.remove(i);
             }
         }
         if (result.isNil() && tc.isBlockGiven()) {
             result = tc.yield(entry(0));
         }
         return result;
     }
 
     /** rb_ary_delete_at
      *
      */
     public IRubyObject delete_at(IRubyObject obj) {
         modify();
         int pos = (int) obj.convertToInteger().getLongValue();
         int len = getLength();
         if (pos >= len) {
             return getRuntime().getNil();
         }
         
         return pos < 0 && (pos += len) < 0 ?
             getRuntime().getNil() : (IRubyObject) list.remove(pos);
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject reject_bang() {
         modify();
         IRubyObject retVal = getRuntime().getNil();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = getLength() - 1; i >= 0; i--) {
             if (context.yield(entry(i)).isTrue()) {
                 retVal = (IRubyObject) list.remove(i);
             }
         }
         return retVal.isNil() ? (IRubyObject) retVal : (IRubyObject) this;
     }
 
     /** rb_ary_delete_if
      *
      */
     public IRubyObject delete_if() {
         reject_bang();
         return this;
     }
 
     /** rb_ary_replace
      *
      */
     public IRubyObject replace(IRubyObject other) {
         replace(0, getLength(), other.convertToArray());
         return this;
     }
 
     /** rb_ary_cmp
      *
      */
     public IRubyObject op_cmp(IRubyObject other) {
         RubyArray ary = other.convertToArray();
         int otherLen = ary.getLength();
         int len = getLength();
         int minCommon = Math.min(len, otherLen);
         ThreadContext context = getRuntime().getCurrentContext();
         RubyClass fixnumClass = getRuntime().getClass("Fixnum");
         for (int i = 0; i < minCommon; i++) {
         	IRubyObject result = entry(i).callMethod(context, "<=>", ary.entry(i));
             if (! result.isKindOf(fixnumClass) || RubyFixnum.fix2int(result) != 0) {
                 return result;
             }
         }
         if (len != otherLen) {
             return len < otherLen ? RubyFixnum.minus_one(getRuntime()) : RubyFixnum.one(getRuntime());
         }
         return RubyFixnum.zero(getRuntime());
     }
 
     /** rb_ary_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 1, 2);
         IRubyObject result = aref(args);
         if (argc == 2) {
             long beg = RubyNumeric.fix2long(args[0]);
             long len = RubyNumeric.fix2long(args[1]);
             replace(beg, len, getRuntime().getNil());
         } else if (args[0] instanceof RubyFixnum && RubyNumeric.fix2long(args[0]) < getLength()) {
             replace(RubyNumeric.fix2long(args[0]), 1, getRuntime().getNil());
         } else if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), false, true);
             replace(begLen[0], begLen[1], getRuntime().getNil());
         }
         return result;
     }
 
     /** rb_ary_assoc
      *
      */
     public IRubyObject assoc(IRubyObject arg) {
         for (int i = 0, len = getLength(); i < len; i++) {
             if (!(entry(i) instanceof RubyArray && ((RubyArray) entry(i)).getLength() > 0)) {
                 continue;
             }
             RubyArray ary = (RubyArray) entry(i);
             if (arg.callMethod(getRuntime().getCurrentContext(), "==", ary.entry(0)).isTrue()) {
                 return ary;
             }
         }
         return getRuntime().getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     public IRubyObject rassoc(IRubyObject arg) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = 0, len = getLength(); i < len; i++) {
             if (!(entry(i) instanceof RubyArray && ((RubyArray) entry(i)).getLength() > 1)) {
                 continue;
             }
             RubyArray ary = (RubyArray) entry(i);
             if (arg.callMethod(context, "==", ary.entry(1)).isTrue()) {
                 return ary;
             }
         }
         return getRuntime().getNil();
     }
 
     /** rb_ary_flatten_bang
      *
      */
     public IRubyObject flatten_bang() {
         modify();
         return flatten(list) ? this : getRuntime().getNil();
     }
 
     /** rb_ary_flatten
      *
      */
     public IRubyObject flatten() {
         RubyArray rubyArray = (RubyArray) dup();
         rubyArray.flatten_bang();
         return rubyArray;
     }
 
     /** rb_ary_nitems
      *
      */
     public IRubyObject nitems() {
         int count = 0;
         for (int i = 0, len = getLength(); i < len; i++) {
             count += entry(i).isNil() ? 0 : 1;
         }
         return getRuntime().newFixnum(count);
     }
 
     /** rb_ary_plus
      *
      */
     public IRubyObject op_plus(IRubyObject other) {
         List otherList = other.convertToArray().getList();
         List newList = new ArrayList(getLength() + otherList.size());
         newList.addAll(list);
         newList.addAll(otherList);
         return new RubyArray(getRuntime(), newList);
     }
 
     /** rb_ary_times
      *
      */
     public IRubyObject op_times(IRubyObject arg) {
         if (arg instanceof RubyString) {
             return join((RubyString) arg);
         }
 
         int len = (int) arg.convertToInteger().getLongValue();
         if (len < 0) {
             throw getRuntime().newArgumentError("negative argument");
         }
         ArrayList newList = new ArrayList(getLength() * len);
         for (int i = 0; i < len; i++) {
             newList.addAll(list);
         }
         return new RubyArray(getRuntime(), newList);
     }
 
     private static ArrayList uniq(List oldList) {
         ArrayList newList = new ArrayList(oldList.size());
         Set passed = new HashSet(oldList.size());
 
         for (Iterator iter = oldList.iterator(); iter.hasNext();) {
             Object item = iter.next();
             if (! passed.contains(item)) {
                 passed.add(item);
                 newList.add(item);
             }
         }
         newList.trimToSize();
         return newList;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     public IRubyObject uniq_bang() {
         modify();
         ArrayList newList = uniq(list);
         if (newList.equals(list)) {
             return getRuntime().getNil();
         }
         list = newList;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     public IRubyObject uniq() {
         return new RubyArray(getRuntime(), uniq(list));
     }
 
     /** rb_ary_diff
      *
      */
     public IRubyObject op_diff(IRubyObject other) {
         List ary1 = new ArrayList(list);
         List ary2 = other.convertToArray().getList();
         int len2 = ary2.size();
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = ary1.size() - 1; i >= 0; i--) {
             IRubyObject obj = (IRubyObject) ary1.get(i);
             for (int j = 0; j < len2; j++) {
                 if (obj.callMethod(context, "==", (IRubyObject) ary2.get(j)).isTrue()) {
                     ary1.remove(i);
                     break;
                 }
             }
         }
         return new RubyArray(getRuntime(), ary1);
     }
 
     /** rb_ary_and
      *
      */
     public IRubyObject op_and(IRubyObject other) {
     	RubyClass arrayClass = getRuntime().getClass("Array");
     	
     	// & only works with array types
     	if (!other.isKindOf(arrayClass)) {
     		throw getRuntime().newTypeError(other, arrayClass);
     	}
         List ary1 = uniq(list);
         int len1 = ary1.size();
         List ary2 = other.convertToArray().getList();
         int len2 = ary2.size();
         ArrayList ary3 = new ArrayList(len1);
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = 0; i < len1; i++) {
             IRubyObject obj = (IRubyObject) ary1.get(i);
             for (int j = 0; j < len2; j++) {
                 if (obj.callMethod(context, "eql?", (IRubyObject) ary2.get(j)).isTrue()) {
                     ary3.add(obj);
                     break;
                 }
             }
         }
         ary3.trimToSize();
         return new RubyArray(getRuntime(), ary3);
     }
 
     /** rb_ary_or
      *
      */
     public IRubyObject op_or(IRubyObject other) {
         List newArray = new ArrayList(list);
         
         newArray.addAll(other.convertToArray().getList());
         
         return new RubyArray(getRuntime(), uniq(newArray));
     }
 
     /** rb_ary_sort
      *
      */
     public RubyArray sort() {
         RubyArray rubyArray = (RubyArray) dup();
         rubyArray.sort_bang();
         return rubyArray;
     }
 
     /** rb_ary_sort_bang
      *
      */
     public IRubyObject sort_bang() {
         modify();
         setTmpLock(true);
 
         Comparator comparator;
         if (getRuntime().getCurrentContext().isBlockGiven()) {
             comparator = new BlockComparator();
         } else {
             comparator = new DefaultComparator();
         }
         Collections.sort(list, comparator);
 
         setTmpLock(false);
         return this;
     }
 
     public void marshalTo(MarshalStream output) throws IOException {
         output.write('[');
         output.dumpInt(getList().size());
         for (Iterator iter = getList().iterator(); iter.hasNext(); ) {
             output.dumpObject((IRubyObject) iter.next());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         RubyArray result = input.getRuntime().newArray();
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     public RubyString pack(IRubyObject obj) {
 	RubyString iFmt = RubyString.objAsString(obj);
         return Pack.pack(this.list, iFmt);
     }
 
     class BlockComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             ThreadContext context = getRuntime().getCurrentContext();
             IRubyObject result = context.getFrameBlockOrRaise().yield(context, getRuntime().newArray((IRubyObject) o1, (IRubyObject) o2), null, null, true);
             return (int) ((RubyNumeric) result).getLongValue();
         }
     }
 
     static class DefaultComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
             if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
             	long diff = RubyNumeric.fix2long(obj1) - RubyNumeric.fix2long(obj2);
 
             	return diff < 0 ? -1 : diff > 0 ? 1 : 0;
             }
 
             if (o1 instanceof RubyString && o2 instanceof RubyString) {
                 StringMetaClass stringMC = (StringMetaClass)((RubyObject)o1).getMetaClass();
                 return RubyNumeric.fix2int(
                         stringMC.op_cmp.call(stringMC.getRuntime().getCurrentContext(), (RubyString)o1, stringMC, "<=>", new IRubyObject[] {(RubyString)o2}, false));
             }
 
             return RubyNumeric.fix2int(obj1.callMethod(obj1.getRuntime().getCurrentContext(), "<=>", obj2));
         }
     }
 
 
     public Class getJavaClass() {
         return List.class;
     }
     
     // Satisfy java.util.List interface (for Java integration)
     
 	public int size() {
 		return list.size();
 	}
 
 	public boolean isEmpty() {
 		return list.isEmpty();
 	}
 
 	public boolean contains(Object element) {
 		return list.contains(JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public Iterator iterator() {
 		return new ConversionIterator(list.iterator());
 	}
 
 	public Object[] toArray() {
 		Object[] array = new Object[getLength()];
 		Iterator iter = iterator();
 		
 		for (int i = 0; iter.hasNext(); i++) {
 			array[i] = iter.next();
 		}
 
 		return array;
 	}
 
 	public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
         int length = getLength();
             
         if(array.length < length) {
             Class type = array.getClass().getComponentType();
             array = (Object[])Array.newInstance(type, length);
         }
 
         Iterator iter = iterator();
         for (int i = 0; iter.hasNext(); i++) {
             array[i] = iter.next();
         }
         
         return array;
 	}
 
 	public boolean add(Object element) {
 		return list.add(JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public boolean remove(Object element) {
 		return list.remove(JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public boolean containsAll(Collection c) {
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (indexOf(iter.next()) == -1) {
 				return false;
 			}
 		}
 
 		return true;
 	}
 
 	public boolean addAll(Collection c) {
 		for (Iterator iter = c.iterator(); iter.hasNext(); ) {
 			add(iter.next());
 		}
 
 		return !c.isEmpty();
 	}
 
 	public boolean addAll(int index, Collection c) {
 		Iterator iter = c.iterator();
 		for (int i = index; iter.hasNext(); i++) {
 			add(i, iter.next());
 		}
 
 		return !c.isEmpty();
 	}
 
 	public boolean removeAll(Collection c) {
 		boolean changed = false;
 		
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (remove(iter.next())) {
 				changed = true;
 			}
 		}
 
 		return changed;
 	}
 
 	public boolean retainAll(Collection c) {
 		boolean listChanged = false;
 		
 		for (Iterator iter = iterator(); iter.hasNext();) {
 			Object element = iter.next();
 			if (!c.contains(element)) {
 				remove(element);
 				listChanged = true;
 			}
 		}
 
 		return listChanged;
 	}
 
 	public Object get(int index) {
 		return JavaUtil.convertRubyToJava((IRubyObject) list.get(index), Object.class);
 	}
 
 	public Object set(int index, Object element) {
 		return list.set(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public void add(int index, Object element) {
 		list.add(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public Object remove(int index) {
 		return JavaUtil.convertRubyToJava((IRubyObject) list.remove(index), Object.class);
 	}
 
 	public int indexOf(Object element) {
 		return list.indexOf(JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public int lastIndexOf(Object element) {
 		return list.lastIndexOf(JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
 	public ListIterator listIterator() {
 		return new ConversionListIterator(list.listIterator());
 	}
 
 	public ListIterator listIterator(int index) {
 		return new ConversionListIterator(list.listIterator(index));
 	}
 
 	// TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
 	// How can we support this operation?
 	public List subList(int fromIndex, int toIndex) {
 		if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
 			throw new IndexOutOfBoundsException();
 
 		}
 		IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
 		
 		return subList.isNil() ? null : (List) subList;  
 	}
 
 	public void clear() {
 		list.clear();
 	}
 
 	class ConversionListIterator implements ListIterator {
 		private ListIterator iterator;
 
 		public ConversionListIterator(ListIterator iterator) {
 			this.iterator = iterator;
 		}
 
 		public boolean hasNext() {
 			return iterator.hasNext();
 		}
 
 		public Object next() {
 			return JavaUtil.convertRubyToJava((IRubyObject) iterator.next(), Object.class);
 		}
 
 		public boolean hasPrevious() {
 			return iterator.hasPrevious();
 		}
 
 		public Object previous() {
 			return JavaUtil.convertRubyToJava((IRubyObject) iterator.previous(), Object.class);
 		}
 
 		public int nextIndex() {
 			return iterator.nextIndex();
 		}
 
 		public int previousIndex() {
 			return iterator.previousIndex();
 		}
 
 		public void remove() {
 			iterator.remove();
 		}
 
 		public void set(Object arg0) {
 			// TODO Auto-generated method stub
 		}
 
 		public void add(Object arg0) {
 			// TODO Auto-generated method stub
 		}
 	}
 }
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 8374762f51..98aac54977 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,301 +1,302 @@
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
 package org.jruby;
 
 import java.util.HashMap;
 
 import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyClass extends RubyModule {
 	
 	private final IRuby runtime;
+    
+    // the default allocator
+    private final ObjectAllocator allocator;
 
     /**
      * @mri rb_boot_class
      */
-    protected RubyClass(RubyClass superClass) {
+    protected RubyClass(RubyClass superClass, ObjectAllocator allocator) {
         super(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, null, null);
 
         infectBy(superClass);
         this.runtime = superClass.getRuntime();
+        this.allocator = allocator;
     }
 
-    protected RubyClass(IRuby runtime, RubyClass superClass) {
+    protected RubyClass(IRuby runtime, RubyClass superClass, ObjectAllocator allocator) {
         super(runtime, null, superClass, null, null);
+        this.allocator = allocator;
         this.runtime = runtime;
     }
 
-    protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass) {
+    protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator) {
         super(runtime, metaClass, superClass, null, null);
+        this.allocator = allocator;
         this.runtime = runtime;
     }
     
-    protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
+    protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass, superClass, parentCRef, name);
+        this.allocator = allocator;
         this.runtime = runtime;
     }
     
+    public IRubyObject allocate() {
+        return getAllocator().allocate(getRuntime(), this);
+    }
+    
+    public static RubyClass newClassClass(IRuby runtime, RubyClass moduleClass) {
+        ObjectAllocator defaultAllocator = new ObjectAllocator() {
+            public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+                IRubyObject instance = new RubyObject(runtime, klass);
+                instance.setMetaClass(klass);
+
+                return instance;
+            }
+        };
+        
+        return new RubyClass(
+                runtime,
+                null /* FIXME: should be something else? */,
+                moduleClass,
+                defaultAllocator,
+                null,
+                "Class");
+    }
+    
     /* (non-Javadoc)
 	 * @see org.jruby.RubyObject#getRuntime()
 	 */
 	public IRuby getRuntime() {
 		return runtime;
 	}
 
     public boolean isModule() {
         return false;
     }
 
     public boolean isClass() {
         return true;
     }
 
     public static void createClassClass(RubyClass classClass) {
         CallbackFactory callbackFactory = classClass.getRuntime().callbackFactory(RubyClass.class);
         classClass.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newClass"));
         classClass.defineMethod("allocate", callbackFactory.getMethod("allocate"));
         classClass.defineMethod("new", callbackFactory.getOptMethod("newInstance"));
         classClass.defineMethod("superclass", callbackFactory.getMethod("superclass"));
         classClass.defineSingletonMethod("inherited", callbackFactory.getSingletonMethod("inherited", IRubyObject.class));
         classClass.undefineMethod("module_function");
     }
     
     public static IRubyObject inherited(IRubyObject recv, IRubyObject arg) {
         return recv.getRuntime().getNil();
     }
 
     /** Invokes if  a class is inherited from an other  class.
      * 
      * MRI: rb_class_inherited
      * 
      * @since Ruby 1.6.7
      * 
      */
     public void inheritedBy(RubyClass superType) {
         if (superType == null) {
             superType = getRuntime().getObject();
         }
         superType.callMethod(getRuntime().getCurrentContext(), "inherited", this);
     }
 
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
         if (!isSingleton()) {
             return this;
         }
 
-        MetaClass clone = new MetaClass(getRuntime(), getMetaClass(), getSuperClass().getCRef());
+        MetaClass clone = new MetaClass(getRuntime(), getMetaClass(), getMetaClass().getAllocator(), getSuperClass().getCRef());
         clone.initCopy(this);
         clone.setInstanceVariables(new HashMap(getInstanceVariables()));
 
         return (RubyClass) cloneMethods(clone);
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     public RubyClass getMetaClass() {
         RubyClass type = super.getMetaClass();
 
         return type != null ? type : getRuntime().getClass("Class");
     }
 
     public RubyClass getRealClass() {
         return this;
     }
 
     public MetaClass newSingletonClass(SinglyLinkedList parentCRef) {
-        MetaClass newClass = new MetaClass(getRuntime(), this, parentCRef);
+        MetaClass newClass = new MetaClass(getRuntime(), this, this.getAllocator(), parentCRef);
         newClass.infectBy(this);
         return newClass;
     }
 
     public static RubyClass newClass(IRuby runtime, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
-        return new RubyClass(runtime, runtime.getClass("Class"), superClass, parentCRef, name);
+        return new RubyClass(runtime, runtime.getClass("Class"), superClass, superClass.getAllocator(), parentCRef, name);
     }
 
     /** Create a new subclass of this class.
      * @return the new sublass
      * @throws TypeError if this is class `Class'
      * @mri rb_class_new
      */
     protected RubyClass subclass() {
         if (this == getRuntime().getClass("Class")) {
             throw getRuntime().newTypeError("can't make subclass of Class");
         }
-        return new RubyClass(this);
+        return new RubyClass(this, getAllocator());
     }
 
     /** rb_class_new_instance
      *
      */
     public IRubyObject newInstance(IRubyObject[] args) {
-        IRubyObject obj = allocate();
+        IRubyObject obj = (IRubyObject)getAllocator().allocate(getRuntime(), this);
         obj.callInit(args);
         return obj;
     }
+    
+    public ObjectAllocator getAllocator() {
+        return allocator;
+    }
 
     /** rb_class_s_new
      *
      */
     public static RubyClass newClass(IRubyObject recv, IRubyObject[] args) {
         final IRuby runtime = recv.getRuntime();
 
         RubyClass superClass;
         if (args.length > 0) {
             if (args[0] instanceof RubyClass) {
                 superClass = (RubyClass) args[0];
             } else {
                 throw runtime.newTypeError(
                     "wrong argument type " + args[0].getType().getName() + " (expected Class)");
             }
         } else {
             superClass = runtime.getObject();
         }
 
         ThreadContext tc = runtime.getCurrentContext();
         RubyClass newClass = superClass.newSubClass(null,tc.peekCRef());
 
         // call "initialize" method
         newClass.callInit(args);
 
         // call "inherited" method of the superclass
         newClass.inheritedBy(superClass);
 
 		if (tc.isBlockGiven()) {
             tc.getFrameBlock().yield(tc, null, newClass, newClass, false);
 		}
 
 		return newClass;
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */
     public IRubyObject superclass() {
         RubyClass superClass = getSuperClass();
         while (superClass != null && superClass.isIncluded()) {
             superClass = superClass.getSuperClass();
         }
 
         return superClass != null ? superClass : getRuntime().getNil();
     }
 
     /** rb_class_s_inherited
      *
      */
     public static IRubyObject inherited(RubyClass recv) {
         throw recv.getRuntime().newTypeError("can't make subclass of Class");
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('c');
         output.dumpString(getName());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream output) throws java.io.IOException {
         return (RubyClass) RubyModule.unmarshalFrom(output);
     }
 
-    /**
-     * Creates a new object of this class by calling the 'allocateObject' method.
-     * This class must be the type of the new object.
-     * 
-     * @return the new allocated object.
-     */
-    public IRubyObject allocate() {
-        IRubyObject newObject = allocateObject();
-        if (newObject.getType() != getRealClass()) {
-            throw getRuntime().newTypeError("wrong instance allocation");
-        }
-        return newObject;
-    }
-
-    /**
-     * <p>
-     * This method is a constructor for ruby objects. It is called by the "Class#new",
-     * "Object#clone" and Object#dup" to create new object instances.
-     * </p>
-     * <p>
-     * Builtin meta classes (subclasses of {@link ObjectMetaClass}) have to override this method to
-     * create instances of the corresponding subclass of RubyObject.
-     * </p>
-     * <p>
-     * (mri: rb_class_allocate_instance)
-     * </p>
-     * 
-     * @return a new RubyObject
-     */
-    protected IRubyObject allocateObject() {
-        IRubyObject newObject = new RubyObject(runtime, this);
-        return newObject;
-    }
-
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
         RubyClass classClass = runtime.getClass("Class");
         
         // Cannot subclass 'Class' or metaclasses
         if (this == classClass) {
             throw runtime.newTypeError("can't make subclass of Class");
         } else if (this instanceof MetaClass) {
             throw runtime.newTypeError("can't make subclass of virtual class");
         }
 
-        RubyClass newClass = new RubyClass(runtime, classClass, this, parentCRef, name);
+        RubyClass newClass = new RubyClass(runtime, classClass, this, getAllocator(), parentCRef, name);
 
         newClass.makeMetaClass(getMetaClass(), newClass.getCRef());
         newClass.inheritedBy(this);
 
         if(null != name) {
             ((RubyModule)parentCRef.getValue()).setConstant(name, newClass);
         }
 
         return newClass;
     }
     
     protected IRubyObject doClone() {
     	return RubyClass.newClass(getRuntime(), getSuperClass(), null/*FIXME*/, getBaseName());
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 9023b04cc8..18f24fd410 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1244 +1,1245 @@
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
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.Iter;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
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
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
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
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return ((RubyString) callMethod(getRuntime().getCurrentContext(), "to_s")).toString();
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
 
     /** rb_define_singleton_method
      *
      */
     public void defineFastSingletonMethod(String name, Callback method) {
         getSingletonClass().defineFastMethod(name, method);
     }
 
     public void addSingletonMethod(String name, DynamicMethod method) {
         getSingletonClass().addMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
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
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = null;
 
         method = rubyclass.searchMethod(name);
 
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs);
         }
 
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = rubyclass.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
 
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         IRubyObject result = method.call(context, this, implementer, name, args, false);
 
         return result;
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY);
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
 
     	if (!varName.startsWith("@")) {
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
 
     	if (!varName.startsWith("@")) {
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
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this);
     }
 
     public void callInit(IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         tc.setIfBlockAvailable();
         try {
             callMethod(getRuntime().getCurrentContext(), "initialize", args);
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
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
     private String trueFalseNil(String v) {
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
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
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
                     return context.getFrameBlockOrRaise().yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
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
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf);
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
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
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         // no binding, just eval in "current" frame (caller's frame)
         Iter iter = threadContext.getFrameIter();
         IRubyObject result = getRuntime().getNil();
 
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, threadContext.getCurrentScope()), this);
         } finally {
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
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
     	return (int) RubyNumeric.fix2long(callMethod(getRuntime().getCurrentContext(), "hash"));
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
-    	return getMetaClass().getRealClass().allocate();
+        RubyClass realClass = getMetaClass().getRealClass();
+    	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod(getRuntime().getCurrentContext(), "clone");
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
                     if(name.startsWith("@")) {
                         part.append(" ");
                         part.append(sep);
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
             if (!Character.isUpperCase(name.charAt(0))) {
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
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
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
         String description = callMethod(getRuntime().getCurrentContext(), "inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description,
             noClass ? "" : ":", noClass ? "" : getType().getName()});
 
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
             return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL);
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
 }
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index 36b30a2ab9..fdf89fa334 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,450 +1,451 @@
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
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 
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
     public RubyStruct(IRuby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public static RubyClass createStructClass(IRuby runtime) {
         RubyClass structClass = runtime.defineClass("Struct", runtime.getObject());
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
         structClass.includeModule(runtime.getModule("Enumerable"));
 
         structClass.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
 
         structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));
 
         structClass.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
 
         structClass.defineFastMethod("to_s", callbackFactory.getMethod("to_s"));
         structClass.defineFastMethod("inspect", callbackFactory.getMethod("inspect"));
         structClass.defineFastMethod("to_a", callbackFactory.getMethod("to_a"));
         structClass.defineFastMethod("values", callbackFactory.getMethod("to_a"));
         structClass.defineFastMethod("size", callbackFactory.getMethod("size"));
         structClass.defineFastMethod("length", callbackFactory.getMethod("size"));
 
         structClass.defineMethod("each", callbackFactory.getMethod("each"));
         structClass.defineFastMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
         structClass.defineFastMethod("[]=", callbackFactory.getMethod("aset", IRubyObject.class, IRubyObject.class));
 
         structClass.defineFastMethod("members", callbackFactory.getMethod("members"));
 
         return structClass;
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
 
     private IRubyObject setByName(String name, IRubyObject value) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0; i < member.getLength(); i++) {
             if (member.entry(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0; i < member.getLength(); i++) {
             if (member.entry(i).asSymbol().equals(name)) {
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
     public static RubyClass newInstance(IRubyObject recv, IRubyObject[] args) {
         String name = null;
 
         if (args.length > 0 && args[0] instanceof RubyString) {
             name = args[0].toString();
         }
 
         RubyArray member = recv.getRuntime().newArray();
 
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             member.append(RubySymbol.newSymbol(recv.getRuntime(), args[i].asSymbol()));
         }
 
         RubyClass newStruct;
+        RubyClass superClass = (RubyClass)recv;
 
         if (name == null) {
-            newStruct = new RubyClass((RubyClass) recv);
+            newStruct = new RubyClass(superClass, superClass.getAllocator());
         } else {
             if (!IdUtil.isConstant(name)) {
                 throw recv.getRuntime().newNameError("identifier " + name + " needs to be constant", name);
             }
             newStruct = ((RubyClass) recv).defineClassUnder(name, (RubyClass) recv);
         }
 
         newStruct.setInstanceVariable("__size__", member.length());
         newStruct.setInstanceVariable("__member__", member);
 
         CallbackFactory callbackFactory = recv.getRuntime().callbackFactory(RubyStruct.class);
         newStruct.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.defineSingletonMethod("[]", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.defineSingletonMethod("members", callbackFactory.getSingletonMethod("members"));
 
         // define access methods.
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             String memberName = args[i].asSymbol();
             newStruct.defineMethod(memberName, callbackFactory.getMethod("get"));
             newStruct.defineMethod(memberName + "=", callbackFactory.getMethod("set", IRubyObject.class));
         }
         
         ThreadContext context = recv.getRuntime().getCurrentContext();
         if (context.isBlockGiven()) {
             context.getFrameBlock().yield(context, null, newStruct, newStruct, false);
         }
 
         return newStruct;
     }
 
     /** Create new Structure.
      *
      * MRI: struct_alloc
      *
      */
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject[] args) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         int size = RubyNumeric.fix2int(getInstanceVariable((RubyClass) recv, "__size__"));
 
         struct.values = new IRubyObject[size];
 
         struct.callInit(args);
 
         return struct;
     }
 
     public IRubyObject initialize(IRubyObject[] args) {
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
 
     public static RubyArray members(IRubyObject recv) {
         RubyArray member = (RubyArray) getInstanceVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0; i < member.getLength(); i++) {
             result.append(recv.getRuntime().newString(member.entry(i).asSymbol()));
         }
 
         return result;
     }
 
     public RubyArray members() {
         return members(classOf());
     }
 
     public IRubyObject set(IRubyObject value) {
         String name = getRuntime().getCurrentContext().getFrameLastFunc();
         if (name.endsWith("=")) {
             name = name.substring(0, name.length() - 1);
         }
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0; i < member.getLength(); i++) {
             if (member.entry(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private RaiseException notStructMemberError(String name) {
         return getRuntime().newNameError(name + " is not struct member", name);
     }
 
     public IRubyObject get() {
         String name = getRuntime().getCurrentContext().getFrameLastFunc();
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0; i < member.getLength(); i++) {
             if (member.entry(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     public IRubyObject rbClone() {
         RubyStruct clone = new RubyStruct(getRuntime(), getMetaClass());
 
         clone.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, clone.values, 0, values.length);
 
         clone.setFrozen(this.isFrozen());
         clone.setTaint(this.isTaint());
 
         return clone;
     }
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other) {
             return getRuntime().getTrue();
         } else if (!(other instanceof RubyStruct)) {
             return getRuntime().getFalse();
         } else if (getMetaClass() != other.getMetaClass()) {
             return getRuntime().getFalse();
         } else {
             for (int i = 0; i < values.length; i++) {
                 if (!values[i].equals(((RubyStruct) other).values[i])) {
                     return getRuntime().getFalse();
                 }
             }
             return getRuntime().getTrue();
         }
     }
 
     public IRubyObject to_s() {
         return inspect();
     }
 
     public IRubyObject inspect() {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         StringBuffer sb = new StringBuffer(100);
 
         sb.append("#<struct ").append(getMetaClass().getName()).append(' ');
 
         for (int i = 0; i < member.getLength(); i++) {
             if (i > 0) {
                 sb.append(", ");
             }
 
             sb.append(member.entry(i).asSymbol()).append("=");
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
 
     public IRubyObject each() {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             context.yield(values[i]);
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
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('S');
 
         String className = getMetaClass().getName();
         if (className == null) {
             throw getRuntime().newArgumentError("can't dump anonymous class");
         }
         output.dumpObject(RubySymbol.newSymbol(getRuntime(), className));
 
         List members = ((RubyArray) getInstanceVariable(classOf(), "__member__")).getList();
         output.dumpInt(members.size());
 
         for (int i = 0; i < members.size(); i++) {
             RubySymbol name = (RubySymbol) members.get(i);
             output.dumpObject(name);
             output.dumpObject(values[i]);
         }
     }
 
     public static RubyStruct unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         IRuby runtime = input.getRuntime();
 
         RubySymbol className = (RubySymbol) input.unmarshalObject();
         RubyClass rbClass = pathToClass(runtime, className.asSymbol());
         if (rbClass == null) {
             throw runtime.newNameError("uninitialized constant " + className, className.asSymbol());
         }
 
         int size = input.unmarshalInt();
 
         IRubyObject[] values = new IRubyObject[size];
         for (int i = 0; i < size; i++) {
             input.unmarshalObject(); // Read and discard a Symbol, which is the name
             values[i] = input.unmarshalObject();
         }
 
         RubyStruct result = newStruct(rbClass, values);
         input.registerLinkTarget(result);
         return result;
     }
 
     private static RubyClass pathToClass(IRuby runtime, String path) {
         // FIXME: Throw the right ArgumentError's if the class is missing
         // or if it's a module.
         return (RubyClass) runtime.getClassFromPath(path);
     }
 }
diff --git a/src/org/jruby/libraries/RubySocket.java b/src/org/jruby/libraries/RubySocket.java
index a660c7ea29..db1f06f9d2 100644
--- a/src/org/jruby/libraries/RubySocket.java
+++ b/src/org/jruby/libraries/RubySocket.java
@@ -1,32 +1,33 @@
 package org.jruby.libraries;
 
 import org.jruby.IRuby;
+import org.jruby.RubyBasicSocket;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.internal.runtime.methods.DirectInvocationMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
-public class RubySocket extends RubyObject {
+public class RubySocket extends RubyBasicSocket {
     public static abstract class SocketMethod extends DirectInvocationMethod {
         public SocketMethod(RubyModule implementationClass, Arity arity, Visibility visibility) {
             super(implementationClass, arity, visibility);
         }
         
         public IRubyObject internalCall(ThreadContext context, IRubyObject receiver, RubyModule lastClass, String name, IRubyObject[] args, boolean noSuper) {
             RubySocket s = (RubySocket)receiver;
             
             return invoke(s, args);
         }
         
         public abstract IRubyObject invoke(RubySocket target, IRubyObject[] args);
     };
 
 	public RubySocket(IRuby runtime, RubyClass metaClass) {
 		super(runtime, metaClass);
 	}
 
 }
diff --git a/src/org/jruby/libraries/SocketMetaClass.java b/src/org/jruby/libraries/SocketMetaClass.java
index e660bdbb4f..0a005d5322 100644
--- a/src/org/jruby/libraries/SocketMetaClass.java
+++ b/src/org/jruby/libraries/SocketMetaClass.java
@@ -1,66 +1,78 @@
 package org.jruby.libraries;
 
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
+import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyString;
 import org.jruby.libraries.RubySocket.SocketMethod;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.BasicSocketMetaClass;
 
 public class SocketMetaClass extends BasicSocketMetaClass {
 
 	public SocketMetaClass(IRuby runtime) {
-		super("Socket", RubySocket.class, runtime.getClass("BasicSocket"));
+		super("Socket", RubySocket.class, runtime.getClass("BasicSocket"), SOCKET_ALLOCATOR);
 	}
 	
     public SocketMethod gethostname = new SocketMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubySocket self, IRubyObject[] args) {
         	return self.getRuntime().getNil();
         }
     };
     
     protected class SocketMeta extends Meta {
 	    protected void initializeClass() {
 	    	defineSingletonMethod("gethostname", Arity.noArguments());
 	    	defineSingletonMethod("gethostbyname", Arity.singleArgument());
 	    }
     }
     
     public RubyString gethostname() {
     	try {
 			String hostName = InetAddress.getLocalHost().getHostName();
 			return getRuntime().newString(hostName);
 		} catch (UnknownHostException e) {
 			// DSC throw SystemError("gethostname");
 			return getRuntime().newString("");
 		}
     }
     
     public RubyArray gethostbyname(IRubyObject hostname) {
 		try {
 			RubyString name = (RubyString) hostname;
 			InetAddress inetAddress = InetAddress.getByName(name.toString());
 			List parts = new ArrayList();
 			parts.add(getRuntime().newString(inetAddress.getCanonicalHostName()));
 			parts.add(RubyArray.newArray(getRuntime()));
 			parts.add(new RubyFixnum(getRuntime(),2));
 			parts.add(getRuntime().newString(RubyString.bytesToString(inetAddress.getAddress())));
 			return RubyArray.newArray(getRuntime(), parts);
 		} catch (UnknownHostException e) {
 			// DSC throw SystemError("gethostbyname");
 			return RubyArray.newArray(getRuntime());
 		}
     }
 
+    private static ObjectAllocator SOCKET_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubySocket instance = new RubySocket(runtime, klass);
+
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
+
     protected Meta getMeta() {
     	return new SocketMeta();
     }
 }
diff --git a/src/org/jruby/runtime/ObjectAllocator.java b/src/org/jruby/runtime/ObjectAllocator.java
new file mode 100644
index 0000000000..42d91a558d
--- /dev/null
+++ b/src/org/jruby/runtime/ObjectAllocator.java
@@ -0,0 +1,28 @@
+/*
+ * ObjectAllocator.java
+ *
+ * Created on January 6, 2007, 1:35 AM
+ *
+ * To change this template, choose Tools | Template Manager
+ * and open the template in the editor.
+ */
+
+package org.jruby.runtime;
+
+import org.jruby.IRuby;
+import org.jruby.RubyClass;
+import org.jruby.runtime.builtin.IRubyObject;
+
+/**
+ *
+ * @author headius
+ */
+public interface ObjectAllocator {
+    public IRubyObject allocate(IRuby runtime, RubyClass klazz);
+    
+    public static final ObjectAllocator NOT_ALLOCATABLE_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            throw new RuntimeException("Ruby \"" + klass.getName() + "\" object can not be allocated");
+        }
+    };
+}
diff --git a/src/org/jruby/runtime/builtin/meta/AbstractMetaClass.java b/src/org/jruby/runtime/builtin/meta/AbstractMetaClass.java
index 01353bb7a0..07d649b3a8 100644
--- a/src/org/jruby/runtime/builtin/meta/AbstractMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/AbstractMetaClass.java
@@ -1,343 +1,344 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.SimpleReflectedMethod;
 import org.jruby.internal.runtime.methods.FullFunctionReflectedMethod;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * <p>
  * The main meta class for all other meta classes.
  * </p>
  */
 public abstract class AbstractMetaClass extends RubyClass {
 	protected abstract class Meta {
         protected abstract void initializeClass();
         
 		/**
 		 * Base implementation uses the data-driven approach not used currently but
 		 * possibly revisited in the future.
 		 */
 //		protected void initializeClass() {
 //			includeModules(getIncludedModules());
 //
 //			defineMethods(Visibility.PUBLIC, getSingletonMethods(), true);
 //			defineMethods(Visibility.PUBLIC, getPublicMethods(), false);
 //			defineMethods(Visibility.PRIVATE, getPrivateMethods(), false);
 //
 //			defineAliases(getAliases());
 //
 //			undefineMethods(getUndefineMethods(), false);
 //			undefineMethods(getUndefineSingletonMethods(), true);
 //
 //			defineConstants(getDefineConstants(), false);
 //			setConstants(getSetConstants(), false);
 //		}
 
 		// Empty impls
 		protected Object[][] getPublicMethods() {
 			return new Object[][] {};
 		}
 
 		protected Object[][] getDefineConstants() {
 			return new Object[][] {};
 		}
 
 		protected Object[][] getSetConstants() {
 			return new Object[][] {};
 		}
 
 		protected Object[][] getSingletonMethods() {
 			return new Object[][] {};
 		}
 
 		protected String[][] getAliases() {
 			return new String[][] {};
 		}
 
 		protected Object[][] getPrivateMethods() {
 			return new Object[][] {};
 		}
 
 		protected String[] getIncludedModules() {
 			return new String[] {};
 		}
 
 		protected String[] getUndefineMethods() {
 			return new String[] {};
 		}
 
 		protected String[] getUndefineSingletonMethods() {
 			return new String[] {};
 		}
 
 //		public void defineMethods(Visibility visibility, Object[][] methods,
 //				boolean singleton) {
 //			for (int i = 0; i < methods.length; i++) {
 //				String name = (String) methods[i][0];
 //				Arity arity = (Arity) methods[i][1];
 //				String java_name = null;
 //				switch (methods[i].length) {
 //				case 2:
 //					java_name = (String) methods[i][0];
 //					break;
 //				case 3:
 //					java_name = (String) methods[i][2];
 //					break;
 //				}
 //
 //				assert name != null;
 //				assert arity != null;
 //				assert java_name != null;
 //
 //				visibility = name.equals("initialize") ? Visibility.PRIVATE
 //						: Visibility.PUBLIC;
 //
 //				if (singleton) {
 //					getSingletonClass().addMethod(
 //							name,
 //							new ReflectedMethod(AbstractMetaClass.this, AbstractMetaClass.this
 //									.getClass(), java_name, arity, visibility));
 //				} else {
 //					addMethod(name, new ReflectedMethod(AbstractMetaClass.this,
 //							builtinClass, java_name, arity, visibility));
 //				}
 //			}
 //		}
 
 		public void undefineMethods(String[] undefineMethods, boolean singleton) {
 			for (int i = 0; i < undefineMethods.length; i++) {
 				if (singleton) {
 					getSingletonClass().undefineMethod(undefineMethods[i]);
 				} else {
 					undefineMethod(undefineMethods[i]);
 				}
 			}
 		}
 
 		public void defineConstants(Object[][] constants, boolean singleton) {
 			for (int i = 0; i < constants.length; i++) {
 				if (singleton) {
 					getSingletonClass().defineConstant(
 							(String) constants[i][0],
 							(IRubyObject) constants[i][1]);
 				} else {
 					defineConstant((String) constants[i][0],
 							(IRubyObject) constants[i][1]);
 				}
 			}
 		}
 
 		public void setConstants(Object[][] constants, boolean singleton) {
 			for (int i = 0; i < constants.length; i++) {
 				if (singleton) {
 					getSingletonClass().setConstant((String) constants[i][0],
 							(IRubyObject) constants[i][1]);
 				} else {
 					setConstant((String) constants[i][0],
 							(IRubyObject) constants[i][1]);
 				}
 			}
 		}
 
 		public void includeModules(String[] includedModules) {
 			for (int i = 0; i < includedModules.length; i++) {
 				includeModule(getRuntime().getModule(includedModules[i]));
 			}
 		}
 
 		public void defineAliases(Object[][] aliases) {
 			for (int i = 0; i < aliases.length; i++) {
 				defineAlias((String) aliases[i][0], (String) aliases[i][1]);
 			}
 		}
 	};
 
 	protected Meta getMeta() {
 		return null;
 	}
 
 	protected Class builtinClass;
     protected MethodFactory mfactory = MethodFactory.createFactory();
 
 	// Only for other core modules/classes
 	protected AbstractMetaClass(IRuby runtime, RubyClass metaClass,
-			RubyClass superClass, SinglyLinkedList parentCRef, String name,
+			RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name,
 			Class builtinClass) {
-		super(runtime, metaClass, superClass, parentCRef, name);
+		super(runtime, metaClass, superClass, allocator, parentCRef, name);
 
 		this.builtinClass = builtinClass;
 	}
 
-	protected AbstractMetaClass(String name, Class builtinClass, RubyClass superClass) {
-		this(name, builtinClass, superClass, superClass.getRuntime().getClass(
+	protected AbstractMetaClass(String name, Class builtinClass, RubyClass superClass, ObjectAllocator allocator) {
+		this(name, builtinClass, superClass, allocator, superClass.getRuntime().getClass(
 				"Object").getCRef(), true);
 	}
 
 	protected AbstractMetaClass(String name, Class builtinClass, RubyClass superClass,
-            SinglyLinkedList parentCRef) {
-		this(name, builtinClass, superClass, parentCRef, false);
+            ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		this(name, builtinClass, superClass, allocator, parentCRef, false);
 	}
 
 	protected AbstractMetaClass(String name, Class builtinClass, RubyClass superClass,
-            SinglyLinkedList parentCRef, boolean init) {
+            ObjectAllocator allocator, SinglyLinkedList parentCRef, boolean init) {
 		super(superClass.getRuntime(), superClass.getRuntime()
-				.getClass("Class"), superClass, parentCRef, name);
+				.getClass("Class"), superClass, allocator, parentCRef, name);
 
 		assert name != null;
 		assert builtinClass != null;
 		//assert RubyObject.class.isAssignableFrom(builtinClass) || RubyObject.class == builtinClass: "builtinClass have to be a subclass of RubyObject.";
 		assert superClass != null;
 
 		this.builtinClass = builtinClass;
 
 		makeMetaClass(superClass.getMetaClass(), superClass.getRuntime()
 				.getCurrentContext().peekCRef());
 		inheritedBy(superClass);
 
                 if(name != null) {
                     ((RubyModule)parentCRef.getValue()).setConstant(name, this);
                 }
 
 		if (init) {
 			getMeta().initializeClass();
 		}
 	}
 
 	public AbstractMetaClass(IRuby runtime, RubyClass metaClass, RubyClass superClass,
-            SinglyLinkedList parentCRef, String name) {
-		super(runtime, metaClass, superClass, parentCRef, name);
+            ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
+		super(runtime, metaClass, superClass, allocator, parentCRef, name);
 	}
 
 	public void defineMethod(String name, Arity arity) {
 		defineMethod(name, arity, name);
 	}
 
 	public void defineMethod(String name, Arity arity, String java_name) {
 		assert name != null;
 		assert arity != null;
 		assert java_name != null;
 
 		Visibility visibility = name.equals("initialize") ? Visibility.PRIVATE
 				: Visibility.PUBLIC;
 
 		addMethod(name, mfactory.getFullMethod(this, builtinClass, java_name,
 				arity, visibility));
 	}
 
 	public void definePrivateMethod(String name, Arity arity) {
 		addMethod(name, mfactory.getFullMethod(this, builtinClass, name, arity,
 				Visibility.PRIVATE));
 	}
 
 	public void definePrivateMethod(String name, Arity arity, String java_name) {
 		addMethod(name, mfactory.getFullMethod(this, builtinClass, java_name,
 				arity, Visibility.PRIVATE));
 	}
 
 	public void defineFastMethod(String name, Arity arity) {
 		defineFastMethod(name, arity, name);
 	}
 
 	public void defineFastMethod(String name, Arity arity, String java_name) {
 		assert name != null;
 		assert arity != null;
 		assert java_name != null;
 
 		Visibility visibility = name.equals("initialize") ? Visibility.PRIVATE
 				: Visibility.PUBLIC;
 
 		addMethod(name, mfactory.getSimpleMethod(this, builtinClass, java_name,
 				arity, visibility));
 	}
 
 	public void defineFastPrivateMethod(String name, Arity arity) {
 		addMethod(name, mfactory.getSimpleMethod(this, builtinClass, name, arity,
 				Visibility.PRIVATE));
 	}
 
 	public void defineFastPrivateMethod(String name, Arity arity, String java_name) {
 		addMethod(name, mfactory.getSimpleMethod(this, builtinClass, java_name,
 				arity, Visibility.PRIVATE));
 	}
 
 	public void defineSingletonMethod(String name, Arity arity) {
 		defineSingletonMethod(name, arity, name);
 	}
 
 	public void defineSingletonMethod(String name, Arity arity, String java_name) {
 		assert name != null;
 		assert arity != null;
 		assert java_name != null;
 
 		Visibility visibility = name.equals("initialize") ? Visibility.PRIVATE
 				: Visibility.PUBLIC;
 
 		getSingletonClass().addMethod(
 				name,
 				mfactory.getFullMethod(this, getClass(), java_name, arity,
 						visibility));
 	}
 
 	public void defineFastSingletonMethod(String name, Arity arity) {
 		defineSingletonMethod(name, arity, name);
 	}
 
 	public void defineFastSingletonMethod(String name, Arity arity, String java_name) {
 		assert name != null;
 		assert arity != null;
 		assert java_name != null;
 
 		Visibility visibility = name.equals("initialize") ? Visibility.PRIVATE
 				: Visibility.PUBLIC;
 
 		getSingletonClass().addMethod(
 				name,
 				mfactory.getSimpleMethod(this, getClass(), java_name, arity,
 						visibility));
 	}
 
 	/**
 	 * Only intended to be called by ModuleMetaClass and ClassMetaClass. Seems
 	 * like a waste to subclass over this and there seems little risk of
 	 * programmer error. We cannot define methods for there two classes since
 	 * there is a circular dependency between them and ObjectMetaClass. We defer
 	 * initialization until after construction and meta classes are made.
 	 */
 	public void initializeBootstrapClass() {
 		getMeta().initializeClass();
 	}
 }
diff --git a/src/org/jruby/runtime/builtin/meta/ArrayMetaClass.java b/src/org/jruby/runtime/builtin/meta/ArrayMetaClass.java
index 60e0775598..f8756315e6 100644
--- a/src/org/jruby/runtime/builtin/meta/ArrayMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/ArrayMetaClass.java
@@ -1,165 +1,160 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class ArrayMetaClass extends ObjectMetaClass {
     public ArrayMetaClass(IRuby runtime) {
-        super("Array", RubyArray.class, runtime.getObject());
+        super("Array", RubyArray.class, runtime.getObject(), ARRAY_ALLOCATOR);
     }
     
-	public ArrayMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyArray.class, superClass, parentCRef);
+	public ArrayMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyArray.class, superClass, allocator, parentCRef);
 	}
 
 	protected class ArrayMeta extends Meta {
 		protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Enumerable"));
 	
 	        defineFastMethod("+", Arity.singleArgument(), "op_plus");
 	        defineFastMethod("*", Arity.singleArgument(), "op_times");
 	        defineFastMethod("-", Arity.singleArgument(), "op_diff");
 	        defineFastMethod("&", Arity.singleArgument(), "op_and");
 	        defineFastMethod("|", Arity.singleArgument(), "op_or");
 	        defineFastMethod("[]", Arity.optional(), "aref");
 	        defineFastMethod("[]=", Arity.optional(), "aset");
 	        defineFastMethod("<=>", Arity.singleArgument(), "op_cmp");
 	        defineFastMethod("<<", Arity.singleArgument(), "append");
 	        defineFastMethod("==", Arity.singleArgument(), "array_op_equal");
 	        defineFastMethod("assoc", Arity.singleArgument());
 	        defineFastMethod("at", Arity.singleArgument(), "at");
 	        defineFastMethod("clear", Arity.noArguments(), "rb_clear");
 	        defineFastMethod("clone", Arity.noArguments(), "rbClone");
 	        defineMethod("collect", Arity.noArguments());
 	        defineMethod("collect!", Arity.noArguments(), "collect_bang");
 	        defineFastMethod("compact", Arity.noArguments());
 	        defineFastMethod("compact!", Arity.noArguments(), "compact_bang");
 	        defineFastMethod("concat", Arity.singleArgument());
 	        defineMethod("delete", Arity.singleArgument());
 	        defineFastMethod("delete_at", Arity.singleArgument());
 	        defineMethod("delete_if", Arity.noArguments());
 	        defineMethod("each", Arity.noArguments());
 	        defineMethod("each_index", Arity.noArguments());
 	        defineFastMethod("eql?", Arity.singleArgument(), "eql");
 	        defineFastMethod("empty?", Arity.noArguments(), "empty_p");
 	        defineMethod("fetch", Arity.optional());
 	        defineMethod("fill", Arity.optional());
 	        defineFastMethod("first", Arity.optional());
 	        defineFastMethod("flatten", Arity.noArguments());
 	        defineFastMethod("flatten!", Arity.noArguments(), "flatten_bang");
 	        defineFastMethod("frozen?", Arity.noArguments(), "frozen");
 	        defineFastMethod("hash", Arity.noArguments());
 	        defineFastMethod("include?", Arity.singleArgument(), "include_p");
 	        defineFastMethod("index", Arity.singleArgument());
 	        defineFastMethod("indices", Arity.optional());
             // FIXME: shouldn't this be private?
 	        defineMethod("initialize", Arity.optional());
 	        defineFastMethod("insert", Arity.optional());
 	        defineFastMethod("inspect", Arity.noArguments());
 	        defineFastMethod("join", Arity.optional());
 	        defineFastMethod("last", Arity.optional());
 	        defineFastMethod("length", Arity.noArguments());
 	        defineFastMethod("nitems", Arity.noArguments());
 	        defineFastMethod("pack", Arity.singleArgument());
 	        defineFastMethod("pop", Arity.noArguments());
 	        defineFastMethod("push", Arity.optional());
 	        defineFastMethod("rassoc", Arity.singleArgument());
 	        defineMethod("reject!", Arity.noArguments(), "reject_bang");
 	        defineFastMethod("replace", Arity.singleArgument(), "replace");
 	        defineFastMethod("reverse", Arity.noArguments());
 	        defineFastMethod("reverse!", Arity.noArguments(), "reverse_bang");
 	        defineMethod("reverse_each", Arity.noArguments());
 	        defineFastMethod("rindex", Arity.singleArgument());
 	        defineFastMethod("shift", Arity.noArguments());
 	        defineMethod("sort", Arity.noArguments());
 	        defineMethod("sort!", Arity.noArguments(), "sort_bang");
 	        defineFastMethod("slice", Arity.optional(), "aref");
 	        defineFastMethod("slice!", Arity.optional(), "slice_bang");
 	        defineFastMethod("to_a", Arity.noArguments());
 	        defineFastMethod("to_ary", Arity.noArguments());
 	        defineFastMethod("to_s", Arity.noArguments());
 	        defineFastMethod("transpose", Arity.noArguments());
 	        defineFastMethod("uniq", Arity.noArguments());
 	        defineFastMethod("uniq!", Arity.noArguments(), "uniq_bang");
 	        defineFastMethod("unshift", Arity.optional());
 	        defineFastMethod("values_at", Arity.optional());
 	        defineAlias("===", "==");
 	        defineAlias("size", "length");
 	        defineAlias("indexes", "indices");
 	        defineAlias("filter", "collect!");
 	        defineAlias("map!", "collect!");
 	
 	        defineSingletonMethod("new", Arity.optional(), "newInstance");
 	        defineSingletonMethod("[]", Arity.optional(), "create");
 		}
 	};
 
 	protected Meta getMeta() {
 		return new ArrayMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new ArrayMetaClass(name, this, parentCRef);
+		return new ArrayMetaClass(name, this, ARRAY_ALLOCATOR, parentCRef);
 	}
+    
+    private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            // FIXME: not sure how much I like this call back to the runtime...
+            RubyArray instance = runtime.newArray();
 
-	protected IRubyObject allocateObject() {
-        RubyArray instance = getRuntime().newArray();
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+            instance.setMetaClass(klass);
 
-    public IRubyObject newInstance(IRubyObject[] args) {
-        RubyArray instance = (RubyArray)allocateObject();
-        
-        instance.setMetaClass(this);
-        instance.callInit(args);
-       
-        return instance;
-    }
+            return instance;
+        }
+    };
     
     public IRubyObject create(IRubyObject[] args) {
-        RubyArray array = (RubyArray)allocateObject();
-        array.setMetaClass(this);
+        // FIXME: Why is this calling allocate directly instead of the normal newInstance process? Performance?
+        RubyArray array = (RubyArray)ARRAY_ALLOCATOR.allocate(getRuntime(), this);
         
         if (args.length >= 1) {
             for (int i = 0; i < args.length; i++) {
                 array.add(args[i]);
             }
         }
         
         return array;
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/BasicSocketMetaClass.java b/src/org/jruby/runtime/builtin/meta/BasicSocketMetaClass.java
index 216682d147..fed1edf27f 100644
--- a/src/org/jruby/runtime/builtin/meta/BasicSocketMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/BasicSocketMetaClass.java
@@ -1,100 +1,107 @@
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
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBasicSocket;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyBoolean;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.Arity;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class BasicSocketMetaClass extends IOMetaClass {
 
     public BasicSocketMetaClass(IRuby runtime) {
-        super("BasicSocket", RubyBasicSocket.class, runtime.getClass("IO")); 
+        super("BasicSocket", RubyBasicSocket.class, runtime.getClass("IO"), BASICSOCKET_ALLOCATOR); 
     }
 
-    public BasicSocketMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        this(name, RubyBasicSocket.class, superClass, parentCRef);
+    public BasicSocketMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        this(name, RubyBasicSocket.class, superClass, allocator, parentCRef);
     }
     
-    public BasicSocketMetaClass(String name, Class clazz, RubyClass superClass) {
-    	super(name, clazz, superClass);
+    public BasicSocketMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
+    	super(name, clazz, superClass, allocator);
     }
     
-    public BasicSocketMetaClass(String name, Class clazz, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, clazz, superClass, parentCRef);
+    public BasicSocketMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, clazz, superClass, allocator, parentCRef);
     }
 
     protected class BasicSocketMeta extends Meta {
     	protected void initializeClass() {
             // FIXME: shouldn't this be private?
             defineMethod("initialize", Arity.singleArgument());
             defineMethod("send", Arity.optional(), "write_send");
             defineMethod("recv", Arity.optional());
             defineMethod("shutdown", Arity.optional());
             defineMethod("__getsockname", Arity.noArguments(), "getsockname");
             defineMethod("__getpeername", Arity.noArguments(), "getpeername");
             defineSingletonMethod("do_not_reverse_lookup", Arity.noArguments());
             defineSingletonMethod("do_not_reverse_lookup=", Arity.singleArgument(), "set_do_not_reverse_lookup");
     	}
     };
 
     public IRubyObject do_not_reverse_lookup() {
         return getRuntime().newBoolean(getRuntime().isDoNotReverseLookupEnabled());
     }
     
     public IRubyObject set_do_not_reverse_lookup(IRubyObject flag) {
         getRuntime().setDoNotReverseLookupEnabled(((RubyBoolean) flag).isTrue());
         return getRuntime().newBoolean(getRuntime().isDoNotReverseLookupEnabled());
     }
     
     protected Meta getMeta() {
     	return new BasicSocketMeta();
     }
 
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new BasicSocketMetaClass(name, this, parentCRef);
+        return new BasicSocketMetaClass(name, this, BASICSOCKET_ALLOCATOR, parentCRef);
     }
 
     public RubyClass newSubClass(String name, RubyModule parent) {
 		BasicSocketMetaClass basicSocketMetaClass = new BasicSocketMetaClass(getRuntime());
         basicSocketMetaClass.initializeClass();
         
         return basicSocketMetaClass;
     }
-    
-    public IRubyObject allocateObject() {
-        return new RubyBasicSocket(getRuntime(), this); 
-    }
+
+    private static ObjectAllocator BASICSOCKET_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyBasicSocket instance = new RubyBasicSocket(runtime, klass);
+
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/BignumMetaClass.java b/src/org/jruby/runtime/builtin/meta/BignumMetaClass.java
index f58e5c4a38..32ca5dc1e6 100644
--- a/src/org/jruby/runtime/builtin/meta/BignumMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/BignumMetaClass.java
@@ -1,80 +1,93 @@
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
 package org.jruby.runtime.builtin.meta;
 
+import java.math.BigInteger;
 import org.jruby.IRuby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
+import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class BignumMetaClass extends ObjectMetaClass {
     public BignumMetaClass(IRuby runtime) {
-        super("Bignum", RubyBignum.class, runtime.getClass("Integer"));
+        super("Bignum", RubyBignum.class, runtime.getClass("Integer"), BIGNUM_ALLOCATOR);
     }
     
-	public BignumMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyBignum.class, superClass, parentCRef);
+	public BignumMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyBignum.class, superClass, allocator, parentCRef);
 	}
 
 	protected class BignumMeta extends Meta {
 		protected void initializeClass() {
 	        defineFastMethod("~", Arity.noArguments(), "op_invert");
 	        defineFastMethod("&", Arity.singleArgument(), "op_and");
 	        defineFastMethod("<<", Arity.singleArgument(), "op_lshift");
 	        defineFastMethod("%", Arity.singleArgument(), "op_mod");
 	        defineFastMethod("+", Arity.singleArgument(), "op_plus");
 	        defineFastMethod("*", Arity.singleArgument(), "op_mul");
 	        defineFastMethod("**", Arity.singleArgument(), "op_pow");
 	        defineFastMethod("-", Arity.singleArgument(), "op_minus");
 	        defineFastMethod("modulo", Arity.singleArgument(), "op_mod");
 	        defineFastMethod("/", Arity.singleArgument(), "op_div");
 	        defineFastMethod(">>", Arity.singleArgument(), "op_rshift");
 	        defineFastMethod("|", Arity.singleArgument(), "op_or");
 	        defineFastMethod("^", Arity.singleArgument(), "op_xor");
 	        defineFastMethod("-@", Arity.noArguments(), "op_uminus");
 	        defineFastMethod("[]", Arity.singleArgument(), "aref");
 	        defineFastMethod("coerce", Arity.singleArgument(), "coerce");
 	        defineFastMethod("remainder", Arity.singleArgument(), "remainder");
 	        defineFastMethod("hash", Arity.noArguments(), "hash");
 	        defineFastMethod("size", Arity.noArguments(), "size");
 	        defineFastMethod("quo", Arity.singleArgument(), "quo");
 	        defineFastMethod("to_f", Arity.noArguments(), "to_f");
 	        defineFastMethod("to_i", Arity.noArguments(), "to_i");
 	        defineFastMethod("to_s", Arity.optional(), "to_s");
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new BignumMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new BignumMetaClass(name, this, parentCRef);
+		return new BignumMetaClass(name, this, BIGNUM_ALLOCATOR, parentCRef);
 	}
+    
+    public static ObjectAllocator BIGNUM_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyBignum instance = RubyBignum.newBignum(runtime, BigInteger.ZERO);
+            
+            instance.setMetaClass(klass);
+            
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/BindingMetaClass.java b/src/org/jruby/runtime/builtin/meta/BindingMetaClass.java
index 265be8e7a9..8164e651bc 100644
--- a/src/org/jruby/runtime/builtin/meta/BindingMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/BindingMetaClass.java
@@ -1,73 +1,68 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBinding;
 import org.jruby.RubyClass;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class BindingMetaClass extends ObjectMetaClass {
     public BindingMetaClass(IRuby runtime) {
-        super("Binding", RubyBinding.class, runtime.getObject());
+        super("Binding", RubyBinding.class, runtime.getObject(), BINDING_ALLOCATOR);
     }
     
-	public BindingMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyBinding.class, superClass, parentCRef);
+	public BindingMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyBinding.class, superClass, allocator, parentCRef);
 	}
 
 	protected class BindingMeta extends Meta {
 		protected void initializeClass() {
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new BindingMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new BindingMetaClass(name, this, parentCRef);
+		return new BindingMetaClass(name, this, BINDING_ALLOCATOR, parentCRef);
 	}
 
-	protected IRubyObject allocateObject() {
-        RubyBinding instance = getRuntime().newBinding();
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+    private static ObjectAllocator BINDING_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyBinding instance = runtime.newBinding();
 
-    public IRubyObject newInstance(IRubyObject[] args) {
-        RubyBinding instance = RubyBinding.newBinding(getRuntime());
-        
-        instance.callInit(args);
-       
-        return instance;
-    }
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/ClassMetaClass.java b/src/org/jruby/runtime/builtin/meta/ClassMetaClass.java
deleted file mode 100644
index bcad3aa54b..0000000000
--- a/src/org/jruby/runtime/builtin/meta/ClassMetaClass.java
+++ /dev/null
@@ -1,93 +0,0 @@
-/*
- * Created on Jun 20, 2005
- *
- * To change the template for this generated file go to
- * Window - Preferences - Java - Code Generation - Code and Comments
- */
-package org.jruby.runtime.builtin.meta;
-
-import org.jruby.IRuby;
-import org.jruby.RubyClass;
-import org.jruby.runtime.Arity;
-import org.jruby.util.collections.SinglyLinkedList;
-
-// Note: This code is not currently live.  It will be hooked up
-// some time around 0.8.3 development cycle.
-public class ClassMetaClass extends ObjectMetaClass {
-    public ClassMetaClass(IRuby runtime, RubyClass superClass) {
-    	super(runtime, null, superClass, runtime.getObject().getCRef(), "Class", RubyClass.class);
-    }
-
-	public ClassMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyClass.class, superClass, parentCRef);
-	}
-	
-	protected class ClassMeta extends Meta {
-		public void initializeClass() {
-	        defineMethod("new", Arity.optional(), "newInstance");
-	        defineMethod("superclass", Arity.noArguments(), "superclass");
-	
-	        defineFastSingletonMethod("new", Arity.optional(), "newClass");
-	        defineSingletonMethod("inherited", Arity.singleArgument());
-	        
-	        undefineMethod("module_function");
-		}
-	};
-	
-	protected Meta getMeta() {
-		return new ClassMeta();
-	}
-	
-	/*
-	public RubyClass newSubClass(String name, RubyModule parentModule) {
-		return new ClassMetaClass(name, this, parentModule);
-	}
-
-	protected IRubyObject allocateObject() {
-        RubyClass instance = (RubyClass) newClass(IRubyObject.NULL_ARRAY);
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
-
-    public IRubyObject newInstance(IRubyObject[] args) {
-        return newClass(IRubyObject.NULL_ARRAY);
-    }
-    
-	public IRubyObject newClass(IRubyObject[] args) {
-	    Ruby runtime = getRuntime();
-
-        RubyClass superClass;
-        if (args.length > 0) {
-            if (args[0] instanceof RubyClass) {
-                superClass = (RubyClass) args[0];
-            } else {
-                throw runtime.newTypeError(
-                    "wrong argument type " + args[0].getType().getName() + " (expected Class)");
-            }
-        } else {
-        	 superClass = runtime.getClasses().getObjectClass();
-        }
-
-        RubyClass newClass = superClass.subclass();
-
-        newClass.makeMetaClass(superClass.getMetaClass(), runtime.getCurrentContext().getLastRubyClass());
-
-        // call "initialize" method
-        newClass.callInit(args);
-
-        // call "inherited" method of the superclass
-        newClass.inheritedBy(superClass);
-
-        if (runtime.getCurrentContext().isBlockGiven()) {
-            runtime.yield(null, newClass, newClass, false);
-        }
-
-        return newClass;
-    }
-    
-    public IRubyObject inherited(IRubyObject ignore) {
-    	return getRuntime().getNil();
-    }*/
-}
diff --git a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
index db7b94ae3d..9375c56550 100644
--- a/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FileMetaClass.java
@@ -1,675 +1,682 @@
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
+import org.jruby.runtime.ObjectAllocator;
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
-        super("File", RubyFile.class, runtime.getClass("IO"));
+        super("File", RubyFile.class, runtime.getClass("IO"), FILE_ALLOCATOR);
     }
 
-    public FileMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, RubyFile.class, superClass, parentCRef);
+    public FileMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, RubyFile.class, superClass, allocator, parentCRef);
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
-		return new FileMetaClass(name, this, parentCRef);
-	}
-
-	public IRubyObject allocateObject() {
-		return new RubyFile(getRuntime(), this);
+		return new FileMetaClass(name, this, FILE_ALLOCATOR, parentCRef);
 	}
+    
+    private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyFile instance = new RubyFile(runtime, klass);
+            
+            instance.setMetaClass(klass);
+            
+            return instance;
+        }
+    };
 	
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
     	
         return getRuntime().newRubyFileStat(JRubyFile.create(getRuntime().getCurrentDirectory(),name.toString()));
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
diff --git a/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java b/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
index 022edffd9f..67685f1bd2 100644
--- a/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
@@ -1,97 +1,106 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubySymbol;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class FixnumMetaClass extends IntegerMetaClass {
 	public FixnumMetaClass(IRuby runtime) {
-		super("Fixnum", RubyFixnum.class, runtime.getClass("Integer"));
+		super("Fixnum", RubyFixnum.class, runtime.getClass("Integer"), FIXNUM_ALLOCATOR);
 	}
 	
-	public FixnumMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyFixnum.class, superClass, parentCRef);
+	public FixnumMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyFixnum.class, superClass, allocator, parentCRef);
 	}
 	
 	protected class FixnumMeta extends Meta {
 		protected void initializeClass() {
 		        defineFastMethod("quo", Arity.singleArgument());
 		        defineFastMethod("to_f", Arity.noArguments());
 		        defineFastMethod("to_i", Arity.noArguments());
 		        defineFastMethod("to_s", Arity.optional());
 		        defineFastMethod("taint", Arity.noArguments());
 		        defineFastMethod("freeze", Arity.noArguments());
 		        defineFastMethod("<<", Arity.singleArgument(), "op_lshift");
 		        defineFastMethod(">>", Arity.singleArgument(), "op_rshift");
 		        defineFastMethod("+", Arity.singleArgument(), "op_plus");
 		        defineFastMethod("-", Arity.singleArgument(), "op_minus");
 		        defineFastMethod("*", Arity.singleArgument(), "op_mul");
 		        defineFastMethod("/", Arity.singleArgument(), "op_div");
 		        defineAlias("div", "/");
 		        defineFastMethod("%", Arity.singleArgument(), "op_mod");
 		        defineFastMethod("**", Arity.singleArgument(), "op_pow");
 		        defineFastMethod("&", Arity.singleArgument(), "op_and");
 		        defineFastMethod("|", Arity.singleArgument(), "op_or");
 		        defineFastMethod("^", Arity.singleArgument(), "op_xor");
 		        defineFastMethod("size", Arity.noArguments());
 		        defineFastMethod("[]", Arity.singleArgument(), "aref");
 		        defineFastMethod("hash", Arity.noArguments());
 		        defineFastMethod("id2name", Arity.noArguments());
 		        defineFastMethod("~", Arity.noArguments(), "invert");
 		        defineFastMethod("id", Arity.noArguments());
 	
 		        defineFastSingletonMethod("induced_from", Arity.singleArgument(), "induced_from");
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new FixnumMeta();
 	}
 
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new FixnumMetaClass(name, this, parentCRef);
+        return new FixnumMetaClass(name, this, FIXNUM_ALLOCATOR, parentCRef);
 	}
 
     public RubyInteger induced_from(IRubyObject number) {
     	// TODO: Remove once asNumeric in RubyObject tries to convert
         if (number instanceof RubySymbol) {
             return (RubyInteger) number.callMethod(getRuntime().getCurrentContext(), "to_i");
         } 
 
         return ((IntegerMetaClass) getRuntime().getClass("Integer")).induced_from(number);
     }
 
+    private static ObjectAllocator FIXNUM_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyFixnum instance = runtime.newFixnum(0);
 
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/HashMetaClass.java b/src/org/jruby/runtime/builtin/meta/HashMetaClass.java
index 0ad65378fe..45810ee16f 100644
--- a/src/org/jruby/runtime/builtin/meta/HashMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/HashMetaClass.java
@@ -1,129 +1,133 @@
 /*
  * Created on Jun 21, 2005
  *
  * To change the template for this generated file go to
  * Window - Preferences - Java - Code Generation - Code and Comments
  */
 package org.jruby.runtime.builtin.meta;
 
 import java.util.HashMap;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyHash;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class HashMetaClass extends ObjectMetaClass {
     public HashMetaClass(IRuby runtime) {
-        super("Hash", RubyHash.class, runtime.getObject());
+        super("Hash", RubyHash.class, runtime.getObject(), HASH_ALLOCATOR);
     }
     
-	public HashMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyHash.class, superClass, parentCRef);
+	public HashMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyHash.class, superClass, allocator, parentCRef);
 	}
 
 	protected class HashMeta extends Meta {
 		protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Enumerable"));
 	
 	        defineFastMethod("==", Arity.singleArgument(), "equal");
 	        defineFastMethod("[]", Arity.singleArgument(), "aref");
 	        defineFastMethod("[]=", Arity.twoArguments(), "aset");
 			defineFastMethod("clear", Arity.noArguments(), "rb_clear");
 			defineFastMethod("clone", Arity.noArguments(), "rbClone");
 			defineMethod("default", Arity.optional(), "getDefaultValue");
             defineMethod("default_proc", Arity.noArguments()); 
 			defineMethod("default=", Arity.singleArgument(), "setDefaultValue");
 			defineMethod("delete", Arity.singleArgument());
 			defineMethod("delete_if", Arity.noArguments());
 			defineMethod("each", Arity.noArguments());
 			defineMethod("each_pair", Arity.noArguments());
 			defineMethod("each_value", Arity.noArguments());
 			defineMethod("each_key", Arity.noArguments());
 	        defineFastMethod("empty?", Arity.noArguments(), "empty_p");
 	        defineMethod("fetch", Arity.optional());
 	        defineFastMethod("has_value?", Arity.singleArgument(), "has_value");
 	        defineFastMethod("index", Arity.singleArgument());
 	        defineFastMethod("indices", Arity.optional());
 	        defineMethod("initialize", Arity.optional());
 	        defineFastMethod("inspect", Arity.noArguments());
 			defineFastMethod("invert", Arity.noArguments());
 	        defineFastMethod("include?", Arity.singleArgument(), "has_key");
 			defineFastMethod("keys", Arity.noArguments());
 	        defineMethod("merge", Arity.singleArgument());
 	        defineFastMethod("rehash", Arity.noArguments());
 			defineMethod("reject", Arity.noArguments());
 			defineMethod("reject!", Arity.noArguments(), "reject_bang");
 	        defineFastMethod("replace", Arity.singleArgument());
 			defineFastMethod("shift", Arity.noArguments());
 	        defineFastMethod("size", Arity.noArguments(), "rb_size");
 			defineMethod("sort", Arity.noArguments());
 	        defineFastMethod("to_a", Arity.noArguments());
 	        defineFastMethod("to_hash", Arity.noArguments());
 	        defineFastMethod("to_s", Arity.noArguments());
 	        defineMethod("update", Arity.singleArgument());
 			defineFastMethod("values", Arity.noArguments(), "rb_values");
 	        defineFastMethod("values_at", Arity.optional());
 
 	        defineAlias("has_key?", "include?");
 			defineAlias("indexes", "indices");
 	        defineAlias("key?", "include?");
 			defineAlias("length", "size");
 	        defineAlias("member?", "include?");
 	        defineAlias("merge!", "update");
 	        defineAlias("store", "[]=");
 	        defineAlias("value?", "has_value?");
 	        
 	        defineSingletonMethod("new", Arity.optional(), "newInstance");
 	        defineFastSingletonMethod("[]", Arity.optional(), "create");
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new HashMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new HashMetaClass(name, this, parentCRef);
+		return new HashMetaClass(name, this, HASH_ALLOCATOR, parentCRef);
 	}
 
-	protected IRubyObject allocateObject() {
-        RubyHash instance = new RubyHash(getRuntime());
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+    private static ObjectAllocator HASH_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyHash instance = new RubyHash(runtime);
+
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
 
     public IRubyObject newInstance(IRubyObject[] args) {
+        // FIXME: This is pretty ugly, but I think it's being done to capture the block. Confirm that.
     	IRuby runtime = getRuntime();
-        RubyHash hash = (RubyHash)allocateObject();
+        RubyHash hash = (RubyHash)HASH_ALLOCATOR.allocate(runtime, this);
 
         // A block to represent 'default' value for unknown values
         if (runtime.getCurrentContext().isBlockGiven()) {
         	hash.setDefaultProc(runtime.newProc());
         }
         
         hash.setMetaClass(this);
         hash.callInit(args);
         
         return hash;
     }
     
     public IRubyObject create(IRubyObject[] args) {
-        RubyHash hash = (RubyHash)allocateObject();
+        RubyHash hash = (RubyHash)HASH_ALLOCATOR.allocate(getRuntime(), this);
 
         if (args.length == 1) {
             hash.setValueMap(new HashMap(((RubyHash) args[0]).getValueMap()));
         } else if (args.length % 2 != 0) {
             throw getRuntime().newArgumentError("odd number of args for Hash");
         } else {
             for (int i = 0; i < args.length; i += 2) {
                 hash.aset(args[i], args[i + 1]);
             }
         }
         return hash;
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
index 8b088e919e..cb35bd86fa 100644
--- a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
@@ -1,405 +1,408 @@
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
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.RubyKernel;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOHandler;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class IOMetaClass extends ObjectMetaClass {
 
     public IOMetaClass(IRuby runtime) {
-        this("IO", RubyIO.class, runtime.getObject());
+        this("IO", RubyIO.class, runtime.getObject(), IO_ALLOCATOR);
     }
 
-    public IOMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        this(name, RubyIO.class, superClass, parentCRef);
+    public IOMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        this(name, RubyIO.class, superClass, allocator, parentCRef);
     }
     
-    public IOMetaClass(String name, Class clazz, RubyClass superClass) {
-    	super(name, clazz, superClass);
+    public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
+    	super(name, clazz, superClass, allocator);
     }
 
-    public IOMetaClass(String name, Class clazz, RubyClass superClass, SinglyLinkedList parentCRef) {
-    	super(name, clazz, superClass, parentCRef);
+    public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+    	super(name, clazz, superClass, allocator, parentCRef);
     }
 
     protected class IOMeta extends Meta {
 	    protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Enumerable"));
 	
 	        // TODO: Implement tty? and isatty.  We have no real capability to 
 	        // determine this from java, but if we could set tty status, then
 	        // we could invoke jruby differently to allow stdin to return true
 	        // on this.  This would allow things like cgi.rb to work properly.
 	
 	        defineSingletonMethod("foreach", Arity.optional());
 			defineFastSingletonMethod("read", Arity.optional());
 	        defineFastSingletonMethod("readlines", Arity.optional());
 	        defineSingletonMethod("popen", Arity.optional());
             defineFastSingletonMethod("select", Arity.optional());
 			defineFastSingletonMethod("pipe", Arity.noArguments());
 	
 	        defineFastMethod("<<", Arity.singleArgument(), "addString");
 			defineFastMethod("binmode", Arity.noArguments());
 	        defineFastMethod("clone", Arity.noArguments(), "clone_IO");
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
-        return new IOMetaClass(name, this, parentCRef);
-    }
-
-    public IRubyObject allocateObject() {
-        return new RubyIO(getRuntime(), this); 
+        return new IOMetaClass(name, this, IO_ALLOCATOR, parentCRef);
     }
+    
+    private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            return new RubyIO(runtime, klass); 
+        }
+    };
 
     /** rb_io_s_foreach
      * 
      */
     public IRubyObject foreach(IRubyObject[] args) {
         int count = checkArgumentCount(args, 1, -1);
         IRubyObject filename = args[0].convertToString();
         filename.checkSafeString();
         RubyIO io = (RubyIO) ((FileMetaClass) getRuntime().getClass("File")).open(new IRubyObject[] { filename }, false);
 
         if (!io.isNil() && io.isOpen()) {
         	try {
 	            IRubyObject[] newArgs = new IRubyObject[count - 1];
 	            System.arraycopy(args, 1, newArgs, 0, count - 1);
 	
 	            IRubyObject nextLine = io.internalGets(newArgs);
 	            while (!nextLine.isNil()) {
 	                getRuntime().getCurrentContext().yield(nextLine);
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
 
     public static IRubyObject select_static(IRuby runtime, IRubyObject[] args) {
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
                 for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
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
 
     public IRubyObject read(IRubyObject[] args) {
         checkArgumentCount(args, 1, 3);
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments);
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
 
     public RubyArray readlines(IRubyObject[] args) {
         int count = checkArgumentCount(args, 1, 2);
 
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments);
         try {
         	return file.readlines(separatorArguments);
         } finally {
         	file.close();
         }
     }
     
     //XXX Hacked incomplete popen implementation to make
     public IRubyObject popen(IRubyObject[] args) {
     	IRuby runtime = getRuntime();
     	checkArgumentCount(args, 1, 2);
     	IRubyObject cmdObj = args[0].convertToString();
     	cmdObj.checkSafeString();
     	String command = cmdObj.toString();
         ThreadContext tc = runtime.getCurrentContext();
 
         /*
     	// only r works so throw error if anything else specified.
         if (args.length >= 2) {
             String mode = args[1].convertToString().toString();
             if (!mode.equals("r")) {
                 throw runtime.newNotImplementedError("only 'r' currently supported");
             }
         }
     	*/
     	try {
     		//TODO Unify with runInShell()
 	    	Process process;
 	    	String shell = System.getProperty("jruby.shell");
                 if (shell != null) {
 	            String shellSwitch = "-c";
 	            if (!shell.endsWith("sh")) {
 	                shellSwitch = "/c";
 	            }
 	            process = Runtime.getRuntime().exec(new String[] { shell, shellSwitch, command });
 	        } else {
 	            process = Runtime.getRuntime().exec(command);
                 }
 	    	
 	    	RubyIO io = new RubyIO(runtime, process);
 	    	
 	    	if (tc.isBlockGiven()) {
 		        try {
 		        	tc.yield(io);
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
 		IRuby runtime = getRuntime();
 		Pipe pipe = Pipe.open();
 		return runtime.newArray(new IRubyObject[]{
 			new RubyIO(runtime, pipe.source()),
 			new RubyIO(runtime, pipe.sink())
 			});
 	}
 }
diff --git a/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java b/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
index 3d7b383f64..9a5882d497 100644
--- a/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
@@ -1,99 +1,95 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class IntegerMetaClass extends NumericMetaClass {
 	public IntegerMetaClass(IRuby runtime) {
-        super("Integer", RubyInteger.class, runtime.getClass("Numeric"));
+        super("Integer", RubyInteger.class, runtime.getClass("Numeric"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 	}
 	
-	public IntegerMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, RubyInteger.class, superClass, parentCRef);
+	public IntegerMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, RubyInteger.class, superClass, allocator, parentCRef);
     }
 	
-    public IntegerMetaClass(String name, Class clazz, RubyClass superClass) {
-    	super(name, clazz, superClass);
+    public IntegerMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
+    	super(name, clazz, superClass, allocator);
     }
 
-    public IntegerMetaClass(String name, Class clazz, RubyClass superClass, SinglyLinkedList parentCRef) {
-    	super(name, clazz, superClass, parentCRef);
+    public IntegerMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+    	super(name, clazz, superClass, allocator, parentCRef);
     }
     
     protected class IntegerMeta extends Meta {
 		protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Precision"));
 	        
 	        defineFastMethod("chr", Arity.noArguments());
 	        defineMethod("downto",  Arity.singleArgument());
 	        defineFastMethod("integer?", Arity.noArguments(), "int_p");
 	        defineFastMethod("next",  Arity.noArguments());
 	        defineAlias("succ", "next");
 	        defineMethod("times", Arity.noArguments());
 	        defineMethod("upto", Arity.singleArgument());
 	        
 	        getSingletonClass().undefineMethod("new");
 	        defineFastSingletonMethod("induced_from",  Arity.singleArgument());
 		}
     };
     
     protected Meta getMeta() {
     	return new IntegerMeta();
     }
 
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new IntegerMetaClass(name, this, parentCRef);
-	}
-
-	// This cannot be allocated directly
-	protected IRubyObject allocateObject() {
-		return null;
+        return new IntegerMetaClass(name, this, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, parentCRef);
 	}
 	
     public RubyInteger induced_from(IRubyObject number) {
         if (number instanceof RubyFixnum) {
             return (RubyFixnum) number;
         } else if (number instanceof RubyFloat) {
             return ((RubyFloat) number).to_i();
         } else if (number instanceof RubyBignum) {
             return getRuntime().newFixnum(((RubyBignum) number).getLongValue());
         } else {
             throw getRuntime().newTypeError("failed to convert " + number.getMetaClass() + 
                 " into Integer");
         }
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/ModuleMetaClass.java b/src/org/jruby/runtime/builtin/meta/ModuleMetaClass.java
index 04b9f62266..e941cc7888 100644
--- a/src/org/jruby/runtime/builtin/meta/ModuleMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/ModuleMetaClass.java
@@ -1,137 +1,128 @@
 /*
  * Created on Jun 21, 2005
  *
  * To change the template for this generated file go to
  * Window - Preferences - Java - Code Generation - Code and Comments
  */
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class ModuleMetaClass extends ObjectMetaClass {
 	public ModuleMetaClass(IRuby runtime, RubyClass superClass) {
-		super(runtime, null, superClass, runtime.getObject().getCRef(), "Module", RubyModule.class);
+		super(runtime, null, superClass, MODULE_ALLOCATOR, runtime.getObject().getCRef(), "Module", RubyModule.class);
 	}
-
-    public ModuleMetaClass(IRuby runtime) {
-        super("Module", RubyModule.class, runtime.getObject());
-    }
     
-	public ModuleMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyModule.class, superClass, parentCRef);
-	}
-	
-	protected ModuleMetaClass(String name, Class builtinClass, RubyClass superClass) {
-		super(name, builtinClass, superClass);
-	}
-	
-	protected ModuleMetaClass(String name, Class builtinClass, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, builtinClass, superClass, parentCRef);
+	public ModuleMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyModule.class, superClass, allocator, parentCRef);
 	}
 
 	protected class ModuleMeta extends Meta {
 		public void initializeClass() {
 	        defineFastMethod("===", Arity.singleArgument(), "op_eqq");
 	        defineFastMethod("<=>", Arity.singleArgument(), "op_cmp");
 	        defineFastMethod("<", Arity.singleArgument(), "op_lt");
 	        defineFastMethod("<=", Arity.singleArgument(), "op_le");
 	        defineFastMethod(">", Arity.singleArgument(), "op_gt");
 	        defineFastMethod(">=", Arity.singleArgument(), "op_ge");
 	        defineFastMethod("ancestors", Arity.noArguments());
 	        defineFastMethod("class_variables", Arity.noArguments());
 	        defineFastMethod("clone", Arity.noArguments(), "rbClone");
 	        defineFastMethod("const_defined?", Arity.singleArgument(), "const_defined");
 	        defineFastMethod("const_get", Arity.singleArgument(), "const_get");
 	        defineMethod("const_missing", Arity.singleArgument());
 	        defineFastMethod("const_set", Arity.twoArguments());
 	        defineFastMethod("constants", Arity.noArguments());
 	        defineFastMethod("dup", Arity.noArguments());
             defineMethod("extended", Arity.singleArgument());
 	        defineFastMethod("included", Arity.singleArgument());
 	        defineFastMethod("included_modules", Arity.noArguments());
 	        defineMethod("initialize", Arity.optional());
 	        defineFastMethod("instance_method", Arity.singleArgument());
 	        defineFastMethod("instance_methods", Arity.optional());
 	        defineFastMethod("method_defined?", Arity.singleArgument(), "method_defined");
 	        defineMethod("module_eval", Arity.optional());
 	        defineFastMethod("name", Arity.noArguments());
 	        defineFastMethod("private_class_method", Arity.optional());
 	        defineFastMethod("private_instance_methods", Arity.optional());
 	        defineFastMethod("protected_instance_methods", Arity.optional());
 	        defineFastMethod("public_class_method", Arity.optional());
 	        defineFastMethod("public_instance_methods", Arity.optional());
 	        defineFastMethod("to_s",  Arity.noArguments());
 	
 	        defineAlias("class_eval", "module_eval");
 	
 	        defineFastPrivateMethod("alias_method", Arity.twoArguments());
 	        defineFastPrivateMethod("append_features", Arity.singleArgument());
 	        defineFastPrivateMethod("attr", Arity.optional());
 	        defineFastPrivateMethod("attr_reader", Arity.optional());
 	        defineFastPrivateMethod("attr_writer", Arity.optional());
 	        defineFastPrivateMethod("attr_accessor", Arity.optional());
 	        definePrivateMethod("define_method", Arity.optional());
 	        defineFastPrivateMethod("extend_object", Arity.singleArgument());
 	        defineFastPrivateMethod("include", Arity.optional());
 	        definePrivateMethod("method_added", Arity.singleArgument());
 	        defineFastPrivateMethod("module_function", Arity.optional());
 	        definePrivateMethod("public", Arity.optional(), "rbPublic");
 	        definePrivateMethod("protected", Arity.optional(), "rbProtected");
 	        definePrivateMethod("private", Arity.optional(), "rbPrivate");
 	        defineFastPrivateMethod("remove_class_variable", Arity.singleArgument());
 	        defineFastPrivateMethod("remove_const", Arity.singleArgument());
 	        defineFastPrivateMethod("remove_method", Arity.optional());
 	        defineFastPrivateMethod("undef_method", Arity.singleArgument());
 	
 	        defineSingletonMethod("nesting", Arity.noArguments());
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new ModuleMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new ModuleMetaClass(name, this, parentCRef);
+		return new ModuleMetaClass(name, this, MODULE_ALLOCATOR, parentCRef);
 	}
 
-	protected IRubyObject allocateObject() {
-        RubyModule instance = RubyModule.newModule(getRuntime(), null);
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+    private static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyModule instance = RubyModule.newModule(runtime, null);
+
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
     
     protected void defineModuleFunction(String name, Arity arity) {
         definePrivateMethod(name, arity);
         defineSingletonMethod(name, arity);
     }
 
     protected void defineModuleFunction(String name, Arity arity, String javaName) {
         definePrivateMethod(name, arity, javaName);
         defineSingletonMethod(name, arity, javaName);
     }
     
    /** 
     * Return an array of nested modules or classes.
     */
    public RubyArray nesting() {
 	   IRuby runtime = getRuntime();
        RubyModule object = runtime.getObject();
        SinglyLinkedList base = runtime.getCurrentContext().peekCRef();
        RubyArray result = runtime.newArray();
        
        for (SinglyLinkedList current = base; current.getValue() != object; current = current.getNext()) {
            result.append((RubyModule)current.getValue());
        }
        
        return result;
    }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/NumericMetaClass.java b/src/org/jruby/runtime/builtin/meta/NumericMetaClass.java
index b327d79bd7..ad9d5d13e9 100644
--- a/src/org/jruby/runtime/builtin/meta/NumericMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/NumericMetaClass.java
@@ -1,106 +1,110 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyNumeric;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class NumericMetaClass extends ObjectMetaClass {
 	public NumericMetaClass(IRuby runtime) {
-        super("Numeric", RubyNumeric.class, runtime.getObject());
+        super("Numeric", RubyNumeric.class, runtime.getObject(), NUMERIC_ALLOCATOR);
     }
 	    
-	public NumericMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, RubyNumeric.class, superClass, parentCRef);
+	public NumericMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, RubyNumeric.class, superClass, allocator, parentCRef);
     }
 
-    public NumericMetaClass(String name, Class clazz, RubyClass superClass) {
-    	super(name, clazz, superClass);
+    public NumericMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
+    	super(name, clazz, superClass, allocator);
     }
 
-    public NumericMetaClass(String name, Class clazz, RubyClass superClass, SinglyLinkedList parentCRef) {
-    	super(name, clazz, superClass, parentCRef);
+    public NumericMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+    	super(name, clazz, superClass, allocator, parentCRef);
     }
     
     protected class NumericMeta extends Meta {
 	    protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Comparable"));
 	
 	        defineFastMethod("+@", Arity.noArguments(), "op_uplus");
 	        defineFastMethod("-@", Arity.noArguments(), "op_uminus");
 	        defineFastMethod("<=>", Arity.singleArgument(), "cmp");
 	        defineFastMethod("==", Arity.singleArgument(), "equal");
 	        defineFastMethod("equal?", Arity.singleArgument(), "veryEqual");
 	        defineFastMethod("===", Arity.singleArgument(), "equal");
 	        defineFastMethod("abs", Arity.noArguments());
 	        defineFastMethod("ceil", Arity.noArguments());
 	        defineFastMethod("coerce", Arity.singleArgument());
 	        defineFastMethod("clone", Arity.noArguments(), "rbClone");
 	        defineFastMethod("divmod", Arity.singleArgument(), "divmod");
 	        defineFastMethod("eql?", Arity.singleArgument(), "eql");
 	        defineFastMethod("floor", Arity.noArguments());
 	        defineFastMethod("integer?", Arity.noArguments(), "int_p");
 	        defineFastMethod("modulo", Arity.singleArgument());
 	        defineFastMethod("nonzero?", Arity.noArguments(), "nonzero_p");
 	        defineFastMethod("remainder", Arity.singleArgument());
 	        defineFastMethod("round", Arity.noArguments());
 	        defineFastMethod("truncate", Arity.noArguments());
 	        defineFastMethod("to_int", Arity.noArguments());
 	        defineFastMethod("zero?", Arity.noArguments(), "zero_p");
             defineMethod("step", Arity.required(1), "step");
             
             // Add relational operators that are faster than comparable's implementations
             defineFastMethod(">=", Arity.singleArgument(), "op_ge");
             defineFastMethod(">", Arity.singleArgument(), "op_gt");
             defineFastMethod("<=", Arity.singleArgument(), "op_le");
             defineFastMethod("<", Arity.singleArgument(), "op_lt");
 	        
 	        defineFastSingletonMethod("new", Arity.optional(), "newInstance"); 
 	    }
     };
     
     protected Meta getMeta() {
     	return new NumericMeta();
     }
 		
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new NumericMetaClass(name, this, parentCRef);
+        // FIXME: this and the other newSubClass impls should be able to defer to the default impl
+        return new NumericMetaClass(name, this, NUMERIC_ALLOCATOR, parentCRef);
     }
 
-	protected IRubyObject allocateObject() {
-		RubyNumeric instance = getRuntime().newNumeric();
+    private static ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyNumeric instance = runtime.newNumeric();
 
-		instance.setMetaClass(this);
-		
-        return instance;
-    }
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/ObjectMetaClass.java b/src/org/jruby/runtime/builtin/meta/ObjectMetaClass.java
index 5de4215e63..6c9e0545e8 100644
--- a/src/org/jruby/runtime/builtin/meta/ObjectMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/ObjectMetaClass.java
@@ -1,104 +1,106 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * <p>
  * The meta class for Object
  * </p>
  */
 public class ObjectMetaClass extends AbstractMetaClass {
     // Only for creating ObjectMetaClass directly
     public ObjectMetaClass(IRuby runtime) {
-    	super(runtime, null /*Would be Class if it existed yet */, null, null, "Object");
+    	super(runtime, null /*Would be Class if it existed yet */, null, OBJECT_ALLOCATOR, null, "Object");
     	
     	this.builtinClass = RubyObject.class;
     }
     
     // Only for other core modules/classes
-    protected ObjectMetaClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, 
+    protected ObjectMetaClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, 
             SinglyLinkedList parentCRef, String name, Class builtinClass) {
-    	super(runtime, metaClass, superClass, parentCRef, name);
+    	super(runtime, metaClass, superClass, allocator, parentCRef, name);
     	
     	this.builtinClass = builtinClass;
     }
     
-    protected ObjectMetaClass(String name, Class builtinClass, RubyClass superClass) {
-        this(name, builtinClass, superClass, superClass.getRuntime().getClass("Object").getCRef());
+    protected ObjectMetaClass(String name, Class builtinClass, RubyClass superClass, ObjectAllocator allocator) {
+        this(name, builtinClass, superClass, allocator, superClass.getRuntime().getClass("Object").getCRef());
     }
 
-    protected ObjectMetaClass(String name, Class builtinClass, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, parentCRef, name);
+    protected ObjectMetaClass(String name, Class builtinClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, allocator, parentCRef, name);
 
         assert builtinClass != null;
         //assert RubyObject.class.isAssignableFrom(builtinClass) || RubyObject.class == builtinClass: "builtinClass have to be a subclass of RubyObject.";
         assert superClass != null;
 
         this.builtinClass = builtinClass;
 
         makeMetaClass(superClass.getMetaClass(), getCRef());
         inheritedBy(superClass);
 
         if(name != null) {
             ((RubyModule)parentCRef.getValue()).setConstant(name, this);
         }
     }
     
     protected class ObjectMeta extends Meta {
 	    protected void initializeClass() {
 	        definePrivateMethod("initialize", Arity.optional());
 	        definePrivateMethod("inherited", Arity.singleArgument());
 		}
     };
     
     protected Meta getMeta() {
     	return new ObjectMeta();
     }
+    
+    private static ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            IRubyObject instance = new RubyObject(runtime, klass);
+            instance.setMetaClass(klass);
 
-	protected IRubyObject allocateObject() {
-        RubyObject instance = new RubyObject(getRuntime(), this);
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+            return instance;
+        }
+    };
     
     public void initializeClass() {
         getMeta().initializeClass();
     }
     
 }
diff --git a/src/org/jruby/runtime/builtin/meta/ProcMetaClass.java b/src/org/jruby/runtime/builtin/meta/ProcMetaClass.java
index 64e90b9064..4d8dd2307c 100644
--- a/src/org/jruby/runtime/builtin/meta/ProcMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/ProcMetaClass.java
@@ -1,81 +1,74 @@
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyProc;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class ProcMetaClass extends ObjectMetaClass {
     public ProcMetaClass(IRuby runtime) {
-        super("Proc", RubyProc.class, runtime.getObject());
+        super("Proc", RubyProc.class, runtime.getObject(), PROC_ALLOCATOR);
     }
     
-	public ProcMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubyProc.class, superClass, parentCRef);
+	public ProcMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubyProc.class, superClass, allocator, parentCRef);
 	}
 
 	protected class ProcMeta extends Meta {
 		protected void initializeClass() {
 			defineFastMethod("arity", Arity.noArguments(), "arity");
 			defineMethod("binding", Arity.noArguments(), "binding");
 			defineMethod("call", Arity.optional(), "call");
 			defineAlias("[]", "call");
 			defineMethod("to_proc", Arity.noArguments(), "to_proc");
-	
-	        defineSingletonMethod("new", Arity.optional(), "newInstance"); 
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new ProcMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new ProcMetaClass(name, this, parentCRef);
+		return new ProcMetaClass(name, this, PROC_ALLOCATOR, parentCRef);
 	}
+    
+    private static ObjectAllocator PROC_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyProc instance = runtime.newProc();
 
-	protected IRubyObject allocateObject() {
-        RubyProc instance = getRuntime().newProc();
-        
-		instance.setMetaClass(this);
-		
-		return instance;
-	}
+            instance.setMetaClass(klass);
 
-    public IRubyObject newInstance(IRubyObject[] args) {
-        IRubyObject instance = allocateObject();
-        
-        instance.callInit(args);
-       
-        return instance;
-    }
+            return instance;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/StringMetaClass.java b/src/org/jruby/runtime/builtin/meta/StringMetaClass.java
index 3d5c3dbe98..ec1298bc5d 100644
--- a/src/org/jruby/runtime/builtin/meta/StringMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/StringMetaClass.java
@@ -1,291 +1,296 @@
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.runtime.builtin.meta;
 
 import java.util.Locale;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyString;
 import org.jruby.RubyString.StringMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class StringMetaClass extends ObjectMetaClass {
     public StringMetaClass(IRuby runtime) {
-        super("String", RubyString.class, runtime.getObject());
+        super("String", RubyString.class, runtime.getObject(), STRING_ALLOCATOR);
     }
 
-    private StringMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, RubyString.class, superClass, parentCRef);
+    private StringMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, RubyString.class, superClass, allocator, parentCRef);
     }
 
     public StringMethod hash = new StringMethod(this, Arity.noArguments(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             return self.getRuntime().newFixnum(self.toString().hashCode());
         }
     };
     
     public StringMethod to_s = new StringMethod(this, Arity.noArguments(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             return self;
         }
     };
     
     /* rb_str_cmp_m */
     public StringMethod op_cmp = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             if (args[0] instanceof RubyString) {
                 return getRuntime().newFixnum(self.cmp((RubyString) args[0]));
             }
             
             ThreadContext context = self.getRuntime().getCurrentContext();
                 
             if (args[0].respondsTo("to_str") && args[0].respondsTo("<=>")) {
                 IRubyObject tmp = args[0].callMethod(context, "<=>", self);
 
                 if (!tmp.isNil()) {
                     return tmp instanceof RubyFixnum ? tmp.callMethod(context, "-") :
                         getRuntime().newFixnum(0).callMethod(context, "-", tmp);
                 }
             }
             
             return getRuntime().getNil();
         }
     };
 
     public StringMethod equal = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             IRubyObject other = args[0];
             
             if (other == self) {
                 return self.getRuntime().getTrue();
             } else if (!(other instanceof RubyString)) {
                 if (other.respondsTo("to_str")) {
                     return other.callMethod(self.getRuntime().getCurrentContext(), "==", self);
                 }
                 return self.getRuntime().getFalse();
             }
             /* use Java implementation if both different String instances */
             return self.getRuntime().newBoolean(
                     self.toString().equals(((RubyString) other).toString()));
             
         }
     };
     
     public StringMethod veryEqual = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             IRubyObject other = args[0];
             IRubyObject truth = self.callMethod(self.getRuntime().getCurrentContext(), "==", other);
             
             return truth == self.getRuntime().getNil() ? self.getRuntime().getFalse() : truth;
         }
     };
 
     public StringMethod op_plus = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             IRubyObject other = args[0];
             RubyString str = RubyString.stringValue(other);
             
             return (RubyString) self.newString(self.toString() + str.toString()).infectBy(str);
         }
     };
 
     public StringMethod op_mul = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             IRubyObject other = args[0];
             
             RubyInteger otherInteger =
                     (RubyInteger) other.convertType(RubyInteger.class, "Integer", "to_i");
             long len = otherInteger.getLongValue();
     
             if (len < 0) {
                 throw self.getRuntime().newArgumentError("negative argument");
             }
     
             if (len > 0 && Long.MAX_VALUE / len < self.getValue().length()) {
                 throw self.getRuntime().newArgumentError("argument too big");
             }
             StringBuffer sb = new StringBuffer((int) (self.getValue().length() * len));
     
             for (int i = 0; i < len; i++) {
                 sb.append(self.getValue());
             }
     
             RubyString newString = self.newString(sb.toString());
             newString.setTaint(self.isTaint());
             return newString;
         }
     };
 
     public StringMethod format = new StringMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubyString self, IRubyObject[] args) {
             IRubyObject arg = args[0];
             
             if (arg instanceof RubyArray) {
                 Object[] args2 = new Object[((RubyArray) arg).getLength()];
                 for (int i = 0; i < args2.length; i++) {
                     args2[i] = JavaUtil.convertRubyToJava(((RubyArray) arg).entry(i));
                 }
                 return self.getRuntime().newString(new PrintfFormat(Locale.US, self.toString()).sprintf(args2));
             }
             return self.getRuntime().newString(new PrintfFormat(Locale.US, self.toString()).sprintf(JavaUtil.convertRubyToJava(arg)));
         }
     };
 
     protected class StringMeta extends Meta {
 	    protected void initializeClass() {
 	        includeModule(getRuntime().getModule("Comparable"));
 	        includeModule(getRuntime().getModule("Enumerable"));
 	
             addMethod("<=>", op_cmp);
             addMethod("==", equal);
             addMethod("===", veryEqual);
             addMethod("+", op_plus);
             addMethod("*", op_mul);
             addMethod("%", format);
             addMethod("hash", hash);
             addMethod("to_s", to_s);
             
             // To override Comparable with faster String ones
             defineFastMethod(">=", Arity.singleArgument(), "op_ge");
             defineFastMethod(">", Arity.singleArgument(), "op_gt");
             defineFastMethod("<=", Arity.singleArgument(), "op_le");
             defineFastMethod("<", Arity.singleArgument(), "op_lt");
             
             defineFastMethod("eql?", Arity.singleArgument(), "op_eql");
             
 	        defineFastMethod("[]", Arity.optional(), "aref");
 	        defineFastMethod("[]=", Arity.optional(), "aset");
 	        defineFastMethod("=~", Arity.singleArgument(), "match");
 	        defineFastMethod("~", Arity.noArguments(), "match2");
 	        defineFastMethod("capitalize", Arity.noArguments());
 	        defineFastMethod("capitalize!", Arity.noArguments(), "capitalize_bang");
 	        defineFastMethod("casecmp", Arity.singleArgument());
 	        defineFastMethod("center", Arity.optional());
 	        defineFastMethod("chop", Arity.noArguments());
 	        defineFastMethod("chop!", Arity.noArguments(), "chop_bang");
 	        defineFastMethod("chomp", Arity.optional());
 	        defineFastMethod("chomp!", Arity.optional(), "chomp_bang");
 	        defineFastMethod("clone", Arity.noArguments(), "rbClone");
 	        defineFastMethod("concat", Arity.singleArgument());
 	        defineFastMethod("count", Arity.optional());
 	        defineFastMethod("crypt", Arity.singleArgument());
 	        defineFastMethod("delete", Arity.optional());
 	        defineFastMethod("delete!", Arity.optional(), "delete_bang");
 	        defineFastMethod("downcase", Arity.noArguments());
 	        defineFastMethod("downcase!", Arity.noArguments(), "downcase_bang");
 	        defineFastMethod("dump", Arity.noArguments());
 	        defineFastMethod("dup", Arity.noArguments());
 	        defineMethod("each_line", Arity.optional());
 	        defineMethod("each_byte", Arity.noArguments());
 	        defineFastMethod("empty?", Arity.noArguments(), "empty");
 	        defineMethod("gsub", Arity.optional());
 	        defineMethod("gsub!", Arity.optional(), "gsub_bang");
 	        defineFastMethod("hex", Arity.noArguments());
 	        defineFastMethod("include?", Arity.singleArgument(), "include");
 	        defineFastMethod("index", Arity.optional());
 	        defineMethod("initialize", Arity.optional(), "initialize");
 	        defineMethod("initialize_copy", Arity.singleArgument(), "replace");
 	        defineFastMethod("insert", Arity.twoArguments());
 	        defineFastMethod("inspect", Arity.noArguments());
 	        defineFastMethod("length", Arity.noArguments());
 	        defineFastMethod("ljust", Arity.optional());
 	        defineFastMethod("lstrip", Arity.noArguments());
 	        defineFastMethod("lstrip!", Arity.noArguments(), "lstrip_bang");
 	        defineFastMethod("match", Arity.singleArgument(), "match3");
 	        defineFastMethod("oct", Arity.noArguments());
 	        defineFastMethod("replace", Arity.singleArgument());
 	        defineFastMethod("reverse", Arity.noArguments());
 	        defineFastMethod("reverse!", Arity.noArguments(), "reverse_bang");
 	        defineFastMethod("rindex", Arity.optional());
 	        defineFastMethod("rjust", Arity.optional());
 	        defineFastMethod("rstrip", Arity.noArguments());
 	        defineFastMethod("rstrip!", Arity.noArguments(), "rstrip_bang");
 	        defineMethod("scan", Arity.singleArgument());
 	        defineFastMethod("slice!", Arity.optional(), "slice_bang");
 	        defineFastMethod("split", Arity.optional());
 	        defineFastMethod("strip", Arity.noArguments());
 	        defineFastMethod("strip!", Arity.noArguments(), "strip_bang");
 	        defineFastMethod("succ", Arity.noArguments());
 	        defineFastMethod("succ!", Arity.noArguments(), "succ_bang");
 	        defineFastMethod("squeeze", Arity.optional());
 	        defineFastMethod("squeeze!", Arity.optional(), "squeeze_bang");
 	        defineMethod("sub", Arity.optional());
 	        defineMethod("sub!", Arity.optional(), "sub_bang");
 	        defineFastMethod("sum", Arity.optional());
 	        defineFastMethod("swapcase", Arity.noArguments());
 	        defineFastMethod("swapcase!", Arity.noArguments(), "swapcase_bang");
 	        defineFastMethod("to_f", Arity.noArguments());
 	        defineFastMethod("to_i", Arity.optional());
 	        defineFastMethod("to_str", Arity.noArguments());
 	        defineFastMethod("to_sym", Arity.noArguments());
 	        defineFastMethod("tr", Arity.twoArguments());
 	        defineFastMethod("tr!", Arity.twoArguments(), "tr_bang");
 	        defineFastMethod("tr_s", Arity.twoArguments());
 	        defineFastMethod("tr_s!", Arity.twoArguments(), "tr_s_bang");
 	        defineFastMethod("unpack", Arity.singleArgument());
 	        defineFastMethod("upcase", Arity.noArguments());
 	        defineFastMethod("upcase!", Arity.noArguments(), "upcase_bang");
 	        defineMethod("upto", Arity.singleArgument());
 	
 	        defineAlias("<<", "concat");
 	        defineAlias("each", "each_line");
 	        defineAlias("intern", "to_sym");
 	        defineAlias("next", "succ");
 	        defineAlias("next!", "succ!");
 	        defineAlias("size", "length");
 	        defineAlias("slice", "[]");
 	    }
     };
     
     protected Meta getMeta() {
     	return new StringMeta();
     }
 
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new StringMetaClass(name, this, parentCRef);
+        return new StringMetaClass(name, this, STRING_ALLOCATOR, parentCRef);
     }
 
-    protected IRubyObject allocateObject() {
-        RubyString newString = getRuntime().newString("");
-		newString.setMetaClass(this);
-		return newString;
-    }
+    private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyString newString = runtime.newString("");
+            
+            newString.setMetaClass(klass);
+            
+            return newString;
+        }
+    };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/SymbolMetaClass.java b/src/org/jruby/runtime/builtin/meta/SymbolMetaClass.java
index d3975ed56e..cbc233217f 100644
--- a/src/org/jruby/runtime/builtin/meta/SymbolMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/SymbolMetaClass.java
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
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
 package org.jruby.runtime.builtin.meta;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubySymbol;
 import org.jruby.RubySymbol.SymbolMethod;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class SymbolMetaClass extends ObjectMetaClass {
     public SymbolMetaClass(IRuby runtime) {
-        super("Symbol", RubySymbol.class, runtime.getObject());
+        super("Symbol", RubySymbol.class, runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
     }
     
-	public SymbolMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-		super(name, RubySymbol.class, superClass, parentCRef);
+	public SymbolMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+		super(name, RubySymbol.class, superClass, allocator, parentCRef);
 	}
 
     public SymbolMethod equal = new SymbolMethod(this, Arity.singleArgument(), Visibility.PUBLIC) {
         public IRubyObject invoke(RubySymbol self, IRubyObject[] args) {
             IRubyObject other = args[0];
             
             // Symbol table ensures only one instance for every name,
             // so object identity is enough to compare symbols.
             return self.getRuntime().newBoolean(self == other);
         }
     };
 
 	protected class SymbolMeta extends Meta {
 		public void initializeClass() {
             addMethod("==", equal);
             
 	        defineFastMethod("clone", Arity.noArguments(), "rbClone");
 	        defineFastMethod("freeze", Arity.noArguments()); 
 	        defineFastMethod("hash", Arity.noArguments()); 
 	        defineFastMethod("inspect", Arity.noArguments());
 	        defineFastMethod("taint", Arity.noArguments());
 	        defineFastMethod("to_i", Arity.noArguments());
 	        defineFastMethod("to_s", Arity.noArguments());
             defineFastMethod("to_sym", Arity.noArguments());
             defineFastSingletonMethod("all_symbols", Arity.noArguments());
             defineAlias("dup", "clone");
 	        defineAlias("id2name", "to_s");
 	        defineAlias("to_int", "to_i");
 				
 	        getMetaClass().undefineMethod("new");
 		}
 	};
 	
 	protected Meta getMeta() {
 		return new SymbolMeta();
 	}
 	
 	public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-		return new SymbolMetaClass(name, this, parentCRef);
+		return new SymbolMetaClass(name, this, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, parentCRef);
 	}
     
     public IRubyObject all_symbols() {
         return getRuntime().newArray(getRuntime().getSymbolTable().all_symbols());
     }
-
 }
diff --git a/src/org/jruby/runtime/builtin/meta/TimeMetaClass.java b/src/org/jruby/runtime/builtin/meta/TimeMetaClass.java
index 065cf0cc1c..cc25c56a2a 100644
--- a/src/org/jruby/runtime/builtin/meta/TimeMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/TimeMetaClass.java
@@ -1,317 +1,312 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime.builtin.meta;
 
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.TimeZone;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.runtime.Arity;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class TimeMetaClass extends ObjectMetaClass {
     public TimeMetaClass(IRuby runtime) {
-        super("Time", RubyTime.class, runtime.getObject());
-    }
-        
-    public TimeMetaClass(String name, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, RubyTime.class, superClass, parentCRef);
+        super("Time", RubyTime.class, runtime.getObject(), TIME_ALLOCATOR);
     }
 
-    public TimeMetaClass(String name, Class clazz, RubyClass superClass) {
-        super(name, clazz, superClass);
-    }
-
-    public TimeMetaClass(String name, Class clazz, RubyClass superClass, SinglyLinkedList parentCRef) {
-        super(name, clazz, superClass, parentCRef);
+    public TimeMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
+        super(name, RubyTime.class, superClass, allocator, parentCRef);
     }
     
     protected class TimeMeta extends Meta {
         protected void initializeClass() {
             includeModule(getRuntime().getModule("Comparable"));
     
             defineSingletonMethod("new", Arity.noArguments(), "s_new"); 
             defineFastSingletonMethod("now", Arity.noArguments(), "s_new"); 
             defineFastSingletonMethod("at", Arity.optional(), "new_at"); 
             defineFastSingletonMethod("local", Arity.optional(), "new_local"); 
             defineFastSingletonMethod("mktime", Arity.optional(), "new_local"); 
             defineFastSingletonMethod("utc", Arity.optional(), "new_utc"); 
             defineFastSingletonMethod("gm", Arity.optional(), "new_utc"); 
             defineSingletonMethod("_load", Arity.singleArgument(), "s_load"); 
             
             // To override Comparable with faster String ones
             defineFastMethod(">=", Arity.singleArgument(), "op_ge");
             defineFastMethod(">", Arity.singleArgument(), "op_gt");
             defineFastMethod("<=", Arity.singleArgument(), "op_le");
             defineFastMethod("<", Arity.singleArgument(), "op_lt");
             
             defineFastMethod("===", Arity.singleArgument(), "same2");
             defineFastMethod("+", Arity.singleArgument(), "op_plus"); 
             defineFastMethod("-", Arity.singleArgument(), "op_minus"); 
             defineFastMethod("<=>", Arity.singleArgument(), "op_cmp");
             defineFastMethod("asctime", Arity.noArguments()); 
             defineFastMethod("mday", Arity.noArguments()); 
             defineAlias("day", "mday"); 
             defineAlias("ctime", "asctime");
             defineFastMethod("sec", Arity.noArguments()); 
             defineFastMethod("min", Arity.noArguments()); 
             defineFastMethod("hour", Arity.noArguments()); 
             defineFastMethod("month", Arity.noArguments());
             defineAlias("mon", "month"); 
             defineFastMethod("year", Arity.noArguments()); 
             defineFastMethod("wday", Arity.noArguments()); 
             defineFastMethod("yday", Arity.noArguments());
             defineFastMethod("isdst", Arity.noArguments());
             defineAlias("dst?", "isdst");
             defineFastMethod("zone", Arity.noArguments()); 
             defineFastMethod("to_a", Arity.noArguments()); 
             defineFastMethod("to_f", Arity.noArguments()); 
             defineFastMethod("to_i", Arity.noArguments());
             defineFastMethod("to_s", Arity.noArguments()); 
             defineFastMethod("inspect", Arity.noArguments()); 
             defineFastMethod("strftime", Arity.singleArgument()); 
             defineFastMethod("usec",  Arity.noArguments());
             defineAlias("tv_usec", "usec"); 
             defineAlias("tv_sec", "to_i"); 
             defineFastMethod("gmtime", Arity.noArguments());   
             defineAlias("utc", "gmtime"); 
             defineFastMethod("gmt?", Arity.noArguments(), "gmt");
             defineAlias("utc?", "gmt?");
             defineAlias("gmtime?", "gmt?");
             defineFastMethod("localtime", Arity.noArguments()); 
             defineFastMethod("hash", Arity.noArguments()); 
             defineMethod("initialize_copy", Arity.singleArgument()); 
             defineMethod("_dump", Arity.optional(),"dump"); 
             defineFastMethod("gmt_offset", Arity.noArguments());
             defineAlias("gmtoff", "gmt_offset");
             defineAlias("utc_offset", "gmt_offset");
             defineFastMethod("getgm", Arity.noArguments());
             defineFastMethod("getlocal", Arity.noArguments());
             defineAlias("getutc", "getgm");
         }
     };
     
     protected Meta getMeta() {
         return new TimeMeta();
     }
         
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
-        return new NumericMetaClass(name, this, parentCRef);
+        return new TimeMetaClass(name, this, TIME_ALLOCATOR, parentCRef);
     }
 
-    protected IRubyObject allocateObject() {
-        RubyTime instance = new RubyTime(getRuntime(), this);
+    private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            RubyTime instance = new RubyTime(runtime, klass);
 
-        instance.setMetaClass(this);
-        
-        return instance;
-    }
+            instance.setMetaClass(klass);
+
+            return instance;
+        }
+    };
     
     public IRubyObject s_new() {
         RubyTime time = new RubyTime(getRuntime(), this);
         GregorianCalendar cal = new GregorianCalendar();
         cal.setTime(new Date());
         time.setJavaCalendar(cal);
         return time;
     }
 
     public IRubyObject new_at(IRubyObject[] args) {
         int len = checkArgumentCount(args, 1, 2);
 
         Calendar cal = Calendar.getInstance(); 
         RubyTime time = new RubyTime(getRuntime(), this, cal);
 
         if (args[0] instanceof RubyTime) {
             ((RubyTime) args[0]).updateCal(cal);
         } else {
             long seconds = RubyNumeric.num2long(args[0]);
             long millisecs = 0;
             long microsecs = 0;
             if (len > 1) {
                 long tmp = RubyNumeric.num2long(args[1]);
                 millisecs = tmp / 1000;
                 microsecs = tmp % 1000;
             }
             else {
                 // In the case of two arguments, MRI will discard the portion of
                 // the first argument after a decimal point (i.e., "floor").
                 // However in the case of a single argument, any portion after
                 // the decimal point is honored.
                 if (args[0] instanceof RubyFloat) {
                     double dbl = ((RubyFloat) args[0]).getDoubleValue();
                     long micro = (long) ((dbl - seconds) * 1000000);
                     millisecs = micro / 1000;
                     microsecs = micro % 1000;
                 }
             }
             time.setUSec(microsecs);
             cal.setTimeInMillis(seconds * 1000 + millisecs);
         }
 
         time.callInit(args);
 
         return time;
     }
 
     public RubyTime new_local(IRubyObject[] args) {
         return createTime(args, false);
     }
 
     public RubyTime new_utc(IRubyObject[] args) {
         return createTime(args, true);
     }
 
     public RubyTime s_load(IRubyObject from) {
         return s_mload((RubyTime) s_new(), from);
     }
 
     protected RubyTime s_mload(RubyTime time, IRubyObject from) {
         Calendar calendar = Calendar.getInstance();
         calendar.clear();
         calendar.setTimeZone(TimeZone.getTimeZone(RubyTime.UTC));
         byte[] fromAsBytes = null;
         try {
             fromAsBytes = from.toString().getBytes("ISO8859_1");
         } catch(final java.io.UnsupportedEncodingException uee) {
             throw getRuntime().newTypeError("marshaled time format differ");
         }
         if(fromAsBytes.length != 8) {
             throw getRuntime().newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for(int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8*i);
         }
         for(int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8*(i-4));
         }
         if((p & (1<<31)) == 0) {
             calendar.setTimeInMillis(p * 1000L + s);
         } else {
             p &= ~(1<<31);
             calendar.set(Calendar.YEAR,((p >>> 14) & 0xFFFF)+1900);
             calendar.set(Calendar.MONTH,((p >>> 10) & 0xF));
             calendar.set(Calendar.DAY_OF_MONTH,((p >>> 5)  & 0x1F));
             calendar.set(Calendar.HOUR_OF_DAY,(p & 0x1F));
             calendar.set(Calendar.MINUTE,((s >>> 26) & 0x3F));
             calendar.set(Calendar.SECOND,((s >>> 20) & 0x3F));
             calendar.set(Calendar.MILLISECOND,(s & 0xFFFFF));
         }
         time.setJavaCalendar(calendar);
         return time;
     }
     
     private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
     private static final long[] time_min = {1, 0, 0, 0, 0};
     private static final long[] time_max = {31, 23, 59, 60, Long.MAX_VALUE};
 
     private RubyTime createTime(IRubyObject[] args, boolean gmt) {
         int len = 6;
         if (args.length == 10) {
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0] };
         } else {
             len = checkArgumentCount(args, 1, 7);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
         if(!(args[0] instanceof RubyNumeric)) {
             args[0] = args[0].callMethod(tc,"to_i");
         }
         int year = (int)RubyNumeric.num2long(args[0]);
         int month = 0;
         
         if (len > 1) {
             if (!args[1].isNil()) {
                 if (args[1] instanceof RubyString) {
                     month = -1;
                     for (int i = 0; i < 12; i++) {
                         if (months[i].equalsIgnoreCase(args[1].toString())) {
                             month = i;
                         }
                     }
                     if (month == -1) {
                         try {
                             month = Integer.parseInt(args[1].toString()) - 1;
                         } catch (NumberFormatException nfExcptn) {
                             throw getRuntime().newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int)RubyNumeric.num2long(args[1]) - 1;
                 }
             }
             if (0 > month || month > 11) {
                 throw getRuntime().newArgumentError("Argument out of range.");
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0 };
 
         for (int i = 0; len > i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if(!(args[i+2] instanceof RubyNumeric)) {
                     args[i+2] = args[i+2].callMethod(tc,"to_i");
                 }
                 int_args[i] = (int)RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > int_args[i] || int_args[i] > time_max[i]) {
                     throw getRuntime().newArgumentError("Argument out of range.");
                 }
             }
         }
 
         Calendar cal = gmt ? Calendar.getInstance(TimeZone.getTimeZone(RubyTime.UTC)) : 
             Calendar.getInstance(); 
         RubyTime time = new RubyTime(getRuntime(), (RubyClass) this, cal);
         cal.set(year, month, int_args[0], int_args[1], int_args[2], int_args[3]);
         cal.set(Calendar.MILLISECOND, int_args[4] / 1000);
         time.setUSec(int_args[4] % 1000);
 
         time.callInit(args);
 
         return time;
     }
 }
diff --git a/src/org/jruby/yaml/JRubyConstructor.java b/src/org/jruby/yaml/JRubyConstructor.java
index b4493a8bae..8382457ffe 100644
--- a/src/org/jruby/yaml/JRubyConstructor.java
+++ b/src/org/jruby/yaml/JRubyConstructor.java
@@ -1,324 +1,324 @@
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
  * $Id$
  */
 package org.jruby.yaml;
 
 import java.util.Date;
 import java.util.Calendar;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import java.util.regex.Pattern;
 
 import org.jvyaml.Composer;
 import org.jvyaml.Constructor;
 import org.jvyaml.ConstructorException;
 import org.jvyaml.ConstructorImpl;
 import org.jvyaml.SafeConstructorImpl;
 
 import org.jvyaml.nodes.Node;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyHash;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @version $Revision$
  */
 public class JRubyConstructor extends ConstructorImpl {
     private final static Map yamlConstructors = new HashMap();
     private final static Map yamlMultiConstructors = new HashMap();
     private final static Map yamlMultiRegexps = new HashMap();
     public YamlConstructor getYamlConstructor(final Object key) {
         return (YamlConstructor)yamlConstructors.get(key);
     }
 
     public YamlMultiConstructor getYamlMultiConstructor(final Object key) {
         return (YamlMultiConstructor)yamlMultiConstructors.get(key);
     }
 
     public Pattern getYamlMultiRegexp(final Object key) {
         return (Pattern)yamlMultiRegexps.get(key);
     }
 
     public Set getYamlMultiRegexps() {
         return yamlMultiRegexps.keySet();
     }
 
     public static void addConstructor(final String tag, final YamlConstructor ctor) {
         yamlConstructors.put(tag,ctor);
     }
 
     public static void addMultiConstructor(final String tagPrefix, final YamlMultiConstructor ctor) {
         yamlMultiConstructors.put(tagPrefix,ctor);
         yamlMultiRegexps.put(tagPrefix,Pattern.compile("^"+tagPrefix));
     }
 
     private final IRuby runtime;
 
     public JRubyConstructor(final IRubyObject receiver, final Composer composer) {
         super(composer);
         this.runtime = receiver.getRuntime();
     }
 
     public Object constructRubyScalar(final Node node) {
         return runtime.newString((String)super.constructScalar(node));
     }
 
     public Object constructRubySequence(final Node node) {
         return runtime.newArray((List)super.constructSequence(node));
     }
 
     public Object constructRubyMapping(final Node node) {
         return RubyHash.newHash(runtime,(Map)super.constructMapping(node),runtime.getNil());
     }
 
     public Object constructRubyPairs(final Node node) {
         return runtime.newArray((List)super.constructPairs(node));
     }
 
     public static Object constructYamlNull(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).runtime.getNil();
     }
     
     public static Object constructYamlBool(final Constructor ctor, final Node node) {
         return SafeConstructorImpl.constructYamlBool(ctor,node) == Boolean.TRUE ? ((JRubyConstructor)ctor).runtime.getTrue() : ((JRubyConstructor)ctor).runtime.getFalse();
     }
 
     public static Object constructYamlOmap(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).constructRubyPairs(node);
     }
 
     public static Object constructYamlPairs(final Constructor ctor, final Node node) {
         return constructYamlOmap(ctor,node);
     }
 
     public static Object constructYamlSet(final Constructor ctor, final Node node) {
         return SafeConstructorImpl.constructYamlSet(ctor,node);
     }
 
     public static Object constructYamlStr(final Constructor ctor, final Node node) {
         final org.jruby.RubyString str = (org.jruby.RubyString)((JRubyConstructor)ctor).constructRubyScalar(node);
         return (str.getValue().length() == 0 && ((org.jvyaml.nodes.ScalarNode)node).getStyle() == 0) ? str.getRuntime().getNil() : str;
     }
 
     public static Object constructYamlSeq(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).constructRubySequence(node);
     }
 
     public static Object constructYamlMap(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).constructRubyMapping(node);
     }
 
     public static Object constructUndefined(final Constructor ctor, final Node node) {
         throw new ConstructorException(null,"could not determine a constructor for the tag " + node.getTag(),null);
     }
 
     public static Object constructYamlTimestamp(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).runtime.newTime(((Date)SafeConstructorImpl.constructYamlTimestamp(ctor,node)).getTime()).callMethod(((JRubyConstructor)ctor).runtime.getCurrentContext(),"utc");
     }
 
     public static Object constructYamlTimestampYMD(final Constructor ctor, final Node node) {
         Date d = (Date)SafeConstructorImpl.constructYamlTimestamp(ctor,node);
         Calendar c = Calendar.getInstance();
         c.setTime(d);
         IRuby runtime = ((JRubyConstructor)ctor).runtime;
         return runtime.getClass("Date").callMethod(runtime.getCurrentContext(),"new",new IRubyObject[]{runtime.newFixnum(c.get(Calendar.YEAR)),runtime.newFixnum(c.get(Calendar.MONTH)+1),runtime.newFixnum(c.get(Calendar.DAY_OF_MONTH)),});
     }
 
     public static Object constructYamlInt(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).runtime.newFixnum(((Long)SafeConstructorImpl.constructYamlInt(ctor,node)).longValue());
     }
     public static Object constructYamlFloat(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).runtime.newFloat(((Double)SafeConstructorImpl.constructYamlFloat(ctor,node)).doubleValue());
     }
     public static Object constructYamlBinary(final Constructor ctor, final Node node) {
         return ((JRubyConstructor)ctor).runtime.newString(((String)SafeConstructorImpl.constructYamlBinary(ctor,node)));
     }
     public static Object constructJava(final Constructor ctor, final String pref, final Node node) {
         return SafeConstructorImpl.constructJava(ctor,pref,node);
     }
     public static Object constructRuby(final Constructor ctor, final String tag, final Node node) {
         final IRuby runtime = ((JRubyConstructor)ctor).runtime;
         RubyModule objClass = runtime.getModule("Object");
         if(tag != null) {
             final String[] nms = tag.split("::");
             for(int i=0,j=nms.length;i<j;i++) {
                 objClass = (RubyModule)objClass.getConstant(nms[i]);
             }
         }
         final RubyClass theCls = (RubyClass)objClass;
-        final RubyObject oo = (RubyObject)theCls.allocate();
+        final RubyObject oo = (RubyObject)theCls.getAllocator().allocate(runtime, theCls);
         final Map vars = (Map)(ctor.constructMapping(node));
         for(final Iterator iter = vars.keySet().iterator();iter.hasNext();) {
             final IRubyObject key = (IRubyObject)iter.next();
             oo.setInstanceVariable("@" + key.toString(),(IRubyObject)vars.get(key));
         }
         return oo;
     }
 
     public static Object constructRubyMap(final Constructor ctor, final String tag, final Node node) {
         final IRuby runtime = ((JRubyConstructor)ctor).runtime;
         RubyModule objClass = runtime.getModule("Object");
         if(tag != null) {
             final String[] nms = tag.split("::");
             for(int i=0,j=nms.length;i<j;i++) {
                 objClass = (RubyModule)objClass.getConstant(nms[i]);
             }
         }
         final RubyClass theCls = (RubyClass)objClass;
-        final RubyObject oo = (RubyObject)theCls.allocate();
+        final RubyObject oo = (RubyObject)theCls.getAllocator().allocate(runtime, theCls);
         final Map vars = (Map)(ctor.constructMapping(node));
         for(final Iterator iter = vars.keySet().iterator();iter.hasNext();) {
             final IRubyObject key = (IRubyObject)iter.next();
             oo.callMethod(oo.getRuntime().getCurrentContext(),"[]=", new IRubyObject[]{key,(IRubyObject)vars.get(key)});
         }
         return oo;
     }
 
     public static Object constructRubySequence(final Constructor ctor, final String tag, final Node node) {
         final IRuby runtime = ((JRubyConstructor)ctor).runtime;
         RubyModule objClass = runtime.getModule("Object");
         if(tag != null) {
             final String[] nms = tag.split("::");
             for(int i=0,j=nms.length;i<j;i++) {
                 objClass = (RubyModule)objClass.getConstant(nms[i]);
             }
         }
         final RubyClass theCls = (RubyClass)objClass;
-        final RubyObject oo = (RubyObject)theCls.allocate();
+        final RubyObject oo = (RubyObject)theCls.getAllocator().allocate(runtime, theCls);
         final List vars = (List)(ctor.constructSequence(node));
         for(final Iterator iter = vars.iterator();iter.hasNext();) {
             oo.callMethod(oo.getRuntime().getCurrentContext(),"<<", new IRubyObject[]{(IRubyObject)iter.next()});;
         }
         return oo;
     }
 
     static {
         addConstructor("tag:yaml.org,2002:null",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlNull(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:bool",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlBool(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:omap",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlOmap(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:pairs",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlPairs(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:set",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlSet(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:int",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlInt(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:float",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlFloat(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:timestamp",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlTimestamp(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:timestamp#ymd",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlTimestampYMD(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:str",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlStr(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:binary",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlBinary(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:seq",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlSeq(self,node);
                 }
             });
         addConstructor("tag:yaml.org,2002:map",new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return constructYamlMap(self,node);
                 }
             });
         addConstructor(null,new YamlConstructor() {
                 public Object call(final Constructor self, final Node node) {
                     return self.constructPrivateType(node);
                 }
             });
         addMultiConstructor("tag:yaml.org,2002:map:",new YamlMultiConstructor() {
                 public Object call(final Constructor self, final String pref, final Node node) {
                     return constructRubyMap(self,pref,node);
                 }
             });
         addMultiConstructor("tag:yaml.org,2002:seq:",new YamlMultiConstructor() {
                 public Object call(final Constructor self, final String pref, final Node node) {
                     return constructRubySequence(self,pref,node);
                 }
             });
         addMultiConstructor("tag:yaml.org,2002:ruby/object:",new YamlMultiConstructor() {
                 public Object call(final Constructor self, final String pref, final Node node) {
                     return constructRuby(self,pref,node);
                 }
             });
         addMultiConstructor("tag:yaml.org,2002:java/object:",new YamlMultiConstructor() {
                 public Object call(final Constructor self, final String pref, final Node node) {
                     return constructJava(self,pref,node);
                 }
             });
     }
 }// JRubyConstructor
 
diff --git a/test/org/jruby/test/MockRubyObject.java b/test/org/jruby/test/MockRubyObject.java
index e6dbdd5a8e..926a51c4f8 100644
--- a/test/org/jruby/test/MockRubyObject.java
+++ b/test/org/jruby/test/MockRubyObject.java
@@ -1,32 +1,33 @@
 package org.jruby.test;
 
 import org.jruby.IRuby;
 import org.jruby.RubyClass;
 import org.jruby.RubyObject;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class MockRubyObject extends RubyObject {
 
 	private final IRuby runtime;
 
 	private static class TestMeta extends RubyClass {
 
 		protected TestMeta(IRuby runtime) {
-			super(runtime, runtime.getObject());
+            // This null doesn't feel right
+			super(runtime, runtime.getObject(), null);
 		}
 	}
 	
 	public MockRubyObject(IRuby runtime) {
 		super(runtime, new TestMeta(runtime));
 		this.runtime = runtime;
 	}
 	
 	public IRuby getRuntime() {
 		return runtime;
 	}
 	
 	public static void throwException(IRubyObject recv) {
 		throw new RuntimeException("x");
 	}
 
 }
