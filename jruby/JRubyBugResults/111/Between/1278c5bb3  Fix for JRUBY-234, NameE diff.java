diff --git a/src/org/jruby/IRuby.java b/src/org/jruby/IRuby.java
index f8ab31e95a..07a344d804 100644
--- a/src/org/jruby/IRuby.java
+++ b/src/org/jruby/IRuby.java
@@ -1,399 +1,399 @@
 package org.jruby;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
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
 	public RubyClass defineClass(String name, RubyClass superClass);
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass, SinglyLinkedList parentCRef);
 
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
 
 	public RubyFileStat newRubyFileStat(File file);
 
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
 
-    public RaiseException newNoMethodError(String message);
+    public RaiseException newNoMethodError(String message, String name);
 
-    public RaiseException newNameError(String message);
+    public RaiseException newNameError(String message, String name);
 
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
 }
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index ca48f3e217..31b91ad3a3 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1388 +1,1388 @@
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
 import org.jruby.ast.executable.InstructionCompiler2;
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
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.SocketLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.ext.openssl.RubyOpenSSL;
 import org.jruby.libraries.DigestLibrary;
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
     private String currentDirectory = JRubyFile.getFileProperty("user.dir");
     
     private long startTime = System.currentTimeMillis();
     
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
     
     private IRubyObject tmsStruct;
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err) {
         this.in = in;
         this.out = out;
         this.err = err;
         
         objectSpaceEnabled = true;
     }
 
     /**
      * Create and initialize a new jruby Runtime.
      */
     private Ruby(InputStream in, PrintStream out, PrintStream err, boolean osEnabled) {
         this.in = in;
         this.out = out;
         this.err = err;
         
         objectSpaceEnabled = osEnabled;
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
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err, boolean osEnabled) {
         Ruby ruby = new Ruby(in, out, err, osEnabled);
         ruby.init();
         
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static IRuby newInstance(InputStream in, PrintStream out, PrintStream err) {
         return newInstance(in, out, err, true);
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
 	            return (IRubyObject)je.getSecondaryData();
         	} 
 
             throw je;
 		}
     }
 
     public IRubyObject compileAndRun(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             String classname = node.getPosition().getFile();
             if (classname.endsWith(".rb")) {
                 classname = classname.substring(0, classname.length() - 3);
             }
             InstructionCompiler2 compiler = new InstructionCompiler2();
             compiler.compile(classname, node.getPosition().getFile(), node);
             
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
         loadService.registerBuiltin("yaml_internal.rb", new YamlLibrary());
         loadService.registerBuiltin("enumerator.rb", new EnumeratorLibrary());
         loadService.registerBuiltin("readline.rb", new Readline.Service());
         loadService.registerBuiltin("openssl.so", new RubyOpenSSL.Service());
         loadService.registerBuiltin("digest.so", new DigestLibrary());
         loadService.registerBuiltin("digest.rb", new DigestLibrary());
         loadService.registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         loadService.registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         loadService.registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         loadService.registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
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
         RubyClass structClass = RubyStruct.createStructClass(this);
         
         tmsStruct = RubyStruct.newInstance(structClass,
                 new IRubyObject[] {
             newString("Tms"),
             newSymbol("utime"),
             newSymbol("stime"),
             newSymbol("cutime"),
             newSymbol("cstime")});
         
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
-        RubyClass nameError = defineClass("NameError", standardError);
+        RubyClass nameError = RubyNameError.createNameErrorClass(this, standardError);
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
         createSysErr(IErrno.EADDRINUSE, "EADDRINUSE");
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
 
-    public RaiseException newNoMethodError(String message) {
-    	return newRaiseException(getClass("NoMethodError"), message);
+    public RaiseException newNoMethodError(String message, String name) {
+        return new RaiseException(new RubyNameError(this, this.getClass("NoMethodError"), message, name), true);
     }
 
-    public RaiseException newNameError(String message) {
-    	return newRaiseException(getClass("NameError"), message);
+    public RaiseException newNameError(String message, String name) {
+        return new RaiseException(new RubyNameError(this, this.getClass("NameError"), message, name), true);
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
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index ee54db7309..00aceb17db 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1031 +1,1031 @@
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
 import org.jruby.runtime.ICallable;
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
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
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
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineModuleFunction("rand", callbackFactory.getOptSingletonMethod("rand"));
         module.defineModuleFunction("readline", callbackFactory.getOptSingletonMethod("readline"));
         module.defineModuleFunction("readlines", callbackFactory.getOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRubyObject.class));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRubyObject.class));
         module.defineModuleFunction("select", callbackFactory.getOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRubyObject.class));
         module.defineModuleFunction("sleep", callbackFactory.getSingletonMethod("sleep", IRubyObject.class));
         module.defineModuleFunction("split", callbackFactory.getOptSingletonMethod("split"));
         module.defineModuleFunction("sprintf", callbackFactory.getOptSingletonMethod("sprintf"));
         module.defineModuleFunction("srand", callbackFactory.getOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineModuleFunction("system", callbackFactory.getOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineModuleFunction("exec", callbackFactory.getOptSingletonMethod("system"));
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
         String description = recv.callMethod(runtime.getCurrentContext(), "inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
             noClass ? "" : ":", noClass ? "" : recv.getType().getName()});
 
-        throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg) : runtime.newNoMethodError(msg);
+        throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
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
         IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
         
         if (value.isNil()) {
             ICallable method = object.getMetaClass().searchMethod("to_a");
             
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
         return object.callMethod(recv.getRuntime().getCurrentContext(), "to_f");
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             String val = object.toString();
             if(val.length() > 0 && val.charAt(0) == '0') {
                 if(val.length() > 1) {
                     if(val.charAt(1) == 'x') {
                         return recv.getRuntime().newString(val.substring(2)).callMethod(context,"to_i", recv.getRuntime().newFixnum(16));
                     } else if(val.charAt(1) == 'b') {
                         return recv.getRuntime().newString(val.substring(2)).callMethod(context,"to_i", recv.getRuntime().newFixnum(2));
                     } else {
                         return recv.getRuntime().newString(val.substring(1)).callMethod(context,"to_i", recv.getRuntime().newFixnum(8));
                     }
                 }
             }
         }
         return object.callMethod(context, "to_i");
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
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
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
 
         return ((StringMetaClass)str.getMetaClass()).format.call(recv.getRuntime().getCurrentContext(), str, str.getMetaClass(), "%", new IRubyObject[] {newArgs}, false);
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
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException(RubyException.newInstance(runtime.getClass("RuntimeError"), args));
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
     public static IRubyObject require(IRubyObject recv, IRubyObject name) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args) {
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
 
         src.checkSafeString();
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = recv.getRuntime().newBinding();
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return context.yield(tag);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ThrowJump) {
 	            if (je.getPrimaryData().equals(tag.asSymbol())) {
 	                return (IRubyObject)je.getSecondaryData();
 	            }
         	}
        		throw je;
         } finally {
        		context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args) {
         IRuby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setPrimaryData(tag);
                 je.setSecondaryData(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
-        throw runtime.newNameError(message);
+        throw runtime.newNameError(message, tag);
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
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             context.yield(recv.getRuntime().getNil());
 
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
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 728fa3f09d..bce5c199e7 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1581 +1,1590 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.CallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperCallable;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ICallable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
+import org.jruby.exceptions.RaiseException;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     private static final String CVAR_TAINT_ERROR =
         "Insecure: can't modify class variable";
     private static final String CVAR_FREEZE_ERROR = "class/module";
-    
+
     // superClass may be null.
     private RubyClass superClass;
-    
+
     // Containing class...The parent of Object is null. Object should always be last in chain.
     //public RubyModule parentModule;
-    
+
     // CRef...to eventually replace parentModule
     public SinglyLinkedList cref;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
 
     protected RubyModule(IRuby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass);
-        
+
         this.superClass = superClass;
         //this.parentModule = parentModule;
-		
+
         setBaseName(name);
 
         // If no parent is passed in, it is safe to assume Object.
         if (parentCRef == null) {
             if (runtime.getObject() != null) {
                 parentCRef = runtime.getObject().getCRef();
             }
         }
         this.cref = new SinglyLinkedList(this, parentCRef);
     }
-    
+
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     private void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
-    
+
     public RubyModule getParent() {
         if (cref.getNext() == null) {
             return null;
         }
 
         return (RubyModule)cref.getNext().getValue();
     }
 
     public void setParent(RubyModule p) {
         cref.setNext(p.getCRef());
     }
 
     public Map getMethods() {
         return methods;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
-    
+
     public RubyModule getNonIncludedClass() {
         return this;
     }
- 
+
     public String getBaseName() {
         return classId;
     }
-    
+
     public void setBaseName(String name) {
         classId = name;
     }
-    
+
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
-        	if (isClass()) {
+            if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
-        	} else {
+            } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
-        	}
+            }
         }
-        
+
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
-        
+
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             result.insert(0, "::").insert(0, p.getBaseName());
         }
 
         return result.toString();
     }
 
-    /** 
+    /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
-        
+
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
-        
+
         return includedModule;
     }
-    
+
     /**
      * Search this and parent modules for the named variable.
      * 
      * @param name The variable to search for
      * @return The module in which that variable is found, or null if not found
      */
     private RubyModule getModuleWithInstanceVar(String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getInstanceVariable(name) != null) {
                 return p;
             }
         }
         return null;
     }
 
-    /** 
+    /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = getModuleWithInstanceVar(name);
-        
+
         if (module == null) {
             module = this;
         }
 
         return module.setInstanceVariable(name, value, CVAR_TAINT_ERROR, CVAR_FREEZE_ERROR);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         RubyModule module = getModuleWithInstanceVar(name);
-        
+
         if (module != null) {
-        	IRubyObject variable = module.getInstanceVariable(name); 
-        	
+            IRubyObject variable = module.getInstanceVariable(name);
+
             return variable == null ? getRuntime().getNil() : variable;
         }
-        
-        throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName());
+
+        throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
-    /** 
+    /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         return getModuleWithInstanceVar(name) != null;
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      * @see RubyObject#setInstanceVariable(String, IRubyObject, String, String)
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
-        IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant", 
+        IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant",
                 "class/module");
-        
+
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return result;
     }
-    
+
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
-    	IRubyObject module = getConstantAt(name);
-    	
-    	return  (module instanceof RubyClass) ? (RubyClass) module : null;
+        IRubyObject module = getConstantAt(name);
+
+        return  (module instanceof RubyClass) ? (RubyClass) module : null;
     }
 
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     public IRubyObject const_missing(IRubyObject name) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
-            throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol());
-        } 
+            throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
+        }
 
-        throw getRuntime().newNameError("uninitialized constant " + name.asSymbol());
+        throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
-    /** 
+    /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
-        
+
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
-        
+
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
-    		return;
-    	}
-        
+            return;
+        }
+
         infectBy(module);
-        
+
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
-        
+
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
-            
-            boolean superclassSeen = false;            
+
+            boolean superclassSeen = false;
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
-                c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(), 
+                c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
-            for (Iterator iter = ((RubyModule) arg).getMethods().keySet().iterator(); 
+            for (Iterator iter = ((RubyModule) arg).getMethods().keySet().iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
         }
- 
+
     }
 
     public void defineMethod(String name, Callback method) {
-        Visibility visibility = name.equals("initialize") ? 
-        		Visibility.PRIVATE : Visibility.PUBLIC;
+        Visibility visibility = name.equals("initialize") ?
+                Visibility.PRIVATE : Visibility.PUBLIC;
 
         addMethod(name, new CallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new CallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         IRuby runtime = getRuntime();
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
         ICallable method = searchMethod(name);
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
 
-            throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'");
+            throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     private void addCachedMethod(String name, ICallable method) {
         // Included modules modify the original 'included' modules class.  Since multiple
-    	// classes can include the same module, we cannot cache in the original included module.
+        // classes can include the same module, we cannot cache in the original included module.
         if (!isIncluded()) {
             getMethods().put(name, method);
             getRuntime().getCacheMap().add(method, this);
         }
     }
-    
+
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, ICallable method) {
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
             ICallable existingMethod = (ICallable) getMethods().remove(name);
             if (existingMethod != null) {
-    	        getRuntime().getCacheMap().remove(name, existingMethod);
+                getRuntime().getCacheMap().remove(name, existingMethod);
             }
 
             getMethods().put(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
-    	getMethods().remove(name);
+        getMethods().remove(name);
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
             ICallable method = (ICallable) getMethods().remove(name);
             if (method == null) {
-                throw getRuntime().newNameError("method '" + name + "' not defined in " + getName());
+                throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
-        
+
             getRuntime().getCacheMap().remove(name, method);
         }
     }
-    
+
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public ICallable searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 ICallable method = (ICallable) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
-                    
+
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
-    
+
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public ICallable retrieveMethod(String name) {
         return (ICallable)getMethods().get(name);
     }
-    
+
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
-    
+
     public void addModuleFunction(String name, ICallable method) {
         addMethod(name, method);
         addSingletonMethod(name, method);
-    }   
+    }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         defineSingletonMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         defineSingletonMethod(name, method);
     }
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
-        retry: while (true) { 
+        retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
-            
+
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
-                        getRuntime().getWarnings().warn("toplevel constant " + name + 
+                        getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
-            
+
                     return constant;
                 }
                 p = p.getSuperClass();
             }
-            
+
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
-            
+
             break;
         }
-        
+
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
-    
+
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
-    
+
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
-    	return getInstanceVariable(name);
+        return getInstanceVariable(name);
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
         ICallable method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
-            
+
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
-                        (isModule() ? "module" : "class") + " `" + getName() + "'");
+                        (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         getMethods().put(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         IRubyObject type = getConstantAt(name);
-        
+
         if (type == null) {
-            return (RubyClass) setConstant(name, 
-            		getRuntime().defineClassUnder(name, superClazz, cref)); 
+            return (RubyClass) setConstant(name,
+                    getRuntime().defineClassUnder(name, superClazz, cref));
         }
 
         if (!(type instanceof RubyClass)) {
-        	throw getRuntime().newTypeError(name + " is not a class.");
+            throw getRuntime().newTypeError(name + " is not a class.");
         }
-            
+
         return (RubyClass) type;
     }
-    
+
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz) {
-    	IRubyObject type = getConstantAt(name);
-    	
-    	if (type == null) {
-            return (RubyClass) setConstant(name, 
-            		getRuntime().defineClassUnder(name, superClazz, cref)); 
-    	}
-
-    	if (!(type instanceof RubyClass)) {
-    		throw getRuntime().newTypeError(name + " is not a class.");
+        IRubyObject type = getConstantAt(name);
+
+        if (type == null) {
+            return (RubyClass) setConstant(name,
+                    getRuntime().defineClassUnder(name, superClazz, cref));
+        }
+
+        if (!(type instanceof RubyClass)) {
+            throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
-        	throw getRuntime().newNameError(name + " is already defined.");
-        } 
-            
-    	return (RubyClass) type;
+            throw getRuntime().newNameError(name + " is already defined.", name);
+        }
+
+        return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
-        
+
         if (type == null) {
-            return (RubyModule) setConstant(name, 
-            		getRuntime().defineModuleUnder(name, cref)); 
+            return (RubyModule) setConstant(name,
+                    getRuntime().defineModuleUnder(name, cref));
         }
 
         if (!(type instanceof RubyModule)) {
-        	throw getRuntime().newTypeError(name + " is not a module.");
-        } 
+            throw getRuntime().newTypeError(name + " is not a module.");
+        }
 
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
-            throw getRuntime().newNameError("bad constant name " + name);
+            throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
-            throw getRuntime().newNameError("wrong class variable name " + name.asSymbol());
+            throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
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
-            throw getRuntime().newNameError("cannot remove " + name.asSymbol() + " for " + getName());
+            throw cannotRemoveError(name.asSymbol());
         }
 
-        throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName());
+        throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
-        
+
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
-		final IRuby runtime = getRuntime();
+        final IRuby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
-                	checkArgumentCount(args, 0, 0);
+                    checkArgumentCount(args, 0, 0);
 
-		    	    IRubyObject variable = self.getInstanceVariable(variableName);
-		    	
-		            return variable == null ? runtime.getNil() : variable;
+                    IRubyObject variable = self.getInstanceVariable(variableName);
+
+                    return variable == null ? runtime.getNil() : variable;
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
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
-					IRubyObject[] fargs = runtime.getCurrentContext().getFrameArgs();
-					
-			        if (fargs.length != 1) {
-			            throw runtime.newArgumentError("wrong # of arguments(" + fargs.length + "for 1)");
-			        }
+                    IRubyObject[] fargs = runtime.getCurrentContext().getFrameArgs();
 
-			        return self.setInstanceVariable(variableName, fargs[0]);
+                    if (fargs.length != 1) {
+                        throw runtime.newArgumentError("wrong # of arguments(" + fargs.length + "for 1)");
+                    }
+
+                    return self.setInstanceVariable(variableName, fargs[0]);
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
 
         ICallable method = searchMethod(name);
 
         if (method.isUndefined()) {
-            throw getRuntime().newNameError("undefined method '" + name + "' for " + 
-                                (isModule() ? "module" : "class") + " '" + getName() + "'");
+            throw getRuntime().newNameError("undefined method '" + name + "' for " +
+                                (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 final ThreadContext context = getRuntime().getCurrentContext();
                 addMethod(name, new CallbackMethod(this, new Callback() {
-	                public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
-				        return context.callSuper(context.getFrameArgs());
-	                }
-
-	                public Arity getArity() {
-	                    return Arity.optional();
-	                }
-	            }, visibility));
+                    public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
+                        return context.callSuper(context.getFrameArgs());
+                    }
+
+                    public Arity getArity() {
+                        return Arity.optional();
+                    }
+                }, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         ICallable method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         ICallable method = searchMethod(name);
         if (method.isUndefined()) {
-            throw getRuntime().newNameError("undefined method `" + name + 
-                "' for class `" + this.getName() + "'");
+            throw getRuntime().newNameError("undefined method `" + name +
+                "' for class `" + this.getName() + "'", name);
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
 
     // What is argument 1 for in this method?
     public IRubyObject define_method(IRubyObject[] args) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         ICallable newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) {
             visibility = Visibility.PRIVATE;
         }
-        
-        
+
+
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc() : (RubyProc)args[1];
             body = proc;
-            
+
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setLastClass(this);
             proc.getBlock().getFrame().setLastFunc(name);
-            
+
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
-            
+
             newMethod = new MethodMethod(this, method.unbind(), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
-        
+
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperCallable(getSingletonClass(), newMethod, Visibility.PUBLIC));
             callMethod(context, "singleton_method_added", symbol);
         }
 
         callMethod(context, "method_added", symbol);
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args) {
         ThreadContext context = getRuntime().getCurrentContext();
-        
+
         context.preExecuteUnder(this);
 
         try {
             return method.execute(this, args);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(IRuby runtime, String name) {
         return newModule(runtime, name, null);
     }
 
     public static RubyModule newModule(IRuby runtime, String name, SinglyLinkedList parentCRef) {
         // Modules do not directly define Object as their superClass even though in theory they
-    	// should.  The C version of Ruby may also do this (special checks in rb_alias for Module
-    	// makes me think this).
+        // should.  The C version of Ruby may also do this (special checks in rb_alias for Module
+        // makes me think this).
         // TODO cnutter: Shouldn't new modules have Module as their superclass?
         RubyModule newModule = new RubyModule(runtime, runtime.getClass("Module"), null, parentCRef, name);
         ThreadContext tc = runtime.getCurrentContext();
         if (tc.isBlockGiven()) {
             tc.yieldCurrentBlock(null, newModule, newModule, false);
         }
         return newModule;
     }
-    
+
     public RubyString name() {
-    	return getRuntime().newString(getBaseName() == null ? "" : getName());
+        return getRuntime().newString(getBaseName() == null ? "" : getName());
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
 
     /** rb_mod_clone
      *
      */
     public IRubyObject rbClone() {
         return cloneMethods((RubyModule) super.rbClone());
     }
-    
+
     protected IRubyObject cloneMethods(RubyModule clone) {
-    	RubyModule realType = this.getNonIncludedClass();
+        RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             ICallable method = (ICallable) entry.getValue();
 
             // Do not clone cached methods
-            if (method.getImplementationClass() == realType) {            
+            if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make ICallable immutable
                 ICallable clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.getMethods().put(entry.getKey(), clonedMethod);
             }
         }
-        
+
         return clone;
     }
-    
+
     protected IRubyObject doClone() {
-    	return RubyModule.newModule(getRuntime(), getBaseName(), cref.getNext());
+        return RubyModule.newModule(getRuntime(), getBaseName(), cref.getNext());
     }
 
     /** rb_mod_dup
      *
      */
     public IRubyObject dup() {
         RubyModule dup = (RubyModule) rbClone();
         dup.setMetaClass(getMetaClass());
         dup.setFrozen(false);
         // +++ jpetersen
         // dup.setSingleton(isSingleton());
         // --- jpetersen
 
         return dup;
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
-    
+
     public List getAncestorList() {
         ArrayList list = new ArrayList();
-        
+
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
-        
+
         return list;
     }
-    
+
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
-        
+
         return false;
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
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
-       
+
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
-       
+
        return getRuntime().getNil();
    }
 
    /** rb_mod_lt
     *
     */
    public IRubyObject op_lt(IRubyObject obj) {
-    return obj == this ? getRuntime().getFalse() : op_le(obj); 
+    return obj == this ? getRuntime().getFalse() : op_le(obj);
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
-       
+
        RubyModule module = (RubyModule)obj;
-       
+
        if (module.isKindOfModule(this)) {
            return getRuntime().newFixnum(1);
        } else if (this.isKindOfModule(module)) {
            return getRuntime().newFixnum(-1);
        }
-       
+
        return getRuntime().getNil();
    }
 
    public boolean isKindOfModule(RubyModule type) {
-       for (RubyModule p = this; p != null; p = p.getSuperClass()) { 
+       for (RubyModule p = this; p != null; p = p.getSuperClass()) {
            if (p.isSame(type)) {
                return true;
            }
        }
-       
+
        return false;
    }
-   
+
    public boolean isSame(RubyModule module) {
        return this == module;
    }
 
     /** rb_mod_initialize
      *
      */
     public IRubyObject initialize(IRubyObject[] args) {
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
-    	checkArgumentCount(args, 1, 2);
+        checkArgumentCount(args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
-        
+
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
-            throw getRuntime().newNameError("wrong constant name " + name);
+            throw wrongConstantNameError(name);
         }
 
         return getConstant(name);
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
-            throw getRuntime().newNameError("wrong constant name " + name);
+            throw wrongConstantNameError(name);
         }
 
-        return setConstant(name, value); 
+        return setConstant(name, value);
     }
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
-            throw getRuntime().newNameError("wrong constant name " + name);
+            throw wrongConstantNameError(name);
         }
 
         return getRuntime().newBoolean(getConstantAt(name) != null);
     }
 
+    private RaiseException wrongConstantNameError(String name) {
+        return getRuntime().newNameError("wrong constant name " + name, name);
+    }
+
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap undefinedMethods = new HashMap();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
-        	RubyModule realType = type.getNonIncludedClass();
+            RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 ICallable method = (ICallable) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (method.isUndefined()) {
-                	undefinedMethods.put(methodName, Boolean.TRUE);
-                	continue;
+                    undefinedMethods.put(methodName, Boolean.TRUE);
+                    continue;
                 }
-                if (method.getImplementationClass() == realType && 
+                if (method.getImplementationClass() == realType &&
                     method.getVisibility().is(visibility) && undefinedMethods.get(methodName) == null) {
-                	RubyString name = getRuntime().newString(methodName);
+                    RubyString name = getRuntime().newString(methodName);
 
-                	if (!ary.includes(name)) {
-                		ary.append(name);
-                	}
-                } 
+                    if (!ary.includes(name)) {
+                        ary.append(name);
+                    }
+                }
             }
-				
+
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
-    	return instance_methods(args, Visibility.PUBLIC);
+        return instance_methods(args, Visibility.PUBLIC);
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
-        
+
         if (getRuntime().getClass("Module") == this) {
-            for (Iterator vars = objectClass.instanceVariableNames(); 
-            	vars.hasNext();) {
+            for (Iterator vars = objectClass.instanceVariableNames();
+                 vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
-            
+
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
-            
+
             for (Iterator vars = p.instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
         }
-        
+
         return getRuntime().newArray(constantNames);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject remove_class_variable(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isClassVariable(id)) {
-            throw getRuntime().newNameError("wrong class variable name " + id);
+            throw getRuntime().newNameError("wrong class variable name " + id, id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
-        IRubyObject variable = removeInstanceVariable(id); 
+        IRubyObject variable = removeInstanceVariable(id);
         if (variable != null) {
             return variable;
         }
 
         if (isClassVarDefined(id)) {
-            throw getRuntime().newNameError("cannot remove " + id + " for " + getName());
+            throw cannotRemoveError(id);
         }
-        throw getRuntime().newNameError("class variable " + id + " not defined for " + getName());
+        throw getRuntime().newNameError("class variable " + id + " not defined for " + getName(), id);
     }
-    
+
+    private RaiseException cannotRemoveError(String id) {
+        return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
+    }
+
     public IRubyObject remove_const(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isConstant(id)) {
-            throw getRuntime().newNameError("wrong constant name " + id);
+            throw wrongConstantNameError(id);
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
-            throw getRuntime().newNameError("cannot remove " + id + " for " + getName());
+            throw cannotRemoveError(id);
         }
-        throw getRuntime().newNameError("constant " + id + " not defined for " + getName());
+        throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
     }
-    
+
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
         obj.extendObject(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
-        
+
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
-    
+
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
-    
+
     public IRubyObject extended(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             getRuntime().getCurrentContext().setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
-    
+
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
-        
+
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 ICallable method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperCallable(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
-    
+
     public IRubyObject method_added(IRubyObject nothing) {
-    	return getRuntime().getNil();
+        return getRuntime().getNil();
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
 
     public IRubyObject module_eval(IRubyObject[] args) {
         return specificEval(this, args);
     }
 
     public RubyModule remove_method(IRubyObject name) {
         removeMethod(name.asSymbol());
         return this;
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('m');
         output.dumpString(name().toString());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = input.unmarshalString();
         IRuby runtime = input.getRuntime();
         RubyModule result = runtime.getClassFromPath(name);
         if (result == null) {
-            throw runtime.newNameError("uninitialized constant " + name);
+            throw runtime.newNameError("uninitialized constant " + name, name);
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 
     public IRubyObject inspect() {
         return callMethod(getRuntime().getCurrentContext(), "to_s");
     }
 }
diff --git a/src/org/jruby/RubyNameError.java b/src/org/jruby/RubyNameError.java
new file mode 100644
index 0000000000..2b393d3e84
--- /dev/null
+++ b/src/org/jruby/RubyNameError.java
@@ -0,0 +1,86 @@
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
+ * Copyright (C) 2006 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
+import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.runtime.CallbackFactory;
+
+/**
+ * @author Anders Bengtsson
+ */
+public class RubyNameError extends RubyException {
+    private IRubyObject name;
+
+    public static RubyClass createNameErrorClass(IRuby runtime, RubyClass standardErrorClass) {
+        RubyClass nameErrorClass = runtime.defineClass("NameError", standardErrorClass);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyNameError.class);
+        
+        nameErrorClass.defineSingletonMethod("new", 
+                callbackFactory.getOptSingletonMethod("newRubyNameError"));		
+        nameErrorClass.defineSingletonMethod("exception", 
+				callbackFactory.getOptSingletonMethod("newRubyNameError"));		
+
+        nameErrorClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
+        nameErrorClass.defineMethod("name", callbackFactory.getMethod("name"));
+
+        return nameErrorClass;
+    }
+
+    protected RubyNameError(IRuby runtime, RubyClass exceptionClass) {
+        this(runtime, exceptionClass, "NameError");
+    }
+
+    public RubyNameError(IRuby runtime, RubyClass exceptionClass, String message) {
+        this(runtime, exceptionClass, message, null);
+    }
+
+    public RubyNameError(IRuby runtime, RubyClass exceptionClass, String message, String name) {
+        super(runtime, exceptionClass, message);
+        this.name = name == null ? runtime.getNil() : runtime.newString(name);
+    }
+    
+    public static RubyNameError newRubyNameError(IRubyObject recv, IRubyObject[] args) {
+        IRuby runtime = recv.getRuntime();
+        RubyNameError newError = new RubyNameError(runtime, runtime.getClass("NameError"));
+        newError.callInit(args);
+        return newError;
+    }
+
+    public IRubyObject initialize(IRubyObject[] args) {
+        super.initialize(args);
+        if (args.length > 1) {
+            name = args[1];
+        }
+        return this;
+    }
+
+    public IRubyObject name() {
+        return name;
+    }
+}
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 4ad35a13ff..c8fc825e7d 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1201 +1,1201 @@
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
 
     // Object identity, initialized on demand
     private long id = 0;
 
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
 
     public void addSingletonMethod(String name, ICallable method) {
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
         ICallable method = null;
 
         method = rubyclass.searchMethod(name);
 
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
-                throw getRuntime().newNameError("super: no superclass method '" + name + "'");
+                throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
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
-    		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
+    		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
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
-    		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name");
+    		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
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
         if (id == 0) {
             id = getRuntime().getObjectSpace().createId(this);
         }
         return getRuntime().newFixnum(id);
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
     	return getMetaClass().getRealClass().allocate();
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
                     part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                     sep = ", ";
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
-        	throw getRuntime().newNameError(msg);
+        	throw getRuntime().newNameError(msg, name);
         }
-        throw getRuntime().newNoMethodError(msg);
+        throw getRuntime().newNoMethodError(msg, name);
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
-           throw getRuntime().newNameError("wrong instance variable name " + id);
+           throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
-       throw getRuntime().newNameError("instance variable " + id + " not defined");
+       throw getRuntime().newNameError("instance variable " + id + " not defined", id);
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
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index ef03eb4adc..5bfea21548 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,440 +1,445 @@
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
+import org.jruby.exceptions.RaiseException;
 
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
 
         structClass.defineMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
 
         structClass.defineMethod("to_s", callbackFactory.getMethod("to_s"));
         structClass.defineMethod("inspect", callbackFactory.getMethod("inspect"));
         structClass.defineMethod("to_a", callbackFactory.getMethod("to_a"));
         structClass.defineMethod("values", callbackFactory.getMethod("to_a"));
         structClass.defineMethod("size", callbackFactory.getMethod("size"));
         structClass.defineMethod("length", callbackFactory.getMethod("size"));
 
         structClass.defineMethod("each", callbackFactory.getMethod("each"));
         structClass.defineMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
         structClass.defineMethod("[]=", callbackFactory.getMethod("aset", IRubyObject.class, IRubyObject.class));
 
         structClass.defineMethod("members", callbackFactory.getMethod("members"));
 
         return structClass;
     }
 
     private static IRubyObject getInstanceVariable(RubyClass type, String name) {
         RubyClass structClass = type.getRuntime().getClass("Struct");
 
         while (type != null && type != structClass) {
-        	IRubyObject variable = type.getInstanceVariable(name);
+            IRubyObject variable = type.getInstanceVariable(name);
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
-    	testFrozen("Struct is frozen");
+        testFrozen("Struct is frozen");
 
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
 
-        throw getRuntime().newNameError(name + " is not struct member");
+        throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0; i < member.getLength(); i++) {
             if (member.entry(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
-        throw getRuntime().newNameError(name + " is not struct member");
+        throw notStructMemberError(name);
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
 
         if (name == null) {
             newStruct = new RubyClass((RubyClass) recv);
         } else {
             if (!IdUtil.isConstant(name)) {
-                throw recv.getRuntime().newNameError("identifier " + name + " needs to be constant");
+                throw recv.getRuntime().newNameError("identifier " + name + " needs to be constant", name);
             }
             newStruct = ((RubyClass) recv).defineClassUnder(name, (RubyClass) recv);
         }
 
         newStruct.setInstanceVariable("__size__", member.length());
         newStruct.setInstanceVariable("__member__", member);
 
         CallbackFactory callbackFactory = recv.getRuntime().callbackFactory(RubyStruct.class);
-		newStruct.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
+        newStruct.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.defineSingletonMethod("[]", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.defineSingletonMethod("members", callbackFactory.getSingletonMethod("members"));
 
         // define access methods.
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             String memberName = args[i].asSymbol();
             newStruct.defineMethod(memberName, callbackFactory.getMethod("get"));
             newStruct.defineMethod(memberName + "=", callbackFactory.getMethod("set", IRubyObject.class));
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
 
-        throw getRuntime().newNameError(name + " is not struct member");
+        throw notStructMemberError(name);
+    }
+
+    private RaiseException notStructMemberError(String name) {
+        return getRuntime().newNameError(name + " is not struct member", name);
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
 
-        throw getRuntime().newNameError(name + " is not struct member");
+        throw notStructMemberError(name);
     }
 
     public IRubyObject rbClone() {
         RubyStruct clone = new RubyStruct(getRuntime(), getMetaClass());
 
         clone.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, clone.values, 0, values.length);
-        
+
         clone.setFrozen(this.isFrozen());
         clone.setTaint(this.isTaint());
-        
+
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
-            throw runtime.newNameError("uninitialized constant " + className);
+            throw runtime.newNameError("uninitialized constant " + className, className.asSymbol());
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
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index decf56e234..6b0af6ae9f 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -463,1341 +463,1343 @@ public class EvaluationState {
                     args = setupArgs(context, iVisited.getArgsNode(), self);
                 } finally {
                     context.endCallArgs();
                 }
                 
                 assert receiver.getMetaClass() != null : receiver.getClass().getName();
                 // If reciever is self then we do the call the same way as vcall
                 CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
     
                 return receiver.callMethod(context, iVisited.getName(), args, callType);
             }
             case NodeTypes.CASENODE: {
                 CaseNode iVisited = (CaseNode) node;
                 IRubyObject expression = null;
                 if (iVisited.getCaseNode() != null) {
                     expression = evalInternal(context, iVisited.getCaseNode(), self);
                 }
     
                 context.pollThreadEvents();
     
                 IRubyObject result = runtime.getNil();
     
                 Node firstWhenNode = iVisited.getFirstWhenNode();
                 while (firstWhenNode != null) {
                     if (!(firstWhenNode instanceof WhenNode)) {
                         node = firstWhenNode;
                         continue bigloop;
                     }
     
                     WhenNode whenNode = (WhenNode) firstWhenNode;
     
                     if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                         for (Iterator iter = ((ArrayNode) whenNode.getExpressionNodes()).iterator(); iter
                                 .hasNext();) {
                             Node tag = (Node) iter.next();
     
                             context.setPosition(tag.getPosition());
                             if (isTrace(runtime)) {
                                 callTraceFunction(context, "line", self);
                             }
     
                             // Ruby grammar has nested whens in a case body because of
                             // productions case_body and when_args.
                             if (tag instanceof WhenNode) {
                                 RubyArray expressions = (RubyArray) evalInternal(context, ((WhenNode) tag)
                                                 .getExpressionNodes(), self);
     
                                 for (int j = 0; j < expressions.getLength(); j++) {
                                     IRubyObject condition = expressions.entry(j);
     
                                     if ((expression != null && condition.callMethod(context, "===", expression)
                                             .isTrue())
                                             || (expression == null && condition.isTrue())) {
                                         node = ((WhenNode) firstWhenNode).getBodyNode();
                                         continue bigloop;
                                     }
                                 }
                                 continue;
                             }
     
                             result = evalInternal(context, tag, self);
     
                             if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                                     || (expression == null && result.isTrue())) {
                                 node = whenNode.getBodyNode();
                                 continue bigloop;
                             }
                         }
                     } else {
                         result = evalInternal(context, whenNode.getExpressionNodes(), self);
     
                         if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                                 || (expression == null && result.isTrue())) {
                             node = ((WhenNode) firstWhenNode).getBodyNode();
                             continue bigloop;
                         }
                     }
     
                     context.pollThreadEvents();
     
                     firstWhenNode = whenNode.getNextCase();
                 }
     
                 return runtime.getNil();
             }
             case NodeTypes.CLASSNODE: {
                 ClassNode iVisited = (ClassNode) node;
                 Node superNode = iVisited.getSuperNode();
                 RubyClass superClass = superNode == null ? null : (RubyClass) evalInternal(context, superNode, self);
                 Node classNameNode = iVisited.getCPath();
                 String name = ((INameNode) classNameNode).getName();
                 RubyModule enclosingClass = getEnclosingModule(context, classNameNode, self);
                 RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
     
                 if (context.getWrapper() != null) {
                     rubyClass.extendObject(context.getWrapper());
                     rubyClass.includeModule(context.getWrapper());
                 }
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), rubyClass, self);
             }
             case NodeTypes.CLASSVARASGNNODE: {
                 ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
                 RubyModule rubyClass = (RubyModule) context.peekCRef().getValue();
     
                 if (rubyClass == null) {
                     rubyClass = self.getMetaClass();
                 } else if (rubyClass.isSingleton()) {
                     rubyClass = (RubyModule) rubyClass.getInstanceVariable("__attached__");
                 }
     
                 rubyClass.setClassVar(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.CLASSVARDECLNODE: {
     
                 ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
     
                 // FIXME: shouldn't we use cref here?
                 if (context.getRubyClass() == null) {
                     throw runtime.newTypeError("no class/module to define class variable");
                 }
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
                 ((RubyModule) context.peekCRef().getValue()).setClassVar(iVisited.getName(),
                         result);
     
                 return runtime.getNil();
             }
             case NodeTypes.CLASSVARNODE: {
                 ClassVarNode iVisited = (ClassVarNode) node;
                 RubyModule rubyClass = (RubyModule) context.peekCRef().getValue();
     
                 if (rubyClass == null) {
                     rubyClass = self.getMetaClass();
                 } else if (rubyClass.isSingleton()) {
                     rubyClass = (RubyModule)rubyClass.getInstanceVariable("__attached__");
                 }
                 
                     return rubyClass.getClassVar(iVisited.getName());
                     }
             case NodeTypes.COLON2NODE: {
                 Colon2Node iVisited = (Colon2Node) node;
                 Node leftNode = iVisited.getLeftNode();
     
                 // TODO: Made this more colon3 friendly because of cpath production
                 // rule in grammar (it is convenient to think of them as the same thing
                 // at a grammar level even though evaluation is).
                 if (leftNode == null) {
                     return runtime.getObject().getConstantFrom(iVisited.getName());
                 } else {
                     IRubyObject result = evalInternal(context, iVisited.getLeftNode(), self);
                     if (result instanceof RubyModule) {
                         return ((RubyModule) result).getConstantFrom(iVisited.getName());
                     } else {
                         return result.callMethod(context, iVisited.getName());
                     }
                 }
             }
             case NodeTypes.COLON3NODE: {
                 Colon3Node iVisited = (Colon3Node) node;
                 return runtime.getObject().getConstantFrom(iVisited.getName());
             }
             case NodeTypes.CONSTDECLNODE: {
                 ConstDeclNode iVisited = (ConstDeclNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
                 IRubyObject module;
     
                 if (iVisited.getPathNode() != null) {
                     module = evalInternal(context, iVisited.getPathNode(), self);
                 } else {
                     
     
                     // FIXME: why do we check RubyClass and then use CRef?
                     if (context.getRubyClass() == null) {
                         // TODO: wire into new exception handling mechanism
                         throw runtime.newTypeError("no class/module to define constant");
                     }
                     module = (RubyModule) context.peekCRef().getValue();
                 }
     
                 // FIXME: shouldn't we use the result of this set in setResult?
                 ((RubyModule) module).setConstant(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.CONSTNODE: {
                 ConstNode iVisited = (ConstNode) node;
                 return context.getConstant(iVisited.getName());
             }
             case NodeTypes.DASGNNODE: {
                 DAsgnNode iVisited = (DAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
 
                 // System.out.println("DSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
     
                 return result;
             }
             case NodeTypes.DEFINEDNODE: {
                 DefinedNode iVisited = (DefinedNode) node;
                 String definition = getDefinition(context, iVisited.getExpressionNode(), self);
                 if (definition != null) {
                     return runtime.newString(definition);
                 } else {
                     return runtime.getNil();
                 }
             }
             case NodeTypes.DEFNNODE: {
                 DefnNode iVisited = (DefnNode) node;
                 
                 RubyModule containingClass = context.getRubyClass();
     
                 if (containingClass == null) {
                     throw runtime.newTypeError("No class to add method.");
                 }
     
                 String name = iVisited.getName();
                 if (containingClass == runtime.getObject() && name.equals("initialize")) {
                     runtime.getWarnings().warn("redefining Object#initialize may cause infinite loop");
                 }
     
                 Visibility visibility = context.getCurrentVisibility();
                 if (name.equals("initialize") || visibility.isModuleFunction()) {
                     visibility = Visibility.PRIVATE;
                 }
     
                 DefaultMethod newMethod = new DefaultMethod(containingClass, iVisited.getScope(), 
                         iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), visibility, context.peekCRef());
     
                 if (iVisited.getBodyNode() != null) {
                     iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
                 }
     
                 containingClass.addMethod(name, newMethod);
     
                 if (context.getCurrentVisibility().isModuleFunction()) {
                     containingClass.getSingletonClass().addMethod(
                             name,
                             new WrapperCallable(containingClass.getSingletonClass(), newMethod,
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
             case NodeTypes.DEFSNODE: {
                 DefsNode iVisited = (DefsNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self);
     
                 if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                     throw runtime.newSecurityError("Insecure; can't define singleton method.");
                 }
                 if (receiver.isFrozen()) {
                     throw runtime.newFrozenError("object");
                 }
                 if (!receiver.singletonMethodsAllowed()) {
                     throw runtime.newTypeError("can't define singleton method \"" + iVisited.getName()
                             + "\" for " + receiver.getType());
                 }
     
                 RubyClass rubyClass = receiver.getSingletonClass();
     
                 if (runtime.getSafeLevel() >= 4) {
                     ICallable method = (ICallable) rubyClass.getMethods().get(iVisited.getName());
                     if (method != null) {
                         throw runtime.newSecurityError("Redefining method prohibited.");
                     }
                 }
     
                 DefaultMethod newMethod = new DefaultMethod(rubyClass, iVisited.getScope(), 
                         iVisited.getBodyNode(), (ArgsNode) iVisited.getArgsNode(), 
                         Visibility.PUBLIC, context.peekCRef());
 
                 if (iVisited.getBodyNode() != null) {
                     iVisited.getBodyNode().accept(new CreateJumpTargetVisitor(newMethod));
                 }
     
                 rubyClass.addMethod(iVisited.getName(), newMethod);
                 receiver.callMethod(context, "singleton_method_added", runtime.newSymbol(iVisited.getName()));
     
                 return runtime.getNil();
             }
             case NodeTypes.DOTNODE: {
                 DotNode iVisited = (DotNode) node;
                 return RubyRange.newRange(runtime, evalInternal(context, iVisited.getBeginNode(), self), evalInternal(context, iVisited
                                 .getEndNode(), self), iVisited.isExclusive());
             }
             case NodeTypes.DREGEXPNODE: {
                 DRegexpNode iVisited = (DRegexpNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self).toString());
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
 
                 return RubyRegexp.newRegexp(runtime, sb.toString(), iVisited.getOptions(), lang);
             }
             case NodeTypes.DSTRNODE: {
                 DStrNode iVisited = (DStrNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self).toString());
                 }
     
                 return runtime.newString(sb.toString());
             }
             case NodeTypes.DSYMBOLNODE: {
                 DSymbolNode iVisited = (DSymbolNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.getNode().iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self).toString());
                 }
     
                 return runtime.newSymbol(sb.toString());
             }
             case NodeTypes.DVARNODE: {
                 DVarNode iVisited = (DVarNode) node;
 
                 // System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
                 IRubyObject obj = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
                 // FIXME: null check is removable once we figure out how to assign to unset named block args
                 return obj == null ? runtime.getNil() : obj;
             }
             case NodeTypes.DXSTRNODE: {
                 DXStrNode iVisited = (DXStrNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self).toString());
                 }
     
                 return self.callMethod(context, "`", runtime.newString(sb.toString()));
             }
             case NodeTypes.ENSURENODE: {
                 EnsureNode iVisited = (EnsureNode) node;
     
                 // save entering the try if there's nothing to ensure
                 if (iVisited.getEnsureNode() != null) {
                     IRubyObject result = runtime.getNil();
     
                     try {
                         result = evalInternal(context, iVisited.getBodyNode(), self);
                     } finally {
                         evalInternal(context, iVisited.getEnsureNode(), self);
                     }
     
                     return result;
                 }
     
                 node = iVisited.getBodyNode();
                 continue bigloop;
             }
             case NodeTypes.EVSTRNODE: {
                 EvStrNode iVisited = (EvStrNode) node;
     
                 node = iVisited.getBody();
                 continue bigloop;
             }
             case NodeTypes.FALSENODE: {
                 context.pollThreadEvents();
                 return runtime.getFalse();
             }
             case NodeTypes.FCALLNODE: {
                 FCallNode iVisited = (FCallNode) node;
                 
                 context.beginCallArgs();
                 IRubyObject[] args;
                 try {
                     args = setupArgs(context, iVisited.getArgsNode(), self);
                 } finally {
                     context.endCallArgs();
                 }
     
                 return self.callMethod(context, iVisited.getName(), args, CallType.FUNCTIONAL);
             }
             case NodeTypes.FIXNUMNODE: {
                 FixnumNode iVisited = (FixnumNode) node;
                 return runtime.newFixnum(iVisited.getValue());
             }
             case NodeTypes.FLIPNODE: {
                 FlipNode iVisited = (FlipNode) node;
                 IRubyObject result = runtime.getNil();
     
                 if (iVisited.isExclusive()) {
                     if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                         result = evalInternal(context, iVisited.getBeginNode(), self).isTrue() ? runtime.getFalse()
                                 : runtime.getTrue();
                         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                         return result;
                     } else {
                         if (evalInternal(context, iVisited.getEndNode(), self).isTrue()) {
                             context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                         }
                         return runtime.getTrue();
                     }
                 } else {
                     if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                         if (evalInternal(context, iVisited.getBeginNode(), self).isTrue()) {
                             context.getCurrentScope().setValue(
                                     iVisited.getIndex(),
                                     evalInternal(context, iVisited.getEndNode(), self).isTrue() ? runtime.getFalse()
                                             : runtime.getTrue(), iVisited.getDepth());
                             return runtime.getTrue();
                         } else {
                             return runtime.getFalse();
                         }
                     } else {
                         if (evalInternal(context, iVisited.getEndNode(), self).isTrue()) {
                             context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                         }
                         return runtime.getTrue();
                     }
                 }
             }
             case NodeTypes.FLOATNODE: {
                 FloatNode iVisited = (FloatNode) node;
                 return RubyFloat.newFloat(runtime, iVisited.getValue());
             }
             case NodeTypes.FORNODE: {
                 ForNode iVisited = (ForNode) node;
                 
                 // For nodes do not have to create an addition scope so we just pass null
                 context.preForLoopEval(Block.createBlock(iVisited.getVarNode(), null,
                         iVisited.getCallable(), self));
     
                 try {
                     while (true) {
                         try {
                             ISourcePosition position = context.getPosition();
                             context.beginCallArgs();
     
                             IRubyObject recv = null;
                             try {
                                 recv = evalInternal(context, iVisited.getIterNode(), self);
                             } finally {
                                 context.setPosition(position);
                                 context.endCallArgs();
                             }
     
                             return recv.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallType.NORMAL);
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
                         IRubyObject breakValue = (IRubyObject) je.getPrimaryData();
     
                         return breakValue == null ? runtime.getNil() : breakValue;
                     default:
                         throw je;
                     }
                 } finally {
                     context.postForLoopEval();
                 }
             }
             case NodeTypes.GLOBALASGNNODE: {
                 GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
     
                 runtime.getGlobalVariables().set(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.GLOBALVARNODE: {
                 GlobalVarNode iVisited = (GlobalVarNode) node;
                 return runtime.getGlobalVariables().get(iVisited.getName());
             }
             case NodeTypes.HASHNODE: {
                 HashNode iVisited = (HashNode) node;
     
                 Map hash = null;
                 if (iVisited.getListNode() != null) {
                     hash = new HashMap(iVisited.getListNode().size() / 2);
     
                     for (Iterator iterator = iVisited.getListNode().iterator(); iterator.hasNext();) {
                         // insert all nodes in sequence, hash them in the final instruction
                         // KEY
                         IRubyObject key = evalInternal(context, (Node) iterator.next(), self);
                         IRubyObject value = evalInternal(context, (Node) iterator.next(), self);
     
                         hash.put(key, value);
                     }
                 }
     
                 if (hash == null) {
                     return RubyHash.newHash(runtime);
                 }
     
                 return RubyHash.newHash(runtime, hash, runtime.getNil());
             }
             case NodeTypes.IFNODE: {
                 IfNode iVisited = (IfNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getCondition(), self);
     
                 if (result.isTrue()) {
                     node = iVisited.getThenBody();
                     continue bigloop;
                 } else {
                     node = iVisited.getElseBody();
                     continue bigloop;
                 }
             }
             case NodeTypes.INSTASGNNODE: {
                 InstAsgnNode iVisited = (InstAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
                 self.setInstanceVariable(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.INSTVARNODE: {
                 InstVarNode iVisited = (InstVarNode) node;
                 IRubyObject variable = self.getInstanceVariable(iVisited.getName());
     
                 return variable == null ? runtime.getNil() : variable;
             }
                 //                case NodeTypes.ISCOPINGNODE:
                 //                EvaluateVisitor.iScopingNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.ITERNODE: {
                 IterNode iVisited = (IterNode) node;
                 
                 context.preIterEval(Block.createBlock(iVisited.getVarNode(), 
                         new DynamicScope(iVisited.getScope(), context.getCurrentScope()), 
                         iVisited.getCallable(), self));
                 
                 try {
                     while (true) {
                         try {
                             context.setBlockAvailable();
                             return evalInternal(context, iVisited.getIterNode(), self);
                         } catch (JumpException je) {
                             switch (je.getJumpType().getTypeId()) {
                             case JumpType.RETRY:
                                 // allow loop to retry
                                 break;
                             default:
                                 throw je;
                             }
                         } finally {
                             context.clearBlockAvailable();
                         }
                     }
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
                     case JumpType.BREAK:
                         IRubyObject breakValue = (IRubyObject) je.getPrimaryData();
     
                         return breakValue == null ? runtime.getNil() : breakValue;
                     default:
                         throw je;
                     }
                 } finally {
                     context.postIterEval();
                 }
             }
             case NodeTypes.LOCALASGNNODE: {
                 LocalAsgnNode iVisited = (LocalAsgnNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
                 
                 // System.out.println("LSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
 
                 return result;
             }
             case NodeTypes.LOCALVARNODE: {
                 LocalVarNode iVisited = (LocalVarNode) node;
 
                 //System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
                 IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
                 return result == null ? runtime.getNil() : result;
             }
             case NodeTypes.MATCH2NODE: {
                 Match2Node iVisited = (Match2Node) node;
                 IRubyObject recv = evalInternal(context, iVisited.getReceiverNode(), self);
                 IRubyObject value = evalInternal(context, iVisited.getValueNode(), self);
     
                 return ((RubyRegexp) recv).match(value);
             }
             case NodeTypes.MATCH3NODE: {
                 Match3Node iVisited = (Match3Node) node;
                 IRubyObject recv = evalInternal(context, iVisited.getReceiverNode(), self);
                 IRubyObject value = evalInternal(context, iVisited.getValueNode(), self);
     
                 if (value instanceof RubyString) {
                     return ((RubyRegexp) recv).match(value);
                 } else {
                     return value.callMethod(context, "=~", recv);
                 }
             }
             case NodeTypes.MATCHNODE: {
                 MatchNode iVisited = (MatchNode) node;
                 return ((RubyRegexp) evalInternal(context, iVisited.getRegexpNode(), self)).match2();
             }
             case NodeTypes.MODULENODE: {
                 ModuleNode iVisited = (ModuleNode) node;
                 Node classNameNode = iVisited.getCPath();
                 String name = ((INameNode) classNameNode).getName();
                 RubyModule enclosingModule = getEnclosingModule(context, classNameNode, self);
     
                 if (enclosingModule == null) {
                     throw runtime.newTypeError("no outer class/module");
                 }
     
                 RubyModule module;
                 if (enclosingModule == runtime.getObject()) {
                     module = runtime.getOrCreateModule(name);
                 } else {
                     module = enclosingModule.defineModuleUnder(name);
                 }
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), module, self);
             }
             case NodeTypes.MULTIPLEASGNNODE: {
                 MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
                 return AssignmentVisitor.assign(context, self, iVisited, evalInternal(context,
                         iVisited.getValueNode(), self), false);
             }
             case NodeTypes.NEWLINENODE: {
                 NewlineNode iVisited = (NewlineNode) node;
     
                 // something in here is used to build up ruby stack trace...
                 context.setPosition(iVisited.getPosition());
     
                 if (isTrace(runtime)) {
                     callTraceFunction(context, "line", self);
                 }
     
                 // TODO: do above but not below for additional newline nodes
                 node = iVisited.getNextNode();
                 continue bigloop;
             }
             case NodeTypes.NEXTNODE: {
                 NextNode iVisited = (NextNode) node;
     
                 context.pollThreadEvents();
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
     
                 // now used as an interpreter event
                 JumpException je = new JumpException(JumpException.JumpType.NextJump);
     
                 je.setPrimaryData(result);
                 je.setSecondaryData(iVisited);
     
                 //state.setCurrentException(je);
                 throw je;
             }
             case NodeTypes.NILNODE:
                 return runtime.getNil();
             case NodeTypes.NOTNODE: {
                 NotNode iVisited = (NotNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getConditionNode(), self);
                 return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
             }
             case NodeTypes.NTHREFNODE: {
                 NthRefNode iVisited = (NthRefNode) node;
                 return RubyRegexp.nth_match(iVisited.getMatchNumber(), context.getBackref());
             }
             case NodeTypes.OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
     
                 // add in reverse order
                 IRubyObject result = evalInternal(context, iVisited.getFirstNode(), self);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue bigloop;
             }
             case NodeTypes.OPASGNNODE: {
                 OpAsgnNode iVisited = (OpAsgnNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self);
                 IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
     
                 if (iVisited.getOperatorName().equals("||")) {
                     if (value.isTrue()) {
                         return value;
                     }
                     value = evalInternal(context, iVisited.getValueNode(), self);
                 } else if (iVisited.getOperatorName().equals("&&")) {
                     if (!value.isTrue()) {
                         return value;
                     }
                     value = evalInternal(context, iVisited.getValueNode(), self);
                 } else {
                     value = value.callMethod(context, iVisited.getOperatorName(), evalInternal(context,
                             iVisited.getValueNode(), self));
                 }
     
                 receiver.callMethod(context, iVisited.getVariableName() + "=", value);
     
                 context.pollThreadEvents();
     
                 return value;
             }
             case NodeTypes.OPASGNORNODE: {
                 OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
                 String def = getDefinition(context, iVisited.getFirstNode(), self);
     
                 IRubyObject result = runtime.getNil();
                 if (def != null) {
                     result = evalInternal(context, iVisited.getFirstNode(), self);
                 }
                 if (!result.isTrue()) {
                     result = evalInternal(context, iVisited.getSecondNode(), self);
                 }
     
                 return result;
             }
             case NodeTypes.OPELEMENTASGNNODE: {
                 OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self);
     
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
     
                 IRubyObject firstValue = receiver.callMethod(context, "[]", args);
     
                 if (iVisited.getOperatorName().equals("||")) {
                     if (firstValue.isTrue()) {
                         return firstValue;
                     }
                     firstValue = evalInternal(context, iVisited.getValueNode(), self);
                 } else if (iVisited.getOperatorName().equals("&&")) {
                     if (!firstValue.isTrue()) {
                         return firstValue;
                     }
                     firstValue = evalInternal(context, iVisited.getValueNode(), self);
                 } else {
                     firstValue = firstValue.callMethod(context, iVisited.getOperatorName(), evalInternal(context, iVisited
                                     .getValueNode(), self));
                 }
     
                 IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
                 System.arraycopy(args, 0, expandedArgs, 0, args.length);
                 expandedArgs[expandedArgs.length - 1] = firstValue;
                 return receiver.callMethod(context, "[]=", expandedArgs);
             }
             case NodeTypes.OPTNNODE: {
                 OptNNode iVisited = (OptNNode) node;
     
                 IRubyObject result = runtime.getNil();
                 while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             result = evalInternal(context, iVisited.getBodyNode(), self);
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
                                 return (IRubyObject) je.getPrimaryData();
                             default:
                                 throw je;
                             }
                         }
                     }
                 }
                 return result;
             }
             case NodeTypes.ORNODE: {
                 OrNode iVisited = (OrNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getFirstNode(), self);
     
                 if (!result.isTrue()) {
                     result = evalInternal(context, iVisited.getSecondNode(), self);
                 }
     
                 return result;
             }
                 //                case NodeTypes.POSTEXENODE:
                 //                EvaluateVisitor.postExeNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.REDONODE: {
                 context.pollThreadEvents();
     
                 // now used as an interpreter event
                 JumpException je = new JumpException(JumpException.JumpType.RedoJump);
     
                 je.setSecondaryData(node);
     
                 throw je;
             }
             case NodeTypes.REGEXPNODE: {
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
 
                 return RubyRegexp.newRegexp(runtime, iVisited.getPattern(), lang);
             }
             case NodeTypes.RESCUEBODYNODE: {
                 RescueBodyNode iVisited = (RescueBodyNode) node;
                 node = iVisited.getBodyNode();
                 continue bigloop;
             }
             case NodeTypes.RESCUENODE: {
                 RescueNode iVisited = (RescueNode)node;
                 RescuedBlock : while (true) {
                     try {
                         // Execute rescue block
                         IRubyObject result = evalInternal(context, iVisited.getBodyNode(), self);
 
                         // If no exception is thrown execute else block
                         if (iVisited.getElseNode() != null) {
                             if (iVisited.getRescueNode() == null) {
                                 runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
                             }
                             result = evalInternal(context, iVisited.getElseNode(), self);
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
                                 exceptionNodesList = (ListNode) evalInternal(context, exceptionNodes, self);
                             } else {
                                 exceptionNodesList = (ListNode) exceptionNodes;
                             }
                             
                             if (isRescueHandled(context, raisedException, exceptionNodesList, self)) {
                                 try {
                                     return evalInternal(context, rescueNode, self);
                                 } catch (JumpException je) {
                                     if (je.getJumpType() == JumpException.JumpType.RetryJump) {
                                         // should be handled in the finally block below
                                         //state.runtime.getGlobalVariables().set("$!", state.runtime.getNil());
                                         //state.threadContext.setRaisedException(null);
                                         continue RescuedBlock;
                                         
                                     } else {
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
                         runtime.getGlobalVariables().set("$!", runtime.getNil());
                     }
                 }
             }
             case NodeTypes.RETRYNODE: {
                 context.pollThreadEvents();
     
                 JumpException je = new JumpException(JumpException.JumpType.RetryJump);
     
                 throw je;
             }
             case NodeTypes.RETURNNODE: {
                 ReturnNode iVisited = (ReturnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self);
     
                 JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
     
                 je.setPrimaryData(iVisited.getTarget());
                 je.setSecondaryData(result);
                 je.setTertiaryData(iVisited);
     
                 throw je;
             }
             case NodeTypes.ROOTNODE: {
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
                     return eval(context, iVisited.getBodyNode(), self);
                 } finally {
                     context.postRootNode();
                 }
             }
             case NodeTypes.SCLASSNODE: {
                 SClassNode iVisited = (SClassNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self);
     
                 RubyClass singletonClass;
     
                 if (receiver.isNil()) {
                     singletonClass = runtime.getNilClass();
                 } else if (receiver == runtime.getTrue()) {
                     singletonClass = runtime.getClass("TrueClass");
                 } else if (receiver == runtime.getFalse()) {
                     singletonClass = runtime.getClass("FalseClass");
                 } else {
                     if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                         throw runtime.newSecurityError("Insecure: can't extend object.");
                     }
     
                     singletonClass = receiver.getSingletonClass();
                 }
     
                 
     
                 if (context.getWrapper() != null) {
                     singletonClass.extendObject(context.getWrapper());
                     singletonClass.includeModule(context.getWrapper());
                 }
     
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self);
             }
             case NodeTypes.SELFNODE:
                 return self;
             case NodeTypes.SPLATNODE: {
                 SplatNode iVisited = (SplatNode) node;
                 return splatValue(evalInternal(context, iVisited.getValue(), self));
             }
                 ////                case NodeTypes.STARNODE:
                 ////                EvaluateVisitor.starNodeVisitor.execute(this, node);
                 ////                break;
             case NodeTypes.STRNODE: {
                 StrNode iVisited = (StrNode) node;
                 return runtime.newString(iVisited.getValue());
             }
             case NodeTypes.SUPERNODE: {
                 SuperNode iVisited = (SuperNode) node;
                 
     
                 if (context.getFrameLastClass() == null) {
-                    throw runtime.newNameError("Superclass method '" + context.getFrameLastFunc()
-                            + "' disabled.");
+                    String name = context.getFrameLastFunc();
+                    throw runtime.newNameError("Superclass method '" + name
+                            + "' disabled.", name);
                 }
     
                 context.beginCallArgs();
     
                 IRubyObject[] args = null;
                 try {
                     args = setupArgs(context, iVisited.getArgsNode(), self);
                 } finally {
                     context.endCallArgs();
                 }
                 return context.callSuper(args);
             }
             case NodeTypes.SVALUENODE: {
                 SValueNode iVisited = (SValueNode) node;
                 return aValueSplat(evalInternal(context, iVisited.getValue(), self));
             }
             case NodeTypes.SYMBOLNODE: {
                 SymbolNode iVisited = (SymbolNode) node;
                 return runtime.newSymbol(iVisited.getName());
             }
             case NodeTypes.TOARYNODE: {
                 ToAryNode iVisited = (ToAryNode) node;
                 return aryToAry(evalInternal(context, iVisited.getValue(), self));
             }
             case NodeTypes.TRUENODE: {
                 context.pollThreadEvents();
                 return runtime.getTrue();
             }
             case NodeTypes.UNDEFNODE: {
                 UndefNode iVisited = (UndefNode) node;
                 
     
                 if (context.getRubyClass() == null) {
                     throw runtime
                             .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
                 }
                 context.getRubyClass().undef(iVisited.getName());
     
                 return runtime.getNil();
             }
             case NodeTypes.UNTILNODE: {
                 UntilNode iVisited = (UntilNode) node;
     
                 IRubyObject result = runtime.getNil();
                 
                 while (!(result = evalInternal(context, iVisited.getConditionNode(), self)).isTrue()) {
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             result = evalInternal(context, iVisited.getBodyNode(), self);
                             break loop;
                         } catch (JumpException je) {
                             switch (je.getJumpType().getTypeId()) {
                             case JumpType.REDO:
                                 continue;
                             case JumpType.NEXT:
                                 break loop;
                             case JumpType.BREAK:
                                 return (IRubyObject) je.getPrimaryData();
                             default:
                                 throw je;
                             }
                         }
                     }
                 }
                 
                 return result;
             }
             case NodeTypes.VALIASNODE: {
                 VAliasNode iVisited = (VAliasNode) node;
                 runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
     
                 return runtime.getNil();
             }
             case NodeTypes.VCALLNODE: {
                 VCallNode iVisited = (VCallNode) node;
                 return self.callMethod(context, iVisited.getName(),
                         IRubyObject.NULL_ARRAY, CallType.VARIABLE);
             }
             case NodeTypes.WHENNODE:
                 assert false;
                 return null;
             case NodeTypes.WHILENODE: {
                 WhileNode iVisited = (WhileNode) node;
     
                 IRubyObject result = runtime.getNil();
                 boolean firstTest = iVisited.evaluateAtStart();
                 
                 while (!firstTest || (result = evalInternal(context, iVisited.getConditionNode(), self)).isTrue()) {
                     firstTest = true;
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             evalInternal(context, iVisited.getBodyNode(), self);
                             break loop;
                         } catch (JumpException je) {
                             switch (je.getJumpType().getTypeId()) {
                             case JumpType.REDO:
                                 continue;
                             case JumpType.NEXT:
                                 break loop;
                             case JumpType.BREAK:
                                 return result;
                             default:
                                 throw je;
                             }
                         }
                     }
                 }
                 
                 return result;
             }
             case NodeTypes.XSTRNODE: {
                 XStrNode iVisited = (XStrNode) node;
                 return self.callMethod(context, "`", runtime.newString(iVisited.getValue()));
             }
             case NodeTypes.YIELDNODE: {
                 YieldNode iVisited = (YieldNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getArgsNode(), self);
                 if (iVisited.getArgsNode() == null) {
                     result = null;
                 }
     
                 return  context.yieldCurrentBlock(result, null, null,
                         iVisited.getCheckState());
                 
             }
             case NodeTypes.ZARRAYNODE: {
                 return runtime.newArray();
             }
             case NodeTypes.ZSUPERNODE: {
                 
     
                 if (context.getFrameLastClass() == null) {
-                    throw runtime.newNameError("superclass method '" + context.getFrameLastFunc()
-                            + "' disabled");
+                    String name = context.getFrameLastFunc();
+                    throw runtime.newNameError("superclass method '" + name
+                            + "' disabled", name);
                 }
     
                 return context.callSuper(context.getFrameArgs());
             }
             }
         } while (true);
         }
 
     private static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) {
             return value;
         }
 
         if (value.respondsTo("to_ary")) {
             return value.convertToType("Array", "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     private static IRubyObject evalClassDefinitionBody(ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self) {
         IRuby runtime = context.getRuntime();
         context.preClassEval(scope, type);
 
         try {
             if (isTrace(runtime)) {
                 callTraceFunction(context, "class", type);
             }
 
             return evalInternal(context, bodyNode, type);
         } finally {
             context.postClassEval();
 
             if (isTrace(runtime)) {
                 callTraceFunction(context, "end", null);
             }
         }
     }
     
     /**
      * Helper method.
      *
      * test if a trace function is avaiable.
      *
      */
     private static boolean isTrace(IRuby runtime) {
         return runtime.getTraceFunction() != null;
     }
 
     private static void callTraceFunction(ThreadContext context, String event, IRubyObject zelf) {
         IRuby runtime = context.getRuntime();
         String name = context.getFrameLastFunc();
         RubyModule type = context.getFrameLastClass();
         runtime.callTraceFunction(context, event, context.getPosition(), zelf, name, type);
     }
 
     private static IRubyObject splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     private static IRubyObject aValueSplat(IRubyObject value) {
         IRuby runtime = value.getRuntime();
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     private static RubyArray arrayValue(IRubyObject value) {
         IRubyObject newValue = value.convertToType("Array", "to_ary", false);
 
         if (newValue.isNil()) {
             IRuby runtime = value.getRuntime();
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getType().searchMethod("to_a").getImplementationClass() != runtime
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
 
     private static IRubyObject[] setupArgs(ThreadContext context, Node node,
             IRubyObject self) {
         if (node == null) {
             return IRubyObject.NULL_ARRAY;
         }
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode)node;
             ISourcePosition position = context.getPosition();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
             // avoid using ArrayList unless we absolutely have to
             List argsList = null;
             // once we find a splat, stuff remaining args in argsList and combine afterwards
             boolean hasSplat = false;
             // index for the beginning of splatted args, used for combination later
             int splatBegins = 0;
 
             for (int i = 0; i < size; i++) {
                 Node next = argsArrayNode.get(i);
                 
                 if (hasSplat) {
                     // once we've found a splat, we switch to an arraylist to handle growing
                     if (next instanceof SplatNode) {
                         argsList.addAll(((RubyArray) evalInternal(context, next, self)).getList());
                     } else {
                         argsList.add(evalInternal(context, next, self));
                     }
                 } else {
                     if (next instanceof SplatNode) {
                         // switch to ArrayList, since we've got splatted args in the list
                         argsList = new ArrayList();
                         splatBegins = i;
                         hasSplat = true;
                         argsList.addAll(((RubyArray) evalInternal(context, next, self)).getList());
                     } else {
                         argsArray[i] = evalInternal(context, next, self);
                     }
                 }
             }
             
             if (hasSplat) {
                 // we had splatted arguments, combine unsplatted with list
                 IRubyObject[] argsArray2 = (IRubyObject[])argsList.toArray(new IRubyObject[argsList.size()]);
                 IRubyObject[] newArgsArray = new IRubyObject[splatBegins + argsArray2.length];
                 System.arraycopy(argsArray, 0, newArgsArray, 0, splatBegins);
                 System.arraycopy(argsArray2, 0, newArgsArray, splatBegins, argsArray2.length);
                 
                 argsArray = argsArray2;
             }
 
             context.setPosition(position);
 
             return argsArray;
         }
 
         return ArgsUtil.arrayify(evalInternal(context, node, self));
     }
 
     private static RubyModule getEnclosingModule(ThreadContext context, Node node, IRubyObject self) {
         RubyModule enclosingModule = null;
 
         if (node instanceof Colon2Node) {
             IRubyObject result = evalInternal(context, ((Colon2Node) node).getLeftNode(), self);
 
             if (result != null && !result.isNil()) {
                 enclosingModule = (RubyModule) result;
             }
         } else if (node instanceof Colon3Node) {
             enclosingModule = context.getRuntime().getObject();
         }
 
         if (enclosingModule == null) {
             enclosingModule = (RubyModule) context.peekCRef().getValue();
         }
 
         return enclosingModule;
     }
 
     private static boolean isRescueHandled(ThreadContext context, RubyException currentException, ListNode exceptionNodes,
             IRubyObject self) {
         IRuby runtime = context.getRuntime();
         if (exceptionNodes == null) {
             return currentException.isKindOf(runtime.getClass("StandardError"));
         }
 
         context.beginCallArgs();
 
         IRubyObject[] args = null;
         try {
             args = setupArgs(context, exceptionNodes, self);
         } finally {
             context.endCallArgs();
         }
 
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(runtime.getClass("Module"))) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             if (args[i].callMethod(context, "===", currentException).isTrue()) return true;
         }
         return false;
     }
 }
diff --git a/src/org/jruby/exceptions/RaiseException.java b/src/org/jruby/exceptions/RaiseException.java
index 46fdd68ea4..3bc6326c49 100644
--- a/src/org/jruby/exceptions/RaiseException.java
+++ b/src/org/jruby/exceptions/RaiseException.java
@@ -1,125 +1,126 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
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
 package org.jruby.exceptions;
 
 import java.io.PrintWriter;
 import java.io.StringWriter;
 
-import org.jruby.IRuby;
-import org.jruby.NativeException;
-import org.jruby.RubyClass;
-import org.jruby.RubyException;
+import org.jruby.*;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RaiseException extends JumpException {
 	private static final long serialVersionUID = -7612079169559973951L;
 	
 	private RubyException exception;
 
     public RaiseException(RubyException actException) {
-    	super(JumpType.RaiseJump);
-        setException(actException, false);
+    	this(actException, false);
     }
 
     public RaiseException(IRuby runtime, RubyClass excptnClass, String msg, boolean nativeException) {
 		super(msg, JumpType.RaiseJump);
         if (msg == null) {
             msg = "No message available";
         }
         setException((RubyException) excptnClass.callMethod(runtime.getCurrentContext(), "new", excptnClass.getRuntime().newString(msg)), nativeException);
     }
-    
+
+    public RaiseException(RubyException exception, boolean isNativeException) {
+        super(JumpType.RaiseJump);
+        setException(exception, isNativeException);
+    }
+
     public static RaiseException createNativeRaiseException(IRuby runtime, Throwable cause) {
         NativeException nativeException = new NativeException(runtime, runtime.getClass(NativeException.CLASS_NAME), cause);
         return new RaiseException(cause, nativeException);
     }
 
     private static String buildMessage(Throwable exception) {
 	    StringBuffer sb = new StringBuffer();
 	    StringWriter stackTrace = new StringWriter();
 	    exception.printStackTrace(new PrintWriter(stackTrace));
 	
 	    sb.append("Native Exception: '").append(exception.getClass()).append("'; ");
 	    sb.append("Message: ").append(exception.getMessage()).append("; ");
 	    sb.append("StackTrace: ").append(stackTrace.getBuffer().toString());
 
 	    return sb.toString();
     }
 
     public RaiseException(Throwable cause, NativeException nativeException) {
         super(buildMessage(cause), cause, JumpType.RaiseJump);
         setException(nativeException, false);
     }
 
     /**
      * Gets the exception
      * @return Returns a RubyException
      */
     public RubyException getException() {
         return exception;
     }
 
     /**
      * Sets the exception
      * @param newException The exception to set
      */
     protected void setException(RubyException newException, boolean nativeException) {
         IRuby runtime = newException.getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         
         runtime.getGlobalVariables().set("$!", newException);
 
         if (runtime.getTraceFunction() != null) {
             runtime.callTraceFunction(context, "return", context.getPosition(),
                     context.getFrameSelf(), context.getFrameLastFunc(), context.getFrameLastClass());
         }
 
         this.exception = newException;
 
         if (runtime.getStackTraces() > 5) {
             return;
         }
 
         runtime.setStackTraces(runtime.getStackTraces() + 1);
 
         if (newException.callMethod(context, "backtrace").isNil() && context.getSourceFile() != null) {
             IRubyObject backtrace = context.createBacktrace(0, nativeException);
             newException.callMethod(context, "set_backtrace", backtrace);
         }
 
         runtime.setStackTraces(runtime.getStackTraces() - 1);
     }
 }
diff --git a/src/org/jruby/internal/runtime/ReadonlyAccessor.java b/src/org/jruby/internal/runtime/ReadonlyAccessor.java
index a458f27207..7edb1f33d6 100644
--- a/src/org/jruby/internal/runtime/ReadonlyAccessor.java
+++ b/src/org/jruby/internal/runtime/ReadonlyAccessor.java
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.internal.runtime;
 
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class ReadonlyAccessor implements IAccessor {
     private String name;
     private IAccessor accessor;
 
     public ReadonlyAccessor(String name, IAccessor accessor) {
         assert name != null;
         assert accessor != null;
 
         this.name = name;
         this.accessor = accessor;
     }
 
     public IRubyObject getValue() {
         return accessor.getValue();
     }
 
     public IRubyObject setValue(IRubyObject newValue) {
         assert newValue != null;
 
-        throw newValue.getRuntime().newNameError(name + " is a read-only variable");
+        throw newValue.getRuntime().newNameError(name + " is a read-only variable", name);
     }
 }
diff --git a/src/org/jruby/javasupport/JavaClass.java b/src/org/jruby/javasupport/JavaClass.java
index 9aedfa16d5..193596821f 100644
--- a/src/org/jruby/javasupport/JavaClass.java
+++ b/src/org/jruby/javasupport/JavaClass.java
@@ -1,585 +1,586 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 package org.jruby.javasupport;
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
+import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 public class JavaClass extends JavaObject {
 
     private JavaClass(IRuby runtime, Class javaClass) {
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaClass"), javaClass);
     }
     
     public static JavaClass get(IRuby runtime, Class klass) {
         JavaClass javaClass = runtime.getJavaSupport().getJavaClassFromCache(klass);
         if (javaClass == null) {
             javaClass = new JavaClass(runtime, klass);
             runtime.getJavaSupport().putJavaClassIntoCache(javaClass);
         }
         return javaClass;
     }
 
     public static RubyClass createJavaClassClass(IRuby runtime, RubyModule javaModule) {
         RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.getClass("JavaObject")); 
 
     	CallbackFactory callbackFactory = runtime.callbackFactory(JavaClass.class);
         
         result.includeModule(runtime.getModule("Comparable"));
         
         JavaObject.registerRubyMethods(runtime, result);
         
         result.defineSingletonMethod("for_name", 
                 callbackFactory.getSingletonMethod("for_name", IRubyObject.class));
         result.defineMethod("public?", 
                 callbackFactory.getMethod("public_p"));
         result.defineMethod("protected?", 
                 callbackFactory.getMethod("protected_p"));
         result.defineMethod("private?", 
                 callbackFactory.getMethod("private_p"));
         result.defineMethod("final?", 
                 callbackFactory.getMethod("final_p"));
         result.defineMethod("interface?", 
                 callbackFactory.getMethod("interface_p"));
         result.defineMethod("array?", 
                 callbackFactory.getMethod("array_p"));
         result.defineMethod("name", 
                 callbackFactory.getMethod("name"));
         result.defineMethod("to_s", 
                 callbackFactory.getMethod("name"));
         result.defineMethod("superclass", 
                 callbackFactory.getMethod("superclass"));
         result.defineMethod("<=>", 
                 callbackFactory.getMethod("op_cmp", IRubyObject.class));
         result.defineMethod("java_instance_methods", 
                 callbackFactory.getMethod("java_instance_methods"));
         result.defineMethod("java_class_methods", 
                 callbackFactory.getMethod("java_class_methods"));
         result.defineMethod("java_method", 
                 callbackFactory.getOptMethod("java_method"));
         result.defineMethod("constructors", 
                 callbackFactory.getMethod("constructors"));
         result.defineMethod("constructor", 
                 callbackFactory.getOptMethod("constructor"));
         result.defineMethod("array_class", 
                 callbackFactory.getMethod("array_class"));
         result.defineMethod("new_array", 
                 callbackFactory.getMethod("new_array", IRubyObject.class));
         result.defineMethod("fields", 
                 callbackFactory.getMethod("fields"));
         result.defineMethod("field", 
                 callbackFactory.getMethod("field", IRubyObject.class));
         result.defineMethod("interfaces", 
                 callbackFactory.getMethod("interfaces"));
         result.defineMethod("primitive?", 
                 callbackFactory.getMethod("primitive_p"));
         result.defineMethod("assignable_from?", 
                 callbackFactory.getMethod("assignable_from_p", IRubyObject.class));
         result.defineMethod("component_type", 
                 callbackFactory.getMethod("component_type"));
 		result.defineMethod("declared_instance_methods", 
                 callbackFactory.getMethod("declared_instance_methods"));
         result.defineMethod("declared_class_methods", 
                 callbackFactory.getMethod("declared_class_methods"));
         result.defineMethod("declared_fields", 
                 callbackFactory.getMethod("declared_fields"));
         result.defineMethod("declared_field", 
                 callbackFactory.getMethod("declared_field", IRubyObject.class));
         result.defineMethod("declared_constructors", 
                 callbackFactory.getMethod("declared_constructors"));
         result.defineMethod("declared_constructor", 
                 callbackFactory.getOptMethod("declared_constructor"));
         result.defineMethod("declared_method", 
                 callbackFactory.getOptMethod("declared_method"));
         result.defineMethod("define_instance_methods_for_proxy", 
                 callbackFactory.getMethod("define_instance_methods_for_proxy", IRubyObject.class));
         
         result.getMetaClass().undefineMethod("new");
 
         return result;
     }
     
     public static JavaClass for_name(IRubyObject recv, IRubyObject name) {
         String className = name.asSymbol();
         Class klass = recv.getRuntime().getJavaSupport().loadJavaClass(className);
         return JavaClass.get(recv.getRuntime(), klass);
     }
     
     /**
      *  Get all methods grouped by name (e.g. 'new => {new(), new(int), new(int, int)}, ...')
      *  @param isStatic determines whether you want static or instance methods from the class
      */
     private Map getMethodsClumped(boolean isStatic) {
         Map map = new HashMap();
         if(((Class)getValue()).isInterface()) {
             return map;
         }
 
         Method methods[] = javaClass().getMethods();
         
         for (int i = 0; i < methods.length; i++) {
             if (isStatic != Modifier.isStatic(methods[i].getModifiers())) {
                 continue;
             }
             
             String key = methods[i].getName();
             RubyArray methodsWithName = (RubyArray) map.get(key); 
             
             if (methodsWithName == null) {
                 methodsWithName = getRuntime().newArray();
                 map.put(key, methodsWithName);
             }
             
             methodsWithName.append(JavaMethod.create(getRuntime(), methods[i]));
         }
         
         return map;
     }
     
     private Map getPropertysClumped() {
         Map map = new HashMap();
         BeanInfo info;
         
         try {
             info = Introspector.getBeanInfo(javaClass());
         } catch (IntrospectionException e) {
             return map;
         }
         
         PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
         
         for (int i = 0; i < descriptors.length; i++) {
             Method readMethod = descriptors[i].getReadMethod();
             
             if (readMethod != null) {
                 String key = readMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     
                     map.put(key, aliases);    
                 }
 
                 if (readMethod.getReturnType() == Boolean.class ||
                     readMethod.getReturnType() == boolean.class) {
                     aliases.add(descriptors[i].getName() + "?");
                 }
                 aliases.add(descriptors[i].getName());
             }
             
             Method writeMethod = descriptors[i].getWriteMethod();
 
             if (writeMethod != null) {
                 String key = writeMethod.getName();
                 List aliases = (List) map.get(key);
                 
                 if (aliases == null) {
                     aliases = new ArrayList();
                     map.put(key, aliases);
                 }
                 
                 aliases.add(descriptors[i].getName()  + "=");
             }
         }
         
         return map;
     }
     
     private void define_instance_method_for_proxy(final RubyClass proxy, List names, 
             final RubyArray methods) {
         final RubyModule javaUtilities = getRuntime().getModule("JavaUtilities");
         Callback method = new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 IRubyObject[] argsArray = new IRubyObject[args.length + 1];
                 ThreadContext context = self.getRuntime().getCurrentContext();
                 
                 argsArray[0] = self.callMethod(context, "java_object");
                 RubyArray argsAsArray = getRuntime().newArray();
                 for (int j = 0; j < args.length; j++) {
                     argsArray[j+1] = Java.ruby_to_java(proxy, args[j]);
                     argsAsArray.append(argsArray[j+1]);
                 }
 
                 IRubyObject[] mmArgs = new IRubyObject[] {methods, argsAsArray};
                 IRubyObject result = javaUtilities.callMethod(context, "matching_method", mmArgs);
                 return Java.java_to_ruby(self, result.callMethod(context, "invoke", argsArray));
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
         
         for(Iterator iter = names.iterator(); iter.hasNext(); ) {
             String methodName = (String) iter.next();
             
             // We do not override class since it is too important to be overridden by getClass
             // short name.
             if (!methodName.equals("class")) {
                 proxy.defineMethod(methodName, method);
                 
                 String rubyCasedName = getRubyCasedName(methodName);
                 if (rubyCasedName != null) {
                     proxy.defineAlias(rubyCasedName, methodName);
                 }
             }
         }
     }
     
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z])([A-Z])");
     
     private String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
 
         String rubyCasedName = m.replaceAll("$1_$2").toLowerCase();
         
         if (rubyCasedName.equals(javaCasedName)) {
             return null;
         }
         
         return rubyCasedName;
     }
     
     public IRubyObject define_instance_methods_for_proxy(IRubyObject arg) {
         assert arg instanceof RubyClass;
         
         Map aliasesClump = getPropertysClumped();
         Map methodsClump = getMethodsClumped(false);
         RubyClass proxy = (RubyClass) arg;
         
         for (Iterator iter = methodsClump.keySet().iterator(); iter.hasNext(); ) {
             String name = (String) iter.next();
             RubyArray methods = (RubyArray) methodsClump.get(name);
             List aliases = (List) aliasesClump.get(name);
 
             if (aliases == null) {
                 aliases = new ArrayList();
             }
 
             aliases.add(name);
             
             define_instance_method_for_proxy(proxy, aliases, methods);
         }
         
         return getRuntime().getNil();
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(javaClass().getModifiers()));
     }
 
     public RubyBoolean protected_p() {
         return getRuntime().newBoolean(Modifier.isProtected(javaClass().getModifiers()));
     }
 
     public RubyBoolean private_p() {
         return getRuntime().newBoolean(Modifier.isPrivate(javaClass().getModifiers()));
     }
 
 	Class javaClass() {
 		return (Class) getValue();
 	}
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(javaClass().getModifiers()));
     }
 
     public RubyBoolean interface_p() {
         return getRuntime().newBoolean(javaClass().isInterface());
     }
 
     public RubyBoolean array_p() {
         return getRuntime().newBoolean(javaClass().isArray());
     }
     
     public RubyString name() {
         return getRuntime().newString(javaClass().getName());
     }
 
     public IRubyObject superclass() {
         Class superclass = javaClass().getSuperclass();
         if (superclass == null) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), superclass);
     }
 
     public RubyFixnum op_cmp(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("<=> requires JavaClass (" + other.getType() + " given)");
         }
         JavaClass otherClass = (JavaClass) other;
         if (this.javaClass() == otherClass.javaClass()) {
             return getRuntime().newFixnum(0);
         }
         if (otherClass.javaClass().isAssignableFrom(this.javaClass())) {
             return getRuntime().newFixnum(-1);
         }
         return getRuntime().newFixnum(1);
     }
 
     public RubyArray java_instance_methods() {
         return java_methods(javaClass().getMethods(), false);
     }
 
     public RubyArray declared_instance_methods() {
         return java_methods(javaClass().getDeclaredMethods(), false);
     }
 
 	private RubyArray java_methods(Method[] methods, boolean isStatic) {
         RubyArray result = getRuntime().newArray(methods.length);
         for (int i = 0; i < methods.length; i++) {
             Method method = methods[i];
             if (isStatic == Modifier.isStatic(method.getModifiers())) {
                 result.append(JavaMethod.create(getRuntime(), method));
             }
         }
         return result;
 	}
 
 	public RubyArray java_class_methods() {
 	    return java_methods(javaClass().getMethods(), true);
     }
 
 	public RubyArray declared_class_methods() {
 	    return java_methods(javaClass().getDeclaredMethods(), true);
     }
 
 	public JavaMethod java_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.create(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     public JavaMethod declared_method(IRubyObject[] args) throws ClassNotFoundException {
         String methodName = args[0].asSymbol();
         Class[] argumentTypes = buildArgumentTypes(args);
         return JavaMethod.createDeclared(getRuntime(), javaClass(), methodName, argumentTypes);
     }
 
     private Class[] buildArgumentTypes(IRubyObject[] args) throws ClassNotFoundException {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         Class[] argumentTypes = new Class[args.length - 1];
         for (int i = 1; i < args.length; i++) {
             JavaClass type = for_name(this, args[i]);
             argumentTypes[i - 1] = type.javaClass();
         }
         return argumentTypes;
     }
 
     public RubyArray constructors() {
         return buildConstructors(javaClass().getConstructors());
     }
 
     public RubyArray declared_constructors() {
         return buildConstructors(javaClass().getDeclaredConstructors());
     }
 
     private RubyArray buildConstructors(Constructor[] constructors) {
         RubyArray result = getRuntime().newArray(constructors.length);
         for (int i = 0; i < constructors.length; i++) {
             result.append(new JavaConstructor(getRuntime(), constructors[i]));
         }
         return result;
     }
 
     public JavaConstructor constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getConstructor(parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
-            throw getRuntime().newNameError("no matching java constructor");
+            throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     public JavaConstructor declared_constructor(IRubyObject[] args) {
         try {
             Class[] parameterTypes = buildClassArgs(args);
             Constructor constructor;
             constructor = javaClass().getDeclaredConstructor (parameterTypes);
             return new JavaConstructor(getRuntime(), constructor);
         } catch (NoSuchMethodException nsme) {
-            throw getRuntime().newNameError("no matching java constructor");
+            throw getRuntime().newNameError("no matching java constructor", null);
         }
     }
 
     private Class[] buildClassArgs(IRubyObject[] args) {
         Class[] parameterTypes = new Class[args.length];
         for (int i = 0; i < args.length; i++) {
             String name = args[i].asSymbol();
             parameterTypes[i] = getRuntime().getJavaSupport().loadJavaClass(name);
         }
         return parameterTypes;
     }
 
     public JavaClass array_class() {
         return JavaClass.get(getRuntime(), Array.newInstance(javaClass(), 0).getClass());
     }
 
     public JavaObject new_array(IRubyObject lengthArgument) {
         if (! (lengthArgument instanceof RubyInteger)) {
             throw getRuntime().newTypeError(lengthArgument, getRuntime().getClass("Integer"));
         }
         int length = (int) ((RubyInteger) lengthArgument).getLongValue();
         return new JavaArray(getRuntime(), Array.newInstance(javaClass(), length));
     }
 
     public RubyArray fields() {
         return buildFieldResults(javaClass().getFields());
     }
 
     public RubyArray declared_fields() {
         return buildFieldResults(javaClass().getDeclaredFields());
     }
     
 	private RubyArray buildFieldResults(Field[] fields) {
         RubyArray result = getRuntime().newArray(fields.length);
         for (int i = 0; i < fields.length; i++) {
             result.append(new JavaField(getRuntime(), fields[i]));
         }
         return result;
 	}
 
 	public JavaField field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
-            throw getRuntime().newNameError(undefinedFieldMessage(stringName));
+            throw undefinedFieldError(stringName);
         }
     }
 
 	public JavaField declared_field(IRubyObject name) {
 		String stringName = name.asSymbol();
         try {
             Field field = javaClass().getDeclaredField(stringName);
 			return new JavaField(getRuntime(),field);
         } catch (NoSuchFieldException nsfe) {
-            throw getRuntime().newNameError(undefinedFieldMessage(stringName));
+            throw undefinedFieldError(stringName);
         }
     }
 
-	private String undefinedFieldMessage(String stringName) {
-		return "undefined field '" + stringName + "' for class '" + javaClass().getName() + "'";
-	}
+    private RaiseException undefinedFieldError(String name) {
+        return getRuntime().newNameError("undefined field '" + name + "' for class '" + javaClass().getName() + "'", name);
+    }
 
-	public RubyArray interfaces() {
+    public RubyArray interfaces() {
         Class[] interfaces = javaClass().getInterfaces();
         RubyArray result = getRuntime().newArray(interfaces.length);
         for (int i = 0; i < interfaces.length; i++) {
             result.append(JavaClass.get(getRuntime(), interfaces[i]));
         }
         return result;
     }
 
     public RubyBoolean primitive_p() {
         return getRuntime().newBoolean(isPrimitive());
     }
 
     public RubyBoolean assignable_from_p(IRubyObject other) {
         if (! (other instanceof JavaClass)) {
             throw getRuntime().newTypeError("assignable_from requires JavaClass (" + other.getType() + " given)");
         }
 
         Class otherClass = ((JavaClass) other).javaClass();
 
         if (!javaClass().isPrimitive() && otherClass == Void.TYPE ||
             javaClass().isAssignableFrom(otherClass)) {
             return getRuntime().getTrue();
         }
         otherClass = JavaUtil.primitiveToWrapper(otherClass);
         Class thisJavaClass = JavaUtil.primitiveToWrapper(javaClass());
         if (thisJavaClass.isAssignableFrom(otherClass)) {
             return getRuntime().getTrue();
         }
         if (Number.class.isAssignableFrom(thisJavaClass)) {
             if (Number.class.isAssignableFrom(otherClass)) {
                 return getRuntime().getTrue();
             }
             if (otherClass.equals(Character.class)) {
                 return getRuntime().getTrue();
             }
         }
         if (thisJavaClass.equals(Character.class)) {
             if (Number.class.isAssignableFrom(otherClass)) {
                 return getRuntime().getTrue();
             }
         }
         return getRuntime().getFalse();
     }
 
     private boolean isPrimitive() {
         return javaClass().isPrimitive();
     }
 
     public JavaClass component_type() {
         if (! javaClass().isArray()) {
             throw getRuntime().newTypeError("not a java array-class");
         }
         return JavaClass.get(getRuntime(), javaClass().getComponentType());
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index f8f84420de..4f49af4d16 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,223 +1,225 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 package org.jruby.javasupport;
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaMethod extends JavaCallable {
     private final Method method;
 
     public static RubyClass createJavaMethodClass(IRuby runtime, RubyModule javaModule) {
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject());
         CallbackFactory callbackFactory = runtime.callbackFactory(JavaMethod.class);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         
         result.defineMethod("name", 
                 callbackFactory.getMethod("name"));
         result.defineMethod("arity", 
                 callbackFactory.getMethod("arity"));
         result.defineMethod("public?", 
                 callbackFactory.getMethod("public_p"));
         result.defineMethod("final?", 
                 callbackFactory.getMethod("final_p"));
         result.defineMethod("static?", 
                 callbackFactory.getMethod("static_p"));
         result.defineMethod("invoke", 
                 callbackFactory.getOptMethod("invoke"));
         result.defineMethod("invoke_static", 
                 callbackFactory.getOptMethod("invoke_static"));
         result.defineMethod("argument_types", 
                 callbackFactory.getMethod("argument_types"));
         result.defineMethod("inspect", 
                 callbackFactory.getMethod("inspect"));
         result.defineMethod("return_type", 
                 callbackFactory.getMethod("return_type"));
 
         return result;
     }
 
     public JavaMethod(IRuby runtime, Method method) {
         super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaMethod"));
         this.method = method;
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
         if (Modifier.isPublic(method.getModifiers()) &&
             Modifier.isPublic(method.getClass().getModifiers()) &&
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
             accesibleObject().setAccessible(true);
         }
     }
 
     public static JavaMethod create(IRuby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(IRuby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
-            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'");
+            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
+                    methodName);
         }
     }
 
     public static JavaMethod createDeclared(IRuby runtime, Class javaClass, String methodName, Class[] argumentTypes) {
         try {
             Method method = javaClass.getDeclaredMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
-            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'");
+            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
+                    methodName);
         }
     }
 
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     protected int getArity() {
         return method.getParameterTypes().length;
     }
 
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     public IRubyObject invoke(IRubyObject[] args) {
         if (args.length != 1 + getArity()) {
             throw getRuntime().newArgumentError(args.length, 1 + getArity());
         }
 
         IRubyObject invokee = args[0];
         if (! (invokee instanceof JavaObject)) {
             throw getRuntime().newTypeError("invokee not a java object");
         }
         Object javaInvokee = ((JavaObject) invokee).getValue();
         Object[] arguments = new Object[args.length - 1];
         System.arraycopy(args, 1, arguments, 0, arguments.length);
         convertArguments(arguments);
 
         if (! method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                               "got" + javaInvokee.getClass().getName() + " wanted " +
                                               method.getDeclaringClass().getName() + ")");
         }
         return invokeWithExceptionHandling(javaInvokee, arguments);
     }
 
     public IRubyObject invoke_static(IRubyObject[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(arguments);
         return invokeWithExceptionHandling(null, arguments);
     }
 
     public IRubyObject return_type() {
         Class klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
     private IRubyObject invokeWithExceptionHandling(Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return JavaObject.wrap(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect());
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
             // This point is only reached if there was an exception handler installed.
             return getRuntime().getNil();
         }
     }
 
     private void convertArguments(Object[] arguments) {
         Class[] parameterTypes = parameterTypes();
         for (int i = 0; i < arguments.length; i++) {
             arguments[i] = JavaUtil.convertArgument(arguments[i], parameterTypes[i]);
         }
     }
 
     protected Class[] parameterTypes() {
         return method.getParameterTypes();
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     protected int getModifiers() {
         return method.getModifiers();
     }
 
     protected AccessibleObject accesibleObject() {
         return method;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index 0d3bdfe020..ce2a49fcc9 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
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
 package org.jruby.javasupport;
 
 import java.lang.ref.WeakReference;
 import java.net.URL;
 import java.net.URLClassLoader;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.WeakHashMap;
 
 import org.jruby.IRuby;
 import org.jruby.RubyProc;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class JavaSupport {
     private IRuby runtime;
 
     private Map exceptionHandlers = new HashMap();
 
     private ClassLoader javaClassLoader = this.getClass().getClassLoader();
 
     private Map instanceCache = Collections.synchronizedMap(new WeakHashMap(100));
 
     public JavaSupport(IRuby ruby) {
         this.runtime = ruby;
     }
 
     public Class loadJavaClass(String className) {
         try {
             Class result = primitiveClass(className);
             if (result == null) {
                 return Class.forName(className, true, javaClassLoader);
             }
             return result;
         } catch (ClassNotFoundException cnfExcptn) {
-            throw runtime.newNameError("cannot load Java class " + className);
+            throw runtime.newNameError("cannot load Java class " + className, className);
         }
     }
     
     public JavaClass getJavaClassFromCache(Class clazz) {
         WeakReference ref = (WeakReference) instanceCache.get(clazz);
         
         return ref == null ? null : (JavaClass) ref.get();
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         instanceCache.put(clazz.javaClass(), new WeakReference(clazz));
     }
     
     public void addToClasspath(URL url) {
         javaClassLoader = URLClassLoader.newInstance(new URL[] { url }, javaClassLoader);
     }
 
     public void defineExceptionHandler(String exceptionClass, RubyProc handler) {
         exceptionHandlers.put(exceptionClass, handler);
     }
 
     public void handleNativeException(Throwable exception) {
         if (exception instanceof RaiseException) {
             throw (RaiseException) exception;
         }
         Class excptnClass = exception.getClass();
         RubyProc handler = (RubyProc)exceptionHandlers.get(excptnClass.getName());
         while (handler == null &&
                excptnClass != Throwable.class) {
             excptnClass = excptnClass.getSuperclass();
         }
         if (handler != null) {
             handler.call(new IRubyObject[]{JavaUtil.convertJavaToRuby(runtime, exception)});
         } else {
             throw createRaiseException(exception);
         }
     }
 
     private RaiseException createRaiseException(Throwable exception) {
         RaiseException re = RaiseException.createNativeRaiseException(runtime, exception);
         
         return re;
     }
 
     private static Class primitiveClass(String name) {
         if (name.equals("long")) {
             return Long.TYPE;
         } else if (name.equals("int")) {
             return Integer.TYPE;
         } else if (name.equals("boolean")) {
             return Boolean.TYPE;
         } else if (name.equals("char")) {
             return Character.TYPE;
         } else if (name.equals("short")) {
             return Short.TYPE;
         } else if (name.equals("byte")) {
             return Byte.TYPE;
         } else if (name.equals("float")) {
             return Float.TYPE;
         } else if (name.equals("double")) {
             return Double.TYPE;
         }
         return null;
     }
 
     public ClassLoader getJavaClassLoader() {
         return javaClassLoader;
     }
     
     public JavaObject getJavaObjectFromCache(Object object) {
     	WeakReference ref = (WeakReference)instanceCache.get(object);
     	if (ref == null) {
     		return null;
     	}
     	JavaObject javaObject = (JavaObject) ref.get();
     	if (javaObject != null && javaObject.getValue() == object) {
     		return javaObject;
     	}
         return null;
     }
     
     public void putJavaObjectIntoCache(JavaObject object) {
     	instanceCache.put(object.getValue(), new WeakReference(object));
     }
 }
diff --git a/src/org/jruby/runtime/ReadonlyGlobalVariable.java b/src/org/jruby/runtime/ReadonlyGlobalVariable.java
index 292cbeaa33..a62735997c 100644
--- a/src/org/jruby/runtime/ReadonlyGlobalVariable.java
+++ b/src/org/jruby/runtime/ReadonlyGlobalVariable.java
@@ -1,44 +1,44 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.runtime;
 
 import org.jruby.IRuby;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ReadonlyGlobalVariable extends GlobalVariable {
 
     public ReadonlyGlobalVariable(IRuby runtime, String name, IRubyObject value) {
         super(runtime, name, value);
     }
 
     public IRubyObject set(IRubyObject value) {
-        throw runtime.newNameError(name() + " is a read-only variable");
+        throw runtime.newNameError(name() + " is a read-only variable", name());
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index f6d547ebaa..c01e3b6cb7 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1149 +1,1150 @@
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
     
     private void restoreBlockState(Block block, RubyModule klass) {
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
         scopeIndex--;
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
         pushFrame(new Frame(this, self, args, lastFunc, lastClass, getPosition(), getCurrentIter(), getCurrentBlock()));
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
     
     /////////////////////////// ITER MANAGEMENT //////////////////////////
     private Iter popIter() {
         return (Iter) iterStack[iterIndex--];
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
 
     public IRubyObject callSuper(IRubyObject[] args) {
     	Frame frame = getCurrentFrame();
     	
         if (frame.getLastClass() == null) {
-            throw runtime.newNameError("superclass method '" + frame.getLastFunc() + "' must be enabled by enableSuper().");
+            String name = frame.getLastFunc();
+            throw runtime.newNameError("superclass method '" + name + "' must be enabled by enableSuper().", name);
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
             postYield(currentBlock);
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
             postYield(yieldBlock);
             postProcBlockCall();
         }
     }
     
     private IRubyObject yieldInternal(Block yieldBlock, IRubyObject value, IRubyObject self, RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = yieldBlock.getSelf();
         }
         
         // FIXME: during refactoring, it was determined that all calls to yield are passing false for yieldProc; is this still needed?
         IRubyObject[] args = getBlockArgs(value, self, false, aValue, yieldBlock);
         
         while (true) {
             try {
                 // FIXME: is it appropriate to use the current frame's (the block's frame's) lastClass?
                 IRubyObject result = yieldBlock.getMethod().call(runtime.getCurrentContext(), self, getCurrentFrame().getLastClass(), null, args, false);
                 
                 return result;
             } catch (JumpException je) {
                 if (je.getJumpType() == JumpException.JumpType.RedoJump) {
                     // do nothing, allow loop to redo
                 } else {
                     throw je;
                 }
             }
         }
     }
 
     private IRubyObject[] getBlockArgs(IRubyObject value, IRubyObject self, boolean yieldProc, boolean aValue, Block currentBlock) {
         Node blockVar = currentBlock.getVar();
         //FIXME: block arg handling is mucked up in strange ways and NEED to
         // be fixed. Especially with regard to Enumerable. See RubyEnumerable#eachToList too.
         if(blockVar == null) {
             return new IRubyObject[]{value};
         }
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
                     runtime.getWarnings().warn("multiple values for a block parameter (" + length + " for 1)");
                 }
             } else if (value == null) { 
                 runtime.getWarnings().warn("multiple values for a block parameter (0 for 1)");
             }
 
             AssignmentVisitor.assign(this, getFrameSelf(), blockVar, value, yieldProc); 
         }
         return ArgsUtil.arrayify(value);
     }
     
     public IRubyObject mAssign(IRubyObject self, MultipleAsgnNode node, RubyArray value, boolean pcall) {
         // Assign the values.
         int valueLen = value.getLength();
         int varLen = node.getHeadNode() == null ? 0 : node.getHeadNode().size();
         
         Iterator iter = node.getHeadNode() != null ? node.getHeadNode().iterator() : Collections.EMPTY_LIST.iterator();
         for (int i = 0; i < valueLen && iter.hasNext(); i++) {
             Node lNode = (Node) iter.next();
             AssignmentVisitor.assign(this, getFrameSelf(), lNode, value.entry(i), pcall);
         }
 
         if (pcall && iter.hasNext()) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         if (node.getArgsNode() != null) {
             if (node.getArgsNode() instanceof StarNode) {
                 // no check for '*'
             } else if (varLen < valueLen) {
                 ArrayList newList = new ArrayList(value.getList().subList(varLen, valueLen));
                 AssignmentVisitor.assign(this, getFrameSelf(), node.getArgsNode(), runtime.newArray(newList), pcall);
             } else {
                 AssignmentVisitor.assign(this, getFrameSelf(), node.getArgsNode(), runtime.newArray(0), pcall);
             }
         } else if (pcall && valueLen < varLen) {
             throw runtime.newArgumentError("Wrong # of arguments (" + valueLen + " for " + varLen + ")");
         }
 
         while (iter.hasNext()) {
             AssignmentVisitor.assign(this, getFrameSelf(), (Node)iter.next(), runtime.getNil(), pcall);
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
         return (SinglyLinkedList)crefStack[crefIndex];
     }
     
     public void setCRef(SinglyLinkedList newCRef) {
         crefStack[++crefIndex] = newCRef;
         expandCrefsIfNecessary();
     }
     
     public void unsetCRef() {
         crefIndex--;
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
         }
         
         return module;
     }
 
     public void pushRubyClass(RubyModule currentModule) {
         assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         return (RubyModule)parentStack[parentIndex--];
     }
 	
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = (RubyModule)parentStack[parentIndex];
 
         return parentModule.getNonIncludedClass();
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
         int traceSize = frameIndex - level;
         
         if (traceSize <= 0) {
         	return backtrace;
         }
         
         if (nativeException) {
             // assert level == 0;
             addBackTraceElement(backtrace, (Frame) frameStack[frameIndex], null);
         }
         
         for (int i = traceSize; i > 0; i--) {
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
 
     private Block preYieldCurrentBlock(RubyModule klass) {
         Block currentBlock = getCurrentFrame().getBlockArg();
 
         restoreBlockState(currentBlock, klass);
 
         return currentBlock;
     }
     
     private void preYieldSpecificBlock(Block specificBlock, RubyModule klass) {
         restoreBlockState(specificBlock, klass);
     }
 
     public void preEvalWithBinding(Block block) {
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
 }
diff --git a/src/org/jruby/runtime/marshal/UnmarshalStream.java b/src/org/jruby/runtime/marshal/UnmarshalStream.java
index 7f573daaf8..6dc4263005 100644
--- a/src/org/jruby/runtime/marshal/UnmarshalStream.java
+++ b/src/org/jruby/runtime/marshal/UnmarshalStream.java
@@ -1,284 +1,284 @@
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
 package org.jruby.runtime.marshal;
 
 import java.io.FilterInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.RubyStruct;
 import org.jruby.RubySymbol;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Unmarshals objects from strings or streams in Ruby's marsal format.
  *
  * @author Anders
  */
 public class UnmarshalStream extends FilterInputStream {
     protected final IRuby runtime;
     private UnmarshalCache cache;
 
     public UnmarshalStream(IRuby runtime, InputStream in) throws IOException {
         super(in);
         this.runtime = runtime;
         this.cache = new UnmarshalCache(runtime);
 
         in.read(); // Major
         in.read(); // Minor
     }
 
     public IRubyObject unmarshalObject() throws IOException {
         return unmarshalObject(null);
     }
 
     public IRubyObject unmarshalObject(IRubyObject proc) throws IOException {
         int type = readUnsignedByte();
         IRubyObject result;
         if (cache.isLinkType(type)) {
             result = cache.readLink(this, type);
         } else {
         	result = unmarshalObjectDirectly(type, proc);
         }
         return result;
     }
 
     public void registerLinkTarget(IRubyObject newObject) {
         cache.register(newObject);
     }
 
     private IRubyObject unmarshalObjectDirectly(int type, IRubyObject proc) throws IOException {
     	IRubyObject rubyObj = null;
         switch (type) {
         	case 'I':
                 rubyObj = unmarshalObject(proc);
                 defaultInstanceVarsUnmarshal(rubyObj, proc);
         		break;
             case '0' :
                 rubyObj = runtime.getNil();
                 break;
             case 'T' :
                 rubyObj = runtime.getTrue();
                 break;
             case 'F' :
                 rubyObj = runtime.getFalse();
                 break;
             case '"' :
                 rubyObj = RubyString.unmarshalFrom(this);
                 break;
             case 'i' :
                 rubyObj = RubyFixnum.unmarshalFrom(this);
                 break;
             case 'f' :
             	rubyObj = RubyFloat.unmarshalFrom(this);
             	break;
             case ':' :
                 rubyObj = RubySymbol.unmarshalFrom(this);
                 break;
             case '[' :
                 rubyObj = RubyArray.unmarshalFrom(this);
                 break;
             case '{' :
                 rubyObj = RubyHash.unmarshalFrom(this);
                 break;
             case 'c' :
                 rubyObj = RubyClass.unmarshalFrom(this);
                 break;
             case 'm' :
                 rubyObj = RubyModule.unmarshalFrom(this);
                 break;
             case 'l' :
                 rubyObj = RubyBignum.unmarshalFrom(this);
                 break;
             case 'S' :
                 rubyObj = RubyStruct.unmarshalFrom(this);
                 break;
             case 'o' :
                 rubyObj = defaultObjectUnmarshal(proc);
                 break;
             case 'u' :
                 rubyObj = userUnmarshal();
                 break;
             case 'U' :
                 rubyObj = userNewUnmarshal();
                 break;
             case 'C' :
             	rubyObj = uclassUnmarshall();
             	break;
             default :
                 throw getRuntime().newArgumentError("dump format error(" + (char)type + ")");
         }
         
         if (proc != null) {
 			proc.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[] {rubyObj});
 		}
         return rubyObj;
     }
 
 
     public IRuby getRuntime() {
         return runtime;
     }
 
     public int readUnsignedByte() throws IOException {
         int result = read();
         if (result == -1) {
             throw new IOException("Unexpected end of stream");
         }
         return result;
     }
 
     public byte readSignedByte() throws IOException {
         int b = readUnsignedByte();
         if (b > 127) {
             return (byte) (b - 256);
         }
 		return (byte) b;
     }
 
     public String unmarshalString() throws IOException {
         int length = unmarshalInt();
         byte[] buffer = new byte[length];
         int bytesRead = read(buffer);
         if (bytesRead != length) {
             throw new IOException("Unexpected end of stream");
         }
         return RubyString.bytesToString(buffer);
     }
 
     public int unmarshalInt() throws IOException {
         int c = readSignedByte();
         if (c == 0) {
             return 0;
         } else if (4 < c && c < 128) {
             return c - 5;
         } else if (-129 < c && c < -4) {
             return c + 5;
         }
         long result;
         if (c > 0) {
             result = 0;
             for (int i = 0; i < c; i++) {
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         } else {
             c = -c;
             result = -1;
             for (int i = 0; i < c; i++) {
                 result &= ~((long) 0xff << (8 * i));
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         }
         return (int) result;
     }
 
     private IRubyObject defaultObjectUnmarshal(IRubyObject proc) throws IOException {
         RubySymbol className = (RubySymbol) unmarshalObject();
 
         // ... FIXME: handle if class doesn't exist ...
 
         RubyClass type = (RubyClass) runtime.getClassFromPath(className.asSymbol());
 
         assert type != null : "type shouldn't be null.";
 
         IRubyObject result = new RubyObject(runtime, type);
         registerLinkTarget(result);
 
         defaultInstanceVarsUnmarshal(result, proc);
 
         return result;
     }
     
     private void defaultInstanceVarsUnmarshal(IRubyObject object, IRubyObject proc) throws IOException {
     	int count = unmarshalInt();
     	
     	for (int i = 0; i < count; i++) {
     		object.setInstanceVariable(unmarshalObject().asSymbol(), unmarshalObject(proc));
     	}
     }
     
     private IRubyObject uclassUnmarshall() throws IOException {
     	RubySymbol className = (RubySymbol)unmarshalObject();
     	
     	RubyClass type = (RubyClass)runtime.getClassFromPath(className.asSymbol());
     	
     	IRubyObject result = unmarshalObject();
     	
     	result.setMetaClass(type);
     	
     	return result;
     }
 
     private IRubyObject userUnmarshal() throws IOException {
         String className = unmarshalObject().asSymbol();
         String marshaled = unmarshalString();
         RubyModule classInstance;
         try {
             classInstance = runtime.getClassFromPath(className);
         } catch (RaiseException e) {
-            if (e.getException().getType() == runtime.getModule("NameError")) {
+            if (e.getException().isKindOf(runtime.getModule("NameError"))) {
                 throw runtime.newArgumentError("undefined class/module " + className);
             } 
                 
             throw e;
         }
         if (!classInstance.respondsTo("_load")) {
             throw runtime.newTypeError("class " + classInstance.getName() + " needs to have method `_load'");
         }
         IRubyObject result = classInstance.callMethod(getRuntime().getCurrentContext(),
             "_load", runtime.newString(marshaled));
         registerLinkTarget(result);
         return result;
     }
 
     private IRubyObject userNewUnmarshal() throws IOException {
         String className = unmarshalObject().asSymbol();
         IRubyObject marshaled = unmarshalObject();
         RubyClass classInstance = runtime.getClass(className);
         IRubyObject result = classInstance.newInstance(new IRubyObject[0]);;
         result.callMethod(getRuntime().getCurrentContext(),"marshal_load", marshaled);
         registerLinkTarget(result);
         return result;
     }
 }
diff --git a/test/org/jruby/test/BaseMockRuby.java b/test/org/jruby/test/BaseMockRuby.java
index 86ae97fd86..b9ce0d26ae 100644
--- a/test/org/jruby/test/BaseMockRuby.java
+++ b/test/org/jruby/test/BaseMockRuby.java
@@ -1,693 +1,693 @@
 package org.jruby.test;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.util.Hashtable;
 import java.util.List;
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
 
 	public RubyClass defineClass(String name, RubyClass superClass) {
 		throw new MockException();
 	}
 
 	public RubyClass defineClassUnder(String name, RubyClass superClass,
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
 
 	public boolean isBlockGiven() {
 		throw new MockException();
 	}
 
 	public boolean isFBlockGiven() {
 		throw new MockException();
 		
 	}
 
 	public void setVerbose(IRubyObject verbose) {
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
 
 	public RubyFileStat newRubyFileStat(File file) {
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
 
-	public RaiseException newNoMethodError(String message) {
+	public RaiseException newNoMethodError(String message, String name) {
 		throw new MockException();
 		
 	}
 
-	public RaiseException newNameError(String message) {
+	public RaiseException newNameError(String message, String name) {
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
 }
diff --git a/test/testException2.rb b/test/testException2.rb
index 2813262b9b..f4ea712fa0 100644
--- a/test/testException2.rb
+++ b/test/testException2.rb
@@ -1,101 +1,111 @@
 require 'test/minirunit'
 test_check "Test Exception (2)"
 
 def raise_test(thing_to_raise, exception_to_rescue)
   e = nil
   begin
     raise thing_to_raise
   rescue exception_to_rescue
     e = $!
   end
   test_equal(exception_to_rescue, e.class)
 end
 
 test_ok(ArgumentError < Exception)
 
 raise_test(ArgumentError.new("hello"), ArgumentError)
 raise_test("hello", RuntimeError)
 
 class SomeOtherException < StandardError
 end
 e = Exception.new
 test_ok(!e.kind_of?(SomeOtherException))
 test_ok(!e.kind_of?(StandardError))
 test_ok(e.kind_of?(Exception))
 begin
   raise "whoah!"
 rescue SomeOtherException
   test_fail()
 rescue Exception
   test_ok(true)
 end
 
 class Foo
 end
 
 class Har 
   def exception(message)
     Bar.new(message)
   end
 end
 
 class Bar < Exception
   def exception(message)
     1
   end
 end
 
 class Gar < Exception
   def exception(message)
      Bar.new(message)
   end
 end
 
 test_exception(TypeError) { raise nil }
 test_exception(TypeError) { raise Foo }
 test_exception(TypeError) { raise Foo, "HEH" }
 test_exception(TypeError) { raise Foo, "HEH", caller }
 test_exception(TypeError) { raise Har }
 test_exception(TypeError) { raise Har, "HEH" }
 test_exception(TypeError) { raise Har, "HEH", caller }
 test_exception(Bar) { raise Bar }
 test_exception(Bar) { raise Bar, "HEH" }
 test_exception(Bar) { raise Bar, "HEH", caller }
 test_exception(Bar) { raise Gar.new, "HEH" }
 test_exception(TypeError) { raise Bar.new, "HEH" }
 test_exception(TypeError) { raise Bar.new, "HEH", caller }
 
 # empty rescue block should cause method to return nil
 def do_except
   raise Exception.new
 rescue Exception
 end
 
 test_equal(nil, do_except)
 
 # Check exception hierarchy structure
 test_ok(NoMemoryError < Exception)
 test_ok(ScriptError < Exception)
 test_ok(LoadError < ScriptError)
 test_ok(NotImplementedError < ScriptError)
 test_ok(SyntaxError < ScriptError)
 # we don't implement SignalError or descendants
 test_ok(StandardError < Exception)
 test_ok(ArgumentError < StandardError)
 test_ok(IOError < StandardError)
 test_ok(EOFError < IOError)
 test_ok(IndexError < StandardError)
 test_ok(LocalJumpError < StandardError)
 test_ok(NameError < StandardError)
 test_ok(NoMethodError < NameError)
 test_ok(RangeError < StandardError)
 test_ok(FloatDomainError < RangeError)
 test_ok(RegexpError < StandardError)
 test_ok(RuntimeError < StandardError)
 test_ok(SecurityError < StandardError)
 # we don't implement SystemCallError
 test_ok(ThreadError < StandardError)
 test_ok(TypeError < StandardError)
 test_ok(ZeroDivisionError < StandardError)
 test_ok(SystemExit < Exception)
-test_ok(SystemStackError < Exception)
\ No newline at end of file
+test_ok(SystemStackError < Exception)
+
+n = NameError.new
+test_equal("NameError", n.message)
+test_equal(nil, n.name)
+n = NameError.new("foo")
+test_equal("foo", n.message)
+test_equal(nil, n.name)
+n = NameError.new("foo", "bar")
+test_equal("foo", n.message)
+test_equal("bar", n.name)
