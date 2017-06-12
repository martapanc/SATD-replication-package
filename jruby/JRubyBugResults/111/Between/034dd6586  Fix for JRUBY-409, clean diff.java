diff --git a/src/org/jruby/IRuby.java b/src/org/jruby/IRuby.java
index 54a3eddeaf..e36b414309 100644
--- a/src/org/jruby/IRuby.java
+++ b/src/org/jruby/IRuby.java
@@ -1,433 +1,435 @@
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
 import org.jruby.runtime.MethodSelectorTable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
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
     
     public MethodSelectorTable getSelectorTable();
     
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
     
     public KCode getKCode();
     
     public void setKCode(KCode kcode);
 
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
     
     public JRubyClassLoader getJRubyClassLoader();
 
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
 
 	public RubyFileStat newRubyFileStat(String file);
 
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
+    
+    public RaiseException newFloatDomainError(String message);
 
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
 
     public Profile getProfile();
     
     public String getJRubyHome();
 }
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 29edef1914..479a9733b8 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1645 +1,1649 @@
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
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
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
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.libraries.ThreadLibrary;
 import org.jruby.ext.openssl.RubyOpenSSL;
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
 import org.jruby.runtime.builtin.meta.ArrayMetaClass;
-import org.jruby.runtime.builtin.meta.BignumMetaClass;
 import org.jruby.runtime.builtin.meta.BindingMetaClass;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
-import org.jruby.runtime.builtin.meta.FixnumMetaClass;
 import org.jruby.runtime.builtin.meta.HashMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
-import org.jruby.runtime.builtin.meta.IntegerMetaClass;
 import org.jruby.runtime.builtin.meta.ModuleMetaClass;
-import org.jruby.runtime.builtin.meta.NumericMetaClass;
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
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby implements IRuby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "etc", "nkf" };
     
     private CacheMap cacheMap = new CacheMap(this);
     private ThreadService threadService = new ThreadService(this);
     private Hashtable runtimeInformation;
     private final MethodSelectorTable selectorTable;
 
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
 
-    private FixnumMetaClass fixnumClass;
+    private RubyClass fixnumClass;
     
     private IRubyObject tmsStruct;
 
     private Profile profile;
 
     private String jrubyHome;
     
     private KCode kcode = KCode.NIL;
 
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
             // do the compile
             StandardASMCompiler compiler = new StandardASMCompiler(node);
             NodeCompilerFactory.getCompiler(node).compile(node, compiler);
             
             Class scriptClass = compiler.loadClass(this);
             
             Script script = (Script)scriptClass.newInstance();
             // FIXME: Pass something better for args and block here?
             return script.run(getCurrentContext(), getTopSelf(), IRubyObject.NULL_ARRAY, null);
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
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
     
     public KCode getKCode() {
         return kcode;
     }
     
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
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
-            new NumericMetaClass(this).initializeClass();
+            RubyNumeric.createNumericClass(this);
         }
+
+        if(profile.allowClass("Integer")) {
+            RubyInteger.createIntegerClass(this);
+        }
+        
         if(profile.allowClass("Fixnum")) {
-            new IntegerMetaClass(this).initializeClass();        
-            fixnumClass = new FixnumMetaClass(this);
-            fixnumClass.initializeClass();
+            fixnumClass = RubyFixnum.createFixnumClass(this);
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
-            RubyFloat.createFloatClass(this);
+           RubyFloat.createFloatClass(this);
         }        
 
         if(profile.allowClass("Bignum")) {
-            new BignumMetaClass(this).initializeClass();
+           // new BignumMetaClass(this).initializeClass();
+            RubyBignum.createBignumClass(this);
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
-        // FIXME: Actually this somewhere
+        // FIXME: Actually this somewhere <- fixed
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
     
+    public RaiseException newFloatDomainError(String message){
+        return newRaiseException(getClass("FloatDomainError"), message);
+    }
+    
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
diff --git a/src/org/jruby/RubyBigDecimal.java b/src/org/jruby/RubyBigDecimal.java
index 894ac7d8d4..37f8e6b78c 100644
--- a/src/org/jruby/RubyBigDecimal.java
+++ b/src/org/jruby/RubyBigDecimal.java
@@ -1,614 +1,614 @@
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
 package org.jruby;
 
 import java.math.BigDecimal;
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyBigDecimal extends RubyNumeric {
     private static final ObjectAllocator BIGDECIMAL_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(IRuby runtime, RubyClass klass) {
             return new RubyBigDecimal(runtime, klass);
         }
     };
     
     public static RubyClass createBigDecimal(IRuby runtime) {
         RubyClass result = runtime.defineClass("BigDecimal",runtime.getClass("Numeric"), BIGDECIMAL_ALLOCATOR);
 
         result.setConstant("ROUND_DOWN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_DOWN));
         result.setConstant("SIGN_POSITIVE_INFINITE",RubyNumeric.int2fix(runtime,3));
         result.setConstant("EXCEPTION_OVERFLOW",RubyNumeric.int2fix(runtime,1));
         result.setConstant("SIGN_POSITIVE_ZERO",RubyNumeric.int2fix(runtime,1));
         result.setConstant("EXCEPTION_ALL",RubyNumeric.int2fix(runtime,255));
         result.setConstant("ROUND_CEILING",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_CEILING));
         result.setConstant("ROUND_UP",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_UP));
         result.setConstant("SIGN_NEGATIVE_FINITE",RubyNumeric.int2fix(runtime,-2));
         result.setConstant("EXCEPTION_UNDERFLOW",RubyNumeric.int2fix(runtime, 4));
         result.setConstant("SIGN_NaN",RubyNumeric.int2fix(runtime, 0));
         result.setConstant("BASE",RubyNumeric.int2fix(runtime,10000));
         result.setConstant("ROUND_HALF_DOWN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_DOWN));
         result.setConstant("ROUND_MODE",RubyNumeric.int2fix(runtime,256));
         result.setConstant("SIGN_POSITIVE_FINITE",RubyNumeric.int2fix(runtime,2));
         result.setConstant("EXCEPTION_INFINITY",RubyNumeric.int2fix(runtime,1));
         result.setConstant("ROUND_HALF_EVEN",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_EVEN));
         result.setConstant("ROUND_HALF_UP",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_HALF_UP));
         result.setConstant("SIGN_NEGATIVE_INFINITE",RubyNumeric.int2fix(runtime,-3));
         result.setConstant("EXCEPTION_ZERODIVIDE",RubyNumeric.int2fix(runtime,1));
         result.setConstant("SIGN_NEGATIVE_ZERO",RubyNumeric.int2fix(runtime,-1));
         result.setConstant("EXCEPTION_NaN",RubyNumeric.int2fix(runtime,2));
         result.setConstant("ROUND_FLOOR",RubyNumeric.int2fix(runtime,BigDecimal.ROUND_FLOOR));
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyBigDecimal.class);
 
         runtime.getModule("Kernel").defineModuleFunction("BigDecimal",callbackFactory.getOptSingletonMethod("newBigDecimal"));
         result.defineFastSingletonMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
         result.defineFastSingletonMethod("ver", callbackFactory.getSingletonMethod("ver"));
         result.defineSingletonMethod("_load", callbackFactory.getSingletonMethod("_load",IRubyObject.class));
         result.defineFastSingletonMethod("double_fig", callbackFactory.getSingletonMethod("double_fig"));
         result.defineFastSingletonMethod("limit", callbackFactory.getOptSingletonMethod("limit"));
         result.defineFastSingletonMethod("mode", callbackFactory.getSingletonMethod("mode",IRubyObject.class,IRubyObject.class));
 
         result.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         result.defineFastMethod("%", callbackFactory.getMethod("mod",IRubyObject.class));
         result.defineFastMethod("modulo", callbackFactory.getMethod("mod",IRubyObject.class));
         result.defineFastMethod("*", callbackFactory.getOptMethod("mult"));
         result.defineFastMethod("mult", callbackFactory.getOptMethod("mult"));
         result.defineFastMethod("**", callbackFactory.getMethod("power",IRubyObject.class));
         result.defineFastMethod("power", callbackFactory.getMethod("power",IRubyObject.class));
         result.defineFastMethod("+", callbackFactory.getOptMethod("add"));
         result.defineFastMethod("add", callbackFactory.getOptMethod("add"));
         result.defineFastMethod("-", callbackFactory.getOptMethod("sub"));
         result.defineFastMethod("sub", callbackFactory.getOptMethod("sub"));
         result.defineFastMethod("/", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("div", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("quo", callbackFactory.getOptMethod("div"));
         result.defineFastMethod("<=>", callbackFactory.getMethod("spaceship",IRubyObject.class));
         result.defineFastMethod("==", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("===", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("eql?", callbackFactory.getMethod("eql_p",IRubyObject.class));
         result.defineFastMethod("!=", callbackFactory.getMethod("ne",IRubyObject.class));
         result.defineFastMethod("<", callbackFactory.getMethod("lt",IRubyObject.class));
         result.defineFastMethod("<=", callbackFactory.getMethod("le",IRubyObject.class));
         result.defineFastMethod(">", callbackFactory.getMethod("gt",IRubyObject.class));
         result.defineFastMethod(">=", callbackFactory.getMethod("ge",IRubyObject.class));
         result.defineFastMethod("abs", callbackFactory.getMethod("abs"));
         result.defineFastMethod("ceil", callbackFactory.getMethod("ceil",IRubyObject.class));
         result.defineFastMethod("coerce", callbackFactory.getMethod("coerce",IRubyObject.class));
         result.defineFastMethod("divmod", callbackFactory.getMethod("divmod",IRubyObject.class)); 
         result.defineFastMethod("exponent", callbackFactory.getMethod("exponent"));
         result.defineFastMethod("finite?", callbackFactory.getMethod("finite_p"));
         result.defineFastMethod("fix", callbackFactory.getMethod("fix"));
         result.defineFastMethod("floor", callbackFactory.getMethod("floor",IRubyObject.class));
         result.defineFastMethod("frac", callbackFactory.getMethod("frac"));
         result.defineFastMethod("infinite?", callbackFactory.getMethod("infinite_p"));
         result.defineFastMethod("inspect", callbackFactory.getMethod("inspect"));
         result.defineFastMethod("nan?", callbackFactory.getMethod("nan_p"));
         result.defineFastMethod("nonzero?", callbackFactory.getMethod("nonzero_p"));
         result.defineFastMethod("precs", callbackFactory.getMethod("precs"));
         result.defineFastMethod("remainder", callbackFactory.getMethod("remainder",IRubyObject.class));
         result.defineFastMethod("round", callbackFactory.getOptMethod("round"));
         result.defineFastMethod("sign", callbackFactory.getMethod("sign"));
         result.defineFastMethod("split", callbackFactory.getMethod("split"));
         result.defineFastMethod("sqrt", callbackFactory.getOptMethod("sqrt"));
         result.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
         result.defineFastMethod("to_i", callbackFactory.getMethod("to_i"));
         result.defineFastMethod("to_int", callbackFactory.getMethod("to_int"));
         result.defineFastMethod("to_s", callbackFactory.getOptMethod("to_s"));
         result.defineFastMethod("truncate", callbackFactory.getOptMethod("truncate"));
         result.defineFastMethod("zero?", callbackFactory.getMethod("zero_p"));
 
         result.setClassVar("VpPrecLimit", RubyFixnum.zero(runtime));
 
         return result;
     }
 
     private BigDecimal value;
 
     public RubyBigDecimal(IRuby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     public RubyBigDecimal(IRuby runtime, BigDecimal value) {
         super(runtime, runtime.getClass("BigDecimal"));
         this.value = value;
     }
 
     public static RubyBigDecimal newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         RubyBigDecimal result = (RubyBigDecimal)klass.allocate();
         result.callInit(args);
         
         return result;
     }
 
     public static RubyBigDecimal newBigDecimal(IRubyObject recv, IRubyObject[] args) {
         return newInstance(recv.getRuntime().getClass("BigDecimal"), args);
     }
 
     public static IRubyObject ver(IRubyObject recv) {
         return recv.getRuntime().newString("1.0.1");
     }
 
     public static IRubyObject _load(IRubyObject recv, IRubyObject p1) {
         // TODO: implement
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject double_fig(IRubyObject recv) {
         return recv.getRuntime().newFixnum(20);
     }
     
     public static IRubyObject limit(IRubyObject recv, IRubyObject[] args) {
         RubyModule c = (RubyModule)recv;
         IRubyObject nCur = c.getClassVar("VpPrecLimit");
         if(recv.checkArgumentCount(args,0,1) == 1) {
             if(args[0].isNil()) {
                 return nCur;
             }
             c.setClassVar("VpPrecLimit",args[0]);
         }
 
         return nCur;
     }
 
     public static IRubyObject mode(IRubyObject recv, IRubyObject mode, IRubyObject value) {
         System.err.println("unimplemented: mode");
         // TODO: implement
         return recv.getRuntime().getNil();
     }
 
     private RubyBigDecimal getVpValue(IRubyObject v, boolean must) {
         if(v instanceof RubyBigDecimal) {
             return (RubyBigDecimal)v;
         } else if(v instanceof RubyFixnum || v instanceof RubyBignum) {
             String s = v.toString();
             return newInstance(getRuntime().getClass("BigDecimal"),new IRubyObject[]{getRuntime().newString(s)});
         }
         if(must) {
             throw getRuntime().newTypeError(trueFalseNil(v.getMetaClass().getName() + " can't be coerced into BigDecimal"));
         }
         return null;
     }
 
     public IRubyObject initialize(IRubyObject[] args) {
         String ss = args[0].convertToString().toString();
         if(ss.indexOf('.') != -1) {
             ss = removeTrailingZeroes(ss);
         }
         try {
             this.value = new BigDecimal(ss);
         } catch(NumberFormatException e) {
             this.value = new BigDecimal("0");
         }
 
         return this;
     }
 
     private RubyBigDecimal setResult() {
         return setResult(0);
     }
 
     private RubyBigDecimal setResult(int scale) {
         int prec = RubyFixnum.fix2int(getRuntime().getClass("BigDecimal").getClassVar("VpPrecLimit"));
         int prec2 = Math.max(scale,prec);
         if(prec2 > 0 && this.value.scale() > (prec2-exp())) {
             this.value = this.value.setScale(prec2-exp(),BigDecimal.ROUND_HALF_UP);
         }
         return this;
     }
 
     public IRubyObject mod(IRubyObject arg) {
         System.err.println("unimplemented: mod");
         // TODO: implement
         return this;
     }
 
     public IRubyObject mult(IRubyObject[] args) {
         RubyBigDecimal val = getVpValue(args[0],false);
         if(val == null) {
             return callCoerced("*",args[0]);
         }
 
         return new RubyBigDecimal(getRuntime(),value.multiply(val.value)).setResult();
     }
 
     public IRubyObject power(IRubyObject arg) {
         // TODO: better implementation
         BigDecimal val = value;
         int times = RubyNumeric.fix2int(arg.convertToInteger());
         for(int i=0;i<times;i++) {
             val = val.multiply(val);
         }
         return new RubyBigDecimal(getRuntime(),val).setResult();
     }
 
     public IRubyObject add(IRubyObject[] args) {
         RubyBigDecimal val = getVpValue(args[0],false);
         if(val == null) {
             return callCoerced("+",args[0]);
         }
         return new RubyBigDecimal(getRuntime(),value.add(val.value)).setResult();
     }
 
     public IRubyObject sub(IRubyObject[] args) {
         RubyBigDecimal val = getVpValue(args[0],false);
         if(val == null) {
             return callCoerced("-",args[0]);
         }
         return new RubyBigDecimal(getRuntime(),value.subtract(val.value)).setResult();
     }
 
     public IRubyObject div(IRubyObject[] args) {
         int scale = 0;
         if(checkArgumentCount(args,1,2) == 2) {
             scale = RubyNumeric.fix2int(args[1]);
         }
 
         RubyBigDecimal val = getVpValue(args[0],false);
         if(val == null) {
             return callCoerced("/",args[0]);
         }
 
         if(scale == 0) {
             return new RubyBigDecimal(getRuntime(),value.divide(val.value,200,BigDecimal.ROUND_HALF_UP)).setResult();
         } else {
             return new RubyBigDecimal(getRuntime(),value.divide(val.value,200,BigDecimal.ROUND_HALF_UP)).setResult(scale);
         }
     }
 
     private IRubyObject cmp(IRubyObject r, char op) {
         int e = 0;
         RubyBigDecimal rb = getVpValue(r,false);
         if(rb == null) {
             e = RubyNumeric.fix2int(callCoerced("<=>",rb));
         } else {
             e = value.compareTo(rb.value);
         }
         switch(op) {
         case '*': return getRuntime().newFixnum(e);
         case '=': return (e==0)?getRuntime().getTrue():getRuntime().getFalse();
         case '!': return (e!=0)?getRuntime().getTrue():getRuntime().getFalse();
         case 'G': return (e>=0)?getRuntime().getTrue():getRuntime().getFalse();
         case '>': return (e> 0)?getRuntime().getTrue():getRuntime().getFalse();
         case 'L': return (e<=0)?getRuntime().getTrue():getRuntime().getFalse();
         case '<': return (e< 0)?getRuntime().getTrue():getRuntime().getFalse();
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject spaceship(IRubyObject arg) {
         return cmp(arg,'*');
     }
 
     public IRubyObject eql_p(IRubyObject arg) {
         return cmp(arg,'=');
     }
 
     public IRubyObject ne(IRubyObject arg) {
         return cmp(arg,'!');
     }
 
     public IRubyObject lt(IRubyObject arg) {
         return cmp(arg,'<');
     }
 
     public IRubyObject le(IRubyObject arg) {
         return cmp(arg,'L');
     }
 
     public IRubyObject gt(IRubyObject arg) {
         return cmp(arg,'>');
     }
 
     public IRubyObject ge(IRubyObject arg) {
         return cmp(arg,'G');
     }
 
-    public RubyNumeric abs() {
+    public IRubyObject abs() {
         return new RubyBigDecimal(getRuntime(),value.abs()).setResult();
     }
 
     public IRubyObject ceil(IRubyObject arg) {
         System.err.println("unimplemented: ceil");
         // TODO: implement correctly
         return this;
     }
 
     public IRubyObject coerce(IRubyObject other) {
         IRubyObject obj;
         if(other instanceof RubyFloat) {
             obj = getRuntime().newArray(other,to_f());
         } else {
             obj = getRuntime().newArray(getVpValue(other,true),this);
         }
         return obj;
     }
 
     public double getDoubleValue() { return value.doubleValue(); }
     public long getLongValue() { return value.longValue(); }
 
     public RubyNumeric multiplyWith(RubyInteger value) { 
         return (RubyNumeric)mult(new IRubyObject[]{value});
     }
 
     public RubyNumeric multiplyWith(RubyFloat value) { 
         return (RubyNumeric)mult(new IRubyObject[]{value});
     }
 
     public RubyNumeric multiplyWith(RubyBignum value) { 
         return (RubyNumeric)mult(new IRubyObject[]{value});
     }
 
     public IRubyObject divmod(IRubyObject arg) {
         System.err.println("unimplemented: divmod");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject exponent() {
         return getRuntime().newFixnum(exp());
     }
 
     private int exp() {
         return value.abs().unscaledValue().toString().length() - value.abs().scale();
     }
 
     public IRubyObject finite_p() {
         System.err.println("unimplemented: finite?");
         // TODO: implement correctly
         return getRuntime().getTrue();
     }
 
     public IRubyObject fix() {
         System.err.println("unimplemented: fix");
         // TODO: implement correctly
         return this;
     }
 
     public IRubyObject floor(IRubyObject arg) {
         System.err.println("unimplemented: floor");
         // TODO: implement correctly
         return this;
     }
  
     public IRubyObject frac() {
         System.err.println("unimplemented: frac");
         // TODO: implement correctly
         return this;
     }
 
     public IRubyObject infinite_p() {
         System.err.println("unimplemented: infinite?");
         // TODO: implement correctly
         return getRuntime().getFalse();
     }
 
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer("#<BigDecimal:").append(Integer.toHexString(System.identityHashCode(this))).append(",");
         val.append("'").append(this.callMethod(getRuntime().getCurrentContext(), "to_s")).append("'").append(",");
         int len = value.abs().unscaledValue().toString().length();
         int pow = len/4;
         val.append(len).append("(").append((pow+1)*4).append(")").append(">");
         return getRuntime().newString(val.toString());
     }
 
     public IRubyObject nan_p() {
         System.err.println("unimplemented: nan?");
         // TODO: implement correctly
         return getRuntime().getFalse();
     }
 
     public IRubyObject nonzero_p() {
         return value.signum() != 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
  
     public IRubyObject precs() {
         System.err.println("unimplemented: precs");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject remainder(IRubyObject arg) {
         System.err.println("unimplemented: remainder");
         // TODO: implement
         return this;
     }
 
     public IRubyObject round(IRubyObject[] args) {
         System.err.println("unimplemented: round");
         // TODO: implement
         return this;
     }
 
     public IRubyObject sign() {
         System.err.println("unimplemented: sign");
         // TODO: implement correctly
         return getRuntime().newFixnum(value.signum());
     }
 
     public IRubyObject split() {
         System.err.println("unimplemented: split");
         // TODO: implement
         return getRuntime().getNil();
     }
 
     public IRubyObject sqrt(IRubyObject[] args) {
         System.err.println("unimplemented: sqrt");
         // TODO: implement correctly
         return new RubyBigDecimal(getRuntime(),new BigDecimal(Math.sqrt(value.doubleValue()))).setResult();
     }
 
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(),value.doubleValue());
     }
 
     public IRubyObject to_i() {
         return RubyNumeric.int2fix(getRuntime(),value.longValue());
     }
 
     public IRubyObject to_int() {
         // TODO: implement to handle infinity and stuff
         return RubyNumeric.int2fix(getRuntime(),value.longValue());
     }
 
     private String removeTrailingZeroes(String in) {
         while(in.length() > 0 && in.charAt(in.length()-1)=='0') {
             in = in.substring(0,in.length()-1);
         }
         return in;
     }
 
     public IRubyObject to_s(IRubyObject[] args) {
         boolean engineering = true;
         boolean pos_sign = false;
         boolean pos_space = false;
         int groups = 0;
 
         if(args.length != 0 && !args[0].isNil()) {
             String format = args[0].toString();
             int start = 0;
             int end = format.length();
             if(format.length() > 0 && format.charAt(0) == '+') {
                 pos_sign = true;
                 start++;
             } else if(format.length() > 0 && format.charAt(0) == ' ') {
                 pos_sign = true;
                 pos_space = true;
                 start++;
             }
             if(format.length() > 0 && format.charAt(format.length()-1) == 'F') {
                 engineering = false;
                 end--;
             } else if(format.length() > 0 && format.charAt(format.length()-1) == 'E') {
                 engineering = true;
                 end--;
             }
             String nums = format.substring(start,end);
             if(nums.length()>0) {
                 groups = Integer.parseInt(nums);
             }
         }
 
         String out = null;
         if(engineering) {
             BigDecimal abs = value.abs();
             String unscaled = abs.unscaledValue().toString();
             int exponent = exp();
             int signum = value.signum();
             StringBuffer build = new StringBuffer();
             build.append(signum == -1 ? "-" : (signum == 1 ? (pos_sign ? (pos_space ? " " : "+" ) : "") : ""));
             build.append("0.");
             if(0 == groups) {
                 String s = removeTrailingZeroes(unscaled);
                 if("".equals(s)) {
                     build.append("0");
                 } else {
                     build.append(s);
                 }
             } else {
                 int index = 0;
                 String sep = "";
                 while(index < unscaled.length()) {
                     int next = index+groups;
                     if(next > unscaled.length()) {
                         next = unscaled.length();
                     }
                     build.append(sep).append(unscaled.substring(index,next));
                     sep = " ";
                     index += groups;
                 }
             }
             build.append("E").append(exponent);
             out = build.toString();
         } else {
             BigDecimal abs = value.abs();
             String unscaled = abs.unscaledValue().toString();
             int ix = abs.toString().indexOf('.');
             String whole = unscaled;
             String after = null;
             if(ix != -1) {
                 whole = unscaled.substring(0,ix);
                 after = unscaled.substring(ix);
             }
             int signum = value.signum();
             StringBuffer build = new StringBuffer();
             build.append(signum == -1 ? "-" : (signum == 1 ? (pos_sign ? (pos_space ? " " : "+" ) : "") : ""));
             if(0 == groups) {
                 build.append(whole);
                 if(null != after) {
                     build.append(".").append(after);
                 }
             } else {
                 int index = 0;
                 String sep = "";
                 while(index < whole.length()) {
                     int next = index+groups;
                     if(next > whole.length()) {
                         next = whole.length();
                     }
                     build.append(sep).append(whole.substring(index,next));
                     sep = " ";
                     index += groups;
                 }
                 if(null != after) {
                     build.append(".");
                     index = 0;
                     sep = "";
                     while(index < after.length()) {
                         int next = index+groups;
                         if(next > after.length()) {
                             next = after.length();
                         }
                         build.append(sep).append(after.substring(index,next));
                         sep = " ";
                         index += groups;
                     }
                 }
             }
             out = build.toString();
         }
 
         return getRuntime().newString(out);
     }
 
     public IRubyObject truncate(IRubyObject[] args) {
         System.err.println("unimplemented: truncate");
         // TODO: implement
         return this;
     }
 
-    public RubyBoolean zero_p() {
+    public IRubyObject zero_p() {
         return value.signum() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 }// RubyBigdecimal
diff --git a/src/org/jruby/RubyBignum.java b/src/org/jruby/RubyBignum.java
index e8a60a7445..c523678b48 100644
--- a/src/org/jruby/RubyBignum.java
+++ b/src/org/jruby/RubyBignum.java
@@ -1,396 +1,624 @@
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
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
+import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyBignum extends RubyInteger {
+    public static RubyClass createBignumClass(IRuby runtime) {
+        RubyClass bignum = runtime.defineClass("Bignum", runtime.getClass("Integer"),
+                ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyBignum.class);
+
+        bignum.defineFastMethod("to_s", callbackFactory.getOptMethod("to_s"));
+        bignum.defineFastMethod("coerce", callbackFactory.getMethod("coerce", IRubyObject.class));
+        bignum.defineFastMethod("-@", callbackFactory.getMethod("uminus"));
+        bignum.defineFastMethod("+", callbackFactory.getMethod("plus", IRubyObject.class));
+        bignum.defineFastMethod("-", callbackFactory.getMethod("minus", IRubyObject.class));
+        bignum.defineFastMethod("*", callbackFactory.getMethod("mul", IRubyObject.class));
+        bignum.defineFastMethod("/", callbackFactory.getMethod("div", IRubyObject.class));
+        bignum.defineFastMethod("%", callbackFactory.getMethod("mod", IRubyObject.class));
+        bignum.defineFastMethod("div", callbackFactory.getMethod("div", IRubyObject.class));
+        bignum.defineFastMethod("divmod", callbackFactory.getMethod("divmod", IRubyObject.class));
+        bignum.defineFastMethod("modulo", callbackFactory.getMethod("mod", IRubyObject.class));
+        bignum.defineFastMethod("remainder", callbackFactory.getMethod("remainder", IRubyObject.class));
+        bignum.defineFastMethod("quo", callbackFactory.getMethod("quo", IRubyObject.class));
+        bignum.defineFastMethod("**", callbackFactory.getMethod("pow", IRubyObject.class));
+        bignum.defineFastMethod("&", callbackFactory.getMethod("and", IRubyObject.class));
+        bignum.defineFastMethod("|", callbackFactory.getMethod("or", IRubyObject.class));
+        bignum.defineFastMethod("^", callbackFactory.getMethod("xor", IRubyObject.class));
+        bignum.defineFastMethod("~", callbackFactory.getMethod("neg"));
+        bignum.defineFastMethod("<<", callbackFactory.getMethod("lshift", IRubyObject.class));
+        bignum.defineFastMethod(">>", callbackFactory.getMethod("rshift", IRubyObject.class));
+        bignum.defineFastMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
+
+        bignum.defineFastMethod("<=>", callbackFactory.getMethod("cmp", IRubyObject.class));
+        bignum.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
+        bignum.defineFastMethod("eql?", callbackFactory.getMethod("eql_p", IRubyObject.class));
+        bignum.defineFastMethod("hash", callbackFactory.getMethod("hash"));
+        bignum.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
+        bignum.defineFastMethod("abs", callbackFactory.getMethod("abs"));
+        bignum.defineFastMethod("size", callbackFactory.getMethod("size"));
+
+        return bignum;
+    }
+
     private static final int BIT_SIZE = 64;
-    private static final long MAX = (1L<<(BIT_SIZE - 2)) - 1;
+    private static final long MAX = (1L << (BIT_SIZE - 1)) - 1;
     private static final BigInteger LONG_MAX = BigInteger.valueOf(MAX);
-    private static final BigInteger LONG_MIN = BigInteger.valueOf(-MAX-1);
+    private static final BigInteger LONG_MIN = BigInteger.valueOf(-MAX - 1);
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
 
     private final BigInteger value;
 
     public RubyBignum(IRuby runtime, BigInteger value) {
         super(runtime, runtime.getClass("Bignum"));
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         switch (switchvalue) {
             case OP_PLUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_plus(args[0]);
+                return plus(args[0]);
             case OP_MINUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_minus(args[0]);
-            case OP_LT_SWITCHVALUE:
-                Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_lt(args[0]);
+                return minus(args[0]);
             case 0:
             default:
                 return super.callMethod(context, rubyclass, name, args, callType);
         }
     }
 
-    public double getDoubleValue() {
-        return value.doubleValue();
+    public static RubyBignum newBignum(IRuby runtime, long value) {
+        return newBignum(runtime, BigInteger.valueOf(value));
     }
 
-    public long getLongValue() {
-        long result = getTruncatedLongValue();
-        if (! BigInteger.valueOf(result).equals(value)) {
-            throw getRuntime().newRangeError("bignum too big to convert into 'int'");
+    public static RubyBignum newBignum(IRuby runtime, double value) {
+        return newBignum(runtime, new BigDecimal(value).toBigInteger());
         }
-        return result;
+
+    public static RubyBignum newBignum(IRuby runtime, BigInteger value) {
+        return new RubyBignum(runtime, value);
+    }
+
+    public double getDoubleValue() {
+        return big2dbl(this);
     }
 
-    public long getTruncatedLongValue() {
-        return value.longValue();
+    public long getLongValue() {
+        return big2long(this);
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public BigInteger getValue() {
         return value;
     }
 
+    /*  ================
+     *  Utility Methods
+     *  ================ 
+     */
+
     /* If the value will fit in a Fixnum, return one of those. */
-    private static RubyInteger bigNorm(IRuby runtime, BigInteger bi) {
+    /** rb_big_norm
+     * 
+     */
+    public static RubyInteger bignorm(IRuby runtime, BigInteger bi) {
         if (bi.compareTo(LONG_MIN) < 0 || bi.compareTo(LONG_MAX) > 0) {
             return newBignum(runtime, bi);
         }
         return runtime.newFixnum(bi.longValue());
     }
 
-    public static BigInteger bigIntValue(RubyNumeric other) {
-        assert !(other instanceof RubyFloat) : "argument must be an integer";
+    /** rb_big2long
+     * 
+     */
+    public static long big2long(RubyBignum value) {
+        BigInteger big = value.getValue();
 
-        return (other instanceof RubyBignum) ? ((RubyBignum) other).getValue() : BigInteger.valueOf(other.getLongValue());
+        if (big.compareTo(LONG_MIN) < 0 || big.compareTo(LONG_MAX) > 0) {
+            throw value.getRuntime().newRangeError("bignum too big to convert into `long'");
     }
-
-    protected int compareValue(RubyNumeric other) {
-        if (other instanceof RubyFloat) {
-            double otherVal = other.getDoubleValue();
-            double thisVal = getDoubleValue();
-            return thisVal > otherVal ? 1 : thisVal < otherVal ? -1 : 0;
+        return big.longValue();
         }
 
-        return getValue().compareTo(bigIntValue(other));
+    /** rb_big2dbl
+     * 
+     */
+    public static double big2dbl(RubyBignum value) {
+        BigInteger big = value.getValue();
+        double dbl = big.doubleValue();
+        if (dbl == Double.NEGATIVE_INFINITY || dbl == Double.POSITIVE_INFINITY) {
+            value.getRuntime().getWarnings().warn("Bignum out of Float range");
+    }
+        return dbl;
     }
 
-    public RubyFixnum hash() {
-        return getRuntime().newFixnum(value.hashCode());
+    /** rb_int2big
+     * 
+     */
+    public static BigInteger fix2big(RubyFixnum arg) {
+        return BigInteger.valueOf(arg.getLongValue());
     }
 
-    // Bignum methods
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
 
-    public static RubyBignum newBignum(IRuby runtime, long value) {
-        return newBignum(runtime, BigInteger.valueOf(value));
+    /** rb_big_to_s
+     * 
+     */
+    public IRubyObject to_s(IRubyObject[] args) {
+        checkArgumentCount(args, 0, 1);
+
+        int base = args.length == 0 ? 10 : num2int(args[0]);
+        if (base < 2 || base > 36) {
+            throw getRuntime().newArgumentError("illegal radix " + base);
+    }
+        return getRuntime().newString(getValue().toString(base));
     }
 
-    public static RubyBignum newBignum(IRuby runtime, double value) {
-        return newBignum(runtime, new BigDecimal(value).toBigInteger());
+    /** rb_big_coerce
+     * 
+     */
+    public IRubyObject coerce(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return getRuntime().newArray(newBignum(getRuntime(), ((RubyFixnum) other).getLongValue()), this);
+        } else if (other instanceof RubyBignum) {
+            return getRuntime().newArray(newBignum(getRuntime(), ((RubyBignum) other).getValue()), this);
     }
 
-    public static RubyBignum newBignum(IRuby runtime, BigInteger value) {
-        return new RubyBignum(runtime, value);
+        throw getRuntime().newTypeError("Can't coerce " + other.getMetaClass().getName() + " to Bignum");
     }
 
-    public static RubyBignum newBignum(IRuby runtime, String value) {
-        return new RubyBignum(runtime, new BigInteger(value));
+    /** rb_big_uminus
+     * 
+     */
+    public IRubyObject uminus() {
+        return bignorm(getRuntime(), value.negate());
     }
 
-    public IRubyObject remainder(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).remainder(other);
-        } else if (other instanceof RubyNumeric) {
-            return bigNorm(getRuntime(), getValue().remainder(bigIntValue((RubyNumeric) other)));
+    /** rb_big_plus
+     * 
+     */
+    public IRubyObject plus(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return bignorm(getRuntime(), value.add(fix2big(((RubyFixnum) other))));
         }
-        
-        return callCoerced("remainder", other);
+        if (other instanceof RubyBignum) {
+            return bignorm(getRuntime(), value.add(((RubyBignum) other).value));
+        } else if (other instanceof RubyFloat) {
+            return RubyFloat.newFloat(getRuntime(), big2dbl(this) + ((RubyFloat) other).getDoubleValue());
     }
-
-    public RubyNumeric multiplyWith(RubyInteger other) {
-        return bigNorm(getRuntime(), getValue().multiply(bigIntValue(other)));
+        return coerceBin("+", other);
     }
 
-    public RubyNumeric multiplyWith(RubyFloat other) {
-        return other.multiplyWith(RubyFloat.newFloat(getRuntime(), getDoubleValue()));
+    /** rb_big_minus
+     * 
+     */
+    public IRubyObject minus(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return bignorm(getRuntime(), value.subtract(fix2big(((RubyFixnum) other))));
     }
-    
-    public RubyNumeric quo(IRubyObject other) {
-        return RubyFloat.newFloat(getRuntime(), ((RubyNumeric) op_div(other)).getDoubleValue());
+        if (other instanceof RubyBignum) {
+            return bignorm(getRuntime(), value.subtract(((RubyBignum) other).value));
+        } else if (other instanceof RubyFloat) {
+            return RubyFloat.newFloat(getRuntime(), big2dbl(this) - ((RubyFloat) other).getDoubleValue());
+    }
+        return coerceBin("-", other);
     }
 
-    public IRubyObject op_and(IRubyObject other) {
+    /** rb_big_mul
+     * 
+     */
+    public IRubyObject mul(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return bignorm(getRuntime(), value.multiply(fix2big(((RubyFixnum) other))));
+        }
         if (other instanceof RubyBignum) {
-            return bigNorm(getRuntime(), value.and(((RubyBignum) other).value));
-        } else if (other instanceof RubyNumeric) {
-	        return bigNorm(getRuntime(), 
-	            getValue().and(newBignum(getRuntime(), ((RubyNumeric) other).getLongValue()).getValue()));
+            return bignorm(getRuntime(), value.multiply(((RubyBignum) other).value));
+        } else if (other instanceof RubyFloat) {
+            return RubyFloat.newFloat(getRuntime(), big2dbl(this) * ((RubyFloat) other).getDoubleValue());
         }
-        
-        return callCoerced("&", other);
-    }
-
-    public IRubyObject op_div(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_div(other);
-        } else if (other instanceof RubyNumeric) {
-        	BigInteger otherBig = bigIntValue((RubyNumeric) other);
-        	
-        	if (otherBig.equals(BigInteger.ZERO)) {
-        		throw getRuntime().newZeroDivisionError();
-        	}
-        	
-            BigInteger[] results = getValue().divideAndRemainder(otherBig);
-
-            if (results[0].compareTo(BigInteger.ZERO) <= 0 &&
-                results[1].compareTo(BigInteger.ZERO) != 0) {
-                return bigNorm(getRuntime(), results[0].subtract(BigInteger.ONE));
-            }
+        return coerceBin("*", other);
+    }
 
-            return bigNorm(getRuntime(), results[0]);
+    /** rb_big_div
+     * 
+     */
+    public IRubyObject div(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big((RubyFixnum) other);
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else if (other instanceof RubyFloat) {
+            return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyFloat) other).getDoubleValue());
+        } else {
+            return coerceBin("/", other);
         }
-        
-        return callCoerced("/", other);
-    }
 
-    public IRubyObject op_invert() {
-        return bigNorm(getRuntime(), getValue().not());
+        if (otherValue.equals(BigInteger.ZERO)) {
+            throw getRuntime().newZeroDivisionError();
+        }
+
+        BigInteger[] results = value.divideAndRemainder(otherValue);
+
+        if (results[0].signum() == -1 && results[1].signum() != 0) {
+            return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
+        }
+        return bignorm(getRuntime(), results[0]);
     }
 
-    public IRubyObject op_lshift(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            long shift = ((RubyNumeric ) other).getLongValue();
-            if (shift > Integer.MAX_VALUE || shift < Integer.MIN_VALUE) {
-			    throw getRuntime().newRangeError("bignum too big to convert into `int'");
-		    }
-            return new RubyBignum(getRuntime(), value.shiftLeft((int) shift));
+    /** rb_big_divmod
+     * 
+     */
+    public IRubyObject divmod(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big((RubyFixnum) other);
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else {
+            return coerceBin("divmod", other);
+        }
+
+        if (otherValue.equals(BigInteger.ZERO)) {
+            throw getRuntime().newZeroDivisionError();
+        }
+
+        BigInteger[] results = value.divideAndRemainder(otherValue);
+
+        if (results[0].signum() == -1 && results[1].signum() != 0) {
+            return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
     	}
-    	
-    	return callCoerced("<<", other);
+        final IRuby runtime = getRuntime();
+        return RubyArray.newArray(getRuntime(), bignorm(runtime, results[0]), bignorm(runtime, results[1]));
     }
 
-    public IRubyObject op_minus(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_minus(other);
-        } else if (other instanceof RubyNumeric) {
-            return bigNorm(getRuntime(), getValue().subtract(bigIntValue((RubyNumeric) other)));
+    /** rb_big_modulo
+     * 
+     */
+    public IRubyObject mod(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big((RubyFixnum) other);
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else {
+            return coerceBin("%", other);
         }
-        
-        return callCoerced("-", other);
-    }
+        if (otherValue.equals(BigInteger.ZERO)) {
+            throw getRuntime().newZeroDivisionError();
+        }
+        BigInteger result = value.mod(otherValue.abs());
+        if (otherValue.signum() == -1) {
+            result = otherValue.add(result);
+        }
+        return bignorm(getRuntime(), result);
 
-    public IRubyObject op_mod(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).modulo(other);
-        } else if (other instanceof RubyNumeric) {
-            BigInteger m = bigIntValue((RubyNumeric ) other);
-            BigInteger result = getValue().mod(m.abs());
-            if (m.compareTo(BigInteger.ZERO) < 0) {
-                result = m.add(result);
             }
 
-            return bigNorm(getRuntime(), result);
+    /** rb_big_remainder
+     * 
+     */
+    public IRubyObject remainder(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big(((RubyFixnum) other));
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else {
+            return coerceBin("remainder", other);
         }
-        
-        return callCoerced("%", other);
+        if (otherValue.equals(BigInteger.ZERO)) {
+            throw getRuntime().newZeroDivisionError();
+    }
+        return bignorm(getRuntime(), value.remainder(otherValue));
     }
 
-    public IRubyObject op_mul(IRubyObject other) {
+    /** rb_big_quo
+
+     * 
+     */
+    public IRubyObject quo(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
-            return ((RubyNumeric ) other).multiplyWith(this);
+            return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyNumeric) other).getDoubleValue());
+        } else {
+            return coerceBin("quo", other);
     	}
-    	
-    	return callCoerced("*", other);
     }
 
-    public IRubyObject op_or(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return new RubyBignum(getRuntime(), value.or(bigIntValue((RubyNumeric ) other)));
+    /** rb_big_pow
+     * 
+     */
+    public IRubyObject pow(IRubyObject other) {
+        double d;
+        if (other instanceof RubyFixnum) {
+            RubyFixnum fix = (RubyFixnum) other;
+            // MRI issuses warning here on (RBIGNUM(x)->len * SIZEOF_BDIGITS * yy > 1024*1024)
+            if (((value.bitLength() + 7) / 8) * 4 * fix.getLongValue() > 1024 * 1024) {
+                getRuntime().getWarnings().warn("in a**b, b may be too big");
     	}
-    	
-    	return callCoerced("|", other);
+            return bignorm(getRuntime(), value.pow((int) fix.getLongValue())); // num2int is also implemented
+        } else if (other instanceof RubyBignum) {
+            getRuntime().getWarnings().warn("in a**b, b may be too big");
+            d = ((RubyBignum) other).getDoubleValue();
+        } else if (other instanceof RubyFloat) {
+            d = ((RubyFloat) other).getDoubleValue();
+        } else {
+            return coerceBin("**", other);
+
+    }
+        return RubyFloat.newFloat(getRuntime(), Math.pow(big2dbl(this), d));
     }
 
-    public IRubyObject op_plus(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return ((RubyFloat)other).op_plus(this);
-        } else if (other instanceof RubyNumeric) {
-            return bigNorm(getRuntime(), getValue().add(bigIntValue((RubyNumeric ) other)));
+    /** rb_big_and
+     * 
+     */
+    public IRubyObject and(IRubyObject other) {
+        other = other.convertToInteger();
+        if (other instanceof RubyBignum) {
+            return bignorm(getRuntime(), value.and(((RubyBignum) other).value));
+        } else if(other instanceof RubyFixnum) {
+            return bignorm(getRuntime(), value.and(fix2big((RubyFixnum)other)));
         }
-        
-        return callCoerced("+", other);
+        return coerceBin("&", other);
     }
 
-    public IRubyObject op_pow(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_pow(other);
-        } else if (other instanceof RubyNumeric) {
-            return bigNorm(getRuntime(), getValue().pow((int) ((RubyNumeric) other).getLongValue()));
+    /** rb_big_or
+     * 
+     */
+    public IRubyObject or(IRubyObject other) {
+        other = other.convertToInteger();
+        if (other instanceof RubyBignum) {
+            return bignorm(getRuntime(), value.or(((RubyBignum) other).value));
         }
-        
-        return callCoerced("**", other);
+        if (other instanceof RubyFixnum) { // no bignorm here needed
+            return bignorm(getRuntime(), value.or(fix2big((RubyFixnum)other)));
+        }
+        return coerceBin("|", other);
     }
 
-    public IRubyObject op_rshift(IRubyObject other) {
-        long shift = ((RubyNumeric ) other).getLongValue();
-        if (shift > Integer.MAX_VALUE || shift < Integer.MIN_VALUE) {
-			throw getRuntime().newRangeError("bignum too big to convert into `int'");
-		} else if (other instanceof RubyNumeric) {
-            return new RubyBignum(getRuntime(), value.shiftRight((int) shift));
+    /** rb_big_xor
+     * 
+     */
+    public IRubyObject xor(IRubyObject other) {
+        other = other.convertToInteger();
+        if (other instanceof RubyBignum) {
+            return bignorm(getRuntime(), value.xor(((RubyBignum) other).value));
 		}
-        
-        return callCoerced(">>", other);
+        if (other instanceof RubyFixnum) {
+            return bignorm(getRuntime(), value.xor(BigInteger.valueOf(((RubyFixnum) other).getLongValue())));
     }
 
-    public IRubyObject op_uminus() {
-        return bigNorm(getRuntime(), getValue().negate());
+        return coerceBin("^", other);
     }
 
-    public IRubyObject op_xor(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return bigNorm(getRuntime(), value.xor(bigIntValue((RubyNumeric) other)));
+    /** rb_big_neg     
+     * 
+     */
+    public IRubyObject neg() {
+        return RubyBignum.newBignum(getRuntime(), value.not());
     	}
+
+    /** rb_big_lshift     
+     * 
+     */
+    public IRubyObject lshift(IRubyObject other) {
+        int width = num2int(other);
+        if (width < 0) {
+            return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
+        }
     	
-    	return callCoerced("^", other);
+        return bignorm(getRuntime(), value.shiftLeft(width));
+    }
+
+    /** rb_big_rshift     
+     * 
+     */
+    public IRubyObject rshift(IRubyObject other) {
+        int width = num2int(other);
+
+        if (width < 0) {
+            return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
+        }
+        return bignorm(getRuntime(), value.shiftRight(width));
     }
 
+    /** rb_big_aref     
+     * 
+     */
     public RubyFixnum aref(IRubyObject other) {
-        long pos = other.convertToInteger().getLongValue();
-        boolean isSet = getValue().testBit((int) pos);
-        return getRuntime().newFixnum(isSet ? 1 : 0);
+        if (other instanceof RubyBignum) {
+            if (((RubyBignum) other).value.signum() >= 0 || value.signum() == -1) {
+                return RubyFixnum.zero(getRuntime());
+            }
+            return RubyFixnum.one(getRuntime());
+        }
+        int position = num2int(other);
+        if (position < 0) {
+            return RubyFixnum.zero(getRuntime());
+        }
+        
+        return value.testBit(num2int(other)) ? RubyFixnum.one(getRuntime()) : RubyFixnum.zero(getRuntime());
     }
 
-    public IRubyObject to_s(IRubyObject[] args) {
-    	checkArgumentCount(args, 0, 1);
+    /** rb_big_cmp     
+     * 
+     */
+    public IRubyObject cmp(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big((RubyFixnum) other);
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else if (other instanceof RubyFloat) {
+            return dbl_cmp(getRuntime(), big2dbl(this), ((RubyFloat) other).getDoubleValue());
+        } else {
+            return coerceCmp("<=>", other);
+        }
 
-    	int radix = args.length == 0 ? 10 : (int) args[0].convertToInteger().getLongValue();
+        // wow, the only time we can use the java protocol ;)        
+        return RubyFixnum.newFixnum(getRuntime(), value.compareTo(otherValue));
+    }
 
-        return getRuntime().newString(getValue().toString(radix));
+    /** rb_big_eq     
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        final BigInteger otherValue;
+        if (other instanceof RubyFixnum) {
+            otherValue = fix2big((RubyFixnum) other);
+        } else if (other instanceof RubyBignum) {
+            otherValue = ((RubyBignum) other).value;
+        } else if (other instanceof RubyFloat) {
+            double a = ((RubyFloat) other).getDoubleValue();
+            if (Double.isNaN(a)) {
+                return getRuntime().getFalse();
+            }
+            return RubyBoolean.newBoolean(getRuntime(), a == big2dbl(this));
+        } else {
+            return super.equal(other);
+        }
+        return RubyBoolean.newBoolean(getRuntime(), value.compareTo(otherValue) == 0);
     }
 
-    public RubyFloat to_f() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue());
+    /** rb_big_eql     
+     * 
+     */
+    public IRubyObject eql_p(IRubyObject other) {
+        if (other instanceof RubyBignum) {
+            return RubyBoolean.newBoolean(getRuntime(), value.compareTo(((RubyBignum)other).value) == 0);
+        }
+        return getRuntime().getFalse();
     }
 
-    public RubyNumeric[] getCoerce(RubyNumeric other) {
-        if (!(other instanceof RubyInteger)) {
-            return new RubyNumeric[] { other, this };
+    /** rb_big_hash
+     * 
+     */
+    public RubyFixnum hash() {
+        return getRuntime().newFixnum(value.hashCode());
         }
-        return new RubyNumeric[] { RubyFloat.newFloat(getRuntime(), other.getDoubleValue()), RubyFloat.newFloat(getRuntime(), getDoubleValue())};
+
+    /** rb_big_to_f
+     * 
+     */
+    public IRubyObject to_f() {
+        return RubyFloat.newFloat(getRuntime(), getDoubleValue());
+    }
+
+    /** rb_big_abs
+     * 
+     */
+    public IRubyObject abs() {
+        return RubyBignum.newBignum(getRuntime(), value.abs());
     }
 
+    /** rb_big_size
+     * 
+     */
     public RubyFixnum size() {
-        int byteLength = value.bitLength() / 8;
-        if (value.bitLength() % 8 != 0) {
-            byteLength++;
-        }
-        return getRuntime().newFixnum(byteLength);
+        return getRuntime().newFixnum((value.bitLength() + 7) / 8);
     }
 
     public void marshalTo(MarshalStream output) throws IOException {
         output.write('l');
         output.write(value.signum() >= 0 ? '+' : '-');
 
         BigInteger absValue = value.abs();
 
         byte[] digits = absValue.toByteArray();
 
         boolean oddLengthNonzeroStart = (digits.length % 2 != 0 && digits[0] != 0);
         int shortLength = digits.length / 2;
         if (oddLengthNonzeroStart) {
 			shortLength++;
 		}
         output.dumpInt(shortLength);
-        
+
         for (int i = 1; i <= shortLength * 2 && i <= digits.length; i++) {
         	output.write(digits[digits.length - i]);
         }
-        
+
         if (oddLengthNonzeroStart) {
             // Pad with a 0
             output.write(0);
         }
     }
+
     public static RubyBignum unmarshalFrom(UnmarshalStream input) throws IOException {
         boolean positive = input.readUnsignedByte() == '+';
         int shortLength = input.unmarshalInt();
 
         // BigInteger required a sign byte in incoming array
         byte[] digits = new byte[shortLength * 2 + 1];
 
         for (int i = digits.length - 1; i >= 1; i--) {
         	digits[i] = input.readSignedByte();
         }
 
         BigInteger value = new BigInteger(digits);
         if (!positive) {
             value = value.negate();
         }
 
         RubyBignum result = newBignum(input.getRuntime(), value);
         input.registerLinkTarget(result);
         return result;
     }
-
-    public IRubyObject coerce(IRubyObject other) {
-        if (other instanceof RubyFixnum) {
-            return getRuntime().newArray(newBignum(getRuntime(), ((RubyFixnum)other).getLongValue()), this);
-        }
-        throw getRuntime().newTypeError("Can't coerce " + other.getMetaClass().getName() + " to Bignum");
-    }
 }
diff --git a/src/org/jruby/RubyComparable.java b/src/org/jruby/RubyComparable.java
index 8415fbcc14..2b93351ce6 100644
--- a/src/org/jruby/RubyComparable.java
+++ b/src/org/jruby/RubyComparable.java
@@ -1,132 +1,199 @@
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** Implementation of the Comparable module.
  *
  */
 public class RubyComparable {
+
     public static RubyModule createComparable(IRuby runtime) {
         RubyModule comparableModule = runtime.defineModule("Comparable");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyComparable.class);
         comparableModule.defineFastMethod("==", callbackFactory.getSingletonMethod("equal", IRubyObject.class));
         comparableModule.defineFastMethod(">", callbackFactory.getSingletonMethod("op_gt", IRubyObject.class));
         comparableModule.defineFastMethod(">=", callbackFactory.getSingletonMethod("op_ge", IRubyObject.class));
         comparableModule.defineFastMethod("<", callbackFactory.getSingletonMethod("op_lt", IRubyObject.class));
         comparableModule.defineFastMethod("<=", callbackFactory.getSingletonMethod("op_le", IRubyObject.class));
-        comparableModule.defineFastMethod("between?", callbackFactory.getSingletonMethod("between_p", IRubyObject.class, IRubyObject.class));
+        comparableModule.defineFastMethod("between?", callbackFactory.getSingletonMethod("between_p",
+                IRubyObject.class, IRubyObject.class));
 
         return comparableModule;
     }
 
+    /*  ================
+     *  Utility Methods
+     *  ================ 
+     */
+
+    /** rb_cmpint
+     * 
+     */
+    public static long cmpint(IRubyObject val, IRubyObject a, IRubyObject b) {
+        if (val.isNil()) {
+            cmperr(a, b);
+        }
+        if (val instanceof RubyFixnum) {
+            return ((RubyFixnum) val).getLongValue();
+        }
+        if (val instanceof RubyBignum) {
+            if (((RubyBignum) val).getValue().signum() == -1) {
+                return 1;
+            }
+            return -1;
+        }
+
+        final IRuby runtime = val.getRuntime();
+        final ThreadContext tc = runtime.getCurrentContext();
+        final RubyFixnum zero = RubyFixnum.one(runtime);
+        if (val.callMethod(tc, ">", zero).isTrue()) {
+            return 1;
+        }
+        if (val.callMethod(tc, "<", zero).isTrue()) {
+            return -1;
+        }
+        return 0;
+    }
+
+    /** rb_cmperr
+     * 
+     */
+    public static void cmperr(IRubyObject recv, IRubyObject other) {
+        IRubyObject target;
+        if (other.isImmediate() || !(other.isNil() || other.isTrue() || other == recv.getRuntime().getFalse())) {
+            target = other.inspect();
+        } else {
+            target = other.getType();
+        }
+
+        throw recv.getRuntime().newArgumentError("comparison of " + recv.getType() + " with " + target + " failed");
+    }
+
+    /*  ================
+     *  Module Methods
+     *  ================ 
+     */
+
+    /** cmp_equal (cmp_eq inlined here)
+     * 
+     */
     public static IRubyObject equal(IRubyObject recv, IRubyObject other) {
+        if (recv == other) {
+            return recv.getRuntime().getTrue();
+        }
+        IRuby runtime = recv.getRuntime();
+        IRubyObject result = null;
         try {
-            if (recv == other) {
-                return recv.getRuntime().getTrue();
-            }
-            IRubyObject result = recv.callMethod(recv.getRuntime().getCurrentContext(), "<=>", other);
-            
-            if (result.isNil()) {
-            	return result;
-            }
-            
-            return RubyNumeric.fix2int(result) != 0 ? recv.getRuntime().getFalse() : recv.getRuntime().getTrue(); 
+            result = recv.callMethod(runtime.getCurrentContext(), "<=>", other);
         } catch (RaiseException e) {
-        	RubyException raisedException = e.getException();
-        	if (raisedException.isKindOf(recv.getRuntime().getClass("NoMethodError"))) {
-        		return recv.getRuntime().getFalse();
-        	} else if (raisedException.isKindOf(recv.getRuntime().getClass("NameError"))) {
-        		return recv.getRuntime().getFalse();
-        	}
-        	throw e;
+            return recv.getRuntime().getFalse();
         }
+
+        if (result.isNil()) {
+            return result;
+        }
+
+        return RubyBoolean.newBoolean(runtime, cmpint(result, recv, other) == 0);
     }
-    
-    
-    private static void cmperr(IRubyObject recv, IRubyObject other) {
-        String message = "comparison of " + recv.getType() + " with " + other.getType() + " failed";
-        throw recv.getRuntime().newArgumentError(message);
-    }
-    
 
+    /** cmp_gt
+     * 
+     */
+    // <=> may return nil in many circumstances, e.g. 3 <=> NaN        
     public static RubyBoolean op_gt(IRubyObject recv, IRubyObject other) {
-        // <=> may return nil in many circumstances, e.g. 3 <=> NaN
-        IRubyObject tmp = recv.callMethod(recv.getRuntime().getCurrentContext(), "<=>", other);
-        
-        if (tmp.isNil()) {
+        final IRuby runtime = recv.getRuntime();
+        IRubyObject result = recv.callMethod(runtime.getCurrentContext(), "<=>", other);
+
+        if (result.isNil()) {
             cmperr(recv, other);
         }
 
-        return RubyNumeric.fix2int(tmp) > 0 ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
+        return RubyBoolean.newBoolean(runtime, cmpint(result, recv, other) > 0);
     }
 
+    /** cmp_ge
+     * 
+     */
     public static RubyBoolean op_ge(IRubyObject recv, IRubyObject other) {
-        IRubyObject tmp = recv.callMethod(recv.getRuntime().getCurrentContext(), "<=>", other);
-        
-        if (tmp.isNil()) {
+        final IRuby runtime = recv.getRuntime();
+        IRubyObject result = recv.callMethod(runtime.getCurrentContext(), "<=>", other);
+
+        if (result.isNil()) {
             cmperr(recv, other);
         }
 
-        return RubyNumeric.fix2int(tmp) >= 0 ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
+        return RubyBoolean.newBoolean(runtime, cmpint(result, recv, other) >= 0);
     }
 
+    /** cmp_lt
+     * 
+     */
     public static RubyBoolean op_lt(IRubyObject recv, IRubyObject other) {
-        IRubyObject tmp = recv.callMethod(recv.getRuntime().getCurrentContext(), "<=>", other);
+        final IRuby runtime = recv.getRuntime();
+        IRubyObject result = recv.callMethod(runtime.getCurrentContext(), "<=>", other);
 
-        if (tmp.isNil()) {
+        if (result.isNil()) {
             cmperr(recv, other);
         }
 
-        return RubyNumeric.fix2int(tmp) < 0 ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
+        return RubyBoolean.newBoolean(runtime, cmpint(result, recv, other) < 0);
     }
 
+    /** cmp_le
+     * 
+     */
     public static RubyBoolean op_le(IRubyObject recv, IRubyObject other) {
-        IRubyObject tmp = recv.callMethod(recv.getRuntime().getCurrentContext(), "<=>", other);
+        final IRuby runtime = recv.getRuntime();
+        IRubyObject result = recv.callMethod(runtime.getCurrentContext(), "<=>", other);
 
-        if (tmp.isNil()) {
+        if (result.isNil()) {
             cmperr(recv, other);
         }
 
-        return RubyNumeric.fix2int(tmp) <= 0 ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
+        return RubyBoolean.newBoolean(runtime, cmpint(result, recv, other) <= 0);
     }
 
+    /** cmp_between
+     * 
+     */
     public static RubyBoolean between_p(IRubyObject recv, IRubyObject first, IRubyObject second) {
-        return recv.getRuntime().newBoolean(op_lt(recv, first).isFalse() && 
-                op_gt(recv, second).isFalse());
+
+        return recv.getRuntime().newBoolean(op_lt(recv, first).isFalse() && op_gt(recv, second).isFalse());
     }
 }
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index 29e56f3974..8e6a9a8b57 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,469 +1,720 @@
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
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
 
+import java.math.BigInteger;
+
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
+import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Fixnum class.
  *
  * @author jpetersen
  */
 public class RubyFixnum extends RubyInteger {
+    
+    public static RubyClass createFixnumClass(IRuby runtime) {
+        RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getClass("Integer"),
+                ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyFixnum.class);
+
+        fixnum.includeModule(runtime.getModule("Precision"));
+        fixnum.defineFastSingletonMethod("induced_from", callbackFactory.getSingletonMethod(
+                "induced_from", IRubyObject.class));
+
+        fixnum.defineFastMethod("to_s", callbackFactory.getOptMethod("to_s"));
+
+        fixnum.defineFastMethod("id2name", callbackFactory.getMethod("id2name"));
+        fixnum.defineFastMethod("to_sym", callbackFactory.getMethod("to_sym"));
+
+        fixnum.defineFastMethod("-@", callbackFactory.getMethod("uminus"));
+        fixnum.defineFastMethod("+", callbackFactory.getMethod("plus", IRubyObject.class));
+        fixnum.defineFastMethod("-", callbackFactory.getMethod("minus", IRubyObject.class));
+        fixnum.defineFastMethod("*", callbackFactory.getMethod("mul", IRubyObject.class));
+        fixnum.defineFastMethod("/", callbackFactory.getMethod("div_slash", IRubyObject.class));
+        fixnum.defineFastMethod("div", callbackFactory.getMethod("div_div", IRubyObject.class));
+        fixnum.defineFastMethod("%", callbackFactory.getMethod("mod", IRubyObject.class));
+        fixnum.defineFastMethod("modulo", callbackFactory.getMethod("mod", IRubyObject.class));
+        fixnum.defineFastMethod("divmod", callbackFactory.getMethod("divmod", IRubyObject.class));
+        fixnum.defineFastMethod("quo", callbackFactory.getMethod("quo", IRubyObject.class));
+        fixnum.defineFastMethod("**", callbackFactory.getMethod("pow", IRubyObject.class));
+
+        fixnum.defineFastMethod("abs", callbackFactory.getMethod("abs"));
+
+        fixnum.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
+        fixnum.defineFastMethod("<=>", callbackFactory.getMethod("cmp", IRubyObject.class));
+
+        fixnum.defineFastMethod(">", callbackFactory.getMethod("gt", IRubyObject.class));
+        fixnum.defineFastMethod(">=", callbackFactory.getMethod("ge", IRubyObject.class));
+        fixnum.defineFastMethod("<", callbackFactory.getMethod("lt", IRubyObject.class));
+        fixnum.defineFastMethod("<=", callbackFactory.getMethod("le", IRubyObject.class));
+
+        fixnum.defineFastMethod("~", callbackFactory.getMethod("rev"));
+        fixnum.defineFastMethod("&", callbackFactory.getMethod("and", IRubyObject.class));
+        fixnum.defineFastMethod("|", callbackFactory.getMethod("or", IRubyObject.class));
+        fixnum.defineFastMethod("^", callbackFactory.getMethod("xor", IRubyObject.class));
+        fixnum.defineFastMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
+        fixnum.defineFastMethod("<<", callbackFactory.getMethod("lshift", IRubyObject.class));
+        fixnum.defineFastMethod(">>", callbackFactory.getMethod("rshift", IRubyObject.class));
+
+        fixnum.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
+        fixnum.defineFastMethod("size", callbackFactory.getMethod("size"));
+        fixnum.defineFastMethod("zero?", callbackFactory.getMethod("zero_p"));
+
+        return fixnum;
+    }    
+    
     private long value;
     private static final int BIT_SIZE = 64;
+    private static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     private static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1;
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
 
     public RubyFixnum(IRuby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(IRuby runtime, long value) {
         super(runtime, runtime.getFixnum());
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         switch (switchvalue) {
             case OP_PLUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_plus(args[0]);
+                return plus(args[0]);
             case OP_MINUS_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_minus(args[0]);
+                return minus(args[0]);
             case OP_LT_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return op_lt(args[0]);
+                return lt(args[0]);
             case 0:
             default:
                 return super.callMethod(context, rubyclass, name, args, callType);
         }
     }
     
     public boolean isImmediate() {
     	return true;
     }
 
     public Class getJavaClass() {
         return Long.TYPE;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return value;
     }
 
+    public static RubyFixnum newFixnum(IRuby runtime, long value) {
+        RubyFixnum fixnum;
+        RubyFixnum[] fixnumCache = runtime.getFixnumCache();
+
+        if (value >= 0 && value < fixnumCache.length) {
+            fixnum = fixnumCache[(int) value];
+            if (fixnum == null) {
+                fixnum = new RubyFixnum(runtime, value);
+                fixnumCache[(int) value] = fixnum;
+            }
+        } else {
+            fixnum = new RubyFixnum(runtime, value);
+        }
+        return fixnum;
+    }
+
+    public RubyFixnum newFixnum(long newValue) {
+        return newFixnum(getRuntime(), newValue);
+    }
+
     public static RubyFixnum zero(IRuby runtime) {
         return newFixnum(runtime, 0);
     }
 
     public static RubyFixnum one(IRuby runtime) {
         return newFixnum(runtime, 1);
     }
 
     public static RubyFixnum minus_one(IRuby runtime) {
         return newFixnum(runtime, -1);
     }
 
-    protected int compareValue(RubyNumeric other) {
-        if (other instanceof RubyBignum) {
-            return -other.compareValue(this);
-        } else if (other instanceof RubyFloat) {
-            final double otherVal = other.getDoubleValue();
-            return value > otherVal ? 1 : value < otherVal ? -1 : 0;
-        } 
-          
-        long otherVal = other.getLongValue();
-        
-        return value > otherVal ? 1 : value < otherVal ? -1 : 0;
-    }
-
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
-    
+
     public int hashCode() {
         return (int) value ^ (int) (value >> 32);
     }
-    
+
     public boolean equals(Object other) {
         if (other == this) {
             return true;
         }
-        
-        if (other instanceof RubyFixnum) { 
-            RubyFixnum num = (RubyFixnum)other;
-            
+
+        if (other instanceof RubyFixnum) {
+            RubyFixnum num = (RubyFixnum) other;
+
             if (num.value == value) {
                 return true;
             }
         }
-        
+
         return false;
     }
 
-    // Methods of the Fixnum Class (fix_*):
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
 
-    public static RubyFixnum newFixnum(IRuby runtime, long value) {
-        RubyFixnum fixnum;
-        RubyFixnum[] fixnumCache = runtime.getFixnumCache();
-        
-        if (value >= 0 && value < fixnumCache.length) {
-            fixnum = fixnumCache[(int) value];
-            if (fixnum == null) {
-                fixnum = new RubyFixnum(runtime, value);
-                fixnumCache[(int) value] = fixnum;
+    /** fix_to_s
+     * 
+     */
+    public RubyString to_s(IRubyObject[] args) {
+        checkArgumentCount(args, 0, 1);
+
+        int base = args.length == 0 ? 10 : num2int(args[0]);
+        if (base < 2 || base > 36) {
+            throw getRuntime().newArgumentError("illegal radix " + base);
             }
-        } else {
-            fixnum = new RubyFixnum(runtime, value);
+        return getRuntime().newString(Long.toString(value, base));
         }
-        return fixnum;
+
+    /** fix_id2name
+     * 
+     */
+    public IRubyObject id2name() {
+        String symbol = RubySymbol.getSymbol(getRuntime(), value);
+        if (symbol != null) {
+            return getRuntime().newString(symbol);
+    }
+        return getRuntime().getNil();
     }
 
-    public RubyFixnum newFixnum(long newValue) {
-        return newFixnum(getRuntime(), newValue);
+    /** fix_to_sym
+     * 
+     */
+    public IRubyObject to_sym() {
+        String symbol = RubySymbol.getSymbol(getRuntime(), value);
+        if (symbol != null) {
+            return RubySymbol.newSymbol(getRuntime(), symbol);
+    }
+        return getRuntime().getNil();
     }
 
-    public RubyNumeric multiplyWith(RubyFixnum other) {
-        long otherValue = other.getLongValue();
-        if (otherValue == 0) {
-            return RubyFixnum.zero(getRuntime());
+    /** fix_uminus
+     * 
+     */
+    public IRubyObject uminus() {
+        if (value == MIN) { // a gotcha
+            return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
+        }
+        return RubyFixnum.newFixnum(getRuntime(), -value);
+    }
+
+    /** fix_plus
+     * 
+     */
+    public IRubyObject plus(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long otherValue = ((RubyFixnum) other).value;
+            long result = value + otherValue;
+            if ((~(value ^ otherValue) & (value ^ result) & SIGN_BIT) != 0) {
+                return RubyBignum.newBignum(getRuntime(), value).plus(other);
+            }
+            return newFixnum(result);
         }
-        long result = value * otherValue;
-        if (result > MAX || result < MIN || result / otherValue != value) {
-            return (RubyNumeric) RubyBignum.newBignum(getRuntime(), getLongValue()).op_mul(other);
+        if (other instanceof RubyBignum) {
+            return ((RubyBignum) other).plus(this);
         }
-		return newFixnum(result);
+        if (other instanceof RubyFloat) {
+            return getRuntime().newFloat((double) value + ((RubyFloat) other).getDoubleValue());
+        }
+        return coerceBin("+", other);
     }
 
-    public RubyNumeric multiplyWith(RubyInteger other) {
-        return other.multiplyWith(this);
+    /** fix_minus
+     * 
+     */
+    public IRubyObject minus(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long otherValue = ((RubyFixnum) other).value;
+            long result = value - otherValue;
+            if ((~(value ^ ~otherValue) & (value ^ result) & SIGN_BIT) != 0) {
+                return RubyBignum.newBignum(getRuntime(), value).minus(other);
+            }
+            return newFixnum(result);
+        } else if (other instanceof RubyBignum) {
+            return RubyBignum.newBignum(getRuntime(), value).minus(other);
+        } else if (other instanceof RubyFloat) {
+            return getRuntime().newFloat((double) value - ((RubyFloat) other).getDoubleValue());
+        }
+        return coerceBin("-", other);
     }
 
-    public RubyNumeric multiplyWith(RubyFloat other) {
-       return other.multiplyWith(RubyFloat.newFloat(getRuntime(), getLongValue()));
+    /** fix_mul
+     * 
+     */
+    public IRubyObject mul(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long otherValue = ((RubyFixnum) other).value;
+            if (value == 0) {
+                return RubyFixnum.zero(getRuntime());
+            }
+            long result = value * otherValue;
+            IRubyObject r = newFixnum(getRuntime(),result);
+            if(RubyNumeric.fix2long(r) != result || result/value != otherValue) {
+                return (RubyNumeric) RubyBignum.newBignum(getRuntime(), value).mul(other);
+            }
+            return r;
+        } else if (other instanceof RubyBignum) {
+            return ((RubyBignum) other).mul(this);
+        } else if (other instanceof RubyFloat) {
+            return getRuntime().newFloat((double) value * ((RubyFloat) other).getDoubleValue());
+        }
+        return coerceBin("*", other);
     }
 
-    public RubyNumeric quo(IRubyObject other) {
-        return new RubyFloat(getRuntime(), ((RubyNumeric) op_div(other)).getDoubleValue());
+    /** fix_div
+     * here is terrible MRI gotcha:
+     * 1.div 3.0 -> 0
+     * 1 / 3.0   -> 0.3333333333333333
+     * 
+     * MRI is also able to do it in one place by looking at current frame in rb_num_coerce_bin:
+     * rb_funcall(x, ruby_frame->orig_func, 1, y);
+     * 
+     * also note that RubyFloat doesn't override Numeric.div
+     */
+    public IRubyObject div_div(IRubyObject other) {
+        return idiv(other, "div");
     }
-    
-    public IRubyObject op_and(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return newFixnum(value & ((RubyNumeric) other).getTruncatedLongValue());
+
+    public IRubyObject div_slash(IRubyObject other) {
+        return idiv(other, "/");
+    }
+
+    public IRubyObject idiv(IRubyObject other, String method) {
+        if (other instanceof RubyFixnum) {
+            long x = value;
+            long y = ((RubyFixnum) other).value;
+
+            if (y == 0) {
+                throw getRuntime().newZeroDivisionError();
+            }
+
+            long div = x / y;
+            long mod = x % y;
+
+            if (mod < 0 && y > 0 || mod > 0 && y < 0) {
+                div -= 1;
     	}
-    	
-    	return callCoerced("&", other);
+            return getRuntime().newFixnum(div);
+    }
+        return coerceBin(method, other);
     }
 
-    public IRubyObject op_div(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_div(other);
-        } else if (other instanceof RubyBignum) {
-            return RubyBignum.newBignum(getRuntime(), getLongValue()).op_div(other);
-        } else if (other instanceof RubyBigDecimal) {
-            //ugly hack until we support this correctly
-        } else if (other instanceof RubyNumeric) {
+    /** fix_mod
+     * 
+     */
+    public IRubyObject mod(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
             // Java / and % are not the same as ruby
-            long x = getLongValue();
-            long y = ((RubyNumeric) other).getLongValue();
-            
+            long x = value;
+            long y = ((RubyFixnum) other).value;
+
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
             }
-            
+
+            long mod = x % y;
+
+            if (mod < 0 && y > 0 || mod > 0 && y < 0) {
+                mod += y;
+            }
+
+            return getRuntime().newFixnum(mod);
+        }
+        return coerceBin("%", other);
+    }
+
+    /** fix_divmod
+     * 
+     */
+    public IRubyObject divmod(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long x = value;
+            long y = ((RubyFixnum) other).value;
+            final IRuby runtime = getRuntime();
+
+            if (y == 0) {
+                throw runtime.newZeroDivisionError();
+            }
+
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
+                mod += y;
             }
 
-            return getRuntime().newFixnum(div);
-        } 
-        
-        return callCoerced("/", other);
+            IRubyObject fixDiv = RubyFixnum.newFixnum(getRuntime(), div);
+            IRubyObject fixMod = RubyFixnum.newFixnum(getRuntime(), mod);
+
+            return RubyArray.newArray(runtime, fixDiv, fixMod);
+
+    }
+        return coerceBin("divmod", other);
     }
 
-    public IRubyObject op_lshift(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            long width = ((RubyNumeric) other).getLongValue();
-            if (width < 0) {
-                return op_rshift(((RubyNumeric) other).op_uminus());
+    /** fix_quo
+     * 
+     */
+    public IRubyObject quo(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyFloat.newFloat(getRuntime(), (double) value
+                    / (double) ((RubyFixnum) other).value);
             }
-            if (value > 0) {
-                if (width >= BIT_SIZE - 2 || value >> (BIT_SIZE - width) > 0) {
-                    RubyBignum bigValue = 
-                        RubyBignum.newBignum(getRuntime(), RubyBignum.bigIntValue(this));
-                
-                    return bigValue.op_lshift(other);
+        return coerceBin("quo", other);
 	            }
-            } else {
-	            if (width >= BIT_SIZE - 1 || value >> (BIT_SIZE - width) < -1) {
-                    RubyBignum bigValue = 
-                        RubyBignum.newBignum(getRuntime(), RubyBignum.bigIntValue(this));
-                
-                    return bigValue.op_lshift(other);
+
+    /** fix_pow 
+     * 
+     */
+    public IRubyObject pow(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long b = ((RubyFixnum) other).value;
+            if (b == 0) {
+                return RubyFixnum.one(getRuntime());
                 }
+            if (b == 1) {
+                return this;
             }
+            if (b > 0) {
+                return RubyBignum.newBignum(getRuntime(), value).pow(other);
+            }
+            return RubyFloat.newFloat(getRuntime(), Math.pow(value, b));
+        } else if (other instanceof RubyFloat) {
+            return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyFloat) other)
+                    .getDoubleValue()));
+        }
+        return coerceBin("**", other);
+    }
 
-            return newFixnum(value << width);
+    /** fix_abs
+     * 
+     */
+    public IRubyObject abs() {
+        if (value < 0) {
+            return RubyFixnum.newFixnum(getRuntime(), -value);
     	}
-    	
-    	return callCoerced("<<", other);
+        return this;
     }
 
-    public IRubyObject op_minus(IRubyObject other) {
-        if(other instanceof RubyFixnum) {
-            long otherValue = ((RubyNumeric) other).getLongValue();
-            long result = value - otherValue;
-            
-            if ((value <= 0 && otherValue >= 0 && (result > 0 || result < -MAX)) || 
-                (value >= 0 && otherValue <= 0 && (result < 0 || result > MAX))) {
-                return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
+    /** fix_equal
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyBoolean.newBoolean(getRuntime(), value == ((RubyFixnum) other).value);
             }
-            
-            return newFixnum(result);
-        } else if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_minus(other);
-        } else if (other instanceof RubyBignum) {
-            return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
-        } else if (other instanceof RubyBigDecimal) {
-            //ugly hack until we support this correctly
-        } else if (other instanceof RubyNumeric) {
-            long otherValue = ((RubyNumeric) other).getLongValue();
-            long result = value - otherValue;
-            
-            if ((value <= 0 && otherValue >= 0 && (result > 0 || result < -MAX)) || 
-                (value >= 0 && otherValue <= 0 && (result < 0 || result > MAX))) {
-                return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
+        return super.equal(other);
             }
-            
-            return newFixnum(result);
+
+    /** fix_cmp
+     * 
+     */
+    public IRubyObject cmp(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            long otherValue = ((RubyFixnum) other).value;
+            if (value == otherValue) {
+                return RubyFixnum.zero(getRuntime());
         }
+            if (value > otherValue) {
+                return RubyFixnum.one(getRuntime());
+            }
+            return RubyFixnum.minus_one(getRuntime());
+        }
+        return coerceCmp("<=>", other);
+    }
 
-        return callCoerced("-", other);        
+    /** fix_gt
+     * 
+     */
+    public IRubyObject gt(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyBoolean.newBoolean(getRuntime(), value > ((RubyFixnum) other).value);
+    }
+        return coerceRelOp(">", other);
     }
 
-    public IRubyObject op_mod(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_mod(other);
-        } else if (other instanceof RubyBignum) {
-            return RubyBignum.newBignum(getRuntime(), getLongValue()).op_mod(other);
-        } else if (other instanceof RubyBigDecimal) {
-            //ugly hack until we support this correctly
-        } else if (other instanceof RubyNumeric) {
-	        // Java / and % are not the same as ruby
-            long x = getLongValue();
-            long y = ((RubyNumeric) other).getLongValue();
-            long mod = x % y;
+    /** fix_ge
+     * 
+     */
+    public IRubyObject ge(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyBoolean.newBoolean(getRuntime(), value >= ((RubyFixnum) other).value);
+        }
+        return coerceRelOp(">=", other);
+    }
 
-            if (mod < 0 && y > 0 || mod > 0 && y < 0) {
-                mod += y;
+    /** fix_lt
+     * 
+     */
+    public IRubyObject lt(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyBoolean.newBoolean(getRuntime(), value < ((RubyFixnum) other).value);
             }
+        return coerceRelOp("<", other);
+    }
 
-            return getRuntime().newFixnum(mod);
+    /** fix_le
+     * 
+     */
+    public IRubyObject le(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return RubyBoolean.newBoolean(getRuntime(), value <= ((RubyFixnum) other).value);
         }
-        
-        return (RubyNumeric) callCoerced("%", other);
+        return coerceRelOp("<=", other);
     }
 
-    public IRubyObject op_mul(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-        	return ((RubyNumeric) other).multiplyWith(this);
+    /** fix_rev
+     * 
+     */
+    public IRubyObject rev() {
+        return newFixnum(~value);
     	}
-    	
-    	return callCoerced("*", other);
+
+    /** fix_and
+     * 
+     */
+    public IRubyObject and(IRubyObject other) {
+        if (other instanceof RubyBignum) {
+            return ((RubyBignum) other).and(this);
+    }
+        return RubyFixnum.newFixnum(getRuntime(), value & num2long(other));
     }
 
-    public IRubyObject op_or(IRubyObject other) {
+    /** fix_or 
+     * 
+     */
+    public IRubyObject or(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return newFixnum(value | ((RubyFixnum) other).value);
+        }
         if (other instanceof RubyBignum) {
-            return ((RubyBignum) other).op_or(this);
-        } else if (other instanceof RubyNumeric) {
+            return ((RubyBignum) other).or(this);
+        }
+        if (other instanceof RubyNumeric) {
             return newFixnum(value | ((RubyNumeric) other).getLongValue());
         }
-        
-        return (RubyInteger) callCoerced("|", other);
+
+        return or(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
     }
 
-    public IRubyObject op_plus(IRubyObject other) {
-        if(other instanceof RubyFixnum) {
-            long otherValue = ((RubyFixnum)other).getLongValue();
-            long result = value + otherValue;
-            if((value < 0 && otherValue < 0 && (result > 0 || result < -MAX)) || 
-                (value > 0 && otherValue > 0 && (result < 0 || result > MAX))) {
-                return RubyBignum.newBignum(getRuntime(), value).op_plus(other);
+    /** fix_xor 
+     * 
+     */
+    public IRubyObject xor(IRubyObject other) {
+        if (other instanceof RubyFixnum) {
+            return newFixnum(value ^ ((RubyFixnum) other).value);
             }
-            return newFixnum(result);
-        }
-        if(other instanceof RubyBignum) {
-            return RubyBignum.newBignum(getRuntime(), value).op_plus(other);
+        if (other instanceof RubyBignum) {
+            return ((RubyBignum) other).xor(this);
         }
-        if(other instanceof RubyFloat) {
-            return getRuntime().newFloat(getDoubleValue() + ((RubyFloat)other).getDoubleValue());
+        if (other instanceof RubyNumeric) {
+            return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
         }
-        return callCoerced("+", other);
-    }
 
-    public IRubyObject op_pow(IRubyObject other) {
-        if (other instanceof RubyFloat) {
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_pow(other);
-        } else if (other instanceof RubyNumeric) {
-            long longValue = ((RubyNumeric) other).getLongValue();
-            
-		    if (longValue == 0) {
-		        return getRuntime().newFixnum(1);
-		    } else if (longValue == 1) {
-		        return this;
-		    } else if (longValue > 1) {
-		        return RubyBignum.newBignum(getRuntime(), getLongValue()).op_pow(other);
-		    } 
-		      
-            return RubyFloat.newFloat(getRuntime(), getDoubleValue()).op_pow(other);
-        }
-        
-        return callCoerced("**", other);
-    }
-
-    public IRubyObject op_rshift(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            long width = ((RubyNumeric) other).getLongValue();
-            if (width < 0) {
-			    return op_lshift(((RubyNumeric) other).op_uminus());
-		    }
-            return newFixnum(value >> width);
-    	}
-    	
-    	return callCoerced(">>", other);
-    }
+        return xor(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
+        }
 
-    public IRubyObject op_xor(IRubyObject other) {
+    /** fix_aref 
+     * 
+     */
+    public IRubyObject aref(IRubyObject other) {
         if (other instanceof RubyBignum) {
-            return ((RubyBignum) other).op_xor(this);
-        } else if (other instanceof RubyNumeric) {
-            return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
+            RubyBignum big = (RubyBignum) other;
+            RubyObject tryFix = RubyBignum.bignorm(getRuntime(), big.getValue());
+            if (!(tryFix instanceof RubyFixnum)) {
+                if (big.getValue().signum() == 0 || value >= 0) {
+                    return RubyFixnum.zero(getRuntime());
+                }
+                return RubyFixnum.one(getRuntime());
+            }
         }
-        
-        return callCoerced("^", other);
-    }
 
-    public RubyString to_s(IRubyObject[] args) {
-    	checkArgumentCount(args, 0, 1);
+        long otherValue = num2long(other);
 
-    	int radix = args.length == 0 ? 10 : (int) args[0].convertToInteger().getLongValue();
-        
-        return getRuntime().newString(Long.toString(getLongValue(), radix));
-    }
-    
-    public RubyFloat to_f() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue());
+        if (otherValue < 0) {
+            return RubyFixnum.zero(getRuntime());
+        }
+
+        if (BIT_SIZE - 1 < otherValue) {
+            if (value < 0) {
+                return RubyFixnum.one(getRuntime());
+            }
+            return RubyFixnum.zero(getRuntime());
+        }
+
+        return (value & (1L << otherValue)) == 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
     }
 
-    public RubyFixnum size() {
-        return newFixnum((long) Math.ceil(BIT_SIZE / 8.0));
+    /** fix_lshift 
+     * 
+     */
+    public IRubyObject lshift(IRubyObject other) {
+        long width = num2long(other);
+
+        if (width < 0) {
+            return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
+        }
+
+        if (width == 0) {
+            return this;
+        }
+
+        if (width > BIT_SIZE - 1 || ((~0L << BIT_SIZE - width - 1) & value) != 0) {
+            return RubyBignum.newBignum(getRuntime(), value).lshift(other);
+        }
+
+        return newFixnum(value << width);
     }
 
-    public RubyFixnum aref(IRubyObject other) {
-        long position = other.convertToInteger().getLongValue();
+    /** fix_rshift 
+     * 
+     */
+    public IRubyObject rshift(IRubyObject other) {
+        long width = num2long(other);
 
-        // Seems mighty expensive to keep creating over and over again.
-        // How else can this be done though?
-        if (position > BIT_SIZE) {
-            return RubyBignum.newBignum(getRuntime(), value).aref(other);
+        if (width < 0) {
+            return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
         }
 
-        return newFixnum((value & 1L << position) == 0 ? 0 : 1);
+        if (width == 0) {
+            return this;
+        }
+
+        if (width >= BIT_SIZE - 1) {
+            if (value < 0) {
+                return RubyFixnum.minus_one(getRuntime());
+        }
+            return RubyFixnum.zero(getRuntime());
+        }
+
+        return newFixnum(value >> width);
     }
 
-    public IRubyObject id2name() {
-        String symbol = RubySymbol.getSymbol(getRuntime(), value);
-        if (symbol != null) {
-            return getRuntime().newString(symbol);
+    /** fix_to_f 
+     * 
+     */
+    public IRubyObject to_f() {
+        return RubyFloat.newFloat(getRuntime(), (double) value);
         }
-        return getRuntime().getNil();
+
+    /** fix_size 
+     * 
+     */
+    public IRubyObject size() {
+        return newFixnum((long) ((BIT_SIZE + 7) / 8));
     }
 
-    public RubyFixnum invert() {
-        return newFixnum(~value);
+    /** fix_zero_p 
+     * 
+     */
+    public IRubyObject zero_p() {
+        return RubyBoolean.newBoolean(getRuntime(), value == 0);
     }
 
     public RubyFixnum id() {
         return newFixnum(value * 2 + 1);
     }
 
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
-    public IRubyObject times() {
-        IRuby runtime = getRuntime();
-        ThreadContext context = runtime.getCurrentContext();
-        for (long i = 0; i < value; i++) {
-            context.yield(newFixnum(runtime, i));
+    public IRubyObject rbClone() {
+        throw getRuntime().newTypeError("can't clone Fixnum");
         }
-        return this;
-    }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         if (value <= MAX_MARSHAL_FIXNUM) {
             output.write('i');
             output.dumpInt((int) value);
         } else {
             output.dumpObject(RubyBignum.newBignum(getRuntime(), value));
         }
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
+
+    /*  ================
+     *  Singleton Methods
+     *  ================ 
+     */
+
+    /** rb_fix_induced_from
+     * 
+     */
+
+    public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
+        return RubyNumeric.num2fix(other);
+    }
 }
diff --git a/src/org/jruby/RubyFloat.java b/src/org/jruby/RubyFloat.java
index a56adb7639..492b954edf 100644
--- a/src/org/jruby/RubyFloat.java
+++ b/src/org/jruby/RubyFloat.java
@@ -1,485 +1,504 @@
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
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyFloat extends RubyNumeric {
+
+    public static RubyClass createFloatClass(IRuby runtime) {
+        RubyClass floatc = runtime.defineClass("Float", runtime.getClass("Numeric"),
+                ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyFloat.class);
+        floatc.getSingletonClass().undefineMethod("allocate");
+        floatc.getSingletonClass().undefineMethod("new");
+
+        floatc.defineFastSingletonMethod("induced_from", callbackFactory.getSingletonMethod(
+                "induced_from", IRubyObject.class));
+        floatc.includeModule(runtime.getModule("Precision"));
+
+        // Java Doubles are 64 bit long:            
+        floatc.defineConstant("ROUNDS", RubyFixnum.newFixnum(runtime, 1));
+        floatc.defineConstant("RADIX", RubyFixnum.newFixnum(runtime, 2));
+        floatc.defineConstant("MANT_DIG", RubyFixnum.newFixnum(runtime, 53));
+        floatc.defineConstant("DIG", RubyFixnum.newFixnum(runtime, 15));
+        // Double.MAX_EXPONENT since Java 1.6
+        floatc.defineConstant("MIN_EXP", RubyFixnum.newFixnum(runtime, -1021));
+        // Double.MAX_EXPONENT since Java 1.6            
+        floatc.defineConstant("MAX_EXP", RubyFixnum.newFixnum(runtime, 1024));
+        floatc.defineConstant("MIN_10_EXP", RubyFixnum.newFixnum(runtime, -307));
+        floatc.defineConstant("MAX_10_EXP", RubyFixnum.newFixnum(runtime, -308));
+        floatc.defineConstant("MIN", RubyFloat.newFloat(runtime, Double.MIN_VALUE));
+        floatc.defineConstant("MAX", RubyFloat.newFloat(runtime, Double.MAX_VALUE));
+        floatc.defineConstant("EPSILON", RubyFloat.newFloat(runtime, 2.2204460492503131e-16));
+
+        floatc.defineFastMethod("to_s", callbackFactory.getMethod("to_s"));
+        floatc.defineFastMethod("coerce", callbackFactory.getMethod("coerce", IRubyObject.class));
+        floatc.defineFastMethod("-@", callbackFactory.getMethod("uminus"));
+        floatc.defineFastMethod("+", callbackFactory.getMethod("plus", IRubyObject.class));
+        floatc.defineFastMethod("-", callbackFactory.getMethod("minus", IRubyObject.class));
+        floatc.defineFastMethod("*", callbackFactory.getMethod("mul", IRubyObject.class));
+        floatc.defineFastMethod("/", callbackFactory.getMethod("fdiv", IRubyObject.class));
+        floatc.defineFastMethod("%", callbackFactory.getMethod("mod", IRubyObject.class));
+        floatc.defineFastMethod("modulo", callbackFactory.getMethod("mod", IRubyObject.class));
+        floatc.defineFastMethod("divmod", callbackFactory.getMethod("divmod", IRubyObject.class));
+        floatc.defineFastMethod("**", callbackFactory.getMethod("pow", IRubyObject.class));
+        floatc.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
+        floatc.defineFastMethod("<=>", callbackFactory.getMethod("cmp", IRubyObject.class));
+        floatc.defineFastMethod(">", callbackFactory.getMethod("gt", IRubyObject.class));
+        floatc.defineFastMethod(">=", callbackFactory.getMethod("ge", IRubyObject.class));
+        floatc.defineFastMethod("<", callbackFactory.getMethod("lt", IRubyObject.class));
+        floatc.defineFastMethod("<=", callbackFactory.getMethod("le", IRubyObject.class));
+        floatc.defineFastMethod("eql?", callbackFactory.getMethod("eql_p", IRubyObject.class));
+        floatc.defineFastMethod("hash", callbackFactory.getMethod("hash"));
+        floatc.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
+        floatc.defineFastMethod("abs", callbackFactory.getMethod("abs"));
+        floatc.defineFastMethod("zero?", callbackFactory.getMethod("zero_p"));
+
+        floatc.defineFastMethod("to_i", callbackFactory.getMethod("truncate"));
+        floatc.defineFastMethod("to_int", callbackFactory.getMethod("truncate"));
+        floatc.defineFastMethod("floor", callbackFactory.getMethod("floor"));
+        floatc.defineFastMethod("ceil", callbackFactory.getMethod("ceil"));
+        floatc.defineFastMethod("round", callbackFactory.getMethod("round"));
+        floatc.defineFastMethod("truncate", callbackFactory.getMethod("truncate"));
+
+        floatc.defineFastMethod("nan?", callbackFactory.getMethod("nan_p"));
+        floatc.defineFastMethod("infinite?", callbackFactory.getMethod("infinite_p"));
+        floatc.defineFastMethod("finite?", callbackFactory.getMethod("finite_p"));
+
+        return floatc;
+    }
+
     private final double value;
 
     public RubyFloat(IRuby runtime) {
         this(runtime, 0.0);
     }
 
     public RubyFloat(IRuby runtime, double value) {
         super(runtime, runtime.getClass("Float"));
         this.value = value;
     }
 
     public Class getJavaClass() {
         return Double.TYPE;
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public double getValue() {
         return this.value;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return (long) value;
     }
-    
-    public RubyFloat convertToFloat() {
-    	return this;
-    }
 
-    public static RubyClass createFloatClass(IRuby runtime) {
-        RubyClass result = runtime.defineClass("Float", runtime.getClass("Numeric"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
-        CallbackFactory callbackFactory = runtime.callbackFactory(RubyFloat.class);
-        
-        result.defineFastMethod("+", callbackFactory.getMethod("op_plus", IRubyObject.class));
-        result.defineFastMethod("-", callbackFactory.getMethod("op_minus", IRubyObject.class));
-        result.defineFastMethod("*", callbackFactory.getMethod("op_mul", IRubyObject.class));
-        result.defineFastMethod("/", callbackFactory.getMethod("op_div", IRubyObject.class));
-        result.defineFastMethod("%", callbackFactory.getMethod("op_mod", IRubyObject.class));
-        result.defineFastMethod("**", callbackFactory.getMethod("op_pow", IRubyObject.class));
-        // Although not explicitly documented in the Pickaxe, Ruby 1.8 Float
-        // does have its own implementations of these relational operators.
-        // These appear to be necessary if for no oher reason than proper NaN
-        // handling.
-        result.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
-        result.defineFastMethod("<=>", callbackFactory.getMethod("cmp",
-                IRubyObject.class));
-        result.defineFastMethod(">", callbackFactory.getMethod("op_gt",
-                IRubyObject.class));
-        result.defineFastMethod(">=", callbackFactory.getMethod("op_ge", IRubyObject.class));
-        result.defineFastMethod("<", callbackFactory.getMethod("op_lt", IRubyObject.class));
-        result.defineFastMethod("<=", callbackFactory.getMethod("op_le", IRubyObject.class));
-        result.defineFastMethod("ceil", callbackFactory.getMethod("ceil"));
-        result.defineFastMethod("finite?", callbackFactory.getMethod("finite_p"));
-        result.defineFastMethod("floor", callbackFactory.getMethod("floor"));
-        result.defineFastMethod("hash", callbackFactory.getMethod("hash"));
-        result.defineFastMethod("infinite?", callbackFactory.getMethod("infinite_p"));
-        result.defineFastMethod("nan?", callbackFactory.getMethod("nan_p"));
-        result.defineFastMethod("round", callbackFactory.getMethod("round"));
-        result.defineFastMethod("to_i", callbackFactory.getMethod("to_i"));
-        result.defineAlias("to_int", "to_i");
-        result.defineFastMethod("to_f", callbackFactory.getMethod("to_f"));
-        result.defineFastMethod("to_s", callbackFactory.getMethod("to_s"));
-        result.defineFastMethod("truncate", callbackFactory.getMethod("truncate"));
-
-        result.getMetaClass().undefineMethod("new");
-        result.defineFastSingletonMethod("induced_from", callbackFactory.getSingletonMethod("induced_from", IRubyObject.class));
-        return result;
+    public RubyFloat convertToFloat() {
+        return this;
     }
 
     protected int compareValue(RubyNumeric other) {
         double otherVal = other.getDoubleValue();
         return getValue() > otherVal ? 1 : getValue() < otherVal ? -1 : 0;
     }
 
-    public RubyFixnum hash() {
-        return getRuntime().newFixnum(new Double(value).hashCode());
-    }
-
-    // Float methods (flo_*)
-
     public static RubyFloat newFloat(IRuby runtime, double value) {
         return new RubyFloat(runtime, value);
     }
 
-    public static RubyFloat induced_from(IRubyObject recv, IRubyObject number) {
-        if (number instanceof RubyFloat) {
-            return (RubyFloat) number;
-        } else if (number instanceof RubyInteger) {
-            return (RubyFloat) number.callMethod(number.getRuntime().getCurrentContext(), "to_f");
-        } else {
-            throw recv.getRuntime().newTypeError("failed to convert " + number.getMetaClass() + " into Float");
-        }
-    }
-
-    public RubyArray coerce(RubyNumeric other) {
-        return getRuntime().newArray(newFloat(getRuntime(), other.getDoubleValue()), this);
-    }
-
-    public RubyInteger ceil() {
-        double val = Math.ceil(getDoubleValue());
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
 
-        if (val < RubyFixnum.MIN || val > RubyFixnum.MAX) {
-            return RubyBignum.newBignum(getRuntime(), val);
+    /** rb_flo_induced_from
+     * 
+     */
+    public static IRubyObject induced_from(IRubyObject recv, IRubyObject number) {
+        if (number instanceof RubyFixnum || number instanceof RubyBignum) {
+            return number.callMethod(recv.getRuntime().getCurrentContext(), "to_f");
         }
-		return getRuntime().newFixnum((long) val);
-    }
-
-    public RubyInteger floor() {
-        double val = Math.floor(getDoubleValue());
-
-        if (val < Long.MIN_VALUE || val > Long.MAX_VALUE) {
-            return RubyBignum.newBignum(getRuntime(), val);
+        if (number instanceof RubyFloat) {
+            return number;
         }
-		return getRuntime().newFixnum((long) val);
+        throw recv.getRuntime().newTypeError(
+                "failed to convert " + number.getMetaClass() + " into Float");
     }
 
-    public RubyInteger round() {
-        double decimal = value % 1;
-        double round = Math.round(value);
-
-        // Ruby rounds differently than java for negative numbers.
-        if (value < 0 && decimal == -0.5) {
-            round -= 1;
+    /** flo_to_s
+     * 
+     */
+    public IRubyObject to_s() {
+        if (Double.isInfinite(value)) {
+            return RubyString.newString(getRuntime(), value < 0 ? "-Infinity" : "Infinity");
         }
 
-        if (value < RubyFixnum.MIN || value > RubyFixnum.MAX) {
-            return RubyBignum.newBignum(getRuntime(), round);
+        if (Double.isNaN(value)) {
+            return RubyString.newString(getRuntime(), "NaN");
         }
-        return getRuntime().newFixnum((long) round);
+
+        // TODO: needs formatting "%#.15g" and "%#.14e"
+        return RubyString.newString(getRuntime(), "" + value);
     }
 
-    public RubyInteger truncate() {
-        if (value > 0.0) {
-            return floor();
-        } else if (value < 0.0) {
-            return ceil();
-        } else {
-            return RubyFixnum.zero(getRuntime());
-        }
+    /** flo_coerce
+     * 
+     */
+    public IRubyObject coerce(IRubyObject other) {
+        // MRI doesn't type check here either
+        return getRuntime().newArray(
+                newFloat(getRuntime(), ((RubyNumeric) other).getDoubleValue()), this);
     }
 
-    public RubyNumeric multiplyWith(RubyNumeric other) {
-        return other.multiplyWith(this);
+    /** flo_uminus
+     * 
+     */
+    public IRubyObject uminus() {
+        return RubyFloat.newFloat(getRuntime(), -value);
     }
 
-    public RubyNumeric multiplyWith(RubyFloat other) {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue() * other.getDoubleValue());
+    /** flo_plus
+     * 
+     */
+    public IRubyObject plus(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            return RubyFloat.newFloat(getRuntime(), value + ((RubyNumeric) other).getDoubleValue());
+        }
+        return coerceBin("+", other);
     }
 
-    public RubyNumeric multiplyWith(RubyInteger other) {
-        return other.multiplyWith(this);
+    /** flo_minus
+     * 
+     */
+    public IRubyObject minus(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            return RubyFloat.newFloat(getRuntime(), value - ((RubyNumeric) other).getDoubleValue());
+        }
+        return coerceBin("-", other);
     }
 
-    public RubyNumeric multiplyWith(RubyBignum other) {
-        return other.multiplyWith(this);
+    /** flo_mul
+     * 
+     */
+    public IRubyObject mul(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            return RubyFloat.newFloat(getRuntime(), value * ((RubyNumeric) other).getDoubleValue());
+        }
+        return coerceBin("*", other);
     }
-    
-    public IRubyObject op_div(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return RubyFloat.newFloat(getRuntime(), 
-                getDoubleValue() / ((RubyNumeric) other).getDoubleValue());
-    	}
-    	
-    	return callCoerced("/", other);
+
+    /** flo_div
+     * 
+     */
+    public IRubyObject fdiv(IRubyObject other) { // don't override Numeric#div !
+        if (other instanceof RubyNumeric) {
+            return RubyFloat.newFloat(getRuntime(), value / ((RubyNumeric) other).getDoubleValue());
+        }
+        return coerceBin("div", other);
     }
 
-    public IRubyObject op_mod(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            // Modelled after c ruby implementation (java /,% not same as ruby)
-            double x = getDoubleValue();
+    /** flo_mod
+     * 
+     */
+    public IRubyObject mod(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
             double y = ((RubyNumeric) other).getDoubleValue();
-            double mod = x % y;
+            // Modelled after c ruby implementation (java /,% not same as ruby)        
+            double x = value;
 
-            if (mod < 0 && y > 0 || mod > 0 && y < 0) {
+            double mod = Math.IEEEremainder(x, y);
+            if (y * mod < 0) {
                 mod += y;
             }
 
             return RubyFloat.newFloat(getRuntime(), mod);
-    	}
-    	
-    	return callCoerced("%", other);
+        }
+        return coerceBin("%", other);
     }
 
-    public IRubyObject op_minus(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return RubyFloat.newFloat(getRuntime(), 
-                getDoubleValue() - ((RubyNumeric) other).getDoubleValue());
-    	}
+    /** flo_divmod
+     * 
+     */
+    public IRubyObject divmod(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double y = ((RubyNumeric) other).getDoubleValue();
+            double x = value;
 
-    	return callCoerced("-", other);
-    }
+            double mod = Math.IEEEremainder(x, y);
+            double div = (x - mod) / y;
 
-    // TODO: Anders double-dispatch here does not seem like it has much benefit when we need
-    // to dynamically check to see if we need to coerce first.
-    public IRubyObject op_mul(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return ((RubyNumeric) other).multiplyWith(this);
-    	}
-    	
-    	return callCoerced("*", other);
+            if (y * mod < 0) {
+                mod += y;
+                div -= 1.0;
+            }
+            final IRuby runtime = getRuntime();
+            IRubyObject car = dbl2num(runtime, div);
+            RubyFloat cdr = RubyFloat.newFloat(runtime, mod);
+            return RubyArray.newArray(runtime, car, cdr);
+        }
+        return coerceBin("%", other);
     }
 
-    public IRubyObject op_plus(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return RubyFloat.newFloat(getRuntime(),
-                getDoubleValue() + ((RubyNumeric) other).getDoubleValue());
-    	}
-    	
-    	return callCoerced("+", other);
+    /** flo_pow
+     * 
+     */
+    public IRubyObject pow(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyNumeric) other)
+                    .getDoubleValue()));
+        }
+        return coerceBin("/", other);
     }
 
-    public IRubyObject op_pow(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return RubyFloat.newFloat(getRuntime(), 
-                Math.pow(getDoubleValue(), ((RubyNumeric) other).getDoubleValue()));
-    	}
-    	
-    	return callCoerced("**", other);
-    }
+    /** flo_eq
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        if (Double.isNaN(value)) {
+            return getRuntime().getFalse();
+        }
+        if (other instanceof RubyNumeric) {
+            return RubyBoolean.newBoolean(getRuntime(), value == ((RubyNumeric) other)
+                    .getDoubleValue());
+        }
+        // Numeric.equal            
+        return super.equal(other);
 
-    public IRubyObject op_uminus() {
-        return RubyFloat.newFloat(getRuntime(), -value);
     }
 
-    public IRubyObject to_s() {
-        return getRuntime().newString("" + getValue());
+    /** flo_cmp
+     * 
+     */
+    public IRubyObject cmp(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double b = ((RubyNumeric) other).getDoubleValue();
+            return dbl_cmp(getRuntime(), value, b);
+        }
+        return coerceCmp("<=>", other);
     }
 
-    public RubyFloat to_f() {
-        return this;
+    /** flo_gt
+     * 
+     */
+    public IRubyObject gt(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double b = ((RubyNumeric) other).getDoubleValue();
+            return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value > b);
+        }
+        return coerceRelOp(">", other);
     }
 
-    public RubyInteger to_i() {
-    	if (value > Integer.MAX_VALUE) {
-    		return RubyBignum.newBignum(getRuntime(), getValue());
-    	}
-        return getRuntime().newFixnum(getLongValue());
+    /** flo_ge
+     * 
+     */
+    public IRubyObject ge(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double b = ((RubyNumeric) other).getDoubleValue();
+            return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value >= b);
+        }
+        return coerceRelOp(">=", other);
     }
 
-    public IRubyObject infinite_p() {
-        if (getValue() == Double.POSITIVE_INFINITY) {
-            return getRuntime().newFixnum(1);
-        } else if (getValue() == Double.NEGATIVE_INFINITY) {
-            return getRuntime().newFixnum(-1);
-        } else {
-            return getRuntime().getNil();
+    /** flo_lt
+     * 
+     */
+    public IRubyObject lt(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double b = ((RubyNumeric) other).getDoubleValue();
+            return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value < b);
         }
+        return coerceRelOp("<", other);
     }
 
-    public RubyBoolean finite_p() {
-        if (! infinite_p().isNil()) {
-            return getRuntime().getFalse();
-        }
-        if (nan_p().isTrue()) {
-            return getRuntime().getFalse();
+    /** flo_le
+     * 
+     */
+    public IRubyObject le(IRubyObject other) {
+        if (other instanceof RubyNumeric) {
+            double b = ((RubyNumeric) other).getDoubleValue();
+            return RubyBoolean.newBoolean(getRuntime(), !Double.isNaN(b) && value <= b);
         }
-        return getRuntime().getTrue();
+        return coerceRelOp("<=", other);
     }
 
-    public RubyBoolean nan_p() {
-        return getRuntime().newBoolean(Double.isNaN(getValue()));
+    /** flo_eql
+     * 
+     */
+    public IRubyObject eql_p(IRubyObject other) {
+        if (other instanceof RubyFloat) {
+            double b = ((RubyFloat) other).value;
+            if (Double.isNaN(value) || Double.isNaN(b)) {
+                return getRuntime().getFalse();
+            }
+            if (value == b) {
+                return getRuntime().getTrue();
+            }
+        }
+        return getRuntime().getFalse();
     }
 
-    public RubyBoolean zero_p() {
-        return getRuntime().newBoolean(getValue() == 0);
+    /** flo_hash
+     * 
+     */
+    public RubyFixnum hash() {
+        long l = Double.doubleToLongBits(value);
+        return getRuntime().newFixnum((long) (l ^ l >>> 32));
     }
 
-	public void marshalTo(MarshalStream output) throws java.io.IOException {
-		output.write('f');
-		String strValue = this.toString();
-
-		if (Double.isInfinite(value)) {
-			strValue = value < 0 ? "-inf" : "inf";
-		} else if (Double.isNaN(value)) {
-			strValue = "nan";
-		}
-		output.dumpString(strValue);
-	}
-	
-    public static RubyFloat unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
-        return RubyFloat.newFloat(input.getRuntime(),
-                                    Double.parseDouble(input.unmarshalString()));
+    /** flo_fo 
+     * 
+     */
+    public IRubyObject to_f() {
+        return this;
     }
-    
-    /* flo_eq */
-    public IRubyObject equal(IRubyObject other) {
-        if (!(other instanceof RubyNumeric)) {
-            return other.callMethod(getRuntime().getCurrentContext(), "==", this);
-        }
 
-        double otherValue = ((RubyNumeric) other).getDoubleValue();
-        
-        if (other instanceof RubyFloat && Double.isNaN(otherValue)) {
-            return getRuntime().getFalse();
+    /** flo_abs
+     * 
+     */
+    public IRubyObject abs() {
+        if (value < 0) {
+            return RubyFloat.newFloat(getRuntime(), Math.abs(value));
         }
-        
-        return (value == otherValue) ? getRuntime().getTrue() : getRuntime().getFalse();
+        return this;
     }
-    
 
-    /* flo_cmp */
-    public IRubyObject cmp(IRubyObject other) {
-        if (!(other instanceof RubyNumeric)) {
-            IRubyObject[] tmp = getCoerced(other, false);
-            if (tmp == null) {
-                return getRuntime().getNil();
-            }
-            return tmp[1].callMethod(getRuntime().getCurrentContext(), "<=>", tmp[0]);
-        }
-
-        return doubleCompare(((RubyNumeric) other).getDoubleValue());
+    /** flo_zero_p
+     * 
+     */
+    public IRubyObject zero_p() {
+        return RubyBoolean.newBoolean(getRuntime(), value == 0.0);
     }
 
-    
-    private void cmperr(IRubyObject other) {
-        String message = "comparison of " + this.getType() + " with " + other.getType() + " failed";
+    /** flo_truncate
+     * 
+     */
+    public IRubyObject truncate() {
+        double f = value;
+        if (f > 0.0) {
+            f = Math.floor(f);
+        }
+        if (f > 0.0) {
+            f = Math.ceil(f);
+        }
+        return dbl2num(getRuntime(), f);
+    }
 
-        throw this.getRuntime().newArgumentError(message);
+    /** loor
+     * 
+     */
+    public IRubyObject floor() {
+        return dbl2num(getRuntime(), Math.floor(value));
     }
 
-    /* flo_gt */
-    public IRubyObject op_gt(IRubyObject other) {
-        if (Double.isNaN(value)) {
-            return getRuntime().getFalse();
-        }
+    /** flo_ceil
+     * 
+     */
+    public IRubyObject ceil() {
+        return dbl2num(getRuntime(), Math.ceil(value));
+    }
 
-        if (!(other instanceof RubyNumeric)) {
-            IRubyObject[] tmp = getCoerced(other, false);
-            if (tmp == null) {
-                cmperr(other);
-            }
-            
-            return tmp[1].callMethod(getRuntime().getCurrentContext(), "<=>", tmp[0]);
+    /** flo_round
+     * 
+     */
+    public IRubyObject round() {
+        double f = value;
+        if (f > 0.0) {
+            f = Math.floor(f + 0.5);
         }
-        
-        double oth = ((RubyNumeric) other).getDoubleValue();
-        
-        if (other instanceof RubyFloat && Double.isNaN(oth)) { 
-            return getRuntime().getFalse();
+        if (f < 0.0) {
+            f = Math.ceil(f - 0.5);
         }
-        
-        return value > oth ? getRuntime().getTrue() : getRuntime().getFalse();
+        return dbl2num(getRuntime(), f);
     }
-    
-    /* flo_ge */
-    public IRubyObject op_ge(IRubyObject other) {
-        if (Double.isNaN(value)) {
-            return getRuntime().getFalse();
-        }
 
-        if (!(other instanceof RubyNumeric)) {
-            IRubyObject[] tmp = getCoerced(other, false);
-            if (tmp == null) {
-                cmperr(other);
-            }
-            
-            return tmp[1].callMethod(getRuntime().getCurrentContext(), "<=>", tmp[0]);
-        }
-        
-        double oth = ((RubyNumeric) other).getDoubleValue();
-        
-        if (other instanceof RubyFloat && Double.isNaN(oth)) { 
-            return getRuntime().getFalse();
-        }
-        
-        return value >= oth ? getRuntime().getTrue() : getRuntime().getFalse();
+    /** flo_is_nan_p
+     * 
+     */
+    public IRubyObject nan_p() {
+        return RubyBoolean.newBoolean(getRuntime(), Double.isNaN(value));
     }
 
-    /* flo_lt */
-    public IRubyObject op_lt(IRubyObject other) {
-        if (Double.isNaN(value)) {
-            return getRuntime().getFalse();
+    /** flo_is_infinite_p
+     * 
+     */
+    public IRubyObject infinite_p() {
+        if (Double.isInfinite(value)) {
+            return RubyFixnum.newFixnum(getRuntime(), value < 0 ? -1 : 1);
         }
+        return getRuntime().getNil();
+    }
 
-        if (!(other instanceof RubyNumeric)) {
-            IRubyObject[] tmp = getCoerced(other, false);
-            if (tmp == null) {
-                cmperr(other);
-            }
-            
-            return tmp[1].callMethod(getRuntime().getCurrentContext(), "<=>", tmp[0]);
-        }
-        
-        double oth = ((RubyNumeric) other).getDoubleValue();
-        
-        if (other instanceof RubyFloat && Double.isNaN(oth)) { 
+    /** flo_is_finite_p
+     * 
+     */
+    public IRubyObject finite_p() {
+        if (Double.isInfinite(value) || Double.isNaN(value)) {
             return getRuntime().getFalse();
         }
-        
-        return value < oth ? getRuntime().getTrue() : getRuntime().getFalse();
+        return getRuntime().getTrue();
     }
 
-    
-    /* flo_le */
-    public IRubyObject op_le(IRubyObject other) {
-        if (Double.isNaN(value)) {
-            return getRuntime().getFalse();
-        }
+    public void marshalTo(MarshalStream output) throws java.io.IOException {
+        output.write('f');
+        String strValue = this.toString();
 
-        if (!(other instanceof RubyNumeric)) {
-            IRubyObject[] tmp = getCoerced(other, false);
-            if (tmp == null) {
-                cmperr(other);
-            }
-            
-            return tmp[1].callMethod(getRuntime().getCurrentContext(), "<=>", tmp[0]);
-        }
-        
-        double oth = ((RubyNumeric) other).getDoubleValue();
-        
-        if (other instanceof RubyFloat && Double.isNaN(oth)) { 
-            return getRuntime().getFalse();
+        if (Double.isInfinite(value)) {
+            strValue = value < 0 ? "-inf" : "inf";
+        } else if (Double.isNaN(value)) {
+            strValue = "nan";
         }
-        
-        return value <= oth ? getRuntime().getTrue() : getRuntime().getFalse();
+        output.dumpString(strValue);
     }
 
-    
-    /* dbl_cmp */
-    private IRubyObject doubleCompare(double oth) {
-        if (Double.isNaN(value) || Double.isNaN(oth)) {
-            return getRuntime().getNil();
-        }
-        
-        if (value == oth) {
-            return getRuntime().newFixnum(0);
-        }
-        
-        return value > oth ? getRuntime().newFixnum(1) : getRuntime().newFixnum(-1);
+    public static RubyFloat unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
+        return RubyFloat.newFloat(input.getRuntime(), Double.parseDouble(input.unmarshalString()));
     }
-    
+
 }
diff --git a/src/org/jruby/RubyInteger.java b/src/org/jruby/RubyInteger.java
index e4da2ff147..9b2bfff1ab 100644
--- a/src/org/jruby/RubyInteger.java
+++ b/src/org/jruby/RubyInteger.java
@@ -1,156 +1,228 @@
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
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
 
+import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** Implementation of the Integer class.
  *
  * @author  jpetersen
  */
-public abstract class RubyInteger extends RubyNumeric { 
+public abstract class RubyInteger extends RubyNumeric {
+
+    public static RubyClass createIntegerClass(IRuby runtime) {
+        RubyClass integer = runtime.defineClass("Integer", runtime.getClass("Numeric"),
+                ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyInteger.class);
+        integer.getSingletonClass().undefineMethod("allocate");
+        integer.getSingletonClass().undefineMethod("new");
+
+        integer.defineFastMethod("integer?", callbackFactory.getMethod("int_p"));
+        integer.defineMethod("upto", callbackFactory.getMethod("upto", IRubyObject.class));
+        integer.defineMethod("downto", callbackFactory.getMethod("downto", IRubyObject.class));
+        integer.defineMethod("times", callbackFactory.getMethod("times"));
+
+        integer.includeModule(runtime.getModule("Precision"));
+
+        integer.defineFastMethod("succ", callbackFactory.getMethod("succ"));
+        integer.defineFastMethod("next", callbackFactory.getMethod("succ"));
+        integer.defineFastMethod("chr", callbackFactory.getMethod("chr"));
+        integer.defineFastMethod("to_i", callbackFactory.getMethod("to_i"));
+        integer.defineFastMethod("to_int", callbackFactory.getMethod("to_i"));
+        integer.defineFastMethod("floor", callbackFactory.getMethod("to_i"));
+        integer.defineFastMethod("ceil", callbackFactory.getMethod("to_i"));
+        integer.defineFastMethod("round", callbackFactory.getMethod("to_i"));
+        integer.defineFastMethod("truncate", callbackFactory.getMethod("to_i"));
+
+        integer.defineFastSingletonMethod("induced_from", callbackFactory.getSingletonMethod("induced_from",
+                IRubyObject.class));
+        return integer;
+    }
+
     public RubyInteger(IRuby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
-    
+
     public RubyInteger convertToInteger() {
-    	return this;
+        return this;
     }
 
     // conversion
     protected RubyFloat toFloat() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
-    // Integer methods
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
 
-    public RubyString chr() {
-        if (getLongValue() < 0 || getLongValue() > 0xff) {
-            throw getRuntime().newRangeError(this.toString() + " out of char range");
+    /** int_int_p
+     * 
+     */
+    public IRubyObject int_p() {
+        return getRuntime().getTrue();
+    }
+
+    /** int_upto
+     * 
+     */
+    public IRubyObject upto(IRubyObject to) {
+        ThreadContext context = getRuntime().getCurrentContext();
+
+        if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
+
+            RubyFixnum toFixnum = (RubyFixnum) to;
+            long toValue = toFixnum.getLongValue();
+            for (long i = getLongValue(); i <= toValue; i++) {
+                context.yield(RubyFixnum.newFixnum(getRuntime(), i));
+            }
+        } else {
+            RubyNumeric i = this;
+
+            while (true) {
+                if (i.callMethod(context, ">", to).isTrue()) {
+                    break;
+                }
+                context.yield(i);
+                i = (RubyNumeric) i.callMethod(context, "+", RubyFixnum.one(getRuntime()));
+            }
         }
-        return getRuntime().newString(new String(new char[] { (char) getLongValue() }));
+        return this;
     }
 
+    /** int_downto
+     * 
+     */
     // TODO: Make callCoerced work in block context...then fix downto, step, and upto.
     public IRubyObject downto(IRubyObject to) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
             RubyFixnum toFixnum = (RubyFixnum) to;
             long toValue = toFixnum.getLongValue();
             for (long i = getLongValue(); i >= toValue; i--) {
                 context.yield(RubyFixnum.newFixnum(getRuntime(), i));
             }
         } else {
             RubyNumeric i = this;
 
             while (true) {
                 if (i.callMethod(context, "<", to).isTrue()) {
                     break;
                 }
                 context.yield(i);
                 i = (RubyNumeric) i.callMethod(context, "-", RubyFixnum.one(getRuntime()));
             }
         }
         return this;
     }
 
-    public RubyBoolean int_p() {
-        return getRuntime().getTrue();
-    }
-
+    /** int_dotimes
+     * 
+     */
     public IRubyObject times() {
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (this instanceof RubyFixnum) {
 
             long value = getLongValue();
             for (long i = 0; i < value; i++) {
                 context.yield(RubyFixnum.newFixnum(getRuntime(), i));
             }
         } else {
             RubyNumeric i = RubyFixnum.zero(getRuntime());
             while (true) {
                 if (!i.callMethod(context, "<", this).isTrue()) {
                     break;
                 }
                 context.yield(i);
                 i = (RubyNumeric) i.callMethod(context, "+", RubyFixnum.one(getRuntime()));
             }
         }
 
         return this;
     }
 
-    public IRubyObject next() {
+    /** int_succ
+     * 
+     */
+    public IRubyObject succ() {
         if (this instanceof RubyFixnum) {
             return RubyFixnum.newFixnum(getRuntime(), getLongValue() + 1L);
         } else {
             return callMethod(getRuntime().getCurrentContext(), "+", RubyFixnum.one(getRuntime()));
         }
     }
 
-    public IRubyObject upto(IRubyObject to) {
-        ThreadContext context = getRuntime().getCurrentContext();
-
-        if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
-
-            RubyFixnum toFixnum = (RubyFixnum) to;
-            long toValue = toFixnum.getLongValue();
-            for (long i = getLongValue(); i <= toValue; i++) {
-                context.yield(RubyFixnum.newFixnum(getRuntime(), i));
-            }
-        } else {
-            RubyNumeric i = this;
-
-            while (true) {
-                if (i.callMethod(context, ">", to).isTrue()) {
-                    break;
-                }
-                context.yield(i);
-                i = (RubyNumeric) i.callMethod(context, "+", RubyFixnum.one(getRuntime()));
-            }
+    /** int_chr
+     * 
+     */
+    public RubyString chr() {
+        if (getLongValue() < 0 || getLongValue() > 0xff) {
+            throw getRuntime().newRangeError(this.toString() + " out of char range");
         }
-        return this;
+        return getRuntime().newString(new String(new char[] { (char) getLongValue() }));
     }
 
+    /** int_to_i
+     * 
+     */
     public RubyInteger to_i() {
         return this;
     }
 
-    public RubyNumeric multiplyWith(RubyBignum value) {
-        return value.multiplyWith(this);
+    /*  ================
+     *  Singleton Methods
+     *  ================ 
+     */
+
+    /** rb_int_induced_from
+     * 
+     */
+    public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
+        if (other instanceof RubyFixnum || other instanceof RubyBignum) {
+            return other;
+        } else if (other instanceof RubyFloat) {
+            return other.callMethod(recv.getRuntime().getCurrentContext(), "to_i");
+        } else {
+            throw recv.getRuntime().newTypeError(
+                    "failed to convert " + other.getMetaClass().getName() + " into Integer");
+        }
     }
+
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 29e889548a..8e97aeda77 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1112 +1,1125 @@
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
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicMethod;
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
 
         module.defineFastModuleFunction("Array", callbackFactory.getSingletonMethod("new_array", IRubyObject.class));
         module.defineFastModuleFunction("Float", callbackFactory.getSingletonMethod("new_float", IRubyObject.class));
         module.defineFastModuleFunction("Integer", callbackFactory.getSingletonMethod("new_integer", IRubyObject.class));
         module.defineFastModuleFunction("String", callbackFactory.getSingletonMethod("new_string", IRubyObject.class));
         module.defineFastModuleFunction("`", callbackFactory.getSingletonMethod("backquote", IRubyObject.class));
         module.defineFastModuleFunction("abort", callbackFactory.getOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getSingletonMethod("autoload", IRubyObject.class, IRubyObject.class));
         module.defineFastPublicModuleFunction("autoload?", callbackFactory.getSingletonMethod("autoload_p", IRubyObject.class));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         // TODO: Implement Kernel#callcc
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRubyObject.class));
         module.defineFastModuleFunction("chomp", callbackFactory.getOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getOptSingletonMethod("exit_bang"));
         module.defineFastModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("puts", callbackFactory.getOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRubyObject.class));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRubyObject.class));
         module.defineFastModuleFunction("select", callbackFactory.getOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRubyObject.class));
         module.defineFastModuleFunction("sleep", callbackFactory.getSingletonMethod("sleep", IRubyObject.class));
         module.defineFastModuleFunction("split", callbackFactory.getOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getOptSingletonMethod("system"));
         // TODO: Implement Kernel#test (partial impl)
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getSingletonMethod("warn", IRubyObject.class));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRubyObject.class));
         
         // Object methods
-        module.defineFastPublicModuleFunction("==", objectCallbackFactory.getMethod("equal", IRubyObject.class));
-        module.defineAlias("===", "==");
+        module.defineFastPublicModuleFunction("==", objectCallbackFactory.getMethod("obj_equal", IRubyObject.class));
+        module.defineFastPublicModuleFunction("===", objectCallbackFactory.getMethod("equal", IRubyObject.class));
+
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getMethod("id"));
         module.defineAlias("__id__", "id");
         module.defineAlias("object_id", "id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getMethod("kind_of", IRubyObject.class));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getMethod("same", IRubyObject.class));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getMethod("match", IRubyObject.class));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getMethod("frozen"));
         module.defineModuleFunction("initialize_copy", objectCallbackFactory.getMethod("initialize_copy", IRubyObject.class));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getMethod("instance_of", IRubyObject.class));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getMethod("instance_variable_get", IRubyObject.class));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getMethod("instance_variable_set", IRubyObject.class, IRubyObject.class));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getMethod("method", IRubyObject.class));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRubyObject.class));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getMethod("untaint"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc());
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if(recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         IAutoloadMethod m = recv.getRuntime().getLoadService().autoloadFor(name);
         if(m == null) {
             return recv.getRuntime().getNil();
         } else {
             return recv.getRuntime().newString(m.file());
         }
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
              * @see org.jruby.runtime.load.IAutoloadMethod#load(IRuby, String)
              */
             public IRubyObject load(IRuby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
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
 
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
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
-        if(object instanceof RubyString) {
-            return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
-        } else {
-            return object.callMethod(recv.getRuntime().getCurrentContext(), "to_f");
+        if(object instanceof RubyFixnum){
+            return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
+        }else if(object instanceof RubyFloat){
+            return object;
+        }else if(object instanceof RubyBignum){
+            return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
+        }else if(object instanceof RubyString){
+            return RubyNumeric.str2fnum(recv.getRuntime(), (RubyString)object, true);
+        }else if(object.isNil()){
+            throw recv.getRuntime().newTypeError("can't convert nil into Float");
+        }else{
+            RubyFloat rFloat = object.convertToFloat();
+            if(rFloat.getDoubleValue() == Double.NaN){
+                recv.getRuntime().newTypeError("invalid value for Float()");
+            }
+            return rFloat;
         }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             String val = object.toString();
             if(val.length() > 0 && val.charAt(0) == '0') {
                 if(val.length() > 1) {
                     if(val.charAt(1) == 'x') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),16,true);
                     } else if(val.charAt(1) == 'b') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),2,true);
                     } else {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(1)),8,true);
                     }
                 }
             }
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,10,true);
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
 
         return str.format(newArgs);
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
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args));
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
         throw runtime.newNameError(message, tag);
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
         if (finalToken.indexOf("ruby") != -1 || finalToken.endsWith(".rb") || finalToken.indexOf("irb") != -1)
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
     	private File dir;
     	
     	public InProcessScript(String[] argArray, InputStream in, PrintStream out, PrintStream err, File dir) {
     		this.argArray = argArray;
     		this.in = in;
     		this.out = out;
     		this.err = err;
     		this.dir = dir;
     	}
 
 		public int getResult() {
 			return result;
 		}
 
 		public void setResult(int result) {
 			this.result = result;
 		}
 		
         public void run() {
             String oldDir = System.getProperty("user.dir");
             try {
                 System.setProperty("user.dir", dir.getAbsolutePath());
                 result = new Main(in, out, err).run(argArray);
             } finally {
                 System.setProperty("user.dir", oldDir);
             }
         }
     }
 
     public static int runInShell(IRuby runtime, IRubyObject[] rawArgs) {
         return runInShell(runtime,rawArgs,runtime.getOutputStream());
     }
 
     public static int runInShell(IRuby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             String shell = runtime.evalScript("require 'rbconfig'; Config::CONFIG['SHELL']").toString();
             rawArgs[0] = runtime.newString(repairDirSeps(rawArgs[0].toString()));
             Process aProcess = null;
             InProcessScript ipScript = null;
             File pwd = new File(runtime.evalScript("Dir.pwd").toString());
             
             if (isRubyCommand(rawArgs[0].toString())) {
                 List args = parseCommandLine(rawArgs);
                 String command = (String)args.get(0);
 
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 if(command.trim().endsWith("irb")) {
                     startIndex = 0;
                     args.set(0,runtime.getJRubyHome() + File.separator + "bin" + File.separator + "jirb");
                 }
                 String[] argArray = (String[])args.subList(startIndex,args.size()).toArray(new String[0]);
                 ipScript = new InProcessScript(argArray, input, new PrintStream(output), new PrintStream(error), pwd);
                 
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
                 aProcess = Runtime.getRuntime().exec(argArray, new String[]{}, pwd);
             } else {
                 // execute command directly, no wildcard expansion
                 if (rawArgs.length > 1) {
                     String[] argArray = new String[rawArgs.length];
                     for (int i=0;i<rawArgs.length;i++) {
                         argArray[i] = rawArgs[i].toString();
                     }
                     aProcess = Runtime.getRuntime().exec(argArray, new String[]{}, pwd);
                 } else {
                     aProcess = Runtime.getRuntime().exec(rawArgs[0].toString(), new String[]{}, pwd);
                 }
             }
             
             if (aProcess != null) {
                 handleStreams(aProcess,input,output,error);
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
     
     private static void handleStreams(Process p, InputStream in, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
         OutputStream pIn = p.getOutputStream();
 
         boolean done = false;
         int b;
         boolean proc = false;
         while(!done) {
             if(pOut.available() > 0) {
                 byte[] input = new byte[pOut.available()];
                 if((b = pOut.read(input)) == -1) {
                     done = true;
                 } else {
                     out.write(input);
                 }
                 proc = true;
             }
             if(pErr.available() > 0) {
                 byte[] input = new byte[pErr.available()];
                 if((b = pErr.read(input)) != -1) {
                     err.write(input);
                 }
                 proc = true;
             }
             if(in.available() > 0) {
                 byte[] input = new byte[in.available()];
                 if((b = in.read(input)) != -1) {
                     pIn.write(input);
                 }
                 proc = true;
             }
             if(!proc) {
                 if((b = pOut.read()) == -1) {
                     if((b = pErr.read()) == -1) {
                         done = true;
                     } else {
                         err.write(b);
                     }
                 } else {
                     out.write(b);
                 }
             }
             proc = false;
         }
         pOut.close();
         pErr.close();
         pIn.close();
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
         int resultCode = runInShell(runtime, args);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyNumeric.java b/src/org/jruby/RubyNumeric.java
index a144640547..61c22035be 100644
--- a/src/org/jruby/RubyNumeric.java
+++ b/src/org/jruby/RubyNumeric.java
@@ -1,606 +1,798 @@
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
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Antti Karanta <Antti.Karanta@napa.fi>
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
 
 import java.math.BigInteger;
+import java.util.List;
 
 import org.jruby.exceptions.RaiseException;
+import org.jruby.runtime.CallbackFactory;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Base class for all numerical types in ruby.
  */
 // TODO: Numeric.new works in Ruby and it does here too.  However trying to use
 //   that instance in a numeric operation should generate an ArgumentError. Doing
 //   this seems so pathological I do not see the need to fix this now.
 public class RubyNumeric extends RubyObject {
-    
-    public static double DBL_EPSILON=2.2204460492503131e-16;
+
+    public static RubyClass createNumericClass(IRuby runtime) {
+        RubyClass numeric = runtime.defineClass("Numeric", runtime.getObject(), NUMERIC_ALLOCATOR);
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyNumeric.class);
+        numeric.defineFastMethod("singleton_method_added", callbackFactory.getMethod("sadded",
+                IRubyObject.class));
+
+        numeric.includeModule(runtime.getModule("Comparable"));
+
+        numeric.defineFastMethod("initialize_copy", callbackFactory.getMethod("init_copy", IRubyObject.class));
+        numeric.defineFastMethod("coerce", callbackFactory.getMethod("coerce", IRubyObject.class));
+
+        numeric.defineFastMethod("+@", callbackFactory.getMethod("uplus"));
+        numeric.defineFastMethod("-@", callbackFactory.getMethod("uminus"));
+        numeric.defineFastMethod("<=>", callbackFactory.getMethod("cmp", IRubyObject.class));
+        numeric.defineFastMethod("quo", callbackFactory.getMethod("quo", IRubyObject.class));
+        numeric.defineFastMethod("eql?", callbackFactory.getMethod("eql_p", IRubyObject.class));
+        numeric.defineFastMethod("div", callbackFactory.getMethod("div", IRubyObject.class));
+        numeric.defineFastMethod("divmod", callbackFactory.getMethod("divmod", IRubyObject.class));
+        numeric.defineFastMethod("modulo", callbackFactory.getMethod("modulo", IRubyObject.class));
+        numeric.defineFastMethod("remainder", callbackFactory.getMethod("remainder", IRubyObject.class));
+        numeric.defineFastMethod("abs", callbackFactory.getMethod("abs"));
+        numeric.defineFastMethod("to_int", callbackFactory.getMethod("to_int"));
+        numeric.defineFastMethod("integer?", callbackFactory.getMethod("int_p"));
+        numeric.defineFastMethod("zero?", callbackFactory.getMethod("zero_p"));
+        numeric.defineFastMethod("nonzero?", callbackFactory.getMethod("nonzero_p"));
+        numeric.defineFastMethod("floor", callbackFactory.getMethod("floor"));
+        numeric.defineFastMethod("ceil", callbackFactory.getMethod("ceil"));
+        numeric.defineFastMethod("round", callbackFactory.getMethod("round"));
+        numeric.defineFastMethod("truncate", callbackFactory.getMethod("truncate"));
+        numeric.defineMethod("step", callbackFactory.getOptMethod("step"));
+
+        return numeric;
+    }
+
+    protected static ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            return new RubyNumeric(runtime, klass);
+        }
+    };
+
+    public static double DBL_EPSILON = 2.2204460492503131e-16;
 
     public RubyNumeric(IRuby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     // The implementations of these are all bonus (see TODO above)  I was going
     // to throw an error from these, but it appears to be the wrong place to
     // do it.
-    public double getDoubleValue() { return 0; }
-    public long getLongValue() { return 0; }
-    public RubyNumeric multiplyWith(RubyInteger value) { return value; }
-    public RubyNumeric multiplyWith(RubyFloat value) { return value; }
-    public RubyNumeric multiplyWith(RubyBignum value) { return value; }
+    public double getDoubleValue() {
+        return 0;
+    }
 
-    public long getTruncatedLongValue() {
-        return getLongValue();
+    public long getLongValue() {
+        return 0;
     }
-    
+
     public static RubyNumeric newNumeric(IRuby runtime) {
-    	return new RubyNumeric(runtime, runtime.getClass("Numeric"));
+        return new RubyNumeric(runtime, runtime.getClass("Numeric"));
+    }
+
+    /*  ================
+     *  Utility Methods
+     *  ================ 
+     */
+
+    /** rb_num2int, check_int, NUM2INT
+     * 
+     */
+    public static int num2int(IRubyObject arg) {
+        long num;
+        if (arg instanceof RubyFixnum) { // Fixnums can be bigger than int
+            num = ((RubyFixnum) arg).getLongValue();
+        } else {
+            num = num2long(arg);
+        }
+
+        String s;
+        if (num < Integer.MIN_VALUE) {
+            s = "small";
+        } else if (num > Integer.MAX_VALUE) {
+            s = "big";
+        } else {
+            return (int) num;
+        }
+        throw arg.getRuntime().newRangeError("integer " + num + " too " + s + " to convert to `int'");
     }
 
-    // TODO: Find all consumers and convert to correct conversion protocol
+    // TODO: Find all consumers and convert to correct conversion protocol <- done
+    /** rb_num2long and FIX2LONG (numeric.c)
+     * 
+     */
     public static long num2long(IRubyObject arg) {
-        if (arg instanceof RubyNumeric) {
-            return ((RubyNumeric) arg).getLongValue();
+        if (arg instanceof RubyFixnum) {
+            return ((RubyFixnum) arg).getLongValue();
+        }
+        if (arg.isNil()) {
+            throw arg.getRuntime().newTypeError("no implicit conversion from nil to integer");
+        }
+
+        if (arg instanceof RubyFloat) {
+            double aFloat = ((RubyFloat) arg).getDoubleValue();
+            if (aFloat <= (double) Long.MAX_VALUE && aFloat >= (double) Long.MIN_VALUE) {
+                return (long) aFloat;
+            } else {
+                // TODO: number formatting here, MRI uses "%-.10g", 1.4 API is a must?
+                throw arg.getRuntime().newTypeError("float " + aFloat + "out of range of integer");
+            }
+        } else if (arg instanceof RubyBignum) {
+            return RubyBignum.big2long((RubyBignum) arg);
+        }
+        return arg.convertToInteger().getLongValue();
+    }
+
+    /** rb_dbl2big + LONG2FIX at once (numeric.c)
+     * 
+     */
+    public static IRubyObject dbl2num(IRuby runtime, double val) {
+        if (Double.isInfinite(val)) {
+            throw runtime.newFloatDomainError(val < 0 ? "-Infinity" : "Infinity");
         }
-        throw arg.getRuntime().newTypeError("argument is not numeric");
+        if (Double.isNaN(val)) {
+            throw runtime.newFloatDomainError("NaN");
+        }
+
+        if (val > (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
+            return RubyBignum.newBignum(runtime, val);
+        }
+        return RubyFixnum.newFixnum(runtime, (long) val);
+    }
+
+    /** rb_num2dbl and NUM2DBL
+     * 
+     */
+    public static double num2dbl(IRubyObject arg) {
+        if (arg instanceof RubyFloat) {
+            return ((RubyFloat) arg).getDoubleValue();
+        } else if (arg instanceof RubyString) {
+            throw arg.getRuntime().newTypeError("no implicit conversion to float from string");
+        } else if (arg == arg.getRuntime().getNil()) {
+            throw arg.getRuntime().newTypeError("no implicit conversion to float from nil");
+        }
+        return arg.convertToFloat().getDoubleValue();
+    }
+
+    /** rb_dbl_cmp (numeric.c)
+     * 
+     */
+    public static IRubyObject dbl_cmp(IRuby runtime, double a, double b) {
+        if (Double.isNaN(a) || Double.isNaN(b)) {
+            return runtime.getNil();
+        }
+        if (a > b) {
+            return RubyFixnum.one(runtime);
+        }
+        if (a < b) {
+            return RubyFixnum.minus_one(runtime);
+        }
+        return RubyFixnum.zero(runtime);
     }
 
     public static long fix2long(IRubyObject arg) {
         if (arg instanceof RubyFixnum) {
             return ((RubyFixnum) arg).getLongValue();
         }
         throw arg.getRuntime().newTypeError("argument is not a Fixnum");
     }
 
     public static int fix2int(IRubyObject arg) {
         long val = fix2long(arg);
         if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
             throw arg.getRuntime().newTypeError("argument value is too big to convert to int");
         }
         return (int) val;
     }
 
     public static RubyInteger str2inum(IRuby runtime, RubyString str, int base) {
-        return str2inum(runtime,str,base,false);
+        return str2inum(runtime, str, base, false);
     }
 
     public static RubyNumeric int2fix(IRuby runtime, long val) {
-        return RubyFixnum.newFixnum(runtime,val);
+        return RubyFixnum.newFixnum(runtime, val);
+    }
+
+    /** rb_num2fix
+     * 
+     */
+    public static IRubyObject num2fix(IRubyObject val) {
+        if (val instanceof RubyFixnum) {
+            return val;
+        }
+        if (val instanceof RubyBignum) {
+            // any BigInteger is bigger than Fixnum and we don't have FIXABLE
+            throw val.getRuntime().newRangeError("integer " + val + " out of range of fixnum");
+        }
+        return RubyFixnum.newFixnum(val.getRuntime(), num2long(val));
     }
 
     /**
      * Converts a string representation of an integer to the integer value. 
      * Parsing starts at the beginning of the string (after leading and 
      * trailing whitespace have been removed), and stops at the end or at the 
      * first character that can't be part of an integer.  Leading signs are
      * allowed. If <code>base</code> is zero, strings that begin with '0[xX]',
      * '0[bB]', or '0' (optionally preceded by a sign) will be treated as hex, 
      * binary, or octal numbers, respectively.  If a non-zero base is given, 
      * only the prefix (if any) that is appropriate to that base will be 
      * parsed correctly.  For example, if the base is zero or 16, the string
      * "0xff" will be converted to 256, but if the base is 10, it will come out 
      * as zero, since 'x' is not a valid decimal digit.  If the string fails 
      * to parse as a number, zero is returned.
      * 
      * @param runtime  the ruby runtime
      * @param str   the string to be converted
      * @param base  the expected base of the number (2, 8, 10 or 16), or 0 
      *              if the method should determine the base automatically 
      *              (defaults to 10).
      * @param raise if the string is not a valid integer, raise error, otherwise return 0
      * @return  a RubyFixnum or (if necessary) a RubyBignum representing 
      *          the result of the conversion, which will be zero if the 
      *          conversion failed.
      */
     public static RubyInteger str2inum(IRuby runtime, RubyString str, int base, boolean raise) {
         StringBuffer sbuf = new StringBuffer(str.toString().trim());
         if (sbuf.length() == 0) {
-            if(raise) {
-                throw runtime.newArgumentError("invalid value for Integer: " + str.callMethod(runtime.getCurrentContext(),"inspect").toString());
+            if (raise) {
+                throw runtime.newArgumentError("invalid value for Integer: "
+                        + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return RubyFixnum.zero(runtime);
         }
         int pos = 0;
         int radix = (base != 0) ? base : 10;
         boolean digitsFound = false;
         if (sbuf.charAt(pos) == '-') {
             pos++;
         } else if (sbuf.charAt(pos) == '+') {
             sbuf.deleteCharAt(pos);
         }
         if (pos == sbuf.length()) {
-            if(raise) {
-                throw runtime.newArgumentError("invalid value for Integer: " + str.callMethod(runtime.getCurrentContext(),"inspect").toString());
+            if (raise) {
+                throw runtime.newArgumentError("invalid value for Integer: "
+                        + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return RubyFixnum.zero(runtime);
         }
         if (sbuf.charAt(pos) == '0') {
             sbuf.deleteCharAt(pos);
             if (pos == sbuf.length()) {
                 return RubyFixnum.zero(runtime);
             }
             if (sbuf.charAt(pos) == 'x' || sbuf.charAt(pos) == 'X') {
                 if (base == 0 || base == 16) {
                     radix = 16;
                     sbuf.deleteCharAt(pos);
                 }
             } else if (sbuf.charAt(pos) == 'b' || sbuf.charAt(pos) == 'B') {
                 if (base == 0 || base == 2) {
                     radix = 2;
                     sbuf.deleteCharAt(pos);
                 }
             } else {
                 radix = (base == 0) ? 8 : base;
             }
         }
         while (pos < sbuf.length()) {
             if (sbuf.charAt(pos) == '_') {
                 sbuf.deleteCharAt(pos);
             } else if (Character.digit(sbuf.charAt(pos), radix) != -1) {
                 digitsFound = true;
                 pos++;
             } else {
                 break;
             }
         }
         if (!digitsFound) {
-            if(raise) {
-                throw runtime.newArgumentError("invalid value for Integer: " + str.callMethod(runtime.getCurrentContext(),"inspect").toString());
+            if (raise) {
+                throw runtime.newArgumentError("invalid value for Integer: "
+                        + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return RubyFixnum.zero(runtime);
         }
         try {
             long l = Long.parseLong(sbuf.substring(0, pos), radix);
             return runtime.newFixnum(l);
         } catch (NumberFormatException ex) {
             try {
                 BigInteger bi = new BigInteger(sbuf.substring(0, pos), radix);
                 return new RubyBignum(runtime, bi);
-            } catch(NumberFormatException e) {
-                if(raise) {
-                    throw runtime.newArgumentError("invalid value for Integer: " + str.callMethod(runtime.getCurrentContext(),"inspect").toString());
+            } catch (NumberFormatException e) {
+                if (raise) {
+                    throw runtime.newArgumentError("invalid value for Integer: "
+                            + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
                 }
                 return RubyFixnum.zero(runtime);
             }
         }
     }
 
     public static RubyFloat str2fnum(IRuby runtime, RubyString arg) {
-        return str2fnum(runtime,arg,false);
+        return str2fnum(runtime, arg, false);
     }
 
     /**
      * Converts a string representation of a floating-point number to the 
      * numeric value.  Parsing starts at the beginning of the string (after 
      * leading and trailing whitespace have been removed), and stops at the 
      * end or at the first character that can't be part of a number.  If 
      * the string fails to parse as a number, 0.0 is returned.
      * 
      * @param runtime  the ruby runtime
      * @param arg   the string to be converted
      * @param raise if the string is not a valid float, raise error, otherwise return 0.0
      * @return  a RubyFloat representing the result of the conversion, which
      *          will be 0.0 if the conversion failed.
      */
     public static RubyFloat str2fnum(IRuby runtime, RubyString arg, boolean raise) {
         String str = arg.toString().trim();
         double d = 0.0;
         int pos = str.length();
         for (int i = 0; i < pos; i++) {
             if ("0123456789eE+-.".indexOf(str.charAt(i)) == -1) {
-                if(raise) {
-                    throw runtime.newArgumentError("invalid value for Float(): " + arg.callMethod(runtime.getCurrentContext(),"inspect").toString());
+                if (raise) {
+                    throw runtime.newArgumentError("invalid value for Float(): "
+                            + arg.callMethod(runtime.getCurrentContext(), "inspect").toString());
                 }
                 pos = i + 1;
                 break;
             }
         }
         for (; pos > 0; pos--) {
             try {
                 d = Double.parseDouble(str.substring(0, pos));
             } catch (NumberFormatException ex) {
-                if(raise) {
-                    throw runtime.newArgumentError("invalid value for Float(): " + arg.callMethod(runtime.getCurrentContext(),"inspect").toString());
+                if (raise) {
+                    throw runtime.newArgumentError("invalid value for Float(): "
+                            + arg.callMethod(runtime.getCurrentContext(), "inspect").toString());
                 }
                 continue;
             }
             break;
         }
         return new RubyFloat(runtime, d);
     }
 
-    /* Numeric methods. (num_*)
+    /** Numeric methods. (num_*)
      *
      */
-    
     protected IRubyObject[] getCoerced(IRubyObject other, boolean error) {
         IRubyObject result;
-        
+
         try {
             result = other.callMethod(getRuntime().getCurrentContext(), "coerce", this);
         } catch (RaiseException e) {
             if (error) {
-                throw getRuntime().newTypeError(other.getMetaClass().getName() + 
-                        " can't be coerced into " + getMetaClass().getName());
+                throw getRuntime().newTypeError(
+                        other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
-             
             return null;
         }
-        
-        if (!(result instanceof RubyArray) || ((RubyArray)result).getLength() != 2) {
+
+        if (!(result instanceof RubyArray) || ((RubyArray) result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
-        
-        return ((RubyArray)result).toJavaArray();
+
+        return ((RubyArray) result).toJavaArray();
+    }
+
+    protected IRubyObject callCoerced(String method, IRubyObject other, boolean err) {
+        IRubyObject[] args = getCoerced(other, err);
+        return args[0].callMethod(getRuntime().getCurrentContext(), method, args[1]);
     }
 
     protected IRubyObject callCoerced(String method, IRubyObject other) {
         IRubyObject[] args = getCoerced(other, true);
         return args[0].callMethod(getRuntime().getCurrentContext(), method, args[1]);
     }
-    
+
+    // beneath are rewritten coercions that reflect MRI logic, the aboves are used only by RubyBigDecimal
+
+    /** coerce_body
+     * 
+     */
+    protected final IRubyObject coerceBody(IRubyObject other) {
+        return other.callMethod(getRuntime().getCurrentContext(), "coerce", this);
+    }
+
+    /** do_coerce
+     * 
+     */
+    protected final List doCoerce(IRubyObject other, boolean err) {
+        IRubyObject result;
+        try {
+            result = coerceBody(other);
+        } catch (RaiseException e) {
+            if (err) {
+                throw getRuntime().newTypeError(
+                        other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
+            }
+            return null;
+        }
+
+        if (!(result instanceof RubyArray) || ((RubyArray) result).getLength() != 2) {
+            throw getRuntime().newTypeError("coerce must return [x, y]");
+        }
+        return ((RubyArray) result).getList();
+    }
+
+    /** rb_num_coerce_bin
+     *  coercion taking two arguments
+     */
+    protected final IRubyObject coerceBin(String method, IRubyObject other) {
+        List list = doCoerce(other, true);
+        return ((RubyObject) list.get(0))
+                .callMethod(getRuntime().getCurrentContext(), method, (RubyObject) list.get(1));
+    }
+
+    /** rb_num_coerce_cmp
+     *  coercion used for comparisons
+     */
+    protected final IRubyObject coerceCmp(String method, IRubyObject other) {
+        List list = doCoerce(other, false);
+        if (list == null) {
+            return getRuntime().getNil(); // MRI does it!
+        }
+        return ((RubyObject) list.get(0))
+                .callMethod(getRuntime().getCurrentContext(), method, (RubyObject) list.get(1));
+    }
+
+    /** rb_num_coerce_relop
+     *  coercion used for relative operators
+     */
+    protected final IRubyObject coerceRelOp(String method, IRubyObject other) {
+        List list = doCoerce(other, false);
+        if (list != null) {
+            IRubyObject result = ((RubyObject) list.get(0)).callMethod(getRuntime().getCurrentContext(), method,
+                    (RubyObject) list.get(1));
+            if (!result.isNil()) {
+                return result;
+            }
+        }
+
+        RubyComparable.cmperr(this, other); // MRI also does it that way       
+        return null; // not reachd as in MRI
+    }
+
     public RubyNumeric asNumeric() {
-    	return this;
+        return this;
+    }
+
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
+
+    /** num_sadded
+     *
+     */
+    public IRubyObject sadded(IRubyObject name) {
+        throw getRuntime().newTypeError("can't define singleton method " + name + " for " + getMetaClass().getName());
+    }
+
+    /** num_init_copy
+     *
+     */
+    public IRubyObject init_copy(IRubyObject arg) {
+        throw getRuntime().newTypeError("can't copy " + getMetaClass().getName());
     }
 
     /** num_coerce
      *
      */
     public IRubyObject coerce(IRubyObject other) {
         if (getMetaClass() == other.getMetaClass()) {
             return getRuntime().newArray(other, this);
-        } 
+        }
 
         return getRuntime().newArray(other.convertToFloat(), convertToFloat());
     }
-    
-    public IRubyObject to_int() {
-        return callMethod(getRuntime().getCurrentContext(), "to_i", IRubyObject.NULL_ARRAY);
-    }
 
-    /** num_clone
+    /** num_uplus
      *
      */
-    public IRubyObject rbClone() {
+    public IRubyObject uplus() {
         return this;
     }
-    
-    public IRubyObject op_ge(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newBoolean(compareValue((RubyNumeric) other) >= 0);
-        } 
-        
-        return RubyComparable.op_ge(this, other);
-    }
-    
-    public IRubyObject op_gt(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newBoolean(compareValue((RubyNumeric) other) > 0);
-        } 
-        
-        return RubyComparable.op_gt(this, other);
-    }
-
-    public IRubyObject op_le(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newBoolean(compareValue((RubyNumeric) other) <= 0);
-        } 
-        
-        return RubyComparable.op_le(this, other);
-    }
-    
-    public IRubyObject op_lt(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newBoolean(compareValue((RubyNumeric) other) < 0);
-        } 
-        
-        return RubyComparable.op_lt(this, other);
+
+    /** num_uminus
+     *
+     */
+    public IRubyObject uminus() {
+        RubyFixnum zero = RubyFixnum.zero(getRuntime());
+        List list = zero.doCoerce(this, true);
+        return ((RubyObject) list.get(0)).callMethod(getRuntime().getCurrentContext(), "-", (RubyObject) list.get(1));
     }
 
-    /** num_uplus
+    /** num_cmp
      *
      */
-    public IRubyObject op_uplus() {
-        return this;
+    public IRubyObject cmp(IRubyObject other) {
+        if (this == other) { // won't hurt fixnums
+            return RubyFixnum.zero(getRuntime());
+        }
+        return getRuntime().getNil();
     }
 
-    /** num_uminus
+    /** num_eql
      *
      */
-    public IRubyObject op_uminus() {
-        RubyArray coerce = (RubyArray) coerce(RubyFixnum.zero(getRuntime()));
+    public IRubyObject eql_p(IRubyObject other) {
+        if (getMetaClass() != other.getMetaClass()) {
+            return getRuntime().getFalse();
+        }
+        return super.equal(other);
+    }
 
-        return (RubyNumeric) coerce.entry(0).callMethod(getRuntime().getCurrentContext(), "-", coerce.entry(1));
+    /** num_quo
+     *
+     */
+    public IRubyObject quo(IRubyObject other) {
+        return callMethod(getRuntime().getCurrentContext(), "/", other);
     }
-    
-    public IRubyObject cmp(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newFixnum(compareValue((RubyNumeric) other));
-        }
 
-        return other.respondsTo("to_int") ? callCoerced("<=>", other) : getRuntime().getNil();
+    /** num_div
+     * 
+     */
+    public IRubyObject div(IRubyObject other) {
+        return callMethod(getRuntime().getCurrentContext(), "/", other).convertToFloat().floor();
     }
 
+    /** num_divmod
+     * 
+     */
     public IRubyObject divmod(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-    	    RubyNumeric denominator = (RubyNumeric) other;
-            RubyNumeric div = (RubyNumeric) callMethod(getRuntime().getCurrentContext(), "/", denominator);
-            if (div instanceof RubyFloat) {
-                double d = Math.floor(((RubyFloat) div).getValue());
-                if (((RubyFloat) div).getValue() > d) {
-                    div = RubyFloat.newFloat(getRuntime(), d);
-                }
-            }
-            
-            return getRuntime().newArray(div, modulo(denominator));
-    	}
+        IRubyObject cdr = callMethod(getRuntime().getCurrentContext(), "%", other);
+        IRubyObject car = div(other);
+        return RubyArray.newArray(getRuntime(), car, cdr);
 
-    	return callCoerced("divmod", other);
     }
 
     /** num_modulo
      *
      */
     public IRubyObject modulo(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            return (RubyNumeric) callMethod(getRuntime().getCurrentContext(), "%", other);
-    	}
-    	
-    	return callCoerced("modulo", other);
+        return callMethod(getRuntime().getCurrentContext(), "%", other);
     }
 
     /** num_remainder
      *
      */
-    public IRubyObject remainder(IRubyObject other) {
-    	if (other instanceof RubyNumeric) {
-            IRubyObject mod = modulo(other);
-            final RubyNumeric zero = RubyFixnum.zero(getRuntime());
-            ThreadContext context = getRuntime().getCurrentContext();
+    public IRubyObject remainder(IRubyObject y) {
+        ThreadContext context = getRuntime().getCurrentContext();
+        IRubyObject z = callMethod(context, "%", y);
+        IRubyObject x = this;
+        RubyFixnum zero = RubyFixnum.zero(getRuntime());
+
+        if (!(((RubyNumeric)z).equal(zero).isTrue())
+            && ((x.callMethod(context, "<", zero)).isTrue()
+                && (y.callMethod(context, ">", zero)).isTrue()) 
+            || ((x.callMethod(context, ">", zero)).isTrue()
+                && (y.callMethod(context, "<", zero)).isTrue())) {
+            return z.callMethod(context, "-",y);
+        }
 
-            if (callMethod(context, "<", zero).isTrue() && other.callMethod(context, ">", zero).isTrue() || 
-                callMethod(context, ">", zero).isTrue() && other.callMethod(context, "<", zero).isTrue()) {
+        return z;
+    }
 
-                return (RubyNumeric) mod.callMethod(context, "-", other);
-            }
+    /** num_abs
+     *
+     */
+    public IRubyObject abs() {
+        ThreadContext context = getRuntime().getCurrentContext();
+        if (callMethod(context, "<", RubyFixnum.zero(getRuntime())).isTrue()) {
+            return (RubyNumeric) callMethod(context, "-@");
+        }
+        return this;
+    }
 
-            return mod;
-    	}
-    	
-    	return callCoerced("remainder", other);
+    /** num_to_int
+     * 
+     */
+    public IRubyObject to_int() {
+        return callMethod(getRuntime().getCurrentContext(), "to_i", IRubyObject.NULL_ARRAY);
     }
 
-    protected int compareValue(RubyNumeric other) {
-        return -1;
+    /** num_int_p
+     *
+     */
+    public IRubyObject int_p() {
+        return getRuntime().getFalse();
     }
-    
-    /** num_equal
+
+    /** num_zero_p
      *
      */
-    public RubyBoolean veryEqual(IRubyObject other) {
-    	IRubyObject truth = super.equal(other);
-    	
-    	return truth == getRuntime().getNil() ? getRuntime().getFalse() : 
-    		(RubyBoolean) truth;
+    public IRubyObject zero_p() {
+        // Will always return a boolean
+        return equal(RubyFixnum.zero(getRuntime())).isTrue() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
-    
-    /** num_equal
+
+    /** num_nonzero_p
      *
      */
-    public IRubyObject equal(IRubyObject other) {
-        if (other instanceof RubyNumeric) {
-            return getRuntime().newBoolean(compareValue((RubyNumeric) other) == 0);
+    public IRubyObject nonzero_p() {
+        if (callMethod(getRuntime().getCurrentContext(), "zero?").isTrue()) {
+            return getRuntime().getNil();
         }
-        return super.equal(other); // +++ rb_equal
+        return this;
     }
 
-    /** num_eql
+    /** num_floor
      *
      */
-    public RubyBoolean eql(IRubyObject other) {
-        // Two numbers of the same value, but different types are not
-        // 'eql?'.
-        if (getMetaClass() != other.getMetaClass()) {
-            return getRuntime().getFalse();
-        }
-        
-        // However, if they are the same type, then we try a regular
-        // equal as a float with 1.0 may be two different ruby objects.
-        // It will always return a boolean
-        return (RubyBoolean) equal(other);
+    public IRubyObject floor() {
+        return convertToFloat().floor();
     }
 
-    /** num_abs
+    /** num_ceil
      *
      */
-    public RubyNumeric abs() {
-        ThreadContext context = getRuntime().getCurrentContext();
-        if (callMethod(context, "<", RubyFixnum.zero(getRuntime())).isTrue()) {
-            return (RubyNumeric) callMethod(context, "-@");
-        }
-		return this;
+    public IRubyObject ceil() {
+        return convertToFloat().ceil();
+    }
+
+    /** num_round
+     *
+     */
+    public IRubyObject round() {
+        return convertToFloat().round();
+    }
+
+    /** num_truncate
+     *
+     */
+    public IRubyObject truncate() {
+        return convertToFloat().truncate();
     }
-    
-    public IRubyObject step(IRubyObject[]args) {
-        checkArgumentCount(args, 1, 2);
-        
-        IRubyObject to = args[0];
+
+    /** num_step
+     * 
+     */
+    public IRubyObject step(IRubyObject[] args) {
+        IRubyObject to;
         IRubyObject step;
-        
-        if(args.length == 1){ 
+
+        if (args.length == 1) {
+            to = args[0];
             step = RubyFixnum.one(getRuntime());
-        }else{
+        } else if (args.length == 2) {
+            to = args[0];
             step = args[1];
+        } else {
+            throw getRuntime().newTypeError("wrong number of arguments");
         }
-        
         ThreadContext context = getRuntime().getCurrentContext();
         if (this instanceof RubyFixnum && to instanceof RubyFixnum && step instanceof RubyFixnum) {
             long value = getLongValue();
             long end = ((RubyFixnum) to).getLongValue();
             long diff = ((RubyFixnum) step).getLongValue();
 
             if (diff == 0) {
                 throw getRuntime().newArgumentError("step cannot be 0");
             }
             if (diff > 0) {
                 for (long i = value; i <= end; i += diff) {
                     context.yield(RubyFixnum.newFixnum(getRuntime(), i));
                 }
             } else {
                 for (long i = value; i >= end; i += diff) {
                     context.yield(RubyFixnum.newFixnum(getRuntime(), i));
                 }
             }
-        } else if (this instanceof RubyFloat && to instanceof RubyFloat && step instanceof RubyFloat) {
-            double beg = ((RubyFloat) this).getDoubleValue();
-            double end = ((RubyFloat) to).getDoubleValue();
-            double unit = ((RubyFloat) step).getDoubleValue();
+        } else if (this instanceof RubyFloat || to instanceof RubyFloat || step instanceof RubyFloat) {
+            double beg = num2dbl(this);
+            double end = num2dbl(to);
+            double unit = num2dbl(step);
 
             if (unit == 0) {
                 throw getRuntime().newArgumentError("step cannot be 0");
-            }           
-            
-            final double epsilon = DBL_EPSILON;
-            double n = (end - beg)/unit;
-            double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end-beg)) / Math.abs(unit) * epsilon;
-            
-            if (err>0.5) {
-                err=0.5;            
+            }
+
+            double n = (end - beg) / unit;
+            double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
+
+            if (err > 0.5) {
+                err = 0.5;
             }
             n = Math.floor(n + err) + 1;
-            
-            for(double i = 0; i < n; i++){
+
+            for (double i = 0; i < n; i++) {
                 context.yield(RubyFloat.newFloat(getRuntime(), i * unit + beg));
             }
 
         } else {
-            RubyNumeric test = (RubyNumeric) to;
             RubyNumeric i = this;
-            
-            if(step instanceof RubyFloat){
-                if (((RubyFloat)step).getDoubleValue() == 0){   
-                    throw getRuntime().newArgumentError("step cannot be 0");
-                }
-            } else {
-                if (((RubyNumeric) step).getLongValue() == 0) {
-                    throw getRuntime().newArgumentError("step cannot be 0");
-                }
-            }
 
-            String cmp = "<";
-            if (((RubyBoolean) step.callMethod(getRuntime().getCurrentContext(), "<", getRuntime()
-                    .newFixnum(0))).isFalse()) {
+            String cmp;
+            if (((RubyBoolean) step.callMethod(context, ">", RubyFixnum.zero(getRuntime()))).isTrue()) {
                 cmp = ">";
+            } else {
+                cmp = "<";
             }
 
             while (true) {
-                if (i.callMethod(context, cmp, test).isTrue()) {
+                if (i.callMethod(context, cmp, to).isTrue()) {
                     break;
                 }
                 context.yield(i);
                 i = (RubyNumeric) i.callMethod(context, "+", step);
             }
         }
         return this;
     }
-    
 
-    /** num_int_p
-     *
-     */
-    public RubyBoolean int_p() {
-        return getRuntime().getFalse();
-    }
-
-    /** num_zero_p
-     *
-     */
-    public RubyBoolean zero_p() {
-    	// Will always return a boolean
-        return (RubyBoolean) equal(RubyFixnum.zero(getRuntime()));
-    }
-
-    /** num_nonzero_p
+    /** num_clone
      *
      */
-    public IRubyObject nonzero_p() {
-        if (callMethod(getRuntime().getCurrentContext(), "zero?").isTrue()) {
-            return getRuntime().getNil();
-        }
+    public IRubyObject rbClone() {
         return this;
     }
 
-    /** num_floor
-     *
-     */
-    public RubyInteger floor() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue()).floor();
-    }
-
-    /** num_ceil
-     *
-     */
-    public RubyInteger ceil() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue()).ceil();
-    }
-
-    /** num_round
-     *
-     */
-    public RubyInteger round() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue()).round();
-    }
-
-    /** num_truncate
+    //    /** num_equal
+    //     *
+    //     */
+    //    public RubyBoolean veryEqual(IRubyObject other) {
+    //        IRubyObject truth = super.equal(other);
+    //
+    //        return truth == getRuntime().getNil() ? getRuntime().getFalse() : (RubyBoolean) truth;
+    //    }
+    //
+    /** num_equal
      *
      */
-    public RubyInteger truncate() {
-        return RubyFloat.newFloat(getRuntime(), getDoubleValue()).truncate();
-    }
+    public IRubyObject equal(IRubyObject other) {
+        if (this == other) { // it won't hurt fixnums
+            return getRuntime().getTrue();
+        }
 
-    public RubyNumeric multiplyWith(RubyFixnum value) {
-        return multiplyWith((RubyInteger) value);
+        return other.callMethod(getRuntime().getCurrentContext(), "==", this);
     }
 
-    public RubyFloat convertToFloat() {
-        return getRuntime().newFloat(getDoubleValue());
+    public boolean singletonMethodsAllowed() {
+        return false;
     }
-
-	public boolean singletonMethodsAllowed() {
-		return false;
-	}
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 1a04e12f41..04f7e1fb7d 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1282 +1,1298 @@
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
     public IRubyObject callMethod(ThreadContext context, byte methodIndex, String name,
             IRubyObject[] args, CallType callType) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType);
         } else {
             return callMethod(context, module, name, args, callType);
         }
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, name, args, callType);
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
 
     protected String trueFalseNil(String v) {
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
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
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
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
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
-    public IRubyObject equal(IRubyObject obj) {
-        if (isNil()) {
-            return getRuntime().newBoolean(obj.isNil());
-        }
-        return getRuntime().newBoolean(this == obj);
+    public IRubyObject obj_equal(IRubyObject obj) {
+        return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
+//        if (isNil()) {
+//            return getRuntime().newBoolean(obj.isNil());
+//        }
+//        return getRuntime().newBoolean(this == obj);
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
+        if (isImmediate()) { // rb_special_const_p(obj) equivalent
+            getRuntime().newTypeError("can't clone " + getMetaClass().getName());
+        }
+        
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
                     if(IdUtil.isInstanceVariable(name)) {
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
+
+    /** rb_equal
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        if(this == other || callMethod(getRuntime().getCurrentContext(), "==",other).isTrue()){
+            return getRuntime().getTrue();
+        }
+ 
+        return getRuntime().getFalse();
+    }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java b/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
index 1ef56debc5..75f84565ac 100644
--- a/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/FixnumMetaClass.java
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
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class FixnumMetaClass extends IntegerMetaClass {
 	public FixnumMetaClass(IRuby runtime) {
 		super("Fixnum", RubyFixnum.class, runtime.getClass("Integer"), FIXNUM_ALLOCATOR);
         this.index = ClassIndex.FIXNUM;
 	}
 	
 	public FixnumMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
 		super(name, RubyFixnum.class, superClass, allocator, parentCRef);
         this.index = ClassIndex.FIXNUM;
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
         return new FixnumMetaClass(name, this, FIXNUM_ALLOCATOR, parentCRef);
 	}
 
-    public RubyInteger induced_from(IRubyObject number) {
+    public IRubyObject induced_from(IRubyObject number) {
     	// TODO: Remove once asNumeric in RubyObject tries to convert
         if (number instanceof RubySymbol) {
             return (RubyInteger) number.callMethod(getRuntime().getCurrentContext(), "to_i");
         } 
 
         return ((IntegerMetaClass) getRuntime().getClass("Integer")).induced_from(number);
     }
 
     private static ObjectAllocator FIXNUM_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(IRuby runtime, RubyClass klass) {
             RubyFixnum instance = runtime.newFixnum(0);
 
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 }
diff --git a/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java b/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
index 9a5882d497..c5e34fea0c 100644
--- a/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/IntegerMetaClass.java
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
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class IntegerMetaClass extends NumericMetaClass {
 	public IntegerMetaClass(IRuby runtime) {
         super("Integer", RubyInteger.class, runtime.getClass("Numeric"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 	}
 	
 	public IntegerMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         super(name, RubyInteger.class, superClass, allocator, parentCRef);
     }
 	
     public IntegerMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
     	super(name, clazz, superClass, allocator);
     }
 
     public IntegerMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
     	super(name, clazz, superClass, allocator, parentCRef);
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
         return new IntegerMetaClass(name, this, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, parentCRef);
 	}
 	
-    public RubyInteger induced_from(IRubyObject number) {
+    public IRubyObject induced_from(IRubyObject number) {
         if (number instanceof RubyFixnum) {
             return (RubyFixnum) number;
         } else if (number instanceof RubyFloat) {
-            return ((RubyFloat) number).to_i();
+            return ((RubyFloat) number).to_int();
         } else if (number instanceof RubyBignum) {
             return getRuntime().newFixnum(((RubyBignum) number).getLongValue());
         } else {
             throw getRuntime().newTypeError("failed to convert " + number.getMetaClass() + 
                 " into Integer");
         }
     }
 }
diff --git a/test/org/jruby/test/BaseMockRuby.java b/test/org/jruby/test/BaseMockRuby.java
index 66778968fa..103de3f8cb 100644
--- a/test/org/jruby/test/BaseMockRuby.java
+++ b/test/org/jruby/test/BaseMockRuby.java
@@ -1,731 +1,735 @@
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
 import org.jruby.runtime.MethodSelectorTable;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 
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
 
 	public RubyFileStat newRubyFileStat(String file) {
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
 
 	public JRubyClassLoader getJRubyClassLoader() {
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
 
+    public RaiseException newFloatDomainError(String message) {
+        throw new MockException();
+    }
+
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
 
     public Profile getProfile() {
         return null;
     }
     
     public Map getRuntimeInformation() {
         return null;
     }
     
     public String getJRubyHome() {
         return null;
     }
     
     public MethodSelectorTable getSelectorTable() {
         return null;
     }
     
     public KCode getKCode() {
         return null;
     }
     
     public void setKCode(KCode kcode) {
         return;
     }
 }
diff --git a/test/testBignum.rb b/test/testBignum.rb
new file mode 100644
index 0000000000..bc8cfbc0b3
--- /dev/null
+++ b/test/testBignum.rb
@@ -0,0 +1,32 @@
+require 'test/minirunit'
+test_check "Test bignums:"
+
+# others tested in FixnumBignumAutoconversion test
+
+Inf = 1/0.0
+big = 12 ** 56
+big2 = 341 ** 43
+test_equal(big.class,Bignum)
+
+test_exception(ArgumentError){big.to_s(-1)}
+test_exception(ArgumentError){big.to_s(37)}
+test_exception(TypeError){big.coerce(0.0)}
+
+test_equal((100**50/2.0).class,Float)
+test_equal((100**50+2.0).class,Float)
+test_equal((100**50-2.0).class,Float)
+test_equal((100**50*2.0).class,Float)
+test_equal((100**50%2.0).class,Float)
+
+test_exception(FloatDomainError){big.divmod(0.0)}
+test_exception(ZeroDivisionError){big.divmod(0)}
+test_exception(ZeroDivisionError){big.remainder(0)}
+test_exception(ZeroDivisionError){big.modulo(0)}
+
+test_equal(big2[0],1)
+test_equal(big2[1],0)
+test_equal(big[-1],0)
+test_equal(-big2[0],-1)
+test_equal(-big2[1],0)
+
+# more to come
diff --git a/test/testFixnum.rb b/test/testFixnum.rb
new file mode 100644
index 0000000000..69209ce58d
--- /dev/null
+++ b/test/testFixnum.rb
@@ -0,0 +1,60 @@
+require 'test/minirunit'
+test_check "Test fixnums:"
+
+test_equal(Fixnum,(Fixnum.induced_from 2).class)
+test_equal(0,(Fixnum.induced_from 0.9))
+
+test_no_exception{10.to_sym}
+
+test_exception(ArgumentError){10.to_s(-1)}
+test_exception(ArgumentError){10.to_s(37)}
+test_equal("1010",10.to_s(2))
+test_exception(RangeError){10.to_s 100**100}
+
+test_equal((10.div 4),2)
+test_equal((10.div 4.0).class,Fixnum) # awesome
+test_equal((10 / 4.0),2.5)
+test_equal((10 / 4).class,Fixnum)
+
+test_equal(10.modulo(4).class,Fixnum)
+test_equal(10.modulo(4.0).class,Float)
+
+test_equal(1.divmod(2),[0,1])
+
+test_equal((2 ** 100).class,Bignum)
+test_equal((2 ** 100.0).class,Float)
+
+test_equal(1==1.0,true)
+
+test_equal(2 <=> 1,1)
+test_equal(1 <=> 2,-1)
+test_equal(2 <=> 2,0)
+test_equal(1 <=> 1/0.0,-1)
+test_equal(1 <=> -1/0.0,1)
+test_equal(1 <=> 0/0.0,nil)
+test_equal(1 <=> 2,-1)
+
+test_equal(1.0.eql?(1),false)
+
+test_equal(1234&4321,192)
+big = 123**123
+test_equal(1234&big,1090)
+test_equal((1234&big).class,Fixnum)
+test_equal((1234|big).class,Bignum)
+test_equal((1234^big).class,Bignum)
+
+test_equal(1234[0],0)
+test_equal(1234[1],1)
+test_equal(1234[-1],0)
+test_equal(-1234[0],0)
+test_equal(-1234[1],1)
+test_equal(-1234[-1],0)
+
+test_equal(1234 << -5,38)
+test_equal(1234 >> -5,39488)
+
+test_equal(1.to_f.class,Float)
+test_equal(1.zero?,false)
+test_equal(0.nonzero?,nil) # awesome
+
+test_equal(0,1*0)
diff --git a/test/testFixnumBignumAutoconversion.rb b/test/testFixnumBignumAutoconversion.rb
new file mode 100644
index 0000000000..3eb9a8ecc3
--- /dev/null
+++ b/test/testFixnumBignumAutoconversion.rb
@@ -0,0 +1,46 @@
+require 'test/minirunit'
+test_check "Test fixnum to bignum autoconversion:"
+
+if RUBY_PLATFORM =~ /java/
+
+    MAX = 9223372036854775807 # don't trust << and minus yet
+    MIN = -9223372036854775808 # only lexer tested
+
+    test_equal(MAX.class,Fixnum)
+    test_equal(MIN.class,Fixnum)
+
+    # no bignorm in Fixnum#-,+
+    test_equal((MAX+1).class,Bignum)
+    test_equal((MAX-(-1)).class,Bignum)
+
+    test_equal((MIN+(-1)).class,Bignum)
+    test_equal((MIN-1).class,Bignum)
+
+    test_equal((MAX&1234).class,Fixnum)
+
+    test_equal((MAX/2)*2+1,MAX)
+    test_equal((MIN/2)*2,MIN)
+    test_equal(((MAX/2)*2+1).class,Fixnum)
+    test_equal(((MIN/2)*2).class,Fixnum)
+    test_equal(MAX|MIN,-1)
+    test_equal(MAX^MIN,-1)
+    test_equal(MAX&MIN,0)
+
+    test_equal((MAX << 1).class,Bignum)
+    test_equal((MIN << 1).class,Bignum)
+
+    BMAX = 9223372036854775808
+    BMIN = -9223372036854775809
+
+    test_equal(BMAX.class,Bignum)
+    test_equal(BMIN.class,Bignum)
+
+    test_equal((BMAX+(-1)).class,Fixnum)
+    test_equal((BMAX-1).class,Fixnum)
+    test_equal((BMIN+1).class,Fixnum)
+    test_equal((BMIN-(-1)).class,Fixnum)
+    
+    test_equal((BMAX >> 1).class,Fixnum)
+    test_equal((BMIN >> 1).class,Fixnum)
+
+end
diff --git a/test/testFloat.rb b/test/testFloat.rb
index 34815bb5ca..f56572c864 100644
--- a/test/testFloat.rb
+++ b/test/testFloat.rb
@@ -1,47 +1,98 @@
 require 'test/minirunit'
 test_check "Test Floating points:"
 
 # I had to choose a value for testing float equality
 # 1.0e-11 maybe too big but since the result depends
 # on the actual machine precision it may be too small
 # too...
 def test_float_equal(a,b)
   test_ok(a-b < 1.0e-11)
 end
 
 test_float_equal(9.2 , 3.5 + 5.7)
 test_float_equal(9.9, 3.0*3.3)
 test_float_equal(1.19047619 , 2.5 / 2.1)
 test_float_equal(39.0625, 2.5 ** 4)
 
 test_equal("1.1", 1.1.to_s)
 test_equal("1.0", 1.0.to_s)
 test_equal("-1.0", -1.0.to_s)
 
 test_equal(nil, 0.0.infinite?)
 test_equal(-1, (-1.0/0.0).infinite?)
 test_equal(1, (+1.0/0.0).infinite?)
 
 # a few added tests for MRI compatibility now that we override relops
 # from Comparable
 
 class Smallest
   include Comparable
   def <=> (other)
     -1
   end
 end
 
 class Different
   include Comparable
   def <=> (other)
     nil
   end
 end
 
 s = Smallest.new
 d = Different.new
 test_equal(nil, 3.0 <=> s)
 test_equal(nil, 3.0 <=> d)
 test_exception(ArgumentError) { 3.0 < s }
 test_exception(ArgumentError) { 3.0 < d }
+
+test_ok(Float.induced_from(1))
+test_ok(Float.induced_from(100**50))
+test_exception(TypeError){Float.induced_from('')}
+
+test_equal(1.0.to_s=="1.0",true)
+
+Inf = 1/0.0
+MInf = -1/0.0
+NaN = 0/0.0
+
+test_equal(10-Inf,MInf)
+test_equal(10+Inf,Inf)
+test_equal(10/Inf,0.0)
+test_equal(Inf.class,Float)
+test_ok(Inf.infinite?)
+test_ok(0.0.finite?)
+
+test_equal(1.2 <=> 1.1,1)
+test_equal(1.1 <=> 1.2,-1)
+test_equal(1.2 <=> 1.2,0)
+
+test_equal(1.0.floor.class,Fixnum)
+test_equal(1.0.ceil.class,Fixnum)
+test_equal(1.0.round.class,Fixnum)
+test_equal(1.0.truncate.class,Fixnum)
+
+test_equal(1.0.eql?(1),false)
+
+test_equal((1.0+100**50).class,Float)
+test_equal((1.0-100**50).class,Float)
+test_equal((1.0/100**50).class,Float)
+test_equal((1.0*100**50).class,Float)
+test_equal((1.0%100**50).class,Float)
+
+test_equal((1.0+100).class,Float)
+test_equal((1.0-100).class,Float)
+test_equal((1.0/100).class,Float)
+test_equal((1.0*100).class,Float)
+test_equal((1.0%100).class,Float)
+
+
+test_equal(1.0.div(1).class,Fixnum)
+test_equal(1.0.modulo(2.0).class,Float)
+
+test_exception(FloatDomainError){1.0.divmod(0.0)}
+test_equal(Inf.to_s=="Infinity",true)
+test_equal((-Inf).to_s=="-Infinity",true)
+test_equal(NaN.to_s=="NaN",true)
+test_equal(NaN.to_s=="NaN",true)
+test_equal(NaN==NaN,false)
diff --git a/test/testInteger.rb b/test/testInteger.rb
new file mode 100644
index 0000000000..6a10707a98
--- /dev/null
+++ b/test/testInteger.rb
@@ -0,0 +1,72 @@
+require 'test/minirunit'
+test_check "Test integers:"
+
+test_exception(NoMethodError){Integer.new}
+test_exception(TypeError){Integer.allocate} rescue NoMethodError # allocate throws TypeError!
+
+test_equal(true,1.integer?)
+
+# for Fixnum operations - fast version
+
+a = 0
+10.times do |i|
+    a+=i
+end
+
+test_equal(a,45)
+
+a = 0
+10.upto(20) do |i|
+    a+=i
+end
+
+test_equal(a,165)
+
+a = 0
+20.downto(10) do |i|
+    a+=i
+end
+
+test_equal(a,165)
+
+test_equal(0.next,1)
+
+# for Bignum operations - slow version
+
+big = 10000000000000000000
+
+test_equal(big.class,Bignum)
+
+a = 0
+big.times do |i|
+    a+=i
+    break if i > 10
+end
+
+test_equal(a,66)
+
+a = 0
+big.upto(big+10) do |i|
+    a += i
+end
+
+test_equal(a,110000000000000000055)
+
+a = 0
+big.downto(big-10) do |i|
+    a += i
+end
+
+test_equal(a,109999999999999999945)
+
+test_equal(big.next,big + 1)
+
+test_equal(1.chr,"\001")
+test_equal(10.chr,"\n")
+
+test_equal(1.to_i,1)
+test_equal(10.to_i,10)
+
+test_exception(TypeError){Integer.induced_from "2"}
+test_equal(Fixnum,Integer.induced_from(2.0).class)
+test_equal(Bignum,Integer.induced_from(100**100).class)
diff --git a/test/testNumeric.rb b/test/testNumeric.rb
new file mode 100644
index 0000000000..edf2decbbe
--- /dev/null
+++ b/test/testNumeric.rb
@@ -0,0 +1,92 @@
+require 'test/minirunit'
+test_check "Test numerics:"
+
+a = Numeric.new
+b = Numeric.new
+
+test_equal(Array,(a.coerce b).class)
+
+test_equal(a,+a)
+test_exception(TypeError){-a}
+
+test_equal(0,a <=> a)
+test_equal(nil,a <=> b)
+test_equal(nil,a <=> 1)
+test_equal(nil,a <=> "")
+
+test_exception(NoMethodError){a.quo b} # no '%' method
+
+test_equal(false,a.eql?(b))
+
+[:div,:divmod,:modulo,:remainder].each do |meth|
+    test_exception(NoMethodError){a.send meth,b}
+end
+
+test_exception(ArgumentError){a.abs}
+
+test_exception(NoMethodError){a.to_int}
+
+test_equal(false,a.integer?)
+test_equal(false,a.zero?)
+test_equal(a,a.nonzero?)
+
+[:floor,:ceil,:round,:truncate].each do |meth|
+    test_exception(TypeError){a.send meth}
+end
+
+test_exception(ArgumentError){a.step(0)}
+
+
+test_equal(nil,a == b)
+test_equal(true,a == a)
+test_exception(ArgumentError){a < b}
+test_exception(NoMethodError){a + b}
+
+
+# Fixnum
+
+a = 0
+
+10.step(20) do |i|
+    a+=i
+end
+
+test_equal(a,165)
+
+a = 0
+10.step(20,3) do |i|
+    a+=i
+end
+
+test_equal(a,58)
+
+a = 0
+
+20.step(10,-3) do |i|
+    a+=i
+end
+
+test_equal(a,62)
+
+# Float
+a = 0.0
+
+10.0.step(12.0) do |i|
+    a+=i
+end
+
+test_equal(a,33.0)
+
+a = 0.0
+10.0.step(12.0,0.3) do |i|
+    a+=i
+end
+
+test_equal(a,76.3)
+
+a = 0.0
+12.0.step(10.0,-0.3) do |i|
+    a+=i
+end
+
+test_equal(a,77.7)
diff --git a/test/test_index b/test/test_index
index b8eba95e01..08901d5621 100644
--- a/test/test_index
+++ b/test/test_index
@@ -1,98 +1,103 @@
 mri/testArray.rb
 mri/testAssignment.rb
 mri/testAssignment2.rb
 mri/testBignum.rb
 mri/testCall.rb
 mri/testCase.rb
 mri/testCondition.rb
 mri/testException.rb
 mri/testFloat.rb
 mri/testHash.rb
 mri/testIfUnless.rb
 mri/testIterator.rb
 mri/testProc.rb
 mri/testString.rb
 mri/testWhileUntil.rb
 testBigDecimal.rb
+testBignum.rb
 testCacheMapLeak.rb
 testClass.rb
 testClasses.rb
 testCompiler.rb
 testConstant.rb
 testDigest.rb
 testDir.rb
 testEnumerable.rb
 testEnumerator.rb
 testEnv.rb
 testEval.rb
 testException.rb
 testException2.rb
 testExpressions.rb
 testFile.rb
 testFileTest.rb
+testFixnum.rb
+testFixnumBignumAutoconversion.rb
 testFloat.rb
 testGC.rb
 testGlobalVars.rb
 testHash.rb
 testHereDocument.rb
 testHigherJavaSupport.rb
 testIconv.rb
 testIf.rb
 testInspect.rb
 testInstantiatingInterfaces.rb
+testInteger.rb
 testIO.rb
 testJavaExtensions.rb
 testJavaProxy.rb
 testJavaSupport.rb
 testKernel.rb
 testLine.rb
 testLine_block_comment.rb
 testLine_block_comment_start.rb
 testLine_code.rb
 testLine_comment.rb
 testLine_line_comment_start.rb
 testLine_mixed_comment.rb
 testLoad.rb
 testLoops.rb
 testLowerJavaSupport.rb
 testMarshal.rb
 testMethods.rb
 testModule.rb
 testNesting.rb
 testNoMethodError.rb
 testNumber.rb
+testNumeric.rb
 testObject.rb
 testObjectSpace.rb
 testPackUnpack.rb
 testPipe.rb
 testPositions.rb
 testProc.rb
 testProcess.rb
 testRandom.rb
 testRange.rb
 testRbConfig.rb
 testRedefine.rb
 testRegexp.rb
 testReturn.rb
 testRuntimeCallbacks.rb
 testSocket.rb
 testSplat.rb
 testString.rb
 testStringChomp.rb
 testStringEachLineStress.rb
 testStringEval.rb
 testStringGsubStress.rb
 testStringIO.rb
 testStringScan.rb
 testStringScanStress.rb
 testStringSplitStress.rb
 testStruct.rb
 testSuper.rb
 testSymbol.rb
 testThread.rb
 testTime.rb
 testUnboundMethod.rb
 testVariableAndMethod.rb
 testVisibility.rb
 testXML.rb
 testYAML.rb
